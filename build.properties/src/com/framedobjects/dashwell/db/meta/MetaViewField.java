/**
 * 
 */
package com.framedobjects.dashwell.db.meta;

/**
 * @author Jens Richnow
 *
 */
public class MetaViewField {
  
  private int fieldID = 0;
  private int viewID = 0;
  private int schemaID = 0;
  private String fieldName = null;
  private String description = null;
  private String sourceTable = null;
  private String sourceField = null;
  private String displayName = null;
  private String namingField = null;
  private String phoneField = null;
  private String largeField = null;
  private int flags = 0;
  private String optionList = null;
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
   * @return Returns the displayName.
   */
  public String getDisplayName() {
    return displayName;
  }
  /**
   * @param displayName The displayName to set.
   */
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }
  /**
   * @return Returns the fieldID.
   */
  public int getFieldID() {
    return fieldID;
  }
  /**
   * @param fieldID The fieldID to set.
   */
  public void setFieldID(int fieldID) {
    this.fieldID = fieldID;
  }
  /**
   * @return Returns the fieldName.
   */
  public String getFieldName() {
    return fieldName;
  }
  /**
   * @param fieldName The fieldName to set.
   */
  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }
  /**
   * @return Returns the flags.
   */
  public int getFlags() {
    return flags;
  }
  /**
   * @param flags The flags to set.
   */
  public void setFlags(int flags) {
    this.flags = flags;
  }
  /**
   * @return Returns the optionList.
   */
  public String getOptionList() {
    return optionList;
  }
  /**
   * @param optionList The optionList to set.
   */
  public void setOptionList(String optionList) {
    this.optionList = optionList;
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
   * @return Returns the sourceField.
   */
  public String getSourceField() {
    return sourceField;
  }
  /**
   * @param sourceField The sourceField to set.
   */
  public void setSourceField(String sourceField) {
    this.sourceField = sourceField;
  }
  /**
   * @return Returns the sourceTable.
   */
  public String getSourceTable() {
    return sourceTable;
  }
  /**
   * @param sourceTable The sourceTable to set.
   */
  public void setSourceTable(String sourceTable) {
    this.sourceTable = sourceTable;
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
  /**
   * @return Returns the largeField.
   */
  public String getLargeField() {
    return largeField;
  }
  /**
   * @param largeField The largeField to set.
   */
  public void setLargeField(String largeField) {
    this.largeField = largeField;
  }
  /**
   * @return Returns the namingField.
   */
  public String getNamingField() {
    return namingField;
  }
  /**
   * @param namingField The namingField to set.
   */
  public void setNamingField(String namingField) {
    this.namingField = namingField;
  }
  /**
   * @return Returns the phoneField.
   */
  public String getPhoneField() {
    return phoneField;
  }
  /**
   * @param phoneField The phoneField to set.
   */
  public void setPhoneField(String phoneField) {
    this.phoneField = phoneField;
  }

}
