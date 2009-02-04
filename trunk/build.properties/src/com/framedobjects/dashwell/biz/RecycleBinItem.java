package com.framedobjects.dashwell.biz;

/**
 * Represents an item in the recycle bin. Each item consists of a name, type 
 * id, whereby the id and type is used to correctly identify a row in the 
 * database.
 * @author Jens Richnow
 *
 */
public class RecycleBinItem {

	private String name = null;
	private int id = 0;
	private String type = null;
	private int projectId = 1;
	
	public RecycleBinItem(int id, String type, String name, int projectId){
		this.id = id;
		this.type = type;
		this.name = name;
		this.projectId = projectId;
	}
	
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getType() {
		return type;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	
	
}
