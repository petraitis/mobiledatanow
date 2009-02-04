/**	$Id: ProgressNativeDelegate.java,v 1.2 2002/09/10 01:52:45 jonc Exp $
 *
 *	Progress RDBMS
 *
 */
package wsl.fw.datasource.dbdelegate;

import java.util.Vector;

import wsl.fw.datasource.Entity;
import wsl.fw.datasource.Field;

public class ProgressNativeDelegate
	extends NativeDelegate
{
	public static final String NAME = "PROGRESS";

	/**
	 *  Progress has the following restriction:
	 *
	 *      a.	If you have a unique key on a field, you can't
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
		constraintSql += ")";		// add final constraint paren

		if (constraintCount > 1)	// multiple-field primary keys only
			return constraintSql;
		return "";
	}
}
