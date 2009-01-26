package com.framedobjects.dashwell.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.DataSourceException;
import wsl.fw.datasource.RecordSet;
import wsl.fw.exception.MdnException;
import wsl.fw.security.User;
import wsl.mdn.dataview.GroupTablePermission;
import wsl.mdn.dataview.QueryDobj;
import wsl.mdn.dataview.WebServiceOperationDobj;
import wsl.mdn.mdnim.IMConnection;
import wsl.mdn.mdnmsgsvr.MessagingSettingDetails;
import wsl.mdn.mdnmsgsvr.MdnMessageSeparator;
import wsl.mdn.mdnmsgsvr.TemporaryBlockContacInfo;
import wsl.mdn.mdnmsgsvr.UserReply;

import com.framedobjects.dashwell.biz.GuestMessagesInfo;
import com.framedobjects.dashwell.biz.MessageObject;
import com.framedobjects.dashwell.biz.MsgSessionManager;
import com.framedobjects.dashwell.db.DataAgentFactory;
import com.framedobjects.dashwell.db.IDataAgent;
import com.framedobjects.dashwell.db.RmiAgent;
import com.framedobjects.dashwell.mdnEmail.MdnMailService;
import com.framedobjects.dashwell.mdninstantmsg.MDNIMServer;
import com.framedobjects.dashwell.mdninstantmsg.connection.AbstractIMConnection;
import com.framedobjects.dashwell.utils.MessagingUtils;
import com.framedobjects.dashwell.utils.webservice.ParamHelper;
import com.framedobjects.dashwell.utils.webservice.ParamListItem;
import com.framedobjects.dashwell.mdninstantmsg.eventhandler.AbstractIMEventHandler;
import com.framedobjects.dashwell.mdnsms.MdnSmppGatewayServer;
import com.framedobjects.dashwell.mdnsms.MdnSmsServer;
import com.itbs.aimcer.commune.MessageSupport;

/**
 * Message Manager could be use via all messaging services (Email/IM/GSM/SMPP)
 * Provide Messaging service session management
 * Filtering Spam received query messages (permanent-temporary block contacts)
 * Handle sequent messaging queries as wsl.mdn.mdnmsgsvr.UserReply
 */
public class MessageManager {
	public static String GUEST_MAP_KEY = "guestMsgMap";
	public static String GUEST_OBJ = "guestObj";
	
	public static String GUEST_LIMITATION = "unlimited";
	
	public final static String UR_PARENT_MAP = "urParentMap";
	public final static String UR_START_TIMEOUT_MAP = "urStartTimeMap";
	public final static String UR_TIMEOUT_KEY = "urTimeoutMap";
	
	private static IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	private UserReply userReply;
	private QueryDobj queryDobj;
	private String userIdKey;
	private static MsgSessionManager session;
	private String emailKeyword;
	private ArrayList<String> uiToHandleNoKeywordQueries;
	
	public static int savePeriod = 5;//mintuts
	
	private static boolean isDisconnect = false;
	private static int cancelTime = 0;	

    /**
     * Default constructor.
     */	
	public MessageManager(String userIdKey, MsgSessionManager session){
		this.userIdKey = userIdKey;
		this.session = session;
	}
	
	public UserReply getUserReply(){
		return userReply;
	}
	
	public QueryDobj getQueryDobj(){
		return queryDobj;
	}

	/**
	 *  Separator is defined in setting page.
	 * @return String the messaging seperator of this project
	 */
	public static String getMdnSeparator(){
		String separator = null;
		try {
			MdnMessageSeparator mdnMsgSeparator = dataAgent.getMessageSeparator();
			if(mdnMsgSeparator != null)
				separator = mdnMsgSeparator.getConditionSeperator();
		} catch (MdnException e) {
			e.printStackTrace();
		}
		if(separator == null || separator.equals(""))
			separator = " ";
		return separator;
	}
	
	/**
	 *  Initialized message session 
	 * @return MsgSessionManager
	 */	
	public MsgSessionManager initMessageSession(){
		Map urParentMap  = new HashMap();//key = userId , value = parentId
		Map urStartTimeMap = new HashMap();//key = userId , value = startTime
        Map urTimeoutMap = new HashMap();//key = userId , value = UserReply timeout
		
		if(session.getAttribute(UR_PARENT_MAP) != null)
        	urParentMap = (Map)session.getAttribute(UR_PARENT_MAP);

        if(session.getAttribute(UR_START_TIMEOUT_MAP) != null)
	    	urStartTimeMap = (Map)session.getAttribute(UR_START_TIMEOUT_MAP);
	    
        if(session.getAttribute(UR_TIMEOUT_KEY) != null)
        	urTimeoutMap = (Map)session.getAttribute(UR_TIMEOUT_KEY);
        
        session.setAttribute(UR_PARENT_MAP, urParentMap);
        session.setAttribute(UR_START_TIMEOUT_MAP, urStartTimeMap);
        session.setAttribute(UR_TIMEOUT_KEY, urTimeoutMap);
        
        
        return session;
	}
	
	/**
	 *  Defined message object as a Query Messaging or User Reply messaging by unique user_Id key 
	 * @return MsgSessionManager
	 */		
	public MessageObject getMessageObject(String keyword){
		MessageObject messagingObject = null;
		Map urParentMap = (Map)session.getAttribute(UR_PARENT_MAP);
		try {
			if(!urParentMap.containsKey(userIdKey)){
		    	QueryDobj queryMessaging;
					queryMessaging = dataAgent.getQueryByIMKeyword(keyword);
		    	queryDobj = queryMessaging;
		    	if(queryMessaging != null && queryMessaging.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT){
		    		messagingObject = MessageObject.getMessageObject(queryMessaging);
		    		messagingObject.setKeyword(keyword);
		    		messagingObject.setDisplayResult(queryMessaging.getImDisplayResult());
		    		messagingObject.setTimeout(queryMessaging.getTimeout());
		    		messagingObject.setProjectId(queryMessaging.getProjectId());
		    	}
		    }else{
		    	String parenIdStr = urParentMap.get(userIdKey).toString();
		    	UserReply ur = dataAgent.getUserReplyByMsgTxtAndParentId(Integer.parseInt(parenIdStr), keyword);
		    	userReply = ur;
		    	if(ur != null){
		    		messagingObject = MessageObject.getMessageObject(ur);
		    		messagingObject.setTimeout(ur.getTimeout());
		    		messagingObject.setProjectId(ur.getProjectId());
		    	}
		    }
		} catch (MdnException e) {
			e.printStackTrace();
		}
		return messagingObject;
	}
	
	/**
	 * Get Message Object(Query/UserReply)for SMS 
	 * @param smppServerId
	 * @param keyword
	 * @return MessageObject
	 */			
	public MessageObject getSMSMessageObject(String keyword, int smppServerId){
		MessageObject messagingObject = null;
		Map urParentMap = (Map)session.getAttribute(UR_PARENT_MAP);
		try {
			if(!urParentMap.containsKey(userIdKey)){
		    	QueryDobj queryMessaging;
				queryMessaging = dataAgent.getQueryBySmsKeyword(keyword, smppServerId); 
		    	queryDobj = queryMessaging;
		    	if(queryMessaging != null && queryMessaging.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT){
		    		messagingObject = MessageObject.getMessageObject(queryMessaging);
		    		messagingObject.setKeyword(keyword);
		    		messagingObject.setDisplayResult(queryMessaging.getMobileDisplayResult());
		    		messagingObject.setTimeout(queryMessaging.getTimeout());
		    	}
		    }else{
		    	String parenIdStr = urParentMap.get(userIdKey).toString();
		    	UserReply ur = dataAgent.getUserReplyByMsgTxtAndParentId(Integer.parseInt(parenIdStr), keyword);
		    	userReply = ur;
		    	if(ur != null){
		    		messagingObject = MessageObject.getMessageObject(ur);
		    		messagingObject.setTimeout(ur.getTimeout());
		    	}
		    }
		} catch (MdnException e) {
			e.printStackTrace();
		}
		return messagingObject;
	}		
	
	/**
	 * Get Message Object(Query/UserReply)for Email 
	 * @param smppServerId
	 * @param receivedMsg
	 * @param separator 
	 * @return MessageObject
	 */				
	public MessageObject getEmailMessageObject(int emailId, String receivedMsg, String separator){
		MessageObject messagingObject = null;
		Map urParentMap = (Map)session.getAttribute(UR_PARENT_MAP);
		try {
			if(!urParentMap.containsKey(userIdKey)){
		    	QueryDobj queryMessaging = null;

		    	if(receivedMsg == null)
		    		receivedMsg = "";
		    	
		    	QueryDobj withoutKeywordQuery = dataAgent.getUniqueQueryByEmailInfo(emailId, "");
		    	if(withoutKeywordQuery != null){
		    		emailKeyword = "";
		    		queryDobj = withoutKeywordQuery;
		    		uiToHandleNoKeywordQueries = MessagingUtils.getUserInputForNoKeywordQuery(separator,receivedMsg);		    		
		    	}else{
					Map userInputsMap = MessagingUtils.userInputsParser(separator, receivedMsg);
					uiToHandleNoKeywordQueries = (ArrayList<String>)userInputsMap.get("userInputs");
					
					emailKeyword = (String)userInputsMap.get("keyword");
		    		
		    		queryMessaging = dataAgent.getUniqueQueryByEmailInfo(emailId, emailKeyword.toLowerCase());//dataAgent.getQueryByIMKeyword(keyword);
		    		queryDobj = queryMessaging;
		    	}
		    	if(queryDobj != null && queryDobj.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT){
		    		messagingObject = MessageObject.getMessageObject(queryDobj);
		    		messagingObject.setKeyword(receivedMsg.toLowerCase());
		    		messagingObject.setDisplayResult(queryDobj.getEmailDisplayResult());
		    		messagingObject.setTimeout(queryDobj.getTimeout());
		    		messagingObject.setDatasourceStatus(queryDobj.getDatasourceStatus());//webserviceeeee
		    		messagingObject.setWebServiceId(queryDobj.getWebServiceId());//webserviceeeee
		    	}
		    }else{
				Map userInputsMap = MessagingUtils.userInputsParser(separator, receivedMsg);
				uiToHandleNoKeywordQueries = (ArrayList<String>)userInputsMap.get("userInputs");
				String keyword = (String)userInputsMap.get("keyword");
				
		    	String parenIdStr = urParentMap.get(userIdKey).toString();
		    	UserReply ur = dataAgent.getUserReplyByMsgTxtAndParentId(Integer.parseInt(parenIdStr), keyword.toLowerCase());
		    	userReply = ur;
		    	if(ur != null){
		    		messagingObject = MessageObject.getMessageObject(ur);
		    		messagingObject.setTimeout(ur.getTimeout());
		    	}
		    }
		} catch (MdnException e) {
			e.printStackTrace();
		}
		return messagingObject;
	}
	
	/**
	 * Get Message Object(Query/UserReply)for SMPP
	 * @param smppServerId
	 * @param receivedMsg
	 * @param separator
	 * @return MessageObject
	 */				
	public MessageObject getSmppMessageObject(int smppServerId, String receivedMsg, String separator){
		MessageObject messagingObject = null;
		Map urParentMap = (Map)session.getAttribute(UR_PARENT_MAP);
		String smsKeyword;
		try {
			if(!urParentMap.containsKey(userIdKey)){
		    	QueryDobj queryMessaging = null;

		    	if(receivedMsg == null)
		    		receivedMsg = "";
		    	
		    	QueryDobj withoutKeywordQuery = dataAgent.getQueryBySmsKeyword("", smppServerId);
		    	if(withoutKeywordQuery != null){
		    		smsKeyword = "";
		    		queryDobj = withoutKeywordQuery;
		    		uiToHandleNoKeywordQueries = MessagingUtils.getUserInputForNoKeywordQuery(separator,receivedMsg);		    		
		    	}else{
					Map userInputsMap = MessagingUtils.userInputsParser(separator, receivedMsg);
					uiToHandleNoKeywordQueries = (ArrayList<String>)userInputsMap.get("userInputs");
					
					smsKeyword = (String)userInputsMap.get("keyword");
		    		
		    		queryMessaging = dataAgent.getQueryBySmsKeyword(smsKeyword.toLowerCase(), smppServerId);
		    		queryDobj = queryMessaging;
		    	}
		    	if(queryDobj != null && queryDobj.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT){
		    		messagingObject = MessageObject.getMessageObject(queryDobj);
		    		messagingObject.setKeyword(receivedMsg.toLowerCase());
		    		messagingObject.setDisplayResult(queryDobj.getMobileDisplayResult());
		    		messagingObject.setTimeout(queryDobj.getTimeout());
		    	}
		    }else{
				Map userInputsMap = MessagingUtils.userInputsParser(separator, receivedMsg);
				uiToHandleNoKeywordQueries = (ArrayList<String>)userInputsMap.get("userInputs");
				String keyword = (String)userInputsMap.get("keyword");
				
		    	String parenIdStr = urParentMap.get(userIdKey).toString();
		    	UserReply ur = dataAgent.getUserReplyByMsgTxtAndParentId(Integer.parseInt(parenIdStr), keyword.toLowerCase());
		    	userReply = ur;
		    	if(ur != null){
		    		messagingObject = MessageObject.getMessageObject(ur);
		    		messagingObject.setTimeout(ur.getTimeout());
		    	}
		    }
		} catch (MdnException e) {
			e.printStackTrace();
		}
		return messagingObject;
	}
	
	/**
	 * Provide Replying Message via received messaged info which sent by user
	 * 
	 * @param userId
	 * @param userInputsMap
	 * @param messageObject
	 * @param langXmlFileName
	 * @return ReplyingMessage as a String
	 */
	public String getReplyingMessage(int userId, Map userInputsMap, MessageObject messageObject, String langXmlFileName){
		String replyingMsg ="";
		RecordSet rs = null;
		User mdnUser;
		try {
			mdnUser = dataAgent.getUser(userId, true);

			int projectId = messageObject.getProjectId();
        	List authorityFieldsList = new ArrayList();//MessagingUtils.getAuthenticatedMessaginFields(mdnUser, projectId);
		
			int displayResult = messageObject.getDisplayResult();
			String responseFormat = messageObject.getResponse();
	
			ArrayList<String> userInputsList = (ArrayList<String>)userInputsMap.get("userInputs");
			
			int resultInt = 0;
			if(messageObject.getType().equalsIgnoreCase(Constants.SELECT_QUERY_TYPE)){
				authorityFieldsList = MessagingUtils.getAuthenticatedMessaginFields(mdnUser, projectId);
				rs = dataAgent.getSelectQueryResultWithUserInput(queryDobj, userReply, userInputsList, messageObject.getObjectType(), userId);//(messagingInfo, null, userInputsList, null);//0 = Faild
			}else if(messageObject.getType().equalsIgnoreCase(Constants.INSERT_QUERY_TYPE)){
				int entityId;
				if(queryDobj != null)
					entityId = queryDobj.getViewOrTableId(); 
				else	
					entityId = userReply.getViewOrTableId();								
				
				GroupTablePermission hasPermission = MessagingUtils.getAuthenticatedMessaginEntity(mdnUser, projectId, entityId);
				if(hasPermission != null)
					resultInt = dataAgent.getInsertQueryResultWithUserInput(queryDobj, userReply, userInputsList, messageObject.getObjectType(), userId);//0 = Faild
				else
					resultInt = Constants.NO_PERMISSION_INSERT;
				
			}else if(messageObject.getType().equalsIgnoreCase(Constants.UPDATE_QUERY_TYPE)){
				int entityId;
				if(queryDobj != null)
					entityId = queryDobj.getViewOrTableId(); 
				else	
					entityId = userReply.getViewOrTableId();				
				
				GroupTablePermission hasPermission = MessagingUtils.getAuthenticatedMessaginEntity(mdnUser, projectId, entityId);
				if(hasPermission != null)
					resultInt = dataAgent.getUpdateQueryResultWithUserInput(queryDobj, userReply, userInputsList, messageObject.getObjectType(), userId);// 0 = Faild
				else
					resultInt = Constants.NO_PERMISSION_UPDATE;								
			}
			
	        if( rs!= null )
	        {
	        	Vector allFields;
	        	if(queryDobj!=null)
	        		allFields = queryDobj.getDataView().getFields();
	        	else
	        		allFields = userReply.getDataView(true).getFields();	        	
	        	
	        	Vector rows = rs.getRows();
	        	if( rows != null && displayResult > rows.size())
	        		displayResult = rows.size();

	        	for(int i = 0; rows != null && i < displayResult; i++)
	        	{
	        		// get the data object
	        		DataObject obj = (DataObject)rows.elementAt(i);
	        		replyingMsg += MessagingUtils.responseMassageParser2(responseFormat, authorityFieldsList, obj, allFields) + " \n";
	        	}
	        }else if(resultInt > 0 && responseFormat != null && !responseFormat.equals("") )//Just for using update and insert query
	        	replyingMsg = responseFormat;
	        else if(resultInt == RmiAgent.FAILED_EXEC_SQL )//-10
	        	replyingMsg = MessageConstants.getMessage(langXmlFileName, MessageConstants.FAILED_QUARY_MSG_RESPONSE);//"FAILED";
	        else if(resultInt == Constants.NO_PERMISSION_INSERT )//-20
	        	replyingMsg = MessageConstants.getMessage(langXmlFileName, MessageConstants.NO_PERMISSION_IN_RESP);	        	        
	        else if(resultInt == Constants.NO_PERMISSION_UPDATE )//-25
	        	replyingMsg = MessageConstants.getMessage(langXmlFileName, MessageConstants.NO_PERMISSION_UP_RESP);	        	        	        
		} catch (MdnException e) {
			e.printStackTrace();
		}
        return replyingMsg;
	}
	
	/**
	 * Provide Replying Message via received messaged info, It would be invoked for MDN users
	 * @param userId
	 * @param messageObject
	 * @param langXmlFileName
	 * @return String replyingMsg
	 */
	public String getAdvanceReplyingMessage(int userId, MessageObject messageObject, String langXmlFileName){
		String replyingMsg ="";
		RecordSet rs = null;
		User mdnUser;
		try {
			mdnUser = dataAgent.getUser(userId, true);
			List authorityFieldsList = new ArrayList();
			
			int displayResult = messageObject.getDisplayResult();
			String responseFormat = messageObject.getResponse();
	
			ArrayList<String> userInputsList = uiToHandleNoKeywordQueries;
			
			int resultInt = 0;
			
			
			if(messageObject.getDatasourceStatus() == 1){
			if(messageObject.getType().equalsIgnoreCase(Constants.SELECT_QUERY_TYPE)){
				authorityFieldsList = MessagingUtils.getAuthenticatedMessaginFields(mdnUser, messageObject.getProjectId());
				rs = dataAgent.getSelectQueryResultWithUserInput(queryDobj, userReply, userInputsList, messageObject.getObjectType(), userId);//(messagingInfo, null, userInputsList, null);//0 = Faild
			}else if(messageObject.getType().equalsIgnoreCase(Constants.INSERT_QUERY_TYPE)){
				int entityId;
				if(queryDobj != null)
					entityId = queryDobj.getViewOrTableId(); 
				else	
					entityId = userReply.getViewOrTableId();								

				GroupTablePermission hasPermission = MessagingUtils.getAuthenticatedMessaginEntity(mdnUser, messageObject.getProjectId(), entityId);
				if(hasPermission != null)
					resultInt = dataAgent.getInsertQueryResultWithUserInput(queryDobj, userReply, userInputsList, messageObject.getObjectType(), userId);//-10 = Faild
				else
					resultInt = Constants.NO_PERMISSION_INSERT;
			}else if(messageObject.getType().equalsIgnoreCase(Constants.UPDATE_QUERY_TYPE)){
				int entityId;
				if(queryDobj != null)
					entityId = queryDobj.getViewOrTableId(); 
				else	
					entityId = userReply.getViewOrTableId();
				
				GroupTablePermission hasPermission = MessagingUtils.getAuthenticatedMessaginEntity(mdnUser, messageObject.getProjectId(), entityId);
				if(hasPermission != null)
					resultInt = dataAgent.getUpdateQueryResultWithUserInput(queryDobj, userReply, userInputsList, messageObject.getObjectType(), userId);// -10 = Faild
				else
					resultInt = Constants.NO_PERMISSION_UPDATE;
			}
			
	        if( rs!= null )
	        {
	        	Vector allFields;
	        	if(queryDobj!=null)
	        		allFields = queryDobj.getDataView().getFields();
	        	else
	        		allFields = userReply.getDataView(true).getFields();
	        	
	        	Vector rows = rs.getRows();
	        	if( rows != null && displayResult > rows.size())
	        		displayResult = rows.size();
	        	
	        	for(int i = 0; rows != null && i < displayResult; i++) {
	        		// get the data object
	        		DataObject obj = (DataObject)rows.elementAt(i);
	        		replyingMsg += MessagingUtils.responseMassageParser2(responseFormat, authorityFieldsList, obj, allFields) + "\n";
	        	}
	        }else if(resultInt > 0 && responseFormat != null && !responseFormat.equals("") )//Just for using update and insert query
	        	replyingMsg = responseFormat;
	        else if(resultInt == RmiAgent.FAILED_EXEC_SQL )//-10
	        	replyingMsg = MessageConstants.getMessage(langXmlFileName, MessageConstants.FAILED_QUARY_MSG_RESPONSE);
	        else if(resultInt == Constants.NO_PERMISSION_INSERT )//-20
	        	replyingMsg = MessageConstants.getMessage(langXmlFileName, MessageConstants.NO_PERMISSION_IN_RESP);	        	        
	        else if(resultInt == Constants.NO_PERMISSION_UPDATE )//-25
	        	replyingMsg = MessageConstants.getMessage(langXmlFileName, MessageConstants.NO_PERMISSION_UP_RESP);	   
			}else{//Handle webservices messaging type
				WebServiceOperationDobj selectedWebService;
				try {
					selectedWebService = dataAgent.getWebServiceOperationByID(queryDobj.getWebServiceId());
					ParamHelper paramHelper = MessagingUtils.getParamHelper(selectedWebService);
					List<ParamListItem> resultParam = MessagingUtils.executeWebService(queryDobj.getId(), userInputsList, paramHelper);//new ArrayList<ParamListItem>();
					replyingMsg = MessagingUtils.prepareWSResponseMsg(paramHelper, resultParam, displayResult, responseFormat);
				} catch (MdnException e) {
					e.printStackTrace();
				}	
			}
		} catch (MdnException e) {
			e.printStackTrace();
		}
        return replyingMsg;
	}		
	
	/**
	 * Provide Replying Message by received messaged info, It would be invoked for guest user
	 * which means messages that sent by unknown user contacts as public messages
	 * 
	 * @param userInputsMap
	 * @param messageObject
	 * @param langXmlFileName
	 * @return String replyingMsg
	 */
	public String getReplyingMessageForGuestUser(Map userInputsMap, MessageObject messageObject, String langXmlFileName){
		String replyingMsg ="";
		RecordSet rs = null;

			int projectId = messageObject.getProjectId();
        	List authorityFieldsList = new ArrayList();
		
			int displayResult = messageObject.getDisplayResult();
			String responseFormat = messageObject.getResponse();
	
			ArrayList<String> userInputsList;
			if(userInputsMap != null)
				userInputsList = (ArrayList<String>)userInputsMap.get("userInputs");
			else
				userInputsList = uiToHandleNoKeywordQueries;
			
			int resultInt = 0;
			
			if(messageObject.getDatasourceStatus() == 1){
				if(messageObject.getType().equalsIgnoreCase(Constants.SELECT_QUERY_TYPE)){
					authorityFieldsList = MessagingUtils.getAuthenticatedMessaginFields(null, projectId);
					try {
						rs = dataAgent.getSelectQueryResultWithUserInput(queryDobj, userReply, userInputsList, messageObject.getObjectType(), 0);
					} catch (MdnException e) {
						e.printStackTrace();
					}
				}else if(messageObject.getType().equalsIgnoreCase(Constants.INSERT_QUERY_TYPE)){
					int entityId;
					if(queryDobj != null)
						entityId = queryDobj.getViewOrTableId(); 
					else	
						entityId = userReply.getViewOrTableId();				
					
					GroupTablePermission hasPermission = MessagingUtils.getAuthenticatedMessaginEntity(null, projectId, entityId);
					if(hasPermission != null)
						resultInt = dataAgent.getInsertQueryResultWithUserInput(queryDobj, userReply, userInputsList, messageObject.getObjectType(), 0);//0 = Faild
					else
						resultInt = Constants.NO_PERMISSION_INSERT;
					
				}else if(messageObject.getType().equalsIgnoreCase(Constants.UPDATE_QUERY_TYPE)){
					int entityId;
					if(queryDobj != null)
						entityId = queryDobj.getViewOrTableId(); 
					else	
						entityId = userReply.getViewOrTableId();
					
					GroupTablePermission hasPermission = MessagingUtils.getAuthenticatedMessaginEntity(null, projectId, entityId);
					if(hasPermission != null){
						resultInt = dataAgent.getUpdateQueryResultWithUserInput(queryDobj, userReply, userInputsList, messageObject.getObjectType(), 0);// 0 = Faild
					}else
						resultInt = Constants.NO_PERMISSION_UPDATE;				
				}
				
		        if( rs!= null )
		        {
		        	Vector allFields;
		        	if(queryDobj!=null)
		        		allFields = queryDobj.getDataView().getFields();
		        	else
		        		allFields = userReply.getDataView(true).getFields();
		        	
		        	Vector rows = rs.getRows();
		        	if( rows != null && displayResult > rows.size())
		        		displayResult = rows.size();

		        	for(int i = 0; rows != null && i < displayResult; i++)
		        	{
		        		// get the data object
		        		DataObject obj = (DataObject)rows.elementAt(i);
		        		replyingMsg += MessagingUtils.responseMassageParser2(responseFormat, authorityFieldsList, obj, allFields) + "\n";
		        	}
		        }else if(resultInt > 0 && responseFormat != null && !responseFormat.equals("") )//Just for using update and insert query
		        	replyingMsg = responseFormat;
		        else  if(resultInt == RmiAgent.FAILED_EXEC_SQL )//-10
		        	replyingMsg = MessageConstants.getMessage(langXmlFileName, MessageConstants.FAILED_QUARY_MSG_RESPONSE);	        	        
		        else if(resultInt == Constants.NO_PERMISSION_INSERT )//-20
		        	replyingMsg = MessageConstants.getMessage(langXmlFileName, MessageConstants.NO_PERMISSION_IN_RESP);	        	        
		        else if(resultInt == Constants.NO_PERMISSION_UPDATE )//-25
		        	replyingMsg = MessageConstants.getMessage(langXmlFileName, MessageConstants.NO_PERMISSION_UP_RESP);	        	        
		        
			}else{
				//Manage web services messaging type
				WebServiceOperationDobj selectedWebService;
				try {
					selectedWebService = dataAgent.getWebServiceOperationByID(queryDobj.getWebServiceId());
					ParamHelper paramHelper = MessagingUtils.getParamHelper(selectedWebService);
					List<ParamListItem> resultParam = MessagingUtils.executeWebService(queryDobj.getId(), userInputsList, paramHelper);//new ArrayList<ParamListItem>();
					replyingMsg = MessagingUtils.prepareWSResponseMsg(paramHelper, resultParam, displayResult, responseFormat);
				} catch (MdnException e) {
					e.printStackTrace();
				}
			}
	        return replyingMsg;
	}		
	
	/**
	 * keep some data in session to use by user reply to manage sequence messaging and timeout thread 
	 * 
	 * @param messageObject
	 * @return MsgSessionManager
	 */
	public MsgSessionManager catchMessageDataInSession(MessageObject messageObject){
        Map urStartTimeMap = (Map)session.getAttribute(UR_START_TIMEOUT_MAP);
        Map urTimeoutMap = (Map)session.getAttribute(UR_TIMEOUT_KEY);
        Map urParentMap = (Map)session.getAttribute(UR_PARENT_MAP);
    	
    	int parentId = messageObject.getId();	        
        List<UserReply> childrenList;
		try {
				childrenList = dataAgent.getUserRepliesByParentId(parentId);
	        //	The Query/UR Has any Children?
	        if(childrenList != null && childrenList.size() > 0){//Yes: set the parent properties in session(For an User)
	        	urStartTimeMap.put(userIdKey, String.valueOf((new Date()).getTime()));
	        	urTimeoutMap.put(userIdKey, messageObject.getTimeout());
	        	session.setAttribute(UR_START_TIMEOUT_MAP, urStartTimeMap);
	        	session.setAttribute(UR_TIMEOUT_KEY, urTimeoutMap);
	        	
	        	urParentMap.put(userIdKey, String.valueOf(parentId));
	        	session.setAttribute(UR_PARENT_MAP, urParentMap);
	        	
	        }else{//No:  remove the parent properties in session(For an User)
	        	if(urStartTimeMap.containsKey(userIdKey)){
	        		urStartTimeMap.remove(userIdKey);
	        		session.setAttribute(UR_START_TIMEOUT_MAP, urStartTimeMap);
	        	}
	        	if(urTimeoutMap.containsKey(userIdKey)){
	        		urTimeoutMap.remove(userIdKey);
	        		session.setAttribute(UR_TIMEOUT_KEY, urTimeoutMap);
	        	}
	        	if(urParentMap.containsKey(userIdKey)){
	        		urParentMap.remove(userIdKey);
	        		session.setAttribute(UR_PARENT_MAP, urParentMap);
	        	}
	        }
		} catch (MdnException e) {
			e.printStackTrace();
		}
		return session;
	}
	
	/**
	 * Update/Save some data to check security and filtering to access for recieved public messaging 
	 * That was set by admin for more control to access to data  
	 * 
	 * @param messagingServerType
	 * @param isUnlimited
	 * @param availablePublicMessages
	 * @return
	 */
	public MsgSessionManager editGuestInfoInSession(String messagingServerType, boolean isUnlimited, int availablePublicMessages){
		MessagingSettingDetails guestObj = (MessagingSettingDetails)session.getAttribute(GUEST_OBJ);
		int countForAllGuestUser = guestObj.getTotalMsgCount();
		
		countForAllGuestUser = countForAllGuestUser + 1;
		guestObj.setTotalMsgCount(countForAllGuestUser);
		session.setAttribute(GUEST_OBJ, guestObj);
		
    	try {
	    	if(!isUnlimited && availablePublicMessages != -1 && guestObj.getStatus() == 0){
	    		if(countForAllGuestUser > availablePublicMessages ){
	    			System.out.println("Warning: You have reached the limit of "+ availablePublicMessages + " messages sent through Mobile Data Now to public users. Please purchase the license to get unlimited messages for public users at www.mobiledatanow.com");
	    			guestObj.setStatus(1);
	            	guestObj.setTotalMsgCount(countForAllGuestUser);
					guestObj.save();
	            	session.setAttribute(GUEST_OBJ, guestObj);
	        	}else {
	        		guestObj.setTotalMsgCount(countForAllGuestUser);
	            	guestObj.save();
	            	session.setAttribute(GUEST_OBJ, guestObj);
	        	}
	    	}
		} catch (DataSourceException e) {
			e.printStackTrace();
		}    		
    	return session;
	}
	
	/**
	
	 * Manage temporary block some contacts in specific time
	 * If it was set by admin then a Thread would be run to check the number of received messages for per user in definition time
	 * 
	 * @param guestObj
	 * @param msgControlsInfo
	 * @return
	 */
	public MsgSessionManager manageTemproryBlockContacts(MessagingSettingDetails guestObj, TemporaryBlockContacInfo msgControlsInfo ){
		//Info from DB
		int forPeriod = Integer.valueOf(msgControlsInfo.getMaxPeriod());
		int maxMsg = msgControlsInfo.getMaxMessage();
		cancelTime = Integer.valueOf(msgControlsInfo.getCancelPeriod());
		
		if(forPeriod > 0 ){//if forPeriod == 0 means there is no limitation
			Map guestMsgMap = new HashMap();//key = userKey , value = GuestMessageInfo
			//Catch Info by userKey
	        if(session.getAttribute(GUEST_MAP_KEY) != null){
	        	guestMsgMap = (Map)session.getAttribute(GUEST_MAP_KEY);
	        }
	        session.setAttribute(GUEST_MAP_KEY, guestMsgMap);	        			
	        GuestMessagesInfo newGuestMsgInfo;
	        int guestMsgCount;
	        long firstRecivedMsgTime;
	    	if(guestMsgMap.containsKey(userIdKey)){
	    		//get last info from session
	    		GuestMessagesInfo existsGuestMsgInfo = (GuestMessagesInfo)guestMsgMap.get(userIdKey);
	    		guestMsgCount = existsGuestMsgInfo.getCount();
	    		firstRecivedMsgTime = existsGuestMsgInfo.getFirstMsgReceivedDate().getTime();
	    		
	    		// Set new Msg Info for an user contact
	    		guestMsgCount = guestMsgCount + 1;
	    		existsGuestMsgInfo.setCount(guestMsgCount);
	    		newGuestMsgInfo = existsGuestMsgInfo;
	    		
		    	if(!newGuestMsgInfo.isBlock()){
		    		long now = new Date().getTime();
			    	long checkTime = firstRecivedMsgTime + (forPeriod*60*1000);
			    	System.out.println("check the time&msg count checkTime = " + new Date(checkTime));	    		
		    		if(now <= checkTime){
						if(guestMsgCount> maxMsg){
							System.out.println("Block Temprory : "+ userIdKey);
			    			//shoud be catch this info>>> userId = key , value = setTimeToBlock=now, isBlock=true
			    			newGuestMsgInfo.setIsBlock(true);
			    			newGuestMsgInfo.setTimeToBlock(new Date().getTime());
			    			System.out.println("time to block this user :::: " + new Date());
			    			newGuestMsgInfo.setCancelTime(cancelTime*60*1000 + new Date().getTime());
						}
			    	}else{
			    		System.out.println("Refresh all temprory info again because the period time has passed from first recived msg to check");
			    		guestMsgCount = 1;
			    		newGuestMsgInfo = new GuestMessagesInfo();
			    		newGuestMsgInfo.setCount(guestMsgCount);
			    		newGuestMsgInfo.setFirstMsgReceivedDate(new Date());//Now
						newGuestMsgInfo.setIsBlock(false);	    		
			    	}
		    	}else{
			    	System.out.println("this user is block now and try to unblock that afret difinition cancel time " + new Date(newGuestMsgInfo.getCancelTime()));
			    	if(new Date().getTime()> newGuestMsgInfo.getCancelTime()){//Unblock after cancel period time
			    		System.out.println("Unblock Temprory : " + userIdKey);
			    		guestMsgCount = 1;
			    		newGuestMsgInfo = new GuestMessagesInfo();
			    		newGuestMsgInfo.setCount(guestMsgCount);
			    		newGuestMsgInfo.setFirstMsgReceivedDate(new Date());//Now
						newGuestMsgInfo.setIsBlock(false);    	
						
			    	}	    		
		    	}
		    	
	    	}else{//Init GuestMsgInfo
	    		guestMsgCount = 1;
	    		newGuestMsgInfo = new GuestMessagesInfo();
	    		newGuestMsgInfo.setCount(guestMsgCount);
	    		newGuestMsgInfo.setFirstMsgReceivedDate(new Date());//Now
				newGuestMsgInfo.setIsBlock(false);
	    	}
			guestMsgMap.put(userIdKey, newGuestMsgInfo);
			session.setAttribute(GUEST_MAP_KEY, guestMsgMap);			
		}
		
		return session;
	}	
	public static void clearMessagingSession(MsgSessionManager session){
		if(session != null){
			session.setAttribute(UR_PARENT_MAP, new HashMap());
			session.setAttribute(UR_TIMEOUT_KEY, new HashMap());
		}
	}
	
	public void cancelMessage(){
        Map urStartTimeMap = (Map)session.getAttribute(UR_START_TIMEOUT_MAP);
        Map urTimeoutMap = (Map)session.getAttribute(UR_TIMEOUT_KEY);
        Map urParentMap = (Map)session.getAttribute(UR_PARENT_MAP);

		if(urStartTimeMap.containsKey(userIdKey)){
    		urStartTimeMap.remove(userIdKey);
    		session.setAttribute(UR_START_TIMEOUT_MAP, urStartTimeMap);
    	}

    	if(urTimeoutMap.containsKey(userIdKey)){
    		urTimeoutMap.remove(userIdKey);
    		session.setAttribute(UR_TIMEOUT_KEY, urTimeoutMap);
    	}

    	if(urParentMap.containsKey(userIdKey)){
    		urParentMap.remove(userIdKey);
    		session.setAttribute(UR_PARENT_MAP, urParentMap);
    	}
	}
	
	/**
	 * Run a thread to manage timeout for sequense messages
	 * 
	 * @param session
	 * @param messagingServerType
	 */
	public static void runTimeoutThread(MsgSessionManager session, String messagingServerType){
		//Timeout Thread run
		int freq = Integer.parseInt("1000");
		//long startServerTime = (new Date()).getTime();
		//long timeToSave = startServerTime + (2*60*1000);
		for (; ;) {
			try {
				Thread.sleep(freq);// sleep for freq milliseconds
			
				//If MDNIMServivce is down run it again Automatically
				if(messagingServerType.equals(MDNIMServer.SERVER_TYPE)){
					dataAgent.getAdminUsers();//Just to test RMI connection
    				List<IMConnection> imConnections = (List<IMConnection>)session.getAttribute("imConnList");
    			  	for (IMConnection imConnection : imConnections) {
    			  		if(imConnection.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT){
    						AbstractIMConnection abstractConnection = new AbstractIMConnection();
    						MessageSupport connection = abstractConnection.getConnction(imConnection, session);
    						
    						if(connection!= null)
    						{
    							Iterator listener = connection.getEventListenerIterator();
    							while (listener.hasNext()) {
    								AbstractIMEventHandler element = (AbstractIMEventHandler) listener.next();
									isDisconnect = element.getConnectionError();
									if(isDisconnect){
										element.setConnectionError(false);
										abstractConnection.handleConnection(imConnection, session);
										dataAgent.getAdminUsers();
									}
								}
    						}
    			  		}
    			  	}
				}
    			Map urStartTimeMap = (Map)session.getAttribute(UR_START_TIMEOUT_MAP);//key = userId , value = startTime
	            Map urTimeoutMap = (Map)session.getAttribute(UR_TIMEOUT_KEY);//key = userId , value = UserReply timeout
	    		Map urParentMap = (Map)session.getAttribute(UR_PARENT_MAP);
	    		
	            Iterator itMap = null;
	            if(urTimeoutMap != null)
	            	itMap = urTimeoutMap.keySet().iterator();
	            
	            while (itMap!= null && itMap.hasNext()) {
					String userIdKey = (String) itMap.next();
		            if(urTimeoutMap.containsKey(userIdKey)){
		            	String startTimeStr = urStartTimeMap.get(userIdKey).toString();
		            	String timeoutStr = urTimeoutMap.get(userIdKey).toString();
		            	
		            	long startTime = Long.parseLong(startTimeStr);
		            	long now = (new Date()).getTime();
		            	long timeout = Long.parseLong(timeoutStr) * 1000;
		        		long end = startTime + timeout;
		        		
		        		if(timeout > 0 && now > end){//Timeout
		        			System.out.println("WARNING: Timeout period expired (userId : " + userIdKey +" )");
		            		urStartTimeMap.remove(userIdKey);
		            		session.setAttribute(UR_START_TIMEOUT_MAP, urStartTimeMap);

		            		urTimeoutMap.remove(userIdKey);
		            		session.setAttribute(UR_TIMEOUT_KEY, urTimeoutMap);
		            		
		    	        	if(urParentMap.containsKey(userIdKey)){
		    	        		urParentMap.remove(userIdKey);
		    	        		session.setAttribute(UR_PARENT_MAP, urParentMap); 
		    	        	}
		    	        	itMap = urTimeoutMap.keySet().iterator();
		        		}
		            }
				}
	        } catch (Exception ex) {
	        	System.out.println("Connection Problem!!!");
	        	try {
	        		long waitTimeToRestart = freq + (30*1000);//30 sec 
	        		System.out.println("Wait...");
					Thread.sleep(waitTimeToRestart);// sleep for freq milliseconds
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	        	isDisconnect = true;
	        }
		}
	}		
	
	/**
	 * get definition queries' keyword to show them to user for help
	 * 
	 * @param allQueries
	 * @param msgType
	 * @return String that contains a list of queries keyword
	 */
	public String getKeysList(List<QueryDobj> allQueries, String msgType){
		String keywordsList = msgType + " Help";
		for (QueryDobj q : allQueries) {
			String key = null;
			if(msgType.equalsIgnoreCase(MDNIMServer.SERVER_TYPE))
				key = q.getImKeyword();
			if(msgType.equalsIgnoreCase(MdnSmsServer.SERVER_TYPE) || msgType.equalsIgnoreCase(MdnSmppGatewayServer.SERVER_TYPE))
				key = q.getSmsKeyword();
			if(msgType.equalsIgnoreCase(MdnMailService.SERVER_TYPE)){
				String emailAddress="";
				if(q.getEmailAddressId() != -1){
					try {
						emailAddress = dataAgent.getEmailSettingById(q.getEmailAddressId()).getEmailAddress();
						emailAddress = "Email Service: " + emailAddress + " ,Keyword: ";
					} catch (MdnException e) {
						e.printStackTrace();
					}
				}
				String emailKeyword = q.getEmailKeyword();
				if(emailKeyword!= null && !emailKeyword.equals(""))
					key = emailAddress + emailKeyword + "\n";
				else
					key = emailAddress + "No Keyword \n";
			}
			
			if(key!=null && !key.equals("")){
				if(!keywordsList.contains("\n"))
					keywordsList =  keywordsList + ": \n"+ key;
				else
					keywordsList = keywordsList + "\n"+ key;
			}
		}
		return keywordsList;
	}
	
	public static boolean isBlank(String str){
		if(str != null && !str.trim().equals(""))
			return false;
		else
			return true;
	}	
	
	public static MsgSessionManager getSession(){
		return session;
	}		
}
