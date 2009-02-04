/**	$Id: DataSourceDobj.java,v 1.2 2002/07/15 22:29:00 jonc Exp $
 *
 * DataObject used to persist DataSource objects
 *
 */

package wsl.mdn.dataview;

import java.util.Vector;
import wsl.fw.util.Util;
import wsl.fw.datasource.*;

public abstract class DataSourceDobj
	extends DataObject
{
	//--------------------------------------------------------------------------
	// constants

	public final static String ENT_DATASOURCE       = "TBL_DATASOURCE";
	public final static String FLD_ID               = "FLD_ID";
	public final static String FLD_NAME             = "FLD_NAME";
	public final static String FLD_CLASS            = "FLD_CLASS";
	public final static String FLD_DESCRIPTION      = "FLD_DESCRIPTION";
	public final static String FLD_JDBC_URL         = "FLD_JDBC_URL";
	public final static String FLD_JDBC_CATALOG     = "FLD_JDBC_CATALOG";
	public final static String FLD_JDBC_USER        = "FLD_JDBC_USER";
	public final static String FLD_JDBC_PASSWORD    = "FLD_JDBC_PASSWORD";
	public final static String FLD_JDBC_DRIVERID    = "FLD_JDBC_DRIVERID";
	public final static String FLD_IS_MIRRORED      = "FLD_IS_MIRRORED";
	public final static String FLD_IS_MIRROR_DB     = "FLD_IS_MIRROR_DB";
	public final static String FLD_MIRRORID         = "FLD_MIRRORID";
	public final static String FLD_PROJECT_ID       = "FLD_PROJECT_ID";
	public final static String FLD_DEL_STATUS       = "FLD_DEL_STATUS";


	//--------------------------------------------------------------------------
	// attributes

	private transient Vector  _entities       = null;
	private transient Vector  _imageEntities  = null;
	private transient Vector  _deleteEntities = null;
	private transient Vector  _joins          = null;
	private transient Vector  _imageJoins     = null;
	private transient Vector  _deleteJoins    = null;
	private transient Vector  _views          = null;
	private           boolean _entitiesLoaded = false;
	private           boolean _joinsLoaded    = false;
	private           boolean _viewsLoaded    = false;

	//--------------------------------------------------------------------------
	// construction
	public DataSourceDobj ()
	{
	}

	//--------------------------------------------------------------------------
	// persistence

	/**
	 * Static factory method to create the entity to be used by this dataobject
	 * and any subclasses. This is called by the DataManager's factory when
	 * creating a ENTITY entity.
	 * @return the created entity.
	 */
	public static Entity
	createEntity ()
	{
		// create the entity
		Entity ent = new EntityImpl (ENT_DATASOURCE);

		// add the key generator for the system id
		ent.addKeyGeneratorData (new DefaultKeyGeneratorData (ENT_DATASOURCE, FLD_ID));

		// create the fields and add them to the entity
		ent.addField (new FieldImpl (FLD_ID, Field.FT_INTEGER, Field.FF_UNIQUE_KEY | Field.FF_SYSTEM_KEY));
		ent.addField (new FieldImpl (FLD_NAME, Field.FT_STRING, Field.FF_NAMING));
		ent.addField (new FieldImpl (FLD_CLASS, Field.FT_STRING));
		ent.addField (new FieldImpl (FLD_DESCRIPTION, Field.FT_STRING));
		ent.addField (new FieldImpl (FLD_JDBC_URL, Field.FT_STRING, Field.FF_NONE, 255));
		ent.addField (new FieldImpl (FLD_JDBC_CATALOG, Field.FT_STRING));
		ent.addField (new FieldImpl (FLD_JDBC_USER, Field.FT_STRING));
		ent.addField (new FieldImpl (FLD_JDBC_PASSWORD, Field.FT_STRING));
		ent.addField (new FieldImpl (FLD_JDBC_DRIVERID, Field.FT_INTEGER));

		ent.addField (new FieldImpl (FLD_IS_MIRRORED, Field.FT_INTEGER));
		ent.addField (new FieldImpl (FLD_IS_MIRROR_DB, Field.FT_INTEGER));
/*
		ent.addField (new FieldImpl (FLD_IS_MIRRORED, Field.FT_BOOLEAN));
		ent.addField (new FieldImpl (FLD_IS_MIRROR_DB, Field.FT_BOOLEAN));
*/
		ent.addField (new FieldImpl (FLD_MIRRORID, Field.FT_INTEGER));
		ent.addField (new FieldImpl (FLD_PROJECT_ID, Field.FT_INTEGER));
		ent.addField (new FieldImpl (FLD_DEL_STATUS, Field.FT_INTEGER));

		// return the entity
		return ent;
	}

	/**
	 * @return String the name of the entity that defines this DataObject
	 */
	public String
	getEntityName ()
	{
		return ENT_DATASOURCE;
	}

	//--------------------------------------------------------------------------
	// accessors

	/**
	 * @return int the id of this Entity
	 */
	public int
	getId ()
	{
		return getIntValue (FLD_ID);
	}

	/**
	 * Set the id of this Entity
	 * @param id
	 */
	public void
	setId (
	 int id)
	{
		setValue (FLD_ID, id);
	}
	/**
	 * @return int the id of this project
	 */
	public int
	getProjectId ()
	{
		return getIntValue (FLD_PROJECT_ID);
	}

	/**
	 * Set the id of this project
	 * @param id
	 */
	public void
	setProjectId (
	 int id)
	{
		setValue (FLD_PROJECT_ID, id);
	}
	/**
	 * Returns the name of the field
	 * @return String
	 */
	public String
	getName ()
	{
		return getStringValue (FLD_NAME);
	}

	/**
	 * Sets the field name
	 * @param name
	 * @return void
	 */
	public void
	setName (
	 String name)
	{
		setValue (FLD_NAME, name);
	}

	/**
	 * Returns the description of the field
	 * @return String
	 */
	public String
	getDescription ()
	{
		return getStringValue (FLD_DESCRIPTION);
	}

	/**
	 * Sets the entity description into the field
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
	 * @return true if the DataSource is mirrored
	 */
	public boolean
	isMirrored ()
	{
		return getBooleanValue (FLD_IS_MIRRORED);
	}

	/**
	 * Sets the mirrored flag
	 * @param b
	 * @return void
	 */
	public void
	setIsMirrored (
	 boolean b)
	{
		setValue (FLD_IS_MIRRORED, b);
	}

	/**
	 * @return true if the DataSource is a mirror database
	 */
	public boolean
	isMirrorDb ()
	{
		return getBooleanValue (FLD_IS_MIRROR_DB);
	}

	/**
	 * true if the database is a mirror (not mirrored) db
	 * @param b
	 * @return void
	 */
	public void
	setIsMirrorDb (
	 boolean b)
	{
		setValue (FLD_IS_MIRROR_DB, b);
	}

	/**
	 * @return int the mirror id
	 */
	public int
	getMirrorId ()
	{
		return getIntValue (FLD_MIRRORID);
	}

	/**
	 * Set the mirror id
	 * @param id
	 */
	public void
	setMirrorId (
	 int id)
	{
		setValue (FLD_MIRRORID, id);
	}

	public int getDelStatus() {
		return getIntValue(FLD_DEL_STATUS);
	}

	public void setDelStatus(int delStatus) {
		setValue(FLD_DEL_STATUS, delStatus);
	}	
	
	//--------------------------------------------------------------------------
	// cascading save

	/**
	 * Called post insert / update call on DataSource
	 */
	protected void
	postSave ()
		throws DataSourceException
	{
		// process imaging, delete the delete joins
		if (isJoinImaging ())
			processJoinImage ();

		// save all joins
		JoinDobj j;
		for (int i = 0; _joinsLoaded && i < getJoins ().size (); i++)
		{
			j = (JoinDobj)getJoins ().elementAt (i);
			if (j != null)
			{
				// if new set keys
				if (j.getState () == DataObject.NEW)
					j.setDataSourceId (this.getId ());

				// save
				j.save ();
			}
		}

		// process imaging, delete the delete entities
		if (isEntityImaging ())
			processEntityImage ();

		// save all entities
		EntityDobj ent;
		for (int i = 0; _entitiesLoaded && i < getEntities ().size (); i++)
		{
			ent = (EntityDobj)getEntities ().elementAt (i);
			if (ent != null)
			{
				// if new set keys
				if (ent.getState () == DataObject.NEW)
					ent.setDataSourceId (this.getId ());

				// save
				ent.save ();
			}
		}
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
		// delete all joins
		JoinDobj j;
		for (int i = 0; i < getJoins ().size (); i++)
		{
			j = (JoinDobj)getJoins ().elementAt (i);
			if (j != null && j.getState () == DataObject.IN_DB)
				j.delete ();
		}
		getJoins ().clear ();

		// delete all entities
		EntityDobj ent;
		for (int i = 0; i < getEntities ().size (); i++)
		{
			ent = (EntityDobj)getEntities ().elementAt (i);
			if (ent != null && ent.getState () == DataObject.IN_DB)
				ent.delete ();
		}
		getEntities ().clear ();

		// delete all dataviews
		DataView dv;
		for (int i = 0; i < getDataViews ().size (); i++)
		{
			dv = (DataView)getDataViews ().elementAt (i);
			if (dv != null && dv.getState () == DataObject.IN_DB)
				dv.delete ();
		}
		getDataViews ().clear ();

		// delete transfers
		deleteTransfers ();
	}

	/**
	 * Delete related data transfers
	 */
	private void
	deleteTransfers ()
		throws DataSourceException
	{
		// build query
		Query q = new Query (DataTransfer.ENT_DATATRANSFER);
		QueryCriterium qc = new QueryCriterium (DataTransfer.ENT_DATATRANSFER,
			DataTransfer.FLD_DSID,
			QueryCriterium.OP_EQUALS, new Integer (this.getId ()));
		q.addQueryCriterium (qc);

		// do select
		RecordSet rs = DataManager.getSystemDS ().select (q);

		// iterate and delete transfers
		DataTransfer dt;
		while (rs.next ())
		{
			dt = (DataTransfer)rs.getCurrentObject ();
			if (dt != null && dt.getState () == DataObject.IN_DB)
				dt.delete ();
		}
	}

	//--------------------------------------------------------------------------
	// impl

	/**
	 * Create and return the DataSource object
	 * @return DataSource
	 */
	public abstract DataSource
	createImpl ();

	//--------------------------------------------------------------------------
	// Entities
	/**
	 * Add an Entity to the Entity vector
	 * @param entity the Entity to add
	 */
	public void
	addEntity (
	 EntityDobj entityDobj)
	{
		// validate
		Util.argCheckNull (entityDobj);

		// ensure the entities vector exists
		if (_entities == null)
			_entities = new Vector ();

		// add the entity, replace if one of the same name already exists
		int entIndex = -1;
		for (int i = 0; i < _entities.size (); i++)
		{
			String name1 = ( (EntityDobj) _entities.elementAt (i)).getName ();
			String name2 = entityDobj.getName ();
			if ( (name1 == null && name2 == null)
				|| (name1 != null && name2 != null && name1.equals (name2)))
			{
				// found entityDobj with matching name, save index and stop
				entIndex = i;
				break;
			}
		}

		if (entIndex == -1)
			_entities.add (entityDobj);
		else
			_entities.set (entIndex, entityDobj);

		// set the loaded flag
		_entitiesLoaded = true;
	}

	//--------------------------------------------------------------------------
	/**
	 * Get an entity by name from the the DataSource
	 * @param entityName the name of the Entity
	 * @return Entity the Entity with the param name, or null if not found
	 */
	public EntityDobj
	getEntity (
	 String entityName)
	{
		// iterate the entities
		EntityDobj ent = null;
		for (int i = 0; i < getEntities ().size (); i++)
		{
			// get the entity and compare
			ent = (EntityDobj)getEntities ().elementAt (i);
			if (ent != null && ent.getName ().equalsIgnoreCase (entityName))
				return ent;
		}

		// not found
		throw new RuntimeException ("Entity not found: " + entityName);//return null;
	}

	/**
	 * Get the Vector of Entities
	 * @return Vector the Entities
	 */
	public Vector
	getEntities ()
	{
		// ensure entities vector exists
		if (_entities == null)
			_entities = new Vector ();

		// if not loaded, load
		if (!_entitiesLoaded)
		{
			// must be indb
			if (this.getState () == DataObject.IN_DB)
			{
				try
				{
					// build entity/field join query
					DataSource ds = DataManager.getSystemDS ();
					Query q = new Query (EntityDobj.ENT_ENTITY);
					q.addQueryEntity (FieldDobj.ENT_FIELD);
					q.addQueryCriterium (new QueryCriterium (EntityDobj.ENT_ENTITY,
						EntityDobj.FLD_DSID, QueryCriterium.OP_EQUALS,
						new Integer (this.getId ())));
					q.addSort (new Sort (EntityDobj.ENT_ENTITY, EntityDobj.FLD_ID));
					q.addSort (new Sort (FieldDobj.ENT_FIELD, FieldDobj.FLD_ID));
					RecordSet rs = ds.select (q);
					buildEntitiesFromRecordSet (rs);

					// do just entities to catch entities with no fields
					q = new Query (EntityDobj.ENT_ENTITY);
					q.addQueryCriterium (new QueryCriterium (EntityDobj.ENT_ENTITY,
						EntityDobj.FLD_DSID, QueryCriterium.OP_EQUALS,
						new Integer (this.getId ())));
					q.addSort (new Sort (EntityDobj.ENT_ENTITY, EntityDobj.FLD_ID));
					rs = ds.select (q);
					buildEntitiesFromRecordSet (rs);
				}
				catch (Exception e)
				{
					throw new RuntimeException (e.toString ());
				}
			}

			// set flag
			_entitiesLoaded = true;
		}

		return _entities;
	}

	/**
	 * Build entities from a RecordSet
	 * @param rs the RecordSet to build from
	 */
	private void
	buildEntitiesFromRecordSet (
	 RecordSet rs)
		throws DataSourceException
	{
		// execute query and build vector
		FieldDobj f;
		EntityDobj ent;
		while (rs != null && rs.next ())
		{
			// get the entity
			f = (FieldDobj)rs.getCurrentObject (FieldDobj.ENT_FIELD);
			ent = (EntityDobj)rs.getCurrentObject (EntityDobj.ENT_ENTITY);
			if (ent != null)
			{
				// if entity added get the added one
				EntityDobj found = null;
				EntityDobj temp;
				for (int i = 0; found == null && i < _entities.size (); i++)
				{
					// get temp and compare
					temp = (EntityDobj)_entities.elementAt (i);
					if (temp != null && temp.getId () == ent.getId ())
						found = temp;
				}

				// if not found add
				if (found == null)
				{
					found = ent;
					addEntity (found);
				}

				// add the field to the entity
				if (f != null)
					found.addField (f);
			}
		}
	}

	/**
	 * Remove an entity
	 */
	public EntityDobj
	removeEntity (
	 EntityDobj ent,
	 boolean doDelete)
	{
		// if it is indb, remove by key
		EntityDobj rem = null;
		if (ent.getState () == DataObject.IN_DB)
		{
			// iterate
			EntityDobj temp;
			for (int i = 0; i < getEntities ().size (); i++)
			{
				temp = (EntityDobj)getEntities ().elementAt (i);
				if (temp != null && temp.getState () == DataObject.IN_DB &&
					temp.getId () == ent.getId ())
					rem = (EntityDobj)getEntities ().remove (i);
			}
		}

		// else, try to remove by ref
		else
		{
			if (getEntities ().remove (ent))
				rem = ent;
		}

		try
		{
			// delete
			if (doDelete && rem.getState () == DataObject.IN_DB)
			{
				// imaging?
				if (isEntityImaging ())
				{
					// ensure delete entities vector exists
					if (_deleteEntities == null)
						_deleteEntities = new Vector ();
					_deleteEntities.add (rem);
				}
				else
					rem.delete ();
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException (e.toString ());
		}

		// return
		return rem;
	}

	//--------------------------------------------------------------------------
	// entity imaging
	/**
	 * Start an imaging session
	 */
	public void
	imageEntities ()
	{
		// clone joins
		_imageEntities = (Vector)getEntities ().clone ();
	}

	/**
	 * Reverts entities to the image
	 */
	public void
	revertEntitiesToImage ()
	{
		// must be imaging
		if (isEntityImaging ())
		{
			// set entities vector back to image
			_entities = _imageEntities;

			// clear the image and delete joins
			clearEntityImage ();
		}
	}

	/**
	 * End the imaging session and delete image deletes
	 */
	public void
	processEntityImage ()
		throws DataSourceException
	{
		// ensure delete entities vector exists
		if (_deleteEntities == null)
			_deleteEntities = new Vector ();

		// iterate deletes
		EntityDobj ent;
		for (int i = 0; i < _deleteEntities.size (); i++)
		{
			ent = (EntityDobj)_deleteEntities.elementAt (i);
			if (ent != null && ent.getState () == DataObject.IN_DB)
				ent.delete ();
		}

		// clear image
		clearEntityImage ();
	}

	/**
	 * Clear the image
	 */
	public void
	clearEntityImage ()
	{
		// ensure delete entities vector exists
		if (_deleteEntities == null)
			_deleteEntities = new Vector ();

		// clear the image and deletes
		_imageEntities = null;
		_deleteEntities.clear ();
	}

	/**
	 * @return boolean true if entity imaging
	 */
	public boolean
	isEntityImaging ()
	{
		return _imageEntities != null;
	}

	/**
	 * @return the delete entities
	 */
	public Vector
	getDeletedEntities ()
	{
		return _deleteEntities;
	}

	//--------------------------------------------------------------------------
	// Joins
	/**
	 * Add a Join to the join Vector
	 * @param join the Join to add
	 */
	public void
	addJoin (
	 JoinDobj joinDobj)
	{
		// validate
		Util.argCheckNull (joinDobj);

		// ensure Joins vector exists
		if (_joins == null)
			_joins = new Vector ();

		// add the join
		JoinDobj temp;
		for (int i = 0; joinDobj.getState () == DataObject.IN_DB && i < _joins.size (); i++)
		{
			temp = (JoinDobj) _joins.elementAt (i);
			if (temp.getId () == joinDobj.getId ())
			{
				_joinsLoaded = true;
				return;
			}
		}
		_joins.add (joinDobj);

		// set the loaded flag
		_joinsLoaded = true;
	}

	/**
	 * Remove a join
	 */
	public JoinDobj
	removeJoin (
	 JoinDobj j,
	 boolean doDelete)
	{
		// ensure Joins and delete Joins vectors exist
		if (_joins == null)
			_joins = new Vector ();
		if (_deleteJoins == null)
			_deleteJoins = new Vector ();

		// if it is indb, remove by key
		JoinDobj rem = null;
		if (j.getState () == DataObject.IN_DB)
		{
			// iterate
			JoinDobj temp;
			for (int i = 0; i < _joins.size (); i++)
			{
				temp = (JoinDobj)_joins.elementAt (i);
				if (temp != null && temp.getState () == DataObject.IN_DB &&
					temp.getId () == j.getId ())
					rem = (JoinDobj)_joins.remove (i);
			}
		}

		// else, try to remove by ref
		else
		{
			if (_joins.remove (j))
				rem = j;
		}

		try
		{
			// delete
			if (doDelete && rem.getState () == DataObject.IN_DB)
			{
				// imaging?
				if (isJoinImaging ())
					_deleteJoins.add (rem);
				else
					rem.delete ();
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException (e.toString ());
		}

		// return
		return rem;
	}

	/**
	 * Get the Vector of Joins
	 * @return Vector the Joins
	 */
	public Vector
	getJoins ()
	{
		// ensure Joins vector exists
		if (_joins == null)
			_joins = new Vector ();

		// if not loaded, load
		if (!_joinsLoaded)
		{
			// must be indb
			if (this.getState () == DataObject.IN_DB)
			{
				// build query
				DataSource ds = DataManager.getSystemDS ();
				Query q = new Query (JoinDobj.ENT_JOIN);
				q.addQueryCriterium (new QueryCriterium (JoinDobj.ENT_JOIN,
					JoinDobj.FLD_DSID, QueryCriterium.OP_EQUALS,
					new Integer (this.getId ())));

				try
				{
					// execute query and build vector
					JoinDobj j;
					RecordSet rs = ds.select (q);
					while (rs != null && rs.next ())
					{
						// get the join
						j = (JoinDobj)rs.getCurrentObject ();
						if (j != null)
							addJoin (j);
					}
				}
				catch (Exception e)
				{
					throw new RuntimeException (e.toString ());
				}
			}

			// set flag
			_joinsLoaded = true;
		}

		return _joins;
	}

	/**
	 * Set joins vector
	 * @param joins the new joins Vector
	 */
	public void
	setJoins (
	 Vector joins)
	{
		_joins = joins;
	}

	//--------------------------------------------------------------------------
	// join imaging

	/**
	 * Start an imaging session
	 */
	public void
	imageJoins ()
	{
		// clone joins
		_imageJoins = (Vector)getJoins ().clone ();
	}

	/**
	 * Reverts fields to the image
	 */
	public void
	revertJoinsToImage ()
	{
		// must be imaging
		if (isJoinImaging ())
		{
			// set joins vector back to image
			_joins = _imageJoins;

			// clear the image and delete joins
			clearJoinImage ();
		}
	}

	/**
	 * End the imaging session and delete image deletes
	 */
	public void
	processJoinImage ()
		throws DataSourceException
	{
		// ensure delete Joins vector exists
		if (_deleteJoins == null)
			_deleteJoins = new Vector ();

		// iterate deletes
		JoinDobj j;
		for (int i = 0; i < _deleteJoins.size (); i++)
		{
			j = (JoinDobj)_deleteJoins.elementAt (i);
			if (j != null && j.getState () == DataObject.IN_DB)
				j.delete ();
		}

		// clear image
		clearJoinImage ();
	}

	/**
	 * Clear the image
	 */
	public void
	clearJoinImage ()
	{
		// ensure delete Joins vector exists
		if (_deleteJoins == null)
			_deleteJoins = new Vector ();

		// clear the image and join fields
		_imageJoins = null;
		_deleteJoins.clear ();
	}

	/**
	 * @return boolean true if join imaging
	 */
	public boolean
	isJoinImaging ()
	{
		return _imageJoins != null;
	}

	//--------------------------------------------------------------------------
	// DataViews

	/**
	 * Add a DataView to the DataView vector
	 * @param dv the DataView to add
	 */
	public void
	addDataView (
	 DataView dv)
	{
		// ensure views vector exists
		if (_views == null)
			_views = new Vector ();

		// validate
		Util.argCheckNull (dv);

		// add the entity
		_views.add (dv);

		// set the loaded flag
		_viewsLoaded = true;
	}

	/**
	 * Get a DataView by name from the DataSource
	 * @param dvName the name of the DataView
	 * @return DataView the DataView with the param name, or null if not found
	 */
	public DataView
	getDataView (
	 String dvName)
	{
		// iterate the entities
		DataView dv = null;
		for (int i = 0; i < getDataViews ().size (); i++)
		{
			// get the entity and compare
			dv = (DataView)getDataViews ().elementAt (i);
			if (dv != null && dv.getName ().equalsIgnoreCase (dvName))
				return dv;
		}

		// not found
		throw new RuntimeException ("DataView not found: " + dvName);//return null;
	}

	/**
	 * Get the Vector of DataViews
	 * @return Vector the DataViews
	 */
	public Vector
	getDataViews ()
	{
		// ensure views vector exists
		if (_views == null)
			_views = new Vector ();

		// if not loaded, load
		if (!_viewsLoaded)
		{
			// must be indb
			if (this.getState () == DataObject.IN_DB)
			{
				// build query
				DataSource ds = DataManager.getSystemDS ();
				Query q = new Query (DataView.ENT_DATAVIEW);
				q.addQueryEntity (DataViewField.ENT_DVFIELD);
				q.addQueryCriterium (new QueryCriterium (DataView.ENT_DATAVIEW,
					DataView.FLD_SOURCE_DSID, QueryCriterium.OP_EQUALS,
					new Integer (this.getId ())));
				q.addSort (new Sort (DataView.ENT_DATAVIEW, DataView.FLD_ID));
				q.addSort (new Sort (DataViewField.ENT_DVFIELD, DataViewField.FLD_ID));

				try
				{
					// execute query and build vector
					DataViewField f;
					DataView dv;
					RecordSet rs = ds.select (q);
					while (rs != null && rs.next ())
					{
						// get the entity
						f = (DataViewField)rs.getCurrentObject (DataViewField.ENT_DVFIELD);
						dv = (DataView)rs.getCurrentObject (DataView.ENT_DATAVIEW);
						if (dv != null && f != null)
						{
							// if entity added get the added one
							DataView found = null;
							DataView temp;
							for (int i = 0; found == null && i < _views.size (); i++)
							{
								// get temp and compare
								temp = (DataView)_views.elementAt (i);
								if (temp != null && temp.getId () == dv.getId ())
									found = temp;
							}

							// if not found add
							if (found == null)
							{
								found = dv;
								addDataView (found);
							}

							// add the field to the entity
							found.addField (f);
						}
					}
				}
				catch (Exception e)
				{
					throw new RuntimeException (e.toString ());
				}
			}

			// set flag
			_viewsLoaded = true;
		}

		return _views;
	}

	/**
	 * Remove a DataView
	 */
	public DataView
	removeDataView (
	 DataView dv,
	 boolean doDelete)
	{
		// if it is indb, remove by key
		DataView rem = null;
		if (dv.getState () == DataObject.IN_DB)
		{
			// iterate
			DataView temp;
			for (int i = 0; i < getDataViews ().size (); i++)
			{
				temp = (DataView)getDataViews ().elementAt (i);
				if (temp != null && temp.getState () == DataObject.IN_DB &&
					temp.getId () == dv.getId ())
					rem = (DataView)getDataViews ().remove (i);
			}
		}

		// else, try to remove by ref
		else
		{
			if (getDataViews ().remove (dv))
				rem = dv;
		}

		try
		{
			// delete
			if (doDelete && rem.getState () == DataObject.IN_DB)
				rem.delete ();
		}
		catch (Exception e)
		{
			throw new RuntimeException (e.toString ());
		}

		return rem;
	}

	/**
	 * @return true if the views are already loaded.
	 */
	public boolean
	areViewsLoaded ()
	{
		return _viewsLoaded;
	}
}
