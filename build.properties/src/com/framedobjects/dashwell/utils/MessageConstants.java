package com.framedobjects.dashwell.utils;

import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class MessageConstants {
  private static String getGuiValueForElement(Element node, String elementName){
	    String value = "";
	    if (elementName == null )
	    	return value;
	    
	    if(node != null){
		    Element conn = node.getChild(elementName);
		    if (conn != null){
		      value = conn.getAttributeValue("label");
		    }
	    }
	    
	    return value;
	  }
	  
	  private static Element getXMLLanguageFile(String file) {
	    Element root = null;
	    SAXBuilder builder = new SAXBuilder();
	    try {
	      Document doc = builder.build(Constants.LANGUAGE_FILE_URL + file);
	      root = doc.getRootElement();
	    } catch (JDOMException e) {
	        e.printStackTrace();
	    } catch (IOException ioe){
		      ioe.printStackTrace();
	    }
	    return root;
	  }
	  
	  public static String getMessage(String languageFile, String key){
	    Element guiDef = getXMLLanguageFile(languageFile);
	    String dataSources = getGuiValueForElement(guiDef, key);
		return dataSources;
	  }
	  
	  //General Messages keys
	  public final static String SUCCESSFUL = "msg-successful";
	  public final static String DB_ERROR = "msg-db-error";
	  public final static String DUPLICATE_NAME = "msg-duplicate-name";
	  public final static String DUPLICATE_NAME_IN_DB = "msg-duplicate-name-in-bin";
	  
	  //Users & Groups Messages keys
	  public final static String PASSWORD_NOT_MATCH = "msg-password-not-match";
	  public final static String NEED_ADMIN = "msg-need-admin";
	  public final static String MISSING_PRIVILEGE = "msg-missing-privilege";
	  public final static String MISSING_USERNAME = "msg-missing-username";
	  public final static String MISSING_PASSWORD = "msg-missing-password";
	  public final static String MISSING_GROUP_NAME = "msg-missing-group-name";
	  public final static String DUPLICATE_USERNAME = "msg-duplicate-username";
	  public final static String DUPLICATE_GROUP = "msg-duplicate-group";
	  public final static String DUPLICATE_GUEST_GROUP = "msg-duplicate-guest-group";
	  public final static String MISSING_FIRST_NAME = "msg-missing-first-name";
	  public final static String MISSING_LAST_NAME = "msg-missing-last-name";
	  public final static String DUPLICATE_EMAIL = "msg-duplicate-email";
	  public final static String DUPLICATE_MOBILE = "msg-duplicate-mobile";
	  public final static String DUPLICATE_IM = "msg-duplicate-im";
	  public final static String LICENSE_RESTRICT = "msg-license-restrict";
	  public final static String INVALID_LICENSE = "msg-invalid-license";
	  public final static String MISSING_CUSTOM_NAME = "msg-missing-custom-name";
	  public final static String MISSING_CUSTOM_PARAM = "msg-missing-custom-param";
	  public final static String NOT_ALLOW_PUBLIC_GROUP = "msg-not-allow-public-group";
	  
	  //Setting Messages keys
	  //IM
	  public final static String SUCCESSFUL_IM = "msg-successful-im-setting";
	  public final static String DUPLICATE_IM_PROVIDER = "msg-duplicate-im-provider";	  
	  public final static String DUPLICATE_IM_PROVIDER_IN_RECYCLE_BIN = "msg-duplicate-im-provider-in-bin";
	  public final static String MISSING_IM_NAME = "msg-missing-im-name";
	  public final static String MISSING_IM_PROVIDER = "msg-missing-im-provider";
	  //Email
	  public final static String SUCCESSFUL_EMAIL = "msg-successful-mail-setting";
	  public final static String INVALID_EMAIL = "msg-invalid-email";	
	  public final static String MISSING_IMAP_HOST = "msg-missing-imap-host";			
	  public final static String MISSING_IMAP_USERNAME = "msg-missing-imap-username";			
	  public final static String MISSING_IMAP_PASSWORD = "msg-missing-imap-password";				
	  public final static String MISSING_SMTP_HOST = "msg-missing-smtp-host";	
	  public final static String MISSING_SMTP_USERNAME = "msg-missing-smtp-username";			
	  public final static String MISSING_SMTP_PASSWORD = "msg-missing-smtp-password";
	  public final static String DUPLICATE_EMAIL_ADDRESS = "msg-duplicate-address";
	  
	  //SMS
	  public final static String SUCCESSFUL_SMS = "msg-successful-sms-setting";
	  //Message Controls
	  public final static String MISSING_TYPE = "msg-missing-type";		  
	  public final static String MISSING_CONTACT = "msg-missing-contact";
	  public final static String DUPLICATE_BLOCK_CONTACT = "msg-duplicate-block";
	  public final static String NO_SELECT_QUERY = "no-select-query";
	  
	  //Message Controls
	  public final static String NO_SPACE = "msg-no-space";	
	  public final static String INVALID_SEARCH_EMAIL = "msg-invalid-search-email";
	  public final static String INVALID_SEARCH_IM = "msg-invalid-search-im";
	  public final static String INVALID_SEARCH_SMS = "msg-invalid-search-sms";
	  public final static String SEARCH_NO_RESULT = "search-no-result-found";
	  public final static String SEARCH_RESULT_FORMAT_KEY = "search-result-format-keyword";
	  public final static String SEARCH_RESULT_FORMAT_NAME = "search-result-format-name";
	  public final static String SEARCH_RESULT_FORMAT_DESC = "search-result-format-desc";
	  public final static String SEARCH_RESULT_FORMAT_UR = "search-result-format-UserReply";
	  public final static String SEARCH_RESULT_FORMAT_EMAIL_SERVER = "search-result-format-email-server";
	  
	  //SMPP
	  public final static String MISSING_NUMBER = "msg-source-number";
	  public final static String DUPLICATE_NUMBER = "duplicate-msg-source-number";
	  public final static String MISSING_HOST = "msg-missing-host";		  
	  public final static String MISSING_PORT = "msg-missing-port";
	  public final static String SUCCESSFUL_SMPP = "msg-successful-smpp-setting";
	  public final static String GSM_NUMBER = "gsm-number";
	  
	  //Queries
	  public final static String SELECT_VIEW_LABEL = "lbl-select-view";
	  public final static String SELECT_TABLE_LABEL = "lbl-select-table";
	  public final static String MISSING_QUERY_NAME = "msg-missing-query-name";
	  public final static String MISSING_DB = "msg-missing-db";
	  public final static String MISSING_VIEW_TBL = "msg-missing-view-tbl";
	  public final static String WRONG_WRONG_EMAIL_KEY1 = "msg-wrong-email-key1";
	  public final static String WRONG_WRONG_EMAIL_KEY2 = "msg-wrong-email-key2";
	  public final static String WRONG_WRONG_EMAIL_KEY3 = "msg-wrong-email-key3";
	  public final static String WRONG_WRONG_EMAIL_KEY4 = "msg-wrong-email-key4";
	  
	  public final static String WRONG_WRONG_SMS_KEY1 = "msg-wrong-sms-key1";
	  public final static String WRONG_WRONG_SMS_KEY2 = "msg-wrong-sms-key2";
	  public final static String WRONG_WRONG_SMS_KEY3 = "msg-wrong-sms-key3";
	  public final static String WRONG_WRONG_SMS_KEY4 = "msg-wrong-sms-key4";
	  public final static String WRONG_WRONG_SMS_KEY5 = "msg-wrong-sms-key5";
	  
	  public final static String WRONG_WRONG_IM_KEY1 = "msg-wrong-im-key1";	  
	  public final static String WRONG_WRONG_IM_KEY2 = "msg-wrong-im-key2";
	  
	  public final static String MISSING_VIEW = "msg-missing-view";
	  public final static String MISSING_ENTER_TXT = "msg-missing-enter-txt";
	  public final static String MISSING_ENTER_SMS_KEY = "msg-enter-sms-keyword";
	  public final static String MISSING_ENTER_IM_KEY = "msg-enter-im-keyword";
	  public final static String INVALID_KEY = "msg-invalid-keyword";

	  //Default Response Message format
	  public final static String SUCCESS_QUARY_MSG_RESPONSE = "success-msg-response";
	  public final static String FAILED_QUARY_MSG_RESPONSE = "failed-msg-response";
	  public final static String NO_RESULT_TEST_RESPONSE = "no-result-response";
	  public final static String NO_RESPONSE_FORMAT = "no-response-defined";
	  public final static String NO_PERMISSION_UP_RESP = "no-permission-up-resp";
	  public final static String NO_PERMISSION_IN_RESP = "no-permission-in-resp";
	  
	  //User Replies
	  public final static String MISSING_UR_MSG_KEYWORD= "missing-ur-msg-keyword";
	  public final static String DUPLICATE_UR_MSG_KEYWORD= "duplicate-ur-msg-keyword";
	  
	  
	  //GSM validation
	  public final static String DUPLICATE_GSM_NUM = "msg-duplicate-gsm-number"; 
	  public final static String MISSING_GSM_NUM = "msg-missing-gsm-number";	
	  public final static String MISSING_COMM = "msg-missing-comm";		
	  public final static String INVALID_COMM = "msg-invalid-comm";
	  public final static String MISSING_BAUDRATE = "msg-missing-baudrate";				  
	  
	  public static int limitationCount = 200;
}
