package be.uantwerpen.services;

import be.uantwerpen.models.Server;
import be.uantwerpen.models.ServerLoad;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Thomas on 14/02/2016.
 */
@Service
public class ServerPoller
{
    public ServerLoad pollServerLoad(Server server, int serverPort, String serviceURL)
    {
        RestTemplate restTemplate = new RestTemplate();
        URL serverURL;
        ServerLoad serverLoad;

        try
        {
            serverURL = new URL("http", server.getIpaddr(), serverPort, serviceURL);
        }
        catch(MalformedURLException e)
        {
            System.err.println("Cannot form correct URL to contact server: " + server.getIpaddr());

            return null;
        }

        try
        {
            serverLoad = restTemplate.getForObject(serverURL.toString(), ServerLoad.class);
        }
        catch(RestClientException e)
        {
            System.err.println("Server unreachable to connect: " + server.getIpaddr());

            return null;
        }

        if(serverLoad != null)
        {
            server.addServerLoad(serverLoad);
        }

        return serverLoad;
    }
}
