/*
 * Created on 16/08/2005
 *
 */
package com.framedobjects.dashwell.biz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import com.framedobjects.dashwell.db.DbConnection;
import com.framedobjects.dashwell.db.DbSchema;
import com.framedobjects.dashwell.db.meta.MetaTable;
import com.framedobjects.dashwell.utils.DataUtils;

/**
 * @author Richmow
 * 
 */
public class ConnectionWizard {
	
	private String type = null;

  /** Database connection parameters */
  private int connectionID = 0;
  private String driver = null;
  private String name = null;
  private String password = null;
  private String url = null;
  private String username = null;
  private String schema = null;
  private DbSchema dbSchema = null;
  private String _test = null;
  private String mirrorDB = null;
  private Vector drivers = DataUtils.getDriverList();

  /** Parameters for the tables page. */
  private SortedSet<MetaTable> availableTables = Collections
      .synchronizedSortedSet(new TreeSet<MetaTable>());

  private SortedSet<MetaTable> metaTables = Collections
      .synchronizedSortedSet(new TreeSet<MetaTable>());

  private String availableTable = null;

  private String metaTable = null;

  private boolean needTableValidation = false;

  /** Parameters for the relationship page. */
  private DbConnection dbConn = null;

  private Vector leftFields = null;

  private Vector rightFields = null;

  private String leftTable = null;

  private String rightTable = null;

  private String leftField = null;

  private String rightField = null;

  private String relation = null;

  private boolean isFinish = false;

  public void initialiseTables() {
    if (metaTables == null || metaTables.size() == 0) {
      SortedSet<MetaTable> availTables = Collections.synchronizedSortedSet(new TreeSet<MetaTable>());
      ArrayList<MetaTable> tables = dbSchema.getTables(); 
      for (MetaTable table : tables) {
      	System.out.println(table);
      	availTables.add(table);
			}
//      Iterator iter = dbSchema.getTables().iterator();
//      while (iter.hasNext()) {
//        availTables.add((MetaTable)iter.next());
//      }
      availableTables = availTables;
      // Make sure the meta tables are reset.
      metaTables = Collections.synchronizedSortedSet(new TreeSet<MetaTable>());
    }
  }

  public void moveToMetaTables(String tableName) {
    Iterator iter = availableTables.iterator();
    MetaTable metaTable = null;
    while (iter.hasNext()) {
      metaTable = (MetaTable) iter.next();
      if (metaTable.getName().equalsIgnoreCase(tableName)) {
        break;
      }
    }
    if (metaTable != null) {
      if (availableTables.remove(metaTable)) {
        metaTables.add(metaTable);
      }
    }
  }

  public void moveToAvailableTables(String tableName) {
    Iterator iter = metaTables.iterator();
    MetaTable metaTable = null;
    while (iter.hasNext()) {
      metaTable = (MetaTable) iter.next();
      if (metaTable.getName().equalsIgnoreCase(tableName)) {
        break;
      }
    }
    if (metaTable != null) {
      if (metaTables.remove(metaTable)) {
        availableTables.add(metaTable);
      }
    }
  }

  public void moveAllToMetaTables() {
    Iterator iter = availableTables.iterator();
    MetaTable metaTable = null;
    while (iter.hasNext()) {
      metaTable = (MetaTable) iter.next();
      if (!metaTables.contains(metaTable)) {
        metaTables.add(metaTable);
      }
    }
    availableTables = Collections.synchronizedSortedSet(new TreeSet<MetaTable>());
  }

  public void moveAllToAvailableTables() {
    Iterator iter = metaTables.iterator();
    MetaTable metaTable = null;
    while (iter.hasNext()) {
      metaTable = (MetaTable) iter.next();
      if (!availableTables.contains(metaTable)) {
        availableTables.add(metaTable);
      }
    }
    metaTables = Collections.synchronizedSortedSet(new TreeSet<MetaTable>());
  }
  
  public void addToMetaTables(String tableName) {
    Iterator iter = availableTables.iterator();
    MetaTable metaTable = null;
    while (iter.hasNext()) {
      metaTable = (MetaTable) iter.next();
      if (metaTable.getName().equalsIgnoreCase(tableName)) {
        break;
      }
    }
    if (metaTable != null) {
      metaTables.add(metaTable);
    }
  }
  
  public void resetMetaTables(){
  	metaTables = Collections.synchronizedSortedSet(new TreeSet<MetaTable>());
  }

  /**
   * @return Returns the connectionID.
   */
  public int getConnectionID() {
    return connectionID;
  }

  /**
   * @param connectionID
   *          The connectionID to set.
   */
  public void setConnectionID(int connectionID) {
    this.connectionID = connectionID;
  }

  /**
   * @return Returns the dbSchema.
   */
  public DbSchema getDbSchema() {
    return dbSchema;
  }

  /**
   * @param dbSchema
   *          The dbSchema to set.
   */
  public void setDbSchema(DbSchema dbSchema) {
    this.dbSchema = dbSchema;
  }

  /**
   * @return Returns the driver.
   */
  public String getDriver() {
    return driver;
  }

  /**
   * @param driver
   *          The driver to set.
   */
  public void setDriver(String driver) {
    this.driver = driver;
  }

  /**
   * @return Returns the name.
   */
  public String getName() {
    return name;
  }

  /**
   * @param name
   *          The name to set.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return Returns the password.
   */
  public String getPassword() {
    return password;
  }

  /**
   * @param password
   *          The password to set.
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * @return Returns the schema.
   */
  public String getSchema() {
    return schema;
  }

  /**
   * @param schema
   *          The schema to set.
   */
  public void setSchema(String schema) {
    this.schema = schema;
  }

  /**
   * @return Returns the url.
   */
  public String getUrl() {
    return url;
  }

  /**
   * @param url
   *          The url to set.
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * @return Returns the username.
   */
  public String getUsername() {
    return username;
  }

  /**
   * @param username
   *          The username to set.
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * @return Returns the _test.
   */
  public String get_test() {
    return _test;
  }

  /**
   * @param _test
   *          The _test to set.
   */
  public void set_test(String _test) {
    this._test = _test;
  }

  /**
   * @return Returns the drivers.
   */
  public Vector getDrivers() {
    return drivers;
  }

  /**
   * @param drivers
   *          The drivers to set.
   */
  public void setDrivers(Vector drivers) {
    this.drivers = drivers;
  }

  /**
   * @return Returns the dbConn.
   */
  public DbConnection getDbConn() {
    return dbConn;
  }

  /**
   * @param dbConn
   *          The dbConn to set.
   */
  public void setDbConn(DbConnection dbConn) {
    this.dbConn = dbConn;
  }

  /**
   * @return Returns the rightTables.
   */
  public SortedSet<MetaTable> getMetaTables() {
    return metaTables;
  }

  /**
   * @param rightTables
   *          The rightTables to set.
   */
  public void setMetaTables(SortedSet<MetaTable> rightTables) {
    this.metaTables = rightTables;
  }

  /**
   * @return Returns the leftTable.
   */
  public String getLeftTable() {
    return leftTable;
  }

  /**
   * @param leftTable
   *          The leftTable to set.
   */
  public void setLeftTable(String leftTable) {
    this.leftTable = leftTable;
  }

  /**
   * @return Returns the rightTable.
   */
  public String getRightTable() {
    return rightTable;
  }

  /**
   * @param rightTable
   *          The rightTable to set.
   */
  public void setRightTable(String rightTable) {
    this.rightTable = rightTable;
  }

  /**
   * @return Returns the leftField.
   */
  public String getLeftField() {
    return leftField;
  }

  /**
   * @param leftField
   *          The leftField to set.
   */
  public void setLeftField(String leftField) {
    this.leftField = leftField;
  }

  /**
   * @return Returns the leftFields.
   */
  public Vector getLeftFields() {
    return leftFields;
  }

  /**
   * @param leftFields
   *          The leftFields to set.
   */
  public void setLeftFields(Vector leftFields) {
    this.leftFields = leftFields;
  }

  /**
   * @return Returns the rightField.
   */
  public String getRightField() {
    return rightField;
  }

  /**
   * @param rightField
   *          The rightField to set.
   */
  public void setRightField(String rightField) {
    this.rightField = rightField;
  }

  /**
   * @return Returns the rightFields.
   */
  public Vector getRightFields() {
    return rightFields;
  }

  /**
   * @param rightFields
   *          The rightFields to set.
   */
  public void setRightFields(Vector rightFields) {
    this.rightFields = rightFields;
  }

  /**
   * @return Returns the relation.
   */
  public String getRelation() {
    return relation;
  }

  /**
   * @param relation
   *          The relation to set.
   */
  public void setRelation(String relation) {
    this.relation = relation;
  }

  /**
   * @return Returns the availableTables.
   */
  public SortedSet<MetaTable> getAvailableTables() {
    return availableTables;
  }

  /**
   * @param availableTables
   *          The availableTables to set.
   */
  public void setAvailableTables(SortedSet availableTables) {
    this.availableTables = availableTables;
  }

  /**
   * @return Returns the availableTable.
   */
  public String getAvailableTable() {
    return availableTable;
  }

  /**
   * @param availableTable
   *          The availableTable to set.
   */
  public void setAvailableTable(String availableTable) {
    this.availableTable = availableTable;
  }

  /**
   * @return Returns the metaTable.
   */
  public String getMetaTable() {
    return metaTable;
  }

  /**
   * @param metaTable
   *          The metaTable to set.
   */
  public void setMetaTable(String metaTable) {
    this.metaTable = metaTable;
  }

  /**
   * @return Returns the isFinish.
   */
  public boolean isFinish() {
    return isFinish;
  }

  /**
   * @param isFinish
   *          The isFinish to set.
   */
  public void setFinish(boolean isFinish) {
    this.isFinish = isFinish;
  }

  /**
   * @return Returns the needTableValidation.
   */
  public boolean isNeedTableValidation() {
    return needTableValidation;
  }

  /**
   * @param needTableValidation
   *          The needTableValidation to set.
   */
  public void setNeedTableValidation(boolean needTableValidation) {
    this.needTableValidation = needTableValidation;
  }

  /**
   * @return Returns the mirrorDB.
   */
  public String getMirrorDB() {
    return mirrorDB;
  }

  /**
   * @param mirrorDB
   *          The mirrorDB to set.
   */
  public void setMirrorDB(String mirrorDB) {
    this.mirrorDB = mirrorDB;
  }

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
