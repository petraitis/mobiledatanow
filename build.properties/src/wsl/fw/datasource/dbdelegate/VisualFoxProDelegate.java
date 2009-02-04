/**	$Id: VisualFoxProDelegate.java,v 1.3 2002/09/23 23:05:00 jonc Exp $
 *
 *	Visual FoxPro native delegate.
 *
 */
package wsl.fw.datasource.dbdelegate;

public class VisualFoxProDelegate
	extends NativeDelegate
{
	public static final String NAME = "Visual FoxPro";

	/**
	 *	@return String column name after any db specific limitations
	 */
	public String
	mungFieldName (
	 String columnname)
	{
		return columnname;
	}

	/**
	 * Don't use parameterised selects. There is a problem with bit-fields
	 * type conversion...
	 *
	 * @return boolean false
	 */
	public boolean
	useParamSelect ()
	{
		return false;
	}
}
