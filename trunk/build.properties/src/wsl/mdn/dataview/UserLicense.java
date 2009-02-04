package wsl.mdn.dataview;

import java.io.Serializable;
import java.util.ArrayList;

public class UserLicense implements Serializable {

	private String type = null;
	private int numberOfUsers = 0;
	private String expiryDate= null;
	
	/**
	 * Constructor 
	 * @param numberOfUsers
	 * @param expiryDate
	 */
	public UserLicense(String type, int numberOfUsers, String expiryDate) {
		setType(type);
		setNumberOfUsers(numberOfUsers);
		setExpiryDate(expiryDate);
	}
	public String getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}
	public int getNumberOfUsers() {
		return numberOfUsers;
	}
	public void setNumberOfUsers(int numberOfUsers) {
		this.numberOfUsers = numberOfUsers;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
