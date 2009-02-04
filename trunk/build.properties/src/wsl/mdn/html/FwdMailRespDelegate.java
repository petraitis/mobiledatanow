/**	$Id: FwdMailRespDelegate.java,v 1.2 2002/11/14 03:42:36 tecris Exp $
 *
 *	Obtain Responses for Forwarding Mail
 *
 */
package wsl.mdn.html;

import org.apache.ecs.html.*;
import org.apache.ecs.*;

import org.apache.ecs.html.*;
import org.apache.ecs.*;

import wsl.fw.datasource.DataSource;
import wsl.fw.html.WslHtmlTable;
import wsl.fw.html.WslHtmlUtil;
import wsl.fw.msgserver.MailMessageDobj;
import wsl.fw.resource.ResId;

import java.io.IOException;
import javax.servlet.ServletException;

public class FwdMailRespDelegate
	extends MdnHtmlServletDelegate
{
    /*
     * Resources
	 */
    public static final ResId
		TEXT_TO			= new ResId ("FwdMailRespDelegate.text.To"),
		TEXT_SUBJECT	= new ResId ("FwdMailRespDelegate.text.Subject"),
		TEXT_REPLY		= new ResId ("FwdMailRespDelegate.text.Reply"),
		TEXT_SEND		= new ResId ("FwdMailRespDelegate.text.Send");

    /**
     * Default ctor
     */
	public
	FwdMailRespDelegate ()
	{
	}

	/**
	 * Handle actions for the FwdMailResp
	 * @throws IOException, standard exception thrown by servlet methods.
	 * @throws ServletException, standard exception thrown by servlet methods.
	 */
	public void
	run ()
		throws IOException, ServletException
	{
		/*
		 *	We present a Form with:
		 *		Input cell for Subject
		 *		Input cell for Recipient
		 *		Input cell for user response
		 *		Hidden Inputs detailing Email Reference
		 */
		try
		{
			// cell
			MdnClientCell client = new MdnClientCell ();
			client.setAlign (AlignType.CENTER);

			/*
			 *	Get handed requests parameters
			 */
			String strIndex = _request.getParameter (
								MdnHtmlServlet.PV_RECORD_INDEX);

			/*
			 *	Get the MailMessage in question
			 */
			UserState userState = getUserState ();
			PagedItrMsgDelegate pmd = (PagedItrMsgDelegate)
								userState.getCurrentPagedItDelegate ();
			int index = Integer.parseInt (strIndex);
			MailMessageDobj mm = (MailMessageDobj) pmd.getMessage (index);

			/*
			 *	Form Presentation
			 */
			Form form = new Form();
			form.setMethod (Form.POST);
			form.setAction (makeHref (MdnHtmlServlet.ACT_FWD_MAIL_SEND));
			client.addElement (form);

			/*
			 *	Email reference: Cache+Index
			 */
			form.addElement (
					new Input (
						Input.HIDDEN,
						MdnHtmlServlet.PV_PAGEDITERATORID,
						userState.getCurrentPItId ()));
			form.addElement (
					new Input (
						Input.HIDDEN,
						MdnHtmlServlet.PV_RECORD_INDEX,
						strIndex));

			WslHtmlTable table = new WslHtmlTable ();
			form.addElement (table);

			/*
			 *	Have a editable Subject
			 *	(provide a default)
			 */
			TD cell = new TD (WslHtmlUtil.esc (TEXT_SUBJECT.getText ()));
			TR row = new TR (cell);
			table.addElement (row);
			cell = new TD (
					new Input (Input.TEXT,
							   FwdMailSendDelegate.FWD_SUBJECT,
							   "Fwd: [" + mm.getSubject () + "]"));
			row = new TR (cell);
			table.addElement (row);

			/*
			 *	Present To: Input
			 */
			cell = new TD (WslHtmlUtil.esc (TEXT_TO.getText ()));
			row = new TR (cell);
			table.addElement (row);
			cell = new TD (
					new Input (Input.TEXT,
							   FwdMailSendDelegate.FWD_TO,
							   ""));
			row = new TR (cell);
			table.addElement (row);

			/*
			 *	Present User Body Input
			 */
			cell = new TD (WslHtmlUtil.esc (TEXT_REPLY.getText ()));
			row = new TR (cell);
			table.addElement (row);

			cell = new TD (
					new TextArea (FwdMailSendDelegate.FWD_BODY,4,30));
			row = new TR (cell);
			table.addElement (row);

			/*
			 *	Post button
			 */
			cell = new TD (
					new Input (Input.SUBMIT,
							   "send",
							   WslHtmlUtil.esc (TEXT_SEND.getText ())));
			row = new TR(cell);
			table.addElement (row);

			outputClientCell (client);

		} catch (Exception e)
		{
			onError (e);
		}
    }
}
