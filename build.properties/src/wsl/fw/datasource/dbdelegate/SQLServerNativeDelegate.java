/**	$Id: SQLServerNativeDelegate.java,v 1.3 2002/09/10 01:52:45 jonc Exp $
 *
 *	Microsoft SQL Server native delegate.
 *
 */
package wsl.fw.datasource.dbdelegate;

public class SQLServerNativeDelegate
	extends NativeDelegate
{
	public static final String NAME = "Microsoft SQL Server";

	/**
	 * @return String the type for a datetime for CREATE TABLE
	 */
	public String
	getDateTimeType ()
	{
		return "DATETIME";
	}

	/**
	 * @return String the type for a DECIMAL for CREATE TABLE
	 */
	public String
	getDecimalType ()
	{
		return "FLOAT";
	}

	/**
	 * @return boolean true if we use parameterised select
	 */
	public boolean
	useParamSelect ()
	{
		return false;
	}

	/**
	 * @return boolean true if we use parameterised update and insert
	 */
	public boolean
	useParamUpdate ()
	{
		return false;
	}
}
