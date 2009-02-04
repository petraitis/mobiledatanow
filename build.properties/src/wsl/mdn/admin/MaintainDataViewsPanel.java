//==============================================================================
// MaintainDataViewsPanel.java
// Copyright (c) 2001 WAP Solutions Ltd.
//==============================================================================

package wsl.mdn.admin;

// imports
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.resource.ResId;
import wsl.fw.help.HelpId;
import wsl.fw.datasource.*;
import wsl.fw.gui.*;
import wsl.mdn.common.MdnAdminConst;
import wsl.mdn.common.MdnDataManager;
import wsl.mdn.dataview.*;

//------------------------------------------------------------------------------
/**
 * Panel used to maintain DataViews.
 */
public class MaintainDataViewsPanel extends WslButtonPanel
    implements ActionListener, TreeExpansionListener, TreeSelectionListener,
    WizardClosedListener
{
    // resources
    public static final ResId BUTTON_ADD_DATAVIEW  =
        new ResId("MaintainDataViewsPanel.button.AddDataView");
    public static final ResId BUTTON_ADD_QUERY  =
        new ResId("MaintainDataViewsPanel.button.AddQuery");
    public static final ResId BUTTON_ADD_FIELD  =
        new ResId("MaintainDataViewsPanel.button.AddField");
    public static final ResId BUTTON_REMOVE  =
        new ResId("MaintainDataViewsPanel.button.Remove");
    public static final ResId BUTTON_PROPERTIES  =
        new ResId("MaintainDataViewsPanel.button.Properties");
    public static final ResId BUTTON_SET_NAMING  =
        new ResId("MaintainDataViewsPanel.button.SetNaming");
    public static final ResId BUTTON_CLOSE  =
        new ResId("MaintainDataViewsPanel.button.Close");
    public static final ResId BUTTON_EXEC  =
        new ResId("MaintainDataViewsPanel.button.Exec");
    public static final ResId TREE_NAME  =
        new ResId("MaintainDataViewsPanel.tree");
    public static final ResId PANEL_TITLE  =
        new ResId("MaintainDataViewsPanel.title");
    public static final ResId BUTTON_NEW_SQL_QUERY  =
        new ResId("MaintainDataViewsPanel.button.NewSQLQuery");
    public static final ResId BUTTON_HELP =
        new ResId("OkPanel.button.Help");

    public static final ResId ERR_BUILD_TREE =
        new ResId("MaintainDataViewsPanel.error.buildTree");
    public static final ResId ERR_SAVE_DATAVIEW =
        new ResId("MaintainDataViewsPanel.error.saveDataView");
    public static final ResId ERR_SAVE_QUERY =
        new ResId("MaintainDataViewsPanel.error.saveQuery");

    public final static HelpId HID_MAINT_DATAVIEW = new HelpId("mdn.admin.MaintainDataViewsPanel");

    // constants
    private static final int BTN_WIDTH = 132;

    // attributes
    private WslButton _btnAddDataView = new WslButton(BUTTON_ADD_DATAVIEW.getText(), BTN_WIDTH, this);
    private WslButton _btnAddQuery = new WslButton(BUTTON_ADD_QUERY.getText(), BTN_WIDTH, this);
    private WslButton _btnDirectSQL = new WslButton(BUTTON_NEW_SQL_QUERY.getText(), BTN_WIDTH, this);
    // private WslButton _btnAddField = new WslButton(BUTTON_ADD_FIELD.getText(), BTN_WIDTH, this);
    private WslButton _btnRemove = new WslButton(BUTTON_REMOVE.getText(), BTN_WIDTH, this);
    private WslButton _btnProperties = new WslButton(BUTTON_PROPERTIES.getText(), BTN_WIDTH, this);
    private WslButton _btnSetNaming = new WslButton(BUTTON_SET_NAMING.getText(), BTN_WIDTH, this);
    private WslButton _btnClose = new WslButton(BUTTON_CLOSE.getText(), BTN_WIDTH, this);
    private WslButton _btnExec = new WslButton(BUTTON_EXEC.getText(), BTN_WIDTH, this);
    private WslButton _btnTest = new WslButton("Test", BTN_WIDTH, this);
    private DataObjectTree _tree;
    private boolean _isBuildingTree = false;
    private DataViewWizard _viewWiz = null;
    private QueryWizardPanel _queryWiz = null;


    //--------------------------------------------------------------------------
    /**
     * Blank constructor.
     */
    public MaintainDataViewsPanel()
    {
        // super
        super(WslButtonPanel.VERTICAL);

        // set title
        setPanelTitle(PANEL_TITLE.getText());

        // init controls
        initMaintainDataViewsPanelControls();

        // build the tree
        buildTree();

        // update buttons
        updateButtons();
    }

    //--------------------------------------------------------------------------
    /**
     * Initialise controls.
     */
    private void initMaintainDataViewsPanelControls()
    {
        // add buttons
        addButton(_btnAddDataView);
        addButton(_btnAddQuery);
        addButton(_btnDirectSQL);
        //addButton(_btnAddField);
        addButton(_btnRemove);
        addButton(_btnProperties);
        addButton(_btnSetNaming);
        addButton(_btnExec);
        addHelpButton(BUTTON_HELP.getText(), HID_MAINT_DATAVIEW, BTN_WIDTH);
        _btnClose.setIcon(Util.resourceIcon(GuiConst.FW_IMAGE_PATH + "close.gif"));
        addButton(_btnClose);
        // addCustomButton(_btnTest);

        // create the tree
        _tree = new DataObjectTree(TREE_NAME.getText());
        _tree.addTreeExpansionListener(this);
        _tree.addTreeSelectionListener(this);
        _tree.addListener(new DataListenerData(null, DataSourceDobj.ENT_DATASOURCE, null));
        _tree.addListener(new DataListenerData(null, DataView.ENT_DATAVIEW, null));
        _tree.addListener(new DataListenerData(null, DataViewField.ENT_DVFIELD, null));
        _tree.addListener(new DataListenerData(null, QueryDobj.ENT_QUERY, null));
        JScrollPane sp = new JScrollPane(_tree);

        // add the tree to the main panel
        getMainPanel().setBorder(BorderFactory.createLoweredBevelBorder());
        getMainPanel().setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets.top = GuiConst.DEFAULT_INSET;
        gbc.insets.left = GuiConst.DEFAULT_INSET;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        gbc.weightx = 1;
        gbc.weighty = 1;
        getMainPanel().add(sp, gbc);
    }

    //--------------------------------------------------------------------------
    /**
     * Build the Category and Product tree.
     */
    private void buildTree()
    {
        // set is building flag
        _isBuildingTree = true;

        // clear tree
        _tree.clear();

        // build the tree staring at the root
        try
        {
            buildNode(_tree.getRoot());
        }
        catch(Exception e)
        {
            GuiManager.showErrorDialog(this, ERR_BUILD_TREE.getText(), e);
        }

        // refresh the tree to ensure proper display
        _tree.refreshModel();

        // set is building flag
        _isBuildingTree = false;
    }

    //--------------------------------------------------------------------------
    /**
     * Build a node of the tree.
     */
    private void buildNode(DoTreeNode parentNode) throws DataSourceException
    {
        // set is building flag
        _isBuildingTree = true;

        // clear the node
        try
        {
            Util.argCheckNull(parentNode);
            _tree.clearNode(parentNode);

            // get parent data object and key
            DataObject doParent = parentNode.getDataObject();

            // if the doParent is null, then it is the root
            if(doParent == null)
            {
                // select all datasources
                DataSource ds = DataManager.getSystemDS();
                Query q = new Query(DataSourceDobj.ENT_DATASOURCE);
                QueryCriterium qc = new QueryCriterium(JdbcDataSourceDobj.ENT_DATASOURCE,
                    JdbcDataSourceDobj.FLD_IS_MIRROR_DB, QueryCriterium.OP_EQUALS,
                    new Boolean(false));
                qc.setOrIsNull(true);
                q.addQueryCriterium(qc);
                RecordSet rs = ds.select(q);

                // build the root node
                _tree.buildFromRecordSet(rs, parentNode);
            }

            // if the doParent is datasource, build dataviews
            else if(doParent instanceof DataSourceDobj)
            {
                // add queries
                _tree.buildFromVector(((DataSourceDobj)doParent).getDataViews(),
                    parentNode, true);
            }

            // else, building a dataview
            else if(doParent instanceof DataView)
            {
                // add queries
                _tree.buildFromVector(((DataView)doParent).getQueries(),
                    parentNode, false);

                // add fields
                _tree.buildFromVector(((DataView)doParent).getFields(),
                    parentNode, false);
            }
        }
        finally
        {
            _isBuildingTree = false;
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Tree selection changed.
     */
    public void valueChanged(TreeSelectionEvent ev)
    {
        updateButtons();
    }

    //--------------------------------------------------------------------------
    /**
     * Tree node expanded.
     */
    public void treeExpanded(TreeExpansionEvent ev)
    {
        // if building, out
        if(_isBuildingTree)
            return;

        // get the expanded node
        DoTreeNode node = (DoTreeNode)ev.getPath().getLastPathComponent();
        if(node != null)
        {
            try
            {
                // build the node
                buildNode(node);
            }
            catch(Exception e)
            {
                GuiManager.showErrorDialog(this, ERR_BUILD_TREE.getText(), e);
            }
        }

        // update buttons
        updateButtons();
    }

    //--------------------------------------------------------------------------
    /**
     * Tree node collapsed.
     */
    public void treeCollapsed(TreeExpansionEvent event)
    {
        // if building, out
        if(_isBuildingTree)
            return;
    }

    //--------------------------------------------------------------------------
    /**
     * Button clicked.
     */
    public void actionPerformed(ActionEvent ev)
    {
        try
        {
            // switch on source
            if(ev.getSource().equals(_btnClose))
                closePanel();
            else if(ev.getSource().equals(_btnAddDataView))
                onAddDataView();
            else if(ev.getSource().equals(_btnAddQuery))
                onAddQuery();
            /*
            else if(ev.getSource().equals(_btnAddField))
                onAddField();
            */
            else if(ev.getSource().equals(_btnRemove))
                onRemove();
            else if(ev.getSource().equals(_btnProperties))
                onProperties();
            else if(ev.getSource().equals(_btnSetNaming))
                onSetNaming();
            else if(ev.getSource().equals(_btnExec))
                onExec();
            else if (ev.getSource().equals(_btnDirectSQL))
                onDirectSQL();
            else if(ev.getSource().equals(_btnTest))
                onTest();

            // update buttons
            updateButtons();
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
     * Panel closing.
     */
    public void onClosePanel()
    {
        // remove data listeners
        _tree.removeAllListeners();

        // super
        super.onClosePanel();
    }

    //--------------------------------------------------------------------------
    /**
     * Add DataSource button clicked.
     */
    public void onAddDataView() throws DataSourceException
    {
        // create the wizard controller
        _viewWiz = new DataViewWizard();

        // set the selected ds
        DataObject dobj = _tree.getSelectedDataObject();
        if(dobj != null)
        {
            if (dobj instanceof DataView)
                _viewWiz.setExistingDataView((DataView)dobj);
            else if (dobj instanceof DataSourceDobj)
                _viewWiz.setExistingDataSourceDobj((DataSourceDobj)dobj);
        }

        // add close listener
        _viewWiz.addCloseListener(this);

        // create framing dialog and show
        GuiManager.openWslPanel(this.getFrameParent(), _viewWiz, true);
    }

    //--------------------------------------------------------------------------
    /**
     * DataStore import wizard closed
     */
    public void wizardClosed(boolean bFinished)
    {
        // is closing
        if(isClosing())
            return;

        // must be finished
        if(bFinished)
        {
            // DataViewWizard
            if(_viewWiz != null)
            {
                // get the view from the wizard
                DataView dv = (DataView)_viewWiz.getDataView();
                if(dv != null)
                {
                    try
                    {
                        // save the view
                        dv.save();

                        // if a new view add it to the tree
                        DataObject dobj = _tree.getSelectedDataObject();
                        boolean isExisting = dobj != null && dobj instanceof DataView &&
                            ((DataView)dobj) == dv;
                        if(!isExisting)
                        {
                            // find the datastore
                            int dsid = dv.getSourceDsId();
                            DoTreeNode dsNode = _tree.findDataObject(DataSourceDobj.class,
                                DataSourceDobj.FLD_ID, new Integer(dsid));

                            // if found rebuild node
                            if(dsNode != null)
                            {
                                // get the datasource
                                DataSourceDobj dsDobj = (DataSourceDobj)_tree.getDataObject(dsNode);

                                // if views already loaded then add the new
                                // view to the datasourcedobj, else load the
                                // views which will also get the new view as it
                                // has already been saved to the db
                                if (dsDobj.areViewsLoaded())
                                    dsDobj.addDataView(dv);
                                else
                                    dsDobj.getDataViews();

                                // update the tree
                                buildNode(dsNode);
                            }
                        }
                        else
                            buildNode(_tree.getSelectedNode());
                    }
                    catch(Exception e)
                    {
                        GuiManager.showErrorDialog(this, ERR_SAVE_DATAVIEW.getText(), e);
                    }
                }

                // clear wiz
                _viewWiz = null;
            }

            // query wizard
            else if(_queryWiz != null)
            {
                // get the query from the wizard
                QueryDobj q = (QueryDobj)_queryWiz.getQuery();
                if(q != null)
                {
                    try
                    {
                        // save the query
                        q.save();

                        // if a new query add it to the tree
                        boolean doAddNode = false;
                        DataObject dobj = _tree.getSelectedDataObject();
                        if(dobj != null && !_queryWiz.isNewQuery())
                        {
                            // get the dv parent
                            DoTreeNode parentNode = (DoTreeNode)_tree.getSelectedNode().getParent();
                            DataView parentDv = (DataView)_tree.getDataObject(parentNode);

                            // if ! the same parent remove and add
                            if(q.getDataView().getId() != parentDv.getId())
                            {
                                parentDv.removeQuery(q, false);
                                buildNode(parentNode);
                                doAddNode = true;
                            }
                        }
                        else
                            doAddNode = true;

                        // add a new node
                        if(doAddNode)
                        {
                            // get the view belonging to the query
                            int dvid = q.getDataView().getId();
                            DoTreeNode dvNode = _tree.findDataObject(DataView.class,
                                DataView.FLD_ID, new Integer(dvid));

                            // if found rebuild node
                            if(dvNode != null)
                            {
                                // get data view
                                DataView parentDv = (DataView)_tree.getDataObject(dvNode);


                                // if queries already loaded then add the new
                                // query to the view, else load the queries
                                // which will also get the new query as it
                                // has already been saved to the db
                                if (parentDv.areQueriesLoaded())
                                    parentDv.addQuery(q);
                                else
                                    parentDv.getQueries();

                                // update the tree
                                buildNode(dvNode);
                            }
                        }
                    }
                    catch(Exception e)
                    {
                        GuiManager.showErrorDialog(this, ERR_SAVE_QUERY.getText(), e);
                    }
                }

                // clear wiz
                _queryWiz = null;
            }
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Add Query button clicked.
     */
    public void onAddQuery() throws DataSourceException
    {
        // create the wizard controller
        _queryWiz = new QueryWizardPanel();

        // set the selected ds
        DataObject dobj = _tree.getSelectedDataObject();
        if(dobj != null && dobj instanceof QueryDobj)
            _queryWiz.setExistingQuery((QueryDobj)dobj);
        else if(dobj != null && dobj instanceof DataView)
            _queryWiz.setParentDataView((DataView)dobj);

        // add close listener
        _queryWiz.addCloseListener(this);

        // create framing dialog and show
        GuiManager.openWslPanel(this.getFrameParent(), _queryWiz, true);
    }

    //--------------------------------------------------------------------------
    /**
     * Add Field button clicked.
     */
    /*
    public void onAddField() throws DataSourceException
    {
        // get the selected entity
        DataObject dobj = _tree.getSelectedDataObject();
        if(dobj != null && dobj instanceof DataView)
        {
            // cast the view
            DataView view = (DataView)dobj;

            // create the field
            DataViewField f = new DataViewField();
            f.setSourceDataSource(view.getSourceDataSource());

            // open the properties panel
            OkCancelPanel panel = GuiManager.openOkCancelPanel(
                this.getFrameParent(), f, true, false);

            // if Ok clicked
            if(panel.isOk())
            {
                // add it to the entity and save
                // fixme, this will ned to be corrected for field loading
                // if this code is reenabled
                view.addField(f);
                view.save();

                // add to tree
                _tree.addNode(_tree.getSelectedNode(), f, false);
            }
        }
    }
    */

    //--------------------------------------------------------------------------
    /**
     * Remove button clicked.
     */
    public void onRemove() throws DataSourceException
    {
        // get the selected object
        DataObject dobj = _tree.getSelectedDataObject();

        // if it is IN_DB delete it
        if(dobj != null && dobj.getState() == DataObject.IN_DB)
        {
            // get parent
            DoTreeNode selNode = _tree.getSelectedNode();
            DoTreeNode parentNode = (DoTreeNode)selNode.getParent();
            DataObject parentDobj = _tree.getDataObject(parentNode);

            if(parentDobj == null)
            {
                // if parent not found just delete
                dobj.delete();
            }
            else if (dobj instanceof DataView)
            {
                // if view remove from from DataSourceDobj
                ((DataSourceDobj)parentDobj).removeDataView((DataView)dobj, true);
            }
            else if(dobj instanceof DataViewField)
            {
                // else if field remove from view
                ((DataView)parentDobj).removeField((DataViewField)dobj, true);
            }
            else if(dobj instanceof QueryDobj)
            {
                // else if query remove from view
                ((DataView)parentDobj).removeQuery((QueryDobj)dobj, true);
            }
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Properties button clicked.
     */
    public void onProperties() throws DataSourceException
    {
        // get the selected datasource
        DataObject dobj = _tree.getSelectedDataObject();
        if(dobj != null)
        {
            // if it is a DirectQueryDobj then set the parent dataview
            if(dobj instanceof DirectQueryDobj)
            {
                // get the parent
                DoTreeNode node = _tree.getSelectedNode();
                DoTreeNode parentNode = (DoTreeNode)node.getParent();
                DirectQueryDataView dv = (DirectQueryDataView)_tree.getDataObject(parentNode);
                dobj = dv;
            }
            // if a query, use the wizard
            else if(dobj instanceof QueryDobj)
            {
                onAddQuery();
                return;
            }

            // if it is a DataViewField then set the ds
            else if(dobj instanceof DataViewField)
            {
                // get the fields parent
                DoTreeNode node = _tree.getSelectedNode();
                DoTreeNode parentNode = (DoTreeNode)node.getParent();
                DataView dv = (DataView)_tree.getDataObject(parentNode);
                DataSourceDobj ds = dv.getSourceDataSource();

                // set the ds into the field
                ((DataViewField)dobj).setSourceDataSource(ds);
            }

            // open the panel
            OkCancelPanel panel = GuiManager.openOkCancelPanel(this.getFrameParent(), dobj, true);

            if (dobj instanceof DirectQueryDataView && panel.isOk())
            {
                // get the view belonging to the query
                int dvid = ((DirectQueryDataView)dobj).getId();
                DoTreeNode dvNode = _tree.findDataObject(DirectQueryDataView.class,
                    DataView.FLD_ID, new Integer(dvid));

                // if found rebuild node
                if(dvNode != null)
                {
//                    _tree.getSelectedNode().add(dvNode);
                    buildNode(dvNode);
                }
            }
        }
    }

    //--------------------------------------------------------------------------
    /**
     * SetNaming button clicked.
     */
    private void onSetNaming() throws DataSourceException
    {
        TreePath[] selectedPaths = _tree.getSelectionPaths();
        if (selectedPaths != null)
        {
            int n = selectedPaths.length;
            for (int i = 0; i < n; i++)
            {
                DefaultMutableTreeNode lastNode = (DefaultMutableTreeNode)
                    ((TreePath)selectedPaths[i]).getLastPathComponent();
                if (lastNode != null && !lastNode.isRoot())
                {
                    // get the selected datasource
                    //DataObject dobj = _tree.getSelectedDataObject();
                    DataObject dobj = (DataObject)lastNode.getUserObject();
                    if(dobj != null)
                    {
                        // if it is a DataViewField then set the ds
                        if(dobj instanceof DataViewField)
                        {
                            DataViewField dvf = (DataViewField)dobj;
                            if (dvf.hasFlag(Field.FF_NAMING))
                                dvf.setFlags(dvf.getFlags() & ~Field.FF_NAMING);
                            else
                                dvf.setFlags(dvf.getFlags() | Field.FF_NAMING);
                            dvf.save();
                        } //if(dobj instanceof DataViewField)
                    } //if(dobj != null)
                } //if (lastNode != null)
            } //for (int i = 0; i < n; i++)
        } //if (selectedPaths != null)
    }

    //--------------------------------------------------------------------------
    /**
     * Execute Query button clicked
     */
    public void onExec() throws DataSourceException
    {
        // get the selected query
        QueryDobj q = (QueryDobj)_tree.getSelectedDataObject();
        DataView dv = (DataView)_tree.getDataObject((DefaultMutableTreeNode)_tree.getSelectedNode().getParent());

        if (q != null)
        {
            ExecuteDataViewsPanel execPanel = new ExecuteDataViewsPanel(q);
            GuiManager.openWslPanel(getFrameParent(), execPanel, true);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Update controls
     */
    public void updateButtons()
    {
        // flags
        DataObject selDobj = _tree.getSelectedDataObject();
        boolean hasDobj = selDobj != null;
        boolean isView = hasDobj && selDobj instanceof DataView;
        boolean isQuery = hasDobj && selDobj instanceof QueryDobj;
        boolean isDataSource = hasDobj && selDobj instanceof DataSourceDobj;
        //boolean isDataViewField = hasDobj && selDobj instanceof DataViewField;
        boolean isDataViewField = isAnyDataViewFieldDobjSelected();
        boolean isDirect = selDobj instanceof DirectQueryDobj || selDobj instanceof DirectQueryDataView;

        // enable
        _btnAddDataView.setEnabled(!isDirect);
        _btnAddQuery.setEnabled(!isDirect);
        // _btnAddField.setEnabled(isView);
        _btnRemove.setEnabled(hasDobj && !isDataSource);
        _btnProperties.setEnabled(hasDobj);
        _btnSetNaming.setEnabled(isDataViewField);
        _btnExec.setEnabled(isQuery);
        _btnDirectSQL.setEnabled(isDataSource);
    }

    //--------------------------------------------------------------------------
    /**
     * @return true if a dataview field is on the selected path.
     */
    private boolean isAnyDataViewFieldDobjSelected()
    {
        TreePath[] selectedPaths = _tree.getSelectionPaths();
        if (selectedPaths != null)
        {
            int n = selectedPaths.length;
            for (int i = 0; i < n; i++)
            {
                DefaultMutableTreeNode lastNode = (DefaultMutableTreeNode)
                    ((TreePath)selectedPaths[i]).getLastPathComponent();
                if (lastNode != null && !lastNode.isRoot())
                {
                    // get the selected datasource
                    //DataObject dobj = _tree.getSelectedDataObject();
                    DataObject dobj = (DataObject)lastNode.getUserObject();
                    if(dobj != null)
                    {
                        // if it is a DataViewField then set the ds
                        if(dobj instanceof DataViewField)
                        {
                            return true;
                        } //if(dobj instanceof DataViewField)
                    } //if(dobj != null)
                } //if (lastNode != null)
            } //for (int i = 0; i < n; i++)
        } //if (selectedPaths != null)
        return false;
    }

    //--------------------------------------------------------------------------
    /**
     * Direct SQL button pressed.
     */
    public void onDirectSQL()
    {
        DataObject selDobj = _tree.getSelectedDataObject();
        Util.argCheckNull(selDobj);

        if (selDobj instanceof DataSourceDobj)
        {
            DataSourceDobj ds = (DataSourceDobj)selDobj;

            DirectQueryDataView dv = new DirectQueryDataView();
            dv.setSourceDataSource(ds);
            dv.setSourceDsId(ds.getId());

            DirectSQLPanel directSQLPanel = new DirectSQLPanel();
            directSQLPanel.setDataObject(dv);

            OkCancelPanel okCancel = GuiManager.openOkCancelPanel(getFrameParent(), (PropertiesPanel)directSQLPanel, "Direct SQL Query", true, true);
            if (okCancel.isOk())
            {
                // if views already loaded then add the new
                // direct sql view to the datasourcedobj, else load the
                // views which will also get the new direct sql view as it
                // has already been saved to the db
                if (ds.areViewsLoaded())
                    ds.addDataView(dv);
                else
                    ds.getDataViews();

                // update the tree
                try
                {
                    buildNode(_tree.getSelectedNode());
                }
                catch (DataSourceException e)
                {
                    GuiManager.showErrorDialog(this, ERR_BUILD_TREE.getText(), e);
                }
            }
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Preferred size
     */
    public Dimension getPreferredSize()
    {
        return MdnAdminConst.DEFAULT_PANEL_SIZE;
    }


    //--------------------------------------------------------------------------
    // Test

    /**
     * Test MdnDataCache
     */
    private void onTest() throws Exception
    {
        // get the data cache
        MdnDataCache cache = MdnDataCache.getCache();

        // get current dobj
        DataObject dobj = _tree.getSelectedDataObject();
        if(dobj != null && dobj instanceof DataView)
        {
            DataView dv = cache.getDataView(dobj.getUniqueKey());
            Log.debug("Found DataObject: " + dv);
        }
    }

    /**
     * Test Insert button clicked
     */
    public void onTestInsert() throws DataSourceException
    {
        // get the selected view
        DataView dv = (DataView)_tree.getSelectedDataObject();

        // create a record
        Record rec = new Record(dv);

        // set attribs
        rec.setValue("TBL_CHILD.FLD_NAME", "Test Insert Name");

        // save
        rec.save();

        // update and save
        rec.setValue("TBL_CHILD.FLD_NUMBER", new Integer(33445566));
        rec.save();

        // delete it
        rec.delete();
    }
}

//==============================================================================
// end of file MaintainDataViewsPanel.java
//==============================================================================

