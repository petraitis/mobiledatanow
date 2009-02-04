package com.framedobjects.dashwell.mdnsms;

import ie.omk.smpp.Address;
import ie.omk.smpp.AlreadyBoundException;
import ie.omk.smpp.BadCommandIDException;
import ie.omk.smpp.Connection;
import ie.omk.smpp.UnsupportedOperationException;
import ie.omk.smpp.event.ConnectionObserver;
import ie.omk.smpp.event.ReceiverExitEvent;
import ie.omk.smpp.event.SMPPEvent;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.message.SMPPProtocolException;
import ie.omk.smpp.message.SubmitSM;
import ie.omk.smpp.message.tlv.TLVTable;
import ie.omk.smpp.message.tlv.Tag;
import ie.omk.smpp.util.EncodingFactory;
import ie.omk.smpp.version.VersionException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import wsl.fw.datasource.DataSourceException;
import wsl.fw.security.User;
import wsl.mdn.mdnmsgsvr.MdnSmpp;
import wsl.mdn.mdnmsgsvr.MessageLog;
import wsl.mdn.mdnmsgsvr.MessagingSettingDetails;
import wsl.mdn.mdnmsgsvr.TemporaryBlockContacInfo;

import com.framedobjects.dashwell.biz.GuestMessagesInfo;
import com.framedobjects.dashwell.biz.MdnSmppServerObject;
import com.framedobjects.dashwell.biz.MessageObject;
import com.framedobjects.dashwell.biz.MsgSessionManager;
import com.framedobjects.dashwell.db.DataAgentFactory;
import com.framedobjects.dashwell.db.IDataAgent;
import com.framedobjects.dashwell.utils.Constants;
import com.framedobjects.dashwell.utils.MessageManager;
import com.framedobjects.dashwell.utils.MessagingUtils;

public class MdnConnectionObserver implements ConnectionObserver {
	
	private IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	
	private MsgSessionManager sessionManager;
	private boolean tempBlock = false; 
	private boolean unlimited;
	private int availablePublicMessages;
	private MdnSmppServerObject smppServerObject;
	
	public void setLimitation(boolean unlimited){
		this.unlimited = unlimited;
	}			
	public boolean getLimitation(){
		return unlimited;
	}	
	public void setSessionManager(MsgSessionManager sessionManager){
		this.sessionManager = sessionManager;
	}
	public MsgSessionManager getSessionManager(){
		return sessionManager;
	}		
	
	public void setSmppServerObject(MdnSmppServerObject smppServerObject){
		this.smppServerObject = smppServerObject;
	}
	public MdnSmppServerObject getSmppServerObject(){
		return smppServerObject;
	}		
	
	public void setAvailablePubliicMessages(int availablePubliicMessages){
		this.availablePublicMessages = availablePubliicMessages;
	}			
	public int getAvailablePubliicMessages(){
		return availablePublicMessages;
	}	
	
	public void packetReceived(Connection conn, SMPPPacket pack) {
		switch(pack.getCommandId()) {
			case SMPPPacket.DELIVER_SM :
				System.out.println("Received DELIVER_SM Report from >>>>>>>>>" + smppServerObject.getNumber());
				try {
					String replyMsg = prepareReplyMessage(pack);

					SubmitSM sm = (SubmitSM)smppServerObject.getSmppConnection().newInstance(SMPPPacket.SUBMIT_SM);
					if(replyMsg != null && !replyMsg.equals("")){
						Address destUserAddress = pack.getSource();
						if(!MessageManager.isBlank(smppServerObject.getDestTON()))
							destUserAddress.setTON(Integer.parseInt(smppServerObject.getDestTON()));
						if(!MessageManager.isBlank(smppServerObject.getDestNPI()))
							destUserAddress.setNPI(Integer.parseInt(smppServerObject.getDestNPI()));
						sm.setDestination(destUserAddress);
						//pack.getSource().setTON(1);
						//sm.setDestination(pack.getSource());
						
						String sourceNumber = smppServerObject.getNumber();
						sourceNumber = sourceNumber.replace("+", "");

						String sourceTonStr = smppServerObject.getSourceTON();
						String sourceNpiStr = smppServerObject.getSourceNPI();
						if(MessageManager.isBlank(sourceTonStr))
							sourceTonStr = "1";//default value
						if(MessageManager.isBlank(sourceNpiStr))
							sourceNpiStr = "1";//default value
						int sourceTon = Integer.parseInt(sourceTonStr);
						int sourceNpi = Integer.parseInt(sourceNpiStr);
						sm.setSource(new Address(sourceTon, sourceNpi, sourceNumber));
						
						//sm set DataCoding 0x80 for unicode 
						int smsSize = replyMsg.length();
						System.out.println("result size = " + smsSize);
						if(smsSize <= 150){
/*							UTF16Encoding encoding;
							try {
								encoding = new UTF16Encoding(true);
				            	byte[] encoded = encoding.encodeString("simple text");
				            	System.out.println("utf:::::>>" + encoded);
				            	String decoded = encoding.decodeString(encoded);
				            	System.out.println("text :::::>>" + decoded);					
				            	
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
*/							
							sm.setMessageText(replyMsg);
							
							smppServerObject.getSmppConnection().sendRequest(sm);					
						}else{ //Handel Concatenated long messages
							if(smppServerObject.getUseTlv() == 1){
								System.out.println("Use TLV");
								sendConcatenatedMsgByTLV(sm, replyMsg);
							}else{
								System.out.println("Use 7-bit packed encoding");
								sendConcatenatedMsgBy7BitPacked(sm, replyMsg);
							}
						}
						System.out.println(DateFormat.getDateTimeInstance().format(new Date()) + "  Sent response successfull for this sms : " + pack.getMessageText());
					}else{
						System.out.println("No result for this keyword");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case SMPPPacket.BIND_TRANSCEIVER_RESP :
				if (pack.getCommandStatus() != 0) {
					System.out.println(DateFormat.getDateTimeInstance().format(new Date()) + "  " +smppServerObject.getNumber() + "has error binding: " + pack.getCommandStatus());
					MdnSmppGatewayServer.exit = true;
					synchronized(this) {
						notify();
					}
				} else {
					System.out.println(DateFormat.getDateTimeInstance().format(new Date()) + "  " + smppServerObject.getHost() + " Bound successfully!");
				}
			case SMPPPacket.SUBMIT_SM_RESP :
				//System.out.println("got SUBMIT_SM_RESP for this msg = " + pack.getMessageText());
				break;
			case SMPPPacket.SUBMIT_SM:
				System.out.println("Received SUBMIT_SM " + pack.getMessageText());
				break;				
		}
	}
	private void sendConcatenatedMsgByTLV(SubmitSM sm, String longRepliedMsg) {
		List msgList= splitLongMsg(longRepliedMsg);
		int totalSeqment =  msgList.size();
		System.out.println("Total parts Number: " + totalSeqment);							
		int refNum = generateReferenceNumber();//new Integer(357);
		for(int i = 0; i< totalSeqment; i++){
			String smsPart = (String)msgList.get(i);
			
			sm.setMessage(smsPart.getBytes(),EncodingFactory.getInstance().getDefaultAlphabet());
			TLVTable tlv = setOptionalTlvTag(smsPart, refNum, new Integer(i+1), totalSeqment);
			sm.setTLVTable(tlv);
			
			try {
				smppServerObject.getSmppConnection().sendRequest(sm);
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
			} catch (AlreadyBoundException e) {
				e.printStackTrace();
			} catch (VersionException e) {
				e.printStackTrace();
			} catch (SMPPProtocolException e) {
				e.printStackTrace();
			} catch (UnsupportedOperationException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
	}
	private void sendConcatenatedMsgBy7BitPacked(SubmitSM sm, String longRepliedMsg){
        byte[] msgBytes = longRepliedMsg.getBytes();
        ByteArrayInputStream ms = new ByteArrayInputStream(msgBytes);
        int len = msgBytes.length;
        
        int totalAmount = ((int)len / 153) + 1;
        int i = 0;
        byte[] temp;
        int refernce = generateReferenceNumber();//need a reference number so that they are not mixed.
        System.out.println("total part = " + totalAmount);
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
           
           // sm set DataCoding 0x80 for unicode
           sm.setEsmClass((byte)0x40);
           sm.setMessage(temp, EncodingFactory.getInstance().getDefaultAlphabet());
           try {
        	   smppServerObject.getSmppConnection().sendRequest(sm);
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
			} catch (AlreadyBoundException e) {
				e.printStackTrace();
			} catch (VersionException e) {
				e.printStackTrace();
			} catch (SMPPProtocolException e) {
				e.printStackTrace();
			} catch (UnsupportedOperationException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }		
	}
	
	private String prepareReplyMessage(SMPPPacket request) throws BadCommandIDException {
		String receivedTextMessage = request.getMessageText();
		Address sourceAddress = request.getSource();
		String mobileNumber = sourceAddress.getAddress();

		System.out.println(DateFormat.getDateTimeInstance().format(new Date()) + "  This SMS received ( " + receivedTextMessage + ") from this number (" + mobileNumber + ")" );
		
		boolean isUnlimited = unlimited;
		String userKey;
		String smsKeyword="";
		String replyMsg = null; 
		String file = (String)getSessionManager().getAttribute(Constants.SESSION_LANGUAGE_FILE);
		try
		{
			String msgTxt = "";
			
			String separator = MessageManager.getMdnSeparator();
			
			Map userInputsMap = MessagingUtils.userInputsParser(separator, receivedTextMessage);
			smsKeyword = (String)userInputsMap.get("keyword");
			
			MessageManager messageManager = null;

			String userNumber = mobileNumber;
			if(!mobileNumber.contains("+"))
				userNumber = "+" + mobileNumber;
				
			User mdnUser = dataAgent.getUserByMobileNumber(userNumber);
			if(mdnUser != null){
				System.out.println("MDN User");
				//Save Email Message Log
				MessageLog msgLog = new MessageLog();
				msgLog.setText(receivedTextMessage);
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
	        		
	        		//String file = (String)getSessionManager().getAttribute(Constants.SESSION_LANGUAGE_FILE);

	        		if(userInputsList == null || userInputsList.isEmpty()){
	    				result = MessagingUtils.getAllQueriesList(MdnSmppGatewayServer.SERVER_TYPE, file);
	    			} else {
	    				String userInput = userInputsList.get(0);
	    				result = MessagingUtils.searchQueryKeyword(MdnSmppGatewayServer.SERVER_TYPE, file, userInput);
	    			}
	        		replyMsg = result;
		        }else{	
		        	MdnSmpp smpp = dataAgent.getSmsGatewayByNumber(getSmppServerObject().getNumber());
			        //MessageObject messagingInfo = messageManager.getSMSMessageObject(smsKeyword.toLowerCase(), smpp.getId());
		        	MessageObject messagingInfo = messageManager.getSmppMessageObject(smpp.getId(), receivedTextMessage, separator);
					if(messagingInfo != null) {
						msgTxt = messageManager.getAdvanceReplyingMessage(mdnUser.getId(), messagingInfo, file);
						if(msgTxt != null && !msgTxt.equals("")){
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
				Map permanentBlockList = dataAgent.getBlockContactsByType(MdnSmsServer.SERVER_TYPE_ID);//Use one msg type for block sms number (in GSM and SMPP services)

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
		    			messageManager.editGuestInfoInSession(MdnSmppGatewayServer.SERVER_TYPE, isUnlimited, availablePublicMessages);
		    		
			        int status = guestObj.getStatus();	
			        if(isUnlimited)
			        	status=0;
			        
			        if(tempBlock){
			        	replyMsg = msgControlsInfo.getReply();
			        } else if(status == 0){//If it is unlimited public message license or it hasn't reached the limit of 200 messages yet
				        MessagingSettingDetails msgSettInfo = dataAgent.getGuestMsgInfo("Email");
						String searchTerm = msgSettInfo.getSearchTerm();
						
			        	if(smsKeyword.equalsIgnoreCase(searchTerm)){
			        		String result;
			        		ArrayList<String> userInputsList = (ArrayList<String>)userInputsMap.get("userInputs");
			        		
			        		//String file = (String)getSessionManager().getAttribute(Constants.SESSION_LANGUAGE_FILE);

			        		if(userInputsList == null || userInputsList.isEmpty()){
			    				result = MessagingUtils.getAllQueriesList(MdnSmppGatewayServer.SERVER_TYPE, file);
			    			} else {
			    				String userInput = userInputsList.get(0);
			    				result = MessagingUtils.searchQueryKeyword(MdnSmppGatewayServer.SERVER_TYPE, file, userInput);
			    			}
			        		replyMsg = result;
				        }else{
				        	MdnSmpp smpp = dataAgent.getSmsGatewayByNumber(getSmppServerObject().getNumber());					        	
			    			MessageObject messagingInfo = messageManager.getSmppMessageObject(smpp.getId(), receivedTextMessage, separator);
					        if(messagingInfo != null) {
								msgTxt = messageManager.getReplyingMessageForGuestUser(null, messagingInfo, file);
								if(msgTxt != null && !msgTxt.trim().equals("")){
									replyMsg = msgTxt;
									messageManager.catchMessageDataInSession(messagingInfo);
								}else{
									System.out.println("Invalid SMS Value!");
								}
							}else{
								System.out.println("Invalid SMS Keyword!");
							}
				        }
			        }else{
			        	System.out.println("This license is limited for " + availablePublicMessages + " public messages and it has reached the limit of 200 messages sent through Mobile Data Now to public users");
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
		if (event.getType() == SMPPEvent.RECEIVER_EXCEPTION && !smppServerObject.getSmppConnection().isBound()) {
			System.out.println(DateFormat.getDateTimeInstance().format(new Date()) + "  RECEIVER_EXCEPTION Event");
			MdnSmppGatewayServer smppServer = new MdnSmppGatewayServer();
			smppServer.connect(smppServerObject.getMdnSmsGateway());
		}else if (event.getType() == SMPPEvent.RECEIVER_EXIT && ((ReceiverExitEvent)event).isException()&& !smppServerObject.getSmppConnection().isBound()) {
			System.out.println(DateFormat.getDateTimeInstance().format(new Date()) + "  RECEIVER_EXIT Event");
			MdnSmppGatewayServer smppServer = new MdnSmppGatewayServer();			
			smppServer.connect(smppServerObject.getMdnSmsGateway());
		}		
	}
	
	private TLVTable setOptionalTlvTag(String smsPart, int refNum, int seqNum, int totalSeqment) {
		TLVTable tlv = new TLVTable(); 
		tlv.set(Tag.SAR_MSG_REF_NUM, refNum);
		tlv.set(Tag.SAR_SEGMENT_SEQNUM, seqNum);					
		tlv.set(Tag.SAR_TOTAL_SEGMENTS, new Integer(totalSeqment));
		return tlv;
	}

	private int generateReferenceNumber() {
        Random x = new Random(); // This random number is because every Concatenated message 
        int refernce = x.nextInt(55); //need a reference number so that they are not mixed.		
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
			longMsg = longMsg.substring(smsPart.length());
		}
		smsPart = longMsg.substring(start);
		msgItemList.add(smsPart);		

		return msgItemList;
	}	
	
}
