package com.framedobjects.dashwell.utils;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import javax.sql.rowset.JdbcRowSet;
import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;

import org.apache.ecs.html.Body;
import org.apache.ecs.xml.XML;
import org.apache.ecs.xml.XMLDocument;
import org.apache.log4j.Logger;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import sun.tools.javap.Tables;

import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.Field;
import wsl.fw.datasource.RecordSet;
import wsl.fw.exception.MdnException;
import wsl.fw.security.Feature;
import wsl.fw.security.Group;
import wsl.fw.security.GroupMembership;
import wsl.fw.security.User;
import wsl.mdn.dataview.DataSourceDobj;
import wsl.mdn.dataview.DataView;
import wsl.mdn.dataview.DataViewField;
import wsl.mdn.dataview.DirectQueryDobj;
import wsl.mdn.dataview.EntityDobj;
import wsl.mdn.dataview.FieldDobj;
import wsl.mdn.dataview.GroupDataView;
import wsl.mdn.dataview.GroupTablePermission;
import wsl.mdn.dataview.JdbcDataSourceDobj;
import wsl.mdn.dataview.JdbcDriver;
import wsl.mdn.dataview.JoinDobj;
import wsl.mdn.dataview.LanguageDobj;
import wsl.mdn.dataview.ProjectDobj;
import wsl.mdn.dataview.QueryCriteriaDobj;
import wsl.mdn.dataview.QueryDobj;
import wsl.mdn.dataview.Record;
import wsl.mdn.dataview.ResultWrapper;
import wsl.mdn.dataview.UserLicense;
import wsl.mdn.dataview.UserLicenses;
import wsl.mdn.dataview.WebServiceDescription;
import wsl.mdn.dataview.WebServiceDetail;
import wsl.mdn.dataview.WebServiceDobj;
import wsl.mdn.dataview.WebServiceOperationDobj;
import wsl.mdn.mdnim.IMConnection;
import wsl.mdn.mdnim.IMContact;
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

import com.framedobjects.dashwell.biz.IMConactDetailes;
import com.framedobjects.dashwell.biz.RecycleBinItem;
import com.framedobjects.dashwell.db.DataAgentFactory;
import com.framedobjects.dashwell.db.IDataAgent;
import com.framedobjects.dashwell.db.meta.MetaField;
import com.framedobjects.dashwell.db.meta.MetaRelation;
import com.framedobjects.dashwell.db.meta.MetaTable;
import com.framedobjects.dashwell.utils.MessagingUtils;
import com.framedobjects.dashwell.utils.webservice.IParamView;
import com.framedobjects.dashwell.utils.webservice.ParamListItem;
import com.framedobjects.dashwell.utils.webservice.WebServiceResultRow;

public class XmlFormatter {
  
  private static Logger logger = Logger.getLogger(XmlFormatter.class.getName());
  
  private final static String ROOT_CSSS = "csss";
  private final static String ROOT_DB = "db";
  private final static String ROOT_NAVIGATION = "navigation";
  private final static String ROOT_MESSAGE = "msg";
  private final static String ROOT_ROOT = "root";
  private final static String ROOT_SCREENS = "screens";
  
  private final static String ATTRIBUTE_DISPLAY_NAME = "displayname";
  private final static String ATTRIBUTE_DRAGGABLE = "_draggable";
  private final static String ATTRIBUTE_DRAGTARGET = "_dragtarget";
  private final static String ATTRIBUTE_DRIVER = "driver";
  private final static String ATTRIBUTE_FONTSTYLE = "_fontstyle";
  private final static String ATTRIBUTE_HEIGHT = "height";
  private final static String ATTRIBUTE_ICON_NAME = "_iconstate";
  private final static String ATTRIBUTE_ID = "id";
  private final static String ATTRIBUTE_MIRRORRED = "mirrorred";
  private final static String ATTRIBUTE_NAME = "name";
  private final static String ATTRIBUTE_OPTTREE_OPEN = "__OPTTREE_META_open";
  private final static String ATTRIBUTE_PASSWORD = "password";
  private final static String ATTRIBUTE_TYPE = "type";
  private final static String ATTRIBUTE_URL = "url";
  private final static String ATTRIBUTE_USERNAME = "username";
  private final static String ATTRIBUTE_WIDTH = "width";
  private final static String ATTRIBUTE_VALUE = "value";
  
  private final static String ELEMENT_CSS = "css";
  private final static String ELEMENT_DB_ERROR = "db-error";
  private final static String ELEMENT_NODE = "node";
  private final static String ELEMENT_OK = "ok";
  private final static String ELEMENT_USERNAME = "username";
  
  private final static String ELEMENT_NAV_DATABASE = "database";
  private final static String ELEMENT_NAV_DATABASES = "databases";
  private final static String ELEMENT_NAV_HOME = "home";
  private final static String ELEMENT_NAV_PROJECTS = "projects";
  
  private final static String ELEMENT_SCREEN = "screen";
  private final static String ELEMENT_UNDEFINED_ACTION = "undefinedAction";
  
  private final static String FONTSTYLE_BOLD = "bold";
  private final static String FONTSTYLE_BOLD_ITALIC = "bolditalic";
  private final static String FONTSTYLE_PLAIN = "plain";
  
  private final static String TYPE_CONNECTION = "connection";
  private final static String TYPE_DATABASE = "database";
  private final static String TYPE_DATABASES = "databases";
  private final static String TYPE_ADD_DATABASE = "add-database";
  private final static String TYPE_DEPLOYMENT = "deployment";
  private final static String TYPE_DRIVERS = "drivers";
  private final static String TYPE_FIELD = "field";
  private final static String TYPE_GROUP = "group";
  private final static String TYPE_GROUPS = "groups";
  private final static String TYPE_LANGUAGE = "language";
  private final static String TYPE_NOTIF_EMAIL = "notif-email";
  private final static String TYPE_PROJECT = "project";
  private final static String TYPE_PROJECTS = "projects";
  private final static String TYPE_TABLE = "table";
  private final static String TYPE_USER = "user";
  private final static String TYPE_USERS = "users";
  private final static String TYPE_VIEWS = "views";
  private final static String TYPE_ADD_VIEW = "Add View";
  private final static String TYPE_VIEW = "view";
  private final static String TYPE_WEB_SERVICES = "web-services";
  private final static String TYPE_ADD_WEB_SERVICE = "add-web-service";
  private final static String TYPE_WEB_SERVICE_QUERY = "web-service-query";
  private final static String TYPE_WEB_SERVICE_OPERATION = "web-service-operation";
    // field types
  private final static String TYPE_CURRENCY = "Currency";
  private final static String TYPE_DATE_TIME = "DateTime";
  private final static String TYPE_DECIMAL = "Decimal";
  private final static String TYPE_INTEGER = "Integer";
  private final static String TYPE_STRING = "String";
  private final static String TYPE_BOOLEAN = "Boolean";
  private final static String TYPE_UNKNOWN = "Unknown";
    
  private final static String ATTRIBUTE_DATE_TIME = "date";
  
  public Element undefinedAction(){
    Element root = new Element(ROOT_MESSAGE);
    root.addContent(ELEMENT_UNDEFINED_ACTION);
    return root;
  }
  
  public Element dbError(){
    Element root = new Element(ROOT_MESSAGE);
    root.addContent(ELEMENT_DB_ERROR);
    return root;
  }

  public Element validLogin(String action, User user/*, int numUsers, int daysLeft*/){
    Element root = new Element(ROOT_ROOT);
    //root.addContent(ELEMENT_OK);
    root.setAttribute("action", action);
    root.setAttribute("result", ELEMENT_OK);
    //root.setAttribute("numUsers", "Unlimited");
    //licensee root.setAttribute("daysLeft", String.valueOf(daysLeft));
    if (user != null){
        root.setAttribute(ELEMENT_USERNAME, user.getName() );
        //root.setAttribute(ELEMENT_USER_ID, String.valueOf(user.getId()));   	
    }
    
    /*Element username = new Element(ELEMENT_USERNAME);
    username.addContent(user.getName());
    root.addContent(username);
    
    Element userID = new Element(ELEMENT_USER_ID);
    userID.addContent(String.valueOf(user.getId()));
    root.addContent(userID);
    */
    return root;
  }
  
  public Element invalidLogin(String action, String languageFile, String msg1, String msg2, String msg3){
    Element root = new Element(ROOT_ROOT);
    // Get the GUI definition file.
    Element guiDef = this.getXMLLanguageFile(languageFile);
    // Get the language elements we are interested in.
    String invalidLogin 	= msg1 == null ? "" : getGuiValueForElement(guiDef, msg1).equals("")? msg1 : getGuiValueForElement(guiDef, msg1);
    String invalidLogin2 	= msg2 == null ? "" : getGuiValueForElement(guiDef, msg2).equals("")? msg2 : getGuiValueForElement(guiDef, msg2);
    String invalidLogin3 	= msg3 == null ? "" : getGuiValueForElement(guiDef, msg3).equals("")? msg3 : getGuiValueForElement(guiDef, msg3);
    root.setAttribute("action", action);
    root.setAttribute("result", invalidLogin);
    root.setAttribute("error1", invalidLogin);
    root.setAttribute("error2", invalidLogin2);
    root.setAttribute("error3", invalidLogin3);
    return root;
  }
  
  public Element getUserLicenses(ResultWrapper resultWrapper){
	  Element root = new Element(ROOT_ROOT);
	  /*UserLicenses userLicenses = (UserLicenses)resultWrapper.getObject();
	  String errorMsg = resultWrapper.getErrorMsg();
	  if (errorMsg != null){
		  root.setAttribute("result", "invalid");
		  root.setAttribute("error1", errorMsg);
		  return root;
	  }else if (userLicenses != null){
		  root.setAttribute("result", ELEMENT_OK);
		  String publicMsgDisplay = "";
		  if (!userLicenses.isPublicGroup()){
			  	IDataAgent dataAgent = DataAgentFactory.getDataInterface();  
			  	MessagingSettingDetails imPublicMsgInfo = null;
			    MessagingSettingDetails smsPublicMsgInfo = null;
			    MessagingSettingDetails emailPublicMsgInfo = null;	    
			    try {
			    	imPublicMsgInfo = dataAgent.getGuestMsgInfo("IM");
				    smsPublicMsgInfo = dataAgent.getGuestMsgInfo("SMS");
				    emailPublicMsgInfo = dataAgent.getGuestMsgInfo("Email");
			    } catch (MdnException e) {
					e.printStackTrace();
				}
			    
		    	int leftIM = MessageConstants.limitationCount - imPublicMsgInfo.getTotalMsgCount();
		    	int leftSms = MessageConstants.limitationCount - smsPublicMsgInfo.getTotalMsgCount();
		    	int leftEmail = MessageConstants.limitationCount - emailPublicMsgInfo.getTotalMsgCount();
		    	
		    	if (leftIM <= 0 && leftSms <=0 && leftEmail <=0){
		    		publicMsgDisplay = "Expired";
		    	}else{
		    		//publicMsgDisplay = "Public Messages left: Email:" + leftEmail + " IM: " + leftIM + " SMS: " + leftSms;
		    		publicMsgDisplay = "Trial";
			    }
		  }else{
			  publicMsgDisplay = "Enabled";
		  }
		  root.setAttribute("publicGroup", publicMsgDisplay);
		  for (UserLicense userLicense : userLicenses.getUserLicenses()){
			  Element node = new Element(ELEMENT_NODE);
			  node.setAttribute("type", userLicense.getType());
			  node.setAttribute("colon", ":");
			  node.setAttribute("quantity", String.valueOf(userLicense.getNumberOfUsers()));
			  node.setAttribute("userText", userLicense.getNumberOfUsers() > 1 ? " users " : " user ");
			  node.setAttribute("expiryText", userLicense.getExpiryDate() == null ? "" : " (expiry ");
			  node.setAttribute("expiry", userLicense.getExpiryDate() == null ? "" : userLicense.getExpiryDate());
			  node.setAttribute("expiryText2", userLicense.getExpiryDate() == null ? "" : ") ");
			  root.addContent(node);
		  }
	  }licensee*/
	  return root;
  }
  
  public Element dbConnection(DataSourceDobj dbConnection){
  	Element root = new Element(ROOT_ROOT);
  	
  	Element nodeDbConn = new Element("dbConnection");
  	nodeDbConn.setAttribute(ATTRIBUTE_ID, String.valueOf(dbConnection.getId()));
  	nodeDbConn.setAttribute(ATTRIBUTE_NAME, dbConnection.getName());
  	if (dbConnection instanceof JdbcDataSourceDobj){
  		JdbcDataSourceDobj jdbcDataSourceDobj = (JdbcDataSourceDobj)dbConnection;
  	  	nodeDbConn.setAttribute(ATTRIBUTE_USERNAME, jdbcDataSourceDobj.getJdbcUser() == null ? "" : jdbcDataSourceDobj.getJdbcUser());
  	  	nodeDbConn.setAttribute(ATTRIBUTE_PASSWORD, jdbcDataSourceDobj.getJdbcPassword() == null ? "" : jdbcDataSourceDobj.getJdbcPassword());
  	  	nodeDbConn.setAttribute(ATTRIBUTE_DRIVER, jdbcDataSourceDobj.getJdbcDriver());
  	  	nodeDbConn.setAttribute("driverId", String.valueOf(jdbcDataSourceDobj.getJdbcDriverId()));
  	  	nodeDbConn.setAttribute(ATTRIBUTE_URL, jdbcDataSourceDobj.getJdbcUrl());
  	  	//nodeDbConn.setAttribute(ATTRIBUTE_SCHEMA, jdbcDataSourceDobj.getJdbcCatalog() == null ? "" : jdbcDataSourceDobj.getJdbcCatalog());
  	}

  	nodeDbConn.setAttribute(ATTRIBUTE_MIRRORRED, String.valueOf(dbConnection.isMirrored()));

  	root.addContent(nodeDbConn);
  	
  	return root;
  }
  
  /*public Element duplicateLanguageName(){
  	Element root = new Element(ROOT_ROOT);
  	root.addContent("Duplicate language name.");
  	return root;
  }
  
  public Element languageNameEmpty(){
  	Element root = new Element(ROOT_ROOT);
  	root.addContent("Language file missing.");
  	return root;
  }*/
  
  public Element setResult(String result){
  	Element root = new Element(ROOT_ROOT);
  	root.addContent(result);
  	return root;
  }

  public Element searchQueryResultXml(String result){
	  	Element root = new Element(ROOT_ROOT);
	  	root.setAttribute("result", result);
	  	return root;
  }
  
  public Element saveLanguageResult(String result, LanguageDobj language, String action, String detailAction){
	  	Element root = new Element(ROOT_ROOT);
	  	root.addContent(result);
	  	root.setAttribute("action", action);
	  	root.setAttribute("detailAction", detailAction);
	  	root.setAttribute("id", String.valueOf(language.getId()));
	  	root.setAttribute("name", language.getName());
	  	root.setAttribute("originalFilename", language.getFileName()== null? "" : language.getFileName());
	  	root.setAttribute("filename", language.getFileName()== null? "" : language.getFileName());
	  	root.setAttribute("defaultLang", String.valueOf(language.isDefault()));
	  	return root;
	  }  
  public Element getLanguage(LanguageDobj language, String action){
	  	Element root = new Element(ROOT_ROOT);
	  	root.setAttribute("action", action);
	  	root.setAttribute("id", String.valueOf(language.getId()));
	  	root.setAttribute("name", language.getName());
	  	root.setAttribute("originalFilename", language.getFileName()== null? "" : language.getFileName());
	  	root.setAttribute("filename", language.getFileName()== null? "" : language.getFileName());
	  	root.setAttribute("defaultLang", String.valueOf(language.isDefault()));
	  	return root;
	  }
  
  public Element navigation(){
    Element root = new Element(ROOT_NAVIGATION);
    Element navHome = new Element(ELEMENT_NAV_HOME);
    root.addContent(navHome);
    // Projects.
    root.addContent(new Element(ELEMENT_NAV_PROJECTS));
    // Databases.
    Element dbs = new Element(ELEMENT_NAV_DATABASES);
    Element db1 = new Element(ELEMENT_NAV_DATABASE);
    db1.setAttribute(ATTRIBUTE_NAME, "Northwind");
    dbs.addContent(db1);
    
    Element db2 = new Element(ELEMENT_NAV_DATABASE);
    db2.setAttribute(ATTRIBUTE_NAME, "Bliss");
    dbs.addContent(db2);
    
    root.addContent(dbs);
    return root;
  }
  
  public Element dbTablesAsTree(String dbName){
    Element root = new Element(ROOT_DB);
    //root.setAttribute(ATTRIBUTE_NAME, "Northwind");
    
    Element table = new Element(ELEMENT_NODE);
    table.setAttribute(ATTRIBUTE_NAME, "Customer");
    table.setAttribute(ATTRIBUTE_TYPE, TYPE_TABLE);
    table.setAttribute(ATTRIBUTE_ICON_NAME, "table");
    
    addFieldElementWith2Attributes(table, ELEMENT_NODE, ATTRIBUTE_NAME, "CustomerID", ATTRIBUTE_TYPE, TYPE_FIELD);
    addFieldElementWith2Attributes(table, ELEMENT_NODE, ATTRIBUTE_NAME, "ContactTitle", ATTRIBUTE_TYPE, TYPE_FIELD);
    addFieldElementWith2Attributes(table, ELEMENT_NODE, ATTRIBUTE_NAME, "ContactName", ATTRIBUTE_TYPE, TYPE_FIELD);
    addFieldElementWith2Attributes(table, ELEMENT_NODE, ATTRIBUTE_NAME, "CompanyName", ATTRIBUTE_TYPE, TYPE_FIELD);
    addFieldElementWith2Attributes(table, ELEMENT_NODE, ATTRIBUTE_NAME, "Address", ATTRIBUTE_TYPE, TYPE_FIELD);
    addFieldElementWith2Attributes(table, ELEMENT_NODE, ATTRIBUTE_NAME, "City", ATTRIBUTE_TYPE, TYPE_FIELD);
    addFieldElementWith2Attributes(table, ELEMENT_NODE, ATTRIBUTE_NAME, "PostalCode", ATTRIBUTE_TYPE, TYPE_FIELD);
    addFieldElementWith2Attributes(table, ELEMENT_NODE, ATTRIBUTE_NAME, "Region", ATTRIBUTE_TYPE, TYPE_FIELD);
    addFieldElementWith2Attributes(table, ELEMENT_NODE, ATTRIBUTE_NAME, "Country", ATTRIBUTE_TYPE, TYPE_FIELD);
    addFieldElementWith2Attributes(table, ELEMENT_NODE, ATTRIBUTE_NAME, "Phone", ATTRIBUTE_TYPE, TYPE_FIELD);
    addFieldElementWith2Attributes(table, ELEMENT_NODE, ATTRIBUTE_NAME, "Fax", ATTRIBUTE_TYPE, TYPE_FIELD);
    
    root.addContent(table);
    
    addTableElementWith2Attributes(root, ELEMENT_NODE, ATTRIBUTE_NAME, "Products", ATTRIBUTE_TYPE, TYPE_TABLE);
    addTableElementWith2Attributes(root, ELEMENT_NODE, ATTRIBUTE_NAME, "Categories", ATTRIBUTE_TYPE, TYPE_TABLE);
    addTableElementWith2Attributes(root, ELEMENT_NODE, ATTRIBUTE_NAME, "Category Sales for 1995", ATTRIBUTE_TYPE, TYPE_TABLE);
    addTableElementWith2Attributes(root, ELEMENT_NODE, ATTRIBUTE_NAME, "Current Product List", ATTRIBUTE_TYPE, TYPE_TABLE);
    addTableElementWith2Attributes(root, ELEMENT_NODE, ATTRIBUTE_NAME, "Customers and Suppliers by City", ATTRIBUTE_TYPE, TYPE_TABLE);
    
    return root;
  }
  
  /**
   * Builds the XML for the users and groups tree structure. Caters for both,
   * the standard opttree as well as dragtree.
   * @param users           Collection of users.
   * @param groups          Collection of groups.
   * @param languageFile    The current language definition file.
   * @return                The XML data file.
   */
  public Element userGroupsAsTree(int projectId, Vector<User> users, Vector<Group> groups, String languageFile){
    
    Element guiDef = this.getXMLLanguageFile(languageFile);
    String usersLang = getGuiValueForElement(guiDef, "tree-users");
    String groupsLang = getGuiValueForElement(guiDef, "tree-groups");
    String addUser = getGuiValueForElement(guiDef, "tree-add-user");
    String addGroup = getGuiValueForElement(guiDef, "tree-add-group");
    String userCustomQuery = getGuiValueForElement(guiDef, "tree-user-custom-query");
    
    Element root = new Element(ROOT_ROOT);
    root.setAttribute("projectId", String.valueOf(projectId));
    
    Element nodeGroups = new Element(ELEMENT_NODE);
    nodeGroups.setAttribute(ATTRIBUTE_NAME, groupsLang);
    nodeGroups.setAttribute(ATTRIBUTE_TYPE, TYPE_GROUPS);
    nodeGroups.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD);
    nodeGroups.setAttribute(ATTRIBUTE_OPTTREE_OPEN, "true");
    nodeGroups.setAttribute(ATTRIBUTE_ICON_NAME, "groups");
    
    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
    nodeGroups.addContent(getNavNodeWithIcon(addGroup, "add_group", FONTSTYLE_PLAIN, "add"));    
    for (Group group : groups) {
      Element nodeGroup = new Element(ELEMENT_NODE);
      nodeGroup.setAttribute(ATTRIBUTE_NAME, group.getName());
      nodeGroup.setAttribute(ATTRIBUTE_TYPE, TYPE_GROUP);
      nodeGroup.setAttribute(ATTRIBUTE_ID, String.valueOf(group.getId()));
      nodeGroup.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_PLAIN);
      nodeGroup.setAttribute(ATTRIBUTE_OPTTREE_OPEN, "true");
      nodeGroup.setAttribute(ATTRIBUTE_DRAGTARGET, "true");
      if(group.getGuest() != 1)
    	  nodeGroup.setAttribute(ATTRIBUTE_ICON_NAME, "group");
      else
    	  nodeGroup.setAttribute(ATTRIBUTE_ICON_NAME, "guest");
    	  
      nodeGroups.addContent(nodeGroup);
      
      try {
    	  List<GroupMembership> usersListRelatedToGroup = dataAgent.getUserGroupByGroupId(group.getId(), projectId);
    	  
    	  for(GroupMembership membership: usersListRelatedToGroup){
              Element nodeUser = new Element(ELEMENT_NODE);
              int userId = membership.getUserId();
              User user = dataAgent.getUser(userId, true);
              String userNodeName = user.getFirstName() + " "+ user.getLastName();
              nodeUser.setAttribute(ATTRIBUTE_NAME, userNodeName);
              nodeUser.setAttribute(ATTRIBUTE_TYPE, TYPE_USER);
              nodeUser.setAttribute(ATTRIBUTE_ID, String.valueOf(user.getId()));
              nodeUser.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_PLAIN);
              nodeUser.setAttribute(ATTRIBUTE_DRAGGABLE, "true");
              nodeUser.setAttribute(ATTRIBUTE_ICON_NAME, "user");
              nodeGroup.addContent(nodeUser);    		  
    	  }
      } catch (MdnException e) {
		e.printStackTrace();
      }
    }
    root.addContent(nodeGroups);
    
    Element customFieldNode = new Element(ELEMENT_NODE);
    customFieldNode.setAttribute(ATTRIBUTE_NAME, userCustomQuery);
    customFieldNode.setAttribute(ATTRIBUTE_TYPE, "user_custom");
    customFieldNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD);
    customFieldNode.setAttribute(ATTRIBUTE_OPTTREE_OPEN, "true");
    customFieldNode.setAttribute(ATTRIBUTE_DRAGTARGET, "true");
    customFieldNode.setAttribute(ATTRIBUTE_ICON_NAME, "users-custom-values");
    root.addContent(customFieldNode);
    //customField.addContent(getNavNodeWithIcon(userCustomQuery, "user_custom", FONTSTYLE_PLAIN, "users-custom-values"));    
    
    Element nodeUsers = new Element(ELEMENT_NODE);
    nodeUsers.setAttribute(ATTRIBUTE_NAME, usersLang);
    nodeUsers.setAttribute(ATTRIBUTE_TYPE, TYPE_USERS);
    nodeUsers.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD);
    nodeUsers.setAttribute(ATTRIBUTE_OPTTREE_OPEN, "true");
    nodeUsers.setAttribute(ATTRIBUTE_DRAGTARGET, "true");
    nodeUsers.setAttribute(ATTRIBUTE_ICON_NAME, "users");
    
    nodeUsers.addContent(getNavNodeWithIcon(addUser, "add_user", FONTSTYLE_PLAIN, "add"));
    for (User user : users) {
      Element nodeUser = new Element(ELEMENT_NODE);
      String userNodeName = user.getFirstName() + " "+ user.getLastName() ;
      nodeUser.setAttribute(ATTRIBUTE_NAME, userNodeName);
      nodeUser.setAttribute(ATTRIBUTE_TYPE, TYPE_USER);
      nodeUser.setAttribute(ATTRIBUTE_ID, String.valueOf(user.getId()));
      nodeUser.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_PLAIN);
      nodeUser.setAttribute(ATTRIBUTE_DRAGGABLE, "true");
      nodeUser.setAttribute(ATTRIBUTE_ICON_NAME, "user");
      nodeUsers.addContent(nodeUser);
    }
    
    root.addContent(nodeUsers);
    
    return root;
  }
  
  @SuppressWarnings("unchecked")
public Element userXML(User user, List<IMConactDetailes> contactsDetailsList, Vector<Group> allGroups, List<CustomField> allCustomFields, 
		List<UserCustomField> userCustomFields, Map allCustomMap, List<Feature> privileges, ArrayList<String> licenseTypes, GroupMembership groupForUser, String languageFile){
  	Element root = new Element(ROOT_ROOT);
    for (UserCustomField userCustomField : userCustomFields) {//Grid
    	int userCustomId = userCustomField.getCustomId();
		if(allCustomMap.containsKey(userCustomId)){
			String userCustomName = (String)allCustomMap.get(userCustomId);
			Element userCustomNode = new Element("userCustom");
			userCustomNode.setAttribute("userCustomName", userCustomName);
			userCustomNode.setAttribute("userCustomParam", userCustomField.getParameter());
			if(groupForUser != null ){
				userCustomNode.setAttribute("uGroupId", String.valueOf(groupForUser.getGroupId()));
			}else{
				userCustomNode.setAttribute("uGroupId", "0");
			}
			root.addContent(userCustomNode);
		}
	}
    if(userCustomFields.size()<=0 ){
		Element basicNode = new Element("test");
		if(groupForUser != null ){
			basicNode.setAttribute("uGroupId", String.valueOf(groupForUser.getGroupId()));
		}else
			basicNode.setAttribute("uGroupId", "0");
		//basicNode.setAttribute("uGroupId", String.valueOf(user.getGroupId()));
		root.addContent(basicNode);
    }  	
  	Element nodeUser = new Element(ELEMENT_NODE);
  	if(user.getName() != null){
  		nodeUser.setAttribute(ATTRIBUTE_NAME, user.getName());
	    nodeUser.setAttribute("username", user.getName());
  	}
    nodeUser.setAttribute("userID", String.valueOf(user.getId()));
    nodeUser.setAttribute("password", user.getPassword() == null ? "" : user.getPassword());
    nodeUser.setAttribute("firstName", user.getFirstName());
    nodeUser.setAttribute("lastName", user.getLastName());
    nodeUser.setAttribute("privilege", user.getPrivilege());
    if(user.getLicenseType() != null && !user.getLicenseType().equals(""))
    	nodeUser.setAttribute("licenseType", user.getLicenseType());
    nodeUser.setAttribute("email", user.getEmail() == null ? "" : user.getEmail());
    nodeUser.setAttribute("mobile", user.getMobile() == null ? "" : user.getMobile());
    nodeUser.setAttribute("notes", user.getNotes() == null ? "" : user.getNotes());
    
    // Check for groups and if present add them to the XML file.
	nodeUser.setAttribute("user-group-id", String.valueOf(user.getGroupId()));
	
	nodeUser.setAttribute("im-size", String.valueOf(contactsDetailsList.size()));
	Element nodeImConn = null;
	for (IMConactDetailes contactDetailes : contactsDetailsList) {
		String imType = contactDetailes.getImType();
		if(imType.equals(Constants.AIM_OSCAR_IM_TYPE_ID))
			nodeImConn = new Element("aim");
		else if(imType.equals(Constants.YAHOO_IM_TYPE_ID))
			nodeImConn = new Element("yahoo");
		else if(imType.equals(Constants.GT_IM_TYPE_ID))
			nodeImConn = new Element("google");
		else if(imType.equals(Constants.MSN_IM_TYPE_ID))
			nodeImConn = new Element("msn");
		else if(imType.equals(Constants.JABBER_IM_TYPE_ID))
			nodeImConn = new Element("jabber");
		else if(imType.equals(Constants.ICQ_IM_TYPE_ID))
			nodeImConn = new Element("icq");
		if(nodeImConn != null){
			nodeImConn.setAttribute("IM-Type", MessagingUtils.getConnectionTypeNameByID(Integer.parseInt(contactDetailes.getImType())));
			if(contactDetailes.getContactText() != null && !contactDetailes.getContactText().equals(""))
				nodeImConn.setAttribute("IM-Contact-Name", contactDetailes.getContactText());
			nodeUser.addContent(nodeImConn);
		}
	}
    for (CustomField customField : allCustomFields) {//Combobox
		Element customNode = new Element("custom");
		customNode.setAttribute("customName", customField.getName());
		customNode.setAttribute("customId", String.valueOf(customField.getId()));
		nodeUser.addContent(customNode);
	}

    
	Element nodeNoGroup = new Element("group");
	nodeNoGroup.setAttribute("name", "");
	nodeNoGroup.setAttribute("group-id", "0");
	nodeUser.addContent(nodeNoGroup);
	if (allGroups != null){
	    for (Group group : allGroups) {
	    	if(group.getGuest() != 1){
				Element nodeGroup = new Element("group");
				nodeGroup.setAttribute("name", group.getName());
				nodeGroup.setAttribute("group-id", String.valueOf(group.getId()));
				nodeUser.addContent(nodeGroup);
	    	}
		}
	}		
	
	if (privileges != null){
	    for (Feature privilege : privileges) {
			Element nodePrivilege = new Element("privilege");
			nodePrivilege.setAttribute("name", privilege.getName());
			nodePrivilege.setAttribute("description", privilege.getDescription());
			nodeUser.addContent(nodePrivilege);
		}
	}		

	if (licenseTypes != null){
	    for (String licenseType : licenseTypes) {
			Element nodeLicenseType = new Element("licenseType");
			nodeLicenseType.setAttribute("name", licenseType);
			nodeUser.addContent(nodeLicenseType);
		}
	}	
	//nodeUser = prepareEmptyIMConnectionXML(nodeUser, contactsDetailsList);//for new user

	root.addContent(nodeUser);
  	return root;
  }
  public synchronized Element saveUserResult(String action, int newUserId, String username, String file, String result, int projectId){ 
	  	Element root = new Element(ROOT_ROOT);
	  	root.setAttribute("action", action);
	  	root.setAttribute("user-id", String.valueOf(newUserId));
	  	root.setAttribute("id", String.valueOf(newUserId));
	  	root.setAttribute("projectId", String.valueOf(projectId));
	  	//root.setAttribute("name", username);
	  	root.setAttribute("result", result);	  	
	  	
	  	return root;
  }

  public synchronized Element addCustomResult(String action, String userId, String result, String projectId){ 
	  	Element root = new Element(ROOT_ROOT);
	  	root.setAttribute("action", action);
	  	root.setAttribute("user-id", userId);
	  	root.setAttribute("result", result);	
	  	root.setAttribute("projectId", projectId);		  	
	  	return root;
}

  public Element newUserXML(List<IMConactDetailes> contactsDetailsList, Vector<Group> groups, List<Feature> privileges, ArrayList<String> licenseTypes){
	  	Element root = new Element(ROOT_ROOT);

	  	//Empty group node
        Element emptyGroupNode = new Element("group");	    	
        emptyGroupNode.setAttribute(ATTRIBUTE_NAME, "");
        emptyGroupNode.setAttribute(ATTRIBUTE_TYPE, TYPE_GROUP);
        emptyGroupNode.setAttribute(ATTRIBUTE_ID, "0");
        emptyGroupNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_PLAIN);
        emptyGroupNode.setAttribute(ATTRIBUTE_OPTTREE_OPEN, "true");
        emptyGroupNode.setAttribute(ATTRIBUTE_DRAGTARGET, "true");
        emptyGroupNode.setAttribute(ATTRIBUTE_ICON_NAME, "group");
        root.addContent(emptyGroupNode);
		for (Group group : groups) {
	    	if(group.getGuest() != 1){//If the group isn't public group add it as an item combobox			
		        Element nodeGroup = new Element("group");	    	
		        nodeGroup.setAttribute(ATTRIBUTE_NAME, group.getName());
		        nodeGroup.setAttribute(ATTRIBUTE_TYPE, TYPE_GROUP);
		        nodeGroup.setAttribute(ATTRIBUTE_ID, String.valueOf(group.getId()));
		        nodeGroup.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_PLAIN);
		        nodeGroup.setAttribute(ATTRIBUTE_OPTTREE_OPEN, "true");
		        nodeGroup.setAttribute(ATTRIBUTE_DRAGTARGET, "true");
		        nodeGroup.setAttribute(ATTRIBUTE_ICON_NAME, "group");
		        root.addContent(nodeGroup);
	    	}
	    }
		
		if (privileges != null){
		    for (Feature privilege : privileges) {
				Element nodePrivilege = new Element("privilege");
				nodePrivilege.setAttribute("name", privilege.getName());
				nodePrivilege.setAttribute("description", privilege.getDescription());
				root.addContent(nodePrivilege);
			}
		}


		if (licenseTypes != null){
		    for (String licenseType : licenseTypes) {
				Element nodeLicenseType = new Element("licenseType");
				nodeLicenseType.setAttribute("name", licenseType);
				root.addContent(nodeLicenseType);
			}
		}			
		
		//root.addContent(nodeUser);
	  	return root;
	  }


  public Element groupXML(Group group){
  	Element root = new Element(ROOT_ROOT);
  	Element nodeGroup = new Element(ELEMENT_NODE);
  	nodeGroup.setAttribute(ATTRIBUTE_NAME, group.getName());
  	nodeGroup.setAttribute("groupID", String.valueOf(group.getId()));
  	nodeGroup.setAttribute("guest", booleanConvertor(group.getGuest()));
  	
    root.addContent(nodeGroup);
  	return root;
  }
  /*
  public synchronized Element dbWizardResult(String file, String action, String step, String result){ 
  	Element root = new Element(ROOT_ROOT);
  	root.setAttribute("action", action);
  	root.setAttribute("step", step);
  	root.addContent(result);
  	return root;
  }
  */
  public synchronized Element dbWizardFieldsResult(Vector<MetaField> fields){ 
  	Element root = new Element(ROOT_ROOT);
  	
  	Element fieldsNode = new Element("fields");
  	fieldsNode.setAttribute(ATTRIBUTE_TYPE, "fields");
  	
  	for (MetaField field : fields) {
			Element fieldNode = new Element("field");
			fieldNode.setAttribute(ATTRIBUTE_ID, String.valueOf(field.getFieldID()));
			fieldNode.setAttribute(ATTRIBUTE_NAME, field.getName());
			fieldsNode.addContent(fieldNode);
		}
  	
  	root.addContent(fieldsNode);
  	
  	return root;
  }
  
  public synchronized Element dbWizardTablesResult(Vector<MetaTable> tables){ 
  	Element root = new Element(ROOT_ROOT);
  	
  	Element availTablesNode = new Element("avail-tables");
  	availTablesNode.setAttribute(ATTRIBUTE_TYPE, "avail-tables");
  	
  	for (MetaTable table : tables) {
			Element availTableNode = new Element("avail-table");
			availTableNode.setAttribute(ATTRIBUTE_ID, String.valueOf(table.getTableID()));
			availTableNode.setAttribute(ATTRIBUTE_NAME, table.getName());
			availTablesNode.addContent(availTableNode);
		}
  	
  	root.addContent(availTablesNode);
  	
  	return root;
  }
  
  public synchronized Element dbImportTablesResult(Vector<MetaTable> tables){ 
  	Element root = new Element(ROOT_ROOT);
  	
  	Element importTablesNode = new Element("import-tables");
  	importTablesNode.setAttribute(ATTRIBUTE_TYPE, "importTables");
  	
  	for (MetaTable table : tables) {
			Element importTableNode = new Element("import-table");
			importTableNode.setAttribute(ATTRIBUTE_ID, String.valueOf(table.getTableID()));
			importTableNode.setAttribute(ATTRIBUTE_NAME, table.getName());
			importTablesNode.addContent(importTableNode);
		}
  	
  	root.addContent(importTablesNode);
  	
  	return root;
  }
  
  public synchronized Element dbGetRelationsResult(Vector<MetaRelation> relations){ 
  	Element root = new Element(ROOT_ROOT);
  	
  	for (MetaRelation relation : relations) {
			Element relationNode = new Element("relation");
			relationNode.setAttribute(ATTRIBUTE_NAME, relation.getQualifiedName());
			root.addContent(relationNode);
		}
  	
  	return root;
  }
  
  public synchronized Element testDbConnectionResult(String file, String action, String result){ 
  	return simpleResult(file, action, result);
  }
  
  public synchronized Element newDbConnectionResult(String file, String action, String result, int projectId, int newConnId, String dbName){ 
  	Element root = simpleResult(file, action, result);
  	root.setAttribute("projectId", String.valueOf(projectId));
  	root.setAttribute("connId", String.valueOf(newConnId));
  	root.setAttribute("dbName", dbName);
  	return root;
  }
  
  public synchronized Element dbAddRelationResult(String file, String action, String result){ 
  	return simpleResult(file, action, result);
  }
  
  public synchronized Element dbDeleteRelationResult(String file, String action, String result){ 
  	return simpleResult(file, action, result);
  }
  
  public synchronized Element abortWizardResult(String file, String action, String result){ 
  	return simpleResult(file, action, result);
  }
  
  public synchronized Element finishWizardResult(String file, String action, String result){ 
  	return simpleResult(file, action, result);
  }
  
  public synchronized Element recycleConnectionResult(String file, String action, String result){ 
  	return simpleResult(file, action, result);
  }
  
  public synchronized Element editAddViewResult(String file, String action, String result, String newID, String projectId){ 
	  Element root = 	simpleResult(file, action, result);
	  root.setAttribute(ATTRIBUTE_ID, newID);
	  root.setAttribute("projectId", projectId);
	  return root;
  }  
  
  public synchronized Element editResult(String file, String action, String result){ 
  	return simpleResult(file, action, result);
  }
  
  public synchronized Element newUserResult(String file, String result){ 
	  	Element root = new Element(ROOT_ROOT);
	  	root.setAttribute("action", "userNew");
	  	root.setAttribute("result", result);	  	
	  	return root;
//  	return simpleResult(file, "newUser", result);
  }
  
  public synchronized Element editUserResult(String file, String result){ 
  	return simpleResult(file, "editUser", result);
  }
  
  public synchronized Element recycleUserResult(String file, String result, int projectId){ 
  	//return simpleResult(file, "recycleUser", result);
  	Element root = new Element(ROOT_ROOT);
  	root.setAttribute("action", "recycleUser");
  	root.addContent(result);
  	root.setAttribute("result", result);
  	root.setAttribute("projectId", String.valueOf(projectId));
  	return root;
  	
  }
  
  public synchronized Element clearUserResult(String file, String result){ 
  	return simpleResult(file, "clearUser", result);
  }
  
  public synchronized Element deleteUserResult(String file, String result){ 
  	return simpleResult(file, "deleteUser", result);
  }
  
  public synchronized Element newGroupResult(String file, String result){ 
  	return simpleResult(file, "newGroup", result);
  }
  
  public synchronized Element recycleGroupResult(String file, String result){ 
  	return simpleResult(file, "recycleGroup", result);
  }
  
  public synchronized Element clearGroupResult(String file, String result){ 
  	return simpleResult(file, "clearGroup", result);
  }
  
  public synchronized Element deleteGroupResult(String file, String result){ 
  	return simpleResult(file, "deleteGroup", result);
  }
  
  public synchronized Element simpleResult(String file, String action, String result){
  	// TODO Need to look up the translation for the result.
  	Element root = new Element(ROOT_ROOT);
  	root.setAttribute("action", action);
  	root.addContent(result);
  	return root;
  }
  public synchronized Element simpleResultWithErrorMsg(String file, String action, String result, String errorMsg){
	  	// TODO Need to look up the translation for the result.
	  	Element root = new Element(ROOT_ROOT);
	  	root.setAttribute("action", action);
	  	root.setAttribute("errorMessage", errorMsg);
	  	root.addContent(result);
	  	return root;
	  }
  public synchronized Element simpleResult(String action, String result){
	  	// TODO Need to look up the translation for the result.
	  	Element root = new Element(ROOT_ROOT);
	  	root.setAttribute("action", action);
	  	root.addContent(result);
	  	return root;
	  }
  
  public synchronized Element simpleResultWithProjectId(String file, String action, String result, int projectId){
  	// TODO Need to look up the translation for the result.
  	Element root = simpleResult(file, action, result);
  	root.setAttribute("projectId", String.valueOf(projectId));
  	return root;
  }  
  
  public Element recycleBin(Vector<RecycleBinItem> recycleData){
  	Element root = new Element(ROOT_ROOT);
  	for (RecycleBinItem item : recycleData) {
  		Element binNode = new Element(ELEMENT_NODE);
  		binNode.setAttribute("id", String.valueOf(item.getId()));
  		binNode.setAttribute("type", item.getType());
  		binNode.setAttribute("name", item.getName());
  		binNode.setAttribute("projectId", String.valueOf(item.getProjectId()));
    		root.addContent(binNode);
	}
    return root;
  }

  public Element userRecycleBin(Vector<RecycleBinItem> recycleData){
	  	Element root = new Element(ROOT_ROOT);
	  	for (RecycleBinItem item : recycleData) {
	  		Element binNode = new Element(ELEMENT_NODE);
	  		binNode.setAttribute("id", String.valueOf(item.getId()));
	  		binNode.setAttribute("type", item.getType());
	  		binNode.setAttribute("name", item.getName());
	  		binNode.setAttribute("projectId", String.valueOf(item.getProjectId()));
	  		binNode.setAttribute(ATTRIBUTE_ICON_NAME, "user");
	  		root.addContent(binNode);
		}
	    return root;
	  }
  
  public Element languages(List<LanguageDobj> languages){
    Element root = new Element(ROOT_ROOT);
    /*addElementWith2Attributes(root, "lang", "name", "Chinese (simple)", "value", "cn_simple");
    addElementWith2Attributes(root, "lang", "name", "Chinese (traditional)", "value", "ch_trad");
    */
//    addElementWith2Attributes(root, "lang", "name", "English", "value", "en");
//    addElementWith2Attributes(root, "lang", "name", "Australian English", "value", "au");
    /*addElementWith2Attributes(root, "lang", "name", "German", "value", "de");
    addElementWith2Attributes(root, "lang", "name", "Japanese", "value", "jp");
    addElementWith2Attributes(root, "lang", "name", "Russian", "value", "ru");
    addElementWith2Attributes(root, "lang", "name", "Spanish", "value", "sp");*/
    
    for (LanguageDobj lang: languages){
    	addElementWith2Attributes(root, "lang", "name", lang.getName(), "id", String.valueOf(lang.getId()));
    }
    
    return root;
  }
  /*public Element language(LanguageDobj lang){
	    Element root = new Element(ROOT_ROOT);	    
	    //addElementWith2Attributes(root, "lang", "name", lang.getName(), "value", lang.getValue() == null ? "" : lang.getValue());	    
	    root.setAttribute("name", lang.getName());
	    return root;
	  }*/  
  public Element exportFileURL(String filename){
	  	Element root = new Element(ROOT_ROOT);
	  	root.setAttribute("filename", filename);	    
	    return root;
	  }
  /*public Element fileUploadURL(){
  	Element root = new Element(ROOT_ROOT);
  	root.addContent(Constants.LANGUAGE_FILE_UPLOAD_URL);
    
    return root;
  }
  public Element driverFileUploadURL(){
  	Element root = new Element(ROOT_ROOT);
  	root.addContent(Constants.DRIVER_FILE_UPLOAD_URL);
    
    return root;
  } 

  public Element projectFileUploadURL(String url){
	  	Element root = new Element(ROOT_ROOT);
	  	root.addContent(url);	    
	    return root;
	  } 
 */
  public Element getSecureLoginLink(String loginLink, String errorMsg){
	  	Element root = new Element(ROOT_ROOT);
	  	root.addContent(loginLink != null ? loginLink : "");
	  	if (errorMsg != null)
	  		root.setAttribute("result", "fail");
	  	else
	  		root.setAttribute("result", "ok");
	    return root;
	  }  
  
  public Element getRegisteredEmail(String registeredEmail, int installationRef, String errorMsg, String result){
	  	Element root = new Element(ROOT_ROOT);
	  	root.setAttribute("registeredEmail", registeredEmail != null ? registeredEmail : "");
	  	root.setAttribute("installationRef", installationRef + "");
	  	root.setAttribute("result", result != null ? result : "");
	  	root.setAttribute("msg",errorMsg);
	    return root;
	  }   

  public Element getLogFilePath(String action, String[] filePaths){
	  	Element root = new Element(ROOT_ROOT);
	  	root.setAttribute("MdnServerLog", filePaths.length > 0? filePaths[0] : "");
	  	root.setAttribute("MdnServerError", filePaths.length > 1? filePaths[1] : "");
	  	root.setAttribute("EmailServerLog", filePaths.length > 2? filePaths[2] : "");
	  	root.setAttribute("EmailServerError", filePaths.length > 3? filePaths[3] : "");
	  	root.setAttribute("SmsServerLog", filePaths.length > 4? filePaths[4] : "");
	  	root.setAttribute("SmsServerError", filePaths.length > 5? filePaths[5] : "");
	  	root.setAttribute("IMServerLog", filePaths.length > 6? filePaths[6] : "");
	  	root.setAttribute("IMServerError", filePaths.length > 7? filePaths[7] : "");
	    return root;
	  }   
  
  public Element navigationProject(String languageFile, String projectName){
	    Element guiDef = this.getXMLLanguageFile(languageFile);
	    String projectLang = getGuiValueForElement(guiDef, "tree-project");
	    
	    // Define the XML structure.
	    Element root = new Element(ROOT_ROOT);
	    
	    Element projectElement = new Element(ELEMENT_NODE);
	    projectElement.setAttribute(ATTRIBUTE_NAME, projectLang);
	    projectElement.setAttribute(ATTRIBUTE_TYPE, TYPE_PROJECT);
	    projectElement.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD);
	    projectElement.setAttribute("__OPTTREE_META_open", "true");
	    projectElement.setAttribute(ATTRIBUTE_ICON_NAME, "project");

	    Element projectNameNode = new Element(ELEMENT_NODE);
	    projectNameNode.setAttribute(ATTRIBUTE_NAME, projectName);
	    projectNameNode.setAttribute(ATTRIBUTE_TYPE, TYPE_PROJECT);
	    projectNameNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_PLAIN);
	    projectNameNode.setAttribute("__OPTTREE_META_open", "true");
	    projectNameNode.setAttribute(ATTRIBUTE_ICON_NAME, "project");
	    projectElement.addContent(projectNameNode);
	    
	    root.addContent(projectElement);
	    
	    return root;
	}  
  
  public Element navigationHome(String languageFile, List<ProjectDobj> projects){
    Element guiDef = this.getXMLLanguageFile(languageFile);
    String projectsLang = getGuiValueForElement(guiDef, "tree-projects");
    String addProject = getGuiValueForElement(guiDef, "tree-add-project");
    
    // Define the XML structure.
    Element root = new Element(ROOT_ROOT); 
    
    Element projectElement = new Element(ELEMENT_NODE);
    projectElement.setAttribute(ATTRIBUTE_NAME, projectsLang);
    projectElement.setAttribute(ATTRIBUTE_TYPE, TYPE_PROJECTS);
    projectElement.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD);
    projectElement.setAttribute("__OPTTREE_META_open", "true");
    projectElement.setAttribute(ATTRIBUTE_ICON_NAME, "projects");

    Element addNode = new Element(ELEMENT_NODE);
    addNode.setAttribute(ATTRIBUTE_NAME, addProject);
    addNode.setAttribute(ATTRIBUTE_TYPE, "add-project");
    addNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_PLAIN);
    addNode.setAttribute("__OPTTREE_META_open", "true");
    addNode.setAttribute(ATTRIBUTE_ICON_NAME, "add");    
    projectElement.addContent(addNode);      
    
    root.setAttribute("projectsCount", String.valueOf(projects.size()));
    for (ProjectDobj project: projects){
      Element eachProject = getNavNodeWithValue(project.getName(), TYPE_PROJECT, FONTSTYLE_PLAIN, "project", String.valueOf(project.getId()));
      eachProject.setAttribute(ATTRIBUTE_DRAGGABLE, "true");
      projectElement.addContent(eachProject);
    }
    
    root.addContent(projectElement);
    
    return root;
  }

  public Element getProject(String action, String result, ProjectDobj project){
	    // Define the XML structure.
	    Element root = new Element(ROOT_ROOT);
	  	root.setAttribute("action", action);
	  	root.addContent(result);
	  	root.setAttribute(ATTRIBUTE_ID, String.valueOf(project.getId()));
	  	root.setAttribute(ATTRIBUTE_NAME, project.getName());
	  	
	  	root.setAttribute("defaultId", String.valueOf(project.getId()));
	  	root.setAttribute("defaultName", project.getName());	    
	  	
	    Element projectElement = new Element(ELEMENT_NODE);
	    projectElement.setAttribute(ATTRIBUTE_ID, String.valueOf(project.getId()));
	    projectElement.setAttribute(ATTRIBUTE_NAME, project.getName());
	    projectElement.setAttribute(ATTRIBUTE_TYPE, TYPE_PROJECT);
	    projectElement.setAttribute("description", project.getDescription()==null? "" : project.getDescription());
	    
	    root.addContent(projectElement);
	    
	    return root;
	  }  
  
  public Element navigationPresentations(String languageFile, List<QueryDobj> messagingsInfo){
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	    Element guiDef = this.getXMLLanguageFile(languageFile);
	    String projectsLang = getGuiValueForElement(guiDef, "tree-projects");
	    
	    String msgAccess = getGuiValueForElement(guiDef, "tree-msg-access");
	    String addMsg = getGuiValueForElement(guiDef, "tree-add-msg");
	    String addUserReply = getGuiValueForElement(guiDef, "tree-user-reply");
	    
	    // Define the XML structure.
	    Element root = new Element(ROOT_ROOT);
	    
	    /*Element nodePresentation = getNavNodeWithIcon("Presentation 1", TYPE_PRESENTATION, FONTSTYLE_BOLD, true, "presentation"); 
	    //nodePresentation.addContent(getNavNodeWithIcon("General", "general", FONTSTYLE_BOLD_ITALIC, "presentationGeneral"));
	    
	    Element nodeDesigner = getNavNodeWithIcon("Designer", "designer", FONTSTYLE_BOLD_ITALIC, true, "designer");
	      
	    Element nodeHome = getNavNodeWithIcon("Home", "home", FONTSTYLE_PLAIN, true, "sitemap");
	    nodeHome.addContent(getNavNodeWithIcon("Customer", "field", FONTSTYLE_ITALIC, "sitemap"));
	    
	    Element nodeEmail = getNavNodeWithIcon("Email", "field", FONTSTYLE_ITALIC, true, "sitemap");
	    nodeEmail.addContent(getNavNodeWithIcon("Calendar", "calendar", FONTSTYLE_PLAIN, "sitemap"));
	    nodeEmail.addContent(getNavNodeWithIcon("Tasks", "tasks", FONTSTYLE_PLAIN, "sitemap"));
	    
	    nodeHome.addContent(nodeEmail);
	    
	    nodeHome.addContent(getNavNodeWithIcon("Hours", "field", FONTSTYLE_ITALIC, "sitemap"));
	    nodeHome.addContent(getNavNodeWithIcon("Orders", "field", FONTSTYLE_ITALIC, "sitemap"));
	    nodeHome.addContent(getNavNodeWithIcon("Products", "field", FONTSTYLE_ITALIC, "sitemap"));
	    
	    nodeDesigner.addContent(nodeHome);
	    
	    nodePresentation.addContent(nodeDesigner);
	    
	    root.addContent(nodePresentation);
	    */
	    //Messaging Access
	  	Element msgAccessNode = new Element(ELEMENT_NODE);
	  	msgAccessNode.setAttribute(ATTRIBUTE_NAME, msgAccess);
	  	msgAccessNode.setAttribute(ATTRIBUTE_TYPE, "msg-access");
	  	msgAccessNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_PLAIN);
	  	msgAccessNode.setAttribute("__OPTTREE_META_open", "true");
	  	msgAccessNode.setAttribute(ATTRIBUTE_ICON_NAME, "email");    
	  	root.addContent(msgAccessNode);
	 
	  	
	  	msgAccessNode.addContent(getNavNodeWithIcon(addMsg, "add_msg", FONTSTYLE_PLAIN, "add"));
	  	for (QueryDobj msgInfo : messagingsInfo) {
	  		String msgName = msgInfo.getName();
	        Element nodeMsg = getNavNodeWithValue(msgName, "msg-info", FONTSTYLE_PLAIN, "email", String.valueOf(msgInfo.getId()));
	        nodeMsg.setAttribute(ATTRIBUTE_DRAGGABLE, "true");
	        nodeMsg.setAttribute(ATTRIBUTE_DRAGTARGET, "true");
	        if(msgInfo.getDatasourceStatus() == 1){
		        nodeMsg.setAttribute("viewID", String.valueOf(msgInfo.getViewOrTableId()));
		        String qType = msgInfo.getType();
		        nodeMsg.setAttribute("queryType", qType);
	        }
	        msgAccessNode.addContent(nodeMsg);
	        
	        //UserReply tree >>>>>>>>>>>>>>>>
	        nodeMsg.addContent(getNavNodeWithValue(addUserReply, "add_user_reply", FONTSTYLE_PLAIN, "add", String.valueOf(msgInfo.getId())));//Value=parentId
		    //if(qType.equals(Constants.SELECT_QUERY_TYPE)) {
	        int queryId = msgInfo.getId();
			try {
				List<UserReply> userRepliesList1 = dataAgent.getUserRepliesByParentId(queryId);
				
				for (int i=0; i<userRepliesList1.size() ; i++) {//1-ROOT
					//Root INFO
					UserReply userReply = userRepliesList1.get(i); 
					String urId = String.valueOf(userReply.getId());
					
					Element nodeUR = appendUserReplyNodeToTree(userReply, nodeMsg);							
					
					List<String> childrenList2 = parsChildrenId(userReply.getChildren());//Pars children list

        	        //Appent Add node to this UserReply
        	        nodeUR.addContent(getNavNodeWithValue(addUserReply, "add_user_reply", FONTSTYLE_PLAIN, "add", urId));
					for (int j=0; j<childrenList2.size() ; j++) {//2	
						String childId = childrenList2.get(j);
						UserReply urChild = dataAgent.getUserReplyById(Integer.parseInt(childId));
						//Append UserReply node to tree
						if(urChild.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT){//IF 2 Not Del
							Element nodeUR2 = appendUserReplyNodeToTree(urChild, nodeUR);
	
							List<String> childrenList3 = parsChildrenId(urChild.getChildren());
							//Append "Add" to tree
							nodeUR2.addContent(getNavNodeWithValue(addUserReply, "add_user_reply", FONTSTYLE_PLAIN, "add", childId));
							for (int k=0; k<childrenList3.size() ; k++) {//3
								String childId3 = childrenList3.get(k);
								UserReply urChild3 = dataAgent.getUserReplyById(Integer.parseInt(childId3));
								if(urChild3.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT){//IF 3 Not Del
									//Append UserReply node to tree
									Element nodeUR3 = appendUserReplyNodeToTree(urChild3, nodeUR2);
									
									List<String> childrenList4 = parsChildrenId(urChild3.getChildren());
									//Append "Add" to tree
									nodeUR3.addContent(getNavNodeWithValue(addUserReply, "add_user_reply", FONTSTYLE_PLAIN, "add", childId3));									
									for (int d=0; d<childrenList4.size() ; d++) {//4
										String childId4 = childrenList4.get(d);
										UserReply urChild4 = dataAgent.getUserReplyById(Integer.parseInt(childId4));
										if(urChild4.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT){//IF 4 Not Del
											// Append UserReply node to tree
											Element nodeUR4 = appendUserReplyNodeToTree(urChild4, nodeUR3);										
			
											//Append "Add" to tree
											List<String> childrenList5 = parsChildrenId(urChild4.getChildren());
											nodeUR4.addContent(getNavNodeWithValue(addUserReply, "add_user_reply", FONTSTYLE_PLAIN, "add", childId4));									
											for (int e=0; e<childrenList5.size() ; e++) {//5
												String childId5 = childrenList5.get(e);
												UserReply urChild5 = dataAgent.getUserReplyById(Integer.parseInt(childId5));
												// Append UserReply node to tree
												if(urChild5.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT){//IF 5 Not Del
													Element nodeUR5 = appendUserReplyNodeToTree(urChild5, nodeUR4);	
												}//If 5
											}//5
										}//If 4
									}//4
								}//If 3
							}//3
						}//if 2
					}//2
				}//1
			} catch (MdnException e) {
				e.printStackTrace();
			}
				//}
	        //UserReply Tree <<<<<<<<<<<<<<<<<<<
	  	}
	    return root;
	  }  
	public List<String> parsChildrenId(String childrenStr)
	{
		List<String> childrenList = new ArrayList<String> ();
		String child;
		if(childrenStr != null && childrenStr.length()>0)
		{
			while(childrenStr.contains(",")) {
				int endIndex = childrenStr.indexOf(",");
				child = childrenStr.substring(0, endIndex);
				childrenList.add(child);
				childrenStr = childrenStr.substring(endIndex+1);
			}
	    	if(!childrenStr.contains(",") && childrenStr.length()>0)
	    	{
	    		childrenList.add(childrenStr);
	    	}
		}
		return childrenList;
	}
	
	private Element appendUserReplyNodeToTree(UserReply userReply, Element parentNode){
		String urText = userReply.getMsgText();
		int viewOrTableId = userReply.getViewOrTableId();

		Element nodeUR = getNavNodeWithValue(urText, "user_reply", FONTSTYLE_PLAIN, "userReply", String.valueOf(userReply.getId()));
        nodeUR.setAttribute(ATTRIBUTE_DRAGGABLE, "true");
        nodeUR.setAttribute(ATTRIBUTE_DRAGTARGET, "true");
        nodeUR.setAttribute(ATTRIBUTE_ICON_NAME, "ur");
        if(userReply.getDatasourceStatus() == 1){        
	        nodeUR.setAttribute("viewID", String.valueOf(viewOrTableId));
	        nodeUR.setAttribute("queryType", userReply.getType());
        }
        parentNode.addContent(nodeUR);
        
        return nodeUR;
	}
	
	public Element rightClickFunction(String[] arText) {
		//Create _rcoptions Node
		Element childRCOptions = new Element("_rcoptions");
		
		//Create _option Node with Loop if More than One
		for (int i = 0; i < arText.length; i++) {
			Element childOption = new Element("_option");
			childOption.setAttribute("name", arText[i]);
			childOption.setAttribute("enabled", "true");
			childOption.setAttribute("visible", "true");
			childRCOptions.addContent(childOption);
		}
		
		//Return Value
		return childRCOptions;
	}
	
  public Element navigationSettings(String languageFile, List<LanguageDobj> languages, List<JdbcDriver> list, List<IMConnection> imConnections, List<MdnEmailSetting> mdnEmailsServer, List<MdnSmpp> smppGateways){
    Element guiDef = this.getXMLLanguageFile(languageFile);
    if(guiDef == null)
    	System.out.println("Session expired !!! ");
    //String account = getGuiValueForElement(guiDef, "tree-account");
    String language = getGuiValueForElement(guiDef, "tree-language");
    String dbDrivers = getGuiValueForElement(guiDef, "tree-db-drivers");
    String messaging = getGuiValueForElement(guiDef, "tree-msg");
    String im = getGuiValueForElement(guiDef, "tree-im");    
    String add_im = getGuiValueForElement(guiDef, "tree-add-im");
    String email = getGuiValueForElement(guiDef, "tree-email");  
    String set_smtp = getGuiValueForElement(guiDef, "tree-set-smtp");
    String sms = getGuiValueForElement(guiDef, "tree-sms");    
    String smsSettTitle = getGuiValueForElement(guiDef, "tree-sms-sett");
    String addSmppTitle = getGuiValueForElement(guiDef, "tree-sdd-new-smpp");
    //String smppGWTitle = getGuiValueForElement(guiDef, "tree-qw-sms-sett");
    String msControlsTitle = getGuiValueForElement(guiDef, "tree-msg-controls");
    String pubMsgLogTitle = getGuiValueForElement(guiDef, "tree-pub-msg-log");
        
    // Define the XML structure.
    Element root = new Element(ROOT_ROOT);
    root.setAttribute(ATTRIBUTE_DRAGTARGET, "true");
    
    //Element accountNode = getNavNodeWithIcon(account, "account", FONTSTYLE_BOLD, true, "account"); 
    //root.addContent(accountNode);
    
    //Element nodeSettings = getNavNodeWithIcon(generalSettings, TYPE_GENERAL, FONTSTYLE_BOLD, true, "generalSettings"); 
    Element langNode = getNavNodeWithIcon(language, TYPE_LANGUAGE, FONTSTYLE_BOLD, "language");
    langNode.setAttribute("__OPTTREE_META_open", "true");
    root.addContent(langNode);
    Element addLang = getNavNodeWithIcon("Add Language", "add-lang", FONTSTYLE_PLAIN, "add");
    langNode.addContent(addLang);
    for (LanguageDobj lang: languages){
    	Element enNode = getNavNodeWithValue(lang.getName(), "lang", FONTSTYLE_PLAIN, "language", String.valueOf(lang.getId()));
    	enNode.setAttribute(ATTRIBUTE_DRAGGABLE, "true");
    	langNode.addContent(enNode);
    	
    	//Delete Driver Code from Ryan *Start* >>>>>>>>
		enNode.addContent(rightClickFunction(new String[] {"Delete Language"}));
		//Delete Driver Code from Ryan *End* <<<<<<<<<
    }
    
    Element driversNode = getNavNodeWithIcon(dbDrivers, TYPE_DRIVERS, FONTSTYLE_BOLD, "dbDrivers");
    driversNode.setAttribute("__OPTTREE_META_open", "true");
    Element addDriver = getNavNodeWithIcon("Add Database Driver", "add-driver", FONTSTYLE_PLAIN, "add");
    driversNode.addContent(addDriver);
    if (list != null){
		for (JdbcDriver jdbcDriver : list) {
			Element node = new Element(ELEMENT_NODE);
			node.setAttribute("id", String.valueOf(jdbcDriver.getId()));
			node.setAttribute("name", jdbcDriver.getName());
			node.setAttribute("driver", jdbcDriver.getDriver());
			node.setAttribute("urlFormat", jdbcDriver.getUrlFormat());
			node.setAttribute("description", jdbcDriver.getDescription() == null ? "" : jdbcDriver.getDescription());
			node.setAttribute(ATTRIBUTE_TYPE, "driver");
			node.setAttribute(ATTRIBUTE_ICON_NAME, "dbDrivers");
			node.setAttribute(ATTRIBUTE_DRAGGABLE, "true");
			node.setAttribute(ATTRIBUTE_DRAGTARGET, "true");
			//node.setAttribute("__OPTTREE_META_open", "true");
			driversNode.addContent(node);
			
			//Delete Driver Code from Ryan *Start* >>>>>>>>
			node.addContent(rightClickFunction(new String[] {"Delete Driver"}));
			//Delete Driver Code from Ryan *End* <<<<<<<<<
		}
    }
    root.addContent(driversNode);
    
    //Messagin Settings
    Element msgNode = new Element(ELEMENT_NODE);
    msgNode.setAttribute(ATTRIBUTE_NAME, messaging);
    msgNode.setAttribute(ATTRIBUTE_TYPE, "msg-root");
    msgNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD);
    msgNode.setAttribute("__OPTTREE_META_open", "true");
    //msgNode.setAttribute(ATTRIBUTE_ICON_NAME, "msg");
    msgNode.setAttribute(ATTRIBUTE_ICON_NAME, "msg-sett");
    
    String separator = getGuiValueForElement(guiDef, "tree-msg-sep");
  	Element msgSep = new Element(ELEMENT_NODE);
  	msgSep.setAttribute(ATTRIBUTE_NAME, separator);
  	msgSep.setAttribute(ATTRIBUTE_TYPE, "msg-sep");
  	msgSep.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_PLAIN);
  	msgSep.setAttribute("__OPTTREE_META_open", "true");
  	msgSep.setAttribute(ATTRIBUTE_ICON_NAME, "separator");    
  	msgNode.addContent(msgSep);
    
  	Element msgControls = new Element(ELEMENT_NODE);
  	msgControls.setAttribute(ATTRIBUTE_NAME, msControlsTitle);
  	msgControls.setAttribute(ATTRIBUTE_TYPE, "msg-controls");
  	msgControls.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_PLAIN);
  	msgControls.setAttribute("__OPTTREE_META_open", "true");
  	msgControls.setAttribute(ATTRIBUTE_ICON_NAME, "msg-controls");    
  	msgNode.addContent(msgControls);
  	
  	//TODO: Public Message Logs
  	/*Element pubMsgLog = new Element(ELEMENT_NODE);
  	pubMsgLog.setAttribute(ATTRIBUTE_NAME, pubMsgLogTitle);
  	pubMsgLog.setAttribute(ATTRIBUTE_TYPE, "pub-msg-log");
  	pubMsgLog.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_PLAIN);
  	pubMsgLog.setAttribute("__OPTTREE_META_open", "true");
  	pubMsgLog.setAttribute(ATTRIBUTE_ICON_NAME, "pub-msg-log");    
  	msgNode.addContent(pubMsgLog);*/  	  	
  	
    String search = getGuiValueForElement(guiDef, "tree-search");
  	Element searchNode = new Element(ELEMENT_NODE);
  	searchNode.setAttribute(ATTRIBUTE_NAME, search);
  	searchNode.setAttribute(ATTRIBUTE_TYPE, "search");
  	searchNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_PLAIN);
  	searchNode.setAttribute("__OPTTREE_META_open", "true");
  	searchNode.setAttribute(ATTRIBUTE_ICON_NAME, "messaging_search");    
  	msgNode.addContent(searchNode);  	
  	
  	Element emailNode = new Element(ELEMENT_NODE);
  	emailNode.setAttribute(ATTRIBUTE_NAME, email);
  	emailNode.setAttribute(ATTRIBUTE_TYPE, "email");
  	emailNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD);
  	emailNode.setAttribute("__OPTTREE_META_open", "true");
  	emailNode.setAttribute(ATTRIBUTE_ICON_NAME, "new-email");
  	emailNode.setAttribute(ATTRIBUTE_DRAGTARGET, "false");
    msgNode.addContent(emailNode);
    emailNode.addContent(getNavNodeWithIcon(set_smtp, "add_mdn_email", FONTSTYLE_PLAIN, "add"));
  	for (MdnEmailSetting mdnEmail : mdnEmailsServer) {
  		if(mdnEmail.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT){
	  		//String emailImapName = mdnEmail.getImapUserName();
	  		String emailImapName = mdnEmail.getEmailAddress();
	        Element nodeEmail = getNavNodeWithValue(emailImapName, "edit_mdn_email", FONTSTYLE_PLAIN, "new-email", String.valueOf(mdnEmail.getId()));
	        nodeEmail.setAttribute(ATTRIBUTE_DRAGGABLE, "true");
	        nodeEmail.setAttribute(ATTRIBUTE_DRAGTARGET, "true");
	        emailNode.addContent(nodeEmail);
	        
			//Delete Driver Code from Ryan *Start* >>>>>>>>
	        nodeEmail.addContent(rightClickFunction(new String[] {"Delete Email"}));
			//Delete Driver Code from Ryan *End* <<<<<<<<<
  		}
  	}
    //emailNode.addContent(getNavNodeWithIcon(address_book, "address_book", FONTSTYLE_PLAIN, "email"));
    
    Element imNode = new Element(ELEMENT_NODE);
    imNode.setAttribute(ATTRIBUTE_NAME, im);
    imNode.setAttribute(ATTRIBUTE_TYPE, "im");
    imNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD);
    imNode.setAttribute("__OPTTREE_META_open", "true");
    imNode.setAttribute(ATTRIBUTE_ICON_NAME, "im");    
    imNode.setAttribute(ATTRIBUTE_DRAGTARGET, "false");
    msgNode.addContent(imNode);

    imNode.addContent(getNavNodeWithIcon(add_im, "add_im", FONTSTYLE_PLAIN, "add"));
  	for (IMConnection conn : imConnections) {
  		if(conn.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT){
	  		int typeId = conn.getType();
	  		String connName = conn.getName();
	  		String imName = MessagingUtils.getConnectionTypeNameByID(typeId)+ " - " + connName;
	        Element nodeImConn = getNavNodeWithValue(imName, "im-conn", FONTSTYLE_PLAIN, "im", String.valueOf(conn.getId()));
	        nodeImConn.setAttribute(ATTRIBUTE_DRAGGABLE, "true");
	        nodeImConn.setAttribute(ATTRIBUTE_DRAGTARGET, "true");
	  		imNode.addContent(nodeImConn);
	  		
			//Delete Driver Code from Ryan *Start* >>>>>>>>
	  		nodeImConn.addContent(rightClickFunction(new String[] {"Delete IM Connection"}));
			//Delete Driver Code from Ryan *End* <<<<<<<<<
  		}
  	}

  	Element smsNode = new Element(ELEMENT_NODE);
  	smsNode.setAttribute(ATTRIBUTE_NAME, sms);
  	smsNode.setAttribute(ATTRIBUTE_TYPE, "sms");
  	smsNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD);
  	smsNode.setAttribute("__OPTTREE_META_open", "true");
  	smsNode.setAttribute(ATTRIBUTE_ICON_NAME, "sms");
  	smsNode.setAttribute(ATTRIBUTE_DRAGGABLE, "false");
    msgNode.addContent(smsNode);
    
  	Element addSmpp = new Element(ELEMENT_NODE);
  	addSmpp.setAttribute(ATTRIBUTE_NAME, addSmppTitle);
  	addSmpp.setAttribute(ATTRIBUTE_TYPE, "add-smpp");
  	addSmpp.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_PLAIN);
  	addSmpp.setAttribute("__OPTTREE_META_open", "true");
  	addSmpp.setAttribute(ATTRIBUTE_ICON_NAME, "add");
  	addSmpp.setAttribute(ATTRIBUTE_DRAGGABLE, "false");
  	addSmpp.setAttribute(ATTRIBUTE_DRAGTARGET, "false");
  	smsNode.addContent(addSmpp);  	

  	Element smsSett = new Element(ELEMENT_NODE);
  	smsSett.setAttribute(ATTRIBUTE_NAME, smsSettTitle);
  	smsSett.setAttribute(ATTRIBUTE_TYPE, "sms-sett");
  	smsSett.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_PLAIN);
  	smsSett.setAttribute("__OPTTREE_META_open", "true");
  	smsSett.setAttribute(ATTRIBUTE_ICON_NAME, "sms");
  	smsSett.setAttribute(ATTRIBUTE_DRAGGABLE, "false");
  	smsNode.addContent(smsSett);  	
  	
  	for (MdnSmpp smpp : smppGateways) {
  		if(smpp.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT){
	  		String number = smpp.getNumber();
	        Element smppNode = getNavNodeWithValue("SMPP " + number, "smpp-sett", FONTSTYLE_PLAIN, "smpp-sett", String.valueOf(smpp.getId()));
	        smppNode.setAttribute(ATTRIBUTE_DRAGGABLE, "true");
	        smppNode.setAttribute(ATTRIBUTE_DRAGTARGET, "true");
	  		smsNode.addContent(smppNode);
	  		
	  		//Delete Driver Code from Ryan *Start* >>>>>>>>
			smppNode.addContent(rightClickFunction(new String[] {"Delete SMPP Connection"}));
			//Delete Driver Code from Ryan *End* <<<<<<<<<
  		}
  	}  	
  	
   	root.addContent(msgNode);
    
  	//root.addContent(getNavNodeWithIcon(lookFeel, "lookandfeel", FONTSTYLE_PLAIN, "lookAndFeel"));
    //root.addContent(nodeSettings);
    
    return root;
  }
  
  public Element navigationDeployment(String languageFile){
    Element guiDef = this.getXMLLanguageFile(languageFile);
    String deployment = getGuiValueForElement(guiDef, "tree-deployment");
    String userGroups = getGuiValueForElement(guiDef, "tree-users-groups");
    String notifEmail = getGuiValueForElement(guiDef, "tree-notif-email");
    
    Element root = new Element(ROOT_ROOT);
    Element nodeDeployment = getNavNodeWithIcon(deployment, TYPE_DEPLOYMENT, FONTSTYLE_BOLD, true, "deployment");
    //nodeDeployment.addContent(getNavNodeWithIcon(userGroups, TYPE_USER_GROUPS, FONTSTYLE_PLAIN, "userGroups"));
    
    Element nodeEmailNotif = getNavNodeWithIcon(notifEmail, TYPE_NOTIF_EMAIL, FONTSTYLE_BOLD_ITALIC, true, "emailNotification");
    nodeEmailNotif.addContent(getNavNodeWithIcon("Administrator", TYPE_GROUP, FONTSTYLE_PLAIN, "singleAdmin"));
    nodeEmailNotif.addContent(getNavNodeWithIcon("Sales", TYPE_GROUP, FONTSTYLE_PLAIN, "singleUser"));
    nodeEmailNotif.addContent(getNavNodeWithIcon("Field", TYPE_GROUP, FONTSTYLE_PLAIN, "singleUser"));
    nodeEmailNotif.addContent(getNavNodeWithIcon("Tech", TYPE_GROUP, FONTSTYLE_PLAIN, "singleUser"));
    
    nodeDeployment.addContent(nodeEmailNotif);
    
    root.addContent(nodeDeployment);
    
    return root;
  }
  
  public Element dbConnection(String dbName){
    Element root = new Element(ROOT_ROOT);
    return root;
  }
  
  
  /**
   * TODO Need to pass in a DB wrapper object.
   * @param languageFile
   * @return
   */
  public Element navigationDatabases(String languageFile, List<DataSourceDobj> dbs, int projectId, String action, String result, int newConnId, String dbName){
    // Get the GUI definition file.
    Element guiDef = this.getXMLLanguageFile(languageFile);
    // Get the language elements we are interested in.
    String databases = getGuiValueForElement(guiDef, "tree-databases");
    String addDatabase = getGuiValueForElement(guiDef, "tree-add-database");
    String connection = getGuiValueForElement(guiDef, "tree-connection");
    String tables = getGuiValueForElement(guiDef, "tree-tables");
    String relationships = getGuiValueForElement(guiDef, "tree-relationships");
    String views = getGuiValueForElement(guiDef, "tree-views");
    String addView = getGuiValueForElement(guiDef, "tree-add-view");
    //String queries = getGuiValueForElement(guiDef, "tree-queries");
    String addSelectQuery = getGuiValueForElement(guiDef, "tree-add-select-query");
    String addInsertQuery = getGuiValueForElement(guiDef, "tree-add-insert-query");
    String addUpdateQuery = getGuiValueForElement(guiDef, "tree-add-update-query");
    //String otherSources = getGuiValueForElement(guiDef, "tree-other-sources");
    String email = getGuiValueForElement(guiDef, "tree-email");
    String addEmail = getGuiValueForElement(guiDef, "tree-add-email");
    String ws = getGuiValueForElement(guiDef, "tree-ws");
    String addWs = getGuiValueForElement(guiDef, "tree-add-ws");
    
    
    
    // Define the XML structure.
    Element root = new Element(ROOT_ROOT);    
  	root.setAttribute("action", action);
  	root.addContent(result);    
  	root.setAttribute("projectId", String.valueOf(projectId));
  	root.setAttribute("connId", String.valueOf(newConnId));
  	root.setAttribute("dbName", dbName);
  	
    Element dbsNode = new Element(ELEMENT_NODE);
    dbsNode.setAttribute(ATTRIBUTE_NAME, databases);
    dbsNode.setAttribute(ATTRIBUTE_TYPE, TYPE_DATABASES);
    dbsNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD);
    dbsNode.setAttribute("__OPTTREE_META_open", "true");
    dbsNode.setAttribute(ATTRIBUTE_ICON_NAME, "database");
    
    Element addDbNode = new Element(ELEMENT_NODE);
    addDbNode.setAttribute(ATTRIBUTE_NAME, addDatabase);
    addDbNode.setAttribute(ATTRIBUTE_TYPE, TYPE_ADD_DATABASE);
    addDbNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_PLAIN);
    addDbNode.setAttribute("__OPTTREE_META_open", "true");
    addDbNode.setAttribute(ATTRIBUTE_ICON_NAME, "add");    
    dbsNode.addContent(addDbNode);
    
    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
    for (DataSourceDobj db: dbs) {
      
      int connID = db.getId();
    	
      Element dbNode = new Element(ELEMENT_NODE);
      dbNode.setAttribute(ATTRIBUTE_NAME, db.getName());
      dbNode.setAttribute(ATTRIBUTE_ID, String.valueOf(connID));
      dbNode.setAttribute("projectId", String.valueOf(projectId));
      dbNode.setAttribute(ATTRIBUTE_TYPE, TYPE_DATABASE);
      dbNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD);
      dbNode.setAttribute("__OPTTREE_META_open", "true");
      dbNode.setAttribute(ATTRIBUTE_ICON_NAME, "database");
      dbNode.setAttribute(ATTRIBUTE_DRAGGABLE, "true");
      
      Element connNode = getNavNodeWithIcon(connection, TYPE_CONNECTION, FONTSTYLE_PLAIN, "connection");
      connNode.setAttribute("__OPTTREE_META_open", "true");
      dbNode.addContent(connNode);
 
      //create view node
      Element viewNode = getNavNodeWithIcon(views, TYPE_VIEWS, FONTSTYLE_PLAIN, "views");
      viewNode.setAttribute("__OPTTREE_META_open", "true");
      
      //Add 'add view' node to view node
      Element addViewNode = getNavNodeWithIcon(addView, TYPE_ADD_VIEW, FONTSTYLE_PLAIN, "add");
      addViewNode.setAttribute("__OPTTREE_META_open", "true");
      viewNode.addContent(addViewNode);
      
      //Add detail views to view node

      List<DataView> allViews;
      try {
    	  allViews = dataAgent.getAllMetaViews(connID, true);
          for (DataView eachView: allViews){
              Element eachViewNode = getNavNodeWithValue(eachView.getName(), TYPE_VIEW, FONTSTYLE_PLAIN, "view", String.valueOf(eachView.getId()));
              eachViewNode.setAttribute("__OPTTREE_META_open", "true");
              eachViewNode.setAttribute(ATTRIBUTE_DRAGGABLE, "true");
              
              
              
              //eachViewNode.setAttribute(ATTRIBUTE_DRAGTARGET, "true");              
              viewNode.addContent(eachViewNode);
          }
          
          //Add view node
          dbNode.addContent(viewNode);
      } catch (MdnException e) {
    	  // TODO Auto-generated catch block
    	  e.printStackTrace();
      }      
  
      
      dbsNode.addContent(dbNode);
    }
    
    root.addContent(dbsNode);
    
    //Statr comment
    Element wsNode = new Element(ELEMENT_NODE);
    wsNode.setAttribute(ATTRIBUTE_NAME, ws);
    wsNode.setAttribute(ATTRIBUTE_TYPE, TYPE_WEB_SERVICES);
    wsNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD);
    wsNode.setAttribute("__OPTTREE_META_open", "true");
    wsNode.setAttribute(ATTRIBUTE_ICON_NAME, "webservices");

    Element addWsNode = new Element(ELEMENT_NODE);
    addWsNode.setAttribute(ATTRIBUTE_NAME, addWs);
    addWsNode.setAttribute(ATTRIBUTE_TYPE, TYPE_ADD_WEB_SERVICE);
    addWsNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_PLAIN);
    addWsNode.setAttribute("__OPTTREE_META_open", "false");
    addWsNode.setAttribute(ATTRIBUTE_ICON_NAME, "add");    
    wsNode.addContent(addWsNode);
        
    
    List<WebServiceOperationDobj> operations;
	try {
		operations = dataAgent.getAllWebServiceOperations(projectId);
	    for (WebServiceOperationDobj eachOp : operations){
	        //Add 'query' node to view node
	        Element queryNode = getNavNodeWithExtraValue(eachOp.getName(), TYPE_WEB_SERVICE_OPERATION, FONTSTYLE_PLAIN, "webservices", String.valueOf(eachOp.getId()), eachOp.getUrl(), eachOp.getOperation());
	        queryNode.setAttribute(ATTRIBUTE_DRAGGABLE, "true");
	        wsNode.addContent(queryNode);            	  
	    }	     
	} catch (MdnException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
     
    root.addContent(wsNode); 
    
    /*
    Element emailNode = new Element(ELEMENT_NODE);
    emailNode.setAttribute(ATTRIBUTE_NAME, email);
    emailNode.setAttribute(ATTRIBUTE_TYPE, TYPE_EMAIL);
    emailNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD);
    emailNode.setAttribute("__OPTTREE_META_open", "true");
    emailNode.setAttribute(ATTRIBUTE_ICON_NAME, "db_email");
    //emailNode.addContent(getNavNodeWithIcon("MS Exchange", "Email", FONTSTYLE_PLAIN, "email"));
    Element addEmailNode = new Element(ELEMENT_NODE);
    addEmailNode.setAttribute(ATTRIBUTE_NAME, addEmail);
    addEmailNode.setAttribute(ATTRIBUTE_TYPE, TYPE_ADD_EMAIL);
    addEmailNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_PLAIN);
    addEmailNode.setAttribute("__OPTTREE_META_open", "false");
    addEmailNode.setAttribute(ATTRIBUTE_ICON_NAME, "add");   
    emailNode.addContent(addEmailNode);
    root.addContent(emailNode);
    */
    //root.addContent(dsNode);
    
    return root;
  }

  public Document exportProject(int projectId, boolean includeUG, boolean includeSetting) throws MdnException{
	  	IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	  	List<DataSourceDobj> dbs = null;
	  	
	  	ProjectDobj project = dataAgent.getNavProjectById(projectId);
	  	
	  	Element documentRoot = new Element("Project");
	  	//DocType dt = new DocType(ProjectDobj.ENT_PROJECT);
	  	documentRoot.setAttribute("includeUsersGroups", includeUG ? "true" : "false");
	  	documentRoot.setAttribute("includeSettings", includeSetting ? "true" : "false");
	  	
	  	Document document = new Document(documentRoot);//, dt
	  	
	  	//Element dummyRoot = new Element("Detail");
	  	
	  	
	  	Element root = new Element(ProjectDobj.ENT_PROJECT);
	  	root.setAttribute(ProjectDobj.FLD_ID, String.valueOf(project.getId()));
	  	root.setAttribute(ProjectDobj.FLD_NAME, project.getName());
	  	root.setAttribute(ProjectDobj.FLD_DESCRIPTION, project.getDescription());
	  	root.setAttribute(ProjectDobj.FLD_DEL_STATUS, String.valueOf(project.getDelStatus()));
	  	
	  	
		dbs = dataAgent.getAllDbConnections(projectId);
		for (DataSourceDobj db: dbs) {		  
			int connID = db.getId();
			
			Element dbNode = new Element(DataSourceDobj.ENT_DATASOURCE);
			dbNode.setAttribute(DataSourceDobj.FLD_ID, String.valueOf(db.getId()));
			dbNode.setAttribute(DataSourceDobj.FLD_NAME, db.getName());
			dbNode.setAttribute(DataSourceDobj.FLD_DESCRIPTION, db.getDescription() == null ? "null" : db.getDescription());
			dbNode.setAttribute(DataSourceDobj.FLD_PROJECT_ID, String.valueOf(db.getProjectId()));
			dbNode.setAttribute(DataSourceDobj.FLD_DEL_STATUS, String.valueOf(db.getDelStatus()));
		  	if (db instanceof JdbcDataSourceDobj){
		  		JdbcDataSourceDobj jdbcDataSourceDobj = (JdbcDataSourceDobj)db;
		  		
		  		dbNode.setAttribute(DataSourceDobj.FLD_JDBC_USER, jdbcDataSourceDobj.getJdbcUser() == null ? "null" : jdbcDataSourceDobj.getJdbcUser());
		  		dbNode.setAttribute(DataSourceDobj.FLD_JDBC_PASSWORD, jdbcDataSourceDobj.getJdbcPassword() == null ? "null" : jdbcDataSourceDobj.getJdbcPassword());
		  		dbNode.setAttribute(DataSourceDobj.FLD_JDBC_DRIVERID, String.valueOf(jdbcDataSourceDobj.getJdbcDriverId()));
		  		dbNode.setAttribute(DataSourceDobj.FLD_JDBC_URL, jdbcDataSourceDobj.getJdbcUrl());
		  		
		  		JdbcDriver jdbcDriver = dataAgent.getJdbcDriverById(jdbcDataSourceDobj.getJdbcDriverId());
		  		if (!includeSetting && jdbcDriver!= null){
		  			//need to export jdbcDriver also
					Element node = new Element(JdbcDriver.ENT_JDBCDRIVER);
										
					node.setAttribute(JdbcDriver.FLD_ID, String.valueOf(jdbcDriver.getId()));
					node.setAttribute(JdbcDriver.FLD_NAME, jdbcDriver.getName());
					node.setAttribute(JdbcDriver.FLD_DRIVER, jdbcDriver.getDriver());
					node.setAttribute(JdbcDriver.FLD_URL_FORMAT, jdbcDriver.getUrlFormat());
					node.setAttribute(JdbcDriver.FLD_DESCRIPTION, jdbcDriver.getDescription() == null ? "" : jdbcDriver.getDescription());
					node.setAttribute(JdbcDriver.FLD_FILE_NAME, jdbcDriver.getFileName() == null? "": jdbcDriver.getFileName());
					node.setAttribute(JdbcDriver.FLD_DEL_STATUS, jdbcDriver.getDelStatus()+"");
					
					dbNode.addContent(node);
		  		}
		  	}
		  	
		  	
			
			List<EntityDobj> tables = dataAgent.getAllMetaTables(connID, true);
			for (EntityDobj table : tables){
				Element tableNode = new Element(EntityDobj.ENT_ENTITY);
				tableNode.setAttribute(EntityDobj.FLD_ID, String.valueOf(table.getId()));
				tableNode.setAttribute(EntityDobj.FLD_NAME, table.getName());
				tableNode.setAttribute(EntityDobj.FLD_DESCRIPTION, table.getDescription() == null ? "null" : table.getDescription());
				tableNode.setAttribute(EntityDobj.FLD_DSID, String.valueOf(table.getDataSourceId()));
				tableNode.setAttribute(EntityDobj.FLD_FLAGS, String.valueOf(table.getFlags()));
				
				dbNode.addContent(tableNode);
				
				List<FieldDobj> fields = table.getFields();
				for (FieldDobj field : fields){
					Element fieldNode = new Element(FieldDobj.ENT_FIELD);
					fieldNode.setAttribute(FieldDobj.FLD_ID, String.valueOf(field.getId()));
					fieldNode.setAttribute(FieldDobj.FLD_NAME, field.getName());
					fieldNode.setAttribute(FieldDobj.FLD_FLAGS, String.valueOf(field.getFlags()));
					fieldNode.setAttribute(FieldDobj.FLD_TYPE, String.valueOf(field.getType()));
					fieldNode.setAttribute(FieldDobj.FLD_NATIVETYPE, String.valueOf(field.getNativeType()));
					fieldNode.setAttribute(FieldDobj.FLD_DESCRIPTION, field.getDescription() == null ? "null" : field.getDescription());
					fieldNode.setAttribute(FieldDobj.FLD_DSID, String.valueOf(field.getDsId()));
					fieldNode.setAttribute(FieldDobj.FLD_ENTITYID, String.valueOf(field.getEntityId()));
					fieldNode.setAttribute(FieldDobj.FLD_COLUMN_SIZE, String.valueOf(field.getColumnSize()));
					fieldNode.setAttribute(FieldDobj.FLD_DECIMAL_DIGITS, String.valueOf(field.getDecimalDigits()));
					
					tableNode.addContent(fieldNode);
				}				
			}
			
			
			List<DataView> allViews = dataAgent.getAllMetaViews(connID, true);
			for (DataView eachView: allViews){
				Element viewNode = new Element(DataView.ENT_DATAVIEW);
				viewNode.setAttribute(DataView.FLD_ID, String.valueOf(eachView.getId()));
				viewNode.setAttribute(DataView.FLD_NAME, eachView.getName());
				viewNode.setAttribute(DataView.FLD_DESCRIPTION, eachView.getDescription() == null ? "" : eachView.getDescription());
				viewNode.setAttribute(DataView.FLD_SOURCE_DSID, String.valueOf(eachView.getSourceDsId()));
				viewNode.setAttribute(DataView.FLD_DEL_STATUS, String.valueOf(eachView.getDelStatus()));
				
				dbNode.addContent(viewNode);
				
				List<DataViewField> fields = eachView.getFields();
				for (DataViewField field: fields){
					Element fieldNode = new Element(DataViewField.ENT_DVFIELD);
					fieldNode.setAttribute(DataViewField.FLD_ID, String.valueOf(field.getId()));
					fieldNode.setAttribute(DataViewField.FLD_NAME, field.getName());
					fieldNode.setAttribute(DataViewField.FLD_DATAVIEWID, String.valueOf(field.getDataViewId()));
					fieldNode.setAttribute(DataViewField.FLD_DESCRIPTION, field.getDescription());
					fieldNode.setAttribute(DataViewField.FLD_SOURCE_FIELD, field.getSourceField());
					fieldNode.setAttribute(DataViewField.FLD_SOURCE_ENTITY, field.getSourceEntity());
					fieldNode.setAttribute(DataViewField.FLD_DISPLAY_NAME, field.getDisplayName());
					fieldNode.setAttribute(DataViewField.FLD_FLAGS, String.valueOf(field.getFlags()));
					fieldNode.setAttribute(DataViewField.FLD_OPTION_LIST, field.getOptionList() == null ? "null" : field.getOptionList());
					fieldNode.setAttribute(DataViewField.FLD_TYPE, String.valueOf(field.getType()));
					viewNode.addContent(fieldNode);
				}
			}
			root.addContent(dbNode);
		}	
		List<QueryDobj> allMsgInfo = dataAgent.getAllQueries(projectId);
		for (QueryDobj msg: allMsgInfo){
			Element msgNode = new Element(QueryDobj.ENT_QUERY);
			msgNode.setAttribute(QueryDobj.FLD_ID, String.valueOf(msg.getId()));
			msgNode.setAttribute(QueryDobj.FLD_NAME, msg.getName());
			msgNode.setAttribute(QueryDobj.FLD_PARENTID, String.valueOf(msg.getViewOrTableId()));
			msgNode.setAttribute(QueryDobj.FLD_TYPE, msg.getType());
			msgNode.setAttribute(QueryDobj.FLD_CRITERIA, msg.getCriteriaString());
			msgNode.setAttribute(QueryDobj.FLD_SORTS, msg.getSortString() == null ? "" : msg.getSortString());
			msgNode.setAttribute(QueryDobj.FLD_DESCRIPTION, msg.getDescription() == null? "" : msg.getDescription());
			msgNode.setAttribute(QueryDobj.FLD_GROUPFIELDID, String.valueOf(msg.getGroupFieldId()));
			if (msg instanceof DirectQueryDobj)
				msgNode.setAttribute(QueryDobj.FLD_RAWQUERY, ((DirectQueryDobj)msg).getRawSQL());
			msgNode.setAttribute(QueryDobj.FLD_EMAIL_KEYWORD, msg.getEmailKeyword() == null ? "" : msg.getEmailKeyword());
			msgNode.setAttribute(QueryDobj.FLD_EMAIL_ADDRESS_ID, String.valueOf(msg.getEmailAddressId()));
			msgNode.setAttribute(QueryDobj.FLD_EMAIL_DISPLAY_RESULT, String.valueOf(msg.getEmailDisplayResult()));
			msgNode.setAttribute(QueryDobj.FLD_MOBILE_STATUS, String.valueOf(msg.getMobileStatus()));
			msgNode.setAttribute(QueryDobj.FLD_MOBILE_DISPLAY_RESULT, String.valueOf(msg.getMobileDisplayResult()));
			msgNode.setAttribute(QueryDobj.FLD_IM_STATUS, String.valueOf(msg.getIMStatus()));
			msgNode.setAttribute(QueryDobj.FLD_IM_DISPLAY_RESULT, String.valueOf(msg.getImDisplayResult()));
			msgNode.setAttribute(QueryDobj.FLD_IM_KEYWORD, msg.getImKeyword() == null ? "" : msg.getImKeyword());
			msgNode.setAttribute(QueryDobj.FLD_DEL_STATUS, String.valueOf(msg.getDelStatus()));
			msgNode.setAttribute(QueryDobj.FLD_SMS_KEYWORD, msg.getSmsKeyword() == null ? "" : msg.getSmsKeyword());
			msgNode.setAttribute(QueryDobj.FLD_DB_ID, String.valueOf(msg.getDatabaseId()));
			msgNode.setAttribute(QueryDobj.FLD_CONDITION_SEPERATOR, msg.getConditionSeperator() == null ? "" :  msg.getConditionSeperator());
			msgNode.setAttribute(QueryDobj.FLD_RESPONSE, msg.getResponse() == null ? "" : msg.getResponse());
			msgNode.setAttribute(QueryDobj.FLD_TIMEOUT, msg.getTimeout());
			msgNode.setAttribute(QueryDobj.FLD_DS_STATUS, String.valueOf(msg.getDatasourceStatus()));
			msgNode.setAttribute(QueryDobj.FLD_WS_ID, String.valueOf(msg.getWebServiceId()));
			msgNode.setAttribute(QueryDobj.FLD_PROJECT_ID, String.valueOf(msg.getProjectId()));
			
			if (!includeSetting){
	  			Element nodeEmail = new Element(MdnEmailSetting.ENT_EMAIL);
	  			MdnEmailSetting mdnEmail = dataAgent.getEmailSettingById(msg.getEmailAddressId());
	  			nodeEmail.setAttribute(MdnEmailSetting.FLD_ID, mdnEmail.getId()+"");
		        nodeEmail.setAttribute(MdnEmailSetting.FLD_EMAIL_ADDRESS, mdnEmail.getEmailAddress());
		        nodeEmail.setAttribute(MdnEmailSetting.FLD_IMAP_HOST, mdnEmail.getImapHost());
		        nodeEmail.setAttribute(MdnEmailSetting.FLD_IMAP_USER_NAME, mdnEmail.getImapUserName());		        
		        nodeEmail.setAttribute(MdnEmailSetting.FLD_IMAP_PASSWORD, mdnEmail.getImapPassword());
		        nodeEmail.setAttribute(MdnEmailSetting.FLD_IMAP_PORT, mdnEmail.getImapPort());
		        nodeEmail.setAttribute(MdnEmailSetting.FLD_IMAP_ENCRYPTED_TYPE, mdnEmail.getImapEncryptedType()+"");
		        nodeEmail.setAttribute(MdnEmailSetting.FLD_SMTP_HOST, mdnEmail.getSmtpHost());
		        nodeEmail.setAttribute(MdnEmailSetting.FLD_SMTP_USERNAME, mdnEmail.getSmtpUsername());
		        nodeEmail.setAttribute(MdnEmailSetting.FLD_SMTP_PASSWORD, mdnEmail.getSmtpPassword());
		        nodeEmail.setAttribute(MdnEmailSetting.FLD_SMTP_PORT, mdnEmail.getSmtpPort());
		        nodeEmail.setAttribute(MdnEmailSetting.FLD_SMTP_ENCRYPTED_TYPE, mdnEmail.getSmtpEncryptedType()+"");
		        nodeEmail.setAttribute(MdnEmailSetting.FLD_PROJECT_ID, mdnEmail.getProjectId()+"");
		        nodeEmail.setAttribute(MdnEmailSetting.FLD_DEL_STATUS, mdnEmail.getDelStatus()+"");
		        msgNode.addContent(nodeEmail);				
			}
			
			List<QueryCriteriaDobj> queryCriteria = dataAgent.getQueryCriteriaByQueryID(msg.getId(), "");
			for (QueryCriteriaDobj qc : queryCriteria){
				Element qcNode = new Element(QueryCriteriaDobj.ENT_QUERY_CRITERIA);
				qcNode.setAttribute(QueryCriteriaDobj.FLD_ID, String.valueOf(qc.getId()));
				qcNode.setAttribute(QueryCriteriaDobj.FLD_QUERY_ID, String.valueOf(qc.getQueryId()));
				qcNode.setAttribute(QueryCriteriaDobj.FLD_VALUEORCOND, qc.getValueOrCondition());
				qcNode.setAttribute(QueryCriteriaDobj.FLD_ROW_NO, String.valueOf(qc.getRowNo()));
				qcNode.setAttribute(QueryCriteriaDobj.FLD_TYPE, qc.getType());
				qcNode.setAttribute(QueryCriteriaDobj.FLD_USED, String.valueOf(qc.getUsed()));
				qcNode.setAttribute(QueryCriteriaDobj.FLD_INDENT, String.valueOf(qc.getIndent()));
				qcNode.setAttribute(QueryCriteriaDobj.FLD_PARENT, qc.getParent() == null ? "" : qc.getParent());
				qcNode.setAttribute(QueryCriteriaDobj.FLD_NUMBER, String.valueOf(qc.getNumber()));
				qcNode.setAttribute(QueryCriteriaDobj.FLD_NAME, qc.getName());
				qcNode.setAttribute(QueryCriteriaDobj.FLD_COMP_ID, String.valueOf(qc.getCompId()));
				qcNode.setAttribute(QueryCriteriaDobj.FLD_COMPARISON, qc.getComparison());
				qcNode.setAttribute(QueryCriteriaDobj.FLD_VALUE, qc.getValue());
				qcNode.setAttribute(QueryCriteriaDobj.FLD_CONNECTION, qc.getConnection());
				qcNode.setAttribute(QueryCriteriaDobj.FLD_VALUE2, qc.getValue2());
				qcNode.setAttribute(QueryCriteriaDobj.FLD_GROUPING, qc.getGrouping());
				qcNode.setAttribute(QueryCriteriaDobj.FLD_VALUE_USERINPUT_SEQ, qc.getValueUserInputSeq());
				qcNode.setAttribute(QueryCriteriaDobj.FLD_VALUE2_USERINPUT_SEQ, qc.getValue2UserInputSeq());
				qcNode.setAttribute(QueryCriteriaDobj.FLD_OBJECT_TYPE, qc.getObjectType() == null ? "" : qc.getObjectType());
				
				msgNode.addContent(qcNode);
			}			
			
			root.addContent(msgNode);
			
			List<UserReply> userReplies = dataAgent.getUserRepliesByParentId(msg.getId());
			for (UserReply ur : userReplies){
				Element urNode = appendURToExportFile(ur, dataAgent);
				
				List<String> childrenList2 = parsChildrenId(ur.getChildren());
				for (int j=0; j<childrenList2.size() ; j++) {//2	
					String childId = childrenList2.get(j);
					UserReply urChild = dataAgent.getUserReplyById(Integer.parseInt(childId));
					Element nodeUR2 = appendURToExportFile(urChild, dataAgent);
					urNode.addContent(nodeUR2);
					
					List<String> childrenList3 = parsChildrenId(urChild.getChildren());	
					for (int k=0; k<childrenList3.size() ; k++) {//3
						String childId3 = childrenList3.get(k);
						UserReply urChild3 = dataAgent.getUserReplyById(Integer.parseInt(childId3));
						Element nodeUR3 = appendURToExportFile(urChild3, dataAgent);
						nodeUR2.addContent(nodeUR3);
						
						List<String> childrenList4 = parsChildrenId(urChild3.getChildren());
						for (int d=0; d<childrenList4.size() ; d++) {//4
							String childId4 = childrenList4.get(d);
							UserReply urChild4 = dataAgent.getUserReplyById(Integer.parseInt(childId4));
							Element nodeUR4 = appendURToExportFile(urChild4, dataAgent);
							nodeUR3.addContent(nodeUR4);
							List<String> childrenList5 = parsChildrenId(urChild4.getChildren());
							for (int e=0; e<childrenList5.size() ; e++) {//5
								String childId5 = childrenList5.get(e);
								UserReply urChild5 = dataAgent.getUserReplyById(Integer.parseInt(childId5));
								Element nodeUR5 = appendURToExportFile(urChild5, dataAgent);
								nodeUR4.addContent(nodeUR5);
							}//5
						}//4
					}//3
				}//2
				
				msgNode.addContent(urNode);
			}
		}
		documentRoot.addContent(root);
		if (includeUG){
		    Vector<User> users;
		    Vector<Group> groups;
			users = dataAgent.getUsers();   
			groups = dataAgent.getGroups(projectId);
			
		    Element nodeGroups = new Element("Groups");
		    
		    for (Group group : groups) {
		      Element nodeGroup = new Element(Group.ENT_GROUP);
		      nodeGroup.setAttribute(Group.FLD_ID, group.getId() + "");
		      nodeGroup.setAttribute(Group.FLD_NAME, group.getName());
		      nodeGroup.setAttribute(Group.FLD_DESCRIPTION, group.getDescription() == null ? "" : group.getDescription());
		      nodeGroup.setAttribute(Group.FLD_GUEST, group.getGuest() + "");
		      nodeGroup.setAttribute(Group.FLD_STATUS_DEL, group.getDelStatus());
		      nodeGroup.setAttribute(Group.FLD_PROJECT_ID, group.getProjectId()+"");			    	  
		      nodeGroups.addContent(nodeGroup);
		      

	    	  List<GroupMembership> usersListRelatedToGroup = dataAgent.getUserGroupByGroupId(group.getId(), projectId);
	    	  
	    	  for(GroupMembership membership: usersListRelatedToGroup){
	              Element nodeMembership = new Element(GroupMembership.ENT_GROUPMEMBERSHIP);
	              nodeMembership.setAttribute(GroupMembership.FLD_GROUPID, membership.getGroupId()+ "");
	              nodeMembership.setAttribute(GroupMembership.FLD_USERID, membership.getUserId() + "");
	              nodeMembership.setAttribute(GroupMembership.FLD_PROJECT_ID, String.valueOf(membership.getProjectId()));
	              nodeGroup.addContent(nodeMembership);    		  
	    	  }
	    	  
	    	  List<GroupDataView> groupDataViewLst = dataAgent.getGroupViewsPermissions(group.getId());
	    	  for (GroupDataView groupDataView: groupDataViewLst){
	    		  Element nodeGroupDataView = new Element(GroupDataView.ENT_GROUPDATAVIEW);
	    		  nodeGroupDataView.setAttribute(GroupDataView.FLD_ID, groupDataView.getId()+"");
	    		  nodeGroupDataView.setAttribute(GroupDataView.FLD_GROUPID, groupDataView.getGroupId()+"");
	    		  nodeGroupDataView.setAttribute(GroupDataView.FLD_DATAVIEWID, groupDataView.getDataViewId()+"");
	    		  nodeGroup.addContent(nodeGroupDataView);
	    	  }
		    }
		    
		    documentRoot.addContent(nodeGroups);
		    
    	    Element nodeUsers = new Element("Users");
    	    
    	    for (User user : users) {
    	      Element nodeUser = new Element(User.ENT_USER);
    	      nodeUser.setAttribute(User.FLD_ID, user.getId() + "");
    	      nodeUser.setAttribute(User.FLD_NAME, user.getName());
    	      nodeUser.setAttribute(User.FLD_PASSWORD, user.getPassword());
    	      nodeUser.setAttribute(User.FLD_FIRST_NAME, user.getFirstName());
    	      nodeUser.setAttribute(User.FLD_LAST_NAME, user.getLastName());
    	      nodeUser.setAttribute(User.FLD_DEL_STATUS, user.getDelStatus());
    	      nodeUser.setAttribute(User.FLD_EMAIL, user.getEmail() == null ? "" : user.getEmail());
    	      nodeUser.setAttribute(User.FLD_MOBILE, user.getMobile()== null? "" : user.getMobile());
    	      nodeUser.setAttribute(User.FLD_NOTES, user.getNotes() == null ? "" : user.getNotes());
    	      nodeUser.setAttribute(User.FLD_GROUP_ID, user.getGroupId()+"");
    	      nodeUser.setAttribute(User.FLD_PRIVILEGE, user.getPrivilege());
    	      nodeUser.setAttribute(User.FLD_LICENSE_TYPE, user.getLicenseType());    	      
    	      nodeUsers.addContent(nodeUser);
    	      
    	      List<UserCustomField> userCustomFields = dataAgent.getUserCustomFieldsByUserId(user.getId());
    	      for (UserCustomField userCustomField : userCustomFields) {
    	      	Element userCustomNode = new Element(UserCustomField.ENT_USER_CUSTOM_FIELD);
	  			userCustomNode.setAttribute(UserCustomField.FLD_ID, userCustomField.getId()+"");
	  			userCustomNode.setAttribute(UserCustomField.FLD_USER_ID, userCustomField.getUserId()+"");
	  			userCustomNode.setAttribute(UserCustomField.FLD_CUSTOM_ID, userCustomField.getCustomId()+"");
	  			userCustomNode.setAttribute(UserCustomField.FLD_CUSTOM_PARAM, userCustomField.getParameter());
	  			nodeUser.addContent(userCustomNode);
    	  		
    	  		} 
    	      
    	      List<IMContact> contacts = dataAgent.getImContactsByUserID(user.getId());
    	      for (IMContact contact : contacts) {
      	      	Element contactNode = new Element(IMContact.ENT_IM_CONTACT);
      	      	contactNode.setAttribute(IMContact.FLD_ID, contact.getId()+"");
      	      	contactNode.setAttribute(IMContact.FLD_IM_CONTACT_NAME, contact.getName());
      	      	contactNode.setAttribute(IMContact.FLD_MDN_USER_ID, contact.getUserId()+"");
      	      	contactNode.setAttribute(IMContact.FLD_IM_MDN_CONNECTION_TYPE, contact.getImConnectionType()+"");
      	      	contactNode.setAttribute(IMContact.FLD_PROJECT_ID, contact.getProjectId()+"");
  	  			nodeUser.addContent(contactNode);
      	  		
      	  		} 
    	    }
    	    
    	    Element nodeCustomFields = new Element("CustomFields");
    	    List<CustomField> allCustomFields = new ArrayList<CustomField>();
    	    allCustomFields = dataAgent.getAllCustomFields();
    	    for (CustomField customField : allCustomFields){
    	    	Element customNode = new Element(CustomField.ENT_CUSTOM_FIELD);
    	    	customNode.setAttribute(CustomField.FLD_ID, customField.getId()+"");
    	    	customNode.setAttribute(CustomField.FLD_NAME, customField.getName());
    	    	nodeCustomFields.addContent(customNode);
    	    } 
    	    
    	    nodeUsers.addContent(nodeCustomFields);
    	    
    	    if (!includeSetting){
    	    	//need to include imconnections
    		    Element imNode = new Element("im");
    		    nodeUsers.addContent(imNode);
    		    
    		    List<IMConnection> imConnections = dataAgent.getAllIMConnections();
    		    for (IMConnection conn : imConnections) {
    		  		if(conn.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT){
    			  		Element nodeImConn = new Element(IMConnection.ENT_IM_CONNECTION);
    			        nodeImConn.setAttribute(IMConnection.FLD_ID, conn.getId()+"");
    			        nodeImConn.setAttribute(IMConnection.FLD_NAME, conn.getName());
    			        nodeImConn.setAttribute(IMConnection.FLD_PASSWORD, conn.getPassword());
    			        nodeImConn.setAttribute(IMConnection.FLD_TYPE, conn.getType()+"");
    			        nodeImConn.setAttribute(IMConnection.FLD_STATUS, conn.getStatus()+"");
    			        nodeImConn.setAttribute(IMConnection.FLD_USER_NAME, conn.getUserName());
    			        nodeImConn.setAttribute(IMConnection.FLD_STATUS_DATETIME, conn.getStatusDesc()+"");
    			        nodeImConn.setAttribute(IMConnection.FLD_PROJECT_ID, conn.getProjectId()+"");
    			        nodeImConn.setAttribute(IMConnection.FLD_DEL_STATUS, conn.getDelStatus()+"");		        
    			        imNode.addContent(nodeImConn);
    		  		}
    		  	}    	    	
    	    }
    	    
    	    
    	    documentRoot.addContent(nodeUsers);    	    
		}
		
		if (includeSetting){
		    Element settingRoot = new Element("Settings");
		    documentRoot.addContent(settingRoot);
		    
			List<IMConnection> imConnections;
			List<MdnEmailSetting> mdnEmailsServer;
			List<JdbcDriver> list = null;
			
			mdnEmailsServer = dataAgent.getMdnEmailAddresses();
			list = dataAgent.getAllJdbcDrivers();
		    List<LanguageDobj> languages = dataAgent.getAllLanguages();
			    
			Element langNode = new Element("Languages");
		    settingRoot.addContent(langNode);
		    for (LanguageDobj lang: languages){
		    	Element enNode = new Element(LanguageDobj.ENT_LANGUAGE);
		    	enNode.setAttribute(LanguageDobj.FLD_ID, lang.getId()+"");
		    	enNode.setAttribute(LanguageDobj.FLD_NAME, lang.getName());
		    	enNode.setAttribute(LanguageDobj.FLD_FILE_NAME, lang.getFileName());
		    	enNode.setAttribute(LanguageDobj.FLD_DEFAULT, lang.isDefault()+"");
		    	enNode.setAttribute(LanguageDobj.FLD_DEL_STATUS, lang.getDelStatus()+"");
		    	langNode.addContent(enNode);
		    }
		    
		    Element driversNode = new Element("Drivers");
		    if (list != null){
				for (JdbcDriver jdbcDriver : list) {
					Element node = new Element(JdbcDriver.ENT_JDBCDRIVER);
					node.setAttribute(JdbcDriver.FLD_ID, String.valueOf(jdbcDriver.getId()));
					node.setAttribute(JdbcDriver.FLD_NAME, jdbcDriver.getName());
					node.setAttribute(JdbcDriver.FLD_DRIVER, jdbcDriver.getDriver());
					node.setAttribute(JdbcDriver.FLD_URL_FORMAT, jdbcDriver.getUrlFormat());
					node.setAttribute(JdbcDriver.FLD_DESCRIPTION, jdbcDriver.getDescription() == null ? "" : jdbcDriver.getDescription());
					node.setAttribute(JdbcDriver.FLD_FILE_NAME, jdbcDriver.getFileName() == null? "": jdbcDriver.getFileName());
					node.setAttribute(JdbcDriver.FLD_DEL_STATUS, jdbcDriver.getDelStatus()+"");
					driversNode.addContent(node);
				}
			  }
		    settingRoot.addContent(driversNode);
		    
		    //Messagin Settings
		    Element msgNode = new Element("MessagingSettings");
		    
			MdnMessageSeparator msgSep = dataAgent.getMessageSeparator();
			if(msgSep != null){
			    Element msgSepNode = new Element(MdnMessageSeparator.ENT_MSG_SEPARATOR);
			    msgSepNode.setAttribute(MdnMessageSeparator.FLD_ID, msgSep.getId()+"");
			    msgSepNode.setAttribute(MdnMessageSeparator.FLD_PROJECT_ID, msgSep.getProjectId()+"");
			    msgSepNode.setAttribute(MdnMessageSeparator.FLD_CONDITION_SEPERATOR, msgSep.getConditionSeperator());   
			  	msgNode.addContent(msgSepNode);
			}			    

		    
		  	Element msgControls = new Element("MessageControls");
		  	msgNode.addContent(msgControls);  
		  	
		    //boolean limitation = dataAgent.getPublicGroupFlag();//Unlimited = true, limited = false		    
		    TemporaryBlockContacInfo msgControlsInfo = null;
		    MessagingSettingDetails imPublicMsgInfo = null;
		    MessagingSettingDetails smsPublicMsgInfo = null;
		    MessagingSettingDetails emailPublicMsgInfo = null;
		    List<BlockContacts> blockContacts = null;	    
	    	msgControlsInfo = dataAgent.getTempBlockContacts(); 
		    imPublicMsgInfo = dataAgent.getGuestMsgInfo("IM");
		    smsPublicMsgInfo = dataAgent.getGuestMsgInfo("SMS");
		    emailPublicMsgInfo = dataAgent.getGuestMsgInfo("Email");
	    	blockContacts = dataAgent.getBlockContacts();
		  	if(msgControlsInfo != null){
			  	Element tempBlockNode = new Element(TemporaryBlockContacInfo.ENT_TEMP_BLOCK_INFO);
			  	tempBlockNode.setAttribute(TemporaryBlockContacInfo.FLD_ID, msgControlsInfo.getId()+""); 
			  	tempBlockNode.setAttribute(TemporaryBlockContacInfo.FLD_MAX_MSG, msgControlsInfo.getMaxMessage()+"");
			  	tempBlockNode.setAttribute(TemporaryBlockContacInfo.FLD_MAX_PERIOD, msgControlsInfo.getMaxPeriod());
			  	tempBlockNode.setAttribute(TemporaryBlockContacInfo.FLD_CANCEL_PERIOD, msgControlsInfo.getCancelPeriod());
			  	tempBlockNode.setAttribute(TemporaryBlockContacInfo.FLD_REPLY, msgControlsInfo.getReply());
			  	msgControls.addContent(tempBlockNode);
		  	}
	    	
	    	
		  	//msgControls.setAttribute("unlimited", String.valueOf(limitation));
		  	
		  	Element emailGuestMsgNode = new Element(MessagingSettingDetails.ENT_MSG_SETT_INFO);
		  	emailGuestMsgNode.setAttribute(MessagingSettingDetails.FLD_ID, emailPublicMsgInfo.getId()+"");
		  	emailGuestMsgNode.setAttribute(MessagingSettingDetails.FLD_TYPE, emailPublicMsgInfo.getType());
		  	emailGuestMsgNode.setAttribute(MessagingSettingDetails.FLD_STATUS, emailPublicMsgInfo.getState()+"");
		  	emailGuestMsgNode.setAttribute(MessagingSettingDetails.FLD_TOTAL_MSG_COUNT, String.valueOf(emailPublicMsgInfo.getTotalMsgCount()));
		  	msgControls.addContent(emailGuestMsgNode);
		  	
		  	Element imGuestMsgNode = new Element(MessagingSettingDetails.ENT_MSG_SETT_INFO);
		  	imGuestMsgNode.setAttribute(MessagingSettingDetails.FLD_ID, imPublicMsgInfo.getId()+"");
		  	imGuestMsgNode.setAttribute(MessagingSettingDetails.FLD_TYPE, imPublicMsgInfo.getType());
		  	imGuestMsgNode.setAttribute(MessagingSettingDetails.FLD_STATUS, imPublicMsgInfo.getState()+"");
		  	imGuestMsgNode.setAttribute(MessagingSettingDetails.FLD_TOTAL_MSG_COUNT, String.valueOf(imPublicMsgInfo.getTotalMsgCount()));
		  	msgControls.addContent(imGuestMsgNode);
		  	
		  	Element smsGuestMsgNode = new Element(MessagingSettingDetails.ENT_MSG_SETT_INFO);
		  	smsGuestMsgNode.setAttribute(MessagingSettingDetails.FLD_ID, smsPublicMsgInfo.getId()+"");
		  	smsGuestMsgNode.setAttribute(MessagingSettingDetails.FLD_TYPE, smsPublicMsgInfo.getType());
		  	smsGuestMsgNode.setAttribute(MessagingSettingDetails.FLD_STATUS, smsPublicMsgInfo.getState()+"");
		  	smsGuestMsgNode.setAttribute(MessagingSettingDetails.FLD_TOTAL_MSG_COUNT, String.valueOf(smsPublicMsgInfo.getTotalMsgCount()));
		  	msgControls.addContent(smsGuestMsgNode);
		  	
		  	Element blockContractsNode = new Element("BlockContacts");
		  	for (BlockContacts bc : blockContacts) {
		  		Element nodeBc = new Element(BlockContacts.ENT_BLOCK_CONTACTS);		  		
		  		nodeBc.setAttribute(BlockContacts.FLD_TYPE, bc.getType());
		  		nodeBc.setAttribute(BlockContacts.FLD_CONTACT, bc.getContact());	 
		  		nodeBc.setAttribute(BlockContacts.FLD_ID, String.valueOf(bc.getId()));
		  		blockContractsNode.addContent(nodeBc);
			}	  
		  	msgControls.addContent(blockContractsNode);
		    
		    
		  	
		  	Element emailNode = new Element("Emails");
		    msgNode.addContent(emailNode);
		    for (MdnEmailSetting mdnEmail : mdnEmailsServer) {
	  			Element nodeEmail = new Element(MdnEmailSetting.ENT_EMAIL);
		        nodeEmail.setAttribute(MdnEmailSetting.FLD_ID, mdnEmail.getId()+"");
		        nodeEmail.setAttribute(MdnEmailSetting.FLD_EMAIL_ADDRESS, mdnEmail.getEmailAddress());
		        nodeEmail.setAttribute(MdnEmailSetting.FLD_IMAP_HOST, mdnEmail.getImapHost());
		        nodeEmail.setAttribute(MdnEmailSetting.FLD_IMAP_USER_NAME, mdnEmail.getImapUserName());		        
		        nodeEmail.setAttribute(MdnEmailSetting.FLD_IMAP_PASSWORD, mdnEmail.getImapPassword());
		        nodeEmail.setAttribute(MdnEmailSetting.FLD_IMAP_PORT, mdnEmail.getImapPort());
		        nodeEmail.setAttribute(MdnEmailSetting.FLD_IMAP_ENCRYPTED_TYPE, mdnEmail.getImapEncryptedType()+"");
		        nodeEmail.setAttribute(MdnEmailSetting.FLD_SMTP_HOST, mdnEmail.getSmtpHost());
		        nodeEmail.setAttribute(MdnEmailSetting.FLD_SMTP_USERNAME, mdnEmail.getSmtpUsername());
		        nodeEmail.setAttribute(MdnEmailSetting.FLD_SMTP_PASSWORD, mdnEmail.getSmtpPassword());
		        nodeEmail.setAttribute(MdnEmailSetting.FLD_SMTP_PORT, mdnEmail.getSmtpPort());
		        nodeEmail.setAttribute(MdnEmailSetting.FLD_SMTP_ENCRYPTED_TYPE, mdnEmail.getSmtpEncryptedType()+"");
		        nodeEmail.setAttribute(MdnEmailSetting.FLD_PROJECT_ID, mdnEmail.getProjectId()+"");
		        nodeEmail.setAttribute(MdnEmailSetting.FLD_DEL_STATUS, mdnEmail.getDelStatus()+"");
		        emailNode.addContent(nodeEmail);
		  	}
		    
		    Element imNode = new Element("im");
		    msgNode.addContent(imNode);
		    
		    imConnections = dataAgent.getAllIMConnections();
		    for (IMConnection conn : imConnections) {
		  		if(conn.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT){
			  		Element nodeImConn = new Element(IMConnection.ENT_IM_CONNECTION);
			        nodeImConn.setAttribute(IMConnection.FLD_ID, conn.getId()+"");
			        nodeImConn.setAttribute(IMConnection.FLD_NAME, conn.getName());
			        nodeImConn.setAttribute(IMConnection.FLD_PASSWORD, conn.getPassword());
			        nodeImConn.setAttribute(IMConnection.FLD_TYPE, conn.getType()+"");
			        nodeImConn.setAttribute(IMConnection.FLD_STATUS, conn.getStatus()+"");
			        nodeImConn.setAttribute(IMConnection.FLD_USER_NAME, conn.getUserName());
			        nodeImConn.setAttribute(IMConnection.FLD_STATUS_DATETIME, conn.getStatusDesc()+"");
			        nodeImConn.setAttribute(IMConnection.FLD_PROJECT_ID, conn.getProjectId()+"");
			        nodeImConn.setAttribute(IMConnection.FLD_DEL_STATUS, conn.getDelStatus()+"");		        
			        imNode.addContent(nodeImConn);
		  		}
		  	}

		    		  	
		  	MdnSmsSetting mdnInfo = new MdnSmsSetting();
			mdnInfo = dataAgent.getSmsSetting();		    
			if (mdnInfo != null){
			  	Element smsSett = new Element(MdnSmsSetting.ENT_SMS);
			  	msgNode.addContent(smsSett);
				smsSett.setAttribute(MdnSmsSetting.FLD_SIM_NUMBER, mdnInfo.getNumber());
			  	smsSett.setAttribute(MdnSmsSetting.FLD_COMM, mdnInfo.getComm());
			  	smsSett.setAttribute(MdnSmsSetting.FLD_BAUDRATE, mdnInfo.getBaudrate());
			  	smsSett.setAttribute(MdnSmsSetting.FLD_MODEM_MAN, mdnInfo.getModemManufacturer() == null ? "" : mdnInfo.getModemManufacturer());
			  	smsSett.setAttribute(MdnSmsSetting.FLD_MODEM_MODEL, mdnInfo.getmodemModel() == null ? "" : mdnInfo.getmodemModel());				
			}
			    			
		    
		  	//------------------------------
		  	settingRoot.addContent(msgNode);
		}

	return document;
}  
  
  private Element appendURToExportFile(UserReply ur, IDataAgent dataAgent) throws MdnException{
		Element urNode = new Element(UserReply.ENT_USER_REPLY);
		urNode.setAttribute(UserReply.FLD_ID, String.valueOf(ur.getId()));
		urNode.setAttribute(UserReply.FLD_TYPE, ur.getType());
		urNode.setAttribute(UserReply.FLD_VIEW_TABLE_ID, String.valueOf(ur.getViewOrTableId()));
		urNode.setAttribute(UserReply.FLD_DB_ID, String.valueOf(ur.getDatabaseId()));
		urNode.setAttribute(UserReply.FLD_CRITERIA, ur.getCriteriaString());
		urNode.setAttribute(UserReply.FLD_SORTS, ur.getSortString()==null? "": ur.getSortString());
		urNode.setAttribute(UserReply.FLD_GROUPFIELDID, String.valueOf(ur.getGroupFieldId()));
		urNode.setAttribute(UserReply.FLD_PARENT_ID, String.valueOf(ur.getParentId()));
		urNode.setAttribute(UserReply.FLD_CHILDREN, ur.getChildren()== null? "" : ur.getChildren());
		urNode.setAttribute(UserReply.FLD_MSG_TEXT, ur.getMsgText());
		urNode.setAttribute(UserReply.FLD_TIMEOUT, ur.getTimeout());
		urNode.setAttribute(UserReply.FLD_RESPONSE, ur.getResponse());
		urNode.setAttribute(UserReply.FLD_PROJECT_ID, String.valueOf(ur.getProjectId()));
		urNode.setAttribute(UserReply.FLD_DESCIPTION, ur.getDescription()==null? "" : ur.getDescription());
		urNode.setAttribute(UserReply.FLD_QUERY_PARENT_ID, String.valueOf(ur.getParentId()));
		urNode.setAttribute(UserReply.FLD_DISPLAY_RESULT, String.valueOf(ur.getDisplayResult()));
		urNode.setAttribute(UserReply.FLD_DS_STATUS, String.valueOf(ur.getDatasourceStatus()));
		urNode.setAttribute(UserReply.FLD_WS_ID, String.valueOf(ur.getWebServiceId()));
		urNode.setAttribute(UserReply.FLD_DEL_STATUS, String.valueOf(ur.getDelStatus()));
		
		List<QueryCriteriaDobj> queryCriteriaUR = dataAgent.getQueryCriteriaByQueryID(ur.getId(), "UR");
		for (QueryCriteriaDobj qc : queryCriteriaUR){
			Element qcNode = new Element(QueryCriteriaDobj.ENT_QUERY_CRITERIA);
			qcNode.setAttribute(QueryCriteriaDobj.FLD_ID, String.valueOf(qc.getId()));
			qcNode.setAttribute(QueryCriteriaDobj.FLD_QUERY_ID, String.valueOf(qc.getQueryId()));
			qcNode.setAttribute(QueryCriteriaDobj.FLD_VALUEORCOND, qc.getValueOrCondition());
			qcNode.setAttribute(QueryCriteriaDobj.FLD_ROW_NO, String.valueOf(qc.getRowNo()));
			qcNode.setAttribute(QueryCriteriaDobj.FLD_TYPE, qc.getType());
			qcNode.setAttribute(QueryCriteriaDobj.FLD_USED, String.valueOf(qc.getUsed()));
			qcNode.setAttribute(QueryCriteriaDobj.FLD_INDENT, String.valueOf(qc.getIndent()));
			qcNode.setAttribute(QueryCriteriaDobj.FLD_PARENT, qc.getParent() == null? "" : qc.getParent());
			qcNode.setAttribute(QueryCriteriaDobj.FLD_NUMBER, String.valueOf(qc.getNumber()));
			qcNode.setAttribute(QueryCriteriaDobj.FLD_NAME, qc.getName());
			qcNode.setAttribute(QueryCriteriaDobj.FLD_COMP_ID, String.valueOf(qc.getCompId()));
			qcNode.setAttribute(QueryCriteriaDobj.FLD_COMPARISON, qc.getComparison());
			qcNode.setAttribute(QueryCriteriaDobj.FLD_VALUE, qc.getValue());
			qcNode.setAttribute(QueryCriteriaDobj.FLD_CONNECTION, qc.getConnection());
			qcNode.setAttribute(QueryCriteriaDobj.FLD_VALUE2, qc.getValue2());
			qcNode.setAttribute(QueryCriteriaDobj.FLD_GROUPING, qc.getGrouping());
			qcNode.setAttribute(QueryCriteriaDobj.FLD_VALUE_USERINPUT_SEQ, qc.getValueUserInputSeq());
			qcNode.setAttribute(QueryCriteriaDobj.FLD_VALUE2_USERINPUT_SEQ, qc.getValue2UserInputSeq());
			qcNode.setAttribute(QueryCriteriaDobj.FLD_OBJECT_TYPE, qc.getObjectType());
			
			urNode.addContent(qcNode);
		}	  
		return urNode;
  }
  
  public Element getExportStructure(String languageFile, int projectId){
	    // Get the GUI definition file.
	    Element guiDef = this.getXMLLanguageFile(languageFile);
	    // Get the language elements we are interested in.
	    String dataSources = getGuiValueForElement(guiDef, "tree-data-sources");
	    String databases = getGuiValueForElement(guiDef, "tree-databases");	    
	    String ws = getGuiValueForElement(guiDef, "tree-ws");
	    String strEndUserAccess = getGuiValueForElement(guiDef, "tree-end-user-access");
	    String strMessagingAccess = getGuiValueForElement(guiDef, "tree-messaging-access");
	    String strMessagingAlerts = getGuiValueForElement(guiDef, "tree-messaging-alerts");
	    String strUsersGroups = getGuiValueForElement(guiDef, "tree-users-groups");
	    String strUsers = getGuiValueForElement(guiDef, "tree-users");
	    String strGroups = getGuiValueForElement(guiDef, "tree-groups");
	    String strPermissions = getGuiValueForElement(guiDef, "tree-permissions");
	    String strDeployment = getGuiValueForElement(guiDef, "tree-deployment");
	    String strTemplates = getGuiValueForElement(guiDef, "tree-templates");
	    String strSettings = getGuiValueForElement(guiDef, "tree-general-settings");
	    String strTrial = getGuiValueForElement(guiDef, "tree-30-day-trial");
	    String strEnc = getGuiValueForElement(guiDef, "tree-Encrypted");
	    
	    // Define the XML structure.
	    Element root = new Element(ROOT_ROOT);
	    
	    /*Element dsNode = new Element(ELEMENT_NODE);
	    dsNode.setAttribute(ATTRIBUTE_NAME, dataSources);
	    dsNode.setAttribute(ATTRIBUTE_TYPE, "datasources");
	    dsNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD);
	    dsNode.setAttribute("__OPTTREE_META_open", "true");
	    dsNode.setAttribute(ATTRIBUTE_ICON_NAME, "database");
	    
	    Element dbsNode = new Element(ELEMENT_NODE);
	    dbsNode.setAttribute(ATTRIBUTE_NAME, databases);
	    dbsNode.setAttribute(ATTRIBUTE_TYPE, TYPE_DATABASES);
	    dbsNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD);
	    dbsNode.setAttribute("__OPTTREE_META_open", "false");
	    dbsNode.setAttribute(ATTRIBUTE_ICON_NAME, "database");
	    

	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	    List<DataSourceDobj> dbs = null;
		try {
			dbs = dataAgent.getAllDbConnections(projectId);
		} catch (MdnException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    for (DataSourceDobj db: dbs) {
	      
	      int connID = db.getId();
	    	
	      Element dbNode = new Element(ELEMENT_NODE);
	      dbNode.setAttribute(ATTRIBUTE_NAME, db.getName());
	      dbNode.setAttribute(ATTRIBUTE_ID, String.valueOf(connID));
	      dbNode.setAttribute(ATTRIBUTE_TYPE, TYPE_DATABASE);
	      dbNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD);
	      dbNode.setAttribute("__OPTTREE_META_open", "false");
	      dbNode.setAttribute(ATTRIBUTE_ICON_NAME, "database");
	          
	      dbsNode.addContent(dbNode);
	    }
	    dsNode.addContent(dbsNode);	    
	    
	    
	    Element wsNode = new Element(ELEMENT_NODE);
	    wsNode.setAttribute(ATTRIBUTE_NAME, ws);
	    wsNode.setAttribute(ATTRIBUTE_TYPE, TYPE_WEB_SERVICES);
	    wsNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD);
	    wsNode.setAttribute("__OPTTREE_META_open", "false");
	    wsNode.setAttribute(ATTRIBUTE_ICON_NAME, "db_webservices");
	        
	    
	    List<QueryDobj> queries;
		try {
			queries = dataAgent.getQueriesByType("webservice");
		    for (QueryDobj eachQuery : queries){
		        //Add 'query' node to view node
		        Element queryNode = getNavNodeWithValue(eachQuery.getName(), TYPE_WEB_SERVICE_QUERY, FONTSTYLE_PLAIN, "query", String.valueOf(eachQuery.getId()));
		        queryNode.setAttribute(ATTRIBUTE_DRAGGABLE, "true");
		        wsNode.addContent(queryNode);            	  
		    }	     
		} catch (MdnException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dsNode.addContent(wsNode);
		root.addContent(dsNode); 
*/

	    /*Element endUserNode = new Element(ELEMENT_NODE);
	    endUserNode.setAttribute(ATTRIBUTE_NAME, strEndUserAccess);
	    endUserNode.setAttribute(ATTRIBUTE_TYPE, "endUserAccess");
	    endUserNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD);
	    endUserNode.setAttribute("__OPTTREE_META_open", "true");
	    endUserNode.setAttribute(ATTRIBUTE_ICON_NAME, "email");		
		root.addContent(endUserNode);
		
	    Element msgAccessNode = new Element(ELEMENT_NODE);
	    msgAccessNode.setAttribute(ATTRIBUTE_NAME, strMessagingAccess);
	    msgAccessNode.setAttribute(ATTRIBUTE_TYPE, "messagingAccess");
	    msgAccessNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD);
	    msgAccessNode.setAttribute("__OPTTREE_META_open", "false");
	    msgAccessNode.setAttribute(ATTRIBUTE_ICON_NAME, "email");
	    endUserNode.addContent(msgAccessNode);
	    
	    Element msgAlertNode = new Element(ELEMENT_NODE);
	    msgAlertNode.setAttribute(ATTRIBUTE_NAME, strMessagingAlerts);
	    msgAlertNode.setAttribute(ATTRIBUTE_TYPE, "messagingAlerts");
	    msgAlertNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD);
	    msgAlertNode.setAttribute("__OPTTREE_META_open", "false");
	    msgAlertNode.setAttribute(ATTRIBUTE_ICON_NAME, "email");
	    endUserNode.addContent(msgAlertNode);
	    */
	    Element usersGroupsNode = new Element(ELEMENT_NODE);
	    usersGroupsNode.setAttribute(ATTRIBUTE_NAME, strUsersGroups);
	    usersGroupsNode.setAttribute(ATTRIBUTE_TYPE, "usersGroups");
	    usersGroupsNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD);
	    usersGroupsNode.setAttribute("__OPTTREE_META_open", "true");	
		root.addContent(usersGroupsNode);
		
	    /*Element usersNode = new Element(ELEMENT_NODE);
	    usersNode.setAttribute(ATTRIBUTE_NAME, strUsers);
	    usersNode.setAttribute(ATTRIBUTE_TYPE, "users");
	    usersNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD);
	    usersNode.setAttribute("__OPTTREE_META_open", "false");	
	    usersGroupsNode.addContent(usersNode);		

	    Element groupsNode = new Element(ELEMENT_NODE);
	    groupsNode.setAttribute(ATTRIBUTE_NAME, strGroups);
	    groupsNode.setAttribute(ATTRIBUTE_TYPE, "groups");
	    groupsNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD);
	    groupsNode.setAttribute("__OPTTREE_META_open", "false");	
	    usersGroupsNode.addContent(groupsNode);

	    Element permissionsNode = new Element(ELEMENT_NODE);
	    permissionsNode.setAttribute(ATTRIBUTE_NAME, strPermissions);
	    permissionsNode.setAttribute(ATTRIBUTE_TYPE, "permissions");
	    permissionsNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD);
	    permissionsNode.setAttribute("__OPTTREE_META_open", "false");	
	    usersGroupsNode.addContent(permissionsNode);
	    
	    Element deploymentNode = new Element(ELEMENT_NODE);
	    deploymentNode.setAttribute(ATTRIBUTE_NAME, strDeployment);
	    deploymentNode.setAttribute(ATTRIBUTE_TYPE, "deployment");
	    deploymentNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD);
	    deploymentNode.setAttribute("__OPTTREE_META_open", "true");	
	    root.addContent(deploymentNode);
	    
	    Element templatesNode = new Element(ELEMENT_NODE);
	    templatesNode.setAttribute(ATTRIBUTE_NAME, strTemplates);
	    templatesNode.setAttribute(ATTRIBUTE_TYPE, "templates");
	    templatesNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD);
	    templatesNode.setAttribute("__OPTTREE_META_open", "false");	
	    deploymentNode.addContent(templatesNode);	    
*/
	    Element settingsNode = new Element(ELEMENT_NODE);
	    settingsNode.setAttribute(ATTRIBUTE_NAME, strSettings);
	    settingsNode.setAttribute(ATTRIBUTE_TYPE, "settings");
	    settingsNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD);
	    settingsNode.setAttribute("__OPTTREE_META_open", "true");	
	    root.addContent(settingsNode);	    
/*
	    Element trialNode = new Element(ELEMENT_NODE);
	    trialNode.setAttribute(ATTRIBUTE_NAME, strTrial);
	    trialNode.setAttribute(ATTRIBUTE_TYPE, "trial");
	    trialNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD);
	    trialNode.setAttribute("__OPTTREE_META_open", "true");	
	    root.addContent(trialNode);	    

	    Element encryptedNode = new Element(ELEMENT_NODE);
	    encryptedNode.setAttribute(ATTRIBUTE_NAME, strEnc);
	    encryptedNode.setAttribute(ATTRIBUTE_TYPE, "encrypted");
	    encryptedNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD);
	    encryptedNode.setAttribute("__OPTTREE_META_open", "true");	
	    root.addContent(encryptedNode);	    
*/	    
	    return root;
	  }  
  
  private Element getNavNode(String attrName, String attrType, 
      String fontStyle){
    Element node = new Element(ELEMENT_NODE);
    node.setAttribute(ATTRIBUTE_NAME, attrName);
    node.setAttribute(ATTRIBUTE_TYPE, attrType);
    node.setAttribute(ATTRIBUTE_FONTSTYLE, fontStyle);
    return node;
  }
  
  private Element getNavNodeWithIcon(String attrName, String attrType, 
      String fontStyle, String iconName){
    Element node = new Element(ELEMENT_NODE);
    node.setAttribute(ATTRIBUTE_NAME, attrName);
    node.setAttribute(ATTRIBUTE_TYPE, attrType);
    node.setAttribute(ATTRIBUTE_FONTSTYLE, fontStyle);
    node.setAttribute(ATTRIBUTE_ICON_NAME, iconName);
    return node;
  }

  private Element getNavNodeWithValue(String attrName, String attrType, 
	      String fontStyle, String iconName, String atrrValue){
    Element node = getNavNodeWithIcon(attrName, attrType, fontStyle, iconName);
    node.setAttribute(ATTRIBUTE_VALUE, atrrValue);
    return node;
  }  

  private Element getNavNodeWithExtraValue(String attrName, String attrType, 
	      String fontStyle, String iconName, String atrrValue, String url, String operation){
    Element node = getNavNodeWithValue(attrName, attrType, fontStyle, iconName, atrrValue);
    node.setAttribute("url", url);
    node.setAttribute("operation", operation);
    return node;
  }  
  
  private Element getNavNode(String attrName, String attrType, 
      String fontStyle, boolean isOpen){
    Element node = new Element(ELEMENT_NODE);
    node.setAttribute(ATTRIBUTE_NAME, attrName);
    node.setAttribute(ATTRIBUTE_TYPE, attrType);
    node.setAttribute(ATTRIBUTE_FONTSTYLE, fontStyle);
    if (isOpen){
      node.setAttribute(ATTRIBUTE_OPTTREE_OPEN, "true");
    }
    return node;
  }
  
  private Element getNavNodeWithIcon(String attrName, String attrType, 
      String fontStyle, boolean isOpen, String iconName){
    Element node = new Element(ELEMENT_NODE);
    node.setAttribute(ATTRIBUTE_NAME, attrName);
    node.setAttribute(ATTRIBUTE_TYPE, attrType);
    node.setAttribute(ATTRIBUTE_FONTSTYLE, fontStyle);
    if (isOpen){
      node.setAttribute(ATTRIBUTE_OPTTREE_OPEN, "true");
    }
    node.setAttribute(ATTRIBUTE_ICON_NAME, iconName);
    return node;
  }
  
  private String getGuiValueForElement(Element node, String elementName){
    String value = "";
    if (elementName == null )
    	return value;
    
    Element conn = null;
    if(node != null)
    	conn = node.getChild(elementName);
    //Element conn = node.getChild(elementName);
    
    if (conn != null){
      value = conn.getAttributeValue("label");
    }
    return value;
  }
  
  /**
   * Retrieves the XML Language file.
   * @param file
   * @return
   */
  private Element getXMLLanguageFile(String file) {
    Element root = null;
    SAXBuilder builder = new SAXBuilder();
    try {
    	if(file != null){
    		Document doc = builder.build(Constants.LANGUAGE_FILE_URL + file);
    		root = doc.getRootElement();
    	}
    } catch (JDOMException e) {
        logger.error("Exception parsing GUI XML document");
        e.printStackTrace();
    } catch (IOException ioe){
	      logger.error("IOException parsing GUI XML document");
	      ioe.printStackTrace();
    }
    return root;
  }

  
  public Element getCSS(){
    Element root = new Element(ROOT_CSSS);
    addElementWithAttribute(root, ELEMENT_CSS, ATTRIBUTE_NAME, "Funky Gold");
    addElementWithAttribute(root, ELEMENT_CSS, ATTRIBUTE_NAME, "Smooth Yellow");
    addElementWithAttribute(root, ELEMENT_CSS, ATTRIBUTE_NAME, "Cool Blue");
    addElementWithAttribute(root, ELEMENT_CSS, ATTRIBUTE_NAME, "Corporate");
    addElementWithAttribute(root, ELEMENT_CSS, ATTRIBUTE_NAME, "Casual");
    return root;
  }
  
  public Element getScreenDefs(){
    Element root = new Element(ROOT_SCREENS);
    
    Element screen1 = new Element(ELEMENT_SCREEN);
    screen1.setAttribute(ATTRIBUTE_DISPLAY_NAME, "PDA 320 x 160");
    screen1.setAttribute(ATTRIBUTE_NAME, "PDA");
    screen1.setAttribute(ATTRIBUTE_WIDTH, "320");
    screen1.setAttribute(ATTRIBUTE_HEIGHT, "160");
    root.addContent(screen1);
    
    Element screen2 = new Element(ELEMENT_SCREEN);
    screen2.setAttribute(ATTRIBUTE_DISPLAY_NAME, "Wayne's Mobile 160 x 120");
    screen2.setAttribute(ATTRIBUTE_NAME, "Wayne's Mobile");
    screen2.setAttribute(ATTRIBUTE_WIDTH, "160");
    screen2.setAttribute(ATTRIBUTE_HEIGHT, "120");
    root.addContent(screen2);
    
    return root;
  }
  
  @SuppressWarnings("unchecked")
public Element getTables(DataSourceDobj connection, List<EntityDobj> tables){
  	Element root = new Element(ROOT_ROOT);
  	
  	Element connNode = new Element(ELEMENT_NODE);
  	connNode.setAttribute(ATTRIBUTE_ID, String.valueOf(connection.getId()));
  	connNode.setAttribute(ATTRIBUTE_NAME, connection.getName());
  	connNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD);
  	connNode.setAttribute("__OPTTREE_META_open", "true");
  	connNode.setAttribute(ATTRIBUTE_ICON_NAME, "tables");
  	
  	for (EntityDobj table : tables) {
			Element tableNode = new Element(ELEMENT_NODE);
			tableNode.setAttribute(ATTRIBUTE_ID, String.valueOf(table.getId()));
			tableNode.setAttribute(ATTRIBUTE_NAME, table.getName());
			tableNode.setAttribute(ATTRIBUTE_TYPE, "table");
			tableNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD);
			tableNode.setAttribute("__OPTTREE_META_open", "false");
			tableNode.setAttribute(ATTRIBUTE_ICON_NAME, "table");
			List<FieldDobj> list = (List<FieldDobj>)(table.getFields());
			for (FieldDobj field : list) {
				Element fieldNode = new Element(ELEMENT_NODE);
				fieldNode.setAttribute(ATTRIBUTE_ID, String.valueOf(field.getId()));
				fieldNode.setAttribute(ATTRIBUTE_NAME, field.getName());
				fieldNode.setAttribute(ATTRIBUTE_TYPE, "field");
				fieldNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_PLAIN);
				fieldNode.setAttribute(ATTRIBUTE_ICON_NAME, "field");
				tableNode.addContent(fieldNode);
			}
			
			connNode.addContent(tableNode);
		}
  	
  	root.addContent(connNode);
  	return root;
  }

  public Element getJoins(DataSourceDobj connection, List<JoinDobj> joins){
	  	Element root = new Element(ROOT_ROOT);
	  	
	  	Element connNode = new Element(ELEMENT_NODE);
	  	connNode.setAttribute(ATTRIBUTE_ID, String.valueOf(connection.getId()));
	  	connNode.setAttribute(ATTRIBUTE_NAME, connection.getName());
	  	connNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD);
	  	connNode.setAttribute("__OPTTREE_META_open", "true");
	  	connNode.setAttribute(ATTRIBUTE_ICON_NAME, "database");
	  	
	  	String wholeDescription = "";
	  	for (JoinDobj join : joins) {
				
  			wholeDescription += join.toString() + "\n";
		}
		Element joinNode = new Element(ELEMENT_NODE);
		
		//joinNode.setAttribute(ATTRIBUTE_ID, String.valueOf(join.getId()));
		joinNode.setAttribute("description", wholeDescription);
		joinNode.setAttribute(ATTRIBUTE_TYPE, "join");
		joinNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD_ITALIC);
		joinNode.setAttribute("__OPTTREE_META_open", "false");
		joinNode.setAttribute(ATTRIBUTE_ICON_NAME, "join");
		
		connNode.addContent(joinNode);	  
		
	  	root.addContent(connNode);
	  	return root;
	  }
  
  /**
   * create tree with all the tables, but some fields are ticked/checked for specified view
   * @param connection
   * @param tables
   * @param view
   * @return
   */
  @SuppressWarnings("unchecked")
public Element getTablesView(DataSourceDobj connection, List<EntityDobj> tables, DataView view){
  	Element root = new Element(ROOT_ROOT);
  	
  	Element connNode = new Element(ELEMENT_NODE);
  	connNode.setAttribute(ATTRIBUTE_ID, String.valueOf(connection.getId()));
  	connNode.setAttribute(ATTRIBUTE_NAME, connection.getName());
  	connNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD);
  	connNode.setAttribute("__OPTTREE_META_open", "true");
  	connNode.setAttribute(ATTRIBUTE_ICON_NAME, "database");
  	
  	for (EntityDobj table : tables) {
			Element tableNode = new Element(ELEMENT_NODE);
			tableNode.setAttribute(ATTRIBUTE_ID, String.valueOf(table.getId()));
			tableNode.setAttribute(ATTRIBUTE_NAME, table.getName());
			tableNode.setAttribute(ATTRIBUTE_TYPE, "table");
			tableNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD);
			tableNode.setAttribute("__OPTTREE_META_open", "false");
			tableNode.setAttribute(ATTRIBUTE_ICON_NAME, "table");
			
			//Check if this table is part of view
			boolean useCurrTable= false;
			Vector fields = null;
			List<DataViewField> allViewFields = null;
			if (view != null)
			{
				fields = view.getFields();
				allViewFields = fields;
				
				for (DataViewField viewField: allViewFields){
					String viewSourceTable = viewField.getSourceEntity();
					if (viewSourceTable.equalsIgnoreCase(table.getName()))
					{
						useCurrTable = true;
					}
				}
				//Set to be checked and open to be true when it is part of view
				if (useCurrTable){				
					tableNode.setAttribute("__OPTTREE_META_open", "true");
					tableNode.setAttribute("_checked", "true");
				}
			}	
			
			for (FieldDobj field : (List<FieldDobj>)(table.getFields())) {
				Element fieldNode = new Element(ELEMENT_NODE);
				fieldNode.setAttribute(ATTRIBUTE_ID, String.valueOf(field.getId()));
				fieldNode.setAttribute(ATTRIBUTE_NAME, field.getName());
				
				fieldNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_PLAIN);
				fieldNode.setAttribute(ATTRIBUTE_ICON_NAME, "field");				
				fieldNode.setAttribute("tablefield", table.getName() +  "." +field.getName());
				
				//Check if this field is part of view
				boolean useCurrField= false;
				DataViewField relatedViewField = null;
				
				if (view != null)
				{
					allViewFields = fields;
					for (DataViewField viewField: allViewFields){
						String viewSourceTable = viewField.getSourceEntity();
						String viewSourceField = viewField.getSourceField();
						if (viewSourceTable.equalsIgnoreCase(table.getName()) && viewSourceField.equalsIgnoreCase(field.getName()))
						{
							useCurrField = true;
							relatedViewField = viewField;
						}
					}
					//Set to be checked when it is part of view
					if (useCurrField){
						fieldNode.setAttribute("_checked", "true");
						//Overwrite the table field ID
						fieldNode.setAttribute("viewfieldid", String.valueOf(relatedViewField.getId()));
						fieldNode.setAttribute(ATTRIBUTE_TYPE, "viewfield");
						fieldNode.setAttribute("view-field-description", relatedViewField.getDescription() != null ? relatedViewField.getDescription() : "");
						fieldNode.setAttribute("display-name", relatedViewField.getDisplayName());
					}
					else{
						//fieldNode.setAttribute(ATTRIBUTE_ID, "");
						fieldNode.setAttribute(ATTRIBUTE_TYPE, "tablefield");
						fieldNode.setAttribute("view-field-description", "");
						fieldNode.setAttribute("display-name", "");
					}
				}
				else
				{
					//fieldNode.setAttribute(ATTRIBUTE_ID, "");
					fieldNode.setAttribute(ATTRIBUTE_TYPE, "tablefield");
					fieldNode.setAttribute("view-field-description", "");
					fieldNode.setAttribute("display-name", "");					
				}
				tableNode.addContent(fieldNode);
			}
			
			connNode.addContent(tableNode);
		}
  	
  	root.addContent(connNode);
  	return root;
  }
  /**
   * create tree with all the tables, but some fields are ticked/checked for specified view
   * @param connection
   * @param tables
   * @param view
   * @return
   */
  @SuppressWarnings("unchecked")
  public Element groupDataViewXML(int projectId, Group group, List<GroupDataView> groupDataViews, String result, String action){
  	Element root = new Element(ROOT_ROOT);

    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
    List<DataSourceDobj> dbs = null;
	try {
		dbs = dataAgent.getAllDbConnections(projectId);	    
	} catch (MdnException e) {
		e.printStackTrace();
	}   	
  	
	for (DataSourceDobj db: dbs) {//TBL_DATASOURCE
		Element connNode = new Element(ELEMENT_NODE);
		int dbId = db.getId();
		if(db.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT){
			List<DataView> views = null;
			try {
				views = dataAgent.getAllMetaViews(dbId, true);//TBL_DATAVIEW
			} catch (MdnException e) {
				e.printStackTrace();
			}
	
			if(views!= null && views.size() > 0) {
				connNode.setAttribute(ATTRIBUTE_ID, String.valueOf(dbId));
				connNode.setAttribute(ATTRIBUTE_NAME, db.getName());
				connNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD);
				connNode.setAttribute("__OPTTREE_META_open", "true");
				connNode.setAttribute(ATTRIBUTE_ICON_NAME, "database");
		  	
				for (DataView view : views) {
					int checkedFieldSize = 0;
					if(view.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT){
						Element viewNode = new Element(ELEMENT_NODE);
						int viewId = view.getId();
						viewNode.setAttribute(ATTRIBUTE_ID, String.valueOf(viewId));
						viewNode.setAttribute(ATTRIBUTE_NAME, view.getName());
						viewNode.setAttribute(ATTRIBUTE_TYPE, "view");
						viewNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD_ITALIC);
						viewNode.setAttribute("__OPTTREE_META_open", "false");
						viewNode.setAttribute(ATTRIBUTE_ICON_NAME, "view");
					
						for (DataViewField field : (List<DataViewField>)(view.getFields())) {//TBL_DVFIELD
							Element fieldNode = new Element(ELEMENT_NODE);
							int fieldId = field.getId();
							fieldNode.setAttribute(ATTRIBUTE_ID, String.valueOf(fieldId));
							fieldNode.setAttribute(ATTRIBUTE_NAME, field.getName());
							fieldNode.setAttribute(ATTRIBUTE_TYPE, "field");
							fieldNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_PLAIN);
							fieldNode.setAttribute(ATTRIBUTE_ICON_NAME, "field");				
							fieldNode.setAttribute("viewfield", view.getName() +  "." +field.getName());
							
							//Check if this view is part of GroupView
							boolean permitCurrView= false;
							for (GroupDataView groupDataView: groupDataViews){//TBL_GROUPDATAVIEW
								if (fieldId == groupDataView.getDataViewId()) {
									checkedFieldSize++;
									permitCurrView = true;
								}
							}
							//Set to be checked and open to be true when it is part of view
							if (permitCurrView){				
								fieldNode.setAttribute("_checked", "true");
								viewNode.setAttribute("__OPTTREE_META_open", "true");
							}
							if(checkedFieldSize == view.getFields().size()){
								viewNode.setAttribute("_checked", "true");
							}
							viewNode.addContent(fieldNode);
						}
						connNode.addContent(viewNode);
					}
				}
				root.addContent(connNode);
			}
		}
	}
  	root.setAttribute("result", result);
  	
  	if(action != null)
  		root.setAttribute("action", action);
  	
  	if(group != null)
  	{
  		root.setAttribute("group-id", String.valueOf(group.getId()));
  		root.setAttribute("group-name", group.getName());
  		root.setAttribute("projectId", String.valueOf(projectId));
  		root.setAttribute("guest", booleanConvertor(group.getGuest()));
  	}
  	return root;
  }  
  
  public Element groupTableXML(int projectId, Group group, List<GroupTablePermission> groupTablePermissions){
	  	Element root = new Element(ROOT_ROOT);
	  	
	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	    List<DataSourceDobj> dbs = null;
		try {
			dbs = dataAgent.getAllDbConnections(projectId);	    
		} catch (MdnException e) {
			e.printStackTrace();
		}   	
	  	
		for (DataSourceDobj db: dbs) {
			Element connNodeTbl = new Element("node2");
			int dbId = db.getId();
			if(db.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT){
				List<EntityDobj> tables = null;
				try {
					tables = dataAgent.getAllMetaTables(dbId, false);
				} catch (MdnException e) {
					e.printStackTrace();
				}
				
				/* ----------------------- TBL Tree ----------------------- */
				if(tables!= null && tables.size() > 0) {
					int checkedSize = 0;
					
					connNodeTbl.setAttribute(ATTRIBUTE_ID, String.valueOf(dbId));
					connNodeTbl.setAttribute(ATTRIBUTE_NAME, db.getName());
					connNodeTbl.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD);
					connNodeTbl.setAttribute("__OPTTREE_META_open", "true");
					connNodeTbl.setAttribute(ATTRIBUTE_ICON_NAME, "database");
			  	
					for (EntityDobj table : tables) {
						Element tblNode = new Element("node2");
						int tblId = table.getId();
						tblNode.setAttribute(ATTRIBUTE_ID, String.valueOf(tblId));
						tblNode.setAttribute(ATTRIBUTE_NAME, table.getName());
						tblNode.setAttribute(ATTRIBUTE_TYPE, "table");
						//tblNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD_ITALIC);
						tblNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_PLAIN);
						
						//_________________________________________________________________
						//Check if this table is part of groupTablePermissions
						for (GroupTablePermission groupTablePermission: groupTablePermissions){//TBL_GROUP_ENTITY_PERMISSION
							if (tblId == groupTablePermission.getEntityId()) {
								checkedSize++;
								
								tblNode.setAttribute("_checked", "true");
								tblNode.setAttribute("__OPTTREE_META_open", "true");								
							}
						}
//						if (permitCurrTable){				
//							fieldNode.setAttribute("_checked", "true");
//							viewNode.setAttribute("__OPTTREE_META_open", "true");
//						}
						//_________________________________________________________________
						
						if(checkedSize == tables.size()){
							connNodeTbl.setAttribute("_checked", "true");
						}						
						
						connNodeTbl.addContent(tblNode);
					}
					root.addContent(connNodeTbl);
				}
			}
		}
	  	return root;
	  }    
  /**
   * Format the view XML set
   * @param connection
   * @param views
   * @return
   */
  @SuppressWarnings("unchecked")
public Element getViews(DataSourceDobj connection, List<DataView> views){
  	Element root = new Element(ROOT_ROOT);
  	
  	Element connNode = new Element("conn");
  	connNode.setAttribute(ATTRIBUTE_ID, String.valueOf(connection.getId()));
  	connNode.setAttribute(ATTRIBUTE_NAME, connection.getName());
  	connNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD);
  	connNode.setAttribute("__OPTTREE_META_open", "true");
  	connNode.setAttribute(ATTRIBUTE_ICON_NAME, "database");
  	
  	//add empty field
	Element emptyViewNode = new Element("view");
	emptyViewNode.setAttribute(ATTRIBUTE_ID, "");
	emptyViewNode.setAttribute(ATTRIBUTE_NAME, "");
	emptyViewNode.setAttribute(ATTRIBUTE_TYPE, "view");
	emptyViewNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD_ITALIC);
	emptyViewNode.setAttribute("__OPTTREE_META_open", "false");
	emptyViewNode.setAttribute(ATTRIBUTE_ICON_NAME, "view");
  	connNode.addContent(emptyViewNode);
  	
  	for (DataView view : views) {
			Element viewNode = new Element("view");
			viewNode.setAttribute(ATTRIBUTE_ID, String.valueOf(view.getId()));
			viewNode.setAttribute(ATTRIBUTE_NAME, view.getName());
			viewNode.setAttribute(ATTRIBUTE_TYPE, "view");
			viewNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_BOLD_ITALIC);
			viewNode.setAttribute("__OPTTREE_META_open", "false");
			viewNode.setAttribute(ATTRIBUTE_ICON_NAME, "view");
			for (DataViewField field : (List<DataViewField>)(view.getFields())) {
				Element fieldNode = new Element("field");
				fieldNode.setAttribute(ATTRIBUTE_ID, String.valueOf(field.getId()));
				fieldNode.setAttribute(ATTRIBUTE_NAME, field.getDisplayName());
				fieldNode.setAttribute(ATTRIBUTE_TYPE, "field");
				fieldNode.setAttribute(ATTRIBUTE_FONTSTYLE, FONTSTYLE_PLAIN);
				fieldNode.setAttribute(ATTRIBUTE_ICON_NAME, "field");
				viewNode.addContent(fieldNode);
			}
			
			connNode.addContent(viewNode);
		}
  	
  	root.addContent(connNode);
  	return root;
  }  
  
  public Element getTable(EntityDobj table){
  	Element root = new Element(ROOT_ROOT);
  	
  	Element tableNode = new Element("table");
  	tableNode.setAttribute("tableID", String.valueOf(table.getId()));
  	tableNode.setAttribute("connID", String.valueOf(table.getDataSourceId()));
  	tableNode.setAttribute(ATTRIBUTE_NAME, table.getName());
  	tableNode.setAttribute("flags", String.valueOf(table.getFlags()));
  	tableNode.setAttribute("description", table.getDescription() != null ? table.getDescription() : "");
  	tableNode.setAttribute(ATTRIBUTE_TYPE, "table");//TODO ???TABLE_CAT
  	if (table.getFields() != null){
	  	for (FieldDobj field : (Vector<FieldDobj>)(table.getFields())) {
	
		  	    // type
		  	    int type = field.getType();
		  	    String strType = null;
		  	    if(type == Field.FT_STRING)
		  	    	strType = TYPE_STRING;
		  	    else if(type == Field.FT_INTEGER)
		  	    	strType = TYPE_INTEGER;
		  	    else if(type == Field.FT_DECIMAL)
		  	        strType = TYPE_DECIMAL;
		  	    else if(type == Field.FT_DATETIME)
		  	    	strType = TYPE_DATE_TIME;
		  	    else if(type == Field.FT_CURRENCY)
		  	    	strType = TYPE_CURRENCY;
		  	    else if(type == Field.FT_BOOLEAN)
		  	    	strType = TYPE_BOOLEAN; 
		  	    else
		  	    	strType = TYPE_UNKNOWN;
		  	    
		  	    
		  	    // flags
		  	    int flags = field.getFlags();
		  	    boolean readOnly = false;
		  	    if(flags == Field.FF_READ_ONLY)
		  	    	readOnly = true;
		  	    boolean uniqueKey = false;
		  	    if(flags == Field.FF_UNIQUE_KEY)
		  	    	uniqueKey = true;
	  	    
				Element fieldNode = new Element("field");
				fieldNode.setAttribute(ATTRIBUTE_ID, String.valueOf(field.getId()));
				fieldNode.setAttribute("tableID", String.valueOf(field.getEntityId()));
				fieldNode.setAttribute(ATTRIBUTE_NAME, field.getName());
				fieldNode.setAttribute("description", field.getDescription() != null ? field.getDescription() : "");
				fieldNode.setAttribute("uniqueKey", uniqueKey? "true" : "false" );
				fieldNode.setAttribute("readOnly", readOnly? "true" : "false");
				fieldNode.setAttribute("type", strType);
				tableNode.addContent(fieldNode);
			}
  	}
  	root.addContent(tableNode);
  	return root;
  }

  @SuppressWarnings("unchecked")
  public Element getView(DataView view){
	  	Element root = new Element(ROOT_ROOT);
	  	
	  	Element viewNode = new Element("view");
	  	viewNode.setAttribute("viewID", String.valueOf(view.getId()));
	  	viewNode.setAttribute("connID", String.valueOf(view.getSourceDsId()));
	  	viewNode.setAttribute(ATTRIBUTE_NAME, view.getName());
	  	viewNode.setAttribute("description", view.getDescription() == null ? "" : view.getDescription());
	  	viewNode.setAttribute(ATTRIBUTE_TYPE, "view");
	  	viewNode.setAttribute("sourceTables", view.getSourceTableNames());
	  	
	  	if (view.getFields() != null){
		  	for (DataViewField field : (List<DataViewField>)(view.getFields())) {
					Element fieldNode = new Element("field");
					fieldNode.setAttribute(ATTRIBUTE_ID, String.valueOf(field.getId()));
					fieldNode.setAttribute("viewID", String.valueOf(field.getDataViewId()));
					fieldNode.setAttribute(ATTRIBUTE_NAME, field.getName());
					fieldNode.setAttribute("description", field.getDescription() == null ? "" : field.getDescription());
					viewNode.addContent(fieldNode);
				}
	  	}
	  	root.addContent(viewNode);
	  	return root;
  }  
  
  public Element getFieldResult(FieldDobj field, EntityDobj table){
    // type
    int type = field.getType();
    String strType = null;
    if(type == Field.FT_STRING)
    	strType = TYPE_STRING;
    else if(type == Field.FT_INTEGER)
    	strType = TYPE_INTEGER;
    else if(type == Field.FT_DECIMAL)
        strType = TYPE_DECIMAL;
    else if(type == Field.FT_DATETIME)
    	strType = TYPE_DATE_TIME;
    else if(type == Field.FT_CURRENCY)
    	strType = TYPE_CURRENCY;
    else if(type == Field.FT_BOOLEAN)
    	strType = TYPE_BOOLEAN; 
    else
    	strType = TYPE_UNKNOWN;
    
    
    // flags
    int flags = field.getFlags();
    boolean readOnly = false;
    if(flags == Field.FF_READ_ONLY)
    	readOnly = true;
    boolean uniqueKey = false;
    if(flags == Field.FF_UNIQUE_KEY)
    	uniqueKey = true;
    
	Element root = new Element(ROOT_ROOT);
  	
  	Element tableNode = new Element("table");
  	tableNode.setAttribute(ATTRIBUTE_ID, String.valueOf(table.getId()));
  	tableNode.setAttribute(ATTRIBUTE_NAME, table.getName());
  	tableNode.setAttribute("description", table.getDescription() != null ? table.getDescription() : "");
  	tableNode.setAttribute(ATTRIBUTE_TYPE, "");//TODO ???TABLE_CAT
  	
  	Element fieldNode = new Element("field");
		fieldNode.setAttribute(ATTRIBUTE_ID, String.valueOf(field.getId()));
		fieldNode.setAttribute("tableID", String.valueOf(field.getEntityId()));
		fieldNode.setAttribute("connID", String.valueOf(table.getDataSourceId()));
		fieldNode.setAttribute(ATTRIBUTE_NAME, field.getName());
		fieldNode.setAttribute("description", field.getDescription() != null ? field.getDescription() : "");
		fieldNode.setAttribute("type", strType);
		fieldNode.setAttribute("uniqueKey", uniqueKey? "true" : "false");
		fieldNode.setAttribute("readOnly", readOnly? "true" : "false");
		fieldNode.setAttribute("size", String.valueOf(field.getColumnSize()));
		fieldNode.setAttribute("decimalDigits", String.valueOf(field.getDecimalDigits()));
		tableNode.addContent(fieldNode);
  	
  	root.addContent(tableNode);
  	return root;
  }

  public Element getViewField(DataViewField field, String action, String msg){
	  	Element root = new Element(ROOT_ROOT);
	  	root.addContent(msg);
	  	root.setAttribute("action", action);
	  	Element fieldNode = new Element("field");
		fieldNode.setAttribute(ATTRIBUTE_ID, String.valueOf(field.getId()));
		fieldNode.setAttribute("viewID", String.valueOf(field.getDataViewId()));
		fieldNode.setAttribute(ATTRIBUTE_NAME, field.getName());
		fieldNode.setAttribute("description", field.getDescription() == null ? "" : field.getDescription());
		fieldNode.setAttribute("uniqueKey", field.getUniqueKey());
		fieldNode.setAttribute("sourceTable",field.getSourceEntity());
		fieldNode.setAttribute("sourceField",field.getSourceField());
		fieldNode.setAttribute("displayName", field.getDisplayName());
		fieldNode.setAttribute("decimalDigits", String.valueOf(field.getDecimalDigits()));
		fieldNode.setAttribute("namingField", field.getFlags() == Field.FF_NAMING ? "true" : "false");
	  	
	  	root.addContent(fieldNode);
	  	return root;
	  } 
  /**
   * This method should has same format with getViewField method
   * @param field
   * @param table
   * @return
   */
  public Element getTableFieldForView(FieldDobj field, EntityDobj table){

	    // type
	    int type = field.getType();
	    String strType = null;
	    if(type == Field.FT_STRING)
	    	strType = TYPE_STRING;
	    else if(type == Field.FT_INTEGER)
	    	strType = TYPE_INTEGER;
	    else if(type == Field.FT_DECIMAL)
	        strType = TYPE_DECIMAL;
	    else if(type == Field.FT_DATETIME)
	    	strType = TYPE_DATE_TIME;
	    else if(type == Field.FT_CURRENCY)
	    	strType = TYPE_CURRENCY;
	    else if(type == Field.FT_BOOLEAN)
	    	strType = TYPE_BOOLEAN; 
	    else
	    	strType = TYPE_UNKNOWN;
	    
	    
	    // flags
	    int flags = field.getFlags();
	    boolean readOnly = false;
	    if(flags == Field.FF_READ_ONLY)
	    	readOnly = true;
	    boolean uniqueKey = false;
	    if(flags == Field.FF_UNIQUE_KEY)
	    	uniqueKey = true;
	    
		Element root = new Element(ROOT_ROOT);
	  	
	  	
	  	Element fieldNode = new Element("field");
			fieldNode.setAttribute(ATTRIBUTE_ID, String.valueOf(field.getId()));
			fieldNode.setAttribute("tableID", String.valueOf(field.getEntityId()));
			fieldNode.setAttribute("connID", String.valueOf(table.getDataSourceId()));
			fieldNode.setAttribute(ATTRIBUTE_NAME, field.getName());
			fieldNode.setAttribute("description", field.getDescription() != null ? field.getDescription() : "");
			fieldNode.setAttribute("type", strType);
			fieldNode.setAttribute("uniqueKey", uniqueKey? "true" : "false");
			fieldNode.setAttribute("readOnly", readOnly? "true" : "false");
			fieldNode.setAttribute("size", String.valueOf(field.getColumnSize()));
			fieldNode.setAttribute("decimalDigits", String.valueOf(field.getDecimalDigits()));
			
			//These two fields are for display information in view part
			fieldNode.setAttribute("displayName", "");
			fieldNode.setAttribute("namingField", "false");
			
			root.addContent(fieldNode);
	  	
	  	return root;
	  }  
  
  public Element getQuery(QueryDobj query){
	  	Element root = new Element("query");
	  	
	  	Element queryNode = new Element("query");
	  	queryNode.setAttribute("queryID", String.valueOf(query.getId()));
	  	
	  	queryNode.setAttribute(ATTRIBUTE_NAME, query.getName());
	  	queryNode.setAttribute("description", query.getDescription() == null ? "" : query.getDescription());
	  	queryNode.setAttribute(ATTRIBUTE_TYPE, "query");
	  	
	  	if(query.getDatasourceStatus() == 1){
		  	queryNode.setAttribute("viewOrTableID", String.valueOf(query.getViewOrTableId()));
		  	queryNode.setAttribute("queryType", query.getType());
		  	queryNode.setAttribute("sql", query.getCriteriaString() == null ? "" : query.getCriteriaString());
		  	queryNode.setAttribute("sort", query.getSortString() != null ? query.getSortString() : "");
		  	queryNode.setAttribute("groupFieldID", String.valueOf(query.getGroupFieldId()));
		  	
		  	if(query.getType().equals("select")){
			  	DataView dataView = query.getDataView (true);
			  	queryNode.setAttribute("viewID", String.valueOf(dataView.getId()));
			  	queryNode.setAttribute("viewOrTableName", dataView.getName());
			  	queryNode.setAttribute("sourceTables", dataView.getSourceTableNames());
		  		Object[] fields = dataView.getFields ().toArray();
			  	for (int i = 0; i < fields.length; i++){
			  		DataViewField field = (DataViewField)fields[i];
					Element fieldNode = new Element("field");
					fieldNode.setAttribute(ATTRIBUTE_ID, String.valueOf(field.getId()));
					fieldNode.setAttribute("viewID", String.valueOf(field.getDataViewId()));
					String fieldName = "";
					if (field.getSourceEntity().indexOf(" ") > -1){
						fieldName = "[" + field.getSourceEntity() + "]";
					}else{
						fieldName = field.getSourceEntity();
					}
					if (field.getSourceField().indexOf(" ") > -1){
						fieldName += ".[" + field.getSourceField() + "]";
					}else{
						fieldName += "." + field.getSourceField();
					}
					fieldNode.setAttribute(ATTRIBUTE_NAME, fieldName);
					fieldNode.setAttribute("description", field.getDescription() == null ? "" : field.getDescription());

			  	    // type
			  	    int type = field.getType();
			  	    String strType = null;
			  	    if(type == Field.FT_STRING)
			  	    	strType = TYPE_STRING;
			  	    else if(type == Field.FT_INTEGER)
			  	    	strType = TYPE_INTEGER;
			  	    else if(type == Field.FT_DECIMAL)
			  	        strType = TYPE_DECIMAL;
			  	    else if(type == Field.FT_DATETIME)
			  	    	strType = TYPE_DATE_TIME;
			  	    else if(type == Field.FT_CURRENCY)
			  	    	strType = TYPE_CURRENCY;
			  	    else if(type == Field.FT_BOOLEAN)
			  	    	strType = TYPE_BOOLEAN; 
			  	    else
			  	    	strType = TYPE_UNKNOWN;				
					fieldNode.setAttribute("type", strType);
					queryNode.addContent(fieldNode);
					if (field.getId() == query.getGroupFieldId()){
						queryNode.setAttribute("groupFieldName", field.getName());					
					}				
			  	}	  		
		  	}else if(query.getType().equalsIgnoreCase("insert") || query.getType().equalsIgnoreCase("update")){//Insert & update
		  		EntityDobj entity = query.getTable (true);
		  		queryNode.setAttribute("viewOrTableName", entity.getName());
		  		Object[] fields = entity.getFields ().toArray();
		  		
		  		for (int i = 0; i < fields.length; i++){
			  		FieldDobj field = (FieldDobj)fields[i];
					Element fieldNode = new Element("field");
					fieldNode.setAttribute(ATTRIBUTE_ID, String.valueOf(field.getId()));
					fieldNode.setAttribute("viewID", String.valueOf(field.getEntityId()));
					fieldNode.setAttribute(ATTRIBUTE_NAME, field.getName().indexOf(" ") > -1 ? "[" + field.getName()+ "]" : field.getName());
					fieldNode.setAttribute("description", field.getDescription() == null ? "" : field.getDescription());
					queryNode.addContent(fieldNode);
			  	    // type
			  	    int type = field.getType();
			  	    String strType = null;
			  	    if(type == Field.FT_STRING)
			  	    	strType = TYPE_STRING;
			  	    else if(type == Field.FT_INTEGER)
			  	    	strType = TYPE_INTEGER;
			  	    else if(type == Field.FT_DECIMAL)
			  	        strType = TYPE_DECIMAL;
			  	    else if(type == Field.FT_DATETIME)
			  	    	strType = TYPE_DATE_TIME;
			  	    else if(type == Field.FT_CURRENCY)
			  	    	strType = TYPE_CURRENCY;
			  	    else if(type == Field.FT_BOOLEAN)
			  	    	strType = TYPE_BOOLEAN; 
			  	    else
			  	    	strType = TYPE_UNKNOWN;
			  	    
			  	    fieldNode.setAttribute("type", strType);
					if (field.getId() == query.getGroupFieldId()){
						queryNode.setAttribute("groupFieldName", field.getName());					
					}
			  	}	  		
		  	}		  	
		  	
	  	}else{
	  		queryNode.setAttribute("viewOrTableID", String.valueOf(query.getWebServiceId()));
	  	}

//        // get criteria from the query	  	
//	  	Object[] sorts = query.getSorts().toArray();
//        for(int i = 0; i < sorts.length; i++){
//        	String sort = (String)sorts[i];
//        	Element sortNode = new Element("sort");
//        	sortNode.setAttribute("field", sort);
//        	sortNode.setAttribute("", sort)
//        }
	  	
	  	//TODO;;Webservice
	  	
	  	root.addContent(queryNode);
	  	
	  	System.out.println(root);
	  	
	  	return root;
  } 

  public Element getEmptyQueryRecordSet(String action){
	Element root = new Element("root");
	root.setAttribute("action", action);
	Element metaDataNode = new Element("metadata"); 
    
    root.addContent(metaDataNode);
    
    Element resultsetNode = new Element("resultset");
    
    root.addContent(resultsetNode);
  	return root;
}   
  
  public Element getQueryRecordSet(DataView dataView, RecordSet rs, String sorts){//QueryDobj query, 
	
	  Element root = new Element("root");
	  	//root.setAttribute("name", "db_query_result");
	  	
	  	if (rs != null && rs.size()>=1){
	  		root.setAttribute("result", "hasData");
	  		root.setAttribute("viewId", dataView.getId()+"");
	        Vector dobjs = rs.getRows();

	        Vector flds = null;
	        //DataView dataView = query.getDataView();
	        if(dobjs != null && dobjs.size() > 0)
	            flds = ((DataObject)dobjs.elementAt(0)).getEntity().getFields();
	        else
	            flds = dataView.getFields();
	        String colTitles[] = new String[flds.size()];
	        DataViewField fld;
	        Element metaDataNode = new Element("metadata"); 
	        
	        for (int i = 0; i < flds.size(); i++)
	        {
	            fld = (DataViewField)flds.elementAt(i);
	            colTitles[i] = fld.getDisplayName(); 
	            Element titleNode = new Element("column"); 
	            titleNode.setAttribute("name", colTitles[i]);
	            titleNode.setAttribute("display", colTitles[i]);
	            titleNode.setAttribute("editable", "false");
	            titleNode.setAttribute("resizable", "true");
	            metaDataNode.addContent(titleNode);
	        }
	        root.addContent(metaDataNode);
	        
	        Element resultsetNode = new Element("resultset");
	        // build the table model
	        DataObject dobj;
	        for(int i = 0, j = 0; dobjs != null && i < dobjs.size(); i++)
	        {
	            Element rowNode = new Element("row");
	            
	        	// get the data object
	            dobj = (DataObject)dobjs.elementAt(i);

	            // if it valid, add it
	            // iterate the columns
	            Vector pvRow = new Vector();
	            for(int col = 0; col < colTitles.length; col++)
	            {
	            	// get the value and add to the row
	                //Element columnNode = new Element("col");
	            	Object value = null;
	                Record r = (Record)dobj;
	                if (r != null)
	                {
	                    String colName = (col > colTitles.length)? "": colTitles[col];
	                    Vector fields = r.getEntity().getFields();
	                    String fldName = ((DataViewField)fields.elementAt(col)).getName();
	                    Object val = r.getObjectValue(fldName);
	                    
	                    value = (val == null)? new String(""): val;
	                    Element columnNode = new Element(colName.indexOf(" ")> 0? colName.replace(" ", ""): colName);
	                    //columnNode.setAttribute("name", colName);
	                    columnNode.setAttribute("value", value.toString());
	                    //rowNode.setAttribute(colName, value.toString());
	                    rowNode.addContent(columnNode);
	                }	   
	                //rowNode.addContent(columnNode);
	            }
	            resultsetNode.addContent(rowNode);
	        }
	        root.addContent(resultsetNode);
	  	}else{
	  		root.setAttribute("result", "NoData");
	  		Element metaDataNode = new Element("metadata");
	        Element titleNode = new Element("column"); 
	        titleNode.setAttribute("name", "No-data");
	        titleNode.setAttribute("display", "No-data");
	        titleNode.setAttribute("editable", "false");
	        titleNode.setAttribute("resizable", "true");
	        metaDataNode.addContent(titleNode);
	  		root.addContent(metaDataNode);
	  		Element resultsetNode = new Element("resultset");
	  		root.addContent(resultsetNode);
	  	}
	  	return root;
} 
  public Element getQueryCriteriaXml(List<QueryCriteriaDobj> queryCriteria){
	  	Element root = new Element(ROOT_ROOT);
	  	
	  	for (QueryCriteriaDobj queryCriteriaDobj : queryCriteria){
		  	Element queryNode = new Element("node");
		  	queryNode.setAttribute(ATTRIBUTE_ID, String.valueOf(queryCriteriaDobj.getId()));
		  	queryNode.setAttribute("row", String.valueOf(queryCriteriaDobj.getRowNo()));
		  	queryNode.setAttribute(ATTRIBUTE_TYPE, queryCriteriaDobj.getType() == null ? "query" : queryCriteriaDobj.getType());
		  	queryNode.setAttribute("valueOrCondition", queryCriteriaDobj.getValueOrCondition() == null ? "" : queryCriteriaDobj.getValueOrCondition());
		  	queryNode.setAttribute("used", String.valueOf(queryCriteriaDobj.getUsed()));
		  	queryNode.setAttribute("indent", queryCriteriaDobj.getIndent() == -1 ? "0" : String.valueOf(queryCriteriaDobj.getIndent()));
		  	queryNode.setAttribute("parent", queryCriteriaDobj.getParent() == null ? "" : queryCriteriaDobj.getParent());
		  	queryNode.setAttribute("number", String.valueOf(queryCriteriaDobj.getNumber()));
		  	queryNode.setAttribute("field", queryCriteriaDobj.getName());
		  	queryNode.setAttribute("compID", String.valueOf(queryCriteriaDobj.getCompId()));
		  	queryNode.setAttribute("comparison", queryCriteriaDobj.getComparison());
		  	queryNode.setAttribute("value", queryCriteriaDobj.getValue() == null ? "_____" : queryCriteriaDobj.getValue());
		  	queryNode.setAttribute("connection", queryCriteriaDobj.getConnection());
		  	queryNode.setAttribute("value2", queryCriteriaDobj.getValue2()== null ? "_____" : queryCriteriaDobj.getValue2());
		  	queryNode.setAttribute("grouping", queryCriteriaDobj.getGrouping());
		  	queryNode.setAttribute("valueUserInput", queryCriteriaDobj.getValueUserInputSeq() == null ? "" : queryCriteriaDobj.getValueUserInputSeq());
		  	queryNode.setAttribute("value2UserInput", queryCriteriaDobj.getValue2UserInputSeq() == null ? "" : queryCriteriaDobj.getValue2UserInputSeq());
		  	root.addContent(queryNode);
	  	}
	  	
	  	System.out.println(root);
	  	
	  	return root;
  }

  public Element getQueryCriteria(List<QueryCriteriaDobj> queryCriteria){
	  	Element root = new Element("criteria");
	  	
	  	for (QueryCriteriaDobj queryCriteriaDobj : queryCriteria){
		  	Element queryNode = new Element("node");
		  	queryNode.setAttribute(ATTRIBUTE_ID, String.valueOf(queryCriteriaDobj.getId()));
		  	queryNode.setAttribute("row", String.valueOf(queryCriteriaDobj.getRowNo()));
		  	queryNode.setAttribute(ATTRIBUTE_TYPE, queryCriteriaDobj.getType() == null ? "query" : queryCriteriaDobj.getType());
		  	queryNode.setAttribute("valueOrCondition", queryCriteriaDobj.getValueOrCondition() == null ? "" : queryCriteriaDobj.getValueOrCondition());
		  	queryNode.setAttribute("used", String.valueOf(queryCriteriaDobj.getUsed()));
		  	queryNode.setAttribute("indent", queryCriteriaDobj.getIndent() == -1 ? "0" : String.valueOf(queryCriteriaDobj.getIndent()));
		  	queryNode.setAttribute("parent", queryCriteriaDobj.getParent() == null ? "" : queryCriteriaDobj.getParent());
		  	queryNode.setAttribute("number", String.valueOf(queryCriteriaDobj.getNumber()));
		  	queryNode.setAttribute("field", queryCriteriaDobj.getName());
		  	queryNode.setAttribute("compID", String.valueOf(queryCriteriaDobj.getCompId()));
		  	queryNode.setAttribute("comparison", queryCriteriaDobj.getComparison());
		  	queryNode.setAttribute("value", queryCriteriaDobj.getValue() == null ? "_____" : queryCriteriaDobj.getValue());
		  	queryNode.setAttribute("connection", queryCriteriaDobj.getConnection());
		  	queryNode.setAttribute("value2", queryCriteriaDobj.getValue2()== null ? "_____" : queryCriteriaDobj.getValue2());
		  	queryNode.setAttribute("grouping", queryCriteriaDobj.getGrouping());
		  	queryNode.setAttribute("valueUserInput", queryCriteriaDobj.getValueUserInputSeq() == null ? "" : queryCriteriaDobj.getValueUserInputSeq());
		  	queryNode.setAttribute("value2UserInput", queryCriteriaDobj.getValue2UserInputSeq() == null ? "" : queryCriteriaDobj.getValue2UserInputSeq());
		  	root.addContent(queryNode);
	  	}
	  	
	  	System.out.println(root);
	  	
	  	return root;
}  
  
  public Element getViewForQueryCriteria(DataView view){
	  	Element root = new Element("view");
	  	
	  	if (view == null){
	  		return root;
	  	}
	  	List<DataViewField> fields = view.getFields();
	  	Vector<String> tableNames = new Vector<String>();
	  	
	  	for (DataViewField field : fields){
		  	String tableName = field.getSourceEntity();
		  	
		  	if (!tableNames.contains(tableName)){
		  		Element fieldNode = new Element("node");
		  		fieldNode.setAttribute("field", tableName.indexOf(" ") > -1 ? "[" + tableName + "]" : tableName);
		  		root.addContent(fieldNode);
		  		tableNames.add(tableName);
		  	}		  		  		
	  	}	  	
	  	
	  	for (DataViewField field : fields){
		  	String tableName = field.getSourceEntity();
		  	
	  		Element subNode = new Element("subnode");
	  		subNode.setAttribute("field", field.getSourceField().indexOf(" ") > -1 ? "[" + field.getSourceField() + "]" : field.getSourceField());		  	
		  	List<Element> fieldNodes = root.getChildren(); 	
			for (Element fieldNode : fieldNodes){
				String currentTableName = fieldNode.getAttributeValue("field");
				boolean added = false;
				int index1 = currentTableName.indexOf("[");
				int index2 = currentTableName.indexOf("]");
				if ( index1 > -1 && index2 > -1){
					if (currentTableName.equals("[" + tableName + "]"))
						added = true;
				}else{
					if (currentTableName.equals(tableName))
						added = true;
				}
				if (added){
					fieldNode.addContent(subNode);
				}
			}	  		  		
	  	}

	  	return root;
  }  
   
  public Element getTableForQueryCriteria(EntityDobj table){
	  	Element root = new Element("view");//use view as root name because at the UI, this is treated as view node
	  	List<FieldDobj> fields = table.getFields();
	  	
//  		Element fieldNode = new Element("node");
//  		fieldNode.setAttribute("field", table.getName());
//  		root.addContent(fieldNode);	  	
	  	
	  	for (FieldDobj field : fields){
		  	Element subNode = new Element("node");
		  	subNode.setAttribute("field", field.getName());		  	
		  	root.addContent(subNode);	  		
	  	}

	  	return root;
}  
  private void addElementWithAttribute(Element root, String elementName, 
      String attributeName, String attributeValue){
      Element element = new Element(elementName);
      element.setAttribute(attributeName, attributeValue);
      root.addContent(element);
  }
  
  private void addElementWith2Attributes(Element root, String elementName, 
      String attribute1Name, String attribute1Value,
      String attribute2Name, String attribute2Value){
      Element element = new Element(elementName);
      element.setAttribute(attribute1Name, attribute1Value);
      element.setAttribute(attribute2Name, attribute2Value);
      root.addContent(element);
  }
  
  private void addFieldElementWith2Attributes(Element root, String elementName, 
      String attribute1Name, String attribute1Value,
      String attribute2Name, String attribute2Value){
      Element element = new Element(elementName);
      element.setAttribute(attribute1Name, attribute1Value);
      element.setAttribute(attribute2Name, attribute2Value);
      element.setAttribute(ATTRIBUTE_ICON_NAME, "field");
      root.addContent(element);
  }
  
  private void addTableElementWith2Attributes(Element root, String elementName, 
      String attribute1Name, String attribute1Value,
      String attribute2Name, String attribute2Value){
      Element element = new Element(elementName);
      element.setAttribute(attribute1Name, attribute1Value);
      element.setAttribute(attribute2Name, attribute2Value);
      element.setAttribute(ATTRIBUTE_ICON_NAME, "table");
      root.addContent(element);
  }
  
  public synchronized Element newIMConnectionResult(String file, String result, String newImConnId, boolean isSuccess){ 
	  	Element root = new Element(ROOT_ROOT);
	  	root.setAttribute("action", "imNew");
	  	root.setAttribute("result", result);	  	
	  	root.setAttribute("connId", newImConnId);
	  	root.setAttribute("isSuccess", String.valueOf(isSuccess));
	  	return root;
  }

  public synchronized Element showMsgResult(String file, String result, String vtLabel, QueryDobj msgQ, List<DataSourceDobj> databases, List tablesOrViewsList){ 
	  	Element root = new Element(ROOT_ROOT);	  	
	  	root.setAttribute("msgResult", result);	  	
	  	//root.setAttribute("msgId", String.valueOf(msgId));	  

	  	root.setAttribute("queryTypeName", msgQ.getType());	  	
	  	root.setAttribute("queryTypeValue", String.valueOf(MessagingUtils.getQueryTypeNumber(msgQ.getType())));	  	

	  	
	  	root.setAttribute("vtLable", vtLabel);	  	
		  	
	  	root.setAttribute("msgId", String.valueOf(msgQ.getId()));
	  	root.setAttribute("name", msgQ.getName());
	  	if(msgQ.getDescription() != null && !msgQ.getDescription().equals(""))
	  		root.setAttribute("description", msgQ.getDescription());	    
	  	
	  	root.setAttribute("dbId", String.valueOf(msgQ.getDatabaseId()));	  	
	  	root.setAttribute("viewOrTableId", String.valueOf(msgQ.getViewOrTableId()));
	  	root.setAttribute("queryTypeInDB", String.valueOf(MessagingUtils.getQueryTypeNumber(msgQ.getType())));
	  	root.setAttribute("tableId", String.valueOf(msgQ.getViewOrTableId()));	    
	  	root.setAttribute("emailId", String.valueOf(msgQ.getEmailAddressId()));

	  	if(msgQ.getEmailKeyword()!= null && !msgQ.getEmailKeyword().equals(""))
	  		root.setAttribute("emailKeyword", msgQ.getEmailKeyword());	    
	  	if(msgQ.getSmsKeyword()!= null && !msgQ.getSmsKeyword().equals(""))
	  		root.setAttribute("smsKeyword", msgQ.getSmsKeyword());	    
	  	if(msgQ.getImKeyword()!= null && !msgQ.getImKeyword().equals(""))
	  		root.setAttribute("imKeyword", msgQ.getImKeyword());	    
	  	
	  	root.setAttribute("mobileStatus", String.valueOf(msgQ.getMobileStatus()));	    
	  	root.setAttribute("imStatus", String.valueOf(msgQ.getIMStatus()));	    
	  	root.setAttribute("emailDisplayResult", String.valueOf(msgQ.getEmailDisplayResult()));	    
	  	root.setAttribute("mobileDisplayResult", String.valueOf(msgQ.getMobileDisplayResult()));
	  	root.setAttribute("imDisplayResult", String.valueOf(msgQ.getImDisplayResult()));	    
	  	
	  	for (DataSourceDobj db : databases) {
	  		Element nodeDb = new Element("db");
	  		nodeDb.setAttribute("name", db.getName());
	  		nodeDb.setAttribute("id", Integer.toString(db.getId()));	  		
	  		root.addContent(nodeDb);
		}

  		if(vtLabel.equalsIgnoreCase(Constants.VIEWS_LABEL)){//If list objects be instanceof DataView
  			for (int i = 0; i<tablesOrViewsList.size(); i++) {
  				DataView view = (DataView) tablesOrViewsList.get(i);
  				Element nodeViews = new Element("query");
  				nodeViews.setAttribute("name", view.getName());
  				nodeViews.setAttribute("id", Integer.toString(view.getId()));	  		
  				root.addContent(nodeViews);
  			}
		}else if(vtLabel.equalsIgnoreCase(Constants.TABLE_LABEL)){//if list objects be instanceof EntityDobj
  			for (int i = 0; i<tablesOrViewsList.size(); i++) {
  				EntityDobj table = (EntityDobj) tablesOrViewsList.get(i);
  				Element nodeViews = new Element("query");
  				nodeViews.setAttribute("name", table.getName());
  				nodeViews.setAttribute("id", Integer.toString(table.getId()));	  		
  				root.addContent(nodeViews);
  			}
		}
	  	return root;
  }

  public synchronized Element showMsgResponse(String file, String result, String vtLabel, QueryDobj msgQ, List<DataSourceDobj> databases, List tablesOrViewsList){ 
	  	Element root = new Element(ROOT_ROOT);	  	
	  	root.setAttribute("msgResponse", result);	  	
	  	//root.setAttribute("msgId", String.valueOf(msgId));	  

	  	root.setAttribute("queryTypeName", msgQ.getType());	  	
	  	root.setAttribute("queryTypeValue", String.valueOf(MessagingUtils.getQueryTypeNumber(msgQ.getType())));	  	

	  	
	  	root.setAttribute("vtLable", vtLabel);	  	
		  	
	  	root.setAttribute("msgId", String.valueOf(msgQ.getId()));
	  	root.setAttribute("name", msgQ.getName());
	  	if(msgQ.getDescription() != null && !msgQ.getDescription().equals(""))
	  		root.setAttribute("description", msgQ.getDescription());	    
	  	
	  	root.setAttribute("dbId", String.valueOf(msgQ.getDatabaseId()));	  	
	  	root.setAttribute("viewOrTableId", String.valueOf(msgQ.getViewOrTableId()));
	  	root.setAttribute("queryTypeInDB", String.valueOf(MessagingUtils.getQueryTypeNumber(msgQ.getType())));
	  	root.setAttribute("tableId", String.valueOf(msgQ.getViewOrTableId()));	    
	  	root.setAttribute("emailId", String.valueOf(msgQ.getEmailAddressId()));

	  	if(msgQ.getEmailKeyword()!= null && !msgQ.getEmailKeyword().equals(""))
	  		root.setAttribute("emailKeyword", msgQ.getEmailKeyword());	    
	  	if(msgQ.getSmsKeyword()!= null && !msgQ.getSmsKeyword().equals(""))
	  		root.setAttribute("smsKeyword", msgQ.getSmsKeyword());	    
	  	if(msgQ.getImKeyword()!= null && !msgQ.getImKeyword().equals(""))
	  		root.setAttribute("imKeyword", msgQ.getImKeyword());	    
	  	
	  	root.setAttribute("mobileStatus", String.valueOf(msgQ.getMobileStatus()));	    
	  	root.setAttribute("imStatus", String.valueOf(msgQ.getIMStatus()));	    
	  	root.setAttribute("emailDisplayResult", String.valueOf(msgQ.getEmailDisplayResult()));	    
	  	root.setAttribute("mobileDisplayResult", String.valueOf(msgQ.getMobileDisplayResult()));
	  	root.setAttribute("imDisplayResult", String.valueOf(msgQ.getImDisplayResult()));	    
	  	
	  	for (DataSourceDobj db : databases) {
	  		Element nodeDb = new Element("db");
	  		nodeDb.setAttribute("name", db.getName());
	  		nodeDb.setAttribute("id", Integer.toString(db.getId()));	  		
	  		root.addContent(nodeDb);
		}

		if(vtLabel.equalsIgnoreCase(Constants.VIEWS_LABEL)){//If list objects be instanceof DataView
			for (int i = 0; i<tablesOrViewsList.size(); i++) {
				DataView view = (DataView) tablesOrViewsList.get(i);
				Element nodeViews = new Element("query");
				nodeViews.setAttribute("name", view.getName());
				nodeViews.setAttribute("id", Integer.toString(view.getId()));	  		
				root.addContent(nodeViews);
			}
		}else if(vtLabel.equalsIgnoreCase(Constants.TABLE_LABEL)){//if list objects be instanceof EntityDobj
			for (int i = 0; i<tablesOrViewsList.size(); i++) {
				EntityDobj table = (EntityDobj) tablesOrViewsList.get(i);
				Element nodeViews = new Element("query");
				nodeViews.setAttribute("name", table.getName());
				nodeViews.setAttribute("id", Integer.toString(table.getId()));	  		
				root.addContent(nodeViews);
			}
		}
	  	return root;
}

  
  public synchronized Element saveIMConactForUserResult(String result){ 
	  	Element root = new Element(ROOT_ROOT);
	  	root.setAttribute("action", "saveImContact");
	  	root.setAttribute("result", result);	  	
	  	return root;
 }
  
  public synchronized Element viewIMConnectionResult(String file, IMConnection result, String btnTxt){
	  	Element root = new Element(ROOT_ROOT);
	  	root.setAttribute("action", "imConn");
	  	root.setAttribute("name", result.getName());	  	
	  	root.setAttribute("username", result.getUserName());	  	
	  	root.setAttribute("password", result.getPassword());
	  	root.setAttribute("type", MessagingUtils.getConnectionTypeNameByID(result.getType()));
	  	root.setAttribute("status", String.valueOf(result.getStatus()));
	  	root.setAttribute("id", String.valueOf(result.getId()));	  	
	  	String buttonText;
	  	String statusText = "";
//	  	if(result.getStatus()==0)
//	  	{
//	  		buttonText="Connect";
//	  		statusText="Disconnected";
//	  	}else{
//	  		buttonText="Disconnect";
//	  		statusText="Connected";
//	  	}

	  	if(btnTxt.equalsIgnoreCase("connect")) {
	  		statusText="Disconnected";
	  	}
	  	if(btnTxt.equalsIgnoreCase("disconnect")){
	  		statusText="Connected";
	  	}
	  	
	  	root.setAttribute("btnText", btnTxt);

	  	if(result.getStatusDesc()!= null)
	  	{
	  		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.DEFAULT);
	  		String datetime = df.format(result.getStatusDesc());
	  		String statusDesc = statusText + " at " + datetime;
	  		root.setAttribute(ATTRIBUTE_DATE_TIME, statusDesc);
	  	}
	  	
	  	return root;
  }

  public synchronized Element connectDisconnectResult(String file, String result, String btnTxt){ 
	  	Element root = new Element(ROOT_ROOT);
	  	root.setAttribute("action", "connDisConn");
  		root.setAttribute("result", result);
  		root.setAttribute("btnText", btnTxt);
	  	return root;
  }

  public synchronized Element connectDisconnectSms(String result, String smsBtnTxt){ 
	  	Element root = new Element(ROOT_ROOT);
	  	root.setAttribute("action", "connDisconnSms");
	  	root.setAttribute("result", result);	  	
	  	root.setAttribute("smsBtnTxt", smsBtnTxt);	  		  	
	  	return root;
}
  
  public synchronized Element showSmsInfo(MdnSmsSetting mdnInfo, String smsBtnTxt, List<String> baundrateList, String action, String result){ 
	  	Element root = new Element(ROOT_ROOT);

	  	for (String rate : baundrateList) {
	  		Element rateNode = new Element("baudratesNode");
	  		rateNode.setAttribute(ATTRIBUTE_NAME, rate);
	  		root.addContent(rateNode);
	  	}
	  	
	  	if(mdnInfo != null && mdnInfo.getNumber() != null)
	  		root.setAttribute("simNum", mdnInfo.getNumber());
	  	if(mdnInfo != null && mdnInfo.getComm() != null)
		  	root.setAttribute("comm", mdnInfo.getComm());
	  	if(mdnInfo != null && mdnInfo.getBaudrate() != null)
		  	root.setAttribute("baudrate", mdnInfo.getBaudrate());
	  	if(mdnInfo != null && mdnInfo.getModemManufacturer() != null)
		  	root.setAttribute("modemMan", mdnInfo.getModemManufacturer());
	  	if(mdnInfo != null && mdnInfo.getmodemModel() != null)
		  	root.setAttribute("modemModel", mdnInfo.getmodemModel());	
	  	
	  	root.setAttribute("smsBtnTxt", smsBtnTxt);
	  	root.setAttribute("action", action);
	  	root.setAttribute("result", result);

	  	return root;
}

  public synchronized Element displaySmsGateway(MdnSmpp smsGateway, String result, String response, String isTreeChanged){ 
	  	Element root = new Element(ROOT_ROOT);

	  	if(smsGateway != null){
	  		root.setAttribute("id", String.valueOf(smsGateway.getId()));	  		
		  	if(smsGateway.getNumber() != null){
		  		if(!smsGateway.getNumber().contains("+"))
		  			root.setAttribute("number", "+" + smsGateway.getNumber().trim());
				else
					root.setAttribute("number", smsGateway.getNumber());
  			}if(smsGateway.getHost() != null)
		  		root.setAttribute("host", smsGateway.getHost());
		  	if(smsGateway.getPort() != null)
		  		root.setAttribute("port", smsGateway.getPort());
		  	if(smsGateway.getUsername() != null)
		  		root.setAttribute("username", smsGateway.getUsername());		  	
		  	if(smsGateway.getPassword() != null)
		  		root.setAttribute("password", smsGateway.getPassword());		  			  	
		  	if(smsGateway.getSourceNPI() != null)
		  		root.setAttribute("npi", smsGateway.getSourceNPI());
		  	if(smsGateway.getSourceTON() != null)
		  		root.setAttribute("ton", smsGateway.getSourceTON());
		  	if(smsGateway.getDestNPI() != null)
		  		root.setAttribute("destNpi", smsGateway.getDestNPI());
		  	if(smsGateway.getDestTON() != null)
		  		root.setAttribute("destTon", smsGateway.getDestTON());		  			  	
		  	if(smsGateway.getBindNPI() != null)
		  		root.setAttribute("bindNpi", smsGateway.getBindNPI());
		  	if(smsGateway.getBindTON() != null)
		  		root.setAttribute("bindTon", smsGateway.getBindTON());		  			  			  	
		  	root.setAttribute("tlv", booleanConvertor(smsGateway.getUseTlv()));
		  	root.setAttribute("useAddrRange", booleanConvertor(smsGateway.getUseAddrRange()));
		  	root.setAttribute("checkTime", String.valueOf(smsGateway.getInterval()));		  			  	
	  	}
	  	if(result != null)
	  		root.setAttribute("result", result);
	  	
	  	if(response != null)
  			root.setAttribute("response", response);
	  	
	  	if(isTreeChanged != null)
	  		root.setAttribute("isTreeChanged", isTreeChanged);
	  	
	  	return root;
  }
  
  public synchronized Element getAllIMConnectionResult(List<IMConnection> imConnections){ 
	  Element imList = new Element("im_connection_list");
	  	Element connNode = new Element("conn");
	  	for (IMConnection conn : imConnections) {
	  		Element nodeImConn = new Element("IMConnection");
	  		nodeImConn.setAttribute(ATTRIBUTE_ID, String.valueOf(conn.getId()));
	  		nodeImConn.setAttribute(ATTRIBUTE_NAME, conn.getName());
	  		nodeImConn.setAttribute(ATTRIBUTE_TYPE, Integer.toString(conn.getType()));
	  		connNode.addContent(nodeImConn);
	  	}
	  	imList.addContent(connNode);
	  	return imList;
  }

  public synchronized Element deleteIMConnectionResult(String file, String result){ 
	  	Element root = new Element(ROOT_ROOT);
	  	root.setAttribute("action", "deleteIm");
	  	root.setAttribute("result", result);	  	
	  	return root;
}

  public Element newMessagingXML(List<DataSourceDobj> databases, List viewsTables, String vtLable, 
		  List<MdnEmailSetting> emailAddresses, List<MdnSmpp> allSmpp, String languageFile){
	  	Element root = new Element(ROOT_ROOT);

	  	root.setAttribute("vtLable", vtLable);	  	
	  	//root.setAttribute("msgId", qMsgId);

        Element guiDef = this.getXMLLanguageFile(languageFile);
	  	
        //Fill Email Addressesb combobox -->>
        String emailTagName = "emailAddress";
  		Element addNewNode = new Element(emailTagName);
  		addNewNode.setAttribute("name", getGuiValueForElement(guiDef, "lbl-add-new-item"));
  		addNewNode.setAttribute("id", "0");	  		
  		root.addContent(addNewNode);

  		Element noEmailNode = new Element(emailTagName);
  		noEmailNode.setAttribute("name", getGuiValueForElement(guiDef, "lbl-no-email"));
  		noEmailNode.setAttribute("id", "-1");	  		
  		root.addContent(noEmailNode);
  		
	  	for (MdnEmailSetting emailAddress : emailAddresses) {
	  		if(emailAddress.getDelStatus()== Constants.MARKED_AS_NOT_DELETED_INT){
		  		Element nodeEmail = new Element(emailTagName);
		  		nodeEmail.setAttribute("name", emailAddress.getEmailAddress());
		  		nodeEmail.setAttribute("id", Integer.toString(emailAddress.getId()));	  		
		  		root.addContent(nodeEmail);
	  		}
		}
	  	//----------------------------------
	  	
	  	for (DataSourceDobj db : databases) {
	  		Element nodeDb = new Element("db");
	  		nodeDb.setAttribute("name", db.getName());
	  		nodeDb.setAttribute("id", Integer.toString(db.getId()));	  		
	  		root.addContent(nodeDb);
		}

	  	if(vtLable.equalsIgnoreCase(Constants.VIEWS_LABEL)){
			for (int i = 0; i<viewsTables.size(); i++) {
				DataView view = (DataView) viewsTables.get(i);
				Element nodeViews = new Element("query");
				nodeViews.setAttribute("name", view.getName());
				nodeViews.setAttribute("id", Integer.toString(view.getId()));	  		
				root.addContent(nodeViews);
			}
		}else if(vtLable.equalsIgnoreCase(Constants.TABLE_LABEL)){
			for (int i = 0; i<viewsTables.size(); i++) {
				EntityDobj table = (EntityDobj) viewsTables.get(i);
				Element nodeViews = new Element("query");
				nodeViews.setAttribute("name", table.getName());
				nodeViews.setAttribute("id", Integer.toString(table.getId()));	  		
				root.addContent(nodeViews);
			}
		}
	  	
	  	
	  	//Fill Mobile Combobox --------->>
	  	String mobileTagName = "smpp";
  		Element addNewMobNode = new Element(mobileTagName);
  		addNewMobNode.setAttribute("number", getGuiValueForElement(guiDef, "lbl-add-new-item"));
  		addNewMobNode.setAttribute("id", "0");	  		
  		root.addContent(addNewMobNode);

  		Element noSmsNode = new Element(mobileTagName);
  		noSmsNode.setAttribute("number", getGuiValueForElement(guiDef, "lbl-no-sms"));
  		noSmsNode.setAttribute("id", "-1");	  		
  		root.addContent(noSmsNode);	  	
	  	
  		Element nodeGsm = new Element("smpp");
  		nodeGsm.setAttribute("number", "GSM Modem Connection");
  		nodeGsm.setAttribute("id", "1");	  		
  		root.addContent(nodeGsm);	  	
	  	for (MdnSmpp smpp : allSmpp) {
	  		if(smpp.getDelStatus()== Constants.MARKED_AS_NOT_DELETED_INT){
		  		Element nodeSmpp = new Element("smpp");
		  		nodeSmpp.setAttribute("number", "SMPP " + smpp.getNumber());
		  		nodeSmpp.setAttribute("id", Integer.toString(smpp.getId()));	  		
		  		root.addContent(nodeSmpp);
	  		}
		}
	  	//------------------------------------
	  	
	  	return root;
	  }

  public Element emptyUserReplyXML(List<DataSourceDobj> databases, String vtLable, String queryMsgId, String queryName, String queryDesc){
	  	Element root = new Element(ROOT_ROOT);

	  	root.setAttribute("vtLable", vtLable);	  	
	  	root.setAttribute("queryId", queryMsgId);
	  	root.setAttribute("queryName", queryName);
	  	root.setAttribute("queryDesc", queryDesc == null ? "" : queryDesc);

	  	for (DataSourceDobj db : databases) {
	  		Element nodeDb = new Element("db");
	  		nodeDb.setAttribute("name", db.getName());
	  		nodeDb.setAttribute("id", Integer.toString(db.getId()));	  		
	  		root.addContent(nodeDb);
		}

	  	return root;
	  }
  
  public synchronized Element addMessagingInfoXML(String action, String file, String result, QueryDobj query, String responseText, String projectId){ 
	  	Element root = new Element(ROOT_ROOT);
	  	root.setAttribute("action", action);
	  	root.setAttribute("msgId", String.valueOf(query.getId()));
	  	if(query.getType()!= null)
	  		root.setAttribute("queryType", String.valueOf(MessagingUtils.getQueryTypeNumber(query.getType())));	  	
	  	root.setAttribute("viewID", String.valueOf(query.getViewOrTableId()));
	  	root.setAttribute("result", result);	  	
	  	root.setAttribute("projectId", projectId);
	  	root.setAttribute("msgResponse", responseText);
	  	return root;
  }

  public synchronized Element addUserReplyXML(String file, String result, UserReply userReply, String responseText, String projectId){ 
	  	Element root = new Element(ROOT_ROOT);
	  	root.setAttribute("action", "addUserReply");
	  	root.setAttribute("urId", String.valueOf(userReply.getId()));
	  	if(userReply.getType()!= null)
	  		root.setAttribute("queryType", String.valueOf(MessagingUtils.getQueryTypeNumber(userReply.getType())));	  	
	  	root.setAttribute("viewID", String.valueOf(userReply.getViewOrTableId()));
	  	root.setAttribute("result", result);	  	
	  	root.setAttribute("projectId", projectId);
	  	root.setAttribute("msgResponse", responseText);
	  	return root;
  }
  
  public synchronized Element saveQueryMsgPropsXML(String file, String result, QueryDobj query, String viewOrTableIdInDB){ 
	  	Element root = new Element(ROOT_ROOT);
	  	root.setAttribute("action", "saveQueryMsgProps");
	  	root.setAttribute("msgId", String.valueOf(query.getId()));
	  	root.setAttribute("queryID", String.valueOf(query.getId()));
	  	if(query.getType()!= null)
	  		root.setAttribute("queryType", String.valueOf(MessagingUtils.getQueryTypeNumber(query.getType())));	  	
	  	root.setAttribute("viewID", String.valueOf(query.getViewOrTableId()));
	  	root.setAttribute("dbID", String.valueOf(query.getDatabaseId()));
	  	root.setAttribute("viewOrTableIdInDB", viewOrTableIdInDB);	
	  	root.setAttribute("tableID", String.valueOf(query.getViewOrTableId()));
	  	root.setAttribute("result", result);	
	  	
	  	return root;
  }

  public synchronized Element saveUserReplyPropsXML(String file, String result, UserReply ur, String viewOrTableIdInDB, String parentName, String parentDesc){ 
	  	Element root = new Element(ROOT_ROOT);
	  	root.setAttribute("action", "saveUserReplyProps");
	  	root.setAttribute("urId", String.valueOf(ur.getId()));
	  	root.setAttribute("parentId", String.valueOf(ur.getParentId()));
	  	if(ur.getType()!= null)
	  		root.setAttribute("queryType", String.valueOf(MessagingUtils.getQueryTypeNumber(ur.getType())));	  	
	  	root.setAttribute("viewID", String.valueOf(ur.getViewOrTableId()));
	  	root.setAttribute("dbID", String.valueOf(ur.getDatabaseId()));
	  	root.setAttribute("viewOrTableIdInDB", viewOrTableIdInDB);
	  	root.setAttribute("parentName", parentName);	
	  	root.setAttribute("parentDesc", parentDesc);	
	  	root.setAttribute("result", result);	
	  	
	  	return root;
  }

  public synchronized Element saveQueryMsgDetailsXML(String file, String result, QueryDobj query){ 
	  	Element root = new Element(ROOT_ROOT);
	  	root.setAttribute("action", "saveQueryMsgDetails");
	  	root.setAttribute("msgId", String.valueOf(query.getId()));
	  	root.setAttribute("queryID", String.valueOf(query.getId()));
	  	root.setAttribute("queryTypeName", query.getType());	  	
	  	root.setAttribute("queryTypeValue", String.valueOf(MessagingUtils.getQueryTypeNumber(query.getType())));	  	
	  	root.setAttribute("result", result);	
	  	
	  	return root;
  }

  public synchronized Element saveUrMsgInfoXML(String file, String result, UserReply ur){ 
	  	Element root = new Element(ROOT_ROOT);
	  	root.setAttribute("action", "saveUrMsgInfo");
	  	root.setAttribute("urId", String.valueOf(ur.getId()));
	  	root.setAttribute("queryID", String.valueOf(ur.getId()));
	  	root.setAttribute("result", result);	
	  	
	  	return root;
  }
  
  public synchronized Element displayQueryProperties(String file, String dbName, QueryDobj msgInfo, String vtLable, 
		  String viewOrTableName, List<MdnEmailSetting> emailAddresses, List<MdnSmpp> allSmpp, String responseFormat, 
		  String defaultResponseFormat, String wsOperationName, int wsOperationID){

	  	Element root = new Element(ROOT_ROOT);
	  		  	
	  	root.setAttribute("msgId", String.valueOf(msgInfo.getId()));
	  	root.setAttribute("name", msgInfo.getName());
	  	if(msgInfo.getDescription() != null && !msgInfo.getDescription().equals(""))
	  		root.setAttribute("description", msgInfo.getDescription());	    
	  	
	  	//set some already Info in DB for this QueryMsg object
	  	root.setAttribute("timeout", msgInfo.getTimeout());
	  	
	  	if(msgInfo.getDatasourceStatus() == 1){//If Datasource is database
		  	root.setAttribute("vtLable", vtLable);	  	
	  		root.setAttribute("queryTypeName", msgInfo.getType());	  	
		  	root.setAttribute("queryTypeValue", String.valueOf(MessagingUtils.getQueryTypeNumber(msgInfo.getType())));	  	
	
		  	root.setAttribute("dbId", String.valueOf(msgInfo.getDatabaseId()));
		  	root.setAttribute("dbName", dbName);
		  	root.setAttribute("viewOrTableId", String.valueOf(msgInfo.getViewOrTableId()));
		  	root.setAttribute("viewOrTableName", viewOrTableName);
		  	root.setAttribute("tableId", String.valueOf(msgInfo.getViewOrTableId()));
		  	root.setAttribute("queryID", String.valueOf(msgInfo.getId()));
	  	}else{
	  		System.out.println(">>>>> wsOperationName = " + wsOperationName + " ,   wsOperationID:" + wsOperationID);
	  		root.setAttribute("wsOperationName", wsOperationName);
	  		root.setAttribute("wsOperationID", String.valueOf(wsOperationID));
	  	}
	  	
	  	int dsId = msgInfo.getDatasourceStatus();
	  	root.setAttribute("queryDSId", String.valueOf(dsId));
	  	if(dsId == 1)
	  		root.setAttribute("queryDSName", "Database");
	  	else
	  		root.setAttribute("queryDSName", "Web Service");
	  	
	  	root.setAttribute("action", "editProps");

	  	if(msgInfo.getId() != -1)
	  	{
		  	if(msgInfo.getEmailKeyword()!= null && !msgInfo.getEmailKeyword().equals(""))
		  		root.setAttribute("emailKeyword", msgInfo.getEmailKeyword());	    
		  	if(msgInfo.getSmsKeyword()!= null && !msgInfo.getSmsKeyword().equals(""))
		  		root.setAttribute("smsKeyword", msgInfo.getSmsKeyword());	    
		  	if(msgInfo.getImKeyword()!= null && !msgInfo.getImKeyword().equals(""))
		  		root.setAttribute("imKeyword", msgInfo.getImKeyword());	    
		  	
		  	root.setAttribute("mobileStatus", String.valueOf(msgInfo.getMobileStatus()));	    
		  	root.setAttribute("imStatus", String.valueOf(msgInfo.getIMStatus()));	
		  	root.setAttribute("emailId", String.valueOf(msgInfo.getEmailAddressId()));
		  	root.setAttribute("emailDisplayResult", String.valueOf(msgInfo.getEmailDisplayResult()));	    
		  	root.setAttribute("mobileDisplayResult", String.valueOf(msgInfo.getMobileDisplayResult()));
		  	root.setAttribute("imDisplayResult", String.valueOf(msgInfo.getImDisplayResult()));
		  	root.setAttribute("responseFormat", responseFormat);
		  	root.setAttribute("defaultResponseFormat", defaultResponseFormat);		  	
	  	
	  	}
	  	
	  	Element guiDef = this.getXMLLanguageFile(file);
	  	
        //Fill Email Addressesb combobox -->>
        String emailTagName = "emailAddress";
  		Element addNewNode = new Element(emailTagName);
  		addNewNode.setAttribute("name", getGuiValueForElement(guiDef, "lbl-add-new-item"));
  		addNewNode.setAttribute("id", "0");	  		
  		root.addContent(addNewNode);

  		Element noEmailNode = new Element(emailTagName);
  		noEmailNode.setAttribute("name", getGuiValueForElement(guiDef, "lbl-no-email"));
  		noEmailNode.setAttribute("id", "-1");	  		
  		root.addContent(noEmailNode);

	  	for (MdnEmailSetting emailAddress : emailAddresses) {
	  		if(emailAddress.getDelStatus()== Constants.MARKED_AS_NOT_DELETED_INT){	  		
		  		Element nodeEmail = new Element("emailAddress");
		  		nodeEmail.setAttribute("name", emailAddress.getEmailAddress());
		  		nodeEmail.setAttribute("id", Integer.toString(emailAddress.getId()));	  		
		  		root.addContent(nodeEmail);
	  		}
	  	}
	  	//-------------------------------------

	  	//Fill Mobile Combobox --------->>
	  	String mobileTagName = "smpp";
  		Element addNewMobNode = new Element(mobileTagName);
  		addNewMobNode.setAttribute("number", getGuiValueForElement(guiDef, "lbl-add-new-item"));
  		addNewMobNode.setAttribute("id", "0");	  		
  		root.addContent(addNewMobNode);

  		Element noSmsNode = new Element(mobileTagName);
  		noSmsNode.setAttribute("number", getGuiValueForElement(guiDef, "lbl-no-sms"));
  		noSmsNode.setAttribute("id", "-1");	  		
  		root.addContent(noSmsNode);	  	
	  	
	  	
  		Element nodeGsm = new Element("smpp");
  		nodeGsm.setAttribute("number", "GSM Modem Connection");
  		nodeGsm.setAttribute("id", Integer.toString(1));	  		
  		root.addContent(nodeGsm);	  		  	
	  	for (MdnSmpp smpp : allSmpp) {
	  		if(smpp.getDelStatus()== Constants.MARKED_AS_NOT_DELETED_INT){
		  		Element nodeSmpp = new Element("smpp");
		  		nodeSmpp.setAttribute("number", "SMPP " + smpp.getNumber());
		  		nodeSmpp.setAttribute("id", Integer.toString(smpp.getId()));	  		
		  		root.addContent(nodeSmpp);
	  		}
		}	  	
	  	//-------------------------------------
	  	
	  	return root;
  }
  
  public synchronized Element displayQueryBuilderXML(String file, QueryDobj msgInfo){

	  	Element root = new Element(ROOT_ROOT);
	  		  	
	  	root.setAttribute("msgId", String.valueOf(msgInfo.getId()));
	  	root.setAttribute("queryID", String.valueOf(msgInfo.getId()));
	  	root.setAttribute("viewOrTableId", String.valueOf(msgInfo.getViewOrTableId()));
	  	root.setAttribute("dbId", String.valueOf(msgInfo.getDatabaseId()));	  
	  	
	  	return root;
	  	
  }  

  public synchronized Element getDefaultTextMsgForResponseXML(String defaultResponseFormat){
	  	Element root = new Element(ROOT_ROOT);
	  	root.setAttribute("responseFormat", defaultResponseFormat);
	  	return root;
  }  

  public synchronized Element displayMessageSeparatorXML(String file, String separator){
	  	Element root = new Element(ROOT_ROOT);

	  	String seperatorNum = MessagingUtils.getSeperatorNum(separator);
	  	root.setAttribute("separator", seperatorNum);
	  	if(seperatorNum.equals("3"))
	  		root.setAttribute("other", separator);

	  	return root;
  }
  
  public synchronized Element displayMessagingInfoXML(String file, QueryDobj msgInfo, List<MdnEmailSetting> emailAddresses, String responseFormat){

	  	Element root = new Element(ROOT_ROOT);

	  	root.setAttribute("msgId", String.valueOf(msgInfo.getId()));
	  	
	  	if(msgInfo.getDatasourceStatus() == 1){
		  	root.setAttribute("queryID", String.valueOf(msgInfo.getId()));
		  	root.setAttribute("viewOrTableId", String.valueOf(msgInfo.getViewOrTableId()));
		  	root.setAttribute("dbId", String.valueOf(msgInfo.getDatabaseId()));	  
		  	root.setAttribute("queryTypeName", msgInfo.getType());
	  	}
	  	
	  	if(msgInfo.getId() != -1)
	  	{
		  	if(msgInfo.getEmailKeyword()!= null && !msgInfo.getEmailKeyword().equals(""))
		  		root.setAttribute("emailKeyword", msgInfo.getEmailKeyword());	    
		  	if(msgInfo.getSmsKeyword()!= null && !msgInfo.getSmsKeyword().equals(""))
		  		root.setAttribute("smsKeyword", msgInfo.getSmsKeyword());	    
		  	if(msgInfo.getImKeyword()!= null && !msgInfo.getImKeyword().equals(""))
		  		root.setAttribute("imKeyword", msgInfo.getImKeyword());	    
		  	
		  	root.setAttribute("mobileStatus", String.valueOf(msgInfo.getMobileStatus()));	    
		  	root.setAttribute("imStatus", String.valueOf(msgInfo.getIMStatus()));	
		  	root.setAttribute("emailId", String.valueOf(msgInfo.getEmailAddressId()));
		  	root.setAttribute("emailDisplayResult", String.valueOf(msgInfo.getEmailDisplayResult()));	    
		  	root.setAttribute("mobileDisplayResult", String.valueOf(msgInfo.getMobileDisplayResult()));
		  	root.setAttribute("imDisplayResult", String.valueOf(msgInfo.getImDisplayResult()));
		  	root.setAttribute("responseFormat", responseFormat);
	  	
	  	}
	  	for (MdnEmailSetting emailAddress : emailAddresses) {
	  		if(emailAddress.getDelStatus()== Constants.MARKED_AS_NOT_DELETED_INT){	  		
		  		Element nodeEmail = new Element("emailAddress");
		  		nodeEmail.setAttribute("name", emailAddress.getEmailAddress());
		  		nodeEmail.setAttribute("id", Integer.toString(emailAddress.getId()));	  		
		  		root.addContent(nodeEmail);
	  		}
	  	}
	  	
	  	return root;
  }  

  public synchronized Element displayUrMessagingInfoXML(String file, UserReply ur, String responseFormat){

	  	Element root = new Element(ROOT_ROOT);

	  	root.setAttribute("urId", String.valueOf(ur.getId()));
	  	root.setAttribute("queryID", String.valueOf(ur.getId()));
	  	root.setAttribute("viewOrTableId", String.valueOf(ur.getViewOrTableId()));
	  	root.setAttribute("dbId", String.valueOf(ur.getDatabaseId()));	  
	  	root.setAttribute("queryTypeName", ur.getType());
	  	
	  	if(ur.getId() != -1)
		  	root.setAttribute("responseFormat", responseFormat);
	  	
	  	root.setAttribute("displayResult", String.valueOf(ur.getDisplayResult()));		  	

	  	return root;
	}  

  public synchronized Element testMessaginResultXML(String file, QueryDobj msgInfo, String replyMessaing, String result, List<MdnEmailSetting> emailAddresses, List<MdnSmpp> allSmpp , String responseFormat){
	  
	  Element guiDef = this.getXMLLanguageFile(file);
	  
	  Element root = new Element(ROOT_ROOT);

//	  	root.setAttribute("action", "testMessaginResult");
//	  	root.setAttribute("msgId", String.valueOf(msgInfo.getId()));
//	  	root.setAttribute("queryID", String.valueOf(msgInfo.getId()));
//	  	root.setAttribute("queryTypeName", msgInfo.getType());
//	  	root.setAttribute("queryType", msgInfo.getType());
	  	root.setAttribute("replyMessaing", replyMessaing);
/*	  	root.setAttribute("responseFormat", responseFormat);
	  	root.setAttribute("result", result);
	
	  	if(msgInfo.getId() != -1){
		  	root.setAttribute("viewOrTableId", String.valueOf(msgInfo.getViewOrTableId()));
		  	root.setAttribute("dbId", String.valueOf(msgInfo.getDatabaseId()));	  
		  	
		  	if(msgInfo.getEmailKeyword()!= null && !msgInfo.getEmailKeyword().equals(""))
		  		root.setAttribute("emailKeyword", msgInfo.getEmailKeyword());	    
		  	if(msgInfo.getSmsKeyword()!= null && !msgInfo.getSmsKeyword().equals(""))
		  		root.setAttribute("smsKeyword", msgInfo.getSmsKeyword());	    
		  	if(msgInfo.getImKeyword()!= null && !msgInfo.getImKeyword().equals(""))
		  		root.setAttribute("imKeyword", msgInfo.getImKeyword());	    
		  	
		  	root.setAttribute("mobileStatus", String.valueOf(msgInfo.getMobileStatus()));	    
		  	root.setAttribute("imStatus", String.valueOf(msgInfo.getIMStatus()));	 
		  	root.setAttribute("emailId", String.valueOf(msgInfo.getEmailAddressId()));
		  	root.setAttribute("emailDisplayResult", String.valueOf(msgInfo.getEmailDisplayResult()));	    
		  	root.setAttribute("mobileDisplayResult", String.valueOf(msgInfo.getMobileDisplayResult()));
		  	root.setAttribute("imDisplayResult", String.valueOf(msgInfo.getImDisplayResult()));
		  	//root.setAttribute("responseFormat", responseFormat);
		  	
		  	String seperator = msgInfo.getConditionSeperator();
		  	String seperatorNum = MessagingUtils.getSeperatorNum(seperator);
		  	root.setAttribute("seperator", seperatorNum);
		  	if(seperatorNum.equals("3"))
		  		root.setAttribute("other", seperator);
		  	
	  	}
*/	
	    //Fill Email Addresses combobox -->>
	    String emailTagName = "emailAddress";
		Element addNewNode = new Element(emailTagName);
		addNewNode.setAttribute("name", getGuiValueForElement(guiDef, "lbl-add-new-item"));
		addNewNode.setAttribute("id", "0");	  		
		root.addContent(addNewNode);
	
		Element noEmailNode = new Element(emailTagName);
		noEmailNode.setAttribute("name", getGuiValueForElement(guiDef, "lbl-no-email"));
		noEmailNode.setAttribute("id", "-1");	  		
		root.addContent(noEmailNode);
	  	for (MdnEmailSetting emailAddress : emailAddresses) {
	  		if(emailAddress.getDelStatus()== Constants.MARKED_AS_NOT_DELETED_INT){
		  		Element nodeEmail = new Element("emailAddress");
		  		nodeEmail.setAttribute("name", emailAddress.getEmailAddress());
		  		nodeEmail.setAttribute("id", Integer.toString(emailAddress.getId()));	  		
		  		root.addContent(nodeEmail);
	  		}
		}
	  	//---------------------------------------
	  	
	  	//Fill Mobile combo
	  	String mobileTagName = "smpp";
		Element addNewMobNode = new Element(mobileTagName);
		addNewMobNode.setAttribute("number", getGuiValueForElement(guiDef, "lbl-add-new-item"));
		addNewMobNode.setAttribute("id", "0");	  		
		root.addContent(addNewMobNode);
	
		Element noSmsNode = new Element(mobileTagName);
		noSmsNode.setAttribute("number", getGuiValueForElement(guiDef, "lbl-no-sms"));
		noSmsNode.setAttribute("id", "-1");	  		
		root.addContent(noSmsNode);	  	
	  	
		Element nodeGsm = new Element(mobileTagName);
		nodeGsm.setAttribute("number", "GSM Modem Connection");
		nodeGsm.setAttribute("id", Integer.toString(1));	  		
		root.addContent(nodeGsm);	  		  	
	  	for (MdnSmpp smpp : allSmpp) {
	  		if(smpp.getDelStatus()== Constants.MARKED_AS_NOT_DELETED_INT){
		  		Element nodeSmpp = new Element(mobileTagName);
		  		nodeSmpp.setAttribute("number", "SMPP " + smpp.getNumber());
		  		nodeSmpp.setAttribute("id", Integer.toString(smpp.getId()));	  		
		  		root.addContent(nodeSmpp);
	  		}
		}	  	
	  	//-------------------------------------------
	  	
	  	return root;
  }  

  public synchronized Element testURMessaginResultXML(String file, UserReply userReply, String replyMessaing, String result, String responseFormat){

	  	Element root = new Element(ROOT_ROOT);

	  	root.setAttribute("action", "testURMessaginResult");
	  	root.setAttribute("urId", String.valueOf(userReply.getId()));
	  	root.setAttribute("queryID", String.valueOf(userReply.getId()));
	  	root.setAttribute("replyMessaing", replyMessaing);
	  	root.setAttribute("responseFormat", responseFormat);
	  	root.setAttribute("result", result);

	  	if(userReply.getId() != -1){
		  	root.setAttribute("viewOrTableId", String.valueOf(userReply.getViewOrTableId()));
		  	root.setAttribute("dbId", String.valueOf(userReply.getDatabaseId()));	
	  	}
		  	
	  	return root;
}  

  
  public synchronized Element displayUrPropsResult(String file, String dbName, UserReply userReply, QueryDobj query, UserReply urParent, String vtLable, String viewOrTableName, String responseFormat, String defaultResponseFormat){
	  	Element root = new Element(ROOT_ROOT);
	  		  	
	  	int pId = userReply.getParentId();
	  	root.setAttribute("urId", String.valueOf(userReply.getId()));
	  	if(query != null){
		  	root.setAttribute("parentName", query.getName());
		  	root.setAttribute("parentId", String.valueOf(userReply.getParentId()));
		  	if(query.getDescription()!= null && !query.getDescription().equals(""))
		  		root.setAttribute("parentDesc", query.getDescription());
	  	}else{
	  		if(urParent != null && urParent.getMsgText() != null)
	  			root.setAttribute("parentName", urParent.getMsgText());
	  		else
	  			root.setAttribute("parentName", "-");

	  		root.setAttribute("parentDesc", "-");	  		
	  	}
	  	if(userReply.getMsgText() != null && !(userReply.getMsgText()).equals(""))
	  		root.setAttribute("urMsgTxt", userReply.getMsgText());
	  	if(userReply.getDescription() != null && !(userReply.getDescription()).equals(""))
	  		root.setAttribute("urDescTxt", userReply.getDescription());
	  	if(userReply.getTimeout() != null && !(userReply.getTimeout()).equals(""))
	  		root.setAttribute("timeout", userReply.getTimeout());
	  	
	  	if(userReply.getDatasourceStatus() == 1){ 
		  	root.setAttribute("vtLable", vtLable);	  	
	  		// set some already Info in DB for this QueryMsg object
		  	root.setAttribute("queryTypeName", userReply.getType());	  	
		  	root.setAttribute("queryTypeValue", String.valueOf(MessagingUtils.getQueryTypeNumber(userReply.getType())));

		  	root.setAttribute("dbId", String.valueOf(userReply.getDatabaseId()));	  	
		  	root.setAttribute("dbName", dbName);
		  	root.setAttribute("viewOrTableId", String.valueOf(userReply.getViewOrTableId()));
		  	root.setAttribute("viewOrTableName", viewOrTableName);
		  	root.setAttribute("queryTypeInDB", String.valueOf(MessagingUtils.getQueryTypeNumber(userReply.getType())));
		  	root.setAttribute("tableId", String.valueOf(userReply.getViewOrTableId()));
	  	}

	  	
	  	root.setAttribute("queryID", String.valueOf(userReply.getId()));
	  	root.setAttribute("queryTypeName", userReply.getType());
	  	
	  	if(userReply.getId() != -1)
		  	root.setAttribute("responseFormat", responseFormat);
	  	
	  	root.setAttribute("displayResult", String.valueOf(userReply.getDisplayResult()));
	  	root.setAttribute("defaultResponseFormat", defaultResponseFormat);	
	  	
	  	return root;
}
  
  public synchronized Element displayMsgInfoResult(String file, String dbName, QueryDobj msgInfo, List<DataSourceDobj> databases, List tablesOrViewsList, String vtLable){
	  	Element root = new Element(ROOT_ROOT);
	  	root.setAttribute("vtLable", vtLable);	  	
	  		  	
	  	root.setAttribute("msgId", String.valueOf(msgInfo.getId()));
	  	root.setAttribute("queryTypeName", msgInfo.getType());	  	
	  	root.setAttribute("queryTypeValue", String.valueOf(MessagingUtils.getQueryTypeNumber(msgInfo.getType())));	  	
	  	root.setAttribute("name", msgInfo.getName());
	  	if(msgInfo.getDescription() != null && !msgInfo.getDescription().equals(""))
	  		root.setAttribute("description", msgInfo.getDescription());	    
	  	//root.setAttribute("dsType", String.valueOf(msgInfo.getDataSourceType()));
	  	
	  	//set some already Info in DB for this QueryMsg object
	  	root.setAttribute("dbId", String.valueOf(msgInfo.getDatabaseId()));	  	
	  	root.setAttribute("dbName", dbName);
	  	root.setAttribute("viewOrTableId", String.valueOf(msgInfo.getViewOrTableId()));
	  	root.setAttribute("queryTypeInDB", String.valueOf(MessagingUtils.getQueryTypeNumber(msgInfo.getType())));
	  	root.setAttribute("tableId", String.valueOf(msgInfo.getViewOrTableId()));	    
	  	root.setAttribute("emailId", String.valueOf(msgInfo.getEmailAddressId()));

	  	if(msgInfo.getEmailKeyword()!= null && !msgInfo.getEmailKeyword().equals(""))
	  		root.setAttribute("emailKeyword", msgInfo.getEmailKeyword());	    
	  	if(msgInfo.getSmsKeyword()!= null && !msgInfo.getSmsKeyword().equals(""))
	  		root.setAttribute("smsKeyword", msgInfo.getSmsKeyword());	    
	  	if(msgInfo.getImKeyword()!= null && !msgInfo.getImKeyword().equals(""))
	  		root.setAttribute("imKeyword", msgInfo.getImKeyword());	    
	  	
	  	root.setAttribute("mobileStatus", String.valueOf(msgInfo.getMobileStatus()));	    
	  	root.setAttribute("imStatus", String.valueOf(msgInfo.getIMStatus()));	    
	  	root.setAttribute("emailDisplayResult", String.valueOf(msgInfo.getEmailDisplayResult()));	    
	  	root.setAttribute("mobileDisplayResult", String.valueOf(msgInfo.getMobileDisplayResult()));
	  	root.setAttribute("imDisplayResult", String.valueOf(msgInfo.getImDisplayResult()));
	  	
	  	String seperator = msgInfo.getConditionSeperator();
	  	String seperatorNum = MessagingUtils.getSeperatorNum(seperator);
	  	root.setAttribute("seperator", seperatorNum);
	  	if(seperatorNum.equals("3"))
	  		root.setAttribute("other", seperator);
	  	
	  	for (DataSourceDobj db : databases) {
	  		Element nodeDb = new Element("db");
	  		nodeDb.setAttribute("name", db.getName());
	  		nodeDb.setAttribute("id", Integer.toString(db.getId()));	  		
	  		root.addContent(nodeDb);
		}

  		if(vtLable.equalsIgnoreCase(Constants.VIEWS_LABEL)){//If list objects be instanceof DataView
  			for (int i = 0; i<tablesOrViewsList.size(); i++) {
  				DataView view = (DataView) tablesOrViewsList.get(i);
  				Element nodeViews = new Element("query");
  				nodeViews.setAttribute("name", view.getName());
  				nodeViews.setAttribute("id", Integer.toString(view.getId()));	  		
  				root.addContent(nodeViews);
  			}
		}else if(vtLable.equalsIgnoreCase(Constants.TABLE_LABEL)){//if list objects be instanceof EntityDobj
  			for (int i = 0; i<tablesOrViewsList.size(); i++) {
  				EntityDobj table = (EntityDobj) tablesOrViewsList.get(i);
  				Element nodeViews = new Element("query");
  				nodeViews.setAttribute("name", table.getName());
  				nodeViews.setAttribute("id", Integer.toString(table.getId()));	  		
  				root.addContent(nodeViews);
  			}
		}
	  	
	  	return root;
  }

  public synchronized Element fillCombo(String file, QueryDobj msgInfo, List<DataSourceDobj> databases, List tablesOrViewsList, String vtLable, List<MdnEmailSetting> emailAddresses, List<MdnSmpp> allSmpp){
	  	Element guiDef = this.getXMLLanguageFile(file);
	  
	  	Element root = new Element(ROOT_ROOT);
	  		  	
	  	root.setAttribute("vtLable", vtLable);

	  	root.setAttribute("queryTypeName", msgInfo.getType());	  	
	  	root.setAttribute("queryTypeValue", String.valueOf(MessagingUtils.getQueryTypeNumber(msgInfo.getType())));	  	
	  	
	  	root.setAttribute("msgId", String.valueOf(msgInfo.getId()));
	  	if(!msgInfo.getName().equals(""))
	  		root.setAttribute("name", msgInfo.getName());
	  	if(!msgInfo.getDescription().equals(""))
	  		root.setAttribute("description", msgInfo.getDescription());
	  	if(!msgInfo.getTimeout().equals(""))
	  		root.setAttribute("timeout", msgInfo.getTimeout());
	  	if(msgInfo.getEmailDisplayResult() != -1)
	  		root.setAttribute("emailDisplayResult", String.valueOf(msgInfo.getEmailDisplayResult()));
	  	if(msgInfo.getMobileDisplayResult() != -1)
	  		root.setAttribute("mobileDisplayResult", String.valueOf(msgInfo.getMobileDisplayResult()));
	  	if(msgInfo.getImDisplayResult() != -1)
	  		root.setAttribute("imDisplayResult", String.valueOf(msgInfo.getImDisplayResult()));
	  	
	  	if(msgInfo.getEmailKeyword() != null && !msgInfo.getEmailKeyword().equals(""))
	  		root.setAttribute("emailKeyword", msgInfo.getEmailKeyword());
	  	if(msgInfo.getSmsKeyword() != null && !msgInfo.getSmsKeyword().equals(""))
	  		root.setAttribute("smsKeyword", msgInfo.getSmsKeyword());
	  	if(msgInfo.getImKeyword() != null && !msgInfo.getImKeyword().equals(""))
	  		root.setAttribute("imKeyword", msgInfo.getImKeyword());
	  	
	  	String emailTagName = "emailAddress";
  		Element addNewNode = new Element(emailTagName);
  		addNewNode.setAttribute("name", getGuiValueForElement(guiDef, "lbl-add-new-item"));
  		addNewNode.setAttribute("id", "0");	  		
  		root.addContent(addNewNode);

  		Element noEmailNode = new Element(emailTagName);
  		noEmailNode.setAttribute("name", getGuiValueForElement(guiDef, "lbl-no-email"));
  		noEmailNode.setAttribute("id", "-1");	  		
  		root.addContent(noEmailNode);
	  	for (MdnEmailSetting emailAddress : emailAddresses) {
	  		Element nodeEmail = new Element(emailTagName);
	  		nodeEmail.setAttribute("name", emailAddress.getEmailAddress());
	  		nodeEmail.setAttribute("id", Integer.toString(emailAddress.getId()));	  		
	  		root.addContent(nodeEmail);
		}
	  	
	  	for (DataSourceDobj db : databases) {
	  		Element nodeDb = new Element("db");
	  		nodeDb.setAttribute("name", db.getName());
	  		nodeDb.setAttribute("id", Integer.toString(db.getId()));	  		
	  		root.addContent(nodeDb);
		}

  		if(vtLable.equalsIgnoreCase(Constants.VIEWS_LABEL)){//If list objects be instanceof DataView
  			for (int i = 0; i<tablesOrViewsList.size(); i++) {
  				DataView view = (DataView) tablesOrViewsList.get(i);
  				Element nodeViews = new Element("query");
  				nodeViews.setAttribute("name", view.getName());
  				nodeViews.setAttribute("id", Integer.toString(view.getId()));	  		
  				root.addContent(nodeViews);
  			}
		}else if(vtLable.equalsIgnoreCase(Constants.TABLE_LABEL)){//if list objects be instanceof EntityDobj
  			for (int i = 0; i<tablesOrViewsList.size(); i++) {
  				EntityDobj table = (EntityDobj) tablesOrViewsList.get(i);
  				Element nodeViews = new Element("query");
  				nodeViews.setAttribute("name", table.getName());
  				nodeViews.setAttribute("id", Integer.toString(table.getId()));	  		
  				root.addContent(nodeViews);
  			}
		}
  		
	  	String mobileTagName = "smpp";
  		Element addNewMobNode = new Element(mobileTagName);
  		addNewMobNode.setAttribute("number", getGuiValueForElement(guiDef, "lbl-add-new-item"));
  		addNewMobNode.setAttribute("id", "0");	  		
  		root.addContent(addNewMobNode);

  		Element noSmsNode = new Element(mobileTagName);
  		noSmsNode.setAttribute("number", getGuiValueForElement(guiDef, "lbl-no-sms"));
  		noSmsNode.setAttribute("id", "-1");	  		
  		root.addContent(noSmsNode);	  	
  		
  		Element nodeGsm = new Element(mobileTagName);
  		nodeGsm.setAttribute("number", "GSM Modem Connection");
  		nodeGsm.setAttribute("id", Integer.toString(1));	  		
  		root.addContent(nodeGsm);	  	  		
	  	for (MdnSmpp smpp : allSmpp) {
	  		if(smpp.getDelStatus()== Constants.MARKED_AS_NOT_DELETED_INT){
		  		Element nodeEmail = new Element(mobileTagName);
		  		nodeEmail.setAttribute("number", "SMPP " + smpp.getNumber());
		  		nodeEmail.setAttribute("id", Integer.toString(smpp.getId()));	  		
		  		root.addContent(nodeEmail);
	  		}
		}	  		  	
	  	return root;
}

  public synchronized Element changeCombo(String file, UserReply userReplyMessage, List<DataSourceDobj> databases, List tablesOrViewsList, String vtLable, String queryId, String queryName, String queryDesc, String parentId){
	  	Element root = new Element(ROOT_ROOT);
	  		  	
	  	root.setAttribute("vtLable", vtLable);

	  	root.setAttribute("queryTypeName", userReplyMessage.getType());	  	
	  	root.setAttribute("queryTypeValue", String.valueOf(MessagingUtils.getQueryTypeNumber(userReplyMessage.getType())));	  	
	  	
	  	root.setAttribute("userReplyId", String.valueOf(userReplyMessage.getId()));

	  	root.setAttribute("queryId", queryId);
	  	root.setAttribute("queryName", queryName);
	  	root.setAttribute("queryDesc", queryDesc);
	  	
	  	if(!userReplyMessage.getMsgText().equals(""))
	  		root.setAttribute("textMsgUr", userReplyMessage.getMsgText());
	  	if(!userReplyMessage.getTimeout().equals(""))
	  		root.setAttribute("timeoutUr", userReplyMessage.getTimeout());
	  	
	  	for (DataSourceDobj db : databases) {
	  		Element nodeDb = new Element("db");
	  		nodeDb.setAttribute("name", db.getName());
	  		nodeDb.setAttribute("id", Integer.toString(db.getId()));	  		
	  		root.addContent(nodeDb);
		}

		if(vtLable.equalsIgnoreCase(Constants.VIEWS_LABEL)){//If list objects be instanceof DataView
			for (int i = 0; i<tablesOrViewsList.size(); i++) {
				DataView view = (DataView) tablesOrViewsList.get(i);
				Element nodeViews = new Element("query");
				nodeViews.setAttribute("name", view.getName());
				nodeViews.setAttribute("id", Integer.toString(view.getId()));	  		
				root.addContent(nodeViews);
			}
		}else if(vtLable.equalsIgnoreCase(Constants.TABLE_LABEL)){//if list objects be instanceof EntityDobj
			for (int i = 0; i<tablesOrViewsList.size(); i++) {
				EntityDobj table = (EntityDobj) tablesOrViewsList.get(i);
				Element nodeViews = new Element("query");
				nodeViews.setAttribute("name", table.getName());
				nodeViews.setAttribute("id", Integer.toString(table.getId()));	  		
				root.addContent(nodeViews);
			}
		}
	  	
	  	return root;
  }
  
  public synchronized Element getMsgControls(List<BlockContacts> blockContacts, MessagingSettingDetails imPublicMsgInfo, 
		  MessagingSettingDetails emailPublicMsgInfo, MessagingSettingDetails smsPublicMsgInfo, MessagingSettingDetails smppPublicMsgInfo,
		  boolean limitation, TemporaryBlockContacInfo msgControlsInfo, int availablePublicMessages){ 
	  	Element root = new Element(ROOT_ROOT);
  	
	  	root.setAttribute("emailNumLeft", String.valueOf(availablePublicMessages - emailPublicMsgInfo.getTotalMsgCount()));
	  	root.setAttribute("imNumLeft", String.valueOf(availablePublicMessages - imPublicMsgInfo.getTotalMsgCount()));
	  	root.setAttribute("smsNumLeft", String.valueOf(availablePublicMessages - smsPublicMsgInfo.getTotalMsgCount()));
	  	root.setAttribute("smppNumLeft", String.valueOf(availablePublicMessages - smppPublicMsgInfo.getTotalMsgCount()));	  		  	
	  	
	  	if(limitation == true || availablePublicMessages == -1)
	  		root.setAttribute("unlimited", String.valueOf(true));
	  	else
	  		root.setAttribute("unlimited", String.valueOf(false));
	  	
	  	root.setAttribute("emailNum", String.valueOf(emailPublicMsgInfo.getTotalMsgCount()));
	  	root.setAttribute("imNum", String.valueOf(imPublicMsgInfo.getTotalMsgCount()));
	  	root.setAttribute("smsNum", String.valueOf(smsPublicMsgInfo.getTotalMsgCount()));
	  	root.setAttribute("smppNum", String.valueOf(smppPublicMsgInfo.getTotalMsgCount()));
	  	
	  	if(msgControlsInfo != null){
		  	root.setAttribute("maxMsg", String.valueOf(msgControlsInfo.getMaxMessage())); 
		  	root.setAttribute("period", msgControlsInfo.getMaxPeriod());
		  	root.setAttribute("cancel", msgControlsInfo.getCancelPeriod());
		  	root.setAttribute("reply", msgControlsInfo.getReply());
	  	}
	  	
	  	for (BlockContacts bc : blockContacts) {
	  		Element nodeBc = new Element("block");
	  		String typeId = bc.getType();
	  		String typeName = "";
	  		if(typeId.equals("1"))
	  			typeName = "Email";
	  		else if(typeId.equals("2"))
	  			typeName = "IM";
	  		else if(typeId.equals("3"))
	  			typeName = "SMS";
	  		
	  		nodeBc.setAttribute("type", typeName);
	  		nodeBc.setAttribute("contact", bc.getContact());	 
	  		nodeBc.setAttribute("cbxName", bc.getContact()+"(" + typeName+ ")");
	  		nodeBc.setAttribute("id", String.valueOf(bc.getId()));
	  		root.addContent(nodeBc);
		}
	  	return root;
  }

  public synchronized Element getSearchTerm(String searchTerm){ 
	  	Element root = new Element(ROOT_ROOT);
	
	  	root.setAttribute("term", searchTerm);
	  	
	  	return root;
  }
  public synchronized Element displayEmailSett(String action, MdnEmailSetting emailSett, String result, boolean isSuccess){
	  	Element root = new Element(ROOT_ROOT);
	  	root.setAttribute("action", action);
	  	if(emailSett != null && isSuccess){//result.equalsIgnoreCase("OK")) {
	  		root.setAttribute("emailId", String.valueOf(emailSett.getId()));
	  		
	  		root.setAttribute("imapEncType", String.valueOf(emailSett.getImapEncryptedType()));
	  		root.setAttribute("address", emailSett.getEmailAddress() == null ? "" : emailSett.getEmailAddress());
	  		root.setAttribute("imapHost", emailSett.getImapHost());
	  		root.setAttribute("imapUserName", emailSett.getImapUserName());
	  		root.setAttribute("imapPassword", emailSett.getImapPassword());
	  		root.setAttribute("imapPort", emailSett.getImapPort() == null || emailSett.getImapPort().equals("")? "143" : emailSett.getImapPort());	  		
	  		
	  		root.setAttribute("smtpEncType", String.valueOf((emailSett.getSmtpEncryptedType())));	  		
	  		root.setAttribute("smtpHost", emailSett.getSmtpHost());
	  		root.setAttribute("smtpUsername", emailSett.getSmtpUsername() == null ? "" : emailSett.getSmtpUsername());
	  		root.setAttribute("smtpPassword", emailSett.getSmtpPassword() == null ? "" : emailSett.getSmtpPassword());
	  		root.setAttribute("smtpPort", emailSett.getSmtpPort() == null || emailSett.getSmtpPort().equals("")? "25" : emailSett.getSmtpPort());
	  	}
	  	root.setAttribute("isSuccess", String.valueOf(isSuccess));
	  	root.setAttribute("result", result);
	  	return root;
  }
  private String booleanConvertor(int status){
	  if(status == 1)
		  return "true";
	  else
		  return "false";
  }
  
  public Element getQueryRecordSet(RecordSet rs){
	  	Element root = new Element(ROOT_ROOT);
	  	if(rs!= null && rs.size()>1)
	  		root.setAttribute("test", "YES");
	  	else
	  		root.setAttribute("test", "NO");
	  	return root;
  } 
  
  public Element getCompsForUpdateQuery1(List<CustomField> customFields){
	  	Element root = new Element("comps");
	  	
	  	Element descriptionNode = new Element("group-explain");	
	  	descriptionNode.setAttribute("first", "Please update");
	  	descriptionNode.setAttribute("second", "of the following fields");
	  	root.addContent(descriptionNode);
	  	
	  	Element groupNode1 = new Element("group");
	  	groupNode1.setAttribute("value", "1");
	  	groupNode1.setAttribute("name", "all");
	  	root.addContent(groupNode1);
	  	
//	  	Element groupNode2 = new Element("group");
//	  	groupNode2.setAttribute("value", "2");
//	  	groupNode2.setAttribute("name", "any");
//	  	root.addContent(groupNode2);
//	  	
//	  	Element groupNode3 = new Element("group");
//	  	groupNode3.setAttribute("value", "3");
//	  	groupNode3.setAttribute("name", "none");
//	  	root.addContent(groupNode3);	  	
//	  	
//	  	Element groupNode4 = new Element("group");
//	  	groupNode4.setAttribute("value", "4");
//	  	groupNode4.setAttribute("name", "not all");
//	  	root.addContent(groupNode4);	
	  	
	  	Element actionNode1 = new Element("action");
	  	actionNode1.setAttribute("value", "1");
	  	actionNode1.setAttribute("name", "Add Condition (after current)");
	  	root.addContent(actionNode1);	  	
	  	
	  	Element actionNode2 = new Element("action");
	  	actionNode2.setAttribute("value", "2");
	  	actionNode2.setAttribute("name", "Add Bracket (after current)");
	  	root.addContent(actionNode2);		  	
	  	
	  	Element actionNode3 = new Element("action");
	  	actionNode3.setAttribute("value", "3");
	  	actionNode3.setAttribute("name", "Delete Current Row");
	  	root.addContent(actionNode3);	
	  	
	  	Element actionNode4 = new Element("action");
	  	actionNode4.setAttribute("value", "4");
	  	actionNode4.setAttribute("name", "Enable/Disable Row");
	  	root.addContent(actionNode4);	
	  	
	  	Element compNode1 = new Element("comp");
	  	compNode1.setAttribute("value", "1");
	  	compNode1.setAttribute("name", "is equal to");
	  	root.addContent(compNode1);	  	
	  	
	  	Element compNode2 = new Element("comp");
	  	compNode2.setAttribute("value", "2");
	  	compNode2.setAttribute("name", "is not equal to");
	  	root.addContent(compNode2);	 
	  	
	  	Element compNode3 = new Element("comp");
	  	compNode3.setAttribute("value", "3");
	  	compNode3.setAttribute("name", "is less than");
	  	root.addContent(compNode3);	
	  	
	  	Element compNode4 = new Element("comp");
	  	compNode4.setAttribute("value", "4");
	  	compNode4.setAttribute("name", "is less than or equal to");
	  	root.addContent(compNode4);	 
	  	
	  	Element compNode5 = new Element("comp");
	  	compNode5.setAttribute("value", "5");
	  	compNode5.setAttribute("name", "is greater than");
	  	root.addContent(compNode5);
	  	
	  	Element compNode6 = new Element("comp");
	  	compNode6.setAttribute("value", "6");
	  	compNode6.setAttribute("name", "is greater than or equal to");
	  	root.addContent(compNode6);
	  	
	  	Element compNode7 = new Element("comp");
	  	compNode7.setAttribute("value", "7");
	  	compNode7.setAttribute("name", "is null");
	  	root.addContent(compNode7);
	  	
	  	Element compNode8 = new Element("comp");
	  	compNode8.setAttribute("value", "8");
	  	compNode8.setAttribute("name", "is in list");
	  	root.addContent(compNode8);

	  	Element compNode9 = new Element("comp");
	  	compNode9.setAttribute("value", "9");
	  	compNode9.setAttribute("name", "is not in list");
	  	root.addContent(compNode9);
	  	
	  	Element compNode10 = new Element("comp");
	  	compNode10.setAttribute("value", "10");
	  	compNode10.setAttribute("name", "starts with");
	  	root.addContent(compNode10);	  	
	  	
	  	Element compNode11 = new Element("comp");
	  	compNode11.setAttribute("value", "11");
	  	compNode11.setAttribute("name", "does not start with");
	  	root.addContent(compNode11);	  		  	
	  	
	  	Element compNode12 = new Element("comp");
	  	compNode12.setAttribute("value", "12");
	  	compNode12.setAttribute("name", "contains");
	  	root.addContent(compNode12);	  		  	
	  	
	  	Element compNode13 = new Element("comp");
	  	compNode13.setAttribute("value", "13");
	  	compNode13.setAttribute("name", "does not contain");
	  	root.addContent(compNode13);	  	
	  	
	  	Element compNode14 = new Element("comp");
	  	compNode14.setAttribute("value", "14");
	  	compNode14.setAttribute("name", "is between");
	  	root.addContent(compNode14);
	  	
	  	Element compNode15 = new Element("comp");
	  	compNode15.setAttribute("value", "15");
	  	compNode15.setAttribute("name", "is not between");
	  	root.addContent(compNode15);
	  	
	  	Element valueNode1 = new Element("value");
	  	valueNode1.setAttribute("value", "1");
	  	valueNode1.setAttribute("name", "Value");
	  	root.addContent(valueNode1);		  	
	  	
	  	Element customFieldNode;
	  	for (CustomField customField : customFields){
	  		customFieldNode = new Element("value");
	  		customFieldNode.setAttribute("value", "customField");
	  		customFieldNode.setAttribute("name", customField.getName());
		  	root.addContent(customFieldNode);	
	  	}
	  	
	  	
	  	Element valueNode3 = new Element("value");
	  	valueNode3.setAttribute("value", "3");
	  	valueNode3.setAttribute("name", "User Input");
	  	root.addContent(valueNode3);	
	  	
	  	Element valueNode4 = new Element("value");
	  	valueNode4.setAttribute("value", "4");
	  	valueNode4.setAttribute("name", "Calendar");
	  	root.addContent(valueNode4);	
	  	
	  	Element valueNode5 = new Element("value");
	  	valueNode5.setAttribute("value", "5");
	  	valueNode5.setAttribute("name", "Boolean");
	  	root.addContent(valueNode5);		

	  	Element valueNode6 = new Element("value");
	  	valueNode6.setAttribute("value", "6");
	  	valueNode6.setAttribute("name", "Current Date/Time");
	  	root.addContent(valueNode6);	  	
	  	
	  	Element booleanNode1 = new Element("boolean");
	  	booleanNode1.setAttribute("value", "1");
	  	booleanNode1.setAttribute("name", "true");
	  	root.addContent(booleanNode1);		  	
	  	
	  	Element booleanNode2 = new Element("boolean");
	  	booleanNode2.setAttribute("value", "2");
	  	booleanNode2.setAttribute("name", "false");
	  	root.addContent(booleanNode2);		  	

	  	Element newRequestNode = new Element("newRequest");
	  	newRequestNode.setAttribute("value", "true");
	  	root.addContent(newRequestNode);	  	
	  	
	  	//System.out.println("Comps for update Query \n" + root.toString());
	  	
	  	return root;
}   
  /**
   * This is for insert query and web service
   * @param customFields
   * @return
   */
  public Element getSimpleComps(List<CustomField> customFields){
	  	Element root = new Element("comps");
	  	
	  	Element descriptionNode = new Element("group-explain");	
	  	descriptionNode.setAttribute("first", "Please set ");
	  	descriptionNode.setAttribute("second", "of the following fields:");
	  	root.addContent(descriptionNode);
	  		  	
	  	//No Group nodes
	  	Element groupNode1 = new Element("group");
	  	groupNode1.setAttribute("value", "1");
	  	groupNode1.setAttribute("name", "all");
	  	root.addContent(groupNode1);

	  	Element actionNode1 = new Element("action");
	  	actionNode1.setAttribute("value", "1");
	  	actionNode1.setAttribute("name", "Add Condition (after current)");
	  	root.addContent(actionNode1);	  	
	  	
//	  	Element actionNode2 = new Element("action");
//	  	actionNode2.setAttribute("value", "2");
//	  	actionNode2.setAttribute("name", "Add Bracket (after current)");
//	  	root.addContent(actionNode2);		  	
	  	
	  	Element actionNode3 = new Element("action");
	  	actionNode3.setAttribute("value", "3");
	  	actionNode3.setAttribute("name", "Delete Current Row");
	  	root.addContent(actionNode3);	
	  	
	  	Element actionNode4 = new Element("action");
	  	actionNode4.setAttribute("value", "4");
	  	actionNode4.setAttribute("name", "Enable/Disable Row");
	  	root.addContent(actionNode4);	  	
	  	
	  	Element compNode1 = new Element("comp");
	  	compNode1.setAttribute("value", "1");
	  	compNode1.setAttribute("name", "is equal to");
	  	root.addContent(compNode1);	
	  	
	  	Element valueNode1 = new Element("value");
	  	valueNode1.setAttribute("value", "1");
	  	valueNode1.setAttribute("name", "Value");
	  	root.addContent(valueNode1);		 	
	  	
	  	Element customFieldNode;
	  	if (customFields != null){
		  	for (CustomField customField : customFields){
		  		customFieldNode = new Element("value");
		  		customFieldNode.setAttribute("value", "customField");
		  		customFieldNode.setAttribute("name", customField.getName());
			  	root.addContent(customFieldNode);	
		  	}
	  	}
	  	
	  	Element valueNode3 = new Element("value");
	  	valueNode3.setAttribute("value", "3");
	  	valueNode3.setAttribute("name", "User Input");
	  	root.addContent(valueNode3);	
	  	
	  	Element valueNode4 = new Element("value");
	  	valueNode4.setAttribute("value", "4");
	  	valueNode4.setAttribute("name", "Calendar");
	  	root.addContent(valueNode4);	
	  	
	  	Element valueNode5 = new Element("value");
	  	valueNode5.setAttribute("value", "5");
	  	valueNode5.setAttribute("name", "Boolean");
	  	root.addContent(valueNode5);		

	  	Element valueNode6 = new Element("value");
	  	valueNode6.setAttribute("value", "6");
	  	valueNode6.setAttribute("name", "Current Date/Time");
	  	root.addContent(valueNode6);	  	
	  	
	  	Element booleanNode1 = new Element("boolean");
	  	booleanNode1.setAttribute("value", "1");
	  	booleanNode1.setAttribute("name", "true");
	  	root.addContent(booleanNode1);		  	
	  	
	  	Element booleanNode2 = new Element("boolean");
	  	booleanNode2.setAttribute("value", "2");
	  	booleanNode2.setAttribute("name", "false");
	  	root.addContent(booleanNode2);		  	

	  	Element newRequestNode = new Element("newRequest");
	  	newRequestNode.setAttribute("value", "false");
	  	root.addContent(newRequestNode);   	
	  	
	  	return root;
} 
  
  public Element getWebServiceQueryBuider(List<QueryCriteriaDobj> queryCriteria, List<IParamView> parameters){	  
	  Element root = new Element(ROOT_ROOT);
	  Element comps = getSimpleComps(null);
	  root.addContent(comps);
	  
	  Element criteria = getQueryCriteria(queryCriteria);
	  root.addContent(criteria);
	  
	  Element viewNode = getWebServiceDetails(parameters);
	  //Element viewNode = getViewForQueryCriteria(view);
	  root.addContent(viewNode);
	  
	  //Element queryNode = getQuery(query);
	  //root.addContent(queryNode);
	  
	  return root;	  
  }
  
  public Element getSelectQueryBuilder(List<QueryCriteriaDobj> queryCriteria, DataView view, List<CustomField> customFields, QueryDobj query){
	  Element root = new Element(ROOT_ROOT);
	  Element comps = getCompsForSelectQuery(customFields);
	  root.addContent(comps);
	  
	  Element criteria = getQueryCriteria(queryCriteria);
	  root.addContent(criteria);
	  
	  Element viewNode = getViewForQueryCriteria(view);
	  root.addContent(viewNode);
	  
	  Element queryNode = getQuery(query);
	  root.addContent(queryNode);
	  
	  return root;
  }

  public Element getWSQueryBuilder(List<QueryCriteriaDobj> queryCriteria, List<CustomField> customFields, QueryDobj query){
	  Element root = new Element(ROOT_ROOT);
	  Element comps = getCompsForSelectQuery(customFields);
	  root.addContent(comps);
	  
	  Element criteria = getQueryCriteria(queryCriteria);
	  root.addContent(criteria);
	  
//	  Element viewNode = getViewForQueryCriteria(view);
//	  root.addContent(viewNode);
	  
	  Element queryNode = getQuery(query);
	  root.addContent(queryNode);
	  
	  return root;
  }  
  
  public Element getUpdateQueryBuilder(List<QueryCriteriaDobj> queryCriteria, EntityDobj table, List<CustomField> customFields, QueryDobj query){
	  Element root = new Element(ROOT_ROOT);
	  Element comps = getCompsForUpdateQuery1(customFields);
	  root.addContent(comps);
	  
	  Element criteria = getQueryCriteria(queryCriteria);
	  root.addContent(criteria);

	  Element tableNode = getTableForQueryCriteria(table);
	  root.addContent(tableNode);	  

	  Element queryNode = getQuery(query);
	  root.addContent(queryNode);	  
	  
	  return root;
  }
  
  public Element getInsertQueryBuilder(List<QueryCriteriaDobj> queryCriteria, EntityDobj table, List<CustomField> customFields, QueryDobj query){
	  Element root = new Element(ROOT_ROOT);
	  Element comps = getSimpleComps(customFields);
	  root.addContent(comps);
	  
	  Element criteria = getQueryCriteria(queryCriteria);
	  root.addContent(criteria);

	  Element tableNode = getTableForQueryCriteria(table);
	  root.addContent(tableNode);	  

	  Element queryNode = getQuery(query);
	  root.addContent(queryNode);
	  
	  return root;
  }  
  
  public Element getCompsForSelectQuery(List<CustomField> customFields){//String file, String action
	  	//Element root = new Element(ROOT_ROOT);
	  	Element root = new Element("comps");
	  	
	  	Element descriptionNode = new Element("group-explain");	
	  	descriptionNode.setAttribute("first", "Choose records where");
	  	descriptionNode.setAttribute("second", "of the following apply");
	  	root.addContent(descriptionNode);
	  	
	  	Element groupNode1 = new Element("group");
	  	groupNode1.setAttribute("value", "1");
	  	groupNode1.setAttribute("name", "all");
	  	root.addContent(groupNode1);
	  	
	  	Element groupNode2 = new Element("group");
	  	groupNode2.setAttribute("value", "2");
	  	groupNode2.setAttribute("name", "any");
	  	root.addContent(groupNode2);
	  	
	  	Element groupNode3 = new Element("group");
	  	groupNode3.setAttribute("value", "3");
	  	groupNode3.setAttribute("name", "none");
	  	root.addContent(groupNode3);	  	
	  	
	  	Element groupNode4 = new Element("group");
	  	groupNode4.setAttribute("value", "4");
	  	groupNode4.setAttribute("name", "not all");
	  	root.addContent(groupNode4);	
	  	
	  	Element actionNode1 = new Element("action");
	  	actionNode1.setAttribute("value", "1");
	  	actionNode1.setAttribute("name", "Add Condition (after current)");
	  	root.addContent(actionNode1);	  	
	  	
	  	Element actionNode2 = new Element("action");
	  	actionNode2.setAttribute("value", "2");
	  	actionNode2.setAttribute("name", "Add Bracket (after current)");
	  	root.addContent(actionNode2);		  	
	  	
	  	Element actionNode3 = new Element("action");
	  	actionNode3.setAttribute("value", "3");
	  	actionNode3.setAttribute("name", "Delete Current Row");
	  	root.addContent(actionNode3);	
	  	
	  	Element actionNode4 = new Element("action");
	  	actionNode4.setAttribute("value", "4");
	  	actionNode4.setAttribute("name", "Enable/Disable Row");
	  	root.addContent(actionNode4);	
	  	
	  	Element compNode1 = new Element("comp");
	  	compNode1.setAttribute("value", "1");
	  	compNode1.setAttribute("name", "is equal to");
	  	root.addContent(compNode1);	  	
	  	
	  	Element compNode2 = new Element("comp");
	  	compNode2.setAttribute("value", "2");
	  	compNode2.setAttribute("name", "is not equal to");
	  	root.addContent(compNode2);	 
	  	
	  	Element compNode3 = new Element("comp");
	  	compNode3.setAttribute("value", "3");
	  	compNode3.setAttribute("name", "is less than");
	  	root.addContent(compNode3);	
	  	
	  	Element compNode4 = new Element("comp");
	  	compNode4.setAttribute("value", "4");
	  	compNode4.setAttribute("name", "is less than or equal to");
	  	root.addContent(compNode4);	 
	  	
	  	Element compNode5 = new Element("comp");
	  	compNode5.setAttribute("value", "5");
	  	compNode5.setAttribute("name", "is greater than");
	  	root.addContent(compNode5);
	  	
	  	Element compNode6 = new Element("comp");
	  	compNode6.setAttribute("value", "6");
	  	compNode6.setAttribute("name", "is greater than or equal to");
	  	root.addContent(compNode6);
	  	
	  	Element compNode7 = new Element("comp");
	  	compNode7.setAttribute("value", "7");
	  	compNode7.setAttribute("name", "is null");
	  	root.addContent(compNode7);
	  	
	  	Element compNode8 = new Element("comp");
	  	compNode8.setAttribute("value", "8");
	  	compNode8.setAttribute("name", "is in list");
	  	root.addContent(compNode8);

	  	Element compNode9 = new Element("comp");
	  	compNode9.setAttribute("value", "9");
	  	compNode9.setAttribute("name", "is not in list");
	  	root.addContent(compNode9);
	  	
	  	Element compNode10 = new Element("comp");
	  	compNode10.setAttribute("value", "10");
	  	compNode10.setAttribute("name", "starts with");
	  	root.addContent(compNode10);	  	
	  	
	  	Element compNode11 = new Element("comp");
	  	compNode11.setAttribute("value", "11");
	  	compNode11.setAttribute("name", "does not start with");
	  	root.addContent(compNode11);	  		  	
	  	
	  	Element compNode12 = new Element("comp");
	  	compNode12.setAttribute("value", "12");
	  	compNode12.setAttribute("name", "contains");
	  	root.addContent(compNode12);	  		  	
	  	
	  	Element compNode13 = new Element("comp");
	  	compNode13.setAttribute("value", "13");
	  	compNode13.setAttribute("name", "does not contain");
	  	root.addContent(compNode13);	  	
	  	
	  	Element compNode14 = new Element("comp");
	  	compNode14.setAttribute("value", "14");
	  	compNode14.setAttribute("name", "is between");
	  	root.addContent(compNode14);
	  	
	  	Element compNode15 = new Element("comp");
	  	compNode15.setAttribute("value", "15");
	  	compNode15.setAttribute("name", "is not between");
	  	root.addContent(compNode15);
	  	
	  	Element valueNode1 = new Element("value");
	  	valueNode1.setAttribute("value", "1");
	  	valueNode1.setAttribute("name", "Value");
	  	root.addContent(valueNode1); 		  	
	  	
	  	Element customFieldNode;
	  	int valueCounter = 3;
	  	if (customFields != null){
		  	for (CustomField customField : customFields){
		  		customFieldNode = new Element("value");
		  		//customFieldNode.setAttribute("value", String.valueOf(valueCounter));
		  		customFieldNode.setAttribute("value", "customField");//String.valueOf(customField.getId()));
		  		customFieldNode.setAttribute("name", customField.getName());
			  	root.addContent(customFieldNode);	
			  	//valueCounter++;
		  	}
	  	}
	  	
	  	Element valueNode3 = new Element("value");
	  	valueNode3.setAttribute("value", "3");
	  	valueNode3.setAttribute("name", "User Input");
	  	root.addContent(valueNode3);	
	  	
	  	Element valueNode4 = new Element("value");
	  	valueNode4.setAttribute("value", "4");
	  	valueNode4.setAttribute("name", "Calendar");
	  	root.addContent(valueNode4);	
	  	
	  	Element valueNode5 = new Element("value");
	  	valueNode5.setAttribute("value", "5");
	  	valueNode5.setAttribute("name", "Boolean");
	  	root.addContent(valueNode5);		

	  	Element valueNode6 = new Element("value");
	  	valueNode6.setAttribute("value", "6");
	  	valueNode6.setAttribute("name", "Current Date/Time");
	  	root.addContent(valueNode6);	  	
	  	
	  	Element booleanNode1 = new Element("boolean");
	  	booleanNode1.setAttribute("value", "1");
	  	booleanNode1.setAttribute("name", "true");
	  	root.addContent(booleanNode1);		  	
	  	
	  	Element booleanNode2 = new Element("boolean");
	  	booleanNode2.setAttribute("value", "2");
	  	booleanNode2.setAttribute("name", "false");
	  	root.addContent(booleanNode2);		  	

	  	Element newRequestNode = new Element("newRequest");
	  	newRequestNode.setAttribute("value", "true");
	  	root.addContent(newRequestNode);	  	
	  	
	  	//System.out.println("comps for selectQuery: \n" + root);
	  	
	  	return root;
  }     
  public Element getWebServices(List<WebServiceDobj> allSamples){
	  	Element root = new Element(ROOT_ROOT);  	
  	
	  	for (WebServiceDobj sample : allSamples) {
			Element sampleNode = new Element(ELEMENT_NODE);
			sampleNode.setAttribute(ATTRIBUTE_ID, String.valueOf(sample.getId()));
			sampleNode.setAttribute("provider", sample.getProviderName());
			sampleNode.setAttribute("description", sample.getDescription());
			sampleNode.setAttribute("url", sample.getUrl());
			root.addContent(sampleNode);
		}
	  	
	  	return root;	  
  }
  
  public Element getWebServiceDescription(WebServiceDescription webServiceDescription, String selectedOperation){
	  	Element root = new Element(ROOT_ROOT);  	
	  	Definition definition = webServiceDescription.getDefinition();
	  	Element rootNode = new Element(ELEMENT_NODE);
	  	rootNode.setAttribute(ATTRIBUTE_TYPE, "URL");
	  	rootNode.setAttribute(ATTRIBUTE_NAME, webServiceDescription.getWsdlUrl());
	  	rootNode.setAttribute("__OPTTREE_META_open", "true");
	  	
	  	Map s = definition.getServices();
	  	Set serviceSet = s.entrySet();
	  	
	    for (Iterator i = serviceSet.iterator(); i.hasNext(); ) {
            Entry entry = (Entry) i.next();
            Service service = (Service) entry.getValue();
            
            Element serviceNode = new Element(ELEMENT_NODE);
            serviceNode.setAttribute(ATTRIBUTE_TYPE, "service");
            serviceNode.setAttribute(ATTRIBUTE_NAME, service.getQName().getLocalPart());
            serviceNode.setAttribute("__OPTTREE_META_open", "true");
            
            Map portsMap = service.getPorts();
            Set set = portsMap.entrySet();

//    	  	String inPartName = null;
//    		QName inPartTypeName = null;
//    		String outPartName = null;
//    		QName outPartTypeName = null;
    		
    		//go through PortTypes
    	    for (Iterator j = set.iterator(); j.hasNext(); ) {
	            Entry entryPort = (Entry) j.next();
	            Port port = (Port) entryPort.getValue();
	            Binding binding = port.getBinding();
	            PortType portType = binding.getPortType();
                System.out.println(portType.getQName().toString());
                
	            Element portNode = new Element(ELEMENT_NODE);
	            portNode.setAttribute(ATTRIBUTE_TYPE, "port");
	            portNode.setAttribute(ATTRIBUTE_NAME, port.getName());
	            //portNode.setAttribute(ATTRIBUTE_NAME, portType.getQName().getLocalPart());	            
	            portNode.setAttribute("__OPTTREE_META_open", "true");
	            
                List list = portType.getOperations();
                for (Iterator it = list.iterator(); it.hasNext(); ) {
            		Operation operation = (Operation) it.next();
            		Element operationNode = new Element(ELEMENT_NODE);
            		System.out.println(operation.getName());
            		operationNode.setAttribute(ATTRIBUTE_TYPE, "operation");
            		String operationName = operation.getName();
            		operationNode.setAttribute(ATTRIBUTE_NAME, operationName);
            		operationNode.setAttribute("service", service.getQName().getLocalPart());
            		operationNode.setAttribute("port", portType.getQName().getLocalPart());
            		operationNode.setAttribute("__OPTTREE_META_open", "false");
            		
            		if (operationName.equalsIgnoreCase(selectedOperation)){
            			operationNode.setAttribute("_checked", "true");
            		}
            		
            		//display request parameters

//            		//Input 
//            		Element inputNode = new Element(ELEMENT_NODE);
//            		inputNode.setAttribute(ATTRIBUTE_TYPE, "input");
//            		inputNode.setAttribute(ATTRIBUTE_NAME, "Input: " + operation.getInput().getMessage().getQName().getLocalPart());
//            		inputNode.setAttribute("__OPTTREE_META_open", "false");
//            		
//    				Map inputPartsMap = operation.getInput().getMessage().getParts();
//    				Collection inputParts = inputPartsMap.values();
//    				Iterator inputPartIter = inputParts.iterator();
//    				//System.out.print("\tRequest: ");
//    				while (inputPartIter.hasNext())
//    				{
//    					Part part = (Part)inputPartIter.next();
//    					inPartName = part.getName();
//    					inPartTypeName = part.getTypeName();
//                		//Part Node
//    					Element partNode = new Element(ELEMENT_NODE);
//                		partNode.setAttribute(ATTRIBUTE_TYPE, "part");
//    					partNode.setAttribute(ATTRIBUTE_NAME, inPartName);
//    					partNode.setAttribute("__OPTTREE_META_open", "false");
//    					
//                		//Type Name Node
//                		Element typeNameNode = new Element(ELEMENT_NODE);
//    					typeNameNode.setAttribute(ATTRIBUTE_TYPE, "typeName");
//                		if (inPartTypeName != null)
//    						typeNameNode.setAttribute(ATTRIBUTE_NAME, inPartTypeName.getLocalPart());  //inPartTypeName.getNamespaceURI());
//    					else
//    						typeNameNode.setAttribute(ATTRIBUTE_NAME, inPartTypeName.toString());
//                		typeNameNode.setAttribute("__OPTTREE_META_open", "false");
//                		
//    					partNode.addContent(typeNameNode);
//    					
//    					inputNode.addContent(partNode);
//    				}
//    				operationNode.addContent(inputNode);
//    				
//    				//Output
//            		Element outputNode = new Element(ELEMENT_NODE);
//            		outputNode.setAttribute(ATTRIBUTE_TYPE, "output");
//            		outputNode.setAttribute(ATTRIBUTE_NAME, "Output: " + operation.getOutput().getMessage().getQName().getLocalPart());
//            		outputNode.setAttribute("__OPTTREE_META_open", "false");
////    				 display response parameters
//    				Map outputPartsMap = operation.getOutput().getMessage().getParts();
//    				Collection outputParts = outputPartsMap.values();
//    				Iterator outputPartIter = outputParts.iterator();
//    				//System.out.print("\tResponse: ");
//    				while (outputPartIter.hasNext())
//    				{
//    					Part part = (Part)outputPartIter.next();
//    					outPartName = part.getName();
//    					outPartTypeName = part.getTypeName();
//    					
//                		//Part Node
//    					Element partNode = new Element(ELEMENT_NODE);
//    					partNode.setAttribute(ATTRIBUTE_TYPE, "part");
//                		partNode.setAttribute(ATTRIBUTE_NAME, outPartName);
//                		partNode.setAttribute("__OPTTREE_META_open", "false");
//                		
//                		//Type Name Node
//                		Element typeNameNode = new Element(ELEMENT_NODE);    					
//                		typeNameNode.setAttribute(ATTRIBUTE_TYPE, "typeName");
//    					if (outPartTypeName != null)
//    						typeNameNode.setAttribute(ATTRIBUTE_NAME, outPartTypeName.getLocalPart());// + " , " + inPartTypeName.getNamespaceURI());
//    					else 
//    						typeNameNode.setAttribute(ATTRIBUTE_NAME, outPartTypeName.toString());
//    					typeNameNode.setAttribute("__OPTTREE_META_open", "false");
//    					
//    					partNode.addContent(typeNameNode);
//    					
//    					outputNode.addContent(partNode);
//    					
//    				}
//    				operationNode.addContent(outputNode);
    				
    				portNode.addContent(operationNode);
    				//rootNode.addContent(operationNode);
            	}
                serviceNode.addContent(portNode);
    	    }
    	    rootNode.addContent(serviceNode);
	    }	  	
	    root.addContent(rootNode);
	  	return root;	  
	  
  }  
  public Element getWebServiceDetails(List<IParamView> parameters){
	  Element root = new Element("view");
	  	for (IParamView parameter : parameters){
		  	Element subNode = new Element("node");
	  		subNode.setAttribute("field", parameter.getLabel());		  	
	  		root.addContent(subNode);		  		
	  	}	  			

	  	return root;
  }    
  public Element getWebServiceOperation(WebServiceOperationDobj webServiceOperationDobj){
	  	Element root = new Element(ROOT_ROOT);  	
	
	  	root.setAttribute(ATTRIBUTE_ID, String.valueOf(webServiceOperationDobj.getId()));
	  	root.setAttribute("name", webServiceOperationDobj.getName() == null ? "" : webServiceOperationDobj.getName());
	  	root.setAttribute("description", webServiceOperationDobj.getDescription() == null ? "" : webServiceOperationDobj.getDescription());
	  	root.setAttribute("url", webServiceOperationDobj.getUrl()==null ? "" : webServiceOperationDobj.getUrl());
	  	root.setAttribute("operation", webServiceOperationDobj.getOperation() == null? "": webServiceOperationDobj.getOperation());
	  	
	  	return root;
}
  public Element getWebServiceOperations(List<WebServiceOperationDobj> webServiceOperationDobjs){
	  	Element root = new Element(ROOT_ROOT);  	
	  	for (WebServiceOperationDobj webServiceOperationDobj : webServiceOperationDobjs){
		  	Element wsNode = new Element("ws");
		  	wsNode.setAttribute(ATTRIBUTE_ID, String.valueOf(webServiceOperationDobj.getId()));
		  	wsNode.setAttribute("name", webServiceOperationDobj.getName() == null ? "" : webServiceOperationDobj.getName());
		  	wsNode.setAttribute("description", webServiceOperationDobj.getDescription() == null ? "" : webServiceOperationDobj.getDescription());
		  	wsNode.setAttribute("url", webServiceOperationDobj.getUrl()==null ? "" : webServiceOperationDobj.getUrl());
		  	wsNode.setAttribute("service", webServiceOperationDobj.getService() == null? "": webServiceOperationDobj.getService());
		  	wsNode.setAttribute("port", webServiceOperationDobj.getPort() == null? "": webServiceOperationDobj.getPort());
		  	wsNode.setAttribute("operation", webServiceOperationDobj.getOperation() == null? "": webServiceOperationDobj.getOperation());
		  	root.addContent(wsNode);
	  	}
	  	return root;
} 
  public Element getDrivers(List<JdbcDriver> list, String errorMsg){
	  Element root = new Element(ROOT_ROOT);
	  
	  int driversNum = list.size();
	  root.setAttribute("driversNum", String.valueOf(driversNum));
	  
	  //Add NEW item for datacombobox
		Element addNewNode = new Element("driver");
		addNewNode.setAttribute("id", "0");
		addNewNode.setAttribute("name", "Add New Driver");
		root.addContent(addNewNode);
		
	  if (list != null){
		for (JdbcDriver jdbcDriver : list) {
			Element node = new Element("driver");
			node.setAttribute("id", String.valueOf(jdbcDriver.getId()));
			node.setAttribute("name", jdbcDriver.getName());
			node.setAttribute("driver", jdbcDriver.getDriver());
			node.setAttribute("urlFormat", jdbcDriver.getUrlFormat());
			node.setAttribute("description", jdbcDriver.getDescription() == null ? "" : jdbcDriver.getDescription());
			root.addContent(node);
		}
	  }
	  root.addContent(errorMsg);
	  return root;
  }
  public Element getDriver(JdbcDriver jdbcDriver){
	  Element root = new Element(ROOT_ROOT);  
	  Element node = new Element("driver");
	  node.setAttribute("id", String.valueOf(jdbcDriver.getId()));
	  node.setAttribute("name", jdbcDriver.getName());
	  node.setAttribute("driver", jdbcDriver.getDriver());
	  node.setAttribute("urlFormat", jdbcDriver.getUrlFormat());
	  node.setAttribute("description", jdbcDriver.getDescription() == null ? "" : jdbcDriver.getDescription());
	  node.setAttribute("filename", jdbcDriver.getFileName() == null ? "" : jdbcDriver.getFileName());
	  node.setAttribute("originalFilename", jdbcDriver.getFileName() == null ? "" : jdbcDriver.getFileName());
	  root.addContent(node);
	  return root;
  }  
  
  public Element getMessageLogs(List<MessageLog> messageLogs , String result, int userId){
		Element root = new Element(ROOT_ROOT);
		root.setAttribute("userID", String.valueOf(userId));
		root.setAttribute("result", result);

		for (MessageLog msgLog : messageLogs) {
			Element msgLogNode = new Element("msgLog");
			msgLogNode.setAttribute("text", msgLog.getText());
			msgLogNode.setAttribute("date", msgLog.getDate().toString());
			msgLogNode.setAttribute("type", msgLog.getMessageType());
			root.addContent(msgLogNode);
		}
		return root;
  }
  
  public static Element displayCustomQueryXml(Map allCustomQueries, String result){
		Element root = new Element(ROOT_ROOT);
		if(result!= null){
			root.setAttribute("result", result);
		}
		List<CustomField>  allCustomQueriesList = new ArrayList<CustomField>();
		allCustomQueriesList.addAll(allCustomQueries.values());
		for (CustomField customField : allCustomQueriesList) {//Combobox
			Element customNode = new Element("custom");
			customNode.setAttribute("customName", customField.getName());
			customNode.setAttribute("customId", String.valueOf(customField.getId()));
			root.addContent(customNode);
		}  
		return root;
}

  public Element getWebserviceResult(List<ParamListItem> resultParam, int rowSize, List<WebServiceResultRow> getParamListItemsAsRow){//(DataView dataView, RecordSet rs, String sorts){//getQueryRecordSet 
		
	  Element root = new Element("root");
	  	
		Element metaDataNode = new Element("metadata"); 
		Set colTitlesSet = new HashSet();
		for(ParamListItem resultItem : resultParam )
		{
			String colTitle;
			Object resultItemValue = resultItem.getValue();
			if(resultItemValue.getClass().isArray()){
				Object[] resultItemArray = (Object[])resultItemValue;
				colTitle = "Result-";
				for(int i = 0; i< resultItemArray.length; i++){
					Object obj = resultItemArray[i];
					//System.out.println(resultItem.getLabel() + " :" + obj.toString());
					colTitlesSet.add(colTitle+i);
				}
			}else{
				colTitle = resultItem.getLabel();
				colTitlesSet.add(colTitle);
				//System.out.println(resultItem.getLabel() + " :" + resultItem.getValue());
			}
			
			//colTitlesSet.add(colTitle);
		}
		Object colTitles[] = (Object[])colTitlesSet.toArray();
		for(int i = 0; i<colTitles.length; i++){
	        Element titleNode = new Element("column"); 
	        titleNode.setAttribute("name", colTitles[i].toString());
	        titleNode.setAttribute("display", colTitles[i].toString());
	        titleNode.setAttribute("editable", "false");
	        titleNode.setAttribute("resizable", "true");
	        metaDataNode.addContent(titleNode);			
		}
		root.addContent(metaDataNode);
		
		//Values........
        Element resultsetNode = new Element("resultset");		
		if(rowSize >1 && !getParamListItemsAsRow.isEmpty()){
	        for(WebServiceResultRow row : getParamListItemsAsRow ){
				System.out.println("Row (" + row.getRowIndex() + ")- parent = " + row.getParentObj().getClass().getSimpleName());
				List<ParamListItem> paramsRow = row.getParamItemList();
	        	Element rowNode = new Element("row");
	            for(int col = 0; col < colTitles.length; col++)
	            {
	            	Object val = null;
	    			String valTitle = "";			
					for(ParamListItem resultItem : paramsRow )
					{
	        			Object resultItemValue = resultItem.getValue();
	        			if(resultItemValue.getClass().isArray()){
	        				Object[] resultItemArray = (Object[])resultItemValue;
	        				for(int i = 0; i< resultItemArray.length; i++){
	        					Object obj = resultItemArray[i];
	        					val = obj;
	        					valTitle = resultItem.getLabel();
	        				}
	        			}else{
	        				val = resultItemValue;
	        				valTitle = resultItem.getLabel();
	        			}
	        			if(valTitle.equalsIgnoreCase(colTitles[col].toString())){
	        	        	Object value = (val == null)? new String(""): val;
	        	            String colName = valTitle;// (col > colTitles.length)? "": colTitles[col];
	        	            
	        	            value = (val == null)? new String(""): val;
	        	            Element columnNode = new Element(colName.indexOf(" ")> 0? colName.replace(" ", ""): colName);
	        	            columnNode.setAttribute("value", value.toString());
	        	            rowNode.addContent(columnNode);                    			        			
	        	        }
					}
	            }
	            resultsetNode.addContent(rowNode);
			}
		}else{
        	Element rowNode = new Element("row");
            for(int col = 0; col < colTitles.length; col++)
            {
            	Object val = "";
    			String valTitle = "";			
    			for(ParamListItem resultItem : resultParam )
				{
        			Object resultItemValue = resultItem.getValue();
        			if(resultItemValue.getClass().isArray()){
        				Object[] resultItemArray = (Object[])resultItemValue;
        				for(int i = 0; i< resultItemArray.length; i++){
        					Object obj = resultItemArray[i];
        					val = obj.toString();
        					valTitle = "Result-" + i;//resultItem.getLabel();
        					
                			if(valTitle.equalsIgnoreCase(colTitles[col].toString())){
                	        	Object value = (val == null)? new String(""): val;
                	            String colName = valTitle;// (col > colTitles.length)? "": colTitles[col];
                	            
                	            value = (val == null)? new String(""): val;
                	            Element columnNode = new Element(colName.indexOf(" ")> 0? colName.replace(" ", ""): colName);
                	            columnNode.setAttribute("value", value.toString());
                	            rowNode.addContent(columnNode);                    			        		
                	        }        					
        				}
        			}else{
        				val = resultItemValue;
        				valTitle = resultItem.getLabel();
            			
        				if(valTitle.equalsIgnoreCase(colTitles[col].toString())){
            	        	Object value = (val == null)? new String(""): val;
            	            String colName = valTitle;// (col > colTitles.length)? "": colTitles[col];
            	            
            	            value = (val == null)? new String(""): val;
            	            Element columnNode = new Element(colName.indexOf(" ")> 0? colName.replace(" ", ""): colName);
            	            columnNode.setAttribute("value", value.toString());
            	            rowNode.addContent(columnNode);                    			        		
            	        }        				
        			}
				}
            }
            resultsetNode.addContent(rowNode);			
		}
		root.addContent(resultsetNode);	
	  	return root;
}
  public static void main(String[] args){
    
  }
}
