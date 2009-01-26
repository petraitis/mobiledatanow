package com.framedobjects.dashwell.mdnsms;

import ie.omk.smpp.BadCommandIDException;
import ie.omk.smpp.Connection;
import ie.omk.smpp.UnsupportedOperationException;
import ie.omk.smpp.message.EnquireLink;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.message.SMPPProtocolException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import wsl.fw.exception.MdnException;
import wsl.mdn.dataview.LanguageDobj;
import wsl.mdn.mdnmsgsvr.MdnSmpp;
import wsl.mdn.mdnmsgsvr.MessagingSettingDetails;

import com.framedobjects.dashwell.biz.MdnSmppServerObject;
import com.framedobjects.dashwell.biz.MsgSessionManager;
import com.framedobjects.dashwell.db.DataAgentFactory;
import com.framedobjects.dashwell.db.IDataAgent;
import com.framedobjects.dashwell.utils.Constants;
import com.framedobjects.dashwell.utils.MessageManager;

public class MdnSmppGatewayServer extends Thread {

	public static List<MdnSmppServerObject> smppServerObjectList = new ArrayList<MdnSmppServerObject>();
	private static MdnSmppGatewayServer instance;
	public static List<MdnSmpp> smppSettingList;

	private static MsgSessionManager sessionManager;
	private static boolean unlimited;
	private static int availablePubliicMessages;
	
	public static boolean exit = false;
	
	public static String SERVER_TYPE = "SMPP";
	public static String SERVER_TYPE_ID = "4";	

	static IDataAgent dataAgent = DataAgentFactory.getDataInterface();

	public MdnSmppGatewayServer(){
	}
	
	public MdnSmppGatewayServer(List<MdnSmpp> smppSettingList){
		//this.smsGatewaySetting = smsGatewaySetting;
		this.smppSettingList = smppSettingList;
		init();
	}
	
	public static MdnSmppGatewayServer getInstance(List<MdnSmpp> smppSettingList) {
		if (instance == null)
			instance = new MdnSmppGatewayServer(smppSettingList);
			
		return instance;
	}

	private void init(){
		MsgSessionManager session = new MsgSessionManager();
		
		boolean limitation = dataAgent.getPublicGroupFlag();//Unlimited = true, limited = false
		setLimitation(limitation);
		
		int availablePubliicMessages = dataAgent.getAvailablePublicMessages();
		setAvailablePubliicMessages(availablePubliicMessages);
		
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
	
	public void setSessionManager(MsgSessionManager sessionManager){
		this.sessionManager = sessionManager;
	}

	public void setLimitation(boolean unlimited){
		this.unlimited = unlimited;
	}
	
	public void setAvailablePubliicMessages(int availablePubliicMessages){
		this.availablePubliicMessages = availablePubliicMessages;
	}				
	
	public void connect(MdnSmpp smsGatewaySetting) {
		int id = smsGatewaySetting.getId();
		String number = smsGatewaySetting.getNumber();
		String host = smsGatewaySetting.getHost();
		String port = smsGatewaySetting.getPort();
		String username = smsGatewaySetting.getUsername();
		String password = smsGatewaySetting.getPassword();
		String sourceNpi = smsGatewaySetting.getSourceNPI();
		String sourceTon = smsGatewaySetting.getSourceTON();
		String destNpi = smsGatewaySetting.getDestNPI();
		String destTon = smsGatewaySetting.getDestTON();		
		String bindNpi = smsGatewaySetting.getBindNPI();
		String bindTon = smsGatewaySetting.getBindTON();				
		String type = smsGatewaySetting.getType();
		int useTlv = smsGatewaySetting.getUseTlv();
		int checkTime = smsGatewaySetting.getInterval();
		int useAddrRange = smsGatewaySetting.getUseAddrRange();
		
		Connection gatewayConnection = null;
		MdnConnectionObserver mdnObserver = new MdnConnectionObserver();
		try {
			gatewayConnection = new Connection(host, Integer.parseInt(port), true);//asynchronous
			//gatewayConnection.addObserver(this);
			
			mdnObserver.setLimitation(unlimited);
			mdnObserver.setSessionManager(sessionManager);
			mdnObserver.setAvailablePubliicMessages(availablePubliicMessages);
			//setMdnObserver(mdnObserver);			
			//gatewayConnection.addObserver(mdnObserver);
		} catch (UnknownHostException uhe) {
			System.out.println("host or port is incorrect!");
			//System.exit(0);
		}
		
		MdnSmppServerObject smppServerObject = new MdnSmppServerObject(id, number, host, port, username, password, sourceNpi, sourceTon, 
				destNpi, destTon, bindNpi, bindTon, type, useTlv, checkTime, gatewayConnection, useAddrRange);
		smppServerObject.setStartTime(new Date().getTime());
		smppServerObjectList.add(smppServerObject);
		
		mdnObserver.setSmppServerObject(smppServerObject);	
		gatewayConnection.addObserver(mdnObserver);
		
		boolean retry = false;
		while (!retry) {
			try {
				String addressRange;
				if(useAddrRange == 0)
					addressRange = null;
				else
					addressRange = number.replace("+", "").trim();
				
				if(MessageManager.isBlank(bindNpi))
					bindNpi = "1";//default value
				if(MessageManager.isBlank(bindTon))
					bindTon = "1";//default value
				
				gatewayConnection.bind(Connection.TRANSCEIVER, username, password, "SMPP", Integer.parseInt(bindNpi), Integer.parseInt(bindTon), addressRange);

/*				Latin1Encoding iso88591 = new Latin1Encoding();
				gatewayConnection.setDefaultAlphabet(iso88591);
				
				UTF16Encoding encoding = new UTF16Encoding(true);
            	byte[] encoded = encoding.encode(string);
            	String decoded = encoding.decode(encoded);
*/
		        gatewayConnection.autoAckLink(true);
		        gatewayConnection.autoAckMessages(true);
		        
				retry = true;
			} catch (IOException ioe) {
				System.out.println("bind error for this connection : " + smppServerObject.getNumber());
				ioe.printStackTrace();
			}
		}
	}	

	public void run() {
		while (!exit) {
			for (MdnSmpp smsGatewaySetting : smppSettingList) {
				connect(smsGatewaySetting);
			}
			
			//long startSrvs = new Date().getTime();
			
			int freq = Integer.parseInt("100");
			for (;;) {
				try {
					Thread.sleep(freq);//thread awake any 100 milisec to check received sms 
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				for(int i= 0; i< smppServerObjectList.size(); i++){
					MdnSmppServerObject smppServerObject = smppServerObjectList.get(i);
					
					long rightNow = new Date().getTime();
					
					long startSrvs = smppServerObject.getStartTime(); 
					long diff = rightNow - startSrvs;

					long checkTime = (smppServerObject.getInterval())*1000;
					
					if(checkTime != 0 && diff > checkTime)
					{
						startSrvs = new Date().getTime();
						smppServerObject.setStartTime(startSrvs);
						try {
							if(smppServerObject.getSmppConnection().isBound()){
								//System.out.println("send enquirelink for this connection " + smppServerObject.getHost());
								EnquireLink activeConnRequest = (EnquireLink)smppServerObject.getSmppConnection().newInstance(SMPPPacket.ENQUIRE_LINK);
								smppServerObject.getSmppConnection().sendRequest(activeConnRequest);
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
/*			connect();
			synchronized(this) {
				try {
					wait();
				} catch (InterruptedException ie) {
					System.out.println("thread problem!!!");
				}
			}*/
		
	}
	
	public static void main(String[] args) {
		try {
			smppSettingList = dataAgent.getAllSmppGateway();
		} catch (MdnException e1) {
			e1.printStackTrace();
		}
		
		if(smppSettingList.isEmpty()){
			System.out.println("No MDN SMPP connection has been configured yet!");
		}else{
			MdnSmppGatewayServer gatewayServer = MdnSmppGatewayServer.getInstance(smppSettingList);
			gatewayServer.run();			
		}
	}	
}
