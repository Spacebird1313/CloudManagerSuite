package be.uantwerpen.services;

import be.uantwerpen.models.Cloud;
import be.uantwerpen.models.Server;
import be.uantwerpen.tools.XMLParser;
import org.opennebula.client.*;
import org.opennebula.client.template.Template;
import org.opennebula.client.template.TemplatePool;
import org.opennebula.client.vm.VirtualMachine;
import org.opennebula.client.vm.VirtualMachinePool;
import org.springframework.stereotype.Service;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Thomas on 15/02/2016.
 */
@Service
public class CloudManager
{
    public CloudManager()
    {

    }

    public static int createVMFromTemplate(Cloud cloud, int templateId)
    {
        Client oneClient;
        OneResponse oneResponse;

        oneClient = getClientConnection(cloud);

        if(oneClient == null)
        {
            return -1;
        }

        return instantiateTemplate(cloud, oneClient, templateId);
    }

    public static int createVMFromTemplate(Cloud cloud, String templateName)
    {
        Client oneClient;
        OneResponse oneResponse;
        List<Template> templateList;
        Template template;

        oneClient = getClientConnection(cloud);

        if(oneClient == null)
        {
            return -1;
        }

        templateList = getTemplateList(cloud);

        if(templateList == null)
        {
            return -1;
        }

        int i = 0;
        Boolean found = false;
        Iterator<Template> it = templateList.iterator();
        while(it.hasNext() && !found)
        {
            template = it.next();

            if(template.getName().equals(templateName))
            {
                found = true;
            }
            else
            {
                i++;
            }
        }

        if(found)
        {
            return instantiateTemplate(cloud, oneClient, Integer.parseInt(templateList.get(i).getId()));
        }
        else
        {
            System.err.println("Template with name: " + templateName + " does not exist on the server!");

            return -1;
        }
    }

    public static Boolean deleteVM(Cloud cloud, int vmId)
    {
        Client oneClient;
        OneResponse oneResponse;
        List<VirtualMachine> vmList;
        VirtualMachine deleteVM = null;
        String ipAddress;

        oneClient = getClientConnection(cloud);

        vmList = getVMList(cloud);

        Iterator<VirtualMachine> it = vmList.iterator();

        while(it.hasNext() && deleteVM == null)
        {
            VirtualMachine vm = it.next();

            if(Integer.parseInt(vm.getId()) == vmId)
            {
                deleteVM = vm;
            }
        }

        if(deleteVM != null)
        {
            ipAddress = getIPAddress(cloud, vmId);
            oneResponse = deleteVM.delete();
        }
        else
        {
            System.err.println("Could not find VM with id: " + vmId);

            return false;
        }

        if(oneResponse.isError())
        {
            System.err.println("Could not delete VM with id: " + vmId);
            System.err.println(oneResponse.getErrorMessage());

            return false;
        }
        else
        {
            cloud.removeServer(new Server(vmId, ipAddress));
            return true;
        }
    }

    private static int instantiateTemplate(Cloud cloud, Client oneClient, int templateId)
    {
        OneResponse oneResponse;
        int newVMId;
        String ipAddress;

        //Instantiate template with id
        oneResponse = Template.instantiate(oneClient, templateId, "", false, "");

        if(oneResponse.isError())
        {
            System.err.println("Template could not be instantiated!");
            System.err.println(oneResponse.getErrorMessage());

            return -1;
        }

        if(!loadServerList(cloud))
        {
            System.err.println("Could not retrieve IP address of new VM instance");

            return -1;
        }

        return oneResponse.getIntMessage();
    }

    public static Boolean loadServerList(Cloud cloud)
    {
        NodeList nodes;
        List<VirtualMachine> vmList;

        vmList = getVMList(cloud);

        if(vmList != null)
        {
            for(int i = 0; i < vmList.size(); i++)
            {
                try
                {
                    nodes = XMLParser.parseXML(vmList.get(i).info().getMessage(), "ETH0_IP");

                    if(nodes.getLength() > 0)
                    {
                        Server server = new Server(Integer.parseInt(vmList.get(i).getId()), nodes.item(0).getTextContent());

                        if(vmList.get(i).state() == 3)
                        {
                            server.setOperationalState(true);
                        }

                        cloud.updateServer(server);
                    }
                }
                catch(Exception ex)
                {
                    System.err.println("XML response of VM: " + i + " for ETH0 interface could not be parsed!");
                    System.err.println(ex.getMessage());
                }
            }
        }
        else
        {
            return false;
        }

        return true;
    }

    public static String getIPAddress(Cloud cloud, int vmID)
    {
        NodeList nodes;
        List<VirtualMachine> vmList;
        VirtualMachine vm;

        vmList = getVMList(cloud);

        if(vmList != null)
        {
            int i = 0;
            Boolean found = false;
            Iterator<VirtualMachine> it = vmList.iterator();

            while(it.hasNext() && !found)
            {
                vm = it.next();

                if(Integer.parseInt(vm.getId()) == vmID)
                {
                    found = true;
                }
                else
                {
                    i++;
                }
            }

            if(found)
            {
                try
                {
                    nodes = XMLParser.parseXML(vmList.get(i).info().getMessage(), "ETH0_IP");

                    if(nodes.getLength() > 0)
                    {
                        return nodes.item(0).getTextContent();
                    }
                    else
                    {
                        return null;
                    }
                }
                catch(Exception ex)
                {
                    System.err.println("XML response of VM: " + i + " for ETH0 interface could not be parsed!");
                    System.err.println(ex.getMessage());
                }
            }
        }

        return null;
    }

    private static Client getClientConnection(Cloud cloud)
    {
        try
        {
            //Connect to OpenNebula Cloud
            return new Client(cloud.getLoginCredentials().getUsername() + ":" + cloud.getLoginCredentials().getPassword(), cloud.getURL());
        }
        catch(ClientConfigurationException ex)
        {
            System.err.println("Client configurations are incorrect!");
            ex.printStackTrace();

            return null;
        }
    }

    private static List<VirtualMachine> getVMList(Cloud cloud)
    {
        Client oneClient;
        OneResponse oneResponse;
        VirtualMachinePool vmPool;
        NodeList nodes;
        List<VirtualMachine> vmList = new ArrayList<VirtualMachine>();

        oneClient = getClientConnection(cloud);

        if(oneClient == null)
        {
            return null;
        }

        vmPool = new VirtualMachinePool(oneClient);

        oneResponse = vmPool.infoMine(oneClient);

        if(oneResponse.isError())
        {
            System.err.println("VM pool could not be retrieved from server!");
            System.err.println(oneResponse.getErrorMessage());

            return null;
        }

        try
        {
            nodes = XMLParser.parseXML(oneResponse.getMessage(), "VM");
        }
        catch(Exception ex)
        {
            System.err.println("XML response of VM pool could not be parsed!");
            System.err.println(ex.getMessage());

            return null;
        }

        for(int i = 0; i < nodes.getLength(); i++)
        {
            vmList.add((VirtualMachine)vmPool.factory(nodes.item(i)));
        }

        return vmList;
    }

    private static List<Template> getTemplateList(Cloud cloud)
    {
        Client oneClient;
        OneResponse oneResponse;
        TemplatePool templatePool;
        NodeList nodes;
        List<Template> templateList = new ArrayList<Template>();

        oneClient = getClientConnection(cloud);

        if(oneClient == null)
        {
            return null;
        }

        templatePool = new TemplatePool(oneClient);

        oneResponse = templatePool.infoAll(oneClient);

        if(oneResponse.isError())
        {
            System.err.println("Template pool could not be retrieved from server!");
            System.err.println(oneResponse.getErrorMessage());

            return null;
        }

        try
        {
            nodes = XMLParser.parseXML(oneResponse.getMessage(), "VMTEMPLATE");
        }
        catch(Exception ex)
        {
            System.err.println("XML response of Template pool could not be parsed!");
            System.err.println(ex.getMessage());

            return null;
        }

        for(int i = 0; i < nodes.getLength(); i++)
        {
            templateList.add((Template)templatePool.factory(nodes.item(i)));
        }

        return templateList;
    }

    public static void printServersStatus(Cloud cloud)
    {
        System.out.println("#\tServer ID\t\tServer IP\t\tActive\t\tCPU-Load");

        for(int i = 0; i < cloud.getServers().size(); i++)
        {
            System.out.print(i + "\t");
            System.out.print(cloud.getServers().get(i).getID() + "\t\t\t");
            System.out.print(cloud.getServers().get(i).getIpaddr() + "\t\t");
            System.out.print(cloud.getServers().get(i).getOperationalState() + "\t\t");
            System.out.println(cloud.getServers().get(i).getAverageCPULoad(1));
        }
    }

    public static void printTemplatePool(Cloud cloud)
    {
        List<Template> templateList = getTemplateList(cloud);

        System.out.println("#\tTemplate ID\t\t Template name");

        for(int i = 0; i < templateList.size(); i++)
        {
            System.out.print(i + "\t\t");
            System.out.print(templateList.get(i).getId() + "\t\t\t");
            System.out.println(templateList.get(i).getName());
        }
    }
}
