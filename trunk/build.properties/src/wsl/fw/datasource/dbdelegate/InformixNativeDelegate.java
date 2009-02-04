/**	$Id: InformixNativeDelegate.java,v 1.2 2002/09/10 01:52:45 jonc Exp $
 *
 *	Informix Online Native delegate.
 *
 */
package wsl.fw.datasource.dbdelegate;

import java.util.Vector;

import wsl.fw.datasource.Entity;
import wsl.fw.datasource.Field;

public class InformixNativeDelegate
	extends NativeDelegate
{
	public static final String NAME = "Informix Dynamic Server";

	public String
	getMemoType (
	 int size)
	{
		return "CHAR (" + size + ")";
	}

	public String
	getDateTimeType ()
	{
		return "DATETIME YEAR TO MINUTE";
	}

	public String
	getDecimalType ()
	{
		return "DECIMAL";
	}

	/**
	 *  Informix differs from the standard constraint version
	 *  in that:
	 *  	a.	It doesn't support named table constraints
	 *      b.	If you have a unique key on a field, you can't
	 *      	define a PRIMARY KEY table constraint on that
	 *          field as well.
	 */
	public String
	getTableConstraintSQL (
	 Entity ent,
	 Vector fields)
	{
		int constraintCount = 0;
		String strTable = ent.getName ().replace (' ', '_');
		strTable = strTable.replace ('.', '_');
		String constraintSql = ",\n"
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

		constraintSql += ")";		// add final constraint paren

		if (constraintCount > 1)	// multiple-field primary keys only
			return constraintSql;
		return "";
	}
}
