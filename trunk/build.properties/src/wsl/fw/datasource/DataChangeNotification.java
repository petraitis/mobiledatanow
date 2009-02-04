//==============================================================================
// DataChangeNotification.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.datasource;

import java.io.Serializable;

//------------------------------------------------------------------------------
/**
 * Notification data passed to DataChangeListeners.
 */
public class DataChangeNotification implements Serializable
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/datasource/DataChangeNotification.java $ ";

    /**
     * DataObject has been inserted
     */
    public static final int INSERT = 0;

    /**
     * DataObject has been updated
     */
    public static final int UPDATE = 1;

    /**
     * DataObject has been deleted
     */
    public static final int DELETE = 2;

    // attributes
    private DataObject   _dobj = null;
    private String       _entityName = "";
    private String       _dobjKey = "";
    private Class        _class = null;
    private int          _changeType;
    private Serializable _userTag;
    private boolean      _fromRemoteNotifier = false;

    /**
     * Constructor taking params
     * @param dobj DataObject that has changed. null for DELETE.
     * @param entityName entity name of the DataObject that has changed.
     * @param c class of the DataObject that has changed.
     * @param dobj composite uinque key of the DataObject that has changed.
     *   For update and delete it is the key of the original object before
     *   changes. For insert is ths the key of the new object.
     * @param changeType tyep of change (INSERT, UPDATE or DELETE)
     */
    public DataChangeNotification(DataObject dobj, String entityName, Class c,
        String dobjKey, int changeType)
    {
        // set attribs
        _dobj = dobj;
        _entityName = entityName;
        _class = c;
        _dobjKey = dobjKey;
        _changeType = changeType;
    }

    /**
     * Set the user tage into the notification.
     * @param userTag the tag to set, may be null, must be Serializable
     */
    public void setUserTag(Serializable userTag)
    {
        _userTag = userTag;
    }

    /**
     * @return DataObject the DataObject in the notification
     */
    public DataObject getDataObject()
    {
        return _dobj;
    }

    /**
     * @return String the Entity name of the DataObject
     */
    public String getEntityName()
    {
        return _entityName;
    }

    /**
     * @return Class the class of the DataObject
     */
    public Class getObjectClass()
    {
        return _class;
    }

    /**
     * @return String the DataObject composite unique key
     */
    public String getDataObjectKey()
    {
        return _dobjKey;
    }

    /**
     * @return int the type of change
     */
    public int getChangeType()
    {
        return _changeType;
    }

    /**
     * @return the user tag in the notification
     */
    public Serializable getUserTag()
    {
        return _userTag;
    }

    /**
     * @return String a string describing this object
     */
    public String toString()
    {
        // build the string
        String ret = "DataChangeNotification: DataObject = " + _dobj + "; Entity name = " + _entityName + "; Key = " + _dobjKey + "; Change type = ";
        switch(_changeType)
        {
            case INSERT: ret += "INSERT"; break;
            case UPDATE: ret += "UPDATE"; break;
            case DELETE: ret += "DELETE"; break;
            default: ret += "UNKNOWN"; break;
        }
        return ret;
    }

    //--------------------------------------------------------------------------
    /**
     * Set the fromRemoteNotifier state. This should only be used by data
     * manager when processing remote notifications.
     */
    void setFromRemoteNotifier(boolean isRemote)
    {
        _fromRemoteNotifier = isRemote;
    }

    //--------------------------------------------------------------------------
    /**
     * @return true if this notification came from the remote notifier.
     */
    public boolean isFromRemoteNotifier()
    {
        return _fromRemoteNotifier;
    }
}

//==============================================================================
// end of file DataChangeNotification.java
//==============================================================================
