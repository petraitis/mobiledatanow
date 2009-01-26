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

import wsl.fw.datasource.DataSourceException;
import wsl.fw.exception.MdnException;
import wsl.fw.resource.ResId;
import wsl.mdn.common.MdnDataManager;
import wsl.mdn.dataview.DataViewDataSource;
import wsl.mdn.dataview.QueryDobj;

import com.framedobjects.dashwell.db.DataAgentFactory;
import com.framedobjects.dashwell.db.IDataAgent;

public class InsertQueryTest {
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

		String queryID = "121";
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		QueryDobj query = null;
		boolean success = false;
		try {
			query = dataAgent.getQueryByID(Integer.parseInt(queryID));
			
	        // get the view ds
	        DataViewDataSource dvds = MdnDataManager.getDataViewDS();
	        
	        if (query != null && query.isComplete(null))
            	dvds.execInsertOrUpdate(query);	        
	        
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			success = false;
		} catch (MdnException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			success = false;
		} catch (DataSourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			success = false;
		}
		String result = null;
		if (success){
			result = "OK";
		}
		else{
			result = "Failed";
		}
	}

}
