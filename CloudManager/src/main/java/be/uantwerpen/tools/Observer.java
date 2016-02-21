package be.uantwerpen.tools;

import java.util.Observable;

/**
 * Created by Thomas on 21/02/2016.
 */
public class Observer extends Observable
{
    public void clearChanged()
    {
        super.clearChanged();
    }

    public void setChanged()
    {
        super.setChanged();
    }
}
