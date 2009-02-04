/**	$Header: /wapsolutions/cvsroot/mdn/fw/src/wsl/fw/datasource/RecordItrDSClient.java,v 1.1.1.1 2002/06/11 23:11:42 jonc Exp $
 *
 *	Client side for RecordItr using a remote DataSource
 *
 */
package wsl.fw.datasource;

public class RecordItrDSClient
	extends RecordItrClient
{
	private DataSource _ds;
	private RecordItrRef _ref;

	public
	RecordItrDSClient (
	 DataSource ds,
	 RecordItrRef ref)
	{
		_ds = ds;
		_ref = ref;
	}

	public boolean
	hasNext ()
	{
		try
		{
			return _ds.iHasNextObj (_ref);

		} catch (DataSourceException e)
		{
			System.err.println ("err=" + e.getMessage ());
			e.printStackTrace ();
		}
		return false;
	}

	public Object
	next ()
	{
		try
		{
			return _ds.iNextObj (_ref);

		} catch (DataSourceException e)
		{
			System.err.println ("err=" + e.getMessage ());
			e.printStackTrace ();
		}
		return null;
	}
}