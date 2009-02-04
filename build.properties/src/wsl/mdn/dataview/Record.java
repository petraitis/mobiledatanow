//==============================================================================
// Record.java
// Copyright (c) 2001 WAP Solutions Ltd.
//==============================================================================

package wsl.mdn.dataview;

// imports
import java.util.Vector;
import wsl.fw.util.Util;
import wsl.fw.datasource.*;


/**
 * DataObject subclass for dynamically created DataObjects that are built from
 * a dynamic Entity built from an EntityDobj.
 * Can be created manually to reflect a given entity, or automatically
 * by the DataSource when building query results.
 */
public class Record extends DataObject
{
    //--------------------------------------------------------------------------
    // attributes

    private Vector _sourceRecs = new Vector();


    //--------------------------------------------------------------------------
    /**
     * Default constructor, used by DataSource when creating from a query.
     * The DS should then call setEntity as Records are not subclassed to know
     * their entity.
     */
    public Record()
    {
        // do not do init as we don't yet have an entity, the DataSource should
        // set it later
        super(false);
    }

    //--------------------------------------------------------------------------
    /**
     * Parameterised constructor that sets the entity to fully construct a
     * record.
     * @param entity, the entity that defines the structure of this record.
     */
    public Record(Entity entity)
    {
        // do not do init as we manually set the entity
        super(false);

        // set the entity
        setEntity(entity);
    }

    //--------------------------------------------------------------------------
    /**
     * Special geberic implementation of getEntityName to get from our entity
     * if one is set.
     * @return the entity name.
     */
    public String getEntityName()
    {
        if (iGetEntity() == null)
            return null;
        else
            return iGetEntity().getName();
    }


    //--------------------------------------------------------------------------
    // source records

    /**
     * @return Vector the Vector of source records
     */
    public Vector getSourceRecords()
    {
        return _sourceRecs;
    }

    /**
     * Add a source records
     * @param rec the source record
     */
    public void addSourceRecord(Record rec)
    {
        // validate
        Util.argCheckNull(rec);

        // add to source recs
        _sourceRecs.add(rec);
    }

    /**
     * Get a source record from an entity name
     * @param entityname
     * @return Record the source record
     */
    public Record getSourceRecord(String entityName)
    {
        // iterate source recs
        Record rec;
        for(int i = 0; i < _sourceRecs.size(); i++)
        {
            // get the rec and compare
            rec = (Record)_sourceRecs.elementAt(i);
            if(rec != null && rec.getEntity().getName().equalsIgnoreCase(entityName))
                return rec;
        }

        // not found
        return null;
    }

    /**
     * Get the value of a source record field
     * @param sourceEntName the name of the source entity
     * @param sourceFieldName the name of the source field
     * @return Object the value of the source field
     */
    public Object getSourceValue(String sourceEntName, String sourceFieldName)
    {
        // get the source rec
        Record sourceRec = getSourceRecord(sourceEntName);

        // return
        return (sourceRec != null)? sourceRec.getObjectValue(sourceFieldName): null;
    }


    //--------------------------------------------------------------------------
    // save that avoids keyCheck

    /**
     * Save the DataObject into its DataSource. Delegates to insert() or
     * update() based on state.
     * @param doKeyCheck avois a key constraint check if true
     * @throws DataSourceException if there is an error from the DataSource.
     */
    public void save(boolean doKeyCheck) throws DataSourceException
    {
        boolean old = _doKeyCheck;
        _doKeyCheck = doKeyCheck;
        save();
        _doKeyCheck = old;
    }

    //--------------------------------------------------------------------------
    // clone and copy

    /**
     * Clone and return the current this
     * @return Object the cloned Record
     */
    public Object clone()
    {
        Record r = new Record(this.getEntity());
        r._sourceRecs = _sourceRecs;
        r.setState(this.getState());

        // values
        r.copyValues(this);

        // image
        if(getState() == DataObject.IN_DB)
            r.setImage();

        // return
        return r;
    }

    //--------------------------------------------------------------------------
    /**
     * Copy the values of the param into this
     * @param dobj the DataObject to copy
     */
    public void copyValues(DataObject dobj)
    {
        // values
        Vector fields = dobj.getEntity().getFields();
        for(int i = 0; fields != null && i < fields.size(); i++)
        {
            Field f = (Field)fields.elementAt(i);
            if(f != null)
                setValue(f.getName(), dobj.getObjectValue(f.getName()));
        }
    }
}

//==============================================================================
// end of file Record.java
//==============================================================================
