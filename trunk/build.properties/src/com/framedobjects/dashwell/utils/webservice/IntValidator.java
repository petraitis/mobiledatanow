package com.framedobjects.dashwell.utils.webservice;
/**
 * Class that parses a {@link java.lang.String} to a short value.
 * 
 */
public class IntValidator implements IValidator {
  
  public boolean validate(String value) {
    if(value==null)
      return false;
    try {
      Integer.parseInt(value);
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
