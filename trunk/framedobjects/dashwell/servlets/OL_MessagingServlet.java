package com.framedobjects.dashwell.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.smslib.CService;

import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.DataSourceException;
import wsl.fw.datasource.Field;
import wsl.fw.datasource.RecordSet;
import wsl.fw.exception.MdnException;
import wsl.mdn.common.MdnDataManager;
import wsl.mdn.dataview.DataSourceDobj;
import wsl.mdn.dataview.DataView;
import wsl.mdn.dataview.DataViewDataSource;
import wsl.mdn.dataview.DataViewField;
import wsl.mdn.dataview.EntityDobj;
import wsl.mdn.dataview.QueryCriteriaDobj;
import wsl.mdn.dataview.QueryDobj;
import wsl.mdn.dataview.WebServiceOperationDobj;
import wsl.mdn.mdnmsgsvr.BlockContacts;
import wsl.mdn.mdnmsgsvr.CustomField;
import wsl.mdn.mdnmsgsvr.MdnSmpp;
import wsl.mdn.mdnmsgsvr.MessagingSettingDetails;
import wsl.mdn.mdnmsgsvr.MdnEmailSetting;
import wsl.mdn.mdnmsgsvr.MdnMessageSeparator;
import wsl.mdn.mdnmsgsvr.MdnSmsSetting;
import wsl.mdn.mdnmsgsvr.TemporaryBlockContacInfo;
import wsl.mdn.mdnmsgsvr.UserCustomField;
import wsl.mdn.mdnmsgsvr.UserReply;

import com.framedobjects.dashwell.db.DataAgentFactory;
import com.framedobjects.dashwell.db.IDataAgent;
import com.framedobjects.dashwell.utils.MessagingUtils;
import com.framedobjects.dashwell.mdnsms.MdnSmsServer;
import com.framedobjects.dashwell.utils.Constants;
import com.framedobjects.dashwell.utils.MessageConstants;
import com.framedobjects.dashwell.utils.XmlFormatter;
import com.framedobjects.dashwell.utils.webservice.IParamView;
import com.framedobjects.dashwell.utils.webservice.ParamHelper;
import com.framedobjects.dashwell.utils.webservice.ParamListItem;
import com.framedobjects.dashwell.utils.webservice.ParamView;
import com.framedobjects.dashwell.utils.webservice.WebServiceResultRow;


public class OL_MessagingServlet  extends HttpServlet {
	
	private static Logger logger = Logger.getLogger(OL_MessagingServlet.class.getName());
	Map catchMsgObject = new HashMap();
	private List tablesOrViewsList;
	private List<DataSourceDobj> databases;
	private QueryDobj msgInfo;
	private String vtLable;
	private String dbName;
	private Map databasesMap = new HashMap();//Key = id , Value = name
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.debug("URI: " + request.getRequestURI());
	    logger.debug("Query string: " + request.getQueryString());
	
	    Enumeration parameters = request.getParameterNames();
	    while (parameters.hasMoreElements()){
	      String p = (String)parameters.nextElement();
	      logger.debug("-- Parameter: " + p + ": " + request.getParameter(p));
	    }
	    
	    String action = request.getParameter(Constants.REQUEST_PARAM_ACTION);
	    Element root = null;
	    if (action != null){
		    if (action.equalsIgnoreCase("saveEmailSetting") || action.equalsIgnoreCase("editEmailSetting")){
		    	root = saveEmailSetting(request);
		    } else if (action.equalsIgnoreCase("displayEmailSett")){
	      		root = displayEmailSett(request);
		    } else if (action.equalsIgnoreCase("deleteEmailSett")){
	      		root = deleteEmailSett(request);
		    } else if (action.equalsIgnoreCase("displayEmptyMsg")){
	      		root = displayEmptyMsg(request);
		    } else if (action.equalsIgnoreCase("displayMsgInfo")){
	      		root = displayMsgInfo(request);
		    } else if (action.equalsIgnoreCase("displayQueryProperties")){
	      		root = displayQueryProperties(request);
		    } else if (action.equalsIgnoreCase("changedDataBaseItem")){
	      		root = changedDataBaseItem(request);
		    } else if (action.equalsIgnoreCase("changedDataBaseItemForUserReply")){
	      		root = changedDataBaseItemForUserReply(request);
		    } else if (action.equalsIgnoreCase("testResult")){
	      		root = testResult(request);
		    } else if (action.equalsIgnoreCase("showMsgResult")){
	      		root = showMsgResult(request);
		    } else if (action.equalsIgnoreCase("showMsgResponse")){
	      		root = showMsgResponse(request);
		    } else if (action.equalsIgnoreCase("saveMsgResponse")){
	      		root = saveMsgResponse(request);
		    } else if (action.equalsIgnoreCase("getDefaultTextMsgResult")){
	      		root = getDefaultTextMsgResult(request);
		    } else if (action.equalsIgnoreCase("connDisConnSms")){
	      		root = connDisConnSms(request);
		    } else if (action.equalsIgnoreCase("showSms")){
	      		root = showSms(request);
		    } else if (action.equalsIgnoreCase("saveSmsInfo")){
	      		root = saveSmsInfo(request);
		    } else if (action.equalsIgnoreCase("displayQueryBuilderInfo")){
	      		root = displayQueryBuilderInfo(request);
		    } else if (action.equalsIgnoreCase("testMessaginResultXML")){
	      		root = testMessaginResultXML(request);
		    } else if (action.equalsIgnoreCase("displayMsgSeperator")){
	      		root = displayMsgSeperator(request);
		    } else if (action.equalsIgnoreCase("saveMsgSeperator")){
	      		root = saveMsgSeperator(request);
		    } else if (action.equalsIgnoreCase("displayEmptyUserReply")){
	      		root = displayEmptyUserReply(request);
		    } else if (action.equalsIgnoreCase("addUserReply")){
	      		root = addUserReply(request);
		    } else if (action.equalsIgnoreCase("displayUserReplyProps")){
	      		root = displayUserReplyProps(request);
		    } else if (action.equalsIgnoreCase("saveUserReplyProps")){
	      		root = saveUserReplyProps(request);
		    } else if (action.equalsIgnoreCase("saveUserReplyQuery")){
	      		root = saveUserReplyQuery(request);
		    } else if (action.equalsIgnoreCase("displayUserReplyMessagingInfo")){
	      		root = displayUserReplyMessagingInfo(request);
		    } else if (action.equalsIgnoreCase("saveUrMsgInfo")){
	      		root = saveUrMsgInfo(request);
		    } else if (action.equalsIgnoreCase("testURMessaginResult")){
	      		root = testURMessaginResult(request);
		    } else if (action.equalsIgnoreCase("deleteQuery")){
	  			root = deleteQuery(request);
		    } else if (action.equals("deleteUR")){
		    	root = deleteUR(request);
		    } else if (action.equals("msgControls")){
		    	root = getMsgControls(request);		    	
		    } else if (action.equals("saveBlockContact")){
		    	root = saveBlockContact(request);		  
		    } else if (action.equals("saveMsgControl")){ 
		    	root = saveMsgControl(request);		    
		    } else if (action.equals("displaySearchKeywords")){ 
		    	root = displaySearchKeywords(request);		    		    	
		    } else if (action.equals("saveSearchTerm")){ 
		    	root = saveSearchTerm(request);		    		    			    	
		    } else if (action.equals("testSearchTerm")){ 
		    	root = testSearchTerm(request);		    
		    } else if (action.equals("displaySmpp")){ 
		    	root = displaySmpp(request);
		    } else if (action.equals("saveSmpp")){
		    	root = saveSmpp(request);		  
		    } else if (action.equals("recycleSmpp")){
		    	root = recycleSmpp(request);		 
		    } else if (action.equals("displayPubMsgLog")){
		    	root = displayPubMsgLog(request);		  
		    } else if (action.equals("getDefaultResponseForEditWS")){
		    	root = getDefaultResponseForEditWS(request);		    		    			    			    	
		    } else {
		    	root = new XmlFormatter().undefinedAction();
		    }
	    } else {
	    	root = new XmlFormatter().undefinedAction();
	    }
	    
	    // Return the XML-formatted reply.
	    response.setHeader ("Pragma",        "no-cache");
	    response.setHeader ("Cache-Control", "no-cache");
	    response.setContentType("text/xml; charset=UTF-8");
	    String xml = new XMLOutputter().outputString(new Document(root));        
	    logger.debug("xml: " + xml);
	    
	    ServletOutputStream out = response.getOutputStream();
	    out.println(xml);
	}
	private Element saveEmailSetting(HttpServletRequest request){
	    String action = request.getParameter("action");
	    //  Get the language file.
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);

	    String emailId = request.getParameter("emailId");
	    
	    String address = request.getParameter("address");
	    String imapHost = request.getParameter("imapHost");
	    String imapUserName = request.getParameter("imapUserName");
	    String imapPassword = request.getParameter("imapPassword");
	    String imapPort = request.getParameter("imapPort");
	    int imapEncType = Integer.parseInt(request.getParameter("imapEncType"));
	    
	    String smtpHost = request.getParameter("smtpHost");
	    String smtpUsername = request.getParameter("smtpUsername");
	    String smtpPassword = request.getParameter("smtpPassword");
	    String smtpPort = request.getParameter("smtpPort");
	    int smtpEncType = Integer.parseInt(request.getParameter("smtpEncType"));
	    
	    String state = request.getParameter("state");
	    
	    String result = MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL_EMAIL);
	    // Get the user from the database.
	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	    MdnEmailSetting emailSett = null;
	    try {
	    	
	    	if(emailId != null && !emailId.equals('0') && !emailId.equals(Constants.UNDEFINED)){
	    		emailSett = dataAgent.getEmailSettingById(Integer.parseInt(emailId));
	    		emailSett.setState(DataObject.IN_DB);
	    	}else{
	    		emailSett = new MdnEmailSetting();
	    		emailSett.setState(0);
	    		emailId = "0";
	    	}
			
			if(address == null || address.equals("") || address.equals(Constants.UNDEFINED) || !address.contains("@"))
				result = MessageConstants.getMessage(file, MessageConstants.INVALID_EMAIL);
			else {
				MdnEmailSetting EmailSettingByAddress = dataAgent.getEmailByAddress(address);
				if(EmailSettingByAddress != null && EmailSettingByAddress.getId()!= Integer.parseInt(emailId))
					result = MessageConstants.getMessage(file, MessageConstants.DUPLICATE_EMAIL_ADDRESS);
			}
			if(imapHost == null || imapHost.equals("") || imapHost.equals(Constants.UNDEFINED))
				result = MessageConstants.getMessage(file, MessageConstants.MISSING_IMAP_HOST);
			else if(imapUserName == null || imapUserName.equals("") || imapUserName.equals(Constants.UNDEFINED))
				result = MessageConstants.getMessage(file, MessageConstants.MISSING_IMAP_USERNAME);
			else if(imapPassword == null || imapPassword.equals("") || imapPassword.equals(Constants.UNDEFINED))
				result = MessageConstants.getMessage(file, MessageConstants.MISSING_IMAP_PASSWORD);
			else if(smtpHost == null || smtpHost.equals("") || smtpHost.equals(Constants.UNDEFINED))
				result = MessageConstants.getMessage(file, MessageConstants.MISSING_SMTP_HOST);			
			else if(smtpEncType != 1 && (smtpUsername == null || smtpUsername.equals("") || smtpUsername.equals(Constants.UNDEFINED)))//when "None" item wasn't selected needs username and password
				result = MessageConstants.getMessage(file, MessageConstants.MISSING_SMTP_USERNAME);
			else if(smtpEncType != 1 && (smtpPassword == null || smtpPassword.equals("") || smtpPassword.equals(Constants.UNDEFINED)))
				result = MessageConstants.getMessage(file, MessageConstants.MISSING_SMTP_PASSWORD);			
			//else{
			if(result.equalsIgnoreCase(MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL_EMAIL))){
				emailSett.setEmailAddress(address);
				emailSett.setImapHost(imapHost);
				emailSett.setImapUserName(imapUserName);
				emailSett.setImapPassword(imapPassword);
				emailSett.setImapPort(imapPort);
				emailSett.setImapEncryptedType(imapEncType);

				emailSett.setSmtpHost(smtpHost);
				emailSett.setSmtpUsername(smtpUsername);
				emailSett.setSmtpPassword(smtpPassword);
				emailSett.setSmtpPort(smtpPort);
				emailSett.setSmtpEncryptedType(smtpEncType);
				
				emailSett.setState(Integer.parseInt(state));
				emailSett.setDelStatus(Integer.parseInt(Constants.MARKED_AS_NOT_DELETED));
				try {
					emailSett.save();
				} catch (DataSourceException e) {
					e.printStackTrace();
				}
				//dataAgent.saveEmailSetting(emailSett);
			}
	    } catch (MdnException e) {
			e.printStackTrace();
		}
	    
	    boolean isSuccess = false;
	    if(result.equalsIgnoreCase(MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL_EMAIL)))
	    	isSuccess = true;
	    	
	    return new XmlFormatter().displayEmailSett(action, emailSett, result, isSuccess);
	  }

	private Element displayEmailSett(HttpServletRequest request){
	    String action = request.getParameter("action");
	    String emailId = request.getParameter("emailId");	    
	    
	    //  Get the language file.
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    String result = MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL_EMAIL);
	    
	    //String result = "OK";
	    // Get the user from the database.
	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	    MdnEmailSetting emailSett = null;
	    try {
	    	emailSett = dataAgent.getEmailSettingById(Integer.parseInt(emailId));
	    } catch (MdnException e) {
			e.printStackTrace();
			result = "No email setting is found.";
		}
	    
	    boolean isSuccess = false;
	    if(result.equalsIgnoreCase(MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL_EMAIL)))
	    	isSuccess = true;
	    
	    return new XmlFormatter().displayEmailSett(action, emailSett, result, isSuccess);
	}
	
	public Element displayEmptyMsg(HttpServletRequest request){
		String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
		
	    //String dbId = request.getParameter("db-id");
	    String queryType = request.getParameter("query-type");
	    String projectId = request.getParameter("projectId");
	    int intProjectId = 1;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e1) {
			intProjectId = 1;
		}	
	    List tablesOrViewsList = new ArrayList();
	    List<DataSourceDobj> databases = new ArrayList<DataSourceDobj>();

	    if(queryType == null)
	    {
	    	queryType = "1";
	    }
	    int selectedQuerytype = Integer.parseInt(queryType);	    
	    String vtLable = "";
		if(selectedQuerytype == 1)//Select
			vtLable = MessageConstants.getMessage(file, MessageConstants.SELECT_VIEW_LABEL);			
		else{//Update or Insert
			vtLable = MessageConstants.getMessage(file, MessageConstants.SELECT_TABLE_LABEL);
			String defaultResponseFormat = MessageConstants.getMessage(file, MessageConstants.SUCCESS_QUARY_MSG_RESPONSE) + "\n";
		}

		List<MdnEmailSetting> emailAddresses = new ArrayList();
		List<MdnSmpp> allSmpp = new ArrayList();
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	    try {
	    	emailAddresses = dataAgent.getMdnEmailAddresses();
		    // Get the queries from the database.
	    	databases = dataAgent.getAllDbConnections(intProjectId);
	    	allSmpp = dataAgent.getAllSmppGateway();
	    } catch (MdnException e) {
			e.printStackTrace();
		}
	    
	    
	    
		return new XmlFormatter().newMessagingXML(databases, tablesOrViewsList, vtLable, emailAddresses, allSmpp, file);
	  }

	private Element displayMsgInfo(HttpServletRequest request){
		//  Get the language file.
		String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
		int id = Integer.parseInt(request.getParameter("msgId"));
	    String projectId = request.getParameter("projectId");
	    int intProjectId = 1;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e1) {
			intProjectId = 1;
		}	  
		// Format the data.
		initMsg(intProjectId, id, file);
		return new XmlFormatter().displayMsgInfoResult(file, dbName, msgInfo, databases, tablesOrViewsList, vtLable);
  }
	private void initTablesOrViewsList(String queryType, int dbId){
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		tablesOrViewsList = new ArrayList();
		try {
    		List<DataView> views;
    		int queryTypeInt = Integer.parseInt(queryType);
    		
			if(queryTypeInt == Constants.SELECT_QUERY_TYPE_NUM ){//Select
				views = dataAgent.getAllMetaViews(dbId, true);
				tablesOrViewsList = views;
			}else{//Update and Insert
				List tables = dataAgent.getAllMetaTables(dbId, true);//Get all Tables for a DS
				tablesOrViewsList = tables;
			}
		} catch (MdnException e) {
			e.printStackTrace();
		}
	}	

	private void initAllDatabases(int projectId){
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		databases = new ArrayList<DataSourceDobj>();
		try {
			databases = dataAgent.getAllDbConnections(projectId);
		  	for (DataSourceDobj db : databases) {
		  		databasesMap.put(String.valueOf(db.getId()), db.getName());
			}
		} catch (MdnException e) {
			e.printStackTrace();
		}
	}
	
	private int getDbIdByVTId(int viewOrTableId, String queryType){
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		int queryTypeInt = Integer.parseInt(queryType);
		int dbId = -1;
		try {
			if(queryTypeInt == Constants.SELECT_QUERY_TYPE_NUM) { //Select
				DataView view = dataAgent.getMetaViewByID(viewOrTableId, true);
				dbId =  view.getSourceDsId();
			}else {//Update & Insert
				EntityDobj table = dataAgent.getMetaTableByID(viewOrTableId, true);
				dbId =  table.getDataSourceId();
			}
		} catch (MdnException e) {
			e.printStackTrace();
		}
		return dbId;
	}

	private void initMsg(int projectId, int msgId, String file){
		initAllDatabases(projectId);
		
	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		msgInfo = null;
		dbName = "";
		vtLable = "";
		try {
			msgInfo = dataAgent.getQueryPropertiesByID(msgId);//dataAgent.getQueryByID(msgId);
			String querytypeDb = msgInfo.getType();
			int queryType = MessagingUtils.getQueryTypeNumber(querytypeDb);

			if(queryType == Constants.SELECT_QUERY_TYPE_NUM)//Select
				vtLable = MessageConstants.getMessage(file, MessageConstants.SELECT_VIEW_LABEL);			
			else////Update or Insert
				vtLable = MessageConstants.getMessage(file, MessageConstants.SELECT_TABLE_LABEL);

			//Get Selected database Id from DB
			int viewOrTableId = msgInfo.getViewOrTableId();
			int dbIdInDB = msgInfo.getDatabaseId();

			dbName = databasesMap.get(String.valueOf(dbIdInDB)).toString();
    		
    		//int selectedDbId = db.getId();
			initTablesOrViewsList(String.valueOf(queryType), dbIdInDB);//initTablesOrViewsList(String.valueOf(queryType), selectedDbId);
		} catch (MdnException e) {
			e.printStackTrace();
		}
	}
	
	private Element changedDataBaseItem(HttpServletRequest request){
		//  Get the language file.
		String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
		int id = Integer.parseInt(request.getParameter("msgId"));
	    String selectedDbIdStr = request.getParameter("selected-db-id");
	    String selectedQueryType = request.getParameter("selected-q-type");
		String timeout = request.getParameter("timeout");

	    String projectId = request.getParameter("projectId");
	    int intProjectId = 1;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e1) {
			intProjectId = 1;
		}	   	    
	    
	    //These data for caching user input data in the screen before saving
	    
	    List<MdnEmailSetting> emailAddresses = new ArrayList();
    	List<MdnSmpp> allSmpp = new ArrayList();	    
	    List tablesOrViewsList = new ArrayList();
	    List<DataSourceDobj> databases = new ArrayList<DataSourceDobj>();
	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		QueryDobj msgInfo = null;
		//String dbName = "";
		String vtLable = "";
	    
		try {
			if(id != -1)
				msgInfo = dataAgent.getQueryByID(id);
			else
				msgInfo = new QueryDobj();
			
			if(Integer.parseInt(selectedQueryType) == Constants.UPDATE_QUERY_TYPE_NUM || Integer.parseInt(selectedQueryType) == Constants.INSERT_QUERY_TYPE_NUM)//Update or insert
				vtLable = MessageConstants.getMessage(file, MessageConstants.SELECT_TABLE_LABEL);			
			else
				vtLable = MessageConstants.getMessage(file, MessageConstants.SELECT_VIEW_LABEL);
			
			//Combo data
			emailAddresses = dataAgent.getMdnEmailAddresses();
	    	allSmpp = dataAgent.getAllSmppGateway();
			databases = dataAgent.getAllDbConnections(intProjectId);

			//Get Selected database Id from DB
			int dbIdInDB;
			List<DataView> views;
    		tablesOrViewsList = new ArrayList();

		    int selectedDbId;
		    try{
		    	selectedDbId = Integer.parseInt(selectedDbIdStr);
		    }catch(NumberFormatException e){
		    	
		    	int viewOrTableId = msgInfo.getViewOrTableId();
		    	if(Integer.parseInt(selectedQueryType) == Constants.SELECT_QUERY_TYPE_NUM) { //Select
					DataView view = dataAgent.getMetaViewByID(viewOrTableId, true);
					dbIdInDB =  view.getSourceDsId();
				}else {//Insert & update
					EntityDobj table = dataAgent.getMetaTableByID(viewOrTableId, true);
					dbIdInDB =  table.getDataSourceId();
				}
		    	selectedDbId = dbIdInDB;
		    }
    		
    		if(Integer.parseInt(selectedQueryType) == Constants.SELECT_QUERY_TYPE_NUM){//Select
				views = dataAgent.getAllMetaViews(selectedDbId, true);
				tablesOrViewsList = views;
			}else{//Update and Insert
				List tables = dataAgent.getAllMetaTables(selectedDbId, true);//Get all Tables for a DS
				tablesOrViewsList = tables;
			}
		} catch (MdnException e) {
			e.printStackTrace();
		}
		msgInfo = keepQueryInfo(request);
		if(timeout == null || timeout.equals("") || timeout.equals(Constants.UNDEFINED))
			msgInfo.setTimeout("");
		else
			msgInfo.setTimeout(timeout);
		
		// Format the data.
		return new XmlFormatter().fillCombo(file, msgInfo, databases, tablesOrViewsList, vtLable, emailAddresses, allSmpp);
  }

	private Element changedDataBaseItemForUserReply(HttpServletRequest request){
		//  Get the language file.
		String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
		int userReplyId = Integer.parseInt(request.getParameter("userReplyId"));
		String parentId = request.getParameter("parentId");
		String queryId = request.getParameter("queryId");
		String queryName = request.getParameter("queryName");
		String queryDesc = request.getParameter("queryDesc");
		
		String textMsgUr = request.getParameter("textMsgUr");
		String timeoutUr = request.getParameter("timeoutUr");
		
	    String selectedDbIdStr = request.getParameter("selected-db-id");
	    String selectedQueryType = request.getParameter("selected-q-type");
	    
	    String projectId = request.getParameter("projectId");
	    int intProjectId = 1;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e1) {
			intProjectId = 1;
		}	    	    
	    
	    //These data for caching user input data in the screen before saving
	    
	    List tablesOrViewsList = new ArrayList();
	    List<DataSourceDobj> databases = new ArrayList<DataSourceDobj>();
	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		//QueryDobj msgInfo = null;
	    UserReply userReplyMessage = null;
		String vtLable = "";
	    
		try {
			if(userReplyId == -1){}
				userReplyMessage = new UserReply();
			
			if(Integer.parseInt(selectedQueryType) == Constants.UPDATE_QUERY_TYPE_NUM || Integer.parseInt(selectedQueryType) == Constants.INSERT_QUERY_TYPE_NUM)//Update or insert
				vtLable = MessageConstants.getMessage(file, MessageConstants.SELECT_TABLE_LABEL);			
			else
				vtLable = MessageConstants.getMessage(file, MessageConstants.SELECT_VIEW_LABEL);
			
			databases = dataAgent.getAllDbConnections(intProjectId);

			//Get Selected database Id from DB
			int dbIdInDB;
			List<DataView> views;
    		tablesOrViewsList = new ArrayList();

		    int selectedDbId;
		    try{
		    	selectedDbId = Integer.parseInt(selectedDbIdStr);
		    }catch(NumberFormatException e){
		    	
		    	int viewOrTableId = userReplyMessage.getViewOrTableId();
		    	if(Integer.parseInt(selectedQueryType) == Constants.SELECT_QUERY_TYPE_NUM) { //Select
					DataView view = dataAgent.getMetaViewByID(viewOrTableId, true);
					dbIdInDB =  view.getSourceDsId();
				}else {//Insert & update
					EntityDobj table = dataAgent.getMetaTableByID(viewOrTableId, true);
					dbIdInDB =  table.getDataSourceId();
				}
		    	selectedDbId = dbIdInDB;
		    }
    		
    		if(Integer.parseInt(selectedQueryType) == Constants.SELECT_QUERY_TYPE_NUM){//Select
				views = dataAgent.getAllMetaViews(selectedDbId, true);
				tablesOrViewsList = views;
			}else{//Update and Insert
				List tables = dataAgent.getAllMetaTables(selectedDbId, true);//Get all Tables for a DS
				tablesOrViewsList = tables;
			}
		} catch (MdnException e) {
			e.printStackTrace();
		}
		
		String sorts = request.getParameter("sorts");
		String groupFieldName = request.getParameter("groupFieldName");
		String groupFieldId = request.getParameter("groupFieldId");	
		String queryType = request.getParameter("queryType");
		String viewID = request.getParameter("viewID");		
		
		try {
			String queryTypeStr = MessagingUtils.getQueryTypeStr(Integer.parseInt(queryType));
			userReplyMessage.setType(queryTypeStr);
		}catch(NumberFormatException e){
			userReplyMessage.setType("select");
		}

		try {
			userReplyMessage.setViewOrTableId(Integer.parseInt(viewID));
		}catch(NumberFormatException e){
			//System.out.println(" >>>>> Missing Table or view ID");
		}
		
		if(textMsgUr == null || textMsgUr.equals("") || textMsgUr.equals(Constants.UNDEFINED))
			userReplyMessage.setMsgText("");
		else
			userReplyMessage.setMsgText(textMsgUr.toLowerCase());
		
		if(timeoutUr == null || timeoutUr.equals("") || timeoutUr.equals(Constants.UNDEFINED))
			userReplyMessage.setTimeout("");
		else
			userReplyMessage.setTimeout(timeoutUr);
		
		return new XmlFormatter().changeCombo(file, userReplyMessage, databases, tablesOrViewsList, vtLable, queryId, queryName, queryDesc, parentId);
  }
	
	private Element testResult(HttpServletRequest request){
	    String queryID = request.getParameter("queryID");
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		QueryDobj query = null;
		RecordSet rs = null;
		try {
			query = dataAgent.getQueryByID(Integer.parseInt(queryID));
			
	        // get the view ds
	        DataViewDataSource dvds = MdnDataManager.getDataViewDS();
	        
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (MdnException e1) {
			e1.printStackTrace();
		}
	    return null;//new XmlFormatter().getQueryRecordSet(query, rs);
  }
	
	private Element showMsgResult(HttpServletRequest request){
		String sql = request.getParameter("sql");
		String dataSourceId = request.getParameter("connID");
		String viewID = request.getParameter("viewID");
		
		String queryType = request.getParameter("queryType");
		int queryTypeInt = Integer.parseInt(queryType);

		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		DataView view = null;
		RecordSet rs = null;
		String replyingMsg = "";
		
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
		
		try {
			view = dataAgent.getMetaViewByID(Integer.parseInt(viewID), true);
			
	        // get the view ds
	        DataViewDataSource dvds = MdnDataManager.getDataViewDS();
	        
    		int selectedDbId;
	    	selectedDbId = Integer.parseInt(dataSourceId);

	    	if(queryTypeInt == Constants.SELECT_QUERY_TYPE_NUM){//Select
		        rs = dvds.execDirectSQL(selectedDbId, sql, view);
		        
		        if( rs!= null )
		        {
		        	Vector rows = rs.getRows();
		        	Vector titles = new Vector();
		        
		        	if(rows != null && rows.size() > 0)
		        		titles = ((DataObject)rows.elementAt(0)).getEntity().getFields();
		        
		        	String titlesStringArray[] = new String[titles.size()];
		        
		        	int displayResult = rows.size();
		        
		        	for(int i = 0; rows != null && i < displayResult; i++)
		        	{
		        		// get the data object
		        		DataObject obj = (DataObject)rows.elementAt(i);
		            
		        		for(int col = 0; col < titlesStringArray.length; col++)
		        		{
		        			if (obj != null) {
		        				Vector fields = obj.getEntity().getFields();
		        				String fldName = ((DataViewField)fields.elementAt(col)).getName();
		        				String fldDisplayName = ((DataViewField)fields.elementAt(col)).getName();
		        				Object val = obj.getObjectValue(fldName);
	
		        				if(val == null)
		        					val = "";
		                    
		        				replyingMsg += fldDisplayName + ": " + val + "\n";
		        			}	   
		        		}
		        		replyingMsg += "-------------------------------"+ "\n";
		        	}
		        }else{
		        	replyingMsg = MessageConstants.getMessage(file, MessageConstants.NO_RESULT_TEST_RESPONSE);
		        }
	    	}
	        
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (MdnException e1) {
			e1.printStackTrace();
		} catch (DataSourceException e) {
			replyingMsg = "Failed";
			e.printStackTrace();
		}
		
		if(replyingMsg.length()<=0)
		{
			replyingMsg = MessageConstants.getMessage(file, MessageConstants.NO_RESULT_TEST_RESPONSE);
		}
		QueryDobj msgQ = keepQueryInfo(request);
		
		return new XmlFormatter().showMsgResult(file, replyingMsg,vtLable, msgQ, databases, tablesOrViewsList);
	}
	
	private QueryDobj keepQueryInfo(HttpServletRequest request){
		String msgId = request.getParameter("msgId");		
		String emailId = request.getParameter("emailId");
		String emailKeyword = request.getParameter("emailKeyword");
		String smsKeyword = request.getParameter("smsKeyword");							
		String imKeyword = request.getParameter("imKeyword");
		String emailDisplayResult = request.getParameter("emailDisplayResult");
		String mobileStatus = request.getParameter("mobileStatus");
		String mobileDisplayResult = request.getParameter("mobileDisplayResult");
		String imStatus = request.getParameter("imStatus");
		String imDisplayResult = request.getParameter("imDisplayResult");
		String queryName = request.getParameter("queryName");
		String queryDescription = request.getParameter("queryDescription");
		String queryType = request.getParameter("queryType");
		String viewID = request.getParameter("viewID");		
		
		QueryDobj msgQ = new QueryDobj();
		msgQ.setId(msgId);
		//Keep data
		if(queryName == null || queryName.equals("") || queryName.equals(Constants.UNDEFINED))
			msgQ.setName("");
		else
			msgQ.setName(queryName);
		
		if(queryDescription == null || queryDescription.equals("") || queryDescription.equals(Constants.UNDEFINED))
			msgQ.setDescription("");
		else
			msgQ.setDescription(queryDescription);

		try {
			String queryTypeStr = MessagingUtils.getQueryTypeStr(Integer.parseInt(queryType));
			msgQ.setType(queryTypeStr);
		}catch(NumberFormatException e){
			msgQ.setType("select");
		}

		try {
			msgQ.setViewOrTableId(Integer.parseInt(viewID));
		}catch(NumberFormatException e){
			//System.out.println(" >>>>> Missing Table or view ID");
		}
		
		try {
			msgQ.setEmailDisplayResult(Integer.valueOf(emailDisplayResult));
		}catch(NumberFormatException e){
			msgQ.setEmailDisplayResult(1);
		}
		try{
			msgQ.setMobileDisplayResult(Integer.valueOf(mobileDisplayResult));
		}catch(NumberFormatException e){
			msgQ.setMobileDisplayResult(1);
		}
		try{
			msgQ.setImDisplayResult(Integer.valueOf(imDisplayResult));
		}catch(NumberFormatException e){
			msgQ.setImDisplayResult(1);
		}
		
		try {
			msgQ.setEmailAddressId(Integer.valueOf(emailId));
		}catch(NumberFormatException e){
			msgQ.setEmailDisplayResult(1);//No Email
		}

		try {
			msgQ.setMobileStatus(Integer.valueOf(mobileStatus));
		}catch(NumberFormatException e){
			msgQ.setMobileStatus(1);//No SMS
		}

		try {
			msgQ.setImStatus(Integer.valueOf(imStatus));
		}catch(NumberFormatException e){
			msgQ.setImStatus(1);//No IM
		}
		if(emailKeyword == null || emailKeyword.equals("") || emailKeyword.equals(Constants.UNDEFINED))
			msgQ.setEmailKeyword("");
		else
			msgQ.setEmailKeyword(emailKeyword.toLowerCase());
		
		if(smsKeyword == null || smsKeyword.equals("") || smsKeyword.equals(Constants.UNDEFINED))
			msgQ.setSmsKeyword("");
		else
			msgQ.setSmsKeyword(smsKeyword.toLowerCase());

		if(imKeyword == null || imKeyword.equals("") || imKeyword.equals(Constants.UNDEFINED))
			msgQ.setImKeyword("");
		else
			msgQ.setImKeyword(imKeyword.toLowerCase());
		
		return msgQ;
		
	}
	
	private Element getDefaultTextMsgResult(HttpServletRequest request){
		String ds = request.getParameter("ds");
		
		if(ds.equals("2")){//For web service query
/*			try{
				String wsdlUrl = request.getParameter("WSDLUrl");
				String selectedService = request.getParameter("service");
				String selectedPort = request.getParameter("port");
				String selectedOperation = request.getParameter("operation");
				if(paramHelperMap.containsKey(wsdlUrl)){
					paramHelper = (ParamHelper)paramHelperMap.get(wsdlUrl);
				}else{
					paramHelper = new ParamHelper(wsdlUrl);
					paramHelper.setCurrentService(selectedService);
					paramHelper.setCurrentPort(selectedPort);
					paramHelper.setCurrentOperation(selectedOperation);
					paramHelper.createParamList();
					paramHelperMap.put(wsdlUrl, paramHelper);
				}
				List<ParamListItem> reqParamList = paramHelper.getParamList();
				for (ParamListItem parameter : reqParamList){
					parameter.getLabel();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}									*/
		}else{
			String sql = request.getParameter("sql");
			String dataSourceId = request.getParameter("connID");
			String viewID = request.getParameter("viewID");
			IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		    String projectId = request.getParameter("projectId");
		    int intProjectId = 1;
			try {
				intProjectId = Integer.parseInt(projectId);
			} catch (NumberFormatException e1) {
				intProjectId = 1;
			}	    
			DataView view = null;
			RecordSet rs = null;
			String replyingMsg = "";
			
			String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
			
			try {
				if(sql == null || sql.equals("") || sql.equals(Constants.UNDEFINED) || 
						dataSourceId == null || dataSourceId.equals("") || dataSourceId.equals(Constants.UNDEFINED) || 
						viewID == null || viewID.equals("") || viewID.equals(Constants.UNDEFINED)){
					replyingMsg = MessageConstants.getMessage(file, MessageConstants.NO_RESULT_TEST_RESPONSE);
				}else{
					view = dataAgent.getMetaViewByID(Integer.parseInt(viewID), true);
			        // get the view ds
			        DataViewDataSource dvds = MdnDataManager.getDataViewDS();
			        
		    		int selectedDbId;
			    	try{
			    		selectedDbId = Integer.parseInt(dataSourceId);
			    	}catch(NumberFormatException e){
			    		String selectedDbName = dataSourceId;
			    		selectedDbId = (dataAgent.getDbConnectionByName(intProjectId, selectedDbName)).getId();
			    	}
			        
			        rs = dvds.execDirectSQL(selectedDbId, sql, view);
			        
			        if( rs!= null )
			        {
			        	Vector rows = rs.getRows();
			        	Vector titles = new Vector();
			        
			        	if(rows != null && rows.size() > 0)
			        		titles = ((DataObject)rows.elementAt(0)).getEntity().getFields();
			        	
			        		for(int col = 0; col < titles.size(); col++)
			        		{
			        			String filedName = titles.get(col).toString();
			        			replyingMsg += "<b> Title : </b> %" + filedName + "% \n";
			        		}
			        }
				}
				if(replyingMsg.length()<=0)
				{
					replyingMsg = MessageConstants.getMessage(file, MessageConstants.NO_RESULT_TEST_RESPONSE);
				}
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
			} catch (MdnException e1) {
				e1.printStackTrace();
			} catch (DataSourceException e) {
				e.printStackTrace();
			}
		}
		return new Element("root");//new XmlFormatter().showMsgResult(file, replyingMsg, msgId);
	}
	
	private Element saveMsgResponse(HttpServletRequest request){
		String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
		
		String restext = request.getParameter("restext");
		String queryId = request.getParameter("queryID");
	    String projectId = request.getParameter("projectId");
	    int intProjectId = 1;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e1) {
			intProjectId = 1;
		}	    		
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		try {
			QueryDobj queryMsg = dataAgent.getQueryByID(Integer.parseInt(queryId));
			queryMsg.setState(DataObject.IN_DB);
			queryMsg.setDelStatus(Integer.parseInt(Constants.MARKED_AS_NOT_DELETED));
			queryMsg.setResponse(restext);
			queryMsg.save();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (MdnException e) {
			e.printStackTrace();
		} catch (DataSourceException e) {
			e.printStackTrace();
		}

		String viewID = request.getParameter("viewID");		
		String queryType = request.getParameter("queryType");
		int queryTypeInt = Integer.parseInt(queryType);
		
		String vtLable;
		if(queryTypeInt == Constants.SELECT_QUERY_TYPE_NUM)//Select
			vtLable = MessageConstants.getMessage(file, MessageConstants.SELECT_VIEW_LABEL);			
		else//Update or Insert
			vtLable = MessageConstants.getMessage(file, MessageConstants.SELECT_TABLE_LABEL);
		
		initAllDatabases(intProjectId);
		QueryDobj msgQ = keepQueryInfo(request);
		msgQ.setId(queryId);

		int dbId = getDbIdByVTId(Integer.parseInt(viewID), queryType);
		initTablesOrViewsList(queryType, dbId);

		return new XmlFormatter().showMsgResponse(file, restext, vtLable, msgQ, databases, tablesOrViewsList);
	}

	private Element showMsgResponse(HttpServletRequest request){
		String sql = request.getParameter("sql");
		String dataSourceId = request.getParameter("connID");
		String viewID = request.getParameter("viewID");
		String msgId = request.getParameter("msgId");
		
		String queryType = request.getParameter("queryType");
		int queryTypeInt = Integer.parseInt(queryType);
	    String projectId = request.getParameter("projectId");
	    //Get project ID
	    int intProjectId = 1;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e1) {
			intProjectId = 1;
		}	    		
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		String responseTxt ="";//"Not defined any response Message for this query";
		try {
			QueryDobj queryMsg = dataAgent.getQueryByID(Integer.parseInt(msgId));
			String resTxt = queryMsg.getResponse();
			if(resTxt!=null && !resTxt.equals(""))
				responseTxt = resTxt;
			else if(sql != null && !sql.equals("") && !sql.equals(Constants.UNDEFINED) && 
					dataSourceId != null && !dataSourceId.equals("") && !dataSourceId.equals(Constants.UNDEFINED) && 
					viewID != null && !viewID.equals("") && !viewID.equals(Constants.UNDEFINED)){
				
					DataView view = dataAgent.getMetaViewByID(Integer.parseInt(viewID), true);
			        DataViewDataSource dvds = MdnDataManager.getDataViewDS();
			        
		    		int selectedDbId;
			    	try{
			    		selectedDbId = Integer.parseInt(dataSourceId);
			    	}catch(NumberFormatException e){
			    		String selectedDbName = dataSourceId;
			    		selectedDbId = (dataAgent.getDbConnectionByName(intProjectId, selectedDbName)).getId();
			    	}
			    	
			    	if(queryTypeInt == Constants.SELECT_QUERY_TYPE_NUM){//Select
				    	RecordSet rs = dvds.execDirectSQL(selectedDbId, sql, view);
				    	String replyingMsg = "";
				        if( rs!= null )
				        {
				        	Vector rows = rs.getRows();
				        	Vector titles = new Vector();
				        
				        	if(rows != null && rows.size() > 0)
				        		titles = ((DataObject)rows.elementAt(0)).getEntity().getFields();
				        	
				        		for(int col = 0; col < titles.size(); col++)
				        		{
				        			String filedName = titles.get(col).toString();
				        			DataViewField fieldObj = (DataViewField)titles.get(col);
				        			replyingMsg += fieldObj.getDisplayName() +" : %" + filedName + "% \n";
				        			
				        		}
				        		responseTxt = replyingMsg;
				        }else{
				        	responseTxt = "";//"There are no results for this query.";
						}
			    	}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (MdnException e) {
			e.printStackTrace();
		} catch (DataSourceException e) {
			responseTxt = "Failed";
			e.printStackTrace();
		}
		
//		if(responseTxt.equals(""))
//			responseTxt = "There are no results for this query.";
		
		String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);	
		
		//to keep data for showing after close the windows	
		String vtLable;
		if(queryTypeInt == Constants.SELECT_QUERY_TYPE_NUM)//Select
			vtLable = MessageConstants.getMessage(file, MessageConstants.SELECT_VIEW_LABEL);			
		else//Update or Insert
			vtLable = MessageConstants.getMessage(file, MessageConstants.SELECT_TABLE_LABEL);
		
		initAllDatabases(intProjectId);
		QueryDobj msgQ = keepQueryInfo(request);

		int dbId = getDbIdByVTId(Integer.parseInt(viewID), queryType);
		initTablesOrViewsList(queryType, dbId);

		return new XmlFormatter().showMsgResponse(file, responseTxt, vtLable, msgQ, databases, tablesOrViewsList);
	}

	private List<String> getBaudratesList(){
		List<String> baundrateList = new ArrayList();
		
		baundrateList.add("110");
		baundrateList.add("300");
		baundrateList.add("1200");
		baundrateList.add("2400");
		baundrateList.add("4800");
		baundrateList.add("9600");
		baundrateList.add("19200");
		baundrateList.add("38400");
		baundrateList.add("57600");
		baundrateList.add("115200");
		baundrateList.add("230400");
		baundrateList.add("460800");
		baundrateList.add("921600");
		
		return baundrateList;
	}
	private Element showSms(HttpServletRequest request){
/*		String driverName = "com.sun.comm.Win32Driver";
		CommDriver commDriver;//RXTXCommDriver
		try {
			commDriver = (CommDriver)Class.forName(driverName).newInstance();
			commDriver.initialize();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}		
		Enumeration commList = CommPortIdentifier.getPortIdentifiers();
		while (commList.hasMoreElements()) {
			CommPortIdentifier element = (CommPortIdentifier) commList.nextElement();
			System.out.println("******************************* comm list name = " + element);
			System.out.println("******************************* comm list name = " + element.getName());
		}		
*/		
		MdnSmsSetting mdnInfo = new MdnSmsSetting();
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		String smsBtnTxt = "Connect";
		
		try {
			mdnInfo = dataAgent.getSmsSetting();
			HttpSession session = request.getSession();
			if(session.getAttribute("SmsConnects")!= null)
				smsBtnTxt = "Disconnect";
			else
				smsBtnTxt = "Connect";
		} catch (MdnException e) {
			e.printStackTrace();
		}

		return new XmlFormatter().showSmsInfo(mdnInfo, smsBtnTxt, getBaudratesList(), "showSms", "--");
	}
	
	private Element connDisConnSms(HttpServletRequest request){
		String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
		
		String number = request.getParameter("number");
		String comm = request.getParameter("comm");
		String baudrate = request.getParameter("baudrate");
		String modemMan = request.getParameter("modemMan");
		String modemModel = request.getParameter("modemModel");
		
		MdnSmsSetting mdnSmsInfo = new MdnSmsSetting();
		String smsBtnTxt = "Connect";
		String result = MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL);
		
		HttpSession session = request.getSession();
		CService service;
		MdnSmsServer smsServer = new MdnSmsServer();
		if(number == null || number.equals("") || number.equals("-") || number.equals(Constants.UNDEFINED))
			result = "Missing MDN Mobile Number";
		else 
			mdnSmsInfo.setNumber(number);
			
		if(comm == null || comm.equals("") || comm.equals("-") || comm.equals(Constants.UNDEFINED))
			result = "Missing Comm port used";
		else
			mdnSmsInfo.setComm(comm);
		
		if(baudrate == null || baudrate.equals("") || baudrate.equals("-") || baudrate.equals(Constants.UNDEFINED))
			result = "Missing baudrate";
		else
			mdnSmsInfo.setBaudrate(baudrate);
		
		if(modemMan == null || modemMan.equals("") || modemMan.equals("-") || modemMan.equals(Constants.UNDEFINED))
			result = "Missing Modem Manufacturer";
		else
			mdnSmsInfo.setModemManufacturer(modemMan);
		
		if(modemModel == null || modemModel.equals("") || modemModel.equals("-") || modemModel.equals(Constants.UNDEFINED))
			result = "Missing Modem Model";
		else
			mdnSmsInfo.setModemModel(modemModel);
			

		if(session.getAttribute("SmsConnects")!= null){//Do Disconnect
			service = (CService)session.getAttribute("SmsConnects");
			smsServer.disconnect(service, session);
			smsBtnTxt = "Connect";
		}else{//Do Connect
			if(result.equals("OK")){
				service = new CService(comm, Integer.parseInt(baudrate), modemMan, modemModel);/*such as ("COM1", 9600, "Wavecom", "M1306B") */
				smsBtnTxt = "Disconnect";
			}
		}
		return new XmlFormatter().showSmsInfo(mdnSmsInfo, smsBtnTxt, getBaudratesList(), "connDisConnSms", result);
	}

	private Element saveSmsInfo(HttpServletRequest request){
		String number = request.getParameter("number");
		String comm = request.getParameter("comm");
		String baudrate = request.getParameter("baudrate");
		String modemMan = request.getParameter("modemMan");
		String modemModel = request.getParameter("modemModel");
		
		String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    String result = MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL_SMS);

	    //String result = "OK";
		
		MdnSmsSetting smsSetting = new MdnSmsSetting();
		try {
			IDataAgent dataAgent = DataAgentFactory.getDataInterface();
			MdnSmsSetting  existSmsSetting = dataAgent.getSmsSetting();
			if(existSmsSetting != null)
				smsSetting = existSmsSetting;
			
			if(number!= null && !number.equals("") ){
				if(!number.contains("+"))
					number = "+" + number.trim();
				
				MdnSmpp smsGateway = dataAgent.getSmsGatewayByNumber(number);
				if(smsGateway != null)
					result = MessageConstants.getMessage(file, MessageConstants.DUPLICATE_GSM_NUM);
			}
			
			if(number == null || number.equals("") || number.equals("-") || number.equals(Constants.UNDEFINED))
				result = MessageConstants.getMessage(file, MessageConstants.MISSING_GSM_NUM);
			else 
				smsSetting.setNumber(number);
			
			if(comm != null && comm.trim().contains("comm"))
				comm = comm.replace("comm", "COM");
			
			if(comm != null && comm.trim().contains("COMM"))
				comm = comm.replace("COMM", "COM");
			
			if(comm == null || comm.equals("") || comm.equals("-") || comm.equals(Constants.UNDEFINED))
				result = MessageConstants.getMessage(file, MessageConstants.MISSING_COMM);
			else if(comm.trim().contains(" "))
				result = MessageConstants.getMessage(file, MessageConstants.INVALID_COMM);
			else{
				comm = comm.toUpperCase();
				smsSetting.setComm(comm.trim());
			}
			
			if(baudrate == null || baudrate.equals("") || baudrate.equals("-") || baudrate.equals(Constants.UNDEFINED))
				result = MessageConstants.getMessage(file, MessageConstants.MISSING_BAUDRATE);
			else
				smsSetting.setBaudrate(baudrate);
			
//			if(modemMan == null || modemMan.equals("") || modemMan.equals("-") || modemMan.equals("undefined"))
//				result = "Missing Modem Manufacturer";
//			else
//				smsSetting.setModemManufacturer(modemMan);
//			
//			if(modemModel == null || modemModel.equals("") || modemModel.equals("-") || modemModel.equals("undefined"))
//				result = "Missing Modem Model";
//			else
//				smsSetting.setModemModel(modemModel);
			
			if(result.equals(MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL_SMS))){
				smsSetting.setNumber(number);
				dataAgent.saveSmsSetting(smsSetting);
			}
		} catch (MdnException e) {
			e.printStackTrace();
		}
		
	    boolean isSuccess = false;
	    if(result.equalsIgnoreCase(MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL_SMS)))
	    	isSuccess = true;		
		
		return new XmlFormatter().showSmsInfo(smsSetting, "--", getBaudratesList(), "saveSmsInfo", result);
	}
	
	private Element displayQueryProperties(HttpServletRequest request){
	    String dsIDStr = request.getParameter("dsStatus");
	    int ds = 1;
		try {
			ds = Integer.parseInt(dsIDStr);
		} catch (NumberFormatException e1) {
			ds = 1;
		}	    		
		
		//  Get the language file.
		String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
		int id = Integer.parseInt(request.getParameter("msgId"));

		String projectId = request.getParameter("projectId");
	    int intProjectId = 1;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e1) {
			intProjectId = 1;
		}	
		
		String viewOrTableName = "";
	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();

	    List<MdnEmailSetting> emailAddresses = new ArrayList<MdnEmailSetting>();
    	List<MdnSmpp> allSmpp = new ArrayList<MdnSmpp>();			
    
	    String responseTxt="";	    
	    String defaultResponse="";
	    
	    int wsOperationId = 0;
	    String wsOperationName = " ";
	    
		try {
			msgInfo = dataAgent.getQueryPropertiesByID(id);

			//Get Selected database Id from DB
			int viewOrTableId = msgInfo.getViewOrTableId();
			int dbIdInDB = msgInfo.getDatabaseId();
			String querytypeDb = msgInfo.getType();
			ds = msgInfo.getDatasourceStatus();
			
			if(ds == 1){//If Datasource is database(Not Webservice)
				int queryType = MessagingUtils.getQueryTypeNumber(querytypeDb);
				if(queryType == Constants.SELECT_QUERY_TYPE_NUM){//Select
					vtLable = MessageConstants.getMessage(file, MessageConstants.SELECT_VIEW_LABEL);
					DataView view = dataAgent.getMetaViewByID(viewOrTableId, true);
					viewOrTableName = view.getName();
				}else{//Update or Insert
					vtLable = MessageConstants.getMessage(file, MessageConstants.SELECT_TABLE_LABEL);
					EntityDobj table = dataAgent.getMetaTableByID(viewOrTableId, true);
					viewOrTableName = table.getName();
				}
		
				DataSourceDobj	db = dataAgent.getDbConnectionByID(intProjectId, dbIdInDB);
				dbName = db.getName();
				//if(id != -1){
					int viewID = msgInfo.getViewOrTableId();
					int queryTypeNum = MessagingUtils.getQueryTypeNumber(msgInfo.getType());
				//}
					
				defaultResponse = MessagingUtils.getDefaultResponse(String.valueOf(queryTypeNum), msgInfo.getCriteriaString(), String.valueOf(msgInfo.getDatabaseId()), String.valueOf(msgInfo.getViewOrTableId()), request,ds, null);	
			}else{
				wsOperationId = msgInfo.getWebServiceId();
				WebServiceOperationDobj selectedWebService = dataAgent.getWebServiceOperationByID(wsOperationId);
				wsOperationName = selectedWebService.getName();
				String wsdlUrl = selectedWebService.getUrl();
				//TODO:defaultResponse = MessagingUtils.getDefaultResponse(null, null, null, null, request, ds, wsOperationId, wsOperationName, wsOperationName);//TODO
			}
			
			// getMessagingInfo for Message Builder tab >>>>>
		    if(id != -1){
				//responseTxt ="Not defined any response Message for this query";
				msgInfo = dataAgent.getMessagingInfoByID(id);
				emailAddresses = dataAgent.getMdnEmailAddresses();
				allSmpp = dataAgent.getAllSmppGateway();
				
				//Manage Response format
				String resTxt = msgInfo.getResponse();
				//int viewID = msgInfo.getViewOrTableId();
				//int queryTypeNum = MessagingUtils.getQueryTypeNumber(msgInfo.getType());
				msgInfo.getSmsKeyword();
				
				//defaultResponse = MessagingUtils.getDefaultResponse(String.valueOf(queryTypeNum), msgInfo.getCriteriaString(), String.valueOf(msgInfo.getDatabaseId()), String.valueOf(msgInfo.getViewOrTableId()), request, ds);				
				
				if(resTxt!=null && !resTxt.equals(""))
					responseTxt = resTxt;
//				else{
//					if(ds == 1){
//					}
//				}
		    }else{
		    	System.out.println("Never reach here!");
		    	msgInfo = new QueryDobj();
		    	msgInfo.setId(id);
				emailAddresses = dataAgent.getMdnEmailAddresses();	
				responseTxt = "";
		    }		
		} catch (MdnException e) {
			e.printStackTrace();
		}
		// Format the data.
		return new XmlFormatter().displayQueryProperties(file, dbName, msgInfo, vtLable, viewOrTableName, emailAddresses, allSmpp, responseTxt, defaultResponse, wsOperationName, wsOperationId);
   }

	private Element getDefaultResponseForEditWS(HttpServletRequest request){
	    String dsIDStr = request.getParameter("dsStatus");
		//  Get the language file.
		String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
		int id = Integer.parseInt(request.getParameter("msgId"));

	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();

	    String replyingMsg="";
	    int wsOperationId = 0;
		try{	
			msgInfo = dataAgent.getQueryPropertiesByID(id);
			wsOperationId = msgInfo.getWebServiceId();
			WebServiceOperationDobj selectedWebService = dataAgent.getWebServiceOperationByID(wsOperationId);
			replyingMsg = MessagingUtils.getDefaultResponse(null, null, null, null, request, 2, selectedWebService);
		} catch (MdnException e) {
			e.printStackTrace();
		}		
		return new XmlFormatter().getDefaultTextMsgForResponseXML(replyingMsg);
	}
	
	private Element displayQueryBuilderInfo(HttpServletRequest request){
		//  Get the language file.
		String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
		int id = Integer.parseInt(request.getParameter("msgId"));

	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		try {
			msgInfo = dataAgent.getQueryBuilderInfoByID(id);
		} catch (MdnException e) {
			e.printStackTrace();
		}

		return new XmlFormatter().displayQueryBuilderXML(file, msgInfo);
   }

	private String sqlParser(HttpServletRequest request, DataView view, ArrayList<String> userInputsList){
		String ids = request.getParameter("ids");
		String rows = request.getParameter("rows");		
		String types = request.getParameter("types");
		String useds = request.getParameter("useds");
		String indents = request.getParameter("indents");
		String valueOrConditions = request.getParameter("valueOrConditions");
		String numbers = request.getParameter("numbers");
		String parents = request.getParameter("parents");
		String fields = request.getParameter("fields");
		String compIDs = request.getParameter("compIDs");
		String comparisons = request.getParameter("comparisons");
		String values = request.getParameter("values");
		String connections = request.getParameter("connections");
		String value2s = request.getParameter("value2s");
		String groupings = request.getParameter("groupings");
		String sorts = request.getParameter("sorts");		
		String groupFieldId = request.getParameter("groupFieldId");	
		String userInputSeqs = request.getParameter("userInputSeqs");
		String userInputSeqs2 = request.getParameter("userInputSeqs2");
		
        String[] rowsList = rows.split(",");
        String[] typesList = types.split(",");
        String[] usedsList = useds.split(",");
        String[] indentsList = indents.split(",");
        String[] valueOrConditionsList = valueOrConditions.split(",");        
        
        String[] numbersList = null;
        if (numbers != null)
        	numbersList = numbers.split(",");

        String[] parentsList = null;
        if (parents != null)
        	parentsList = parents.split(",");                
        
        String[] fieldsList = fields.split(",");
        String[] compIDsList = compIDs.split(",");
        String[] comparisonsList = comparisons.split(",");
        String[] valuesList = values.split(",");
        String[] connectionsList = connections.split(",");
        String[] value2sList = value2s.split(",");
        String[] groupingsList = groupings.split(",");
        String[] userInputSeqsList = userInputSeqs.split(",");
        String[] userInputSeqs2List = userInputSeqs2.split(",");
		int index = 1;
		
		boolean empty = false;
		if (rowsList.length == 1){
			String numberList =numbersList[0]; 
			if (numberList.equals("") || numberList.equals(Constants.UNDEFINED)){
				empty = true;
			}
		}
		String sql = "";
		String fromTableName = null;
		ArrayList<String> fromTableNames = new ArrayList<String>();
		Vector<DataViewField> viewFields = (Vector<DataViewField>)view.getFields();
		
		if (!empty){
            for (int i = 0; i< rowsList.length; i++){
            	String row = rowsList[i];
            	String type = typesList[i];
            	String used = usedsList[i];
            	String indent = indentsList[i];
            	String valueOrCondition = valueOrConditionsList[i];
            	
            	String parent = null;
            	if (parentsList != null && parentsList.length == rowsList.length)
            		parent = parentsList[i];
            	
            	String number = null;
            	if (numbersList != null && numbersList.length == rowsList.length)
            		number = numbersList[i];                	
            	
            	String field = fieldsList[i];
            	String compID = compIDsList[i];
            	String comparison = comparisonsList[i];
            	String value = valuesList[i];               	
            	String connection = connectionsList[i];
            	String value2 = value2sList[i];               	
            	String grouping = groupingsList[0];
            	
            	String userInputSeq = null;
            	if (userInputSeqsList != null && userInputSeqsList.length > i)//if (userInputSeqsList != null && userInputSeqsList.length == rowsList.length)
            		userInputSeq = userInputSeqsList[i];
            	
            	String userInputSeq2 = null;
            	if (userInputSeqs2List != null && userInputSeqs2List.length > i)//if (userInputSeqs2List != null && userInputSeqs2List.length == rowsList.length)
            		userInputSeq2 = userInputSeqs2List[i];
            	
            	// create a Query Criteria object
            	QueryCriteriaDobj queryCriteriaObj = new QueryCriteriaDobj();

            	queryCriteriaObj.setValueOrCondition(valueOrCondition);
            	int rowInt;
				try {
					rowInt = Integer.parseInt(row);
				} catch (NumberFormatException e) {
					rowInt = index;
				}
                queryCriteriaObj.setRowNo(rowInt);
                queryCriteriaObj.setType(type);
                queryCriteriaObj.setUsed(Boolean.valueOf(used).booleanValue()? 1: 0);
                queryCriteriaObj.setIndent(Integer.parseInt(indent));
                queryCriteriaObj.setParent(parent == null ? "" : parent);
                queryCriteriaObj.setNumber(number == null ? 0 : Integer.parseInt(number));
                queryCriteriaObj.setName(field);
                queryCriteriaObj.setCompId(Integer.parseInt(compID));
                queryCriteriaObj.setComparison(comparison);
                queryCriteriaObj.setValue(value);
                queryCriteriaObj.setConnection(connection);
                queryCriteriaObj.setValue2(value2);
                queryCriteriaObj.setGrouping(grouping);
            	queryCriteriaObj.setValueUserInputSeq(userInputSeq);
            	queryCriteriaObj.setValue2UserInputSeq(userInputSeq2);
                
				if(userInputsList != null && !userInputsList.isEmpty())
				{
					String condition = "AND"; 
					if ( grouping.equalsIgnoreCase("all") )
						condition = "AND" ;
					else if (grouping.equalsIgnoreCase("any"))
						condition = "OR";						
	
					boolean usedBoolean = Boolean.parseBoolean(used); 
					if (!usedBoolean){
						continue;
					} else {
						if (!sql.equals("")){
							sql = sql + condition + " ";
						}
					}
	
					String wholeField = queryCriteriaObj.getName();
					int currPos = wholeField.indexOf(".", 0);
	   				if (currPos > 0){
	   					fromTableName = wholeField.substring(0, currPos);
	   					boolean included = fromTableNames.contains(fromTableName);
	   					if (!included){
	   						fromTableNames.add(fromTableName);
	   					}
	   				}				
					
					//Get Field Type
					int fieldType = Field.FT_STRING;
					for (DataViewField currField : viewFields){
						String currWholeField = currField.getSourceEntity() + "." + currField.getSourceField();
						if (currWholeField.equalsIgnoreCase(wholeField)){
							fieldType = currField.getType();
						}
					}

					value = queryCriteriaObj.getValue();
					value2 = queryCriteriaObj.getValue2();
					if (value.equals("_____")){
						value = "";
					}
					if (value2.equals("_____")){
						value2 = "";
					}
					
					//set value from user input
					if (value.equalsIgnoreCase(queryCriteriaObj.VALUE_USERINPUT) && userInputsList.size()>0 ){
						try{
							value = userInputsList.get(Integer.parseInt(queryCriteriaObj.getValueUserInputSeq())-1);
						}catch (Exception e) {
							System.out.println("USERINPUT ERROR : UserInput is wrong ");
						}
					}
					
					//set value2 if value2 is userinput also
					if (value2.equalsIgnoreCase(queryCriteriaObj.VALUE_USERINPUT) && userInputsList.size()>0 ){
						try{
							value2 = userInputsList.get(Integer.parseInt(queryCriteriaObj.getValue2UserInputSeq())-1);
						}catch (Exception e) {
							System.out.println("USERINPUT ERROR : UserInput is wrong ");
						}
					}
					
					String thisRow = QueryDobj.getSQLCondition(queryCriteriaObj, userInputsList, fieldType, value, value2);
					sql = sql + thisRow + " ";
					
					// Need to check whether we deal with the 'grouping' value of NONE.
					if (grouping.equals("none") || grouping.equals("not all")){
						sql = "NOT (" + sql + ")";
					}            	
	            	///////Criteria pars end
                index++;
				}
            }	
		}
            	///////Group & Sort Pars Start
				String selectFromSql = "";
				for (int m = 0; m < fromTableNames.size(); m++){
	   				String currTableName = fromTableNames.get(m);
	   				selectFromSql = selectFromSql + currTableName + ",";
	   			}
				selectFromSql = selectFromSql.substring(0, selectFromSql.length()-1);
				// Add the WHERE clause.
				if (sql.length() > 1){
					sql = "SELECT * FROM " + selectFromSql + " WHERE " + sql;
				}
				else{
					sql = "SELECT * FROM " + selectFromSql;
				}
				if(groupFieldId != null && !groupFieldId.equals("") && !groupFieldId.equals(Constants.UNDEFINED)){
				int groupFieldIdInt = Integer.parseInt(groupFieldId);
					if (groupFieldIdInt != 0 && groupFieldIdInt != -1){
						String groupFieldName = "";
						for (DataViewField currField : viewFields){
							if (currField.getId() == groupFieldIdInt){
								groupFieldName = currField.getSourceEntity() +"." +currField.getSourceField();
							}
						}				
						sql += " GROUP BY " + groupFieldName;
					}
				}
				if (sorts != null && !sorts.equals("")){
					sql += " ORDER BY " + sorts;
				}
			 
			///////Group & Sort Pars End
		return sql;
	}
	
	private Element testMessaginResultXML(HttpServletRequest request){
		//  Get the language file.
		String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
		int id = Integer.parseInt(request.getParameter("msgId"));
	    String smsKeyword = request.getParameter("smsKeyword");
	    String imKeyword = request.getParameter("imKeyword");
	    String emailKeyword = request.getParameter("emailKeyword");
	    String emailDisplayResult = request.getParameter("emailDisplayResult");	    
	    String mobileDisplayResult = request.getParameter("mobileDisplayResult");
	    String imDisplayResult = request.getParameter("imDisplayResult");	
	    String responseFormat = request.getParameter("responseFormat");
	    String sorts = request.getParameter("sorts");
	    String groupFieldId = request.getParameter("groupFieldId");
		String queryType = request.getParameter("queryType");
		String sql = request.getParameter("sql");
		String mobileStatus = request.getParameter("mobileStatus");		
	    int messagingType = Integer.parseInt(request.getParameter("messagingType"));
	    String enterFormatMessage = request.getParameter("enterFormatMessage");
		
		
		//startttttttttttttttttttttttttttttttttttttttttttttttttttttttt
		//>>>params
		String ds = request.getParameter("dsStatus");
		String wsdlUrl = request.getParameter("WSDLUrl");
		String selectedOperation = request.getParameter("operation");
		String selectedService = request.getParameter("service");
		String selectedPort = request.getParameter("port");
		//String userInputTestString = request.getParameter("uiStr");
		
		String rowsQ = request.getParameter("rows");		
		String numbers = request.getParameter("numbers");
		String fields = request.getParameter("fields");
		String values = request.getParameter("values");
        String[] rowsList = rowsQ.split(",");
        String[] numbersList = null;
        if (numbers != null)
        	numbersList = numbers.split(",");

        String[] fieldsList = fields.split(",");
        String[] valuesList = values.split(",");
		boolean empty = false;
		//String result = null;
		if (rowsList.length == 1){
			String numberList =numbersList[0]; 
			if (numberList.equals("") || numberList.equals("undefined")){
				empty = true;
			}
		}		
		//<<<params
//		String responseFormat = "";				
//		ArrayList<String> userInputList = MessagingUtils.getUserInputForNoKeywordQuery(separator, userInputTestString);		
		//endddddddddddddddddddddddddddddddddddddddddddddddddddddddddd
		
		//___________________________________________________________________________

	    String projectId = request.getParameter("projectId");
	    int intProjectId = 1;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e1) {
			intProjectId = 1;
		}	    
	    
		String result = MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL);
		String replyContext = "";

		//String sql;
		int viewID = 0;
		List<MdnEmailSetting> emailAddresses = new ArrayList(); 
	  	List<MdnSmpp> allSmpp = new ArrayList<MdnSmpp>();		
		
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		DataView view;
		try {
			String separator = " ";
			MdnMessageSeparator mdnMsgSep = dataAgent.getMessageSeparator();
			if(mdnMsgSep != null){
				separator = mdnMsgSep.getConditionSeperator();
			}
			
			emailAddresses = dataAgent.getMdnEmailAddresses();
			allSmpp = dataAgent.getAllSmppGateway();
			
		    if(id != -1){
		    	msgInfo = (QueryDobj) dataAgent.getQueryByID(id);
				//sql = msgInfo.getCriteriaString();
				viewID = msgInfo.getViewOrTableId();
				queryType = msgInfo.getType();
				if(sql.equals(""))
					sql = msgInfo.getCriteriaString();
		    }else{
		    	if(ds.equals(Constants.DS_DB_STATUS)){
			    	String viewIdStr = request.getParameter("viewID");
			    	if(Integer.parseInt(queryType) != Constants.SELECT_QUERY_TYPE_NUM)
			    		result = MessageConstants.getMessage(file, MessageConstants.NO_SELECT_QUERY);//result = "Query type isn't Select";
			    	else if(viewIdStr == null || viewIdStr.equals("") || viewIdStr.equals(Constants.UNDEFINED))
			    		result = MessageConstants.getMessage(file, MessageConstants.MISSING_VIEW);
			    	else
			    		viewID = Integer.parseInt(viewIdStr);
			    	
			    	msgInfo = new QueryDobj();
			    	msgInfo.setId(id);
			    	msgInfo.setViewOrTableId(viewID);
			    	msgInfo.setSortString(sorts);
			    	msgInfo.setType(MessagingUtils.getQueryTypeStr(Integer.parseInt(queryType)));
			    	if(groupFieldId != null &&  !groupFieldId.equals("") && !groupFieldId.equalsIgnoreCase(Constants.UNDEFINED))
			    		msgInfo.setGroupFieldId(Integer.parseInt(groupFieldId));
		    	}else{
		    		//TODO :IT IS WEBSERVICE
		    	}
		    }
			if(result.equals(MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL))) {
				if(messagingType != 1 && messagingType != 2 && (enterFormatMessage == null || enterFormatMessage.equals("") || enterFormatMessage.equals(Constants.UNDEFINED) )){
					result = MessageConstants.getMessage(file, MessageConstants.MISSING_ENTER_TXT);
				}else if((messagingType == 2 && mobileStatus.equals("1")) && (smsKeyword == null || smsKeyword.equals(Constants.UNDEFINED) || smsKeyword.equals(""))){//we have this validation just for gsm
					result = MessageConstants.getMessage(file, MessageConstants.MISSING_ENTER_SMS_KEY);
				}else if(messagingType == 3  && (imKeyword == null || imKeyword.equals(Constants.UNDEFINED) || imKeyword.equals(""))){
					result = MessageConstants.getMessage(file, MessageConstants.MISSING_ENTER_IM_KEY);
				}else if(ds.equals(Constants.DS_DB_STATUS)&& !msgInfo.getType().equalsIgnoreCase(Constants.SELECT_QUERY_TYPE)){
					result = MessageConstants.getMessage(file, MessageConstants.NO_SELECT_QUERY); 
				}else if(result.equals(MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL))){
					ArrayList<String> userInputsList = null;
					
					String keyword ="";
					String keywordInTextTest = null;
					if(messagingType == 1){
						if(emailKeyword == null || emailKeyword.equals("") || emailKeyword.equals(Constants.UNDEFINED) ){
							userInputsList = MessagingUtils.getUserInputForNoKeywordQuery(separator, enterFormatMessage);
							keyword="";
							keywordInTextTest = "";
						}else{
							keyword = emailKeyword;
						}
					}
					if(messagingType == 2){
						if(!mobileStatus.equals("1")&& (smsKeyword == null || smsKeyword.equals("") || smsKeyword.equals(Constants.UNDEFINED))){
							userInputsList = MessagingUtils.getUserInputForNoKeywordQuery(separator, enterFormatMessage);
							keyword = "";
							keywordInTextTest = "";						
						}else{
							keyword = smsKeyword;
						}
					}
					
					if(messagingType == 3 ){
						keyword = imKeyword;					
					}
					
					if(userInputsList == null){
						Map userInputMap = MessagingUtils.userInputsParser(separator, enterFormatMessage);
						userInputsList = (ArrayList<String>)userInputMap.get("userInputs");
						keywordInTextTest = (String)userInputMap.get("keyword");
					}
					
					if(!keyword.equals(keywordInTextTest))
						result = MessageConstants.getMessage(file, MessageConstants.INVALID_KEY); 
					else{
						RecordSet rs = null;
						if(id != -1){//Existing
							if(ds.equals(Constants.DS_DB_STATUS)){
								if(msgInfo.getType().equalsIgnoreCase(Constants.SELECT_QUERY_TYPE)){
									view = dataAgent.getMetaViewByID(viewID, true);
									int dbId =  view.getSourceDsId();
			
									if(userInputsList!= null && !userInputsList.isEmpty() && sql.contains("UserInput"))
										rs = dataAgent.getSelectQueryResultWithUserInput(msgInfo, null, userInputsList, null, -1);
									else{
										DataViewDataSource dvds = MdnDataManager.getDataViewDS();
										view = dataAgent.getMetaViewByID(viewID, true);
										dbId =  view.getSourceDsId();
										dvds = MdnDataManager.getDataViewDS();
										rs = dvds.execDirectSQL(dbId, sql, view);
									}
								}
							}else{//WS Edit Q mode
								String wsId = request.getParameter("wsId");
								WebServiceOperationDobj selectedWebService = dataAgent.getWebServiceOperationByID(Integer.parseInt(wsId));
								
								ParamHelper paramHelper = MessagingUtils.getParamHelper(selectedWebService);
								List<ParamListItem> resultParam = MessagingUtils.executeWebService(request, userInputsList, paramHelper);
								
								int displayResult = 1;
								try{
									if(messagingType == 1)
									{
										displayResult = Integer.parseInt(emailDisplayResult);
									}else if(messagingType == 2){
										displayResult = Integer.parseInt(mobileDisplayResult);
									}else if(messagingType == 3){
										displayResult = Integer.parseInt(imDisplayResult);
									}
								} catch (NumberFormatException e1) {
									displayResult = 1;
								}									
								replyContext = MessagingUtils.prepareWSResponseMsg(paramHelper, resultParam, displayResult, responseFormat);								
							}
						}else{//NEW
							if(ds.equals(Constants.DS_DB_STATUS)){
								DataViewDataSource dvds = MdnDataManager.getDataViewDS();
								view = dataAgent.getMetaViewByID(viewID, true);
								int dbId =  view.getSourceDsId();
								dvds = MdnDataManager.getDataViewDS();
								if(!userInputsList.isEmpty() && sql.contains("UserInput")){
									String sqlParsed = sqlParser(request, view, userInputsList);
									if(!sqlParsed.equals(""))
										sql = sqlParsed;
								}
								rs = dvds.execDirectSQL(dbId, sql, view);
							}else{//TODO: web service
								//-----------------------------------------------------------------
								WebServiceOperationDobj selectedWebService = new WebServiceOperationDobj();
								selectedWebService.setUrl(wsdlUrl);
								selectedWebService.setOperation(selectedOperation);
								selectedWebService.setService(selectedService);
								selectedWebService.setPort(selectedPort);
								
								//replyingMsg = MessagingUtils.getDefaultResponse(null, null, null, null, request, 2, selectedWebService);				
								ParamHelper paramHelper = MessagingUtils.getParamHelper(selectedWebService);
								List<ParamListItem> resultParam = MessagingUtils.executeWebService(request, userInputsList, paramHelper);//new ArrayList<ParamListItem>();
/* 							try {
//									List<ParamListItem> paramList = new ArrayList<ParamListItem>();
									ParamHelper paramHelper = null;
									Map paramHelperMap = MessagingUtils.getParamHelperMap();
									String wsdlUrlKey = wsdlUrl+selectedOperation;
									if(paramHelperMap.containsKey(wsdlUrlKey)){
										paramHelper = (ParamHelper)paramHelperMap.get(wsdlUrlKey);
									}
									
									List myValuesList = new ArrayList(); 
									if(paramHelper != null){
										if (!empty){
											int userinputIndex = 0;
											for (int i = 0; i< rowsList.length; i++){
								            	String number = null;
								            	if (numbersList != null && numbersList.length == rowsList.length)
								            		number = numbersList[i];                	
								            	
								            	String field = fieldsList[i];
								            	String value = valuesList[i];               	
								            	
								            	if(value.equals("[UserInput]")){
								            		if(userInputsList != null && !userInputsList.isEmpty())
								            			value = userInputsList.get(i);
								            		
								            		if(userInputsList != null && !userInputsList.isEmpty()){
								            			value = userInputsList.get(userinputIndex);
								            			userinputIndex++;
								            		}
								            		
								            	}
								            	myValuesList.add(value);
								            }        			
										}				

										Object[] myvalues = myValuesList.toArray(new Object[0]);
										List<ParamListItem> reqParamList = paramHelper.getParamList();
										resultParam =  paramHelper.invoke(reqParamList, myvalues);
ta inja */										
										//-get responseFormat -> if != null -> make response msg as String >>
										int displayResult = 1;
										try{
											if(messagingType == 1)
											{
												displayResult = Integer.parseInt(emailDisplayResult);
											}else if(messagingType == 2){
												displayResult = Integer.parseInt(mobileDisplayResult);
											}else if(messagingType == 3){
												displayResult = Integer.parseInt(imDisplayResult);
											}
										} catch (NumberFormatException e1) {
											displayResult = 1;
										}									
										replyContext = MessagingUtils.prepareWSResponseMsg(paramHelper, resultParam, displayResult, responseFormat);
								
										/*String originalResult = "";
										List<WebServiceResultRow> paramListItemsAsRow = paramHelper.getParamListItemsAsRow();
										Set titlesSet = new HashSet();
										for(ParamListItem resultItem : resultParam )
										{
											String title;
											Object resultItemValue = resultItem.getValue();
											if(resultItemValue.getClass().isArray()) {
												Object type = resultItemValue.getClass().getComponentType();
												Object[] resultItemArray = (Object[])resultItemValue;
												int len = resultItemArray.length;
												for(int i = 0; i< resultItemArray.length; i++){
													Object obj = resultItemArray[i];
													originalResult = originalResult + obj.toString() + "\n";
												}
											} else {
												title = resultItem.getLabel();
												titlesSet.add(title);
												String fieldItem = title + ": " + resultItemValue.toString() + "\n";    
												originalResult = originalResult + fieldItem;
											}
											
										}			
										
										replyContext +="";
										int displayResult = 1;
										try{
											if(messagingType == 1)
											{
												displayResult = Integer.parseInt(emailDisplayResult);
											}else if(messagingType == 2){
												displayResult = Integer.parseInt(mobileDisplayResult);
											}else if(messagingType == 3){
												displayResult = Integer.parseInt(imDisplayResult);
											}
										} catch (NumberFormatException e1) {
											displayResult = 1;
										}	
								
										//Vector titles = new Vector();
										Object titles[] = (Object[])titlesSet.toArray();
							
										if( displayResult > paramListItemsAsRow.size())
											displayResult = paramListItemsAsRow.size();
							    
										if(!paramListItemsAsRow.isEmpty()){
											for(int k = 0;  k < displayResult; k++) {
												// get the data object
												WebServiceResultRow rowObj = paramListItemsAsRow.get(k);//one row which contains complexType fields If result is ComplexType[] array
								
												if(responseFormat != null && !responseFormat.trim().equals(""))
													replyContext += MessagingUtils.responseMassageParserWS(responseFormat, titles, rowObj) + "\n";
												else{
									    			List<ParamListItem> paramsRowObj = rowObj.getParamItemList();
													for(ParamListItem resultItem : paramsRowObj )
													{
														String title;
														Object resultItemValue = resultItem.getValue();
														if(resultItemValue.getClass().isArray()) {
															Object[] resultItemArray = (Object[])resultItemValue;
															for(int i = 0; i< resultItemArray.length; i++){
																Object obj = resultItemArray[i];
																replyContext = replyContext + obj.toString() + "\n";
															}
														} else {
															title = resultItem.getLabel();
															String fieldItem = title + ": " + resultItemValue.toString() + "\n";    
															replyContext = replyContext + fieldItem;
														}														
													}
												}	
											}
										}else{
											replyContext = originalResult;
										}
										*/
										//_______________________________________________________;
//									}else{
//										result = "You first need to define input arguments in query builder tab.";
//									}
								
//								} catch (MdnException e) {
//									e.printStackTrace();
//								}										
								//------------------------------------------------------------------			
							}
						}
						//___________________________________________________________________________
						if( rs!= null && ds.equals(Constants.DS_DB_STATUS)) {
							replyContext +="";
							Vector rows = rs.getRows();
				
							int displayResult = 1;
							try{
								if(messagingType == 1)
								{
									displayResult = Integer.parseInt(emailDisplayResult);
								}else if(messagingType == 2){
									displayResult = Integer.parseInt(mobileDisplayResult);
								}else if(messagingType == 3){
									displayResult = Integer.parseInt(imDisplayResult);
								}
							} catch (NumberFormatException e1) {
								displayResult = 1;
							}	
					
							Vector titles = new Vector();
							if(rows != null && rows.size() > 0)
								titles = ((DataObject)rows.elementAt(0)).getEntity().getFields();
				
							if( rows != null && displayResult > rows.size())
								displayResult = rows.size();
				    
							for(int k = 0; rows != null && k < displayResult; k++) {
								// get the data object
								DataObject obj = (DataObject)rows.elementAt(k);
				
				        		// get the data object
								replyContext += MessagingUtils.responseMassageParser(responseFormat, titles, obj) + "\n";
							}
				    	}
					}
				}
			}
		} catch (MdnException e) {
			e.printStackTrace();
		} catch (DataSourceException e) {
			e.printStackTrace();
		}

		if(replyContext.equals(""))
			replyContext = MessageConstants.getMessage(file, MessageConstants.NO_RESULT_TEST_RESPONSE);
		
		return new XmlFormatter().testMessaginResultXML(file, msgInfo, replyContext, result, emailAddresses, allSmpp, responseFormat);		
	}
	
	private Element displayMsgSeperator(HttpServletRequest request){
		//  Get the language file.
		String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
		
		String seperator = "";
		
	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		try {
			MdnMessageSeparator msgSep = dataAgent.getMessageSeparator();
			if(msgSep != null){
				seperator = msgSep.getConditionSeperator();
			}	
			
		} catch (MdnException e) {
			e.printStackTrace();
		} 

		return new XmlFormatter().displayMessageSeparatorXML(file, seperator);
   }
	
	private Element saveMsgSeperator(HttpServletRequest request){
		//  Get the language file.
		String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    String seperator = request.getParameter("seperator");	
	    String other = request.getParameter("other");
	    int state =0;
		
	    MdnMessageSeparator msgSep = new MdnMessageSeparator();
	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	    
		try {
			msgSep = dataAgent.getMessageSeparator();

			if(msgSep != null)
				state = 1;
			else
				msgSep = new MdnMessageSeparator();
			
			if(seperator.equals("0"))
				msgSep.setConditionSeperator("");
	    	else if(seperator.equals("1"))
	    		msgSep.setConditionSeperator(",");
	    	else if(seperator.equals("2"))
	    		msgSep.setConditionSeperator(";");
	    	else if(seperator.equals("3") && other != null && !other.trim().equals("") && !other.equals(Constants.UNDEFINED)){
	    		msgSep.setConditionSeperator(other);
	    	}else
	    		msgSep.setConditionSeperator("");//Default seperator
			
			msgSep.setState(state);
			msgSep.save();
			
		} catch (MdnException e) {
			e.printStackTrace();
		} catch (DataSourceException e) {
			e.printStackTrace();
		}

		return new XmlFormatter().displayMessageSeparatorXML(file, msgSep.getConditionSeperator());
   }
	private Element displayEmptyUserReply(HttpServletRequest request){
		String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
		
	    String queryType = request.getParameter("query-type");
	    String parentId = request.getParameter("msgId");

    	//Get project ID	    	
	    String projectId = request.getParameter("projectId");
	    int intProjectId = 1;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e1) {
			intProjectId = 1;
		}	    
	    
	    String qmParentName= "";
	    String qmParentDesc= "";

	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();	    
		try {
			QueryDobj queryMsgParent = dataAgent.getQueryByID(Integer.parseInt(parentId));
			if(queryMsgParent != null){
			    qmParentName= queryMsgParent.getName();
			    qmParentDesc= queryMsgParent.getDescription();
			}else
			{
				UserReply userReply = dataAgent.getUserReplyById(Integer.parseInt(parentId));
			    qmParentName= userReply.getMsgText();
			    qmParentDesc= userReply.getDescription();
			    if(qmParentName == null || qmParentName.equals(""))
			    	qmParentName = "-";
			    if(qmParentDesc == null || qmParentDesc.equals(""))
			    	qmParentDesc= "-";
			}
		    		
		    databases = new ArrayList<DataSourceDobj>();
		    if(queryType == null)
		    {
		    	queryType = "1";
		    }
		    int selectedQuerytype = Integer.parseInt(queryType);	    
		    vtLable = "";
			if(selectedQuerytype == 1)//Select
				vtLable = MessageConstants.getMessage(file, MessageConstants.SELECT_VIEW_LABEL);			
			else//Update or Insert
				vtLable = MessageConstants.getMessage(file, MessageConstants.SELECT_TABLE_LABEL);

		    // Get the queries from the database.
	    	databases = dataAgent.getAllDbConnections(intProjectId);

		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (MdnException e1) {
			e1.printStackTrace();
		}
		return new XmlFormatter().emptyUserReplyXML(databases, vtLable, parentId, qmParentName, qmParentDesc);
	}
	
	private Element addUserReply(HttpServletRequest request){
		
		//String action = request.getParameter("action");
		String saveState = request.getParameter("saveState");
		int saveStateInt = Integer.parseInt(saveState);
		String strTableID = request.getParameter("tableID");

		String queryType = request.getParameter("queryType");
		String sqlText = request.getParameter("sqlText");
		String ids = request.getParameter("ids");
		String rows = request.getParameter("rows");		
		String types = request.getParameter("types");
		String useds = request.getParameter("useds");
		String indents = request.getParameter("indents");
		String valueOrConditions = request.getParameter("valueOrConditions");
		String numbers = request.getParameter("numbers");
		String parents = request.getParameter("parents");
		String fields = request.getParameter("fields");
		String compIDs = request.getParameter("compIDs");
		String comparisons = request.getParameter("comparisons");
		String values = request.getParameter("values");
		String connections = request.getParameter("connections");
		String value2s = request.getParameter("value2s");
		String groupings = request.getParameter("groupings");
		String sorts = request.getParameter("sorts");		
		String groupFieldId = request.getParameter("groupFieldId");
		String userInputSeqs = request.getParameter("userInputSeqs");
		String userInputSeqs2 = request.getParameter("userInputSeqs2");

		String parentId = request.getParameter("parentId");
		
		String textMsg = request.getParameter("textMsg");
		String msgDesc = request.getParameter("msgDesc");		
		String timeoutUr = request.getParameter("timeoutUr");
		String newResponseFormat = request.getParameter("newResponseFormat");	
		String displayResult = request.getParameter("displayResult");	

    	//Get project ID	    	
	    String projectId = request.getParameter("projectId");
	    int intProjectId = 1;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e1) {
			intProjectId = 1;
		}		
	    // Get the current language file.
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
		
		//Messaging Info
	    String dbId = request.getParameter("dbId");	    
	    
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		String result = MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL);

		String resText = "";
		int dbIdInt = -1;
		UserReply userReply = new UserReply();
		try {
			UserReply urByTxt = dataAgent.getUserReplyByMessageText(textMsg);
			int foundUrParentId = 0; 
			if(urByTxt != null)
				foundUrParentId = urByTxt.getParentId();
			
	    	if(textMsg == null || textMsg.equals("") || textMsg.equals(Constants.UNDEFINED)) 
	    		result = MessageConstants.getMessage(file, MessageConstants.MISSING_UR_MSG_KEYWORD);
	    	else if(urByTxt != null && foundUrParentId == Integer.parseInt(parentId) ) 
			    result = MessageConstants.getMessage(file, MessageConstants.DUPLICATE_UR_MSG_KEYWORD);
			
	    	//userReply.setDelStatus(Integer.parseInt(Constants.MARKED_AS_NOT_DELETED));
	    	if(dbId == null || dbId.equals("") || dbId.equals(Constants.UNDEFINED))				
		    	result = MessageConstants.getMessage(file, MessageConstants.MISSING_DB);
		    else if(strTableID == null || strTableID.equals("") || strTableID.equals(Constants.UNDEFINED))		
		    	result = MessageConstants.getMessage(file, MessageConstants.MISSING_VIEW_TBL);

	    	if(result.equalsIgnoreCase(MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL))){
		    	try{
		    		dbIdInt = Integer.parseInt(dbId);
		    	}catch(NumberFormatException e){
		    		String selectedDbName = dbId;
		    		dbIdInt = (dataAgent.getDbConnectionByName(intProjectId, selectedDbName)).getId();
		    	}
		    	try{
		    		int timeoutint = Integer.parseInt(timeoutUr);
		    		if(timeoutint <= 0)
		    			userReply.setTimeout("5");
		    		else
		    			userReply.setTimeout(timeoutUr);
		    	}catch(NumberFormatException e){
		    		userReply.setTimeout("5");//Default is 5 Seconds
		    	}

	            try{
		    		userReply.setDisplayResult(Integer.parseInt(displayResult));
		    	}catch(NumberFormatException e){
		    		userReply.setDisplayResult(1);//Default is 1 
		    	}

		    	userReply.setMsgText(textMsg.toLowerCase());
		    	
		    	userReply.setDescription(msgDesc);
		    	userReply.setResponse(newResponseFormat);
		    	
		    	userReply.setParentId(Integer.parseInt(parentId));
		    	
	            int tableID = Integer.parseInt(strTableID);
	            userReply.setViewOrTableId(tableID);
	            userReply.setType(queryType);
	            userReply.setCriteriaString(sqlText);
	            if (sorts != null && !sorts.equalsIgnoreCase(Constants.UNDEFINED)&& !sorts.equals(""))
	            	userReply.setSortString(sorts);
	            
	            if (groupFieldId != null && !groupFieldId.equalsIgnoreCase(Constants.UNDEFINED) && !groupFieldId.equals("") && !groupFieldId.equalsIgnoreCase("-1")){
	            	int groupFieldIdInt = 0;
					try {
						groupFieldIdInt = Integer.parseInt(groupFieldId);
					} catch (NumberFormatException e) {
						//In this case, group field id is name.
						groupFieldIdInt = getGroupFieldIdByName(userReply, groupFieldId);
					}
					userReply.setGroupFieldId(groupFieldIdInt);	
	            }
		    	
	            userReply.setDatabaseId(dbIdInt);
		    	
	            userReply.setState(saveStateInt);
	            userReply.setDatasourceStatus(1);
	            userReply.setDelStatus(Constants.MARKED_AS_NOT_DELETED_INT);
	            userReply.setProjectId(intProjectId);
	            userReply.save();
                
	            int newUserReplyId = userReply.getId();
	            addChildToURParent(Integer.parseInt(parentId), newUserReplyId);
	            
                String[] idsList = ids.split(",");
                String[] rowsList = rows.split(",");
                String[] typesList = types.split(",");
                String[] usedsList = useds.split(",");
                String[] indentsList = indents.split(",");
                String[] valueOrConditionsList = valueOrConditions.split(",");        
                
                String[] numbersList = null;
                if (numbers != null && !numbers.equals(""))
                	numbersList = numbers.split(",");
 
                String[] parentsList = null;
                if (parents != null && !parents.equals(""))
                	parentsList = parents.split(",");                
                
                String[] fieldsList = fields.split(",");
                String[] compIDsList = compIDs.split(",");
                String[] comparisonsList = comparisons.split(",");
                String[] valuesList = values.split(",");
                String[] connectionsList = connections.split(",");
                String[] value2sList = value2s.split(",");
                String[] groupingsList = groupings.split(",");
                String[] userInputSeqsList = userInputSeqs.split(",");
                String[] userInputSeqs2List = userInputSeqs2.split(",");
                
        		boolean empty = false;
        		if (rowsList.length == 1){
        			String rowList =rowsList[0]; 
        			if (rowList.equals("")){
        				//There is no query criteria need to save
        				empty = true;
        				result = "OK";
        			}
        		}
        		
        		if (!empty){
	                int index = 1;
	                for (int i = 0; i< rowsList.length; i++){
	                	String id = idsList[i];
	                	String row = rowsList[i];
	                	String type = typesList[i];
	                	String used = usedsList[i];
	                	String indent = indentsList[i];
	                	String valueOrCondition = valueOrConditionsList[i];
	                	
	                	String parent = null;
	                	if (parentsList != null && parentsList.length == rowsList.length)
	                		parent = parentsList[i];
	                	
	                	String number = null;
	                	if (numbersList != null && numbersList.length == rowsList.length)
	                		number = numbersList[i];                	
	                	
	                	String field = fieldsList[i];
	                	String compID = compIDsList[i];
	                	String comparison = comparisonsList[i];
	                	String value = valuesList[i];               	
	                	String connection = connectionsList[i];
	                	String value2 = value2sList[i];               	
	                	String grouping = groupingsList[i];
	                	
                    	String userInputSeq = null;
                    	if (userInputSeqsList != null && userInputSeqsList.length > i)//if (userInputSeqsList != null && userInputSeqsList.length == rowsList.length)
                    		userInputSeq = userInputSeqsList[i];
                    	
                    	String userInputSeq2 = null;
                    	if (userInputSeqs2List != null && userInputSeqs2List.length > i)//if (userInputSeqs2List != null && userInputSeqs2List.length == rowsList.length)
                    		userInputSeq2 = userInputSeqs2List[i];
	                	
	                	// create a Query Criteria object
	                	QueryCriteriaDobj queryCriteriaObj = new QueryCriteriaDobj();
	                	
	                	int queryCriteriaSaveState = DataObject.NEW;
	                	if (id != null && !id.equalsIgnoreCase(Constants.UNDEFINED) && !id.equalsIgnoreCase("-1")){
	                		try {
								queryCriteriaObj = dataAgent.getQueryCriteriaByID(Integer.parseInt(id));
								queryCriteriaSaveState = DataObject.IN_DB;
							} catch (NumberFormatException e) {
								queryCriteriaSaveState = DataObject.NEW;
							}
	                	}
	                	
	                	queryCriteriaObj.setQueryId(newUserReplyId);
	                	queryCriteriaObj.setValueOrCondition(valueOrCondition);
	                	int rowInt;
						try {
							rowInt = Integer.parseInt(row);
						} catch (NumberFormatException e) {
							rowInt = index;
						}
	                    queryCriteriaObj.setRowNo(rowInt);
	                    queryCriteriaObj.setType(type);
	                    queryCriteriaObj.setUsed(Boolean.valueOf(used).booleanValue() ? 1 :0);
	                    queryCriteriaObj.setIndent(Integer.parseInt(indent));
	                    queryCriteriaObj.setParent(parent == null ? "" : parent);
	                    queryCriteriaObj.setNumber(number == null ? 0 : Integer.parseInt(number));
	                    queryCriteriaObj.setName(field);
	                    queryCriteriaObj.setCompId(Integer.parseInt(compID));
	                    queryCriteriaObj.setComparison(comparison);
	                    queryCriteriaObj.setValue(value);
	                    queryCriteriaObj.setConnection(connection);
	                    queryCriteriaObj.setValue2(value2);
	                    queryCriteriaObj.setGrouping(grouping);
	                    
                    	queryCriteriaObj.setValueUserInputSeq(userInputSeq);
                    
                    	queryCriteriaObj.setValue2UserInputSeq(userInputSeq2);
	                    
                    	queryCriteriaObj.setObjectType("UR");
                    	
	                    queryCriteriaObj.setState(queryCriteriaSaveState);
	                    queryCriteriaObj.save();
	                    index++;
	                }
        		}
    			resText = result;
	    	}
		} catch (MdnException e1) {
			e1.printStackTrace();
			result = "Database error";
		} catch (DataSourceException e) {
			e.printStackTrace();
			result = "Database error";
		}			
		return new XmlFormatter().addUserReplyXML(file, result, userReply, resText, projectId);	    
	}	
	
	private int getGroupFieldIdByName(UserReply urMsg, String groupFieldName) {
		int groupFieldId = 0;
		//This situation only happens when it is select query
	  	Object[] fields = urMsg.getDataView (true).getFields ().toArray();
	  	for (int i = 0; i < fields.length; i++){
	  		DataViewField field = (DataViewField)fields[i];
			if (groupFieldName.equalsIgnoreCase(field.getName())){
				groupFieldId = field.getId();				
			}				
	  	}		
		
		return groupFieldId;
	}
	
	private void addChildToURParent(int parentId, int newChildId){
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		try {
			UserReply ur = dataAgent.getUserReplyById(parentId);
			if(ur != null){//If parent be a query
				String oldChildrenList = ur.getChildren();
				if(oldChildrenList == null || oldChildrenList.length()<1)
					oldChildrenList = "";
				else
					oldChildrenList = oldChildrenList + ",";
				
				String newChildrenList = oldChildrenList.concat(String.valueOf(newChildId));
				ur.setChildren(newChildrenList);
				
				ur.setState(1);
				ur.setDelStatus(Constants.MARKED_AS_NOT_DELETED_INT);
				ur.save();
			}

		} catch (MdnException e) {
			e.printStackTrace();
		} catch (DataSourceException e) {
			e.printStackTrace();
		}
	}
	
	private Element displayUserReplyProps(HttpServletRequest request){
	    String dsIDStr = request.getParameter("dsStatus");//
	    int ds = 1;
		try {
			ds = Integer.parseInt(dsIDStr);
		} catch (NumberFormatException e1) {
			ds = 1;
		}	    
		
		
		//  Get the language file.
		String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
		int id = Integer.parseInt(request.getParameter("urId"));
		String projectId = request.getParameter("projectId");
	    int intProjectId = 1;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e1) {
			intProjectId = 1;
		}	
		

	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		UserReply userReply = null;
		QueryDobj query = null;
		UserReply urParent = null;
		dbName = "";
		vtLable = "";
		String viewOrTableName = "";		
		String responseTxt = "";
		String defaultResponse = "";

		try {
			userReply = dataAgent.getURPropertiesByID(id);
			int parentId = userReply.getParentId();
			query = dataAgent.getQueryByID(parentId);
			if(query == null)
				urParent = dataAgent.getURPropertiesByID(parentId);
			if(userReply.getDatasourceStatus() == 1){ 
				String querytypeDb = userReply.getType();
				int queryType = MessagingUtils.getQueryTypeNumber(querytypeDb);

				//Get Selected database Id from DB
				int viewOrTableId = userReply.getViewOrTableId();
				int dbIdInDB = userReply.getDatabaseId();

				if(queryType == Constants.SELECT_QUERY_TYPE_NUM){//Select
					vtLable = MessageConstants.getMessage(file, MessageConstants.SELECT_VIEW_LABEL);			
					DataView view = dataAgent.getMetaViewByID(viewOrTableId, true);
					viewOrTableName = view.getName();
				}else{//Update or Insert
					vtLable = MessageConstants.getMessage(file, MessageConstants.SELECT_TABLE_LABEL);
					EntityDobj table = dataAgent.getMetaTableByID(viewOrTableId, true);
					viewOrTableName = table.getName();
				}

				DataSourceDobj	db = dataAgent.getDbConnectionByID(intProjectId, dbIdInDB);
				dbName = db.getName();
			}
			
			if(userReply != null){
				int queryTypeNum = MessagingUtils.getQueryTypeNumber(userReply.getType());
				defaultResponse = MessagingUtils.getDefaultResponse(String.valueOf(queryTypeNum), userReply.getCriteriaString(), String.valueOf(userReply.getDatabaseId()), String.valueOf(userReply.getViewOrTableId()), request, ds, null);
			}
			
		    if(id != -1){
				//responseTxt ="Not defined any response Message for this query";
				responseTxt =MessageConstants.getMessage(file, MessageConstants.NO_RESPONSE_FORMAT); 
				
				//Manage Response format
				String resTxt = userReply.getResponse();
				int viewID = userReply.getViewOrTableId();
				String queryType = userReply.getType();
				if(resTxt!=null && !resTxt.equals(""))
					responseTxt = resTxt;
				else{
					responseTxt = defaultResponse;
				}
			if(responseTxt.equals(""))
				responseTxt = MessageConstants.getMessage(file, MessageConstants.NO_RESULT_TEST_RESPONSE);
		    }else{
		    	userReply = new UserReply();
		    	userReply.setId(id);
				responseTxt = "";
		    }			
		} catch (MdnException e) {
			e.printStackTrace();
		}
		// Format the data.
		return new XmlFormatter().displayUrPropsResult(file, dbName, userReply, query, urParent,  vtLable, viewOrTableName, responseTxt, defaultResponse);
	}
	
	private Element saveUserReplyProps(HttpServletRequest request){
		String saveState = request.getParameter("saveState");
		int saveStateInt = Integer.parseInt(saveState);
		String urIdStr = request.getParameter("urId");
		int urId = Integer.parseInt(urIdStr);
		String parentName = request.getParameter("parentName");
		String parentDesc = request.getParameter("parentDesc");
		String parentId = request.getParameter("parentId");		

		String msgTxt = request.getParameter("msgTxt");
		String msgDesc = request.getParameter("msgDesc");
		String timeout = request.getParameter("timeout");

		String strTableID = request.getParameter("tableID");
		String viewOrTableIdOld = request.getParameter("viewOrTableIdInDB");
		String queryType = request.getParameter("queryType");
	    String dbId = request.getParameter("dbId");	    

    	//Get project ID	    	
	    String projectId = request.getParameter("projectId");
	    int intProjectId = 1;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e1) {
			intProjectId = 1;
		}	    
	    // Get the current language file.
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		String result = MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL);

		int dbIdInt = -1;
		UserReply userReply = new UserReply();		
		try {
				if (saveStateInt == DataObject.IN_DB){
					userReply = dataAgent.getUserReplyById(urId);
				}
				
				UserReply urByTxt = dataAgent.getUserReplyByMessageText(msgTxt);
				int foundUrParentId = 0; 
				if(urByTxt != null){
					foundUrParentId = urByTxt.getParentId();//parentId
				}
				
		    	if(msgTxt == null || msgTxt.equals("") || msgTxt.equals(Constants.UNDEFINED)) 
		    		result = result = MessageConstants.getMessage(file, MessageConstants.MISSING_UR_MSG_KEYWORD);
			    else if(urByTxt != null && !msgTxt.equals(userReply.getMsgText()) && foundUrParentId == userReply.getParentId() ) 
				    result = MessageConstants.getMessage(file, MessageConstants.DUPLICATE_UR_MSG_KEYWORD);
		    	
		    	if(saveStateInt == 0){
			    	if(dbId == null || dbId.equals("") || dbId.equals(Constants.UNDEFINED))				
				    	result = MessageConstants.getMessage(file, MessageConstants.MISSING_DB);
				    else if(strTableID == null || strTableID.equals("") || strTableID.equals(Constants.UNDEFINED))		
				    	result = MessageConstants.getMessage(file, MessageConstants.MISSING_VIEW_TBL);

			    	if(result.equalsIgnoreCase(MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL))){
				    	try{
				    		dbIdInt = Integer.parseInt(dbId);
				    	}catch(NumberFormatException e){
				    		String selectedDbName = dbId;
				    		dbIdInt = (dataAgent.getDbConnectionByName(intProjectId, selectedDbName)).getId();
				    	}
			            int tableID = Integer.parseInt(strTableID);
			            userReply.setViewOrTableId(tableID);
			            userReply.setType(queryType);
			            userReply.setDatabaseId(dbIdInt);
			    	}
		    	}
		    	
		    	try{
		    		int timeoutint = Integer.parseInt(timeout);
		    		if(timeoutint <= 0)
		    			userReply.setTimeout("5");
		    		else
		    			userReply.setTimeout(timeout);
		    	}catch(NumberFormatException e){
		    		userReply.setTimeout("5");//Default is 5 Seconds
		    		//result = "Timeout is invalid number";
		    	}
		    	
		    	if(result.equalsIgnoreCase(MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL))){
		    		userReply.setMsgText(msgTxt.toLowerCase());
		    		userReply.setDescription(msgDesc);
			    	
		    		userReply.setState(saveStateInt);
		    		userReply.setDatasourceStatus(1);
		    		userReply.setDelStatus(Constants.MARKED_AS_NOT_DELETED_INT);
		    		userReply.save();

	                int newUserReplyID = userReply.getId();
	                System.out.println("new UserReply ID " + newUserReplyID);
		    	}
		} catch (MdnException e1) {
			e1.printStackTrace();
			result = MessageConstants.getMessage(file, MessageConstants.DB_ERROR);
		} catch (DataSourceException e) {
			e.printStackTrace();
			result = MessageConstants.getMessage(file, MessageConstants.DB_ERROR);
		}			
		return new XmlFormatter().saveUserReplyPropsXML(file, result, userReply, viewOrTableIdOld, parentName, parentDesc);	    
	}	
	private Element saveUserReplyQuery(HttpServletRequest request) {
		String saveState = request.getParameter("saveState");
		int saveStateInt = Integer.parseInt(saveState);
		String dbConnID = request.getParameter("connID");
		
	    String projectId = request.getParameter("projectId");
	    int intProjectId = 1;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e1) {
			intProjectId = 1;
		}	

		int connectionID;
		try {
			connectionID = Integer.parseInt(dbConnID);
		} catch (NumberFormatException e2) {
			connectionID = 0;
		}		
		String urIdStr = request.getParameter("urId");
		int urId;
		try {
			urId = Integer.parseInt(urIdStr);
		} catch (NumberFormatException e2) {
			urId = -1;
		}
		String strTableID = request.getParameter("tableID");
		int tableID;
		try {
			tableID = Integer.parseInt(strTableID);
		} catch (NumberFormatException e2) {
			tableID = -1;
		}		
		String queryType = request.getParameter("queryType");
		String sqlText = request.getParameter("sqlText");
		String ids = request.getParameter("ids");
		String rows = request.getParameter("rows");		
		String types = request.getParameter("types");
		String useds = request.getParameter("useds");
		String indents = request.getParameter("indents");
		String valueOrConditions = request.getParameter("valueOrConditions");
		String numbers = request.getParameter("numbers");
		String parents = request.getParameter("parents");
		String fields = request.getParameter("fields");
		String compIDs = request.getParameter("compIDs");
		String comparisons = request.getParameter("comparisons");
		String values = request.getParameter("values");
		String connections = request.getParameter("connections");
		String value2s = request.getParameter("value2s");
		String groupings = request.getParameter("groupings");
		String sorts = request.getParameter("sorts");		
		String groupFieldId = request.getParameter("groupFieldId");	
		String userInputSeqs = request.getParameter("userInputSeqs");
		String userInputSeqs2 = request.getParameter("userInputSeqs2");
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		String result = "";
		
		String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
		try {
	        // create the entity
			UserReply ur = new UserReply();
			if (saveStateInt == DataObject.IN_DB){
				ur = dataAgent.getUserReplyById(urId);
			}
            
            ur.setViewOrTableId(tableID);
            ur.setType(queryType);
            ur.setCriteriaString(sqlText);
            if (sorts != null && !sorts.equalsIgnoreCase(Constants.UNDEFINED)&& !sorts.equals("") && !sorts.equalsIgnoreCase("null"))
            	ur.setSortString(sorts);
            if (groupFieldId != null && !groupFieldId.equalsIgnoreCase(Constants.UNDEFINED) && !groupFieldId.equalsIgnoreCase("null") && !groupFieldId.equals("") && !groupFieldId.equalsIgnoreCase("-1")){
            	int groupFieldIdInt = 0;
				try {
					groupFieldIdInt = Integer.parseInt(groupFieldId);
				} catch (NumberFormatException e) {
					//In this case, group field id is name.
					groupFieldIdInt = getGroupFieldIdByName(ur, groupFieldId);
				}
            	ur.setGroupFieldId(groupFieldIdInt);	
            }
            	
            
            // save ds
            ur.setState(saveStateInt);
            ur.setProjectId(intProjectId);
            ur.setDelStatus(Constants.MARKED_AS_NOT_DELETED_INT);
            ur.save();
            
            int newQueryID = ur.getId();
            System.out.println("new query ID " + newQueryID);
            
            String[] idsList = ids.split(",");
            String[] rowsList = rows.split(",");
            String[] typesList = types.split(",");
            String[] usedsList = useds.split(",");
            String[] indentsList = indents.split(",");
            String[] valueOrConditionsList = valueOrConditions.split(",");        
            
            String[] numbersList = null;
            if (numbers != null)
            	numbersList = numbers.split(",");
 
                String[] parentsList = null;
                if (parents != null)
                	parentsList = parents.split(",");                
            
            String[] fieldsList = fields.split(",");
            String[] compIDsList = compIDs.split(",");
            String[] comparisonsList = comparisons.split(",");
            String[] valuesList = values.split(",");
            String[] connectionsList = connections.split(",");
            String[] value2sList = value2s.split(",");
            String[] groupingsList = groupings.split(",");
            String[] userInputSeqsList = userInputSeqs.split(",");
            String[] userInputSeqs2List = userInputSeqs2.split(",");
    		int index = 1;
    		
    		boolean empty = false;
    		if (rowsList.length == 1){
    			String numberList =numbersList[0]; 
    			if (numberList.equals("") || numberList.equals(Constants.UNDEFINED)){
    				//There is no query criteria need to save
    				empty = true;
    				result = "OK";
    			}
    		}
    		
    		//delete all of the old criteria data
			if (saveStateInt == DataObject.IN_DB){
				//need to delete all the old query criteria
				List<QueryCriteriaDobj> queryCriteria = dataAgent.getQueryCriteriaByQueryID(newQueryID, "UR");
				for (QueryCriteriaDobj queryCriteriaDobj : queryCriteria){
					queryCriteriaDobj.delete();
				}
			}    		
    		
    		if (!empty){
                for (int i = 0; i< rowsList.length; i++){
                	String id = idsList[i];
                	String row = rowsList[i];
                	String type = typesList[i];
                	String used = usedsList[i];
                	String indent = indentsList[i];
                	String valueOrCondition = valueOrConditionsList[i];
                	
                	String parent = null;
                	if (parentsList != null && parentsList.length == rowsList.length)
                		parent = parentsList[i];
                	
                	String number = null;
                	if (numbersList != null && numbersList.length == rowsList.length)
                		number = numbersList[i];                	
                	
                	String field = fieldsList[i];
                	String compID = compIDsList[i];
                	String comparison = comparisonsList[i];
                	String value = valuesList[i];               	
                	String connection = connectionsList[i];
                	String value2 = value2sList[i];               	
                	String grouping = groupingsList[i];
                	
                	
                	String userInputSeq = null;
                	if (userInputSeqsList != null && userInputSeqsList.length > i)
                		userInputSeq = userInputSeqsList[i];
                	
                	String userInputSeq2 = null;
                	if (userInputSeqs2List != null && userInputSeqs2List.length > i)
                		userInputSeq2 = userInputSeqs2List[i];
                	
                	// create a Query Criteria object
                	QueryCriteriaDobj queryCriteriaObj = new QueryCriteriaDobj();
                	
                	int queryCriteriaSaveState = DataObject.NEW;

                	queryCriteriaObj.setQueryId(newQueryID);
                	queryCriteriaObj.setValueOrCondition(valueOrCondition);
                	int rowInt;
					try {
						rowInt = Integer.parseInt(row);
					} catch (NumberFormatException e) {
						rowInt = index;
					}
                    queryCriteriaObj.setRowNo(rowInt);
                    queryCriteriaObj.setType(type);
                    queryCriteriaObj.setUsed(Boolean.valueOf(used).booleanValue() ? 1: 0);
                    queryCriteriaObj.setIndent(Integer.parseInt(indent));
                    queryCriteriaObj.setParent(parent == null ? "" : parent);
                    queryCriteriaObj.setNumber(number == null ? 0 : Integer.parseInt(number));
                    queryCriteriaObj.setName(field);
                    queryCriteriaObj.setCompId(Integer.parseInt(compID));
                    queryCriteriaObj.setComparison(comparison);
                    queryCriteriaObj.setValue(value);
                    queryCriteriaObj.setConnection(connection);
                    queryCriteriaObj.setValue2(value2);
                    queryCriteriaObj.setGrouping(grouping);
                	queryCriteriaObj.setValueUserInputSeq(userInputSeq);
                	queryCriteriaObj.setValue2UserInputSeq(userInputSeq2);
                	queryCriteriaObj.setObjectType("UR");
                    queryCriteriaObj.setState(queryCriteriaSaveState);
                    queryCriteriaObj.save();
                    index++;
                }        			
    		}
            result = MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL);
		} catch (MdnException e1) {
			e1.printStackTrace();
			result = MessageConstants.getMessage(file, MessageConstants.DB_ERROR);
		} catch (DataSourceException e) {
			e.printStackTrace();
			result = MessageConstants.getMessage(file, MessageConstants.DB_ERROR);
		}			
		
	    String action = request.getParameter("action");
	    return new XmlFormatter().editResult(file, action, result);
	}	

	private Element displayUserReplyMessagingInfo(HttpServletRequest request){
		//  Get the language file.
		String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
		int id = Integer.parseInt(request.getParameter("urId"));
		
	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	    String responseTxt;
	    UserReply ur = new UserReply();
		try {
		    if(id != -1){
				responseTxt = MessageConstants.getMessage(file, MessageConstants.NO_RESPONSE_FORMAT);
				ur = dataAgent.getUserReplyrMsgInfoByID(id);
				
				//Manage Response format
				String resTxt = ur.getResponse();
				int viewID = ur.getViewOrTableId();
				String queryType = ur.getType();
				if(resTxt!=null && !resTxt.equals(""))
					responseTxt = resTxt;
				else{
					DataView view = dataAgent.getMetaViewByID(viewID, true);
	
			    	if(queryType.equals(Constants.SELECT_QUERY_TYPE)){//Select
				    	String replyingMsg = "";
			        	Vector titles = new Vector();
			        
		        		titles = view.getFields();
		        	
		        		for(int col = 0; col < titles.size(); col++)
		        		{
		        			String filedName = titles.get(col).toString();
		        			DataViewField fieldObj = (DataViewField)titles.get(col);
		        			replyingMsg += fieldObj.getDisplayName() +" : %" + filedName + "% \n";
		        		}
		        		responseTxt = replyingMsg;
			        }else{
			        	responseTxt = MessageConstants.getMessage(file, MessageConstants.NO_RESULT_TEST_RESPONSE);
					}
				}
				
			if(responseTxt.equals(""))
				responseTxt = MessageConstants.getMessage(file, MessageConstants.NO_RESULT_TEST_RESPONSE);
		    }else{
		    	ur = new UserReply();
		    	ur.setId(id);
				responseTxt = "";
		    }
		} catch (MdnException e) {
			responseTxt = "";
			e.printStackTrace();
		} 
		return new XmlFormatter().displayUrMessagingInfoXML(file, ur, responseTxt);
   }
	
	private Element saveUrMsgInfo(HttpServletRequest request){
		String saveState = request.getParameter("saveState");
		int saveStateInt = Integer.parseInt(saveState);
		String urId = request.getParameter("urId");

	    String responseFormat = request.getParameter("responseFormat");
	    String displayResult = request.getParameter("displayResult");
	    
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		UserReply ur = new UserReply();
		String result = "OK";
    	if(result.equalsIgnoreCase("OK")){
    		try {
				if (saveStateInt == DataObject.IN_DB)
					ur = dataAgent.getUserReplyById(Integer.parseInt(urId));
				
		    	if(responseFormat != null && !responseFormat.equals(Constants.UNDEFINED))
		    		ur.setResponse(responseFormat);
		    	else
		    		ur.setResponse("");

	        	try{
	        		ur.setDisplayResult(Integer.parseInt(displayResult));
	        	}catch(NumberFormatException e){
	        		ur.setDisplayResult(1);
	        	}
		    	
		    	ur.setState(saveStateInt);
		    	ur.setDelStatus(Constants.MARKED_AS_NOT_DELETED_INT);		    	
				ur.save();
			} catch (NumberFormatException e1) {
				result = "Database Error";
				e1.printStackTrace();
			} catch (MdnException e1) {
				result = "Database Error";
				e1.printStackTrace();
			} catch (DataSourceException e) {
				result = "Database Error";
				e.printStackTrace();
			}
    	}
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
		return new XmlFormatter().saveUrMsgInfoXML(file, result, ur);	  
	}	

	private Element testURMessaginResult(HttpServletRequest request){
		//  Get the language file.
		String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
		int id = Integer.parseInt(request.getParameter("urId"));
		
	    String responseFormat = request.getParameter("responseFormat");
	    String enterFormatMessage = request.getParameter("enterFormatMessage");
	    String displayResult = request.getParameter("displayResult");
	    
	    String projectId = request.getParameter("projectId");
	    int intProjectId = 1;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e1) {
			intProjectId = 1;
		}	    
	    
		String result = "OK";
		String replyContext = "";

		String sql;
		int viewID = 0;
		String queryType;
		
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		DataView view;
		UserReply ur = new UserReply();
		try {
			String separator = " ";
			MdnMessageSeparator mdnMsgSep = dataAgent.getMessageSeparator();
			if(mdnMsgSep != null){
				separator = mdnMsgSep.getConditionSeperator();
			}
			
		    if(id != -1){
		    	ur = (UserReply) dataAgent.getUserReplyById(id);
				sql = ur.getCriteriaString();
				viewID = ur.getViewOrTableId();
				queryType = ur.getType();
				if(!ur.getType().equalsIgnoreCase(Constants.SELECT_QUERY_TYPE))
					result = MessageConstants.getMessage(file, MessageConstants.NO_SELECT_QUERY); 
		    }else{
		    	sql = request.getParameter("sql");
		    	String viewIdStr = request.getParameter("viewID");
		    	queryType = request.getParameter("queryType");
		    	String sort = request.getParameter("sort");
		    	String groupId = request.getParameter("groupId");
		    	//queryType = MessagingUtils.getQueryTypeStr(Integer.parseInt(queryTypeNum));
		    	
		    	if(sort != null && !sort.equals("") && !sort.equalsIgnoreCase(Constants.UNDEFINED))
		    		ur.setSortString(sort);
		    	if(groupId != null && !groupId.equals("") && !groupId.equalsIgnoreCase(Constants.UNDEFINED))
		    		ur.setGroupFieldId(Integer.parseInt(groupId));

		    	if(!queryType.equalsIgnoreCase(Constants.SELECT_QUERY_TYPE))//(Integer.parseInt(queryTypeNum) != Constants.SELECT_QUERY_TYPE_NUM)
		    		result = MessageConstants.getMessage(file, MessageConstants.NO_SELECT_QUERY);//result = "Query type isn't Select";
		    	else if(sql == null || sql.equals("") || sql.equals(Constants.UNDEFINED))
		    		result = "Missing SQL criteria";
		    	else if(viewIdStr == null || viewIdStr.equals("") || viewIdStr.equals(Constants.UNDEFINED))
		    		result = "Missing Selected View";
		    	else
		    		viewID = Integer.parseInt(viewIdStr);
		    	
		    	ur.setCriteriaString(sql);
		    	ur.setViewOrTableId(viewID);
		    	ur.setType(queryType);
		    	
		    }
			if(result.equals("OK"))
			{
				ArrayList<String> userInputsList = null;
				if(enterFormatMessage != null && !enterFormatMessage.equals("") && !enterFormatMessage.equals(Constants.UNDEFINED) && ur.getCriteriaString().contains("UserInput")){
					userInputsList = MessagingUtils.userInputsParserListUr(separator, enterFormatMessage);
				}
				
				RecordSet rs = null;
				if(id != -1){
					 if(queryType.equalsIgnoreCase(Constants.SELECT_QUERY_TYPE)){
						 if(userInputsList!= null && !userInputsList.isEmpty())
							rs = dataAgent.getSelectQueryResultWithUserInput(null, ur, userInputsList, "UR", -1);
						else{
							view = dataAgent.getMetaViewByID(viewID, true);
							int dbId =  view.getSourceDsId();
							DataViewDataSource dvds = MdnDataManager.getDataViewDS();
							rs = dvds.execDirectSQL(dbId, sql, view);
						}
					}
//					 else if(queryType.equalsIgnoreCase(Constants.INSERT_QUERY_TYPE)){
//						int resultInt = dataAgent.getInsertQueryResultWithUserInput(null, ur, userInputsList, "UR");//0 = Faild
//						if(resultInt > 0 && responseFormat != null && !responseFormat.equals("") && !responseFormat.equals("undefined"))
//							replyContext = responseFormat;
//					}else if(queryType.equalsIgnoreCase(Constants.UPDATE_QUERY_TYPE)){
//						int resultInt = dataAgent.getUpdateQueryResultWithUserInput(null, ur, userInputsList, "UR");//0 = Faild
//						if(resultInt > 0 && responseFormat != null && !responseFormat.equals("") && !responseFormat.equals("undefined"))
//							replyContext = responseFormat;
//					}
				}else{
					DataViewDataSource dvds = MdnDataManager.getDataViewDS();
					view = dataAgent.getMetaViewByID(viewID, true);
					int dbId =  view.getSourceDsId();
					dvds = MdnDataManager.getDataViewDS();
					if(userInputsList!= null && !userInputsList.isEmpty() && sql.contains("UserInput")){
						String sqlParsed = sqlParser(request, view, userInputsList);
						if(!sqlParsed.equals(""))
							sql = sqlParsed;
					}

					rs = dvds.execDirectSQL(dbId, sql, view);
				}
				if( rs!= null ) {
					Vector rows = rs.getRows();

					int displayResultInt;
					try{
						displayResultInt = Integer.parseInt(displayResult);
					} catch (NumberFormatException e1) {
						displayResultInt = 1;
					}	
					
					Vector titles = new Vector();
					if(rows != null && rows.size() > 0)
						titles = ((DataObject)rows.elementAt(0)).getEntity().getFields();
		
					if( rows != null && displayResultInt > rows.size())
						displayResultInt = rows.size();
		    
					for(int k = 0; rows != null && k < displayResultInt; k++) {
						// get the data object
						DataObject obj = (DataObject)rows.elementAt(k);
		        		// get the data object
						replyContext += MessagingUtils.responseMassageParser(responseFormat, titles, obj) + "\n";
					}
		    	}
			}
		} catch (MdnException e) {
			e.printStackTrace();
		} catch (DataSourceException e) {
			e.printStackTrace();
		}

		if(replyContext.equals(""))
			replyContext = "There are no results for this query";
		
		return new XmlFormatter().testURMessaginResultXML(file, ur, replyContext, result, responseFormat);		
	}
	
	private Element deleteQuery(HttpServletRequest request){
	    String queryID = request.getParameter("queryID");
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		QueryDobj query = null;
		try {
			query = dataAgent.getQueryByID(Integer.parseInt(queryID));
			query.delete();
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (MdnException e1) {
			e1.printStackTrace();
		} catch (DataSourceException e) {
			e.printStackTrace();
		}
	    return displayEmptyMsg(request);
	}	
	
	private Element deleteUR(HttpServletRequest request){
		String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
		
	    String urId = request.getParameter("urId");
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		UserReply ur = null;
		int parentId = 0;
	    String qmParentName= "";
	    String qmParentDesc= "";
		
		try {
			ur = dataAgent.getUserReplyById(Integer.parseInt(urId));
			//ur.setDelStatus(Constants.MARKED_AS_RECYCLED_INT);
			parentId = ur.getParentId();
			//ur.save();

			ur.delete();
			
	    	//Get project ID	    	
		    String projectId = request.getParameter("projectId");
		    int intProjectId = 1;
			try {
				intProjectId = Integer.parseInt(projectId);
			} catch (NumberFormatException e1) {
				intProjectId = 1;
			}	    
	    
			QueryDobj queryMsgParent = dataAgent.getQueryByID(parentId);
			if(queryMsgParent != null){
			    qmParentName= queryMsgParent.getName();
			    qmParentDesc= queryMsgParent.getDescription();
			} else {
				UserReply userReply = dataAgent.getUserReplyById(parentId);
			    qmParentName= userReply.getMsgText();
			    qmParentDesc= userReply.getDescription();
			    if(qmParentName == null || qmParentName.equals(""))
			    	qmParentName = "-";
			    if(qmParentDesc == null || qmParentDesc.equals(""))
			    	qmParentDesc= "-";
			}
		    		
		    databases = new ArrayList<DataSourceDobj>();
		    int selectedQuerytype = 1;	    
		    vtLable = "";
			if(selectedQuerytype == 1)//Select
				vtLable = MessageConstants.getMessage(file, MessageConstants.SELECT_VIEW_LABEL);			
			else//Update or Select
				vtLable = MessageConstants.getMessage(file, MessageConstants.SELECT_TABLE_LABEL);

		    // Get the queries from the database.
	    	databases = dataAgent.getAllDbConnections(intProjectId);

		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (MdnException e1) {
			e1.printStackTrace();
		} catch (DataSourceException e) {
			e.printStackTrace();
		}
		return new XmlFormatter().emptyUserReplyXML(databases, vtLable, String.valueOf(parentId), qmParentName, qmParentDesc);
	}	

	private Element deleteEmailSett(HttpServletRequest request){
	    Element root = new Element("root");
	    String emailId = request.getParameter("emailId");
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		MdnEmailSetting email = null;
		try {
			email = dataAgent.getEmailSettingById(Integer.parseInt(emailId));
			email.setDelStatus(Constants.MARKED_AS_RECYCLED_INT);
			email.save();

			List<QueryDobj> queriesList = dataAgent.geQueryByEmailId(Integer.parseInt(emailId));
			for (QueryDobj q : queriesList) {
				q.setEmailAddressId(-1);
				q.setEmailKeyword(null);
				q.setState(DataObject.IN_DB);
				q.save();
			}
			
			
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (MdnException e1) {
			e1.printStackTrace();
		} catch (DataSourceException e) {
			e.printStackTrace();
		}
		return root;
	}
	
	private Element getMsgControls(HttpServletRequest request){
	    // Get the user from the database.
	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	    boolean limitation = dataAgent.getPublicGroupFlag();//Unlimited = true, limited = false
	    
	    TemporaryBlockContacInfo msgControlsInfo = null;
	    MessagingSettingDetails imPublicMsgInfo = null;
	    MessagingSettingDetails smsPublicMsgInfo = null;
	    MessagingSettingDetails emailPublicMsgInfo = null;
	    MessagingSettingDetails smppPublicMsgInfo = null;
	    List<BlockContacts> blockContacts = null;
	    int availablePublicMessages = 0;
	    try {
	    	msgControlsInfo = dataAgent.getTempBlockContacts(); 
		    imPublicMsgInfo = dataAgent.getGuestMsgInfo("IM");
		    smsPublicMsgInfo = dataAgent.getGuestMsgInfo("SMS");
		    emailPublicMsgInfo = dataAgent.getGuestMsgInfo("Email");
		    smppPublicMsgInfo =  dataAgent.getGuestMsgInfo("SMPP");
	    	blockContacts = dataAgent.getBlockContacts();
	    	
	    	availablePublicMessages = dataAgent.getAvailablePublicMessages();
	    	
	    } catch (MdnException e) {
			e.printStackTrace();
		}
	    
	    return new XmlFormatter().getMsgControls(blockContacts, imPublicMsgInfo, emailPublicMsgInfo, smsPublicMsgInfo, smppPublicMsgInfo, limitation, msgControlsInfo, availablePublicMessages);
	}	

	private Element saveBlockContact(HttpServletRequest request){
		String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
		String result = MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL);
		
		String type = request.getParameter("type");
		String contact = request.getParameter("contact");
		String id = request.getParameter("id");
		try {		
			String mode;
			IDataAgent dataAgent = DataAgentFactory.getDataInterface();
			BlockContacts blockContact;
			if(id == null || id.equals("") || id.equals(Constants.UNDEFINED)){//add
				mode = "add";
				blockContact = new BlockContacts();
				blockContact.setType(type);
				
				/* Add Validation */
				if(type == null || type.equals("") || type.equals(Constants.UNDEFINED))
					result = MessageConstants.getMessage(file, MessageConstants.MISSING_TYPE);				
				else if(contact == null || contact.equals("") || contact.equals(Constants.UNDEFINED))
					result = MessageConstants.getMessage(file, MessageConstants.MISSING_CONTACT);
				else if(dataAgent.getBlockContact(type, contact) != null)
					result = MessageConstants.getMessage(file, MessageConstants.DUPLICATE_BLOCK_CONTACT);				
			}else{//edit or delete
				mode="edit";
				int idInt = Integer.valueOf(id);
				blockContact = dataAgent.getBlockContact(idInt);
				type = blockContact.getType();
				if((contact == null || contact.equals("") || contact.equals(Constants.UNDEFINED))){
					mode="delete";
				}
			}
				
			if(result.equalsIgnoreCase(MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL))) {//result == OK
				if(mode.equalsIgnoreCase("delete")){
					blockContact.delete();
				}else{
					contact = contact.trim();
					if(type.equals("3") && !contact.contains("+"))
						contact = "+" + contact;
					blockContact.setContact(contact);
					blockContact.save();
				}
			}
		} catch (DataSourceException e) {
			e.printStackTrace();
		} catch (MdnException e) {
			e.printStackTrace();
		}
		// Get the user from the database.
	    return new XmlFormatter().setResult(result);
	}		
	

	private Element saveMsgControl(HttpServletRequest request){
//		String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
//		String result = MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL);
		
		String defultReply = "too many messages. Please try again later.";
		
		String maxMsg = request.getParameter("maxMsg");
		String forPeriod = request.getParameter("forPeriod");
		String cancelAfter = request.getParameter("cancelAfter");
		String blockReply = request.getParameter("blockReply");
		
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		try {
			TemporaryBlockContacInfo msgControlsInfo = dataAgent.getTempBlockContacts();
			if(maxMsg != null && !maxMsg.equals("") && !maxMsg.equals(Constants.UNDEFINED)){
				try{
					Integer.parseInt(maxMsg);
					msgControlsInfo.setMaxMessage(Integer.parseInt(maxMsg));
				}catch (NumberFormatException e) {
				}				
			}
			if(forPeriod != null && !forPeriod.equals("") && !forPeriod.equals(Constants.UNDEFINED)){
				try{
					Integer.parseInt(forPeriod);
					msgControlsInfo.setMaxPeriod(forPeriod);
				}catch (NumberFormatException e) {
				}
			}
			if(cancelAfter != null && !cancelAfter.equals("") && !cancelAfter.equals(Constants.UNDEFINED)){
				try{
					Integer.parseInt(cancelAfter);
					msgControlsInfo.setCancelPeriod(cancelAfter);	
				}catch (NumberFormatException e) {
				}				
			}
			if(blockReply != null && !blockReply.equals("") && !blockReply.equals(Constants.UNDEFINED))
				msgControlsInfo.setReply(blockReply);
			else
				msgControlsInfo.setReply(defultReply);
			
			msgControlsInfo.setState(DataObject.IN_DB);
			msgControlsInfo.save();
			
		} catch (MdnException e) {
			e.printStackTrace();
		} catch (DataSourceException e) {
			e.printStackTrace();
		}
		
		return new Element("root");	
	}
	private Element displaySearchKeywords(HttpServletRequest request){
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		String term = "Help";
		try {
			MessagingSettingDetails msgSettInfo = dataAgent.getGuestMsgInfo("Email");
			term = msgSettInfo.getSearchTerm();
		} catch (MdnException e) {
			e.printStackTrace();
		}
		return new XmlFormatter().getSearchTerm(term);
	}
	
	private Element saveSearchTerm(HttpServletRequest request){
		
		String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
		String result = MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL);
		
		String searchTerm = request.getParameter("term");
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		try {
			if(searchTerm == null || searchTerm.trim().equals("") || searchTerm.trim().contains(" ") )
				result = MessageConstants.getMessage(file, MessageConstants.NO_SPACE);
			else {
				QueryDobj emailKey = dataAgent.getQueryByEmailKeyword(searchTerm);
				if(emailKey != null)
					result = MessageConstants.getMessage(file, MessageConstants.INVALID_SEARCH_EMAIL);
				else {
					List<QueryDobj> smsKey = dataAgent.getQueryBySmsKeyword(searchTerm); 
					if(!smsKey.isEmpty()) 
						result = MessageConstants.getMessage(file, MessageConstants.INVALID_SEARCH_SMS);
					else {
						QueryDobj imKey = dataAgent.getQueryByIMKeyword(searchTerm);
						if(imKey != null)
							result = MessageConstants.getMessage(file, MessageConstants.INVALID_SEARCH_IM);
					}
				}
			}

			if (result.equalsIgnoreCase((MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL)))){
				MessagingSettingDetails msgSettInfo = dataAgent.getGuestMsgInfo("Email");
				msgSettInfo.setSearchTerm(searchTerm);
				msgSettInfo.save();
			}
			
		} catch (MdnException e) {
			e.printStackTrace();
		} catch (DataSourceException e) {
			e.printStackTrace();
		}
		return new XmlFormatter().setResult(result);
	}
	
	private Element testSearchTerm(HttpServletRequest request){
		String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
		String result;
		
		String type = request.getParameter("type");
		String userInput = request.getParameter("ui");
		
			if(userInput == null || userInput.trim().equals("") || userInput.trim().equals(Constants.UNDEFINED) ){
				result = MessagingUtils.getAllQueriesList(type, file);
			} else {
				result = MessagingUtils.searchQueryKeyword(type, file, userInput);
			}

		return new XmlFormatter().searchQueryResultXml(result);
	}
	
	private Element displaySmpp(HttpServletRequest request){
		MdnSmpp smsGateway = new MdnSmpp();
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		String id = request.getParameter("id");
		try {
			smsGateway = dataAgent.getSmsGatewayByID(Integer.parseInt(id));
		} catch (MdnException e) {
			e.printStackTrace();
		}
		
		return new XmlFormatter().displaySmsGateway(smsGateway, null, null, "false");
	}
	
	private Element saveSmpp(HttpServletRequest request){
		String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
		String result = MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL_SMPP);
		
		String id = request.getParameter("id");
		String number = request.getParameter("number");
		String host = request.getParameter("host");
		String port = request.getParameter("port");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		String sourceNpi = request.getParameter("npi");
		String sourceTon = request.getParameter("ton");
		String destNpi = request.getParameter("destNpi");
		String destTon = request.getParameter("destTon");
		String bindNpi = request.getParameter("bindNpi");
		String bindTon = request.getParameter("bindTon");
		
		String interval = request.getParameter("checkTime");
		String useTlv = request.getParameter("useTlv");
		String useAddrRange = request.getParameter("useAddrRange");
		
		String isTreeChanged = "false";
		String response = null;
		MdnSmpp smsGateway = null;
		MdnSmsSetting gsm = null;
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		try {
			if(number!= null && !number.equals("")){
				if(!number.contains("+"))
					number = "+" + number.trim();
				
				smsGateway = dataAgent.getSmsGatewayByNumber(number);
				gsm = dataAgent.getSmsSetting();
			}
			
			if(number == null || number.equals("") || number.equals(Constants.UNDEFINED)){
				result = MessageConstants.getMessage(file, MessageConstants.MISSING_NUMBER);
			}else if(smsGateway != null && smsGateway.getId() != Integer.parseInt(id)){
				result = MessageConstants.getMessage(file, MessageConstants.DUPLICATE_NUMBER);
			}else if(gsm != null && gsm.getNumber().equals(number)){
				result = MessageConstants.getMessage(file, MessageConstants.GSM_NUMBER);
			}else if(host == null || host.equals("") || host.equals(Constants.UNDEFINED)){
				result = MessageConstants.getMessage(file, MessageConstants.MISSING_HOST);
			}else if(port == null || port.equals("") || port.equals(Constants.UNDEFINED)){
				result = MessageConstants.getMessage(file, MessageConstants.MISSING_PORT);
			}else if(username == null || username.equals("") || username.equals(Constants.UNDEFINED)){
				result = MessageConstants.getMessage(file, MessageConstants.MISSING_USERNAME);
			}else if(password == null || password.equals("") || password.equals(Constants.UNDEFINED)){
				result = MessageConstants.getMessage(file, MessageConstants.MISSING_PASSWORD);
			}else{//Validation is correct
				if(id == null || id.equals("") || id.equals(Constants.UNDEFINED) || id.equals("-1")){//ADD
					smsGateway = new MdnSmpp();
					smsGateway.setState(DataObject.NEW);
					response = "OK";
					isTreeChanged = "true";
				}else{//EDIT
					smsGateway = dataAgent.getSmsGatewayByID(Integer.parseInt(id));
					
					smsGateway.setState(DataObject.IN_DB);
					if(!smsGateway.getNumber().equalsIgnoreCase(number))
						isTreeChanged = "true";
				}
				if(!number.contains("+"))
					number = "+" + number.trim();
				
				if(sourceTon == null || sourceTon.equals("") || sourceTon.equals(Constants.UNDEFINED))
					sourceTon = "";
				if(sourceNpi == null || sourceNpi.equals("") || sourceNpi.equals(Constants.UNDEFINED))
					sourceNpi = "";
				
				if(destTon == null || destTon.equals("") || destTon.equals(Constants.UNDEFINED))
					destTon = "";
				if(destNpi == null || destNpi.equals("") || destNpi.equals(Constants.UNDEFINED))
					destNpi = "";
				
				if(bindTon == null || bindTon.equals("") || bindTon.equals(Constants.UNDEFINED))
					bindTon = "";
				if(bindNpi == null || bindNpi.equals("") || bindNpi.equals(Constants.UNDEFINED))
					bindNpi = "";				
				
				smsGateway.setNumber(number.trim());
				smsGateway.setHost(host);
				smsGateway.setPort(port);
				smsGateway.setUsername(username);
				smsGateway.setPassword(password);
				
				smsGateway.setSourceNPI(sourceNpi);
				smsGateway.setSourceTON(sourceTon);
				smsGateway.setDestNPI(destNpi);
				smsGateway.setDestTON(destTon);
				smsGateway.setBindNPI(bindNpi);
				smsGateway.setBindTON(bindTon);
				
				smsGateway.setDelStatus(Constants.MARKED_AS_NOT_DELETED_INT);
				
				if(useTlv == null || useTlv.equals("") || useTlv.equals(Constants.UNDEFINED))
					useTlv = "false";
				
				if(useTlv.equalsIgnoreCase("false"))
					smsGateway.setUseTlv(0);
				else
					smsGateway.setUseTlv(1);
				
				if(useAddrRange == null || useAddrRange.equals("") || useAddrRange.equals(Constants.UNDEFINED))
					useAddrRange = "false";
				
				if(useAddrRange.equalsIgnoreCase("false"))
					smsGateway.setUseAddrRange(0);
				else
					smsGateway.setUseAddrRange(1);				
				
				if(interval == null || interval.equals("") || interval.equals(Constants.UNDEFINED))
					smsGateway.setInterval(0);
				else
					smsGateway.setInterval(Integer.parseInt(interval));

				smsGateway.save();				
			}
		} catch (DataSourceException e) {
			e.printStackTrace();
			response = "Faild";
		} catch (MdnException e) {
			e.printStackTrace();
			response = "Faild";
		}			
		return new XmlFormatter().displaySmsGateway(smsGateway, result, response, isTreeChanged);
	}
	
	private Element recycleSmpp(HttpServletRequest request){
	    Element root = new Element("root");
	    String smppId = request.getParameter("smppId");
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		MdnSmpp smpp = null;
		try {
			smpp = dataAgent.getSmsGatewayByID(Integer.parseInt(smppId));
			smpp.setDelStatus(Constants.MARKED_AS_RECYCLED_INT);
			smpp.save();

			//Delete all queries which are related to this smpp id
			
			List<QueryDobj> queriesList = dataAgent.getAllQueriesBySmsServerId(Integer.parseInt(smppId));
			for (QueryDobj q : queriesList) {
				q.setMobileStatus(-1);
				q.setSmsKeyword(null);
				q.setState(DataObject.IN_DB);
				q.save();
			}
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (MdnException e1) {
			e1.printStackTrace();
		} catch (DataSourceException e) {
			e.printStackTrace();
		}
		return root;
	}
	
	private Element displayPubMsgLog(HttpServletRequest request){
		Element root = new Element("root");
		return root;
	}
	
}


