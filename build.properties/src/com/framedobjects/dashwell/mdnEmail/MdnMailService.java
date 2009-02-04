package com.framedobjects.dashwell.mdnEmail;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import wsl.fw.datasource.DataManager;
import wsl.fw.datasource.DataSourceException;
import wsl.fw.exception.MdnException;
import wsl.fw.resource.ResId;
import wsl.fw.resource.ResourceManager;
import wsl.fw.security.User;
import wsl.fw.util.CKfw;
import wsl.fw.util.Config;
import wsl.fw.util.Log;
import wsl.mdn.common.MdnAdminConst;
import wsl.mdn.common.MdnDataManager;
import wsl.mdn.common.MdnResourceManager;
import wsl.mdn.dataview.LanguageDobj;
import wsl.mdn.dataview.MdnDataCache;
import wsl.mdn.dataview.QueryDobj;
import wsl.mdn.mdnmsgsvr.MessagingSettingDetails;
import wsl.mdn.mdnmsgsvr.MdnEmailSetting;
import wsl.mdn.mdnmsgsvr.MessageLog;
import wsl.mdn.mdnmsgsvr.TemporaryBlockContacInfo;
import wsl.mdn.server.MdnServer;

import com.framedobjects.dashwell.biz.GuestMessagesInfo;
import com.framedobjects.dashwell.biz.MessageObject;
import com.framedobjects.dashwell.biz.MsgSessionManager;
import com.framedobjects.dashwell.db.DataAgentFactory;
import com.framedobjects.dashwell.db.IDataAgent;
import com.framedobjects.dashwell.utils.Constants;
import com.framedobjects.dashwell.utils.MessageManager;
import com.framedobjects.dashwell.utils.MessagingUtils;


public class MdnMailService {
/*	public static final ResId
	ERR_LICENSE1	= new ResId ("MdnAdminApp.err.license1"),
	ERR_LICENSE2	= new ResId ("MdnAdminApp.err.license2"),
	TEXT_STARTING	= new ResId ("MdnAdminApp.text.starting"),
	TEXT_VERSION	= new ResId ("mdn.versionText"),
	ERR_VERSION1	= new ResId ("MdnAdminApp.err.version1"),
	ERR_VERSION2	= new ResId ("MdnAdminApp.err.version2"),
	ERR_WILL_EXIT	= new ResId ("MdnAdminApp.err.willExit"),
	ERR_NO_SERVER	= new ResId ("MdnAdminApp.err.noServer"); */
	
	public static final String SERVER_TYPE = "Email";
	public static final String SERVER_TYPE_ID = "1";
	
	public boolean disconnect = false;
	
	public static void sendNewPasswordToEmail(String emailAddress, String content){
		List folders = new ArrayList<Folder>();
		//init();
		Properties props = System.getProperties();
		//MdnMailServer s = new MdnMailServer();
		try {
			//Get a Store object
			String smtpHost;
			String imapHost;
			String imapUserName;
			String imapPassword;
			
		    IDataAgent dataAgent = DataAgentFactory.getDataInterface();

		    List<MdnEmailSetting> mdnEmails = dataAgent.getMdnEmailAddresses();
	    	MdnEmailSetting mdnEmail = mdnEmails.get(0);

			if (mdnEmail != null){
	    		Session session = Session.getInstance(props, null);//Get a Session object
				Store store = session.getStore(Constants.IMAP);		    		
	    		smtpHost = mdnEmail.getSmtpHost();
	    		imapHost = mdnEmail.getImapHost();
	    		imapUserName = mdnEmail.getImapUserName();
	    		imapPassword = mdnEmail.getImapPassword();
	    		
				store.connect(imapHost, imapUserName, imapPassword);
				//Connect
	    		Folder folder = store.getFolder(Constants.INBOX);//Open a Folder
	    		if (folder == null || !folder.exists()) {
	    			System.out.println("Invalid folder");
	    			System.exit(1);
	    		}

	    		folder.open(Folder.READ_WRITE);
	    		System.out.println("Successful Connection : " + imapUserName);

	    		// Add messageCountListener to listen for new messages
	    		MDNEmailListener mdnEmailAdaptor = new MDNEmailListener();

	    		MsgSessionManager sessionManager = new MsgSessionManager();
		    	mdnEmailAdaptor.setSessionManager(sessionManager);
	    		
	    		mdnEmailAdaptor.setEmailSetting(mdnEmail);
//	    		folder.addMessageCountListener(mdnEmailAdaptor);
//	    		s.setFolder(folder);
//	    		folders.add(folder);
	    	}
	    	//s.setFolders(folders);
		} catch (MdnException e) {
			//e.printStackTrace();
		} catch (NoSuchProviderException e1) {
			//e1.printStackTrace();
		} catch (MessagingException e) {
			//e.printStackTrace();
		}
	}
	
	public void initEmailSetting(){
		List folders = new ArrayList<Folder>();
		//init();
		
		boolean limitation = false;
		int availablePubliicMessages;
	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		MessagingSettingDetails guestObj = null;
		try {
			guestObj = dataAgent.getGuestMsgInfo(SERVER_TYPE);
			
			limitation = dataAgent.getPublicGroupFlag();//Unlimited = true, limited = false
			availablePubliicMessages = dataAgent.getAvailablePublicMessages();

			//Get a Store object
			//String smtpHost;
			String imapHost;
			String imapUserName;
			String imapPassword;
			
			String imapProtocol;
			String imapPort;
			
			boolean hasNotDeletedEmailSetting = false;
		    List<MdnEmailSetting> mdnEmails = dataAgent.getMdnEmailAddresses();
		    if(mdnEmails.isEmpty()){
    			System.out.println("No MDN Email Setting has been configured yet!");
    			System.exit(1);		    	
		    }else{
		    	for (MdnEmailSetting mdnEmail : mdnEmails) {
		    		disconnect = false;
		    		//Properties props = System.getProperties();
		    		Properties props = new Properties();
		    		if(mdnEmail != null && mdnEmail.getDelStatus() != 1){
		    			hasNotDeletedEmailSetting = true;
			    		imapHost = mdnEmail.getImapHost();
			    		imapUserName = mdnEmail.getImapUserName();
			    		imapPassword = mdnEmail.getImapPassword();
			    		imapProtocol = Constants.IMAP;
			    		imapPort = mdnEmail.getImapPort();
			    		
			    		//smtpHost = mdnEmail.getSmtpHost();
			    		
			    		Session session;
			    		
			    		if(mdnEmail.getImapEncryptedType() == Constants.SSL || mdnEmail.getImapEncryptedType() == Constants.TLS){
			    			MDNAuthenticator auth = null;
	
			    			props.put("mail.imap.host", imapHost);
		    		        props.put("mail.imap.port", imapPort);
		    		        
		    		        props.put("mail.imap.starttls.enable","true");
		    		        props.put("mail.imap.auth", "true"); 
		    		        props.put("mail.imap.socketFactory.port", imapPort);
		    		        props.put("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		    		        props.put("mail.imap.socketFactory.fallback", "false");
			    			
			    			auth = new MDNAuthenticator(imapUserName, imapPassword);
			    			//session = Session.getDefaultInstance(props, auth);
			    			session = Session.getInstance(props, auth);
			    		}else{
			    			session = Session.getInstance(props, null);//Get a Session object
			    		}
			    		Store store = null;
						try {
							store = session.getStore(imapProtocol);
						} catch (NoSuchProviderException e) {
							System.out.println("IMAP protocol problem");
							disconnect = true;
							continue;
						}
						try {
							store.connect(imapHost, imapUserName, imapPassword);
						} catch (MessagingException e) {
							System.out.println("IMAP Connection problem");
							disconnect = true;
							continue;
						}
						
						//Connect
			    		Folder folder;
						try {
							folder = store.getFolder(Constants.INBOX);
							//Open a Folder
				    		if (folder == null || !folder.exists()) {
				    			System.out.println("Invalid folder");
				    			System.exit(1);
				    		}
				    		folder.open(Folder.READ_WRITE);
				    		System.out.println("Successful Connection : " + imapUserName);
				    		
				    		// Add messageCountListener to listen for new messages
				    		MDNEmailListener mdnEmailAdaptor = new MDNEmailListener();
				    		
				    		MsgSessionManager sessionManager = new MsgSessionManager();
				    		sessionManager.setAttribute(MessageManager.GUEST_OBJ, guestObj);
				    		
				    		LanguageDobj defaultLanguage = dataAgent.getDefaultLanguage();
				    		String file = defaultLanguage.getFileName();
				    		sessionManager.setAttribute(Constants.SESSION_LANGUAGE_FILE, file);				    		
				    		
					    	mdnEmailAdaptor.setSessionManager(sessionManager);
					    	mdnEmailAdaptor.setLimitation(limitation);
					    	mdnEmailAdaptor.setAvailablePubliicMessages(availablePubliicMessages);
				    		
					    	mdnEmailAdaptor.setEmailSetting(mdnEmail);
				    		
				    		folder.addMessageCountListener(mdnEmailAdaptor);
				    		folders.add(folder);
						} catch (MessagingException e) {
							disconnect = true;
							//e.printStackTrace();
						}		    		
		    		}
		    	}
		    	if(!hasNotDeletedEmailSetting){
	    			System.out.println("No MDN Email Setting has been configured yet!!");
	    			System.exit(1);		    		
		    	}
		    }
	    	//s.setFolders(folders);
		} catch (MdnException e2) {
			System.out.println("Mail Server is down");
        	long waitToRestartTime = (60*1000); 
        	try {
				Thread.sleep(waitToRestartTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
			disconnect = true;
			//e2.printStackTrace();
		}	    	

		// Check mail once in "freq" MILLIseconds
		int freq = Integer.parseInt("100");
		for (;;) {
			try {
				Thread.sleep(freq);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// sleep for freq milliseconds
			if(disconnect){
				System.out.println("Try to Restart MDNMailServer...");
		    	MdnMailService emailServer = new MdnMailService();
		    	emailServer.initEmailSetting();
			}
			
			for (int i=0 ; i<folders.size() ; i++) {	
				Folder currFolder = (Folder)folders.get(i);
				try {
					currFolder.getMessageCount();// This is to force the IMAP server to send us EXISTS notifications.
				} catch (MessagingException e) {
					disconnect = true;
				} 
	
				if(!disconnect){
					MsgSessionManager session = MessageManager.getSession();
					if(session != null){
						session.setAttribute(MessageManager.GUEST_OBJ, guestObj);
						
						try{
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
				        } catch (Exception ex) {
				        	disconnect = true;
				        	//ex.printStackTrace();
				        }
					}
				}
    		}
		}
	}
    		
    public static void main(String args[])
	{
    	MdnMailService emailServer = new MdnMailService();
    	emailServer.initEmailSetting();
    }
/*
    static private void init(){
		ResourceManager.set (new MdnResourceManager ());

		// log start and version
		Log.log (
			TEXT_STARTING.getText ()
			+ " " + TEXT_VERSION.getText ()
			+ " " + MdnServer.VERSION_NUMBER);

		// set the config (must be second as nearly everything uses configs)
		Config.setSingleton (MdnAdminConst.MDN_CONFIG_FILE, true);
		Config.getSingleton ().addContext (CKfw.RMICLIENT_CONTEXT);

		// set the DataManager
		DataManager.setDataManager (new MdnDataManager ());

		// set the data cache
		MdnDataCache.setCache (new MdnDataCache (false));		
	}
*/	
	public static class MDNEmailListener extends MessageCountAdapter {
	    private MsgSessionManager sessionManager;
	    private MdnEmailSetting emailSetting;
		private boolean unlimited;
		private int availablePubliicMessages;
		private boolean tempBlock = false;
		
	    public MDNEmailListener() {
			super();
		}

		public void setSessionManager(MsgSessionManager sessionManager){
			this.sessionManager = sessionManager;
		}
		public MsgSessionManager getSessionManager(){
			return sessionManager;
		}
	    
		public void setEmailSetting(MdnEmailSetting emailSetting){
			this.emailSetting = emailSetting;
		}

		public MdnEmailSetting getEmailSetting(){
			return emailSetting;
		}
		
		public boolean getLimitation(){
			return unlimited;
		}
		
		public void setLimitation(boolean unlimited){
			this.unlimited = unlimited;
		}
		
		public int getAvailablePubliicMessages(){
			return availablePubliicMessages;
		}
		
		public void setAvailablePubliicMessages(int availablePubliicMessages){
			this.availablePubliicMessages = availablePubliicMessages;
		}		
		
		public void messagesAdded(MessageCountEvent ev) {
			Message[] msgs = ev.getMessages();
			String userKey;

			// Just dump out the new messages
			for (int i = 0; i < msgs.length; i++) {
				try {            	 
					Message msg = (Message)msgs[i];
					Address[] addresses = msg.getFrom();
					
					String receivedMsg = msg.getSubject();
					
					if(receivedMsg == null)
						receivedMsg = "";
					
					System.out.println("Recived email from " + addresses[0] + " , Subject : " + receivedMsg);
					
					String replyContext = "";
					MessageObject msgInfo = null;
					MessageManager messageManager = null;

					String senderEmailAddress = parsEmailAddress(addresses[0].toString());//Sender Address
					IDataAgent dataAgent = DataAgentFactory.getDataInterface();
					try {
						String separator = MessageManager.getMdnSeparator();

						MdnEmailSetting email = dataAgent.getEmailByAddress(getEmailSetting().getEmailAddress());
						int emailId = email.getId();						
						
						User mdnUser = dataAgent.getUserByEmailAddress(senderEmailAddress);//find email from Email setting TBL
						if(mdnUser != null){
							System.out.println("MDN user");
						
							//Save Email Message Log
							MessageLog msgLog = new MessageLog();
							msgLog.setText(receivedMsg);
							msgLog.setDate(new Date());
							msgLog.setUserId(mdnUser.getId());
							msgLog.setMessageType(Constants.EMAIL_MSG_TYPE);
							msgLog.setState(0);
							try {
								msgLog.save();
							} catch (DataSourceException e) {
								//e.printStackTrace();
							}

							userKey = String.valueOf(mdnUser.getId());
					        messageManager = new MessageManager(userKey, getSessionManager());
					        messageManager.initMessageSession();
					        
					        String file = (String)messageManager.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
					        
					        boolean isSearchTerm = false;
					        if(receivedMsg.equalsIgnoreCase("cancel")){
					        	System.out.println("CANCEL");
					        	messageManager.cancelMessage();
					        }else{
				    			MessagingSettingDetails msgSettInfo = dataAgent.getGuestMsgInfo("Email");
								String searchTerm = msgSettInfo.getSearchTerm();

								Map userInputsMap = MessagingUtils.userInputsParser(separator, receivedMsg);
								String emailKeyword = (String)userInputsMap.get("keyword");									
					        	if(searchTerm.equalsIgnoreCase(emailKeyword)){//(imKeyword.equalsIgnoreCase("help")){
					        		isSearchTerm = true;
					        		String result;
					        		ArrayList<String> userInputsList = (ArrayList<String>)userInputsMap.get("userInputs");
					        		
					        		//String file = (String)messageManager.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);

					        		if(userInputsList == null || userInputsList.isEmpty()){
					    				result = MessagingUtils.getAllQueriesList(SERVER_TYPE, file);
					    			} else {
					    				String userInput = userInputsList.get(0);
					    				result = MessagingUtils.searchQueryKeyword(SERVER_TYPE, file, userInput);
					    			}
					        		replyContext = result;					        	
						        }else
						        	msgInfo = messageManager.getEmailMessageObject(emailId, receivedMsg, separator);//(emailId, emailKeyword);
					        }
							
							if(msgInfo != null){
								replyContext = messageManager.getAdvanceReplyingMessage(mdnUser.getId(), msgInfo, file);
							}else if(!isSearchTerm)
								System.out.println("Invalid Keyword!");
					        sessionManager = messageManager.getSession();
					        setSessionManager(sessionManager);

						} else {
							System.out.println("Unknown user");
							userKey = senderEmailAddress;//Contact
							
							Map permanentBlockList = dataAgent.getBlockContactsByType(SERVER_TYPE_ID);
							
							if(!permanentBlockList.containsKey(userKey)){
								TemporaryBlockContacInfo msgControlsInfo = dataAgent.getTempBlockContacts();								
								
								messageManager = new MessageManager(userKey, getSessionManager());		        
						        messageManager.initMessageSession();
						        
						        String file = (String)messageManager.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
						        
						        MessagingSettingDetails guestObj = (MessagingSettingDetails)getSessionManager().getAttribute(MessageManager.GUEST_OBJ);
						        
								//public Messages
					        	messageManager.manageTemproryBlockContacts(guestObj, msgControlsInfo);
					    		Map guestMsgMap = (Map)getSessionManager().getAttribute(MessageManager.GUEST_MAP_KEY);
					    		if(guestMsgMap != null){
					    			GuestMessagesInfo guestMsgInfo = (GuestMessagesInfo)guestMsgMap.get(userKey);
					    			if(guestMsgInfo != null && guestMsgInfo.isBlock())
					    				tempBlock=true;
					    			else
					    				tempBlock = false;
					    		}			        				        
						        
					    		System.out.println("Email available public message :::::>>" + getAvailablePubliicMessages());
					    		if(!tempBlock)
					    			messageManager.editGuestInfoInSession(SERVER_TYPE, getLimitation(), getAvailablePubliicMessages());
					    		
						        int status = guestObj.getStatus();	
						        if(getLimitation())
						        	status=0;
						        
						        if(tempBlock){
						        	replyContext = msgControlsInfo.getReply();
						        } else if(status == 0){//If it hasn't reached the limit of available public messages yet
						        	Date now = new Date();
					    			System.out.println("Public Msg <Date>:" + DateFormat.getDateTimeInstance().format(now) + "  <Type>:" + 
					    					SERVER_TYPE + "  <From>:"  + userKey + "  <Query>:" + receivedMsg);
					    			
					    			//Search term
					    			MessagingSettingDetails msgSettInfo = dataAgent.getGuestMsgInfo("Email");
									String searchTerm = msgSettInfo.getSearchTerm();

									Map userInputsMap = MessagingUtils.userInputsParser(separator, receivedMsg);
									String emailKeyword = (String)userInputsMap.get("keyword");									
						        	if(searchTerm.equalsIgnoreCase(emailKeyword)){//(imKeyword.equalsIgnoreCase("help")){
						        		String result;
						        		ArrayList<String> userInputsList = (ArrayList<String>)userInputsMap.get("userInputs");
						        		
						        		//String file = (String)messageManager.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);

						        		if(userInputsList == null || userInputsList.isEmpty()){
						    				result = MessagingUtils.getAllQueriesList(SERVER_TYPE, file);
						    			} else {
						    				String userInput = userInputsList.get(0);
						    				result = MessagingUtils.searchQueryKeyword(SERVER_TYPE, file, userInput);
						    			}
						        		replyContext = result;
						        	} else{
						    			msgInfo = messageManager.getEmailMessageObject(emailId, receivedMsg, separator);//(emailId, emailKeyword);					        	
										if(msgInfo != null) {//If there is this keyword for any query|user-reply message in DB
											replyContext = messageManager.getReplyingMessageForGuestUser(null, msgInfo, file);
		
									        sessionManager = messageManager.getSession();
									        setSessionManager(sessionManager);									
										}else
											System.out.println("Invalid Keyword");
							        }
						        }else
						        	System.out.println("This license is limited for "+ getAvailablePubliicMessages() + " public messages and it has reached the limit of 200 messages sent through MDN to public users");
							}else
								System.out.println("This contact is block! >> " + senderEmailAddress);
						}
					} catch (MdnException e) {
						System.out.println("error 1 !!!!!");
						e.printStackTrace();
					}  
					if(!replyContext.equals("")){
						
						Properties props = new Properties();
						props.put(Constants.SMTP_HOST_PROP, getEmailSetting().getSmtpHost());
						props.put(Constants.SMTP_PORT_PROP, getEmailSetting().getSmtpPort());
						Session session;
						
						if(getEmailSetting().getSmtpEncryptedType() == Constants.SSL){
							props = setSmtpSSLProperties(props);
							Authenticator auth = new MDNAuthenticator(getEmailSetting().getSmtpUsername(),getEmailSetting().getSmtpPassword());
							session = Session.getInstance(props, auth);							
						}else if(getEmailSetting().getSmtpEncryptedType() == Constants.TLS){
							props = setSmtpTLSProperties(props);
							session = Session.getInstance(props, null);
						}else if(getEmailSetting().getSmtpEncryptedType() == Constants.AUTO){
							props = setSmtpAutoProperties(props);
							Authenticator auth = new MDNAuthenticator(getEmailSetting().getSmtpUsername(),getEmailSetting().getSmtpPassword());
							session = Session.getInstance(props, auth);														
						}else{//None
							session = Session.getInstance(props, null);
						}

						Message sendingMsg = new MimeMessage(session);
						sendingMsg.setFrom(new InternetAddress(getEmailSetting().getEmailAddress()));//Send Response Email By Mdn Email Address That email sent to the address.
						if(msg.getSubject() == null || msg.getSubject().equals(""))
							sendingMsg.setSubject("MDN Report");
						else
							sendingMsg.setSubject(msg.getSubject());

						sendingMsg.setSentDate(new Date());
						sendingMsg.setText(replyContext);

						if(getEmailSetting().getSmtpHost().equals("smtp.mail.yahoo.com")){
							sendingMsg.setFrom(new InternetAddress(getEmailSetting().getSmtpUsername()+"@yahoo.com"));
							sendingMsg.addRecipient(Message.RecipientType.TO, new InternetAddress(senderEmailAddress));
						}
						
						session = Session.getInstance(props, null);
						Transport transport = session.getTransport(Constants.SMTP);
						transport.connect(getEmailSetting().getSmtpHost(), getEmailSetting().getSmtpUsername(), getEmailSetting().getSmtpPassword());
						transport.sendMessage(sendingMsg, msg.getFrom());
						transport.close();
						System.out.println(" >>>>>>>> Sent Email Successful! ");						

						messageManager.catchMessageDataInSession(msgInfo);
					}else{
						System.out.println(" MDNEmailServer can't send the result back for this message: " + receivedMsg +" to this contact: "+ senderEmailAddress);
					}
		  			msg.setFlag(Flags.Flag.DELETED, true);
				} catch (MessagingException mex) {
					System.out.println("Faild email transport by definition smtp : " + mex.getMessage());
					mex.printStackTrace();
				}
			}
		}		

		private String parsEmailAddress(String fromAddress){
			String emailAddress = "";
		    if(fromAddress != null)
		    {
		    	// fromAddress : firstName lastName <email@abc.com>
		    	if(fromAddress.contains("<") && fromAddress.contains(">")){
			    	int startIndex = fromAddress.indexOf("<") + 1;
			    	int endIndex = fromAddress.indexOf(">");
			    	emailAddress = fromAddress.substring(startIndex, endIndex);
		    	}else
		    		emailAddress = fromAddress;
		    }
			return emailAddress;
		}
		
		private Properties setSmtpCommonProperties(Properties props){
			props.put(Constants.SMTP_HOST_PROP, getEmailSetting().getSmtpHost());
			props.put(Constants.SMTP_PORT_PROP, getEmailSetting().getSmtpPort());
			return props;
			
		}
		
		public Properties setSmtpSSLProperties(Properties props){
			props.put("mail.smtp.user", getEmailSetting().getSmtpUsername());
			//props.put("mail.smtp.port", getEmailSetting().getSmtpPort());
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.auth", "true");
			//props.put("mail.smtp.debug", "true");
			props.put("mail.smtp.socketFactory.port", getEmailSetting().getSmtpPort());
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.put("mail.transport.protocol", "smtps");
			props.put("mail.smtp.socketFactory.fallback", "false");
			props.put("mail.smtp.quitwait", "false");

			return  props;
		}
		
		private Properties setSmtpTLSProperties(Properties props){
			props.put("mail.smtp.auth", "true");
			props.put("mail.debug", "true"); 
			props.put("mail.smtp.starttls.enable", "true");//Start TLS			
			return  props;
		}

		private Properties setSmtpAutoProperties(Properties props){
			props.put("mail.smtp.auth", "true");
			return  props;
		}			
	}
}

