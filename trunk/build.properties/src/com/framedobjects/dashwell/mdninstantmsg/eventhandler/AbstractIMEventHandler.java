package com.framedobjects.dashwell.mdninstantmsg.eventhandler;

/**
 * This is an Event listener class that perform necessary action 
 * or checking when a listener method is called by an MDN IM.
 * 
 * @author Adele
 */

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import wsl.fw.datasource.DataSourceException;
import wsl.fw.exception.MdnException;
import wsl.mdn.mdnmsgsvr.MessagingSettingDetails;
import wsl.mdn.mdnmsgsvr.MessageLog;
import wsl.mdn.mdnmsgsvr.TemporaryBlockContacInfo;

import com.framedobjects.dashwell.biz.GuestMessagesInfo;
import com.framedobjects.dashwell.biz.MessageObject;
import com.framedobjects.dashwell.biz.MsgSessionManager;
import com.framedobjects.dashwell.db.DataAgentFactory;
import com.framedobjects.dashwell.db.IDataAgent;
import com.framedobjects.dashwell.handlers.InstantMessengerHandler;
import com.framedobjects.dashwell.mdninstantmsg.MDNIMServer;
import com.framedobjects.dashwell.mdninstantmsg.connection.AbstractIMConnection;
import com.framedobjects.dashwell.utils.MessagingUtils;
import com.framedobjects.dashwell.utils.Constants;
import com.framedobjects.dashwell.utils.MessageManager;
import com.itbs.aimcer.bean.Contact;
import com.itbs.aimcer.bean.Message;
import com.itbs.aimcer.bean.MessageImpl;
import com.itbs.aimcer.bean.Nameable;
import com.itbs.aimcer.commune.Connection;
import com.itbs.aimcer.commune.ConnectionEventListener;
import com.itbs.aimcer.commune.FileTransferSupport;
import com.itbs.aimcer.commune.IconSupport;
import com.itbs.aimcer.commune.MessageSupport;

public class AbstractIMEventHandler implements ConnectionEventListener {
    private boolean hasError;
    private int groupListSize;
    public boolean isFirst= true;
    private boolean tempBlock = false;    

    private static Logger logger = Logger.getLogger(AbstractIMEventHandler.class.getName());
    
	public boolean messageReceived(MessageSupport connection, Message message) {
		setConnectionError(false);
		//logger.debug(" Message Received: '" + message.getPlainText().trim() +"'");
		InstantMessengerHandler handler = new InstantMessengerHandler();
		try {
			String user = message.getContact().getName();
			int userId = handler.getUserIMContactByContactText(user);
			final String userKey;
			String textMessage = message.getPlainText();
			
			Date date = new Date();
			String connectionServiceName = connection.getServiceName();
			int type = MessagingUtils.getConnectionIdByName(connectionServiceName);

			MessageLog recievedMessage = new MessageLog();

			IDataAgent dataAgent = DataAgentFactory.getDataInterface();
			TemporaryBlockContacInfo msgControlsInfo = dataAgent.getTempBlockContacts();
			
			String replyingMsg ="";
			String separator = MessageManager.getMdnSeparator();
			MsgSessionManager session = AbstractIMConnection.getSession();
			
			String file = (String)session.getAttribute(Constants.SESSION_LANGUAGE_FILE);
			
			if(userId > 0){// If this user has authority
				if(textMessage != null && !textMessage.equals("") && !textMessage.contains("\r") && !textMessage.contains("\n") && !textMessage.contains("%") && !textMessage.contains("IM Help") && !textMessage.contains("No results found")){
						System.out.println("MDN User");
						textMessage = textMessage.trim();
						try {
							recievedMessage.setText(textMessage);
							recievedMessage.setDate(date);
							recievedMessage.setConnectionId(type);
							recievedMessage.setUserId(userId);
							recievedMessage.setMessageType(Constants.IM_MSG_TYPE);
							recievedMessage.setState(0);
							
							recievedMessage.save();
						} catch (DataSourceException e) {
							e.printStackTrace();
						}
					//}
					userKey = String.valueOf(userId);
	
					MessageManager messageManager = new MessageManager(userKey, session);		        
			        messageManager.initMessageSession();
					Map userInputsMap = MessagingUtils.userInputsParser(separator, textMessage);
					String imKeyword = (String)userInputsMap.get("keyword");
			        
			        if(imKeyword.equalsIgnoreCase("cancel")){
			        	logger.info("CANCEL");
			        	messageManager.cancelMessage();
			        }
			        
			        MessagingSettingDetails msgSettInfo = dataAgent.getGuestMsgInfo("Email");
					String searchTerm = msgSettInfo.getSearchTerm();
					
		        	if(imKeyword.equalsIgnoreCase(searchTerm)){//(imKeyword.equalsIgnoreCase("help")){
		        		String result;
		        		ArrayList<String> userInputsList = (ArrayList<String>)userInputsMap.get("userInputs");
		        		
		        		if(userInputsList == null || userInputsList.isEmpty()){
		    				result = MessagingUtils.getAllQueriesList(MDNIMServer.SERVER_TYPE, file);
		    			} else {
		    				String userInput = userInputsList.get(0);
		    				result = MessagingUtils.searchQueryKeyword(MDNIMServer.SERVER_TYPE, file, userInput);
		    			}
		        		connection.sendMessage(new MessageImpl(message.getContact(), false, result));
		        	} else {
				        MessageObject messagingInfo = messageManager.getMessageObject(imKeyword.toLowerCase());				
						if(messagingInfo != null) {//If there is this keyword for any query|user-reply message in DB
							replyingMsg = messageManager.getReplyingMessage(userId, userInputsMap, messagingInfo, file);
							if(replyingMsg != null && !replyingMsg.trim().equals("")){
								if(!replyingMsg.contains("\n"))
									replyingMsg = replyingMsg + "\n";
								connection.sendMessage(new MessageImpl(message.getContact(), false, replyingMsg));
							}else
								System.out.println("Invalid IM UserInput value!");
						    messageManager.catchMessageDataInSession(messagingInfo);
						}else
							System.out.println("Invalid IM keyword");
			        }
				}
			} else if(textMessage != null && !textMessage.equals("") && !textMessage.contains("\r") && !textMessage.contains("\n") && !textMessage.contains("%") 
					&& !textMessage.equalsIgnoreCase(msgControlsInfo.getReply())&& !textMessage.contains("IM Help") && !textMessage.contains("No results found")){
				System.out.println("Unknown user");
				textMessage = textMessage.trim();
				userKey = user;//Contact
				boolean isUnlimited = MDNIMServer.getLimitation();

				Map permanentBlockList = dataAgent.getBlockContactsByType(MDNIMServer.SERVER_TYPE_ID);

				if(!permanentBlockList.containsKey(user)){
					//Guest Messages				
					MessageManager messageManager = new MessageManager(userKey, session);		        
			        messageManager.initMessageSession();

			        MessagingSettingDetails guestObj = (MessagingSettingDetails)session.getAttribute(MessageManager.GUEST_OBJ);
			        
		        	messageManager.manageTemproryBlockContacts(guestObj, msgControlsInfo);
		    		Map guestMsgMap = (Map)session.getAttribute(MessageManager.GUEST_MAP_KEY);
		    		if(guestMsgMap != null){
		    			GuestMessagesInfo guestMsgInfo = (GuestMessagesInfo)guestMsgMap.get(userKey);
		    			if(guestMsgInfo != null && guestMsgInfo.isBlock())
		    				tempBlock=true;
		    			else
		    				tempBlock = false;
		    		}			        

		    		if(!tempBlock)
		    			messageManager.editGuestInfoInSession(MDNIMServer.SERVER_TYPE, isUnlimited, MDNIMServer.getAvailablePublicMessages());
		    		
			        int status = guestObj.getStatus();	
			        if(isUnlimited)
			        	status=0;
			        
			        if(tempBlock){
			        	connection.sendMessage(new MessageImpl(message.getContact(), false, msgControlsInfo.getReply()));
			        }else if(status == 0){//If it hasn't reached the limit of 200 messages yet
						Map userInputsMap = MessagingUtils.userInputsParser(separator, textMessage);
						String imKeyword = (String)userInputsMap.get("keyword");
				    
		    			Date now = new Date();
		    			System.out.println("Public Msg <Date>:" + DateFormat.getDateTimeInstance().format(now) + "  <Type>:" + 
		    					MDNIMServer.SERVER_TYPE + "  <From>:"  + userKey + "  <Query>:" + imKeyword);
		    			
				        if(imKeyword.equalsIgnoreCase("cancel")){
				        	logger.info("CANCEL");
				        	messageManager.cancelMessage();
				        }
				        
				        MessagingSettingDetails msgSettInfo = dataAgent.getGuestMsgInfo("Email");
						String searchTerm = msgSettInfo.getSearchTerm();
						
			        	if(imKeyword.equalsIgnoreCase(searchTerm)){
			        		String result;
			        		ArrayList<String> userInputsList = (ArrayList<String>)userInputsMap.get("userInputs");
			        		
			        		if(userInputsList == null || userInputsList.isEmpty()){
			    				result = MessagingUtils.getAllQueriesList(MDNIMServer.SERVER_TYPE, file);
			    			} else {
			    				String userInput = userInputsList.get(0);
			    				result = MessagingUtils.searchQueryKeyword(MDNIMServer.SERVER_TYPE, file, userInput);
			    			}
			        		connection.sendMessage(new MessageImpl(message.getContact(), false, result));
			        	}else{				        
					        MessageObject messagingInfo = messageManager.getMessageObject(imKeyword.toLowerCase());
							if(messagingInfo != null) {//If there is this keyword for any query|user-reply message in DB
								replyingMsg = messageManager.getReplyingMessageForGuestUser(userInputsMap, messagingInfo, file);
								if(replyingMsg != null && !replyingMsg.trim().equals("")){
									if(!replyingMsg.contains("\n"))
										replyingMsg = replyingMsg + "\n";
									connection.sendMessage(new MessageImpl(message.getContact(), true, replyingMsg));
								}else
									System.out.println("Invalid IM UserInput value!");
							    messageManager.catchMessageDataInSession(messagingInfo);
							}else
								System.out.println("Invalid IM Keyword!");
			        	}
			        }else
			        	System.out.println("Warning: You have reached the limit of "+ MDNIMServer.getAvailablePublicMessages() + " messages sent through Mobile Data Now to public users. Please purchase the license to get unlimited messages for public users at www.mobiledatanow.com");
				}else
					System.out.println("This contact is block >> " + user);
			}	
		} catch (MdnException e1) {
			e1.printStackTrace();
		}

		return true;		
	} 

	public void connectionEstablished(Connection arg0) {
		logger.info("Connection Established for " + arg0.getUser().getName());
		setConnectionError(false);
	}

	public void connectionFailed(Connection connection, String connectioFailedMsg) {
		setConnectionError(true);
//		MessageManager.clearMessagingSession(AbstractIMConnection.getSession());
//		if(AbstractIMConnection.getSession() != null)
//			AbstractIMConnection.getSession().setAttribute("connError", connectioFailedMsg);
//		
//		logger.error(" The Connection( "+ connection.getUser().getName() + " )Error: >>> " + connectioFailedMsg );
		System.out.println("Fail >> This Connection( "+ connection.getUser().getName() + " )Error: >>> " + connectioFailedMsg );
//		connection.reconnect();
	}

	public void connectionInitiated(Connection arg0) {
		//System.out.println("3***************** connectionInitiated. getGroupList size >>>>" + arg0.getGroupList().size());
	}

	public void connectionLost(Connection connection) {
//		MessageManager.clearMessagingSession(AbstractIMConnection.getSession());
		setConnectionError(true);
//		logger.info(" Connection Lost for this connection user : " + connection.getUser().getName());
//		connection.reconnect();
		System.out.println("lost Connection Lost for this connection user : " + connection.getUser().getName());
	}

	public boolean contactRequestReceived(String contact, MessageSupport conn) {
		logger.info("Contact request received from this contact: " + contact + " to this connection: " + conn.getServiceName());
		System.out.println("Contact request received from this contact: " + contact + " to this connection: " + conn.getServiceName());
		return true;
	}

	public boolean emailReceived(MessageSupport arg0, Message arg1) throws Exception {
		//System.out.println("6***************** emailReceived >>>>>>" + arg1.getText());
		return false;
	}

	public void errorOccured(String arg0, Exception arg1) {
		setConnectionError(false);
		System.out.println("7***************** errorOccured. arg0 >> " + arg0 );
	}

	public void fileReceiveRequested(FileTransferSupport arg0, Contact arg1, String arg2, String arg3, Object arg4) {
		//System.out.println("8***************** fileReceiveRequested. arg0 >> " + arg0 + "  arg1(Contact name)>>>" + arg1.getName() + "  arg2>>>>" + arg0 + "  arg3>>>>" + arg3 + "  arg4>>>>" + arg4);
		
	}

	public void pictureReceived(IconSupport arg0, Contact arg1) {
		//System.out.println("9***************** pictureReceived. IconSupport arg0 user >> " + arg0.getUser() + "arg1 contact name>>> " + arg1.getName());
	}

	public void statusChanged(Connection arg0) {
		//System.out.println("10***************** statusChanged. arg0 user >>" + arg0.getUser());
	}

	public void statusChanged(Connection conn, Contact arg1, boolean arg2, boolean arg3, int arg4) {
		//logger.debug(" Status Changed. for this contact name : " + arg1.getDisplayName());
		int groupSize = conn.getGroupList().size();
		setGroupListSize(groupSize);		
		//logger.debug("grouplist size " + groupSize);
	}

	public void typingNotificationReceived(MessageSupport arg0, Nameable arg1) {
		//System.out.println("12***************** typingNotificationReceived. TO >>> " + arg0.getUserName() + "  arg1 From >>>> " + arg1.getName());
	}
	
	public void setConnectionError(boolean hasError){
		this.hasError = hasError;
	}

	public boolean getConnectionError(){
		return hasError;
	}
	
	public void setGroupListSize(int groupListSize){
		this.groupListSize = groupListSize;
	}
	public int getGroupListSize(){
		return groupListSize;
	}

}
