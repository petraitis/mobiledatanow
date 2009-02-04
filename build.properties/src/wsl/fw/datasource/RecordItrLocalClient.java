/**	$Header: /wapsolutions/cvsroot/mdn/fw/src/wsl/fw/datasource/RecordItrLocalClient.java,v 1.1.1.1 2002/06/11 23:11:42 jonc Exp $
 *
 *	Client side for RecordItr using local RecordItr
 */
package wsl.fw.datasource;

public class RecordItrLocalClient
	extends RecordItrClient
{
	private RecordItr _itr;

	public
	RecordItrLocalClient (
	 RecordItr itr)
	{
		_itr = itr;
	}

	public boolean
	hasNext ()
	{
		return _itr.hasNext ();
	}

	public Object
	next ()
	{
		return _itr.next ();
	}
}