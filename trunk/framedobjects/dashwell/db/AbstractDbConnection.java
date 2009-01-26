/**
 * 
 */
package com.framedobjects.dashwell.db;

import wsl.mdn.dataview.JdbcDriver;

/**
 * This class describes all parameters required for a database connectivity.
 * @author Jens Richnow
 *
 */
public class AbstractDbConnection {

  protected int connectionID = 0;
  protected int driverId = 0;
  protected JdbcDriver jdbcDriver = null;
  protected String driver = null;
  protected String name = null;
  protected String password = null;
  protected String url = null;
  protected String username = null;
  //protected String schema = null;
  protected int mirrorred = 0;
  protected DbSchema dbSchema = null;
  
  public AbstractDbConnection(int connectionID, int driverId, String name,
  		String username, String password, String url, int mirrored){//String schema, 
  	this.connectionID = connectionID;
  	this.driverId = driverId;
  	this.name = name;
  	this.username = username;
  	this.password = password;
  	this.url = url;
  	//this.schema = schema;
  	this.mirrorred = mirrored;
  }
  
  public AbstractDbConnection(int connectionID, String driver, String name,
  			String username, String password, String url, int mirrored) {//String schema, 
		this.connectionID = connectionID;
	  	this.driver = driver;
	  	this.name = name;
	  	this.username = username;
	  	this.password = password;
	  	this.url = url;
	  	//this.schema = schema;
	  	this.mirrorred = mirrored;
  }
  
  /**
   * @return Returns the connectionID.
   */
  public int getConnectionID() {
    return connectionID;
  }
  /**
   * @return Returns the dbSchema.
   */
  public DbSchema getDbSchema() {
    return dbSchema;
  }
  /**
   * @return Returns the driver.
   */
  public String getDriver() {
    return driver;
  }
  /**
   * @return Returns the mirrorred.
   */
  public int getMirrorred() {
    return mirrorred;
  }
  /**
   * @return Returns the name.
   */
  public String getName() {
    return name;
  }
  /**
   * @return Returns the password.
   */
  public String getPassword() {
    return password;
  }
  /**
   * @return Returns the schema.
   */
  /*public String getSchema() {
    return schema;
  }*/
  /**
   * @return Returns the url.
   */
  public String getUrl() {
    return url;
  }
  /**
   * @return Returns the username.
   */
  public String getUsername() {
    return username;
  }


	public void setDbSchema(DbSchema dbSchema) {
		this.dbSchema = dbSchema;
	}


	public int getDriverId() {
		return driverId;
	}


	public void setDriverId(int driverId) {
		this.driverId = driverId;
	}


	public JdbcDriver getJdbcDriver() {
		return jdbcDriver;
	}


	public void setJdbcDriver(JdbcDriver jdbcDriver) {
		this.jdbcDriver = jdbcDriver;
	}
}
