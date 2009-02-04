/*	$Id: ImapSmtpSession.java,v 1.11 2002/09/27 01:29:43 jonc Exp $
 *
 *	Session for an ImapSmtpMsgServer
 *
 */
package wsl.fw.msgserver;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.Multipart;
import javax.mail.Folder;
import javax.mail.Flags.Flag;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Part;
import javax.activation.DataHandler;


import wsl.fw.datasource.DataSourceException;
import wsl.fw.datasource.RecordItr;
import wsl.fw.datasource.RecordItrGroup;
import wsl.fw.datasource.RecordVectorItr;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import java.io.IOException;

public class ImapSmtpSession
	extends UserSession
{
	/**
	 *
	 */
	String _userEmail;
	Hashtable _folders;					// Cached folders
	Session _session;
	Store _imap;
	javax.mail.Folder _inbox;			// inbox
	javax.mail.Folder _root;			// IMAP root directory

	/**
	 * Constructor
	 */
	public
	ImapSmtpSession (
	 SessionManager mgr,
	 String cookie,
	 String imapHost,
	 String smtpHost,
	 String root,
	 String user,
	 String passwd)
		throws DataSourceException
	{
		super (mgr, cookie);

		_userEmail = user + "@" + smtpHost;
		_folders = new Hashtable ();

		Properties props = new Properties ();
		props.setProperty ("mail.user", user);
		props.setProperty ("mail.imap.host", imapHost);
		props.setProperty ("mail.smtp.host", smtpHost);

		_session = Session.getInstance (props, null);

		/*
		 *	Connect to the IMAP server
		 */
		try
		{
			_imap = _session.getStore ("imap");
			_imap.connect (imapHost, user, passwd);

			/*
			 *	Get the standard folders
			 */
			_inbox = _imap.getFolder ("INBOX");
			if (root != null && root.length () > 0)
				_root = _imap.getFolder (root);
			else
				_root = _imap.getDefaultFolder ();

		} catch (MessagingException e)
		{
			super.logout ();
			throw new DataSourceException (e.getMessage ());
		}
	}

	private javax.mail.Folder
	getWriteFolder (
	 String url)
		throws MessagingException
	{
		if (_folders.containsKey (url))
			return (javax.mail.Folder) _folders.get (url);

		/*
		 *	Add a new entry for a READ_WRITE folder
		 */
		javax.mail.Folder f = _imap.getFolder (new URLName (url));
		f.open (javax.mail.Folder.READ_WRITE);
		_folders.put (url, f);

		return f;
	}

	/**
	 *	Extend the logout functionality to clean up the Session.
	 */
	public void
	logout ()
	{
		super.logout ();					// required call to superclass

		/*
		 *	Close IMAP folders
		 */
		Enumeration enums = _folders.elements ();
		while (enums.hasMoreElements ())
		{
			/*
			 *	Force a close on everything, ignoring errors.
			 */
			try
			{
				javax.mail.Folder f = (javax.mail.Folder) enums.nextElement ();
				f.close (true);

			} catch (MessagingException e)
			{
			} catch (IllegalStateException e)
			{
			}
		}

		try
		{
			_imap.close ();

		} catch (MessagingException e)
		{
		}

		/*
		 *	Remove internal references in all cases
		 */
		_session = null;
		_imap = null;
	}

	public void
	sendEmail (
	 MailMessageDobj mObj)
		throws DataSourceException
	{
		try
		{
			// construct the message
			MimeMessage msg = new MimeMessage (_session);
			msg.setFrom (new InternetAddress (_userEmail));

			msg.addRecipients(
				javax.mail.Message.RecipientType.TO,
				InternetAddress.parse (mObj.getRecipient (), false));

			msg.setSubject (mObj.getSubject ());
			msg.setText (mObj.getText ());

			Transport.send (msg);

		} catch (AddressException e)
		{
			throw new DataSourceException (e.getMessage ());

		} catch (MessagingException e)
		{
			throw new DataSourceException (e.getMessage ());

		}
	}

	public void
	forwardEmail (
	 MailMessageDobj mObj)
		throws DataSourceException
	{
		try
		{
			// construct the message
			MimeMessage msg = new MimeMessage (_session);
			msg.setFrom (new InternetAddress (_userEmail));

			msg.addRecipients(
				javax.mail.Message.RecipientType.TO,
				InternetAddress.parse (mObj.getRecipient (), false));

			// setting the message subject
			msg.setSubject (mObj.getSubject ());

			// here we create the part for the message text
			BodyPart msgBodyPart = new MimeBodyPart ();
			msgBodyPart.setText (mObj.getText ());

			Multipart multipart = new MimeMultipart ();
			multipart.addBodyPart (msgBodyPart);

			// handle the attachment
			/*
			 *	First, locate the message via the folder and message id
			 */
			javax.mail.Folder f = getWriteFolder (mObj.getFolderId ());
			javax.mail.Message message= f.getMessage (
										Integer.parseInt (mObj.getId ()));
			// create the part for the attachment(s)
			msgBodyPart = new MimeBodyPart ();
			// get the attachment(s) from the original message
			msgBodyPart.setDataHandler (message.getDataHandler ());
			// add the part to the multipart
			multipart.addBodyPart (msgBodyPart);

			// add the multipart to the message
			msg.setContent (multipart);

			Transport.send (msg);

		} catch (NumberFormatException e)
		{
			throw new DataSourceException (e.getMessage ());

		} catch (AddressException e)
		{
			throw new DataSourceException (e.getMessage ());

		} catch (MessagingException e)
		{
			throw new DataSourceException (e.getMessage ());

		}
	}

	public void
	deleteEmail (
	 MailMessageDobj mObj)
		throws DataSourceException
	{
		/*
		 *	Locate the message via the folder and message id
		 */
		try
		{
			javax.mail.Folder f = getWriteFolder(mObj.getFolderId ());
			javax.mail.Message m = f.getMessage (
										Integer.parseInt (mObj.getId ()));
			m.setFlag (Flag.DELETED, true);


		} catch (MessagingException e)
		{
			throw new DataSourceException (e.getMessage ());

		} catch (NumberFormatException e)
		{
			throw new DataSourceException (e.getMessage ());
		}
	}

	public void
	markEmail (
	 MailMessageDobj mObj)
		throws DataSourceException
	{
		/*
		 *	Locate the message via the folder and message id
		 */
		try
		{
			javax.mail.Folder f = getWriteFolder(mObj.getFolderId ());
			javax.mail.Message m = f.getMessage (
										Integer.parseInt (mObj.getId ()));
			m.setFlag (Flag.SEEN, true);

		} catch (MessagingException e)
		{
			throw new DataSourceException (e.getMessage ());

		} catch (NumberFormatException e)
		{
			throw new DataSourceException (e.getMessage ());
		}
	}

	public RecordItr
	getRootFolderItr ()
		throws MessagingException
	{
		/*
		 *	Present the INBOX and any other folders in the user's base
		 */
		Vector result = new Vector ();

		result.add (new FolderDobj (
							"All Mail",
							_inbox.getURLName ().toString (),
							wsl.fw.msgserver.Folder.FCT_MIXED,
							ActionDobj.AT_MAIL_ALL));
		result.add (new FolderDobj (
							"Unread Mail",
							_inbox.getURLName ().toString (),
							wsl.fw.msgserver.Folder.FCT_MIXED,
							ActionDobj.AT_MAIL_UNREAD));

		/*
		 *	Only list subfolders if it supports it
		 */
		if ((_root.getType () & javax.mail.Folder.HOLDS_FOLDERS) != 0)
		{
			/*
			 *	List root subfolders
			 */
			javax.mail.Folder flist [] = _root.list ();
			for (int i = 0; i < flist.length; i++)
			{
				/*
				 *	Skip the INBOX. Some root-folders relist this again.
				 */
				if (flist [i].getURLName ().equals (_inbox.getURLName ()))
					continue;

				/*
				 *	Display the folder if its subscribed to, or
				 *	could possibly hold subfolders
				 */
				if (flist [i].isSubscribed () ||
					(flist [i].getType () & javax.mail.Folder.HOLDS_FOLDERS) != 0)
				{
					result.add (new FolderDobj (
										flist [i].getName (),
										flist [i].getURLName ().toString (),
										wsl.fw.msgserver.Folder.FCT_MIXED,
										ActionDobj.AT_FOLDER));
				}
			}

		} else
		{
			result.add (new FolderDobj (
								_root.getName (),
								_root.getURLName ().toString (),
								wsl.fw.msgserver.Folder.FCT_MIXED,
								ActionDobj.AT_FOLDER));
		}

		return new RecordVectorItr (result);
	}

	public RecordItr
	getMailFolderItr (
	 FolderDobj folder,
	 boolean unreadOnly)
		throws MessagingException
	{
		javax.mail.Folder f = getWriteFolder (folder.getId ());

		if ((f.getType () & javax.mail.Folder.HOLDS_FOLDERS) != 0 &&
			(f.getType () & javax.mail.Folder.HOLDS_MESSAGES) != 0)
		{
			/*
			 *	return a double whammy
			 */
			RecordItrGroup both = new RecordItrGroup ();
			both.add (new ImapSubfolderItr (f));
			both.add (new ImapFolderItr (f, unreadOnly));
			return both;

		} else if ((f.getType () & javax.mail.Folder.HOLDS_FOLDERS) != 0)
		{
			return new ImapSubfolderItr (f);
		}

		return new ImapFolderItr (f, unreadOnly);		// default
	}
}
