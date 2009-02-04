/*	$Header: /wapsolutions/cvsroot/mdn/fw/src/wsl/fw/msgserver/MaintainMsgServersPanel.java,v 1.3 2002/10/01 23:58:16 tecris Exp $
 *
 *
 */
package wsl.fw.msgserver;

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
import wsl.fw.msgserver.*;
import wsl.fw.security.*;

public class MaintainMsgServersPanel
	extends WslButtonPanel
    implements ActionListener, TreeExpansionListener, TreeSelectionListener
{
    // resources
    public static final ResId
		BUTTON_ADD_MSEX_SERVER		= new ResId ("MaintainMsgServersPanel.button.AddMsexServer"),
    	BUTTON_ADD_DOMINO_SERVER	= new ResId ("MaintainMsgServersPanel.button.AddDominoServer"),
    	BUTTON_ADD_IMAPSMTP_SERVER	= new ResId ("MaintainMsgServersPanel.button.AddImapSmtpServer"),
    	BUTTON_ADD_PROFILE			= new ResId ("MaintainMsgServersPanel.button.AddProfile"),
    	BUTTON_REMOVE				= new ResId ("MaintainMsgServersPanel.button.Remove"),
    	BUTTON_PROPERTIES			= new ResId ("MaintainMsgServersPanel.button.Properties"),
    	BUTTON_CLOSE				= new ResId ("MaintainMsgServersPanel.button.Close"),
    	TREE_NAME					= new ResId ("MaintainMsgServersPanel.tree"),
    	PANEL_TITLE					= new ResId ("MaintainMsgServersPanel.title"),
    	BUTTON_HELP					= new ResId ("OkPanel.button.Help"),
    	ERR_BUILD_TREE				= new ResId ("MaintainMsgServersPanel.error.buildTree"),
    	ERR_SAVE					= new ResId ("MaintainMsgServersPanel.error.save");

    public final static HelpId HID_MAINT_MSGSERVERS = new HelpId("mdn.admin.MaintainMsgServersPanel");

    // constants
    private static final int BTN_WIDTH = 132;

    // attributes
    private WslButton
		_btnAddMsexServer	= new WslButton (BUTTON_ADD_MSEX_SERVER.getText(), BTN_WIDTH, this),
    	_btnAddDominoServer	= new WslButton (BUTTON_ADD_DOMINO_SERVER.getText(), BTN_WIDTH, this),
    	_btnAddImapSmtpServer = new WslButton (BUTTON_ADD_IMAPSMTP_SERVER.getText(), BTN_WIDTH, this),
    	_btnAddProfile		= new WslButton (BUTTON_ADD_PROFILE.getText(), BTN_WIDTH, this),
    	_btnRemove			= new WslButton (BUTTON_REMOVE.getText(), BTN_WIDTH, this),
    	_btnProperties		= new WslButton (BUTTON_PROPERTIES.getText(), BTN_WIDTH, this),
    	_btnClose			= new WslButton (BUTTON_CLOSE.getText(), BTN_WIDTH, this),
    	_btnTest			= new WslButton ("Test", BTN_WIDTH, this);
    private DataObjectTree _tree;
    private boolean _isBuildingTree = false;

    //--------------------------------------------------------------------------
    /**
     * Blank constructor.
     */
    public MaintainMsgServersPanel()
    {
        // super
        super(WslButtonPanel.VERTICAL);

        // set title
        setPanelTitle(PANEL_TITLE.getText());

        // init controls
        initMaintainMsgServersPanelControls();

        // build the tree
        buildTree();

        // update buttons
        updateButtons();
    }

    //--------------------------------------------------------------------------
    /**
     * Initialise controls.
     */
    private void initMaintainMsgServersPanelControls()
    {
        // add buttons
        addButton (_btnAddMsexServer);
        addButton (_btnAddDominoServer);
        addButton (_btnAddImapSmtpServer);
        addButton (_btnAddProfile);
        addButton (_btnRemove);
        addButton (_btnProperties);
        _btnClose.setIcon(Util.resourceIcon(GuiConst.FW_IMAGE_PATH + "close.gif"));
        //addHelpButton(BUTTON_HELP.getText(), HID_MAINT_MSGSERVERS, BTN_WIDTH, false);
        addButton(_btnClose);
        //addCustomButton(_btnTest);

        // create the tree
        _tree = new DataObjectTree(TREE_NAME.getText());
        _tree.addTreeExpansionListener(this);
        _tree.addTreeSelectionListener(this);
        _tree.addListener(new DataListenerData(null, MessageServer.ENT_MSGSERVER, null));
        _tree.addListener(new DataListenerData(null, MessageServerProfile.ENT_MSGSVR_PROFILE, null));
        JScrollPane sp = new JScrollPane(_tree);
        sp.setPreferredSize(new Dimension(450, 450));

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
                // select all message servers
                DataSource ds = DataManager.getSystemDS();
                Query q = new Query(MessageServer.ENT_MSGSERVER);
                RecordSet rs = ds.select(q);

                // build the root node
                _tree.buildFromRecordSet(rs, parentNode, true);
            }

            // if it is a message server, build the profile
            else if(doParent instanceof MessageServer)
            {
                // select all message servers
                DataSource ds = DataManager.getSystemDS();
                Query q = new Query(MessageServerProfile.ENT_MSGSVR_PROFILE);
                q.addQueryEntity(User.ENT_USER);
                q.addQueryCriterium(new QueryCriterium(
                    MessageServerProfile.ENT_MSGSVR_PROFILE,
                    MessageServerProfile.FLD_MSG_SERVERID,
                    QueryCriterium.OP_EQUALS,
                    new Integer(((MessageServer)doParent).getId())));
                RecordSet rs = ds.select(q);

                // build the tree
                // add to tree and recurse to get sub-branches
                MessageServerProfile dobjProfile;
                User dobjUser;
                while(rs.next())
                {
                    // get the DataObject
                    dobjProfile = (MessageServerProfile)rs.getCurrentObject(MessageServerProfile.ENT_MSGSVR_PROFILE);
                    dobjUser = (User)rs.getCurrentObject(User.ENT_USER);

                    // set the user into the profile
                    dobjProfile.setUser(dobjUser);

                    // add to tree as child
                    _tree.addNode(parentNode, dobjProfile, false);
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
            else if (ev.getSource ().equals (_btnAddMsexServer))
                onAddMsexServer ();
            else if (ev.getSource ().equals (_btnAddDominoServer))
                onAddDominoServer ();
            else if (ev.getSource ().equals (_btnAddImapSmtpServer))
                onAddImapSmtpServer ();
            else if(ev.getSource().equals(_btnAddProfile))
                onAddProfile();
            else if(ev.getSource().equals(_btnRemove))
                onRemove();
            else if(ev.getSource().equals(_btnProperties))
                onProperties();
            else if(ev.getSource().equals(_btnTest))
                onTest();

            // update buttons
            updateButtons();
        }
        catch(Exception e)
        {
			e.printStackTrace ();
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
     * Add MS Exchange button clicked.
     */
    private void onAddMsexServer() throws DataSourceException
    {
        // create the msg server
        MsExchangeMsgServer ms = createMsExchangeMsgServer();

        // open the properties panel
        OkCancelPanel panel = GuiManager.openOkCancelPanel(
            this.getFrameParent(), ms, true, true);

        // if Ok clicked
        if(panel.isOk())
        {
            // update tree
            buildNode(_tree.getRoot());
        }
    }

    /**
     * Add Domino button clicked.
     */
    private void
	onAddDominoServer ()
		throws DataSourceException
    {
        // create the msg server
        DominoMsgServer ms = createDominoMsgServer();

        // open the properties panel
        OkCancelPanel panel = GuiManager.openOkCancelPanel(
            this.getFrameParent(), ms, true, true);

        // if Ok clicked
        if(panel.isOk())
        {
            // update tree
            buildNode(_tree.getRoot());
        }
    }

    /**
     * Add Imap+Smtp button clicked.
     */
    private void
	onAddImapSmtpServer ()
		throws DataSourceException
    {
        // create the msg server
        ImapSmtpMsgServer ms = createImapSmtpMsgServer ();

        // open the properties panel
        OkCancelPanel panel = GuiManager.openOkCancelPanel(
            this.getFrameParent(), ms, true, true);

        // if Ok clicked
        if (panel.isOk())
        {
            // update tree
            buildNode(_tree.getRoot());
        }
    }

    /**
     * Add Profile button clicked.
     */
    private void onAddProfile() throws DataSourceException
    {
        // create the msg server
        MessageServerProfile msp = new MessageServerProfile();

        // open the properties panel
        OkCancelPanel panel = GuiManager.openOkCancelPanel(
            this.getFrameParent(), msp, true, false);

        // if Ok clicked
        if(panel.isOk())
        {
            // set the server id and save
            MessageServer ms = (MessageServer)_tree.getSelectedDataObject();
            msp.setMsgServerId(new Integer(ms.getId()));
            msp.save();

            // update tree
            buildNode(_tree.getSelectedNode());
            _isBuildingTree = true;
            _tree.collapsePath(_tree.getSelectionPath());
            _tree.expandPath(_tree.getSelectionPath());
            _isBuildingTree = false;
        }
    }

    /**
     * Remove button clicked.
     */
    private void onRemove() throws DataSourceException
    {
        // get the selected object
        DataObject dobj = _tree.getSelectedDataObject();

        // if it is IN_DB delete it
        if(dobj != null && dobj.getState() == DataObject.IN_DB)
        {
            // if it is a datatransfer, straight delete
            if(dobj instanceof MessageServer ||
             		dobj instanceof MessageServerProfile)
			{
                dobj.delete();
        		_tree.removeSelectedNode ();
			}
        }
    }

    /**
     * Properties button clicked.
     */
    private void onProperties() throws DataSourceException
    {
        // get the selected datasource
        DataObject dobj = _tree.getSelectedDataObject();

        if(dobj != null)
            GuiManager.openOkCancelPanel(this.getFrameParent(), dobj, true);
    }

    /**
     * Update controls
     */
    public void updateButtons()
    {
        // flags
        DataObject selDobj = _tree.getSelectedDataObject();
        boolean hasDobj = selDobj != null;
        boolean isServer = hasDobj && selDobj instanceof MessageServer;
        boolean isDominoServer = hasDobj && selDobj instanceof DominoMsgServer;
        boolean isImapSmtpServer = hasDobj && selDobj instanceof ImapSmtpMsgServer;

        // enable
        _btnAddMsexServer.setEnabled (true);
        _btnAddDominoServer.setEnabled (true);
        _btnAddImapSmtpServer.setEnabled (true);
        _btnAddProfile.setEnabled(isServer);
        _btnRemove.setEnabled(hasDobj);
        _btnProperties.setEnabled(hasDobj);
		_btnTest.setEnabled(isDominoServer);
    }


    //--------------------------------------------------------------------------
    // override for new message server create

    /**
     * @return the MsExchangeMsgServer(or subclass) object
     */
    protected MsExchangeMsgServer createMsExchangeMsgServer()
    {
        return new MsExchangeMsgServer();
    }

    /**
     * @return the DominoMsgServer(or subclass) object
     */
    protected DominoMsgServer
	createDominoMsgServer ()
    {
        return new DominoMsgServer();
    }

    protected ImapSmtpMsgServer
	createImapSmtpMsgServer ()
    {
        return new ImapSmtpMsgServer();
    }

    //--------------------------------------------------------------------------
    // test

    private void onTest() throws Exception
    {
	    // get the server
        DominoMsgServer ms = (DominoMsgServer)_tree.getSelectedDataObject();

		// create impl
		DataSource impl = ms.createImpl();

		// do something

    }
}
