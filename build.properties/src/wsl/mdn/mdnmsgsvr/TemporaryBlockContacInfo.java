package wsl.mdn.mdnmsgsvr;

import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.DefaultKeyGeneratorData;
import wsl.fw.datasource.Entity;
import wsl.fw.datasource.EntityImpl;
import wsl.fw.datasource.Field;
import wsl.fw.datasource.FieldImpl;

public class TemporaryBlockContacInfo  extends DataObject {
	// version tag
    private final static String _ident = "$Date: 2006/12/9 12:26:09 $  $Revision: 1.2 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/mdnmsgsvr/TemporaryBlockContacInfo.java $ ";

    //--------------------------------------------------------------------------
    // constants
	
    // the entity name
    public final static String ENT_TEMP_BLOCK_INFO   = "TBL_TEMP_BLOCK_INFO";
    
    public final static String FLD_ID               = "FLD_ID";
    public final static String FLD_MAX_MSG 	 	    = "FLD_MAX_MSG";	
    public final static String FLD_MAX_PERIOD 	    = "FLD_MAX_PERIOD";
    public final static String FLD_CANCEL_PERIOD 	= "FLD_CANCEL_PERIOD";
    public final static String FLD_REPLY 			= "FLD_REPLY";
    
    public TemporaryBlockContacInfo()
    {
    }

    public static Entity createEntity()
    {
        // create the Mdn Email entity
        Entity obj = new EntityImpl(ENT_TEMP_BLOCK_INFO, TemporaryBlockContacInfo.class);

        // add the key generator for the system id
        obj.addKeyGeneratorData(new DefaultKeyGeneratorData(ENT_TEMP_BLOCK_INFO, FLD_ID));

        // create the fields and add them to the entity
        obj.addField(new FieldImpl(FLD_ID, Field.FT_INTEGER, Field.FF_UNIQUE_KEY | Field.FF_SYSTEM_KEY));
        
        obj.addField(new FieldImpl(FLD_MAX_MSG, Field.FT_INTEGER));
        obj.addField(new FieldImpl(FLD_MAX_PERIOD, Field.FT_STRING, Field.FF_NONE, 50));
        obj.addField(new FieldImpl(FLD_CANCEL_PERIOD, Field.FT_STRING, Field.FF_NONE, 50));
        obj.addField(new FieldImpl(FLD_REPLY, Field.FT_STRING, Field.FF_NONE, 50));
        
        // return the entity
        return obj;
    }

	public String getEntityName() {
		return ENT_TEMP_BLOCK_INFO;
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
    public int getMaxMessage()
    {
        return getIntValue(FLD_MAX_MSG);
    }
    public void setMaxMessage(int maxMessage)
    {
        setValue(FLD_MAX_MSG, maxMessage);
    }                    
    //  --------------------------------------------------------------------------
    public String getMaxPeriod() {
		return getStringValue(FLD_MAX_PERIOD);
	}

    public void setMaxPeriod(String maxPeriod) {
    	setValue(FLD_MAX_PERIOD, maxPeriod);
	}    
    //  --------------------------------------------------------------------------
    public String getCancelPeriod() {
		return getStringValue(FLD_CANCEL_PERIOD);
	}

    public void setCancelPeriod(String cancelPeriod) {
    	setValue(FLD_CANCEL_PERIOD, cancelPeriod);
	}        
    //  --------------------------------------------------------------------------
    public String getReply() {
		return getStringValue(FLD_REPLY);
	}

    public void setReply(String reply) {
    	setValue(FLD_REPLY, reply);
	}            
}
