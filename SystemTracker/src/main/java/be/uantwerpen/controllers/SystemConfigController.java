package be.uantwerpen.controllers;

import be.uantwerpen.models.SystemConfig;
import be.uantwerpen.models.SystemProperty;
import be.uantwerpen.services.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Thomas on 24/02/2016.
 */
@RestController
public class SystemConfigController
{
    @Autowired
    SystemConfigService systemConfigService;

    private final AtomicLong counter = new AtomicLong();

    @RequestMapping(value = "/systemConfig", method = RequestMethod.GET)
    public SystemConfig getSystemConfig()
    {
        String vmPool = systemConfigService.getVmPool();

        return new SystemConfig(counter.incrementAndGet(), vmPool);
    }

    @RequestMapping(value = "/systemConfig", method = RequestMethod.POST)
    public ResponseEntity<SystemProperty> setSystemProperty(@RequestBody SystemProperty property)
    {
        long id = -1L;
        String key = "null";

        if(property != null)
        {
            id = property.getId();
            key = property.getKey();

            if(systemConfigService.setProperty(property))
            {
                //Set system property succeed
                return new ResponseEntity<SystemProperty>(new SystemProperty(id, key, "success"), HttpStatus.OK);
            }
        }

        //Set system property failed
        return new ResponseEntity<SystemProperty>(new SystemProperty(id, key, "failed"), HttpStatus.NOT_FOUND);
    }
}
