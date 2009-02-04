/**
 * 
 */
package com.framedobjects.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * An abstract class for all query subclasses providing default implementations
 * for all <code>findObject()</code> methods other than 
 * <code>findObject(Object[])</code>.
 * 
 * @author Jens Richnow
 *
 */
public abstract class SQLAbstractQuery extends SQLOperation {

  public List execute(){
    return execute(new Object[]{});
  }
  
  public List execute(boolean param){
    return execute(new Object[]{new Boolean(param)});
  }
  
  public List execute(int param){
    return execute(new Object[]{new Integer(param)});
  }
                                 
  public List execute(int param1, int param2){
    return execute(new Object[]{new Integer(param1), new Integer(param2)});
  }
  
  public List execute(long param){
    return execute(new Object[]{new Long(param)});
  }
  
  public List execute(long param1, long param2){
    return execute(new Object[]{new Long(param1), new Long(param2)});
  }
  
  public List execute(String param){
    return execute(new Object[]{param});
  }
  
  public List execute(String param1, String param2){
    return execute(new Object[]{param1, param2});
  }
  
  public List execute(String param1, int param2){
    return execute(new Object[]{param1, new Integer(param2)});
  }
  
  public abstract List execute(Object[] params);
  
  public List execute(Connection connection) throws SQLException, SQLParameterException{
    return execute(new Object[]{});
  }
  
  public List execute(Connection connection, boolean param) throws SQLException, SQLParameterException{
    return execute(connection, new Object[]{new Boolean(param)});
  }
  
  public List execute(Connection connection, int param) throws SQLException, SQLParameterException{
    return execute(connection, new Object[]{new Integer(param)});
  }
                                 
  public List execute(Connection connection, int param1, int param2) throws SQLException, SQLParameterException{
    return execute(connection, new Object[]{new Integer(param1), new Integer(param2)});
  }
  
  public List execute(Connection connection, long param) throws SQLException, SQLParameterException{
    return execute(connection, new Object[]{new Long(param)});
  }
  
  public List execute(Connection connection, long param1, long param2) throws SQLException, SQLParameterException{
    return execute(connection, new Object[]{new Long(param1), new Long(param2)});
  }
  
  public List execute(Connection connection, String param) throws SQLException, SQLParameterException{
    return execute(connection, new Object[]{param});
  }
  
  public List execute(Connection connection, String param1, String param2) throws SQLException, SQLParameterException{
    return execute(connection, new Object[]{param1, param2});
  }
  
  public List execute(Connection connection, String param1, int param2) throws SQLException, SQLParameterException{
    return execute(connection, new Object[]{param1, new Integer(param2)});
  }
  
  public abstract List execute(Connection connection, Object[] params) throws SQLException, SQLParameterException;
  
  public Object findObject() {
    return findObject(new Object[]{});
  }

  public Object findObject(boolean param) {
    return findObject(new Object[]{new Boolean(param)});
  }

  public Object findObject(int param) {
    return findObject(new Object[]{new Integer(param)});
  }

  public Object findObject(int param1, int param2) {
    return findObject(new Object[]{new Integer(param1), new Integer(param2)});
  }

  public Object findObject(long param) {
    return findObject(new Object[]{new Long(param)});
  }
  
  public Object findObject(long param1, long param2) {
    return findObject(new Object[]{new Long(param1), new Long(param2)});
  }

  public Object findObject(String param) {
    return findObject(new Object[]{param});
  }

  public Object findObject(String param1, String param2) {
    return findObject(new Object[]{param1, param2});
  }

  public Object findObject(String param1, int param2) {
    return findObject(new Object[]{param1, new Integer(param2)});
  }
  
  public abstract Object findObject(Object[] params);
  
  public Object findObject(Connection connection) 
          throws SQLException, SQLParameterException{
    return findObject(connection, new Object[]{});
  }

  public Object findObject(Connection connection, boolean param) 
          throws SQLException, SQLParameterException{
    return findObject(connection, new Object[]{new Boolean(param)});
  }

  public Object findObject(Connection connection, int param) 
          throws SQLException, SQLParameterException{
    return findObject(connection, new Object[]{new Integer(param)});
  }

  public Object findObject(Connection connection, int param1, int param2) 
          throws SQLException, SQLParameterException{
    return findObject(connection, 
                  new Object[]{new Integer(param1), new Integer(param2)});
  }

  public Object findObject(Connection connection, long param) 
          throws SQLException, SQLParameterException{
    return findObject(connection, new Object[]{new Long(param)});
  }

  public Object findObject(Connection connection, String param)
          throws SQLException, SQLParameterException{
    return findObject(connection, new Object[]{param});
  }

  public Object findObject(Connection connection, String param1, String param2)
          throws SQLException, SQLParameterException{
    return findObject(connection, new Object[]{param1, param2});
  }

  public Object findObject(Connection connection, String param1, int param2) 
          throws SQLException, SQLParameterException{
    return findObject(connection, new Object[]{param1, new Integer(param2)});
  }
  
  public abstract Object findObject(Connection connection, Object[] params)
          throws SQLException, SQLParameterException; 
  
  public abstract Object mapRow(ResultSet rs) throws SQLException;
}
