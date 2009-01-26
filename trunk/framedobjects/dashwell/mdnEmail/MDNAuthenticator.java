package com.framedobjects.dashwell.mdnEmail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class MDNAuthenticator  extends Authenticator {
/*	private String userName;
	private String password;
	
	public PasswordAuthentication getPasswordAuthentication(){
		PasswordAuthentication p = new PasswordAuthentication(getUserName(),getPassword());
		return p;
	}
	public String getUserName(){
		return userName;
	}
	public void setUserName(String userName){
		this.userName = userName;
	}
	public String getPassword(){
		return password;
	}
	public void setPassword(String password){
		this.password = password;
	}
*/	
	private String username;
	private String password;
	
     public MDNAuthenticator() {
    	 super();
     }

     public MDNAuthenticator(String username, String password) {
    	 super();
    	 this.username = username;
    	 this.password = password;
    }
    
    public PasswordAuthentication getPasswordAuthentication() {
    	return new PasswordAuthentication(username, password);
    }
}

