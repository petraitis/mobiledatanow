package wsl.fw.msgserver;

// imports
import java.util.Hashtable;
import java.util.Vector;
import wsl.fw.datasource.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c)
 * Company:
 * @author
 * @version 1.0
 */

public abstract class MessageDobj extends DataObject
{
    //--------------------------------------------------------------------------
    // constants

    public static final String ENT_MS_MESSAGE   = "MS_MESSAGE";


    //--------------------------------------------------------------------------
    // DataObject

    /**
     * Static factory method to create the entity to be used by this dataobject
     * and any subclasses. This is called by the DataManager's factory when
     * creating a MESSAGE entity.
     * @return the created entity.
     */
    public static Entity createEntity()
    {
        // create the entity
        Entity ent = new EntityImpl(ENT_MS_MESSAGE, MessageDobj.class, true);

        // return the entity
        return ent;
    }

    //--------------------------------------------------------------------------
    /**
     * @return String the name of the entity that defines this DataObject
     */
    public String getEntityName()
    {
        return ENT_MS_MESSAGE;
    }
}