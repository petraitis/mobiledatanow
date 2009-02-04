/**	$Id: DataViewDataSource.java,v 1.2 2002/07/15 22:17:42 jonc Exp $
 *
 */
package wsl.mdn.dataview;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.datasource.*;

public class DataViewDataSource
	implements DataSource
{
	/**
	 * Default ctor
	 */
	public
	DataViewDataSource ()
	{
	}
	
	/**
	 * Insert a DataObject into the source
	 * @param dobj the DataObject to insert
	 * @return boolean true if the operation occurs
	 * @throws DataSourceException if there is an error from the DataSource.
	 */
	public boolean
	insert 
	 (DataObject dobj)
		throws DataSourceException
	{
		// validate
		Util.argCheckNull (dobj);
		Record dvr = (Record)dobj;

		// get the DataView
		DataView dv = (DataView)dobj.getEntity ();
		Util.argCheckNull (dv);

		// get the source ds
		DataSource sourceDs = MdnDataCache.getCache ().getDataSource (dv.getSourceDsId ());

		// iterate the dvfs and set values into source record
		Vector fields = dv.getFields ();
		Record sourceRec;
		String entityName;
		DataViewField dvf;
		Field sourceField;
		for (int i = 0; fields != null && i < fields.size (); i++)
		{
			// get the field
			dvf = (DataViewField)fields.elementAt (i);
			if (dvf != null)
			{
				// get source entity name
				entityName = dvf.getSourceEntity ();
				if (entityName != null && entityName.length () > 0)
				{
					// get the source rec
					// if rec not in hash, create and add
					sourceRec = (Record)dvr.getSourceRecord (entityName);
					if (sourceRec == null)
					{
						sourceRec = new Record (sourceDs.getEntity (entityName));
						dvr.addSourceRecord (sourceRec);
					}

					// set the value into the source rec (if not readonly)
					sourceField = sourceDs.getEntity (entityName).getField (dvf.getSourceField ());
					if (sourceField != null && !sourceField.hasFlag (Field.FF_READ_ONLY))
						sourceRec.setValue (sourceField.getName (), dobj.getStringValue (dvf.getName ()));
				}
			}
		}

		// insert the record (s)
		Vector sourceRecs = dvr.getSourceRecords ();
		for (int i = 0; i < sourceRecs.size (); i++)
		{
			// get the rec and insert
			sourceRec = (Record)sourceRecs.elementAt (i);
			if (sourceRec != null)
				sourceRec.save ();
		}

		return true;
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
		// validate
		Util.argCheckNull (dobj);

		// get the DataView
		DataView dv = (DataView)dobj.getEntity ();
		Util.argCheckNull (dv);
		Record dvr = (Record)dobj;

		// get the source ds
		DataSource sourceDs = MdnDataCache.getCache ().getDataSource (dv.getSourceDsId ());

		// iterate the dvfs and set values into source records
		Vector fields = dv.getFields ();
		Record rec;
		String entityName;
		DataViewField dvf;
		Field sourceField;
		for (int i = 0; fields != null && i < fields.size (); i++)
		{
			// get the field
			dvf = (DataViewField)fields.elementAt (i);
			if (dvf != null)
			{
				// get source entity name
				entityName = dvf.getSourceEntity ();
				if (entityName != null && entityName.length () > 0)
				{
					// get the source rec
					// if rec not in hash, create and add
					rec = (Record)dvr.getSourceRecord (entityName);
					if (rec == null)
						throw new RuntimeException ("Source record not found");

					// set the value into the source rec (if not readonly)
					sourceField = sourceDs.getEntity (entityName).getField (dvf.getSourceField ());
					if (sourceField != null && !sourceField.hasFlag (Field.FF_READ_ONLY))
						rec.setValue (sourceField.getName (), dobj.getStringValue (dvf.getName ()));
				}
			}
		}

		// iterate the source recs and save
		Vector sourceRecs = dvr.getSourceRecords ();
		for (int i = 0; i < sourceRecs.size (); i++)
		{
			// get the rec and save
			rec = (Record)sourceRecs.elementAt (i);
			if (rec != null)
				rec.save ();
		}

		return true;
	}

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
		Util.argCheckNull (dobj);

		// get the DataView
		DataView dv = (DataView)dobj.getEntity ();
		Util.argCheckNull (dv);

		// get the source ds
		DataSource sourceDs = MdnDataCache.getCache ().getDataSource (dv.getSourceDsId ());

		// iterate the dvfs and create a map of source entities
		Hashtable sourceEnts = new Hashtable ();
		Vector fields = dv.getFields ();
		String entityName = "";
		DataViewField dvf;
		for (int i = 0; fields != null && i < fields.size (); i++)
		{
			// get the field
			dvf = (DataViewField)fields.elementAt (i);
			if (dvf != null)
			{
				// get source entity name
				entityName = dvf.getSourceEntity ();
				if (entityName != null && entityName.length () > 0)
				{
					// add to map if not in it
					if (sourceEnts.get (entityName) == null)
						sourceEnts.put (entityName, entityName);
				}
			}
		}

		// no entities
		Record dvr = (Record)dobj;
		Record delRec = null;
		if (sourceEnts.size () == 0)
			throw new RuntimeException ("No entities found");

		// if only 1 entity, then delete it
		else if (sourceEnts.size () == 1)
			delRec = dvr.getSourceRecord (entityName);

		// multiple entities
		else
		{
			// build a vector of valid source joins
			Enumeration elementEnum;
			Join join;
			Vector validJoins = new Vector ();
			Vector allJoins = sourceDs.getJoins ();
			for (int i = 0; allJoins != null && i < allJoins.size (); i++)
			{
				// get the join
				join = (Join)allJoins.elementAt (i);
				if (join != null)
				{
					// iterate entities
					elementEnum = sourceEnts.elements ();
					boolean hasLeft = false;
					boolean hasRight = false;
					while (elementEnum != null && elementEnum.hasMoreElements ())
					{
						// get the entity
						entityName = (String)elementEnum.nextElement ();

						// hasleft, hasRight
						if (join.getLeftEntity ().equalsIgnoreCase (entityName))
							hasLeft = true;
						else if (join.getRightEntity ().equalsIgnoreCase (entityName))
							hasRight = true;
					}

					// if hasLeft and hasRight, then valid join
					if (hasLeft && hasRight)
						validJoins.add (join);
				}
			}

			// get the left-most entity
			entityName = getLeftMostEntity (validJoins);
			if (entityName == null)
				throw new RuntimeException ("Unable to find left-most entity");

			// get and delete the delete rec
			delRec = dvr.getSourceRecord (entityName);
		}

		// must have a rec
		if (delRec == null)
			throw new RuntimeException ("Unable to find source record");

		// delete the delRec
		delRec.delete ();

		return true;
	}

	/**
	 * Find the left most entity
	 * @param joins a vector joins to search
	 * @return String the left-most entity
	 */
	private String
	getLeftMostEntity (
	 Vector joins)
	{
		// must have joins
		if (joins == null || joins.size () == 0)
			return null;

		// iterate joins
		Join join, temp;
		String left = null;
		for (int i = 0; i < joins.size (); i++)
		{
			// get the join
			join = (Join)joins.elementAt (i);

			// get left
			left = join.getLeftEntity ();

			// iterate all joins, and see if it is a right
			boolean isValid = true;
			for (int j = 0; isValid && j < joins.size (); j++)
			{
				temp = (Join)joins.elementAt (j);
				if (temp != null && temp.getRightEntity ().equalsIgnoreCase (left))
					isValid = false;
			}

			// if valid we have found it
			if (isValid)
				return left;
		}

		return null;	// not found
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
		return "";
	}

	/**
	 * Set the unique name of the DataSource
	 * @param String the DataSource name
	 */
	public void
	setName (
	 String name)
	{
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
		return null;
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
		return -1;
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
		// delegate
		throw new RuntimeException ("Not Implemented");
	}

	/**
	 * Get the Vector of Entities
	 * @return Vector the Entities
	 */
	public Vector
	getEntities ()
	{
		return null;
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
		//TODO createEntityTable fixme
	}

	//--------------------------------------------------------------------------
	// Join

	/**
	 * Add a Join to the join Vector
	 * @param join the Join to add
	 */
	public void
	addJoin (
	 Join join)
	{
	}

	/**
	 * Get the Vector of Joins
	 * @return Vector the Joins
	 */
	public Vector
	getJoins ()
	{
		return null;
	}

	//--------------------------------------------------------------------------
	// misc

	/**
	 * Get an DataView by name from the the DataSource
	 * @param name the name of the DataView
	 * @return DataView the DataView with the param name, or null if not found
	 */
	public DataView
	getDataView (
	 int dvid)
	{
		// get it from the cache
		try
		{
			MdnDataCache cache = MdnDataCache.getCache ();
			Util.argCheckNull (cache);
			return cache.getDataView (new Integer (dvid));
		}
		catch (Exception e)
		{
			throw new RuntimeException (e.toString ());
		}
	}

	/**
	 * Select a RecordSet of DataObjects from the source
	 * @param query Query information
	 * @return DataObject
	 * @throws DataSourceException if there is an error from the DataSource.
	 */
	public RecordSet
	select (
	 QueryDobj query)
		throws DataSourceException
	{
		// validate
		Util.argCheckNull (query);

		// if the query has no dv, get one
		DataView dv = query.getDataView (false);
		if (dv == null)
			dv = getDataView (query.getViewOrTableId ());
		Util.argCheckNull (dv);

		// delegate
		return (dv == null)? null: select (query.createImpl (), dv);
	}

	/**
	 * Select a RecordSet of DataObjects from the source
	 * @param query Query information
	 * @return DataObject
	 * @throws DataSourceException if there is an error from the DataSource.
	 */
	public RecordSet
	select (
	 Query query)
		throws DataSourceException
	{
		throw new RuntimeException ("Not Implemented");
	}

	public RecordItrRef
	iSelect (
	 Query q)
		throws DataSourceException
	{
		throw new DataSourceException ("Not Implemented");
	}

	public boolean
	iHasNextObj (
	 RecordItrRef r)
		throws DataSourceException
	{
		throw new DataSourceException ("Not Implemented");
	}

	public Object
	iNextObj (
	 RecordItrRef r)
		throws DataSourceException
	{
		throw new DataSourceException ("Not Implemented");
	}

	/**
	 * Select a RecordSet of DataObjects from the source
	 * @param query Query information
	 * @return DataObject
	 * @throws DataSourceException if there is an error from the DataSource.
	 */
	public RecordSet
	select (
			Query query,
			DataView dv
	 )
		throws DataSourceException
	{
		// delegate
		return select (query, dv, (Vector)null);
	}

	/**
	 * Select a RecordSet of DataObjects from the source
	 * @param query Query information
	 * @param dv the DataView to select for
	 * @param extraSrcCriteria extra source criteria
	 * @return DataObject
	 * @throws DataSourceException if there is an error from the DataSource.
	 */
	private RecordSet
	select (
	 Query query,
	 DataView dv,
	 Vector extraSrcCriteria)
		throws DataSourceException
	{
		// validate
		Util.argCheckNull (query);
		Util.argCheckNull (dv);

		if (query instanceof DirectQuery)
			return execDirectSQL ((DirectQuery)query, dv);

		// create a ds query
		Query dsQuery = new Query ();

		// add the entities
		DataViewField dvf;
		Vector fields = dv.getFields ();
		for (int i = 0; fields != null && i < fields.size (); i++)
		{
			// get the dvf and add entity
			dvf = (DataViewField)fields.elementAt (i);
			if (dvf != null)
			{
				// not needed, addQueryField also adds the entity
				// dsQuery.addQueryEntity (dvf.getSourceEntity ());

				// add the field to query fields
				// note the act of adding query fields menas that the query will
				// use field exclusions and only the specificly added fields
				// (and the reqd keys) will be inited in the data objects
				dsQuery.addQueryField (dvf.getSourceEntity (), dvf.getSourceField ());
			}
		}

		// criteria
		Vector v = query.getCriteria ();
		QueryCriterium dvqc, dsqc;
		for (int i = 0; v != null && i < v.size (); i++)
		{
			// get the view qc
			dvqc = (QueryCriterium)v.elementAt (i);
			if (dvqc != null)
			{
				// get the dvfield
				dvf = (DataViewField)dv.getField (dvqc._fieldName);
				if (dvf != null)
				{
					// create a ds qc and set attribs
					dsqc = new QueryCriterium ();
					dsqc._entityName = dvf.getSourceEntity ();
					dsqc._fieldName = dvf.getSourceField ();
					dsqc._op = dvqc._op;
					dsqc._value = dvqc._value;
					
					System.out.println("dsqc._entityName: " + dsqc._entityName + " - QueryCriterium: " + dsqc);

					// add to the ds query
					dsQuery.addQueryCriterium (dsqc);
				}
			}
		}

		// extra source criteria
		for (int i = 0; extraSrcCriteria != null && i < extraSrcCriteria.size (); i++)
		{
			// add the extra criterium
			dsqc = (QueryCriterium)extraSrcCriteria.elementAt (i);
			if (dsqc != null)
				dsQuery.addQueryCriterium (dsqc);
		}

		// sorts
		v = query.getSorts ();
		Sort dvs, dss;
		for (int i = 0; v != null && i < v.size (); i++)
		{
			// get the view sort
			dvs = (Sort)v.elementAt (i);
			if (dvs != null)
			{
				// get the dvfield
				dvf = (DataViewField)dv.getField (dvs._fieldName);
				if (dvf != null)
				{
					// create a ds sort and set attribs
					dss = new Sort (dvf.getSourceEntity (), dvf.getSourceField (),
						dvs._direction);

					// add to the ds query
					dsQuery.addSort (dss);
				}
			}
		}

		// get the datasource
		DataSource sourceDs = MdnDataCache.getCache ().getDataSource (dv.getSourceDsId ());
		if (sourceDs == null)
			throw new RuntimeException ("Source DataSource not found, ID = " + dv.getSourceDsId ());

		System.out.println("dsQuery: " + dsQuery);
		// get the source recordset
		RecordSet sourceRs = sourceDs.select (dsQuery);
		
		System.out.println("sourceRs: " + sourceRs.size());
		
		// iterate source recordset
		RecordSet viewRs = new RecordSet ();
		Hashtable hashRow = new Hashtable (89);
		DataObject row;
		Vector dobjs;
		DataObject rec;
		Record viewRec;
		while (sourceRs != null && sourceRs.next ())
		{
			// build the hashRow
			hashRow.clear ();
			row = (DataObject)sourceRs.getCurrentObject ();
			if (row instanceof Row)
			{
				dobjs = ( (Row)row).getComponents ();
				for (int j = 0; dobjs != null && j < dobjs.size (); j++)
				{
					rec = (DataObject)dobjs.elementAt (j);
					if (rec != null)
						hashRow.put (rec.getEntity ().getName (), rec);
				}
			}
			else
				hashRow.put (row.getEntity ().getName (), row);

			// create a new view record
			viewRec = new Record (dv);
			viewRec.setState (DataObject.IN_DB);

			// iterate view fields
			Object val;
			for (int i = 0; fields != null && i < fields.size (); i++)
			{
				// get the field
				dvf = (DataViewField)fields.elementAt (i);

				// get the rec from the hashRow
				rec = (DataObject)hashRow.get (dvf.getSourceEntity ());
				if (rec != null)
				{
					// get the field value
					//val = rec.getStringValue (dvf.getSourceField ());
					val = rec.getObjectValue (dvf.getSourceField ());
					if (val != null)
					{
						//set the value into the view rec
						viewRec.setValue (dvf.getName (), val);
					}
				}
			}

			// add the source records to the view rec
			Enumeration elementsEnum = hashRow.elements ();
			while (elementsEnum != null && elementsEnum.hasMoreElements ())
				viewRec.addSourceRecord ((Record)elementsEnum.nextElement ());

			// add the view record to the view recset
			viewRs.addRow (viewRec);
		}

		// return the view rs
		return viewRs;
	}

	//--------------------------------------------------------------------------
	// select distinct

	/**
	 * Select a Vector of distinct Strings from a view
	 * @param query
	 */
	public Vector
	selectDistinct (
	 QueryDobj query)
		throws DataSourceException
	{
		// validate
		Util.argCheckNull (query);

		// get the group field id
		Vector ret = null;
		int gfid = query.getGroupFieldId ();
		if (gfid >= 0)
		{
			// get the view
			DataView dv = query.getDataView (false);
			if (dv == null)
				dv = getDataView (query.getViewOrTableId ());
			Util.argCheckNull (dv);

			// get the dvf
			DataViewField dvf = (DataViewField)dv.getField (gfid);
			if (dvf != null)
			{
				// get the datasource
				DataSource sourceDs = MdnDataCache.getCache ().getDataSource (dv.getSourceDsId ());
				if (sourceDs == null)
					throw new RuntimeException ("Source DataSource not found, ID = " + dv.getSourceDsId ());

				// do a select distinct
				ret = sourceDs.selectDistinct (dvf.getSourceEntity (),
					dvf.getSourceField ());
			}
		}

		// return
		return (ret == null)? new Vector (): ret;
	}

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
		throw new RuntimeException ("Not Implemented");
	}

	//--------------------------------------------------------------------------
	// select with parent rec

	/**
	 * Select a RecordSet of DataObjects from the source
	 * @param query Query information
	 * @return DataObject
	 * @throws DataSourceException if there is an error from the DataSource.
	 */
	public RecordSet
	select (
	 QueryDobj query,
	 Record parentRec)
		throws DataSourceException
	{
		// validate
		Util.argCheckNull (query);

		// if the query has no dv, get one
		DataView dv = query.getDataView (false);
		if (dv == null)
			dv = getDataView (query.getViewOrTableId ());
		Util.argCheckNull (dv);

		// delegate
		return (dv == null)? null: select (query.createImpl (), dv, parentRec);
	}

	/**
	 * Perform a query that establishes criteria from a parent record
	 * @param q the query to perform
	 * @param dv the DataView to select from
	 * @param parentRec the parent Record that will provide extra criteria for the select
	 * @return RecordSet the resulting RecordSet
	 */
	public RecordSet
	select (
	 Query q,
	 DataView dv,
	 Record parentRec)
		throws DataSourceException
	{
		// validate
		Util.argCheckNull (q);
		Util.argCheckNull (dv);
		Util.argCheckNull (parentRec);

		// get the joins between current view tables and parent tables
		// get the source ds
		DataSource sourceDs = MdnDataCache.getCache ().getDataSource (dv.getSourceDsId ());
		if (sourceDs == null)
			throw new RuntimeException ("Source DataSource not found, ID = " + dv.getSourceDsId ());

		if (q instanceof DirectQuery)
			return execDirectSQL ((DirectQuery)q, dv);

		// get the source join criteria
		Vector extraSrcCriteria = getSourceJoinCriteria (dv, parentRec, sourceDs);

		// execute query
		return select (q, dv, extraSrcCriteria);
	}

	/**
	 * Builds a vectro of criteria for the join to the parent record
	 * dataview of the parent and dataview of the child
	 * @param childDv the child DataView
	 * @param parentRec the parent Record
	 * @param sourceDs the source DataSource
	 * @return Vector QueryCriteria for the source query
	 */
	private Vector
	getSourceJoinCriteria (
	 DataView childDv,
	 Record parentRec,
	 DataSource sourceDs)
	{
		// validate
		Util.argCheckNull (childDv);
		Util.argCheckNull (parentRec);
		Util.argCheckNull (sourceDs);

		// get child and parent entities
		DataView parentDv = (DataView)parentRec.getEntity ();
		Vector childEnts = getSourceEntities (childDv, sourceDs);
		Vector parentEnts = getSourceEntities (parentDv, sourceDs);

		// get the source ds joins
		Vector joins = sourceDs.getJoins ();

		// iterate child ents
		Vector ret = new Vector ();
		Entity childEnt;
		for (int i = 0; joins != null && i < childEnts.size (); i++)
		{
			// get the child ent
			childEnt = (Entity)childEnts.elementAt (i);

			// iterate parent entities
			Entity parentEnt;
			for (int j = 0; j < parentEnts.size (); j++)
			{
				// get the parent ent
				parentEnt = (Entity)parentEnts.elementAt (j);

				// iterate the joins
				Join join;
				Object parentVal;
				for (int k = 0; k < joins.size (); k++)
				{
					// get the join
					join = (Join)joins.elementAt (k);

					// child is left
					Field childField = null;
					Field parentField = null;
					if (join.getLeftEntity ().equals (childEnt.getName ()) &&
						join.getRightEntity ().equals (parentEnt.getName ()))
					{
						// get the child field with the appropriate entity, field
						childField = childEnt.getField (join.getLeftKey ());

						// get the parent field with the appropriate entity, field
						parentField = parentEnt.getField (join.getRightKey ());
					}

					// child is right
					else if (join.getLeftEntity ().equals (parentEnt.getName ()) &&
						join.getRightEntity ().equals (childEnt.getName ()))
					{
						// get the child field with the appropriate entity, field
						childField = childEnt.getField (join.getRightKey ());

						// get the parent field with the appropriate entity, field
						parentField = parentEnt.getField (join.getLeftKey ());
					}

					// if fields found add to hash
					if (childField != null && parentField != null)
					{
						// get the parent value
						parentVal = parentRec.getSourceValue (parentEnt.getName (),
							parentField.getName ());

						// create a query criterium and add
						QueryCriterium qc = new QueryCriterium (
							childEnt.getName (), childField.getName (),
							QueryCriterium.OP_EQUALS,
							parentVal);
						ret.add (qc);
					}
				}
			}
		}

		// return
		return ret;
	}

	/**
	 * @return a unique Vector of source entities in a DataView
	 * @param dv the DataView
	 * @param sourceDs the source DataSource
	 */
	private Vector
	getSourceEntities (
	 DataView dv,
	 DataSource sourceDs)
	{
		// validate
		Util.argCheckNull (dv);
		Util.argCheckNull (sourceDs);

		// build vector of entities
		Vector ents = new Vector ();
		Entity ent;
		DataViewField dvf;
		Vector fields = dv.getFields ();
		for (int i = 0; fields != null && i < fields.size (); i++)
		{
			// get the field
			dvf = (DataViewField)fields.elementAt (i);
			if (dvf != null)
			{
				// get the entity from the source ds
				ent = sourceDs.getEntity (dvf.getSourceEntity ());
				Util.argCheckNull (ent);

				// if not in the ent vector, add it
				if (!ents.contains (ent))
					ents.add (ent);
			}
		}

		return ents;
	}

	/**
	 * test main
	 */
	public static void
	main (
	 String args [])
	{
		try
		{
			// set the ResourceManager
			wsl.fw.resource.ResourceManager.set (new wsl.mdn.common.MdnResourceManager ());

			// set the config
			wsl.fw.util.Config.setSingleton (wsl.mdn.common.MdnAdminConst.MDN_CONFIG_FILE, true);
			wsl.fw.util.Config.getSingleton ().addContext (wsl.fw.util.CKfw.RMICLIENT_CONTEXT);

			// set the DataManager
			wsl.fw.datasource.DataManager.setDataManager (new wsl.mdn.common.MdnDataManager ());

			// query
			Query q = new Query ("Product");
			RecordSet rs = wsl.mdn.common.MdnDataManager.getDataViewDS ().select (q);
			while (rs != null && rs.next ())
				System.out.println (rs.getCurrentObject ().getLongDesc ());
			Thread.currentThread ().sleep (1000);
		}
		catch (Exception e)
		{
			Log.error ("DataViewDataSource.main: " ,e);
		}
	}

	//--------------------------------------------------------------------------
	/**
	 * @return an identifier that can be used to lookup this DataSource. This
	 *   should be a DataSourceParam or a string name.
	 */
	public Object
	getDsId ()
	{
		return null;
	}

	//--------------------------------------------------------------------------
	/**
	 * Import a the names of tables from the datasource.
	 * @return a Vector of EntitySchemaNames containing the table names.
	 */
	public Vector
	importTableNames ()
		throws DataSourceException
	{
		return null;
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
		return null;
	}

	/**
	 * Import an entity definition from a raw SQL select statement.
	 */
	public Entity
	importRawSelectDefinition (
	 String selectStmt)
		throws DataSourceException
	{
		return null;
	}

	/**
	 * Executes a direct SQL query.  The return value is a hashtable with
	 * keys that match the query resultset's column names.  Each column
	 * (the hashtable value) is a Vector
	 */
	public Hashtable
	execDirectSQL (
	 String sql)
		throws DataSourceException
	{
		throw new DataSourceException ("execDirectSQL () not implemented");
	}
	public RecordSet execDirectSQL (int dataSourceId, String sql,
			 DataView dv)
		throws DataSourceException
	{
		// get the datasource
		DataSource sourceDs = MdnDataCache.getCache ().getDataSource (dataSourceId);
		if (sourceDs == null)
			throw new RuntimeException ("Source DataSource not found, ID = " + dataSourceId);
	
		// execute the query and get the source data
		System.out.println("SQL: " + sql);
		// execute the query and get the source data
		Hashtable hash = sourceDs.execDirectSQL(sql);
		
		return getRecordSet(hash, dv);
	}
	public RecordSet execDirectSQL (String sql,
			 DataView dv)
		throws DataSourceException
	{
		// get the datasource
		DataSource sourceDs = MdnDataCache.getCache ().getDataSource (dv.getSourceDsId ());
		if (sourceDs == null)
			throw new RuntimeException ("Source DataSource not found, ID = " + dv.getSourceDsId ());
	
		// execute the query and get the source data
		System.out.println("SQL: " + sql);
		// execute the query and get the source data
		Hashtable hash = sourceDs.execDirectSQL(sql);
		
		return getRecordSet(hash, dv);
	}	
	public int execInsertOrUpdate(String arg0) throws DataSourceException {
		throw new DataSourceException ("execInsertOrUpdate () not implemented");
	}	
	/**
	 * Exectues a direct SQL update or insert query.  The return value is row number which executes
	 */	
	public int execInsertOrUpdate (int dataSourceId, String sql)
		throws DataSourceException
	{
		// get the datasource
		DataSource sourceDs = MdnDataCache.getCache ().getDataSource (dataSourceId);
		if (sourceDs == null)
			throw new RuntimeException ("Source DataSource not found, ID = " + dataSourceId);
	
		// execute the query and get the source data
		System.out.println("SQL: " + sql);
		int row = sourceDs.execInsertOrUpdate (sql);
		
		return row ;	
	}
	/**
	 * execute insert / update sql directly
	 * @param query QueryDobj
	 * @return boolean true if success
	 * @throws DataSourceException
	 */
	public boolean execInsertOrUpdate (QueryDobj query)
		throws DataSourceException
	{
		//If query is insert/update
		EntityDobj entity = query.getTable(true);
		
		// get the datasource
		DataSource sourceDs = MdnDataCache.getCache ().getDataSource (entity.getDataSourceId());
		if (sourceDs == null)
			throw new RuntimeException ("Source DataSource not found, ID = " + entity.getDataSourceId());
	
		// execute the query and get the source data
		System.out.println("SQL: " + query.getCriteriaString());
		int row = sourceDs.execInsertOrUpdate (query.getCriteriaString());
		
		return row > 0 ? true : false;		
	} 	
	
	private RecordSet execDirectSQL (
						 DirectQuery query,
						 DataView dv)
		throws DataSourceException
	{
		// get the datasource
		DataSource sourceDs = MdnDataCache.getCache ().getDataSource (dv.getSourceDsId ());
		if (sourceDs == null)
			throw new RuntimeException ("Source DataSource not found, ID = " + dv.getSourceDsId ());

		// execute the query and get the source data
		Hashtable hash = sourceDs.execDirectSQL (query.getSQL ());

		return getRecordSet(hash, dv);
	}

	private RecordSet getRecordSet(Hashtable hash, DataView dv){
		// Get the hashtable keys and turn them into an array
		Collection vals = hash.values ();
		Iterator iter = vals.iterator ();
		if (!iter.hasNext ())
			throw new RuntimeException ("Source DataSource invalid.  It contains no columns.");

		// Get the row count from the first columns Vector
		Vector col = (Vector)iter.next ();
		int rowCount = col.size ();

		RecordSet viewRs = new RecordSet ();
		Record viewRec;
		DataViewField dvf = null;
		Vector fields = dv.getFields ();
		
		List usedFieldsNamesLst = new ArrayList();
		
		for (int row = 0; row < rowCount; row++)
		{
			// create a new view record
			viewRec = new Record (dv);
			viewRec.setState (DataObject.IN_DB);

			// iterate view fields
			Object val;
			for (int i = 0; fields != null && i < fields.size (); i++)
			{
				// get the field
				dvf = (DataViewField)fields.elementAt (i);
				
				//Take out the table name from field name
				/*String fieldName = dvf.getName ();
				int pos = fieldName.indexOf(".");
				if (pos > 0){
					fieldName = fieldName.substring(pos + 1);
				}*/
				//System.out.println("fieldName: " + fieldName);
				String fieldName = dvf.getSourceField();
				
				// get the rec from the hashRow
				col = (Vector)hash.get (fieldName);
				if (col != null)
				{
					val = col.elementAt (row);

					if (val != null)
					{
						//set the value into the view rec
						viewRec.setValue (dvf.getName (), val.toString ());
						usedFieldsNamesLst.add(fieldName);
					}
				}
			}
			//----------- If there is any aggregated function result  -----------//
			Object val2;
			Set fieldsAsKeys = hash.keySet();
			Iterator fieldsAsKeysItr = fieldsAsKeys.iterator();
			while (fieldsAsKeysItr.hasNext()) {
				String fieldName = (String) fieldsAsKeysItr.next();
				if(!usedFieldsNamesLst.contains(fieldName)){
					Vector valVec = (Vector)hash.get(fieldName);
					if(valVec != null){
						val2 = valVec.elementAt (row);
						if(val2 != null){
							//System.out.println(row + "- Name= " + fieldName + ",  Value= " + val2.toString());
							viewRec.setValue(fieldName, val2.toString());
						}
					}
				}
			}
			//------------------------------------------------------------------//

			// add the view record to the view recset
			viewRs.addRow (viewRec);
		}
		// return the view rs
		return viewRs;		
	}
	
	/**
	 * Drop (in the database underlying the datasource) the table for this
	 * entity.
	 * @param entityName, the name of the entity whose table is to be created.
	 * @throws DataSourceException if the creation failed, usually due to the
	 *   table already existing
	 */
	public void dropEntityTable (String entityName)
		throws DataSourceException
	{
		// stub
	}

	/**
	 * Test that the data source can connect to the data base.
	 * @throws DataSourceException if the connection cannot be made.
	 */
	public void testConnection ()
		throws DataSourceException
	{
		// stub
	}


}
