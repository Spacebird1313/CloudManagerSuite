package be.uantwerpen.terminal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Thomas on 21/02/2016.
 */
public class TerminalReader implements Runnable
{
    private TerminalObserver observer;

    public TerminalReader()
    {
        this.observer = new TerminalObserver();
    }

    public TerminalObserver getObserver()
    {
        return this.observer;
    }

    @Override
    public void run()
    {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("# ");

        try
        {
            String command = input.readLine();
            this.observer.setChanged();
            this.observer.notifyObservers(command);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}
