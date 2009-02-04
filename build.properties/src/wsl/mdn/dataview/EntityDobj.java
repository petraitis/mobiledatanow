//Source file: D:\\dev\\wsl\\mdn\\dataview\\EntityDobj.java

package wsl.mdn.dataview;

// imports
import java.util.Vector;
import java.io.IOException;
import java.io.ObjectInputStream;
import wsl.fw.util.Util;
import wsl.fw.datasource.*;

//------------------------------------------------------------------------------
/**
 * DataObject that persists Entities
 */
public class EntityDobj extends DataObject
{
    //--------------------------------------------------------------------------
    // constants

    public final static String ENT_ENTITY           = "TBL_ENTITY";
    public final static String FLD_ID               = "FLD_ID";
    public final static String FLD_NAME             = "FLD_NAME";
    public final static String FLD_FLAGS            = "FLD_FLAGS";
    public final static String FLD_DSID             = "FLD_DSID";
    public final static String FLD_DESCRIPTION      = "FLD_DESCRIPTION";


    //--------------------------------------------------------------------------
    // attributes

    private transient EntityImpl _impl         = null;
    private transient Vector     _fields       = new Vector();
    private transient boolean    _fieldsLoaded = false;
    private EntitySchemaName     _esn = null;

    //--------------------------------------------------------------------------
    // construction

    /**
     * Blank ctor
     */
    public EntityDobj()
    {
    }
    public EntityDobj(int tableId, String tableName, int flags, int dsId,  String description)
    {
    	this.setId(tableId);
    	this.setName(tableName);
    	this.setFlags(flags);
    	this.setDataSourceId(dsId);
    	this.setDescription(description);
    }
    /**
     * Entity ctor
     */
    public EntityDobj(Entity ent)
    {
        this.setName(ent.getName());
        this.setFlags(ent.getFlags());
    }


    //--------------------------------------------------------------------------
    // persistence

    /**
     * Static factory method to create the entity to be used by this dataobject
     * and any subclasses. This is called by the DataManager's factory when
     * creating a ENTITY entity.
     * @return the created entity.
     */
    public static Entity createEntity()
    {
        // create the entity
        Entity ent = new EntityImpl(ENT_ENTITY, EntityDobj.class);

        // add the key generator for the system id
        ent.addKeyGeneratorData(new DefaultKeyGeneratorData(ENT_ENTITY, FLD_ID));

        // create the fields and add them to the entity
        ent.addField(new FieldImpl(FLD_ID, Field.FT_INTEGER, Field.FF_SYSTEM_KEY | Field.FF_UNIQUE_KEY));
        ent.addField(new FieldImpl(FLD_NAME, Field.FT_STRING, Field.FF_NAMING));
        ent.addField(new FieldImpl(FLD_FLAGS, Field.FT_INTEGER));
        ent.addField(new FieldImpl(FLD_DSID, Field.FT_INTEGER));
        ent.addField(new FieldImpl(FLD_DESCRIPTION, Field.FT_STRING));

        // create the joins and add them to the entity
        ent.addJoin(new JoinImpl(ENT_ENTITY, FLD_DSID,
            DataSourceDobj.ENT_DATASOURCE, DataSourceDobj.FLD_ID));

        // return the entity
        return ent;
    }

    /**
     * @return String the name of the entity that defines this DataObject
     */
    public String getEntityName()
    {
        return ENT_ENTITY;
    }


    //--------------------------------------------------------------------------
    // accessors


    /**
     * @return int the id of this Entity
     */
    public int getId()
    {
        return getIntValue(FLD_ID);
    }

    /**
     * Set the id of this Entity
     * @param id
     */
    public void setId(int id)
    {
        setValue(FLD_ID, id);
    }

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
        return Record.class;
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
        return getIntValue(FLD_FLAGS);
    }

    /**
     * Set the entity flags
     * @param flags the value to set
     */
    public void setFlags(int val)
    {
        setValue(FLD_FLAGS, val);
    }

    /**
     * @return int the id of the parent DataSource of this Entity
     */
    public int getDataSourceId()
    {
        return getIntValue(FLD_DSID);
    }

    /**
     * Set the id of the parent DataSource of this Entity
     * @param id the parent DataSource id
     */
    public void setDataSourceId(int id)
    {
        setValue(FLD_DSID, id);
    }

    /**
     * Returns the description of the entity
     * @return String
     */
    public String getDescription()
    {
        return getStringValue(FLD_DESCRIPTION);
    }

    /**
     * Sets the entity description into the Entity
     * @param name
     * @return void
     */
    public void setDescription(String name)
    {
        setValue(FLD_DESCRIPTION, name);
    }


    //--------------------------------------------------------------------------
    // impl

    /**
     * Create and return the EntityImpl object.
     * The field and entity dobjs must define a valid entity.
     * The created entity should be added to a DataSource or have its DataSource
     * set.
     * @return the created Entity.
     */
    public Entity createImpl()
    {
        // create the entity
        _impl = new EntityImpl(getName(), getDefaultClass());

        // set flags
        _impl.setFlags(getFlags());

        // create and add the fields
        Vector v = getFields();
        for (int i = 0; i < v.size(); i++)
        {
            FieldDobj fd = (FieldDobj) v.get(i);
            _impl.addField(fd.createImpl());
        }

        // fixme, what about key generator data?

        // validate
        //_impl.verifyEntityDefinition();

        return _impl;
    }


    //--------------------------------------------------------------------------
    // fields

    /**
     * Add a single Field to the Entity
     * @param f the field to add
     */
    public void addField(FieldDobj f)
    {
        // validate
        Util.argCheckNull(f);

        // add the entity
        _fields.add(f);

        // set the loaded flag
        _fieldsLoaded = true;
    }

    /**
     * Returns the fields of the entity
     * @return Hashtable
     */
    public Vector getFields()
    {
        // if not loaded, load
        if(!_fieldsLoaded)
        {
            // must be indb
            if(this.getState() == DataObject.IN_DB)
            {
                // build query
                DataSource ds = DataManager.getSystemDS();
                Query q = new Query(FieldDobj.ENT_FIELD);
                q.addQueryCriterium(new QueryCriterium(FieldDobj.ENT_FIELD,
                    FieldDobj.FLD_ENTITYID, QueryCriterium.OP_EQUALS,
                    new Integer(this.getId())));

                try
                {
                    // execute query and build vector
                    FieldDobj f;
                    RecordSet rs = ds.select(q);
                    while(rs != null && rs.next())
                    {
                        // get the field
                        f = (FieldDobj)rs.getCurrentObject();
                        if(f != null)
                            addField(f);
                    }
                }
                catch(Exception e)
                {
                    throw new RuntimeException(e.toString());
                }
            }

            // set flag
            _fieldsLoaded = true;
        }

        // return
        return _fields;
    }

    /**
     * Returns a the field mapped to the param name
     * @param fieldName the name of the field
     * @return Field
     */
    public FieldDobj getField(String fieldName)
    {
        // iterate the entities
        FieldDobj f = null;
        for(int i = 0; i < getFields().size(); i++)
        {
            // get the entity and compare
            f = (FieldDobj)getFields().elementAt(i);
            if(f != null && f.getName().equalsIgnoreCase(fieldName))
                return f;
        }

        // not found
        throw new RuntimeException("Field not found: " + fieldName);
    }

    /**
     * Remove a field
     */
    public FieldDobj removeField(FieldDobj f, boolean doDelete)
    {
        // if it is indb, remove by key
        FieldDobj rem = null;
        if(f.getState() == DataObject.IN_DB)
        {
            // iterate
            FieldDobj temp;
            for(int i = 0; i < getFields().size(); i++)
            {
                temp = (FieldDobj)getFields().elementAt(i);
                if(temp != null && temp.getState() == DataObject.IN_DB &&
                    temp.getId() == f.getId())
                    rem = (FieldDobj)getFields().remove(i);
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
                rem.delete();
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
        // save all fields
        FieldDobj f;
        for(int i = 0; _fieldsLoaded && i < getFields().size(); i++)
        {
            f = (FieldDobj)getFields().elementAt(i);
            if(f != null)
            {
                // if new set keys
                if(f.getState() == DataObject.NEW)
                {
                    f.setEntityId(this.getId());
                    f.setDsId(this.getDataSourceId());
                }

                // save
                f.save();
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
        FieldDobj f;
        for(int i = 0; i < getFields().size(); i++)
        {
            f = (FieldDobj)getFields().elementAt(i);
            if(f != null && f.getState() == DataObject.IN_DB)
                f.delete();
        }
        getFields().clear();

        // delete transfer entities
        deleteTransferEntities();
    }

    /**
     * delete transfer entities
     */
    private void deleteTransferEntities() throws DataSourceException
    {
        // build query
        Query q = new Query(DataTransfer.ENT_DATATRANSFER);
        QueryCriterium qc = new QueryCriterium(DataTransfer.ENT_DATATRANSFER,
            DataTransfer.FLD_DSID,
            QueryCriterium.OP_EQUALS, new Integer(this.getDataSourceId()));
        q.addQueryCriterium(qc);
        qc = new QueryCriterium(TransferEntity.ENT_TRANSFERENTITY,
            TransferEntity.FLD_ENTITYNAME,
            QueryCriterium.OP_EQUALS, this.getName());
        q.addQueryCriterium(qc);

        // do select
        RecordSet rs = DataManager.getSystemDS().select(q);

        // iterate and delete transfer entities
        TransferEntity te;
        while(rs.next())
        {
            te = (TransferEntity)rs.getCurrentObject(TransferEntity.ENT_TRANSFERENTITY);
            if(te != null && te.getState() == DataObject.IN_DB)
                te.delete();
        }
    }


    //--------------------------------------------------------------------------
    /**
     * Serialization support.
     */
    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException
    {
        _impl         = null;
        _fields       = new Vector();
        _fieldsLoaded = false;

        // do default loading
        in.defaultReadObject();
    }

    //--------------------------------------------------------------------------
    /**
     * @return the EntitySchemaName, used for partially imported entities.
     *   Should not be used except is DS import wizard.
     */
    public EntitySchemaName getEsn()
    {
        return _esn;
    }

    //--------------------------------------------------------------------------
    /**
     *
     */
    public void setEsn(EntitySchemaName esn)
    {
        _esn = esn;
        if (_esn != null && !Util.isEmpty(_esn.getFullTableName()))
            setName(_esn.getFullTableName());
    }

}
