package be.uantwerpen.models;

import org.opennebula.client.vm.VirtualMachine;

/**
 * Created by Thomas on 13/02/2016.
 */
public class Server
{
    private final int id;
    private final String ipaddr;
    private Double[] CPUloads;
    private Long[] memFree;
    private Long memTotal;
    private static int maxHistory;
    private int historyCounter;
    private Boolean historyFull;
    private Boolean operational;
    private Boolean bootUp;

    public Server(int vmId, String ipaddr)
    {
        this.id = vmId;
        this.ipaddr = ipaddr;
        this.maxHistory = 250;
        this.historyFull = false;
        this.historyCounter = 0;
        this.operational = false;
        this.bootUp = false;
        this.CPUloads = new Double[maxHistory];
        this.memFree = new Long[maxHistory];
        this.memTotal = -1L;
    }

    public Server(int vmId, String ipaddr, int maxHistory)
    {
        this.id = vmId;
        this.ipaddr = ipaddr;
        this.maxHistory = maxHistory;
        this.historyFull = false;
        this.historyCounter = 0;
        this.operational = false;
        this.bootUp = false;
        this.CPUloads = new Double[maxHistory];
        this.memFree = new Long[maxHistory];
        this.memTotal = -1L;
    }

    public String getIpaddr()
    {
        return this.ipaddr;
    }

    public int getID()
    {
        return this.id;
    }

    public void setOperationalState(Boolean operational)
    {
        this.operational = operational;
    }

    public Boolean getOperationalState()
    {
        return this.operational;
    }

    public void setBootState(Boolean bootUp)
    {
        this.bootUp = bootUp;
    }

    public Boolean getBootState()
    {
        return this.bootUp;
    }

    public void addLoadState(double CPULoad, long memFree, long memTotal)
    {
        if((CPULoad < 0 && CPULoad > 100) || memFree < 0 || memTotal < 0)
        {
            //Failed to retrieve correct load information
            return;
        }

        if(this.historyCounter >= this.maxHistory)
        {
            this.historyFull = true;
            this.historyCounter = 0;
        }

        this.CPUloads[historyCounter] = CPULoad;
        this.memFree[historyCounter] = memFree;
        this.memTotal = memTotal;

        this.historyCounter++;
    }

    public long getMemTotal()
    {
        return this.memTotal;
    }

    public Double getAverageCPULoad(int numOfSamples)
    {
        return this.calculateAverage(this.CPUloads, numOfSamples);
    }

    public Long getAverageMemFree(int numOfSamples)
    {
        return new Double(this.calculateAverage(this.memFree, numOfSamples)).longValue();
    }

    public void addServerLoad(ServerLoad serverLoad)
    {
        this.addLoadState(serverLoad.getCPULoad(), serverLoad.getMemFree(), serverLoad.getMemTotal());
    }

    private Double calculateAverage(Number[] samplePoints, int numOfSamples)
    {
        Double sum = 0D;

        if(numOfSamples < 0 || numOfSamples > this.maxHistory)
        {
            //Invalid range
            return -1D;
        }

        for(int i = this.historyCounter - 1; i >= 0 && i >= this.historyCounter - numOfSamples; i--)
        {
            sum += (Double)samplePoints[i];
        }

        if(!this.historyFull)
        {
            if(numOfSamples > this.historyCounter)
            {
                return sum / this.historyCounter;
            }
        }
        else
        {
            if(historyCounter - numOfSamples < 0)
            {
                for(int i = this.maxHistory - 1; i > this.maxHistory - (numOfSamples - this.historyCounter); i--)
                {
                    sum += (Double)samplePoints[i];
                }
            }
        }

        return sum / numOfSamples;
    }

    @Override
    public boolean equals(Object object)
    {
        if(!object.getClass().equals(Server.class))
        {
            return false;
        }

        if(((Server)object).getIpaddr().equals(this.ipaddr) && (((Server)object).getID() == this.id))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
