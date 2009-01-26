package com.framedobjects.dashwell.tests.GoogleSearch;

import javax.naming.Context;
import javax.xml.soap.SOAPMessage;

public class SOAPInterceptor {
	private Element soapBody; 
	public void intercept( SOAPMessage message, Context messageContext ) { 
		try { 
		soapBody = message.getBody(); 
		} 
		catch( Exception e ){ 
		 System.err.println( e.toString()); 
		} 
	} 
	public Element getResponse(){ 
		return soapBody; 
	} 
}
