package com.framedobjects.dashwell.biz;

import java.util.List;

public class User {
  
  private int userID = -1;
  private String username = null;
  private String password = null;
  private String email = null;
  private String mobile = null;
  private String details = null;
  private String queryUserID = null;
  private List<Group> allGroups = null;
  private List<IMConactDetailes> allIMConacts = null;
  
  public User(){} 
  
  public User(int userID, String username, String password, String email,
  						String mobile, String detail, String queryUserID){
    this.username = username;
    this.password = password;
    this.email = email;
    this.mobile = mobile;
    this.userID = userID;
    this.details = detail;
    this.queryUserID = queryUserID;
  }
  
  /**
   * @return Returns the password.
   */
  public String getPassword() {
    return password;
  }
  /**
   * @return Returns the userID.
   */
  public int getUserID() {
    return userID;
  }
  /**
   * @return Returns the username.
   */
  public String getUsername() {
    return username;
  }
  
  public String toString(){
    return this.getClass().getName() +  "[" + this.getUserID() + ": " + 
            this.getUsername() + "; " + this.getPassword() + "; " + 
            this.getEmail() + "; " + this.getMobile() + "; " + 
            this.getDetails() + "; " + this.getQueryUserID() + "]";
  }

  /**
   * @return Returns the allGroups.
   */
  public List<Group> getAllGroups() {
    return allGroups;
  }

  /**
   * @param allGroups The allGroups to set.
   */
  public void setAllGroups(List<Group> allGroups) {
    this.allGroups = allGroups;
  }

	public String getDetails() {
		if (details == null){
			details = "";
		}
		return details;
	}

	public String getQueryUserID() {
		if (queryUserID == null){
			queryUserID = "";
		}
		return queryUserID;
	}

	public String getEmail() {
		if (email == null){
			email = "";
		}
		return email;
	}

	public String getMobile() {
		if (mobile == null){
			mobile = "";
		}
		return mobile;
	}

	/**
	   * @return Returns the allIMConnection.
	   */
	  public List<IMConactDetailes> getAllIMConacts() {
	    return allIMConacts;
	  }

	  /**
	   * @param allIMConnection The allIMConnection to set.
	   */
	  public void setAllIMConnections(List<IMConactDetailes> allIMConacts) {
	    this.allIMConacts = allIMConacts;
	  }

}
