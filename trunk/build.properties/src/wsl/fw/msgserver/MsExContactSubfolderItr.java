/**	$Header: /wapsolutions/cvsroot/mdn/fw/src/wsl/fw/msgserver/MsExContactSubfolderItr.java,v 1.1.1.1 2002/06/11 23:11:42 jonc Exp $
 *
 *	Iterator to browse Contact subfolders (if any)
 *
 */
package wsl.fw.msgserver;

public class MsExContactSubfolderItr
	extends MsExSubfolderItr
{
	public
	MsExContactSubfolderItr (
	 String loginUrl)
	 	throws MessageServerException
	{
		super (loginUrl);
		MSExchangeInterface.createContactSubfolderIterator (
			getLoginUrl (), getId ());
	}
}