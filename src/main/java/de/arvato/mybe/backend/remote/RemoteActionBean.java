package de.arvato.mybe.backend.remote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

@Component
public class RemoteActionBean implements RemoteAction, ApplicationContextAware
{
    private Logger LOGGER = LoggerFactory.getLogger(RemoteActionBean.class);

    @PostConstruct
    public void postConstruct(){
        LOGGER.info("RemoteActionBean is loaded.");
    }

    @Autowired
    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        context = applicationContext;
    }

    @Override
    public void shutdown()
    {
        LOGGER.info("Remote instructions received, shutting down spring application...");
        SpringApplication.exit(context, () -> 0);
    }
}
