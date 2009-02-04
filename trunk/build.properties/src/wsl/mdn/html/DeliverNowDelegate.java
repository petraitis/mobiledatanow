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
import wsl.fw.msgserver.*;
import wsl.fw.security.User;
import wsl.mdn.guiconfig.MsgServerAction;
import wsl.mdn.guiconfig.LoginSettings;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:
 * @author
 * @version 1.0
 */

public class DeliverNowDelegate extends MdnHtmlServletDelegate
{

    public DeliverNowDelegate()
    {
    }
    public void run() throws javax.servlet.ServletException, java.io.IOException
    {
        try
        {
            // deliver now
            deliverNow();

            // delegate
            delegate(new MenuDelegate());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Deliver mail now
     */
    private void deliverNow() throws DataSourceException
    {
        // get the userState
        UserState userState = getUserState();
        Util.argCheckNull(userState);

        // get the menu action
        MsgServerAction msa = (MsgServerAction)userState.getCurrentMenu();
        Util.argCheckNull(msa);

        // get the message server
        MessageServer ms = getUserState().getCache().getMessageServer(
            new Integer(msa.getMsgServerId()));
        Util.argCheckNull(ms);

        // create an impl for the message server (hides remote)
        DataSource ds = ms.createImpl();
        Util.argCheckNull(ds);

        // create a deliver now action query and execute
        User user = userState.getUser();
        Util.argCheckNull(user);
        //DoActionQuery q = new DoActionQuery(user.getId(), DoActionQuery.AQ_DELIVER_NOW);
        //ds.select(q);
    }
}