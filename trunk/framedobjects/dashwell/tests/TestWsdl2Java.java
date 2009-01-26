package com.framedobjects.dashwell.tests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.xml.soap.SOAPElement;

import org.apache.axis.tools.ant.wsdl.Wsdl2javaAntTask;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.util.FileUtils;

import wsl.fw.datasource.DataManager;
import wsl.fw.exception.MdnException;
import wsl.fw.resource.ResourceManager;
import wsl.fw.util.CKfw;
import wsl.fw.util.Config;
import wsl.fw.util.Log;
import wsl.mdn.common.MdnAdminConst;
import wsl.mdn.common.MdnDataManager;
import wsl.mdn.common.MdnResourceManager;
import wsl.mdn.dataview.MdnDataCache;
import wsl.mdn.dataview.WebServiceDetail;
import wsl.mdn.server.LicenseManager;
import wsl.mdn.server.MdnServer;

import com.framedobjects.dashwell.utils.ClassHelper;
import com.framedobjects.dashwell.utils.JMDNFile;
import com.framedobjects.dashwell.utils.WebServiceDefinitionHelper;
import com.framedobjects.dashwell.utils.Wsdl2Java;
import com.sun.org.apache.xml.internal.security.Init;


public class TestWsdl2Java {

	//http://developer.ebay.com/webservices/latest/eBaySvc.wsdl 
	/**- timeout*/
	
	//https://www.google.com/api/adsense/v2/SiteFilterService?wsdl 
	/**--Could not read/write to/from file.
	//---Details --	java.lang.NullPointerException --	null
	 * */
	
	//http://code.google.com/apis/coupons/basic_coupon_sample.xml //not wsdl
	//http://api.google.com/GoogleSearch.wsdl
	
	//http://mail.yahooapis.com/ws/mail/v1.1/wsdl
	/**
	 * An error occured while parsing the wsdl document.

Details --
The definition of {urn:yahoo:ymws}MetaData results in a loop.
	 */
	
	
	//https://adcenterapi.microsoft.com/v4/CampaignManagement/CampaignManagement.asmx?wsdl
	/**
	 * Could not read/write to/from file.

Details --
java.lang.NullPointerException
null
	 */
	
	//http://soap.search.msn.com/webservices.asmx?wsdl 
	/**--working in the first step
	//AppId: C404D67EAC4A0841A6CBAA97CA8394257F97AF80 (Firetrust.Ltd)
	 * 
	 */
	
	//http://terraserver-usa.com/TerraService2.asmx?WSDL 
	/**
	 *  -- working in the first step
	 *  -- the request send int 
	 *  -- response is form also ???
	 */
	
	
	//http://webservices.amazon.com/AWSECommerceService/AWSECommerceService.wsdl?
	/**
	 * Java Compile Error.
This usually means, that your WSDL Documents contains errors.

Details --
Compile failed; see the compiler error output for details.

	 */
	
	//http://s3.amazonaws.com/ec2-downloads/ec2.wsdl?
	/**
	 * Java Compile Error.
This usually means, that your WSDL Documents contains errors.

Details --
Compile failed; see the compiler error output for details.

	 */
	
	//http://mechanicalturk.amazonaws.com/AWSMechanicalTurk/AWSMechanicalTurkRequester.wsdl?
	/**
	 * working in the first step
	 * -- do not understand the parameter value
	 */
	
	//http://queue.amazonaws.com/doc/2006-04-01/QueueService.wsdl?
	/**
	 * Java Compile Error.
This usually means, that your WSDL Documents contains errors.

Details --
Compile failed; see the compiler error output for details.

	 */
	
	//http://websearch.amazonaws.com/doc/2006-02-15/AlexaWebSearch.wsdl?
	/**
	 * Java Compile Error.
This usually means, that your WSDL Documents contains errors.

Details --
Compile failed; see the compiler error output for details.

	 */
	
	//http://ats.amazonaws.com/doc/2005-11-21/AlexaTopSites.wsdl?
	/**
	 *  Java Compile Error.
This usually means, that your WSDL Documents contains errors.

Details --
Compile failed; see the compiler error output for details.

	 */
	
	//http://ast.amazonaws.com/doc/2006-05-15/AlexaSiteThumbnail.wsdl? 
	/**--working in the first step
	 * 
	 */
	
	//https://www.google.com/api/adsense/v2/SiteFilterService?wsdl
	/**
	 * Could not read/write to/from file.

Details --
java.lang.NullPointerException
null
	 */
	
	//http://code.google.com/apis/coupons/basic_coupon_sample.xml //not wsdl
	
	//http://www.bloglines.com/search?format=publicapi&apiuser=myusername&apikey=275938797F98797FA9879AF&q=bloglines+freedback
		
	//http://newsisfree.com/nifapi.wsdl
	/**
	 * An error occured while parsing the wsdl document.

Details --
Type boolean is referenced but not defined.

	 */

	//http://geocoder.us/dist/eg/clients/GeoCoder.wsdl
	/**
	 * first step working
	 * type any string, it clear all the input and start again
	 */
	
	//http://www.ejse.com/WeatherService/Service.asmx?WSDL
	/**
	 * first step working
	 * give int get result
	 * An error occurred invoking the specified method.

Server was unable to process request. --> Object reference not set to an instance of an object.
	 */
	
	//http://ws.strikeiron.com/ypcom/yp1?WSDL
	/**
	 * first step working
	 * need to find parameter value (An error occurred invoking the specified method.

No user identifier provided
	 * )
	 */
	
	//http://ws.strikeiron.com/ReversePhoneLookup?WSDL
	/**
	 * first step working
	 * need to find parameter value
	 * An error occurred invoking the specified method.

No user identifier provided
	 */
	
	//http://ws.strikeiron.com/donotcall2_5?WSDL
	/**
	 * first step working
	 * need to find parameter value
	 * An error occurred invoking the specified method.

No user identifier provided
	 */
	
	//http://ws.strikeiron.com/ResidentialLookup3?WSDL
	/**
	 * first step working
	 * need to find parameter value
	 * An error occurred invoking the specified method.

No user identifier provided
	 */
	
	
	private static String outputDirName = "C:/Test/Output/";
	private static String m_wsdlUrl = "http://www.ejse.com/WeatherService/Service.asmx?WSDL";
	private static JMDNFile m_jMdnFile = null;	
    private static boolean enableCaching = true;
    private static String connectTimeout = "5";
    private static String readTimeout = "15";
    private static URLClassLoader m_jarLoader;
    protected static ClassHelper m_classHelper;
    
    protected static List<Class> m_wsdlServiceInterface;
    protected static List<Class> m_wsdlSDI;
    protected static List<Class> m_wsdlTypes;
    protected static List<Class> m_wsdlHolders;
    protected static List<Class> m_wsdlStub;
    protected static List<Class> m_wsdlServiceLocator;	
    
	public TestWsdl2Java() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*Wsdl2javaAntTask wsdl2Java = new Wsdl2javaAntTask();
		wsdl2Java.setURL("http://api.google.com/GoogleSearch.wsdl");
		wsdl2Java.setTimeout(50000);
		wsdl2Java.setOutput(new File("C:/Test"));
		wsdl2Java.execute();
		*/
        // Create temp directory
/*
        File tempDir = new File("C:/Test");

        tempDir.mkdir();
        Project project = new Project();//AntUtil.getProject();
        // axis-java2wsdl

        String wsdlFileName = tempDir + "/service.wsdl";

        //int pos = className.lastIndexOf(".");
        
        
        
        String packagePath = "C:/test";//className.substring(0, pos);

        String[] packagePaths = packagePath.split(".");

        String namespace = "urn:";

        for (int i = packagePaths.length - 1; i >= 0; i--) {
            namespace += packagePaths[i];

            if (i > 0) {
                namespace += ".";
            }
        }
*/
        /*String location = "http://localhost/services/" + serviceName;

        String mappingPackage = packagePath.substring(
            0, packagePath.lastIndexOf(".")) + ".ws";

        

        Java2WsdlAntTask java2Wsdl = new Java2WsdlAntTask();

        NamespaceMapping mapping = new NamespaceMapping();

        mapping.setNamespace(namespace);
        mapping.setPackage(mappingPackage);

        java2Wsdl.setProject(project);
        java2Wsdl.setClassName(className);
        java2Wsdl.setOutput(new File(wsdlFileName));
        java2Wsdl.setLocation(location);
        java2Wsdl.setNamespace(namespace);
        java2Wsdl.addMapping(mapping);

        java2Wsdl.execute();
*/
        // axis-wsdl2java
/*
        Wsdl2javaAntTask wsdl2Java = new Wsdl2javaAntTask();

        wsdl2Java.setProject(project);
        wsdl2Java.setURL("http://api.google.com/GoogleSearch.wsdl");
        wsdl2Java.setOutput(tempDir);
        wsdl2Java.setServerSide(true);
        wsdl2Java.setTestCase(false);
        wsdl2Java.setVerbose(false);
        //wsdl2Java.set
        wsdl2Java.execute();
        
        SOAPElement soapElement;
*/
        // Get content
/*
        String deployContent = FileUtils.read(
            tempDir + "/" + StringUtil.replace(packagePath, ".", "/") +
                "/deploy.wsdd");

        deployContent = StringUtil.replace(
            deployContent, packagePath + "." + serviceName + "SoapBindingImpl",
            className);

        deployContent = _format(deployContent);

        String undeployContent = FileUtil.read(
            tempDir + "/" + StringUtil.replace(packagePath, ".", "/") +
                "/undeploy.wsdd");

        undeployContent = _format(undeployContent);

        // Delete temp directory

        DeleteTask.deleteDirectory(tempDir);
*/
        //return new String[] {deployContent, undeployContent};		

        /*URLClassLoader classLoader = ...;
        DynamicClientFactory dcf = DynamicClientFactory.newInstance();
        Client client = dcf.createClient("people.wsdl", classLoader);

//        The context class loader has been reset by the dynamic client factory's create client code
        Object person = Thread.currentThread().getContextClassLoader().loadClass("com.acme.Person").newInstance();

        Method m = person.getClass().getMethod("setName", String.class);
        m.invoke(person, "Joe Schmoe");

        client.invoke("addPerson", person);   
        */
		//init();	
		//LicenseManager.compileWebService("http://www.ejse.com/WeatherService/Service.asmx?WSDL");
		/*try {
			getWsdl();
		} catch (MdnException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    // classHelper 
	    // TODO fix that jarloader problem, we don't want to create classhelper twice
	    try {
			m_classHelper = new ClassHelper(m_jarLoader);
		} catch (MdnException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   */
		String wsdlUrl = "http://ws.strikeiron.com/ypcom/yp1?WSDL";
		Definition definition = null;
		try {
			definition = WebServiceDefinitionHelper.getWebServiceDefinition(wsdlUrl);
		} catch (WSDLException e) {
			e.printStackTrace();
		}
		String selectedOperation = "SearchCategorySummary";
		WebServiceDetail webServiceDetail = null;
		webServiceDetail = WebServiceDefinitionHelper.loadWebServiceDefinition(definition, selectedOperation);
		
	}

	private static void init(){
		/*
		 *	Set the ResourceManager
		 * (must be first as everything uses resource strings)
		 */
		ResourceManager.set (new MdnResourceManager ());


		// set the config (must be second as nearly everything uses configs)
		Config.setSingleton (MdnAdminConst.MDN_CONFIG_FILE, true);
		Config.getSingleton ().addContext (CKfw.RMICLIENT_CONTEXT);

		// set the DataManager
		DataManager.setDataManager (new MdnDataManager ());

		// set the data cache
		MdnDataCache.setCache (new MdnDataCache (false));		
	}   
    
	  public static void getWsdl() throws MdnException{
		  m_jMdnFile = new JMDNFile(m_wsdlUrl, outputDirName);  
		    // classHelper
		    m_classHelper = new ClassHelper(m_jarLoader);
		    
		    m_wsdlTypes = new ArrayList<Class>();
		    m_wsdlHolders = new ArrayList<Class>();
		    m_wsdlSDI = new ArrayList<Class>();
		    m_wsdlStub = new ArrayList<Class>();
		    m_wsdlServiceInterface = new ArrayList<Class>();
		    m_wsdlServiceLocator = new ArrayList<Class>();
		    
		  boolean wsdlChanged = true;
		    long newChecksum = 0;
		    long oldChecksum = 0;
		    String wsdlFile = null;
		    
		    File jarfile = new File(outputDirName 
		        + m_jMdnFile.getWsdlLocalName() + ".jar");
		    
		    System.out.println("Getting wsdl: " + m_wsdlUrl);
		    
		    /* caching: checksum of WSDL file */
		    if (Boolean.valueOf(enableCaching)){
		      
		      try {
		        URL wsdlUrl = new URL(m_wsdlUrl);
		        
		        URLConnection conn = null;
		        
		        /* dispatch https/http */
		        if (wsdlUrl.getProtocol().equals("http")){
		          conn = (HttpURLConnection) wsdlUrl.openConnection();
		        }
		        else if (wsdlUrl.getProtocol().equals("https")){
/*		        	String keyStore=args[0];
		            String keyStorePassword=args[1];
		            String trustStore=args[2];
		            String trustStorePassword=args[3];
		            String endpointAddress=args[4];

		            try {
		                Stub stub = createProxy();
		                System.setProperty("javax.net.ssl.keyStore", keyStore);
		                System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);
		                System.setProperty("javax.net.ssl.trustStore", trustStore);
		                System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
		                stub._setProperty(javax.xml.rpc.Stub.ENDPOINT_ADDRESS_PROPERTY, endpointAddress);

		                HelloIF hello = (HelloIF)stub;
		                System.out.println(hello.sayHello("Duke! ( secure!"));
	                  } catch (Exception ex) {
	                      ex.printStackTrace();
	                  }
*/

		          /* set the truststore
		           * take temporary truststore if not accepted permanently 
		           */
//		          String trustStore = Properties.getConfig("network.keyStore");
//		          if (m_trustManager.isTemporaryValid())
//		            trustStore = System.getProperty("java.io.tmpdir") 
//		            + File.separator + trustStore;
		        	
//		          System.setProperty("javax.net.ssl.trustStore", 
//		              m_trustManager.getkeystorePath());
		        }
		        
		        // timeout values in seconds
		        int connect_timeout = Integer.parseInt(connectTimeout);
		        int read_timeout = Integer.parseInt(readTimeout);
		        // timeout values needed in milliseconds
		        conn.setConnectTimeout(connect_timeout * 1000);
		        conn.setReadTimeout(read_timeout * 1000);

		        InputStream in = conn.getInputStream();
		        
		        StringBuffer content = new StringBuffer();
		        byte[] buf=new byte[0xFFFF];
		        int len;
		        while ((len = in.read(buf)) > 0) {
		          // somehow axis makes too many line breaks. we don't want them
		          String str = new String(buf, 0, len);
		          content.append(str.replaceAll("[\n\r]{2,}", "\n"));
		        }
		        in.close();
		        
		        /* check if it is actually a WSDL document */
		        if (!content.toString().toLowerCase().trim().endsWith("definitions>"))
		          throw new MdnException("error.notAWSDLDocument");
		        
		        // TODO: download WSDL first, such that afterwards it doesn't
		        // have to be read remotely again
		        // TODO: download referenced files too (e.g. external xsd files)
		        // <xsd:import schemaLocation="XWebEmailValidation.xsd"
		        
		        // set wsdlUrl to local file (avoid refetching of remote one)
		        wsdlFile = outputDirName 
		            + m_jMdnFile.getWsdlLocalName() + ".wsdl";
		        
		        // write WSDL to local file
		        BufferedWriter out = new BufferedWriter(
		            new FileWriter(wsdlFile));
		        out.write(content.toString());
		        out.close();
		        
		        newChecksum = m_jMdnFile.generateChecksum(content.toString());
		        oldChecksum = m_jMdnFile.getChecksum();
		        System.out.println("Wsdl Checksum (" + m_jMdnFile.getWsdlLocalName() + "): old " 
		            + oldChecksum + ", new " + newChecksum);
		        
		        if (newChecksum == oldChecksum && jarfile.exists()){ 
		          wsdlChanged = false;
		          System.out.println("Checksum didn't change, no need to recompile");
		        }
		        //else addChecksum(newChecksum);
		      }
		      catch(SocketTimeoutException e){
		    	  e.printStackTrace();
		    	  throw new MdnException("error.SocketTimeoutException", e);
		      }
		      catch(MalformedURLException e){
		    	  e.printStackTrace();
		    	  throw new MdnException("error.MalformedURLException", e);
		      }
		      catch (IOException e) {
		    	  e.printStackTrace();
		    	  throw new MdnException("error.IOException", e);
		      }
		    }
		    else wsdlChanged = true;
		    
		    /* now compile the web service */
		    compileWebService(wsdlChanged, jarfile, newChecksum);
		  }	
	private static void compileWebService(boolean wsdlChanged, File jarfile, long newChecksum) {
		Emitter parser = null;
		/* Parse the WSDL (invoke wsdl2java) */
        parser = new Emitter();
        parser.setOutputDir(outputDirName);
        parser.setBuildFileWanted(true);
        parser.setAllWanted(true);
        //parser.setImports(true);
        //parser.setNowrap(true); // --> if set, methods may contain too many args
        //parser.setHelperWanted(true);
        //parser.setServerSide(true);
        //parser.setSkeletonWanted(true);
        parser.setWrapArrays(false);
        
        try{ 
//        TODO: download included files for WSDL too (see above)
          parser.run(/*wsdlFile*/m_wsdlUrl);
        }
        catch(Exception e){
          //throw new Exception(Properties.getMessage("error.wsdl2javaParse"), e);
        	e.printStackTrace();
        }        
        /* Compile the classes and create jar (invoke ant) */
        Project project = new Project();
        ProjectHelper helper = ProjectHelper.getProjectHelper();
    
        project.init();
        helper.parse(project, new File("C:/Test/Output/build.xml"));
        
        /* add source files to jar file */
        // create copy task
        Copy copyTask = (Copy)project.createTask("copy");//new Copy();
        FileSet fileset = new FileSet();
        fileset.setDir(new File(project.getProperty("src")));
        fileset.setCaseSensitive(true);
        fileset.setIncludes("**/*.java");
        copyTask.setTodir(new File(project.getProperty("build.classes")));
        copyTask.addFileset(fileset);
        
        Target jarTarget = (Target)project.getTargets().get("jar");
        
        // create include source target (properties as jar target)
        Target newJarTarget = new Target();
        newJarTarget.setName("includeSrc");
        newJarTarget.setLocation(jarTarget.getLocation());
        newJarTarget.setProject(jarTarget.getProject());
        
        // add copy task to target
        newJarTarget.addTask(copyTask);
        // jar target depends on include src target 
        jarTarget.setDepends("includeSrc");
        // add target to project
        project.addOrReplaceTarget("includeSrc", newJarTarget); 
        
        /* disable ant warnings */
        Target compileTarget = (Target)project.getTargets().get("compile");
        Task[] compileTasks = compileTarget.getTasks();
        RuntimeConfigurable compileConfig;
        for(int i = 0; i < compileTasks.length; ++i){
          if(compileTasks[i].getTaskName().equals("javac")){
            compileConfig = compileTasks[i].getRuntimeConfigurableWrapper();
            compileConfig.setAttribute("nowarn","true");
          }
    
        }
        

        
        System.out.println("Starting Compilation...");
        try {
          project.executeTarget("jar");
        }
        catch (BuildException e) {
          e.printStackTrace();
        }
        finally{
          // remove generated sources
        	m_jMdnFile.cleanOutputDir();
        }
        
        //System.out.println("Generated jar file: " + jarfile.getAbsolutePath()); 		

        try{ 
            m_jarLoader = new URLClassLoader(new URL[] { jarfile.toURL() }); 
          }
          catch(MalformedURLException e){
            e.printStackTrace();
        	  
          }
          
          /* now load the classes */
          try {
			loadClasses(wsdlChanged, parser, newChecksum);
		} catch (MdnException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	/*public static Project getProject() { 
		Project project = new Project(); 
		SystemLogger logger = new SystemLogger(); 
		logger.setMessageOutputLevel(Project.MSG_INFO); 
		logger.setOutputPrintStream(System.out); 
		logger.setErrorPrintStream(System.err); 
		project.addBuildListener(logger); 
		return project; 
	} */
	
  private static void loadClasses(boolean wsdlChanged, Emitter parser, long newChecksum) 
      throws MdnException{
    /* Classify classes */
    List generatedClasses = null;
    if (wsdlChanged){
      generatedClasses = parser.getGeneratedClassNames();
      m_jMdnFile.createJMDNFile(generatedClasses, newChecksum);
    }
    else
      generatedClasses = m_jMdnFile.getGeneratedClassNames();
    
    for(Object o : generatedClasses){
      if (o != null){
        String classname = o.toString();
        System.out.println("classname: " + classname);
        Class cl = null;
        try{ 
          cl = m_jarLoader.loadClass(classname);
        }
        catch(ClassNotFoundException e){
          throw new MdnException("error.ClassNotFoundException", e);
        }
        
        /* categorize the class */
        if (cl.isInterface()){
          // service interface
          if (m_classHelper.hasInterface(cl, "javax.xml.rpc.Service")){
            m_wsdlServiceInterface.add(cl);
            System.out.println("Got class: service interface: " + cl.getCanonicalName());
          }
          // sdi (service definition interface, aka porttypes)
          else if(m_classHelper.hasInterface(cl, "java.rmi.Remote")){
            m_wsdlSDI.add(cl);
            System.out.println("Got class: SDI: " + cl.getCanonicalName());
          }
        }
        else if (!cl.isArray() && !cl.isPrimitive()){ // is class
          // service locator 
          if (m_classHelper.hasSuperclass(cl, "org.apache.axis.client.Service")){
            m_wsdlServiceLocator.add(cl);
            System.out.println("Got class: service locator: " + cl.getCanonicalName());
          }
          // stub (aka binding)
          else if (m_classHelper.hasSuperclass(cl, "org.apache.axis.client.Stub")){
            m_wsdlStub.add(cl);
            System.out.println("Got class: stub: " + cl.getCanonicalName());
          }
          // holder
          else if(m_classHelper.hasInterface(cl, "javax.xml.rpc.holders.Holder")){
            m_wsdlHolders.add(cl);
            System.out.println("Got class: holder: " + cl.getCanonicalName());
          }
          // type (bean in most cases)
          else if (m_classHelper.hasInterface(cl, "java.io.Serializable")){
            m_wsdlTypes.add(cl);
            System.out.println("Got class: type: " + cl.getCanonicalName());
          }
          // unknown class type
          else{
        	  System.out.println("error.unknownClassType"); 
                //new String[]{cl.getCanonicalName()}));
          }
        }
      }
    }
  }
  	
}
