package com.framedobjects.dashwell.mdnsms;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.comm.CommDriver;

import javax.servlet.http.HttpSession;

import org.smslib.CIncomingCall;
import org.smslib.CIncomingMessage;
import org.smslib.CMessage;
import org.smslib.COutgoingMessage;
import org.smslib.CService;
import org.smslib.ICallListener;
import org.smslib.ISmsMessageListener;

import wsl.fw.datasource.DataSourceException;
import wsl.fw.exception.MdnException;
import wsl.fw.security.User;
import wsl.mdn.dataview.LanguageDobj;
import wsl.mdn.dataview.QueryDobj;
import wsl.mdn.mdnmsgsvr.MessagingSettingDetails;
import wsl.mdn.mdnmsgsvr.MdnSmsSetting;
import wsl.mdn.mdnmsgsvr.MessageLog;
import wsl.mdn.mdnmsgsvr.TemporaryBlockContacInfo;

import com.framedobjects.dashwell.biz.GuestMessagesInfo;
import com.framedobjects.dashwell.biz.MessageObject;
import com.framedobjects.dashwell.biz.MsgSessionManager;
import com.framedobjects.dashwell.db.DataAgentFactory;
import com.framedobjects.dashwell.db.IDataAgent;
import com.framedobjects.dashwell.utils.MessagingUtils;
import com.framedobjects.dashwell.utils.Constants;
import com.framedobjects.dashwell.utils.MessageManager;

public class MdnSmsServer {
	//GSM Service Constants
	public static String SERVER_TYPE = "SMS";
	public static String SERVER_TYPE_ID = "3";
	public static int GSM_SERVER_ID = 1;

	//Smslib API variable 
	public CMessageListener smsMessageListener;
	public CCallListener callListener;
	
	//get these variable from MobileDataNow license
	private static boolean unlimited;
	private static int availablePublicMessages;	

	private static IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	
	private void init(CService service, MsgSessionManager session){
		MdnSmsSetting smsSett = null;
		try {
			smsSett = dataAgent.getSmsSetting();
		} catch (MdnException e1) {
			e1.printStackTrace();
		}
		
		//service = new CService("COM1", 9600, "Wavecom", "M1306B");
		String comm = smsSett.getComm();//for linux "/dev/ttyS0"
		int baudrate = Integer.parseInt(smsSett.getBaudrate());
		String manufacturer = smsSett.getModemManufacturer();
		String model = smsSett.getmodemModel();
		service = new CService(comm, baudrate, manufacturer, model);

		smsMessageListener = new CMessageListener();

		callListener = new CCallListener();

		//System.out.println("  Using " + CService._name + " " + CService._version);
		
		String driverName = "com.sun.comm.Win32Driver";//"gnu.io.RXTXCommDriver" // or get as a JNLP property
//		String driverName = "gnu.io.RXTXCommDriver";//"gnu.io.RXTXCommDriver" // or get as a JNLP property
//		RXTXCommDriver commDriver;
		

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
		
		//MsgSessionManager sessionManager = new MsgSessionManager();
		//smsMessageListener.setSessionManager(sessionManager);
		
		//LINUX DRIVERNAME
//  		String port = "/dev/ttyS0";//"/dev/ttyUSB0";  //or----->> /dev/ttyS0 for testing
//  		try {
//			CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(port);
//			CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
//			if (commPort instanceof SerialPort) {
//				SerialPort serialPort = (SerialPort) commPort;
//				serialPort.setSerialPortParams(baudrate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
//			}
//		} catch (NoSuchPortException e) {
//			e.printStackTrace();
//		} catch (PortInUseException e) {
//			e.printStackTrace();
//		} catch (UnsupportedCommOperationException e) {
//			e.printStackTrace();
//		}
  		
  		
//		RXTXCommDriver gnuDriver = new RXTXCommDriver();
//		CommPortIdentifier.addPortName(port, CommPortIdentifier.PORT_SERIAL, gnuDriver);
//		  
		smsMessageListener.setSessionManager(session);
		
	}
	
	public void connect(CService service, MsgSessionManager session){
		init(service, session);
		try {
			service.connect();
			System.out.println("SMS server connected successfully");
			session.setAttribute("SmsConnects", service);				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static class CCallListener implements ICallListener
	{
		public void received(CService service, CIncomingCall call)
		{
			System.out.println("<< ---------------------------- INCOMING CALL ---------------------------- >>");
			System.out.println(" From: " + call.getPhoneNumber() + " @ time of call ===>> " + call.getTimeOfCall());
			System.out.println("<< ---------------------------- END ---------------------------- >>");
		}
	}
	
	// This is the message callback class.
	// The "received" method of this class is called by SMSLib API for each message received.
	private static class CMessageListener implements ISmsMessageListener
	{
		private MsgSessionManager sessionManager;
		private boolean tempBlock = false; 
		
		public void setSessionManager(MsgSessionManager sessionManager){
			this.sessionManager = sessionManager;
		}
		public MsgSessionManager getSessionManager(){
			return sessionManager;
		}
		
		public boolean received(CService service, CIncomingMessage message)
		{
			String mobileNumber = message.getOriginator();
			// Display the message received...
			System.out.println(">>>>>> This Message received ( " + message.getText() + ") from this number (" + mobileNumber + ")");
			MdnSmsServer mdnSmsServer = new MdnSmsServer();
			boolean isUnlimited = mdnSmsServer.unlimited;
			
			String userKey;
			try
			{
				String msgTxt = "";
				String textMessage = message.getText();
				
				String separator = MessageManager.getMdnSeparator();
				
				Map userInputsMap = MessagingUtils.userInputsParser(separator, textMessage);
				String smsKeyword = (String)userInputsMap.get("keyword");
				
				MessageManager messageManager = null;

				User mdnUser = dataAgent.getUserByMobileNumber(mobileNumber);
				String file = (String)getSessionManager().getAttribute(Constants.SESSION_LANGUAGE_FILE);
				if(mdnUser != null){
					System.out.println("MDN User");
					//Save Email Message Log
					MessageLog msgLog = new MessageLog();
					msgLog.setText(textMessage);
					msgLog.setDate(new Date());
					msgLog.setUserId(mdnUser.getId());
					msgLog.setMessageType(Constants.SMS_MSG_TYPE);
					msgLog.setState(0);
					try {
						msgLog.save();
					} catch (DataSourceException e) {
						e.printStackTrace();
					}
					userKey = String.valueOf(mdnUser.getId());
			        messageManager = new MessageManager(userKey, getSessionManager());		        
			        messageManager.initMessageSession();
					
			        if(smsKeyword.equalsIgnoreCase("cancel")){
			        	System.out.println("CANCEL");
			        	messageManager.cancelMessage();
			        }

			        MessagingSettingDetails msgSettInfo = dataAgent.getGuestMsgInfo("Email");
					String searchTerm = msgSettInfo.getSearchTerm();
					
		        	if(smsKeyword.equalsIgnoreCase(searchTerm)){//(imKeyword.equalsIgnoreCase("help")){
		        		String result;
		        		ArrayList<String> userInputsList = (ArrayList<String>)userInputsMap.get("userInputs");
		        		
		        		if(userInputsList == null || userInputsList.isEmpty()){
		    				result = MessagingUtils.getAllQueriesList(SERVER_TYPE, file);
		    			} else {
		    				String userInput = userInputsList.get(0);
		    				result = MessagingUtils.searchQueryKeyword(SERVER_TYPE, file, userInput);
		    			}
						COutgoingMessage msg = new COutgoingMessage(mobileNumber, result);
						msg.setMessageEncoding(CMessage.MessageEncoding.Enc7Bit);
						service.sendMessage(msg);		        		
			        }else{	
				        MessageObject messagingInfo = messageManager.getSMSMessageObject(smsKeyword.toLowerCase(), GSM_SERVER_ID);
						if(messagingInfo != null) {
							msgTxt = messageManager.getReplyingMessage(mdnUser.getId(), userInputsMap, messagingInfo, file);
							if(msgTxt != null && !msgTxt.equals("")){
								COutgoingMessage msg = new COutgoingMessage(mobileNumber, msgTxt);
								msg.setMessageEncoding(CMessage.MessageEncoding.Enc7Bit);
								service.sendMessage(msg);
								messageManager.catchMessageDataInSession(messagingInfo);
								System.out.println("Sent response successfull for this sms : " + smsKeyword);
							}else{
								System.out.println("Invalid SMS UserInput value!");
							}
						}else
							System.out.println("Invalid SMS Keyword!");
			        }
				}else {
					System.out.println("Unknown user");
					userKey = mobileNumber;
					Map permanentBlockList = dataAgent.getBlockContactsByType(SERVER_TYPE_ID);

					if(!permanentBlockList.containsKey(userKey)){
						
						IDataAgent dataAgent = DataAgentFactory.getDataInterface();
						TemporaryBlockContacInfo msgControlsInfo = dataAgent.getTempBlockContacts();
						
						messageManager = new MessageManager(userKey, getSessionManager());		        
				        messageManager.initMessageSession();
				       
				        MessagingSettingDetails guestObj = (MessagingSettingDetails)getSessionManager().getAttribute(MessageManager.GUEST_OBJ);
				        
						//Guest Messages				
			        	messageManager.manageTemproryBlockContacts(guestObj, msgControlsInfo);
			    		Map guestMsgMap = (Map)getSessionManager().getAttribute(MessageManager.GUEST_MAP_KEY);
			    		if(guestMsgMap != null){
			    			GuestMessagesInfo guestMsgInfo = (GuestMessagesInfo)guestMsgMap.get(userKey);
			    			if(guestMsgInfo != null && guestMsgInfo.isBlock())
			    				tempBlock=true;
			    			else
			    				tempBlock = false;
			    		}			        				        
				        
			    		if(!tempBlock)
			    			messageManager.editGuestInfoInSession(SERVER_TYPE, isUnlimited, availablePublicMessages);
			    		
				        int status = guestObj.getStatus();	
				        if(isUnlimited)
				        	status=0;
				        
				        if(tempBlock){
							COutgoingMessage msg = new COutgoingMessage(mobileNumber, msgControlsInfo.getReply());
							msg.setMessageEncoding(CMessage.MessageEncoding.Enc7Bit);
							service.sendMessage(msg);				        	
				        } else if(status == 0){//If it is unlimited public message license or it hasn't reached the limit of 200 messages yet
				        	
				        	Date now = new Date();
			    			System.out.println("Public Msg <Date>:" + DateFormat.getDateTimeInstance().format(now) + "  <Type>:" + 
			    					SERVER_TYPE + "  <From>:"  + userKey + "  <Query>:" + textMessage);				        	

					        MessagingSettingDetails msgSettInfo = dataAgent.getGuestMsgInfo("Email");
							String searchTerm = msgSettInfo.getSearchTerm();
							
				        	if(smsKeyword.equalsIgnoreCase(searchTerm)){
				        		String result;
				        		ArrayList<String> userInputsList = (ArrayList<String>)userInputsMap.get("userInputs");
				        		
				        		if(userInputsList == null || userInputsList.isEmpty()){
				    				result = MessagingUtils.getAllQueriesList(SERVER_TYPE, file);
				    			} else {
				    				String userInput = userInputsList.get(0);
				    				result = MessagingUtils.searchQueryKeyword(SERVER_TYPE, file, userInput);
				    			}
								COutgoingMessage msg = new COutgoingMessage(mobileNumber, result);
								msg.setMessageEncoding(CMessage.MessageEncoding.Enc7Bit);
								service.sendMessage(msg);		        					    			
					        }else{
				    			MessageObject messagingInfo = messageManager.getSMSMessageObject(smsKeyword.toLowerCase(), GSM_SERVER_ID);
						        if(messagingInfo != null) {
									msgTxt = messageManager.getReplyingMessageForGuestUser(userInputsMap, messagingInfo, file);
									if(msgTxt != null && !msgTxt.trim().equals("")){
										/*
											String mimeType = "text/plain";
											String encoding = "UTF-8";
											String text = "Hello from my java midlet";
											byte[] contents = text.getBytes(encoding);
											MessagePart msgPart = new MessagePart(contents, 0, contents.length, mimeType, "id1", "contentLocation", encoding);
											mulMsg.addMessagePart(msgPart);
											
											The MessagePart constructor will take all the header parameters and the message content as a byte array.
											
											Here we add a picture to the MMS:
											
											InputStream is = getClass().getResourceAsStream("image.jpg");
											mimeType = "image/jpeg";
											contents = new byte[is.available()];
											is.read(contents);
											msgPart = new MessagePart(contents, 0, contents.length, mimeType, "id2", "bg.jpg", null);
											mulMsg.addMessagePart(msgPart); 
										 */
										
										COutgoingMessage msg = new COutgoingMessage(mobileNumber, msgTxt);
										msg.setMessageEncoding(CMessage.MessageEncoding.Enc7Bit);
										service.sendMessage(msg);
										
										messageManager.catchMessageDataInSession(messagingInfo);
										System.out.println("Sent response successfull for this sms : " + smsKeyword);
									}else{
										System.out.println("Invalid SMS Value!");
									}
								}else{
									System.out.println("Invalid SMS Keyword!");
								}
					        }
							
							
							
				        }else{
				        	System.out.println("This license is limited for 200 public messages and it has reached the limit of 200 messages sent through Mobile Data Now to public users");
				        }
					}else{
						System.out.println("This contact is block! >> " + mobileNumber);
					}
				}
			}
			catch (Exception e)
			{
				System.out.println("Could not send reply message!");
				e.printStackTrace();
			}
			// Return false to leave the message in memory - otherwise return true to delete it.
			return true;
		}
	}
	
	public void runSmsServer(CService service, MsgSessionManager session){
		boolean limitation = dataAgent.getPublicGroupFlag();//Unlimited = true, limited = false
		unlimited = limitation;
		
		availablePublicMessages = dataAgent.getAvailablePublicMessages();
		
		//IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		MessagingSettingDetails guestObj = null;
		try {
			guestObj = dataAgent.getGuestMsgInfo(SERVER_TYPE);
			session.setAttribute(MessageManager.GUEST_OBJ, guestObj);
			
    		LanguageDobj defaultLanguage = dataAgent.getDefaultLanguage();
    		String file = defaultLanguage.getFileName();
    		session.setAttribute(Constants.SESSION_LANGUAGE_FILE, file);			
			
		} catch (MdnException e1) {
			e1.printStackTrace();
		}
		
		connect(service, session);
		service.setCallHandler(callListener);

		// Set the message callback class.
		service.setMessageHandler(smsMessageListener);

		// Set the polling interval in seconds.
		service.setAsyncPollInterval(10);

		// Set the class of the messages to be read.
		service.setAsyncRecvClass(CIncomingMessage.MessageClass.All);

		// Switch to asynchronous POLL mode.
		try {
			service.setReceiveMode(CService.ReceiveMode.AsyncPoll);
		} catch (Exception e) {
			e.printStackTrace();
		}
		MessageManager.runTimeoutThread(session, SERVER_TYPE);
	}
	
	public void disconnect(CService service, HttpSession session){
		try {
			service.disconnect();
			session.removeAttribute("SmsConnects");
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public static boolean getLimitation(){
		return unlimited;
	}	
	
	public static int getAvailablePubliicMessages(){
		return availablePublicMessages;
	}	
	
	public static void main(String[] args)
	{
		MdnSmsSetting smsSetting = null;
		try {
			smsSetting = dataAgent.getSmsSetting();
		} catch (MdnException e) {
			e.printStackTrace();
		}
		if(smsSetting != null){
			String comm = smsSetting.getComm();
			String baudrate = smsSetting.getBaudrate();
			String modemMan = smsSetting.getModemManufacturer();
			String modemModel = smsSetting.getmodemModel();
			
			if(comm!= null && baudrate != null){
				MsgSessionManager session = new MsgSessionManager();
				CService service = new CService(comm, Integer.parseInt(baudrate), modemMan, modemModel);
				MdnSmsServer server = new MdnSmsServer();
				server.runSmsServer(service, session);
			}
		}else{
			System.out.println("No MDN GSM Modem has been configured yet!");
		}
	}
}

