/**	$Id: Query.java,v 1.3 2003/06/12 23:27:28 tecris Exp $
 *
 * Encapsulates all required data for a DataSource select query.
 *
 */
package wsl.fw.datasource;

// imports
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.io.Serializable;

public class Query implements Serializable
{
    /**
     * attributes
     */
    private Vector    _criteria = null;
    private Vector    _sorts    = null;
    private Hashtable _entities = null;
    private boolean   _hasFieldExclusions = false;

    //--------------------------------------------------------------------------
    /**
     * Add an entity to query on
     * @param entityName the entity name to add
     * @return void
     * @roseuid 3973D34E02BB
     */
    public void addQueryEntity(String entityName)
    {
        // if null, create vector
        if(_entities == null)
            _entities = new Hashtable();

        // add the query entity
        if(entityName != null)
            if (_entities.get(entityName) == null)
                _entities.put(entityName, new HashSet());
    }

    //--------------------------------------------------------------------------
    /**
     * Add a fields to the query.
     * By default a query will have no fields and hasFieldExclusions() will
     * return false.
     * In this case executing the the query will fill in all fields for all
     * entities in the query.
     * If ANY fields are added then the query will only fill in data for the
     * fields explicitly named hasFieldExclusions() will be true.
     * This function may be used to limit the work done by the database,
     * DataManager and DataSource.
     * Note that the DataSource forces the addition of all KEY fields to ensure
     * the data objects are valid and update and delete will work.
     * @param entityName, the name of the entity to add a field for.
     * @param fieldName, the name of the field.
     */
    public void addQueryField(String entityName, String fieldName)
    {
        // add the entity
        addQueryEntity(entityName);

        // add the field to that entity
        Set fieldSet = (Set) _entities.get(entityName);
        fieldSet.add(fieldName);

        // set flag to indicate this query has fields
        _hasFieldExclusions = true;
    }

    //--------------------------------------------------------------------------
    /**
     * Add a QueryCriterium to the Query
     * @param qc the QueryCriterium to add
     * @return void
     * @roseuid 3973D34E02BD
     */
    public void addQueryCriterium(QueryCriterium qc)
    {
        // if null, create vector
        if(_criteria == null)
            _criteria = new Vector();

        // add the query criterium
        if(qc != null)
        {
            _criteria.add(qc);

            // add the entity
            addQueryEntity(qc._entityName);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Add a QueryCriterium to the Query
     * @param sort the Sort to add
     * @return void
     * @roseuid 3973D34E02BE
     */
    public void addSort(Sort sort)
    {
        // if null, create vector
        if(_sorts == null)
            _sorts = new Vector();

        // add the query entity
        if(sort != null)
            _sorts.add(sort);
    }

    //--------------------------------------------------------------------------
    /**
     * Return the Vector of QueryCriterium objects
     * @return Vector
     * @roseuid 3973D4150357
     */
    public Vector getCriteria()
    {
        return _criteria;
    }

    //--------------------------------------------------------------------------
    /**
     * Return the Vector of Sort objects
     * @return Vector
     * @roseuid 3973D4240164
     */
    public Vector getSorts()
    {
        return _sorts;
    }

    //--------------------------------------------------------------------------
    /**
     * Return the Vector of entity String objects
     * @return Vector
     * @roseuid 3973D4310325
     */
    public Enumeration getQueryEntities()
    {
        return (_entities == null)? null: _entities.keys();
    }

    //--------------------------------------------------------------------------
    /**
     * Return the query entities as a hash table
     * @return Hashtable the query entities
     */
    public Hashtable getEntitiesHash()
    {
        return _entities;
    }

    //--------------------------------------------------------------------------
    /**
     * Determine if the field should be excluded.
     * @param entityName, the name of the entity.
     * @param fieldName, the name of the field.
     * @return true if we are using field exclusions and the named field is not
     *   one of the explicitly added fields.
     */
    public boolean isFieldExcluded(String entityName, String fieldName)
    {
        if (_hasFieldExclusions && _entities != null
            && entityName != null && fieldName != null)
        {
            Set fieldSet = (Set) _entities.get(entityName);
            if (fieldSet != null && !fieldSet.contains(fieldName))
                return true;
        }

        return false;
    }

    //--------------------------------------------------------------------------
    /**
     * @return true if this query has fields.
     */
    public boolean hasFieldExclusions()
    {
        return _hasFieldExclusions;
    }

    //--------------------------------------------------------------------------
    /**
     * Returns the number of query entities
     * @return int the number of query entities
     */
    public int getNumberQueryEntities()
    {
        return (_entities == null)? 0: _entities.size();
    }

    //--------------------------------------------------------------------------
    /**
     * Blank constructor
     * @roseuid 3973DF970371
     */
    public Query()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Create a query with a single QueryCriterium
     * @param qc the QueryCriterium
     * @roseuid 3973DFB8031E
     */
    public Query(QueryCriterium qc)
    {
        // add the qc
        addQueryCriterium(qc);
    }

    //--------------------------------------------------------------------------
    /**
     * Create a query with a single entity name
     * @param entityName the entity name
     * @roseuid 3973DFC300F3
     */
    public Query(String entityName)
    {
        // add the query entity
        addQueryEntity(entityName);
    }

    //--------------------------------------------------------------------------
    /**
     * Create a query with 2 entity names
     * @param entityName1 the first entity name
     * @param entityName1 the first entity name
     */
    public Query(String entityName1, String entityName2)
    {
        // add the query entities
        addQueryEntity(entityName1);
        addQueryEntity(entityName2);
    }

    //--------------------------------------------------------------------------
    /**
     * Create a query with 2 QueryCriterium objects
     * @param qc1 the first QueryCriterium
     * @param qc2 the second QueryCriterium
     * @roseuid 3973DFDF0086
     */
    public Query(QueryCriterium qc1, QueryCriterium qc2)
    {
        // add the qcs
        addQueryCriterium(qc1);
        addQueryCriterium(qc2);
    }

    //--------------------------------------------------------------------------
    /**
     * Create a query with a QueryCriterium object and a Sort object
     * @param qc
     * @param sort
     * @roseuid 3973DFF900B5
     */
    public Query(QueryCriterium qc, Sort sort)
    {
        // add the qc
        addQueryCriterium(qc);

        // add the sort
        addSort(sort);
    }

    //--------------------------------------------------------------------------
    /**
     * @return true if all the criteria are complete.
     */
    public boolean isComplete()
    {
        for (int i = 0; _criteria != null && i < _criteria.size(); i++)
            if (!((QueryCriterium) _criteria.get(i)).isComplete())
                return false;
        return true;
    }

	/**
	 *  Keyword substitution
	 */
	public void
	setUseridKeyword (
	 String userid)
	{
		Vector v = getCriteria ();
		for (int i = 0; v != null && i < v.size (); i++)
		{
			QueryCriterium qc = (QueryCriterium) v.elementAt (i);
			if (qc._value != null &&
				qc._value.toString ().equals (QueryCriterium.USERID_VALUE))
			{
				qc._value = userid;
			}
		}
 	}

	/**
	 *  Evaluate functions
	 */
	public void evaluateFunctions()
	{
		Vector v = getCriteria ();
		for (int i = 0; v != null && i < v.size (); i++)
		{
			QueryCriterium qc = (QueryCriterium) v.elementAt (i);
            if(qc.isFunction())
                qc.evaluateFunction();
		}
 	}
}