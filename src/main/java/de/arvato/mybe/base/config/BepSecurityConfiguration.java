package de.arvato.mybe.base.config;

import aep.core.starter.security.auth.AuthMethodConfigurer;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import aep.core.starter.security.configuration.CorsProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true
)
@Order(99)
/**
 * this class overrides SecurityConfiguration from AEP spring boot starter.
 */
public class BepSecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final AuthMethodConfigurer authMethodConfigurer;
    private final CorsProperties corsProperties;

    @Autowired
    public BepSecurityConfiguration(AuthMethodConfigurer authMethodConfigurer, CorsProperties corsProperties) {
        this.authMethodConfigurer = authMethodConfigurer;
        this.corsProperties = corsProperties;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //allow access for organisation and employee
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.AuthorizedUrl configurer =
                (ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)
                        ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)
                                ((HttpSecurity)((HttpSecurity)http.cors().and()).csrf()
                                        .disable()).authorizeRequests()
                                        .antMatchers(new String[]{"/", "/home",
                                                "/actuator/health", "/swagger-ui*",
                                                "/v3/api-docs",
                                                "/api/orgUnit",
                                                "/api/employee",
                                                "/api/emailQueue",
                                                "/api/holiday"
                                        }
                                        )).permitAll().anyRequest();
        this.authMethodConfigurer.configureHttpSecurity(configurer);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        this.authMethodConfigurer.configureWebSecurity(auth);
    }
}
