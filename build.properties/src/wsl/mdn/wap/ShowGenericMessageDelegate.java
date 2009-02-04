/**	$Header: /wapsolutions/cvsroot/mdn/mdn/src/wsl/mdn/wap/ShowGenericMessageDelegate.java,v 1.1.1.1 2002/06/11 23:35:35 jonc Exp $
 *
 *	Display a Generic ItemDobj
 *
 */
package wsl.mdn.wap;

import java.util.Vector;
import java.io.IOException;
import javax.servlet.ServletException;

import org.apache.ecs.wml.*;

import wsl.fw.servlet.*;
import wsl.fw.util.Util;
import wsl.fw.resource.ResId;
import wsl.fw.wml.WEUtil;
import wsl.fw.datasource.*;
import wsl.fw.msgserver.*;
import wsl.mdn.guiconfig.LoginSettings;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:
 * @author
 * @version 1.0
 */

public class ShowGenericMessageDelegate extends MdnWmlServletDelegate
{
    //--------------------------------------------------------------------------
    // construction

    /**
     * Default ctor
     */
    public ShowGenericMessageDelegate()
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

                // get the message
                PagedItrMsgDelegate pmd =
					(PagedItrMsgDelegate) getUserState().getCurrentPagedItDelegate();
                ItemDobj md = (ItemDobj)pmd.getMessage(index);
                Util.argCheckNull(md);

                // subject as title
                card.setTitle(WEUtil.esc(md.toString()));

                /*
				 *	Iterate thru' the fields, embelishing as required.
				 */
                Vector labels = md.getFieldLabels();
                for (int i = 0; i < labels.size(); i++)
                {
                    /*
					 *	Get the label and value
					 */
                    String label = (String) labels.elementAt (i);
                    p.addElement (WEUtil.esc (label + ": "));

                    String value = WEUtil.esc (
						Util.noNullStr (md.getFieldValue (label)));

					/*
					 *	If the field supports click&dial, add appropriate tags
					 */
					if (value.length () > 0 && md.isPhonedialField (label))
						p.addElement (WEUtil.makePhoneLink (value, value));
					else
                    	p.addElement (WEUtil.esc(value));

                    p.addElement(new BR());
                }
            }

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