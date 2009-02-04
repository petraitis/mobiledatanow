/**	$Id: DeleteMailDelegate.java,v 1.6 2002/07/24 04:16:36 jonc Exp $
 *
 *	Handle deletion of email
 *
 */
package wsl.mdn.html;

import org.apache.ecs.html.*;
import org.apache.ecs.*;

import wsl.fw.html.WslHtmlTable;
import wsl.fw.resource.ResId;
import wsl.fw.util.Util;
import wsl.fw.datasource.DataSource;
import wsl.fw.html.WslHtmlUtil;
import wsl.fw.msgserver.MailMessageDobj;
import wsl.fw.msgserver.SessionMailMsgDobj;
import wsl.fw.msgserver.MessageServer;
import wsl.mdn.guiconfig.MsgServerAction;

import java.io.IOException;
import javax.servlet.ServletException;

public class DeleteMailDelegate
	extends MdnHtmlServletDelegate
{
	/*
	 *	Resources
	 */
	public static final ResId
		TEXT_TITLE			= new ResId ("DeleteMailDelegate.text.Title"),
		TEXT_DEL_SUCCESS	= new ResId ("DeleteMailDelegate.text.Success"),
		TEXT_DEL_ERROR		= new ResId ("DeleteMailDelegate.text.Error"),
		TEXT_BAD_INDEX		= new ResId ("DeleteMailDelegate.text.BadIndex");

	/**
	 * Default ctor
	 */
	public
	DeleteMailDelegate ()
	{
	}

	/**
	 * Handle actions for the DeleteMail
	 * @throws IOException, standard exception thrown by servlet methods.
	 * @throws ServletException, standard exception thrown by servlet methods.
	 */
	public void
	run ()
		throws IOException, ServletException
	{
		try
		{
			/*
			 *	Get request parameters
			 */
			String strIndex = _request.getParameter (
								MdnHtmlServlet.PV_RECORD_INDEX);

			/*
			 *	Get the MailMessage in question
			 */
			UserState userState = getUserState ();
			PagedItrMsgDelegate pmd = (PagedItrMsgDelegate)
								userState.
								getCurrentPagedItDelegate ();
			int index = Integer.parseInt (strIndex);
			MailMessageDobj mObj = (MailMessageDobj) pmd.getMessage (index);

			/*
			 *	Get the MessageServer to delete it
			 */
			MsgServerAction msa = (MsgServerAction)
								  userState.getCurrentMenu ();

			MessageServer ms = userState.
				getCache ().
				getMessageServer (new Integer (msa.getMsgServerId ()));

			DataSource ds = ms.createImpl ();		// create the backend

			/*
			 *	Create a session-signed copy to send back for deletion
			 */
			SessionMailMsgDobj dObj = new SessionMailMsgDobj (
											userState.getSessionId (),
											mObj);
			dObj.setUserId (userState.getUser ().getId ());	// old msgserv hack
			if (ds.delete (dObj))
			{
				/*
				 *	Present the user with the MailBox
				 *	we're looking at
				 */
				pmd.removeObject (index);
				fwdToMailbox ();

			} else
			{
				/*
				 *	Present failure result
				 */
				MdnClientCell client = new MdnClientCell ();
				client.setAlign (AlignType.CENTER);

				WslHtmlTable table = new WslHtmlTable ();
				client.addElement (table);

				String str = WslHtmlUtil.esc (TEXT_DEL_ERROR.getText ());

				TD cell = new TD (MdnHtmlServlet.getTitleElement (str));
				TR row = new TR (cell);
				cell.setColSpan (2);
				table.addElement (row);

				/*
				 *	Hand off link
				 */
				// Menu link
				String href = makeHref (MdnHtmlServlet.ACT_MENU);
				A a = new A (href, WslHtmlUtil.esc ("Menu"));
				cell = new TD (a);
				row = new TR (cell);
				cell.setColSpan (2);
				table.addElement (row);

				outputClientCell (client);
			}

		} catch (Exception e)
		{
			onError(e);
		}
	}
}
