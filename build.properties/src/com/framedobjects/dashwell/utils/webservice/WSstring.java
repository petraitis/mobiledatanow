package com.framedobjects.dashwell.utils.webservice;
import org.apache.log4j.Logger;

import wsl.fw.exception.MdnException;
/**
 * The <code>VCstring</code> class represents the Java string type.
 * 
 */
public class WSstring extends ParamView {

  private static final Logger LOGGER = Logger.getLogger(WSstring.class);

  private String m_stringValue;
  /*
   * Value that will be added to the current text
   * width.
   */
  private final int STRING_VALUE_SIZEEXT=150;

  /**
   * Constructs a new instance of this class.
   * 
   * @param paramListItem item where values of the component can be saved.
   * @param parent parent composite.
   */
  public WSstring(ParamListItem paramListItem) throws MdnException {
    this(paramListItem,0);
  }
  
  /**
   * Constructs a new instance of this class.
   * 
   * @param paramListItem item where values of the component can be saved.
   * @param parent parent composite.
   * @param arrayIndex the index where this component must save the values.
   */  
  public WSstring(ParamListItem paramListItem,int arrayIndex) throws MdnException {
    super(paramListItem,arrayIndex);

    m_stringValue=new String();

    /*
     * Set default value to the component.
     */
    setDefaultParamListItemValue();
  }

  /**
   * The default value of a {@link ParamListItem} object is
   * a string or already a value of the correct Java type.
   * The string has to be parsed into the the correct Java type
   * and stored in the {@link ParamListItem} object.
   *
   */  
  private void setDefaultParamListItemValue() throws MdnException {
    String value=null;
    /*
     * If ParamListItem has a string value.
     */
    if(m_paramListItem.getVectorData().get(m_arrayIndex) instanceof String)
      value=(String)m_paramListItem.getVectorData().get(m_arrayIndex);
    /*
     * Get default value (of correct Java type) if ParamListItem
     * holds more than one value (array mode).
     */
    else {
      value=(String)m_paramListItem.getVectorData().get(0);
      m_paramListItem.getVectorData().set(m_arrayIndex,value);
    }
    
    if(value==null)
      throw new MdnException("error.VCstringNullPointerException",new NullPointerException());
    
    m_stringValue=value.toString();
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
