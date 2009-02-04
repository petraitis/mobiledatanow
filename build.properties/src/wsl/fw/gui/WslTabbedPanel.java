//==============================================================================
// WslTabbedPanel.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.gui;

import wsl.fw.util.Util;
import wsl.fw.util.Log;
import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import wsl.fw.resource.ResId;

//------------------------------------------------------------------------------
/**
 * A button panel whose client area is a tab control which can contain multiple
 * WslTabChildPanels.
 * Usage:
 * Create an instance (or subclass) and call addTabPanel() to add tab panes
 * which are concrete subclasses or WslTabChildPanel. This is just a continer,
 * the logic and real work is in the WslTabChildPanels.
 */
public class WslTabbedPanel
    extends WslButtonPanel
    implements ActionListener, ChangeListener
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/gui/WslTabbedPanel.java $ ";

    // resources
    public static final ResId BUTTON_CLOSE  = new ResId("WslTabbedPanel.button.Close");
    public static final ResId BUTTON_HELP  = new ResId("WslTabbedPanel.button.Help");

    // attributes
    protected WslButton   _btnClose = new WslButton(BUTTON_CLOSE.getText(), this);
    protected JTabbedPane _tabbedPane;

    //--------------------------------------------------------------------------
    /**
     * Constructor.
     * @param orientation, the orientation (HORIZONTAL or VERTICAL as defined in
     *   WslButtonPanel) of the buttons maintained by the superclass.
     */
    public WslTabbedPanel(int orientation)
    {
        // call base class constructor
        super(orientation);

        // set the close button icon
        _btnClose.setIcon(Util.resourceIcon(GuiConst.FW_IMAGE_PATH + "close.gif"));
        addCustomButton(_btnClose);

        // set layout for main panel
        getMainPanel().setLayout(new BorderLayout());

        // create the tab control and add it
        _tabbedPane = new JTabbedPane();
        getMainPanel().add(_tabbedPane, BorderLayout.CENTER);

        // set the change listener
        _tabbedPane.addChangeListener(this);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the frame parent window. Override.
     */
    public void setFrameParent(Window parent)
    {
        // call bas class function
        super.setFrameParent(parent);

        // ripple parent down to all the contained chil tabs

        for (int i = 0; i < _tabbedPane.getTabCount(); i++)
        {
            WslTabChildPanel tabChild = (WslTabChildPanel) _tabbedPane.getComponentAt(i);
            tabChild.setFrameParent(parent);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Add a new tab child panel.
     *
     */
    public void addTabPanel(WslTabChildPanel tabPanel)
    {
        tabPanel.setFrameParent(getFrameParent());
        _tabbedPane.add(tabPanel.getPanelTitle(), tabPanel);
    }

    //--------------------------------------------------------------------------
    /**
     * Action performed, handle the close button.
     */
    public void actionPerformed(ActionEvent ev)
    {
        try
        {
            // switch on source
            if(ev.getSource().equals(_btnClose)) // close the panel
                closePanel();
        }
        catch(Exception e)
        {
            Log.error("WslTabbedPanel.actionPerformed:", e);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Handle state changed events generated whena new tab is selected.
     */
    public void stateChanged(ChangeEvent e)
    {
        // new tab selected
        // get the new tab
        WslTabChildPanel tabChild = null;
        int              index    = _tabbedPane.getSelectedIndex();
        if (index != -1)
            tabChild = (WslTabChildPanel) _tabbedPane.getComponentAt(index);

        // remove the standard close button and help button
        removeAllCustomButtons();

        // add help button if the child has one
        if (tabChild != null)
            if (tabChild.getHelpId() != null)
                addHelpButton(BUTTON_HELP.getText(), tabChild.getHelpId(), -1, true);

        // add the standard close button
        addCustomButton(_btnClose);

        // remove all buttons owned by the previous child tab
        removeAllButtons();

        // if a tab is selected set its buttons and notify it of activation
        if (tabChild != null)
        {
            WslButton buttons[] = tabChild.getButtons();
            for (int i = 0; i < buttons.length; i++)
                addButton(buttons[i]);

            tabChild.onSelected(true);
        }

        // notify all other tabs of deselect
        for (int i = 0; i < _tabbedPane.getTabCount(); i++)
        {
            WslTabChildPanel deselTabChild = (WslTabChildPanel) _tabbedPane.getComponentAt(i);
            if (deselTabChild != tabChild)
                deselTabChild.onSelected(false);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Panel closing. Notify children.
     */
    public void onClosePanel()
    {
        // notify all children of close
        for (int i = 0; i < _tabbedPane.getTabCount(); i++)
        {
            WslTabChildPanel tabChild = (WslTabChildPanel) _tabbedPane.getComponentAt(i);
            tabChild.onClosePanel();
        }

        // call superclass
        super.onClosePanel();
    }
}

//==============================================================================
// end of file WslTabbedPanel.java
//==============================================================================
