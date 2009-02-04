package wsl.mdn.admin;

// imports
import java.lang.Integer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.JScrollPane;
import javax.swing.Icon;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import wsl.fw.gui.*;
import wsl.fw.resource.ResId;
import wsl.fw.gui.DataObjectTree;
import wsl.fw.gui.DoTreeNode;
import wsl.fw.util.Log;
import wsl.fw.util.Util;
import wsl.fw.util.Type;
import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.DataManager;
import wsl.fw.datasource.DataSource;
import wsl.fw.datasource.DataSourceException;
import wsl.fw.security.Group;
import wsl.fw.datasource.Query;
import wsl.fw.datasource.QueryCriterium;
import wsl.fw.datasource.RecordSet;
import wsl.fw.help.HelpId;
import wsl.mdn.common.MdnAdminConst;
import wsl.mdn.dataview.DataSourceDobj;
import wsl.mdn.dataview.EntityDobj;
import wsl.mdn.dataview.DataView;
import wsl.mdn.dataview.GroupDataView;
import wsl.mdn.dataview.FieldExclusion;
import wsl.mdn.dataview.DataViewField;

//------------------------------------------------------------------------------
/**
 * TabPane for Data View Permissions maintenance.
 */
public class DataViewPermissionsTabPane
    extends WslTabChildPanel
    implements ActionListener, TreeExpansionListener, TreeSelectionListener
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:35:35 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/admin/DataViewPermissionsTabPane.java $ ";

    // resources
    public static final ResId BUTTON_ENABLE  = new ResId("DataViewPermissionsTabPane.button.Enable");
    public static final ResId BUTTON_DISABLE  = new ResId("DataViewPermissionsTabPane.button.Disable");
    public static final ResId BUTTON_PROPERTIES  = new ResId("DataViewPermissionsTabPane.button.Properties");
    public static final ResId TAB_DATA_VIEW_PERMISSIONS  = new ResId("DataViewPermissionsTabPane.tab.Permissions");
    public static final ResId NODE_GROUPS  = new ResId("DataViewPermissionsTabPane.node.Groups");
    public static final ResId ERR_BUILDTREE = new ResId("DataViewPermissionsTabPane.error.buildTree");
    public static final ResId ERR_GDV_INSERT = new ResId("DataViewPermissionsTabPane.error.insertGroupDataView");
    public static final ResId ERR_FIELD_EXCLUSION_SELECT = new ResId("DataViewPermissionsTabPane.error.selectFieldExclusion");
    public static final ResId ERR_DISABLE = new ResId("DataViewPermissionsTabPane.error.disable");

    public static final Icon ICON_ENABLED = Util.resourceIcon(MdnAdminConst.SS_IMAGE_PATH + "enabled.gif");
    public static final Icon ICON_DISABLED = Util.resourceIcon(MdnAdminConst.SS_IMAGE_PATH + "disabled.gif");

    public final static HelpId HID_DATAVIEW_PERMISSIONS = new HelpId("mdn.admin.DataViewPermissionsTabPane");

    // attributes
    MdnUserGroupMaintenancePanel _tabParent;
    private DataObjectTree    _tree;
    private TreePath[] _selectedPaths = new TreePath[0];

    // controls
    private WslButton _btnEnable   = new WslButton(BUTTON_ENABLE.getText(), this);
    private WslButton _btnDisable   = new WslButton(BUTTON_DISABLE.getText(), this);
    private WslButton _btnProperties   = new WslButton(BUTTON_PROPERTIES.getText(), this);

    private WslButton _buttons[] = { _btnEnable, _btnDisable, _btnProperties };

    private boolean _isBuildingTree = false;


    //--------------------------------------------------------------------------
    /**
     * Creates tab for parent panel of UserGroupMaintenancePanel.
     * @param tabParent Parent panel of UserGroupMaintenancePanel.
     */
    public DataViewPermissionsTabPane(MdnUserGroupMaintenancePanel tabParent)
    {
        super(TAB_DATA_VIEW_PERMISSIONS.getText());

        _tabParent = tabParent;


        // add controls
        createControls();

        // build the tree
        buildTree();

        // update buttons
        updateButtons();
    }

    //--------------------------------------------------------------------------
    /**
     * Create the tree control.
     */
    private void createControls()
    {
        // create the tree and scroller
        //_tree = new DataObjectTree(NODE_GROUPS.getText());
        _tree = new DataObjectTree();
        _tree.addTreeExpansionListener(this);
        _tree.addTreeSelectionListener(this);
        JScrollPane sp = new JScrollPane(_tree);

        sp.setPreferredSize(new Dimension(500, 500));

        _tree.setSort(new DoComparator());


        // add the tree to the panel
        // setBorder(BorderFactory.createLoweredBevelBorder());
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets.top = GuiConst.DEFAULT_INSET;
        gbc.insets.left = GuiConst.DEFAULT_INSET;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        gbc.weightx = 1;
        gbc.weighty = 1;
        add(sp, gbc);
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
            GuiManager.showErrorDialog(this, ERR_BUILDTREE.getText(), e);
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

        Icon iconEnabled = Util.resourceIcon(MdnAdminConst.SS_IMAGE_PATH + "enabled.gif");
        Icon iconDisabled = Util.resourceIcon(MdnAdminConst.SS_IMAGE_PATH + "disabled.gif");

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
                // build root node
                // select all groups
                DataSource ds = DataManager.getSystemDS();
                Query q = new Query(Group.ENT_GROUP);
                RecordSet rs = ds.select(q);

                // build the root node
                _tree.buildFromRecordSet(rs, parentNode);
            }

            // else, building a Group node
            else if(doParent instanceof Group)
            {
                buildGroupNode(parentNode);
            }

            // if the doParent is datasource, build dataviews
            else if(doParent instanceof DataSourceDobj)
            {
                buildDataSourceDobjNode(parentNode);
            }

            // else, building an DataView node
            else if(doParent instanceof DataView)
            {
                buildDataViewNode(parentNode);
            }
        }
        finally
        {
            _isBuildingTree = false;
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Building a Group node.
     * @param parentNode Group node.
     */
    private void buildGroupNode(DoTreeNode parentNode) throws DataSourceException
    {
        // set is building flag
        _isBuildingTree = true;

        //Icon iconEnabled = Util.resourceIcon(MdnAdminConst.SS_IMAGE_PATH + "enabled.gif");
        //Icon iconDisabled = Util.resourceIcon(MdnAdminConst.SS_IMAGE_PATH + "disabled.gif");
        Icon iconStore = Util.resourceIcon(MdnAdminConst.SS_IMAGE_PATH + "db.gif");

        // clear the node
        try
        {
                // select all datasources
                DataSource ds = DataManager.getSystemDS();
                Query q = new Query(DataSourceDobj.ENT_DATASOURCE);
                QueryCriterium qc = new QueryCriterium(DataSourceDobj.ENT_DATASOURCE,
                    DataSourceDobj.FLD_IS_MIRROR_DB, QueryCriterium.OP_EQUALS,
                    new Boolean(false));
                qc.setOrIsNull(true);
                q.addQueryCriterium(qc);
                RecordSet rs = ds.select(q);

                // build the root node
                _tree.buildFromRecordSet(rs, parentNode);
        }
        finally
        {
            _isBuildingTree = false;
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Building a Group node.
     * @param parentNode Group node.
     */
    private void buildDataSourceDobjNode(DoTreeNode parentNode) throws DataSourceException
    {
        // set is building flag
        _isBuildingTree = true;

        Icon iconEnabled = Util.resourceIcon(MdnAdminConst.SS_IMAGE_PATH + "enabled.gif");
        Icon iconDisabled = Util.resourceIcon(MdnAdminConst.SS_IMAGE_PATH + "disabled.gif");

        try
        {
            // clear the node
            Util.argCheckNull(parentNode);
            _tree.clearNode(parentNode);

            // get DataSourceDobj data object and key
            DataSourceDobj doDataSourceDobj = (DataSourceDobj)(parentNode.getDataObject());

            // get DataSourceDobj parent data object and key
            DataObject doParent = ((DoTreeNode)(parentNode.getParent())).getDataObject();

            DataSource ds = DataManager.getSystemDS();
            Query q = new Query(DataView.ENT_DATAVIEW);
//            QueryCriterium qc2 = new QueryCriterium(DataView.ENT_DATAVIEW, DataView.FLD_SOURCE_DSID,
//                QueryCriterium.OP_EQUALS, ((DataSourceDobj)doParent).getId() );
            QueryCriterium qc2 = new QueryCriterium(DataView.ENT_DATAVIEW, DataView.FLD_SOURCE_DSID,
                QueryCriterium.OP_EQUALS, new Integer(((DataSourceDobj)doDataSourceDobj).getId()) );
            q.addQueryCriterium(qc2);
            RecordSet rsDataView = ds.select(q);

            q = new Query(GroupDataView.ENT_GROUPDATAVIEW);
            QueryCriterium qc = new QueryCriterium(GroupDataView.ENT_GROUPDATAVIEW, GroupDataView.FLD_GROUPID,
                QueryCriterium.OP_EQUALS, ((Group)doParent).getId() );
            q.addQueryCriterium(qc);
            RecordSet rsGroupDataView = ds.select(q);

            // add to tree and recurse to get sub-branches
            DataObject dobj;
            while(rsDataView.next())
            {
                // get the DataObject
                dobj = rsDataView.getCurrentObject();
                if(dobj != null)
                {
                    // add to tree as child
                    Icon nodeIcon = iconDisabled;
                    Boolean enabled = Boolean.FALSE;

                    DataObject dobj2;
                    rsGroupDataView.reset();
                    while(rsGroupDataView.next())
                    {
                        dobj2 = rsGroupDataView.getCurrentObject();
                        if ( ( (DataView)dobj ).getId() == ((GroupDataView)dobj2).getDataViewId() )
                        {
                            nodeIcon = iconEnabled;
                            enabled = Boolean.TRUE;
                            break;
                        }
                    } //while(rsGroupDataView.next())

                    // add to tree as child
                    DoTreeNode newNode = _tree.addNode(parentNode, dobj, enabled, nodeIcon, enabled.booleanValue());
                } //if(dobj != null)
            } //while(rsDataView.next())
        }
        finally
        {
            _isBuildingTree = false;
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Building a DataView node.
     * @param parentNode DataGroup node.
     */
    private void buildDataViewNode(DoTreeNode parentNode) throws DataSourceException
    {
        // set is building flag
        _isBuildingTree = true;

        Icon iconEnabled = Util.resourceIcon(MdnAdminConst.SS_IMAGE_PATH + "enabled.gif");
        Icon iconDisabled = Util.resourceIcon(MdnAdminConst.SS_IMAGE_PATH + "disabled.gif");

        try
        {
            // clear the node
            Util.argCheckNull(parentNode);
            _tree.clearNode(parentNode);

            // get parent data object and key
            DataObject doParent = parentNode.getDataObject();

            if (((Boolean)parentNode.getExtraData()) == Boolean.FALSE)
                return;

            // build the parent node
            DataSource ds = DataManager.getSystemDS();

            Query q = new Query(DataViewField.ENT_DVFIELD);
            QueryCriterium qc = new QueryCriterium(DataViewField.ENT_DVFIELD,
                DataViewField.FLD_DATAVIEWID,
                QueryCriterium.OP_EQUALS,
                new Integer(((DataView)doParent).getId()));
            q.addQueryCriterium(qc);
            RecordSet rsDataViewField = ds.select(q);

            Query q2 = new Query(FieldExclusion.ENT_FIELDEXCLUSION);
            QueryCriterium qc1 = new QueryCriterium(FieldExclusion.ENT_FIELDEXCLUSION,
                FieldExclusion.FLD_GROUPID,
                QueryCriterium.OP_EQUALS,
                ((Group)((DoTreeNode)((parentNode.getParent()).getParent())).getDataObject()).getId() );
            q2.addQueryCriterium(qc1);
            RecordSet rsFieldExclusion = ds.select(q2);

            // add to tree and recurse to get sub-branches
            DataObject dobj;
            while(rsDataViewField.next())
            {
                // get the DataObject
                dobj = rsDataViewField.getCurrentObject();
                if(dobj != null)
                {
                    // add to tree as child
                    Icon nodeIcon = iconEnabled;
                    Boolean enabled = Boolean.TRUE;

                    DataObject dobj2;
                    rsFieldExclusion.reset();
                    while(rsFieldExclusion.next())
                    {
                        dobj2 = rsFieldExclusion.getCurrentObject();
                        if (dobj2 != null)
                        if ( ( (DataViewField)dobj ).getId() == ((FieldExclusion)dobj2).getDvFieldId() )
                        {
                            nodeIcon = iconDisabled;
                            enabled = Boolean.FALSE;
                            break;
                        }
                    }

                    // add to tree as child
                    DoTreeNode newNode = _tree.addNode(parentNode, dobj, enabled, nodeIcon, false);
                }
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
        TreePath[] selectedPaths = _tree.getSelectionPaths();
        //_selectedPaths = selectedPaths;
        if (selectedPaths != null)
        {
            int n = selectedPaths.length;
            DefaultMutableTreeNode lastNode = (DefaultMutableTreeNode)
                ((TreePath)selectedPaths[n - 1]).getLastPathComponent();
            if (lastNode != null
                && (lastNode.getUserObject()  instanceof Group
                    || lastNode.getUserObject()  instanceof DataSourceDobj
                    || lastNode.isRoot()
                    )
                )
            {
                _tree.setSelectionPaths(_selectedPaths);
            }
            else
            {
                _selectedPaths = selectedPaths;
            }
        }
        else
        {
            if (_selectedPaths != null)
            {
                _tree.setSelectionPaths(_selectedPaths);
            }
        }

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
                GuiManager.showErrorDialog(this, ERR_BUILDTREE.getText(), e);
            }
        }

        _selectedPaths = _tree.getSelectionPaths();

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

        //_selectedPaths = _tree.getSelectionPaths();
        _selectedPaths = new TreePath[0];
        _tree.setSelectionPaths(_selectedPaths);

    }
    //--------------------------------------------------------------------------
    /**
     * Handle actions from buttons.
     */
    public void actionPerformed(ActionEvent event)
    {
        try
        {
            if(event.getSource().equals(_btnEnable))
                onEnable();
            else if(event.getSource().equals(_btnDisable))
                onDisable();
            else if(event.getSource().equals(_btnProperties))
                onProperties();
        }
        catch(Exception e)
        {
            GuiManager.showErrorDialog(this, GuiManager.ERR_UNHANDLED.getText(), e);
            Log.error(GuiManager.ERR_UNHANDLED.getText(), e);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Enable button clicked.
     */
    private void onEnable()
    {
        // go throw all selected nodes
        if ( _selectedPaths == null)
            return;
        DefaultMutableTreeNode node;
        for (int i = 0; i < _selectedPaths.length; i++)
        {

        // get object for selected tree node
        node = (DefaultMutableTreeNode)
            ((TreePath)_selectedPaths[i]).getLastPathComponent();
        DoTreeNode doTreeNode = (DoTreeNode)node;
        if ( (doTreeNode.getExtraData() == null) || (doTreeNode.getExtraData().equals(new Boolean(true))) )
            continue;
        DataObject selDobj = doTreeNode.getDataObject();
        if (selDobj instanceof DataView)
        {
            // this is a DataView tree node
            DoTreeNode groupNode = (DoTreeNode)doTreeNode.getParent().getParent();

            int groupId = ((Group) groupNode.getDataObject()).getIntId();
            DoTreeNode dataViewSelected = doTreeNode;
            TreePath treePath = _selectedPaths[i];
            TreePath groupPath = treePath.getParentPath();

            // create and initialize new GroupDataView object
            GroupDataView gdv = new GroupDataView();
            int dataViewId = ((DataView)selDobj).getId();
            gdv.setGroupId(groupId);
            gdv.setDataViewId(dataViewId);
//            gdv.setCanDelete(1);
//            gdv.setCanAdd(1);
//            gdv.setCanEdit(1);

            try
            {
                // save new GroupDataView object
                gdv.save();

                // change tree
                doTreeNode.setExtraData(new Boolean(true));
                doTreeNode.setIcon(ICON_ENABLED);
                DoTreeNode dummy = new DoTreeNode("Dummy", null);
                doTreeNode.add(dummy);

                _tree.setExpandsSelectedPaths(true);
                _tree.scrollPathToVisible(_selectedPaths[i]);
                _tree.setSelectionPaths(_selectedPaths);
                _tree.expandPath(_selectedPaths[i]);
            }
            catch (Exception e)
            {
                GuiManager.showErrorDialog(this, ERR_GDV_INSERT.getText(), e);
            }
        }
        else if (selDobj instanceof DataViewField)
        {
            // this is DataViewField tree node
            // get groupId
            //DoTreeNode dataViewNode = (DoTreeNode)_tree.getSelectedNode().getParent();
            DoTreeNode dataViewNode = (DoTreeNode)doTreeNode.getParent();
            DoTreeNode groupNode = (DoTreeNode)dataViewNode.getParent().getParent();
            Group group = (Group)groupNode.getDataObject();
            int groupId = group.getIntId();
            // get dataViewFieldId
            DoTreeNode dataViewFieldSelected = doTreeNode;
            TreePath treePath = _selectedPaths[i];
            TreePath dataViewPath = treePath.getParentPath();
            int dataViewFieldId = ((DataViewField)selDobj).getId();


            DataSource ds = DataManager.getSystemDS();
            try
            {
                // find FieldExclusion object for selected group and view
                Query q = new Query(FieldExclusion.ENT_FIELDEXCLUSION);

                QueryCriterium qc1 = new QueryCriterium(FieldExclusion.ENT_FIELDEXCLUSION, FieldExclusion.FLD_GROUPID,
                    QueryCriterium.OP_EQUALS, new Integer(groupId) );
                q.addQueryCriterium(qc1);

                QueryCriterium qc2 = new QueryCriterium(FieldExclusion.ENT_FIELDEXCLUSION, FieldExclusion.FLD_DVFIELDID,
                    QueryCriterium.OP_EQUALS, new Integer(dataViewFieldId) );
                q.addQueryCriterium(qc2);

                RecordSet rsFieldExclusion = ds.select(q);

                // delete object from Field Exclusions to make field enabled
                if (rsFieldExclusion.next())
                    ds.delete(rsFieldExclusion.getCurrentObject());

                // change tree
                doTreeNode.setExtraData(new Boolean(true));
                doTreeNode.setIcon(ICON_ENABLED);
                DoTreeNode dummy = new DoTreeNode("Dummy", null);
                doTreeNode.add(dummy);

                _tree.setExpandsSelectedPaths(true);
                _tree.scrollPathToVisible(_selectedPaths[i]);
                _tree.setSelectionPaths(_selectedPaths);
                _tree.expandPath(_selectedPaths[i]);

                //_tree.removeNode(dataViewFieldSelected);
                //DoTreeNode newNode = _tree.addNode(dataViewNode, selDobj, new Boolean(true), ICON_ENABLED, false);
                //TreePath newPath = dataViewPath.pathByAddingChild(newNode);
                //_tree.setExpandsSelectedPaths(true);
                //_tree.scrollPathToVisible(newPath);
                //_tree.setSelectionPath(newPath);
            }
            catch (Exception e)
            {
                GuiManager.showErrorDialog(this, ERR_FIELD_EXCLUSION_SELECT.getText(), e);
            }
        }

        } //for (int i = 0; i < _selectedPaths.length; i++)
    }
    //--------------------------------------------------------------------------
    /**
     * Enable button clicked.
     */
    private void onEnableSingleSelection()
    {
        // get object for selected tree node
        DataObject selDobj = _tree.getSelectedDataObject();
        if (selDobj instanceof DataView)
        {
            // this is a DataView tree node
            DoTreeNode groupNode = (DoTreeNode)_tree.getSelectedNode().getParent();
            int groupId = ((Group)(groupNode.getDataObject())).getIntId();
            DoTreeNode dataViewSelected = _tree.getSelectedNode();
            TreePath treePath = _tree.getSelectionPath();
            TreePath groupPath = treePath.getParentPath();

            // create and initialize new GroupDataView object
            GroupDataView gdv = new GroupDataView();
            int dataViewId = ((DataView)selDobj).getId();
            gdv.setGroupId(groupId);
            gdv.setDataViewId(dataViewId);
//            gdv.setCanDelete(1);
//            gdv.setCanAdd(1);
//            gdv.setCanEdit(1);

            try
            {
                // save new GroupDataView object
                gdv.save();

                // change tree
                _tree.removeNode(dataViewSelected);
                DoTreeNode newNode = _tree.addNode(groupNode, selDobj, new Boolean(true), ICON_ENABLED, true);
                TreePath newPath = groupPath.pathByAddingChild(newNode);
                _tree.setExpandsSelectedPaths(true);
                _tree.scrollPathToVisible(newPath);
                _tree.setSelectionPath(newPath);
                _tree.expandPath(newPath);
            }
            catch (Exception e)
            {
                GuiManager.showErrorDialog(this, ERR_GDV_INSERT.getText(), e);
            }
        }
        else if (selDobj instanceof DataViewField)
        {
            // this is DataViewField tree node
            // get groupId
            DoTreeNode dataViewNode = (DoTreeNode)_tree.getSelectedNode().getParent();
            DoTreeNode groupNode = (DoTreeNode)_tree.getSelectedNode().getParent().getParent();
            int groupId = ((Group)(groupNode.getDataObject())).getIntId();
            // get dataViewFieldId
            DoTreeNode dataViewFieldSelected = _tree.getSelectedNode();
            TreePath treePath = _tree.getSelectionPath();
            TreePath dataViewPath = treePath.getParentPath();
            int dataViewFieldId = ((DataViewField)selDobj).getId();


            DataSource ds = DataManager.getSystemDS();
            try
            {
                // find FieldExclusion object for selected group and view
                Query q = new Query(FieldExclusion.ENT_FIELDEXCLUSION);

                QueryCriterium qc1 = new QueryCriterium(FieldExclusion.ENT_FIELDEXCLUSION, FieldExclusion.FLD_GROUPID,
                    QueryCriterium.OP_EQUALS, new Integer(groupId) );
                q.addQueryCriterium(qc1);

                QueryCriterium qc2 = new QueryCriterium(FieldExclusion.ENT_FIELDEXCLUSION, FieldExclusion.FLD_DVFIELDID,
                    QueryCriterium.OP_EQUALS, new Integer(dataViewFieldId) );
                q.addQueryCriterium(qc2);

                RecordSet rsFieldExclusion = ds.select(q);

                // delete object from Field Exclusions to make field enabled
                if (rsFieldExclusion.next())
                    ds.delete(rsFieldExclusion.getCurrentObject());

                // change tree
                _tree.removeNode(dataViewFieldSelected);
                DoTreeNode newNode = _tree.addNode(dataViewNode, selDobj, new Boolean(true), ICON_ENABLED, false);
                TreePath newPath = dataViewPath.pathByAddingChild(newNode);
                _tree.setExpandsSelectedPaths(true);
                _tree.scrollPathToVisible(newPath);
                _tree.setSelectionPath(newPath);
            }
            catch (Exception e)
            {
                GuiManager.showErrorDialog(this, ERR_FIELD_EXCLUSION_SELECT.getText(), e);
            }
        }
    }
    //--------------------------------------------------------------------------
    /**
     * Disable button clicked.
     */
    private void onDisable()
    {
        if ((_selectedPaths == null) || (_selectedPaths.length == 0))
            return;
        // go throw all selected nodes
        DefaultMutableTreeNode node;
        //for (int i = 0; i < _selectedPaths.length; i++)
        int i = 0;
        while (true)
        {

        // get object for selected tree node
        node = (DefaultMutableTreeNode)
            ((TreePath)_selectedPaths[i]).getLastPathComponent();
        DoTreeNode doTreeNode = (DoTreeNode)node;
        if ( (doTreeNode.getExtraData() == null) || (doTreeNode.getExtraData().equals(new Boolean(false))) )
        {
            i++;
            if (i == _selectedPaths.length)
                break;
            continue;
        }
        DataObject selDobj = doTreeNode.getDataObject();

        if (selDobj instanceof DataView)
        {
            // this is a DataView tree node
            if ((doTreeNode == null) || (doTreeNode.getParent() == null) || (doTreeNode.getParent().getParent() == null))
                break;
            DoTreeNode groupNode = (DoTreeNode)doTreeNode.getParent().getParent();
            DoTreeNode dsNode = (DoTreeNode)doTreeNode.getParent();
            int groupId = (((Group)(groupNode.getDataObject())).getIntId());
            DoTreeNode dataViewSelected = doTreeNode;
            TreePath treePath = _selectedPaths[i];
            TreePath groupPath = treePath.getParentPath();

            int dataViewId = ((DataView)selDobj).getId();
            DataSource ds = DataManager.getSystemDS();

            try
            {
                // find GroupDataView object for selected group and view
                Query q = new Query(GroupDataView.ENT_GROUPDATAVIEW);

                QueryCriterium qc1 = new QueryCriterium(GroupDataView.ENT_GROUPDATAVIEW, GroupDataView.FLD_GROUPID,
                    QueryCriterium.OP_EQUALS, new Integer(groupId) );
                q.addQueryCriterium(qc1);

                QueryCriterium qc2 = new QueryCriterium(GroupDataView.ENT_GROUPDATAVIEW, GroupDataView.FLD_DATAVIEWID,
                    QueryCriterium.OP_EQUALS, new Integer(dataViewId) );
                q.addQueryCriterium(qc2);

                RecordSet rsGroupDataView = ds.select(q);
                // delete GroupDataView object to disable selected view for the group
                if (rsGroupDataView.next())
                    ds.delete(rsGroupDataView.getCurrentObject());

                // change tree
                TreePath tp = _selectedPaths[i];
                TreePath[] sp = new TreePath[_selectedPaths.length - 1];
                int k = 0;
                for (int j = 0; j < _selectedPaths.length; j++)
                {
                    if (i != j)
                    {
                        sp[k] = _selectedPaths[j];
                        k++;
                    }
                }
                _selectedPaths = sp;

                _tree.removeSelectionPath(tp);
                doTreeNode.removeAllChildren();
                _tree.removeNode(doTreeNode);
                DoTreeNode newNode = _tree.addNode(dsNode, selDobj, new Boolean(false), ICON_DISABLED, false);
                doTreeNode = newNode;
                TreePath dataViewPath = treePath.getParentPath();
                TreePath newPath = dataViewPath.pathByAddingChild(newNode);
                _tree.addSelectionPath(newPath);
                _selectedPaths = _tree.getSelectionPaths();
            }
            catch (Exception e)
            {
                GuiManager.showErrorDialog(this, ERR_DISABLE.getText(), e);
            }
        }
        else if (selDobj instanceof DataViewField)
        {
            // this is DataViewField tree node
            // get groupId
            DoTreeNode dataViewNode = (DoTreeNode)doTreeNode.getParent();
            if (dataViewNode == null)
                continue;
            DoTreeNode groupNode = (DoTreeNode)dataViewNode.getParent().getParent();
            int groupId = (((Group)(groupNode.getDataObject())).getIntId());
            // get dataViewFieldId
            DoTreeNode dataViewFieldSelected = doTreeNode;
            TreePath treePath = _selectedPaths[i];
            TreePath dataViewPath = treePath.getParentPath();
            int dataViewFieldId = ((DataViewField)selDobj).getId();

            // create and initialize new FieldExclusion object
            FieldExclusion fe = new FieldExclusion();
            fe.setDvFieldId(dataViewFieldId);
            fe.setGroupId(groupId);

            try
            {
                fe.save();

                // change tree

                _tree.removeSelectionPath(_selectedPaths[i]);
                TreePath[] sp = new TreePath[_selectedPaths.length - 1];
                int k = 0;
                for (int j = 0; j < _selectedPaths.length; j++)
                {
                    if (i != j)
                    {
                        sp[k] = _selectedPaths[j];
                        k++;
                    }
                }
                _selectedPaths = sp;
                _tree.removeNode(doTreeNode);
                DoTreeNode newNode = _tree.addNode(dataViewNode, selDobj, new Boolean(false), ICON_DISABLED, false);
                TreePath newPath = dataViewPath.pathByAddingChild(newNode);
                _tree.addSelectionPath(newPath);
            }
            catch (Exception e)
            {
                GuiManager.showErrorDialog(this, ERR_DISABLE.getText(), e);
            }
        }

        i = 0;
        } //for (int i = 0; i < _selectedPaths.length; i++)
    }

    //--------------------------------------------------------------------------
    /**
     * Disable button clicked.
     */
    private void onDisableSingleSelection()
    {
        // get object for selected tree node
        DataObject selDobj = _tree.getSelectedDataObject();
        if (selDobj instanceof DataView)
        {
            // this is a DataView tree node
            DoTreeNode groupNode = (DoTreeNode)_tree.getSelectedNode().getParent();
            int groupId = (((Group)(groupNode.getDataObject())).getIntId());
            DoTreeNode dataViewSelected = _tree.getSelectedNode();
            TreePath treePath = _tree.getSelectionPath();
            TreePath groupPath = treePath.getParentPath();

            int dataViewId = ((DataView)selDobj).getId();
            DataSource ds = DataManager.getSystemDS();

            try
            {
                // find GroupDataView object for selected group and view
                Query q = new Query(GroupDataView.ENT_GROUPDATAVIEW);

                QueryCriterium qc1 = new QueryCriterium(GroupDataView.ENT_GROUPDATAVIEW, GroupDataView.FLD_GROUPID,
                    QueryCriterium.OP_EQUALS, new Integer(groupId) );
                q.addQueryCriterium(qc1);

                QueryCriterium qc2 = new QueryCriterium(GroupDataView.ENT_GROUPDATAVIEW, GroupDataView.FLD_DATAVIEWID,
                    QueryCriterium.OP_EQUALS, new Integer(dataViewId) );
                q.addQueryCriterium(qc2);

                RecordSet rsGroupDataView = ds.select(q);
                // delete GroupDataView object to disable selected view for the group
                if (rsGroupDataView.next())
                    ds.delete(rsGroupDataView.getCurrentObject());

                // change tree
                _tree.removeNode(dataViewSelected);
                DoTreeNode newNode = _tree.addNode(groupNode, selDobj, new Boolean(false), ICON_DISABLED, false);
                TreePath newPath = groupPath.pathByAddingChild(newNode);
                _tree.setExpandsSelectedPaths(true);
                _tree.scrollPathToVisible(groupPath.pathByAddingChild(newNode));
                _tree.setSelectionPath(newPath);
            }
            catch (Exception e)
            {
                GuiManager.showErrorDialog(this, ERR_DISABLE.getText(), e);
            }
        }
        else if (selDobj instanceof DataViewField)
        {
            // this is DataViewField tree node
            // get groupId
            DoTreeNode dataViewNode = (DoTreeNode)_tree.getSelectedNode().getParent();
            DoTreeNode groupNode = (DoTreeNode)_tree.getSelectedNode().getParent().getParent();
            int groupId = (((Group)(groupNode.getDataObject())).getIntId());
            // get dataViewFieldId
            DoTreeNode dataViewFieldSelected = _tree.getSelectedNode();
            TreePath treePath = _tree.getSelectionPath();
            TreePath dataViewPath = treePath.getParentPath();
            int dataViewFieldId = ((DataViewField)selDobj).getId();

            // create and initialize new FieldExclusion object
            FieldExclusion fe = new FieldExclusion();
            fe.setDvFieldId(dataViewFieldId);
            fe.setGroupId(groupId);

            DataSource ds = DataManager.getSystemDS();
            try
            {
                ds.insert(fe);

                // change tree
                _tree.removeNode(dataViewFieldSelected);
                DoTreeNode newNode = _tree.addNode(dataViewNode, selDobj, new Boolean(false), ICON_DISABLED, false);
                TreePath newPath = dataViewPath.pathByAddingChild(newNode);
                _tree.setExpandsSelectedPaths(true);
                _tree.scrollPathToVisible(newPath);
                _tree.setSelectionPath(newPath);
            }
            catch (Exception e)
            {
                GuiManager.showErrorDialog(this, ERR_DISABLE.getText(), e);
            }
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Properties button clicked.
     */
    public void onProperties() throws DataSourceException
    {
        // get the selected dataview
        DataObject selDobj = _tree.getSelectedDataObject();
        int dataViewId = ((DataView)selDobj).getId();
        DoTreeNode groupNode = (DoTreeNode)_tree.getSelectedNode().getParent().getParent();
        int groupId = (((Group)(groupNode.getDataObject())).getIntId());

        DataSource ds = DataManager.getSystemDS();

        // find GroupDataView object for selected group and view
        Query q = new Query(GroupDataView.ENT_GROUPDATAVIEW);

        QueryCriterium qc1 = new QueryCriterium(GroupDataView.ENT_GROUPDATAVIEW, GroupDataView.FLD_GROUPID,
            QueryCriterium.OP_EQUALS, new Integer(groupId) );
        q.addQueryCriterium(qc1);

        QueryCriterium qc2 = new QueryCriterium(GroupDataView.ENT_GROUPDATAVIEW, GroupDataView.FLD_DATAVIEWID,
            QueryCriterium.OP_EQUALS, new Integer(dataViewId) );
        q.addQueryCriterium(qc2);

        RecordSet rsGroupDataView = ds.select(q);
        // open properties panel for found GroupDataView object
        if (rsGroupDataView.next())
        {
            DataObject dobj = rsGroupDataView.getCurrentObject();
            GuiManager.openOkCancelPanel(this.getFrameParent(), dobj, true);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Update buttons
     */
    public void updateButtons()
    {
        // flags
        boolean isGroup = false;
        boolean hasDobj = ((_selectedPaths != null) && (_selectedPaths.length > 0));
        boolean haveEnabled = false;
        boolean haveDesabled = false;
        boolean isEnabledButton = false;
        boolean isDataView = false;

        // go throw all selected nodes
        DefaultMutableTreeNode node;
        if (_selectedPaths != null)
        for (int i = 0; i < _selectedPaths.length; i++)
        {
            node = (DefaultMutableTreeNode)
                ((TreePath)_selectedPaths[i]).getLastPathComponent();
            DoTreeNode doTreeNode = (DoTreeNode)node;
            Object extraData = doTreeNode.getExtraData();
            if (extraData != null)
            {
                boolean enabled = ((Boolean)doTreeNode.getExtraData()).booleanValue();
                haveDesabled = haveDesabled || !enabled;
                haveEnabled = haveEnabled || enabled;
                isDataView = doTreeNode.getDataObject() instanceof DataView;
            }
        }

        // enable
        _btnEnable.setEnabled(haveDesabled);
        _btnDisable.setEnabled(haveEnabled);
        _btnProperties.setEnabled((_selectedPaths != null) && (_selectedPaths.length == 1
                                        && isDataView
                                        && haveEnabled)
                                    );
    }
    //--------------------------------------------------------------------------
    /**
     * Get the buttons that this tab wants to display in the parent's button.
     * panel. This is called each time the tab child panel is selected.
     * @return an array of buttons, may be empty, not null.
     */
    public WslButton[] getButtons()
    {
        return _buttons;
    }

    public void onSelected(boolean selected)
    {
        //updateButtons();
        if (selected)
            buildTree();
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
        return HID_DATAVIEW_PERMISSIONS;
    }
}