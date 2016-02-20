package be.uantwerpen.services;

import be.uantwerpen.models.Cloud;
import be.uantwerpen.models.Server;
import be.uantwerpen.tools.XMLParser;
import org.opennebula.client.*;
import org.opennebula.client.template.Template;
import org.opennebula.client.vm.VirtualMachine;
import org.opennebula.client.vm.VirtualMachinePool;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
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
        VirtualMachine vm;
        OneResponse oneResponse;
        int newVMId;

        try
        {
            //Connect to OpenNebula Cloud
            oneClient = new Client(cloud.getLoginCredentials().getUsername() + ":" + cloud.getLoginCredentials().getPassword(), cloud.getURL());
        }
        catch(ClientConfigurationException ex)
        {
            System.err.println("Client configurations are incorrect!");
            ex.printStackTrace();

            return false;
        }

        //Instantiate template with id
        oneResponse = Template.instantiate(oneClient, templateId, "", false, "");

        if(oneResponse.isError())
        {
            System.err.println("Template could not be instantiated!");
            System.err.println(oneResponse.getErrorMessage());

            return false;
        }

        newVMId = oneResponse.getIntMessage();

        cloud.addServer(new Server(newVMId, ""));

        return true;
    }

    public Boolean createVMFromTemplate(String templateName)
    {
        return true;
    }

    public Boolean listSetup()
    {
        Client oneClient;
        OneResponse oneResponse;
        VirtualMachinePool vmPool;
        NodeList nodes;
        List<VirtualMachine> vmList = new ArrayList<VirtualMachine>();

        try
        {
            //Connect to OpenNebula Cloud
            oneClient = new Client(cloud.getLoginCredentials().getUsername() + ":" + cloud.getLoginCredentials().getPassword(), cloud.getURL());
        }
        catch(ClientConfigurationException ex)
        {
            System.err.println("Client configurations are incorrect!");
            ex.printStackTrace();

            return false;
        }

        vmPool = new VirtualMachinePool(oneClient);

        oneResponse = vmPool.infoMine(oneClient);

        if(oneResponse.isError())
        {
            System.err.println("VM pool could not be retrieved from server!");
            System.err.println(oneResponse.getErrorMessage());

            return false;
        }

        try
        {
            nodes = XMLParser.parseXML(oneResponse.getMessage(), "VM");
        }
        catch(Exception ex)
        {
            System.err.println("XML response of VM pool could not be parsed!");
            System.err.println(ex.getMessage());

            return false;
        }

        for(int i = 0; i < nodes.getLength(); i++)
        {
            vmList.add((VirtualMachine)vmPool.factory(nodes.item(i)));
        }

        System.out.println(vmList.get(0).info().getMessage());

        try
        {
            nodes = XMLParser.parseXML(vmList.get(0).info().getMessage(), "ID");
        }
        catch(Exception ex)
        {
            System.err.println("XML response of ETH0 interface could not be parsed!");
            System.err.println(ex.getMessage());

            return false;
        }

        System.out.println(nodes.item(0).getAttributes().getLength());

        return true;
    }
}
