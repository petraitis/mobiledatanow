/*	$Id: FolderDobj.java,v 1.3 2002/07/22 23:47:06 jonc Exp $
 *
 *	Folders
 *
 */
package wsl.fw.msgserver;

import java.util.Date;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Vector;
import wsl.fw.datasource.*;
import wsl.fw.util.Log;
import java.text.SimpleDateFormat;

public class FolderDobj
	extends ActionDobj
{
	//--------------------------------------------------------------------------
	// attributes

	private String _contentType = Folder.FCT_MIXED;
	private static SimpleDateFormat _df;
	{
		_df = new SimpleDateFormat ("EEE MMM d yyyy");
	}

	//--------------------------------------------------------------------------
	// construction

	/**
	 * Name ctor
	 * @param name the name of the folder
	 * @param folderType the type of folder
	 */
	public
	FolderDobj (
	 Folder f)
	{
		// delegate
		this (f._name, f._id, f._contentType, AT_FOLDER);

		// init the action type
		setActionType (buildActionType (f));
	}

	/**
	 * Ctor taking a name, action type and content type
	 * @param name
	 * @param contentType
	 * @param actionType
	 */
	public
	FolderDobj (
	 String name,
	 String contentType,
	 int actionType)
	{
		// delegate
		this (name, "1000", contentType, actionType);
	}

	/**
	 * Ctor taking a name, id, action type and content type
	 * @param name
	 * @param id
	 * @param contentType
	 * @param actionType
	 */
	public
	FolderDobj (
	 String name,
	 String id,
	 String contentType,
	 int actionType)
	{
		// super
		super (actionType, name, id);

		// attribs
		_contentType = contentType;
	}

	/**
	 * Build the action type from the Folder
	 * @param f Folder
	 * @return the action type constant
	 */
	private int
	buildActionType (
	 Folder f)
	{
		// default is generic folder
		int at = AT_FOLDER;

		// is it a default folder
		if (f._name.equals (Folder.DFN_CALENDAR))
			at = this.AT_DEFAULT_CALENDAR;
		else if (f._name.equals (Folder.DFN_CONTACTS))
			at = this.AT_DEFAULT_CONTACTS;
		else if (f._name.equals (Folder.DFN_INBOX))
			at = this.AT_DEFAULT_INBOX;

		// return
		return at;
	}


	//--------------------------------------------------------------------------
	// accessors

	/**
	 * @return the content type
	 */
	public String
	getContentType ()
	{
		return _contentType;
	}


	//--------------------------------------------------------------------------
	// Action interface

	/**
	 * Execute the action
	 * @param ms the MessageServer to execute the action on
	 * @return the resulting RecordSet
	 */
	public RecordItrClient
	doActionQuery (
	  DataSource ms,
	  String sessionId,
	  Object userId)
		throws DataSourceException
	{
		// calendar
		Vector v = new Vector ();

		switch (getActionType ())
		{
		case FolderDobj.AT_DEFAULT_CALENDAR:
			// build custom folders
			v.add (new FolderDobj ("Today", getId (),
					Folder.FCT_CALENDAR, FolderDobj.AT_CAL_TODAY));
			v.add (new FolderDobj ("Tomorrow", getId (),
					Folder.FCT_CALENDAR, FolderDobj.AT_CAL_TOMORROW));
			v.add (new FolderDobj ("This Week", getId (),
					Folder.FCT_CALENDAR, FolderDobj.AT_CAL_THIS_WEEK));
			v.add (new FolderDobj ("Next Week", getId (),
					Folder.FCT_CALENDAR, FolderDobj.AT_CAL_NEXT_WEEK));
			break;

		case FolderDobj.AT_CAL_THIS_WEEK:
		case FolderDobj.AT_CAL_NEXT_WEEK:
			// get todays date
			Date today = new Date ();
			Calendar cal = Calendar.getInstance ();
			cal.setTime (today);

			/*
			 *	Advance to next week if required
			 */
			if (getActionType () == FolderDobj.AT_CAL_NEXT_WEEK)
				cal.add (Calendar.DATE, 7);

			/*
			 *	Since we want the first presented
			 *	day to be Monday, we work out day adjustments required.
			 */
			int mondayOffset = 0;
			switch (cal.get (Calendar.DAY_OF_WEEK))
			{
			case Calendar.TUESDAY:
				mondayOffset = -1;
				break;
			case Calendar.WEDNESDAY:
				mondayOffset = -2;
				break;
			case Calendar.THURSDAY:
				mondayOffset = -3;
				break;
			case Calendar.FRIDAY:
				mondayOffset = -4;
				break;
			case Calendar.SATURDAY:
				mondayOffset = -5;
				break;
			case Calendar.SUNDAY:
				mondayOffset = -6;
				break;
			}
			if (mondayOffset < 0)
				cal.add (Calendar.DATE, mondayOffset);

			/*
			 *	Show 7 days worth
			 */
			for (int i = 0; i < 7; i++)
			{
				// add the actiondobj
				v.add (new FolderDobj (
						dateToString (cal.getTime ()), getId (),
						Folder.FCT_CALENDAR, FolderDobj.AT_CAL_DATE));

				// advance the date
				cal.add (Calendar.DATE, 1);
			}
			break;

		default:
			return super.doActionQuery (ms, sessionId, userId);
		}

		/*
		 *	Return a local client-side RecordIterator. The contents
		 *	of the Vector are built on the client, and doesn't reference
		 *	the (remote) DataSource
		 */
		return new RecordItrLocalClient (new RecordVectorItr (v));
	}

	//--------------------------------------------------------------------------
	// date parsing

	/**
	 * convert date to string
	 * @param d the Date to parse
	 * @return the string representation of the date
	 */
	public synchronized static String
	dateToString (
	 Date d)
	{
		return _df.format (d);
	}

	/**
	 * convert string to date
	 * @param s the String to parse
	 * @return the Date representation of the string
	 */
	public synchronized static Date
	stringToDate (
	 String s)
	{
		try
		{
			return _df.parse (s);
		}
		catch (Exception e)
		{
			Log.error ("Cant parse date: " + s);
			return null;
		}
	}
}
