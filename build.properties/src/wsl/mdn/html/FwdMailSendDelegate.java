/**	$Id: FwdMailSendDelegate.java,v 1.5 2002/09/26 04:47:37 tecris Exp $
 *
 *	Forward Mail
 *
 */
package wsl.mdn.html;

import javax.servlet.ServletContext;
import javax.servlet.RequestDispatcher;

import org.apache.ecs.html.*;
import org.apache.ecs.*;

import wsl.fw.datasource.DataSource;
import wsl.fw.html.WslHtmlTable;
import wsl.fw.resource.ResId;
import wsl.fw.html.WslHtmlUtil;
import wsl.fw.msgserver.MailMessageDobj;
import wsl.fw.msgserver.SessionMailMsgDobj;
import wsl.fw.msgserver.SessionFwMailMsgDobj;
import wsl.fw.msgserver.MessageServer;
import wsl.fw.util.Util;
import wsl.mdn.guiconfig.MsgServerAction;

import java.io.IOException;
import javax.servlet.ServletException;

public class FwdMailSendDelegate
	extends MdnHtmlServletDelegate
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
								MdnHtmlServlet.PV_RECORD_INDEX);
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
			MsgServerAction msa = (MsgServerAction) userState.getCurrentMenu ();

			MessageServer ms =
					userState.
					getCache ().
					getMessageServer (new Integer (msa.getMsgServerId ()));

			DataSource ds = ms.createImpl ();

			if (ds.insert (fwdObj))
			{
				/*
				 *	If it sent ok, we present the user with the MailBox
				 */
				fwdToMailbox ();

			} else
			{
				/*
				 *	Present failure result
				 */
				String str = WslHtmlUtil.esc (TEXT_FAILURE.getText ());

				/*
				 *	Present results
				 */
				MdnClientCell client = new MdnClientCell ();
				client.setAlign (AlignType.CENTER);

				WslHtmlTable table = new WslHtmlTable ();
				client.addElement (table);

				TD cell = new TD (MdnHtmlServlet.getTitleElement (str));
				TR row = new TR (cell);
				cell.setColSpan (2);
				table.addElement (row);

				outputClientCell (client);
			}

		} catch (Exception e)
		{
			onError (e);
		}
	}
}
