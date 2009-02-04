/*	$Id: PagedGroupSelectDelegate.java,v 1.1.1.1 2002/06/11 23:35:35 jonc Exp $
 *
 *
 */
package wsl.mdn.wap;

// imports
import java.util.Vector;
import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.ecs.wml.*;
import wsl.fw.servlet.*;
import wsl.fw.util.Util;
import wsl.fw.resource.ResId;
import wsl.fw.wml.WEUtil;
import wsl.fw.datasource.RecordSet;
import wsl.mdn.dataview.Record;
import wsl.mdn.guiconfig.LoginSettings;

public class PagedGroupSelectDelegate
	extends PagedVectorDelegate
{
	static private final int MAXVALLEN = 60;

    //--------------------------------------------------------------------------
    // construction

    /**
     * Ctor taking a Vector
     * @param v the Vector
     */
    public
	PagedGroupSelectDelegate (
	 Vector v,
	 int queryId)
    {
        super(v, queryId);
    }

    /**
     * Ctor taking a Vector
     * @param v the Vector
     */
    public
	PagedGroupSelectDelegate (
	 Vector v,
	 int queryId,
	 int recsPerPage)
    {
        super(v, queryId, recsPerPage);
    }


    //--------------------------------------------------------------------------
    // WML Output

    /**
     * Get the card element for an entry in the Vector
     */
    public P
	getListElement (
	 int index)
    {
        // get the records
        Object val = _v.elementAt(index);

        // add to the card
        String href = makeHref(MdnWmlServlet.ACT_GROUPSUBQUERY);
        href = addParam(href, MdnWmlServlet.PV_RECORD_INDEX, String.valueOf(index));
        href = addParam(href, MdnWmlServlet.PV_MENUACTIONID,
            getUserState().getCurrentMenu().getId().toString());

		/*
		 *	Let's trim the display down if required so we
		 *	don't overflow the WML size restriction.
		 */
		String valStr = val.toString ();
		if (valStr.length () > MAXVALLEN)
			valStr = valStr.substring (0, MAXVALLEN - 3) + "...";

        return WEUtil.makeHrefP (href, WEUtil.esc (valStr));
    }

    /**
     * Get the more action constant
     */
    public String
	getMoreAction()
    {
        return MdnWmlServlet.ACT_NEXTGROUPPAGE;
    }
}