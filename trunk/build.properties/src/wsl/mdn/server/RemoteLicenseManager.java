//==============================================================================
// RemoteLicenseManager.java
// Copyright (c) 2001 WAP Solutions Ltd.
//==============================================================================

package wsl.mdn.server;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;

import com.framedobjects.dashwell.utils.webservice.ParamListItem;

import wsl.fw.exception.MdnException;
import wsl.fw.remote.RmiServant;
import wsl.mdn.dataview.ResultWrapper;
import wsl.mdn.dataview.WebServiceDetail;

//------------------------------------------------------------------------------
/**
 * Remote Interface for licensing functions.
 */
public interface RemoteLicenseManager  extends RmiServant
{
    // constants for isLicenseValid() return values
    // success values are >= 0, failures are < 0.
    /** Return value for isLicenseValid(). A valid full license */
    public final static int LICENSE_VALID = 0;

    /** Return value for isLicenseValid(). A valid evaluation license */
    public final static int LICENSE_EVALUATION = 1;

    /**
     * Return value for isLicenseValid(). Remote failure, could not check
     * license due to server being down or an RMI error. This constant is a
     * placeholder for use by non-remote functions that catch RemoteException
     * and want to return normally.
     */
    public final static int LICENSE_RMI_FAIL = -1;

    /** Return value for isLicenseValid(). No license */
    public final static int LICENSE_NOT_SET = -2;

    /** Return value for isLicenseValid(). Invalid license (corrupt or nodelock) */
    public final static int LICENSE_INVALID = -3;

    /** Return value for isLicenseValid(). License is expired */
    public final static int LICENSE_EXPIRED = -4;

    /** Return value for isgetLicenseLock(). Lock granted */
    public final static int LICENSE_LOCK_GRANTED = 2;

    /** Return value for isgetLicenseLock(). Lock not granted (too many users) */
    public final static int LICENSE_LOCK_NOT_GRANTED = -5;

    /**
     * Check if the license is valid. Return values >= 0 are success (i.e. the
     * license is valis and the application may continue. Return values < 0
     * are a failure.
     * @return one of the above LICENSE_ constants.
     * @throws RemoteException if there is an RMI error.
     */
    public int isLicenseValid() throws RemoteException;
    public ResultWrapper updateUserLicense() throws RemoteException;
    public ResultWrapper updatePublicMessages(int numberSent) throws RemoteException;
    public ResultWrapper changeEmailAddress(String userID) throws RemoteException;
    public ResultWrapper validateUser(String userID, String licenceKey, String installRef) throws RemoteException;
    public ResultWrapper getSecureLoginLink() throws RemoteException;
    public String getRegisterdEmailAddress() throws RemoteException;
    public Boolean getPublicGroupFlag() throws RemoteException;
    public int getAvailablePublicMessages() throws RemoteException;
    public int getInstallationReferenceNumber() throws RemoteException;
    public ResultWrapper getUserLicenses() throws RemoteException;
    /**
     * get Number Of User License
     */
    public int getNumberOfUserLicense() throws RemoteException;
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
    public int getLicenseLock(String key) throws RemoteException;

    /**
     * Release a lock.
     * @param key, the key previously used to acquire the lock.
     * @throws RemoteException if there is an RMI error.
     */
    public void releaseLicenseLock(String key) throws RemoteException;

    /**
     * Get the version number of the MDN Server.
     * @return the version number as a string.
     * @throws RemoteException if there is an RMI error.
     */
    public String getServerVersion() throws RemoteException;
    
    public String getDatabaseDriverUploadPath() throws RemoteException;
    public String getExportFilePath() throws RemoteException;
    public void addDriverFileToClasspath(String file) throws IOException, RemoteException;
    public String getImportFilePath() throws RemoteException;
    public String[] getLogFilePath() throws RemoteException;   
    public String getWebServiceCompileFilePath() throws RemoteException;
    public List<ParamListItem> getParamList(String mWsdlUrl, String service, String port, String operation) throws MdnException, RemoteException; 
    public WebServiceDetail loadWebServiceDefinition(Definition definition, String selectedOperation) throws RemoteException;
    //public Definition getWebServiceDefinition(String wsdlUrl) throws WSDLException, RemoteException; 
}

//==============================================================================
// end of file RemoteLicenseManager.java
//==============================================================================
