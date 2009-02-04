package wsl.mdn.html;

// imports
import java.util.Vector;
import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.ecs.html.*;
import wsl.fw.servlet.*;
import wsl.fw.util.Util;
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

public class PagedSelectDelegate extends PagedVectorDelegate
{
    //--------------------------------------------------------------------------
    // construction

    /**
     * Ctor taking a RecordSet
     * @param rs the RecordSet
     */
    public PagedSelectDelegate(RecordSet rs, int queryId)
    {
        this(rs, queryId, DEFAULT_RECS_PER_PAGE);
    }

    /**
     * Ctor taking a RecordSet
     * @param rs the RecordSet
     */
    public PagedSelectDelegate(RecordSet rs, int queryId, int recsPerPage)
    {
        super(rs.getRows(), queryId, recsPerPage);
    }


    //--------------------------------------------------------------------------
    // records

    /**
     * Get a record by index
     * @param index the record index
     * @return Record the Record at the param index
     */
    public Record getRecord(int index)
    {
        return (Record)this.getObject(index);
    }


    //--------------------------------------------------------------------------
    // wml

    /**
     * Get the card element for an entry in the Vector
     */
    public A getListElement(int index)
    {
        // get the record
        Record r = (Record)_v.elementAt(index);

        // create the P
        String href = makeHref(MdnHtmlServlet.ACT_QUERYRESULT);
        href = addParam(href, MdnHtmlServlet.PV_RECORD_INDEX, String.valueOf(index));
        href = addParam(href, MdnHtmlServlet.PV_MENUACTIONID,
            getUserState().getCurrentMenu().getId().toString());
        A a = new A(href, WslHtmlUtil.esc(r.toString()));

        // return
        return a;
    }

    /**
     * Get the more action constant
     */
    public String getMoreAction()
    {
        return MdnHtmlServlet.ACT_NEXTQUERYPAGE;
    }
}