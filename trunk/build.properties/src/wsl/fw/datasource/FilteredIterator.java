/**	$Header: /wapsolutions/cvsroot/mdn/fw/src/wsl/fw/datasource/FilteredIterator.java,v 1.1.1.1 2002/06/11 23:11:42 jonc Exp $
 *
 *	Apply a class+string filter to an iterator
 *
 */
package wsl.fw.datasource;

import java.util.Iterator;

public class FilteredIterator
	implements Iterator
{
	private Iterator _itr;
	private Class _clazz;
	private String _filter;
	private Object _curObj;

	public
	FilteredIterator (
	 Iterator itr,
	 Class clazz,
	 String filter)
	{
		_itr = itr;
		_clazz = clazz;
		_filter = filter;

		_curObj = null;
		skip ();
	}

	/**
	 *	Skip until we find an Object that satisfies the filter
	 */
	private void
	skip ()
	{
		while (_itr.hasNext ())
		{
			Object obj = _itr.next ();
			if (_clazz.isInstance (obj) &&
				obj.toString ().toLowerCase ().startsWith (_filter.toLowerCase()))
			{
				_curObj = obj;
				return;
			}
		}
		_curObj = null;
 	}

	public boolean
	hasNext ()
	{
		return _curObj != null;
	}

	public Object
	next ()
	{
		Object ret = _curObj;
		if (_curObj != null)
			skip ();
		return ret;
	}

	public void
	remove ()
	{
		_itr.remove ();
	}
}