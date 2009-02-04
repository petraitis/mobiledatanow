package com.framedobjects.dashwell.utils;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

public class Constants {
  
  static Logger logger = Logger.getLogger(Constants.class.getName());
  
  //public static String LANGUAGE_FILE_UPLOAD_URL = null;
  //public static String DRIVER_FILE_UPLOAD_URL = null;
  public static String LANGUAGE_FILE_URL = null;
  public static String LANGUAGE_FILE_PATH = null;
  public static String DATA_AGENT = null;
  public static String DATA_AGENT_DB = "DB";
  public static String DATA_AGENT_RMI = "RMI";
  
  static {
    try {
      ResourceBundle resources = ResourceBundle
          .getBundle("com.framedobjects.dashwell.config");
      LANGUAGE_FILE_URL = resources.getString("language_file_url");
      LANGUAGE_FILE_PATH = resources.getString("language_file_path");
      //LANGUAGE_FILE_UPLOAD_URL = resources.getString("language_file_upload_url");
      //DRIVER_FILE_UPLOAD_URL = resources.getString("driver_file_upload_url");
      DATA_AGENT = resources.getString("data_agent_type");
    } catch (MissingResourceException mre) {
      logger.warn("Constants could not find resource bundle: "
          + mre.getMessage());
    }
  }

  //MODE
  public final static int NEW_MODE = 0;
  public final static int EDIT_MODE = 1;
  
  // Following are all the request parameters coming from OpenLaszlo application.
  public final static String REQUEST_PARAM_ACTION = "action";
  public final static String REQUEST_PARAM_DB_NAME = "db-name";
  public final static String REQUEST_PARAM_PASSWORD = "password";
  public final static String REQUEST_PARAM_USERNAME = "username";
  
  public final static String SESSION_LANGUAGE_FILE = "language";
  public final static String SESSION_LOGIN_USER = "loginUser";
  public final static String SESSION_NEW_LANGUAGE = "newLanguage";
  public final static String SESSION_NEW_DRIVER = "newDriver";
  public final static String SESSION_NEW_PROJECT = "newProject";
  public final static String SESSION_CONNECTION_WIZARD = "dbWizard";
  
  // Following are all ACTION types.
  public final static String ACTION_CSS ="css";
  public final static String ACTION_DB_CONNECTIONS ="dbConn";
  public final static String ACTION_DB_TABLES_TREE ="db-tables-tree";
  public final static String ACTION_GUI_DEFINITION ="gui-definition";
  public final static String ACTION_LOGIN = "login";
  public final static String ACTION_NAVIGATION = "navigation";
  public final static String ACTION_SCREEN_DEF = "screenDef";
  public final static String ACTION_USERGROUPS_TREE = "usergroups";
  
  public final static String MARKED_AS_NOT_DELETED = "0";
  public final static String MARKED_AS_RECYCLED = "1";
  
  public final static int MARKED_AS_NOT_DELETED_INT = 0;
  public final static int MARKED_AS_RECYCLED_INT = 1;
  public final static int MARKED_AS_DELETED_INT = 2;
  public final static int MARKED_AS_CLEARED_INT = 3;

  
  // Following are return flags for interaction in system.
  public final static int OK = 0;
  public final static int ERR_DB = -2;
  public final static int ERR_INVALID_LOGIN = -3;
  
  	public static final String DB_DRIVER_EXCEL = "Microsoft Excel Driver";
	public static final String DB_DRIVER_JDBC_ODBC = "JDBC/ODBC Bridge";
	public static final String DB_DRIVER_MYSQL = "MySQL Connector/JDriver";
	public static final String DB_DRIVER_MSSQL = "Microsoft MMSQL Driver";

	// Following are all Instant Messenger types.
	public final static String AIM_OSCAR_IM_TYPE_NAME ="AIM-Oscar";
	public final static String AIM_OSCAR_IM_TYPE_ID = "1";

	public final static String ICQ_IM_TYPE_NAME ="ICQ";
	public final static String ICQ_IM_TYPE_ID = "2";
	
	public final static String YAHOO_IM_TYPE_NAME ="Yahoo";
	public final static String YAHOO_IM_TYPE_ID = "3";

	public final static String MSN_IM_TYPE_NAME ="MSN";
	public final static String MSN_IM_TYPE_ID = "4";

	public final static String GT_IM_TYPE_NAME ="Google Talk";
	public final static String GT_IM_TYPE_ID = "5";

	public final static String JABBER_IM_TYPE_NAME ="Jabber";
	public final static String JABBER_IM_TYPE_ID = "6";

	// Following are Instant Messenger status.
	public final static String DISCONNECT_STATUS ="0";
	public final static String CONNECT_STATUS ="1";
	
	// MDN IM Group Name
	public final static String IM_MDN_GROUP_NAME ="MDN Group";
	
	//Email 
	public final static String SMTP_MAIL = "smtp";
	public final static String SMTP_HOST_PROP = "mail.smtp.host";
	public final static String SMTP_PORT_PROP = "mail.smtp.port";
	public final static String IMAP = "imap";
	public final static String SMTP = "smtp";
	public final static String INBOX = "inbox";
	public final static int MAX_MSG_NUM = 200;
	
	//SSL
	public final static String SMTP_USER_PROP = "mail.smtp.user";
	public final static String SMTP_AUTH_PROP = "mail.smtp.auth";
	public final static String SMTP_STARTTLS_PROP = "mail.smtp.starttls.enable";
	public final static String SMTP_SOCKET_FACTORY_PORT_PROP = "mail.smtp.socketFactory.port";
	public final static String SMTP_SOCKET_FACTORY_CLASS_PROP = "mail.smtp.socketFactory.class";
	public final static String SMTP_TRANSPORT_PROTOCOL_PROP = "mail.transport.protocol";
	public final static String SMTP_SOCKET_FACTORY_FALLBACK_PROPS = "mail.smtp.socketFactory.fallback";
	public final static String SMTP_QUITWAIT_PROP = "mail.smtp.quitwait";	
	
	//Email Encrypted Type (For IMAP and SMPP)
	public final static int NONE = 1;
	public final static int SSL = 2;
	public final static int TLS = 3;
	public final static int AUTO = 4;
	
	//Messaging Query
	public final static String VIEWS_LABEL = "Select View:";
	public final static String TABLE_LABEL = "Select Table:";
	public final static String INSERT_QUERY_TYPE = "insert";
	public final static String UPDATE_QUERY_TYPE = "update";
	public final static String SELECT_QUERY_TYPE = "select";

	public final static int SELECT_QUERY_TYPE_NUM = 1;
	public final static int UPDATE_QUERY_TYPE_NUM = 2;
	public final static int INSERT_QUERY_TYPE_NUM = 3;	
	
	public final static String SPACE_ID = "0";
	public final static String COMMA_ID = "1";
	public final static String SEMICOLON_ID = "2";
	public final static String OTHER_ID = "3";
	
	public final static String SMS_MSG_TYPE = "SMS";
	public final static String IM_MSG_TYPE  = "IM";
	public final static String EMAIL_MSG_TYPE  = "Email";
	
	public final static String UNDEFINED  = "undefined";
	
	public final static String DATE_TIME_FORMAT  = "yyyy-MM-dd hh:mm";	
	
	public static final int NO_PERMISSION_INSERT = -20;
	public static final int NO_PERMISSION_UPDATE = -25;
	
	public final static String DS_DB_STATUS  = "1";
	public final static String DS_WS_STATUS  = "2";	
}

