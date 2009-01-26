package com.framedobjects.dashwell.utils.webservice;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
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

import org.exolab.castor.net.util.URIResolverImpl;
import org.exolab.castor.xml.schema.ComplexType;
import org.exolab.castor.xml.schema.ContentModelGroup;
import org.exolab.castor.xml.schema.ElementDecl;
import org.exolab.castor.xml.schema.Group;
import org.exolab.castor.xml.schema.Schema;
import org.exolab.castor.xml.schema.Structure;
import org.exolab.castor.xml.schema.reader.SchemaReader;
import org.jdom.Content;
import org.jdom.Namespace;
import org.jdom.input.DOMBuilder;
import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;

import wsl.mdn.dataview.WebServiceDetail;

public class WebServiceDefinitionHelper {

	
	public static WebServiceDetail loadWebServiceDefinition(Definition definition, String selectedOperation){
		//		if (_selectedOperationDetailLoaded){
		//		return;
		//	}
			WebServiceDetail selectedOperationDetail = null;
			if (definition == null){
				return null;
			}
			//URIResolver uriResolver = null;
			
			String targetNamespace = definition.getTargetNamespace();
			System.out.println("=====================================" );
			System.out.println("defintion: \n" + definition);
			System.out.println("=====================================" );
			//Get the the java.wsdl.Types from the java.wsdl.Definition object 
			Types types = definition.getTypes();
			//get a schema element (org.w3c.dom.Element)
			Vector<ExtensibilityElement> schemaExtElem = findExtensibilityElement(types.getExtensibilityElements(), "schema");			
			org.w3c.dom.Element schemaElement = null;
//			if ((schemaExtElem != null) && (schemaExtElem instanceof UnknownExtensibilityElement)) {
//				schemaElement = ((UnknownExtensibilityElement) schemaExtElem).getElement();
//			}
			 for(int i=0;i<schemaExtElem.size();i++){

                 ExtensibilityElement currentSchemaElement=(ExtensibilityElement)schemaExtElem.elementAt(i);

                 if(currentSchemaElement!=null&&currentSchemaElement instanceof UnknownExtensibilityElement){

		              schemaElement = ((UnknownExtensibilityElement)schemaElement).getElement();
		
		              //Schema schema=createschemafromtype(schemaElementt,wsdlDefinition);

                      //schemas.add(schema);

                 }

			 }

     					
			if (schemaElement == null){
                System.err.println("Unable to find schema extensibility element in WSDL");
                return null;				
			}
			
			//transformed schemaElement into a JDOM element (org.jdom.Element) using org.jdom.input.DOMBuilder:
			DOMBuilder domBuilder = new DOMBuilder();
			org.jdom.Element jdomSchemaElement = domBuilder.build(schemaElement);
			jdomSchemaElement.addNamespaceDeclaration(Namespace.getNamespace("soapenc","http://schemas.xmlsoap.org/soap/encoding/"));
			jdomSchemaElement.addNamespaceDeclaration(Namespace.getNamespace("wsdl","http://schemas.xmlsoap.org/wsdl/"));
			jdomSchemaElement.addNamespaceDeclaration(Namespace.getNamespace("SOAP-ENC","http://schemas.xmlsoap.org/soap/encoding/"));
			jdomSchemaElement.addNamespaceDeclaration(Namespace.getNamespace("soap","http://schemas.xmlsoap.org/wsdl/soap/"));
						
			jdomSchemaElement.addNamespaceDeclaration(Namespace.getNamespace("typens",targetNamespace));
			jdomSchemaElement.addNamespaceDeclaration(Namespace.getNamespace("tns",targetNamespace));
			//jdomSchemaElement.addNamespaceDeclaration(Namespace.getNamespace("fns","urn:fault.enterprise.soap.sforce.com"));
			//jdomSchemaElement.addNamespaceDeclaration(Namespace.getNamespace("ens","urn:sobject.enterprise.soap.sforce.com"));
			jdomSchemaElement.addNamespaceDeclaration(Namespace.getNamespace("m0",targetNamespace));
			jdomSchemaElement.addNamespaceDeclaration(Namespace.getNamespace("m1",targetNamespace));
			
			jdomSchemaElement.addNamespaceDeclaration(Namespace.getNamespace("xsd","http://www.w3.org/2001/XMLSchema"));
			
			jdomSchemaElement.addNamespaceDeclaration(Namespace.getNamespace("xsd","http://www.w3.org/2001/XMLSchema-instance"));//TODO
			Content existingImport = jdomSchemaElement.getContent(1);
			org.jdom.Element existingImportSchema;
			try {
				existingImportSchema = (org.jdom.Element)existingImport;
				if(existingImportSchema.getName().equals("import")){
					existingImportSchema.setAttribute("schemaLocation", targetNamespace);
				}
				
			} catch (RuntimeException e1) {
				//nothing
			}
			
			
			org.jdom.Element importSchema = new org.jdom.Element("import");
			importSchema.setAttribute("namespace", "http://schemas.xmlsoap.org/soap/encoding/");
			importSchema.setAttribute("schemaLocation", "http://schemas.xmlsoap.org/soap/encoding/");
			importSchema.setNamespace(Namespace.getNamespace("xsd","http://www.w3.org/2001/XMLSchema"));
			importSchema.setNamespace(Namespace.getNamespace("xsd","http://www.w3.org/2001/XMLSchema-instance"));//TODO
			//importSchema.addNamespaceDeclaration(Namespace.getNamespace("m0",targetNamespace));
			//importSchema.setAttribute("schemaLocation", targetNamespace);
			//importSchema.setAttribute("namespace", targetNamespace);
			
			jdomSchemaElement.addContent(1, importSchema);
		
			
			
			XMLOutputter xmlOutputter = new XMLOutputter();
			String string = xmlOutputter.outputString(jdomSchemaElement.getDocument());
			System.out.println("=====================================" );
			System.out.println("==============  schema: \n" + string);
			System.out.println("=====================================" );
			StringReader in2 = new StringReader(string);
			InputSource schemaSource = new InputSource(in2);
			
			//Once have the JDOM element, transform it into a Castor Schema (org.exolab.castor.xml.schema.Schema) 
			//using the SchemaReader (org.exolab.castor.xml.schema.reader.SchemaReader).
			Schema gTypesSchema = null;
			try {
				SchemaReader schemaReader = new SchemaReader(schemaSource);
				schemaReader.setURIResolver(new URIResolverImpl());
				gTypesSchema = schemaReader.read();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			Map s = definition.getServices();
			Set serviceSet = s.entrySet();
			for (Iterator i = serviceSet.iterator(); i.hasNext(); ) {
			    Entry entry = (Entry) i.next();
			    Service service = (Service) entry.getValue();
			    
			    Map serviceMap = service.getPorts();
			    Set set = serviceMap.entrySet();
			    for (Iterator j = set.iterator(); j.hasNext(); ) {
			        Entry entryPort = (Entry) j.next();
			        Port port = (Port) entryPort.getValue();
			        
			        Binding binding = port.getBinding();
			        PortType portType = binding.getPortType();
			        List list = portType.getOperations();
			        Iterator it = list.iterator(); 
			        while (it.hasNext()){
			        	Operation operation = (Operation) it.next();
			        	boolean selected = false;
			        	//for (int m = 0; m < operationArray.length; m++){
		        		if (operation.getName().equalsIgnoreCase(selectedOperation)){
		        			selected = true;
		        		}
			        	//}
			        	if (selected){
			        		WebServiceDetail webServiceDetail = new WebServiceDetail(operation);
			        		Map inputPartsMap = operation.getInput().getMessage().getParts();
			        		Collection inputParts = inputPartsMap.values();
			        		Iterator inputPartIter = inputParts.iterator();
							while (inputPartIter.hasNext())
							{
								Part part = (Part)inputPartIter.next();
								QName inPartTypeName = null;
								inPartTypeName = part.getTypeName();
								if (inPartTypeName != null){
									String localPart = inPartTypeName.getLocalPart();
									String namespaceInput = inPartTypeName.getNamespaceURI();
									if(namespaceInput.equals("http://www.w3.org/2001/XMLSchema")) {
										webServiceDetail.addParameter(part.getName());
										webServiceDetail.setType(WebServiceDetail.TYPE_SIMPLE);
									}else{
										webServiceDetail.setObjectName(localPart);
										if (gTypesSchema != null){
											ComplexType comp = gTypesSchema.getComplexType(localPart);
						    				Enumeration cTypeBits = comp.enumerate();
						    				while(cTypeBits.hasMoreElements())
						    	            {
						    					Object ct = cTypeBits.nextElement();
						    					Structure structure = (Structure)ct ;
						    					
						    	                switch(structure.getStructureType())
						    	                {
						    	                	case Structure.GROUP:					    	                		
						    	                	case Structure.MODELGROUP:
						    	                		Group modelGroup = (Group)ct;
						    	                		ContentModelGroup contentModel = modelGroup.getContentModelGroup();
						    	                		webServiceDetail.setType(WebServiceDetail.TYPE_COMPLEX);
						    	                		int count = contentModel.getParticleCount();
						    	                		for (int n=0; n< count; n++){
						    	                			ElementDecl elementDecl2 = (ElementDecl)contentModel.getParticle(n);
						    	                			//System.out.println("Structure MODELGROUP: " + elementDecl2.getName());
						    	                			webServiceDetail.addParameter(elementDecl2.getName());
						    	                		}
						    	                		break;
						    	                    default:
						    	                    	System.out.println("Other ComplexType Structure: " + structure.toString());
						    	                }
		
						    	             }										
										}
									}								
								}
								else{
									webServiceDetail.addParameter(part.getName());
								}
		
								
							}//while (inputPartIter.hasNext())
							
							selectedOperationDetail = webServiceDetail;
			        	}
			        	
			        }
			    }
			}
		//	_selectedOperationDetailLoaded = true;
			return selectedOperationDetail;
		}	
	    /**
	     * Find the specified extensibility element, if more than one with the specified name exists in the list,
	     * return the first one found.
	     *
	     * @param extensibleElement WSDL type which extends ElementExtensible.
	     * @param elementType       Name of the extensiblity element to find.
	     * @return ExtensibilityElement The ExtensiblityElement, if not found return null.
	     */
	    protected static Vector<ExtensibilityElement> findExtensibilityElement(List extensibilityElements,
	                                                                   String elementType) {

	        //List extensibilityElements = extensibleElement.getExtensibilityElements();
	        Vector schemaExtElem = new Vector<ExtensibilityElement>();
	    	if (extensibilityElements != null) {
	            for (Object o : extensibilityElements) {
	                ExtensibilityElement element = (ExtensibilityElement) o;
	                if (element.getElementType().getLocalPart().equalsIgnoreCase(elementType)) {
	                    System.out.println("---->>>element.getElementType().getLocalPart(): " + element.getElementType().getLocalPart());
	                    schemaExtElem.add(element);
	                }
	            }
	        }
	        return (Vector<ExtensibilityElement>)schemaExtElem;
	    }	
		public static Definition getWebServiceDefinition(String wsdlUrl) throws WSDLException{
			
			WSDLFactory wsdlFactory = WSDLFactory.newInstance();
			WSDLReader wsdlReader = wsdlFactory.newWSDLReader();

			wsdlReader.setFeature("javax.wsdl.verbose",false);
			wsdlReader.setFeature("javax.wsdl.importDocuments",true);
			
			Definition definition;
			definition = wsdlReader.readWSDL(wsdlUrl);
			
			return definition;
		}  
}
