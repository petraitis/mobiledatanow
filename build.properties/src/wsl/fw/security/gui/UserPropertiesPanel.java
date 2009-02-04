//==============================================================================
// UserPropertiesPanel.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.security.gui;

import javax.swing.JOptionPane;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Vector;
import wsl.fw.security.SecurityManager;
import wsl.fw.datasource.DataSourceException;
import wsl.fw.datasource.DataObject;
import wsl.fw.gui.GuiConst;
import wsl.fw.gui.GuiManager;
import wsl.fw.gui.PropertiesPanel;
import wsl.fw.gui.WslTextField;
import wsl.fw.gui.WslButton;
import wsl.fw.gui.WslLabel;
import wsl.fw.gui.WslPasswordField;
import wsl.fw.security.User;
import wsl.fw.util.Log;
import wsl.fw.util.Util;
import wsl.fw.help.HelpId;
import wsl.fw.resource.ResId;

//------------------------------------------------------------------------------
/**
 * Properties panel to edit Users.
 */
public class UserPropertiesPanel
    extends PropertiesPanel
    implements ActionListener
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/security/gui/UserPropertiesPanel.java $ ";

    // resources
    public static final ResId BUTTON_PASSWORD  = new ResId("UserPropertiesPanel.button.Password");
    public static final ResId LABEL_NAME  = new ResId("UserPropertiesPanel.label.Name");
    public static final ResId MANDATORY_USER_NAME  = new ResId("UserPropertiesPanel.mandatory.UserName");
    public static final ResId LABEL_PASSWORD  = new ResId("UserPropertiesPanel.label.Password");
    public static final ResId MANDATORY_PASSWORD  = new ResId("UserPropertiesPanel.mandatory.Password");
    public static final ResId LABEL_CONFIRM_PASSWORD  = new ResId("UserPropertiesPanel.label.ConfirmPassword");
    public static final ResId MANDATORY_CONFIRM_PASSWORD  = new ResId("UserPropertiesPanel.mandatory.ConfirmPassword");
    public static final ResId LABEL_DETAILS  = new ResId("UserPropertiesPanel.label.Details");
    public static final ResId LABEL_CONFIG_SETTINGS  = new ResId("UserPropertiesPanel.label.ConfigSettings");
    public static final ResId STRING_PASSWORD  = new ResId("UserPropertiesPanel.string.Password");
    public static final ResId DEBUG_CHANGE_PASSWORD  = new ResId("UserPropertiesPanel.debug.ChangePassword");
    public static final ResId TITLE_INVALID_PASSWORD  = new ResId("UserPropertiesPanel.title.InvalidPassword");

    // help id
    public final static HelpId HID_USER = new HelpId("fw.security.gui.UserPropertiesPanel");

    // controls
    private WslTextField     _txtName            = new WslTextField(150);
    private WslPasswordField _txtPassword        = new WslPasswordField(150);
    private WslPasswordField _txtConfirmPassword = new WslPasswordField(150);
    private WslTextField     _txtDetails         = new WslTextField(200);
    private WslTextField     _txtConfig          = new WslTextField(200);
    private WslButton        _btnPassword        = new WslButton(BUTTON_PASSWORD.getText(), this);

    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public UserPropertiesPanel()
    {
        // init controls
        initUserPropertiesPanelControls();
    }

    //--------------------------------------------------------------------------
    /**
     * Init the panel's controls.
     */
    private void initUserPropertiesPanelControls()
    {
        // set layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // User Name control
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
        add(_txtName, gbc);
        addMandatory(MANDATORY_USER_NAME.getText(), _txtName);

        // password
        lbl = new WslLabel(LABEL_PASSWORD.getText());
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(lbl, gbc);
        gbc.gridx = 1;
        _txtPassword.setPreferredSize(new Dimension(100, WslTextField.DEFAULT_HEIGHT));
        add(_txtPassword, gbc);
        addMandatory(MANDATORY_PASSWORD.getText(), _txtPassword);

        // confirm password
        lbl = new WslLabel(LABEL_CONFIRM_PASSWORD.getText());
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(lbl, gbc);
        gbc.gridx = 1;
        _txtConfirmPassword.setPreferredSize(new Dimension(100, WslTextField.DEFAULT_HEIGHT));
        add(_txtConfirmPassword, gbc);
        addMandatory(MANDATORY_CONFIRM_PASSWORD.getText(), _txtConfirmPassword);

        // User details control
        lbl = new WslLabel(LABEL_DETAILS.getText());
        gbc.insets.right = 0;
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(lbl, gbc);
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        gbc.weighty = 1;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        add(_txtDetails, gbc);

        // User config settings control
        lbl = new WslLabel(LABEL_CONFIG_SETTINGS.getText());
        gbc.insets.right = 0;
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(lbl, gbc);
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        gbc.weighty = 1;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        add(_txtConfig, gbc);
    }

    //--------------------------------------------------------------------------
    /**
     * Transfer data between the Category DataObject and panel controls.
     * @param toDataObject, determines the direction of the transfer.
     */
    public void transferData(boolean toDataObject)
    {
        // must have a DataObject
        User dobj = (User) getDataObject();
        Util.argCheckNull(dobj);

        if (toDataObject)
        {
            // to the Category DataObject
            dobj.setName(_txtName.getText());
            if(dobj.getState() == DataObject.NEW)
            {
                String newPw = new String(_txtPassword.getPassword());
                dobj.setPassword(SecurityManager.encryptPassword(newPw));
            }
            //dobj.setDetails(_txtDetails.getText());
            //dobj.setConfigSettings(_txtConfig.getText());
        }
        else
        {
            // to the controls
            _txtName.setText(dobj.getName());
            String pw = (dobj.getState() == DataObject.NEW)? "": STRING_PASSWORD.getText();
            _txtPassword.setText(pw);
            _txtConfirmPassword.setText(pw);
            //_txtDetails.setText(dobj.getDetails());
            //_txtConfig.setText(dobj.getConfigSettings());
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Return the preferred size
     */
    public Dimension getPreferredSize()
    {
        return new Dimension(350, 200);
    }

    //--------------------------------------------------------------------------
    /**
     * Return a vector of custom buttons. These buttons will be added to the button panel.
     * Overriden by subclasses
     * @return Vector A Vector of WslButtons
     */
    public Vector getCustomButtons()
    {
        Vector v = new Vector();
        v.add(_btnPassword);
        return v;
    }

    //--------------------------------------------------------------------------
    /**
     * Action handler.
     */
    public void actionPerformed(ActionEvent event)
    {
        try
        {
            // switch on source
            if(event.getSource().equals(_btnPassword))
                onChangePassword();
        }
        catch(Exception e)
        {
            GuiManager.showErrorDialog(this, GuiManager.ERR_UNHANDLED.getText(), e);
            Log.error(GuiManager.ERR_UNHANDLED.getText(), e);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Handle change password action.
     */
    private void onChangePassword()  throws DataSourceException
    {
        Log.debug(DEBUG_CHANGE_PASSWORD.getText());
        // get the old password
        User user = (User) getDataObject();
        String oldPw = user.getPassword();

        // open the change password panel, with no check for old password as
        // this is an admin function
        ChangePasswordPanel p = ChangePasswordPanel.openChangePasswordPanel(getFrameParent(), null);

        // get the new password and set it
        String newPw = p.getNewPassword();
        if(newPw != null)
        {
            user.setPassword(newPw);
            user.save();
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Check that mandatory fields have data in them. Pops an error dlg if
     * mandatories are not filled. Overridden to check password and confirm
     * password match.
     * @return boolean true if all mandatory fields have data in them
     */
    public boolean checkMandatories()
    {
        if(getDataObject().getState() == DataObject.NEW)
        {
            // validate the new password
            String newPw = new String(_txtPassword.getPassword());
            String confirmPw = new String(_txtConfirmPassword.getPassword());
            String err = SecurityManager.validateNewPassword(newPw, confirmPw);
            if(err.length() > 0)
            {
                JOptionPane.showMessageDialog(getFrameParent(), err, TITLE_INVALID_PASSWORD.getText(), JOptionPane.OK_OPTION);
                return false;
            }
        }

        // super
        return super.checkMandatories();
    }

    //--------------------------------------------------------------------------
    /**
     * Enables / disables controls, specifically password texts and button.
     */
    public void updateButtons()
    {
        // flags
        User    user    = (User) getDataObject();
        boolean hasUser = (user != null);
        boolean isNew   = hasUser && user.getState() == DataObject.NEW;

        // enable / disable
        _txtPassword.setEnabled(hasUser && isNew);
        _txtConfirmPassword.setEnabled(hasUser && isNew);
        _btnPassword.setEnabled(hasUser && !isNew);
    }

    //--------------------------------------------------------------------------
    /**
     * @return the UserPropertiesPanel help id.
     */
    public HelpId getHelpId()
    {
        return HID_USER;
    }
}

//==============================================================================
// end of file UserPropertiesPanel.java
//==============================================================================
