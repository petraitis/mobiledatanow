package wsl.mdn.wap;

// imports
import java.util.Vector;
import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.ecs.wml.*;
import wsl.fw.servlet.*;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.resource.ResId;
import wsl.fw.wml.WEUtil;
import wsl.fw.datasource.*;
import wsl.fw.msgserver.TaskDobj;
import wsl.mdn.guiconfig.LoginSettings;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:
 * @author
 * @version 1.0
 */

public class ShowTaskDelegate extends MdnWmlServletDelegate
{
    //--------------------------------------------------------------------------
    // construction

    /**
     * Default ctor
     */
    public ShowTaskDelegate()
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

            // make the card
            Card card = new Card();
            P p = new P();
            card.addElement(p);

            // get the record and index
            String strIndex = _request.getParameter(MdnWmlServlet.PV_RECORD_INDEX);
            if(strIndex != null && strIndex.length() > 0)
            {
                int index = Integer.parseInt(strIndex);

                // get the contact
                PagedItrMsgDelegate pcd =
					(PagedItrMsgDelegate) getUserState ().getCurrentPagedItDelegate ();
                TaskDobj a = (TaskDobj)pcd.getMessage(index);
                Util.argCheckNull(a);

                // subject as title
                card.setTitle(WEUtil.esc(a.getFieldValue(TaskDobj.FLD_TASK)));

                // iterate the fields
                String label;
                String value;
                Vector labels = a.getFieldLabels();
                for(int i = 0; i < labels.size(); i++)
                {
                    // get the label and value
                    label = (String)labels.elementAt(i);
                    value = a.getFieldValue(label);

                    // add element
                    value = Util.noNullStr(value);
                    p.addElement(WEUtil.esc(label + ": " + value));
                    p.addElement(new BR());
                }
            }
            else
                p.addElement("Bad index");

            // Main
            Do doMain = new Do(DoType.OPTIONS, MdnWmlServlet.TEXT_MAIN.getText());
            Go goMain = new Go(makeHref(MdnWmlServlet.ACT_MAINMENU), Method.GET);
            doMain.addElement(goMain);
            card.addElement(doMain);

            // Back
            Do doOp = new Do(DoType.PREV, MdnWmlServlet.TEXT_BACK.getText());
            doOp.addElement(new Prev());
            card.addElement(doOp);

            // send output
            wmlOutput(card, true);
        }
        catch(Exception e)
        {
            onError(e);
        }
    }
}