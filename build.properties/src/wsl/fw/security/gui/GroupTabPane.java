//==============================================================================
// GroupTabPane.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.security.gui;

import wsl.fw.util.Log;
import wsl.fw.datasource.DataListenerData;
import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.DataManager;
import wsl.fw.datasource.DataSource;
import wsl.fw.datasource.DataSourceException;
import wsl.fw.datasource.Query;
import wsl.fw.datasource.QueryCriterium;
import wsl.fw.datasource.RecordSet;
import wsl.fw.gui.GuiConst;
import wsl.fw.gui.WslTabChildPanel;
import wsl.fw.gui.WslButton;
import wsl.fw.gui.GuiManager;
import wsl.fw.gui.DataObjectTree;
import wsl.fw.gui.DoTreeNode;
import wsl.fw.gui.DoComparator;
import wsl.fw.security.Group;
import wsl.fw.security.GroupMembership;
import wsl.fw.security.User;
import wsl.fw.security.Privilege;
import wsl.fw.security.Feature;
import wsl.fw.util.Util;
import wsl.fw.help.HelpId;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.util.Enumeration;
import java.util.Vector;
import wsl.fw.resource.ResId;

//------------------------------------------------------------------------------
/**
 * Tab child panel for editing Groups and group relationships (i.e. group
 * membership and privileges).
 */
public class GroupTabPane
    extends WslTabChildPanel
    implements ActionListener, TreeExpansionListener, TreeSelectionListener
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/security/gui/GroupTabPane.java $ ";

    // resources
    public static final ResId BUTTON_NEW_GROUP  = new ResId("GroupTabPane.button.NewGroup");
    public static final ResId BUTTON_ADD_PRIV  = new ResId("GroupTabPane.button.AddPriv");
    public static final ResId BUTTON_ADD_USER  = new ResId("GroupTabPane.button.AddUser");
    public static final ResId BUTTON_PROPERTIES  = new ResId("GroupTabPane.button.Properties");
    public static final ResId BUTTON_DELETE  = new ResId("GroupTabPane.button.Delete");
    public static final ResId TITLE_GROUP_TAB_PANE = new ResId("GroupTabPane.title.GroupTabPane");
    public static final ResId TREE_GROUPS = new ResId("GroupTabPane.tree.Groups");
    public static final ResId DEBUG_EXPANDED = new ResId("GroupTabPane.debug.Expanded");
    public static final ResId ERR_RELATIONSHIPS = new ResId("GroupTabPane.error.Relationships");
    public static final ResId WARNING_SELECTED_IS_NULL = new ResId("GroupTabPane.warning.SelectedIsNull");
    public static final ResId ERR_PARENT_GROUP = new ResId("GroupTabPane.error.ParentGroup");
    public static final ResId ERR_FAILED_TO_DELETE = new ResId("GroupTabPane.error.FailedToDelete");
    public static final ResId ERR_ON_DELETE = new ResId("GroupTabPane.error.OnDelete");
    public static final ResId ERR_GROUP_MEMBERSHIP = new ResId("GroupTabPane.error.GroupMembership");
    public static final ResId MSG_DELETE1 = new ResId("GroupTabPane.msg.Delete1");
    public static final ResId MSG_DELETE2 = new ResId("GroupTabPane.msg.Delete2");
    public static final ResId WARNING_UNEXPECTED_DATAOBJECT = new ResId("GroupTabPane.warning.UnexpectedDataObject");
    public static final ResId ERR_DIALOG_FAILED_TO_DELETE = new ResId("GroupTabPane.error.DialogFailedToDelete");
    public static final ResId ERR_NO_GROUP_SELECTED = new ResId("GroupTabPane.error.NoGroupSelected");
    public static final ResId ERR_FAILED_TO_SAVE = new ResId("GroupTabPane.error.FailedToSave");
    public static final ResId ERR_ADD_USER_NO_GROUP_SELECTED = new ResId("GroupTabPane.error.AddUserNoGroupSelected");
    public static final ResId ERR_FAILED_TO_SAVE_GROUP_MEMBERSHIP = new ResId("GroupTabPane.error.FailedToSaveGroupMembership");

    // help id
    public final static HelpId HID_GROUPADMIN = new HelpId("fw.security.gui.GroupTabPane");

    // attributes
    UserGroupMaintenancePanel _ugmpParent;
    private DataObjectTree    _tree;

    // controls
    private WslButton _btnNewGroup     = new WslButton(BUTTON_NEW_GROUP.getText(), this);
    private WslButton _btnAddPrivilege = new WslButton(BUTTON_ADD_PRIV.getText(), this);
    private WslButton _btnAddUser      = new WslButton(BUTTON_ADD_USER.getText(), this);
    private WslButton _btnProperties   = new WslButton(BUTTON_PROPERTIES.getText(), this);
    private WslButton _btnDelete       = new WslButton(BUTTON_DELETE.getText(), this);
    private WslButton _buttons[] = { _btnNewGroup, _btnAddPrivilege,
        _btnAddUser, _btnProperties, _btnDelete };

    //--------------------------------------------------------------------------
    /**
     * Constructor.
     */
    public GroupTabPane(UserGroupMaintenancePanel ugmpParent)
    {
        // call base class constructor to set the title
        super(TITLE_GROUP_TAB_PANE.getText());

        // store ref to parent panel which maintains the user/group/feature sets
        _ugmpParent = ugmpParent;

        // add controls
        createControls();
    }

    //--------------------------------------------------------------------------
    /**
     * Create the tree control.
     */
    private void createControls()
    {
        // create the tree and scroller
        _tree = new DataObjectTree(TREE_GROUPS.getText());
        _tree.addTreeExpansionListener(this);
        _tree.addTreeSelectionListener(this);
        JScrollPane sp = new JScrollPane(_tree);
        sp.setPreferredSize(getPreferredSize());

        // set the sort for the tree
        _tree.setSort(new DoComparator());

        // listen for data changes to user and group
        _tree.addListener(new DataListenerData(null, Group.ENT_GROUP, null));
        _tree.addListener(new DataListenerData(null, User.ENT_USER, null));

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
     * Get the buttons that this tab wants to display in the parent's button.
     * panel. This is called each time the tab child panel is selected.
     * @return an array of buttons, may be empty, not null.
     */
    public WslButton[] getButtons()
    {
        return _buttons;
    }

    //--------------------------------------------------------------------------
    /**
     * Notification function called each time the tab is selected or deselected.
     * @param selected, true if being selected, false if being deselected.
     */
    public void onSelected(boolean selected)
    {
        Log.debug("GroupTabPane.onSelected");
        synchronized (this)
        {
            // remove everything
            _tree.clear();
            _tree.getRoot().setChildrenBuilt(false);


            // rebuild tree and relationships when selected
            if (selected)
                buildNode(_tree.getRoot());

            // refresh the tree to ensure proper display
            _tree.refreshModel();

            // update all buttons
            updateButtons();
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Handle actions from buttons.
     */
    public void actionPerformed(ActionEvent event)
    {
        try
        {
            // switch on source button
            if(event.getSource().equals(_btnNewGroup))
                onNewGroup();
            else if(event.getSource().equals(_btnAddPrivilege))
                onAddPrivilege();
            else if(event.getSource().equals(_btnAddUser))
                onAddUser();
            else if(event.getSource().equals(_btnProperties))
                onProperties();
            else if(event.getSource().equals(_btnDelete))
                onDelete();
        }
        catch(Exception e)
        {
            GuiManager.showErrorDialog(this, GuiManager.ERR_UNHANDLED.getText(), e);
            Log.error(GuiManager.ERR_UNHANDLED.getText(), e);
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
        Log.debug(DEBUG_EXPANDED.getText() + " " + ev.getPath().toString());
        synchronized (this)
        {
            // get the expanded node
            DoTreeNode node = (DoTreeNode) ev.getPath().getLastPathComponent();

            // build the node
            if(node != null)
                buildNode(node);

            // update buttons
            updateButtons();
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Tree node collapsed.
     */
    public void treeCollapsed(TreeExpansionEvent event)
    {
        synchronized (this)
        {
            // do nothing
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Called by the framework to cause subclass panels to update the state
     * of controls.
     */
    public void updateButtons()
    {
        // flags
        DataObject dobj       = _tree.getSelectedDataObject();
        boolean hasDataObject = dobj != null;
        boolean isGroup       = dobj != null && dobj instanceof Group;
        boolean isUser        = dobj != null && dobj instanceof User;
        boolean isFeature     = dobj != null && dobj instanceof Feature;

        // enable
        _btnAddPrivilege.setEnabled(isGroup);
        _btnAddUser.setEnabled(isGroup);
        _btnProperties.setEnabled(hasDataObject);
        _btnDelete.setEnabled(isGroup || isUser || isFeature);
    }

    //--------------------------------------------------------------------------
    /**
     * Build a node of the tree.
     */
    private synchronized void buildNode(DoTreeNode parentNode)
    {
        Util.argCheckNull(parentNode);

        // if node already has children built then exit
        if (parentNode.getChildrenBuilt())
            return;

        // mark as built
        parentNode.setChildrenBuilt(true);

        // ensure all children are removed
        _tree.clearNode(parentNode);


        Log.debug("GroupTabPane.buildNode: " + parentNode.toString());

        // get parent data object and key
        DataObject doParent = parentNode.getDataObject();

        // if the doParent is null, then it is the root
        if(doParent == null)
        {
            // set root categories, which are the groups
            Enumeration groupEnum = _ugmpParent.getGroups().elements();
            while (groupEnum.hasMoreElements())
            {
                Group group = (Group) groupEnum.nextElement();
                _tree.addNode(parentNode, group);
            }
        }
        else if(doParent instanceof Group)
        {
            try
            {
                // else, building a Group
                Group parentGroup = (Group) doParent;

                // select the priv/features
                DataSource ds    = DataManager.getDataSource(Privilege.ENT_PRIVILEGE);
                Query      query = new Query(new QueryCriterium(Privilege.ENT_PRIVILEGE,
                    Privilege.FLD_GROUPID, QueryCriterium.OP_EQUALS, parentGroup.getId()));
                RecordSet rs = ds.select(query);

                // build the feature nodes
                while (rs.next())
                {
                    Privilege priv = (Privilege) rs.getCurrentObject();

                    // get the matching feature and add a node
                    Feature feature = _ugmpParent.getFeature(priv.getFeatureId());
                    if (feature != null)
                    {
                        DoTreeNode node = _tree.addNode(parentNode, feature);
                        node.setAllowsChildren(false);
                    }
                }

                // select the users
                ds = DataManager.getDataSource(User.ENT_USER);
                query = new Query(new QueryCriterium(GroupMembership.ENT_GROUPMEMBERSHIP,
                    GroupMembership.FLD_GROUPID, QueryCriterium.OP_EQUALS,
                    parentGroup.getId()));
                rs = ds.select(query);

                // build the user nodes
                while(rs.next())
                {
                    GroupMembership gm = (GroupMembership) rs.getCurrentObject();

                    // get matching user and add a node
                    User user = _ugmpParent.getUser(new Integer(gm.getUserId()));
                    if (user != null)
                    {
                        DoTreeNode node = _tree.addNode(parentNode, user);
                        node.setAllowsChildren(false);
                    }
                }
            }
            catch (DataSourceException e)
            {
                Log.error(ERR_RELATIONSHIPS.getText() + " ", e);
            }
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
        Log.debug("GroupTabPane.onClosePanel");

        super.onClosePanel();
    }

    //--------------------------------------------------------------------------
    /**
     * Return the preferred size
     */
    public Dimension getPreferredSize()
    {
        return new Dimension(350, 350);
    }

    //--------------------------------------------------------------------------
    /**
     * Display a Properties panel for the selected node.
     */
    private void onProperties()
    {
        // get the selected DataObject
        DataObject dobj = _tree.getSelectedDataObject();

        // open a prop panel, in the case of feature do not save since features
        // should not be edited
        if(dobj != null)
            if (dobj instanceof Feature)
                GuiManager.openOkPanel(getFrameParent(), dobj, true, false);
            else
                GuiManager.openOkCancelPanel(getFrameParent(), dobj, true);
    }

    //--------------------------------------------------------------------------
    /**
     * Delete the selected node.
     * The exact operation depends on the type of the selected node.
     */
    private void onDelete()
    {
        try
        {
            DataObject dobj = _tree.getSelectedDataObject();

            if (dobj == null)
                Log.warning(WARNING_SELECTED_IS_NULL.getText());
            else if (dobj instanceof Feature)
            {
                // remove Privilege
                Group parentGroup = getParentGroupOfSelected();

                // fail if we cannot resolve the group, should never happen
                if (parentGroup == null)
                    Log.error(ERR_PARENT_GROUP.getText());
                else
                {
                    Feature feature = (Feature) dobj;

                    // find the matching priv
                    Privilege priv = new Privilege(parentGroup, feature);
                    if (priv.load())
                    {
                        // found the priv, delete it and remove tree node
                        priv.delete();
                        _tree.removeSelectedNode();
                    }
                    else
                        GuiManager.showErrorDialog(this,
                            ERR_FAILED_TO_DELETE.getText() ,
                            null);
                }
            }
            else if (dobj instanceof User)
            {
                // remove GroupMembership
                Group parentGroup = getParentGroupOfSelected();

                // fail if we cannot resolve the group, should never happen
                if (parentGroup == null)
                    Log.error(ERR_ON_DELETE.getText());
                else
                {
                    User user = (User) dobj;

                    // find the matching membership
                    GroupMembership gm = new GroupMembership(parentGroup, user);
                    if (gm.load())
                    {
                        // found the membership, delete it and remove tree node
                        gm.delete();
                        _tree.removeSelectedNode();
                    }
                    else
                        GuiManager.showErrorDialog(this,
                            ERR_GROUP_MEMBERSHIP.getText(),
                            null);
                }
            }
            else if (dobj instanceof Group)
            {
                // confirm Group deletion
                String message[] = { MSG_DELETE1.getText(),
                    " " + dobj.toString() + MSG_DELETE2.getText() };
                if (GuiManager.showConfirmDialog(this, message))
                        // remove group (cascading delete)
                        dobj.delete();
            }
            else
                Log.warning(WARNING_UNEXPECTED_DATAOBJECT.getText() + " "
                    + dobj.getClass().getName()); // should never happen
        }
        catch (DataSourceException e)
        {
            Log.error("GroupTabPane.onDelete: " + e.toString());
            GuiManager.showErrorDialog(this, ERR_DIALOG_FAILED_TO_DELETE.getText(), e);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Add a new group.
     */
    private void onNewGroup()
    {
        // create a blank group
        Group newGroup = new Group();

        // open an OkCancelPanel to input data and create object
        GuiManager.openOkCancelPanel(getFrameParent(), newGroup, true);

        // if new group created add to the tree
        if(newGroup.getState() != DataObject.NEW)
            _tree.addNode(_tree.getRoot(), newGroup);
    }

    //--------------------------------------------------------------------------
    /**
     * Add (select existing) a Feature/Privilege to the group.
     */
    private void onAddPrivilege()
    {
        // get the parent group, abort if none
        Group parentGroup = getParentGroupOfSelected();
        if(parentGroup == null)
        {
            Log.error(ERR_NO_GROUP_SELECTED.getText());
            return;
        }

        // build the vector of excluded Features (those that are already in group)
        DoTreeNode  selectedNode = _tree.getSelectedNode();
        Vector      vExclude     = new Vector();
        Enumeration nodeEnum         = selectedNode.children();

        while (nodeEnum.hasMoreElements())
        {
            DataObject dobj = ((DoTreeNode) nodeEnum.nextElement()).getDataObject();
            vExclude.add(dobj);
        }

        // select the feature
        Feature feature = _ugmpParent.selectFeature(vExclude);

        // if item selected make new privilege and save it
        if (feature != null)
        {
            try
            {
                // save relationship
                Privilege priv = new Privilege(parentGroup, feature);
                if (!priv.load())
                {
                    priv.save();

                    // add node in tree
                    _tree.addNode(selectedNode, feature, false);
                }
            }
            catch (DataSourceException e)
            {
                GuiManager.showErrorDialog(this, ERR_FAILED_TO_SAVE.getText(), e);
                Log.error(ERR_FAILED_TO_SAVE.getText(), e);
            }
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Add (select existing) a user to the group.
     */
    private void onAddUser()
    {
        // get the parent group, abort if none
        Group parentGroup = (Group) getParentGroupOfSelected();
        if(parentGroup == null)
        {
            Log.error(ERR_ADD_USER_NO_GROUP_SELECTED.getText());
            return;
        }

        // build the vector of excluded users (those that are already in group)
        DoTreeNode  selectedNode = _tree.getSelectedNode();
        Vector      vExclude     = new Vector();
        Enumeration nodeEnum         = selectedNode.children();

        while (nodeEnum.hasMoreElements())
        {
            DataObject dobj = ((DoTreeNode) nodeEnum.nextElement()).getDataObject();
            vExclude.add(dobj);
        }

        // select the user
        User user = _ugmpParent.selectUser(vExclude);

        // if item selected make new group membership and save it
        if (user != null)
        {
            try
            {
                // save relationship
                GroupMembership gm = new GroupMembership(parentGroup, user);
                if (!gm.load())
                {
                    gm.save();

                    // add node in tree
                    _tree.addNode(selectedNode, user, false);
                }
            }
            catch (DataSourceException e)
            {
                GuiManager.showErrorDialog(this, ERR_FAILED_TO_SAVE_GROUP_MEMBERSHIP.getText(), e);
                Log.error(ERR_FAILED_TO_SAVE_GROUP_MEMBERSHIP.getText(), e);
            }
        }
    }

    //--------------------------------------------------------------------------
    /**
     * @return the Group of the node which is or is the direct parent of the
     *   currently selected node, null in no selection or no Group parent.
     */
    Group getParentGroupOfSelected()
    {
        Group group = null;

        DoTreeNode node = _tree.getSelectedNode();
        if (node != null)
            if (node.getDataObject() != null
                && node.getDataObject() instanceof Group)
                group = (Group) node.getDataObject();
            else
                if (node.getParent() != null
                    && node.getParent() instanceof DoTreeNode)
                {
                    node = (DoTreeNode) node.getParent();
                    if (node.getDataObject() != null
                        && node.getDataObject() instanceof Group)
                        group = (Group) node.getDataObject();
                }

        return group;
    }

    //--------------------------------------------------------------------------
    /**
     * @return the GroupTabPane help id.
     */
    public HelpId getHelpId()
    {
        return HID_GROUPADMIN;
    }
}

//==============================================================================
// end of file GroupTabPane.java
//==============================================================================
