package wsl.mdn.html;

// imports
import java.util.Vector;
import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.ecs.html.*;
import org.apache.ecs.*;
import wsl.fw.servlet.*;
import wsl.fw.util.Util;
import wsl.fw.resource.ResId;
import wsl.fw.html.*;
import wsl.fw.msgserver.CriteriaAction;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:
 * @author
 * @version 1.0
 */

public class GetCriteriaDelegate extends MdnHtmlServletDelegate
{
    //--------------------------------------------------------------------------
    // constants

    public static final String RP_CRITERIA = "RpCriteria";


    //--------------------------------------------------------------------------
    // attributes

    private CriteriaAction _ca = null;


    //--------------------------------------------------------------------------
    // construction

    /**
     * CriteriaAction ctor
     */
    public GetCriteriaDelegate(CriteriaAction ca)
    {
        _ca = ca;
    }


    //--------------------------------------------------------------------------
    // output WML

    /**
     * Called by delegate()
     */
    public void run() throws javax.servlet.ServletException, java.io.IOException
    {
        try
        {
            // validate
            Util.argCheckNull(_request);
            Util.argCheckNull(_response);

            // cell properties
            MdnClientCell client = new MdnClientCell();
            client.setAlign(AlignType.CENTER);
            client.setHelpUrl("/help/mdnhelp.html#bLogin");

            // form
            Form form = new Form();
            form.setMethod(Form.POST);
            form.setAction(makeHref());
            client.addElement(form);

            // table
            WslHtmlTable table = new WslHtmlTable();
            form.addElement(table);

            // reply attribs
            String subject = _ca.getName();
            TD cell = new TD(MdnHtmlServlet.getTitleElement(subject));
            cell.setColSpan(2);
            TR row = new TR(cell);
            table.addElement(row);

            // text
            cell = new TD(_ca.getCriteriumLabel());
            row = new TR(cell);
            Input input = new Input(Input.TEXT, RP_CRITERIA, "");
            cell = new TD(input);
            row.addElement(cell);
            table.addElement(row);

            // query button
            input = new Input(Input.SUBMIT, "anchortext", _ca.getAnchorText());
            cell = new TD(input);
            row = new TR(cell);
            table.addElement(row);

            // Paged Iterator Id
            String piid = _request.getParameter(MdnHtmlServlet.PV_PAGEDITERATORID);
            input = new Input (
						Input.HIDDEN,
						MdnHtmlServlet.PV_PAGEDITERATORID,
                		piid);
            row.addElement(input);
            table.addElement(row);

            // recid
            String strIndex = _request.getParameter(MdnHtmlServlet.PV_RECORD_INDEX);
            input = new Input(Input.HIDDEN, MdnHtmlServlet.PV_RECORD_INDEX,
                strIndex);
            row.addElement(input);
            table.addElement(row);

            // action
            input = new Input(Input.HIDDEN, MdnHtmlServlet.RP_ACTION,
                MdnHtmlServlet.ACT_DO_CRITERIA_ACTION);
            row.addElement(input);
            table.addElement(row);

            // send output
            outputClientCell(client);
        }
        catch(Exception e)
        {
            onError("", e);
        }
    }
}