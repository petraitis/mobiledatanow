/*	$Id: UserSession.java,v 1.2 2002/11/11 03:20:57 jonc Exp $
 *
 *	User session
 *
 */
package wsl.fw.msgserver;

import java.util.Hashtable;
import wsl.fw.datasource.RecordItr;
import wsl.fw.datasource.RecordItrRef;

public class UserSession
{
	private static final int
		TIMEOUT		= 5 * 60 * 1000;			// Timeout after 5 min

	private final SessionManager _sessionMgr;	// reference to Container
	private final String _cookie;				// internal session cookie
	private long _lastAction;					// last time we did something

	private Hashtable _iterators;				// active iterators

	/**
	 *  Constructor
	 *
	 * 	@param mgr a Session Manager
	 *  @param id Externally generated id to associate with the session
	 */
	public
	UserSession (
	 SessionManager mgr,
	 String cookie)
	{
		_sessionMgr = mgr;
		_cookie = cookie;
		_iterators = new Hashtable ();

		markTime ();
		_sessionMgr.register (_cookie, this);
	}

	/**
	 *	Remove references to the session. Subclasses may override
	 *  this to handle other cleanups, but should invoke this
	 *  class somewhere.
	 */
	public void
	logout ()
	{
		_sessionMgr.remove (_cookie);	// remove from SessionManager
	}

	public void
	markTime ()
	{
		_lastAction = System.currentTimeMillis ();
	}

	public boolean
	expired ()
	{
		long now = System.currentTimeMillis ();
		return now - _lastAction > TIMEOUT;
	}

	public void
	addIterator (
	 RecordItrRef ref,
	 RecordItr itr)
	{
		_iterators.put (Integer.toString (ref.getKey ()), itr);
	}

	public RecordItr
	getIterator (
	 RecordItrRef ref)
	{
		return (RecordItr) _iterators.get (Integer.toString (ref.getKey ()));
	}
}