/**	$Id: DeleteMailDelegate.java,v 1.5 2002/07/24 04:16:36 jonc Exp $
 *
 *	Delete's selected email
 *
 */
package wsl.mdn.wap;

import org.apache.ecs.wml.*;

import wsl.fw.datasource.DataSource;
import wsl.fw.msgserver.MailMessageDobj;
import wsl.fw.msgserver.SessionMailMsgDobj;
import wsl.fw.msgserver.MessageServer;
import wsl.fw.resource.ResId;
import wsl.fw.util.Util;
import wsl.fw.wml.WEUtil;
import wsl.mdn.guiconfig.MsgServerAction;

import java.io.IOException;
import javax.servlet.ServletException;

public class DeleteMailDelegate
	extends MdnWmlServletDelegate
{
	//--------------------------------------------------------------------------
	// resources
	public static final ResId
		TEXT_TITLE			= new ResId ("DeleteMailDelegate.text.Title"),
		TEXT_DEL_SUCCESS	= new ResId ("DeleteMailDelegate.text.Success"),
		TEXT_DEL_ERROR		= new ResId ("DeleteMailDelegate.text.Error"),
		TEXT_BAD_INDEX		= new ResId ("DeleteMailDelegate.text.BadIndex");

	/**
	 * Default ctor
	 */
	public DeleteMailDelegate()
	{
	}

	//--------------------------------------------------------------------------
	/**
	 * Handle actions for the EditRecordDelegate.
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
								MdnWmlServlet.PV_RECORD_INDEX);

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

				// make the card
				Card card = new Card ();
				card.setTitle (WEUtil.esc (TEXT_TITLE.getText ()));
				P p = new P ();
				card.addElement (p);

				p.addElement (WEUtil.esc (TEXT_DEL_ERROR.getText ()));

				// Main
				Do doMain = new Do (DoType.OPTIONS,
									MdnWmlServlet.TEXT_MAIN.getText ());
				Go goMain = new Go (makeHref (MdnWmlServlet.ACT_MAINMENU),
									Method.GET);
				doMain.addElement (goMain);
				card.addElement (doMain);

				// Back
				Do doOp = new Do (DoType.PREV, MdnWmlServlet.TEXT_BACK.getText ());
				doOp.addElement (new Prev());
				card.addElement (doOp);

				wmlOutput (card, true);
			}

		} catch (Exception e)
		{
			onError(e);
		}
	}
}
