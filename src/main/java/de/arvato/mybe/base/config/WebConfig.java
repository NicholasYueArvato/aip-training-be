package de.arvato.mybe.base.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public FilterRegistrationBean loggingFilterRegistration()
    {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new LoggingFilter().logCall(true));
        registration.addUrlPatterns("/login");
        registration.addUrlPatterns("/api/*");
        registration.addUrlPatterns("/logout");
        registration.setName("loggingFilter");
        registration.setOrder(1);
        return registration;
    }
}
