package com.framedobjects.db;

import java.sql.*;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.bitmechanic.sql.ConnectionPoolManager;

public class ConnectionPool {

	private static ConnectionPool pool;
	private ConnectionPoolManager mgr;
	private static ResourceBundle resources = null;
	static Logger logger = Logger.getLogger(ConnectionPool.class.getName());
	public static String alias = null;

	private ConnectionPool() throws Exception {

		mgr = new ConnectionPoolManager(300);

		try {
			resources = ResourceBundle.getBundle("com.framedobjects.dashwell.db");
		} catch (MissingResourceException mre) {
			logger.warn("Could not find resource bundle: " + mre.getMessage());
		}
		String db_alias = resources.getString("db_alias");
		alias = db_alias;
		String db_driver = resources.getString("db_driver");
		String db_path = resources.getString("db_path");
		String db_autoReconnecnt = resources.getString("db_autoReconnect");
		String db_username = resources.getString("db_username");
		String db_password = resources.getString("db_password");
		String db_max_connections = resources.getString("db_max_connections");
		String db_sec_idle_connection = resources.getString("db_sec_idle_connection");
		String db_sec_checkout_connection = resources.getString("db_sec_checkout_connection");
		String db_reuse_time_connection = resources.getString("db_reuse_time_connection");
		String db_cache_statements = resources.getString("db_cache_statements");

		Class.forName(db_driver).newInstance();

		// Add one alias to the pool for each JDBC datasource you wish to connect
		// to.  From this point forward any objects that need connection handles
		// do not need to know the url, username, or password of the databse.
		// They simply need the "alias" you named the pool with here
		mgr.addAlias(db_alias, db_driver, db_path + "?autoReconnect=" + db_autoReconnecnt,
									db_username, db_password, 
									Integer.parseInt(db_max_connections),
									Integer.parseInt(db_sec_idle_connection), 		// seconds a connection can be idle before it is closed
									Integer.parseInt(db_sec_checkout_connection), // seconds a connection can be checked out by a thread
																																// before it is returned back to the pool
									Integer.parseInt(db_reuse_time_connection), 	// number of times a connection can be re-used before
																																// connection to database is closed and re-opened
									Boolean.getBoolean(db_cache_statements));			// specifies whether to cache statements (optional parameter.  
																																// set to 'true' by default.)
		logger.info("ConnectionManager: " + mgr.dumpInfo());
	}
	

	public static void init() throws Exception {
		pool = new ConnectionPool();
	}

	public static ConnectionPool getInstance() {
		if (pool == null) {
			throw new IllegalStateException("Pool not initialized.");
		}
		return (pool);
	}

	public Connection getConnection() throws SQLException {
		Connection connection = DriverManager.getConnection(
														ConnectionPoolManager.URL_PREFIX + alias, 
														null, null);
		
		return connection;
	}
}