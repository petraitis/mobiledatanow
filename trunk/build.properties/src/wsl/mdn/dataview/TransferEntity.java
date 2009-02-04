package wsl.mdn.dataview;

import wsl.fw.datasource.*;


/**
 * Title:
 * Description:
 * Copyright:    Copyright (c)
 * Company:
 * @author
 * @version 1.0
 */

public class TransferEntity extends DataObject
{
    //--------------------------------------------------------------------------
    // constants

    public final static String ENT_TRANSFERENTITY   = "TBL_TRANSFERENTITY";
    public final static String FLD_ID               = "FLD_ID";
    public final static String FLD_DTID             = "FLD_DTID";
    public final static String FLD_ENTITYNAME       = "FLD_ENTITYNAME";


    //--------------------------------------------------------------------------
    // attributes

    private transient EntityDobj _entity = null;


    //--------------------------------------------------------------------------
    // construction

    /**
     * Default ctor
     */
    public TransferEntity()
    {
    }

    /**
     * @return String the name of the entity that defines this DataObject
     */
    public String getEntityName()
    {
        return ENT_TRANSFERENTITY;
    }


    //--------------------------------------------------------------------------
    // persistence

    /**
     * Static factory method to create the entity to be used by this dataobject
     * and any subclasses. This is called by the DataManager's factory when
     * creating a TRANSFERENTITY entity.
     * @return the created entity.
     */
    public static Entity createEntity()
    {
        // create the entity
        Entity ent = new EntityImpl(ENT_TRANSFERENTITY, TransferEntity.class);

        // add the key generator for the system id
        ent.addKeyGeneratorData(new DefaultKeyGeneratorData(ENT_TRANSFERENTITY, FLD_ID));

        // create the fields and add them to the entity
        ent.addField(new FieldImpl(FLD_ID, Field.FT_INTEGER, Field.FF_SYSTEM_KEY));
        ent.addField(new FieldImpl(FLD_DTID, Field.FT_INTEGER, Field.FF_UNIQUE_KEY));
        ent.addField(new FieldImpl(FLD_ENTITYNAME, Field.FT_STRING, Field.FF_UNIQUE_KEY | Field.FF_NAMING));

        // create the joins and add them to the entity
        ent.addJoin(new JoinImpl(ENT_TRANSFERENTITY, FLD_DTID,
            DataTransfer.ENT_DATATRANSFER, DataTransfer.FLD_ID));
        ent.addJoin(new JoinImpl(ENT_TRANSFERENTITY, FLD_ENTITYNAME,
            EntityDobj.ENT_ENTITY, EntityDobj.FLD_NAME));

        // return the entity
        return ent;
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
     * Returns the dt id
     * @return int
     */
    public int getDataTransferId()
    {
        return getIntValue(FLD_DTID);
    }

    /**
     * Set the dt id
     * @param  val the value to set
     */
    public void setDataTransferId(int val)
    {
        setValue(FLD_DTID, val);
    }

    /**
     * Returns the source entity name
     * @return String
     */
    public String getSourceEntityName()
    {
        return getStringValue(FLD_ENTITYNAME);
    }

    /**
     * Set the source entity name
     * @param val the value to set
     */
    public void setSourceEntityName(String val)
    {
        setValue(FLD_ENTITYNAME, val);
    }

    /**
     * Set the transient entity
     * @param ent the Entity
     */
    public void setEntityDobj(EntityDobj entity)
    {
        _entity = entity;
    }

    /**
     * Get the transient entity
     * @param EntityDobj
     */
    public EntityDobj getEntityDobj()
    {
        return _entity;
    }
}