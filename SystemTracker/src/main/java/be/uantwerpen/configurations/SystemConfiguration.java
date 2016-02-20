package be.uantwerpen.configurations;

import be.uantwerpen.SystemTrackerApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Created by Thomas on 13/02/2016.
 */
@Configuration
public class SystemConfiguration implements ApplicationListener<ContextRefreshedEvent>
{
    //Sigar driver location
    private static String SIGAR_DRIVERS = "/drivers/sigar";

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event)
    {
        System.setProperty("java.library.path", SystemTrackerApplication.class.getResource(SIGAR_DRIVERS).getPath());
    }
}
