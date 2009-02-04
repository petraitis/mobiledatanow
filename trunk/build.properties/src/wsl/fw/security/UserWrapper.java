package wsl.fw.security;

public class UserWrapper {

	private User loginUser = null;
	private String errorMsg = null;
	private String errorMsg2 = null;
	public UserWrapper(User loginUser, String errorMsg, String errorMsg2) {
		setLoginUser(loginUser);
		setErrorMsg(errorMsg);
		setErrorMsg2(errorMsg2);
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public User getLoginUser() {
		return loginUser;
	}
	public void setLoginUser(User loginUser) {
		this.loginUser = loginUser;
	}
	public String getErrorMsg2() {
		return errorMsg2;
	}
	public void setErrorMsg2(String errorMsg2) {
		this.errorMsg2 = errorMsg2;
	}

}
