//==============================================================================
// HelpId.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.help;

import javax.help.BadIDException;
import javax.help.HelpBroker;
import javax.help.DefaultHelpBroker;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Window;
import wsl.fw.gui.WslPanel;

//------------------------------------------------------------------------------
/**
 * Typed wrapper for string help ids. The ID is used to display topics in a
 * help set. The ID refers to a mapId in the help set's map file (.jhm).
 *
 * Usage.
 * The main application object sets a HelpManager.
 * Individual screens that wish to display context sensitive help may define
 * one or more HelpId objects (generally as constants) and implement action
 * listeners for menu items or buttons which call displayHelp() on the HelpId.
 * The caller should pass its parent window or WslPanel to displayHelp so the
 * help system can identify the parent java.awt.Window. This is VERY important
 * if the caller is a modal dialog.
 */
public class HelpId
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/help/HelpId.java $ ";

    // data members
    private String _id;

    //--------------------------------------------------------------------------
    /**
     * Constructor.
     * @param id, the string defining the mapId entry that will be displayed.
     */
    public HelpId(String id)
    {
        _id = id;
    }

    //--------------------------------------------------------------------------
    /**
     * @return the help id string.
     */
    public String getId()
    {
        return _id;
    }

    //--------------------------------------------------------------------------
    /**
     * Display the help topic for this HelpId.
     * No parent window is used so this should only be called from non-modal
     * windows.
     * @throws BadIDException if the id is not present in the helpset (runtime).
     */
    public void displayHelp() throws BadIDException
    {
        // delegate with no parent
        displayHelp(null);
    }

    //--------------------------------------------------------------------------
    /**
     * Display the help topic for this HelpId.
     * @param parent, the parent window for use by the help system. Important if
     *   the parent is a modal dialog. The parent should be a top level Window
     *   (i.e. a frame or dialog) or a WslPanel on which getFrameParent() can be
     *   called. If neither then the parent hierarchy is scanned looking for a
     *   window. May be null.
     * @throws BadIDException if the id is not present in the helpset (runtime).
     */
    public void displayHelp(Component parent) throws BadIDException
    {
        // get the help broker
        HelpBroker hb = HelpManager.getHelpBroker();

        // if present get the parent frame and pass it to the help
        // system so modal dialogs can be correctly handled
        if (parent != null && hb instanceof javax.help.DefaultHelpBroker)
        {
            Window windowParent = null;

            // is parent a Window or a WslPanel?
            if (parent instanceof Window)
                windowParent = (Window) parent;
            else if (parent instanceof WslPanel)
                windowParent = ((WslPanel) parent).getFrameParent();

            // if windowParent is still null then search component hierarchy
            if (windowParent == null)
                windowParent = (Window) SwingUtilities.getAncestorOfClass(Window.class, parent);

            // if we managed to find a suitable parent pass it to help
            if (windowParent != null)
                ((DefaultHelpBroker)hb).setActivationWindow(windowParent);
        }

        // display the help
        hb.setCurrentID(_id);
        hb.setDisplayed(true);
    }
}

//==============================================================================
// end of file HelpId.java
//==============================================================================
