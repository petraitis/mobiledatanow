package com.framedobjects.dashwell.utils.webservice;
/**
 * Class that parses a {@link java.lang.String} to a long value.
 * 
 */
public class LongValidator implements IValidator { 
  
  public boolean validate(String value) {
    if(value==null)
      return false;
    try {
      Long.parseLong(value);
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
