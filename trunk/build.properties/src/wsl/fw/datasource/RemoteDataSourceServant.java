/**	$Header: /wapsolutions/cvsroot/mdn/fw/src/wsl/fw/datasource/RemoteDataSourceServant.java,v 1.1.1.1 2002/06/11 23:11:42 jonc Exp $
 *
 * Servant for remote data sources. Not intended to be generally advertised by
 * the secure registry, it is created by the RemoteDataManager and freed when
 * the proxy which uses it is closed.
 *
 */
package wsl.fw.datasource;

import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import wsl.fw.util.Util;
import wsl.fw.remote.RmiServantBase;
import wsl.fw.remote.UnexportThread;

public class RemoteDataSourceServant
    extends RmiServantBase implements RemoteDataSource
{
    // version tag
    private final static String _ident = "$Header: /wapsolutions/cvsroot/mdn/fw/src/wsl/fw/datasource/RemoteDataSourceServant.java,v 1.1.1.1 2002/06/11 23:11:42 jonc Exp $";

    // the local data source to which all calls from the remote client are
    // delegated
    DataSource _ldsDelegate;


    //--------------------------------------------------------------------------
    /**
     * Constructor, set the delegate data source.
     * @param dataSource, the local data source to which calls are delegated.
     */
    public RemoteDataSourceServant(DataSource dataSource)
    {
        Util.argCheckNull(dataSource);
        _ldsDelegate = dataSource;
    }

    //--------------------------------------------------------------------------
    /**
     * @see DataSource.insert
     * @param dobj
     * @return InsertReturn, a holder for the standard Datasource retval and
     *   the remote server's copy of the modified Dataobject. Needed as
     *   JdbcDataSource sets key values etc on insert.
     * @throws DataSourceException if there is an error from the DataSource.
     * @throws RemoteException if there is an RMI error.
     */
    public RemoteDataSourceProxy.InsertReturn insert(DataObject dobj)
        throws DataSourceException, RemoteException
    {
        // notify listeners
        DataManager.notifyListeners(new DataChangeNotification(dobj,
            dobj.getEntityName(), dobj.getClass(), dobj.getUniqueKey(),
            DataChangeNotification.INSERT));

        // package up the retval and the modified dataobject and return them, as
        // RMI params are passed by value and the DataSource changes the dobj
        // (notably to set generated keys) on insert
        boolean rv = _ldsDelegate.insert(dobj);
        return new RemoteDataSourceProxy.InsertReturn(dobj, rv);
    }

    //--------------------------------------------------------------------------
    /**
     * @see DataSource.update
     * @param dobj
     * @return boolean true if the operation occurs
     * @throws DataSourceException if there is an error from the DataSource.
     * @throws RemoteException if there is an RMI error.
     */
    public boolean update(DataObject dobj)
        throws DataSourceException, RemoteException
    {
        // update
        if(_ldsDelegate.update(dobj))
        {
            // notify listeners
            DataManager.notifyListeners(new DataChangeNotification(dobj,
                dobj.getEntityName(), dobj.getClass(), dobj.getUniqueKey(),
                DataChangeNotification.UPDATE));

            // Note this will need to be changed if dobj is modified by the
            // server as RMI params are passed by value.
            return true;
        }
        else
            return false;
    }


    //--------------------------------------------------------------------------
    /**
     * @see DataSource.delete
     * @param dobj
     * @return boolean true if the operation occurs
     * @throws DataSourceException if there is an error from the DataSource.
     * @throws RemoteException if there is an RMI error.
     */
    public boolean delete(DataObject dobj)
        throws DataSourceException, RemoteException
    {
        // notify listeners
        DataManager.notifyListeners(new DataChangeNotification(dobj,
            dobj.getEntityName(), dobj.getClass(), dobj.getUniqueKey(),
            DataChangeNotification.DELETE));

        // Note this will need to be changed if dobj is modified by the
        // server as RMI params are passed by value.
        return _ldsDelegate.delete(dobj);
    }

    //--------------------------------------------------------------------------
    /**
     * @see DataSource.select
     * @param query
     * @return RecordSet
     * @throws DataSourceException if there is an error from the DataSource.
     * @throws RemoteException if there is an RMI error.
     */
    public RecordSet select(Query query)
        throws DataSourceException, RemoteException
    {
        return _ldsDelegate.select(query);
    }

    public RecordItrRef
	iSelect (
	 Query query)
        throws DataSourceException, RemoteException
    {
        return _ldsDelegate.iSelect (query);
    }

    public boolean
	iHasNextObj (
	 RecordItrRef r)
        throws DataSourceException, RemoteException
    {
        return _ldsDelegate.iHasNextObj (r);
    }

    public Object
	iNextObj (
	 RecordItrRef r)
        throws DataSourceException, RemoteException
    {
        return _ldsDelegate.iNextObj (r);
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
        throws DataSourceException, RemoteException
    {
        return _ldsDelegate.selectDistinct(entityName, fieldName);
    }

    //--------------------------------------------------------------------------
    /**
     * Generate a key from KeyGeneratorData
     * @param kgd the KeyGeneratorData
     * @return Object the key
     */
    public Object generateKey(KeyGeneratorData kgd)
        throws DataSourceException, RemoteException
    {
        return _ldsDelegate.generateKey(kgd);
    }

    //--------------------------------------------------------------------------
    /**
     * Excecute an insert, update, delete or DDL command from raw sql.
     * @param sql, the raw sql defining the update operation.
     * @return the number or records updates or zero for DDL commands.
     */
    public int rawExecuteUpdate(String sql)
        throws DataSourceException, RemoteException
    {
        return _ldsDelegate.rawExecuteUpdate(sql);
    }

    //--------------------------------------------------------------------------
    /**
     * @see DataSource.close
     * @throws DataSourceException if there is an error from the DataSource.
     * @throws RemoteException if there is an RMI error.
     */
    public void close() throws DataSourceException, RemoteException
    {
        // do not close the delegate, there may be multiple remote clients,
        // it will be closed by the local data manager

        // release links to the delegate and unexport from RMI
        _ldsDelegate = null;

        // unexport asynchronously in a thread
        UnexportThread uet = new UnexportThread(this);
        uet.start();
    }

    //--------------------------------------------------------------------------
    /**
     * Return the unique name of the DataSource
     * @return String the DataSource name
     * @throws RemoteException if there is an RMI error.
     */
    public String getName() throws RemoteException
    {
        return _ldsDelegate.getName();
    }

    //--------------------------------------------------------------------------
    /**
     * Set the unique name of the DataSource
     * @param String the DataSource name
     * @throws RemoteException if there is an RMI error.
     */
    public void setName(String name) throws RemoteException
    {
        _ldsDelegate.setName(name);
    }

    //--------------------------------------------------------------------------
    /**
     * Create (in the database underlying the datasource) the table for this
     * entity.
     * @param ent, the entity whose table is to be created.
     * @param deleteFirst, if true the table will be deleted before the
     *   creation.
     * @throws DataSourceException if the creation failed, usually due to the
     *   table already existing
     */
    public void createEntityTable(Entity ent, boolean deleteFirst)
        throws DataSourceException, RemoteException
    {
        _ldsDelegate.createEntityTable(ent, deleteFirst);
    }

    //--------------------------------------------------------------------------
    /**
     * Import a the names of tables from the datasource.
     * @return a Vector of EntitySchemaNames containing the table names.
     */
    public Vector importTableNames()
        throws DataSourceException, RemoteException
    {
        return _ldsDelegate.importTableNames();
    }

    //--------------------------------------------------------------------------
    /**
     * Import an entity definition from the datasource.
     * @param esn, a EntitySchemaName defining the entity to import.
     * @return the imported entity definition.
     */
    public Entity importEntityDefinition(EntitySchemaName esn)
        throws DataSourceException, RemoteException
    {
        return _ldsDelegate.importEntityDefinition(esn);
    }

    //--------------------------------------------------------------------------
    /**
     * Get an entity definition.
     * @param entityName, the name of the entity.
     * @return the entity, or null if not found.
     */
    public Entity getEntity(String entityName)
         throws RemoteException
    {
        return _ldsDelegate.getEntity(entityName);
    }

    //--------------------------------------------------------------------------
    /**
     * Add an entity to the datasource.
     */
    public void addEntity(Entity entity)
         throws RemoteException
    {
        _ldsDelegate.addEntity(entity);
    }

    //--------------------------------------------------------------------------
    /**
     * @return a Vector of joins.
     */
    public Vector getJoins()
         throws RemoteException

    {
        return _ldsDelegate.getJoins();
    }

    //--------------------------------------------------------------------------
    /**
     * Add a join to the datasource.
     */
    public void addJoin(Join join)
         throws RemoteException
    {
        _ldsDelegate.addJoin(join);
    }

    //--------------------------------------------------------------------------
    /**
     * Exectues a direct SQL query.  The return value is a hashtable with keys
     * that match the query resultset's column names.  Each column (the
     * hashtable value) is a Vector that represents.
     */
    public Hashtable execDirectSQL(String sql)
        throws DataSourceException, RemoteException
    {
        return _ldsDelegate.execDirectSQL(sql);
    }
    
    public int execInsertOrUpdate(String sql)
	    throws DataSourceException, RemoteException
	{
	    return _ldsDelegate.execInsertOrUpdate(sql);
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
        throws DataSourceException, RemoteException
    {
        _ldsDelegate.dropEntityTable(entityName);
    }

    //--------------------------------------------------------------------------
    /**
     * Import an entity definition from a raw SQL select statement
     */
    public Entity importRawSelectDefinition(String selectStmt)
        throws DataSourceException, RemoteException
    {
        return _ldsDelegate.importRawSelectDefinition(selectStmt);
    }

    //--------------------------------------------------------------------------
    /**
     * Test that the data source can connect to the data base.
     * @throws DataSourceException if the connection cannot be made.
     */
    public void testConnection()
        throws DataSourceException, RemoteException
    {
        _ldsDelegate.testConnection();
    }
}

//==============================================================================
// end of file RemoteDataSourceServant.java
//==============================================================================
