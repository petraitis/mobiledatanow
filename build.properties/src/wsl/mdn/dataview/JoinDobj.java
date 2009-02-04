//Source file: D:\\dev\\wsl\\mdn\\dataview\\JoinDobj.java

package wsl.mdn.dataview;

// imports
import wsl.fw.datasource.*;

/**
 * DataObject used to persist Join objects
 */
public class JoinDobj extends DataObject
{
    //--------------------------------------------------------------------------
    // constants

    public final static String ENT_JOIN             = "TBL_JOIN";
    public final static String FLD_ID               = "FLD_ID";
    public final static String FLD_DSID             = "FLD_DSID";
    public final static String FLD_LEFT_ENT         = "FLD_LEFT_ENT";
    public final static String FLD_LEFT_FIELD       = "FLD_LEFT_FIELD";
    public final static String FLD_RIGHT_ENT        = "FLD_RIGHT_ENT";
    public final static String FLD_RIGHT_FIELD      = "FLD_RIGHT_FIELD";
    public final static String FLD_JOIN_TYPE        = "FLD_JOIN_TYPE";


    //--------------------------------------------------------------------------
    // attributes

    private transient JoinImpl _impl = null;


    //--------------------------------------------------------------------------
    // construction

    public JoinDobj()
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
    public static Entity createEntity()
    {
        // create the entity
        Entity ent = new EntityImpl(ENT_JOIN, JoinDobj.class);

        // add the key generator for the system id
        ent.addKeyGeneratorData(new DefaultKeyGeneratorData(ENT_JOIN, FLD_ID));

        // create the fields and add them to the entity
        ent.addField(new FieldImpl(FLD_ID, Field.FT_INTEGER, Field.FF_SYSTEM_KEY | Field.FF_UNIQUE_KEY));
        ent.addField(new FieldImpl(FLD_DSID, Field.FT_INTEGER));
        ent.addField(new FieldImpl(FLD_LEFT_ENT, Field.FT_STRING));
        ent.addField(new FieldImpl(FLD_LEFT_FIELD, Field.FT_STRING));
        ent.addField(new FieldImpl(FLD_RIGHT_ENT, Field.FT_STRING));
        ent.addField(new FieldImpl(FLD_RIGHT_FIELD, Field.FT_STRING));
        ent.addField(new FieldImpl(FLD_JOIN_TYPE, Field.FT_STRING));

        // create the joins and add them to the entity
        ent.addJoin(new JoinImpl(ENT_JOIN, FLD_DSID,
            DataSourceDobj.ENT_DATASOURCE, DataSourceDobj.FLD_ID));

        // return the entity
        return ent;
    }

    /**
     * @return String the name of the entity that defines this DataObject
     */
    public String getEntityName()
    {
        return ENT_JOIN;
    }


    //--------------------------------------------------------------------------
    // accessors

    /**
     * @return int the id of this field
     */
    public int getId()
    {
        return getIntValue(FLD_ID);
    }

    /**
     * @return int the datasource id of this field
     */
    public int getDataSourceId()
    {
        return getIntValue(FLD_DSID);
    }

    /**
     * Set the datasource id of this field
     * @param id
     */
    public void setDataSourceId(int id)
    {
        setValue(FLD_DSID, id);
    }

    /**
     * @return String the left entity
     */
    public String getLeftEntity()
    {
        return getStringValue(FLD_LEFT_ENT);
    }

    /**
     * Set the left entity of this field
     * @param ent
     */
    public void setLeftEntity(String ent)
    {
        setValue(FLD_LEFT_ENT, ent);
    }

    /**
     * @return String the left field
     */
    public String getLeftField()
    {
        return getStringValue(FLD_LEFT_FIELD);
    }

    /**
     * Set the left field of this field
     * @param f
     */
    public void setLeftField(String f)
    {
        setValue(FLD_LEFT_FIELD, f);
    }

    /**
     * @return String the right entity
     */
    public String getRightEntity()
    {
        return getStringValue(FLD_RIGHT_ENT);
    }

    /**
     * Set the right entity of this field
     * @param ent
     */
    public void setRightEntity(String ent)
    {
        setValue(FLD_RIGHT_ENT, ent);
    }

    /**
     * @return String the right field
     */
    public String getRightField()
    {
        return getStringValue(FLD_RIGHT_FIELD);
    }

    /**
     * Set the right field of this field
     * @param f
     */
    public void setRightField(String f)
    {
        setValue(FLD_RIGHT_FIELD, f);
    }

    /**
     * @return String the join type
     */
    public String getJoinType()
    {
        return getStringValue(FLD_JOIN_TYPE);
    }

    /**
     * Set the join type of this field
     * @param type
     */
    public void setJoinType(String type)
    {
        setValue(FLD_JOIN_TYPE, type);
    }


    //--------------------------------------------------------------------------
    // impl

    /**
     * Create and return the JoinImpl object. The Join should then be added to
     * a dataSource or entity.
     * @return the created Join.
     */
    public Join createImpl()
    {
        _impl = new JoinImpl(getLeftEntity(), getLeftField(), getRightEntity(),
            getRightField(), getJoinType());

        return _impl;
    }


    //--------------------------------------------------------------------------
    // misc

    public String toString()
    {
        String ret = getStringValue(FLD_LEFT_ENT) + "." + getStringValue(FLD_LEFT_FIELD) +
            " ---> " +
            getStringValue(FLD_RIGHT_ENT) + "." + getStringValue(FLD_RIGHT_FIELD);
        return ret;
    }
}
