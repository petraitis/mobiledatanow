/*	$Id: PagedSelectDelegate.java,v 1.1.1.1 2002/06/11 23:35:35 jonc Exp $
 *
 *	Shows Select results in WML
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

public class PagedSelectDelegate
	extends PagedVectorDelegate
{
	static private final int MAXRESULTLEN = 60;

    //--------------------------------------------------------------------------
    // construction

    /**
     * Ctor taking a RecordSet
     * @param rs the RecordSet
     */
    public
	PagedSelectDelegate (
	 RecordSet rs,
	 int queryId)
    {
        this (rs, queryId, DEFAULT_RECS_PER_PAGE);
    }

    /**
     * Ctor taking a RecordSet
     * @param rs the RecordSet
     */
    public
	PagedSelectDelegate (
	 RecordSet rs,
	 int queryId,
	 int recsPerPage)
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
    public Record
	getRecord (
	 int index)
    {
        return (Record)this.getObject(index);
    }


    //--------------------------------------------------------------------------
    // wml

    /**
     * Get the card element for an entry in the Vector
     */
    public P
	getListElement (
	 int index)
    {
        // get the record
        Record r = (Record)_v.elementAt(index);

        // create the P
        String href = makeHref(MdnWmlServlet.ACT_QUERYRESULT);
        href = addParam(href, MdnWmlServlet.PV_RECORD_INDEX, String.valueOf(index));
        href = addParam(href, MdnWmlServlet.PV_MENUACTIONID,
            getUserState().getCurrentMenu().getId().toString());

		/*
		 *	Let's trim the display down if required so we
		 *	don't overflow the WML size restriction.
		 */
		String result = r.toString ();
		if (result.length () > MAXRESULTLEN)
			result = result.substring (0, MAXRESULTLEN - 3) + "...";

        return WEUtil.makeHrefP (href, WEUtil.esc (result));
    }

    /**
     * Get the more action constant
     */
    public String getMoreAction()
    {
        return MdnWmlServlet.ACT_NEXTQUERYPAGE;
    }
}