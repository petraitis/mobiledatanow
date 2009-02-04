//==============================================================================
// WslPanel.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.gui;

// imports
import java.awt.Window;
import javax.swing.JPanel;
import wsl.fw.util.Log;

import wsl.fw.resource.ResId;

//------------------------------------------------------------------------------
/**
 * Superclass of all panels extending the WSL Framework.
 * WslPanels are expected to be children or a WslDialog which is created by
 * calling GuiManager.getFramingDialog.
 * Whne the WspPanel is attached to the WslDialog by getFramingDialog postCreate
 * is called on the panel to perform any further initialization.
 * When the WslDialog closes onClosePanel is called on the WslPanel.
 */
public class WslPanel extends JPanel
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/gui/WslPanel.java $ ";

    // resources
    public static final ResId WARNING_NOT_WSL_DIALOG  = new ResId("WslPanel.warning.NotWSLDialog");

    // attributes
    private Window _frameParent = null;
    private String _panelTitle = "";
    private boolean _isClosing = false;

    /**
     * Set the frame parent window
     */
    public void setFrameParent(Window parent)
    {
        _frameParent = parent;
    }

    /**
     * @return The Window frame parent
     */
    public Window getFrameParent()
    {
        return _frameParent;
    }

    /**
     * Close the panel by closing its framing parent.
     */
    public final void closePanel()
    {
        // dispose the parent window
        if(_frameParent != null)
        {
            // if the parent is not a WslDialog the call onClosePanel as well
            if (! (_frameParent instanceof WslDialog))
            {
                Log.warning(WARNING_NOT_WSL_DIALOG.getText());
                onClosePanel();
            }

            // hide and dispose the parent
            _frameParent.hide();
            _frameParent.dispose();
        }
    }

    /**
     * Called by the framework to cause subclass panels to update the state of controls
     */
    public void updateButtons()
    {
    }

    /**
     * Called by framework to get the default button for a WslPanel
     * @return a WslButton
     */
    public WslButton getDefaultButton()
    {
        return null;
    }

    /**
     * Post creation call by framework. Allows non-constructor-safe initialisation of some controls
     */
    public void postCreate()
    {
    }

    /**
     * @return String the title for this panel
     */
    public String getPanelTitle()
    {
        return _panelTitle;
    }

    /**
     * Set the title of this panel
     * @param title
     */
    public void setPanelTitle(String title)
    {
        _panelTitle = title;
    }

    //--------------------------------------------------------------------------
    /**
     * Notification function called when we are closed by the framing dlg
     * (which should be a WslDialog) when it disposes due to a closePanel or
     * the close window (X) button.
     * Subclasses which have child WslPanels should propogate this message to
     * the children.
     */
    public void onClosePanel()
    {
        _isClosing = true;
    }

    /**
     * @return boolean true if panel is closing
     */
    protected boolean isClosing()
    {
        return _isClosing;
    }
}

//==============================================================================
// end of file WslPanel.java
//==============================================================================
