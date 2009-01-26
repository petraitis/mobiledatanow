package com.framedobjects.dashwell.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import wsl.fw.exception.MdnException;

import com.framedobjects.dashwell.utils.webservice.IParamView;
import com.framedobjects.dashwell.utils.webservice.ParamHelper;
import com.framedobjects.dashwell.utils.webservice.ParamListItem;
import com.framedobjects.dashwell.utils.webservice.ParamView;

//import com.esri.arcweb.v2.AuthenticationLocator;
//import com.esri.arcweb.v2.IAuthentication;

public class MessagingWebserviceUtils {
/*	static String wsdlUrl = "http://www.blackberry.com/webservices/ContractorAxis/Contractor.wsdl";
	static String selectedService = "ContractorIFService";
	static String selectedPort = "Contractor";
	static String selectedOperation = "register";*/
	//static String selectedOperation = "getMyCompanies";
	//static String selectedOperation = "getContractors";
	//static String selectedOperation = "getAvailableTimeslots";
	//static String selectedOperation = "scheduleVisit";//This is void operation
	
/*	static String wsdlUrl = "http://www.ejse.com/WeatherService/Service.asmx?WSDL";
	static String selectedService = "Service";
	static String selectedPort = "ServiceSoap";
	static String selectedOperation = "getDayForecastInfo";
	//static String selectedOperation = "GetWeatherInfo";
*/	
	
/*	static String wsdlUrl = "http://ws.strikeiron.com/HouseofDev/CurrencyRates160?WSDL";
	static String selectedService = "CurrencyRates";
	static String selectedPort = "CurrencyRatesSoap";
	static String selectedOperation = "getRate";	*/
	
	static String wsdlUrl = "http://arcweb.esri.com/services/v2/Authentication.wsdl";
	static String selectedService = "Authentication";
	static String selectedPort = "IAuthentication";
	static String selectedOperation = "getToken";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ParamHelper paramHelper = initWebServiceDetails();
		executeOperation(paramHelper);

	}
	private static ParamHelper initWebServiceDetails() {
		List<IParamView> parameterViews = null;
		ParamHelper paramHelper = null;
		try {
			paramHelper = new ParamHelper(wsdlUrl);
			paramHelper.setCurrentService(selectedService);
			paramHelper.setCurrentPort(selectedPort);
			paramHelper.setCurrentOperation(selectedOperation);
			
			paramHelper.createParamList();
			parameterViews = paramHelper.getParamViews();
	
			for (IParamView parameter : parameterViews){
		  		System.out.println("Param Lable = " + parameter.getLabel());
		  		
		  		if(((ParamView)parameter).getParent() != null ){
		  			System.out.println("Param PARENT Lable = " + ((ParamView)parameter).getParent().getLabel());
		  		}
			}
			
//			try {
				int expiration = 60; // in seconds
//			    AuthenticationLocator locator = new AuthenticationLocator();
//			    IAuthentication authentication	= locator.getIAuthentication();
				// --------------Call to Authentication Web Services and get the token---------
//				String token = authentication.getToken("mdnun","firetrust",expiration);/*mdnun*/
				
				//System.out.println(token);
//			} catch (ServiceException e1) {
//				e1.printStackTrace();
//			} catch (RemoteException e) {
//				e.printStackTrace();
//			}			
				
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return paramHelper;
	}
	
	private static void executeOperation(ParamHelper paramHelper){
		try {
			/*
			 * operation: scheduleVisit
			 * Input params: VisitRequest(String contractName, String timeslot)
			 * Output params: void = null
			 */
			//Object[] myvalues = {"CARL HAWKINS", "Afternoon of Mon, Dec 8"};
			/*
			 * operation: getAvailableTimeslots
			 * Input params: String(contractor name)
			 * Output params: String[]{...,timeslot,..}
			 */			
			//Object[] myvalues = {"CARL HAWKINS"};
			/*
			 * operation: getContractors
			 * Input params: String(company name)
			 * Output params: Contractor[]{Contractor(String department,String name,String phone,double rate),...}
			 */
			//Object[] myvalues = {"XYZ Contractors"};//company name
			/*
			 * operation: getMyCompanies
			 * Input params: String(UserId)
			 * Output params: Company[]{Company1<String address, String description, String name, String phone>,...}
			 */			
			//Object[] myvalues = {"mmyLa6"};//arrgs: userId<int>
			/*
			 * operation: register
			 * Input params: User(String firestName, String lastName, String password)
			 * Output params: String userId
			 */						
			Object[] myvalues = {"myFnnn", "myLaaaa", "myPasss"};
			
			/*
			 * operation: getDayForecastInfo
			 * Input params: ForecastDays(String value) [zipcode sample:02852]
			 * Output params: DayForecastInfo(String day, String date, int iconIndex, String forecast, String high,String low, String precipChance), int zipcode)
			 */									
			//Object[] myvalues = {"NZD"};//554

            /* AMAZON Search >> Object[] myvalues = {"1keyword",  "2page", "3mode", "4tag", "5type", "6devtag", "7sort", "8local", "9price"}*/
            //Object[] myvalues = {"Finding Nemo", "1",    "books", "tag",  "lite",   "",       "",      "",        ""};
			
			List<ParamListItem> reqParamList = paramHelper.getParamList();
			//========================= get and save request Param ===========================
			System.out.println("__________________________________Input param & Invokation __________________________________");
			Map saveUserInputParamMap = new HashMap<String, Object>();//Key=fieldName , Value=UserInputValue 
			List<IParamView> reqParamView = paramHelper.getParamViews();
			//for(IParamView inParam: reqParamView){
			for(int i = 0; i < reqParamView.size(); i++){
				IParamView inParam = reqParamView.get(i);	
				System.out.println("input param lable: " + inParam.getLabel());
				System.out.println("input param simple lable: " + inParam.getSimpleLabel());
				Object uiObjectValue = myvalues[i];
				saveUserInputParamMap.put(inParam.getSimpleLabel(), uiObjectValue);
			}
			System.out.println("__________________________________________________________________");
			//================================================================================
			
			List<ParamListItem> resultParam =  paramHelper.invoke(reqParamList, saveUserInputParamMap);
			System.out.println(">>>>>>>>>>>>>>>>>>RESULLT>>>>>>>>>>>>>>>>>>>>>>");
			for(ParamListItem resultItem : resultParam )
			{
				Object resultItemValue = resultItem.getValue();
				if(resultItemValue.getClass().isArray()){
					Object type = resultItemValue.getClass().getComponentType();
					Object[] resultItemArray = (Object[])resultItemValue;
					int len = resultItemArray.length;
					for(int i = 0; i< resultItemArray.length; i++){
						Object obj = resultItemArray[i];
						System.out.println("result>>>" + resultItem.getLabel() + " :" + obj.toString());
					}
					
				}else
					System.out.println(resultItem.getLabel() + " :" + resultItem.getValue());
				//System.out.println("Result Type : " + resultItem.getDatatype());
			}
			System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		} catch (MdnException e) {
			e.printStackTrace();
		}
	}
	
/*	  public Object[] constructArguments(Map<ParamListItem> list) throws MdnException{
		    Object[] args = new Object[list.size()];
		    
		    for (int i = 0; i < args.length; ++i){
		    	ParamListItem paramListItem = list.get(i);
		    	System.out.println("list.get(i).getItemtype() == >> " + list.get(i).getItemtype());
		      switch(paramListItem.getItemtype()){
		        case ParamListItem.ITEMTYPE_FIELD:
		        	System.out.println("++++ For call field method to set value paramListItem Label = " + paramListItem.getLabel());
		    		String dataType = paramListItem.getDatatype();
					//Field f = ParamListItem.class.getField(paramListItem.getLabel());
					System.out.println("Data type == " + dataType);
					Object argValue = setArgValue(dataType, "02852");
					args[i] = argValue;
		            //args[i] = "name"+i;
		            break;        	
		        //args[i] = list.get(i).getValue()[0];
		        //break;
		        case ParamListItem.ITEMTYPE_ARRAY:
		          args[i] = paramListItem.getValue();
		          System.out.println("arg value = " + args[i]);
		        break;
		        case ParamListItem.ITEMTYPE_CONTAINER:
		          Class cl = paramListItem.getContainerComponentType();
		          Object[] containerArgs = constructArguments(paramListItem.getList());
		          // create a new instance of the "container" class with the
		          // arguments that we get from the paramListItem
		          args[i] = getNewInstance(cl, containerArgs);
		        break;
		      }
		    }
		    
		    return args;
		  }  */
}

/*
			// --------------Locate Authentication Web services -----------
			int expiration = 60; // in seconds
            AuthenticationLocator locator = new AuthenticationLocator();
            IAuthentication authentication = locator.getIAuthentication() ;
           
            // --------------Call to Authentication Web Services and get the token---------
            String token = authentication.getToken(username,password,expiration);
 
 */
