//==============================================================================
// NotifierServant.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.notification;

import wsl.fw.remote.RmiServantBase;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.io.Serializable;

//------------------------------------------------------------------------------
/**
 * Implementation of the Notifier remote interface.
 * Handles the registration of listeners and propogation of generic remote
 * notifications.
 */
public class NotifierServant extends RmiServantBase implements Notifier
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/notification/NotifierServant.java $ ";

    private HashMap _notificationTypes = new HashMap();

    //--------------------------------------------------------------------------
    /**
     * Default constructor
     */
    public NotifierServant()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Send a notification to the Notifier which will be propogated to all
     * listeners that have registered interest in the notificationType and
     * subtype. NT_ALL and NST_ALL may not be used
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
     * @throws RemoteException if there is an RMI error.
     */
    public void sendNotification(String notificationType,
        String notificationSubtype, Serializable notificationData)
        throws RemoteException
    {
        Util.argCheckNull(notificationType);

        Log.debug("NotifierServant.sendNotification: " + notificationType
            + ", " + notificationSubtype);

        // get the set of listeners for this notificationType and subtype as
        // well as the NT_ALL and matching NST_ALL listeners
        HashSet combinedListeners = new HashSet();
        HashSet listenerAll;
        HashMap subTypes;
        synchronized (_notificationTypes)
        {
            // get NT_ALL
            listenerAll = (HashSet) _notificationTypes.get(NT_ALL);
            // get subtype map
            subTypes = (HashMap) _notificationTypes.get(notificationType);
        }

        // if members in the NT_ALL set then lock and add them
        if (listenerAll != null)
            synchronized (listenerAll)
            {
                combinedListeners.addAll(listenerAll);
            }

        // if type matches get the subtypes and NST_ALL
        if (subTypes != null)
        {
            HashSet listenerSubtypeAll;
            HashSet listenerSubtype;

            synchronized (subTypes)
            {
                // get NST_ALL
                listenerSubtypeAll = (HashSet) subTypes.get(NST_ALL);
                // get subtype set
                listenerSubtype = (HashSet) subTypes.get(notificationSubtype);
            }

            // if members in the NTS_ALL set then lock and add them
            if (listenerSubtypeAll != null)
                synchronized (listenerSubtypeAll)
                {
                    combinedListeners.addAll(listenerSubtypeAll);
                }

            // if members in the subtype set then lock and add them
            if (listenerSubtype != null)
                synchronized (listenerSubtype)
                {
                    combinedListeners.addAll(listenerSubtype);
                }
        }

        // iterate over the listeners in the combined set
        Iterator iter = combinedListeners.iterator();
        while (iter.hasNext())
        {
            // get a listener and start a thread to send it the
            // notification
            NotificationListener listener = (NotificationListener) iter.next();
            NotifierThread nt = new NotifierThread(notificationType,
                notificationSubtype, notificationData, listener, this);
            nt.start();
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Add a new NotificationListener for the specified notification type.
     * It is ok (and advisable as unresponsive listeners are culled) to call
     * this function more than once for the same listener. It is ok for the same
     * listener to listen to multiple notification types.
     * @param listener, the NotificationListener that will be called whenever
     *   a notification event of the specified type occurs. Not null.
     * @param notificationType, a String specifying the type of notification to
     *   listen for. Not null. If NT_ALL then subtype is ignored and all
     *   notifications are listened to.
     * @param notificationSubtype, a string specifying the subtype of the
     *   notification, used to determine which listeners to notify. Not null.
     *   If there is no subtype use NST_NONE. If NST_ALL then all notification
     *   subtypes are listened to.
     * @throws RemoteException if there is an RMI error.
     */
    public void addNotificationListener(NotificationListener listener,
        String notificationType, String notificationSubtype)
        throws RemoteException
    {
        Util.argCheckNull(listener);
        Util.argCheckNull(notificationType);

        Log.debug("NotifierServant.addNotificationListener: " + notificationType
            + ", " + notificationSubtype);

        HashSet listenerSet;
        // lock the top level notification types
        synchronized (_notificationTypes)
        {
            // get the set of listensers for this notification type
            if (notificationType.equals(NT_ALL))
            {
                // if NT_ALL the listener set is at the top level
                listenerSet = (HashSet) _notificationTypes.get(NT_ALL);
                if (listenerSet == null)
                {
                    // if NT_ALL does not exist then add it
                    listenerSet = new HashSet();
                    _notificationTypes.put(NT_ALL, listenerSet);
                }
            }
            else
            {
                // not NT_ALL, ge the subtypes
                HashMap subTypes = (HashMap) _notificationTypes.get(notificationType);
                if (subTypes == null)
                {
                    // subtype does not exist, create it
                    subTypes = new HashMap();
                    _notificationTypes.put(notificationType, subTypes);
                }

                // lock the subtypes
                synchronized (subTypes)
                {
                    // get the listeners for the specified subtype
                    listenerSet = (HashSet) subTypes.get(notificationSubtype);
                    if (listenerSet == null)
                    {
                        // if this subtype does not exist then add it
                        listenerSet = new HashSet();
                        subTypes.put(notificationSubtype, listenerSet);
                    }
                }
            }
        }

        // add the listener to the set
        synchronized (listenerSet)
        {
            listenerSet.add(listener);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Remove the NotificationListener so it no longer receives notifications
     * of the specified type. Should be called before exit by any program that
     * has added notification listeners to remove those listeners.
     * @param listener, the NotificationListener to remove. Not null.
     * @param notificationType, a String specifying the type of notification
     *   that will no longer be listened for. If NT_ALL subtype is ignored and
     *   this listener is removed  for all notification types.
     * @param notificationSubtype, a string specifying the subtype of the
     *   notification, that will not longer be listened to. Not null.
     *   If there is no subtype use NST_NONE. If NST_ALL then all notification
     *   subtypes are removed.
     * @throws RemoteException if there is an RMI error.
     */
    public void removeNotificationListener(NotificationListener listener,
        String notificationType, String notificationSubtype)
        throws RemoteException
    {
        Log.debug("NotifierServant.removeNotificationListener: "
            + notificationType + ", " + notificationSubtype);

        if (notificationType.equals(NT_ALL))
        {
            // NT_ALL, remove from NT_ALL then iterate removing from all types
            String types[];
            HashSet listenerAll;
            synchronized (_notificationTypes)
            {
                // get the types
                types = (String[]) _notificationTypes.keySet().toArray(new String[0]);

                // get the NT_ALL set
                listenerAll = (HashSet) _notificationTypes.get(NT_ALL);
            }

            // remove from the all set
            if (listenerAll != null)
                synchronized (listenerAll)
                {
                    listenerAll.remove(listener);
                }

            // recursively call remove for each type except NT_ALL
            for (int i = 0; i < types.length; i++)
                if (!types[i].equals(NT_ALL))
                    removeNotificationListener(listener, types[i], NST_ALL);
        }
        else
        {
            // not NT_ALL
            // get the subtypes for this notificationType
            HashMap subTypes;
            synchronized (_notificationTypes)
            {
                subTypes = (HashMap) _notificationTypes.get(notificationType);
            }

            // if we have subtypes
            if (subTypes != null)
                if (notificationSubtype.equals(NST_ALL))
                {
                    // NST_ALL, recurse removing listener from all subtypes
                    String  subTypeNames[];
                    HashSet listenerSubtypeAll;
                    synchronized (subTypes)
                    {
                        // get the subtypes
                        subTypeNames = (String[]) subTypes.keySet().toArray(new String[0]);

                        // get the NST_ALL set
                        listenerSubtypeAll = (HashSet) subTypes.get(NST_ALL);
                    }

                    // remove from the subtype all set
                    if (listenerSubtypeAll != null)
                        synchronized (listenerSubtypeAll)
                        {
                            listenerSubtypeAll.remove(listener);
                        }

                    // recursively call remove for each type except NTS_ALL
                    for (int i = 0; i < subTypeNames.length; i++)
                        if (!subTypeNames[i].equals(NST_ALL))
                            removeNotificationListener(listener, notificationType, subTypeNames[i]);
                }
                else
                {
                    // not NST_ALL, loch subtypes
                    HashSet listeners;
                    synchronized (subTypes)
                    {
                        // get the subtype
                        listeners = (HashSet) subTypes.get(notificationSubtype);
                    }

                    // if we have found the subtype then remove the listener
                    if (listeners != null)
                        synchronized (listeners)
                        {
                            listeners.remove(listener);
                        }
                }
        }
    }
}

//==============================================================================
// end of file NotifierServant.java
//==============================================================================
