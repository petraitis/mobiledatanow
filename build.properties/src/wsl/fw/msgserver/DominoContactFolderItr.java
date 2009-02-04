/**	$Header: /wapsolutions/cvsroot/mdn/fw/src/wsl/fw/msgserver/DominoContactFolderItr.java,v 1.1.1.1 2002/06/11 23:11:42 jonc Exp $
 *
 * 	Converts documents in the folder to ContactDobj
 *
 */
package wsl.fw.msgserver;

import lotus.domino.*;

public class DominoContactFolderItr
	extends DominoFolderItr
{
	private static final String
        CTCT_NAME           = "FullName",
        CTCT_FIRSTNAME      = "FirstName",
        CTCT_SURNAME        = "LastName",
        CTCT_COMPANY		= "CompanyName",
        CTCT_EMAIL	        = "InternetAddress",
        CTCT_BUS_PHONE      = "OfficePhoneNumber",
        CTCT_HOME_PHONE     = "PhoneNumber",
        CTCT_CELL_PHONE     = "CellPhoneNumber",
        CTCT_FAX_PHONE      = "OfficeFAXPhoneNumber",
        CTCT_STREET      	= "StreetAddress",
        CTCT_CITY      		= "City",
        CTCT_PROVINCE      	= "State",
        CTCT_COUNTRY		= "Country";

	public
	DominoContactFolderItr (
	 Database db,
	 View v)
	{
		super (db, v);
	}

	public ItemDobj
	docToItemDobj (
	 Document doc)
	 	throws NotesException
	{
		Contact contact = new Contact (
						"",                     // type
						getDocValue (doc, CTCT_NAME),
						getDocValue (doc, CTCT_FIRSTNAME),
						getDocValue (doc, CTCT_SURNAME),
						getDocValue (doc, CTCT_COMPANY),
						getDocValue (doc, CTCT_EMAIL),
						getDocValue (doc, CTCT_BUS_PHONE),
						getDocValue (doc, CTCT_HOME_PHONE),
						getDocValue (doc, CTCT_CELL_PHONE),
						getDocValue (doc, CTCT_FAX_PHONE),
						getDocValue (doc, CTCT_STREET),
						getDocValue (doc, CTCT_CITY),
						getDocValue (doc, CTCT_PROVINCE),
						getDocValue (doc, CTCT_COUNTRY));

		return new ContactDobj (contact);
	}
}