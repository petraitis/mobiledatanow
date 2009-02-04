/**	$Header: /wapsolutions/cvsroot/mdn/fw/src/wsl/fw/datasource/RecordItr.java,v 1.1.1.1 2002/06/11 23:11:42 jonc Exp $
 *
 *	Abstract Iterator for datasource stuff.
 *  Offers an alternative to using RecordSet.
 *
 *  The RecordItr allows the possibility to fetch Objects on demand,
 *  rather than getting an instantiated RecordSet of Objects and handing
 *  the Set around.
 *
 * 	Each instance needs to run on the server end, as such it cannot be handed
 *  back to the client. To do this, the class should either:
 *  	1. be implemented as an RMI Remote
 *  or	2. use a kludge to hand a reference-key-map back to the client
 *
 *  We're using Option 2 at the moment.
 */
package wsl.fw.datasource;

import java.io.Serializable;
import java.util.Iterator;

public abstract class RecordItr
	implements Iterator
{
	public abstract boolean hasNext ();
	public abstract Object next ();

	/**
	 *	Optional class for Iteration.
	 */
	public void
	remove ()
	{
	}
}