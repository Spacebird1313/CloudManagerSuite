package be.uantwerpen.services;

import be.uantwerpen.models.Cloud;
import be.uantwerpen.models.Server;
import be.uantwerpen.tools.XMLParser;
import org.opennebula.client.*;
import org.opennebula.client.template.Template;
import org.opennebula.client.template.TemplatePool;
import org.opennebula.client.vm.VirtualMachine;
import org.opennebula.client.vm.VirtualMachinePool;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Thomas on 15/02/2016.
 */
public class CloudManager
{
    private Cloud cloud;

    public CloudManager(Cloud cloud)
    {
        this.cloud = cloud;
    }

    public Boolean createVMFromTemplate(int templateId)
    {
        Client oneClient;
        OneResponse oneResponse;

        oneClient = this.getClientConnection();

        if(oneClient == null)
        {
            return false;
        }

        return this.instantiateTemplate(oneClient, templateId);
    }

    public Boolean createVMFromTemplate(String templateName)
    {
        Client oneClient;
        OneResponse oneResponse;
        List<Template> templateList;
        Template template;

        oneClient = this.getClientConnection();

        if(oneClient == null)
        {
            return false;
        }

        templateList = this.getTemplateList();

        if(templateList == null)
        {
            return false;
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
            return this.instantiateTemplate(oneClient, Integer.parseInt(templateList.get(i).getId()));
        }
        else
        {
            System.err.println("Template with name: " + templateName + " does not exist on the server!");

            return false;
        }
    }

    private Boolean instantiateTemplate(Client oneClient, int templateId)
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

            return false;
        }

        newVMId = oneResponse.getIntMessage();

        ipAddress = this.getIPAddress(newVMId);

        if(ipAddress != null)
        {
            cloud.addServer(new Server(newVMId, ipAddress));
        }
        else
        {
            System.err.println("Could not retrieve IP address of new VM instance");

            return false;
        }

        return true;
    }

    public Boolean loadServerList()
    {
        NodeList nodes;
        List<VirtualMachine> vmList;

        vmList = this.getVMList();

        if(vmList != null)
        {
            for(int i = 0; i < vmList.size(); i++)
            {
                try
                {
                    nodes = XMLParser.parseXML(vmList.get(i).info().getMessage(), "ETH0_IP");

                    if(nodes.getLength() > 0)
                    {
                        cloud.addServer(new Server(Integer.parseInt(vmList.get(i).getId()), nodes.item(0).getTextContent()));
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

    public String getIPAddress(int vmID)
    {
        NodeList nodes;
        List<VirtualMachine> vmList;
        VirtualMachine vm;

        vmList = this.getVMList();

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

    private Client getClientConnection()
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

    private List<VirtualMachine> getVMList()
    {
        Client oneClient;
        OneResponse oneResponse;
        VirtualMachinePool vmPool;
        NodeList nodes;
        List<VirtualMachine> vmList = new ArrayList<VirtualMachine>();

        oneClient = this.getClientConnection();

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

    private List<Template> getTemplateList()
    {
        Client oneClient;
        OneResponse oneResponse;
        TemplatePool templatePool;
        NodeList nodes;
        List<Template> templateList = new ArrayList<Template>();

        oneClient = this.getClientConnection();

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

    public void printServerStatus()
    {
        System.out.println("#\tServer ID\t\tServer IP\t\tCPU-Load");

        for(int i = 0; i < cloud.getServers().size(); i++)
        {
            System.out.print(i + "\t");
            System.out.print(cloud.getServers().get(i).getID() + "\t\t\t");
            System.out.print(cloud.getServers().get(i).getIpaddr() + "\t\t");
            System.out.println(cloud.getServers().get(i).getAverageCPULoad(1));
        }
    }
}
