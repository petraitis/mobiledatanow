//==============================================================================
// DeleteRecordDelegate.java
// Copyright (c) 2001 WAP Solutions Ltd.
//==============================================================================

package wsl.mdn.wap;

import wsl.fw.servlet.ServletBase;
import wsl.fw.servlet.ServletError;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.resource.ResId;
import wsl.fw.wml.WEUtil;
import wsl.mdn.dataview.Record;
import org.apache.ecs.wml.*;
import java.io.IOException;
import javax.servlet.ServletException;

//------------------------------------------------------------------------------
/**
 * Delegate used by MdnWmlServlet to delete records.
 */
public class DeleteRecordDelegate extends MdnWmlServletDelegate
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:35:35 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/wap/DeleteRecordDelegate.java $ ";

    public final static ResId ERR_DELETE = new ResId("DeleteRecordDelegate.error.delete");

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

            // make the card
            Card card = new Card();
            card.setTitle(WEUtil.esc(ConfirmDeleteDelegate.TEXT_TITLE.getText()));

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
                    // delete the record
                    rec.delete();

                    // delegate to query
                    int queryId = psd.getQueryId();
                    String strQueryId = String.valueOf(queryId);
                    delegate(new QueryRecordsDelegate(strQueryId));
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
            onError(ERR_DELETE.getText(), e);
        }
    }
}

//==============================================================================
// end of file DeleteRecordDelegate.java
//==============================================================================
