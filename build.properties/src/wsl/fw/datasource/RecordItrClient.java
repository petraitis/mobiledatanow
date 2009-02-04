/**	$Header: /wapsolutions/cvsroot/mdn/fw/src/wsl/fw/datasource/RecordItrClient.java,v 1.1.1.1 2002/06/11 23:11:42 jonc Exp $
 *
 *	Client side for RecordItr
 */
package wsl.fw.datasource;

import java.util.Iterator;

public abstract class RecordItrClient
	implements Iterator
{
	public
	RecordItrClient ()
	{
	}

	public abstract boolean
	hasNext ();

	public abstract Object
	next ();

	public void
	remove ()
	{
	}
}