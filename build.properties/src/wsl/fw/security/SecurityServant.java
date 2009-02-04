//==============================================================================
// SecurityServant.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================


package wsl.fw.security;

import java.util.Set;
import java.rmi.RemoteException;
import wsl.fw.remote.RmiServantBase;
import wsl.fw.resource.ResId;

//------------------------------------------------------------------------------
/**
 * Servant to implement the Security remote interface.
 * Provides remote access to the data objects and functions in the
 * wsl.fw.security package.
 */
public class SecurityServant extends RmiServantBase implements Security
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/security/SecurityServant.java $ ";

    // resources
    public static final ResId EXCEPTION_NO_LONGER_USED  = new ResId("SecurityServant.exception.NoLongerUsed");
    public static final ResId EXCEPTION_NO_LONGER_USED2  = new ResId("SecurityServant.exception.NoLongerUsed2");

    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public SecurityServant()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Login and validate a user.
     * @param securityId, a SecurityId identifying the user.
     * @return the user identified by securityId.
     * @throws SecurityException if securityId does not validate the user.
     * @throws RemoteException if there is an RMI error.
     */
    public User login(SecurityId securityId)
        throws RemoteException, SecurityException
    {
        // no longer used
        if (true)
            throw new UnsupportedOperationException(EXCEPTION_NO_LONGER_USED.getText());

        return User.login(securityId).getLoginUser();
    }

    //--------------------------------------------------------------------------
    /**
     * Get the set of features the named user has privilege to.
     * @param username, the user.
     * @return the set of features.
     * @throws RemoteException if there is an RMI error.
     */
    public Set getFeatureSet(String username) throws RemoteException
    {
        // no longer used
        if (true)
            throw new UnsupportedOperationException(EXCEPTION_NO_LONGER_USED2.getText());

        return User.getFeatureSet(username);
    }
}

//==============================================================================
// end of file SecurityServant.java
//==============================================================================
