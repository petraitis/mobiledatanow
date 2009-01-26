package com.framedobjects.dashwell.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import javax.xml.*;
import javax.xml.namespace.QName;
//import javax.xml.rpc.Service;
import javax.xml.rpc.Call;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.ServiceFactory;

import javax.xml.rpc.encoding.XMLType;
import javax.wsdl.*;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;

import org.apache.axis.encoding.DefaultTypeMappingImpl;
import org.apache.axis.encoding.TypeMapping;


//import org.apache.axis.transport.http.HTTPConstants;
//
import org.apache.commons.httpclient.HttpConnection;
//import org.iso_relax.dispatcher.ElementDecl;
//import org.jdom.input.DOMBuilder;
//import org.openlaszlo.connection.HTTPConnection;
//import org.openlaszlo.remote.soap.ComplexType;
//import org.w3c.dom.Element;

import com.ibm.wsdl.ImportImpl;
import com.ibm.wsdl.PortImpl;
import com.ibm.wsdl.PortTypeImpl;


import wsl.fw.resource.ResId;
import ymsg.network.HTTPConnectionHandler;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.ServiceFactory;

//import org.apache.axis.client.Call;
import org.apache.axis.encoding.DefaultTypeMappingImpl;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.encoding.TypeMappingRegistryImpl;
import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.commons.httpclient.HttpConnection;
import org.exolab.castor.xml.schema.AttributeDecl;
import org.exolab.castor.xml.schema.BlockList;
import org.exolab.castor.xml.schema.ComplexType;
import org.exolab.castor.xml.schema.ContentModelGroup;
import org.exolab.castor.xml.schema.ElementDecl;
import org.exolab.castor.xml.schema.Group;
import org.exolab.castor.xml.schema.Particle;
import org.exolab.castor.xml.schema.Schema;
import org.exolab.castor.xml.schema.Structure;
import org.exolab.castor.xml.schema.reader.SchemaReader;
import org.jdom.Namespace;
import org.jdom.input.DOMBuilder;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import wsl.fw.resource.ResId;

import com.ibm.wsdl.ImportImpl;
import com.ibm.wsdl.factory.WSDLFactoryImpl;

public class WebServiceTest {
	public static final ResId
	ERR_LICENSE1	= new ResId ("MdnAdminApp.err.license1"),
	ERR_LICENSE2	= new ResId ("MdnAdminApp.err.license2"),
	TEXT_STARTING	= new ResId ("MdnAdminApp.text.starting"),
	TEXT_VERSION	= new ResId ("mdn.versionText"),
	ERR_VERSION1	= new ResId ("MdnAdminApp.err.version1"),
	ERR_VERSION2	= new ResId ("MdnAdminApp.err.version2"),
	ERR_WILL_EXIT	= new ResId ("MdnAdminApp.err.willExit"),
	ERR_NO_SERVER	= new ResId ("MdnAdminApp.err.noServer");	
    /** XML Schema Namespace URI */
    private static final String SCHEMA_NAMESPACE_URI =
        "http://www.w3.org/1999/XMLschema";
	//@WebServiceRef(wsdlLocation=
    //"http://webservices.amazon.com/AWSECommerceService/AWSECommerceService.wsdl")
//	public static void main(String[] args) {
//		String implURI = "http://api.google.com/GoogleSearch.wsdl";//"http://home.xtra.co.nz/hosts/nickbolton/sf.wsdl";//"http://webservices.amazon.com/AWSECommerceService/AWSECommerceService.wsdl";
//		try {
//			parseWSDL(implURI);
//		} catch (WSDLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
    private static Namespace schemaNamespace;
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		        
//		try {
//			ServiceFactory serviceFactory = ServiceFactory.newInstance();
//			/* The "new URL(wsdlURL)" parameter is optional */
//			Service service = serviceFactory.createService(new URL(wsdlURL), serviceQN);
			
			//String wsdlURI = "http://webservices.amazon.com/AWSECommerceService/AWSECommerceService.wsdl"; // example url
			String wsdlURI ="http://soap.amazon.com/schemas2/AmazonWebServices.wsdl";//"http://api.google.com/GoogleSearch.wsdl";////"http://www.blackberry.com/webservices/ContractorAxis/Contractor.wsdl";//
			//String wsdlURL = "http://localhost:6080/HelloWebService/services/Hello?wsdl";

			
	
			//String wsdlURI = endpoint + "?WSDL";
			StringBuffer b = new StringBuffer();
			HttpConnection c = null;
			InputStream is = null;
			try {
				long len = 0 ;
				int ch = 0;
				char tmp = ' ';
				
				HTTPConnectionHandler hConnectionHandler = new HTTPConnectionHandler();
				
//				c = (HttpConnection) Connector.open(wsdlURI);
				
//				is = c.openInputStream();
//				len = c.getLength() ;
				if ( len != -1) {
//				 Read exactly Content-Length bytes
				for (int i =0 ; i < len ; i++ ) {
				if ((ch = is.read()) != -1) {
				b.append((char) ch);
//				System.out.print((char) ch);
				}
				}
				}
				else {
//				 Read until the connection is closed
				while ((ch = is.read()) != -1) {
				len = is.available() ;
				b.append((char) ch);
				}
				}
//				xmlDocument = b.toString();
				String temp = "";

//				midlet.message = xmlDocument.substring(0,100) + "\nResponse Size: " + b.length() + " bytes\n";
//				System.out.println(&quot;Result: &quot; + temp);
				}
				catch(Exception e) {
//				 error
				}
				finally {
//				is.close();
				c.close();
				}
			try {

                StringBuffer resultStringBuffer = new StringBuffer();

                URL server = new URL(wsdlURI);

                HttpURLConnection connection = (HttpURLConnection) server.openConnection();



                connection.setDoOutput(true);

                connection.setDoInput(true);



                connection.connect();



                PrintWriter pw = new PrintWriter(connection.getOutputStream());

                

                String requestXMLString = "POST " + server.getPath() + " HTTP/1.0\r\n" + "Content-Type: application/x-www-form-urlencoded\r\n" + "\r\n" ;

                pw.write(requestXMLString);

                pw.flush();

                pw.close();



                BufferedReader response = new BufferedReader(new InputStreamReader(connection.getInputStream()));



                String inputLine = ""; 

                while ((inputLine = response.readLine()) != null) {

                         resultStringBuffer.append(inputLine);

                }



                response.close();

                connection.disconnect();



                String resultString = resultStringBuffer.toString();

                System.out.println("result:" + resultString);

		      }
		
		      catch(java.net.SocketTimeoutException ste){
		
		                ste.printStackTrace();
		
		      }
		
		      catch(IOException ioe){
		
		                ioe.printStackTrace();
		
		      }

							
//			HttpConnection httpConnection = new 
			
			try {
				WSDLFactory wsdlFactory = WSDLFactory.newInstance();
				WSDLReader wsdlReader = wsdlFactory.newWSDLReader();

				wsdlReader.setFeature("javax.wsdl.verbose",false);
				wsdlReader.setFeature("javax.wsdl.importDocuments",true);

				Definition definition = wsdlReader.readWSDL(wsdlURI);
//				System.out.println("definition : " + definition);
				System.out.println("==============================================================");
				if (definition == null)
				{
					System.err.println("definition element is null");
					System.exit(1);
				}

			  	QName qName = definition.getQName();
			  	System.out.println("definition QName name: " + qName);
			  	System.out.println("QName getLocalPart: " + qName.getLocalPart());
			  	System.out.println("QName getNamespaceURI: " + qName.getNamespaceURI());
			  	//Service service = definition.getService(qName);
			  	//System.out.println("Service name: " + service);
			  	
			  	Definition interfaceDef = null;
				// now get the Definition object for the interface WSDL
				Map imports = definition.getImports();
				Set importSet = imports.keySet();
				Iterator setIter = importSet.iterator();
				while(setIter.hasNext()) {
			        Object o = setIter.next();
				    Vector intDoc = (Vector)imports.get(o);
				    // we want to get the ImportImpl object if it exists
				    for(int i=0; i<intDoc.size(); i++) {
				    	Object obj = intDoc.elementAt(i);	
			    		if(obj instanceof ImportImpl) {
			    		    interfaceDef = ((ImportImpl)obj).getDefinition();
			    		    System.out.println("88888888888888888interfaceDef: " + interfaceDef);
			    		}
				    }
				}
				String targetNamespace = definition.getTargetNamespace();
				System.out.println("definition.getTargetNamespace(): " + targetNamespace);
				System.out.println("==============interfaceDef: " + interfaceDef);
			  	
			  	Map s = definition.getServices();
			  	
			  		  	
			  	System.out.println("How many Services: " + s.size());
			  	Set serviceSet = s.entrySet();
			  	System.out.println("How many Services entrySet: " + serviceSet.size());
			  	
//			  	System.out.println("==============================================================");
//			  	System.out.println("Service name: " + s);
//			  	System.out.println("==============================================================");

				//Get the the java.wsdl.Types from the java.wsdl.Definition object 
				Types types = definition.getTypes();
				System.out.println("################types: " + types);
//				System.out.println("types.getNativeAttributeNames()" + ":" + types.getNativeAttributeNames());	
//				System.out.println("==============================================================");
//				System.out.println("Port Types: " + definition.getPortTypes());
//				System.out.println("==============================================================");
//				System.out.println("Bindings: " + definition.getBindings());
//				System.out.println("==============================================================");
//				System.out.println("ExtensibilityElements: " + definition.getExtensibilityElements());
//				System.out.println("==============================================================");
//				System.out.println("ExtensionRegistry: " + definition.getExtensionRegistry());
//				System.out.println("==============================================================");
//				System.out.println("Imports: " + definition.getImports());
//				System.out.println("==============================================================");
//				System.out.println("Messages: " + definition.getMessages());
//				System.out.println("==============================================================");
//				System.out.println("getServices: " + definition.getServices());

				
				System.out.println("################types.getExtensibilityElements(): " + types.getExtensibilityElements());
				
				//get a schema element (org.w3c.dom.Element)
				ExtensibilityElement schemaExtElem = findExtensibilityElement(types.getExtensibilityElements(), "schema");
				System.out.println("################  schemaExtElem: " + schemaExtElem);
				Element schemaElement = null;
				if ((schemaExtElem != null) && (schemaExtElem instanceof UnknownExtensibilityElement)) {
					schemaElement = ((UnknownExtensibilityElement) schemaExtElem).getElement();
				}
				
				System.out.println("################  schemaElement: " + schemaElement);
				System.out.println("################  schemaElement.getChildNodes(): " + schemaElement.getChildNodes());
				System.out.println("################  schemaElement.getBaseURI(): " + schemaElement.getBaseURI());
				System.out.println("################  schemaElement.getNamespaceURI(): " + schemaElement.getNamespaceURI());
				System.out.println("################  schemaElement.getChildNodes(): " + schemaElement.getChildNodes());
				
				
				//transformed my schemaElement into a JDOM element (org.jdom.Element) using org.jdom.input.DOMBuilder:
				DOMBuilder domBuilder = new DOMBuilder();
				org.jdom.Element jdomSchemaElement = domBuilder.build(schemaElement);
				System.out.println("################  jdomSchemaElement: " + jdomSchemaElement);
				System.out.println("################  jdomSchemaElement Content: " + jdomSchemaElement.getContent());
				System.out.println("################  jdomSchemaElement Children: " + jdomSchemaElement.getChildren());
				System.out.println("################  jdomSchemaElement Descendants: " + jdomSchemaElement.getDescendants());
				System.out.println("################  jdomSchemaElement Text: " + jdomSchemaElement.getTextNormalize());
				System.out.println("################  jdomSchemaElement Document: " + jdomSchemaElement.getDocument());
				System.out.println("################  jdomSchemaElement.getNamespace().getURI(): " + jdomSchemaElement.getNamespace().getURI());
//				jdomSchemaElement.
				//Once you have the JDOM element, it's easy to transform it into a Castor Schema (org.exolab.castor.xml.schema.Schema) 
				//using the SchemaReader (org.exolab.castor.xml.schema.reader.SchemaReader).
				//With Castor, its relativly easy to get what you need:
				

//				SchemaUnmarshaller schemaHandler = new SchemaUnmarshaller();

//				Parser parser = ParserFactory.makeParser(); 
//				parser.setDocumentHandler(schemaHandler); 
//				parser.parse("file:"+inputFile); 
//				Schema schema = schemaHandler.getSchema(); 
				
				jdomSchemaElement.addNamespaceDeclaration(Namespace.getNamespace("soapenc","http://schemas.xmlsoap.org/soap/encoding/"));
				jdomSchemaElement.addNamespaceDeclaration(Namespace.getNamespace("wsdl","http://schemas.xmlsoap.org/wsdl/"));
				jdomSchemaElement.addNamespaceDeclaration(Namespace.getNamespace("SOAP-ENC","http://schemas.xmlsoap.org/soap/encoding/"));
				jdomSchemaElement.addNamespaceDeclaration(Namespace.getNamespace("soap","http://schemas.xmlsoap.org/wsdl/soap/"));
				//jdomSchemaElement.addNamespaceDeclaration(Namespace.getNamespace("soap","http://schemas.xmlsoap.org/soap/encoding/"));
				
				
				jdomSchemaElement.addNamespaceDeclaration(Namespace.getNamespace("typens",targetNamespace));
				
				jdomSchemaElement.addNamespaceDeclaration(Namespace.getNamespace("xsd","http://www.w3.org/2001/XMLSchema"));
				
				org.jdom.Element importSchema = new org.jdom.Element("import");
				importSchema.setAttribute("namespace", "http://schemas.xmlsoap.org/soap/encoding/");
				importSchema.setAttribute("schemaLocation", "http://schemas.xmlsoap.org/soap/encoding/");
				importSchema.setNamespace(Namespace.getNamespace("xsd","http://www.w3.org/2001/XMLSchema"));
				
				jdomSchemaElement.addContent(1, importSchema);
				
				//jdomSchemaElement.setNamespace(Namespace.getNamespace("xsd","http://www.w3.org/2001/XMLSchema"));
				

				/*
				 * > <schema targetNamespace="http://soapinterop.org/xsd"
> xmlns="http://www.w3.org/2001/XMLSchema"
> xmlns:SOAP-ENC="http://schemas.xmlsoap.org/soap/encoding/"
> xmlns:http="http://schemas.xmlsoap.org/wsdl/http/"
> xmlns:mime="http://schemas.xmlsoap.org/wsdl/mime/"
> xmlns:s0="http://soapinterop.org/xsd"
> xmlns:s1="http://soapinterop.org/"
> xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"
> xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/"
> xmlns:tns="http://tempuri.org"
> xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
> xmlns:xsd="http://www.w3.org/2001/XMLSchema"> 
				 */
				XMLOutputter xmlOutputter = new XMLOutputter();
				String string = xmlOutputter.outputString(jdomSchemaElement.getDocument());
				System.out.println("==============  string: " + string);
				
				StringReader in2 = new StringReader(string);
				InputSource schemaSource = new InputSource(in2);
				
				Namespace schemaNamespace = Namespace.getNamespace(SCHEMA_NAMESPACE_URI);
				
//				SAXBuilder saxBuilder = new SAXBuilder();
//				try {
//					Document schemaDoc = saxBuilder.build(schemaSource);
//					System.out.println("################  Document: " + schemaDoc);
//					System.out.println("################  Document: " + schemaDoc.getBaseURI());
//
//					//schemaDoc.getRootElement().getAttributes();
//		            // Handle attributes
//					//System.out.println("666666666666666666  schemaDoc.getRootElement().getChildren(): " + schemaDoc.getRootElement().getChildren());
////		            List attributes = schemaDoc.getRootElement().getChildren();
//////		                                         .getChildren("attribute", 
//////		                                                      schemaNamespace);
////		            for (Iterator i = attributes.iterator(); i.hasNext(); ) {
////		                // Iterate and handle
////		            	org.jdom.Element attribute = (org.jdom.Element)i.next();
////		            	//System.out.println("$$$$$$$$$$$$ attribute: " + attribute.getValue());
////		                handleAttribute(attribute);
////		            }
//		            // Handle attributes nested within complex types					
//				} catch (JDOMException e2) {
//					// TODO Auto-generated catch block
//					e2.printStackTrace();
//				} catch (IOException e2) {
//					// TODO Auto-generated catch block
//					e2.printStackTrace();
//				}
				
				
				Schema gTypesSchema = null;
				try {
//					SchemaReader schemaReader = new SchemaReader(jdomSchemaElement.getNamespaceURI());
					SchemaReader schemaReader = new SchemaReader(schemaSource);//wsdlURI);
					//System.out.println("################  schemaReader: " + schemaReader);
//					SOAPMappingRegistry smr = new SOAPMappingRegistry();
//					smr.mapTypes("http://schemas.xmlsoap.org/soap/encoding/", 
//					         new QName("http://schemas.xmlsoap.org/soap/encoding/", "Array"),
//					         Object.class,
//					         null,
//					         new ArrayDeserializer());					
					
					//org.apache.xerces.parsers.SAXParser parser = null;
					gTypesSchema = schemaReader.read();
					System.out.println("################  gTypesSchema has element decls: " + gTypesSchema.getElementDecls().hasMoreElements());
					Enumeration testEnum = gTypesSchema.getElementDecls();
					while (testEnum.hasMoreElements()){
						System.out.println("################  gTypesSchema: " + testEnum.nextElement());
					}
					System.out.println("################  gTypesSchema has complex types: " + gTypesSchema.getComplexTypes().hasMoreElements());
					testEnum = gTypesSchema.getComplexTypes();
					while (testEnum.hasMoreElements()){
						ComplexType complexType = (ComplexType)(testEnum.nextElement());
						System.out.println("################  gTypesSchema complex type: " + complexType.getName());
						Enumeration enu = complexType.getAttributeDecls();
						while (enu.hasMoreElements())
						{
							AttributeDecl attributeDecl = (AttributeDecl)enu.nextElement(); 
							System.out.println("################################  gTypesSchema complex type: " + attributeDecl.getReferenceName());
						}
						
						//System.out.println("################  gTypesSchema complex type: " + complexType.getAnnotations().hasMoreElements());
//						Enumeration enu = complexType.getAnnotations();
//						while (enu.hasMoreElements())
//						{
//							System.out.println("################################  gTypesSchema complex type: " + enu.nextElement());
//						}
						
					}	
					
					testEnum = gTypesSchema.getModelGroups();
					System.out.println("################  gTypesSchema has model groups: " + testEnum.hasMoreElements());
					while (testEnum.hasMoreElements()){
						System.out.println("################  gTypesSchema Model Group: " + testEnum.nextElement());
					}
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			  	
			    for (Iterator i = serviceSet.iterator(); i.hasNext(); ) {
		            Entry entry = (Entry) i.next();
		            Service service = (Service) entry.getValue();
//		            System.out.println("==============================================================");
//				  	System.out.println("Service service: " + service);
//				  	System.out.println("==============================================================");
				  	System.out.println("Service: " + service.getQName().toString());	
				  	System.out.println("==============================================================");
				  	
					//String namespace = service.getQName().getNamespaceURI();//"http://Hello.com";
					//String serviceName = "HelloWebService";
					QName serviceQN = service.getQName();//new QName(namespace, serviceName);				  	
				  	String namespace = service.getQName().getNamespaceURI();
					
					javax.xml.rpc.Service serviceObj = null;
					javax.xml.rpc.Call call = null;
					try {
						ServiceFactory serviceFactory = ServiceFactory.newInstance();
						/* The "new URL(wsdlURL)" parameter is optional */
						//Service serviceObj = 
					
						serviceObj = serviceFactory.createService(new URL(wsdlURI), serviceQN);
						
			            call = serviceObj.createCall();
						
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ServiceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				  	Map serviceMap = service.getPorts();
					String inPartName = null;
					QName inPartTypeName = null;
					String outPartName = null;
					QName outPartTypeName = null;
					Set set = serviceMap.entrySet();
				    for (Iterator j = set.iterator(); j.hasNext(); ) {
			            Entry entryPort = (Entry) j.next();
			            Port port = (Port) entryPort.getValue();
			            System.out.println("port name: " + port.getName());
			            QName portQN = new QName(namespace, port.getName());
//			            try {
//							Remote myProxy = serviceObj.getPort(portQN, port.getClass());
//							System.out.println("myProxy: " + myProxy);
//						} catch (ServiceException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//			            
			            //Set Port Type
			            call.setPortTypeName(portQN);
			            
			            System.out.println("==============================================================");
			            Binding binding = port.getBinding();
			            BindingEntry bindingEntry = new BindingEntry(binding);
			            System.out.println("**********************bindingEntry: " + bindingEntry.getParameters());
			            
			            
			            //System.out.println("binding: " + binding);
			            System.out.println("==============================================================");
			            PortType portType = binding.getPortType();
//			            System.out.println("portType: " + portType);
			            System.out.println("==============================================================");
			            System.out.println("portType: " + portType.getQName().getLocalPart());
			            List list = portType.getOperations();
			            
			            //for (Iterator it = list.iterator(); it.hasNext(); ) {
			            Iterator it = list.iterator(); 
			            	
			            while (it.hasNext()){
			            	Operation operation = (Operation) it.next();
		            		System.out.println("Operation: " + operation.getName());
		            		//System.out.println("**********************operation parameters: " + operation.getParameterOrdering());
		            		//System.out.println("**********************operation parameters: " + operation.getInput());
		            		//System.out.println("==============================================================");
		            		//System.out.println("**********************bindingEntry on operation: " + bindingEntry.getParameters(operation));
		            		
		            		call.setOperationName(new QName(namespace, operation.getName()));
		            		call.setProperty(Call.ENCODINGSTYLE_URI_PROPERTY, ""); 
		            		call.setProperty(Call.OPERATION_STYLE_PROPERTY, "wrapped");


		            		//				 display request parameters
		            		Message inputMessage = operation.getInput().getMessage();	
		            		System.out.println("inputMessage: " + inputMessage);
		            		System.out.println("inputMessage Class" + ":" + inputMessage.getClass());

		            		System.out.println("OutputMessage: " + operation.getOutput().getMessage());
		            		//System.out.println("operation.getInput().get: " + operation.getInput().getMessage().getQName().getLocalPart());
		            		//System.out.println("operation.getOutput().getName(): " + operation.getOutput().getMessage().getQName().getLocalPart());
							Map inputPartsMap = operation.getInput().getMessage().getParts();
							Collection inputParts = inputPartsMap.values();
							Iterator inputPartIter = inputParts.iterator();
							Object[] inputParams = new Object[inputParts.size()];
							System.out.println("\tRequest: ");
							while (inputPartIter.hasNext())
							{
								Part part = (Part)inputPartIter.next();
								
//								part.getElementName().getLocalPart();
								
								inPartName = part.getName();
								inPartTypeName = part.getTypeName();//qname
							    // if it's not in the http://www.w3.org/2001/XMLSchema namespace then
							    // we don't know about it - throw an exception
							    String namespaceInput = inPartTypeName.getNamespaceURI();
//							    if(!namespaceInput.equals("http://www.w3.org/2001/XMLSchema")) {
//							    	throw new WSDLException(
//						                    WSDLException.OTHER_ERROR,"Namespace unrecognized");
//							    }
							    // now we can get the Java type which the the QName maps to 
							    // we do this by using the Axis tools which map WSDL types
						            // to Java types in the wsdl2java tool
							    String localPart = inPartTypeName.getLocalPart();
							    QName wsdlQName = new QName(namespaceInput,localPart);
							    
//							    System.out.println("**************namespaceInput: " + namespaceInput);
//							    System.out.println("**************localPart: " + localPart);
//							    System.out.println("**************wsdlQName: " + wsdlQName);
							    
							    TypeMappingRegistry registry = new TypeMappingRegistryImpl();
							    javax.xml.rpc.encoding.TypeMapping tm1 = registry.getTypeMapping("http://schemas.xmlsoap.org/soap/encoding/");
							    TypeMapping tm = null;//DefaultTypeMappingImpl.getSingleton();
							    Class cl = tm.getClassForQName(wsdlQName);
//							    
//							    
//							    ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();
//							    
//							    Class cl;
//								try {
//									cl = ReflectionUtils.getClassForQName(wsdlQName);
//									System.out.println("**************Class: " + cl);
//								} catch (ReflectionException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
							    
								//WsdlUtils.
								
//							    // if the Java type is a primitive, we need to wrap it in an object
							    if(cl.isPrimitive()) {
						    		cl = wrapPrimitive(cl); 
							    }
//							    
//							                    String m_requestElemName = inputElementName;
//							                    m_fullyQualifiedRequestClassName =
//							                            XmlBeanNameUtils.getDocumentElementXmlBeanClassName(m_requestElemName);	

//							    JAXRPCHandler jaxHandler = new JAXRPCHandler();
//							    jaxHandler.
//							    WSDLUtils utils = new WSDLUtils();
//							    utils.getAddressFromPort(arg0);
							    
							    
							    // we could prompt the user to input the param here but we'll just
							    // assume a random number between 1 and 10. First we need to 
							    // find the constructor which takes a string representation of a number
							    // if a complex type was required we would use reflection to break it 
							    // down and prompt the user to input values for each member variable
						        // in Object representing the complex type
							    try {
						    		Constructor cstr = cl.getConstructor(
						                        new Class[] { Class.forName("java.lang.String") });
						    		inputParams[0] = cstr.newInstance(
						                        new Object [] { ""+new Random().nextInt(10) });
							    } catch(Exception e) {
						    		// shoudn't happen
							    	e.printStackTrace();
							    }	
							    
								if (inPartTypeName != null)
									System.out.println(inPartName + " : " + inPartTypeName.getLocalPart() + " , " + inPartTypeName.getNamespaceURI());
								else
									System.out.println(inPartName + " : " + inPartTypeName);
								
								
//								System.out.println("input part.getDocumentationElement()" + ":" + part.getDocumentationElement());
								//System.out.println("input part.getElementName()" + ":" + part.getElementName());
								//System.out.println("input part.getElementName().getLocalPart()" + ":" + part.getElementName().getLocalPart());
//								System.out.println("input part.getExtensionAttributes()" + ":" + part.getExtensionAttributes());
//								System.out.println("input part.getNativeAttributeNames()" + ":" + part.getNativeAttributeNames());
//								
//								List attributes = part.getNativeAttributeNames();
//								for (Iterator iter = attributes.iterator(); iter.hasNext(); ) {
//									Object o = iter.next();
//									System.out.println("What object: " + o);
//									System.out.println("What object: " + o.getClass());
//								}
								
			            		call.addParameter(inPartName, new QName("<xsd:string>"),ParameterMode.IN);	
			            		
			            		if (gTypesSchema != null){
				    				String localpart = inPartTypeName.getLocalPart();
				    				System.out.println(inPartName + " localpart: " + localpart);
			            			//ElementDecl elementDecl = gTypesSchema.getElementDecl(localpart);
				    				//org.exolab.castor.xml.schema.XMLType xmlType = elementDecl.getType();
				    				ComplexType comp = gTypesSchema.getComplexType(localpart);
				    				//System.out.println("????????????????complexType attribute: " + gTypesSchema.getAttribute(localpart));
				    				BlockList blockList = comp.getBlock();
				    				//System.out.println("????????????????complexType blocklist: " + blockList);
				    				//System.out.println("????????????????complexType blocklist: " + comp.getElementDecl(arg0));
				    				//if (xmlType.isComplexType()) {
				    				
				    				Enumeration e =  comp.enumerate();
				    				while (e.hasMoreElements()){
				    					Particle particle = (Particle)e.nextElement();
				    					//System.out.println("????????????????complexType particle: " + particle.get);
				    				}
				    				
				    				//System.out.println("????????????????complexType particle count: " + comp.getParticleCount());
				    				for(int m=0; m<comp.getParticleCount(); m++) {
				    					Particle particle = comp.getParticle(m);
				    					//System.out.println("????????????????complexType particle: " + particle);
				    				}
				    				
				    				
				    				if (comp != null){
				    				//It's a complex type
				    					//ComplexType complexType = (ComplexType) xmlType;
				    					//System.out.println("????????????????complexType: " + comp);
				    					//System.out.println("????????????????complexType: " + comp.getAnyAttribute());
				    				}		
				    				
				    				Enumeration cTypeBits = comp.enumerate();
				    				while(cTypeBits.hasMoreElements())
				    	            {
				    					Object ct = cTypeBits.nextElement();
				    					Structure structure = (Structure)ct ;

				    	                switch(structure.getStructureType())
				    	                {
				    	                	case Structure.GROUP:
				    	                		
				    	                	case Structure.MODELGROUP:
				    	                		//System.out.println("Structure GROUP");
				    	                		System.out.println("========================");
				    	                		System.out.println("Structure MODELGROUP");
				    	                		Group modelGroup = (Group)ct;
				    	                		ContentModelGroup contentModel = modelGroup.getContentModelGroup();
				    	                		int count = contentModel.getParticleCount();
				    	                		for (int n=0; n< count; n++){
				    	                			ElementDecl elementDecl2 = (ElementDecl)contentModel.getParticle(n);
				    	                			System.out.println("Structure MODELGROUP: " + elementDecl2.getName());
				    	                		}
				    	                		System.out.println("========================");
				    	                    break;

				    	                    default:

				    	                            System.out.println("Other ComplexType Structure: " + structure.toString());

				    	                }

				    	             }
				    				
				    				
			            		}
			            		else{
			            			System.out.println("gTypesSchema is nulllllllllllllll");
			            		}
			            		
			            		/*
			            		 * XMLType type = elem.getType();
									if (type != null) {
									   if (type.isSimpleType()) {
									      ...
									   }
									   else if (type.isComplexType()) {
									      ...
									   }
									}
			            		 */
			            		
							}						
							
//			
			//				 display response parameters
							Map outputPartsMap = operation.getOutput().getMessage().getParts();
							Collection outputParts = outputPartsMap.values();
							Iterator outputPartIter = outputParts.iterator();
							System.out.print("\tResponse: ");
							while (outputPartIter.hasNext())
							{
								Part part = (Part)outputPartIter.next();
								outPartName = part.getName();
								outPartTypeName = part.getTypeName();
								if (outPartTypeName != null)
									System.out.println("output: " + outPartName + ":" + outPartTypeName.getLocalPart() + " , " + inPartTypeName.getNamespaceURI());
								else 
									System.out.println("output: " + outPartName + ":" + outPartTypeName);
								
//								System.out.println("output part.getDocumentationElement()" + ":" + part.getDocumentationElement());
//								System.out.println("output part.getElementName()" + ":" + part.getElementName());
//								System.out.println("output part.getExtensionAttributes()" + ":" + part.getExtensionAttributes());
//								System.out.println("output part.getNativeAttributeNames()" + ":" + part.getNativeAttributeNames());	
								
								call.setReturnType(new QName("<xsd:string>"));//
								System.out.println("==============================================================");
							}
		            		
//		            		Object[] inParams = new Object[] {"Jane"};	
//		            		try {
//								String ret = (String) call.invoke(inParams);
//							} catch (RemoteException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}							
			            }
			            
//			            axisInvoke(targetNamespace, serviceName, portName, operationName, inputParams, implURI);
	            	}
			    }	  
			    //System.out.println("Definition: " + definition.toString());
				



				
//				 find service
//				Set<String> keys = definition.getServices().keySet();
				//System.out.println("keys: " + keys.toString());
				
				
				
				//Map map = definition.getPortTypes();
//				String inPartName = null;
//				QName inPartTypeName = null;
//				String outPartName = null;
//				QName outPartTypeName = null;
//				Set set = map.entrySet();
//			    for (Iterator i = set.iterator(); i.hasNext(); ) {
//		            Entry entry = (Entry) i.next();
//		            PortType portType = (PortType) entry.getValue();
//		            System.out.println(portType.getQName().toString());
//		            List list = portType.getOperations();
//		            for (Iterator it = list.iterator(); it.hasNext(); ) {
//	            		Operation operation = (Operation) it.next();
//	            		System.out.println(operation.getName());
//	            		//				 display request parameters
//	            		
//						Map inputPartsMap = operation.getInput().getMessage().getParts();
//						Collection inputParts = inputPartsMap.values();
//						Iterator inputPartIter = inputParts.iterator();
//						System.out.print("\tRequest: ");
//						while (inputPartIter.hasNext())
//						{
//							Part part = (Part)inputPartIter.next();
//							inPartName = part.getName();
//							inPartTypeName = part.getTypeName();
//							if (inPartTypeName != null)
//								System.out.println(inPartName + ":" + inPartTypeName.getLocalPart() + " , " + inPartTypeName.getNamespaceURI());
//							else
//								System.out.println(inPartName + ":" + inPartTypeName);
//						}
//		
//		//				 display response parameters
//						Map outputPartsMap = operation.getOutput().getMessage().getParts();
//						Collection outputParts = outputPartsMap.values();
//						Iterator outputPartIter = outputParts.iterator();
//						System.out.print("\tResponse: ");
//						while (outputPartIter.hasNext())
//						{
//							Part part = (Part)outputPartIter.next();
//							outPartName = part.getName();
//							outPartTypeName = part.getTypeName();
//							if (outPartTypeName != null)
//								System.out.println(outPartName + ":" + outPartTypeName.getLocalPart() + " , " + inPartTypeName.getNamespaceURI());
//							else 
//								System.out.println(outPartName + ":" + outPartTypeName);
//						}	            		
          
				
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WSDLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

			
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ServiceException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
    /**
     * Find the specified extensibility element, if more than one with the specified name exists in the list,
     * return the first one found.
     *
     * @param extensibleElement WSDL type which extends ElementExtensible.
     * @param elementType       Name of the extensiblity element to find.
     * @return ExtensibilityElement The ExtensiblityElement, if not found return null.
     */
    protected static ExtensibilityElement findExtensibilityElement(List extensibilityElements,
                                                                   String elementType) {

        //List extensibilityElements = extensibleElement.getExtensibilityElements();
        if (extensibilityElements != null) {
            for (Object o : extensibilityElements) {
                ExtensibilityElement element = (ExtensibilityElement) o;
                if (element.getElementType().getLocalPart().equalsIgnoreCase(elementType)) {
                    System.out.println("11111111111element.getElementType().getLocalPart(): " + element.getElementType().getLocalPart());
                	return element;
                }
            }
        }
        return null;
    }	
    public static void parseWSDL(String implURI) throws WSDLException {
    	
    	Definition implDef = null;
    	Definition interfaceDef = null;
    	String targetNamespace = null;
    	String serviceName = null;
    	String portName = null;
    	String operationName = null;
    	Object[] inputParams = null;

    	// first get the definition object got the WSDL impl
    	try {
    	    WSDLFactory factory = new WSDLFactoryImpl();
    	    WSDLReader reader = factory.newWSDLReader();
    	    implDef = reader.readWSDL(implURI);
    	} catch(WSDLException e) {
    	    e.printStackTrace();
    	}

    	if(implDef==null) {
    	    throw new WSDLException(WSDLException.OTHER_ERROR,"No WSDL impl definition found.");
    	}

    	// now get the Definition object for the interface WSDL
    	Map imports = implDef.getImports();
    	Set s = imports.keySet();
    	Iterator it = s.iterator();
    	while(it.hasNext()) {
    	    Object o = it.next();
    	    Vector intDoc = (Vector)imports.get(o);
    	    // we want to get the ImportImpl object of it exists
    	    for(int i=0; i<intDoc.size(); i++) {
    		Object obj = intDoc.elementAt(i);
    		if(obj instanceof ImportImpl) {
    		    interfaceDef = ((ImportImpl)obj).getDefinition();
    		}
    	    }
    	}

    	if(interfaceDef == null) {
    	    //throw new WSDLException(WSDLException.OTHER_ERROR,"No WSDL interface definition found.");
    		System.out.println("No WSDL interface definition found.");
    	}

    	// let's get the target namespace Axis will need from the WSDL impl
    	targetNamespace = implDef.getTargetNamespace();

    	// great we've got the WSDL definitions now we need to find the PortType so
    	// we can find the methods we can invoke
    	Vector allPorts = new Vector();
            Map ports = implDef.getPortTypes();
    	s = ports.keySet();
    	it = s.iterator();
    	while(it.hasNext()) {
    	    Object o = it.next();
    	    Object obj = ports.get(o);
    	    if(obj instanceof PortType) {
    		allPorts.add((PortType)obj);
    	    }
    	}	

    	// now we've got a vector of all the port types - normally some logic would
    	// go here to choose which port type we want to use but we'll just choose 
    	// the first one
    	PortType port = (PortType)allPorts.elementAt(0);
    	List operations = port.getOperations();

    	// let's get the service in the WSDL impl which contains this port
    	// to do this we must first find the QName of the binding with the port type 
    	// that corresponds to the port type of our chosen part
    	QName bindingQName = null;
    	Map bindings = implDef.getBindings();
    	s = bindings.keySet();
    	it = s.iterator();
    	while(it.hasNext()) {
    	    Binding binding = (Binding)bindings.get(it.next());
    	    if(binding.getPortType()==port) {
    		// we've got our binding
    		bindingQName = binding.getQName();
    	    }
    	}

    	if(bindingQName==null) {
    	    throw new WSDLException(WSDLException.OTHER_ERROR,"No binding found for chosen port type.");         
    	}

    	// now we can find the service in the WSDL impl which provides an endpoint
    	// for the service we just found above
    	Map implServices = implDef.getServices();
    	s = implServices.keySet();
    	it = s.iterator();
    	while(it.hasNext()) {
    	    Service serv = (Service)implServices.get(it.next());
    	    Map m = serv.getPorts();
    	    Set set = m.keySet(); 
    	    Iterator iter = set.iterator();
    	    while(iter.hasNext()) {
	    		Port p = (Port)m.get(iter.next());
	    		if(p.getBinding().getQName().toString().equals(bindingQName.toString())) {
	    		    // we've got our service store the port name and service name
	    		    portName = serv.getQName().toString();
	    		    serviceName = p.getName();
	    		    break;
	    		}
    	    } 
    	    if(portName != null) break;
    	}
    	
    	// ok now we got all the operations previously - normally we would have some logic here to
    	// choose which operation, however, for the sake of simplicity we'll just 
    	// choose the first one
    	Operation op = (Operation)operations.get(0);
    	operationName = op.getName();



    	// now let's get the Message object describing the XML for the input and output
    	// we don't care about the specific type of the output as we'll just cast it to an Object
    	Message inputs = op.getInput().getMessage();

    	// let's find the input params 
    	Map inputParts = inputs.getParts();
    	// create the object array which Axis will use to pass in the parameters
    	inputParams = new Object[inputParts.size()];
    	s = inputParts.keySet();
    	it = s.iterator();
    	int i=0;
    	while(it.hasNext()) {
    	    Part part = (Part)inputParts.get(it.next());
    	    QName qname = part.getTypeName();
    	    System.out.println("QName name: " + qname);
    	    // if it's not in the http://www.w3.org/2001/XMLSchema namespace then
    	    // we don't know about it - throw an exception
    	    if (qname == null){
    	    	continue;
    	    }
    	    String namespace = qname.getNamespaceURI();
    	    System.out.println("QName namespace: " + namespace);
    	    if(!namespace.equals("http://www.w3.org/2001/XMLSchema")) {
    	    	//throw new WSDLException(WSDLException.OTHER_ERROR,"Namespace unrecognized");
    	    	System.out.println("Namespace unrecognized");
    	    }
    	    
    	    	

    	    // now we can get the Java type which the the QName maps to - we do this
    	    // by using the Axis tools which map WSDL types to Java types in the wsdl2java tool
    	    String localPart = qname.getLocalPart();
    	    System.out.println("localPart: " + localPart);
    	    QName wsdlQName = new QName(namespace,localPart);
    	    TypeMapping tm = null;//DefaultTypeMappingImpl.getSingleton();
    	    
    	    //Class cl = tm.getClassForQName(wsdlQName);
    	    Class cl = tm.getClassForQName(qname);
    	    System.out.println("Class: " + cl);
    	    // if the Java type is a primitive, we need to wrap it in an object
    	    if(cl.isPrimitive()) {
    	    	cl = wrapPrimitive(cl); 
    	    }

    	    // we could prompt the user to input the param here but we'll just
    	    // assume a random number between 1 and 10
    	    // first we need to find the constructor which takes a string representation of a number
    	    // if a complex type was required we would use reflection to break it down
    	    // and prompt the user to input values for each member variable in Object representing
    	    // the complex type
    	    try {
	    		Constructor cstr = cl.getConstructor(new Class[] { Class.forName("java.lang.String") });
	    		inputParams[i] = cstr.newInstance(new Object [] { ""+new Random().nextInt(10) });
    	    } catch(Exception e) {
	    		// shoudn't happen
	    		e.printStackTrace();
    	    }
    	    i++;
    	}	

    	// great now we've built up all the paramters we need to invoke the Web service with Axis
    	// now all we need to do is actually invoke it

    	System.out.print("\nAxis parameters gathered:\nTargetNamespace = "+targetNamespace +"\n"+
    	    "Service Name = "+serviceName +"\n"+
    	    "Port Name = "+portName +"\n"+
    	    "Operation Name = "+operationName+"\n"+
    	    "Input Parameters = ");
    	for(i=0; i<inputParams.length; i++) {
    	    System.out.print(inputParams[i]);
    	    if(inputParams.length != 0 && inputParams.length-1 > i) {
    	    	System.out.print(", ");
    	    }
    	}
    	System.out.println("\n");

    	axisInvoke(targetNamespace, serviceName, portName, operationName, inputParams, implURI);



        }
    /**
     * <p>
     *  This will convert an attribute into constraints.
     * </p>
     *
     * @throws <code>IOException</code> - when parsing errors occur.
     */
    private static void handleAttribute(org.jdom.Element attribute) 
        throws IOException {
        // Get the attribute name and create a Constraint
        String name = attribute.getAttributeValue("name");
        if (name == null) {
            throw new IOException("All schema attributes must have names.");
        }
//        Constraint constraint = new Constraint(name);
        // See if there is a data type on this constraint
        String schemaType = attribute.getAttributeValue("type");
        if (schemaType != null) {
//            constraint.setDataType(
//                DataConverter.getInstance().getJavaType(schemaType));
        }
        // Get the simpleType - if none, we are done with this attribute
        org.jdom.Element simpleType = attribute.getChild("simpleType", schemaNamespace);
        if (simpleType == null) {
            return;
        }
        
        // Handle the data type
        schemaType = simpleType.getAttributeValue("baseType");
        if (schemaType == null) {
            throw new IOException("No data type specified for constraint " + name);
        }
//        constraint.setDataType(DataConverter.getInstance().getJavaType(schemaType));
//        // Handle any allowed values
//        List allowedValues = simpleType.getChildren("enumeration", schemaNamespace);
//        if (allowedValues != null) {
//            for (Iterator i=allowedValues.iterator(); i.hasNext(); ) {
//                Element allowedValue = (Element)i.next();
//                constraint.addAllowedValue(allowedValue.getAttributeValue("value"));
//            }
//        }
//        // Handle ranges
//        Element boundary = simpleType.getChild("minExclusive", schemaNamespace);
//        if (boundary != null) {
//            Double value = new Double(boundary.getAttributeValue("value"));
//            constraint.setMinExclusive(value.doubleValue());
//        }
//        boundary = simpleType.getChild("minInclusive", schemaNamespace);
//        if (boundary != null) {
//            Double value = new Double(boundary.getAttributeValue("value"));
//            constraint.setMinInclusive(value.doubleValue());
//        }
//        boundary = simpleType.getChild("maxExclusive", schemaNamespace);
//        if (boundary != null) {
//            Double value = new Double(boundary.getAttributeValue("value"));
//            constraint.setMaxExclusive(value.doubleValue());
//        }
//        boundary = simpleType.getChild("maxInclusive", schemaNamespace);
//        if (boundary != null) {
//            Double value = new Double(boundary.getAttributeValue("value"));
//            constraint.setMaxInclusive(value.doubleValue());
//        }
//        // Store this constraint
//        constraints.put(name, constraint);
    }
    public static void axisInvoke(String targetNamespace, String serviceName, 
            String portName, String operationName, Object[] inputParams, 
            String implURI) {
    	try {
    	    // first, due to a funny Axis idiosyncrasy we must strip portName of
    	    // it's target namespace so we can pass it in as 
            // targetNamespace, localPart
    	    int index = portName.indexOf(":",
                    portName.indexOf("http://")+new String("http://").length());
    	    String portNamespace = portName.substring(0,index);
   	        portName = portName.substring(
                        index==0?index:index+1); // to strip the :
    	    QName serviceQN = new QName( portNamespace, portName );
    	    org.apache.axis.client.Service service = new org.apache.axis.client.Service(new URL(implURI), serviceQN);
    	    QName portQN = new QName( targetNamespace, serviceName );

    	    // This Call object will be used the invocation
    	    org.apache.axis.client.Call call = (org.apache.axis.client.Call) service.createCall();

    	    // Now make the call...
    	    System.out.println("Invoking service>> " + serviceName + " <<...");
    	    call.setOperation( portQN, operationName );
    	    Object ret = (Integer) call.invoke( inputParams );
   	        System.out.println("Result returned from call to " + serviceName+" -- "+ret);
    	} catch(java.net.MalformedURLException e) {
    	    System.out.println("Error invoking service : "+e);
    	} catch(javax.xml.rpc.ServiceException e2) {
    	    System.out.println("Error invoking service : "+e2);
    	} catch(java.rmi.RemoteException e3) {
    	    System.out.println("Error invoking service : "+e3);
    	}
   }	
    public static Class wrapPrimitive(Class cl) throws WSDLException {
    	String type = cl.getName();
    	try {
    	    if(type.equals("byte")) {
    		return Class.forName("java.lang.Byte");
    	    } else if(type.equals("char")) {
    		return Class.forName("java.lang.Character");
    	    } else if(type.equals("short")) {
    		return Class.forName("java.lang.Short");
    	    } else if(type.equals("int")) {
    		return Class.forName("java.lang.Integer");
    	    } else if(type.equals("double")) {
    		return Class.forName("java.lang.Double");
    	    } else if(type.equals("float")) {
    		return Class.forName("java.lang.Float");
    	    } else if(type.equals("long")) {
    		return Class.forName("java.lang.Long"); 
    	    } else {
    		throw new WSDLException(WSDLException.OTHER_ERROR,"Unrecognized primitive type");
    	    }
    	} catch(ClassNotFoundException e) {
    	    // this should never happen
    	    e.printStackTrace();
    	    return null;
    	}

        }
}
