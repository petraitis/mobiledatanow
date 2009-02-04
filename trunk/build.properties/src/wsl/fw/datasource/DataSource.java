/**	$Id: DataSource.java,v 1.2 2002/07/15 21:53:42 jonc Exp $
 *
 * Interface for objects representing data sources.
 * A data source can have data queried from it, and data updated into it
 * via the DataSource interface
 *
 */
package wsl.fw.datasource;

import java.util.Iterator;
import java.util.Vector;
import java.util.Hashtable;

public interface DataSource
{
	// constants
	public static String SYS_DS_NAME = "System JDBC Data Source";

	/**
	 * Insert a DataObject into the source
	 * @param dobj the DataObject to insert
	 * @return boolean true if the operation occurs
	 * @throws DataSourceException if there is an error from the DataSource.
	 * @roseuid 3973CB2701DD
	 */
	public boolean
	insert (
	 DataObject dobj)
		throws DataSourceException;

	/**
	 * Update a DataObject in the source
	 * @param dobj the DataObject to update
	 * @return boolean true if the operation occurs
	 * @throws DataSourceException if there is an error from the DataSource.
	 * @roseuid 3973CE180140
	 */
	public boolean
	update (
	 DataObject dobj)
		throws DataSourceException;

	/**
	 * Delete a DataObject from the source
	 * @param dobj the DataObject to delete
	 * @return boolean true if the operation occurs
	 * @throws DataSourceException if there is an error from the DataSource.
	 * @roseuid 3973CE180154
	 */
	public boolean
	delete (
	 DataObject dobj)
		throws DataSourceException;

	/**
	 * Select a RecordSet of DataObjects from the source
	 * @param query Query information
	 * @return DataObject
	 * @throws DataSourceException if there is an error from the DataSource.
	 * @roseuid 3973CE4001CA
	 */
	public RecordSet
	select (
	 Query query)
		throws DataSourceException;

	/**
	 *	Return an Iterator referece to the DataObjects from the source
	 * 	An optional alternative to select(), that may or may not be
	 *  implemented.
	 *
	 * @param query Query information
	 * @return DataObject
	 * @throws DataSourceException if there is an error from the DataSource.
	 */
	public RecordItrRef
	iSelect (
	 Query query)
		throws DataSourceException;

	public boolean
	iHasNextObj (
	 RecordItrRef ref)
		throws DataSourceException;

	public Object
	iNextObj (
	 RecordItrRef ref)
		throws DataSourceException;

	/**
	 * Select a distinct set of values from a column
	 * @param entityName
	 * @param fieldName
	 * @return Vector
	 * @throws DataSourceException if there is an error from the DataSource.
	 */
	public Vector
	selectDistinct (
	 String entityName,
	 String fieldName)
		throws DataSourceException;

	/**
	 * Close the DataSource and release its resources
	 * @throws DataSourceException if there is an error from the DataSource.
	 */
	public void
	close ()
		throws DataSourceException;

	/**
	 * Return the unique name of the DataSource
	 * @return String the DataSource name
	 */
	public String
	getName ();

	/**
	 * Set the unique name of the DataSource
	 * @param String the DataSource name
	 */
	public void
	setName (
	 String name);

	/**
	 * Generate a key from KeyGeneratorData
	 * @param kgd the KeyGeneratorData
	 * @return Object the key
	 */
	public Object
	generateKey (
	 KeyGeneratorData kgd)
		throws DataSourceException;

	/**
	 * Excecute an insert, update, delete or DDL command from raw sql.
	 * @param sql, the raw sql defining the update operation.
	 * @return the number or records updates or zero for DDL commands.
	 */
	public int
	rawExecuteUpdate (
	 String sql)
		throws DataSourceException;

	//==========================================================================
	// Version 2

	/**
	 * Add an Entity to the Entity vector
	 * @param entity the Entity to add
	 */
	public void
	addEntity (
	 Entity entity);

	/**
	 * Get an entity by name from the the DataSource
	 * @param name the name of the Entity
	 * @return Entity the Entity with the param name, or null if not found
	 */
	public Entity
	getEntity (
	 String name);

	/**
	 * Get the Vector of Entities
	 * @return Vector the Entities
	 */
	public Vector
	getEntities ();

	/**
	 * Create (in the database underlying the datasource) the table for this
	 * entity.
	 * @param ent, the entity whose table is to be created.
	 * @param deleteFirst, if true the table will be deleted before the
	 *   creation.
	 * @throws DataSourceException if the creation failed, usually due to the
	 *   table already existing
	 */
	public void
	createEntityTable (
	 Entity ent,
	 boolean deleteFirst)
		throws DataSourceException;

	/**
	 * Add a Join to the join Vector
	 * @param join the Join to add
	 */
	public void
	addJoin (
	 Join join);

	/**
	 * Get the Vector of Joins
	 * @return Vector the Joins
	 */
	public Vector
	getJoins ();

	/**
	 * Import a the names of tables from the datasource.
	 * @return a Vector of EntitySchemaNames containing the table names.
	 */
	public Vector
	importTableNames ()
		throws DataSourceException;

	/**
	 * Import an entity definition from the datasource.
	 * @param esn, a EntitySchemaName defining the entity to import.
	 * @return the imported entity definition.
	 */
	public Entity
	importEntityDefinition (
	 EntitySchemaName esn)
		throws DataSourceException;

	/**
	 * Import an Entity definition from a raw SQL select statement
	 */
	public Entity
	importRawSelectDefinition (
	 String selectStmt)
		throws DataSourceException;

	/**
	 * @return an identifier that can be used to lookup this DataSource. This
	 *   should be a DataSourceParam or a string name.
	 */
	public Object
	getDsId ();

	/**
	 * Exectues a direct SQL query.  The return value is a hashtable with
	 * keys that match the query resultset's column names. Each column
	 * (the hashtable value) is a Vector that represents
	 */
	public Hashtable
	execDirectSQL (
	 String sql)
		throws DataSourceException;
	/**
	 * Exectues a direct SQL update or insert query.  The return value is row number which executes
	 */	
	public int
	execInsertOrUpdate (
	 String sql)
		throws DataSourceException;
	/**
	 * Drop (in the database underlying the datasource) the table for this
	 * entity.
	 * @param entityName, the name of the entity whose table is to be created.
	 * @throws DataSourceException if the creation failed, usually due to the
	 *   table already existing
	 */
	public void
	dropEntityTable (
	 String entityName)
		throws DataSourceException;

	/**
	 * Test that the data source can connect to the data base.
	 * @throws DataSourceException if the connection cannot be made.
	 */
	public void
	testConnection ()
		throws DataSourceException;
}
