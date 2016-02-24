package be.uantwerpen.services;

import be.uantwerpen.models.SystemProperty;
import org.springframework.stereotype.Service;

/**
 * Created by Thomas on 24/02/2016.
 */
@Service
public class SystemConfigService
{
    private String vmPool;

    public SystemConfigService()
    {
        this.vmPool = "default";
    }

    public void setVmPool(String groupName)
    {
        this.vmPool = groupName;
    }

    public String getVmPool()
    {
        return this.vmPool;
    }

    public boolean setProperty(SystemProperty property)
    {
        switch(property.getKey().toLowerCase())
        {
            case "vmpool":
                this.setVmPool(property.getValue());
                break;
            default:
                //Property not found
                return false;
        }

        return true;
    }
}
