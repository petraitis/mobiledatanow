package com.framedobjects.dashwell.utils.webservice;
/**
 * Class that parses a {@link java.lang.String} to a double value.
 * 
 */
public class DoubleValidator implements IValidator {
  
  public boolean validate(String value) {
    if(value==null)
      return false;
    try {
      Double.parseDouble(value);
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
