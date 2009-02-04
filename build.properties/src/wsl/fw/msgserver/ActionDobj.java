/*	$Id: ActionDobj.java,v 1.5 2002/07/22 23:47:06 jonc Exp $
 *
 * Implementation superclass for all messagedobjs that initiate an action
 * in the message server
 *
 */
package wsl.fw.msgserver;

import java.util.Iterator;
import wsl.fw.datasource.DataSourceException;
import wsl.fw.datasource.RecordItrClient;
import wsl.fw.datasource.RecordItrDSClient;
import wsl.fw.datasource.RecordSet;
import wsl.fw.datasource.DataSource;

public class ActionDobj
	extends MessageDobj
{
	//--------------------------------------------------------------------------
	// constants

	// just actions
	public static final int
		AT_NONE				= 0,
		AT_LOGOUT			= 1;

	// folder actions
	public static final int
		AT_FOLDER			= 1001,
		AT_MAIL_ALL			= 1002,
		AT_MAIL_UNREAD		= 1003,
		AT_ROOT_FOLDER		= 1004,
		AT_CAL_TODAY		= 1005,
		AT_CAL_TOMORROW		= 1006,
		AT_CAL_THIS_WEEK	= 1007,
		AT_CAL_NEXT_WEEK	= 1008,
		AT_ADDRESS_LIST		= 1009,
		AT_PRIVATE_CONTACTS	= 1010,
		AT_DEFAULT_INBOX	= 1012,
		AT_DEFAULT_CONTACTS	= 1013,
		AT_DEFAULT_CALENDAR	= 1014,
		AT_CAL_DATE			= 1015,
		AT_TASK_LIST		= 1016;

	// crtierium actions
	public static final int
		AT_SEARCH_BY_NAME	= 2000;

	//--------------------------------------------------------------------------
	// attributes

	private int _actionType = AT_NONE;
	private String _name = "";
	private String _id = "";

	//--------------------------------------------------------------------------
	// construction

	/**
	 * Default ctor
	 */
	public
	ActionDobj ()
	{
	}

	/**
	 * Action type ctor
	 * @param actionType the action type
	 */
	public
	ActionDobj (
	 int actionType,
	 String name,
	 String id)
	{
		setActionType (actionType);
		setName (name);
		setId (id);
	}

	//--------------------------------------------------------------------------
	// accessors
	/**
	 * Set the action type
	 * @param actionType
	 */
	public void
	setActionType (
	 int actionType)
	{
		_actionType = actionType;
	}

	/**
	 * @return the action type
	 */
	public int
	getActionType ()
	{
		return _actionType;
	}

	/**
	 * Returns the folder name
	 * @return String
	 */
	public String
	getName ()
	{
		return _name;
	}

	/**
	 * Sets the folder name
	 * @param name
	 * @return void
	 */
	public void
	setName (
	 String name)
	{
		_name = name;
	}

	/**
	 * Returns the folder id
	 * @return String
	 */
	public String
	getId ()
	{
		return _id;
	}


	/**
	 * Sets the folder id
	 * @param id
	 * @return void
	 */
	public void
	setId (
	 String id)
	{
		_id = id;
	}

	//--------------------------------------------------------------------------
	// to string

	public String
	toString ()
	{
		return getName ();
	}

	//--------------------------------------------------------------------------
	// action interface

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
		return new RecordItrDSClient (
					ms,
					ms.iSelect (new ActionQuery (sessionId, userId, this)));
	}
}
