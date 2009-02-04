package wsl.mdn.dataview;

import java.io.Serializable;
import java.util.HashMap;

public class ResultWrapper implements Serializable {

	private Object object = null;
	private String registeredEmailAddress = null;
	private Boolean publicGroupBoolean = null;
	private int availablePublicMessages = 0;
	private int installationReferenceNumber = 0;
	private String secureLoginLink = null;
	private String errorMsg = null;
	private String errorMsg2 = null;
	private HashMap<String, String> installations = null;
	/**
	 * empty constructor	 *
	 */
	public ResultWrapper(){
		
	}
	/**
	 * Constructor with only one error message
	 * @param object
	 * @param errorMsg
	 */
	public ResultWrapper(Object object, String errorMsg) {
		setObject(object);
		setErrorMsg(errorMsg);
	}
	/**
	 * Constructor with two error messages
	 * @param object
	 * @param errorMsg
	 * @param errorMsg2
	 */
	public ResultWrapper(Object object, String errorMsg, String errorMsg2) {
		setObject(object);
		setErrorMsg(errorMsg);
		setErrorMsg2(errorMsg2);
	}
	/**
	 * 
	 * @param object
	 * @param publicGroupObject
	 * @param errorMsg
	 */
	public ResultWrapper(Object object, String registeredEmailAddress, Boolean publicGroupObject, int availablePublicMessages, int installationReferenceNumber, String errorMsg) {
		setObject(object);
		setRegisteredEmailAddress(registeredEmailAddress);
		setPublicGroupBoolean(publicGroupObject);
		setAvailablePublicMessages(availablePublicMessages);
		setInstallationReferenceNumber(installationReferenceNumber);
		setErrorMsg(errorMsg);
	}
	/**
	 * 
	 * @param object
	 * @param publicGroupObject
	 * @param errorMsg
	 * @param errorMsg2
	 */
	public ResultWrapper(Object object, String registeredEmailAddress, Boolean publicGroupObject, String errorMsg, String errorMsg2) {
		setObject(object);
		setRegisteredEmailAddress(registeredEmailAddress);
		setPublicGroupBoolean(publicGroupObject);
		setErrorMsg(errorMsg);
		setErrorMsg2(errorMsg2);
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getErrorMsg2() {
		return errorMsg2;
	}
	public void setErrorMsg2(String errorMsg2) {
		this.errorMsg2 = errorMsg2;
	}
	public Object getObject() {
		return object;
	}
	public void setObject(Object object) {
		this.object = object;
	}
	public Boolean getPublicGroupBoolean() {
		return publicGroupBoolean;
	}
	public void setPublicGroupBoolean(Boolean publicGroupBoolean) {
		this.publicGroupBoolean = publicGroupBoolean;
	}
	public String getRegisteredEmailAddress() {
		return registeredEmailAddress;
	}
	public void setRegisteredEmailAddress(String registeredEmailAddress) {
		this.registeredEmailAddress = registeredEmailAddress;
	}
	public String getSecureLoginLink() {
		return secureLoginLink;
	}
	public void setSecureLoginLink(String secureLoginLink) {
		this.secureLoginLink = secureLoginLink;
	}
	public int getAvailablePublicMessages() {
		return availablePublicMessages;
	}
	public void setAvailablePublicMessages(int availablePublicMessages) {
		this.availablePublicMessages = availablePublicMessages;
	}
	public int getInstallationReferenceNumber() {
		return installationReferenceNumber;
	}
	public void setInstallationReferenceNumber(int installationReferenceNumber) {
		this.installationReferenceNumber = installationReferenceNumber;
	}
	public HashMap<String, String> getInstallations() {
		return installations;
	}
	public void setInstallations(HashMap<String, String> installations) {
		this.installations = installations;
	}
}
