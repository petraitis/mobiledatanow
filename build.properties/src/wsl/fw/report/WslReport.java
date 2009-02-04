
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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.*;
import wsl.fw.util.Log;

/**
 * Encapsulates a printable report
 */
public class WslReport implements Pageable, Printable
{
    //--------------------------------------------------------------------------
    // constants

    private final int X_OFFSET = 10;
    private final int Y_HEAD_OFFSET = 30;
    private final int Y_FOOT_OFFSET = 10;


    //--------------------------------------------------------------------------
    // attributes
    private WslReportSection _header;
    private WslReportSection _body;
    private WslReportSection _footer;
    private PrinterJob _job;


    //--------------------------------------------------------------------------
    // Pageable interface

    /**
     * @return the number of pages in this report
     */
    public int getNumberOfPages()
    {
        // calculate heights
        PageFormat pf = getPageFormat(0);
        WslReportSection head = getHeaderSection();
        WslReportSection body = getBodySection();
        WslReportSection foot = getFooterSection();
        double headHeight = (head != null)? head.getSectionHeight(): 0;
        double footHeight = (foot != null)? foot.getSectionHeight() + Y_FOOT_OFFSET: 0;
        double bodyHeight = (int)pf.getImageableHeight() - (headHeight + footHeight);
        return body.getNumPages(bodyHeight);
    }

    /**
     * @return the PageFormat of the page specified by pageIndex.
     */
    public PageFormat getPageFormat(int pageIndex)
    {
        return getPrinterJob().defaultPage();
    }

    /**
     * @return Printable the page specified by the index
     */
    public Printable getPrintable(int pageIndex)
    {
        return this;
    }

    //--------------------------------------------------------------------------
    // accessors

    /**
     * Set a header section into the report
     * @param header the header section
     */
    public void setHeaderSection(WslReportSection header)
    {
        _header = header;
        if(_header != null)
            _header.setPrintOnce(true);
    }

    /**
     * @return WslReportSection the header section
     */
    public WslReportSection getHeaderSection()
    {
        return _header;
    }

    /**
     * Set a body section into the report
     * @param body the body section
     */
    public void setBodySection(WslReportSection body)
    {
        _body = body;
        if(_body != null)
            _body.setPrintOnce(false);
    }

    /**
     * @return WslReportSection the body section
     */
    public WslReportSection getBodySection()
    {
        return _body;
    }

    /**
     * Set a footer section into the report
     * @param footer the footer section
     */
    public void setFooterSection(WslReportSection footer)
    {
        _footer = footer;
        if(_footer != null)
            _footer.setPrintOnce(true);
    }

    /**
     * @return WslReportSection the footer section
     */
    public WslReportSection getFooterSection()
    {
        return _footer;
    }

    /**
     * @return PrinterJob the printer job
     */
    protected PrinterJob getPrinterJob()
    {
        // if null, create
        if(_job == null)
        {
            _job = PrinterJob.getPrinterJob();
            _job.setPageable(this);
        }
        return _job;
    }

    //--------------------------------------------------------------------------
    // printing

    /**
     * Print the report
     */
    public synchronized void printReport(boolean showPrintDialog) throws PrinterException
    {
        // print dialog
        boolean doPrint = !showPrintDialog || getPrinterJob().printDialog();

        // print
        if(doPrint)
            getPrinterJob().print();
    }

    /**
     * print the report
     */
    public int print(Graphics g, PageFormat pf, int page)
    {
        // convert to Graphics2D
        Graphics2D g2 = (Graphics2D)g;

        // translate coordinate system to exclude margins
        int xo = (int)pf.getImageableX() + X_OFFSET;
        int yo = (int)pf.getImageableY() + Y_HEAD_OFFSET;
        g2.translate(xo, yo);

        // calculate heights
        WslReportSection head = getHeaderSection();
        WslReportSection body = getBodySection();
        WslReportSection foot = getFooterSection();
        double headHeight = (head != null)? head.getSectionHeight(): 0;
        double footHeight = (foot != null)? foot.getSectionHeight() + Y_FOOT_OFFSET: 0;
        double bodyHeight = (int)pf.getImageableHeight() - (headHeight + footHeight);

        // print the header
        int ret = (page == 0)? Printable.PAGE_EXISTS: Printable.NO_SUCH_PAGE;
        if(head != null)
            head.printPage(g2, pf, page);

        // print the body
        if(body != null)
        {
            body.setPageHeight(bodyHeight);
            ret = body.printPage(g2, pf, page);
        }

        // print the footer
        if(foot != null)
            foot.printPage(g2, pf, page);

        // return
        return ret;
    }
}