package com.framedobjects.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Represents the wrapper for running multiple queries using one connection only.
 * @author Jens Richnow
 *
 */
public class SQLMultipleRequest {
  
  protected static Logger logger = Logger.getLogger(SQLMultipleRequest.class.getName());
  private Connection conn = null;
  
  /**
   * Use this method to open the connection to the database. This is the first
   * method called when running multiple queries against a database. Don't
   * forget to call <code>finish()</code> to close the connection after your
   * last query.
   */
  public void start(){
    try {
      if (conn == null){
        conn = ConnectionPool.getInstance().getConnection();  
      } else {
        logger.info("Please use ::start() only once! Using existing connection.");
      }
    } catch (SQLException sqlex){
        logger.error("SQLException while getting connection: " + sqlex.getMessage());
    }
  }
  
  /**
   * <p>Sets the auto commit state for the connection to <code>false</code>. Use
   * this in conjunction with <code>commit()</code> if you need a transaction
   * state for multiple database updates.</p> 
   * <p>Call this method after <code>start()</code> and before all SQL queries. 
   * Don't forget to call <code>commit()</code> after all SQL queries and before
   * <code>finish()</code> which closes the connection.</p>
   */
  public void autoCommitOff(){
    try {
      if (conn != null){
        if (conn.getAutoCommit()){
          conn.setAutoCommit(false);
        } else {
          logger.info("Auto commmit is already turned off.");
        }
      } else {
          logger.info("No connection open to apply ::autoCommitOff().");
      }
    } catch (SQLException sqlex){
        logger.error("SQLException in autoCommitOff(): " + sqlex.getMessage());
    }
  }
  
  public Object findObject(SQLQuery singleQuery){
    return this.findObject(singleQuery, new Object[]{});
  }
  
  public Object findObject(SQLQuery singleQuery, boolean param){
    return this.findObject(singleQuery, new Object[]{new Boolean(param)});
  }
  
  public Object findObject(SQLQuery singleQuery, int param){
    return this.findObject(singleQuery, new Object[]{new Integer(param)});
  }
  
  public Object findObject(SQLQuery singleQuery, int param1, int param2){
    return this.findObject(singleQuery, new Object[]{new Integer(param1), new Integer(param2)});
  }
  
  public Object findObject(SQLQuery singleQuery, long param){
    return this.findObject(singleQuery, new Object[]{new Long(param)});
  }
  
  public Object findObject(SQLQuery singleQuery, long param1, long param2){
    return this.findObject(singleQuery, new Object[]{new Long(param1), new Long(param2)});
  }
  
  public Object findObject(SQLQuery singleQuery, String param){
    return this.findObject(singleQuery, new Object[]{param});
  }
  
  public Object findObject(SQLQuery singleQuery, String param1, String param2){
    return this.findObject(singleQuery, new Object[]{param1, param2});
  }
  
  public Object findObject(SQLQuery singleQuery, String param1, int param2){
    return this.findObject(singleQuery, new Object[]{param1, new Integer(param2)});
  }
  
  /**
   * <p>Use to execute a single query to retrieve a single object from the 
   * database within a cycle of multiple queries. All other convenient methods
   * fall back to this one.</p> 
   * @param singleQuery
   * @param params
   * @return
   */
  public Object findObject(SQLQuery singleQuery, Object[] params){
    Object obj = null;
    try {
      if (conn != null){
        obj = singleQuery.findObject(conn, params);
      } else {
          logger.info("Cannot run query, no connection is present. Use ::start() " +
                      "before calling this method to initiate the connection.");
      }
    } catch (SQLException sqlex){
        logger.error("SQLException in findObject()" + sqlex.getMessage());
        DBUtils.closeConnection(conn);
    } catch (SQLParameterException sqlpex){
        logger.error("SQLParameterException in findObject()" + sqlpex.getMessage());
        DBUtils.closeConnection(conn);
    } 
    return obj;
  }
  
  public List execute(SQLQuery singleQuery){
    return this.execute(singleQuery, new Object[]{});
  }
  
  public List execute(SQLQuery singleQuery, boolean param){
    return this.execute(singleQuery, new Object[]{new Boolean(param)});
  }
  
  public List execute(SQLQuery singleQuery, int param){
    return this.execute(singleQuery, new Object[]{new Integer(param)});
  }
  
  public List execute(SQLQuery singleQuery, int param1, int param2){
    return this.execute(singleQuery, new Object[]{new Integer(param1), new Integer(param2)});
  }
  
  public List execute(SQLQuery singleQuery, long param){
    return this.execute(singleQuery, new Object[]{new Long(param)});
  }
  
  public List execute(SQLQuery singleQuery, long param1, long param2){
    return this.execute(singleQuery, new Object[]{new Long(param1), new Long(param2)});
  }
  
  public List execute(SQLQuery singleQuery, String param){
    return this.execute(singleQuery, new Object[]{param});
  }
  
  public List execute(SQLQuery singleQuery, String param1, String param2){
    return this.execute(singleQuery, new Object[]{param1, param2});
  }
  
  public List execute(SQLQuery singleQuery, String param1, int param2){
    return this.execute(singleQuery, new Object[]{param1, new Integer(param2)});
  }
  
  public List execute(SQLQuery singleQuery, Object[] params){
    List list = null;
    try {
      if (conn != null){
        list = singleQuery.execute(conn, params);
      } else {
          logger.info("Cannot run query, no connection is present. Use ::start() " +
                      "before calling this method to initiate the connection.");
      }
    } catch (SQLException sqlex){
        logger.error("SQLException in findObject()" + sqlex.getMessage());
        DBUtils.closeConnection(conn);
    } catch (SQLParameterException sqlpex){
        logger.error("SQLParameterException in findObject()" + sqlpex.getMessage());
        DBUtils.closeConnection(conn);
    }
    return list;
  }
  
  public int update(SQLUpdate update){
    return this.update(update, new Object[]{});
  }
  
  public int update(SQLUpdate update, boolean param){
    return this.update(update, new Object[]{new Boolean(param)});
  }
  
  public int update(SQLUpdate update, int param){
    return this.update(update, new Object[]{new Integer(param)});
  }
  
  public int update(SQLUpdate update, int param1, int param2){
    return this.update(update, new Object[]{new Integer(param1), new Integer(param2)});
  }
  
  public int update(SQLUpdate update, long param){
    return this.update(update, new Object[]{new Long(param)});
  }
  
  public int update(SQLUpdate update, long param1, long param2){
    return this.update(update, new Object[]{new Long(param1), new Long(param2)});
  }
  
  public int update(SQLUpdate update, String param){
    return this.update(update, new Object[]{param});
  }
  
  public int update(SQLUpdate update, String param1, String param2){
    return this.update(update, new Object[]{param1, param2});
  }
  
  public int update(SQLUpdate update, String param1, int param2){
    return this.update(update, new Object[]{param1, new Integer(param2)});
  }
  
  public int update(SQLUpdate sqlUpdate, Object[] params){
    int result = -1;
    try {
      if (conn != null){
        result = sqlUpdate.update(conn, params);
      } else {
          logger.info("Cannot run update, no connection is present. Use ::start() " +
                      "before calling this method to initiate the connection.");
      }
    } catch (SQLException sqlex){
        logger.error("SQLException in ::update()" + sqlex.getMessage());
        DBUtils.closeConnection(conn);
    } catch (SQLParameterException sqlpex){
        logger.error("SQLParameterException in ::update()" + sqlpex.getMessage());
        DBUtils.closeConnection(conn);
    }
    return result;
  }
  
  /**
   * <p>Use this method to commit a multiple database transactions. You should 
   * have called <code>autoCommitOff()</code> before issueing the database
   * statements. This method implicitely set the auto commit state of the
   * connection to <code>true</code>.
   */
  public void commit(){
    try {
      if (conn != null){
        if (!conn.getAutoCommit()){
          conn.commit();
          conn.setAutoCommit(true);
        } else {
            logger.info("Autocommit state is already true.");
        }
      } else {
          logger.info("No connection open to call ::commit()");
      }
    } catch (SQLException sqlex){
        logger.error("SQLException in commit()" + sqlex.getMessage());
    }
  }
  
  /**
   * Use this method to close the connection after running all queries.
   */
  public void finish(){
    try {
      if (conn != null){
        conn.close();
      } else {
        logger.info("No connection open to close.");
      }
    } catch (SQLException sqlex){
        logger.error("SQLException while closing connection: " + sqlex.getMessage());
        throw new RuntimeException(sqlex);
    }
  }
  
  public int getLastID(){
  	int id = 0;
  	try {
      if (conn != null){
		  	Statement lastID = conn.createStatement();
		    ResultSet rsLastID = lastID.executeQuery("SELECT LAST_INSERT_ID()");
		    if (rsLastID.next()) {
		      id = rsLastID.getInt(1);
		    }
		    rsLastID.close();
		    lastID.close();
      }
  	} catch (SQLException sqlex){
				logger.error("SQLException in getLastID(): " + sqlex.getMessage());
		    throw new RuntimeException(sqlex);
  	}
    return id;
  }
}
