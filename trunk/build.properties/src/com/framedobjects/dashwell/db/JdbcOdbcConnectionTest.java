package com.framedobjects.dashwell.db;

import java.sql.Connection;
import java.sql.DriverManager;


/**
 * @author Jens Richnow
 *
 */
public class JdbcOdbcConnectionTest implements DbConnectionTest {

	/* (non-Javadoc)
	 * @see com.framedobjects.bliss.db.DbConnectionTest#test(com.framedobjects.bliss.biz.DbConnection)
	 */
	public String test(DbConnection dbConn) {
		try {
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			String url = dbConn.getUrl();// + ":" + dbConn.getSchema();
			Connection conn = DriverManager.getConnection(url, dbConn.getUsername(), dbConn.getPassword());
			System.err.println(">> " + conn.getCatalog());
			conn.close();
		} catch (Exception e) {
			return e.getMessage();
		}
		return null;
	}

}
