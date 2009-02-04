//==============================================================================
// Privilege.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.security;

import wsl.fw.util.Util;
import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.Entity;
import wsl.fw.datasource.Field;
import wsl.fw.datasource.EntityImpl;
import wsl.fw.datasource.FieldImpl;
import wsl.fw.datasource.JoinImpl;
import wsl.fw.datasource.DefaultKeyGeneratorData;
import wsl.fw.datasource.Join;
import java.util.Vector;

//------------------------------------------------------------------------------
/**
 * Class to represent the privilege relationship between Feature and Group.
 */
public class Privilege extends DataObject
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/security/Privilege.java $ ";

    // the entity name
    public final static String ENT_PRIVILEGE = "FW_PRIVILEGE";

    // field names
    public final static String FLD_ID        = "FLD_ID";
    public final static String FLD_GROUPID   = "FLD_GROUPID";
    public final static String FLD_FEATUREID = "FLD_FEATUREID";

    //--------------------------------------------------------------------------
    /**
     * Default constructor. Intended for use by the datasource when creating
     * instances. Since a Privilege is invalid if it is not correctly initialized
     * ensure that setGroupId and setFeatureId are called.
     */
    public Privilege()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Constructor, set the foreign keys of this privilege.
     * @param groupId, the foreign key of the Group.
     * @param featureId, the foreign key of the Feature.
     * @throws IllegalArgumentException if the parameters are invalid.
     */
    public Privilege(int groupId, String featureId)
        throws IllegalArgumentException
    {
        // save group name and description
        setGroupId(groupId);
        setFeatureId(featureId);
    }

    //--------------------------------------------------------------------------
    /**
     * Constructor, set the foreign keys of this privilege.
     * @param group, the Group we reference.
     * @param feature, the Feature we reference.
     * @throws IllegalArgumentException if the parameters are invalid.
     */
    public Privilege(Group group, Feature feature)
        throws IllegalArgumentException
    {
        Util.argCheckNull(group);
        Util.argCheckNull(feature);

        // save group and feature keys
        setGroupId(group.getIntValue(Group.FLD_ID));
        setFeatureId(feature.getStringValue(Feature.FLD_NAME));
    }

    //--------------------------------------------------------------------------
    /**
     * Static factory method to create the entity to be used by this dataobject
     * and any subclasses. This is called by the DataManager's factory when
     * creating a PRIVILEGE entity.
     * @return the created entity.
     */
    public static Entity createEntity()
    {
        // create the group entity
        Entity privilegeEntity = new EntityImpl(ENT_PRIVILEGE, Privilege.class);

        // add the key generator for the system id
        privilegeEntity.addKeyGeneratorData(new DefaultKeyGeneratorData(ENT_PRIVILEGE, FLD_ID));

        // create the fields and add them to the entity
        privilegeEntity.addField(new FieldImpl(FLD_ID, Field.FT_INTEGER, Field.FF_SYSTEM_KEY));
        privilegeEntity.addField(new FieldImpl(FLD_GROUPID, Field.FT_INTEGER, Field.FF_UNIQUE_KEY | Field.FF_NAMING));
        privilegeEntity.addField(new FieldImpl(FLD_FEATUREID, Field.FT_STRING, Field.FF_UNIQUE_KEY | Field.FF_NAMING, 50));

        // create the joins and add them to the entity
        privilegeEntity.addJoin(new JoinImpl(ENT_PRIVILEGE, FLD_GROUPID, Group.ENT_GROUP, Group.FLD_ID));
        privilegeEntity.addJoin(new JoinImpl(ENT_PRIVILEGE, FLD_FEATUREID, Feature.ENT_FEATURE, Feature.FLD_NAME));

        // return the entity
        return privilegeEntity;
    }

    //--------------------------------------------------------------------------
    /**
     * Return the Entity name.
     * @return the entity name.
     */
    public String getEntityName()
    {
        return ENT_PRIVILEGE;
    }

    //--------------------------------------------------------------------------
    /**
     * Override, as we have a composite name.
     * @return the group name.
     */
    public String toString()
    {
        return String.valueOf(getGroupId()) + ", " + getFeatureId();
    }

    //--------------------------------------------------------------------------
    /**
     * Get the id of the group.
     * @return the id of the group.
     */
    public int getGroupId()
    {
        return getIntValue(FLD_GROUPID);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the group id.
     * @param groupId, the id of the group.
     */
    public void setGroupId(int groupId)
    {
        setValue(FLD_GROUPID, groupId);
    }

    //--------------------------------------------------------------------------
    /**
     * Get the id of the feature.
     * @return the id of the feature
     */
    public String getFeatureId()
    {
        return getStringValue(FLD_FEATUREID);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the feature id.
     * @param featureId, the feature id.
     * @throws IllegalArgumentException if the feature id is null or empty.
     */
    public void setFeatureId(String featureId) throws IllegalArgumentException
    {
        // check parameter, null or empty feature ids  are not permitted
        Util.argCheckEmpty(featureId);
        setValue(FLD_FEATUREID, featureId);
    }
}

//==============================================================================
// end of file Privilege.java
//==============================================================================
