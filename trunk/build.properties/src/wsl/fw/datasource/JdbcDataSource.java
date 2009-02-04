/**	$Id: JdbcDataSource.java,v 1.11 2004/01/06 01:41:03 tecris Exp $
 *
 * JDBC implementation of the DataSource interface
 *
 */
package wsl.fw.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSetMetaData;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Set;

import wsl.fw.datasource.dbdelegate.*;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.util.Type;
import wsl.fw.util.TypeConversionException;
import wsl.fw.resource.ResId;

public class JdbcDataSource extends DataSourceImplBase
{
	//--------------------------------------------------------------------------
	// resources

	public static final ResId
		VALIDATE_NULL		= new ResId ("JdbcDataSource.validate.Null"),
		VALIDATE_QUERY		= new ResId ("JdbcDataSource.validate.Query"),
		VALIDATE_DRIVER		= new ResId ("JdbcDataSource.validate.Driver"),
		VALIDATE_URL		= new ResId ("JdbcDataSource.validate.URL"),
		LOG_CONNECTING		= new ResId ("JdbcDataSource.log.Connecting"),
		ERR_URL_NOT_FOUND	= new ResId ("JdbcDataSource.error.URLNotFound"),
		ERR_NO_CLASSES		= new ResId ("JdbcDataSource.error.NoClasses"),
		ERR_NO_FIELDS		= new ResId ("JdbcDataSource.error.NoFields"),

		EXCEPTION_INVALID_QUERY		= new ResId ("JdbcDataSource.exception.InvalidQuery"),
		EXCEPTION_NO_QUERY			= new ResId ("JdbcDataSource.exception.NoQuery"),
		EXCEPTION_INVALID_SYNTAX	= new ResId ("JdbcDataSource.exception.InvalidSyntax"),
		EXCEPTION_INVALID_CRITERIUM	= new ResId ("JdbcDataSource.exception.InvalidCriterium"),
		ERR_UNKNOWN_FIELD_TYPE		= new ResId ("JdbcDataSource.error.UnknownFieldType"),
		ERR_INVALID_SQL_QUERY		= new ResId ("JdbcDataSource.error.invalidSqlQuery"),
		ERR_QUERY_FAILURE			= new ResId ("JdbcDataSource.error.queryFailure");

	//--------------------------------------------------------------------------
	// constants

	private static final int MAX_COL_SIZE = 2048;
	public static final String CLASS_COLUMN_NAME = "FLD_CLASS";
	public static final String REM_TABLE_ESC = "@#";

	// dbflags
	public static final int DBF_NONE = 0;
	public static final int DBF_JOINS_IN_WHERE = 1;
	public static final int DBF_NO_SUPPORTS_OJ = 2;
	public static final int DBF_NO_SUPPORTS_ROJ = 4;

	//--------------------------------------------------------------------------
	// attributes

	// jdbc connect info
	private String  _driver = "";
	private String  _url = "";
	private String  _catalog = "";
	private String  _user = "";
	private String  _pw = "";
	private boolean _hasPoolData = false;
	private int     _poolSize;
	private int     _msLifetime;
	private int     _msGetTimeout;
	private JdbcDataSourceParam _param = null;
	private NativeDelegate _native = null;

	// Jdbc attributes
	private JdbcKeyGenerator _kg = null;
	private int _dbFlags = DBF_JOINS_IN_WHERE | DBF_NO_SUPPORTS_OJ;


	//--------------------------------------------------------------------------
	/**
	 * blank constructor
	 * creates and initialises the key generator
	 */
	public JdbcDataSource ()
	{
		_kg = new JdbcKeyGenerator (this);
	}

	//--------------------------------------------------------------------------
	/**
	 * Set the connect data into the DataSource
	 * @param driver Name of the java driver class
	 * @param url URL of the data source
	 * @param catalog Name of the database catalog
	 * @param user Name of the database User
	 * @param pw Password of the database User
	 */
	public synchronized void
	setConnectData (
	 String driver,
	 String url,
	 //String catalog,
	 String user,
	 String pw)
	{		
		_driver = driver;
		_url = url;
		//_catalog = catalog;
		_user = user;
		_pw = pw;
		System.out.println ("JdbcDataSource setConnectData: driver=" + _driver + ", url=" + _url
				+ ", user=" + _user);
	}


	//--------------------------------------------------------------------------
	// flags

	/**
	 * @return int the DB flags
	 */
	public int getDbFlags ()
	{
		return _dbFlags;
	}

	/**
	 * @param flag the flag to check
	 * @return true if a flag exists
	 */
	public boolean hasDbFlag (int flag)
	{
		return (getDbFlags () & flag) > 0;
	}


	//--------------------------------------------------------------------------
	/**
	 * Insert a DataObject into the source
	 * @param dobj the DataObject to insert
	 * @return boolean true if the operation occurs
	 * @throws DataSourceException if there is an error from the DataSource.
	 */
	public boolean
	insert (
	 DataObject dobj)
		throws DataSourceException
	{
		// validate
		Util.argCheckNull (dobj, VALIDATE_NULL.getText ());

		// if no default class, set the class field
		if (dobj.getEntity ().getDefaultClass () == null)
			dobj.setValue (CLASS_COLUMN_NAME, dobj.getClass ().getName ());

		Connection con = null;
		boolean isError = false;
		try
		{
			// add any generated keys
			setGeneratedKeys (dobj);
			con = getConnection ();
			con.setAutoCommit(true);
			// build the insert statement
			String sql = buildInsertStatement (con, dobj, false);

			// execute the statement
			if (sql != null && sql.length () > 0)
			{
				// log
				Log.debug (sql);

				// param update
				if (getNative ().useParamUpdate ())
				{
					// prepare statement
					sql = buildInsertStatement (con, dobj, true);
					PreparedStatement ps = con.prepareStatement (sql);

					// set the insert params
					setInsertParams (ps, dobj);

					// execute the update and close
					ps.execute ();
					ps.close ();
				}
				else
				{
					Statement stmt = getUpdateStatement (con);
					stmt.executeUpdate (sql);
					stmt.close ();
				}
				con.commit();
				return true;
			}
		}
		catch (SQLException e)
		{
			isError = true;
			throw new DataSourceException ("JdbcDataSource.insert: "
				+ e.toString () + ", " + e.getSQLState ());
		}
		catch (Exception e)
		{
			isError = true;
			throw new DataSourceException ("JdbcDataSource.insert: "
				+ e.toString ());
		}
		finally
		{
			// return the connection to the pool
			if (con != null)
				try
				{
					// if error and pooled pooled close the actual connection
					if (isError && con instanceof PooledConnection)
						 ((PooledConnection)con).close (true);
					else
						con.close ();
				}
				catch (SQLException e)
				{
				}
		}

		return false;
	}

	/**
	 * Set the insert parameters for a parameterised insert
	 * @param ps the prepared statement
	 * @param dobj the DataObject
	 */
	private void
	setInsertParams (
	 PreparedStatement ps,
	 DataObject dobj)
		throws SQLException, TypeConversionException
	{
		// add the fields
		Object value;
		Field f;
		Vector fields = dobj.getEntity ().getFields ();
		int paramIndex = 1;
		for (int i = 0; fields != null && i < fields.size (); i++)
		{
			// get the next field
			f = (Field)fields.elementAt (i);
			Util.argCheckNull (f);

			// convert the value
			value = Type.convertValueOnType (dobj.getObjectValue (f.getName ()),
				f.getType ());

			// add if not empty
			if (!Type.isValueEmpty (value))
			{
				setPreparedParam (ps, f, value, paramIndex);
				paramIndex++;
			}
		}
	}

	/**
	 * Set the update parameters for a parameterised update
	 * @param ps the prepared statement
	 * @param dobj the DataObject
	 * @return int the paramIndex
	 */
	private int
	setUpdateParams (
	 PreparedStatement ps,
	 DataObject dobj)
		throws SQLException, TypeConversionException
	{
		// add the fields
		Object value, img;
		Field f;
		Vector fields = dobj.getEntity ().getFields ();
		int paramIndex = 1;
		for (int i = 0; fields != null && i < fields.size (); i++)
		{
			// get the next field
			f = (Field)fields.elementAt (i);
			Util.argCheckNull (f);

			// convert the value
			value = Type.convertValueOnType (dobj.getObjectValue (f.getName ()),
				f.getType ());

			//get the image value
			img = Type.convertValueOnType (dobj.getImageValue (f.getName ()),
				f.getType ());

			// add the field name to sql if not equal
			if (!valuesEqual (value, img) && !Type.isValueEmpty (value))
			{
				setPreparedParam (ps, f, value, paramIndex);
				paramIndex++;
			}
		}

		// return the NEXT param index
		return paramIndex;
	}

	/**
	 * Set the params for the key clause
	 * @param stmt the PreparedStatement
	 * @param dobj the DataObject
	 * @param paramIndex the NEXT parameter index
	 */
	private void
	setKeyClauseParams (
	 PreparedStatement stmt,
	 DataObject dobj,
	 int paramIndex)
		throws SQLException, TypeConversionException
	{
		// validate
		Util.argCheckNull (stmt);
		Util.argCheckNull (dobj);

		// get the entity
		Entity ent = dobj.getEntity ();

		// have we got a sys key
		boolean keysSet = false;
		Object val;
		Field f;
		String sysKey = dobj.getSystemKey (true);
		if (sysKey != null && sysKey.length () > 0)
		{
			// get the sys key field
			f = ent.getSystemKeyField ();
			if (f != null)
			{
				// get the image value
				val = dobj.getImageValue (f.getName ());
				val = Type.convertValueOnType (val, f.getType ());

				// set the key clause param
				if (val != null)
				{
					setPreparedParam (stmt, f, val, paramIndex);
					paramIndex++;
					keysSet = true;
				}
			}
		}

		// else use the unique keys
		if (!keysSet)
		{
			// iterate the key fields
			Vector keyFields = ent.getUniqueKeyFields ();
			for (int i = 0; keyFields != null && i < keyFields.size (); i++)
			{
				// get the field
				f = (Field)keyFields.elementAt (i);
				if (f != null)
				{
					// get the image value
					val = dobj.getImageValue (f.getName ());
					val = Type.convertValueOnType (val, f.getType ());

					// set the key clause param
					if (val != null)
					{
						setPreparedParam (stmt, f, val, paramIndex);
						paramIndex++;
						keysSet = true;
					}
				}
			}
		}

		// else use all fields
		if (!keysSet)
		{
			// iterate the key fields
			Vector keyFields = ent.getFields ();
			for (int i = 0; keyFields != null && i < keyFields.size (); i++)
			{
				// get the field
				f = (Field)keyFields.elementAt (i);
				if (f != null)
				{
					// get the image value
					val = dobj.getImageValue (f.getName ());
					val = Type.convertValueOnType (val, f.getType ());

					// set the key clause param
					if (val != null)
					{
						setPreparedParam (stmt, f, val, paramIndex);
						paramIndex++;
						keysSet = true;
					}
				}
			}
		}
	}

	/**
	 * Set a prepared param into a prepared statement
	 * @param stmt the prepared statement to put the value into
	 * @param f the Field containing the type
	 * @param val the converted value to set
	 * @param paramIndex the index of the parameter in the statement
	 */
	private void
	setPreparedParam (
	 PreparedStatement stmt,
	 Field f,
	 Object val,
	 int paramIndex)
		throws SQLException, TypeConversionException
	{
		// validate
		Util.argCheckNull (stmt);
		Util.argCheckNull (f);
		Util.argCheckNull (val);

		// give first shot to delegate
		if (getNative ().setPreparedParam (stmt, f, val, paramIndex))
			return;

		// switch on type
		switch (f.getType ())
		{
			case Field.FT_STRING:
			{
				assert val instanceof String;
				stmt.setString (paramIndex, (String)val);
				break;
			}
			case Field.FT_BOOLEAN:
			{
				assert  val instanceof Boolean;
				stmt.setBoolean (paramIndex, ( (Boolean)val).booleanValue ());
				break;
			}
			case Field.FT_INTEGER:
			{
				assert val instanceof Integer;
				stmt.setInt (paramIndex, ( (Integer)val).intValue ());
				break;
			}
			case Field.FT_CURRENCY:
			case Field.FT_DECIMAL:
			{
				assert val instanceof Double;
				stmt.setDouble (paramIndex, ( (Double)val).doubleValue ());
				break;
			}
			case Field.FT_DATETIME:
			{
				assert val instanceof java.sql.Timestamp;
				stmt.setTimestamp (paramIndex, (java.sql.Timestamp)val);
				break;
			}
			default:
			{
				stmt.setObject (paramIndex, val);
				break;
			}
		}
	}

	//--------------------------------------------------------------------------
	/**
	 * Executes KeyGenerators and sets any generated keys
	 * @param the DataObject to set keys into
	 * @return void
	 * @throws DataSourceException if there is an error from the DataSource.
	 * @throws SQLException if there is a JDBC error.
	 */
	private void
	setGeneratedKeys (
	 DataObject dobj)
		throws DataSourceException, SQLException
	{
		// must have a DataObject
		if (dobj == null)
			return;

		// get the Entity and KeyGeneratorData
		Entity ent = dobj.getEntity ();
		Vector v = ent.getKeyGeneratorData ();

		// iterate the kgds
		KeyGeneratorData kgd;
		for (int i = 0; v != null && i < v.size (); i++)
		{
			// get the kgd
			kgd = (KeyGeneratorData)v.elementAt (i);

			// execute the key generator
			if (_kg != null && kgd != null)
			{
				Object key = _kg.getNextKey (kgd);

				// set the key into the DataObject
				if (key != null)
					dobj.setValue (kgd._targetField, key);
			}
		}
	}

	//--------------------------------------------------------------------------
	/**
	 * Generate a key from KeyGeneratorData
	 * @param kgd the KeyGeneratorData
	 * @return Object the key
	 */
	public Object
	generateKey (
	 KeyGeneratorData kgd)
		throws DataSourceException
	{
		// delegate to key generator
		Object key = null;
		try
		{
			key = _kg.getNextKey (kgd);
		}
		catch (SQLException e)
		{
			throw new DataSourceException ("JdbcDataSource.generateKey: "
				+ e.toString () + ", " + e.getSQLState ());
		}
		return key;
	}

	//--------------------------------------------------------------------------
	/**
	 * Update a DataObject in the source
	 * @param dobj the DataObject to update
	 * @return boolean true if the operation occurs
	 * @throws DataSourceException if there is an error from the DataSource.
	 */
	public boolean
	update (
	 DataObject dobj)
		throws DataSourceException
	{
		// validate
		//Util.argCheckNull (dobj, "DataObject cannot be null");
		Util.argCheckNull (dobj, VALIDATE_NULL.getText ());

		Connection con = null;
		boolean isError = false;
		try
		{
			con = getConnection ();
			con.setAutoCommit(true);
			// build the update statement
			String sql = buildUpdateStatement (con, dobj, false);

			// execute the statement
			if (sql != null && sql.length () > 0)
			{
				Log.debug (sql);

				// param update
				if (getNative ().useParamUpdate () || getNative ().useParamUpdateCriteria ())
				{
					// prepare statement
					sql = buildUpdateStatement (con, dobj, getNative ().useParamUpdate ());
					PreparedStatement ps = con.prepareStatement (sql);

					// set the update params
					int nextParamIndex = 1;
					if (getNative ().useParamUpdate ())
						nextParamIndex = setUpdateParams (ps, dobj);

					// key clause params
					if (getNative ().useParamUpdateCriteria ())
						setKeyClauseParams (ps, dobj, nextParamIndex);

					// execute the update and close
					ps.execute ();
					ps.close ();
				}
				else
				{
					Statement stmt = getUpdateStatement (con);
					stmt.executeUpdate (sql);
					stmt.close ();
				}
				con.commit();
				return true;
			}
		}
		catch (SQLException e)
		{
			isError = true;
			throw new DataSourceException ("JdbcDataSource.update: "
				+ e.toString () + ", " + e.getSQLState ());
		}
		catch (Exception e)
		{
			isError = true;
			throw new DataSourceException ("JdbcDataSource.update: "
				+ e.toString ());
		}
		finally
		{
			// return the connection to the pool
			if (con != null)
				try
				{
					// if error and pooled pooled close the actual connection
					if (isError && con instanceof PooledConnection)
						 ((PooledConnection)con).close (true);
					else
						con.close ();
				}
				catch (SQLException e)
				{
				}
		}

		return false;
	}

	//--------------------------------------------------------------------------
	/**
	 * Delete a DataObject from the source
	 * @param dobj the DataObject to delete
	 * @return boolean true if the operation occurs
	 * @throws DataSourceException if there is an error from the DataSource.
	 */
	public boolean
	delete (
	 DataObject dobj)
		throws DataSourceException
	{
		// validate
		//Util.argCheckNull (dobj, "DataObject cannot be null");
		Util.argCheckNull (dobj, VALIDATE_NULL.getText ());

		Connection con = null;
		boolean isError = false;
		try
		{
			con = getConnection ();

			// build the delete statement
			String sql = buildDeleteStatement (con, dobj);

			// execute the statement
			if (sql != null && sql.length () > 0)
			{
				// log
				Log.debug (sql);

				// param delete
				if (getNative ().useParamUpdateCriteria ())
				{
					// prepare statement
					PreparedStatement ps = con.prepareStatement (sql);

					// key clause params
					setKeyClauseParams (ps, dobj, 1);

					// execute the update and close
					ps.execute ();
					ps.close ();
				}

				// non param delete
				else
				{
					Statement stmt = getUpdateStatement (con);
					stmt.executeUpdate (sql);
					stmt.close ();
				}
				return true;
			}
		}
		catch (SQLException e)
		{
			isError = true;
			throw new DataSourceException ("JdbcDataSource.delete: "
				+ e.toString () + ", " + e.getSQLState ());
		}
		catch (Exception e)
		{
			isError = true;
			throw new DataSourceException ("JdbcDataSource.delete: "
				+ e.toString ());
		}
		finally
		{
			// return the connection to the pool
			if (con != null)
				try
				{
					// if error and pooled pooled close the actual connection
					if (isError && con instanceof PooledConnection)
						 ((PooledConnection)con).close (true);
					else
						con.close ();
				}
				catch (SQLException e)
				{
				}
		}

		return false;
	}

	//--------------------------------------------------------------------------
	/**
	 * If the query is using field exclusions then ensure that the key fields
	 * are included so the data objects will be valid.
	 * Also includes join fields and class field.
	 * @param query, the query to add key fields for.
	 */
	private void
	addQueryKeyFields (
	 Query query)
	{
		// nothing to do if not using field exclusions
		if (query.hasFieldExclusions ())
		{
			// get the entities and enumerate
			Enumeration entEnum = query.getQueryEntities ();

			if (entEnum != null)
				while (entEnum.hasMoreElements ())
				{
					// get the entity name and entity
					String entName = (String) entEnum.nextElement ();
					Entity ent = getEntity (entName);

					if (ent != null)
					{
						// get the key fields
						Vector keyFields = ent.getUniqueKeyFields ();

						// if no keys add all fields
						if (keyFields.size () <= 0)
							keyFields = ent.getFields ();

						// iterate adding key fields
						for (int i = 0; i < keyFields.size (); i++)
							query.addQueryField (entName,
								 ((Field) keyFields.get (i)).getName ());
					}

					// add the system key
					Field systemKey = ent.getSystemKeyField ();
					if (systemKey != null)
						query.addQueryField (entName, systemKey.getName ());

					// ensure the class name is included if reqd
					if (ent.getDefaultClass () == null)
						if (ent.getField (CLASS_COLUMN_NAME) != null)
							query.addQueryField (entName, CLASS_COLUMN_NAME);
				}

			// get relevant joins and add their keys and foreign keys
			Vector relevantJoins = getValidJoins (query);
			for (int i = 0; i < relevantJoins.size (); i++)
			{
				Join j = (Join) relevantJoins.get (i);
				if (j != null)
				{
					query.addQueryField (j.getLeftEntity (), j.getLeftKey ());
					query.addQueryField (j.getRightEntity (), j.getRightKey ());
				}
			}
		}
	}

	//--------------------------------------------------------------------------
	/**
	 * Select a RecordSet of DataObjects from the source
	 * @param query Query information
	 * @return the query results in a RecordSet
	 * @throws DataSourceException if there is an error from the DataSource.
	 */
	public RecordSet
	select (
	 Query query)
		throws DataSourceException
	{
		// validate
		//Util.argCheckNull (query, "Query cannot be null");
		Util.argCheckNull (query, VALIDATE_QUERY.getText ());

		Connection con = null;
		boolean isError = false;
		try
		{
			// ensure that the query, if it is using field restrictions, has
			// all the key fields set
			addQueryKeyFields (query);

			// build the insert statement
			con = getConnection ();

			String sql = buildSelectStatement (con, query, false);

			// execute the insert statement
			RecordSet ret = new RecordSet ();
			if (sql != null && sql.length () > 0)
			{
				// log
				Log.debug (sql);

				// parameterised or not
				ResultSet rs = null;
				if (getNative ().useParamSelect ())
				{
					// get the prepared statement
					sql = buildSelectStatement (con, query, true);
					PreparedStatement stmt = getPreparedStatement (con, sql);

					// set the prepared statement parameters
					setPreparedParameters (stmt, query);

					// execute the statement
					rs = stmt.executeQuery ();

					// build the record set and close the statement
					if (rs != null)
						buildRecordSet (query, ret, rs);
					stmt.close ();
				}
				else
				{
					// get statement
					Statement stmt = con.createStatement ();

					// execute query
					rs = stmt.executeQuery (sql);

					// build the record set and close the statement
					if (rs != null)
						buildRecordSet (query, ret, rs);
					stmt.close ();
				}
			}

			// return
			return ret;
		}
		catch (SQLException e)
		{
			Log.error ("xxxxxxxxxxxxxx\n", e);
			isError = true;
			throw new DataSourceException ("JdbcDataSource.select : "
				+ e.toString () + ", " + e.getSQLState ());
		}
		catch (Exception e)
		{
			isError = true;
			throw new DataSourceException (e.toString ());
		}
		finally
		{
			// return the connection to the pool
			if (con != null)
			{
				try
				{
					// if error and pooled pooled close the actual connection
					if (isError && con instanceof PooledConnection)
						 ((PooledConnection)con).close (true);
					else
						con.close ();
				}
				catch (SQLException e)
				{
				}
			}
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
	public Vector
	selectDistinct (
	 String entityName,
	 String fieldName)
		throws DataSourceException
	{
		// select
		String sql = "SELECT DISTINCT ";

		// column
		sql += escapeFieldName (fieldName);

		// table
		sql += " FROM " + escapeTableName (entityName);

		// order
		sql += " ORDER BY " + escapeFieldName (fieldName) + " ASC";

		// execute the query
		Connection con = null;
		Vector ret = new Vector ();
		boolean isError = false;
		try
		{
			Log.debug (sql);
			con = getConnection ();
			Statement stmt = con.createStatement ();
			ResultSet rs = stmt.executeQuery (sql);

			// get the resultset meta data
			ResultSetMetaData md = rs.getMetaData ();
			String strClass = md.getColumnClassName (1);

			// iterate resultset and add values
			Object val;
			while (rs.next ())
			{
				// get the value
				val = getResultSetValue (strClass, rs, 1);
				ret.add (val);
			}

			// close statement
			stmt.close ();
		}
		catch (Exception e)
		{
			isError = true;
			throw new DataSourceException ("JdbcDataSource.selectDistinct: " + e.toString ());
		}
		finally
		{
			// return the connection to the pool
			if (con != null)
				try
				{
					// if error and pooled pooled close the actual connection
					if (isError && con instanceof PooledConnection)
						 ((PooledConnection)con).close (true);
					else
						con.close ();
				}
				catch (SQLException e)
				{
				}
		}

		// return
		return ret;
	}

	//--------------------------------------------------------------------------
	/**
	 * Gets a connection to the appropriate database from the DataManager's
	 * connection pool manager. this connection is owned by the caller who MUST
	 * call close on that connection when finished to return it to the pool.
	 * @return java.sql.Connection
	 * @throws DataSourceException if there is an error getting the connection.
	 */
	public Connection
	getConnection ()
		throws DataSourceException
	{
		// must have a driver and url
		//Util.argCheckEmpty (_driver, "Driver name not supplied");
		Util.argCheckEmpty (_driver, VALIDATE_DRIVER.getText ());
		//Util.argCheckEmpty (_url, "URL not supplied");
		Util.argCheckEmpty (_url, VALIDATE_URL.getText ());

		//System.out.println ("JdbcDataSource getConnection: driver=" + _driver + ", url=" + _url
		//		+ ", user=" + _user);		
		
		// get the connectionpool manager
		ConnectionPoolManager cpm = DataManager.getConnectionPoolMgr ();

		// get the pool for the desired database, use connection pool info if
		// it exists
		ConnectionPool pool;
		if (_hasPoolData)
			pool = cpm.getPool (_poolSize, _msLifetime, _msGetTimeout,
				_driver, _url, _catalog, _user, _pw);
		else
			pool = cpm.getPool (_driver, _url, _catalog, _user, _pw);

		try
		{
			// get a connection from the pool and return it
			return pool.getConnection ();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new DataSourceException ("JdbcDataSource.getConnection: " + e.toString ());
		}
	}

	//--------------------------------------------------------------------------
	/**
	 * Close the DataSource and release its resources
	 * @throws DataSourceException if there is an error from the DataSource.
	 */
	public synchronized void
	close ()
		throws DataSourceException
	{
		// does nothing now that the jdbc connections are pooled
	}

	//--------------------------------------------------------------------------
	/**
	 * Call close to release resources
	 */
	protected void
	finalize ()
		throws Throwable
	{
		// close
		close ();

		// super
		super.finalize ();
	}

	//--------------------------------------------------------------------------
	/**
	 * Creates and returns java.sql.Statement
	 * @return java.sql.Statement
	 * @throws DataSourceException if there is an error from the DataSource.
	 * @throws SQLException if there is a JDBC error.
	 */
	private Statement
	getUpdateStatement (
	 Connection con)
		throws DataSourceException, SQLException
	{
		return con.createStatement ();
	}

	//--------------------------------------------------------------------------
	/**
	 * Creates and returns java.sql.Statement
	 * @return java.sql.Statement
	 * @throws DataSourceException if there is an error from the DataSource.
	 * @throws SQLException if there is a JDBC error.
	 */
	private Statement
	getQueryStatement (
	 Connection con)
		throws DataSourceException, SQLException
	{
		return con.createStatement ();
	}

	//--------------------------------------------------------------------------
	/**
	 * Creates and returns a java.sql.PreparedStatement
	 * @param strStmt the String SQL statement
	 * @return the java.sql.PreparedStatement
	 * @throws DataSourceException if there is an error from the DataSource.
	 * @throws SQLException if there is a JDBC error.
	 */
	private PreparedStatement
	getPreparedStatement (
	 Connection con,
	 String strStmt)
		throws DataSourceException, SQLException
	{
		return con.prepareStatement (strStmt);
	}

	//--------------------------------------------------------------------------
	/**
	 * builds an SQL insert statement
	 * @param dobj the DataObject to build a statement for
	 * @return the SQL insert statement or "" if invalid
	 * @throws DataSourceException if there is an error from the DataSource.
	 * @throws SQLException if there is a JDBC error.
	 */
	private String
	buildInsertStatement (
	 Connection con,
	 DataObject dobj,
	 boolean doPrepared)
		throws DataSourceException, SQLException, TypeConversionException
	{
		// must have a DataObject
		Util.argCheckNull (dobj);

		// must have an entity
		Entity ent = dobj.getEntity ();
		Util.argCheckNull (ent);

		// insert clause
		String sql = "INSERT INTO ";
		sql += escapeTableName (ent.getName ());

		// get the fields, must have fields
		Vector fields = ent.getFields ();
		if (fields == null || fields.size () == 0)
			return "";

		// add the fields
		String strValues = "";
		Object value;
		Field f;
		boolean isDirty = false;
		for (int i = 0; i < fields.size (); i++)
		{
			// get the next field
			f = (Field)fields.elementAt (i);
			Util.argCheckNull (f);

			// convert the value
			value = Type.convertValueOnType (dobj.getObjectValue (f.getName ()),
				f.getType ());

			// add if not empty
			if (!Type.isValueEmpty (value))
			{
				// add the field name to sql
				sql += isDirty? ", ": " (";
				sql += escapeFieldName (f.getName ());

				// add the formatted value to the values clause
				strValues += isDirty? ", ": " VALUES (";
				if (doPrepared)
					strValues += "?";
				else
					strValues += formatValueForUpdate (value, f);

				// set the dirty flag
				isDirty = true;
			}
		}

		// if not dirty, no insert
		if (!isDirty)
			return "";

		// else add the values clause
		else
		{
			sql += ")";
			sql += strValues;
			sql += ")";
		}

		// format into native sql string and return
		return getNativeSQL (con, sql);
	}

	/**
	 * Simple helper func to enable turning on/off nativeSQL
	 */
	private String
	getNativeSQL (
	 Connection con,
	 String sql)
	{
		return sql;
		// return con.nativeSQL (sql);
	}

	/**
	 * builds an SQL update statement
	 * @param dobj the DataObject to build a statement for
	 * @return the SQL update statement
	 * @throws DataSourceException if there is an error from the DataSource.
	 * @throws SQLException if there is a JDBC error.
	 */
	private String
	buildUpdateStatement (
	 Connection con,
	 DataObject dobj,
	 boolean doPrepared)
		throws DataSourceException, SQLException, TypeConversionException
	{
		// must have a DataObject
		Util.argCheckNull (dobj);

		// must have an entity
		Entity ent = dobj.getEntity ();
		Util.argCheckNull (ent);

		// update clause
		String sql = "UPDATE ";
		sql += escapeTableName (ent.getName ());

		// get the fields, must have fields
		Vector fields = ent.getFields ();
		if (fields == null || fields.size () == 0)
			return "";

		// add the fields
		Object value, img;
		Field f;
		boolean isDirty = false;
		for (int i = 0; i < fields.size (); i++)
		{
			// get the next field
			f = (Field)fields.elementAt (i);
			Util.argCheckNull (f);

			// convert the value
			value = Type.convertValueOnType (dobj.getObjectValue (f.getName ()),
				f.getType ());

			//get the image value
			img = Type.convertValueOnType (dobj.getImageValue (f.getName ()),
				f.getType ());

			// add the field name to sql if not equal
			if (!valuesEqual (value, img))
			{
				sql += isDirty? ", ": " SET ";
				sql += escapeFieldName (f.getName ());
				sql += " = ";
				if (doPrepared && !Type.isValueEmpty (value))
					sql += "?";
				else
					sql += formatValueForUpdate (value, f);

				// set the dirty flag
				isDirty = true;
			}
		}

		// if not dirty, no update
		if (!isDirty)
			return "";

		// else add the where clause
		else
		{
			// get the key clause
			String strWhere = getKeyClause (dobj);

			// must have a where clause
			if (strWhere.length () > 0)
				sql += " WHERE " + strWhere;
			else
				return "";
		}

		// format into native sql string and return
		return getNativeSQL (con, sql);
	}

	/**
	 * Returns true if 2 values are equal
	 * @param val1 the first value
	 * @param val2 the second value
	 * @return true if the values are equal
	 */
	private static boolean
	valuesEqual (
	 Object val1,
	 Object val2)
	{
		// if both null, then equal
		if (val1 == null && val2 == null)
			return true;

		// 1 param is null, then not equal
		else if (val1 == null || val2 == null)
			return false;

		// else compare strings
		else if (val1.toString ().equals (val2.toString ()))
			return true;

		// else, compare
		else
			return val1.equals (val2);
	}

	/**
	 * Returns a unique key clause
	 *
	 */
	private String
	getKeyClause (
	 DataObject dobj)
		throws TypeConversionException
	{
		// validate
		Util.argCheckNull (dobj);

		// get the entity
		Entity ent = dobj.getEntity ();

		// have we got a sys key
		String sql = "";
		Object val;
		Field f;
		String sysKey = dobj.getSystemKey (true);
		if (sysKey != null && sysKey.length () > 0)
		{
			// get the sys key field
			f = ent.getSystemKeyField ();
			if (f != null)
			{
				// get the image value
				val = dobj.getImageValue (f.getName ());
				val = Type.convertValueOnType (val, f.getType ());

				// if not null build where clause
				if (val != null)
				{
					String strVal = getNative ().useParamUpdateCriteria ()? "?":
						formatValueForUpdate (val, f);
					sql = escapeFieldName (f.getName ()) + " = " + strVal;
				}
			}
		}

		// else use the unique keys
		if (sql.length () == 0)
		{
			// iterate the key fields
			Vector keyFields = ent.getUniqueKeyFields ();
			for (int i = 0; keyFields != null && i < keyFields.size (); i++)
			{
				// get the field
				f = (Field)keyFields.elementAt (i);
				if (f != null)
				{
					// get the image value
					val = dobj.getImageValue (f.getName ());
					val = Type.convertValueOnType (val, f.getType ());

					// if not null build where clause
					if (val != null)
					{
						if (sql.length () > 0)
							sql += " AND ";
						String strVal = getNative ().useParamUpdateCriteria ()? "?":
							formatValueForUpdate (val, f);
						sql += escapeFieldName (f.getName ()) + " = " + strVal;
					}
				}
			}
		}

		// else use all fields
		if (sql.length () == 0)
		{
			// iterate the key fields
			Vector keyFields = ent.getFields ();
			for (int i = 0; keyFields != null && i < keyFields.size (); i++)
			{
				// get the field
				f = (Field)keyFields.elementAt (i);
				if (f != null)
				{
					// get the image value
					val = dobj.getImageValue (f.getName ());
					val = Type.convertValueOnType (val, f.getType ());

					// if not null build where clause
					if (val != null)
					{
						if (sql.length () > 0)
							sql += " AND ";
						String strVal = getNative ().useParamUpdateCriteria ()? "?":
							formatValueForUpdate (val, f);
						sql += escapeFieldName (f.getName ()) + " = " + strVal;
					}
				}
			}
		}

		// return
		return sql;
	}

	/**
	 * builds an SQL delete statement
	 * @param dobj the DataObject to build a statement for
	 * @return the SQL delete statement
	 * @throws DataSourceException if there is an error from the DataSource.
	 * @throws SQLException if there is a JDBC error.
	 */
	private String
	buildDeleteStatement (
	 Connection con,
	 DataObject dobj)
		throws DataSourceException, SQLException, TypeConversionException
	{
		// must have a DataObject
		Util.argCheckNull (dobj);

		// must have an entity
		Entity ent = dobj.getEntity ();
		Util.argCheckNull (ent);

		// update clause
		String sql = "DELETE FROM ";
		sql += escapeTableName (ent.getName ());

		// get the fields, must have fields
		Vector fields = ent.getFields ();
		if (fields == null || fields.size () == 0)
			return "";

		// else add the where clause
		else
		{
			// get the key clause
			String strWhere = getKeyClause (dobj);

			// must have a where clause
			if (strWhere.length () > 0)
				sql += " WHERE " + strWhere;
			else
				return "";
		}

		// format into native sql string and return
		return getNativeSQL (con, sql);
	}

	/**
	 * builds an SQL select statement
	 * @param query Query information
	 * @return the SQL select statement
	 * @throws DataSourceException if there is an error from the DataSource.
	 * @throws SQLException if there is a JDBC error.
	 */
	private String
	buildSelectStatement (
	 Connection con,
	 Query query,
	 boolean doPreparedStatement)
		throws DataSourceException, SQLException, TypeConversionException
	{
		// validate
		Util.argCheckNull (query);

		// build select clause
		String sql = "SELECT ";

		// get the query classes
		Enumeration entities = query.getQueryEntities ();
		if (entities == null || !entities.hasMoreElements ())
			//throw new RuntimeException ("No classes set into query");
			throw new RuntimeException (ERR_NO_CLASSES.getText ());

		// add the fields
		String strFields = "";
		String strEntity;
		Entity ent;
		Field f;
		Vector fields;
		while (entities.hasMoreElements ())
		{
			// get the entity name
			strEntity = (String) entities.nextElement ();

			// get the entity
			ent = getEntity (strEntity);

			if (ent != null)
			{
				// iterate the entity fields
				fields = ent.getFields ();
				for (int i = 0; fields != null && i < fields.size (); i++)
				{
					// get the field
					f = (Field) fields.elementAt (i);

					// add field if not excluded in query
					if (!query.isFieldExcluded (ent.getName (), f.getName ()))
					{
						// add the comma
						if (strFields.length () > 0)
							strFields += ", ";

						// add the field
						strFields += escapeTableName (ent.getName ()) + "."
							+ escapeFieldName (f.getName ());
					}
				}
			}
		}

		// must have entities and fields
		if (strFields.length () == 0)
			//throw new RuntimeException ("No fields in query");
			throw new RuntimeException (ERR_NO_FIELDS.getText ());

		// add entities and fields
		StringBuffer whereClause = new StringBuffer ();
		sql += " " + strFields;
		String strEntities = buildJoinClause (query, whereClause);
		sql += " FROM " + strEntities;

		// add the criteria clause
		String strCriteria = buildCriteriaClause (query, doPreparedStatement);
		if (strCriteria.length () > 0)
		{
			if (whereClause.length () == 0)
				whereClause.append (" WHERE ");
			else
				whereClause.append (" AND ");
			whereClause.append (strCriteria);
		}
		if (whereClause.length () > 0)
			sql += whereClause.toString ();

		// add the sorts
		String strSorts = buildSortClause (query);
		if (strSorts.length () > 0)
			sql += " ORDER BY " + strSorts;

		// format into native sql string and return
		return getNativeSQL (con, sql);
	}

	/**
	 * Inner class for join building
	 */
	private class JoinSubClause
	{
		/**
		 * attributes
		 */
		public Vector _joins = null;
		public String _strJoin = "";
		public String _excludeEntity = "";
		public Join _excludeJoin = null;

		/**
		 * Constructor taking joins vector
		 * @param joins Vector of joins
		 */
		public
		JoinSubClause (
		 Vector joins)
		{
			_joins = joins;
		}

		/**
		 * Set the exclude entity
		 * @param excludeEntity the entity to exclude from the join string
		 */
		public void
		setExcludeEntity (
		 String excludeEntity)
		{
			_excludeEntity = excludeEntity;
		}

		/**
		 * Set the exclude join
		 * @param excludeJoin the join to exclude from the join string
		 */
		public void
		setExcludeJoin (
		 Join excludeJoin)
		{
			_excludeJoin = excludeJoin;
		}

		/**
		 * Build and return the join string
		 * @return String the join sub-clause string
		 */
		public String
		getJoinString ()
		{
			// must have joins
			if (_joins == null || _joins.size () == 0)
				return "";

			// if already built return
			if (_strJoin.length () > 0)
				return _strJoin;

			// build the clause
			Join jn;
			Entity entLeft, entRight;
			for (int i = 0; _joins != null && i < _joins.size (); i++)
			{
				// get the join
				jn = (Join)_joins.elementAt (i);
				if (jn != null)
				{
					// ignore exclude entity
					if (_excludeEntity.length () > 0 && _excludeEntity.equalsIgnoreCase (jn.getRightEntity ()))
						continue;

					// get the entities
					entLeft = getEntity (jn.getLeftEntity ());
					entRight = getEntity (jn.getRightEntity ());

					// add the join
					if (entLeft != null && entRight != null)
					{
						// add the left join entity
						_strJoin += (_strJoin.length () == 0)? escapeTableName (entLeft.getName ()) : " ";
						_strJoin = " (" + _strJoin;

						// add the join type
						_strJoin += " " + jn.getJoinType () + " ";

						// add the right join entity
						_strJoin += escapeTableName (entRight.getName ());

						// add the join criteria
						_strJoin += " ON " + escapeTableName (entLeft.getName ()) + "." + jn.getLeftKey () + " = " +
							escapeTableName (entRight.getName ()) + "." + jn.getRightKey ();

						_strJoin += ")";
					}
				}
			}

			// fixme, hack.
			// in the case that there is one join and it is also the excludejoin
			// then this fn will return "" when what is needed is the join child
			// entity name. This logic is probably not correct.
			// check for empty,
			if (_strJoin.length () <= 0)
				if (_excludeJoin != null && _excludeJoin.getLeftEntity () != null)
				{
					// empty, but we have an exclude join, use the child entity
					_strJoin = escapeTableName (_excludeJoin.getLeftEntity ());
				}
				else
					Log.debug ("fixme, warning, JoinSubClause.getJoinString () is empty and there is no _excludeJoin");

			// return join sub-clause
			return _strJoin;
		}
	}

	/**
	 * Returns a sql criteria clause (minus the WHERE)
	 * @param query the Query object
	 * @return String the sql clause
	 */
	private String
	buildCriteriaClause (
	 Query query,
	 boolean doPreparedStatement)
		throws TypeConversionException
	{
		// validate
		Util.argCheckNull (query);

		// iterate the qcs
		StringBuffer sql = new StringBuffer ("");
		QueryCriterium qc;
		Field f;
		Entity ent;
		Object value;
		Vector qcs = query.getCriteria ();
		for (int i = 0; qcs != null && i < qcs.size (); i++)
		{
			// get the qc, entity and field
			qc = (QueryCriterium)qcs.elementAt (i);
			Util.argCheckNull (qc);
			ent = getEntity (qc._entityName);
			Util.argCheckNull (ent);
			f = ent.getField (qc._fieldName);
			Util.argCheckNull (f);

			// convert the value on type
			value = Type.convertValueOnType (qc._value, f.getType ());

			// only add if criterium is unary or not empty
			if (qc.isUnary () || !qc.isValueEmpty (value))
			{
				// add the AND
				if (sql.length () > 0)
					sql.append (" AND ");

				// add a bracket if or is null
				if (qc.orIsNull ())
					sql.append (" (");

				// column
				sql.append (escapeTableName (ent.getName ()) + "." +
					escapeFieldName (qc._fieldName));

				// operator
				sql.append (" " + qc._op);

				// value (if binary)
				if (qc.isBinary ())
				{
					sql.append (" ");
					sql.append (doPreparedStatement ? "?" :
						formatValueForQuery (qc, f, value));
				}

				// or is null
				if (qc.orIsNull ())
				{
					sql.append (" OR " + escapeTableName (ent.getName ()) + "." +
						escapeFieldName (qc._fieldName) +
						" " + QueryCriterium.OP_IS_NULL + ")");
				}
			}
		}

		// return
		return sql.toString ();
	}

	/**
	 * Returns a sql sort clause
	 * @param query the Query object
	 * @return String the sql clause
	 */
	private String
	buildSortClause (
	 Query query)
	{
		// validate
		Util.argCheckNull (query);

		// iterate the sorts
		String sql = "";
		Sort s;
		Entity ent;
		Vector sorts = query.getSorts ();
		for (int i = 0; sorts != null && i < sorts.size (); i++)
		{
			// get the sort
			s = (Sort)sorts.elementAt (i);
			if (s != null)
			{
				// add the ORDER BY or AND
				sql += (sql.length () == 0)? "": ", ";

				// get the entity
				ent = getEntity (s._entityName);
				if (ent != null)
				{
					// add the sort
					sql += escapeTableName (ent.getName ()) + "." +
						escapeFieldName (s._fieldName) + " " + s._direction;
				}
			}
		}

		// return
		return sql;
	}

	/**
	 * Build a RecordSet from a java.sql.ResultSet
	 * @param query the Query object
	 * @param recset the RecordSet to build
	 * @param rs the java.sql.ResultSet to build from
	 * @return void
	 * @throws SQLException if there is a JDBC error.
	 * @throws ClassNotFoundException, IllegalAccessException or
	 *   InstantiationException if the conceete data object cannot be created.
	 */
	private void
	buildRecordSet (
	 Query query,
	 RecordSet recset,
	 ResultSet rs)
		throws SQLException, ClassNotFoundException, IllegalAccessException,
			   InstantiationException
	{
		// validate params
		Util.argCheckNull (query);
		Util.argCheckNull (recset);
		Util.argCheckNull (rs);

		// get the resultset meta data
		ResultSetMetaData md = rs.getMetaData ();

		// get the query classes
		int numEntities = query.getNumberQueryEntities ();
		if (numEntities <= 0)
			//throw new RuntimeException ("No classes set into query");
			throw new RuntimeException (ERR_NO_CLASSES.getText ());
		boolean isJoin = numEntities > 1;

		// iterate the resultset
		DataObject dobj;
		Row row = null;
		Entity ent;
		Field f;
		Vector fields;
		String className;
		Class c;
		Object val = null;
		String entityName;
		int rsIndex;
		while (rs.next ())
		{
			// create a join if necessary
			if (isJoin)
				row = new Row ();

			// iterate the classes
			rsIndex = 1;
			Enumeration entities = query.getQueryEntities ();
			while (entities.hasMoreElements ())
			{
				// get the class and entity
				entityName = (String)entities.nextElement ();
				ent = getEntity (entityName);
				if (ent != null)
				{
					// get the default class
					c = ent.getDefaultClass ();

					// if no default class get class from resultset
					int classIndex = -1;
					if (c == null)
					{
						classIndex = ent.getFieldIndex (CLASS_COLUMN_NAME);
						if (classIndex >= 0)
						{
							// adjust for previous entities
							classIndex += rsIndex;
							String strClass = rs.getString (classIndex);
							if (strClass != null && strClass.length () > 0)
								c = Class.forName (strClass);
						}
					}

					// create a new data object
					if (c != null)
					{
						dobj = (DataObject)c.newInstance ();
						dobj.setState (DataObject.IN_DB);

						// slight fudge, specifically for wsl.mdn.dataview.Record
						// (but does not reference that class)
						// if the dataobject has not been inited and cannot
						// ever do this because its entityName is not defined
						// then explicitly set the entity
						if (!dobj.initDone () && dobj.getEntityName () == null)
							dobj.setEntity (ent);
					}
					else
						continue;

					// iterate the entity fields
					fields = ent.getFields ();
					for (int i = 0; fields != null && i < fields.size (); i++)
					{
						// get the field
						f = (Field)fields.elementAt (i);

						//  and skip any excluded fields
						if (!query.isFieldExcluded (ent.getName (), f.getName ()))
						{
							// don't try to read the class field again
							if (classIndex != rsIndex)
							{
								// get the object class
								String strClass = md.getColumnClassName (rsIndex);

								// get the resultset value
								val = getResultSetValue (strClass, rs, rsIndex);

								// check if we need unicode conversion
								if (val != null && val instanceof String)
								{
									// get type name
									String typeName = md.getColumnTypeName (rsIndex);

									if (!Util.isEmpty (typeName)
										&& getNative ().shouldUnicodeConvert (typeName))
									{
										// conversion is required, do the convert
										val = unicodeConvert ((String) val);
									}
								}

								// set it into the appropriate field of the data object
								if (val != null)
									dobj.setValue (f.getName (), val);
							}

							//increment recordset index (only done if field is
							// not excluded)
							rsIndex++;
						}
					}

					// set the image
					dobj.setImage ();

					// if a join add to the row
					if (isJoin)
						row.addComponent (dobj);

					// else add directly to the RecordSet
					else
						recset.addRow (dobj);
				}
			}

			// if a join add the row
			if (isJoin)
				recset.addRow (row);
		}
	}

	//--------------------------------------------------------------------------
	/**
	 * Convert a string with unicode represented as hex to unicode characters.
	 * @param src, the source string in hex.
	 * @return the converted chars.
	 */
	public static String
	unicodeConvert (
	 String src)
	{
		// convert to proper unicode
		StringBuffer buf = new StringBuffer ();
		int maxIndex = src.length () - 4;

		// unicode char is comprises 4 hex digits, low byte first
		try
		{
			for (int index = 0; index <= maxIndex; index += 4)
			{
				int low = Integer.parseInt (src.substring (index, index + 2), 16);
				int hi  = Integer.parseInt (src.substring (index + 2, index + 4), 16);
				buf.append ((char) (low + hi * 256));
			}
		}
		catch (NumberFormatException e)
		{
			// could not convert, return original string
			return src;
		}

		return buf.toString ();
	}

	/**
	 * Get the value from the resultset based on field class
	 * @param strClass class of column
	 * @param ResultSet the resultset to get the value from
	 * @param inde the index of the column in the resultset
	 * @return Object the value
	 */
	private Object
	getResultSetValue (
	 String strClass,
	 ResultSet rs,
	 int rsIndex)
	{
		// get the resultset value
		Object val = null;
		try
		{
			// get it from the delegate
			val = getNative ().getResultSetValue (strClass, rs, rsIndex);

			// if not found get it by class
			if (val == null)
			{
				// switch on class
				if (strClass == null || strClass.length () == 0)
					val = rs.getObject (rsIndex);
				else if (strClass.equals (java.lang.String.class.getName ()))
					val = rs.getString (rsIndex);
				else if (strClass.equals (java.lang.Boolean.class.getName ()))
					val = new Boolean (rs.getBoolean (rsIndex));
				else if (strClass.equals (java.lang.Byte.class.getName ()))
					val = new Integer (rs.getByte (rsIndex));
				else if (strClass.equals (java.lang.Character.class.getName ()))
					val = rs.getString (rsIndex);
				else if (strClass.equals (java.lang.Double.class.getName ()))
					val = new Double (rs.getDouble (rsIndex));
				else if (strClass.equals (java.lang.Float.class.getName ()))
					val = new Float (rs.getFloat (rsIndex));
				else if (strClass.equals (java.lang.Integer.class.getName ()))
					val = new Integer (rs.getInt (rsIndex));
				else if (strClass.equals (java.lang.Long.class.getName ()))
					val = new Long (rs.getInt (rsIndex));
				else if (strClass.equals (java.lang.Number.class.getName ()))
					val = new Double (rs.getDouble (rsIndex));
				else if (strClass.equals (java.lang.Short.class.getName ()))
					val = new Integer (rs.getInt (rsIndex));
				else if (strClass.equals (java.math.BigDecimal.class.getName ()))
					val = rs.getBigDecimal (rsIndex);
				else if (strClass.equals (java.sql.Date.class.getName ()))
					val = rs.getDate (rsIndex);
				else if (strClass.equals (java.sql.Time.class.getName ()))
					val = rs.getTime (rsIndex);
				else if (strClass.equals (java.sql.Timestamp.class.getName ()))
					val = rs.getTimestamp (rsIndex);
				else
					val = rs.getObject (rsIndex);
			}
		}
		catch (SQLException e)
		{
		}

		// return
		return val;
	}

	/**
	 * format a value for an SQL update statement
	 * @param value the converted value to format
	 * @return String the formatted value
	 */
	private String
	formatValueForUpdate (
	 Object value, Field f)
		throws TypeConversionException
	{
		// null
		if (value == null || f == null)
			return "NULL";

		// switch on field type
		switch (f.getType ())
		{
			case Field.FT_STRING:
			{
				assert value instanceof String;
				return "'" + escapeSingleQuote ((String)value) + "'";
			}
			case Field.FT_BOOLEAN:
			{
				assert value instanceof Boolean;
				return String.valueOf (Type.objectToInt (value));
			}
			case Field.FT_DATETIME:
			{
				assert value instanceof java.sql.Timestamp;

				// remove decimal seconds
				java.sql.Timestamp sqlDate = (java.sql.Timestamp)value;
				String sqlDateString = sqlDate.toString ();
				sqlDateString = sqlDateString.substring (0, sqlDateString.indexOf ("."));

				// return the string form of the sql timestemp
				return "'" + sqlDateString + "'";
			}
			default:
			{
				// return the string value
				return Type.objectToString (value);
			}
		}
	}

	/**
	 * Escape a single quote (') in a string by changing it to a pair of single
	 * quotes ('') so that SQL can parse it properly.
	 * @param val, the string which may contain single quotes that need escaping.
	 * @return the string with any single quotes converted to a pair of quotes.
	 */
	private static String
	escapeSingleQuote (
	 String val)
	{
		final char SINGLE_QUOTE = '\'';

		// exit early if no single quotes to escape
		if (val.indexOf (SINGLE_QUOTE) == -1)
			return val;

		StringBuffer buf = new StringBuffer ();
		char        c;

		// iterate over chars in input string
		for (int i = 0; i < val.length (); i++)
			{
				// copy the char
				c = val.charAt (i);
				buf.append (c);

				// if it is a single quote the double it
				if (c == SINGLE_QUOTE)
					buf.append (c);
			}

		// return the escaped string
		return buf.toString ();
	}

	/**
	 * Format a criterium value for an sql query statement
	 * @param qc the QueryCriterium to format
	 * @param f the Field to specify type
	 * @param value the converted value
	 * @return String the formatted criterium value
	 */
	private String
	formatValueForQuery (
	 QueryCriterium qc,
	 Field f,
	 Object value)
		throws TypeConversionException
	{
		// validate
		Util.argCheckNull (qc);
		Util.argCheckNull (qc._value);
		Util.argCheckNull (f);

		// convert to string
		String strVal = Type.objectToString (value);

		// switch on field type
		switch (f.getType ())
		{
			// string
			case Field.FT_STRING:
			{
				strVal = escapeSingleQuote (strVal);
				if (qc._op.equals (QueryCriterium.OP_LIKE)
					|| qc._op.equals (QueryCriterium.OP_NOT_LIKE))
					return "'" + strVal + "%'";
				else
					return "'" + strVal + "'";
			}

			// date
			case Field.FT_DATETIME:
			{
				assert value instanceof java.sql.Timestamp;

				// create an sql timestemp
				java.sql.Timestamp sqlDate = (java.sql.Timestamp)value;
				String sqlDateString = sqlDate.toString ();
				sqlDateString = sqlDateString.substring (0, sqlDateString.indexOf ("."));

				// return the string form of the sql timestemp
				return "'" + sqlDateString + "'";
			}
		}

		// return strVal
		return strVal;
	}

	/**
	 * Set the prepared statement parameters into the PreparedStatement
	 * @param stmt the PreparedStatement to set params into
	 * @param query the Query object that provides the param values
	 * @throws SQLException if the re is a JDBC error.
	 */
	private void
	setPreparedParameters (
	 PreparedStatement stmt,
	 Query query)
		throws SQLException, TypeConversionException
	{
		// validate
		Util.argCheckNull (stmt);
		Util.argCheckNull (query);

		// iterate the qcs
		String sql = "";
		QueryCriterium qc;
		Field f;
		Entity ent;
		Object value;
		int paramNum = 1;
		Vector qcs = query.getCriteria ();
		for (int i = 0; qcs != null && i < qcs.size (); i++)
		{
			// get the qc
			qc = (QueryCriterium)qcs.elementAt (i);
			Util.argCheckNull (qc);
			ent = getEntity (qc._entityName);
			Util.argCheckNull (ent);
			f = ent.getField (qc._fieldName);
			Util.argCheckNull (f);

			// convert the value
			value = Type.convertValueOnType (qc._value, f.getType ());

			// only add params for non-empty criteria
			if (!qc.isValueEmpty (value))
			{
				Object val = value;
				// first do special handling for LIKE wildcards
				if (f.getType () == Field.FT_STRING)
				{
					// do not use formatValueForQuery here as it
					// adds single quotes around the string which
					// is not correct for prepared statements
					assert val instanceof String;
					String strVal = (String)val;
					if (qc._op.equals (QueryCriterium.OP_LIKE)
						|| qc._op.equals (QueryCriterium.OP_NOT_LIKE))
						strVal = strVal + "%";
					val = strVal;
				}

				// now set the prepared param
				setPreparedParam (stmt, f, val, paramNum);

				// as we have set a parameter inc the paramNum
				paramNum++;
			}
		}
	}

	//--------------------------------------------------------------------------
	/**
	 * Excecute an insert, update, delete or DDL command from raw sql.
	 * @param sql, the raw sql defining the update operation.
	 * @return the number or records updates or zero for DDL commands.
	 */
	public int
	rawExecuteUpdate (
	 String sql)
		throws DataSourceException
	{
		// validate
		Util.argCheckEmpty (sql);
		int rv = 0;

		Connection con = null;

		try
		{
			// log, convert to native and execute the sql
			sql = unescapeRawString (sql);
			Log.debug (sql);
			con = getConnection ();
			String nativeSql = getNativeSQL (con, sql);
			Statement stmt = getUpdateStatement (con);
			rv = stmt.executeUpdate (sql);
			stmt.close ();
		}
		catch (SQLException e)
		{
			// error, convert to DataSourceException and throw
			throw new DataSourceException ("JdbcDataSource.rawExecuteUpdate: "
				+ e.toString () + ", " + e.getSQLState ());
		}
		finally
		{
			// return the connection to the pool
			if (con != null)
				try
				{
					con.close ();
				}
				catch (SQLException e)
				{
				}
		}

		return rv;
	}

	//--------------------------------------------------------------------------
	// escape a raw table name

	public static String
	escapeRawTableName (
	 String tableName)
	{
		return REM_TABLE_ESC + tableName + REM_TABLE_ESC;
	}

	/**
	 * Unescape a raw string
	 */
	private String
	unescapeRawString (
	 String sql)
	{
		// find the escape
		int posLeft = sql.indexOf (REM_TABLE_ESC);
		if (posLeft >= 0)
		{
			int lenEsc = REM_TABLE_ESC.length ();
			int posRight = sql.indexOf (REM_TABLE_ESC, posLeft + lenEsc);
			if (posRight >= 0)
			{
				String left = sql.substring (0, posLeft);
				String table = sql.substring (posLeft + lenEsc, posRight);
				table = escapeTableName (table);
				String right = sql.substring (posRight + lenEsc);
				return left + table + right;
			}
		}
		return sql;
	}

	//--------------------------------------------------------------------------
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
		throws DataSourceException
	{
		Util.argCheckEmpty (entityName);

		// drop if required
		try
		{
			String deleteSql = "DROP TABLE " + escapeTableName (entityName);
			rawExecuteUpdate (deleteSql);
		}
		catch (DataSourceException e)
		{
			;
		}
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
	public void
	createEntityTable (
	 Entity ent,
	 boolean deleteFirst)
		throws DataSourceException
	{
		Util.argCheckNull (ent);

		// delete if required
		if (deleteFirst)
		{
			try
			{
				String deleteSql = "DROP TABLE " + escapeTableName (ent.getName ());
				rawExecuteUpdate (deleteSql);
			}
			catch (DataSourceException e)
			{
				;
			}
		}

		// create the table
		String createSql = "CREATE TABLE " + escapeTableName (ent.getName ());


		// add the field specifications
		boolean bFirstField = true;
		Vector  fields      = ent.getFields ();
		for (int i = 0; i < fields.size (); i++)
		{
			// handle separating comman and first field
			if (bFirstField)
			{
				createSql += " (\n";
				bFirstField = false;
			}
			else
				createSql += ", \n";

			// get the field
			Field f = (Field) fields.get (i);

			// add field name
			createSql += "  " + escapeFieldName (f.getName ()) + " ";

			// add field type
			switch (f.getType ())
			{
				case Field.FT_STRING   :
				{
					int size = (f.getColumnSize () > 0)? f.getColumnSize (): 50;
					if (size > 255)
						createSql += getNative ().getMemoType (size);
					else
						createSql += getNative ().getVarcharType (size);
					break;
				}

				case Field.FT_BOOLEAN  :
				case Field.FT_INTEGER  :
					createSql += "INTEGER";
					break;

				case Field.FT_DECIMAL  :
					createSql += getNative ().getDecimalType ();
					break;

				case Field.FT_DATETIME :
					createSql += getNative ().getDateTimeType ();
					break;

				case Field.FT_CURRENCY :
					createSql += getNative ().getDecimalType ();
					break;

				default :
					Log.fatal (ERR_UNKNOWN_FIELD_TYPE + ent.getName () + ","
						+ f.getName () + "," + f.getType ());
					break;
			}

			// if a system key make unique
			if (f.hasFlag (Field.FF_SYSTEM_KEY))
				createSql += " NOT NULL UNIQUE";
		}

		// add constraints
		createSql += getNative ().getTableConstraintSQL (ent, fields);

		// add final paren
		createSql += "\n)";

		// execute the sql
		rawExecuteUpdate (createSql);
	}

	/**
	 * Check for spaces in the entity name, and add escape chars if necessary.
	 */
	public String
	escapeTableName (
	 String entName)
	{
		// square brackets for spaces
		if (entName.indexOf (' ') >= 0 || entName.indexOf ('-') >= 0)
			return "\"" + entName + "\"";

		// schema separator replacement
		final char DEFAULT_SEP = '.';
		if (getNative ().doReplaceDotSeparator ())
		{
			char replaceSep = getNative ().getReplaceDotSeparator ();
			int sepCount = entName.indexOf (DEFAULT_SEP);
			if (sepCount >= 0 && replaceSep != DEFAULT_SEP)
				entName = entName.replace (DEFAULT_SEP, replaceSep);
		}

		// return
		return entName;
	}

	/**
	 * Allow the native delegate to impose its limits on the fieldname
	 */
	public String
	escapeFieldName (
	 String name)
	{
		return getNative ().mungFieldName (name);
	}

	//--------------------------------------------------------------------------
	/**
	 * Import a the names of tables from the datasource.
	 * @return a Vector of EntitySchemaNamess containing the table names.
	 */
	public Vector
	importTableNames ()
		throws DataSourceException
	{
		// connect
		Vector tableNames = new Vector ();
		Connection conn = null;
		boolean isError = false;

		try
		{
			// get connection
			conn = getConnection ();

			// get db meta data
			DatabaseMetaData md = conn.getMetaData ();
			String prod = md.getDatabaseProductName ();
			Log.debug ("Database Product Name = " + prod);

			// get tables
			ResultSet rs = md.getTables (null, null, null, new String[] {"TABLE", "VIEW"});
			while (rs != null && rs.next ())
			{
				String tableName  = null;
				String schemaName = null;

				try
				{
					// get the table name and schema
					tableName = getNative ().getTableName (rs);
					schemaName = rs.getString ("TABLE_SCHEM");
					// cat = rs.getString ("TABLE_CAT");
					// tableType = rs.getString ("TABLE_TYPE");

				}
				catch (Exception e)
				{
					// e.printStackTrace();
					// do nothing, expect getting schema to fail for some DBs
				}

				// add schema dot
				String fullTableName = (schemaName == null) ? tableName
					: schemaName + "." + tableName;

				// add it to the vector
				tableNames.add (new EntitySchemaName (fullTableName,
					schemaName, tableName));
			}

			// close the result set
			rs.close ();

		}
		catch (Exception e)
		{
			e.printStackTrace();
			isError = true;
			throw new RuntimeException ("JdbcDataSource.importDefinition: " + e.toString ());
		}
		finally
		{
			// return the connection to the pool
			if (conn != null)
				try
				{
					// if error and pooled pooled close the actual connection
					if (isError && conn instanceof PooledConnection)
						 ((PooledConnection)conn).close (true);
					else
						conn.close ();
				}
				catch (SQLException e)
				{
					e.printStackTrace();
				}
		}

		return tableNames;
	}

	//--------------------------------------------------------------------------
	/**
	 * Import an entity definition from the datasource.
	 * @param esn, a EntitySchemaName defining the entity to import.
	 * @return the imported entity definition.
	 */
	public Entity
	importEntityDefinition (
	 EntitySchemaName esn)
		throws DataSourceException
	{
		Util.argCheckNull (esn);

		Connection conn = null;
		boolean isError = false;

		try
		{
			Vector  pKeys       = new Vector ();
			boolean doPrimaries = true;

			// get connection
			conn = getConnection ();

			// get db meta data
			DatabaseMetaData md = conn.getMetaData ();


			// create the entity
			Entity ent = new EntityImpl ();
			ent.setName (esn.getFullTableName ());

			// get the primary keys for the table
			try
			{
				if (doPrimaries)
				{
					ResultSet keys = md.getPrimaryKeys (null, esn.getSchemaName (),
						esn.getTableName ());
					while (pKeys != null && keys.next ())
						pKeys.add (getStringCol (keys, "COLUMN_NAME", 4));
					keys.close ();
				}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				doPrimaries = false;
			}

			// get the columns for the table
			ResultSet columns = md.getColumns (null, esn.getSchemaName (),
				esn.getTableName (), null);
			TableRSMD trsmd = null;

			while (columns != null && columns.next ())
			{
				// get the col attribs
				String colName = getStringCol (columns, "COLUMN_NAME", 4);
				int colSize = getIntCol (columns, "COLUMN_SIZE", 7);
				if (colSize > MAX_COL_SIZE)
					colSize = MAX_COL_SIZE;
				int numDigits = getIntCol (columns, "DECIMAL_DIGITS", 9);
				int nativeType = getIntCol (columns, "DATA_TYPE", 5);

				// check for null type, if null use result set meta data
				// to get type
				if (nativeType == java.sql.Types.NULL)
				{
					// ensure we have the meta data
					if (trsmd == null)
						trsmd = new TableRSMD (ent.getName ());

					nativeType = trsmd.getColType (colName);
				}

				int type = convertSQLType (nativeType, numDigits);
				if (type < 0) // abort condition
					continue;

				// create a field and add it to the entity
				Field col = new FieldImpl ();
				col.setName (colName);
				col.setType (type);
				col.setNativeType (nativeType);
				col.setFlags (Field.FF_NONE);
				col.setColumnSize (colSize);
				col.setDecimalDigits (numDigits);

				// primary key
				if (doPrimaries)
				{
					if (pKeys.indexOf (colName) != -1)
						col.setFlags (col.getFlags () | Field.FF_UNIQUE_KEY);
				}

				ent.addField (col);
			}
			// close result set and meta data
			columns.close ();

			return ent;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			isError = true;
			throw new RuntimeException ("JdbcDataSource.importDefinition: " + e.toString ());
		}
		finally
		{
			// return the connection to the pool
			if (conn != null)
				try
				{
					// if error and pooled pooled close the actual connection
					if (isError && conn instanceof PooledConnection)
						 ((PooledConnection)conn).close (true);
					else
						conn.close ();
				}
				catch (SQLException e)
				{
					e.printStackTrace();
				}
		}
	}

	//--------------------------------------------------------------------------
	/**
	 * Inner class used to get result set meta data and extract column types.
	 */
	private class TableRSMD
	{
		private Hashtable _colTypes;
		/**
		 * Constructor.
		 * @param tableName, the name of the table to get meta data (column
		 *   types) for.
		 */
		public
		TableRSMD (
		 String tableName)
		{
			_colTypes = new Hashtable ();

			Statement  stmt      = null;
			ResultSet  rs        = null;
			Connection con       = null;
			boolean    isError   = false;

			try
			{
				// get connection
				con = getConnection ();

				// build sql for do-nothing query so we can get metat data from
				// the result set
				String sql = "SELECT * FROM " + escapeTableName (tableName) + " WHERE 0=1";
				String nativeSql = getNativeSQL (con, sql);

				// make statement and execute query
				stmt = con.createStatement ();
				rs = stmt.executeQuery (nativeSql);

				if (rs != null)
				{
					// get the meta data and iterate putting column types in table
					ResultSetMetaData rsmd = rs.getMetaData ();

					if (rsmd != null)
						for (int i = 1; i <= rsmd.getColumnCount (); i++)
						{
							String name = rsmd.getColumnName (i);
							int    type = rsmd.getColumnType (i);
							_colTypes.put (name, new Integer (type));
						}
				}
			}
			catch (Exception e)
			{
				// do nothing
				isError = true;
			}
			finally
			{
				try
				{
					if (rs != null)
						rs.close ();
					if (stmt != null)
						stmt.close ();
				}
				catch (Exception e)
				{
				}

				try
				{
					// if error and pooled pooled close the actual connection
					// else just return to pool
					if (con != null)
					if (isError && con instanceof PooledConnection)
						 ((PooledConnection)con).close (true);
					else
						con.close ();
				}
				catch (Exception e)
				{
				}
			}
		}

		/**
		 * Get the sql type info for the specified column. Uses result set
		 * metat data rather than database meta data.
		 * @param columnName, the name of the column.
		 * @return the sql type number for the specified column.
		 */
		public int
		getColType (
		 String columnName)
		{
			if (Util.isEmpty (columnName))
				return java.sql.Types.NULL;

			Integer colType = (Integer) _colTypes.get (columnName);

			return (colType == null) ? java.sql.Types.NULL : colType.intValue ();
		}
	}

	//--------------------------------------------------------------------------
	/**
	 *	Import an Entity definition from a raw SQL select statement.
	 * 	We try to get run a minimal SQL statement and examine the
	 *  meta-data to extract the definitions
	 */
	public Entity
	importRawSelectDefinition (
	 String selectStmt)
		throws DataSourceException
	{
		Entity retEntity = null;
		String sql, sql2;

		// must have a query
		Util.argCheckNull (selectStmt);
		Util.argCheckEmpty (selectStmt);

		// Run thru' keyword replacements and subs with empty string
		sql = Util.strReplace (selectStmt, DirectQuery.UIDKEYWORD, "''");

		// try and find a 'where' clause on the end of the query, and add a criterium that
		// will cause the query to return 0 rows
		if (sql.toLowerCase ().lastIndexOf ("where") > 0)
		{
			// add an 'AND'
			sql2 = sql + " and 0 = 1";
		}
		else // no 'where' clause
		{
			sql2 = sql + " where 0 = 1";
		}

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		boolean isError = false;
		try
		{
			try
			{

				// get connection
				conn = getConnection ();

				stmt = conn.createStatement ();
				rs = stmt.executeQuery (sql2);
			}
			catch (Exception se)
			{
				// this is probably because the added (0 = 1) condition made query invalid, so
				// fallback on executing the whole original query.
				try
				{
					rs = stmt.executeQuery (sql);
				}
				catch (SQLException se2)
				{
					isError = true;
					// the query is invalid
					throw new DataSourceException (ERR_INVALID_SQL_QUERY.getText ());
				}
			}

			if (rs == null)
			{
				isError = true;
				throw new DataSourceException (ERR_QUERY_FAILURE.getText ());
			}

			try
			{
				// get the metadata
				ResultSetMetaData rsmd = rs.getMetaData ();

				// get table name
				Field col;
				retEntity = new EntityImpl ("RawSelect");

				String name;
				int colCount = rsmd.getColumnCount ();
				for (int i = 1; i <= colCount; i++)
				{
					// get the columns for the table
					name = rsmd.getColumnName (i);
					int colSize = rsmd.getColumnDisplaySize (i);
					if (colSize > MAX_COL_SIZE)
						colSize = MAX_COL_SIZE;

					int numDigits = rsmd.getPrecision (i);
					int nativeType = rsmd.getColumnType (i);
					int type = convertSQLType (nativeType, numDigits);
					if (type < 0) // abort condition
						continue;

					col = new FieldImpl (name, type, Field.FF_NONE);
					col.setNativeType (nativeType);
					col.setColumnSize (colSize);
					retEntity.addField (col);
				}

				// close the rs and conn
				rs.close ();

			}
			catch (Exception e)
			{
				isError = true;
				throw new RuntimeException ("JdbcDataSource.importRawSelectDefinition: " + e.toString ());
			}
		}
		finally
		{
			// return the connection to the pool
			if (conn != null)
				try
				{
					// if error and pooled pooled close the actual connection
					if (isError && conn instanceof PooledConnection)
						 ((PooledConnection)conn).close (true);
					else
						conn.close ();
				}
				catch (SQLException e)
				{
				}
		}

		return retEntity;
	}

	/**
	 * Simple helper function that traps and ignores exceptions
	 */
	private String
	getStringCol (
	 ResultSet cols,
	 String colName,
	 int colIndex)
		throws Exception
	{
		return getStringCol (cols, colName, colIndex, true);
	}

	/**
	 * Simple helper function that traps and ignores exceptions if ignoreExceptions == true
	 */
	private String
	getStringCol (
	 ResultSet cols,
	 String colName,
	 int colIndex,
	 boolean ignoreExceptions)
		throws Exception
	{
		String val = null;
		try
		{
			val = cols.getString (colName);
		}
		catch (Exception e)
		{
			if (!ignoreExceptions)
				throw e;
			else
			{
				try
				{
					val = cols.getString (colIndex);
				}
				catch (Exception e2)
				{
					if (!ignoreExceptions)
						throw e2;
				}
			}
		}
		return val;
	}

	/**
	 * Simple helper function that traps and ignores exceptions
	 */
	private int
	getIntCol (
	 ResultSet cols,
	 String colName,
	 int colIndex)
		throws Exception
	{
		return getIntCol (cols, colName, colIndex, true);
	}

	/**
	 * Simple helper function that traps and ignores exceptions if ignoreExceptions == true
	 */
	private int
	getIntCol (
	 ResultSet cols,
	 String colName,
	 int colIndex,
	 boolean ignoreExceptions)
		throws Exception
	{
		int val = 0;
		try
		{
			val = cols.getInt (colName);
		}
		catch (Exception e)
		{
			if (!ignoreExceptions)
				throw e;
			else
			{
				try
				{
					val = cols.getInt (colIndex);
				}
				catch (Exception e2)
				{
					if (!ignoreExceptions)
						throw e;
				}
			}
		}
		return val;
	}

	//--------------------------------------------------------------------------
	/**
	 * Convert an SQL type to a Field type
	 * @param sqlType the SQL type
	 * @return int the Field type, or -1 if invalid or not found
	 */
	private int
	convertSQLType (
	 int sqlType,
	 int decimalDigits)
	{
		int fieldType = -1;

		switch (sqlType)
		{
			case java.sql.Types.NUMERIC:
			case java.sql.Types.DECIMAL:
				fieldType = (decimalDigits == 0)? Field.FT_INTEGER: Field.FT_DECIMAL;
				break;
			case java.sql.Types.BIT:
			case java.sql.Types.TINYINT:
			case java.sql.Types.SMALLINT:
			case java.sql.Types.INTEGER:
			case java.sql.Types.BIGINT:
				fieldType = Field.FT_INTEGER;
				break;
			case java.sql.Types.FLOAT:
			case java.sql.Types.REAL:
			case java.sql.Types.DOUBLE:
				fieldType = Field.FT_DECIMAL;
				break;
			case java.sql.Types.LONGVARCHAR:
			case java.sql.Types.CHAR:
			case java.sql.Types.VARCHAR:
				fieldType = Field.FT_STRING;
				break;
			case java.sql.Types.BINARY:
			case java.sql.Types.VARBINARY:
			case java.sql.Types.LONGVARBINARY:
				break; // JDBC does not cope with this type, so abort this field type

			case java.sql.Types.DATE:
			case java.sql.Types.TIME:
			case java.sql.Types.TIMESTAMP:
				fieldType = Field.FT_DATETIME;
				break;
			default:
				fieldType = Field.FT_STRING;
		}

		// return the field type
		return fieldType;
	}

	//--------------------------------------------------------------------------
	// new join code

	/**
	 * Build the join clause of the select statement
	 * @param query the Query object
	 * @return String the formatted join clause
	 */
	private String
	buildJoinClause (
	 Query query,
	 StringBuffer whereClause)
	{
		// validate
		Util.argCheckNull (query);

		// get the valid joins
		Vector joins = getValidJoins (query);

		// sort the joins
		//joins = sortJoinVector (joins);

		// build the join string
		StringBuffer sql = new StringBuffer ();

		// no joins, or joins in where
		if (hasDbFlag (DBF_JOINS_IN_WHERE) || joins.size () == 0)
		{
			Enumeration entities = query.getQueryEntities ();
			while (entities != null && entities.hasMoreElements ())
			{
				// comma
				if (sql.length () > 0)
					sql.append (", ");

				// entity
				sql.append (escapeTableName ((String)entities.nextElement ()));
			}

			// add the where joins
			if (hasDbFlag (DBF_JOINS_IN_WHERE) && joins.size () > 0)
				buildWhereJoins (joins, whereClause);
		}

		// we have joins
		else
		{
			// recurse with first entity
			Join join = (Join)joins.elementAt (0);
			recurseBuildJoinString (join.getLeftEntity (), joins, sql);
		}


		// return
		return sql.toString ();
	}

	//--------------------------------------------------------------------------
	/**
	 * Build joins into a where clause
	 * @param joins the joins Vector
	 * @param whereClause the where clause string buffer
	 */
	private void
	buildWhereJoins (
	 Vector joins,
	 StringBuffer whereClause)
	{
		// validate
		Util.argCheckNull (joins);
		Util.argCheckNull (whereClause);

		// iterate the joins
		Join join;
		for (int i = 0; i < joins.size (); i++)
		{
			// where / and
			if (whereClause.length () == 0)
				whereClause.append (" WHERE ");
			else
				whereClause.append (" AND ");

			// get the join
			join = (Join)joins.elementAt (i);

			// left
			whereClause.append (escapeTableName (join.getLeftEntity ()));
			whereClause.append (".");
			whereClause.append (escapeFieldName (join.getLeftKey ()));

			// right
			whereClause.append (" = ");
			whereClause.append (escapeTableName (join.getRightEntity ()));
			whereClause.append (".");
			whereClause.append (escapeFieldName (join.getRightKey ()));
		}
	}

	//--------------------------------------------------------------------------
	/**
	 * Recursively build the join string
	 * @param entity the entity to process
	 * @param joins the available joins vector
	 * @param sql the join StringBuffer
	 */
	private void
	recurseBuildJoinString (
	 String entity,
	 Vector joins,
	 StringBuffer sql)
	{
		// validate
		Util.argCheckEmpty (entity);
		Util.argCheckNull (joins);
		Util.argCheckNull (sql);

		// first entity
		if (sql.length () == 0)
			sql.append (escapeTableName (entity));

		// iterate the joins for this entity
		Join join;
		String otherEntity;
		for (int i = 0; i < joins.size (); i++)
		{
			// get the join
			join = (Join)joins.elementAt (i);

			// compare
			if (join != null && join.getLeftEntity ().equals (entity) ||
				join.getRightEntity ().equals (entity))
			{
				// get the other entity
				if (join.getLeftEntity ().equals (entity))
					otherEntity = join.getRightEntity ();
				else
					otherEntity = join.getLeftEntity ();

				// front bracket
				sql.insert (0, " (");

				// operator
				String joinOp = join.getJoinType ();

				// if entity is right entity in join, need to switch outers
				if (join.getRightEntity ().equals (entity))
				{
					if (joinOp.equals (Join.JT_LEFT_OUTER))
					{
						if (!hasDbFlag (DBF_NO_SUPPORTS_ROJ))
							joinOp = Join.JT_RIGHT_OUTER;
						else
							joinOp = Join.JT_INNER;
					}
					else if (joinOp.equals (Join.JT_RIGHT_OUTER))
						joinOp = Join.JT_LEFT_OUTER;
				}
				if (hasDbFlag (DBF_NO_SUPPORTS_OJ))
					joinOp = Join.JT_INNER;

				sql.append (" ");
				sql.append (joinOp);

				// other entity
				sql.append (" ");
				sql.append (escapeTableName (otherEntity));

				// on
				String leftEnt = join.getLeftEntity ();
				String rightEnt = join.getRightEntity ();
				sql.append (" ON ");
				sql.append (escapeTableName (leftEnt));
				sql.append (".");
				sql.append (escapeFieldName (join.getLeftKey ()));
				sql.append (" = ");
				sql.append (escapeTableName (rightEnt));
				sql.append (".");
				sql.append (escapeFieldName (join.getRightKey ()));

				// find others
				Join temp;
				for (int j = i+1; j < joins.size (); j++)
				{
					// find join with same entities involved
					temp = (Join)joins.elementAt (j);
					if ((temp.getLeftEntity ().equals (leftEnt) &&
						temp.getRightEntity ().equals (rightEnt)) ||
						 (temp.getLeftEntity ().equals (rightEnt) &&
						temp.getRightEntity ().equals (leftEnt)))
					{
						sql.append (" AND ");
						sql.append (escapeTableName (temp.getLeftEntity ()));
						sql.append (".");
						sql.append (escapeFieldName (temp.getLeftKey ()));
						sql.append (" = ");
						sql.append (escapeTableName (temp.getRightEntity ()));
						sql.append (".");
						sql.append (escapeFieldName (temp.getRightKey ()));

						// remove
						joins.remove (j);
						j--;
					}
				}
				sql.append (")");

				// remove the join
				joins.remove (i);
				i--;

				// recurse into otherEntity
				recurseBuildJoinString (otherEntity, joins, sql);
			}
		}
	}

	//--------------------------------------------------------------------------
	/**
	 * Build a Vector of valid joins for the query
	 * @param query the Query
	 * @return Vector the valid joins
	 */
	private Vector
	getValidJoins (
	 Query query)
	{
		// iterate the query classes and build a vector of all involved joins
		Vector allJoins = new Vector ();
		Vector joins;
		Join jn;
		Entity ent;
		String entityName;
		Hashtable entitiesHash = query.getEntitiesHash ();
		Enumeration enums = entitiesHash.keys ();
		while (enums != null && enums.hasMoreElements ())
		{
			// get the class and entity
			entityName = (String)enums.nextElement ();
			ent = getEntity (entityName);
			if (ent != null)
			{
				// iterate joins
				joins = getJoins ();
				for (int i = 0; joins != null && i < joins.size (); i++)
				{
					// get the ent join
					jn = (Join)joins.elementAt (i);

					// if valid child and parent class, add to allJoins
					if (jn != null && jn.getLeftEntity ().equalsIgnoreCase (entityName)
						&& entitiesHash.get (jn.getRightEntity ()) != null)
						allJoins.add (jn);
				}
			}
		}

		// return
		return allJoins;
	}

	//--------------------------------------------------------------------------
	/**
	 * Sort the join vector to avoid outer join inversion
	 * @param joins the join Vector
	 * @param Vector the sorted Vector
	 */
	private Vector
	sortJoinVector (
	 Vector joins)
	{
		// iterate the joins
		Vector ret = new Vector ();
		Join join, temp;
		while (joins.size () > 0)
		{
			// get the join
			join = (Join)joins.remove (0);

			// if it is an outer join
			if (join.getJoinType () == Join.JT_LEFT_OUTER)
			{
				// ensure that the left entity is left of right entity in the vector
				String rightEnt = join.getRightEntity ();
				int index = -1;
				for (int i = 0; index < 0 && i < ret.size (); i++)
				{
					// get temp and compare
					temp = (Join)ret.elementAt (i);
					if (temp.getLeftEntity ().equals (rightEnt) ||
						temp.getRightEntity ().equals (rightEnt))
						index = i;
				}

				// add to ret vector
				if (index >= 0)
					ret.add (index, join);
				else
					ret.add (join);
			}

			// else just add it
			else
				ret.add (join);
		}

		// return
		return ret;
	}

	//--------------------------------------------------------------------------
	/**
	 * Set the connection and pool params using a JdbcDataSourceParam.
	 * @param param the JdbcDataSourceParam to set.
	 */
	public synchronized void
	setParam (
	 JdbcDataSourceParam param)
	{
		// store the param so we can use it as an identifier
		_param = param;

		// set the ds name
		setName (param._name);

		// set the connect info
		setConnectData (param._driver, param._url, 
			param._user, param._pw);//param._catalog,
		
		System.out.println ("JdbcDataSource setParam: driver=" + param._driver + ", url=" + param._url
				+ ", user=" + param._user);
		// if it has pool info set it
		if (param._hasPoolInfo)
			setPoolData (param._poolSize, param._msLifetime, param._msGetTimeout);
		else
			clearPoolData ();

	}

	//--------------------------------------------------------------------------
	/**
	 * Set conection pool data.
	 */
	public synchronized void
	setPoolData (
	 int poolSize,
	 int msLifetime,
	 int msGetTimeout)
	{
		_hasPoolData  = true;
		_poolSize     = poolSize;
		_msLifetime   = msLifetime;
		_msGetTimeout = msGetTimeout;
	}

	//--------------------------------------------------------------------------
	/**
	 * Clear connection pool data.
	 */
	public synchronized void
	clearPoolData ()
	{
		_hasPoolData = false;
	}

	//--------------------------------------------------------------------------
	/**
	 * @return an identifier that can be used to lookup this DataSource. This
	 *   should be a DataSourceParam or a string name.
	 */
	public Object
	getDsId ()
	{
		if (_param != null)
			return _param;
		else
			return getName ();
	}

	//--------------------------------------------------------------------------
	/**
	 * Exectues a direct SQL query.  The return value is a hashtable with keys that match
	 * the query resultset's column names.  Each column (the hashtable value) is a Vector
	 * that represents
	 */
	public Hashtable
	execDirectSQL (
	 String sql)
		throws DataSourceException
	{
		Util.argCheckNull (sql);
		Util.argCheckEmpty (sql);
		Hashtable rows = new Hashtable (89);

		Connection con = null;
		boolean isError = false;
		try
		{
			// log, convert to native and execute the sql
			Log.debug (sql);
			System.out.println("sql: " + sql);
			con = getConnection ();
			String nativeSql = getNativeSQL (con, sql);
			Statement stmt = con.createStatement ();
			ResultSet rs = stmt.executeQuery (nativeSql);
			if (rs == null)
				return null;

			// get the metadata and create the hashtable keys and the vectors
			ResultSetMetaData rsmd = rs.getMetaData ();
			Vector vColumnValues;
			Vector vColumns = new Vector ();
			Vector vClassNames = new Vector ();
			int colCount = rsmd.getColumnCount ();
			for (int i = 0; i < colCount; i++)
			{
				vColumnValues = new Vector ();
				vColumns.add (vColumnValues);
				vClassNames.add (rsmd.getColumnClassName (i+1));
			}

			// extract the data form the result set and put it into the column vectors
			Object val;
			String className;
			for (int row = 0; rs != null && rs.next (); row++)
			{
				for (int i = 0; i < colCount; i++)
				{
					// get the column values vector
					vColumnValues = (Vector)vColumns.elementAt (i);

					// get the class name
					className = (String)vClassNames.elementAt (i);

					// get the value
					val = getResultSetValue (className, rs, i+1);
					vColumnValues.add (row, val);
				}
			}

			// build the return hash
			for (int i = 0; i < colCount; i++)
			{
				// get the colname
				String colName = rsmd.getColumnName (i+1);

				// add to the hash with the col values vector
				rows.put (colName, vColumns.elementAt (i));
			}

			// close the statement
			stmt.close ();
		}
		catch (SQLException e)
		{
			// set error flag
			isError = true;

			// error, convert to DataSourceException and throw
			throw new DataSourceException ("JdbcDataSource.execDirectSQL: "
				+ e.toString () + ", " + e.getSQLState ());
		}
		finally
		{
			// return the connection to the pool
			if (con != null)
			{
				try
				{
					// if error and pooled pooled close the actual connection
					if (isError && con instanceof PooledConnection)
						 ((PooledConnection)con).close (true);
					else
						con.close ();
				}
				catch (SQLException e)
				{
				}
			}
		}

		return rows;
	}
	/**
	 * Exectues a direct SQL update or insert query.  The return value is row number which executes
	 */
	public int
	execInsertOrUpdate (
	 String sql)
		throws DataSourceException
	{
		Util.argCheckNull (sql);
		Util.argCheckEmpty (sql);
		Hashtable rows = new Hashtable (89);

		Connection con = null;
		boolean isError = false;
		try
		{
			// log, convert to native and execute the sql
			Log.debug (sql);
			System.out.println("sql: " + sql);
			con = getConnection ();
			con.setAutoCommit(true);
			String nativeSql = getNativeSQL (con, sql);
			Statement stmt = con.createStatement ();
			int row = stmt.executeUpdate(nativeSql);			
			
			// close the statement
			stmt.close ();
			con.commit();	
			
			return row;
		}
		catch (SQLException e)
		{
			// set error flag
			isError = true;

			// error, convert to DataSourceException and throw
			throw new DataSourceException ("JdbcDataSource.execInsertOrUpdate: "
				+ e.toString () + ", " + e.getSQLState () + " more reason: " + (e.getNextException() != null ? e.getNextException().toString() : ""));
		}
		finally
		{
			// return the connection to the pool
			if (con != null)
			{
				try
				{
					// if error and pooled pooled close the actual connection
					if (isError && con instanceof PooledConnection)
						 ((PooledConnection)con).close (true);
					else
						con.close ();
				}
				catch (SQLException e)
				{
				}
			}
		}
	}
	//--------------------------------------------------------------------------
	/**
	 * Test that the data source can connect to the data base.
	 * @throws DataSourceException if the connection cannot be made.
	 */
	public void
	testConnection ()
		throws DataSourceException
	{
		Connection con = null;

		try
		{
			con = getConnection ();
		}
		finally
		{
			// return the connection to the pool
			if (con != null)
				try
				{
					con.close ();
				}
				catch (SQLException e)
				{
				}
		}
	}

	//--------------------------------------------------------------------------
	// inner native delegate classes

	/**
	 * Factory
	 */
	public NativeDelegate
	getNative ()
	{
		// constants
		final String NAT_DEFAULT = "DEFAULT";

		// if null, create
		if (_native == null)
		{
			String prod = NAT_DEFAULT;
			try
			{
				// get the product version
				DatabaseMetaData md = getConnection ().getMetaData ();
				prod = md.getDatabaseProductName ();
				Log.debug ("Database Product Name = \"" + prod + "\"");

			} catch (Exception e)
			{
				prod = NAT_DEFAULT;
			}

			/*
			 *	Decode the name to set the Native Delegate
			 */
			if (prod.equalsIgnoreCase (AccessNativeDelegate.NAME))
				_native = new AccessNativeDelegate ();
			else if (prod.equalsIgnoreCase (SQLServerNativeDelegate.NAME))
				_native = new SQLServerNativeDelegate ();
			else if (prod.equalsIgnoreCase (OracleNativeDelegate.NAME))
				_native = new OracleNativeDelegate ();
			else if (prod.equalsIgnoreCase (InformixNativeDelegate.NAME))
				_native = new InformixNativeDelegate ();
			else if (prod.equalsIgnoreCase (InformixSENativeDelegate.NAME))
				_native = new InformixSENativeDelegate ();
			else if (prod.equalsIgnoreCase (JDataStoreNativeDelegate.NAME))
				_native = new JDataStoreNativeDelegate ();
			else if (prod.equalsIgnoreCase (DataFlexNativeDelegate.NAME))
				_native = new DataFlexNativeDelegate ();
			else if (prod.equalsIgnoreCase (ProgressNativeDelegate.NAME))
				_native = new ProgressNativeDelegate ();
			else if (prod.equalsIgnoreCase (VisualFoxProDelegate.NAME))
				_native = new VisualFoxProDelegate ();
			else if (prod.equalsIgnoreCase (SybaseNativeDelegate.NAME))
				_native = new SybaseNativeDelegate ();
			else
				_native = new NativeDelegate ();
		}
		return _native;
	}
}
