/* $Id: DominoMsgServer.java,v 1.6 2002/09/26 05:54:11 tecris Exp $
 *
 *  Domino Server
 *
 *	Handles interaction with Domino Servers
 */
package wsl.fw.msgserver;

import java.util.Iterator;
import java.util.Vector;
import lotus.domino.DateTime;
import lotus.domino.Document;
import lotus.domino.Item;

import wsl.fw.datasource.*;

import lotus.domino.NotesException;

public class DominoMsgServer
    extends MessageServer
{
	/*
	 *	Special Folder names under DominoNotes
	 */
	public static final String
		VIEW_INBOX			= "($Inbox)",
		VIEW_CALENDAR		= "($Calendar)",
        VIEW_PEOPLE         = "($People)",
		VIEW_CONTACTS		= "($Contacts)",
		VIEW_TODO			= "($ToDo)";

	/*
	 *
	 */
	private SessionManager _sessionMgr = null;

    public
	DominoMsgServer ()
    {
		super ();
    }

	/**
	 * 	Return the SessionManager, creating as required.
	 *
	 * 	We have this function since the class may be instantiated
	 *  by the Administrator, to manage the persistent properties,
	 *  but we don't really want the SessionManager around since
	 *  the instance isn't actually managing anything.
	 *
	 * @return the SessionManager
	 */
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
	 *  Class requirement
	 */
	public RecordItrRef
	doActionQuery (
	 ActionQuery q)
	 	throws DataSourceException
	{
        // folder actions
		ActionDobj ad = q.getActionDobj ();
		String sessionId = q.getSessionId ();
		String userId = q.getUserId ().toString ();
		DominoUserSession session = getUserSession (sessionId, userId);

		if (ad instanceof FolderDobj)
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
		switch (ad.getActionType ())
		{
		case ActionDobj.AT_LOGOUT:
			session.logout ();
			return null;
		}

        throw new DataSourceException (
			"Action not implemented: " + ad.getActionType ());
	}

	/**
	 *  Class requirement. Spool a message to be sent off
	 */
	public boolean
	insertMailMessage (
	 SessionMailMsgDobj m)
	 	throws DataSourceException
	{
		getUserSession (m.getSessionId ()).sendEmail (m);
		return true;
	}

	/**
	 *  Delete an email
	 */
	public boolean
	deleteMailMessage (
	 SessionMailMsgDobj m)
	 	throws DataSourceException
	{
		getUserSession (m.getSessionId ()).deleteEmail (m);
		return true;
	}

	private DominoUserSession
	getUserSession (
	 String sessionId,
	 String uid)
	 	throws DataSourceException
	{
		/*
		 *	Check if the current user is logged in on the Server
		 */
		SessionManager mgr = getSessionManager ();
		if (!mgr.isLoggedOn (sessionId))
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
			new DominoUserSession (mgr, sessionId,
				getHost (),
				profile.getProfileName (), profile.getPassword ());
		}

		return (DominoUserSession) mgr.getUserSession (sessionId);
	}

	private DominoUserSession
	getUserSession (
	 String sessionId)
	{
		return (DominoUserSession) getSessionManager ().
								   getUserSession (sessionId);
	}

	private RecordItr
	doFolderAction (
	 DominoUserSession session,
	 ActionQuery q)
	 	throws DataSourceException
	{
		FolderDobj folder = (FolderDobj) q.getActionDobj ();

		try
		{
			DateTime day;

			switch (folder.getActionType ())
			{
			case FolderDobj.AT_ROOT_FOLDER:			// Top level folders
				return selectRootFolders (session, q);

			case FolderDobj.AT_DEFAULT_INBOX:		// default INBOX
			case FolderDobj.AT_MAIL_ALL:			// display all mail
			case FolderDobj.AT_MAIL_UNREAD:			// display unread mail
				return session.getMailFolderItr (folder);

			case FolderDobj.AT_DEFAULT_CONTACTS:	// display contacts
			case FolderDobj.AT_ADDRESS_LIST:		// address list contacts
				return getContactFolderItr (session);

			case FolderDobj.AT_TASK_LIST:			// ToDo list
				return session.getTaskFolderItr (folder);

			case FolderDobj.AT_DEFAULT_CALENDAR:	// Calendar
				return displayCalendarGroups (q);

			case FolderDobj.AT_CAL_TODAY:
				/*
				 *	From today until tomorrow
				 */
				return session.getApptFolderItr  (folder, session.getToday ());

			case FolderDobj.AT_CAL_TOMORROW:
				/*
				 *	From Tomorrow until Day after Tomorrow
				 */
				day = session.getToday ();
				day.adjustDay (1);
				return session.getApptFolderItr  (folder, day);

			case FolderDobj.AT_CAL_DATE:
				day = session.getDateTime (FolderDobj.stringToDate(folder.getName ()));
				return session.getApptFolderItr  (folder, day);

			case FolderDobj.AT_FOLDER:
				return selectFolderMessages (session, q);
			}

		} catch (NotesException e)
		{
			throw new DataSourceException ( "Domino: " + e.getMessage ());
		}

		throw new DataSourceException (
					"Unimplemented action: " + folder.getActionType ());
	}

	/*
	 *	Supporting folder actions
	 */
	private static String
	getDocValue (
	 Document doc,
	 String key)
	 	throws NotesException
	{
		Item item = doc.getFirstItem (key);
		if (item == null)
			return "";
		return item.getText ();
	}

	/**
	 * Return the information stores available under Domino
	 */
	private RecordItr
	selectRootFolders (
	 DominoUserSession session,
	 ActionQuery q)
	 	throws DataSourceException
	{
		/*
		 *	Pull out the Folders, and stuff them into the result
		 *
		 *
		 *	Resort:
		 *		- Inbox to the top,
         *      - People/Contacts next.
		 *		- Remove sub-folders
		 *		- () folders removed
		 */
		FolderDobj inbox = null;
        FolderDobj contacts = null;
		Vector normal = new Vector ();
		Vector braced = new Vector ();

		Iterator folders = session.getRootFolders ();
		while (folders.hasNext ())
		{
			FolderDobj folder = (FolderDobj) folders.next ();
			String id = folder.getId ();
			String name = folder.getName ();

			if (id.equals (VIEW_INBOX))
				inbox = folder;
			else if (id.equals (VIEW_PEOPLE))
				contacts = folder;
			else if (id.indexOf ("\\") >= 0)
			{
				/*
				 *	It's a folder underneath a parent folder
				 *		eg: To do's\By Category
				 *			To do's\By Status
				 *
				 *	We don't need to see this, unless the user
				 *	is actually listing the parent folder
				 */
				continue;

			} else if (name.startsWith ("("))
				braced.addElement (folder);
			else
				normal.addElement (folder);
		}

		/*
		 *	Put it all together...
		 */
		Vector display = new Vector ();
		display.add (inbox);
        if (contacts != null)
            display.add (contacts);
		for (int i = 0; i < normal.size (); i++)
			display.add (normal.elementAt (i));

		return new RecordVectorItr (display);
	}

	private RecordItr
	displayCalendarGroups (
	 ActionQuery q)
	{
		/*
		 *	Let's build grouping folder selections
		 *		- Today
		 *		- Tomorrow
		 *		- This Week
		 *		- Next Week
		 */
		Vector v = new Vector ();

		FolderDobj folder = (FolderDobj) q.getActionDobj ();
		String id = folder.getId ();
		v.add (new FolderDobj ("Today",
								   id, Folder.FCT_MIXED,
								   ActionDobj.AT_CAL_TODAY));
		v.add (new FolderDobj ("Tomorrow",
								   id, Folder.FCT_MIXED,
								   ActionDobj.AT_CAL_TOMORROW));
		v.add (new FolderDobj ("This Week",
								   id, Folder.FCT_MIXED,
								   ActionDobj.AT_CAL_THIS_WEEK));
		v.add (new FolderDobj ("Next Week",
								   id, Folder.FCT_MIXED,
								   ActionDobj.AT_CAL_NEXT_WEEK));

		return new RecordVectorItr (v);
	}

	private RecordItr
	selectFolderMessages (
	 DominoUserSession session,
	 ActionQuery q)
	 	throws DataSourceException, NotesException
	{
		/*
		 *	Read in the folder's list of folders & messages
		 *
		 *	List folders before items
		 */
		FolderDobj folder = (FolderDobj) q.getActionDobj ();

		/*
		 *	Get folders listing first.
		 *
		 *	Query the Root Folders (again), which returns
		 *	everything, to find matching names if any.
		 */
		Vector v = new Vector ();

		Iterator folders = session.getRootFolders ();
		while (folders.hasNext ())
		{
			FolderDobj fObj = (FolderDobj) folders.next ();

			String id = fObj.getId ();
			int basePt = id.lastIndexOf ('\\');

			if (basePt >= 0 &&
				id.substring (0, basePt).equals (folder.getId ()))
			{
				v.add (new FolderDobj (
								DominoUserSession.cookIdName (id),
								id,
								Folder.FCT_MIXED,
								ActionDobj.AT_FOLDER));

			}
		}

		/*
		 *
		 */
		RecordItrGroup group = new RecordItrGroup ();
		group.add (new RecordVectorItr (v));
		group.add (session.getFolderMessages (folder));
		return group;
	}

	/**
	 *	Return a iterator that presents:
	 *		- search edit
	 *		- list of contact names
	 */
	private RecordItr
	getContactFolderItr (
	 DominoUserSession session)
		throws DataSourceException
	{
		Vector v = new Vector ();

		try
		{
			// add a Criteria that filters on name and ContactDobjs
			v.add (new CriteriaAction (
					CriteriaAction.AT_SEARCH_BY_NAME,
					"Search by Name", "",
					Class.forName ("wsl.fw.msgserver.ContactDobj"),
					"Name", "Search"));

		} catch (ClassNotFoundException e)
		{
			throw new DataSourceException ("Internal error: " + e.getMessage ());
		}

		/*
		 *
		 */
		RecordItrGroup group = new RecordItrGroup ();
		group.add (new RecordVectorItr (v));
		group.add (session.getContactFolderItr ());
		return group;
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

	public int execInsertOrUpdate(String sql) throws DataSourceException {
		throw new DataSourceException("Not implemented");
	}
	
	
}
