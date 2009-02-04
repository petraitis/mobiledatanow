/**	$Id: MailMessage.java,v 1.2 2002/06/18 23:41:01 jonc Exp $
 *
 *	Intermediate MailMessage from the MessageServer
 *
 */
package wsl.fw.msgserver;

public class MailMessage
	extends Message
{
    //--------------------------------------------------------------------------
    // attributes
	public final String _id;
    public final String _sender;
	public final String _recipient;
    public final String _timeReceived;
    public final String _unread;
    public final String _senderEmail;

    /**
     * Attribute ctor
     * @param subject the subject of the message
     * @param text the body of the message
     * @param sender the sender of the message
     * @param timeReceived the timeReceived of the message
     * @param unread the unread flag of the message
     */
    public
	MailMessage (
	 String id,
	 String subject,
	 String text,
	 String type,
	 String sender,
	 String recipient,
	 String timeReceived,
	 String unread,
	 String senderEmail)
    {
        super (subject, text, type);

		_id = id;
		_recipient = recipient;
        _sender = sender;
        _timeReceived = timeReceived;
        _unread = unread;
        _senderEmail = senderEmail;
    }
}