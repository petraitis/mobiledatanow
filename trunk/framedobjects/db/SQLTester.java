package com.framedobjects.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.framedobjects.dashwell.biz.Group;
import com.framedobjects.dashwell.biz.User;

public class SQLTester {
  
  static Logger logger = Logger.getLogger(SQLTester.class.getName());
  private static boolean IS_INITIALISED = false;

  public SQLTester(){
    try {
      if (!IS_INITIALISED){
        ConnectionPool.init();
        IS_INITIALISED = true;
      } 
    } catch (Exception e) {
      logger.warn("Exception initialising connection pool. " + e.getMessage());
    }
  }
  
  public User getLoginUser(String username, String password){
    User user = null;
    UserQuery query = new UserQuery();
    query.setSQL("select * from users where name = ? and password = ?");
    query.declareParameter(new SQLParameter(Types.VARCHAR));
    query.declareParameter(new SQLParameter(Types.VARCHAR));
    user = (User)query.findObject(username, password);
    return user;
  }
  
  public List getAllUsers(){
    List list = null;
    UserQuery query = new UserQuery();
    query.setSQL("select * from users");
    list = query.execute();
    return list;
  }
  
  public List getAllUsersWithGroups(){
    SQLMultipleRequest multiRequest = new SQLMultipleRequest();
    // Open the connection.
    multiRequest.start();
    
    // Get the specific user first.
    UserQuery query1 = new UserQuery();
    query1.setSQL("select * from users");
    List users = multiRequest.execute(query1);
    User user = null;
    Iterator iter = users.iterator();
    while (iter.hasNext()) {
      user = (User) iter.next();
      GroupQuery groupQuery = new GroupQuery();
      groupQuery.setSQL("select groups.* from groups, user_groups "
          + "where user_groups.user_id = ? "
          + "and user_groups.group_id = groups.group_id");
      groupQuery.declareParameter(new SQLParameter(Types.INTEGER));
      List groups = multiRequest.execute(groupQuery, user.getUserID());
      user.setAllGroups(groups);
    }
    
    // Now cleanup.
    multiRequest.finish();
    
    return users;
  }
  
  class UserQuery extends SQLQuery{
    
    public User mapRow(ResultSet rs) throws SQLException{
      User user = null;
      int userID = rs.getInt("user_id");
      String username = rs.getString("name");
      String password = rs.getString("password");
      user = new User(userID, username, password, null, null, null, null);
      logger.debug(user);
      return user;
    }
  }
  
  class GroupQuery extends SQLQuery{
    
    public Group mapRow(ResultSet rs) throws SQLException{
      Group group = new Group();
      group.setGroupID(rs.getInt("group_id"));
      group.setName(rs.getString("name"));
      group.setDescription(rs.getString("description"));
      logger.debug(group);
      return group;
    }
  }
}
