package com.framedobjects.dashwell.utils.webservice;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.axis.client.Stub;

import org.apache.axis.message.SOAPHeaderElement;

import wsl.fw.exception.MdnException;

/**
 * This Class provides the methods for the controller to
 * interact with the model.
 * It is responsible for the 
 * interaction for the GUI mode.
 * 
 */
public class ParamHelper extends Wsdl2Java{
  
  private List<ParamListItem> m_paramList;
  private int rowsSize = 0;
  
  /**
   * Constructs a new GuiLogic instance for the web service described
   * by the specified WSDL URL. The created instance does not support ssl.
   * 
   * @param wsdlUrl The URL of the WSDL document.
   * @throws MdnException
   */
  public ParamHelper(String wsdlUrl) throws MdnException{
    super(wsdlUrl);
    /* generate the request param list */
    //createParamList();    
  }
  
  
  public void setCurrentOperation(String operation) throws MdnException{
    super.setCurrentOperation(operation);
    
    /* generate the request param list */
    //createParamList();
  }
  
  public void setCurrentPort(String port) throws MdnException{
    super.setCurrentPort(port);
    
    /* generate the request param list */
    //createParamList();
  }
  
  public void setCurrentService(String service) throws MdnException{
    super.setCurrentService(service);
    
    /* generate the request param list */
    //createParamList();
  }
  
  public int getRowSize(){
	  return rowsSize;
  }
  
  public List<WebServiceResultRow> getParamListItemsAsRow(){
	  return m_classHelper.getParamListItemsAsRow();
  }
  

  public List<ParamListItem> invoke(List<ParamListItem> paramList, String[] attachments) throws MdnException{
	    //TODO: Object[] args = m_classHelper.constructArguments(paramList, null);
	    Object[] args = null;
	    Method method = null;
	    Object result = null;
	    try {
	      method = m_currPort.portClass.getMethod(m_currOperation.name, m_currOperation.arguments);
	      result = method.invoke(m_currPort.portInstance, args);
	    }
	    catch(NoSuchMethodException e){
	      throw new MdnException("error.NoSuchMethodException", e);
	    }
	    catch(IllegalAccessException e){
	      throw new MdnException("error.IllegalAccessException", e);
	    }
	    catch(InvocationTargetException e){
	      throw new MdnException("error.InvocationTargetException", e);
	    }
	    catch(IllegalArgumentException e){
	      throw new MdnException("error.IllegalArgumentException", e);
	    }
	    if(result==null)
	      return new ArrayList<ParamListItem>();
	    Class param = result.getClass();    
	    String paramType = param.isArray()? 
	          param.getComponentType().getSimpleName() + "[]": param.getSimpleName();
	    
	    /* get the paramList */
	    List<ParamListItem> list = m_classHelper.getParamListFromFields(
	        new Class[]{param}, new String[]{paramType}, new Object[]{result});
	    System.out.println("result list" + list);
	    return list;
	  }
  
  public List<ParamListItem> invoke(List<ParamListItem> paramList, Object[] values) throws MdnException{
	  
	  m_classHelper.setInputArgValueIndex(0);
	  List inputValueList = Arrays.asList(values);
	  m_classHelper.setInputValueList(inputValueList);
	  Object[] args = m_classHelper.constructArguments(paramList, inputValueList);
	    
	  Method method = null;
	  Object result = null;
	  try {
		Stub stub = (Stub)m_currPort.portInstance;

		
//		 Set the SOAP headers
//	    ((Stub) service).setHeader(apiNS, "developer_email",developerEmail);
//	    ((Stub) service).setHeader(apiNS, "developer_password",developerPassword);
//	    ((Stub) service).setHeader(apiNS, "client_id", clientId);
//		stub._setProperty(Stub.USERNAME_PROPERTY, "nick.bolton@mobiledatanow.com");
//		stub._setProperty(Stub.PASSWORD_PROPERTY, "Xsw98p");

/*
 		JAXBContext jaxbContext = JAXBContext.newInstance("com.strikeiron.ws");
		Marshaller marshaller = jaxbContext.createMarshaller();		
		StringWriter outStream = new StringWriter();

 		LicenseInfo licenseInfo = new LicenseInfo();
		RegisteredUser registeredUser = licenseInfo.getRegisteredUser();
		registeredUser.setUserID("nick.bolton@mobiledatanow.com");
		registeredUser.setPassword("Xsw98p");
		marshaller.marshal(licenseInfo, outStream);
		stub.setOutboundHeaders(Headers.create(new QName("LicenseInfo"), outStream.toString());		
*/		
		
		SOAPHeaderElement[] headers = stub.getHeaders();
		for (int i = 0; i < headers.length; i++) {
			SOAPHeaderElement header = headers[i];
			System.out.println(header.namespaces);
			System.out.println(header.getName());
			System.out.println(header.getValue());
		}
//		stub.getHeader("http://ws.strikeiron.com", "EmailAddress");
//		stub.setHeader("http://ws.strikeiron.com", "EmailAddress", "nick.bolton@mobiledatanow.com");
//		stub.setHeader("http://ws.strikeiron.com", "Xsw98p", "password");
		//stub.setHeader("http://my.name.space/headers", "mysecurityheader", "Value");

	    method = m_currPort.portClass.getMethod(m_currOperation.name, m_currOperation.arguments);
	    //result = method.invoke(m_currPort.portInstance, args);
	    result = method.invoke(stub, args);
	  }
	  catch(NoSuchMethodException e){
	    throw new MdnException("error.NoSuchMethodException", e);
	  }
	  catch(IllegalAccessException e){
	    throw new MdnException("error.IllegalAccessException", e);
	  }
	  catch(InvocationTargetException e){
	  	e.printStackTrace();
	    throw new MdnException("error.InvocationTargetException", e);
	  }
	  catch(IllegalArgumentException e){
	    throw new MdnException("error.IllegalArgumentException", e);
	  }
	  if(result==null)
	    return new ArrayList<ParamListItem>();
	  
	  Class param = result.getClass();    
	  String paramType = param.isArray()? 
	        param.getComponentType().getSimpleName() + "[]": param.getSimpleName();
	        
	  if(result.getClass().isArray()){
		  rowsSize = ((Object[])result).length;
	  }else
		  rowsSize = 1;
	  //rowsSize = (new Object[]{result}).length;
	  /* get the paramList */
	  //Regisresult      
	  m_classHelper.restartParamListItems();//Restart param list
	  m_classHelper.restartParamListItemsAsRow();//Restart result Map
	  List<ParamListItem> list = m_classHelper.getParamListFromFields( new Class[]{param}, new String[]{paramType}, new Object[]{result});
	  return list;
	}  

  
  public List<ParamListItem> invoke(List<ParamListItem> paramList, Map valuesMap) throws MdnException{
	  Object[] inputArgs = m_classHelper.constructArguments(paramList, valuesMap);
	    
	  Method method = null;
	  Object result = null;
	  try {
		Stub stub = (Stub)m_currPort.portInstance;
	    method = m_currPort.portClass.getMethod(m_currOperation.name, m_currOperation.arguments);
	    result = method.invoke(stub, inputArgs);
	    
	  }
	  catch(NoSuchMethodException e){
	    throw new MdnException("error.NoSuchMethodException", e);
	  }
	  catch(IllegalAccessException e){
	    throw new MdnException("error.IllegalAccessException", e);
	  }
	  catch(InvocationTargetException e){
	  	e.printStackTrace();
	    throw new MdnException("error.InvocationTargetException", e);
	  }
	  catch(IllegalArgumentException e){
	    throw new MdnException("error.IllegalArgumentException", e);
	  }
	  if(result==null)
	    return new ArrayList<ParamListItem>();
	  
	  Class param = result.getClass();    
	  String paramType = param.isArray()? 
	        param.getComponentType().getSimpleName() + "[]": param.getSimpleName();
	        
	  if(result.getClass().isArray()){
		  rowsSize = ((Object[])result).length;
	  }else
		  rowsSize = 1;
	  //Regisresult      
	  m_classHelper.restartParamListItems();//Restart param list
	  m_classHelper.restartParamListItemsAsRow();//Restart result Map
	  List<ParamListItem> list = m_classHelper.getParamListFromFields( new Class[]{param}, new String[]{paramType}, new Object[]{result});
	  return list;
	}  
  
  public List<ParamListItem> getParamList(){
    return m_paramList;
  }

  /**
   * Creates the parameter list needed for invoking 
   * the current web service operation. The parameter list is
   * stored in the current instance.
   * 
   * @throws MdnException
   */
  public void createParamList() throws MdnException{
    /* get the method object for the current ws operation */
    Method method = null;
    try {
      method = m_currPort.portClass.getMethod(m_currOperation.name, m_currOperation.arguments);
    }
    catch(NoSuchMethodException e){
      throw new MdnException("error.NoSuchMethodException", e);
    }
    // get the parameter type names
    Class[] params = method.getParameterTypes();
    String[] paramTypes = new String[params.length];
    for (int i = 0; i < params.length; ++i)
      paramTypes[i] = (params[i].isArray())? params[i].getComponentType().getName() + "[]": params[i].getName();
    
    String[] labels = m_classHelper.getParamNames( m_currPort.portClass, method.getName(), paramTypes);
    
    /* get the paramList */
    m_paramList = m_classHelper.getParamListFromConstructor(m_currOperation.arguments,labels);
  }
  
  /**
   * Creates the parameter list needed for invoking 
   * the current web service operation. The parameter list is
   * stored in the current instance.
   * 
   * @throws MdnException
   */
  public void setParamList() throws MdnException{
    /* get the method object for the current ws operation */
    Method method = null;
    try {
      method = m_currPort.portClass.getMethod(m_currOperation.name, m_currOperation.arguments);
    }
    catch(NoSuchMethodException e){
      throw new MdnException("error.NoSuchMethodException", e);
    }
    // get the parameter type names
    Class[] params = method.getParameterTypes();
    String[] paramTypes = new String[params.length];
    for (int i = 0; i < params.length; ++i)
      paramTypes[i] = (params[i].isArray())? 
          params[i].getComponentType().getName() + "[]": params[i].getName();
    
    String[] labels = m_classHelper.getParamNames(
        m_currPort.portClass, method.getName(), paramTypes);
    
    /* get the paramList */
    m_paramList = m_classHelper.getParamListFromConstructor(m_currOperation.arguments,labels);
  }
  
  public List<IParamView> getParamViews() throws MdnException{
	  return this.m_classHelper.getTypeMapper().mapVCRequest(m_paramList, null);	  
  }
}
