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

import javax.management.ReflectionException;
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

import org.apache.axis.client.Call;
import org.apache.axis.encoding.DefaultTypeMappingImpl;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.commons.logging.LogFactory;
import org.apache.soap.Constants;
import org.apache.soap.SOAPException;
import org.apache.soap.encoding.SOAPMappingRegistry;
import org.apache.soap.encoding.soapenc.BeanSerializer;
import org.apache.soap.rpc.Parameter;
import org.apache.soap.rpc.Response;
import org.exolab.castor.net.util.URIResolverImpl;
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

public class WebServiceTest2 {
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
    private static final String SCHEMA_NAMESPACE_URI ="http://www.w3.org/1999/XMLschema";

    private static Namespace schemaNamespace;
	/**
	 * @param args
	 */
	public static void main(String[] args) {		
		//String wsdlURI = "http://webservices.amazon.com/AWSECommerceService/AWSECommerceService.wsdl"; // example url
		String wsdlURI ="http://api.google.com/GoogleSearch.wsdl";//"http://soap.amazon.com/schemas2/AmazonWebServices.wsdl";////"http://www.blackberry.com/webservices/ContractorAxis/Contractor.wsdl";////////
		//String wsdlURL = "http://localhost:6080/HelloWebService/services/Hello?wsdl";
		//http://mssoapinterop.org/asmx/xsd/round4XSD.wsdl
		try {
			WSDLFactory wsdlFactory = WSDLFactory.newInstance();
			WSDLReader wsdlReader = wsdlFactory.newWSDLReader();

			wsdlReader.setFeature("javax.wsdl.verbose",false);
			wsdlReader.setFeature("javax.wsdl.importDocuments",true);

			Definition definition = wsdlReader.readWSDL(wsdlURI);
			System.out.println("=====================================" );
			System.out.println("defintion: \n" + definition);
			System.out.println("=====================================" );
			if (definition == null)
			{
				System.err.println("definition element is null");
				System.exit(1);
			}

		  	QName qName = definition.getQName();
		  	System.out.println("definition QName name: " + qName);
		  	System.out.println("QName getLocalPart: " + qName.getLocalPart());
		  	System.out.println("QName getNamespaceURI: " + qName.getNamespaceURI());

		  	
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
		  	

			//Get the the java.wsdl.Types from the java.wsdl.Definition object 
			Types types = definition.getTypes();

			
			//get a schema element (org.w3c.dom.Element)
			ExtensibilityElement schemaExtElem = findExtensibilityElement(types.getExtensibilityElements(), "schema");

			Element schemaElement = null;
			if ((schemaExtElem != null) && (schemaExtElem instanceof UnknownExtensibilityElement)) {
				schemaElement = ((UnknownExtensibilityElement) schemaExtElem).getElement();
			}
			

			
			
			//transformed my schemaElement into a JDOM element (org.jdom.Element) using org.jdom.input.DOMBuilder:
			DOMBuilder domBuilder = new DOMBuilder();
			org.jdom.Element jdomSchemaElement = domBuilder.build(schemaElement);


			


			
			jdomSchemaElement.addNamespaceDeclaration(Namespace.getNamespace("soapenc","http://schemas.xmlsoap.org/soap/encoding/"));
			jdomSchemaElement.addNamespaceDeclaration(Namespace.getNamespace("wsdl","http://schemas.xmlsoap.org/wsdl/"));
			jdomSchemaElement.addNamespaceDeclaration(Namespace.getNamespace("SOAP-ENC","http://schemas.xmlsoap.org/soap/encoding/"));
			jdomSchemaElement.addNamespaceDeclaration(Namespace.getNamespace("soap","http://schemas.xmlsoap.org/wsdl/soap/"));
			//jdomSchemaElement.addNamespaceDeclaration(Namespace.getNamespace("soap","http://schemas.xmlsoap.org/soap/encoding/"));
			
			
			jdomSchemaElement.addNamespaceDeclaration(Namespace.getNamespace("typens",targetNamespace));
			jdomSchemaElement.addNamespaceDeclaration(Namespace.getNamespace("tns",targetNamespace));
			//jdomSchemaElement.addNamespaceDeclaration(Namespace.getNamespace("fns","urn:fault.enterprise.soap.sforce.com"));
			//jdomSchemaElement.addNamespaceDeclaration(Namespace.getNamespace("ens","urn:sobject.enterprise.soap.sforce.com"));
			jdomSchemaElement.addNamespaceDeclaration(Namespace.getNamespace("m0",targetNamespace));
			jdomSchemaElement.addNamespaceDeclaration(Namespace.getNamespace("m1",targetNamespace));
			
			jdomSchemaElement.addNamespaceDeclaration(Namespace.getNamespace("xsd","http://www.w3.org/2001/XMLSchema"));
			
			org.jdom.Element importSchema = new org.jdom.Element("import");
			importSchema.setAttribute("namespace", "http://schemas.xmlsoap.org/soap/encoding/");
			importSchema.setAttribute("schemaLocation", "http://schemas.xmlsoap.org/soap/encoding/");
			importSchema.setNamespace(Namespace.getNamespace("xsd","http://www.w3.org/2001/XMLSchema"));
			
			jdomSchemaElement.addContent(1, importSchema);
			
			//jdomSchemaElement.setNamespace(Namespace.getNamespace("xsd","http://www.w3.org/2001/XMLSchema"));
			
			//Once you have the JDOM element, it's easy to transform it into a Castor Schema (org.exolab.castor.xml.schema.Schema) 
			//using the SchemaReader (org.exolab.castor.xml.schema.reader.SchemaReader).
			//With Castor, its relativly easy to get what you need:
			
			XMLOutputter xmlOutputter = new XMLOutputter();
			String string = xmlOutputter.outputString(jdomSchemaElement.getDocument());
			System.out.println("=====================================" );
			System.out.println("==============  schema: \n" + string);
			System.out.println("=====================================" );
			
			StringReader in2 = new StringReader(string);
			InputSource schemaSource = new InputSource(in2);
			
			Namespace schemaNamespace = Namespace.getNamespace(SCHEMA_NAMESPACE_URI);
			
			Schema gTypesSchema = null;
			try {
				SchemaReader schemaReader = new SchemaReader(schemaSource);//wsdlURI);
				schemaReader.setURIResolver(new URIResolverImpl());
					
				
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
			  	System.out.println("Service: " + service.getQName().toString());	
			  	System.out.println("==============================================================");
			  	
				QName serviceQN = service.getQName();//new QName(namespace, serviceName);				  	
			  	String namespace = service.getQName().getNamespaceURI();
				
				javax.xml.rpc.Service serviceObj = null;
				javax.xml.rpc.Call call = null;
				try {
					ServiceFactory serviceFactory = ServiceFactory.newInstance();
					/* The "new URL(wsdlURL)" parameter is optional */
				
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
	            		
	            		call.setOperationName(new QName(namespace, operation.getName()));
	            		call.setProperty(Call.ENCODINGSTYLE_URI_PROPERTY, ""); 
	            		call.setProperty(Call.OPERATION_STYLE_PROPERTY, "wrapped");


	            		//				 display request parameters
	            		Message inputMessage = operation.getInput().getMessage();	
	            		System.out.println("inputMessage: " + inputMessage);
	            		System.out.println("inputMessage Class" + ":" + inputMessage.getClass());

	            		System.out.println("OutputMessage: " + operation.getOutput().getMessage());
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

						    // now we can get the Java type which the the QName maps to 
						    // we do this by using the Axis tools which map WSDL types
					            // to Java types in the wsdl2java tool
						    String localPart = inPartTypeName.getLocalPart();
						    QName wsdlQName = new QName(namespaceInput,localPart);
						    

						    
							if (inPartTypeName != null)
								System.out.println("wsdlQName: " + inPartName + " : " + inPartTypeName.getLocalPart() + " , " + inPartTypeName.getNamespaceURI());
							else
								System.out.println("wsdlQName: " + inPartName + " : " + inPartTypeName);
							
							
							
		            		call.addParameter(inPartName, new QName("<xsd:string>"),ParameterMode.IN);	
		            		
		    				//System.out.println(inPartName + " localpart: " + localPart);
		    				
		            		if(namespaceInput.equals("http://www.w3.org/2001/XMLSchema")) {
		            			System.out.println("&&&&&&&&&&&&&&&&& TRADITIONAL TYPE &&&&&&&&&&&&&&&&&");
		            			System.out.println(part);
		            			System.out.println("&&&&&&&&&&&&&&&&& TRADITIONAL TYPE &&&&&&&&&&&&&&&&&");
						    }	
						    else{
			            		if (gTypesSchema != null){
			            			System.out.println("&&&&&&&&&&&&&&&&& COMPLEX TYPE &&&&&&&&&&&&&&&&&");
				    				ComplexType comp = gTypesSchema.getComplexType(localPart);
				    				//System.out.println("????????????????complexType attribute: " + gTypesSchema.getAttribute(localpart));
				    				BlockList blockList = comp.getBlock();
				    				
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
						    }

		            		
		            		
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
							
							
							call.setReturnType(new QName("<xsd:string>"));//
							System.out.println("==============================================================");
						}
	            		
		            }
		            
            	}
		    }
			
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WSDLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
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
                    System.out.println("=============element.getElementType().getLocalPart(): " + element.getElementType().getLocalPart());
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

    	    URL url = new  
 	       URL("http://georgetown:8080/soap/servlet/rpcrouter");
    	    org.apache.soap.rpc.Call call = new org.apache.soap.rpc.Call(  );
 	    SOAPMappingRegistry smr = new SOAPMappingRegistry(  );
 	    call.setSOAPMappingRegistry(smr);
 	    call.setEncodingStyleURI(Constants.NS_URI_SOAP_ENC);
 	    call.setTargetObjectURI("urn:BasicTradingService");
 	    call.setMethodName("executeStockTrade");
 	    BeanSerializer beanSer = new BeanSerializer(  );
 	    
 	    // Map the Stock Trade type
 	    smr.mapTypes(Constants.NS_URI_SOAP_ENC,
 	       new QName("urn:BasicTradingService", "StockTrade"),
 	      cl.getDeclaringClass(), beanSer, beanSer);
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
    	    Call call = (Call) service.createCall();

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
