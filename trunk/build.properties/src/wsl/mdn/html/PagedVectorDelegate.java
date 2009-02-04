package wsl.mdn.html;

// imports
import java.util.Vector;
import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.ecs.html.*;
import org.apache.ecs.*;
import wsl.fw.servlet.*;
import wsl.fw.util.Util;
import wsl.fw.resource.ResId;
import wsl.fw.html.*;
import wsl.fw.datasource.RecordSet;
import wsl.mdn.guiconfig.MenuAction;
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

public abstract class PagedVectorDelegate extends MdnHtmlServletDelegate
{
    //--------------------------------------------------------------------------
    // resources

    public static final ResId TEXT_NO_RESULTS =
        new ResId("HtmlPagedSelectDelegate.text.NoResults");
    public static final ResId TEXT_TITLE =
        new ResId("HtmlPagedSelectDelegate.text.Title");


    //--------------------------------------------------------------------------
    // constants

    public static final int DEFAULT_RECS_PER_PAGE = 20;
    public static final String PV_NEXTINDEX = "nextindex";


    //--------------------------------------------------------------------------
    // attributes

    protected Vector _v = null;
    protected int _queryId;
    protected int _recsPerPage = DEFAULT_RECS_PER_PAGE;
    //protected int _nextRecIndex = 0;


    //--------------------------------------------------------------------------
    // construction

    /**
     * Ctor taking a Vector
     * @param v the Vector
     */
    public PagedVectorDelegate(Vector v, int queryId)
    {
        this(v, queryId, DEFAULT_RECS_PER_PAGE);
    }

    /**
     * Ctor taking a Vector
     * @param v the Vector
     */
    public PagedVectorDelegate(Vector v, int queryId, int recsPerPage)
    {
        _v = v;
        _queryId = queryId;
        _recsPerPage = recsPerPage;
    }


    //--------------------------------------------------------------------------
    // records

    /**
     * Get an object by index
     * @param index the object index
     * @return Object the Object at the param index
     */
    public Object getObject(int index)
    {
        Util.argCheckNull(_v);
        return (_v != null && index < _v.size())? _v.elementAt(index): null;
    }

    /**
     * Reset the record index to 0
     */
    private void resetSDIndex()
    {
        //_nextRecIndex = 0;
    }

    /**
     * @return int the query id
     */
    public int getQueryId()
    {
        return _queryId;
    }

    //--------------------------------------------------------------------------
    // wml

    /**
     * build and send wml
     */
    public void run() throws ServletException, IOException
    {
        try
        {
            // validate
            Util.argCheckNull(_request);
            Util.argCheckNull(_response);

            // cell
            MdnClientCell client = new MdnClientCell();
            client.setAlign(AlignType.CENTER);
            client.setHelpUrl("/help/mdnhelp.html#bQueryResults");

            // title
            String title = TEXT_TITLE.getText();
            MenuAction ma = getUserState().getCurrentMenu();
            if(ma != null)
                title = ma.getName();
            client.setClientTitle(title);

            // table
            WslHtmlTable table = new WslHtmlTable();
            client.addElement(table);
            TD cell = new TD(MdnHtmlServlet.getTitleElement(title));
            TR row = new TR(cell);
            table.addElement(row);

            // get the next index
            int nextIndex = 0;
            String strNextIndex = _request.getParameter(PV_NEXTINDEX);
            if(strNextIndex != null)
                nextIndex = Integer.parseInt(strNextIndex);

            // if no results say so
            Vector recs = _v;
            if(recs == null || recs.size() == 0)
            {
                cell = new TD(PagedSelectDelegate.TEXT_NO_RESULTS.getText());
                row = new TR(cell);
                table.addElement(row);
            }

            // else iterate and add results
            else
            {
                // calc the maxIndex
                int maxIndex = nextIndex + _recsPerPage;
                if(maxIndex > recs.size())
                    maxIndex = recs.size();

                // add elements
                for(; nextIndex < maxIndex; nextIndex++)
                {
                    cell = new TD(getListElement(nextIndex));
                    row = new TR(cell);
                    table.addElement(row);
                }
            }

            // more
            if(nextIndex < recs.size())
            {
                String href = makeHref(getMoreAction());
                href = addParam(href, PV_NEXTINDEX, String.valueOf(nextIndex));
                href = addMoreParams(href);
                cell = new TD(new A(href, WslHtmlUtil.esc(MdnHtmlServlet.TEXT_MORE.getText())));
                row = new TR(cell);
                table.addElement(row);
            }

            // send output
            outputClientCell(client);
        }
        catch(Exception e)
        {
            onError(e);
        }
    }

    /**
     * Get the card element for an entry in the Vector
     */
    public abstract A getListElement(int index);

    /**
     * Get the more action constant
     */
    public abstract String getMoreAction();

    /**
     * Add params to the more href
     * @param the existing href
     * @return the new href
     */
    public String addMoreParams(String href)
    {
        return href;
    }
}