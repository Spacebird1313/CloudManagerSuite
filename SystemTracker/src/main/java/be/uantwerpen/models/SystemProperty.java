package be.uantwerpen.models;

/**
 * Created by Thomas on 24/02/2016.
 */
public class SystemProperty
{
    private long id;
    private String key;
    private String value;

    public SystemProperty()
    {
        this.id = -1;
        this.key = "";
        this.value = "";
    }

    public SystemProperty(long id, String key, String value)
    {
        this.id = id;
        this.key = key;
        this.value = value;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public long getId()
    {
        return this.id;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getKey()
    {
        return this.key;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return this.value;
    }
}
