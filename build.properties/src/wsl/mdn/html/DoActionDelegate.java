/*	$Id: DoActionDelegate.java,v 1.4 2002/07/22 23:47:30 jonc Exp $
 *
 *	What happens when one clicks on a link
 *
 */
package wsl.mdn.html;

import java.util.Iterator;
import java.util.Vector;

import wsl.fw.servlet.*;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.resource.ResId;
import wsl.fw.datasource.*;
import wsl.fw.servlet.ServletBase;
import wsl.fw.servlet.ServletError;
import wsl.fw.security.User;
import wsl.mdn.dataview.DataView;
import wsl.mdn.guiconfig.MenuAction;
import wsl.mdn.guiconfig.Submenu;
import wsl.mdn.guiconfig.QueryRecords;
import wsl.mdn.guiconfig.TextAction;
import wsl.mdn.guiconfig.NewRecord;
import wsl.mdn.guiconfig.LogoutAction;
import wsl.mdn.guiconfig.MsgServerAction;
import wsl.fw.msgserver.*;
import wsl.mdn.dataview.MdnDataCache;

import java.io.IOException;
import javax.servlet.ServletException;

public class DoActionDelegate
	extends MdnHtmlServletDelegate
{
	//--------------------------------------------------------------------------
	// attributes

	private ActionDobj _ad = null;

	//--------------------------------------------------------------------------
	// construction

	/**
	 * ActionDobj ctor
	 * @param ad the ActionDobj
	 */
	public
	DoActionDelegate (
	 ActionDobj ad)
	{
		_ad = ad;
	}

	//--------------------------------------------------------------------------
	// process action

	public void run ()
		throws ServletException, IOException
	{
		// validate
		Util.argCheckNull (_ad);

		try
		{
			Util.argCheckNull (_request);
			Util.argCheckNull (_response);

			// get the userState
			UserState userState = getUserState ();
			Util.argCheckNull (userState);

			// get the menu action
			MsgServerAction msa = (MsgServerAction) userState.getCurrentMenu ();
			Util.argCheckNull (msa);

			// get the message server
			MessageServer ms = getUserState ().getCache ().getMessageServer (
				new Integer (msa.getMsgServerId ()));
			Util.argCheckNull (ms);

			// create an impl for the message server (hides remote)
			DataSource ds = ms.createImpl ();
			Util.argCheckNull (ds);

			// get the user
			User user = userState.getUser ();
			Util.argCheckNull (user);

			Iterator it = _ad.doActionQuery (ds,
											 userState.getSessionId (),
											 user.getId ());
			PagedIteratorDelegate pmd;

			// create a PagedItrMsgDelegate and set it into the UserState
			pmd = new PagedItrMsgDelegate (it);
			userState.setPagedItDelegate (pmd);

			// delegate to PagedItrMsgDelegate to display the list
			delegate (pmd);

		} catch (Exception e)
		{
			onError (e);
		}
	}
}
