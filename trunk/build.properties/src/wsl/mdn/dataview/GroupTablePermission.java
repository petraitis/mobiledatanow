package wsl.mdn.dataview;

import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.DefaultKeyGeneratorData;
import wsl.fw.datasource.Entity;
import wsl.fw.datasource.EntityImpl;
import wsl.fw.datasource.Field;
import wsl.fw.datasource.FieldImpl;

public class GroupTablePermission extends DataObject { 
	// version tag
	private final static String _ident = "$Date: 2008/11/05 23:35:35 $  $Revision: 2.0.0.0 $ "
	    + "$Archive: /Mobile Data Now/Source/wsl/mdn/dataview/GroupTablePermission.java $ ";
	
	//--------------------------------------------------------------------------
	// constants
	public final static String ENT_GROUP_ENTITY_PERMISSION  = "TBL_GROUP_ENTITY_PERMISSION";
	
	public final static String FLD_ID               		= "FLD_ID";
	public final static String FLD_GROUPID          		= "FLD_GROUPID";
	public final static String FLD_ENTITY_ID       			= "FLD_ENTITY_ID";
	
	public GroupTablePermission()
	{
	}
	
	/**
	 * @return String the name of the entity that defines this DataObject
	 */
	public String getEntityName()
	{
	    return ENT_GROUP_ENTITY_PERMISSION;
	}
	
	//--------------------------------------------------------------------------
	// persistence
	
	/**
	 * Static factory method to create the entity to be used by this dataobject
	 * and any subclasses. This is called by the DataManager's factory when
	 * creating a GroupTablePermission entity.
	 * @return the created entity.
	 */
	public static Entity createEntity()
	{
	    // create the entity
	    Entity ent = new EntityImpl(ENT_GROUP_ENTITY_PERMISSION, GroupTablePermission.class);
	
	    // add the key generator for the system id
	    ent.addKeyGeneratorData(new DefaultKeyGeneratorData(ENT_GROUP_ENTITY_PERMISSION, FLD_ID));
	
	    // create the fields and add them to the entity
	    ent.addField(new FieldImpl(FLD_ID, Field.FT_INTEGER, Field.FF_UNIQUE_KEY | Field.FF_SYSTEM_KEY));
	    ent.addField(new FieldImpl(FLD_GROUPID, Field.FT_INTEGER, Field.FF_NONE));
	    ent.addField(new FieldImpl(FLD_ENTITY_ID, Field.FT_INTEGER, Field.FF_NONE));
	
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
	 * Returns the entity id
	 * @return int
	 */
	public int getEntityId()
	{
	    return getIntValue(FLD_ENTITY_ID);
	}
	
	/**
	 * Set the entity id
	 * @param entityId the value to set
	 */
	public void setEntityId(int entityId)
	{
	    setValue(FLD_ENTITY_ID, entityId);
	}
}
