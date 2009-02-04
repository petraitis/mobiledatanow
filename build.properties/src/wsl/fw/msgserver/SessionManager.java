/**	$Header: /wapsolutions/cvsroot/mdn/fw/src/wsl/fw/msgserver/SessionManager.java,v 1.3 2002/11/11 03:20:57 jonc Exp $
 *
 *	Session Manager.
 *
 * 	Used to keep track of open sessions. Of particular use for Java based
 *  MessageServers, JNI based MessageServers may find it more convenient
 *  to implement it (again) externally
 *
 * 	We don't need to timeout the sessions, this is done automatically
 * 	by the presentation side when a session is unbound.
 *
 */
package wsl.fw.msgserver;

import java.util.Enumeration;
import java.util.Hashtable;

class SessionManager
{
	/*
	 *	State variables.
	 */
	private Hashtable _sessions;

	public
	SessionManager ()
	{
		_sessions = new Hashtable ();
	}

	/**
	 * Add the session into internal map. Should only be invoked
	 * from UserSession constructor
	 * @param cookie Internal Session identifier
	 * @param session UserSession
	 */
	public synchronized void
	register (
	 String cookie,
	 UserSession session)
	{
		_sessions.put (cookie, session);
	}

	/**
	 * Remove session from internal map. Should only be invoked
	 * from UserSession.logout();
	 * @param cookie Internal Session identifier
	 */
	public synchronized void
	remove (
	 String cookie)
	{
		_sessions.remove (cookie);
	}

	/**
	 * Is the session-cookie present in the internal map?
	 * @param cookie Internal Session identifier
	 */
	public boolean
	isLoggedOn (
	 String cookie)
	{
		if (_sessions.containsKey (cookie))
		{
			/*
			 *	Every query about a particular session would indicate
			 *	an action on the session. So let's indicate an interest.
			 */
			getUserSession (cookie).markTime ();
			return true;
		}
		return false;
	}

	/**
	 * Return the UserSession from the cookie given
	 * @param cookie Internal Session identifier
	 */
	public UserSession
	getUserSession (
	 String cookie)
	{
		return (UserSession) _sessions.get (cookie);
	}

	/**
	 *	Remove sessions that have timed out.
	 */
	public void
	clearDeadSessions ()
	{
		/*
		 *	There's got to be a more efficient way to do this,
		 *	but this should be enough for now.
		 */
		Enumeration e = _sessions.elements ();
		while (e.hasMoreElements ())
		{
			UserSession session = (UserSession) e.nextElement ();
			if (session.expired ())
				session.logout ();
		}
	}
}
