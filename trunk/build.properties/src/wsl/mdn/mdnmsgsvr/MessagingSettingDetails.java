package wsl.mdn.mdnmsgsvr;

import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.DefaultKeyGeneratorData;
import wsl.fw.datasource.Entity;
import wsl.fw.datasource.EntityImpl;
import wsl.fw.datasource.Field;
import wsl.fw.datasource.FieldImpl;

public class MessagingSettingDetails  extends DataObject
{
	// version tag
    private final static String _ident = "$Date: 2006/12/9 12:26:09 $  $Revision: 1.2 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/mdnmsgsvr/GuestMsgObj.java $ ";

    //--------------------------------------------------------------------------
    // constants
	
    // the entity name
    public final static String ENT_MSG_SETT_INFO        = "TBL_MSG_SETT_INFO";
    
    public final static String FLD_ID                   = "FLD_ID";
	public final static String FLD_TYPE				    = "FLD_TYPE";
    public final static String FLD_STATUS 			    = "FLD_STATUS";
    public final static String FLD_TOTAL_MSG_COUNT      = "FLD_TOTAL_MSG_COUNT";
    
    //Search Keys
    public final static String FLD_SEARCH_TERM          = "FLD_SEARCH_TERM";
    
    public MessagingSettingDetails()
    {
    }

    public static Entity createEntity()
    {
        // create the Mdn Email entity
        Entity obj = new EntityImpl(ENT_MSG_SETT_INFO, MessagingSettingDetails.class);

        // add the key generator for the system id
        obj.addKeyGeneratorData(new DefaultKeyGeneratorData(ENT_MSG_SETT_INFO, FLD_ID));

        // create the fields and add them to the entity
        obj.addField(new FieldImpl(FLD_ID, Field.FT_INTEGER, Field.FF_UNIQUE_KEY | Field.FF_SYSTEM_KEY));
        obj.addField(new FieldImpl(FLD_STATUS, Field.FT_INTEGER));
        obj.addField(new FieldImpl(FLD_TYPE, Field.FT_STRING, Field.FF_NONE, 50));        
        
        obj.addField(new FieldImpl(FLD_TOTAL_MSG_COUNT, Field.FT_INTEGER));
        
        obj.addField(new FieldImpl(FLD_SEARCH_TERM, Field.FT_STRING, Field.FF_NONE, 100));
        
        // return the entity
        return obj;
    }

	public String getEntityName() {
		return ENT_MSG_SETT_INFO;
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
    //--------------------------------------------------------------------------
    public int getStatus()
    {
        return getIntValue(FLD_STATUS);
    }
    public void setStatus(int status)
    {
        setValue(FLD_STATUS, status);
    }    
    //  --------------------------------------------------------------------------
    public String getType() {
		return getStringValue(FLD_TYPE);
	}

    public void setType(String type) {
    	setValue(FLD_TYPE, type);
	}        
    //--------------------------------------------------------------------------
    public int getTotalMsgCount()
    {
        return getIntValue(FLD_TOTAL_MSG_COUNT);
    }
    public void setTotalMsgCount(int totalMsgCount)
    {
        setValue(FLD_TOTAL_MSG_COUNT, totalMsgCount);
    }        
    //  --------------------------------------------------------------------------
    public String getSearchTerm() {
		return getStringValue(FLD_SEARCH_TERM);
	}

    public void setSearchTerm(String searchTerm) {
    	setValue(FLD_SEARCH_TERM, searchTerm);
	}
    
}
