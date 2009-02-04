package wsl.mdn.wap;

// imports
import wsl.fw.servlet.ServletBase;
import wsl.fw.servlet.ServletError;
import wsl.fw.util.Util;
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
import wsl.mdn.common.MdnDataManager;
import org.apache.ecs.wml.*;
import java.io.IOException;
import java.util.Vector;
import javax.servlet.ServletException;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:
 * @author
 * @version 1.0
 */

public class GroupedQueryDelegate extends MdnWmlServletDelegate
{
    //--------------------------------------------------------------------------
    // attributes

   private int _queryId;


    //--------------------------------------------------------------------------
    // construction

    /**
     * Param Ctor
     * @param query
     */
    public GroupedQueryDelegate(int queryId)
    {
        _queryId = queryId;
    }


    //--------------------------------------------------------------------------
    // wml

    /**
     * Output WML
     */
    public void run() throws ServletException, IOException
    {
        DataView dataView = null;
        try
        {
            // get the query and data view
            Integer queryId = new Integer(_queryId);
            QueryDobj queryDobj = getUserState().getCache().getQueryDobj(queryId);
             dataView  = getUserState().getCache().getQueryDataView(queryId);
            queryDobj.setDataView(dataView);

            // perform the query
            DataViewDataSource dvds = MdnDataManager.getDataViewDS();
            Vector v = dvds.selectDistinct(queryDobj);

            // create a PagedVectorDelegate and set it into the UserState
            PagedGroupSelectDelegate pvd = new PagedGroupSelectDelegate(v, queryDobj.getId());
            getUserState().setPagedGroupQuery(getUserState().getCurrentMenuActionId(), pvd);

            // delegate to PagedVectorDelegate to display the list
            delegate(pvd);
        }
        catch(Exception e)
        {
            onError(QueryRecordsDelegate.ERR_EXECUTE_QUERY.getText(), e);
        }
    }
}