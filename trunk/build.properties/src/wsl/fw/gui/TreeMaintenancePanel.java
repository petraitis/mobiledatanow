//==============================================================================
// TreeMaintenancePanel.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.gui;

import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.DataSource;
import wsl.fw.datasource.Query;
import wsl.fw.datasource.QueryCriterium;
import wsl.fw.datasource.RecordSet;
import wsl.fw.datasource.Entity;
import wsl.fw.datasource.DataManager;
import wsl.fw.datasource.DataSourceException;
import wsl.fw.datasource.KeyConstraintException;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import javax.swing.tree.TreePath;

import wsl.fw.resource.ResId;

//--------------------------------------------------------------------------
/**
 * Maintenance panel for editing a tree hierarchy.
 * Fixme, may want to add cascading delete.
 */
public class TreeMaintenancePanel extends ListMaintenancePanel
{
    // version tag
    private final static String _ident = "$Date: 2004/01/06 01:45:33 $  $Revision: 1.2 $ "
        + "$Archive: /Framework/Source/wsl/fw/gui/TreeMaintenancePanel.java $ ";

    // resources
    public static final ResId ERR_PARENT_FIELD  = new ResId("TreeMaintenancePanel.error.ParentField");
    public static final ResId ERR_UNEXPECTED  = new ResId("TreeMaintenancePanel.error.Unexpected");

    // attributes
    private String _parentField;

    //--------------------------------------------------------------------------
    /**
     * Constructor. Builds a TreeMaintenancePanel that edits the DataObject
     * specified by editClass with a hierarchal relationship between ints system
     * key and the key in the field specified by parentField.
     * @param editClass, the class of the DataObject to edit.
     * @param parentField, the name of the field in the DataObject that holds
     * the hierarchal relationship.
     */
    public TreeMaintenancePanel(Class editClass, String parentField)
    {
        // pass on to superclass, deferring init
        super(editClass, false);

        // check params
        Util.argCheckNull(editClass);
        Util.argCheckNull(parentField);

        // set the parent field
        _parentField = parentField;

        // now call init
        super.init();

        // make the root node visible
        _treeDataObjects.setRootVisible(true);
    }

    //--------------------------------------------------------------------------
    /**
     * Build the DataObject tree from the edit class, called from super.init().
     * Fixme, may want to alter tree build behaviour to  build selectively
     * as nodes are expanded.
     */
    protected void buildTree() throws Exception
    {
        // edit class must be available
        Util.argCheckNull(_editClass);

        // get a datasource and entity details
        DataObject dobj = (DataObject)_editClass.newInstance();
        Entity entity = dobj.getEntity();
        String entityName = dobj.getEntityName();
        String keyName = entity.getSystemKeyField().getName();
        DataSource ds = DataManager.getDataSource(entityName);

        // check that the entity has the parentField
        assert entity.getField(_parentField) != null:
            "TreeMaintenancePanel.buildTree: Entity ["
            + entityName + "] " + ERR_PARENT_FIELD.getText() + " ["
            + _parentField + "]";

        // clear tree
        _treeDataObjects.clear();

        // build the tree staring at the root
        buildSubTree(ds, entityName, keyName, _treeDataObjects.getRoot());

        // refresh the tree to ensure proper display
        _treeDataObjects.refreshModel();
    }

    //--------------------------------------------------------------------------
    /**
     * Recursively build the tree.
     * @param ds, the DataSource to query for the tree nodes.
     * @param entityName, the entity name of the DataObject comprising the tree.
     * @param keyName, the name of the systemkey field for this entity.
     * @param parent, the DoTreeNode that is parent to this branch of the tree.
     * @throws DataSourceException if there is an error performing the query.
     */
    private void buildSubTree(DataSource ds, String entityName,
        String keyName, DoTreeNode parent)
        throws DataSourceException
    {
        // get parent data object and key
        DataObject doParent = parent.getDataObject();
        Object parentKey = (doParent != null) ? doParent.getObjectValue(keyName) : null;

        // build query, if the parent is root query for nodes with a null parent
        // else query for nodes who are children of the parent node.
        Query query = new Query();
        if (parentKey == null)
            query.addQueryCriterium(new QueryCriterium(entityName, _parentField,
                QueryCriterium.OP_IS_NULL, null));
        else
            query.addQueryCriterium(new QueryCriterium(entityName, _parentField,
                QueryCriterium.OP_EQUALS, parentKey));

        // select the DataObjects
        RecordSet rs = ds.select(query);

        // add to tree and recurse to get sub-branches
        DataObject dobj;
        while(rs.next())
        {
            // get the DataObject
            dobj = rs.getCurrentObject();
            if(dobj != null)
            {
                // add to tree as child
                DoTreeNode newNode = _treeDataObjects.addNode(parent, dobj);
                // recursively build subtree
                buildSubTree(ds, entityName, keyName, newNode);
            }
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Clear the current item so the next operation will insert a child node.
     */
    protected void onClear()
    {
        // must have an edit class
        Util.argCheckNull(_editClass);

        // create a new instance of the DataObject
        DataObject dobj = null;
        try
        {
            dobj = (DataObject)_editClass.newInstance();
        }
        catch (Exception ex)
        {
            Log.error("TreeMaintenancePanel.onClear", ex);
        }

        // set the new instance into the properties panel, the selected object
        // in the tree will now differ from the edited object in the prop panel
        getPropertiesPanel().setDataObject(dobj);

        // update buttons to show insert action
        updateButtons();
    }

    //--------------------------------------------------------------------------
    /**
     * Save the property panel data object.
     * Copied and modified from MaintenancePanel.onSave which we can't access
     * as it is hidden by our superclass and need to alter anyway.
     * @param parentObj, the parent of the new DataObject if this is an insert
     *   (create) operation rather than an update.
     * @param bCreate, if true this is an insert (create) operation.
     * @throws DataSourceException if the save could not persist to DataSource.
     */
     private boolean savePropPanel(DataObject parentObj, boolean bCreate)
        throws DataSourceException
     {
        // check mandatories
        boolean ret = getPropertiesPanel().checkMandatories();
        if(ret)
        {
            if (bCreate)
            {
                // if creating then set the parent key, first clear in case the
                // parent is null
                getPropertiesPanel().getDataObject().clear();
                if (parentObj != null)
                {
                    // get the key of the parent object
                    Entity ent = parentObj.getEntity();
                    String systemKeyName = ent.getSystemKeyField().getName();
                    Object parentKey = parentObj.getObjectValue(systemKeyName);
                    // set it as the parent of the child we are creating
                    getPropertiesPanel().getDataObject().setValue(_parentField, parentKey);
                }
            }

            // transfer data from controls, the prop panel should not ever
            // set the parent key
            getPropertiesPanel().transferData(true);

            // save the DataObject
            try
            {
                getPropertiesPanel().getDataObject().save();
            }
            catch (KeyConstraintException e)
            {

                GuiManager.showErrorDialog(this, e.getMessage(), null);
                Log.debug("TreeMaintenancePanel.savePropPanel: ", e);
                return false;
            }
            catch (Exception e)
            {
                GuiManager.showErrorDialog(this, ERR_UNEXPECTED.getText(), e);
                Log.error(ERR_UNEXPECTED.getText(), e);
                return false;
            }
        }

        return ret;
    }

    //--------------------------------------------------------------------------
    /**
     * Update the tree on save, or if a new item then insert.
     * @return boolean true if the save was successful
     */
    protected boolean onSave() throws Exception
    {
        DataObject selObj  = _treeDataObjects.getSelectedDataObject();
        DataObject propObj = getPropertiesPanel().getDataObject();
        boolean bCreate = (selObj != propObj);

        if(savePropPanel(selObj, bCreate))
        {
            // if this is a child create then add node to tree and select
            if (bCreate)
            {
                // this is an insert rather than an update
                // get the parent node
                DoTreeNode parentNode = _treeDataObjects.getSelectedNode();
                if (parentNode == null)
                    parentNode = _treeDataObjects.getRoot();

                // insert into tree
                DoTreeNode node = _treeDataObjects.addNode(parentNode, propObj);

                // select the new object in the tree
                _treeDataObjects.setSelectionPath(new TreePath(node.getPath()));
            }

            return true;
        }
        else
            return false;
    }
}

//==============================================================================
// end of file TreeMaintenancePanel.java
//==============================================================================
