/**
 * 
 */
package com.framedobjects.dashwell.db.meta;

import java.util.Iterator;
import java.util.List;

/**
 * This is the meta schema with which the application deals with. It has a 
 * reference to the tables, relations, queries and views. It also allows to get
 * meta data about the fields of the defined tables.
 * @author Jens Richnow
 *
 */
public class MetaSchema {

  /** The database ID for the schema. */
  private int schemaID = 0;
  /** The database schema name. */
  private String name = null;
  /** List of all imported tables. */
  private List tables = null;
  /** List of all established relations. */
  private List relations = null;
  /** List of all established queries. */
  private List queries = null;
  /** List of all established views. */
  private List views = null;
  
  public MetaTable getMetaTableByID(int tableID){
    MetaTable metaTable = null;
    Iterator iter = tables.iterator();
    MetaTable checkTable = null;
    while (iter.hasNext()){
      checkTable = (MetaTable)iter.next();
      if (checkTable.getTableID() == tableID){
        metaTable = checkTable;
        break;
      }
    }
    return metaTable;
  }
  
  public String getTableName(int tableID){
    String tableName = null;
    Iterator iter = tables.iterator();
    MetaTable metaTable = null;
    while (iter.hasNext()){
      metaTable = (MetaTable)iter.next();
      if (metaTable.getTableID() == tableID){
        tableName = metaTable.getName();
        break;
      }
    }
    return tableName;
  }
  
  public MetaField getFieldForTables(int fieldID, int tableID){
    Iterator iter = tables.iterator();
    MetaTable metaTable = null;
    MetaField metaField = null;
    MetaField checkField = null;
    List metaFields = null;
    while (iter.hasNext()){
      metaTable = (MetaTable)iter.next();
      if (metaTable.getTableID() == tableID){
        metaFields = metaTable.getAllFields();
        break;
      }
    }
    if (metaFields != null){
      iter = metaFields.iterator();
      while (iter.hasNext()){
        checkField = (MetaField)iter.next();
        if (checkField.getFieldID() == fieldID){
          metaField = checkField;
          break;
        }
      }
    }
    return metaField;
  }
  
  public List getFieldsForTable(int tableID){
    Iterator iter = tables.iterator();
    MetaTable metaTable = null;
    while (iter.hasNext()){
      metaTable = (MetaTable)iter.next();
      if (metaTable.getTableID() == tableID){
        return metaTable.getAllFields();
      }
    }
    return null;
  }
  
  public List getFieldsForTable(String tableName){
    Iterator iter = tables.iterator();
    MetaTable metaTable = null;
    while (iter.hasNext()){
      metaTable = (MetaTable)iter.next();
      if (metaTable.getName().equalsIgnoreCase(tableName)){
        return metaTable.getAllFields();
      }
    }
    return null;
  }
  
  public MetaRelation getRelation(String relationName){
    MetaRelation metaRelation = null;
    Iterator iter = relations.iterator();
    MetaRelation checkRelation = null;
    while (iter.hasNext()){
      checkRelation = (MetaRelation)iter.next();
      if (checkRelation.getQualifiedName().equalsIgnoreCase(relationName)){
        metaRelation = checkRelation;
        break;
      }
    }
    return metaRelation;
  }
  
  /**
   * @return Returns the queries.
   */
  public List getQueries() {
    return queries;
  }
  /**
   * @param queries The queries to set.
   */
  public void setQueries(List queries) {
    this.queries = queries;
  }
  /**
   * @return Returns the relations.
   */
  public List getRelations() {
    return relations;
  }
  /**
   * @param relations The relations to set.
   */
  public void setRelations(List relations) {
    this.relations = relations;
  }
  /**
   * @return Returns the schemaID.
   */
  public int getSchemaID() {
    return schemaID;
  }
  /**
   * @param schemaID The schemaID to set.
   */
  public void setSchemaID(int schemaID) {
    this.schemaID = schemaID;
  }
  /**
   * @return Returns the tables.
   */
  public List getTables() {
    return tables;
  }
  /**
   * @param tables The tables to set.
   */
  public void setTables(List tables) {
    this.tables = tables;
  }
  /**
   * @return Returns the views.
   */
  public List getViews() {
    return views;
  }
  /**
   * @param views The views to set.
   */
  public void setViews(List views) {
    this.views = views;
  }

  /**
   * @return Returns the name.
   */
  public String getName() {
    return name;
  }

  /**
   * @param name The name to set.
   */
  public void setName(String name) {
    this.name = name;
  }
}
