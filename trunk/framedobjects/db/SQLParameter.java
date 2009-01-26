/**
 * 
 */
package com.framedobjects.db;

/**
 * Placeholder for the kind of query parameter as defined as constants on
 * <code>java.sql.Types</code>
 * @author Richmow
 *
 */
public class SQLParameter {

  private int type = -1;
  
  public SQLParameter(int type){
    this.type = type;
  }

  /**
   * @return Returns the type of the SQL parameter.
   */
  public int getType() {
    return type;
  }
}
