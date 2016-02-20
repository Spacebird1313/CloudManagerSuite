package be.uantwerpen.services;

import be.uantwerpen.models.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Thomas on 13/02/2016.
 */
@Service
public class ServerMonitor
{
    private ServerPoller serverPoller;
    private List<Server> servers;
    private long pollInterval;
    private Boolean serviceRunning;
    private Thread monitorThread;

    public ServerMonitor()
    {
        this.pollInterval = 5000;
        this.servers = new ArrayList<Server>();
        this.serviceRunning = false;
        this.serverPoller = new ServerPoller();
    }

    public ServerMonitor(long pollInterval)
    {
        this.pollInterval = pollInterval;
        this.servers = new ArrayList<Server>();
        this.serviceRunning = false;
        this.serverPoller = new ServerPoller();
    }

    public void startMonitor()
    {
        if(!serviceRunning && monitorThread == null)
        {
            monitorThread = new Thread(new MonitorThread());

            this.serviceRunning = true;

            monitorThread.start();
        }
    }

    public void stopMonitor()
    {
        if(serviceRunning && monitorThread != null)
        {
            this.serviceRunning = false;

            while(monitorThread.isAlive())
            {
                //Wait until thread is terminated
            }

            monitorThread = null;
        }
    }

    public synchronized Boolean addServer(Server server)
    {
        if(!servers.contains(server))
        {
            return servers.add(server);
        }
        else
        {
            return false;
        }
    }

    public synchronized Boolean removeServer(Server server)
    {
        if(!servers.contains(server))
        {
            return servers.remove(server);
        }
        else
        {
            return false;
        }
    }

    public List<Server> getServers()
    {
        return this.servers;
    }

    private class MonitorThread implements Runnable
    {
        public void run()
        {
            //Start first poll of servers on start
            long nextPollTime = new Date().getTime() - pollInterval;

            while(serviceRunning)
            {
                if(nextPollTime <= new Date().getTime())
                {
                    for(Server server : servers)
                    {
                        if(serviceRunning)
                        {
                            if(serverPoller.pollServerLoad(server, 8080, "/SystemTracker/systemLoad") != null)
                            {
                                server.setOperationalState(true);
                            }
                            else
                            {
                                server.setOperationalState(false);
                            }
                        }
                        else
                        {
                            //Break polling sequence
                            return;
                        }
                    }

                    nextPollTime = new Date().getTime() + pollInterval;
                }
            }
        }
    }
}
