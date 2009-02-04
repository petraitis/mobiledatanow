//==============================================================================
// WslButtonPanel.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.gui;

// imports
import java.util.Vector;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import wsl.fw.util.Util;
import wsl.fw.help.HelpId;
import wsl.fw.help.HelpListener;

//------------------------------------------------------------------------------
/**
 * Superclass for WslPanels that contain button panels.
 */
public class WslButtonPanel extends WslWizardChild
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/gui/WslButtonPanel.java $ ";

    // orientation constants
    protected final static int VERTICAL = 0;
    protected final static int HORIZONTAL = 1;

    // attributes
    private JPanel    _pnlButtonParent = null;
    private JPanel    _pnlButtons = null;
    private JPanel    _pnlCustom = null;
    private JPanel    _pnlMain = null;
    private Vector    _buttons = new Vector();
    private Vector    _custom = new Vector();
    private int       _orientation = VERTICAL;

    //--------------------------------------------------------------------------
    /**
     * Blank constructor.
     */
    protected WslButtonPanel(int orientation)
    {
        // set attributes
        _orientation = orientation;

        // init controls
        initWslButtonControls();
    }

    //--------------------------------------------------------------------------
    /**
     * Init controls.
     */
    private void initWslButtonControls()
    {
        // layout
        setLayout(new GridBagLayout());

        // main panel
        _pnlMain = new JPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        add(_pnlMain, gbc);

        // button parent panel
        _pnlButtonParent = new JPanel();
        _pnlButtonParent.setLayout(new GridBagLayout());
        _pnlButtonParent.setBorder(BorderFactory.createLoweredBevelBorder());
        gbc = new GridBagConstraints();

        // buttons panel
        _pnlButtons = new JPanel();
        if(_orientation == VERTICAL)
            gbc.weightx = 1;
        else
            gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        _pnlButtons.setLayout(new GridBagLayout());
        _pnlButtons.setBackground(Color.gray);
        _pnlButtonParent.add(_pnlButtons, gbc);

        // add the remainder panel to the button parent
        _pnlCustom = new JPanel();
        _pnlCustom.setLayout(new GridBagLayout());
        if(_orientation == VERTICAL)
        {
            gbc.gridy = 1;
            gbc.weighty = 1;
        }
        else
        {
            gbc.gridx = 1;
            gbc.weightx = 1;
        }
        //gbc.fill = GridBagConstraints.BOTH;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        _pnlCustom.setBackground(Color.gray);
        _pnlButtonParent.add(_pnlCustom, gbc);

        // add the button parent to the maintenance panel
        gbc = new GridBagConstraints();
        if(_orientation == VERTICAL)
        {
            gbc.gridx = 1;
            gbc.weighty = 1;
        }
        else
        {
            gbc.gridy = 1;
            gbc.weightx = 1;
        }
        gbc.fill = GridBagConstraints.BOTH;
        add(_pnlButtonParent, gbc);
    }

    //--------------------------------------------------------------------------
    /**
     * @return JPanel the button panel.
     */
    public JPanel getButtonPanel()
    {
        return _pnlButtonParent;
    }

    //--------------------------------------------------------------------------
    /**
     * @return the main content panel.
     */
    public JPanel getMainPanel()
    {
        return _pnlMain;
    }

    //--------------------------------------------------------------------------
    /**
     * @return Vector the vector of buttons.
     */
    public Vector getButtons()
    {
        return _buttons;
    }

    //--------------------------------------------------------------------------
    /**
     * Add a button to the button panel.
     * @param btn The WslButton to add.
     */
    protected void addButton(WslButton btn)
    {
        // delegate
        addButton(btn, _buttons, _pnlButtons);
    }

    //--------------------------------------------------------------------------
    /**
     * Add a help button that displays a help topic.
     * @param helpButtonName, the name to display on the help button.
     * @param hid, the HelpId to display.
     */
    protected void addHelpButton(String helpButtonName, HelpId hid)
    {
        // delegate
        addHelpButton(helpButtonName, hid, -1, false);
    }

    //--------------------------------------------------------------------------
    /**
     * Add a help button that displays a help topic.
     * @param helpButtonName, the name to display on the help button.
     * @param hid, the HelpId to display.
     * @param width, the width of the button, if <= 0 the default width.
     */
    protected void addHelpButton(String helpButtonName, HelpId hid, int width)
    {
        // delegate
        addHelpButton(helpButtonName, hid, width, false);
    }

    //--------------------------------------------------------------------------
    /**
     * Add a help button that displays a help topic.
     * @param helpButtonName, the name to display on the help button.
     * @param hid, the HelpId to display.
     * @param width, the width of the button, if <= 0 the default width.
     * @param bCustom, if true button is added to custom rather than normal
     *   button panel.
     */
    protected void addHelpButton(String helpButtonName, HelpId hid, int width,
        boolean bCustom)
    {
        // create a listener
        HelpListener listener = new HelpListener(hid, this);

        // create the button
        WslButton helpButton;
        if (width <= 0)
            helpButton = new WslButton(helpButtonName, listener);
        else
            helpButton = new WslButton(helpButtonName, width, listener);

        // set the help icon
        helpButton.setIcon(Util.resourceIcon(GuiConst.FW_IMAGE_PATH + "help.gif"));

        // add the help button
        if (bCustom)
            addCustomButton(helpButton);
        else
            addButton(helpButton);
    }

    //--------------------------------------------------------------------------
    /**
     * Add a custom button to the custom panel.
     * @param btn The WslButton to add
     */
    protected void addCustomButton(WslButton btn)
    {
        // delegate
        addButton(btn, _custom, _pnlCustom);
    }

    //--------------------------------------------------------------------------
    /**
     * Add a button to the button panel.
     * @param btn The WslButton to add.
     */
    private void addButton(WslButton btn, Vector buttons, JPanel pnl)
    {
        // validate
        Util.argCheckNull(btn);

        // add button to the vector
        buttons.add(btn);

        // add the button
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets.top = GuiConst.DEFAULT_INSET;
        gbc.insets.left = GuiConst.DEFAULT_INSET;
        if(_orientation == VERTICAL)
        {
            gbc.weightx = 1;
            gbc.gridy = (buttons.size() - 1);
            gbc.insets.right = GuiConst.DEFAULT_INSET;
        }
        else
        {
            gbc.weighty = 1;
            gbc.gridx = (buttons.size() - 1);
            gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        }
        pnl.add(btn, gbc);

        // make the panel revalidate to ensure new button is drawn
        pnl.revalidate();
        pnl.repaint();
    }

    //--------------------------------------------------------------------------
    /**
     * Add a vector of buttons to the button panel.
     * @param buttons Vector of buttons to add.
     */
    protected void addButtons(Vector buttons)
    {
        // iterate vector and add buttons
        WslButton btn;
        for(int i = 0; buttons != null && i < buttons.size(); i++)
        {
            btn = (WslButton)buttons.elementAt(i);
            if(btn != null)
                addButton(btn);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Add a vector of buttons to the custom panel.
     * @param buttons Vector of buttons to add.
     */
    protected void addCustomButtons(Vector buttons)
    {
        // iterate vector and add buttons
        WslButton btn;
        for(int i = 0; buttons != null && i < buttons.size(); i++)
        {
            btn = (WslButton)buttons.elementAt(i);
            if(btn != null)
                addCustomButton(btn);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Remove all the custom buttons from this button panel.
     */
    protected void removeAllCustomButtons()
    {
        removeAllButtons(_custom, _pnlCustom);
    }

    //--------------------------------------------------------------------------
    /**
     * Remove all non-custom (i.e standard) buttons from this button panel.
     */
    protected void removeAllButtons()
    {
        removeAllButtons(_buttons, _pnlButtons);
    }

    //--------------------------------------------------------------------------
    /**
     * Remove the buttons in the specified vector/panel.
     */
    private void removeAllButtons(Vector buttons, JPanel pnl)
    {
        // empty the button vector
        buttons.clear();

        // remove all children (buttons) from the button panel
        pnl.removeAll();

        // force revalidate and draw
        pnl.revalidate();
        pnl.repaint();
    }
}

//==============================================================================
// end of file WslButtonPanel.java
//==============================================================================
