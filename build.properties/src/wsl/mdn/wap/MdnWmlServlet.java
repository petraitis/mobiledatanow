/**	$Id: MdnWmlServlet.java,v 1.12 2004/01/06 02:12:12 tecris Exp $
 *
 *	WML controller servlet for MDN
 *
 */
package wsl.mdn.wap;

import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import wsl.fw.resource.ResId;
import wsl.fw.resource.ResourceManager;
import wsl.fw.security.User;
import wsl.fw.servlet.ServletError;
import wsl.fw.util.Config;
import wsl.fw.util.Log;
import wsl.fw.util.Util;
import wsl.fw.wml.WEUtil;
import wsl.mdn.common.MdnAdminConst;
import wsl.mdn.common.MdnServlet;
import wsl.mdn.dataview.MdnDataCache;
import wsl.mdn.guiconfig.LoginSettings;
import wsl.mdn.guiconfig.MenuAction;
import wsl.mdn.server.LicenseManager;
import wsl.mdn.server.MdnServer;

import org.apache.ecs.Element;
import org.apache.ecs.wml.*;

public class MdnWmlServlet
	extends MdnServlet
{
	/*
	 *	Configuration keys
	 */
	private static final String
		PresentationLang	= "MdnWmlServlet.language",
		PresentationCharset	= "MdnWmlServlet.charset";

	//--------------------------------------------------------------------------
	// attributes
	private static SimpleDateFormat _df;
	{
		_df = new SimpleDateFormat ("EEE MMM d yyyy");
	}

	private static final int WML_WARNING_SIZE = 1500;

	// resources
	public static final ResId
		ERR_LOGGEDIN		= new ResId ("MdnWmlServlet.error.LoggedIn"),
		ERR_NOTLOGGEDIN		= new ResId ("MdnWmlServlet.error.NotLoggedIn"),
		ERR_UNKNOWN_ACTION	= new ResId ("MdnWmlServlet.error.UnknownAction"),
		ERR_REC_NOT_FOUND	= new ResId ("MdnWmlServlet.error.RecNotFound"),

		TEXT_LOGIN			= new ResId ("MdnWmlServlet.text.Login"),
		TEXT_LOGOUT			= new ResId ("MdnWmlServlet.text.Logout"),
		TEXT_PROMPTCOLON	= new ResId ("MdnWmlServlet.text.PromptColon"),
		TEXT_NOERROROBJECT	= new ResId ("MdnWmlServlet.text.errorpage.NoErrorObject"),
		TEXT_MESSAGE		= new ResId ("MdnWmlServlet.text.errorpage.Message"),
		TEXT_EXCEPTION		= new ResId ("MdnWmlServlet.text.errorpage.Exception"),
		TEXT_BACK			= new ResId ("MdnWmlServlet.text.Back"),
		TEXT_MORE			= new ResId ("MdnWmlServlet.text.More"),
		TEXT_END			= new ResId ("MdnWmlServlet.text.End"),
		TEXT_OK				= new ResId ("MdnWmlServlet.text.Ok"),
		TEXT_NOT_IMPL		= new ResId ("MdnWmlServlet.text.NotImpl"),
		TEXT_ENTER			= new ResId ("MdnWmlServlet.text.Enter"),
		TEXT_MAIN			= new ResId ("MdnWmlServlet.text.Main"),
		ERR_UNHANDLED		= new ResId ("MdnWmlServlet.error.unhandledException"),
		TEXT_STARTING		= new ResId ("MdnWmlServlet.text.starting"),
		TEXT_VERSION		= new ResId ("mdn.versionText"),
		ERR_VERSION			= new ResId ("MdnWmlServlet.error.version"),
		ERR_NO_SERVER		= new ResId ("MdnWmlServlet.error.noServer"),
		TEXT_SPLASH_TITLE	= new ResId ("MdnWmlServlet.text.title.splash"),
		TEXT_LOGOUT_TITLE	= new ResId ("MdnWmlServlet.text.title.logout"),
		TEXT_ERROR_TITLE	= new ResId ("MdnWmlServlet.text.title.error");

	// WML constants
	public final static String
		WML_MIME_TYPE		= "text/vnd.wap.wml";

	// the url to use when linking to the WMLServlet
	protected final static String HREF = "MdnWmlServlet";

	// parameter variables
	public final static String
		PV_GROUP_ID			= "id.g",
		PV_MENUACTIONID		= "id.m",
		PV_RECORD_INDEX		= "id.r",
		PV_ADDRESS_LISTID	= "id.a",
		PV_INFOSTOREID		= "id.i",
		PV_PAGEDVECTORID	= "id.pv",
		PV_PAGEDITERATORID	= "id.pi";

	// post field constants
	public final static String RP_FIELD_PREFIX  = "RPfp";

	// session variable
	// mdn servlet session variable for holding user state
	protected final static String SV_USERSTATE   = "MdnWmlServlet.WmlUserState";

	// Action constants for actions peformed by this servlet
	public final static String
		ACT_SPLASH			= "a.splash",
		ACT_LOGIN			= "a.login",
		ACT_LOGOUT			= "a.logout",
		ACT_MENU			= "a.menu",
		ACT_QUERYRECORDS	= "a.query",
		ACT_NEWRECORD		= "a.new",
		ACT_EDITRECORD		= "a.edit",
		ACT_SAVERECORD		= "a.save",
		ACT_CANCELUPDATE	= "a.cancel",
		ACT_DELETERECORD	= "a.delete",
		ACT_CONFIRMDELETE	= "a.confirmdelete",
		ACT_SELECTRECORD	= "a.select",
		ACT_NEXTQUERYPAGE	= "a.nextpage",
		ACT_NEXTPVPAGE		= "a.nextpv",
		ACT_GROUPSUBQUERY	= "a.groupsub",
		ACT_NEXTGROUPPAGE	= "a.nextgroup",
		ACT_QUERYRESULT		= "a.qresult",
		ACT_MAINMENU		= "a.main",
		ACT_MSGSERVER		= "a.msgserver",
		ACT_DO_CRITERIA_ACTION  = "a.doca",
		ACT_REPLY_MAIL		= "a.replymail",
		ACT_SEND_MAIL		= "a.sendmail",
		ACT_DELETE_MAIL		= "a.delmail",
		ACT_FWD_MAIL_RESP	= "a.fwdmailr",
		ACT_FWD_MAIL_SEND	= "a.fwdmails",
		ACT_CANCEL_MAIL		= "a.cancelmail",
		ACT_SHOW_MESSAGE	= "a.showmsg",
		ACT_INBOX_MSG		= "a.ibmsg";

	/**
	 *	State variables
	 */
	private static String _lang;			// output language
	private static String _charset;			// i/o encoding charset
	private static String _content;			// output Content-Type

	//--------------------------------------------------------------------------
	/**
	 * Default Constructor.
	 */
	public
	MdnWmlServlet ()
	{
		try
		{
			// set the cache
			if (MdnDataCache.getCache () == null)
				MdnDataCache.setCache (new MdnWapDataCache ());
		}
		catch (Exception e)
		{
			Log.error ("WML Servlet ctor", e);
		}

		// log starting  and version
		Log.log (TEXT_STARTING.getText ()
			+ " " + TEXT_VERSION.getText ()
			+ " " + MdnServer.VERSION_NUMBER);
	}

	//--------------------------------------------------------------------------
	/**
	 * Handle all HTTP requests, don't care if they are GET or POST.
	 */
	protected void
	service (
	 HttpServletRequest request,
	 HttpServletResponse response)
		throws IOException, ServletException
	{
		try
		{
			/*
			 *	Set request encoding, if required, to handle
			 *	intl input conversion.
			 */
			if (getCharset () != null)
			{
				request.setCharacterEncoding (getCharset ());
			}

			// note, call wmlOutput () to write the decks, do not use
			// response.getWriter directly as wmlOutput sets encoding etc.
			// get the WmlUserState  and action
			String action = request.getParameter (RP_ACTION);
			UserState userState = getUserState (request);

			// check if logged in
			if (!userState.isLoggedIn ())
			{
				// following actions permitted if not logged in
				// if no or invalid action, default to splash screen
				if (action == null)                       // null, use splash
					onSplash (request, response);
				else if (action.equals (ACT_SPLASH))       // splash
					onSplash (request, response);
				else if (action.equals (ACT_LOGIN))        // login
				{
					// get LoginDelegate to do the login, this does the actual
					// lock check and will display an error page if there is a
					// license problem or the number of users exceeds the maximum
					delegate (new LoginDelegate (), request, response);
					userState.setLockKey (getLockKey (request));
				}
				//else if (action.equals (ACT_PAGED_SELECT)) // paged select
				//    onPagedSelect (request, response);
				// else if (action.equals (ACT_ERROR))        // error
				//     onError (request, response, null);
				else
				{
					// unknown, go to splash
					Log.debug (ERR_NOTLOGGEDIN.getText ()
						+ ERR_UNKNOWN_ACTION.getText () + action);
					onSplash (request, response);
				}

			} else
			{
				// set the menuid
				String menuId = request.getParameter (PV_MENUACTIONID);
				if (menuId != null && menuId.length () > 0)
					getUserState (request).setMenu (menuId);

				// set the pvid
				String pvId = request.getParameter (PV_PAGEDVECTORID);
				userState.setCurrentPvId (pvId);

				String pItId = request.getParameter (PV_PAGEDITERATORID);
				userState.setCurrentPItId (pItId);

				// if a logged in user selects menu or does anything invalid
				// then goto the menu
				if (action == null || action.equals (ACT_MENU)) // menu
					delegate (new MenuDelegate (), request, response);
				else if (action.equals (ACT_LOGIN))            // login
					delegate (new LoginDelegate (), request, response);
				else if (action.equals (ACT_MAINMENU))            // main menu
				{
					getUserState (request).clearMenus ();
					delegate (new MenuDelegate (), request, response);
				}
				else if (action.equals (ACT_LOGOUT))
				{
					/*
					 *	Logout. Handle general cleanup
					 */
					userState.logout ();
					
					/*
					 * show logout page and free lock
					 */
					onLogout (request, response);

				} else if (action.equals (ACT_NEWRECORD))         // new record
					delegate (new NewRecordDelegate (), request, response);
				else if (action.equals (ACT_EDITRECORD))        // edit record
					delegate (new EditRecordDelegate (), request, response);
				else if (action.equals (ACT_SAVERECORD))        // edit record
					delegate (new SaveRecordDelegate (), request, response);
				else if (action.equals (ACT_CONFIRMDELETE))      // confirm delete record
					delegate (new ConfirmDeleteDelegate (), request, response);
				else if (action.equals (ACT_DELETERECORD))      // delete record
					delegate (new DeleteRecordDelegate (), request, response);
				else if (action.equals (ACT_QUERYRECORDS))      // query
					delegate (new QueryRecordsDelegate (), request, response);
				else if (action.equals (ACT_QUERYRESULT))      // query
					delegate (new QueryResultDelegate (), request, response);
				else if (action.equals (ACT_SELECTRECORD))      // select record
					delegate (new ShowRecordDelegate (), request, response);
				else if (action.equals (ACT_NEXTQUERYPAGE))      // next query page record
					delegate (getUserState (request).getCurrentPagedQuery (), request, response);
				else if (action.equals (ACT_NEXTPVPAGE))      // next query page record
					delegate (getUserState (request).getCurrentPagedItDelegate (), request, response);
				else if (action.equals (ACT_GROUPSUBQUERY))      // query
					delegate (new GroupSubQueryDelegate (), request, response);
				else if (action.equals (ACT_NEXTGROUPPAGE))      // next query page record
					delegate (getUserState (request).getCurrentPagedGroupQuery (), request, response);
				else if (action.equals (ACT_MSGSERVER))      // message server
					delegate (new MsgServerDelegate (), request, response);
				else if (action.equals (ACT_INBOX_MSG))      // inbox message
					delegate (new ShowMailDelegate (), request, response);
				else if (action.equals (ACT_REPLY_MAIL))      // reply mail
					delegate (new ReplyMailDelegate (), request, response);

				else if (action.equals (ACT_SEND_MAIL))
					delegate (new SendMailDelegate (), request, response);

				else if (action.equals (ACT_DELETE_MAIL))
					delegate (new DeleteMailDelegate (), request, response);

				else if (action.equals (ACT_FWD_MAIL_RESP))
					delegate (new FwdMailRespDelegate (), request, response);
				else if (action.equals (ACT_FWD_MAIL_SEND))
					delegate (new FwdMailSendDelegate (), request, response);

				else if (action.equals (ACT_SHOW_MESSAGE))      // show message
					delegate (new ShowMessageDelegate (), request, response);
				else if (action.equals (ACT_DO_CRITERIA_ACTION))      // show message
					delegate (new CriteriaActionDelegate (), request, response);

				else
				{
					// unknown, go to menu
					Log.debug (ERR_LOGGEDIN.getText () + ERR_UNKNOWN_ACTION.getText ()
						+ action);
					delegate (new MenuDelegate (), request, response);
				}
			}

		} catch (Exception e)
		{
			System.err.println ("MdnWmlServet: " + e.getMessage ());
			e.printStackTrace ();
			onError (request, response, new ServletError (ERR_UNHANDLED.getText (), e));
		}
	}

	//--------------------------------------------------------------------------
	/**
	 * Handle splash action.
	 */
	private void
	onSplash (
	 HttpServletRequest request,
	 HttpServletResponse response)
		throws IOException, ServletException
	{
		// load splashscreen info
		// if splashscreen then display it
		LoginSettings ls = getUserState (request).getLoginSettings ();
		assert ls != null;
		if (ls.getFlags (LoginSettings.FLAG_USE_SPLASHSCREEN))
		{
			// create new card and add text
			Card card = new Card ();
			card.setTitle (WEUtil.esc (TEXT_SPLASH_TITLE.getText ()));
			P p =  new P (Alignment.LEFT, Mode.WRAP);
			card.addElement (p);
			if (!Util.isEmpty (ls.getSplashTitle ()))
			{
				p.addElement (WEUtil.esc (ls.getSplashTitle ()));
				p.addElement (new BR ());
			}
			p.addElement (WEUtil.esc (ls.getSplashText ()));

			// href login
			String href = makeHref (response, ACT_LOGIN);
			card.addElement (WEUtil.makeHrefP (href, TEXT_ENTER.getText ()));

			// add do/go for login
			Do doLogin = new Do (DoType.ACCEPT, TEXT_ENTER.getText ());
			doLogin.addElement (new Go (makeHref (response, ACT_LOGIN)));
			card.addElement (doLogin);

			// write to output
			wmlOutput (response, card, true);
		}
		else
		{
			// no splashscreen, go to login screen
			delegate (new LoginDelegate (), request, response);
		}
	}

	//--------------------------------------------------------------------------
	/**
	 * Handle logout action.
	 */
	protected void
	onLogout (
	 HttpServletRequest request,
	 HttpServletResponse response)
		throws IOException, ServletException
	{
		// get the login settings
		LoginSettings ls = getUserState (request).getLoginSettings ();

		// perform the logout by clearing the user state session variable,
		// this will cause any future requests to create a new UserState
		setSessionVar (request, SV_USERSTATE, null);

		// create new card for logout and add text
		Card card = new Card ();
		card.setTitle (WEUtil.esc (TEXT_LOGOUT_TITLE.getText ()));
		P p =  new P (Alignment.LEFT, Mode.WRAP);
		card.addElement (p);
		p.addElement (WEUtil.esc (ls.getLogoutTitle ()));
		p.addElement (new BR ());
		p.addElement (WEUtil.esc (ls.getLogoutText ()));

		// add do/go for login
		Do doLogin = new Do (DoType.ACCEPT, TEXT_LOGIN.getText ());
		doLogin.addElement (new Go (makeHref (response, ACT_LOGIN)));
		card.addElement (doLogin);

		// add prev element to go to login
		Do doPrev = new Do (DoType.PREV);
		doPrev.addElement (new Go (makeHref (response, ACT_LOGIN)));
		card.addElement (doPrev);

		// write to output
		wmlOutput (response, card, true);
	}

	//--------------------------------------------------------------------------
	/**
	 * Handle error action.
	 */
	protected void
	onError (
	 HttpServletRequest request,
	 HttpServletResponse response,
	 ServletError error)
		throws IOException, ServletException
	{
		Util.argCheckNull (request);
		Util.argCheckNull (response);

		// create card
		Card card = new Card ();
		card.setTitle (WEUtil.esc (TEXT_ERROR_TITLE.getText ()));

		// add items to card
		P p =  new P (Alignment.LEFT, Mode.WRAP);
		card.addElement (p);
		if (error == null)
			p.addElement (TEXT_NOERROROBJECT.getText ());
		else
		{
			if (error._message != null && error._message.length () > 0)
			{
				p.addElement (TEXT_MESSAGE.getText () + WEUtil.esc (error._message));
				p.addElement (new BR ());
			}
			if (error._exception != null)
				p.addElement (WEUtil.esc (error._exception.getMessage ()));

			Log.error ("MdnWmlServlet.onError: " + error._message, error._exception);
		}

		// Back
		Do doBack = new Do (DoType.PREV, MdnWmlServlet.TEXT_BACK.getText ());
		doBack.addElement (new Prev ());
		card.addElement (doBack);

		// Main
		Do doMain = new Do (DoType.OPTIONS, MdnWmlServlet.TEXT_MAIN.getText ());
		Go goMain = new Go (makeHref (response, MdnWmlServlet.ACT_MAINMENU), Method.GET);
		doMain.addElement (goMain);
		card.addElement (doMain);

		// ouput wml
		wmlOutput (response, card, true);
   }

	//--------------------------------------------------------------------------
	/**
	 * Output wml to the servlet http output stream.
	 * @param response, the HttpServletResponse that has the output stream.
	 * @param cards, an ECS element (usualy a wml card) or an array or List
	 *   of ECS Elements. These will be added (in order) to a deck and output.
	 * @param disableCache, if true appropriate http anf wml headers will be
	 *   included to disable caching of the page.
	 */
	public static void
	wmlOutput (
	 HttpServletResponse response,
	 Object cards,
	 boolean disableCache)
		throws ServletException
	{
	   try
		{
			/*
			 *	Set output encoding
			 */
			response.setContentType (getContentType ());

			/*
			 *	Build up the Deck
			 */
			WML wml = new WML ();

			// if required disable caching
			if (disableCache)
				wml.addElement (WEUtil.disableCaching (null, response));

			// add the cards or other elements to the deck
			if (cards instanceof Element)
				wml.addElement ((Element) cards);
			else if (cards instanceof Element[])
			{
				Element[] elements = (Element[]) cards;
				for (int i = 0; i < elements.length; i++)
					wml.addElement (elements[i]);
			}
			else if (cards instanceof List)
			{
				List elements = (List) cards;
				for (int i = 0; i < elements.size (); i++)
					wml.addElement ((Element) elements.get (i));
			}
			else
				assert false: "invalid param to wmlOutput";

			// create document and add deck to it
			WMLDocument doc = new WMLDocument (wml);

			/*
			 *	Display the Document
			 */
			PrintWriter out = response.getWriter ();
			String wmlText = doc.toString ();
			out.println (wmlText);

			out.close ();

			// log or warn if size is too large
			if (wmlText.length () > WML_WARNING_SIZE)
				Log.warning ("MdnWmlServlet.wmlOutput WML may be too large: "
					+ wmlText.length () + " characters");
			else
				Log.debug ("MdnWmlServlet.wmlOutput " + wmlText.length ()
					+ " characters");

			// Log WML
			if (Config.getProp (MdnAdminConst.WMLSERVLET_LOG_WML, 0) > 0)
			{
				Log.log ("WML Raw: " + wmlText);
				Log.log ("WML Text:\n" + WMLOutRenderer.parse (wmlText));
			}

		} catch (Exception e)
		{
			throw new ServletException (e.getMessage ());
		}
	}

	//--------------------------------------------------------------------------
	/**
	 * Servlet init override.
	 */
	public void
	init (
	 ServletConfig cfg)
		throws ServletException
	{
		// call superclass first
		super.init (cfg);

		// add the MdnWmlServlet context
		Config config = Config.getSingleton ();
		config.addContext (MdnAdminConst.WMLSERVLET_CONTEXT);

		// do version check
		String serverVersion = LicenseManager.getServerVersion ();

		if (serverVersion == null)
		{
			// could not contact the server, log error and exit
			Log.fatal (ERR_NO_SERVER.getText ());

			throw new ServletException (ERR_NO_SERVER.getText ());
		}

		if (!MdnServer.VERSION_NUMBER.equals (serverVersion))
		{
			// invalid version, log error and exit
			String errorMsg = ERR_VERSION.getText () + "[ " + serverVersion
				+ " / " + MdnServer.VERSION_NUMBER + " ]";

			Log.fatal (errorMsg);

			throw new ServletException (errorMsg);
		}

		/*
		 *	Get output parameters: language and encoding
		 */
		_lang = config.getProperty (PresentationLang);
		_charset = config.getProperty (PresentationCharset);

		/*
		 *	Set defaults, if no parameters found
		 */
		if (_lang == null)
			_lang = Locale.getDefault ().getLanguage ();
		else
		{
			/*
			 *	Refetch the resources using the new Locale
			 */
			ResourceManager.setLocale (new Locale (_lang, ""));
		}
		if (_charset == null)
			_content = WML_MIME_TYPE;
		else
			_content = WML_MIME_TYPE + "; charset=" + _charset;

		Log.debug ("Language=" + _lang + ", Content-Type=" + _content);
	}

	//--------------------------------------------------------------------------
	/**
	 * Servlet shutdown.
	 */
	public void
	destroy ()
	{
		// If required :
		// stop the notification listener
		// DataManager.getDataManager ().stopRemoteNotifications ();

		// call superclass last
		super.destroy ();
	}

	//--------------------------------------------------------------------------
	/**
	 * Wrappers for the .makeHref functions that pass in the
	 * HREF for this servlet
	 */
	public static String
	makeHref (
	 HttpServletResponse response)
	{
		return WEUtil.makeHref (response, HREF);
	}

	public static String
	makeHref (
	 HttpServletResponse response,
	 String action)
	{
		return WEUtil.makeHref (response, HREF, action);
	}

	public static String
	makeHref (
	 HttpServletResponse response,
	 String action,
	 String subaction)
	{
		return WEUtil.makeHref (response, HREF, action, subaction);
	}

	public static String
	addParam (
	 String href,
	 String varName,
	 String value)
	{
		return WEUtil.addParam (href, varName, value);
	}

	/**
	 *
	 */
	public static String
	getLanguage ()
	{
		return _lang;
	}

	public static String
	getCharset ()
	{
		return _charset;
	}

	/**
	 *	Return Content-Type
	 */
	public static String
	getContentType ()
	{
		return _content;
	}

	//--------------------------------------------------------------------------
	/**
	 * Get the WmlUserState object for this user form the session var.
	 * @param request, the HttpServletRequest used to get the session var.
	 * @return the WmlUserState from the session var or a new one
	 */
	public static UserState
	getUserState (
	 HttpServletRequest request)
	{
		UserState userState = (UserState) getSessionVar (request, SV_USERSTATE);
		if (userState == null)
		{
		   userState = new UserState (request.getSession ());
			setSessionVar (request, SV_USERSTATE, userState);
		}
		return userState;
	}


	//--------------------------------------------------------------------------
	// generate a random var name
	/**
	 * @return String a random var name
	 */
	public static String
	getRandomVarName ()
	{
		// generate random number
		double rand = Math.random ();

		// turn into a six digit int
		int i = (int) (rand *= 1000000.0);

		// return as a string
		return "f" + String.valueOf (i);
	}


	//--------------------------------------------------------------------------
	// WAP date parsing

	/**
	 * convert date to string
	 * @param d the Date to parse
	 * @return the string representation of the date
	 */
	public synchronized static String
	dateToString (
	 Date d)
	{
		return _df.format (d);
	}

	/**
	 * convert string to date
	 * @param s the String to parse
	 * @return the Date representation of the string
	 */
	public synchronized static Date
	stringToDate (
	 String s)
	{
		try
		{
			return _df.parse (s);
		}
		catch (Exception e)
		{
			Log.error ("Cant parse date: " + s);
			return null;
		}
	}
}
