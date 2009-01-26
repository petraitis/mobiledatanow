package com.framedobjects.dashwell.servlets;

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
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.DataSourceException;
import wsl.fw.exception.MdnException;
import wsl.fw.security.Feature;
import wsl.fw.security.Group;
import wsl.fw.security.GroupMembership;
import wsl.fw.security.SecurityManager;
import wsl.fw.security.User;
import wsl.mdn.dataview.GroupDataView;
import wsl.mdn.dataview.GroupTablePermission;
import wsl.mdn.mdnim.IMConnection;
import wsl.mdn.mdnim.IMContact;
import wsl.mdn.mdnmsgsvr.CustomField;
import wsl.mdn.mdnmsgsvr.MessageLog;
import wsl.mdn.mdnmsgsvr.UserCustomField;

import com.framedobjects.dashwell.biz.IMConactDetailes;
import com.framedobjects.dashwell.db.DataAgentFactory;
import com.framedobjects.dashwell.db.IDataAgent;
import com.framedobjects.dashwell.handlers.UserDataHandler;
import com.framedobjects.dashwell.utils.Constants;
import com.framedobjects.dashwell.utils.MessageConstants;
import com.framedobjects.dashwell.utils.XmlFormatter;

public class OL_UserServlet extends HttpServlet {
	
	private static Logger logger = Logger
  				.getLogger(OL_UserServlet.class.getName());

	/**
	 * Constructor of the object.
	 */
	public OL_UserServlet() {
		super();
	}

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
	    if (action.equalsIgnoreCase("userNew")){
	    	root = saveUserRequest(request);
	    } else if (action.equalsIgnoreCase("displayNewUser")){
			root = displayNewUserPageRequest(request);
	    } else if (action.equalsIgnoreCase("editUser")){
			root = editUserRequest(request);
	    } else if (action.equalsIgnoreCase("recycleUser")){
	      	root = recycleUserRequest(request);
	    } else if (action.equalsIgnoreCase("clearUser")){
      		root = clearUserRequest(request);
	    } else if (action.equalsIgnoreCase("deleteUser")){
    		root = deleteUserRequest(request);
	    } /*else if (action.equalsIgnoreCase("groupNew")){
  			root = newGroupRequest(request);
	    } */else if (action.equalsIgnoreCase("recycleGroup")){
      		root = recycleGroupRequest(request);
	    } else if (action.equalsIgnoreCase("clearGroup")){
	    	root = clearGroupRequest(request);
	    } else if (action.equalsIgnoreCase("deleteGroup")){
  			root = deleteGroupRequest(request);
	    } else if (action.equalsIgnoreCase("copyUserToGroup")){
			root = copyUserToGroupRequest(request);
	    } else if (action.equalsIgnoreCase("addUserInfo")){
			root = addUserInfo(request);
	    } else if (action.equalsIgnoreCase("displayUser")){
	        root = displayUser(request);
	    } else if (action.equalsIgnoreCase("saveUserInfo")){
	        root = saveUserRequest(request);
	    } else if (action.equalsIgnoreCase("addNewCustom")){
	        root = addNewCustom(request);
	    } else if (action.equalsIgnoreCase("defineExistingCustom")){
	        root = defineExistingCustom(request);
	    } else if (action.equalsIgnoreCase("messageLogs")){
	        root = messageLogs(request);
	    } else if (action.equalsIgnoreCase("clearMessageLogs")){
	        root = clearMessageLogs(request);
	    } else if (action.equalsIgnoreCase("displayCustomQuery")){
	        root = displayCustomQuery(request);	        
	    } else if (action.equalsIgnoreCase("deleteCustomQuery")){
	        root = deleteCustomQuery(request);	        	        
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
	
	/**
	* Get a new User
	* @param request
	* @return
	*/
	private Element saveUserRequest(HttpServletRequest request){
		//  Get the language file.
		String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);

	    String projectId = request.getParameter("projectId");
	    int intProjectId = 1;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e1) {
			intProjectId = 1;
		}   
		
		String userId = request.getParameter("user-id");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
	    String username = request.getParameter("username");
	    String password = request.getParameter("password");
	    String confirmPassword = request.getParameter("confirmPassword");
	    String email = request.getParameter("email");
	    String mobile = request.getParameter("mobile");
	    String groupId = request.getParameter("group");
	    String notes = request.getParameter("notes");
	    String privilege = request.getParameter("privilege");
	    
		String aim = request.getParameter("aim");
		String yahoo = request.getParameter("yahoo");
		String google = request.getParameter("google");
		String msn = request.getParameter("msn");
		String icq = request.getParameter("icq");
		String jabber = request.getParameter("jabber");	    
	    
	    String result = MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL);
	    if(mobile == null || mobile.equals("") || mobile.equalsIgnoreCase(Constants.UNDEFINED)){
	    	mobile= "";
	    }else if(!mobile.contains("+"))
	    	mobile = "+" + mobile.trim();
	    	
	    try {
	    	IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	    	User user = dataAgent.getUser(Integer.parseInt(userId), true);//Update 
			user.setState(DataObject.IN_DB);
			
			String mobileInDb = user.getMobile();
			if(mobileInDb == null)
				mobileInDb = "";
			
			String emailInDb = user.getEmail();
			if(emailInDb == null)
				emailInDb = "";
				
			User u = dataAgent.getUserByMobileNumber("+"+mobile);
			
			List<User> adminUsers = dataAgent.getAdminUsers();
			
			if(firstName == null || firstName.equals("") || firstName.equalsIgnoreCase(Constants.UNDEFINED))
				result = MessageConstants.getMessage(file, MessageConstants.MISSING_FIRST_NAME);
			else if(lastName == null || lastName.equals("") || lastName.equalsIgnoreCase(Constants.UNDEFINED))
				result = MessageConstants.getMessage(file, MessageConstants.MISSING_LAST_NAME);
			else if(!privilege.equalsIgnoreCase("ADMIN") && adminUsers.size() <= 1 && adminUsers.get(0)!= null && ((User)(adminUsers.get(0))).getId() == user.getId())
				result = MessageConstants.getMessage(file, MessageConstants.NEED_ADMIN);
			else if(privilege.equalsIgnoreCase("ADMIN") && (username == null || username.equals("") || username.equalsIgnoreCase(Constants.UNDEFINED)))
				result = MessageConstants.getMessage(file, MessageConstants.MISSING_USERNAME);
			else if(privilege.equalsIgnoreCase("ADMIN") && !username.equals(user.getName()) && dataAgent.getUserByName(username) != null)
				result = MessageConstants.getMessage(file, MessageConstants.DUPLICATE_USERNAME);
			else if(privilege.equalsIgnoreCase("ADMIN") && (user.getPassword()==null || user.getPassword().equals("")) &&(password == null || password.equals("") || password.equals(Constants.UNDEFINED)))
				result = MessageConstants.getMessage(file, MessageConstants.MISSING_PASSWORD);
			else if(privilege.equalsIgnoreCase("ADMIN") && !password.equals(confirmPassword))
				result = MessageConstants.getMessage(file, MessageConstants.PASSWORD_NOT_MATCH);
			else if(email != null && !email.equals("") && !email.equalsIgnoreCase(Constants.UNDEFINED) && 
					!email.equals(emailInDb) && dataAgent.getUserByEmailAddress(email) != null)
				result = MessageConstants.getMessage(file, MessageConstants.DUPLICATE_EMAIL);
			else if(mobile != null && !mobile.equals("") && !mobile.equalsIgnoreCase(Constants.UNDEFINED)){
				if(!mobile.equals(mobileInDb) && dataAgent.getUserByMobileNumber(mobile) != null)
					result = MessageConstants.getMessage(file, MessageConstants.DUPLICATE_MOBILE);
			}
			
			if(result.equalsIgnoreCase(MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL))){
				user.setFirstName(firstName);
				user.setLastName(lastName);
				if(privilege.equalsIgnoreCase("ADMIN")){
					user.setName(username);
					if(password != null && !password.equals("") && !password.equalsIgnoreCase(Constants.UNDEFINED)) {
		    			String encryptPass = SecurityManager.encryptPassword(password);
		    			user.setPassword(encryptPass);
		    		}				
				}else{
					user.setName("");
					user.setPassword("");
				}
		    	user.setEmail(email);
		    	user.setMobile(mobile);
		    	user.setNotes(notes);
		    	user.setPrivilege(privilege);
				dataAgent.saveUser(user);
				
    	    	GroupMembership groupForUser = dataAgent.getUserGroupByProjectId(user.getId(), intProjectId);
    	    	if(groupForUser == null){
    	    		groupForUser = new GroupMembership();
	    	    	groupForUser.setState(DataObject.NEW);
	    	    	groupForUser.setUserId(user.getId());
	    	    	groupForUser.setProjectId(intProjectId);
	    	    	if(groupId!= null && !groupId.equals("") && !groupId.equals(Constants.UNDEFINED) && !groupId.equals("0")){
	    	    		groupForUser.setGroupId(Integer.parseInt(groupId));
	    	    		groupForUser.save();
				    	
	    	    		GroupMembership savedGroup = dataAgent.getUserGroupByProjectId(user.getId(), intProjectId);
	    	    		if(savedGroup == null){
				    		for(;;){//Wait till commite new user first
				    			savedGroup = dataAgent.getUserGroupByProjectId(user.getId(), intProjectId);
				    			if(savedGroup != null)
				    				break;
				    		}
				    	}	    	    		
	    	    	}
    	    	}else{
	    	    	groupForUser.setState(DataObject.IN_DB);
	    	    	if(groupId!= null && !groupId.equals("") && !groupId.equals(Constants.UNDEFINED)&& !groupId.equals("0")){
	    	    		groupForUser.setGroupId(Integer.parseInt(groupId));
	    	    		groupForUser.save();
	    	    		
	    	    		GroupMembership savedGroup = dataAgent.getUserGroupByProjectId(user.getId(), intProjectId);
	    	    		if(savedGroup == null){
				    		for(;;){//Wait till commite new user first
				    			savedGroup = dataAgent.getUserGroupByProjectId(user.getId(), intProjectId);
				    			if(savedGroup != null)
				    				break;
				    		}
				    	}	    	    		
	    	    	}else
	    	    		groupForUser.delete();	    	    	
    	    	}
    	    	
    	    	//IM
    	    	int userID = user.getId();
    	    	int connType;
    	    	if(!isEmpty(aim)){
    	    		connType = Integer.parseInt(Constants.AIM_OSCAR_IM_TYPE_ID);
    	    		
    				IMContact imContactByContactText = dataAgent.getUserIMContactByContactText(aim);
    				if(imContactByContactText != null){
    					int userIdExixtingIm = imContactByContactText.getUserId();
    					int imTypeExixtingIm = imContactByContactText.getImConnectionType();
    					
    					if(userIdExixtingIm != userID && imTypeExixtingIm == connType){
    						result = MessageConstants.getMessage(file, MessageConstants.DUPLICATE_IM)+ " (" + Constants.AIM_OSCAR_IM_TYPE_NAME +  ")";
    					}else
    						saveIMContact(connType, aim, userID);		
    				}else
    					saveIMContact(connType, aim, userID);
    	    	}else
    	    		removeIMContact(Integer.parseInt(Constants.AIM_OSCAR_IM_TYPE_ID), aim, userID);
    	    	
    	    	if(!isEmpty(yahoo)){
    	    		connType = Integer.parseInt(Constants.YAHOO_IM_TYPE_ID);
    	    		
    				IMContact imContactByContactText = dataAgent.getUserIMContactByContactText(yahoo);
    				if(imContactByContactText != null){
    					int userIdExixtingIm = imContactByContactText.getUserId();
    					int imTypeExixtingIm = imContactByContactText.getImConnectionType();
    					
    					if(userIdExixtingIm != userID && imTypeExixtingIm == connType){
    						result = MessageConstants.getMessage(file, MessageConstants.DUPLICATE_IM)+ " (" + Constants.YAHOO_IM_TYPE_NAME +  ")";
    					}else
    						saveIMContact(connType, yahoo, userID);		
    				}else    	    		
    					saveIMContact(connType, yahoo, userID);
    	    	}else
    	    		removeIMContact(Integer.parseInt(Constants.YAHOO_IM_TYPE_ID), yahoo, userID);
    	    	
    	    	if(!isEmpty(google)){
    	    		connType = Integer.parseInt(Constants.GT_IM_TYPE_ID);
    	    		
    				IMContact imContactByContactText = dataAgent.getUserIMContactByContactText(google);
    				if(imContactByContactText != null){
    					int userIdExixtingIm = imContactByContactText.getUserId();
    					int imTypeExixtingIm = imContactByContactText.getImConnectionType();
    					
    					if(userIdExixtingIm != userID && imTypeExixtingIm == connType){
    						result = MessageConstants.getMessage(file, MessageConstants.DUPLICATE_IM)+ " (" + Constants.GT_IM_TYPE_NAME +  ")";
    					}else
    						saveIMContact(connType, google, userID);		
    				}else    	    		    	    		
    					saveIMContact(connType, google, userID);
    	    	}else
    	    		removeIMContact(Integer.parseInt(Constants.GT_IM_TYPE_ID), google, userID);
    	    		
    	    	if(!isEmpty(msn)){
    	    		connType = Integer.parseInt(Constants.MSN_IM_TYPE_ID);
    	    		
    				IMContact imContactByContactText = dataAgent.getUserIMContactByContactText(msn);
    				if(imContactByContactText != null){
    					int userIdExixtingIm = imContactByContactText.getUserId();
    					int imTypeExixtingIm = imContactByContactText.getImConnectionType();
    					
    					if(userIdExixtingIm != userID && imTypeExixtingIm == connType){
    						result = MessageConstants.getMessage(file, MessageConstants.DUPLICATE_IM)+ " (" + Constants.MSN_IM_TYPE_NAME +  ")";
    					}else
    						saveIMContact(connType, msn, userID);		
    				}else    	    		
    					saveIMContact(connType, msn, userID);
    	    	}else
    	    		removeIMContact(Integer.parseInt(Constants.MSN_IM_TYPE_ID), msn, userID);
    	    	
    	    	if(!isEmpty(icq)){
    	    		connType = Integer.parseInt(Constants.ICQ_IM_TYPE_ID);
    			
    	    		IMContact imContactByContactText = dataAgent.getUserIMContactByContactText(icq);
    				if(imContactByContactText != null){
    					int userIdExixtingIm = imContactByContactText.getUserId();
    					int imTypeExixtingIm = imContactByContactText.getImConnectionType();
    					
    					if(userIdExixtingIm != userID && imTypeExixtingIm == connType){
    						result = MessageConstants.getMessage(file, MessageConstants.DUPLICATE_IM)+ " (" + Constants.ICQ_IM_TYPE_NAME +  ")";
    					}else
    						saveIMContact(connType, icq, userID);		
    				}else    	    		    	    		
    					saveIMContact(connType, icq, userID);
    	    	}else
    	    		removeIMContact(Integer.parseInt(Constants.ICQ_IM_TYPE_ID), icq, userID);
    	    	
    	    	if(!isEmpty(jabber)){
    	    		connType = Integer.parseInt(Constants.JABBER_IM_TYPE_ID);
    	    		
    	    		IMContact imContactByContactText = dataAgent.getUserIMContactByContactText(jabber);
    				if(imContactByContactText != null){
    					int userIdExixtingIm = imContactByContactText.getUserId();
    					int imTypeExixtingIm = imContactByContactText.getImConnectionType();
    					
    					if(userIdExixtingIm != userID && imTypeExixtingIm == connType){
    						result = MessageConstants.getMessage(file, MessageConstants.DUPLICATE_IM)+ " (" + Constants.JABBER_IM_TYPE_NAME +  ")";
    					}else
    						saveIMContact(connType, jabber, userID);
    				}else	
    					saveIMContact(connType, jabber, userID);
    	    	}else
    	    		removeIMContact(Integer.parseInt(Constants.JABBER_IM_TYPE_ID), jabber, userID);
    	    	
			}
	    } catch (MdnException e) {
			e.printStackTrace();
			result = MessageConstants.getMessage(file, MessageConstants.DB_ERROR);
		} catch (DataSourceException e) {
			e.printStackTrace();
		}

		//Format the data.
		return new XmlFormatter().saveUserResult("saveUser", Integer.parseInt(userId),username,file,result, intProjectId);//newUserXML(user, contactsDetailsList, allGroups, result);
	}
	
	private boolean isEmpty(String parameter){
		if(parameter!= null && !parameter.equals("") && !parameter.equals(Constants.UNDEFINED))
			return false;
		else
			return true;
	}
	
	private boolean saveIMContact(int connType, String contactNmae, int userId){
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		IMContact userIMContact;
		try {
			userIMContact = dataAgent.getUserIMContactByConnType(connType, userId);
			if(userIMContact == null){
				userIMContact = new IMContact();
				userIMContact.setState(DataObject.NEW);
				userIMContact.setUserId(userId);			
				userIMContact.setImConnectionType(connType);    	    			
			}else
				userIMContact.setState(DataObject.IN_DB);
			
			userIMContact.setName(contactNmae);
			userIMContact.save();
		} catch (MdnException e) {
			e.printStackTrace();
		} catch (DataSourceException e) {
			e.printStackTrace();
		}
		return true;
	}

	private boolean removeIMContact(int connType, String contactNmae, int userId){
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		IMContact userIMContact;
		try {
			userIMContact = dataAgent.getUserIMContactByConnType(connType, userId);
			if(userIMContact != null){
				userIMContact.delete();
			}
		} catch (MdnException e) {
			e.printStackTrace();
		} catch (DataSourceException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	private Element displayNewUserPageRequest(HttpServletRequest request){
 
	    String projectId = request.getParameter("projectId");
	    int intProjectId = 1;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e1) {
			intProjectId = 1;
		}   
		
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		Vector allGroups;
		try {
			allGroups = dataAgent.getGroups(intProjectId);
			List<IMConnection> connections = dataAgent.getAllIMConnections();
		
			//---------  key = Connection Type, value = Contact Text ---------//
			Map contactsTextMap = new HashMap();
			//---------  key = Connection Type, value = Contact ID ---------//
			Map contactsIdMap = new HashMap();
			//---------  key = Connection Type, value = Connection ---------//
			Map connectionMap = new HashMap();

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
//					detailes.setContactText("-");//Not defined contact for this IM connection yet
//					detailes.setContactId("0");
				}
				detailes.setImConnectionId(String.valueOf(conn.getId()));
				detailes.setImName(conn.getName());
				detailes.setImType(connType);
				contactsDetailsList.add(detailes);
			}
			List<Feature> privileges = new ArrayList();
			privileges = dataAgent.getAllPrivileges();
			
			ArrayList<String> licenseTypes = new ArrayList();
			//licenseTypes = dataAgent.getUserLicenseTypes();
			// 	Format the data.
			return new XmlFormatter().newUserXML(contactsDetailsList, allGroups, privileges, licenseTypes);
		} catch (MdnException e) {
			e.printStackTrace();
			return null;
		}
	}	

	private Element editUserRequest(HttpServletRequest request){
    //  Get the language file.
    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
    String username = request.getParameter("username");
    String password = request.getParameter("password");
    String details = request.getParameter("details");
    
//    User user = new User(Integer.parseInt(userID), username, password, email,
//    											mobile, details, queryUserID);
    User user = new User(username, password, details);
    logger.info("> Edit User Request: " + user);
    
    UserDataHandler handler = new UserDataHandler();
    String result = handler.processEditUser(file, user);
    
    // Format the data.
    return new XmlFormatter().editUserResult(file, result);
  }
	
	private Element recycleUserRequest(HttpServletRequest request){
	    //  Get the language file.
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    String userID = request.getParameter("userID");
	    
	    logger.info("> Recycle User Request: " + userID);
	    
	    UserDataHandler handler = new UserDataHandler();
	    String result = handler.processRecycleUser(file, Integer.parseInt(userID));
	    
	    String projectId = request.getParameter("projectId");
	    int intProjectId = 1;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e1) {
			intProjectId = 1;
		}	
		try {
		    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		    if(result.equalsIgnoreCase(MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL))){
		    	User user = dataAgent.getUser(Integer.parseInt(userID), true);
		    	user.setDelStatus(Constants.MARKED_AS_RECYCLED);
		    	user.setState(DataObject.IN_DB);
		    	user.save();
		    	
				GroupMembership groupForUser = dataAgent.getUserGroupByProjectId(Integer.parseInt(userID), intProjectId);
				if(groupForUser != null){
					groupForUser.delete();
				}
		    }
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (MdnException e) {
			e.printStackTrace();
		} catch (DataSourceException e) {
			e.printStackTrace();
		}
	    // Format the data.
	    return new XmlFormatter().recycleUserResult(file, result, intProjectId);
    }
	
	private Element clearUserRequest(HttpServletRequest request){
    //  Get the language file.
    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
    String userID = request.getParameter("userID");
    
    logger.info("> Clear User Request: " + userID);
    
    UserDataHandler handler = new UserDataHandler();
    String result = handler.processClearUser(file, Integer.parseInt(userID));
    
    // Format the data.
    return new XmlFormatter().clearUserResult(file, result);
  }
	
	private Element deleteUserRequest(HttpServletRequest request){
    //  Get the language file.
    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
    String userID = request.getParameter("userID");

    String projectId = request.getParameter("projectId");
    int intProjectId = 1;
	try {
		intProjectId = Integer.parseInt(projectId);
	} catch (NumberFormatException e1) {
		intProjectId = 1;
	}   
    logger.info("> Delete User Request: " + userID);
    
    UserDataHandler handler = new UserDataHandler();
    String result = handler.processDeleteUser(file, Integer.parseInt(userID));
    
	try {
	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	    if(result.equalsIgnoreCase(MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL))){
			GroupMembership groupForUser = dataAgent.getUserGroupByProjectId(Integer.parseInt(userID), intProjectId);
			if(groupForUser != null){
				groupForUser.delete();
			}
	    }
	} catch (NumberFormatException e) {
		e.printStackTrace();
	} catch (MdnException e) {
		e.printStackTrace();
	} catch (DataSourceException e) {
		e.printStackTrace();
	}
    
    // Format the data.
    return new XmlFormatter().deleteUserResult(file, result);
  }
	
	/*private Element newGroupRequest(HttpServletRequest request){
    //  Get the language file.
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    
	    String groupname = request.getParameter("groupname");
	    Group group = new Group(groupname, "");
	    logger.info("> New Group Request: " + group);
	    
	    UserDataHandler handler = new UserDataHandler();
	    String result = handler.processNewGroup(group);
	    
	    // Format the data.
	    return new XmlFormatter().newGroupResult(file, result);
	  }
	*/
	private Element recycleGroupRequest(HttpServletRequest request){

		//  Get the language file.
		String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
		String groupID = request.getParameter("group-id");
    
		logger.info("> Recycle Group Request: " + groupID);
    
		UserDataHandler handler = new UserDataHandler();
		String result = handler.processRecycleGroup(file, Integer.parseInt(groupID));

		String projectId = request.getParameter("projectId");
	    int intProjectId = 1;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e1) {
			intProjectId = 1;
		}	
    
		try {
		    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		    if(result.equalsIgnoreCase(MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL))){
		    	
		    	  List<GroupMembership> usersListRelatedToGroup = dataAgent.getUserGroupByGroupId(Integer.parseInt(groupID),  intProjectId);
		    	  for(GroupMembership membership: usersListRelatedToGroup){
		    		  membership.delete();
		    	  }
//				GroupMembership groupForUser = dataAgent.getUserGroupByGroupId(Integer.parseInt(groupID),  intProjectId);
//				if(groupForUser != null){
//					groupForUser.delete();
//				}
		    }
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (MdnException e) {
			e.printStackTrace();
		} catch (DataSourceException e) {
			e.printStackTrace();
		}
		
		// Format the data.
		return new XmlFormatter().recycleGroupResult(file, result);
	}
	
	private Element clearGroupRequest(HttpServletRequest request){
    //  Get the language file.
    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
    String groupID = request.getParameter("groupID");
    
    logger.info("> Clear Group Request: " + groupID);
    
    UserDataHandler handler = new UserDataHandler();
    String result = handler.processClearGroup(file, Integer.parseInt(groupID));
	String projectId = request.getParameter("projectId");
    int intProjectId = 1;
	try {
		intProjectId = Integer.parseInt(projectId);
	} catch (NumberFormatException e1) {
		intProjectId = 1;
	}	

	try {
	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	    if(result.equalsIgnoreCase(MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL))){
	    	
	    	  List<GroupMembership> usersListRelatedToGroup = dataAgent.getUserGroupByGroupId(Integer.parseInt(groupID),  intProjectId);
	    	  for(GroupMembership membership: usersListRelatedToGroup){
	    		  membership.delete();
	    	  }
//			GroupMembership groupForUser = dataAgent.getUserGroupByGroupId(Integer.parseInt(groupID),  intProjectId);
//			if(groupForUser != null){
//				groupForUser.delete();
//			}
	    }
	} catch (NumberFormatException e) {
		e.printStackTrace();
	} catch (MdnException e) {
		e.printStackTrace();
	} catch (DataSourceException e) {
		e.printStackTrace();
	}
    
    // Format the data.
    return new XmlFormatter().clearGroupResult(file, result);
  }
	
	private Element deleteGroupRequest(HttpServletRequest request){
    //  Get the language file.
    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
    String groupID = request.getParameter("groupID");
    
    logger.info("> Delete Group Request: " + groupID);
    
    UserDataHandler handler = new UserDataHandler();
    String result = handler.processDeleteGroup(file, Integer.parseInt(groupID));
	String projectId = request.getParameter("projectId");
    int intProjectId = 1;
	try {
		intProjectId = Integer.parseInt(projectId);
	} catch (NumberFormatException e1) {
		intProjectId = 1;
	}	

	try {
	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	    if(result.equalsIgnoreCase(MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL))){
	    	
	    	//Remove users membership
	    	List<GroupMembership> usersListRelatedToGroup = dataAgent.getUserGroupByGroupId(Integer.parseInt(groupID),  intProjectId);
	    	for(GroupMembership membership: usersListRelatedToGroup){
	    		membership.delete();
	    	}
	    	
	    	//delete the fildes/tables permissions
	    	List<GroupDataView> dvListRelatedToGroup = dataAgent.getGroupViewsPermissions(Integer.parseInt(groupID));
	    	List<GroupTablePermission> tblListRelatedToGroup = dataAgent.getGroupTablePermissions(Integer.parseInt(groupID));
	    	for(int i=0; i<dvListRelatedToGroup.size(); i++){
	    		GroupDataView gdv = dvListRelatedToGroup.get(i);
	    		gdv.delete();
	    	}
	    	
	    	for(int j=0; j<tblListRelatedToGroup.size(); j++){
	    		GroupTablePermission gt = tblListRelatedToGroup.get(j);
	    		gt.delete();
	    	}	    	
	    }
	} catch (NumberFormatException e) {
		e.printStackTrace();
	} catch (MdnException e) {
		e.printStackTrace();
	} catch (DataSourceException e) {
		e.printStackTrace();
	}
    
    // Format the data.
    return new XmlFormatter().deleteGroupResult(file, result);
  }
	
	private Element copyUserToGroupRequest(HttpServletRequest request){
		//  Get the language file.
		String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
  
	    String id = request.getParameter("userName");
	    String groupId = request.getParameter("groupId");
	    String projectId = request.getParameter("projectId");
	    int intProjectId = 1;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e1) {
			intProjectId = 1;
		}	
	    
	    int userId = 0;
	    String username="";
	    String result;
	    try {
	    	IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	    	
	    	Group publicGroup = dataAgent.getGuestGroup(intProjectId);
	    	if(publicGroup != null && groupId != null && publicGroup.getId()== Integer.parseInt(groupId)){
	    		result = MessageConstants.getMessage(file, MessageConstants.NOT_ALLOW_PUBLIC_GROUP);
	    	}else{
		    	User user;
				user = dataAgent.getUser(Integer.parseInt(id), true);
		    	userId = user.getId();
		    	GroupMembership groupForUser = dataAgent.getUserGroupByProjectId(user.getId(), intProjectId);
		    	if(groupForUser == null){
		    		groupForUser = new GroupMembership();
	    	    	groupForUser.setState(DataObject.NEW);
	    	    	groupForUser.setUserId(user.getId());
	    	    	groupForUser.setProjectId(intProjectId);
	    	    	if(groupId!= null && !groupId.equals("") && !groupId.equals(Constants.UNDEFINED) && !groupId.equals("0")){
	    	    		groupForUser.setGroupId(Integer.parseInt(groupId));
	    	    		groupForUser.save();
	    	    		GroupMembership savedGroup = dataAgent.getUserGroupByProjectId(user.getId(), intProjectId);
	    	    		if(savedGroup == null){
				    		for(;;){//Wait till commite new user first
				    			savedGroup = dataAgent.getUserGroupByProjectId(user.getId(), intProjectId);
				    			if(savedGroup != null)
				    				break;
				    		}
				    	}	    	    		    	    		
	    	    	}
		    	}else{
	    	    	groupForUser.setState(DataObject.IN_DB);
	    	    	if(groupId!= null && !groupId.equals("") && !groupId.equals(Constants.UNDEFINED)&& !groupId.equals("0")){
	    	    		groupForUser.setGroupId(Integer.parseInt(groupId));
	    	    		groupForUser.save();
	    	    		GroupMembership savedGroup = dataAgent.getUserGroupByProjectId(user.getId(), intProjectId);
	    	    		if(savedGroup == null){
				    		for(;;){//Wait till commite new user first
				    			savedGroup = dataAgent.getUserGroupByProjectId(user.getId(), intProjectId);
				    			if(savedGroup != null)
				    				break;
				    		}
				    	}	    	    		    	    		
	    	    	}else{
	    	    		groupForUser.delete();
	    	    	}
		    	}
				result = MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL);
	    	}
		} catch (DataSourceException e) {
			userId = 0;//must not reach hear
			result = MessageConstants.getMessage(file, MessageConstants.DB_ERROR);
			e.printStackTrace();
		} catch (MdnException e) {
			userId = 0;//must not reach hear
			result = MessageConstants.getMessage(file, MessageConstants.DB_ERROR);
			e.printStackTrace();
		}
	
		//Format the data.
	    return new XmlFormatter().saveUserResult("copyUserToGroup",userId,username,file,result,intProjectId);
	}
	
	private Element addUserInfo(HttpServletRequest request){
		//  Get the language file.
		String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);

	    String projectId = request.getParameter("projectId");
	    int intProjectId = 1;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e1) {
			intProjectId = 1;
		}	
		
	    String firstName = request.getParameter("firstName");
	    String lastName = request.getParameter("lastName");
	    String username = request.getParameter("username");
	    String password = request.getParameter("password");
	    String notes = request.getParameter("notes");
	    String groupId = request.getParameter("group");
	    String privilege = request.getParameter("privilege");
	    String confirmPassword = request.getParameter("confirmPassword");
	    String result = MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL);
	    int newUserId = 0;
	    try {
	    	IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	    	
	    	int existingNumberOfUsers = dataAgent.getExistingNumberOfUsers();
	    	int allowedMax = dataAgent.getNumberOfUsers();
	    	if (allowedMax == 0){
	    		//unlimited user licenses
	    	}else if (allowedMax > 0){
		    	if (existingNumberOfUsers >= allowedMax){
		    		result = MessageConstants.getMessage(file, MessageConstants.LICENSE_RESTRICT);	
		    		return new XmlFormatter().saveUserResult("addNewUser", newUserId,username,file,result, intProjectId);
		    	}	    		
	    	}else if (allowedMax < 0){
	    		result = MessageConstants.getMessage(file, MessageConstants.INVALID_LICENSE);
	    		return new XmlFormatter().saveUserResult("addNewUser", newUserId,username,file,result,intProjectId);	    		
	    	}

	    	User user;
	    		user = new User();
	    		user.setState(0);//Insert
				if (firstName == null || firstName.equals("") || firstName.equals(Constants.UNDEFINED))
					result = MessageConstants.getMessage(file, MessageConstants.MISSING_FIRST_NAME);
				else if(lastName == null || lastName.equals("") || lastName.equals(Constants.UNDEFINED))
					result = MessageConstants.getMessage(file, MessageConstants.MISSING_LAST_NAME);
				else if(privilege == null || privilege.equals("") || privilege.equals(Constants.UNDEFINED))
					result = MessageConstants.getMessage(file, MessageConstants.MISSING_PRIVILEGE);
				else if((privilege.equalsIgnoreCase("ADMIN")) && (username == null || username.equals("") || username.equals(Constants.UNDEFINED)))
					result = MessageConstants.getMessage(file, MessageConstants.MISSING_USERNAME);
				else if (privilege.equalsIgnoreCase("ADMIN") && dataAgent.getUserByName(username) != null)
					result = MessageConstants.getMessage(file, MessageConstants.DUPLICATE_USERNAME);
				else if(privilege.equalsIgnoreCase("ADMIN") && (password == null || password.equals("") || password.equals(Constants.UNDEFINED)))
					result = MessageConstants.getMessage(file, MessageConstants.MISSING_PASSWORD);
				else if(privilege.equalsIgnoreCase("ADMIN") && !password.equals(confirmPassword))
					result = MessageConstants.getMessage(file, MessageConstants.PASSWORD_NOT_MATCH);
				else{
		    		user.setFirstName(firstName);
		    		user.setLastName(lastName);
		    		if(privilege.equalsIgnoreCase("ADMIN")){
			    		user.setName(username);
		    			String encryptPass = SecurityManager.encryptPassword(password);
		    			user.setPassword(encryptPass);
		    		}else{
			    		user.setName("");
		    			user.setPassword("");
		    		}
	    			user.setNotes(notes);
	    	    	user.setPrivilege(privilege);
	    	    	user.setDelStatus(Constants.MARKED_AS_NOT_DELETED);
					user.save();
					//commit();
	    	    	newUserId = user.getId();
	    	    	
	    	    	user = dataAgent.getUser(newUserId, true);
			    	if(user == null){
			    		System.out.println("User has not loaded yet");
			    		for(;;){//Wait till commite new user first
			    			user = dataAgent.getUser(newUserId, true);
			    			if(user != null)
			    				break;
			    		}
			    	}
			    	
	    	    	GroupMembership groupForUser = new GroupMembership();
	    	    	groupForUser.setState(DataObject.NEW);
	    	    	groupForUser.setUserId(newUserId);
	    	    	groupForUser.setProjectId(intProjectId);
	    	    	if(groupId!= null && !groupId.equals("") && !groupId.equals(Constants.UNDEFINED) && !groupId.equals("0")){
	    	    		groupForUser.setGroupId(Integer.parseInt(groupId));
	    	    		groupForUser.save();
	    	    		
	    	    		GroupMembership savedGroup = dataAgent.getUserGroupByProjectId(user.getId(), intProjectId);
	    	    		if(savedGroup == null){
				    		for(;;){//Wait till commite new user first
				    			savedGroup = dataAgent.getUserGroupByProjectId(user.getId(), intProjectId);
				    			if(savedGroup != null)
				    				break;
				    		}
				    	}
	    	    		
	    	    	}
//	    	    	else
//	    	    		groupForUser.setGroupId(0);
//	    	    	groupForUser.save();
	    	    	//newUserId = dataAgent.saveUser(user);
					//user.save();
				}
	    } catch (MdnException e) {
			e.printStackTrace();
			result = MessageConstants.getMessage(file, MessageConstants.DB_ERROR);
		} catch (DataSourceException e) {
			e.printStackTrace();
		}
		//Format the data.
		return new XmlFormatter().saveUserResult("addNewUser", newUserId,username,file,result,intProjectId);
	}
	 private Element displayUser(HttpServletRequest request){
		 
		 	String languageFile = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);		 	
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
		    User user = null;
		    Vector allGroups = new Vector();
		    List<CustomField> allCustomFields = new ArrayList<CustomField>();
		    List<UserCustomField> userCustomField = new ArrayList<UserCustomField>();
		    Map allCustomMap = new HashMap();		    
		    
		    List<IMConactDetailes> contactsDetailsList = new ArrayList<IMConactDetailes>(); 
		    
		    List<Feature> privileges = new ArrayList<Feature>();
		    ArrayList<String> licenseTypes = new ArrayList<String>();
		    GroupMembership groupForUser = null;
		    try {
		    	groupForUser = dataAgent.getUserGroupByProjectId(Integer.parseInt(userID), intProjectId);
		    	
		    	privileges = dataAgent.getAllPrivileges();
		    	//licenseTypes = dataAgent.getUserLicenseTypes();
		    	List<IMConnection> connections = dataAgent.getAllIMConnections();
				List<IMContact> contacts = dataAgent.getImContactsByUserID(Integer.parseInt(userID));
				HttpSession session = request.getSession();

		    	contactsDetailsList = getContactsDetailsList(connections, contacts, session);
				
				allGroups = dataAgent.getGroups(intProjectId);
				
				allCustomFields = dataAgent.getAllCustomFields();
			    for (CustomField customField : allCustomFields) {
			    	String customName = customField.getName();
			    	int customId = customField.getId();
			    	allCustomMap.put(customId, customName);
				}
		    	user = dataAgent.getUser(Integer.parseInt(userID), true);
		    	if(user == null){
		    		System.out.println("User has not loaded yet");
		    		for(;;){//Wait till commite new user first
		    			user = dataAgent.getUser(Integer.parseInt(userID), true);
		    			if(user != null)
		    				break;
		    		}
		    	}
				userCustomField = dataAgent.getUserCustomFieldsByUserId(Integer.parseInt(userID));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (MdnException e) {
				e.printStackTrace();
			}
		    
		    return new XmlFormatter().userXML(user, contactsDetailsList,allGroups, allCustomFields, userCustomField, allCustomMap, privileges, licenseTypes, groupForUser, languageFile);
	}	
	 
	 private List getContactsDetailsList(List<IMConnection> connections, List<IMContact> contacts, HttpSession session){
		    //---------  key = Connection Type, value = Contact Text ---------//
		    Map contactsTextMap = new HashMap();
		    //---------  key = Connection Type, value = Contact ID ---------//    
		    Map contactsIdMap = new HashMap();
		    //---------  key = Connection Type, value = Connection ---------//    
		    Map connectionMap = new HashMap();
		 
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
			session.setAttribute("contactsIdMap", contactsIdMap);
			session.setAttribute("contactsTextMap", contactsTextMap);
			session.setAttribute("connectionMap", connectionMap);

			return contactsDetailsList;
	 }

	private Element addNewCustom(HttpServletRequest request){
	    String customName = request.getParameter("customName");
	    String projectId = request.getParameter("projectId");
	    int intProjectId = 1;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e1) {
			intProjectId = 1;
		}   
		
		String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    String result = MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL);
	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	    try {
	    	System.out.println(dataAgent.getCustomFieldByName(customName));
	    	if(customName == null || customName.equals("") || customName.equalsIgnoreCase(Constants.UNDEFINED))
	    		result = MessageConstants.getMessage(file, MessageConstants.MISSING_CUSTOM_NAME);
			else if(dataAgent.isCustomQueryNameDuplicate(customName.toUpperCase()))
				result = MessageConstants.getMessage(file, MessageConstants.DUPLICATE_NAME);
			else{
				//Add New Custom Field for display to all users
				CustomField customField = new CustomField();
				customField.setName(customName);
				customField.setCapitalName(customName.toUpperCase());
				customField.setState(0);//Insert
				
				try {
					customField.save();
					catchAllCustomQueriesMap.put(String.valueOf(customField.getId()), customField);
				} catch (DataSourceException e) {
					result = MessageConstants.getMessage(file, MessageConstants.DB_ERROR);
					e.printStackTrace();
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (MdnException e) {
			e.printStackTrace();
		}
		//Format the data.
		return XmlFormatter.displayCustomQueryXml(catchAllCustomQueriesMap, result);
		//return new XmlFormatter().addCustomResult("addNewCustomResult", "0", result,String.valueOf(intProjectId));
	}		
		
		public Map catchAllCustomQueriesMap = new HashMap();//key=customId , value=CustomField
		private Element displayCustomQuery(HttpServletRequest request){
			IDataAgent dataAgent = DataAgentFactory.getDataInterface();
			List<CustomField> allCustomQueries = null;
			try {
				allCustomQueries = dataAgent.getAllCustomFields();
				for (CustomField custom : allCustomQueries) {
					catchAllCustomQueriesMap.put(String.valueOf(custom.getId()), custom);
				}				
			} catch (MdnException e) {
				e.printStackTrace();
			}

			return XmlFormatter.displayCustomQueryXml(catchAllCustomQueriesMap, null);
		}
		
		private Element deleteCustomQuery(HttpServletRequest request){
		    String customId = request.getParameter("customId");

		    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		    //List<CustomField> allCustomQueries = new ArrayList<CustomField>();
		    
	    	try {
	    		if(customId != null && !customId.equals("") && !customId.equalsIgnoreCase(Constants.UNDEFINED)){		    		
					CustomField customQuery = dataAgent.getCustomFieldById(Integer.parseInt(customId));
					customQuery.delete();
					catchAllCustomQueriesMap.remove(customId);
					
					List<UserCustomField> usersCustomsList = dataAgent.getCustomQueriseByCustomId(Integer.parseInt(customId));
					for (UserCustomField userCustom : usersCustomsList) {
						userCustom.delete();
					}
	    		}
				//allCustomQueries = dataAgent.getAllCustomFields();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (MdnException e) {
				e.printStackTrace();
			} catch (DataSourceException e) {
				e.printStackTrace();
			}
			
		    return XmlFormatter.displayCustomQueryXml(catchAllCustomQueriesMap, null);
		}
		
		private Element defineExistingCustom(HttpServletRequest request){
			String userId = request.getParameter("user-id");
		    String customId = request.getParameter("customId");
		    String customParam = request.getParameter("customParam");
		    String projectId = request.getParameter("projectId");
		    int intProjectId = 1;
			try {
				intProjectId = Integer.parseInt(projectId);
			} catch (NumberFormatException e1) {
				intProjectId = 1;
			}   
			String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
		    String result = MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL);
		    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		    try {
		    	if(customId == null || customId.equals("") || customId.equalsIgnoreCase(Constants.UNDEFINED) || customId.equals("0"))
		    		result = MessageConstants.getMessage(file, MessageConstants.MISSING_CUSTOM_NAME);
		    	else{
		    		UserCustomField userCustomField = dataAgent.getUserCustomByCustomId(Integer.parseInt(userId), Integer.parseInt(customId));
		    		if(userCustomField == null){
		    			if(customParam == null || customParam.equals("") || customParam.equalsIgnoreCase(Constants.UNDEFINED)){
		    				result = MessageConstants.getMessage(file, MessageConstants.MISSING_CUSTOM_PARAM);
		    			}else{
				    		userCustomField = new UserCustomField();
				    		userCustomField.setUserId(Integer.parseInt(userId));
				    		userCustomField.setCustomId(Integer.parseInt(customId));
				    		userCustomField.setParameter(customParam);
				    		userCustomField.setState(0);//Insert
				    		userCustomField.save();
		    			}
		    		}else{
		    			if(customParam == null || customParam.equals("") || customParam.equalsIgnoreCase(Constants.UNDEFINED)){
		    				userCustomField.delete();
		    			}else{
				    		userCustomField.setParameter(customParam);
				    		userCustomField.setState(DataObject.IN_DB);
				    		userCustomField.save();
		    			}
		    		}
		    	}
			} catch (DataSourceException e) {
				e.printStackTrace();
				result = MessageConstants.getMessage(file, MessageConstants.DB_ERROR);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (MdnException e) {
				e.printStackTrace();
			}
			//Format the data.
			return new XmlFormatter().addCustomResult("defineExistingCustomResult",userId, result,String.valueOf(intProjectId));
		}

		 private Element messageLogs(HttpServletRequest request){
			 String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
			String userId = request.getParameter("userId");
			int userIdInt = Integer.parseInt(userId);
			IDataAgent dataAgent = DataAgentFactory.getDataInterface();
			String result = MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL);
			List<MessageLog> msgLogsList = new ArrayList<MessageLog>();
			try {
				msgLogsList = dataAgent.getMessageLogsByUserId(userIdInt);
				 if(msgLogsList.isEmpty())
					 result = "noData";
			} catch (MdnException e) {
				e.printStackTrace();
				result = MessageConstants.getMessage(file, MessageConstants.DB_ERROR);
			}
			 
			return new XmlFormatter().getMessageLogs(msgLogsList, result, userIdInt);
		 }

		 private Element clearMessageLogs(HttpServletRequest request){
			 String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
				String userId = request.getParameter("userId");
				int userIdInt = Integer.parseInt(userId);
				IDataAgent dataAgent = DataAgentFactory.getDataInterface();
				String result = MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL);
				List<MessageLog> msgLogsList = new ArrayList<MessageLog>();
				try {
					msgLogsList = dataAgent.getMessageLogsByUserId(userIdInt);
					 if(!msgLogsList.isEmpty()){
						 for (int i = 0; i<=msgLogsList.size() ;i++) {
							 MessageLog msgLog = msgLogsList.get(i);
							 int msgLogId = msgLog.getId();
							 msgLog.delete();
							 
							 MessageLog deletedMsgLog = dataAgent.getMessageLogById(msgLogId);
							 while (deletedMsgLog != null) {
								deletedMsgLog = dataAgent.getMessageLogById(msgLogId);
							 }
							 //msgLogsList.remove(i);
						 }
						 result = "noData";
					 }
				} catch (MdnException e) {
					e.printStackTrace();
					result = MessageConstants.getMessage(file, MessageConstants.DB_ERROR);
				} catch (DataSourceException e) {
					e.printStackTrace();
					result = MessageConstants.getMessage(file, MessageConstants.DB_ERROR);
				}
				return new XmlFormatter().getMessageLogs(msgLogsList, result, userIdInt);
			 }
		
}
