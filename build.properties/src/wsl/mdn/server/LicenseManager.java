//==============================================================================
// LicenseManager.java
// Copyright (c) 2001 WAP Solutions Ltd.
//==============================================================================

package wsl.mdn.server;

import java.util.List;

import javax.wsdl.Definition;

import wsl.fw.remote.LocalServerFactory;
import wsl.fw.resource.ResId;
import wsl.fw.security.SecurityId;
import wsl.fw.util.Log;
import wsl.mdn.dataview.ResultWrapper;
import wsl.mdn.dataview.WebServiceDetail;

import com.framedobjects.dashwell.utils.webservice.ParamListItem;

//------------------------------------------------------------------------------
/**
 * Static local wrapper to simplify calling the remote licensing functions.
 */
public class LicenseManager
{
    private static SecurityId           s_systemId = null;
    private static RemoteLicenseManager s_remoteLicenseManager = null;

    public final static ResId ERR_EVALUATION       = new ResId("server.LicenseManager.evaluation");
    public final static ResId ERR_EXPIRED          = new ResId("server.LicenseManager.expired");
    public final static ResId ERR_NOT_SET          = new ResId("server.LicenseManager.notSet");
    public final static ResId ERR_INVALID          = new ResId("server.LicenseManager.invalid");
    public final static ResId ERR_LOCK_GRANTED     = new ResId("server.LicenseManager.lockGranted");
    public final static ResId ERR_LOCK_NOT_GRANTED = new ResId("server.LicenseManager.lockNotGranted");
    public final static ResId ERR_RMI_FAIL         = new ResId("server.LicenseManager.rmiFail");
    public final static ResId ERR_VALID            = new ResId("server.LicenseManager.valid");
    public final static ResId ERR_UNKNOWN          = new ResId("server.LicenseManager.unknown");

    //--------------------------------------------------------------------------
    /**
     * Check if the license is valid. Return values >= 0 are success (i.e. the
     * license is valis and the application may continue. Return values < 0
     * are a failure.
     * @return one of the above RemoteLicenseManager.LICENSE_ constants,
     *   which may be RemoteLicenseManager.LICENSE_RMI_FAIL if the server could
     *   not be contacted or there was an RMI error.
     */
    public static int isLicenseValid()
    {
        int rv = RemoteLicenseManager.LICENSE_RMI_FAIL;

        try
        {
            RemoteLicenseManager rmm = getRemoteLicenseManager();
            if (rmm != null)
                rv = rmm.isLicenseValid();
        }
        catch (Exception e)
        {
            Log.warning("LicenseManager.isLicenseValid: " + e.toString());
            clearRemoteLicenseManager();
        }

        return rv;
    }
    /**
     * get Master ID
     * @return String 
     */
    public static String getRegisteredEmailAddress()
    {
        String rv = null;

        try
        {
            RemoteLicenseManager rmm = getRemoteLicenseManager();
            if (rmm != null)
                rv = rmm.getRegisterdEmailAddress();
        }
        catch (Exception e)
        {
            Log.warning("LicenseManager.getRegisterdEmailAddress: " + e.toString());
            clearRemoteLicenseManager();
        }

        return rv;
    } 
    /**
     * get Public Group Flag 
     * @return
     */
    public static Boolean getPublicGroupFlag()
    {
    	Boolean rv = null;

        try
        {
            RemoteLicenseManager rmm = getRemoteLicenseManager();
            if (rmm != null)
                rv = rmm.getPublicGroupFlag();
        }
        catch (Exception e)
        {
            Log.warning("LicenseManager.getPublicGroupFlag: " + e.toString());
            clearRemoteLicenseManager();
        }

        return rv;
    } 
    /**
     * get Public Messages Number 
     * @return
     */
    public static int getAvailablePublicMessages()
    {
    	int rv = 0;

        try
        {
            RemoteLicenseManager rmm = getRemoteLicenseManager();
            if (rmm != null)
                rv = rmm.getAvailablePublicMessages();
        }
        catch (Exception e)
        {
            Log.warning("LicenseManager.getAvailablePublicMessages: " + e.toString());
            clearRemoteLicenseManager();
        }

        return rv;
    }
    /**
     * get Installation Reference Number
     * @return
     */
    public static int getInstallationReferenceNumber()
    {
    	int rv = 0;

        try
        {
            RemoteLicenseManager rmm = getRemoteLicenseManager();
            if (rmm != null)
                rv = rmm.getInstallationReferenceNumber();
        }
        catch (Exception e)
        {
            Log.warning("LicenseManager.getInstallationReferenceNumber: " + e.toString());
            clearRemoteLicenseManager();
        }

        return rv;
    }    
    /**
     * update user license
     * @param userID String
     * @return ResultWrapper
     */
    public static ResultWrapper updateUserLicense()
    {
        ResultWrapper resultWrapper = null;
    	try
        {
            RemoteLicenseManager rmm = getRemoteLicenseManager();
            if (rmm != null)
            	resultWrapper = rmm.updateUserLicense();
            	return resultWrapper;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        	Log.warning("LicenseManager.updateUserLicense: " + e.toString());
            clearRemoteLicenseManager();
        }

        return resultWrapper;
    }
    public static ResultWrapper updatePublicMessages(int numberSent)
    {
        ResultWrapper resultWrapper = null;
    	try
        {
            RemoteLicenseManager rmm = getRemoteLicenseManager();
            if (rmm != null)
            	resultWrapper = rmm.updatePublicMessages(numberSent);
            	return resultWrapper;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        	Log.warning("LicenseManager.updatePublicMessages: " + e.toString());
            clearRemoteLicenseManager();
        }

        return resultWrapper;
    }
    public static ResultWrapper changeEmailAddress(String userID)
    {
        ResultWrapper resultWrapper = null;
    	try
        {
            RemoteLicenseManager rmm = getRemoteLicenseManager();
            if (rmm != null)
            	resultWrapper = rmm.changeEmailAddress(userID);
            	return resultWrapper;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        	Log.warning("LicenseManager.updateUserLicense: " + e.toString());
            clearRemoteLicenseManager();
        }

        return resultWrapper;
    }    
    /**
     * get user license
     * @param userID String
     * @return ResultWrapper
     */
    public static ResultWrapper validateUser(String userID, String licenceKey, String installRef)
    {
        ResultWrapper resultWrapper = null;
    	try
        {
            RemoteLicenseManager rmm = getRemoteLicenseManager();
            if (rmm != null)
            	resultWrapper = rmm.validateUser(userID, licenceKey, installRef);
            	return resultWrapper;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        	Log.warning("LicenseManager.validateUser: " + e.toString());
            clearRemoteLicenseManager();
        }

        return resultWrapper;
    }
    /**
     * get Secure Login Link
     * @param userID
     * @param licenceKey
     * @return
     */
    public static ResultWrapper getSecureLoginLink()
    {
        ResultWrapper resultWrapper = null;
    	try
        {
            RemoteLicenseManager rmm = getRemoteLicenseManager();
            if (rmm != null)
            	resultWrapper = rmm.getSecureLoginLink();
            	return resultWrapper;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        	Log.warning("LicenseManager.getSecureLoginLink: " + e.toString());
            clearRemoteLicenseManager();
        }

        return resultWrapper;
    }
    
    /**
     * get Number Of User License
     * @return int
     */
    public static int getNumberOfUserLicense(){
        int rv = RemoteLicenseManager.LICENSE_RMI_FAIL;

        try
        {
            RemoteLicenseManager rmm = getRemoteLicenseManager();
            if (rmm != null)
                rv = rmm.getNumberOfUserLicense();
        }
        catch (Exception e)
        {
            Log.warning("LicenseManager.getNumberOfUserLicense: " + e.toString());
            clearRemoteLicenseManager();
        }

        return rv;    	
    }
    public static ResultWrapper getUserLicenses(){
    	ResultWrapper rv = null;

        try
        {
            RemoteLicenseManager rmm = getRemoteLicenseManager();
            if (rmm != null)
                rv = rmm.getUserLicenses();
        }
        catch (Exception e)
        {
            Log.warning("LicenseManager.getUserLicenses: " + e.toString());
            clearRemoteLicenseManager();
        }

        return rv;    	
    }
    public static String getDatabaseDriverUploadPath(){
    	String rv = null;

        try
        {
            RemoteLicenseManager rmm = getRemoteLicenseManager();
            if (rmm != null)
                rv = rmm.getDatabaseDriverUploadPath();
        }
        catch (Exception e)
        {
            Log.warning("LicenseManager.getDatabaseDriverUploadPath: " + e.toString());
            clearRemoteLicenseManager();
        }

        return rv;    	
    }
    public static String getExportFilePath(){
    	String rv = null;

        try
        {
            RemoteLicenseManager rmm = getRemoteLicenseManager();
            if (rmm != null)
                rv = rmm.getExportFilePath();
        }
        catch (Exception e)
        {
            Log.warning("LicenseManager.getExportFilePath: " + e.toString());
            clearRemoteLicenseManager();
        }

        return rv;    	
    }    
    public static String getImportFilePath(){
    	String rv = null;

        try
        {
            RemoteLicenseManager rmm = getRemoteLicenseManager();
            if (rmm != null)
                rv = rmm.getImportFilePath();
        }
        catch (Exception e)
        {
            Log.warning("LicenseManager.getImportFilePath: " + e.toString());
            clearRemoteLicenseManager();
        }

        return rv;    	
    }    
    public static String getWebServiceCompileFilePath(){
    	String rv = null;

        try
        {
            RemoteLicenseManager rmm = getRemoteLicenseManager();
            if (rmm != null)
                rv = rmm.getWebServiceCompileFilePath();
        }
        catch (Exception e)
        {
            Log.warning("LicenseManager.getWebServiceCompileFilePath: " + e.toString());
            clearRemoteLicenseManager();
        }

        return rv;    	
    }     
    public static void addDriverFileToClasspath(String file){
    	try
        {
            RemoteLicenseManager rmm = getRemoteLicenseManager();
            if (rmm != null)
                rmm.addDriverFileToClasspath(file);
        }
        catch (Exception e)
        {
            Log.warning("LicenseManager.addDriverFileToClasspath: " + e.toString());
            clearRemoteLicenseManager();
        }   	
    }
    public static String[] getLogFilePath(){
    	String[] rv = null;

        try
        {
            RemoteLicenseManager rmm = getRemoteLicenseManager();
            if (rmm != null)
                rv = rmm.getLogFilePath();
        }
        catch (Exception e)
        {
            Log.warning("LicenseManager.getLogFilePath: " + e.toString());
            clearRemoteLicenseManager();
        }

        return rv;    	
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
     *   for isLicenseValid. May be RemoteLicenseManager.LICENSE_RMI_FAIL if
     *   the server could not be contacted or there was an RMI error.
     */
    public static int getLicenseLock(String key)
    {
        int rv = RemoteLicenseManager.LICENSE_RMI_FAIL;

        try
        {
            RemoteLicenseManager rmm = getRemoteLicenseManager();
            if (rmm != null)
                rv = rmm.getLicenseLock(key);
        }
        catch (Exception e)
        {
            Log.warning("LicenseManager.getLicenseLock: " + e.toString());
            clearRemoteLicenseManager();
        }

        return rv;
    }

    //--------------------------------------------------------------------------
    /**
     * Release a lock.
     * @param key, the key previously used to acquire the lock.
     */
    public static void releaseLicenseLock(String key)
    {
        try
        {
            RemoteLicenseManager rmm = getRemoteLicenseManager();
            if (rmm != null)
                rmm.releaseLicenseLock(key);
        }
        catch (Exception e)
        {
            Log.warning("LicenseManager.releaseLicenseLock: " + e.toString());
            clearRemoteLicenseManager();
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Get server version number.
     * @return the version number, will be null if cound not contact the server.
     */
    public static String getServerVersion()
    {
        try
        {
            RemoteLicenseManager rmm = getRemoteLicenseManager();
            if (rmm != null)
                return rmm.getServerVersion();
        }
        catch (Exception e)
        {
            Log.warning("LicenseManager.getServerVersion: " + e.toString());
            clearRemoteLicenseManager();
        }

        return null;
    }

    //--------------------------------------------------------------------------
    /**
     * Get text describing a LicenseManager error code, such as those returned
     * by isLicenseValid() or getLicenseLock() (i.e. the RemoteLicenseManager
     * LICENSE_ constants).
     * Note, also works for those "error" values that indicate success.
     * @param errorCode, the code to get the description for.
     * @return the descriptive string.
     */
    public static String getErrorDescription(int errorCode)
    {
        switch (errorCode)
        {
            case RemoteLicenseManager.LICENSE_EVALUATION :
                return ERR_EVALUATION.getText();

            case RemoteLicenseManager.LICENSE_EXPIRED :
                return ERR_EXPIRED.getText();

            case RemoteLicenseManager.LICENSE_NOT_SET :
                return ERR_NOT_SET.getText();

            case RemoteLicenseManager.LICENSE_INVALID :
                return ERR_INVALID.getText();

            case RemoteLicenseManager.LICENSE_LOCK_GRANTED :
                return ERR_LOCK_GRANTED.getText();

            case RemoteLicenseManager.LICENSE_LOCK_NOT_GRANTED :
                return ERR_LOCK_NOT_GRANTED.getText();

            case RemoteLicenseManager.LICENSE_RMI_FAIL :
                return ERR_RMI_FAIL.getText();

            case RemoteLicenseManager.LICENSE_VALID :
                return ERR_VALID.getText();
        }

        return ERR_UNKNOWN.getText();
    }

    //--------------------------------------------------------------------------
    /**
     * Get the system id for authenticating with the secure registry.
     * @return a system id loaded from Config data.
     */
    private synchronized static SecurityId getSystemId()
    {
        if (s_systemId == null)
            s_systemId = SecurityId.getSystemId();
        return s_systemId;
    }

    //--------------------------------------------------------------------------
    /**
     * Get the remote license manager, create if necessary. Will attempt to
     * reconnect on subsequent calls if there is a RMI failure.
     * @returns the RemoteLicenseManager.
     */
    private synchronized static RemoteLicenseManager getRemoteLicenseManager()
    {
        if (s_remoteLicenseManager == null)
            try
            {
                // have a name for the remote data manager, get it
                s_remoteLicenseManager = (RemoteLicenseManager)
                    LocalServerFactory.get(getSystemId(),
                    RemoteLicenseManager.class.getName());
            }
            catch (Exception e)
            {
                Log.fatal("LicenseManager.getRemoteLicenseManager: " + e.toString());
            }

        return s_remoteLicenseManager;
    }

    //--------------------------------------------------------------------------
    /**
     * Clear the cached license manager so a new one is created next time.
     */
    private synchronized static void clearRemoteLicenseManager()
    {
         s_remoteLicenseManager = null;
    }
    
    public static List<ParamListItem> getParamList(String mWsdlUrl, String service, String port, String operation) throws Exception{
    	try
        {
            RemoteLicenseManager rmm = getRemoteLicenseManager();
            if (rmm != null)
                return rmm.getParamList(mWsdlUrl, service, port, operation);
        }
        catch (Exception e)
        {
        	clearRemoteLicenseManager();
        	Log.warning("LicenseManager.getParamList: " + e.toString());
            throw e;
        } 
        return null;
    }   
    public static WebServiceDetail loadWebServiceDefinition(Definition definition, String selectedOperation){
    	try
        {
            RemoteLicenseManager rmm = getRemoteLicenseManager();
            if (rmm != null)
                return rmm.loadWebServiceDefinition(definition, selectedOperation);
        }
        catch (Exception e)
        {
            Log.warning("LicenseManager.loadWebServiceDefinition: " + e.toString());
            clearRemoteLicenseManager();
        } 
        return null;
    }    
    /*public static Definition getWebServiceDefinition(String wsdlUrl){
    	try
        {
            RemoteLicenseManager rmm = getRemoteLicenseManager();
            if (rmm != null)
                return rmm.getWebServiceDefinition(wsdlUrl);
        }
        catch (Exception e)
        {
            Log.warning("LicenseManager.getWebServiceDefinition: " + e.toString());
            clearRemoteLicenseManager();
        } 
        return null;
    } */      
}

//==============================================================================
// end of file LicenseManager.java
//==============================================================================
