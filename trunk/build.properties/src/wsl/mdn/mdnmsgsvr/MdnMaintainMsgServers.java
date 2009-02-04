/**	$Header: /wapsolutions/cvsroot/mdn/mdn/src/wsl/mdn/mdnmsgsvr/MdnMaintainMsgServers.java,v 1.1.1.1 2002/06/11 23:35:35 jonc Exp $
 *
 *
 */
package wsl.mdn.mdnmsgsvr;

import wsl.fw.msgserver.DominoMsgServer;
import wsl.fw.msgserver.ImapSmtpMsgServer;
import wsl.fw.msgserver.MaintainMsgServersPanel;
import wsl.fw.msgserver.MsExchangeMsgServer;

public class MdnMaintainMsgServers
	extends MaintainMsgServersPanel
{
    //--------------------------------------------------------------------------
    // construction

    public MdnMaintainMsgServers()
    {
    }


    //--------------------------------------------------------------------------
    // override for new message server create

    /**
     * @return the MsExchangeMsgServer(or subclass) object
     */
    protected MsExchangeMsgServer createMsExchangeMsgServer()
    {
        return new MdnMsExchangeMsgServer();
    }


    /**
     * @return the DominoMsgServer(or subclass) object
     */
    protected DominoMsgServer createDominoMsgServer()
    {
        return new MdnDominoMsgServer();
    }

    protected ImapSmtpMsgServer
	createImapSmtpMsgServer ()
    {
        return new MdnImapSmtpMsgServer();
	}
}