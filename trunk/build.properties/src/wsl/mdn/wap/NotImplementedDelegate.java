package wsl.mdn.wap;

import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.resource.ResId;
import wsl.fw.wml.WEUtil;
import wsl.fw.servlet.ServletBase;
import wsl.fw.servlet.ServletError;
import org.apache.ecs.wml.*;
import java.io.IOException;
import javax.servlet.ServletException;
import java.util.Vector;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:
 * @author
 * @version 1.0
 */

public class NotImplementedDelegate extends MdnWmlServletDelegate
{
    //--------------------------------------------------------------------------
    // constants

    public final static ResId TEXT_TITLE = new ResId("NotImplementedDelegate.text.title");
    public final static ResId ERR_MSG = new ResId("NotImplementedDelegate.error.Msg");


    //--------------------------------------------------------------------------
    // construction

    public NotImplementedDelegate()
    {
    }


    //--------------------------------------------------------------------------
    // output wml

    public void run() throws ServletException, IOException
    {
        try
        {
            Util.argCheckNull(_request);
            Util.argCheckNull(_response);

            // make the card
            Card card = new Card();
            card.setTitle(WEUtil.esc(TEXT_TITLE.getText()));
            P p = new P();
            p.addElement(WEUtil.esc(ERR_MSG.getText()));
            card.addElement(p);

            // Back
            Do doBack = new Do(DoType.PREV, MdnWmlServlet.TEXT_BACK.getText());
            doBack.addElement(new Prev());
            card.addElement(doBack);

            // Main
            Do doMain = new Do(DoType.OPTIONS, MdnWmlServlet.TEXT_MAIN.getText());
            Go goMain = new Go(makeHref(MdnWmlServlet.ACT_MAINMENU), Method.GET);
            doMain.addElement(goMain);
            card.addElement(doMain);

            // output the card
            wmlOutput(card, true);
        }
        catch(Exception e)
        {
            onError(e);
        }
    }
}