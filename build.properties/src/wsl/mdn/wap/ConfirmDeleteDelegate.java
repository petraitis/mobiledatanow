package wsl.mdn.wap;

// imports
import wsl.fw.servlet.ServletBase;
import wsl.fw.util.Util;
import wsl.fw.resource.ResId;
import wsl.mdn.dataview.Record;
import wsl.fw.wml.WEUtil;
import org.apache.ecs.wml.*;
import java.io.IOException;
import javax.servlet.ServletException;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:
 * @author
 * @version 1.0
 */

public class ConfirmDeleteDelegate extends MdnWmlServletDelegate
{
    //--------------------------------------------------------------------------
    // resources

    public static final ResId TEXT_DELETE_REC =
        new ResId("ConfirmDeleteDelegate.text.DeleteRec");
    public static final ResId TEXT_TITLE =
        new ResId("ConfirmDeleteDelegate.text.title");


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

            // make the card
            Card card = new Card();
            card.setTitle(WEUtil.esc(TEXT_TITLE.getText()));

            // get the rec index
            P p = new P();
            card.addElement(p);
            String strIndex = _request.getParameter(MdnWmlServlet.PV_RECORD_INDEX);
            if(strIndex != null && strIndex.length() > 0)
            {
                int index = Integer.parseInt(strIndex);

                // get the record
                PagedSelectDelegate psd = (PagedSelectDelegate)getUserState().getCurrentPagedQuery();
                Record rec = psd.getRecord(index);
                if(rec != null)
                {
                    p.addElement(WEUtil.esc(TEXT_DELETE_REC.getText()));
                    p.addElement(new BR());
                    p.addElement(WEUtil.esc(rec.toString()));

                    // Ok do action
                    Do doOp = new Do(DoType.ACCEPT,
                        WEUtil.esc(MdnWmlServlet.TEXT_OK.getText()));
                    String href = makeHref(MdnWmlServlet.ACT_DELETERECORD);
                    href = addParam(href, MdnWmlServlet.PV_RECORD_INDEX, strIndex);
                    Go go = new Go(href);
                    doOp.addElement(go);
                    card.addElement(doOp);
                }
                else
                    p.addElement(WEUtil.esc(MdnWmlServlet.ERR_REC_NOT_FOUND.getText()));
            }
            else
                p.addElement(WEUtil.esc(MdnWmlServlet.ERR_REC_NOT_FOUND.getText()));

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