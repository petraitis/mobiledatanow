/**	$Header: /wapsolutions/cvsroot/mdn/fw/src/wsl/fw/msgserver/ImapSubfolderItr.java,v 1.1.1.1 2002/06/11 23:11:42 jonc Exp $
 *
 *	Iterator for IMAP folders
 *
 */
package wsl.fw.msgserver;

import javax.mail.Folder;
import javax.mail.internet.InternetAddress;

import wsl.fw.datasource.RecordItr;

import javax.mail.MessagingException;

public class ImapSubfolderItr
	extends RecordItr
{
	/**
	 *	Iterator state
	 */
	javax.mail.Folder _subfolders [];
	int _posn;

	/**
	 *	Constructor
	 */
	ImapSubfolderItr (
	 javax.mail.Folder folder)
	 	throws MessagingException
	{
		_subfolders = folder.list ();
		_posn = 0;
	}

	/**
	 *	Required class method
	 */
	public boolean
	hasNext ()
	{
		return _posn < _subfolders.length;
	}

	public Object
	next ()
	{
		FolderDobj fObj = null;

		try
		{
			fObj = new FolderDobj (
								_subfolders [_posn].getName (),
								_subfolders [_posn].getURLName ().toString (),
								wsl.fw.msgserver.Folder.FCT_MIXED,
								ActionDobj.AT_FOLDER);

		} catch (MessagingException e)
		{
			fObj = new FolderDobj (
								"**Internal Error**",
								"**Internal Error**",
								wsl.fw.msgserver.Folder.FCT_MIXED,
								ActionDobj.AT_DEFAULT_INBOX);
		}

		_posn++;
		return fObj;
	}
}