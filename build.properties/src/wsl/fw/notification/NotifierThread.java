//==============================================================================
// NotifierThread.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.notification;

import java.rmi.RemoteException;
import java.io.Serializable;

//------------------------------------------------------------------------------
/**
 * Thread to handle the sending of notificatons without blocking the
 * NotifierServant.
 */
public class NotifierThread extends Thread
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/notification/NotifierThread.java $ ";

    // attributes
    String               _notificationType;
    String               _notificationSubtype;
    Serializable         _notificationData;
    NotificationListener _listener;
    Notifier             _notifier;

    //--------------------------------------------------------------------------
    /**
     * Constructor taking the notification information and the target listener.
     * @param notificationType, a string specifying the type of notification.
     * @param notificationData, notification information specific to this
     *   notificationType. Listeners are expected to know how to interpret this
     *   data and perform appropriate processing. May be null.
     * @param listener, the NotificationListener that will be called whenever
     *   a notification event of the specified type occurs. Not null.
     * @param notifier, the controlling notifier.
     */
    public NotifierThread(String notificationType, String notificationSubtype,
        Serializable notificationData, NotificationListener listener,
        Notifier notifier)
    {
        // save the params
        _notificationType    = notificationType;
        _notificationSubtype = notificationSubtype;
        _notificationData    = notificationData;
        _listener            = listener;
        _notifier            = notifier;
    }

    //--------------------------------------------------------------------------
    /**
     * Thread run() method, sends the notification.
     */
    public void run()
    {
        try
        {
            // send the notification
            _listener.onNotification(_notificationType, _notificationSubtype, _notificationData);
        }
        catch (RemoteException e)
        {
            // failed to reach listener, remove it from the listener set
            try
            {
                _notifier.removeNotificationListener(_listener, Notifier.NT_ALL, Notifier.NST_ALL);
            }
            catch (Exception ex)
            {
            }
        }
    }
}

//==============================================================================
// end of file NotifierThread.java
//==============================================================================
