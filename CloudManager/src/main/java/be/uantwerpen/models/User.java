package be.uantwerpen.models;

/**
 * Created by Thomas on 18/02/2016.
 */
public class User
{
    private String username;
    private String password;

    public User(String username, String password)
    {
        this.username = username;
        this.password = password;
    }

    public String getUsername()
    {
        return this.username;
    }

    public String getPassword()
    {
        return this.password;
    }
}
