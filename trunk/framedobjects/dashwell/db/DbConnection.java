package com.framedobjects.dashwell.db;

/**
 * The DbConnection class used to connect to the physical database.
 * @author Jens Richnow
 * 
 */
public class DbConnection extends AbstractDbConnection {

	public DbConnection(int connectionID, int driverId, String name,
  		String username, String password, String url, int mirrored){//String schema, 
	  	super(connectionID, driverId, name, username, password, url, mirrored);//schema, 
	  }
}
