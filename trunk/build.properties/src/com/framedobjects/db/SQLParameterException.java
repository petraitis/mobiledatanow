package com.framedobjects.db;

public class SQLParameterException extends Exception {

  private static final long serialVersionUID = 5450862868646901617L;

  public SQLParameterException(){
    super();
  }
  
  public SQLParameterException(String message){
    super(message);
  }
}
