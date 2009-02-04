package wsl.mdn.admin;

// imports
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import javax.swing.JLabel;
import pv.jfcx.JPVPassword;
import javax.swing.JCheckBox;
import wsl.fw.datasource.*;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.gui.GuiConst;
import wsl.fw.gui.PropertiesPanel;
import wsl.fw.gui.WslTextField;
import wsl.fw.gui.WslTextArea;
import wsl.fw.gui.WslComboBox;
import wsl.fw.gui.GuiManager;
import wsl.fw.resource.ResId;
import wsl.fw.help.HelpId;
import wsl.fw.datasource.Field;
import wsl.mdn.dataview.DataView;
import wsl.mdn.dataview.DataSourceDobj;
import wsl.mdn.dataview.QueryDobj;

//------------------------------------------------------------------------------
/**
 *
 */
public class QueryPropPanel extends PropertiesPanel implements ActionListener
{
    // resources
    public static final ResId LABEL_NAME  = new ResId("QueryPropPanel.label.Name");
    public static final ResId LABEL_DESCRIPTION  = new ResId("QueryPropPanel.label.Description");
    public static final ResId LABEL_DATASTORE  = new ResId("QueryPropPanel.label.DataStore");
    public static final ResId LABEL_DATAVIEW  = new ResId("QueryPropPanel.label.DataView");
    public static final ResId LABEL_ALL  = new ResId("QueryPropPanel.label.All");
    public static final ResId ERR_DATASTORE_COMBO  = new ResId("QueryPropPanel.error.dataStoreCombo");
    public static final ResId ERR_DATAVIEW_COMBO  = new ResId("QueryPropPanel.error.dataViewCombo");

    public final static HelpId HID_QUERY = new HelpId("mdn.admin.QueryPropPanel");

    // controls
    private WslTextField _txtName        = new WslTextField(200);
    private WslComboBox _cmbDataStore = new WslComboBox(200);
    private WslComboBox _cmbDataView = new WslComboBox(200);
    private WslTextField _txtDescription = new WslTextField(300);
    private boolean _isBuilding = false;


    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public QueryPropPanel()
    {
        // init controls
        initQueryPropPanelControls();

        // build the combos
        buildDataStoreCombo();
        buildDataViewCombo();
    }

    //--------------------------------------------------------------------------
    /**
     * Init the panel's controls.
     */
    private void initQueryPropPanelControls()
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

        // datastore combo
        lbl = new JLabel(LABEL_DATASTORE.getText());
        gbc.insets.right = 0;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(lbl, gbc);
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        _cmbDataStore.addActionListener(this);
        add(_cmbDataStore, gbc);

        // dataview combo
        lbl = new JLabel(LABEL_DATAVIEW.getText());
        gbc.insets.right = 0;
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(lbl, gbc);
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        add(_cmbDataView, gbc);

        // description control
        lbl = new JLabel(LABEL_DESCRIPTION.getText());
        gbc.insets.right = 0;
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(lbl, gbc);
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        gbc.weighty = 1;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        add(_txtDescription, gbc);
    }

    /**
     * Build DataStore combo
     */
    private void buildDataStoreCombo()
    {
        try
        {
            // set building flag
            _isBuilding = true;

            // build query
            DataSource sysDs = DataManager.getSystemDS();
            Query q = new Query(DataSourceDobj.ENT_DATASOURCE);
            QueryCriterium qc = new QueryCriterium(DataSourceDobj.ENT_DATASOURCE,
                DataSourceDobj.FLD_IS_MIRROR_DB, QueryCriterium.OP_EQUALS,
                new Boolean(false));
            qc.setOrIsNull(true);
            q.addQueryCriterium(qc);

            //select all data views
            RecordSet rs = sysDs.select(q);

            // build combo
            _cmbDataStore.buildFromRecordSet(rs);

            // set to all
            if(_cmbDataStore.getItemCount() > 0)
                _cmbDataStore.setSelectedIndex(0);

            // clear building flag
            _isBuilding = false;
        }
        catch(Exception e)
        {
            GuiManager.showErrorDialog(this, ERR_DATASTORE_COMBO.getText(), e);
        }
    }

    /**
     * Build DataView combo
     */
    private void buildDataViewCombo()
    {
        // build query
        DataSource sysDs = DataManager.getSystemDS();
        Query q = new Query(DataView.ENT_DATAVIEW);

        // get the dsid
        if(_cmbDataStore.getSelectedIndex() >= 0)
        {
            DataSourceDobj ds = (DataSourceDobj)_cmbDataStore.getSelectedItem();
            if(ds != null)
            {
                q.addQueryCriterium(new QueryCriterium(DataView.ENT_DATAVIEW,
                    DataView.FLD_SOURCE_DSID, QueryCriterium.OP_EQUALS,
                    new Integer(ds.getId())));
            }
        }

        try
        {
            //select all data views
            RecordSet rs = sysDs.select(q);

            // build combo
            _cmbDataView.clear();
            _cmbDataView.buildFromRecordSet(rs);
        }
        catch(Exception e)
        {
            GuiManager.showErrorDialog(this, ERR_DATAVIEW_COMBO.getText(), e);
        }
    }


    /**
     * Set the parent DataView
     * @param parentDv the parent DataView
     */
    public void setParentDataView(DataView parentDv)
    {
        // set item in combo
        if(parentDv != null)
            _cmbDataView.selectItem(parentDv.getName());
    }

    //--------------------------------------------------------------------------
    /**
     * Transfer data between the DataObject and panel controls.
     * @param toDataObject, determines the direction of the transfer.
     */
    public void transferData(boolean toDataObject)
    {
        // must have a DataObject
        QueryDobj dobj = (QueryDobj) getDataObject();
        Util.argCheckNull(dobj);

        if (toDataObject)
        {
            // to the Category DataObject
            dobj.setName(_txtName.getText());
            dobj.setDescription(_txtDescription.getText());

            // dataview
            DataView dv = (DataView)_cmbDataView.getSelectedItem();
            if(dv != null)
            {
                dobj.setViewOrTableId(dv.getId());
                dobj.setDataView(dv);
            }
        }
        else
        {
            // to the controls
            _txtName.setText(dobj.getName());
            _txtDescription.setText(dobj.getDescription());

            // get the dataview
            DataView dv = dobj.getDataView();
            if(dv != null)
            {
                // select the data object
                int dsid = dv.getSourceDsId();
                _cmbDataStore.selectDataObject(DataSourceDobj.FLD_ID,
                    String.valueOf(dsid));
            }

            // select the dataview
            int dvid = dobj.getViewOrTableId();
            _cmbDataView.selectDataObject(DataView.FLD_ID,
                String.valueOf(dvid));
        }
    }

    //--------------------------------------------------------------------------
    /**
     * @return true if the finish button is to be enabled.
     */
    public boolean canFinish()
    {
        return true;
    }

    /**
     * Return the preferred size
     */
    public Dimension getPreferredSize()
    {
        return new Dimension(400, 200);
    }


    //--------------------------------------------------------------------------
    // action performed

    /**
     * Action performed
     */
    public void actionPerformed(ActionEvent ev)
    {
        try
        {
            // switch on source
            if(ev.getSource() == _cmbDataStore && !_isBuilding)
                buildDataViewCombo();
        }
        catch(Exception e)
        {
            GuiManager.showErrorDialog(this,
                wsl.mdn.common.MdnAdminConst.ERR_UNHANDLED.getText(), e);
            Log.error(wsl.mdn.common.MdnAdminConst.ERR_UNHANDLED.getText(), e);
        }
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
        return HID_QUERY;
    }
}

