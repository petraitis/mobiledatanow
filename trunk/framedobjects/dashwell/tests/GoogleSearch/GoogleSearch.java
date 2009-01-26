 package com.framedobjects.dashwell.tests.GoogleSearch;

//Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
//Jad home page: http://www.geocities.com/kpdus/jad.html
//Decompiler options: packimports(3) 
//Source File Name:   GoogleSearch.java

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;
import java.util.jar.JarFile;

import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.commons.jci.compilers.CompilationResult;
import org.apache.commons.jci.compilers.JavaCompiler;
import org.apache.commons.jci.compilers.JavaCompilerFactory;
import org.apache.commons.jci.readers.FileResourceReader;
import org.apache.commons.jci.stores.FileResourceStore;
import org.apache.soap.Fault;
import org.apache.soap.SOAPException;
import org.apache.soap.encoding.SOAPMappingRegistry;
import org.apache.soap.encoding.soapenc.BeanSerializer;
import org.apache.soap.rpc.*;
import org.apache.soap.transport.http.SOAPHTTPConnection;
import org.apache.soap.util.xml.QName;
import org.apache.soap.util.xml.XMLJavaMappingRegistry;

//Referenced classes of package com.google.soap.search:
//         GoogleSearchFault, GoogleSearchResult

public class GoogleSearch
{
	public static void main(String[] args) {
		
		//JarFile jarFile = new JarFile();
		
		JavaCompiler compiler = new JavaCompilerFactory().createCompiler("eclipse");
		CompilationResult result = compiler.compile(sources, new FileResourceReader("c:/Test"), new FileResourceStore("c:/Test"));
		System.out.println( result.getErrors().length + " errors");
		System.out.println( result.getWarnings().length + " warnings");	
		
	}
 public void setKey(String s)
 {
     key = s;
 }

 public void setSoapServiceURL(String s)
 {
     soapServiceURL = s;
 }

 public void setQueryString(String s)
 {
     q = s;
 }

 public void setStartResult(int i)
 {
     start = new Integer(i);
 }

 public void setMaxResults(int i)
 {
     maxResults = new Integer(i);
 }

 public void setFilter(boolean flag)
 {
     filter = new Boolean(flag);
 }

 public void setRestrict(String s)
 {
     restrict = s;
 }

 public void setSafeSearch(boolean flag)
 {
     safeSearch = new Boolean(flag);
 }

 public void setLanguageRestricts(String s)
 {
     lr = new String(s);
 }

 /**
  * @deprecated Method setInputEncoding is deprecated
  */

 public void setInputEncoding(String s)
 {
     ie = s;
 }

 /**
  * @deprecated Method setOutputEncoding is deprecated
  */

 public void setOutputEncoding(String s)
 {
     oe = s;
 }

 public void setProxyHost(String s)
 {
     proxyHost = s;
 }

 public void setProxyPort(int i)
 {
     proxyPort = i;
 }

 public void setProxyUserName(String s)
 {
     proxyUserName = s;
 }

 public void setProxyPassword(String s)
 {
     proxyPassword = s;
 }

 public GoogleSearchResult doSearch()
     throws GoogleSearchFault
 {
     Response response = null;
     try
     {
         response = callRemoteMethodUsingSOAP("doGoogleSearch", generateParamsVector());
     }
     catch(Exception exception)
     {
         throw new GoogleSearchFault(exception.toString());
     }
     if(!response.generatedFault())
     {
         Parameter parameter = response.getReturnValue();
         return (GoogleSearchResult)parameter.getValue();
     } else
     {
         Fault fault = response.getFault();
         throw new GoogleSearchFault("Fault Code = " + fault.getFaultCode() + "\nFault String = " + fault.getFaultString());
     }
 }

 public byte[] doGetCachedPage(String s)
     throws GoogleSearchFault
 {
     Response response = null;
     try
     {
         Vector vector = new Vector();
         vector.addElement(new Parameter("key", java.lang.String.class, key, null));
         vector.addElement(new Parameter("url", java.lang.String.class, s, null));
         response = callRemoteMethodUsingSOAP("doGetCachedPage", vector);
     }
     catch(Exception exception)
     {
         throw new GoogleSearchFault(exception.toString());
     }
     if(!response.generatedFault())
     {
         Parameter parameter = response.getReturnValue();
         return (byte[])parameter.getValue();
     } else
     {
         Fault fault = response.getFault();
         throw new GoogleSearchFault("Fault Code = " + fault.getFaultCode() + "\nFault String = " + fault.getFaultString());
     }
 }

 public String doSpellingSuggestion(String s)
     throws GoogleSearchFault
 {
     Response response = null;
     try
     {
         Vector vector = new Vector();
         vector.addElement(new Parameter("key", java.lang.String.class, key, null));
         vector.addElement(new Parameter("phrase", java.lang.String.class, s, null));
         response = callRemoteMethodUsingSOAP("doSpellingSuggestion", vector);
     }
     catch(Exception exception)
     {
         throw new GoogleSearchFault(exception.toString());
     }
     if(!response.generatedFault())
     {
         Parameter parameter = response.getReturnValue();
         return (String)parameter.getValue();
     } else
     {
         Fault fault = response.getFault();
         throw new GoogleSearchFault("Fault Code = " + fault.getFaultCode() + "\nFault String = " + fault.getFaultString());
     }
 }

 protected Vector generateParamsVector()
 {
     Vector vector = new Vector();
     vector.addElement(new Parameter("key", java.lang.String.class, key, null));
     vector.addElement(new Parameter("q", java.lang.String.class, q, null));
     vector.addElement(new Parameter("start", java.lang.Integer.class, start, null));
     vector.addElement(new Parameter("maxResults", java.lang.Integer.class, maxResults, null));
     vector.addElement(new Parameter("filter", java.lang.Boolean.class, filter, null));
     vector.addElement(new Parameter("restrict", java.lang.String.class, restrict, null));
     vector.addElement(new Parameter("safeSearch", java.lang.Boolean.class, safeSearch, null));
     vector.addElement(new Parameter("lr", java.lang.String.class, lr, null));
     vector.addElement(new Parameter("ie", java.lang.String.class, ie, null));
     vector.addElement(new Parameter("oe", java.lang.String.class, oe, null));
     return vector;
 }

 private SOAPMappingRegistry constructTypeRegistryForGoogleSearch()
 {
     SOAPMappingRegistry soapmappingregistry = new SOAPMappingRegistry();
     BeanSerializer beanserializer = new BeanSerializer();
     soapmappingregistry.mapTypes("http://schemas.xmlsoap.org/soap/encoding/", new QName("urn:GoogleSearch", "GoogleSearchResult"), com.google.soap.search.GoogleSearchResult.class, beanserializer, beanserializer);
     soapmappingregistry.mapTypes("http://schemas.xmlsoap.org/soap/encoding/", new QName("urn:GoogleSearch", "DirectoryCategory"), com.google.soap.search.GoogleSearchDirectoryCategory.class, beanserializer, beanserializer);
     soapmappingregistry.mapTypes("http://schemas.xmlsoap.org/soap/encoding/", new QName("urn:GoogleSearch", "ResultElement"), com.google.soap.search.GoogleSearchResultElement.class, beanserializer, beanserializer);
     return soapmappingregistry;
 }

 protected Response callRemoteMethodUsingSOAP(String s, Vector vector)
     throws MalformedURLException, SOAPException
 {
     URL url = new URL(soapServiceURL);
     Call call = constructCall(s, vector);
     return call.invoke(url, "urn:GoogleSearchAction");
 }

 protected Call constructCall(String s, Vector vector)
 {
     Call call = new Call();
     call.setSOAPMappingRegistry(constructTypeRegistryForGoogleSearch());
     call.setTargetObjectURI("urn:GoogleSearch");
     call.setMethodName(s);
     call.setEncodingStyleURI("http://schemas.xmlsoap.org/soap/encoding/");
     call.setParams(vector);
     SOAPHTTPConnection soaphttpconnection = new SOAPHTTPConnection();
     if(proxyHost != null)
     {
         soaphttpconnection.setProxyHost(proxyHost);
         soaphttpconnection.setProxyPort(proxyPort);
         if(proxyUserName != null)
             soaphttpconnection.setProxyUserName(proxyUserName);
         if(proxyPassword != null)
             soaphttpconnection.setProxyPassword(proxyPassword);
     } else
     {
         String s1 = System.getProperty("http.proxyHost");
         if(s1 != null && !"".equals(s1))
         {
             soaphttpconnection.setProxyHost(s1);
             int i = Integer.getInteger("http.proxyPort", 80).intValue();
             soaphttpconnection.setProxyPort(i);
         }
     }
     call.setSOAPTransport(soaphttpconnection);
     return call;
 }

 public GoogleSearch()
 {
     key = null;
     soapServiceURL = "";
     q = null;
     start = new Integer(0);
     maxResults = new Integer(10);
     filter = new Boolean(true);
     restrict = "";
     safeSearch = new Boolean(false);
     lr = "";
     ie = "UTF-8";
     oe = "UTF-8";
     proxyHost = null;
     proxyPort = 80;
     proxyUserName = null;
     proxyPassword = null;
     if(System.getProperty("google.soapEndpointURL") != null)
         setSoapServiceURL(System.getProperty("google.soapEndpointURL"));
     else
         setSoapServiceURL("http://api.google.com/search/beta2");
 }

 protected static final String defaultEndpointURL = "http://api.google.com/search/beta2";
 protected  String key;
 protected  String soapServiceURL;
 protected  String q;
 protected  Integer start;
 protected  Integer maxResults;
 protected  Boolean filter;
 protected  String restrict;
 protected  Boolean safeSearch;
 protected  String lr;
 protected  String ie;
 protected  String oe;
 protected  String proxyHost;
 protected  int proxyPort;
 protected  String proxyUserName;
 protected  String proxyPassword;
}













































