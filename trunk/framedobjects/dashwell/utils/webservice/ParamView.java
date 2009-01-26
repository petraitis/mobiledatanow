package com.framedobjects.dashwell.utils.webservice;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import wsl.fw.exception.MdnException;
/**
 * This class is the abstract super class of all Visual Component classes.
 * It defines the general layout and behaviour of the component. For example every component
 * need a validator list and a item, where values of the component can be saved.
 * 
 */
public abstract class ParamView implements IParamView {
  
  private static final Logger LOGGER = Logger.getLogger(ParamView.class);
  
	protected List<IValidator> m_validators=new ArrayList<IValidator>();
	protected List<IValidator> m_failedValidators=new ArrayList<IValidator>();
	protected ParamListItem m_paramListItem;
	protected String m_label;
	protected String simple_label;
	private IParamView parent;
  
  protected int m_arrayIndex;

  /**
   * Constructs a new instance of this class.
   * 
   * @param paramListItem item where values of the component can be saved.
   * @param parent parent composite.
   */
  public ParamView(ParamListItem paramListItem) throws MdnException {
    this(paramListItem,0);
  }
  
  /**
   * Constructs a new instance of this class.
   * 
   * @param paramListItem item where values of the component can be saved.
   * @param parent parent composite.
   * @param arrayIndex the index where this component must save the values.
   */
	public ParamView(ParamListItem paramListItem,int arrayIndex) throws MdnException {
	    m_paramListItem=paramListItem;
	    m_arrayIndex=arrayIndex;
	    
	    /*
	     * getVectorData() can return a null reference if ParamListItem
	     * is a container type and contains no direct data.
	     */
	    if(paramListItem.getVectorData()!=null && arrayIndex>paramListItem.getVectorData().size())
	      throw new MdnException("error.ArrayIndexOutOfBoundsException",new ArrayIndexOutOfBoundsException(arrayIndex));
	
	    
	    m_label=paramListItem.getLabel();
    
	}
  
  public String getLabel() {
	  if (parent == null)
		  return m_label;
	  else
		  return parent.getLabel() + ": " + m_label;
  }
  
  public void setLabel(String label) {
    this.m_label = label;
  }
	
  public String getSimpleLabel() {
	return m_label;
  }
  
	public void addValidator(IValidator validator) {
		m_validators.add(validator);
	}
	public IValidator[] getValidators() {
		return (IValidator[])m_validators.toArray();
	}
  
  public IValidator removeValidator(IValidator validator) {
    int index=m_validators.indexOf(validator);
    if(index>-1)
      return m_validators.remove(index);
    return null;
  }
	
  public IValidator[] getFailedValidators() {
    if(m_failedValidators.size()>0) {
      return m_failedValidators.toArray(new IValidator[]{});
    }
    else
      return null;
  }
  public IParamView getParent(){
	  return parent;
  }
  
  public void setParent(IParamView parent){
	  this.parent = parent;
  }
}
