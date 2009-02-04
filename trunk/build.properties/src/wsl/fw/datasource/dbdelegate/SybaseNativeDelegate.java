/**	$Id: SybaseNativeDelegate.java,v 1.2 2003/02/26 03:01:28 tecris Exp $
 *
 *	Sybase native delegate.
 *
 */
package wsl.fw.datasource.dbdelegate;

public class SybaseNativeDelegate
	extends NativeDelegate
{
	public static final String NAME = "Adaptive Server Enterprise";

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
