/**	$Id: SecureRegistryServer.java,v 1.4 2002/12/10 04:20:48 tecris Exp $
 *
 * Implementation of the SecureRegistry servant and the server to run it.
 * The secure registry is an access controlled RMI registry that permits
 * bind and unbind from remote hosts.
 *
 * The server starts a standard RMI registry and registers itself under a known
 * name to allow bootstrapp access to clients.
 *
 */
package wsl.fw.remote;

import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import wsl.fw.resource.ResId;
import wsl.fw.resource.ResourceManager;
import wsl.fw.security.BootstrapSecurityManager;
import wsl.fw.security.SecurityException;
import wsl.fw.security.SecurityId;
import wsl.fw.util.CKfw;
import wsl.fw.util.Config;
import wsl.fw.util.Log;
import wsl.fw.util.Util;

public class SecureRegistryServer
	implements SecureRegistry
{
	// resources
	public static final ResId
		DEBUG_BOUND		= new ResId ("SecureRegistryServer.debug.Bound"),
		DEBUG_UNBOUND	= new ResId ("SecureRegistryServer.debug.Unbound"),

		EXCEPTION_COULD_NOT_FIND
			= new ResId ("SecureRegistryServer.exception.CouldNotFind"),
		EXCEPTION_REGISTRY_PORT
			= new ResId ("SecureRegistryServer.exception.RegistryPort"),
		EXCEPTION_SECURE_REGISTRY
			= new ResId ("SecureRegistryServer.exception.SecureRegistry"),
		EXCEPTION_BOOTSTRAP_REGISTRY
			= new ResId ("SecureRegistryServer.exception.BootstrapRegistry"),
		EXCEPTION_ANOTHER_REGISTRY
			= new ResId ("SecureRegistryServer.exception.AnotherRegistry"),
		EXCEPTION_NOT_CREATE
			= new ResId ("SecureRegistryServer.exception.NotCreate"),

		LOG_LISTENING	= new ResId ("SecureRegistryServer.log.Listening"),
		LOG_BOUND		= new ResId ("SecureRegistryServer.log.Bound");

	// constants
	// security property name for SecureRegistryServer
	private final static String SECURITY_PROPERTY = "SecureRegistry.access";
	// READ and WRITE security property values
	private final static String READ = "READ";
	private final static String WRITE = "WRITE";

	private final static String NEG_PORT = "SecureRegistry.negPort";

	// member variables
	private Map _servantNameMap = null;
	private BootstrapSecurityManager _security = null;

	//--------------------------------------------------------------------------
	/**
	 * Private constructor, the only way to create an instance is with main ().
	 */
	private
	SecureRegistryServer ()
	{
		// create the hashtable that stores the object bindings.
		// note HashMap is not synchronized, so we need to sync our methods
		_servantNameMap = new HashMap ();

		// create the BootstrapSecurityManager that uses security info in config
		_security = new BootstrapSecurityManager ();
	}

	//--------------------------------------------------------------------------
	/**
	 * Check if the caller has the named security privilege.
	 * @param caller, the identity of the caller performing this operation.
	 * @param priv, the name of the pricilege to check.
	 * @throws RemoteException if there is an RMI error.
	 * @throws SecurityException if the caller does not have the privilege.
	 */
	public void
	securityCheck (
	 SecurityId caller,
	 String priv)
		throws RemoteException, SecurityException
	{
		// check security against the priv
		_security.check (caller, SECURITY_PROPERTY, priv);
	}

	//--------------------------------------------------------------------------
	/**
	 * Create a secure registry binding between the name and the servant.
	 * @param caller, the identity of the caller performing this operation.
	 * @param name, the name that will be bound to the servant.
	 * @param obj, the servant object.
	 * @throws RemoteException if there is an RMI error.
	 * @throws SecurityException if the caller does not have permission to
	 *   perform this function.
	 * @throws IllegalArgumentException if a parameter is invalid.
	 */
	public synchronized void
	bind (
	 SecurityId caller,
	 String name,
	 RmiServant obj)
		throws RemoteException, SecurityException
	{
		// check security against WRITE feature
		_security.check (caller, SECURITY_PROPERTY, WRITE);

		// check for illegal names
		Util.argCheckEmpty (name);
		// check for null servant
		Util.argCheckNull (obj);

		// place in binding map, first get the servant set
		ServantSet ss = (ServantSet) _servantNameMap.get (name);
		if (ss == null)
		{
			// no servant set, create a new one and place it in the map
			ss = new ServantSet (name);
			_servantNameMap.put (name, ss);
		}

		// now add the servant to the servant set
		ss.add (obj);
		Log.debug (DEBUG_BOUND.getText () + " " + name);
	}

	//--------------------------------------------------------------------------
	/**
	 * Returns an array of the names bound in the registry.
	 * @param caller, the identity of the caller performing this operation.
	 * @return an array of strings naming the available servants.
	 * @throws RemoteException if there is an RMI error.
	 * @throws SecurityException if the caller does not have permission to
	 *   perform this function.
	 */
	public synchronized String []
	list (
	 SecurityId caller)
		throws RemoteException, SecurityException
	{
		// check security against READ feature
		_security.check (caller, SECURITY_PROPERTY, READ);

		// get the set of keys (bound names)
		Set nameSet = _servantNameMap.keySet ();
		// convert the set to an array and return it
		String nameList[] = (String[]) nameSet.toArray (new String[0]);

		return nameList;
	}

	//--------------------------------------------------------------------------
	/**
	 * Returns a reference (stub) for the remote object associated with the
	 * specified name. Note that in the case of load balaning this may not be
	 * the only RmiServant instance bound to that name, nor are subsequent calls
	 * guaranteed to get the same object.
	 * @param caller, the identity of the caller performing this operation.
	 * @param name, the name of the servant to get.
	 * @return the matching RmiServant, not null.
	 * @throws RemoteException if there is an RMI error.
	 * @throws SecurityException if the caller does not have permission to
	 *   perform this function.
	 * @throws NotBoundException if the named servant could not be found.
	 */
	public synchronized RmiServant
	lookup (
	 SecurityId caller,
	 String name)
		throws RemoteException, SecurityException, NotBoundException
	{
		// check security against READ feature
		_security.check (caller, SECURITY_PROPERTY, READ);

		// check for illegal names
		Util.argCheckEmpty (name);

		// look name up in map
		RmiServant servant = null;
		ServantSet ss = (ServantSet) _servantNameMap.get (name);

		// get the next available servant
		if (ss != null)
			servant = ss.getNext ();

		// if there is no servant then remove the name binding and throw
		if (servant == null)
		{
			// option, we could allow the servant to be started on demand
			_servantNameMap.remove (name);
			throw new NotBoundException (EXCEPTION_COULD_NOT_FIND.getText () + " " + name);
		}

		// return the servant
		return servant;
	}

	//--------------------------------------------------------------------------
	/**
	 * Returns an array of references (stubs) for all the remote objects
	 * associated with the specified name.
	 * @param caller, the identity of the caller performing this operation.
	 * @return the array of servants, not null or empty.
	 * @throws RemoteException if there is an RMI error.
	 * @throws SecurityException if the caller does not have permission to
	 *   perform this function.
	 * @throws NotBoundException if there are no servants for the name.
	 */
	public synchronized RmiServant []
	lookupAll (
	 SecurityId caller,
	 String name)
		throws RemoteException, SecurityException, NotBoundException
	{
		// check security against READ feature
		_security.check (caller, SECURITY_PROPERTY, READ);

		// check for illegal names
		Util.argCheckEmpty (name);

		// get the set of servants
		RmiServant servants[] = null;
		ServantSet ss = (ServantSet) _servantNameMap.get (name);
		if (ss != null)
			servants = ss.getAll ();

		// throw if no servants
		if (servants == null || servants.length <= 0)
			throw new NotBoundException (EXCEPTION_COULD_NOT_FIND.getText () + " " + name);
			// option, we could allow the servant to be started on demand

		return servants;
	}

	//--------------------------------------------------------------------------
	/**
	 * Destroys the binding between the specified name and its associated
	 * remote object in the secure registry.
	 * @param caller, the identity of the caller performing this operation.
	 * @param name, the name of the object to remove bindings for.
	 * @param obj, the servant to remove. If null then all servants bound to
	 *   that name are removed.
	 * @throws RemoteException if there is an RMI error.
	 * @throws SecurityException if the caller does not have permission to
	 *   perform this function.
	 */
	public synchronized void
	unbind (
	 SecurityId caller,
	 String name,
	 RmiServant servant)
		throws RemoteException, SecurityException
	{
		// check security against WRITE feature
		_security.check (caller, SECURITY_PROPERTY, WRITE);

		// check for illegal names
		Util.argCheckEmpty (name);

		// if servant is null then remove all servants
		if (servant == null)
			_servantNameMap.remove (name);
		else
		{
			// remove the specified servant
			ServantSet ss = (ServantSet) _servantNameMap.get (name);
			if (ss != null)
				ss.remove (servant);

			// if no servant set or it is empty remove from map
			if (ss == null || ss.size () <= 0)
				_servantNameMap.remove (name);
		}

		Log.debug (DEBUG_UNBOUND.getText () + " " + name);
	}

	//--------------------------------------------------------------------------
	/**
	 * Disply a help message
	 */
	private static void
	displayHelp ()
	{
		System.out.println ("");
		System.out.println ("Syntax is : java "
			+ "wsl.fw.remote.SecureRegistryServer"
			+ " [-help] [- port <srp>] [-regport <rp>]");
		System.out.println ("Where <srp> and <rp> are the ports on which the secure");
		System.out.println ("registry and the standard registry listen.");
		System.out.println ("<rp> defaults to " + Registry.REGISTRY_PORT);
	}

	//--------------------------------------------------------------------------
	/**
	 * Start the rmi registry and secure registry.
	 * The caller must set the resource manager and set nay config files
	 * before calling. This function will add the SECREG_CONTEXT (or override
	 * the context from the command line if allowContextChange is true).
	 * @param args, command line args from the calling main (). Used to get
	 *   commandline overides for context and ports.
	 * @param allowContextChange, if true the commandline can change the
	 *   context.
	 * @return the SecureRegistry, which has been registered and started, null
	 *   on failure. Any failures are logged before return. The caller may
	 *   unexport the returned SecureRegistry to deactivate it.
	 */
	public static SecureRegistry
	startSecureRegistryServer (
	 String args[],
	 boolean allowContextChange)
	{
		// first check the context, loading the default context or that
		// specified on the command line

		if (allowContextChange)
			Config.getSingleton ().addContext (args, CKfw.SECREG_CONTEXT);
		else
			Config.getSingleton ().addContext (CKfw.SECREG_CONTEXT);

		// get the various options from the command line using -port and
		// -regport, Config or the default
		int registryPort = Registry.REGISTRY_PORT;
		int secureServerPort = 0;
		int negPort = 0;
		String sRegistryPort = Util.getArg (args, "-regport",
			Config.getProp (CKfw.SECREG_BOOTSTRAP_PORT));
		String sSecureServerPort = Util.getArg (args, "-port",
			Config.getProp (CKfw.SECREG_PORT));
		String sNegPort = Config.getProp (NEG_PORT);

		// if there is a non-default value then parse and set it
		if (sRegistryPort != null)
			try
			{
				registryPort = Integer.parseInt (sRegistryPort);
			}
			catch (NumberFormatException e)
			{
				Log.error (EXCEPTION_REGISTRY_PORT.getText (), e);
			}
		if (sSecureServerPort != null)
			try
			{
			secureServerPort = Integer.parseInt (sSecureServerPort);
			}
			catch (NumberFormatException e)
			{
				Log.error (EXCEPTION_SECURE_REGISTRY.getText (), e);
			}
		if (sNegPort != null)
			try
			{
			negPort = Integer.parseInt (sNegPort);
			}
			catch (NumberFormatException e)
			{
				Log.error (EXCEPTION_SECURE_REGISTRY.getText (), e);
			}

		Registry registry = null;

		// Create and install a security manager
		System.setSecurityManager (new OpenSecurityMgr ());

		try
		{
			// start the registry
			try
			{
				registry = LocateRegistry.createRegistry (registryPort);
				Log.log (EXCEPTION_BOOTSTRAP_REGISTRY.getText ()
					+ " " + registryPort);
			}
			catch (RemoteException e)
			{
				Log.fatal (EXCEPTION_ANOTHER_REGISTRY.getText (), e);
				return null;
			}

			// create the secure registry
			SecureRegistry secureRegistry = new SecureRegistryServer ();

			// export it on the desired port
			RMISocketFactory.setSocketFactory (new RMIPort (negPort));
			UnicastRemoteObject.exportObject (secureRegistry, secureServerPort);
			Log.log (LOG_LISTENING.getText () + " "
				+ secureServerPort);

			// bind it in the registry
			registry.rebind (SECURE_REGISTRY_NAME, secureRegistry);
			Log.log (LOG_BOUND.getText ());

			return secureRegistry;
		}
		catch (Exception e)
		{
			Log.fatal (EXCEPTION_NOT_CREATE.getText (), e);
			return null;
		}
	}

	//--------------------------------------------------------------------------
	/**
	 * Stop the secure registry server.
	 */
	public static void
	stopSecureRegistryServer (
	 SecureRegistry secReg)
	{
		if (secReg != null)
		{
			try
			{
				// unexport the secure registry
				UnicastRemoteObject.unexportObject (secReg, true);
			}
			catch (Exception e)
			{
				// exiting, don't care
			}
		}
	}

	//--------------------------------------------------------------------------
	/**
	 * Main method, instantiate the server and bind it.
	 * @param args, command line arguments.
	 */
	public static void
	main (
	 String args [])
	{
		// set resource manager
		ResourceManager.set (new ResourceManager ());

		// check for -help
		if (Util.getArg (args, "-help") != null)
		{
			displayHelp ();
			System.exit (-1);
		}

		// start the secure registry
		SecureRegistry secReg = startSecureRegistryServer (args, true);

		// if successful wait until we are told to terminate
		if (secReg != null)
		{
			try
			{
				while (true)
					Thread.sleep (10000);
			}
			catch (InterruptedException e)
			{
			}

			stopSecureRegistryServer (secReg);
		}
	}
}
