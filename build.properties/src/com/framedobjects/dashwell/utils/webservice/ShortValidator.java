package com.framedobjects.dashwell.utils.webservice;
/**
 * Class that parses a {@link java.lang.String} to a short value.
 * 
 */
public class ShortValidator implements IValidator { 
  
  public boolean validate(String value) {
    if(value==null)
      return false;
    try {
      Short.parseShort(value);
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
