package be.uantwerpen.services;

import be.uantwerpen.models.Server;
import be.uantwerpen.tools.Observer;
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
    private Observer observer;

    private int loadLimitPerc;

    public ServerMonitor()
    {
        this.pollInterval = 5000;
        this.servers = new ArrayList<Server>();
        this.serviceRunning = false;
        this.serverPoller = new ServerPoller();
        this.observer = new Observer();

        this.loadLimitPerc = 89;
    }

    public ServerMonitor(long pollInterval)
    {
        this.pollInterval = pollInterval;
        this.servers = new ArrayList<Server>();
        this.serviceRunning = false;
        this.serverPoller = new ServerPoller();
        this.observer = new Observer();

        this.loadLimitPerc = 89;
    }

    public Observer getObserver()
    {
        return this.observer;
    }

    public void setPollInterval(long pollInterval)
    {
        this.pollInterval = pollInterval;
    }

    public long getPollInterval()
    {
        return this.pollInterval;
    }

    public void setLoadLimitPerc(int percentage)
    {
        this.loadLimitPerc = percentage;
    }

    public int getLoadLimitPerc()
    {
        return this.loadLimitPerc;
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

    public Boolean isRunning()
    {
        return this.serviceRunning;
    }

    public synchronized Boolean addServer(Server server)
    {
        if(!server.getOperationalState())
        {
            System.out.println("Server: '" + server.getIpaddr() + "' is not operational.");
            System.out.println("Server will be ignored...");

            return false;
        }

        if(!servers.contains(server))
        {
            if(serverPoller.pollServerLoad(server, 8080, "/SystemTracker/systemLoad") != null)
            {
                return servers.add(server);
            }
            else
            {
                System.out.println("System Tracker is not responding for server: " + server.getIpaddr());
                System.out.println("Server will be ignored...");

                return false;
            }
        }
        else
        {
            return false;
        }
    }

    public synchronized Boolean addServer(Server server, Boolean forced)
    {
        if(!forced)
        {
            return this.addServer(server);
        }
        else
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
    }

    public synchronized void addServers(List<Server> servers)
    {
        for(Server server : servers)
        {
            addServer(server);
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

    public synchronized void clearServerList()
    {
        servers.clear();
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

                    analyzeMonitorResults();
                }
            }
        }
    }

    private void analyzeMonitorResults()
    {

    }
}
