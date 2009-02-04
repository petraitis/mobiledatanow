/*	$Id: UserState.java,v 1.7 2004/01/06 02:09:39 tecris Exp $
 *
 * Holds all state information for a given user session and caches any
 * presentation information.
 *
 */
package wsl.mdn.html;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ecs.*;
import org.apache.ecs.html.*;

import wsl.fw.msgserver.MessageServer;
import wsl.fw.msgserver.ActionDobj;
import wsl.fw.datasource.DataManager;
import wsl.fw.datasource.DataSource;
import wsl.fw.datasource.Query;
import wsl.fw.datasource.QueryCriterium;
import wsl.fw.datasource.RecordSet;
import wsl.fw.security.GroupMembership;
import wsl.fw.security.SecurityId;
import wsl.fw.security.User;
import wsl.fw.util.Log;
import wsl.fw.util.Util;
import wsl.mdn.dataview.MdnDataCache;
import wsl.mdn.dataview.Record;
import wsl.mdn.guiconfig.LoginSettings;
import wsl.mdn.guiconfig.LogoutAction;
import wsl.mdn.guiconfig.MenuAction;
import wsl.mdn.guiconfig.Submenu;
import wsl.mdn.guiconfig.TextAction;
import wsl.mdn.wap.MdnWapDataCache;
import wsl.mdn.server.LicenseManager;

import wsl.fw.security.SecurityException;
import wsl.fw.datasource.DataSourceException;

import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionBindingEvent;

public class UserState
	implements HttpSessionBindingListener
{
	// attributes
	private String _sessionId;
	private String lockKey;

	private User          _user          = null;
	private MenuAction    _rootMenu      = null;
	private MenuAction    _currentMenu   = null;
	private String _currPvId = "";
	private String _curPItId = "";
	private Hashtable _piterQueries = new Hashtable ();
	private Hashtable _pagedQueries = new Hashtable ();
	private Hashtable _groupQueries = new Hashtable ();
	private Hashtable _newRecs = new Hashtable ();
	private Vector _msgServers = new Vector ();

	private MainDocument _mainDoc = null;
	private MdnMenuTree _menuTree = null;

	// iterator for the inbox
	private PagedIteratorDelegate it;
	//--------------------------------------------------------------------------
	/**
	 * Default constructor.
	 */
	public
	UserState (
	 HttpSession session)
	{
		_sessionId = session.getId ();
	}

	//--------------------------------------------------------------------------
	/**
	 * @return true if the user is logged in.
	 */
	public boolean
	isLoggedIn ()
	{
		return _user != null;
	}

	//--------------------------------------------------------------------------
	/**
	 * @return the cachd LoginSettings for this user.
	 */
	public LoginSettings
	getLoginSettings ()
	{
		try
		{
			return getCache ().getLoginSettings ();
		}
		catch (DataSourceException e)
		{
			e.printStackTrace ();
		}
		return null;
	}

	//--------------------------------------------------------------------------
	/**
	 * Login the user.
	 * @param username, the username.
	 * @param password, the password, may not be required (depends on the
	 *   LoginSettings flags).
	 * @return true if the user is successfully logged in.
	 */
	public boolean
	login (
	 String username,
	 String password)
	{
		Log.debug ("UserState.login");

		// perform the login
		try
		{
			_user = getCache ().getUser (username);
			if (_user == null)
			{
				Log.log (User.ERR_USER_NOT_FOUND1.getText () + " ["
					+ username + "] " + User.ERR_USER_NOT_FOUND2.getText ());
				return false;
			}

			// validate password
			if (getLoginSettings ().getFlags (LoginSettings.FLAG_REQUIRE_PASSWORD))
			{
				// test the user against the security id
				SecurityId sid = new SecurityId (username, password);
				if (!_user.identityCheck (sid))
				{
					_user = null;
					Log.log (User.ERR_INCORRECT_PASSWORD.getText () + " ["
						+ sid.getName () + "]");
				}
			}
		}
		catch (Exception e)
		{
			_user = null;
		}
		return (_user != null);
	}

	/**
	 *	Logout the user.
	 *	Inform all message servers that we're saying goodbye.
	 *  & release license lock
	 */
	public void
	logout ()
	{
		Iterator it = _msgServers.iterator ();
		while (it.hasNext ())
		{
			MessageServer msgServer = (MessageServer) it.next ();
			DataSource ds = msgServer.createImpl ();

			ActionDobj actLogout = new ActionDobj (
										ActionDobj.AT_LOGOUT,
										"logout",
										ds.getName ());
			try
			{
				actLogout.doActionQuery (ds, _sessionId, _user.getId ());

			} catch (DataSourceException e)
			{
			}
		}
		LicenseManager.releaseLicenseLock (lockKey);
	}

	public void
	setLockKey (
	 String lockKey)
	{
		this.lockKey = lockKey;
	}

	/**
	 *	@return associated sessionId
	 */
	public String
	getSessionId ()
	{
		return _sessionId;
	}

	/**
	 * @return User the logged in user
	 */
	public User
	getUser ()
	{
		return _user;
	}

	//--------------------------------------------------------------------------
	// menu tree

	/**
	 * @return MdnMenuTree the menu tree
	 */
	public MdnMenuTree
	getMenuTree ()
	{
		// return
		return _menuTree;
	}

	/**
	 * Set the menu tree
	 * @param tree the menu tree
	 */
	public void
	setMenuTree (
	 MdnMenuTree menuTree)
	{
		_menuTree = menuTree;
	}

	//--------------------------------------------------------------------------
	// menu actions

	/**
	 * @return MenuAction the root menu
	 */
	public MenuAction
	getRootMenu ()
	{
		// if null, get
		if (_rootMenu == null)
			getCurrentMenu ();
		return _rootMenu;
	}

	/**
	 * Clear the root and current menu
	 */
	public void
	clearMenus ()
	{
		_currentMenu = null;
		_rootMenu = null;
		_menuTree = null;
	}

	/**
	 * @return int the id of the current menu action
	 */
	public Object
	getCurrentMenuActionId ()
	{
		return (getCurrentMenu () == null)? null: getCurrentMenu ().getId ();
	}

	/**
	 * @return the currently selected menu. If there is no valid current
	 *   selection then the main menu is returned.
	 */
	public MenuAction
	getCurrentMenu ()
	{
		assert _user != null;

		if (_currentMenu == null)
		{
			if (_rootMenu == null)
			{
				// no mainmenu yet, construct the main menu object and then load
				// base menu items for group (s)
				_rootMenu = new Submenu ("Main Menu", "Main Menu", null, null);

				try
				{
					// find the groups the user is a member of
					boolean isFirst = true;
					Vector gmems = _user.getGroupMemberships ();
					for (int i = 0; gmems != null && i < gmems.size (); i++)
					{
						GroupMembership gm = (GroupMembership)gmems.elementAt (i);
						if (gm != null)
						{
							// add a spaced between multiple group menus
							if (isFirst)
								isFirst = false;
							else
								;//_rootMenu.addChildAtEnd (new TextAction ());

							// get the group menu
							Vector v = getCache ().getGroupMenus (new Integer (gm.getGroupId ()));

							// add the group menu items to main menu
							for (int j = 0; v != null && j < v.size (); j++)
								_rootMenu.addChildAtEnd ((MenuAction) v.get (j));
							}
					}
				}
				catch (DataSourceException e)
				{
					Log.error ("UserState.getMenu: " + e.toString ());
				}

				// add the logout entry
				_rootMenu.addChildAtEnd (new LogoutAction ());
			}

			_currentMenu = _rootMenu;
		}

		return _currentMenu;
	}

	//--------------------------------------------------------------------------
	/**
	 * Select the menu specified by the menuId.
	 * @param menuId, the id of the menu to select, or null to select the main
	 *   menu.
	 * @return true if the specified menu is found.
	 */
	public boolean
	setMenu (
	 Object menuId)
	{
		boolean bFound = false;

		// ensure mainmenu is loaded and default to it
		getCurrentMenu ();

		if (menuId == null)
		{
			bFound = true;
			_currentMenu = _rootMenu;
		}
		else
		{
			_currentMenu = findMenu (menuId, _rootMenu);
			if (_currentMenu != null)
				bFound = true;
			else
				_currentMenu = _rootMenu;
		}

		return bFound;
	}

	//--------------------------------------------------------------------------
	/**
	 * Recursively scan through menus to find the MenuAction with menuId.
	 * @param menuId, the menu id to search for.
	 * @param parentMenu, the parent to start searching from.
	 */
	public MenuAction
	findMenu (
	 Object menuId,
	 MenuAction parentMenu)
	{
		// if no parent, search from root
		if (parentMenu == null)
			parentMenu = _rootMenu;

		// delegate
		return (parentMenu == null)? null: parentMenu.findMenu (menuId);
	}

	//--------------------------------------------------------------------------
	/**
	 * @return the data view cache.
	 */
	public MdnWapDataCache
	getCache ()
	{
		return (MdnWapDataCache)MdnDataCache.getCache ();
	}

	//--------------------------------------------------------------------------
	// PagedVectorDelegates

	/**
	 * Set the PagedVectorDelegate
	 * @param maid the menu action id to map against
	 * @param pagedVector
	 */
	public void
	setPagedQuery (
	 Object maid,
	 PagedSelectDelegate pagedQuery)
	{
		Util.argCheckNull (maid);
		_pagedQueries.put (maid, pagedQuery);
	}

	/**
	 * @param maid the menu action id to map against
	 * @return PagedVectorDelegate
	 */
	public PagedVectorDelegate
	getPagedQuery (
	 Object maid)
	{
		return (PagedVectorDelegate)_pagedQueries.get (maid);
	}

	/**
	 * @return PagedVectorDelegate the current paged query
	 */
	public PagedVectorDelegate
	getCurrentPagedQuery ()
	{
		return getPagedQuery (getCurrentMenuActionId ());
	}

	/**
	 * Set the PagedVectorDelegate
	 * @param maid the menu action id to map against
	 * @param pagedVector
	 */
	public void
	setPagedGroupQuery (
	 Object maid,
	 PagedGroupSelectDelegate pagedGroupQuery)
	{
		_groupQueries.put (maid, pagedGroupQuery);
	}

	/**
	 * @param maid the menu action id to map against
	 * @return PagedVectorDelegate
	 */
	public PagedVectorDelegate
	getPagedGroupQuery (
	 Object maid)
	{
		return (PagedVectorDelegate)_groupQueries.get (maid);
	}

	/**
	 * @return PagedVectorDelegate the current paged group query
	 */
	public PagedVectorDelegate
	getCurrentPagedGroupQuery ()
	{
		return getPagedGroupQuery (getCurrentMenuActionId ());
	}

	//--------------------------------------------------------------------------
	// current record

	/**
	 * @return Record the current record
	 */
	public Record
	getRecord (
	 HttpServletRequest request)
	{
		// get the index param
		Record rec = null;
		String strIndex = request.getParameter (MdnHtmlServlet.PV_RECORD_INDEX);
		if (strIndex != null && strIndex.length () > 0)
		{
			int index = Integer.parseInt (strIndex);

			// get the record
			if (index >= 0)
			{
				PagedSelectDelegate psd = (PagedSelectDelegate)getCurrentPagedQuery ();
				rec = psd.getRecord (index);
			}
			else
				rec = getCurrentNewRecord ();
		}

		// return
		return rec;
	}


	//--------------------------------------------------------------------------
	// new record

	/**
	 * Set a new record against the current menu action
	 * @param rec the record to set
	 */
	public void
	setNewRecord (
	 Record rec)
	{
		_newRecs.put (this.getCurrentMenuActionId (), rec);
	}

	/**
	 * Get the current new record
	 * @return Record the current new record
	 */
	public Record
	getCurrentNewRecord ()
	{
		return (Record)_newRecs.get (this.getCurrentMenuActionId ());
	}


	//--------------------------------------------------------------------------
	// HTML Document

	/**
	 * Get (and create if necessary) the main HTML Document
	 * @return MainDocument the main HTML document
	 */
	public MainDocument
	getMainDocument (
	 HttpServletRequest request,
	 HttpServletResponse response)
	{
		// if null, create
		if (_mainDoc == null)
			_mainDoc = new MainDocument (request, response);

		// return
		return _mainDoc;
	}

	//--------------------------------------------------------------------------
	// pvid

	/**
	 * Set the current pvid
	 * @param pvid
	 */
	public void
	setCurrentPvId (
	 String pvid)
	{
		_currPvId = pvid;
	}

	/*
	 *	Paged Iterator series
	 */
	public void
	setCurrentPItId (
	 String piid)
	{
		_curPItId = piid;
	}

	public String
	getCurrentPItId ()
	{
		return _curPItId;
	}

	/**
	 * Given a PagedItDelegate, store it internally using its reference-id
	 *
	 * @param p the PagedItDelegate
	 */
	public void
	setPagedItDelegate (
	 PagedIteratorDelegate p)
	{
		_piterQueries.put (p.getId (), p);
	}

	/**
	 * Given a reference-id, return the corresponding PagedItDelegate
	 *
	 * @param pvid the paged vector id to map against
	 * @return PagedItDelegate
	 */
	public PagedIteratorDelegate
	getPagedItDelegate (
	 String piid)
	{
		if (piid == null || piid.length () == 0)
			return null;
		return (PagedIteratorDelegate) _piterQueries.get (piid);
	}

	/**
	 * @return PagedItDelegate the current paged query
	 */
	public PagedIteratorDelegate
	getCurrentPagedItDelegate ()
	{
		return (PagedIteratorDelegate) getPagedItDelegate (_curPItId);
	}

	/**
	 * @return Record the current pv record
	 */
	public Object
	getCurPIterObj (
	 HttpServletRequest request)
	{
		// get the index param
		String strIndex = request.getParameter (MdnHtmlServlet.PV_RECORD_INDEX);
		if (strIndex != null && strIndex.length () > 0)
		{
			int index = Integer.parseInt (strIndex);
			if (index >= 0)
			{
				PagedIteratorDelegate pd = getCurrentPagedItDelegate ();
				return pd.getObject (index);
			}
		}
		return null;
	}

	/**
	 *	Add Message Server to list that we need to notify on logout
	 */
	public void
	addMessageServer (
	 MessageServer newServer)
	{
		Iterator it = _msgServers.iterator ();
		while (it.hasNext ())
		{
			MessageServer msgServer = (MessageServer) it.next ();
			if (msgServer.getId () == newServer.getId ())
				return;			// it's already in the list
		}

		/*
		 *	Add new entry
		 */
		_msgServers.add (newServer);
	}

	public void
	valueBound (
	 HttpSessionBindingEvent e)
	{
	}

	public void
	valueUnbound (
	 HttpSessionBindingEvent e)
	{
		logout ();
	}

	/**
	 *	we need this iterator for the following scenario:
	 *	a user deletes a message and uses the "BACK" button from
	 *	the html/wml browser,That will bring back a cached page wich
	 *	includes the deleted message.At this point if the user
	 *	selects the last message in the list (wich no longer exists 
	 *	in the mail box) we get an ArrayOutOfBoundsException.We catch
	 *	this exception and we display the actual contents of the mail
	 *	box using PagedIteratorDelagate <it>
	 *
	 */
	public void
	setIterator (
	 PagedIteratorDelegate it)
	 {
		this.it = it;
	 }

	 public PagedIteratorDelegate
	 getIterator ()
	 {
		return it;
	 }
}
