//==============================================================================
// MenuDelegate.java
// Copyright (c) 2001 WAP Solutions Ltd.
//==============================================================================

package wsl.mdn.wap;

import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.resource.ResId;
import wsl.fw.wml.WEUtil;
import wsl.fw.servlet.ServletBase;
import wsl.fw.servlet.ServletError;
import wsl.mdn.dataview.DataView;
import wsl.mdn.guiconfig.MenuAction;
import wsl.mdn.guiconfig.Submenu;
import wsl.mdn.guiconfig.QueryRecords;
import wsl.mdn.guiconfig.TextAction;
import wsl.mdn.guiconfig.NewRecord;
import wsl.mdn.guiconfig.LogoutAction;
import wsl.mdn.guiconfig.MsgServerAction;

import org.apache.ecs.wml.*;

import java.io.IOException;
import javax.servlet.ServletException;
import java.util.Vector;

//------------------------------------------------------------------------------
/**
 * Delegate used by MdnWmlServlet to display menus.
 */
public class MenuDelegate extends MdnWmlServletDelegate
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:35:35 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/wap/MenuDelegate.java $ ";

    public final static ResId ERR_GET_DATAVIEW = new ResId("MenuDelegate.error.getDataView");

    // resources
    // public static final ResId TEXT_??? = new ResId("MenuDelegate.text.??");

    //--------------------------------------------------------------------------
    /**
     *
     */
    public MenuDelegate()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Handle actions for the LoginDelegate.
     * @throws IOException, standard exception thrown by servlet methods.
     * @throws ServletException, standard exception thrown by servlet methods.
     */
    public void run() throws IOException, ServletException
    {
        try
        {
            Util.argCheckNull(_request);
            Util.argCheckNull(_response);

            // get the userState
            UserState userState = getUserState();

            // create the card for the current menu
            Card card = makeMenuCard(userState.getCurrentMenu());
            // ret immediately on fail (null) as makeMenuCard has laready generated
            // an error page
            if (card == null)
                return;

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

    //--------------------------------------------------------------------------
    // make menu href

    /**
     * Make a menu href and p
     * @param action
     * @param subAction
     * @param text
     * @param groupId
     * @return P
     */
    private P makeMenuHrefP(String action, String subAction, String text, String maid)
    {
        // create href
        String href = makeHref(action, subAction);

        // add groupId
        if(maid != null && maid.length() > 0)
            href += WEUtil.esc("&" + MdnWmlServlet.PV_MENUACTIONID + "=" + maid);

        // create p
        return WEUtil.makeHrefP(href, text);
    }

    //--------------------------------------------------------------------------
    /**
     * Make a card which displays the specified menu.
     * @param currentMenu, the menu to display on the card
     */
    private Card makeMenuCard(MenuAction currentMenu)
        throws ServletException, IOException
    {
        // create card
        Card card = new Card();
        card.setTitle(WEUtil.esc(currentMenu.getName()));

        // iterate over menu elements adding them to the menu card
        Vector menuItems = currentMenu.getChildren();
        for (int i = 0; i < menuItems.size(); i++)
        {
            MenuAction item = (MenuAction) menuItems.get(i);
            String maid = (item.getId() == null)? "-1": item.getId().toString();
            int groupId = item.getIntGroupId();

            // validate the dvid
            Integer objDvId = new Integer(item.getIntDataViewId());
            if(objDvId != null && objDvId.intValue() > 0)
            {
                try
                {
                    DataView dv = getUserState().getCache().getDataView(objDvId);
                    if(groupId >= 0 && dv != null && !dv.isGroupEnabled(groupId))
                        continue;
                }
                catch(Exception e)
                {
                    // failed, error and ret null to flag error
                    getServlet().onError(_request, _response,
                        new ServletError(ERR_GET_DATAVIEW.getText(), e));
                    return null;
                }
            }

            if (item instanceof Submenu)
            {
                // submenu
                // submenu + menu id
                Submenu suItem = (Submenu) item;
                card.addElement(makeMenuHrefP(
                    MdnWmlServlet.ACT_MENU,
                    suItem.getId().toString(),
                    WEUtil.esc(suItem.getName()),
                    maid));
            }
            else if (item instanceof QueryRecords)
            {
                // Query
                // query + query id
                QueryRecords qrItem = (QueryRecords) item;
                card.addElement(makeMenuHrefP(
                    MdnWmlServlet.ACT_QUERYRECORDS,
                    qrItem.getQueryId().toString(),
                    WEUtil.esc(qrItem.getName()),
                    maid));
            }
            else if (item instanceof NewRecord)
            {
                // new record
                // new record + DV id
                NewRecord nrItem = (NewRecord) item;
                card.addElement(makeMenuHrefP(
                    MdnWmlServlet.ACT_NEWRECORD,
                    nrItem.getDataViewId().toString(),
                    WEUtil.esc(nrItem.getName()),
                    maid));
            }
            else if (item instanceof TextAction)
            {
                // text
                TextAction taItem = (TextAction) item;
                P p = new P();
                // add text or link depending on type
                if (taItem.hasPhoneLink())
                    p.addElement(WEUtil.makePhoneLink(WEUtil.esc(taItem.getPhoneLink()),
                        WEUtil.esc(taItem.getName())));
                else if (taItem.hasUrlLink())
                {
                    A link = new A(WEUtil.esc(taItem.getUrlLink()));
                    link.addElement(WEUtil.esc(taItem.getName()));
                    p.addElement(link);
                }
                else
                    p.addElement(WEUtil.esc(taItem.getName()));

                card.addElement(p);
            }
            else if (item instanceof MsgServerAction)
            {
                // message server action
                MsgServerAction msa = (MsgServerAction)item;
                card.addElement(makeMenuHrefP(
                    MdnWmlServlet.ACT_MSGSERVER,
                    String.valueOf(msa.getMsgServerId()),
                    WEUtil.esc(msa.getName()),
                    maid));
            }
            else if (item instanceof LogoutAction)
            {
                // logout
                card.addElement(WEUtil.makeHrefP(makeHref(MdnWmlServlet.ACT_LOGOUT),
                    MdnWmlServlet.TEXT_LOGOUT.getText()));
            }
        }

        return card;
    }
}

//==============================================================================
// end of file MenuDelegate.java
//==============================================================================
