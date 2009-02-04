/*	$Header: /wapsolutions/cvsroot/mdn/mdn/src/wsl/mdn/wap/PagedItrMsgDelegate.java,v 1.2 2003/01/07 21:33:02 tecris Exp $
 *
 *	Paged Messages using Iterator
 *
 */
package wsl.mdn.wap;

import java.util.Iterator;
import org.apache.ecs.wml.*;
import wsl.fw.util.Util;
import wsl.fw.msgserver.*;
import wsl.fw.wml.WEUtil;
import wsl.fw.resource.ResId;

public class PagedItrMsgDelegate
	extends PagedIteratorDelegate
{

    private static final ResId LABEL_SUBJECT =
        new ResId ("ImapFolder.label.NoSubject");

    private static final String
        EMPTY_STRING        = "",
        MAIL_MESSAGE_CLASS  = "wsl.fw.msgserver.MailMessageDobj";
	/*
	 *	Magic numbers
	 */
	static private final int MAXSUBJLEN = 60;

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
     * Get the card element dressed up
     */
    public P
	getListElement (
	 int index)
    {
        /*
		 *	Create a P, with the Subject showing
		 */
        MessageDobj md = (MessageDobj) getMessage (index);
        /**
         *  if a message has no subject set the subject to
         *  "(no subject)".
         *
         */
        if (md.getClass ().getName ().equals (MAIL_MESSAGE_CLASS)&&
              md.toString ().equals(EMPTY_STRING))
            ((MailMessageDobj) md).setSubject (LABEL_SUBJECT.getText ());

        // makehref
        String href = makeHref (MdnWmlServlet.ACT_SHOW_MESSAGE);
        href = addParam (href, MdnWmlServlet.PV_RECORD_INDEX, String.valueOf (index));
        href = addParam (href, MdnWmlServlet.PV_PAGEDITERATORID, getId ());

		/*
		 *	Let's trim the display down if required so we
		 *	don't overflow the WML size restriction.
		 */
		String subj = md.toString ();
		if (subj.length () > MAXSUBJLEN)
			subj = subj.substring (0, MAXSUBJLEN - 3) + "...";

        return WEUtil.makeHrefP (href, WEUtil.esc (subj));
    }

    /**
     * Get the more action constant
     */
    public String
	getMoreAction ()
    {
        return MdnWmlServlet.ACT_NEXTPVPAGE;
    }
}