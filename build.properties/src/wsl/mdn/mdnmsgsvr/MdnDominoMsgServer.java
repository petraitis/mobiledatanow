package wsl.mdn.mdnmsgsvr;

import wsl.fw.datasource.*;
import wsl.fw.msgserver.DominoMsgServer;
import wsl.mdn.guiconfig.MenuAction;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c)
 * Company:
 * @author
 * @version 1.0
 */

public class MdnDominoMsgServer extends DominoMsgServer
{
	//--------------------------------------------------------------------------
	// construction

	/**
	 * Default ctor
	 */
    public MdnDominoMsgServer()
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