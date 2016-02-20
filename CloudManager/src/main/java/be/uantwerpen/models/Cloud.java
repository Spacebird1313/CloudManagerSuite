package be.uantwerpen.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 15/02/2016.
 */
public class Cloud
{
    private final String URL;
    private final User loginUser;
    private List<Server> servers;

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

    public void addServer(Server server)
    {
        this.servers.add(server);
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
