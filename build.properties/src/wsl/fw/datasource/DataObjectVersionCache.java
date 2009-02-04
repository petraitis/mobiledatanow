package wsl.fw.datasource;

// imports
import java.util.Hashtable;
import wsl.fw.util.Util;
import wsl.fw.util.Log;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c)
 * Company:
 * @author
 * @version 1.0
 */

public class DataObjectVersionCache extends WslDataCache
    implements DataChangeListener
{
    //--------------------------------------------------------------------------
    // attributes

    private boolean _doDataListen = true;


    //--------------------------------------------------------------------------
    // construction

    /**
     * Default ctor
     */
    public DataObjectVersionCache()
    {
    }

    /**
     * Default ctor
     */
    public DataObjectVersionCache(boolean doDataListen)
    {
        _doDataListen = doDataListen;
    }

    //--------------------------------------------------------------------------
    // get / set

    /**
     * Get a version from the cache
     * @param entityName
     * @param id
     * @return Interger
     */
    public synchronized Integer getObjectVersion(String entityName, Object key)
    {
        // validate
        Util.argCheckEmpty(entityName);
        Util.argCheckNull(key);

        // if the entity table doesnt exist, add a data listener
        if(_doDataListen && _entityTables.get(entityName) == null)
        {
            DataListenerData dld = new DataListenerData(this, entityName, null);
            DataManager.addDataChangeListener(dld);
        }

        // get the object
        String strKey = new String(key.toString());
        Integer ret = (Integer)getObject(entityName, strKey);

        // if null, create random
        if(ret == null)
        {
            // generate random
            ret = generateRandomVersion();

            // set into cache
            setObject(entityName, ret, strKey);
            Log.debug("VersionCache: " + entityName + ":" + strKey + " added; Version = " + ret);
        }

        // return
        return ret;
    }

    /**
     * Update the version for an object
     */
    protected synchronized void updateVersion(DataObject dobj, String entityName, Object key)
    {
        // is their a version?
        Hashtable et = (Hashtable)_entityTables.get(entityName);
        if(et != null)
        {
            // is their a version?
            String strKey = new String(key.toString());
            Integer oldVersion = (Integer)et.get(strKey);
            if(oldVersion != null)
            {
                // generate a new version
                Integer newVersion = generateRandomVersion();
                et.put(strKey, newVersion);
                Log.debug("VersionCache: " + entityName + ":" + key + " changed; Old version = " +
                    oldVersion + "; New version = " + newVersion);
            }
        }
    }

    /**
     * Generates a random integer
     * @return Integer
     */
    protected Integer generateRandomVersion()
    {
        double d = Math.random();
        int i = (int)(d * 1000000.0);
        return new Integer(i);
    }


    //--------------------------------------------------------------------------
    // Data Change

    /**
     * DataObject has changed
     */
    public synchronized void onDataChanged(DataChangeNotification not)
    {
        // validate
        Util.argCheckNull(not);

        // update?
        if(not.getChangeType() == not.UPDATE)
        {
            // get the entity and key
            String entityName = not.getEntityName();
            Object key = not.getDataObjectKey();

            // update the version
            updateVersion(not.getDataObject(), entityName, key);
        }
    }
}