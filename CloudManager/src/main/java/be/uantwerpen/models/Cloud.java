package be.uantwerpen.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Thomas on 15/02/2016.
 */
public class Cloud
{
    private final String URL;
    private final User loginUser;
    private List<Server> servers;

    public Cloud()
    {
        this.URL = "http://localhost:9869/RPC2";
        this.loginUser = new User();
        this.servers = new ArrayList<Server>();
    }

    public Cloud(String URL, User loginUser)
    {
        this.URL = URL;
        this.loginUser = loginUser;
        this.servers = new ArrayList<Server>();
    }

    public String getURL()
    {
        return this.URL;
    }

    public User getLoginCredentials()
    {
        return this.loginUser;
    }

    public List<Server> getServers()
    {
        return this.servers;
    }

    private boolean addServer(Server server)
    {
        if(!this.servers.contains(server))
        {
            return this.servers.add(server);
        }
        else
        {
            return false;
        }
    }

    public boolean updateServer(Server server)
    {
        if(this.servers.contains(server))
        {
            boolean found = false;

            for(Iterator<Server> it = servers.iterator(); it.hasNext() && !found;)
            {
                Server aServer = it.next();

                if(aServer.equals(server))
                {
                    server.setOperationalState(aServer.getOperationalState());

                    found = true;
                }
            }

            return found;
        }
        else
        {
            return this.servers.add(server);
        }
    }

    public boolean removeServer(Server server)
    {
        return this.servers.remove(server);
    }

    public boolean removeServer(int index)
    {
        if(this.servers.remove(index) != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
