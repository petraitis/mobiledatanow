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
import wsl.fw.msgserver.CriteriaAction;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:
 * @author
 * @version 1.0
 */

public class GetCriteriaDelegate extends MdnWmlServletDelegate
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
            Util.argCheckNull(_ca);

            // make the card
            Card card = new Card();
            card.setTitle(WEUtil.esc(_ca.getName()));
            P p;

            // build the go element
            Go go = new Go(makeHref(), Method.GET);
            go.addElement(new Postfield(ServletBase.RP_ACTION, MdnWmlServlet.ACT_DO_CRITERIA_ACTION));

            // add input element to card
            String label = _ca.getCriteriumLabel();
            String varName = MdnWmlServlet.getRandomVarName();
            p = new P();
            card.addElement(p);
            p.addElement(WEUtil.esc(label));
            p.addElement(MdnWmlServlet.TEXT_PROMPTCOLON.getText());
            p.addElement(new WslInput(varName, "", "", null, false));

            // add postfield element to go
            go.addElement(new Postfield(RP_CRITERIA, WEUtil.makeVar(varName)));

            // Paged Iterator Id
            String piid = _request.getParameter (MdnWmlServlet.PV_PAGEDITERATORID);
            go.addElement (new Postfield (MdnWmlServlet.PV_PAGEDITERATORID, piid));
            go.addElement (new Postfield (MdnWmlServlet.PV_RECORD_INDEX,
                _request.getParameter(MdnWmlServlet.PV_RECORD_INDEX)));

            // action anchor
            String anchorText = _ca.getAnchorText();
            Anchor a = new Anchor(WEUtil.esc(anchorText), go);
            a.addElement(WEUtil.esc(anchorText));
            p = new P();
            p.addElement(a);
            card.addElement(p);

            // action do
            Do doact = new Do(DoType.ACCEPT, anchorText);
            doact.addElement(go);
            card.addElement(doact);

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
            onError("", e);
        }
    }
}