package de.arvato.mybe.module.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.arvato.mybe.base.security.AuthRequest;
import de.arvato.mybe.base.security.jwt.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Login service controller
 */
@RestController
public class LoginController
{
    public static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private static ObjectMapper objectMapper;

    public LoginController()
    {
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    @ResponseBody
    public LoginResult auth(HttpServletRequest request,
                                            @RequestBody AuthRequest authenticationRequest) throws Exception
    {
        return doLogin(request, authenticationRequest, JwtTokenUtil.EXPIRATION);
    }


    public LoginResult doLogin(HttpServletRequest request,
                          @RequestBody AuthRequest authenticationRequest,
                          Long expiration) throws Exception
    {
        String userName = authenticationRequest.getUsername();
        String password = authenticationRequest.getPassword();

        LoginResult result = new LoginResult();
        if (userName.equals("admin") && password.equals("Arvato@567"))
        {
            Map<String, Object> optClaims = new HashMap<>();
            optClaims.put("LOCALE", "en_US");

            String token = jwtTokenUtil.generateToken(userName, optClaims, expiration);

            result.setJwttoken(token);
            result.setStatus("OK");
        } else {
            result.setJwttoken("");
            result.setStatus("ERROR");
        }
        return result;
    }
}
