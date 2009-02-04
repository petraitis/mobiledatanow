package wsl.mdn.html;

// imports
import java.util.Vector;
import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.ecs.wml.*;
import wsl.fw.servlet.*;
import wsl.fw.util.Util;
import wsl.fw.resource.ResId;
import wsl.fw.wml.WEUtil;
import wsl.fw.datasource.*;
import wsl.mdn.dataview.*;
import wsl.mdn.common.MdnDataManager;
import wsl.mdn.guiconfig.LoginSettings;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:
 * @author
 * @version 1.0
 */

public class GroupSubQueryDelegate extends MdnHtmlServletDelegate
{
    //--------------------------------------------------------------------------
    // construction

    /**
     * Default ctor
     */
    public GroupSubQueryDelegate()
    {
    }

    /**
     * Output wml
     */
    public void run() throws ServletException, IOException
    {
        try
        {
            // validate
            Util.argCheckNull(_request);
            Util.argCheckNull(_response);

            String strIndex = _request.getParameter(MdnHtmlServlet.PV_RECORD_INDEX);
            if(strIndex != null && strIndex.length() > 0)
            {
                int index = Integer.parseInt(strIndex);

                // get the record
                PagedGroupSelectDelegate psd = (PagedGroupSelectDelegate)
                    getUserState().getCurrentPagedGroupQuery();
                Object val = psd.getObject(index);
                if(val != null)
                {
                    // parse the id
                    Integer queryId = new Integer(psd.getQueryId());

                    // get the query and data view
                    QueryDobj qd = getUserState().getCache().getQueryDobj(queryId);
                    DataView  dv  = getUserState().getCache().getQueryDataView(queryId);

                    // create Query impl from QueryDobj, this is a new instance that is
                    // not shared even if the QueryDobj itself is cached and shared
                    Query q = qd.createNewImpl();

                    // add the grouping criteria
                    DataViewField dvf = (DataViewField)dv.getField(qd.getGroupFieldId());
                    if(dvf == null)
                        throw new ServletException(MdnHtmlServlet.ERR_REC_NOT_FOUND.getText());
                    q.addQueryCriterium(new QueryCriterium(dv.getName(),
                        dvf.getName(), QueryCriterium.OP_EQUALS, val.toString()));

                    // execute query
                    executeQuery(qd, q, dv);
                }
            }
        }
        catch(Exception e)
        {
            onError(QueryRecordsDelegate.ERR_EXECUTE_QUERY.getText(), e);
        }
    }


    //--------------------------------------------------------------------------
    /**
     * Execute a Query on a DataView and display the results page(s).
     * @param queryDobj, the source dobj for the query.
     * @param query, a query (impl from the QueryDobj) that is complete.
     * @param dataview, the dataview that contains the queryDobj, used in
     *   executing the query.
     */
    private void executeQuery(QueryDobj queryDobj, Query query,
        DataView dataView)
        throws IOException, ServletException, DataSourceException
    {
        Util.argCheckNull(query);
        Util.argCheckNull(dataView);

        // perform the query
        DataViewDataSource dvds = MdnDataManager.getDataViewDS();
        RecordSet rs = dvds.select(query, dataView);

        // create a PagedSelectDelegate and set it into the UserState
        PagedSelectDelegate psd = new PagedSelectDelegate(rs, queryDobj.getId());
        getUserState().setPagedQuery(getUserState().getCurrentMenuActionId(), psd);

        // delegate to PagedSelectDelegate to display the list
        delegate(psd);
    }
}