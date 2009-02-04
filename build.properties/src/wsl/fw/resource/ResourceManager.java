/**	$Id: ResourceManager.java,v 1.4 2002/07/18 03:29:57 jonc Exp $
 *
 * Singleton Manager of Locale Resource elements in an application.
 * Note that since the ResourceManager is used by almost all classes it is not
 * permitted to depend on any but the most simple and atomic classes that
 * have not complex dependencies.
 *
 * Specifically ResourceManager should not use Log, Config or any DataObjects.
 *
 */
package wsl.fw.resource;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import wsl.fw.resource.ResId;

public class ResourceManager
{
	// attributes ----------------------------------
	private final static String
		BASE_BUNDLE	= "wsl.fw.resource.strings.fw";

	/**
	 * The resource manager singleton.
	 */
	private static ResourceManager s_singleton = null;

	/**
	 * Set of bundles.
	 */
	private Locale _locale;
	private Hashtable _resBundles = new Hashtable ();

	// constructor----------------------------------
	/**
	 * This constructor adds framework bundle.
	 */
	public
	ResourceManager ()
	{
		_locale = Locale.getDefault ();
		iAddBundle (BASE_BUNDLE);
	}

	public
	ResourceManager (
	 Locale locale)
	{
		_locale = locale;
		iAddBundle (BASE_BUNDLE);
	}

	// methods -------------------------------------

	/**
	 * Sets a subclass ResourceManager as the singleton.
	 * @param rm The subclass ResourceManager object.
	 */
	public static void
	set (
	 ResourceManager rm)
	{
		s_singleton = rm;
	}

	/**
	 * Returns the ResourceManager singleton.
	 * Creates a DataManager if no subclass singleton has been set.
	 * @return The DataManager singleton.
	 */
	public static ResourceManager
	get ()
	{
		return s_singleton;
	}

	/**
	 * This method adds new Bundle to list of Bundles.
	 * @param resBundle Full name of resource class.
	 * @throws MissingResourceException if the resbundle cannot be loaded.
	 */
	public static void
	addBundle (
	 String resBundle)
		throws MissingResourceException
	{
		if (get () == null)
		{
			throw new RuntimeException (
				"ResourceManager.addBundle, ResourceManager not set");
		}
		get ().iAddBundle (resBundle);
	}

	/**
	 * This protected method adds new Bundle to list of Bundles.
	 * @param resBundle Full name of resource class.
	 * @throws MissingResourceException if the resbundle cannot be loaded.
	 */
	protected void
	iAddBundle (
	 String resBundle)
		throws MissingResourceException
	{
		ResourceBundle res = ResourceBundle.getBundle (resBundle, _locale);
		_resBundles.put (resBundle, res);
	}

	/**
	 * This method returns text of resource in default languge.
	 * @param id Resource id.
	 * @return Resource string in Locale.
	 * @throws MissingResourceException if the string cannot be loaded.
	 */
	public static String
	getText (
	 ResId id)
		throws MissingResourceException
	{
		if (get () == null)
		{
			throw new RuntimeException (
				"ResourceManager.addBundle, ResourceManager not set");
		}
		return get ().iGetText (id);
	}

	/**
	 * This protected method returns text of resource in default languge.
	 * @param id Resource id.
	 * @return Resource string in Locale.
	 * @throws MissingResourceException if the string cannot be loaded.
	 */
	protected String
	iGetText (
	 ResId id)
		throws MissingResourceException
	{
		String localString = null;
		Enumeration keys = _resBundles.keys ();
		String key;

		// Find resource in all registed bundles from hashtable _resBundles
		while (keys.hasMoreElements ())
		{
			key = (String)keys.nextElement ();
			try
			{
				ResourceBundle res = (ResourceBundle)_resBundles.get (key);
				// Resource is found
				localString = res.getString (id.getId ());
				break;

			} catch (MissingResourceException e)
			{
				// not found, continue looking
			}
		}

		if (localString == null)
		{
			// There isn't resource among all registed bundles
			String msg = "ResourceManager.iGetText, could not find string: "
							+ id.getId ();

			throw new MissingResourceException (
						msg, getClass ().getName (), id.getId ());
		}

		return localString;
	}

	/**
	 *
	 *	Refetch all bundles for a new Locale
	 *
	 */
	public static void
	setLocale (
	 Locale locale)
	{
		if (s_singleton == null)
			return;									// nothing there yet..

		if (s_singleton._locale.equals (locale))
			return;									// save the CPU cycles

		/*
		 *	Create a new substitute
		 */
		ResourceManager localised = new ResourceManager (locale);

		/*
		 *	Work thru' the Hashtable for existing keys
		 */
		Enumeration e = s_singleton._resBundles.keys ();
		while (e.hasMoreElements ())
		{
			String resBundle = (String) e.nextElement ();
			if (!resBundle.equals (BASE_BUNDLE))
			{
				try
				{
					localised.iAddBundle (resBundle);

				} catch (MissingResourceException ex)
				{
					/*
					 *	Let somebody else handle the problem
					 */
				}
			}
		}
		set (localised);
	}
}
