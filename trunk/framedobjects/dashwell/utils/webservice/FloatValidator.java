package com.framedobjects.dashwell.utils.webservice;
/**
 * Class that parses a {@link java.lang.String} to a float value.
 * 
 */
public class FloatValidator implements IValidator {
  
  public boolean validate(String value) {
    if(value==null)
      return false;
    try {
      Float.parseFloat(value);
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
