//==============================================================================
// DataObject.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.datasource;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Date;
import java.util.Collections;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import wsl.fw.util.Type;
import wsl.fw.util.Log;
import wsl.fw.util.Util;
import wsl.fw.util.TypeConversionException;
import wsl.fw.resource.ResId;

//------------------------------------------------------------------------------
/**
 * An abstract superclass for all objects that may be selected from or
 * serialised to a Data Source.
 */
public abstract class DataObject implements Serializable
{
    // version tag
    private final static String _ident = "$Date: 2004/01/06 01:41:03 $  $Revision: 1.2 $ "
        + "$Archive: /Framework/Source/wsl/fw/datasource/DataObject.java $ ";

    // resources
    public static final ResId ERR_INVALID_STATE  = new ResId("DataObject.error.InvalidState");
    public static final ResId ERR_BAD_STATE  = new ResId("DataObject.error.BadState");
    public static final ResId WARNING_MORE_THEN_ONE  = new ResId("DataObject.warning.MoreThenOne");
    public static final ResId ERR_INVALID_STATE2  = new ResId("DataObject.error.InvalidState2");
    public static final ResId ERR_NO_DATA_OBJECT  = new ResId("DataObject.error.NoDataObject");
    public static final ResId ERR_NOT_REUSE  = new ResId("DataObject.error.NotReuse");

    // constants
    public static final int NEW = 0;
    public static final int IN_DB = 1;
    public static final int DELETED = 2;
    public static final int MARKED_FOR_DELETION = 3;

    // attributes
    // datasource and entity are transient, when deserialized readObject
    // calls init() to reestablish them in the local context.
    private transient Entity     _entity = null;

    private Hashtable _values       = new Hashtable();
    private Hashtable _image        = null;
    private String    _preUpdateKey = null;
    private int       _state        = NEW;
    protected boolean _initDone     = false;
    protected boolean _doKeyCheck   = true;

    //--------------------------------------------------------------------------
    /**
     * Blank constructor. Calls init().
     */
    public DataObject()
    {
        // init
        init();
    }

    //--------------------------------------------------------------------------
    /**
     * Initialises the DataObject. Sets the DataSource and Entity definition.
     * Should not be overidden except in special cases like Row.
     * @return void.
     */
    protected void init()
    {
        // get the DataSource for this DataObject
        DataSource ds = DataManager.getDataSource(getEntityName());

        // get and set the Entity
        Entity ent = ds.getEntity(getEntityName());
        setEntity(ent);

        // set inited flag
        _initDone = true;
    }

    //--------------------------------------------------------------------------
    /**
     * Init for when we have an entity, used by serialization.
     */
    protected void init(Entity entity)
    {
        // set entity and inited flag
        setEntity(entity);
        _initDone = true;
    }

    //--------------------------------------------------------------------------
    /**
     * Serialization support (write).
     * Required because we do not serialize DataSources.
     */
    private void writeObject(ObjectOutputStream outStream)
        throws IOException
    {
        // get info defining the data source and entity for use in reconnection
        // after RMI/serialization

        Object dsId   = null;
        String entId  = null;
        Entity entity = null;

        if (_entity != null)
        {
            if (_entity.getParentDataSource() != null)
            {
                // if we have an entity and a datasource set the ds and ent ids
                dsId  = _entity.getParentDataSource().getDsId();
                entId = _entity.getName();
            }
            else
            {
                // entity but no ds so reattaching is no possible so send
                // entire entity
                entity = _entity;
            }
        }

        // write to stream
        outStream.writeObject(dsId);
        outStream.writeObject(entId);
        outStream.writeObject(entity);

        // now save the rest of the object normally
        outStream.defaultWriteObject();
    }

    //--------------------------------------------------------------------------
    /**
     * Override object deserialization to init the transient data.
     */
    private void readObject(ObjectInputStream inStream)
        throws IOException, ClassNotFoundException
    {
        // read ds and entity data
        // fixme

        Object dsId        = inStream.readObject();
        String entId       = (String) inStream.readObject();
        Entity entity      = (Entity) inStream.readObject();
        Entity localEntity = null;

        // use the data to reconnect to the local ds and entity
        if (dsId != null && entId != null)
        {
            // have ds and ent info
            DataSource ds = null;

            if (dsId instanceof DataSourceParam)
                ds = DataManager.getDataSource((DataSourceParam) dsId);
            else if (dsId != null && dsId instanceof String)
                ds = DataManager.getDataSource((String) dsId);

            // have ds, get the specified local entity from it
            if (ds != null)
                localEntity = ds.getEntity(entId);

        }
        else if (entity != null)
        {
            //no local data, use complete serialized entity
            localEntity = entity;
        }

        // call init, to set the flags and entity to the local one
        init(localEntity);

        // call default serialization to load rest of data
        inStream.defaultReadObject();
    }

    //--------------------------------------------------------------------------
    /**
     * Constructor taking boolean to allow no init.
     * @param doInit if true then init() is called.
     */
    public DataObject(boolean doInit)
    {
        // init
        if(doInit)
            init();
    }

    //--------------------------------------------------------------------------

    /**
     * Get the DataSource used by this object.
     * @return the DataSource.
     * @roseuid 3973CB020035
     */
    public DataSource getDataSource()
    {
        // optional lazy init, if the no init constructor was used then call
        // init if needed
        if (_entity == null && !_initDone)
            init();

        return _entity.getParentDataSource();
    }

    //--------------------------------------------------------------------------
    /**
     * Set an Entity definition into the data object. This also inits it.
     * @param entity the entity to set.
     * @roseuid 3973C3A703A9
     */
    public void setEntity(Entity entity)
    {
        _entity   = entity;
        _initDone = true;
    }

    //--------------------------------------------------------------------------
    /**
     * Return the Entity that defines this DataObject.
     * @return Entity the Entity defining this DataObject.
     */
    public Entity getEntity()
    {
        // optional lazy init, if the no init constructor was used then call
        // init if needed
        if (_entity == null && !_initDone)
            init();

        return _entity;
    }

    //--------------------------------------------------------------------------
    /**
     * Return the Entity that defines this DataObject. Internal version that
     * does not try to perform the lazy init.
     * @return Entity the Entity defining this DataObject.
     */
    protected Entity iGetEntity()
    {
        return _entity;
    }
    //--------------------------------------------------------------------------
    /**
     * @return true if this DataObject heas been inited and bound to its entity
     */
    public boolean initDone()
    {
        return _initDone;
    }

    //--------------------------------------------------------------------------
    /**
     * Return the name of the entity that this DataObject is defined by.
     * @return String name of the Entity.
     */
    public abstract String getEntityName();

    //--------------------------------------------------------------------------
    /**
     * Set a value mapped to a fieldname into the data object.
     * @param fieldName the field name that the value is mapped to.
     * @param value the value to be set, may be null.
     * @roseuid 3973C9830392.
     */
    public void setValue(String fieldName, Object value)
    {
        // if the value is null remove it from the hashtable else
        // map the value to the fieldname in the hashtable
        if (value == null)
            _values.remove(fieldName);
        else
            _values.put(fieldName, value);
    }

    //--------------------------------------------------------------------------
    /**
     * Set a value mapped to a fieldname into the data object.
     * @param fieldName the field name that the value is mapped to.
     * @param value the value to be set.
     * @roseuid 3973C9CF0297.
     */
    public void setValue(String fieldName, int value)
    {
        // delegate
        setValue(fieldName, new Integer(value));
    }

    //--------------------------------------------------------------------------
    /**
     * Set a value mapped to a fieldname into the data object.
     * @param fieldName the field name that the value is mapped to.
     * @param value the value to be set.
     * @roseuid 3973C9D00343.
     */
    public void setValue(String fieldName, double value)
    {
        // delegate
        setValue(fieldName, new Double(value));
    }

    //--------------------------------------------------------------------------
    /**
     * Set a value mapped to a fieldname into the data object.
     * @param fieldName the field name that the value is mapped to.
     * @param value the value to be set.
     */
    public void setValue(String fieldName, boolean value)
    {
        // delegate
        setValue(fieldName, new Boolean(value));
    }

    //--------------------------------------------------------------------------
    /**
     * Return the value mapped to a field name as an Object.
     * @param fieldName the field name to which the value is mapped.
     * @return Object the mapped value.
     * @roseuid 3973CA61005C
     */
    public Object getObjectValue(String fieldName)
    {
        return _values.get(fieldName);
    }

    //--------------------------------------------------------------------------
    /**
     * Return the value mapped to a field name as an String.
     * @param fieldName the field name to which the value is mapped.
     * @return String the mapped value.
     * @roseuid 3973CA7D00A2.
     */
    public String getStringValue(String fieldName)
    {
        // get the object value
        Object obj = getObjectValue(fieldName);

        // convert and return
        return Type.objectToString(obj);
    }

    //--------------------------------------------------------------------------
    /**
     * Return the value mapped to a field name as an Object.
     * @param fieldName the field name to which the value is mapped.
     * @return Object the mapped value.
     * @roseuid 3973CA800061
     */
    public int getIntValue(String fieldName)
    {
        // get the object value
        Object obj = getObjectValue(fieldName);

        // convert and return
        try
        {
            return Type.objectToInt(obj);
        }
        catch (TypeConversionException e)
        {
            // fixme
            throw new RuntimeException(e.toString());
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Return the value mapped to a field name as an Object.
     * @param fieldName the field name to which the value is mapped.
     * @return Object the mapped value.
     * @roseuid 3973CA83003D.
     */
    public double getDoubleValue(String fieldName)
    {
        // get the object value
        Object obj = getObjectValue(fieldName);

        // convert and return
        try
        {
            return Type.objectToDouble(obj);
        }
        catch (TypeConversionException e)
        {
            // fixme
            throw new RuntimeException(e.toString());
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Return the value mapped to a field name as a boolean.
     * @param fieldName the field name to which the value is mapped.
     * @return boolean the mapped value.
     */
    public boolean getBooleanValue(String fieldName)
    {
        // get the object value
        Object obj = getObjectValue(fieldName);

        // convert and return
        return Type.objectToBoolean(obj);
    }

    //--------------------------------------------------------------------------
    /**.
     * Return the value mapped to a field name as an Object
     * @param fieldName the field name to which the value is mapped.
     * @return Object the mapped value.
     * @roseuid 3973CA8700A7
     */
    public java.util.Date getDateValue(String fieldName)
    {
        // get the object value
        Object obj = getObjectValue(fieldName);

        // convert and return
        try
        {
            return Type.objectToDate(obj);
        }
        catch (TypeConversionException e)
        {
            // fixme
            throw new RuntimeException(e.toString());
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Returns the image value of a field.
     * @param fieldName the field to get the iamge value of.
     * @return Object the value in the image table.
     */
    public Object getImageValue(String fieldName)
    {
        return (_image == null)? null: _image.get(fieldName);
    }

    //--------------------------------------------------------------------------
    /**
     * Return the state of the DataObject.
     * @return int the state constant, DataObject.NEW, DataObject.IN_DB,
     * DataObject.DELETED or DataObject.MARKED_FOR_DELETION.
     */
    public int getState()
    {
        return _state;
    }

    //--------------------------------------------------------------------------
    /**
     * Set the state of the DataObject.
     * @param state the new state (DataObject.NEW, DataObject.IN_DB,
     *   DataObject.DELETED or DataObject.MARKED_FOR_DELETION).
     */
    public void setState(int state)
    {
        _state = state;
    }

    //--------------------------------------------------------------------------
    /**
     * Save the DataObject into its DataSource. Delegates to insert() or
     * update() based on state.
     * @throws DataSourceException if there is an error from the DataSource.
     */
    public void save() throws DataSourceException
    {
        // check for valid state
        if (getState() != NEW && getState() != IN_DB)
            //throw new RuntimeException("DataObject.save Invalid state");
            throw new RuntimeException(ERR_INVALID_STATE.getText());

        // check for key collision and throw a KeyConstraintException
        // if the keys will collide.
        if (_doKeyCheck && keyCheck())
            throw new KeyConstraintException();

        // pre save hook
        preSave();

        // switch on state
        if(getState() == NEW)
            insert();
        else if(getState() == IN_DB)
            update();

        // post save hook
        postSave();
    }

    /**
     * Called pre insert / update call on DataSource
     */
    protected void preSave() throws DataSourceException
    {
    }

    /**
     * Called post insert / update call on DataSource
     */
    protected void postSave() throws DataSourceException
    {
    }


    //--------------------------------------------------------------------------
    /**
     * Check if there will be a key collission when this object is saved.
     * @return true if this object's unique keys will collide with another
     *   object already in the DB.
     */
    public boolean keyCheck() throws DataSourceException
    {
        // get the unique key fields
        Entity entity    = getEntity();
        Vector keyFields = entity.getUniqueKeyFields();

        // if this is a new object (will cause an insert) and any of the
        // unique keys are generated (they will not exist yet) then there
        // will be no collission as generated keys are made unique
        if (getState() == NEW)
            for (int i = 0; i < keyFields.size(); i++)
            {
                Field f = (Field) keyFields.get(i);
                if (entity.isFieldGenerated(f))
                    return false;
            }

        // skip if no unique key fields
        if (keyFields.size() <= 0)
            return false;

        // if this is in the DB (i.e. will cause an update) and the keys
        // have not changed the there is no collission
        if (getState() == IN_DB)
        {
            boolean keysChanged = false;
            for (int i = 0; i < keyFields.size() && !keysChanged; i++)
            {
                Field  f        = (Field) keyFields.get(i);
                Object val      = getObjectValue(f.getName());
                Object imageVal = getImageValue(f.getName());

                if (val != null)
                    if (imageVal != null)
                        keysChanged = !val.equals(imageVal);
                    else
                        keysChanged = true;
                else
                    keysChanged = (imageVal != null);
            }

            // update and no key change, cannot be a collission
            if (!keysChanged)
                return false;
        }

        // create a query
        Query query = new Query();

        // iterate key fields adding QueryCriterium
        for (int i = 0; i < keyFields.size(); i++)
        {
            Field  f = (Field) keyFields.get(i);
            Object value = getObjectValue(f.getName());
            QueryCriterium qc;
            if (value == null)
                qc = new QueryCriterium(getEntityName(),
                    f.getName(), QueryCriterium.OP_IS_NULL, null);
            else
                qc = new QueryCriterium(getEntityName(),
                    f.getName(), QueryCriterium.OP_EQUALS, value);
            query.addQueryCriterium(qc);
        }

        // perform query
        RecordSet rs = getDataSource().select(query);

        // check that there is only zero or one result object
        if (rs.next())
        {
            // have a row, get it
            DataObject dobj = rs.getCurrentObject(getEntityName());

            if (dobj != null)
            {
                // if the matching object is us (has the same system key) then
                // it is not a collission
                if (getState() == IN_DB && getSystemKey().equals(dobj.getSystemKey()))
                    return false;

                // collission, found a matching record
                return true;
            }
        }

        // no record found so no collission
        return false;
    }

    //--------------------------------------------------------------------------
    /**
     * @return String a concatenenated String of the values of all the unique
     *   key fields (from the values data set).
     */
    public String getUniqueKey()
    {
        // delegate specifying values (not image)
        return getUniqueKey(false);
    }

    //--------------------------------------------------------------------------
    /**
     * @return String a concatenenated String of the values of all the unique
     *   key fields (from the values data set).
     */
    public String getUniqueKey(boolean useImage)
    {
        // iterate key fields building return
        String ret = "";
        Vector fields = getEntity().getUniqueKeyFields();
        if(fields == null || fields.size() == 0)
            fields = getEntity().getFields();

        // sort to ensure fields are always output in the same order
        //Collections.sort(fields);

        for (int i = 0; i < fields.size(); i++)
        {
            Field f = (Field) fields.get(i);

            // get the object value for this field from the values or image
            Object obj = (useImage) ? getImageValue(f.getName()) : getObjectValue(f.getName());

            // convert to a string and add along with delimiter
            String str = Type.objectToString(obj);
            if(str != null)
            {
                if(ret != null && ret .length() > 0)
                    ret += ";";
                ret += str;
            }
        }

        // return
        return ret;
    }

    //--------------------------------------------------------------------------
    /**
     * @return String the key before this object was updated.
     */
    public String getPreUpdateKey()
    {
        return (_preUpdateKey == null)? getUniqueKey(true): _preUpdateKey;
    }

    //--------------------------------------------------------------------------
    /**
     * Set the pre update key.
     */
    private void setPreUpdateKey(String key)
    {
        _preUpdateKey = key;
    }

    //--------------------------------------------------------------------------
    /**
     * @return String the value of the system key field.
     */
    protected String getSystemKey()
    {
        // delegate
        return getSystemKey(false);
    }

    //--------------------------------------------------------------------------
    /**
     * @return String the value of the system key field
     * @param useImage true if the values retreived should come from the image array
     * instead of the current values array
     */
    protected String getSystemKey(boolean useImage)
    {
        // get the system key field
        String ret = "";
        Field f = getEntity().getSystemKeyField();

        // get the value
        if(f != null)
        {
            // get the object value for this field from the values or image
            Object obj = (useImage) ? getImageValue(f.getName()) : getObjectValue(f.getName());

            // convert to a string
            ret = Type.objectToString(obj);
        }

        // return the key
        return ret;
    }

    //--------------------------------------------------------------------------
    /**
     * Load this a data object using this as criteria from the DataSource.
     * This requires that the key field(s) has been set to identify which object to load.
     * The function correctly passes back the dynamically created DataObject.
     * @return the polymorphic object, or null on failure.
     * @throws DataSourceException if there is an error from the DataSource.
     */
    public DataObject loadPolymorphic() throws DataSourceException
    {
        DataObject ret = null;

        // validate state
        if(getState() == DELETED)
            //throw new RuntimeException("Bad State: cannot load a deleted objected");
            throw new RuntimeException(ERR_BAD_STATE.getText());

        // create a query
        Query query = new Query();

        // have we got a system key set
        String sysKey = this.getSystemKey();
        if(!Util.isEmpty(sysKey))
        {
            // select on this key
            query.addQueryCriterium(new QueryCriterium(getEntityName(), getEntity().getSystemKeyField().getName(),
                QueryCriterium.OP_EQUALS, sysKey));
        }
        // else use the unique keys
        else
        {
            // get the uk fields
            Vector fields = getEntity().getUniqueKeyFields();

            // if no uk fields, use all fields
            if(fields == null || fields.size() == 0)
                fields = getEntity().getFields();

            // iterate key fields adding QueryCriterium
            for (int i = 0; fields != null && i < fields.size(); i++)
            {
                Field f = (Field) fields.get(i);
                QueryCriterium qc = new QueryCriterium(getEntityName(),
                    f.getName(), QueryCriterium.OP_EQUALS, getObjectValue(f.getName()));
                query.addQueryCriterium(qc);
            }
        }

        // perform query
        RecordSet rs = getDataSource().select(query);

        // check that there is only zero or one result object
        if (rs.next())
        {
            // have a row, get it
            ret = rs.getCurrentObject(getEntityName());

            // check for another row and log warning
            if (rs.next())
                //Log.warning("DataObject.load returned more than one object");
                Log.warning(WARNING_MORE_THEN_ONE.getText());
        }

        return ret;
    }

    //--------------------------------------------------------------------------
    /**
     * Load this data object from the DataSource. This requires that the key
     * field(s) has been set to identify which object to load.
     * @return true if the objet was found and loaded.
     * @throws DataSourceException if there is an error from the DataSource.
     */
    public boolean load() throws DataSourceException
    {
        DataObject dobj = loadPolymorphic();
        // if dobj is non-null then load its data into this. since dobj
        // is then discarded there is no need to clone.
        if (dobj != null)
        {
            // check for polymorphic classes and throw exception if incorrect type
            assert dobj.getClass().equals(getClass());

            _values = dobj._values;
            _image = dobj._image;
            setState(IN_DB);

            // success
            return true;
        }

        // fall through to here if no dobj is found
        return false;
    }

    //--------------------------------------------------------------------------
    /**
     * Insert the DataObject into its DataSource
     * @throws DataSourceException if there is an error from the DataSource.
     * @roseuid 3973CADC01E9
     */
    protected void insert() throws DataSourceException
    {
        // validate state
        if(getState() != NEW)
            //throw new RuntimeException("Invalid State");
            throw new RuntimeException(ERR_INVALID_STATE2.getText());

        // must have a data source
        if(getDataSource() == null)
            //throw new RuntimeException("No DataSource set for DataObject");
            throw new RuntimeException(ERR_NO_DATA_OBJECT.getText());

        // delegate to datasource
        if(getDataSource().insert(this))
        {
            // set the image
            setImage();

            // set the state
            setState(IN_DB);

            // notify listeners
            DataManager.notifyListeners(new DataChangeNotification(this, getEntityName(), getClass(), getUniqueKey(), DataChangeNotification.INSERT));
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Update this object into its DataSource.
     * @throws DataSourceException if there is an error from the DataSource.
     * @roseuid 3973CDC201B4
     */
    protected void update() throws DataSourceException
    {
        // validate state
        if(getState() != IN_DB)
            throw new RuntimeException(ERR_INVALID_STATE2.getText());

        // must have a data source
        if(getDataSource() == null)
            //throw new RuntimeException("No DataSource set for DataObject");
            throw new RuntimeException(ERR_NO_DATA_OBJECT.getText());

        // get the old key (the one in DB) before the update and setImage
        // get from image as that is what update uses
        String cuk = getUniqueKey(true);

        // delegate to datasource
        if(getDataSource().update(this))
        {
            // set the image
            setImage();

            // notify listeners
            DataManager.notifyListeners(new DataChangeNotification(this,
                getEntityName(), getClass(), cuk, DataChangeNotification.UPDATE));
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Mark this object fopr deletion
     * @throws DataSourceException if there is an error from the DataSource.
     */
    public void markForDeletion()
    {
        // validate state
        if(getState() != IN_DB)
            //throw new RuntimeException("Invalid State");
            throw new RuntimeException(ERR_INVALID_STATE2.getText());

        // set state
        setState(MARKED_FOR_DELETION);
    }

    //--------------------------------------------------------------------------
    /**
     * Delete this object from the DataSource.
     * @throws DataSourceException if there is an error from the DataSource.
     * @roseuid 3973CDE40312
     */
    public void delete() throws DataSourceException
    {
        // validate state
        if(!(getState() == IN_DB || getState() == MARKED_FOR_DELETION))
            //throw new RuntimeException("Invalid State");
            throw new RuntimeException(ERR_INVALID_STATE2.getText());

        // must have a data source
        if(getDataSource() == null)
            //throw new RuntimeException("No DataSource set for DataObject");
            throw new RuntimeException(ERR_NO_DATA_OBJECT.getText());

        // get the key before we delete and clear, get from image as that is
        // what delete uses
        String dobjKey = getUniqueKey(true);

        // pre delete
        preDelete();

        // delegate to datasource
        if(getDataSource().delete(this))
        {
            // set state to deleted
            setState(DELETED);

            // notify listeners
            DataManager.notifyListeners(new DataChangeNotification(this, getEntityName(), getClass(), dobjKey, DataChangeNotification.DELETE));

            // post delete
            postDelete();
        }
    }

    /**
     * Called pre delete call on DataSource
     */
    protected void preDelete() throws DataSourceException
    {
    }

    /**
     * Called post delete call on DataSource
     */
    protected void postDelete() throws DataSourceException
    {
    }


    //--------------------------------------------------------------------------
    /**
     * Clear the object. Sets state back to DataObject.NEW
     */
    public void clear()
    {
        // cannot even clear a deleted object
        if(getState() == DELETED)
            //throw new RuntimeException("You may not reuse a deleted object");
            throw new RuntimeException(ERR_NOT_REUSE.getText());

        // clear values and image
        clearValues();
        clearImage();

        // set state to new
        setState(NEW);

        // also clear the pre-update key
        _preUpdateKey = null;
    }

    //--------------------------------------------------------------------------
    /**
     * Copies the values table into the image table
     * @return void
     */
    public void setImage()
    {
        // set the current image key as the pre update key
        setPreUpdateKey(getUniqueKey(true));

        // clone the values table
        if(_values != null)
            _image = (Hashtable)_values.clone();
    }

    /**
     * Revert the values back to the image
     */
    public void revertValuesToImage()
    {
        // clone the image table
        if(_image != null)
            _values = (Hashtable)_image.clone();
    }

    //--------------------------------------------------------------------------
    /**
     * Copies the values table of the param object to this.
     * @param dobj theDataObject to import values from.
     */
    public void importValues(DataObject dobj)
    {
        // clone the values table
        if(dobj != null)
            _values = (Hashtable)dobj._values.clone();
    }

    //--------------------------------------------------------------------------
    /**
     * Clears the image table.
     */
    private void clearImage()
    {
        // clear the table
        if(_image != null)
            _image.clear();
    }

    //--------------------------------------------------------------------------
    /**
     * Returns the image table.
     * @return Hashtable the image table.
     */
    private Hashtable getImage()
    {
        return _image;
    }

    //--------------------------------------------------------------------------
    /**
     * Returns the values table.
     * @return Hashtable the values table.
     */
    private Hashtable getValues()
    {
        return _values;
    }

    //--------------------------------------------------------------------------
    /**.
     * Clears the values table
     */
    private void clearValues()
    {
        // clear the table
        if(_values != null)
            _values.clear();
    }

    //--------------------------------------------------------------------------
    /**
     * Returns the value of the first naming field.
     * @return String the value of the naming field.
     */
    public String toString()
    {
        // get the naming field
        Field f;
        String str = "";
        Vector fields = getEntity().getFields();
        Enumeration enums = (fields == null)? null: fields.elements();
        while(enums != null && enums.hasMoreElements())
        {
            // get the field
            f = (Field)enums.nextElement();

            // set nf if naming
            if(f != null && f.hasFlag(Field.FF_NAMING))
            {
                // add the comma
                if(str.length() > 0)
                    str += "; ";

                // add the naming field value
                str += getStringValue(f.getName());
            }
        }

        // return
        return (str.length() == 0)? "No Name": str;
    }

    //--------------------------------------------------------------------------
    /**
     * Returns a long description string including concatenation of fields and
     * values.
     * @return String description.
     */
    public String getLongDesc()
    {
        // class name
        String str = "DataObject: Class = " + getClass().getName();

        // iterate fields
        Field f;
        Vector fields = getEntity().getFields();
        Enumeration enums = (fields == null)? null: fields.elements();
        while(enums != null && enums.hasMoreElements())
        {
            // get the field and build string
            f = (Field)enums.nextElement();
            if(f != null)
                str += "; " + f.getName() + " = " + getStringValue(f.getName());
        }

        // return
        return str;
    }

    //--------------------------------------------------------------------------
    /**
     * Equality test. Used by collections such as set and hashtable. This test
     * considers DataObjects equal if their class and key fields are equal.
     * @param obj, the object to compare to.
     * @return true if the object's class and key fields match.
     */
    public boolean equals(Object obj)
    {
        // delegate, specifying matching class, but not all fields, and not
        // ignore system key
        return equals(obj, false);
    }

    //--------------------------------------------------------------------------
    /**
     * Equality test. Used by collections such as set and hashtable. This test
     * considers DataObjects equal if their class and key fields are equal.
     * @param obj, the object to compare to.
     * @param ignoreSystemKey true if dont compare system keys
     * @return true if the object's class and key fields match.
     */
    public boolean equals(Object obj, boolean ignoreSystemKey)
    {
        // delegate, specifying matching class, but not all fields
        return equals(obj, true, false, ignoreSystemKey);
    }

    //--------------------------------------------------------------------------
    /**
     * Equality test, parameterised to allow varying strictness.
     * @param obj, the object to comapre to.
     * @param bClass, if true tests that classes match.
     * @param bAllFields if true tests all fields, not just keys.
     * @param ignoreSystemKey true if dont compare system keys
     * @return true if the objects match.
     */
    private boolean equals(Object obj, boolean bClass, boolean bAllFields, boolean ignoreSystemKey)
    {
        // not equal if not a data object
        if (!(obj instanceof DataObject))
            return false;
        DataObject dobj = (DataObject) obj;

        // test the reference equality
        if(this == dobj)
        {
            //Log.debug("Reference equality: " + this + " : " + dobj);
            return true;
        }

        // not equal if different entities
        if (getEntityName().compareToIgnoreCase(dobj.getEntityName()) != 0)
            return false;

        // not equal if bClass is true and the classes are not the same
        if (bClass && !getClass().getName().equals(dobj.getClass().getName()))
            return false;

        // test the system keys, ignore if generated and either object is NEW
        boolean isGenerated;
        boolean haveNewObject = (getState() == NEW || dobj.getState() == NEW);
        Field sysKeyField = getEntity().getSystemKeyField();
        if(!ignoreSystemKey && sysKeyField != null)
        {
            isGenerated = getEntity().isFieldGenerated(sysKeyField);
            if(!isGenerated || !haveNewObject)
            {
                boolean isEqual = getSystemKey().equalsIgnoreCase(dobj.getSystemKey());
                //Log.debug("System key equality = " + isEqual + " ; " + this + " ; " + dobj);
                return isEqual;
            }
        }

        // compare fields, all or just key depending on bAllFields
        Field  f;
        String fldName;
        Vector fields = getEntity().getFields();
        Enumeration enums = (fields == null) ? null : fields.elements();
        boolean bDoneCompare = false;
        Object val, paramVal;

        while (enums != null && enums.hasMoreElements())
        {
            // get the field and compare if  key or bAllFields
            f = (Field) enums.nextElement();
            if (f != null && (bAllFields || f.hasFlag(Field.FF_UNIQUE_KEY)))
            {
                // if field is generated and either object is new, ignore
                isGenerated = getEntity().isFieldGenerated(f);
                if(isGenerated && haveNewObject)
                    continue;

                // ignore system key
                if(ignoreSystemKey && f.hasFlag(Field.FF_SYSTEM_KEY))
                    continue;

                // compare param object and this
                bDoneCompare = true;
                fldName = f.getName();
                val = getObjectValue(fldName);
                paramVal = dobj.getObjectValue(fldName);

                // both null is equal
                if(!(val == null && paramVal == null))
                {
                    // either null is inequal
                    if (val == null || paramVal == null || !val.equals(paramVal))
                        return false;
                }
            }
        }

        // special case, check that at least one comparison was done when not
        // using bAllFields to catch data objects that do not have a key.
        // if there is no key then assume comparison is on all fields
        if (!bAllFields && !bDoneCompare)
            return equals(obj, bClass, true, true);

        // no inequalities found, so objects are equal
        //Log.debug("Unique key equality: " + this + " : " + dobj);
        return true;
    }

    //--------------------------------------------------------------------------
    /**
     * Equality test, compare on entity name and key fields.
     * @param entityName, the name of the entity.
     * @param uniqueKey, the unique key @see getUniqueKey.
     * @return true if the keys match.
     */
    public boolean equals(String entityName, String uniqueKey)
    {
        // compare on entity name and key
        if (getEntityName().equalsIgnoreCase(entityName))
            if (getPreUpdateKey().equals(uniqueKey))
                return true;

        // fall through, not equal
        return false;
    }

    //--------------------------------------------------------------------------
    /**
     * Calculate the hashcode from all the key values and the entity name.
     * If there are no keys efined then hash all fields.
     * Use of data objects in Sets and Hashtables depends on this function being
     * consistent with or less rigorous than equals().
     * @return the hash code;
     */
    public int hashCode()
    {
        Field       f;
        String      fldName;
        int         keyHash = getEntityName().toUpperCase().hashCode();
        int         allHash = keyHash;
        int         partHash;
        boolean     bHasKey = false;
        Vector   fields  = getEntity().getFields();
        Enumeration enums    = (fields == null) ? null : fields.elements();

        // iterate fields
        Object value;
        while (enums != null && enums.hasMoreElements())
        {
            // get the field and hash it
            f = (Field) enums.nextElement();
            value = getObjectValue(f.getName());
            if(value != null)
            {
                partHash = getObjectValue(f.getName()).hashCode();

                // add it to hash of all fields
                allHash ^= partHash;

                // if this is a key field add it to the key hash and set key flag
                if (f != null && f.hasFlag(Field.FF_UNIQUE_KEY))
                {
                    keyHash ^= partHash;
                    bHasKey = true;
                }
            }
        }

        // if no keys then use all fields for hash
        return (bHasKey) ? keyHash : allHash;
    }
}

//==============================================================================
// end of file DataObject.java
//==============================================================================
