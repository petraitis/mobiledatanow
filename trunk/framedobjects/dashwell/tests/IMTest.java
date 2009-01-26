package com.framedobjects.dashwell.tests;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletOutputStream;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import wsl.fw.datasource.DataManager;
import wsl.fw.datasource.DataObject;
import wsl.fw.exception.MdnException;
import wsl.fw.resource.ResId;
import wsl.fw.resource.ResourceManager;
import wsl.fw.util.CKfw;
import wsl.fw.util.Config;
import wsl.fw.util.Log;
import wsl.mdn.common.MdnAdminConst;
import wsl.mdn.common.MdnDataManager;
import wsl.mdn.common.MdnResourceManager;
import wsl.mdn.dataview.MdnDataCache;
import wsl.mdn.mdnim.IMConnection;
import wsl.mdn.mdnim.IMContact;
import wsl.mdn.mdnim.IMMessage;
import wsl.mdn.server.MdnServer;

import com.framedobjects.dashwell.db.DataAgentFactory;
import com.framedobjects.dashwell.db.IDataAgent;
import com.framedobjects.dashwell.handlers.InstantMessengerHandler;
import com.framedobjects.dashwell.mdninstantmsg.bean.ContactImplFactory;
import com.framedobjects.dashwell.mdninstantmsg.bean.GroupImpl;
import com.framedobjects.dashwell.mdninstantmsg.bean.GroupImplFactory;
import com.framedobjects.dashwell.mdninstantmsg.connection.AbstractIMConnection;
import com.framedobjects.dashwell.mdninstantmsg.eventhandler.AbstractIMEventHandler;
import com.framedobjects.dashwell.utils.Constants;
import com.framedobjects.dashwell.utils.XmlFormatter;
import com.itbs.aimcer.bean.ContactWrapper;
import com.itbs.aimcer.bean.GroupList;
import com.itbs.aimcer.bean.GroupWrapper;
import com.itbs.aimcer.bean.Nameable;
import com.itbs.aimcer.commune.MessageSupport;
import com.itbs.aimcer.commune.SMS.InvalidDataException;
import com.itbs.aimcer.commune.SMS.SMSMessage;
import com.itbs.aimcer.commune.SMS.TMobile;
import com.itbs.aimcer.commune.jaim.JAIMConnection;
import com.itbs.aimcer.commune.joscar.ICQConnection;
import com.itbs.aimcer.commune.joscar.OscarConnection;
import com.itbs.aimcer.commune.msn.MSNConnection;
import com.itbs.aimcer.commune.smack.SmackConnection;
import com.itbs.aimcer.commune.ymsg.YMsgConnection;
//import com.itbs.aimcer.commune.ymsg.YMsgConnection;
import com.itbs.aimcer.gui.ContactListModel;

public class IMTest {
	public static final ResId
	ERR_LICENSE1	= new ResId ("MdnAdminApp.err.license1"),
	ERR_LICENSE2	= new ResId ("MdnAdminApp.err.license2"),
	TEXT_STARTING	= new ResId ("MdnAdminApp.text.starting"),
	TEXT_VERSION	= new ResId ("mdn.versionText"),
	ERR_VERSION1	= new ResId ("MdnAdminApp.err.version1"),
	ERR_VERSION2	= new ResId ("MdnAdminApp.err.version2"),
	ERR_WILL_EXIT	= new ResId ("MdnAdminApp.err.willExit"),
	ERR_NO_SERVER	= new ResId ("MdnAdminApp.err.noServer");	
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		init();
		String userName = "mdnserver";
		String password = "moradvandi";
		MessageSupport conn = new OscarConnection();

		conn.assignGroupFactory(new GroupImplFactory());
        conn.assignContactFactory(new ContactImplFactory());
        conn.setUserName(userName);
        conn.setPassword(password);
        AbstractIMEventHandler eventHandler = new AbstractIMEventHandler(); 
        conn.addEventListener(eventHandler);
        try {
			conn.connect();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
//		ContactListModel dataModel = ContactListModel.getInstance();
//		GroupImpl group = (GroupImpl) conn.getGroupFactory().create("Group");
//		conn.getGroupList().add(group);
		//ContactWrapper hooman = ContactWrapper.create("YID", conn);
		//group.add(hooman);
		
//		GroupList groupList = conn.getGroupList();
//		System.out.println("............" + groupList);
//		ContactWrapper contact = ContactWrapper.create("YID", null);
//		System.out.println("1111111111111111");
//		Nameable contactNew = new Nameable() {
//			public String getName() {
//				return "asal_m57";
//			}
//		};
//		GroupImpl group = (GroupImpl) conn.getGroupFactory().create("friends");
//
//		conn.addContact(contactNew, group);
		
		
/*		String serviceName = "Aim mdn im test";
		String userName = "476920522";
		String password = "moradvandi";
		String type = "2";
		String status = "0";
		 try {
		AbstractIMEventHandler eventHandler =new AbstractIMEventHandler();
		MessageSupport messageSupport = new ICQConnection();
		messageSupport.assignGroupFactory(new GroupImplFactory());
		messageSupport.assignContactFactory(new ContactImplFactory());
		messageSupport.setUserName(userName);
		messageSupport.setPassword(password);
		messageSupport.addEventListener(eventHandler);
		
        
		
        //init Data manager
		ResourceManager.set (new MdnResourceManager ());
		Config.setSingleton (MdnAdminConst.MDN_CONFIG_FILE, true);
		Config.getSingleton ().addContext (CKfw.RMICLIENT_CONTEXT);
		DataManager.setDataManager (new MdnDataManager ());
		MdnDataCache.setCache(new MdnDataCache (false));		
		//init Data manager
		
		//InstantMessengerHandler handler = new InstantMessengerHandler();
		
//		IMConnection newIMConnection = new IMConnection();
//	    newIMConnection.setName(serviceName);
//	    newIMConnection.setUserName(userName);
//	    newIMConnection.setPassword(password);
//	    newIMConnection.setType(Integer.parseInt(type));
//	    newIMConnection.setStatus(Integer.parseInt(status));
	    
//		AbstractIMConnection abstractConnection = new AbstractIMConnection();
//	    MessageSupport imConn =  abstractConnection.getConnection(newIMConnection);
//	    IMMessage imMessage;
	   
	    	messageSupport.connect();
//			Iterator msgIt = imConn.getEventListenerIterator();
//			if(msgIt.hasNext()){
//				AbstractIMEventHandler eventHandler = (AbstractIMEventHandler)msgIt.next();
//				imMessage = eventHandler.getRecievedMessage();
//			}
//			imConn.disconnect(true);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	    */
	    String result;
	    Element root = null;
		try {
			IMContact imContact = new IMContact();
			String contactid="0";
			
			IDataAgent dataAgent = DataAgentFactory.getDataInterface();
//			if(contactid.equals("0"))//add new
//			{
				imContact.setState(0);
//			}else{//edit
//				imContact.setId(Integer.parseInt(contactid));
//				imContact.setState(DataObject.IN_DB);
//				imContact = dataAgent.getIMContactByID(Integer.parseInt(contactid));
//			}
			imContact.setUserId(Integer.parseInt("1010"));			
			imContact.setImConnectionType(Integer.parseInt("5"));
			imContact.setName("kjkjkjkj");
			imContact.setState(0);
			
			dataAgent.saveIMContact(imContact);
			//imContact.setId(imContactId);

			//root = new XmlFormatter().saveIMConactForUserResult(imContact);
			
			//result = handler.processCreateMDNIMConnection(newIMConnection); // insert a new
			//result = handler.deleteIMConnection(19); // delete an IM
		    //result = new XMLOutputter().outputString(new Document(handler.handleGetConnections()));  //select all        
		} catch (MdnException e) {
			e.printStackTrace();
			result = "Database error";
		}
	     //Format the data.
//	    new XmlFormatter().newIMConnectionResult("en", result);
//	    System.out.println("-------------------------" + new XmlFormatter().newIMConnectionResult("en", result));
	}
	static private void init(){
		/*
		 *	Set the ResourceManager
		 * (must be first as everything uses resource strings)
		 */
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

}
