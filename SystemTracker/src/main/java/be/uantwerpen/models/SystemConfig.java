package be.uantwerpen.models;

/**
 * Created by Thomas on 24/02/2016.
 */
public class SystemConfig
{
    private final long id;
    private final String vmPool;

    public SystemConfig(long id, String vmPool)
    {
        this.id = id;
        this.vmPool = vmPool;
    }

    public long getId()
    {
        return this.id;
    }

    public String getVmPool()
    {
        return this.vmPool;
    }
}
