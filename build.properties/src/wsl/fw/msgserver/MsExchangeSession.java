/*	$Id: MsExchangeSession.java,v 1.1 2002/11/11 03:20:57 jonc Exp $
 *
 *	Session for an ImapSmtpMsgServer
 *
 */
package wsl.fw.msgserver;

public class MsExchangeSession
	extends UserSession
{
	/*
	 *	Instance variables
	 */
	private final String _loginUrl;

	/**
	 *	Constructors
	 */
	public
	MsExchangeSession (
	 SessionManager mgr,
	 String cookie,
	 String loginUrl)
	{
		super (mgr, cookie);
		_loginUrl = loginUrl;
	}

	/*
	 *
	 */
	public void
	logout ()
	{
		try
		{
			MSExchangeInterface.logout (_loginUrl);

		} catch (MessageServerException e)
		{
		}

		super.logout ();
	}
}
