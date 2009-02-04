/*	$Id: MdnHtmlServlet.java,v 1.18 2004/01/06 02:09:39 tecris Exp $
 *
 *	Servlet for HTML
 *
 */
package wsl.mdn.html;

import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RMISecurityManager;
import java.rmi.NotBoundException;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;

import org.apache.ecs.*;
import org.apache.ecs.html.*;

import wsl.fw.html.WslHtmlTable;
import wsl.fw.html.WslHtmlUtil;
import wsl.fw.resource.ResId;
import wsl.fw.resource.ResourceManager;
import wsl.fw.security.User;
import wsl.fw.servlet.ServletError;
import wsl.fw.util.Config;
import wsl.fw.util.Log;
import wsl.fw.util.Util;
import wsl.mdn.common.MdnAdminConst;
import wsl.mdn.common.MdnServlet;
import wsl.mdn.dataview.MdnDataCache;
import wsl.mdn.guiconfig.LoginSettings;
import wsl.mdn.guiconfig.MenuAction;
import wsl.mdn.server.LicenseManager;
import wsl.mdn.server.MdnServer;
import wsl.mdn.wap.MdnWapDataCache;

public class MdnHtmlServlet
	extends MdnServlet
{
	/*
	 *	Configuration keys
	 */
	private static final String
		PresentationLang	= "MdnHtmlServlet.language",
		PresentationCharset	= "MdnHtmlServlet.charset";

	// resources
	public static final ResId
		ERR_LOGGEDIN		= new ResId ("MdnHtmlServlet.error.LoggedIn"),
		ERR_NOTLOGGEDIN		= new ResId ("MdnHtmlServlet.error.NotLoggedIn"),
		ERR_UNKNOWN_ACTION	= new ResId ("MdnHtmlServlet.error.UnknownAction"),
		ERR_REC_NOT_FOUND	= new ResId ("MdnHtmlServlet.error.RecNotFound"),

		TEXT_LOGIN			= new ResId ("MdnHtmlServlet.text.Login"),
		TEXT_LOGOUT			= new ResId ("MdnHtmlServlet.text.Logout"),
		TEXT_PROMPTCOLON	= new ResId ("MdnHtmlServlet.text.PromptColon"),
		TEXT_NOERROROBJECT	= new ResId ("MdnHtmlServlet.text.errorpage.NoErrorObject"),
		TEXT_MESSAGE		= new ResId ("MdnHtmlServlet.text.errorpage.Message"),
		TEXT_EXCEPTION		= new ResId ("MdnHtmlServlet.text.errorpage.Exception"),
		TEXT_BACK			= new ResId ("MdnHtmlServlet.text.Back"),
		TEXT_MORE			= new ResId ("MdnHtmlServlet.text.More"),
		TEXT_END			= new ResId ("MdnHtmlServlet.text.End"),
		TEXT_OK				= new ResId ("MdnHtmlServlet.text.Ok"),
		TEXT_NOT_IMPL		= new ResId ("MdnHtmlServlet.text.NotImpl"),
		TEXT_ENTER			= new ResId ("MdnHtmlServlet.text.Enter"),
		TEXT_MAIN			= new ResId ("MdnHtmlServlet.text.Main"),
		ERR_UNHANDLED		= new ResId ("MdnHtmlServlet.error.unhandledException"),
		TEXT_STARTING		= new ResId ("MdnHtmlServlet.text.starting"),
		TEXT_VERSION		= new ResId ("mdn.versionText"),
		ERR_VERSION			= new ResId ("MdnHtmlServlet.error.version"),
		ERR_NO_SERVER		= new ResId ("MdnHtmlServlet.error.noServer"),

		TEXT_DEFAULT_LOGOUT_TITLE	= new ResId ("MdnHtmlServlet.text.defaultLogoutTitle"),
		TEXT_DEFAULT_LOGOUT_TEXT	= new ResId ("MdnHtmlServlet.text.defaultLogoutText"),
		TEXT_DEFAULT_SPLASH_TITLE	= new ResId ("MdnHtmlServlet.text.defaultSplashTitle"),
		TEXT_DEFAULT_SPLASH_TEXT	= new ResId ("MdnHtmlServlet.text.defaultSplashText"),
		TEXT_THE_FOLLOWING_ERROR	= new ResId ("MdnHtmlServlet.text.TheFollowingError"),
		TEXT_ERROR_TITLE			= new ResId ("MdnHtmlServlet.text.ErrorTitle");


	// HTML constants
	public final static String
		HTML_MIME_TYPE		= "text/html",
		HTML_XML_HEADER		= "<?xml version=\"1.0\"?>";

	// WML constants
	// the url to use when linking to the WMLServlet
	protected final static String HREF = "MdnHtmlServlet";

	// parameter variables
	public final static String
		PV_GROUP_ID			= "HtmlMdn.groupId",
		PV_MENUACTIONID		= "HtmlMdn.maid",
		PV_RECORD_INDEX		= "HtmlMdn.recindex",
		PV_PAGEDVECTORID	= "HtmlMdn.pvid",
		PV_PAGEDITERATORID	= "HtmlMdn.piid";

	// post field constants
	public final static String RP_FIELD_PREFIX  = "RPfp";

	// session variable
	// mdn servlet session variable for holding user state
	protected final static String SV_USERSTATE   = "MdnHtmlServlet.WmlUserState";

	// Action constants for actions peformed by this servlet
	public final static String
		ACT_SPLASH				= "HtmlMdn.splash",
		ACT_LOGIN				= "HtmlMdn.login",
		ACT_LOGOUT				= "HtmlMdn.logout",
		ACT_MENU				= "HtmlMdn.menu",
		ACT_QUERYRECORDS		= "HtmlMdn.query",
		ACT_NEWRECORD			= "HtmlMdn.new",
		ACT_EDITRECORD			= "HtmlMdn.edit",
		ACT_SAVERECORD			= "HtmlMdn.save",
		ACT_CANCELUPDATE		= "HtmlMdn.cancel",
		ACT_DELETERECORD		= "HtmlMdn.delete",
		ACT_CONFIRMDELETE		= "HtmlMdn.confirmdelete",
		ACT_SELECTRECORD		= "HtmlMdn.select",
		ACT_NEXTQUERYPAGE		= "HtmlMdn.nextpage",
		ACT_GROUPSUBQUERY		= "HtmlMdn.groupsub",
		ACT_NEXTGROUPPAGE		= "HtmlMdn.nextgroup",
		ACT_NEXTPVPAGE			= "HtmlMdn.nextpv",
		ACT_QUERYRESULT			= "HtmlMdn.qresult",
		ACT_MAINMENU			= "HtmlMdn.main",
		ACT_SHOWMENU			= "HtmlMdn.showmenu",
		ACT_MSGSERVER			= "HtmlMdn.msgserver",
		ACT_DO_CRITERIA_ACTION	= "HtmlMdn.docact",

		ACT_INBOX				= "HtmlMdn.inbox",
		ACT_INBOX_UNREAD		= "HtmlMdn.unread",
		ACT_INBOX_ALL			= "HtmlMdn.inboxall",
		ACT_FOLDERS				= "HtmlMdn.folders",
		ACT_SHOW_INFOSTORE		= "HtmlMdn.showis",
		ACT_CONTACTS			= "HtmlMdn.contacts",
		ACT_PRIVATE_CONTACTS	= "HtmlMdn.privcon",
		ACT_ADD_LISTS			= "HtmlMdn.addlists",
		ACT_SEARCH_BY_NAME		= "HtmlMdn.searchname",
		ACT_DO_SEARCH			= "HtmlMdn.dosearch",
		ACT_INBOX_MSG			= "HtmlMdn.inboxmsg",
		ACT_REPLY_MAIL			= "HtmlMdn.replymail",
		ACT_DELETE_MAIL			= "HtmlMdn.deletemail",
		ACT_FWD_MAIL_RESP		= "HtmlMdn.fwdmailresp",
		ACT_FWD_MAIL_SEND		= "HtmlMdn.fwdmailsend",
		ACT_SEND_MAIL			= "HtmlMdn.sendmail",
		ACT_CANCEL_MAIL			= "HtmlMdn.cancelmail",
		ACT_SHOW_CONTACT		= "HtmlMdn.showcontact",
		ACT_SHOW_FOLDER			= "HtmlMdn.showfolder",
		ACT_SHOW_MESSAGE		= "HtmlMdn.showmsg",
		ACT_CALENDAR			= "HtmlMdn.calendar",
		ACT_CAL_SHOW_DAY		= "HtmlMdn.calday",
		ACT_CAL_SHOW_WEEK		= "HtmlMdn.calweek",
		ACT_CAL_FIND_DAY		= "HtmlMdn.calfind";

	/*
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
	MdnHtmlServlet ()
	{
		try
		{
			// set the cache
			if (MdnDataCache.getCache () == null)
				MdnDataCache.setCache (new MdnWapDataCache ());

		} catch (Exception e)
		{
			e.printStackTrace ();
		}

		// log starting  and version
		Log.log (
		 TEXT_STARTING.getText ()
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

			/*
			 *	Decide what to do next depending on the RP_ACTION
			 */
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
				{
					userState.setMenu (menuId);
					//Log.debug ("\nSetting menu id = " + menuId + "\n");
				}

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
				else if (action.equals (ACT_SHOWMENU))            // main menu
					delegate (new MenuDelegate (false), request, response);

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
					//LicenseManager.releaseLicenseLock (lockKey);

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
					delegate (userState.getCurrentPagedQuery (), request, response);
				else if (action.equals (ACT_NEXTPVPAGE))      // next query page record
					delegate (userState.getCurrentPagedItDelegate (), request, response);
				else if (action.equals (ACT_GROUPSUBQUERY))      // query
					delegate (new GroupSubQueryDelegate (), request, response);
				else if (action.equals (ACT_NEXTGROUPPAGE))      // next query page record
					delegate (getUserState (request).getCurrentPagedGroupQuery (), request, response);
				else if (action.equals (ACT_MSGSERVER))      // msgserver
					delegate (new MsgServerDelegate (), request, response);
				else if (action.equals (ACT_DO_CRITERIA_ACTION))      // show message
					delegate (new CriteriaActionDelegate (), request, response);
				else if (action.equals (ACT_SHOW_MESSAGE))      // show message
					delegate (new ShowMessageDelegate (), request, response);
				else if (action.equals (ACT_REPLY_MAIL))      // reply mail
					delegate (new ReplyMailDelegate (), request, response);

				else if (action.equals (ACT_SEND_MAIL))      // send mail
					delegate (new SendMailDelegate (), request, response);

				else if (action.equals (ACT_DELETE_MAIL))
					delegate (new DeleteMailDelegate (), request, response);

				else if (action.equals (ACT_FWD_MAIL_RESP))
					delegate (new FwdMailRespDelegate (), request, response);
				else if (action.equals (ACT_FWD_MAIL_SEND))
					delegate (new FwdMailSendDelegate (), request, response);

				else
				{
					// unknown, go to menu
					Log.debug (ERR_LOGGEDIN.getText () + ERR_UNKNOWN_ACTION.getText ()
						+ action);
					delegate (new MenuDelegate (), request, response);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			onError (request, response, new ServletError (ERR_UNHANDLED.getText (), e));
		}
	}

	//--------------------------------------------------------------------------
	/**http://www.zodal.net/
	 * Handle splash action.
	 */
	void onSplash (
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
			// create a table
			WslHtmlTable table = new WslHtmlTable ();
			table.setAlign (AlignType.CENTER);

			// add the heading
			String splashText = ls.getSplashText ();
			if (splashText == null || splashText.length () == 0)
				splashText = TEXT_DEFAULT_SPLASH_TEXT.getText ();
			TD cell = new TD (getTitleElement (splashText));
			TR row = new TR (cell);
			table.addElement (row);

			//add the spacing row
			cell = new TD ();
			cell.setHeight (20);
			row = new TR (cell);
			table.addElement (row);

			// Login menu item
			String link = makeHref (response, ACT_LOGIN);
			A a = new A (link, TEXT_LOGIN.getText ());
			cell = new TD (a);
			row = new TR (cell);
			table.addElement (row);

			// output the client
			String title = ls.getSplashTitle ();
			if (title == null || title.length () == 0)
				title = TEXT_DEFAULT_SPLASH_TITLE.getText ();
			MdnClientCell client = new MdnClientCell (table, title);
			client.setHelpUrl ("/help/mdnhelp.html#bSplashScreen");
			outputClientCell (request, response, client);
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

		// cell properties
		MdnClientCell client = new MdnClientCell ();
		client.setAlign (AlignType.CENTER);
		client.setHelpUrl ("/help/mdnhelp.html#bLogout");

		// title
		String title = ls.getLogoutTitle ();
		if (title == null || title.length () == 0)
			title = TEXT_DEFAULT_LOGOUT_TITLE.getText ();
		client.setClientTitle (title);

		// form
		Form form = new Form ();
		form.setMethod (Form.POST);
		form.setAction (makeHref (response));
		client.addElement (form);

		// table
		WslHtmlTable table = new WslHtmlTable ();
		form.addElement (table);

		// heading
		String logoutText = ls.getLogoutText ();
		if (logoutText == null || logoutText.length () == 0)
			logoutText = TEXT_DEFAULT_LOGOUT_TEXT.getText ();
		TD cell = new TD (getTitleElement (logoutText));
		cell.setColSpan (2);
		TR row = new TR (cell);
		table.addElement (row);

		// spacing
		cell = new TD ();
		cell.setHeight (20);
		row = new TR (cell);
		table.addElement (row);

		// login button
		Input input = new Input (Input.SUBMIT, "Text", MdnHtmlServlet.TEXT_LOGIN.getText ());
		cell = new TD (input);
		row = new TR (cell);
		table.addElement (row);

		// action
		input = new Input (Input.HIDDEN, MdnHtmlServlet.RP_ACTION,
			MdnHtmlServlet.ACT_LOGIN);
		row.addElement (input);
		table.addElement (row);

		// output
		outputClientCell (request, response, client);
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
		// create a table
		WslHtmlTable table = new WslHtmlTable ();
		table.setAlign (AlignType.CENTER);

		// add the heading
		TD cell = new TD (getTitleElement (TEXT_THE_FOLLOWING_ERROR.getText ()));
		TR row = new TR (cell);
		table.addElement (row);

		//add the spacing row
		cell = new TD ();
		cell.setHeight (20);
		row = new TR (cell);
		table.addElement (row);

		// error message
		cell = new TD ();
		row = new TR (cell);
		table.addElement (row);

		// add items to card
		if (error == null)
			cell.addElement (TEXT_NOERROROBJECT.getText ());
		else
		{
			cell.addElement (TEXT_MESSAGE.getText () + WslHtmlUtil.esc (error._message));
			if (error._exception != null)
			{
				cell.addElement (new BR ());
				cell.addElement (TEXT_EXCEPTION.getText () + WslHtmlUtil.esc (error._exception.toString ()));
			}

			Log.error ("MdnHtmlServlet.onError: " + error._message, error._exception);
		}

		// set into the client area of the main doc
		MdnClientCell client = new MdnClientCell (table, TEXT_ERROR_TITLE.getText ());
		client.setHelpUrl ("/help/mdnhelp.html#bError");
		outputClientCell (request, response, client);
   }


   //---------------------------------------------------------------------------
   // response output

	/**
	 * Output the document
	 */
	public void
	outputDocument (
	 HttpServletRequest request,
	 HttpServletResponse response)
		throws IOException, ServletException
	{
		try
		{
			// get the document and set the client builder
			Document doc = getUserState (request).
						   getMainDocument (request, response);

			doc.getHtml ().setPrettyPrint (true);
			doc.getHead ().setPrettyPrint (true);
			doc.getBody ().setPrettyPrint (true);

			/*
			 *	Set output encoding.
			 */
			response.setContentType (getContentType ());

			/*
			 *	Display the document
			 */
			String strOut = doc.toString ();
			PrintWriter out = response.getWriter ();
			out.println (HTML_XML_HEADER);		// write xml headers
			out.println (strOut);				// write document

			out.close ();

		} catch (Exception e)
		{
			throw new ServletException (e.getMessage ());
		}
	}


	/**
	 * Output a client cell
	 * @param request the servlet request
	 * @param response the servlet response
	 * @param client the client cell
	 */
	public void
	outputClientCell (
	 HttpServletRequest request,
	 HttpServletResponse response,
	 MdnClientCell client)
		throws IOException, ServletException
	{
		MainDocument doc = getUserState (request).getMainDocument (request, response);
		doc.setClientAreaCell (request, client);
		doc.setTitle (new Title (client.getClientTitle ()));

		// output the document
		outputDocument (request, response);
	}

	/**
	 * Create and return a title element from a string
	 * @param str the text
	 * @return ConcreteElement the heading element
	 */
	public static ConcreteElement
	getTitleElement (
	 String str)
	{
		return new H2 (str);
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

		// add the MdnHtmlServlet context
		Config config = Config.getSingleton ();
		config.addContext (MdnAdminConst.HTMLSERVLET_CONTEXT);

		// do version check
		String serverVersion = LicenseManager.getServerVersion ();

		if (serverVersion == null)
		{
			// could not contact server, log error and exit
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
		 *	Get output parameters: encoding and language
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
			_content = HTML_MIME_TYPE;
		else
			_content = HTML_MIME_TYPE + "; charset=" + _charset;

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
		return WslHtmlUtil.makeHref (response, HREF);
	}

	public static String
	makeHref (
	 HttpServletResponse response,
	 String action)
	{
		return WslHtmlUtil.makeHref (response, HREF, action);
	}

	public static String
	makeHref (
	 HttpServletResponse response,
	 String action,
	 String subaction)
	{
		return WslHtmlUtil.makeHref (response, HREF, action, subaction);
	}

	public static String
	addParam (
	 String href,
	 String varName,
	 String value)
	{
		return WslHtmlUtil.addHrefParam (href, varName, value);
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
	// deprecated code

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
	}

}
