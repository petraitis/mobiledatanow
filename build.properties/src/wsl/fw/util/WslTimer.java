package wsl.fw.util;

// imports
import java.util.Vector;

/**
 * Title:        Framework utility classes
 * Description:
 * Copyright:    Copyright (c) Jason Nigro
 * Company:      WAP Solutions Ltd
 * @author Jason Nigro
 * @version 1.0
 */

/**
 * Timer notifies every [delay] milliseconds after being started
 */
public class WslTimer implements Runnable
{
    //--------------------------------------------------------------------------
    // attributes

    private int _delay;
    private Vector _listeners = new Vector();
    private boolean _isTimerRunning = false;


    //--------------------------------------------------------------------------
    // construction

    /**
     * Ctor taking a delay
     * @param delay the length of time the timer will wait before notifying listeners
     */
    public WslTimer(int delay)
    {
        // set delay
        setDelay(delay);
    }

    /**
     * Ctor taking a delay and a listener
     * @param delay the length of time the timer will wait before notifying listeners
     * @param listener a WslTimerListener
     */
    public WslTimer(int delay, WslTimerListener listener)
    {
        // set delay
        setDelay(delay);

        // add listener
        addTimerListener(listener);
    }


    //--------------------------------------------------------------------------
    // accessors

    /**
     * Set the delay
     * @param delay
     */
    public void setDelay(int delay)
    {
        _delay = delay;
    }

    /**
     * @return true if the timer is running
     */
    public boolean isTimerRunning()
    {
        return _isTimerRunning;
    }


    //--------------------------------------------------------------------------
    // listeners

    /**
     * Add a WslTimerListener
     * @param listener the listener to add
     */
    public void addTimerListener(WslTimerListener listener)
    {
        _listeners.add(listener);
    }

    /**
     * Remove a WslTimerListener
     * @param listener the listener to remove
     */
    public void removeTimerListener(WslTimerListener listener)
    {
        _listeners.remove(listener);
    }


    //--------------------------------------------------------------------------
    // start, stop

    /**
     * Start the timer
     */
    public void start()
    {
        // set the run flag
        _isTimerRunning = true;

        // create and start thread
        Thread thread = new Thread(this);
        thread.start();
    }

    /**
     * Stop the timer
     */
    public void stop()
    {
        // clear running flag
        _isTimerRunning = false;
    }


    //--------------------------------------------------------------------------
    // run

    public void run()
    {
        try
        {
            // continue if running
            while(_isTimerRunning)
            {
                // sleep for delay
                Thread.currentThread().sleep(_delay);

                // notify listeners
                for(int i = 0; i < _listeners.size(); i++)
                    ((WslTimerListener)_listeners.elementAt(i)).onTimer(this);
            }
        }
        catch(Exception e)
        {
            Log.error("WslTimer.run: ", e);
        }
    }
}