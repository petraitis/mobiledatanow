/**	$Id: MdnServer.java,v 1.14 2002/10/22 21:21:47 jonc Exp $
 *
 *	MDN Server
 *
 */
package wsl.mdn.server;

import wsl.fw.datasource.DataManager;
import wsl.fw.datasource.RemoteDataManagerServant;
import wsl.fw.remote.LocalServerFactory;
import wsl.fw.remote.RmiServer;
import wsl.fw.remote.SecureRegistry;
import wsl.fw.remote.SecureRegistryServer;
import wsl.fw.remote.TerminatorServant;
import wsl.fw.resource.ResId;
import wsl.fw.resource.ResourceManager;
import wsl.fw.util.CKfw;
import wsl.fw.util.Config;
import wsl.fw.util.Log;
import wsl.fw.util.Util;
import wsl.licence.ActivationKey;
import wsl.licence.LicenceKey;
import wsl.mdn.common.MdnAdminConst;
import wsl.mdn.common.MdnDataManager;
import wsl.mdn.common.MdnResourceManager;
import wsl.mdn.dataview.MdnDataCache;
import wsl.mdn.licence.MdnLicenceManager;

//------------------------------------------------------------------------------
/**
 * Server for MDN to provide remote data access.
 */
public class MdnServer
	extends RmiServer
{
	// version number constant
	public final static String VERSION_NUMBER = "2.09";

	// resources
	public static final ResId
		ERR_DATA_MANAGER	= new ResId ("MdnServer.error.DataManager"),
		TXT_TERMINATE		= new ResId ("MdnServer.txt.terminate"),
		TEXT_STARTING		= new ResId ("MdnServer.txt.starting"),
		TEXT_VERSION		= new ResId ("mdn.versionText"),
		DEBUG_CLOSING_DATA_MANAGER
			= new ResId ("MdnServer.debug.ClosingDataManager"),
		DEBUG_RMI_SERVER_EXITING
			= new ResId ("MdnServer.debug.RmiServerExiting");

	// constants
	// time (in MS) after which a server terminate will be turned into a hard
	// System.exit ()
	public final static long MS_TERMINATE_TIME = 6050;

	//--------------------------------------------------------------------------
	/**
	 * Constructor, inits based on the Config and command line args.
	 * @param args, command line args passed from main ().
	 */
	public
	MdnServer (
	 String args[])
	{
		// pass args to superclass
		super (args);

		/*
		 *	Load Microsoft support stuff if required
		 */
		if (System.getProperty ("os.name").startsWith ("Windows"))
		{
			try
			{
				System.loadLibrary ("mdndcl");		// Disable Console Logoff
				Log.debug ("MDNDCL loaded");

			} catch (Throwable t)
			{
				Log.error ("Failed M$ support: " + t.getMessage ());
			}
		}
	}

	//--------------------------------------------------------------------------
	/**
	 * Register the servants for this server. Called by superclass.
	 */
	protected void
	registerServants ()
	{
		// register servants allowing server to assign ports

		// register the remote data manager so that we can use RemoteDataSource
		registerServant (new RemoteDataManagerServant ());

		// register the RemoteLicenseManagerServant so remote clients can
		// test for valid licenses
		registerServant (new RemoteLicenseManagerServant ());

		// register a terminator for us
		registerServant (new TerminatorServant (this));
	}

	//--------------------------------------------------------------------------
	/**
	 * Main entrypoint.
	 */
	public static void
	main (
	 String args[])
	{
	   // set resource manager
		ResourceManager.set (new MdnResourceManager ());

		// log start and version
		Log.log (TEXT_STARTING.getText () + " " + TEXT_VERSION.getText () + " " + VERSION_NUMBER);

		// set the config to use the MDN config files
		Config.setSingleton (MdnAdminConst.MDN_CONFIG_FILE, true);

		// check that there is a valid Licence, if not show the licence screen
		
		/* comment out MDN License checking --->>>
		try
		{
			// load license key
			LicenceKey lk = MdnLicenceManager.getLicenceKey (MdnLicenceManager.PROD_CODE_MDN_SERVER);

			if (lk == null)
				throw new Exception ("licence key is null." );

			// check if it is valid
			if (!MdnLicenceManager.isValidLicenceKey (lk)){
				System.out.println("licence key is invalid: [" + lk + "]");
				throw new Exception ("licence key is invalid: [" + lk + "]");
			}
				

			// get the activation key
			ActivationKey activationKey = MdnLicenceManager.getActivationKey (lk);

			if (activationKey == null)
				throw new Exception ("activation key is null.");

			// if we get here then there is a valid licence. It may be an
			// expired eval copy, but at least the key has been set
		}
		catch (Exception e)
		{
			e.printStackTrace();
			//System.out.println(e.toString());
			// licence missing or invalid
			// show the eula, if accepted show the licence screen
			//if (MdnLicenceManager.showEula ())
			MdnLicenceManager.showLicenceManager ();
			System.exit (1);
		}
		
		<<<<--- */

		// if stop flag on command line then stop the server
		if (Util.getArg (args, "-stop") != null)
		{
			// set the context, this needs a server context
			Config.getSingleton ().addContext (args, CKfw.RMISERVER_CONTEXT);

			// set the local server factory args
			LocalServerFactory.setArgs (args);

			// stop the server
			try
			{
				TerminatorServant.remoteTerminate (MdnServer.class, MS_TERMINATE_TIME);
				System.out.println (TXT_TERMINATE.getText ());
			}
			catch (Exception e)
			{
				e.printStackTrace ();
			}

			// exit as we were only called to do terminte
			System.exit (0);
		}

		// start the secure registry
		SecureRegistry secReg = SecureRegistryServer.startSecureRegistryServer (args, false);

		if (secReg != null)
		{
			// create an instance of the MDN server , this loads the
			// default server context and inits the LocalServerFactory
			MdnServer server = new MdnServer (args);

			// will be getting DataObjects, so ensure data manager is set
			DataManager.setDataManager (new MdnDataManager ());

			// set the version cache
			DataManager.getDataManager ().setVersionCache (new MdnServerVersionCache ());
			MdnDataCache.setCache (new MdnDataCache (false));

			// place any extra command line parameter parsing here
			// place any extra Config or context loading here

			// set up the notification senders and listeners
			// send notifications for changes to Order
			//   DataManager.getDataManager ().addSenderEntity (??);
			//   DataManager.getDataManager ().addListenerEntity (??)

			// start the notification listener (if required)
			//   DataManager.getDataManager ().startRemoteNotifications ();

			// create and start the schedule manager thread
			int sleepTime = Config.getProp (MdnAdminConst.SCHEDULE_MANAGER_PERIOD, 60000);
			ScheduleManager scheduleManager = new ScheduleManager (sleepTime);
			Thread scheduleManagerThread = new Thread (scheduleManager);
			if (Config.getProp (MdnAdminConst.SCHEDULE_MANAGER_START, 1) == 1)
				scheduleManagerThread.start ();

			// start the MDN server running
			server.runServer ();

			// stop the schedule manager
			scheduleManagerThread.interrupt ();

			// stop the notification listener (if required)
			//   DataManager.getDataManager ().stopRemoteNotifications ();

			// mdn server is exiting, since we set a data manager we should
			// now close it.
			try
			{
				Log.debug (DEBUG_CLOSING_DATA_MANAGER.getText ());
				DataManager.closeAll ();
			}
			catch (Exception e)
			{
				Log.error (ERR_DATA_MANAGER.getText (), e);
			}

			// exiting, stop the secure registry
			SecureRegistryServer.stopSecureRegistryServer (secReg);
		}

		Log.debug ("RmiServer [" + MdnServer.class.getName () + "] "
			+ DEBUG_RMI_SERVER_EXITING.getText ());
	}

	//--------------------------------------------------------------------------
	/**
	 * @return the version number.
	 */
	public static String
	getVersionNumber ()
	{
		return VERSION_NUMBER;
	}
}
