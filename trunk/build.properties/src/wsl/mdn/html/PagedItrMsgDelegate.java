/*	$Header: /wapsolutions/cvsroot/mdn/mdn/src/wsl/mdn/html/PagedItrMsgDelegate.java,v 1.2 2003/01/07 02:59:06 tecris Exp $
 *
 *	Paged Messages using Iterator
 *
 */
package wsl.mdn.html;

import java.util.Iterator;
import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.ecs.html.*;
import wsl.fw.util.Util;
import wsl.fw.html.*;
import wsl.fw.msgserver.*;
import wsl.fw.resource.ResId;

public class PagedItrMsgDelegate
	extends PagedIteratorDelegate
{
    private static final ResId LABEL_SUBJECT =
        new ResId ("ImapFolder.label.NoSubject");

    private static final String
        EMPTY_STRING        = "",
        MAIL_MESSAGE_CLASS  = "wsl.fw.msgserver.MailMessageDobj";
    /**
     * Ctor taking a Iterator
     * @param it the Iterator
     */
    public
	PagedItrMsgDelegate (
	 Iterator it)
    {
        super (it);
    }

    /**
     * Ctor taking a Iterator and records/page
     * @param it the Iterator
     * @param recsPerPage
     */
    public
	PagedItrMsgDelegate (
	 Iterator it, int recsPerPage)
    {
        super (it, recsPerPage);
    }

	public MessageDobj
	getMessage (
	 int index)
	{
		return (MessageDobj) getObject (index);
	}

    /**
	 * Class requirement.
	 *
     * Dress the Object with an anchor link..
     */
    public A
	getObjAnchor (
	 int objid)
    {
        /*
		 *	create the Anchor, with the Subject showing.
		 */
        MessageDobj md = (MessageDobj) getMessage (objid);
        /**
         *  if a message has no subject set the subject to
         *  "(no subject)".
         *
         */
        if (md.getClass ().getName ().equals (MAIL_MESSAGE_CLASS)&&
              md.toString ().equals(EMPTY_STRING))
            ((MailMessageDobj) md).setSubject (LABEL_SUBJECT.getText ());
        String href = makeHref (MdnHtmlServlet.ACT_SHOW_MESSAGE);
        href = addParam (href, MdnHtmlServlet.PV_RECORD_INDEX, String.valueOf (objid));
        href = addParam (href, MdnHtmlServlet.PV_PAGEDITERATORID, getId ());
        return new A (href, WslHtmlUtil.esc (md.toString ()));
    }

    /**
     * Get the more action constant
     */
    public String getMoreAction()
    {
        return MdnHtmlServlet.ACT_NEXTPVPAGE;
    }
}