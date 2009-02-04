//==============================================================================
// NotificationListenerServant.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.notification;

import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.remote.RmiServantBase;
import java.rmi.RemoteException;
import java.io.Serializable;
import wsl.fw.resource.ResId;

//--------------------------------------------------------------------------
/**
 * RMI servant to receive notifications, which are then passed on to the
 * non-RMI listener delegate.
 */
public class NotificationListenerServant
    extends RmiServantBase implements NotificationListener
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/notification/NotificationListenerServant.java $ ";

    // resources
    public static final ResId EXCEPTION_DELEGATE  = new ResId("NotificationListenerServant.exception.Delegate");

    // delegate that receives the notifications
    NRNotificationListener _listenerDelegate;

    //--------------------------------------------------------------------------
    /**
     * Constructor, store the delegate reference.
     */
    public NotificationListenerServant(NRNotificationListener ld)
    {
        Util.argCheckNull(ld);
        _listenerDelegate = ld;
    }

    //--------------------------------------------------------------------------
    /**
     * This function is called by the Notifier for any notifications of a type
     * that this object has been registered as a listener for. It is advisable
     * to return from this function quickly, use threads for any extended
     * processing.
     * @param notificationType, a string specifying the type of notification.
     * @param notificationSubtype, a string specifying the notification subtype.
     * @param notificationData, notification information specific to this
     *   notificationType. Listeners are expected to know how to interpret this
     *   data and perform appropriate processing. May be null. Is Serializable.
     * @throws RemoteException if there is an RMI error.
     */
    public void onNotification(String notificationType,
        String notificationSubtype, Serializable notificationData)
        throws RemoteException
    {
        // use a thread to pass this on to the delegate to avoid stalling the
        // rmi call
        NotThread thread = new NotThread(notificationType, notificationSubtype,
            notificationData);
        thread.start();
    }

    //--------------------------------------------------------------------------
    /**
     * Inner thread class to unblock notification sending to the delegate.
     */
    private class NotThread extends Thread
    {
        // attributes
        String _notificationType;
        String _notificationSubtype;
        Serializable _notificationData;

        // constructor
        public NotThread(String notificationType, String notificationSubtype,
            Serializable notificationData)
        {
            _notificationType    = notificationType;
            _notificationSubtype = notificationSubtype;
            _notificationData    = notificationData;
        }

        // thread run method
        public void run()
        {
            try
            {
                // send on to the deleagate
                _listenerDelegate.onNotification(_notificationType,
                    _notificationSubtype, _notificationData);
            }
            catch (Throwable e)
            {
                Log.error(EXCEPTION_DELEGATE.getText(), e);
            }
        }
    }
}

//==============================================================================
// end of file NotificationListenerServant.java
//==============================================================================
