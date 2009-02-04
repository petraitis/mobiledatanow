/**	$Header: /wapsolutions/cvsroot/mdn/mdn/src/wsl/mdn/mdnmsgsvr/MdnImapSmtpMsgServer.java,v 1.1.1.1 2002/06/11 23:35:35 jonc Exp $
 *
 */
package wsl.mdn.mdnmsgsvr;

import wsl.fw.datasource.*;
import wsl.fw.msgserver.ImapSmtpMsgServer;
import wsl.mdn.guiconfig.MenuAction;

public class MdnImapSmtpMsgServer
	extends ImapSmtpMsgServer
{
	//--------------------------------------------------------------------------
	// construction

	/**
	 * Default ctor
	 */
    public MdnImapSmtpMsgServer()
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