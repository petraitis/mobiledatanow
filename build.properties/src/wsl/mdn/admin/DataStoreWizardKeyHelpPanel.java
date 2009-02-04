//==============================================================================
// DataStoreWizardKeyHelpPanel.java
// Copyright (c) 2001 WAP Solutions Ltd.
//==============================================================================

package wsl.mdn.admin;

// imports
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.resource.ResId;
import wsl.fw.datasource.DataObject;
import wsl.fw.gui.GuiConst;
import wsl.fw.gui.WslWizardChild;
import wsl.fw.gui.WslTextArea;
import wsl.fw.help.HelpId;
import wsl.mdn.dataview.DataSourceDobj;
import wsl.mdn.dataview.JdbcDataSourceDobj;

//------------------------------------------------------------------------------
/**
 * Final screen for datastore wizard, display text help on setting key fields.
 */

 public class DataStoreWizardKeyHelpPanel extends WslWizardChild
{
    //--------------------------------------------------------------------------
    // resources

    public static final ResId TEXT_KEYHELP1 = new ResId("DataStoreWizardKeyHelpPanel.text.keyHelp1");
    public static final ResId TEXT_KEYHELP2 = new ResId("DataStoreWizardKeyHelpPanel.text.keyHelp2");
    public static final ResId TEXT_KEYHELP3 = new ResId("DataStoreWizardKeyHelpPanel.text.keyHelp3");

    public final static HelpId HID_DSW_KEYHELP = new HelpId("mdn.admin.DataStoreWizardKeyHelpPanel");

    //--------------------------------------------------------------------------
    /**
     * Constructor.
     */
    public DataStoreWizardKeyHelpPanel()
    {
        // init controls
        initWizardControls();

        // update buttons
        updateButtons();
    }

    //--------------------------------------------------------------------------
    /**
     * Init the panel controls.
     */
    private void initWizardControls()
    {
        // layout
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        this.setBorder(BorderFactory.createLoweredBevelBorder());

        // text control
        gbc.anchor        = GridBagConstraints.NORTHWEST;
        gbc.insets.left   = GuiConst.DEFAULT_INSET * 2;
        gbc.insets.top    = GuiConst.DEFAULT_INSET * 2;
        gbc.insets.right  = GuiConst.DEFAULT_INSET * 2;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET * 2;
        gbc.gridx   = 0;
        gbc.gridy   = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        WslTextArea lbl = new WslTextArea(65, 6);
        lbl.setText(TEXT_KEYHELP1.getText() + "\n\n" + TEXT_KEYHELP2.getText()
            + "\n\n" + TEXT_KEYHELP3.getText());
        lbl.setBackground(this.getBackground());
        JScrollPane scroller = new JScrollPane(lbl);
        scroller.setBorder(BorderFactory.createLoweredBevelBorder());
        add(scroller, gbc);
    }

    //--------------------------------------------------------------------------
    /**
     * If the subclass has help override this to specify the HelpId.
     * This help is displayed using the parent wizards's help button.
     * @return the HelpId of the help to display, if null the help button is not
     *   displayed.
     */
    public HelpId getHelpId()
    {
        return HID_DSW_KEYHELP;
    }
}

//==============================================================================
// end of file DataStoreWizardKeyHelpPanel.java
//==============================================================================
