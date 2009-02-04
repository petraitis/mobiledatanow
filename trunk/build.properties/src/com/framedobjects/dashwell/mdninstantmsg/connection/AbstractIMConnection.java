package com.framedobjects.dashwell.mdninstantmsg.connection;
/**
 * prepare connections for several IM provider in same time 
 * and handle to connect/disconnect them
 * 
 * @author Adele
 */

import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import wsl.mdn.mdnim.IMConnection;

import com.framedobjects.dashwell.biz.MsgSessionManager;
import com.framedobjects.dashwell.mdninstantmsg.bean.ContactImplFactory;
import com.framedobjects.dashwell.mdninstantmsg.bean.GroupImpl;
import com.framedobjects.dashwell.mdninstantmsg.bean.GroupImplFactory;
import com.framedobjects.dashwell.mdninstantmsg.eventhandler.AbstractIMEventHandler;
import com.framedobjects.dashwell.utils.Constants;
import com.itbs.aimcer.bean.ContactWrapper;
import com.itbs.aimcer.bean.Nameable;
import com.itbs.aimcer.commune.MessageSupport;
import com.itbs.aimcer.commune.joscar.ICQConnection;
import com.itbs.aimcer.commune.joscar.OscarConnection;
import com.itbs.aimcer.commune.msn.MSNConnection;
import com.itbs.aimcer.commune.smack.GoogleConnection;
import com.itbs.aimcer.commune.smack.SmackConnection;
import com.itbs.aimcer.commune.ymsg.YMsgConnection;

public class AbstractIMConnection {
	public Map connectionMap = new HashMap();
	public Map btnTxtMap = new HashMap();
	
	public MessageSupport yahooConn;
	public MessageSupport aimConn;
	public MessageSupport msnConn;
	public MessageSupport googleConn;
	public MessageSupport icqConn;
	public MessageSupport jabberConn;
	
	public static MsgSessionManager session;
	
	public Map<String, MessageSupport> getConnectionMap()
	{
		return connectionMap;
	}
	public void setConnectionMap(Map connectionMap)
	{
		this.connectionMap = connectionMap;
	}	

	public Map<String, String> getBtnTxtMap()
	{
		return btnTxtMap;
	}
	public void setBtnTxtMap(Map btnTxtMap)
	{
		this.btnTxtMap = btnTxtMap;
	}	
	
	private void initConnection(MessageSupport conn, String userName, String password)
	{
		AbstractIMEventHandler eventHandler =new AbstractIMEventHandler();
		conn.assignGroupFactory(new GroupImplFactory());
        conn.assignContactFactory(new ContactImplFactory());
        conn.setUserName(userName);
        conn.setPassword(password);
        conn.addEventListener(eventHandler);
	}
	
	private MessageSupport connect(MessageSupport conn){
		try {
			conn.connect();
		} catch (SecurityException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return conn;
	}
	private void disConnect(MessageSupport conn, String connType){
		try {
			conn.disconnect(true);
			setConnectionMap((Map)session.getAttribute("IMConnects"));

			session.setAttribute(connType, "logout");
			
			if(getConnectionMap().containsKey(connType))
			{
				getConnectionMap().remove(connType);
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private MessageSupport getLoggedInConnection(MsgSessionManager session, IMConnection imConnection) {
		MessageSupport connection = null;
		setConnectionMap((Map)session.getAttribute("IMConnects"));
		if(imConnection != null)
		{
			String connType = String.valueOf(imConnection.getType());
			imConnection.setStatus(0);
			if(getConnectionMap()!= null && getConnectionMap().containsKey(connType)) {
				connection = getConnectionMap().get(connType);
			} else {
				connection = getConnection(imConnection,session);//if connection is null, so first login(connect) then changed connection status to connected
			}
		}
		return connection;
	}
	
	public boolean addContact(IMConnection imConnection, final String contactName, MsgSessionManager session)
	{
		MessageSupport connection = getLoggedInConnection(session, imConnection);
		if(connection!= null) {
			Iterator msgIt = connection.getEventListenerIterator();
			if(msgIt.hasNext()){
				AbstractIMEventHandler eventHandler = (AbstractIMEventHandler)msgIt.next();
				ContactWrapper contact2 = ContactWrapper.create(contactName, connection);
				eventHandler.statusChanged(connection, contact2, true, true, 1);
				int groupSize = eventHandler.getGroupListSize();
				if(groupSize>0){
					//String groupName = connection.getGroupList().get(0).getName();
					Nameable contactNew = new Nameable() {
						public String getName() {
							return contactName;
						}
					};
					connection.addContact(contactNew, connection.getGroupList().get(0));
					connection.addContact(contact2, connection.getGroupList().get(0));
				}
				return true;
			}
		}
		return false;
	}

	public boolean removeContact(IMConnection imConnection, final String oldContactName, MsgSessionManager session)
	{
		MessageSupport connection = getLoggedInConnection(session, imConnection);
		if(connection!= null)
		{
			Iterator msgIt = connection.getEventListenerIterator();
			if(oldContactName!= null && !oldContactName.equalsIgnoreCase("") && !oldContactName.equalsIgnoreCase("-"))
			{
				if(msgIt.hasNext()){
					AbstractIMEventHandler eventHandler = (AbstractIMEventHandler)msgIt.next();
					ContactWrapper contact2 = ContactWrapper.create(oldContactName, connection);
					eventHandler.statusChanged(connection, contact2, true, true, 1);
					int groupSize = eventHandler.getGroupListSize();
					for(int i=0; i<groupSize; i++)
					{
						GroupImpl group = (GroupImpl)connection.getGroupList().get(0);
						String groupName = group.getName();
						if(groupName.equalsIgnoreCase(Constants.IM_MDN_GROUP_NAME))
						{
							Nameable contact = new Nameable() {
								public String getName() {
									return oldContactName;
								}
							};
							group.remove(contact);
							connection.removeContact(contact);
						}
					}
				}
			}
		}
		return true;
	}
	
	public MessageSupport getConnection(IMConnection imConnection, MsgSessionManager session){
		MessageSupport returnConnection = null;
		
		if(session.getAttribute("IMConnects")== null)
			setConnectionMap(new HashMap());
		else
			setConnectionMap((Map)session.getAttribute("IMConnects"));
		
		if(session.getAttribute("btnTxtMap")== null)
			setBtnTxtMap(new HashMap());
		else
			setBtnTxtMap((Map)session.getAttribute("btnTxtMap"));
		
		int status = imConnection.getStatus();
		String userName = imConnection.getUserName();
		String password = imConnection.getPassword();
		String connectionType = String.valueOf(imConnection.getType());
		
		if(connectionType.equals(Constants.YAHOO_IM_TYPE_ID)) {
			if(getConnectionMap().containsKey(Constants.YAHOO_IM_TYPE_ID))
			{
				yahooConn = getConnectionMap().get(Constants.YAHOO_IM_TYPE_ID);
				returnConnection = yahooConn;
			}else{
				yahooConn = new YMsgConnection();
				initConnection(yahooConn, userName, password);
				getConnectionMap().put(connectionType, yahooConn);
				returnConnection = connect(yahooConn);
				runTimeoutThread();
			}
		}
		if(connectionType.equals(Constants.MSN_IM_TYPE_ID)) {
			if(getConnectionMap().containsKey(Constants.MSN_IM_TYPE_ID) && Constants.CONNECT_STATUS.equals(String.valueOf(status)))
			{
				msnConn = getConnectionMap().get(Constants.MSN_IM_TYPE_ID);
				returnConnection = msnConn;
			}else{
				msnConn = new MSNConnection();
				initConnection(msnConn, userName, password);
				getConnectionMap().put(connectionType, msnConn);
				returnConnection = connect(yahooConn);
				runTimeoutThread();
			}
		}
		if(connectionType.equals(Constants.AIM_OSCAR_IM_TYPE_ID)) {
			if(getConnectionMap().containsKey(Constants.AIM_OSCAR_IM_TYPE_ID)&& Constants.CONNECT_STATUS.equals(String.valueOf(status)))
			{
				aimConn = getConnectionMap().get(Constants.AIM_OSCAR_IM_TYPE_ID);
				returnConnection = aimConn;
			}else{
				aimConn = new OscarConnection();
				initConnection(aimConn, userName, password);
				getConnectionMap().put(connectionType, aimConn);
				returnConnection = connect(aimConn);
				runTimeoutThread();
			}
		}
		if(connectionType.equals(Constants.GT_IM_TYPE_ID)) {
			if(getConnectionMap().containsKey(Constants.GT_IM_TYPE_ID)&& Constants.CONNECT_STATUS.equals(String.valueOf(status)))
			{
				googleConn = getConnectionMap().get(Constants.GT_IM_TYPE_ID);
				returnConnection = googleConn;
			}else{
				googleConn = new GoogleConnection();
				initConnection(googleConn, userName, password);
				getConnectionMap().put(connectionType, googleConn);
				returnConnection = connect(googleConn);
				runTimeoutThread();
			}
		}
		if(connectionType.equals(Constants.ICQ_IM_TYPE_ID)) {
			if(getConnectionMap().containsKey(Constants.ICQ_IM_TYPE_ID)&& Constants.CONNECT_STATUS.equals(String.valueOf(status)))
			{
				icqConn = getConnectionMap().get(Constants.ICQ_IM_TYPE_ID);
				returnConnection = icqConn;
			}else{
				icqConn = new ICQConnection();
				initConnection(icqConn, userName, password);
				getConnectionMap().put(connectionType, icqConn);
				returnConnection = connect(icqConn);
				runTimeoutThread();
			}
		}
		if(connectionType.equals(Constants.JABBER_IM_TYPE_ID)) {
			if(getConnectionMap().containsKey(Constants.JABBER_IM_TYPE_ID)&& Constants.CONNECT_STATUS.equals(String.valueOf(status)))
			{
				jabberConn = getConnectionMap().get(Constants.JABBER_IM_TYPE_ID);
				returnConnection = jabberConn;
			}else{
				jabberConn = new SmackConnection();
				initConnection(jabberConn, userName, password);
				getConnectionMap().put(connectionType, jabberConn);
				returnConnection = connect(jabberConn);
				runTimeoutThread();
			}
		}
		
		session.setAttribute("IMConnects", getConnectionMap());
//		runTimeoutThread();
		return returnConnection;
	}
	
	public static MsgSessionManager getSession(){
		return session;
	}
	
	private void runTimeoutThread(){
		//TIMEOUT Thread run
		try{
			// Check mail once in "freq" MILLIseconds
	    		int freq = Integer.parseInt("10");
	    		for (; ;) {
	    			Thread.sleep(freq); // sleep for freq milliseconds
		    		////////////////////////////////////timout method
		    		Map urStartTimeMap = null;
		            Map urTimeoutMap = null;
		    		Map urParentMap = null;

	    			if(session != null){
	    				if(session.getAttribute("urStartTimeMap") != null)
	    					urStartTimeMap = (Map)session.getAttribute("urStartTimeMap");//key = userId , value = startTime
	    				if(session.getAttribute("urTimeoutMap") != null)
	    					urTimeoutMap = (Map)session.getAttribute("urTimeoutMap");//key = userId , value = UserReply timeout
	    				if(session.getAttribute("urParentMap") != null)
	    					urParentMap = (Map)session.getAttribute("urParentMap");
	    			}
		    		
		            Iterator itMap = null;
		            if(urTimeoutMap != null)
		            	itMap = urTimeoutMap.keySet().iterator();
		            
		            while (itMap!= null && itMap.hasNext()) {
						String userId = (String) itMap.next();
			            if(urTimeoutMap.containsKey(String.valueOf(userId))){
			            	String startTimeStr = urStartTimeMap.get(String.valueOf(userId)).toString();
			            	String timeoutStr = urTimeoutMap.get(String.valueOf(userId)).toString();
			            	
			            	long startTime = Long.parseLong(startTimeStr);
			            	long now = (new Date()).getTime();
			            	long timeout = Long.parseLong(timeoutStr) * 1000;
			        		long end = startTime + timeout;
			        		
			        		if(timeout > 0 && now > end){//TIMEOUT
			        			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> IM Timeout period expired (UserId : " + userId+")");
			            		urStartTimeMap.remove(String.valueOf(userId));
			            		session.setAttribute("urStartTimeMap", urStartTimeMap);

			            		urTimeoutMap.remove(String.valueOf(userId));
			            		session.setAttribute("urTimeoutMap", urTimeoutMap);
			            		
			    	        	if(urParentMap.containsKey(String.valueOf(userId))){
			    	        		urParentMap.remove(String.valueOf(userId));
			    	        		session.setAttribute("urParentMap", urParentMap);
			    	        	}
			    	        	itMap = urTimeoutMap.keySet().iterator();
			        		}
			            }
					}
	    		}
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
		
	}
	
	public MessageSupport handleConnection(IMConnection imConnection, MsgSessionManager session){
		this.session = session;
		MessageSupport returnConnection = null;
		
		if(session.getAttribute("IMConnects")== null){
			setConnectionMap(new HashMap());
			session.setAttribute("isTimeoutStarted", "yes");
		}else
			setConnectionMap((Map)session.getAttribute("IMConnects"));
		
		String userName = imConnection.getUserName();
		String password = imConnection.getPassword();
		String connectionType = String.valueOf(imConnection.getType());
		
		if(connectionType.equals(Constants.YAHOO_IM_TYPE_ID)) {
			if(getConnectionMap().containsKey(Constants.YAHOO_IM_TYPE_ID))
			{
				yahooConn = getConnectionMap().get(Constants.YAHOO_IM_TYPE_ID);
				//disConnect(yahooConn, connectionType);

				yahooConn.reconnect();
				System.out.println("Try to Reconnect Yahoo IM");
				getConnectionMap().put(connectionType, yahooConn);
				return yahooConn;
				//returnConnection = null;
			}else{
				yahooConn = new YMsgConnection();
				initConnection(yahooConn, userName, password);
				getConnectionMap().put(connectionType, yahooConn);
				returnConnection = connect(yahooConn);
			}
		}
		if(connectionType.equals(Constants.MSN_IM_TYPE_ID)) {
			if(getConnectionMap().containsKey(Constants.MSN_IM_TYPE_ID))
			{
				msnConn = getConnectionMap().get(Constants.MSN_IM_TYPE_ID);
				//disConnect(msnConn, connectionType);
				//returnConnection = null;
				
				msnConn.reconnect();
				System.out.println("Try to Reconnect MSN IM");
				getConnectionMap().put(connectionType, msnConn);
				return msnConn;				
				
			}else{
				msnConn = new MSNConnection();
				initConnection(msnConn, userName, password);
				getConnectionMap().put(connectionType, msnConn);
				returnConnection = connect(msnConn);
			}
		}
		if(connectionType.equals(Constants.AIM_OSCAR_IM_TYPE_ID)) {
			if(getConnectionMap().containsKey(Constants.AIM_OSCAR_IM_TYPE_ID))
			{
				aimConn = getConnectionMap().get(Constants.AIM_OSCAR_IM_TYPE_ID);
				//disConnect(aimConn, connectionType);
				//returnConnection = null;

				aimConn.reconnect();
				System.out.println("Try to Reconnect AIM IM");
				getConnectionMap().put(connectionType, aimConn);
				return aimConn;
				
			}else{
				aimConn = new OscarConnection();
				initConnection(aimConn, userName, password);
				getConnectionMap().put(connectionType, aimConn);
				returnConnection = connect(aimConn);
				
				//runTimeoutThread();
			}
		}
		if(connectionType.equals(Constants.GT_IM_TYPE_ID)) {
			if(getConnectionMap().containsKey(Constants.GT_IM_TYPE_ID))
			{
				googleConn = getConnectionMap().get(Constants.GT_IM_TYPE_ID);
				//disConnect(googleConn, connectionType);
				//returnConnection = null;
				
				googleConn.reconnect();
				System.out.println("Try to Reconnect GTalk IM");
				getConnectionMap().put(connectionType, googleConn);
				return googleConn;				
				
			}else{
				googleConn = new GoogleConnection();
				initConnection(googleConn, userName, password);
				getConnectionMap().put(connectionType, googleConn);
				returnConnection = connect(googleConn);

			}
		}
		if(connectionType.equals(Constants.ICQ_IM_TYPE_ID)) {
			if(getConnectionMap().containsKey(Constants.ICQ_IM_TYPE_ID))
			{
				icqConn = getConnectionMap().get(Constants.ICQ_IM_TYPE_ID);
				//disConnect(icqConn, connectionType);
				//returnConnection = null;
				icqConn.reconnect();
				System.out.println("Try to Reconnect ICQ IM");
				getConnectionMap().put(connectionType, icqConn);
				return icqConn;								
				
			}else{
				icqConn = new ICQConnection();
				initConnection(icqConn, userName, password);
				getConnectionMap().put(connectionType, icqConn);
				returnConnection = connect(icqConn);

			}
		}
		if(connectionType.equals(Constants.JABBER_IM_TYPE_ID)) {
			if(getConnectionMap().containsKey(Constants.JABBER_IM_TYPE_ID))
			{
				jabberConn = getConnectionMap().get(Constants.JABBER_IM_TYPE_ID);
				//disConnect(jabberConn, connectionType);
				//returnConnection = null;
				jabberConn.reconnect();
				System.out.println("Try to Reconnect Jabber IM");
				getConnectionMap().put(connectionType, jabberConn);
				return jabberConn;								
			}else{
				jabberConn = new SmackConnection();
				initConnection(jabberConn, userName, password);
				getConnectionMap().put(connectionType, jabberConn);
				returnConnection = connect(jabberConn);

			}
		}
		session.setAttribute("IMConnects", getConnectionMap());

/*		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		try {
			dataAgent.changedStatus(imConnection);
		} catch (MdnException e) {
			e.printStackTrace();
		}
		if(returnConnection != null && session.getAttribute("isRun") == null){
			session.setAttribute("isRun", "YES");
			MessageManager.runTimeoutThread(session);
		}else
			session.setAttribute("isRun", "NO");
*/		
		return returnConnection;
	}
	
	public String handleConnectionBtnTxt(IMConnection imConnection, MsgSessionManager session){
		if(session.getAttribute("IMConnects")== null)
			setConnectionMap(new HashMap());
		else
			setConnectionMap((Map)session.getAttribute("IMConnects"));
		
		String connectionType = String.valueOf(imConnection.getType());
		
		if(connectionType.equals(Constants.YAHOO_IM_TYPE_ID)) {
			if(getConnectionMap().containsKey(Constants.YAHOO_IM_TYPE_ID))
				return "Disconnect";
			else
				return "Connect";
		}
		if(connectionType.equals(Constants.MSN_IM_TYPE_ID)) {
			if(getConnectionMap().containsKey(Constants.MSN_IM_TYPE_ID))
				return "Disconnect";
			else
				return "Connect";
		}
		if(connectionType.equals(Constants.AIM_OSCAR_IM_TYPE_ID)) {
			if(getConnectionMap().containsKey(Constants.AIM_OSCAR_IM_TYPE_ID))
				return "Disconnect";
			else
				return "Connect";
		}
		if(connectionType.equals(Constants.GT_IM_TYPE_ID)) {
			if(getConnectionMap().containsKey(Constants.GT_IM_TYPE_ID))
				return "Disconnect";
			else
				return "Connect";
		}
		if(connectionType.equals(Constants.ICQ_IM_TYPE_ID)) {
			if(getConnectionMap().containsKey(Constants.ICQ_IM_TYPE_ID))
				return "Disconnect";
			else
				return "Connect";
		}
		if(connectionType.equals(Constants.JABBER_IM_TYPE_ID)) {
			if(getConnectionMap().containsKey(Constants.JABBER_IM_TYPE_ID))
				return "Disconnect";
			else
				return "Connect";
		}
		return "Connect";
	}
	
	public MessageSupport getConnction(IMConnection imConnection, MsgSessionManager session){

		if(session.getAttribute("IMConnects")== null)
			setConnectionMap(new HashMap());
		else
			setConnectionMap((Map)session.getAttribute("IMConnects"));
		
		String connectionType = String.valueOf(imConnection.getType());
		
		if(connectionType.equals(Constants.YAHOO_IM_TYPE_ID)) {
			if(getConnectionMap().containsKey(Constants.YAHOO_IM_TYPE_ID))
				return (MessageSupport)getConnectionMap().get(Constants.YAHOO_IM_TYPE_ID);
//			else
//				return "Connect";
		}
		if(connectionType.equals(Constants.MSN_IM_TYPE_ID)) {
			if(getConnectionMap().containsKey(Constants.MSN_IM_TYPE_ID))
				return (MessageSupport)getConnectionMap().get(Constants.MSN_IM_TYPE_ID);
		}
		if(connectionType.equals(Constants.AIM_OSCAR_IM_TYPE_ID)) {
			if(getConnectionMap().containsKey(Constants.AIM_OSCAR_IM_TYPE_ID))
				return (MessageSupport)getConnectionMap().get(Constants.AIM_OSCAR_IM_TYPE_ID);

		}
		if(connectionType.equals(Constants.GT_IM_TYPE_ID)) {
			if(getConnectionMap().containsKey(Constants.GT_IM_TYPE_ID))
				return (MessageSupport)getConnectionMap().get(Constants.GT_IM_TYPE_ID);
		}
		if(connectionType.equals(Constants.ICQ_IM_TYPE_ID)) {
			if(getConnectionMap().containsKey(Constants.ICQ_IM_TYPE_ID))
				return (MessageSupport)getConnectionMap().get(Constants.ICQ_IM_TYPE_ID);
		}
		if(connectionType.equals(Constants.JABBER_IM_TYPE_ID)) {
			if(getConnectionMap().containsKey(Constants.JABBER_IM_TYPE_ID))
				return (MessageSupport)getConnectionMap().get(Constants.JABBER_IM_TYPE_ID);
		}
		return null;
	}	
	
	
}

