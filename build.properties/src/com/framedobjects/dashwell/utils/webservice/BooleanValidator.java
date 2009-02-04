package com.framedobjects.dashwell.utils.webservice;
/**
 * Class that parses a {@link java.lang.String} to a boolean value.
 * 
 */
public class BooleanValidator implements IValidator {
  
  public boolean validate(String value) {
    if(value==null)
      return false;
    try {
      Boolean.parseBoolean(value);
    }
    catch(NumberFormatException e) {
      /*
       * Ignore NumberFormatException. Validation failed.
       */
      return false;
    }
    return true;
  }
  
}
