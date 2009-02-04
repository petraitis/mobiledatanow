/**	$Header: /wapsolutions/cvsroot/mdn/fw/src/wsl/fw/datasource/RecordVectorItr.java,v 1.1.1.1 2002/06/11 23:11:42 jonc Exp $
 *
 *	An RecordItr for a Vector. Provided as a convenience class
 *
 */
package wsl.fw.datasource;

import java.util.Vector;

public class RecordVectorItr
	extends RecordItr
{
	private int _index;
	private Vector _v;

	public
	RecordVectorItr (
	 Vector v)
	{
		_index = 0;
		_v = v;
	}

	public boolean
	hasNext ()
	{
		return _index < _v.size ();
	}

	public Object
	next ()
	{
		if (hasNext ())
			return _v.elementAt (_index++);
		return null;
	}
}