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
public class ProjectDobj extends DataObject
{
    //--------------------------------------------------------------------------
    // constants

    public final static String ENT_PROJECT          = "TBL_PROJECT";
    public final static String FLD_ID               = "FLD_ID";
    public final static String FLD_NAME             = "FLD_NAME";
    public final static String FLD_DESCRIPTION      = "FLD_DESCRIPTION";
    public final static String FLD_DEL_STATUS      = "FLD_DEL_STATUS";


    //--------------------------------------------------------------------------
    // attributes


    //--------------------------------------------------------------------------
    // construction

    /**
     * Blank ctor
     */
    public ProjectDobj()
    {
    }
    public ProjectDobj(int id, String name, String description)
    {
    	this.setId(id);
    	this.setName(name);
    	this.setDescription(description);
    }
    /**
     * Entity ctor
     */
    public ProjectDobj(Entity ent)
    {
        this.setName(ent.getName());
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
        Entity ent = new EntityImpl(ENT_PROJECT, ProjectDobj.class);

        // add the key generator for the system id
        ent.addKeyGeneratorData(new DefaultKeyGeneratorData(ENT_PROJECT, FLD_ID));

        // create the fields and add them to the entity
        ent.addField(new FieldImpl(FLD_ID, Field.FT_INTEGER, Field.FF_SYSTEM_KEY | Field.FF_UNIQUE_KEY));
        ent.addField(new FieldImpl(FLD_NAME, Field.FT_STRING, Field.FF_NAMING));
        ent.addField(new FieldImpl(FLD_DESCRIPTION, Field.FT_STRING));
        ent.addField(new FieldImpl(FLD_DEL_STATUS, Field.FT_STRING));

        // return the entity
        return ent;
    }

    /**
     * @return String the name of the entity that defines this DataObject
     */
    public String getEntityName()
    {
        return ENT_PROJECT;
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
     * Returns the description of the entity
     * @return String
     */
    public String getDescription()
    {
        return getStringValue(FLD_DESCRIPTION);
    }

    /**
     * Sets the entity delete status into the Entity
     * @param name
     * @return void
     */
    public void setDescription(String name)
    {
        setValue(FLD_DESCRIPTION, name);
    }
    /**
     * Returns the description of the entity
     * @return String
     */
    public String getDelStatus()
    {
        return getStringValue(FLD_DEL_STATUS);
    }

    /**
     * Sets the entity delete status into the Entity
     * @param name
     * @return void
     */
    public void setDelStatus(String status)
    {
        setValue(FLD_DEL_STATUS, status);
    }
}
