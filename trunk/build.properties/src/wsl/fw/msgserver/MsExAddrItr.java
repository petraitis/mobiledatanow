/**	$Header: /wapsolutions/cvsroot/mdn/fw/src/wsl/fw/msgserver/MsExAddrItr.java,v 1.1.1.1 2002/06/11 23:11:42 jonc Exp $
 *
 * 	Iterator for MS Exchange Address Lists
 *
 * 	Can't really base on MsExMessageItr 'cos CDO places these type of
 *  messages as AddressEntry instead of a Message subclass
 *
 */
package wsl.fw.msgserver;

public class MsExAddrItr
	extends MsExItr
{
	public
	MsExAddrItr (
	 String loginUrl,
	 String addrId)
	 	throws MessageServerException
	{
		super (loginUrl);
		MSExchangeInterface.createAddrIterator (
			getLoginUrl (), getId (), addrId);
	}

	public boolean
	hasNext ()
	{
		try
		{
			return MSExchangeInterface.hasNextAddr (getLoginUrl (), getId ());

		} catch (MessageServerException e)
		{
			System.err.println ("ERR=" + e.getMessage ());
			e.printStackTrace ();
		}
		return false;
	}

	/**
	 *	Class requirement
	 *  Expect to return a ContactDobj
	 */
	public Object
	next ()
	{
		String errMsg = "";

		try
		{
			Contact contact = MSExchangeInterface.getNextAddr (
								getLoginUrl (), getId ());

			if (contact != null)
				return new ContactDobj (contact);

			errMsg = "MSExchangeInterface unknown failure";

		} catch (MessageServerException e)
		{
			System.err.println ("ERR=" + e.getMessage ());
			e.printStackTrace ();

			errMsg = e.getMessage ();
		}

		String empty = "";
		return new ContactDobj (
				new Contact (
						"Error",				// type
						"**Internal Error**",	// name
						errMsg,					// firstname
						empty,					// surname
						empty,					// company
						empty,					// email
						empty,					// busTel
						empty,					// homeTel,
						empty,					// mobTel,
						empty,					// busFax,
						empty,					// busStreet,
						empty,					// busCity,
						empty,					// busProvince,
						empty));				// busCountry)
	}
}