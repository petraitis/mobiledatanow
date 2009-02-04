//Source file: ..wsl\\mdn\\mdnim\\IMConnection.java
package wsl.mdn.mdnim;

import java.util.Date;

import wsl.fw.util.Util;
import wsl.fw.datasource.*;
import wsl.mdn.dataview.QueryDobj;

//------------------------------------------------------------------------------
/**
 * Class to represent an IMConnection.
 */
public class IMConnection extends DataObject
{
    // version tag
    private final static String _ident = "$Date: 2006/12/9 12:26:09 $  $Revision: 1.2 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/mdnim/IMConnection.java $ ";

    //--------------------------------------------------------------------------
    // constants
	
    // the entity name
    public final static String ENT_IM_CONNECTION = "TBL_IM_CONNECTION";

    // field names
    public final static String FLD_ID       		 = "FLD_ID";
    public final static String FLD_NAME     		 = "FLD_NAME";
    public final static String FLD_USER_NAME     	 = "FLD_USER_NAME";
    public final static String FLD_PASSWORD 		 = "FLD_PASSWORD";
    public final static String FLD_TYPE      	     = "FLD_TYPE";
    public final static String FLD_STATUS            = "FLD_STATUS";
    public final static String FLD_STATUS_DATETIME   = "FLD_STATUS_DESC";    
    public final static String FLD_PROJECT_ID        = "FLD_PROJECT_ID";
    
    public final static String FLD_DEL_STATUS   	 = "FLD_DEL_STATUS";
    
    public final static String DELIM_AND            = "\f";
    public final static String DELIM_OPS            = "\t";

    //--------------------------------------------------------------------------
    /**
     * Default constructor. Since an IMConnection is invalid if it is not correctly initialized
     * ensure that setter methods are called when using this constructor/
     */
    public IMConnection()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Static factory method to create the entity to be used by this dataobject
     * and any subclasses. This is called by the DataManager's factory when
     * creating a IM_CONNECTION entity.
     * @return the created entity.
     */
    public static Entity createEntity()
    {
        // create the IM connection entity
        //Entity imConnectionEntity = new EntityImpl(ENT_IM_CONNECTION, IMConnection.class);
        Entity imConnectionEntity = new EntityImpl(ENT_IM_CONNECTION, IMConnection.class);

        // add the key generator for the system id
        imConnectionEntity.addKeyGeneratorData(new DefaultKeyGeneratorData(ENT_IM_CONNECTION, FLD_ID));

        // create the fields and add them to the entity
        imConnectionEntity.addField(new FieldImpl(FLD_ID, Field.FT_INTEGER, Field.FF_UNIQUE_KEY | Field.FF_SYSTEM_KEY));
        imConnectionEntity.addField(new FieldImpl(FLD_NAME, Field.FT_STRING, Field.FF_NAMING));
        imConnectionEntity.addField(new FieldImpl(FLD_USER_NAME, Field.FT_STRING, Field.FF_NONE));        
        imConnectionEntity.addField(new FieldImpl(FLD_PASSWORD, Field.FT_STRING, Field.FF_NONE, 1024));
        imConnectionEntity.addField(new FieldImpl(FLD_TYPE, Field.FT_INTEGER, Field.FF_NONE));
        imConnectionEntity.addField(new FieldImpl(FLD_STATUS, Field.FT_INTEGER, Field.FF_NONE));//0 value is for disconnection and 1 for connection
        imConnectionEntity.addField(new FieldImpl(FLD_STATUS_DATETIME, Field.FT_DATETIME, Field.FF_NONE, 1024));
        imConnectionEntity.addField(new FieldImpl(FLD_PROJECT_ID, Field.FT_INTEGER));
        
        imConnectionEntity.addField(new FieldImpl(FLD_DEL_STATUS, Field.FT_INTEGER, Field.FF_NONE));
        // return the entity
        return imConnectionEntity;
    }

    //--------------------------------------------------------------------------
    /**
     * Return the Entity name.
     * @return the entity name.
     */
    public String getEntityName()
    {
        return ENT_IM_CONNECTION;
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
     * Set the ID.
     */
    public void setId(int id)
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

    //--------------------------------------------------------------------------
    /**
     * Get the name of the user.
     * @return the name of the user.
     */
    public String getName()
    {
        return getStringValue(FLD_NAME);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the user name for an IM connection.
     * @param userName, the name of the userName in an IM connection.
     * @throws IllegalArgumentException if the user name is null or empty.
     */
    public void setName(String name) throws IllegalArgumentException
    {
        // check parameter, null or empty user names are not permitted
        Util.argCheckEmpty(name);
        setValue(FLD_NAME, name);
    }

    //--------------------------------------------------------------------------
    /**
     * Get the IM connection's userName.
     * @return the userName.
     */
    public String getUserName()
    {
        return getStringValue(FLD_USER_NAME);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the IM connection's userName.
     * @param userName, the userName.
     */
    public void setUserName(String userName)
    {
        setValue(FLD_USER_NAME, userName);
    }
    
    
    //--------------------------------------------------------------------------
    /**
     * Get the IM connection's password.
     * @return the password.
     */
    public String getPassword()
    {
        return getStringValue(FLD_PASSWORD);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the IM connection's password.
     * @param password, the password.
     */
    public void setPassword(String password)
    {
        setValue(FLD_PASSWORD, password);
    }
    
    //--------------------------------------------------------------------------    
    /**
     * @return int the type
     */
    public int getType()
    {
        return getIntValue(FLD_TYPE);
    }
    
    //--------------------------------------------------------------------------
    /**
     * Set the IM Connection Type
     * @param id
     */
    public void setType(int type)
    {
        setValue(FLD_TYPE, type);
    }
    
    //--------------------------------------------------------------------------    
    /**
     * @return int the status
     */
    public int getStatus()
    {
        return getIntValue(FLD_STATUS);
    }
    
    //--------------------------------------------------------------------------
    /**
     * Set the IM Connection Status
     * @param id
     */
    public void setStatus(int status)
    {
        setValue(FLD_STATUS, status);
    }
    //--------------------------------------------------------------------------
    /**
     * Get the IM connection's statusDesc.
     * @return the userName.
     */
    public Date getStatusDesc()
    {
        return getDateValue(FLD_STATUS_DATETIME);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the IM connection's statusDesc.
     * @param dateTime, the dateTime.
     */
    public void setStatusDesc(Date dateTime)
    {
        setValue(FLD_STATUS_DATETIME, dateTime);
    }
    
    //----------------------------------------------------------------------------
    /**
     * @return int the Delete Status
     */
    public int getDelStatus()
    {
        return getIntValue(FLD_DEL_STATUS);
    }
    
    //--------------------------------------------------------------------------
    /**
     * Set the DeleteStatus
     * @param delStatus
     */
    public void setDelStatus(int delStatus)
    {
        setValue(FLD_DEL_STATUS, delStatus);
    }


}
