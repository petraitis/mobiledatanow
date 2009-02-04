//==============================================================================
// NotifierServer.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.notification;

import wsl.fw.util.Log;
import wsl.fw.notification.NotifierServant;
import wsl.fw.remote.RmiServer;
import wsl.fw.datasource.DataManager;
import wsl.fw.resource.ResourceManager;
import wsl.fw.resource.ResId;

//------------------------------------------------------------------------------
/**
 * Server for the NotifierServant. Only one instance should be used to ensure
 * all notifications are global.
 */
public class NotifierServer extends RmiServer
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $"
        + "$Archive: /Framework/Source/wsl/fw/notification/NotifierServer.java $";

    // resources
    public static final ResId DEBUG_DATA_MANAGER_CLOSING  = new ResId("NotifierServer.debug.DataManagerClosing");
    public static final ResId ERR_DATA_MANAGER  = new ResId("NotifierServer.error.DataManager");
    public static final ResId DEBUG_RMI1  = new ResId("NotifierServer.debug.RMI1");
    public static final ResId DEBUG_RMI2  = new ResId("NotifierServer.debug.RMI2");

    //--------------------------------------------------------------------------
    /**
     * Constructor, inits based on the Config and command line args.
     * @param args, command line args passed from main().
     */
    public NotifierServer(String args[])
    {
        // pass args to superclass
        super(args);
    }

    //--------------------------------------------------------------------------
    /**
     * Register the servants for this server. Called by superclass.
     */
    protected void registerServants()
    {
        // register servants allowing server to assign ports
        // register the notifier servant so we can process notifications
        registerServant(new NotifierServant());
    }

    //--------------------------------------------------------------------------
    /**
     * Main entrypoint.
     */
    public static void main(String args[])
    {
        // set resource manager
        ResourceManager.set(new ResourceManager());

        // create an instance of the Notifier server
        // this also loads the default RMIServer context and inits the
        // LocalServerFactory
        NotifierServer ns = new NotifierServer(args);

        // place any extra command line parameter parsing here
        // place any extra Config or context loading here

        // will be getting and passing on DataObjects, so ensure data manager
        // is set. since we never actually use the DataObjects, just pass them
        // on it does not matter that we do not use a specific DataManager
        // subclass that contains the entity definitions.

        DataManager.setDataManager(new DataManager());

        // start the Notifier server running
        ns.runServer();

        // notifier server is exiting, since we set a data manager we should
        // now close it.
        try
        {
            Log.debug(DEBUG_DATA_MANAGER_CLOSING.getText());
            DataManager.closeAll();
        }
        catch (Exception e)
        {
            Log.error(ERR_DATA_MANAGER.getText(), e);
        }

        Log.debug(DEBUG_RMI1.getText() + " [" + NotifierServer.class.getName() + "] " + DEBUG_RMI2.getText());
    }
}

//==============================================================================
// end of file NotifierServer.java
//==============================================================================
