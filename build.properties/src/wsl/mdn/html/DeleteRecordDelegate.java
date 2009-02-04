//==============================================================================
// DeleteRecordDelegate.java
// Copyright (c) 2001 WAP Solutions Ltd.
//==============================================================================

package wsl.mdn.html;

import wsl.fw.servlet.ServletBase;
import wsl.fw.servlet.ServletError;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.resource.ResId;
import wsl.fw.html.WslHtmlUtil;
import wsl.fw.html.WslHtmlTable;
import org.apache.ecs.*;
import org.apache.ecs.html.*;
import wsl.mdn.dataview.Record;
import java.io.IOException;
import javax.servlet.ServletException;

//------------------------------------------------------------------------------
/**
 * Delegate used by MdnHtmlServlet to delete records.
 */
public class DeleteRecordDelegate extends MdnHtmlServletDelegate
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:35:35 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/html/DeleteRecordDelegate.java $ ";

    public final static ResId ERR_DELETE = new ResId("HtmlDeleteRecordDelegate.error.delete");

    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public DeleteRecordDelegate()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Handle actions for the DeleteRecordDelegate.
     * @throws IOException, standard exception thrown by servlet methods.
     * @throws ServletException, standard exception thrown by servlet methods.
     */
    public void run() throws IOException, ServletException
    {
        try
        {
            // validate
            Util.argCheckNull(_request);
            Util.argCheckNull(_response);

            // make the cell
            MdnClientCell client = new MdnClientCell();
            client.setAlign(AlignType.CENTER);
            client.setClientTitle(ConfirmDeleteDelegate.TEXT_DELETE_TITLE.getText());
            client.setHelpUrl("/help/mdnhelp.html#bDeleteRecord");

            // table and title
            WslHtmlTable table = new WslHtmlTable();
            client.addElement(table);
            TD cell = new TD(MdnHtmlServlet.getTitleElement(
                ConfirmDeleteDelegate.TEXT_DELETE_TITLE.getText()));
            cell.setColSpan(2);
            TR row = new TR(cell);
            table.addElement(row);


            // get the rec index
            String strIndex = _request.getParameter(MdnHtmlServlet.PV_RECORD_INDEX);
            if(strIndex != null && strIndex.length() > 0)
            {
                int index = Integer.parseInt(strIndex);

                // get the record
                PagedSelectDelegate psd = (PagedSelectDelegate)getUserState().getCurrentPagedQuery();
                Record rec = psd.getRecord(index);
                if(rec != null)
                {
                    // delete the record
                    rec.delete();

                    // delegate to query
                    int queryId = psd.getQueryId();
                    String strQueryId = String.valueOf(queryId);
                    delegate(new QueryRecordsDelegate(strQueryId));
                    return;
                }
                else
                    table.addElement(new TR(new TD(WslHtmlUtil.esc(MdnHtmlServlet.ERR_REC_NOT_FOUND.getText()))));
            }
            else
                table.addElement(new TR(new TD(WslHtmlUtil.esc(MdnHtmlServlet.ERR_REC_NOT_FOUND.getText()))));

            // send output
            outputClientCell(client);
        }
        catch(Exception e)
        {
            onError(ERR_DELETE.getText(), e);
        }
    }
}

//==============================================================================
// end of file DeleteRecordDelegate.java
//==============================================================================
