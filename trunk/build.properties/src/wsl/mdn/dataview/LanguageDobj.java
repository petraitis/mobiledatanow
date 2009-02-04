/**	$Id: JdbcDriver.java,v 1.2 2002/08/06 03:04:30 jonc Exp $
 *
 *	Persistent JdbcDriver description
 *
 */
package wsl.mdn.dataview;

import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.DefaultKeyGeneratorData;
import wsl.fw.datasource.Entity;
import wsl.fw.datasource.EntityImpl;
import wsl.fw.datasource.Field;
import wsl.fw.datasource.FieldImpl;

public class LanguageDobj
	extends DataObject
{
	//--------------------------------------------------------------------------
	// constants

	public final static String
		ENT_LANGUAGE		= "TBL_LANGUAGE",
		FLD_ID				= "FLD_ID",
		FLD_NAME			= "FLD_NAME",
		//FLD_VALUE			= "FLD_VALUE",
		FLD_FILE_NAME		= "FLD_FILE_NAME",
		FLD_DEFAULT			= "FLD_DEFAULT",
		FLD_DEL_STATUS      = "FLD_DEL_STATUS";


	//--------------------------------------------------------------------------
	// construction

	/**
	 * Blank ctor
	 */
	public
	LanguageDobj ()
	{
	}

	//--------------------------------------------------------------------------
	// persistence

	/**
	 * Static factory method to create the entity to be used by this dataobject
	 * and any subclasses. This is called by the DataManager's factory when
	 * creating a LANGUAGE entity.
	 * @return the created entity.
	 */
	public static Entity
	createEntity ()
	{
		// create the entity
		Entity ent = new EntityImpl (ENT_LANGUAGE, LanguageDobj.class);

		// add the key generator for the system id
		ent.addKeyGeneratorData (new DefaultKeyGeneratorData (ENT_LANGUAGE, FLD_ID));

		// create the fields and add them to the entity
		ent.addField (new FieldImpl (FLD_ID, Field.FT_INTEGER, Field.FF_SYSTEM_KEY));//Field.FF_UNIQUE_KEY | 
		ent.addField (new FieldImpl (FLD_NAME, Field.FT_STRING, Field.FF_UNIQUE_KEY | Field.FF_NAMING));//
		//ent.addField (new FieldImpl (FLD_VALUE, Field.FT_STRING));
		ent.addField (new FieldImpl (FLD_FILE_NAME, Field.FT_STRING));
		ent.addField (new FieldImpl (FLD_DEFAULT, Field.FT_INTEGER));
		ent.addField (new FieldImpl (FLD_DEL_STATUS, Field.FT_INTEGER));
		
		// return the entity
		return ent;
	}

	/**
	 * @return String the name of the entity that defines this DataObject
	 */
	public String
	getEntityName ()
	{
		return ENT_LANGUAGE;
	}

	//--------------------------------------------------------------------------
	// accessors

	/**
	 * @return int the id
	 */
	public int
	getId ()
	{
		return getIntValue (FLD_ID);
	}

	/**
	 * Sets the id
	 * @param id the id to set
	 */
	public void
	setId (
	 int id)
	{
		setValue (FLD_ID, id);
	}

	/**
	 * Returns the name
	 * @return String the name
	 */
	public String
	getName ()
	{
		return getStringValue (FLD_NAME);
	}

	/**
	 * Sets the name
	 * @param val the value to set
	 */
	public void
	setName (
	 String val)
	{
		setValue (FLD_NAME, val);
	}


//	/**
//	 * Returns the driver name
//	 * @return String the name
//	 */
//	public String getValue ()
//	{
//		return getStringValue (FLD_VALUE);
//	}
//
//	/**
//	 * Sets the driver name
//	 * @param val the value to set
//	 */
//	public void setValue (String val)
//	{
//		setValue (FLD_VALUE, val);
//	}

	/**
	 * Returns the description of the datatransfer
	 * @return String
	 */
	public String getFileName ()
	{
		return getStringValue (FLD_FILE_NAME);
	}

	/**
	 * Sets the entity description into the datatransfer
	 * @param name
	 * @return void
	 */
	public void setFileName (String name)
	{
		setValue (FLD_FILE_NAME, name);
	}
	
	public int isDefault(){
		return getIntValue(FLD_DEFAULT);		
	}
	
	public void setDefault(int defaultLang){
		setValue(FLD_DEFAULT, defaultLang);
	}
	
	public int getDelStatus() {
		return getIntValue(FLD_DEL_STATUS);
	}

	public void setDelStatus(int delStatus) {
		setValue(FLD_DEL_STATUS, delStatus);
	}		
}
