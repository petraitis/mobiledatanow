//==============================================================================
// UserTabPane.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.security.gui;

import wsl.fw.util.Log;
import wsl.fw.util.Util;
import wsl.fw.gui.WslTabChildPanel;
import wsl.fw.gui.WslButton;
import wsl.fw.gui.GuiManager;
import wsl.fw.gui.DataObjectTree;
import wsl.fw.gui.DoTreeNode;
import wsl.fw.gui.DoComparator;
import wsl.fw.gui.GuiConst;
import wsl.fw.security.User;
import wsl.fw.security.Group;
import wsl.fw.security.GroupMembership;
import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.DataManager;
import wsl.fw.datasource.DataSource;
import wsl.fw.datasource.DataSourceException;
import wsl.fw.datasource.Query;
import wsl.fw.datasource.QueryCriterium;
import wsl.fw.datasource.RecordSet;
import wsl.fw.datasource.DataListenerData;
import wsl.fw.help.HelpId;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeExpansionListener;
import javax.swing.JScrollPane;
import wsl.fw.resource.ResId;

//--------------------------------------------------------------------------
/**
 * Tab child panel for editing Users and user relationships.
 */
public class UserTabPane extends WslTabChildPanel
    implements ActionListener, TreeExpansionListener, TreeSelectionListener
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/security/gui/UserTabPane.java $ ";

    // resources
    public static final ResId BUTTON_NEW_USER  = new ResId("UserTabPane.button.NewUser");
    public static final ResId BUTTON_ADD_GROUP  = new ResId("UserTabPane.button.AddGroup");
    public static final ResId BUTTON_PROPERTIES  = new ResId("UserTabPane.button.Properties");
    public static final ResId BUTTON_DELETE  = new ResId("UserTabPane.button.Delete");
    public static final ResId TITLE_USERS  = new ResId("UserTabPane.title.Users");
    public static final ResId TREE_USERS  = new ResId("UserTabPane.tree.Users");
    public static final ResId DEBUG_EXPANDED  = new ResId("UserTabPane.debug.Expanded");
    public static final ResId ERR_RELATIONSHIPS  = new ResId("UserTabPane.error.Relationships");
    public static final ResId WARNING_DATAOBJECT_NULL  = new ResId("UserTabPane.warning.DataObjectNull");
    public static final ResId ERR_PARENT_USER  = new ResId("UserTabPane.error.ParentUser");
    public static final ResId MSG_FAILED_TO_DELETE  = new ResId("UserTabPane.msg.FailedToDelete");
    public static final ResId MSG_SURE_TO_DELETE1  = new ResId("UserTabPane.msg.SureToDelete1");
    public static final ResId MSG_SURE_TO_DELETE2  = new ResId("UserTabPane.msg.SureToDelete2");
    public static final ResId MSG_SURE_TO_DELETE3  = new ResId("UserTabPane.msg.SureToDelete3");
    public static final ResId WARNING_UNEXPECTED_DATAOBJECT  = new ResId("UserTabPane.warning.UnexpectedDataObject");
    public static final ResId MSG_FAILED_TO_DELETE2  = new ResId("UserTabPane.msg.FailedToDelete2");
    public static final ResId ERR_NO_USER_SELECTED  = new ResId("UserTabPane.error.NoUserSelected");
    public static final ResId ERR_FAILED_TO_SAVE  = new ResId("UserTabPane.error.FailedToSave");

    // help id
    public final static HelpId HID_USERADMIN = new HelpId("fw.security.gui.UserTabPane");

    // attributes
    UserGroupMaintenancePanel _ugmpParent;
    private DataObjectTree    _tree;

    // controls
    private WslButton _btnNewUser      = new WslButton(BUTTON_NEW_USER.getText(), this);
    private WslButton _btnAddGroup     = new WslButton(BUTTON_ADD_GROUP.getText(), this);
    private WslButton _btnProperties   = new WslButton(BUTTON_PROPERTIES.getText(), this);
    private WslButton _btnDelete       = new WslButton(BUTTON_DELETE.getText() , this);
    private WslButton _buttons[] = { _btnNewUser, _btnAddGroup,
        _btnProperties, _btnDelete };

    //--------------------------------------------------------------------------
    /**
     * Constructor
     */
    public UserTabPane(UserGroupMaintenancePanel ugmpParent)
    {
        // call base class constructor to set the title
        super(TITLE_USERS.getText());

        // store ref to parent panel which maintains the user/group/feature sets
        _ugmpParent = ugmpParent;

        // add controls
        createControls();
    }

    //--------------------------------------------------------------------------
    /**
     * Create the Tree control.
     */
    private void createControls()
    {
        // create the tree and scroller
        _tree = new DataObjectTree(TREE_USERS.getText());
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
     * Get the buttons that this tab wants to display in the parent's button
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
        Log.debug("UserTabPane.onSelected");
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
            if(event.getSource().equals(_btnNewUser))
                onNewUser();
            else if(event.getSource().equals(_btnAddGroup))
                onAddGroup();
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
     * Tree selection changed
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
     * Tree node collapsed
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

        // enable
        _btnAddGroup.setEnabled(isUser);
        _btnProperties.setEnabled(hasDataObject);
        _btnDelete.setEnabled(isGroup || isUser);
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


        Log.debug("UserTabPane.buildNode: " + parentNode.toString());

        // get parent data object and key
        DataObject doParent = parentNode.getDataObject();

        // if the doParent is null, then it is the root
        if(doParent == null)
        {
            // set root categories, which are the groups
            Enumeration userEnum = _ugmpParent.getUsers().elements();
            while (userEnum.hasMoreElements())
            {
                User user = (User) userEnum.nextElement();
                _tree.addNode(parentNode, user);
            }
        }
        else if(doParent instanceof User)
        {
            try
            {
                // else, building a User's children
                User parentUser = (User) doParent;

                // select the groups
                DataSource ds    = DataManager.getDataSource(GroupMembership.ENT_GROUPMEMBERSHIP);
                Query      query = new Query(new QueryCriterium(GroupMembership.ENT_GROUPMEMBERSHIP,
                    GroupMembership.FLD_USERID, QueryCriterium.OP_EQUALS, parentUser.getId()));
                RecordSet rs = ds.select(query);

                // build the group nodes
                while (rs.next())
                {
                    GroupMembership gm = (GroupMembership) rs.getCurrentObject();

                    // get the matching Group and add a node
                    Group group = _ugmpParent.getGroup(new Integer(gm.getGroupId()));
                    if (group != null)
                    {
                        DoTreeNode node = _tree.addNode(parentNode, group);
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
     * Return the preferred size
     */
    public Dimension getPreferredSize()
    {
        return new Dimension(350, 350);
    }

    //--------------------------------------------------------------------------
    /**
     * Panel closing
     */
    public void onClosePanel()
    {
        // remove data listeners
        _tree.removeAllListeners();

        Log.debug("UserTabPane.onClosePanel");

        super.onClosePanel();
    }

    //--------------------------------------------------------------------------
    /**
     * Display a Properties panel for the selected node
     */
    private void onProperties()
    {
        // get the selected DataObject
        DataObject dobj = _tree.getSelectedDataObject();

        // open a prop panel
        if(dobj != null)
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
                Log.warning(WARNING_DATAOBJECT_NULL.getText());
            else if (dobj instanceof Group)
            {
                // remove GroupMembership
                User parentUser = getParentUserOfSelected();

                // fail if we cannot resolve the user, should never happen
                if (parentUser == null)
                    Log.error(ERR_PARENT_USER.getText());
                else
                {
                    Group group = (Group) dobj;

                    // find the matching GroupMembership
                    GroupMembership gm = new GroupMembership(group, parentUser);
                    if (gm.load())
                    {
                        // found the GroupMembership, delete it and remove tree node
                        gm.delete();
                        _tree.removeSelectedNode();
                    }
                    else
                        GuiManager.showErrorDialog(this,
                            MSG_FAILED_TO_DELETE.getText(),
                            null);
                }
            }
            else if (dobj instanceof User)
            {
                // confirm User deletion
                String message[] = { MSG_SURE_TO_DELETE1.getText(),
                    MSG_SURE_TO_DELETE2.getText() + " " + dobj.toString() + MSG_SURE_TO_DELETE3.getText() };
                if (GuiManager.showConfirmDialog(this, message))
                        // remove user (cascading delete)
                        dobj.delete();
            }
            else
                Log.warning(WARNING_UNEXPECTED_DATAOBJECT.getText() + " "
                    + dobj.getClass().getName()); // should never happen
        }
        catch (DataSourceException e)
        {
            Log.error("UserTabPane.onDelete: " + e.toString());
            GuiManager.showErrorDialog(this, MSG_FAILED_TO_DELETE2.getText(), e);
        }
    }
    //--------------------------------------------------------------------------
    /**
     * Add a new User.
     */
    private void onNewUser()
    {
        // create a blank User
        User newUser = new User();

        // open an OkCancelPanel to input data and create object
        GuiManager.openOkCancelPanel(getFrameParent(), newUser, true);

        // if new user created add to the tree
        if(newUser.getState() != DataObject.NEW)
            _tree.addNode(_tree.getRoot(), newUser);
    }
    //--------------------------------------------------------------------------
    /**
     * Add (select existing) a Group to the user.
     */
    private void onAddGroup()
    {
        // get the parent user, abort if none
        User parentUser = getParentUserOfSelected();
        if(parentUser == null)
        {
            Log.error(ERR_NO_USER_SELECTED.getText());
            return;
        }

        // build the vector of excluded Groups (those that are already in user)
        DoTreeNode  selectedNode = _tree.getSelectedNode();
        Vector      vExclude     = new Vector();
        Enumeration nodeEnum         = selectedNode.children();

        while (nodeEnum.hasMoreElements())
        {
            DataObject dobj = ((DoTreeNode) nodeEnum.nextElement()).getDataObject();
            vExclude.add(dobj);
        }

        // select the group
        Group group = _ugmpParent.selectGroup(vExclude);

        // if item selected make new GroupMembership and save it
        if (group != null)
        {
            try
            {
                // save relationship
                GroupMembership gm = new GroupMembership(group, parentUser);
                if (!gm.load())
                {
                    gm.save();

                    // add node in tree
                    _tree.addNode(selectedNode, group, false);
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
     * @return the User of the node which is or is the direct parent of the
     *   currently selected node, null in no selection or no User parent.
     */
    User getParentUserOfSelected()
    {
        User user = null;

        DoTreeNode node = _tree.getSelectedNode();
        if (node != null)
            if (node.getDataObject() != null
                && node.getDataObject() instanceof User)
                user = (User) node.getDataObject();
            else
                if (node.getParent() != null
                    && node.getParent() instanceof DoTreeNode)
                {
                    node = (DoTreeNode) node.getParent();
                    if (node.getDataObject() != null
                        && node.getDataObject() instanceof User)
                        user = (User) node.getDataObject();
                }

        return user;
    }

    //--------------------------------------------------------------------------
    /**
     * @return the UserTabPane help id.
     */
    public HelpId getHelpId()
    {
        return HID_USERADMIN;
    }
}

//==============================================================================
// end of file UserTabPane.java
//==============================================================================
