//==============================================================================
// DataListenerData.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.datasource;

// imports
import java.io.Serializable;

//------------------------------------------------------------------------------
/**
 * Node class containing information regarding what data change events that
 * DataChangeListeners wish to be notified of.
 */
public class DataListenerData extends Thread
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/datasource/DataListenerData.java $ ";

    // constants
    public static final int ALL = 0;
    public static final int ENTITY = 1;
    public static final int OBJECT = 2;
    public static final int CLASS = 3;

    // public data
    public DataChangeListener _listener = null;
    public int _level = -1;
    public String _entityName = "";
    public String _dobjKey = "";
    public Class _class = null;
    public Serializable _userTag = null;


    /**
     * Add a DataChangeListener. Calling this constructor will subscribe to change events on ALL DataObjects
     * @param the listener to add
     * @param userTag this object will be passed back to the subscriber in all notifications
     */
    public DataListenerData(DataChangeListener listener, Serializable userTag)
    {
        _level = ALL;
        _listener = listener;
        _userTag = userTag;
    }

    /**
     * Add a DataChangeListener. Calling this constructor will subscribe to change events on all DataObjects
     * of the specified entity type
     * @param the listener to add
     * @param entityName the name of entity that specifies the DataObjects that the subscriber wishes to listen to
     * @param userTag this object will be passed back to the subscriber in all notifications
     */
    public DataListenerData(DataChangeListener listener, String entityName, Serializable userTag)
    {
        _level = ENTITY;
        _listener = listener;
        _entityName = entityName;
        _userTag = userTag;
    }

    /**
     * Add a DataChangeListener. Calling this constructor will subscribe to change events on all DataObjects
     * of the specified entity type
     * @param the listener to add
     * @param c the Class that specifies the DataObjects that the subscriber wishes to listen to
     * @param userTag this object will be passed back to the subscriber in all notifications
     */
    public DataListenerData(DataChangeListener listener, Class c, Serializable userTag)
    {
        _level = CLASS;
        _listener = listener;
        _class = c;
        _userTag = userTag;
    }

    /**
     * Add a DataChangeListener. Calling this constructor will subscribe to change events on the DataObject specified
     * @param the listener to add
     * @param entityName the name of entity that specifies the DataObjects that the subscriber wishes to listen to
     * @param userTag this object will be passed back to the subscriber in all notifications
     */
    public DataListenerData(DataChangeListener listener, DataObject dobj, Serializable userTag)
    {
        _level = OBJECT;
        _listener = listener;
        _dobjKey = (dobj == null)? "": dobj.getUniqueKey(true);
        _userTag = userTag;
    }

    /**
     * Return true if the param object is equal to this. Used by Vector for removal.
     * Compares attributes, uses the DataObject key for comparison. Does not compare object tag
     * @param obj the Object to compare to this
     * @return boolean true if the 2 objects are equal (in content, not reference)
     */
    public boolean equals(Object obj)
    {
        // validate
        if(obj == null || !(obj instanceof DataListenerData))
            return false;

        // listener
        DataListenerData data = (DataListenerData)obj;
        if(!(_listener == null && data._listener == null))
        {
            if(_listener == null || data._listener == null)
                return false;
            else if(!_listener.equals(data._listener))
                return false;
        }

        // level
        if(_level != data._level)
            return false;

         // entity name
        if(!_entityName.equals(data._entityName))
            return false;

        // DataObject
        if(!_dobjKey.equals(data._dobjKey))
            return false;

        // Class
        if(!_class.getName().equals(data._class.getName()))
            return false;

        // must be valid
        return true;
    }

    /**
     * Inner class used to run a notification to a listener
     */
    class NotificationRunner implements Runnable
    {
        // attributes
        private DataChangeNotification _dcn;
        private DataChangeListener _listener;

        /**
         * Ctor taking a notification and a listener
         * @param n the DataChangeNotification
         */
        public NotificationRunner(DataChangeListener listener, DataChangeNotification dcn)
        {
            _dcn = dcn;
            _listener = listener;
        }

        /**
         * Runnable override. Run in response to a call to notifyListener().
         * Calls onDataChanged() on the listener
         */
        public void run()
        {
            // notify the listener
            if(_listener != null)
                _listener.onDataChanged(_dcn);
        }
    }

    /**
     * Notify the listener of a DataObject change. Notification is made in its own thread
     * @param DataChangeNotification contains the data regarding data change
     */
    public synchronized void notifyListener(DataChangeNotification notification)
    {
        // validate
        if(canNotify(notification))
        {
            // set the notify data
            notification.setUserTag(_userTag);

            // create and run thread
            Thread t = new Thread(new NotificationRunner(_listener, notification));
            t.start();
        }
    }

    /**
     * @return true if the listener wants to be notified of the param event
     * @param DataChangeNotification contains the data regarding data change
     */
    public boolean canNotify(DataChangeNotification notification)
    {
        // validate
//        if(notification == null || notification.getDataObjectKey().length() == 0 || _listener == null)
        if(notification == null || _listener == null)
            return false;

        // switch on level
        if(_level == ALL)
            return true;
        else if (_level == ENTITY)
            return _entityName.equals(notification.getEntityName());
        else if (_level == CLASS)
            return _class.equals(notification.getObjectClass());
        else if (_level == OBJECT)
        {
            if (notification.getDataObjectKey().length() == 0)
                return false;

            return _dobjKey.equals(notification.getDataObjectKey());
        }
        else
            return false;
    }
}

//==============================================================================
// end of file DataListenerData.java
//==============================================================================
