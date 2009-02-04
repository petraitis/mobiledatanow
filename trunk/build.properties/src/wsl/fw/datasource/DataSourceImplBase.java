/**	$Id: DataSourceImplBase.java,v 1.2 2002/07/15 21:56:28 jonc Exp $
 *
 * Implementation base class for initial DataSource implementations
 * Used to share common code and attributes of DataSource concrete classes
 *
 */
package wsl.fw.datasource;

import java.util.Iterator;
import java.util.Vector;
import java.util.Hashtable;
import wsl.fw.util.Util;

public abstract class DataSourceImplBase
	implements DataSource
{
	//--------------------------------------------------------------------------
	// attributes

	private String _name = "";

	/**
	 * Insert a DataObject into the source
	 * @param dobj the DataObject to insert
	 * @return boolean true if the operation occurs
	 * @throws DataSourceException if there is an error from the DataSource.
	 * @roseuid 3973E78A0024
	 */
	public abstract boolean
	insert (
	 DataObject dobj)
		throws DataSourceException;

	/**
	 * Update a DataObject in the source
	 * @param dobj the DataObject to update
	 * @return boolean true if the operation occurs
	 * @throws DataSourceException if there is an error from the DataSource.
	 * @roseuid 3973E78A002E
	 */
	public abstract boolean
	update (
	 DataObject dobj)
		throws DataSourceException;

	/**
	 * Delete a DataObject from the source
	 * @param dobj the DataObject to delete
	 * @return boolean true if the operation occurs
	 * @throws DataSourceException if there is an error from the DataSource.
	 * @roseuid 3973E78A0042
	 */
	public abstract boolean
	delete (
	 DataObject dobj)
		throws DataSourceException;

	/**
	 * Select a RecordSet of DataObjects from the source
	 * @param query Query information
	 * @return DataObject
	 * @throws DataSourceException if there is an error from the DataSource.
	 * @roseuid 3973E78A0060
	 */
	public abstract RecordSet
	select (
	 Query query)
		throws DataSourceException;

	/**
	 * Return an Iterator to the DataObjects from the Source
	 * Optional alternative to select (), but you lose a lot of
	 * functionality
	 */
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
		return new Vector ();
	}

	/**
	 * Return the unique name of the DataSource
	 * @return String the DataSource name
	 * @throws DataSourceException if there is an error from the DataSource.
	 */
	public String
	getName ()
	{
		return _name;
	}

	/**
	 * Set the unique name of the DataSource
	 * @param String the DataSource name
	 * @throws DataSourceException if there is an error from the DataSource.
	 */
	public void
	setName (
	 String name)
	{
		_name = name;
	}


	//==========================================================================
	// Version 2

	//--------------------------------------------------------------------------
	// attributes

	private transient Vector _entities = new Vector ();
	private transient Vector _joins = new Vector ();

	//--------------------------------------------------------------------------
	// Entity
	/**
	 * Add an Entity to the Entity vector
	 * @param entity the Entity to add
	 */
	public void
	addEntity (
	 Entity entity)
	{
		// validate
		Util.argCheckNull (entity);

		// add the entity, replacing if one of the same name already exists
		int entIndex = -1;
		for (int i = 0; i < _entities.size (); i++)
		{
			String name1 = ( (Entity) _entities.elementAt (i)).getName ();
			String name2 = entity.getName ();
			if ( (name1 == null && name2 == null)
				|| (name1 != null && name2 != null && name1.equals (name2)))
			{
				// found entity with matching name, save index and stop
				entIndex = i;
				break;
			}
		}

		if (entIndex == -1)
			_entities.add (entity);
		else
			_entities.set (entIndex, entity);

		// set the entity parent ds
		entity.setParentDataSource (this);

		// add the joins
		Vector v = entity.getJoins ();
		for (int i = 0; v != null && i < v.size (); i++)
			addJoin ((Join)v.elementAt (i));
	}

	//--------------------------------------------------------------------------
	/**
	 * Get an entity by name from the the DataSource
	 * @param entityName the name of the Entity
	 * @return Entity the Entity with the param name, or null if not found
	 */
	public Entity
	getEntity (
	 String entityName)
	{
		// iterate the entities
		Entity ent = null;
		for (int i = 0; i < getEntities ().size (); i++)
		{
			// get the entity and compare
			ent = (Entity)getEntities ().elementAt (i);
			if (ent != null && ent.getName ().equalsIgnoreCase (entityName))
				return ent;
		}

		// if not found, get Entity from DataManager
		ent = DataManager.createEntity (entityName);
		if (ent != null)
		{
			// verify definition and add to data source
			ent.verifyEntityDefinition ();
			addEntity (ent);

			// return
			return ent;
		}

		// not found
		throw new RuntimeException ("Entity not found: " + entityName);//return null;
	}

	//--------------------------------------------------------------------------
	/**
	 * Get the Vector of Entities
	 * @return Vector the Entities
	 */
	public Vector
	getEntities ()
	{
		// return
		return _entities;
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
		// add the join, avoiding duplication
		for (int i = 0; i < _joins.size (); i++)
			if ( ((Join) _joins.elementAt (i)).equals (join))
				return;
		_joins.add (join);
	}

	//--------------------------------------------------------------------------
	/**
	 * Get the Vector of Joins
	 * @return Vector the Joins
	 */
	public Vector
	getJoins ()
	{
		return _joins;
	}
}
