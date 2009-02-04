/**	$Id: ActionQuery.java,v 1.3 2002/07/22 23:47:06 jonc Exp $
 *
 * A generic query for an action dobj
 *
 */
package wsl.fw.msgserver;

public class ActionQuery
	extends MsgServerQuery
{
	//--------------------------------------------------------------------------
	// attributes
	private ActionDobj _ad = null;

	/**
	 * ActionDobj ctor
	 * @param ad the ActionDobj
	 */
	public
	ActionQuery (
	 String sessionId,
	 Object userId,
	 ActionDobj ad)
	{
		super (sessionId, userId);
		_ad = ad;
	}

	/**
	 * @return the ActionDobj
	 */
	public ActionDobj
	getActionDobj()
	{
		return _ad;
	}
}
