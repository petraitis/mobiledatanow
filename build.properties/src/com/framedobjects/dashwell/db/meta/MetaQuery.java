/**
 * 
 */
package com.framedobjects.dashwell.db.meta;

/**
 * The query defined by the user. Either a direct SQL query or mapped via views.
 * @author Jens Richnow
 *
 */
public class MetaQuery {

  private int queryID = 0;
  private int viewID = 0;
  private int schemaID = 0;
  private String queryName = null;
  private String description = null;
  private String criteria = null;
  private String sorts = null;
  private int groupFieldID = 0;
  private String rawQuery = null;
  /**
   * @return Returns the criteria.
   */
  public String getCriteria() {
    return criteria;
  }
  /**
   * @param criteria The criteria to set.
   */
  public void setCriteria(String criteria) {
    this.criteria = criteria;
  }
  /**
   * @return Returns the description.
   */
  public String getDescription() {
    return description;
  }
  /**
   * @param description The description to set.
   */
  public void setDescription(String description) {
    this.description = description;
  }
  /**
   * @return Returns the groupFieldID.
   */
  public int getGroupFieldID() {
    return groupFieldID;
  }
  /**
   * @param groupFieldID The groupFieldID to set.
   */
  public void setGroupFieldID(int groupFieldID) {
    this.groupFieldID = groupFieldID;
  }
  /**
   * @return Returns the queryID.
   */
  public int getQueryID() {
    return queryID;
  }
  /**
   * @param queryID The queryID to set.
   */
  public void setQueryID(int queryID) {
    this.queryID = queryID;
  }
  /**
   * @return Returns the queryName.
   */
  public String getQueryName() {
    return queryName;
  }
  /**
   * @param queryName The queryName to set.
   */
  public void setQueryName(String queryName) {
    this.queryName = queryName;
  }
  /**
   * @return Returns the rawQuery.
   */
  public String getRawQuery() {
    return rawQuery;
  }
  /**
   * @param rawQuery The rawQuery to set.
   */
  public void setRawQuery(String rawQuery) {
    this.rawQuery = rawQuery;
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
   * @return Returns the sorts.
   */
  public String getSorts() {
    return sorts;
  }
  /**
   * @param sorts The sorts to set.
   */
  public void setSorts(String sorts) {
    this.sorts = sorts;
  }
  /**
   * @return Returns the viewID.
   */
  public int getViewID() {
    return viewID;
  }
  /**
   * @param viewID The viewID to set.
   */
  public void setViewID(int viewID) {
    this.viewID = viewID;
  }
}
