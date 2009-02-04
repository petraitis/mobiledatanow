package com.framedobjects.dashwell.utils.webservice;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.Handler;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.soap.SOAPEnvelope;

import org.apache.log4j.Logger;

/**
 * This class implements a handler that is used to intercept the SOAP messages
 * that travel between the axis engine and the server. Its purpose is to extract the
 * request SOAP message from an RPC request.
 * 
 */
public class SoapHandler implements Handler {
  
  private static final Logger LOGGER = Logger.getLogger(SoapHandler.class);
  
  public static SOAPEnvelope soapMessage;
  
  /* (non-Javadoc)
   * @see javax.xml.rpc.handler.Handler#handleRequest(javax.xml.rpc.handler.MessageContext)
   */
  public boolean handleRequest(MessageContext context) {
    SOAPMessageContext msgContext = (SOAPMessageContext)context;
    /* get SOAP message */
    try{
      // get the whole SOAP message (including envelope)
      soapMessage = msgContext.getMessage().getSOAPPart().getEnvelope();
      
      LOGGER.info("Request Soap Message: " + soapMessage);
    }
    catch(Exception e){ e.printStackTrace(); }

    return false;
  }

  /* (non-Javadoc)
   * @see javax.xml.rpc.handler.Handler#handleResponse(javax.xml.rpc.handler.MessageContext)
   */
  public boolean handleResponse(MessageContext context) {
    org.apache.axis.MessageContext msg = (org.apache.axis.MessageContext)context;
    try{
      javax.xml.soap.SOAPBody body = msg.getMessage().getSOAPBody();
      LOGGER.info("Response Soap Message: " + body);
    }
    catch(Exception e){}
    return true;
  }

  /* (non-Javadoc)
   * @see javax.xml.rpc.handler.Handler#handleFault(javax.xml.rpc.handler.MessageContext)
   */
  public boolean handleFault(MessageContext context) {
    return true;
  }

  /* (non-Javadoc)
   * @see javax.xml.rpc.handler.Handler#init(javax.xml.rpc.handler.HandlerInfo)
   */
  public void init(HandlerInfo config) {
  }

  /* (non-Javadoc)
   * @see javax.xml.rpc.handler.Handler#destroy()
   */
  public void destroy() {
  }

  /* (non-Javadoc)
   * @see javax.xml.rpc.handler.Handler#getHeaders()
   */
  public QName[] getHeaders() {
    return null;
  }

}
