//==============================================================================
// LogoutAction.java
// Copyright (c) 2001 WAP Solutions Ltd.
//==============================================================================

package wsl.mdn.guiconfig;


//------------------------------------------------------------------------------
/**
 * Action that allows the user to log out.
 */
public class LogoutAction extends MenuAction
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:35:35 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/guiconfig/LogoutAction.java $ ";

    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public LogoutAction()
    {
        // Note, name and description are left blank as ther presentation will
        // decide what to display for a logout
    }

    //--------------------------------------------------------------------------
    /**
     * Full constructor.
     * @param name, name of the action.
     * @param description, description of the action.
     * @param groupId, id of the group that owns this menu
     * @param parentMenuId, id of the paren MenuAction that contains this.
     */
    public LogoutAction(Object groupId,
        Object parentMenuId)
    {
        // Note, name and description are left blank as ther presentation will
        // decide what to display for a logout

        // set the group and parent
        setGroupId(groupId);
        setParentMenuId(parentMenuId);
    }
}

//==============================================================================
// end of file LogoutAction.java
//==============================================================================
