package com.framedobjects.dashwell.utils.webservice;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
/**
 * Class that matches a string to a regular expression. 
 * This class allows to define own, complex rules to validate input strings.
 * 
 */
public class RegexValidator implements IValidator  {
	
  private Pattern m_pattern;
  /**
   * Constructs a new instance of this class.
   * 
   * @param regex a regular expression.
   */
	public RegexValidator(String regex) throws PatternSyntaxException {
      m_pattern=Pattern.compile(regex);
	}
  
  public boolean validate(String value) {
    if(value==null)
      return false;
    return m_pattern.matcher(value).matches();
  }
  
}
