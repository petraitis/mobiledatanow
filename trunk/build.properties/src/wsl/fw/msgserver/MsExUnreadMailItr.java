/**	$Header: /wapsolutions/cvsroot/mdn/fw/src/wsl/fw/msgserver/MsExUnreadMailItr.java,v 1.2 2003/01/15 23:42:46 tecris Exp $
 *
 * 	Filter to extract unread mail
 *
 */
package wsl.fw.msgserver;

import wsl.fw.util.Config;

public class MsExUnreadMailItr
	extends MsExMessageItr
{
	MsExMessageItr _itr;
	MailMessageDobj _curObj;
        int index = 150;
        int count = 0;
        int noLimit = -1;

	public final static String
          MSEXCHANGE_CONFIG_FILE = "resource://wsl/config/mdn/MsExchange.conf",
          DEFAULT_LIMIT           = "150",
          SEARCH_LIMIT            = "MsExchange.UnreadSearchLimit";

	public
	MsExUnreadMailItr (
	 String loginUrl,
	 String folderId)
	 	throws MessageServerException
	{
		super (loginUrl, folderId);
		_curObj = null;
		skip ();
		Config.setSingleton (MSEXCHANGE_CONFIG_FILE, false);
                try
                {
                  index = Integer.parseInt(Config.getProp (SEARCH_LIMIT, DEFAULT_LIMIT));
                }
                catch (NumberFormatException nfe)
                {
                  index = 150;
                }
	}

	/**
	 *	Skip to next valid object
	 */
	private void
	skip ()
	{
		while (super.hasNext ())
		{
			Object obj = super.next ();
			if (obj instanceof MailMessageDobj)
			{
				MailMessageDobj mObj = (MailMessageDobj) obj;
                                if (count!=noLimit)
                                {
                                  count++;
                                  if (count==index)
                                  {
                                    _curObj = null;
                                    return;
                                  }
                                }
				if (mObj.isUnread ())
				{
					_curObj = mObj;
					return;
				}
			}
		}
		_curObj = null;
	}

	public boolean
	hasNext ()
	{
		return _curObj != null;
	}

	public Object
	next ()
	{
		Object ret = _curObj;
		if (_curObj != null)
			skip ();
		return ret;
	}
}