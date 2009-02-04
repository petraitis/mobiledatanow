//==============================================================================
// NotificationListenerServer.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.notification;

import wsl.fw.remote.RmiServer;
import wsl.fw.remote.LocalServerFactory;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import java.rmi.server.UnicastRemoteObject;
import wsl.fw.resource.ResId;

//------------------------------------------------------------------------------
/**
 * Notification server, to be used by no remote calsses that wish to receive
 * remote notifications from Notifier. Handles starting and stopping the
 * listener and registering the listener with the Notifier.
 * The runserver, register servants etc fns in the RmiServer superclass should
 * not be called.
 *
 * Note that unlike the RmiServer superclass this does not load contexts so
 * ensure that Config and contexts for the app have been set up (the args are
 * passed to LocalServerFactory.setArgs to ensure it is inited correctly).
 * fixme, May also need to set an RMISecurityManager.
 *
 * Standard usage:
 * Create a listener class that implements NRNotificationListener.
 * Pass that class to NotificationListenerServer constructor.
 * Call start(), the listener will now be able to recieve notifications.
 * Before app exit call stop().
 */
public class NotificationListenerServer extends RmiServer
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/notification/NotificationListenerServer.java $ ";

    // resources
    public static final ResId WARNING_REGIDTER_LISTENER  = new ResId("NotificationListenerServer.warning.RegisterListener");
    public static final ResId ERR_REMOTE_ID  = new ResId("NotificationListenerServer.error.RemoteId");

    // thread for re-listening
    ReListenThread              _reListenThread   = null;
    NRNotificationListener      _listenerDelegate = null;
    NotificationListenerServant _listener         = null;
    String                      _listenerString   = null;
    String                      _notTypes[][]     = null;

    //--------------------------------------------------------------------------
    /**
     * Constructor, passes on to RmiServer to do the init and set up the port
     * ranges.
     */
    public NotificationListenerServer(NRNotificationListener ld, String args[])
    {
        // call superclass telling it no to load the context
        super(args, false);
        Util.argCheckNull(ld);
        _listenerDelegate = ld;
        _listener = new NotificationListenerServant(ld);
    }

    //--------------------------------------------------------------------------
    /**
     * Start listening, this will export the listener and start listening for
     * notifications. The listener will periodically re-register with the
     * Notifier.
     */
    public synchronized boolean start()
    {
        if (_reListenThread == null)
        {
            // get the notification types from the NR listener delegate
            _notTypes = _listenerDelegate.getNotificationTypes();

            // create a thread for the relistening and start it.
            _reListenThread = new ReListenThread();
            _reListenThread.start();
            return true;
        }
        else
            return false;
    }

    //--------------------------------------------------------------------------
    /**
     * Stop listening. This will remove the listener and unexport it.
     */
    public synchronized boolean stop()
    {
        if (_reListenThread != null)
        {
            // interrupt the re-listening thread and wait for it to stop
            _reListenThread._shouldTerminate = true;
            _reListenThread.interrupt();
            try
            {
                _reListenThread.join();
            }
            catch(InterruptedException e)
            {
            }

            _reListenThread = null;
            return true;
        }
        else
            return false;
    }

    //--------------------------------------------------------------------------
    /**
     * Do not use
     */
    protected void runServer()
    {
        throw new UnsupportedOperationException();
    }

    //--------------------------------------------------------------------------
    /**
     * Do not use.
     */
    protected void registerServants()
    {
        throw new UnsupportedOperationException();
    }

    //--------------------------------------------------------------------------
    /**
     * Register or unregister the listener with the Notifier.
     * @param register, if true then register, else unregister.
     */
    private void listen(boolean register)
    {
        try
        {
            // if no listeners then do nothing
            if (_notTypes.length <= 0)
                return;

            // get a reference to the Notifier
            Notifier notifier = (Notifier) LocalServerFactory.get(_securityId,
                Notifier.class.getName());

            // iterate notification types
            for (int i = 0; i < _notTypes.length; i++)
                if (register)
                    notifier.addNotificationListener(_listener, _notTypes[i][0], _notTypes[i][1]);
                else
                    notifier.removeNotificationListener(_listener, _notTypes[i][0], _notTypes[i][1]);
        }
        catch (Exception e)
        {
            Log.warning(WARNING_REGIDTER_LISTENER.getText() + " "
                + e.toString());
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Get a unique id string representing the the remote listener.
     * @return the remote listener's unique id string.
     */
    public String getListenerId()
    {
        return _listenerString;
    }

    //--------------------------------------------------------------------------
    /**
     * Inner class thread to handle reregistering the listeners.
     */
    private class ReListenThread extends Thread
    {
        public boolean _shouldTerminate = false;
        //----------------------------------------------------------------------
        /**
         * Do the add listen calls here, exit when _stopListening is true.
         */
        public void run()
        {
            // Create and install a security manager
            // fixme System.setSecurityManager(new RMISecurityManager());

            // register our listener as a remote object
            registerServant(_listener);
            try
            {
                // save the listener string for later use
                _listenerString = UnicastRemoteObject.toStub(_listener).toString();
            }
            catch (Exception e)
            {
                Log.error(ERR_REMOTE_ID.getText(), e);
            }

            // periodically register the listener with the notifier
            try
            {
                while (!_shouldTerminate)
                {
                    listen(true);
                    Thread.sleep(_rebindMS);
                }
            }
            catch (InterruptedException e)
            {
            }
            finally
            {
                // before exiting unlisten and unexport servant
                listen(false);
                unexportServants();
                // unexported, remove the the string
                _listenerString = null;
            }
        }
    }
}

//==============================================================================
// end of file NotificationListenerServer.java
//==============================================================================
