package com.framedobjects.dashwell.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

import com.framedobjects.dashwell.db.meta.MetaField;
import com.framedobjects.dashwell.db.meta.MetaRelation;
import com.framedobjects.dashwell.db.meta.MetaTable;

/**
 * This class is used to interact directly with the existing database schema.
 * 
 * @author Jens Richnow
 * 
 */
public abstract class DbSchema {

  DbConnection dbConn = null;
  ArrayList<MetaTable> tables = null;
  TreeMap tablesAndFields = null;
  TreeMap relations = new TreeMap();
  public abstract ArrayList<MetaTable> getTables();

  public ArrayList<MetaField> getFieldsForTable(String tableName) {
    ArrayList<MetaField> fields = null;
    if (tableName != null) {
    	fields = (ArrayList) tablesAndFields.get(tableName);
    }
    return fields;
  }

  public ArrayList<MetaRelation> getRelations() {
    ArrayList<MetaRelation> rels = new ArrayList<MetaRelation>();
    Collection coll = relations.values();
    Iterator iter = coll.iterator();
    while (iter.hasNext()) {
      rels.add((MetaRelation)iter.next());
    }
    return rels;
  }

  public void addRelation(MetaRelation relation) {
    if (relation != null
        && !this.relations.containsKey(relation.getQualifiedName())) {
      this.relations.put(relation.getQualifiedName(), relation);
    }
  }

  public void removeRelation(MetaRelation relation) {
    if (relation != null) {
      this.removeRelationByName(relation.getQualifiedName());
    }
  }

  public void removeRelationByName(String qualifiedName) {
    if (qualifiedName != null && this.relations.containsKey(qualifiedName)) {
      this.relations.remove(qualifiedName);
    }
  }
}
