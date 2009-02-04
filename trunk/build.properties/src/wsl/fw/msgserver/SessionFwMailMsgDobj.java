/**	$Id: SessionFwMailMsgDobj.java,v 1.2 2002/09/26 04:46:06 tecris Exp $
 *
 *	A mail message coming back from the client side. It needs to
 *	be marked with a sessionId.
 *
 */
package wsl.fw.msgserver;

public class SessionFwMailMsgDobj
	extends SessionMailMsgDobj
{

    /**
     * Constructor
     * @param session the sessionId
     */
    public
	SessionFwMailMsgDobj (
	 String sessionId)
    {
		super (sessionId);
    }

    public
	SessionFwMailMsgDobj (
	 String sessionId,
	 MailMessageDobj orig)
    {
		super (sessionId, orig);
    }

}
