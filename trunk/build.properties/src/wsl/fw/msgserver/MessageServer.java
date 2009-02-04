/*	$Id: MessageServer.java,v 1.6 2002/09/26 04:36:41 tecris Exp $
 *
 *	Abstract class for MessageServers
 *
 */
package wsl.fw.msgserver;

import java.util.Iterator;
import java.util.Vector;
import java.util.Hashtable;
import wsl.fw.datasource.*;
import wsl.fw.util.Util;

import wsl.fw.datasource.DataSourceException;

public abstract class MessageServer
	extends DataObject
	implements DataSource
{
	//--------------------------------------------------------------------------
	// constants
	public final static String ENT_MSGSERVER        = "TBL_MSG_SERVER";
	public final static String FLD_ID               = "FLD_ID";
	public final static String FLD_DESCRIPTION      = "FLD_DESCRIPTION";
	public final static String FLD_CLASS            = JdbcDataSource.CLASS_COLUMN_NAME;
	public final static String FLD_TYPE             = "FLD_TYPE";
	public final static String FLD_SERVER_NAME      = "FLD_SERVER_NAME";
	public final static String FLD_HOST             = "FLD_SITE_NAME";
	public final static String FLD_EXTRA_STRING1    = "FLD_ORG_NAME";
	public final static String FLD_ISLOCALHOST      = "FLD_ISLOCALHOST";
	public final static String FLD_PROP_TAGS        = "FLD_ORG_NAME";

	//--------------------------------------------------------------------------
	// attributes
	private transient Vector _profiles = null;
	private transient DataSource _impl = null;

	//--------------------------------------------------------------------------
	// construction

	/**
	 * Blank ctor
	 */
	public
	MessageServer ()
	{
	}

	//--------------------------------------------------------------------------
	// persistence

	/**
	 * Static factory method to create the entity to be used by this dataobject
	 * and any subclasses. This is called by the DataManager's factory when
	 * creating a MSGSERVER entity.
	 * @return the created entity.
	 */
	public static Entity
	createEntity ()
	{
		// create the entity
		Entity ent = new EntityImpl (ENT_MSGSERVER, null);

		// add the key generator for the system id
		ent.addKeyGeneratorData (new DefaultKeyGeneratorData (ENT_MSGSERVER, FLD_ID));

		// create the fields and add them to the entity
		ent.addField (new FieldImpl (FLD_ID, Field.FT_INTEGER, Field.FF_UNIQUE_KEY | Field.FF_SYSTEM_KEY));
		ent.addField (new FieldImpl (FLD_DESCRIPTION, Field.FT_STRING));
		ent.addField (new FieldImpl (FLD_TYPE, Field.FT_STRING));
		ent.addField (new FieldImpl (FLD_SERVER_NAME, Field.FT_STRING, Field.FF_NAMING));
		ent.addField (new FieldImpl (FLD_HOST, Field.FT_STRING));
		ent.addField (new FieldImpl (FLD_EXTRA_STRING1, Field.FT_STRING));
		ent.addField (new FieldImpl (FLD_ISLOCALHOST, Field.FT_INTEGER));
		ent.addField (new FieldImpl (FLD_CLASS, Field.FT_STRING)); // polymorph support
		//ent.addField (new FieldImpl (FLD_PROP_TAGS, Field.FT_STRING));

		// return the entity
		return ent;
	}

	//--------------------------------------------------------------------------
	/**
	 * @return String the name of the entity that defines this DataObject
	 */
	public String
	getEntityName ()
	{
		return ENT_MSGSERVER;
	}


	//--------------------------------------------------------------------------
	// create impl

	/**
	 * Create a local or remote version of this object
	 * @return DataSource
	 */
	public DataSource
	createImpl ()
	{
		// if null, create
		if (_impl == null)
		{
			// create a param defining the ms
			MessageServerParam msParam =
				new MessageServerParam (this);

			// get/create the datasource
			_impl = DataManager.getDataSource (msParam);
		}

		// return
		return _impl;
	}

	/**
	 * Set the message server param
	 * @param param
	 */
	public void
	setParam (MessageServerParam msp)
	{
		setServerName (msp.getServerName ());
		setHost (msp.getHost ());
		setId (new Integer (msp.getMsId ()));
		setType (msp.getMsType ());
		setPropTags (msp.getPropTags ());
	}

	//--------------------------------------------------------------------------
	// accessors

	/**
	 * @return int the id
	 */
	public int
	getId ()
	{
		return getIntValue (FLD_ID);
	}

	/**
	 * Set the id
	 * @param id, the id to set
	 */
	public void
	setId (
	 Object id)
	{
		setValue (FLD_ID, id);
	}

	/**
	 * Returns the description
	 * @return String
	 */
	public String
	getDescription ()
	{
		return getStringValue (FLD_DESCRIPTION);
	}

	/**
	 * Sets the entity description
	 * @param name
	 * @return void
	 */
	public void
	setDescription (
	 String name)
	{
		setValue (FLD_DESCRIPTION, name);
	}

	/**
	 * Returns the type
	 * @return String
	 */
	public String
	getType ()
	{
		return getStringValue (FLD_TYPE);
	}

	/**
	 * Sets the entity type
	 * @param name
	 * @return void
	 */
	public void
	setType (
	 String type)
	{
		setValue (FLD_TYPE, type);
	}

	/**
	 * Returns the server name
	 * @return String
	 */
	public String
	getServerName ()
	{
		return getStringValue (FLD_SERVER_NAME);
	}

	/**
	 * Sets the server name
	 * @param name
	 * @return void
	 */
	public void
	setServerName (
	 String name)
	{
		setValue (FLD_SERVER_NAME, name);
	}

	/**
	 * Returns the host
	 * @return String
	 */
	public String
	getHost ()
	{
		return getStringValue (FLD_HOST);
	}

	/**
	 * Sets the host
	 * @param name
	 * @return void
	 */
	public void
	setHost (
	 String name)
	{
		setValue (FLD_HOST, name);
	}

	/**
	 * Returns the extra string
	 * @return String
	 */
	public String
	getExtraString1 ()
	{
		return getStringValue (FLD_EXTRA_STRING1);
	}

	/**
	 * Sets the extra string
	 * @param name
	 * @return void
	 */
	public void
	setExtraString1 (
	 String name)
	{
		setValue (FLD_EXTRA_STRING1, name);
	}

	/**
	 * @return true if localhost server
	 */
	public boolean
	isLocalhost ()
	{
		return getBooleanValue (FLD_ISLOCALHOST);
	}

	/**
	 * Sets the localhost server flag
	 * @param b true if localhost
	 * @return void
	 */
	public void
	setIsLocalhost (
	 boolean b)
	{
		setValue (FLD_ISLOCALHOST, b);
	}

	/**
	 * Returns the property tags
	 * @return String
	 */
	public String
	getPropTags ()
	{
		return getStringValue (FLD_PROP_TAGS);
	}

	/**
	 * Sets the prop tags
	 * @param tags
	 * @return void
	 */
	public void
	setPropTags (
	 String tags)
	{
		setValue (FLD_PROP_TAGS, tags);
	}

	//--------------------------------------------------------------------------
	// message profiles

	/**
	 * @return all the MessageServerProfiles belonging to this message server
	 */
	public Vector
	getProfiles ()
		throws DataSourceException
	{
		// if null, create
		if (_profiles == null)
		{
			// create vector
			_profiles = new Vector ();

			// select profiles
			DataSource sysDs = DataManager.getSystemDS ();
			Query q = new Query (MessageServerProfile.ENT_MSGSVR_PROFILE);
			q.addQueryCriterium (new QueryCriterium (MessageServerProfile.ENT_MSGSVR_PROFILE,
				MessageServerProfile.FLD_MSG_SERVERID, QueryCriterium.OP_EQUALS,
				new Integer (this.getId ())));
			RecordSet rs = sysDs.select (q);

			// build vector
			while (rs.next ())
				_profiles.add (rs.getCurrentObject ());
		}

		// dont use attrib for now to avoid caching issues
		Vector profiles = _profiles;
		_profiles = null;
		return profiles;
/*
		// return
		return _profiles;
*/    }

	/**
	 * @param userId
	 * @return the first profile belonging to message server and user
	 */
	public MessageServerProfile
	getFirstProfile (
	 int userId)
		throws DataSourceException
	{
		// iterate the profiles
		MessageServerProfile msp;
		for (int i = 0; getProfiles () != null && i < getProfiles ().size (); i++)
		{
			// get the msp and compare user ids
			msp = (MessageServerProfile)getProfiles ().elementAt (i);
			if (msp != null && msp.getUserId () == userId)
				return msp;
		}

		// not found
		return null;
	}

	//--------------------------------------------------------------------------
	// cascading delete

	/**
	 * Called pre delete call on DataSource
	 */
	protected void
	preDelete ()
		throws DataSourceException
	{
		// delete all profiles
		for (int i = 0; getProfiles () != null && i < getProfiles ().size (); i++)
			 ((DataObject)getProfiles ().elementAt (i)).delete ();
	}

	//--------------------------------------------------------------------------
	// DataSource interface

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
		// switch on dobj type
		// send email
		if (dobj instanceof SessionFwMailMsgDobj)
			return insertFwMailMessage ((SessionFwMailMsgDobj) dobj);
		// forward email
		else if (dobj instanceof SessionMailMsgDobj)
			return insertMailMessage ((SessionMailMsgDobj) dobj);

		throw new DataSourceException ("Not implemented");
	}

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
		if (dobj instanceof SessionMailMsgDobj)
		{
			updateReadMailMessage ((SessionMailMsgDobj) dobj);
			return true;
		}

		return false;
	}

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
		throws DataSourceException
	{
		if (dobj instanceof SessionMailMsgDobj)
			return deleteMailMessage ((SessionMailMsgDobj) dobj);

		throw new DataSourceException ("Not implemented");
	}

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
		throws DataSourceException
	{
		throw new DataSourceException ("Not implemented");
	}

	/**
	 * Iterator support
	 *
	 * 	Since the RecordItr has to run on the Server-end, we cannot
	 *  pass it over to the Client. All we can do is to hand a reference-key
	 *  back to the client. The client then calls iHasNextObj/iNextObj using
	 *  the reference-key, which the MessageServer maps to the RecordItr
	 *
	 *	The correct way to do this would be to make RecordItr a RMI Remote.
	 *  However, there'd be too many of them floating around, and it'll
	 *  be hell trying to expire them correctly.
	 */
	public RecordItrRef
	iSelect (
	 Query q)
		throws DataSourceException
	{
		return doActionQuery ( (ActionQuery) q);
	}

	public abstract boolean
	iHasNextObj (
	 RecordItrRef ref)
		throws DataSourceException;

	public abstract Object
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
		throws DataSourceException
	{
		throw new DataSourceException ("Not implemented");
	}

	/**
	 * Close the DataSource and release its resources
	 * @throws DataSourceException if there is an error from the DataSource.
	 */
	public void
	close ()
		throws DataSourceException
	{
	}

	/**
	 * Return the unique name of the DataSource
	 * @return String the DataSource name
	 */
	public String
	getName ()
	{
		return getServerName ();
	}

	/**
	 * Set the unique name of the DataSource
	 * @param String the DataSource name
	 */
	public void
	setName (
	 String name)
	{
		setServerName (name);
	}

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
		throw new DataSourceException ("Not implemented");
	}

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
		throw new DataSourceException ("Not implemented");
	}

	/**
	 * Add an Entity to the Entity vector
	 * @param entity the Entity to add
	 */
	public void
	addEntity (
	 Entity entity)
	{
	}

	/**
	 * Get an entity by name from the the DataSource
	 * @param name the name of the Entity
	 * @return Entity the Entity with the param name, or null if not found
	 */
	public Entity
	getEntity (
	 String name)
	{
		throw new RuntimeException ("Not implemented");
	}

	/**
	 * Get the Vector of Entities
	 * @return Vector the Entities
	 */
	public Vector
	getEntities ()
	{
		throw new RuntimeException ("Not implemented");
	}

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
		throw new DataSourceException ("Not implemented");
	}

	/**
	 * Add a Join to the join Vector
	 * @param join the Join to add
	 */
	public void
	addJoin (
	 Join join)
	{
		throw new RuntimeException ("Not implemented");
	}

	/**
	 * Get the Vector of Joins
	 * @return Vector the Joins
	 */
	public Vector
	getJoins ()
	{
		throw new RuntimeException ("Not implemented");
	}

	/**
	 * Import a the names of tables from the datasource.
	 * @return a Vector of EntitySchemaNames containing the table names.
	 */
	public Vector
	importTableNames ()
		throws DataSourceException
	{
		throw new DataSourceException ("Not implemented");
	}

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
		throw new DataSourceException ("Not implemented");
	}

	/**
	 * Import an Entity definition from a raw SQL select statement
	 */
	public Entity
	importRawSelectDefinition (
	 String selectStmt)
		throws DataSourceException
	{
		throw new DataSourceException ("Not implemented");
	}

	/**
	 * @return an identifier that can be used to lookup this DataSource. This
	 *   should be a DataSourceParam or a string name.
	 */
	public Object
	getDsId ()
	{
		return new Integer (this.getId ());
	}

	/**
	 * Executes a direct SQL query.  The return value is a hashtable with keys
	 * that match the query resultset's column names. Each column
	 * (the hashtable value) is a Vector
	 */
	public Hashtable
	execDirectSQL (
	 String sql)
		throws DataSourceException
	{
		throw new DataSourceException ("Not implemented");
	}

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
		throw new DataSourceException ("Not implemented");
	}

	/**
	 * Test that the data source can connect to the data base.
	 * @throws DataSourceException if the connection cannot be made.
	 */
	public void
	testConnection ()
		throws DataSourceException
	{
		throw new DataSourceException ("Not implemented");
	}

	//--------------------------------------------------------------------------
	// Message Server implementation
	protected abstract RecordItrRef
	doActionQuery (
	 ActionQuery q)
		throws DataSourceException;

	/**
	 * Insert (send) a mail message
	 * @param mm the mail message to send
	 * @return true if message sent successfully
	 */
	protected abstract boolean
	insertMailMessage (
	 SessionMailMsgDobj mmd)
		throws DataSourceException;
	
	/**
	 * Insert (forward) a mail message
	 * @param mm the mail message to forward
	 * @return true if message sent successfully
	 */
	protected abstract boolean
	insertFwMailMessage (
	 SessionFwMailMsgDobj fwd)
		throws DataSourceException;

	/**
	 *	Delete a mail message. Provide a default stub.
	 *	@param mObj the mail message to delete
	 *	@return true if message deleted successfully
	 */
	protected boolean
	deleteMailMessage (
	 SessionMailMsgDobj mObj)
		throws DataSourceException
	{
		/*
		 *	If we don't support it, we indicate a failure
		 */
		return false;
	}

	/**
	 *	Mark a mail message as read. Provide a default stub.
	 *	@param mObj the mail message to mark
	 */
	protected void
	updateReadMailMessage (
	 SessionMailMsgDobj mObj)
		throws DataSourceException
	{
		/*
		 *	We don't really care whether the MessageServer supports this,
		 *	it's a nicety.
		 */
	}
}
