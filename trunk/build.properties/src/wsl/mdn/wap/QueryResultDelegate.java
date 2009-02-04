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
import wsl.fw.datasource.*;
import wsl.mdn.dataview.*;
import wsl.mdn.guiconfig.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:
 * @author
 * @version 1.0
 */

public class QueryResultDelegate extends MdnWmlServletDelegate
{
    //--------------------------------------------------------------------------
    // construction

    /**
     * Default ctor
     */
    public QueryResultDelegate()
    {
    }


    //--------------------------------------------------------------------------
    // Output WML

    /**
     * Called by servlet
     */
    public void run() throws ServletException, IOException
    {
        try
        {
            // validate
            Util.argCheckNull(_request);
            Util.argCheckNull(_response);

            // get the record and index
            String strIndex = _request.getParameter(MdnWmlServlet.PV_RECORD_INDEX);
            if(strIndex != null && strIndex.length() > 0)
            {
                int index = Integer.parseInt(strIndex);

                // get the record
                PagedSelectDelegate psd = (PagedSelectDelegate)getUserState().getCurrentPagedQuery();
                Record rec = psd.getRecord(index);
                if(rec != null)
                {
                    // get the current menu action
                    MenuAction ma = getUserState().getCurrentMenu();
                    if(ma != null)
                    {
                        // must be a query
                        assert ma instanceof QueryRecords;

                        // sub query
                        if(ma.getChildren() != null && ma.getChildren().size() > 0)
                        {
                            // get the sub query
                            QueryRecords subq = (QueryRecords)ma.getChildren().elementAt(0);

                            // set the menu action
                            getUserState().setMenu(subq.getId());

                            // delegate to QueryRecordsDelegate
                            delegate(new QueryRecordsDelegate(rec));
                        }

                        // else show record
                        else
                        {
                            delegate(new ShowRecordDelegate(rec, strIndex));
                        }
                    }
                }
            }
        }
        catch(Exception e)
        {
            onError(e);
        }
    }
}
