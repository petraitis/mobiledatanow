//Source file: D:\\dev\\wsl\\mdn\\dataview\\DataView.java

package wsl.mdn.dataview;

// imports
import wsl.fw.util.Util;
import wsl.fw.datasource.*;

import java.util.List;
import java.util.Vector;
import java.util.Hashtable;
import java.io.IOException;
import java.io.ObjectInputStream;
import wsl.fw.security.Group;
import wsl.fw.util.Log;
import wsl.mdn.common.MdnDataManager;
import wsl.mdn.guiconfig.MenuAction;

//------------------------------------------------------------------------------
/**
 * Provides a 'flat' view over multiple fields and entities of a DataSource
 */
public class DataView extends DataObject implements Entity
{
    //--------------------------------------------------------------------------
    // constants

    public final static String ENT_DATAVIEW         = "TBL_DATAVIEW";
    public final static String FLD_ID               = "FLD_ID";
    public final static String FLD_NAME             = "FLD_NAME";
    public final static String FLD_SOURCE_DSID      = "FLD_SOURCE_DSID";
    public final static String FLD_DESCRIPTION      = "FLD_DESCRIPTION";
    public final static String FLD_CLASS            = JdbcDataSource.CLASS_COLUMN_NAME;
    public final static String FLD_DEL_STATUS       = "FLD_DEL_STATUS";
    
    //--------------------------------------------------------------------------
    // attributes

    protected transient DataSourceDobj _sourceDs       = null;
    protected transient Vector<DataViewField>         _fields         = new Vector<DataViewField>();
    private   transient boolean        _fieldsLoaded   = false;
    private   transient Vector         _imageFields    = null;
    private   transient Vector         _deleteFields   = new Vector();
    private   transient Vector         _queries        = new Vector();
    private   transient boolean        _queriesLoaded  = false;
    private   transient Vector         _groups         = null;
    private   transient Vector         _groupDataViews = null;

    //--------------------------------------------------------------------------
    // construction

    /**
     * Blank ctor
     */
    public DataView()
    {
    }


    //--------------------------------------------------------------------------
    // persistence

    /**
     * Static factory method to create the entity to be used by this dataobject
     * and any subclasses. This is called by the DataManager's factory when
     * creating a DATAVIEW entity.
     * @return the created entity.
     */
    public static Entity createEntity()
    {
        // create the entity
        Entity ent = new EntityImpl(ENT_DATAVIEW, DataView.class);

        // add the key generator for the system id
        ent.addKeyGeneratorData(new DefaultKeyGeneratorData(ENT_DATAVIEW, FLD_ID));

        // create the fields and add them to the entity
        ent.addField(new FieldImpl(FLD_ID, Field.FT_INTEGER, Field.FF_UNIQUE_KEY | Field.FF_SYSTEM_KEY));
        ent.addField(new FieldImpl(FLD_NAME, Field.FT_STRING, Field.FF_NAMING));
        ent.addField(new FieldImpl(FLD_SOURCE_DSID, Field.FT_INTEGER));
        ent.addField(new FieldImpl(FLD_DESCRIPTION, Field.FT_STRING));
        ent.addField(new FieldImpl(FLD_CLASS, Field.FT_STRING)); // polymorph support
        ent.addField (new FieldImpl (FLD_DEL_STATUS, Field.FT_INTEGER));

        // return the entity
        return ent;
    }

    //--------------------------------------------------------------------------
    /**
     * @return String the name of the entity that defines this DataObject
     */
    public String getEntityName()
    {
        return ENT_DATAVIEW;
    }


    //--------------------------------------------------------------------------
    // accessors Note the get/setName() are in entity interface section

    /**
     * @return int the id
     */
    public int getId()
    {
        return getIntValue(FLD_ID);
    }

    /**
     * Set the id for this dataview.
     * @param id, the id to set
     */
    public void setId(Object id)
    {
        setValue(FLD_ID, id);
    }

    /**
     * @return int the source datasource id
     */
    public int getSourceDsId()
    {
        return getIntValue(FLD_SOURCE_DSID);
    }

    /**
     * Set the source datasource id
     * @param id
     */
    public void setSourceDsId(int id)
    {
        setValue(FLD_SOURCE_DSID, id);
    }

    /**
     * Returns the description of the dataview
     * @return String
     */
    public String getDescription()
    {
        return getStringValue(FLD_DESCRIPTION);
    }

    /**
     * Sets the entity description into the dataview
     * @param name
     * @return void
     */
    public void setDescription(String name)
    {
        setValue(FLD_DESCRIPTION, name);
    }

	public int getDelStatus() {
		return getIntValue(FLD_DEL_STATUS);
	}

	public void setDelStatus(int delStatus) {
		setValue(FLD_DEL_STATUS, delStatus);
	}	    
    
    /**
     * @return DataSourceDobj the source ds
     */
    public DataSourceDobj getSourceDataSource() throws DataSourceException
    {
        // if null, select
        if(_sourceDs == null)
        {
            // get the dsid
            int dsid = this.getSourceDsId();
            if(dsid >= 0)
            {
                // create query
                DataSource sysDs = DataManager.getSystemDS();
                Query q = new Query(new QueryCriterium(DataSourceDobj.ENT_DATASOURCE,
                    DataSourceDobj.FLD_ID, QueryCriterium.OP_EQUALS, new Integer(dsid)));
                RecordSet rs = sysDs.select(q);

                // get the ds
                if(rs != null && rs.next())
                    _sourceDs = (DataSourceDobj)rs.getCurrentObject();
            }
        }

        // return
        return _sourceDs;
    }

    /**
     * Set the source datasource
     * @param sourceDs
     */
    public void setSourceDataSource(DataSourceDobj sourceDs)
    {
        _sourceDs = sourceDs;
    }


    //--------------------------------------------------------------------------
    // remove field

    /**
     * Remove a field
     */
    public DataViewField removeField(DataViewField f, boolean doDelete)
    {
        // if it is indb, remove by key
        DataViewField rem = null;
        if(f.getState() == DataObject.IN_DB)
        {
            // iterate
            DataViewField temp;
            for(int i = 0; i < getFields().size(); i++)
            {
                temp = (DataViewField)getFields().elementAt(i);
                if(temp != null && temp.getState() == DataObject.IN_DB &&
                    temp.getId() == f.getId())
                    rem = (DataViewField)getFields().remove(i);
            }
        }

        // else, try to remove by ref
        else
        {
            if(getFields().remove(f))
                rem = f;
        }

        try
        {
            // delete
            if(doDelete && rem.getState() == DataObject.IN_DB)
            {
                // imaging?
                if(isFieldImaging())
                    _deleteFields.add(rem);
                else
                    rem.delete();
            }
        }
        catch(Exception e)
        {
            throw new RuntimeException(e.toString());
        }

        // return
        return rem;
    }


    //--------------------------------------------------------------------------
    // cascading save

    /**
     * Called post insert / update call on DataSource
     */
    protected void postSave() throws DataSourceException
    {
        // process imaging, delete the delete fields
        if(isFieldImaging())
            processFieldImage();

        // save all fields
        DataViewField f;
        for(int i = 0; _fieldsLoaded && i < getFields().size(); i++)
        {
            f = (DataViewField)getFields().elementAt(i);
            if(f != null)
            {
                // if new set keys
                if(f.getState() == DataObject.NEW)
                    f.setDataViewId(this.getId());

                // save
                f.save();
            }
        }

        // save all queries
        QueryDobj q;
        for(int i = 0; _queriesLoaded && i < getQueries().size(); i++)
        {
            q = (QueryDobj)getQueries().elementAt(i);
            if(q != null)
            {
                // if new set keys
                if(q.getState() == DataObject.NEW)
                    q.setViewOrTableId(this.getId());

                // save
                q.save();
            }
        }
    }


    //--------------------------------------------------------------------------
    // cascading delete

    /**
     * Called pre delete call on DataSource
     */
    protected void preDelete() throws DataSourceException
    {
        // delete all fields
        DataViewField f;
        for(int i = 0; i < getFields().size(); i++)
        {
            f = (DataViewField)getFields().elementAt(i);
            if(f != null && f.getState() == DataObject.IN_DB)
                f.delete();
        }
        getFields().clear();

        // delete all QueryDobjs
        QueryDobj qd;
        for(int i = 0; i < getQueries().size(); i++)
        {
            qd = (QueryDobj) getQueries().elementAt(i);
            if(qd != null && qd.getState() == DataObject.IN_DB)
                qd.delete();
        }
        getQueries().clear();

        // delete all menu actions (NewRecord actions) that reference this
        DataSource ds = DataManager.getSystemDS();
        Query query = new Query(new QueryCriterium(MenuAction.ENT_MENUACTION,
            MenuAction.FLD_DATAVIEWID, QueryCriterium.OP_EQUALS,
            new Integer(getId())));
        RecordSet rs = ds.select(query);
        while (rs.next())
        {
            DataObject childMenuAction = rs.getCurrentObject();
            if (childMenuAction != null)
                childMenuAction.delete();
        }
    }


    //--------------------------------------------------------------------------
    // field imaging

    /**
     * Start an imaging session
     */
    public void imageFields()
    {
        // close fields
        _imageFields = (Vector)getFields().clone();
    }

    /**
     * Reverts fields to the image
     */
    public void revertToImage()
    {
        // must be imaging
        if(isFieldImaging())
        {
            // set fields vector back to image
            _fields = _imageFields;

            // revert fields values to image
            DataViewField dvf;
            for(int i = 0; _fields != null && i < _fields.size(); i++)
            {
                dvf = (DataViewField)_fields.elementAt(i);
                if(dvf != null)
                    dvf.revertValuesToImage();
            }

            // clear the image and delete fields
            clearFieldImage();

            // null out the sourceDs in order to cause a reload of the datasource from dsID
            _sourceDs = null;

            // revert values back to image
            this.revertValuesToImage();
        }
    }

    /**
     * End the imaging session and delete image deletes
     */
    public void processFieldImage() throws DataSourceException
    {
        // iterate deletes
        DataViewField dvf;
        for(int i = 0; i < _deleteFields.size(); i++)
        {
            dvf = (DataViewField)_deleteFields.elementAt(i);
            if(dvf != null && dvf.getState() == DataObject.IN_DB)
                dvf.delete();
        }

        // clear image
        clearFieldImage();
    }

    /**
     * Clear the image
     */
    public void clearFieldImage()
    {
        // clear the image and delete fields
        _imageFields = null;
        _deleteFields.clear();
    }

    /**
     * @return boolean true if field imaging
     */
    public boolean isFieldImaging()
    {
        return _imageFields != null;
    }

    //==========================================================================
    // Entity interface

    /**
     * Returns the name of the entity
     * @return String
     */
    public String getName()
    {
        return getStringValue(FLD_NAME);
    }

    /**
     * Sets the entity name into the Entity
     * @param name
     * @return void
     */
    public void setName(String name)
    {
        setValue(FLD_NAME, name);
    }

    /**
     * Return the default class
     * @return Class the default class
     */
    public Class getDefaultClass()
    {
        return null;
    }

    /**
     * Set the default class name
     * @param defaultClass the default class
     * @return void
     */
    public void setDefaultClass(Class defaultClass)
    {
    }

    /**
     * Returns the entity flags
     * @return int
     */
    public int getFlags()
    {
        return 0;
    }

    /**
     * Set the entity flags
     * @param flags the value to set
     */
    public void setFlags(int val)
    {
    }

    /**
     * @return DataSource the parent DataSource of this Entity
     */
    public DataSource getParentDataSource()
    {
        return MdnDataManager.getDataViewDS();
    }

    /**
     * Set the parent DataSource of this Entity
     * @param parentDs the parent DataSource
     */
    public void setParentDataSource(DataSource parentDs)
    {
    }


    //--------------------------------------------------------------------------
    // fields

    /**
     * Add a single Field to the Entity
     * @param f the field to add
     */
    public void addField(Field f)
    {
        // validate
        Util.argCheckNull(f);

        // add the entity
        _fields.add((DataViewField)f);

        // set the loaded flag
        _fieldsLoaded = true;        
    }
    /**
     * Sets fields into the Entity
     * @param fields Vector of fields to set
     * @return void
     */
    public void setFields(Vector fields)
    {
        _fields = fields;
    }

    /**
     * Returns the fields of the entity
     * @return Hashtable
     */
    public Vector<DataViewField> getFields()
    {
        // if not loaded, load
        if(this.getState() == DataObject.IN_DB && !_fieldsLoaded)
        {
            // get the ds
            DataSource sysDs = DataManager.getSystemDS();

            // create the query
            Query q = new Query(new QueryCriterium(DataViewField.ENT_DVFIELD,
                DataViewField.FLD_DATAVIEWID, QueryCriterium.OP_EQUALS, new Integer(this.getId())));

            try
            {
                // execute the query
                RecordSet rs = sysDs.select(q);

                // iterate and add fields
                _fields.clear();
                DataViewField field;
                while(rs.next())
                {
                    field = (DataViewField)rs.getCurrentObject();
                    if(field != null)
                        _fields.add(field);
                }

                // set loaded flag
                _fieldsLoaded = true;
            }
            catch(Exception e)
            {
                throw new RuntimeException(e.toString());
            }
        }

        // return fields
        return _fields;
    }
    
    public List<String> getSourceTables(){
    	_fields = getFields();
    	List<String> tableNames = new Vector<String>();
    	
    	for (DataViewField field : (List<DataViewField>)_fields){
    		String tableName = field.getSourceEntity();
    		if (!tableNames.contains(tableName)){
    			tableNames.add(tableName);
    		}
    	}
    	
    	return tableNames;
    }

    public String getSourceTableNames(){
    	List<String> tableNames = getSourceTables();
    	String tableNamesStr = "";
    	
    	for (String table : tableNames){
    		//tableNamesStr += "[" + table + "],";
    		tableNamesStr += "" + table + ",";
    	}
    	if (tableNamesStr.length() > 1)
    		tableNamesStr = tableNamesStr.substring(0, tableNamesStr.length() -1);
    	
    	return tableNamesStr;
    }
    
    /**
     * Returns a the field mapped to the param name
     * @param fieldName the name of the field
     * @return Field
     */
    public DataViewField getField(String fieldName)
    {
        // validate
        Util.argCheckEmpty(fieldName);

        // iterate fields
        DataViewField f;
        for(int i = 0; getFields() != null && i < getFields().size(); i++)
        {
            // get the field and compare
            f = (DataViewField)getFields().elementAt(i);
            if(f != null && f.getName().equalsIgnoreCase(fieldName))
                return f;
        }

        // not found
        throw new RuntimeException("Field not found: " + fieldName);
    }

    /**
     * Returns a the field mapped to the param name
     * @param fieldName the name of the field
     * @return Field
     */
    public DataViewField getField(int id)
    {
        // iterate fields
        DataViewField f;
        for(int i = 0; getFields() != null && i < getFields().size(); i++)
        {
            // get the field and compare
            f = (DataViewField)getFields().elementAt(i);
            if(f != null && f.getId() == id)
                return f;
        }

        // not found
        throw new RuntimeException("Field not found: " + id);
    }

    /**
     * returns a vector of fields that have the FF_UNIQUE_KEY flag set
     * @return Vector the vector of key Fields
     */
    public Vector getUniqueKeyFields()
    {
        // iterate the fields
        Vector v = new Vector();
        DataViewField f;
        for(int i = 0; getFields() != null && i < getFields().size(); i++)
        {
            // get the field
            f = (DataViewField)getFields().elementAt(i);

            // if flag set, add to vector
            if(f != null && f.hasFlag(Field.FF_UNIQUE_KEY))
                v.add(f);
        }

        // return the vector
        return v;
    }

    /**
     * @return Field the first field with FF_SYSTEM_KEY flag set
     */
    public Field getSystemKeyField()
    {
        // return the field
        return null;
    }

    /**
     * returns a vector of fields that have the FF_NAMING flag set
     * @return Vector the vector of naming Fields
     */
    public Vector getNamingFields()
    {
        // iterate the fields
        Vector v = new Vector();
        DataViewField f;
        for(int i = 0; getFields() != null && i < getFields().size(); i++)
        {
            // get the field
            f = (DataViewField)getFields().elementAt(i);

            // if flag set, add to vector
            if(f != null && f.hasFlag(Field.FF_NAMING))
                v.add(f);
        }

        // return the vector
        return v;
    }

    /**
     * @param f field to verify is generated or not
     * @return boolean the if the param field is generated
     */
    public boolean isFieldGenerated(Field f)
    {
        return false;
    }

    /**
     * Get the numeric index of a field from its name
     * @param name the name of the field
     * @return int the index of the field (zero-based)
     */
    public int getFieldIndex(String name)
    {
        return -1;
    }


    //--------------------------------------------------------------------------
    // joins

    /**
     * Add a single Join to the Entity
     * @param j the Join to add
     */
    public void addJoin(Join j)
    {
    }

    /**
     * Sets joins into the Entity
     * @param joins Vector of joins to set
     * @return void
     */
    public void setJoins(Vector joins)
    {
    }

    /**
     * Return the vector of parent joins
     * @return Vector the vector of Join objects
     */
    public Vector getJoins()
    {
        return null;
    }


    //--------------------------------------------------------------------------
    // other operations

    /**
     * Return true if the param flag is set
     * @param flag the flag to check for
     * @return boolean true if the param flag is set
     */
    public boolean hasFlag(int flag)
    {
        return false;
    }

    /**
     * Verifies the structure and flags on the fields and entity
     * @throws RuntimeException if invalid setup of fields or entity
     */
    public void verifyEntityDefinition()
    {
    }

    /**
     * Adds a KeyGeneratorData object to the Entity
     * @param kgd the KeyGeneratorData object to add
     * @return void
     */
    public void addKeyGeneratorData(KeyGeneratorData kgd)
    {
    }

    /**
     * Returns the Vector of KeyGeneratorData objects
     * @return Vector the KeyGeneratorData objects
     */
    public Vector getKeyGeneratorData()
    {
        return null;
    }


    //--------------------------------------------------------------------------
    // Queries

    /**
     * Add a single Query to the DataView
     * @param q the query to add
     */
    public void addQuery(QueryDobj q)
    {
        // validate
        Util.argCheckNull(q);

        // add the entity
        _queries.add(q);

        // set the QueryDobj data view
        q.setDataView(this);

        // set the loaded flag
        _queriesLoaded = true;
    }

    /**
     * Returns the queries of the DataView
     * @return Vector
     */
    public Vector getQueries()
    {
        // if not loaded, load
        if(!_queriesLoaded)
        {
            // must be indb
            if(this.getState() == DataObject.IN_DB)
            {
                // build query
                DataSource ds = DataManager.getSystemDS();
                Query q = new Query(QueryDobj.ENT_QUERY);
                q.addQueryCriterium(new QueryCriterium(QueryDobj.ENT_QUERY,
                    QueryDobj.FLD_PARENTID, QueryCriterium.OP_EQUALS,
                    new Integer(this.getId())));

                try
                {
                    // execute query and build vector
                    QueryDobj qdobj;
                    RecordSet rs = ds.select(q);
                    while(rs != null && rs.next())
                    {
                        // get the field
                        qdobj = (QueryDobj)rs.getCurrentObject();
                        if(qdobj != null)
                            addQuery(qdobj);
                    }
                }
                catch(Exception e)
                {
                    throw new RuntimeException(e.toString());
                }
            }

            // set flag
            _queriesLoaded = true;
        }

        // return
        return _queries;
    }

    /**
     * Returns a the query mapped to the param name
     * @param name the name of the query
     * @return QueryDobj
     */
    public QueryDobj getQuery(String name)
    {
        // iterate the entities
        QueryDobj q = null;
        for(int i = 0; i < getQueries().size(); i++)
        {
            // get the entity and compare
            q = (QueryDobj)getQueries().elementAt(i);
            if(q != null && q.getName().equalsIgnoreCase(name))
                return q;
        }

        // not found
        throw new RuntimeException("Query not found: " + name);
    }

    /**
     * Remove a query
     */
    public QueryDobj removeQuery(QueryDobj q, boolean doDelete)
    {
        // if it is indb, remove by key
        QueryDobj rem = null;
        if(q.getState() == DataObject.IN_DB)
        {
            // iterate
            QueryDobj temp;
            for(int i = 0; i < getQueries().size(); i++)
            {
                temp = (QueryDobj)getQueries().elementAt(i);
                if(temp != null && temp.getState() == DataObject.IN_DB &&
                    temp.getId() == q.getId())
                    rem = (QueryDobj)getQueries().remove(i);
            }
        }

        // else, try to remove by ref
        else
        {
            if(getQueries().remove(q))
                rem = q;
        }

        try
        {
            // delete
            if(doDelete && rem.getState() == DataObject.IN_DB)
                rem.delete();
        }
        catch(Exception e)
        {
            throw new RuntimeException(e.toString());
        }

        // return
        return rem;
    }

    /**
     * @return boolean true if the param queryid belongs to this dv
     */
    public boolean isValidQuery(int queryId)
    {
        // iterate queries
        QueryDobj q;
        for(int i = 0; getQueries() != null && i < getQueries().size(); i++)
        {
            // get the query
            q = (QueryDobj)getQueries().elementAt(i);
            if(q != null && q.getId() == queryId)
                return true;
        }

        // not found
        return false;
    }


    //--------------------------------------------------------------------------
    // groups

    /**
     * @return boolean true if the groupid is enabled for this DataView
     * @param groupId the group to validate
     */
    public boolean isGroupEnabled(int groupId) throws DataSourceException
    {
        // iterate groups
        Group g;
        for(int i = 0; i < getGroups().size(); i++)
        {
            // get the group, return true if same as param
            g = (Group)getGroups().elementAt(i);
            if(g != null && g.getIntValue(Group.FLD_ID) == groupId)
                return true;
        }

        // not found, not enabled
        return false;
    }

    /**
     * @return Vector the Groups that have access to this view
     */
    public Vector getGroups() throws DataSourceException
    {
        // if null, load
        if(_groups == null && this.getState() == DataObject.IN_DB)
        {
            // create the vector
            _groups = new Vector();
            _groupDataViews = new Vector();

            // build query
            Query q = new Query(GroupDataView.ENT_GROUPDATAVIEW);
            q.addQueryEntity(Group.ENT_GROUP);
            q.addQueryCriterium(new QueryCriterium(GroupDataView.ENT_GROUPDATAVIEW,
                GroupDataView.FLD_DATAVIEWID, QueryCriterium.OP_EQUALS,
                new Integer(this.getId())));

            // execute and build vector
            RecordSet rs = DataManager.getSystemDS().select(q);
            while(rs != null && rs.next())
            {
                // add group to vector
                _groups.add(rs.getCurrentObject(Group.ENT_GROUP));
                _groupDataViews.add(rs.getCurrentObject(GroupDataView.ENT_GROUPDATAVIEW));
            }
        }

        // return
        return _groups;
    }

    /**
     * @param groupId
     * @return GroupDataView the GroupDataView for a group id
     */
    public GroupDataView getGroupDataView(int groupId) throws DataSourceException
    {
        // call getGroups() to load
        getGroups();

        // iterate gdvs to find one with matching groupId
        GroupDataView gdv;
        for(int i = 0; _groupDataViews != null && i < _groupDataViews.size(); i++)
        {
            // get the gdv
            gdv = (GroupDataView)_groupDataViews.elementAt(i);
            if(gdv != null && gdv.getGroupId() == groupId)
                return gdv;
        }

        // not found
        return null;
    }


    //--------------------------------------------------------------------------
    // DataViewFields

    /**
     * @param sourceEntity
     * @param sourceField
     * @return DataViewField the dvf with the param sourceEntity and sourceField
     */
    public DataViewField getDataViewField(String sourceEntity, String sourceField)
    {
        // validate
        Util.argCheckEmpty(sourceEntity);
        Util.argCheckEmpty(sourceField);

        // iterate fields
        DataViewField f;
        Vector fields = getFields();
        for(int i = 0; fields != null && i < fields.size(); i++)
        {
            // get the field
            f = (DataViewField)fields.elementAt(i);

            // compare and return if the ONE
            if(f != null && f.getSourceEntity().equals(sourceEntity) &&
                f.getSourceField().equals(sourceField))
                return f;
        }

        // not found
        return null;
    }

    //--------------------------------------------------------------------------
    /**
     * Serialization support.
     */
    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException
    {
        _sourceDs       = null;
        _fields         = new Vector();
        _fieldsLoaded   = false;
        _imageFields    = null;
        _deleteFields   = new Vector();
        _queries        = new Vector();
        _queriesLoaded  = false;
        _groups         = null;
        _groupDataViews = null;

        // do default loading
        in.defaultReadObject();
    }

    //--------------------------------------------------------------------------
    /**
     * Note that this is a cached function.
     * @return a vector (possibly empty) of DataViewFields that are read only.
     */
    public Vector getReadOnlyFields()
    {
        Vector rof = new Vector();

        // get all fields
        Vector fields = getFields();

        try
        {
            // get the data source dobj, use cache
            DataSourceDobj dsd = MdnDataCache.getCache().getDataSourceDobj(new Integer(getSourceDsId()));

            // iterate fields
            for (int i = 0; i < fields.size(); i++)
            {
                // get data view field
                DataViewField f = (DataViewField) fields.get(i);

                // get entity dobj
                EntityDobj entDobj = dsd.getEntity(f.getSourceEntity());

                // if found get field dobj
                if (entDobj != null)
                {
                    FieldDobj fldDobj = entDobj.getField(f.getSourceField());

                    // check for read only flag
                    if (fldDobj != null && fldDobj.hasFlag(Field.FF_READ_ONLY))
                        rof.add(f);
                }
            }
        }
        catch (DataSourceException e)
        {
            Log.error("DataView.getReadOnlyFields: ", e);
        }

        return rof;
    }

    //--------------------------------------------------------------------------
    /**
     * Check if a data view field is read only.
     * @param f, the field to check.
     * @return true if the filed is read only.
     */
    public boolean isFieldReadOnly(DataViewField f)
    {
        // delegate
        return isFieldReadOnly(f, getReadOnlyFields());
    }

    //--------------------------------------------------------------------------
    /**
     * Check if a data view field is read only.
     * @param f, the field to check.
     * @param rof, a vector containing the read only DataViewFields.
     * @return true if the filed is read only.
     */
    public static boolean isFieldReadOnly(DataViewField f, Vector rof)
    {
        if (rof != null && f != null)
            for (int i = 0; i < rof.size(); i++)
                if (f.getId() == ((DataViewField) rof.get(i)).getId())
                    return true;
        return false;
    }

    //--------------------------------------------------------------------------
    /**
     * @return true if queries are already loaded.
     */
    public boolean areQueriesLoaded()
    {
        return _queriesLoaded;
    }
}

//------------------------------------------------------------------------------
//------------------------------------------------------------------------------

