/**	$Id: ReplyMailDelegate.java,v 1.3 2002/11/14 03:43:49 tecris Exp $
 *
 * 	Respond to a Reply to email
 *
 */
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
import wsl.fw.datasource.*;
import wsl.fw.msgserver.MailMessageDobj;
import wsl.mdn.guiconfig.LoginSettings;

public class ReplyMailDelegate extends MdnHtmlServletDelegate
{
    //--------------------------------------------------------------------------
    // resources

    public static final ResId TEXT_TITLE =
        new ResId("ReplyMailDelegate.text.Title");
    public static final ResId TEXT_SEND =
        new ResId("ReplyMailDelegate.text.Send");
    public static final ResId TEXT_CANCEL =
        new ResId("ReplyMailDelegate.text.Cancel");
    public static final ResId LABEL_SUBJECT =
        new ResId("ReplyMailDelegate.label.Subject");
    public static final ResId LABEL_TEXT =
        new ResId("ReplyMailDelegate.label.Text");


    //--------------------------------------------------------------------------
    // constants

    public static final String RP_TEXT = "RpText";


    //--------------------------------------------------------------------------
    // construction

    /**
     * Default ctor
     */
    public ReplyMailDelegate()
    {
    }


    //--------------------------------------------------------------------------
    /**
     * Handle actions for the EditRecordDelegate.
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

            // cell properties
            MdnClientCell client = new MdnClientCell();
            client.setAlign(AlignType.CENTER);
            client.setHelpUrl("/help/mdnhelp.html#bLogin");

            // get the mail message
            String strIndex = _request.getParameter(MdnHtmlServlet.PV_RECORD_INDEX);
            Util.argCheckEmpty(strIndex);
            int index = Integer.parseInt(strIndex);

            // get the original mail
			UserState userState = getUserState ();
            PagedItrMsgDelegate pmd =
				(PagedItrMsgDelegate) userState.getCurrentPagedItDelegate ();
            MailMessageDobj mm = (MailMessageDobj)pmd.getMessage(index);
            Util.argCheckNull(mm);

            // form
            Form form = new Form();
            form.setMethod(Form.POST);
            form.setAction(makeHref());
            client.addElement(form);

            // table
            WslHtmlTable table = new WslHtmlTable();
            form.addElement(table);

            // reply attribs
            String subject = "Re: " + mm.getSubject();
            TD cell = new TD(MdnHtmlServlet.getTitleElement(subject));
            cell.setColSpan(2);
            TR row = new TR(cell);
            table.addElement(row);

            // text
            cell = new TD(LABEL_TEXT.getText());
            row = new TR(cell);
            table.addElement(row);

			TextArea textArea = new TextArea (RP_TEXT,4,30);
            cell = new TD(textArea);
            row = new TR(cell);
            table.addElement(row);

            // send button
            Input input = new Input(Input.SUBMIT, "send", TEXT_SEND.getText());
            cell = new TD(input);
            row = new TR(cell);
            table.addElement(row);

            // recid
            input = new Input (
						Input.HIDDEN,
						MdnHtmlServlet.PV_PAGEDITERATORID,
                		userState.getCurrentPItId ());
            row.addElement (input);
            table.addElement (row);

            // recid
            input = new Input(Input.HIDDEN, MdnHtmlServlet.PV_RECORD_INDEX,
                strIndex);
            row.addElement(input);
            table.addElement(row);

            // action
            input = new Input(Input.HIDDEN, MdnHtmlServlet.RP_ACTION,
                MdnHtmlServlet.ACT_SEND_MAIL);
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
