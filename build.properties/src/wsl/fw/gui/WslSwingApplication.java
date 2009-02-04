//==============================================================================
// WslSwingApplication.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.gui;

import wsl.fw.util.Log;

import wsl.fw.resource.ResId;

/**
 * Superclass for all WSL Swing applications. Subclasses should implement main()
 *  and call setApplication with a new subclass application object. Then call
 * show() to show the application.
 * Initialisation should be done in the subclass constructor. If required
 * exitApplication() can be overridden to alter the close behaviour and/or
 * free resources (i.e. call DataManager.cloaseAll()).
 */
public class WslSwingApplication
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/gui/WslSwingApplication.java $ ";

    // resources
    public static final ResId EXCEPTION_NO_FRAMESET  = new ResId("WslSwingApplication.exception.NoFrameSet");
    public static final ResId DEBUG_EXITING1  = new ResId("WslSwingApplication.debug.Exiting1");
    public static final ResId DEBUG_EXITING2  = new ResId("WslSwingApplication.debug.Exiting2");

    /**
     * Singleton application object
     */
    private static WslSwingApplication _app = null;

    /**
     * Singleton application frame object
     */
    private static WslApplicationFrame _frame = null;

    /**
     * Command line args that started this app.
     */
    private String _args[];


    /**
     * Blank constructor. Set the application singleton.
     */
    public WslSwingApplication(String args[])
    {
        // set the app singleton
        setApplication(this);

        // save the args
        _args = args;
    }

    /**
     * Get the application singleton object
     * @return WslSwingApplication the application singleton object
     */
    public static WslSwingApplication getApplication()
    {
        return _app;
    }

    /**
     * Set the singleton subclass application object
     * @param app the WslSwingApplication subclass to set
     */
    public static void setApplication(WslSwingApplication app)
    {
        _app = app;
    }

    /**
     * Set the singleton subclass WslApplicationFrame object
     * @param frame the WslApplicationFrame object to set
     */
    public static void setApplicationFrame(WslApplicationFrame frame)
    {
        _frame = frame;
    }

    /**
     * @return the singleton WslApplicationFrame object
     */
    public static WslApplicationFrame getApplicationFrame()
    {
        return _frame;
    }

    /**
     * Show the application.
     */
    public void show()
    {
        // must have a frame
        if(_frame == null)
            throw new RuntimeException(EXCEPTION_NO_FRAMESET.getText());

        // show the frame
        _frame.show();
    }

    /**
     * The frame has been asked to close, delegate to the overridable
     * onExitApplication for correct subclass dependent handling.
     */
    public final static void exitApplication()
    {
        getApplication().onExitApplication();
    }

    /**
     * App has been asked to close, default behaviour is to Exit the
     * application. Subclasses should override or extend if they require
     * specific close handling.
     */
    protected void onExitApplication()
    {
        //Log.debug("Application [" + getClass().getName() + "] exiting");
        Log.debug(DEBUG_EXITING1.getText() + getClass().getName() + DEBUG_EXITING2.getText());
        System.exit(0);
    }

    /**
     * @return the command line args.
     */
    public static String[] getArgs()
    {
        return getApplication()._args;
    }

    /**
     * @return all the command line args appended as space delimited string.
     */
    public static String getAppendedArgs()
    {
        StringBuffer buf  = new StringBuffer();

        for (int i = 0; i < getArgs().length; i++)
        {
            buf.append(' ');
            buf.append(getArgs()[i]);
        }

        return buf.toString();
    }
}

//==============================================================================
// end of file WslSwingApplication.java
//==============================================================================
