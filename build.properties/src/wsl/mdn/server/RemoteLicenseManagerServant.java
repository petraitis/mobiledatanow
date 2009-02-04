//==============================================================================
// RemoteLicenseManagerServant.java
// Copyright (c) 2001 WAP Solutions Ltd.
//==============================================================================

package wsl.mdn.server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.JOptionPane;
import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.exolab.castor.net.util.URIResolverImpl;
import org.exolab.castor.xml.schema.ComplexType;
import org.exolab.castor.xml.schema.ContentModelGroup;
import org.exolab.castor.xml.schema.ElementDecl;
import org.exolab.castor.xml.schema.Group;
import org.exolab.castor.xml.schema.Schema;
import org.exolab.castor.xml.schema.Structure;
import org.exolab.castor.xml.schema.reader.SchemaReader;
import org.jdom.Content;
import org.jdom.Namespace;
import org.jdom.input.DOMBuilder;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.framedobjects.dashwell.utils.webservice.ParamHelper;
import com.framedobjects.dashwell.utils.webservice.ParamListItem;
import com.framedobjects.dashwell.utils.webservice.WebServiceDefinitionHelper;
import com.framedobjects.dashwell.utils.webservice.Wsdl2Java;


import wsl.fw.datasource.ClassPathHacker;
import wsl.fw.exception.MdnException;
import wsl.fw.remote.RmiServantBase;
import wsl.fw.util.Config;
import wsl.fw.util.Log;
import wsl.fw.util.Util;
import wsl.fw.util.Validate;
import wsl.licence.ActivationKey;
import wsl.licence.LicenceKey;
import wsl.mdn.common.MdnAdminConst;
import wsl.mdn.licence.LicenseRemoteCallManager;
import wsl.mdn.licence.MdnLicenceManager;
import wsl.mdn.licence.MdnStore;
import wsl.mdn.dataview.ResultWrapper;
import wsl.mdn.dataview.UserLicense;
import wsl.mdn.dataview.UserLicenses;
import wsl.mdn.dataview.WebServiceDetail;

//------------------------------------------------------------------------------
/**
 * RMI servant for license management.
 * Implements the RemoteLicenseManager functions and also maintains a daemon
 * thread to remove expired locks.
 */
public class RemoteLicenseManagerServant
    extends RmiServantBase
    implements RemoteLicenseManager, Runnable
{
    // attributes
    private HashMap       	_locks = new HashMap();
    private long          	_lockTimeout;
    private long          	_lockSweepInterval;
    private ActivationKey 	_activationKey = null;
    private LicenceKey	  	_licenceKey = null;
    private boolean       	_bInvalid = false;
    private String 			_registeredEmailAddress = null;
    private Boolean			_publicGroup = null;
    private int 			_availablePublicMessages = 0;
    private int 			_installationReferenceNumber = 0;

    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public RemoteLicenseManagerServant()
    {
        // get config data
        _lockTimeout       = Config.getProp(MdnAdminConst.LICENSE_MANAGER_LICENSETIMEOUT, 5*60*1000);
        _lockSweepInterval = Config.getProp(MdnAdminConst.LICENSE_MANAGER_CLEANUPPERIOD, 2*60*1000);

        // load the license information
        try
        {
            // load license key
            LicenceKey lk = MdnLicenceManager.getLicenceKey(MdnLicenceManager.PROD_CODE_MDN_SERVER);
            
            if (lk != null)
            {
                // check if it is valid
                if (MdnLicenceManager.isValidLicenceKey(lk))
                {
                    _licenceKey = lk;
                	// get the activation key
                    _activationKey = MdnLicenceManager.getActivationKey(lk);
                    
                    _registeredEmailAddress = MdnLicenceManager.getRegisterdEmailAddress();
                    
                    _publicGroup = MdnLicenceManager.getPublicGroup();
                    
                    _availablePublicMessages = MdnLicenceManager.getAvailablePublicMessages();
                    
                    _installationReferenceNumber = MdnLicenceManager.getInstallationReferenceNumber();
                }
                else
                {
                    // set invalid flag
                    _bInvalid = true;
                }
            }
        }
        catch (Exception e)
        {
            Log.fatal("RemoteLicenseManagerServant ctor (): ", e);
        }

        // start cleanup thread as a daemon
        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();
    }

    //--------------------------------------------------------------------------
    /**
     * Check if the license is valid. Return values >= 0 are success (i.e. the
     * license is valis and the application may continue. Return values < 0
     * are a failure.
     * @return one of the above RemoteLicenseManager.LICENSE_ constants.
     * @throws RemoteException if there is an RMI error.
     */
    public int isLicenseValid() throws RemoteException
    {
        // invalid if no activation key
        if (_activationKey == null)
            return (_bInvalid) ? LICENSE_INVALID : LICENSE_NOT_SET;
        else
        {
            // get the expiry date as an int
            int expiry = _activationKey.getExpiry();

            // valid full license if zero
            if (expiry == 0)
                return LICENSE_VALID;
            else
            {
                // get current date
                Date now = new Date();

                // convert expiry int to a date
                int y = (expiry / 10000) % 10000;
                int m = (expiry / 100) % 100;
                int d = expiry % 100;
                Calendar expiryCal = Calendar.getInstance();
                expiryCal.set(y, m - 1, d);

                Date expiryDate = expiryCal.getTime();
                if  (now.after(expiryDate))
                    return LICENSE_EXPIRED;
                else{
                	int daysLeft = calculateDifference(expiryDate, now);
                    /*
                	//Hack, if expire date is today, then show one more day to expire
                	//Because 0 means permanent
                	if (daysLeft == 0){
                		daysLeft = 1;
                	}*/
                	//plus 1 day (including both boundary date)
                	daysLeft = daysLeft + 1;
                	
                	return daysLeft;//LICENSE_EVALUATION;
                }
            } 
        }
    }
    
    public Boolean getPublicGroupFlag(){
    	return _publicGroup;
    }
    
    public String getRegisterdEmailAddress(){
    	return _registeredEmailAddress;
    }
    
    private static int calculateDifference(Date a, Date b)
    {
        int tempDifference = 0;
        int difference = 0;
        Calendar earlier = Calendar.getInstance();
        Calendar later = Calendar.getInstance();
     
        if (a.compareTo(b) < 0)
        {
            earlier.setTime(a);
            later.setTime(b);
        }
        else
        {
            earlier.setTime(b);
            later.setTime(a);
        }
     
        while (earlier.get(Calendar.YEAR) != later.get(Calendar.YEAR))
        {
            tempDifference = 365 * (later.get(Calendar.YEAR) - earlier.get(Calendar.YEAR));
            difference += tempDifference;
     
            earlier.add(Calendar.DAY_OF_YEAR, tempDifference);
        }
     
        if (earlier.get(Calendar.DAY_OF_YEAR) != later.get(Calendar.DAY_OF_YEAR))
        {
            tempDifference = later.get(Calendar.DAY_OF_YEAR) - earlier.get(Calendar.DAY_OF_YEAR);
            difference += tempDifference;
        }
     
        return difference;
    }
    /**
     * get Number Of User License
     */
    public int getNumberOfUserLicense() throws RemoteException
    {
        // invalid if no activation key
        if (_activationKey == null)
            return (_bInvalid) ? LICENSE_INVALID : LICENSE_NOT_SET;
        else
        {
            // get the number of user license as an int
            int numUsers = _activationKey.getNumUsers();
            return numUsers;
        }
    }
	public int getAvailablePublicMessages() {
		return _availablePublicMessages;
	}  
    //--------------------------------------------------------------------------
    /**
     * Get a lock to use a license counted resource.
     * Used by servlet to limit the number of concurrent users.
     * The owner of the lock should release it when finished (i.e. when the
     * user logs out). To avoid lockout due to misbehaving clients the server
     * automaticly times out inactive locks after a given period.
     * @param key, a unique key that identifies the client, such as an
     *   object hash, session id etc. A given client should ensure it keeps
     *   using the same key. May not be null.
     * @return >=0 (LICENSE_LOCK_GRANTED) if a lock could be acquired (or was
     *   already owned) for this key, < 0 if a lock was not granted.
     *   If there are no free locks then the failure value will be
     *   LICENSE_LOCK_NOT_GRANTED otherwise it will be one of the failure codes
     *   for isLicenseValid.
     * @throws RemoteException if there is an RMI error.
     */
    public int getLicenseLock(String key) throws RemoteException
    {
        Util.argCheckNull(key);

        // must have a valid license
        int rv = isLicenseValid();
        if (rv < 0)
        {
            Log.warning("RemoteLicenseManagerServant.getLicenseLock invalid ["
                + _locks.size() + '/' + getMaxLicenseLocks() + ']' );
            return rv;
        }
        else
            synchronized (_locks)
            {
                if (_locks.get(key) != null || _locks.size() < getMaxLicenseLocks())
                {
                    // success, already have the lock or there is space to assign
                    // another lock. update the timeout and ert true

                    _locks.put(key, new Long(System.currentTimeMillis()));

                    //Log.debug("RemoteLicenseManagerServant.getLicenseLock granted ["
                    //    + _locks.size() + '/' + getMaxLicenseLocks() + ']' );
                    return LICENSE_LOCK_GRANTED;
                }
                else
                {
                    Log.warning("RemoteLicenseManagerServant.getLicenseLock declined ["
                        + _locks.size() + '/' + getMaxLicenseLocks() + ']' );
                    return LICENSE_LOCK_NOT_GRANTED;
                }
            }
    }

    //--------------------------------------------------------------------------
    /**
     * Release a lock.
     * @param key, the key previously used to acquire the lock.
     * @throws RemoteException if there is an RMI error.
     */
    public void releaseLicenseLock(String key) throws RemoteException
    {
        Log.debug("RemoteLicenseManagerServant.releaseLicenseLock ["
            + _locks.size() + '/' + getMaxLicenseLocks() + ']' );

        if (key != null)
            synchronized (_locks)
            {
                _locks.remove(key);
            }
    }

    //--------------------------------------------------------------------------
    /**
     * Get the maximum number of license locks.
     */
    private int getMaxLicenseLocks()
    {
        if (_activationKey == null)
            return 0;
        else
            return _activationKey.getNumUsers();
    }

    //--------------------------------------------------------------------------
    /**
     * Runnable so we can start a separate cleanup thread.
     */
    public void run()
    {
        try
        {
            // loop
            while (true)
            {
                // wait a bit
                Thread.sleep(_lockSweepInterval);

                // do the cleanup
                cleanup();
            }
        }
        catch (InterruptedException e)
        {
            // do nothing
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Clean up any expired locks.
     */
    private void cleanup()
    {
        Vector deleteKeys = new Vector();

        long now = System.currentTimeMillis();

        synchronized (_locks)
        {
            // iterate over locks
            Iterator iter = _locks.keySet().iterator();

            while (iter.hasNext())
            {
                // get the lock key and timeout time
                Object key = iter.next();
                Long timeout = (Long) _locks.get(key);

                // if the timeout time has passed then flag for deletion
                if (now > (timeout.longValue() + _lockTimeout))
                    deleteKeys.add(key);
            }
        }

        // now delete the locks flagged for deletion
        for (int i = 0; i < deleteKeys.size(); i++)
            try
            {
                releaseLicenseLock((String) deleteKeys.get(i));
            }
            catch (Exception e)
            {
                // do nothing as the callis local and we won't get
                // RemoteExceptions
            }
    }

    //--------------------------------------------------------------------------
    /**
     * Get the version number of the MDN Server.
     * @return the version number as a string.
     * @throws RemoteException if there is an RMI error.
     */
    public String getServerVersion() throws RemoteException
    {
        return MdnServer.getVersionNumber();
    }
    public ResultWrapper updatePublicMessages(int numberSent){
    	if (_registeredEmailAddress == null || _registeredEmailAddress.isEmpty()){
    		return new ResultWrapper(null, "Please enter the registered email address.");
    	}   
    	LicenceKey lk = null;
    	String licenceKey = null;
    	try {
			lk = MdnLicenceManager.getLicenceKey(MdnLicenceManager.PROD_CODE_MDN_SERVER);
		
			if (lk == null){
	        	return new ResultWrapper(null, "No license key. Cannot continue.");
	        }else{
	        	if (MdnLicenceManager.isValidLicenceKey(lk)){
	        		licenceKey = lk.toString();
	            }else{
	            	return new ResultWrapper(null, "Invalid license key. Cannot continue.");
	            }
	        	
	        }
    	} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return new ResultWrapper(null, "Invalid license key. Cannot continue.");
		}
    	
    	String msg = "";
		try {
			URL u = new URL("https://api.mobiledatanow.com/updatePublicMessages?id=" + _registeredEmailAddress + "&license_key=" + licenceKey + "&number_sent=" + numberSent);
			String data = "id=" + _registeredEmailAddress + "&license_key=" + licenceKey + "&number_sent=" + numberSent;
			
			System.out.println(u.toString());
			
			HttpURLConnection uc = (HttpURLConnection) u.openConnection();
			uc.setDoOutput(true);
			uc.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
			
			PrintWriter pw = new PrintWriter(uc.getOutputStream());
			pw.write(data);
			pw.flush();
			pw.close();
			
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document        doc        = docBuilder.parse(uc.getInputStream());
			Element element = doc.getDocumentElement();
			String status = element.getAttribute("status");
			if (status.equalsIgnoreCase("ok")){
				msg = element.getFirstChild().getTextContent();
				System.out.println("successful: " + msg);		         		         
		        //return new ResultWrapper(null, msg);
			}
			else {//if (status.equalsIgnoreCase("failed")){
				msg = element.getFirstChild().getTextContent();
				System.out.println("failed: " + msg);		         
		         
		        return new ResultWrapper(null, msg);
			}			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResultWrapper(null, e.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResultWrapper(null, e.toString());
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResultWrapper(null, e.toString());
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResultWrapper(null, e.toString());
		}		    	
    	return null;
    }    
    public ResultWrapper updateUserLicense() throws RemoteException
    {
        /*if (_licenceKey == null){
        	return new ResultWrapper(null, "No license key. Cannot continue.");
        }*/
    	
    	if (_registeredEmailAddress == null || _registeredEmailAddress.isEmpty()){
    		return new ResultWrapper(null, "Please enter the registered email address.");
    	}
    	
    	LicenceKey lk = null;
    	String licenceKey = null;
    	try {
			lk = MdnLicenceManager.getLicenceKey(MdnLicenceManager.PROD_CODE_MDN_SERVER);
		
			if (lk == null){
	        	return new ResultWrapper(null, "No license key. Cannot continue.");
	        }else{
	        	if (MdnLicenceManager.isValidLicenceKey(lk)){
	        		licenceKey = lk.toString();
	            }else{
	            	return new ResultWrapper(null, "Invalid license key. Cannot continue.");
	            }
	        	
	        }
    	} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return new ResultWrapper(null, "Invalid license key. Cannot continue.");
		}
    	
    	// make sure all compulsory fields are valid
        //ResultWrapper ret = null;
        String msg = "";

        String activationKey = null;
        //Short numUsers = null;
        //int expiryDate = 0;
        String generatedBefore = null;
        String strPublicGroup = null;
        try {
			URL u = new URL("https://api.mobiledatanow.com/getActivationKey?id=" + _registeredEmailAddress + "&license_key=" + licenceKey);
        	//URL u = new URL("http://api.mdn.encode.net.nz/getActivationKey?id=wilbuick@gmail.com&license_key=1234567890");
			String data = "id=" + _registeredEmailAddress + "&license_key=" + licenceKey;
			
			System.out.println(u.toString());
			
        	HttpURLConnection uc = (HttpURLConnection) u.openConnection();
			uc.setDoOutput(true);
			//uc.setDoInput(true);
			uc.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
			
			//String requestXMLString = "POST " + u.getPath() + "?" + data + " HTTP/1.0\r\n" + "Content-Type: application/x-www-form-urlencoded\r\n" + "\r\n" ;
			//System.out.println(requestXMLString);
			
			PrintWriter pw = new PrintWriter(uc.getOutputStream());
            //pw.write(requestXMLString);
            pw.write(data);
			pw.flush();
            pw.close();					

        	/*BufferedReader response = new BufferedReader(new InputStreamReader(uc.getInputStream()));


        	StringBuffer resultStringBuffer = new StringBuffer();
            String inputLine = ""; 

            while ((inputLine = response.readLine()) != null) {

                     resultStringBuffer.append(inputLine);

            }
            String resultString = resultStringBuffer.toString();

            System.out.println("result:" + resultString);*/            
            
        	//BufferedInputStream bis = new BufferedInputStream(uc.getInputStream());
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document        doc        = docBuilder.parse(uc.getInputStream());
			//System.out.prinln(doc.toString()) is misleading appears empty
			Element element = doc.getDocumentElement();
			String status = element.getAttribute("status");
			if (status.equalsIgnoreCase("ok")){
				NodeList list = element.getChildNodes();
				
				
				 
				for (int i = 0; i < list.getLength(); i++){
					Node node = list.item(i);
					String nodeName = node.getNodeName();
					String nodeValue = node.getTextContent();
					System.out.println( nodeName + " " + nodeValue);
					if (nodeName.equalsIgnoreCase("activation_key")){
						activationKey = nodeValue;
						
					}
					else if (nodeName.equalsIgnoreCase("number_of_users")){
						//numUsers = Short.valueOf(nodeValue);
						
					}
					else if (nodeName.equalsIgnoreCase("expiry_date")){
						//expiryDate = Integer.valueOf(nodeValue);
						
					}else if (nodeName.equalsIgnoreCase("activation_key_previously_generated")){
						generatedBefore = nodeValue;
					}else if (nodeName.equalsIgnoreCase("public_group")){
						strPublicGroup = nodeValue;
						_publicGroup = nodeValue.equalsIgnoreCase("true")? true: false;
					}else if (nodeName.equalsIgnoreCase("available_public_messages")){
						try {
							_availablePublicMessages = !nodeValue.isEmpty() ? Integer.parseInt(nodeValue) : 0;
						} catch (NumberFormatException e) {
							_availablePublicMessages = 0;
						}
					}else if (nodeName.equalsIgnoreCase("id")){
						_registeredEmailAddress = nodeValue;
					}else if (nodeName.equalsIgnoreCase("installation_reference_number")){
						_installationReferenceNumber = !nodeValue.isEmpty() ? Integer.parseInt(nodeValue) : 0;
					}
				}
				System.out.println("activationKey: " + activationKey);
			}
			else {//if (status.equalsIgnoreCase("failed")){
				msg = element.getFirstChild().getTextContent();
				System.out.println("failed: " + msg);		         
		         
		        return new ResultWrapper(null, msg);
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResultWrapper(null, e.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResultWrapper(null, e.toString());
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResultWrapper(null, e.toString());
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResultWrapper(null, e.toString());
		}
		
		if (generatedBefore == null || !generatedBefore.equalsIgnoreCase("yes")){
            msg = "This is not original installation.  Cannot update.";
            //ret = false;
            return new ResultWrapper(null, msg);			
		}
			
        if (activationKey == null || activationKey.length() < ActivationKey.ENCODED_LENGTH)
        {
            msg = "Incomplete activation key.  Cannot continue.";
            //ret = false;
            return new ResultWrapper(null, msg);
        }
        else
        {
            try
            {
                ActivationKey aKey = new ActivationKey(activationKey, licenceKey);
                if (!aKey.verifyChecksum())
                {
                    msg = "Invalid activation key checksum.";
                    //ret = false;
                    return new ResultWrapper(null, msg);
                }
                _activationKey = aKey;

                if (aKey != null)
                {
                    try
                    {
                        MdnStore store = MdnLicenceManager.getStore();
                        store.setRegisteredEmailAddress(_registeredEmailAddress);
                        store.setActivationKey(lk, aKey);
                        Boolean publicGroup = null;
                        if (strPublicGroup.equalsIgnoreCase("true")){
                        	publicGroup = new Boolean("true");
                        }else{
                        	publicGroup = new Boolean("false");
                        }
                        store.setPublicGroup(publicGroup);
                        store.setAvailablePublicMessages(_availablePublicMessages);
                        store.setInstallationReferenceNumber(_installationReferenceNumber);
                        store.store();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    	System.out.println(e.toString());
                        return new ResultWrapper(null, e.toString());
                    }
                }                
                
                return new ResultWrapper(aKey, null);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            	msg = "Corrupt activation key.";// + e.toString();
                //ret = false;
                return new ResultWrapper(null, msg);
            }

        }
    }    
    public ResultWrapper changeEmailAddress(String newUserID) throws RemoteException
    {
        /*if (_licenceKey == null){
        	return new ResultWrapper(null, "No license key. Cannot continue.");
        }*/
		// get the old master id
		String oldUserId = null;
		try {
			oldUserId = MdnLicenceManager.getRegisterdEmailAddress ();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return new ResultWrapper(null, "Invalid Old User ID. Cannot continue.");
		}

		if (oldUserId == null || oldUserId.isEmpty())
			return new ResultWrapper (null, "Old User ID is not existing. Cannot continue.");      
		
    	if (newUserID == null || newUserID.isEmpty()){
    		ResultWrapper ret = new ResultWrapper(null, "Please enter the new registered email address.");
    		ret.setRegisteredEmailAddress(oldUserId);
    		return ret;
    	}else {
        	if (!Validate.validateEmailAddress(newUserID)){
            	ResultWrapper ret = new ResultWrapper(null, "New email address is not valid."); 
            	ret.setRegisteredEmailAddress(oldUserId);
            	return ret;
        	}    		
    	}	
    	
    	LicenceKey lk = null;
    	String licenceKey = null;
    	try {
			lk = MdnLicenceManager.getLicenceKey(MdnLicenceManager.PROD_CODE_MDN_SERVER);
		
			if (lk == null){
	        	return new ResultWrapper(null, "No license key. Cannot continue.");
	        }else{
	        	if (MdnLicenceManager.isValidLicenceKey(lk)){
	        		licenceKey = lk.toString();
	            }else{
	            	return new ResultWrapper(null, "Invalid license key. Cannot continue.");
	            }
	        	
	        }
    	} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return new ResultWrapper(null, "Invalid license key. Cannot continue.");
		}	
    	
    	// make sure all compulsory fields are valid
        //ResultWrapper ret = null;
        String msg = "";

        try {
			URL u = new URL("https://api.mobiledatanow.com/changeId?old_id=" + oldUserId + "&new_id=" + newUserID + "&license_key=" + licenceKey);
        	String data = "old_id=" + oldUserId + "&new_id=" + newUserID + "&license_key=" + licenceKey;
			
			System.out.println(u.toString());
			
        	HttpURLConnection uc = (HttpURLConnection) u.openConnection();
			uc.setDoOutput(true);
			//uc.setDoInput(true);
			uc.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
			
			//String requestXMLString = "POST " + u.getPath() + "?" + data + " HTTP/1.0\r\n" + "Content-Type: application/x-www-form-urlencoded\r\n" + "\r\n" ;
			//System.out.println(requestXMLString);
			
			PrintWriter pw = new PrintWriter(uc.getOutputStream());
            //pw.write(requestXMLString);
            pw.write(data);
			pw.flush();
            pw.close();					

        	/*BufferedReader response = new BufferedReader(new InputStreamReader(uc.getInputStream()));


        	StringBuffer resultStringBuffer = new StringBuffer();
            String inputLine = ""; 

            while ((inputLine = response.readLine()) != null) {

                     resultStringBuffer.append(inputLine);

            }
            String resultString = resultStringBuffer.toString();

            System.out.println("result:" + resultString);*/            
            
        	//BufferedInputStream bis = new BufferedInputStream(uc.getInputStream());
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document        doc        = docBuilder.parse(uc.getInputStream());
			//System.out.prinln(doc.toString()) is misleading appears empty
			Element element = doc.getDocumentElement();
			String status = element.getAttribute("status");
			if (status.equalsIgnoreCase("ok")){
                try
                {
                    MdnStore store = MdnLicenceManager.getStore();
                    store.setRegisteredEmailAddress(newUserID);
                    _registeredEmailAddress = newUserID;
                    
                    store.store();
                    ResultWrapper ret = new ResultWrapper("ok", "Your email address has been successfully updated.");
                    ret.setRegisteredEmailAddress(newUserID);
                    return ret;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                	System.out.println(e.toString());
                    return new ResultWrapper(null, e.toString());
                }				
			}
			else {//if (status.equalsIgnoreCase("failed")){
				msg = element.getFirstChild().getTextContent();
				System.out.println("failed: " + msg);		         
				ResultWrapper ret = new ResultWrapper(null, msg);
				ret.setRegisteredEmailAddress(oldUserId); 
		        return ret;
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return new ResultWrapper(null, e.toString());
		} catch (IOException e) {
			e.printStackTrace();
			return new ResultWrapper(null, e.toString());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return new ResultWrapper(null, e.toString());
		} catch (SAXException e) {
			e.printStackTrace();
			return new ResultWrapper(null, e.toString());
		}
       
    }        
    public ResultWrapper getUserLicenses(){
    	try {
			if (_licenceKey == null){
				LicenceKey lk = null;
		    	lk = MdnLicenceManager.getLicenceKey(MdnLicenceManager.PROD_CODE_MDN_SERVER);
				if (lk == null){
		        	return new ResultWrapper(null, "No license key. Cannot continue.");
		        }else{
		        	if (MdnLicenceManager.isValidLicenceKey(lk)){
		        		_licenceKey = lk;
	                	// get the activation key
	                    _activationKey = MdnLicenceManager.getActivationKey(lk);
	                    
	                    _registeredEmailAddress = MdnLicenceManager.getRegisterdEmailAddress();	        		
		            }else{
		            	return new ResultWrapper(null, "Invalid license key. Cannot continue.");
		            }
		        	
		        }
			}
			
    	} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return new ResultWrapper(null, "Invalid license key. Cannot continue.");
		} 
    	
    	try {
			URL u = new URL("https://api.mobiledatanow.com/getUsers?id=" + _registeredEmailAddress + "&license_key=" + _licenceKey.toString() + "&activation_key=" + _activationKey.toString());
			String data = "id=" + _registeredEmailAddress + "&license_key=" + _licenceKey.toString() + "&activation_key=" + _activationKey.toString();
			
			System.out.println(u.toString());
			
        	HttpURLConnection uc = (HttpURLConnection) u.openConnection();
			uc.setDoOutput(true);
			uc.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
			
			PrintWriter pw = new PrintWriter(uc.getOutputStream());
            pw.write(data);
			pw.flush();
            pw.close();

            /*BufferedReader in = new BufferedReader(
            		new InputStreamReader(uc.getInputStream()));
    				String res = in.readLine();
    				in.close();
    		System.out.println("RETURN XML FOR GETUSERS [" + res + "]");    */  
            
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document        doc        = docBuilder.parse(uc.getInputStream());
			//System.out.prinln(doc.toString()) is misleading appears empty
			Element element = doc.getDocumentElement();
			NodeList list = element.getChildNodes();	
			
			UserLicenses userLicenses = new UserLicenses();
			
			String status = element.getAttribute("status");
			System.out.println( "status " + status);
			if (status.equalsIgnoreCase("ok")){
				String type = null;
				String quantity = null;
				String expiry = null;
				for (int i = 0; i < list.getLength(); i++){
					Node node = list.item(i);
					
					String nodeName = node.getNodeName();
					if (nodeName.equalsIgnoreCase("user")){
						NodeList userList = node.getChildNodes();
						type = null;
						quantity = null;
						expiry = null;
						for (int j = 0; j < userList.getLength(); j++){
							Node childNode = userList.item(j);
							nodeName = childNode.getNodeName();
							String nodeValue = childNode.getTextContent();
							System.out.println( nodeName + " " + nodeValue);
							if (nodeName.equalsIgnoreCase("type")){
								type = nodeValue;						
							}else if (nodeName.equalsIgnoreCase("quantity")){
								quantity = nodeValue;						
							}else if (nodeName.equalsIgnoreCase("expiry")){
								expiry = nodeValue;						
							}
							
						}
						if (type != null && quantity != null){
							UserLicense userLicense = new UserLicense(type, Integer.parseInt(quantity), expiry);							
							userLicenses.addUserLicense(userLicense);	
							userLicenses.addUserLicenseType(type);		
							
						}						
					}
				}
			}
			userLicenses.setPublicGroup(_publicGroup);
			return new ResultWrapper(userLicenses, null);

    	} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResultWrapper(null, e.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResultWrapper(null, e.toString());
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResultWrapper(null, e.toString());
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResultWrapper(null, e.toString());
		}
    }
    public ResultWrapper validateUser(String userID, String licenceKey, String installRef){
    	return LicenseRemoteCallManager.validateUser(userID, licenceKey, installRef);
    }    
    public ResultWrapper getSecureLoginLink(){
    	return LicenseRemoteCallManager.getSecureLoginLink(_registeredEmailAddress, _licenceKey.toString());
    }   
    public String getDatabaseDriverUploadPath(){
    	return  Config.getProp(MdnAdminConst.DATABASE_DRIVER_UPLOAD_PATH);
    }
    public String getExportFilePath(){
    	return  Config.getProp(MdnAdminConst.EXPORT_PROJECT_FILE_PATH);
    }    
    public String getImportFilePath(){
    	return  Config.getProp(MdnAdminConst.IMPORT_RPOJECT_FILE_PATH);
    }   
    public String getWebServiceCompileFilePath(){
    	return  Config.getProp(MdnAdminConst.WEB_SERVICE_COMPILE_FILE_PATH);
    }
    public void addDriverFileToClasspath(String file) throws IOException{
    	ClassPathHacker.addFile(file);
    }
    public String[] getLogFilePath(){
    	String whole =  Config.getProp(MdnAdminConst.LOG_FILE_PATH);
    	
    	return whole.split(";");
    }

	public int getInstallationReferenceNumber() {
		return _installationReferenceNumber;
	}

	public void setInstallationReferenceNumber(int referenceNumber) {
		_installationReferenceNumber = referenceNumber;
	}

	public List<ParamListItem> getParamList(String mWsdlUrl, String service, String port, String operation) throws MdnException{
		ParamHelper paramHelper = new ParamHelper(mWsdlUrl);
		paramHelper.setCurrentService(service);
		paramHelper.setCurrentPort(port);
		paramHelper.setCurrentOperation(operation);
		paramHelper.createParamList();
		return paramHelper.getParamList();
	}
	public WebServiceDetail loadWebServiceDefinition(Definition definition, String selectedOperation){
		return WebServiceDefinitionHelper.loadWebServiceDefinition(definition, selectedOperation);
	}
}

//==============================================================================
// end of file RemoteLicenseManagerServant.java
//==============================================================================
