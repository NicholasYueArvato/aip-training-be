package de.arvato.mybe.base.security;

import de.arvato.mybe.base.security.jwt.AuthenticationTokenFilter;
import de.arvato.mybe.dao.EmployeeDTO;
import de.arvato.mybe.module.employee.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UserSessionFilter extends AuthenticationTokenFilter
{
    private final EmployeeService employeeService;

    private final String authMethod;

    private final BasicAuthUserConfig basicAuthUserConfig;

    private final String apiKey;

    private final Set<String> adminEmails;

    private final List<String> restSuperUserRights;

    private String[] publicResources = {
            "/api-docs",
            "/actuator/"
    };

    public UserSessionFilter(@Value("${aep.security.auth-method:basic}") String authMethod,
                             @Value("${bep.central.api-key:}") String apiKey,
                             @Value("#{'${bep.central.admin.emails:bot.my@bertelsmann.de}'.split(',')}") Set<String> adminEmails,
                             @Value("#{'${bep.central.rest.super-user-rights:}'.split(',')}") List<String> restSuperUserRights,
                             BasicAuthUserConfig basicAuthUserConfig,
                             EmployeeService employeeService
    )
    {
        this.authMethod = authMethod;
        this.employeeService = employeeService;
        this.basicAuthUserConfig = basicAuthUserConfig;
        this.apiKey = apiKey;
        this.adminEmails = adminEmails;
        this.restSuperUserRights = restSuperUserRights;
    }

    private Jwt getJwt(Authentication authentication)
    {
        if (authentication instanceof JwtAuthenticationToken)
        {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            return jwt;
        }
        return null;
    }


    private String getEmailFromClaim(Authentication authentication)
    {
        Jwt jwt = getJwt(authentication);

        if (jwt == null)
            return "";
        List<String> emails = jwt.getClaimAsStringList("emails");
        if (ListUtils.emptyIfNull(emails).size() > 0)
        {
            return emails.get(0);
        }
        return "";
    }

    private String getUsernameFromClaim(Authentication authentication)
    {


        Jwt jwt = getJwt(authentication);

        if (jwt == null)
            return "";

        String userName = jwt.getClaimAsString("preferred_username");

        if (StringUtils.isNotBlank(userName))
        {
            return userName;
        }
        return authentication.getName();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Current authentication : " + authentication);
        List<String> authorityStrings = authentication.getAuthorities().stream().map(x -> x.getAuthority()).collect(Collectors.toList());
        logger.info("authentication.getAuthorities(): " + StringUtils.join(authorityStrings, ",", 0, authorityStrings.size()));

        UserSession userSession = UserSession.get();

        logger.info("Current UserSession : " + userSession);

        String requestUri = request.getRequestURI();

        try
        {
            if (!Arrays.stream(publicResources).anyMatch(x -> request.getRequestURI().contains(x)))
            {
                userSession.setEmail(getEmailFromClaim(authentication));

                switch (authMethod.toLowerCase())
                {
                    case "oauth2":
                        userSession.setUserName(userSession.getEmail());
                        break;
                    case "oauth2-token-roles":
                        userSession.setUserName(getUsernameFromClaim(authentication));
                        break;
                    case "basic":
                    {
                        userSession.setUserName(authentication.getName());
                        if (basicAuthUserConfig == null)
                        {
                            String message = String.format("basic auth is not configured properly with users");
                            throw userSessionError(message);
                        }
                        BasicAuthUserExt basicAuthUserExt = basicAuthUserConfig.getUsers().stream().filter(x -> {
                            return x.getUsername().equals(authentication.getName());
                        }).findFirst().orElseThrow(
                                () -> {
                                    String message = "illegal basic auth setting for user" + authentication.getName();
                                    return userSessionError(message);
                                }
                        );
                        userSession.setEmail(basicAuthUserExt.getEmail());
                    }
                    break;
                    default:
                        userSession.setUserName("nouser");
                }

                EmployeeDTO employeeDTO = employeeService.getCachedEmployee().stream()
                        .filter(x -> x.getEmail().equalsIgnoreCase(userSession.getEmail()) && x.isActive())
                        .findFirst().orElse(null);


                //add rest super user rights.
                String clientApikey = request.getHeader("apiKey");
                if (StringUtils.isNotBlank(clientApikey))
                {
                    if (clientApikey.trim().equals(apiKey))
                    {
                        //increase flexibility, rest can be called when employee is not existing, (user must be authenticated first)
                        if (employeeDTO == null)
                        {
                            employeeDTO = createTechnicalEmployee("Restadmin", adminEmails.stream().findFirst().get(), "RESTADMIN", "REST API technical user", 99998L);
                        }
                    }
                }

                if (adminEmails.contains(userSession.getEmail()))
                {
                    if (employeeDTO == null)
                    {
                        employeeDTO = createTechnicalEmployee("Sysadmin", userSession.getEmail(), "SYSADMIN", "System Administrator", 99999L);
                    }
                    else
                    {
                        addSuperAdminRights();
                    }
                }

                if (employeeDTO == null)
                {
                    String message = String.format("employee (%s) not setup with email %s", userSession.getUserName(), userSession.getEmail());
                    throw userSessionError(message);
                }

                userSession.setEmployee(employeeDTO);
                userSession.setRacf(employeeDTO.getRacf());
                userSession.setLocale(LocaleUtils.toLocale("en_US"));
                UserSession.set(userSession);
            }
            logger.info("modified authorities: " + StringUtils.join(authorityStrings, ",", 0, authorityStrings.size()));

            chain.doFilter(request, response);
        }
        finally
        {
            UserSession.remove();
        }
    }

    private EmployeeDTO createTechnicalEmployee(String lastName, String email, String racf, String designation, long id)
    {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setEmail(email);
        employeeDTO.setId(id);
        employeeDTO.setFirstName("Technical");
        employeeDTO.setLastName(lastName);
        employeeDTO.setRacf(racf);
        employeeDTO.setDesignation(designation);
        addSuperAdminRights();
        return employeeDTO;
    }

    private void addSuperAdminRights()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<GrantedAuthority> updatedAuthorities = new ArrayList<>(auth.getAuthorities());

        for (String restSuperUserRight : restSuperUserRights)
        {
            updatedAuthorities.add(new SimpleGrantedAuthority(restSuperUserRight));
        }
        Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), updatedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }


    private RuntimeException userSessionError(String message)
    {
        log.error(message);
        return new AccessDeniedException(message);
    }
}

