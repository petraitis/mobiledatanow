/**	$Id: FwdMailSendDelegate.java,v 1.5 2002/09/30 04:23:55 tecris Exp $
 *
 *	Forward email
 *
 */
package wsl.mdn.wap;

import org.apache.ecs.wml.*;

import wsl.fw.datasource.DataSource;
import wsl.fw.msgserver.MailMessageDobj;
import wsl.fw.msgserver.SessionMailMsgDobj;
import wsl.fw.msgserver.SessionFwMailMsgDobj;
import wsl.fw.msgserver.MessageServer;
import wsl.fw.resource.ResId;
import wsl.fw.servlet.ServletBase;
import wsl.fw.util.Util;
import wsl.fw.wml.WEUtil;
import wsl.fw.wml.WslInput;
import wsl.mdn.guiconfig.MsgServerAction;

import java.io.IOException;
import javax.servlet.ServletException;

public class FwdMailSendDelegate
	extends MdnWmlServletDelegate
{
	/*
	 *	Constants
	 */
	public static final String				// Request parameters
		FWD_TO			= "FMTo",
		FWD_SUBJECT		= "FMSsubject",
		FWD_BODY		= "FMSbody";

	/*
	 * Resources
	 */
    public static final ResId
		TEXT_SEPERATOR	= new ResId ("FwdMailSendDelegate.text.Seperator"),
		TEXT_SUCCESS	= new ResId ("FwdMailSendDelegate.text.Success"),
		TEXT_FAILURE	= new ResId ("FwdMailSendDelegate.text.Failure");

	/**
	 * Default ctor
	 */
	public
	FwdMailSendDelegate ()
	{
	}

	/**
	 * Handle actions for the FwdMailSend
	 * @throws IOException, standard exception thrown by servlet methods.
	 * @throws ServletException, standard exception thrown by servlet methods.
	 */
	public void
	run ()
		throws IOException, ServletException
	{
		/*
		 *	Attempt the mail-forward and indicate success
		 */
		try
		{
			/*
			 *	Get request parameters
			 */
			String strIndex = _request.getParameter (
								MdnWmlServlet.PV_RECORD_INDEX);
			String recipient = _request.getParameter (FWD_TO);
			String subject = _request.getParameter (FWD_SUBJECT);
			String body = _request.getParameter (FWD_BODY);

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
			 *	Build the Email Body
			 */
			String fwdBody = body + "\n"
								+ TEXT_SEPERATOR.getText () + "\n"
								+ mObj.getText ();

			/*
			 *	Build the Forwarded email
			 */
			SessionFwMailMsgDobj fwdObj = new SessionFwMailMsgDobj (
											userState.getSessionId (),
											mObj);
			fwdObj.setUserId (userState.getUser ().getId ());
			fwdObj.setSubject (subject);
			fwdObj.setText (fwdBody);
			fwdObj.setRecipient (recipient);

			/*
			 *	Hand the message off to the message server
			 */
			MsgServerAction msa = (MsgServerAction) userState.getCurrentMenu();

			MessageServer ms =
					userState.
					getCache ().
					getMessageServer (new Integer (msa.getMsgServerId ()));

			DataSource ds = ms.createImpl ();

			String str;
			if (ds.insert (fwdObj))
			{
				/*
				 *	If it sent ok, we present the user with the MailBox
				 */
				fwdToMailbox ();

			} else
			{
				/*
				 *	Present failure
				 */
				Card card = new Card ();

				P p = new P ();
				p.addElement (WEUtil.esc (TEXT_FAILURE.getText ()));
				card.addElement (p);

				/*
				 *	Bottom bits
				 */
				// Main
				Do doMain = new Do (DoType.OPTIONS,
									MdnWmlServlet.TEXT_MAIN.getText ());
				Go goMain = new Go (makeHref (MdnWmlServlet.ACT_MAINMENU),
									Method.GET);
				doMain.addElement (goMain);
				card.addElement (doMain);

				// Back
				Do doOp = new Do (DoType.PREV,
								  MdnWmlServlet.TEXT_BACK.getText ());
				doOp.addElement (new Prev ());
				card.addElement (doOp);

				wmlOutput (card, true);
			}

		} catch (Exception e)
		{
			onError(e);
		}
	}
}
