package wsl.mdn.mdnmsgsvr;

import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.DefaultKeyGeneratorData;
import wsl.fw.datasource.Entity;
import wsl.fw.datasource.EntityImpl;
import wsl.fw.datasource.Field;
import wsl.fw.datasource.FieldImpl;

public class MdnSmsSetting extends DataObject
{
    // version tag
    private final static String _ident = "$Date: 2006/12/9 12:26:09 $  $Revision: 1.2 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/mdnmsgsvr/MdnSmsSetting.java $ ";

    //--------------------------------------------------------------------------
    // constants
	
    // the entity name
    public final static String ENT_SMS = "TBL_SMS_SETTING";

    // field names
    public final static String FLD_ID       		     = "FLD_ID";
    public final static String FLD_SIM_NUMBER     		 = "FLD_SIM_NUMBER";
    public final static String FLD_COMM     	         = "FLD_COMM";
    public final static String FLD_BAUDRATE 		     = "FLD_BAUDRATE";
    public final static String FLD_MODEM_MAN      	     = "FLD_MODEM_MAN";
    public final static String FLD_MODEM_MODEL      	 = "FLD_MODEM_MODEL";  
    public final static String FLD_PROJECT_ID        	 = "FLD_PROJECT_ID";

    //--------------------------------------------------------------------------
    /**
     * Default constructor. Since an MdnSmsSetting is invalid if it is not correctly initialized
     * ensure that setter methods are called when using this constructor/
     */
    public MdnSmsSetting()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Static factory method to create the entity to be used by this dataobject
     * and any subclasses. This is called by the DataManager's factory when
     * creating a ENT_SMS entity.
     * @return the created entity.
     */
    public static Entity createEntity()
    {
        // create the Mdn Sms entity
        Entity mdnSmsSettingEntity = new EntityImpl(ENT_SMS, MdnSmsSetting.class);

        // add the key generator for the system id
        mdnSmsSettingEntity.addKeyGeneratorData(new DefaultKeyGeneratorData(ENT_SMS, FLD_ID));

        // create the fields and add them to the entity
        mdnSmsSettingEntity.addField(new FieldImpl(FLD_ID, Field.FT_INTEGER, Field.FF_UNIQUE_KEY | Field.FF_SYSTEM_KEY));
        mdnSmsSettingEntity.addField(new FieldImpl(FLD_SIM_NUMBER, Field.FT_STRING, Field.FF_NONE));
        mdnSmsSettingEntity.addField(new FieldImpl(FLD_COMM, Field.FT_STRING, Field.FF_NONE));        
        mdnSmsSettingEntity.addField(new FieldImpl(FLD_BAUDRATE, Field.FT_STRING, Field.FF_NONE));        
        mdnSmsSettingEntity.addField(new FieldImpl(FLD_MODEM_MAN, Field.FT_STRING, Field.FF_NONE));        
        mdnSmsSettingEntity.addField(new FieldImpl(FLD_MODEM_MODEL, Field.FT_STRING, Field.FF_NONE));        
        mdnSmsSettingEntity.addField(new FieldImpl(FLD_PROJECT_ID, Field.FT_INTEGER));
        // return the entity
        return mdnSmsSettingEntity;
    }

    //--------------------------------------------------------------------------
    /**
     * Return the Entity name.
     * @return the entity name.
     */
    public String getEntityName()
    {
        return ENT_SMS;
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
     * Get the Sim Number.
     * @return the number.
     */
    public String getNumber()
    {
        return getStringValue(FLD_SIM_NUMBER);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the Sim Number.
     * @param number
     */
    public void setNumber(String number)
    {
        setValue(FLD_SIM_NUMBER, number);
    }

    //--------------------------------------------------------------------------
    /**
     * Get the Comm port
     * @return the comm.
     */
    public String getComm()
    {
        return getStringValue(FLD_COMM);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the Comm port.
     * @param comm
     */
    public void setComm(String comm)
    {
        setValue(FLD_COMM, comm);
    }

    //--------------------------------------------------------------------------
    /**
     * Get the baudrate.
     * @return the baudrate.
     */
    public String getBaudrate()
    {
        return getStringValue(FLD_BAUDRATE);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the baudrate.
     * @param baudrate
     */
    public void setBaudrate(String baudrate)
    {
        setValue(FLD_BAUDRATE, baudrate);
    }

    //--------------------------------------------------------------------------
    /**
     * Get the Modem Manufacturer.
     * @return the modemMan.
     */
    public String getModemManufacturer()
    {
        return getStringValue(FLD_MODEM_MAN);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the Modem Manufacturer.
     * @param modemMan
     */
    public void setModemManufacturer(String modemMan)
    {
        setValue(FLD_MODEM_MAN, modemMan);
    }

    //--------------------------------------------------------------------------
    /**
     * Get the Modem Model.
     * @return the modemModel.
     */
    public String getmodemModel()
    {
        return getStringValue(FLD_MODEM_MODEL);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the Modem Model.
     * @param modemModel
     */
    public void setModemModel(String modemModel)
    {
        setValue(FLD_MODEM_MODEL, modemModel);
    }

}


