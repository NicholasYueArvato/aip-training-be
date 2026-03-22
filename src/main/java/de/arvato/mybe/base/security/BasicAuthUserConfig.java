package de.arvato.mybe.base.security;

import
        lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "aep.security.basicauth")
@Data
public class BasicAuthUserConfig
{
    List<BasicAuthUserExt> users;
}
