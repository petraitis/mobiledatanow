/**
 * 
 */
package com.framedobjects.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * Superclass for all types of SQL interactions (query, update).
 * @author Richmow
 *
 */
public abstract class SQLOperation {

  protected static Logger logger = Logger.getLogger(SQLQuery.class.getName());
  
  /** The SQL string for this query. */
  protected String sql = null;
  /** The parameter types for this query. */
  //protected Vector<SQLParameter> paramTypes = new Vector<SQLParameter>();
  protected Vector paramTypes = new Vector();
  /**
   * @param params
   * @param statement
   * @throws SQLException
   */
  protected void setParameters(Object[] params, PreparedStatement statement) 
          throws SQLException, SQLParameterException {
    int paramTypesSize = paramTypes.size();
    // Throw exception if no parameters have been passed and the param type size
    // is larger then one.
    if ((params == null || params.length == 0) && paramTypesSize >= 1){
      throw new SQLParameterException("No parameters have been passed but parameter types are declared.");
    }
    if (params.length > 0){
      // Throw exception if sizes of parameters and their types is not equal.
      if (paramTypesSize < params.length){
        throw new SQLParameterException("Not all parameter types have been declared.");
      }
      if (paramTypesSize > params.length){
        throw new SQLParameterException("Not all parameters are declared.");
      }
    }
    if (paramTypesSize > 0){
      try {
        SQLParameter paramType = null;
        for (int i = 0; i < paramTypesSize; i++){
          //paramType = paramTypes.elementAt(i);
          paramType = (SQLParameter)paramTypes.elementAt(i);
          // Add all the types present.
          switch (paramType.getType()){
            case Types.BIGINT:
              statement.setLong(i+1, ((Long)params[i]).longValue());
              break;
            case Types.BOOLEAN:
              statement.setBoolean(i+1, ((Boolean)params[i]).booleanValue());
              break;
            case Types.INTEGER:
              statement.setInt(i+1, ((Integer)params[i]).intValue());
              break;
            case Types.VARCHAR:
              statement.setString(i+1, (String)params[i]);
              break;
            default:
              throw new SQLParameterException("The parameter type '" + paramType.getType() + "' is not implemented.");
          }
        }
      } catch (Exception e){
          e.printStackTrace();
          throw new SQLParameterException("thrown in ::setParameters(Object[], PreparedStatement)" + e.getMessage());
      }
    }
  }
  
  /**
   * Sets a parameter for the current query. Set the parameters in the same order
   * as defined in the SQL string.
   * @param parameter
   */
  public void declareParameter(SQLParameter parameter) {
    paramTypes.add(parameter);
  }
  /**
   * Sets the SQL String for this query.
   * @param sqlString The SQL string allowing parameters in the form of '?'.
   */
  public void setSQL(String sqlString) {
    this.sql = sqlString;
  }
  
  /**
   * Get the SQL statement for this query.
   * @return Returns the sql.
   */
  public String getSQL() {
    return sql;
  }

}
