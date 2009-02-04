/**	$Header: /wapsolutions/cvsroot/mdn/fw/src/wsl/fw/msgserver/MsExSubfolderItr.java,v 1.1.1.1 2002/06/11 23:11:42 jonc Exp $
 *
 *  Subfolder Iterator for a MS Exchange Folder
 *
 */
package wsl.fw.msgserver;

import wsl.fw.datasource.RecordItr;

public class MsExSubfolderItr
	extends MsExItr
{
	public
	MsExSubfolderItr (
	 String loginUrl,
	 String folderId)
	 	throws MessageServerException
	{
		super (loginUrl);
		MSExchangeInterface.createSubfolderIterator (
			getLoginUrl (), getId (), folderId);
	}

	/**
	 *	Constructor for sub-classes
	 */
	protected
	MsExSubfolderItr (
	 String loginUrl)
	{
		super (loginUrl);
	}

	public boolean
	hasNext ()
	{
		try
		{
			return MSExchangeInterface.hasNextSubfolder (
						getLoginUrl (), getId ());

		} catch (MessageServerException e)
		{
			System.err.println ("ERR=" + e.getMessage ());
			e.printStackTrace ();
		}
		return false;
	}

	/**
	 *  Class requirement.
	 *  The return value should be a FolderDobj
	 */
	public Object
	next ()
	{
		String errMsg = "";

		try
		{
			Folder folder = MSExchangeInterface.getNextSubfolder (
								getLoginUrl (), getId ());

			if (folder != null)
				return new FolderDobj (folder);		//	Convert to FolderDObj

		} catch (MessageServerException e)
		{
			System.err.println ("ERR=" + e.getMessage ());
			e.printStackTrace ();
			errMsg = e.getMessage ();
		}

		return new FolderDobj (new Folder ("**Internal Error** " + errMsg, ""));
	}
}