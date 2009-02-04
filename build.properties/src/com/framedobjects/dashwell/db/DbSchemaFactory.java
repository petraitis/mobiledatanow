/*
 * Created on 14/08/2005
 *
 */
package com.framedobjects.dashwell.db;

import com.framedobjects.dashwell.utils.Constants;

/**
 * Retrieves the database schema (empty) for the given database connection. It 
 * also sets the new database schema to the db connection.
 * @author Jens Richnow
 *
 */
public class DbSchemaFactory {

		public static synchronized DbSchema getSchemaForConnection(DbConnection dbConn){
			if (dbConn != null){
				DbSchema dbSchema = null;
				String driver = dbConn.getDriver();
				if (driver != null){
					if (driver.equalsIgnoreCase(Constants.DB_DRIVER_EXCEL)){
						dbSchema = new ExcelDbSchema(dbConn);
						dbConn.setDbSchema(dbSchema);
						return dbSchema;
					} else if (driver.equalsIgnoreCase(Constants.DB_DRIVER_JDBC_ODBC)){
						dbSchema = new JdbcOdbcDbSchema(dbConn);
						dbConn.setDbSchema(dbSchema);
						return dbSchema;
					} else if (driver.equalsIgnoreCase(Constants.DB_DRIVER_MYSQL)){
						dbSchema = new MySqlDbSchema(dbConn);
						dbConn.setDbSchema(dbSchema);
						return dbSchema;
					}
				}
			}
			return null;
		}
}
