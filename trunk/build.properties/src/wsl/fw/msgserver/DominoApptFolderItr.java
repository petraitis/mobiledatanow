/**	$Header: /wapsolutions/cvsroot/mdn/fw/src/wsl/fw/msgserver/DominoApptFolderItr.java,v 1.1.1.1 2002/06/11 23:11:42 jonc Exp $
 *
 * 	Converts documents in the folder to Appointments
 *
 */
package wsl.fw.msgserver;

import lotus.domino.*;

public class DominoApptFolderItr
	extends DominoFolderItr
{
	public static final String
		APPT_FORM			= "Form",
		APPT_SUBJECT		= "Subject",
		APPT_BODY			= "Body",
		APPT_BEGDATE		= "StartDate",
		APPT_ENDDATE		= "EndDate",
		APPT_LOCATION		= "Location";

	/*
	 *
	 */
	DateTime _day;

	/*
	 *
	 */
	public
	DominoApptFolderItr (
	 Database db,
	 View v,
	 DateTime day)
	{
		super (db, v);
		_day = day;
	}

	public boolean
	isValidDocument (
	 Document doc)
	 	throws NotesException
	{
		String formType = getDocValue (doc, APPT_FORM);
		if (!formType.equals ("Appointment"))
			return false;

		DateTime apptDate = doc.getFirstItem (APPT_BEGDATE).getDateTimeValue ();
		int diff = apptDate.timeDifference (_day);
		if (diff < 0 ||				// before time
			diff > 24 * 60 * 60)	// one day difference
		{
			return false;
		}
		return true;
	}

	public ItemDobj
	docToItemDobj (
	 Document doc)
	 	throws NotesException
	{
		/*
		 *	Create the appointment for display
		 */
		Appointment appt = new Appointment (
						getDocValue (doc, APPT_FORM),
						getDocValue (doc, APPT_SUBJECT),
						getDocValue (doc, APPT_BODY),
						getDocValue (doc, APPT_BEGDATE),
						getDocValue (doc, APPT_ENDDATE),
						getDocValue (doc, APPT_LOCATION));

		return new AppointmentDobj (appt);
	}
}