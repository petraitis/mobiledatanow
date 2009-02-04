//==============================================================================
// NRNotificationListener.java
// Copyright (c) 2000 WAP Solutions Ltd.
// $Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $
// $Archive: /Framework/Source/wsl/fw/notification/NRNotificationListener.java $
//==============================================================================

package wsl.fw.notification;

import java.io.Serializable;

//--------------------------------------------------------------------------
/**
 * Interface to be implemented by non remote classes that wish to recieve
 * remote notifications. These notifications are proxied by the
 * NotificationListenerServant and passed on to this interface for handling.
 */
public interface NRNotificationListener
{
    //--------------------------------------------------------------------------
    /**
     * Called by the NotificationListenerServer to get the set of notification
     * types to listen to. Called when the listener server starts, changes after
     * that time will have no effect unless the server is stopped and restarted.
     * @return a 2 dimensional array of Strings. For each sub array the first
     *   elements is the notification type and the second is the sub type.
     */
    public String[][] getNotificationTypes();

    //--------------------------------------------------------------------------
    /**
     * This function is called by the Notifier (via NotificationListenerServant
     * and NotificationListenerServer which handles the remoting) for any
     * notifications of a type that this object has been registered as a
     * listener for (by specifying the types in getNotificationTypes).
     * @param notificationType, a string specifying the type of notification.
     * @param notificationSubtype, a string specifying the notification subtype.
     * @param notificationData, notification information specific to this
     *   notificationType. Listeners are expected to know how to interpret this
     *   data and perform appropriate processing. May be null. Must be
     *   Serializable.
     */
    public void onNotification(String notificationType,
        String notificationSubtype, Serializable notificationData);
}

//==============================================================================
// end of file NRNotificationListener.java
//==============================================================================
