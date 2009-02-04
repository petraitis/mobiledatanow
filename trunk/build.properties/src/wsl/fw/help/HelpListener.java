//==============================================================================
// HelpListener.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.help;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Component;
import javax.help.BadIDException;
import wsl.fw.util.Log;
import wsl.fw.util.Util;
import wsl.fw.resource.ResId;

//------------------------------------------------------------------------------
/**
 * Listener to handle actions from help buttons and help menu items
 * and display help for a given topic as specified by a HelpId.
 *
 * Usage:
 * Create an instance of HelpId and pass to an addActionListener() call on a
 * menu item or button (or anything else which supports ActionListeners).
 * If the parent window is modal then you must pass the parent Window or
 * WslPanel to the HelpListener constructor.
 */
public class HelpListener implements ActionListener
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/help/HelpListener.java $ ";

    // resources
    public static final ResId ERR_RESOLVE_ID  = new ResId("HelpListener.error.ResolveId");

    // member data
    private HelpId    _helpId;
    private Component _parent = null;

    //--------------------------------------------------------------------------
    /**
     * Constructor.
     * Do not use from a modal window.
     * @param hid, the help id that will be displayed in response to the action.
     */
    public HelpListener(HelpId hid)
    {
        this(hid, null);
    }

    //--------------------------------------------------------------------------
    /**
     * Constructor.
     * @param helpId, the help id that will be displayed in response to the
     *   action. Not null.
     * @param parent, the parent window to use when creating the help window.
     *   This is necessary if the help is being started from a modal dialog
     *   otherwise the help window will not be accessible.
     * @throws IllegalArgumentException if helpId is null.
     */
    public HelpListener(HelpId helpId, Component parent)
    {
        // check and save params
        Util.argCheckNull(helpId);
        _parent = parent;
        _helpId = helpId;
    }

    //--------------------------------------------------------------------------
    /**
     * ActionListener override. Handle a help action and display the help.
     */
    public void actionPerformed(ActionEvent ev)
    {
        try
        {
            // display the help, passing parent (which may be null)
            _helpId.displayHelp(_parent);
        }
        catch (BadIDException ex)
        {
            Log.error(ERR_RESOLVE_ID.getText() + " ["
                + _helpId.getId() + "] :" + ex.toString());
        }
    }
}

//==============================================================================
// end of file HelpListener.java
//==============================================================================
