package com.framedobjects.dashwell.mdninstantmsg;

import java.util.List;

import wsl.fw.exception.MdnException;
import wsl.mdn.dataview.LanguageDobj;
import wsl.mdn.mdnim.IMConnection;
import wsl.mdn.mdnmsgsvr.MessagingSettingDetails;

import com.framedobjects.dashwell.biz.MsgSessionManager;
import com.framedobjects.dashwell.db.DataAgentFactory;
import com.framedobjects.dashwell.db.IDataAgent;
import com.framedobjects.dashwell.mdninstantmsg.connection.AbstractIMConnection;

import com.framedobjects.dashwell.utils.Constants;
import com.framedobjects.dashwell.utils.MessageManager;
import com.itbs.aimcer.commune.MessageSupport;

public class MDNIMServer {
	public static final String SERVER_TYPE = "IM";
	public static final String SERVER_TYPE_ID = "2";
	private static boolean unlimited;
	private static int availablePubliicMessages;
	//private static Logger logger = Logger.getLogger(MDNIMServer.class.getName());
	public static void main(String[] args) {
		MsgSessionManager session = new MsgSessionManager();
		boolean isServerOn = false;
		try {
			IDataAgent dataAgent = DataAgentFactory.getDataInterface();

			boolean limitation = dataAgent.getPublicGroupFlag();//Unlimited = true, limited = false
			unlimited = limitation;
			availablePubliicMessages = dataAgent.getAvailablePublicMessages();

    		LanguageDobj defaultLanguage = dataAgent.getDefaultLanguage();
    		String file = defaultLanguage.getFileName();
    		session.setAttribute(Constants.SESSION_LANGUAGE_FILE, file);
    		
			MessagingSettingDetails guestObj = dataAgent.getGuestMsgInfo(SERVER_TYPE);
			session.setAttribute(MessageManager.GUEST_OBJ, guestObj);

			List<IMConnection> imConnections = getImConnections(session);//dataAgent.getAllIMConnections();
		  	for (IMConnection imConnection : imConnections) {
		  		if(imConnection.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT){
					AbstractIMConnection abstractConnection = new AbstractIMConnection();
					MessageSupport checkConnection = abstractConnection.handleConnection(imConnection, session);
					
					if(checkConnection!= null)
					{
						checkConnection.setAutoLogin(true);

						isServerOn = true;
						System.out.println("Success IM Connection :" + imConnection.getName());
					}else{
						System.out.println("Faild IM Connection :" + imConnection.getName());
					}
		  		}
		  	}
		  	if(isServerOn) {
				MessageManager.runTimeoutThread(session, SERVER_TYPE);
		  	}
		  	
		  	System.out.println("No MDN IM Connection has been configured yet!");
		} catch (MdnException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean getLimitation(){
		return unlimited;
	}
	
	public static int getAvailablePublicMessages(){
		return availablePubliicMessages;
	}	
	
	public static List<IMConnection> getImConnections(MsgSessionManager session){
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		try {
			List<IMConnection> imConnections = dataAgent.getAllIMConnections();
			
			session.setAttribute("imConnList", imConnections);
			return imConnections;
		} catch (MdnException e) {
			e.printStackTrace();
		}		
		return null;
	}
	
}
