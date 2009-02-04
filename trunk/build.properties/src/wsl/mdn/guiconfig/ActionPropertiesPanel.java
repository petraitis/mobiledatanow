//==============================================================================
// ActionPropertiesPanel.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

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
 * Properties panel to edit MenuActions.
 */
public class ActionPropertiesPanel
    extends PropertiesPanel
    implements ActionListener
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:35:35 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/guiconfig/ActionPropertiesPanel.java $ ";

    // resources
    public static final ResId LABEL_DATAVIEW      = new ResId("ActionPropertiesPanel.label.DataView");
    public static final ResId LABEL_QUERY         = new ResId("ActionPropertiesPanel.label.Query");
    public static final ResId LABEL_DATASTORE     = new ResId("ActionPropertiesPanel.label.DataStore");
    public static final ResId LABEL_ALL           = new ResId("ActionPropertiesPanel.label.All");
    public static final ResId LOAD_DATAVIEW_ERROR = new ResId("ActionPropertiesPanel.error.DataViewLoad");
    public static final ResId LOAD_QUERY_ERROR    = new ResId("ActionPropertiesPanel.error.QueryDobjLoad");
    public static final ResId MANDATORY_QUERY     = new ResId("ActionPropertiesPanel.mandatory.Query");
    public static final ResId MANDATORY_DATAVIEW  = new ResId("ActionPropertiesPanel.mandatory.DataView");
    public static final ResId ERR_LOAD_DATASTORE  = new ResId("ActionPropertiesPanel.error.loadDataStore");
    // help id
    public final static HelpId HID_ACTION_PROPPANEL = new HelpId("mdn.guiconfig.ActionPropertiesPanel");

    // attributes
    private boolean _isBuilding = false;

    // controls
    private WslTextField _txtName        = new WslTextField(150);
    private WslTextField _txtDescription = new WslTextField(230);
    private WslComboBox  _cmbDataStore    = new WslComboBox(230);
    private WslComboBox  _cmbDataView    = new WslComboBox(230);
    private JLabel       _lblQuery       = new JLabel(LABEL_QUERY.getText());
    private WslComboBox  _cmbQuery       = new WslComboBox(230);


    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public ActionPropertiesPanel()
    {
        // init controls
        initActionPropertiesPanelControls();

        // build the combos
        buildDataStoreCombo();
        buildDataViewCombo();
        buildQueryCombo();
    }

    //--------------------------------------------------------------------------
    /**
     * Init the panel's controls.
     */
    private void initActionPropertiesPanelControls()
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

        // DataView
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
        add(_cmbDataView, gbc);
        _cmbDataView.addActionListener(this);
        // note, special handling for combo mandatories in hasData
        addMandatory(MANDATORY_DATAVIEW.getText(), _cmbDataView);

        // Query
        gbc.insets.right = 0;
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(_lblQuery, gbc);
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        add(_cmbQuery, gbc);
        _cmbQuery.addActionListener(this);
        // note, special handling for combo mandatories in hasData
        addMandatory(MANDATORY_QUERY.getText(), _cmbQuery);

        // placeholder to fill area even when Query is hidden
        gbc.insets.right = 0;
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(new JLabel(), gbc);
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        gbc.weighty = 1;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        add(new JLabel(), gbc);
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

    /**
     * Build queries combo
     */
    private void buildQueryCombo()
    {
        // set build flag
        _isBuilding = true;

        // clear combo
        _cmbQuery.clear();
        DataView dv = (DataView) _cmbDataView.getSelectedItem();
        if (dv != null)
        {
            // build
            _cmbQuery.buildFromVector(dv.getQueries());

            // set index to top
            if(_cmbQuery.getModel().getSize() > 0)
                _cmbQuery.setSelectedIndex(0);

            // set the default name
            setDefaultName();
        }

        // clear build flag
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
                buildQueryCombo();
            }
            else if (ev.getSource().equals(_cmbDataView) && !_isBuilding)
                buildQueryCombo();
            else if (ev.getSource().equals(_cmbQuery) && !_isBuilding)
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
        Object obj = _cmbQuery.getSelectedItem();
        if(obj != null)
            _txtName.setText(obj.toString());
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

            // set the id
            QueryRecords dobjQR = (QueryRecords) dobj;
            Object qId = null;
            if (_cmbQuery.getSelectedItem() != null)
                qId = new Integer(((QueryDobj) _cmbQuery.getSelectedItem()).getId());
            dobjQR.setQueryId(qId);
            dobjQR.setDataViewId(dvId);
        }
        else
        {
            QueryRecords dobjQR = (QueryRecords) dobj;
            if (dobjQR != null)
            {
                // update
                if (dobjQR.getQueryId() != null)
                {
                    Object queryId = dobjQR.getQueryId();
                    // load the query dobj
                    QueryDobj queryDobj = new QueryDobj();
                    queryDobj.setValue(QueryDobj.FLD_ID, queryId);
                    try
                    {
                        if (null != (queryDobj = (QueryDobj)queryDobj.loadPolymorphic()))
                        {
                            // select the appropriate datastore in combo
                            int dsid = queryDobj.getDataView(true).getSourceDsId();
                            selectComboOnId(_cmbDataStore,
                                new Integer(dsid));

                            // select the appropriate dataView in combo
                            selectComboOnId(_cmbDataView,
                                new Integer(queryDobj.getViewOrTableId()));

                            // select the query in the combo
                            selectComboOnId(_cmbQuery, queryId);
                        }

                    }
                    catch (DataSourceException e)
                    {
                        GuiManager.showErrorDialog(this, LOAD_QUERY_ERROR.getText(), e);
                    }

                    // to the controls
                    _txtName.setText(dobj.getName());
                    _txtDescription.setText(dobj.getDescription());
                }
            }
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Used to select a DataView or QueryDobj ina combo by its FLD_ID.
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
        return HID_ACTION_PROPPANEL;
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

//==============================================================================
// end of file ActionPropertiesPanel.java
//==============================================================================
