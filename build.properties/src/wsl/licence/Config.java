/*	$Id: Config.java,v 1.1.1.1 2002/02/22 02:51:56 jonc Exp $
 *
 *	Configuration properties
 *
 */
package wsl.licence;

import java.io.FileInputStream;
import java.util.PropertyResourceBundle;

import java.io.IOException;
import java.util.MissingResourceException;

public class Config
{
	/*
	 *	Magic strings
	 */
	private final static String
		PropDbDriver			= "database.driver",
		PropDbURL				= "database.url",
		PropDbUsername			= "database.username",
		PropDbPassword			= "database.password";

	/*
	 *	State variables
	 */
	private String dbDriver, dbURL, dbUsername, dbPassword;
	private String imageDir;

	/*
	 *
	 */
	public
	Config (
	 String path)
		throws Exception
	{
		/**
			Build up some environment from the property file supplied
		 */
		PropertyResourceBundle p =
			new PropertyResourceBundle (new FileInputStream (path));

		dbDriver		= p.getString (PropDbDriver);
		dbURL			= p.getString (PropDbURL);
		dbUsername		= p.getString (PropDbUsername);
		dbPassword		= p.getString (PropDbPassword);
	}

	/*
	 *
	 */
	public String
	getDbDriver ()
	{
		return dbDriver;
	}

	public String
	getDbURL ()
	{
		return dbURL;
	}

	public String
	getDbUsername ()
	{
		return dbUsername;
	}

	public String
	getDbPassword ()
	{
		return dbPassword;
	}
}
