package com.framedobjects.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLUpdate extends SQLAbstractUpdate {
  
  public final static int RESULT_UNKNOWN_ERROR = -1; 
  
  public int update(Object[] params) {
    int result = RESULT_UNKNOWN_ERROR;
    Connection conn = null;
    PreparedStatement statement = null;
    try {
      // Get the connection from the connection pool.
      conn = ConnectionPool.getInstance().getConnection();
      // Set the prepared statement.
      statement = conn.prepareStatement(this.sql);
      // Add the parameters, if present.
      setParameters(params, statement);
      result = statement.executeUpdate();
    } catch (SQLException sqlex){
        logger.error("SQLException in update(Object[]): " + sqlex.getMessage());
    } catch (SQLParameterException sqlpex){
        logger.error("SQLParameterException in update(Object[]): " + sqlpex.getMessage());
    } finally {
        DBUtils.closeStatement(statement);
        DBUtils.closeConnection(conn);
    }
    return result;
  }
  
  public int update(Connection connection, Object[] params) 
            throws SQLException, SQLParameterException{
    int result = RESULT_UNKNOWN_ERROR;
    // Set the prepared statement.
    PreparedStatement statement = connection.prepareStatement(this.sql);
    // Add the parameters, if present.
    setParameters(params, statement);
    result = statement.executeUpdate();
    DBUtils.closeStatement(statement);
    return result;
  }
}
