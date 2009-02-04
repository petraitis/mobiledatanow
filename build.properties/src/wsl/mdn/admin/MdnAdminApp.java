/**	$Id: MdnAdminApp.java,v 1.5 2002/07/10 23:34:13 jonc Exp $
 *
 *	Administration
 *
 */
package wsl.mdn.admin;

import javax.help.HelpSetException;
import java.rmi.RMISecurityManager;

import wsl.fw.datasource.DataManager;
import wsl.fw.gui.GuiManager;
import wsl.fw.gui.WslSwingApplication;
import wsl.fw.help.HelpManager;
import wsl.fw.remote.LocalServerFactory;
import wsl.fw.resource.ResId;
import wsl.fw.resource.ResourceManager;
import wsl.fw.util.CKfw;
import wsl.fw.util.Config;
import wsl.fw.util.Log;
import wsl.mdn.common.MdnAdminConst;
import wsl.mdn.common.MdnDataManager;
import wsl.mdn.common.MdnResourceManager;
import wsl.mdn.common.MdnResourceManager;
import wsl.mdn.dataview.MdnDataCache;
import wsl.mdn.server.MdnServer;
import wsl.mdn.server.LicenseManager;

//------------------------------------------------------------------------------
/**
 * Main application object for MDN Administrator.
 */
public class MdnAdminApp extends wsl.fw.gui.WslSwingApplication
{
	// resources
	// public static final ResId ERR_HELP   = new ResId ("MdnAdmin.err.Help");
	public static final ResId
		ERR_LICENSE1	= new ResId ("MdnAdminApp.err.license1"),
		ERR_LICENSE2	= new ResId ("MdnAdminApp.err.license2"),
		TEXT_STARTING	= new ResId ("MdnAdminApp.text.starting"),
		TEXT_VERSION	= new ResId ("mdn.versionText"),
		ERR_VERSION1	= new ResId ("MdnAdminApp.err.version1"),
		ERR_VERSION2	= new ResId ("MdnAdminApp.err.version2"),
		ERR_WILL_EXIT	= new ResId ("MdnAdminApp.err.willExit"),
		ERR_NO_SERVER	= new ResId ("MdnAdminApp.err.noServer");

	//--------------------------------------------------------------------------
	/**
	 * Constructs and sets application, app frame, data manager and gui manager.
	 */
	public
	MdnAdminApp (
	 String args [])
	{
		// call base class to set args
		super (args);

		/*
		 *	Set the ResourceManager
		 * (must be first as everything uses resource strings)
		 */
		ResourceManager.set (new MdnResourceManager ());

		// log start and version
		Log.log (
			TEXT_STARTING.getText ()
			+ " " + TEXT_VERSION.getText ()
			+ " " + MdnServer.VERSION_NUMBER);

		// set the config (must be second as nearly everything uses configs)
		Config.setSingleton (MdnAdminConst.MDN_CONFIG_FILE, true);
		Config.getSingleton ().addContext (CKfw.RMICLIENT_CONTEXT);

		// let LocalServerFactory check for its command line args
		// setting up local server factory (must be before DataManager or
		// anything elese that may remote) end
		LocalServerFactory.setArgs (args);

		// set the help manager
		try
		{
			HelpManager.set (new MdnAdminHelpManager ());
		}
		catch (HelpSetException e)
		{
			Log.error ("MdnAdminApp ctor (HelpManager): " + e.toString ());
		}

		// set the DataManager
		DataManager.setDataManager (new MdnDataManager ());

		// set the GuiManager
		GuiManager.setGuiManager (new MdnAdminGuiManager ());

		// set the data cache
		MdnDataCache.setCache (new MdnDataCache (false));

		// set the ApplicationFrame
		setApplicationFrame (new MdnAdminFrame ());
	}

	//--------------------------------------------------------------------------
	/**
	 * Overridden to ensure the DataManager is closed.
	 *
	 */
	protected void
	onExitApplication ()
	{
		// we have set a DataManager, so here we ensure it is closed properly.
		try
		{
			Log.debug ("MdnAdminApp.onExitApplication");
			DataManager.closeAll ();
		}
		catch (Exception e)
		{
			Log.error ("MdnAdminApp.onExitApplication, DataManager.closeAll ", e);
		}

		// pass on to superclass which will exit the app
		super.onExitApplication ();
	}

	//--------------------------------------------------------------------------
	/**
	 * main () function. Create the singleton and show the application
	 */
	public static void
	main (
	 String [] args)
	{
		// construct the application
		MdnAdminApp app = new MdnAdminApp (args);

		// do version check
		String serverVersion = LicenseManager.getServerVersion ();

		if (serverVersion == null)
		{
			// cound not connect toserver, display error and exit
			String [] errorMsg =
			{
				ERR_NO_SERVER.getText (),
				ERR_WILL_EXIT.getText ()
			 };
			GuiManager.showErrorDialog (null, errorMsg, null);

			app.exitApplication ();
		}

		if (!MdnServer.VERSION_NUMBER.equals (serverVersion))
		{
			// invalid version, display error and exit
			String [] errorMsg =
			{
				ERR_VERSION1.getText (),
				ERR_VERSION2.getText ()
					+ "[ " + serverVersion
					+ " / " + MdnServer.VERSION_NUMBER + " ]",
				ERR_WILL_EXIT.getText ()
			 };
			GuiManager.showErrorDialog (null, errorMsg, null);

			app.exitApplication ();
		}

		// do license check after MdnAdminApp ctor as this sets all singletons
		int rvLicense = LicenseManager.isLicenseValid ();
		if (rvLicense < 0)
		{
			// invalid license, display error and exit
			String [] errorMsg =
			{
				ERR_LICENSE1.getText (),
				LicenseManager.getErrorDescription (rvLicense),
				ERR_LICENSE2.getText ()
			 };
			GuiManager.showErrorDialog (null, errorMsg, null);

			app.exitApplication ();
		}
		else
		{
			// valid license, show the app
			app.show ();
		}
	}


	//--------------------------------------------------------------------------
	// module permissions
	public static final int FID_MSG_SERVERS = 10000;


	/**
	 * Check to see if a feature is enabled
	 * @param featureid the id of the feature
	 * @return true if feature is enabled
	 */
	public static boolean
	isFeatureEnabled (
	 int featureId)
	{
		// all enabled currently
		return true;
	}
}
