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

import java.util.ArrayList;

import wsl.fw.datasource.RecordSet;
import wsl.fw.resource.ResId;

import com.framedobjects.dashwell.db.DataAgentFactory;
import com.framedobjects.dashwell.db.IDataAgent;

public class QueryUserInputTest {
	public static final ResId
	ERR_LICENSE1	= new ResId ("MdnAdminApp.err.license1"),
	ERR_LICENSE2	= new ResId ("MdnAdminApp.err.license2"),
	TEXT_STARTING	= new ResId ("MdnAdminApp.text.starting"),
	TEXT_VERSION	= new ResId ("mdn.versionText"),
	ERR_VERSION1	= new ResId ("MdnAdminApp.err.version1"),
	ERR_VERSION2	= new ResId ("MdnAdminApp.err.version2"),
	ERR_WILL_EXIT	= new ResId ("MdnAdminApp.err.willExit"),
	ERR_NO_SERVER	= new ResId ("MdnAdminApp.err.noServer");	


	/**
	 * @param args
	 */
	public static void main(String[] args) {

		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		ArrayList<String> userInputs = new ArrayList<String>();
		//userInputs.add("test");
		//userInputs.add("53");
		userInputs.add("1");
		userInputs.add("Beverages");
		userInputs.add("Soft drinks, coffees, teas, beers, and ales");
		//userInputs.add("1");
		//userInputs.add("1");
		//userInputs.add("false");
		
		//FIXME method signature doesn't even vaguely match up :(
//		RecordSet rs = dataAgent.getSelectQueryResultWithUserInput(null, null, "4", "149", userInputs, 0);
//		System.out.println("RecordSet: " + rs.size());
//		if (rs.next()){
//			System.out.println(rs.getRows());
//		}
		
//		dataAgent.getUpdateQueryResultWithUserInput("4", "130", userInputs);
		
//		dataAgent.getInsertQueryResultWithUserInput("4", "131", userInputs);
	}

}
