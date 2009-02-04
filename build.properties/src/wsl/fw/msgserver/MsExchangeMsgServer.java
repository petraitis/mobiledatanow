/**	$Id: MsExchangeMsgServer.java,v 1.13 2003/01/15 23:44:17 tecris Exp $
 *
 *	Message Server for Microsoft Exchange
 *
 */
package wsl.fw.msgserver;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;
import wsl.fw.util.Log;
import wsl.fw.datasource.*;
import wsl.fw.security.User;
import wsl.fw.util.Util;

public class MsExchangeMsgServer
	extends MessageServer
{
	//--------------------------------------------------------------------------
	// constants

	// message server type constants
	public static final String
		MST_MSEX2000		= "MSEX2000",
		MST_MSEX55			= "MSEX55",
		MST_OUTLOOK2000		= "MSOL2000";

	// get folder messages flags
	private static final int GFM_FOLDERS = 1;
	private static final int GFM_MESSAGES = 2;
	private static final int GFM_BOTH = 3;

	private static boolean sharedLibLoaded = false;

	/*
	 *	State variables
	 */
	private SessionManager _sessionMgr = null;

	//--------------------------------------------------------------------------
	// construction

	public
	MsExchangeMsgServer ()
	{
		if (!sharedLibLoaded &&
			System.getProperty ("os.name").startsWith ("Windows"))
		{
			/*
			 *	We only attempt to use the Exchange Server stuff
			 *	if required.
			 *
			 *	There's no point in putting this into a static block
			 *	since the presentation side also makes calls to the
			 *	static Strings (and we don't need it loading on a client)
			 */
			try
			{
				Log.debug ("Attempting Loading mdnex.dll");
				System.loadLibrary ("mdnex");
				sharedLibLoaded = true;

			} catch (UnsatisfiedLinkError e)
			{
				Log.error ("Failed to load mdnex.dll :" + e.getMessage ());
			}
		}
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

	private UserSession
	getUserSession (
	 String sessionId,
	 String uid,
	 String loginUrl)
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
			new MsExchangeSession (mgr, sessionId, loginUrl);
		}

		return mgr.getUserSession (sessionId);
	}

	private UserSession
	getUserSession (
	 String sessionId)
	{
		return getSessionManager ().getUserSession (sessionId);
	}

	/**
	 *	Build the MS Exchange cookie for use by native methods
	 */
	private String
	getLoginUrl (
	 ActionQuery a)
		throws DataSourceException
	{
		/*
		 *	Get the profile
		 */
		String userId = a.getUserId ().toString ();
		MessageServerProfile msp = getFirstProfile (Integer.parseInt (userId));
		if (msp == null)
			throw new DataSourceException ("No Message Profile");

		return  MSExchangeInterface.buildLoginUrl (
					a.getSessionId (),
					this, msp.getProfileName(), msp.getPassword ());
	}

	/**
	 *	Build the MS Exchange cookie, give a MailMessageDobj
	 */
	private String
	getLoginUrl (
	 SessionMailMsgDobj mObj)
		throws DataSourceException
	{
		String userId = mObj.getUserId ().toString ();
		MessageServerProfile msp = getFirstProfile (Integer.parseInt (userId));
		if (msp == null)
			throw new DataSourceException ("No Message Profile");

		return  MSExchangeInterface.buildLoginUrl (
					mObj.getSessionId(),
					this, msp.getProfileName(), msp.getPassword ());
	}

	/**
	 *	Support for on-demand
	 */
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
	 * Perform an action (specified by constants in the query)
	 * @param q the query
	 * @return a RecordItrRef
	 */
	protected RecordItrRef
	doActionQuery (
	 ActionQuery q)
		throws DataSourceException
	{
		ActionDobj ad = q.getActionDobj();
		String sessionId = q.getSessionId ();
		String userId = q.getUserId ().toString ();
		UserSession session = getUserSession (
								sessionId,
								userId,
								getLoginUrl (q));

		// folder actions
		if (ad instanceof FolderDobj)
		{
			RecordItr itr = doFolderAction (q);
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
	 * Get the items in a folder
	 * @return the folder items
	 */
	protected RecordItr
	doFolderAction (
	 ActionQuery q)
		throws DataSourceException
	{
		// get the folder dobj
		FolderDobj fd = (FolderDobj)q.getActionDobj();

		// mail all
		switch (fd.getActionType ())
		{
		case FolderDobj.AT_ROOT_FOLDER:
			return getRootFolder (q);

		case FolderDobj.AT_DEFAULT_INBOX:	// inbox
			return getDefaultInbox (q);

		case FolderDobj.AT_MAIL_ALL:
			return getFolderItr (q, GFM_MESSAGES);

		case FolderDobj.AT_MAIL_UNREAD:
			return new MsExUnreadMailItr (getLoginUrl (q), fd.getId ());

		case FolderDobj.AT_DEFAULT_CONTACTS:	// contacts
			// if outlook get private contacts
			if (getType ().equals (MST_OUTLOOK2000))
				return getPrivateContacts (q);
			return presentAddressLists (q);

		case FolderDobj.AT_PRIVATE_CONTACTS:
			return getPrivateContacts (q);

		case FolderDobj.AT_ADDRESS_LIST:		// Exchange address list contacts
			return getAddresses (q);

		case FolderDobj.AT_CAL_TODAY:
		case FolderDobj.AT_CAL_TOMORROW:
		case FolderDobj.AT_CAL_DATE:
			return getDayAppointments(q);

		case FolderDobj.AT_FOLDER:
			return getFolderItr (q, GFM_BOTH);
		}

		/*
		 *	Default unknown case, present empty results
		 */
		return new RecordVectorItr (new Vector ());
	}

	private RecordItr
	getRootFolder (
	 ActionQuery q)
		throws DataSourceException
	{
		Vector result = new Vector ();
                String inboxFdId = "1000";
                String calFdId = "1000";

		// get the info stores
		Iterator it = selectInfoStores (q).iterator ();
		while (it.hasNext ())
		{
			InfoStoreDobj is = (InfoStoreDobj) it.next ();

			// sort defaults to top
			Vector v = selectFolders (q, is.getId());
			for (int i = 0; i < v.size(); i++)
			{
				// get the folder and compare to default folder names
				FolderDobj f = (FolderDobj) v.elementAt (i);

                                /**
                                 *  position Inbox,Contacts,Calendar
                                 *  to the desired indexes
                                 *  and keep folder id's to create the links for
                                 *  "Inbox Fast Access" & "Calendar-Today"
                                 */
				if (f.getName().equals(Folder.DFN_INBOX))
				{
                                    Object tmp = v.elementAt (2);
                                    v.setElementAt (f, 2);
                                    v.setElementAt (tmp, i);
                                    f.setName ("Inbox-Folders");
                                    inboxFdId = f.getId ();
				}
				else if (f.getName().equals(Folder.DFN_CALENDAR))
				{
                                    Object tmp = v.elementAt (1);
                                    v.setElementAt (f, 1);
                                    v.setElementAt (tmp, i);
                                    f.setName ("Calendar-All");
                                    calFdId = f.getId ();
				}
				else if (f.getName().equals(Folder.DFN_CONTACTS))
				{
                                    Object tmp = v.elementAt (0);
                                    v.setElementAt (f, 0);
                                    v.setElementAt (tmp, i);

				}
			}
			result.addAll (v);
		}
		result.add (0,new FolderDobj (
						"Inbox", inboxFdId,
						Folder.FCT_MAIL, FolderDobj.AT_MAIL_ALL));
                result.add (1,new FolderDobj ("Calendar-Today", calFdId,
                                Folder.FCT_CALENDAR, FolderDobj.AT_CAL_TODAY));
		return new RecordVectorItr (result);
	}

	private RecordItr
	getDefaultInbox (
	 ActionQuery q)
		throws DataSourceException
	{
		/*
		 *	Present following choices
		 *		- All Mail
		 *		- Unread Mail
		 *		- Inbox Subfolders
		 */
		FolderDobj fd = (FolderDobj) q.getActionDobj();
		Vector result = new Vector ();
		result.add (new FolderDobj (
						"All Mail", fd.getId(),
						Folder.FCT_MAIL, FolderDobj.AT_MAIL_ALL));
		result.add (new FolderDobj (
						"Unread Mail", fd.getId (),
						Folder.FCT_MAIL, FolderDobj.AT_MAIL_UNREAD));

		/*
		 *	Add subfolder and group both together.
		 */
		RecordItrGroup group = new RecordItrGroup ();
		group.add (new RecordVectorItr (result));
		group.add (getFolderItr (q, GFM_FOLDERS));
		return group;
	}

	/**
	 * Show appointments for a day
	 * @param q the ActionQuery
	 */
	private RecordItr
	getDayAppointments (
	 ActionQuery q)
		throws DataSourceException
	{
		// get the folderdobj
		FolderDobj fd = (FolderDobj)q.getActionDobj();

		// get date
		Date date;
		if (fd.getActionType () == FolderDobj.AT_CAL_TODAY ||
			fd.getActionType () == FolderDobj.AT_CAL_TOMORROW)
		{
			date = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			if (fd.getActionType () == FolderDobj.AT_CAL_TOMORROW)
				cal.add (Calendar.DATE, 1);
			date = cal.getTime();
		}
		else
			date = FolderDobj.stringToDate(fd.getName());

		return new MsExApptItr (getLoginUrl (q), date);
	}

	/**
	 *	Get subfolders and messages
	 *
	 * @param q the triggering ActionQuery
	 * @param flags filtering flag
	 * @return a RecordItr for ContactDobj objects
	 */
	private RecordItr
	getFolderItr (
	 ActionQuery q,
	 int flags)
		throws DataSourceException
	{
		String loginUrl = getLoginUrl (q);
		FolderDobj fd = (FolderDobj) q.getActionDobj ();

		RecordItrGroup both = new RecordItrGroup ();
		/*
		 *	Return singular requests
		 */
		switch (flags)
		{
		case GFM_FOLDERS:
			return new MsExSubfolderItr (loginUrl, fd.getId ());
		case GFM_MESSAGES:
			return new MsExMessageItr (loginUrl, fd.getId ());
		}

		/*
		 *	Return folder & messages
		 */

		both.add (new MsExSubfolderItr (loginUrl, fd.getId ()));
		both.add (new MsExMessageItr (loginUrl, fd.getId ()));
		return both;
	}

	/**
	 *	Select Private Contact info from the Message Server
	 */
	private RecordItr
	getPrivateContacts (
	 ActionQuery q)
		throws DataSourceException
	{

		String loginUrl = getLoginUrl (q);

		Vector v = new Vector ();
		try
		{
			// add a search action
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
		 *	Present the menu
		 *		- search by
		 *		- folders
		 *		- contacts
		 */
		RecordItrGroup display = new RecordItrGroup ();
		display.add (new RecordVectorItr (v));
		display.add (new MsExContactSubfolderItr (loginUrl));
		display.add (new MsExContactItr (loginUrl));
		return display;
	}

	/**
	 *	Given an Address folder, present the contents
	 */
	private RecordItr
	getAddresses (
	 ActionQuery q)
		throws DataSourceException
	{
		String loginUrl = getLoginUrl (q);
		String addrId = q.getActionDobj ().getId();

		Vector v = new Vector ();
		try
		{
			// add a search action
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
		 *	Present the menu
		 *		- search by
		 *		- contacts
		 */
		RecordItrGroup display = new RecordItrGroup ();
		display.add (new RecordVectorItr (v));
		display.add (new MsExAddrItr (loginUrl, addrId));
		return display;
	}

	/**
	 * Present a list of AddressFolders
	 * @param aq the AddressListQuery
	 * @return a RecordItr for AddressListDobj objects
	 */
	private RecordItr
	presentAddressLists  (
	 ActionQuery q)
		throws DataSourceException
	{
		String loginUrl = getLoginUrl (q);

		Vector v = new Vector ();

		// add private contacts to top
		v.add (
			new FolderDobj (
				"Private Contacts", Folder.FCT_CONTACT,
				FolderDobj.AT_PRIVATE_CONTACTS));

		// get addresslists and convert to dobjs
		AddressList [] lists = MSExchangeInterface.getAddressLists (loginUrl);
		if (lists != null)
		{
			for (int i = lists.length - 1; i >= 0; i--)
			{
				AddressList al = lists [i];
				v.add (
					new FolderDobj (
						al._name, al._id, Folder.FCT_MIXED,
						FolderDobj.AT_ADDRESS_LIST));
			}
		}
		return new RecordVectorItr (v);
	}

	/**
	 * Select the Exchange Message folders from an InfoStore
	 * @param fq the FoldersQuery
	 * @return a Vector of FolderDobj objects
	 */
	private Vector
	selectFolders (
	 ActionQuery q,
	 String infoStoreId)
		throws DataSourceException
	{
		// get the folders
		String loginUrl = getLoginUrl (q);
		MSExchangeInterface msi = new MSExchangeInterface();
		Folder[] folders = msi.getFolders(loginUrl, infoStoreId);

		// convert to dobjs
		Vector v = new Vector ();
		if (folders != null)
		{
			for (int i = folders.length - 1; i >= 0; i--)
			{
				// create and add the folder dobj
				FolderDobj fd = new FolderDobj (folders [i]);
				v.add (fd);
			}
		}
		return v;
	}

	/**
	 * Select the infostores for a login
	 * @param isq the InfoStoreQuery
	 * @return a RecordItr for InfoStoreDobj objects
	 */
	private Vector
	selectInfoStores (
	 ActionQuery q)
		throws DataSourceException
	{
		try
		{
			String loginUrl = getLoginUrl (q);
			MSExchangeInterface msi = new MSExchangeInterface ();

			// Get the info stores from native method
			InfoStore[] stores = msi.getInfoStores (loginUrl);
			if (stores == null)
			{
				throw new DataSourceException (
					"Login failed, possible profile access violation");
			}

			// convert to dobjs and build recordset
			Vector v = new Vector ();
			for (int i = stores.length - 1; i >= 0; i--)
			{
				InfoStoreDobj isd = new InfoStoreDobj (stores [i]);
				v.add (isd);
			}
			return v;

		} catch (MessageServerException e)
		{
			/*
			 *	Some code could go in here to reinterpret the error
			 *	messages received from the native code to something
			 *	the end-user can understand..
			 */
			Log.error ("Native Exception code: " + e.getCode ());
			throw (e);
		}
	}

	/**
	 * Insert (send) a mail message
	 * @param mm the mail message to send
	 * @return true if message sent successfully
	 */
	protected boolean
	insertMailMessage (
	 SessionMailMsgDobj mObj)
		throws DataSourceException
	{
		return MSExchangeInterface.sendMailMessage (
					getLoginUrl (mObj),
					Util.noNullStr (mObj.getSender ()),
					Util.noNullStr (mObj.getRecipient ()),
					Util.noNullStr (mObj.getSubject ()),
					Util.noNullStr (mObj.getText ()));
	}

	/**
	 *	Delete a mail message
	 *	@param mObj the mail message to delete
	 *	@return true if message deleted successfully
	 */
	protected boolean
	deleteMailMessage (
	 SessionMailMsgDobj mObj)
		throws DataSourceException
	{
		MSExchangeInterface.deleteMailMessage (
			getLoginUrl (mObj),
			mObj.getFolderId (),
			mObj.getId ());
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
		MSExchangeInterface.updateReadMailMessage (
			getLoginUrl (mObj),
			mObj.getFolderId (),
			mObj.getId ());
	}

	/**
	 * Insert (forward) a mail message
	 * @param mm the mail message to forward
	 * @return true if message sent successfully
	 */
	protected boolean
	insertFwMailMessage (
	 SessionFwMailMsgDobj mObj)
		throws DataSourceException
	{
		MSExchangeInterface.forwardMailMessage (
			getLoginUrl (mObj),
			mObj.getFolderId (),
			mObj.getId (),
			Util.noNullStr (mObj.getSender ()),
			Util.noNullStr (mObj.getRecipient ()),
			Util.noNullStr (mObj.getSubject ()),
			Util.noNullStr (mObj.getText ()));
		return true;
	}

	public int execInsertOrUpdate(String sql) throws DataSourceException {
		throw new DataSourceException("Not implemented");
	}
	
	

}
