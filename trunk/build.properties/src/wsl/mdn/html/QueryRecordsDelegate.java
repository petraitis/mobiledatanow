//==============================================================================
// QueryRecordsDelegate.java
// Copyright (c) 2001 WAP Solutions Ltd.
//==============================================================================

package wsl.mdn.html;

import wsl.fw.servlet.ServletError;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.resource.ResId;
import wsl.fw.html.*;
import wsl.fw.datasource.Query;
import wsl.fw.datasource.QueryCriterium;
import wsl.fw.datasource.RecordSet;
import wsl.fw.datasource.DataSourceException;
import wsl.mdn.dataview.QueryDobj;
import wsl.mdn.dataview.DataView;
import wsl.mdn.dataview.DataViewField;
import wsl.mdn.dataview.DataViewDataSource;
import wsl.mdn.dataview.Record;
import wsl.mdn.common.MdnDataManager;
import wsl.mdn.guiconfig.*;
import org.apache.ecs.html.*;
import org.apache.ecs.*;
import java.io.IOException;
import java.util.Vector;
import javax.servlet.ServletException;

//------------------------------------------------------------------------------
/**
 * Delegate used by MdnHtmlServlet to query records.
 */
public class QueryRecordsDelegate extends MdnHtmlServletDelegate
{
    // version tag
    private final static String _ident = "$Date: 2004/01/06 02:09:39 $  $Revision: 1.4 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/html/QueryRecordsDelegate.java $ ";

    public final static ResId  TEXT_ENTERQUERYPARAMS
        = new ResId("HtmlQueryRecordsDelegate.text.EnterQueryParams");
    public final static ResId  TEXT_DOQUERY
        = new ResId("HtmlQueryRecordsDelegate.text.DoQuery");
    public final static ResId  TEXT_OP_EQUALS
        = new ResId("HtmlQueryRecordsDelegate.text.op.Equals");
    public final static ResId  TEXT_OP_NOT_EQUALS
        = new ResId("HtmlQueryRecordsDelegate.text.op.NotEquals");
    public final static ResId  TEXT_OP_LIKE
        = new ResId("HtmlQueryRecordsDelegate.text.op.Like");
    public final static ResId  TEXT_OP_NOT_LIKE
        = new ResId("HtmlQueryRecordsDelegate.text.op.NotLike");
    public final static ResId  TEXT_OP_GREATER_THAN
        = new ResId("HtmlQueryRecordsDelegate.text.op.GreaterThan");
    public final static ResId  TEXT_OP_GREATER_THAN_EQUALS
        = new ResId("HtmlQueryRecordsDelegate.text.op.GreaterThanEquals");
    public final static ResId  TEXT_OP_LESS_THAN
        = new ResId("HtmlQueryRecordsDelegate.text.op.LessThan");
    public final static ResId  TEXT_OP_LESS_THAN_EQUALS
        = new ResId("HtmlQueryRecordsDelegate.text.op.LessThanEquals");
    public final static ResId  ERR_NOT_QUERY
        = new ResId("HtmlQueryRecordsDelegate.error.notQuery");
    public final static ResId  ERR_EXECUTE_QUERY
        = new ResId("HtmlQueryRecordsDelegate.err.ExecuteQuery");

    public final static String RP_QUERYPARAMS        = "RPqps";
    public final static String RP_QUERYPARAM_PREFIX  = "RPqp";


    //--------------------------------------------------------------------------
    // attributes

    private String _queryId = null;
    private Record _parentRec = null;


    //--------------------------------------------------------------------------
    // construction

    /**
     * Default constructor.
     */
    public QueryRecordsDelegate()
    {
    }

    /**
     * Ctor taking a queryId
     * @param queryId
     */
    public QueryRecordsDelegate(String queryId)
    {
        _queryId = queryId;
    }

    /**
     * Ctor taking a parent record
     */
    public QueryRecordsDelegate(Record parentRec)
    {
        _parentRec = parentRec;
    }


    //--------------------------------------------------------------------------
    /**
     * Handle actions for the QueryRecordsDelegate.
     * @throws IOException, standard exception thrown by servlet methods.
     * @throws ServletException, standard exception thrown by servlet methods.
     */
    public void run() throws IOException, ServletException
    {
        try
        {
            Util.argCheckNull(_request);
            Util.argCheckNull(_response);

            // get the userState and queryId
            UserState userState  = getUserState();
            String strQueryId = _queryId;
            if(strQueryId == null || strQueryId.length() == 0)
            {
                // get the current menu action
                MenuAction ma = userState.getCurrentMenu();
                if(ma != null && ma instanceof QueryRecords)
                    strQueryId = ((QueryRecords)ma).getQueryId().toString();
                else
                    Log.error(ERR_NOT_QUERY.getText() + ma);
            }

            // parse the id
            Integer queryId = new Integer(strQueryId);

            // get the query and data view
            QueryDobj queryDobj = userState.getCache().getQueryDobj(queryId);
            DataView  dataView  = userState.getCache().getQueryDataView(queryId);

            // create Query impl from QueryDobj, this is a new instance that is
            // not shared even if the QueryDobj itself is cached and shared
            Query query = queryDobj.createNewImpl();

            // Set userid keyword
            String uid = "";
            if (userState.getUser() != null &&
                userState.getUser().getConfigSettings() != null)
			{
                uid = userState.getUser().getConfigSettings();
			}
			query.setUseridKeyword (uid);

            // check if it is complete and execute
            if (query.isComplete())
                executeQuery(queryDobj, query, dataView);
            else
            {
                // else check if we have been given params
                if (_request.getParameter(RP_QUERYPARAMS) != null)
                {
                    // if yes use then to complete and execute
                    paramCompleteQuery(query);
                    assert query.isComplete();
                    executeQuery(queryDobj, query, dataView);
                }
                else
                {
                    // else display query param screen
                    displayQueryCriteria(queryDobj, query, dataView);
                }
            }
        }
        catch (Exception e)
        {
           onError("QueryRecordsDelegate.run: ", e);
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
        throws IOException, ServletException
    {
        Log.debug("q = " + query + "; dv = " + dataView);
        Util.argCheckNull(query);
        Util.argCheckNull(dataView);

        try
        {
            // grouped query
            int gfid = queryDobj.getGroupFieldId();
            if(gfid > 0)
                delegate(new GroupedQueryDelegate(queryDobj.getId()));

            // non-grouped query
            else
            {
                // perform the query
                DataViewDataSource dvds = MdnDataManager.getDataViewDS();
                RecordSet rs = null;

                // if we have a parent do a parent select
                if(_parentRec != null)
                    rs = dvds.select(query, dataView, _parentRec);
                else
                    rs = dvds.select(query, dataView);

                // create a PagedSelectDelegate and set it into the UserState
                PagedSelectDelegate psd = new PagedSelectDelegate(rs, queryDobj.getId());
                getUserState().setPagedQuery(getUserState().getCurrentMenuActionId(),
                    psd);

                // delegate to PagedSelectDelegate to display the list
                delegate(psd);
            }
        }
        catch (DataSourceException e)
        {
            getServlet().onError(_request, _response,
                new ServletError(ERR_EXECUTE_QUERY.getText()
                + dataView.getName(), e));
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Complete an incomplete query using the http parameters.
     * This is called in preparation to executing the query.
     * @param query, the incomplete query that is to be completed using http
     *   params.
     */
    private void paramCompleteQuery(Query query)
        throws IOException, ServletException
    {
        Util.argCheckNull(query);

        // iterate over the criteria filling in the values
        Vector criteria = query.getCriteria();

        for (int i = 0; i < criteria.size(); i++)
        {
            QueryCriterium qc = (QueryCriterium) criteria.get(i);

            if (!qc.isComplete())
            {
                String queryParam = _request.getParameter(RP_QUERYPARAM_PREFIX
                    + String.valueOf(i));
                qc._value = queryParam;
            }
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Display the input fields to collect the query criteria for an incomplete
     * query.
     * @param queryDobj, the source dobj for the query.
     * @param query, a query (impl from the QueryDobj) that is inComplete. Input
     *   fields will be generated to collect data to complete the query.
     * @param dataview, the dataview that contains the queryDobj, used to get
     *   display names for the fields.
     */
    private void displayQueryCriteria(QueryDobj queryDobj, Query query,
        DataView dataView)
        throws IOException, ServletException
    {
        Util.argCheckNull(queryDobj);
        Util.argCheckNull(query);
        Util.argCheckNull(dataView);

        // cell properties
        MdnClientCell client = new MdnClientCell();
        client.setAlign(AlignType.CENTER);
        client.setHelpUrl("/help/mdnhelp.html#bParamQuery");

        // title
        String title = TEXT_ENTERQUERYPARAMS.getText() + queryDobj.getName();
        client.setClientTitle(title);

        // form
        Form form = new Form();
        form.setMethod(Form.POST);
        form.setAction(makeHref());
        client.addElement(form);

        // table
        WslHtmlTable table = new WslHtmlTable();
        form.addElement(table);

        // heading
        TD cell = new TD(MdnHtmlServlet.getTitleElement(title));
        cell.setColSpan(2);
        TR row = new TR(cell);
        table.addElement(row);

        // spacing
        cell = new TD();
        cell.setHeight(20);
        row = new TR(cell);
        table.addElement(row);

        // iterate over the criteria producing value entry fields
        Input in;
        Vector criteria = query.getCriteria();
        for (int i = 0; i < criteria.size(); i++)
        {
            QueryCriterium qc = (QueryCriterium) criteria.get(i);
            if (!qc.isComplete())
            {
                // get display name, field name and operator text
                DataViewField dvField        = (DataViewField) dataView.getField(qc._fieldName);
                String        escDisplayName = WslHtmlUtil.esc(dvField.getDisplayName());
                String        escFieldName   = WslHtmlUtil.esc(qc._fieldName);
                String        escOperator    = WslHtmlUtil.esc(getOperatorText(qc));
                String varName = RP_QUERYPARAM_PREFIX + String.valueOf (i);

                // add input elemet to card
                cell = new TD(escDisplayName + " " + escOperator);
                row = new TR(cell);
                in = new Input(Input.TEXT, varName, "");
                cell = new TD(in);
                row.addElement(cell);
                table.addElement(row);
            }
        }

        // Execute button
        in = new Input(Input.SUBMIT, "Text", TEXT_DOQUERY.getText());
        cell = new TD(in);
        row = new TR(cell);
        table.addElement(row);

        // action
        in = new Input(Input.HIDDEN, MdnHtmlServlet.RP_ACTION,
            MdnHtmlServlet.ACT_QUERYRECORDS);
        row.addElement(in);
        table.addElement(row);

        // hidden query params post field
        in = new Input(Input.HIDDEN, RP_QUERYPARAMS, "Y");
        row.addElement(in);
        table.addElement(row);

        // output client
        outputClientCell(client);
    }

    //--------------------------------------------------------------------------
    /**
     * @return text describing the query criterium operator
     */
    public String getOperatorText(QueryCriterium qc)
    {
        // fixme
        String op = qc._op;

        if (op.equals(QueryCriterium.OP_EQUALS))
            return TEXT_OP_EQUALS.getText();
        else if (op.equals(QueryCriterium.OP_NOT_EQUALS))
            return TEXT_OP_NOT_EQUALS.getText();
        else if (op.equals(QueryCriterium.OP_LIKE))
            return TEXT_OP_LIKE.getText();
        else if (op.equals(QueryCriterium.OP_NOT_LIKE))
            return TEXT_OP_NOT_LIKE.getText();
        else if (op.equals(QueryCriterium.OP_GREATER_THAN))
            return TEXT_OP_GREATER_THAN.getText();
        else if (op.equals(QueryCriterium.OP_GREATER_THAN_EQUALS))
            return TEXT_OP_GREATER_THAN_EQUALS.getText();
        else if (op.equals(QueryCriterium.OP_LESS_THAN))
            return TEXT_OP_LESS_THAN.getText();
        else if (op.equals(QueryCriterium.OP_LESS_THAN_EQUALS))
            return TEXT_OP_LESS_THAN_EQUALS.getText();
        else
            return "";
    }
}

//==============================================================================
// end of file QueryRecordsDelegate.java
//==============================================================================
