package be.uantwerpen.services;

import be.uantwerpen.models.Cloud;
import be.uantwerpen.models.User;
import be.uantwerpen.terminal.Terminal;
import org.springframework.stereotype.Service;

/**
 * Created by Thomas on 21/02/2016.
 */
@Service
public class ApplicationManager
{
    private static Terminal terminal;
    private static Cloud oneCloud;

    public ApplicationManager()
    {
        terminal = new Terminal(this);
        oneCloud = new Cloud();
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
        return CloudManager.createVMFromTemplate(oneCloud, templateID);
    }

    public boolean instantiateVM(String templateName)
    {
        return CloudManager.createVMFromTemplate(oneCloud, templateName);
    }

    public boolean deleteVM(int vmId)
    {
        return CloudManager.deleteVM(oneCloud, vmId);
    }

    public void clearCloudInfo()
    {
        String oldCloudURL = oneCloud.getURL();
        User oldCloudUser = oneCloud.getLoginCredentials();

        oneCloud = new Cloud(oldCloudURL, oldCloudUser);
    }

    public void updateCloudInfo()
    {
        CloudManager.loadServerList(oneCloud);
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
}
