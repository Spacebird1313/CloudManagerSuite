package be.uantwerpen.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Thomas on 24/02/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServerConfig
{
    private long id;
    private String vmPool;

    public ServerConfig()
    {
    }

    public long getId()
    {
        return this.id;
    }

    public String getVmPool()
    {
        return this.vmPool;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public void setVmPool(String vmPool)
    {
        this.vmPool = vmPool;
    }

    @Override
    public String toString()
    {
        return "Value{" +
                "id=" + this.id +
                ", vmPool='" + this.vmPool + '\'' +
                '}';
    }
}
