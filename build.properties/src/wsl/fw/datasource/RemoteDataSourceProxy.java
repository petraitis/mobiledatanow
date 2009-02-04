/**	$Header: /wapsolutions/cvsroot/mdn/fw/src/wsl/fw/datasource/RemoteDataSourceProxy.java,v 1.1.1.1 2002/06/11 23:11:42 jonc Exp $
 *
 * Client side proxy for a remote data source. Uses the RemoteDataSource
 * RMI interface to call the RemoteDataSourceServant that it is conected with.
 *
 */
package wsl.fw.datasource;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.resource.ResId;

public class RemoteDataSourceProxy extends DataSourceImplBase
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/datasource/RemoteDataSourceProxy.java $ ";

    // resources
    public static final ResId DEBUG_CONSTRUCTOR            = new ResId("RemoteDataSourceProxy.debug.Constructor");
    public static final ResId EXCEPTION_REMOTE_DATA_SOURCE = new ResId("RemoteDataSourceProxy.exception.RemoteDataSource");
    public static final ResId DEBUG_ATTACHING              = new ResId("RemoteDataSourceProxy.debug.Attaching");
    public static final ResId RMI_ERROR                    = new ResId("RemoteDataSourceProxy.error.rmi");

    // Reference to RemoteDataSourceServant to which all calls are delegated.
    private RemoteDataSource _rdsDelegate;
    private DataSourceParam  _param = null;

    //--------------------------------------------------------------------------
    /**
     * Constructor, communicates with the RemoteDataManager to instantiate
     * and get a reference to the RemoteDataSource delegate.
     * @param dsName, the name of the datasource.
     * @throws RemoteException if the proxy cannot establish a connection
     *   with the remote data source.
     */
    public RemoteDataSourceProxy(String dsName)
    {
        // set name of data source locally
        setName(dsName);

        //Log.debug("RemoteDataSourceProxy constructor");
        Log.debug(DEBUG_CONSTRUCTOR.getText());
    }

    //--------------------------------------------------------------------------
    /**
     * Constructor, communicates with the RemoteDataManager to instantiate
     * and get a reference to the RemoteDataSource delegate.
     * @param param, the params used to create/locate the remote datasource.
     * @throws RemoteException if the proxy cannot establish a connection
     *   with the remote data source.
     */
    public RemoteDataSourceProxy(DataSourceParam param)
    {
        // set the param
        Util.argCheckNull(param);
        _param = param;

        // set name of data source locally
        setName(param._name);

        //Log.debug("RemoteDataSourceProxy constructor");
        Log.debug(DEBUG_CONSTRUCTOR.getText());
    }

    //--------------------------------------------------------------------------
    /**
     * Get the remote data source delegate, handle the deferred creation of the
     * delegate.
     */
    protected synchronized RemoteDataSource getDelegate() throws RemoteException
    {
        // if the delegate is not yet set (or has been discarded due to an
        // error) then get the parent DataManager to create one.
        if (_rdsDelegate == null)
        {
            // if this is a param DS then use the param construction else
            // use the name construction
            if (_param != null)
              _rdsDelegate = DataManager.getDataManager().createRemoteDataSource(_param);
            else
              _rdsDelegate = DataManager.getDataManager().createRemoteDataSource(getName());

            // if we can't get the delegate then throw a RemoteException which will
            // be caught by insert, update etc
            if (_rdsDelegate == null)
                //throw new RemoteException("could not get RemoteDataSource");
                throw new RemoteException(EXCEPTION_REMOTE_DATA_SOURCE.getText());
            else
                //Log.debug("RemoteDataSourceProxy attatching to remote delegate");
                Log.debug(DEBUG_ATTACHING.getText());
        }

        return _rdsDelegate;
    }

    //--------------------------------------------------------------------------
    /**
     * @see DataSource.insert
     * @param dobj
     * @return boolean true if the operation occurs
     * @throws DataSourceException if there is an error from the DataSource.
     */
    public boolean insert(DataObject dobj) throws DataSourceException
    {
        try
        {
            // note that the DataSource changes generated keys etc on insert
            // but RMI passes by value, hence the need for a return and update
            InsertReturn ir = getDelegate().insert(dobj);

            // update the values to that returned from the remote DS server
            // to ensure we have generated keys etc.
            dobj.importValues(ir._dobj);

            // note, this will need to be extended if the server alters more
            // than the values

            return ir._rv;
        }
        catch (RemoteException e)
        {
            // catch remote exceptions and re-throw as DataSourceExceptions.
            // if we get a RemoteException then assume the connection is lost,
            // so we clear the delegate so we get a new one next time
            _rdsDelegate = null;
            throw new DataSourceException(RMI_ERROR.getText() + e.toString());
        }
    }

    //--------------------------------------------------------------------------
    /**
     * @see DataSource.update
     * @param dobj
     * @return boolean true if the operation occurs
     * @throws DataSourceException if there is an error from the DataSource.
     */
    public boolean update(DataObject dobj) throws DataSourceException
    {
        try
        {
            // Note this will need to be changed if dobj is modified by the
            // server as RMI params are passed by value.
            return getDelegate().update(dobj);
        }
        catch (RemoteException e)
        {
            // catch remote exceptions and re-throw as DataSourceExceptions.
            // if we get a RemoteException then assume the connection is lost,
            // so we clear the delegate so we get a new one next time
            _rdsDelegate = null;
            throw new DataSourceException(RMI_ERROR.getText() + e.toString());
        }
    }

    //--------------------------------------------------------------------------
    /**
     * @see DataSource.delete
     * @param dobj
     * @return boolean true if the operation occurs
     * @throws DataSourceException if there is an error from the DataSource.
     */
    public boolean delete(DataObject dobj) throws DataSourceException
    {
        try
        {
            // Note this will need to be changed if dobj is modified by the
            // server as RMI params are passed by value.
            return getDelegate().delete(dobj);
        }
        catch (RemoteException e)
        {
            // catch remote exceptions and re-throw as DataSourceExceptions.
            // if we get a RemoteException then assume the connection is lost,
            // so we clear the delegate so we get a new one next time
            _rdsDelegate = null;
            throw new DataSourceException(RMI_ERROR.getText() + e.toString());
        }
    }

    //--------------------------------------------------------------------------
    /**
     * @see DataSource.select
     * @param query
     * @return RecordSet
     * @throws DataSourceException if there is an error from the DataSource.
     * @roseuid 3990BE41023A
     */
    public RecordSet select(Query query) throws DataSourceException
    {
        try
        {
            return getDelegate().select(query);
        }
        catch (RemoteException e)
        {
            // catch remote exceptions and re-throw as DataSourceExceptions.
            // if we get a RemoteException then assume the connection is lost,
            // so we clear the delegate so we get a new one next time
            _rdsDelegate = null;
            throw new DataSourceException(RMI_ERROR.getText() + e.toString());
        }
    }

    public RecordItrRef
	iSelect (
	 Query query)
	 	throws DataSourceException
    {
        try
        {
            return getDelegate().iSelect (query);

        } catch (RemoteException e)
        {
            // catch remote exceptions and re-throw as DataSourceExceptions.
            // if we get a RemoteException then assume the connection is lost,
            // so we clear the delegate so we get a new one next time
            _rdsDelegate = null;
            throw new DataSourceException (RMI_ERROR.getText() + e.toString());
        }
    }

    public boolean
	iHasNextObj (
	 RecordItrRef ref)
	 	throws DataSourceException
    {
        try
        {
            return getDelegate().iHasNextObj (ref);

        } catch (RemoteException e)
        {
            _rdsDelegate = null;		// forces a refetch
            throw new DataSourceException (RMI_ERROR.getText () + e.toString ());
        }
    }

    public Object
	iNextObj (
	 RecordItrRef ref)
	 	throws DataSourceException
    {
        try
        {
            return getDelegate().iNextObj (ref);

        } catch (RemoteException e)
        {
            _rdsDelegate = null;		// forces a refetch
            throw new DataSourceException (RMI_ERROR.getText () + e.toString ());
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Select a distinct set of values from a column
     * @param entityName
     * @param fieldName
     * @return Vector
     * @throws DataSourceException if there is an error from the DataSource.
     */
    public Vector selectDistinct(String entityName, String fieldName)
        throws DataSourceException
    {
        try
        {
            return getDelegate().selectDistinct(entityName, fieldName);
        }
        catch (RemoteException e)
        {
            // catch remote exceptions and re-throw as DataSourceExceptions.
            // if we get a RemoteException then assume the connection is lost,
            // so we clear the delegate so we get a new one next time
            _rdsDelegate = null;
            throw new DataSourceException(RMI_ERROR.getText() + e.toString());
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Generate a key from KeyGeneratorData
     * @param kgd the KeyGeneratorData
     * @return Object the key
     */
    public Object generateKey(KeyGeneratorData kgd) throws DataSourceException
    {
        try
        {
            return getDelegate().generateKey(kgd);
        }
        catch (RemoteException e)
        {
            // catch remote exceptions and re-throw as DataSourceExceptions.
            // if we get a RemoteException then assume the connection is lost,
            // so we clear the delegate so we get a new one next time
            _rdsDelegate = null;
            throw new DataSourceException(RMI_ERROR.getText() + e.toString());
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Excecute an insert, update, delete or DDL command from raw sql.
     * @param sql, the raw sql defining the update operation.
     * @return the number or records updates or zero for DDL commands.
     */
    public int rawExecuteUpdate(String sql)
        throws DataSourceException
    {
        try
        {
            return getDelegate().rawExecuteUpdate(sql);
        }
        catch (RemoteException e)
        {
            // catch remote exceptions and re-throw as DataSourceExceptions.
            // if we get a RemoteException then assume the connection is lost,
            // so we clear the delegate so we get a new one next time
            _rdsDelegate = null;
            throw new DataSourceException(RMI_ERROR.getText() + e.toString());
        }
    }

    //--------------------------------------------------------------------------
    /**
     * @see DataSource.close
     * @return void
     * @throws DataSourceException if there is an error from the DataSource.
     * @roseuid 3990BE410280
     */
    public void close() throws DataSourceException
    {
        synchronized (this)
        {
            if (_rdsDelegate != null)
                try
                {
                    _rdsDelegate.close();
                    // now that the datasource has been closed this proxy is no longer
                    // valid and remote delegate should have been releases, so null the
                    // delegate pointer
                    _rdsDelegate = null;
                }
                catch (RemoteException e)
                {
                    // catch remote exceptions and re-throw as DataSourceExceptions.
                    // if we get a RemoteException then assume the connection is lost,
                    // so we clear the delegate so we get a new one next time
                    _rdsDelegate = null;
                    throw new DataSourceException(RMI_ERROR.getText() + e.toString());
                }
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Create (in the database underlying the datasource) the table for this
     * entity.
     * @param ent, the entity whose table is to be created.
     * @param deleteFirst, if true the table will be deleted before the
     *   creation.
     */
    public void createEntityTable(Entity ent, boolean deleteFirst)
        throws DataSourceException
    {
        try
        {
            getDelegate().createEntityTable(ent, deleteFirst);
        }
        catch (RemoteException e)
        {
            // catch remote exceptions and re-throw as DataSourceExceptions.
            // if we get a RemoteException then assume the connection is lost,
            // so we clear the delegate so we get a new one next time
            _rdsDelegate = null;
            throw new DataSourceException(RMI_ERROR.getText() + e.toString());
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Import a the names of tables from the datasource.
     * @return a Vector of EntitySchemaNames containing the table names.
     */
    public Vector importTableNames() throws DataSourceException
    {
        try
        {
            return getDelegate().importTableNames();
        }
        catch (RemoteException e)
        {
            // catch remote exceptions and re-throw as DataSourceExceptions.
            // if we get a RemoteException then assume the connection is lost,
            // so we clear the delegate so we get a new one next time
            _rdsDelegate = null;
            throw new DataSourceException(RMI_ERROR.getText() + e.toString());
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Import an entity definition from the datasource.
     * @param esn, a EntitySchemaName defining the entity to import.
     * @return the imported entity definition.
     */
    public Entity importEntityDefinition(EntitySchemaName esn)
        throws DataSourceException
    {
        try
        {
            return getDelegate().importEntityDefinition(esn);
        }
        catch (RemoteException e)
        {
            // catch remote exceptions and re-throw as DataSourceExceptions.
            // if we get a RemoteException then assume the connection is lost,
            // so we clear the delegate so we get a new one next time
            _rdsDelegate = null;
            throw new DataSourceException(RMI_ERROR.getText() + e.toString());
        }
    }

    //--------------------------------------------------------------------------
    /**
     * @return an identifier that can be used to lookup this DataSource. This
     *   should be a DataSourceParam or a string name.
     */
    public Object getDsId()
    {
        if (_param != null)
            return _param;
        else
            return getName();
    }

    //--------------------------------------------------------------------------
    /**
     * Add an Entity to the Entity vector
     * @param entity the Entity to add
     */
    public void addEntity(Entity entity)
    {
        // call superclass to add locally
        super.addEntity(entity);

        // add remotely
        try
        {
            getDelegate().addEntity(entity);
        }
        catch (RemoteException e)
        {
            // catch remote exceptions and re-throw as DataSourceExceptions.
            // if we get a RemoteException then assume the connection is lost,
            // so we clear the delegate so we get a new one next time
            _rdsDelegate = null;
            Log.error("RemoteDataSourceProxy.addEntity: " + e.toString());
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Add a Join to the datasource.
     * @param join the Join to add.
     */
    public void addJoin(Join join)
    {
        // call superclass to add locally
        super.addJoin(join);

        // add remotely
        try
        {
            getDelegate().addJoin(join);
        }
        catch (RemoteException e)
        {
            // catch remote exceptions and re-throw as DataSourceExceptions.
            // if we get a RemoteException then assume the connection is lost,
            // so we clear the delegate so we get a new one next time
            _rdsDelegate = null;
            Log.error("RemoteDataSourceProxy.addJoin: " + e.toString());
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Exectues a direct SQL query.  The return value is a hashtable with keys
     * that match the query resultset's column names.  Each column (the
     * hashtable value) is a Vector that represents.
     */
    public Hashtable execDirectSQL(String sql) throws DataSourceException
    {
        try
        {
            return getDelegate().execDirectSQL(sql);
        }
        catch (RemoteException e)
        {
            // catch remote exceptions and re-throw as DataSourceExceptions.
            // if we get a RemoteException then assume the connection is lost,
            // so we clear the delegate so we get a new one next time
            _rdsDelegate = null;
            throw new DataSourceException(RMI_ERROR.getText() + e.toString());
        }
    }
    public int execInsertOrUpdate(String sql) throws DataSourceException
    {
        try
        {
            return getDelegate().execInsertOrUpdate(sql);
        }
        catch (RemoteException e)
        {
            // catch remote exceptions and re-throw as DataSourceExceptions.
            // if we get a RemoteException then assume the connection is lost,
            // so we clear the delegate so we get a new one next time
            _rdsDelegate = null;
            throw new DataSourceException(RMI_ERROR.getText() + e.toString());
        }
    }
    //--------------------------------------------------------------------------
    /**
     * Drop (in the database underlying the datasource) the table for this
     * entity.
     * @param entityName, the name of the entity whose table is to be created.
     * @throws DataSourceException if the creation failed, usually due to the
     *   table already existing
     */
    public void dropEntityTable(String entityName)
        throws DataSourceException
    {
        try
        {
            getDelegate().dropEntityTable(entityName);
        }
        catch (RemoteException e)
        {
            // catch remote exceptions and re-throw as DataSourceExceptions.
            // if we get a RemoteException then assume the connection is lost,
            // so we clear the delegate so we get a new one next time
            _rdsDelegate = null;
            throw new DataSourceException(RMI_ERROR.getText() + e.toString());
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Import an entity definition from a raw SQL select statement
     */
    public Entity importRawSelectDefinition(String selectStmt)
        throws DataSourceException
    {
        try
        {
            return getDelegate().importRawSelectDefinition(selectStmt);
        }
        catch (RemoteException e)
        {
            // catch remote exceptions and re-throw as DataSourceExceptions.
            // if we get a RemoteException then assume the connection is lost,
            // so we clear the delegate so we get a new one next time
            _rdsDelegate = null;
            throw new DataSourceException(RMI_ERROR.getText() + e.toString());
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Test that the data source can connect to the data base.
     * @throws DataSourceException if the connection cannot be made.
     */
    public void testConnection() throws DataSourceException
    {
        try
        {
            getDelegate().testConnection();
        }
        catch (RemoteException e)
        {
            // catch remote exceptions and re-throw as DataSourceExceptions.
            // if we get a RemoteException then assume the connection is lost,
            // so we clear the delegate so we get a new one next time
            _rdsDelegate = null;
            throw new DataSourceException(RMI_ERROR.getText() + e.toString());
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Inner class to hold return values from remoted insert.
     */
    public static class InsertReturn implements Serializable
    {
        public InsertReturn(DataObject dobj, boolean rv)
        {
            _dobj = dobj;
            _rv = rv;
        }
        public DataObject _dobj;
        public boolean    _rv;
    }
}