//==============================================================================
// NotificationSender.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.notification;

import wsl.fw.security.SecurityId;
import wsl.fw.util.Log;
import wsl.fw.remote.LocalServerFactory;
import java.io.Serializable;
import wsl.fw.resource.ResId;

//------------------------------------------------------------------------------
/**
 * Helper class to send notifications.
 */
public class NotificationSender
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/notification/NotificationSender.java $ ";

    // resources
    public static final ResId WARNING_SENT_NOTIFICATION  = new ResId("NotificationSender.warning.SentNotification");

    //--------------------------------------------------------------------------
    /**
     * Private to stop instantiation
     */
    private NotificationSender()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Send a notification to the global Notifier which will be propogated to
     * all listeners that have registered interest in the notificationType and
     * subtype. NT_ALL and NST_ALL may not be used.
     * Calling this function requires that the app has already inited its
     * Config, contexts and LocalServerfactory.
     * @param id, a security id used to get access to the secure registry.
     * @param notificationType, a string specifying the type of notification,
     *   used to determine which listeners to notify. Not null.
     * @param notificationSubtype, a string specifying the subtype of the
     *   notification, used to determine which listeners to notify. Not null.
     *   If there is no subtype use NST_NONE.
     * @param notificationData, notification information specific to this
     *   notificationType. Listeners are expected to know how to interpret this
     *   data and perform appropriate processing. May be null. May be a
     *   DataObject if the notification server implementation sets a
     *   DataManager. Must be Serializable.
     * @return true if the notification was sent, false if there was an RMI
     *   error or other failure.
     */
    public static boolean sendNotification(SecurityId id, String notificationType,
        String notificationSubtype, Serializable notificationData)
    {
        try
        {
            // send notification
            Notifier notifier = (Notifier) LocalServerFactory.get(id,
                Notifier.class.getName());
            notifier.sendNotification(notificationType, notificationSubtype,
                notificationData);

            return true;
        }
        catch (Exception e)
        {
            Log.warning(WARNING_SENT_NOTIFICATION.getText(), e);
        }

        return false;
    }

    //--------------------------------------------------------------------------
    /**
     * As above, but uses the default system id for security verification.
     * Calling this function requires that the app has already inited its
     * Config, contexts and LocalServerfactory.
     */
    public static boolean sendNotification(String notificationType,
        String notificationSubtype, Serializable notificationData)
    {
        return sendNotification(SecurityId.getSystemId(), notificationType,
            notificationSubtype, notificationData);
    }
}

//==============================================================================
// end of file NotificationSender.java
//==============================================================================
