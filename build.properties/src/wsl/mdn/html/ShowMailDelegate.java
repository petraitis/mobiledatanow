/**	$Id: ShowMailDelegate.java,v 1.6 2002/12/23 20:51:16 tecris Exp $
 *
 *	Display contents of eMail
 *
 */
package wsl.mdn.html;

import org.apache.ecs.html.*;
import org.apache.ecs.*;

import wsl.fw.servlet.*;
import wsl.fw.util.Util;
import wsl.fw.resource.ResId;
import wsl.fw.html.*;
import wsl.fw.datasource.*;
import wsl.fw.msgserver.MailMessageDobj;
import wsl.fw.msgserver.SessionMailMsgDobj;
import wsl.fw.msgserver.ItemDobj;
import wsl.fw.msgserver.MessageServer;
import wsl.mdn.guiconfig.MsgServerAction;

public class ShowMailDelegate
	extends ShowGenericMessageDelegate
{
    //--------------------------------------------------------------------------
    // resources
    public static final ResId
		TEXT_REPLY		= new ResId ("ShowMailDelegate.text.Reply"),
    	TEXT_REPLY_YES	= new ResId ("ShowMailDelegate.text.ReplyYes"),
    	TEXT_YES		= new ResId ("ShowMailDelegate.text.Yes"),
    	TEXT_REPLY_NO	= new ResId ("ShowMailDelegate.text.ReplyNo"),
    	TEXT_NO			= new ResId ("ShowMailDelegate.text.No"),
    	TEXT_FORWARD	= new ResId ("ShowMailDelegate.text.Forward"),
    	TEXT_DELETE		= new ResId ("ShowMailDelegate.text.Delete");

    //--------------------------------------------------------------------------
    // end links

    /**
     * Add links to the end of the client cell
     * @param table the outer table
     * @param recIndex the record index
	 * @param showReply
     */
	protected void
	addEndLinks (
	 WslHtmlTable table,
	 String recIndex,
	 boolean showReply)
    {
		String curPItId = getUserState ().getCurrentPItId ();
        recIndex = String.valueOf (recIndex);		// handle nulls
		
		// reply
		String href = makeHref(MdnHtmlServlet.ACT_REPLY_MAIL);
		href = addParam (href, MdnHtmlServlet.PV_PAGEDITERATORID, curPItId);
		href = addParam (href, MdnHtmlServlet.PV_RECORD_INDEX, recIndex);
		A a = new A (href, WslHtmlUtil.esc (TEXT_REPLY.getText ()));
		TD cell = new TD (a);
		TR row = new TR (cell);
		cell.setColSpan (2);
		if (showReply)
		{
			table.addElement (row);
		}

		// Forward
		href = makeHref (MdnHtmlServlet.ACT_FWD_MAIL_RESP);
		href = addParam (href, MdnHtmlServlet.PV_PAGEDITERATORID, curPItId);
		href = addParam (href, MdnHtmlServlet.PV_RECORD_INDEX, recIndex);
		a = new A (href, WslHtmlUtil.esc (TEXT_FORWARD.getText ()));
		cell = new TD (a);
		row = new TR (cell);
		cell.setColSpan (2);
		table.addElement (row);

		// Delete
		href = makeHref (MdnHtmlServlet.ACT_DELETE_MAIL);
		href = addParam (href, MdnHtmlServlet.PV_PAGEDITERATORID, curPItId);
		href = addParam (href, MdnHtmlServlet.PV_RECORD_INDEX, recIndex);
		a = new A (href, WslHtmlUtil.esc (TEXT_DELETE.getText ()));
		cell = new TD (a);
		row = new TR (cell);
		cell.setColSpan (2);
		table.addElement (row);
    }

	protected void
	markMessageRead (
	 ItemDobj itemObj)
		throws DataSourceException
	{
		if (!(itemObj instanceof MailMessageDobj))
			return;

		/*
		 *	Mark the message as read
		 */
		UserState userState = getUserState ();
		MsgServerAction msa = (MsgServerAction) userState.getCurrentMenu ();
		MessageServer ms = userState.
								getCache ().
								getMessageServer (
									new Integer (msa.getMsgServerId ()));

		DataSource ds = ms.createImpl();

		/*
		 *	Create a session-signed copy to send back
		 */
		SessionMailMsgDobj uObj = new SessionMailMsgDobj (
										userState.getSessionId (),
										(MailMessageDobj) itemObj);
		uObj.setUserId (userState.getUser ().getId ());		// old msgserv hack
		ds.update (uObj);
	}
}
