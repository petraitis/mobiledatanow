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
import wsl.fw.help.HelpId;
import wsl.fw.resource.ResId;
import wsl.fw.datasource.DataObject;
import wsl.fw.gui.GuiConst;
import wsl.fw.gui.WslWizardChild;
import wsl.fw.gui.WslTextArea;
import wsl.mdn.dataview.DataView;
import wsl.mdn.dataview.DataSourceDobj;

//------------------------------------------------------------------------------
/**
 *
 */
public class DataViewWizardSplashPanel extends WslWizardChild
    implements ActionListener
{
    //--------------------------------------------------------------------------
    // resources

    public static final ResId RADIO_NEW_DATAVIEW  =
        new ResId("DataViewWizardSplashPanel.radio.New");
    public static final ResId LABEL_NEW_DATAVIEW  =
        new ResId("DataViewWizardSplashPanel.label.New");
    public static final ResId RADIO_EXISTING_DATAVIEW  =
        new ResId("DataViewWizardSplashPanel.radio.Existing");
    public static final ResId LABEL_EXISTING_DATAVIEW  =
        new ResId("DataViewWizardSplashPanel.label.Existing");

    public final static HelpId HID_DATAVIEW_SPLASH = new HelpId("mdn.admin.DataViewWizardSplashPanel");

    //--------------------------------------------------------------------------
    // attributes

    private DataView _dv = new DataView();
    private DataView _existingDv;
    private DataSourceDobj _dsDobj;

    //--------------------------------------------------------------------------
    // controls

    private JRadioButton _rdoNew = new JRadioButton(RADIO_NEW_DATAVIEW.getText());
    private JRadioButton _rdoExisting = new JRadioButton(RADIO_EXISTING_DATAVIEW.getText());


    //--------------------------------------------------------------------------
    // construction

    /**
     * ctor
     * @param DataSourceDobj ds
     */
    public DataViewWizardSplashPanel()
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
        WslTextArea lbl = new WslTextArea(65, 6);
        lbl.setText(LABEL_NEW_DATAVIEW.getText());
        lbl.setBackground(this.getBackground());
        gbc.weighty = 0.5;
        gbc.gridy = 1;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        add(lbl, gbc);

        // existing
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 0;
        gbc.insets.bottom = 0;
        gbc.weighty = 0.5;
        _rdoExisting.addActionListener(this);
        add(_rdoExisting, gbc);
        lbl = new WslTextArea(65, 4);
        lbl.setText(LABEL_EXISTING_DATAVIEW.getText());
        lbl.setBackground(this.getBackground());
        gbc.gridy = 3;
        gbc.weighty = 0.5;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        add(lbl, gbc);
    }


    //--------------------------------------------------------------------------
    // accessors

    /**
     * Set the existing DataView
     * @param existingDv the existing DataView
     */
    public void setExistingDataView(DataView existingDv)
    {
        // validate
        Util.argCheckNull(existingDv);

        // set attribs
        _existingDv = existingDv;
        _dv = _existingDv;

        // set existing radio
        _rdoExisting.setSelected(true);

        // set the radio text
        _rdoExisting.setText(RADIO_EXISTING_DATAVIEW.getText() + ": " +
            _existingDv.getName());

        // update buttons
        updateButtons();
    }

    /**
     * Set the existing DataView
     * @param existingDv the existing DataView
     */
    public void setExistingDataSourceDobj(DataSourceDobj dsDobj)
    {
        // validate
        Util.argCheckNull(dsDobj);

        // set attribs
        _dsDobj = dsDobj;

        if (_dv != null)
        {
            if (_dv.getState() == DataObject.NEW)
                _dv.setSourceDataSource(dsDobj);
        }
    }

    /**
     * @return DataView the selected DataView
     */
    public DataView getSelectedDataView()
    {
        return _dv;
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
                _dv = new DataView();
            else if(ev.getSource() == _rdoExisting)
                _dv = _existingDv;

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
        boolean hasExisting = _existingDv != null;

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
        return HID_DATAVIEW_SPLASH;
    }
}