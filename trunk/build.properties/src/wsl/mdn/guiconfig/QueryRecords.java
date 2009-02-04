//==============================================================================
// QueryRecords.java
// Copyright (c) 2001 WAP Solutions Ltd.
//==============================================================================

package wsl.mdn.guiconfig;


//------------------------------------------------------------------------------
/**
 */
public class QueryRecords extends MenuAction
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:35:35 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/guiconfig/QueryRecords.java $ ";

    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public QueryRecords()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Full constructor.
     * @param name, name of the action.
     * @param description, description of the action.
     * @param groupId, id of the group that owns this menu
     * @param parentMenuId, id of the paren MenuAction that contains this.
     */
    public QueryRecords(String name, String description, Object groupId,
        Object parentMenuId, Object queryId)
    {
        // call superclass
        super(name, description, groupId, parentMenuId);

        // set the query id
        setQueryId(queryId);
    }

    //--------------------------------------------------------------------------
    /**
     * @return the ID of the query.
     */
    public Object getQueryId()
    {
        return getObjectValue(FLD_QUERYDOBJID);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the ID of the query.
     */
    public void setQueryId(Object id)
    {
        setValue(FLD_QUERYDOBJID, id);
    }


    //--------------------------------------------------------------------------
    /**
     * @return the ID of the dataview.
     */
    public Object getDataViewId()
    {
        return getObjectValue(FLD_DATAVIEWID);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the ID of the dataview.
     */
    public void setDataViewId(Object id)
    {
        setValue(FLD_DATAVIEWID, id);
    }
}

//==============================================================================
// end of file QueryRecords.java
//==============================================================================
