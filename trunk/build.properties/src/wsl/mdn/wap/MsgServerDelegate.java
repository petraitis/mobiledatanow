/**	$Id: MsgServerDelegate.java,v 1.3 2002/07/22 23:47:30 jonc Exp $
 *
 * 	What to do when we get MessageServer requests
 *
 */
package wsl.mdn.wap;

import java.util.Iterator;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.resource.ResId;
import wsl.fw.wml.WEUtil;
import wsl.fw.servlet.ServletBase;
import wsl.fw.servlet.ServletError;
import wsl.fw.datasource.*;
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

import org.apache.ecs.wml.*;

import java.io.IOException;
import javax.servlet.ServletException;
import java.util.Vector;

public class MsgServerDelegate extends MdnWmlServletDelegate
{
	//--------------------------------------------------------------------------
	// construction

	public
	MsgServerDelegate ()
	{
	}

	//--------------------------------------------------------------------------
	// build card
	public void
	run ()
		throws ServletException, IOException
	{
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
			MessageServer ms = userState.
				getCache ().
				getMessageServer (new Integer (msa.getMsgServerId ()));
			Util.argCheckNull (ms);

			/*
			 *	Add Message Server on list to notify on logout
			 */
			userState.addMessageServer (ms);

			// create an impl for the message server (hides remote)
			DataSource ds = ms.createImpl ();
			Util.argCheckNull (ds);

			// create an action query and execute it
			User user = userState.getUser ();
			Util.argCheckNull (user);

			// create a root folder action and execute it
			FolderDobj fd = new FolderDobj (
								"Root",
								Folder.FCT_MIXED,
								FolderDobj.AT_ROOT_FOLDER);
			Iterator it = fd.doActionQuery (ds,
											userState.getSessionId (),
											user.getId ());

			// delegate to paged message delegate
			PagedItrMsgDelegate pmd = new PagedItrMsgDelegate (it);
			userState.setPagedItDelegate (pmd);
			delegate (pmd);

		} catch (Exception e)
		{
			onError (e);
		}
	}
}
