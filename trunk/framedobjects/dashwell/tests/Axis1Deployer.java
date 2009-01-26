package com.framedobjects.dashwell.tests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.OperationType;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap12.SOAP12Binding;
import javax.wsdl.extensions.soap12.SOAP12Body;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLWriter;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.deployment.wsdd.WSDDProvider;
import org.apache.axis.tools.ant.axis.AdminClientTask;
import org.apache.axis.tools.ant.wsdl.TypeMappingVersionEnum;
import org.apache.axis.tools.ant.wsdl.Wsdl2javaAntTask;
import org.apache.axis.wsdl.toJava.Utils;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.taskdefs.Expand;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.jbpm.JbpmException;
import org.ow2.orchestra.ws.WSDeployer;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Axis engine WS deployer.
 */
public class Axis1Deployer extends WSDeployer {

  // WSDD constants
  /** deploy.wsdd. */
  public static final String WSDD_DEPLOY_FILE_NAME = "deploy.wsdd";
  /** undeploy.wsdd. */
  public static final String WSDD_UNDEPLOY_FILE_NAME = "undeploy.wsdd";
  /** axis deployment namespace. */
  public static final String WSDD_NS = "http://xml.apache.org/axis/wsdd/";
  /** wsdd root element name. */
  public static final String WSDD_SERVICE = "service";
  /** wsdd root element style attribute name. */
  public static final String WSDD_SERVICE_STYLE_ATTR = "style";
  /** wsdd root element provider attribute name. */
  public static final String WSDD_SERVICE_PROVIDER_ATTR = "provider";
  /** wsdd style attribute message value. */
  public static final String MESSAGE_STYLE = "Message";
  /** utility var to rename wsdd file with a suffix. */
  public static final String NEW_WSDD_SUFFIX = ".message";
  /** scope of the wsdd generated. */
  public static final String SCOPE = "Request";
  /** jdkVersion used to compile generated java files. */
  public static final String JDK_VERSION = "1.5";
  /** utility variable to define ant environment.*/
  public static final String ANT_ENV = "antEnv";

  private Project antProject;
  private File tmpDir;
  private AxisConfiguration axisConfiguration;
  private Definition wsdlDefinition;

  /**
   * Default constructor.
   * @param wsId - id of the WS : in our case, processName
   * @param wsdlUrl - url of the wsdl file to deploy.
   * @param orchestraDirectory - Absolute path to the orchestra directory.
   */
  public Axis1Deployer(final QName processQName, Definition wsdlDefinition) {
    super(processQName);
    this.wsdlDefinition = wsdlDefinition;
    setAntProject();
    this.axisConfiguration = AxisConfiguration.getInstance();
    setProxy();
  }

  /**
   * return null if operation style is supported by Axis engine. Else, returns a message
   * explaining the problem.
   * @param operationStyle - operationStyle to check
   * @return null if operation style is supported by Axis engine. Else, returns a message
   * explaining the problem.
   */
  protected String checkOperationStyle(final String operationStyle) {
    if (operationStyle == null) {
      return "Style attribute of this operation must be specified";
    } else if (!operationStyle.equals("document")) {
      return "Style attribute of this operation must be : document";
    }
    return null;
  }

  /**
   * return null if operation type is supported by Axis engine. Else, returns a message
   * explaining the problem.
   * @param operationType - operationType to check
   * @return null if operation type is supported by Axis engine. Else, returns a message
   * explaining the problem.
   */
  protected String checkOperationType(final OperationType operationType) {
    if (!operationType.equals(OperationType.REQUEST_RESPONSE)
        && !operationType.equals(OperationType.ONE_WAY)) {
      return "Operation type : " + operationType
      + " is not supported. Please use one of : "
      + OperationType.ONE_WAY + "/"
      + OperationType.REQUEST_RESPONSE;
    }
    return null;
  }

  /**
   * return null if soapVersion is supported by Axis engine. Else, returns a message
   * explaining the problem.
   * @param soapBinding - soapBinding to check
   * @return null if soapVersion is supported by Axis engine. Else, returns a message
   * explaining the problem.
   */
  protected String checkSoapVersion(final ExtensibilityElement soapBinding) {
    String soapVersion = null;
    if (!(soapBinding instanceof SOAPBinding)
        && !(soapBinding instanceof SOAP12Binding)) {
      return "Supported Soap Version are " + URI_WSDL11_SOAP11 + "/"
      + URI_WSDL11_SOAP12;
    }
    return null;
  }

  /**
   * return null if transport is supported by Axis engine. Else, returns a message
   * explaining the problem.
   * @param soapBinding - soapBinding to check
   * @return null if transport is supported by Axis engine. Else, returns a message
   * explaining the problem.
   */
  protected String checkTransport(final ExtensibilityElement soapBinding) {
    String transportUri = "";
    if (soapBinding instanceof SOAPBinding) {
      transportUri = ((SOAPBinding) soapBinding).getTransportURI();
    } else if (soapBinding instanceof SOAP12Binding) {
      transportUri = ((SOAP12Binding) soapBinding).getTransportURI();
    }
    if (!SOAP_HTTP_TRANSPORT_URI.equals(transportUri)) {
      return "Transport URI : " + transportUri
      + " is not supported. Please use "
      + SOAP_HTTP_TRANSPORT_URI;
    }
    return null;
  }

  /**
   * return null if use is supported by Axis engine. Else, returns a message
   * explaining the problem.
   * @param soapBody - soapBody to check
   * @return null if use is supported by Axis engine. Else, returns a message
   * explaining the problem.
   */
  protected String checkUse(final ExtensibilityElement soapBody) {
    String use = "";
    if (soapBody instanceof SOAPBody) {
      use = ((SOAPBody) soapBody).getUse();
    } else if (soapBody instanceof SOAP12Body) {
      use = ((SOAP12Body) soapBody).getUse();
    }
    if (!"literal".equals(use)) {
      return "Use : " + use + " is not supported. Please use " + "literal";
    }
    return null;
  }

  /**
   * return null if soapBody attributes are supported by Axis engine. Else, returns a message
   * explaining the problem.
   * @param soapBody - soapBody to check
   * @return null if soapBody attributes are supported supported by Axis engine. Else, returns a message
   * explaining the problem.
   */
  protected String checkSoapBody(final ExtensibilityElement soapBody) {
    List parts = null;
    if (soapBody instanceof SOAPBody) {
      parts = ((SOAPBody) soapBody).getParts();
    } else if (soapBody instanceof SOAP12Body) {
      parts = ((SOAP12Body) soapBody).getParts();
    }
    if (parts != null) {
      return "SoapBody is using parts attribute which is not currently supported.";
    }
    return null;
  }

  /**
   * Check if WS engine is available. Else throws an exception.
   * @throws Exception - ex if ws engine is not available
   */
  protected void checkWSEngineIsAvailable() {
    String urlString = "http://"
      + this.axisConfiguration.getHost()
      + ":" + this.axisConfiguration.getPort()
      + "/" + this.axisConfiguration.getWebappName()
      + "/" + this.axisConfiguration.getServletPath()
      + "/" + this.axisConfiguration.getGetVersionSvc();
    try {
      connectURL(urlString);
    } catch (Exception e) {
      throw new JbpmException(
          "Axis web container is not started or Axis engine is not deployed",
          e);
    }

  }

  /**
   * This method will deploy the specified WS Service on the choosen ws engine.
   * This method must be overriden by each ws engine Deployer.
   *
   * @param def - Definition object that represents a WS Service to deploy.
   * @throws Exception the exception
   */
  protected void deployServices(List<Service> services) {
    QName javaURI = new QName(WSDDConstants.URI_WSDD_JAVA, WSDDBPELMsgProvider.PROVIDER_NAME);
    WSDDProvider.registerProvider(javaURI, new WSDDBPELMsgProvider());
    synchronized (getProcessQName()) {
      createTmpDir();
      try {
        File wsdl = generateWsdl(wsdlDefinition);
        wsdl2java(wsdl);
        File wsddFile = getDeployWsddFile(wsdlDefinition.getTargetNamespace());
        File newWsddFile = updateWsddFile(wsddFile);
        // remove old wsdd and rename new wsdd
        if (!wsddFile.delete()) {
          throw new JbpmException("Problem deleting old wsdd file : " + wsddFile);
        }
        if (!newWsddFile.renameTo(wsddFile)) {
          throw new JbpmException("Problem renaming new wsdd file from : " + newWsddFile + " to : " + wsddFile);
        }
        // update implementations
        List<String> writtenClasses = updateImplementationFiles(services);
        // compile java classes, create a jar and deploy it into WEB-INF/lib
        deployJarToWebServer();
        // load wsdd file into axis webapp
        callAdminClientTask(wsddFile);
      } catch (Exception e) {
        throw new JbpmException("Exception caught in " + this.getClass(), e);
      } finally {
        deleteTmpDir();
      }
    }
  }
  
  private File generateWsdl(Definition wsdlDefinition) throws WSDLException, IOException {
    WSDLFactory wsdlFactory = WSDLFactory.newInstance();
    WSDLWriter writer = wsdlFactory.newWSDLWriter();
    File wsdlFile = File.createTempFile(getProcessQName().getLocalPart(), ".wsdl", this.tmpDir);
    FileOutputStream fileOutputStream = new FileOutputStream(wsdlFile);
    writer.writeWSDL(wsdlDefinition, fileOutputStream);
    fileOutputStream.close();
    return wsdlFile;
  }

  /**
   * This method will undeploy the specified WS Service from the choosen ws engine.
   * This method must be overriden by each ws engine Deployer.
   *
   * @param def - Definition object that represents a WS Service to undeploy.
   *
   * @throws Exception the exception
   */
  protected void undeployServices(List<Service> services) {
    synchronized (getProcessQName()) {
      createTmpDir();
      try {
        //extract jar file to get undeploy service descriptor
        extractJarFile();
        File[] undeployFiles = getUndeployFiles();
        for (File undeployWsdd : undeployFiles) {
          callAdminClientTask(undeployWsdd);
        }
      } catch (Exception e) {
        throw new JbpmException("Exception caught in " + this.getClass(), e);
      } finally {
        deleteTmpDir();
      }
    }
  }

  /**
   * Extracts a jar deployed in web server to baseDir.
   * @throws Exception - exception
   */
  private void extractJarFile() throws Exception {
    String jarRepository = getJarRepository();
    File jarFile = new File(
        jarRepository
        + File.separator + getProcessQName().getLocalPart()
        + ".jar");
    Expand unjar = new Expand();
    unjar.setProject(this.antProject);
    unjar.setSrc(jarFile);
    unjar.setDest(this.tmpDir);
    unjar.setTaskName("unjar");
    unjar.execute();
  }

  /**
   * Call axis admin client with the given wsdd file to deploy/undeploy services.
   * @param wsddFile - wsdd file to use
   */
  private void callAdminClientTask(final File wsddFile) {
    AdminClientTask adminClientTask = new AdminClientTask();
    adminClientTask.setProject(this.antProject);
    adminClientTask.init();
    adminClientTask.setUrl(
        "-lhttp://" + this.axisConfiguration.getHost()
        + ":" + this.axisConfiguration.getPort()
        + "/" + this.axisConfiguration.getWebappName() + "/servlet/AxisServlet");
    adminClientTask.setXmlFile(wsddFile);
    adminClientTask.setTaskName("adminClientTask");
    adminClientTask.execute();
  }

  /**
   * Deploy axis WS jar to web server. For that :
   * - compile wsdl2java + new binding classes
   * - create the jar file
   * - deploy it in web server
   *
   * @throws Exception the exception
   */
  private void deployJarToWebServer() throws Exception {
    compileClasses();
    File jarFile = createJar();
    File destJarFile = deployJar(jarFile);
    ClassLoader parent = Axis1Deployer.class.getClassLoader();
    URL url = destJarFile.toURI().toURL();
    URL[] urls = new URL[]{url};
    ProcessClassLoader cl = new ProcessClassLoader(getProcessQName(), urls, parent);
    ClassLoaderMap.addCL(getProcessQName(), cl);
  }

  /**
   * Creates the WS jar file based on generated classes.
   * @return the created jar file
   */
  private File createJar() {
    File jarFile = getWSJarFile();
    Jar j = new Jar();
    j.setTaskName("jar");
    j.setProject(this.antProject);
    j.setDestFile(jarFile);
    FileSet fs1 = new FileSet();
    fs1.setDir(this.tmpDir);
    j.addFileset(fs1);
    j.execute();
    return jarFile;
  }

  /**
   * Deploys the given jar file to web server.
   *
   * @param jarFile the jar file
   *
   * @throws Exception the exception
   */
  private File deployJar(final File jarFile) throws Exception {
    String jarRepository = getJarRepository();
    File destJarFile = new File(
        jarRepository
        + File.separator + getProcessQName().getLocalPart()
        + ".jar");
    Copy c = new Copy();
    c.setProject(this.antProject);
    c.setFile(jarFile);
    c.setTofile(destJarFile);
    c.setTaskName("copy");
    c.execute();
    return destJarFile;
  }

  /**
   * Compiles java classes from basedir.
   */
  private void compileClasses() {
    Javac j = new Javac();
    j.setProject(this.antProject);
    j.setDestdir(this.tmpDir);
    Path srcDir = new Path(this.antProject);
    srcDir.setLocation(this.tmpDir);
    j.setSrcdir(srcDir);

    FileSet fileset = new FileSet();
    fileset.setDir(new File(getWebappLibDir()));
    fileset.setIncludes("**/*.jar");

    Path classpath = new Path(this.antProject);
    classpath.addFileset(fileset);

    j.setClasspath(classpath);


    j.setVerbose(false);
    j.setFork(true);
    j.setIncludes("**/*.java");
    j.setTaskName("javac");
    j.setSource(JDK_VERSION);
    j.setDebug(true);
    j.init();
    j.execute();
  }

  /**
   * Create new java implementations for each binding of the given definition.
   * @param def - the definition to analyse.
   * @return a list of created classes
   * @throws Exception - ex
   */
  private List<String> updateImplementationFiles(List<Service> services) throws Exception {
    List<String> writtenClasses = new ArrayList<String>();
    for (Service service : services) {
      for (Port port : (Collection<Port>)service.getPorts().values()) {
        Binding binding = port.getBinding();
        writtenClasses.add(new BindingFileWriter(this.tmpDir, binding, getProcessQName()).write());
      }
    }
    return writtenClasses;
  }

  /**
   * Updates the given wsddFile to make it using message style service.
   * @param wsddFile : wsddFile to update
   * @return the updated wsddFile
   */
  private File updateWsddFile(final File wsddFile) throws Exception {
    //parse wsdd file


    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    Document wsddDoc = documentBuilder.parse(wsddFile);

    //for each service, set style to Message
    Element deploymentElement = wsddDoc.getDocumentElement();
    NodeList services = deploymentElement.getElementsByTagName(WSDD_SERVICE);
    for (int i = 0; i < services.getLength(); i++) {
      Element service = (Element) services.item(i);

      Element processNameParameter = service.getOwnerDocument().createElement("parameter");
      processNameParameter.setAttribute("name", "bpelProcessName");
      processNameParameter.setAttribute("value", getProcessQName().getLocalPart());

      Element processNamespaceParameter = service.getOwnerDocument().createElement("parameter");
      processNamespaceParameter.setAttribute("name", "bpelProcessNamespace");
      processNamespaceParameter.setAttribute("value", getProcessQName().getNamespaceURI());

      Attr providerAttribute = service.getOwnerDocument().createAttribute(WSDD_SERVICE_PROVIDER_ATTR);
      providerAttribute.setValue("java:BPELMsg");

      service.getOwnerDocument().importNode(processNameParameter, true);
      service.getOwnerDocument().importNode(processNamespaceParameter, true);
      service.getOwnerDocument().importNode(providerAttribute, true);

      service.appendChild(processNameParameter);
      service.appendChild(processNamespaceParameter);
      service.setAttributeNode(providerAttribute);

      service.setAttribute(WSDD_SERVICE_STYLE_ATTR, MESSAGE_STYLE);
    }
    File newWsddFile = getWsddCopy(wsddFile);
    writeXmlFile(wsddDoc, newWsddFile);
    return newWsddFile;
  }

  /**
   * Return a copy of the given wsdd file.
   * @param originalWsddFile : original wsdd file
   * @return the copied wsdd file (the new created one)
   */
  private File getWsddCopy(final File originalWsddFile) {
    return new File(originalWsddFile.getAbsolutePath() + NEW_WSDD_SUFFIX);
  }

  /**
   * Return the deploy wsdd file of the given definition.
   * @param def : def to analyse
   * @return the deploy wsdd file used by the given def
   */
  private File getDeployWsddFile(String wsdlTargetnamespace) {
    return new File(getWsddFilesDir(wsdlTargetnamespace) + File.separator + WSDD_DEPLOY_FILE_NAME);
  }

  /**
   * Return the undeploy wsdd file of the given definition.
   * @param def : def to analyse
   * @return the undeploy wsdd file used by the given def
   */
  private File[] getUndeployFiles() {
    FilenameFilter filter = new UndeployFilter();
    return this.tmpDir.listFiles(filter);
  }

  private class UndeployFilter implements FilenameFilter {
      public boolean accept(File dir, String name) {
        return dir.exists() && name.equals("undeploy.wsdd");
    }
  }
  /**
   * Returns the directory where wsdd files of this definition could be found.
   * @param def : def to analyse
   * @return the directory where wsdd files of this definition could be found
   */
  private String getWsddFilesDir(String wsdlTargetnamespace) {
    String wsddFilesDir = this.tmpDir
      + File.separator
      + getDirectoryFromPackage(getPackageFromNamespace(wsdlTargetnamespace));
    return wsddFilesDir;
  }

  /**
   * Performs a wsdl2java task on the given definition.
   * @throws MalformedURLException - if the wsdl url is not correct
   */
  private void wsdl2java(File wsdl) throws MalformedURLException {
    Wsdl2javaAntTask wsdl2java = new Wsdl2javaAntTask();
    wsdl2java.setProject(this.antProject);
    wsdl2java.setTaskName("wsdl2java");
    wsdl2java.setOutput(this.tmpDir);
    wsdl2java.setURL(wsdl.toString());
    wsdl2java.setAll(true);
    wsdl2java.setHelperGen(true);
    wsdl2java.setServerSide(true);
    wsdl2java.setVerbose(true);
    wsdl2java.setNoWrapped(true);
    wsdl2java.setWrapArrays(true);
    wsdl2java.setDebug(false);
    wsdl2java.setAllowInvalidUrl(false);
    wsdl2java.setDeployScope(SCOPE);
    TypeMappingVersionEnum tmve = new TypeMappingVersionEnum();
    tmve.setValue(this.axisConfiguration.getTypeMapping());
    wsdl2java.setTypeMappingVersion(tmve);
    wsdl2java.setAll(true);
    wsdl2java.execute();
  }

  /*
   * UTILITY METHODS
   */

  /**
   * Set basedir field.
   */
  private void createTmpDir() {
    try {
      new File(System.getProperty("java.io.tmpdir")).mkdirs();
      this.tmpDir = File.createTempFile("orch", null, null);
      tmpDir.delete();
      if (!tmpDir.mkdirs()) {
        throw new IOException("Cannot create the temporary directory '" + tmpDir + "'.");
      }

    } catch (Exception e) {
      throw new JbpmException("Error creating " + this.tmpDir, e);
    }
  }

  /**
   * Returns the jar file of the current ws deployed in web server.
   * @return the jar file of the current ws deployed in web server.
   */
  private String getJarRepository() {
    File repo = new File(getWebappLibDir() + File.separator + "WS_Repo");
    repo.mkdirs();
    return repo.getAbsolutePath();
  }

  private String getWebappLibDir() {
    String dir = this.axisConfiguration.getWebappDir()
    + File.separator + this.axisConfiguration.getWebappName()
    + File.separator + "WEB-INF" + File.separator + "lib";
    return dir;
  }

  /**
   * Returns the jar file of the current ws : this jar is in basedir (not already deployed).
   * @return the jar file of the current ws : this jar is in basedir (not already deployed).
   */
  private File getWSJarFile() {
    return new File(this.tmpDir + File.separator + getProcessQName().getLocalPart() + ".jar");
  }

  /**
   * Create a connection to the given url.
   * @param urlString : url to connect
   * @return the created connection
   * @throws MalformedURLException - ex
   * @throws IOException - ex
   * @throws Exception - ex
   */
  private URLConnection connectURL(final String urlString) throws Exception {
    URL getVersionURL = new URL(urlString);
    URLConnection connection = getVersionURL.openConnection();
    try {
      connection.connect();
    } catch (Exception e) {
      throw new Exception("Unable to get a connection on URL : ", e);
    }
    return connection;
  }

  /**
   * Deletes the given directory.
   * @param dir - dir to delete.
   */
  private void deleteDir(final File dir) {
    Delete d = new Delete();
    d.setTaskName("delete");
    d.setDir(dir);
    d.setProject(this.antProject);
    d.execute();
  }

  /**
   * Deletes the given File.
   * @param f - file to delete
   */
  private void deleteFile(final File f) {
    Delete d = new Delete();
    d.setTaskName("delete");
    d.setFile(f);
    d.setProject(this.antProject);
    d.execute();
  }

  /**
   * Deletes all generated files of this ws.
   * @throws Exception - ex
   */
  private void deleteTmpDir() {
    try {
      //deleteDir(this.tmpDir);
    } catch (Exception e) {
      throw new JbpmException(this.getClass() + ".deleteGeneratedFiles, unable to delete directory : " + this.tmpDir, e);
    }
  }

  /**
   * Set the used ant project.
   *
   */
  private void setAntProject() {
    this.antProject = new Project();

    //Class to Write build events to a PrintStream
    DefaultLogger dl = new DefaultLogger();
    dl.setMessageOutputLevel(Project.MSG_INFO);
    dl.setErrorPrintStream(System.out);
    dl.setOutputPrintStream(System.out);
    dl.setEmacsMode(false);
    this.antProject.addBuildListener(dl);

    //Initialise the project. This involves setting the default
    //task definitions and loading the system properties.
    this.antProject.init();

    Property p = new Property();
    p.setEnvironment(ANT_ENV);
    p.setProject(this.antProject);
    p.execute();


  }

  /**
   * This method writes a DOM document to a file.
   * @param doc - doc to write
   * @param file - dest file;
   */
  private static void writeXmlFile(final Document doc, final File file) {
    try {
      // Prepare the DOM document for writing
      Source source = new DOMSource(doc);
      Result result = new StreamResult("file://" + file.getAbsolutePath());
      // Write the DOM document to the file
      Transformer xformer = TransformerFactory.newInstance().newTransformer();
      xformer.transform(source, result);
    } catch (TransformerConfigurationException e) {
      e.printStackTrace();
    } catch (TransformerException e) {
      e.printStackTrace();
    }
  }

  /**
   * Returns the java package that maps to the given namespace.
   * @param ns - ns
   * @return the java package that maps to the given namespace.
   */
  public static String getPackageFromNamespace(final String ns) {
    String packag = Utils.makePackageName(ns);
    return packag;
  }

  /**
   * Returns the directory structure corresponding to the given package.
   * @param packag - packag
   * @return the directory structure corresponding to the given package.
   */
  public static String getDirectoryFromPackage(final String packag) {
    String dir = packag.replace('.', File.separatorChar);
    return dir;
  }

  /**
   * Set the proxy (if needed) from the Axis configuration.
   */
  private void setProxy() {
    System.getProperties().put("http.proxyHost", this.axisConfiguration.getHttpProxyHost());
    System.getProperties().put("http.proxyPort", this.axisConfiguration.getHttpProxyPort());
    System.getProperties().put("http.nonProxyHosts", this.axisConfiguration.getHttpNonProxyHosts());
  }
}
