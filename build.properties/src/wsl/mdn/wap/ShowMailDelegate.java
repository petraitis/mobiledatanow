/**	$Id: ShowMailDelegate.java,v 1.12 2003/01/21 00:55:03 tecris Exp $
 *
 * 	Respond to a Show Email request
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

public class ShowMailDelegate
	extends MdnWmlServletDelegate
{
	//--------------------------------------------------------------------------
	// constants

	private final int
		MAX_TITLE_LEN			= 20,
		MAX_PAYLOAD_LEN			= 650;

	private final static String 
	    EMPTY_STRING	= "",	
		PV_MAILINDEX 	= "id.mail";

	//--------------------------------------------------------------------------
	// resources
	public static final ResId
		TEXT_TITLE		= new ResId ("ShowMailDelegate.text.title"),
		TEXT_REPLY		= new ResId ("ShowMailDelegate.text.Reply"),
		TEXT_REPLY_YES	= new ResId ("ShowMailDelegate.text.ReplyYes"),
		TEXT_YES		= new ResId ("ShowMailDelegate.text.Yes"),
		TEXT_REPLY_NO	= new ResId ("ShowMailDelegate.text.ReplyNo"),
		TEXT_NO			= new ResId ("ShowMailDelegate.text.No"),
		TEXT_FORWARD	= new ResId ("ShowMailDelegate.text.Forward"),
		TEXT_DELETE		= new ResId ("ShowMailDelegate.text.Delete"),
		TEXT_MORE_MAIL	= new ResId ("ShowMailDelegate.text.MoreMail"),
		TEXT_MSG_LIST	= new ResId ("ShowMailDelegate.text.MsgList"),
		ERR_BAD_INDEX	= new ResId ("ShowMailDelegate.error.BadIndex"),
		LABEL_SUBJECT	= new ResId ("ShowMailDelegate.label.Subject"),
		LABEL_SENDER	= new ResId ("ShowMailDelegate.label.Sender"),
		LABEL_RECEIVED	= new ResId ("ShowMailDelegate.label.Received");

	//--------------------------------------------------------------------------
	// construction

	/**
	 * Default ctor
	 */
	public
	ShowMailDelegate ()
	{
	}

	/**
	 * Called by servlet
	 */
	public void
	run ()
		throws ServletException, IOException
	{
		try
		{
			/*
			 *	Get the request parameters
			 */
			int mailIndex = 0;
			String strIndex = _request.getParameter (
									MdnWmlServlet.PV_RECORD_INDEX);
			String strMailIndex = _request.getParameter (PV_MAILINDEX);
			if (strMailIndex != null)
				mailIndex = Integer.parseInt (strMailIndex);

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
			 *	Presentation
			 */
			Card card = new Card ();
			card.setTitle (WEUtil.esc (TEXT_TITLE.getText ()));
			P p = new P ();
			card.addElement (p);

			// subject as title
			String strSubject = mObj.getSubject ();
			if (strSubject.length () > MAX_TITLE_LEN)
				strSubject = strSubject.substring (0, MAX_TITLE_LEN);
			card.setTitle (WEUtil.esc (strSubject));
			int displayed = strSubject.length ();
			boolean showReplyFrom = true;

			if (mailIndex == 0)
			{
				/*
				 *	On the first page, we display the
				 *		- Subject
				 *		- Sender
				 *		- Received date
				 */
				String subj = Util.noNullStr (mObj.getSubject ());
				p.addElement (
					WEUtil.esc (LABEL_SUBJECT.getText () + ": " + subj));
				p.addElement (new BR ());

				// sender
				String sender = Util.noNullStr (mObj.getSender ());
				// do not add the "FROM" field if the sender is an empty string
				if (!sender.equals (EMPTY_STRING))
				{
					p.addElement (
						WEUtil.esc (LABEL_SENDER.getText () + ": " + sender));
					p.addElement (new BR ());
				} else
				{
					showReplyFrom  = false;
				}

				// received
				String recv = Util.noNullStr (mObj.getTimeReceived ());
				p.addElement (
					WEUtil.esc (LABEL_RECEIVED.getText () + ": " + recv));
				p.addElement (new BR ());

				displayed += subj.length () + sender.length () + recv.length ();

				/*
				 *	Mark the message as read
				 */
				MsgServerAction msa = (MsgServerAction)
									  userState.getCurrentMenu ();

				MessageServer ms = userState.
					getCache ().
					getMessageServer (new Integer (msa.getMsgServerId ()));

				DataSource ds = ms.createImpl ();

				/*
				 *	Create a session-signed copy to send back
				 */
				SessionMailMsgDobj dObj = new SessionMailMsgDobj (
												userState.getSessionId (),
												mObj);
				dObj.setUserId (userState.getUser ().getId ());	// msgserv hack
				ds.update (dObj);
			}

			/*
			 *	Display as much of the body that we can.
			 */
			String body = Util.noNullStr (mObj.getText ());
			int max = body.length ();
			int start = mailIndex;
			int end = start + MAX_PAYLOAD_LEN - displayed;
			if (end < start)
				end = start + 10;			// subject possibly too long!

			if (end < max)
			{
				body = body.substring (start, end);
				mailIndex = end;

			} else
			{
				body = body.substring (start);
				mailIndex = -1;
			}
			p.addElement (WEUtil.esc (body));

			/*
			 *	Add trailing links
			 */
			String curPItId = userState.getCurrentPItId ();
			String href;

			// more mail
			if (mailIndex >= 0)
			{

				href = makeHref (MdnWmlServlet.ACT_INBOX_MSG);
				href = addParam (href,
								 MdnWmlServlet.PV_PAGEDITERATORID,
								 curPItId);
				href = addParam (href,
								 MdnWmlServlet.PV_RECORD_INDEX,
								 strIndex);
				href = addParam (href,
								 PV_MAILINDEX,
								 String.valueOf (mailIndex));
				card.addElement (WEUtil.makeHrefP (
									href,
									WEUtil.esc (TEXT_MORE_MAIL.getText ())));
			}

			/*
			 *	Add link to return to the message list
			 *	The link simulates a request for a message with index -1
			 *	and this will trigger an array out of bounds exception 
			 *	Catching the exception(in ShowMessageDelegate)
			 *	will redirect to the message list
			 */
			href = makeHref (MdnWmlServlet.ACT_SHOW_MESSAGE);
			href = addParam (href,
							 MdnWmlServlet.PV_PAGEDITERATORID,
							 curPItId);
			href = addParam (href, MdnWmlServlet.PV_RECORD_INDEX, "-1");
			card.addElement (WEUtil.makeHrefP (
								href,
								WEUtil.esc (TEXT_MSG_LIST.getText ())));

			// reply
			href = makeHref (MdnWmlServlet.ACT_REPLY_MAIL);
			href = addParam (href,
							 MdnWmlServlet.PV_PAGEDITERATORID,
							 curPItId);
			href = addParam (href, MdnWmlServlet.PV_RECORD_INDEX, strIndex);
			// do not add the "REPLY" link if the sender is an empty string
			if (showReplyFrom)
				card.addElement (WEUtil.makeHrefP (
									href,
									WEUtil.esc (TEXT_REPLY.getText ())));

			// forward
			href = makeHref (MdnWmlServlet.ACT_FWD_MAIL_RESP);
			href = addParam (href,
							 MdnWmlServlet.PV_PAGEDITERATORID,
							 curPItId);
			href = addParam (href, MdnWmlServlet.PV_RECORD_INDEX, strIndex);
			card.addElement (WEUtil.makeHrefP (
								href,
								WEUtil.esc (TEXT_FORWARD.getText ())));

			// Delete
			href = makeHref (MdnWmlServlet.ACT_DELETE_MAIL);
			href = addParam (href,
							 MdnWmlServlet.PV_PAGEDITERATORID,
							 curPItId);
			href = addParam (href, MdnWmlServlet.PV_RECORD_INDEX, strIndex);
			card.addElement (WEUtil.makeHrefP (
								href,
								WEUtil.esc (TEXT_DELETE.getText ())));


			/*
			 *	Add Options links
			 */
			// Main
			Do doMain = new Do (DoType.OPTIONS,
								MdnWmlServlet.TEXT_MAIN.getText ());
			Go goMain = new Go (makeHref (MdnWmlServlet.ACT_MAINMENU),
								Method.GET);
			doMain.addElement (goMain);
			card.addElement (doMain);

			// Back
			Do doOp = new Do (DoType.PREV, MdnWmlServlet.TEXT_BACK.getText ());
			doOp.addElement (new Prev ());
			card.addElement (doOp);

			// send output
			wmlOutput (card, true);

		} catch (Exception e)
		{
			onError (e);
		}
	}
}
