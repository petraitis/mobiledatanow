package com.framedobjects.dashwell.tests.GoogleSearch;

public class LateBindingGlue {
	public Document serviceInvocationGlue() 
	throws Throwable { 
		String wsdlName = "http://?../Service.wsdl?WSDL"; 
		String operation = "??.."; 
		String args[] = {  }; 
		/* first, Glue creates a SOAP interceptor */ 
		SOAPInterceptor responseHandler = new SOAPInterceptor(); 
		/* second, Glue registers the SOAP interceptor to catch incoming responses */ 
		ApplicationContext.addInboundSoapResponseInterceptor( 
		(ISOAPInterceptor)responseHandler ); 
		try { 
		/* third, Glue gets a proxy to the Web service through its WSDL */ 
		IProxy proxy = Registry.bind( wsdlName ); 
		/* here, service's operation is invoked through a proxy */ 
		proxy.invoke( operation, args ); 
		} catch( java.rmi.UnmarshalException e ) { 
	//	 Glue is catching the UnmarshalException that is perfectly expected 
		} 
		/* forth, Glue generates an XML document containing the SOAP body, */
		/* and passes the whole document for parsing  */ 
		 return new Document( responseHandler.getResponse() ); 
	} 
}
