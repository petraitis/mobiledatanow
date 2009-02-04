//==============================================================================
// RmiServant.java
// $Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

//------------------------------------------------------------------------------
/**
 * Interface for use by all remote servant objects that will be hosted by a
 * RmiServer and registered with the SecureRegistry.
 */
public interface RmiServant extends Remote
{
    //--------------------------------------------------------------------------
    /**
     * Ping function, allows a client to test if the remote object is still
     * accessible. Should be implemented as a NOP.
     * @throws RemoteException if the servant does not respond.
     */
    public void ping() throws RemoteException;
}

//==============================================================================
// end of file RmiServant.java
//==============================================================================
