package wsl.mdn.mdnim;

import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.DefaultKeyGeneratorData;
import wsl.fw.datasource.Entity;
import wsl.fw.datasource.EntityImpl;
import wsl.fw.datasource.Field;
import wsl.fw.datasource.FieldImpl;
import wsl.fw.util.Util;

//------------------------------------------------------------------------------
/**
 * Class to represent an IM Contact
 */
public class IMContact  extends DataObject{
    // version tag
    private final static String _ident = "$Date: 2006/01/25 10:08:00 $  $Revision: 1.2 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/mdnim/IMContact.java $ ";

    //--------------------------------------------------------------------------
    // constants
	
    // the entity name
    public final static String ENT_IM_CONTACT = "TBL_IM_CONTACT";

    // field names
    public final static String FLD_ID       		        = "FLD_ID";
    public final static String FLD_IM_CONTACT_NAME     	    = "FLD_IM_CONTACT_NAME";
    public final static String FLD_MDN_USER_ID 	            = "FLD_MDN_USER_ID";
    public final static String FLD_IM_MDN_CONNECTION_TYPE 	= "FLD_IM_MDN_CONNECTION_TYPE";
    public final static String FLD_PROJECT_ID        	    = "FLD_PROJECT_ID";
    //--------------------------------------------------------------------------
    /**
     * Default constructor. Since an IMContact is invalid if it is not correctly initialized
     * ensure that setter methods are called when using this constructor/
     */
    public IMContact()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Static factory method to create the entity to be used by this dataobject
     * and any subclasses. This is called by the DataManager's factory when
     * creating a ENT_IM_CONTACT entity.
     * @return the created entity.
     */
    public static Entity createEntity()
    {
        // create the IMContact entity
        Entity imContactEntity = new EntityImpl(ENT_IM_CONTACT, IMContact.class);

        // add the key generator for the system id
        imContactEntity.addKeyGeneratorData(new DefaultKeyGeneratorData(ENT_IM_CONTACT, FLD_ID));

        // create the fields and add them to the entity
        imContactEntity.addField(new FieldImpl(FLD_ID, Field.FT_INTEGER, Field.FF_UNIQUE_KEY | Field.FF_SYSTEM_KEY));
        imContactEntity.addField(new FieldImpl(FLD_IM_CONTACT_NAME, Field.FT_STRING, Field.FF_NAMING));
        imContactEntity.addField(new FieldImpl(FLD_MDN_USER_ID, Field.FT_INTEGER, Field.FF_NONE));
        imContactEntity.addField(new FieldImpl(FLD_IM_MDN_CONNECTION_TYPE, Field.FT_INTEGER, Field.FF_NONE));
        imContactEntity.addField(new FieldImpl(FLD_PROJECT_ID, Field.FT_INTEGER));
        // return the entity
        return imContactEntity;
    }

    //--------------------------------------------------------------------------
    /**
     * Return the Entity name.
     * @return the entity name.
     */
    public String getEntityName()
    {
        return ENT_IM_CONTACT;
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
     * Get the name of the im contact.It is must be username of the IM
     * @return the name of the im contact.
     */
    public String getName()
    {
        return getStringValue(FLD_IM_CONTACT_NAME);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the Name of the im contact.It is must be username of the IM
     * @param name
     */
    public void setName(String name)
    {
        // check parameter, null or empty user names are not permitted
        Util.argCheckEmpty(name);
        setValue(FLD_IM_CONTACT_NAME, name);
    }

    //--------------------------------------------------------------------------    
    /**
     * @return int the sender userId
     */
    public int getUserId()
    {
        return getIntValue(FLD_MDN_USER_ID);
    }
    
    //--------------------------------------------------------------------------
    /**
     * Set the User Id
     * @param userId
     */
    public void setUserId(int userId)
    {
        setValue(FLD_MDN_USER_ID, userId);
    }

    //--------------------------------------------------------------------------    
    /**
     * @return int the sender imConnectionType
     */
    public int getImConnectionType()
    {
        return getIntValue(FLD_IM_MDN_CONNECTION_TYPE);
    }
    
    //--------------------------------------------------------------------------
    /**
     * Set the imConnectiontype
     * @param imConnectionType
     */
    public void setImConnectionType(int imConnectionId)
    {
        setValue(FLD_IM_MDN_CONNECTION_TYPE, imConnectionId);
    }



}
