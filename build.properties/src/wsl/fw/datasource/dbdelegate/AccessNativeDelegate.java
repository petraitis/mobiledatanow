/**
 * MS Access native delegate.
 */
package wsl.fw.datasource.dbdelegate;

public class AccessNativeDelegate
	extends NativeDelegate
{
	public static final String NAME = "ACCESS";

	/**
	 * @return String the type for a memo for CREATE TABLE
	 */
	public String
	getMemoType (
	 int size)
	{
		return "MEMO";
	}

	/**
	 * @return String the type for a datetime for CREATE TABLE
	 */
	public String
	getDateTimeType ()
	{
		return "DATETIME";
	}

	/**
	 * @return boolean true if we use parameterised
	 */
	public boolean
	useParamUpdate ()
	{
		return false;
	}
}
