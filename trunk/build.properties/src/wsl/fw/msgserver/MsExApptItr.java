/**	$Header
 *
 *  Iterator for MS Exchange Appointments
 *
 */
package wsl.fw.msgserver;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MsExApptItr
	extends MsExMessageItr
{
	private static SimpleDateFormat df = new SimpleDateFormat ("yyyyMMdd");

	/*
	 *
	 */
	public
	MsExApptItr (
	 String loginUrl,
	 Date date)
	 	throws MessageServerException
	{
		super (loginUrl);
		MSExchangeInterface.createApptMsgIterator (
			getLoginUrl (),
			df.format (date),
			getId ());
	}
}