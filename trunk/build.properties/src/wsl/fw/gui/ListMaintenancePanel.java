//==============================================================================
// ListMaintenancePanel.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.gui;

// imports
import java.util.Vector;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;
import wsl.fw.util.Log;
import wsl.fw.util.Util;
import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.RecordSet;
import wsl.fw.datasource.DataSource;
import wsl.fw.datasource.Entity;
import wsl.fw.datasource.DataManager;
import wsl.fw.datasource.QueryCriterium;
import wsl.fw.datasource.Query;
import wsl.fw.datasource.DataListenerData;

import wsl.fw.resource.ResId;

//------------------------------------------------------------------------------
/**
 * MaintenencePanel that adds a list showing all the DataObjects in a recordset
 */
public class ListMaintenancePanel
    extends MaintenancePanel
    implements TreeSelectionListener
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/gui/ListMaintenancePanel.java $ ";

    // resources
    public static final ResId BUTTON_HELP  = new ResId("ListMaintenancePanel.button.Help");

    /**
     * minimum list dimension
     */
    private static int MIN_LIST_X = 300;
    private static int MIN_LIST_Y = 250;

    // attributes
    protected DataObjectTree _treeDataObjects = null;
    protected Class _editClass = null;
    protected Query _query = null;

    //--------------------------------------------------------------------------
    /**
     * Constructor taking a data object class that always calls init.
     * @param editClass the class of DataObjects to show in the list
     */
    public ListMaintenancePanel(Class editClass)
    {
        this(editClass, null, true);
    }

    //--------------------------------------------------------------------------
    /**
     * Constructor taking a data object class that always calls init.
     * @param editClass the class of DataObjects to show in the list
     * @param q a Query whose criteria will be appended to the dobj select
     */
    public ListMaintenancePanel(Class editClass, Query q)
    {
        this(editClass, q, true);
    }

    //--------------------------------------------------------------------------
    /**
     * Constructor taking a data object class that always calls init.
     * @param editClass the class of DataObjects to show in the list
     * @param q a Query whose criteria will be appended to the dobj select
     */
    public ListMaintenancePanel(Class editClass, boolean b)
    {
        this(editClass, null, b);
    }

    //--------------------------------------------------------------------------
    /**
     * Constructor taking a data object class.
     * @param editClass the class of DataObjects to show in the list
     * @param doInit, if true init is calles, else the subclass constructor
     * @param q a Query whose criteria will be appended to the dobj select
     *   must call init itself.
     */
    public ListMaintenancePanel(Class editClass, Query q, boolean doInit)
    {
        // set attributes
        _editClass = editClass;
        _query = q;

        if (doInit)
            init();
    }

    //--------------------------------------------------------------------------
    /**
     * init, called from this or a subclass ctor
     */
    protected void init()
    {
        try
        {

            // init controls
            initListMaintenancePanelControls();

            // build tree
            buildTree();

            // update buttons
            updateButtons();
        }
        catch(Exception e)
        {
            Log.error("ListMaintenancePanel init", e);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Create and init controls
     * @param pnlProps The PropertiesPanel to set into the MaintenancePanel
     */
    private void initListMaintenancePanelControls() throws Exception
    {
        // add the list panel
        getMainPanel().setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel pnlList = new JPanel();
        pnlList.setMinimumSize(new Dimension(MIN_LIST_X, MIN_LIST_Y));
        pnlList.setLayout(new GridBagLayout());
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        pnlList.setBorder(BorderFactory.createLoweredBevelBorder());
        getMainPanel().add(pnlList, gbc);

        // list controls
        gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets.top = GuiConst.DEFAULT_INSET;
        gbc.insets.left = GuiConst.DEFAULT_INSET;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        _treeDataObjects = new DataObjectTree();
        _treeDataObjects.addListener(new DataListenerData(null, _editClass, null));
        JScrollPane sp = new JScrollPane(_treeDataObjects);
        _treeDataObjects.addTreeSelectionListener(this);
        sp.setBorder(BorderFactory.createLoweredBevelBorder());
        pnlList.add(sp, gbc);

        // set the properties panel
        PropertiesPanel pp = null;
        if(_editClass != null)
        {
            // create the panel
            pp = GuiManager.createPropertiesPanel(_editClass);

            // set a new instance
            pp.setDataObject(createNewInstance());
        }
        else
            pp = new NullPropertiesPanel();
        setPropertiesPanel(pp);

        // add buttons to buttons panel
        _btnSelect.setIcon(Util.resourceIcon(GuiConst.FW_IMAGE_PATH + "search.gif"));
        _btnSave.setIcon(Util.resourceIcon(GuiConst.FW_IMAGE_PATH + "save.gif"));
        _btnClear.setIcon(Util.resourceIcon(GuiConst.FW_IMAGE_PATH + "new.gif"));
        _btnDelete.setIcon(Util.resourceIcon(GuiConst.FW_IMAGE_PATH + "delete.gif"));
        _btnClose.setIcon(Util.resourceIcon(GuiConst.FW_IMAGE_PATH + "close.gif"));
        addButton(_btnSave);
        addButton(_btnClear);
        addButton(_btnDelete);

        // add help button
        if (_pnlProps != null)
            if (_pnlProps.getHelpId() != null)
                addHelpButton(BUTTON_HELP.getText(), _pnlProps.getHelpId());

        addButton(_btnClose);

        // add custom buttons
        addCustomButtons(_pnlProps.getCustomButtons());
    }

    //--------------------------------------------------------------------------
    /**
     * Set a properties panel into the maintenance panel
     */
    public void setPropertiesPanel(PropertiesPanel pnlProps)
    {
        // remove the old panel
        if(_pnlProps != null)
            getMainPanel().remove(_pnlProps);

        // set the attribute
        _pnlProps = pnlProps;

        // set the maintenance parent
        _pnlProps.setMaintenanceParent(this);

        // add to the maintenance panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        _pnlProps.setBorder(BorderFactory.createLoweredBevelBorder());
        getMainPanel().add(_pnlProps, gbc);
    }

    //--------------------------------------------------------------------------
    /**
     * Called by framework when closing the panel
     */
    public void onClosePanel()
    {
        // remove listeners
        _treeDataObjects.removeAllListeners();

        // super
        super.onClosePanel();
    }

    //--------------------------------------------------------------------------
    /**
     * Build the DataObject list from the edit class
     */
    protected void buildTree() throws Exception
    {
        // validate
        Util.argCheckNull(_editClass);

        // if class is set use it
        String entityName = "";
        if(_editClass != null)
        {
            DataObject dobj = (DataObject)_editClass.newInstance();
            entityName = dobj.getEntityName();
        }

        // get the DataSource and Entity
        DataSource ds = DataManager.getDataSource(entityName);
        Entity ent = DataManager.getEntity(entityName);
        if(ds != null && ent != null)
        {
            // build the query
            Query q = new Query(entityName);
            if(_query != null)
            {
                QueryCriterium qc;
                Vector v = _query.getCriteria();
                for(int i = 0; v != null && i < v.size(); i++)
                    q.addQueryCriterium((QueryCriterium)v.elementAt(i));
            }

            // select the DataObjects
            RecordSet rs = ds.select(q);

            // clear tree
            _treeDataObjects.clear();
            DoTreeNode root = _treeDataObjects.getRoot();

            // build the list
            DataObject dobj;
            while(rs.next())
            {
                // get the DataObject and add to list
                dobj = rs.getCurrentObject();
                if(dobj != null)
                    _treeDataObjects.addNode(root, dobj, false);
            }

            // refresh the tree
            _treeDataObjects.refreshModel();
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Tree has been clicked
     */
    public void valueChanged(TreeSelectionEvent e)
    {
        // get the selected object
       DataObject dobj = (DataObject)_treeDataObjects.getSelectedDataObject();

        // get a properties panel for the selected object
        if(dobj != null)
        {
            // set the data object
            getPropertiesPanel().setDataObject(dobj);
        }
        else
        {
            // no selected object, create a new blank one ready for insert and
            // put it in the properties panel, this effectively clears the panel

            // must have an edit class
            Util.argCheckNull(_editClass);

            // create a new instance
            try
            {
                dobj = createNewInstance();
            }
            catch (Exception ex)
            {
                Log.error("ListMaintenancePanel.valueChanged", ex);
            }

            // set the new instance into the properties panel
            getPropertiesPanel().setDataObject(dobj);
        }

        // update buttons
        updateButtons();
    }

    /**
     * Create a new instance
     */
    protected DataObject createNewInstance() throws Exception
    {
        return (DataObject)_editClass.newInstance();
    }


    //--------------------------------------------------------------------------
    /**
     * Clear the list selection as well as the properties panel
     */
    protected void onClear()
    {
        // clear the list selection, this will cause valueChanged to be called
        // which clears the properties panel
        _treeDataObjects.getSelectionModel().clearSelection();
    }

    //--------------------------------------------------------------------------
    /**
     * Update the list onSave
     * @return boolean true if the save was successful
     */
    protected boolean onSave() throws Exception
    {
        // super
        if(super.onSave())
        {
            // update the list
            DataObject selObj = _treeDataObjects.getSelectedDataObject();
            if(selObj == null)
            {
                // this is an insert rather than an update
                // get the new DataObject
                DataObject dobj = getPropertiesPanel().getDataObject();

                // insert into list
                DoTreeNode node = _treeDataObjects.addNode(_treeDataObjects.getRoot(), dobj, false);

                // select the new object in the tree
                _treeDataObjects.setSelectionPath(new TreePath(node.getPath()));
            }

            return true;
        }
        else
            return false;
    }

    //--------------------------------------------------------------------------
    /**
     * Update the list on delete
     */
    protected void onDelete() throws Exception
    {
        // super
        super.onDelete();
    }

    //--------------------------------------------------------------------------
    /**
     * @return the preferred size for the MaintenancePanel
     */
    public Dimension getPreferredSize()
    {
        // set defaults
        int x = getMinListX();
        int y = getMinListY();

        // if child is bigger, go bigger
        JPanel p = getPropertiesPanel();
        if(p != null)
        {
            Dimension d = p.getPreferredSize();
            x = (x > (int)d.getWidth())? x: (int)d.getWidth();
            y += (int)d.getHeight();
        }

        // add the button panel width
        p = getButtonPanel();
        if(p != null)
        {
            Dimension d = p.getPreferredSize();
            x += (int)d.getWidth();
        }

        // return
        return new Dimension(x, y);
    }

    /**
     * @return int the minimum y size of the list
     */
    protected int getMinListY()
    {
        return MIN_LIST_Y;
    }

    /**
     * @return int the minimum x size of the list
     */
    protected int getMinListX()
    {
        return MIN_LIST_X;
    }
}

//==============================================================================
// end of file ListMaintenancePanel.java
//==============================================================================
