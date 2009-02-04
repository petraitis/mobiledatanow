package com.framedobjects.dashwell.utils.webservice;
/**
 * Class that parses a {@link java.lang.String} to a short value.
 * 
 */
public class CharValidator implements IValidator { 
  
  public boolean validate(String value) {
    if(value==null || value.length()!=1)
      return false;
    return true;
  }
  
}
