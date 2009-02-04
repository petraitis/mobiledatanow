/**	$Id: SessionMailMsgDobj.java,v 1.2 2002/07/24 02:35:36 jonc Exp $
 *
 *	A mail message coming back from the client side. It needs to
 *	be marked with a sessionId.
 *
 */
package wsl.fw.msgserver;

public class SessionMailMsgDobj
	extends MailMessageDobj
{
	private String _sessionId;

    /**
     * Constructor
     * @param session the sessionId
     */
    public
	SessionMailMsgDobj (
	 String sessionId)
    {
		super ();

		_sessionId = sessionId;
    }

    public
	SessionMailMsgDobj (
	 String sessionId,
	 MailMessageDobj orig)
    {
		super (orig);

		_sessionId = sessionId;
    }

	public String
	getSessionId ()
	{
		return _sessionId;
	}
}
