/*	$Header: /wapsolutions/cvsroot/mdn/mdn/src/wsl/mdn/dataview/MdnDataCache.java,v 1.1.1.1 2002/06/11 23:35:35 jonc Exp $
 */
package wsl.mdn.dataview;

// imports
import java.util.Hashtable;
import java.util.Vector;
import wsl.fw.datasource.*;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.mdn.dataview.*;
import wsl.fw.msgserver.MessageServer;
import wsl.fw.msgserver.NullMessageServer;

/**
 * Title:
 * Description: Cache for DataViews, QueryDobjs, DataSourceDobjs
 * Copyright:    Copyright (c)
 * Company:
 * @author
 * @version 1.0
 */
public class MdnDataCache extends WslDataCache
{
    //--------------------------------------------------------------------------
    // constants

    private boolean _doUseCache = true;


    //--------------------------------------------------------------------------
    // attributes

    private static MdnDataCache s_dc = null;
    protected DataObjectVersionCache _versionCache = new DataObjectVersionCache(false);


    //--------------------------------------------------------------------------
    // statics

    /**
     * @return MdnDataCache the cache
     */
    public static MdnDataCache getCache()
    {
        // return
        return s_dc;
    }

    /**
     * Set the cache
     * @param cache the cache to set
     */
    public static void setCache(MdnDataCache cache)
    {
        s_dc = cache;
    }


    //--------------------------------------------------------------------------
    // construction

    /**
     * Ctor taking args
     * @param doUseCache
     */
    public MdnDataCache(boolean doUseCache)
    {
        _doUseCache = doUseCache;
    }


    //--------------------------------------------------------------------------
    // accessors

    /**
     * @return boolean doUseCache
     */
    protected boolean doUseCache()
    {
        return _doUseCache;
    }


    //--------------------------------------------------------------------------
    // DataView

    /**
     * Get a DataView from the cache, if not present then query the db and load
     * it.
     * @param dvId, the id of the DataView to get.
     * @return the DataView, or null if not found.
     */
    public synchronized DataView getDataView(Object dvId) throws DataSourceException
    {
        Util.argCheckNull(dvId);

        // look in cache
        DataView dv = (DataView)getObject(DataView.ENT_DATAVIEW, dvId);

        // if expired, load
        if(isObjectExpired(dv, DataView.ENT_DATAVIEW, dvId))
        {
            // not found, load from DB
            DataView dvTmp = new DataView();
            dvTmp.setId(dvId);

            try
            {
                if (null != (dvTmp = (DataView)dvTmp.loadPolymorphic()))
                {
                    // loaded, set return value and store in cache
                    dv = dvTmp;
                    if(_doUseCache)
                        setObject(DataView.ENT_DATAVIEW, dv, dvId);
                }
            }
            catch (DataSourceException e)
            {
                Log.error("DataViewCache.getDataView", e);
            }
        }

        return dv;
    }


    //--------------------------------------------------------------------------
    // QueryDobj

    /**
     * Get a QueryDobj from the cache, if not present then query the db and
     * load it.
     * @param qdId, the id of the QueryDobj to get.
     * @return the QueryDobj, or null if not found.
     */
    public synchronized QueryDobj getQueryDobj(Object qdId) throws DataSourceException
    {
        Util.argCheckNull(qdId);

        // look in cache
        QueryDobj qd = (QueryDobj)getObject(QueryDobj.ENT_QUERY, qdId);

        if(isObjectExpired(qd, QueryDobj.ENT_QUERY, qdId))
        {
            // not found, load from DB
            QueryDobj qdTmp = new QueryDobj();
            qdTmp.setId(qdId);

            try
            {
                if (null != (qdTmp = (QueryDobj)qdTmp.loadPolymorphic()))
                {
                    // loaded, set return value and store in cache
                    qd = qdTmp;
                    if(_doUseCache)
                        setObject(QueryDobj.ENT_QUERY, qd, qdId);
                }
            }
            catch (DataSourceException e)
            {
                Log.error("DataViewCache.getQueryDobj", e);
            }
        }

        return qd;
    }

    /**
     * Find the DataView that parents a QueryDobj.
     * @param qdId, the Id of the QueryDobj whose parent DataView we must find.
     * @return the DataView that parents the QueryDobj with the specified Id.
     */
    public synchronized DataView getQueryDataView(Object qdId) throws DataSourceException
    {
        DataView  dv = null;
        QueryDobj qd = getQueryDobj(qdId);
        if (qd != null)
            dv = getDataView(new Integer(qd.getViewOrTableId()));

        return dv;
    }


    //--------------------------------------------------------------------------
    // DataSourceDobj

    /**
     * Get a DataSourceDobj from the cache, if not present then query the db and load
     * it.
     * @param dsId, the id of the DataSourceDobj to get.
     * @return the DataSourceDobj, or null if not found.
     */
    public synchronized DataSourceDobj getDataSourceDobj(Object dsId)
        throws DataSourceException
    {
        return getDataSourceDobj(dsId, false);
    }

    /**
     * Get a DataSourceDobj from the cache, if not present then query the db and load
     * it.
     * @param dsId, the id of the DataSourceDobj to get.
     * @return the DataSourceDobj, or null if not found.
     */
    public synchronized DataSourceDobj getDataSourceDobj(Object dsId, boolean doRefresh)
        throws DataSourceException
    {
        Util.argCheckNull(dsId);

        // look in cache
        DataSourceDobj ds = (DataSourceDobj)getObject(DataSourceDobj.ENT_DATASOURCE, dsId);

        if(isObjectExpired(ds, DataSourceDobj.ENT_DATASOURCE, dsId))
        {
            // not found, load from DB
            JdbcDataSourceDobj dsTmp = new JdbcDataSourceDobj();
            dsTmp.setId(Integer.parseInt(dsId.toString()));

            try
            {
                ds = (DataSourceDobj)dsTmp.loadPolymorphic();
                if(ds != null && _doUseCache)
                {
                    // loaded, set return value and store in cache
                    setObject(DataSourceDobj.ENT_DATASOURCE, ds, dsId);
                }
            }
            catch (DataSourceException e)
            {
                Log.error("MdnDataCache.getDataSourceDobj", e);
            }
        }
        return ds;
    }


    //--------------------------------------------------------------------------
    // DataSource

    /**
     * Get a DataSource from a DataSource id
     * @param dsid the id of the datasource
     * @return DataSource the datasource
     */
    public synchronized DataSource getDataSource(int dsid) throws DataSourceException
    {
        // delegate to singleton
        return getDataSource(dsid, false);
    }

    /**
     * Get a DataSource from a DataSource id
     * @param dsid the id of the datasource
     * @return DataSOurce the datasource
     */
    public synchronized DataSource getDataSource(int dsId, boolean doRefresh) throws DataSourceException
    {
        // validate
        if(dsId < 0)
            throw new RuntimeException("Bad DataSource ID");

        // get the dobj
        DataSourceDobj dsDobj = getDataSourceDobj(new Integer(dsId), doRefresh);
        if(dsDobj == null)
            throw new RuntimeException("Bad DataSource ID");

        // get from map
        DataSource ds = (DataSource)dsDobj.createImpl();

        // if it is mirrored set the mirror ds
        if(ds != null && dsDobj.isMirrored())
        {
            // get the mirror ds
            int mirrorId = dsDobj.getMirrorId();
            DataSourceDobj mirrorDobj = getDataSourceDobj(new Integer(mirrorId));
            if(mirrorDobj == null)
                throw new RuntimeException("Bad Mirror DataSource ID");
            DataSource mirrorDs = mirrorDobj.createImpl();

            // set new entities
            for(int i = 0; ds.getEntities() != null && i < ds.getEntities().size(); i++)
                mirrorDs.addEntity((Entity)ds.getEntities().elementAt(i));

            // set joins
            for(int i = 0; ds.getJoins() != null && i < ds.getJoins().size(); i++)
                mirrorDs.addJoin((Join)ds.getJoins().elementAt(i));

            // set the mirror as the ds
            ds = mirrorDs;
        }

        // return
        return ds;
    }


    //--------------------------------------------------------------------------
    // Message Server

    /**
     * Get a MessageServer from the cache, if not present then query the db and load
     * it.
     * @param msId, the id of the MessageServer to get.
     * @return the MessageServer, or null if not found.
     */
    public synchronized MessageServer
	getMessageServer (
	 Object msId)
	 	throws DataSourceException
    {
        // look in cache
        MessageServer ms = (MessageServer)getObject(MessageServer.ENT_MSGSERVER, msId);

        // if expired, load
        if(isObjectExpired(ms, MessageServer.ENT_MSGSERVER, msId))
        {
            /*
			 *	Not found, load from DB
			 */
            MessageServer msTmp = new NullMessageServer ();
            msTmp.setId(msId);

            try
            {
                if (null != (msTmp = (MessageServer)msTmp.loadPolymorphic()))
                {
                    // loaded, set return value and store in cache
                    ms = msTmp;
                    setObject(MessageServer.ENT_MSGSERVER, ms, msId);
                }
            }
            catch (DataSourceException e)
            {
                Log.error("DataViewCache.getMessageServer", e);
            }
        }

        return ms;
    }


    //--------------------------------------------------------------------------
    // remote

    /**
     * @return boolean true if an object has expired in the remote server
     */
    protected synchronized boolean isObjectExpired(Object obj, String entityName, Object key)
        throws DataSourceException
    {
        // if not using the cache, always expired
        if(!doUseCache())
            return true;

        // get the remote dm
        DataManager dm = DataManager.getDataManager();
        if(dm.isDataManagerRemote())
        {
            try
            {
                RemoteDataManager rdm = dm.getRemoteDataManager();

                // get the remote version of the object
                Integer remoteVersion = rdm.getObjectVersion(entityName, key);

                // get the local version of the object
                Integer localVersion = (Integer)_versionCache.getObjectVersion(
                    entityName, key);

                // not equal
                if(obj == null || remoteVersion.intValue() != localVersion.intValue())
                {
                    // output
                    Log.debug("Updating local version " + entityName + ":" + key +
                    "; old = " + localVersion + "; new = " + remoteVersion);

                    // set the new local version
                    _versionCache.setObject(entityName, remoteVersion, key);

                    // is expired
                    return true;
                }

                // equal
                else
                {
                    // not expired
                    return false;
                }
            }
            catch(Exception e)
            {
                throw new DataSourceException(e.toString());
            }
        }
        return (obj != null);
    }

    /**
     * Update the local cache with the latest remote version
     * @param entityName
     * @param key
     */
    public synchronized void updateLocalVersion(String entityName, Object key)
        throws DataSourceException
    {
        // validate
        Util.argCheckEmpty(entityName);
        Util.argCheckNull(key);

        // if not using the cache, do nohting
        if(!doUseCache())
            return;

        // get the remote dm
        DataManager dm = DataManager.getDataManager();
        if(dm.isDataManagerRemote())
        {
            try
            {
                RemoteDataManager rdm = dm.getRemoteDataManager();

                // get the remote version of the object
                Integer remoteVersion = rdm.getObjectVersion(entityName, key);

                // get the local version of the object
                Integer localVersion = (Integer)_versionCache.getObjectVersion(
                    entityName, key);

                // not equal
                if(remoteVersion.intValue() != localVersion.intValue())
                {
                    // output
                    Log.debug("Updating local version " + entityName + ":" + key +
                    "; old = " + localVersion + "; new = " + remoteVersion);

                    // set the new local version
                    _versionCache.setObject(entityName, remoteVersion, key);
                }
            }
            catch(Exception e)
            {
                throw new DataSourceException(e.toString());
            }
        }
    }
}