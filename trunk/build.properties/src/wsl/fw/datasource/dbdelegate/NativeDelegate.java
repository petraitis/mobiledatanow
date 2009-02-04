/**	$Id: NativeDelegate.java,v 1.2 2002/09/10 01:52:45 jonc Exp $
 *
 *	Handle Db specific quirks
 *
 */
package wsl.fw.datasource.dbdelegate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.Vector;

import wsl.fw.datasource.Entity;
import wsl.fw.datasource.Field;
import wsl.fw.util.Config;
import wsl.fw.util.CKfw;
import wsl.fw.util.Type;
import wsl.fw.util.Util;

import java.sql.SQLException;
import wsl.fw.util.TypeConversionException;

public class NativeDelegate
{
	private HashSet _unicodeConvertSet = null;

	/**
	 * @return String the type for a memo for CREATE TABLE
	 */
	public String
	getMemoType (
	 int size)
	{
		return "VARCHAR (" + size + ")";
	}

	/**
	 * @return String type for common short character columns
	 */
	public String
	getVarcharType (
	 int size)
	{
		return "VARCHAR (" + size + ")";
	}

	/**
	 * @return String the type for a datetime for CREATE TABLE
	 */
	public String
	getDateTimeType ()
	{
		return "DATE";
	}

	/**
	 * @return String the type for a DECIMAL for CREATE TABLE
	 */
	public String
	getDecimalType ()
	{
		return "DOUBLE";
	}

	/**
	 *	@return String column name after any db specific limitations
	 */
	public String
	mungFieldName (
	 String columnname)
	{
		return "" + columnname + "";
	}

	/*
	 *	SQL segment for creating Table Constraints
	 *	(for Systems that support them)
	 */
	public String
	getTableConstraintSQL (
	 Entity ent,
	 Vector fields)
	{
		int constraintCount  = 0;
		String strTable = ent.getName ().replace (' ', '_');
		strTable = strTable.replace ('.', '_');
		String constraintSql = ",\n"
			+ "CONSTRAINT " + strTable + "_CONSTRAINT"
			+ " PRIMARY KEY (";

		for (int i = 0; i < fields.size (); i++)
		{
			// get the field
			Field f = (Field) fields.get (i);

			if (f.hasFlag (Field.FF_UNIQUE_KEY))
			{
				if (constraintCount > 0)
					constraintSql += ", ";
				constraintCount++;
				constraintSql += mungFieldName (f.getName ());
			}
		}
		// add final constraint paren
		constraintSql += ")";

		if (constraintCount > 0)
			return constraintSql;
		return "";
	}

	/**
	 * @return boolean true if replace dot separator
	 */
	public boolean
	doReplaceDotSeparator ()
	{
		return false;
	}

	/**
	 * @return char the schema separator
	 */
	public char
	getReplaceDotSeparator ()
	{
		return '_';
	}

	/**
	 * @return boolean true if we use parameterised update and insert
	 */
	public boolean
	useParamUpdate ()
	{
		return true;
	}

	/**
	 * @return boolean true if we use parameterised select
	 */
	public boolean
	useParamSelect ()
	{
		return true;
	}

	/**
	 * @return boolean true if we use parameterised select
	 */
	public boolean
	useParamUpdateCriteria ()
	{
		return useParamSelect ();
	}

	/**
	 * Determine if it is necessary to perform hex to unicode conversion
	 * for the specified data type.
	 * This is used to correct unicode strings where they are loaded as hex
	 * digits rather than unicode characters.
	 * The config should have an entry for each native delegate of the form:
	 * <delegate class>.unicodeConvertTypes=<sql native type>[;<sql native type>...]
	 * @param typeName, a string naming the SQL data type.
	 * @return true if we need to convert unicode strings for the names type.
	 */
	public boolean
	shouldUnicodeConvert (
	 String dataType)
	{
		// if not present load set from config
		if (_unicodeConvertSet == null)
		{
			// get the config param, based off delegate class name
			_unicodeConvertSet = new HashSet ();
			String typeNames = Config.getProp (
				getClass ().getName () + CKfw.UNICODE_CONVERT_TYPE_SUFFIX);

			if (!Util.isEmpty (typeNames))
			{
				// tokenize and add to set
				StringTokenizer st = new StringTokenizer (typeNames, ";");
				while (st.hasMoreTokens())
					_unicodeConvertSet.add (st.nextToken ());
			}
		}

		// return true if the specified type is in the set
		return _unicodeConvertSet.contains (dataType);
	}

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
		return null;
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
		return false;
	}

	/**
	 * Get a table name from meta data
	 * @param rs the DataBaseMetaData resultset for a table
	 */
	public String
	getTableName (
	 ResultSet rs)
		throws SQLException
	{
		String tableName = "";
		try
		{
			// get the table name and schema
			tableName = rs.getString ("TABLE_NAME");

		} catch(Exception e)
		{
			// try ordinal index
			tableName = rs.getString (3);
		}

		// return
		return tableName;
	}
}
