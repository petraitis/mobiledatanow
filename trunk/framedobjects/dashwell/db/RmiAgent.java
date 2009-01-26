package com.framedobjects.dashwell.db;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import wsl.fw.datasource.DataManager;
import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.DataSource;
import wsl.fw.datasource.DataSourceException;
import wsl.fw.datasource.Entity;
import wsl.fw.datasource.EntitySchemaName;
import wsl.fw.datasource.Field;
import wsl.fw.datasource.JdbcDataSourceParam;
import wsl.fw.datasource.Query;
import wsl.fw.datasource.QueryCriterium;
import wsl.fw.datasource.RecordSet;
import wsl.fw.datasource.Sort;
import wsl.fw.exception.MdnException;
import wsl.fw.resource.ResId;
import wsl.fw.resource.ResourceManager;
import wsl.fw.security.Feature;
import wsl.fw.security.Group;
import wsl.fw.security.GroupMembership;
import wsl.fw.security.SecurityManager;
import wsl.fw.security.User;
import wsl.fw.security.UserWrapper;
import wsl.fw.util.CKfw;
import wsl.fw.util.Config;
import wsl.fw.util.Log;
import wsl.mdn.common.MdnAdminConst;
import wsl.mdn.common.MdnDataManager;
import wsl.mdn.common.MdnResourceManager;
import wsl.mdn.dataview.DataSourceDobj;
import wsl.mdn.dataview.DataView;
import wsl.mdn.dataview.DataViewDataSource;
import wsl.mdn.dataview.DataViewField;
import wsl.mdn.dataview.EntityDobj;
import wsl.mdn.dataview.FieldDobj;
import wsl.mdn.dataview.FieldExclusion;
import wsl.mdn.dataview.GroupDataView;
import wsl.mdn.dataview.GroupTablePermission;
import wsl.mdn.dataview.JdbcDataSourceDobj;
import wsl.mdn.dataview.JdbcDriver;
import wsl.mdn.dataview.JoinDobj;
import wsl.mdn.dataview.LanguageDobj;
import wsl.mdn.dataview.MdnDataCache;
import wsl.mdn.dataview.ProjectDobj;
import wsl.mdn.dataview.QueryCriteriaDobj;
import wsl.mdn.dataview.QueryCriteriaHistoryDobj;
import wsl.mdn.dataview.QueryDobj;
import wsl.mdn.dataview.ResultWrapper;
//import wsl.mdn.dataview.UserLicenses;
import wsl.mdn.dataview.WebServiceDobj;
import wsl.mdn.dataview.WebServiceOperationDobj;
import wsl.mdn.guiconfig.MenuAction;
import wsl.mdn.mdnim.IMConnection;
import wsl.mdn.mdnim.IMContact;
import wsl.mdn.mdnim.IMMessage;
import wsl.mdn.mdnmsgsvr.BlockContacts;
import wsl.mdn.mdnmsgsvr.CustomField;
import wsl.mdn.mdnmsgsvr.MdnSmpp;
import wsl.mdn.mdnmsgsvr.MessagingSettingDetails;
import wsl.mdn.mdnmsgsvr.MdnEmailSetting;
import wsl.mdn.mdnmsgsvr.MdnMessageSeparator;
import wsl.mdn.mdnmsgsvr.MdnSmsSetting;
import wsl.mdn.mdnmsgsvr.MessageLog;
import wsl.mdn.mdnmsgsvr.TemporaryBlockContacInfo;
import wsl.mdn.mdnmsgsvr.UserCustomField;
import wsl.mdn.mdnmsgsvr.UserReply;
import wsl.mdn.server.LicenseManager;
import wsl.mdn.server.MdnServer;

import com.framedobjects.dashwell.biz.RecycleBinItem;
import com.framedobjects.dashwell.db.meta.MetaDbConnection;
import com.framedobjects.dashwell.mdnEmail.MdnMailService;
import com.framedobjects.dashwell.mdninstantmsg.MDNIMServer;
import com.framedobjects.dashwell.mdnsms.MdnSmppGatewayServer;
import com.framedobjects.dashwell.mdnsms.MdnSmsServer;
import com.framedobjects.dashwell.utils.MessagingUtils;
import com.framedobjects.dashwell.utils.Constants;

/**
 * Interacts via RMI with server to retrieve data.
 * @author Jens Richnow
 *
 */
public class RmiAgent implements IDataAgent {
	
	static Logger logger = Logger.getLogger(RmiAgent.class.getName());
	
	// resources
	// public static final ResId ERR_HELP   = new ResId ("MdnAdmin.err.Help");
	public static final ResId
		ERR_LICENSE1	= new ResId ("MdnAdminApp.err.license1"),
		ERR_LICENSE2	= new ResId ("MdnAdminApp.err.license2"),
		TEXT_STARTING	= new ResId ("MdnAdminApp.text.starting"),
		TEXT_VERSION	= new ResId ("mdn.versionText"),
		ERR_VERSION1	= new ResId ("MdnAdminApp.err.version1"),
		ERR_VERSION2	= new ResId ("MdnAdminApp.err.version2"),
		ERR_WILL_EXIT	= new ResId ("MdnAdminApp.err.willExit"),
		ERR_NO_SERVER	= new ResId ("MdnAdminApp.err.noServer");	
	
	public static boolean isInitialised = false;
	public static final String CONNECTED = "Connected!";
	private boolean execSql;
	public static final int FAILED_EXEC_SQL = -10;
	
	public RmiAgent() {
		super();
		if (!isInitialised) {
	        init();
	        isInitialised = true;
	      }
	}
	
	private void init(){
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
	
	public int getNumberOfUsers(){
		// get the activation key
		return LicenseManager.getNumberOfUserLicense();
	}	

	public Boolean getPublicGroupFlag(){//True:unlimitided 
		// get the activation key
		return LicenseManager.getPublicGroupFlag();
	}	
	public int getAvailablePublicMessages(){ 
		// get the activation key
		return LicenseManager.getAvailablePublicMessages();
	}	
//	public ResultWrapper getUserLicenses(){
//		// get the activation key
//		return LicenseManager.getUserLicenses();
//	}		

	public String[] getLogFilePath(){
		// get the LogFilePath
		return LicenseManager.getLogFilePath();
	}	
	
//	public ArrayList<String> getUserLicenseTypes(){
//		ResultWrapper resultWrapper = getUserLicenses();
//		UserLicenses userLicenses = (UserLicenses)resultWrapper.getObject();
//		if (userLicenses != null){
//			return userLicenses.getUserLicenseType();
//		}else{
//			return new ArrayList<String>();
//		}		
//	}
	
	public List<LanguageDobj> getAllLanguages() throws MdnException{
		//Find LanguageDobj
		DataSource ds = DataManager.getSystemDS();
		List<LanguageDobj> langs = new Vector<LanguageDobj>();
		Query q = new Query(LanguageDobj.ENT_LANGUAGE);
        QueryCriterium qc = new QueryCriterium(LanguageDobj.ENT_LANGUAGE,
        		LanguageDobj.FLD_DEL_STATUS, QueryCriterium.OP_EQUALS,
                Constants.MARKED_AS_NOT_DELETED_INT);
        q.addQueryCriterium(qc);
        try {
			RecordSet rs = ds.select(q);
			while (rs.next()){
				LanguageDobj lang = (LanguageDobj)rs.getCurrentObject();
				langs.add(lang);
			}
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException when getAllLanguages()", e);
		}		
		return langs;
	}
	public LanguageDobj getDefaultLanguage() throws MdnException{
		//Find LanguageDobj
		DataSource ds = DataManager.getSystemDS();
		Query q = new Query(LanguageDobj.ENT_LANGUAGE);
        QueryCriterium qc = new QueryCriterium(LanguageDobj.ENT_LANGUAGE,
        		LanguageDobj.FLD_DEL_STATUS, QueryCriterium.OP_EQUALS,
                Constants.MARKED_AS_NOT_DELETED_INT);
        q.addQueryCriterium(qc);
        QueryCriterium qc2 = new QueryCriterium(LanguageDobj.ENT_LANGUAGE,
        		LanguageDobj.FLD_DEFAULT, QueryCriterium.OP_EQUALS,
                true);
        q.addQueryCriterium(qc2);
        try {
			RecordSet rs = ds.select(q);
			if (rs.next()){
				LanguageDobj lang = (LanguageDobj)rs.getCurrentObject();
				return lang;
			}
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException when getDefaultLanguage()", e);
		}		
		return null;
	}	
	
	public LanguageDobj getLanguageById(int id) throws MdnException{
		//Find LanguageDobj
		DataSource ds = DataManager.getSystemDS();
		Query q = new Query(LanguageDobj.ENT_LANGUAGE);
        /*QueryCriterium qc = new QueryCriterium(LanguageDobj.ENT_LANGUAGE,
        		LanguageDobj.FLD_DEL_STATUS, QueryCriterium.OP_EQUALS,
                Constants.MARKED_AS_NOT_DELETED_INT);
        q.addQueryCriterium(qc);*/
        QueryCriterium qc2 = new QueryCriterium(LanguageDobj.ENT_LANGUAGE,
        		LanguageDobj.FLD_ID, QueryCriterium.OP_EQUALS,
                id);
        q.addQueryCriterium(qc2);
        try {
			RecordSet rs = ds.select(q);
			if (rs.next()){
				LanguageDobj lang = (LanguageDobj)rs.getCurrentObject();
				return lang;
			}
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException when getLanguageById()", e);
		}		
		return null;
	}	
	public LanguageDobj getLanguageByName(String languageName) throws MdnException{
		//Find LanguageDobj
		DataSource ds = DataManager.getSystemDS();
		Query q = new Query(LanguageDobj.ENT_LANGUAGE);
        QueryCriterium qc = new QueryCriterium(LanguageDobj.ENT_LANGUAGE,
        		LanguageDobj.FLD_DEL_STATUS, QueryCriterium.OP_EQUALS,
                Constants.MARKED_AS_NOT_DELETED_INT);
        q.addQueryCriterium(qc);
        QueryCriterium qc2 = new QueryCriterium(LanguageDobj.ENT_LANGUAGE,
        		LanguageDobj.FLD_NAME, QueryCriterium.OP_EQUALS,
        		languageName);
        q.addQueryCriterium(qc2);
        try {
			RecordSet rs = ds.select(q);
			if (rs.next()){
				LanguageDobj lang = (LanguageDobj)rs.getCurrentObject();
				return lang;
			}
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException when getLanguageByName()", e);
		}		
		return null;
	}		
	public List<JdbcDriver> getAllJdbcDrivers() throws MdnException{
		//Find JdbcDriver
		DataSource ds = DataManager.getSystemDS();
		List<JdbcDriver> jdbcDrivers = new Vector<JdbcDriver>();
		Query q = new Query(JdbcDriver.ENT_JDBCDRIVER);
        QueryCriterium qc = new QueryCriterium(JdbcDriver.ENT_JDBCDRIVER,
        		JdbcDriver.FLD_DEL_STATUS, QueryCriterium.OP_EQUALS,
                Constants.MARKED_AS_NOT_DELETED_INT);
        q.addQueryCriterium(qc);
        try {
			RecordSet rs = ds.select(q);
			while (rs.next()){
				JdbcDriver jdbcDriver = (JdbcDriver)rs.getCurrentObject();
				jdbcDrivers.add(jdbcDriver);
			}
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException when getAllJdbcDrivers()", e);
		}		
		return jdbcDrivers;
	}
	public JdbcDriver getJdbcDriverByName(String driverName) throws MdnException{
		//Find JdbcDriver
		DataSource ds = DataManager.getSystemDS();
		JdbcDriver jdbcDriver = null;
		Query q = new Query(JdbcDriver.ENT_JDBCDRIVER);
		QueryCriterium qc = new QueryCriterium(JdbcDriver.ENT_JDBCDRIVER, JdbcDriver.FLD_NAME, QueryCriterium.OP_EQUALS, driverName);
		qc.setOrIsNull(true);
		q.addQueryCriterium(qc);
		try {
			RecordSet rs = ds.select(q);
			if (rs.next()){
				jdbcDriver = (JdbcDriver)rs.getCurrentObject();
			}
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException when getJDBCDriverByName()", e);
		}		
		return jdbcDriver;
	}
	public JdbcDriver getJdbcDriverByDriver(String driver) throws MdnException{
		//Find JdbcDriver
		DataSource ds = DataManager.getSystemDS();
		JdbcDriver jdbcDriver = null;
		Query q = new Query(JdbcDriver.ENT_JDBCDRIVER);
		QueryCriterium qc = new QueryCriterium(JdbcDriver.ENT_JDBCDRIVER, JdbcDriver.FLD_DRIVER, QueryCriterium.OP_EQUALS, driver);
		qc.setOrIsNull(true);
		q.addQueryCriterium(qc);
		try {
			RecordSet rs = ds.select(q);
			if (rs.next()){
				jdbcDriver = (JdbcDriver)rs.getCurrentObject();
			}
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException when getJDBCDriverByName()", e);
		}		
		return jdbcDriver;
	}
	public JdbcDriver getJdbcDriverById(int driverId) throws MdnException{
		//Find JdbcDriver
		DataSource ds = DataManager.getSystemDS();
		JdbcDriver jdbcDriver = null;
		Query q = new Query(JdbcDriver.ENT_JDBCDRIVER);
		QueryCriterium qc = new QueryCriterium(JdbcDriver.ENT_JDBCDRIVER, JdbcDriver.FLD_ID, QueryCriterium.OP_EQUALS, driverId);
		qc.setOrIsNull(true);
		q.addQueryCriterium(qc);
		try {
			RecordSet rs = ds.select(q);
			if (rs.next()){
				jdbcDriver = (JdbcDriver)rs.getCurrentObject();
			}
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException when getJDBCDriverByName()", e);
		}		
		return jdbcDriver;
	}
	public String handleTestDbConnection(DbConnection dbConn){
        // make a param describing the data source
        String     dsName  = dbConn.getName();
        JdbcDriver d       = dbConn.getJdbcDriver();
        String     driver  = (d == null) ? "" : d.getDriver();
        //String     url     = dbConn.getUrl();
        //String     catalog = dbConn.getSchema();
        String     user    = dbConn.getUsername();
        String     pw      = dbConn.getPassword();

        /*JdbcDataSourceParam dsParam = new JdbcDataSourceParam(dsName,
            driver, url, catalog, user, pw);*/
		String wholeUrl = dbConn.getUrl(); // + ":" + dbConn.getSchema();
		JdbcDataSourceParam dsParam = new JdbcDataSourceParam(dsName,
				driver, wholeUrl, user, pw);//catalog, 
        // get/create the data source from Datamanager
        DataSource ds = DataManager.getDataSource(dsParam);

        // test that the connection is valid
        String result = null;
        try {
			ds.testConnection();
			result = CONNECTED;
			
		} catch (DataSourceException e) {
			e.printStackTrace();
			//result = "Driver not supported";
			result = "Could not connect to the database. Check that the JDBC driver and URL are correct and that the database is accessible.";
		}catch (Exception e) {
			e.printStackTrace();
			//result = "Driver not supported";
			result = "Could not connect to the database. Check that the JDBC driver and URL are correct and that the database is accessible.";
		}		
//		DbConnectionTest test = DbConnectionTestFactory.getDbConnectionTest(dbConn.getDriver());
		/*if (test != null){
			String result = test.test(dbConn); 
			if (result == null){
				result = CONNECTED;
			} 
			return result;
		}*/
		return result;
	}
	public ResultWrapper createNewDbConnection(int projectId, DbConnection dbConn) {
		
		//Create JdbcDataSourceDobj from DbConnection
		JdbcDataSourceDobj jdbcDataSourceDobj = new JdbcDataSourceDobj();
		jdbcDataSourceDobj.setName(dbConn.getName());
		//String url = dbConn.getUrl() + ":" + dbConn.getSchema();
		String url = dbConn.getUrl();// + ":" + dbConn.getSchema();
		jdbcDataSourceDobj.setJdbcUrl(url);
		jdbcDataSourceDobj.setJdbcUser(dbConn.getUsername());
		jdbcDataSourceDobj.setJdbcPassword(dbConn.getPassword());
		//jdbcDataSourceDobj.setJdbcCatalog(dbConn.getSchema());
		jdbcDataSourceDobj.setIsMirrored(dbConn.getMirrorred() > 0 ? true : false);
		
		JdbcDriver jdbcDriver = dbConn.getJdbcDriver();//getJdbcDriverByName(dbConn.getDriver());

		jdbcDataSourceDobj.setDriverDobj(jdbcDriver);
		jdbcDataSourceDobj.setJdbcDriverId(jdbcDriver.getId());
		jdbcDataSourceDobj.setProjectId(projectId);

        // get/create the data source from Datamanager
        // create a param defining the datasource
		String wholeUrl = dbConn.getUrl(); // + ":" + dbConn.getSchema();
		JdbcDataSourceParam dsParam = new JdbcDataSourceParam(dbConn.getName(),
        		jdbcDriver.getDriver(), wholeUrl, //dbConn.getSchema(),
        		dbConn.getUsername(), dbConn.getPassword());

        // get/create the datasource
        DataSource _impl = DataManager.getDataSource(dsParam);
		
		//Insert into database
		try {
			jdbcDataSourceDobj.setState(DataObject.NEW);
			jdbcDataSourceDobj.setDelStatus(Constants.MARKED_AS_NOT_DELETED_INT);
			jdbcDataSourceDobj.save();
			int lastInsertId = jdbcDataSourceDobj.getId();
			logger.debug("... schema inserted with connection_id " + lastInsertId);
            
	        Vector tableNames = _impl.importTableNames();
	        // import definition
	        EntitySchemaName esn;
	        EntityDobj ed;
	        // iterate table names
	        // save all entities
	        for(int i = 0; tableNames != null && i < tableNames.size(); i++)
	        {
	            // create the EntityDobj and partially init it with the esn
	            esn = (EntitySchemaName) tableNames.elementAt(i);
	            ed = new EntityDobj();
	            ed.setEsn(esn); 
				
		        Entity importedEnt = null;
		        try
		        {
		            importedEnt = _impl.importEntityDefinition(esn);
		        }
		        catch (DataSourceException e)
		        {
		            e.printStackTrace();
		        	Log.error("DataSourceException when createNewDbConnection(): ", e);
		            return new ResultWrapper(jdbcDataSourceDobj, e.toString());
		        }catch (Exception e) {
		        	return new ResultWrapper(jdbcDataSourceDobj, e.toString());
				}

		        if (importedEnt != null)
		        {
		        	ed.setName(importedEnt.getName());
		        	ed.setFlags(importedEnt.getFlags());
					// if new set keys
		            ed.setDataSourceId (lastInsertId);
		        	
					// save entity/table
		            ed.save ();
					System.out.println("... table inserted with table_id " + ed.getId());
		            logger.debug("... table inserted with table_id " + ed.getId());		        	
		        	
		            // add the FieldDobjs
		            for(int j = 0; importedEnt.getFields() != null && j < importedEnt.getFields().size(); j++)
		            {
		                // create the FieldDobj
		                Field f = (Field) importedEnt.getFields().elementAt(j);
		                FieldDobj fd = new FieldDobj(f);
		                ed.addField(fd);
		                fd.setDsId(lastInsertId);
		                fd.setEntityId(ed.getId());
		                fd.save();
		                //System.out.println("... field inserted with field_id " + fd.getId());
		                logger.debug("... field inserted with field_id " + fd.getId());
		            }
		        }	
		        
			}
	        
	        return new ResultWrapper(jdbcDataSourceDobj, "OK");//getAllDbConnections(projectId);
		} catch (DataSourceException e) {
			e.printStackTrace();
			//throw new MdnException("DataSourceException when createNewDbConnection()", e);
			return new ResultWrapper(jdbcDataSourceDobj, e.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultWrapper(jdbcDataSourceDobj, e.toString());
		}
	}
	public DbConnection dbConnection(HttpServletRequest request){
	    String idString = request.getParameter("id");
	    int id = idString != null ? Integer.parseInt(idString) : 0;
	    String name = request.getParameter("name");
	    String url = request.getParameter("url");
	    //String schema = request.getParameter("schema");
	    String driverId = request.getParameter("driverId");
	    String username = request.getParameter("username");
	    String password = request.getParameter("password");
	    String mirrorString = request.getParameter("mirrorred");
	    int mirror = 0;
	    if (mirrorString != null){
	    	mirror = mirrorString.equalsIgnoreCase("true") ? 1 : 0;
	    }
	    DbConnection newConn = new DbConnection(id, Integer.parseInt(driverId), name, username, password, url, mirror);
		//Find JdbcDriver
	    JdbcDriver jdbcDriver = null;
		try {
			jdbcDriver = getJdbcDriverById(Integer.parseInt(driverId));
		} catch (MdnException e) {
			jdbcDriver = null;
		}	    
		newConn.setJdbcDriver(jdbcDriver);
	    // Get the DbConnection object.
	    return newConn;
	}	
	
	public List<DataSourceDobj> getAllDbConnections(int projectId) throws MdnException{
        // select all datasources
        DataSource ds = DataManager.getSystemDS();
        Query q = new Query(DataSourceDobj.ENT_DATASOURCE);
        QueryCriterium qc = new QueryCriterium(JdbcDataSourceDobj.ENT_DATASOURCE,
            JdbcDataSourceDobj.FLD_IS_MIRROR_DB, QueryCriterium.OP_EQUALS,
            new Boolean(false));
        qc.setOrIsNull(true);
        q.addQueryCriterium(qc);
        
        QueryCriterium qc2 = new QueryCriterium(JdbcDataSourceDobj.ENT_DATASOURCE,
                JdbcDataSourceDobj.FLD_PROJECT_ID, QueryCriterium.OP_EQUALS,
                projectId);
        q.addQueryCriterium(qc2);            
        
        QueryCriterium qc3 = new QueryCriterium(JdbcDataSourceDobj.ENT_DATASOURCE,
                JdbcDataSourceDobj.FLD_DEL_STATUS, QueryCriterium.OP_EQUALS,
                Constants.MARKED_AS_NOT_DELETED_INT);
        q.addQueryCriterium(qc3);   
        
        try {
			RecordSet rs = ds.select(q);
			List<DataSourceDobj> list = new Vector<DataSourceDobj>();
			while (rs.next()){
				DataSourceDobj object = (DataSourceDobj)rs.getCurrentObject();
				list.add(object);
	        }
			return list;
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException when getAllDbConnections()", e);
		}
	}
	
	public List<WebServiceDobj> getAllSampleWebServices() throws MdnException{
        // select all datasources
        DataSource ds = DataManager.getSystemDS();
        Query q = new Query(WebServiceDobj.ENT_WEB_SERVICE);
        QueryCriterium qc = new QueryCriterium(WebServiceDobj.ENT_WEB_SERVICE,
        		WebServiceDobj.FLD_TYPE, QueryCriterium.OP_EQUALS,
        		WebServiceDobj.TYPE_SAMPLE);
        qc.setOrIsNull(true);
        q.addQueryCriterium(qc);
        try {
			RecordSet rs = ds.select(q);
			List<WebServiceDobj> list = new Vector<WebServiceDobj>();
			while (rs.next()){
				WebServiceDobj object = (WebServiceDobj)rs.getCurrentObject();
				list.add(object);
	        }
			return list;
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException when getAllSampleWebServices()", e);
		}
	}	

	public List<WebServiceDobj> getAllThirdPartyWebServices() throws MdnException{
        // select all datasources
        DataSource ds = DataManager.getSystemDS();
        Query q = new Query(WebServiceDobj.ENT_WEB_SERVICE);
        QueryCriterium qc = new QueryCriterium(WebServiceDobj.ENT_WEB_SERVICE,
        		WebServiceDobj.FLD_TYPE, QueryCriterium.OP_EQUALS,
        		WebServiceDobj.TYPE_THIRDPARTY);
        qc.setOrIsNull(true);
        q.addQueryCriterium(qc);
        try {
			RecordSet rs = ds.select(q);
			List<WebServiceDobj> list = new Vector<WebServiceDobj>();
			while (rs.next()){
				WebServiceDobj object = (WebServiceDobj)rs.getCurrentObject();
				list.add(object);
	        }
			return list;
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException when getAllSampleWebServices()", e);
		}
	}		

	public int clearConnection(int projectId, int connectionID){
		try {
			DataSourceDobj sourceDs = getDbConnectionByID(projectId, connectionID);
			sourceDs.setDelStatus(Constants.MARKED_AS_NOT_DELETED_INT);//delete status = 1
			sourceDs.save();
			return 1;
		} catch (MdnException e) {
			e.printStackTrace();
		} catch (DataSourceException e) {
			e.printStackTrace();
		}
		return 0;
	}	
	
	public int recycleConnection(int projectId, int connectionID){
		try {
			DataSourceDobj sourceDs = getDbConnectionByID(projectId, connectionID);
			sourceDs.setDelStatus(Constants.MARKED_AS_RECYCLED_INT);//delete status = 1
			sourceDs.save();
			return 1;
		} catch (MdnException e) {
			e.printStackTrace();
		} catch (DataSourceException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int deleteConnection(int projectId, int connectionID){
		try {
			DataSourceDobj sourceDs = getDbConnectionByID(projectId, connectionID);
			sourceDs.delete();
			return 1;
		} catch (MdnException e) {
			e.printStackTrace();
		} catch (DataSourceException e) {
			e.printStackTrace();
		}
		return 0;
	}	
	
	public List<EntityDobj> getAllMetaTables(int dbConnectionID, boolean includeFields) throws MdnException{
		// build entity/field join query
		DataSource ds = DataManager.getSystemDS ();
		Query q = new Query (EntityDobj.ENT_ENTITY);
		//q.addQueryEntity (FieldDobj.ENT_FIELD);
		q.addQueryCriterium (new QueryCriterium (EntityDobj.ENT_ENTITY,
			EntityDobj.FLD_DSID, QueryCriterium.OP_EQUALS,
			new Integer (dbConnectionID)));
		//q.addSort (new Sort (EntityDobj.ENT_ENTITY, EntityDobj.FLD_ID));
		//q.addSort (new Sort (FieldDobj.ENT_FIELD, FieldDobj.FLD_ID));
		try {
			RecordSet rs = ds.select (q);
			List<EntityDobj> tables = new Vector<EntityDobj>();
			while (rs != null && rs.next ())
			{
				EntityDobj ent = (EntityDobj)rs.getCurrentObject ();
				tables.add(ent);
			}
			return tables;
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getAllMetaTables", e);
		}
	}
	
	public EntityDobj getMetaTableByID(int tableID, boolean includeFields) throws MdnException{
		// build entity/field join query
		DataSource ds = DataManager.getSystemDS ();
		Query q = new Query (EntityDobj.ENT_ENTITY);
		//q.addQueryEntity (FieldDobj.ENT_FIELD);
		q.addQueryCriterium (new QueryCriterium (EntityDobj.ENT_ENTITY,
			EntityDobj.FLD_ID, QueryCriterium.OP_EQUALS,
			new Integer (tableID)));
		RecordSet rs;
		try {
			rs = ds.select (q);
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getMetaTableByID", e);
		}
		if (rs != null && rs.next ())
		{
			EntityDobj ent = (EntityDobj)rs.getCurrentObject ();
			return ent;
		}
		return null;
	}
	
	public EntityDobj getMetaTableByName(String tableName, int connID, boolean includeFields) throws MdnException{
		// build entity/field join query
		DataSource ds = DataManager.getSystemDS ();
		Query q = new Query (EntityDobj.ENT_ENTITY);
		//q.addQueryEntity (FieldDobj.ENT_FIELD);
		q.addQueryCriterium (new QueryCriterium (EntityDobj.ENT_ENTITY,
			EntityDobj.FLD_NAME, QueryCriterium.OP_EQUALS,
			tableName));
		RecordSet rs;
		try {
			rs = ds.select (q);
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getMetaTableByID", e);
		}
		if (rs != null && rs.next ())
		{
			EntityDobj ent = (EntityDobj)rs.getCurrentObject ();
			return ent;
		}
		return null;
	}
	public DataView getMetaViewByName(String viewName, int connID) throws MdnException {
		// build entity/field join query
		DataSource ds = DataManager.getSystemDS ();
		
		Query q = new Query(DataView.ENT_DATAVIEW);
        q.addQueryCriterium(new QueryCriterium(DataView.ENT_DATAVIEW,
            DataView.FLD_NAME, QueryCriterium.OP_EQUALS,
            viewName));
        q.addQueryCriterium(new QueryCriterium(DataView.ENT_DATAVIEW,
                DataView.FLD_SOURCE_DSID, QueryCriterium.OP_EQUALS,
                connID));
        RecordSet rs;
		try {
			rs = ds.select (q);
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getMetaViewByName", e);
		}
		if (rs != null && rs.next ())
		{
			DataView ent = (DataView)rs.getCurrentObject ();
			return ent;
		}
		return null;
	}	
	
	public int editMetaTable(EntityDobj table){
		try {
			table.setState (DataObject.IN_DB);
			table.save();
			return 1;
		} catch (DataSourceException e) {
			e.printStackTrace();
			return -1;
		}
//		// drop table on old name
//        if(ds.isMirrored())
//            dropMirrorTable(ds, oldName);
//
//        // recreate table
//        if(ds.isMirrored())
//            createMirrorTable(ds, ed);
	}
	
	public FieldDobj getMetaField(int fieldID) throws MdnException{
        // build query
        DataSource ds = DataManager.getSystemDS();
        Query q = new Query(FieldDobj.ENT_FIELD);
        q.addQueryCriterium(new QueryCriterium(FieldDobj.ENT_FIELD,
            FieldDobj.FLD_ID, QueryCriterium.OP_EQUALS,
            new Integer(fieldID)));
        RecordSet rs;
		try {
			rs = ds.select(q);
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getMetaField", e);
		}   
		if (rs != null && rs.next ())
		{
			FieldDobj fieldDobj = (FieldDobj)rs.getCurrentObject ();
			return fieldDobj;
		}
		return null;		
	}
	
	public DataSourceDobj getDbConnectionByName(int projectId, String dbConnName) throws MdnException{
        // create query
        DataSource sysDs = DataManager.getSystemDS();
        Query q = new Query(new QueryCriterium(DataSourceDobj.ENT_DATASOURCE,
            DataSourceDobj.FLD_NAME, QueryCriterium.OP_EQUALS, dbConnName));
        
        QueryCriterium qc2 = new QueryCriterium(JdbcDataSourceDobj.ENT_DATASOURCE,
                JdbcDataSourceDobj.FLD_PROJECT_ID, QueryCriterium.OP_EQUALS,
                projectId);
        q.addQueryCriterium(qc2);        
        
        RecordSet rs;
		try {
			rs = sysDs.select(q);
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getDbConnectionByName", e);
		}
        DataSourceDobj sourceDs = null;
        // get the ds
        if(rs != null && rs.next())
            sourceDs = (DataSourceDobj)rs.getCurrentObject();
        
        return sourceDs;
	}
	
	public DataSourceDobj getDbConnectionByID(int projectId, int connectionID) throws MdnException{
        // create query
        DataSource sysDs = DataManager.getSystemDS();
        Query q = new Query(new QueryCriterium(DataSourceDobj.ENT_DATASOURCE,
            DataSourceDobj.FLD_ID, QueryCriterium.OP_EQUALS, new Integer(connectionID)));
        
        QueryCriterium qc2 = new QueryCriterium(JdbcDataSourceDobj.ENT_DATASOURCE,
                JdbcDataSourceDobj.FLD_PROJECT_ID, QueryCriterium.OP_EQUALS,
                projectId);
        q.addQueryCriterium(qc2);        
        RecordSet rs;
		try {
			rs = sysDs.select(q);
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getDbConnectionByID", e);
		}
        DataSourceDobj sourceDs = null;
        // get the ds
        if(rs != null && rs.next())
            sourceDs = (DataSourceDobj)rs.getCurrentObject();
        
        return sourceDs;
	}
	
	public MetaDbConnection getDbConnectionByFullUrl(String url, String schema){
		return null;
	}

	
	public Group getGroup(int groupID) throws MdnException {
        DataSource ds = DataManager.getSystemDS();

        Query q1 = new Query(Group.ENT_GROUP);
        QueryCriterium qc1 = new QueryCriterium(Group.ENT_GROUP, Group.FLD_ID,
            QueryCriterium.OP_EQUALS, new Integer(groupID) );
        q1.addQueryCriterium(qc1);
        RecordSet rsGroup;
		try {
			rsGroup = ds.select(q1);
	        if (rsGroup.next())
	        {
	            Group group = (Group)rsGroup.getCurrentObject();
	            return group;
	        }  			
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getGroup()", e);
		}   
        return null;
	}
	
	public Group getGroupByName(String groupName, int projectId) throws MdnException{
        
        DataSource ds = DataManager.getDataSource(Group.ENT_GROUP);
        QueryCriterium qc1 = new QueryCriterium(Group.ENT_GROUP, Group.FLD_NAME,QueryCriterium.OP_EQUALS, groupName );
        QueryCriterium qc2 = new QueryCriterium(Group.ENT_GROUP, Group.FLD_PROJECT_ID,QueryCriterium.OP_EQUALS, projectId );
        Query q = new Query(qc1, qc2);
        RecordSet rsGroup;
		try {
			rsGroup = ds.select(q);
	        if (rsGroup.next())
	        {
	            Group group = (Group)rsGroup.getCurrentObject();
	            return group;
	        }  			
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getGroupByName()", e);
		}   
        return null;
	}
	
	public Group getGuestGroup(int projectId) throws MdnException{
        
        DataSource ds = DataManager.getDataSource(Group.ENT_GROUP);
        QueryCriterium qc1 = new QueryCriterium(Group.ENT_GROUP, Group.FLD_GUEST,QueryCriterium.OP_EQUALS, 1);
        QueryCriterium qc2 = new QueryCriterium(Group.ENT_GROUP, Group.FLD_PROJECT_ID,QueryCriterium.OP_EQUALS, projectId );
        Query q = new Query(qc1, qc2);
        RecordSet rsGroup;
		try {
			rsGroup = ds.select(q);
	        if (rsGroup.next())
	        {
	            Group group = (Group)rsGroup.getCurrentObject();
	            return group;
	        }  			
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getGroupByName()", e);
		}   
        return null;
	}	
	public GroupMembership getUserGroupByProjectId(int userId, int projectId) throws MdnException{
		DataSource ds = DataManager.getDataSource(GroupMembership.ENT_GROUPMEMBERSHIP);
        QueryCriterium q1 = new QueryCriterium(GroupMembership.ENT_GROUPMEMBERSHIP, GroupMembership.FLD_USERID, QueryCriterium.OP_EQUALS, new Integer(userId));
        QueryCriterium q2 = new QueryCriterium(GroupMembership.ENT_GROUPMEMBERSHIP, GroupMembership.FLD_PROJECT_ID, QueryCriterium.OP_EQUALS, new Integer(projectId));
        Query query = new Query(q1,q2);
        
        // get the results
        RecordSet rs;
		try {
			rs = ds.select(query);
	        // get the user out of the result set
	        if (rs.next())
	        {
	        	GroupMembership group = (GroupMembership) rs.getCurrentObject();
	        	return group;
	        }	
	        return null;
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getUserGroupByProjectId()", e);
		}
	}

	public List<GroupMembership> getUserGroupByGroupId(int groupId, int projectId) throws MdnException{
		DataSource ds = DataManager.getDataSource(GroupMembership.ENT_GROUPMEMBERSHIP);
        QueryCriterium q1 = new QueryCriterium(GroupMembership.ENT_GROUPMEMBERSHIP, GroupMembership.FLD_GROUPID, QueryCriterium.OP_EQUALS, new Integer(groupId));
        QueryCriterium q2 = new QueryCriterium(GroupMembership.ENT_GROUPMEMBERSHIP, GroupMembership.FLD_PROJECT_ID, QueryCriterium.OP_EQUALS, new Integer(projectId));
        Query query = new Query(q1,q2);
        
        // get the results
        RecordSet rs;
        Vector<GroupMembership> groupsMembership = new Vector<GroupMembership>();
		try {
			rs = ds.select(query);
	        // get the user out of the result set
			while (rs.next())
	        {
	        	GroupMembership group = (GroupMembership) rs.getCurrentObject();
	        	//return group;
	        	groupsMembership.add(group);
	        }	
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getUserGroupByProjectId()", e);
		}
		return groupsMembership;
	}
	
	public Vector<Group> getGroups(int projectId) throws MdnException{
        // select all groups
        DataSource ds = DataManager.getSystemDS();
        Query q = new Query(new QueryCriterium(Group.ENT_GROUP, Group.FLD_PROJECT_ID,
                QueryCriterium.OP_EQUALS, projectId ));
        RecordSet rsGroup;
        Vector<Group> groups = new Vector<Group>();
        try {
			rsGroup = ds.select(q);
	        while (rsGroup.next())
	        {
	            Group group = (Group)rsGroup.getCurrentObject();
	            if(group.getDelStatus()!= null && group.getDelStatus().equals(Constants.MARKED_AS_NOT_DELETED))
	            groups.add(group);
	        }  			
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getGroups()", e);
		}
        return groups;
	}

	public int saveGroup(Group group) throws MdnException {
		try {
			group.setDelStatus(Constants.MARKED_AS_NOT_DELETED);
			group.save();
			return group.getId();
		} catch (DataSourceException e) {
			e.printStackTrace();
			//throw new MdnException("DataSourceException in saveGroup", e);
			return 0;
		}
	}
	
	public List<GroupDataView> getGroupViewsPermissions(int groupID) throws MdnException
	{
		DataSource ds = DataManager.getSystemDS();
		Query q = new Query(GroupDataView.ENT_GROUPDATAVIEW);
        QueryCriterium qc = new QueryCriterium(GroupDataView.ENT_GROUPDATAVIEW, GroupDataView.FLD_GROUPID,
            QueryCriterium.OP_EQUALS, groupID );
        q.addQueryCriterium(qc);
        Vector<GroupDataView> groupViews = new Vector<GroupDataView>();
        try {
			RecordSet rsGroupDataView = ds.select(q);
			while(rsGroupDataView.next())
            {
            	GroupDataView dobj = (GroupDataView)rsGroupDataView.getCurrentObject();
            	groupViews.add(dobj);
            }			
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getGroupViewsPermissions()", e);
		}
		return groupViews;
	}
	
	public List<GroupTablePermission> getGroupTablePermissions(int groupID) throws MdnException
	{
		DataSource ds = DataManager.getSystemDS();
		Query q = new Query(GroupTablePermission.ENT_GROUP_ENTITY_PERMISSION);
        QueryCriterium qc = new QueryCriterium(GroupTablePermission.ENT_GROUP_ENTITY_PERMISSION, GroupTablePermission.FLD_GROUPID,
            QueryCriterium.OP_EQUALS, groupID );
        q.addQueryCriterium(qc);
        Vector<GroupTablePermission> groupTables = new Vector<GroupTablePermission>();
        try {
			RecordSet rsGroupDataView = ds.select(q);
			while(rsGroupDataView.next())
            {
				GroupTablePermission tablPermission = (GroupTablePermission)rsGroupDataView.getCurrentObject();
				groupTables.add(tablPermission);
            }			
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getGroupTablePermissions()", e);
		}
		return groupTables;
	}
	
	public GroupTablePermission getGroupTablePermissionByEntityID(int entityId,int groupID) throws MdnException{  
        DataSource ds = DataManager.getSystemDS();
        Query q = new Query(GroupTablePermission.ENT_GROUP_ENTITY_PERMISSION);
        
        QueryCriterium qc = new QueryCriterium(GroupTablePermission.ENT_GROUP_ENTITY_PERMISSION, GroupTablePermission.FLD_ENTITY_ID, QueryCriterium.OP_EQUALS, entityId);
        QueryCriterium qc2 = new QueryCriterium(GroupTablePermission.ENT_GROUP_ENTITY_PERMISSION, GroupTablePermission.FLD_GROUPID,QueryCriterium.OP_EQUALS, groupID );

        q.addQueryCriterium(qc);
        q.addQueryCriterium(qc2);            
        
		try {
			RecordSet rsGroupTablePermission = ds.select(q);
	        if (rsGroupTablePermission.next())
	        {
	        	GroupTablePermission groupTblPermission = (GroupTablePermission)rsGroupTablePermission.getCurrentObject();
	            return groupTblPermission;
	        }  			
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getGroupTablePermissionByEntityID()", e);
		}   
        return null;
	}
	
	public int saveGroupDataView(GroupDataView groupDataView) throws MdnException
	{
		int groupFieldId = 0;
		try {
			groupDataView.save();
			groupFieldId = groupDataView.getId();
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in saveGroupDataViews", e);
		}
		return groupFieldId;
	}

	public void deleteGroupDataView(GroupDataView groupDataView) throws MdnException {
        try {
        	groupDataView.delete();
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in groupDataView", e);
		}
	}

	public Vector<GroupDataView> getGroupDataViewByFieldID(int groupDataViewId) throws MdnException{
        // create query
        DataSource sysDs = DataManager.getSystemDS();
        Query q = new Query(new QueryCriterium(GroupDataView.ENT_GROUPDATAVIEW,
        		GroupDataView.FLD_DATAVIEWID, QueryCriterium.OP_EQUALS, new Integer(groupDataViewId)));
        RecordSet rs;
		try {
			rs = sysDs.select(q);
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getGroupDataViewByID", e);
		}
		Vector<GroupDataView> GroupDataViews = new Vector<GroupDataView>();
        // get the ds
        if(rs != null && rs.next())
        {
        	GroupDataView dobj = (GroupDataView)rs.getCurrentObject();
        	GroupDataViews.add(dobj);
        }
        
        return GroupDataViews;
	}
	public GroupDataView getGroupDataViewByGroupID(int groupId) throws MdnException{
        DataSource ds = DataManager.getSystemDS();

        Query q1 = new Query(GroupDataView.ENT_GROUPDATAVIEW);
        QueryCriterium qc1 = new QueryCriterium(GroupDataView.ENT_GROUPDATAVIEW, GroupDataView.FLD_GROUPID,
            QueryCriterium.OP_EQUALS, new Integer(groupId) );
        q1.addQueryCriterium(qc1);
        RecordSet rsGroup;
		try {
			rsGroup = ds.select(q1);
	        if (rsGroup.next())
	        {
	        	GroupDataView group = (GroupDataView)rsGroup.getCurrentObject();
	            return group;
	        }  			
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getGroup()", e);
		}   
        return null;
	}
	 public GroupDataView getGroupDataViewByID(int id) throws MdnException{
	        DataSource ds = DataManager.getSystemDS();

	        Query q1 = new Query(GroupDataView.ENT_GROUPDATAVIEW);
	        QueryCriterium qc1 = new QueryCriterium(GroupDataView.ENT_GROUPDATAVIEW, GroupDataView.FLD_ID,
	            QueryCriterium.OP_EQUALS, new Integer(id) );
	        q1.addQueryCriterium(qc1);
	        RecordSet rsGroup;
			try {
				rsGroup = ds.select(q1);
		        if (rsGroup.next())
		        {
		        	GroupDataView group = (GroupDataView)rsGroup.getCurrentObject();
		            return group;
		        }  			
			} catch (DataSourceException e) {
				e.printStackTrace();
				throw new MdnException("DataSourceException in getGroup()", e);
			}   
	        return null;
		 
	 }
	 
	 public GroupTablePermission getGroupTablePermissionByID(int id) throws MdnException {
        DataSource ds = DataManager.getSystemDS();

        Query q1 = new Query(GroupTablePermission.ENT_GROUP_ENTITY_PERMISSION);
        QueryCriterium qc1 = new QueryCriterium(GroupTablePermission.ENT_GROUP_ENTITY_PERMISSION, GroupTablePermission.FLD_ID,
            QueryCriterium.OP_EQUALS, new Integer(id) );
        q1.addQueryCriterium(qc1);
        RecordSet rsGroup;
		try {
			rsGroup = ds.select(q1);
	        if (rsGroup.next())
	        {
	        	GroupTablePermission groupTablePermission = (GroupTablePermission)rsGroup.getCurrentObject();
	            return groupTablePermission;
	        }  			
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getGroupTablePermissionByID()", e);
		}   
        return null;		 
	 }

	 public Vector<FieldExclusion> getFieldExclusion(int groupID) throws MdnException{
		DataSource ds = DataManager.getSystemDS();
        Query q2 = new Query(FieldExclusion.ENT_FIELDEXCLUSION);
        QueryCriterium qc1 = new QueryCriterium(FieldExclusion.ENT_FIELDEXCLUSION,
            FieldExclusion.FLD_GROUPID,
            QueryCriterium.OP_EQUALS,
            groupID );
        q2.addQueryCriterium(qc1);
        Vector<FieldExclusion> fieldExclusions = new Vector<FieldExclusion>();
        try {
			RecordSet rsFieldExclusion = ds.select(q2);
			while(rsFieldExclusion.next())
            {
				FieldExclusion dobj = (FieldExclusion)rsFieldExclusion.getCurrentObject();
				fieldExclusions.add(dobj);
            }
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getFieldExclusion()", e);
		}
		return fieldExclusions;
	}
	
	public int markGroupAsCleared(int groupID) throws MdnException{
		try {
			//get group by id
			Group group = getGroup(groupID);
			group.setDelStatus(Constants.MARKED_AS_NOT_DELETED);//delete status = 1
			group.save();
			return group.getId();
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in markGroupAsCleared", e);
		}
	}
	
	public int markGroupAsRecycled(int groupID) throws MdnException{
		try {
			//get group by id
			Group group = getGroup(groupID);
			group.setDelStatus(Constants.MARKED_AS_RECYCLED);//delete status = 1
			group.save();
			return group.getId();
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in markGroupAsDeleted", e);
		}
	}
	
	public User getUserByName(String username) throws MdnException {
        DataSource ds = DataManager.getDataSource(User.ENT_USER);

        // create the query for user on username
        Query query = new Query(new QueryCriterium(User.ENT_USER, User.FLD_NAME, QueryCriterium.OP_EQUALS, username));
        // get the results
        RecordSet rs;
		try {
			rs = ds.select(query);
	        // get the user out of the result set
	        if (rs.next())
	        {
	        	User user = (User) rs.getCurrentObject();
	        	return user;
	        }	
	        return null;
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getUserByName()", e);
		}
	}

	public Vector<User> getAdminUsers() throws MdnException {
        RecordSet  rs;
        DataSource ds = DataManager.getDataSource(User.ENT_USER);
        QueryCriterium q1 = new QueryCriterium(User.ENT_USER, User.FLD_PRIVILEGE, QueryCriterium.OP_EQUALS, "ADMIN");
        QueryCriterium q2 = new QueryCriterium(User.ENT_USER, User.FLD_DEL_STATUS, QueryCriterium.OP_EQUALS, "0");
        Query query = new Query(q1,q2);
        Vector<User> users = new Vector<User>();
        try {
			rs    = ds.select(query);
	        while (rs.next())
	        {
	            User user = (User) rs.getCurrentObject();
	            String userStatus = user.getDelStatus();
	            if(userStatus == null || userStatus.equals(Constants.MARKED_AS_NOT_DELETED))
	            	users.add(user);
	        }
        } catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getUsers()", e);
		}

        return users;
	}
	
	public User getUserByMobileNumber(String mobileNumber) throws MdnException{
		DataSource ds = DataManager.getDataSource(User.ENT_USER);

        // create the query for user on username
        //Query query = new Query(new QueryCriterium(User.ENT_USER, User.FLD_MOBILE, QueryCriterium.OP_EQUALS, mobileNumber));
        
        QueryCriterium q1 = new QueryCriterium(User.ENT_USER, User.FLD_MOBILE, QueryCriterium.OP_EQUALS, mobileNumber);
        QueryCriterium q2 = new QueryCriterium(User.ENT_USER, User.FLD_DEL_STATUS, QueryCriterium.OP_EQUALS, "0");
        Query query = new Query(q1,q2);
        
        // get the results
        RecordSet rs;
		try {
			rs = ds.select(query);
	        // get the user out of the result set
	        if (rs.next())
	        {
	        	User user = (User) rs.getCurrentObject();
	        	return user;
	        }	
	        return null;
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getUserByMobileNumber()", e);
		}
	}

	public UserWrapper getLoginUser(String username, String password) throws MdnException{
		return SecurityManager.login(username, password);
	}

	public UserWrapper getLoginUser(String username) throws MdnException{
		return SecurityManager.getLoginUserByName(username);
	}	
	
	public int isLicenseValid (){
		int rvLicense = LicenseManager.isLicenseValid ();
		return rvLicense;
	}
	
	public User getUser(int userID, boolean includeGroups) throws MdnException{
        // perform a query to find the user
        // get the data source for querying
        DataSource ds = DataManager.getDataSource(User.ENT_USER);

        // create the query for user on username
        Query query = new Query(new QueryCriterium(User.ENT_USER, User.FLD_ID,
            QueryCriterium.OP_EQUALS, userID));
        // get the results
        RecordSet rs;
		try {
			rs = ds.select(query);
	        // get the user out of the result set
	        if (rs.next())
	        {
	        	User user = (User) rs.getCurrentObject();
	        	return user;
	        }	
	        return null;
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getUser()", e);
		}
	}
	
	/*
	 * Get users list from DB when users status deleted are not recycled status. 
	 */
	public Vector<User> getUsers() throws MdnException {
        DataSource ds;
        Query      query;
        RecordSet  rs;
		ds    = DataManager.getDataSource(User.ENT_USER);
		query = new Query(User.ENT_USER);
//        query = new Query(new QueryCriterium(User.ENT_USER, User.FLD_PROJECT_ID,
//                QueryCriterium.OP_EQUALS, projectId));
        Vector<User> users = new Vector<User>();
        try {
			rs    = ds.select(query);
	        while (rs.next())
	        {
	            User user = (User) rs.getCurrentObject();
	            String userStatus = user.getDelStatus();
	            if(userStatus == null || userStatus.equals(Constants.MARKED_AS_NOT_DELETED))
	            	users.add(user);
	        }
        } catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getUsers()", e);
		}

        return users;
	}

	public int getExistingNumberOfUsers() throws MdnException {
        DataSource ds;
        Query      query;
        RecordSet  rs;
		ds    = DataManager.getDataSource(User.ENT_USER);
		query = new Query(User.ENT_USER);
//        query = new Query(new QueryCriterium(User.ENT_USER, User.FLD_PROJECT_ID,
//                QueryCriterium.OP_EQUALS, projectId));
        Vector<User> users = new Vector<User>();
        try {
			rs    = ds.select(query);
	        while (rs.next())
	        {
	            User user = (User) rs.getCurrentObject();
	            String userStatus = user.getDelStatus();
	            if(userStatus == null || userStatus.equals(Constants.MARKED_AS_NOT_DELETED))
	            	users.add(user);
	        }
        } catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getUsers()", e);
		}

        return users.size();
	}	
	
	public Vector<User> getUsersByGroupId(int groupId) throws MdnException {
		DataSource ds = DataManager.getDataSource(User.ENT_USER);
        Query query = new Query(new QueryCriterium(User.ENT_USER, User.FLD_GROUP_ID,
                QueryCriterium.OP_EQUALS, groupId));
        Vector<User> users = new Vector<User>();
        RecordSet  rs;
        try {
			rs    = ds.select(query);
	        while (rs.next())
	        {
	            User user = (User) rs.getCurrentObject();
	            String userStatus = user.getDelStatus();
	            if(userStatus.equals(Constants.MARKED_AS_NOT_DELETED))
	            	users.add(user);
	        }
        } catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getUsersByGroupId", e);
		}

        return users;
	}

	public User getUserByEmailAddress(String emailAddress) throws MdnException{
		DataSource ds = DataManager.getDataSource(User.ENT_USER);
        //Query query = new Query(new QueryCriterium(User.ENT_USER, User.FLD_EMAIL, QueryCriterium.OP_EQUALS, emailAddress));

        QueryCriterium q1 = new QueryCriterium(User.ENT_USER, User.FLD_EMAIL, QueryCriterium.OP_EQUALS, emailAddress);
        QueryCriterium q2 = new QueryCriterium(User.ENT_USER, User.FLD_DEL_STATUS, QueryCriterium.OP_EQUALS, "0");
        Query query = new Query(q1,q2);
        
        User user = new User();
        RecordSet  rs;
        try {
			rs    = ds.select(query);
	        while (rs.next())
	        {
	            user = (User) rs.getCurrentObject();
	            String userStatus = user.getDelStatus();
	            if(userStatus.equals(Constants.MARKED_AS_NOT_DELETED))
	            	return user;
	        }
        } catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getUsersByGroupId", e);
		}

        return null;
		
	}
	public int saveUser(User user) throws MdnException {
		try {
			user.setDelStatus(Constants.MARKED_AS_NOT_DELETED);
			user.save();
			return user.getId();
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in saveUser", e);
		}
	}
	
	public int editUser(User user){
		return 0;
	}
	
	public int markUserAsCleared(int userID) throws MdnException{
		try {
			//get user by id
			User user = getUser(userID, true);
			user.setDelStatus(Constants.MARKED_AS_NOT_DELETED);//delete status = 1
			user.save();

			return user.getId();
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in markUserAsCleared", e);
		}
	}

	/*
	 * Marks the user in the DB as being recycled, i.e., the user has been put into
	 * the recycle bin. Use the flag as defined on
	 * <code>Constants.MARKED_AS_RECYCLED</code> that is 1.
	 * @param userId
	 * @return int - UserId
	 */
	public int markUserAsRecycled(int userID) throws MdnException{
		try {
			//get user by id
			User user = getUser(userID, true);
			user.setDelStatus(Constants.MARKED_AS_RECYCLED);//delete status = 1
			user.save();
			
			Vector<IMContact> imContacts = getImContactsByUserID(userID);
			for(IMContact imc : imContacts){
				imc.delete();
			}

			return user.getId();
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in markUserAsRecycled", e);
		}
	}

	/*
	 * Delete user from recycle bin.
	 * @param userId
	 * @return int - successful delete an user if return value is 1.
	 */
	public int deleteUser(int userID) throws MdnException{
        // must have an id
        if(userID < 0)
            return -1;

        try {
        	/* Get user by id */
			User user = getUser(userID, true);
			/* Delete user */
			user.delete();
			return 1;
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in markUserAsDeleted", e);
		}
	}

	public int deleteGroup(int groupID) throws MdnException{
        // must have an id
        if(groupID < 0)
            return -1;

		try {
			//get group by id
			Group group = getGroup(groupID);
			group.delete();
			return 1;
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in deleteGroup", e);
		}		
		
	}	
	public Vector<RecycleBinItem> getProjectRecycleBinContent() throws MdnException{
	  	Vector<RecycleBinItem> bin = new Vector<RecycleBinItem>();
	  	RecycleBinItem binItem = null;
	  	
        RecordSet  rs;
        DataSource ds = DataManager.getSystemDS();
        Query q = new Query(ProjectDobj.ENT_PROJECT);
        QueryCriterium qc2 = new QueryCriterium(ProjectDobj.ENT_PROJECT,
        		ProjectDobj.FLD_DEL_STATUS, QueryCriterium.OP_EQUALS,
                Constants.MARKED_AS_RECYCLED);
        q.addQueryCriterium(qc2); 
        
        try {
			rs    = ds.select(q);
	        while (rs.next())
	        {
	        	ProjectDobj project = (ProjectDobj) rs.getCurrentObject();
	            String userStatus = project.getDelStatus();
	            if(userStatus.equals(Constants.MARKED_AS_RECYCLED)){
					binItem = new RecycleBinItem(project.getId(), "project", project.getName(), project.getId());
					bin.add(binItem);
	            }
	        }
        } catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getUsers()", e);
		}
        
		return bin;				
	}	
	public Vector<RecycleBinItem> getSettingsRecycleBinContent() throws MdnException{
	  	Vector<RecycleBinItem> bin = new Vector<RecycleBinItem>();
	  	RecycleBinItem binItem = null;
	  	DataSource ds = DataManager.getSystemDS();
        
        RecordSet  rs;
        Query q = new Query(LanguageDobj.ENT_LANGUAGE);
        QueryCriterium qc2 = new QueryCriterium(LanguageDobj.ENT_LANGUAGE,
        		LanguageDobj.FLD_DEL_STATUS, QueryCriterium.OP_EQUALS,
                Constants.MARKED_AS_RECYCLED_INT);
        q.addQueryCriterium(qc2); 
        
        try {
			rs    = ds.select(q);
	        while (rs.next())
	        {
	        	LanguageDobj lang = (LanguageDobj) rs.getCurrentObject();
	            int status = lang.getDelStatus();
	            if(status == Constants.MARKED_AS_RECYCLED_INT){
					binItem = new RecycleBinItem(lang.getId(), "language", lang.getName(), -1);
					bin.add(binItem);
	            }
	        }
        } catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getSettingsRecycleBinContent()", e);
		}

        q = new Query(JdbcDriver.ENT_JDBCDRIVER);
        qc2 = new QueryCriterium(JdbcDriver.ENT_JDBCDRIVER,
        		JdbcDriver.FLD_DEL_STATUS, QueryCriterium.OP_EQUALS,
                Constants.MARKED_AS_RECYCLED_INT);
        q.addQueryCriterium(qc2); 
        
        try {
			rs    = ds.select(q);
	        while (rs.next())
	        {
	        	JdbcDriver driver = (JdbcDriver) rs.getCurrentObject();
	            int status = driver.getDelStatus();
	            if(status == Constants.MARKED_AS_RECYCLED_INT){
					binItem = new RecycleBinItem(driver.getId(), "driver", driver.getName(), -1);
					bin.add(binItem);
	            }
	        }
        } catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getSettingsRecycleBinContent()", e);
		}        
        
        q = new Query(IMConnection.ENT_IM_CONNECTION);
        qc2 = new QueryCriterium(IMConnection.ENT_IM_CONNECTION,
        		IMConnection.FLD_DEL_STATUS, QueryCriterium.OP_EQUALS,
                Constants.MARKED_AS_RECYCLED_INT);
        q.addQueryCriterium(qc2); 
        
        try {
			rs    = ds.select(q);
	        while (rs.next())
	        {
	        	IMConnection imConn = (IMConnection) rs.getCurrentObject();
	            int status = imConn.getDelStatus();
	            if(status == Constants.MARKED_AS_RECYCLED_INT){
					binItem = new RecycleBinItem(imConn.getId(), "im-conn", imConn.getName() + "-"+MessagingUtils.getConnectionTypeNameByID(imConn.getType()), -1);
					bin.add(binItem);
	            }
	        }
        } catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getSettingsRecycleBinContent()", e);
		}        
        
        //EMAIL SETT 
        q = new Query(MdnEmailSetting.ENT_EMAIL);
        qc2 = new QueryCriterium(MdnEmailSetting.ENT_EMAIL,
        		MdnEmailSetting.FLD_DEL_STATUS, QueryCriterium.OP_EQUALS,
                Constants.MARKED_AS_RECYCLED_INT);
        q.addQueryCriterium(qc2); 
        
        try {
			rs    = ds.select(q);
	        while (rs.next())
	        {
	        	MdnEmailSetting emailSett = (MdnEmailSetting) rs.getCurrentObject();
	            int status = emailSett.getDelStatus();
	            if(status == Constants.MARKED_AS_RECYCLED_INT){
					binItem = new RecycleBinItem(emailSett.getId(), "edit_mdn_email", emailSett.getEmailAddress(), -1);
					bin.add(binItem);
	            }
	        }
        } catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getSettingsRecycleBinContent()", e);
		}                
        //smpp sett
        q = new Query(MdnSmpp.ENT_SMPP);
        qc2 = new QueryCriterium(MdnSmpp.ENT_SMPP,
        		MdnSmpp.FLD_DEL_STATUS, QueryCriterium.OP_EQUALS,
                Constants.MARKED_AS_RECYCLED_INT);
        q.addQueryCriterium(qc2); 
        
        try {
			rs    = ds.select(q);
	        while (rs.next())
	        {
	        	MdnSmpp smpp = (MdnSmpp) rs.getCurrentObject();
	            int status = smpp.getDelStatus();
	            if(status == Constants.MARKED_AS_RECYCLED_INT){
					binItem = new RecycleBinItem(smpp.getId(), "smpp-sett", "SMPP " + smpp.getNumber(), -1);
					bin.add(binItem);
	            }
	        }
        } catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getSettingsRecycleBinContent()", e);
		}                        
        
		return bin;
	}		
	public Vector<RecycleBinItem> getUsersGroupsRecycleBinContent(int projectId) throws MdnException{
	  	Vector<RecycleBinItem> bin = new Vector<RecycleBinItem>();
	  	RecycleBinItem binItem = null;
	  	
        RecordSet  rs;
        DataSource ds = DataManager.getSystemDS();
        Query q = new Query(User.ENT_USER);
        /*QueryCriterium qc = new QueryCriterium(User.ENT_USER, User.FLD_PROJECT_ID,
                QueryCriterium.OP_EQUALS, projectId);
        qc.setOrIsNull(true);
        q.addQueryCriterium(qc);
        */
        QueryCriterium qc2 = new QueryCriterium(User.ENT_USER,
        		User.FLD_DEL_STATUS, QueryCriterium.OP_EQUALS,
                Constants.MARKED_AS_RECYCLED);
        q.addQueryCriterium(qc2); 
        
        try {
			rs    = ds.select(q);
	        while (rs.next())
	        {
	            User user = (User) rs.getCurrentObject();
	            String userStatus = user.getDelStatus();
	            if(userStatus.equals(Constants.MARKED_AS_RECYCLED)){
					binItem = new RecycleBinItem(user.getId(), "user", user.getFirstName()+" "+ user.getLastName(), projectId);
					bin.add(binItem);
	            }
	        }
        } catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getUsers()", e);
		}
	  	

	    // Add all groups that are marked as being recycled.
        q = new Query(Group.ENT_GROUP);
        QueryCriterium qc = new QueryCriterium(Group.ENT_GROUP, Group.FLD_PROJECT_ID,
                QueryCriterium.OP_EQUALS, projectId );
        qc.setOrIsNull(true);
        q.addQueryCriterium(qc);
        
        qc2 = new QueryCriterium(Group.ENT_GROUP,
        		Group.FLD_STATUS_DEL, QueryCriterium.OP_EQUALS,
                Constants.MARKED_AS_RECYCLED);
        q.addQueryCriterium(qc2);
        
        RecordSet rsGroup;
        try {
			rsGroup = ds.select(q);
	        while (rsGroup.next())
	        {
	            Group group = (Group)rsGroup.getCurrentObject();
	            if(group.getDelStatus()!= null && group.getDelStatus().equals(Constants.MARKED_AS_RECYCLED)){
	            	binItem = new RecycleBinItem(group.getIntId(), "group", group.getName(), projectId);
					bin.add(binItem);
	            }
					
	        }  			
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getGroups()", e);
		}
        
		return bin;		
		
	}

	public Vector<RecycleBinItem> getDBRecycleBinContent(int projectId) throws MdnException{
	  	Vector<RecycleBinItem> bin = new Vector<RecycleBinItem>();
	  	RecycleBinItem binItem = null;
	  	
        RecordSet  rs;
        DataSource ds = DataManager.getSystemDS();
        Query q = new Query(DataSourceDobj.ENT_DATASOURCE);
        QueryCriterium qc = new QueryCriterium(DataSourceDobj.ENT_DATASOURCE, DataSourceDobj.FLD_PROJECT_ID,
                QueryCriterium.OP_EQUALS, projectId);
        qc.setOrIsNull(true);
        q.addQueryCriterium(qc);
        
        QueryCriterium qc2 = new QueryCriterium(DataSourceDobj.ENT_DATASOURCE,
        		DataSourceDobj.FLD_DEL_STATUS, QueryCriterium.OP_EQUALS,
                Constants.MARKED_AS_RECYCLED);
        q.addQueryCriterium(qc2); 
        
        try {
			rs    = ds.select(q);
	        while (rs.next())
	        {
	        	DataSourceDobj dataSourceDobj = (DataSourceDobj) rs.getCurrentObject();
	            int status = dataSourceDobj.getDelStatus();
	            if(status == Constants.MARKED_AS_RECYCLED_INT){
					binItem = new RecycleBinItem(dataSourceDobj.getId(), "datasource", dataSourceDobj.getName(), projectId);
					bin.add(binItem);
		        }
	        }


	        q = new Query(DataSourceDobj.ENT_DATASOURCE);
	        qc = new QueryCriterium(DataSourceDobj.ENT_DATASOURCE, DataSourceDobj.FLD_PROJECT_ID,
	                QueryCriterium.OP_EQUALS, projectId);
	        qc.setOrIsNull(true);
	        q.addQueryCriterium(qc);
	        
	        
			rs    = ds.select(q);
	        while (rs.next())
	        {
	        	DataSourceDobj dataSourceDobj = (DataSourceDobj) rs.getCurrentObject();
	    	    // Add all groups that are marked as being recycled.
	            q = new Query(DataView.ENT_DATAVIEW);
	            qc = new QueryCriterium(DataView.ENT_DATAVIEW, DataView.FLD_SOURCE_DSID,
	                    QueryCriterium.OP_EQUALS, dataSourceDobj.getId() );
	            qc.setOrIsNull(true);
	            q.addQueryCriterium(qc);
	            
	            qc2 = new QueryCriterium(DataView.ENT_DATAVIEW,
	            		DataView.FLD_DEL_STATUS, QueryCriterium.OP_EQUALS,
	                    Constants.MARKED_AS_RECYCLED);
	            q.addQueryCriterium(qc2);
	            
	            RecordSet rsDataView;
	            
    			rsDataView = ds.select(q);
    	        while (rsDataView.next())
    	        {
    	        	DataView dataView = (DataView)rsDataView.getCurrentObject();
    	            if(dataView.getDelStatus() == Constants.MARKED_AS_RECYCLED_INT ){
    	            	binItem = new RecycleBinItem(dataView.getId(), "dataView", dataView.getName(), projectId);
    					bin.add(binItem);
    	            }
    					
    	        }  			

	        }            
	            
	    } catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getDataSource()", e);
		}
        
		return bin;		
		
	}	
	
	public List<DataView> getAllMetaViews(int dbConnectionID, boolean includeFields) throws MdnException
	{
		DataSource ds = DataManager.getSystemDS();
		Query q = new Query(DataView.ENT_DATAVIEW);
		QueryCriterium qc2 = new QueryCriterium(DataView.ENT_DATAVIEW, DataView.FLD_SOURCE_DSID,
									QueryCriterium.OP_EQUALS, new Integer(dbConnectionID) );
		q.addQueryCriterium(qc2);
		QueryCriterium qc1 = new QueryCriterium(DataView.ENT_DATAVIEW, DataView.FLD_DEL_STATUS,
				QueryCriterium.OP_EQUALS, Constants.MARKED_AS_NOT_DELETED_INT );
		q.addQueryCriterium(qc1);
		try {
			RecordSet rsDataView = ds.select(q);
			List<DataView> list = new Vector<DataView>();
			while (rsDataView.next()){
				DataView object = (DataView)rsDataView.getCurrentObject();					
				list.add(object);
	        }
			return list;			
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getAllMetaViews", e);
		}  
	}

	public DataView getMetaViewByID(int viewID, boolean includeFields) throws MdnException
	{
		Query q = new Query(DataView.ENT_DATAVIEW);
        q.addQueryCriterium(new QueryCriterium(DataView.ENT_DATAVIEW,
            DataView.FLD_ID, QueryCriterium.OP_EQUALS,
            new Integer(viewID)));
        RecordSet rs;
		try {
			rs = DataManager.getSystemDS().select(q);
	        if(rs != null && rs.next())
	            return (DataView)rs.getCurrentObject();
	        else
	        	return null;
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getMetaViewByID", e);
		}
	}
	
	public List<QueryDobj> getQueriesByID(int id) throws MdnException
	{
        // build query
        DataSource ds = DataManager.getSystemDS();
        Query q = new Query(QueryDobj.ENT_QUERY);
        q.addQueryCriterium(new QueryCriterium(QueryDobj.ENT_QUERY,
            QueryDobj.FLD_PARENTID, QueryCriterium.OP_EQUALS,
            new Integer(id)));
        RecordSet rs;
		try {
			rs = ds.select(q);
			QueryDobj qdobj;
			List<QueryDobj>     queries        = new Vector<QueryDobj>();
			while(rs != null && rs.next())
            {
                // get the query
                qdobj = (QueryDobj)rs.getCurrentObject();
                if(qdobj != null)
                	queries.add(qdobj);
            }
			return queries;
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getQueriesByViewID", e);
		}
	}

	public QueryDobj getQueryByID(int queryID) throws MdnException
	{
        // build query
        DataSource ds = DataManager.getSystemDS();
        Query q = new Query(QueryDobj.ENT_QUERY);
        q.addQueryCriterium(new QueryCriterium(QueryDobj.ENT_QUERY,
            QueryDobj.FLD_ID, QueryCriterium.OP_EQUALS,
            new Integer(queryID)));
        RecordSet rs;
		try {
			rs = ds.select(q);
			QueryDobj qdobj;
			if(rs != null && rs.next())
            {
                // get the query
                qdobj = (QueryDobj)rs.getCurrentObject();
                if(qdobj != null)
                	return qdobj;
            }
			return null;
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getQueriesByViewID", e);
		}
	}	

	public List<QueryDobj> getAllQueries(int projectId) throws MdnException {
		
	    DataSource ds    = DataManager.getDataSource(QueryDobj.ENT_QUERY);
	    Query q = new Query(QueryDobj.ENT_QUERY);

        QueryCriterium qc1 = new QueryCriterium(QueryDobj.ENT_QUERY,
        		QueryDobj.FLD_PROJECT_ID, QueryCriterium.OP_EQUALS,
                projectId);
        q.addQueryCriterium(qc1);            
	    
	    List<QueryDobj> queries = new Vector<QueryDobj>();
	    try {
	    	RecordSet rs    = ds.select(q);
		    while (rs.next())
		    {
		    	QueryDobj queryDobj = (QueryDobj) rs.getCurrentObject();
		    	if(queryDobj.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT)
		    		queries.add(queryDobj);
		    }
	    } catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getAllQueries()", e);
		}
	    return queries;
	}
	
	public List<QueryDobj> getAllQueries() throws MdnException {
		
	    DataSource ds    = DataManager.getDataSource(QueryDobj.ENT_QUERY);
	    Query q = new Query(QueryDobj.ENT_QUERY);
	    
	    List<QueryDobj> queries = new Vector<QueryDobj>();
	    try {
	    	RecordSet rs    = ds.select(q);
		    while (rs.next())
		    {
		    	QueryDobj queryDobj = (QueryDobj) rs.getCurrentObject();
		    	if(queryDobj.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT)
		    		queries.add(queryDobj);
		    }
	    } catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getAllQueries()", e);
		}
	    return queries;
	}	
	public List<QueryCriteriaDobj> getQueryCriteriaByQueryID(int queryID, String objectType) throws MdnException
	{
        // build query
        DataSource ds = DataManager.getSystemDS();
//        Query q1 = new Query(QueryCriteriaDobj.ENT_QUERY_CRITERIA);
//        q1.addQueryCriterium(new QueryCriterium(QueryCriteriaDobj.ENT_QUERY_CRITERIA,
//            QueryCriteriaDobj.FLD_QUERY_ID, QueryCriterium.OP_EQUALS,
//            new Integer(queryID)));

        Query q;
        QueryCriterium q1 =new QueryCriterium(QueryCriteriaDobj.ENT_QUERY_CRITERIA, QueryCriteriaDobj.FLD_QUERY_ID, QueryCriterium.OP_EQUALS, new Integer(queryID)); 
        QueryCriterium q2;
        if(objectType!= null && !objectType.equals("")){
            q2 = new QueryCriterium(QueryCriteriaDobj.ENT_QUERY_CRITERIA, QueryCriteriaDobj.FLD_OBJECT_TYPE, QueryCriterium.OP_EQUALS, objectType);
            q = new Query(q1,q2);
        }else
        	q = new Query(q1);
        
        RecordSet rs;
		try {
			rs = ds.select(q);
			List<QueryCriteriaDobj>     queryCriteria        = new Vector<QueryCriteriaDobj>();
			while(rs != null && rs.next())
            {
                // get the query
				QueryCriteriaDobj qdobj = (QueryCriteriaDobj)rs.getCurrentObject();
                if(qdobj != null)
                	queryCriteria.add(qdobj);
            }
			return queryCriteria;
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getQueryCriteriaByQueryID", e);
		}
	}		

	public QueryCriteriaDobj getQueryCriteriaByID(int id) throws MdnException
	{
        // build query
        DataSource ds = DataManager.getSystemDS();
        Query q = new Query(QueryCriteriaDobj.ENT_QUERY_CRITERIA);
        q.addQueryCriterium(new QueryCriterium(QueryCriteriaDobj.ENT_QUERY_CRITERIA,
            QueryCriteriaDobj.FLD_ID, QueryCriterium.OP_EQUALS,
            new Integer(id)));
        RecordSet rs;
		try {
			rs = ds.select(q);
			if(rs != null && rs.next())
            {
				// get the query
				QueryCriteriaDobj qdobj = (QueryCriteriaDobj)rs.getCurrentObject();
				if (qdobj != null)
					return qdobj;
            }
			return null;
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getQueryCriteriaByID", e);
		}
	}	
	
	public List<ProjectDobj> getNavProjects() throws MdnException{
	    /*Vector<String> projects = new Vector<String>();
	    projects.add("ACT!");
	    projects.add("Sample 1");
	    projects.add("Sample 2");
	    projects.add("Sample 3");
	    projects.add("Weather");
	    */
	    DataSource ds    = DataManager.getDataSource(ProjectDobj.ENT_PROJECT);
	    Query query = new Query(ProjectDobj.ENT_PROJECT);
	    
        QueryCriterium qc2 = new QueryCriterium(ProjectDobj.ENT_PROJECT,
        		ProjectDobj.FLD_DEL_STATUS, QueryCriterium.OP_EQUALS,
                Constants.MARKED_AS_NOT_DELETED);
        query.addQueryCriterium(qc2);
        
	    List<ProjectDobj> projects = new Vector<ProjectDobj>();
	    try {
	    	RecordSet rs    = ds.select(query);
		    while (rs.next())
		    {
		    	ProjectDobj projectDobj = (ProjectDobj) rs.getCurrentObject();
		    	projects.add(projectDobj);
		    }
	    } catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getAllQueries()", e);
		}   
	    return projects;
	}	
	public ProjectDobj getDefaultProject() throws MdnException{

	    DataSource ds    = DataManager.getDataSource(ProjectDobj.ENT_PROJECT);
	    Query query = new Query(ProjectDobj.ENT_PROJECT);
        QueryCriterium qc2 = new QueryCriterium(ProjectDobj.ENT_PROJECT,
        		ProjectDobj.FLD_DEL_STATUS, QueryCriterium.OP_EQUALS,
                Constants.MARKED_AS_NOT_DELETED);
        query.addQueryCriterium(qc2);
	    try {
	    	RecordSet rs    = ds.select(query);
		    int smallestId = 999999999;
		    ProjectDobj retProjectDobj = null;
		    while (rs.next())
		    {
		    	ProjectDobj projectDobj = (ProjectDobj) rs.getCurrentObject();
		    	if (projectDobj.getId() <= smallestId){
		    		smallestId = projectDobj.getId();
		    		retProjectDobj = projectDobj;
		    	}		    	
		    }
	    	return retProjectDobj;
	    } catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getAllQueries()", e);
		}   
	}	  
	public ProjectDobj getNavProjectById(int id) throws MdnException{

	    DataSource ds    = DataManager.getDataSource(ProjectDobj.ENT_PROJECT);
	    Query query = new Query(ProjectDobj.ENT_PROJECT);
	    query.addQueryCriterium(new QueryCriterium(ProjectDobj.ENT_PROJECT,
	    		ProjectDobj.FLD_ID, QueryCriterium.OP_EQUALS,
	            new Integer(id)));
	    try {
	    	RecordSet rs    = ds.select(query);
		    if (rs.next())
		    {
		    	ProjectDobj projectDobj = (ProjectDobj) rs.getCurrentObject();
		    	return projectDobj;
		    }
	    } catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getAllQueries()", e);
		}   
	    return null;
	}	  

	public ProjectDobj getNavProjectByName(String name) throws MdnException{

	    DataSource ds    = DataManager.getDataSource(ProjectDobj.ENT_PROJECT);
	    Query query = new Query(ProjectDobj.ENT_PROJECT);
	    query.addQueryCriterium(new QueryCriterium(ProjectDobj.ENT_PROJECT,
	    		ProjectDobj.FLD_NAME, QueryCriterium.OP_EQUALS,
                name));
	    try {
	    	RecordSet rs    = ds.select(query);
		    if (rs.next())
		    {
		    	ProjectDobj projectDobj = (ProjectDobj) rs.getCurrentObject();
		    	return projectDobj;
		    }
	    } catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getAllQueries()", e);
		}   
	    return null;
	}		  
	  
	  public DataViewField getViewField(int fieldID) throws MdnException
	  {
          DataSource ds = DataManager.getSystemDS();

          Query q = new Query(DataViewField.ENT_DVFIELD);
          QueryCriterium qc = new QueryCriterium(DataViewField.ENT_DVFIELD,
              DataViewField.FLD_ID,
              QueryCriterium.OP_EQUALS,
              new Integer(fieldID));
          q.addQueryCriterium(qc);
          try {
			RecordSet rsDataViewField = ds.select(q);
			if (rsDataViewField != null && rsDataViewField.next())
			{
				DataViewField dataViewField = (DataViewField)rsDataViewField.getCurrentObject();
				return dataViewField;
			}
			return null;
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getViewField", e);
		}		  
	  }
	  
	  public List<JoinDobj> getJoins(int connectionID) throws MdnException
	  {
			// build query
			DataSource ds = DataManager.getSystemDS ();
			Query q = new Query (JoinDobj.ENT_JOIN);
			q.addQueryCriterium (new QueryCriterium (JoinDobj.ENT_JOIN,
				JoinDobj.FLD_DSID, QueryCriterium.OP_EQUALS,
				new Integer (connectionID)));
			// execute query and build vector
			JoinDobj j;
			RecordSet rs;
			List<JoinDobj> joins = new Vector<JoinDobj>();
			try {
				rs = ds.select (q);
				while (rs != null && rs.next ())
				{
					// get the join
					j = (JoinDobj)rs.getCurrentObject ();
					if (j != null)
						joins.add (j);
				}				
			} catch (DataSourceException e) {
				e.printStackTrace();
				throw new MdnException("DataSourceException in getJoins", e);
			}			
			return joins;
	  }

	  public int createIMConnection(IMConnection imConnection) throws MdnException{
		  try {
			imConnection.setState(0);  //new
			imConnection.save();
			return imConnection.getId();
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in createIMConnection", e);
		}
	  }
		
	public Vector<IMConnection> getAllIMConnections() throws MdnException {
	    DataSource ds    = DataManager.getDataSource(IMConnection.ENT_IM_CONNECTION);
	    Query query = new Query(IMConnection.ENT_IM_CONNECTION);

	    Vector<IMConnection> connections = new Vector<IMConnection>();
	    try {
	    	RecordSet rs    = ds.select(query);
		    while (rs.next())
		    {
		    	IMConnection connection = (IMConnection) rs.getCurrentObject();
		    	if(connection.getDelStatus() ==  Constants.MARKED_AS_NOT_DELETED_INT)
		    		connections.add(connection);
		    }
	    } catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getAllIMConnections()", e);
		}
	    return connections;
	}

	public Vector<Feature> getAllPrivileges() throws MdnException {
	    DataSource ds    = DataManager.getDataSource(Feature.ENT_FEATURE);
	    Query query = new Query(new QueryCriterium(Feature.ENT_FEATURE, Feature.FLD_TYPE, QueryCriterium.OP_EQUALS, "PRIVILEGE"));

	    Vector<Feature> privileges = new Vector<Feature>();
	    try {
	    	RecordSet rs    = ds.select(query);
		    while (rs.next())
		    {
		    	Feature privilege = (Feature) rs.getCurrentObject();
		        privileges.add(privilege);
		    }
	    } catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getAllPrivileges()", e);
		}
	    return privileges;
	}	
	public Vector<Feature> getAllLicenseTypes() throws MdnException {
	    DataSource ds    = DataManager.getDataSource(Feature.ENT_FEATURE);
	    Query query = new Query(new QueryCriterium(Feature.ENT_FEATURE, Feature.FLD_TYPE, QueryCriterium.OP_EQUALS, "LICENSE"));

	    Vector<Feature> licenses = new Vector<Feature>();
	    try {
	    	RecordSet rs    = ds.select(query);
		    while (rs.next())
		    {
		    	Feature license = (Feature) rs.getCurrentObject();
		        licenses.add(license);
		    }
	    } catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getAllLicenseTypes()", e);
		}
	    return licenses;
	}	
	public boolean isDuplicateIMConnection(int type) throws MdnException{
		IMConnection imConn = null;
		DataSource ds = DataManager.getDataSource(IMConnection.ENT_IM_CONNECTION);
        Query query = new Query(new QueryCriterium(IMConnection.ENT_IM_CONNECTION, IMConnection.FLD_TYPE, QueryCriterium.OP_EQUALS, new Integer(type)));

        // get the results
        RecordSet rs;
		try {
			rs = ds.select(query);
	        if (rs.next())
	        {
	        	imConn = (IMConnection) rs.getCurrentObject();
	        }	
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getUserByName()", e);
		}
		
		if(imConn != null && imConn.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT)
			return true;//Is duplicate
		else
			return false;//Is not duplicate
        }
	
	public IMConnection getImConnectionByID(int connectionID) throws MdnException{
        // create query
        DataSource sysDs = DataManager.getSystemDS();
        Query q = new Query(new QueryCriterium(IMConnection.ENT_IM_CONNECTION,
        		IMConnection.FLD_ID, QueryCriterium.OP_EQUALS, new Integer(connectionID)));
        RecordSet rs;
		try {
			rs = sysDs.select(q);
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getImConnectionByID", e);
		}
		IMConnection imConn = null;
        // get the ds
        if(rs != null && rs.next())
            imConn = (IMConnection)rs.getCurrentObject();
        
        return imConn;
	}

	public IMConnection getImConnectionByTypeID(int type) throws MdnException{
        // create query
        DataSource sysDs = DataManager.getSystemDS();
        Query q = new Query(new QueryCriterium(IMConnection.ENT_IM_CONNECTION,
        		IMConnection.FLD_TYPE, QueryCriterium.OP_EQUALS, new Integer(type)));
        RecordSet rs;
		try {
			rs = sysDs.select(q);
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getImConnectionByTypeID", e);
		}
		IMConnection imConn = null;
        // get the ds
        if(rs != null && rs.next())
            imConn = (IMConnection)rs.getCurrentObject();
        
        return imConn;
	}

	public void changedStatus(IMConnection imConnection) throws MdnException{
		try {
			//int status = imConnection.getStatus();
			imConnection.setState (DataObject.IN_DB);
//			if(status == Integer.parseInt(Constants.CONNECT_STATUS))
//			{
//				imConnection.setStatus(Integer.parseInt(Constants.DISCONNECT_STATUS));
//			}else if(status == Integer.parseInt(Constants.DISCONNECT_STATUS)) {
//				imConnection.setStatus(Integer.parseInt(Constants.CONNECT_STATUS));
//			}
			imConnection.setStatusDesc(new Date());
			imConnection.save();
		} catch (DataSourceException e) {
			e.printStackTrace();
		}
	}
	
	public int deleteIMConnection(int imID) throws MdnException{
        // must have an id
        if(imID < 0)
            return -1;

        // select IM Connection
        DataSource sysDs = DataManager.getSystemDS();
        Query q = new Query(IMConnection.ENT_IM_CONNECTION);
        q.addQueryCriterium(new QueryCriterium(IMConnection.ENT_IM_CONNECTION,
            MenuAction.FLD_ID, QueryCriterium.OP_EQUALS,
            new Integer(imID)));
        RecordSet rs;
		try {
			rs = sysDs.select(q);
			// delete IM connection
			while(rs.next())
				rs.getCurrentObject().delete();
			return 1;
		} catch (DataSourceException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public int removeIMContact(int contactID) throws MdnException{
        // must have an id
        if(contactID < 0)
            return -1;

        // select IM Connection
        DataSource sysDs = DataManager.getSystemDS();
        Query q = new Query(IMContact.ENT_IM_CONTACT);
        q.addQueryCriterium(new QueryCriterium(IMContact.ENT_IM_CONTACT,
            MenuAction.FLD_ID, QueryCriterium.OP_EQUALS,
            new Integer(contactID)));
        RecordSet rs;
		try {
			rs = sysDs.select(q);
			// delete IM contact
			while(rs.next())
				rs.getCurrentObject().delete();
			return 1;
		} catch (DataSourceException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public int saveRecievedIMMessage(IMMessage imMessage) throws MdnException{
		  try {
			  imMessage.setState(0);  //new
			  imMessage.save();
			  return imMessage.getId();
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in save an IMMessage in DB", e);
		}
	}

	public Vector<IMContact> getImContactsByUserID(int userID) throws MdnException{
        // create query
        DataSource sysDs = DataManager.getSystemDS();
        Query q = new Query(new QueryCriterium(IMContact.ENT_IM_CONTACT,
        		IMContact.FLD_MDN_USER_ID, QueryCriterium.OP_EQUALS, new Integer(userID)));
	    Vector<IMContact> contacts = new Vector<IMContact>();
	    try {
	    	RecordSet rs    = sysDs.select(q);
		    while (rs.next())
		    {
		    	IMContact contact = (IMContact) rs.getCurrentObject();
		    	contacts.add(contact);
		    }
	    } catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getImContactsByUserID()", e);
		}
	    return contacts;
	}

	public IMContact getIMContactByID(int contactID) throws MdnException{
        // create query
        DataSource sysDs = DataManager.getSystemDS();
        Query q = new Query(new QueryCriterium(IMContact.ENT_IM_CONTACT,
        		IMContact.FLD_ID, QueryCriterium.OP_EQUALS, new Integer(contactID)));
        RecordSet rs;
		try {
			rs = sysDs.select(q);
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getIMContactByID", e);
		}
		IMContact imConn = null;
        // get the ds
        if(rs != null && rs.next())
            imConn = (IMContact)rs.getCurrentObject();
        
        return imConn;
	}

	public IMContact getUserIMContactByConnType(int connectionType, int userId) throws MdnException{
        // create query
        DataSource sysDs = DataManager.getSystemDS();
        QueryCriterium q1 = new QueryCriterium(new QueryCriterium(IMContact.ENT_IM_CONTACT, IMContact.FLD_IM_MDN_CONNECTION_TYPE, QueryCriterium.OP_EQUALS, new Integer(connectionType)));
        QueryCriterium q2 = new QueryCriterium(new QueryCriterium(IMContact.ENT_IM_CONTACT, IMContact.FLD_MDN_USER_ID, QueryCriterium.OP_EQUALS, new Integer(userId)));
        Query query = new Query(q1,q2);
        
        RecordSet rs;
		try {
			rs = sysDs.select(query);
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getIMContactByID", e);
		}
		IMContact imConn = null;
        // get the ds
        if(rs != null && rs.next())
            imConn = (IMContact)rs.getCurrentObject();
        
        return imConn;
	}

	public int saveIMContact(IMContact imContact) throws MdnException{
		  try {
			  imContact.save();
			  return imContact.getId();
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in createIMContact", e);
		}
	  }

	public IMContact getUserIMContactByContactText(String userContactText) throws MdnException{
        // create query
        DataSource sysDs = DataManager.getSystemDS();
        Query q = new Query(new QueryCriterium(IMContact.ENT_IM_CONTACT, IMContact.FLD_IM_CONTACT_NAME, QueryCriterium.OP_EQUALS, userContactText));
//        QueryCriterium q2 = new QueryCriterium(User.ENT_USER, User.FLD_DEL_STATUS, QueryCriterium.OP_EQUALS, "0");
//        Query query = new Query(q1,q2);
        
        RecordSet rs;
		try {
			rs = sysDs.select(q);
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getImConnectionByID", e);
		}
		IMContact contact = null;
        // get the ds
        if(rs != null && rs.next())
            contact = (IMContact)rs.getCurrentObject();
        
        return contact;
	}

	public QueryDobj getQueryByName(String name, int projectId) throws MdnException {
        DataSource sysDs = DataManager.getSystemDS();
        QueryCriterium q1 = new QueryCriterium(QueryDobj.ENT_QUERY, QueryDobj.FLD_NAME, QueryCriterium.OP_EQUALS, name);
        QueryCriterium q2 = new QueryCriterium(QueryDobj.ENT_QUERY, QueryDobj.FLD_PROJECT_ID, QueryCriterium.OP_EQUALS, projectId);
        Query q = new Query(q1,q2);		
		
        // create query
//        DataSource sysDs = DataManager.getSystemDS();
//        Query q = new Query(new QueryCriterium(QueryDobj.ENT_QUERY, QueryDobj.FLD_NAME, QueryCriterium.OP_EQUALS, name));s
        RecordSet rs;
		try {
			rs = sysDs.select(q);
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getQueryByName()", e);
		}
		QueryDobj query = null;
        // get the ds
        if(rs != null && rs.next())
        	query = (QueryDobj)rs.getCurrentObject();
        
        return query;
	}

	public List<QueryDobj> getQueriesByType(String type) throws MdnException
	{
        // build query
        DataSource ds = DataManager.getSystemDS();
        Query q = new Query(QueryDobj.ENT_QUERY);
        q.addQueryCriterium(new QueryCriterium(QueryDobj.ENT_QUERY,
            QueryDobj.FLD_TYPE, QueryCriterium.OP_EQUALS,
            type));
        RecordSet rs;
		try {
			rs = ds.select(q);
			QueryDobj qdobj;
			List<QueryDobj>     queries        = new Vector<QueryDobj>();
			while(rs != null && rs.next())
            {
                // get the query
                qdobj = (QueryDobj)rs.getCurrentObject();
                if(qdobj != null)
                	queries.add(qdobj);
            }
			return queries;
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getQueriesByViewID", e);
		}
	}
	
	public QueryDobj getQueryByIMKeyword(String keyword) throws MdnException {
        // create query
        DataSource sysDs = DataManager.getSystemDS();
        Query q = new Query(new QueryCriterium(QueryDobj.ENT_QUERY,
        		QueryDobj.FLD_IM_KEYWORD, QueryCriterium.OP_EQUALS, keyword));
        RecordSet rs;
		try {
			rs = sysDs.select(q);
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getQueryByIMKeyword()", e);
		}
		QueryDobj queryDobj = null;
        // get the ds
        if(rs != null && rs.next())
        	queryDobj = (QueryDobj)rs.getCurrentObject();
        
        return queryDobj;
	}

	public QueryDobj getQueryBySmsKeyword(String keyword, int smsServerId) throws MdnException {
		DataSource sysDs = DataManager.getSystemDS();
		
        QueryCriterium q1 = new QueryCriterium(QueryDobj.ENT_QUERY,QueryDobj.FLD_MOBILE_STATUS, QueryCriterium.OP_EQUALS,new Integer(smsServerId));
        QueryCriterium q2;
        if(keyword.equals(""))
        	q2 = new QueryCriterium(QueryDobj.ENT_QUERY,QueryDobj.FLD_SMS_KEYWORD, QueryCriterium.OP_IS_NULL, null);
        else
        	q2 = new QueryCriterium(QueryDobj.ENT_QUERY,QueryDobj.FLD_SMS_KEYWORD, QueryCriterium.OP_EQUALS, keyword);
        
        Query q = new Query(q1,q2);
        
        RecordSet rs;
		try {
			rs = sysDs.select(q);
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getAddressBookByEmailAddress()", e);
		}
        // get the ds
		QueryDobj queryMessaging = null;
        if(rs != null && rs.next())
        {
        	queryMessaging = (QueryDobj)rs.getCurrentObject();
        	//if(queryMessaging.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT)
        	return queryMessaging;
        }
		return null;
	}

	public List<QueryDobj> getQueryBySmsKeyword(String keyword) throws MdnException {
        DataSource sysDs = DataManager.getSystemDS();
        Query q = new Query(new QueryCriterium(QueryDobj.ENT_QUERY, QueryDobj.FLD_SMS_KEYWORD, QueryCriterium.OP_EQUALS, keyword));
        RecordSet rs = null;
        List<QueryDobj>     queries   = new Vector<QueryDobj>();        
		try {
			rs = sysDs.select(q);
			QueryDobj qdobj;
			while(rs != null && rs.next())
            {
                // get the query
                qdobj = (QueryDobj)rs.getCurrentObject();
                if(qdobj != null)
                	queries.add(qdobj);
            }
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getQueryBySmsKeyword()", e);
		}
        
        return queries;
	}
	
	public QueryDobj getQueryByEmailKeyword(String keyword) throws MdnException {
        // create query
        DataSource sysDs = DataManager.getSystemDS();
        Query q = new Query(new QueryCriterium(QueryDobj.ENT_QUERY,
        		QueryDobj.FLD_EMAIL_KEYWORD, QueryCriterium.OP_EQUALS, keyword));
        RecordSet rs;
		try {
			rs = sysDs.select(q);
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getQueryByEmailKeyword()", e);
		}
		QueryDobj queryDobj = null;
        // get the ds
        if(rs != null && rs.next())
        	queryDobj = (QueryDobj)rs.getCurrentObject();
        
        return queryDobj;
	}

	public List<QueryDobj> searchQueryByKeyword(String messagingType, String keyword) throws MdnException {
        // create query
        DataSource sysDs = DataManager.getSystemDS();
        Query q = null;
        if(messagingType.equalsIgnoreCase(MDNIMServer.SERVER_TYPE)){
        	q = new Query(new QueryCriterium(QueryDobj.ENT_QUERY, QueryDobj.FLD_IM_KEYWORD, QueryCriterium.OP_LIKE, keyword));
        }else if(messagingType.equalsIgnoreCase(MdnSmsServer.SERVER_TYPE) || messagingType.equalsIgnoreCase(MdnSmppGatewayServer.SERVER_TYPE)){
        	q = new Query(new QueryCriterium(QueryDobj.ENT_QUERY, QueryDobj.FLD_SMS_KEYWORD, QueryCriterium.OP_LIKE, keyword));
        }else if(messagingType.equalsIgnoreCase(MdnMailService.SERVER_TYPE)){
        	q = new Query(new QueryCriterium(QueryDobj.ENT_QUERY, QueryDobj.FLD_EMAIL_KEYWORD, QueryCriterium.OP_LIKE, keyword));
        }
        
        List queriesList = new ArrayList<QueryDobj>();
        RecordSet rs = null;
		try {
			if(q != null)
				rs = sysDs.select(q);
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getQueryByIMKeyword()", e);
		}
		QueryDobj queryDobj = null;
        // get the ds
		if(rs != null){
			while(rs.next()){
	        	queryDobj = (QueryDobj)rs.getCurrentObject();
	        	if(queryDobj.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT){
	        		queriesList.add(queryDobj);
	        	}
	        }
		}
        return queriesList;
	}	
	public List<QueryDobj> geQueryByEmailId(int emailId) throws MdnException {
		List<QueryDobj> queryEmailIdList = new ArrayList<QueryDobj>();
        // create query
        DataSource sysDs = DataManager.getSystemDS();
        Query q = new Query(new QueryCriterium(QueryDobj.ENT_QUERY,
        		QueryDobj.FLD_EMAIL_ADDRESS_ID, QueryCriterium.OP_EQUALS, new Integer(emailId)));
        RecordSet rs;
		try {
			rs = sysDs.select(q);
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in geQueryByEmailId()", e);
		}
		QueryDobj queryDobj = null;
        // get the ds
        while(rs.next())
        {
        	queryDobj = (QueryDobj)rs.getCurrentObject();
        	if(queryDobj.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT){
	        	String emailKeyword = queryDobj.getEmailKeyword();
	        	if(emailKeyword == null || emailKeyword.equals(""))
	        		emailKeyword = "null";
	        	queryEmailIdList.add(queryDobj);
        	}
        }
        return queryEmailIdList;
	}
	
	public List<QueryDobj> getAllQueriesBySmsServerId(int smsServerId) throws MdnException {
		List<QueryDobj> queriesList = new ArrayList<QueryDobj>();
        // create query
        DataSource sysDs = DataManager.getSystemDS();
        Query q = new Query(new QueryCriterium(QueryDobj.ENT_QUERY, QueryDobj.FLD_MOBILE_STATUS, QueryCriterium.OP_EQUALS, new Integer(smsServerId)));
        RecordSet rs;
		try {
			rs = sysDs.select(q);
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in geQueryByEmailId()", e);
		}
		QueryDobj queryDobj = null;
        // get the ds
        while(rs.next())
        {
        	queryDobj = (QueryDobj)rs.getCurrentObject();
        	if(queryDobj.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT){
	        	String keyword = queryDobj.getSmsKeyword();
	        	queriesList.add(queryDobj);
        	}
        }
        return queriesList;
	}	
	public int saveEmailSetting(MdnEmailSetting emailSetting) throws MdnException {
		try {
			emailSetting.save();
			return emailSetting.getId();
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in saveEmailSetting", e);
		}
	}

	public MdnEmailSetting getEmailSetting() throws MdnException {
	    DataSource ds    = DataManager.getDataSource(MdnEmailSetting.ENT_EMAIL);
	    Query query = new Query(MdnEmailSetting.ENT_EMAIL);

	    try {
	    	RecordSet rs    = ds.select(query);
		    while (rs.next())
		    {
		    	MdnEmailSetting mailSett = (MdnEmailSetting) rs.getCurrentObject();
		    	return mailSett;
		    	//emailsSett.add(mailSett);
		    }
	    } catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getEmailSetting()", e);
		}
	    return null;
	}

	public List<MdnEmailSetting> getMdnEmailAddresses() throws MdnException {
	    DataSource ds    = DataManager.getDataSource(MdnEmailSetting.ENT_EMAIL);
	    Query query = new Query(MdnEmailSetting.ENT_EMAIL);

	    Vector<MdnEmailSetting> mdnEmails = new Vector<MdnEmailSetting>();
	    try {
	    	RecordSet rs    = ds.select(query);
		    while (rs.next()) {
		    	MdnEmailSetting emailSett = (MdnEmailSetting) rs.getCurrentObject();
		    	mdnEmails.add(emailSett);
		    }
	    } catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getMdnEmailAddresses", e);
		}
	    return mdnEmails;
	}

	public MdnEmailSetting getEmailSettingById(int mdnEmailId) throws MdnException {
        DataSource sysDs = DataManager.getSystemDS();
        Query q = new Query(new QueryCriterium(MdnEmailSetting.ENT_EMAIL,
        		MdnEmailSetting.FLD_ID, QueryCriterium.OP_EQUALS, new Integer(mdnEmailId)));
        RecordSet rs;
		try {
			rs = sysDs.select(q);
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getEmailSettingById()", e);
		}
		MdnEmailSetting mdnEmailServer = null;
        // get the ds
        if(rs != null && rs.next())
        {
        	mdnEmailServer = (MdnEmailSetting)rs.getCurrentObject();
        }
        return mdnEmailServer;
	}

	public MdnEmailSetting getEmailByAddress(String emailAddress) throws MdnException {
        DataSource sysDs = DataManager.getSystemDS();
        Query q = new Query(new QueryCriterium(MdnEmailSetting.ENT_EMAIL,
        		MdnEmailSetting.FLD_EMAIL_ADDRESS, QueryCriterium.OP_EQUALS, emailAddress));
        RecordSet rs;
		try {
			rs = sysDs.select(q);
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getEmailByAddress()", e);
		}
		MdnEmailSetting mdnEmailServer = null;
        // get the ds
        if(rs != null && rs.next())
        {
        	mdnEmailServer = (MdnEmailSetting)rs.getCurrentObject();
        }
        return mdnEmailServer;
	}
	
	public int saveSmsSetting(MdnSmsSetting smsSetting) throws MdnException {
		try {
			smsSetting.save();
			return smsSetting.getId();
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in saveSmsSetting", e);
		}
	}

	public MdnSmsSetting getSmsSetting() throws MdnException {
	    DataSource ds    = DataManager.getDataSource(MdnSmsSetting.ENT_SMS);
	    Query query = new Query(MdnSmsSetting.ENT_SMS);

	    try {
	    	RecordSet rs    = ds.select(query);
		    while (rs.next())
		    {
		    	MdnSmsSetting smsSett = (MdnSmsSetting) rs.getCurrentObject();
		    	return smsSett;
		    }
	    } catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getSmsSetting()", e);
		}
	    return null;
	}

	public MdnSmpp getSmsGatewayByNumber(String sourceNumber) throws MdnException{
        DataSource sysDs = DataManager.getSystemDS();
        Query q = new Query(new QueryCriterium(MdnSmpp.ENT_SMPP,
        		MdnSmpp.FLD_NUMBER, QueryCriterium.OP_EQUALS, sourceNumber));

	    try {
	    	RecordSet rs = sysDs.select(q);
		    while (rs.next())
		    {
		    	MdnSmpp smsGateway = (MdnSmpp) rs.getCurrentObject();
		    	return smsGateway;
		    }
	    } catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getSmsGateway()", e);
		}
	    return null;		
	}
	
	public MdnSmpp getSmsGatewayByID(int id) throws MdnException{
        DataSource sysDs = DataManager.getSystemDS();
        Query q = new Query(new QueryCriterium(MdnSmpp.ENT_SMPP,
        		MdnSmpp.FLD_ID, QueryCriterium.OP_EQUALS, new Integer(id)));

	    try {
	    	RecordSet rs = sysDs.select(q);
		    while (rs.next()) {
		    	MdnSmpp smsGateway = (MdnSmpp) rs.getCurrentObject();
		    	return smsGateway;
		    }
	    } catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getSmsGatewayByID()", e);
		}
	    return null;				
	}
	
	public List<MdnSmpp> getAllSmppGateway() throws MdnException {
	    DataSource ds    = DataManager.getDataSource(MdnSmpp.ENT_SMPP);
	    Query query = new Query(MdnSmpp.ENT_SMPP);

	    Vector<MdnSmpp> smppGateways = new Vector<MdnSmpp>();
	    try {
	    	RecordSet rs    = ds.select(query);
		    while (rs.next())
		    {
		    	MdnSmpp smpp = (MdnSmpp) rs.getCurrentObject();
		    	if(smpp.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT)
		    		smppGateways.add(smpp);
		    }
	    } catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getAllSmppGateway()", e);
		}
	    return smppGateways;
	}	
	
	public QueryDobj getUniqueQueryByEmailInfo(int emailAddressId, String emailKeyword) throws MdnException{
		DataSource sysDs = DataManager.getSystemDS();
		
        QueryCriterium q1 = new QueryCriterium(QueryDobj.ENT_QUERY,QueryDobj.FLD_EMAIL_ADDRESS_ID, QueryCriterium.OP_EQUALS,new Integer(emailAddressId));
        QueryCriterium q2;
        if(emailKeyword.equals(""))
        	q2 = new QueryCriterium(QueryDobj.ENT_QUERY,QueryDobj.FLD_EMAIL_KEYWORD, QueryCriterium.OP_IS_NULL, null);
        else
        	q2 = new QueryCriterium(QueryDobj.ENT_QUERY,QueryDobj.FLD_EMAIL_KEYWORD, QueryCriterium.OP_EQUALS, emailKeyword);
        
        Query q = new Query(q1,q2);
        
        RecordSet rs;
		try {
			rs = sysDs.select(q);
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getAddressBookByEmailAddress()", e);
		}
        // get the ds
		QueryDobj queryMessaging = null;
        if(rs != null && rs.next())
        {
        	queryMessaging = (QueryDobj)rs.getCurrentObject();
        	//if(queryMessaging.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT)
        	return queryMessaging;
        }
		
		return null;

	}
	
	private void saveQueryCriteriaHistory(QueryCriteriaDobj queryCriteriaDobj, String msgID, ArrayList<String> userInputs){
		//Add historic data
		QueryCriteriaHistoryDobj queryCriteriaHistoryDobj = new QueryCriteriaHistoryDobj();	
		queryCriteriaHistoryDobj.setMsgId(Integer.parseInt(msgID));
		queryCriteriaHistoryDobj.setOriginalId(queryCriteriaDobj.getId());
		queryCriteriaHistoryDobj.setQueryId(queryCriteriaDobj.getQueryId());
		queryCriteriaHistoryDobj.setValueOrCondition(queryCriteriaDobj.getValueOrCondition());
		queryCriteriaHistoryDobj.setRowNo(queryCriteriaDobj.getRowNo());
		queryCriteriaHistoryDobj.setType(queryCriteriaDobj.getType());
		queryCriteriaHistoryDobj.setUsed(queryCriteriaDobj.getUsed());
		queryCriteriaHistoryDobj.setIndent(queryCriteriaDobj.getIndent());
		queryCriteriaHistoryDobj.setParent(queryCriteriaDobj.getParent());
		queryCriteriaHistoryDobj.setNumber(queryCriteriaDobj.getNumber());
		queryCriteriaHistoryDobj.setName(queryCriteriaDobj.getName());
		queryCriteriaHistoryDobj.setCompId(queryCriteriaDobj.getCompId());
		queryCriteriaHistoryDobj.setComparison(queryCriteriaDobj.getComparison());
		queryCriteriaHistoryDobj.setConnection(queryCriteriaDobj.getConnection());
		queryCriteriaHistoryDobj.setGrouping(queryCriteriaDobj.getGrouping());
		
		//set value from user input
		if (queryCriteriaDobj.getValue().equalsIgnoreCase(queryCriteriaDobj.VALUE_USERINPUT))
			queryCriteriaHistoryDobj.setValue(userInputs.get(Integer.parseInt(queryCriteriaDobj.getValueUserInputSeq())-1));					
		else
			queryCriteriaHistoryDobj.setValue(queryCriteriaDobj.getValue());
		
		//set value2 if value2 is userinput also
		if (queryCriteriaDobj.getValue2().equalsIgnoreCase(queryCriteriaDobj.VALUE_USERINPUT))
			queryCriteriaHistoryDobj.setValue2(userInputs.get(Integer.parseInt(queryCriteriaDobj.getValue2UserInputSeq())-1));
		else
			queryCriteriaHistoryDobj.setValue2(queryCriteriaDobj.getValue2());
		
		queryCriteriaHistoryDobj.setDateTime(new Date());
		queryCriteriaHistoryDobj.setState(DataObject.NEW);
		try {
			queryCriteriaHistoryDobj.save();
		} catch (DataSourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
	}
	
	public int getInsertQueryResultWithUserInput(QueryDobj query, UserReply ur, ArrayList<String> userInputs, String objectType, int userId){
		execSql = true;
		try {
			EntityDobj entity; 
			int dataSourceId;
			int queryOrUrId;
			String simpleSql;
			if(objectType != null && objectType.equalsIgnoreCase("UR")){
				queryOrUrId = ur.getId();
				entity = ur.getTable(true);
				dataSourceId = entity.getDataSourceId();
				simpleSql = ur.getCriteriaString();
			}else{
				queryOrUrId = query.getId();
				entity = query.getTable(true);
				dataSourceId = entity.getDataSourceId();
				simpleSql = query.getCriteriaString();
			}
			
			Vector<FieldDobj> fields = (Vector<FieldDobj>)entity.getFields();
			List<QueryCriteriaDobj> queryCriteria = getQueryCriteriaByQueryID(queryOrUrId, objectType);
			
//			for (QueryCriteriaDobj queryCriteriaDobj : queryCriteria){
//				if (queryCriteriaDobj.hasUserInputValue()){
//					saveQueryCriteriaHistory(queryCriteriaDobj, String.valueOf(queryOrUrId), userInputs);
//				}
//			}
			String sql = simpleSql;
/*
			CustomField cf = null;
			UserCustomField ucf = null;
			
			boolean used = false;
			String fieldsStr = "(";
			String values = "(";			
			//Get new sql string
			for (QueryCriteriaDobj queryCriteriaDobj : queryCriteria){
				
				used = queryCriteriaDobj.getUsed() == 1 ? true : false; 
				if (!used){
					continue;
				} 
				//Get Field Type
				int fieldType = Field.FT_STRING;
				String field = queryCriteriaDobj.getName();
				
				for (FieldDobj currField : fields){
					if (currField.getName().equalsIgnoreCase(field)){
						fieldType = currField.getType();
					}
				}
				
				String value = queryCriteriaDobj.getValue();
				
				//set value from user input
					if (value.equalsIgnoreCase(queryCriteriaDobj.VALUE_USERINPUT)){
						try{
							value = userInputs.get(Integer.parseInt(queryCriteriaDobj.getValueUserInputSeq())-1);
						}catch (Exception e) {
							System.out.println("USERINPUT ERROR : UserInput is wrong ");
						}
					}else{
						cf = getCustomFieldByName(value);
						if(cf != null){
							ucf = getUserCustomByCustomId(userId, cf.getId());
							if(ucf != null)
								value = ucf.getParameter();
						}
					}
				fieldsStr = fieldsStr + "  " + field + " , ";
				if (fieldType == Field.FT_STRING){
					values = values + " '" + value + "', ";
				}else{
					values = values + "  " + value + " , ";
				}
			}
			//if((userInputs != null && !userInputs.isEmpty()) || (cf != null && ucf !=null)) {
			if((simpleSql.contains("[UserInput") && userInputs != null && !userInputs.isEmpty()) || (cf != null && ucf !=null)){	
				fieldsStr = fieldsStr.substring(0, fieldsStr.length() - 2);
				values = values.substring(0, values.length() - 2);
				fieldsStr += ") ";
				values += ") ";
				sql = "INSERT INTO " + entity.getName() + " " + fieldsStr + " VALUES " + values;
			}else
				sql = simpleSql;
*/			
			//System.out.println("****SQL: " + sql);		

			sql = replaceSqlWithUserInput(sql, userInputs, userId);
			
			int row = 0;
			
	        // get the view ds
	        DataViewDataSource dvds = MdnDataManager.getDataViewDS();
	        if(execSql){
		        row = dvds.execInsertOrUpdate(dataSourceId, sql);	
	            System.out.println("How many rows are inserted: "  + row);
	        }
	        return row;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (MdnException e) {
			e.printStackTrace();
		} catch (DataSourceException e) {
			System.out.println("Invalid SQL Statement: " + e);
			return FAILED_EXEC_SQL;
		}
		return 0;
	}

	public int getUpdateQueryResultWithUserInput(QueryDobj query, UserReply ur, ArrayList<String> userInputs, String objectType, int userId){
		execSql = true;
		try {
			EntityDobj entity; 
			int dataSourceId;
			int queryOrUrId;
			String simpleSql;
			if(objectType != null && objectType.equalsIgnoreCase("UR")){
				queryOrUrId = ur.getId();
				entity = ur.getTable(true);
				dataSourceId = entity.getDataSourceId();
				simpleSql = ur.getCriteriaString();
			}else{
				queryOrUrId = query.getId();
				entity = query.getTable(true);
				dataSourceId = entity.getDataSourceId();
				simpleSql = query.getCriteriaString();
			}
			Vector<FieldDobj> fields = (Vector<FieldDobj>)entity.getFields();
			List<QueryCriteriaDobj> queryCriteria = getQueryCriteriaByQueryID(queryOrUrId, objectType);

			String sql = simpleSql;
/*			String sqlValue="";
			String sqlWhere="";
			boolean used = false;
			boolean lastIsCondition = false;
			CustomField cf = null;
			UserCustomField ucf = null;
			
			//Get new sql string
			int i = 0;
			for (QueryCriteriaDobj queryCriteriaDobj : queryCriteria){
				
				i++;
				String condition = "AND"; 
				String grouping = queryCriteriaDobj.getGrouping();
				if ( grouping.equalsIgnoreCase("all") )
					condition = "AND" ;
				else if (grouping.equalsIgnoreCase("any"))
					condition = "OR";
				
				used = queryCriteriaDobj.getUsed() == 1 ? true : false; 
				if (!used){
					continue;
				} else {
					if (!sqlWhere.trim().equals("") && lastIsCondition && i < queryCriteria.size()){
						sqlWhere = sqlWhere + condition + " ";
					}
				}

				// Deal with the IS NULL case.
				String thisRow = null;
				
				//Get Field Type
				int fieldType = Field.FT_STRING;
				String field = queryCriteriaDobj.getName();
				for (FieldDobj currField : fields){
					if (currField.getName().equalsIgnoreCase(field)){
						fieldType = currField.getType();
					}
				}
				
				String value = queryCriteriaDobj.getValue();
				String valueOrCondition = queryCriteriaDobj.getValueOrCondition();
				
				String value2 = queryCriteriaDobj.getValue2();
				
				if (value.equals("_____")){
					value = "";
				}
				if (value2.equals("_____")){
					value2 = "";
				}
				
				//set value from user input
					if (value.equalsIgnoreCase(queryCriteriaDobj.VALUE_USERINPUT)){
						try{
							value = userInputs.get(Integer.parseInt(queryCriteriaDobj.getValueUserInputSeq())-1);
						}catch (Exception e) {
							System.out.println("USERINPUT ERROR : UserInput is wrong ");
						}
					}else{
						cf = getCustomFieldByName(value);
						if(cf != null){
							ucf = getUserCustomByCustomId(userId, cf.getId());
							if(ucf != null)
								value = ucf.getParameter();
						}
					}

					//set value2 if value2 is userinput also
					if (value2.equalsIgnoreCase(queryCriteriaDobj.VALUE_USERINPUT)){
						try{
							value2 = userInputs.get(Integer.parseInt(queryCriteriaDobj.getValue2UserInputSeq())-1);
						}catch (Exception e) {
							System.out.println("USERINPUT ERROR : UserInput is wrong ");
						}
					}else if(!value2.equals("")){
						CustomField cf2 = getCustomFieldByName(value2);
						if(cf2 != null){
							UserCustomField ucf2 = getUserCustomByCustomId(userId, cf2.getId());
							if(ucf2 != null)
								value2 = ucf2.getParameter();
						}
					}
				
				if (valueOrCondition.equalsIgnoreCase("value")){
					if (fieldType == Field.FT_STRING){
						thisRow = "  " + field + "  = " + " '" + value + "', ";
					}else{
						thisRow = "  " + field + "  = " + "  " + value + " , ";
					}				
					sqlValue = sqlValue + thisRow ;
					lastIsCondition = false;
				}
				else{
					thisRow = QueryDobj.getSQLCondition(queryCriteriaDobj, userInputs, fieldType, value, value2);
					sqlWhere = sqlWhere + thisRow + " ";
					lastIsCondition = true;
				}				
				
				// Need to check whether we deal with the 'grouping' value of NONE.
				if (grouping.equals("none") || grouping.equals("not all")){
					sqlWhere = "NOT (" + sqlWhere + ")";
				}
			}
			//if(userInputs != null && !userInputs.isEmpty())
			if((simpleSql.contains("[UserInput") && userInputs != null && !userInputs.isEmpty()) || (cf != null && ucf !=null))
			{
				// Add the WHERE clause	
				sqlValue = sqlValue.substring(0, sqlValue.length() - 2);
				
				sql = "UPDATE " + entity.getName() + " SET" + sqlValue;			
				
				if (sqlWhere.length() > 0)
					sql = sql + " WHERE " + sqlWhere;
			}else
				sql = simpleSql;
*/			
			sql = replaceSqlWithUserInput(sql, userInputs, userId);
			//System.out.println("****SQL: " + sql);		
			
			int row=0;
	        // get the view ds			
	        DataViewDataSource dvds = MdnDataManager.getDataViewDS();
	        if(execSql){
		        row = dvds.execInsertOrUpdate(dataSourceId, sql);	
	            System.out.println("How many rows are updated: "  + row);
	        }
	        return row;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (MdnException e) {
			e.printStackTrace();
		} catch (DataSourceException e) {
			e.printStackTrace();
		}
		return 0;
			
	}
	
	public List<UserReply> getAllUserReplies(int projectId) throws MdnException {
		DataSource ds    = DataManager.getDataSource(UserReply.ENT_USER_REPLY);
	    Query query = new Query(UserReply.ENT_USER_REPLY);
	    
        QueryCriterium qc1 = new QueryCriterium(UserReply.ENT_USER_REPLY,
        		UserReply.FLD_PROJECT_ID, QueryCriterium.OP_EQUALS,
                projectId);
        query.addQueryCriterium(qc1);            

	    Vector<UserReply> userReplies = new Vector<UserReply>();
	    try {
	    	RecordSet rs    = ds.select(query);
		    while (rs.next())
		    {
		    	UserReply userReplyMessage = (UserReply) rs.getCurrentObject();
		    	userReplies.add(userReplyMessage);
		    }
	    } catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getAllUserReplies()", e);
		}
	    return userReplies;
	    
	}
	
	public UserReply getUserReplyById(int userReplyId) throws MdnException{
	    DataSource ds    = DataManager.getDataSource(UserReply.ENT_USER_REPLY);
	    Query q = new Query(UserReply.ENT_USER_REPLY);

	    q.addQueryCriterium (new QueryCriterium (UserReply.ENT_USER_REPLY,
	    		UserReply.FLD_ID, QueryCriterium.OP_EQUALS,
			new Integer (userReplyId)));
		RecordSet rs;
		UserReply userReplyMessage = null;
		try {
			rs = ds.select (q);
		    if (rs != null && rs.next())
		    {
		    	userReplyMessage = (UserReply) rs.getCurrentObject();
		    }
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getUserRepliesById", e);
		}
		return userReplyMessage;
	}

	public UserReply getUserReplyByMessageText(String msgTxt) throws MdnException {
        // create query
        DataSource sysDs = DataManager.getSystemDS();
        Query q = new Query(new QueryCriterium(UserReply.ENT_USER_REPLY,
        		UserReply.FLD_MSG_TEXT, QueryCriterium.OP_EQUALS, msgTxt));
        RecordSet rs;
		try {
			rs = sysDs.select(q);
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getUserReplyByMessageText()", e);
		}
		UserReply ur = null;
        // get the ds
        if(rs != null && rs.next())
        	ur = (UserReply)rs.getCurrentObject();
        
        return ur;
	}
	
	public UserReply getUserReplyByMsgTxtAndParentId(int parentId, String msgTxt) throws MdnException{
		DataSource sysDs = DataManager.getSystemDS();

		QueryCriterium q1 = new QueryCriterium(UserReply.ENT_USER_REPLY,UserReply.FLD_PARENT_ID, QueryCriterium.OP_EQUALS, parentId);

		QueryCriterium q2;
		if(msgTxt == null || msgTxt.equals(""))
			q2 = new QueryCriterium(UserReply.ENT_USER_REPLY, UserReply.FLD_MSG_TEXT, QueryCriterium.OP_IS_NULL, null);
		else
			q2 = new QueryCriterium(UserReply.ENT_USER_REPLY, UserReply.FLD_MSG_TEXT, QueryCriterium.OP_EQUALS, msgTxt);
		
        Query q = new Query(q1,q2);
        
        RecordSet rs;
		try {
			rs = sysDs.select(q);
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getUserReplyByMsgTxtAndParentId()", e);
		}
        // get the ds
		UserReply ur = null;
        if(rs != null && rs.next())
        {
        	ur = (UserReply)rs.getCurrentObject();
        }
		
		return ur;

	}

	public List<UserReply> getUserRepliesByParentId(int parentId) throws MdnException{
	    DataSource ds    = DataManager.getDataSource(UserReply.ENT_USER_REPLY);
	    Query q = new Query(UserReply.ENT_USER_REPLY);

	    q.addQueryCriterium (new QueryCriterium (UserReply.ENT_USER_REPLY,
	    		UserReply.FLD_PARENT_ID, QueryCriterium.OP_EQUALS,
			new Integer (parentId)));
		RecordSet rs;
		Vector<UserReply> userReplies = new Vector<UserReply>();
		try {
			rs = ds.select (q);
		    while (rs.next())
		    {
		    	UserReply userReplyMessage = (UserReply) rs.getCurrentObject();
		    	if(userReplyMessage.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT)
		    		userReplies.add(userReplyMessage);
		    }
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getUserRepliesByParentId", e);
		}
		return userReplies;
	}
	
	public RecordSet getSelectQueryResultWithUserInput(QueryDobj query, UserReply ur , ArrayList<String> userInputs, String objectType, int userId) throws MdnException{
		RecordSet rs = null;
		
		try {
			DataView dataView; 
			int dataSourceId;
			int groupFieldId;
			String sortString;
			int queryOrUrId; 
			String simpleSql;
			if(objectType != null && objectType.equalsIgnoreCase("UR")){
				queryOrUrId = ur.getId();
				dataView = ur.getDataView(true);
				dataSourceId = dataView.getSourceDsId();
				groupFieldId = ur.getGroupFieldId();
				sortString = ur.getSortString();
				simpleSql = ur.getCriteriaString();
			}else{
				queryOrUrId = query.getId();
				dataView = query.getDataView(true);
				dataSourceId = dataView.getSourceDsId();
				groupFieldId = query.getGroupFieldId();
				sortString = query.getSortString();
				simpleSql = query.getCriteriaString();
			}
			/*String[] lstUserInputs = new String[0];
			if (userInputs != null){
				lstUserInputs = (String[])userInputs.toArray();
			}*/
			
			return getQueryResultWithUserInput(dataView, simpleSql, userInputs, userId);
			
			/*Vector<DataViewField> fields = (Vector<DataViewField>)dataView.getFields();
			
			List<QueryCriteriaDobj> queryCriteria = getQueryCriteriaByQueryID(queryOrUrId, objectType);
			
			CustomField cf = null;
			UserCustomField ucf = null;

			String sql = "";
			boolean used = false;
			String fromTableName = null;
			ArrayList<String> fromTableNames = new ArrayList<String>();
			//Get new sql string
			for (QueryCriteriaDobj queryCriteriaDobj : queryCriteria){
			
				String condition = "AND"; 
				String grouping = queryCriteriaDobj.getGrouping();
				if ( grouping.equalsIgnoreCase("all") )
					condition = "AND" ;
				else if (grouping.equalsIgnoreCase("any"))
					condition = "OR";						

				used = queryCriteriaDobj.getUsed() == 1 ? true : false; 
				if (!used){
					continue;
				} else {
					if (!sql.equals("")){
						sql = sql + condition + " ";
					}
				}

				String wholeField = queryCriteriaDobj.getName();
				int currPos = wholeField.indexOf(".", 0);
   				if (currPos > 0){
   					fromTableName = wholeField.substring(0, currPos);
   					boolean included = fromTableNames.contains(fromTableName);
   					if (!included){
   						fromTableNames.add(fromTableName);
   					}
   				}				
				
				//Get Field Type
				int fieldType = Field.FT_STRING;
				for (DataViewField currField : fields){
					String currWholeField = currField.getSourceEntity() + "." + currField.getSourceField();
					if (currWholeField.equalsIgnoreCase(wholeField)){
						fieldType = currField.getType();
					}
				}
				
				String value = queryCriteriaDobj.getValue();
				String value2 = queryCriteriaDobj.getValue2();
				
				if (value.equals("_____")){
					value = "";
				}
				if (value2.equals("_____")){
					value2 = "";
				}
				
				//set value from user input
					if (value.equalsIgnoreCase(queryCriteriaDobj.VALUE_USERINPUT)){
						try{
							value = userInputs.get(Integer.parseInt(queryCriteriaDobj.getValueUserInputSeq())-1);
						}catch (Exception e) {
							System.out.println("USERINPUT ERROR : UserInput is wrong ");
						}
					} else {
						CustomField isCF = getCustomFieldByName(value);
						if(isCF != null && userId != 0){
							cf = getCustomFieldByName(value);
							if(cf != null){
								ucf = getUserCustomByCustomId(userId, cf.getId());
								if(ucf != null)
									value = ucf.getParameter();
							}
						}
					}
					System.out.println("+++++++++++++++++++++++++++++++ value == " + value);
					//set value2 if value2 is userinput also
					if (value2.equalsIgnoreCase(queryCriteriaDobj.VALUE_USERINPUT)){
						try{
							value2 = userInputs.get(Integer.parseInt(queryCriteriaDobj.getValue2UserInputSeq())-1);
						}catch (Exception e) {
							System.out.println("USERINPUT ERROR : UserInput is wrong ");
						}
					}else {
						CustomField isCF = getCustomFieldByName(value); 
						if(isCF != null && userId != 0){
							CustomField cf2 = getCustomFieldByName(value2);
							if(cf2 != null){
								UserCustomField ucf2 = getUserCustomByCustomId(userId, cf2.getId());
								if(ucf2 != null)
									value2 = ucf2.getParameter();
							}
						}
					}
				
				String thisRow = QueryDobj.getSQLCondition(queryCriteriaDobj, userInputs, fieldType, value, value2);
				sql = sql + thisRow + " ";
				
				// Need to check whether we deal with the 'grouping' value of NONE.
				if (grouping.equals("none") || grouping.equals("not all")){
					sql = "NOT (" + sql + ")";
				}
			}
			
			if((simpleSql.contains("[UserInput") && userInputs != null && !userInputs.isEmpty()) || (cf != null && ucf !=null))				
			{
				String selectFromSql = "";
				for (int m = 0; m < fromTableNames.size(); m++){
	   				String currTableName = fromTableNames.get(m);
	   				selectFromSql = selectFromSql + currTableName + ",";
	   			}
				selectFromSql = selectFromSql.substring(0, selectFromSql.length()-1);
				// Add the WHERE clause.
				if (sql.length() > 1){
					sql = "SELECT * FROM " + selectFromSql + " WHERE " + sql;
				}
				else{
					sql = "SELECT * FROM " + selectFromSql;
				}
				if (groupFieldId != 0 && groupFieldId != -1){
					String groupFieldName = "";
					for (DataViewField currField : fields){
						if (currField.getId() == groupFieldId){
							groupFieldName = currField.getSourceEntity() +"." +currField.getSourceField();
						}
					}				
					sql += " GROUP BY " + groupFieldName;
				}
				if (sortString != null && !sortString.equals("")){
					sql += " ORDER BY " + sortString;
				}
			}else
			{
				sql = simpleSql; 
			}
			//System.out.println("****SQL: " + sql);	
			
	        // get the view ds
	        DataViewDataSource dvds = MdnDataManager.getDataViewDS();
	        
            rs = dvds.execDirectSQL(dataSourceId, sql, dataView);
			*/
	        
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} /*catch (MdnException e) {
			e.printStackTrace();
		} catch (DataSourceException e) {
			e.printStackTrace();
		}*/
		return rs;
	}
	
	public String replaceSqlWithUserInput(String sql, ArrayList<String> userInputs, int userId) throws MdnException{
		if(sql.contains(Constants.DATE_TIME_FORMAT)){
			System.out.println("current date/time replacement");
			Timestamp timestamp = new Timestamp(new Date().getTime());
			String matchingStr = "["+Constants.DATE_TIME_FORMAT+"]";
			sql = sql.replace(matchingStr, timestamp.toString());				
		}
		
		if (sql.contains("UserInput") && userInputs != null && userInputs.size() != 0){//
			int i = 1;
			for (String userInput : userInputs){
				//String regex = "\\[UserInput" + i + "\\]";
				String regex = "UserInput" + i + "";
				if (sql.contains(regex)){
					regex = "\\[UserInput" + i + "\\]";
					Pattern p = Pattern.compile(regex);
					Matcher m = p.matcher(sql);
					sql = m.replaceFirst(userInput.trim());
					//sql = sql.replaceAll(regex, userInput);
				}
				i++;
			}
		}
		
		boolean containCustom = false;
		//Check userId and custom fields
		List<CustomField> customFields = getAllCustomFields();
		for (CustomField cf : customFields){
			String customName = cf.getName();
			
			if (sql.contains(customName)){
				String matchingStr = null;
				if (sql.contains(customName)){
					matchingStr = customName;
					containCustom = true;
				}
				
				if (userId != 0){//MDN user
					Pattern p = Pattern.compile(matchingStr);
					Matcher m = p.matcher(sql);
					
					UserCustomField ucf = getUserCustomByCustomId(userId, cf.getId());
					if(ucf != null){
						String replaceStr = matchingStr.replace(cf.getName(), ucf.getParameter());
						sql = m.replaceFirst(replaceStr);
					}else{
						execSql = false;
						System.out.println( "The custom query value <" + customName + "> has not been defined for this user");
					}
				} 
			}
		}
		
		if(userId == 0 && containCustom){
			execSql = false;
			System.out.println( "This query contains custom query values, so public users are not permitted");			
		}
		
		if(execSql)
			System.out.println("****SQL: " + sql);
		
		return sql;		
	}
	
	public RecordSet getQueryResultWithUserInput(DataView dataView, String sql, ArrayList<String> userInputs, int userId) throws MdnException{
		RecordSet rs = null;
		
		try {
			sql = replaceSqlWithUserInput(sql, userInputs, userId);
			
	        // get the view ds
	        DataViewDataSource dvds = MdnDataManager.getDataViewDS();
	        
            rs = dvds.execDirectSQL(sql, dataView);
	        
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw new MdnException(e.toString());
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException(e.toString());
		}
		return rs;
	}	
	
	public QueryDobj getQueryPropertiesByID(int queryID) throws MdnException   
	{
/*		QueryDobj queryMsg = new QueryDobj();
        DataSource sourceDs = DataManager.getSystemDS();

		if (sourceDs == null)
			throw new RuntimeException ("Source DataSource not found" );
		
		String sql = "SELECT FLD_NAME, FLD_DESCRIPTION, FLD_TYPE, FLD_PARENTID, " +
				"FLD_DB_ID, FLD_TIMEOUT, FLD_DS_STATUS, FLD_WS_ID " +
				" FROM TBL_QUERY " +
				" WHERE FLD_ID = " + queryID;
		Hashtable hash;
		try {
			hash = sourceDs.execDirectSQL (sql);
			
			Collection vals = hash.values ();
			Iterator iter = vals.iterator ();
			Vector col = (Vector)iter.next ();
			
			
			Map fieldsNameMap = new HashMap();//field Name, method Name
			Map paramTypeMap = new HashMap();//field Name, parameter type
			
			fieldsNameMap.put("FLD_NAME", "setName");
			paramTypeMap.put("FLD_NAME", String.class);
			
			fieldsNameMap.put("FLD_DESCRIPTION", "setDescription");
			paramTypeMap.put("FLD_DESCRIPTION", String.class);
			
			fieldsNameMap.put("FLD_TYPE", "setType");
			paramTypeMap.put("FLD_TYPE", String.class);
			
			fieldsNameMap.put("FLD_PARENTID", "setViewOrTableId");
			paramTypeMap.put("FLD_PARENTID", int.class);
			
			fieldsNameMap.put("FLD_DB_ID", "setDatabaseId");
			paramTypeMap.put("FLD_DB_ID", int.class);
			
			fieldsNameMap.put("FLD_TIMEOUT", "setTimeout");
			paramTypeMap.put("FLD_TIMEOUT", String.class);

			fieldsNameMap.put("FLD_DS_STATUS", "setDatasourceStatus");
			paramTypeMap.put("FLD_DS_STATUS", int.class);
			
			fieldsNameMap.put("FLD_WS_ID", "setWebServiceId");
			paramTypeMap.put("FLD_WS_ID", int.class);
			
			queryMsg = (QueryDobj)setResultValuesInObject(queryMsg, hash, fieldsNameMap, paramTypeMap);
			queryMsg.setId(queryID);
			
				Vector queryNameVec = (Vector)hash.get("FLD_NAME");
				if (queryNameVec != null){
					String  queryName = (String)queryNameVec.elementAt (row);
					item.setName(queryName);
				}
		
		} catch (DataSourceException e) {
			e.printStackTrace();
		}
		return queryMsg;*/
		////////////////////////////////////////////////////////
        // build query
        DataSource ds = DataManager.getSystemDS();
        Query q = new Query(QueryDobj.ENT_QUERY);
        q.addQueryCriterium(new QueryCriterium(QueryDobj.ENT_QUERY,
            QueryDobj.FLD_ID, QueryCriterium.OP_EQUALS,
            new Integer(queryID)));
        RecordSet rs;
		try {
			rs = ds.select(q);
			QueryDobj qdobj;
			while(rs != null && rs.next())
            {
                // get the query
                qdobj = (QueryDobj)rs.getCurrentObject();
                if(qdobj != null)
                	return qdobj;
            }
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getQueriesByViewID", e);
		}
		return null;
	}	

	public QueryDobj getQueryBuilderInfoByID(int queryID) throws MdnException
	{
        // build query
        DataSource ds = DataManager.getSystemDS();
        Query q = new Query(QueryDobj.ENT_QUERY);
        q.addQueryCriterium(new QueryCriterium(QueryDobj.ENT_QUERY,
            QueryDobj.FLD_ID, QueryCriterium.OP_EQUALS,
            new Integer(queryID)));
        RecordSet rs;
		try {
			rs = ds.select(q);
			QueryDobj qdobj;
			while(rs != null && rs.next()) {
                // get the query
                qdobj = (QueryDobj)rs.getCurrentObject();
                if(qdobj != null)
                	return qdobj;
            }
			//return queries;
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getQueriesByViewID", e);
		}
		return null;

	}	
	
	public QueryDobj getMessagingInfoByID(int queryID) throws MdnException
	{
        // build query
        DataSource ds = DataManager.getSystemDS();
        Query q = new Query(QueryDobj.ENT_QUERY);
        q.addQueryCriterium(new QueryCriterium(QueryDobj.ENT_QUERY,
            QueryDobj.FLD_ID, QueryCriterium.OP_EQUALS,
            new Integer(queryID)));
        RecordSet rs;
		try {
			rs = ds.select(q);
			QueryDobj qdobj;
			while(rs != null && rs.next())
            {
                // get the query
                qdobj = (QueryDobj)rs.getCurrentObject();
                if(qdobj != null)
                	return qdobj;
            }
			//return queries;
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getQueriesByViewID", e);
		}
		return null;
	}	
	
	public MdnMessageSeparator getMessageSeparator() throws MdnException{

	    DataSource ds    = DataManager.getDataSource(MdnMessageSeparator.ENT_MSG_SEPARATOR);
	    Query query = new Query(MdnMessageSeparator.ENT_MSG_SEPARATOR);

	    MdnMessageSeparator msgSeparator = null;
	    try {
	    	RecordSet rs    = ds.select(query);
	    	if(rs != null && rs.next()){
	    		msgSeparator = (MdnMessageSeparator) rs.getCurrentObject();
		    }
	    } catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getMessageSeparator()", e);
		}
	    return msgSeparator;
	}
	
	public UserReply getURPropertiesByID(int userReplyID) throws MdnException   
	{
	    DataSource ds    = DataManager.getDataSource(UserReply.ENT_USER_REPLY);
	    Query q = new Query(UserReply.ENT_USER_REPLY);

	    q.addQueryCriterium (new QueryCriterium (UserReply.ENT_USER_REPLY,
	    		UserReply.FLD_ID, QueryCriterium.OP_EQUALS,
			new Integer (userReplyID)));
		RecordSet rs;
		UserReply userReplyMessage = null;
		
		try {
			rs = ds.select (q);
		    if (rs != null && rs.next())
		    {
		    	userReplyMessage = (UserReply) rs.getCurrentObject();
		    }
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getUserRepliesById", e);
		}
		return userReplyMessage;
	}	

	public UserReply getUrQueryBuilderInfoByID(int userReplyID) throws MdnException
	{
	    DataSource ds    = DataManager.getDataSource(UserReply.ENT_USER_REPLY);
	    Query q = new Query(UserReply.ENT_USER_REPLY);

	    q.addQueryCriterium (new QueryCriterium (UserReply.ENT_USER_REPLY,
	    		UserReply.FLD_ID, QueryCriterium.OP_EQUALS,
			new Integer (userReplyID)));
		RecordSet rs;
		UserReply userReplyMessage = null;
		
		try {
			rs = ds.select (q);
		    if (rs != null && rs.next())
		    {
		    	userReplyMessage = (UserReply) rs.getCurrentObject();
		    }
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getUserRepliesById", e);
		}
		return userReplyMessage;
		
	}	

	public UserReply getUserReplyrMsgInfoByID(int userReplyID) throws MdnException
	{
	    DataSource ds    = DataManager.getDataSource(UserReply.ENT_USER_REPLY);
	    Query q = new Query(UserReply.ENT_USER_REPLY);

	    q.addQueryCriterium (new QueryCriterium (UserReply.ENT_USER_REPLY,
	    		UserReply.FLD_ID, QueryCriterium.OP_EQUALS,
			new Integer (userReplyID)));
		RecordSet rs;
		UserReply userReplyMessage = null;
		
		try {
			rs = ds.select (q);
		    if (rs != null && rs.next())
		    {
		    	userReplyMessage = (UserReply) rs.getCurrentObject();
		    }
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getUserRepliesById", e);
		}
		return userReplyMessage;

	}	

/*	private Object setResultValuesInObject(Object obj, Hashtable hash, Map fieldsNameMap, Map paramTypeMap){
		Iterator fieldsName = fieldsNameMap.keySet().iterator();
		
		while (fieldsName.hasNext ()){
			String fieldName = (String)fieldsName.next ();
			String methodName =  (String)fieldsNameMap.get (fieldName);
			Class paramType =  (Class)paramTypeMap.get (fieldName);

			Vector fieldValueVector = (Vector)hash.get(fieldName);
			
			if (fieldValueVector != null && fieldValueVector.size()>0){
				Object value = (Object)fieldValueVector.elementAt (0);
				Method m;
				try {
					m = (obj.getClass()).getDeclaredMethod(methodName, paramType);
					m.invoke(obj, value);
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				
			}
		}
		return obj;
	}
*/

	public WebServiceOperationDobj getWebServiceOperationByID(int id) throws MdnException{
		// build entity/field join query
		DataSource ds = DataManager.getSystemDS ();
		Query q = new Query (WebServiceOperationDobj.ENT_WEB_SERVICE_OPERATION);
		//q.addQueryEntity (FieldDobj.ENT_FIELD);
		q.addQueryCriterium (new QueryCriterium (WebServiceOperationDobj.ENT_WEB_SERVICE_OPERATION,
				WebServiceOperationDobj.FLD_ID, QueryCriterium.OP_EQUALS,
				new Integer (id)));
		RecordSet rs;
		try {
			rs = ds.select (q);
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getWebServiceOperationByID", e);
		}
		if (rs != null && rs.next ())
		{
			WebServiceOperationDobj ent = (WebServiceOperationDobj)rs.getCurrentObject ();
			return ent;
		}
		return null;
	}	
	
	public List<WebServiceOperationDobj> getAllWebServiceOperations(int projectId) throws MdnException{
        // select all datasources
        DataSource ds = DataManager.getSystemDS();
        Query q = new Query(WebServiceOperationDobj.ENT_WEB_SERVICE_OPERATION);
        QueryCriterium qc = new QueryCriterium(WebServiceOperationDobj.ENT_WEB_SERVICE_OPERATION,
        		WebServiceOperationDobj.FLD_PROJECT_ID, QueryCriterium.OP_EQUALS,
        		projectId);
        qc.setOrIsNull(true);
        q.addQueryCriterium(qc);           
        
        
        try {
			RecordSet rs = ds.select(q);
			List<WebServiceOperationDobj> list = new Vector<WebServiceOperationDobj>();
			while (rs.next()){
				WebServiceOperationDobj object = (WebServiceOperationDobj)rs.getCurrentObject();
				list.add(object);
	        }
			return list;
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException when getAllWebServiceOperations()", e);
		}
	}	

	public List<CustomField> getAllCustomFields() throws MdnException {
		DataSource ds    = DataManager.getDataSource(CustomField.ENT_CUSTOM_FIELD);
	    Query query = new Query(CustomField.ENT_CUSTOM_FIELD);

	    Vector<CustomField> customFields = new Vector<CustomField>();
	    try {
	    	RecordSet rs    = ds.select(query);
		    while (rs.next())
		    {
		    	CustomField customField = (CustomField) rs.getCurrentObject();
		    	customFields.add(customField);
		    }
	    } catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getAllCustomFields()", e);
		}
	    return customFields;
	    
	}
	public CustomField getCustomFieldById(int id) throws MdnException{
		DataSource ds    = DataManager.getDataSource(CustomField.ENT_CUSTOM_FIELD);
	    Query query = new Query(CustomField.ENT_CUSTOM_FIELD);
	    query.addQueryCriterium (new QueryCriterium (CustomField.ENT_CUSTOM_FIELD,
	    		CustomField.FLD_ID, QueryCriterium.OP_EQUALS, new Integer (id)));
	    
        RecordSet rs;
		try {
			rs = ds.select(query);
			CustomField customField;
			if(rs != null && rs.next()) {
				customField = (CustomField)rs.getCurrentObject();
                return customField;
            }
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getCustomFieldById", e);
		}
		return null;
	}
	public CustomField getCustomFieldByName(String name) throws MdnException{
		DataSource ds    = DataManager.getDataSource(CustomField.ENT_CUSTOM_FIELD);
	    Query query = new Query(CustomField.ENT_CUSTOM_FIELD);
	    query.addQueryCriterium (new QueryCriterium (CustomField.ENT_CUSTOM_FIELD,
	    		CustomField.FLD_NAME, QueryCriterium.OP_EQUALS, name));
	    
        RecordSet rs;
		try {
			rs = ds.select(query);
			CustomField customField;
			if(rs != null && rs.next()) {
				customField = (CustomField)rs.getCurrentObject();
                return customField;
            }
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getCustomFieldByName", e);
		}
		return null;
	}

	public boolean isCustomQueryNameDuplicate(String capitalName) throws MdnException{
		DataSource ds    = DataManager.getDataSource(CustomField.ENT_CUSTOM_FIELD);
	    Query query = new Query(CustomField.ENT_CUSTOM_FIELD);
	    query.addQueryCriterium (new QueryCriterium (CustomField.ENT_CUSTOM_FIELD,
	    		CustomField.FLD_CAPITAL_NAME, QueryCriterium.OP_EQUALS, capitalName));
	    
        RecordSet rs;
		try {
			rs = ds.select(query);
			CustomField customField;
			if(rs != null && rs.next()) {
				customField = (CustomField)rs.getCurrentObject();
                return true;//customField;
            }
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getCustomFieldByName", e);
		}
		return false;
	}
	
	public List<UserCustomField> getUserCustomFieldsByUserId(int userId) throws MdnException {
		DataSource ds    = DataManager.getDataSource(UserCustomField.ENT_USER_CUSTOM_FIELD);
	    Query query = new Query(UserCustomField.ENT_USER_CUSTOM_FIELD);
	    query.addQueryCriterium (new QueryCriterium (UserCustomField.ENT_USER_CUSTOM_FIELD,
	    		UserCustomField.FLD_USER_ID, QueryCriterium.OP_EQUALS, new Integer (userId)));
	    
	    Vector<UserCustomField> userCustomFields = new Vector<UserCustomField>();
	    try {
	    	RecordSet rs    = ds.select(query);
		    while (rs.next())
		    {
		    	UserCustomField customField = (UserCustomField) rs.getCurrentObject();
		    	userCustomFields.add(customField);
		    }
	    } catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getUserCustomFieldsByUserId()", e);
		}
	    return userCustomFields;
	}
	
	public List<UserCustomField> getCustomQueriseByCustomId(int customId) throws MdnException {
		DataSource ds    = DataManager.getDataSource(UserCustomField.ENT_USER_CUSTOM_FIELD);
	    Query query = new Query(UserCustomField.ENT_USER_CUSTOM_FIELD);
	    query.addQueryCriterium (new QueryCriterium (UserCustomField.ENT_USER_CUSTOM_FIELD,
	    		UserCustomField.FLD_CUSTOM_ID, QueryCriterium.OP_EQUALS, new Integer (customId)));
	    
	    Vector<UserCustomField> userCustomFields = new Vector<UserCustomField>();
	    try {
	    	RecordSet rs    = ds.select(query);
		    while (rs.next())
		    {
		    	UserCustomField customField = (UserCustomField) rs.getCurrentObject();
		    	userCustomFields.add(customField);
		    }
	    } catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getCustomQueriseByCustomId()", e);
		}
	    return userCustomFields;
	}	
	public UserCustomField getUserCustomByCustomId(int userId, int customId) throws MdnException{
		DataSource sysDs = DataManager.getSystemDS();
		QueryCriterium q1 = new QueryCriterium(UserCustomField.ENT_USER_CUSTOM_FIELD, UserCustomField.FLD_USER_ID, QueryCriterium.OP_EQUALS, new Integer (userId));
		QueryCriterium q2 = new QueryCriterium(UserCustomField.ENT_USER_CUSTOM_FIELD, UserCustomField.FLD_CUSTOM_ID, QueryCriterium.OP_EQUALS, new Integer (customId));
        Query q = new Query(q1,q2);
        
        RecordSet rs;
		try {
			rs = sysDs.select(q);
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getUserCustomByCustomId()", e);
		}
        // get the ds
		UserCustomField cf = null;
        if(rs != null && rs.next())
        {
        	cf = (UserCustomField)rs.getCurrentObject();
        }
		return cf;
	}

	public List<MessageLog> getMessageLogsByUserId(int userId) throws MdnException {
		DataSource ds    = DataManager.getDataSource(MessageLog.ENT_MESSAGE_LOG);
	    Query query = new Query(MessageLog.ENT_MESSAGE_LOG);
	    query.addQueryCriterium (new QueryCriterium (MessageLog.ENT_MESSAGE_LOG, MessageLog.FLD_SENDER_USER_ID, QueryCriterium.OP_EQUALS, new Integer (userId)));
        Sort sort = new Sort(MessageLog.ENT_MESSAGE_LOG, MessageLog.FLD_ID, "DESC");
        query.addSort(sort);

	    Vector<MessageLog> messageLogs = new Vector<MessageLog>();
        RecordSet rs;
		try {
			rs = ds.select(query);
			MessageLog messageLog;
			int i=1;
			while (rs.next() && i<=24){
				messageLog = (MessageLog)rs.getCurrentObject();
				messageLogs.add(messageLog);
				i++;
            }
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getMessageLogsByUserId", e);
		}
		return messageLogs;
	    
	}
	public MessageLog getMessageLogById(int msgLogId) throws MdnException {
		DataSource ds    = DataManager.getDataSource(MessageLog.ENT_MESSAGE_LOG);
	    Query query = new Query(MessageLog.ENT_MESSAGE_LOG);
	    query.addQueryCriterium (new QueryCriterium (MessageLog.ENT_MESSAGE_LOG,
	    		MessageLog.FLD_ID, QueryCriterium.OP_EQUALS, new Integer (msgLogId)));
	    
        RecordSet rs;
		try {
			rs = ds.select(query);
			MessageLog msgLog;
			if(rs != null && rs.next()) {
				msgLog = (MessageLog)rs.getCurrentObject();
                return msgLog;
            }
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getMessageLogById", e);
		}
		return null;
		
	}
	
	public MessagingSettingDetails getGuestMsgInfo(String messagingSeverType) throws MdnException {
		DataSource ds    = DataManager.getDataSource(MessagingSettingDetails.ENT_MSG_SETT_INFO);
	    Query query = new Query(MessagingSettingDetails.ENT_MSG_SETT_INFO);
	    query.addQueryCriterium (new QueryCriterium (MessagingSettingDetails.ENT_MSG_SETT_INFO,
	    		MessagingSettingDetails.FLD_TYPE, QueryCriterium.OP_EQUALS, messagingSeverType));	    

	    RecordSet rs;
		try {
			rs = ds.select(query);
			MessagingSettingDetails messagingSettingDetails;
			if(rs != null && rs.next()) {
				messagingSettingDetails = (MessagingSettingDetails)rs.getCurrentObject();
                return messagingSettingDetails;
            }
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getGuestMsgInfo", e);
		}
		return null;
	}
	
	
	public List<BlockContacts> getBlockContacts() throws MdnException {
		DataSource ds    = DataManager.getDataSource(BlockContacts.ENT_BLOCK_CONTACTS);
	    Query query = new Query(BlockContacts.ENT_BLOCK_CONTACTS);

	    Vector<BlockContacts> blockContacts = new Vector<BlockContacts>();
        RecordSet rs;
		try {
			rs = ds.select(query);
			BlockContacts blockContact;
			int i=1;
			while (rs.next()){
				blockContact = (BlockContacts)rs.getCurrentObject();
				blockContacts.add(blockContact);
				i++;
            }
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getBlockContacts", e);
		}
		return blockContacts;
	}	
	
	public Map<String, BlockContacts> getBlockContactsByType(String type) throws MdnException {
		DataSource ds    = DataManager.getDataSource(BlockContacts.ENT_BLOCK_CONTACTS);
	    Query query = new Query(BlockContacts.ENT_BLOCK_CONTACTS);
	    query.addQueryCriterium (new QueryCriterium (BlockContacts.ENT_BLOCK_CONTACTS,
	    		MessagingSettingDetails.FLD_TYPE, QueryCriterium.OP_EQUALS, type));	    		
		
	    //Vector<BlockContacts> blockContacts = new Vector<BlockContacts>();
	    Map<String, BlockContacts> blockContacts = new HashMap<String, BlockContacts>();
        RecordSet rs;
		try {
			rs = ds.select(query);
			BlockContacts blockContact;
			while (rs.next()){
				blockContact = (BlockContacts)rs.getCurrentObject();
				blockContacts.put(blockContact.getContact(), blockContact);
            }
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getBlockContacts", e);
		}
		return blockContacts;
	}		
	
	public BlockContacts getBlockContact(String type, String contact) throws MdnException {
		DataSource ds = DataManager.getSystemDS();
		QueryCriterium q1 = new QueryCriterium(BlockContacts.ENT_BLOCK_CONTACTS, BlockContacts.FLD_TYPE, QueryCriterium.OP_EQUALS, type);
		QueryCriterium q2 = new QueryCriterium(BlockContacts.ENT_BLOCK_CONTACTS, BlockContacts.FLD_CONTACT, QueryCriterium.OP_EQUALS, contact);
        Query q = new Query(q1,q2);
        
	    RecordSet rs;
		try {
			rs = ds.select(q);
			BlockContacts blockContacts;
			if(rs != null && rs.next()) {
				blockContacts = (BlockContacts)rs.getCurrentObject();
                return blockContacts;
            }
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getBlockContact", e);
		}
		return null;
	}	
	
	public BlockContacts getBlockContact(int id) throws MdnException {
		DataSource ds = DataManager.getSystemDS();
		QueryCriterium q1 = new QueryCriterium(BlockContacts.ENT_BLOCK_CONTACTS, BlockContacts.FLD_ID, QueryCriterium.OP_EQUALS, id);
        Query q = new Query(q1);
        
	    RecordSet rs;
		try {
			rs = ds.select(q);
			BlockContacts blockContacts;
			if(rs != null && rs.next()) {
				blockContacts = (BlockContacts)rs.getCurrentObject();
                return blockContacts;
            }
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getBlockContactByID", e);
		}
		return null;
	}
	
	public TemporaryBlockContacInfo getTempBlockContacts() throws MdnException {
		DataSource ds    = DataManager.getDataSource(TemporaryBlockContacInfo.ENT_TEMP_BLOCK_INFO);
	    Query query = new Query(TemporaryBlockContacInfo.ENT_TEMP_BLOCK_INFO);

	    RecordSet rs;
		try {
			rs = ds.select(query);
			TemporaryBlockContacInfo tempInfo;
			if(rs != null && rs.next()) {
				tempInfo = (TemporaryBlockContacInfo)rs.getCurrentObject();
                return tempInfo;
            }
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException in getTempBlockContacts", e);
		}
		return null;
	}
	
	public static void dataBaseUpdate() throws MdnException {
        DataSource sourceDs = DataManager.getSystemDS();
		if (sourceDs == null)
			throw new RuntimeException ("Source DataSource not found" );
		
/*		String sql = "SELECT FLD_NAME, FLD_DESCRIPTION, FLD_TYPE, FLD_PARENTID, " +
				"FLD_DB_ID, FLD_TIMEOUT, FLD_DS_STATUS, FLD_WS_ID " +
				" FROM TBL_QUERY " +
				" WHERE FLD_ID = 1";*/
		
		String sql = "CREATE TABLE TBL_TEST ( FLD_ID BIGINT, FLD_NAME VARCHAR(56))";
		Hashtable hash;
		try {
			hash = sourceDs.execDirectSQL (sql);
			
/*			Collection vals = hash.values ();
			Iterator iter = vals.iterator ();
			Vector col = (Vector)iter.next ();
			
			
			Map fieldsNameMap = new HashMap();//field Name, method Name
			Map paramTypeMap = new HashMap();//field Name, parameter type
			
			fieldsNameMap.put("FLD_NAME", "setName");
			paramTypeMap.put("FLD_NAME", String.class);
			
			fieldsNameMap.put("FLD_DESCRIPTION", "setDescription");
			paramTypeMap.put("FLD_DESCRIPTION", String.class);
			
			fieldsNameMap.put("FLD_TYPE", "setType");
			paramTypeMap.put("FLD_TYPE", String.class);
			
			fieldsNameMap.put("FLD_PARENTID", "setViewOrTableId");
			paramTypeMap.put("FLD_PARENTID", int.class);
			
			fieldsNameMap.put("FLD_DB_ID", "setDatabaseId");
			paramTypeMap.put("FLD_DB_ID", int.class);
			
			fieldsNameMap.put("FLD_TIMEOUT", "setTimeout");
			paramTypeMap.put("FLD_TIMEOUT", String.class);

			fieldsNameMap.put("FLD_DS_STATUS", "setDatasourceStatus");
			paramTypeMap.put("FLD_DS_STATUS", int.class);
			
			fieldsNameMap.put("FLD_WS_ID", "setWebServiceId");
			paramTypeMap.put("FLD_WS_ID", int.class);
			
			queryMsg = (QueryDobj)setResultValuesInObject(queryMsg, hash, fieldsNameMap, paramTypeMap);
			queryMsg.setId(queryID);
			
				Vector queryNameVec = (Vector)hash.get("FLD_NAME");
				if (queryNameVec != null){
					String  queryName = (String)queryNameVec.elementAt (row);
					item.setName(queryName);
				}
*/
		} catch (DataSourceException e) {
			e.printStackTrace();
		}
		//return queryMsg;
	}
}
