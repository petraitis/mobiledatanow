package wsl.mdn.guiconfig;

import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JComponent;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import wsl.fw.gui.GuiConst;
import wsl.fw.gui.PropertiesPanel;
import wsl.fw.gui.WslTextField;
import wsl.fw.gui.WslLabel;
import wsl.fw.gui.WslComboBox;
import wsl.fw.gui.GuiManager;
import wsl.fw.datasource.*;
import wsl.fw.security.Group;
import wsl.fw.util.Log;
import wsl.fw.util.Util;
import wsl.fw.help.HelpId;
import wsl.fw.resource.ResId;

import wsl.mdn.dataview.DataView;
import wsl.mdn.dataview.DataSourceDobj;
import wsl.mdn.dataview.QueryDobj;


//------------------------------------------------------------------------------
/**
 *
 */
public class NewRecordPropPanel extends PropertiesPanel
    implements ActionListener
{
    // resources
    public static final ResId LABEL_DATAVIEW      = new ResId("NewRecordPropPanel.label.DataView");
    public static final ResId LABEL_DATASTORE     = new ResId("NewRecordPropPanel.label.DataStore");
    public static final ResId LOAD_DATAVIEW_ERROR = new ResId("NewRecordPropPanel.error.DataViewLoad");
    public static final ResId MANDATORY_DATAVIEW  = new ResId("NewRecordPropPanel.mandatory.DataView");
    public static final ResId LABEL_NEW           = new ResId("NewRecordPropPanel.label.New");
    public static final ResId ERR_LOAD_DATAVIEW   = new ResId("NewRecordPropPanel.error.loadDataView");
    public static final ResId ERR_LOAD_DATASTORE  = new ResId("NewRecordPropPanel.error.loadDataStore");

    // help id
    public final static HelpId HID_NEW_RECORD = new HelpId("mdn.guiconfig.NewRecordPropPanel");

    // attributes
    private boolean _isBuilding = false;

    // controls
    private WslTextField _txtName        = new WslTextField(150);
    private WslTextField _txtDescription = new WslTextField(230);
    private WslComboBox  _cmbDataStore    = new WslComboBox(230);
    private WslComboBox  _cmbDataView    = new WslComboBox(230);


    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public NewRecordPropPanel()
    {
        // init controls
        initNewRecordPropPanelControls();

        // build the combos
        buildDataStoreCombo();
        buildDataViewCombo();
    }

    //--------------------------------------------------------------------------
    /**
     * Init the panel's controls.
     */
    private void initNewRecordPropPanelControls()
    {
        // set layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Name
        WslLabel lbl = new WslLabel(TextOrSubmenuPropertiesPanel.LABEL_NAME.getText());
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
        addMandatory(TextOrSubmenuPropertiesPanel.MANDATORY_NAME.getText(), _txtName);

        // Description
        lbl = new WslLabel(TextOrSubmenuPropertiesPanel.LABEL_DESCRIPTION.getText());
        gbc.insets.right = 0;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(lbl, gbc);
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        add(_txtDescription, gbc);

        // DataStore
        lbl = new WslLabel(ActionPropertiesPanel.LABEL_DATASTORE.getText());
        gbc.insets.right = 0;
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(lbl, gbc);
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        add(_cmbDataStore, gbc);
        _cmbDataStore.addActionListener(this);

        // DataView
        lbl = new WslLabel(ActionPropertiesPanel.LABEL_DATAVIEW.getText());
        gbc.insets.right = 0;
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(lbl, gbc);
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        gbc.weighty = 1;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        add(_cmbDataView, gbc);
        _cmbDataView.addActionListener(this);
        // note, special handling for combo mandatories in hasData
        addMandatory(MANDATORY_DATAVIEW.getText(), _cmbDataView);
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
            GuiManager.showErrorDialog(this, ERR_LOAD_DATASTORE.getText(), e);
        }
    }

    /**
     * Build DataView combo
     */
    private void buildDataViewCombo()
    {
        // set building flag
        _isBuilding = true;

        // get the DataSource
        if(_cmbDataStore.getSelectedIndex() >= 0)
        {
            DataSourceDobj ds = (DataSourceDobj)_cmbDataStore.getSelectedItem();
            if(ds != null)
            {
                // get views
                Vector views = ds.getDataViews();

                // build combo
                _cmbDataView.clear();
                _cmbDataView.buildFromVector(views);

                // set index to top
                if(_cmbDataView.getItemCount() > 0)
                    _cmbDataView.setSelectedIndex(0);
            }
        }

        // clear building flag
        _isBuilding = false;
    }

    //--------------------------------------------------------------------------
    /**
     * Action handler.
     */
    public void actionPerformed(ActionEvent ev)
    {
        try
        {
            // switch on source
            if (ev.getSource().equals(_cmbDataStore) && !_isBuilding)
            {
                buildDataViewCombo();
                setDefaultName();
            }
            else if (ev.getSource().equals(_cmbDataView) && !_isBuilding)
                setDefaultName();
        }
        catch(Exception e)
        {
            GuiManager.showErrorDialog(this,
                wsl.mdn.common.MdnAdminConst.ERR_UNHANDLED.getText(), e);
            Log.error(wsl.mdn.common.MdnAdminConst.ERR_UNHANDLED.getText(), e);
        }
    }

    /**
     * Set the default name from the query selection
     */
    private void setDefaultName()
    {
        DataView dv = (DataView)_cmbDataView.getSelectedItem();
        if(dv != null)
            _txtName.setText(LABEL_NEW.getText() + " " + dv.getName());
    }

    //--------------------------------------------------------------------------
    /**
     * Transfer data between the Category DataObject and panel controls.
     * @param toDataObject, determines the direction of the transfer.
     */
    public void transferData(boolean toDataObject)
    {
        // must have a DataObject
        MenuAction dobj = (MenuAction) getDataObject();
        Util.argCheckNull(dobj);

        if (toDataObject)
        {
            // to the Category DataObject
            dobj.setName(_txtName.getText());
            dobj.setDescription(_txtDescription.getText());

            // get the dataview id
            Object dvId = null;
            if (_cmbDataView.getSelectedItem() != null)
                dvId = new Integer(((DataView) _cmbDataView.getSelectedItem()).getId());
            NewRecord dobjNR = (NewRecord) dobj;
            dobjNR.setDataViewId(dvId);
        }
        else
        {
            // select the dataview in the combo
            NewRecord dobjNR = (NewRecord) dobj;
            if(dobjNR.getState() == DataObject.IN_DB)
            {
                DataView dv = new DataView();
                dv.setId(dobjNR.getDataViewId());
                try
                {
                    if(null != (dv = (DataView)dv.loadPolymorphic()))
                    {
                        _isBuilding = true;
                        selectComboOnId(_cmbDataStore, new Integer(dv.getSourceDsId()));
                        buildDataViewCombo();
                        selectComboOnId(_cmbDataView, dobjNR.getDataViewId());
                        _isBuilding = false;
                    }
                }
                catch(Exception e)
                {
                    GuiManager.showErrorDialog(this, ERR_LOAD_DATAVIEW.getText(), e);
                }

                // set name and desc
                _txtName.setText(dobjNR.getName());
                _txtDescription.setText(dobjNR.getDescription());
            }
            else
                setDefaultName();
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Used to select a DataView ina combo by its FLD_ID.
     * @param combo, the combo.
     * @param id, the id of the item to select.
     * @return the index of the item selected.
     */
    private int selectComboOnId(WslComboBox combo, Object id)
    {
        Util.argCheckNull(combo);

        if (id == null)
            combo.setSelectedIndex(-1);
        else
        {
            // iterate the combo
            for(int i = 0; i < combo.getItemCount(); i++)
            {
                // compare the item id
                if (((DataObject) combo.getItemAt(i)).getObjectValue(DataView.FLD_ID).equals(id))
                {
                    combo.setSelectedIndex(i);
                    return i;
                }
            }
        }

        // not found
        return -1;
    }

    //--------------------------------------------------------------------------
    /**
     * Return the preferred size
     */
    public Dimension getPreferredSize()
    {
        return new Dimension(350, 180);
    }

    //--------------------------------------------------------------------------
    /**
     * @return the ActionPropertiesPanel help id.
     */
    public HelpId getHelpId()
    {
        return HID_NEW_RECORD;
    }

    //--------------------------------------------------------------------------
    /**
     * Overrideto correctly handle checking for empty combos.
     * @return boolean true if the component contains data.
     */
    protected boolean hasData(JComponent comp)
    {
        // validate
        Util.argCheckNull(comp);

        if (comp instanceof WslComboBox)
        {
            if(((WslComboBox)comp).getSelectedItem() == null)
                return false;
        }
        return super.hasData(comp);
    }
}