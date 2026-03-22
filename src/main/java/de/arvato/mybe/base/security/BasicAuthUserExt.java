package de.arvato.mybe.base.security;

import aep.core.starter.security.auth.basic.BasicAuthUser;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "aep.security.basicauth.users")
public class BasicAuthUserExt extends BasicAuthUser
{
    private String racf;
    private String email;
}
