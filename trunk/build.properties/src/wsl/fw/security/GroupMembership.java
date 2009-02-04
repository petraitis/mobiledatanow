//==============================================================================
// GroupMembership.java
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
 * Class to represent the GroupMembership relationship between User and Group.
 */
public class GroupMembership extends DataObject
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/security/GroupMembership.java $ ";

    // the entity name
    public final static String ENT_GROUPMEMBERSHIP = "FW_GROUPMEMBERSHIP";

    // field names
    public final static String FLD_ID      = "FLD_ID";
    public final static String FLD_GROUPID = "FLD_GROUPID";
    public final static String FLD_USERID  = "FLD_USERID";
    public final static String FLD_PROJECT_ID  = "FLD_PROJECT_ID";

    //--------------------------------------------------------------------------
    /**
     * Default constructor. Intended for use by the datasource when creating
     * instances. Since a GroupMembership is invalid if it is not correctly initialized
     * ensure that setGroupId and setUserId are called.
     */
    public GroupMembership()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Constructor, set the foreign keys of this GroupMembership.
     * @param groupId, the foreign key of the Group.
     * @param userId, the foreign key of the User.
     */
    public GroupMembership(int groupId, int userId)
    {
        // save group name and description
        setGroupId(groupId);
        setUserId(userId);
    }

    //--------------------------------------------------------------------------
    /**
     * Constructor, set the foreign keys of this GroupMembership.
     * @param group, the Group to reference.
     * @param user, the User to reference.
     * @throws IllegalArgumentException if the parameters are ivalid.
     */
    public GroupMembership(Group group, User user)
        throws IllegalArgumentException
    {
        Util.argCheckNull(group);
        Util.argCheckNull(user);

        // save group name and description
        setGroupId(group.getIntValue(Group.FLD_ID));
        setUserId(user.getIntValue(User.FLD_ID));
    }

    //--------------------------------------------------------------------------
    /**
     * Static factory method to create the entity to be used by this dataobject
     * and any subclasses. This is called by the DataManager's factory when
     * creating a GROUPMEMBERSHIP entity.
     * @return the created entity.
     */
    public static Entity createEntity()
    {
        // create the group entity
        Entity memberEntity = new EntityImpl(ENT_GROUPMEMBERSHIP, GroupMembership.class);

        // add the key generator for the system id
        memberEntity.addKeyGeneratorData(new DefaultKeyGeneratorData(ENT_GROUPMEMBERSHIP, FLD_ID));

        // create the fields and add them to the entity
        Vector fields = new Vector();
        fields.add(new FieldImpl(FLD_ID, Field.FT_INTEGER, Field.FF_UNIQUE_KEY | Field.FF_SYSTEM_KEY));
        fields.add(new FieldImpl(FLD_GROUPID, Field.FT_INTEGER));
        fields.add(new FieldImpl(FLD_USERID, Field.FT_INTEGER));
        fields.add(new FieldImpl(FLD_PROJECT_ID, Field.FT_INTEGER));
        memberEntity.setFields(fields);

        // create the joins and add them to the entity
        Vector joins = new Vector();
        joins.add(new JoinImpl(ENT_GROUPMEMBERSHIP, FLD_GROUPID, Group.ENT_GROUP, Group.FLD_ID));
        joins.add(new JoinImpl(ENT_GROUPMEMBERSHIP, FLD_USERID, User.ENT_USER, User.FLD_ID));
        memberEntity.setJoins(joins);

        // return the entity
        return memberEntity;
    }

    //--------------------------------------------------------------------------
    /**
     * Return the Entity name.
     * @return the entity name.
     */
    public String getEntityName()
    {
        return ENT_GROUPMEMBERSHIP;
    }

    //--------------------------------------------------------------------------
    /**
     * Override, as we have a composite name.
     * @return the group name.
     */
    public String toString()
    {
        return String.valueOf(getGroupId()) + ", " + String.valueOf(getUserId());
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
     * Get the id of the user.
     * @return the id of the user
     */
    public int getUserId()
    {
        return getIntValue(FLD_USERID);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the user id.
     * @param userId, the user id.
     */
    public void setUserId(int userId)
    {
        setValue(FLD_USERID, userId);
    }
    //--------------------------------------------------------------------------
    /**
     * Get the id of the project.
     * @return the id of the project
     */
    public int getProjectId()
    {
        return getIntValue(FLD_PROJECT_ID);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the project id.
     * @param projectId, the project id.
     */
    public void setProjectId(int projectId)
    {
        setValue(FLD_PROJECT_ID, projectId);
    }

}

//==============================================================================
// end of file GroupMembership.java
//==============================================================================
