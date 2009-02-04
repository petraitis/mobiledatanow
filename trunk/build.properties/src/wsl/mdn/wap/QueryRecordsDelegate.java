/**	$Id: QueryRecordsDelegate.java,v 1.4 2004/01/06 02:12:12 tecris Exp $
 *
 *	Delegate used by MdnWmlServlet to query records.
 *
 */
package wsl.mdn.wap;

import wsl.fw.servlet.ServletBase;
import wsl.fw.servlet.ServletError;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.resource.ResId;
import wsl.fw.wml.WEUtil;
import wsl.fw.datasource.Query;
import wsl.fw.datasource.QueryCriterium;
import wsl.fw.datasource.RecordSet;
import wsl.fw.datasource.DataSourceException;
import wsl.fw.wml.WslInput;
import wsl.mdn.dataview.QueryDobj;
import wsl.mdn.dataview.DataView;
import wsl.mdn.dataview.DataViewField;
import wsl.mdn.dataview.DataViewDataSource;
import wsl.mdn.dataview.Record;
import wsl.mdn.common.MdnDataManager;
import wsl.mdn.guiconfig.*;
import org.apache.ecs.wml.*;

import java.io.IOException;
import java.util.Vector;
import javax.servlet.ServletException;

public class QueryRecordsDelegate extends MdnWmlServletDelegate
{
    public final static ResId  TEXT_TITLE                  = new ResId("QueryRecordsDelegate.text.title");
    public final static ResId  TEXT_ENTERQUERYPARAMS       = new ResId("QueryRecordsDelegate.text.EnterQueryParams");
    public final static ResId  TEXT_DOQUERY                = new ResId("QueryRecordsDelegate.text.DoQuery");
    public final static ResId  TEXT_OP_EQUALS              = new ResId("QueryRecordsDelegate.text.op.Equals");
    public final static ResId  TEXT_OP_NOT_EQUALS          = new ResId("QueryRecordsDelegate.text.op.NotEquals");
    public final static ResId  TEXT_OP_LIKE                = new ResId("QueryRecordsDelegate.text.op.Like");
    public final static ResId  TEXT_OP_NOT_LIKE            = new ResId("QueryRecordsDelegate.text.op.NotLike");
    public final static ResId  TEXT_OP_GREATER_THAN        = new ResId("QueryRecordsDelegate.text.op.GreaterThan");
    public final static ResId  TEXT_OP_GREATER_THAN_EQUALS = new ResId("QueryRecordsDelegate.text.op.GreaterThanEquals");
    public final static ResId  TEXT_OP_LESS_THAN           = new ResId("QueryRecordsDelegate.text.op.LessThan");
    public final static ResId  TEXT_OP_LESS_THAN_EQUALS    = new ResId("QueryRecordsDelegate.text.op.LessThanEquals");
    public final static ResId  ERR_NOT_QUERY               = new ResId("QueryRecordsDelegate.error.notQuery");
    public final static ResId  ERR_EXECUTE_QUERY           = new ResId("QueryRecordsDelegate.err.ExecuteQuery");

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

            // replace any userid criteria
            String uid = "";
            if(userState.getUser() != null &&
                userState.getUser().getConfigSettings() != null)
			{
                uid = userState.getUser().getConfigSettings ();
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

        // create card and heading
        Card card = new Card();
        card.setTitle(WEUtil.esc(TEXT_TITLE.getText()));
        P p =  new P(Alignment.LEFT, Mode.WRAP);
        card.addElement(p);
        p.addElement(WEUtil.esc(TEXT_ENTERQUERYPARAMS.getText() + queryDobj.getName()));

        // Back
        Do doBack = new Do(DoType.OPTIONS,
            WEUtil.esc(MdnWmlServlet.TEXT_BACK.getText()));
        doBack.addElement(new Prev());
        card.addElement(doBack);

        // create do/go element
        Do doQuery = new Do(DoType.ACCEPT, TEXT_DOQUERY.getText());
        Go goQuery = new Go(makeHref(), Method.GET);
        doQuery.addElement(goQuery);
        goQuery.addElement(new Postfield(ServletBase.RP_ACTION,
            MdnWmlServlet.ACT_QUERYRECORDS));
        goQuery.addElement(new Postfield(ServletBase.RP_SUB_ACTION,
            String.valueOf(queryDobj.getId())));
        goQuery.addElement(new Postfield(RP_QUERYPARAMS, "Y"));

        // iterate over the criteria producing value entry fields
        Vector criteria = query.getCriteria();

        for (int i = 0; i < criteria.size(); i++)
        {
            QueryCriterium qc = (QueryCriterium) criteria.get(i);

            if (!qc.isComplete())
            {
                // get display name, field name and operator text
                DataViewField dvField        = (DataViewField) dataView.getField(qc._fieldName);
                String        escDisplayName = WEUtil.esc(dvField.getDisplayName());
                String        escFieldName   = WEUtil.esc(qc._fieldName);
                String        escOperator    = WEUtil.esc(getOperatorText(qc));
                String varName = MdnWmlServlet.getRandomVarName();

                // add input elemet to card
                card.addElement(p = new P());
                p.addElement(escDisplayName + " " + escOperator);
                p.addElement(MdnWmlServlet.TEXT_PROMPTCOLON.getText());
                p.addElement(new WslInput(varName, escDisplayName, null,
                    null, false));

                // add postfield element to go
                goQuery.addElement(new Postfield(RP_QUERYPARAM_PREFIX
                    + String.valueOf(i), WEUtil.makeVar(varName)));
            }
        }

		// action anchor
		String anchorText = WEUtil.esc (TEXT_DOQUERY.getText ());
		Anchor a = new Anchor (anchorText, goQuery);
		a.addElement (anchorText);
		p = new P ();
		p.addElement (a);
		card.addElement (p);

        // query option
        card.addElement(doQuery);

        wmlOutput(card, true);
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
