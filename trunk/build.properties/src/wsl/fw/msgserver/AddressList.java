package wsl.fw.msgserver;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c)
 * Company:
 * @author
 * @version 1.0
 */

public class AddressList
{
    //--------------------------------------------------------------------------
    // attributes

    public String _name;
    public String _id;


    //--------------------------------------------------------------------------
    // construction

    /**
     * name, id ctor
     * @param name the name of the address list
     * @param id the id of the address list
     */
    public AddressList(String name, String id)
    {
        _name = name;
        _id = id;
    }
}