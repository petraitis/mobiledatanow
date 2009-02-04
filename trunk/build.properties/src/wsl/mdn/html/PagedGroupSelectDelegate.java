package wsl.mdn.html;

// imports
import java.util.Vector;
import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.ecs.html.*;
import wsl.fw.servlet.*;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.resource.ResId;
import wsl.fw.html.*;
import wsl.fw.datasource.RecordSet;
import wsl.mdn.dataview.Record;
import wsl.mdn.guiconfig.LoginSettings;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:
 * @author
 * @version 1.0
 */

public class PagedGroupSelectDelegate extends PagedVectorDelegate
{
    //--------------------------------------------------------------------------
    // construction

    /**
     * Ctor taking a Vector
     * @param v the Vector
     */
    public PagedGroupSelectDelegate(Vector v, int queryId)
    {
        super(v, queryId);
    }

    /**
     * Ctor taking a Vector
     * @param v the Vector
     */
    public PagedGroupSelectDelegate(Vector v, int queryId, int recsPerPage)
    {
        super(v, queryId, recsPerPage);
    }


    //--------------------------------------------------------------------------
    // WML Output

    /**
     * Get the card element for an entry in the Vector
     */
    public A getListElement(int index)
    {
        // get the records
        Object val = _v.elementAt(index);

        // add to the card
        String href = makeHref(MdnHtmlServlet.ACT_GROUPSUBQUERY);
        href = addParam(href, MdnHtmlServlet.PV_RECORD_INDEX, String.valueOf(index));
        href = addParam(href, MdnHtmlServlet.PV_MENUACTIONID,
            getUserState().getCurrentMenu().getId().toString());

        // create p
        String str = (val == null)? "": val.toString();
        A a = new A(href, WslHtmlUtil.esc(str));

        // return
        return a;
    }

    /**
     * Get the more action constant
     */
    public String getMoreAction()
    {
        return MdnHtmlServlet.ACT_NEXTGROUPPAGE;
    }
}