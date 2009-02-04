/**	$Id: InformixSENativeDelegate.java,v 1.2 2002/09/10 01:52:45 jonc Exp $
 *
 *	Informix SE Native delegate.
 *
 */
package wsl.fw.datasource.dbdelegate;

public class InformixSENativeDelegate
	extends InformixNativeDelegate
{
	public static final String NAME = "Informix-SQL";

	/**
	 *  Informix SE doesn't support VARCHARS
	 */
	public String
	getVarcharType (
	 int size)
	{
		return "CHAR (" + size + ")";
	}

	/**
	 *  @return String truncate to limit imposed by SE
	 */
	public String
	mungFieldName (
	 String name)
	{
		final int MAXLENGTH = 18;

		if (name.length () > MAXLENGTH)
			return name.substring (0, MAXLENGTH);
		return name;
	}
}
