package com.framedobjects.dashwell.utils.webservice;
import org.apache.log4j.Logger;

import wsl.fw.exception.MdnException;

/**
 * The <code>VCchar/code> class represents the Java character type.
 */
public class WSchar extends ParamView {
  
  private static final Logger LOGGER = Logger.getLogger(WSchar.class);

  private String m_stringValue;
  /*
   * Value that will be added to the current text
   * width.
   */
  private final int STRING_VALUE_SIZEEXT=10;

  /**
   * Constructs a new instance of this class.
   * 
   * @param paramListItem item where values of the component can be saved.
   * @param parent parent composite.
   */
  public WSchar(ParamListItem paramListItem) throws MdnException {
    this(paramListItem,0);
  }

  /**
   * Constructs a new instance of this class.
   * 
   * @param paramListItem item where values of the component can be saved.
   * @param parent parent composite.
   * @param arrayIndex the index where this component must save the values.
   */
  public WSchar(ParamListItem paramListItem,int arrayIndex) throws MdnException {
    super(paramListItem,arrayIndex);
    
    m_stringValue=new String();

    /*
     * Set default value to the component.
     */
    setDefaultParamListItemValue();
  }
  
  /**
   * Parses the string value into the correct Java type
   * and saves it in a {@link ParamListItem} object.
   * 
   * @param value string value to parse.
   */
  private void parseValue(String value) {
    if(value.length()==1)
      m_paramListItem.getVectorData().set(m_arrayIndex,value.toCharArray()[0]);
  }
  
  /**
   * The default value of a {@link ParamListItem} object is
   * a string or already a value of the correct Java type.
   * The string has to be parsed into the the correct Java type
   * and stored in the {@link ParamListItem} object.
   *
   */  
  private void setDefaultParamListItemValue() throws MdnException {
    Character value;
    try {
      /*
       * If ParamListItem has a string value.
       */
      if(m_paramListItem.getVectorData().get(0) instanceof String) {
        value=((String)m_paramListItem.getVectorData().get(0)).toCharArray()[0];
        m_paramListItem.getVectorData().set(0,value);
      }
      /*
       * If ParamListItem has already a value of the correct type.
       * This happens if a ParamListItem was created from the Web Service
       * response.
       */
      else if(m_paramListItem.getVectorData().get(m_arrayIndex) instanceof Character)
        value=(Character)m_paramListItem.getVectorData().get(m_arrayIndex);
      /*
       * Get default value (of correct Java type) if ParamListItem
       * holds more than one value (array mode).
       */
      else {
        value=(Character)m_paramListItem.getVectorData().get(0);
        m_paramListItem.getVectorData().set(m_arrayIndex,value);
      }
    }
    /*
     * May occurs if the default value is null, an empty string
     * or contains more than one character.
     */
    catch(ArrayIndexOutOfBoundsException e) {
      throw new MdnException("error.ArrayIndexOutOfBoundsException"
          +"error.VCMappingDefaultValueError" +
              m_paramListItem.getDatatype(),e);
    }
    catch(ClassCastException e) {
      throw new MdnException("error.ClassCastException"
          +"error.VCMappingDefaultValueError" +
              m_paramListItem.getDatatype(),e);
    }
    
    m_stringValue = value.toString();
  }
  
  public boolean validate() {
    boolean valid=true;
    m_failedValidators.clear();
    for(IValidator validator:m_validators) {
      if(!validator.validate(m_stringValue))
        m_failedValidators.add(validator);
    }
    /*
     * Even if only one validator failed, validation failed.
     */
    if(m_failedValidators.size()>0)
      valid=false;
    

    return valid;
  }
  

}
