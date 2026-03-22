package de.arvato.mybe.base.config;

import aep.core.starter.security.auth.basic.BasicAuthProperties;
import aep.core.starter.security.auth.basic.BasicAuthUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class BasicAuthTokenGenerator
{
    private final static Logger LOGGER = LoggerFactory.getLogger(BasicAuthTokenGenerator.class);


    @Value("${aep.security.auth-method:}")
    private String authMethod;

    @Autowired
    BasicAuthProperties basicAuthProperties;

    @PostConstruct
    public void printToken(){
        if(authMethod.equals("basic")){
            LOGGER.warn("The authentication method is \"basic\". This will not be used in remote servers.");
            for (BasicAuthUser basicAuthUser : basicAuthProperties.getUsers()) {
                String clear = basicAuthUser.getUsername() + ":" + basicAuthUser.getPassword();
                String encodedString = Base64.getEncoder().encodeToString(clear.getBytes());
                LOGGER.info("The basic auth token for user {} is {} ", basicAuthUser.getUsername(), encodedString);
                LOGGER.info("User {} has these rights {}", basicAuthUser.getUsername(), basicAuthUser.getRights().toString());
            }
        }
    }
}
