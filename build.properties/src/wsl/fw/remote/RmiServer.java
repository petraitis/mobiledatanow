/**	$Id: RmiServer.java,v 1.4 2004/01/06 01:50:44 tecris Exp $
 *
 * Base class for all RMI servers. An RmiServer contains a set of servant
 * objects and periodically re-registers with the SecureRegistry.
 * Standard usage:
 * Create a concrete subclass.
 * Create an implementation for registerServants which creates the servants
 *   and calls registerServant for each of them.
 * Create a main () function which does any required configuration, constructs
 *   an instance of the concrete subclass, passing command line arguments, and
 *   then calls runServer () on it. If necessary the subclass constructor or
 *   later calls (before runServer) can alter the settings of LocalServerFactory
 *   or the SecurityId.
 * Optionally make config entries for the servant port ranges to be used by the
 *   server subclass.
 *
 */
package wsl.fw.remote;

import java.net.ServerSocket;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import wsl.fw.resource.ResId;
import wsl.fw.security.SecurityException;
import wsl.fw.security.SecurityId;
import wsl.fw.util.CKfw;
import wsl.fw.util.Config;
import wsl.fw.util.Log;
import wsl.fw.util.Util;

public abstract class RmiServer
{
    // resources
    public static final ResId
		ERR_PORT_RANGE	= new ResId ("LocalServerFactory.error.PortRange"),
    	ERR_RUN_SERVER	= new ResId ("LocalServerFactory.error.RunServer"),

    	EXCEPTION_NOT_PARSE1
			= new ResId ("LocalServerFactory.exception.NotParse1"),
    	EXCEPTION_NOT_PARSE2
			= new ResId ("LocalServerFactory.exception.NotParse2"),
    	EXCEPTON_COULD_NOT_BIND
			= new ResId ("LocalServerFactory.exception.CouldNotBind"),
    	ERR_COULD_NOT_EXPORT
			= new ResId ("LocalServerFactory.error.CouldNotExport"),
    	EXCEPTON_SECURE_REGISTRY
			= new ResId ("LocalServerFactory.exception.SecureRegistry"),
    	EXCEPTON_BOOTSTRAP_REGISTRY
			= new ResId ("LocalServerFactory.exception.BootstrapRegistry"),
    	LOG_EXPORTED_ON_PORT1
			= new ResId ("LocalServerFactory.log.ExportedOnPort1"),
    	LOG_EXPORTED_ON_PORT2
			= new ResId ("LocalServerFactory.log.ExportedOnPort2"),

    	DEBUG_BINDING	= new ResId ("LocalServerFactory.debug.Binding"),
    	DEBUG_UNBINDING	= new ResId ("LocalServerFactory.debug.Unbinding"),
    	DEBUG_UNEXPORTING = new ResId ("LocalServerFactory.debug.Unexporting"),
    	WARNING_RANGE_FULL = new ResId ("LocalServerFactory.warning.RangeFull");

    // private member variables
    private   List _servants;

    // protected memeber variables, if the default values are not acceptable
    // the subclass' constructor should set these as required
    protected long       _rebindMS;
    protected SecurityId _securityId;
    private   int        _nextServantPort = 0;
    private   int        _maxServantPort  = 0;
    private   Thread     _rmiServerThread = null;

    //--------------------------------------------------------------------------
    /**
     * Construct the RMI server. This parses command arguments
     * [-host <hostname>] [-regport <bootstrap registry port>] [ -context <ctx>]
     * Note that this sets the singleton LocalServerFactory and adds the context
     * information to the Config singleton.
     * The RMIServer will use the default, context, commandline or Config
     * information to locate the Secure registry. The Config singleton should be
     * properly initialized with any extra information before creating and
     * running the server.
     * @param args, the command line arguments.
     * @see LocalServerFactory.setArgs.
     * @see Config.addContext
     * @param loadContext, if false then the default server context or context
     *   specified in args is not loaded.
     */
    public
	RmiServer (
	 String args [],
	 boolean loadContext)
    {
        // create list to hold our servants
        _servants = new LinkedList ();

        // default security uses the system ID created from Config data
        if (loadContext)
        {
            // load the rmi server context, or that in the command line
            Config.getSingleton ().addContext (args, CKfw.RMISERVER_CONTEXT);
        }

        // load rebind period from Config, else default rebind every 30 seconds
        _rebindMS = Config.getProp (CKfw.RMISERVER_MS_REBIND, 30000);

        // send the args to LocalServerFactory so it can parse them
        LocalServerFactory.setArgs (args);

        // do this after the context is loaded so the config entries for
        // getSystemId have been set
        _securityId = SecurityId.getSystemId ();

        // load servant ports from Config
        String servantPortKey = CKfw.RMISERVANT_PORTRANGE_PREFIX
            + getClass ().getName ();
        String servantPorts = Config.getProp (servantPortKey);
        // if there is a port range specified parse and set it
        if (servantPorts != null)
            try
            {
                StringTokenizer st = new StringTokenizer (servantPorts, "-");
                String sMin = st.nextToken ();
                String sMax = st.nextToken ();
                int min = Integer.parseInt (sMin);
                int max = Integer.parseInt (sMax);

                // check for validity
                if (min <= 0 || max <= 0 || max < min)
                    Log.error (servantPortKey + "=" + servantPorts
                        + " " + ERR_PORT_RANGE.getText ());
                else
                    {
                        // valid, set the range
                        _nextServantPort = min;
                        _maxServantPort  = max;
                    }
            }
            catch (Exception e)
            {
                // parsing failed, log it
                Log.error (EXCEPTION_NOT_PARSE1.getText () + " " + servantPortKey
                    + "=" + servantPorts + ", " + EXCEPTION_NOT_PARSE2.getText () + " " + e.toString ());
            }
    }

    //--------------------------------------------------------------------------
    /**
     * As above, but loads the context by default.
     */
    public
	RmiServer (
	 String args [])
    {
        this (args, true);
    }

    //--------------------------------------------------------------------------
    /**
     * Call on the constructed RMIServer subclass after all initialisation is
     * finished. This function will not return unless it is interrupted.
     */
    protected void
	runServer ()
    {
        // save this thread as the server thread
        _rmiServerThread = Thread.currentThread ();

        // Create and install a security manager
        System.setSecurityManager (new OpenSecurityMgr ());

        // if non-default host and regport have been set then set them into
        // the LocalServerFactory

        // call abstract function to get the subclass to register its servants
        registerServants ();

        // abort if there are no servants
        if (_servants.size () <= 0)
        {
            Log.error (ERR_RUN_SERVER.getText ());
            return;
        }

        // periodically bind the servants in the SecureRegistry
        try
        {
            while (true)
            {
                bindServants (true);
                Thread.sleep (_rebindMS);
            }
        }
        catch (InterruptedException e)
        {
        }
        finally
        {
            _rmiServerThread = null;

            // before exiting unbind and unexport all servants
            bindServants (false);
            unexportServants ();
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Stop the currently executing rmi server thread.
     */
    public void
	stopServer ()
    {
        if (_rmiServerThread != null)
            _rmiServerThread.interrupt ();
    }

    //--------------------------------------------------------------------------
    /**
     * Abstract function to be overridden in subclasses. Called by runServer.
     */
    protected abstract void
	registerServants ();

    //--------------------------------------------------------------------------
    /**
     * Register a servant with the server. This causes the servant to be
     * exported and later bound in the SecureRegistry.
     * The concrete RmiServer subclass should call this function to register
     * each of its servants. The servant is bound on a port in the range
     * specified by the <RMISERVANT_PORTRANGE_PREFIX + the server classname>
     * key in the Config, the value is <min port>-<max port>. Most servers
     * should use this function rather than explicitly setting the port.
     * @param servant, the servant to register, not null.
     * @throws IllegalArgument or AssertFailed exception if the parameters are
     *   invalid.
     */
    protected void
	registerServant (
	 RmiServantBase servant)
    {
        // get the next available port then delegate
        registerServant (servant, getNextServantPort ());
    }

    //--------------------------------------------------------------------------
    /**
     * Register a servant with the server. This causes the servant to be
     * exported and later bound in the SecureRegistry.
     * The concrete RmiServer subclass should call this function to register
     * each of its servants.
     * @param servant, the servant to register, not null.
     * @param servantPort, the port on which to export the servant, or 0 to
     * use a default system assigned port, not < 0.
     * @throws IllegalArgument exception or AssertFailed if the parameters are
     *   invalid.
     */
    protected void
	registerServant (
	 RmiServantBase servant,
	 int servantPort)
    {
        // check param
        Util.argCheckNull (servant);
        assert servantPort >= 0: "servantPort >= 0";

        try
        {
            // export the servant
            UnicastRemoteObject.exportObject (servant, servantPort);
            // if successful add it to the servant list
            _servants.add (servant);
            Log.log (LOG_EXPORTED_ON_PORT1.getText () + " " + servant.getBindName ()
                + " " + LOG_EXPORTED_ON_PORT2.getText () + " " + servantPort);
        }
        catch (RemoteException e)
        {
            Log.error (ERR_COULD_NOT_EXPORT.getText () + " "
                + servant.getClass ().getName (), e);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Bind or unbind all registered servants with the SecureRegistry.
     * @param bBind, if true the servants are bound, if false they are unbound.
     */
    private void
	bindServants (
	 boolean bBind)
    {
        // get the SecureRegistry, if there is an error log it and return,
        // but do not abort as the SecureRegistry may only be temporarily
        // unavailable
        SecureRegistry sr = null;
        try
        {
            sr = LocalServerFactory.getSecureRegistry ();
        }
        catch (NotBoundException nbe)
        {
            Log.error (EXCEPTON_SECURE_REGISTRY.getText (), nbe);
            return;
        }
        catch (RemoteException re)
        {
            Log.error (EXCEPTON_BOOTSTRAP_REGISTRY.getText (), re);
            return;
        }

        // iterate all servants binding or unbinding them
        for (int i = 0; i < _servants.size (); i++)
            try
            {
                RmiServantBase servant = (RmiServantBase) _servants.get (i);

                if (bBind)
                {
                    sr.bind (_securityId, servant.getBindName (), servant);
                    Log.debug (DEBUG_BINDING.getText () + " "
                        + servant.getBindName ());
                }
                else
                {
                    sr.unbind (_securityId, servant.getBindName (), servant);
                    Log.debug (DEBUG_UNBINDING.getText () + " "
                        + servant.getBindName ());
                }
            }
            catch (Exception e)
            {
                Log.error (EXCEPTON_COULD_NOT_BIND.getText () + " "
                    + _servants.get (i).getClass ().getName (), e);
            }
    }

    //--------------------------------------------------------------------------
    /**
     * Unexport all servants in the list.
     */
    protected void
	unexportServants ()
    {
        // iterate all servants unexporting them
        for (int i = 0; i < _servants.size (); i++)
        {
            RmiServantBase servant = (RmiServantBase) _servants.get (i);
            Log.debug (DEBUG_UNEXPORTING.getText () + " "
                + servant.getBindName ());

            // as a part of the servant unexport/shutdown, tell it it is being
            // terminated
            servant.terminate ();

            // soft unexport in separate thread
            UnexportThread uet = new UnexportThread (servant);
            uet.start ();
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Get the next available port in the range. If no range is specified or the
     * range is full then 0 (indicating a system allocated port) is returned.
     * A warning is logged if a port is not available.
     * @returns the port number.
     */
    private int
	getNextServantPort ()
    {
        // _nextServantPort == 0 indicates no defined range, so use 0 which
        // indicates system assinged ports
        if (_nextServantPort == 0)
            return 0;

        // iterate through port range, return first free port
        while (_nextServantPort <= _maxServantPort)
        {
            int port = _nextServantPort++;
            if (isPortFree (port))
                return port;
        }

        // if we reach here then the range is full, log a warning and return 0
        Log.warning (getClass ().getName () + WARNING_RANGE_FULL.getText ());
        return 0;
    }

    //--------------------------------------------------------------------------
    /**
     * Check if the specified port is free (i.e. can be listened on).
     * @param the port number to check.
     * @return true if the port is free.
     */
    public static boolean
	isPortFree (
	 int port)
    {
        try
        {
            // try opening the port
            ServerSocket sock = new ServerSocket (port);
            // success, free port and return true
            sock.close ();
            return true;
        }
        catch (Exception e)
        {
        }

        // failure
        return false;
    }
}
