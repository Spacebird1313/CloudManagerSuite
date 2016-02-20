package be.uantwerpen.services;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.springframework.stereotype.Service;

/**
 * Created by Thomas on 13/02/2016.
 */
@Service
public class SystemLoadService
{
    public Double getCPULoad()
    {
        Sigar sigar = new Sigar();
        CpuPerc cpu = null;

        try
        {
            cpu = sigar.getCpuPerc();
        }
        catch(SigarException se)
        {
            System.err.println("Error, while retrieving CPU load");
            se.printStackTrace();

            return -1d;
        }

        return cpu.getCombined() * 100;
    }

    public long getTotalMem()
    {
        Sigar sigar = new Sigar();
        Mem memory = null;

        try
        {
            memory = sigar.getMem();
        }
        catch(SigarException se)
        {
            System.err.println("Error, while retrieving Total memory");
            se.printStackTrace();

            return -1L;
        }

        return memory.getTotal();
    }

    public long getFreeMem()
    {
        Sigar sigar = new Sigar();
        Mem memory = null;

        try
        {
            memory = sigar.getMem();
        }
        catch(SigarException se)
        {
            System.err.println("Error, while retrieving Free memory");
            se.printStackTrace();

            return -1L;
        }

        return memory.getFree();
    }

    public long getInUseMem()
    {
        Sigar sigar = new Sigar();
        Mem memory = null;

        try
        {
            memory = sigar.getMem();
        }
        catch(SigarException se)
        {
            System.err.println("Error, while retrieving In-use memory");
            se.printStackTrace();

            return -1L;
        }

        return memory.getUsed();
    }
}
