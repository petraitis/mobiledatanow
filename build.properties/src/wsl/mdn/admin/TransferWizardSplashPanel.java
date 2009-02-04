package wsl.mdn.admin;

// imports
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.resource.ResId;
import wsl.fw.help.HelpId;
import wsl.fw.datasource.DataObject;
import wsl.fw.gui.GuiConst;
import wsl.fw.gui.WslWizardChild;
import wsl.fw.gui.WslTextArea;
import wsl.mdn.dataview.DataTransfer;


//------------------------------------------------------------------------------
/**
 *
 */
public class TransferWizardSplashPanel extends WslWizardChild
    implements ActionListener
{
    //--------------------------------------------------------------------------
    // resources

    public static final ResId RADIO_NEW_TRANSFER  =
        new ResId("TransferWizardSplashPanel.radio.New");
    public static final ResId LABEL_NEW_TRANSFER  =
        new ResId("TransferWizardSplashPanel.label.New");
    public static final ResId RADIO_EXISTING_TRANSFER  =
        new ResId("TransferWizardSplashPanel.radio.Existing");
    public static final ResId LABEL_EXISTING_TRANSFER  =
        new ResId("TransferWizardSplashPanel.label.Existing");

    public final static HelpId HID_TRANSFER_SPLASH = new HelpId("mdn.admin.TransferWizardSplashPanel");

    //--------------------------------------------------------------------------
    // attributes

    private DataTransfer _dt = new DataTransfer();
    private DataTransfer _existingDt;


    //--------------------------------------------------------------------------
    // controls

    private JRadioButton _rdoNew = new JRadioButton(RADIO_NEW_TRANSFER.getText());
    private JRadioButton _rdoExisting = new JRadioButton(RADIO_EXISTING_TRANSFER.getText());


    //--------------------------------------------------------------------------
    // construction

    /**
     * ctor
     */
    public TransferWizardSplashPanel()
    {
        // init controls
        initWizardControls();

        // update buttons
        updateButtons();
    }

    /**
     * Init the panel controls
     */
    private void initWizardControls()
    {
        // layout
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        this.setBorder(BorderFactory.createLoweredBevelBorder());

        // button group
        ButtonGroup bg = new ButtonGroup();
        bg.add(_rdoNew);
        bg.add(_rdoExisting);
        _rdoNew.setSelected(true);

        // new
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets.left = GuiConst.DEFAULT_INSET * 4;
        gbc.insets.top = GuiConst.DEFAULT_INSET;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        _rdoNew.addActionListener(this);
        add(_rdoNew, gbc);
        WslTextArea lbl = new WslTextArea(65, 4);
        lbl.setText(LABEL_NEW_TRANSFER.getText());
        lbl.setBackground(this.getBackground());
        gbc.gridy = 1;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        add(lbl, gbc);

        // existing
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets.bottom = 0;
        _rdoExisting.addActionListener(this);
        add(_rdoExisting, gbc);
        lbl = new WslTextArea(65, 4);
        lbl.setText(LABEL_EXISTING_TRANSFER.getText());
        lbl.setBackground(this.getBackground());
        gbc.gridy = 3;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        add(lbl, gbc);
    }


    //--------------------------------------------------------------------------
    // accessors

    /**
     * Set the existing DataTransfer
     * @param existingDt the existing DataTransfer
     */
    public void setExistingDataTransfer(DataTransfer existingDt)
    {
        // validate
        Util.argCheckNull(existingDt);

        // set attribs
        _existingDt = existingDt;
        _dt = _existingDt;

        // set existing radio
        _rdoExisting.setSelected(true);

        // set the radio text
        _rdoExisting.setText(RADIO_EXISTING_TRANSFER.getText() + ": " +
            _existingDt.getName());

        // update buttons
        updateButtons();
    }

    /**
     * @return DataTransfer the selected DataTransfer
     */
    public DataTransfer getSelectedDataTransfer()
    {
        return _dt;
    }


    //--------------------------------------------------------------------------
    // actions

    /**
     * Button clicked
     */
    public void actionPerformed(ActionEvent ev)
    {
        try
        {
            // switch on source
            if(ev.getSource() == _rdoNew)
                _dt = new DataTransfer();
            else if(ev.getSource() == _rdoExisting)
                _dt = _existingDt;

            // update buttons
            updateButtons();
        }
        catch(Exception e)
        {
            wsl.fw.gui.GuiManager.showErrorDialog(this,
                wsl.mdn.common.MdnAdminConst.ERR_UNHANDLED.getText(), e);
            Log.error(wsl.mdn.common.MdnAdminConst.ERR_UNHANDLED.getText(), e);
        }
    }


    //--------------------------------------------------------------------------
    // misc

    /**
     * Update controls
     */
    public void updateButtons()
    {
        // flags
        boolean hasExisting = _existingDt != null;

        // enable
        _rdoExisting.setEnabled(hasExisting);
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
        return HID_TRANSFER_SPLASH;
    }
}