package wsl.fw.datasource;

// imports
import java.util.Hashtable;
import java.util.Enumeration;
import wsl.fw.util.Util;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:
 * @author
 * @version 1.0
 */

public class WslDataCache
{
    //--------------------------------------------------------------------------
    // attributes

    protected Hashtable _entityTables = new Hashtable(89);


    //--------------------------------------------------------------------------
    // construction

    public WslDataCache()
    {
    }


    //--------------------------------------------------------------------------
    // get and set object

    /**
     * Get an object from the cache
     * @param entityName
     * @param id
     * @return Object
     */
    public synchronized Object getObject(String entityName, Object key)
    {
        // validate
        Util.argCheckEmpty(entityName);
        Util.argCheckNull(key);

        // get the entity table
        Object ret = null;
        Hashtable et = (Hashtable)_entityTables.get(entityName);

        // get the object from the table
        if(et != null)
            ret = et.get(key.toString());

        // return
        return ret;
    }

    /**
     * Set an object into the cache mapped to a value
     * @param obj the Object to set
     */
    public synchronized void setObject(String entityName, Object obj, Object key)
    {
        // validate
        Util.argCheckNull(obj);
        Util.argCheckNull(key);

        // get the entity table
        Hashtable et = (Hashtable)_entityTables.get(entityName);

        // if null, create
        if(et == null)
        {
            et = new Hashtable(89);
            _entityTables.put(entityName, et);
        }

        // set the obj
        et.put(key.toString(), obj);
    }

    /**
     * @return Object the first object in an entity table
     * @param entityName
     */
    public synchronized Object getFirstObject(String entityName)
    {
        // validate
        Util.argCheckEmpty(entityName);

        // get the et
        Hashtable et = (Hashtable)_entityTables.get(entityName);
        if(et != null)
        {
            // get the first element
            Enumeration enums = et.elements();
            return (enums != null && enums.hasMoreElements())? enums.nextElement(): null;
        }
        else
            return null;
    }

    /**
     * @return Enumeration an enumeration on an entity table, null if not found
     * @param entityName
     */
    public synchronized Enumeration getEntityTable(String entityName)
    {
        Hashtable et = (Hashtable)_entityTables.get(entityName);
        return (et != null)? et.elements(): null;
    }


    //--------------------------------------------------------------------------
    // clear

    /**
     * Clear the whole cache
     */
    public synchronized void clearCache()
    {
        _entityTables.clear();
    }

    /**
     * Clear a single entity
     * @param entityName
     */
    public synchronized void clearCache(String entityName)
    {
        // if the table exists clear it
        Hashtable et = (Hashtable)_entityTables.get(entityName);
        if(et != null)
            et.clear();
    }
}