/**	$Id: LoginDelegate.java,v 1.5 2003/02/10 20:48:30 tecris Exp $
 *
 * Delegate used by MdnWmlServlet to handle user login.
 *
 */
package wsl.mdn.wap;

import org.apache.ecs.wml.*;

import wsl.fw.resource.ResId;
import wsl.fw.servlet.ServletBase;
import wsl.fw.util.Log;
import wsl.fw.util.Util;
import wsl.fw.wml.WEUtil;
import wsl.fw.wml.WslInput;
import wsl.mdn.common.MdnServlet;
import wsl.mdn.guiconfig.LoginSettings;
import wsl.mdn.server.LicenseManager;

import java.io.IOException;
import javax.servlet.ServletException;

//------------------------------------------------------------------------------
public class LoginDelegate extends MdnWmlServletDelegate
{
	// resources
	public static final ResId
		TEXT_LOGIN_FAILED	= new ResId ("LoginDelegate.text.LoginFailed"),
		TEXT_LOCK_FAILED1	= new ResId ("LoginDelegate.text.lockFailed1"),
		TEXT_LOCK_FAILED2	= new ResId ("LoginDelegate.text.lockFailed2"),
		TEXT_NO_USERNAME	= new ResId ("LoginDelegate.error.NoUsername"),
		TEXT_TITLE			= new ResId ("LoginDelegate.text.title");

	// login param names
	public final static String RP_USER     = "RPusername";
	public final static String RP_PASSWORD = "RPpassword";

	private final static String NUMERIC_FORMAT = "*N";

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
			// check the license and get lock
			String lockKey = MdnServlet.getLockKey (_request);
			int lockRv = LicenseManager.getLicenseLock (lockKey);
			if (lockRv < 0)
			{
				// failed to get lock, display lock error page
				wmlOutput (buildLockFailedPage (lockRv), true);

			} else
			{
				// get the userState, username and password
				String username = _request.getParameter (RP_USER);
				String password = _request.getParameter (RP_PASSWORD);
				UserState userState = getUserState ();

				if (username != null)
				{
					if (username.length () == 0)
					{
						// if empty username, error
						wmlOutput (buildNoUsernamePage (), true);

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
							wmlOutput (buildLoginFailedPage (), true);
						}

					} else
					{
						/*
						 *	Build a login page with prefilled username
						 */
						wmlOutput (buildLoginPage (username), true);
					}

				} else
				{
					/*
					 *	No params, just build the login page
					 */
					wmlOutput (buildLoginPage (null), true);
				}
			}

		} catch (Exception e)
		{
			onError ("Login Error", e);
		}
	}

	/**
	 * @return the login wml card.
	 */
	private Card
	buildLoginPage (
	 String username)
	{
		// WML variable names
		final String VAR_USERNAME = "un";
		final String VAR_PASSWORD = "pw";

		LoginSettings ls = getUserState ().getLoginSettings ();
		boolean usePassword = ls.getFlags (LoginSettings.FLAG_REQUIRE_PASSWORD);

		// make ther card
		Card card = new Card ();
		card.setTitle (WEUtil.esc (TEXT_TITLE.getText ()));
		P p = new P (Alignment.LEFT, Mode.WRAP);
		card.addElement (p);

		// add the login title and text
		if (!Util.isEmpty (ls.getLoginTitle ()))
		{
			p.addElement (WEUtil.esc (ls.getLoginTitle ()));
			p.addElement (new BR ());
		}
		if (!Util.isEmpty (ls.getLoginText ()))
			p.addElement (WEUtil.esc (ls.getLoginText ()));

		// add the username prompt and input
		card.addElement (p = new P ());
		p.addElement (WEUtil.esc (ls.getUsernamePrompt ()));
		p.addElement (MdnWmlServlet.TEXT_PROMPTCOLON.getText ());
		p.addElement (
			new WslInput (
				VAR_USERNAME,
				WEUtil.esc (ls.getUsernamePrompt ()),
				username,
				null,
				false));

		// if password is required build the password input
		if (ls.getFlags (LoginSettings.FLAG_REQUIRE_PASSWORD))
		{
			card.addElement (p = new P ());
			p.addElement (WEUtil.esc (ls.getPasswordPrompt ()));
			p.addElement (MdnWmlServlet.TEXT_PROMPTCOLON.getText ());
			WslInput passwordInput = new WslInput (
				VAR_PASSWORD,
				WEUtil.esc (ls.getPasswordPrompt ()), 
				null, 
				Type.PASSWORD, 
				true);
			if (ls.getFlags (LoginSettings.FLAG_NUMERIC_PASSWORD))
				passwordInput.setFormat (NUMERIC_FORMAT);
			p.addElement (passwordInput);
		}

		// build the go login
		Go goLogin = new Go (makeHref (), Method.GET);
		goLogin.addElement (new Postfield (ServletBase.RP_ACTION, MdnWmlServlet.ACT_LOGIN));
		goLogin.addElement (new Postfield (RP_USER, WEUtil.makeVar (VAR_USERNAME)));
		if (ls.getFlags (LoginSettings.FLAG_REQUIRE_PASSWORD))
			goLogin.addElement (new Postfield (RP_PASSWORD, WEUtil.makeVar (VAR_PASSWORD)));
		else
			goLogin.addElement (new Postfield (RP_PASSWORD, ""));

		// add the href login
		Anchor a = new Anchor (WEUtil.esc (MdnWmlServlet.TEXT_LOGIN.getText ()), goLogin);
		a.addElement (WEUtil.esc (MdnWmlServlet.TEXT_LOGIN.getText ()));
		p = new P ();
		p.addElement (a);
		card.addElement (p);

		// add the do login
		Do doLogin = new Do (DoType.ACCEPT, MdnWmlServlet.TEXT_LOGIN.getText ());
		doLogin.addElement (goLogin);
		card.addElement (doLogin);

		return card;
	}

	//--------------------------------------------------------------------------
	/**
	 * @return the login failed wml card.
	 */
	private Card
	buildLoginFailedPage ()
	{
		// creatre card and para with login failed message
		Card card = new Card ();
		card.setTitle (WEUtil.esc (TEXT_TITLE.getText ()));
		P p =  new P (Alignment.LEFT, Mode.WRAP);
		card.addElement (p);
		p.addElement (TEXT_LOGIN_FAILED.getText ());

		// add do/go for login
		Do doLogin = new Do (DoType.ACCEPT, MdnWmlServlet.TEXT_LOGIN.getText ());
		doLogin.addElement (new Go (makeHref (MdnWmlServlet.ACT_LOGIN)));
		card.addElement (doLogin);

		return card;
	}

	//--------------------------------------------------------------------------
	/**
	 * @return the login failed wml card.
	 */
	private Card
	buildNoUsernamePage ()
	{
		// creatre card and para with login failed message
		Card card = new Card ();
		card.setTitle (WEUtil.esc (TEXT_TITLE.getText ()));
		P p =  new P (Alignment.LEFT, Mode.WRAP);
		card.addElement (p);
		p.addElement (TEXT_NO_USERNAME.getText ());

		// add do/go for login
		Do doLogin = new Do (DoType.ACCEPT, MdnWmlServlet.TEXT_LOGIN.getText ());
		doLogin.addElement (new Go (makeHref (MdnWmlServlet.ACT_LOGIN)));
		card.addElement (doLogin);

		return card;
	}

	//--------------------------------------------------------------------------
	/**
	 * @return the lock failed wml card.
	 */
	private Card
	buildLockFailedPage (
	 int lockRv)
	{
		Log.warning ("LoginDelegate.buildLockFailedPage: "
			+ LicenseManager.getErrorDescription (lockRv));

		// create card and para with lock failed message
		Card card = new Card ();
		card.setTitle (WEUtil.esc (TEXT_TITLE.getText ()));
		P p =  new P (Alignment.LEFT, Mode.WRAP);
		card.addElement (p);
		p.addElement (TEXT_LOCK_FAILED1.getText ());
		p.addElement (new BR ());
		p.addElement (LicenseManager.getErrorDescription (lockRv));
		p.addElement (new BR ());
		p.addElement (TEXT_LOCK_FAILED2.getText ());

		// add do/go for login
		Do doLogin = new Do (DoType.ACCEPT, MdnWmlServlet.TEXT_LOGIN.getText ());
		doLogin.addElement (new Go (makeHref (MdnWmlServlet.ACT_LOGIN)));
		card.addElement (doLogin);

		return card;
	}
}
