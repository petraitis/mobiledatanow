package com.framedobjects.dashwell.servlets;
/**
 * @author Adele
 * HttpServlet to manage IM request actions
 */

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.DataSourceException;
import wsl.fw.exception.MdnException;
import wsl.mdn.dataview.JdbcDriver;
import wsl.mdn.dataview.LanguageDobj;
import wsl.mdn.mdnim.IMConnection;
import wsl.mdn.mdnmsgsvr.MdnEmailSetting;
import wsl.mdn.mdnmsgsvr.MdnSmpp;

import com.framedobjects.dashwell.biz.MsgSessionManager;
import com.framedobjects.dashwell.db.DataAgentFactory;
import com.framedobjects.dashwell.db.IDataAgent;
import com.framedobjects.dashwell.handlers.InstantMessengerHandler;
import com.framedobjects.dashwell.mdninstantmsg.connection.AbstractIMConnection;
import com.framedobjects.dashwell.utils.MessagingUtils;
import com.framedobjects.dashwell.utils.Constants;
import com.framedobjects.dashwell.utils.MessageConstants;
import com.framedobjects.dashwell.utils.XmlFormatter;
import com.itbs.aimcer.commune.MessageSupport;

public class OL_IMServlet  extends HttpServlet {
	
	private static Logger logger = Logger
  				.getLogger(OL_UserServlet.class.getName());

	/**
	 * Constructor of the object.
	 */
	public OL_IMServlet() {
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
			if (action.equalsIgnoreCase("imNew")){
				root = newIMConnectionRequest(request);
			} else if (action.equalsIgnoreCase("getAllConnections")){
				root = getAllConnections(request);
			} else if (action.equalsIgnoreCase("imConn")){
				root = viewConnRequest(request);
			} else if (action.equalsIgnoreCase("connDisConn")){
				root = connectDisconnectRequest(request);
			} else if (action.equalsIgnoreCase("deleteIm")){
				root = deleteIMRequest(request);
			} else if (action.equalsIgnoreCase("restoreMsgSett")){
				root = restoreMsgSett(request);				
			} else if (action.equalsIgnoreCase("clearMsgSett")){
				root = clearMsgSett(request);								
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
	 * Get a new IM Connection
	 * @param request
	 * @return
	 */
	private Element newIMConnectionRequest(HttpServletRequest request){
    //  Get the language file.
    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
    
    String imname = request.getParameter("imname");
    String username = request.getParameter("username");
    String password = request.getParameter("password");
    String type = request.getParameter("type");
    String imId = request.getParameter("imId");
    
    int typeId;
	try{
		typeId = Integer.parseInt(type);
	}catch (NumberFormatException e) {
		typeId = MessagingUtils.getConnectionIdByName(type);
	}
	
    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
    InstantMessengerHandler handler = new InstantMessengerHandler();
    String result = MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL_IM); 
    result = handler.validationNewConnection(imname, username, password, type, file);
    int newImConnId = 0;
	if(result.equalsIgnoreCase(MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL_IM))) {
		try {
			IMConnection newIMConnection;
			IMConnection duplicateIMConn = dataAgent.getImConnectionByTypeID(typeId);
			if(imId == null || imId.equals("") || imId.equalsIgnoreCase("undefined")){
				newIMConnection = new IMConnection();
				newIMConnection.setStatus(DataObject.NEW);
				newIMConnection.setDelStatus(Integer.parseInt(Constants.MARKED_AS_NOT_DELETED));
				if (duplicateIMConn != null){
					result = MessageConstants.getMessage(file, MessageConstants.DUPLICATE_IM_PROVIDER);
				}
			} else {
				int imIDint = Integer.parseInt(imId);
				newIMConnection = dataAgent.getImConnectionByID(imIDint);
				newIMConnection.setStatus(DataObject.IN_DB);

				if(duplicateIMConn != null && duplicateIMConn.getId() != imIDint){
					result = MessageConstants.getMessage(file, MessageConstants.DUPLICATE_IM_PROVIDER);
					if(duplicateIMConn.getDelStatus() == Constants.MARKED_AS_RECYCLED_INT)
						result = MessageConstants.getMessage(file, MessageConstants.DUPLICATE_IM_PROVIDER_IN_RECYCLE_BIN);
				}
			}
			if(result.equalsIgnoreCase(MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL_IM))){
				newIMConnection.setName(imname);
				newIMConnection.setUserName(username);			
				newIMConnection.setPassword(password);
				newIMConnection.setType(typeId);
				newIMConnection.save();
				newImConnId = newIMConnection.getId();
			}
		} catch (MdnException e) {
			e.printStackTrace();
			result = MessageConstants.getMessage(file, MessageConstants.DB_ERROR);
		} catch (DataSourceException e) {
			e.printStackTrace();
			result = MessageConstants.getMessage(file, MessageConstants.DB_ERROR);
		}
	}
	
    boolean isSuccess = false;
    if(result.equalsIgnoreCase(MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL_IM)))
    	isSuccess = true;
	
    // Format the data.
    return new XmlFormatter().newIMConnectionResult(file, result, Integer.toString(newImConnId), isSuccess);

	}

	/**
	 * Display an IM Connection
	 * @param request
	 * @return
	 */
	private Element viewConnRequest(HttpServletRequest request){
		//  Get the language file.
		String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
		String id = request.getParameter("imID");
    
		IMConnection selectedConn;
		String btnTxt ="Connect";
		try {
			IDataAgent dataAgent = DataAgentFactory.getDataInterface();
			selectedConn = dataAgent.getImConnectionByID(Integer.parseInt(id));
			
			//AbstractIMConnection abstractIMConnection = new AbstractIMConnection();
			//MsgSessionManager session = new MsgSessionManager();
			btnTxt = "";//abstractIMConnection.handleConnectionBtnTxt(selectedConn, session);
			
		    // Format the data.
		    return new XmlFormatter().viewIMConnectionResult(file, selectedConn, btnTxt);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return null;
		} catch (MdnException e) {
			e.printStackTrace();
			return null;
		} 
  }

	/**
	 * Connect/Disconnect an IM Connection
	 * @param request
	 * @return
	 */
	private Element connectDisconnectRequest(HttpServletRequest request){
	    //  Get the language file.
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    
	    String result = "OK";
	    
	    String username = request.getParameter("username");
	    String name = request.getParameter("name");
	    String password = request.getParameter("password");
	    String type = request.getParameter("type");
	    String status = request.getParameter("status");
	    int id = Integer.parseInt(request.getParameter("id"));
	    
		IMConnection im = new IMConnection();
		im.setName(name);
		im.setUserName(username);
		im.setPassword(password);
		im.setType(MessagingUtils.getConnectionIdByName(type));
		im.setStatus(Integer.parseInt(status));
		logger.info("> Connect/Disconnect IM Connection Request: " + im);
		String btnTxt = "connect";
		try {
			IDataAgent dataAgent = DataAgentFactory.getDataInterface();
			IMConnection imConnection = dataAgent.getImConnectionByID(id);

			AbstractIMConnection abstractConnection = new AbstractIMConnection();
			MsgSessionManager session = new MsgSessionManager();
			MessageSupport checkConnection = abstractConnection.handleConnection(imConnection, session);
			
			if(request.getSession().getAttribute("connError") == null){
				if(checkConnection!= null)
				{
					Iterator msgIt = checkConnection.getEventListenerIterator();
					dataAgent.changedStatus(imConnection);
					btnTxt = "Disconnect";
				}else{
					btnTxt = "Connect";
				}
				
			}else{
				result = (String)request.getSession().getAttribute("connError");
			}
			//result = "Username or password for selected IM Provider is invalid";
		} catch (MdnException e) {
			e.printStackTrace();
			result = "IM Connection error";
		}
	    // Format the data.
	    return new XmlFormatter().connectDisconnectResult(file, result, btnTxt);
	}

	/**
	 * Get All the IM Connections
	 * @param request
	 * @return
	 */
	private Element getAllConnections(HttpServletRequest request){
	    InstantMessengerHandler handler = new InstantMessengerHandler();
	    return handler.handleGetConnections();
	}	
	
	/**
	 * Delete IM Connection
	 * @param request
	 * @return
	 */
	private Element deleteIMRequest(HttpServletRequest request){
		//  Get the language file.
		String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
    
		String id = request.getParameter("id");
		InstantMessengerHandler handler = new InstantMessengerHandler();
		String result;
		try {
			result = handler.deleteIMConnection(Integer.parseInt(id));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			result = "IM Connection id is invalid";
		} catch (MdnException e) {
			e.printStackTrace();
			result = "Database error";
		}
		// Format the data.
		return new XmlFormatter().deleteIMConnectionResult(file, result);
  }

	private Element restoreMsgSett(HttpServletRequest request){
		//  Get the language file.
		String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
    
		String id = request.getParameter("id");
		String type = request.getParameter("type");

		
		List<IMConnection> connections;
		List<MdnEmailSetting> mdnEmailsServer;
		List<JdbcDriver> list = null;
		
		try {		
			IDataAgent dataAgent = DataAgentFactory.getDataInterface();
			if(type.equals("im-conn")){
				IMConnection im = dataAgent.getImConnectionByID(Integer.parseInt(id));
				im.setDelStatus(Constants.MARKED_AS_NOT_DELETED_INT);
				im.save();
			} else if(type.equals("edit_mdn_email")){
				MdnEmailSetting emailSett = dataAgent.getEmailSettingById(Integer.parseInt(id));
				emailSett.setDelStatus(Constants.MARKED_AS_NOT_DELETED_INT);
				emailSett.save();				
			} else if(type.equals("smpp-sett")){
				MdnSmpp smsGateway = dataAgent.getSmsGatewayByID(Integer.parseInt(id));				
				smsGateway.setDelStatus(Constants.MARKED_AS_NOT_DELETED_INT);
				smsGateway.save();				
			}
			
			connections = dataAgent.getAllIMConnections();
			mdnEmailsServer = dataAgent.getMdnEmailAddresses();
			list = dataAgent.getAllJdbcDrivers();
		    List<LanguageDobj> languages = dataAgent.getAllLanguages();
		    List<MdnSmpp> smppGateways = dataAgent.getAllSmppGateway();
			return new XmlFormatter().navigationSettings(file, languages, list, connections, mdnEmailsServer, smppGateways);
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (MdnException e) {
			e.printStackTrace();
		} catch (DataSourceException e) {
			e.printStackTrace();
		}
		// Format the data.
		return new Element("root");//new XmlFormatter().deleteIMConnectionResult(file, result);
  }	
	private Element clearMsgSett(HttpServletRequest request){
		//  Get the language file.
		String id = request.getParameter("id");
		String type = request.getParameter("type");
		
		try {		
			IDataAgent dataAgent = DataAgentFactory.getDataInterface();
			if(type.equals("im-conn")){
				IMConnection im = dataAgent.getImConnectionByID(Integer.parseInt(id));
				im.delete();
			}else if(type.equals("edit_mdn_email")){
				MdnEmailSetting emailSett = dataAgent.getEmailSettingById(Integer.parseInt(id));
				emailSett.delete();				
			}else if(type.equals("smpp-sett")){
				MdnSmpp smsGateway = dataAgent.getSmsGatewayByID(Integer.parseInt(id));
				smsGateway.delete();
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (MdnException e) {
			e.printStackTrace();
		} catch (DataSourceException e) {
			e.printStackTrace();
		}
		return new Element("root");
  }		
}
