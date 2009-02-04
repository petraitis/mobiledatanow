/**	$Header: /wapsolutions/cvsroot/mdn/mdn/src/wsl/mdn/wap/CriteriaActionDelegate.java,v 1.1.1.1 2002/06/11 23:35:35 jonc Exp $
 *
 */
package wsl.mdn.wap;

// imports
import java.util.Iterator;
import java.io.IOException;
import javax.servlet.ServletException;

import org.apache.ecs.wml.*;
import wsl.fw.servlet.*;
import wsl.fw.util.Util;
import wsl.fw.resource.ResId;
import wsl.fw.wml.*;
import wsl.fw.msgserver.*;
import wsl.fw.datasource.*;
import wsl.fw.security.User;
import wsl.fw.util.Log;
import wsl.mdn.guiconfig.MsgServerAction;

public class CriteriaActionDelegate extends MdnWmlServletDelegate
{
    //--------------------------------------------------------------------------
    // construction

    /**
     * Default ctor
     */
    public CriteriaActionDelegate()
    {
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

            // get the user
            User user = userState.getUser();
            Util.argCheckNull(user);

            // get the criteria value
            String value = _request.getParameter(GetCriteriaDelegate.RP_CRITERIA);
            value = (value == null)? "": value;

            // get the record and index
            String strIndex = _request.getParameter(MdnWmlServlet.PV_RECORD_INDEX);
            if(strIndex != null && strIndex.length() > 0)
            {
                int index = Integer.parseInt(strIndex);

				PagedItrMsgDelegate pmd =
					(PagedItrMsgDelegate) userState.getCurrentPagedItDelegate ();
                CriteriaAction ca = (CriteriaAction) pmd.getMessage (index);
                Util.argCheckNull(ca);

				/*
				 *	Get an iterator to the innards of the PagedItMsgDelegate.
				 */
                Iterator it = pmd.iterator ();

				// if there's a filter, tag it on
				if (value.length () > 0)
					it = new FilteredIterator (it, ca.getCriteriumClass (), value);

                // create a new PagedMessageDelegate and set it into the UserState
                pmd = new PagedItrMsgDelegate (it);
                userState.setPagedItDelegate (pmd);

                // delegate to PagedMessageDelegate to display the list
                delegate(pmd);
            }
        }
        catch(Exception e)
        {
            onError("", e);
        }
    }
}