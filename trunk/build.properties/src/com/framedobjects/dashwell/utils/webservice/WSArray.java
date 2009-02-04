package com.framedobjects.dashwell.utils.webservice;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.log4j.Logger;

import wsl.fw.exception.MdnException;
/**
 * The <code>VCArray</code> class represents an array of Java types.
 * This class holds other Visual Components and manages their layout.
 * <code>VCArray</code> can add or remove (increase or decrease the array size) components.<br/><br/>
 * For example a <code>VCArray</code> object can add several <code>VCint</code>
 * objects. An <code>int[5]</code> is represented as a <code>VCArray</code> object
 * that contains five <code>VCint</code> objects.
 * 
 */
public class WSArray extends ParamView {

  private static final Logger LOGGER = Logger.getLogger(WSArray.class);

  private List<IParamView> m_addedVC;
  private Class m_visualClass;
  
  private String m_groupArray=null;
  
  private Object m_startValue;
  private int m_arraypos=-1;

  /**
   * Constructs a new instance of this class.
   * 
   * @param compositeGUI the composite where new components can be drawn.
   * @param addedVC the list that contains all visual components.
   * @param paramListItem the correspondung {@link ParamListItem} for this object.
   * @param parent the parent composite.
   * @param visualClass the visual class that will be instantiated for a new array element.
   */
  public WSArray(final List<IParamView> addedVC,ParamListItem paramListItem,
      final Class visualClass) throws MdnException {
    super(paramListItem);
    
    m_addedVC=addedVC;
    m_visualClass=visualClass;


    m_groupArray=paramListItem.getLabel();

    /*
     * Reset the current default label.
     */
    m_label = "";

    m_startValue=paramListItem.getVectorData().get(0);
    /*
     * Add one array element by default.
     */
    // TODO: is one element really necessary?
    //addArrayElement();
  }
  
  public void addArrayElement() {
    IParamView vc=null;
    try {
      m_arraypos++;
      
      if(m_arraypos>0)
        m_paramListItem.getVectorData().add(m_startValue);
      
      Constructor constructor=m_visualClass.getConstructor(new Class[]{ParamListItem.class,int.class});
      vc=(IParamView)constructor.newInstance(new Object[]{m_paramListItem,m_arraypos});

      m_addedVC.add(vc);

    }
    catch(SecurityException e) {
      new MdnException("error.SecurityException",e);
    }
    catch(IllegalArgumentException e) {
      m_arraypos--;
      new MdnException("error.IllegalArgumentException",e);
    }
    catch(NoSuchMethodException e) {
      m_arraypos--;
      new MdnException("error.NoSuchMethodException",e);
    }
    catch(InstantiationException e) {
      m_arraypos--;
      new MdnException("error.InstantiationException",e);
    }
    catch(IllegalAccessException e) {
      m_arraypos--;
      new MdnException("error.IllegalAccessException",e);
    }
    catch(InvocationTargetException e) {
      m_arraypos--;
      new MdnException("error.InvocationTargetException",e);
    }    
  }
  
  private void removeArrayElement() {
    if(m_arraypos>0) {
      
      m_paramListItem.getVectorData().remove(m_arraypos);
      
      m_arraypos--;
    }    
  }


  public void addValidator(IValidator validator) {
    /*
     * Do nothing.
     */
  }

  public IValidator[] getValidators() {
    return null;
  }

  public String getLabel() {
    return m_groupArray;
  }

  public void setLabel(String value) {
    m_groupArray = value;
  }

  public boolean validate() {
    return true;
  }
  
  // TODO: Implement error state in VCArray.
  public void setErrorState() {

  }
}
