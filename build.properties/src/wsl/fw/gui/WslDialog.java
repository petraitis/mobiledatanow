//==============================================================================
// WslDialog.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.gui;

import wsl.fw.util.Log;
import javax.swing.JDialog;
import java.awt.Dialog;
import java.awt.Frame;

//------------------------------------------------------------------------------
/**
 * Dialog that has a WslPanel as its main child and handles notification of the
 * child panel when closing.
 */
public class WslDialog extends JDialog
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/gui/WslDialog.java $ ";

    // attributes
    WslPanel _wslPanel = null;

    //--------------------------------------------------------------------------
    /**
     * Constructors to match those in JDialog
     */
    public WslDialog(WslPanel wslPanel)
    {
        super();
        init(wslPanel);
    }

    public WslDialog(WslPanel wslPanel, Dialog owner)
    {
        super(owner);
        init(wslPanel);
    }

    public WslDialog(WslPanel wslPanel, Dialog owner, boolean modal)
    {
        super(owner, modal);
        init(wslPanel);
    }

    public WslDialog(WslPanel wslPanel, Dialog owner, String title)
    {
        super(owner, title);
        init(wslPanel);
    }

    public WslDialog(WslPanel wslPanel, Dialog owner, String title, boolean modal)
    {
        super(owner, title, modal);
        init(wslPanel);
    }

    public WslDialog(WslPanel wslPanel, Frame owner)
    {
        super(owner);
        init(wslPanel);
    }

    public WslDialog(WslPanel wslPanel, Frame owner, boolean modal)
    {
        super(owner, modal);
        init(wslPanel);
    }

    public WslDialog(WslPanel wslPanel, Frame owner, String title)
    {
        super(owner, title);
        init(wslPanel);
    }

    public WslDialog(WslPanel wslPanel, Frame owner, String title, boolean modal)
    {
        super(owner, title, modal);
        init(wslPanel);
    }

    //--------------------------------------------------------------------------
    /**
     * Init, set up the dialog to contain the WslPanel.
     * @param wslPanel, the child WslPanel.
     */
    private void init(WslPanel wslPanel)
    {
        // default to dispose on close, the dispose override will ensure that
        // the WslPanel gets its onClose message
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // save the panel
        _wslPanel = wslPanel;

        // if we have a panel
        if (_wslPanel != null)
        {
            // add panel to content pane
            getContentPane().add(_wslPanel);

            // set dlg in the panel
            _wslPanel.setFrameParent(this);
        }

        // pack the dialog
        pack();

        // if we have a panel
        if (_wslPanel != null)
        {
            // set the default button if valid
            if(_wslPanel.getDefaultButton() != null)
                getRootPane().setDefaultButton(_wslPanel.getDefaultButton());

            // call postCreate on panel
            _wslPanel.postCreate();
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Override of dispose to call onClosePanel on our child WslPanel.
     */
    public void dispose()
    {
        if (_wslPanel != null)
            _wslPanel.onClosePanel();
        super.dispose();
    }
}

//==============================================================================
// end of file WslDialog.java
//==============================================================================


