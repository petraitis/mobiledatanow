/**	$Id: OracleNativeDelegate.java,v 1.2 2002/09/10 01:52:45 jonc Exp $
 *
 * Oracle native delegate.
 */

package wsl.fw.datasource.dbdelegate;

public class OracleNativeDelegate
	extends NativeDelegate
{
	public static final String NAME = "Oracle";

	/**
	 * @return String the type for a datetime for CREATE TABLE
	 */
	public String
	getDateTimeType ()
	{
		return "DATE";
	}
}
