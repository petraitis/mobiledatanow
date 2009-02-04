package com.framedobjects.dashwell.db;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.log4j.Logger;

/**
 * @author Jens Richnow
 *
 */
public class MySQLConnectionTest implements DbConnectionTest {
	
	private static Logger logger = Logger.getLogger(MySQLConnectionTest.class.getName());

	public static void main(String[] args) {
		test();
	}
	
	/* (non-Javadoc)
	 * @see com.framedobjects.bliss.db.DbConnectionTest#test(com.framedobjects.bliss.biz.DbConnection)
	 */
	//DbConnection dbConn
	public static String test() {
		try {
			Class.forName("org.gjt.mm.mysql.Driver");
			//String url = dbConn.getUrl() + "/" + dbConn.getSchema();
			String url = "jdbc:mysql://192.168.1.226:3306/etomite";
			//Connection conn = DriverManager.getConnection(url, dbConn.getUsername(), dbConn.getPassword());
			System.out.println("Starting connection");
			Connection conn = DriverManager.getConnection(url, "root", "f1retrust");
			logger.debug(">> db connection test: " + conn.getCatalog());
			System.out.println("Stopping connection");
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return null;
	}

	public String test(DbConnection dbConn) {
		// TODO Auto-generated method stub
		return null;
	}

}
