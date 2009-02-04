//==============================================================================
// Terminator.java
// Copyright (c) 2001 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import wsl.fw.security.SecurityId;
import wsl.fw.security.SecurityException;

//------------------------------------------------------------------------------
/**
 * Remote interface for a Terminator used to remotely terminate an RMI server.
 */
public interface Terminator extends Remote
{
    /**
     * Terminate the rmi server.
     * @param caller, a SecurityId used to verify the caller has permission
     *   to terminate.
     * @param msTerminateTime, if <= 0 no effect, else after this many MS
     *   the VM will be stopped with System.exit(1);
     * @throws RemoteException if there is an RMI error.
     * @throws SecurityException if the caller does not have the privilege.
     */
    public void terminate(SecurityId caller, long msTerminateTime)
        throws RemoteException, SecurityException;
}

//==============================================================================
// end of file Terminator.java
//==============================================================================
