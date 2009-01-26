package com.framedobjects.dashwell.db.meta;

import java.util.List;

/**
 * @author Jens Richnow
 *
 */
public class MetaTable implements Comparable {

	private int tableID = 0;
	private int connectionID = 0;
	private String name = null;
	private String description = null;
	private String type = null;
	private boolean checked = false; // TODO Still required?
	private List<MetaField> allFields = null;
	
	public MetaTable(int tableID, int connectionID, String type, String name, String description){
		this.tableID = tableID;
		this.connectionID = connectionID;
		this.type = type;
		this.name = name;
		this.description = description;
	}
	
	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description == null ? "" : description;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name == null ? "" : name;
	}
	/**
	 * @return Returns the tableID.
	 */
	public int getTableID() {
		return tableID;
	}
	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return type == null ? "" : type;
	}
	/**
	 * @return Returns the checked.
	 */
	public boolean isChecked() {
		return checked;
	}
	/**
	 * @param checked The checked to set.
	 */
	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object object) {
		MetaTable checkTable = (MetaTable)object;
		String checkTableName = checkTable.getName();
		return this.name.compareTo(checkTableName);
	}

  /**
   * @return Returns the connectionID.
   */
  public int getConnectionID() {
    return connectionID;
  }

  /**
   * @return Returns the allFields.
   */
  public List<MetaField> getAllFields() {
    return allFields;
  }

  /**
   * @param allFields The allFields to set.
   */
  public void setAllFields(List<MetaField> allFields) {
    this.allFields = allFields;
  }
  
  public String toString(){
  	return "[" + this.getName() +", type:" + this.getType() + "]";
  }
}
