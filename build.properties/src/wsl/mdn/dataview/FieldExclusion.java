//Source file: D:\\dev\\wsl\\mdn\\dataview\\FieldExclusion.java

package wsl.mdn.dataview;

import java.util.Vector;

import wsl.fw.datasource.*;
import wsl.fw.security.Group;

/**
 */
public class FieldExclusion extends DataObject
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:35:35 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/dataview/FieldExclusion.java $ ";

    //--------------------------------------------------------------------------
    // constants

    public final static String ENT_FIELDEXCLUSION    = "TBL_FIELDEXCLUSION";

    public final static String FLD_ID               = "FLD_ID";
    public final static String FLD_GROUPID          = "FLD_GROUPID";
    public final static String FLD_DVFIELDID        = "FLD_DVFIELDID";

    public FieldExclusion()
    {
    }

    /**
     * @return String the name of the entity that defines this DataObject
     */
    public String getEntityName()
    {
        return ENT_FIELDEXCLUSION;
    }
    //--------------------------------------------------------------------------
    // persistence

    /**
     * Static factory method to create the entity to be used by this dataobject
     * and any subclasses. This is called by the DataManager's factory when
     * creating a FIELDEXCLUSION entity.
     * @return the created entity.
     */
    public static Entity createEntity()
    {
        // create the entity
        Entity ent = new EntityImpl(ENT_FIELDEXCLUSION, FieldExclusion.class);

        // add the key generator for the system id
        ent.addKeyGeneratorData(new DefaultKeyGeneratorData(ENT_FIELDEXCLUSION, FLD_ID));

        // create the fields and add them to the entity
        ent.addField(new FieldImpl(FLD_ID, Field.FT_INTEGER, Field.FF_SYSTEM_KEY));
        ent.addField(new FieldImpl(FLD_GROUPID, Field.FT_INTEGER, Field.FF_UNIQUE_KEY | Field.FF_NAMING));
        ent.addField(new FieldImpl(FLD_DVFIELDID, Field.FT_INTEGER, Field.FF_UNIQUE_KEY | Field.FF_NAMING));

        // create the joins and add them to the entity
        Vector joins = new Vector();
        joins.add(new JoinImpl(ENT_FIELDEXCLUSION, FLD_GROUPID, Group.ENT_GROUP, Group.FLD_ID));
        joins.add(new JoinImpl(ENT_FIELDEXCLUSION, FLD_DVFIELDID, DataViewField.ENT_DVFIELD, DataViewField.FLD_ID));
        ent.setJoins(joins);


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
     * Returns the group id
     * @return int
     */
    public int getGroupId()
    {
        return getIntValue(FLD_GROUPID);
    }

    /**
     * Set the group id
     * @param  groupId the value to set
     */
    public void setGroupId(int groupId)
    {
        setValue(FLD_GROUPID, groupId);
    }

    /**
     * Returns the dv field id
     * @return int
     */
    public int getDvFieldId()
    {
        return getIntValue(FLD_DVFIELDID);
    }

    /**
     * Set the dv field id
     * @param dvFieldId the value to set
     */
    public void setDvFieldId(int dvFieldId)
    {
        setValue(FLD_DVFIELDID, dvFieldId);
    }
}
