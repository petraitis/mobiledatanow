package wsl.mdn.mdnmsgsvr;

import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.DefaultKeyGeneratorData;
import wsl.fw.datasource.Entity;
import wsl.fw.datasource.EntityImpl;
import wsl.fw.datasource.Field;
import wsl.fw.datasource.FieldImpl;

public class MdnMessageSeparator  extends DataObject
{
    // version tag
    private final static String _ident = "$Date: 2006/12/9 12:26:09 $  $Revision: 1.2 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/mdnmsgsvr/MdnMessageSeparator.java $ ";

    //--------------------------------------------------------------------------
    // constants
	
    // the entity name
    public final static String ENT_MSG_SEPARATOR = "TBL_MSG_SEPARATOR";

    // field names
    public final static String FLD_ID       		     = "FLD_ID";
    public final static String FLD_CONDITION_SEPERATOR   = "FLD_CONDITION_SEPERATOR";
    public final static String FLD_PROJECT_ID        	 = "FLD_PROJECT_ID";
    //--------------------------------------------------------------------------
    /**
     * Default constructor. Since an MdnMessageSeparator is invalid if it is not correctly initialized
     * ensure that setter methods are called when using this constructor/
     */
    public MdnMessageSeparator()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Static factory method to create the entity to be used by this dataobject
     * and any subclasses. This is called by the DataManager's factory when
     * creating a ENT_MSG_SEPARATOR entity.
     * @return the created entity.
     */
    public static Entity createEntity()
    {
        // create the Mdn Email entity
        Entity mdnMessageSeparator = new EntityImpl(ENT_MSG_SEPARATOR, MdnMessageSeparator.class);

        // add the key generator for the system id
        mdnMessageSeparator.addKeyGeneratorData(new DefaultKeyGeneratorData(ENT_MSG_SEPARATOR, FLD_ID));

        // create the fields and add them to the entity
        mdnMessageSeparator.addField(new FieldImpl(FLD_ID, Field.FT_INTEGER, Field.FF_UNIQUE_KEY | Field.FF_SYSTEM_KEY));
        mdnMessageSeparator.addField(new FieldImpl(FLD_CONDITION_SEPERATOR, Field.FT_STRING, Field.FF_NONE));        
        mdnMessageSeparator.addField(new FieldImpl(FLD_PROJECT_ID, Field.FT_INTEGER));
        // return the entity
        return mdnMessageSeparator;
    }

    //--------------------------------------------------------------------------
    /**
     * Return the Entity name.
     * @return the entity name.
     */
    public String getEntityName()
    {
        return ENT_MSG_SEPARATOR;
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
     * Get the conditionSeperator
     * @return the conditionSeperator.
     */
     public String getConditionSeperator()
     {
        return getStringValue(FLD_CONDITION_SEPERATOR);
     }

     //--------------------------------------------------------------------------
     /**
     * Set the conditionSeperator.
     * @param conditionSeperator
     */
     public void setConditionSeperator(String conditionSeperator)
     {
             setValue(FLD_CONDITION_SEPERATOR, conditionSeperator);
     }
}
