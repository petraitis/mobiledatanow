//==============================================================================
// NewRecordDelegate.java
// Copyright (c) 2001 WAP Solutions Ltd.
//==============================================================================

package wsl.mdn.wap;

import wsl.fw.servlet.ServletBase;
import wsl.fw.util.Util;
import wsl.fw.resource.ResId;
import wsl.fw.wml.WEUtil;
import wsl.mdn.guiconfig.*;
import wsl.mdn.dataview.*;
import org.apache.ecs.wml.*;

import java.io.IOException;
import javax.servlet.ServletException;

//------------------------------------------------------------------------------
/**
 * Delegate used by MdnWmlServlet to ??.
 */
public class NewRecordDelegate extends MdnWmlServletDelegate
{
    // version tag
    private final static String _ident = "$Date: 2004/01/06 02:12:12 $  $Revision: 1.2 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/wap/NewRecordDelegate.java $ ";

    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public NewRecordDelegate()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Handle actions for the ??Delegate.
     * @throws IOException, standard exception thrown by servlet methods.
     * @throws ServletException, standard exception thrown by servlet methods.
     */
    public void run() throws IOException, ServletException
    {
        try
        {
            // get the menu action
            UserState us = getUserState();
            MenuAction ma = us.getCurrentMenu();

            // validate
            Util.argCheckNull(us);
            Util.argCheckNull(ma);
            assert ma instanceof NewRecord;

            DataView dv = us.getCache().getDataView(ma.getDataViewId());
            if(dv != null)
            {
                // create a record for the dv
                Record rec = new Record(dv);
                if(rec != null)
                {
                    // set the record into the user state
                    us.setNewRecord(rec);

                    // delegate to show record
                    delegate(new EditRecordDelegate(rec, "-1"));
                }
            }
        }
        catch(Exception e)
        {
           onError(e);
        }
    }
}

//==============================================================================
// end of file NewRecordDelegate.java
//==============================================================================
