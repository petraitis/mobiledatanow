/**
 * 
 */
package com.framedobjects.dashwell.db.meta;

import java.util.List;

/**
 * Represents a data view.
 * @author Jens Richnow
 *
 */
public class MetaView {

  private int viewID = 0;
  private int connectionID = 0;
  private String viewName = null;
  private String description = null;
  private List mappedFields = null;
  private List<MetaViewField> allFields = null;
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
   * @return Returns the schemaID.
   */
  public int getConnectionID() {
    return connectionID;
  }
  /**
   * @param schemaID The schemaID to set.
   */
  public void setConnectionID(int schemaID) {
    this.connectionID = schemaID;
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
   * @return Returns the viewName.
   */
  public String getViewName() {
    return viewName;
  }
  /**
   * @param viewName The viewName to set.
   */
  public void setViewName(String viewName) {
    this.viewName = viewName;
  }
  /**
   * @return Returns the mappedFields.
   */
  public List getMappedFields() {
    return mappedFields;
  }
  /**
   * @param mappedFields The mappedFields to set.
   */
  public void setMappedFields(List mappedFields) {
    this.mappedFields = mappedFields;
  }
	public List<MetaViewField> getAllFields()
	{
		return allFields;
	}
	public void setAllFields(List<MetaViewField> allFields)
	{
		this.allFields = allFields;
	}
}
