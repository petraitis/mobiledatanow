/**	$Id: MsExMessageItr.java,v 1.2 2002/06/18 23:41:01 jonc Exp $
 *
 *  Message Iterator for a MS Exchange Folder
 *
 */
package wsl.fw.msgserver;

import wsl.fw.datasource.RecordItr;

public class MsExMessageItr
	extends MsExItr
{
	private final String _folderId;

	/**
	 *	General available constructor
	 */
	public
	MsExMessageItr (
	 String loginUrl,
	 String folderId)
	 	throws MessageServerException
	{
		super (loginUrl);
		_folderId = folderId;

		MSExchangeInterface.createMessageIterator (
			getLoginUrl (), getId (), folderId,
			false);					// reverse order presentation
	}

	/**
	 *	Constructor intended for descendant classes
	 */
	protected
	MsExMessageItr (
	 String loginUrl)
	{
		super (loginUrl);
		_folderId = "";				// not required..
	}

	public String
	getFolderId ()
	{
		return _folderId;
	}

	/**
	 *	Iterator support
	 */
	public boolean
	hasNext ()
	{
		try
		{
			return MSExchangeInterface.hasNextMessage (getLoginUrl (), getId ());

		} catch (MessageServerException e)
		{
			System.err.println ("ERR=" + e.getMessage ());
			e.printStackTrace ();
		}
		return false;
	}

	public Object
	next ()
	{
		String errMsg = "";
		try
		{
			Message msg = MSExchangeInterface.getNextMessage (getLoginUrl (), getId ());

			if (msg != null)
			{
				if (msg instanceof MailMessage)
					return new MailMessageDobj (_folderId, (MailMessage) msg);
				else if (msg instanceof Contact)
					return new ContactDobj ((Contact) msg);
				else if(msg instanceof Appointment)
					return new AppointmentDobj ((Appointment) msg);
				else if (msg instanceof Task)
					return new TaskDobj ((Task) msg);

				return new GenericItemDobj (msg);
			}

		} catch (MessageServerException e)
		{
			System.err.println ("ERR=" + e.getMessage ());
			e.printStackTrace ();

			errMsg = e.getMessage ();
		}

		return new GenericItemDobj (
				new Message ("**Internal Error**", errMsg, "Error"));
	}
}