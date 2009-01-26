package com.framedobjects.dashwell.biz;

import org.apache.log4j.Logger;

import wsl.fw.exception.MdnException;
import wsl.fw.security.UserWrapper;

import com.framedobjects.dashwell.db.DataAgentFactory;
import com.framedobjects.dashwell.db.IDataAgent;

public class LoginManager {
  
  private static Logger logger = Logger.getLogger(LoginManager.class.getName());

  public UserWrapper getLoginUser(String username, String password) throws MdnException{
    logger.info("verifying login for: " + username + "/" + password);
    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
    return dataAgent.getLoginUser(username, password);
  }
  public UserWrapper getLoginUser(String username) throws MdnException{
    logger.info("verifying login for: " + username);
    IDataAgent dataAgent = DataAgentFactory.getDataInterface();
    return dataAgent.getLoginUser(username);
  }  
}
