package de.arvato.mybe.base.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthHelper
{
    private final String authMethod;

    public AuthHelper(@Value("${aep.security.auth-method:basic}") String authMethod)
    {
        this.authMethod = authMethod;
    }

    public String getUserName()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        switch (authMethod.toLowerCase())
        {
            case "oauth2":
                return getClaimValueFromJwt(authentication, "email");
            case "oauth2-token-roles":
                return getClaimValueFromJwt(authentication, "preferred_username");
            case "basic":
                return authentication.getName();
            default:
                return "unknown";
        }
    }

    String getClaimValueFromJwt(Authentication authentication, String property)
    {
        if (authentication instanceof JwtAuthenticationToken)
        {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String userName = jwt.getClaimAsString(property);
            return userName;
        }
        return authentication.getName();
    }
}