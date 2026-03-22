package de.arvato.mybe.base.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Post authentication data provider
 */
@Component
public class PostAuthenticationDataProvider
{
    @Autowired
    private Environment env;

    // Key values for result hash map:
    private final static String DATA_KEY_USER = "user";
    private final static String DATA_KEY_FIRSTNAME = "firstname";
    private final static String DATA_KEY_LASTNAME = "lastname";
    private final static String DATA_KEY_PERMISSIONS = "permissions";
    private final static String DATA_KEY_INITIALPASSWORD = "initialPassword";

    /**
     * Collects the required data
     */
    public HashMap<String, Object> process(UserDetails userDetails)
    {
        // The collected data (result hash map):
        HashMap<String, Object> map = new HashMap<>();

        // Determine and add all user interface permissions for this user to return object:
        List<String> permissions = new ArrayList<>();
        for (GrantedAuthority grantedAuthority : userDetails.getAuthorities())
        {
            permissions.add(grantedAuthority.getAuthority());
        }

        map.put(DATA_KEY_PERMISSIONS, permissions);

        //TODO - change to real user
        map.put(DATA_KEY_USER, "admin");
        map.put(DATA_KEY_FIRSTNAME, "John");
        map.put(DATA_KEY_LASTNAME, "Doe");
        map.put(DATA_KEY_INITIALPASSWORD, "0");

        return map;
    }
}
