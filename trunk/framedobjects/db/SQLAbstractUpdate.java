package com.framedobjects.db;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class SQLAbstractUpdate extends SQLOperation {

  public int update(){
    return update(new Object[]{});
  }
  
  public int update(boolean param){
    return update(new Object[]{new Boolean(param)});
  }
  
  public int update(int param){
    return update(new Object[]{new Integer(param)});
  }
  
  public int update(int param1, int param2){
    return update(new Object[]{new Integer(param1), new Integer(param2)});
  }
  
  public int update(long param){
    return update(new Object[]{new Long(param)});
  }
  
  public int update(long param1, long param2){
    return update(new Object[]{new Long(param1), new Long(param2)});
  }
  
  public int update(String param){
    return update(new Object[]{param});
  }
  
  public int update(String param1, String param2){
    return update(new Object[]{param1, param2});
  }
  
  public int update(String param1, int param2){
    return update(new Object[]{param1, new Integer(param2)});
  }
  
  public abstract int update(Object[] params);
  
  public int update(Connection connection) throws SQLException, 
          SQLParameterException{
    return update(connection, new Object[]{});
  }
  
  public int update(Connection connection, boolean param)
        throws SQLException, SQLParameterException{
    return update(connection, new Object[]{new Boolean(param)});
  }
  
  public int update(Connection connection, int param)
        throws SQLException, SQLParameterException{
    return update(connection, new Object[]{new Integer(param)});
  }
  
  public int update(Connection connection, int param1, int param2)
        throws SQLException, SQLParameterException{
    return update(connection, new Object[]{new Integer(param1), new Integer(param2)});
  }
  
  public int update(Connection connection, long param)
        throws SQLException, SQLParameterException{
    return update(connection, new Object[]{new Long(param)});
  }
  
  public int update(Connection connection, long param1, long param2)
        throws SQLException, SQLParameterException{
    return update(connection, new Object[]{new Long(param1), new Long(param2)});
  }
  
  public int update(Connection connection, String param)
        throws SQLException, SQLParameterException{
    return update(connection, new Object[]{param});
  }
  
  public int update(Connection connection, String param1, String param2)
        throws SQLException, SQLParameterException{
    return update(connection, new Object[]{param1, param2});
  }
  
  public int update(Connection connection, String param1, int param2)
        throws SQLException, SQLParameterException{
    return update(connection, new Object[]{param1, new Integer(param2)});
  }
  
  public abstract int update(Connection connection, Object[] params)
        throws SQLException, SQLParameterException;
}
