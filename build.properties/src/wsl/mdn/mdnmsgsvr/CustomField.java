package wsl.mdn.mdnmsgsvr;

import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.DefaultKeyGeneratorData;
import wsl.fw.datasource.Entity;
import wsl.fw.datasource.EntityImpl;
import wsl.fw.datasource.Field;
import wsl.fw.datasource.FieldImpl;

public class CustomField  extends DataObject
{
    // version tag
    private final static String _ident = "$Date: 2006/12/9 12:26:09 $  $Revision: 1.2 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/mdnmsgsvr/CustomField.java $ ";

    //--------------------------------------------------------------------------
    // constants
	
    // the entity name
    public final static String ENT_CUSTOM_FIELD          = "TBL_CUSTOM_FIELD";
    
    public final static String FLD_ID               	 = "FLD_ID";
    public final static String FLD_NAME                  = "FLD_NAME";
    public final static String FLD_CAPITAL_NAME          = "FLD_CAPITAL_NAME";
    
    public CustomField()
    {
    }
    
    public static Entity createEntity()
    {
        // create the Mdn Email entity
        Entity customField = new EntityImpl(ENT_CUSTOM_FIELD, CustomField.class);

        // add the key generator for the system id
        customField.addKeyGeneratorData(new DefaultKeyGeneratorData(ENT_CUSTOM_FIELD, FLD_ID));

        // create the fields and add them to the entity
        customField.addField(new FieldImpl(FLD_ID, Field.FT_INTEGER, Field.FF_UNIQUE_KEY | Field.FF_SYSTEM_KEY));        
        customField.addField(new FieldImpl(FLD_NAME, Field.FT_STRING, Field.FF_NONE, 50));
        customField.addField(new FieldImpl(FLD_CAPITAL_NAME, Field.FT_STRING, Field.FF_NONE, 50));
        
        // return the entity
        return customField;
    }

	public String getEntityName() {
		return ENT_CUSTOM_FIELD;
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
     * @return int the name
     */
    public String getName() {
		return getStringValue(FLD_NAME);
	}
    //  --------------------------------------------------------------------------
    /**
     * Set the name
     * @param name
     */
    public void setName(String name) {
    	setValue(FLD_NAME, name);
	}    
    //  --------------------------------------------------------------------------
    /**
     * @return int the capitalName
     */
    public String getCapitalName() {
		return getStringValue(FLD_CAPITAL_NAME);
	}
    //  --------------------------------------------------------------------------
    /**
     * Set the capitalName
     * @param capitalName
     */
    public void setCapitalName(String capitalName) {
    	setValue(FLD_CAPITAL_NAME, capitalName);
	}        
}
