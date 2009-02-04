package com.framedobjects.dashwell.db.meta;

/**
 * @author Jens Richnow
 *
 */
public class MetaRelation {

	private int relationID = 0;
	private String leftTable = null;
	private String leftField = null;
	private String rightTable = null;
	private String rightField = null;
	private String qualifiedName = null;
  private int connectionID = 0;
	
	public MetaRelation(int relationID, int connectionID, String relationName, 
			String leftTable, String leftField, String rightTable, String rightField){
		this.relationID = relationID;
		this.connectionID = connectionID;
		this.leftTable = leftTable;
		this.leftField = leftField;
		this.rightTable = rightTable;
		this.rightField = rightField;
		if (relationName == null){
			this.qualifiedName = leftTable + "." + leftField + " --> " + rightTable + "." + rightField;
		} else {
			this.qualifiedName = relationName;
		}
	}
	
	public String getQualifiedName(){
		return this.qualifiedName;
	}
	
	/**
	 * @return Returns the leftField.
	 */
	public String getLeftField() {
		return leftField;
	}
	/**
	 * @return Returns the leftTable.
	 */
	public String getLeftTable() {
		return leftTable;
	}
	/**
	 * @return Returns the relationId.
	 */
	public int getRelationID() {
		return relationID;
	}
	/**
	 * @return Returns the rightField.
	 */
	public String getRightField() {
		return rightField;
	}
	/**
	 * @return Returns the rightTable.
	 */
	public String getRightTable() {
		return rightTable;
	}
	/**
   * @return Returns the connectionID.
   */
  public int getConnectionID() {
    return connectionID;
  }
}
