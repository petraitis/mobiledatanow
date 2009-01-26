package com.framedobjects.dashwell.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.TreeMap;

import com.framedobjects.dashwell.db.meta.MetaField;
import com.framedobjects.dashwell.db.meta.MetaTable;
import com.framedobjects.dashwell.utils.DataUtils;

/**
 * @author Jens Richnow
 * 
 */
public class MySqlDbSchema extends DbSchema {

  /**
   * 
   */
  public MySqlDbSchema(DbConnection dbConn) {
    this.dbConn = dbConn;
  }

  public ArrayList getTables() {
    if (tables == null) {
      tables = new ArrayList<MetaTable>();
      tablesAndFields = new TreeMap();
      ArrayList<MetaField> fields = null;
      try {
        Class.forName("org.gjt.mm.mysql.Driver");
        String url = dbConn.getUrl();// + "/" + dbConn.getSchema();
        Connection conn = DriverManager.getConnection(url,
            dbConn.getUsername(), dbConn.getPassword());
        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet rsTables = metaData.getTables(null, null, null, null);
        String tableName = null;
        String tableType = null;
        MetaTable table = null;
        String columnName = null;
        String columnType = null;
        String columnSize = null;
        String columnDescription = null;
        MetaField field = null;
        while (rsTables.next()) {
          tableName = rsTables.getString("TABLE_NAME");
          tableType = rsTables.getString("TABLE_CAT");
          table = new MetaTable(0, 0, tableType, tableName, "");
          tables.add(table);
          // Now get the fields for the table.
          fields = new ArrayList<MetaField>();
          ResultSet rsColumns = metaData
              .getColumns(null, null, tableName, null);
          while (rsColumns.next()) {
            columnName = rsColumns.getString("COLUMN_NAME");
            columnType = rsColumns.getString("TYPE_NAME");
            columnSize = rsColumns.getString("COLUMN_SIZE");
            columnDescription = rsColumns.getString("REMARKS");
            field = new MetaField();
            field.setName(columnName);
            field.setType(DataUtils.getMetaFieldType(columnType));
            field.setNativeType(columnType);
            field.setSize(columnSize);
            field.setDescription(columnDescription);
            fields.add(field);
          }
          // Add the table and its fields to the map.
          tablesAndFields.put(tableName, fields);
        }
        conn.close();
      } catch (Exception e) {
        System.err.println(e.getMessage());
      }
    }
    return tables;
  }

  // public ArrayList getFieldsForTable(String tableName){
  // ArrayList fields = null;
  // if (tableName != null){
  // fields = (ArrayList)tablesAndFields.get(tableName);
  // }
  // ArrayList fields = new ArrayList();
  // try {
  // Class.forName("org.gjt.mm.mysql.Driver");
  // String url = dbConn.getUrl() + "/" + dbConn.getSchema();
  // Connection conn = DriverManager.getConnection(url, dbConn.getUsername(),
  // dbConn.getPassword());
  // DatabaseMetaData metaData = conn.getMetaData();
  // String name = null;
  // String type = null;
  // String size = null;
  // String description = null;
  // MetaField field = null;
  // ResultSet rsColumns = metaData.getColumns(null, null, tableName, null);
  // while (rsColumns.next()){
  // name = rsColumns.getString("COLUMN_NAME");
  // type = rsColumns.getString("TYPE_NAME");
  // size = rsColumns.getString("COLUMN_SIZE");
  // description = rsColumns.getString("REMARKS");
  // field = new MetaField();
  // field.setName(name);
  // field.setType(type);
  // field.setSize(size);
  // field.setDescription(description);
  // fields.add(field);
  // }
  // conn.close();
  // } catch (Exception e) {
  // System.err.println(e.getMessage());
  // }
  // return fields;
  // }
}
