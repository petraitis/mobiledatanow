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

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:
 * @author
 * @version 1.0
 */

public abstract class PagedVectorDelegate extends MdnWmlServletDelegate
{
    //--------------------------------------------------------------------------
    // resources

    public static final ResId TEXT_NO_RESULTS =
        new ResId("PagedSelectDelegate.text.NoResults");
    public static final ResId TEXT_TITLE =
        new ResId("PagedSelectDelegate.text.title");


    //--------------------------------------------------------------------------
    // constants

    public static final int DEFAULT_RECS_PER_PAGE = 5;
    public static final String PV_NEXTINDEX = "nextindex";


    //--------------------------------------------------------------------------
    // attributes

    protected Vector _v = null;
    protected int _queryId;
    protected int _recsPerPage = DEFAULT_RECS_PER_PAGE;
    protected String _title = TEXT_TITLE.getText();


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

    /**
     * @return the record vector
     */
    public Vector getRecords()
    {
        return _v;
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

            // make the card
            Card card = new Card();
            card.setTitle(WEUtil.esc(_title));

            // add the start links
            addStartLinks(card);

            // get the next index
            int nextIndex = 0;
            String strNextIndex = _request.getParameter(PV_NEXTINDEX);
            if(strNextIndex != null)
                nextIndex = Integer.parseInt(strNextIndex);

            // if no results say so
            Vector recs = _v;
            if(recs == null || recs.size() == 0)
            {
                P p = new P(Alignment.LEFT, Mode.WRAP);
                p.addElement(WEUtil.esc(PagedSelectDelegate.TEXT_NO_RESULTS.getText()));
                card.addElement(p);
            }

            // else iterate and add results
            else
            {
                // add elements
                P p;
                int maxIndex = recs.size();
                for(int count = 0; count < _recsPerPage && nextIndex < maxIndex; nextIndex++)
                {
                    p = getListElement(nextIndex);
                    if(p != null)
                    {
                        count++;
                        card.addElement(p);
                    }
                }
            }

            // more
            if(nextIndex < recs.size())
            {
                String href = makeHref(getMoreAction());
                href = addParam(href, PV_NEXTINDEX, String.valueOf(nextIndex));
                href = addMoreParams(href);
                card.addElement(WEUtil.makeHrefP(href,
                    WEUtil.esc(MdnWmlServlet.TEXT_MORE.getText())));
            }

            // add links to the end of the card
            addEndLinks(card);

            // More do action
            String label = (nextIndex < _v.size())?
                MdnWmlServlet.TEXT_MORE.getText(): MdnWmlServlet.TEXT_END.getText();
            Do doOp = new Do(DoType.ACCEPT, WEUtil.esc(label));
            String href = makeHref(getMoreAction());
            href = addParam(href, PV_NEXTINDEX, String.valueOf(nextIndex));
            href = addParam(href, MdnWmlServlet.PV_MENUACTIONID,
                getUserState().getCurrentMenu().getId().toString());
            href = addMoreParams(href);
            Go go = new Go(href);
            doOp.addElement(go);
            card.addElement(doOp);

            // Main
            Do doMain = new Do(DoType.OPTIONS, MdnWmlServlet.TEXT_MAIN.getText());
            Go goMain = new Go(makeHref(MdnWmlServlet.ACT_MAINMENU), Method.GET);
            doMain.addElement(goMain);
            card.addElement(doMain);

            // Back
            doOp = new Do(DoType.PREV, MdnWmlServlet.TEXT_BACK.getText());
            doOp.addElement(new Prev());
            card.addElement(doOp);

            // send output
            wmlOutput(card, true);
        }
        catch(Exception e)
        {
            onError(e);
        }
    }

    /**
     * Get the card element for an entry in the Vector
     */
    public abstract P getListElement(int index);

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

    /**
     * Add links to the start of the card
     */
    public void addStartLinks(Card card)
    {
    }

    /**
     * Add links to the end of the card
     */
    public void addEndLinks(Card card)
    {
    }


    //--------------------------------------------------------------------------
    // title

    /**
     * Set the title
     * @param title the new title
     */
    public void setTitle(String title)
    {
        _title = title;
    }
}