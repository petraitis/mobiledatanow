package com.framedobjects.dashwell.utils.webservice;

import org.apache.log4j.Logger;

import wsl.fw.exception.MdnException;
/**
 * The <code>VCContainer</code> class represents an object that contains
 * nested data types. This class holds other Visual Components and manages
 * their layout.
 * 
 */
public class WSContainer extends ParamView {
  
  private static final Logger LOGGER = Logger.getLogger(WSContainer.class);

  private String m_groupContainer=null;
  
  /**
   * Constructs a new instance of this class.
   * 
   * @param paramListItem the correspondung {@link ParamListItem} for this object.
   * @param parent the parent composite.
   */
  public WSContainer(ParamListItem paramListItem) throws MdnException {
    super(paramListItem);
    
    if(paramListItem.getItemtype()!=ParamListItem.ITEMTYPE_CONTAINER)
      throw new MdnException("error.VCContainerParamListItemNotContainerException");
    
    m_label = "";
    
    m_groupContainer = paramListItem.getLabel();
  }
  

  public void addValidator(IValidator validator) {
    /*
     * Do nothing.
     */
  }

  public String getLabel() {
    return m_groupContainer;
  }
  
  public void setLabel(String label) {
    m_groupContainer = label;
  }

  public IValidator[] getValidators() {
    return null;
  }

  public boolean validate() {
    return true;
  }

}
