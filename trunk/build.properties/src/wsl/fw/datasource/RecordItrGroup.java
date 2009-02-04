/**	$Header: /wapsolutions/cvsroot/mdn/fw/src/wsl/fw/datasource/RecordItrGroup.java,v 1.1.1.1 2002/06/11 23:11:42 jonc Exp $
 *
 * 	Groups of RecordItr
 *
 */
package wsl.fw.datasource;

import java.util.Vector;

public class RecordItrGroup
	extends RecordItr
{
	int _curIterator;
	private Vector _itrs;

	/**
	 *	Constructor
	 */
	public
	RecordItrGroup ()
	{
		_itrs = new Vector ();
	}

	/**
	 *	Class requirement
	 */
	public boolean
	hasNext ()
	{
		/*
		 *	Work thru' all the RecordItrs we hold to find something
		 *	that hasNext() returns true..
		 */
		while (_curIterator < _itrs.size ())
		{
			RecordItr itr = (RecordItr) _itrs.elementAt (_curIterator);

			if (itr.hasNext ())
				return true;
			_curIterator++;		// let's try the next one
		}
		return false;
	}

	/**
	 *	Class requirement
	 */
	public Object
	next ()
	{
		/*
		 *	Work thru' all the RecordItrs we hold to find something
		 *	that hasNext() returns a valid object
		 */
		while (_curIterator < _itrs.size ())
		{
			RecordItr itr = (RecordItr) _itrs.elementAt (_curIterator);
			Object obj = itr.next ();

			if (obj != null)
				return obj;
			_curIterator++;		// let's try the next one
		}
		return null;
	}

	/**
	 *	Add an iterator onto the Queue.
	 */
	public void
	add (
	 RecordItr itr)
	{
		_itrs.add (itr);
	}
}