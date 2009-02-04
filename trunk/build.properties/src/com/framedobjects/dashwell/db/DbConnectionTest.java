package com.framedobjects.dashwell.db;


/**
 * @author Jens Richnow
 *
 */
public interface DbConnectionTest {

	/**
	 * The DB connection test method. 
	 * @param dbConn 	The configured <code>DbConnection</code> object.
	 * @return 				<code>null/code>, if test is successfull otherwise an 
	 * 								exceptionmessage.
	 */
	public String test(DbConnection dbConn);
}
