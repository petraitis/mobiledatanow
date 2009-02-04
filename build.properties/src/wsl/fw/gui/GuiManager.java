/**	$Header: /wapsolutions/cvsroot/mdn/fw/src/wsl/fw/gui/GuiManager.java,v 1.2 2004/01/06 01:45:33 tecris Exp $
 *
 * Singleton Manager of Gui elements in an application. Contains static methods
 * to open various framework windows.
 *
 */
package wsl.fw.gui;

// imports
import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.DataManager;
import wsl.fw.datasource.Entity;
import wsl.fw.datasource.DataSource;
import wsl.fw.datasource.Query;
import wsl.fw.datasource.RecordSet;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.security.gui.UserGroupMaintenancePanel;
import java.util.Hashtable;
import java.util.Vector;
import java.awt.Window;
import java.awt.Frame;
import java.awt.Dialog;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.Component;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import wsl.fw.resource.ResId;

public class GuiManager
{
    // resources
    public static final ResId
		NODE_USER				= new ResId ("GuiManager.node.User"),
    	NODE_GROUP				= new ResId ("GuiManager.node.Group"),
    	NODE_FEATURE			= new ResId ("GuiManager.node.Feature"),
    	NODE_PRIVILEGE			= new ResId ("GuiManager.node.Privilege"),
    	NODE_GROUP_MEMBERSHIP	= new ResId ("GuiManager.node.GroupMembership"),
    	NODE_PRESENTATION_ITEM	= new ResId ("GuiManager.node.PresentationItem"),
    	NODE_MSG_SERVER			= new ResId ("GuiManager.node.MsexMsgServer"),
    	NODE_DOMINO_MSG_SERVER	= new ResId ("GuiManager.node.DominoMsgServer"),
    	NODE_IMAPSMTP_MSGSERVER	= new ResId ("GuiManager.node.ImapSmtpMsgServer"),
    	NODE_MSGSERVER_PROFILE	= new ResId ("GuiManager.node.MsgServerProfile"),
    	TITLE_SEARCH			= new ResId ("GuiManager.title.Search"),
    	TITLE_MAINTAIN			= new ResId ("GuiManager.title.Maintain"),
    	TITLE_EDIT				= new ResId ("GuiManager.title.Edit"),
    	TITLE_VIEW				= new ResId ("GuiManager.title.View"),
    	ASSERT_SUB_CLASS		= new ResId ("GuiManager.assert.Subclass"),
    	TITLE_MAINTAIN_USERS_GROUPS = new ResId ("GuiManager.title.MaintainUsersGroups"),
    	ERR_INVALID_PARENT		= new ResId ("GuiManager.err.InvalidParent"),
    	ERR_UNABLE_CREATE		= new ResId ("GuiManager.err.UnableCreate"),
    	MSG_ERROR				= new ResId ("GuiManager.msg.Error"),
    	MSG_CONFIRM				= new ResId ("GuiManager.msg.Confirm"),
    	ERR_UNHANDLED			= new ResId ("GuiManager.error.unhandledException");

    // GuiManager singleton object
    private static GuiManager s_gm = null;

    // Map of GuiMapNodes. Maps a class to a number of elements. See GuiMapNode
    private Hashtable _guiMap = new Hashtable();


    // static initialiser
    {
        try
        {
            // look and feel
            javax.swing.UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e)
        {
        }
    }

    /**
     * Set the GuiManager singleton
     * @param gm
     */
    public static void setGuiManager(GuiManager gm)
    {
        s_gm = gm;
    }

    /**
     * @return GuiManager the GuiManager singleton
     */
    public static GuiManager getGuiManager()
    {
        return s_gm;
    }

    /**
     * Default constructor.
     */
    public GuiManager()
    {
        // add framework GuiMapNodes here
        addGuiMapNode(new GuiMapNode(wsl.fw.security.User.class,
            wsl.fw.security.gui.UserPropertiesPanel.class,
            NODE_USER.getText(), Util.resourceIcon(GuiConst.FW_IMAGE_PATH + "User.gif")));
        addGuiMapNode(new GuiMapNode(wsl.fw.security.Group.class,
            wsl.fw.security.gui.GroupPropertiesPanel.class,
            NODE_GROUP.getText(), Util.resourceIcon(GuiConst.FW_IMAGE_PATH + "Group.gif")));
        addGuiMapNode(new GuiMapNode(wsl.fw.security.Feature.class,
            wsl.fw.security.gui.FeaturePropertiesPanel.class,
            NODE_FEATURE.getText(), Util.resourceIcon(GuiConst.FW_IMAGE_PATH + "Feature.gif")));
        addGuiMapNode(new GuiMapNode(wsl.fw.security.Privilege.class, null,
            NODE_PRIVILEGE.getText(), Util.resourceIcon(GuiConst.FW_IMAGE_PATH + "Privilege.gif")));
        addGuiMapNode(new GuiMapNode(wsl.fw.security.GroupMembership.class, null,
            NODE_GROUP_MEMBERSHIP.getText(), Util.resourceIcon(GuiConst.FW_IMAGE_PATH + "GroupMembership.gif")));
        addGuiMapNode(new GuiMapNode(wsl.fw.presentation.PresentationItem.class, null,
            NODE_PRESENTATION_ITEM.getText(), Util.resourceIcon(GuiConst.FW_IMAGE_PATH + "PresentationItem.gif")));
        addGuiMapNode (
			new GuiMapNode(
				wsl.fw.msgserver.MsExchangeMsgServer.class,
            	wsl.fw.msgserver.MsgServerPropPanel.class,
            	NODE_MSG_SERVER.getText(),
				Util.resourceIcon (GuiConst.FW_IMAGE_PATH + "msgserver.gif")));
        //addGuiMapNode (
			//new GuiMapNode(
				//wsl.fw.msgserver.DominoMsgServer.class,
            	//wsl.fw.msgserver.DominoPropPanel.class,
            	//NODE_DOMINO_MSG_SERVER.getText(),
				//Util.resourceIcon (GuiConst.FW_IMAGE_PATH + "Privilege.gif")));
        //addGuiMapNode (
			//new GuiMapNode (
				//wsl.fw.msgserver.ImapSmtpMsgServer.class,
            	//wsl.fw.msgserver.ImapSmtpPropPanel.class,
            	//NODE_IMAPSMTP_MSGSERVER.getText(),
				//Util.resourceIcon (GuiConst.FW_IMAGE_PATH + "msgserver.gif")));
        addGuiMapNode(new GuiMapNode(wsl.fw.msgserver.MessageServerProfile.class,
            wsl.fw.msgserver.MsgServerProfilePropPanel.class,
            NODE_MSGSERVER_PROFILE.getText(), Util.resourceIcon(GuiConst.FW_IMAGE_PATH + "msgserverprofile.gif")));
    }

    /**
     * Add a GuiMapNode to the gui map
     * @param node a node to add to the gui map
     */
    protected void addGuiMapNode(GuiMapNode node)
    {
        // add the node to the table mapped on the DataObject class
        _guiMap.put(node._dataObjectClass, node);
    }

    /**
     * Get the GuiMapNode for the specified data object class.
     * @param the Class of the DataObject.
     * @return the associated GuiMapNode, null if no association.
     */
    public GuiMapNode getGuiMapNode(Class doc)
    {
        return (GuiMapNode)_guiMap.get(doc);
    }

    /**
     * Get the GuiMapNode for the specified data object.
     * @param the the DataObject.
     * @return the associated GuiMapNode, null if no association.
     */
    public GuiMapNode getGuiMapNode(DataObject dobj)
    {
        return (dobj == null)? null: (GuiMapNode)_guiMap.get(dobj.getClass());
    }

    /**
     * Return the properties class mapped to the DataObject class
     * @param doc DataObject class
     * @return Class the PropertiesPanel class
     */
    public static Class getPropPanelFromDataObject(Class doc)
    {
        // get the node
        GuiMapNode node = (GuiMapNode) getGuiManager()._guiMap.get(doc);

        // return the PropertiesPanel class
        return (node == null)? null: node._propPanelClass;
    }

    /**
     * Return the readable name mapped to the DataObject class
     * @param doc DataObject class
     * @return String readable name
     */
    public static String getTitleFromDataObject(Class doc)
    {
        // get the node
        GuiMapNode node = (GuiMapNode) getGuiManager()._guiMap.get(doc);

        // return the PropertiesPanel class
        return (node == null)? null: node._title;
    }

    /**
     * @return String a title for the Select Panel
     */
    private static String getSelectPanelTitle(Class c)
    {
        String dobjName = getTitleFromDataObject(c);
        //String title = "Search for ";
        String title = TITLE_SEARCH.getText() + " ";
        if(dobjName.length() > 0)
        {
            String vowels = "aeiouAEIOU";
            String englishAlphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
            if(vowels.indexOf(dobjName.charAt(0)) >= 0)
                title += "an " + dobjName;
            else
                if(englishAlphabet.indexOf(dobjName.charAt(0)) >= 0)
                    title += "a " + dobjName;
        }
        return title;
    }


    /**
     * @param parent
     * @param entityName
     * @param autoSearch
     * @return DataObject
     * @exception
     * @roseuid 3990C3E10082
     */
    public static DataObject selectDataObject(Window parent, String entityName, boolean doAutoSearch)
    {
        // create the SelectPanel
        SelectPanel sp = new SelectPanel(entityName, doAutoSearch);

        // create the framing dialog
        Entity ent = DataManager.getEntity(entityName);
        if(ent != null)
        {
            try
            {
                Class c = ent.getDefaultClass();
                if(c != null)
                {
                    String title = getSelectPanelTitle(c);
                    JDialog dlg = getFramingDialog(parent, title, true, sp);

                    // centre the window and show it
                    centerWindow(dlg);
                    dlg.show();

                    // return
                    return sp.getSelectedDataObject();
                }
            }
            catch(Exception e)
            {
                Log.error("GuiManager.selectDataObject", e);
            }
        }
        return null;
    }

    //--------------------------------------------------------------------------
    /**
     * Select a data object from the supplied vector. All searching is done from
     * the data objects in the vector rather than from the database.
     * @param parent, parent window.
     * @param title, title for the select window.
     * @param dobjs, the vector of data objects.
     */
    public static DataObject selectDataObject(Window parent, String title,
        Vector dobjs)
    {
        // create the SelectPanel
        SelectPanel sp = new SelectPanel(dobjs);

        // create the framing dialog
        JDialog dlg = getFramingDialog(parent, title, true, sp);

        // centre the window and show it
        centerWindow(dlg);
        dlg.show();

        // return
        return sp.getSelectedDataObject();
    }

    //--------------------------------------------------------------------------
    /**
     * @param parent
     * @param c
     * @param autoSearch
     * @return DataObject
     * @exception
     * @roseuid 3990C4A3009F
     */
    public static DataObject selectDataObject(Window parent, Class c, boolean doAutoSearch)
    {
        // create the SelectPanel
        SelectPanel sp = new SelectPanel(c, doAutoSearch);

        // create the framing dialog
        String title = getSelectPanelTitle(c);
        JDialog dlg = getFramingDialog(parent, title, true, sp);

        // centre the window and show it
        centerWindow(dlg);
        dlg.show();

        // return
        return sp.getSelectedDataObject();
    }

    /**
     * Create and open a maintenance panel for the param DataObject
     * @param parent The parent Window
     * @param dobj a DataObject to set into the maintenance panel
     * @return MaintenancePanel the created panel
     * @exception
     * @roseuid 3990C7F10327
     */
    public static MaintenancePanel openMaintenancePanel(Window parent, DataObject dobj, boolean doModal)
    {
        // validate params
        Util.argCheckNull(dobj);

        // create the properties panel
        PropertiesPanel pp = createPropertiesPanel(dobj.getClass());
        Util.argCheckNull(pp);

        // set the DataObject into the properties panel
        pp.setDataObject(dobj);

        // create the maintenance panel
        MaintenancePanel mp = new MaintenancePanel(pp);

        // create the framing dialog
        String title = getTitleFromDataObject(dobj.getClass());
        //JDialog dlg = getFramingDialog(parent, "Maintain " + title, doModal, mp);
        JDialog dlg = getFramingDialog(parent, TITLE_MAINTAIN.getText() + " " + title, doModal, mp);

        // call post create on the properties panel
        pp.postCreate();

        // centre the window and show it
        centerWindow(dlg);
        dlg.show();

        // return
        return mp;
    }

    /**
     * Create and open an OkCancel panel for the param DataObject
     * @param parent The parent Window
     * @param dobj a DataObject to set into the OkCancel panel
     * @param doModal opens modal panel if true
     * @return OkCancelPanel the created panel
     * @exception
     * @roseuid 3990C7F10327
     */
    public static OkCancelPanel openOkCancelPanel(Window parent, DataObject dobj, boolean doModal)
    {
        // delegate
        return openOkCancelPanel(parent, dobj, doModal, true);
    }

    /**
     * Create and open an OkCancel panel for the param DataObject
     * @param parent The parent Window
     * @param dobj a DataObject to set into the OkCancel panel
     * @param doModal opens modal panel if true
     * @param doSave saves DataObject onOk() if true
     * @return OkCancelPanel the created panel
     * @exception
     * @roseuid 3990C7F10327
     */
    public static OkCancelPanel openOkCancelPanel(Window parent, DataObject dobj,
        boolean doModal, boolean doSave)
    {
        // validate params
        Util.argCheckNull(dobj);

        // create the properties panel
        PropertiesPanel pp = createPropertiesPanel(dobj.getClass());
        Util.argCheckNull(pp);

        // set the data object and get the title
        pp.setDataObject(dobj);
        String title = getTitleFromDataObject(dobj.getClass());

        // delegate
        return openOkCancelPanel(parent, pp, title, doModal, doSave);
    }

    /**
     * Create and open an OkCancel panel for the param PropertiesPanel
     * @param parent The parent Window
     * @param pp the PropertiesPanel
     * @param title the title for the panel
     * @param doModal true to open the panel modally
     * @param doSave true to save the data object when the panel is closed
     * @return OkCancelPanel the created panel
     * @exception
     */
    public static OkCancelPanel openOkCancelPanel(Window parent, PropertiesPanel pp, String title, boolean doModal, boolean doSave)
    {
        // validate params
        Util.argCheckNull(pp);

        // create the maintenance panel
        OkCancelPanel op = new OkCancelPanel(pp);
        op.setDoSave(doSave);

        // create the framing dialog
        JDialog dlg = getFramingDialog(parent, TITLE_EDIT.getText() + " " + title, doModal, op);

        // call post create on the properties panel
        pp.postCreate();

        // centre the window and show it
        centerWindow(dlg);
        dlg.show();

        // return
        return op;
    }

    /**
     * Create and open an Ok panel for the param DataObject
     * @param parent The parent Window
     * @param dobj a DataObject to set into the OkCancel panel
     * @return OkCancelPanel the created panel
     * @exception
     * @roseuid 3990C7F10327
     */
    public static OkPanel openOkPanel(Window parent, DataObject dobj, boolean doModal, boolean bSave)
    {
        // validate params
        Util.argCheckNull(dobj);

        // create the properties panel
        PropertiesPanel pp = createPropertiesPanel(dobj.getClass());
        Util.argCheckNull(pp);

        // set the data object and get the title
        pp.setDataObject(dobj);
        String title = getTitleFromDataObject(dobj.getClass());

        // delegate
        return openOkPanel(parent, pp, title, doModal, bSave);
    }

    /**
     * Create and open an Ok panel for the param PropertiesPanel
     * @param parent The parent Window
     * @param pp the PropertiesPanel
     * @param title the title for the panel
     * @param doModal true to open the panel modally
     * @param doSave true to save the data object when the panel is closed
     * @return OkPanel the created panel
     * @exception
     */
    public static OkPanel openOkPanel(Window parent, PropertiesPanel pp, String title, boolean doModal, boolean doSave)
    {
        // validate params
        Util.argCheckNull(pp);

        // create the maintenance panel
        OkPanel op = new OkPanel(pp);
        op.setDoSave(doSave);

        // create the framing dialog
        //String fullTitle = (doSave ? "Edit " : "View ") + title;
        String fullTitle = (doSave ? TITLE_EDIT.getText() + " " : TITLE_VIEW.getText() + " ") + title;
        JDialog dlg = getFramingDialog(parent, fullTitle, doModal, op);

        // call post create on the properties panel
        pp.postCreate();

        // centre the window and show it
        centerWindow(dlg);
        dlg.show();

        // return
        return op;
    }

    /**
     * Create and open a list maintenance panel for the param entity name
     * @param parent The parent Window
     * @param editClass The class of DataObjects to edit
     * @param doModal If true the dialog is shown modally
     * @return ListMaintenancePanel the created panel
     * @exception
     */
    public static ListMaintenancePanel openListMaintenancePanel(Window parent, Class editClass, boolean doModal)
    {
        // delegate
        return openListMaintenancePanel(parent, editClass, null, doModal);
    }

    /**
     * Create and open a list maintenance panel for the param entity name
     * @param parent The parent Window
     * @param editClass The class of DataObjects to edit
     * @param doModal If true the dialog is shown modally
     * @return ListMaintenancePanel the created panel
     * @exception
     */
    public static ListMaintenancePanel openListMaintenancePanel(Window parent,
        Class editClass, Query q, boolean doModal)
    {
        // create the ListMaintenancePanel
        ListMaintenancePanel mp = new ListMaintenancePanel(editClass, q);

        // create the framing dialog
        String title = TITLE_MAINTAIN.getText() + " " + getTitleFromDataObject(editClass);
        mp.setPanelTitle(title);

        // open and return
        return (ListMaintenancePanel)openWslPanel(parent, mp, doModal);
    }

    /**
     * Create and open a tree maintenance panel to edit the DataObject specified
     * by editClass.
     * @param parent, The parent Window.
     * @param editClass, The class of DataObjects to edit.
     * @param parentField, the name of the DataObject field that holds the
     *   reference to the parent DataObject key. Must be a field in the same
     *   DataObject, i.e. the DataObject represents a hierarchy.
     * @param doModal If true the dialog is shown modally.
     * @return TreeMaintenancePanel the created panel.
     */

    public static TreeMaintenancePanel openTreeMaintenancePanel(Window parent,
        Class editClass, String parentField, boolean doModal)
    {
        assert DataObject.class.isAssignableFrom(editClass):
            "GuiManager.openTreeMaintenancePanel: editClass "
                + ASSERT_SUB_CLASS.getText() + " DataObject";

        // create the TreeMaintenancePanel
        TreeMaintenancePanel mp = new TreeMaintenancePanel(editClass, parentField);

        // create the title
        String title = TITLE_MAINTAIN.getText() + " " + getTitleFromDataObject(editClass);
        mp.setPanelTitle(title);

        // open in a wsl dialog panel
        openWslPanel(parent, mp, doModal);
        return mp;
    }

    /**
     * Open the User/Group maintenance panel.
     * @param parent, The parent Window.
     * @param doModal If true the dialog is shown modally.
     * @return the created UserGroupMaintenancePanel.
     */
    public static UserGroupMaintenancePanel openUserGroupMaintenancePanel(
        Window parent, boolean doModal)
    {
        // create the TreeMaintenancePanel
        UserGroupMaintenancePanel mp = new UserGroupMaintenancePanel();

        // set the title
        mp.setPanelTitle(TITLE_MAINTAIN_USERS_GROUPS.getText());

        // open in a wsl dialog panel
        openWslPanel(parent, mp, doModal);
        return mp;
    }

    /**
     * Open a WslPanel. create a dialog frame and centre the window
     * @param parent the parent Window of the panel
     * @param panel the WslPanel to open
     * @param doModal true if panel is opened modally
     * @return WslPanel the panel
     */
    public static WslPanel openWslPanel(Window parent, WslPanel panel, boolean doModal)
    {
        // create the framing dialog
        JDialog dlg = getFramingDialog(parent, panel.getPanelTitle(), doModal, panel);

        // centre the window and show it
        centerWindow(dlg);
        dlg.show();

        // return
        return panel;
    }

    /**
     * Creates a framing dialog, and sets in the param panel
     * @param parent Window parent
     * @param title
     * @param doModal true if dialog is to be modal
     * @param panel WslPanel to set into the framing dialog
     * @return The framing dialog (a WslDialog)
     */
    public static WslDialog getFramingDialog(Window parent, String title, boolean doModal, WslPanel panel)
    {
        // create the dialog
        WslDialog dlg = null;
        if(parent == null)
            dlg = new WslDialog(panel, (Frame)null, title, doModal);
        else if(parent instanceof Frame)
            dlg = new WslDialog(panel, (Frame)parent, title, doModal);
        else if(parent instanceof Dialog)
            dlg = new WslDialog(panel, (Dialog)parent, title, doModal);
        else
            throw new RuntimeException(ERR_INVALID_PARENT.getText());

        // return
        return dlg;
    }

    /**
     * Create a PropertiesPanel for the param DataObject class
     * @param c the class of the DataObject to create the PropertiesPanel for
     * @return PropertiesPanel the created panel
     */
    public static PropertiesPanel createPropertiesPanel(Class c)
    {
        // get the class from the gui map
        GuiMapNode node = (GuiMapNode) getGuiManager()._guiMap.get(c);

        // create the properties panel
        PropertiesPanel pp = null;
        if(node != null && node._propPanelClass != null)
        {
            try
            {
               pp = (PropertiesPanel)node._propPanelClass.newInstance();
            }
            catch(Exception e)
            {
                Log.error("GuiManager.createPropertiesPanel", e);
                //throw new RuntimeException("Unable to create class " + node._propPanelClass.getName());
                throw new RuntimeException(ERR_UNABLE_CREATE.getText() + " "
                    + node._propPanelClass.getName());
            }
        }

        // return
        return pp;
    }

    /**
     * Center the window
     * @param w The window to center
     */
    public static void centerWindow(Window w)
    {
        // validate params
        if(w == null)
            return;

        // get the screen and window dimensions
        Dimension sd = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension wd = w.getSize();

        // calculate the top, left point for centered window
        int x = (int)((sd.getWidth() - wd.getWidth()) / 2.0);
        int y = (int)((sd.getHeight() - wd.getHeight()) / 2.0);

        // set the location into the window
        w.setLocation(x, y);
    }

    //--------------------------------------------------------------------------
    /**
     * Display a modal error dialog (title is Error) with some message text and
     * an optional exception.
     * @param parent, the parent component.
     * @param message, the message to display, may be a string, an object that
     *   supports toString or an array of objects that support toString..
     * @param ex, an exception to display, may be null.
     */
    public static void showErrorDialog(Component parent, Object message,
        Exception ex)
    {
        Object msgObj[] = { message, ex };

        JOptionPane.showMessageDialog(parent, msgObj,
            MSG_ERROR.getText(), JOptionPane.ERROR_MESSAGE);
    }

    //--------------------------------------------------------------------------
    /**
     * Display a modal message dialog with an totle and some message text.
     * @param parent, the parent component.
     * @param title, the dialog title.
     * @param message, the message to display, may be a string, an object that
     *   supports toString or an array of objects that support toString..
     */
    public static void showMessageDialog(Component parent, String title,
        Object message)
    {
        JOptionPane.showMessageDialog(parent, message,
            title, JOptionPane.INFORMATION_MESSAGE);
    }

    //--------------------------------------------------------------------------
    /**
     * Display a modal confirm dialog (title is Confirm) with some message text
     * and yes/no buttons.
     * @param parent, the parent component.
     * @param message, the message to display, may be a string, an object that
     *   supports toString or an array of objects that support toString.
     * @return true if the user selected "yes".
     */
    public static boolean showConfirmDialog(Component parent, Object message)
    {
        return (JOptionPane.showConfirmDialog(parent, message, MSG_CONFIRM.getText(),
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
            == JOptionPane.YES_OPTION);
    }

    //--------------------------------------------------------------------------
    /**
     * Open a WslProgressPanel. create a dialog frame and centre the window
     * @param parent the parent Window of the panel
     * @param client the WslProgressClient to show in progress panel
     * @return WslPanel the panel
     */
    public static WslProgressPanel runProgressPanel(Window parent, WslProgressClient client)
    {
        // create a progress panel
        WslProgressPanel panel = new WslProgressPanel(client);

        // create the framing dialog
        JDialog dlg = getFramingDialog(parent, client.getProgressTitle(), false, panel);

        // centre
        centerWindow(dlg);

        // run the panel
        WslProgressPanel.runProgressPanel(panel);

        // return
        return panel;
    }
}

//==============================================================================
// end of file GuiManager.java
//==============================================================================
