package com.framedobjects.dashwell.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.DataSource;
import wsl.fw.datasource.DataSourceException;
import wsl.fw.datasource.RecordSet;
import wsl.fw.exception.MdnException;
import wsl.fw.util.Util;
import wsl.mdn.common.MdnDataManager;
import wsl.mdn.dataview.DataSourceDobj;
import wsl.mdn.dataview.DataView;
import wsl.mdn.dataview.DataViewDataSource;
import wsl.mdn.dataview.DataViewField;
import wsl.mdn.dataview.EntityDobj;
import wsl.mdn.dataview.FieldDobj;
import wsl.mdn.dataview.JdbcDataSourceDobj;
import wsl.mdn.dataview.JdbcDriver;
import wsl.mdn.dataview.MdnDataCache;
import wsl.mdn.dataview.QueryCriteriaDobj;
import wsl.mdn.dataview.QueryDobj;
import wsl.mdn.dataview.WebServiceDescription;
import wsl.mdn.dataview.WebServiceDobj;
import wsl.mdn.dataview.WebServiceOperationDobj;
import wsl.mdn.mdnmsgsvr.CustomField;
import wsl.mdn.mdnmsgsvr.MdnMessageSeparator;
import wsl.mdn.mdnmsgsvr.UserReply;

import com.framedobjects.dashwell.db.DataAgentFactory;
import com.framedobjects.dashwell.db.DbConnection;
import com.framedobjects.dashwell.db.IDataAgent;
import com.framedobjects.dashwell.db.RmiAgent;
import com.framedobjects.dashwell.db.meta.MetaField;
import com.framedobjects.dashwell.db.meta.MetaRelation;
import com.framedobjects.dashwell.db.meta.MetaTable;
import com.framedobjects.dashwell.handlers.DbDataHandler;
import com.framedobjects.dashwell.utils.MessageConstants;
import com.framedobjects.dashwell.utils.MessagingUtils;
import com.framedobjects.dashwell.utils.Constants;
import com.framedobjects.dashwell.utils.XmlFormatter;
import com.framedobjects.dashwell.utils.webservice.IParamView;
import com.framedobjects.dashwell.utils.webservice.ParamHelper;
import com.framedobjects.dashwell.utils.webservice.ParamListItem;
import com.framedobjects.dashwell.utils.webservice.ParamView;
import com.framedobjects.dashwell.utils.webservice.WebServiceDefinitionHelper;
import com.framedobjects.dashwell.utils.webservice.WebServiceResultRow;

public class OL_DBServlet extends HttpServlet {
	
	private static Logger logger = Logger.getLogger(OL_DBServlet.class.getName());

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.debug("URI: " + request.getRequestURI());
	    logger.debug("Query string: " + request.getQueryString());
	
	    Enumeration parameters = request.getParameterNames();
	    while (parameters.hasMoreElements()){
	      String p = (String)parameters.nextElement();
	      logger.debug("-- Parameter: " + p + ": " + request.getParameter(p));
	    }
	    
	    String action = request.getParameter(Constants.REQUEST_PARAM_ACTION);
	    Element root = null;
	    if (action != null){
		    if (action.equalsIgnoreCase("getDbConnection")){
		    	root = getDbConnection(request);
		    } else if (action.equalsIgnoreCase("getDrivers")){
	      		root = getDrivers(request);
		    } else if (action.equalsIgnoreCase("getDriver")){
	      		root = getDriver(request);
		    } else if (action.equalsIgnoreCase("getNewDriver")){
	      		root = getNewDriver(request);
		    } else if (action.equalsIgnoreCase("addNewDriver")){
	      		root = addNewDriver(request);
		    } else if (action.equalsIgnoreCase("saveDriver")){
	      		root = saveDriver(request);
		    } else if (action.equalsIgnoreCase("recycleDriver")){
	      		root = recycleDriver(request);
		    } else if (action.equalsIgnoreCase("deleteDriver")){
	      		root = deleteDriver(request);
		    } else if (action.equalsIgnoreCase("saveConn")){
	      		root = saveConnection(request);
		    } /*else if (action.equalsIgnoreCase("dbWizard")){
	      		root = dbWizard(request);
		    } */else if (action.equalsIgnoreCase("dbWizardTables")){
	      		root = dbWizardTables(request);
		    } else if (action.equalsIgnoreCase("dbWizardFields")){
	    		root = dbWizardFields(request);
		    } else if (action.equalsIgnoreCase("dbImportTables")){
	    		root = dbImportTables(request);
		    } else if (action.equalsIgnoreCase("dbWizardGetRelations")){
				root = dbWizardGetRelations(request);
		    } else if (action.equalsIgnoreCase("dbWizardAddRelation")){
	  			root = dbAddRelation(request);
		    } else if (action.equalsIgnoreCase("dbWizardDeleteRelation")){
				root = dbDeleteRelation(request);
		    } else if (action.equalsIgnoreCase("testDbConnectionNew")){
	      		root = testDbConnection(request);
		    } else if (action.equalsIgnoreCase("testConn")){
	    		root = testDbConnection(request);
		    } else if (action.equalsIgnoreCase("abortWizard")){
	    		root = abortWizard(request);
		    } /*else if (action.equalsIgnoreCase("finishDbWizard")){
	  			root = finishWizard(request);
		    } */else if (action.equalsIgnoreCase("getTables")){
	      		root = getTables(request);
		    } /*else if (action.equalsIgnoreCase("getJoins")){
	      		root = getJoins(request);
		    } */else if (action.equalsIgnoreCase("getTable")){
	    		root = getTable(request);
		    }else if (action.equalsIgnoreCase("editTable")){
	  			root = editTable(request);
		    } else if (action.equalsIgnoreCase("getField")){
	  			root = getField(request);
		    } else if (action.equalsIgnoreCase("editField")){
				root = editField(request);
		    }else if (action.equalsIgnoreCase("getView")){
	    		root = getView(request);
		    } else if (action.equalsIgnoreCase("getViewField")){
	  			root = getViewField(request);
		    } else if (action.equalsIgnoreCase("saveViewField")){
	  			root = saveViewField(request);
		    } else if (action.equalsIgnoreCase("saveViewFieldEdit")){
	  			root = saveViewField(request);
		    } else if (action.equalsIgnoreCase("addNewView")){
	    		root = addNewView(request);
		    } else if (action.equalsIgnoreCase("saveView")){
	    		root = saveView(request);
		    } else if (action.equalsIgnoreCase("getTableFieldForView")){
	  			root = getTableFieldForView(request);
		    }else if (action.equalsIgnoreCase("getNewSelectQueryBuilder")){
	  			root = getNewSelectQueryBuilder(request);
		    } else if (action.equalsIgnoreCase("getNewUpdateQueryBuilder")){
	  			root = getNewUpdateQueryBuilder(request);
		    } else if (action.equalsIgnoreCase("getNewInsertQueryBuilder")){
	  			root = getNewInsertQueryBuilder(request);
		    } else if (action.equalsIgnoreCase("getSelectQueryBuilder")){
	  			root = getSelectQueryBuilder(request);
		    } else if (action.equalsIgnoreCase("getWebserviceQueryBuilder")){//TODO:
	  			root = getWebserviceQueryBuilder(request);
		    } else if (action.equalsIgnoreCase("getUpdateQueryBuilder")){
	  			root = getUpdateQueryBuilder(request);
		    } else if (action.equalsIgnoreCase("getInsertQueryBuilder")){
	  			root = getInsertQueryBuilder(request);
		    }else if (action.equalsIgnoreCase("recycleDataView")){
	  			root = recycleDataView(request);
		    } else if (action.equalsIgnoreCase("clearDataView")){
	  			root = clearDataView(request);
		    } else if (action.equalsIgnoreCase("deleteDataView")){
	  			root = deleteDataView(request);
		    } else if (action.equalsIgnoreCase("getSelectQueryResult")){
	  			root = getSelectQueryResult(request);
		    } else if (action.equalsIgnoreCase("getEmptySelectQueryResult")){
	  			root = getEmptySelectQueryResult(request);
		    } else if (action.equalsIgnoreCase("getUpdateQueryResult")){
	  			root = getUpdateQueryResult(request);
		    } else if (action.equalsIgnoreCase("getInsertQueryResult")){
	  			root = getInsertQueryResult(request);
		    } else if (action.equalsIgnoreCase("saveQuery")){
	    		root = saveQuery(request);
		    } else if (action.equalsIgnoreCase("saveQueryMsgProps")){
	    		root = saveQueryMsgProps(request);
		    } else if (action.equalsIgnoreCase("saveMsgDetailsInfo")){
	    		root = saveMsgDetailsInfo(request);
		    } else if (action.equalsIgnoreCase("getDefaultTextMsgForResponse")){
	    		root = getDefaultTextMsgForResponse(request);
		    } else if (action.equalsIgnoreCase("addQueryAndMsgInfo") || action.equalsIgnoreCase("editQueryAndMsgInfo")){
	    		root = saveQueryAndMsgInfo(request);
		    } else if (action.equalsIgnoreCase("getTablesView")){
	      		root = getTablesView(request);
		    } else if (action.equalsIgnoreCase("recycleConn")){
	    		root = recycleConnection(request);
		    } else if (action.equalsIgnoreCase("clearConn")){
	    		root = clearConnection(request);
		    } else if (action.equalsIgnoreCase("deleteConn")){
	    		root = deleteConnection(request);
		    } else if (action.equalsIgnoreCase("getAllSampleWebServices")){
	    		root = getAllSampleWebServices(request);
		    } else if (action.equalsIgnoreCase("getAllThirdPartyWebServices")){
	    		root = getAllThirdPartyWebServices(request);
		    } else if (action.equalsIgnoreCase("getWebServiceDescription")){
	    		root = getWebServiceDescription(request);
		    } else if (action.equalsIgnoreCase("getWebServiceDetails")){
	    		//root = getWebServiceDetails(request);
		    } else if (action.equalsIgnoreCase("getQueryCriteriaOfWebServiceDetails")){
	    		root = getQueryCriteriaOfWebServiceDetails(request);
		    } else if (action.equalsIgnoreCase("getWebServiceOperation")){
	    		root = getWebServiceOperation(request);
		    } else if (action.equalsIgnoreCase("getAllWebServiceOperations")){
	    		root = getAllWebServiceOperations(request);
		    } else if (action.equalsIgnoreCase("saveWebServiceOperation")){
	    		root = saveWebServiceOperation(request);
		    } else if (action.equalsIgnoreCase("testWebServiceQuery")){
	    		root = testWebServiceQuery(request);
		    } else if (action.equalsIgnoreCase("loadSelectedWsDetails")){
	    		root = loadSelectedWsDetails(request);
		    } else if (action.equalsIgnoreCase("getResponeseFormat")){
	    		root = getResponeseFormat(request);	    		
		    } else {
		    	root = new XmlFormatter().undefinedAction();
		    }
	    } else {
	    	root = new XmlFormatter().undefinedAction();
	    }
	    
	    // Return the XML-formatted reply.
	    response.setHeader ("Pragma",        "no-cache");
	    response.setHeader ("Cache-Control", "no-cache");
	    response.setContentType("text/xml; charset=UTF-8");
	    String xml = new XMLOutputter().outputString(new Document(root));        
	    logger.debug("xml: " + xml);
	    
	    ServletOutputStream out = response.getOutputStream();
	    out.println(xml);
	}
	
	private Element deleteDriver(HttpServletRequest request) {
		String action = request.getParameter("action");
		String driverName = request.getParameter("driverName");
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		//List<JdbcDriver> list = null;
		String msg = null;
		try {
			JdbcDriver jdbcDriver = dataAgent.getJdbcDriverByName(driverName);
			if (jdbcDriver != null){
				jdbcDriver.delete();
				msg = "OK";
			}else{
				msg = "Driver [" + driverName + "] does not exist.";
			}
			//list = dataAgent.getAllJdbcDrivers();
		} catch (MdnException e) {
			e.printStackTrace();
			msg = "Database error.";
		} catch (DataSourceException e) {
			e.printStackTrace();
			msg = "Database error.";
		}
		Element root = new XmlFormatter().simpleResult(action, msg);
		return root;
	}

	private Element recycleDriver(HttpServletRequest request) {
		String action = request.getParameter("action");
		String driverId = request.getParameter("driverId");
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		//List<JdbcDriver> list = null;
		String msg = null;
		try {
			JdbcDriver jdbcDriver = dataAgent.getJdbcDriverById(Integer.parseInt(driverId));
			if (jdbcDriver != null){
				jdbcDriver.setDelStatus(Constants.MARKED_AS_RECYCLED_INT);
				jdbcDriver.save();
				msg = "OK";
			}else{
				msg = "Driver [" + driverId + "] does not exist.";
			}
			//list = dataAgent.getAllJdbcDrivers();
		} catch (MdnException e) {
			e.printStackTrace();
			msg = "Database error.";
		} catch (DataSourceException e) {
			e.printStackTrace();
			msg = "Database error.";
		}
		Element root = new XmlFormatter().simpleResult(action, msg);
		return root;
	}	
	
	private Element saveDriver(HttpServletRequest request) {
		String action = request.getParameter("action");
		String driverId = request.getParameter("driverId");
		String driverName = request.getParameter("driverName");
		String driver = request.getParameter("driver");
		String urlFormat = request.getParameter("urlFormat");
		String description = request.getParameter("description");
		String fileName = request.getParameter("fileName");
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		//List<JdbcDriver> list = null;
		String msg = null;
		try {			
			if (driverName == null || driverName.trim().equals("") || driverName.equalsIgnoreCase("null") ||  driverName.equalsIgnoreCase("undefined") ){
				msg = "Empty Driver Name.";

			}else if (driver == null || driver.trim().equals("") || driver.equalsIgnoreCase("null") ||  driver.equalsIgnoreCase("undefined")){
				msg = "Empty Driver.";
			}
			if (msg != null){
				//list = dataAgent.getAllJdbcDrivers();
				Element root = new XmlFormatter().simpleResult(action, msg);
				return root;				
			}
			int intDriverId = Integer.parseInt(driverId);
			JdbcDriver jdbcDriver = dataAgent.getJdbcDriverById(intDriverId);
			if (jdbcDriver != null){
				JdbcDriver jdbcDriverWithSameName = dataAgent.getJdbcDriverByName(driverName);
				
				if (jdbcDriverWithSameName != null && !jdbcDriverWithSameName.getName().equals(jdbcDriver.getName())){
					msg = "Duplicate Driver Name.";
					Element root = new XmlFormatter().simpleResult(action, msg);
					return root;
				}
				
				jdbcDriver.setState(DataObject.IN_DB);
				jdbcDriver.setName(driverName);
				jdbcDriver.setDriver(driver);
				jdbcDriver.setUrlFormat(urlFormat);
				jdbcDriver.setDescription(description);
				if (fileName != null && !fileName.isEmpty() && !fileName.equalsIgnoreCase("undefined"))
					jdbcDriver.setFileName(fileName);
				jdbcDriver.save();
				msg = "OK";
			}else{
				msg = "Driver [" + driverName + "] does not exist.";
			}
			//list = dataAgent.getAllJdbcDrivers();
		} catch (MdnException e) {
			e.printStackTrace();
			msg = "Database error.";
		} catch (DataSourceException e) {
			e.printStackTrace();
			msg = "Database error.";
		}
		Element root = new XmlFormatter().simpleResult(action, msg);
		return root;
	}

	private Element addNewDriver(HttpServletRequest request) {
		String action = request.getParameter("action");
		String driverName = request.getParameter("driverName");
		String driver = request.getParameter("driver");
		String urlFormat = request.getParameter("urlFormat");
		String description = request.getParameter("description");
		String fileName = request.getParameter("fileName");
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		//List<JdbcDriver> list = null;
		String msg = null;
		try {
			
			
			if (driverName == null || driverName.trim().equals("") || driverName.equalsIgnoreCase("null") ||  driverName.equalsIgnoreCase("undefined") ){
				msg = "Empty Driver Name.";

			}else if (driver == null || driver.trim().equals("") || driver.equalsIgnoreCase("null") ||  driver.equalsIgnoreCase("undefined")){
				msg = "Empty Driver.";
			}
			if (msg != null){
				//list = dataAgent.getAllJdbcDrivers();
				Element root = new XmlFormatter().simpleResult(action, msg);
				return root;				
			}

			JdbcDriver jdbcDriver = dataAgent.getJdbcDriverByName(driverName);
			
			if (jdbcDriver == null){
				jdbcDriver = new JdbcDriver();
				jdbcDriver.setState(DataObject.NEW);
				jdbcDriver.setName(driverName);
				jdbcDriver.setDriver(driver);
				jdbcDriver.setUrlFormat(urlFormat);
				jdbcDriver.setDescription(description);
				if (fileName != null && !fileName.isEmpty() && !fileName.equalsIgnoreCase("undefined"))
					jdbcDriver.setFileName(fileName);
				jdbcDriver.setDelStatus(Constants.MARKED_AS_NOT_DELETED_INT);
				jdbcDriver.save();

				int newId = jdbcDriver.getId();				
				JdbcDriver newDriver = dataAgent.getJdbcDriverById(newId);
				if(newDriver == null){
					System.out.println("new Driver object has not been loaded yet");
					for(;;){//Wait till commite newDriver first
						newDriver = dataAgent.getJdbcDriverById(newId);
						if(newDriver != null)
							break;
					}
				}				
				
				msg = "OK";
			}else{
				msg = "Duplicate Driver Name.";
			}
			//list = dataAgent.getAllJdbcDrivers();
			
		} catch (MdnException e) {
			e.printStackTrace();
			msg = "Database error.";
		} catch (DataSourceException e) {
			e.printStackTrace();
			msg = "Database error.";
		}
		Element root = new XmlFormatter().simpleResult(action, msg);
		return root;
	}

	private Element getDrivers(HttpServletRequest request) {
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		List<JdbcDriver> list = null;
		try {
			list = dataAgent.getAllJdbcDrivers();
		} catch (MdnException e) {
			e.printStackTrace();
		}
		Element root = new XmlFormatter().getDrivers(list, null);
		return root;
	}

	private Element getDriver(HttpServletRequest request) {
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		String id = request.getParameter("id");
		int driverId = Integer.parseInt(id);
		JdbcDriver driver = null;
		try {
			driver = dataAgent.getJdbcDriverById(driverId);
		} catch (MdnException e) {
			e.printStackTrace();
		}
		Element root = new XmlFormatter().getDriver(driver);
		return root;
	}	
	private Element getNewDriver(HttpServletRequest request) {
		JdbcDriver driver = new JdbcDriver();
		driver.setId(-1);
		driver.setName("");
		driver.setDriver("");
		driver.setUrlFormat("");
		driver.setDescription("");
		driver.setFileName("");
		Element root = new XmlFormatter().getDriver(driver);
		return root;
	}	
	private Element saveWebServiceOperation(HttpServletRequest request) {
		Element root = null;
		String id = request.getParameter("id");
		String name = request.getParameter("name");
		String description = request.getParameter("description");
		String url = request.getParameter("url");
		String operation = request.getParameter("operation");
		String service = request.getParameter("service");
		String port = request.getParameter("port");
		String result = null;
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    String action = request.getParameter("action");
		String projectId = request.getParameter("projectId");
		int intProjectId = 1;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e2) {
			intProjectId = 1;
		}
		
		if (name == null || name.equals("") || name.equalsIgnoreCase("null") || name.equalsIgnoreCase("undefined")){
			result = "Please enter the name.";
		}
	    
	    if (url == null || url.equals("") || url.equalsIgnoreCase("null") || url.equalsIgnoreCase("undefined")){
			result = "Please select one WSDL URL.";
		}	    
	    
		if (operation == null || operation.equals("") || operation.equalsIgnoreCase("null") || operation.equalsIgnoreCase("undefined")){
			result = "Please select one operation.";
		}
		
		if (result != null){
			root = new XmlFormatter().simpleResultWithProjectId(file, action, result, intProjectId);
			return root;			
		}
		

		int saveStateInt = DataObject.NEW;
		WebServiceOperationDobj webServiceOperationDobj = null;
		
		try {
			int intId = Integer.parseInt(id);
			IDataAgent dataAgent = DataAgentFactory.getDataInterface();
			
			try {
				webServiceOperationDobj = dataAgent.getWebServiceOperationByID(intId);
				webServiceOperationDobj.setName(name);
				webServiceOperationDobj.setDescription(description);
				webServiceOperationDobj.setUrl(url);
				webServiceOperationDobj.setService(service);
				webServiceOperationDobj.setPort(port);
				webServiceOperationDobj.setOperation(operation);
				webServiceOperationDobj.setProjectId(intProjectId);
				saveStateInt = DataObject.IN_DB;
				webServiceOperationDobj.setState(saveStateInt);
				try {
					webServiceOperationDobj.save();
					result = "OK";
				} catch (DataSourceException e) {
					e.printStackTrace();
					result = "Database Error.";
				}
				
			} catch (MdnException e) {
				saveStateInt = DataObject.NEW;
				webServiceOperationDobj = new WebServiceOperationDobj();
				webServiceOperationDobj.setName(name);
				webServiceOperationDobj.setDescription(description);
				webServiceOperationDobj.setUrl(url);
				webServiceOperationDobj.setService(service);
				webServiceOperationDobj.setPort(port);
				webServiceOperationDobj.setOperation(operation);
				webServiceOperationDobj.setProjectId(intProjectId);
				webServiceOperationDobj.setState(saveStateInt);
				try {
					webServiceOperationDobj.save();
					result = "OK";
				} catch (DataSourceException e1) {
					e1.printStackTrace();
					result = "Database Error.";
				} /*catch (MdnException e2) {
					e2.printStackTrace();
					result = "Web Service Compile Error.";
				}*/
			}
			
		} catch (NumberFormatException e) {
			//if (id == null || id.equals("") || id.equalsIgnoreCase("null") || id.equalsIgnoreCase("undefined")){
				saveStateInt = DataObject.NEW;
				webServiceOperationDobj = new WebServiceOperationDobj();
				webServiceOperationDobj.setName(name);
				webServiceOperationDobj.setDescription(description);
				webServiceOperationDobj.setUrl(url);
				webServiceOperationDobj.setService(service);
				webServiceOperationDobj.setPort(port);
				webServiceOperationDobj.setOperation(operation);
				webServiceOperationDobj.setProjectId(intProjectId);
				webServiceOperationDobj.setState(saveStateInt);
				try {
					webServiceOperationDobj.save();
					result = "OK";
				} catch (DataSourceException e1) {
					e1.printStackTrace();
					result = "Database Error.";
				} /*catch (MdnException e2) {
					e.printStackTrace();
					result = "Web Service Compile Error.";
				}*/
				
			//}
		}
	    root = new XmlFormatter().simpleResultWithProjectId(file, action, result, intProjectId);
		return root;
	}

	private Element getWebServiceOperation(HttpServletRequest request) {
		String id = request.getParameter("id");
		try {
			int intId = Integer.parseInt(id);
			IDataAgent dataAgent = DataAgentFactory.getDataInterface();
			WebServiceOperationDobj webServiceOperationDobj = null;
			try {
				webServiceOperationDobj = dataAgent.getWebServiceOperationByID(intId);
			} catch (MdnException e) {
				e.printStackTrace();
			}
			return new XmlFormatter().getWebServiceOperation(webServiceOperationDobj);
		} catch (NumberFormatException e) {
			//if (id == null || id.equals("") || id.equalsIgnoreCase("null") || id.equalsIgnoreCase("undefined")){
				WebServiceOperationDobj webServiceOperationDobj = new WebServiceOperationDobj();
				Element root = new XmlFormatter().getWebServiceOperation(webServiceOperationDobj);
				return root;
			//}
		}
	}
	private Element getAllWebServiceOperations(HttpServletRequest request) {
		try {
			String projectId = request.getParameter("projectId");
			int intProjectId = 1;
			try {
				intProjectId = Integer.parseInt(projectId);
			} catch (NumberFormatException e2) {
				intProjectId = 1;
			}
			IDataAgent dataAgent = DataAgentFactory.getDataInterface();
			List<WebServiceOperationDobj> webServiceOperationDobjs = null;
			try {
				webServiceOperationDobjs = dataAgent.getAllWebServiceOperations(intProjectId);
			} catch (MdnException e) {
				e.printStackTrace();
			}
			return new XmlFormatter().getWebServiceOperations(webServiceOperationDobjs);
		} catch (NumberFormatException e) {
			//if (id == null || id.equals("") || id.equalsIgnoreCase("null") || id.equalsIgnoreCase("undefined")){
				WebServiceOperationDobj webServiceOperationDobj = new WebServiceOperationDobj();
				Element root = new XmlFormatter().getWebServiceOperation(webServiceOperationDobj);
				return root;
			//}
		}
	}


	/*private Element getWebServiceDetails(HttpServletRequest request) {
		String wsdlUrl = request.getParameter("WSDLUrl");
		String operation = request.getParameter("operation");
		System.out.println("operations: "+ operation);
		//String[] operationArray = operations.split(",");
		Element root = null;
		try {
			Definition definition = WebServiceDefinitionHelper.getWebServiceDefinition(wsdlUrl);
			//WebServiceDescription webServiceDescription = new WebServiceDescription(wsdlUrl, definition);
			WebServiceDetail operationObj = null;
			operationObj = WebServiceDefinitionHelper.loadWebServiceDefinition(definition, operation);
			root = new XmlFormatter().getWebServiceDetails(operationObj);
		} catch (WSDLException e) {
			e.printStackTrace();
		    // Get the current language file.
		    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
		    String action = request.getParameter("action");
			String result = "definition element is null";
			root = new XmlFormatter().simpleResult(file, action, result);			
		}

		return root;
	}*/
		
	private Element getQueryCriteriaOfWebServiceDetails(HttpServletRequest request) {
		String wsdlUrl = request.getParameter("WSDLUrl");
		String selectedService = request.getParameter("service");
		String selectedPort = request.getParameter("port");
		String selectedOperation = request.getParameter("operation");
		System.out.println("operation: "+ selectedOperation);
		//String[] operationArray = operations.split(",");
		/*Definition definition = null;
		try {
			definition = WebServiceDefinitionHelper.getWebServiceDefinition(wsdlUrl);
		} catch (WSDLException e) {
			e.printStackTrace();
		}*/
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    String action = request.getParameter("action");
		/*if (definition == null){
		    // Get the current language file.
			String result = "definition element is null";
			return new XmlFormatter().simpleResult(file, action, result);
		}
		else{*/
			//WebServiceDescription webServiceDescription = new WebServiceDescription(wsdlUrl, definition);
			//WebServiceDetail webServiceDetail = null;
			//webServiceDetail = WebServiceDefinitionHelper.loadWebServiceDefinition(definition, selectedOperation);
			
			List<QueryCriteriaDobj> allQueryCriteria= new Vector<QueryCriteriaDobj>();
			int index = 1;
			//for (WebServiceDetail webServiceDetail : operationObjs){
			//Operation operation = webServiceDetail.getOperation();
			//ArrayList<String> parameters = (ArrayList<String>)webServiceDetail.getParameters();
			//List<ParamListItem> parameters = null;
			List<IParamView> parameterViews = null;
			try {
				Map paramHelperMap = MessagingUtils.getParamHelperMap();
				ParamHelper paramHelper;		
				String wsdlUrlKey = wsdlUrl+selectedOperation;
				if(paramHelperMap.containsKey(wsdlUrlKey)){
					paramHelper = (ParamHelper)paramHelperMap.get(wsdlUrlKey);
				}else{				
					paramHelper = new ParamHelper(wsdlUrl);
					paramHelper.setCurrentService(selectedService);
					paramHelper.setCurrentPort(selectedPort);
					paramHelper.setCurrentOperation(selectedOperation);
					paramHelper.createParamList();
					
					paramHelperMap.put(wsdlUrlKey, paramHelper);
					
					System.out.println("^^^^^^^ Load on getQueryCriteriaOfWebServiceDetails method : " + wsdlUrl);
				}
				
				//parameters = paramHelper.getParamList();
				parameterViews = paramHelper.getParamViews();
				//parameters = LicenseManager.getParamList(wsdlUrl, selectedService, selectedPort, selectedOperation);
			} catch (Exception e) {
				e.printStackTrace();
				return new XmlFormatter().simpleResult(file, action, "Error when getting parameters: " + e.toString());
			}
			for (IParamView parameter : parameterViews){
				QueryCriteriaDobj queryCriteria = new QueryCriteriaDobj();
		  		queryCriteria.setUsed(1);
//		  		if (webServiceDetail.getObjectName() != null)
//		  			queryCriteria.setName(webServiceDetail.getObjectName() + "." + parameter);
//		  		else
//		  			queryCriteria.setName(parameter);//operation.getName() + "." + 
		  		queryCriteria.setName(parameter.getLabel());
		  		queryCriteria.setRowNo(index);
		  		queryCriteria.setNumber(index);
		  		queryCriteria.setValueOrCondition("Input arqument");//queryCriteria.setValueOrCondition("condition");
		  		queryCriteria.setParent(((ParamView)parameter).getParent() != null ? ((ParamView)parameter).getParent().getLabel() : "");
		  		queryCriteria.setCompId(QueryCriteriaDobj.COMPARISON_ID_EQUAL);
		  		queryCriteria.setComparison(QueryCriteriaDobj.COMPARISON_EQUAL);
		  		queryCriteria.setConnection(QueryCriteriaDobj.CONNECTION_AND);
		  		queryCriteria.setValue("_____");
		  		queryCriteria.setGrouping(QueryCriteriaDobj.GROUPING_ALL);
				allQueryCriteria.add(queryCriteria);
				index++;
			}
			//}
			
			Element root = new XmlFormatter().getWebServiceQueryBuider(allQueryCriteria, parameterViews);			

			return root;
		//}
	}	
	
	private Element loadSelectedWsDetails(HttpServletRequest request) {
		String wsdlUrl = request.getParameter("url");
		String selectedService = request.getParameter("service");
		String selectedPort = request.getParameter("port");
		String selectedOperation = request.getParameter("operation");
		
		try {
			Map paramHelperMap = MessagingUtils.getParamHelperMap();
			ParamHelper paramHelper;				
			String wsdlUrlKey = wsdlUrl+selectedOperation;
			if(paramHelperMap.containsKey(wsdlUrlKey)){
				paramHelper = (ParamHelper)paramHelperMap.get(wsdlUrlKey);
			}else{				
				paramHelper = new ParamHelper(wsdlUrl);
				paramHelper.setCurrentService(selectedService);
				paramHelper.setCurrentPort(selectedPort);
				paramHelper.setCurrentOperation(selectedOperation);
				paramHelper.createParamList();
				
				paramHelperMap.put(wsdlUrlKey, paramHelper);
				
				System.out.println("Load On loadSelectedWsDetails method :::::>> " + wsdlUrl + " : " + selectedOperation);
			}
		} catch (MdnException e) {
			e.printStackTrace();
		}			
		Element root = new Element("root");
		return root;
	}		
	
	
	private Element testWebServiceQuery(HttpServletRequest request) {
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		
		List<ParamListItem> resultParam = new ArrayList<ParamListItem>();
		int rowSize = 0;
		List<WebServiceResultRow> paramListItemsAsRow = null;
		
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    String action = request.getParameter("action");
		
		String msgId = request.getParameter("msgId");
 		String wsdlUrl;
		String selectedService;
		String selectedPort;
		String selectedOperation;		 
		
    	try {
    		WebServiceOperationDobj selectedWebService;
	        if (msgId == null || msgId.equalsIgnoreCase(Constants.UNDEFINED) || msgId .equalsIgnoreCase("-1")){//new Q
	    		wsdlUrl = request.getParameter("WSDLUrl");
	    		selectedService = request.getParameter("service");
	    		selectedPort = request.getParameter("port");
	    		selectedOperation = request.getParameter("operation");
	    		selectedWebService = new WebServiceOperationDobj();
	    		selectedWebService.setUrl(wsdlUrl);
	    		selectedWebService.setService(selectedService);
	    		selectedWebService.setPort(selectedPort);
	    		selectedWebService.setOperation(selectedOperation);
	        }else{//existing Q
	        	String wsOperationID = request.getParameter("wsOperationID");
				selectedWebService = dataAgent.getWebServiceOperationByID(Integer.parseInt(wsOperationID));
				wsdlUrl = selectedWebService.getUrl();
				selectedOperation = selectedWebService.getOperation();
				selectedPort = selectedWebService.getPort();
				selectedService = selectedWebService.getService();				
	        }
	        
	        String userInputTestString = request.getParameter("userInputTestValues");
			String separator = " ";
			MdnMessageSeparator mdnMsgSep = null;
			mdnMsgSep = dataAgent.getMessageSeparator();
			if(mdnMsgSep != null){
				separator = mdnMsgSep.getConditionSeperator();
			}		    				        
	        ArrayList<String> userInputList = MessagingUtils.getUserInputForNoKeywordQuery(separator, userInputTestString);

			ParamHelper paramHelper = MessagingUtils.getParamHelper(selectedWebService);
			resultParam = MessagingUtils.executeWebService(request, userInputList, paramHelper);//new ArrayList<ParamListItem>();			
			
			rowSize = paramHelper.getRowSize();
			paramListItemsAsRow = paramHelper.getParamListItemsAsRow();
		} catch (MdnException e) {
			e.printStackTrace();
			return new XmlFormatter().simpleResult(file, action, "Error when getting result: " + e.toString());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} 
		return new XmlFormatter().getWebserviceResult(resultParam, rowSize, paramListItemsAsRow);			

	}
	private Element getResponeseFormat(HttpServletRequest request) {//For add new Query screen
		
		List<ParamListItem> resultParam = new ArrayList<ParamListItem>();
		String responseFormat = "";		
		
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
		String wsdlUrl = request.getParameter("WSDLUrl");
		String service = request.getParameter("service");
		String port = request.getParameter("port");
		String selectedOperation = request.getParameter("operation");
		String userInputTestString = request.getParameter("uiStr");
		
		WebServiceOperationDobj selectedWebService = new WebServiceOperationDobj();
		selectedWebService.setUrl(wsdlUrl);
		selectedWebService.setOperation(selectedOperation);
		selectedWebService.setPort(port);
		selectedWebService.setService(service);		
		
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
        
		ParamHelper paramHelper = MessagingUtils.getParamHelper(selectedWebService);
		resultParam = MessagingUtils.executeWebService(request, userInputList, paramHelper);//new ArrayList<ParamListItem>();			
        
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
			
		return new XmlFormatter().getDefaultTextMsgForResponseXML(responseFormat);
	}
	private Element getWebServiceDescription(HttpServletRequest request) {
		String wsdlUrl = request.getParameter("WSDLUrl");
	    // Get the current language file.
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    String action = request.getParameter("action");
	    String selectedOperation = request.getParameter("selectedOperation");
	    String result = null;
	    try {
	    	Definition definition = WebServiceDefinitionHelper.getWebServiceDefinition(wsdlUrl);
	    	WebServiceDescription webServiceDescription = new WebServiceDescription(wsdlUrl, definition);	    				
			try {
				
				if (definition == null){
					result = "definition element is null";
					return new XmlFormatter().simpleResult(file, action, result);
				}
				else{
					webServiceDescription.setDefinition(definition);
					Element root = new XmlFormatter().getWebServiceDescription(webServiceDescription, selectedOperation);
					return root;
				}				
			} catch (RuntimeException e) {
				e.printStackTrace();
				result = "RuntimeException";
				return new XmlFormatter().simpleResult(file, action, result);
			}

			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			result = "IllegalArgumentException";
			return new XmlFormatter().simpleResult(file, action, result);
		} catch (WSDLException e) {
			e.printStackTrace();
			result = "WSDLException";
			return new XmlFormatter().simpleResult(file, action, result);
		}
	}

	private Element getAllSampleWebServices(HttpServletRequest request) {
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		List<WebServiceDobj> allSamples;
		try {
			allSamples = dataAgent.getAllSampleWebServices();
			Element root = new XmlFormatter().getWebServices(allSamples);
			return root;
		} catch (MdnException e) {
			e.printStackTrace();
			return null;
		}
	}

	private Element getAllThirdPartyWebServices(HttpServletRequest request) {
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		List<WebServiceDobj> allSamples;
		try {
			allSamples = dataAgent.getAllThirdPartyWebServices();
			Element root = new XmlFormatter().getWebServices(allSamples);
			return root;
		} catch (MdnException e) {
			e.printStackTrace();
			return null;
		}
	}
	/*
	private Element getCompsForUpdateQuery1(HttpServletRequest request) {
	    // Get the current language file.
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    String action = request.getParameter("action");
		Element root = new XmlFormatter().getCompsForUpdateQuery1();//file, action
		return root;
	}

	private Element getCompsForUpdateQuery2(HttpServletRequest request) {
	    // Get the current language file.
//	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
//	    String action = request.getParameter("action");
		Element root = new XmlFormatter().getCompsForSelectQuery();//file, action
		return root;
	}

	private Element getSimpleComps(HttpServletRequest request) {
	    // Get the current language file.
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    String action = request.getParameter("action");
		Element root = new XmlFormatter().getSimpleComps();//file, action
		return root;
	}
*/
	private Element getNewSelectQueryBuilder(HttpServletRequest request) {
	    // Get the current language file.
	    //String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    //String action = request.getParameter("action");
	    String viewID = request.getParameter("viewID");

		int intViewId;
		try {
			intViewId = Integer.parseInt(viewID);
		} catch (NumberFormatException e) {
			intViewId = -1;
		}
	    
	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		DataView view = null;
		List<CustomField> customFields = new ArrayList();
		try {
			view = dataAgent.getMetaViewByID(intViewId, true);
			customFields = dataAgent.getAllCustomFields();
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (MdnException e1) {
			e1.printStackTrace();
		}
		
		List<QueryCriteriaDobj> queryCriteria= new Vector<QueryCriteriaDobj>();
		
	    //String viewOrTableID = request.getParameter("ID");
	    //String queryType = request.getParameter("queryType");
	    QueryDobj query = new QueryDobj();
		query.setName("New Query");

		query.setViewOrTableId(intViewId);
		query.setType("select");		
		
		Element root = new XmlFormatter().getSelectQueryBuilder(queryCriteria, view, customFields, query);//file, action
		return root;
	}	

	private Element getNewUpdateQueryBuilder(HttpServletRequest request) {
	    // Get the current language file.
	    //String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    //String action = request.getParameter("action");
	    String tableID = request.getParameter("tableID");
		
		int intTableId;
		try {
			intTableId = Integer.parseInt(tableID);
		} catch (NumberFormatException e) {
			intTableId = -1;
		}
		
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		EntityDobj table = null;
		List<CustomField> customFields = new ArrayList();
		try {
			table = dataAgent.getMetaTableByID(intTableId, true);
			customFields = dataAgent.getAllCustomFields();
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (MdnException e1) {
			e1.printStackTrace();
		}
		
		List<QueryCriteriaDobj> queryCriteria= new Vector<QueryCriteriaDobj>();
		
	    //String viewOrTableID = request.getParameter("ID");
	    //String queryType = request.getParameter("queryType");
	    QueryDobj query = new QueryDobj();
		query.setName("New Query");

		query.setViewOrTableId(intTableId);
		query.setType("update");		
		
		Element root = new XmlFormatter().getUpdateQueryBuilder(queryCriteria, table, customFields, query);//file, action
		return root;
	}
	
	private Element getNewInsertQueryBuilder(HttpServletRequest request) {
	    // Get the current language file.
	    //String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    //String action = request.getParameter("action");
	    String tableID = request.getParameter("tableID");
		int intTableId;
		try {
			intTableId = Integer.parseInt(tableID);
		} catch (NumberFormatException e) {
			intTableId = -1;
		}
	    
	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	    List<QueryCriteriaDobj> allQueryCriteria= new Vector<QueryCriteriaDobj>();
		List<CustomField> customFields = new ArrayList();
		EntityDobj table = null;
		try {
			table = dataAgent.getMetaTableByID(intTableId, true);
			customFields = dataAgent.getAllCustomFields();
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (MdnException e1) {
			e1.printStackTrace();
		}		
		int index = 1;
	  	List<FieldDobj> fields = (List<FieldDobj>)(table.getFields());
	  	for (FieldDobj field : fields){
	  		QueryCriteriaDobj queryCriteria = new QueryCriteriaDobj();
	  		queryCriteria.setUsed(1);
	  		queryCriteria.setName(field.getName());
	  		queryCriteria.setRowNo(index);
	  		queryCriteria.setNumber(index);
	  		queryCriteria.setValueOrCondition("value");
	  		queryCriteria.setParent(table.getName());
	  		queryCriteria.setCompId(QueryCriteriaDobj.COMPARISON_ID_EQUAL);
	  		queryCriteria.setComparison(QueryCriteriaDobj.COMPARISON_EQUAL);
	  		queryCriteria.setConnection(QueryCriteriaDobj.CONNECTION_AND);
	  		queryCriteria.setValue("_____");
	  		queryCriteria.setGrouping(QueryCriteriaDobj.GROUPING_ALL);
	  		allQueryCriteria.add(queryCriteria);
	  		index++;
	  	}	  	

	    //String viewOrTableID = request.getParameter("ID");
	    //String queryType = request.getParameter("queryType");
	    QueryDobj query = new QueryDobj();
		query.setName("New Query");

		query.setViewOrTableId(intTableId);
		query.setType("insert");	  	
	  	
		Element root = new XmlFormatter().getInsertQueryBuilder(allQueryCriteria, table, customFields, query);//file, action
		return root;
	}	
	
	private Element getSelectQueryBuilder(HttpServletRequest request) {
	    // Get the current language file.
	    //String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    //String action = request.getParameter("action");
	    String queryID = request.getParameter("queryID");
	    String objType = request.getParameter("objType");
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		List<QueryCriteriaDobj> queryCriteria= null;
		List<CustomField> customFields = new ArrayList();		
		try {
			if(objType == null || objType.equals("undefined"))
				objType="";
			
			queryCriteria = dataAgent.getQueryCriteriaByQueryID(Integer.parseInt(queryID), objType);
			
			//Custom Fields
			customFields = dataAgent.getAllCustomFields();

		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (MdnException e1) {
			e1.printStackTrace();
		}

		QueryDobj query = null;
		try {
			if(objType != null && objType.equalsIgnoreCase("UR")){
				UserReply ur = dataAgent.getUserReplyById(Integer.parseInt(queryID));
				query = convertQueryObjToUserReply(ur);
			}else
				query = dataAgent.getQueryByID(Integer.parseInt(queryID));
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (MdnException e1) {
			e1.printStackTrace();
		}
		
		DataView view = null;
		if (query!= null && query.getType().equalsIgnoreCase("select"))
		{
			view = query.getDataView(true);
		}	
		
		Element root = new XmlFormatter().getSelectQueryBuilder(queryCriteria, view, customFields, query);//file, action
		return root;
	}

	
	private Element getUpdateQueryBuilder(HttpServletRequest request) {
	    // Get the current language file.
	    //String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    //String action = request.getParameter("action");
	    String queryID = request.getParameter("queryID");
	    String objType = request.getParameter("objType");
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		List<QueryCriteriaDobj> queryCriteria= null;
		List<CustomField> customFields = new ArrayList();

		try {
			if(objType == null || objType.equals("undefined"))
				objType="";
			
			queryCriteria = dataAgent.getQueryCriteriaByQueryID(Integer.parseInt(queryID), objType);
			customFields = dataAgent.getAllCustomFields();
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (MdnException e1) {
			e1.printStackTrace();
		}

		QueryDobj query = null;
		try {
			if(objType != null && objType.equalsIgnoreCase("UR")){
				UserReply ur = dataAgent.getUserReplyById(Integer.parseInt(queryID));
				query = convertQueryObjToUserReply(ur);
			}else
				query = dataAgent.getQueryByID(Integer.parseInt(queryID));
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (MdnException e1) {
			e1.printStackTrace();
		}
		EntityDobj table = null;
		if (query.getType().equalsIgnoreCase("update")){
			table = query.getTable(true);
		}		
		
		Element root = new XmlFormatter().getUpdateQueryBuilder(queryCriteria, table, customFields, query);//file, action
		return root;
	}
	
	private Element getInsertQueryBuilder(HttpServletRequest request) {
	    // Get the current language file.
	    //String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    //String action = request.getParameter("action");
	    String queryID = request.getParameter("queryID");
	    String objType = request.getParameter("objType");
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		List<QueryCriteriaDobj> queryCriteria= null;
		List<CustomField> customFields = new ArrayList();
		try {
			if(objType == null || objType.equals("undefined"))
				objType="";
			
			queryCriteria = dataAgent.getQueryCriteriaByQueryID(Integer.parseInt(queryID), objType);
			customFields = dataAgent.getAllCustomFields();
			
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (MdnException e1) {
			e1.printStackTrace();
		}

		QueryDobj query = null;
		try {
			if(objType != null && objType.equalsIgnoreCase("UR")){
				UserReply ur = dataAgent.getUserReplyById(Integer.parseInt(queryID));
				query = convertQueryObjToUserReply(ur);
			}else
				query = dataAgent.getQueryByID(Integer.parseInt(queryID));
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (MdnException e1) {
			e1.printStackTrace();
		}
		EntityDobj table = null;
		if (query.getType().equalsIgnoreCase("insert") ){
			table = query.getTable(true);
		}	
		
		Element root = new XmlFormatter().getInsertQueryBuilder(queryCriteria, table, customFields, query);//file, action
		return root;
	}	
	
	
	private Element getWebserviceQueryBuilder(HttpServletRequest request) {
	    // Get the current language file.
	    //String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    //String action = request.getParameter("action");
	    String queryID = request.getParameter("queryID");
	    String objType = request.getParameter("objType");
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		List<QueryCriteriaDobj> queryCriteria= null;
		List<CustomField> customFields = new ArrayList();		
		try {
			if(objType == null || objType.equals("undefined"))
				objType="";
			
			queryCriteria = dataAgent.getQueryCriteriaByQueryID(Integer.parseInt(queryID), objType);
			
			//Custom Fields
			customFields = dataAgent.getAllCustomFields();

		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (MdnException e1) {
			e1.printStackTrace();
		}

		QueryDobj query = null;
		try {
			if(objType != null && objType.equalsIgnoreCase("UR")){
				UserReply ur = dataAgent.getUserReplyById(Integer.parseInt(queryID));
				query = convertQueryObjToUserReply(ur);
			}else
				query = dataAgent.getQueryByID(Integer.parseInt(queryID));
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (MdnException e1) {
			e1.printStackTrace();
		}
		
//		DataView view = null;
//		if (query!= null && query.getType().equalsIgnoreCase("select"))
//		{
//			view = query.getDataView(true);
//		}	
		
		Element root = new XmlFormatter().getWSQueryBuilder(queryCriteria, customFields, query);//getSelectQueryBuilder(queryCriteria, view, customFields, query);//file, action
		return root;
	}
		
/*	private Element getCompsForSelectQuery(HttpServletRequest request) {
	    // Get the current language file.
	    //String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    //String action = request.getParameter("action");
		Element root = new XmlFormatter().getCompsForSelectQuery();//file, action
		return root;
	}
*/


	private Element getDbConnection(HttpServletRequest request){
	    Element root = null;
	    String dbName = request.getParameter("dbName");
	    String projectId = request.getParameter("projectId");
	    int intProjectId = 1;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e1) {
			intProjectId = 1;
		}
	    // Get the user from the database.
	    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	    DataSourceDobj dbConnection;
		try {
			dbConnection = dataAgent.getDbConnectionByName(intProjectId, dbName);
			root = new XmlFormatter().dbConnection(dbConnection);
		} catch (MdnException e) {
			e.printStackTrace();
		}
	    
	    return root;
	  }
	
	private Element testDbConnection(HttpServletRequest request){
	    // Get the DbConnection object.
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	    DbConnection dbConn = dataAgent.dbConnection(request);
	    // Handle the request.
	    String result = dataAgent.handleTestDbConnection(dbConn);
	    // Get the current language file.
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    String action = request.getParameter("action");
	    Element root = new XmlFormatter().testDbConnectionResult(file, action, result);
	    return root;
	  }
	
	/*private Element dbWizard(HttpServletRequest request){
		DbDataHandler handler = new DbDataHandler();
		String result = handler.handleDbWizard(request);
		String action = request.getParameter("action");
		String step = request.getParameter("step");
		// Get the current language file.
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    Element root = new XmlFormatter().dbWizardResult(file, action, step, result);
		return root;
	}*/
	
	private Element dbWizardTables(HttpServletRequest request){
		DbDataHandler handler = new DbDataHandler();
		Vector<MetaTable> tables = handler.handleDbWizardTables(request);
		Element root = new XmlFormatter().dbWizardTablesResult(tables);
		return root;
	}
	
	private Element dbWizardFields(HttpServletRequest request){
		DbDataHandler handler = new DbDataHandler();
		Vector<MetaField> tables = handler.handleDbWizardFields(request);
		Element root = new XmlFormatter().dbWizardFieldsResult(tables);
		return root;
	}
	
	private Element dbImportTables(HttpServletRequest request){
		DbDataHandler handler = new DbDataHandler();
		Vector<MetaTable> tables = handler.handleDbImportTables(request);
		Element root = new XmlFormatter().dbImportTablesResult(tables);
		return root;
	}
	
	private Element dbAddRelation(HttpServletRequest request){
		DbDataHandler handler = new DbDataHandler();
		String result = handler.handleAddRelation(request);
	    // Get the current language file.
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    String action = request.getParameter("action");
	    Element root = new XmlFormatter().dbAddRelationResult(file, action, result);
		return root;
	}
	
	private Element dbDeleteRelation(HttpServletRequest request){
		DbDataHandler handler = new DbDataHandler();
		String result = handler.handleDeleteRelation(request);
	    // Get the current language file.
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    String action = request.getParameter("action");
	    Element root = new XmlFormatter().dbDeleteRelationResult(file, action, result);
		return root;
	}
	
	private Element dbWizardGetRelations(HttpServletRequest request){
		DbDataHandler handler = new DbDataHandler();
		Vector<MetaRelation> relations = handler.handleGetRelations(request);
		Element root = new XmlFormatter().dbGetRelationsResult(relations);
		return root;
	}
	
	private Element abortWizard(HttpServletRequest request){
		DbDataHandler handler = new DbDataHandler();
		String result = handler.handleAbortWizard(request);
	    // Get the current language file.
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    String action = request.getParameter("action");
	    Element root = new XmlFormatter().abortWizardResult(file, action, result);
		return root;
	}
	/*
	private Element finishWizard(HttpServletRequest request){
		DbDataHandler handler = new DbDataHandler();
		String result = handler.handleFinishWizard(request);
	    // Get the current language file.
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    String action = request.getParameter("action");
	    Element root = new XmlFormatter().finishWizardResult(file, action, result);
		return root;
	}
	*/
	private Element saveConnection(HttpServletRequest request){
		Element root = null;

		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
	    DbConnection dbConn = dataAgent.dbConnection(request);
	    String result = dataAgent.handleTestDbConnection(dbConn);	
	    
		String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
		String action = request.getParameter("action");	    
	    
		if (!result.equals(RmiAgent.CONNECTED)){
			result = "Error: This is invalid connection.";
			root = new XmlFormatter().simpleResult(file, action, result);			
			return root;	    	
	    }
		
		// Get the DbConnection object.
	    String dbName = request.getParameter("name");
	    String projectId = request.getParameter("projectId");
	    int intProjectId = 1;
		try {
			intProjectId = Integer.parseInt(projectId);
			System.out.println("projectId in connection save: [" + projectId + "]");
		} catch (NumberFormatException e1) {
			intProjectId = 1;
		}
	    // Get the user from the database.
	    DataSourceDobj dbConnection;
		try {
			dbConnection = dataAgent.getDbConnectionByName(intProjectId, dbName);			
		} catch (MdnException e) {
			e.printStackTrace();
			result = "Error: This is invalid connection.";
			root = new XmlFormatter().simpleResult(file, action, result);			
			return root;
		}
	    //String url = request.getParameter("url");
	    //String schema = request.getParameter("schema");
	    //String driver = request.getParameter("driver");
	    //String username = request.getParameter("username");
	    //String password = request.getParameter("password");
	    //String mirrorString = request.getParameter("mirrorred");
	    //boolean mirror = dbConn.getMirrorred();
	    /*if (mirrorString != null){
	    	mirror = mirrorString.equalsIgnoreCase("true") ? true : false;
	    }*/
	    //String wholeUrl = url + ":" + schema;
	    if (dbConnection instanceof JdbcDataSourceDobj){
	    	
	    	((JdbcDataSourceDobj)dbConnection).setJdbcUrl(dbConn.getUrl());
	    	//((JdbcDataSourceDobj)dbConnection).setJdbcCatalog(dbConn.getSchema());
	    	((JdbcDataSourceDobj)dbConnection).setJdbcUser(dbConn.getUsername());
	    	((JdbcDataSourceDobj)dbConnection).setJdbcPassword(dbConn.getPassword());
			//Find JdbcDriver
			JdbcDriver jdbcDriver = dbConn.getJdbcDriver();
			if (jdbcDriver == null){
				result = "Error: This is invalid jdbc driver.";
				root = new XmlFormatter().simpleResult(file, action, result);
				return root;
			}
			else{
				((JdbcDataSourceDobj)dbConnection).setDriverDobj(jdbcDriver);
				((JdbcDataSourceDobj)dbConnection).setJdbcDriverId(jdbcDriver.getId());	 				
			}
			/*try {
				jdbcDriver = dataAgent.getJdbcDriverByName(driver);
			} catch (MdnException e) {
				e.printStackTrace();
				result = "Error: This is invalid jdbc driver.";
				root = new XmlFormatter().simpleResult(file, action, result);
			}*/
   	
	    }
	    dbConnection.setIsMirrored(dbConn.getMirrorred() == 1 ? true : false);
	    dbConnection.setProjectId(intProjectId);
	    dbConnection.setDelStatus(Constants.MARKED_AS_NOT_DELETED_INT);
	    dbConnection.setState(DataObject.IN_DB);
	    try {
			dbConnection.save();
			return new XmlFormatter().dbConnection(dbConnection);
		} catch (DataSourceException e) {
			e.printStackTrace();
			result = "Error: Save error.";
			root = new XmlFormatter().simpleResult(file, action, result);
			return root;
		}	    
	}
	
	private Element getTables(HttpServletRequest request){
	    Element root = null;
	    String dbConnID = request.getParameter("connID");
	    String projectId = request.getParameter("projectId");
	    
	    int intProjectId;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e) {
			intProjectId = 1;
		}
	    
	    int intDbConnId;
		try {
			intDbConnId = Integer.parseInt(dbConnID);
		    
		    DbDataHandler handler = new DbDataHandler();
		    return handler.handleGetTables(intProjectId, intDbConnId);
		} catch (NumberFormatException e) {
		    //if (dbConnID == null || dbConnID.equals("") || dbConnID.equalsIgnoreCase("null") || dbConnID.equalsIgnoreCase("undefined")){
			String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
			String action = request.getParameter("action");
			String result = "NoData";
			root = new XmlFormatter().simpleResult(file, action, result);
			return root;
		}
	}
/*	
	private Element getJoins(HttpServletRequest request){
	    Element root = null;
	    String dbConnID = request.getParameter("connID");
		String projectId = request.getParameter("projectId");
		int intProjectId = Integer.parseInt(projectId);	
	    DbDataHandler handler = new DbDataHandler();
	    return handler.handleGetJoins(intProjectId, Integer.parseInt(dbConnID));
	}	
*/		
	private Element getTablesView(HttpServletRequest request){
	    Element root = null;
		String projectId = request.getParameter("projectId");
		int intProjectId;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e) {
			intProjectId = 1;
		}
	    String dbConnID = request.getParameter("connID");
	    String viewID = request.getParameter("viewID");
	    DbDataHandler handler = new DbDataHandler();
	    if (viewID == null || viewID == "" || viewID.equalsIgnoreCase("null"))
	    {
	    	viewID = "0";
	    }
	    Element result = handler.handleGetTablesView(intProjectId, Integer.parseInt(dbConnID), Integer.parseInt(viewID));
	    
	    return result;
	}	
	/**
	 * Get All the views for the specified database
	 * @param request
	 * @return
	 */
/*
	private Element getViews(HttpServletRequest request){
	    Element root = null;
	    String dbConnID = request.getParameter("connID");
	    DbDataHandler handler = new DbDataHandler();
		String projectId = request.getParameter("projectId");
		int intProjectId = Integer.parseInt(projectId);
	    return handler.handleGetViews(intProjectId, Integer.parseInt(dbConnID));
	}	
*/	
	/**
	 * get specified view based on view ID
	 * @param request
	 * @return
	 */
	private Element getView(HttpServletRequest request){
	    Element root = null;
	    String viewID = request.getParameter("viewID");
	    String includeFields = request.getParameter("includeFields");
	    boolean incl = includeFields != null ? Boolean.getBoolean(includeFields) : false;
	    DbDataHandler handler = new DbDataHandler();
	    DataView view;
		try {
			if (viewID == null || viewID.equalsIgnoreCase("null") || viewID.equalsIgnoreCase("undefined"))
			{
				view = new DataView();
				view.setName("");
				view.setDescription("");
			}
			else
			{
				view = handler.getView(Integer.parseInt(viewID), incl);
			}
			
			return new XmlFormatter().getView(view);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (MdnException e) {
			e.printStackTrace();
		}
		return null;
	}	
	private Element getViewField(HttpServletRequest request){
	    Element root = null;
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    String action = request.getParameter("action");
	    String fieldID = request.getParameter("fieldID");
	    DbDataHandler handler = new DbDataHandler();
	    return handler.getViewField(action, file, fieldID);
	  }	
	private Element saveViewField(HttpServletRequest request){
	    Element root = null;
	    // Get the current language file.
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    String action = request.getParameter("action");
	    String fieldID = request.getParameter("fieldID");
	    String fieldDescription = request.getParameter("fieldDescription");
	    String fieldDisplayName = request.getParameter("fieldDisplayName");
	    String namingField = request.getParameter("namingField");
	    DbDataHandler handler = new DbDataHandler();

	    return handler.saveViewField(action, fieldID, fieldDescription, fieldDisplayName, Boolean.getBoolean(namingField), file);
	  }	
	
	private Element getTableFieldForView(HttpServletRequest request){
	    Element root = null;
	    String fieldID = request.getParameter("fieldID");
	    String includeTable = request.getParameter("includeTable");
	    boolean incl = includeTable != null ? Boolean.getBoolean(includeTable) : false;
	    DbDataHandler handler = new DbDataHandler();
	    Element field = null;
	    try {
	    	field = handler.getTableFieldForView(Integer.parseInt(fieldID), incl);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (MdnException e) {
			e.printStackTrace();
		}
		return field;
	  }	

	private Element addNewView(HttpServletRequest request) {
		String dbConnID = request.getParameter("connID");
		int connectionID = Integer.parseInt(dbConnID);	
		String projectId = request.getParameter("projectId");
		int intProjectId;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e2) {
			intProjectId =1;
		}			
		
		String viewName = request.getParameter("viewName");
		String viewDescription = request.getParameter("viewDescription");
		String selectedTables = request.getParameter("selectedTables");		
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		String result = "";
		DataView check = null;
		try {
			check = dataAgent.getMetaViewByName(viewName, connectionID);
		} catch (MdnException e) {
			e.printStackTrace();
		}
		boolean valid = true;
		if (check != null){
			if (check.getName().equals(viewName) ){
				result = "Duplicate view name";
				valid = false;
			} 
		}	
		String newID = "";
		if (valid)
		{
			DataSourceDobj ds;
			
			try {
				ds = dataAgent.getDbConnectionByID(intProjectId, connectionID);
				if (ds != null){
		            // create the entity
					DataView newView = new DataView();	
		            newView.setName(viewName);
		            newView.setDescription(viewDescription);
		            newView.setSourceDsId(connectionID);
	                // ensure entities are loaded
	                ds.getDataViews();

	                // add the entity
	                ds.addDataView(newView);
	                
	                String[] listOfSelectedIds = selectedTables.split(",");
	                for (int i = 0; i< listOfSelectedIds.length; i++){
	                	String id = listOfSelectedIds[i];
	                	int fieldID = Integer.parseInt(id);
	                	//Get Table Field object
	                	FieldDobj f = dataAgent.getMetaField(fieldID);
	                	int tableID = f.getEntityId();
	                	//Get Table object
	                	EntityDobj entObj = dataAgent.getMetaTableByID(tableID, false);
	                    // create a DataViewField
	                	DataViewField dvf = new DataViewField();
	                    String name = entObj.getName() + "." + f.getName();
	                    dvf.setName(name);
	                    dvf.setDescription(name);
	                    dvf.setDisplayName(f.getName());
	                    dvf.setSourceField(f.getName());
	                    dvf.setSourceEntity(entObj.getName());
	                    dvf.setType(f.getType());
	                    newView.addField(dvf);
	                }
	                
	                // save ds
	                newView.setState(DataObject.NEW);
	                newView.setDelStatus(Constants.MARKED_AS_NOT_DELETED_INT);
	                newView.save();
	                newID = newView.getId() + "";
	                result = "OK";
				}			
			} catch (MdnException e1) {
				e1.printStackTrace();
				result = "Database error";
			} catch (DataSourceException e) {
				e.printStackTrace();
				result = "Database error";
			}			
		}
	    
	    // Get the current language file.
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    String action = request.getParameter("action");
	    return new XmlFormatter().editAddViewResult(file, action, result, newID, String.valueOf(intProjectId));
	}
	
	private Element saveView(HttpServletRequest request) {
		String dbConnID = request.getParameter("connID");
		int connectionID = Integer.parseInt(dbConnID);	
		String projectId = request.getParameter("projectId");
		int intProjectId;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e2) {
			intProjectId =1;
		}			
		
		String viewName = request.getParameter("viewName");
		String viewDescription = request.getParameter("viewDescription");
		String selectedTables = request.getParameter("selectedTables");		
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		String result = "";
		DataView dataView = null;
		boolean valid = true;
		try {
			dataView = dataAgent.getMetaViewByName(viewName, connectionID);
		} catch (MdnException e) {
			e.printStackTrace();
			result = "View " + viewName + " does not exist.";
			valid = false;
		}
		if (dataView == null){
			if (dataView.getName().equals(viewName) ){
				result = "View " + viewName + " does not exist.";
				valid = false;
			} 
		}	
		String newID = "";
		if (valid)
		{
			DataSourceDobj ds;
			
			try {
				ds = dataAgent.getDbConnectionByID(intProjectId, connectionID);
				if (ds != null){
		            // create the entity
					//DataView newView = check;	
//		            newView.setName(viewName);
		            dataView.setDescription(viewDescription);
//		            check.setSourceDsId(connectionID);
	                // ensure entities are loaded
	                ds.getDataViews();

	                // add the entity
	                //ds.addDataView(newView);
	                
	                String[] listOfSelectedIds = selectedTables.split(",");
	                for (int i = 0; i< listOfSelectedIds.length; i++){
	                	String id = listOfSelectedIds[i];
	                	int fieldID = Integer.parseInt(id);
	                	//Get Table Field object
	                	FieldDobj f = dataAgent.getMetaField(fieldID);
	                	int tableID = f.getEntityId();
	                	//Get Table object
	                	EntityDobj entObj = dataAgent.getMetaTableByID(tableID, false);
	                	String fieldName = entObj.getName() + "." + f.getName();
	                	
	                	try {
	                		//DataViewField dvf = check.getField(fieldName);
	                		DataViewField dvf = dataView.getDataViewField(entObj.getName(), f.getName());
	                		//found it, do not change anything
	                		if (dvf == null){
			                	dvf = new DataViewField();		                    
			                	dvf.setState(DataObject.NEW);
			                	dvf.setName(fieldName);
			                    dvf.setDescription(fieldName);
			                    dvf.setDisplayName(f.getName());
			                    dvf.setSourceField(f.getName());
			                    dvf.setSourceEntity(entObj.getName());
			                    dvf.setType(f.getType());
			                    dataView.addField(dvf);		                			
	                		}
						} catch (RuntimeException e) {
							//e.printStackTrace();
							
							//new one, create this one
		                    // create a DataViewField
		                	DataViewField dvf = new DataViewField();		                    
		                	dvf.setState(DataObject.NEW);
		                	dvf.setName(fieldName);
		                    dvf.setDescription(fieldName);
		                    dvf.setDisplayName(f.getName());
		                    dvf.setSourceField(f.getName());
		                    dvf.setSourceEntity(entObj.getName());
		                    dvf.setType(f.getType());
		                    dataView.addField(dvf);							
						}
	                }
	                
	                Vector<DataViewField> fields = dataView.getFields();
	                Vector<DataViewField> removedFields = new Vector<DataViewField>();
	                for (DataViewField field : fields){
		                boolean found = false;
	                	for (int i = 0; i< listOfSelectedIds.length; i++){
		                	String id = listOfSelectedIds[i];
		                	int fieldID = Integer.parseInt(id);
		                	//Get Table Field object
		                	FieldDobj f = dataAgent.getMetaField(fieldID);
		                	int tableID = f.getEntityId();
		                	//Get Table object
		                	EntityDobj entObj = dataAgent.getMetaTableByID(tableID, false);
		                	String fieldName = entObj.getName() + "." + f.getName();
		                	
		                	if (fieldName.equalsIgnoreCase(field.getName())){
		                		found = true;
		                	}
		                }	 
	                	if (!found){
	                		field.setState(DataObject.IN_DB);
	                		removedFields.add(field);	                		
	                	}
	                }
	                
	                for (DataViewField field : removedFields){
	                	dataView.removeField(field, true);
	                }
	                
	                // save ds
	                dataView.setState(DataObject.IN_DB);
	                dataView.setDelStatus(Constants.MARKED_AS_NOT_DELETED_INT);
	                dataView.save();
	                newID = dataView.getId() + "";
	                result = "OK";
				}			
			} catch (MdnException e1) {
				e1.printStackTrace();
				result = "Database error";
			} catch (DataSourceException e) {
				e.printStackTrace();
				result = "Database error";
			}			
		}
	    
	    // Get the current language file.
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    String action = request.getParameter("action");
	    return new XmlFormatter().editAddViewResult(file, action, result, newID, String.valueOf(intProjectId));
	}	
	private Element saveQuery(HttpServletRequest request) {
		String saveState = request.getParameter("saveState");
		int saveStateInt = Integer.parseInt(saveState);
		/*String dbConnID = request.getParameter("connID");

		int connectionID;
		try {
			connectionID = Integer.parseInt(dbConnID);
		} catch (NumberFormatException e2) {
			connectionID = 0;
		}		*/
		String queryIdStr = request.getParameter("queryId");
		int queryId;
		try {
			queryId = Integer.parseInt(queryIdStr);
		} catch (NumberFormatException e2) {
			queryId = -1;
		}
//		String queryName = request.getParameter("queryName");
//		String queryDescription = request.getParameter("queryDescription");
		String strTableID = request.getParameter("tableID");
		int tableID;
		try {
			tableID = Integer.parseInt(strTableID);
		} catch (NumberFormatException e2) {
			tableID = -1;
		}		
		
		String projectId = request.getParameter("projectId");
		int intProjectId = 1;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e2) {
			intProjectId = 1;
		}
		
		String queryType = request.getParameter("queryType");
		String sqlText = request.getParameter("sqlText");
		//rows, types, useds, indents, valueOrConditions, numbers, fields, compIDs, comparisons, values, connections, value2s, grouping
		String ids = request.getParameter("ids");
		String rows = request.getParameter("rows");		
		String types = request.getParameter("types");
		String useds = request.getParameter("useds");
		String indents = request.getParameter("indents");
		String valueOrConditions = request.getParameter("valueOrConditions");
		String numbers = request.getParameter("numbers");
		String parents = request.getParameter("parents");
		String fields = request.getParameter("fields");
		String compIDs = request.getParameter("compIDs");
		String comparisons = request.getParameter("comparisons");
		String values = request.getParameter("values");
		String connections = request.getParameter("connections");
		String value2s = request.getParameter("value2s");
		String groupings = request.getParameter("groupings");
		String sorts = request.getParameter("sorts");		
		String groupFieldId = request.getParameter("groupFieldId");	
		String userInputSeqs = request.getParameter("userInputSeqs");
		String userInputSeqs2 = request.getParameter("userInputSeqs2");
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		String result = "";

		//DataSourceDobj ds;
		
		try {
			//ds = dataAgent.getDbConnectionByID(connectionID);
			//if (ds != null){
	            // create the entity
				QueryDobj query = new QueryDobj();
				if (saveStateInt == DataObject.IN_DB){
					query = dataAgent.getQueryByID(queryId);
				}
	            
//				if(queryName != null && !queryName.equalsIgnoreCase("undefined") && !queryName.equalsIgnoreCase(""))
//					query.setName(queryName);
//				if(queryDescription != null && !queryDescription.equalsIgnoreCase("undefined") && !queryDescription.equalsIgnoreCase(""))
//					query.setDescription(queryDescription);
	            query.setViewOrTableId(tableID);
	            query.setType(queryType);
	            query.setCriteriaString(sqlText);
	            if (sorts != null && !sorts.equalsIgnoreCase(Constants.UNDEFINED)&& !sorts.equals("") && !sorts.equalsIgnoreCase("null"))
	            	query.setSortString(sorts);
	            if (groupFieldId != null && !groupFieldId.equalsIgnoreCase(Constants.UNDEFINED) && !groupFieldId.equalsIgnoreCase("null") && !groupFieldId.equals("") && !groupFieldId.equalsIgnoreCase("-1")){
	            	int groupFieldIdInt = 0;
					try {
						groupFieldIdInt = Integer.parseInt(groupFieldId);
					} catch (NumberFormatException e) {
						//In this case, group field id is name.
						groupFieldIdInt = getGroupFieldIdByName(query, groupFieldId);
					}
	            	query.setGroupFieldId(groupFieldIdInt);	
	            }
	            	
	            
                // save ds
	            query.setProjectId(intProjectId);
                query.setState(saveStateInt);
                query.setDelStatus(Constants.MARKED_AS_NOT_DELETED_INT);
                query.save();
                
	            int newQueryID = query.getId();
                System.out.println("new query ID " + newQueryID);
	            
                String[] idsList = ids.split(",");
                String[] rowsList = rows.split(",");
                String[] typesList = types.split(",");
                String[] usedsList = useds.split(",");
                String[] indentsList = indents.split(",");
                String[] valueOrConditionsList = valueOrConditions.split(",");        
                
                String[] numbersList = null;
                if (numbers != null)
                	numbersList = numbers.split(",");
 
                String[] parentsList = null;
                if (parents != null)
                	parentsList = parents.split(",");                
                
                String[] fieldsList = fields.split(",");
                String[] compIDsList = compIDs.split(",");
                String[] comparisonsList = comparisons.split(",");
                String[] valuesList = values.split(",");
                String[] connectionsList = connections.split(",");
                String[] value2sList = value2s.split(",");
                String[] groupingsList = groupings.split(",");
                String[] userInputSeqsList = userInputSeqs.split(",");
                String[] userInputSeqs2List = userInputSeqs2.split(",");
        		int index = 1;
        		
        		//delete all of the old criteria data
				if (saveStateInt == DataObject.IN_DB){
					//need to delete all the old query criteria
					List<QueryCriteriaDobj> queryCriteria = dataAgent.getQueryCriteriaByQueryID(queryId, "");
					for (QueryCriteriaDobj queryCriteriaDobj : queryCriteria){
						queryCriteriaDobj.delete();
					}
				}			
				
        		boolean empty = false;
        		if (rowsList.length == 1){
        			String numberList =numbersList[0]; 
        			if (numberList.equals("") || numberList.equals(Constants.UNDEFINED)){
        				//There is no query criteria need to save
        				empty = true;
        				result = "OK";
        			}
        		}
        		
        		if (!empty){
        			for (int i = 0; i< rowsList.length; i++){
                    	String id = idsList[i];
                    	String row = rowsList[i];
                    	String type = typesList[i];
                    	String used = usedsList[i];
                    	String indent = indentsList[i];
                    	String valueOrCondition = valueOrConditionsList[i];
                    	
                    	String parent = null;
                    	if (parentsList != null && parentsList.length == rowsList.length)
                    		parent = parentsList[i];
                    	
                    	String number = null;
                    	if (numbersList != null && numbersList.length == rowsList.length)
                    		number = numbersList[i];                	
                    	
                    	String field = fieldsList[i];
                    	String compID = compIDsList[i];
                    	String comparison = comparisonsList[i];
                    	String value = valuesList[i];               	
                    	String connection = connectionsList[i];
                    	String value2 = value2sList[i];               	
                    	String grouping = groupingsList[i];
                    	
                    	String userInputSeq = null;
                    	if (userInputSeqsList != null && userInputSeqsList.length > i)//if (userInputSeqsList != null && userInputSeqsList.length == rowsList.length)
                    		userInputSeq = userInputSeqsList[i];
                    	
                    	String userInputSeq2 = null;
                    	if (userInputSeqs2List != null && userInputSeqs2List.length > i)//if (userInputSeqs2List != null && userInputSeqs2List.length == rowsList.length)
                    		userInputSeq2 = userInputSeqs2List[i];
                    	
                    	// create a Query Criteria object
                    	QueryCriteriaDobj queryCriteriaObj = new QueryCriteriaDobj();
                    	
                    	int queryCriteriaSaveState = DataObject.NEW;
                    	/*if (id != null && !id.equalsIgnoreCase("undefined") && !id.equalsIgnoreCase("-1")){
                    		try {
    							queryCriteriaObj = dataAgent.getQueryCriteriaByID(Integer.parseInt(id));
    							queryCriteriaSaveState = DataObject.IN_DB;
    						} catch (NumberFormatException e) {
    							queryCriteriaSaveState = DataObject.NEW;
    						}
                    	}*/
                    	
                    	queryCriteriaObj.setQueryId(newQueryID);
                    	queryCriteriaObj.setValueOrCondition(valueOrCondition);
                    	int rowInt;
    					try {
    						rowInt = Integer.parseInt(row);
    					} catch (NumberFormatException e) {
    						rowInt = index;
    					}
                        queryCriteriaObj.setRowNo(rowInt);
                        queryCriteriaObj.setType(type);
                        queryCriteriaObj.setUsed(Boolean.valueOf(used).booleanValue() ? 1 : 0);
                        queryCriteriaObj.setIndent(Integer.parseInt(indent));
                        queryCriteriaObj.setParent(parent == null ? "" : parent);
                        queryCriteriaObj.setNumber(number == null ? 0 : Integer.parseInt(number));
                        queryCriteriaObj.setName(field);
                        queryCriteriaObj.setCompId(Integer.parseInt(compID));
                        queryCriteriaObj.setComparison(comparison);
                        queryCriteriaObj.setValue(value);
                        queryCriteriaObj.setConnection(connection);
                        queryCriteriaObj.setValue2(value2);
                        queryCriteriaObj.setGrouping(grouping);
                        //if (userInputSeq != null && !userInputSeq.equals("") && !userInputSeq.equalsIgnoreCase("undefined")){
                        	queryCriteriaObj.setValueUserInputSeq(userInputSeq);
                        //}
                        
                        //if (userInputSeq2 != null && !userInputSeq2.equals("") && !userInputSeq2.equalsIgnoreCase("undefined")){
                        	queryCriteriaObj.setValue2UserInputSeq(userInputSeq2);
                        //}
                        
                        queryCriteriaObj.setState(queryCriteriaSaveState);
                        queryCriteriaObj.save();
                        index++;
                    }        			
        		}
                result = "OK";
			//}			
		} catch (MdnException e1) {
			e1.printStackTrace();
			result = "Database error";
		} catch (DataSourceException e) {
			e.printStackTrace();
			result = "Database error";
		}			
	    // Get the current language file.
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    String action = request.getParameter("action");
	    return new XmlFormatter().editResult(file, action, result);
	}	

	private int getGroupFieldIdByName(QueryDobj query, String groupFieldName) {
		int groupFieldId = 0;
		//This situation only happens when it is select query
	  	Object[] fields = query.getDataView (true).getFields ().toArray();
	  	for (int i = 0; i < fields.length; i++){
	  		DataViewField field = (DataViewField)fields[i];
			if (groupFieldName.equalsIgnoreCase(field.getName())){
				groupFieldId = field.getId();				
			}				
	  	}		
		
		return groupFieldId;
	}

	private Element saveQueryMsgProps(HttpServletRequest request){
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
		
		String saveState = request.getParameter("saveState");
		int saveStateInt = Integer.parseInt(saveState);
		String queryIdStr = request.getParameter("queryId");
		int queryId = Integer.parseInt(queryIdStr);
		String queryName = request.getParameter("queryName");
		String queryDescription = request.getParameter("queryDescription");
		String strTableID = request.getParameter("tableID");
		String viewOrTableIdOld = request.getParameter("viewOrTableIdInDB");
    	String timeout = request.getParameter("timeout");		    	

		String queryType = request.getParameter("queryType");
		
	    String dbId = request.getParameter("dbId");	    

	    String projectId = request.getParameter("projectId");
	    int intProjectId = 1;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e1) {
			intProjectId = 1;
		}	    
	    
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		String result = MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL);

		int dbIdInt = -1;
		QueryDobj query = new QueryDobj();		
		try {
				if (saveStateInt == DataObject.IN_DB){
					query = dataAgent.getQueryByID(queryId);
				}
				query.setDelStatus(Integer.parseInt(Constants.MARKED_AS_NOT_DELETED));
		    	if(queryName == null || queryName.equals("") || queryName.equals(Constants.UNDEFINED))
		    		result = MessageConstants.getMessage(file, MessageConstants.MISSING_QUERY_NAME);
			    else if(dataAgent.getQueryByName(queryName, intProjectId) != null && query.getName()!= null && !queryName.equals(query.getName())) 
			    	result = MessageConstants.getMessage(file, MessageConstants.DUPLICATE_NAME);
		    	
		    	else if(saveStateInt == 0){
			    	if(dbId == null || dbId.equals("") || dbId.equals(Constants.UNDEFINED))				
			    		result = MessageConstants.getMessage(file, MessageConstants.MISSING_DB);
				    else if(strTableID == null || strTableID.equals("") || strTableID.equals(Constants.UNDEFINED))
				    	result = MessageConstants.getMessage(file, MessageConstants.MISSING_VIEW_TBL);
	
			    	if(result.equalsIgnoreCase("OK")){
				    	try{
				    		dbIdInt = Integer.parseInt(dbId);
				    	}catch(NumberFormatException e){
				    		String selectedDbName = dbId;
				    		dbIdInt = (dataAgent.getDbConnectionByName(intProjectId, selectedDbName)).getId();
				    	}
			            int tableID = Integer.parseInt(strTableID);
			            query.setViewOrTableId(tableID);
			            query.setType(queryType);
				    	query.setDatabaseId(dbIdInt);
			    	}
		    	}
		    	if(result.equalsIgnoreCase("OK")){
			    	try{
			    		int timeoutint = Integer.parseInt(timeout);
			    		if(timeoutint <= 0)
			    			query.setTimeout("5");
			    		else
			    			query.setTimeout(timeout);
			    	}catch(NumberFormatException e){
			    		//userReply.setTimeout("5");//Default is 5 Seconds
			    	}
		    		
		    		query.setName(queryName);
		            query.setDescription(queryDescription);
			    	
	                query.setState(saveStateInt);
	                query.setDatasourceStatus(1);
	                query.save();

	                int newQueryID = query.getId();
	                System.out.println("new query ID " + newQueryID);
		    	}
		} catch (MdnException e1) {
			e1.printStackTrace();
			result = "Database error";
		} catch (DataSourceException e) {
			e.printStackTrace();
			result = "Database error";
		}			
	    // Get the current language file.

		return new XmlFormatter().saveQueryMsgPropsXML(file, result, query, viewOrTableIdOld);	    
	}	

	private Element saveMsgDetailsInfo(HttpServletRequest request){
		String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);		
		
		String saveState = request.getParameter("saveState");
		int saveStateInt = Integer.parseInt(saveState);
		String msgId = request.getParameter("msgId");

	    String emailId = request.getParameter("emailId");
	    String mobileStatus = request.getParameter("mobileStatus");	    
	    String imStatus = request.getParameter("imStatus");	    
	    String emailKeyword = request.getParameter("emailKeyword");
	    String smsKeyword = request.getParameter("smsKeyword");
	    String imKeyword = request.getParameter("imKeyword");
	    String emailDisplayResult = request.getParameter("emailDisplayResult");	    
	    String mobileDisplayResult = request.getParameter("mobileDisplayResult");
	    String imDisplayResult = request.getParameter("imDisplayResult");	
	    String responseFormat = request.getParameter("responseFormat");
	    
	    String result = MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL);
	    
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		QueryDobj msgQuery = new QueryDobj();
		try {
			if (saveStateInt == DataObject.IN_DB)
				msgQuery = dataAgent.getQueryByID(Integer.parseInt(msgId));
			
			msgQuery.setDelStatus(Integer.parseInt(Constants.MARKED_AS_NOT_DELETED));
			String searchTerm = dataAgent.getGuestMsgInfo("Email").getSearchTerm();
			
			if(emailId != null && !emailId.equals("") && !emailId.equals(Constants.UNDEFINED) && !emailId.equals("-1")/*No email*/){
	    		if(searchTerm.equalsIgnoreCase(emailKeyword)){
	    			result = MessageConstants.getMessage(file, MessageConstants.WRONG_WRONG_EMAIL_KEY1);
	    		}else{				
					int emailInt = Integer.parseInt(emailId);
					QueryDobj queryByEmail = dataAgent.getUniqueQueryByEmailInfo(emailInt, emailKeyword);
					QueryDobj qWithoutKey = dataAgent.getUniqueQueryByEmailInfo(emailInt, "");
					
					if(queryByEmail != null && queryByEmail.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT){
						if(queryByEmail.getId() != msgQuery.getId())//CheckId
							result = MessageConstants.getMessage(file, MessageConstants.WRONG_WRONG_EMAIL_KEY2);
					}else if(qWithoutKey != null && qWithoutKey.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT){
						if(qWithoutKey.getId() != Integer.parseInt(msgId))//CheckId
							result = MessageConstants.getMessage(file, MessageConstants.WRONG_WRONG_EMAIL_KEY3);
					}else if((emailKeyword == null || emailKeyword.equals("") || emailKeyword.equals(Constants.UNDEFINED))){
						List<QueryDobj> emailList = dataAgent.geQueryByEmailId(emailInt);
						if(emailList.size() >= 1){
							if(emailList.size()== 1){
								int firstQId = emailList.get(0).getId();
								if(firstQId != msgQuery.getId())//CheckId
									result = MessageConstants.getMessage(file, MessageConstants.WRONG_WRONG_EMAIL_KEY4);
							}else{
								result = MessageConstants.getMessage(file, MessageConstants.WRONG_WRONG_EMAIL_KEY4);
							}
						}
					}
	    		}		
			}
			
			if(result.equals(MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL))) {
				if((smsKeyword == null || smsKeyword.equals("") || smsKeyword.equals(Constants.UNDEFINED)) && mobileStatus.equals("1")){//dont allow to make a No SMS keyword query If select GSM server 
					result = MessageConstants.getMessage(file, MessageConstants.WRONG_WRONG_SMS_KEY1);
				}else if(mobileStatus != null && !mobileStatus.equals("") && !mobileStatus.equals(Constants.UNDEFINED) && !mobileStatus.equals("-1")/*No SMS Server*/){
					int smsServerId = Integer.parseInt(mobileStatus);
					QueryDobj queryBySmsIdAndkeyword = dataAgent.getQueryBySmsKeyword(smsKeyword, smsServerId);
					QueryDobj queryBySmsIdNoKeyword = dataAgent.getQueryBySmsKeyword("", smsServerId);
					
		    		if(searchTerm.equalsIgnoreCase(smsKeyword)){
		    			result = MessageConstants.getMessage(file, MessageConstants.WRONG_WRONG_SMS_KEY2);
		    		}else{
						if(queryBySmsIdAndkeyword != null && queryBySmsIdAndkeyword.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT){
							if(queryBySmsIdAndkeyword.getId() != msgQuery.getId())//CheckId
								result = MessageConstants.getMessage(file, MessageConstants.WRONG_WRONG_SMS_KEY3);
						}else if(queryBySmsIdNoKeyword != null && queryBySmsIdNoKeyword.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT){
							if(queryBySmsIdNoKeyword.getId() != Integer.parseInt(msgId))//CheckId
								result = MessageConstants.getMessage(file, MessageConstants.WRONG_WRONG_SMS_KEY4);
						}else if((smsKeyword == null || smsKeyword.equals("") || smsKeyword.equals(Constants.UNDEFINED))){
							List<QueryDobj> queriesList = dataAgent.getAllQueriesBySmsServerId(smsServerId);
							if(queriesList.size() >= 1){
								if(queriesList.size()== 1){
									int firstQId = queriesList.get(0).getId();
									if(firstQId != msgQuery.getId())//CheckId
										result = MessageConstants.getMessage(file, MessageConstants.WRONG_WRONG_SMS_KEY5);
								}else{
									result = MessageConstants.getMessage(file, MessageConstants.WRONG_WRONG_SMS_KEY5);
								}
							}
						}
		    		}
				}
	    	}

			if(result.equals(MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL)))	{//Check IM keyword
		    	if(result.equals(MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL)) && imKeyword != null && !imKeyword.equals("") && !imKeyword.equals(Constants.UNDEFINED)
						&& !imKeyword.equals(msgQuery.getImKeyword())){
		    		if(searchTerm.equalsIgnoreCase(imKeyword)){
		    			result = MessageConstants.getMessage(file, MessageConstants.WRONG_WRONG_IM_KEY1);
		    		}else{		    		
			    		QueryDobj uniqueIm = dataAgent.getQueryByIMKeyword(imKeyword);
			    		if(uniqueIm != null && uniqueIm.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT)
			    			result = MessageConstants.getMessage(file, MessageConstants.WRONG_WRONG_IM_KEY2);
		    		}
		    	}
	    	}
	    	
	    	if(result.equals(MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL)))	{//Setting user input value
		    	if(emailKeyword == null || emailKeyword.equals("") || emailKeyword.equals(Constants.UNDEFINED) || emailId.equals("-1"))
		    		msgQuery.setEmailKeyword(null);
		    	else
		    		msgQuery.setEmailKeyword(emailKeyword.toLowerCase());
		    	
		    	if(smsKeyword == null || smsKeyword.equals("") || smsKeyword.equals(Constants.UNDEFINED) || mobileStatus.equals("-1"))
		    		msgQuery.setSmsKeyword(null);
		    	else
		    		msgQuery.setSmsKeyword(smsKeyword.toLowerCase());
		    	
		    	if(imKeyword == null || imKeyword.equals("") || imKeyword.equals(Constants.UNDEFINED) || imStatus.equals("-1"))
		    		msgQuery.setImKeyword(null);
		    	else
		    		msgQuery.setImKeyword(imKeyword.toLowerCase());		    	
		    	
		    	if(emailId != null && !emailId.equals(""))	   	
		    		msgQuery.setEmailAddressId(Integer.parseInt(emailId));
		    	else
		    		msgQuery.setEmailAddressId(-1);
		    		    
		    	if(mobileStatus != null && !mobileStatus.equals(""))
		    		msgQuery.setMobileStatus(Integer.parseInt(mobileStatus));
		    	else
		    		msgQuery.setMobileStatus(-1);
		    	
		    	if(imStatus != null && !imStatus.equals(""))
		    		msgQuery.setImStatus(Integer.parseInt(imStatus));
		    	else {
		    		msgQuery.setImStatus(-1);
		    		msgQuery.setImKeyword(null);
		    	}
		    	
		    	if(imKeyword == null || imKeyword.equals("") || imKeyword.equalsIgnoreCase(Constants.UNDEFINED))
		    		msgQuery.setImStatus(-1);		    	
		    		    	
		    	//Default value for display result is 1
		    	try{
		    		msgQuery.setEmailDisplayResult(Integer.parseInt(emailDisplayResult));
		    	}catch(NumberFormatException e){
		    		msgQuery.setEmailDisplayResult(1);
		    	}
		    	try{
		    		msgQuery.setMobileDisplayResult(Integer.parseInt(mobileDisplayResult));
		    	}catch(NumberFormatException e){
		    		msgQuery.setMobileDisplayResult(1);
		    	}
		    	try{
		    		msgQuery.setImDisplayResult(Integer.parseInt(imDisplayResult));
		    	}catch(NumberFormatException e){
		    		msgQuery.setImDisplayResult(1);
		    	}
		    	
		    	if(responseFormat != null  && !responseFormat.equals(Constants.UNDEFINED) && !responseFormat.equals(""))
		    		msgQuery.setResponse(responseFormat);
		    	else
		    		msgQuery.setResponse("");
		    	
		    	msgQuery.setState(saveStateInt);
				msgQuery.save();
	    	}
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (MdnException e1) {
			e1.printStackTrace();
		} catch (DataSourceException e) {
			e.printStackTrace();
		}
    	
		return new XmlFormatter().saveQueryMsgDetailsXML(file, result, msgQuery);	    
	}	
	
	private Element saveQueryAndMsgInfo(HttpServletRequest request) {
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);

		String saveState = request.getParameter("saveState");
		int saveStateInt = Integer.parseInt(saveState);
		String queryIdStr = request.getParameter("queryId");
		String queryName = request.getParameter("queryName");
		String queryDescription = request.getParameter("queryDescription");
		String strTableID = request.getParameter("tableID");
		String wsOperationId = request.getParameter("wsID");
		
		String queryType = request.getParameter("queryType");
		String sqlText = request.getParameter("sqlText");
		//rows, types, useds, indents, valueOrConditions, numbers, fields, compIDs, comparisons, values,connections, value2s, grouping
		String ids = request.getParameter("ids");
		String rows = request.getParameter("rows");		
		String types = request.getParameter("types");
		String useds = request.getParameter("useds");
		String indents = request.getParameter("indents");
		String valueOrConditions = request.getParameter("valueOrConditions");
		String numbers = request.getParameter("numbers");
		String parents = request.getParameter("parents");
		String fields = request.getParameter("fields");
		String compIDs = request.getParameter("compIDs");
		String comparisons = request.getParameter("comparisons");
		String values = request.getParameter("values");
		String connections = request.getParameter("connections");
		String value2s = request.getParameter("value2s");
		String groupings = request.getParameter("groupings");
		String sorts = request.getParameter("sorts");		
		String groupFieldId = request.getParameter("groupFieldId");
		String userInputSeqs = request.getParameter("userInputSeqs");
		String userInputSeqs2 = request.getParameter("userInputSeqs2");

		//Messaging Info
	    String dbId = request.getParameter("dbId");	    
	    String emailId = request.getParameter("emailId");
	    String emailKeyword = request.getParameter("emailKeyword");
	    String smsKeyword = request.getParameter("smsKeyword");
	    String imKeyword = request.getParameter("imKeyword");
	    String mobileStatus = request.getParameter("mobileStatus");	    
	    String imStatus = request.getParameter("imStatus");	    
	    String emailDisplayResult = request.getParameter("emailDisplayResult");	    
	    String mobileDisplayResult = request.getParameter("mobileDisplayResult");
	    String imDisplayResult = request.getParameter("imDisplayResult");	
		String newResponseFormat = request.getParameter("newResponseFormat");
		String timeout = request.getParameter("timeout");

	    String projectId = request.getParameter("projectId");
	    int intProjectId = 1;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e1) {
			intProjectId = 1;
		}	    
	    
	    String dsIDStr = request.getParameter("dsStatus");
	    int ds = 1;
		try {
			ds = Integer.parseInt(dsIDStr);
		} catch (NumberFormatException e1) {
			ds = 1;
		}	    
		
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		String result = MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL);
		String action = "addQueryAndMsgInfo";

		String resText = "";
		int dbIdInt = -1;
		int queryId = -1;
		QueryDobj query = new QueryDobj();		
		try {
				if (saveStateInt == DataObject.IN_DB){
					if(queryIdStr == null || queryIdStr.equals("") || queryIdStr.equals(Constants.UNDEFINED))
						queryId = Integer.parseInt(queryIdStr);					
					
					query = dataAgent.getQueryByID(queryId);
					action = "editQueryAndMsgInfo";
				}
				query.setDelStatus(Integer.parseInt(Constants.MARKED_AS_NOT_DELETED));
				query.setProjectId(intProjectId);

				if(queryName == null || queryName.equals("") || queryName.equals(Constants.UNDEFINED)) 
		    		result = MessageConstants.getMessage(file, MessageConstants.MISSING_QUERY_NAME);
			    else if(dataAgent.getQueryByName(queryName, intProjectId) != null ) 
			    	result = MessageConstants.getMessage(file, MessageConstants.DUPLICATE_NAME);
			    
		    	if(result.equals(MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL)) && ds == 1){
			    	if(dbId == null || dbId.equals("") || dbId.equals(Constants.UNDEFINED))				
			    		result = MessageConstants.getMessage(file, MessageConstants.MISSING_DB);
				    else if(strTableID == null || strTableID.equals("") || strTableID.equals(Constants.UNDEFINED))		
				    	result = MessageConstants.getMessage(file, MessageConstants.MISSING_VIEW_TBL);
		    	}

		    	if(result.equals(MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL)) && ds == 2){//TODO: check is null?!
			    	if(wsOperationId == null || wsOperationId.equals("") || wsOperationId.equals(Constants.UNDEFINED))				
			    		result = "Missing Select Web Service"; //MessageConstants.getMessage(file, MessageConstants.MISSING_DB);
		    	}

		    	String searchTerm = dataAgent.getGuestMsgInfo("Email").getSearchTerm();
		    	
		    	if(emailId != null && !emailId.equals("") && !emailId.equals(Constants.UNDEFINED) && !emailId.equals("-1")/*No email*/){
					int emailInt = Integer.parseInt(emailId);
					QueryDobj queryByEmail = dataAgent.getUniqueQueryByEmailInfo(emailInt, emailKeyword);
					QueryDobj qWithoutKey = dataAgent.getUniqueQueryByEmailInfo(emailInt, "");
					
		    		if(searchTerm.equalsIgnoreCase(emailKeyword)){
		    			result = MessageConstants.getMessage(file, MessageConstants.WRONG_WRONG_EMAIL_KEY1);
		    		}else{					
						if(queryByEmail != null && queryByEmail.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT){
							result = MessageConstants.getMessage(file, MessageConstants.WRONG_WRONG_EMAIL_KEY2);
						}else if(qWithoutKey != null && queryByEmail.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT){
							result = MessageConstants.getMessage(file, MessageConstants.WRONG_WRONG_EMAIL_KEY3);
						}else if((emailKeyword == null || emailKeyword.equals("") || emailKeyword.equals(Constants.UNDEFINED))){
							List<QueryDobj> emailList = dataAgent.geQueryByEmailId(emailInt);
							if(emailList.size()>0)
								result = MessageConstants.getMessage(file, MessageConstants.WRONG_WRONG_EMAIL_KEY4);
						}
		    		}
				}

				if(result.equals(MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL)))
		    	{
					if((smsKeyword == null || smsKeyword.equals("") || smsKeyword.equals(Constants.UNDEFINED)) && mobileStatus.equals("1")){//If defined GSM server dont allow to make a No SMS keyword query 
						result = MessageConstants.getMessage(file, MessageConstants.WRONG_WRONG_SMS_KEY1);
					} else if(mobileStatus != null && !mobileStatus.equals("") && !mobileStatus.equals(Constants.UNDEFINED) && !mobileStatus.equals("-1")/*No SMS Server*/){
						int smsServerId = Integer.parseInt(mobileStatus);
						QueryDobj queryBySmsIdAndkeyword = dataAgent.getQueryBySmsKeyword(smsKeyword, smsServerId);
						QueryDobj queryBySmsIdNoKeyword = dataAgent.getQueryBySmsKeyword("", smsServerId);
						
			    		if(searchTerm.equalsIgnoreCase(smsKeyword)){
			    			result = MessageConstants.getMessage(file, MessageConstants.WRONG_WRONG_SMS_KEY2);
			    		}else{
							if(queryBySmsIdAndkeyword != null && queryBySmsIdAndkeyword.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT){
								result = MessageConstants.getMessage(file, MessageConstants.WRONG_WRONG_SMS_KEY3);
							}else if(queryBySmsIdNoKeyword != null && queryBySmsIdNoKeyword.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT){
								result = MessageConstants.getMessage(file, MessageConstants.WRONG_WRONG_SMS_KEY4);
							}else if((smsKeyword == null || smsKeyword.equals("") || smsKeyword.equals(Constants.UNDEFINED))){
								List<QueryDobj> queriesList = dataAgent.getAllQueriesBySmsServerId(smsServerId);
								if(queriesList.size()>0)
									result = MessageConstants.getMessage(file, MessageConstants.WRONG_WRONG_SMS_KEY5);
							}
			    		}
					}
		    	}
		    	
				if(result.equals(MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL)))
		    	{
			    	if(result.equals(MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL)) && imKeyword != null && !imKeyword.equals("") && !imKeyword.equals(Constants.UNDEFINED)){
			    		if(searchTerm.equalsIgnoreCase(imKeyword)){
			    			result = MessageConstants.getMessage(file, MessageConstants.WRONG_WRONG_IM_KEY1);
			    		}else{					
				    		QueryDobj uniqueIM = dataAgent.getQueryByIMKeyword(imKeyword);
				    		if(uniqueIM != null && uniqueIM.getDelStatus() == Constants.MARKED_AS_NOT_DELETED_INT)
				    			result = MessageConstants.getMessage(file, MessageConstants.WRONG_WRONG_IM_KEY2);
			    		}
			    	}
		    	}
		    	
		    	if(result.equalsIgnoreCase(MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL))){
		    		
					query.setName(queryName);
		            query.setDescription(queryDescription);
		            query.setDatasourceStatus(ds);
		            
		            if(ds == 1){//If ds is database save database Info
				    	try{
				    		dbIdInt = Integer.parseInt(dbId);
				    	}catch(NumberFormatException e){
				    		String selectedDbName = dbId;
				    		dbIdInt = (dataAgent.getDbConnectionByName(intProjectId, selectedDbName)).getId();
				    	}
				    	
			            int tableID = Integer.parseInt(strTableID);
			            query.setViewOrTableId(tableID);
			            query.setType(queryType);
			            query.setCriteriaString(sqlText);
			            if (sorts != null && !sorts.equalsIgnoreCase(Constants.UNDEFINED)&& !sorts.equals(""))
			            	query.setSortString(sorts);
//			            if (newResponseFormat != null && !newResponseFormat.equalsIgnoreCase(Constants.UNDEFINED)&& !newResponseFormat.equals(""))
//			            	query.setResponse(newResponseFormat);
			            if (groupFieldId != null && !groupFieldId.equalsIgnoreCase(Constants.UNDEFINED) && !groupFieldId.equals("") && !groupFieldId.equalsIgnoreCase("-1")){
			            	int groupFieldIdInt = 0;
							try {
								groupFieldIdInt = Integer.parseInt(groupFieldId);
							} catch (NumberFormatException e) {
								//In this case, group field id is name.
								groupFieldIdInt = getGroupFieldIdByName(query, groupFieldId);
							}
			            	query.setGroupFieldId(groupFieldIdInt);	
			            }
				    	
				    	query.setDatabaseId(dbIdInt);
		            }else{
		            	//TODO: for WEB SERVICE QUERY
		            	query.setWebServiceId(Integer.parseInt(wsOperationId));
		            }
		            
		            if (newResponseFormat != null && !newResponseFormat.equalsIgnoreCase(Constants.UNDEFINED)&& !newResponseFormat.equals(""))
		            	query.setResponse(newResponseFormat);
		            
			    	if(emailId != null && !emailId.equals("")){
			    		query.setEmailAddressId(Integer.parseInt(emailId));
				    	if(emailKeyword != null && !emailKeyword.equals("") && !emailKeyword.equals(Constants.UNDEFINED) && !emailId.equals("-1"))
				    		query.setEmailKeyword(emailKeyword.toLowerCase());    				    				 
			    	} else
			    		query.setEmailAddressId(-1);
			    		    
			    	if(mobileStatus != null && !mobileStatus.equals("")){
			    		query.setMobileStatus(Integer.parseInt(mobileStatus));
				    	if(smsKeyword != null && !smsKeyword.equals("") && !smsKeyword.equals(Constants.UNDEFINED) && !mobileStatus.equals("-1"))
				    		query.setSmsKeyword(smsKeyword.toLowerCase());    				    				 			    		
			    	}else
			    		query.setMobileStatus(-1);
			    	
			    	
			    	if(imKeyword == null || imKeyword.equals("") || imKeyword.equals(Constants.UNDEFINED) || imStatus.equals("-1"))
			    		query.setImKeyword(null);
			    	else
			    		query.setImKeyword(imKeyword.toLowerCase());		    				    	
			    	
			    	if(imStatus != null && !imStatus.equals(""))
			    		query.setImStatus(Integer.parseInt(imStatus));
			    	else{
			    		query.setImStatus(-1);
			    		query.setImKeyword(null);
			    	}
			    	
			    	if(imKeyword == null || imKeyword.equals("") || imKeyword.equalsIgnoreCase(Constants.UNDEFINED))
			    		query.setImStatus(-1);		    				    	
			    		    	
			    	//Default value for display result is 1
			    	try{
			    		query.setEmailDisplayResult(Integer.parseInt(emailDisplayResult));
			    	}catch(NumberFormatException e){
			    		query.setEmailDisplayResult(1);
			    	}
			    	try{
			    		query.setMobileDisplayResult(Integer.parseInt(mobileDisplayResult));
			    	}catch(NumberFormatException e){
			    		query.setMobileDisplayResult(1);
			    	}
			    	try{
			    		query.setImDisplayResult(Integer.parseInt(imDisplayResult));
			    	}catch(NumberFormatException e){
			    		query.setImDisplayResult(1);
			    	}
			    	
					try{
			    		int timeoutint = Integer.parseInt(timeout);
			    		if(timeoutint <= 0)
			    			query.setTimeout("5");
			    		else
			    			query.setTimeout(timeout);
			    	}catch(NumberFormatException e){
			    		query.setTimeout("5");//Default is 5 Seconds
			    	}
			    	
	                query.setState(saveStateInt);
	                query.setDelStatus(Constants.MARKED_AS_NOT_DELETED_INT);	                
	                query.save();
	                
		            int newQueryID = query.getId();
		            
		            
		            //if(ds == 1){//If ds is databese save the criteria
		                String[] idsList = ids.split(",");
		                String[] rowsList = rows.split(",");
		                String[] typesList = types.split(",");
		                String[] usedsList = useds.split(",");
		                String[] indentsList = indents.split(",");
		                String[] valueOrConditionsList = valueOrConditions.split(",");        
		                
		                String[] numbersList = null;
		                if (numbers != null && !numbers.equals(""))
		                	numbersList = numbers.split(",");
		 
		                String[] parentsList = null;
		                if (parents != null && !parents.equals(""))
		                	parentsList = parents.split(",");                
		                
		                String[] fieldsList = fields.split(",");
		                String[] compIDsList = compIDs.split(",");
		                String[] comparisonsList = comparisons.split(",");
		                String[] valuesList = values.split(",");
		                String[] connectionsList = connections.split(",");
		                String[] value2sList = value2s.split(",");
		                String[] groupingsList = groupings.split(",");
		                String[] userInputSeqsList = userInputSeqs.split(",");
		                String[] userInputSeqs2List = userInputSeqs2.split(",");

		        		//delete all of the old criteria data
						if (saveStateInt == DataObject.IN_DB){
							//need to delete all the old query criteria
							List<QueryCriteriaDobj> queryCriteria = dataAgent.getQueryCriteriaByQueryID(queryId, "");
							for (QueryCriteriaDobj queryCriteriaDobj : queryCriteria){
								queryCriteriaDobj.delete();
							}
						}		                
		                
		        		boolean empty = false;
		        		if (rowsList.length == 1){
		        			String rowList =rowsList[0]; 
		        			if (rowList.equals("")){
		        				//There is no query criteria need to save
		        				empty = true;
		        				//result = "OK";
		        			}
		        		}
		        		
		        		if (!empty){
			                int index = 1;
			                for (int i = 0; i< rowsList.length; i++){
			                	String id = idsList[i];
			                	String row = rowsList[i];
			                	String type = typesList[i];
			                	String used = usedsList[i];
			                	String indent = indentsList[i];
			                	String valueOrCondition = valueOrConditionsList[i];
			                	
			                	String parent = null;
			                	if (parentsList != null && parentsList.length == rowsList.length)
			                		parent = parentsList[i];
			                	
			                	String number = null;
			                	if (numbersList != null && numbersList.length == rowsList.length)
			                		number = numbersList[i];                	
			                	
			                	String field = fieldsList[i];
			                	String compID = compIDsList[i];
			                	String comparison = comparisonsList[i];
			                	String value = valuesList[i];               	
			                	String connection = connectionsList[i];
			                	String value2 = value2sList[i];               	
			                	String grouping = groupingsList[i];
			                	
		                    	String userInputSeq = null;
		                    	if (userInputSeqsList != null && userInputSeqsList.length > i)
		                    		userInputSeq = userInputSeqsList[i];
		                    	
		                    	String userInputSeq2 = null;
		                    	if (userInputSeqs2List != null && userInputSeqs2List.length > i)
		                    		userInputSeq2 = userInputSeqs2List[i];
			                	
			                	// create a Query Criteria object
			                	QueryCriteriaDobj queryCriteriaObj = new QueryCriteriaDobj();
			                	
			                	int queryCriteriaSaveState = DataObject.NEW;
			                	
			                	queryCriteriaObj.setQueryId(newQueryID);
			                	queryCriteriaObj.setValueOrCondition(valueOrCondition);
			                	int rowInt;
								try {
									rowInt = Integer.parseInt(row);
								} catch (NumberFormatException e) {
									rowInt = index;
								}
			                    queryCriteriaObj.setRowNo(rowInt);
			                    queryCriteriaObj.setType(type);
			                    queryCriteriaObj.setUsed(Boolean.valueOf(used).booleanValue() ? 1 : 0);
			                    queryCriteriaObj.setIndent(Integer.parseInt(indent));
			                    queryCriteriaObj.setParent(parent == null ? "" : parent);
			                    queryCriteriaObj.setNumber(number == null ? 0 : Integer.parseInt(number));
			                    System.out.println("Saveeeeeeeeeeee WS : " + field + " :" + value);
			                    queryCriteriaObj.setName(field);
			                    queryCriteriaObj.setCompId(Integer.parseInt(compID));
			                    queryCriteriaObj.setComparison(comparison);
			                    queryCriteriaObj.setValue(value);
			                    queryCriteriaObj.setConnection(connection);
			                    queryCriteriaObj.setValue2(value2);
			                    queryCriteriaObj.setGrouping(grouping);
			                    
		                    	queryCriteriaObj.setValueUserInputSeq(userInputSeq);
		                    
		                    	queryCriteriaObj.setValue2UserInputSeq(userInputSeq2);
			                    
			                    queryCriteriaObj.setState(queryCriteriaSaveState);
			                    queryCriteriaObj.save();
			                    index++;
			                }
		        		}
		            //}
	        		resText = result;
		    	}
		} catch (MdnException e1) {
			e1.printStackTrace();
			result = "Database error";
		} catch (DataSourceException e) {
			e.printStackTrace();
			result = "Database error";
		}			
		return new XmlFormatter().addMessagingInfoXML(action, file, result, query, resText, projectId);	    
	}
	/**
	 * would be call only for queries which are in Database mode
	 * @param request
	 * @return
	 */
	private Element getDefaultTextMsgForResponse(HttpServletRequest request)
	{
	    String dsIDStr = request.getParameter("dsStatus");
	    int ds = 1;
		try {
			ds = Integer.parseInt(dsIDStr);
		} catch (NumberFormatException e1) {
			ds = 1;
		}	    
		String queryType = request.getParameter("queryType"); 
		String sql = request.getParameter("sql"); 
		String dbId = request.getParameter("dbId");
		String viewID = request.getParameter("viewID");
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		DataView view = null;
		RecordSet rs = null;
		String replyingMsg = "";
/*		if(sql == null || sql.equals("") || sql.equals(Constants.UNDEFINED) || 
				dbId == null || dbId.equals("") || dbId.equals("undefined") || 
				viewID == null || viewID.equals("") || viewID.equals("undefined")){
			replyingMsg = "There are no results for this query.";
		}else{
			try {
				view = dataAgent.getMetaViewByID(Integer.parseInt(viewID), true);
		    	if(Integer.parseInt(queryType)== Constants.SELECT_QUERY_TYPE_NUM){//Select
		        	Vector titles = new Vector();
	        		titles = view.getFields();
	        		for(int col = 0; col < titles.size(); col++)
	        		{
	        			String filedName = titles.get(col).toString();
	        			//replyingMsg += "Title : %" + filedName + "% \n";
	        			DataViewField fieldObj = (DataViewField)titles.get(col);
	        			replyingMsg += fieldObj.getDisplayName() +" : %" + filedName + "% \n";
	        		}
		    	}
			}catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (MdnException e) {
				e.printStackTrace();
			}
		} */
		//replyingMsg = gerDefaultResponse(queryType, sql, dbId, viewID);
		replyingMsg = MessagingUtils.getDefaultResponse(queryType, sql, dbId, viewID, request, ds, null);
		
		return new XmlFormatter().getDefaultTextMsgForResponseXML(replyingMsg);
	}
	
	/*private Element getQuery(HttpServletRequest request){
	    Element root = null;
	    String queryID = request.getParameter("queryID");
	    String objType = request.getParameter("objType");	    
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		QueryDobj query = null;
		try {
			if(objType == null || objType.equals("") || objType.equalsIgnoreCase("undefined"))
				query = dataAgent.getQueryByID(Integer.parseInt(queryID));
			else{
				UserReply ur = dataAgent.getUserReplyById(Integer.parseInt(queryID));
				query = convertQueryObjToUserReply(ur);
				
			}
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (MdnException e1) {
			e1.printStackTrace();
		}
	    return new XmlFormatter().getQuery(query);
	}*/
	
	private QueryDobj convertQueryObjToUserReply(UserReply userReply){
		QueryDobj query = new QueryDobj();
		String msgTxt = userReply.getMsgText();
		if(msgTxt != null && !msgTxt.equals(""))
			query.setName(userReply.getMsgText());
		else
			query.setName("-");
		query.setCriteriaString(userReply.getCriteriaString());
		query.setDatabaseId(userReply.getDatabaseId());
		query.setDataView(userReply.getDataView(false));
		query.setGroupFieldId(userReply.getGroupFieldId());
		query.setResponse(userReply.getResponse());
		query.setSortString(userReply.getSortString());
		query.setType(userReply.getType());
		query.setViewOrTableId(userReply.getViewOrTableId());
		//query.setDescription(userReply.getDescription());
		
		return query;
	}
	/*
	private Element getNewQuery(HttpServletRequest request){
	    //Element root = null;
	    String viewOrTableID = request.getParameter("ID");
	    String queryType = request.getParameter("queryType");
	    //IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		QueryDobj query = new QueryDobj();
		query.setName("New Query");
		int id;
		try {
			id = Integer.parseInt(viewOrTableID);
		} catch (NumberFormatException e) {
			id = -1;
		}
		query.setViewOrTableId(id);
		query.setType(queryType);
	    return new XmlFormatter().getQuery(query);
	}*/

	private Element recycleDataView(HttpServletRequest request){
	    Element root = null;
	    String viewID = request.getParameter("viewID");
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		DataView view = null;
		try {
			view = dataAgent.getMetaViewByID(Integer.parseInt(viewID), true);
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (MdnException e1) {
			e1.printStackTrace();
		}
		try {
			view.setDelStatus(Constants.MARKED_AS_RECYCLED_INT);
			view.save();
		} catch (DataSourceException e) {
			e.printStackTrace();
		}
	    return new XmlFormatter().getView(view);
	}
	
	private Element clearDataView(HttpServletRequest request){
	    String viewID = request.getParameter("viewID");
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		DataView view = null;
		try {
			view = dataAgent.getMetaViewByID(Integer.parseInt(viewID), true);
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (MdnException e1) {
			e1.printStackTrace();
		}
		try {
			view.setDelStatus(Constants.MARKED_AS_NOT_DELETED_INT);
			view.save();
		} catch (DataSourceException e) {
			e.printStackTrace();
		}
	    return new XmlFormatter().getView(view);
	}
	private Element deleteDataView(HttpServletRequest request){
	    Element root = null;
	    String viewID = request.getParameter("viewID");
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		DataView view = null;
		try {
			view = dataAgent.getMetaViewByID(Integer.parseInt(viewID), true);
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (MdnException e1) {
			e1.printStackTrace();
		}
		try {
			view.delete();
		} catch (DataSourceException e) {
			e.printStackTrace();
		}
	    return new XmlFormatter().getView(view);
	}	

	/**
	 * Becaue in query builder, the view XML is specified defined and is different from the view normal format
	 * Create specified XML format just for query builder
	 * @param request - view ID
	 * @return
	 */	
	private Element getTableForQueryCriteriaByTableID(HttpServletRequest request){
	    String tableID = request.getParameter("tableID");
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		EntityDobj table = null;
		try {
			table = dataAgent.getMetaTableByID(Integer.parseInt(tableID), true);
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (MdnException e1) {
			e1.printStackTrace();
		}
		//DataView view = query.getDataView(true);
		
	    return new XmlFormatter().getTableForQueryCriteria(table);
	}		
	
	private Element getEmptySelectQueryResult(HttpServletRequest request){
		String action = request.getParameter("action");
		return new XmlFormatter().getEmptyQueryRecordSet(action);
	}
	
	private Element getSelectQueryResult(HttpServletRequest request){
	    //String queryID = request.getParameter("queryID");
		String sql = request.getParameter("sql");
		String dataSourceId = request.getParameter("connID");
		String viewID = request.getParameter("viewID");
		String sorts = request.getParameter("sorts");
		String userInputTestValues = request.getParameter("userInputTestValues");
	    String projectId = request.getParameter("projectId");
	    int intProjectId = 1;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e1) {
			intProjectId = 1;
		}		
		
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		
		//dataAgent.getSelectQueryResultWithUserInput(msgInfo, null, userInputsList, null, -1);
		
		//QueryDobj query = null;
		DataView view = null;
		RecordSet rs = null;
		boolean success = false;
		String errorMsg = "";
		if(sql != null && !sql.equals("") && !sql.equals("undefined")){
			try {
				//query = dataAgent.getQueryByID(Integer.parseInt(queryID));
				view = dataAgent.getMetaViewByID(Integer.parseInt(viewID), true);
				
		        // get the view ds
		        DataViewDataSource dvds = MdnDataManager.getDataViewDS();
		        
	    		int selectedDbId;
		    	try{
		    		selectedDbId = Integer.parseInt(dataSourceId);
		    	}catch(NumberFormatException e){
		    		String selectedDbName = dataSourceId;
		    		selectedDbId = (dataAgent.getDbConnectionByName(intProjectId, selectedDbName)).getId();
		    	}
		        
		    	if (userInputTestValues == null || userInputTestValues.isEmpty() || userInputTestValues.equalsIgnoreCase("undefined")){
		    		rs = dvds.execDirectSQL(selectedDbId, sql, view);
		    	}else{
					String separator = " ";
					MdnMessageSeparator mdnMsgSep = dataAgent.getMessageSeparator();
					if(mdnMsgSep != null){
						separator = mdnMsgSep.getConditionSeperator();
					}		    		
		    		
		    		ArrayList<String> lstUserInputs = MessagingUtils.getUserInputForNoKeywordQuery(separator, userInputTestValues);
					rs = dataAgent.getQueryResultWithUserInput(view, sql, lstUserInputs, 0);
					success = true;
		    	}
		        //rs = dvds.execDirectSQL(Integer.parseInt(dataSourceId), sql, view);
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
				success = false;
				errorMsg = e1.toString();
			} catch (MdnException e1) {
				e1.printStackTrace();
				success = false;
				errorMsg = e1.toString();
			} catch (DataSourceException e) {
				e.printStackTrace();
				success = false;
				errorMsg = e.toString();				
			}
		}
		String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
		String action = request.getParameter("action");
		Element element = null;
		if (rs != null && rs.getRows().size() > 0){			
			element = new XmlFormatter().getQueryRecordSet(view, rs, sorts);
		}else{
			if (success){
				String result = "NoData";
				element = new XmlFormatter().simpleResultWithErrorMsg(file, action, result, errorMsg);
			}else{
				String result = "Failed";
				element = new XmlFormatter().simpleResultWithErrorMsg(file, action, result, errorMsg);				
			}
		}
	    return element;
	}	
	
	private Element getUpdateQueryResult(HttpServletRequest request){
	    // Get the current language file.
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    String action = request.getParameter("action");
		String sql = request.getParameter("sql");
		String dataSourceId = request.getParameter("connID");
		String userInputTestValues = request.getParameter("userInputTestValues");
	    
	    String projectId = request.getParameter("projectId");
	    int intProjectId = 1;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e1) {
			intProjectId = 1;
		}			
		
		//IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		//QueryDobj query = null;
		boolean success = false;
		String errorMsg = "";
		try {
			//query = dataAgent.getQueryByID(Integer.parseInt(queryID));
			IDataAgent dataAgent = DataAgentFactory.getDataInterface();
    		
	        // get the view ds
	        DataViewDataSource dvds = MdnDataManager.getDataViewDS();
	        
    		int selectedDbId = 0;
	    	try{
	    		selectedDbId = Integer.parseInt(dataSourceId);
	    	}catch(NumberFormatException e){
	    		String selectedDbName = dataSourceId;
	    		try {
					selectedDbId = (dataAgent.getDbConnectionByName(intProjectId, selectedDbName)).getId();
				} catch (MdnException e1) {
					e1.printStackTrace();
				}
	    	}

	    	if (userInputTestValues == null || userInputTestValues.isEmpty() || userInputTestValues.equalsIgnoreCase("undefined")){
	    		//rs = dvds.execDirectSQL(selectedDbId, sql, view);
	    	}else{
				String separator = " ";
				MdnMessageSeparator mdnMsgSep = dataAgent.getMessageSeparator();
				if(mdnMsgSep != null){
					separator = mdnMsgSep.getConditionSeperator();
				}		    		

	    		ArrayList<String> lstUserInputs = MessagingUtils.getUserInputForNoKeywordQuery(separator, userInputTestValues);
	    		sql = dataAgent.replaceSqlWithUserInput(sql, lstUserInputs, 0);
	    	}	    	
	    	
	        //if (query != null && query.isComplete(null))
            int row = dvds.execInsertOrUpdate(selectedDbId, sql);
            System.out.println("How many rows are updated: "  + row);
            if (row > 0){
            	success = true;
            	if (row > 1)
            		errorMsg = row + " rows are updated.";
            	else 
            		errorMsg = row + " row is updated.";
            }else if (row == 0){
            	success = false;
            	errorMsg = row + " row is updated.";
            }
	        
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
			success = false;
			errorMsg = e1.toString();
		} catch (DataSourceException e) {
			e.printStackTrace();
			success = false;
			errorMsg = e.toString();
		} catch (MdnException e) {
			e.printStackTrace();
			success = false;
			errorMsg = e.toString();
		}
		String result = null;
		if (success){
			result = "OK";
		}
		else{
			result = "Failed";
		}
		
	    return new XmlFormatter().simpleResultWithErrorMsg(file, action, result, errorMsg);
	}
	
	private Element getInsertQueryResult(HttpServletRequest request){
	    // Get the current language file.
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    String action = request.getParameter("action");
		String sql = request.getParameter("sql");
		String userInputTestValues = request.getParameter("userInputTestValues");
	    
		String dataSourceId = request.getParameter("connID");
	    String projectId = request.getParameter("projectId");
	    int intProjectId = 1;
		try {
			intProjectId = Integer.parseInt(projectId);
		} catch (NumberFormatException e1) {
			intProjectId = 1;
		}	
		
		boolean success = false;
		String errorMsg = "";
		try {
			// get the view ds
	        DataViewDataSource dvds = MdnDataManager.getDataViewDS();
	        
	        IDataAgent dataAgent = DataAgentFactory.getDataInterface();
    		
    		int selectedDbId = 0;
	    	try{
	    		selectedDbId = Integer.parseInt(dataSourceId);
	    	}catch(NumberFormatException e){
	    		String selectedDbName = dataSourceId;
	    		try {
					selectedDbId = (dataAgent.getDbConnectionByName(intProjectId, selectedDbName)).getId();
				} catch (MdnException e1) {
					e1.printStackTrace();
				}
	    	}
	    	if (userInputTestValues == null || userInputTestValues.isEmpty() || userInputTestValues.equalsIgnoreCase("undefined")){
	    		//rs = dvds.execDirectSQL(selectedDbId, sql, view);
	    	}else{
				String separator = " ";
				MdnMessageSeparator mdnMsgSep = dataAgent.getMessageSeparator();
				if(mdnMsgSep != null){
					separator = mdnMsgSep.getConditionSeperator();
				}		    		
	    		
	    		/*String[] userInputs = userInputTestValues.split(",");
	    		ArrayList<String> lstUserInputs = new ArrayList<String>();
				for (String userInput : userInputs){
	    			lstUserInputs.add(userInput);
	    		}*/
	    		ArrayList<String> lstUserInputs = MessagingUtils.getUserInputForNoKeywordQuery(separator, userInputTestValues);
	    		sql = dataAgent.replaceSqlWithUserInput(sql, lstUserInputs, 0);
	    	}
	    	
	        //if (query != null && query.isComplete(null))
            int row = dvds.execInsertOrUpdate(selectedDbId, sql);
            System.out.println("How many rows are inserted: "  + row);
            if (row > 0){
            	success = true;
            	if (row > 1)
            		errorMsg = row + " rows are inserted.";
            	else 
            		errorMsg = row + " row is inserted.";
            }else if (row == 0){
            	success = false;
            	errorMsg = row + " row is inserted.";
            }
	        
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
			success = false;
			errorMsg = e1.toString();
		} catch (DataSourceException e) {
			e.printStackTrace();
			success = false;
			errorMsg = e.toString();
		} catch (MdnException e) {
			e.printStackTrace();
			success = false;
			errorMsg = e.toString();
		}
		String result = null;
		if (success){
			result = "OK";
		}
		else{
			result = "Failed";
		}
	    return new XmlFormatter().simpleResultWithErrorMsg(file, action, result, errorMsg);
	}
	
	private Element getTable(HttpServletRequest request){
	    String tableID = request.getParameter("tableID");
	    String includeFields = request.getParameter("includeFields");
	    boolean incl = includeFields != null ? Boolean.getBoolean(includeFields) : false;
	    DbDataHandler handler = new DbDataHandler();
	    EntityDobj table = null;
	    if(tableID == null || tableID.equals("") || tableID.equalsIgnoreCase("null") || tableID.equalsIgnoreCase("undefined"))
	    {
		    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
		    XmlFormatter xmlFormatter = new XmlFormatter();
	    	return xmlFormatter.simpleResult(file, "noAction", "OK");
	    }
	    
    	try {
    		table = handler.getTable(Integer.parseInt(tableID), incl);
    	} catch (NumberFormatException e) {
    		e.printStackTrace();
    	} catch (MdnException e) {
    		e.printStackTrace();
    	}
    	return new XmlFormatter().getTable(table);
	    
	}

	/**
     * Create a single mirror table
     * @param ds the DataSource to mirror
     * @param ed the EntityDobj to (re)create in the mirror
     */
    private void createMirrorTable(DataSourceDobj ds, EntityDobj ed) throws DataSourceException
    {
        // validate
        Util.argCheckNull(ds);
        assert ds.isMirrored();

        // get the mirror ds
        int mirrorId = ds.getMirrorId();
        DataSource mirrorDs = MdnDataCache.getCache().getDataSource(mirrorId);

        // create the table in the mirror
        mirrorDs.createEntityTable(ed.createImpl(), true);
    }
    
	private Element editTable(HttpServletRequest request){
	    String connIDStr = request.getParameter("connID");
	    int connID = Integer.parseInt(connIDStr);
	    String tableIDStr = request.getParameter("tableID");
	    int tableID = Integer.parseInt(tableIDStr);
	    String name = request.getParameter("name");
	    String description = request.getParameter("description");

	    DbDataHandler handler = new DbDataHandler();
	    //String result = handler.editTable(table);
	    
		String result = null;
		// Check for duplicate table name.
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		EntityDobj check = null;
		try {
			check = dataAgent.getMetaTableByName(name, 
					connID, false);
		} catch (MdnException e) {
			e.printStackTrace();
		}
		if (check != null){
			if (check.getName().equalsIgnoreCase(name) && 
					check.getId() != tableID){
				result = "Duplicate table name";
			} 
		} 
		else{
			EntityDobj table = null;
			try {
				//Get table from database
				table = handler.getTable(tableID, true);
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
				result = "Database error - table "+ tableID +" does not exist.";
			} catch (MdnException e1) {
				e1.printStackTrace();
				result = "Database error - table "+ tableID +" does not exist.";
			}
			if (table != null)
			{
				//Update table name/description now
				table.setName(name);
				table.setDescription(description);
				table.setState (DataObject.IN_DB);
				try {
					table.save();
					result = "OK";
				} catch (DataSourceException e) {
					e.printStackTrace();
					result = "Database error";
				}				
			}
		}	    
	    
	    // Get the current language file.
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    String action = request.getParameter("action");
	    return new XmlFormatter().editResult(file, action, result);
	}
	
	private Element getField(HttpServletRequest request){
	    String fieldID = request.getParameter("fieldID");
	    String includeTable = request.getParameter("includeTable");
	    boolean incl = includeTable != null ? Boolean.getBoolean(includeTable) : false;
	    DbDataHandler handler = new DbDataHandler();
	    if(fieldID == null || fieldID.equals("") || fieldID.equalsIgnoreCase("null") || fieldID.equalsIgnoreCase("undefined"))
		{
		    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
		    XmlFormatter xmlFormatter = new XmlFormatter();
	    	return xmlFormatter.simpleResult(file, "noAction", "OK");	    	
		}
	    
    	Element field = null;
	    try {
	    	field = handler.getField(Integer.parseInt(fieldID), incl);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (MdnException e) {
			e.printStackTrace();
		}
		return field;

	  }
	
	private Element editField(HttpServletRequest request){
	    String fieldID = request.getParameter("fieldID");
	    String tableID = request.getParameter("tableID");
	    String name = request.getParameter("name");
	    String description = request.getParameter("description");
	    MetaField field = new MetaField();
	    DbDataHandler handler = new DbDataHandler();
	    //String result = handler.editField(field);
	    // Get the current language file.
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    String action = request.getParameter("action");
	    //return new XmlFormatter().editFieldResult(file, action, result);
	    return null;
	}
	
	private Element recycleConnection(HttpServletRequest request){
	    Element root = null;
	    String projectID = request.getParameter("projectID");
	    String dbConnID = request.getParameter("connID");
	    DbDataHandler handler = new DbDataHandler();
	    String result = handler.handleRecycleConnection(Integer.parseInt(projectID), Integer.parseInt(dbConnID)); 
	    // Get the current language file.
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    String action = request.getParameter("action");
	    root = new XmlFormatter().recycleConnectionResult(file, action, result);
	    return root;
	}
	private Element clearConnection(HttpServletRequest request){
	    Element root = null;
	    String projectID = request.getParameter("projectID");
	    String dbConnID = request.getParameter("connID");
	    DbDataHandler handler = new DbDataHandler();
	    String result = handler.handleClearConnection(Integer.parseInt(projectID), Integer.parseInt(dbConnID)); 
	    // Get the current language file.
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    String action = request.getParameter("action");
	    root = new XmlFormatter().simpleResult(file, action, result);
	    return root;
	}
	private Element deleteConnection(HttpServletRequest request){
	    Element root = null;
	    String projectID = request.getParameter("projectID");
	    String dbConnID = request.getParameter("connID");
	    DbDataHandler handler = new DbDataHandler();
	    String result = handler.handleDeleteConnection(Integer.parseInt(projectID), Integer.parseInt(dbConnID)); 
	    // Get the current language file.
	    String file = (String)request.getSession().getAttribute(Constants.SESSION_LANGUAGE_FILE);
	    String action = request.getParameter("action");
	    root = new XmlFormatter().simpleResult(file, action, result);
	    return root;
	}	
}
