package de.arvato.mybe.base.security;

import de.arvato.mybe.dao.EmployeeDTO;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Locale;

/**
 * User session
 */
@Component
@Data
public class UserSession implements Serializable
{
    private static final InheritableThreadLocal<UserSession> threadLocal = new InheritableThreadLocal<>();

    private Locale locale;
    private String userName = "SYSADMIN";
    private String racf = "SYSADMIN";
    private String email = "";
    private EmployeeDTO employee;

    public static void set(UserSession userSession)
    {
        threadLocal.set(userSession);
    }

    public static UserSession get()
    {
        UserSession userSession = threadLocal.get();

        if (userSession == null)
        {
            userSession = new UserSession();
            remove();
            set(userSession);
        }

        return userSession;
    }



    public static void remove()
    {
        threadLocal.remove();
    }
}
