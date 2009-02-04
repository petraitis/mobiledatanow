package com.framedobjects.dashwell.handlers;

import java.util.List;

import org.jdom.Element;

import wsl.fw.exception.MdnException;
import wsl.fw.security.Group;
import wsl.fw.security.User;
import wsl.fw.security.UserWrapper;
import wsl.mdn.mdnim.IMConnection;

import com.framedobjects.dashwell.db.DataAgentFactory;
import com.framedobjects.dashwell.db.IDataAgent;
import com.framedobjects.dashwell.utils.MessageConstants;
import com.framedobjects.dashwell.utils.XmlFormatter;

public class UserDataHandler {

	public String processCheckNewUser(String file, User user){
//		try {
			// 	Validate username and password.
			if (user.getName() == null){
				return MessageConstants.getMessage(file, MessageConstants.MISSING_USERNAME);
			}
			if (user.getPassword() == null){
				return MessageConstants.getMessage(file, MessageConstants.MISSING_PASSWORD);
			}
			// Check with database.
			IDataAgent dataAgent = DataAgentFactory.getDataInterface();

			/*			User checkUser;
				checkUser = dataAgent.getLoginUser(user.getName(), user.getPassword());
			if (checkUser != null){
				return "User exists already.";
			}
*/	
			try {
				if (dataAgent.getUserByName(user.getName()) != null){
					return MessageConstants.getMessage(file, MessageConstants.DUPLICATE_USERNAME);
				}
			} catch (MdnException e) {
				e.printStackTrace();
			}
			return null;
			// Now create the new user.
//			int newUserId = dataAgent.saveUser(user);
//			if (newUserId > 0){
//				return String.valueOf(newUserId);
//			}
//		} catch (MdnException e) {
//			e.printStackTrace();
//			return "Database error";
//		}
//		return "Database error";
	}
	
	public String processEditUser(String file, User user){
		try {
			// Validate username and password.
			if (user.getName() == null){
				return MessageConstants.getMessage(file, MessageConstants.MISSING_USERNAME);
			}
			if (user.getPassword() == null){
				return MessageConstants.getMessage(file, MessageConstants.MISSING_PASSWORD);
			}
			// Check with database.
			IDataAgent dataAgent = DataAgentFactory.getDataInterface();
			/*UserWrapper checkUser;
			try {
				checkUser = dataAgent.getLoginUser(user.getName(), user.getPassword());
			} catch (MdnException e) {
				e.printStackTrace();
				return "Database error";
			} 
			if (checkUser.getLoginUser() != null && checkUser.getLoginUser().getId() != user.getId()){
				return "User exists already.";
			}*/
			
			User checkUserWithName = dataAgent.getUserByName(user.getName()); 
			if (checkUserWithName != null && checkUserWithName.getId() != user.getId()){
				return MessageConstants.getMessage(file, MessageConstants.DUPLICATE_USERNAME);
			}
			// Now create the new user.
			if (dataAgent.saveUser(user) > 0){
				return MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL);
			}
		} catch (MdnException e) {
			e.printStackTrace();
		}
		return MessageConstants.getMessage(file, MessageConstants.DB_ERROR);
	}
	
	public String processRecycleUser(String file, int userID){
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		try {
			List<User> adminUsers = dataAgent.getAdminUsers();
			if(adminUsers.size()>1){
				if (dataAgent.markUserAsRecycled(userID) > 0){
					return MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL);
				} else {
					return MessageConstants.getMessage(file, MessageConstants.DB_ERROR);
				}
			}else if(adminUsers.get(0)!= null && ((User)(adminUsers.get(0))).getId() == userID)
				return MessageConstants.getMessage(file, MessageConstants.NEED_ADMIN);
				
		} catch (MdnException e) {
			e.printStackTrace();
			return MessageConstants.getMessage(file, MessageConstants.DB_ERROR);
		}
		return MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL);
	}
	
	public String processClearUser(String file, int userID){
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		try {
			if (dataAgent.markUserAsCleared(userID) > 0){
				return MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL);
			} else {
				return MessageConstants.getMessage(file, MessageConstants.DB_ERROR);
			}
		} catch (MdnException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return MessageConstants.getMessage(file, MessageConstants.DB_ERROR);
		}
	}
	
	public String processDeleteUser(String file, int userID){
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		try {
			if (dataAgent.deleteUser(userID) > 0){
				return MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL);
			} else {
				return MessageConstants.getMessage(file, MessageConstants.DB_ERROR);
			}
		} catch (MdnException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return MessageConstants.getMessage(file, MessageConstants.DB_ERROR);
		}
	}
	
	public String processNewGroup(String file, Group group){
		try {		
			// Validate groupname.
			if (group.getName() == null){
				return MessageConstants.getMessage(file, MessageConstants.MISSING_GROUP_NAME);
			}
			// Check with database.
			IDataAgent dataAgent = DataAgentFactory.getDataInterface();
			Group checkGroup = null;//dataAgent.getGroupByName(group.getName()); 
			if (checkGroup != null){
				return MessageConstants.getMessage(file, MessageConstants.DUPLICATE_GROUP);
			}
			// Now create the new Group.
			if (dataAgent.saveGroup(group) > 0){
				return MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL);
			}
			return MessageConstants.getMessage(file, MessageConstants.DB_ERROR);
		} catch (MdnException e) {
			e.printStackTrace();
			return MessageConstants.getMessage(file, MessageConstants.DB_ERROR);
		}
		
	}
	
	public String processRecycleGroup(String file, int groupID){
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		try {
			if (dataAgent.markGroupAsRecycled(groupID) > 0){
				return MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL);
			} else {
				return MessageConstants.getMessage(file, MessageConstants.DB_ERROR);
			}
		} catch (MdnException e) {
			e.printStackTrace();
			return MessageConstants.getMessage(file, MessageConstants.DB_ERROR);
		}
	}
	
	public String processClearGroup(String file, int groupID){
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		try {
			if (dataAgent.markGroupAsCleared(groupID) > 0){
				return MessageConstants.getMessage(file, MessageConstants.SUCCESSFUL);
			} else {
				return MessageConstants.getMessage(file, MessageConstants.DB_ERROR);
			}
		} catch (MdnException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return MessageConstants.getMessage(file, MessageConstants.DB_ERROR);
		}
	}
	
	public String processDeleteGroup(String file, int groupID){
		IDataAgent dataAgent = DataAgentFactory.getDataInterface();
		try {
			if (dataAgent.deleteGroup(groupID) > 0){
				return "OK";
			} else {
				return MessageConstants.getMessage(file, MessageConstants.DB_ERROR);
			}
		} catch (MdnException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return MessageConstants.getMessage(file, MessageConstants.DB_ERROR);
		}
	}
}
