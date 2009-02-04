package wsl.mdn.admin;

// imports
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
 * Opening screen for the DataStoreImportWizard
 * Allows user to choose to import a new data store, or update the existing data store
 */
public class DataStoreWizardSplashPanel extends WslWizardChild
    implements ActionListener
{
    //--------------------------------------------------------------------------
    // resources

    public static final ResId RADIO_NEW_DATASTORE  =
        new ResId("DataStoreWizardSplashPanel.radio.New");
    public static final ResId LABEL_NEW_DATASTORE  =
        new ResId("DataStoreWizardSplashPanel.label.New");
    public static final ResId RADIO_EXISTING_DATASTORE  =
        new ResId("DataStoreWizardSplashPanel.radio.Existing");
    public static final ResId LABEL_EXISTING_DATASTORE  =
        new ResId("DataStoreWizardSplashPanel.label.Existing");

    public final static HelpId HID_DSW_SPLASH = new HelpId("mdn.admin.DataStoreWizardSplashPanel");

    //--------------------------------------------------------------------------
    // attributes

    private DataSourceDobj _ds = new JdbcDataSourceDobj();
    private DataSourceDobj _existingDs;


    //--------------------------------------------------------------------------
    // controls

    private JRadioButton _rdoNew = new JRadioButton(RADIO_NEW_DATASTORE.getText());
    private JRadioButton _rdoExisting = new JRadioButton(RADIO_EXISTING_DATASTORE.getText());


    //--------------------------------------------------------------------------
    // construction

    /**
     * ctor
     * @param DataSourceDobj ds
     */
    public DataStoreWizardSplashPanel()
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
        lbl.setText(LABEL_NEW_DATASTORE.getText());
        lbl.setBackground(this.getBackground());
        lbl.setBorder(BorderFactory.createLoweredBevelBorder());
        gbc.gridy = 1;
        gbc.weighty = 0.5;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        add(lbl, gbc);

        // existing
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 0;
        gbc.insets.bottom = 0;
        _rdoExisting.addActionListener(this);
        add(_rdoExisting, gbc);
        lbl = new WslTextArea(65, 4);
        lbl.setText(LABEL_EXISTING_DATASTORE.getText());
        lbl.setBackground(this.getBackground());
        lbl.setBorder(BorderFactory.createLoweredBevelBorder());
        gbc.gridy = 3;
        gbc.weighty = 0.5;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        add(lbl, gbc);
    }


    //--------------------------------------------------------------------------
    // accessors

    /**
     * Set the existing datasource
     * @param existingDs the existing datasource
     */
    public void setExistingDataSource(DataSourceDobj existingDs)
    {
        // validate
        Util.argCheckNull(existingDs);

        // set attribs
        _existingDs = existingDs;
        _ds = _existingDs;

        // set existing radio
        _rdoExisting.setSelected(true);

        // set the radio text
        _rdoExisting.setText(RADIO_EXISTING_DATASTORE.getText() + ": " +
            _existingDs.getName());

        // update buttons
        updateButtons();
    }

    /**
     * @return DataSourceDobj the selected datasource
     */
    public DataSourceDobj getSelectedDataSource()
    {
        return _ds;
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
                _ds = new JdbcDataSourceDobj();
            else if(ev.getSource() == _rdoExisting)
                _ds = _existingDs;

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
        boolean hasExisting = _existingDs != null;

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
        return HID_DSW_SPLASH;
    }


}