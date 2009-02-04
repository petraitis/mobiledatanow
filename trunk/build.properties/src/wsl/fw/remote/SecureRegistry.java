//==============================================================================
// SecureRegistry.java
// $Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.remote;

import wsl.fw.security.SecurityId;
import wsl.fw.security.SecurityException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import wsl.fw.security.Feature;

//--------------------------------------------------------------------------
/**
 * Interface for secure registry functions. This mimics the interface
 * java.rmi.registry.Registry, but adds an authentication parameter (SecurityId)
 * which may be used to authenticate the caller before performing the function.
 * Implementations may, at their discretion, allow multiple bindings to the same
 * name and perform load balancing between the multiple instances.
 */
public interface SecureRegistry extends Remote
{
    // known name for bootstrapping
    public final static String SECURE_REGISTRY_NAME = "wsl.fw.remote.SecureRegistry";

    //--------------------------------------------------------------------------
    /**
     * Check if the caller has the named security privilege.
     * @param caller, the identity of the caller performing this operation.
     * @param priv, the name of the pricilege to check.
     * @throws RemoteException if there is an RMI error.
     * @throws SecurityException if the caller does not have the privilege.
     */
    public void securityCheck(SecurityId caller, String priv)
        throws RemoteException, SecurityException;

    //--------------------------------------------------------------------------
    /**
     * Create a secure registry binding between the name and the servant.
     * @param caller, the identity of the caller performing this operation.
     * @param name, the name that will be bound to the servant.
     * @param obj, the servant object.
     * @throws RemoteException if there is an RMI error.
     * @throws SecurityException if the caller does not have permission to
     *   perform this function.
     */
    public void bind(SecurityId caller, String name, RmiServant obj)
        throws RemoteException, SecurityException;

    //--------------------------------------------------------------------------
    /**
     * Returns an array of the names bound in the registry.
     * @param caller, the identity of the caller performing this operation.
     * @return an array of strings naming the available servants.
     * @throws RemoteException if there is an RMI error.
     * @throws SecurityException if the caller does not have permission to
     *   perform this function.
     */
    public String[] list(SecurityId caller)
        throws RemoteException, SecurityException;

    //--------------------------------------------------------------------------
    /**
     * Returns a reference, a stub, for the remote object associated with the
     * specified name. Note that in the case of load balaning this may not be
     * the only RmiServant instance bound to that name, nor are subsequent calls
     * guaranteed to get the same object.
     * @param caller, the identity of the caller performing this operation.
     * @param name, the name of the servant to get.
     * @return the matching RmiServant.
     * @throws RemoteException if there is an RMI error.
     * @throws SecurityException if the caller does not have permission to
     *   perform this function.
     * @throws NotBoundException if the named servant could not be found.
     */
    public RmiServant lookup(SecurityId caller, String name)
        throws RemoteException, SecurityException, NotBoundException;

    //--------------------------------------------------------------------------
    /**
     * Returns an array of references (stubs) for all the remote objects
     * associated with the specified name.
     * @param caller, the identity of the caller performing this operation.
     * @return the array of servants.
     * @throws RemoteException if there is an RMI error.
     * @throws SecurityException if the caller does not have permission to
     *   perform this function.
     * @throws NotBoundException if the named servant could not be found.
     */
    public RmiServant[] lookupAll(SecurityId caller, String name)
        throws RemoteException, SecurityException, NotBoundException;

    //--------------------------------------------------------------------------
    /**
     * Destroys the binding between the specified name and its associated
     * remote object.
     * @param caller, the identity of the caller performing this operation.
     * @param name, the name of the object to remove bindings for.
     * @param obj, the servant to remove. if null then all servants bound to
     *   that name are removed.
     * @throws RemoteException if there is an RMI error.
     * @throws SecurityException if the caller does not have permission to
     *   perform this function.
     */
    public void unbind(SecurityId caller, String name, RmiServant obj)
        throws RemoteException, SecurityException;
}

//==============================================================================
// end of file SecureRegistry.java
//==============================================================================
