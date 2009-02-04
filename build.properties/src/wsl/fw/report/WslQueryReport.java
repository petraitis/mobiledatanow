
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
 * @author
 * @version 1.0
 */
package wsl.fw.report;

// imports
import java.util.Vector;
import java.util.Enumeration;
import java.awt.print.PrinterException;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.datasource.DataManager;
import wsl.fw.datasource.DataSource;
import wsl.fw.datasource.Query;
import wsl.fw.datasource.RecordSet;
import wsl.fw.datasource.DataObject;

import wsl.fw.resource.ResId;

/**
 * Formats and prints a report from a Query
 */
public class WslQueryReport extends WslReport
{
    //--------------------------------------------------------------------------
    // resources
    public static final ResId ERR_PRINT_PAGE  = new ResId("WslQueryReport.error.PrintPage");

    // attributes

    private Query _query;


    //--------------------------------------------------------------------------
    // init

    /**
     * Blank ctor
     */
    public WslQueryReport()
    {
    }

    /**
     * Constructor taking a query
     * @param query the Query to execute for this report
     */
    public WslQueryReport(Query query)
    {
        setQuery(query);
    }

    /**
     * Set a query into the report
     * @param query the Query to set
     */
    public void setQuery(Query query)
    {
        _query = query;
    }


    //--------------------------------------------------------------------------
    // printing

    /**
     * Print the report
     */
    public synchronized void printReport(boolean showPrintDialog) throws PrinterException
    {
        // must have a query
        Util.argCheckNull(_query);

        try
        {
            // execute the query
            Enumeration enums = _query.getQueryEntities();
            if(enums != null && enums.hasMoreElements())
            {
                // get the first entity
                String entityName = (String)enums.nextElement();

                // get the data source and execute query
                DataSource ds = DataManager.getDataSource(entityName);
                RecordSet rs = ds.select(_query);

                // get the row vector
                Vector rows = rs.getRows();

                // set data into the sections
                if(rows != null && rows.size() > 0)
                {
                    // header
                    DataObject row = (DataObject)rows.elementAt(0);
                    if(getHeaderSection() != null && getHeaderSection() instanceof WslQuerySection)
                        ((WslQuerySection)getHeaderSection()).setRow(row);

                    // footer
                    if(getFooterSection() != null && getFooterSection() instanceof WslQuerySection)
                        ((WslQuerySection)getFooterSection()).setRow(row);

                    // body
                    if(getBodySection() != null && getBodySection() instanceof WslQuerySection)
                        ((WslQuerySection)getBodySection()).setRows(rows);
                }
            }

            // print dialog
            boolean doPrint = !showPrintDialog || getPrinterJob().printDialog();

            // print
            if(doPrint)
                getPrinterJob().print();
        }
        catch(Exception e)
        {
            Log.error("WslQueryReport.printPage", e);
        }
    }
}