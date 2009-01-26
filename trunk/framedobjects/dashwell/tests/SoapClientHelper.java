package com.framedobjects.dashwell.tests;

import java.util.*;
import java.net.*;
import java.io.*;
import java.lang.reflect.*;
import org.apache.soap.util.xml.*;
import org.apache.soap.*;
import org.apache.soap.encoding.*;
import org.apache.soap.encoding.soapenc.*;
import org.apache.soap.rpc.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import org.apache.xerces.parsers.*;
import org.apache.xpath.*;


public class SoapClientHelper
{
  // this one can be shared among services
  //--------------the shared parser is declared here
  private static DOMParser parser = new DOMParser();

  // this is Service specific data
  private static class ServiceData {
    private SOAPMappingRegistry smr = new SOAPMappingRegistry();
    private InvocationHandler invocationHandler;
    private String servletURLName;
  }
  // map of ServiceData indexed by class
  private static Map serviceMap = new HashMap();

  //--------------this static block finishes setting up the parser
  static
  {
    try
    {
      parser.setFeature( "http://xml.org/sax/features/namespaces", true);
      parser.setErrorHandler( new org.xml.sax.ErrorHandler() {
          public void warning (org.xml.sax.SAXParseException exception) {
            System.out.println( exception );
          }
          public void error (org.xml.sax.SAXParseException exception) {
            System.out.println( exception );
          }
          public void fatalError (org.xml.sax.SAXParseException exception) {
            System.out.println( exception );
          }
        }
      );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
  }

  public static synchronized void initService( final Class serviceClass,
    String ddXMLFileName, String servletURL )
    throws SoapClientHelperException
  {
    ServiceData serviceData = new ServiceData();
    try {
      parser.parse( ddXMLFileName );
      Document document = parser.getDocument();
      Element service = document.getDocumentElement();
      final String id = XPathAPI.selectSingleNode( service, "@id",
        service ).getNodeValue();
      String type = XPathAPI.selectSingleNode( service, "isd:provider/@type",
        service ).getNodeValue();
      String methods = XPathAPI.selectSingleNode( service,
        "isd:provider/@methods", service ).getNodeValue();
      String clazz = XPathAPI.selectSingleNode( service,
        "isd:provider/isd:java/@class", service ).getNodeValue();

      NodeList mappings = XPathAPI.selectNodeList( service,
                                                  "isd:mappings/*" );
      for ( int i=0; i<mappings.getLength(); i++ )
      {
        Node node = mappings.item(i);
        NamedNodeMap attrs = node.getAttributes();

        String x = null;
        if ( attrs.getNamedItem( "xmlns:x" ) != null )
          x = (String)attrs.getNamedItem( "xmlns:x" ).getNodeValue();

        String javaType = null;
        Class javaTypeClass = null;
        QName qname = null;
        if ( attrs.getNamedItem( "javaType" ) != null )
        {
          javaType = (String)attrs.getNamedItem( "javaType" ).getNodeValue();
          javaTypeClass = Class.forName( javaType );
        }

        if ( attrs.getNamedItem( "qname" ) != null )
        {
          String qnameString = (String)attrs.getNamedItem( "qname" ).getNodeValue();
  //--------------this is a bit crude!
          qname = new QName( x, qnameString.substring(2) );
        }

        String java2XMLClassName = null;
        Class j2XSerializerClass = null;
        Serializer j2XSerializer = null;
        if ( attrs.getNamedItem( "java2XMLClassName" ) != null )
        {
          java2XMLClassName = (String)attrs.getNamedItem( "java2XMLClassName" ).getNodeValue();
          j2XSerializerClass = Class.forName( java2XMLClassName );
          j2XSerializer = (Serializer)j2XSerializerClass.newInstance();
        }

        String xml2JavaClassName = null;
        Class x2JSerializerClass = null;
        Deserializer x2JSerializer = null;
        if ( attrs.getNamedItem( "xml2JavaClassName" ) != null )
        {
          xml2JavaClassName = (String)attrs.getNamedItem( "xml2JavaClassName" ).getNodeValue();
          x2JSerializerClass = Class.forName( xml2JavaClassName );
          x2JSerializer = (Deserializer)x2JSerializerClass.newInstance();
        }

  //--------------here is the type marshallers registration
        serviceData.smr.mapTypes( Constants.NS_URI_SOAP_ENC, qname,
          javaTypeClass, j2XSerializer, x2JSerializer );
      }

  //--------------that's the proxy's invocation handler
      serviceData.invocationHandler = new InvocationHandler() {
        public Object invoke(Object proxy, Method method, Object[] args)
          throws SoapClientHelperException
        {
          Vector params = new Vector();
          for (int i=0; i<args.length; i++)
            params.addElement(args[i]);
          Response resp = SoapClientHelper.makeSoapCall( serviceClass, id, method.getName(), params);
          return resp.getReturnValue().getValue();
        }
      };
      serviceData.servletURLName = servletURL;
      serviceMap.put( serviceClass, serviceData );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      throw new SoapClientHelperException ( e.getMessage() );
    }
  }

  //--------------that's the proxy's invocation handler
  public static Object makeServiceInstance( Class serviceClass )
  throws SoapClientHelperException
  {
    ServiceData serviceData = (ServiceData)serviceMap.get( serviceClass );
    if ( serviceData == null )
      throw new SoapClientHelperException ( "Service " + serviceClass.getName() + " not initialzed"  );
    return Proxy.newProxyInstance( SoapClientHelper.class.getClassLoader(),
        new Class[] {serviceClass}, serviceData.invocationHandler );
  }

  //--------------this is where we really factor out the code
  private static Response makeSoapCall( Class serviceClass, String serviceName,
    String methodName, Vector params)
    throws SoapClientHelperException
  {
    ServiceData serviceData = (ServiceData)serviceMap.get( serviceClass );
    if ( serviceData == null )
      throw new SoapClientHelperException ( "Service " + serviceClass.getName() + " not initialzed"  );
    try
    {
      Vector paramParams = new Vector();
      for (int i=0; i<params.size(); i++)
  //--------------note that the parameter names seem quite irrelevant
        paramParams.addElement(new Parameter("dummy",
                                             params.elementAt(i).getClass(),
                                             params.elementAt(i), null));
      Call call = new Call();
      call.setSOAPMappingRegistry( serviceData.smr );
      call.setTargetObjectURI(serviceName);
      call.setMethodName(methodName);
      call.setEncodingStyleURI(Constants.NS_URI_SOAP_ENC);
      if ( paramParams.size() > 0 )
        call.setParams(paramParams);
      Response resp = call.invoke(new URL( serviceData.servletURLName ), "");
      if (resp.generatedFault())
        throw new SoapClientHelperException( resp.getFault().getFaultCode(),
          resp.getFault().getFaultString() );
      return resp;
    }
    catch (SOAPException e)
    {
      throw new SoapClientHelperException ( e );
    }
    catch (MalformedURLException e)
    {
      throw new SoapClientHelperException ( e.getMessage() );
    }
  }
}
