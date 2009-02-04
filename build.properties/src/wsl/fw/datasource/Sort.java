//==============================================================================
// Sort.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.datasource;

import java.io.Serializable;

//------------------------------------------------------------------------------
/**
 * Encapsulates information for a sort on a DataSource query
 */
public class Sort implements Serializable
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/datasource/Sort.java $ ";

    /**
     * constants
     */
    public static final String DIR_ASC = "ASC";
    public static final String DIR_DESC = "DESC";

    /**
     * attributes
     */
    public String _fieldName = "";
    public String _entityName = "";
    public String _direction = DIR_ASC;

    //--------------------------------------------------------------------------
    /**
     * Constructor taking arguments
     * @param entityName the entity of the field to be sorted on
     * @param fieldName the field to be sorted on
     */
    public Sort(String entityName, String fieldName)
    {
        // delegate
        this(entityName, fieldName, DIR_ASC);
    }

    /**
     * Copy ctor
     * @param s the Sort to copy
     */
    public Sort(Sort s)
    {
        this(s._entityName, s._fieldName, s._direction);
    }


    //--------------------------------------------------------------------------
    /**
     * Constructor taking arguments
     * @param entityName the entity of the field to be sorted on
     * @param fieldName the field to be sorted on
     * @param direction the direction of the sort
     * @roseuid 3973E0F4035F
     */
    public Sort(String entityName, String fieldName, String direction)
    {
        // set attribs
        _fieldName = fieldName;
        _entityName = entityName;
        _direction = direction;
    }

    /**
     * toString()
     */
    public String toString()
    {
        return _fieldName + " " + _direction;
    }
}

//==============================================================================
// end of file Sort.java
//==============================================================================
