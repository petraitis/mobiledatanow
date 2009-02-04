package com.framedobjects.dashwell.utils.webservice;
/**
 * Class that parses a {@link java.lang.String} to a byte value.
 * 
 */
public class ByteValidator implements IValidator {
  
  public boolean validate(String value) {
    if(value==null)
      return false;
    try {
      Byte.parseByte(value);
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
