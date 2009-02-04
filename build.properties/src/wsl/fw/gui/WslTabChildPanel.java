//==============================================================================
// WslTabChildPanel.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.gui;

import wsl.fw.util.Util;
import wsl.fw.help.HelpId;

//------------------------------------------------------------------------------
/**
 * Base class for panels to be placed in tabs in a WslTabbedPanel.
 * Usage:
 * Create a concrete subclass that implements the abstract functions, create
 * controls and action listeners.
 */
public abstract class WslTabChildPanel extends WslWizardChild
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/gui/WslTabChildPanel.java $ ";

    //--------------------------------------------------------------------------
    /**
     * Constructor.
     * @param panelTitle, the title for the panel which is used as the name of
     *   the tab for this panel. Not null or empty.
     */
    public WslTabChildPanel(String panelTitle)
    {
        // check the param and set the title
        Util.argCheckEmpty(panelTitle);
        setPanelTitle(panelTitle);
    }

    //--------------------------------------------------------------------------
    /**
     * Get the buttons that this tab wants to display in the parent's button
     * panel. This is called each time the tab child panel is selected. The
     * subclass should handle listening to the button actions.
     * @return an array of buttons, may be empty, not null.
     */
    public abstract WslButton[] getButtons();

    //--------------------------------------------------------------------------
    /**
     * Notification function called each time the tab is selected or deselected.
     * @param selected, true if being selected, false if being deselected.
     */
    public abstract void onSelected(boolean selected);

    //--------------------------------------------------------------------------
    /**
     * Get the HelpId for this tab child panel. The WslTabbedPanel parent will
     * use this id to create a help button. If the tab child needs multiple help
     * buttons it can add and manage extra ones as custom buttons using
     * getButtons.
     * @return the HelpId for the help button, or null if no help button.
     */
    public HelpId getHelpId()
    {
        return null;
    }
}

//==============================================================================
// end of file WslTabChildPanel.java
//==============================================================================
