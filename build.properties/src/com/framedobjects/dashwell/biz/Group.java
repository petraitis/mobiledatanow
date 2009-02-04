/*
 * Created on 7/08/2005
 *
 */
package com.framedobjects.dashwell.biz;

import java.util.List;

/**
 * @author Richmow
 *
 */
public class Group {

	private int groupID = 0;
	private String name = null;
	private String description = null;
	private List allUsers = null;
	private String selection = null;
	
	public Group(){}
	
	public Group(int groupID, String groupName){
		this.groupID = groupID;
		this.name = groupName;
	}
	
	/**
	 * @return Returns the allUsers.
	 */
	public List getAllUsers() {
		return allUsers;
	}
	/**
	 * @param allUsers The allUsers to set.
	 */
	public void setAllUsers(List allUsers) {
		this.allUsers = allUsers;
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
	 * @return Returns the groupID.
	 */
	public int getGroupID() {
		return groupID;
	}
	/**
	 * @param groupID The groupID to set.
	 */
	public void setGroupID(int groupID) {
		this.groupID = groupID;
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
	/**
	 * @return Returns the selection.
	 */
	public String getSelection() {
		return selection;
	}
	/**
	 * @param selection The selection to set.
	 */
	public void setSelection(String selection) {
		this.selection = selection;
	}
    
    public String toString(){
      return this.getClass().getName() + "[" + this.getGroupID() + ": " + 
              this.getName() + "; " + this.getDescription() + "]";
    }
}
