/**	$Id: FwdMailRespDelegate.java,v 1.3 2002/10/01 21:39:52 tecris Exp $
 *
 *	Obtain responses for Forwarding email
 *
 */
package wsl.mdn.wap;

import org.apache.ecs.wml.*;

import wsl.fw.datasource.DataSource;
import wsl.fw.msgserver.MailMessageDobj;
import wsl.fw.msgserver.MessageServer;
import wsl.fw.resource.ResId;
import wsl.fw.servlet.ServletBase;
import wsl.fw.util.Util;
import wsl.fw.wml.WEUtil;
import wsl.fw.wml.WslInput;
import wsl.mdn.guiconfig.MsgServerAction;

import java.io.IOException;
import javax.servlet.ServletException;

public class FwdMailRespDelegate
	extends MdnWmlServletDelegate
{
	/*
	 * Resources
	 */
    public static final ResId
		TEXT_TITLE		= new ResId ("FwdMailRespDelegate.text.Title"),
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
		try
		{
			/*
			 *	Get the request parameters
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
			MailMessageDobj mm = (MailMessageDobj) pmd.getMessage (index);

			/*
			 *	Presentation
			 */
			Card card = new Card ();
			card.setTitle (WEUtil.esc (TEXT_TITLE.getText ()));

			/*
			 *	Accept element
			 */
			// build the save go element
			Go goSave = new Go (makeHref (), Method.GET);

			// action
			goSave.addElement (
				new Postfield (
					ServletBase.RP_ACTION,
					MdnWmlServlet.ACT_FWD_MAIL_SEND));

			// email ref
			goSave.addElement (
				new Postfield (
					MdnWmlServlet.PV_PAGEDITERATORID,
					userState.getCurrentPItId ()));
			goSave.addElement (
				new Postfield (
					MdnWmlServlet.PV_RECORD_INDEX,
					strIndex));

			/*
			 *	Editable subject (with default)
			 */
			P p = new P ();
			p.addElement (WEUtil.esc (TEXT_SUBJECT.getText ()));
			p.addElement (MdnWmlServlet.TEXT_PROMPTCOLON.getText ());
			p.addElement (
                new WslInput (
					FwdMailSendDelegate.FWD_SUBJECT,
					null,
					"Fwd: [" + mm.getSubject () + "]",
					null,
					true));
			card.addElement (p);
			goSave.addElement (
				new Postfield (
					FwdMailSendDelegate.FWD_SUBJECT,
					WEUtil.makeVar (FwdMailSendDelegate.FWD_SUBJECT)));

			/*
			 *	Who to send to
			 */
			p = new P ();
			p.addElement (WEUtil.esc (TEXT_TO.getText ()));
			p.addElement (MdnWmlServlet.TEXT_PROMPTCOLON.getText ());
			p.addElement (
                new WslInput (
					FwdMailSendDelegate.FWD_TO,
					null,
					"",
					null,
					false));			// required field
			card.addElement (p);
			goSave.addElement (
				new Postfield (
					FwdMailSendDelegate.FWD_TO,
					WEUtil.makeVar (FwdMailSendDelegate.FWD_TO)));

			/*
			 *	Reply body
			 */
			p = new P ();
			p.addElement (WEUtil.esc (TEXT_REPLY.getText ()));
			p.addElement (MdnWmlServlet.TEXT_PROMPTCOLON.getText ());
			p.addElement (
                new WslInput (
					FwdMailSendDelegate.FWD_BODY,
					null,
					"",
					null,
					true));
			card.addElement (p);
			goSave.addElement (
				new Postfield (
					FwdMailSendDelegate.FWD_BODY,
					WEUtil.makeVar (FwdMailSendDelegate.FWD_BODY)));

			// send anchor
            Anchor a = new Anchor(WEUtil.esc(TEXT_SEND.getText()), goSave);
            a.addElement(WEUtil.esc(TEXT_SEND.getText()));
            p = new P();
            p.addElement(a);
            card.addElement(p);


                        // Save
			Do doSave = new Do (DoType.ACCEPT, TEXT_SEND.getText ());
			doSave.addElement (goSave);
			card.addElement (doSave);

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

		} catch (Exception e)
		{
			onError(e);
		}
	}
}
