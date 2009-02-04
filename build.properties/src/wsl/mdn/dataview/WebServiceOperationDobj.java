//Source file: D:\\dev\\wsl\\mdn\\dataview\\EntityDobj.java

package wsl.mdn.dataview;

// imports
import java.util.Vector;
import java.io.IOException;
import java.io.ObjectInputStream;
import wsl.fw.util.Util;
import wsl.fw.datasource.*;

//------------------------------------------------------------------------------
/**
 * DataObject that persists Entities
 */
public class WebServiceOperationDobj extends DataObject
{
    //--------------------------------------------------------------------------
    // constants

    public final static String ENT_WEB_SERVICE_OPERATION      = "TBL_WEB_SERVICE_OPERATION";
    public final static String FLD_ID               = "FLD_ID";
    public final static String FLD_NAME    			= "FLD_NAME";
    public final static String FLD_DESCRIPTION      = "FLD_DESCRIPTION";
    public final static String FLD_URL             	= "FLD_URL";
    public final static String FLD_SERVICE        	= "FLD_SERVICE";
    public final static String FLD_PORT        		= "FLD_PORT";
    public final static String FLD_OPERATION        = "FLD_OPERATION";
    public final static String FLD_PROJECT_ID       = "FLD_PROJECT_ID";

    //--------------------------------------------------------------------------
    // construction

    /**
     * Blank ctor
     */
    public WebServiceOperationDobj()
    {
    }
    public WebServiceOperationDobj(int id, String name, String description, String url, String service, String port, String operation)
    {
    	this.setId(id);
    	this.setName(name);
    	this.setDescription(description);
    	this.setUrl(url);
    	this.setService(service);
    	this.setPort(port);
    	this.setOperation(operation);
    }


    //--------------------------------------------------------------------------
    // persistence

    /**
     * Static factory method to create the entity to be used by this dataobject
     * and any subclasses. This is called by the DataManager's factory when
     * creating a ENTITY entity.
     * @return the created entity.
     */
    public static Entity createEntity()
    {
        // create the entity
        Entity ent = new EntityImpl(ENT_WEB_SERVICE_OPERATION, WebServiceOperationDobj.class);

        // add the key generator for the system id
        ent.addKeyGeneratorData(new DefaultKeyGeneratorData(ENT_WEB_SERVICE_OPERATION, FLD_ID));

        // create the fields and add them to the entity
        ent.addField(new FieldImpl(FLD_ID, Field.FT_INTEGER, Field.FF_SYSTEM_KEY | Field.FF_UNIQUE_KEY));
        ent.addField(new FieldImpl(FLD_NAME, Field.FT_STRING));
        ent.addField(new FieldImpl(FLD_DESCRIPTION, Field.FT_STRING));
        ent.addField(new FieldImpl(FLD_URL, Field.FT_STRING));
        ent.addField(new FieldImpl(FLD_SERVICE, Field.FT_STRING));
        ent.addField(new FieldImpl(FLD_PORT, Field.FT_STRING));
        ent.addField(new FieldImpl(FLD_OPERATION, Field.FT_STRING));
        ent.addField(new FieldImpl(FLD_PROJECT_ID, Field.FT_INTEGER));
        // return the entity
        return ent;
    }

    /**
     * @return String the name of the entity that defines this DataObject
     */
    public String getEntityName()
    {
        return ENT_WEB_SERVICE_OPERATION;
    }


    //--------------------------------------------------------------------------
    // accessors


    /**
     * @return int the id of this Entity
     */
    public int getId()
    {
        return getIntValue(FLD_ID);
    }

    /**
     * Set the id of this Entity
     * @param id
     */
    public void setId(int id)
    {
        setValue(FLD_ID, id);
    }
	/**
	 * @return int the id of this project
	 */
	public int
	getProjectId ()
	{
		return getIntValue (FLD_PROJECT_ID);
	}

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
    /**
     * Returns the name of the entity
     * @return String
     */
    public String getName()
    {
        return getStringValue(FLD_NAME);
    }

    /**
     * Sets the entity name into the Entity
     * @param name
     * @return void
     */
    public void setName(String name)
    {
        setValue(FLD_NAME, name);
    }

    /**
     * Return the default class
     * @return Class the default class
     */
    public Class getDefaultClass()
    {
        return Record.class;
    }

    /**
     * Set the default class name
     * @param defaultClass the default class
     * @return void
     */
    public void setDefaultClass(Class defaultClass)
    {
    }

    /**
     * @return String URL
     */
    public String getUrl()
    {
        return getStringValue(FLD_URL);
    }

    /**
     * Set URL
     * @param url
     */
    public void setUrl(String url)
    {
        setValue(FLD_URL, url);
    }

    /**
     * Returns the description of the entity
     * @return String
     */
    public String getDescription()
    {
        return getStringValue(FLD_DESCRIPTION);
    }

    /**
     * Sets the entity description into the Entity
     * @param name
     * @return void
     */
    public void setDescription(String description)
    {
        setValue(FLD_DESCRIPTION, description);
    }
    
    /**
     * @return String service
     */
    public String getService()
    {
        return getStringValue(FLD_SERVICE);
    }

    /**
     * Set service
     * @param service
     */
    public void setService(String service)
    {
        setValue(FLD_SERVICE, service);
    } 
    /**
     * @return String port
     */
    public String getPort()
    {
        return getStringValue(FLD_PORT);
    }

    /**
     * Set port
     * @param port
     */
    public void setPort(String port)
    {
        setValue(FLD_PORT, port);
    } 
    /**
     * @return String operation
     */
    public String getOperation()
    {
        return getStringValue(FLD_OPERATION);
    }

    /**
     * Set operation
     * @param operation
     */
    public void setOperation(String operation)
    {
        setValue(FLD_OPERATION, operation);
    }     
}
