/**	$Id: JDataStoreNativeDelegate.java,v 1.3 2004/01/06 01:43:16 tecris Exp $
 *
 * JDataStore native delegate.
 *
 */
package wsl.fw.datasource.dbdelegate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import wsl.fw.datasource.Field;
import wsl.fw.util.Util;
import wsl.fw.util.Type;

import java.sql.SQLException;
import wsl.fw.util.TypeConversionException;

public class JDataStoreNativeDelegate
	extends NativeDelegate
{
	public static final String NAME = "DataStore";

	/**
	 * Get the value from the resultset based on field class
	 * @param strClass class of column
	 * @param ResultSet the resultset to get the value from
	 * @param inde the index of the column in the resultset
	 * @return Object the value
	 */
	public Object
	getResultSetValue (
	 String strClass,
	 ResultSet rs,
	 int rsIndex)
		throws SQLException
	{
		Object val = rs.getObject (rsIndex);
		if (val != null)
		{
			if (val instanceof java.sql.Date)
				val = new java.sql.Date (((java.sql.Date) val).getTime ());
		}
		return val;
	}

	/**
	 * Set a prepared param into a prepared statement
	 * @param stmt the prepared statement to put the value into
	 * @param f the Field containing the type
	 * @param val the converted value to set
	 * @param paramIndex the index of the parameter in the statement
	 * @return boolean true if param set by delegate
	 */
	public boolean
	setPreparedParam (
	 PreparedStatement stmt,
	 Field f,
	 Object val,
	 int paramIndex)
		throws SQLException, TypeConversionException
	{
		// date
		if (f.getType () == Field.FT_DATETIME)
		{
			assert val instanceof java.sql.Timestamp;
			java.sql.Date d = Type.objectToSqlDate (val);
			stmt.setDate (paramIndex, d);
			return true;
		}
		return false;	// not set, return false
	}
}
