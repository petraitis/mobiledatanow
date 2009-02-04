package wsl.mdn.mdnmsgsvr;

import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.DefaultKeyGeneratorData;
import wsl.fw.datasource.Entity;
import wsl.fw.datasource.EntityImpl;
import wsl.fw.datasource.Field;
import wsl.fw.datasource.FieldImpl;

public class UserCustomField   extends DataObject
{
    // version tag
    private final static String _ident = "$Date: 2006/12/9 12:26:09 $  $Revision: 1.2 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/mdnmsgsvr/UserCustomField.java $ ";

    //--------------------------------------------------------------------------
    // constants
	
    // the entity name
    public final static String ENT_USER_CUSTOM_FIELD     = "TBL_USER_CUSTOM_FIELD";
    
    public final static String FLD_ID               	 = "FLD_ID";
    public final static String FLD_USER_ID               = "FLD_USER_ID";
    public final static String FLD_CUSTOM_ID             = "FLD_CUSTOM_ID";
    public final static String FLD_CUSTOM_PARAM          = "FLD_CUSTOM_PARAM";
    
    public UserCustomField()
    {
    }
    
    public static Entity createEntity()
    {
        // create the Mdn Email entity
        Entity userCustomField = new EntityImpl(ENT_USER_CUSTOM_FIELD, UserCustomField.class);

        // add the key generator for the system id
        userCustomField.addKeyGeneratorData(new DefaultKeyGeneratorData(ENT_USER_CUSTOM_FIELD, FLD_ID));

        // create the fields and add them to the entity
        userCustomField.addField(new FieldImpl(FLD_ID, Field.FT_INTEGER, Field.FF_UNIQUE_KEY | Field.FF_SYSTEM_KEY));
        userCustomField.addField(new FieldImpl(FLD_USER_ID, Field.FT_INTEGER, Field.FF_NONE));
        userCustomField.addField(new FieldImpl(FLD_CUSTOM_ID, Field.FT_INTEGER, Field.FF_NONE));
        userCustomField.addField(new FieldImpl(FLD_CUSTOM_PARAM, Field.FT_STRING, Field.FF_NONE));
        
        // return the entity
        return userCustomField;
    }

	public String getEntityName() {
		return ENT_USER_CUSTOM_FIELD;
	}

	/**
     * @return the ID
     */
    public int getId()
    {
        return getIntValue(FLD_ID);
    }
	
    //--------------------------------------------------------------------------    
    /**
     * @return int the userId
     */
    public int getUserId()
    {
        return getIntValue(FLD_USER_ID);
    }
    
    //--------------------------------------------------------------------------
    /**
     * Set the userId
     * @param userId
     */
    public void setUserId(int userId)
    {
        setValue(FLD_USER_ID, userId);
    }
    //--------------------------------------------------------------------------    
    /**
     * @return int the customId
     */
    public int getCustomId()
    {
        return getIntValue(FLD_CUSTOM_ID);
    }
    
    //--------------------------------------------------------------------------
    /**
     * Set the custom
     * @param custom
     */
    public void setCustomId(int customId)
    {
        setValue(FLD_CUSTOM_ID, customId);
    }

	//  --------------------------------------------------------------------------
    /**
     * @return int the parameter
     */
    public String getParameter() {
		return getStringValue(FLD_CUSTOM_PARAM);
	}
    //  --------------------------------------------------------------------------
    /**
     * Set the parameter
     * @param parameter
     */
    public void setParameter(String parameter) {
    	setValue(FLD_CUSTOM_PARAM, parameter);
	}    
    
}
