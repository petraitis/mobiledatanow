package wsl.mdn.dataview;

import java.io.Serializable;
import java.util.ArrayList;

public class UserLicenses implements Serializable {

	private ArrayList<UserLicense> userLicenses = null;
	private ArrayList<String> userLicenseTypes = new ArrayList<String>();
	private boolean publicGroup = false;
	
	public final static String LICENSE_PERPETUAL 	= "Perpetual";
	public final static String LICENSE_ANNUAL 		= "Annual";
	
	/**
	 * Constructor 
	 */
	public UserLicenses() {
		userLicenses = new ArrayList<UserLicense>();
	}
	/**
	 * Constructor 
	 * @param userLicenses
	 */
	public UserLicenses(ArrayList<UserLicense> userLicenses) {
		setUserLicenses(userLicenses);
	}
	public ArrayList<UserLicense> getUserLicenses() {
		return userLicenses;
	}
	public void setUserLicenses(ArrayList<UserLicense> userLicenses) {
		this.userLicenses = userLicenses;
	}
	
	public void addUserLicense(UserLicense userLicense){
		if (userLicenses == null){
			userLicenses = new ArrayList<UserLicense>();
		}
		userLicenses.add(userLicense);
	}
	
	public void addUserLicenseType(String userLicenseType){
		if (!userLicenseTypes.contains(userLicenseType)){
			userLicenseTypes.add(userLicenseType);
		}
	}
	
	public ArrayList<String> getUserLicenseType() {
		return userLicenseTypes;
	}
	public void setUserLicenseType(ArrayList<String> userLicenseTypes) {
		this.userLicenseTypes = userLicenseTypes;
	}
	public boolean isPublicGroup() {
		return publicGroup;
	}
	public void setPublicGroup(boolean publicGroup) {
		this.publicGroup = publicGroup;
	}	
}
