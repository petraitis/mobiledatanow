/*	$Id: MailMessageDobj.java,v 1.4 2002/07/24 02:35:36 jonc Exp $
 *
 *	Represents a mail message in a message server
 *
 */
package wsl.fw.msgserver;

import wsl.fw.datasource.*;
import wsl.fw.util.Type;

public class MailMessageDobj
	extends ItemDobj
{
	//--------------------------------------------------------------------------
	// constants
	public final static String
		FLD_SUBJECT			= "Subject",
		FLD_TEXT			= "Text",
		FLD_SENDER			= "Sender",
		FLD_SENDER_EMAIL	= "Sender Email",
		FLD_RECIPIENT		= "Recipient",
		FLD_TIME_RECEIVED	= "Recvd",
		FLD_UNREAD			= "Unread";


	//--------------------------------------------------------------------------
	// attributes
	private final String _id;				// unique identifier
	private final String _folderId;			// folder holding the message
	private String _recipient = "";
	private String _senderEmail = "";
	private boolean _isUnread = true;

	//--------------------------------------------------------------------------
	// construction

	/**
	 * Blank ctor
	 */
	public
	MailMessageDobj ()
	{
		_id = null;
		_folderId = null;
	}

	/**
	 * MailMessage ctor
	 * @param mm the MailMessage
	 */
	public
	MailMessageDobj (
	 String folderId,
	 MailMessage mm)
	{
		_id	= mm._id;
		_folderId = folderId;

		// set attribs
		setUnread (mm._unread);
		setRecipient (mm._recipient);
		setSenderEmail (mm._senderEmail);

		// set fields
		setFieldValue (FLD_SUBJECT, mm._subject);
		setFieldValue (FLD_SENDER, mm._sender);
		setFieldValue (FLD_TIME_RECEIVED, mm._timeReceived);
		setFieldValue (FLD_TEXT, mm._text);
	}

	/**
	 *	Somewhat shallow clone
	 */
	protected
	MailMessageDobj (
	 MailMessageDobj orig)
	{
		_id				= orig._id;
		_folderId		= orig._folderId;
		_recipient		= orig._recipient;
		_senderEmail	= orig._senderEmail;
		_isUnread		= orig._isUnread;

		setSubject (orig.getSubject ());
		setSender (orig.getSender ());
		setTimeReceived (orig.getTimeReceived ());
		setText (orig.getText ());
	}

	/**
	 * method for subs to create / add fields
	 */
	protected void
	createFields ()
	{
		addField (FLD_SUBJECT);
		addField (FLD_SENDER);
		addField (FLD_TIME_RECEIVED);
		addField (FLD_TEXT);
	}

	//--------------------------------------------------------------------------
	// accessors
	/**
	 *	@return message identifer
	 */
	public String
	getId ()
	{
		return _id;
	}

	/**
	 *	@return the id of the Folder holding the Message
	 */
	public String
	getFolderId ()
	{
		return _folderId;
	}

	/**
	 * @return the subject of the message
	 */
	public String
	getSubject ()
	{
		return getFieldValue (FLD_SUBJECT);
	}

	/**
	 * Set the subject
	 * @param subject
	 */
	public void
	setSubject (
	 String subject)
	{
		setFieldValue (FLD_SUBJECT, subject);
	}


	/**
	 * @return the body of the message
	 */
	public String
	getText ()
	{
		return getFieldValue (FLD_TEXT);
	}

	/**
	 * Set the body
	 * @param body
	 */
	public void
	setText (
	 String text)
	{
		setFieldValue (FLD_TEXT, text);
	}

	/**
	 * @return the sender of the message
	 */
	public String
	getSender ()
	{
		return getFieldValue (FLD_SENDER);
	}

	/**
	 * Set the sender
	 * @param sender
	 */
	public void
	setSender (
	 String sender)
	{
		setFieldValue (FLD_SENDER, sender);
	}

	/**
	 * @return the email of the sender of the message
	 */
	public String
	getSenderEmail ()
	{
		return _senderEmail;
	}

	/**
	 * Set the sender email
	 * @param sender
	 */
	public void
	setSenderEmail (
	 String sender)
	{
		_senderEmail = sender;
	}

	/**
	 * @return the recipient of the message
	 */
	public String
	getRecipient ()
	{
		return _recipient;
	}

	/**
	 * Set the recipient
	 * @param recipient
	 */
	public void
	setRecipient (String recipient)
	{
		_recipient = recipient;
	}

	/**
	 * @return the time received of the message
	 */
	public String
	getTimeReceived ()
	{
		return getFieldValue (FLD_TIME_RECEIVED);
	}

	/**
	 * Set the time received
	 * @param time
	 */
	public void
	setTimeReceived (
	 String time)
	{
		setFieldValue (FLD_TIME_RECEIVED, time);
	}

	/**
	 * @return the unread flag
	 */
	public boolean
	isUnread ()
	{
		return _isUnread;
	}

	/**
	 * Set the unread flag
	 * @param unread
	 */
	public void
	setUnread (
	 String unread)
	{
		_isUnread = Type.objectToBoolean (unread);
	}

	/**
	 *	Query field email capability (override superclass)
	 *  @label label field name
	 */
	public boolean
	isEmailField (
	 String label)
	{
		return label.equals (FLD_SENDER_EMAIL);
	}

	public String
	toString ()
	{
		return getSubject ();
	}
}
