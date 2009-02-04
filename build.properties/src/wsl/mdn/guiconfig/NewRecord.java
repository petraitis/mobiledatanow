//==============================================================================
// NewRecord.java
// Copyright (c) 2001 WAP Solutions Ltd.
//==============================================================================

package wsl.mdn.guiconfig;


//------------------------------------------------------------------------------
/**
 */
public class NewRecord extends MenuAction
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:35:35 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/guiconfig/NewRecord.java $ ";

    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public NewRecord()
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
    public NewRecord(String name, String description, Object groupId,
        Object parentMenuId, Object dataViewId)
    {
        // call superclass
        super(name, description, groupId, parentMenuId);

        // set the dataview id
        setDataViewId(dataViewId);
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
// end of file NewRecord.java
//==============================================================================
