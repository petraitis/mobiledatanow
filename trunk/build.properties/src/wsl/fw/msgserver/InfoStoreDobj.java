package wsl.fw.msgserver;

import wsl.fw.util.Log;
import wsl.fw.datasource.*;


/**
 * Title:
 * Description:
 * Copyright:    Copyright (c)
 * Company:
 * @author
 * @version 1.0
 */

public class InfoStoreDobj extends MessageDobj
{
    //--------------------------------------------------------------------------
    // attributes

    private String _name = "";
    private String _id = "";


    //--------------------------------------------------------------------------
    // construction

    /**
     * Name ctor
     * @param is the infostore
     */
    public InfoStoreDobj(InfoStore is)
    {
        setName(is._name);
        setId(is._id);
    }


    //--------------------------------------------------------------------------
    // accessors

    /**
     * Returns the name
     * @return String
     */
    public String getName()
    {
        return _name;
    }


    /**
     * Sets the name
     * @param name
     * @return void
     */
    public void setName(String name)
    {
        _name = name;
    }

    /**
     * Returns the id
     * @return String
     */
    public String getId()
    {
        return _id;
    }


    /**
     * Sets the id
     * @param id
     * @return void
     */
    public void setId(String id)
    {
        _id = id;
    }


    //--------------------------------------------------------------------------
    // to string

    public String toString()
    {
        return getName();
    }
}