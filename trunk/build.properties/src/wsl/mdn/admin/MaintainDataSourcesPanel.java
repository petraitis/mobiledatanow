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
import wsl.fw.util.Type;
import wsl.fw.resource.ResId;
import wsl.fw.help.HelpId;
import wsl.fw.datasource.*;
import wsl.fw.gui.*;
import wsl.mdn.dataview.*;
import wsl.mdn.common.MdnAdminConst;
import wsl.mdn.common.MdnDataManager;

//------------------------------------------------------------------------------
/**
 *
 */
public class MaintainDataSourcesPanel extends WslButtonPanel
    implements ActionListener, TreeExpansionListener, TreeSelectionListener, WizardClosedListener
{
    // resources
    public static final ResId BUTTON_ADD_DATASOURCE  = new ResId("MaintainDataSourcesPanel.button.AddDataSource");
    public static final ResId BUTTON_ADD_RELATION  = new ResId("MaintainDataSourcesPanel.button.AddRelation");
    public static final ResId BUTTON_ADD_ENTITY  = new ResId("MaintainDataSourcesPanel.button.AddEntity");
    public static final ResId BUTTON_ADD_FIELD  = new ResId("MaintainDataSourcesPanel.button.AddField");
    public static final ResId BUTTON_REMOVE  = new ResId("MaintainDataSourcesPanel.button.Remove");
    public static final ResId BUTTON_PROPERTIES  = new ResId("MaintainDataSourcesPanel.button.Properties");
    public static final ResId BUTTON_SETKEY  = new ResId("MaintainDataSourcesPanel.button.SetKey");
    public static final ResId BUTTON_CLOSE  = new ResId("MaintainDataSourcesPanel.button.Close");
    public static final ResId BUTTON_SYNC  = new ResId("MaintainDataSourcesPanel.button.Sync");
    public static final ResId TREE_NAME  = new ResId("MaintainDataSourcesPanel.tree");
    public static final ResId PANEL_TITLE  = new ResId("MaintainDataSourcesPanel.title");
    public static final ResId REL_PANEL_TITLE  = new ResId("MaintainDataSourcesPanel.relpanel.title");
    public static final ResId BUTTON_HELP = new ResId("OkPanel.button.Help");
    public static final ResId ERR_BUILD_TREE = new ResId("MaintainDataSourcesPanel.error.buildTree");
    public static final ResId ERR_SAVE = new ResId("MaintainDataSourcesPanel.error.save");

    public final static HelpId HID_MAINT_DATASOURCES = new HelpId("mdn.admin.MaintainDataSourcesPanel");

    // constants
    private static final int BTN_WIDTH = 132;

    // attributes
    private WslButton _btnAddDataSource = new WslButton(BUTTON_ADD_DATASOURCE.getText(), BTN_WIDTH, this);
    private WslButton _btnAddEntity = new WslButton(BUTTON_ADD_ENTITY.getText(), BTN_WIDTH, this);
    private WslButton _btnAddField = new WslButton(BUTTON_ADD_FIELD.getText(), BTN_WIDTH, this);
    private WslButton _btnRelations = new WslButton(BUTTON_ADD_RELATION.getText(), BTN_WIDTH, this);
    private WslButton _btnRemove = new WslButton(BUTTON_REMOVE.getText(), BTN_WIDTH, this);
    private WslButton _btnProperties = new WslButton(BUTTON_PROPERTIES.getText(), BTN_WIDTH, this);
    private WslButton _btnSetKey = new WslButton(BUTTON_SETKEY.getText(), BTN_WIDTH, this);
    private WslButton _btnSync = new WslButton(BUTTON_SYNC.getText(), BTN_WIDTH, this);
    private WslButton _btnClose = new WslButton(BUTTON_CLOSE.getText(), BTN_WIDTH, this);
    private WslButton _btnTest = new WslButton("Test", BTN_WIDTH, this);
    private DataObjectTree _tree;
    private boolean _isBuildingTree = false;
    private DataStoreImportWizard _wiz = null;

    //--------------------------------------------------------------------------
    /**
     * Blank constructor.
     */
    public MaintainDataSourcesPanel()
    {
        // super
        super(WslButtonPanel.VERTICAL);

        // set title
        setPanelTitle(PANEL_TITLE.getText());

        // init controls
        initMaintainDataSourcesPanelControls();

        // build the tree
        buildTree();

        // update buttons
        updateButtons();
    }

    //--------------------------------------------------------------------------
    /**
     * Initialise controls.
     */
    private void initMaintainDataSourcesPanelControls()
    {
        // add buttons
        addButton(_btnAddDataSource);
        addButton(_btnAddEntity);
        addButton(_btnAddField);
        addButton(_btnRelations);
        addButton(_btnRemove);
        addButton(_btnProperties);
        addButton(_btnSetKey);
        addButton(_btnSync);
        addHelpButton(BUTTON_HELP.getText(), HID_MAINT_DATASOURCES, BTN_WIDTH, false);
        _btnClose.setIcon(Util.resourceIcon(GuiConst.FW_IMAGE_PATH + "close.gif"));
        addButton(_btnClose);
        // addCustomButton(_btnTest);

        // create the tree
        _tree = new DataObjectTree(TREE_NAME.getText());
        _tree.addTreeExpansionListener(this);
        _tree.addTreeSelectionListener(this);
        _tree.addListener(new DataListenerData(null, DataSourceDobj.ENT_DATASOURCE, null));
        _tree.addListener(new DataListenerData(null, EntityDobj.ENT_ENTITY, null));
        _tree.addListener(new DataListenerData(null, FieldDobj.ENT_FIELD, null));
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

            // else, building a datasource
            else if(doParent instanceof DataSourceDobj)
            {
                // build the parent node
                _tree.buildFromVector(((DataSourceDobj)doParent).getEntities(), parentNode);
            }

            // else, building an entity
            else if(doParent instanceof EntityDobj)
            {
                // build the parent node
                _tree.buildFromVector(((EntityDobj)doParent).getFields(),
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
            else if(ev.getSource().equals(_btnAddDataSource))
                onAddDataSource();
            else if(ev.getSource().equals(_btnAddEntity))
                onAddEntity();
            else if(ev.getSource().equals(_btnAddField))
                onAddField();
            else if(ev.getSource().equals(_btnRelations))
                onRelations();
            else if(ev.getSource().equals(_btnRemove))
                onRemove();
            else if(ev.getSource().equals(_btnProperties))
                onProperties();
            else if(ev.getSource().equals(_btnSetKey))
                onSetKey();
            else if(ev.getSource().equals(_btnSync))
                onSyncMirror();
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

    /**
     * Add DataSource button clicked.
     */

    public void onAddDataSource() throws DataSourceException
    {
        // create the wizard controller
        _wiz = new DataStoreImportWizard();

        // set the selected ds
        DataObject dobj = _tree.getSelectedDataObject();
        if(dobj != null && dobj instanceof DataSourceDobj)
            _wiz.setExistingDataSource((DataSourceDobj)dobj);

        // add close listener
        _wiz.addCloseListener(this);

        // create framing dialog and show
        GuiManager.openWslPanel(this.getFrameParent(), _wiz, true);
    }

    /**
     * DataStore import wizard closed
     */
    public void wizardClosed(boolean bFinished)
    {
        // ignore if closing
        if(isClosing())
            return;

        // validate
        Util.argCheckNull(_wiz);

        // must be finished
        if(bFinished)
        {
            // get the ds from the wizard
            DataSourceDobj ds = (DataSourceDobj)_wiz.getDataSource();
            if(ds != null)
            {
                try
                {
                    Cursor oldCursor = null;
                    try
                    {
                        oldCursor = getCursor();
                        setCursor(new Cursor(Cursor.WAIT_CURSOR));

                        // was it mirrored?
                        boolean wasMirrored = Type.objectToBoolean(
                            ds.getImageValue(DataSourceDobj.FLD_IS_MIRRORED));

                        // update the mirror
                        //if(ds.isMirrored())
                        {
                            int mirrorId = Type.objectToInt(
                                ds.getImageValue(DataSourceDobj.FLD_MIRRORID));
                            updateMirrorTables(ds, wasMirrored, mirrorId);
                        }

                        // save the ds
                        ds.save();

                        // if a new ds add it to the tree
                        DataObject dobj = _tree.getSelectedDataObject();
                        boolean isExisting = dobj != null && dobj instanceof DataSourceDobj &&
                            ((DataSourceDobj)dobj) == ds;
                        if(!isExisting)
                            _tree.addNode(_tree.getRoot(), ds, true);

                        // else rebuild the node
                        else
                            buildNode(_tree.getSelectedNode());
                    }
                    finally
                    {
                        if (oldCursor != null)
                            setCursor(oldCursor);
                    }
                }
                catch(Exception e)
                {
                    GuiManager.showErrorDialog(this, ERR_SAVE.getText(), e);
                }
            }
        }
    }

    /**
     * Updtae the mirror tables for a mirrored ds
     * @param ds the mirrored DataSource
     */
    private void updateMirrorTables(DataSourceDobj ds, boolean wasMirrored, int oldMirrorId)
        throws DataSourceException
    {
        // validate
        Util.argCheckNull(ds);
        int newMirrorId = ds.getMirrorId();

        // if newly unmirrored, drop all tables
        if(wasMirrored && !ds.isMirrored())
            dropAllMirrorTables(ds, oldMirrorId);

        // if newly mirrored, create all tables
        else if(!wasMirrored && ds.isMirrored())
            createAllMirrorTables(ds);

        // was mirrored and is mirrored
        else if(wasMirrored && ds.isMirrored())
        {
            // if changed mirrors all together, drop old, create new
            if(newMirrorId != oldMirrorId)
            {
                // drop old mirror tables
                dropAllMirrorTables(ds, oldMirrorId);

                // create new mirror
                createAllMirrorTables(ds);
            }

            // else modifying existing mirror
            else if(ds.isMirrored())
            {
                // get the mirror ds
                int mirrorId = ds.getMirrorId();
                DataSource mirrorDs = MdnDataCache.getCache().getDataSource(mirrorId);

                // drop the deleted tables
                EntityDobj ed;
                Vector tables = ds.getDeletedEntities();
                for(int i = 0; tables != null && i < tables.size(); i++)
                {
                    // get the entity dobj
                    ed = (EntityDobj)tables.elementAt(i);
                    if(ed != null)
                        mirrorDs.dropEntityTable(ed.getName());
                }

                // recreate any new tables
                tables = ds.getEntities();
                for(int i = 0; tables != null && i < tables.size(); i++)
                {
                    // get the entity dobj
                    ed = (EntityDobj)tables.elementAt(i);
                    if(ed != null && ed.getState() == DataObject.NEW)
                        mirrorDs.createEntityTable(ed.createImpl(), true);
                }
            }
        }
    }

    /**
     * Create the mirror tables for a mirrored ds
     * @param ds the DataSource to mirror
     */
    private void createAllMirrorTables(DataSourceDobj ds) throws DataSourceException
    {
        // validate
        Util.argCheckNull(ds);

        // get the mirror ds
        int mirrorId = ds.getMirrorId();
        DataSource mirrorDs = MdnDataCache.getCache().getDataSource(mirrorId);

        // iterate the the ds tables
        EntityDobj ed;
        Vector tables = ds.getEntities();
        for(int i = 0; tables != null && i < tables.size(); i++)
        {
            // get the entity dobj
            ed = (EntityDobj)tables.elementAt(i);
            if(ed != null)
            {
                // create the table in the mirror
                mirrorDs.createEntityTable(ed.createImpl(), true);
            }
        }
    }

    /**
     * Create a single mirror table
     * @param ds the DataSource to mirror
     * @param ed the EntityDobj to (re)create in the mirror
     */
    private void createMirrorTable(DataSourceDobj ds, EntityDobj ed) throws DataSourceException
    {
        // validate
        Util.argCheckNull(ds);
        assert ds.isMirrored();

        // get the mirror ds
        int mirrorId = ds.getMirrorId();
        DataSource mirrorDs = MdnDataCache.getCache().getDataSource(mirrorId);

        // create the table in the mirror
        mirrorDs.createEntityTable(ed.createImpl(), true);
    }

    /**
     * Drop the mirror tables for a mirrored ds
     * @param ds the DataSource to mirror
     */
    private void dropAllMirrorTables(DataSourceDobj ds, int mirrorId) throws DataSourceException
    {
        // validate
        Util.argCheckNull(ds);

        // get the mirror ds
        //int mirrorId = ds.getMirrorId();
        DataSource mirrorDs = MdnDataCache.getCache().getDataSource(mirrorId);

        // iterate the the ds tables
        EntityDobj ed;
        Vector tables = ds.getEntities();
        for(int i = 0; tables != null && i < tables.size(); i++)
        {
            // get the entity dobj
            ed = (EntityDobj)tables.elementAt(i);
            if(ed != null)
            {
                // drop the table in the mirror
                mirrorDs.dropEntityTable(ed.getName());
            }
        }
    }

    /**
     * Drop a single table
     */
    private void dropMirrorTable(DataSourceDobj ds, String entityName) throws DataSourceException
    {
        // validate
        Util.argCheckNull(ds);
        assert ds.isMirrored();

        // get the mirror ds
        int mirrorId = ds.getMirrorId();
        DataSource mirrorDs = MdnDataCache.getCache().getDataSource(mirrorId);

        // drop the table in the mirror
        mirrorDs.dropEntityTable(entityName);
    }

    /**
     * Add Entity button clicked.
     */
    public void onAddEntity() throws DataSourceException
    {
        // get the selected entity
        DataObject dobj = _tree.getSelectedDataObject();
        if(dobj != null && dobj instanceof DataSourceDobj)
        {
            // cast the ds
            DataSourceDobj ds = (DataSourceDobj)dobj;

            // create the entity
            EntityDobj ent = new EntityDobj();

            // open the properties panel
            OkCancelPanel panel = GuiManager.openOkCancelPanel(
                this.getFrameParent(), ent, true, false);

            // if Ok clicked
            if(panel.isOk())
            {
                // ensure entities are loaded
                ds.getEntities();

                // add the entity
                ds.addEntity(ent);

                // update mirror
                if(ds.isMirrored())
                    createMirrorTable(ds, ent);

                // save ds
                ds.save();

                // update tree
                buildNode(_tree.getSelectedNode());
            }
        }
    }

    /**
     * Add Field button clicked.
     */

    public void onAddField() throws DataSourceException
    {
        // get the selected entity
        DataObject dobj = _tree.getSelectedDataObject();
        if(dobj != null && dobj instanceof EntityDobj)
        {
            // get the datasource
            DoTreeNode dsNode = (DoTreeNode)_tree.getSelectedNode().getParent();
            DataSourceDobj ds = (DataSourceDobj)_tree.getDataObject(dsNode);

            // cast the entity
            EntityDobj ent = (EntityDobj)dobj;

            // create the field
            FieldDobj f = new FieldDobj();

            // open the properties panel
            OkCancelPanel panel = GuiManager.openOkCancelPanel(
                this.getFrameParent(), f, true, false);

            // if Ok clicked
            if(panel.isOk())
            {
                // add it to the entity and save
                ent.addField(f);
                ent.save();

                // update mirror
                if(ds.isMirrored())
                    createMirrorTable(ds, ent);

                // add to tree
                _tree.addNode(_tree.getSelectedNode(), f, false);
            }
        }
    }

    /**
     * Relations button clicked.
     */

    public void onRelations() throws DataSourceException
    {
        // get the selected entity
        DataObject dobj = _tree.getSelectedDataObject();
        if(dobj != null && dobj instanceof DataSourceDobj)
        {
            // cast the ds
            DataSourceDobj ds = (DataSourceDobj)dobj;

            // create and open the relationships panel
            RelationshipsPanel panel = new RelationshipsPanel(ds);
            GuiManager.openWslPanel(this.getFrameParent(), panel, true);
        }
    }

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
            // if it is a datasource, straight delete
            if(dobj instanceof DataSourceDobj)
            {
                DataSourceDobj ds = (DataSourceDobj)dobj;
                if(ds.isMirrored())
                    dropAllMirrorTables(ds, ds.getMirrorId());
                dobj.delete();
            }

            // else remove from parent
            else
            {
                // get parent
                DoTreeNode selNode = _tree.getSelectedNode();
                DoTreeNode parentNode = (DoTreeNode)selNode.getParent();
                DataObject parentDobj = _tree.getDataObject(parentNode);

                // if entity remove from data source
                if(dobj instanceof EntityDobj)
                {
                    // remove
                    DataSourceDobj ds = (DataSourceDobj)parentDobj;
                    ds.removeEntity((EntityDobj)dobj, true);

                    // drop the table
                    if(ds.isMirrored())
                        dropMirrorTable(ds, ((EntityDobj)dobj).getName());
                }

                // else if field remove from entity
                else if(dobj instanceof FieldDobj)
                {
                    // remove
                    EntityDobj ed = (EntityDobj)parentDobj;
                    ed.removeField((FieldDobj)dobj, true);

                    // recreate mirror table
                    DoTreeNode dsNode = (DoTreeNode)parentNode.getParent();
                    DataSourceDobj ds = (DataSourceDobj)_tree.getDataObject(dsNode);
                    if(ds.isMirrored())
                        createMirrorTable(ds, ed);
                }
            }
        }
    }

    /**
     * Properties button clicked.
     */
    public void onProperties() throws DataSourceException
    {
        // get the selected datasource
        DataObject dobj = _tree.getSelectedDataObject();
        if(dobj != null)
        {
            // was it mirrored?
            String oldName = "";
            boolean wasMirrored = false;
            int oldMirrorId = -1;
            if(dobj instanceof DataSourceDobj)
            {
                wasMirrored = Type.objectToBoolean(
                    ((DataSourceDobj)dobj).getImageValue(DataSourceDobj.FLD_IS_MIRRORED));
                oldMirrorId = (wasMirrored)? ((DataSourceDobj)dobj).getMirrorId(): -1;
            }

            // if entity, keep the old name
            else if(dobj instanceof EntityDobj)
                oldName = ((EntityDobj)dobj).getName();

            // open the panel
            OkCancelPanel panel = GuiManager.openOkCancelPanel(
                this.getFrameParent(), dobj, true);
            if(panel.isOk())
            {
                // if it is a ds, update mirror
                if(dobj instanceof DataSourceDobj)
                    updateMirrorTables((DataSourceDobj)dobj, wasMirrored, oldMirrorId);

                // if entity, (re)create table
                else if(dobj instanceof EntityDobj)
                {
                    // get the parent ds
                    DoTreeNode eNode = _tree.getSelectedNode();
                    DoTreeNode dsNode = (DoTreeNode)eNode.getParent();
                    DataSourceDobj ds = (DataSourceDobj)_tree.getDataObject(dsNode);
                    EntityDobj ed = (EntityDobj)dobj;

                    // drop table on old name
                    if(ds.isMirrored())
                        dropMirrorTable(ds, oldName);

                    // recreate table
                    if(ds.isMirrored())
                        createMirrorTable(ds, ed);
                }

                // if field (re)create table
                else if(dobj instanceof FieldDobj)
                {
                    // get the parent ds and enitity
                    DoTreeNode fNode = _tree.getSelectedNode();
                    DoTreeNode eNode = (DoTreeNode)fNode.getParent();
                    DoTreeNode dsNode = (DoTreeNode)eNode.getParent();
                    DataSourceDobj ds = (DataSourceDobj)_tree.getDataObject(dsNode);
                    EntityDobj ed = (EntityDobj)_tree.getDataObject(eNode);

                    // recreate table
                    if(ds.isMirrored())
                        createMirrorTable(ds, ed);
                }
            }
        }
    }

    /**
     * SetNaming button clicked.
     */
    private void onSetKey() throws DataSourceException
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
                        // if it is a FieldDobj then set the ds
                        if(dobj instanceof FieldDobj)
                        {
                            FieldDobj f = (FieldDobj)dobj;
                            if (f.hasFlag(Field.FF_UNIQUE_KEY))
                                f.setFlags(f.getFlags() & ~Field.FF_UNIQUE_KEY);
                            else
                                f.setFlags(f.getFlags() | Field.FF_UNIQUE_KEY);
                            f.save();
                        } //if(dobj instanceof FieldDobj)
                    } //if(dobj != null)
                } //if (lastNode != null)
            } //for (int i = 0; i < n; i++)
        } //if (selectedPaths != null)
    }

    /**
     * Sync Mirror button clicked
     */
    private void onSyncMirror() throws DataSourceException
    {
        // get the selected object
        DataObject dobj = _tree.getSelectedDataObject();
        if(dobj != null && dobj instanceof DataSourceDobj)
        {
            DataSourceDobj ds = (DataSourceDobj)dobj;
            if(ds.isMirrored())
            {
                // drop / create tables
                createAllMirrorTables(ds);
            }
        }
    }

    /**
     * Update controls
     */
    public void updateButtons()
    {
        // flags
        DataObject selDobj = _tree.getSelectedDataObject();
        boolean hasDobj = selDobj != null;
        boolean isDs = hasDobj && selDobj instanceof DataSourceDobj;
        boolean isEnt = hasDobj && selDobj instanceof EntityDobj;
        boolean isMirrored = isDs && ((DataSourceDobj)selDobj).isMirrored();
        boolean isFieldDobj = isAnyFieldDobjSelected();

        // enable
        _btnAddDataSource.setEnabled(true);
        _btnAddEntity.setEnabled(isDs);
        _btnAddField.setEnabled(isEnt);
        _btnRelations.setEnabled(isDs);
        _btnRemove.setEnabled(hasDobj);
        _btnProperties.setEnabled(hasDobj);
        _btnSetKey.setEnabled(isFieldDobj);
        _btnSync.setEnabled(isDs && isMirrored);
    }

    private boolean isAnyFieldDobjSelected()
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
                    DataObject dobj = (DataObject)lastNode.getUserObject();
                    if(dobj != null)
                    {
                        // if it is a FieldDobj then set the ds
                        if(dobj instanceof FieldDobj)
                        {
                            return true;
                        } //if(dobj instanceof FieldDobj)
                    } //if(dobj != null)
                } //if (lastNode != null)
            } //for (int i = 0; i < n; i++)
        } //if (selectedPaths != null)
        return false;
    }

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
     * Test Oracle
     */
    private void onTest() throws Exception
    {
        // get the selected ds
        DataObject dobj = _tree.getSelectedDataObject();
        if(dobj instanceof DataSourceDobj)
        {
            // get the ds
            DataSourceDobj dsDobj = (DataSourceDobj)dobj;

            // conn
            JdbcDataSource ds = (JdbcDataSource)MdnDataCache.getCache().getDataSource(
                dsDobj.getId(), true);
            Connection conn = ds.getConnection();


            // get db meta data
            DatabaseMetaData md = conn.getMetaData();

            // get tables
            String name, cat, type, schema;
            ResultSet rs = md.getTables(null, null, null, new String[] {"TABLE"});
            while(rs != null && rs.next())
            {
                // get the table name
                name = rs.getString("TABLE_NAME");
                schema = rs.getString("TABLE_SCHEM");
                cat = rs.getString("TABLE_CAT");
                type = rs.getString("TABLE_TYPE");
                System.out.println(name + ";" + schema + ";" + cat + ";" + type);
            }

            // select
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM DEMO.CUSTOMER");
            while(rs != null && rs.next())
            {
                // get the table name
                name = rs.getString("NAME");
                System.out.println(name);
            }
        }
    }

    /**
     * Test SQL Server
     */
    private void onTest3() throws Exception
    {
        // get the selected ds
        DataObject dobj = _tree.getSelectedDataObject();
        if(dobj instanceof EntityDobj)
        {
            // cast entity
            EntityDobj ent = (EntityDobj)dobj;

            // get the ds
            DoTreeNode parentNode = (DoTreeNode)_tree.getSelectedNode().getParent();
            DataSourceDobj dsDobj = (DataSourceDobj)_tree.getDataObject(parentNode);

            // conn
            JdbcDataSource ds = (JdbcDataSource)MdnDataCache.getCache().getDataSource(
                dsDobj.getId(), true);
            Connection conn = ds.getConnection();

            // select
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + ent.getName());

            // get the rs meta data
            if(rs != null && rs.next())
            {
                ResultSetMetaData md = rs.getMetaData();
                for(int i = 1; i <= md.getColumnCount(); i++)
                {
                    System.out.println("Col " + i + "; Name = " + md.getColumnName(i)
                        + "; Name = " + md.getColumnName(i)
                        + "; Class = " + md.getColumnClassName(i)
                        + "; Type = " + md.getColumnType(i)
                        + "; Type Name = " + md.getColumnTypeName(i)
                        + "; Label = " + md.getColumnLabel(i)
                        + "; Display Size = " + md.getColumnDisplaySize(i)
                        );
                    if(md.getColumnType(i) == java.sql.Types.LONGVARCHAR)
                    {
                        try
                        {
                            System.out.println("   Value = " + rs.getString(i));
                        }
                        catch(Exception e)
                        {
                        }
                    }
                    else
                        System.out.println("   Value = " + rs.getString(i));
                }
            }
        }
    }

    /**
     * Test multiple connections
     */
    private void onTest2() throws Exception
    {
        assert false: "Commented out, jdc";
        /*
        // get the selected ds
        DataObject dobj = _tree.getSelectedDataObject();
        if(dobj instanceof DataSourceDobj)
        {
            // conn 1
            JdbcDataSource ds1 = (JdbcDataSource)MdnDataCache.getCache().getDataSource(
                ((DataSourceDobj)dobj).getId(), true);
            Connection conn1 = ds1.getConnection();

            // conn 2
            JdbcDataSource ds2 = (JdbcDataSource)MdnDataCache.getCache().getDataSource(
                ((DataSourceDobj)dobj).getId(), true);
            Connection conn2 = ds2.getConnection();

            // select on 1
            Statement stmt1 = conn1.createStatement();
            ResultSet rs1 = stmt1.executeQuery("SELECT * FROM TBL_ENTITY");

            // select on 2
            Statement stmt2 = conn2.createStatement();
            ResultSet rs2 = stmt2.executeQuery("SELECT * FROM TBL_ENTITY");

            // iterate
            while(rs1 != null && rs2 != null && rs1.next() && rs2.next())
            {
                // output 1
                System.out.println("Obj 1: " + rs1.getObject(2));
                System.out.println("Obj 2: " + rs2.getObject(2) + "\n");
            }
        }
    */
    }
}
