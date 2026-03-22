package de.arvato.mybe;

import de.arvato.mybe.base.RemoteActionClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;
import java.io.IOException;

@SpringBootApplication
@EnableMBeanExport
@EnableRetry
@EnableScheduling
public class MyBeBaseApp extends SpringBootServletInitializer
{
    public static void main(String[] args) throws IOException
    {
        if (args.length > 0 && args[0].equals("shutdown")) {
            if (args.length != 3) {
                System.err.print("Not enough arguments, user must provide `shutdown [host] [post]` as arguments");
            } else {
                new RemoteActionClient(args[1], Integer.parseInt(args[2])).shutdown();
            }
        } else {
            SpringApplicationBuilder app = new SpringApplicationBuilder(MyBeBaseApp.class);

            ApplicationHome home = new ApplicationHome(app.getClass());
            File jarDir = home.getDir();
            File pidFile = new File(jarDir, MyBeBaseApp.class.getSimpleName() + ".pid");
            app.build(args).addListeners(new ApplicationPidFileWriter(pidFile));
            app.run();
        }
    }
}

