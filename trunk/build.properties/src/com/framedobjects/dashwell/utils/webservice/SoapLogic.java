package com.framedobjects.dashwell.utils.webservice;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.stream.StreamSource;

import org.apache.axis.AxisFault;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.client.Stub;
import org.apache.axis.message.SOAPEnvelope;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import wsl.fw.exception.MdnException;

/**
 * This class provides the methods for the controller to
 * interact with the model.
 * It provides the methods to 
 * interact on SOAP protocol level
 * for the editor mode.
 * 
 *  
 */
public class SoapLogic extends Wsdl2Java{
  private javax.xml.soap.SOAPEnvelope m_soapMessage;
  private javax.xml.soap.SOAPEnvelope m_soapResponse;
  
  /**
   * Constructs a new SoapLogic instance for the web service described
   * by the specified WSDL URL. This instance does not support ssl.
   * 
   * @param wsdlUrl The URL of the WSDL document.
   * @throws MdnException
   */
  public SoapLogic(String wsdlUrl) throws MdnException{
    super(wsdlUrl);
    /* generate the SOAP message */
    createSoapMessage();    
  }
  

  public void setCurrentOperation(String operation) throws MdnException{
    super.setCurrentOperation(operation);
    createSoapMessage();
  }
  
  public void setCurrentPort(String port) throws MdnException{
    super.setCurrentPort(port);
    createSoapMessage();
  }
  
  public void setCurrentService(String service) throws MdnException{
    super.setCurrentService(service);
    createSoapMessage();
  }
  
  public javax.xml.soap.SOAPEnvelope getSoapMessage() {
    return m_soapMessage;
  }
  
  public javax.xml.soap.SOAPEnvelope getSoapResponse(){
    return m_soapResponse;
  }
  
  public void importSoapMessage(String filename) throws MdnException{
    try {
      StringBuffer msg = new StringBuffer();
      BufferedReader in = new BufferedReader(new FileReader(filename));
      String buf;
      while ((buf = in.readLine()) != null){
        msg.append(buf);
        msg.append("\n");
      }
      
      MessageFactory messageFactory = MessageFactory.newInstance();
      SOAPMessage request = messageFactory.createMessage();
      request.getSOAPHeader().detachNode();
      
      StreamSource domSource = new StreamSource(new ByteArrayInputStream(msg.toString().getBytes()));
      request.getSOAPPart().setContent(domSource);
      //normalizeDomNode(request.getSOAPPart());
      
      m_soapMessage = request.getSOAPPart().getEnvelope();
      
      // normalize here instead, as if the document is not well formed
      // we will get a soapexception from getenvelope, not a not so 
      // informative domexception from normalizeDomNode()
      m_jMdnFile.normalizeDomNode(m_soapMessage);
      
      System.out.println("Imported SOAP message from " + filename);
    }
    catch(SOAPException e){
      throw new MdnException("error.SOAPException", e);
    }
    catch (FileNotFoundException e) {
      throw new MdnException("error.FileNotFoundException", e);
    }
    catch (IOException e) {
      throw new MdnException("error.IOException", e);
    }
  }
  
  public void exportSoapMessage(String filename, String soap_msg) throws MdnException{
    try {
      PrintWriter out = new PrintWriter(new FileWriter(filename, false));
      out.print(soap_msg);
      out.close();
      
      System.out.println("exported SOAP message to " + filename);
    }
    catch (IOException e) {
      throw new MdnException("error.IOException", e);
    }
  }
  
  public String insertXmlDocument(String url) throws MdnException{
    try{
      MessageFactory messageFactory = MessageFactory.newInstance();
      SOAPMessage request = messageFactory.createMessage();
      request.getSOAPHeader().detachNode();
      
      DocumentBuilderFactory factory = 
        DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      //factory.setValidating(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(url);
      
      m_jMdnFile.normalizeDomNode(request.getSOAPPart());
      
      javax.xml.soap.SOAPEnvelope envelope = request.getSOAPPart().getEnvelope();
      envelope = m_soapMessage;
      request.getSOAPBody().addDocument(doc);
      
      return request.getSOAPPart().getEnvelope().toString();
    }
    catch(SOAPException e){
      throw new MdnException("error.SOAPException", e);
    }
    catch(ParserConfigurationException e){
      throw new MdnException("error.ParserConfigurationException", e);
    }
    catch(SAXException e){
      throw new MdnException("error.SAXException", e);
    }
    catch (IOException e) {
      throw new MdnException("error.IOException", e);
    }
  }

  /**
   * Sends a request to the web service (invokes the web service).
   * 
   * @param soapMessage the message to be sent.
   * @param attachments the paths of the attachments for the request.
   * @return the response SOAP message.
   * @throws MdnException
   */
  public SOAPEnvelope invoke(String soapMessage, String[] attachments) throws MdnException{
    try{
      
      /* create a SOAP message */
      MessageFactory messageFactory = MessageFactory.newInstance();
      SOAPMessage request = messageFactory.createMessage();
      request.getSOAPHeader().detachNode();
      
      StreamSource domSource = new StreamSource(new ByteArrayInputStream(soapMessage.getBytes()));
      request.getSOAPPart().setContent(domSource);
      m_jMdnFile.normalizeDomNode(request.getSOAPPart());
      
      System.out.println("Sending Soap Message: " + request.getSOAPPart().getEnvelope());
      
      /* invoke the service */
      Call call = ((Stub)m_currPort.portInstance)._getCall(); // get the call object containing the correct soapaction
      //call.setRequestMessage((Message)request);
      
      // TODO add attachments
      /*
      if (attachments != null)
        for (String attachment : attachments){
          DataHandler handler = new DataHandler(new FileDataSource(new File(attachment)));
          AttachmentPart attachment_part = request.createAttachmentPart(handler);
          call.addAttachmentPart(attachment_part);
        }
      */
      
      SOAPEnvelope responseEnvelope = call.invoke((SOAPEnvelope)request.getSOAPPart().getEnvelope());
      
      System.out.println("Response Soap Message: " + responseEnvelope);
      m_soapResponse = responseEnvelope;
      return responseEnvelope;
      
    }
    catch(SOAPException e){
      throw new MdnException("error.SOAPException", e);
    }
    catch(AxisFault e){
      throw new MdnException("error.AxisFault", e);
    }
  }
  
  /**
   * Creates a default Soap Message that can be edited and sent
   * to the Web Service.
   * 
   * @throws MdnException
   */
  private void createSoapMessage() throws MdnException{
    List<HandlerInfo> handlerchain = null;
    try{
    	System.out.println("Creating Soap Message...");
      
      Class portClass = m_currPort.portClass;
  
      Method method = portClass.getMethod(m_currOperation.name, m_currOperation.arguments);
      Service serviceLocator = m_currService.serviceLocator;
      System.out.println(method.toString());
      
      /* add the soaphandler */
      handlerchain = 
        serviceLocator.getHandlerRegistry().getHandlerChain(m_currPort.qName);
      HandlerInfo hi = new HandlerInfo(SoapHandler.class, /*handlerConfig*/null, null);
      handlerchain.add(hi);
      
      /* invoke web service */    
      Object[] args = m_classHelper.constructArguments(m_currOperation.arguments);
      method.invoke(m_currPort.portInstance, args);
    }
    catch(NoSuchMethodException e){
      throw new MdnException("error.NoSuchMethodException", e);
    }
    catch(IllegalAccessException e){
      throw new MdnException("error.IllegalAccessException", e);
    }
    catch(InvocationTargetException e){
      /* don't rethrow, as we want to carry on if the response messages
       * is null, as we don't care about the response at this point.
       * Only rethrow if the soapMessage from the handler is null, as in that case
       * we really have a serious invocationtargetexception
       */
      if (SoapHandler.soapMessage == null)
        throw new MdnException("error.InvocationTargetException", e);
    }
    catch(IllegalArgumentException e){
      throw new MdnException("error.IllegalArgumentException", e);
    }

    m_soapMessage = SoapHandler.soapMessage;
    
    /* clear the handlerchain (else the message would be sent later on) */
    handlerchain.clear();
  }

}
