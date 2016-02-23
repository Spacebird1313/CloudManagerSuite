package be.uantwerpen.services;

import be.uantwerpen.models.Cloud;
import be.uantwerpen.models.Server;
import be.uantwerpen.models.User;
import be.uantwerpen.terminal.Terminal;
import org.springframework.stereotype.Service;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Thomas on 21/02/2016.
 */
@Service
public class ApplicationManager
{
    private static Terminal terminal;
    private static Cloud oneCloud;
    private static ServerMonitor serverMonitor;

    public ApplicationManager()
    {
        terminal = new Terminal(this);
        oneCloud = new Cloud();
        serverMonitor = new ServerMonitor();

        serverMonitor.getObserver().addObserver(new Observer()
        {
            @Override
            public void update(Observable source, Object object)
            {
                executeMonitorAction((String) object);
            }
        });
    }

    public static void systemReady()
    {
        CloudManager.loadServerList(oneCloud);

        terminal.printTerminal(" :: Cloud Manager - 2016 ::  -  Developed by: Thomas Huybrechts");
        terminal.printTerminal("Type 'help' to display the possible commands.");
        terminal.activateTerminal();
    }

    public static void setCloud(String cloudIP, User cloudUser)
    {
        String cloudURL = new String("http://").concat(cloudIP.concat("/RPC2"));

        oneCloud = new Cloud(cloudURL, cloudUser);
    }

    public void setCloudIP(String cloudIP)
    {
        String cloudURL = new String("http://").concat(cloudIP.concat("/RPC2"));
        User oldUser = oneCloud.getLoginCredentials();

        oneCloud = new Cloud(cloudURL, oldUser);

        updateCloudInfo();
    }

    public void setCloudUser(User cloudUser)
    {
        String oldCloudURL = oneCloud.getURL();

        oneCloud = new Cloud(oldCloudURL, cloudUser);

        updateCloudInfo();
    }

    public void setCloudUser(String username, String password)
    {
        String oldCloudURL = oneCloud.getURL();

        oneCloud = new Cloud(oldCloudURL, new User(username, password));

        updateCloudInfo();
    }

    public String getCloudIP()
    {
        return oneCloud.getURL().split("/")[2];
    }

    public String getCloudUsername()
    {
        return oneCloud.getLoginCredentials().getUsername();
    }

    public boolean instantiateVM(int templateID)
    {
        int vmId = CloudManager.createVMFromTemplate(oneCloud, templateID);

        if(vmId == -1)
        {
            return false;
        }

        if(serverMonitor.isRunning())
        {
            Server newServer = oneCloud.getServerById(vmId);
            newServer.setBootState(true);

            serverMonitor.addServer(newServer, true);
        }

        return true;
    }

    public boolean instantiateVM(String templateName)
    {
        int vmId = CloudManager.createVMFromTemplate(oneCloud, templateName);

        if(vmId == -1)
        {
            return false;
        }

        if(serverMonitor.isRunning())
        {
            Server newServer = oneCloud.getServerById(vmId);
            newServer.setBootState(true);

            serverMonitor.addServer(newServer, true);
        }

        return true;
    }

    public boolean deleteVM(int vmId)
    {
        boolean status = CloudManager.deleteVM(oneCloud, vmId);

        if(serverMonitor.isRunning() && status)
        {
            serverMonitor.clearServerList();
            serverMonitor.addServers(oneCloud.getServers());
        }

        return status;
    }

    public void clearCloudInfo()
    {
        String oldCloudURL = oneCloud.getURL();
        User oldCloudUser = oneCloud.getLoginCredentials();

        oneCloud = new Cloud(oldCloudURL, oldCloudUser);

        if(serverMonitor.isRunning())
        {
            serverMonitor.clearServerList();
        }
    }

    public void updateCloudInfo()
    {
        CloudManager.loadServerList(oneCloud);

        if(serverMonitor.isRunning())
        {
            serverMonitor.addServers(oneCloud.getServers());
        }
    }

    public void startMonitoring()
    {
        serverMonitor.clearServerList();
        serverMonitor.addServers(oneCloud.getServers());

        serverMonitor.startMonitor();
    }

    public void stopMonitoring()
    {
        serverMonitor.stopMonitor();
    }

    public boolean getMonitoringState()
    {
        return serverMonitor.isRunning();
    }

    public void printServersStatus()
    {
        CloudManager.printServersStatus(oneCloud);
    }

    public void printTemplatePool()
    {
        CloudManager.printTemplatePool(oneCloud);
    }

    public void exitSystem()
    {
        System.exit(0);
    }

    private void executeMonitorAction(String monitorAction)
    {
        if(monitorAction.toLowerCase().equals("vm overload"))
        {
            System.out.println("Instantiating new VM...");

            this.instantiateVM(17);
        }
    }
}
