/*	$Id: ImapSmtpMsgServer.java,v 1.7 2002/09/26 04:30:23 tecris Exp $
 *
 *	Message Server implemented using IMAP4 and SMTP
 *
 *		IMAP4 server	: getServerName ()
 *		SMTP server		: getHost ();
 *
 *		Directory prefix: getExtraString1()
 *			- this should really be a user-level preference, but
 *			  we haven't got support for this yet.
 *
 */
package wsl.fw.msgserver;

import java.util.Iterator;
import java.util.Vector;

import wsl.fw.datasource.*;

import javax.mail.MessagingException;

public class ImapSmtpMsgServer
    extends MessageServer
{
	/*
	 *
	 */
	private SessionManager _sessionMgr = null;

    public
	ImapSmtpMsgServer ()
    {
		super ();
    }

	private SessionManager
	getSessionManager ()
	{
		if (_sessionMgr == null)
			_sessionMgr = new SessionManager ();
		return _sessionMgr;
	}

	public boolean
	iHasNextObj (
	 RecordItrRef ref)
	 	throws DataSourceException
	{
		return getUserSession (ref.getSessionId ()).
			   getIterator (ref).hasNext ();
	}

	public Object
	iNextObj (
	 RecordItrRef ref)
	 	throws DataSourceException
	{
		return getUserSession (ref.getSessionId ()).
			   getIterator (ref).next ();
	}

	/**
	 *  Class requirment. Handle the action.
	 */
	public RecordItrRef
	doActionQuery (
	 ActionQuery q)
	 	throws DataSourceException
	{
        ActionDobj action = q.getActionDobj ();
		String sessionId = q.getSessionId ();
		String userId = q.getUserId ().toString ();
		ImapSmtpSession session = getUserSession (sessionId, userId);

        // folder actions
        if (action instanceof FolderDobj)
		{
			/*
			 *	Get a RecordItr back and store it locally on the
			 *	server side in a UserSession keymap. Return the
			 *	serializable reference to it that will be used in the
			 *	iHasNextObj and iNextObj to lookup the held iterator
			 */
			RecordItr itr = doFolderAction (session, q);
			RecordItrRef ref = new RecordItrRef (sessionId);

			session.addIterator (ref, itr);
			return ref;
		}

		/*
		 *	Other general actions
		 */
		switch (action.getActionType ())
		{
		case ActionDobj.AT_LOGOUT:
			session.logout ();
			return null;
		}

        throw new DataSourceException (
			"Action not implemented: " + action.getActionType ());
	}

	private ImapSmtpSession
	getUserSession (
	 String sessionId,
	 String uid)
	 	throws DataSourceException
	{
		/*
		 *	Check if the current user is logged in on the Server
		 */
		SessionManager sessionMgr = getSessionManager ();
		if (!sessionMgr.isLoggedOn (sessionId))
		{
			/*
			 *	Let's attempt to log in the user to the MessageServer.
			 *
			 *	Translate the current user to their profile on MessageServer
			 */
			MessageServerProfile profile = getFirstProfile (
												Integer.parseInt (uid));
			if (profile == null)
				throw new DataSourceException ("No Message Profile");

			/*
			 *	Create a new session using the supplied profile info.
			 *	The session info will automatically be held in the
			 *	sessionMgr during instantiation.
			 */
			new ImapSmtpSession (sessionMgr, sessionId,
				getServerName (), getHost (), getExtraString1 (),
				profile.getProfileName (), profile.getPassword ());
		}

		return (ImapSmtpSession) sessionMgr.getUserSession (sessionId);
	}

	private ImapSmtpSession
	getUserSession (
	 String sessionId)
	{
		return (ImapSmtpSession) getSessionManager ().
								 getUserSession (sessionId);
	}

	private RecordItr
	doFolderAction (
	 ImapSmtpSession session,
	 ActionQuery q)
	 	throws DataSourceException
	{
		FolderDobj folder = (FolderDobj) q.getActionDobj ();

		try
		{
			switch (folder.getActionType ())
			{
			case FolderDobj.AT_ROOT_FOLDER:			// Top level folders
			case FolderDobj.AT_DEFAULT_INBOX:		// default INBOX
				return session.getRootFolderItr ();

			case FolderDobj.AT_MAIL_UNREAD:			// display unread mail
				return session.getMailFolderItr (folder, true);

			case FolderDobj.AT_MAIL_ALL:			// display all mail
			case FolderDobj.AT_FOLDER:				// display a folder
				return session.getMailFolderItr (folder, false);
			}

		} catch (MessagingException e)
		{
			throw new DataSourceException ( "IMAP+SMTP: " + e.getMessage ());
		}

		throw new DataSourceException (
					"Unimplemented action: " + folder.getActionType ());
	}

	/**
	 *  Class requirement. Spool a message to be sent off
     *	@param mObj the mail message to send
     *	@return true if message sent successfully
	 */
	protected boolean
	insertMailMessage (
	 SessionMailMsgDobj mObj)
	 	throws DataSourceException
	{
		getUserSession (mObj.getSessionId ()).sendEmail (mObj);
		return true;
	}

	/**
	 * Insert (forward) a mail message
	 * @param mm the mail message to forward
	 * @return true if message sent successfully
	 */
	protected boolean
	insertFwMailMessage (
	 SessionFwMailMsgDobj fwd)
		throws DataSourceException
	{
		getUserSession (fwd.getSessionId ()).forwardEmail (fwd);
		return true; 
	}

	/**
	 *	Delete a MailMessage
     *	@param mObj the mail message to delete
     *	@return true if message deleted successfully
	 */
	protected boolean
	deleteMailMessage (
	 SessionMailMsgDobj mObj)
	 	throws DataSourceException
	{
		getUserSession (mObj.getSessionId ()).deleteEmail (mObj);
		return true;
	}

	/**
	 *	Mark a mail message as read.
     *	@param mObj the mail message to mark
	 */
	protected void
	updateReadMailMessage (
	 SessionMailMsgDobj mObj)
	 	throws DataSourceException
	{
		getUserSession (mObj.getSessionId ()).markEmail (mObj);
	}

	public int execInsertOrUpdate(String sql) throws DataSourceException {
		throw new DataSourceException("Not implemented");
	}

	
	
}
