package com.framedobjects.dashwell.utils.webservice;
/**
 * Defines a standard set of methods for validators. Validators for visual
 * components (VC classes) are used to validate a string for any defined rule.
 * 
 */
public interface IValidator {
  /**
   * Validates a string for correctness.
   * 
   * @param value the string to validate.
   * @return true if string is valid.
   */
  public boolean validate(String value);
}
