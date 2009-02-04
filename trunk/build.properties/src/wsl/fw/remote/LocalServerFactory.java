//==============================================================================
// LocalServerFactory.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.remote;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.NotBoundException;
import wsl.fw.security.SecurityId;
import wsl.fw.security.SecurityException;
import wsl.fw.util.Config;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.util.CKfw;
import wsl.fw.resource.ResId;

//------------------------------------------------------------------------------
/**
 * LocalServerFactory, a class to retrieve remote objects (RmiServants) from
 * the secure registry.
 * Use the get() function which does getSecureRegistry().lookup() or use
 * the getSecureRegistry() function and then access the SecureRegistry
 * directly.
 * Uses defaults and Config entries to locate the secure registry, which can be
 * overridden by calling setHost(), setPort() or setArgs().
 * Command line clients should usually call setArgs() passing the command line
 * parameters so that LocalServerFactory can parse -host and -regport.
 */
// Option, this class could be extended to allow for local creation as well.
public class LocalServerFactory
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $"
        + "$Archive: /Framework/Source/wsl/fw/remote/LocalServerFactory.java $";

    // resources
    public static final ResId EXCEPTON_HOST_EMPTY  = new ResId("LocalServerFactory.exception.HostEmpty");
    public static final ResId WARNING_SET_PORT  = new ResId("LocalServerFactory.warning.SetPort");
    public static final ResId WARNING_SET_ARGS  = new ResId("LocalServerFactory.warning.SetArgs");

    // member variables
    private static String s_host;
    private static int    s_regPort;

    //--------------------------------------------------------------------------
    /**
     * Static initializer block to set the default host and port.
     */
    static
    {
        // set he host and port to their default or Config values
        setHost(null);
        setPort(0);
    }

    //--------------------------------------------------------------------------
    /**
     * Private constructor to stop instantiation.
     */
    private LocalServerFactory()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Set the host to be used by the LocalServerFactory.
     * @param host, the host to use (name ir IP), may not be empty, if null the
     *   default or config value is used.
     * @throws IllegalArgumentException if the host is empty.
     */
    public static void setHost(String host) throws IllegalArgumentException
    {
        if (host == null)
        {
            // get the host from Config, default to localhost
            s_host = Config.getProp(CKfw.SECREG_HOST, "localhost");
        }
        else
        {
            // use the provided host, provided it is not empty
            if (host.length() <= 0)
                throw new IllegalArgumentException(EXCEPTON_HOST_EMPTY.getText());
            s_host = host;
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Set the bootstrap registry port to be used by the LocalServerFactory.
     * @param regPort, the port to use, if <= 0 uses the default or Config port.
     */
    public static void setPort(int regPort)
    {
        if (regPort <= 0)
        {
            // get the port from Config or use default
            s_regPort = Registry.REGISTRY_PORT;
            String sPort = Config.getProp(CKfw.SECREG_BOOTSTRAP_PORT);
            if (sPort != null)
                try
                {
                    s_regPort = Integer.parseInt(sPort);
                }
                catch (Exception e)
                {
                    Log.warning(WARNING_SET_PORT.getText() + " "
                        + CKfw.SECREG_BOOTSTRAP_PORT + " [" + sPort + "]", e);
                }
        }
        else
        {
            // set to the specified port
            s_regPort = regPort;
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Set the default host and port from the command line arguments
     * [-host <hostname>] [-regport <boostrap registry port>].
     * @param args, the command line arguments, if the -host and/or -regport
     *   parameters are not forund or are invalid then the default or config
     *   values are used.
     */
    public static void setArgs(String args[])
    {
        // if there is a -host argument then set it
        String host = Util.getArg(args, "-host");
        if (host != null)
            setHost(host);

        // if there is a -regport argument parse it ansd set the port
        String registryPort = Util.getArg(args, "-regport");

        // if registry port param exists parase it
        if (registryPort != null)
            try
            {
                setPort(Integer.parseInt(registryPort));
            }
            catch (NumberFormatException e)
            {
                Log.warning(WARNING_SET_ARGS.getText()
                    + " [" + registryPort + "]", e);
            }
    }

    //--------------------------------------------------------------------------
    /**
     * Get the help for the -host and -regport arguments.
     */
    public static String getHelp()
    {
        return "Optionally you may use the -host <host name> or -regport <port>"
            + " arguments to set the host and port used to connect to the "
            + "secure registry.";
    }

    //--------------------------------------------------------------------------
    /**
     * Get a reference to the secure registry using the default or config host
     * and regport, or those previously set with setHost(), setPort() or
     * setArgs().
     * @return the SecureRegistry.
     * @throws RemoteException if the bootstrap registry could not be found or
     *   there was an RMI error.
     * @throws NotBoundException if the SecureRegistry could not be found.
     */
    public static SecureRegistry getSecureRegistry()
        throws RemoteException, NotBoundException
    {
        // get the bootstrap registry and return the contained SecureRegistry
        Registry registry = LocateRegistry.getRegistry(s_host, s_regPort);
        return (SecureRegistry) registry.lookup(SecureRegistry.SECURE_REGISTRY_NAME);
    }

    //--------------------------------------------------------------------------
    /**
     * Get a servant from the secure registry using the default or Config values
     * for the host and port of the SecureRegistryServer.
     * @param id, a SecurityId that the SecureRegistry will use to determine if
     *   the eccess is permitted.
     * @param servantName, the name of the servant object to retrieve.
     * @return the servant
     * @throws RemoteException if the bootstrp registry could not be accessed or
     *   there is an RMI error.
     * @throws SecurityException if this user (id) does not have permission.
     * @throws NotBoundException if the servant or the SecureRegistry could not
     *   be found.
     */
    public static RmiServant get(SecurityId id, String servantName)
        throws RemoteException, SecurityException, NotBoundException
    {
        return getSecureRegistry().lookup(id, servantName);
    }
}

//==============================================================================
// end of file LocalServerFactory.java
//==============================================================================
