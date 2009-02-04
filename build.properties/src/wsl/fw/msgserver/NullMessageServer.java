/*	$Header: /wapsolutions/cvsroot/mdn/fw/src/wsl/fw/msgserver/NullMessageServer.java,v 1.3 2002/09/26 04:45:10 tecris Exp $
 *
 *	A Message Server that does nothing.
 *	Useful for polymorphic reloads.
 */
package wsl.fw.msgserver;

import java.util.Iterator;

import wsl.fw.datasource.RecordItrRef;
import wsl.fw.datasource.RecordSet;
import wsl.fw.datasource.DataSourceException;

public class NullMessageServer
	extends MessageServer
{
	public
	NullMessageServer ()
	{
		super ();
	}

	public boolean
	iHasNextObj (
	 RecordItrRef ref)
	 	throws DataSourceException
	{
		throw new DataSourceException ("NullMessage Server action");
	}

	public Object
	iNextObj (
	 RecordItrRef ref)
	 	throws DataSourceException
	{
		throw new DataSourceException ("NullMessage Server action");
	}

	/**
	 *  Class requirment. Handle the action.
	 */
	public RecordItrRef
	doActionQuery (
	 ActionQuery q)
	 	throws DataSourceException
	{
		throw new DataSourceException ("NullMessage Server action");
	}

	/**
	 *  Class requirement. Spool a message to be sent off
	 */
	public boolean
	insertMailMessage (
	 SessionMailMsgDobj m)
	 	throws DataSourceException
	{
		throw new DataSourceException ( "NullMessage Server action");
	}
	/**
	 * Insert (forward) a mail message
	 * @param mm the mail message to forward
	 * @return true if message sent successfully
	 */
	protected boolean
	insertFwMailMessage (
	 SessionFwMailMsgDobj fwd)
		throws DataSourceException
	{
		throw new DataSourceException ( "NullMessage Server action");
	}

	public int execInsertOrUpdate(String sql) throws DataSourceException {
		throw new DataSourceException ( "NullMessage Server action");
	}
	
	
}
