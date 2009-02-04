//==============================================================================
// CustomPresentation.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.presentation;

import java.util.Vector;
import java.util.Map;
import java.util.HashMap;
import java.io.PrintWriter;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.datasource.Query;
import wsl.fw.datasource.QueryCriterium;
import wsl.fw.datasource.RecordSet;
import wsl.fw.datasource.Sort;
import wsl.fw.datasource.DataSource;
import wsl.fw.datasource.DataManager;
import wsl.fw.datasource.DataSourceException;
import wsl.fw.resource.ResId;

//------------------------------------------------------------------------------
/**
 * Base class for custom presentations. Uses PresentationItem. Edited by
 * PresentationEditor. Implementing classes must be added to the Config
 * under the CKfw.CUSTOM_PRESENTATIONS key (semicolon delimited) to make them
 * accessible to the editor.
 *
 * Customisation elements (PresentationItems) are of 2 types.
 * HEADER, an element that will be included at the top of the specified page.
 * FOOTER, an element that will be included at the bottom of the specified page.
 * Either elements may be included multiple times, a sequence number ensures
 * uniqueness and defines order.
 * A header or footer may be static text or a url (beginning with / and relative
 * to the current context) to a page that will be included.
 * Either type of element must be a valid document fragment for the type of
 * custom presentation. I.e. a valid WML paragraph for WMLPresentation.
 *
 * Subclasses should impement a singleton or static accessor (for that specific
 * subclass) so that all users within the VM use the singleton and read from
 * its cache. The subclass also defines the names of the pages that may be
 * customised.
 *
 * The servlets and/or JSPs that generate a given presentation should get the
 * singleton for their concrete presentation subclass and call embed() passing
 * the appropriate servlet context parameters, the name of the page and true
 * for headers, then again with false for the footer.
 */
public abstract class CustomPresentation
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/presentation/CustomPresentation.java $ ";

    // resources
    public static final ResId ERR_SERVLET_WRITER  = new ResId("CustomPresentation.error.ServletWriter");
    public static final ResId ERR_SERVLET_CONTEXT  = new ResId("CustomPresentation.error.ServletContext");
    public static final ResId WARNING_INCLUDE  = new ResId("CustomPresentation.warning.Include");
    public static final ResId WARNING_DISPATCHER  = new ResId("CustomPresentation.warning.Dispatcher");
    public static final ResId ERR_CONTENT_TYPE  = new ResId("CustomPresentation.error.ContentType");
    public static final ResId ERR_QUERY  = new ResId("CustomPresentation.error.Query");
    public static final ResId ERR_DATA_SOURCE  = new ResId("CustomPresentation.error.DataSource");

    // attributes
    // maps to cache the customization data
    Map _pageHeaders = new HashMap();
    Map _pageFooters = new HashMap();

    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public CustomPresentation()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Abstract method, concrete subclasses must implement.
     * Get a description of the custom presentation.
     * @return the description.
     */
    public abstract String getPresentationDescription();

    //--------------------------------------------------------------------------
    /**
     * Abstract method, concrete subclasses must implement.
     * Get the list of pages (page names) that may be customized.
     */
    public abstract Vector getCustomizablePages();

    //--------------------------------------------------------------------------
    /**
     * Get a Vector of Strings enumerating the possible positions.
     * @return the positions.
     */
    public Vector getPositions()
    {
        Vector v = new Vector();
        v.add(PresentationItem.HEADER);
        v.add(PresentationItem.FOOTER);
        return v;
    }

    //--------------------------------------------------------------------------
    /**
     * Get a Vector of Strings enumerating the content types.
     * @return the content types.
     */
    public Vector getContentTypes()
    {
        Vector v = new Vector();
        v.add(PresentationItem.CT_TEXT);
        v.add(PresentationItem.CT_URL);
        return v;
    }

    //--------------------------------------------------------------------------
    /**
     * Flush the custom content cache (this VM only) so the contents will be
     * re-read from the DataSource.
     */
    public void flush()
    {
        synchronized (_pageHeaders)
        {
            _pageHeaders.clear();
        }
        synchronized (_pageFooters)
        {
            _pageFooters.clear();
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Embed the custom content in the output stream of the calling servlet.
     * All headers or footers will be embedded in the order defined by the
     * sequence field of the PresentationItems. This function should be called
     * by each customisable servlet/JSP page to embed the customised content.
     * @param servlet, the calling servlet.
     * @param request, the caller's HttpServletRequest.
     * @param response, the caller's HttpServletResponse.
     * @param page, the name of the page context to get the customization for.
     * @param bHeader, if true gets the headers, if false gets footers.
     */
    public void embed(HttpServlet servlet, HttpServletRequest request,
        HttpServletResponse response, String page, boolean bHeader)
    {
        // get the header or footer elements for the specified page
        Vector elements = getElements(bHeader, page);

        // get context and writer for output
        PrintWriter    out = null;
        try
        {
            out = response.getWriter();
        }
        catch (IOException e)
        {
            Log.error(ERR_SERVLET_WRITER.getText()
                + e.toString());
        }

        ServletContext sc = servlet.getServletContext();
        if (sc == null)
            Log.error(ERR_SERVLET_CONTEXT.getText());

        // iterate adding them
        for (int i = 0; i < elements.size(); i++)
        {
            // get the element, extract content and type
            PresentationItem pi = (PresentationItem) elements.get(i);
            String contentType = pi.getContentType();
            String content = pi.getContent();

            // embed the content
            if (contentType.equals(PresentationItem.CT_TEXT))
            {
                // text content, write it to the output
                if (out != null)
                    out.print(content);
            }
            else if (contentType.equals(PresentationItem.CT_URL))
            {
                // url content, include the url document
                if (sc != null)
                {
                    //get the request dispatcher and forward to the url
                    RequestDispatcher rd = sc.getRequestDispatcher(content);
                    if(rd != null)
                    try
                    {
                        rd.include(request, response);
                    }
                    catch (Exception e)
                    {
                        Log.warning(WARNING_INCLUDE.getText() + " "
                            + elements.toString());
                    }
                    else
                        Log.warning(WARNING_DISPATCHER.getText()
                            + " [" + content +"]");
                }
            }
            else
                Log.error(ERR_CONTENT_TYPE.getText()
                    + " [" + contentType + "]");
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Get the cached elements (PresentationItems) for the specified page.
     * Load from DB if required.
     * @param cache, the top level page cache for this type of element.
     * @param page, the name of the desired page.
     */
    private Vector getElements(boolean bHeader, String page)
    {
        Map cache = ((bHeader) ? _pageHeaders : _pageFooters);

        synchronized (cache)
        {
            // get from cache
            Vector v = (Vector) cache.get(page);

            // if not in cache then load from DataSource and add to cache
            if (v == null)
            {
                v = loadElements(bHeader, page);
                cache.put(page, v);
            }

            return v;
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Load a set of elements from the DataSource.
     * @param bHeader, true ti load header items, false to load footers.
     * @param page, the name of the page to load elements for.
     * @return a Vector of header or footer PresentationItems for this page.
     */
    private Vector loadElements(boolean bHeader, String page)
    {
        Vector v = new Vector();

        // selection criteria for load, the class of the CustomPresentation
        // subclass and the position
        String presentationClass  = getClass().getName();
        String position = (bHeader) ? PresentationItem.HEADER : PresentationItem.FOOTER;

        // get the data source for querying
        DataSource ds = DataManager.getDataSource(PresentationItem.ENT_PRESENTATION_ITEM);

        if(ds != null)
        {
            try
            {
                // add query criteria and sort by sequence
                Query query = new Query();
                query.addQueryCriterium(new QueryCriterium(PresentationItem.ENT_PRESENTATION_ITEM,
                    PresentationItem.FLD_PRESENTATION_CLASS, QueryCriterium.OP_EQUALS,
                    presentationClass));
                query.addQueryCriterium(new QueryCriterium(PresentationItem.ENT_PRESENTATION_ITEM,
                    PresentationItem.FLD_POSITION, QueryCriterium.OP_EQUALS, position));
                query.addQueryCriterium(new QueryCriterium(PresentationItem.ENT_PRESENTATION_ITEM,
                    PresentationItem.FLD_PAGE, QueryCriterium.OP_EQUALS, page));
                query.addSort(new Sort(PresentationItem.ENT_PRESENTATION_ITEM,
                    PresentationItem.FLD_SEQUENCE));

                // perform the query
                RecordSet rs = ds.select(query);

                // iterate adding the PresentationItems to the vector
                while (rs.next())
                    v.add(rs.getCurrentObject());
            }
            catch (DataSourceException e)
            {
                Log.error(ERR_QUERY.getText(), e);
            }
        }
        else
            Log.error(ERR_DATA_SOURCE.getText());

        return v;
    }
}

//==============================================================================
// end of file CustomPresentation.java
//==============================================================================
