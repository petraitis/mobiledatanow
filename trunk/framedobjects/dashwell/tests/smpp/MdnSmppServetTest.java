package com.framedobjects.dashwell.tests.smpp;

import ie.omk.smpp.Address;
import ie.omk.smpp.BadCommandIDException;
import ie.omk.smpp.Connection;
import ie.omk.smpp.UnsupportedOperationException;
import ie.omk.smpp.event.ConnectionObserver;
import ie.omk.smpp.event.ReceiverExitEvent;
import ie.omk.smpp.event.SMPPEvent;
import ie.omk.smpp.message.EnquireLink;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.message.SMPPProtocolException;
import ie.omk.smpp.message.SubmitSM;
import ie.omk.smpp.message.tlv.TLVTable;
import ie.omk.smpp.message.tlv.Tag;
import ie.omk.smpp.util.EncodingFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import wsl.fw.datasource.DataSourceException;
import wsl.fw.exception.MdnException;
import wsl.fw.security.User;
import wsl.mdn.dataview.LanguageDobj;
import wsl.mdn.mdnmsgsvr.MdnSmpp;
import wsl.mdn.mdnmsgsvr.MessageLog;
import wsl.mdn.mdnmsgsvr.MessagingSettingDetails;
import wsl.mdn.mdnmsgsvr.TemporaryBlockContacInfo;

import com.framedobjects.dashwell.biz.GuestMessagesInfo;
import com.framedobjects.dashwell.biz.MessageObject;
import com.framedobjects.dashwell.biz.MsgSessionManager;
import com.framedobjects.dashwell.db.DataAgentFactory;
import com.framedobjects.dashwell.db.IDataAgent;
import com.framedobjects.dashwell.utils.Constants;
import com.framedobjects.dashwell.utils.MessageManager;
import com.framedobjects.dashwell.utils.MessagingUtils;

public class MdnSmppServetTest  extends Thread implements ConnectionObserver {

	private Connection gatewayConnection;
	private static MdnSmppServetTest instance;
	private MdnSmpp smsGateway;
	private boolean exit = false;
	
	public static String SERVER_TYPE = "SMPP";
	public static String SERVER_TYPE_ID = "4";	
	
	private MsgSessionManager sessionManager;
	private boolean tempBlock = false; 
	private static boolean unlimited;
	private static int availablePubliicMessages;

	static IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	
	private MdnSmppServetTest(MdnSmpp smsGateway){
		this.smsGateway = smsGateway;
		init();
	}
	
	public static MdnSmppServetTest getInstance(MdnSmpp smsGateway) {
		if (instance == null)
			instance = new MdnSmppServetTest(smsGateway);
		return instance;
	}

	public static boolean getLimitation(){
		return unlimited;
	}
	
	public static int getAvailablePubliicMessages(){
		return availablePubliicMessages;
	}		
	
	public void setSessionManager(MsgSessionManager sessionManager){
		this.sessionManager = sessionManager;
	}
	public MsgSessionManager getSessionManager(){
		return sessionManager;
	}
	
	private void init(){
		MsgSessionManager session = new MsgSessionManager();
		
		boolean limitation = dataAgent.getPublicGroupFlag();//Unlimited = true, limited = false
		unlimited = limitation;
		
		int availablePubliicMessages = dataAgent.getAvailablePublicMessages();
		
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
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
		setSessionManager(session);
	}
	
	private void connect() {
		String host = smsGateway.getHost();
		String port = smsGateway.getPort();
		String username = smsGateway.getUsername();
		String password = smsGateway.getPassword();
		String npi = smsGateway.getSourceNPI();
		String ton = smsGateway.getSourceTON();		
		
		try {
			gatewayConnection = new Connection(host, Integer.parseInt(port), true);//asynchronous
			gatewayConnection.addObserver(this);
		} catch (UnknownHostException uhe) {
			System.out.println("host or port is incorrect!");
			//System.exit(0);
		}
		
		boolean retry = false;
		while (!retry) {
			try {
				if(npi != null && !npi.equals("") && ton!= null && !ton.equals(""))
					gatewayConnection.bind(Connection.TRANSCEIVER, username, password, "SMPP", Integer.parseInt(npi), Integer.parseInt(ton), null);
				else
					gatewayConnection.bind(Connection.TRANSCEIVER, username, password, "SMPP");
				
		        gatewayConnection.autoAckLink(true);
		        gatewayConnection.autoAckMessages(true);
		        
				retry = true;
			} catch (IOException ioe) {
				System.out.println("bind error!!!");
				//ioe.printStackTrace();
			}
		}
	}	

	public void run() {
		while (!exit) {
			long startSrvs = new Date().getTime();
			long checkTime = (smsGateway.getInterval())*1000;
			connect();
			int freq = Integer.parseInt("100");
			for (;;) {
				try {
					Thread.sleep(freq);//thread awake any 100 milisec to check received sms 
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				long rightNow = new Date().getTime();
				long diff = rightNow - startSrvs;
				if(checkTime != 0 && diff > checkTime)
				{
					startSrvs = new Date().getTime();
					try {
						//System.out.println("send enquire link  to chech this connection ; ");
						if(gatewayConnection.isBound()){
							EnquireLink activeConnRequest = (EnquireLink)gatewayConnection.newInstance(SMPPPacket.ENQUIRE_LINK);
							gatewayConnection.sendRequest(activeConnRequest);
						}
					} catch (BadCommandIDException e) {
						e.printStackTrace();
					} catch (SMPPProtocolException e) {
						e.printStackTrace();
					} catch (UnsupportedOperationException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}									
				}
				
				MsgSessionManager session = MessageManager.getSession();
				if(session != null){
	    			Map urStartTimeMap = (Map)session.getAttribute(MessageManager.UR_START_TIMEOUT_MAP);//key = userId , value = startTime
		            Map urTimeoutMap = (Map)session.getAttribute(MessageManager.UR_TIMEOUT_KEY);//key = userId , value = UserReply timeout
		    		Map urParentMap = (Map)session.getAttribute(MessageManager.UR_PARENT_MAP);
		    		
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
			        		
			        		if(timeout > 0 && now > end){//TIMEOUT
			        			System.out.println("----------------------------------------> Timeout period expired (userId : " + userIdKey +" )");
			            		urStartTimeMap.remove(userIdKey);
			            		session.setAttribute(MessageManager.UR_START_TIMEOUT_MAP, urStartTimeMap);

			            		urTimeoutMap.remove(userIdKey);
			            		session.setAttribute(MessageManager.UR_TIMEOUT_KEY, urTimeoutMap);
			            		
			    	        	if(urParentMap.containsKey(userIdKey)){
			    	        		urParentMap.remove(userIdKey);
			    	        		session.setAttribute(MessageManager.UR_PARENT_MAP, urParentMap); 
			    	        	}
			    	        	itMap = urTimeoutMap.keySet().iterator();
			        		}
			            }
					}
				}				
			}
		}
	}
	public void packetReceived(Connection conn, SMPPPacket pack) {
		switch(pack.getCommandId()) {
			case SMPPPacket.DELIVER_SM : 
				try {
					String replyMsg = processRequest(pack);

					SubmitSM sm = (SubmitSM)gatewayConnection.newInstance(SMPPPacket.SUBMIT_SM);
					if(replyMsg != null && !replyMsg.equals("")){
						sm.setDestination(pack.getSource());
						if(smsGateway.getNumber() != null)
							sm.setSource(new Address(0, 0, smsGateway.getNumber()));
						
						int smsSize = replyMsg.length();
						System.out.println("result size = " + smsSize);
						if(smsSize <= 150){
							sm.setMessageText(replyMsg);
							gatewayConnection.sendRequest(sm);					
						}else{
//     start				List msgList= splitLongMsg(replyMsg);
//							int totalSeqment =  msgList.size();
//							System.out.println("Total parts Number: " + totalSeqment);							
//							int refNum = generateReferenceNumber();//new Integer(357);
//							for(int i = 0; i< totalSeqment; i++){
//								String smsPart = (String)msgList.get(i);
//								sm.setMessage(smsPart.getBytes(),EncodingFactory.getInstance().getDefaultAlphabet());
//								TLVTable tvl = getUDHHeader(smsPart, refNum, new Integer(i+1), totalSeqment);
//								sm.setTLVTable(tvl);
//								gatewayConnection.sendRequest(sm);//SMPPResponse resp =
//   end					}
							////////////////////////////////////////////////////////start
				            byte[] msgBytes = replyMsg.getBytes();
				            ByteArrayInputStream ms = new ByteArrayInputStream(msgBytes);
				            int len = msgBytes.length;//ms.length
				            
				            int totalAmount = ((int)len / 153) + 1;
				            int i = 0;
				            byte[] temp;
				            //Random x = new Random(); // This random number is because every Concatenated message 
				            int refernce = generateReferenceNumber();//x.nextInt(255); //need a reference number so that they are not mixed.
				            System.out.println("+++++ totalAmount = " + totalAmount);
				            for(i=1;i<=totalAmount ;i++)
				            {
				               int arraycount = 159;
				               if(i * 153 > len)
				                  arraycount = ((int)len - (153 * (i -1))) + 6;
				               temp = new byte[arraycount];
				               temp[0] = 0x05;
				               temp[1] = 0x00;
				               temp[2] = 0x03;
				               temp[3] = Byte.parseByte(String.valueOf(refernce));  //refrence number
				               temp[4] = Byte.parseByte(String.valueOf(totalAmount)); // Total number of messages
				               temp[5] = Byte.parseByte(String.valueOf(i)); //current message number
				               
				               ms.read(temp,6,arraycount - 6);
				               //ms.write(temp, 6, arraycount-6);//msRead(temp,6,arraycount - 6);
				               //temp = pack8Bits(temp); //this is uncommented if you need to pack the data.
				               
				               sm.setEsmClass((byte)0x40);
				               sm.setMessage(temp, EncodingFactory.getInstance().getDefaultAlphabet());
				               gatewayConnection.sendRequest(sm);
				            }
							////////////////////////////////////////////////////////end
						}
						System.out.println("Sent response successfull for this sms : " + pack.getMessageText());
					}else{
						System.out.println("No result for this keyword");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case SMPPPacket.BIND_TRANSCEIVER_RESP :
				if (pack.getCommandStatus() != 0) {
					System.out.println("Error binding: " + pack.getCommandStatus());
					exit = true;
					synchronized(this) {
						notify();
					}
				} else {
					System.out.println("SMPP Gateway Connected/Bounded successfully!");
				}
			case SMPPPacket.SUBMIT_SM_RESP :
				//System.out.println("got SUBMIT_SM_RESP for this msg = " + pack.getMessageText());
				break;
		}
	}

	public static byte[] pack8Bits(byte[] NormalData)
	{
		  int i = 0; //Pointer for the NormalData
		  int j = 0; //Pointer for the PackedData
		  int DLWOHeader = NormalData.length - 7; //Data Length With Out Header :)
		  int PackedDataLength = ((DLWOHeader) - (DLWOHeader/8)) + 7; //Length of packed data will be equalt to Normal length - (Normal length / 8) + Header Length
		  byte[] PackedData = new byte[PackedDataLength]; 
		  for(j=0;j<6;j++)
		     PackedData[j] = NormalData[j]; //copy the header because it should not be packed
		  PackedData[j] = (byte)(NormalData[j] << 1); //This is just for long message because the header is 6 bytes 0500ff030201 then the first byte of the data will be the end of the octan and it must be shifted by 1 bit always.
		  j++;			
				
		  for(i=7;i<NormalData.length ;i++)
		  {
		    if(i != NormalData.length -1)
		       PackedData[j] = (byte)((NormalData[i] >> ((i+1) % 8))|(NormalData[i+1] << (7 - ((i+1) % 8)))); //It is hard to explain but trust me it is working :-)
		    else
		       PackedData[j] = (byte)((NormalData[i] >> ((i+1) % 8))|(byte)0x00); //If the number of bytes is not a multiple of 7 then we must pack with zero at the end GSM specification :-)
		    j++;
		    if((j % 7) == 0 && j != 7) //this is because every 8 bytes is packed into 7 bytes :-) so we skip any multiple of 8 and j!= 8 because the header is not packed so no byte to skip
		    i++;
		  }
		  return PackedData;
	}	
	private String processRequest(SMPPPacket request) throws BadCommandIDException {//SubmitSM
		String textMessage = request.getMessageText();
		Address sourceAddress = request.getSource();
		String mobileNumber = sourceAddress.getAddress();

		System.out.println("This SMS received ( " + textMessage + ") from this number (" + mobileNumber + ")");
		
		boolean isUnlimited = unlimited;
		int availablePubliicMessages = getAvailablePubliicMessages();
		String userKey;
		String smsKeyword="";
		String replyMsg = null; 
		String file = (String)getSessionManager().getAttribute(Constants.SESSION_LANGUAGE_FILE);
		SubmitSM sm = (SubmitSM)gatewayConnection.newInstance(SMPPPacket.SUBMIT_SM);
		try
		{
			String msgTxt = "";
			
			String separator = MessageManager.getMdnSeparator();
			
			Map userInputsMap = MessagingUtils.userInputsParser(separator, textMessage);
			smsKeyword = (String)userInputsMap.get("keyword");
			
			MessageManager messageManager = null;

			User mdnUser = dataAgent.getUserByMobileNumber(mobileNumber);
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
				
	        	if(smsKeyword.equalsIgnoreCase(searchTerm)){
	        		String result;
	        		ArrayList<String> userInputsList = (ArrayList<String>)userInputsMap.get("userInputs");
	        		
	        		if(userInputsList == null || userInputsList.isEmpty()){
	    				result = MessagingUtils.getAllQueriesList(SERVER_TYPE, file);
	    			} else {
	    				String userInput = userInputsList.get(0);
	    				result = MessagingUtils.searchQueryKeyword(SERVER_TYPE, file, userInput);
	    			}
	        		replyMsg = result;
					//sm.setMessageText(result);
		        }else{	
			        MessageObject messagingInfo = messageManager.getSMSMessageObject(smsKeyword.toLowerCase(), 0);
					if(messagingInfo != null) {
						msgTxt = messageManager.getReplyingMessage(mdnUser.getId(), userInputsMap, messagingInfo, file);
						if(msgTxt != null && !msgTxt.equals("")){
							//sm.setMessageText(msgTxt);
							replyMsg = msgTxt;
							messageManager.catchMessageDataInSession(messagingInfo);
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

				String mobileNumberForCheckBlock = userKey;
				if(!userKey.contains("+"))
					mobileNumberForCheckBlock = "+"+userKey;
				
				if(!permanentBlockList.containsKey(mobileNumberForCheckBlock)){
					
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
		    			messageManager.editGuestInfoInSession(SERVER_TYPE, isUnlimited, availablePubliicMessages);
		    		
			        int status = guestObj.getStatus();	
			        if(isUnlimited)
			        	status=0;
			        
			        if(tempBlock){
			        	replyMsg = msgControlsInfo.getReply();
			        	//sm.setMessageText(msgControlsInfo.getReply());				        	
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
			        		replyMsg = result;
							//sm.setMessageText(result);
				        }else{
			    			MessageObject messagingInfo = messageManager.getSMSMessageObject(smsKeyword.toLowerCase(), 0);
					        if(messagingInfo != null) {
								msgTxt = messageManager.getReplyingMessageForGuestUser(userInputsMap, messagingInfo, file);
								if(msgTxt != null && !msgTxt.trim().equals("")){
									replyMsg = msgTxt;
									//sm.setMessageText(msgTxt);
									messageManager.catchMessageDataInSession(messagingInfo);
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
		
		return replyMsg;
	}	
	
	public void update(Connection arg0, SMPPEvent event) {
		if (event.getType() == SMPPEvent.RECEIVER_EXCEPTION && !gatewayConnection.isBound()) {
			System.out.println("RECEIVER_EXCEPTION Event");
			connect();
		}else if (event.getType() == SMPPEvent.RECEIVER_EXIT && ((ReceiverExitEvent)event).isException()&& !gatewayConnection.isBound()) {
			System.out.println("RECEIVER_EXIT Event");
			connect();
		}		
	}
	
	private TLVTable getUDHHeader(String smsPart, int refNum, int seqNum, int totalSeqment) {
		TLVTable tvl = new TLVTable(); 
		tvl.set(Tag.SAR_MSG_REF_NUM, refNum);
		tvl.set(Tag.SAR_SEGMENT_SEQNUM, seqNum);					
		tvl.set(Tag.SAR_TOTAL_SEGMENTS, new Integer(totalSeqment));
		return tvl;
	}

	private int generateReferenceNumber() {
        Random x = new Random(); // This random number is because every Concatenated message 
        int refernce = x.nextInt(55); //need a reference number so that they are not mixed.		
		 System.out.println("::::::::::::: refNum = " + refernce);
		 return refernce;
	}
	
	private static List splitLongMsg(String longMsg){
		int start=0;
		int limitedLenght = 150;
		List msgItemList = new ArrayList<String>();
		String smsPart;
		while(limitedLenght < longMsg.length()){
			smsPart = longMsg.substring(start, limitedLenght);
			msgItemList.add(smsPart);
			//System.out.println("smsPart: " + smsPart);
			longMsg = longMsg.substring(smsPart.length());
		}
		smsPart = longMsg.substring(start);
		//System.out.println("last smsPart: " + smsPart);
		msgItemList.add(smsPart);		

		return msgItemList;
	}	
	
	public static void main(String[] args) {
		MdnSmpp smsGateway = null;
		try {
			smsGateway = dataAgent.getSmsGatewayByNumber("123");//TODO
		} catch (MdnException e1) {
			e1.printStackTrace();
		}
		
		if(smsGateway == null){
			System.out.println("No MDN SMS Gateway has been configured yet!");
		}else{
			MdnSmppServetTest gatewayServer = MdnSmppServetTest.getInstance(smsGateway);
			gatewayServer.run();			
		}
	}
}

