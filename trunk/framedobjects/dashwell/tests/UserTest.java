package com.framedobjects.dashwell.tests;


public class UserTest {
//	public static final ResId
//	ERR_LICENSE1	= new ResId ("MdnAdminApp.err.license1"),
//	ERR_LICENSE2	= new ResId ("MdnAdminApp.err.license2"),
//	TEXT_STARTING	= new ResId ("MdnAdminApp.text.starting"),
//	TEXT_VERSION	= new ResId ("mdn.versionText"),
//	ERR_VERSION1	= new ResId ("MdnAdminApp.err.version1"),
//	ERR_VERSION2	= new ResId ("MdnAdminApp.err.version2"),
//	ERR_WILL_EXIT	= new ResId ("MdnAdminApp.err.willExit"),
//	ERR_NO_SERVER	= new ResId ("MdnAdminApp.err.noServer");	
	
	public static void main(String[] args) {
		//init();
		System.out.println("============================================");
			//FileReader reader = new FileReader("test.xml");
//			IDataAgent dataAgent = DataAgentFactory.getDataInterface();
//			try {
//				dataAgent.getQueryPropertiesByID(136);
//			} catch (MdnException e) {
//				e.printStackTrace();
//			}
//			File file = new File("C:/");
//			FileInputStream filei = new FileInputStream(file);

		
			/*		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	    
		String username = "testifjiaj";
	    String password = "test";
	    String email = "test@email.com";
	    String mobile = "123456789";
	    String details = "test";
	    String queryUserID = "123";
	    String groupId = "1009";
  
	    UserDataHandler handler = new UserDataHandler();
	    User user = new User();
		user.setState(0);//Insert State
	    user.setName(username);
	    user.setEmail(email);
	    user.setMobile(mobile);
	    user.setDetails(details);
	    user.setQueryUserId(queryUserID);
	    user.setGroupId(Integer.parseInt(groupId));
	    //new User(new SecurityId("admin", "admin"), "Administrator");
	    String encryptPass = SecurityManager.encryptPassword(password);
	    user.setPassword(encryptPass);
	    SecurityId se = new SecurityId(username, username);
	    System.out.println("...................."+encryptPass);


		try {
			dataAgent.saveUser(user);
		} catch (MdnException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/	}
//	static private void init(){
//		/*
//		 *	Set the ResourceManager
//		 * (must be first as everything uses resource strings)
//		 */
//		ResourceManager.set (new MdnResourceManager ());
//
//		// log start and version
//		Log.log (
//			TEXT_STARTING.getText ()
//			+ " " + TEXT_VERSION.getText ()
//			+ " " + MdnServer.VERSION_NUMBER);
//
//		// set the config (must be second as nearly everything uses configs)
//		Config.setSingleton (MdnAdminConst.MDN_CONFIG_FILE, true);
//		Config.getSingleton ().addContext (CKfw.RMICLIENT_CONTEXT);
//
//		// set the DataManager
//		DataManager.setDataManager (new MdnDataManager ());
//
//		// set the data cache
//		MdnDataCache.setCache (new MdnDataCache (false));		
//	}
	
}
