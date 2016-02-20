package be.uantwerpen.controllers;

import be.uantwerpen.models.SystemLoad;
import be.uantwerpen.services.SystemLoadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Thomas on 13/02/2016.
 */
@RestController
public class SystemLoadController
{
    @Autowired
    SystemLoadService systemLoadService;

    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/systemLoad")
    public SystemLoad getSystemLoad()
    {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd - HH.mm.ss").format(new Date());
        Double cpuLoad = systemLoadService.getCPULoad();
        Long memTotal = systemLoadService.getTotalMem();
        Long memFree = systemLoadService.getFreeMem();
        Long memInUse = systemLoadService.getInUseMem();

        return new SystemLoad(counter.incrementAndGet(), timeStamp, cpuLoad, memTotal, memFree, memInUse);
    }
}
