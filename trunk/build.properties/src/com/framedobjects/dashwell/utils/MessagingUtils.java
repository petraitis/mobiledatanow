package com.framedobjects.dashwell.utils;
/**
 * Utility class for MDN Messaging Services(IM/SMS/SMPP/Email).
 * This class provides general functionality to handle Income and outcome messages in MDN
 * 
 * @author Adele
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import wsl.fw.datasource.DataObject;
import wsl.fw.exception.MdnException;
import wsl.fw.security.Group;
import wsl.fw.security.GroupMembership;
import wsl.fw.security.User;
import wsl.mdn.dataview.DataView;
import wsl.mdn.dataview.DataViewField;
import wsl.mdn.dataview.EntityDobj;
import wsl.mdn.dataview.GroupDataView;
import wsl.mdn.dataview.GroupTablePermission;
import wsl.mdn.dataview.QueryCriteriaDobj;
import wsl.mdn.dataview.QueryDobj;
import wsl.mdn.dataview.WebServiceOperationDobj;
import wsl.mdn.mdnmsgsvr.MdnMessageSeparator;

import com.framedobjects.dashwell.db.DataAgentFactory;
import com.framedobjects.dashwell.db.IDataAgent;
import com.framedobjects.dashwell.mdnEmail.MdnMailService;
import com.framedobjects.dashwell.mdninstantmsg.MDNIMServer;
import com.framedobjects.dashwell.mdnsms.MdnSmppGatewayServer;
import com.framedobjects.dashwell.mdnsms.MdnSmsServer;
import com.framedobjects.dashwell.utils.Constants;
import com.framedobjects.dashwell.utils.webservice.ParamHelper;
import com.framedobjects.dashwell.utils.webservice.ParamListItem;
import com.framedobjects.dashwell.utils.webservice.WebServiceResultRow;

public class MessagingUtils {
	
	private static Map paramHelperMap = new HashMap(); /* <key = wsdlurl:operation , value = ParamHelper> */
	
	/**
	 * 
	 * @return Map
	 */
	public static Map getParamHelperMap(){
		return paramHelperMap;
	}
	
	/**
	 * 
	 * @param paramHelperMap
	 * @return Map
	 */
	public static Map setParamHelperMap(Map paramHelperMap){
		return paramHelperMap;
	}	
	
	/**
	 * defined IM types as a final variable
	 * 
	 * @return Map
	 */
	  private static Map getMapConnectionType()
	  {
		  Map connectionTypeMap = new HashMap();
		  connectionTypeMap.put(Constants.AIM_OSCAR_IM_TYPE_ID, Constants.AIM_OSCAR_IM_TYPE_NAME);
		  connectionTypeMap.put(Constants.ICQ_IM_TYPE_ID, Constants.ICQ_IM_TYPE_NAME);
		  connectionTypeMap.put(Constants.YAHOO_IM_TYPE_ID, Constants.YAHOO_IM_TYPE_NAME);
		  connectionTypeMap.put(Constants.MSN_IM_TYPE_ID, Constants.MSN_IM_TYPE_NAME);
		  connectionTypeMap.put(Constants.JABBER_IM_TYPE_ID, Constants.JABBER_IM_TYPE_NAME);
		  connectionTypeMap.put(Constants.GT_IM_TYPE_ID, Constants.GT_IM_TYPE_NAME);
		  return connectionTypeMap;
	  }
	  /**
	   * 
	   * @param typeId (IM type)
	   * @return String IM name
	   */
	  public static String getConnectionTypeNameByID(int typeId){
		  Map map = getMapConnectionType();
		  String typeName = (String)map.get(String.valueOf(typeId));
		  return typeName;
	  }
	  
	  public static int getConnectionIdByName(String connectionName){
		  Map map = getMapConnectionType();
		  List typesList = new ArrayList(map.keySet());
		  for(int i=0; i<typesList.size(); i++) {
			  String keyId = (String)typesList.get(i);
			  String name = (String)map.get(keyId);
			  if(name.equalsIgnoreCase(connectionName))
				  return Integer.parseInt(keyId);
		  }
		 return 0;//never reach here
	  }

	  /**
	   * 
	   * @param querytypeStr
	   * @return int Qyery Type Number 
	   */
		public static int getQueryTypeNumber(String querytypeStr){
			if(querytypeStr.equalsIgnoreCase(Constants.SELECT_QUERY_TYPE))
				return Constants.SELECT_QUERY_TYPE_NUM;//1
			else if(querytypeStr.equalsIgnoreCase(Constants.UPDATE_QUERY_TYPE))
				return Constants.UPDATE_QUERY_TYPE_NUM;//2
			else if(querytypeStr.equalsIgnoreCase(Constants.INSERT_QUERY_TYPE))
				return Constants.INSERT_QUERY_TYPE_NUM;//3
			else 
				return Constants.SELECT_QUERY_TYPE_NUM;//1 default value
		}

		/**
		 * 
		 * @param querytypeNum
		 * @return query type name
		 */
		public static String getQueryTypeStr(int querytypeNum){
			if(querytypeNum == Constants.SELECT_QUERY_TYPE_NUM)
				return Constants.SELECT_QUERY_TYPE;/*select*/
			else if(querytypeNum == Constants.UPDATE_QUERY_TYPE_NUM)
				return Constants.UPDATE_QUERY_TYPE;/* update */
			else if(querytypeNum == Constants.INSERT_QUERY_TYPE_NUM)
				return Constants.INSERT_QUERY_TYPE;/* insert */
			else 
				return Constants.SELECT_QUERY_TYPE;/* select */
		}
		
		/**
		 * The default MDN messaging separators are space (0), Comma (1), semicolon (2)
		 * For another separator character which would be defined by user using 3
		 * @param seperatorStr
		 * @return String
		 */
		public static String getSeperatorNum(String seperatorStr){
			if(seperatorStr == null || seperatorStr.equals(""))
				return Constants.SPACE_ID;//0
			else if(seperatorStr.equalsIgnoreCase(","))
				return Constants.COMMA_ID;//1
			else if(seperatorStr.equalsIgnoreCase(";"))
				return Constants.SEMICOLON_ID;//2
			else 
				return Constants.OTHER_ID;//3 default value is space
		}
		
		/**
		 * Prepare response message to display as a test result in admin screen by using saved specific response format for the DB Status query
		 * 
		 * @param responseFormat
		 * @param fields
		 * @param obj
		 * @return String Response text
		 */
		public static String responseMassageParser(String responseFormat, Vector fields, DataObject obj){
			List indexList = new ArrayList();
			String str = responseFormat;
			int indexKey = responseFormat.indexOf("%");
			while(str.contains("%"))
	    	{
				int index = str.indexOf("%");
	    		str = str.substring(index+1);
	    		int ii = str.indexOf("%");
	    		indexList.add(String.valueOf(indexKey));
	    		indexKey = indexKey + (ii+1);
	    	}
			
			List filedListFromResponse = new ArrayList();
	    	for(int j = 0; j < indexList.size(); ){
	    		int firstIndex = Integer.parseInt(indexList.get(j).toString()) + 1;
	    		int secondIndex = Integer.parseInt(indexList.get(j+1).toString());
	    		String field =  responseFormat.substring(firstIndex,secondIndex);

	    		filedListFromResponse.add(field);
	    		j=j+2;
	    	}
	    	
	    	List aggregateFields = filedListFromResponse;
	    	
	    	String newStr ="";
    		for(int i = 0; i < fields.size(); i++)
    		{
    			if (obj != null) {
    				String fldName = ((DataViewField)fields.elementAt(i)).getName();
    				
    				Object val = obj.getObjectValue(fldName);
    				if(val == null || val.toString().equals(""))
    					val = "-";
    				
    				if(filedListFromResponse.contains(fldName)){
    					aggregateFields.remove(fldName);
    					
    					String value= val.toString();
    		    		if(newStr.equals(""))
    		    			newStr = responseFormat.replace("%" + fldName + "%", value);
    		    		else{
    		    			newStr = newStr.replace("%" + fldName + "%", value);	
    		    		}
    		    		responseFormat = newStr;
    				} else {
    					newStr = responseFormat;
    				}
    			}	   
    		}
    		
    		for(int j=0 ; j< aggregateFields.size() ; j++){
    			String aggFldName = (String)aggregateFields.get(j);
    			Object val = obj.getObjectValue(aggFldName);
    			if(val != null){
    				String value = val.toString();
    				if(filedListFromResponse.contains(aggFldName)){
    		    		if(newStr.equals(""))
    		    			newStr = responseFormat.replace("%" + aggFldName + "%", value);
    		    		else{
    		    			newStr = newStr.replace("%" + aggFldName + "%", value);	
    		    		}
    		    		responseFormat = newStr;
    				}    				
    			}
    		}
			return newStr;
		}
		
		
		/**
		 * Prepare response message to display as a test result in admin screen by using saved specific response format for the WS Status query
		 * 
		 * @param responseFormat
		 * @param fields
		 * @param rowObj
		 * @return
		 */
		public static String responseMassageParserWS(String responseFormat, Object fields[] , WebServiceResultRow rowObj){
			List indexList = new ArrayList();
			String str = responseFormat;
			int indexKey = responseFormat.indexOf("%");
			while(str.contains("%"))
	    	{
				int index = str.indexOf("%");
	    		str = str.substring(index+1);
	    		int ii = str.indexOf("%");
	    		indexList.add(String.valueOf(indexKey));
	    		indexKey = indexKey + (ii+1);
	    	}
			
			List filedListFromResponse = new ArrayList();
	    	for(int j = 0; j < indexList.size(); ){
	    		int firstIndex = Integer.parseInt(indexList.get(j).toString()) + 1;
	    		int secondIndex = Integer.parseInt(indexList.get(j+1).toString());
	    		String field =  responseFormat.substring(firstIndex,secondIndex);
	    		filedListFromResponse.add(field);
	    		
	    		j=j+2;
	    	}
	    	
	    	String newStr ="";
	    	
	    	for(int a = 0; a < fields.length; a++)
    		{
	    		Object val = null;
    			String fieldName = "";
    			List<ParamListItem> paramsRowObj = rowObj.getParamItemList();
				for(ParamListItem resultItem : paramsRowObj )
				{
        			Object resultItemValue = resultItem.getValue();
        			if(resultItemValue.getClass().isArray()){
        				 Object[] resultItemArray = (Object[])resultItemValue;
        				for(int i = 0; i< resultItemArray.length; i++){
        					Object paramItem = resultItemArray[i];
        					val = paramItem;
        					fieldName = resultItem.getLabel();
        				}
        			}else{
        				//System.out.println(resultItem.getLabel() + " :" + resultItem.getValue());
        				val = resultItemValue;
        				fieldName = resultItem.getLabel();
        			}
    				if(val == null || val.toString().equals(""))
    					val = "-";        			
    				if(filedListFromResponse.contains(fieldName)){
    					
    					String value= val.toString();
    		    		if(newStr.equals(""))
    		    			newStr = responseFormat.replace("%" + fieldName + "%", value);
    		    		else{
    		    			newStr = newStr.replace("%" + fieldName + "%", value);	
    		    		}
    		    		responseFormat = newStr;
    				} else {
    					newStr = responseFormat;
    				}        			
				}
				
    		}
			return newStr;
		}		
		
		/**
		 * Provide response message to send to user by using saved specific response format for the DB Status query
		 * This method must be invoke by messaging service 
		 * 
		 * @param responseFormat
		 * @param premissionFieldsName
		 * @param obj
		 * @param allFields
		 * @return
		 */
		public static String responseMassageParserForMessagingServices(String responseFormat, List premissionFieldsName, DataObject obj, Vector allFields){
			List indexList = new ArrayList();
			if(responseFormat != null)
				responseFormat = responseFormat.replaceAll("\r", "\n");
			String str = responseFormat;
			String newStr ="";
			if(responseFormat != null)
			{
				int indexKey = responseFormat.indexOf("%");
				while(str.contains("%")) { //pars response format and get index f fields from between % sign
					int index = str.indexOf("%");
		    		str = str.substring(index+1);
		    		int ii = str.indexOf("%");
		    		indexList.add(String.valueOf(indexKey));
		    		indexKey = indexKey + (ii+1);
		    	}
				
				List filedListFromResponse = new ArrayList();
		    	for(int j = 0; j < indexList.size(); ){//get fields name with finding index
		    		int firstIndex = Integer.parseInt(indexList.get(j).toString()) + 1;
		    		int secondIndex = Integer.parseInt(indexList.get(j+1).toString());
		    		String field =  responseFormat.substring(firstIndex,secondIndex);
		    		filedListFromResponse.add(field);
		    		j=j+2;
		    	}
		    	
	    		for(int i = 0; i < premissionFieldsName.size(); i++)// loop on premissionFields list and get its fields name
	    		{
	    			if (obj != null) {
	    				String fldName = (String)premissionFieldsName.get(i);
	    				Object val = obj.getObjectValue(fldName);
	
	    				if(val == null || val.toString().equals(""))
	    					val = "-";
	    				
	    				if(filedListFromResponse.contains(fldName)){ //If finding fields on format response has premission it can replace with value
	    					String value= val.toString();
	    		    		if(newStr.equals(""))
	    		    			newStr = responseFormat.replace("%" + fldName + "%", value);
	    		    		else{
	    		    			newStr = newStr.replace("%" + fldName + "%", value);	
	    		    		}	    					
	    				}
	    			}	   
	    		}
	    		
	    		List dataViewfieldsNameList = new ArrayList();
	    		for(int j = 0; j < allFields.size(); j++) {
	    			String fldName = ((DataViewField)allFields.get(j)).getName();
	    			dataViewfieldsNameList.add(fldName);
	    		}
	    		
	    		for(int i = 0; i < filedListFromResponse.size(); i++) {
	    			String fldName = (String)filedListFromResponse.get(i);
	    			
    				if(newStr.equals(""))
    					newStr = responseFormat;
    				
    				if(newStr.contains(fldName) ){
    					//If finding fields on format response has premission it can replace with value
    					if(dataViewfieldsNameList.contains(fldName))
    						newStr = newStr.replaceAll("%" + fldName + "%", "No Permissions");
    					else{
    						Object val = obj.getObjectValue(fldName);
    						if(val != null)
    							newStr = newStr.replaceAll("%" + fldName + "%", val.toString());
    					}
    				}	   	    			
	    		}
			}
			
			if(newStr.equals("")){
				newStr = responseFormat;
	    	}
			return newStr;
		}

		/**
		 * Pars recived message to recognize userInput by messaging separator chracter
		 * 
		 * @param seperator
		 * @param userInputsStr
		 * @return Map
		 */
		public static Map userInputsParser(String seperator, String userInputsStr){
			if(seperator == null || seperator.equals(""))
				seperator = " ";
			
			ArrayList<String> userInputsList = new ArrayList();
			Map userInputsMap = new HashMap();
			
			int startIndex = 0;
			String key = "keyword";
			int endIndex = userInputsStr.indexOf(seperator);
			if(endIndex != -1){
				String keyword = userInputsStr.substring(startIndex, endIndex);
				userInputsMap.put(key, keyword);
				userInputsStr = userInputsStr.substring(endIndex+1);
			}
			
			int i = 0;
			
			userInputsStr = userInputsStr.trim() + "~";
			
			while(userInputsStr.contains(seperator)){
				endIndex = userInputsStr.indexOf(seperator);
				String ui = userInputsStr.substring(startIndex, endIndex);
				userInputsStr = userInputsStr.substring(endIndex+1);
				//userInputsMap.put(key, ui);
				userInputsList.add(ui);
				i++;
				//key = "[UserInput" + i + "]";
			}
			
			if(userInputsStr.contains("~")){
				String ui = userInputsStr.substring(0, userInputsStr.length()-1);
				
				if(userInputsMap.get("keyword") != null)
					userInputsList.add(ui);
				else
					userInputsMap.put(key, ui);
			}
			
			userInputsMap.put("userInputs", userInputsList);
			return userInputsMap;
		}

		/**
		 * Pars recived message to recognize userInput by messaging separator chracter
		 * This method is using for No keyword messages
		 * 
		 * @param seperator
		 * @param userInputsStr
		 * @return
		 */
				
		public static ArrayList<String> getUserInputForNoKeywordQuery(String seperator, String userInputsStr){
			ArrayList<String> userInputsList = new ArrayList<String> ();
			String child;
			if(userInputsStr != null && userInputsStr.length()>0)
			{
				while(userInputsStr.contains(seperator)) {
					int endIndex = userInputsStr.indexOf(seperator);
					child = userInputsStr.substring(0, endIndex);
					userInputsList.add(child);
					userInputsStr = userInputsStr.substring(endIndex+1);
				}
		    	if(!userInputsStr.contains(seperator) && userInputsStr.length()>0)
		    	{
		    		userInputsList.add(userInputsStr);
		    	}
			}
			return userInputsList;
		}
		
		public static ArrayList<String> userInputsParserListUr(String seperator, String userInputsStr){
			ArrayList<String> userInputsList = new ArrayList();
			
			int startIndex = 0;
			int i = 1;
			
			userInputsStr = userInputsStr.trim() + "~";
			
			while(userInputsStr.contains(seperator)){
				int endIndex = userInputsStr.indexOf(seperator);
				String ui = userInputsStr.substring(startIndex, endIndex);
				userInputsStr = userInputsStr.substring(endIndex+1);
				userInputsList.add(ui);
				i++;
			}				
			if(userInputsStr.contains("~")){
				String ui = userInputsStr.substring(0, userInputsStr.length()-1);;
				userInputsList.add(ui);
			}
			return userInputsList;
		}
		
		
		public static String sqlQueryParser(String sql, Map userInputsMap){
			Iterator keysList = userInputsMap.keySet().iterator();
			while (keysList.hasNext()) {
				String keyUI = (String) keysList.next();
				String valueUI = (String)userInputsMap.get(keyUI);
				if(sql.contains(keyUI)){
					sql = sql.replace(keyUI, valueUI);
				}
			}
			return sql;
		}

		
		public static Map getUserInputsMap(String userInputsStr, String separator){
			//String separator = userInputsStr.substring(0,1);
			userInputsStr = userInputsStr.substring(1);
			
			List userInputsList = new ArrayList();
			Map userInputsMap = new HashMap();
			
			//userInputsMap.put("separator", separator);
			
			int startIndex = 0;
			String key = "keyword";
			int i = 0;
			
			userInputsStr = userInputsStr.trim() + "~";
			
			while(userInputsStr.contains(separator)){
				int endIndex = userInputsStr.indexOf(separator);
				String ui = userInputsStr.substring(startIndex, endIndex);
				userInputsStr = userInputsStr.substring(endIndex+1);
				userInputsMap.put(key, ui);
				userInputsList.add(ui);
				i++;
				key = "[UserInput" + i + "]";
			}				
			if(userInputsStr.contains("~")){
				String ui = userInputsStr.substring(0, userInputsStr.length()-1);
				if(userInputsMap.get("keyword") != null)
					key = "[UserInput" + i + "]";
				else
					key = "keyword";
				userInputsMap.put(key, ui);
			}
			
			return userInputsMap;
		}

		/**
		 * Get the list of fields which user allows to access
		 * 
		 * @param mdnUser
		 * @param projectId
		 * @return List
		 */
		public static List getAuthenticatedMessaginFields(User mdnUser, int projectId){
			List premissionFields = new ArrayList();
			
			IDataAgent dataAgent = DataAgentFactory.getDataInterface();
			List<GroupDataView> dvgs;
			int groupId  = 0;
			try {
				if(mdnUser != null){
					GroupMembership groupForUser = dataAgent.getUserGroupByProjectId(mdnUser.getId(), projectId);
					if(groupForUser != null)
						groupId = groupForUser.getGroupId();
				}else{
					Group guestGroup = dataAgent.getGuestGroup(projectId);
					if(guestGroup != null)
						groupId = guestGroup.getId();
				}

				dvgs = dataAgent.getGroupViewsPermissions(groupId);
				for (GroupDataView dvg : dvgs){
					int fieldId = dvg.getDataViewId();
					DataViewField field = dataAgent.getViewField(fieldId);
					if(field != null){
						String fieldName = field.getName();
						premissionFields.add(fieldName);
					}
				}
			} catch (MdnException e) {
				e.printStackTrace();
			}
			
			return premissionFields;
		}
		
		/**
		 * There is a default response for per query that was prepared by using DB fields 
		 * Format of default reponse is <tableName.fieldName : %tableName.fieldName%>
		 * 
		 * @param queryType
		 * @param sql
		 * @param dbId
		 * @param viewID
		 * @param request
		 * @param ds
		 * @param selectedWebService
		 * @return
		 */
		public static String getDefaultResponse(String queryType, String sql, String dbId, String viewID, HttpServletRequest request, int ds,
				WebServiceOperationDobj selectedWebService ){
			String replyingMsg = "";
			
			if(ds == 2){
				//WS
				replyingMsg = getDefaultResultFormatForWebServices(request, selectedWebService);//wsdlUrl, selectedOperation
			}else{
				IDataAgent dataAgent = DataAgentFactory.getDataInterface();			
				if(sql == null || sql.equals("") || sql.equals(Constants.UNDEFINED) || 
						dbId == null || dbId.equals("") || dbId.equals(Constants.UNDEFINED) || 
						viewID == null || viewID.equals("") || viewID.equals(Constants.UNDEFINED)){
					replyingMsg = "There are no results for this query.";
				}else{
					DataView view = null;
					try {
						view = dataAgent.getMetaViewByID(Integer.parseInt(viewID), true);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (MdnException e) {
						e.printStackTrace();
					}
			    	if(Integer.parseInt(queryType)== Constants.SELECT_QUERY_TYPE_NUM){//Select
			        	Vector titles = new Vector();
		        		titles = view.getFields();
		        		for(int col = 0; col < titles.size(); col++)
		        		{
		        			String filedName = titles.get(col).toString();
		        			DataViewField fieldObj = (DataViewField)titles.get(col);
		        			replyingMsg += fieldObj.getDisplayName() +" : %" + filedName + "% \n";
		        		}
			    	}else{
			    		int tableID = Integer.parseInt(viewID);
			    		try {
							EntityDobj table = dataAgent.getMetaTableByID(tableID, true);
				    	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
				    	    replyingMsg = MessageConstants.getMessage(file, MessageConstants.SUCCESS_QUARY_MSG_RESPONSE) + " " + table.getName();
						} catch (MdnException e) {
							e.printStackTrace();
						}
			    	}
				}
			}
			return replyingMsg;
		}
		
		/**
		 * There is a default response for per query that was prepared by Web Services result 
		 * Format of default reponse is <tableName.fieldName : %tableName.fieldName%>
		 *  
		 * @param request
		 * @param selectedWebService
		 * @return
		 */
		public static String getDefaultResultFormatForWebServices(HttpServletRequest request, WebServiceOperationDobj selectedWebService){//must be call only on admin screen No messaging Service!!
			String userInputTestString = request.getParameter("uiStr");
			
			IDataAgent dataAgent = DataAgentFactory.getDataInterface();
			String separator = " ";
			MdnMessageSeparator mdnMsgSep = null;
			try {
				mdnMsgSep = dataAgent.getMessageSeparator();
			} catch (MdnException e1) {
				e1.printStackTrace();
			}
			if(mdnMsgSep != null){
				separator = mdnMsgSep.getConditionSeperator();
			}		    				
			ArrayList<String> userInputList = MessagingUtils.getUserInputForNoKeywordQuery(separator, userInputTestString);			
			
			ParamHelper paramHelper = getParamHelper(selectedWebService);
			List<ParamListItem> resultParam = executeWebService(request, userInputList, paramHelper);//= new ArrayList<ParamListItem>();
			
			String responseFormat = "";		
			Set titlesSet = new HashSet();
			for(ParamListItem resultItem : resultParam )
			{
				String title;
				Object resultItemValue = resultItem.getValue();
				if(resultItemValue.getClass().isArray()){
					Object[] resultItemArray = (Object[])resultItemValue;
					responseFormat = "The result is a list.";
				}else{
					title = resultItem.getLabel();
					titlesSet.add(title);
				}
			}			
			Object titles[] = (Object[])titlesSet.toArray();
			for(int i = 0; i<titles.length; i++){
		        String lable = titles[i].toString();
		        responseFormat = responseFormat + lable + ": %" + lable + "% \n";
			}			
			return responseFormat;
		}
		
		/**
		 * Get Input Arguments for web service operation 
		 * 
		 * @param wsdlUrl
		 * @param selectedService
		 * @param selectedPort
		 * @param selectedOperation
		 * @return
		 */
		public static List getInputArqumentsList(String wsdlUrl, String selectedService, String selectedPort, String selectedOperation){
			List argsLableList = new ArrayList();
			ParamHelper paramHelper;
			try{
				if(getParamHelperMap().containsKey(wsdlUrl)){
					paramHelper = (ParamHelper)paramHelperMap.get(wsdlUrl);
				}else{
					paramHelper = new ParamHelper(wsdlUrl);
					paramHelper.setCurrentService(selectedService);
					paramHelper.setCurrentPort(selectedPort);
					paramHelper.setCurrentOperation(selectedOperation);
					paramHelper.createParamList();
					paramHelperMap.put(wsdlUrl, paramHelper);
				}
				List<ParamListItem> reqParamList = paramHelper.getParamList();
				for (ParamListItem parameter : reqParamList){
					String lableParam = parameter.getLabel();
					argsLableList.add(lableParam);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}				
			return argsLableList;
		}
		
		/**
		 * get all query keywords to make a help result
		 * 
		 * @param msgType
		 * @param languageFile
		 * @return
		 */
		public static String getAllQueriesList(String msgType, String languageFile){
			String result = "";
			
			IDataAgent dataAgent = DataAgentFactory.getDataInterface();
			
			try {
				List<QueryDobj> allQueries = dataAgent.getAllQueries();
				if(allQueries != null && !allQueries.isEmpty()){
					for (QueryDobj q : allQueries) {
						if(q.getDelStatus() == Integer.parseInt(Constants.MARKED_AS_NOT_DELETED)){
							String key = null;
							if(msgType.equalsIgnoreCase(MDNIMServer.SERVER_TYPE)){
								key = q.getImKeyword();
							}if(msgType.equalsIgnoreCase(MdnSmsServer.SERVER_TYPE) || msgType.equalsIgnoreCase(MdnSmppGatewayServer.SERVER_TYPE)){
								key = q.getSmsKeyword();
							}if(msgType.equalsIgnoreCase(MdnMailService.SERVER_TYPE)){
								String keywordLable = MessageConstants.getMessage(languageFile, MessageConstants.SEARCH_RESULT_FORMAT_KEY);
								String emailServerLable = MessageConstants.getMessage(languageFile, MessageConstants.SEARCH_RESULT_FORMAT_EMAIL_SERVER);							
								String emailAddress="";
								if(q.getEmailAddressId() != -1){
									emailAddress = dataAgent.getEmailSettingById(q.getEmailAddressId()).getEmailAddress();
									emailAddress = emailServerLable + emailAddress + " ," + keywordLable;
				
									String emailKeyword = q.getEmailKeyword();
									if(emailKeyword!= null && !emailKeyword.equals(""))
										key = emailAddress + emailKeyword + "\n";
									else
										key = emailAddress + "No Keyword \n";					
								}
							}
							if(key != null && !key.equals(""))
								result = result + "\n"+ key;
						}
						if(result.trim().equals(""))
							result= MessageConstants.getMessage(languageFile, MessageConstants.SEARCH_NO_RESULT);						
					}
				}else{
					result = MessageConstants.getMessage(languageFile, MessageConstants.SEARCH_NO_RESULT);
				}
			} catch (MdnException e) {
				e.printStackTrace();
			}		
			return result;
		}
		
		/**
		 * Search query by keyword
		 * 
		 * @param msgType
		 * @param languageFile
		 * @param keyword
		 * @return
		 */
		public static String searchQueryKeyword(String msgType, String languageFile, String keyword){
			String result = "";
			IDataAgent dataAgent = DataAgentFactory.getDataInterface();
			try {
				List<QueryDobj> queries = dataAgent.searchQueryByKeyword(msgType, keyword);
				if(queries != null && !queries.isEmpty()){
					String info="";
					for (QueryDobj q : queries) {
						String keywordLable = MessageConstants.getMessage(languageFile, MessageConstants.SEARCH_RESULT_FORMAT_KEY);
						String nameLable = MessageConstants.getMessage(languageFile, MessageConstants.SEARCH_RESULT_FORMAT_NAME);
						String descLable = MessageConstants.getMessage(languageFile, MessageConstants.SEARCH_RESULT_FORMAT_DESC);
						String emailServer = MessageConstants.getMessage(languageFile, MessageConstants.SEARCH_RESULT_FORMAT_EMAIL_SERVER);
						String descUR = MessageConstants.getMessage(languageFile, MessageConstants.SEARCH_RESULT_FORMAT_UR);
						if(msgType.equalsIgnoreCase(MDNIMServer.SERVER_TYPE)){
							info = info + keywordLable + q.getImKeyword() + "\n";
						}else if(msgType.equalsIgnoreCase(MdnSmsServer.SERVER_TYPE) || msgType.equalsIgnoreCase(MdnSmppGatewayServer.SERVER_TYPE)){
							info = info + keywordLable + q.getSmsKeyword() + "\n";
						}else if(msgType.equalsIgnoreCase(MdnMailService.SERVER_TYPE)){
							String emailAddress="";
							if(q.getEmailAddressId() != -1){
								emailAddress = dataAgent.getEmailSettingById(q.getEmailAddressId()).getEmailAddress();
								String emailInfo;
			
								String emailKeyword = q.getEmailKeyword();
								if(emailKeyword == null || emailKeyword.equals(""))
									emailKeyword = "No Keyword \n";
								
								emailInfo = keywordLable + emailKeyword + "\n" + emailServer + emailAddress;
								
								info = info + emailInfo + "\n";
							}						
						}
						info = info+ nameLable + q.getName() + "\n";
						info = info + descLable + q.getDescription()+ "\n";
					}
					if(!info.trim().equals(""))
						result= info;
					else
						result= MessageConstants.getMessage(languageFile, MessageConstants.SEARCH_NO_RESULT);
				}else{
					result = MessageConstants.getMessage(languageFile, MessageConstants.SEARCH_NO_RESULT);
				}
			} catch (MdnException e) {
				e.printStackTrace();
			}
			
			return result;
		}
		
		/**
		 * get DB table permission to control data access to retrieve data by Insert/Update queries 
		 * 
		 * @param mdnUser
		 * @param projectId
		 * @param queryEntityId
		 * @return
		 */
		public static GroupTablePermission getAuthenticatedMessaginEntity(User mdnUser, int projectId, int queryEntityId){
			GroupTablePermission tablePermission = null;
			IDataAgent dataAgent = DataAgentFactory.getDataInterface();
			
			int groupId  = 0;
			try {
				if(mdnUser != null){
					GroupMembership groupForUser = dataAgent.getUserGroupByProjectId(mdnUser.getId(), projectId);
					if(groupForUser != null)
						groupId = groupForUser.getGroupId();
				}else{
					Group guestGroup = dataAgent.getGuestGroup(projectId);
					if(guestGroup != null)
						groupId = guestGroup.getId();
				}
				
				tablePermission = dataAgent.getGroupTablePermissionByEntityID(queryEntityId, groupId);
				
			} catch (MdnException e) {
				e.printStackTrace();
			}
			
			return tablePermission;//If it returns null that means there is no authority for the group to access to this Entity 
		}
		
		/**
		 * get ParamHelper by WebServiceOperationDobj info 
		 * 
		 * @param selectedWebService
		 * @return
		 */
		public static ParamHelper getParamHelper(WebServiceOperationDobj selectedWebService){
			String wsdlUrl = selectedWebService.getUrl();
			String wsOperation = selectedWebService.getOperation();
			String wsPort = selectedWebService.getPort();
			String wsService = selectedWebService.getService();
			
			ParamHelper paramHelper = null;	
			try {
				String wsdlUrlKey = wsdlUrl + wsOperation;
				if(paramHelperMap.containsKey(wsdlUrlKey)){
					paramHelper = (ParamHelper)paramHelperMap.get(wsdlUrlKey);
				}else{
					paramHelper = new ParamHelper(wsdlUrl);
					paramHelper.setCurrentService(wsService);
					paramHelper.setCurrentPort(wsPort);
					paramHelper.setCurrentOperation(wsOperation);
					paramHelper.createParamList();
					paramHelperMap.put(wsdlUrlKey, paramHelper);
				}		
			} catch (MdnException e) {
				e.printStackTrace();
			}			
			return paramHelper;
		}
		
		/**
		 * Invoke web service operation to get the result then put the data in list of MDN ParamListItem format 
		 * 
		 * @param request
		 * @param userInputList
		 * @param paramHelper
		 * @return List<ParamListItem>
		 */
		public static List<ParamListItem> executeWebService(HttpServletRequest request, ArrayList<String> userInputList, ParamHelper paramHelper){
			String rows = request.getParameter("rows");		
			String numbers = request.getParameter("numbers");
			String fields = request.getParameter("fields");
			String values = request.getParameter("values");
	        String[] rowsList = rows.split(",");
	        
	        String[] numbersList = null;
	        if (numbers != null)
	        	numbersList = numbers.split(",");

	        String[] fieldsList = fields.split(",");
	        String[] valuesList = values.split(",");
			boolean empty = false;
			if (rowsList.length == 1){
				String numberList =numbersList[0]; 
				if (numberList.equals("") || numberList.equals("undefined")){
					//There is no query criteria need to save
					empty = true;
				}
			}
			List myvaluesList = new ArrayList();

			List<ParamListItem> resultParam = new ArrayList<ParamListItem>();
			try{

				
				List<ParamListItem> paramList = new ArrayList<ParamListItem>();
				
				paramList = paramHelper.getParamList();
				if (!empty){
					int userinputIndex = 0;
					for (int i = 0; i< rowsList.length; i++){
		            	
		            	String number = null;
		            	if (numbersList != null && numbersList.length == rowsList.length)
		            		number = numbersList[i];                	
		            	
		            	String field = fieldsList[i];
		            	String value = valuesList[i];               	
		            	
		            	if(value.equals("[UserInput]")){
		            		if(userInputList != null && !userInputList.isEmpty()){
		            			value = userInputList.get(userinputIndex);
		            			userinputIndex++;
		            		}
		            	}
		            	myvaluesList.add(value);
		            }        			
				}				
				Object[] myvalues = myvaluesList.toArray(new Object[0]);
				List<ParamListItem> reqParamList = paramHelper.getParamList();
				resultParam =  paramHelper.invoke(reqParamList, myvalues);			
		
			} catch (MdnException e) {
				e.printStackTrace();
			}			
			return resultParam;
		}
		
		/**
		 * Invoke web service operation to get the result then put the data in list of MDN ParamListItem format  
		 * 
		 * @param queryId
		 * @param userInputList
		 * @param paramHelper
		 * @return List<ParamListItem>
		 */
		public static List<ParamListItem> executeWebService(int queryId, ArrayList<String> userInputList, ParamHelper paramHelper){
			IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	        String[] fieldsList;
	        String[] valuesList;		
			List myvaluesList = new ArrayList();
			List<ParamListItem> resultParam = new ArrayList<ParamListItem>();
			List<ParamListItem> paramList = new ArrayList<ParamListItem>();
			
			try{
			List<QueryCriteriaDobj> criteriaList = dataAgent.getQueryCriteriaByQueryID(queryId, null);
			paramList = paramHelper.getParamList();			
			int userinputIndex = 0;
			for(QueryCriteriaDobj criteriaRow: criteriaList){
				String fldName = criteriaRow.getName();
				String value = criteriaRow.getValue();
				String rowNum = criteriaRow.getValue();
				
            	if(value.equals("[UserInput]")){
            		if(userInputList != null && !userInputList.isEmpty()){
            			value = userInputList.get(userinputIndex);
            			userinputIndex++;
            		}
            	}
	            myvaluesList.add(value);
			}
			
				Object[] myvalues = myvaluesList.toArray(new Object[0]);
				List<ParamListItem> reqParamList = paramHelper.getParamList();
				resultParam =  paramHelper.invoke(reqParamList, myvalues);			
		
			} catch (MdnException e) {
				e.printStackTrace();
			}			
			return resultParam;
		}		
		
		/**
		 * Provide response message for web services query
		 * 
		 * @param paramHelper
		 * @param resultParam
		 * @param displayResult
		 * @param responseFormat
		 * @return
		 */
		public static String prepareWSResponseMsg(ParamHelper paramHelper, List<ParamListItem> resultParam, int displayResult, String responseFormat){
			//-get responseFormat -> if != null -> make response msg as String >>  
			String originalResult = "";
			String replyContext = "";
			List<WebServiceResultRow> paramListItemsAsRow = paramHelper.getParamListItemsAsRow();
			Set titlesSet = new HashSet();
			for(ParamListItem resultItem : resultParam )
			{
				String title;
				Object resultItemValue = resultItem.getValue();
				if(resultItemValue.getClass().isArray()) {
					Object type = resultItemValue.getClass().getComponentType();
					Object[] resultItemArray = (Object[])resultItemValue;
					int len = resultItemArray.length;
					for(int i = 0; i< resultItemArray.length; i++){
						Object obj = resultItemArray[i];
						originalResult = originalResult + obj.toString() + "\n";
					}
				} else {
					title = resultItem.getLabel();
					titlesSet.add(title);
					String fieldItem = title + ": " + resultItemValue.toString() + "\n";    
					originalResult = originalResult + fieldItem;
				}
				
			}			
			
			Object titles[] = (Object[])titlesSet.toArray();

			if( displayResult > paramListItemsAsRow.size())
				displayResult = paramListItemsAsRow.size();
    
			if(!paramListItemsAsRow.isEmpty()){
				for(int k = 0;  k < displayResult; k++) {
					// get the data object
					WebServiceResultRow rowObj = paramListItemsAsRow.get(k);//one row which contains complexType fields If result is ComplexType[] array
	
					if(responseFormat != null && !responseFormat.trim().equals(""))
						replyContext += MessagingUtils.responseMassageParserWS(responseFormat, titles, rowObj) + "\n";
					else{
		    			List<ParamListItem> paramsRowObj = rowObj.getParamItemList();
						for(ParamListItem resultItem : paramsRowObj )
						{
							String title;
							Object resultItemValue = resultItem.getValue();
							if(resultItemValue.getClass().isArray()) {
								Object[] resultItemArray = (Object[])resultItemValue;
								for(int i = 0; i< resultItemArray.length; i++){
									Object obj = resultItemArray[i];
									replyContext = replyContext + obj.toString() + "\n";
								}
							} else {
								title = resultItem.getLabel();
								String fieldItem = title + ": " + resultItemValue.toString() + "\n";    
								replyContext = replyContext + fieldItem;
							}														
						}
					}	
				}
			}else{
				replyContext = originalResult;
			}		
			
			return replyContext; 
		}
}
