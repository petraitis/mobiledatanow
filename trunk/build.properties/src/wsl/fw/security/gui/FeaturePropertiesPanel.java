//==============================================================================
// FeaturePropertiesPanel.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.security.gui;

import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dimension;

import wsl.fw.gui.GuiConst;
import wsl.fw.gui.PropertiesPanel;
import wsl.fw.gui.WslTextField;
import wsl.fw.gui.WslLabel;
import wsl.fw.security.Feature;
import wsl.fw.util.Log;
import wsl.fw.util.Util;
import wsl.fw.help.HelpId;
import wsl.fw.resource.ResId;

//------------------------------------------------------------------------------
/**
 * Properties panel to edit Features. Read Only.
 */
public class FeaturePropertiesPanel
    extends PropertiesPanel
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/security/gui/FeaturePropertiesPanel.java $ ";

    // resources
    public static final ResId LABEL_NAME  = new ResId("FeaturePropertiesPanel.label.Name");
    public static final ResId MANDATORY_GROUP_NAME  = new ResId("FeaturePropertiesPanel.mandatory.GroupName");
    public static final ResId LABEL_DESCRIPTION  = new ResId("FeaturePropertiesPanel.label.Description");

    // help id
    public final static HelpId HID_FEATURE = new HelpId("fw.security.gui.FeaturePropertiesPanel");

    // controls
    private WslTextField _txtName        = new WslTextField(150);
    private WslTextField _txtDescription = new WslTextField(200);

    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public FeaturePropertiesPanel()
    {
        // init controls
        initGroupPropertiesPanelControls();
    }

    //--------------------------------------------------------------------------
    /**
     * Init the panel's controls. Feature is a read-only Dataobject so the
     * text fields are disabled.
     */
    private void initGroupPropertiesPanelControls()
    {
        // set layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Group Name control
        WslLabel lbl = new WslLabel(LABEL_NAME.getText());
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets.left = GuiConst.DEFAULT_INSET;
        gbc.insets.top = GuiConst.DEFAULT_INSET;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 0.2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(lbl, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        _txtName.setEnabled(false);
        add(_txtName, gbc);
        addMandatory(MANDATORY_GROUP_NAME.getText(), _txtName);

        // Group description control
        lbl = new WslLabel(LABEL_DESCRIPTION.getText());
        gbc.insets.right = 0;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(lbl, gbc);
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        gbc.weighty = 1;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        _txtDescription.setEnabled(false);
        add(_txtDescription, gbc);
    }

    //--------------------------------------------------------------------------
    /**
     * Transfer data between the Category DataObject and panel controls.
     * @param toDataObject, determines the direction of the transfer.
     */
    public void transferData(boolean toDataObject)
    {
        // must have a DataObject
        Feature dobj = (Feature) getDataObject();
        Util.argCheckNull(dobj);

        if (toDataObject)
        {
            // to the Category DataObject
            // skip as Feature is a readonly object
        }
        else
        {
            // to the controls
            _txtName.setText(dobj.getName());
            _txtDescription.setText(dobj.getDescription());
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Return the preferred size
     */
    public Dimension getPreferredSize()
    {
        return new Dimension(300, 150);
    }

    //--------------------------------------------------------------------------
    /**
     * @return the FeaturePropertiesPanel help id.
     */
    public HelpId getHelpId()
    {
        return HID_FEATURE;
    }

}

//==============================================================================
// end of file FeaturePropertiesPanel.java
//==============================================================================
