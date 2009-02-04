package com.framedobjects.dashwell.db;

import com.framedobjects.dashwell.utils.Constants;

/**
 * @author Jens Richnow
 *
 */
public class DbConnectionTestFactory {

	public static synchronized DbConnectionTest getDbConnectionTest(String driver){
		if (driver != null){
			if (driver.equalsIgnoreCase(Constants.DB_DRIVER_EXCEL)){
				return new ExcelConnectionTest();
			} else if (driver.equalsIgnoreCase(Constants.DB_DRIVER_JDBC_ODBC)){
				return new JdbcOdbcConnectionTest();
			} else if (driver.equalsIgnoreCase(Constants.DB_DRIVER_MYSQL)){
				return new MySQLConnectionTest();
			}
		}
		return null;
	}
}
