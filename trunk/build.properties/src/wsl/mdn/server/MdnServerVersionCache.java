package wsl.mdn.server;

// imports
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.datasource.*;
import wsl.fw.security.*;
import wsl.mdn.dataview.*;
import wsl.mdn.guiconfig.*;
import wsl.mdn.mdnim.IMConnection;
import wsl.mdn.mdnim.IMContact;
import wsl.mdn.mdnim.IMMessage;
import wsl.mdn.mdnmsgsvr.CustomField;
import wsl.mdn.mdnmsgsvr.MessagingSettingDetails;
import wsl.mdn.mdnmsgsvr.TemporaryBlockContacInfo;
import wsl.mdn.mdnmsgsvr.BlockContacts;
import wsl.mdn.mdnmsgsvr.MdnEmailSetting;
import wsl.mdn.mdnmsgsvr.MdnMessageSeparator;
import wsl.mdn.mdnmsgsvr.MdnSmsSetting;
import wsl.mdn.mdnmsgsvr.MdnSmpp;
import wsl.mdn.mdnmsgsvr.MessageLog;
import wsl.mdn.mdnmsgsvr.UserReply;
import wsl.mdn.mdnmsgsvr.UserCustomField;
import wsl.fw.msgserver.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:
 * @author
 * @version 1.0
 */

public class MdnServerVersionCache extends DataObjectVersionCache
{
    //--------------------------------------------------------------------------
    // construction

    /**
     * Blank ctor
     */
    public MdnServerVersionCache()
    {
        DataListenerData dld = new DataListenerData(this, DataSourceDobj.ENT_DATASOURCE, null);
        DataManager.addDataChangeListener(dld);
        dld = new DataListenerData(this, EntityDobj.ENT_ENTITY, null);
        DataManager.addDataChangeListener(dld);
        dld = new DataListenerData(this, FieldDobj.ENT_FIELD, null);
        DataManager.addDataChangeListener(dld);
        dld = new DataListenerData(this, JoinDobj.ENT_JOIN, null);
        DataManager.addDataChangeListener(dld);
        dld = new DataListenerData(this, DataView.ENT_DATAVIEW, null);
        DataManager.addDataChangeListener(dld);
        dld = new DataListenerData(this, DataViewField.ENT_DVFIELD, null);
        DataManager.addDataChangeListener(dld);
        dld = new DataListenerData(this, QueryDobj.ENT_QUERY, null);
        DataManager.addDataChangeListener(dld);
        dld = new DataListenerData(this, QueryCriteriaDobj.ENT_QUERY_CRITERIA, null);
        DataManager.addDataChangeListener(dld);
        dld = new DataListenerData(this, QueryCriteriaHistoryDobj.ENT_QUERY_CRITERIA_HISTORY, null);
        DataManager.addDataChangeListener(dld);
        dld = new DataListenerData(this, MenuAction.ENT_MENUACTION, null);
        DataManager.addDataChangeListener(dld);
        dld = new DataListenerData(this, FieldExclusion.ENT_FIELDEXCLUSION, null);
        DataManager.addDataChangeListener(dld);
        dld = new DataListenerData(this, GroupMembership.ENT_GROUPMEMBERSHIP, null);
        DataManager.addDataChangeListener(dld);
        dld = new DataListenerData(this, GroupDataView.ENT_GROUPDATAVIEW, null);
        DataManager.addDataChangeListener(dld);
        dld = new DataListenerData(this, GroupTablePermission.ENT_GROUP_ENTITY_PERMISSION, null);
        DataManager.addDataChangeListener(dld);        
        dld = new DataListenerData(this, User.ENT_USER, null);
        DataManager.addDataChangeListener(dld);
        dld = new DataListenerData(this, LoginSettings.ENT_LOGINSETTINGS, null);
        DataManager.addDataChangeListener(dld);
        dld = new DataListenerData(this, MessageServer.ENT_MSGSERVER, null);
        DataManager.addDataChangeListener(dld);
        dld = new DataListenerData(this, MessageServerProfile.ENT_MSGSVR_PROFILE, null);
        DataManager.addDataChangeListener(dld);
        dld = new DataListenerData(this, IMConnection.ENT_IM_CONNECTION, null);
        DataManager.addDataChangeListener(dld);
        dld = new DataListenerData(this, IMMessage.ENT_IM_MESSAGE, null);
        DataManager.addDataChangeListener(dld);
        dld = new DataListenerData(this, IMContact.ENT_IM_CONTACT, null);
        DataManager.addDataChangeListener(dld);
        dld = new DataListenerData(this, MdnEmailSetting.ENT_EMAIL, null);
        DataManager.addDataChangeListener(dld);
        dld = new DataListenerData(this, WebServiceDobj.ENT_WEB_SERVICE, null);
        DataManager.addDataChangeListener(dld);
        dld = new DataListenerData(this, MdnSmsSetting.ENT_SMS, null);
        DataManager.addDataChangeListener(dld);
        dld = new DataListenerData(this, MdnMessageSeparator.ENT_MSG_SEPARATOR, null);
        DataManager.addDataChangeListener(dld);
        dld = new DataListenerData(this, UserReply.ENT_USER_REPLY, null);
        DataManager.addDataChangeListener(dld);
        dld = new DataListenerData(this, ProjectDobj.ENT_PROJECT, null);
        DataManager.addDataChangeListener(dld);
        dld = new DataListenerData(this, WebServiceOperationDobj.ENT_WEB_SERVICE_OPERATION, null);
        DataManager.addDataChangeListener(dld);
        dld = new DataListenerData(this, CustomField.ENT_CUSTOM_FIELD, null);
        DataManager.addDataChangeListener(dld);
        dld = new DataListenerData(this, UserCustomField.ENT_USER_CUSTOM_FIELD, null);
        DataManager.addDataChangeListener(dld);
        dld = new DataListenerData(this, MessageLog.ENT_MESSAGE_LOG, null);
        DataManager.addDataChangeListener(dld);
        dld = new DataListenerData(this, LanguageDobj.ENT_LANGUAGE, null);
        DataManager.addDataChangeListener(dld);
        dld = new DataListenerData(this, MessagingSettingDetails.ENT_MSG_SETT_INFO, null);
        DataManager.addDataChangeListener(dld);        
        dld = new DataListenerData(this, TemporaryBlockContacInfo.ENT_TEMP_BLOCK_INFO, null);
        DataManager.addDataChangeListener(dld);                
        dld = new DataListenerData(this, BlockContacts.ENT_BLOCK_CONTACTS, null);
        DataManager.addDataChangeListener(dld);                
        dld = new DataListenerData(this, MdnSmpp.ENT_SMPP, null);
        DataManager.addDataChangeListener(dld);                        
        
    }

    //--------------------------------------------------------------------------
    // Data Change

    /**
     * DataObject has changed
     */
    public void onDataChanged(DataChangeNotification notification)
    {
        // validate
        Util.argCheckNull(notification);

        // switch on type
        if(notification.getEntityName().equals(DataView.ENT_DATAVIEW))
        {
            // get the dvid
            DataView dv = (DataView)notification.getDataObject();
            int dvid = dv.getId();

            // update the ds version
            updateVersion(notification.getDataObject(), DataView.ENT_DATAVIEW, new Integer(dvid));
        }
        else if(notification.getEntityName().equals(User.ENT_USER))
        {
            // get the lsid
            User user = (User)notification.getDataObject();
            Object uid = user.getId();

            // update the ds version
            updateVersion(notification.getDataObject(), User.ENT_USER, uid);
        }
        else if(notification.getEntityName().equals(LoginSettings.ENT_LOGINSETTINGS))
        {
            // get the lsid
            LoginSettings ls = (LoginSettings)notification.getDataObject();
            Object lsid = ls.getId();

            // update the ds version
            updateVersion(notification.getDataObject(), LoginSettings.ENT_LOGINSETTINGS, lsid);
        }
        else if(notification.getEntityName().equals(DataSourceDobj.ENT_DATASOURCE))
        {
            // get the ds id
            DataSourceDobj ds = (DataSourceDobj)notification.getDataObject();
            int dsid = ds.getId();

            // update the ds version
            updateVersion(notification.getDataObject(), DataSourceDobj.ENT_DATASOURCE, new Integer(dsid));
        }
        else if(notification.getEntityName().equals(EntityDobj.ENT_ENTITY))
        {
            // get the ds id
            EntityDobj e = (EntityDobj)notification.getDataObject();
            int dsid = e.getDataSourceId();

            // update the ds version
            updateVersion(notification.getDataObject(), DataSourceDobj.ENT_DATASOURCE, new Integer(dsid));
        }
        else if(notification.getEntityName().equals(FieldDobj.ENT_FIELD))
        {
            // get the ds id
            FieldDobj f = (FieldDobj)notification.getDataObject();
            int dsid = f.getDsId();

            // update the ds version
            updateVersion(notification.getDataObject(), DataSourceDobj.ENT_DATASOURCE, new Integer(dsid));
        }
        else if(notification.getEntityName().equals(JoinDobj.ENT_JOIN))
        {
            // get the ds id
            JoinDobj j = (JoinDobj)notification.getDataObject();
            int dsid = j.getDataSourceId();

            // update the ds version
            updateVersion(notification.getDataObject(), DataSourceDobj.ENT_DATASOURCE, new Integer(dsid));
        }
        else if(notification.getEntityName().equals(DataViewField.ENT_DVFIELD))
        {
            // get the dv id
            DataViewField dvf = (DataViewField)notification.getDataObject();
            int dvid = dvf.getDataViewId();

            // update the ds version
            updateVersion(notification.getDataObject(), DataView.ENT_DATAVIEW, new Integer(dvid));
        }
        else if(notification.getEntityName().equals(MenuAction.ENT_MENUACTION))
        {
            // get the group id
            MenuAction ma = (MenuAction)notification.getDataObject();
            Object gid = ma.getGroupId();

            // update the ds version
            updateVersion(notification.getDataObject(), MenuAction.ENT_MENUACTION, gid);
        }
        else if(notification.getEntityName().equals(FieldExclusion.ENT_FIELDEXCLUSION))
        {
            // get the group/dv id
            FieldExclusion fe = (FieldExclusion)notification.getDataObject();
            int gid = fe.getGroupId();

            // update the ds version
            updateVersion(notification.getDataObject(), FieldExclusion.ENT_FIELDEXCLUSION, new Integer(gid));
        }
        else if(notification.getEntityName().equals(GroupMembership.ENT_GROUPMEMBERSHIP))
        {
            // get the user id
            GroupMembership gm = (GroupMembership)notification.getDataObject();
            int uid = gm.getUserId();
            int gid = gm.getGroupId();

            // update the user version
            Log.debug("Group Membership: userId = " + uid);
            updateVersion(notification.getDataObject(), User.ENT_USER, new Integer(uid));
        }
        else if(notification.getEntityName().equals(GroupDataView.ENT_GROUPDATAVIEW))
        {
            // get the dv id
            GroupDataView gdv = (GroupDataView)notification.getDataObject();
            int dvid = gdv.getDataViewId();
            int gid = gdv.getGroupId();

            // update the dataview version
            updateVersion(notification.getDataObject(), DataView.ENT_DATAVIEW, new Integer(dvid));
        }
        else if(notification.getEntityName().equals(GroupTablePermission.ENT_GROUP_ENTITY_PERMISSION))
        {
            // get the dv id
        	GroupTablePermission gTblPerm = (GroupTablePermission)notification.getDataObject();
            int gTblPermId = gTblPerm.getId();

            // update the GroupTablePermission version
            updateVersion(notification.getDataObject(), GroupTablePermission.ENT_GROUP_ENTITY_PERMISSION, new Integer(gTblPermId));
        }        
        else if(notification.getEntityName().equals(MessageServer.ENT_MSGSERVER))
        {
            // get the msid
            MessageServer ms = (MessageServer)notification.getDataObject();
            Integer msid = new Integer(ms.getId());

            // update the ms version
            Log.debug("MessageServer: id = " + msid);
            updateVersion(notification.getDataObject(), MessageServer.ENT_MSGSERVER, msid);
        }
        else if(notification.getEntityName().equals(MessageServerProfile.ENT_MSGSVR_PROFILE))
        {
            // get the msid
            MessageServerProfile msp = (MessageServerProfile)notification.getDataObject();
            Integer msid = new Integer(msp.getMsgServerId());

            // update the ms version
            Log.debug("MessageServerProfile: id = " + msid);
            updateVersion(notification.getDataObject(), MessageServer.ENT_MSGSERVER, msid);
        }
        else if(notification.getEntityName().equals(IMConnection.ENT_IM_CONNECTION))
        {
            // get the imcid
            IMConnection imc = (IMConnection)notification.getDataObject();
            Integer imcid = new Integer(imc.getId());

            // update the ms version
            Log.debug("IMConnection: id = " + imcid);
            updateVersion(notification.getDataObject(), IMConnection.ENT_IM_CONNECTION, imcid);
        }

        else if(notification.getEntityName().equals(IMMessage.ENT_IM_MESSAGE))
        {
            // get the immid
        	IMMessage imm = (IMMessage)notification.getDataObject();
            Integer immid = new Integer(imm.getId());

            // update the ms version
            Log.debug("IMMessage: id = " + immid);
            updateVersion(notification.getDataObject(), IMMessage.ENT_IM_MESSAGE, immid);
        }
        else if(notification.getEntityName().equals(IMContact.ENT_IM_CONTACT))
        {
            // get the imcid
        	IMContact imc = (IMContact)notification.getDataObject();
            Integer imcid = new Integer(imc.getId());

            // update the ms version
            Log.debug("IMContact: id = " + imcid);
            updateVersion(notification.getDataObject(), IMContact.ENT_IM_CONTACT, imcid);
        }
        else if(notification.getEntityName().equals(MdnEmailSetting.ENT_EMAIL))
        {
            // get the email
        	MdnEmailSetting emailSett = (MdnEmailSetting)notification.getDataObject();
            Integer emailSettId = new Integer(emailSett.getId());

            // update the emailsett version
            Log.debug("MdnEmailSetting: id = " + emailSettId);
            updateVersion(notification.getDataObject(), MdnEmailSetting.ENT_EMAIL, emailSettId);
        }
        else if(notification.getEntityName().equals(WebServiceDobj.ENT_WEB_SERVICE))
        {
            // get the ds id
        	WebServiceDobj e = (WebServiceDobj)notification.getDataObject();
            int dsid = e.getId();

            // update the ds version
            updateVersion(notification.getDataObject(), WebServiceDobj.ENT_WEB_SERVICE, new Integer(dsid));
        }
        else if(notification.getEntityName().equals(MdnSmsSetting.ENT_SMS))
        {
            // get the sms
        	MdnSmsSetting smsSett = (MdnSmsSetting)notification.getDataObject();
            Integer smsSettId = new Integer(smsSett.getId());

            // update the MdnSmsSetting version
            Log.debug("MdnSmsSetting: id = " + smsSettId);
            updateVersion(notification.getDataObject(), MdnSmsSetting.ENT_SMS, smsSettId);
        }
        else if(notification.getEntityName().equals(MdnMessageSeparator.ENT_MSG_SEPARATOR))
        {
            // get the msg separator
        	MdnMessageSeparator msgSep = (MdnMessageSeparator)notification.getDataObject();
            Integer msgSepId = new Integer(msgSep.getId());

            // update the msgSep version
            Log.debug("MdnMsgSep: id = " + msgSepId);
            updateVersion(notification.getDataObject(), MdnMessageSeparator.ENT_MSG_SEPARATOR, msgSepId);
        }
        else if(notification.getEntityName().equals(UserReply.ENT_USER_REPLY))
        {
            // get the msg separator
        	UserReply userReply = (UserReply)notification.getDataObject();
            Integer userReplyId = new Integer(userReply.getId());

            // update the msgSep version
            Log.debug("UserReply: id = " + userReplyId);
            updateVersion(notification.getDataObject(), UserReply.ENT_USER_REPLY, userReplyId);
        }
        else if(notification.getEntityName().equals(ProjectDobj.ENT_PROJECT))
        {
            // get the msg separator
        	ProjectDobj project = (ProjectDobj)notification.getDataObject();
            Integer projectId = new Integer(project.getId());

            // update the msgSep version
            Log.debug("projectId: id = " + projectId);
            updateVersion(notification.getDataObject(), ProjectDobj.ENT_PROJECT, projectId);
        }
        else if(notification.getEntityName().equals(WebServiceOperationDobj.ENT_WEB_SERVICE_OPERATION))
        {
            // get the msg separator
        	WebServiceOperationDobj webServiceOperationDobj = (WebServiceOperationDobj)notification.getDataObject();
            Integer id = new Integer(webServiceOperationDobj.getId());

            // update the msgSep version
            Log.debug("WebServiceOperationDobj id = " + id);
            updateVersion(notification.getDataObject(), WebServiceOperationDobj.ENT_WEB_SERVICE_OPERATION, id);
        }
        else if(notification.getEntityName().equals(CustomField.ENT_CUSTOM_FIELD))
        {
            // get the msg separator
        	CustomField customField = (CustomField)notification.getDataObject();
            Integer id = new Integer(customField.getId());

            // update the msgSep version
            Log.debug("CustomField id = " + id);
            updateVersion(notification.getDataObject(), CustomField.ENT_CUSTOM_FIELD, id);
        }
        else if(notification.getEntityName().equals(UserCustomField.ENT_USER_CUSTOM_FIELD))
        {
            // get the msg separator
        	UserCustomField customField = (UserCustomField)notification.getDataObject();
            Integer id = new Integer(customField.getId());

            // update the msgSep version
            Log.debug("UserCustomField id = " + id);
            updateVersion(notification.getDataObject(), UserCustomField.ENT_USER_CUSTOM_FIELD, id);
        }
        else if(notification.getEntityName().equals(MessageLog.ENT_MESSAGE_LOG))
        {
            // get the msg separator
        	MessageLog msgLog = (MessageLog)notification.getDataObject();
            Integer id = new Integer(msgLog.getId());

            // update the msgSep version
            Log.debug("msgLog id = " + id);
            updateVersion(notification.getDataObject(), MessageLog.ENT_MESSAGE_LOG, id);
        }
        if(notification.getEntityName().equals(LanguageDobj.ENT_LANGUAGE))
        {
            // get the dvid
        	LanguageDobj lang = (LanguageDobj)notification.getDataObject();
            int dvid = lang.getId();

            // update the ds version
            updateVersion(notification.getDataObject(), LanguageDobj.ENT_LANGUAGE, new Integer(dvid));
        }
        else if(notification.getEntityName().equals(MessagingSettingDetails.ENT_MSG_SETT_INFO))
        {
        	MessagingSettingDetails guestObj = (MessagingSettingDetails)notification.getDataObject();
            Integer id = new Integer(guestObj.getId());

            Log.debug("guestObj id = " + id);
            updateVersion(notification.getDataObject(), MessagingSettingDetails.ENT_MSG_SETT_INFO, id);
        }        
        else if(notification.getEntityName().equals(TemporaryBlockContacInfo.ENT_TEMP_BLOCK_INFO))
        {
        	TemporaryBlockContacInfo guestObj = (TemporaryBlockContacInfo)notification.getDataObject();
            Integer id = new Integer(guestObj.getId());

            updateVersion(notification.getDataObject(), TemporaryBlockContacInfo.ENT_TEMP_BLOCK_INFO, id);
        }                
        else if(notification.getEntityName().equals(BlockContacts.ENT_BLOCK_CONTACTS))
        {
        	BlockContacts blockContacts = (BlockContacts)notification.getDataObject();
            Integer id = new Integer(blockContacts.getId());

            Log.debug("Block Contacts id = " + id);
            updateVersion(notification.getDataObject(), BlockContacts.ENT_BLOCK_CONTACTS, id);
        }                
        else if(notification.getEntityName().equals(MdnSmpp.ENT_SMPP))
        {
        	MdnSmpp smsGateway = (MdnSmpp)notification.getDataObject();
            Integer id = new Integer(smsGateway.getId());

            Log.debug("Mdn SMPP id = " + id);
            updateVersion(notification.getDataObject(), MdnSmpp.ENT_SMPP, id);
        }                        
        else
            super.onDataChanged(notification);
    }
}