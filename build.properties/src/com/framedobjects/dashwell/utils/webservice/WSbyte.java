package com.framedobjects.dashwell.utils.webservice;
import org.apache.log4j.Logger;

import wsl.fw.exception.MdnException;
/**
 * The <code>VCbyte</code> class represents the Java byte type.
 * 
 */
public class WSbyte extends ParamView {
  
  private static final Logger LOGGER = Logger.getLogger(WSbyte.class);

  private String m_stringValue;
  /*
   * Value that will be added to the current text
   * width.
   */
  private final int STRING_VALUE_SIZEEXT=150;

  /**
   * Constructs a new instance of this class.
   * 
   * @param item item where values of the component can be saved.
   * @param parent parent composite.
   */
  public WSbyte(ParamListItem item) throws MdnException {
    this(item,0);
  }
  
  /**
   * Constructs a new instance of this class.
   * 
   * @param item item where values of the component can be saved.
   * @param parent parent composite.
   * @param arrayIndex the index where this component must save the values.
   */
  public WSbyte(final ParamListItem item,int arrayIndex) throws MdnException {
    super(item,arrayIndex);
    
    m_stringValue= new String();
    
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
    try {
      m_paramListItem.getVectorData().set(m_arrayIndex,Byte.parseByte(value));
    }
    catch(NumberFormatException e) {
      /*
       * Ignore NumberFormatException, but ParamListItem value
       * not set.
       */
    }
  }
  
  /**
   * The default value of a {@link ParamListItem} object is
   * a string or already a value of the correct Java type.
   * The string has to be parsed into the the correct Java type
   * and stored in the {@link ParamListItem} object.
   *
   */  
  private void setDefaultParamListItemValue() throws MdnException {
    Byte value;
    try {
      /*
       * If ParamListItem has a string value.
       */
      if(m_paramListItem.getVectorData().get(0) instanceof String) {
        value=(Byte)Byte.parseByte((String)m_paramListItem.getVectorData().get(0));
        m_paramListItem.getVectorData().set(0,value);
      }
      /*
       * If ParamListItem has already a value of the correct type.
       * This happens if a ParamListItem was created from the Web Service
       * response.
       */
      else if(m_paramListItem.getVectorData().get(m_arrayIndex) instanceof Byte)
        value=(Byte)m_paramListItem.getVectorData().get(m_arrayIndex);
      /*
       * Get default value (of correct Java type) if ParamListItem
       * holds more than one value (array mode).
       */
      else {
        value=(Byte)m_paramListItem.getVectorData().get(0);
        m_paramListItem.getVectorData().set(m_arrayIndex,value);
      }
    }
    catch(NumberFormatException e) {
      throw new MdnException("error.NumberFormatException"
          +"error.VCMappingDefaultValueError" + 
              m_paramListItem.getDatatype(),e);
    }
    catch(ClassCastException e) {
      throw new MdnException("error.ClassCastException"
          +"error.VCMappingDefaultValueError"+
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
