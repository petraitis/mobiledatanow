package wsl.mdn.mdnmsgsvr;

import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.DefaultKeyGeneratorData;
import wsl.fw.datasource.Entity;
import wsl.fw.datasource.EntityImpl;
import wsl.fw.datasource.Field;
import wsl.fw.datasource.FieldImpl;

public class MdnSmpp  extends DataObject
{
    // version tag
    private final static String _ident = "$Date: 2006/12/9 12:26:09 $  $Revision: 1.2 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/mdnmsgsvr/MdnSmpp.java $ ";

    //--------------------------------------------------------------------------
    // constants
	
    // the entity name
    public final static String ENT_SMPP = "TBL_SMPP";

    // field names
    public final static String FLD_ID       		     = "FLD_ID";
    public final static String FLD_NUMBER	     		 = "FLD_NUMBER";
    public final static String FLD_HOST     	         = "FLD_HOST";
    public final static String FLD_PORT		 		     = "FLD_PORT";
    public final static String FLD_USERNAME      	     = "FLD_USERNAME";
    public final static String FLD_PASSWORD		      	 = "FLD_PASSWORD";  
    public final static String FLD_SOURCE_NPI		     = "FLD_SOURCE_NPI";
    public final static String FLD_SOURCE_TON		     = "FLD_SOURCE_TON";
    public final static String FLD_DEST_NPI		         = "FLD_DEST_NPI";
    public final static String FLD_DEST_TON		         = "FLD_DEST_TON";
    public final static String FLD_BIND_NPI		         = "FLD_BIND_NPI";
    public final static String FLD_BIND_TON		         = "FLD_BIND_TON";    
    public final static String FLD_TYPE		        	 = "FLD_TYPE";
    public final static String FLD_INTERVAL			     = "FLD_INTERVAL";
    public final static String FLD_USE_TLV			     = "FLD_USE_TLV";
    public final static String FLD_USE_ADDRESS_RANGE	 = "FLD_USE_ADDRESS_RANGE";
    public final static String FLD_DEL_STATUS   		 = "FLD_DEL_STATUS";
    

    //--------------------------------------------------------------------------
    /**
     * Default constructor. Since an MdnSmpp is invalid if it is not correctly initialized
     * ensure that setter methods are called when using this constructor/
     */
    public MdnSmpp()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Static factory method to create the entity to be used by this dataobject
     * and any subclasses. This is called by the DataManager's factory when
     * creating a ENT_SMPP entity.
     * @return the created entity.
     */
    public static Entity createEntity()
    {
        // create the Mdn Sms entity
        Entity mdnSmppEntity = new EntityImpl(ENT_SMPP, MdnSmpp.class);

        // add the key generator for the system id
        mdnSmppEntity.addKeyGeneratorData(new DefaultKeyGeneratorData(ENT_SMPP, FLD_ID));

        // create the fields and add them to the entity
        mdnSmppEntity.addField(new FieldImpl(FLD_ID, Field.FT_INTEGER, Field.FF_UNIQUE_KEY | Field.FF_SYSTEM_KEY));
        mdnSmppEntity.addField(new FieldImpl(FLD_NUMBER, Field.FT_STRING, Field.FF_NONE));
        mdnSmppEntity.addField(new FieldImpl(FLD_HOST, Field.FT_STRING, Field.FF_NONE));        
        mdnSmppEntity.addField(new FieldImpl(FLD_PORT, Field.FT_STRING, Field.FF_NONE));        
        mdnSmppEntity.addField(new FieldImpl(FLD_USERNAME, Field.FT_STRING, Field.FF_NONE));        
        mdnSmppEntity.addField(new FieldImpl(FLD_PASSWORD, Field.FT_STRING, Field.FF_NONE));        
        mdnSmppEntity.addField(new FieldImpl(FLD_SOURCE_NPI,  Field.FT_STRING, Field.FF_NONE));
        mdnSmppEntity.addField(new FieldImpl(FLD_SOURCE_TON,  Field.FT_STRING, Field.FF_NONE));
        mdnSmppEntity.addField(new FieldImpl(FLD_DEST_NPI,  Field.FT_STRING, Field.FF_NONE));
        mdnSmppEntity.addField(new FieldImpl(FLD_DEST_TON,  Field.FT_STRING, Field.FF_NONE));        
        mdnSmppEntity.addField(new FieldImpl(FLD_BIND_NPI,  Field.FT_STRING, Field.FF_NONE));
        mdnSmppEntity.addField(new FieldImpl(FLD_BIND_TON,  Field.FT_STRING, Field.FF_NONE));                
        mdnSmppEntity.addField(new FieldImpl(FLD_INTERVAL,  Field.FT_INTEGER));
        mdnSmppEntity.addField(new FieldImpl(FLD_USE_TLV,  Field.FT_INTEGER));
        mdnSmppEntity.addField(new FieldImpl(FLD_USE_ADDRESS_RANGE,  Field.FT_INTEGER));
        mdnSmppEntity.addField(new FieldImpl(FLD_DEL_STATUS, Field.FT_INTEGER, Field.FF_NONE));
        
        // return the entity
        return mdnSmppEntity;
    }

    //--------------------------------------------------------------------------
    /**
     * Return the Entity name.
     * @return the entity name.
     */
    public String getEntityName()
    {
        return ENT_SMPP;
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

    //--------------------------------------------------------------------------
    /**
     * Get the Number.
     * @return the number.
     */
    public String getNumber()
    {
        return getStringValue(FLD_NUMBER);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the Number.
     * @param number
     */
    public void setNumber(String number)
    {
        setValue(FLD_NUMBER, number);
    }

    //--------------------------------------------------------------------------
    /**
     * Get the host
     * @return the host
     */
    public String getHost()
    {
        return getStringValue(FLD_HOST);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the host.
     * @param host
     */
    public void setHost(String host)
    {
        setValue(FLD_HOST, host);
    }

    //--------------------------------------------------------------------------
    /**
     * Get the port.
     * @return the port.
     */
    public String getPort()
    {
        return getStringValue(FLD_PORT);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the port.
     * @param port
     */
    public void setPort(String port)
    {
        setValue(FLD_PORT, port);
    }

    //--------------------------------------------------------------------------
    /**
     * Get the username.
     * @return the username.
     */
    public String getUsername()
    {
        return getStringValue(FLD_USERNAME);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the username.
     * @param username
     */
    public void setUsername(String username)
    {
        setValue(FLD_USERNAME, username);
    }

    //--------------------------------------------------------------------------
    /**
     * Get the Password.
     * @return the Password.
     */
    public String getPassword()
    {
        return getStringValue(FLD_PASSWORD);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the Password.
     * @param Password
     */
    public void setPassword(String password)
    {
        setValue(FLD_PASSWORD, password);
    }
    //--------------------------------------------------------------------------
    /**
     * Get the sourceNPI.
     * @return the sourceNPI.
     */
    public String getSourceNPI()
    {
        return getStringValue(FLD_SOURCE_NPI);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the sourceNpi.
     * @param sourceNpi
     */
    public void setSourceNPI(String sourceNPI)
    {
        setValue(FLD_SOURCE_NPI, sourceNPI);
    }
    //--------------------------------------------------------------------------
    /**
     * Get the sourceTON.
     * @return sourceTON.
     */
    public String getSourceTON()
    {
        return getStringValue(FLD_SOURCE_TON);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the sourceTON.
     * @param sourceTON
     */
    public void setSourceTON(String sourceTON)
    {
        setValue(FLD_SOURCE_TON, sourceTON);
    }
    //--------------------------------------------------------------------------
    /**
     * Get the destNPI.
     * @return the destNPI.
     */
    public String getDestNPI()
    {
        return getStringValue(FLD_DEST_NPI);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the destNPI.
     * @param destNPI
     */
    public void setDestNPI(String destNPI)
    {
        setValue(FLD_DEST_NPI, destNPI);
    }
    //--------------------------------------------------------------------------
    /**
     * Get the destTON.
     * @return destTON.
     */
    public String getDestTON()
    {
        return getStringValue(FLD_DEST_TON);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the destTON.
     * @param destTON
     */
    public void setDestTON(String destTON)
    {
        setValue(FLD_DEST_TON, destTON);
    }
    //--------------------------------------------------------------------------
    /**
     * Get the bindNPI.
     * @return the bindNPI.
     */
    public String getBindNPI()
    {
        return getStringValue(FLD_BIND_NPI);
    }
    //--------------------------------------------------------------------------
    /**
     * Set the bindNPI.
     * @param bindNPI
     */
    public void setBindNPI(String bindNPI)
    {
        setValue(FLD_BIND_NPI, bindNPI);
    }
    //--------------------------------------------------------------------------
    /**
     * Get the bindTON.
     * @return bindTON.
     */
    public String getBindTON()
    {
        return getStringValue(FLD_BIND_TON);
    }
    //--------------------------------------------------------------------------
    /**
     * Set the bindTON.
     * @param bindTON
     */
    public void setBindTON(String bindTON)
    {
        setValue(FLD_BIND_TON, bindTON);
    }
    //--------------------------------------------------------------------------
    /**
     * Get the Type.
     * @return Type.
     */
    public String getType()
    {
        return getStringValue(FLD_TYPE);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the Type.
     * @param Type
     */
    public void setType(String type)
    {
        setValue(FLD_TYPE, type);
    }    
    //--------------------------------------------------------------------------
    /**
     * Get the interval.
     * @return interval.
     */
    public int getInterval()
    {
        return getIntValue(FLD_INTERVAL);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the interval.
     * @param interval
     */
    public void setInterval(int interval)
    {
        setValue(FLD_INTERVAL, interval);
    }
    //--------------------------------------------------------------------------
    /**
     * Get the UseTlv.
     * @return UseTlv.
     */
    public int getUseTlv()
    {
        return getIntValue(FLD_USE_TLV);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the UseTlv.
     * @param useTlv
     */
    public void setUseTlv(int useTlv)
    {
        setValue(FLD_USE_TLV, useTlv);
    }
    //--------------------------------------------------------------------------
    /**
     * Get the UseAddrRange.
     * @return useAddrRange.
     */
    public int getUseAddrRange()
    {
        return getIntValue(FLD_USE_ADDRESS_RANGE);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the UseAddrRange.
     * @param useAddrRange
     */
    public void setUseAddrRange(int useAddrRange)
    {
        setValue(FLD_USE_ADDRESS_RANGE, useAddrRange);
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



