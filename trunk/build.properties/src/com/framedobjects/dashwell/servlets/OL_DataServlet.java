package com.framedobjects.dashwell.servlets;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.DataSourceException;
import wsl.fw.exception.MdnException;
import wsl.fw.security.Group;
import wsl.fw.security.GroupMembership;
import wsl.fw.security.User;
import wsl.fw.security.UserWrapper;
import wsl.mdn.dataview.DataSourceDobj;
import wsl.mdn.dataview.DataView;
import wsl.mdn.dataview.DataViewField;
import wsl.mdn.dataview.EntityDobj;
import wsl.mdn.dataview.FieldDobj;
import wsl.mdn.dataview.GroupDataView;
import wsl.mdn.dataview.GroupTablePermission;
import wsl.mdn.dataview.JdbcDataSourceDobj;
import wsl.mdn.dataview.JdbcDriver;
import wsl.mdn.dataview.LanguageDobj;
import wsl.mdn.dataview.ProjectDobj;
import wsl.mdn.dataview.QueryCriteriaDobj;
import wsl.mdn.dataview.QueryDobj;
import wsl.mdn.dataview.ResultWrapper;
import wsl.mdn.mdnim.IMConnection;
import wsl.mdn.mdnim.IMContact;
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
import wsl.mdn.server.LicenseManager;
import wsl.mdn.server.MdnServer;

import com.framedobjects.dashwell.biz.IMConactDetailes;
import com.framedobjects.dashwell.biz.LoginManager;
import com.framedobjects.dashwell.biz.RecycleBinItem;
import com.framedobjects.dashwell.db.DataAgentFactory;
import com.framedobjects.dashwell.db.DbConnection;
import com.framedobjects.dashwell.db.IDataAgent;
import com.framedobjects.dashwell.handlers.DbDataHandler;
import com.framedobjects.dashwell.utils.Constants;
import com.framedobjects.dashwell.utils.MessageConstants;
import com.framedobjects.dashwell.utils.XmlFormatter;

public class OL_DataServlet extends HttpServlet {

  private static Logger logger = Logger
      .getLogger(OL_DataServlet.class.getName());

  /**
   * The doGet method of the servlet. <br>
   * 
   * This method is called when a form has its tag value method equals to get.
   * 
   * @param request
   *          the request send by the client to the server
   * @param response
   *          the response send by the server to the client
   * @throws ServletException
   *           if an error occurred
   * @throws IOException
   *           if an error occurred
 * @throws MdnException 
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException{
    logger.debug("URI: " + request.getRequestURI());
    logger.debug("Query string: " + request.getQueryString());
    
    Enumeration parameters = request.getParameterNames();
    while (parameters.hasMoreElements()){
      String p = (String)parameters.nextElement();
      logger.debug("-- Parameter: " + p + ": " + request.getParameter(p));
    }
    
    String action = request.getParameter(Constants.REQUEST_PARAM_ACTION);
    Element root = null;
    if(action != null){
	    if (action.equalsIgnoreCase(Constants.ACTION_LOGIN)){
	    	root = loginAction(request);
	    } else if (action.equalsIgnoreCase("forgotPassword")){
	    	root = forgotPassword(request);
	    } else if (action.equalsIgnoreCase("updateLicense")){
	    	root = updateLicense(request);
	    } else if (action.equalsIgnoreCase("changeEmailAddress")){
	    	root = changeEmailAddress(request);
	    } else if (action.equalsIgnoreCase(Constants.ACTION_DB_CONNECTIONS)){
	        root = getDBConnections(request);
	    } else if (action.equalsIgnoreCase(Constants.ACTION_USERGROUPS_TREE)){
	        root = getUserGroupsAsTree(request);
	    } else if (action.equalsIgnoreCase("dbUser")){
	    	root = getUser(request);
	    } else if (action.equalsIgnoreCase("dbGroup")){
	    	root = getGroup(request);
	    } /*else if (action.equalsIgnoreCase("saveImContact")){
	        root = saveUserIMContacts(request);
	    } */else if (action.equalsIgnoreCase("getGroupViewPermissions")){
	    	root = getGroupViewPermissions(request);
	    } else if (action.equalsIgnoreCase("getGroupTablePermissions")){
	    	root = getGroupTablePermissions(request);
	    } else if (action.equalsIgnoreCase("setGroupPermissions")){
	        root = setGroupPermissions(request);
	    } else if (action.equalsIgnoreCase(Constants.ACTION_CSS)){
	        root = getCSS(request);
	    } else if (action.equalsIgnoreCase("lang")){
	    	root = getLanguages(request);
	    } else if (action.equalsIgnoreCase("defaultLang")){
	    	root = getDefaultLanguage(request);
	    } else if (action.equalsIgnoreCase("saveLanguage")){
	    	root = saveLanguages(request);
	    } else if (action.equalsIgnoreCase("saveLanguageAsDefault")){
	    	root = saveLanguageAsDefault(request);
	    } else if (action.equalsIgnoreCase("recycleLanguage")){
	    	root = recycleLanguage(request);
	    } else if (action.equalsIgnoreCase("getLanguage")){
	    	root = getLanguage(request);
	    } else if (action.equalsIgnoreCase("getNewLanguage")){
	    	root = getNewLanguage(request);
	    } else if (action.equalsIgnoreCase("navHome")){
	        root = getNavigationHome(request);
	    } else if (action.equalsIgnoreCase("getDefaultProject")){
	        root = getDefaultProject(request);
	    } else if (action.equalsIgnoreCase("getProject")){
	        root = getProjectById(request);
	    } else if (action.equalsIgnoreCase("recycleProject")){
	        root = recycleProject(request);
	    } else if (action.equalsIgnoreCase("clearProject")){
	        root = clearProject(request);
	    } else if (action.equalsIgnoreCase("deleteProject")){
	        root = deleteProject(request);
	    } else if (action.equalsIgnoreCase("addProject")){
	        root = addProject(request);
	    } else if (action.equalsIgnoreCase("saveProject")){
	        root = saveProject(request);
	    } else if (action.equalsIgnoreCase("getExportStructure")){
	        root = getExportStructure(request);
	    } else if (action.equalsIgnoreCase("exportFile")){
	        root = exportFile(request);
	    } else if (action.equalsIgnoreCase("exportFileURL")){
	    	root = exportFileURL(request);
	    } else if (action.equalsIgnoreCase("importProject")){
	    	root = importProjectFile(request);
	    } else if (action.equalsIgnoreCase("navigateProject")){
	        root = getNavigationProject(request);
	    } else if (action.equalsIgnoreCase("navDbs")){
	        root = getNavigationDatabases(request);
	    } else if (action.equalsIgnoreCase("navPres")){
	        root = getNavigationPresentation(request);
	    } else if (action.equalsIgnoreCase("navSett")){
	        root = getNavigationSettings(request);
	    } else if (action.equalsIgnoreCase("navDepl")){
	        root = getNavigationDeployment(request);
	    } else if (action.equalsIgnoreCase("recycleUsersGroups")){
	        root = getUsersGroupsRubbish(request);
	    } else if (action.equalsIgnoreCase("recycleDatabase")){
	        root = recycleDatabase(request);
	    } else if (action.equalsIgnoreCase("updateProjectRecycleBin")){
	        root = updateProjectRecycleBin(request);
	    } else if (action.equalsIgnoreCase("updateSettingsRecycleBin")){
	        root = updateSettingsRecycleBin(request);
	    } else if (action.equalsIgnoreCase("getEmptyRecycleBin")){
	        root = getEmptyRecycleBin(request);
	    } else if (action.equalsIgnoreCase(Constants.ACTION_SCREEN_DEF)){
	        root = getScreenDefs(request);
	    } /*else if (action.equalsIgnoreCase("fileUploadURL")){
	      	root = getFileUploadURL(request);
	    } else if (action.equalsIgnoreCase("driverFileUploadURL")){
	      	root = getDriverFileUploadURL(request);
	    } */else if (action.equalsIgnoreCase("userLicenses")){
	      	root = getUserLicenses(request);
	    } else if (action.equalsIgnoreCase("getSecureLoginLink")){
	      	root = getSecureLoginLink(request);
	    } else if (action.equalsIgnoreCase("getRegisteredEmail")){
	      	root = getRegisteredEmail(request);
	    } else if (action.equalsIgnoreCase("newDbConnection")){
	      	root = newDbConnection(request);
	    } /*else if (action.equalsIgnoreCase("projectFileUploadURL")){
	      	root = getProjectFileUploadURL(request);
	    } */else if (action.equalsIgnoreCase("getLogFilePath")){
	      	root = getLogFilePath(request);
	    } else {
	        root = new XmlFormatter().undefinedAction();
	    }
    }else
    	root = new XmlFormatter().undefinedAction();
    
    // Return the XML-formatted reply.
    response.setHeader ("Pragma",        "no-cache");
    response.setHeader ("Cache-Control", "no-cache");
    response.setContentType("text/xml; charset=UTF-8");
    String xml = new XMLOutputter().outputString(new Document(root));        
    logger.debug("xml: " + xml);
    
    ServletOutputStream out = response.getOutputStream();
    out.println(xml);
  }

  private Element getUserLicenses(HttpServletRequest request) {
/*	  IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	  ResultWrapper resultWrapper = dataAgent.getUserLicenses();
	  if (resultWrapper != null)
		  return new XmlFormatter().getUserLicenses(resultWrapper);
	  else
		  return null;*/
	  return new Element("root");
  }

private Element loginAction(HttpServletRequest request) {
	request.getSession().setMaxInactiveInterval(-1);
    Element root = null;
    String action = request.getParameter("action");
    String username = request.getParameter(Constants.REQUEST_PARAM_USERNAME);
    String password = request.getParameter(Constants.REQUEST_PARAM_PASSWORD);
    LoginManager loginMngr = new LoginManager();
    String languageFile = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
    if (languageFile == null){
    	request.getSession().setAttribute(Constants.SESSION_LANGUAGE_FILE, "gui_en.xml");
    }
    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
    // do version check
	/* licensee String serverVersion = LicenseManager.getServerVersion ();
	if (serverVersion == null)
	{
		// cound not connect toserver, display error and exit
		root = new XmlFormatter().invalidLogin(action, languageFile, "error-invalid-login_server_error", "", "");
		return root;
	}*/

    /* licensee if (!MdnServer.VERSION_NUMBER.equals (serverVersion))
	{
		// invalid version, display error and exit
		String  errorMsg = "[ " + serverVersion
				+ " / " + MdnServer.VERSION_NUMBER + " ]";
		
		root = new XmlFormatter().invalidLogin(action, languageFile, "error-invalid-login_version_error1", "error-invalid-login_version_error2", errorMsg);
		return root;
	}*/

	// do license check after MdnAdminApp ctor as this sets all singletons
    /* licensee	int daysLeft = LicenseManager.isLicenseValid ();
	if (daysLeft < 0)
	{
		// invalid license, display error and exit
		String errorMsg = LicenseManager.getErrorDescription (daysLeft);
		root = new XmlFormatter().invalidLogin(action, languageFile, "error-invalid-login_license_error", errorMsg, "");
		return root;
	}
	else
	{
		
    int numUsers = 0;
		try {
			numUsers = dataAgent.getNumberOfUsers();
		} catch (Exception e1) {
			e1.printStackTrace();
		} */
		UserWrapper userWrapper;
		try {
			userWrapper = loginMngr.getLoginUser(username, password);
		    if (userWrapper.getLoginUser() != null){
				// valid license, show the app		    	
		        root = new XmlFormatter().validLogin(action, userWrapper.getLoginUser()/*, numUsers, daysLeft*/);
		        // Set the user on the session to identify subsequent requests.
		        request.getSession().setAttribute(Constants.SESSION_LOGIN_USER, userWrapper.getLoginUser());
		        // TODO Read the language setting from the database.
				
		      } else {
		          //String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
		    	  root = new XmlFormatter().invalidLogin(action, languageFile, userWrapper.getErrorMsg(), userWrapper.getErrorMsg2(), "");
		      }		
		} catch (MdnException e) {
			e.printStackTrace();
	        //String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
			root = new XmlFormatter().invalidLogin(action, languageFile, "error-invalid-login", "", "");		
		}
	//}
    return root;
  }
  private Element updateLicense(HttpServletRequest request) {
	    /* licensee Element root = null;
	    String action = request.getParameter("action");
	    //String userName = request.getParameter("userName");
	    //String masterId = request.getParameter("masterId");
	    //LoginManager loginMngr = new LoginManager();
	    String languageFile = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    if (languageFile == null){
	    	request.getSession().setAttribute(Constants.SESSION_LANGUAGE_FILE, "gui_en.xml");
	    }
	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	    // do version check
		String serverVersion = LicenseManager.getServerVersion ();
		if (serverVersion == null)
		{
			// cound not connect toserver, display error and exit
			root = new XmlFormatter().invalidLogin(action, languageFile, "error-invalid-login_server_error", "", "");
			return root;
		}

		
		// do license check after MdnAdminApp ctor as this sets all singletons
		ResultWrapper resultWrapper = LicenseManager.updateUserLicense();		
		if (resultWrapper == null){
			root = new XmlFormatter().invalidLogin(action, languageFile, "Failed!", "", "");
			return root;			
		}
		String errorMsg = resultWrapper.getErrorMsg();
		if (errorMsg != null){
			root = new XmlFormatter().invalidLogin(action, languageFile, errorMsg, "", "");
			return root;			
		}
		
		*/
		
		
		//int daysLeft = LicenseManager.isLicenseValid();

		/*int numUsers = 0;
		try {
			numUsers = dataAgent.getNumberOfUsers();
		} catch (Exception e1) {
			e1.printStackTrace();
		}*/
		//UserWrapper userWrapper = null;
		/*try {
			userWrapper = loginMngr.getLoginUser(userName);
		} catch (MdnException e) {
			e.printStackTrace();
			root = new XmlFormatter().invalidLogin(action, languageFile, "error-invalid-login", "", "");
		}*/
		//if (userWrapper != null){
		  
		/* licensee resultWrapper = dataAgent.getUserLicenses();
		  if (resultWrapper != null)
			  return new XmlFormatter().getUserLicenses(resultWrapper);
		  else
			  return new XmlFormatter().invalidLogin(action, languageFile, "Failed!", "", "");
		  */
		  
		  
			// valid license, show the app		    	
	        //root = new XmlFormatter().validLogin(action, userWrapper.getLoginUser(), numUsers, daysLeft);			
		//}

	    return new Element("root");
	  }
  private Element changeEmailAddress(HttpServletRequest request) {
	    
	  	Element root = null;
	    String action = request.getParameter("action");
	    //String userName = request.getParameter("userName");
	    String masterId = request.getParameter("masterId");
	    //LoginManager loginMngr = new LoginManager();
	    String languageFile = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    if (languageFile == null){
	    	request.getSession().setAttribute(Constants.SESSION_LANGUAGE_FILE, "gui_en.xml");
	    }
	    //IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	    // do version check
		String serverVersion = LicenseManager.getServerVersion ();
		if (serverVersion == null)
		{
			// cound not connect toserver, display error and exit
			root = new XmlFormatter().invalidLogin(action, languageFile, "error-invalid-login_server_error", "", "");
			return root;
		}

		if (masterId == null || masterId.equals("undefined") || masterId.isEmpty()){
			root = new XmlFormatter().invalidLogin(action, languageFile, "Please enter new email address.", "", "");
			return root;			
		}
		
		// do license check after MdnAdminApp ctor as this sets all singletons
		ResultWrapper resultWrapper = LicenseManager.changeEmailAddress(masterId);	
		int installationRef = LicenseManager.getInstallationReferenceNumber();
		if (resultWrapper == null){
			root = new XmlFormatter().getRegisteredEmail(null, installationRef, "Failed!", "");//(action, languageFile, "Failed!", "", "");
			return root;			
		}
		String errorMsg = resultWrapper.getErrorMsg();
		if (errorMsg != null){
			root = new XmlFormatter().getRegisteredEmail(resultWrapper.getRegisteredEmailAddress(),installationRef, errorMsg, (String)resultWrapper.getObject());//(action, languageFile, errorMsg, "", "");
			return root;			
		}
		/*int daysLeft = LicenseManager.isLicenseValid();

		int numUsers = 0;
		try {
			numUsers = dataAgent.getNumberOfUsers();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		UserWrapper userWrapper = null;
		try {
			userWrapper = loginMngr.getLoginUser(userName);
		} catch (MdnException e) {
			e.printStackTrace();
			root = new XmlFormatter().invalidLogin(action, languageFile, "error-invalid-login", "", "");
		}*/
		//if (userWrapper != null){
			// valid license, show the app		    	
	        root = new XmlFormatter().getRegisteredEmail(resultWrapper.getRegisteredEmailAddress(),installationRef, "", "");//(action, userWrapper.getLoginUser(), numUsers, daysLeft);			
		//}

	    return root;
	  }
  private Element forgotPassword(HttpServletRequest request) {
	    Element root = null;
	    String action = request.getParameter("action");
	    String username = request.getParameter(Constants.REQUEST_PARAM_USERNAME);
	    //String password = request.getParameter(Constants.REQUEST_PARAM_PASSWORD);
	    LoginManager loginMngr = new LoginManager();
	    String languageFile = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    if (languageFile == null){
	    	request.getSession().setAttribute(Constants.SESSION_LANGUAGE_FILE, "gui_en.xml");
	    }
	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	    // do version check
		String serverVersion = LicenseManager.getServerVersion ();
		if (serverVersion == null)
		{
			// cound not connect toserver, display error and exit
			root = new XmlFormatter().invalidLogin(action, languageFile, "error-invalid-login_server_error", "", "");
			return root;
		}

		if (!MdnServer.VERSION_NUMBER.equals (serverVersion))
		{
			// invalid version, display error and exit
			String  errorMsg = "[ " + serverVersion
					+ " / " + MdnServer.VERSION_NUMBER + " ]";
			
			root = new XmlFormatter().invalidLogin(action, languageFile, "error-invalid-login_version_error1", "error-invalid-login_version_error2", errorMsg);
			return root;
		}

		// do license check after MdnAdminApp ctor as this sets all singletons
		int daysLeft = LicenseManager.isLicenseValid ();
		if (daysLeft < 0)
		{
			// invalid license, display error and exit
			String errorMsg = LicenseManager.getErrorDescription (daysLeft);
			root = new XmlFormatter().invalidLogin(action, languageFile, "error-invalid-login_license_error", errorMsg, "");
			return root;
		}
		else
		{
			int numUsers = 0;
			try {
				numUsers = dataAgent.getNumberOfUsers();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			UserWrapper userWrapper;
			try {
				userWrapper = loginMngr.getLoginUser(username);
			    if (userWrapper.getLoginUser() != null){
					// valid license, show the app		    	
			        root = new XmlFormatter().validLogin(action, userWrapper.getLoginUser()/*, numUsers, daysLeft*/);
			        // Set the user on the session to identify subsequent requests.
			        //request.getSession().setAttribute(Constants.SESSION_LOGIN_USER, userWrapper.getLoginUser());
			        // TODO Read the language setting from the database.
					
			      } else {
			          //String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
			    	  root = new XmlFormatter().invalidLogin(action, languageFile, userWrapper.getErrorMsg(), userWrapper.getErrorMsg2(), "");
			      }		
			} catch (MdnException e) {
				e.printStackTrace();
		        //String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
				root = new XmlFormatter().invalidLogin(action, languageFile, "error-invalid-login", "", "");		
			}
		}
	    return root;
	  }  
  
  private Element getLanguages(HttpServletRequest request){
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		List<LanguageDobj> languages;
		try {
			languages = dataAgent.getAllLanguages();
		    // Format the data.
		    return new XmlFormatter().languages(languages);
		} catch (MdnException e) {
			e.printStackTrace();
			return null;
		}	  
  }
  private Element getDefaultLanguage(HttpServletRequest request){
	  	String action = request.getParameter("action");
	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		try {
			LanguageDobj language = dataAgent.getDefaultLanguage();
		    // Format the data.
		    return new XmlFormatter().getLanguage(language, action);
		} catch (MdnException e) {
			e.printStackTrace();
			return null;
		}	  
}
  private Element getLanguage(HttpServletRequest request){
	    String action = request.getParameter("action");
	    String languageId = request.getParameter("languageId");
	    
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		try {
			LanguageDobj newLang = dataAgent.getLanguageById(Integer.parseInt(languageId));
			return new XmlFormatter().getLanguage(newLang, action);
		} catch (MdnException e1) {
			e1.printStackTrace();
			return null;
		}
}    
  private Element getNewLanguage(HttpServletRequest request){
	    String action = request.getParameter("action");
	    
		LanguageDobj newLang = new LanguageDobj();
		newLang.setId(-1);
		newLang.setDelStatus(Constants.MARKED_AS_NOT_DELETED_INT);
		newLang.setDefault(0);
		newLang.setName("");
		newLang.setFileName("");
		return new XmlFormatter().getLanguage(newLang, action);

}   
  private Element saveLanguages(HttpServletRequest request){
	    String action = request.getParameter("action");
	    String languageId = request.getParameter("languageId");
	    String languageName = request.getParameter("languageName");
	    String fileName = request.getParameter("fileName");
	    String defaultLang = request.getParameter("defaultLang");
	    int intLanguageId= Integer.parseInt(languageId);
	    boolean isDefault = defaultLang.equalsIgnoreCase("1") ? true : false;
	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		LanguageDobj languageDobj = null;
		String detailAction = "add";
		try {
			
			if (intLanguageId == -1){
				detailAction = "add";
				LanguageDobj newLang = new LanguageDobj();
			    newLang.setName(languageName);
			    newLang.setFileName(fileName);
			    newLang.setDefault(isDefault? 1 : 0);
			    newLang.setDelStatus(Constants.MARKED_AS_NOT_DELETED_INT);
			    newLang.setState(DataObject.NEW);
			    newLang.save();
			    
				intLanguageId = newLang.getId();				
				newLang = dataAgent.getLanguageById(intLanguageId);
				if(newLang == null){
					System.out.println("new Language object has not been loaded yet");
					for(;;){//Wait till commite newLang first
						newLang = dataAgent.getLanguageById(intLanguageId);
						if(newLang != null)
							break;
					}
				}
		
				languageDobj = newLang;
				
		    }else{
		    	detailAction = "update";
		    	LanguageDobj oldLang = dataAgent.getLanguageById(intLanguageId);
		    	if (oldLang != null){
		    		LanguageDobj langWithSameName = dataAgent.getLanguageByName(languageName);
					if (langWithSameName != null){	
						if (!langWithSameName.getName().equalsIgnoreCase(oldLang.getName()))
							return new XmlFormatter().saveLanguageResult("Duplicate language name.", oldLang, action, detailAction);			
					}
		    		
		    		oldLang.setState(DataObject.IN_DB);
		    		oldLang.setName(languageName);
		    		oldLang.setFileName(fileName);
		    		oldLang.setDefault(isDefault? 1 : 0);
		    		oldLang.setDelStatus(Constants.MARKED_AS_NOT_DELETED_INT);
		    		oldLang.save();
		    		languageDobj = oldLang;
		    		
		    	}
		    }

		
			//If saved one is default language, reset all others to not default
			if (isDefault){
				List<LanguageDobj> languages;
				languages = dataAgent.getAllLanguages();
			    // Format the data.
			    for (LanguageDobj lang : languages){
			    	if (lang.getId() != intLanguageId){
			    		lang.setDefault(0);
				    	lang.setState(DataObject.IN_DB);
				    	lang.save();
			    	}
			    }			
			}
		} catch (MdnException e1) {
			e1.printStackTrace();
			return new XmlFormatter().saveLanguageResult("Language Save Error.", null, action, detailAction);
		} catch (DataSourceException e1) {
			e1.printStackTrace();
			return new XmlFormatter().saveLanguageResult("Language Save Error.", null, action, detailAction);
		}
		return new XmlFormatter().saveLanguageResult("OK", languageDobj, action, detailAction);
	  
}  
  private Element saveLanguageAsDefault(HttpServletRequest request){
	    String action = request.getParameter("action");
	    String languageId = request.getParameter("languageId");
	    int intLanguageId= Integer.parseInt(languageId);
	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		LanguageDobj languageDobj = null;
		String detailAction = "update";
		try {
			LanguageDobj oldLang = dataAgent.getLanguageById(intLanguageId);
	    	if (oldLang != null){
	    		oldLang.setState(DataObject.IN_DB);
	    		oldLang.setDefault(1);
	    		oldLang.setDelStatus(Constants.MARKED_AS_NOT_DELETED_INT);
	    		oldLang.save();
	    		languageDobj = oldLang;
	    	}
		
			//If saved one is default language, reset all others to not default
			List<LanguageDobj> languages;
			languages = dataAgent.getAllLanguages();
		    // Format the data.
		    for (LanguageDobj lang : languages){
		    	if (lang.getId() != intLanguageId){
		    		lang.setDefault(0);
			    	lang.setState(DataObject.IN_DB);
			    	lang.save();
		    	}
		    }			
		} catch (MdnException e1) {
			e1.printStackTrace();
			return new XmlFormatter().saveLanguageResult("Language Save Error.", languageDobj, action, detailAction);
		} catch (DataSourceException e1) {
			e1.printStackTrace();
			return new XmlFormatter().saveLanguageResult("Language Save Error.", languageDobj, action, detailAction);
		}
		return new XmlFormatter().saveLanguageResult("OK", languageDobj, action, detailAction);
	  
}    
  private Element recycleLanguage(HttpServletRequest request){
	    String action = request.getParameter("action");
	    String languageId = request.getParameter("languageId");
	    int intLanguageId= Integer.parseInt(languageId);
	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		LanguageDobj languageDobj = null;
		String detailAction = "";
		try {
			LanguageDobj oldLang = dataAgent.getLanguageById(intLanguageId);
	    	if (oldLang != null){
	    		oldLang.setState(DataObject.IN_DB);
	    		oldLang.setDelStatus(Constants.MARKED_AS_RECYCLED_INT);
	    		oldLang.save();
	    		languageDobj = oldLang;
	    		detailAction = "recycle";
	    	}

		} catch (MdnException e1) {
			e1.printStackTrace();
			return new XmlFormatter().setResult("Language Save Error.");
		} catch (DataSourceException e1) {
			e1.printStackTrace();
			return new XmlFormatter().setResult("Language Save Error.");
		}
		return new XmlFormatter().saveLanguageResult("OK", languageDobj, action, detailAction);
	  
}    
  
  /*private Element getFileUploadURL(HttpServletRequest request){
	  return new XmlFormatter().fileUploadURL();
  }
  private Element getDriverFileUploadURL(HttpServletRequest request){
	  return new XmlFormatter().driverFileUploadURL();
  }  
  private Element getProjectFileUploadURL(HttpServletRequest request){
	  String url = LicenseManager.getImportFilePath();
	  return new XmlFormatter().projectFileUploadURL(url);
  } */
  private Element getSecureLoginLink(HttpServletRequest request){
	  // LICENSE COMMENT OUT >> ResultWrapper ret = LicenseManager.getSecureLoginLink();
	  String loginLink = null; // LICENSE COMMENT OUT >> ret.getSecureLoginLink();
	  String errorMsg = null;// LICENSE COMMENT OUT >> ret.getErrorMsg();
	  return new XmlFormatter().getSecureLoginLink(loginLink, errorMsg);
  }  
  
  private Element getRegisteredEmail(HttpServletRequest request){
	  String ret = LicenseManager.getRegisteredEmailAddress();
	  int installationRef = LicenseManager.getInstallationReferenceNumber();
	  return new XmlFormatter().getRegisteredEmail(ret, installationRef, "", "");
  }
  
  private Element getLogFilePath(HttpServletRequest request){
	  String action = request.getParameter("action");
	  String[] ret = LicenseManager.getLogFilePath();
	  return new XmlFormatter().getLogFilePath(action, ret);
  } 
  
  private Element getNavigationHome(HttpServletRequest request){
    //  Get the language file.
    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
    // Get the projects from the DB.
    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
    List<ProjectDobj> projects;
	try {
		projects = dataAgent.getNavProjects();
	    // Format the data.
	    return new XmlFormatter().navigationHome(file, projects);
	} catch (MdnException e) {
		e.printStackTrace();
		return null;
	}
  }
  
  private Element getNavigationProject(HttpServletRequest request){
	    //  Get the language file.
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    String projectId = request.getParameter("projectId");
	    // Get the projects from the DB.
	    int intProjectId = 1;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e1) {
			intProjectId = 1;
		}
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		ProjectDobj project;
		try {
			project = dataAgent.getNavProjectById(intProjectId);
//			 Format the data.
			return new XmlFormatter().navigationProject(file, project.getName());
		} catch (MdnException e) {
			e.printStackTrace();
			return null;
		}
		
	  }  
  private Element getDefaultProject(HttpServletRequest request){
	    //  Get the language file.
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    if (file == null){
	    	request.getSession().setAttribute(Constants.SESSION_LANGUAGE_FILE, "gui_en.xml");
	    	file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    }
	    String action = request.getParameter("action");
	    // Get the projects from the DB.
	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	    try {
			ProjectDobj project = dataAgent.getDefaultProject();
		    // Format the data.
		    return new XmlFormatter().getProject(action, MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL), project);
		} catch (MdnException e) {
			e.printStackTrace();
			return null;
		}			
} 
  private Element getProjectById(HttpServletRequest request){
	    //  Get the language file.
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    String action = request.getParameter("action");
	    String projectId = request.getParameter("projectId");
	    // Get the projects from the DB.
	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	    int intProjectId = 1;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e1) {
			intProjectId = 1;
		}
	    try {
			ProjectDobj project = dataAgent.getNavProjectById(intProjectId);
		    // Format the data.
		    return new XmlFormatter().getProject(action, MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL), project);
		} catch (MdnException e) {
			e.printStackTrace();
			return null;
		}			
  } 
  
  private Element addProject(HttpServletRequest request){
	    //  Get the language file.
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    String action = request.getParameter("action");
	    String name = request.getParameter("name");
	    String description = request.getParameter("description");
		try {
			if (name == null || name.equals("") || name.equalsIgnoreCase("undefined")){
				return new XmlFormatter().simpleResult(action, "Please enter the project name.");
			}
			IDataAgent dataAgent = DataAgentFactory.getDataInterface();
			/*try {
				ProjectDobj existingProject = dataAgent.getNavProjectByName(name);
				if (existingProject != null){
					return new XmlFormatter().simpleResult(action, "A project named '" + name + "' already exists. Please use a different name.");
				}
			} catch (MdnException e) {
				e.printStackTrace();
				//ok
			}*/
			
			ProjectDobj project = new ProjectDobj();
			project.setName(name);
			project.setDescription(description);
			project.setDelStatus(Constants.MARKED_AS_NOT_DELETED);
			project.setState(DataObject.NEW);
			project.save();
			
			int newId = project.getId();				
			ProjectDobj newProject = dataAgent.getNavProjectById(newId);
			if(newProject == null){
				System.out.println("new Project object has not been loaded yet");
				for(;;){//Wait till commite new project first
					newProject = dataAgent.getNavProjectById(newId);
					if(newProject != null)
						break;
				}
			}	
			
			// Format the data.
		    return new XmlFormatter().getProject(action, MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL), project);
		} catch (DataSourceException e) {
			e.printStackTrace();
			return new XmlFormatter().simpleResult(action, "Database error.");
		} catch (MdnException e) {
			e.printStackTrace();
			return new XmlFormatter().simpleResult(action, "Database error.");
		}			
  }
  private Element saveProject(HttpServletRequest request){
	    //  Get the language file.
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    String action = request.getParameter("action");
	    String name = request.getParameter("name");
	    String description = request.getParameter("description");
	    String projectId = request.getParameter("projectId");
	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	    int intProjectId = 0;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e1) {
			return new XmlFormatter().simpleResult(action, "Error.");
		}
	    try {
			ProjectDobj project = dataAgent.getNavProjectById(intProjectId);
			project.setName(name);
			project.setDescription(description);
			project.setDelStatus(Constants.MARKED_AS_NOT_DELETED);
			project.setState(DataObject.IN_DB);
			project.save();			
		    // Format the data.
		    return new XmlFormatter().getProject(action, MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL), project);
		} catch (MdnException e) {
			e.printStackTrace();
			return new XmlFormatter().simpleResult(action, MessageConstants.getMessage(file, MessageConstants.DB_ERROR));
		} catch (DataSourceException e) {
			e.printStackTrace();
			return new XmlFormatter().simpleResult(action, MessageConstants.getMessage(file, MessageConstants.DB_ERROR));
		}
			
  }
	private Element recycleProject(HttpServletRequest request){
		String action = request.getParameter("action");
		String projectID = request.getParameter("projectID");
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		ProjectDobj project = null;
		try {
			project = dataAgent.getNavProjectById(Integer.parseInt(projectID));
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (MdnException e1) {
			e1.printStackTrace();
		}
		try {
			project.setDelStatus(Constants.MARKED_AS_RECYCLED);
			project.save();
		} catch (DataSourceException e) {
			e.printStackTrace();
		}
	    return new XmlFormatter().getProject(action, "OK", project);
	}
	private Element clearProject(HttpServletRequest request){
		String action = request.getParameter("action");
		String projectID = request.getParameter("projectID");
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		ProjectDobj project = null;
		try {
			project = dataAgent.getNavProjectById(Integer.parseInt(projectID));
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (MdnException e1) {
			e1.printStackTrace();
		}
		try {
			project.setDelStatus(Constants.MARKED_AS_NOT_DELETED);
			project.save();
		} catch (DataSourceException e) {
			e.printStackTrace();
		}
	    return new XmlFormatter().getProject(action, "OK", project);
	}	
	private Element deleteProject(HttpServletRequest request){
		String action = request.getParameter("action");
		String projectID = request.getParameter("projectID");
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		ProjectDobj project = null;
		try {
			project = dataAgent.getNavProjectById(Integer.parseInt(projectID));
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (MdnException e1) {
			e1.printStackTrace();
		}
		try {
			project.delete();
		} catch (DataSourceException e) {
			e.printStackTrace();
		}
	    return new XmlFormatter().getProject(action, "OK", project);
	}
	private Element getExportStructure(HttpServletRequest request){
	    //  Get the language file.
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    
	    // Get the projects from the DB.
	    //IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	    String projectId = request.getParameter("projectId");
	    int intProjectId = 1;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e1) {
			intProjectId = 1;
		}
	    // Format the data.
		return new XmlFormatter().getExportStructure(file, intProjectId);
  }
  
private Element exportFileURL(HttpServletRequest request){
	String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	String action = request.getParameter("action");
	String projectId = request.getParameter("projectId");
    int intProjectId = 1;
	try {
		intProjectId = Integer.parseInt(projectId);
	} catch (NumberFormatException e1) {
		intProjectId = 1;
	}	
	IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	ProjectDobj project = null;
	try {
		project = dataAgent.getNavProjectById(intProjectId);
	} catch (MdnException e) {
		return new XmlFormatter().simpleResult(file, action, "Get file name failed.");
	}
	String path = LicenseManager.getExportFilePath();
  	//String sep = System.getProperty("file.separator", "/");
    String filename = path + project.getName() + ".xml";	// + sep
    return new XmlFormatter().exportFileURL(filename);
}

private int importDriverFromFile(org.w3c.dom.Element nodeDriver, IDataAgent dataAgent) throws DataSourceException, MdnException{
	  
	  String driverName = nodeDriver.getAttribute(JdbcDriver.FLD_NAME);
	  String driver = nodeDriver.getAttribute(JdbcDriver.FLD_DRIVER);
	  String driverUrlFormat = nodeDriver.getAttribute(JdbcDriver.FLD_URL_FORMAT);
	  String driverDesc = nodeDriver.getAttribute(JdbcDriver.FLD_DESCRIPTION);
	  String driverFilename = nodeDriver.getAttribute(JdbcDriver.FLD_FILE_NAME);
	  String driverDelStatus = nodeDriver.getAttribute(JdbcDriver.FLD_DEL_STATUS);
	  JdbcDriver jdbcDriver = new JdbcDriver();
	  JdbcDriver jdbcDriverWithSameName = dataAgent.getJdbcDriverByName(driverName);
	  //If same name, then overwrite
	  if (jdbcDriverWithSameName != null){
		  jdbcDriver = jdbcDriverWithSameName;
		  jdbcDriver.setState(DataObject.IN_DB);
	  }else{
		  jdbcDriver.setState(DataObject.NEW);
	  }
	  
		jdbcDriver.setName(driverName);
		jdbcDriver.setDriver(driver);
		jdbcDriver.setUrlFormat(driverUrlFormat);
		jdbcDriver.setDescription(driverDesc);
		jdbcDriver.setFileName(driverFilename);
		jdbcDriver.setDelStatus(Integer.parseInt(driverDelStatus));
		jdbcDriver.save();	
		
		int newDriverId = jdbcDriver.getId();				
		JdbcDriver newDriver = dataAgent.getJdbcDriverById(newDriverId);
		if(newDriver == null){
			System.out.println("new jdbc driver object has not been loaded yet");
			for(;;){//Wait till commite new Database first
				newDriver = dataAgent.getJdbcDriverById(newDriverId);
				if(newDriver != null)
					break;
			}
		}			
	return newDriverId;
}

private int importEmailFromFile(org.w3c.dom.Element nodeEmail, IDataAgent dataAgent) throws MdnException, DataSourceException{
	  String emailAddress = nodeEmail.getAttribute(MdnEmailSetting.FLD_EMAIL_ADDRESS);
  	  String emailImapHost = nodeEmail.getAttribute(MdnEmailSetting.FLD_IMAP_HOST);
  	  String emailImapUserName = nodeEmail.getAttribute(MdnEmailSetting.FLD_IMAP_USER_NAME);		        
  	  String emailImapPassword = nodeEmail.getAttribute(MdnEmailSetting.FLD_IMAP_PASSWORD);
  	  String emailImapPort = nodeEmail.getAttribute(MdnEmailSetting.FLD_IMAP_PORT);
  	  String emailImapSSL = nodeEmail.getAttribute(MdnEmailSetting.FLD_IMAP_ENCRYPTED_TYPE);
  	  String emailSmtpHost = nodeEmail.getAttribute(MdnEmailSetting.FLD_SMTP_HOST);
  	  String emailSmtpUserName = nodeEmail.getAttribute(MdnEmailSetting.FLD_SMTP_USERNAME);
  	  String emailSmtpPassword = nodeEmail.getAttribute(MdnEmailSetting.FLD_SMTP_PASSWORD);
  	  String emailSmtpPort = nodeEmail.getAttribute(MdnEmailSetting.FLD_SMTP_PORT);
  	  String emailSmtpSSL = nodeEmail.getAttribute(MdnEmailSetting.FLD_SMTP_ENCRYPTED_TYPE);
  	  //String emailProjectId = nodeEmail.getAttribute(MdnEmailSetting.FLD_PROJECT_ID);
  	  String emailDelStatus = nodeEmail.getAttribute(MdnEmailSetting.FLD_DEL_STATUS);		
  	  
  	  MdnEmailSetting emailSett = new MdnEmailSetting();
		MdnEmailSetting existingEmail = dataAgent.getEmailByAddress(emailAddress);
		if (existingEmail != null){
			emailSett = existingEmail;
			emailSett.setState(DataObject.IN_DB);
		}else{
			emailSett.setState(DataObject.NEW);
		}
		
  	  emailSett.setEmailAddress(emailAddress);
		emailSett.setImapHost(emailImapHost);
		emailSett.setImapUserName(emailImapUserName);
		emailSett.setImapPassword(emailImapPassword);
		emailSett.setImapPort(emailImapPort);
		emailSett.setImapEncryptedType(Integer.parseInt(emailImapSSL));
		
		emailSett.setSmtpHost(emailSmtpHost);
		emailSett.setSmtpUsername(emailSmtpUserName);
		emailSett.setSmtpPassword(emailSmtpPassword);
		emailSett.setSmtpPort(emailSmtpPort);
		emailSett.setSmtpEncryptedType(Integer.parseInt(emailSmtpSSL));
		
		
		emailSett.setDelStatus(Integer.parseInt(emailDelStatus));
		emailSett.save();

		int newEmailId = emailSett.getId();				
		MdnEmailSetting newEmail = dataAgent.getEmailSettingById(newEmailId);
		if(newEmail == null){
			System.out.println("new email object has not been loaded yet");
			for(;;){//Wait till commite new Database first
				newEmail = dataAgent.getEmailSettingById(newEmailId);
				if(newEmail != null)
					break;
			}
		}
		return newEmailId;
}

private void importImSettings(org.w3c.dom.Element msgSettingRoot, IDataAgent dataAgent, HashMap<String, String> imIdMappings) throws NumberFormatException, MdnException, DataSourceException{
    NodeList imLst = msgSettingRoot.getElementsByTagName("im");
    org.w3c.dom.Element imRoot = (org.w3c.dom.Element) imLst.item(0);
    
    NodeList imsLst = imRoot.getElementsByTagName(IMConnection.ENT_IM_CONNECTION);
    for (int i=0; i < imsLst.getLength(); i++){
  	  org.w3c.dom.Element nodeImConn = (org.w3c.dom.Element) imsLst.item(i);		 
  	  String imId = nodeImConn.getAttribute(IMConnection.FLD_ID);
  	  String imName = nodeImConn.getAttribute(IMConnection.FLD_NAME);
  	  String imPassword = nodeImConn.getAttribute(IMConnection.FLD_PASSWORD);
  	  String imType = nodeImConn.getAttribute(IMConnection.FLD_TYPE);
  	  String imStatus = nodeImConn.getAttribute(IMConnection.FLD_STATUS);
  	  String imUserName = nodeImConn.getAttribute(IMConnection.FLD_USER_NAME);
  	  String imStatusDatetime = nodeImConn.getAttribute(IMConnection.FLD_STATUS_DATETIME);
  	  //String imProjectId = nodeImConn.getAttribute(IMConnection.FLD_PROJECT_ID);
  	  String imDelStatus = nodeImConn.getAttribute(IMConnection.FLD_DEL_STATUS);
	        
	        IMConnection newIMConnection = new IMConnection();
	        IMConnection existingIMConn = dataAgent.getImConnectionByTypeID(Integer.parseInt(imType));
	        if (existingIMConn != null){
	        	newIMConnection = existingIMConn;
	        	newIMConnection.setState(DataObject.IN_DB);
	        }else{
	        	newIMConnection.setState(DataObject.NEW);
	        }
	        
			newIMConnection.setStatus(Integer.parseInt(imStatus));
			newIMConnection.setDelStatus(Integer.parseInt(imDelStatus));
			newIMConnection.setName(imName);
			newIMConnection.setUserName(imUserName);			
			newIMConnection.setPassword(imPassword);
			newIMConnection.setType(Integer.parseInt(imType));
			//newIMConnection.setStatusDesc(new Date(imStatusDatetime));
			
			newIMConnection.save();
			
			int newImId = newIMConnection.getId();				
			IMConnection imConnection = dataAgent.getImConnectionByID(newImId);
			if(imConnection == null){
				System.out.println("new IMConnection object has not been loaded yet");
				for(;;){//Wait till commite new project first
					imConnection = dataAgent.getImConnectionByID(newImId);
					if(imConnection != null)
						break;
				}
			}	
			imIdMappings.put(imId, newImId+"");
    }	
}

private void importSettings(org.w3c.dom.Element documentRoot, IDataAgent dataAgent, HashMap<String, String> driverIdMappings, HashMap<String, String> emailIdMappings, HashMap<String, String> imIdMappings) throws MdnException, DataSourceException{
    NodeList Settings = documentRoot.getElementsByTagName("Settings");
    org.w3c.dom.Element SettingsRoot = (org.w3c.dom.Element) Settings.item(0);	
    
    NodeList languageLst = SettingsRoot.getElementsByTagName("Languages");
    org.w3c.dom.Element languageRoot = (org.w3c.dom.Element) languageLst.item(0);
    
    NodeList languagesLst = languageRoot.getElementsByTagName(LanguageDobj.ENT_LANGUAGE);
    for (int i = 0; i< languagesLst.getLength(); i++){
  	  org.w3c.dom.Element nodeLang = (org.w3c.dom.Element) languagesLst.item(i);
  	  //String langId = nodeLang.getAttribute(LanguageDobj.FLD_ID);
  	  String langName = nodeLang.getAttribute(LanguageDobj.FLD_NAME);
  	  String langFileName = nodeLang.getAttribute(LanguageDobj.FLD_FILE_NAME);
  	  String langDefault = nodeLang.getAttribute(LanguageDobj.FLD_DEFAULT);
  	  String langDelStatus = nodeLang.getAttribute(LanguageDobj.FLD_DEL_STATUS);
  	  
  	  LanguageDobj newLang = new LanguageDobj();
  	  LanguageDobj langWithSameName = dataAgent.getLanguageByName(langName);
			if (langWithSameName != null){	
				newLang = 	langWithSameName;	
				newLang.setState(DataObject.IN_DB);
				newLang.setDefault(Integer.parseInt(langDefault));
			}else{
				newLang.setState(DataObject.NEW);
				newLang.setDefault(0);//set new one to be not default
			}	    	  
  	  
		    newLang.setName(langName);
		    newLang.setFileName(langFileName);
		    
		    newLang.setDelStatus(Integer.parseInt(langDelStatus));
		    
		    newLang.save();		    	  
    }
    
    NodeList driverLst = SettingsRoot.getElementsByTagName("Drivers");
    org.w3c.dom.Element driverRoot = (org.w3c.dom.Element) driverLst.item(0);	
    
    NodeList driversLst = driverRoot.getElementsByTagName(JdbcDriver.ENT_JDBCDRIVER);
    for (int i = 0; i< driversLst.getLength(); i++){
  	  org.w3c.dom.Element nodeDriver = (org.w3c.dom.Element) driversLst.item(i);
  	  String driverId = nodeDriver.getAttribute(JdbcDriver.FLD_ID);
  	  int newDriverId = importDriverFromFile(nodeDriver, dataAgent);
  	  driverIdMappings.put(driverId, newDriverId+"");
    }
    
    NodeList msgSettingLst = SettingsRoot.getElementsByTagName("MessagingSettings");
    org.w3c.dom.Element msgSettingRoot = (org.w3c.dom.Element) msgSettingLst.item(0);	
    
    NodeList separatorsLst = msgSettingRoot.getElementsByTagName(MdnMessageSeparator.ENT_MSG_SEPARATOR);
    if (separatorsLst.getLength() > 0){
  	  org.w3c.dom.Element msgSepNode = (org.w3c.dom.Element) separatorsLst.item(0);
		    //msgSepNode.getAttribute(MdnMessageSeparator.FLD_ID);
		    //msgSepNode.getAttribute(MdnMessageSeparator.FLD_PROJECT_ID);
		    String conditionSeperator = msgSepNode.getAttribute(MdnMessageSeparator.FLD_CONDITION_SEPERATOR);

		    
	    	  //TODO
	    	  //THIS WILL OVERWRITE THE EXISTING ONE				    
		    MdnMessageSeparator msgSep = new MdnMessageSeparator();
			MdnMessageSeparator existingMsgSep = dataAgent.getMessageSeparator();
			if(msgSep != null){
				msgSep = existingMsgSep;
				msgSep.setState(DataObject.IN_DB);
			}else{
				msgSep.setState(DataObject.NEW);
			}
		    msgSep.setConditionSeperator(conditionSeperator);
		    msgSep.save();
		    
		    
    }
    
    NodeList msgControlsLst = msgSettingRoot.getElementsByTagName("MessageControls");
    org.w3c.dom.Element msgControlsRoot = (org.w3c.dom.Element) msgControlsLst.item(0);
    
    NodeList tempBlockLst = msgControlsRoot.getElementsByTagName(TemporaryBlockContacInfo.ENT_TEMP_BLOCK_INFO);
    if (tempBlockLst.getLength() > 0){
  	  org.w3c.dom.Element tempBlockNode = (org.w3c.dom.Element) tempBlockLst.item(0);
		  	//String tempBlockId = tempBlockNode.getAttribute(TemporaryBlockContacInfo.FLD_ID); 
		  	String tempBlockMaxMsg = tempBlockNode.getAttribute(TemporaryBlockContacInfo.FLD_MAX_MSG);
		  	String tempBlockMaxPeriod = tempBlockNode.getAttribute(TemporaryBlockContacInfo.FLD_MAX_PERIOD);
		  	String tempBlockCancelPeriod = tempBlockNode.getAttribute(TemporaryBlockContacInfo.FLD_CANCEL_PERIOD);
		  	String tempBlockReply = tempBlockNode.getAttribute(TemporaryBlockContacInfo.FLD_REPLY);	

	    	  //TODO
	    	  //THIS WILL OVERWRITE THE EXISTING ONE				  	
			TemporaryBlockContacInfo msgControlsInfo = dataAgent.getTempBlockContacts();
			
			TemporaryBlockContacInfo existingMsgControlsInfo = dataAgent.getTempBlockContacts(); 
			if (existingMsgControlsInfo != null){
				msgControlsInfo = existingMsgControlsInfo;
				msgControlsInfo.setState(DataObject.IN_DB);
			}else
				msgControlsInfo.setState(DataObject.NEW);
			
			msgControlsInfo.setMaxMessage(Integer.parseInt(tempBlockMaxMsg));
			msgControlsInfo.setMaxPeriod(tempBlockMaxPeriod);
			msgControlsInfo.setCancelPeriod(tempBlockCancelPeriod);	
			msgControlsInfo.setReply(tempBlockReply);
			msgControlsInfo.save();				  	
    }
    
    NodeList publicMsgInfoLst = msgControlsRoot.getElementsByTagName(MessagingSettingDetails.ENT_MSG_SETT_INFO);
    for (int i=0; i < publicMsgInfoLst.getLength(); i++){
  	  org.w3c.dom.Element publicMsgInfoNode = (org.w3c.dom.Element) publicMsgInfoLst.item(i);
  	  //String publicMsgInfoId = publicMsgInfoNode.getAttribute(GuestMsgObj.FLD_ID);
  	  String publicMsgInfoType = publicMsgInfoNode.getAttribute(MessagingSettingDetails.FLD_TYPE);
  	  String publicMsgInfoStatus = publicMsgInfoNode.getAttribute(MessagingSettingDetails.FLD_STATUS);
  	  String publicMsgInfoTotalMsgCount = publicMsgInfoNode.getAttribute(MessagingSettingDetails.FLD_TOTAL_MSG_COUNT);	
	    
  	  MessagingSettingDetails newPublicMsgInfo = new MessagingSettingDetails();
	    	    
  	  MessagingSettingDetails existingPublicMsgInfo = dataAgent.getGuestMsgInfo(publicMsgInfoType);		    	  
  	  //TODO
  	  //THIS WILL OVERWRITE THE EXISTING ONE
  	  if (existingPublicMsgInfo != null){
  		  newPublicMsgInfo = existingPublicMsgInfo;
  		  newPublicMsgInfo.setState(DataObject.IN_DB);
  	  }else
  		  newPublicMsgInfo.setState(DataObject.NEW);
  	  
  	  newPublicMsgInfo.setStatus(Integer.parseInt(publicMsgInfoStatus));
  	  //Nick: 09/06/2008 shouldn't import the number of public messages as the imported file might be going on a new installation
  	  //newPublicMsgInfo.setTotalMsgCount(Integer.parseInt(publicMsgInfoTotalMsgCount));
  	  newPublicMsgInfo.save();
    }
    
    NodeList blockContractLst = msgControlsRoot.getElementsByTagName("BlockContacts");
    org.w3c.dom.Element blockContractsRoot = (org.w3c.dom.Element) blockContractLst.item(0);
    
    NodeList blockContractsLst = blockContractsRoot.getElementsByTagName(BlockContacts.ENT_BLOCK_CONTACTS);
    for (int i=0; i < blockContractsLst.getLength(); i++){
  	  org.w3c.dom.Element nodeBc = (org.w3c.dom.Element) blockContractsLst.item(i);
  	  String bcType = nodeBc.getAttribute(BlockContacts.FLD_TYPE);
  	  String bcContact = nodeBc.getAttribute(BlockContacts.FLD_CONTACT);	 
  	  //String bcId = nodeBc.getAttribute(BlockContacts.FLD_ID);	
  	  
  	  BlockContacts blockContact = new BlockContacts();
  	  BlockContacts existingBlockContact = dataAgent.getBlockContact(bcType, bcContact);
  	  if (existingBlockContact == null){
				blockContact.setType(bcType);		    	  
				blockContact.setContact(bcContact);
				blockContact.setState(DataObject.NEW);
				blockContact.save();		    		  
  	  }else{
  		  //do nothing
  	  }
    }
    
    NodeList emailLst = msgSettingRoot.getElementsByTagName("Emails");
    org.w3c.dom.Element emailsRoot = (org.w3c.dom.Element) emailLst.item(0);
    
    NodeList emailsLst = emailsRoot.getElementsByTagName(MdnEmailSetting.ENT_EMAIL);
    for (int i=0; i < emailsLst.getLength(); i++){
  	  org.w3c.dom.Element nodeEmail = (org.w3c.dom.Element) emailsLst.item(i);
  	  String emailId = nodeEmail.getAttribute(MdnEmailSetting.FLD_ID);
  	  int newEmailId = importEmailFromFile(nodeEmail, dataAgent);
  	  emailIdMappings.put(emailId, newEmailId+"");
    }
    
    importImSettings(msgSettingRoot, dataAgent, imIdMappings);
    
    NodeList smsLst = msgSettingRoot.getElementsByTagName(MdnSmsSetting.ENT_SMS);
    if (smsLst.getLength() > 0){
  	  org.w3c.dom.Element smsSett = (org.w3c.dom.Element) smsLst.item(0);		      
  	  String smsSimNumber = smsSett.getAttribute(MdnSmsSetting.FLD_SIM_NUMBER);
  	  String smsComm = smsSett.getAttribute(MdnSmsSetting.FLD_COMM);
  	  String smsBaudrate = smsSett.getAttribute(MdnSmsSetting.FLD_BAUDRATE);
  	  String smsModemMan = smsSett.getAttribute(MdnSmsSetting.FLD_MODEM_MAN);
  	  String smsModemModel = smsSett.getAttribute(MdnSmsSetting.FLD_MODEM_MODEL);	
			MdnSmsSetting smsSetting = new MdnSmsSetting();
		
			MdnSmsSetting  existSmsSetting = dataAgent.getSmsSetting();
			if(existSmsSetting != null){
				smsSetting = existSmsSetting;
				smsSetting.setState(DataObject.IN_DB);
			}else
				smsSetting.setState(DataObject.NEW);
			
			smsSetting.setNumber(smsSimNumber);
			smsSetting.setComm(smsComm);
			smsSetting.setBaudrate(smsBaudrate);
			smsSetting.setModemManufacturer(smsModemMan);
			smsSetting.setModemModel(smsModemModel);
			smsSetting.save();
    }	
}

private Element importProjectFile(HttpServletRequest request){
	String action = request.getParameter("action");
	
	String filename = request.getParameter("filename");
	String path = LicenseManager.getImportFilePath();
	IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	String errorMessage = "ok";
	String langFile = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	try {
		File file = new File(path + filename);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		org.w3c.dom.Document doc = db.parse(file);
		doc.getDocumentElement().normalize();
		System.out.println("Root element " + doc.getDocumentElement().getNodeName());
		NodeList nodeLst = doc.getElementsByTagName("Project");
		System.out.println("Information of all Project");		  
	    Node fstNode = nodeLst.item(0);
	    
	    if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
	  
	      org.w3c.dom.Element documentRoot = (org.w3c.dom.Element) fstNode;
	      String includeSettinsStr = documentRoot.getAttribute("includeSettings");
	      String includeUsersGroupsStr = documentRoot.getAttribute("includeUsersGroups");
	      boolean includeSettings = includeSettinsStr.equalsIgnoreCase("true")? true : false;
	      HashMap<String, String> driverIdMappings = new HashMap<String, String>();
	      HashMap<String, String> emailIdMappings = new HashMap<String, String>();
	      HashMap<String, String> imIdMappings = new HashMap<String, String>();
	      if (includeSettings){
	    	  importSettings(documentRoot, dataAgent, driverIdMappings, emailIdMappings, imIdMappings);		      
	      }//if setting is included
	      
	      boolean includeUsersGroups = includeUsersGroupsStr.equalsIgnoreCase("true")? true: false;
	      
	      NodeList projectLst = documentRoot.getElementsByTagName(ProjectDobj.ENT_PROJECT);
	      
	      org.w3c.dom.Element projectRoot = (org.w3c.dom.Element) projectLst.item(0);
	      //String projectId = projectRoot.getAttribute(ProjectDobj.FLD_ID);
	      String projectName = projectRoot.getAttribute(ProjectDobj.FLD_NAME);
	      String projectDesc = projectRoot.getAttribute(ProjectDobj.FLD_DESCRIPTION);
	      String projectDelStatus = projectRoot.getAttribute(ProjectDobj.FLD_DEL_STATUS);
	      
	      //Save project
			ProjectDobj project = new ProjectDobj();
			project.setName(projectName);
			project.setDescription(projectDesc);
			project.setDelStatus(projectDelStatus);
			project.setState(DataObject.NEW);
			project.save();
			
			int newProjectId = project.getId();				
			ProjectDobj newProject = dataAgent.getNavProjectById(newProjectId);
			if(newProject == null){
				System.out.println("new Project object has not been loaded yet");
				for(;;){//Wait till commite new project first
					newProject = dataAgent.getNavProjectById(newProjectId);
					if(newProject != null)
						break;
				}
			}	      
		HashMap<String, String> dbIdMappings = new HashMap<String, String>();  
		HashMap<String, String> tableIdMappings = new HashMap<String, String>();
		HashMap<String, String> viewIdMappings = new HashMap<String, String>();
		HashMap<String, String> tableFieldIdMappings = new HashMap<String, String>();
		HashMap<String, String> viewFieldIdMappings = new HashMap<String, String>();
		
	      NodeList dbLst = projectRoot.getElementsByTagName(DataSourceDobj.ENT_DATASOURCE);
	      for (int i = 0; i< dbLst.getLength(); i++){
		      org.w3c.dom.Element dbNode = (org.w3c.dom.Element) dbLst.item(i);
		      String dbId = dbNode.getAttribute(DataSourceDobj.FLD_ID);
		      String dbName = dbNode.getAttribute(DataSourceDobj.FLD_NAME);
		      String dbDesc = dbNode.getAttribute(DataSourceDobj.FLD_DESCRIPTION);
		      //String dbProjectId = dbNode.getAttribute(DataSourceDobj.FLD_PROJECT_ID);
		      String dbDelStatus = dbNode.getAttribute(DataSourceDobj.FLD_DEL_STATUS);
			  String dbJdbcUser = dbNode.getAttribute(DataSourceDobj.FLD_JDBC_USER);
			  String dbJdbcPassword = dbNode.getAttribute(DataSourceDobj.FLD_JDBC_PASSWORD);
			  String dbJdbcDriverId = dbNode.getAttribute(DataSourceDobj.FLD_JDBC_DRIVERID);
			  String dbJdbcUrl = dbNode.getAttribute(DataSourceDobj.FLD_JDBC_URL);
			  
			  //Save Datasource
				//Create JdbcDataSourceDobj from DbConnection
				JdbcDataSourceDobj jdbcDataSourceDobj = new JdbcDataSourceDobj();
				jdbcDataSourceDobj.setName(dbName);
				jdbcDataSourceDobj.setDescription(dbDesc);
				jdbcDataSourceDobj.setJdbcUrl(dbJdbcUrl);
				jdbcDataSourceDobj.setJdbcUser(dbJdbcUser);
				jdbcDataSourceDobj.setJdbcPassword(dbJdbcPassword);				
				jdbcDataSourceDobj.setProjectId(newProjectId);

		        //Insert into database
				jdbcDataSourceDobj.setState(DataObject.NEW);
				jdbcDataSourceDobj.setDelStatus(Integer.parseInt(dbDelStatus));

				//set jdbcdriver id
				String newDriverId = null;
				if (!includeSettings){
					NodeList driverLst = dbNode.getElementsByTagName(JdbcDriver.ENT_JDBCDRIVER);
					if (driverLst.getLength() > 1){
						org.w3c.dom.Element nodeDriver = (org.w3c.dom.Element) driverLst.item(0);
						if (nodeDriver != null)
							newDriverId = importDriverFromFile(nodeDriver, dataAgent)+"";	
					}
				}else{
					newDriverId = driverIdMappings.get(dbJdbcDriverId);
				}
				if (newDriverId != null)
					jdbcDataSourceDobj.setJdbcDriverId(Integer.parseInt(newDriverId));
				
				//save database connection
				jdbcDataSourceDobj.save();			  
				  
				int newDSId = jdbcDataSourceDobj.getId();				
				DataSourceDobj newDB = dataAgent.getDbConnectionByID(newProjectId, newDSId);
				if(newDB == null){
					System.out.println("new Database object has not been loaded yet");
					for(;;){//Wait till commite new Database first
						newDB = dataAgent.getDbConnectionByID(newProjectId, newDSId);
						if(newDB != null)
							break;
					}
				}
				dbIdMappings.put(dbId, newDSId+"");
				
				
			  NodeList entityLst = dbNode.getElementsByTagName(EntityDobj.ENT_ENTITY);
			  for (int j=0; j<entityLst.getLength(); j++){
				  org.w3c.dom.Element tableNode = (org.w3c.dom.Element) entityLst.item(j);
				  String entityId = tableNode.getAttribute(EntityDobj.FLD_ID);
				  String entityName = tableNode.getAttribute(EntityDobj.FLD_NAME);
				  String entityDesc = tableNode.getAttribute(EntityDobj.FLD_DESCRIPTION);
				  //String entityDSId = tableNode.getAttribute(EntityDobj.FLD_DSID);
				  String entityFlags = tableNode.getAttribute(EntityDobj.FLD_FLAGS);
				  
				  EntityDobj ed = new EntityDobj();
		        	ed.setName(entityName);
		        	ed.setDescription(entityDesc);
		        	ed.setFlags(Integer.parseInt(entityFlags));
					// if new set keys
		            ed.setDataSourceId (newDSId);		        	
					// save entity/table
		            ed.save ();

					int newEntityId = ed.getId();				
					EntityDobj newEntity = dataAgent.getMetaTableByID(newEntityId, false);
					if(newEntity == null){
						System.out.println("new Entity object has not been loaded yet");
						for(;;){//Wait till commite new Entity first
							newEntity = dataAgent.getMetaTableByID(newEntityId, false);
							if(newEntity != null)
								break;
						}
					}	
					
					tableIdMappings.put(entityId, newEntityId + "");	
		            
				  NodeList fieldLst = tableNode.getElementsByTagName(FieldDobj.ENT_FIELD);
				  
				  for (int m=0; m<fieldLst.getLength(); m++){
					  org.w3c.dom.Element fieldNode = (org.w3c.dom.Element) fieldLst.item(m);
					  String fieldId = fieldNode.getAttribute(FieldDobj.FLD_ID);
					  String fieldName = fieldNode.getAttribute(FieldDobj.FLD_NAME);
					  String fieldFlags = fieldNode.getAttribute(FieldDobj.FLD_FLAGS);
					  String fieldType = fieldNode.getAttribute(FieldDobj.FLD_TYPE);
					  String fieldNativeType = fieldNode.getAttribute(FieldDobj.FLD_NATIVETYPE);
					  String fieldDesc = fieldNode.getAttribute(FieldDobj.FLD_DESCRIPTION);
					  //String fieldDSId = fieldNode.getAttribute(FieldDobj.FLD_DSID);
					  //String fieldEnityId = fieldNode.getAttribute(FieldDobj.FLD_ENTITYID);
					  String fieldColumnSize = fieldNode.getAttribute(FieldDobj.FLD_COLUMN_SIZE);
					  String fieldDecimalDigits = fieldNode.getAttribute(FieldDobj.FLD_DECIMAL_DIGITS);

		                FieldDobj fd = new FieldDobj();
		                fd.setName(fieldName);
		                fd.setFlags(Integer.parseInt(fieldFlags));
		                fd.setType(Integer.parseInt(fieldType));
		                fd.setNativeType(Integer.parseInt(fieldNativeType));
		                fd.setDescription(fieldDesc);
		                fd.setDsId(newDSId);
		                fd.setEntityId(newEntityId);
		                fd.setColumnSize(Integer.parseInt(fieldColumnSize));
		                fd.setDecimalDigits(Integer.parseInt(fieldDecimalDigits));
		                fd.save();		
		                
		                tableFieldIdMappings.put(fieldId, fd.getId()+"");
				  }
			  }
			  
			  NodeList viewLst = dbNode.getElementsByTagName(DataView.ENT_DATAVIEW);
			  for (int j=0; j<viewLst.getLength(); j++){
				  org.w3c.dom.Element viewNode = (org.w3c.dom.Element) viewLst.item(j);
				  String viewId = viewNode.getAttribute(DataView.FLD_ID);
				  String viewName = viewNode.getAttribute(DataView.FLD_NAME);
				  String viewDesc = viewNode.getAttribute(DataView.FLD_DESCRIPTION);
				  //String viewDSId = viewNode.getAttribute(DataView.FLD_SOURCE_DSID);
				  String viewDelStatus = viewNode.getAttribute(DataView.FLD_DEL_STATUS);
				  
		            // create the entity
					DataView newView = new DataView();	
		            newView.setName(viewName);
		            newView.setDescription(viewDesc);
		            newView.setSourceDsId(newDSId);				  
	                newView.setState(DataObject.NEW);
	                newView.setDelStatus(Integer.parseInt(viewDelStatus));
	                newView.save();

					int newViewId = newView.getId();				
					DataView newViewObj = dataAgent.getMetaViewByID(newViewId, false);
					if(newViewObj == null){
						System.out.println("new View object has not been loaded yet");
						for(;;){//Wait till commite new View first
							newViewObj = dataAgent.getMetaViewByID(newViewId, false);
							if(newViewObj != null)
								break;
						}
					}
					
					viewIdMappings.put(viewId, newViewId+"");
	                
				  NodeList fieldLst = viewNode.getElementsByTagName(DataViewField.ENT_DVFIELD);
				  for (int m=0; m<fieldLst.getLength(); m++){
					  org.w3c.dom.Element fieldNode = (org.w3c.dom.Element) fieldLst.item(m);
					  String fieldId = fieldNode.getAttribute(DataViewField.FLD_ID);
					  String fieldName = fieldNode.getAttribute(DataViewField.FLD_NAME);
					  //String fieldDVId = fieldNode.getAttribute(DataViewField.FLD_DATAVIEWID);
					  String fieldDesc = fieldNode.getAttribute(DataViewField.FLD_DESCRIPTION);
					  String fieldSourceField = fieldNode.getAttribute(DataViewField.FLD_SOURCE_FIELD);
					  String fieldSourceEntity = fieldNode.getAttribute(DataViewField.FLD_SOURCE_ENTITY);
					  String fieldDisplayName = fieldNode.getAttribute(DataViewField.FLD_DISPLAY_NAME);
					  String fieldFlags = fieldNode.getAttribute(DataViewField.FLD_FLAGS);
					  String fieldOptionList = fieldNode.getAttribute(DataViewField.FLD_OPTION_LIST);
					  String fieldType = fieldNode.getAttribute(DataViewField.FLD_TYPE);					  

	                	DataViewField dvf = new DataViewField();
	                    dvf.setName(fieldName);
	                    dvf.setDescription(fieldDesc);
	                    dvf.setDisplayName(fieldDisplayName);
	                    dvf.setSourceField(fieldSourceField);
	                    dvf.setSourceEntity(fieldSourceEntity);
	                    dvf.setType(Integer.parseInt(fieldType));	
	                    dvf.setDataViewId(newViewId);
	                    dvf.setFlags(Integer.parseInt(fieldFlags));
	                    dvf.setOptionList(fieldOptionList);
	                    dvf.save();
	                    viewFieldIdMappings.put(fieldId, dvf.getId() +"");
				  }
			  }
	      }
	      
	      NodeList msgLst = projectRoot.getElementsByTagName(QueryDobj.ENT_QUERY);
	      for (int i = 0; i< msgLst.getLength(); i++){
	    	  org.w3c.dom.Element msgNode = (org.w3c.dom.Element) msgLst.item(i);
	    	  String msgId = msgNode.getAttribute(QueryDobj.FLD_ID);
	    	  String msgName = msgNode.getAttribute(QueryDobj.FLD_NAME);
	    	  String msgParentId = msgNode.getAttribute(QueryDobj.FLD_PARENTID);
	    	  String msgType = msgNode.getAttribute(QueryDobj.FLD_TYPE);
	    	  String msgCriteria = msgNode.getAttribute(QueryDobj.FLD_CRITERIA);
	    	  String msgSorts = msgNode.getAttribute(QueryDobj.FLD_SORTS);
	    	  String msgDesc = msgNode.getAttribute(QueryDobj.FLD_DESCRIPTION);
	    	  String msgGroupFieldId = msgNode.getAttribute(QueryDobj.FLD_GROUPFIELDID);
	    	  String msgRawQuery = msgNode.getAttribute(QueryDobj.FLD_RAWQUERY);
	    	  String msgEmailKeyword = msgNode.getAttribute(QueryDobj.FLD_EMAIL_KEYWORD);
	    	  String msgEmailAddressId = msgNode.getAttribute(QueryDobj.FLD_EMAIL_ADDRESS_ID);
	    	  String msgEmailDisplayResult = msgNode.getAttribute(QueryDobj.FLD_EMAIL_DISPLAY_RESULT);
	    	  String msgMobileStatus = msgNode.getAttribute(QueryDobj.FLD_MOBILE_STATUS);
	    	  String msgMobileDisplayResult = msgNode.getAttribute(QueryDobj.FLD_MOBILE_DISPLAY_RESULT);
	    	  String msgImStatus = msgNode.getAttribute(QueryDobj.FLD_IM_STATUS);
	    	  String msgImDisplayResult = msgNode.getAttribute(QueryDobj.FLD_IM_DISPLAY_RESULT);
	    	  String msgImKeyword = msgNode.getAttribute(QueryDobj.FLD_IM_KEYWORD);
	    	  String msgDelStatus = msgNode.getAttribute(QueryDobj.FLD_DEL_STATUS);
	    	  String msgSmsKeyword = msgNode.getAttribute(QueryDobj.FLD_SMS_KEYWORD);
	    	  String msgDBId = msgNode.getAttribute(QueryDobj.FLD_DB_ID);
	    	  String msgConditionSeperator = msgNode.getAttribute(QueryDobj.FLD_CONDITION_SEPERATOR);
	    	  String msgResponse = msgNode.getAttribute(QueryDobj.FLD_RESPONSE);
	    	  String msgTimeout = msgNode.getAttribute(QueryDobj.FLD_TIMEOUT);
	    	  String msgDsStatus = msgNode.getAttribute(QueryDobj.FLD_DS_STATUS);
	    	  String msgWSId = msgNode.getAttribute(QueryDobj.FLD_WS_ID);
	    	  //String msgProjectId = msgNode.getAttribute(QueryDobj.FLD_PROJECT_ID);	 
	    	  
	    	  QueryDobj msgQuery = new QueryDobj();
	    	  msgQuery.setName(msgName);
	    	  msgQuery.setDescription(msgDesc);
	    	  msgQuery.setType(msgType);
	    	  String newParentId = null;
	    	  if (msgType.equalsIgnoreCase("select")){
	    		  newParentId = viewIdMappings.get(msgParentId);
	    	  }else{
	    		  newParentId = tableIdMappings.get(msgParentId);
	    	  }
	    	  msgQuery.setViewOrTableId(Integer.parseInt(newParentId));
	    	  
	    	  msgQuery.setCriteriaString(msgCriteria);
	    	  msgQuery.setSortString(msgSorts);
	    	  
	    	  if (msgGroupFieldId != null && !msgGroupFieldId.isEmpty() && Integer.parseInt(msgGroupFieldId) != 0){
		    	  String newFieldId = viewFieldIdMappings.get(msgGroupFieldId);
		    	  msgQuery.setGroupFieldId(Integer.parseInt(newFieldId));
	    	  }
	    	  msgQuery.setWebServiceId(Integer.parseInt(msgWSId));
	    	  
	    	  String newDbId = dbIdMappings.get(msgDBId);
	    	  msgQuery.setDatabaseId(Integer.parseInt(newDbId));
	    	  
	    	  
	    	  msgQuery.setEmailKeyword(msgEmailKeyword);
	    		msgQuery.setSmsKeyword(msgSmsKeyword);
	    		msgQuery.setImKeyword(msgImKeyword);		    	
		    	//msgQuery.setEmailAddressId();
		    	msgQuery.setMobileStatus(Integer.parseInt(msgMobileStatus));
		    	msgQuery.setImStatus(Integer.parseInt(msgImStatus));
		    	
		    	//Default value for display result is 1
		    	msgQuery.setEmailDisplayResult(Integer.parseInt(msgEmailDisplayResult));
		    	msgQuery.setMobileDisplayResult(Integer.parseInt(msgMobileDisplayResult));
		    	msgQuery.setImDisplayResult(Integer.parseInt(msgImDisplayResult));
		    	
		    	msgQuery.setResponse(msgResponse);
		    	msgQuery.setConditionSeperator(msgConditionSeperator);
		    	msgQuery.setProjectId(newProjectId);
		    	msgQuery.setDatasourceStatus(Integer.parseInt(msgDsStatus));
		    	msgQuery.setTimeout(msgTimeout);
		    	msgQuery.setDelStatus(Integer.parseInt(msgDelStatus));
		    	msgQuery.setState(DataObject.NEW);
		    	
		    	String newEmailId = null;
		    	if (!includeSettings){
		    		NodeList emailLst = msgNode.getElementsByTagName(MdnEmailSetting.ENT_EMAIL);
		    		org.w3c.dom.Element nodeEmail = (org.w3c.dom.Element) emailLst.item(0);
		    		if (nodeEmail != null){
		    			newEmailId = importEmailFromFile(nodeEmail, dataAgent)+"";
		    		}
		    	}else{
		    		newEmailId = emailIdMappings.get(msgEmailAddressId);
		    	}
		    	if (newEmailId != null)
		    		msgQuery.setEmailAddressId(Integer.parseInt(newEmailId));
				msgQuery.save();	    	  

				int newQueryId = msgQuery.getId();				
				QueryDobj newQueryObj = dataAgent.getQueryByID(newQueryId);
				if(newQueryObj == null){
					System.out.println("new Query object has not been loaded yet");
					for(;;){//Wait till commite new View first
						newQueryObj = dataAgent.getQueryByID(newQueryId);
						if(newQueryObj != null)
							break;
					}
				}				
				
	    	  NodeList criteriaLst = msgNode.getElementsByTagName(QueryCriteriaDobj.ENT_QUERY_CRITERIA);
	    	  for (int j=0; j<criteriaLst.getLength(); j++){
				  org.w3c.dom.Element qcNode = (org.w3c.dom.Element) criteriaLst.item(j);
				  //String qcId = qcNode.getAttribute(QueryCriteriaDobj.FLD_ID);
				  String qcQueryId = qcNode.getAttribute(QueryCriteriaDobj.FLD_QUERY_ID);
				  String qcValueOrCond = qcNode.getAttribute(QueryCriteriaDobj.FLD_VALUEORCOND);
				  String qcRowNo = qcNode.getAttribute(QueryCriteriaDobj.FLD_ROW_NO);
				  String qcType = qcNode.getAttribute(QueryCriteriaDobj.FLD_TYPE);
				  String qcUsed = qcNode.getAttribute(QueryCriteriaDobj.FLD_USED);
				  String qcIndent = qcNode.getAttribute(QueryCriteriaDobj.FLD_INDENT);
				  String qcParent = qcNode.getAttribute(QueryCriteriaDobj.FLD_PARENT);
				  String qcNumber = qcNode.getAttribute(QueryCriteriaDobj.FLD_NUMBER);
				  String qcName = qcNode.getAttribute(QueryCriteriaDobj.FLD_NAME);
				  String qcCompId = qcNode.getAttribute(QueryCriteriaDobj.FLD_COMP_ID);
				  String qcComparison = qcNode.getAttribute(QueryCriteriaDobj.FLD_COMPARISON);
				  String qcValue = qcNode.getAttribute(QueryCriteriaDobj.FLD_VALUE);
				  String qcConnection = qcNode.getAttribute(QueryCriteriaDobj.FLD_CONNECTION);
				  String qcValue2 = qcNode.getAttribute(QueryCriteriaDobj.FLD_VALUE2);
				  String qcGrouping = qcNode.getAttribute(QueryCriteriaDobj.FLD_GROUPING);
				  String qcV1UISeq = qcNode.getAttribute(QueryCriteriaDobj.FLD_VALUE_USERINPUT_SEQ);
				  String qcV2UISeq = qcNode.getAttribute(QueryCriteriaDobj.FLD_VALUE2_USERINPUT_SEQ);
				  String qcObjectType = qcNode.getAttribute(QueryCriteriaDobj.FLD_OBJECT_TYPE);	
				  
				  if (qcQueryId.equalsIgnoreCase(msgId)){
	              	// create a Query Criteria object
	              	QueryCriteriaDobj queryCriteriaObj = new QueryCriteriaDobj();
	              	
	              	queryCriteriaObj.setQueryId(newQueryId);
	              	queryCriteriaObj.setValueOrCondition(qcValueOrCond);
					  queryCriteriaObj.setRowNo(Integer.parseInt(qcRowNo));
					  queryCriteriaObj.setType(qcType);
					  queryCriteriaObj.setUsed(Integer.parseInt(qcUsed));
					  queryCriteriaObj.setIndent(Integer.parseInt(qcIndent));
					  queryCriteriaObj.setParent(qcParent);
					  queryCriteriaObj.setNumber(Integer.parseInt(qcNumber));
					  queryCriteriaObj.setName(qcName);
					  queryCriteriaObj.setCompId(Integer.parseInt(qcCompId));
					  queryCriteriaObj.setComparison(qcComparison);
					  queryCriteriaObj.setValue(qcValue);
					  queryCriteriaObj.setConnection(qcConnection);
					  queryCriteriaObj.setValue2(qcValue2);
	                queryCriteriaObj.setGrouping(qcGrouping);
	                  
	              	queryCriteriaObj.setValueUserInputSeq(qcV1UISeq);
	              
	              	queryCriteriaObj.setValue2UserInputSeq(qcV2UISeq);
	              	queryCriteriaObj.setObjectType(qcObjectType);
	                  queryCriteriaObj.setState(DataObject.NEW);
	                  queryCriteriaObj.save();	
				  }
	    	  }// for (int j=0; j<criteriaLst.getLength(); j++){
	    	  
	    	  HashMap<String, String> urIdMappings = new HashMap<String, String>(); 
	    	  //HashMap<String, String> urIdMappingsReverse = new HashMap<String, String>(); 
	    	  
	    	  NodeList urLst = msgNode.getElementsByTagName(UserReply.ENT_USER_REPLY);
	    	  for (int j=0; j<urLst.getLength(); j++){
	    		  org.w3c.dom.Element urNode = (org.w3c.dom.Element) urLst.item(j);
	    		  String urId = urNode.getAttribute(UserReply.FLD_ID);
	    		  String urType = urNode.getAttribute(UserReply.FLD_TYPE);
	    		  String urViewTableId = urNode.getAttribute(UserReply.FLD_VIEW_TABLE_ID);
	    		  String urDBId = urNode.getAttribute(UserReply.FLD_DB_ID);
	    		  String urCriteria = urNode.getAttribute(UserReply.FLD_CRITERIA);
	    		  String urSorts = urNode.getAttribute(UserReply.FLD_SORTS);
	    		  String urGroupFieldId = urNode.getAttribute(UserReply.FLD_GROUPFIELDID);
	    		  String urParentId = urNode.getAttribute(UserReply.FLD_PARENT_ID);
	    		  String urMsgText = urNode.getAttribute(UserReply.FLD_MSG_TEXT);
	    		  String urTimeout = urNode.getAttribute(UserReply.FLD_TIMEOUT);
	    		  String urResponse = urNode.getAttribute(UserReply.FLD_RESPONSE);
	    		  //String urProjectId = urNode.getAttribute(UserReply.FLD_PROJECT_ID);
	    		  String urDesc = urNode.getAttribute(UserReply.FLD_DESCIPTION);
	    		  //String urQueryParentId = urNode.getAttribute(UserReply.FLD_QUERY_PARENT_ID);
	    		  String urDisplayResult = urNode.getAttribute(UserReply.FLD_DISPLAY_RESULT);
	    		  String urDSStatus = urNode.getAttribute(UserReply.FLD_DS_STATUS);
	    		  String urWSId = urNode.getAttribute(UserReply.FLD_WS_ID);
	    		  String urDelStatus = urNode.getAttribute(UserReply.FLD_DEL_STATUS);

	    		  UserReply urQuery = new UserReply();
		    	  urQuery.setDescription(urDesc);
		    	  urQuery.setType(urType);
		    	  String newURViewTableId = null;
		    	  if (urType.equalsIgnoreCase("select")){
		    		  newURViewTableId = viewIdMappings.get(urViewTableId);
		    	  }else{
		    		  newURViewTableId = tableIdMappings.get(urViewTableId);
		    	  }
		    	  urQuery.setViewOrTableId(Integer.parseInt(newURViewTableId));
		    	  
		    	  String newURDbId = dbIdMappings.get(urDBId);
		    	  urQuery.setDatabaseId(Integer.parseInt(newURDbId));
		    	  if (urParentId.equalsIgnoreCase(msgId)){
		    		  urQuery.setParentId(newQueryId);
		    	  }
		    	  
		    	  
		    	  urQuery.setCriteriaString(urCriteria);
		    	  urQuery.setSortString(urSorts);
		    	  
		    	  if (urGroupFieldId != null && !urGroupFieldId.isEmpty() && Integer.parseInt(urGroupFieldId) != 0){
			    	  String newURFieldId = viewFieldIdMappings.get(urGroupFieldId);
			    	  urQuery.setGroupFieldId(Integer.parseInt(newURFieldId));
		    	  }
		    	  
		    	  urQuery.setWebServiceId(Integer.parseInt(urWSId));
		    	  
		    	  
		    	  urQuery.setQueryId(newQueryId);
		    	  urQuery.setMsgText(urMsgText);
		    	  urQuery.setDisplayResult(Integer.parseInt(urDisplayResult));
			    	
		    	  urQuery.setResponse(urResponse);
		    	  
			    	urQuery.setProjectId(newProjectId);
			    	urQuery.setDatasourceStatus(Integer.parseInt(urDSStatus));
			    	urQuery.setTimeout(urTimeout);
			    	urQuery.setDelStatus(Integer.parseInt(urDelStatus));
			    	urQuery.setState(DataObject.NEW);
					urQuery.save();	    	  

					int newURQueryId = urQuery.getId();				
					UserReply newURQuery = dataAgent.getUserReplyById(newURQueryId);
					if(newURQuery == null){
						System.out.println("new Query object has not been loaded yet");
						for(;;){//Wait till commite new View first
							newURQuery = dataAgent.getUserReplyById(newURQueryId);
							if(newURQuery != null)
								break;
						}
					}	
					urIdMappings.put(urId, newURQueryId+"");
					//urIdMappingsReverse.put(newURQueryId+"", urId);
	    		  
		    	  NodeList urCriteriaLst = urNode.getElementsByTagName(QueryCriteriaDobj.ENT_QUERY_CRITERIA);
		    	  for (int m=0; m<urCriteriaLst.getLength(); m++){
					  org.w3c.dom.Element qcNode = (org.w3c.dom.Element) urCriteriaLst.item(m);
					  //String qcId = qcNode.getAttribute(QueryCriteriaDobj.FLD_ID);
					  String qcQueryId = qcNode.getAttribute(QueryCriteriaDobj.FLD_QUERY_ID);
					  String qcValueOrCond = qcNode.getAttribute(QueryCriteriaDobj.FLD_VALUEORCOND);
					  String qcRowNo = qcNode.getAttribute(QueryCriteriaDobj.FLD_ROW_NO);
					  String qcType = qcNode.getAttribute(QueryCriteriaDobj.FLD_TYPE);
					  String qcUsed = qcNode.getAttribute(QueryCriteriaDobj.FLD_USED);
					  String qcIndent = qcNode.getAttribute(QueryCriteriaDobj.FLD_INDENT);
					  String qcParent = qcNode.getAttribute(QueryCriteriaDobj.FLD_PARENT);
					  String qcNumber = qcNode.getAttribute(QueryCriteriaDobj.FLD_NUMBER);
					  String qcName = qcNode.getAttribute(QueryCriteriaDobj.FLD_NAME);
					  String qcCompId = qcNode.getAttribute(QueryCriteriaDobj.FLD_COMP_ID);
					  String qcComparison = qcNode.getAttribute(QueryCriteriaDobj.FLD_COMPARISON);
					  String qcValue = qcNode.getAttribute(QueryCriteriaDobj.FLD_VALUE);
					  String qcConnection = qcNode.getAttribute(QueryCriteriaDobj.FLD_CONNECTION);
					  String qcValue2 = qcNode.getAttribute(QueryCriteriaDobj.FLD_VALUE2);
					  String qcGrouping = qcNode.getAttribute(QueryCriteriaDobj.FLD_GROUPING);
					  String qcV1UISeq = qcNode.getAttribute(QueryCriteriaDobj.FLD_VALUE_USERINPUT_SEQ);
					  String qcV2UISeq = qcNode.getAttribute(QueryCriteriaDobj.FLD_VALUE2_USERINPUT_SEQ);
					  String qcObjectType = qcNode.getAttribute(QueryCriteriaDobj.FLD_OBJECT_TYPE);	
					  
		              	// create a Query Criteria object
					  if (qcQueryId.equalsIgnoreCase(urId)){
		              	QueryCriteriaDobj queryCriteriaObj = new QueryCriteriaDobj();
		              	
		              	queryCriteriaObj.setQueryId(newURQueryId);
		              	queryCriteriaObj.setValueOrCondition(qcValueOrCond);
						  queryCriteriaObj.setRowNo(Integer.parseInt(qcRowNo));
						  queryCriteriaObj.setType(qcType);
						  queryCriteriaObj.setUsed(Integer.parseInt(qcUsed));
						  queryCriteriaObj.setIndent(Integer.parseInt(qcIndent));
						  queryCriteriaObj.setParent(qcParent);
						  queryCriteriaObj.setNumber(Integer.parseInt(qcNumber));
						  queryCriteriaObj.setName(qcName);
						  queryCriteriaObj.setCompId(Integer.parseInt(qcCompId));
						  queryCriteriaObj.setComparison(qcComparison);
						  queryCriteriaObj.setValue(qcValue);
						  queryCriteriaObj.setConnection(qcConnection);
						  queryCriteriaObj.setValue2(qcValue2);
		                queryCriteriaObj.setGrouping(qcGrouping);
		                  
		              	queryCriteriaObj.setValueUserInputSeq(qcV1UISeq);
		              
		              	queryCriteriaObj.setValue2UserInputSeq(qcV2UISeq);
		              	queryCriteriaObj.setObjectType(qcObjectType);
		                  queryCriteriaObj.setState(DataObject.NEW);
		                  queryCriteriaObj.save();	
					  }
		    	  }
	    	  }
	    	  NodeList urLstAgain = msgNode.getElementsByTagName(UserReply.ENT_USER_REPLY);
	    	  for (int j=0; j<urLstAgain.getLength(); j++){
	    		  org.w3c.dom.Element urNode = (org.w3c.dom.Element) urLstAgain.item(j);
	    		  String urParentId = urNode.getAttribute(UserReply.FLD_PARENT_ID);
	    		  String urChildren = urNode.getAttribute(UserReply.FLD_CHILDREN);
	    		  
	    		  String urId = urNode.getAttribute(UserReply.FLD_ID);
	    		  
	    		  String newURQueryId = urIdMappings.get(urId);
	    		  UserReply newURQuery = dataAgent.getUserReplyById(Integer.parseInt(newURQueryId));
	    		  String newURParentId = null;
	    		  if (urParentId != null && !urParentId.isEmpty() && Integer.parseInt(urParentId) != 0){
	    			  newURParentId = urIdMappings.get(urParentId);
	    		  }
	    		  List<String> childrenList2 = new XmlFormatter().parsChildrenId(urChildren);//Pars children list
	    		  String newURChildren = "";
	    		  for (int m=0; m<childrenList2.size() ; m++) {//2	
						String childId = childrenList2.get(m);
						String newURChildId = urIdMappings.get(childId);
						newURChildren += newURChildId + ",";
	    		  }
	    		  
	    		  if (newURChildren.length() > 1){
	    			  newURChildren = newURChildren.substring(0, newURChildren.length()-1);
	    		  }
	    		  
	    		  if (!urParentId.equalsIgnoreCase(msgId)){
	    			  newURQuery.setParentId(newURParentId == null? 0 : Integer.parseInt(newURParentId));
		    	  }
	    		  
	    		  newURQuery.setChildren(newURChildren);
	    		  newURQuery.setState(DataObject.IN_DB);
	    		  newURQuery.save();
	    	  }
	    	  
	      }
	      
	      if (includeUsersGroups){
	    	  importUsersGroups(documentRoot, dataAgent, errorMessage, langFile, newProjectId, viewFieldIdMappings, includeSettings, imIdMappings); 
	      }
	      	      
	    }
	    
	    
		  
	} catch (ParserConfigurationException e) {
		e.printStackTrace();
		return new XmlFormatter().simpleResult(action, "ParserConfigurationException: " + e.toString());
	} catch (SAXException e) {
		e.printStackTrace();
		return new XmlFormatter().simpleResult(action, "SAXException: " + e.toString());
	} catch (IOException e) {
		e.printStackTrace();
		return new XmlFormatter().simpleResult(action, "IOException: " + e.toString());
	} catch (DataSourceException e) {
		e.printStackTrace();
		return new XmlFormatter().simpleResult(action, "DataSourceException: " + e.toString());
	} catch (MdnException e) {
		e.printStackTrace();
		return new XmlFormatter().simpleResult(action, e.toString());
	}	
	return new XmlFormatter().simpleResultWithErrorMsg(langFile, action,"ok", errorMessage);
}

	private void importUsersGroups(org.w3c.dom.Element documentRoot, IDataAgent dataAgent, String errorMessage, String langFile, 
				 int newProjectId, HashMap<String, String> viewFieldIdMappings, boolean includeSettings, HashMap<String, String> imIdMappings) throws MdnException, DataSourceException{
  	  
		HashMap<String, String> userIdMappings = new HashMap<String, String>();
	  HashMap<String, String> groupIdMappings = new HashMap<String, String>();
	  
      NodeList users = documentRoot.getElementsByTagName("Users");
      org.w3c.dom.Element userRoot = (org.w3c.dom.Element) users.item(0);

      if (!includeSettings){
	        importImSettings(userRoot, dataAgent, imIdMappings);
		}    
      
      NodeList customFieldLst = userRoot.getElementsByTagName("CustomFields");
      org.w3c.dom.Element customFieldRoot = (org.w3c.dom.Element) customFieldLst.item(0);
      
      HashMap<String, String> customFieldIdMappings = new HashMap<String, String>();
      NodeList customFieldsLst = customFieldRoot.getElementsByTagName(CustomField.ENT_CUSTOM_FIELD);
      for (int i = 0; i< customFieldsLst.getLength(); i++){
	      org.w3c.dom.Element customNode = (org.w3c.dom.Element) customFieldsLst.item(i);
	      String customFieldId = customNode.getAttribute(CustomField.FLD_ID);
	      String customFieldName = customNode.getAttribute(CustomField.FLD_NAME);
	      
	      CustomField existingCustomField = dataAgent.getCustomFieldByName(customFieldName);
	      int newCustomFieldId;
	      if (existingCustomField != null){
	    	  //do nothing
	    	  newCustomFieldId = existingCustomField.getId();
	      }else{
	    	  CustomField customField = new CustomField();
	    	  customField.setName(customFieldName);
	    	  customField.setState(DataObject.NEW);//Insert
	    	  customField.save();
	    	  
	    	  newCustomFieldId = customField.getId();
		    	
	    	  CustomField newCustomField = dataAgent.getCustomFieldById(newCustomFieldId);
		    	if(newCustomField == null){
		    		System.out.println("New custom field object has not loaded yet");
		    		for(;;){//Wait till commite new user first
		    			newCustomField = dataAgent.getCustomFieldById(newCustomFieldId);
		    			if(newCustomField != null)
		    				break;
		    		}
		    	}	    	  
	      }
	      customFieldIdMappings.put(customFieldId, newCustomFieldId+"");
      }     
      
      NodeList userLst = userRoot.getElementsByTagName(User.ENT_USER);
      for (int i = 0; i< userLst.getLength(); i++){
	      org.w3c.dom.Element nodeUser = (org.w3c.dom.Element) userLst.item(i);		      
	      String userId = nodeUser.getAttribute(User.FLD_ID);
	      String userName = nodeUser.getAttribute(User.FLD_NAME);
	      String userPassword = nodeUser.getAttribute(User.FLD_PASSWORD);
	      String userFirstName = nodeUser.getAttribute(User.FLD_FIRST_NAME);
	      String userLastName = nodeUser.getAttribute(User.FLD_LAST_NAME);
	      String userDelStatus = nodeUser.getAttribute(User.FLD_DEL_STATUS);
	      String userEmail = nodeUser.getAttribute(User.FLD_EMAIL);
	      String userMobile = nodeUser.getAttribute(User.FLD_MOBILE);
	      String userNotes = nodeUser.getAttribute(User.FLD_NOTES);
	      
	      String userPrivilege = nodeUser.getAttribute(User.FLD_PRIVILEGE);
	      String userLicenseType = nodeUser.getAttribute(User.FLD_LICENSE_TYPE); 
	      
	    	User user = new User();
	    	
	    	if (userPrivilege.equalsIgnoreCase("ADMIN"))
	    	{
	    		User existingUser = dataAgent.getUserByName(userName);
	    		if (existingUser != null){
	    			user = existingUser;
	    			user.setState(DataObject.IN_DB);//Update
	    		}else{
	    			user.setState(DataObject.NEW);//Insert
	    		}
	    	}else{
	    		user.setState(DataObject.NEW);//Insert
	    	}
	    	
	    	if (user.getState() == DataObject.NEW){
		    	int existingNumberOfUsers = dataAgent.getExistingNumberOfUsers();
		    	int allowedMax = dataAgent.getNumberOfUsers();
		    	if (allowedMax == 0){
		    		//unlimited user licenses
		    	}else if (allowedMax > 0){
			    	if (existingNumberOfUsers >= allowedMax){
			    		errorMessage = MessageConstants.getMessage(langFile, MessageConstants.LICENSE_RESTRICT);
			    		continue;
			    	}	    		
		    	}else if (allowedMax < 0){
		    		errorMessage = MessageConstants.getMessage(langFile, MessageConstants.INVALID_LICENSE);	   
		    		continue;
		    	}
	    	}
	    	
    		user.setFirstName(userFirstName);
    		user.setLastName(userLastName);
    		user.setName(userName);
    		user.setPassword(userPassword);		    		
			user.setNotes(userNotes);
	    	user.setPrivilege(userPrivilege);
	    	user.setDelStatus(userDelStatus);
	    	user.setEmail(userEmail);
	    	user.setMobile(userMobile);
	    	user.setLicenseType(userLicenseType);
			user.save();
			//commit();
	    	int newUserId = user.getId();
	    	
	    	user = dataAgent.getUser(newUserId, true);
	    	if(user == null){
	    		System.out.println("User has not loaded yet");
	    		for(;;){//Wait till commite new user first
	    			user = dataAgent.getUser(newUserId, true);
	    			if(user != null)
	    				break;
	    		}
	    	}
	    	
	    	userIdMappings.put(userId, newUserId+"");
	    	
	    	//Add user custom fields
	        NodeList nodeUserCustomFieldLst = nodeUser.getElementsByTagName(UserCustomField.ENT_USER_CUSTOM_FIELD);
	        for (int j = 0; j< nodeUserCustomFieldLst.getLength(); j++){
	  	      org.w3c.dom.Element userCustomNode = (org.w3c.dom.Element) nodeUserCustomFieldLst.item(j);	
	  	      //String userCustomId = userCustomNode.getAttribute(UserCustomField.FLD_ID);
	  	      //String userCustomUserId = userCustomNode.getAttribute(UserCustomField.FLD_USER_ID);
	  	      String userCustomCustomId = userCustomNode.getAttribute(UserCustomField.FLD_CUSTOM_ID);
	  	      String userCustomCustomParam = userCustomNode.getAttribute(UserCustomField.FLD_CUSTOM_PARAM);	  	      
				
	  			String newCustomFieldId = customFieldIdMappings.get(userCustomCustomId);
	  			int intNewCustomFieldId = Integer.parseInt(newCustomFieldId);
	  			
	  			UserCustomField userCustomField = new UserCustomField();
				
	  			UserCustomField existingUserCustomField = dataAgent.getUserCustomByCustomId(newUserId, intNewCustomFieldId);
	  			if (existingUserCustomField != null){
	  				userCustomField = existingUserCustomField;
	  				userCustomField.setState(DataObject.IN_DB);//update
	  			}else{
	  				userCustomField.setState(DataObject.NEW);//Insert
	  			}
	  			userCustomField.setUserId(newUserId);
				userCustomField.setCustomId(intNewCustomFieldId);
				userCustomField.setParameter(userCustomCustomParam);
				
				userCustomField.save();
	        }
	        
	        //Add im contact
	        NodeList nodeImContactLst = nodeUser.getElementsByTagName(IMContact.ENT_IM_CONTACT);
	        for (int j = 0; j< nodeImContactLst.getLength(); j++){
	  	      org.w3c.dom.Element contactNode = (org.w3c.dom.Element) nodeImContactLst.item(j);
	  	      //String contactId = contactNode.getAttribute(IMContact.FLD_ID);
	  	      String contactName = contactNode.getAttribute(IMContact.FLD_IM_CONTACT_NAME);
	  	      //String contactUserId = contactNode.getAttribute(IMContact.FLD_MDN_USER_ID);
	  	      String contactImType = contactNode.getAttribute(IMContact.FLD_IM_MDN_CONNECTION_TYPE);
	  	      //String contactProjectId = contactNode.getAttribute(IMContact.FLD_PROJECT_ID);
	  	      
	  	      IMContact imContact = new IMContact();
	  	      IMContact existingImContact = dataAgent.getUserIMContactByConnType(Integer.parseInt(contactImType), newUserId);
	  	      if (existingImContact != null){
	  	    	  imContact = existingImContact;
	  	    	  imContact.setState(DataObject.IN_DB);//Update
	  	      }else{
	  	    	  imContact.setState(DataObject.NEW);//Insert
	  	      }
	  	      imContact.setUserId(newUserId);
	  	      imContact.setName(contactName);
	  	      imContact.setImConnectionType(Integer.parseInt(contactImType));
	  	      imContact.save();
	        }
	    	
      }
      
	  
	  NodeList groups = documentRoot.getElementsByTagName("Groups");
      
      org.w3c.dom.Element groupRoot = (org.w3c.dom.Element) groups.item(0);
      NodeList groupLst = groupRoot.getElementsByTagName(Group.ENT_GROUP);
      for (int i = 0; i< groupLst.getLength(); i++){
	      org.w3c.dom.Element nodeGroup = (org.w3c.dom.Element) groupLst.item(i);
	      String groupId = nodeGroup.getAttribute(Group.FLD_ID);
	      String groupName = nodeGroup.getAttribute(Group.FLD_NAME);
	      String groupDesc = nodeGroup.getAttribute(Group.FLD_DESCRIPTION);
	      String groupGuest = nodeGroup.getAttribute(Group.FLD_GUEST);
	      String groupStatusDel = nodeGroup.getAttribute(Group.FLD_STATUS_DEL);
	      //String groupProjectId = nodeGroup.getAttribute(Group.FLD_PROJECT_ID);	
		    
	      Group group = new Group();
		  group.setName(groupName);
		  group.setProjectId(newProjectId);
		  group.setGuest(Integer.parseInt(groupGuest));
		  group.setDescription(groupDesc);
		  group.setDelStatus(groupStatusDel);
		  group.setState(DataObject.NEW);
		  group.save();
		    
			int newGroupId = group.getId();				
			Group newGroup = dataAgent.getGroup(newGroupId);
			if(newGroup == null){
				System.out.println("new Group object has not been loaded yet");
				for(;;){//Wait till commite new View first
					newGroup = dataAgent.getGroup(newGroupId);
					if(newGroup != null)
						break;
				}
			}
			groupIdMappings.put(groupId, newGroupId+"");
			
    	  NodeList membershipLst = nodeGroup.getElementsByTagName(GroupMembership.ENT_GROUPMEMBERSHIP);
    	  for (int j=0; j<membershipLst.getLength(); j++){
			  org.w3c.dom.Element nodeMembership = (org.w3c.dom.Element) membershipLst.item(j);
			  String memGroupId = nodeMembership.getAttribute(GroupMembership.FLD_GROUPID);
			  String memUserId = nodeMembership.getAttribute(GroupMembership.FLD_USERID);
			  //String memProjectId = nodeMembership.getAttribute(GroupMembership.FLD_PROJECT_ID);
			  
			  GroupMembership groupForUser = new GroupMembership();
    	    	groupForUser.setState(DataObject.NEW);
    	    	
    	    	String newGroupIdStr = groupIdMappings.get(memGroupId);
    	    	String newUserIdStr = userIdMappings.get(memUserId);
    	    	
    	    	try {
					int newUserId = Integer.parseInt(newUserIdStr);
	    	    	groupForUser.setGroupId(Integer.parseInt(newGroupIdStr));
	    	    	groupForUser.setUserId(newUserId);
	    	    	groupForUser.setProjectId(newProjectId);
	    	    	groupForUser.save();
    	    	} catch (NumberFormatException e) {
					e.printStackTrace();
					//Do nothing, user can not be added previously because of maximum number limitation
				}
    	    	

    	  }
    	  
    	  NodeList groupDataViewLst = nodeGroup.getElementsByTagName(GroupDataView.ENT_GROUPDATAVIEW);
    	  for (int j=0; j<groupDataViewLst.getLength(); j++){
			  org.w3c.dom.Element nodeGroupDataView = (org.w3c.dom.Element) groupDataViewLst.item(j);
    		  //String groupDataViewId = nodeGroupDataView.getAttribute(GroupDataView.FLD_ID);
    		  String groupDataViewGroupId = nodeGroupDataView.getAttribute(GroupDataView.FLD_GROUPID);
    		  String groupDataViewId = nodeGroupDataView.getAttribute(GroupDataView.FLD_DATAVIEWID);
    		  
    		  String newGroupIdStr = groupIdMappings.get(groupDataViewGroupId); 
    		  String newDataViewId = viewFieldIdMappings.get(groupDataViewId);
    		  
    		  GroupDataView groupDataView = new GroupDataView();
				groupDataView.setState(DataObject.NEW);
				groupDataView.setGroupId(Integer.parseInt(newGroupIdStr));
				groupDataView.setDataViewId(Integer.parseInt(newDataViewId));
				groupDataView.save();
    	  }
    	  
    	  
    	  //TODO : import usersgroup data to xml........
     	  NodeList groupTablesLst = nodeGroup.getElementsByTagName(GroupTablePermission.ENT_GROUP_ENTITY_PERMISSION);
    	  for (int j=0; j<groupTablesLst.getLength(); j++){
			  org.w3c.dom.Element nodeGroupTable = (org.w3c.dom.Element) groupTablesLst.item(j);
    		  String groupTableGroupId = nodeGroupTable.getAttribute(GroupTablePermission.FLD_GROUPID);
    		  String groupTableEntityId = nodeGroupTable.getAttribute(GroupTablePermission.FLD_ENTITY_ID);
    		  
    		  String newGroupIdStr = groupIdMappings.get(groupTableGroupId); 
    		  String newEntityId = viewFieldIdMappings.get(groupTableEntityId);
    		  
    		  GroupTablePermission groupTablePermission = new GroupTablePermission();
    		  groupTablePermission.setState(DataObject.NEW);
    		  groupTablePermission.setGroupId(Integer.parseInt(newGroupIdStr));
    		  groupTablePermission.setEntityId(Integer.parseInt(newEntityId));
    		  groupTablePermission.save();
    	  }    	
    	  //END
      }		
      
      //Update group id
      NodeList userLstAgain = userRoot.getElementsByTagName(User.ENT_USER);
      for (int i = 0; i< userLstAgain.getLength(); i++){
	      org.w3c.dom.Element nodeUser = (org.w3c.dom.Element) userLstAgain.item(i);
	      String userGroupId = nodeUser.getAttribute(User.FLD_GROUP_ID);
	      String userId = nodeUser.getAttribute(User.FLD_ID);
	      
	      String newGroupIdStr = groupIdMappings.get(userGroupId);
	      
	      String newUserIdStr = userIdMappings.get(userId);
	      
	      if (newUserIdStr != null){
	    	int newUserId;
	    	try {
	    		newUserId = Integer.parseInt(newUserIdStr);
	    	} catch (NumberFormatException e) {
				e.printStackTrace();
				//Do nothing, user can not be added previously because of maximum number limitation
				continue;
			}
			
	    	User user = dataAgent.getUser(newUserId, true);
				
			//set group id
			try {
				user.setGroupId(Integer.parseInt(newGroupIdStr));
			} catch (NumberFormatException e) {
				//e.printStackTrace();
				//do nothing, 
				//do not set group id then
			}
			
			user.setState(DataObject.IN_DB);//Update
			user.save();
	    	  
	      }
      }
	}
	
  private Element exportFile(HttpServletRequest request){
	  	//String fileNamePath = request.getParameter("fileNamePath");
	  	String projectId = request.getParameter("projectId");
	    String action = request.getParameter("action");
	    String includeUsersGroups = request.getParameter("includeUsersGroups");
	    String includeSettings = request.getParameter("includeSettings");
	    boolean booleanIncludeUG = includeUsersGroups.equals("true")? true : false;
	    boolean booleanIncludeSetting = includeSettings.equals("true")? true: false;
	    int intProjectId = 1;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e1) {
			intProjectId = 1;
		}
		try {
			IDataAgent dataAgent = DataAgentFactory.getDataInterface();
			ProjectDobj project = dataAgent.getNavProjectById(intProjectId);
			String path = LicenseManager.getExportFilePath();
		  	//String sep = System.getProperty("file.separator", "/");
	        String filename = path + project.getName() + ".xml";// + sep
	        
			File _file = new File(filename);
	        if (_file != null && _file.exists())
	            _file.delete();
	        
			_file.createNewFile(); 
	      
			FileWriter writer = new FileWriter(_file);
			//writer.setContentType("text/xml");
	        //String xmlDecl = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
	        //byte[] xmlDeclBytes = xmlDecl.getBytes("US-ASCII");			
			//writer.write(xmlDecl);
	        
			Document exportFileElement = new XmlFormatter().exportProject(intProjectId, booleanIncludeUG, booleanIncludeSetting);
			String wholeStr = new XMLOutputter().outputString(exportFileElement);
			System.out.println("export project string [" + wholeStr + "]");
			int len = wholeStr.length();			
			if (len < 1024){
				writer.write(wholeStr);
			}else{
				int i = 0;
				while ( i < len){
					if (len < i + 1024){
						writer.write(wholeStr.substring(i));
						//System.out.println("string [" + wholeStr.substring(i) + "]");
					}else{
						writer.write(wholeStr.substring(i, i+1024));
						//System.out.println("string [" + wholeStr.substring(i, i+1024) + "]");
					}					
					i += 1024;
				}
			}
			writer.flush();
			writer.close();
			
			return new XmlFormatter().simpleResult(action, "Export Successful.");
		} catch (IOException e) {
			e.printStackTrace();
			return new XmlFormatter().simpleResult(action, "IO error.");
		} catch (MdnException e) {
			e.printStackTrace();
			return new XmlFormatter().simpleResult(action, e.toString());
		}
  }
  
  private Element getNavigationDatabases(HttpServletRequest request){
    // Get the language file.
    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
    String action = request.getParameter("action");
    String projectId = request.getParameter("projectId");
    int intProjectId = 1;
	try {
		intProjectId = Integer.parseInt(projectId);
	} catch (NumberFormatException e1) {
		intProjectId = 1;
	}
    // Get the databases for the navigation.
    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
    List<DataSourceDobj> dbs;
	try {
		dbs = dataAgent.getAllDbConnections(intProjectId);
	    // Format the data.
	    return new XmlFormatter().navigationDatabases(file, dbs, intProjectId, action, "", 0, "");
	} catch (MdnException e) {
		e.printStackTrace();
		return null;
	}   
  }
	private Element newDbConnection(HttpServletRequest request){
	    // Get the DbConnection object.
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	    DbConnection dbConn = dataAgent.dbConnection(request);
	    String action = request.getParameter("action");
	    String projectId = request.getParameter("projectId");
	    int intProjectId = 1;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e1) {
			intProjectId = 1;
		}
	    // Handle the request.
	    DbDataHandler handler = new DbDataHandler();
	    ResultWrapper result = handler.handleNewDbConnection(intProjectId, dbConn);
	    String errorMsg = result.getErrorMsg();
	    
	    JdbcDataSourceDobj jdbcDataSourceDobj = (JdbcDataSourceDobj)result.getObject();
	    int newConnId = 0;
	    String dbName = "";
	    if (jdbcDataSourceDobj != null){
	    	newConnId = jdbcDataSourceDobj.getId();
	    	dbName = jdbcDataSourceDobj.getName();
	    }
	    // Get the current language file.
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);	
	    // Get the databases for the navigation.
	    List<DataSourceDobj> dbs = null;
		try {
			dbs = dataAgent.getAllDbConnections(intProjectId);
		} catch (MdnException e) {
			e.printStackTrace();
			return new XmlFormatter().navigationDatabases(file, null, intProjectId, action, e.toString(), newConnId, dbName);
		} 
	    // Format the data.
		return new XmlFormatter().navigationDatabases(file, dbs, intProjectId, action, errorMsg, newConnId, dbName);	    	
	}
	
	
  private Element getNavigationPresentation(HttpServletRequest request){
    // Get the language file.
    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
    // TODO Get the databases for the presentations. Too big an unknown yet.
    //Vector<NavDatabases> dbs = new DbAgent().getNavDatabases();

    String projectId = request.getParameter("projectId");
    int intProjectId = 1;
	try {
		intProjectId = Integer.parseInt(projectId);
	} catch (NumberFormatException e1) {
		intProjectId = 1;
	}   
    
    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
    List<QueryDobj> allMsgInfo = new Vector<QueryDobj>();
    List<UserReply> allUserReplyList = new Vector<UserReply>();
    Map userrReplyMap = new HashMap();
    try {
		allMsgInfo = dataAgent.getAllQueries(intProjectId);
		allUserReplyList = dataAgent.getAllUserReplies(intProjectId);

	} catch (MdnException e) {
		e.printStackTrace();
	}
    // Format the data.
    return new XmlFormatter().navigationPresentations(file, allMsgInfo);
  }
  
  private Element getNavigationSettings(HttpServletRequest request){
    // Get the language file.
    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
    
    // Format the data.
	IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	List<IMConnection> connections;
	List<MdnEmailSetting> mdnEmailsServer;
	List<JdbcDriver> list = null;
	try {
		connections = dataAgent.getAllIMConnections();
		mdnEmailsServer = dataAgent.getMdnEmailAddresses();
		list = dataAgent.getAllJdbcDrivers();
	    List<LanguageDobj> languages = dataAgent.getAllLanguages();
	    List<MdnSmpp> smppGateways = dataAgent.getAllSmppGateway();
		return new XmlFormatter().navigationSettings(file, languages, list, connections, mdnEmailsServer, smppGateways);
	} catch (MdnException e) {
		e.printStackTrace();
		return null;
	}

  }
  
  private Element getNavigationDeployment(HttpServletRequest request){
    // Get the language file.
    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
    // Format the data.
    return new XmlFormatter().navigationDeployment(file);
  }
  
  private Element getDBConnections(HttpServletRequest request){
    Element root = null;
    String dbName = request.getParameter(Constants.REQUEST_PARAM_DB_NAME);
    // TODO Get object representing DB connection from DB and pass to formatter.
    root = new XmlFormatter().dbConnection(dbName);
    return root;
  }
/*
  private Element saveUserIMContacts(HttpServletRequest request){
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    String result="";
	    Element root = null;

	    String userid = request.getParameter("userid");
	    if(!userid.equalsIgnoreCase("0"))
	    {
	    	String connTypeName = request.getParameter("connTypeName");
	    	int connTypeId = IMUtil.getConnectionIdByName(connTypeName);
	    	String contact = request.getParameter("contact");
	    	//fetch data from cash memory
	    	Map contactsIdMap = (HashMap)request.getSession().getAttribute("contactsIdMap");
	    	Map connectionMap = (HashMap)request.getSession().getAttribute("connectionMap");
	    	Map contactsTextMap = (HashMap)request.getSession().getAttribute("contactsTextMap");

	    	IMConnection conn = (IMConnection)connectionMap.get(String.valueOf(connTypeId));
	    	String contactid = (String)contactsIdMap.get(String.valueOf(connTypeId));

	    	IDataAgent dataAgent = DataAgentFactory.getDataInterface();	
	    	AbstractIMConnection abstractIMConnection = new AbstractIMConnection();
	    	IMContact imContact = null;
	    	
	    	MsgSessionManager session = new MsgSessionManager();
	    
			try {
				if(contactid == null)
					contactid="0";
			
				if(contact!= null && !contact.trim().equals("-") && !contact.trim().equals("") )
				{
					if(contactid.equals("0")) //----- INSERT ------//
					{
						imContact = new IMContact();
						imContact.setState(0);
					}else{ //------ UPDATE ------//
						imContact = dataAgent.getIMContactByID(Integer.parseInt(contactid));
						imContact.setState(DataObject.IN_DB);
					}
					String oldContactName = imContact.getName();
					if(!contact.equals(oldContactName))
					{
						imContact.setUserId(Integer.parseInt(userid));			
						imContact.setImConnectionType(connTypeId);
						imContact.setName(contact);
						dataAgent.saveIMContact(imContact);
					
						abstractIMConnection.removeContact(conn, oldContactName, session);
						boolean checkContactResult = abstractIMConnection.addContact(conn, contact,session);						
						if(checkContactResult && imContact!= null) {
							//int imContactId = dataAgent.saveIMContact(imContact);
							result = "Successful Add/Edit '" + imContact.getName() + "' as a user IM contact.";
						}else
							result = "Please first login selected IM Connection If you want add this contact to IM ";
					}
				} else {
					//delete that contact if exist
					if(!contactid.equals("0")) //----- DELETE A Contact------//
					{
						String oldContactName = (String)contactsTextMap.get(String.valueOf(connTypeId));
						dataAgent.removeIMContact(Integer.parseInt(contactid));
						abstractIMConnection.removeContact(conn, oldContactName, session);
						result = "Remove " + oldContactName + " from MDN IM";
					}				
				}
			} catch (MdnException e) {
				result = "Database error";
				e.printStackTrace();
			}
	    }else{
	    	result = "Please, first define Name and Password for new user.";
	    }
		root = new XmlFormatter().saveIMConactForUserResult(result);
		String xml = new XMLOutputter().outputString(root);        
    	logger.info("xml: " + xml);
	    // Format the data.
	    return root;
  }
*/
  private Element getUser(HttpServletRequest request){
    Element root = null;
    String userID = request.getParameter("user-id");
    
    String projectId = request.getParameter("projectId");
    int intProjectId = 1;
	try {
		intProjectId = Integer.parseInt(projectId);
	} catch (NumberFormatException e1) {
		intProjectId = 1;
	}  
    
    // Get the user from the database.
    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
    User user;
    
    //---------  key = Connection Type, value = Contact Text ---------//
    Map contactsTextMap = new HashMap();
    //---------  key = Connection Type, value = Contact ID ---------//    
    Map contactsIdMap = new HashMap();
    //---------  key = Connection Type, value = Connection ---------//    
    Map connectionMap = new HashMap();
	
    try {
		user = dataAgent.getUser(Integer.parseInt(userID), true);
		if(user == null) {
			user = dataAgent.getUser(Integer.parseInt(userID), true);
		}
		List<IMConnection> connections = dataAgent.getAllIMConnections();
		
		List<IMContact> contacts = dataAgent.getImContactsByUserID(Integer.parseInt(userID));
		
		for (IMContact contact : contacts) {
			String contactText = contact.getName();
			String contactConnectionType = String.valueOf(contact.getImConnectionType());
			String contactId = String.valueOf(contact.getId());
			contactsTextMap.put(contactConnectionType, contactText);
			contactsIdMap.put(contactConnectionType, contactId);
		}		
		
		List<IMConactDetailes> contactsDetailsList = new ArrayList();
		for (IMConnection conn : connections) {
			String connType = String.valueOf(conn.getType());
			connectionMap.put(connType, conn);
			IMConactDetailes  detailes = new IMConactDetailes();

			if(contactsTextMap.containsKey(connType)){
				String contactText = (String)contactsTextMap.get(connType);
				detailes.setContactText(contactText);
				String contactId = (String)contactsIdMap.get(connType);
				detailes.setContactId(contactId);
			}else{
				//detailes.setContactText("-");//Not defined contact for this IM connection yet
				//detailes.setContactId("0");
			}
			detailes.setImConnectionId(String.valueOf(conn.getId()));
			detailes.setImName(conn.getName());
			detailes.setImType(connType);
			contactsDetailsList.add(detailes);
		}

		/*
	     * these maps are to cash data in session
	     */
		request.getSession().setAttribute("contactsIdMap", contactsIdMap);
		request.getSession().setAttribute("contactsTextMap", contactsTextMap);
		request.getSession().setAttribute("connectionMap", connectionMap);
		
		Vector allGroups = dataAgent.getGroups(intProjectId);
		//root = new XmlFormatter().userXML(user, contactsDetailsList,allGroups);
		String xml = new XMLOutputter().outputString(root);        
	    logger.info("xml: " + xml);
	} catch (NumberFormatException e) {
		e.printStackTrace();
	} catch (MdnException e) {
		e.printStackTrace();
	}
    
    return root;
  }
  
  private Element getGroup(HttpServletRequest request){
    Element root = null;
    String groupID = request.getParameter("group-id");
    // Get the user from the database.
    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
    Group group;
	try {
		group = dataAgent.getGroup(Integer.parseInt(groupID));
		List<GroupDataView> groupDataViews = dataAgent.getGroupViewsPermissions(Integer.parseInt(groupID));

		if(groupDataViews!=null && groupDataViews.size() > 0) {
			GroupDataView groupDataView = groupDataViews.get(0);
		}
		root = new XmlFormatter().groupXML(group);
	} catch (NumberFormatException e) {
		e.printStackTrace();
	} catch (MdnException e) {
		e.printStackTrace();
	}
    
    return root;
  }
  private boolean convertNumberToBoolean(int number){
	  if(number == 1)
		  return true;
	  else
		  return false;
  }
  
  private Element getGroupViewPermissions(HttpServletRequest request){
	    Element root = null;
	    String groupID = request.getParameter("groupID");
	    try {
	    	List<GroupDataView> groupDataView;
	    	Group group = null;
	    	IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	    	
	    	if(groupID != null) {
	    		int groupIDInt = Integer.parseInt(groupID);
	    		group = dataAgent.getGroup(groupIDInt);
	    		groupDataView = dataAgent.getGroupViewsPermissions(groupIDInt);
	    	} else
	    		groupDataView = new Vector<GroupDataView>();
	    	//Get project ID	    	
		    String projectId = request.getParameter("projectId");
		    int intProjectId = 1;
			try {
				intProjectId = Integer.parseInt(projectId);
			} catch (NumberFormatException e1) {
				intProjectId = 1;
			}	    	
			
	    	root = new XmlFormatter().groupDataViewXML(intProjectId, group, groupDataView, "Not-Applicant", null);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (MdnException e) {
			e.printStackTrace();
		}
	    return root;
	  }  

  private Element getGroupTablePermissions(HttpServletRequest request){
	    Element root = null;
	    String groupID = request.getParameter("groupID");
	    List<GroupTablePermission> groupTablePermissions = new ArrayList<GroupTablePermission>();
	    try {
	    	Group group = null;
	    	IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	    	
	    	if(groupID != null) {
	    		int groupIDInt = Integer.parseInt(groupID);
	    		group = dataAgent.getGroup(groupIDInt);
	    		groupTablePermissions = dataAgent.getGroupTablePermissions(Integer.parseInt(groupID));
	    	}

	    	//Get project ID	    	
		    String projectId = request.getParameter("projectId");
		    int intProjectId = 1;
			try {
				intProjectId = Integer.parseInt(projectId);
			} catch (NumberFormatException e1) {
				intProjectId = 1;
			}	    	
			
	    	root = new XmlFormatter().groupTableXML(intProjectId, group, groupTablePermissions);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (MdnException e) {
			e.printStackTrace();
		}
	    return root;
	  }    
  
  
  private Element getGroupPermissionsTree(int projectId , Group group, String result, String action){
	    Element root = null;
	    try {
	    	List<GroupDataView> groupDataView;
	    	IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	    	
	    	if(group != null) {
	    		int groupID = group.getId();
	    		groupDataView = dataAgent.getGroupViewsPermissions(groupID);
	    	}else
	    	{
	    		groupDataView = new Vector<GroupDataView>();
	    	}
	    	groupDataView.size();
	    	root = new XmlFormatter().groupDataViewXML(projectId, group, groupDataView, result, action);
		} catch (MdnException e) {
			e.printStackTrace();
		}
	    return root;
	  }  
  
  
  private Element setGroupPermissions(HttpServletRequest request){
	  	String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    
	    String projectId = request.getParameter("projectId");
	    int intProjectId = 1;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e1) {
			intProjectId = 1;
		}	
	    String groupId = request.getParameter("groupId");
	    String groupname = request.getParameter("groupname");	    
	    String fieldsSelected = request.getParameter("sel-id");
	    String tablesSelected = request.getParameter("selTblId");
	    String guest = request.getParameter("guest");
	    int isGuest;
	    if(guest.equals("true"))
	    	isGuest = 1;
	    else
	    	isGuest = 0;

	    List allSelectedFieldIdsList = new ArrayList();
	    List allSelectedTablesIdsList = new ArrayList();
	    Group group = null;
	    String action;
	    if(groupId.equals("0"))
	    	action = "add";
	    else
	    	action = "edit";
	    
	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	    String result = MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL);
	    try {
	    	 Group duplicateGroup = dataAgent.getGroupByName(groupname, intProjectId);
	    	 Group guestGroup = dataAgent.getGuestGroup(intProjectId);
		    if(Integer.parseInt(groupId) == 0 && (groupname == null || groupname.equals("") || groupname.equals("undefined")))
		    	result = MessageConstants.getMessage(file, MessageConstants.MISSING_GROUP_NAME);
		    else if(duplicateGroup != null && Integer.parseInt(groupId) != duplicateGroup.getId())//Integer.parseInt(groupId) == 0 &&
		    	result = MessageConstants.getMessage(file, MessageConstants.DUPLICATE_GROUP);
		    else if(isGuest == 1 && guestGroup != null && Integer.parseInt(groupId) != guestGroup.getId() )
		    	result = MessageConstants.getMessage(file, MessageConstants.DUPLICATE_GUEST_GROUP);		    
		    else{

		    	//Save group
			    if(groupId.equals("0") && groupname != null && !groupname.equals("") && !groupname.equals("undefined"))//For Add Group Page
	    		{
	    		    group = new Group();
	    		    group.setName(groupname);
	    		    group.setProjectId(intProjectId);
	    		    group.setGuest(isGuest);
	    		    
	    		    if(duplicateGroup!= null){//Handle Duplicate Group Name
	    		    	groupId = String.valueOf(duplicateGroup.getId());
	    		    }else {
	    		    	logger.info("> New Group Request: " + group);
	    		    	int groupIdInt = dataAgent.saveGroup(group);
	    		    	while (groupIdInt == 0) {
	    		    		System.out.println("not save yet..........");
	    		    		groupIdInt = dataAgent.saveGroup(group);
						}
	    		    	groupId = String.valueOf(groupIdInt);
	    		    }
	    		}else{
	    			group = dataAgent.getGroup(Integer.parseInt(groupId));
	    			group.setName(groupname);
	    			group.setGuest(isGuest);
	    			dataAgent.saveGroup(group);
	    		}		    	
		    	//-----

			    int groupIdInt = Integer.parseInt(groupId);
			    
				//Make fields selected list from string
			    if(fieldsSelected != null)
			    {
			    	while(fieldsSelected.contains(",")) {
			    		int index = fieldsSelected.indexOf(",");
			    		String idStr;
			    		if(index == -1) {
			    			idStr = fieldsSelected;
			    			break;
			    		}
			    		idStr = fieldsSelected.substring(0, index);
			    		allSelectedFieldIdsList.add(idStr);
		
			    		fieldsSelected = fieldsSelected.substring(index+1);
			    	}
			    	if(!fieldsSelected.contains(",") && fieldsSelected.length()>2) {
			    		allSelectedFieldIdsList.add(fieldsSelected);
			    	}
			    }
			    //----			    
			    saveGroupViewsPermissions(allSelectedFieldIdsList, groupIdInt);
			    
				//Make tables selected list from string
			    if(tablesSelected != null)
			    {
			    	while(tablesSelected.contains(",")) {
			    		int index = tablesSelected.indexOf(",");
			    		String idStr;
			    		if(index == -1) {
			    			idStr = tablesSelected;
			    			break;
			    		}
			    		idStr = tablesSelected.substring(0, index);
			    		allSelectedTablesIdsList.add(idStr);
		
			    		tablesSelected = tablesSelected.substring(index+1);
			    	}
			    	if(!tablesSelected.contains(",") && tablesSelected.length()>2) {
			    		allSelectedTablesIdsList.add(tablesSelected);
			    	}
			    }
			    saveGroupTablesPermissions(allSelectedTablesIdsList, groupIdInt);
			    //----			    			    
		    } 
	    }catch (MdnException e) {
			e.printStackTrace();
		}

	    return getGroupPermissionsTree(intProjectId, group, result, action);
	  }  
  
  private void saveGroupViewsPermissions(List allSelectedFieldIdsList, int groupId){
	  	IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	  	try{
			List<GroupDataView> groupDataViewsInDB = dataAgent.getGroupViewsPermissions(groupId);
			// Deleted unChecked items if there are in DB
			for (int b = 0; b< groupDataViewsInDB.size(); b++){
				GroupDataView delGroupDataView = (GroupDataView)groupDataViewsInDB.get(b);
				String fieldIdInDB = String.valueOf(delGroupDataView.getDataViewId());
				if(!allSelectedFieldIdsList.contains(fieldIdInDB)){//Then Delete it
					dataAgent.deleteGroupDataView(delGroupDataView);
					
					/* TODO: GroupDataView afterDelObj = dataAgent.getGroupDataViewByID(delObject.getDataViewId());
					GroupDataView afterDelObj = dataAgent.getGroupDataViewByID(delGroupDataView.getId());
					while (afterDelObj != null) {
						afterDelObj = dataAgent.getGroupDataViewByID(delObject.getDataViewId());
					}*/
				}else{
					allSelectedFieldIdsList.remove(fieldIdInDB);//Remove it from selected list because it saved already
				}
			}
			//---
			
			//new fields list to add
			GroupDataView groupDataView = null;
			for(int j = 0; j<allSelectedFieldIdsList.size(); j++){
				int fieldId = Integer.parseInt((String)allSelectedFieldIdsList.get(j));
				groupDataView = new GroupDataView();
				groupDataView.setState(DataObject.NEW);
				groupDataView.setGroupId(groupId);
				groupDataView.setDataViewId(fieldId);
				int newId = dataAgent.saveGroupDataView(groupDataView);
				
				GroupDataView newObj = null;
		    	if(newObj == null){
		    		for(;;){//Wait till commite new user first
		    			newObj = dataAgent.getGroupDataViewByID(newId);
		    			if(newObj != null)
		    				break;
		    		}
		    	}
			}
			//-----
	  }catch (MdnException e) {
			e.printStackTrace();
	  }		
  }

  private void saveGroupTablesPermissions(List allSelectedTablesIdsList, int groupId){
	  	IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	  	try{
			List<GroupTablePermission> groupTablesInDB = dataAgent.getGroupTablePermissions(groupId);
			// Deleted unChecked items if there are in DB
			for (int b = 0; b< groupTablesInDB.size(); b++){
				GroupTablePermission groupTablesItemInDB = (GroupTablePermission)groupTablesInDB.get(b);
				String tblIdInDB = String.valueOf(groupTablesItemInDB.getEntityId());
				if(!allSelectedTablesIdsList.contains(tblIdInDB)){//Then Delete it
					groupTablesItemInDB.delete();
					/*GroupTablePermission afterDelObj = dataAgent.getGroupTablePermissionByID(groupTablesItemInDB.getId());
					while (afterDelObj != null) {
						System.out.println("not delete yet.............");
						afterDelObj = dataAgent.getGroupTablePermissionByID(groupTablesItemInDB.getId());
					}*/
				}else{
					allSelectedTablesIdsList.remove(tblIdInDB);//Remove it from selected list because it saved already
				}
			}
			//---
			
			//new fields list to add
			GroupTablePermission groupTablePermission = null;
			for(int j = 0; j<allSelectedTablesIdsList.size(); j++){
				int tableId = Integer.parseInt((String)allSelectedTablesIdsList.get(j));
				groupTablePermission = new GroupTablePermission();
				groupTablePermission.setState(DataObject.NEW);
				groupTablePermission.setGroupId(groupId);
				groupTablePermission.setEntityId(tableId);
				groupTablePermission.save();
				/*int newId = groupTablePermission.getId();
				GroupTablePermission newObj = null;
		    	if(newObj == null){
		    		for(;;){//Wait till commite new user first
		    			newObj = dataAgent.getGroupTablePermissionByID(newId);
		    			
		    			if(newObj != null)
		    				break;
		    		}
		    	}*/
			}
			//-----
	  }catch (MdnException e) {
		  e.printStackTrace();
	  } catch (DataSourceException e) {
		  e.printStackTrace();
	  }
  }  
  private Element getUserGroupsAsTree(HttpServletRequest request){
    Element root = null;
    // Get the language file.
    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);

    String projectId = request.getParameter("projectId");
    int intProjectId = 1;
	try {
		intProjectId = Integer.parseInt(projectId);
	} catch (NumberFormatException e1) {
		intProjectId = 1;
	}	   
    
    // Get the users and groups from the database.
    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
    //DbAgent dbAgent = new DbAgent();
    Vector<User> users;
    Vector<Group> groups;
	try {
		users = dataAgent.getUsers();   
		groups = dataAgent.getGroups(intProjectId);
		
		root = new XmlFormatter().userGroupsAsTree(intProjectId, users, groups, file);
	} catch (MdnException e) {
		e.printStackTrace();
	}
    
    return root;
  }
  
  private Element getUsersGroupsRubbish(HttpServletRequest request){
    Element root = null;
    String dbName = request.getParameter(Constants.REQUEST_PARAM_DB_NAME);
    String projectId = request.getParameter("projectId");
    int intProjectId = 1;
	try {
		intProjectId = Integer.parseInt(projectId);
	} catch (NumberFormatException e1) {
		intProjectId = 1;
	}
    // Get the language file.
    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
    Vector<RecycleBinItem> recycleData = new Vector<RecycleBinItem>();
	try {
		recycleData = dataAgent.getUsersGroupsRecycleBinContent(intProjectId);
	} catch (MdnException e) {
		e.printStackTrace();
	}
    root = new XmlFormatter().userRecycleBin(recycleData);
    return root;
  }
  private Element recycleDatabase(HttpServletRequest request){
	    Element root = null;
	    //String dbName = request.getParameter(Constants.REQUEST_PARAM_DB_NAME);
	    String projectId = request.getParameter("projectId");
	    int intProjectId = 1;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e1) {
			intProjectId = 1;
		}
	    // Get the language file.
	    //String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	    Vector<RecycleBinItem> recycleData = new Vector<RecycleBinItem>();
		try {
			recycleData = dataAgent.getDBRecycleBinContent(intProjectId);
		} catch (MdnException e) {
			e.printStackTrace();
		}
	    root = new XmlFormatter().recycleBin(recycleData);
	    return root;
	  }  
  private Element updateProjectRecycleBin(HttpServletRequest request){
	    Element root = null;
	    
	    // Get the language file.
	    //String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	    Vector<RecycleBinItem> recycleData = new Vector<RecycleBinItem>();
		try {
			recycleData = dataAgent.getProjectRecycleBinContent();
		} catch (MdnException e) {
			e.printStackTrace();
		}
	    root = new XmlFormatter().recycleBin(recycleData);
	    return root;
	  } 
  private Element updateSettingsRecycleBin(HttpServletRequest request){
	    Element root = null;
	    
	    // Get the language file.
	    //String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	    Vector<RecycleBinItem> recycleData = new Vector<RecycleBinItem>();
		try {
			recycleData = dataAgent.getSettingsRecycleBinContent();
		} catch (MdnException e) {
			e.printStackTrace();
		}
	    root = new XmlFormatter().recycleBin(recycleData);
	    return root;
	  } 

  private Element getEmptyRecycleBin(HttpServletRequest request){
	    Element root = null;
	    
	    // Get the language file.
	    //String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	    Vector<RecycleBinItem> recycleData = new Vector<RecycleBinItem>();
		//try {
			//recycleData = dataAgent.getProjectRecycleBinContent();
		//} catch (MdnException e) {
		//	e.printStackTrace();
		//}
	    root = new XmlFormatter().recycleBin(recycleData);
	    return root;
	  }
  private Element getCSS(HttpServletRequest request){
    Element root = null;
    root = new XmlFormatter().getCSS();
    return root;
  }
  
  private Element getScreenDefs(HttpServletRequest request){
    Element root = null;
    root = new XmlFormatter().getScreenDefs();
    return root;
  }
  
 }
