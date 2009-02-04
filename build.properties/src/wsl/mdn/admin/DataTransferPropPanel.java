package wsl.mdn.admin;

// imports
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JComponent;
import pv.jfcx.JPVPassword;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.datasource.*;
import wsl.fw.gui.GuiConst;
import wsl.fw.gui.PropertiesPanel;
import wsl.fw.gui.WslTextField;
import wsl.fw.gui.WslComboBox;
import wsl.fw.gui.WslButton;
import wsl.fw.resource.ResId;
import wsl.fw.help.HelpId;
import wsl.mdn.dataview.DataSourceDobj;
import wsl.mdn.dataview.DataTransfer;

//------------------------------------------------------------------------------
/**
 *
 */
public class DataTransferPropPanel extends PropertiesPanel
    implements ActionListener
{
    //--------------------------------------------------------------------------
    // resources

    public static final ResId LABEL_NAME  =
        new ResId("DataTransferPropPanel.label.Name");
    public static final ResId LABEL_DATASOURCE  =
        new ResId("DataTransferPropPanel.label.DataSource");
    public static final ResId LABEL_DESCRIPTION  =
        new ResId("DataTransferPropPanel.label.Description");
    public static final ResId COMBO_MIRROR  =
        new ResId("DataTransferPropPanel.combo.Mirror");
    public static final ResId BUTTON_FILTER  =
        new ResId("DataTransferPropPanel.btn.Filter");
    public static final ResId ERR_GET_DS =
        new ResId("DataTransferPropPanel.error.gettingDataSource");

    public final static HelpId HID_DATA_TRANSFER = new HelpId("mdn.admin.DataTransferPropPanel");

    //--------------------------------------------------------------------------
    // controls

    private WslTextField _txtName        = new WslTextField(300);
    private WslComboBox _cmbDataSource = new WslComboBox(300);
    private WslTextField _txtDescription        = new WslTextField(300);
    private WslTextField _txtFilter        = new WslTextField(300);
    private WslButton _btnFilter        = new WslButton(BUTTON_FILTER.getText());


    //--------------------------------------------------------------------------
    // attributes



    //--------------------------------------------------------------------------
    // construction

    /**
     * Default constructor.
     */
    public DataTransferPropPanel()
    {
        // init controls
        initDataTransferPropPanelControls();

        // build the type combo
        buildDataSourceCombo();

        // update buttons
        updateButtons();
    }

    //--------------------------------------------------------------------------
    /**
     * Init the panel's controls.
     */
    private void initDataTransferPropPanelControls()
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

        // description
        lbl = new JLabel(LABEL_DESCRIPTION.getText());
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.insets.right = 0;
        add(lbl, gbc);
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        add(_txtDescription, gbc);

        // datasource combo
        lbl = new JLabel(LABEL_DATASOURCE.getText());
        gbc.insets.right = 0;
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(lbl, gbc);
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        gbc.weighty = 1;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        add(_cmbDataSource, gbc);
        addMandatory(COMBO_MIRROR.getText(), _cmbDataSource);

        // redundant, do not show
        // filter
        /*
        gbc.insets.right = 0;
        gbc.gridx = 0;
        gbc.gridy = 3;
        _btnFilter.addActionListener(this);
        add(_btnFilter, gbc);
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        gbc.weighty = 1;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        add(_txtFilter, gbc);
        */
    }

    /**
     * Build type combo
     */
    private void buildDataSourceCombo()
    {
        // select the datasources
        DataSource sysDs = DataManager.getSystemDS();
        Query q = new Query(DataSourceDobj.ENT_DATASOURCE);
        q.addQueryCriterium(new QueryCriterium(DataSourceDobj.ENT_DATASOURCE,
            DataSourceDobj.FLD_IS_MIRRORED, QueryCriterium.OP_EQUALS,
            new Boolean(true)));
        try
        {
            RecordSet rs = sysDs.select(q);

            // iterate and build combo
            DataSourceDobj ds;
            while(rs != null && rs.next())
            {
                // get the ds
                ds = (DataSourceDobj)rs.getCurrentObject();
                if(ds != null)
                    _cmbDataSource.addItem(ds);
            }
        }
        catch(Exception e)
        {
            wsl.fw.gui.GuiManager.showErrorDialog(this,
                ERR_GET_DS.getText(), e);
        }
    }


    //--------------------------------------------------------------------------
    /**
     * Transfer data between the DataObject and panel controls.
     * @param toDataObject, determines the direction of the transfer.
     */
    public void transferData(boolean toDataObject)
    {
        // must have a DataObject
        DataTransfer dobj = (DataTransfer) getDataObject();
        Util.argCheckNull(dobj);

        if (toDataObject)
        {
            // to the DataObject
            dobj.setName(_txtName.getText());
            dobj.setDescription(_txtDescription.getText());

            // datasource
            DataSourceDobj ds = (DataSourceDobj)_cmbDataSource.getSelectedItem();
            if(ds != null)
                dobj.setDataSourceId(ds.getId());
        }
        else
        {
            // to the controls
            _txtName.setText(dobj.getName());
            _txtDescription.setText(dobj.getDescription());

            // datasource
            int dsid = dobj.getDataSourceId();
            DataSourceDobj ds;
            for(int i = 0; i < _cmbDataSource.getItemCount(); i++)
            {
                // get the ds and compare ids
                ds = (DataSourceDobj)_cmbDataSource.getItemAt(i);
                if(ds != null && ds.getId() == dsid)
                    _cmbDataSource.selectItem(ds.toString());
            }
        }
    }


    //--------------------------------------------------------------------------
    // actions

    /**
     * Action performed
     */
    public void actionPerformed(ActionEvent ev)
    {
        try
        {
            // switch on source
            if(ev.getSource() == _btnFilter)
                onFilter();

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

    /**
     * Filter button clicked
     */
    public void onFilter()
    {
    }


    //--------------------------------------------------------------------------
    /**
     * Validates Data in control.
     * @param comp Component to validate data.
     * @return boolean true if the component contains data
     */
    protected boolean hasData(JComponent comp)
    {
        // validate
        Util.argCheckNull(comp);

        // check combo Mirror Data Store has items
        if (comp == _cmbDataSource)
        {
            if (_cmbDataSource.getItemCount() == 0)
                return false;
        }

        // check for rest
        return super.hasData(comp);
    }

    //--------------------------------------------------------------------------
    // misc

    /**
     * Update controls
     */
    public void updateButtons()
    {
        _btnFilter.setEnabled(false);
    }

    /**
     * Return the preferred size
     */
    public Dimension getPreferredSize()
    {
        return new Dimension(400, 200);
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
        return HID_DATA_TRANSFER;
    }
}