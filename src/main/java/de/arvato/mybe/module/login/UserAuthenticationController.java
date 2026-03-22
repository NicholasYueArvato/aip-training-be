package de.arvato.mybe.module.login;


import de.arvato.mybe.base.security.PostAuthenticationDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;

/**
 * User authentication controller
 */
@RestController
@RequestMapping("/api/authentication")
public class UserAuthenticationController
{
    @Autowired
    PostAuthenticationDataProvider postAuthenticationDataProvider;

    public static Logger logger = LoggerFactory.getLogger(UserAuthenticationController.class);
    /**
     * Should be called ONCE - after the user has been successfully authenticated - by the frontend
     * <p>
     * Writes authenticated user data to the session / Returns authenticated user data for frontend display
     * <p>
     * Receives the selected FE language code and stores it in session
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/{locale}", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public HashMap<String, Object> postAuthenticationProcessing(Principal principal, @PathVariable("locale") String locale, HttpServletRequest request)
    {

        Object princical = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(princical instanceof UserDetails))
        {
            //LOGGER.error((String)princical);
            return null;
        }

        UserDetails userDetails = (UserDetails) princical;

        HashMap<String, Object> map = postAuthenticationDataProvider.process(userDetails); //clientDAO, userDAO, partnerServiceDAO, userDetails, userManager);

        return map;
    }

}
