//==============================================================================
// PresentationBuilderPanel.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.mdn.guiconfig;

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
import wsl.fw.security.Group;
import wsl.fw.gui.*;
import wsl.mdn.admin.MdnAdminGuiManager;

import wsl.mdn.dataview.*;
import wsl.mdn.common.*;

//------------------------------------------------------------------------------
/**
 * Button panel with tree view used to edit the login settings and build menu
 * structures.
 */
public class PresentationBuilderPanel extends WslButtonPanel
    implements ActionListener, TreeExpansionListener, TreeSelectionListener
{
    // resources
    public static final ResId PANEL_TITLE       = new ResId("PresentationBuilderPanel.PanelTitle");
    public static final ResId TEMPLATES         = new ResId("PresentationBuilderPanel.text.Templates");
    public static final ResId GROUPS            = new ResId("PresentationBuilderPanel.text.Groups");
    public static final ResId BUTTON_ADD_MENU   = new ResId("PresentationBuilderPanel.button.AddMenu");
    public static final ResId BUTTON_ADD_TEXT   = new ResId("PresentationBuilderPanel.button.AddText");
    public static final ResId BUTTON_ADD_NEWRECORD   = new ResId("PresentationBuilderPanel.button.AddNewRecord");
    public static final ResId BUTTON_ADD_QUERYRECORD = new ResId("PresentationBuilderPanel.button.AddQueryRecord");
    public static final ResId BUTTON_DELETE     = new ResId("PresentationBuilderPanel.button.Delete");
    public static final ResId BUTTON_COPY       = new ResId("PresentationBuilderPanel.button.Copy");
    public static final ResId BUTTON_PASTE      = new ResId("PresentationBuilderPanel.button.Paste");
    public static final ResId BUTTON_MOVEUP     = new ResId("PresentationBuilderPanel.button.MoveUp");
    public static final ResId BUTTON_MOVEDOWN   = new ResId("PresentationBuilderPanel.button.MoveDown");
    public static final ResId BUTTON_PROPERTIES = new ResId("PresentationBuilderPanel.button.Properties");
    public static final ResId BUTTON_MSG_SERVER = new ResId ("PresentationBuilderPanel.button.MessageServer");
    public static final ResId BUTTON_HELP       = new ResId("OkPanel.button.Help");
    public static final ResId ERR_BUILD_TREE    = new ResId("PresentationBuilderPanel.error.buildTree");
    public static final ResId ERR_ADD_CHILD     = new ResId("PresentationBuilderPanel.error.addChild");

    public final static HelpId HID_PRESENTATION_BUILDER = new HelpId("mdn.guiconfig.PresentationBuilderPanel");

    // constants
    private static final int BTN_WIDTH = 120;
    private static final int BOT_BTN_WIDTH = 104;

    // attributes
    private WslButton _btnAddMenu = new WslButton(BUTTON_ADD_MENU.getText(), BTN_WIDTH, this);
    private WslButton _btnAddText = new WslButton(BUTTON_ADD_TEXT.getText(), BTN_WIDTH, this);
    private WslButton _btnAddNewRecord = new WslButton(BUTTON_ADD_NEWRECORD.getText(), BTN_WIDTH, this);
    private WslButton _btnAddQueryRecord = new WslButton(BUTTON_ADD_QUERYRECORD.getText(), BTN_WIDTH, this);
    private WslButton _btnDelete = new WslButton(BUTTON_DELETE.getText(), BTN_WIDTH, this);
    private WslButton _btnCopy = new WslButton(BUTTON_COPY.getText(), BOT_BTN_WIDTH, this);
    private WslButton _btnPaste = new WslButton(BUTTON_PASTE.getText(), BOT_BTN_WIDTH, this);
    private WslButton _btnMoveUp = new WslButton(BUTTON_MOVEUP.getText(), BOT_BTN_WIDTH, this);
    private WslButton _btnMoveDown = new WslButton(BUTTON_MOVEDOWN.getText(), BOT_BTN_WIDTH, this);
    private WslButton _btnMsgServer = new WslButton(BUTTON_MSG_SERVER.getText(), BTN_WIDTH, this);
    private WslButton _btnProperties = new WslButton(BUTTON_PROPERTIES.getText(), BTN_WIDTH, this);
    private WslButton _btnClose = new WslButton(MaintenancePanel.BUTTON_CLOSE.getText(), BTN_WIDTH, this);

    private DataObjectTree _tree;
    private DoTreeNode     _templates      = null;
    private DoTreeNode     _groups         = null;
    private MenuAction     _clipboard      = null;

    private JPanel _pnlBottomBtns;

    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public PresentationBuilderPanel()
    {
        // super
        super(WslButtonPanel.VERTICAL);

        // set title
        setPanelTitle(PANEL_TITLE.getText());

        // init controls
        initPresentationBuilderPanelControls();

        // build the tree
        buildTree();

        // update buttons
        updateButtons();
    }

    //--------------------------------------------------------------------------
    /**
     * Initialise controls.
     */
    private void initPresentationBuilderPanelControls()
    {
        // add buttons
        addButton(_btnAddMenu);
        addButton(_btnAddText);
        addButton(_btnAddNewRecord);
        addButton(_btnAddQueryRecord);
        addButton(_btnMsgServer);
        addButton(_btnDelete);
        addButton(_btnProperties);
        addHelpButton(BUTTON_HELP.getText(), HID_PRESENTATION_BUILDER, BTN_WIDTH);
        _btnClose.setIcon(Util.resourceIcon(GuiConst.FW_IMAGE_PATH + "close.gif"));
        addButton(_btnClose);

        // create the tree and add listeners
        _tree = new DataObjectTree();
        _tree.addTreeExpansionListener(this);
        _tree.addTreeSelectionListener(this);
        _tree.addListener(new DataListenerData(null, MenuAction.ENT_MENUACTION , null));
        _tree.addListener(new DataListenerData(null, Group.ENT_GROUP, null));
        JScrollPane sp = new JScrollPane(_tree);

        // add the tree to the main panel
        getMainPanel().setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel pnlTree = new JPanel(new GridBagLayout());
        pnlTree.setBorder(BorderFactory.createLoweredBevelBorder());
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        getMainPanel().add(pnlTree, gbc);
        gbc.insets.top = GuiConst.DEFAULT_INSET;
        gbc.insets.left = GuiConst.DEFAULT_INSET;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        sp.setBorder(BorderFactory.createLoweredBevelBorder());
        pnlTree.add(sp, gbc);

        // bottom button panel
        JPanel pnlBottomBtns = new JPanel(new GridBagLayout());
        pnlBottomBtns.setBorder(BorderFactory.createLoweredBevelBorder());
        pnlBottomBtns.setBackground(Color.gray);
        gbc = new GridBagConstraints();
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        getMainPanel().add(pnlBottomBtns, gbc);

        // bottom buttons
        gbc = new GridBagConstraints();
        gbc.insets.top = GuiConst.DEFAULT_INSET;
        gbc.insets.left = GuiConst.DEFAULT_INSET;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        pnlBottomBtns.add(_btnCopy, gbc);
        gbc.gridx = 1;
        pnlBottomBtns.add(_btnPaste, gbc);
        gbc.gridx = 2;
        pnlBottomBtns.add(_btnMoveUp, gbc);
        gbc.gridx = 3;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        pnlBottomBtns.add(_btnMoveDown, gbc);
    }

    //--------------------------------------------------------------------------
    /**
     * Build the do the initial tree build from the root.
     */
    private void buildTree()
    {
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
    }

    //--------------------------------------------------------------------------
    /**
     * Build a node of the tree.
     */
    private void buildNode(DoTreeNode parentNode) throws DataSourceException
    {
        // if node already has children built then exit
        if (parentNode.getChildrenBuilt())
            return;

        // mark as built, this stops recursive and repeated builds
        parentNode.setChildrenBuilt(true);

        // clear the node
        Util.argCheckNull(parentNode);
        _tree.clearNode(parentNode);

        // get parent data object
        DataObject doParent = parentNode.getDataObject();

        if (parentNode == _tree.getRoot())
        {
            // add base entries to tree
            // login settings
            _tree.addNode(parentNode, LoginSettings.getLoginSettings(), false);

            // templates
            _templates = _tree.addNode(parentNode, TEMPLATES.getText(), null,
                Util.resourceIcon(MdnAdminGuiManager.MDN_IMAGE_PATH + "Template.gif"));

            // groups
            _groups = _tree.addNode(parentNode, GROUPS.getText(), null,
                Util.resourceIcon(GuiConst.FW_IMAGE_PATH + "Group.gif"));
        }
        else if (parentNode == _templates)
        {
            // build templates
            Vector v = MenuAction.getGroupMenus(null);
            buildMenuActionsFromVector(v, parentNode);
        }
        else if (parentNode == _groups)
        {
            DataSource ds = DataManager.getSystemDS();
            Query q = new Query(Group.ENT_GROUP);
            RecordSet rs = ds.select(q);

            // build the groups
            _tree.buildFromRecordSet(rs, parentNode);
        }
        else if(doParent instanceof Group)
        {
            // build the menus for the group
            Vector v = MenuAction.getGroupMenus(((Group) doParent).getId());
            buildMenuActionsFromVector(v, parentNode);
        }
        else if(doParent instanceof Submenu || doParent instanceof QueryRecords)
        {
            // build submenus for this
            buildMenuActionsFromVector(((MenuAction)doParent).getChildren(),
                parentNode);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Tree selection changed.
     */
    public void valueChanged(TreeSelectionEvent ev)
    {
        // note, this is a work-around to solve a possible race condition
        // related to adding a child on an unexpaned parent and getLastChildNode
        // causing a buildNode

        // selection changed, do a pre-emptive build
        // get the expanded node
        DoTreeNode node = _tree.getSelectedNode();
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

        // selection changed update buttons
        updateButtons();
    }

    //--------------------------------------------------------------------------
    /**
     * Tree node expanded.
     */
    public void treeExpanded(TreeExpansionEvent ev)
    {
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
        // nothing to do
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
            if(ev.getSource().equals(_btnClose))               // close
                closePanel();
            else if(ev.getSource().equals(_btnAddMenu))        // add menu
                onAddMenu();
            else if(ev.getSource().equals(_btnAddText))        // add text
                onAddText();
            else if(ev.getSource().equals(_btnAddNewRecord))   // add NewRecord
                onAddNewRecord();
            else if(ev.getSource().equals(_btnAddQueryRecord)) // add QueryRecord
                onAddQueryRecord();
            else if(ev.getSource().equals(_btnDelete))         // delete
                onDelete();
            else if(ev.getSource().equals(_btnCopy))           // copy
                onCopy();
            else if(ev.getSource().equals(_btnPaste))          // paste
                onPaste();
            else if(ev.getSource().equals(_btnMoveUp))         // move up
                onMove(true);
            else if(ev.getSource().equals(_btnMoveDown))       // move down
                onMove(false);
            else if(ev.getSource().equals(_btnMsgServer))      // Message Servers
                onMsgServer();
            else if(ev.getSource().equals(_btnProperties))     // properties
                onProperties();

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
     * Add Menu button clicked.
     */
    private void onAddMenu()
    {
        DoTreeNode parent = _tree.getSelectedNode();

        // double check for valid parent
        assert canHaveMenuChildren(parent);

        // create a new TextAction
        Submenu submenu = new Submenu();

        // edit it in an OkCancel Panel
        OkCancelPanel panel = GuiManager.openOkCancelPanel(getFrameParent(),
            submenu, true, false);

        // if Ok clicked add and save it
        if (panel.isOk())
            addToParent(parent, submenu);
    }

    //--------------------------------------------------------------------------
    /**
     * Add Text clicked.
     */
    private void onAddText()
    {
        DoTreeNode parent = _tree.getSelectedNode();

        // double check for valid parent
        assert canHaveMenuChildren(parent);

        // create a new TextAction
        TextAction ta = new TextAction();

        // edit it in an OkCancel Panel
        OkCancelPanel panel = GuiManager.openOkCancelPanel(getFrameParent(),
            ta, true, false);

        // if Ok clicked add and save it
        if (panel.isOk())
            addToParent(parent, ta);
    }

    //--------------------------------------------------------------------------
    /**
     * Add Action button clicked.
     */
    private void onAddNewRecord()
    {
        DoTreeNode parent = _tree.getSelectedNode();

        // double check for valid parent
        assert canHaveMenuChildren(parent);

        // create a new TextAction
        NewRecord nra = new NewRecord();

        // edit it in an OkCancel Panel
        OkCancelPanel panel = GuiManager.openOkCancelPanel(getFrameParent(),
            nra, true, false);

        // if Ok clicked add and save it
        if (panel.isOk())
            addToParent(parent, nra);
    }

    //--------------------------------------------------------------------------
    /**
     * Add Action button clicked.
     */
    private void onAddQueryRecord()
    {
        DoTreeNode parent = _tree.getSelectedNode();

        // double check for valid parent
        assert canHaveMenuChildren(parent);

        // create a new TextAction
        QueryRecords qra = new QueryRecords();

        // edit it in an OkCancel Panel
        OkCancelPanel panel = GuiManager.openOkCancelPanel(getFrameParent(),
            qra, true, false);

        // if Ok clicked add and save it
        if (panel.isOk())
            addToParent(parent, qra);
    }


    //--------------------------------------------------------------------------
    /**
     * Message Server button clicked.
     */
    private void
    onMsgServer ()
    {
        DoTreeNode parent = _tree.getSelectedNode();

        // create a new MsgServerAction
        MsgServerAction msa = new MsgServerAction();

        // edit it in an OkCancel Panel
        OkCancelPanel panel = GuiManager.openOkCancelPanel(getFrameParent(),
            msa, true, false);

        // if Ok clicked add and save it
        if (panel.isOk())
            addToParent(parent, msa);
    }

    //--------------------------------------------------------------------------
    /**
     * Delete button clicked.
     */
    private void onDelete() throws DataSourceException
    {
        // get the selected node and object
        DataObject dobj = _tree.getSelectedDataObject();

        // if it is IN_DB delete it
        if(dobj != null && dobj.getState() == DataObject.IN_DB)
        {
            // extra check, only allow MenuActions to be deleted
            if (dobj instanceof MenuAction)
            {
                // if the MenuAction is a child of a parent menuAction then
                // delete it from the DB AND the parent
                DoTreeNode parentNode = (DoTreeNode) _tree.getSelectedNode().getParent();

                if (parentNode != null && parentNode.getDataObject() != null
                    && parentNode.getDataObject() instanceof MenuAction)
                {
                    // remove from parent
                    MenuAction parentMenuAction = (MenuAction) parentNode.getDataObject();
                    parentMenuAction.removeChild((MenuAction) dobj, false);
                }

                // delete it, the listeners ensures the tree is updated
                // there should be no objects in the tree which are not in the DB
                dobj.delete();
            }
            else
                assert false;
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Properties button clicked.
     */
    private void onProperties() throws DataSourceException
    {
        // get the selected DataObject and show it in a properties panel
        DataObject dobj = _tree.getSelectedDataObject();
        if(dobj != null)
            GuiManager.openOkCancelPanel(getFrameParent(), dobj, true);
    }

    //--------------------------------------------------------------------------
    /**
     * Copy the selected MenuAction to the "clipboard".
     */
    private void onCopy()
    {
        // get the dataObject
        DataObject dobj = _tree.getSelectedDataObject();

        // if it is a menuAction then place a deep clone copy in the clipboard
        if (dobj instanceof MenuAction)
        {
            _clipboard = ((MenuAction) dobj).cloneBranch();
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Paste the MenuAction in the clipboard into the current selection.
     */
    private void onPaste()
    {
        // must have clipboard entry
        assert _clipboard != null;

        DoTreeNode parent = _tree.getSelectedNode();

        // add clipboard entry and save it
        addToParent(parent, _clipboard);

        // clear the clipboard entry as is is now saaved and in use
        _clipboard = null;

        // ensure paset button is updated
        updateButtons();
    }

    //--------------------------------------------------------------------------
    /**
     * Move the selected MenuAction node up or down by altering its (and its
     * neighbour's) sequence number.
     */
    private void onMove(boolean bUp) throws DataSourceException
    {
        // abort if we don't have a MenuAction
        if (_tree.getSelectedDataObject() == null
            || !(_tree.getSelectedDataObject() instanceof MenuAction))
            return;

        // get the node, dobj and parent
        MenuAction currentMA   = (MenuAction) _tree.getSelectedDataObject();
        DoTreeNode currentNode = _tree.getSelectedNode();
        DoTreeNode parentNode  = (DoTreeNode) currentNode.getParent();
        MenuAction parentMA    = null;
        if (parentNode.getDataObject() != null
            && parentNode.getDataObject() instanceof MenuAction)
            parentMA = (MenuAction) parentNode.getDataObject();

        // get the neighbour
        DoTreeNode neighbourNode = (DoTreeNode)
            ((bUp) ? parentNode.getChildBefore(currentNode)
            : parentNode.getChildAfter(currentNode));

        // if neighbour is null then we are at the top/bottom and cannot move
        if (neighbourNode == null)
            return;

        MenuAction neighbourMA = (MenuAction) neighbourNode.getDataObject();

        // swap the sequence numbers of the DataObjects
        int tmpSeq = currentMA.getSequence();
        currentMA.setSequence(neighbourMA.getSequence());
        neighbourMA.setSequence(tmpSeq);

        // save them
        currentMA.save();
        neighbourMA.save();

        // if the parent is a MenuAction which contains these then resort
        // it to ensure its invernal vector remains consistent
        if (parentMA != null)
            parentMA.sortChildren();

        // swap node positions in tree
        DoTreeNode node1;
        DoTreeNode node2;

        // choose order of nodes
        if (bUp)
        {
            node1 = neighbourNode;
            node2 = currentNode;
        }
        else
        {
            node2 = neighbourNode;
            node1 = currentNode;
        }

        // use model to remove and insert
        DefaultTreeModel model = (DefaultTreeModel) _tree.getModel();
        model.removeNodeFromParent(node2);
        int index = model.getIndexOfChild(parentNode, node1);
        model.insertNodeInto(node2, parentNode, index);

        // finished, when moving up the selected node is removed, so ensure it
        // is reselected
        if (bUp)
            _tree.setSelectionPath(new TreePath(currentNode.getPath()));
    }

    //--------------------------------------------------------------------------
    /**
     * Update the state of the controls.
     */
    public void updateButtons()
    {
        // get state
        DoTreeNode selNode      = _tree.getSelectedNode();
        DataObject selDobj      = _tree.getSelectedDataObject();
        boolean    hasDobj      = selDobj != null;
        boolean    isMenuAction = hasDobj && selDobj instanceof MenuAction;
        boolean    isGroup      = hasDobj && selDobj instanceof Group;
        boolean    isTemplates  = selNode == _templates;
        boolean    isSubmenu    = hasDobj && selDobj instanceof Submenu;
        boolean    isQuery    = hasDobj && selDobj instanceof QueryRecords;

        // enable or disable by state
        _btnProperties.setEnabled(hasDobj);
        _btnDelete.setEnabled(isMenuAction);
        _btnMoveUp.setEnabled(isMenuAction);
        _btnMoveDown.setEnabled(isMenuAction);
        _btnCopy.setEnabled(isMenuAction);
        _btnPaste.setEnabled((isTemplates || isGroup || isSubmenu)
            && _clipboard != null);
        _btnAddNewRecord.setEnabled(isTemplates || isGroup || isSubmenu);
        _btnAddQueryRecord.setEnabled(isTemplates || isGroup || isSubmenu || isQuery);
        _btnAddText.setEnabled(isTemplates || isGroup || isSubmenu);
        _btnAddMenu.setEnabled(isTemplates || isGroup || isSubmenu);
        _btnMsgServer.setEnabled(isTemplates || isGroup || isSubmenu);
    }

    //--------------------------------------------------------------------------
    /**
     * Adds child to the parent tree node and data object, sets the sequence
     * number and saves the child to the DB.
     */
    private void addToParent(DoTreeNode parent, MenuAction child)
    {
        Util.argCheckNull(parent);
        Util.argCheckNull(child);

        // get the dobj of the parent
        DataObject parentDobj = parent.getDataObject();
        MenuAction maParent   = null;

        // get sequence id and set it in the child
        child.setSequence(getLastChildSequence(parent) + 1);

        if (parentDobj != null && parentDobj instanceof MenuAction)
        {
            // if parent is a MenuAction
            // set childs parent id and group using the parentDobj
            maParent = (MenuAction) parentDobj;
            child.setParentMenuId(maParent.getId());
            child.setGroupIdDeep(maParent.getGroupId());
        }
        else if (parentDobj != null && parentDobj instanceof Group)
        {
            // if parent is a group
            // set childs parent id and group using the parentDobj
            Group groupParent = (Group) parentDobj;
            child.setGroupIdDeep(groupParent.getId());
            child.setParentMenuId(null);
        }
        else if (parent == _templates)
        {
            // parent is templates
            child.setGroupIdDeep(null);
            child.setParentMenuId(null);
        }
        else
            assert false;

        // parent and group ids are now set
        // save child
        try
        {
            // save the child
            child.save();

            // if there is a parent menu then add child to it
            if (maParent != null)
                maParent.addChild(child);

            // place in tree and show
            DoTreeNode node = addMenuActionNode(parent, child);
            _tree.scrollPathToVisible(new TreePath(node.getPath()));
            // fixme, are there cases where the select is a problem?
            _tree.setSelectionPath(new TreePath(node.getPath()));
        }
        catch (Exception e)
        {
            GuiManager.showErrorDialog(this, ERR_ADD_CHILD.getText(), e);
            Log.error(ERR_ADD_CHILD.getText(), e);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * @return the last child node of the parent, null if no children.
     */
    private DoTreeNode getLastChildNode(DoTreeNode parent)
    {
        Util.argCheckNull(parent);
        try
        {
            // ensure the node is built so it can get its children
            // not that this can cause a race condition when adding children
            // to an unexpanded nodes, a work aound has been put in
            // valueChanged()
            buildNode(parent);

            // get the last child.
            return (DoTreeNode) parent.getLastChild();
        }
        catch (Exception e)
        {
        }

        return null;
    }

    //--------------------------------------------------------------------------
    /**
     * @return the last child DataObject of the parent, null if no children.
     */
    private DataObject getLastChildDobj(DoTreeNode parent)
    {
        DoTreeNode node = getLastChildNode(parent);

        if (node != null)
            return node.getDataObject();
        else
            return null;
    }

    //--------------------------------------------------------------------------
    /**
     * @return the sequence number of the parent's last child, -1 if no children.
     */
    private int getLastChildSequence(DoTreeNode parent)
    {
        DataObject dobj = getLastChildDobj(parent);

        if (dobj != null && dobj instanceof MenuAction)
            return ((MenuAction) dobj).getSequence();
        else
            return -1;
    }

    //--------------------------------------------------------------------------
    /**
     * @param parent, the parent node to test.
     * @return true if parent is allowed to have MenuAction children.
     */
    private boolean canHaveMenuChildren(DoTreeNode parent)
    {
        if (parent == null)
            return false;

        DataObject parentDobj   = parent.getDataObject();
        boolean    hasDobj      = parentDobj != null;
        boolean    isGroup      = hasDobj && parentDobj instanceof Group;
        boolean    isTemplates  = parent == _templates;
        boolean    isSubmenu    = hasDobj && parentDobj instanceof Submenu;
        boolean    isQuery    = hasDobj && parentDobj instanceof QueryRecords;

        return isTemplates || isGroup || isSubmenu || isQuery;
    }

    //--------------------------------------------------------------------------
    /**
     * Add a MenuAction subclass node to the tree, whether it is allowed
     * children is chosen based on the type of child.
     * @param parent, the parent node.
     * @param child, the MenuAction to add.
     * @return the created node that holds the MenuAction.
     */
    private DoTreeNode addMenuActionNode(DoTreeNode parent, MenuAction child)
    {
        Util.argCheckNull(parent);
        Util.argCheckNull(child);

        boolean allowChildren = child instanceof Submenu || child instanceof QueryRecords;
        return _tree.addNode(parent, child, allowChildren);
    }

    //--------------------------------------------------------------------------
    /**
     * Iterate ove a vector ading the MenuActions to the tree. Each is given the
     * correct expandability based on subclass.
     * @param v, the vector of MenuActions.
     * @parem parent, the node to addd the children to.
     */
    private void buildMenuActionsFromVector(Vector v, DoTreeNode parent)
    {
        // validate params
        Util.argCheckNull(v);
        Util.argCheckNull(parent);

        // iterate adding to tree
        MenuAction dobj;

        for (int i = 0; i < v.size(); i++)
        {
            // get the DataObject
            dobj = (MenuAction) v.elementAt(i);
            if(dobj != null)
            {
                // add to tree as child
                addMenuActionNode(parent, dobj);
            }
        }
    }


    //--------------------------------------------------------------------------
    // test

    private void onTest() throws Exception
    {
        // get the selected queryrecs
        QueryRecords childQr = (QueryRecords)_tree.getSelectedDataObject();

        // get the parent queryrecs
        DoTreeNode parentNode = (DoTreeNode)_tree.getSelectedNode().getParent();
        DataObject dobj = _tree.getDataObject(parentNode);
        if(dobj != null && dobj instanceof QueryRecords)
        {
            QueryRecords parentQr = (QueryRecords)dobj;

            // get the parent query dobj
            Object queryId = parentQr.getQueryId();
            QueryDobj parentQuery = new QueryDobj();
            parentQuery.setId(queryId);
            if(null != (parentQuery = (QueryDobj)parentQuery.loadPolymorphic()))
            {
                // execute the parent query
                DataViewDataSource dvds = MdnDataManager.getDataViewDS();
                RecordSet rs = dvds.select(parentQuery);
                if(rs != null && rs.next())
                {
                    // get the first rec
                    Record parentRec = (Record)rs.getCurrentObject();

                    // get the chld query
                    QueryDobj childQuery = new QueryDobj();
                    queryId = childQr.getQueryId();
                    childQuery.setId(queryId);

                    if(null != (childQuery = (QueryDobj)childQuery.loadPolymorphic()))
                    {
                        // execute the query with the parent rec
                        rs = dvds.select(childQuery, parentRec);
                        while(rs != null && rs.next())
                        {
                            System.out.println("*** " + rs.getCurrentObject());
                        }
                    }
                }
            }
        }
    }

    /**
     * Preferred size
     */
    public Dimension getPreferredSize()
    {
        return new Dimension(620, 516);
    }
}

//==============================================================================
// end of file PresentationBuilderPanel.java
//==============================================================================
