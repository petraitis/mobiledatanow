//==============================================================================
// NotificationListener.java
// Copyright (c) 2000 WAP Solutions Ltd.
// $Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $
// $Archive: /Framework/Source/wsl/fw/notification/NotificationListener.java $
//==============================================================================

package wsl.fw.notification;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.io.Serializable;

//------------------------------------------------------------------------------
/**
 * Interface for remotable objects that wish to receive notifications.
 * Implementing classes should call Notifier.addNotificationListener() to
 * register their interest ina specific notification type.
 * Classes interested in receiving notifications should implement
 * NRNotification Listener and pass themselves to NotificationListenerServer.
 */
public interface NotificationListener extends Remote
{
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
     *   data and perform appropriate processing. May be null. Is serializable.
     * @throws RemoteException if there is an RMI error.
     */
    public void onNotification(String notificationType,
        String notificationSubtype, Serializable notificationData)
        throws RemoteException;
}

//==============================================================================
// end of file NotificationListener.java
//==============================================================================
