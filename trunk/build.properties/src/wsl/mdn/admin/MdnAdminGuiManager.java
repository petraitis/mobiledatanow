/**	$Header: /wapsolutions/cvsroot/mdn/mdn/src/wsl/mdn/admin/MdnAdminGuiManager.java,v 1.1.1.1 2002/06/11 23:35:35 jonc Exp $
 *
 * MdnAdminGuiManager subclass for the Mdn project
 *
 */
package wsl.mdn.admin;

// imports
import java.awt.Window;
import javax.swing.JDialog;
import wsl.fw.util.Log;
import wsl.fw.util.Util;
import wsl.fw.gui.GuiManager;
import wsl.fw.gui.GuiMapNode;
import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.Field;
import wsl.fw.msgserver.DominoPropPanel;
import wsl.fw.msgserver.MsgServerPropPanel;
import wsl.fw.msgserver.ImapSmtpPropPanel;
import wsl.mdn.admin.*;
import wsl.mdn.dataview.*;
import wsl.mdn.guiconfig.*;
import wsl.mdn.mdnmsgsvr.*;
import wsl.fw.resource.ResId;

public class MdnAdminGuiManager extends GuiManager
{
    // constants
    public final static String MDN_IMAGE_PATH = "wsl/mdn/resource/images/";

    // resources
    public static final ResId NODE_JDBC_DATASOURCE   = new ResId("MdnAdminGuiManager.node.JdbcDataSource");
    public static final ResId NODE_DATAVIEW          = new ResId("MdnAdminGuiManager.node.DataView");
    public static final ResId NODE_DATAVIEWFIELD     = new ResId("MdnAdminGuiManager.node.DataViewField");
    public static final ResId NODE_ENTITY            = new ResId("MdnAdminGuiManager.node.Entity");
    public static final ResId NODE_FIELD             = new ResId("MdnAdminGuiManager.node.Field");
    public static final ResId NODE_JOIN              = new ResId("MdnAdminGuiManager.node.Join");
    public static final ResId NODE_QUERY             = new ResId("MdnAdminGuiManager.node.Query");
    public static final ResId NODE_LOGINSETTINGS     = new ResId("MdnAdminGuiManager.node.LoginSettings");
    public static final ResId NODE_SUBMENU           = new ResId("MdnAdminGuiManager.node.Submenu");
    public static final ResId NODE_TEXTACTION        = new ResId("MdnAdminGuiManager.node.TextAction");
    public static final ResId NODE_SHOWMAINMENU      = new ResId("MdnAdminGuiManager.node.ShowMainMenu");
    public static final ResId NODE_NEWRECORD         = new ResId("MdnAdminGuiManager.node.NewRecord");
    public static final ResId NODE_QUERYRECORDS      = new ResId("MdnAdminGuiManager.node.QueryRecords");
    public static final ResId NODE_GROUPDATAVIEW     = new ResId("MdnAdminGuiManager.node.GroupDataView");
    public static final ResId NODE_SCHEDULING        = new ResId("MdnAdminGuiManager.node.Scheduling");
    public static final ResId NODE_JDBCDRIVER        = new ResId("MdnAdminGuiManager.node.JdbcDriver");
    public static final ResId NODE_MSGSERVER_ACTION  = new ResId("MdnAdminGuiManager.node.MsgServerAction");


    //--------------------------------------------------------------------------
    /**
     * Blank constructor. Builds the gui map
     */
    public MdnAdminGuiManager()
    {
        // add the gui nodes
        // JdbcDataSourceDobj
        addGuiMapNode(new GuiMapNode(JdbcDataSourceDobj.class, JdbcPropPanel.class, NODE_JDBC_DATASOURCE.getText(),
            Util.resourceIcon(MDN_IMAGE_PATH + "db.gif")));
        // DataView
        addGuiMapNode(new GuiMapNode(DataView.class, DataViewPropPanel.class, NODE_DATAVIEW.getText(),
            Util.resourceIcon(MDN_IMAGE_PATH + "table.gif")));
        // DirectQueryDataView
        addGuiMapNode(new GuiMapNode(DirectQueryDataView.class, DirectSQLPanel.class, NODE_DATAVIEW.getText(),
            Util.resourceIcon(MDN_IMAGE_PATH + "table.gif")));
        // DataViewField
        addGuiMapNode(new GuiMapNode(DataViewField.class, DataViewFieldPropPanel.class, NODE_DATAVIEWFIELD.getText(),
            Util.resourceIcon(MDN_IMAGE_PATH + "field.gif")));
        // EntityDobj
        addGuiMapNode(new GuiMapNode(EntityDobj.class, EntityPropPanel.class, NODE_ENTITY.getText(),
            Util.resourceIcon(MDN_IMAGE_PATH + "table.gif")));
        // FieldDobj
        addGuiMapNode(new GuiMapNode(FieldDobj.class, FieldPropPanel.class, NODE_FIELD.getText(),
            Util.resourceIcon(MDN_IMAGE_PATH + "field.gif")));
        // JoinDobj
        addGuiMapNode(new GuiMapNode(JoinDobj.class, null, NODE_JOIN.getText(),
            Util.resourceIcon(MDN_IMAGE_PATH + "lock.gif")));
        // QueryDobj
        addGuiMapNode(new GuiMapNode(QueryDobj.class, null, NODE_QUERY.getText(),
            Util.resourceIcon(MDN_IMAGE_PATH + "qmark.gif")));
        // DirectQueryDobj
        addGuiMapNode(new GuiMapNode(DirectQueryDobj.class, null, NODE_QUERY.getText(),
            Util.resourceIcon(MDN_IMAGE_PATH + "qmark.gif")));
        // LoginSettings
        addGuiMapNode(new GuiMapNode(LoginSettings.class, LoginSettingsPropertiesPanel.class,
            NODE_LOGINSETTINGS.getText(),
            Util.resourceIcon(MDN_IMAGE_PATH + "properties.gif")));
        // Submenu
        addGuiMapNode(new GuiMapNode(Submenu.class, TextOrSubmenuPropertiesPanel.class,
            NODE_SUBMENU.getText(),
            Util.resourceIcon(MDN_IMAGE_PATH + "menu.gif")));
        // TextAction
        addGuiMapNode(new GuiMapNode(TextAction.class, TextOrSubmenuPropertiesPanel.class,
            NODE_TEXTACTION.getText(),
            Util.resourceIcon(MDN_IMAGE_PATH + "text.gif")));
       // NewRecord
        addGuiMapNode(new GuiMapNode(NewRecord.class, NewRecordPropPanel.class,
            NODE_NEWRECORD.getText(),
            Util.resourceIcon(MDN_IMAGE_PATH + "NewRecord.gif")));
        // QueryRecords
        addGuiMapNode(new GuiMapNode(QueryRecords.class, ActionPropertiesPanel.class,
            NODE_QUERYRECORDS.getText(),
            Util.resourceIcon(MDN_IMAGE_PATH + "qmark.gif")));
        // ShowMainMenu
        addGuiMapNode(new GuiMapNode(ShowMainMenu.class, ActionPropertiesPanel.class,
            NODE_SHOWMAINMENU.getText(),
            Util.resourceIcon(MDN_IMAGE_PATH + "menu.gif")));
        // MdnMsExchangeMsgServer
        addGuiMapNode(
			new GuiMapNode(
				MdnMsExchangeMsgServer.class,
				MsgServerPropPanel.class,
            	GuiManager.NODE_MSG_SERVER.getText(),
            	Util.resourceIcon(MDN_IMAGE_PATH + "msgserver.gif")));
        // MdnDominoMsgServer
        //addGuiMapNode(
			//new GuiMapNode(
				//MdnDominoMsgServer.class,
				//DominoPropPanel.class,
            	//GuiManager.NODE_DOMINO_MSG_SERVER.getText(),
            	//Util.resourceIcon(MDN_IMAGE_PATH + "table.gif")));
        // MdnImapSmtpMsgServer
        //addGuiMapNode(
			//new GuiMapNode(
				//MdnImapSmtpMsgServer.class,
				//ImapSmtpPropPanel.class,
            	//GuiManager.NODE_IMAPSMTP_MSGSERVER.getText(),
            	//Util.resourceIcon(MDN_IMAGE_PATH + "msgserver.gif")));
        // MsgServerAction
        addGuiMapNode(new GuiMapNode(MsgServerAction.class, MsgServerActionPropPanel.class,
            NODE_MSGSERVER_ACTION.getText(),
            Util.resourceIcon(MDN_IMAGE_PATH + "msgserver.gif")));
        // DataTransfer
        addGuiMapNode(new GuiMapNode(DataTransfer.class, null,
            NODE_TEXTACTION.getText(),
            Util.resourceIcon(MDN_IMAGE_PATH + "datatransfer.gif")));
        // TransferEntity
        addGuiMapNode(new GuiMapNode(TransferEntity.class, null,
            NODE_TEXTACTION.getText(),
            Util.resourceIcon(MDN_IMAGE_PATH + "table.gif")));
        // Scheduling
        addGuiMapNode(new GuiMapNode(Scheduling.class, null,
            NODE_TEXTACTION.getText(),
            Util.resourceIcon(MDN_IMAGE_PATH + "menu.gif")));
        // Users and Groups
        addGuiMapNode(new GuiMapNode(GroupDataView.class, GroupDataViewPropPanel.class,
            NODE_GROUPDATAVIEW.getText(),
            Util.resourceIcon(MDN_IMAGE_PATH + "hammer.gif")));
        // Scheduling
        addGuiMapNode(new GuiMapNode(Scheduling.class, SchedulingPropPanel.class,
            NODE_SCHEDULING.getText(),
            Util.resourceIcon(MDN_IMAGE_PATH + "hammer.gif")));
        // JDBC Driver
        addGuiMapNode(new GuiMapNode(JdbcDriver.class, JdbcDriverPropPanel.class,
            NODE_JDBCDRIVER.getText(),
            Util.resourceIcon(MDN_IMAGE_PATH + "dbdriver.gif")));
    }


    /**
     * Get the GuiMapNode for the specified data object.
     * @param the the DataObject.
     * @return the associated GuiMapNode, null if no association.
     */
    public GuiMapNode getGuiMapNode(DataObject dobj)
    {
        // validate
        Util.argCheckNull(dobj);

        // is it a mirrored datasource
        if(dobj instanceof DataSourceDobj)
        {
            if (((DataSourceDobj)dobj).isMirrored())
                return new GuiMapNode(JdbcDataSourceDobj.class, JdbcPropPanel.class,
                    NODE_JDBC_DATASOURCE.getText(),
                    Util.resourceIcon(MDN_IMAGE_PATH + "mirrordb.gif"));
        }

        // is it a DataViewField
        else if(dobj instanceof DataViewField)
        {
            // is it a naming field
            if (((DataViewField)dobj).hasFlag(Field.FF_NAMING))
                return new GuiMapNode(DataViewField.class, DataViewFieldPropPanel.class,
                    NODE_FIELD.getText(),
                    Util.resourceIcon(MDN_IMAGE_PATH + "naming.gif"));
        }

        // is it a FieldDobj
        else if(dobj instanceof FieldDobj)
        {
            // is it a unique key field
            if (((FieldDobj)dobj).hasFlag(Field.FF_UNIQUE_KEY))
                return new GuiMapNode(FieldDobj.class, FieldPropPanel.class,
                    NODE_FIELD.getText(),
                    Util.resourceIcon(MDN_IMAGE_PATH + "key.gif"));
        }

        return super.getGuiMapNode(dobj);
    }
}

//==============================================================================
// end of file MdnAdminGuiManager.java
//==============================================================================
