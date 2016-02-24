package be.uantwerpen.models;

/**
 * Created by Thomas on 13/02/2016.
 */
public class SystemLoad
{
    private final long id;
    private final String timeStamp;
    private final double CPULoad;
    private final long memTotal;
    private final long memFree;
    private final long memInUse;

    public SystemLoad(long id, String timeStamp, double CPULoad, long memTotal, long memFree, long memInUse)
    {
        this.id = id;
        this.timeStamp = timeStamp;
        this.CPULoad = CPULoad;
        this.memTotal = memTotal;
        this.memFree = memFree;
        this.memInUse = memInUse;
    }

    public long getId()
    {
        return this.id;
    }

    public String getTimeStamp()
    {
        return this.timeStamp;
    }

    public double getCPULoad()
    {
        return this.CPULoad;
    }

    public long getMemTotal()
    {
        return this.memTotal;
    }

    public long getMemFree()
    {
        return this.memFree;
    }

    public long getMemInUse()
    {
        return this.memInUse;
    }
}
