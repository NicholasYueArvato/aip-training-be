package de.arvato.mybe.base.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;


//@Configuration
public class GitInfoConfiguration
{
    private static final Logger LOG = LoggerFactory.getLogger(GitInfoConfiguration.class);

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer()
    {
        PropertySourcesPlaceholderConfigurer c = new PropertySourcesPlaceholderConfigurer();
        c.setLocation(new ClassPathResource("git.properties"));
        c.setIgnoreResourceNotFound(true);
        c.setIgnoreUnresolvablePlaceholders(true);
        return c;
    }


    @Value("${git.branch}")
    private String branch;

    @Value("${git.commit.id.abbrev}")
    private String commitId;

    @Value("${git.commit.message.short}")
    private String commitMessage;

    @Value("${git.build.time}")
    private String buildTime;

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup()
    {
        LOG.info("================================================");
        LOG.info("  GIT:");
        LOG.info("    branch:     {}", branch);
        LOG.info("    commit:");
        LOG.info("      id:       {}", commitId);
        LOG.info("      message:  {}", commitMessage);
        LOG.info("    build time: {}", buildTime);
        LOG.info("================================================");
    }

}
