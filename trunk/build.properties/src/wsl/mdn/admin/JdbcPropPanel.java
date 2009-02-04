package wsl.mdn.admin;

// imports
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import java.sql.Connection;
import pv.jfcx.JPVPassword;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.datasource.*;
import wsl.fw.gui.GuiConst;
import wsl.fw.gui.GuiManager;
import wsl.fw.gui.PropertiesPanel;
import wsl.fw.gui.WslTextField;
import wsl.fw.gui.WslComboBox;
import wsl.fw.resource.ResId;
import wsl.fw.help.HelpId;
import wsl.mdn.dataview.JdbcDataSourceDobj;
import wsl.mdn.dataview.JdbcDriver;

//------------------------------------------------------------------------------
/**
 *
 */
public class JdbcPropPanel extends PropertiesPanel implements ActionListener
{
    //--------------------------------------------------------------------------
    // resources

    public static final ResId LABEL_NAME  = new ResId("JdbcPropPanel.label.Name");
    public static final ResId LABEL_DESCRIPTION  = new ResId("JdbcPropPanel.label.Description");
    public static final ResId LABEL_DRIVER  = new ResId("JdbcPropPanel.label.Driver");
    public static final ResId LABEL_URL  = new ResId("JdbcPropPanel.label.Url");
    public static final ResId LABEL_CATALOG  = new ResId("JdbcPropPanel.label.Catalog");
    public static final ResId LABEL_USER  = new ResId("JdbcPropPanel.label.User");
    public static final ResId LABEL_PASSWORD  = new ResId("JdbcPropPanel.label.Password");
    public static final ResId LABEL_MIRRORED  = new ResId("JdbcPropPanel.label.Mirrored");
    public static final ResId LABEL_MIRROR_DS  = new ResId("JdbcPropPanel.label.MirrorDataStore");
    public static final ResId ERR_BUILD_COMBO = new ResId("JdbcPropPanel.error.buildCombo");

    public static final ResId ERR_JDBC_1 = new ResId("JdbcPropPanel.error.jdbc1");
    public static final ResId ERR_JDBC_2 = new ResId("JdbcPropPanel.error.jdbc2");
    public static final ResId ERR_JDBC_3 = new ResId("JdbcPropPanel.error.jdbc3");

    public final static HelpId HID_JDBC = new HelpId("mdn.admin.JdbcPropPanel");

    //--------------------------------------------------------------------------
    // controls

    private WslTextField _txtName        = new WslTextField(200);
    private WslTextField _txtDescription = new WslTextField(300);
    private WslComboBox _cmbDrivers        = new WslComboBox(300);
    private WslTextField _txtUrl        = new WslTextField(300);
    private WslTextField _txtCatalog        = new WslTextField(200);
    private WslTextField _txtUser        = new WslTextField(200);
    private JPVPassword _txtPassword        = new JPVPassword('*');
    private JCheckBox _chkIsMirrored = new JCheckBox(LABEL_MIRRORED.getText());
    private WslComboBox _cmbMirrors        = new WslComboBox(300);


    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public JdbcPropPanel()
    {
        // init controls
        initJdbcPropPanelControls();

        // build drivers
        buildDriverCombo();

        // build mirror combo
        buildMirrorCombo();

        // update buttons
        updateButtons();
    }

    //--------------------------------------------------------------------------
    /**
     * Init the panel's controls.
     */
    private void initJdbcPropPanelControls()
    {
        // set layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Name control
        JLabel lbl = new JLabel(LABEL_NAME.getText());
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets.left = GuiConst.DEFAULT_INSET;
        gbc.insets.top = GuiConst.DEFAULT_INSET;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 0.2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(lbl, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        add(_txtName, gbc);
        addMandatory(LABEL_NAME.getText(), _txtName);

        // description control
        lbl = new JLabel(LABEL_DESCRIPTION.getText());
        gbc.insets.right = 0;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(lbl, gbc);
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        add(_txtDescription, gbc);

        // driver control
        lbl = new JLabel(LABEL_DRIVER.getText());
        gbc.insets.right = 0;
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(lbl, gbc);
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        add(_cmbDrivers, gbc);

        // url control
        lbl = new JLabel(LABEL_URL.getText());
        gbc.insets.right = 0;
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(lbl, gbc);
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        add(_txtUrl, gbc);
        addMandatory(LABEL_URL.getText(), _txtUrl);

        // catalog control
        lbl = new JLabel(LABEL_CATALOG.getText());
        gbc.insets.right = 0;
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(lbl, gbc);
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        add(_txtCatalog, gbc);

        // user control
        lbl = new JLabel(LABEL_USER.getText());
        gbc.insets.right = 0;
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(lbl, gbc);
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        add(_txtUser, gbc);

        // password control
        lbl = new JLabel(LABEL_PASSWORD.getText());
        gbc.insets.right = 0;
        gbc.gridx = 0;
        gbc.gridy = 6;
        add(lbl, gbc);
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        add(_txtPassword, gbc);

        // mirrored
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        gbc.weighty = 1;
        _chkIsMirrored.addActionListener(this);
        add(_chkIsMirrored, gbc);

        // mirror combo
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        add(_cmbMirrors, gbc);
    }

    /**
     * Build the driver combo
     */
    private void buildDriverCombo()
    {
        try
        {
            // select all drivers
            Query q = new Query(JdbcDriver.ENT_JDBCDRIVER);
            RecordSet rs = DataManager.getSystemDS().select(q);

            // build combo
            if(rs != null)
                _cmbDrivers.buildFromRecordSet(rs);
        }
        catch(Exception e)
        {
            GuiManager.showErrorDialog(this, ERR_BUILD_COMBO.getText(), e);
        }
    }

    /**
     * Build the mirror combo
     */
    private void buildMirrorCombo()
    {
        try
        {
            // select all mirrors
            Query q = new Query(JdbcDataSourceDobj.ENT_DATASOURCE);
            q.addQueryCriterium(new QueryCriterium(JdbcDataSourceDobj.ENT_DATASOURCE,
                JdbcDataSourceDobj.FLD_IS_MIRROR_DB, QueryCriterium.OP_EQUALS,
                new Boolean(true)));
            RecordSet rs = DataManager.getSystemDS().select(q);

            // build combo
            if(rs != null)
                _cmbMirrors.buildFromRecordSet(rs);
        }
        catch(Exception e)
        {
            GuiManager.showErrorDialog(this, ERR_BUILD_COMBO.getText(), e);
        }
    }


    //--------------------------------------------------------------------------
    // actions

    /**
     * Action event
     */
    public void actionPerformed(ActionEvent ev)
    {
        if(ev.getSource() == _chkIsMirrored)
            updateButtons();
    }


    //--------------------------------------------------------------------------
    /**
     * Override of checkMandatories() to ensure the driver and url are valid.
     * @return boolean true if all mandatory fields have data in them
     */
    public boolean checkMandatories()
    {
        boolean rv = super.checkMandatories();

        if (rv)
            try
            {
                // make a param describing the data source
                String     dsName  = _txtName.getText();
                JdbcDriver d       = (JdbcDriver)_cmbDrivers.getSelectedItem();
                String     driver  = (d == null) ? "" : d.getDriver();
                String     url     = _txtUrl.getText();
                String     catalog = _txtCatalog.getText();
                String     user    = _txtUser.getText();
                String     pw      = _txtPassword.getText();

                JdbcDataSourceParam dsParam = new JdbcDataSourceParam(dsName,
                    driver, url, user, pw);//catalog, 

                // get/create the data source from Datamanager
                DataSource ds = DataManager.getDataSource(dsParam);

                // test that the connection is valid
                ds.testConnection();
            }
            catch (Exception e)
            {
                // show error dialog
                String message[] =
                {
                    ERR_JDBC_1.getText(),
                    ERR_JDBC_2.getText(),
                    ERR_JDBC_3.getText()
                };
                GuiManager.showErrorDialog(this, message, null);
                Log.warning(ERR_JDBC_1.getText(), e);
                rv = false;
            }

        return rv;
    }

    //--------------------------------------------------------------------------
    /**
     * Transfer data between the DataObject and panel controls.
     * @param toDataObject, determines the direction of the transfer.
     */
    public void transferData(boolean toDataObject)
    {
        // must have a DataObject
        JdbcDataSourceDobj dobj = (JdbcDataSourceDobj) getDataObject();
        Util.argCheckNull(dobj);

        if (toDataObject)
        {
            // to the Category DataObject
            dobj.setName(_txtName.getText());
            dobj.setDescription(_txtDescription.getText());
            dobj.setJdbcUrl(_txtUrl.getText());
            dobj.setJdbcCatalog(_txtCatalog.getText());
            dobj.setJdbcUser(_txtUser.getText());
            dobj.setJdbcPassword(_txtPassword.getText());
            dobj.setIsMirrored(_chkIsMirrored.isSelected());

            // driver
            JdbcDriver d = (JdbcDriver)_cmbDrivers.getSelectedItem();
            if(d != null)
            {
                dobj.setJdbcDriverId(d.getId());
                dobj.setDriverDobj(d);
            }

            // mirror ds
            if(dobj.isMirrored())
            {
                JdbcDataSourceDobj ds = (JdbcDataSourceDobj)_cmbMirrors.getSelectedItem();
                if(ds != null)
                    dobj.setMirrorId(ds.getId());
            }
        }
        else
        {
            // to the controls
            _txtName.setText(dobj.getName());
            _txtDescription.setText(dobj.getDescription());
            _txtUrl.setText(dobj.getJdbcUrl());
            _txtCatalog.setText(dobj.getJdbcCatalog());
            _txtUser.setText(dobj.getJdbcUser());
            _txtPassword.setText(dobj.getJdbcPassword());
            _chkIsMirrored.setSelected(dobj.isMirrored());

            // driver
            JdbcDriver d = dobj.getDriverDobj();
            if(d != null)
                _cmbDrivers.selectItem(d.getName());

            // mirror ds
            if(dobj.isMirrored())
            {
                // iterate mirror combo
                JdbcDataSourceDobj ds;
                for(int i = 0; i < _cmbMirrors.getItemCount(); i++)
                {
                    // get the ds and compare
                    ds = (JdbcDataSourceDobj)_cmbMirrors.getItemAt(i);
                    if(ds != null && ds.getId() == dobj.getMirrorId())
                    {
                        _cmbMirrors.setSelectedIndex(i);
                        break;
                    }
                }
            }

            // update buttons
            updateButtons();
        }
    }


    //--------------------------------------------------------------------------
    // wizard

    /**
     * @return true if the finish button is to be enabled.
     */
    public boolean canFinish()
    {
        return true;
    }


    //--------------------------------------------------------------------------
    // misc

    /**
     * Enable controls
     */
    public void updateButtons()
    {
        // flags
        JdbcDataSourceDobj dobj = (JdbcDataSourceDobj)getDataObject();
        boolean isMirrorDb = dobj == null || dobj.isMirrorDb();
        boolean isMirrored = _chkIsMirrored.isSelected();

        // enable
        _chkIsMirrored.setVisible(!isMirrorDb);
        _cmbMirrors.setVisible(!isMirrorDb && isMirrored);
    }

    /**
     * Return the preferred size
     */
    public Dimension getPreferredSize()
    {
        //return new Dimension(450, 250);
        return new Dimension(500, 300);
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
        return HID_JDBC;
    }
}