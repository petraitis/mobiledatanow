/**
 * 
 */
package com.framedobjects.dashwell.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import wsl.fw.datasource.RecordSet;
import wsl.fw.exception.MdnException;
import wsl.fw.security.Feature;
import wsl.fw.security.Group;
import wsl.fw.security.GroupMembership;
import wsl.fw.security.User;
import wsl.fw.security.UserWrapper;
import wsl.mdn.dataview.DataSourceDobj;
import wsl.mdn.dataview.DataView;
import wsl.mdn.dataview.DataViewField;
import wsl.mdn.dataview.EntityDobj;
import wsl.mdn.dataview.FieldDobj;
import wsl.mdn.dataview.FieldExclusion;
import wsl.mdn.dataview.GroupDataView;
import wsl.mdn.dataview.GroupTablePermission;
import wsl.mdn.dataview.JdbcDriver;
import wsl.mdn.dataview.JoinDobj;
import wsl.mdn.dataview.LanguageDobj;
import wsl.mdn.dataview.ProjectDobj;
import wsl.mdn.dataview.QueryCriteriaDobj;
import wsl.mdn.dataview.QueryDobj;
import wsl.mdn.dataview.ResultWrapper;
import wsl.mdn.dataview.WebServiceDobj;
import wsl.mdn.dataview.WebServiceOperationDobj;
import wsl.mdn.mdnim.IMConnection;
import wsl.mdn.mdnim.IMContact;
import wsl.mdn.mdnim.IMMessage;
//import wsl.mdn.mdnmsgsvr.AddressBook;
import wsl.mdn.mdnmsgsvr.BlockContacts;
import wsl.mdn.mdnmsgsvr.CustomField;
import wsl.mdn.mdnmsgsvr.MdnSmpp;
import wsl.mdn.mdnmsgsvr.MessagingSettingDetails;
import wsl.mdn.mdnmsgsvr.MdnEmailSetting;
import wsl.mdn.mdnmsgsvr.MdnMessageSeparator;
import wsl.mdn.mdnmsgsvr.MdnSmsSetting;
import wsl.mdn.mdnmsgsvr.MessageLog;
import wsl.mdn.mdnmsgsvr.TemporaryBlockContacInfo;
import wsl.mdn.mdnmsgsvr.UserCustomField;
import wsl.mdn.mdnmsgsvr.UserReply;

import com.framedobjects.dashwell.biz.ConnectionWizard;
import com.framedobjects.dashwell.biz.RecycleBinItem;
import com.framedobjects.dashwell.db.meta.MetaDbConnection;
import com.framedobjects.dashwell.db.meta.MetaField;
import com.framedobjects.dashwell.db.meta.MetaTable;

/**
 * Interface defining all methods required to interact with the datastore.
 * 
 * @author Jens Richnow
 *
 */
public interface IDataAgent {
	
	//public int createNewDbConnection(ConnectionWizard wizard);
	public UserWrapper getLoginUser(String username) throws MdnException;
	public int getNumberOfUsers();
	//public int isLicenseValid ();
	public int getAvailablePublicMessages();
	public List<LanguageDobj> getAllLanguages() throws MdnException;
	public LanguageDobj getLanguageById(int id) throws MdnException;
	public LanguageDobj getLanguageByName(String languageName) throws MdnException;
	public LanguageDobj getDefaultLanguage() throws MdnException;
	public List<JdbcDriver> getAllJdbcDrivers() throws MdnException;
	public JdbcDriver getJdbcDriverByName(String driverName) throws MdnException;
	public JdbcDriver getJdbcDriverById(int driverId) throws MdnException;
	public JdbcDriver getJdbcDriverByDriver(String driver) throws MdnException;
	public String handleTestDbConnection(DbConnection dbConn);
	public ResultWrapper createNewDbConnection(int projectId, DbConnection dbConn);
	public List<DataSourceDobj> getAllDbConnections(int projectId) throws MdnException;
	public DataSourceDobj getDbConnectionByName(int projectId, String dbConnName) throws MdnException;
	public DataSourceDobj getDbConnectionByID(int projectId, int connectionID) throws MdnException;
	public MetaDbConnection getDbConnectionByFullUrl(String url, String schema);
	public int recycleConnection(int projectId, int connectionID);
	public int clearConnection(int projectId, int connectionID);
	public int deleteConnection(int projectId, int connectionID);
	public int getExistingNumberOfUsers() throws MdnException;
	
	public List<EntityDobj> getAllMetaTables(int dbConnectionID, boolean includeFields) throws MdnException;
	public List<DataView> getAllMetaViews(int dbConnectionID, boolean includeFields) throws MdnException;
	public EntityDobj getMetaTableByID(int tableID, boolean includeFields) throws MdnException;
	public List<JoinDobj> getJoins(int connectionID) throws MdnException;
	public DataView getMetaViewByID(int viewID, boolean includeFields)throws MdnException;
	public DataView getMetaViewByName(String viewName, int connID)throws MdnException;
	public DataViewField getViewField(int fieldID) throws MdnException;
	public EntityDobj getMetaTableByName(String tableName, int connID, boolean includeFields) throws MdnException;
	public int editMetaTable(EntityDobj table);
	public List<QueryDobj> getQueriesByID(int id) throws MdnException;
	public QueryDobj getQueryByID(int queryID) throws MdnException;
	public List<QueryDobj> getQueriesByType(String type) throws MdnException;
	public List<QueryDobj> getAllQueries(int projectId) throws MdnException;	
	public List<QueryDobj> getAllQueries() throws MdnException;
	public DbConnection dbConnection(HttpServletRequest request);
	public List<QueryDobj> searchQueryByKeyword(String messagingType, String keyword) throws MdnException;
	/* ----------------------------- WORK WITH MESSAGING INFO (from QueryDobj object)----------------------------- */
	public QueryDobj getQueryByName(String name, int projectId) throws MdnException;	
	public QueryDobj getQueryByIMKeyword(String keyword) throws MdnException;
	public QueryDobj getQueryBySmsKeyword(String keyword, int smsServerId) throws MdnException;
	public List<QueryDobj> getQueryBySmsKeyword(String keyword) throws MdnException;
	public List<QueryDobj> geQueryByEmailId(int emailId) throws MdnException;
	public List<QueryDobj> getAllQueriesBySmsServerId(int smsServerId) throws MdnException;	
	public QueryDobj getUniqueQueryByEmailInfo(int emailAddressId, String emailKeyword) throws MdnException;	
	public QueryDobj getQueryByEmailKeyword(String keyword) throws MdnException;
	/* ----------------------------- WORK WITH USER REPLY MESSAGE ----------------------------- */
	public List<UserReply> getAllUserReplies(int projectId) throws MdnException;	
	public UserReply getUserReplyById(int userReplyId) throws MdnException;
	public UserReply getURPropertiesByID(int userReplyID) throws MdnException;
	//public List<UserReply> getUserRepliesByQueryId(int queryId) throws MdnException;
	public List<UserReply> getUserRepliesByParentId(int parentId) throws MdnException;
	public UserReply getUserReplyrMsgInfoByID(int userReplyId) throws MdnException;
	public UserReply getUserReplyByMessageText(String msgTxt) throws MdnException;
	public UserReply getUserReplyByMsgTxtAndParentId(int parentId, String msgTxt) throws MdnException;
	
	//public QueryDobj getQueryByID2(int queryID) throws MdnException;
	//
	public List<QueryCriteriaDobj> getQueryCriteriaByQueryID(int queryID, String objectType) throws MdnException;
	public QueryCriteriaDobj getQueryCriteriaByID(int id) throws MdnException;
	
	//public RecordSet getSelectQueryResultWithUserInput(String msgID, String queryID, ArrayList<String> userInputs, String objectType);
	public RecordSet getSelectQueryResultWithUserInput(QueryDobj query, UserReply ur , ArrayList<String> userInputs, String objectType, int userId) throws MdnException;
	public RecordSet getQueryResultWithUserInput(DataView dataView, String sql, ArrayList<String> userInputs, int userId)  throws MdnException;
	public String replaceSqlWithUserInput(String sql, ArrayList<String> userInputs, int userId) throws MdnException;
	//public int getUpdateQueryResultWithUserInput(String msgID, String queryID, ArrayList<String> userInputs, String objectType);
	public int getUpdateQueryResultWithUserInput(QueryDobj query, UserReply ur, ArrayList<String> userInputs, String objectType, int userId);	
	public int getInsertQueryResultWithUserInput(QueryDobj query, UserReply ur, ArrayList<String> userInputs, String objectType, int userId);
	
	public FieldDobj getMetaField(int fieldID) throws MdnException;
	public List<WebServiceDobj> getAllSampleWebServices() throws MdnException;
	public List<WebServiceDobj> getAllThirdPartyWebServices() throws MdnException;
	public WebServiceOperationDobj getWebServiceOperationByID(int id) throws MdnException;
	public List<WebServiceOperationDobj> getAllWebServiceOperations(int projectId) throws MdnException;
  /* ----------------------------- GROUP Query Statments ----------------------------- */
  /**
  * Retrieves a group based on the group ID.
  * @param groupID		The group ID for which to return the group. 
  * @return					The group object if found otherwise <code>null</code>.
  */
  public Group getGroup(int groupID) throws MdnException;
  public Group getGroupByName(String groupName, int projectId) throws MdnException;
  public Group getGuestGroup(int projectId) throws MdnException;
  public Vector<Group> getGroups(int projectId) throws MdnException;
  public List<GroupDataView> getGroupViewsPermissions(int groupID) throws MdnException;
  public List<GroupTablePermission> getGroupTablePermissions(int groupID) throws MdnException;
  public GroupTablePermission getGroupTablePermissionByEntityID(int EntityId,int groupID) throws MdnException;
  public int saveGroupDataView(GroupDataView groupDataView) throws MdnException;
  public void deleteGroupDataView(GroupDataView groupDataView) throws MdnException;	
  public Vector<GroupDataView> getGroupDataViewByFieldID(int groupDataViewId) throws MdnException;
  public GroupDataView getGroupDataViewByGroupID(int id) throws MdnException;
  public GroupDataView getGroupDataViewByID(int id) throws MdnException;
  public GroupTablePermission getGroupTablePermissionByID(int id) throws MdnException;
  public Vector<FieldExclusion> getFieldExclusion(int groupID) throws MdnException;
  public int saveGroup(Group group) throws MdnException;
  public int markGroupAsCleared(int userID) throws MdnException;
  public int markGroupAsRecycled(int userID) throws MdnException;
  public int deleteGroup(int groupID) throws MdnException;
  public GroupMembership getUserGroupByProjectId(int userId, int projectId) throws MdnException;
  public List<GroupMembership> getUserGroupByGroupId(int groupId, int projectId) throws MdnException;
  //list<User> getUsersbyGroupId(int groupIdInMembershipTable);
  /* ----------------------------------------------------------------------------------- */
	
  /* ----------------------------- USER Query Statments ----------------------------- */  
  public UserWrapper getLoginUser(String username, String password) throws MdnException;
  public User getUser(int userID, boolean includeGroups) throws MdnException;
  /**
   * Checks whether a given username exists already.
   * @param username
   * @return
   */
  public User getUserByName(String username) throws MdnException;
  public User getUserByMobileNumber(String mobileNumber) throws MdnException;
  public Vector<wsl.fw.security.User> getUsers() throws MdnException;
  public Vector<User> getUsersByGroupId(int groupId) throws MdnException;
  public Vector<User> getAdminUsers() throws MdnException;
  public int saveUser(User user) throws MdnException;
  public Vector<Feature> getAllPrivileges() throws MdnException;
  //public Vector<Feature> getAllLicenseTypes() throws MdnException;
  //public ResultWrapper getUserLicenses();
  //public ArrayList<String> getUserLicenseTypes();
  /**
	* Marks the user in the DB as being cleared. This should only happen if the
	* user has been revived from the recycle bin. Use the flag as defined on
	* <code>Constants.MARKED_AS_CLEARED</code>.
	* @param userID	The user ID of the user to clear.
	* @return				A flag indicating the outcome of the DB transaction.
	*/
  public int markUserAsCleared(int userID)  throws MdnException;
  /**
	* Marks the user in the DB as being recycled, i.e., the user has been put into
	* the recycle bin. Use the flag as defined on
	* <code>Constants.MARKED_AS_RECYCLED</code>.
	* @param userID	The user ID of the user to recycle.
	* @return				A flag indicating the outcome of the DB transaction.
	*/
  public int markUserAsRecycled(int userID) throws MdnException;
  /**
   * Deletes the user in the DB, i.e., the user has been deleted
   * @param userID	    The user ID of the user to recycle.
   * @return				A flag indicating the outcome of the DB transaction.
   */
  public int deleteUser(int userID) throws MdnException;
  public User getUserByEmailAddress(String emailAddress) throws MdnException;
  
  /* ----------------------------- MDN User Custom Fields Statments ----------------------------- */ 
	public List<CustomField> getAllCustomFields() throws MdnException;
	public CustomField getCustomFieldById(int id) throws MdnException;
	public CustomField getCustomFieldByName(String name) throws MdnException;
	public boolean isCustomQueryNameDuplicate(String capitalName) throws MdnException;
	public List<UserCustomField> getUserCustomFieldsByUserId(int userId) throws MdnException;
	public UserCustomField getUserCustomByCustomId(int userId, int customId) throws MdnException;
	public List<UserCustomField> getCustomQueriseByCustomId(int customId) throws MdnException;
  /* ----------------------------------------------------------------------------------- */
	
  /* ----------------------------- MDN IM Connections Query Statments ----------------------------- */ 
	public int createIMConnection(IMConnection imConnection) throws MdnException;
	public Vector<IMConnection> getAllIMConnections() throws MdnException;
	public boolean isDuplicateIMConnection(int type) throws MdnException;
	public IMConnection getImConnectionByID(int connectionID) throws MdnException;
	public void changedStatus(IMConnection imConnection) throws MdnException;
	public int deleteIMConnection(int imID) throws MdnException;
	public IMConnection getImConnectionByTypeID(int type) throws MdnException;
  /* ----------------------------------------------------------------------------------------------- */	
	
	/* ----------------------------- MDN Messaging Info Query Statments(for Email or SMS or IM messages) ----------------------------- */
	/**
	 * Saves recieved IM Messages to MDN DB.
	 * @param imMessage
	 * @return int - is more than 0 if dosn't have any DB problem
	 */
	public int saveRecievedIMMessage(IMMessage imMessage) throws MdnException;
	public QueryDobj getQueryPropertiesByID(int queryID) throws MdnException;
	public QueryDobj getQueryBuilderInfoByID(int queryID) throws MdnException;
	public QueryDobj getMessagingInfoByID(int queryID) throws MdnException;
	/* ----------------------------------------------------------------------------------------------- */
	
	/* ----------------------------- MDN IM Contacts Query Statments ----------------------------- */ 
	public Vector<IMContact> getImContactsByUserID(int userID) throws MdnException;
	public int saveIMContact(IMContact imContact) throws MdnException;
	public IMContact getUserIMContactByContactText(String userContactText) throws MdnException;
	public IMContact getIMContactByID(int contactID) throws MdnException;
	public IMContact getUserIMContactByConnType(int connectionType, int userId) throws MdnException;
	public int removeIMContact(int contactID) throws MdnException;
	/* ----------------------------------------------------------------------------------------------- */	
	public MdnMessageSeparator getMessageSeparator() throws MdnException;
	/* ----------------------------- MDN Email Setting Query Statments ----------------------------- */	
	public int saveEmailSetting(MdnEmailSetting emailSetting) throws MdnException;
	public MdnEmailSetting getEmailSetting() throws MdnException;
	public List<MdnEmailSetting> getMdnEmailAddresses() throws MdnException;
	public MdnEmailSetting getEmailSettingById(int mdnEmailId) throws MdnException;
	public MdnEmailSetting getEmailByAddress(String emailAddress) throws MdnException;
	/* --------------------------------------------------------------------------------------------- */	
	/* ----------------------------- MDN Sms Setting Query Statments ----------------------------- */	
	public int saveSmsSetting(MdnSmsSetting smsSetting) throws MdnException;
	public MdnSmsSetting getSmsSetting() throws MdnException;
	//public MdnSmsGateway getSmsGateway() throws MdnException;
	public List<MdnSmpp> getAllSmppGateway() throws MdnException;
	public MdnSmpp getSmsGatewayByNumber(String sourceNumber) throws MdnException;
	public MdnSmpp getSmsGatewayByID(int id) throws MdnException;
	/* --------------------------------------------------------------------------------------------- */	
	
	public List<MessageLog> getMessageLogsByUserId(int userId) throws MdnException;
	public MessageLog getMessageLogById(int msgLogId) throws MdnException;
	/**
	 * Retrieves the content of the recycle bin. The items of the recycle bin
	 * represent all possible objects and are simply wrapped in a transfer object. 
	 * @return
	 */
	public Vector<RecycleBinItem> getUsersGroupsRecycleBinContent(int projectId) throws MdnException;
	public Vector<RecycleBinItem> getDBRecycleBinContent(int projectId) throws MdnException;
	public Vector<RecycleBinItem> getProjectRecycleBinContent() throws MdnException;
	public Vector<RecycleBinItem> getSettingsRecycleBinContent() throws MdnException;
	
	public List<ProjectDobj> getNavProjects()  throws MdnException;
	public ProjectDobj getNavProjectById(int id) throws MdnException;
	public ProjectDobj getNavProjectByName(String name) throws MdnException;
	public ProjectDobj getDefaultProject() throws MdnException;
	
	public MessagingSettingDetails getGuestMsgInfo(String messagingSeverType) throws MdnException;
	public List<BlockContacts> getBlockContacts() throws MdnException;
	public Map<String, BlockContacts> getBlockContactsByType(String type) throws MdnException;
	public Boolean getPublicGroupFlag();
	public BlockContacts getBlockContact(String type, String contact) throws MdnException;
	public BlockContacts getBlockContact(int id) throws MdnException;
	public TemporaryBlockContacInfo getTempBlockContacts() throws MdnException;
		
	public String[] getLogFilePath();
}
