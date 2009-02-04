//==============================================================================
// Group.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.security;

import wsl.fw.util.Util;
import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.DataSource;
import wsl.fw.datasource.DataSourceException;
import wsl.fw.datasource.RecordSet;
import wsl.fw.datasource.Query;
import wsl.fw.datasource.QueryCriterium;
import wsl.fw.datasource.Entity;
import wsl.fw.datasource.Field;
import wsl.fw.datasource.DefaultKeyGeneratorData;
import wsl.fw.datasource.DataManager;
import wsl.fw.datasource.EntityImpl;
import wsl.fw.datasource.FieldImpl;
import wsl.fw.datasource.JoinImpl;
import java.util.Vector;

//------------------------------------------------------------------------------
/**
 * Class to represent a group of users. Groups have privileges to access Features.
 */
public class Group extends DataObject
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/security/Group.java $ ";

    // the entity name
    public final static String ENT_GROUP = "FW_GROUP";

    // field names
    public final static String FLD_ID          = "FLD_ID";
    public final static String FLD_NAME        = "FLD_NAME";
    public final static String FLD_GUEST        = "FLD_GUEST";//0 = Group isn't guest and If 1 = Group is a guest group
    public final static String FLD_DESCRIPTION = "FLD_DESCRIPTION";
    public final static String FLD_STATUS_DEL  = "FLD_STATUS_DEL";
    public final static String FLD_PROJECT_ID  = "FLD_PROJECT_ID";
    //--------------------------------------------------------------------------
    /**
     * Default constructor. Intended for use by the datasource when creating
     * instances. Since a Group is invalid if it is not correctly initialized
     * ensure that setName is called when using this constructor/
     */
    public Group()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Constructor, set the name and description of the group.
     * @param groupName, the name of this group.
     * @param groupDescription, the description of this group.
     * @throws IllegalArgumentException if the parameters are ivalid.
     */
    public Group(int projectId, String groupName, String groupDescription)
        throws IllegalArgumentException
    {
        // save group name and description
        setProjectId(projectId);
    	setName(groupName);
        setDescription(groupDescription);
    }

    //--------------------------------------------------------------------------
    /**
     * Static factory method to create the entity to be used by this dataobject
     * and any subclasses. This is called by the DataManager's factory when
     * creating a GROUP entity.
     * @return the created entity.
     */
    public static Entity createEntity()
    {
        // create the group entity
        Entity groupEntity = new EntityImpl(ENT_GROUP, Group.class);

        // add the key generator for the system id
        groupEntity.addKeyGeneratorData(new DefaultKeyGeneratorData(ENT_GROUP, FLD_ID));

        // create the fields and add them to the entity
        groupEntity.addField(new FieldImpl(FLD_ID, Field.FT_INTEGER, Field.FF_UNIQUE_KEY | Field.FF_SYSTEM_KEY));
        groupEntity.addField(new FieldImpl(FLD_NAME, Field.FT_STRING, Field.FF_NAMING));
        groupEntity.addField(new FieldImpl(FLD_GUEST, Field.FT_INTEGER));
//        groupEntity.addField(new FieldImpl(FLD_ID, Field.FT_INTEGER, Field.FF_SYSTEM_KEY));
//        groupEntity.addField(new FieldImpl(FLD_NAME, Field.FT_STRING, Field.FF_UNIQUE_KEY | Field.FF_NAMING, 50));
        groupEntity.addField(new FieldImpl(FLD_DESCRIPTION, Field.FT_STRING, Field.FF_NONE, 200));
        groupEntity.addField(new FieldImpl(FLD_STATUS_DEL, Field.FT_STRING, Field.FF_NONE, 200));
        groupEntity.addField(new FieldImpl(FLD_PROJECT_ID, Field.FT_INTEGER));
        // return the entity
        return groupEntity;
    }

    //--------------------------------------------------------------------------
    /**
     * Return the Entity name.
     * @return the entity name.
     */
    public String getEntityName()
    {
        return ENT_GROUP;
    }

    //--------------------------------------------------------------------------
    /**
     * @return the ID
     */
    public int getId()
    {
        return getIntValue(FLD_ID);
    }

    //--------------------------------------------------------------------------
    /**
     * @return the ID and an int
     */
    public int getIntId()
    {
        return getIntValue(FLD_ID);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the ID.
     */
    public void setId(Object id)
    {
        setValue(FLD_ID, id);
    }
    //  --------------------------------------------------------------------------
 	/**
 	 * @return int the id of this project
 	 */
 	public int
 	getProjectId ()
 	{
 		return getIntValue (FLD_PROJECT_ID);
 	}
 	//	--------------------------------------------------------------------------
 	/**
 	 * Set the id of this project
 	 * @param id
 	 */
 	public void
 	setProjectId (
 	 int id)
 	{
 		setValue (FLD_PROJECT_ID, id);
 	}   
    //  --------------------------------------------------------------------------
 	/**
 	 * @return int the is Guest or not
 	 */
 	public int
 	getGuest ()
 	{
 		return getIntValue (FLD_GUEST);
 	}
 	//	--------------------------------------------------------------------------
 	/**
 	 * Set the guest
 	 * @param guest
 	 */
 	public void
 	setGuest (
 	 int guest)
 	{
 		setValue (FLD_GUEST, guest);
 	} 	
    //--------------------------------------------------------------------------
    /**
     * Get the name of the group.
     * @return the name of the group.
     */
    public String getName()
    {
        return getStringValue(FLD_NAME);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the group name.
     * @param groupName, the name of the group.
     * @throws IllegalArgumentException if the group name is null or empty.
     */
    public void setName(String groupName) throws IllegalArgumentException
    {
        // check parameter, null or empty group names are not permitted
        Util.argCheckEmpty(groupName);
        setValue(FLD_NAME, groupName);
    }

    //--------------------------------------------------------------------------
    /**
     * Get the description of the group.
     * @return the description of the group.
     */
    public String getDescription()
    {
        return getStringValue(FLD_DESCRIPTION);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the description.
     * @param groupDescription, the description.
     * @throws IllegalArgumentException if the group description is null.
     */
    public void setDescription(String groupDescription)
    {
        // check parameter, null descriptions are not permitted
        //Util.argCheckNull(groupDescription);
        setValue(FLD_DESCRIPTION, groupDescription);
    }

    //--------------------------------------------------------------------------
    /**
     * Get the description of the group.
     * @return the description of the group.
     */
    public String getDelStatus()
    {
        return getStringValue(FLD_STATUS_DEL);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the description.
     * @param groupDescription, the description.
     * @throws IllegalArgumentException if the group description is null.
     */
    public void setDelStatus(String delStatus)
    {
        setValue(FLD_STATUS_DEL, delStatus);
    }
    
    //--------------------------------------------------------------------------
    /**
     * Delete this group from the DataSource. Overidden to remove all the
     * dependant group memberships and privileges.
     * @throws DataSourceException if there is an error from the DataSource.
     */
    public void delete() throws DataSourceException
    {
        // perform query to get the memberships
        DataSource ds = DataManager.getDataSource(GroupMembership.ENT_GROUPMEMBERSHIP);
        Query query = new Query(new QueryCriterium(GroupMembership.ENT_GROUPMEMBERSHIP,
            GroupMembership.FLD_GROUPID, QueryCriterium.OP_EQUALS, getId()));
        RecordSet rs = ds.select(query);

        // iterate over the memberships removing them
        while (rs.next())
            rs.getCurrentObject().delete();

        // perform a query to get all the Privileges
        ds = DataManager.getDataSource(Privilege.ENT_PRIVILEGE);
        query = new Query(new QueryCriterium(Privilege.ENT_PRIVILEGE,
            Privilege.FLD_GROUPID, QueryCriterium.OP_EQUALS, getId()));
        rs = ds.select(query);

        // iterate over the privileges removing them
        while (rs.next())
            rs.getCurrentObject().delete();

        // call base class
        super.delete();
    }
}

//==============================================================================
// end of file Group.java
//==============================================================================
