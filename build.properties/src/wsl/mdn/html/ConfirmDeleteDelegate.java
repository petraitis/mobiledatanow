//==============================================================================
// ConfirmDeleteDelegate.java
// Copyright (c) 2001 WAP Solutions Ltd.
//==============================================================================

package wsl.mdn.html;

// imports
import wsl.fw.servlet.ServletBase;
import wsl.fw.util.Util;
import wsl.fw.resource.ResId;
import wsl.mdn.dataview.Record;
import wsl.fw.html.WslHtmlTable;
import wsl.fw.html.WslHtmlUtil;
import org.apache.ecs.*;
import org.apache.ecs.html.*;
import java.io.IOException;
import javax.servlet.ServletException;

//------------------------------------------------------------------------------
/**
 * Delegate for confirming a record delete
 */
public class ConfirmDeleteDelegate extends MdnHtmlServletDelegate
{
    //--------------------------------------------------------------------------
    // resources

    public static final ResId TEXT_DELETE_REC =
        new ResId("HtmlConfirmDeleteDelegate.text.DeleteRec");

    public static final ResId TEXT_DELETE_TITLE =
        new ResId("HtmlConfirmDeleteDelegate.text.DeleteTitle");

    public static final ResId BTN_DELETE =
        new ResId("HtmlConfirmDeleteDelegate.button.Delete");

    //--------------------------------------------------------------------------
    // construction

    /**
     * Default ctor
     */
    public ConfirmDeleteDelegate()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Handle actions for the ConfirmDeleteDelegate.
     * @throws IOException, standard exception thrown by servlet methods.
     * @throws ServletException, standard exception thrown by servlet methods.
     */
    public void run() throws ServletException, IOException
    {
        try
        {
            // validate
            Util.argCheckNull(_request);
            Util.argCheckNull(_response);

            // make the client
            MdnClientCell client = new MdnClientCell();
            client.setAlign(AlignType.CENTER);
            client.setClientTitle(TEXT_DELETE_TITLE.getText());
            client.setHelpUrl("/help/mdnhelp.html#bDeleteRecord");

            // form
            Form form = new Form();
            form.setMethod(Form.POST);
            form.setAction(makeHref());
            client.addElement(form);

            // table and heading
            WslHtmlTable table = new WslHtmlTable();
            form.addElement(table);
            TD cell = new TD(MdnHtmlServlet.getTitleElement(TEXT_DELETE_TITLE.getText()));
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
                    cell = new TD();
                    cell.addElement(WslHtmlUtil.esc(TEXT_DELETE_REC.getText()));
                    cell.addElement(new BR());
                    cell.addElement(new B(WslHtmlUtil.esc(rec.toString())));
                    table.addElement(new TD(cell));

                    // spacing
                    cell = new TD();
                    cell.setHeight(20);
                    row = new TR(cell);
                    table.addElement(row);

                    // delete button
                    Input input = new Input(Input.SUBMIT, "deleteButton", BTN_DELETE.getText());
                    cell = new TD(input);
                    row = new TR(cell);
                    table.addElement(row);

                    // action and param
                    input = new Input(Input.HIDDEN, MdnHtmlServlet.RP_ACTION,
                        MdnHtmlServlet.ACT_DELETERECORD);
                    row.addElement(input);
                    input = new Input(Input.HIDDEN, MdnHtmlServlet.PV_RECORD_INDEX,
                        strIndex);
                    row.addElement(input);
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
            onError(DeleteRecordDelegate.ERR_DELETE.getText(), e);
        }
    }
}

//==============================================================================
// ConfirmDeleteDelegate.java
//==============================================================================
