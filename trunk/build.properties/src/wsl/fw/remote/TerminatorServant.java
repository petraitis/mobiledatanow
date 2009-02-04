//==============================================================================
// TerminatorServant.java
// Copyright (c) 2001 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.remote;

import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.resource.ResId;
import wsl.fw.security.BootstrapSecurityManager;
import wsl.fw.security.SecurityException;
import wsl.fw.security.SecurityId;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;

//------------------------------------------------------------------------------
/**
 * Servant to remotely terminate an rmi server.
 */
public class TerminatorServant
    extends RmiServantBase
    implements Terminator, Runnable
{
    public final static String TERMINATE = "TERMINATE";

    // resources
    public final static ResId TXT_HARD_TERMINATE = new ResId("remote.TerminatorServant.txt.hardTerminate");

    private RmiServer _rmiServer;
    private long      _msTerminateTime;

    //--------------------------------------------------------------------------
    /**
     * Constructor.
     * @param rmiServer, the server this terminator is to terminate.
     */
    public TerminatorServant(RmiServer rmiServer)
    {
        Util.argCheckNull(rmiServer);
        _rmiServer = rmiServer;
    }

    //--------------------------------------------------------------------------
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
        throws RemoteException, SecurityException
    {
        // test caller's access
        try
        {
            SecureRegistry secureRegistry = LocalServerFactory.getSecureRegistry();
            secureRegistry.securityCheck(caller, TERMINATE);
        }
        catch (NotBoundException e)
        {
            throw new RemoteException("TerminatorServant.terminate", e);
        }

        // terminate the server
        _rmiServer.stopServer();

        // if hard terminate then start terminate timer thread
        if (msTerminateTime > 0)
        {
            Thread terminateThread = new Thread(this);
            _msTerminateTime = msTerminateTime;
            terminateThread.setDaemon(true);
            terminateThread.start();
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Overload to get the registry binding name for this servant.
     * If one has not been explicitly set then the class name + the rmi server's
     * class name is used as a default.
     * @return the name to bind to.
     */
    public String getBindName()
    {
        if (_bindName == null)
            return makeTerminatorBindName(_rmiServer.getClass());
        else
            return _bindName;
    }

    //--------------------------------------------------------------------------
    /**
     * Make a bind name for a terminator attached to a a given server.
     * @param serverClass, the class of the server to make the bind name for.
     * @return a bind name string.
     */
    public static String makeTerminatorBindName(Class serverClass)
    {
        Util.argCheckNull(serverClass);

        return Terminator.class.getName() + ":" + serverClass.getName();
    }

    //--------------------------------------------------------------------------
    /**
     * Remotely terminate a server.
     * @param serverClass, the class of the server to terminate.
     * @param msTerminateTime, if <= 0 no effect, else after this many MS
     *   the VM will be stopped with System.exit(1);
     * @throws SecurityException if the caller does not have the required priv.
     */
    public static void remoteTerminate(Class serverClass, long msTerminateTime)
        throws RemoteException, SecurityException
    {
        // get the bind name for the appropriate terminator
        String terminatorName = makeTerminatorBindName(serverClass);
        SecurityId securityId = SecurityId.getSystemId();

        try
        {
            // get the terminator
            Terminator terminator = (Terminator) LocalServerFactory.get(
                securityId, terminatorName);

            // terminate
            terminator.terminate(securityId, msTerminateTime);
        }
        catch (NotBoundException e)
        {
            throw new RemoteException("TerminatorServant.remoteTerminate", e);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Used for hard termination in a timer thread.
     */
    public void run()
    {
        try
        {
            Thread.sleep(_msTerminateTime);
        }
        catch (Exception e)
        {
        }
        Log.log(TXT_HARD_TERMINATE.getText());
        System.exit(1);
    }
}

//==============================================================================
// end of file TerminatorServant.java
//==============================================================================
