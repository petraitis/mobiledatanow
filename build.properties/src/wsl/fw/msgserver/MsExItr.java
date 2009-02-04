/**	$Header: /wapsolutions/cvsroot/mdn/fw/src/wsl/fw/msgserver/MsExItr.java,v 1.1.1.1 2002/06/11 23:11:42 jonc Exp $
 *
 *  Iterators for a MS Exchange Folder
 *
 *  We need to uniquely identify them for the Native method
 *
 */
package wsl.fw.msgserver;

import wsl.fw.datasource.RecordItr;

public abstract class MsExItr
	extends RecordItr
{
	private static int _nextId = 0;

	private int _id;
	private String _loginUrl;

	public
	MsExItr (
	 String loginUrl)
	{
		_id = getNextId ();
		_loginUrl = loginUrl;
	}

	public int
	getId ()
	{
		return _id;
	}

	public String
	getLoginUrl ()
	{
		return _loginUrl;
	}

	private static synchronized int
	getNextId ()
	{
		return _nextId++;
	}
}