package be.uantwerpen.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.stereotype.Component;

/**
 * Created by Thomas on 14/02/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServerLoad
{
    private Long id;
    private String timeStamp;
    private double CPULoad;
    private long memTotal;
    private long memFree;
    private long memInUse;

    public ServerLoad()
    {
    }

    public Long getId()
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

    public void setId(long id)
    {
        this.id = id;
    }

    public void setTimeStamp(String timeStamp)
    {
        this.timeStamp = timeStamp;
    }

    public void setCPULoad(double CPULoad)
    {
        this.CPULoad = CPULoad;
    }

    public void setMemTotal(long memTotal)
    {
        this.memTotal = memTotal;
    }

    public void setMemFree(long memFree)
    {
        this.memFree = memFree;
    }

    public void setMemInUse(long memInUse)
    {
        this.memInUse = memInUse;
    }

    @Override
    public String toString()
    {
        return "Value{" +
                "id=" + this.id +
                ", timeStamp='" + this.timeStamp + '\'' +
                ", CPULoad=" + this.CPULoad +
                ", memTotal=" + this.memTotal +
                ", memFree=" + this.memFree +
                ", memInUse=" + this.memInUse +
                '}';
    }
}
