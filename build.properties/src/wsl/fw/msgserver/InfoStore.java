package wsl.fw.msgserver;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c)
 * Company:
 * @author
 * @version 1.0
 */

public class InfoStore
{
    //--------------------------------------------------------------------------
    // attributes

    public String _name;
    public String _id;


    //--------------------------------------------------------------------------
    // construction

    /**
     * name, id ctor
     * @param name the name of the info store
     * @param id the id of the info store
     */
    public InfoStore(String name, String id)
    {
        _name = name;
        _id = id;
    }
}