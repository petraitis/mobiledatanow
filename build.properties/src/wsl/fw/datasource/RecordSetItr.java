/**	$Header: /wapsolutions/cvsroot/mdn/fw/src/wsl/fw/datasource/RecordSetItr.java,v 1.1.1.1 2002/06/11 23:11:42 jonc Exp $
 *
 *	An Iterator for a RecordSet.
 *  Wraps the RecordSet up as an RecordItr
 *
 */
package wsl.fw.datasource;

public class RecordSetItr
	extends RecordItr
{
	private int _index;
	private RecordSet _rs;

	public
	RecordSetItr (
	 RecordSet rs)
	{
		_index = 0;
		_rs = rs;
	}

	public boolean
	hasNext ()
	{
		return _index < _rs.size ();
	}

	public Object
	next ()
	{
		if (hasNext ())
			return _rs.getRows ().elementAt (_index++);
		return null;
	}
}