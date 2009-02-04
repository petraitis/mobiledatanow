package wsl.mdn.mdnmsgsvr;

import wsl.fw.msgserver.MsExchangeMsgServer;
import wsl.fw.datasource.*;
import wsl.mdn.guiconfig.MenuAction;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c)
 * Company:
 * @author
 * @version 1.0
 */

public class MdnMsExchangeMsgServer extends MsExchangeMsgServer
{
	//--------------------------------------------------------------------------
	// construction

	/**
	 * Default ctor
	 */
    public MdnMsExchangeMsgServer()
    {
    }

    //--------------------------------------------------------------------------
    // cascading delete

    /**
     * Called pre delete call on DataSource
     */
    protected void preDelete() throws DataSourceException
    {
        // must have an id
        if(this.getId() < 0)
            return;

        // select menu actions
        DataSource sysDs = DataManager.getSystemDS();
        Query q = new Query(MenuAction.ENT_MENUACTION);
        q.addQueryCriterium(new QueryCriterium(MenuAction.ENT_MENUACTION,
            MenuAction.FLD_MSGSERVERID, QueryCriterium.OP_EQUALS,
            new Integer(this.getId())));
        RecordSet rs = sysDs.select(q);

        // delete menu actions
        while(rs.next())
            rs.getCurrentObject().delete();

        // super
        super.preDelete();
    }
}