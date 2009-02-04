/**	$Header: /wapsolutions/cvsroot/mdn/mdn/src/wsl/mdn/wap/ReplyMailDelegate.java,v 1.1.1.1 2002/06/11 23:35:35 jonc Exp $
 *
 * 	Repond to a reply-email action
 */
package wsl.mdn.wap;

// imports
import java.util.Vector;
import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.ecs.wml.*;
import wsl.fw.servlet.*;
import wsl.fw.util.Util;
import wsl.fw.resource.ResId;
import wsl.fw.wml.*;
import wsl.fw.datasource.*;
import wsl.fw.msgserver.MailMessageDobj;
import wsl.mdn.guiconfig.LoginSettings;

public class ReplyMailDelegate extends MdnWmlServletDelegate
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
    // attributes

    private boolean _doSend = false;


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

            // make the card
            Card card = new Card();
            card.setTitle(WEUtil.esc(TEXT_TITLE.getText()));

            // get the record
            P p;
            String strIndex = _request.getParameter(MdnWmlServlet.PV_RECORD_INDEX);
            if(strIndex != null && strIndex.length() > 0)
            {
                int index = Integer.parseInt(strIndex);
				UserState userState = getUserState ();

                // get the mail message
                PagedItrMsgDelegate pmd =
					(PagedItrMsgDelegate) userState.getCurrentPagedItDelegate ();
                MailMessageDobj mm = (MailMessageDobj)pmd.getMessage(index);
                Util.argCheckNull(mm);

                // build the save go element
                Go goSave = new Go(makeHref(), Method.GET);
                goSave.addElement(new Postfield(ServletBase.RP_ACTION, MdnWmlServlet.ACT_SEND_MAIL));

                // add input element to card
                String varName = MdnWmlServlet.getRandomVarName();
                p = new P();
                card.addElement(p);
                p.addElement(WEUtil.esc(LABEL_TEXT.getText()));
                p.addElement(MdnWmlServlet.TEXT_PROMPTCOLON.getText());
                p.addElement(new WslInput(varName, "", "", null, false));

                // add postfield element to go
                goSave.addElement(new Postfield(RP_TEXT, WEUtil.makeVar(varName)));

                // add IteratorId
                goSave.addElement (
					new Postfield (
						MdnWmlServlet.PV_PAGEDITERATORID,
						userState.getCurrentPItId ()));

                // add rec index to save
                goSave.addElement(new Postfield(MdnWmlServlet.PV_RECORD_INDEX, strIndex));

                // send anchor
                Anchor a = new Anchor(WEUtil.esc(TEXT_SEND.getText()), goSave);
                a.addElement(WEUtil.esc(TEXT_SEND.getText()));
                p = new P();
                p.addElement(a);
                card.addElement(p);

                // send do
                Do doSave = new Do(DoType.ACCEPT, TEXT_SEND.getText());
                doSave.addElement(goSave);
                card.addElement(doSave);

                // Main
                Do doMain = new Do(DoType.OPTIONS, MdnWmlServlet.TEXT_MAIN.getText());
                Go goMain = new Go(makeHref(MdnWmlServlet.ACT_MAINMENU), Method.GET);
                doMain.addElement(goMain);
                card.addElement(doMain);
            }
            else
            {
                p = new P();
                p.addElement(WEUtil.esc(MdnWmlServlet.ERR_REC_NOT_FOUND.getText()));
                card.addElement(p);
            }

            // Back
            Do doOp = new Do(DoType.PREV, MdnWmlServlet.TEXT_BACK.getText());
            doOp.addElement(new Prev());
            card.addElement(doOp);

            // send output
            wmlOutput(card, true);
        }
        catch(Exception e)
        {
            onError("", e);
        }
    }
}