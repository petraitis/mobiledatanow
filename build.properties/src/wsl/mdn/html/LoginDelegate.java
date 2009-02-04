/**	$Id: LoginDelegate.java,v 1.3 2002/07/18 03:18:59 jonc Exp $
 *
 * Delegate used by MdnHtmlServlet to handle user login.
 *
 */
package wsl.mdn.html;

import wsl.fw.servlet.ServletBase;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.resource.ResId;
import wsl.fw.html.*;
import wsl.mdn.guiconfig.LoginSettings;
import wsl.mdn.server.LicenseManager;
import wsl.mdn.common.MdnServlet;

import org.apache.ecs.*;
import org.apache.ecs.html.*;

import java.io.IOException;
import javax.servlet.ServletException;

public class LoginDelegate extends MdnHtmlServletDelegate
{
	// resources
	public static final ResId
		TEXT_LOGIN_FAILED	= new ResId ("HtmlLoginDelegate.text.LoginFailed"),
		TEXT_LOCK_FAILED1	= new ResId ("HtmlLoginDelegate.text.lockFailed1"),
		TEXT_LOCK_FAILED2	= new ResId ("HtmlLoginDelegate.text.lockFailed2"),
		TEXT_NO_USERNAME	= new ResId ("HtmlLoginDelegate.error.NoUsername"),
		TEXT_DEFAULT_LOGIN_TITLE
			= new ResId ("HtmlLoginDelegate.text.defaultLoginTitle"),
		TEXT_DEFAULT_LOGIN_TEXT
			= new ResId ("HtmlLoginDelegate.text.defaultLoginText"),
		TEXT_DEFAULT_USER	= new ResId ("HtmlLoginDelegate.text.defaultUser"),
		TEXT_DEFAULT_PASSWORD
			= new ResId ("HtmlLoginDelegate.text.defaultPassword");

	// login param names
	public final static String
		RP_USER		= "RPusername",
		RP_PASSWORD	= "RPpassword";

	//--------------------------------------------------------------------------
	/**
	 * Default constructor.
	 */
	public
	LoginDelegate ()
	{
	}

	//--------------------------------------------------------------------------
	/**
	 * Handle actions for the LoginDelegate.
	 * @throws IOException, standard exception thrown by servlet methods.
	 * @throws ServletException, standard exception thrown by servlet methods.
	 */
	public void
	run ()
		throws IOException, ServletException
	{
		try
		{
			Util.argCheckNull (_request);
			Util.argCheckNull (_response);

			// check the license and get lock
			String lockKey = MdnServlet.getLockKey (_request);
			int lockRv = LicenseManager.getLicenseLock (lockKey);
			if (lockRv < 0)
			{
				// failed to get lock, display lock error page
				onError (TEXT_LOCK_FAILED1.getText ());

			} else
			{
				// get the userState, username and password
				String username = _request.getParameter (RP_USER);
				String password = _request.getParameter (RP_PASSWORD);
				UserState userState = getUserState ();

				if (username != null)
				{
					// if empty username, error
					if (username.length () == 0)
					{
						// failed logon, build and output the
						onError (TEXT_NO_USERNAME.getText ());

					} else if (password != null && password.length () != 0)
					{
						// have a username and password, try to log in
						if (getUserState ().login (username, password))
						{
							// successful login, delegate to main menu
							getUserState ().clearMenus ();
							delegate (new MenuDelegate ());

						} else
						{
							// failed logon, build and output the
							onError (TEXT_LOGIN_FAILED.getText ());
						}

					} else
					{
						/*
						 *	Username is non-empty, but password is.
						 *	Build a login page with prefilled username
						 */
						outputClientCell (buildLoginPage (username));
					}

				} else
				{
					/*
					 *	No params, so just build the login page
					 */
					outputClientCell (buildLoginPage (null));
				}
			}

		} catch (Exception e)
		{
			onError (TEXT_LOGIN_FAILED.getText (), e);
		}
	}

	//--------------------------------------------------------------------------
	/**
	 * @return the login page
	 */
	private MdnClientCell
	buildLoginPage (
	 String username)
	{
		// login settings
		LoginSettings ls = getUserState ().getLoginSettings ();
		Util.argCheckNull (ls);

		// cell properties
		MdnClientCell client = new MdnClientCell ();
		client.setAlign (AlignType.CENTER);
		client.setHelpUrl ("/help/mdnhelp.html#bLogin");

		// title
		String title = ls.getLoginTitle ();
		if (title == null || title.length () == 0)
			title = TEXT_DEFAULT_LOGIN_TITLE.getText ();
		client.setClientTitle (title);

		// form
		Form form = new Form ();
		form.setMethod (Form.POST);
		form.setAction (makeHref ());
		client.addElement (form);

		// table
		WslHtmlTable table = new WslHtmlTable ();
		form.addElement (table);

		// heading
		String loginText = ls.getLoginText ();
		if (loginText == null || loginText.length () == 0)
			loginText = TEXT_DEFAULT_LOGIN_TEXT.getText ();
		TD cell = new TD (MdnHtmlServlet.getTitleElement (loginText));
		cell.setColSpan (2);
		TR row = new TR (cell);
		table.addElement (row);

		// user name
		String userText = ls.getUsernamePrompt ();
		if (userText == null || userText.length () == 0)
			userText = TEXT_DEFAULT_USER.getText ();
		userText += ":";
		cell = new TD (userText);
		row = new TR (cell);
		Input input = new Input (Input.TEXT,
								 RP_USER,
								 username == null ? "" : username);
		cell = new TD (input);
		row.addElement (cell);
		table.addElement (row);

		// password
		if (ls.getFlags (LoginSettings.FLAG_REQUIRE_PASSWORD))
		{
			String pwText = ls.getPasswordPrompt ();
			if (pwText == null || pwText.length () == 0)
				pwText = TEXT_DEFAULT_PASSWORD.getText ();
			pwText += ":";
			cell = new TD (pwText);
			row = new TR (cell);
			input = new Input (Input.PASSWORD, RP_PASSWORD, "");
			cell = new TD (input);
			row.addElement (cell);
			table.addElement (row);
		}

		// login button
		input = new Input (Input.SUBMIT, "loginButton", MdnHtmlServlet.TEXT_LOGIN.getText ());
		cell = new TD (input);
		row = new TR (cell);
		table.addElement (row);

		// action
		input = new Input (Input.HIDDEN, MdnHtmlServlet.RP_ACTION,
			MdnHtmlServlet.ACT_LOGIN);
		row.addElement (input);
		table.addElement (row);

		return client;
	}
}
