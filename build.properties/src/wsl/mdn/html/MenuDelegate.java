//==============================================================================
// MenuDelegate.java
// Copyright (c) 2001 WAP Solutions Ltd.
//==============================================================================

package wsl.mdn.html;

import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.resource.ResId;
import wsl.fw.wml.WEUtil;
import wsl.fw.html.*;
import wsl.fw.servlet.ServletBase;
import wsl.fw.servlet.ServletError;
import wsl.mdn.dataview.DataView;
import wsl.mdn.guiconfig.MenuAction;
import wsl.mdn.guiconfig.Submenu;
import wsl.mdn.guiconfig.QueryRecords;
import wsl.mdn.guiconfig.TextAction;
import wsl.mdn.guiconfig.NewRecord;
import wsl.mdn.guiconfig.LogoutAction;

import org.apache.ecs.html.*;
import org.apache.ecs.*;

import java.io.IOException;
import javax.servlet.ServletException;
import java.util.Vector;

//------------------------------------------------------------------------------
/**
 * Delegate used by MdnHtmlServlet to display menus.
 */
public class MenuDelegate extends MdnHtmlServletDelegate
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:35:35 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/html/MenuDelegate.java $ ";

    public final static ResId ERR_GET_DATAVIEW =
        new ResId("HtmlMenuDelegate.error.getDataView");
    public final static ResId MENU_TITLE =
        new ResId("HtmlMenuDelegate.text.MenuTitle");

    // resources
    // public static final ResId TEXT_??? = new ResId("MenuDelegate.text.??");


    //--------------------------------------------------------------------------
    // attributes

    private boolean _toggleNode = true;


    //--------------------------------------------------------------------------
    /**
     *
     */
    public MenuDelegate()
    {
        this(true);
    }

    //--------------------------------------------------------------------------
    /**
     * Boolean ctor
     * @param toggleNode, if true toggles current menu node
     */
    public MenuDelegate(boolean toggleNode)
    {
        _toggleNode = toggleNode;
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

            // client cell
            MdnClientCell client = new MdnClientCell();
            //client.setVAlign(AlignType.TOP);
            client.setAlign(AlignType.CENTER);
            client.setNoWrap(true);
            client.setHelpUrl("/help/mdnhelp.html#bMenu");

            // add a heading
            String title = MENU_TITLE.getText();
            client.setClientTitle(title);

            // tree
            MdnMenuTree tree = userState.getMenuTree();
            if(tree == null)
            {
                tree = new MdnMenuTree(_request, _response, userState);
                userState.setMenuTree(tree);
            }
            client.addElement(tree);

            // toggle node if we have a current menuid
            Object menuId = userState.getCurrentMenuActionId();
            if(menuId != null && _toggleNode)
            {
                // find node from id
                WslHtmlTreeNode node = tree.findNode(menuId);
                if(node != null)
                {
                    // toggle the node
                    tree.toggleNode(_request, _response, node);
                }
            }

            // output the client
            outputClientCell(client);
        }
        catch(Exception e)
        {
            onError(e);
        }
    }
}

//==============================================================================
// end of file MenuDelegate.java
//==============================================================================
