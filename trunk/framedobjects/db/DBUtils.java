package com.framedobjects.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class DBUtils {
  
  static Logger logger = Logger.getLogger(DBUtils.class.getName());
  
  protected static void closeConnection(Connection connection){
    try {
      connection.close();
    } catch (SQLException sqlex){
        logger.error("Cannot close DB connection.");
        throw new RuntimeException(sqlex);
    }
  }
  
  /**
   * @param statement
   */
  protected static void closeStatement(PreparedStatement statement){
    try {
      statement.close();
    } catch (SQLException sqlex){
        logger.error("Cannot close prepared statement.");
        throw new RuntimeException(sqlex);
    }
  }
}
