/**	$Id: JdbcKeyGenerator.java,v 1.3 2002/06/17 02:17:56 jonc Exp $
 *
 *	Strategy class that generates next keys.
 *	Generates sequential integer keys.
 *
 */
package wsl.fw.datasource;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import wsl.fw.datasource.dbdelegate.NativeDelegate;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.resource.ResId;

public class JdbcKeyGenerator
{
    // resources
    public static final ResId
		RES_DEBUG_GENERATE_KEY = new ResId ("JdbcKeyGenerator.debug.GenerateKey");

    // attributes
    private JdbcDataSource _ds = null;
    private Statement      _stmt = null;

    //--------------------------------------------------------------------------
    /**
     * Constructor.
     * @param ds the JdbcDataSource to generate keys for.
     */
    public JdbcKeyGenerator(JdbcDataSource ds)
    {
        _ds = ds;
    }

    //--------------------------------------------------------------------------
    /**
     * Generates a next key from generator data. Generated sequential integer
     * keys.
     * @param kgd the key generator data.
     * @return Object the generated key (an Integer).
     */
    public synchronized Object
	getNextKey (
	 KeyGeneratorData kgd)
        throws SQLException, DataSourceException
    {
        // fixme
        // May want to optimise by block allocating keys.
        // Does not work for multiple servers since the select/update is not a
        // locked atomic operation outside this JVM.

        // validate
        Util.argCheckNull(kgd);
        Util.argCheckNull(_ds);

        Connection conn = null;
        boolean isError = false;
        try
        {
            // get the connection
            conn = _ds.getConnection();
            Util.argCheckNull(conn);

            // build the statement
            String sql =
				"SELECT " + _ds.escapeFieldName (kgd._keyColumn)
				+ " FROM " + _ds.escapeTableName (kgd._keyTable)
                + " WHERE "
				+ _ds.escapeFieldName (kgd._mapColumn)
				+ " = "
				+ "'" + kgd._mapValue + "'";

            sql = getNativeSQL(conn, sql);

            // execute the query
            if(_stmt == null)
                _stmt = conn.createStatement();
            ResultSet rs = _stmt.executeQuery(sql);

            // get the key
            Object key = null;
            if(rs != null && rs.next())
            {
                key = rs.getObject(kgd._keyColumn);

                // update the key
                int nextKey = Integer.parseInt(key.toString());
                nextKey++;
                sql =
					"UPDATE " + _ds.escapeTableName (kgd._keyTable)
					+ " SET "
					+ _ds.escapeFieldName (kgd._keyColumn)
					+ " = "
                    + String.valueOf(nextKey)
					+ " WHERE " + _ds.escapeFieldName (kgd._mapColumn)
					+ " = " + "'" + kgd._mapValue + "'";

                sql = getNativeSQL(conn, sql);
                Log.debug(RES_DEBUG_GENERATE_KEY.getText() + " " + sql);
                _stmt.executeUpdate(sql);
            }
            else
            {
                // no entry in keygen table, do an insert instead
                // set the starting key
                final int initialKey = 1;
                int       nextKey = initialKey + 1;

                key = new Integer(initialKey);

                // create SQL for insert
                sql =
					"INSERT INTO " + _ds.escapeTableName (kgd._keyTable)
					+ " ("
					+ _ds.escapeFieldName (kgd._mapColumn) + ", "
                    + _ds.escapeFieldName (kgd._keyColumn)
					+ ") VALUES ('" + kgd._mapValue + "', "
                    + String.valueOf (nextKey) + ")";

                // convert to native, log and execute
                sql = getNativeSQL(conn, sql);
                Log.debug(RES_DEBUG_GENERATE_KEY.getText() + " " + sql);
                _stmt.executeUpdate(sql);
            }

            // return the key
            return key;
        }
        catch(SQLException e)
        {
            isError = true;
            throw e;
        }
        finally
        {
            // return the connection to the pool
            if (conn != null)
                try
                {
                    // if error and pooled pooled close the actual connection
                    if(isError && conn instanceof PooledConnection)
                        ((PooledConnection)conn).close(true);
                    else
                        conn.close();
                }
                catch (SQLException e)
                {
                }
        }
    }

    /**
     * Simple helper func to enable turning on/off nativeSQL
     */
    private String getNativeSQL(Connection conn, String sql)
    {
        return sql;
//        return conn.nativeSQL(sql);
    }
}
