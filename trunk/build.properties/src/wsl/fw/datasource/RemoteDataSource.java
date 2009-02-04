/**	$Header: /wapsolutions/cvsroot/mdn/fw/src/wsl/fw/datasource/RemoteDataSource.java,v 1.1.1.1 2002/06/11 23:11:42 jonc Exp $
 *
 * Remote interface for communicating with remote data sources.
 *
 */
package wsl.fw.datasource;

import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import wsl.fw.remote.RmiServant;

public interface RemoteDataSource extends RmiServant
{
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
        throws DataSourceException, RemoteException;

    //--------------------------------------------------------------------------
    /**
     * Note this will need to be changed if dobj is modified by the server as
     * RMI params are passed by value.
     * @see DataSource.update
     * @param dobj
     * @return boolean true if the operation occurs
     * @throws DataSourceException if there is an error from the DataSource.
     * @throws RemoteException if there is an RMI error.
     */
    public boolean update(DataObject dobj)
        throws DataSourceException, RemoteException;

    //--------------------------------------------------------------------------
    /**
     * Note this will need to be changed if dobj is modified by the server as
     * RMI params are passed by value.
     * @see DataSource.delete.
     * @param dobj
     * @return boolean true if the operation occurs
     * @throws DataSourceException if there is an error from the DataSource.
     * @throws RemoteException if there is an RMI error.
     */
    public boolean delete(DataObject dobj)
        throws DataSourceException, RemoteException;

    //--------------------------------------------------------------------------
    /**
     * @see DataSource.select
     * @param query
     * @return RecordSet
     * @throws DataSourceException if there is an error from the DataSource.
     * @throws RemoteException if there is an RMI error.
     */
    public RecordSet select(Query query)
        throws DataSourceException, RemoteException;

	public RecordItrRef
	iSelect (
	 Query q)
        throws DataSourceException, RemoteException;

	public boolean
	iHasNextObj (
	 RecordItrRef r)
        throws DataSourceException, RemoteException;

	public Object
	iNextObj (
	 RecordItrRef r)
        throws DataSourceException, RemoteException;

    //--------------------------------------------------------------------------
    /**
     * Select a distinct set of values from a column
     * @param entityName
     * @param fieldName
     * @return Vector
     * @throws DataSourceException if there is an error from the DataSource.
     */
    public Vector selectDistinct(String entityName, String fieldName)
        throws DataSourceException, RemoteException;

    //--------------------------------------------------------------------------
    /**
     * Generate a key from KeyGeneratorData
     * @param kgd the KeyGeneratorData
     * @return Object the key
     */
    public Object generateKey(KeyGeneratorData kgd)
        throws DataSourceException, RemoteException;

    //--------------------------------------------------------------------------
    /**
     * Excecute an insert, update, delete or DDL command from raw sql.
     * @param sql, the raw sql defining the update operation.
     * @return the number or records updates or zero for DDL commands.
     */
    public int rawExecuteUpdate(String sql)
        throws DataSourceException, RemoteException;

    //--------------------------------------------------------------------------
    /**
     * @see DataSource.close
     * @return void
     * @throws DataSourceException if there is an error from the DataSource.
     * @throws RemoteException if there is an RMI error.
     */
    public void close() throws DataSourceException, RemoteException;

    //--------------------------------------------------------------------------
    /**
     * Return the unique name of the DataSource
     * @return String the DataSource name
     * @throws RemoteException if there is an RMI error.
     */
    public String getName() throws RemoteException;

    //--------------------------------------------------------------------------
    /**
     * Set the unique name of the DataSource
     * @param String the DataSource name
     * @throws RemoteException if there is an RMI error.
     */
    public void setName(String name) throws RemoteException;

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
        throws DataSourceException, RemoteException;

    //--------------------------------------------------------------------------
    /**
     * Import a the names of tables from the datasource.
     * @return a Vector of EntitySchemaNames containing the table names.
     */
    public Vector importTableNames()
        throws DataSourceException, RemoteException;

    //--------------------------------------------------------------------------
    /**
     * Import an entity definition from the datasource.
     * @param esn, a EntitySchemaName defining the entity to import.
     * @return the imported entity definition.
     */
    public Entity importEntityDefinition(EntitySchemaName esn)
        throws DataSourceException, RemoteException;

    //--------------------------------------------------------------------------
    /**
     * Get an entity definition.
     * @param entityName, the name of the entity.
     * @return the entity, or null if not found.
     */
    public Entity getEntity(String entityName)
         throws RemoteException;

    //--------------------------------------------------------------------------
    /**
     * Add an entity to the datasource.
     */
    public void addEntity(Entity entity)
         throws RemoteException;

    //--------------------------------------------------------------------------
    /**
     * @return a Vector of joins.
     */
    public Vector getJoins()
         throws RemoteException;

    //--------------------------------------------------------------------------
    /**
     * Add a join to the datasource.
     */
    public void addJoin(Join join)
         throws RemoteException;

    //--------------------------------------------------------------------------
    /**
     * Exectues a direct SQL query.  The return value is a hashtable with keys
     * that match the query resultset's column names.  Each column (the
     * hashtable value) is a Vector that represents.
     */
    public Hashtable execDirectSQL(String sql)
        throws DataSourceException, RemoteException;
    public int execInsertOrUpdate(String sql)
    	throws DataSourceException, RemoteException;
    
    //--------------------------------------------------------------------------
    /**
     * Drop (in the database underlying the datasource) the table for this
     * entity.
     * @param entityName, the name of the entity whose table is to be created.
     * @throws DataSourceException if the creation failed, usually due to the
     *   table already existing
     */
    public void dropEntityTable(String entityName)
        throws DataSourceException, RemoteException;

    //--------------------------------------------------------------------------
    /**
     * Import an entity definition from a raw SQL select statement
     */
    public Entity importRawSelectDefinition(String selectStmt)
        throws DataSourceException, RemoteException;

    //--------------------------------------------------------------------------
    /**
     * Test that the data source can connect to the data base.
     * @throws DataSourceException if the connection cannot be made.
     */
    public void testConnection()
        throws DataSourceException, RemoteException;
}

//==============================================================================
// end of file RemoteDataSource.java
//==============================================================================
