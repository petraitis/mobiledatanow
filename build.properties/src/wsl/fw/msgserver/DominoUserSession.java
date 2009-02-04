/*	$Header: /wapsolutions/cvsroot/mdn/fw/src/wsl/fw/msgserver/DominoUserSession.java,v 1.5 2002/09/26 05:54:52 tecris Exp $
 *
 *	User session for Domino Server Connections
 */
package wsl.fw.msgserver;

import lotus.domino.*;
import java.util.Date;
import java.util.Vector;
import java.util.Iterator;
import wsl.fw.util.Util;

import wsl.fw.datasource.DataSourceException;

public class DominoUserSession
	extends UserSession
{
	/*
	 *	Magic numbers and strings
	 */
	private static final String
		SYSTEMVIEW_PREFIX	= "($";

	/**
	 *
	 */
	private Session _session = null;	// Domino session
	private String _user;
	private Database
        _mailDb = null,                 // std mailbox database
        _addrDb = null;                 // address book & other interesting stuff
    private View _contacts = null;		// contact folder to use..

	/*
	 *	Used by getRootFolders
	 */
	public class FolderCollection
		extends Vector
		implements Iterator
	{
		private int _index;

		public
		FolderCollection ()
		{
			super ();
			_index = 0;
		}

		public boolean
		hasNext ()
		{
			return _index < size ();
		}

		public Object
		next ()
		{
			return elementAt (_index++);
		}

		public void
		remove ()
		{
			if (_index > 0)
				remove (--_index);
		}
	}

	/*
	 *	Used by getFolderMessages
	 */
	public class MessageCollection
		extends Vector
		implements Iterator
	{
		private int _index;

		public
		MessageCollection ()
		{
			super ();
			_index = 0;
		}

		public boolean
		hasNext ()
		{
			return _index < size ();
		}

		public Object
		next ()
		{
			return elementAt (_index++);
		}

		public void
		remove ()
		{
			if (_index > 0)
				remove (--_index);
		}
	}

	/**
	 * Constructor
	 */
	public
	DominoUserSession (
	 SessionManager mgr,
	 String cookie,
	 String host,
	 String user,
	 String passwd)
		throws DataSourceException
	{
		super (mgr, cookie);
		_user = user;

		/*
		 *	Let's try the connection
		 */
		try
		{
			_session = NotesFactory.createSession (host, user, passwd);
			_mailDb = _session.getDbDirectory (null).openMailDatabase ();

            /*
             *  The address book is bit more interesting to get. What
             *  we'll do is to get the list of possible databases back,
             *  and pick the first one that looks close to what we want..
             */
            Vector dbVector = _session.getAddressBooks ();
			for (int i = 0; i < dbVector.size (); i++)
			{
				Database addrDb = (Database) dbVector.elementAt (i);

                if (addrDb.isPublicAddressBook ())
                {
				    addrDb.open ();
                    _contacts = addrDb.getView (DominoMsgServer.VIEW_PEOPLE);
                    if (_contacts != null)
                    {
                        /*
                         *  Success!
                         */
                        _addrDb = addrDb;
                        break;
                    }
				    addrDb.recycle ();
                }
			}

		} catch (NotesException e)
		{
			super.logout ();

			switch (e.id)
			{
			case NotesError.NOTES_ERR_GETIOR_FAILED:
				throw new MessageServerException (
							e.id,
							"Domino: DIIOP Server not running");

			case NotesError.NOTES_ERR_INVALID_USERNAME_PASSWD:
				throw new MessageServerException (
							e.id,
							"Domino: Bad username/password");

			default:
				throw new MessageServerException (
							e.id,
							"Domino: id=" + e.id + ", msg=" + e.getMessage ());
			}
		}
	}

	/**
	 *	Extend the logout functionality to clean up the Domino Session.
	 */
	public void
	logout ()
	{
		super.logout ();					// required call to superclass
		try
		{
			_session.recycle ();			// tear down the object

		} catch (NotesException e)
		{
		}

		_session = null;					// remove internal ref in all cases
	}

	/**
	 *  Return Iterator containing FolderDobjs
	 */
	public Iterator
	getRootFolders ()
		throws DataSourceException
	{
		/*
		 *	Convert Domino Folder/Views to FolderDobjs
		 *
		 *	Don't try to filter the results, we'll let invoker
		 *	handle that.
		 */
		FolderCollection folders = new FolderCollection ();

		try
		{
			Vector views = _mailDb.getViews ();

			for (int i = 0; i < views.size (); i++)
			{
				View view = (View) views.elementAt (i);

				String id = view.getName ();
				String name = cookIdName (id);

				/*
				 *	Let's decide what we want to happen depending
				 *	on the name that comes back.
				 */
				int action = ActionDobj.AT_NONE;
				if (id.equals (DominoMsgServer.VIEW_INBOX))
					action = ActionDobj.AT_DEFAULT_INBOX;

				else if (id.equals (DominoMsgServer.VIEW_CALENDAR))
					action = ActionDobj.AT_DEFAULT_CALENDAR;

                /*
                 *  This does absolutely nothing, since the server
                 *  doesn't contain anything in ($Contacts), it's only
                 *  available in the (Local) database.
                 */
				//else if (id.equals (DominoMsgServer.VIEW_CONTACTS))
				//	action = ActionDobj.AT_DEFAULT_CONTACTS;

				else if (id.equals (DominoMsgServer.VIEW_TODO))
					action = ActionDobj.AT_TASK_LIST;

				else if (id.startsWith (SYSTEMVIEW_PREFIX))
					continue;				// ignore

				else if (view.isFolder ())
				{
					action = ActionDobj.AT_FOLDER;

				} else
					continue;				// ignore everything else

				folders.add (new FolderDobj (
								name, id,
								Folder.FCT_MIXED,
								action));
			}

            /*
             *  Final catch for Contacts..
             */
            if (_contacts != null)
            {
				folders.add (new FolderDobj (
                                cookIdName (_contacts.getName ()),
								_contacts.getName (),
								Folder.FCT_MIXED,
				                ActionDobj.AT_DEFAULT_CONTACTS));
            }

		} catch (NotesException e)
		{
			throw new DataSourceException ("Domino: " + e.getMessage ());
		}

		return folders;
	}

	/**
	 *  Process the id-name given to something suitable for presentation
	 */
	public static String
	cookIdName (
	 String raw)
	{
		if (raw.startsWith (SYSTEMVIEW_PREFIX))
		{
			/*
			 *	Trim off the System Prefix and the trailing bracket
			 */
			return raw.substring (
						SYSTEMVIEW_PREFIX.length (), raw.length () - 1 );

		} else if (raw.indexOf ('\\') >= 0)
		{
			/*
			 *	Return everything *after* the last '\'
			 */
			return raw.substring (raw.lastIndexOf ('\\') + 1);
		}
		return raw;
	}

	/**
     *  Generic case:
	 *	Returns an Iterator to Notes' Documents
	 */
	public DominoFolderItr
	getFolderMessages (
	 FolderDobj folder)
	 	throws NotesException
	{
		View view = _mailDb.getView (folder.getId ());
		return new DominoFolderItr (_mailDb, view);
	}

	public DominoApptFolderItr
	getApptFolderItr (
	 FolderDobj folder,
	 DateTime day)
	 	throws NotesException
	{
		View view = _mailDb.getView (folder.getId ());
		return new DominoApptFolderItr (_mailDb, view, day);
	}

	public DominoMailFolderItr
	getMailFolderItr (
	 FolderDobj folder)
	 	throws NotesException
	{
		View view = _mailDb.getView (folder.getId ());
		return new DominoMailFolderItr (_mailDb, view);
	}

	public DominoTaskFolderItr
	getTaskFolderItr (
	 FolderDobj folder)
	 	throws NotesException
	{
		View view = _mailDb.getView (folder.getId ());
		return new DominoTaskFolderItr (_mailDb, view);
	}

    /*
     *  Handle Contacts with Domino Notes.
     */
    public DominoFolderItr
    getContactFolderItr ()
    {
		if (_contacts == null)
			return new DominoFolderItr ();		// empty set
		return new DominoContactFolderItr (_addrDb, _contacts);
    }

	public void
	sendEmail (
	 MailMessageDobj mObj)
	 	throws DataSourceException
	{
		try
		{
			/*
			 *	Under Notes, one has to create the Document,
			 *	add the required Items, and then Document.send()
			 *	I'm assuming that it'll handle Notes user-addressing
			 *	conventions as well as general Internet conventions
			 */
			Document email = _mailDb.createDocument ();
			email.replaceItemValue (
				DominoMailFolderItr.MAIL_SENDER,
				Util.noNullStr (_user));
			email.replaceItemValue (
				DominoMailFolderItr.MAIL_SUBJECT,
				Util.noNullStr (mObj.getSubject ()));
			email.replaceItemValue (
				DominoMailFolderItr.MAIL_BODY,
				Util.noNullStr (mObj.getText ()));

			email.send (Util.noNullStr (mObj. getRecipient ()));

		} catch (NotesException e)
		{
			throw new DataSourceException ("Domino: " + e.text);
		}
	}

	public void
	forwardEmail (
	 MailMessageDobj mObj)
	 	throws DataSourceException
	{
		try
		{
			/*
			 *	To forward a message we find the message,
			 *	create a copy of the message ,
			 *	override subject,text etc 
			 *	send the message
			 */
			Document email = _mailDb.getDocumentByUNID (mObj.getId ());
			Document forward = _mailDb.createDocument ();
			email.copyAllItems (forward,true);
			forward.replaceItemValue (
				DominoMailFolderItr.MAIL_SENDER,
				Util.noNullStr (_user));
			forward.replaceItemValue (
				DominoMailFolderItr.MAIL_SUBJECT,
				Util.noNullStr (mObj.getSubject ()));
			forward.replaceItemValue (
				DominoMailFolderItr.MAIL_BODY,
				Util.noNullStr (mObj.getText ()));

			forward.send (Util.noNullStr (mObj. getRecipient ()));

		} catch (NotesException e)
		{
			throw new DataSourceException ("Domino: " + e.text);
		}
	}

	public void
	deleteEmail (
	 MailMessageDobj mObj)
	 	throws DataSourceException
	{
		try
		{
			Document email = _mailDb.getDocumentByUNID (mObj.getId ());
			email.remove (false);			// soft-removal

		} catch (NotesException e)
		{
			throw new DataSourceException ("Domino: " + e.getMessage ());
		}
	}

	public DateTime
	getToday ()
		throws NotesException
	{
		return _session.createDateTime ("Today");
	}

	public DateTime
	getDateTime (
	 Date date)
		throws NotesException
	{
		return _session.createDateTime (date);
	}
}
