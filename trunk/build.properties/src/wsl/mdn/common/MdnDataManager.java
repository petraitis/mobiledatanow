//==============================================================================
// MdnDataManager.java
// Copyright (c) 2001 WAP Solutions Ltd.
//==============================================================================

package wsl.mdn.common;

// imports

import java.util.Vector;
import wsl.fw.util.Config;
import wsl.fw.datasource.DataManager;
import wsl.fw.datasource.Entity;
import wsl.fw.datasource.Field;
import wsl.fw.datasource.Join;
import wsl.fw.datasource.KeyGeneratorData;
import wsl.fw.datasource.DataSource;
import wsl.fw.datasource.DataSource;
import wsl.fw.datasource.JdbcDataSourceParam;

import wsl.mdn.dataview.DataSourceDobj;
import wsl.mdn.dataview.DataView;
import wsl.mdn.dataview.DataViewField;
import wsl.mdn.dataview.EntityDobj;
import wsl.mdn.dataview.FieldDobj;
import wsl.mdn.dataview.GroupTablePermission;
import wsl.mdn.dataview.JoinDobj;
import wsl.mdn.dataview.LanguageDobj;
import wsl.mdn.dataview.ProjectDobj;
import wsl.mdn.dataview.QueryCriteriaDobj;
import wsl.mdn.dataview.QueryCriteriaHistoryDobj;
import wsl.mdn.dataview.QueryDobj;
import wsl.mdn.dataview.GroupDataView;
import wsl.mdn.dataview.FieldExclusion;
import wsl.mdn.dataview.DataTransfer;
import wsl.mdn.dataview.TransferEntity;
import wsl.mdn.dataview.DataViewDataSource;
import wsl.mdn.dataview.Scheduling;
import wsl.mdn.dataview.WebServiceDobj;
import wsl.mdn.dataview.WebServiceOperationDobj;
import wsl.mdn.guiconfig.MenuAction;
import wsl.mdn.guiconfig.LoginSettings;
import wsl.mdn.mdnim.IMConnection;
import wsl.mdn.mdnim.IMContact;
import wsl.mdn.mdnim.IMMessage;
//import wsl.mdn.mdnmsgsvr.AddressBook;
import wsl.mdn.mdnmsgsvr.CustomField;
import wsl.mdn.mdnmsgsvr.MdnSmpp;
import wsl.mdn.mdnmsgsvr.MessagingSettingDetails;
import wsl.mdn.mdnmsgsvr.TemporaryBlockContacInfo;
import wsl.mdn.mdnmsgsvr.BlockContacts;
import wsl.mdn.mdnmsgsvr.MdnEmailSetting;
import wsl.mdn.mdnmsgsvr.MdnMessageSeparator;
import wsl.mdn.mdnmsgsvr.MdnSmsSetting;
import wsl.mdn.mdnmsgsvr.MessageLog;
import wsl.mdn.mdnmsgsvr.UserCustomField;
import wsl.mdn.mdnmsgsvr.UserReply;
import wsl.mdn.dataview.JdbcDriver;

//------------------------------------------------------------------------------
/**
 * Data manager for the mdn.
 * Defines the domain entities for the mdn
 */
public class MdnDataManager extends DataManager
{
    //--------------------------------------------------------------------------
    // version tag

    private final static String _ident = "$Date: 2002/06/11 23:35:35 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/common/MdnDataManager.java $ ";


    //--------------------------------------------------------------------------
    // attributes

    private static DataViewDataSource _dvds = null;

    //--------------------------------------------------------------------------
    // overrides

    /**
     * Return the name of the DataSource mapped to this entity name
     * @param the name of the entity to be mapped to a datasource
     * @return String the name of the datasource to be used for this entity
     */
    protected String getEntityDSName(String entityName)
    {
        // delegate to super
        return super.getEntityDSName(entityName);
    }

    /**
     * Creates a DataSource based on a DataSource name
     * @param dsName DataSource name
     * @return DataSource
     */
    protected DataSource iCreateDataSource(String dsName)
    {
        // delegate to super
        return super.iCreateDataSource(dsName);
    }

    /**
     * Entity factory. Creates and returns entity definitions based on param name
     * @param entityName the name of the entity to create
     * @return Entity the created Entity
     */
    protected Entity iCreateEntity(String entityName)
    {
        // switch on name
        Entity ent = null;

        if (entityName.equals(DataView.ENT_DATAVIEW))
            ent = DataView.createEntity();
        else if (entityName.equals(DataViewField.ENT_DVFIELD))
            ent = DataViewField.createEntity();
        else if (entityName.equals(DataSourceDobj.ENT_DATASOURCE))
            ent = DataSourceDobj.createEntity();
        else if (entityName.equals(EntityDobj.ENT_ENTITY))
            ent = EntityDobj.createEntity();
        else if (entityName.equals(FieldDobj.ENT_FIELD))
            ent = FieldDobj.createEntity();
        else if (entityName.equals(JoinDobj.ENT_JOIN))
            ent = JoinDobj.createEntity();
        else if (entityName.equals(QueryDobj.ENT_QUERY))
            ent = QueryDobj.createEntity();
        else if (entityName.equals(QueryCriteriaDobj.ENT_QUERY_CRITERIA))
            ent = QueryCriteriaDobj.createEntity();
        else if (entityName.equals(QueryCriteriaHistoryDobj.ENT_QUERY_CRITERIA_HISTORY))
            ent = QueryCriteriaHistoryDobj.createEntity();
        else if (entityName.equals(GroupDataView.ENT_GROUPDATAVIEW))
            ent = GroupDataView.createEntity();
        else if (entityName.equals(GroupTablePermission.ENT_GROUP_ENTITY_PERMISSION))
        	ent = GroupTablePermission.createEntity();        
        else if (entityName.equals(FieldExclusion.ENT_FIELDEXCLUSION))
            ent = FieldExclusion.createEntity();
        else if (entityName.equals(MenuAction.ENT_MENUACTION))
            ent = MenuAction.createEntity();
        else if (entityName.equals(LoginSettings.ENT_LOGINSETTINGS))
            ent = LoginSettings.createEntity();
        else if (entityName.equals(DataTransfer.ENT_DATATRANSFER))
            ent = DataTransfer.createEntity();
        else if (entityName.equals(TransferEntity.ENT_TRANSFERENTITY))
            ent = TransferEntity.createEntity();
        else if (entityName.equals(Scheduling.ENT_SCHEDULING))
            ent = Scheduling.createEntity();
        else if (entityName.equals(JdbcDriver.ENT_JDBCDRIVER))
            ent = JdbcDriver.createEntity();
        else if (entityName.equals(IMConnection.ENT_IM_CONNECTION))
            ent = IMConnection.createEntity();
        else if (entityName.equals(IMMessage.ENT_IM_MESSAGE))
            ent = IMMessage.createEntity();
        else if (entityName.equals(IMContact.ENT_IM_CONTACT))
            ent = IMContact.createEntity();
        else if (entityName.equals(MdnEmailSetting.ENT_EMAIL))
        	ent = MdnEmailSetting.createEntity();
//        else if (entityName.equals(AddressBook.ENT_ADDRESS_BOOK))
//        	ent = AddressBook.createEntity();
        else if (entityName.equals(WebServiceDobj.ENT_WEB_SERVICE))
            ent = WebServiceDobj.createEntity(); 
        else if (entityName.equals(MdnSmsSetting.ENT_SMS))
        	ent = MdnSmsSetting.createEntity();
        else if (entityName.equals(MdnMessageSeparator.ENT_MSG_SEPARATOR))
        	ent = MdnMessageSeparator.createEntity();
        else if (entityName.equals(UserReply.ENT_USER_REPLY))
        	ent = UserReply.createEntity();
        else if (entityName.equals(ProjectDobj.ENT_PROJECT))
            ent = ProjectDobj.createEntity();       
        else if (entityName.equals(WebServiceOperationDobj.ENT_WEB_SERVICE_OPERATION))
            ent = WebServiceOperationDobj.createEntity();
        else if (entityName.equals(CustomField.ENT_CUSTOM_FIELD))
        	ent = CustomField.createEntity();
        else if (entityName.equals(UserCustomField.ENT_USER_CUSTOM_FIELD))
        	ent = UserCustomField.createEntity();
        else if (entityName.equals(MessageLog.ENT_MESSAGE_LOG))
        	ent = MessageLog.createEntity();
        else if (entityName.equals(LanguageDobj.ENT_LANGUAGE))
        	ent = LanguageDobj.createEntity();
        else if (entityName.equals(MessagingSettingDetails.ENT_MSG_SETT_INFO))
    		ent = MessagingSettingDetails.createEntity();        
        else if (entityName.equals(TemporaryBlockContacInfo.ENT_TEMP_BLOCK_INFO))
        	ent = TemporaryBlockContacInfo.createEntity();                
        else if (entityName.equals(BlockContacts.ENT_BLOCK_CONTACTS))
    		ent = BlockContacts.createEntity();                
        else if (entityName.equals(MdnSmpp.ENT_SMPP))
		ent = MdnSmpp.createEntity();                        
        
        // if entity not found call superclass
        if (ent == null)
            ent = super.iCreateEntity(entityName);

        // return the entity, or null if not found
        return ent;
    }


    //--------------------------------------------------------------------------
    // DataViewDataSource singleton

    /**
     * @return DataViewDataSource the singleton DataViewDataSource
     */
    public static DataViewDataSource getDataViewDS()
    {
        // if null, create
        if(_dvds == null)
            _dvds = new DataViewDataSource();

        // return
        return _dvds;
    }
}

//==============================================================================
// end of file MdnDataManager.java
//==============================================================================
