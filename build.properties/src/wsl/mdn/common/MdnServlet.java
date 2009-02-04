/**	$Id: MdnServlet.java,v 1.6 2004/01/06 02:05:36 tecris Exp $
 *
 * Superclass to hold servlet functionality common to all presentations
 *
 */
package wsl.mdn.common;

import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import wsl.fw.datasource.DataManager;
import wsl.fw.resource.ResourceManager;
import wsl.fw.servlet.ServletBase;
import wsl.fw.util.CKfw;
import wsl.fw.util.Config;
import wsl.fw.util.Log;
import wsl.fw.util.Util;

public class MdnServlet extends ServletBase
{
	// presentation servlet session variable and parameter constants
	public final static String SV_USER       = "servlet.user";

	static
	{
		  /*
		   *	Load Microsoft support stuff if required
		   */
		  if (System.getProperty ("os.name").startsWith ("Windows"))
		  {
			  try
			  {
				  System.loadLibrary ("mdndcl");		// Disable Console Logoff
				  System.err.println ("MDNDCL loaded");

			  } catch (Throwable t)
			  {
				  System.err.println ("Failed M$ support: " + t.getMessage ());
			  }
		  }
	}

	//--------------------------------------------------------------------------
	/**
	 * Default constructor.
	 */
	public
	MdnServlet ()
	{
		// set the ResourceManager, do here rather than init as this happens
		// earlier and ResourceManager needs to be inited soonest
		synchronized (ResourceManager.class)
		{
			ResourceManager rm = ResourceManager.get ();

			if (rm == null)
				ResourceManager.set (new MdnResourceManager ());
		}
	}

	//--------------------------------------------------------------------------
	/**
	 * Servlet initialisation.
	 */
	public void
	init (
	 ServletConfig cfg)
		throws ServletException
	{
		// init the MDN config, must do this before super.init () as
		// the superclass uses Config
		Config.setSingleton (MdnAdminConst.MDN_CONFIG_FILE, true);
		Config.getSingleton ().addContext (CKfw.RMICLIENT_CONTEXT);

		// call superclass
		super.init (cfg);

		// will be getting DataObjects, so ensure data manager is set
		synchronized (DataManager.class)
		{
			DataManager dm = DataManager.getDataManager ();
			if (dm == null)
				DataManager.setDataManager (new MdnDataManager ());
			else
				assert dm instanceof MdnDataManager;
		}
	}

	//--------------------------------------------------------------------------
	/**
	 * Servlet shutdown.
	 */
	public void
	destroy ()
	{
		// servlet is being destroyed, since we set a data manager we should
		// now close it.
		try
		{
			Log.debug ("Closing DataManager");
			DataManager.closeAll ();
		}
		catch (Exception e)
		{
			Log.error ("MdnServlet.destroy: DataManager.closeAll failed", e);
		}

		Log.debug ("Servlet [" + getClass ().getName () + "] exiting");
	}

	//--------------------------------------------------------------------------
	/**
	 * Get a unique key to identify locks for the current user's session (as
	 * identified by the session object in the request). Any calls to get a
	 * lock in any MdnServlet should use this function to get the key.
	 * @param request, the HttpServletRequest used to help build the key.
	 * @return the lock key.
	 */
	public static String
	getLockKey (
	 HttpServletRequest request)
	{
		final char SEP = ':';
		StringBuffer buf = new StringBuffer ();
		buf.append (request.getServerName ());
		buf.append (SEP);
		buf.append (request.getServerPort ());
		buf.append (SEP);
		buf.append (request.getSession ().getId ());

		return buf.toString ();
	}
}
