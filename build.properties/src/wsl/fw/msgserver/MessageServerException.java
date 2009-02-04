/*	$Header: /wapsolutions/cvsroot/mdn/fw/src/wsl/fw/msgserver/MessageServerException.java,v 1.1.1.1 2002/06/11 23:11:42 jonc Exp $
 *
 *	Exception class for MessageServers
 *
 */
package wsl.fw.msgserver;

import wsl.fw.datasource.DataSourceException;

public class MessageServerException
	extends DataSourceException
{
	long _code;			// numeric error code

	public
	MessageServerException (
	 long code,
	 String msg)
	{
		super (msg);
		_code = code;
	}

	public
	MessageServerException (
	 String msg)
	{
		this (0, msg);
	}

	public long
	getCode ()
	{
		return _code;
	}
}