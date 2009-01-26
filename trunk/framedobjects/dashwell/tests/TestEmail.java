package com.framedobjects.dashwell.tests;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;

import wsl.fw.resource.ResId;
import wsl.fw.util.Util;

public class TestEmail {

	public static final ResId
	ERR_LICENSE1	= new ResId ("MdnAdminApp.err.license1"),
	ERR_LICENSE2	= new ResId ("MdnAdminApp.err.license2"),
	TEXT_STARTING	= new ResId ("MdnAdminApp.text.starting"),
	TEXT_VERSION	= new ResId ("mdn.versionText"),
	ERR_VERSION1	= new ResId ("MdnAdminApp.err.version1"),
	ERR_VERSION2	= new ResId ("MdnAdminApp.err.version2"),
	ERR_WILL_EXIT	= new ResId ("MdnAdminApp.err.willExit"),
	ERR_NO_SERVER	= new ResId ("MdnAdminApp.err.noServer");	
	
	/**  main() is used to start an instance of the ListServer
	*/
	public static void main(String args[]) throws Exception
	{
        MessageDigest md = MessageDigest.getInstance("SHA");
        String testInt = "1";
        String encryptTest;
        byte digestBytes[] = md.digest(testInt.getBytes());
        encryptTest = Util.bytesToHex(digestBytes);
       
		
/*		String pass = "123456";
		byte[] defaultBytes = pass.getBytes();
		try{
			MessageDigest algorithm = MessageDigest.getInstance("MD5");
			algorithm.reset();
			algorithm.update(defaultBytes);
			byte messageDigest[] = algorithm.digest();
	
			StringBuffer hexString = new StringBuffer();
			for (int i=0;i<messageDigest.length;i++) {
				String hex = Integer.toHexString(0xFF & messageDigest[i]);
				if(hex.length()==1)
				hexString.append('0');
		
				hexString.append(hex);
			}
			System.out.println("pass "+pass+" md5 version is "+hexString.toString());
			pass = hexString+"";
		} catch(NoSuchAlgorithmException nsae){
		}*/

//		init();
		
//		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
//		
//		QueryDobj msgInfo = dataAgent.getUniqueQueryByEmailInfo(1, "");
////		QueryDobj msgInfo = dataAgent.getQueryByID2(138);
//		System.out.println(">>>>>>>>>> name = " + msgInfo.getName());
//		System.out.println(">>>>>>>>>> id = " + msgInfo.getId());
		
		
		
/*		EmailSender sender = new EmailSender();
		
		EmailMessage msg = new EmailMessage();
		msg.setFrom("MDNTEST");
		msg.setDestAddress("test@test.com");
		msg.setSubject("test4");
		msg.setContent("content test..............");
		
		sender.sendMsg(msg, "smtp.clear.net.nz");
*/	
		
	}

	/*static private void init(){
		
		 *	Set the ResourceManager
		 * (must be first as everything uses resource strings)
		 
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
}


