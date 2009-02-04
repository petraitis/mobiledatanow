/**	$Id: DirectQuery.java,v 1.1 2002/06/14 00:14:16 jonc Exp $
 *
 *	DirectQuery - handles raw-sql
 *
 */
package wsl.fw.datasource;

import wsl.fw.datasource.Query;
import wsl.fw.datasource.DataSourceException;
import wsl.fw.util.Util;

public class DirectQuery
	extends Query
{
	public final static String UIDKEYWORD = "<UID>";

    private String _query;

    public
	DirectQuery()
    {
    }

    public
	DirectQuery (
	 String query)
    {
        _query = query;
    }

    public String
	getSQL ()
    {
        return _query != null ? _query : "";
    }

    public void
	setSQL (
	 String query)
    {
        _query = query;
    }

	/**
	 *  Keyword substitution
	 */
	public void
	setUseridKeyword (
	 String userid)
	{
		/*
		 *	Hunt thru' the sql statement and replace the keyword tag..
		 */
		String replacement = "'" + userid + "'";

		_query = Util.strReplace (_query, UIDKEYWORD, replacement);
 	}
}