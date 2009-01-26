package com.framedobjects.dashwell.handlers;
/**
 * Handler class to manage MDN Instant Message actions.
 * @author Adele
 */

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.jdom.Element;
import org.smslib.CService;

import wsl.fw.datasource.DataSourceException;
import wsl.fw.exception.MdnException;
import wsl.mdn.mdnim.IMConnection;
import wsl.mdn.mdnim.IMContact;
import wsl.mdn.mdnim.IMMessage;

import com.framedobjects.dashwell.biz.MsgSessionManager;
import com.framedobjects.dashwell.db.DataAgentFactory;
import com.framedobjects.dashwell.db.IDataAgent;
import com.framedobjects.dashwell.mdninstantmsg.connection.AbstractIMConnection;
import com.framedobjects.dashwell.utils.Constants;
import com.framedobjects.dashwell.utils.MessageConstants;
import com.framedobjects.dashwell.utils.XmlFormatter;
import com.itbs.aimcer.commune.MessageSupport;
import com.itbs.aimcer.commune.joscar.ICQConnection;
import com.itbs.aimcer.commune.joscar.OscarConnection;
import com.itbs.aimcer.commune.msn.MSNConnection;
import com.itbs.aimcer.commune.smack.GoogleConnection;
import com.itbs.aimcer.commune.smack.SmackConnection;
import com.itbs.aimcer.commune.ymsg.YMsgConnection;

public class InstantMessengerHandler {
	public String processCreateMDNIMConnection(IMConnection newConnection) throws MdnException{	
		// Check with database.
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		boolean isDuplicate = dataAgent.isDuplicateIMConnection(newConnection.getType()); 
		if (isDuplicate){
			return "Duplicate IM connection for this IM provider.";
		}
		// Now create the new IM connection.
		if (dataAgent.createIMConnection(newConnection) > 0){
			return "OK";
		}
		return "Database error";
	}

	public IMConnection viewConn(int imId) throws MdnException {	
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		IMConnection selectedConn = dataAgent.getImConnectionByID(imId);
		
		return selectedConn;
	}

	public String connectDisConnect(IMConnection selectedConn, int id, HttpSession session) throws MdnException {	

		AbstractIMConnection abstractConnection = new AbstractIMConnection();
		
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		IMConnection imConnection = dataAgent.getImConnectionByID(id);

		MsgSessionManager madnSession = new MsgSessionManager();
		MessageSupport checkConnection = abstractConnection.handleConnection(imConnection, madnSession);
		if(checkConnection!= null)
		{
			Iterator msgIt = checkConnection.getEventListenerIterator();
			dataAgent.changedStatus(imConnection);
			return "OK";
		}else
			return "Username or password for selected IM Provider is invalid";

	}

	public String validationNewConnection(String imName, String userName, String password, String type, String file){
		boolean hasAlready = false;
		//String error= MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL);
		String error = MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL_IM); 
		if (imName == null || imName.trim().equals("")){
			error =  MessageConstants.getMessage(file, MessageConstants.MISSING_IM_NAME);//"Missing IM Name";
			//error +=  MessageConstants.getMessage(file, MessageConstants.MISSING_IM_NAME);//"Missing IM Name";
			hasAlready = true;
		}
		if (userName == null || userName.trim().equals("") || userName.equalsIgnoreCase("undefined")){
			if(hasAlready)
				error =  MessageConstants.getMessage(file, MessageConstants.MISSING_USERNAME);
				//error +=  "\n" + MessageConstants.getMessage(file, MessageConstants.MISSING_USERNAME);
			else
				error =  MessageConstants.getMessage(file, MessageConstants.MISSING_USERNAME);
				//error +=  MessageConstants.getMessage(file, MessageConstants.MISSING_USERNAME);
			hasAlready = true;
		}
		if (password == null || password.trim().equals("") || password.equalsIgnoreCase("undefined")){
			if(hasAlready)
				error =  MessageConstants.getMessage(file, MessageConstants.MISSING_PASSWORD);
				//error +=  "\n" + MessageConstants.getMessage(file, MessageConstants.MISSING_PASSWORD);
			else
				error =  MessageConstants.getMessage(file, MessageConstants.MISSING_PASSWORD);
				//error +=  MessageConstants.getMessage(file, MessageConstants.MISSING_PASSWORD);
			hasAlready = true;
		}
		if (type == null || type.trim().equals("") || type.equalsIgnoreCase("undefined")){
			if(hasAlready)
				error = MessageConstants.getMessage(file, MessageConstants.MISSING_IM_PROVIDER);
				//error +=  "\n" + MessageConstants.getMessage(file, MessageConstants.MISSING_IM_PROVIDER);
			else
				error =  MessageConstants.getMessage(file, MessageConstants.MISSING_IM_PROVIDER);
				//error +=  MessageConstants.getMessage(file, MessageConstants.MISSING_IM_PROVIDER);
		}
		return error;
	
	}
	/**
	 * get all the IM Connections
	 * @return Element
	 */
	public Element handleGetConnections(){
		Element root = null;
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		List<IMConnection> connections;
		try {
			connections = dataAgent.getAllIMConnections();
			root = new XmlFormatter().getAllIMConnectionResult(connections);
		} catch (MdnException e) {
			e.printStackTrace();
		}
		return root;
	}	

	public String deleteIMConnection(int imID) throws MdnException{	
		try{
			// Check with database.
			IDataAgent dataAgent = DataAgentFactory.getDataInterface();
			// Now delete the IM.
			IMConnection im = dataAgent.getImConnectionByID(imID);
			im.setDelStatus(Constants.MARKED_AS_RECYCLED_INT);
			im.save();
			
//			if (dataAgent.deleteIMConnection(imID) > 0){
//				return "OK";
//			}
		} catch (DataSourceException e) {
			return "Database error";
			//e.printStackTrace();
		}
		return "OK";
	}

	/**
	 * save the IM Message detailes in DB
	 * @return Element
	 */
	public String saveIMMessage(IMMessage imMessage) throws MdnException{	
		// Check with database.
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		// Now Save the new IM Message.
		if (dataAgent.saveRecievedIMMessage(imMessage) > 0){
			return "OK";
		}
		return "Database error";
	}

	/**
	 * get user IM contact by contact name
	 * @return Element
	 */
	public int getUserIMContactByContactText(String userContactText) throws MdnException{	
		// Check with database.
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		IMContact contact = dataAgent.getUserIMContactByContactText(userContactText);
		if(contact != null)
			return contact.getUserId();
		else
			return 0;
	}
}
