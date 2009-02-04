//==============================================================================
// PropertiesPanel.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.gui;

// imports
import wsl.fw.datasource.DataObject;
import wsl.fw.util.Util;
import wsl.fw.help.HelpId;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.awt.Window;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;
import javax.swing.JOptionPane;

import wsl.fw.resource.ResId;

//------------------------------------------------------------------------------
/**
 * Superclass for all PropertiesPanels. These panels show data in a DataObject
 * and transfer this data between controls and the DataObject
 */
public abstract class PropertiesPanel extends WslWizardChild
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/gui/PropertiesPanel.java $ ";

    // resources
    public static final ResId MSG_NOT_FILLED  = new ResId("PropertiesPanel.msg.NotFilled");
    public static final ResId MSG_TITLE  = new ResId("PropertiesPanel.msg.Title");

    // attributes
    private DataObject _dobj;
    private Hashtable  _mandatories = new Hashtable();
    private WslPanel   _pnlMaintenance;

    //--------------------------------------------------------------------------
    /**
     * Set the maintenance parent of the PropertiesPanel.
     * @param parent the WslPanel maintenance parent
     */
    public void setMaintenanceParent(WslPanel parent)
    {
        _pnlMaintenance = parent;
    }

    //--------------------------------------------------------------------------
    /**
     * @return The Window frame parent
     */
    public Window getFrameParent()
    {
        return (_pnlMaintenance == null)? null: _pnlMaintenance.getFrameParent();
    }

    //--------------------------------------------------------------------------
    /**
     * Add a mandatory field
     * @param name A readable name for the field
     * @param comp The component to add
     */
    protected void addMandatory(String name, JComponent comp)
    {
        // put into the map
        _mandatories.put(name, comp);
    }

    //--------------------------------------------------------------------------
    /**
     * Check that mandatory fields have data in them. Pops an error dlg if
     * mandatories are not filled.
     * @return boolean true if all mandatory fields have data in them
     */
    public boolean checkMandatories()
    {
        // iterate the mandatories hashtable
        String msg = "";
        String key;
        JComponent comp;
        Enumeration enums = _mandatories.keys();
        while(enums != null && enums.hasMoreElements())
        {
            // get the key and component
            key = (String)enums.nextElement();
            comp = (JComponent)_mandatories.get(key);
            if(comp != null && !hasData(comp))
            {
                // build error message
                if(msg.length() > 0)
                    msg += ", ";
                msg += key;
            }
        }

        // if empty mandatories, show error
        if(msg.length() > 0)
        {
            msg = MSG_NOT_FILLED.getText() + " " + msg;
            JOptionPane.showMessageDialog(this, msg, MSG_TITLE.getText(), JOptionPane.ERROR_MESSAGE);
            return false;
        }
        else
            return true;
    }

    //--------------------------------------------------------------------------
    /**
     * @return boolean true if the component contains data
     */
    protected boolean hasData(JComponent comp)
    {
        // validate
        Util.argCheckNull(comp);

        // switch on type
        if(comp instanceof JTextComponent)
            return (((JTextComponent)comp).getText().length() > 0);

        // has data by default
        return true;
    }

    //--------------------------------------------------------------------------
    /**
     * Transfer data between DataObject and controls
     * @param toDataObject
     * @return void
     * @exception
     * @roseuid 398FB791003E
     */
    public abstract void transferData(boolean toDataObject);

    //--------------------------------------------------------------------------
    /**
     * @return DataObject
     * @exception
     * @roseuid 398FB7D90069
     */
    public DataObject getDataObject()
    {
        return _dobj;
    }

    //--------------------------------------------------------------------------
    /**
     * @param dobj
     * @return void
     * @exception
     * @roseuid 398FB81E000E
     */
    public void setDataObject(DataObject dobj)
    {
        // set attribute
        _dobj = dobj;

        // transfer data to controls
        transferData(false);
        updateButtons();
    }

    //--------------------------------------------------------------------------
    /**
     * Return a vector of custom buttons. These buttons will be added to the button panel.
     * Overriden by subclasses
     * @return Vector A Vector of WslButtons
     */
    public Vector getCustomButtons()
    {
        return null;
    }

    //--------------------------------------------------------------------------
    /**
     * Get the HelpId for this properties panel. The standard PropertiesPanel
     * parents (MaintenancePanel, OkCancelPanel etc) will use this id to create
     * a help button. If the PropertiesPanel needs multiple help buttons it can
     * add and manage extra ones as custom buttons.
     * @return the HelpId for the help button, or null if no help button.
     */
    public HelpId getHelpId()
    {
        return null;
    }
}

//==============================================================================
// end of file PropertiesPanel.java
//==============================================================================
