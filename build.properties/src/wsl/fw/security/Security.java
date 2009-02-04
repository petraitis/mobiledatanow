//==============================================================================
// Security.java
// $Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $
// $Archive: /Framework/Source/wsl/fw/security/Security.java $
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.security;

import java.util.Set;
import java.rmi.RemoteException;
import wsl.fw.remote.RmiServant;

//------------------------------------------------------------------------------
/**
 * Remote interface definition for security operations
 */
public interface Security extends RmiServant
{
    //--------------------------------------------------------------------------
    /**
     * Login and validate a user.
     * @param securityId, a SecurityId identifying the user.
     * @return the user identified by securityId.
     * @throws SecurityException if securityId does not validate the user.
     * @throws RemoteException if there is an RMI error.
     */
    public User login(SecurityId securityId)
        throws RemoteException, SecurityException;

    //--------------------------------------------------------------------------
    /**
     * Get the set of features the named user has privilege to.
     * @param username, the user.
     * @return the set of features.
     * @throws RemoteException if there is an RMI error.
     */
    public Set getFeatureSet(String username) throws RemoteException;
}

//==============================================================================
// end of file Security.java
//==============================================================================
