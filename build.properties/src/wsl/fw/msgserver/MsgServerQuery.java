/**	$Id: MsgServerQuery.java,v 1.3 2002/07/22 23:47:06 jonc Exp $
 *
 */
package wsl.fw.msgserver;

import wsl.fw.datasource.Query;

public class MsgServerQuery
	extends Query
{
	private String _sessionId;
	private Object _userId;

	/**
	 * Param ctor
	 * @param userid
	 */
	public
	MsgServerQuery (
	 String sessionId,
	 Object userId)
	{
		_sessionId = sessionId;
		setUserId (userId);
	}

	/**
	 * Set the User id
	 * @param userid
	 */
	public void
	setUserId (
	 Object userId)
	{
		_userId = userId;
	}

	/**
	 * @return the User id
	 */
	public Object
	getUserId ()
	{
		return _userId;
	}

	public String
	getSessionId ()
	{
		return _sessionId;
	}
}
