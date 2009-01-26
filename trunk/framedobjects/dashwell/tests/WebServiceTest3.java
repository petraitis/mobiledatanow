package com.framedobjects.dashwell.tests;

//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.rmi.Remote;
//import java.rmi.RemoteException;
//import java.util.Collection;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.Vector;
//import java.util.Map.Entry;
//
//import javax.xml.*;
//import javax.xml.namespace.QName;
////import javax.xml.rpc.Service;
//import javax.xml.rpc.Call;
//import javax.xml.rpc.ParameterMode;
//import javax.xml.rpc.ServiceException;
//import javax.xml.rpc.ServiceFactory;
//
//import javax.xml.rpc.encoding.XMLType;
//import javax.wsdl.*;
//import javax.wsdl.extensions.ExtensibilityElement;
//import javax.wsdl.extensions.UnknownExtensibilityElement;
//import javax.wsdl.factory.WSDLFactory;
//import javax.wsdl.xml.WSDLReader;
//
//import org.apache.axis.encoding.DefaultTypeMappingImpl;
//import org.apache.axis.encoding.TypeMapping;
//
//
////import org.apache.axis.transport.http.HTTPConstants;
////
////import org.apache.commons.httpclient.HttpConnection;
////import org.iso_relax.dispatcher.ElementDecl;
////import org.jdom.input.DOMBuilder;
////import org.openlaszlo.connection.HTTPConnection;
////import org.openlaszlo.remote.soap.ComplexType;
////import org.w3c.dom.Element;
//
//import com.ibm.wsdl.ImportImpl;
//import com.ibm.wsdl.PortImpl;
//import com.ibm.wsdl.PortTypeImpl;
//
//
//import wsl.fw.resource.ResId;
//import ymsg.network.HTTPConnectionHandler;



import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import javax.management.ReflectionException;
import javax.xml.namespace.QName;

import org.apache.axis.encoding.DefaultTypeMappingImpl;
import org.apache.axis.encoding.TypeMapping;
import org.apache.soap.Constants;
import org.apache.soap.Fault;
import org.apache.soap.SOAPException;
import org.apache.soap.encoding.SOAPMappingRegistry;
import org.apache.soap.encoding.soapenc.BeanSerializer;
import org.apache.soap.rpc.Call;
import org.apache.soap.rpc.Parameter;
import org.apache.soap.rpc.Response;
import org.jdom.Namespace;
import org.springframework.util.ReflectionUtils;

public class WebServiceTest3 {

    /** XML Schema Namespace URI */
    private static final String SCHEMA_NAMESPACE_URI ="http://www.w3.org/1999/XMLschema";

    private static Namespace schemaNamespace;
	/**
	 * @param args
	 */
	public static void main(String[] args) {		
		//String wsdlURI = "http://webservices.amazon.com/AWSECommerceService/AWSECommerceService.wsdl"; // example url
		/*String wsdlURI ="http://api.google.com/GoogleSearch.wsdl";//"http://soap.amazon.com/schemas2/AmazonWebServices.wsdl";////"http://www.blackberry.com/webservices/ContractorAxis/Contractor.wsdl";////////
		//String wsdlURL = "http://localhost:6080/HelloWebService/services/Hello?wsdl";
		

		Call call=new Call();
		SOAPMappingRegistry smr = call.getSOAPMappingRegistry();
		call.setSOAPMappingRegistry(smr);
		call.setMethodName("doGoogleSearch");
		call.setEncodingStyleURI(Constants.NS_URI_SOAP_ENC);
		call.setTargetObjectURI(target where the soap is implemented);
		Vector params = new Vector();
		params.addElement(new Parameter("parameter to be passed", type of parameter,null);

		call.setParams(params);
		Response resp = call.invoke(url to be invoked, target object uri);

		*/
		/*
		URL url = new  
	       URL("http://georgetown:8080/soap/servlet/rpcrouter");
	    Call call = new Call(  );
	    SOAPMappingRegistry smr = new SOAPMappingRegistry(  );
	    call.setSOAPMappingRegistry(smr);
	    call.setEncodingStyleURI(Constants.NS_URI_SOAP_ENC);
	    call.setTargetObjectURI("urn:BasicTradingService");
	    call.setMethodName("executeStockTrade");
	    BeanSerializer beanSer = new BeanSerializer(  );
	    
	    TypeMapping tm = DefaultTypeMappingImpl.getSingleton();
	    Class cl;
		try {
			cl = TypeMapping.getClassForQName(wsdlQName);
			System.out.println("**************Class: " + cl);
		} catch (ReflectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    // Map the Stock Trade type
	    smr.mapTypes(Constants.NS_URI_SOAP_ENC,
	       new QName("urn:BasicTradingService", "StockTrade"),
	       StockTrade_ClientSide.class, beanSer, beanSer);
	    // create an instance of the stock trade
	    StockTrade_ClientSide trade = 
	           new StockTrade_ClientSide("XYZ", false, 350);
	    Vector params = new Vector(  );
	    params.addElement(new Parameter("trade", 
	                           StockTrade_ClientSide.class, trade, null));
	    call.setParams(params);
	    Response resp;
	    try {
	      resp = call.invoke(url, "");
	      Parameter ret = resp.getReturnValue(  );
	      Object desc = ret.getValue(  );
	      System.out.println("Trade Description: " + desc);
	    }
	    catch (SOAPException e) {
	      System.err.println("Caught SOAPException (" +
	                         e.getFaultCode(  ) + "): " +
	                         e.getMessage(  ));
	    }
	    */
		/*SOAPMappingRegistry soapmappingregistry = new SOAPMappingRegistry();
		BeanSerializer beanserializer = new BeanSerializer();
		soapmappingregistry.mapTypes("http://schemas.xmlsoap.org/soap/encoding/", new QName("urn:GoogleSearch", "GoogleSearchResult"), GoogleSearchResult.class, beanserializer, beanserializer);
		soapmappingregistry.mapTypes("http://schemas.xmlsoap.org/soap/encoding/", new QName("urn:GoogleSearch", "DirectoryCategory"), com.google.soap.search.GoogleSearchDirectoryCategory.class, beanserializer, beanserializer);
		soapmappingregistry.mapTypes("http://schemas.xmlsoap.org/soap/encoding/", new QName("urn:GoogleSearch", "ResultElement"), com.google.soap.search.GoogleSearchResultElement.class, beanserializer, beanserializer);
		*/

/*		
		
		//		 Step 1: identify the service 
		try {
			URL url = new URL("http://www.ibm.com/namespace/wsif/samples/stockquote"); 
			Call call = new Call(); 
			 call.setTargetObjectURI("urn:xmltoday-delayed-quotes"); 
			 // Step 2: identify the operation 
			 call.setMethodName("getQuote"); 
			 call.setEncodingStyleURI(Constants.NS_URI_SOAP_ENC);//encodingStyleURI); 
			 // Step 3: identify the parameters 
			 Vector params = new Vector(); 
			 String symbol = "Wilde, Oscar";
			 params.addElement(new Parameter("symbol", 
			                          String.class, symbol, null));
			 call.setParams(params); 
			 // Step 4: execute the operation 
			 Response resp = call.invoke(url, 
			                   "http://example.com/GetTradePrice");
			 // Step 5: extract the result or fault 
			 if(resp.generatedFault()) 
			 { 
			   Fault fault = resp.getFault(); 
			   System.out.println("Ouch, the call failed: "); 
			   System.out.println("  Fault Code   = " +  
			                      fault.getFaultCode()); 
			   System.out.println("  Fault String = " +  
			                      fault.getFaultString()); 
			   //throw new SOAPException("Execution failed " + fault); 
			   System.out.println("Execution failed " + fault);
			 } 
			 else 
			 { 
			   Parameter result = resp.getReturnValue(); 
			   //return ((Float) result.getValue()).floatValue(); 
			   System.out.println("Return value: [" +((Float) result.getValue()).floatValue() +"]");
			 }
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SOAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	*/
/*TODO: commentout		Uri uri = new Uri("http://api.google.com/GoogleSearch.wsdl");

		WebRequest webRequest = WebRequest.Create(uri);

		System.IO.Stream requestStream = webRequest.GetResponse().GetResponseStream();

//		 Get a WSDL file describing a service

		ServiceDescription sd = ServiceDescription.Read(requestStream);

		string sdName = sd.Services[0].Name;

//		 Initialize a service description servImport

		ServiceDescriptionImporter servImport = new ServiceDescriptionImporter();

		servImport.AddServiceDescription(sd, String.Empty, String.Empty);

		servImport.ProtocolName = "Soap";

		servImport.CodeGenerationOptions = CodeGenerationOptions.GenerateProperties;

		CodeNamespace nameSpace = new CodeNamespace();

		CodeCompileUnit codeCompileUnit = new CodeCompileUnit();

		codeCompileUnit.Namespaces.Add(nameSpace);

//		 Set Warnings

		ServiceDescriptionImportWarnings warnings = servImport.Import(nameSpace, codeCompileUnit);

		if (warnings == 0)

		{

		StringWriter stringWriter = new StringWriter(System.Globalization.CultureInfo.CurrentCulture);

		Microsoft.CSharp.CSharpCodeProvider prov = new Microsoft.CSharp.CSharpCodeProvider();

		prov.GenerateCodeFromNamespace(nameSpace, stringWriter, new CodeGeneratorOptions());

//		 Compile the assembly with the appropriate references

		string[] assemblyReferences = new string[2] { "System.Web.Services.dll", "System.Xml.dll" };

		CompilerParameters param = new CompilerParameters(assemblyReferences);

		param.GenerateExecutable = false;

		param.GenerateInMemory = true;

		param.TreatWarningsAsErrors = false;

		param.WarningLevel = 4;

		CompilerResults results = new CompilerResults(new TempFileCollection());

		results = prov.CompileAssemblyFromDom(param, codeCompileUnit);

		Assembly assembly = results.CompiledAssembly;

		service = assembly.GetType(sdName);

		methodInfo = service.GetMethods();

		foreach (MethodInfo t in methodInfo)

		{

		if (t.Name == "Discover")

		break;

		 

		treeWsdl.Nodes[0].Nodes.Add(t.Name);

		}

		treeWsdl.Nodes[0].Expand();

		}		*/
	}


}
