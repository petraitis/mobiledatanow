package wsl.mdn.mdnmsgsvr;

import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.DefaultKeyGeneratorData;
import wsl.fw.datasource.Entity;
import wsl.fw.datasource.EntityImpl;
import wsl.fw.datasource.Field;
import wsl.fw.datasource.FieldImpl;

public class BlockContacts extends DataObject {
	// version tag
    private final static String _ident = "$Date: 2006/12/9 12:26:09 $  $Revision: 1.2 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/mdnmsgsvr/BlockContacts.java $ ";

    //--------------------------------------------------------------------------
    // constants
	
    // the entity name
    public final static String ENT_BLOCK_CONTACTS   = "TBL_BLOCK_CONTACTS";
    
    public final static String FLD_ID               = "FLD_ID";
	public final static String FLD_TYPE				= "FLD_TYPE";
    public final static String FLD_CONTACT 			= "FLD_CONTACT";
    
    public BlockContacts(){
    }

    public static Entity createEntity()
    {
        Entity obj = new EntityImpl(ENT_BLOCK_CONTACTS, BlockContacts.class);

        // add the key generator for the system id
        obj.addKeyGeneratorData(new DefaultKeyGeneratorData(ENT_BLOCK_CONTACTS, FLD_ID));

        // create the fields and add them to the entity
        obj.addField(new FieldImpl(FLD_ID, Field.FT_INTEGER, Field.FF_UNIQUE_KEY | Field.FF_SYSTEM_KEY));
        obj.addField(new FieldImpl(FLD_TYPE, Field.FT_STRING, Field.FF_NONE, 50));        
        
        obj.addField(new FieldImpl(FLD_CONTACT, Field.FT_STRING, Field.FF_NONE, 50));
        
        // return the entity
        return obj;
    }

	public String getEntityName() {
		return ENT_BLOCK_CONTACTS;
	}    
    //--------------------------------------------------------------------------
    /**
     * @return the ID
     */
    public int getId()
    {
        return getIntValue(FLD_ID);
    }
    public void setId(int id)
    {
        setValue(FLD_ID, id);
    }
    //  --------------------------------------------------------------------------
    public String getType() {
		return getStringValue(FLD_TYPE);
	}

    public void setType(String type) {
    	setValue(FLD_TYPE, type);
	}        
    //  --------------------------------------------------------------------------
    public String getContact() {
		return getStringValue(FLD_CONTACT);
	}

    public void setContact(String contact) {
    	setValue(FLD_CONTACT, contact);
	}        

}
