//==============================================================================
// GroupPropertiesPanel.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.mdn.guiconfig;

import javax.swing.JLabel;
import javax.swing.JCheckBox;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import wsl.fw.gui.GuiConst;
import wsl.fw.gui.PropertiesPanel;
import wsl.fw.gui.WslTextField;
import wsl.fw.gui.WslLabel;
import wsl.fw.security.Group;
import wsl.fw.util.Log;
import wsl.fw.util.Util;
import wsl.fw.help.HelpId;
import wsl.fw.resource.ResId;

//------------------------------------------------------------------------------
/**
 * Properties panel to edit Login Settings.
 */
public class LoginSettingsPropertiesPanel
    extends PropertiesPanel
    implements ActionListener
{
    // version tag
    private final static String _ident = "$Date: 2003/02/10 20:45:12 $  $Revision: 1.2 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/guiconfig/LoginSettingsPropertiesPanel.java $ ";

    // resources
    public static final ResId LABEL_USESPLASHSCREEN   = new ResId("LoginSettingsPropertiesPanel.label.useSplashScreen");
    public static final ResId LABEL_SPLASHSCREENTITLE = new ResId("LoginSettingsPropertiesPanel.label.SplashScreenTitle");
    public static final ResId LABEL_SPLASHSCREENTEXT  = new ResId("LoginSettingsPropertiesPanel.label.SplashScreenText");
    public static final ResId LABEL_LOGINSCREENTITLE  = new ResId("LoginSettingsPropertiesPanel.label.LoginScreenTitle");
    public static final ResId LABEL_LOGINSCREENTEXT   = new ResId("LoginSettingsPropertiesPanel.label.LoginScreenText");
    public static final ResId LABEL_USERNAMEPROMPT    = new ResId("LoginSettingsPropertiesPanel.label.UsernamePrompt");
    public static final ResId LABEL_PASSWORDPROMPT    = new ResId("LoginSettingsPropertiesPanel.label.PasswordPrompt");
    public static final ResId LABEL_REQUIREPASSWORD   = new ResId("LoginSettingsPropertiesPanel.label.RequirePassword");
    public static final ResId LABEL_NUMERICPASSWORD   = new ResId("LoginSettingsPropertiesPanel.label.NumericPassword");
    public static final ResId LABEL_LOGOUTSCREENTITLE = new ResId("LoginSettingsPropertiesPanel.label.LogoutScreenTitle");
    public static final ResId LABEL_LOGOUTSCREENTEXT  = new ResId("LoginSettingsPropertiesPanel.label.LogoutScreenText");

    // help id
    public final static HelpId HID_LOGINSETTINGS = new HelpId("mdn.guiconfig.LoginSettingsPropertiesPanel");

    // controls
    private JCheckBox    _checkUseSplashScreen = new JCheckBox(LABEL_USESPLASHSCREEN.getText());
    private WslLabel     _lblSplashTitle       = new WslLabel(LABEL_SPLASHSCREENTITLE.getText());
    private WslTextField _txtSplashTitle       = new WslTextField(150);
    private WslLabel     _lblSplashText        = new WslLabel(LABEL_SPLASHSCREENTEXT.getText());
    private WslTextField _txtSplashText        = new WslTextField(250);
    private WslTextField _txtLoginTitle        = new WslTextField(150);
    private WslTextField _txtLoginText         = new WslTextField(250);
    private WslTextField _txtUsernamePrompt    = new WslTextField(150);
    private JCheckBox    _checkRequirePassword = new JCheckBox(LABEL_REQUIREPASSWORD.getText());
    private JCheckBox    _checkNumericPassword = new JCheckBox(LABEL_NUMERICPASSWORD.getText());
    private WslLabel     _lblPasswordPrompt    = new WslLabel(LABEL_PASSWORDPROMPT.getText());
    private WslTextField _txtPasswordPrompt    = new WslTextField(150);
    private WslTextField _txtLogoutTitle       = new WslTextField(150);
    private WslTextField _txtLogoutText        = new WslTextField(250);

    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public LoginSettingsPropertiesPanel()
    {
        // init controls
        initLoginSettingsPropertiesPanelControls();
    }

    //--------------------------------------------------------------------------
    /**
     * Init the panel's controls.
     */
    private void initLoginSettingsPropertiesPanelControls()
    {
        JLabel lbl;

        // set layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // use splashscreen
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets.left = GuiConst.DEFAULT_INSET;
        gbc.insets.top = GuiConst.DEFAULT_INSET;
        gbc.weightx = 0.2;
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(_checkUseSplashScreen, gbc);
        _checkUseSplashScreen.addActionListener(this);

        // splashscreen title
        gbc.insets.right = 0;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(_lblSplashTitle, gbc);
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        //gbc.weighty = 1;
        add(_txtSplashTitle, gbc);

        // splashscreen text
        gbc.insets.right = 0;
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(_lblSplashText, gbc);
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        //gbc.weighty = 1;
        add(_txtSplashText, gbc);

        // login screen title
        lbl = new WslLabel(LABEL_LOGINSCREENTITLE.getText());
        gbc.insets.right = 0;
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(lbl, gbc);
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        //gbc.weighty = 1;
        add(_txtLoginTitle, gbc);

        // login screen text
        lbl = new WslLabel(LABEL_LOGINSCREENTEXT.getText());
        gbc.insets.right = 0;
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(lbl, gbc);
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        add(_txtLoginText, gbc);

        // username prompt
        lbl = new WslLabel(LABEL_USERNAMEPROMPT.getText());
        gbc.insets.right = 0;
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(lbl, gbc);
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        add(_txtUsernamePrompt, gbc);

        // require password
        gbc.insets.right = 0;
        gbc.gridx = 1;
        gbc.gridy = 6;
        add(_checkRequirePassword, gbc);
        _checkRequirePassword.addActionListener(this);

        // require numeric password
        gbc.insets.right = 0;
        gbc.gridx = 1;
        gbc.gridy = 7;
        add(_checkNumericPassword, gbc);
        _checkNumericPassword.addActionListener(this);

        // password prompt
        gbc.insets.right = 0;
        gbc.gridx = 0;
        gbc.gridy = 8;
        add(_lblPasswordPrompt, gbc);
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        add(_txtPasswordPrompt, gbc);

        // logout screen title
        lbl = new WslLabel(LABEL_LOGOUTSCREENTITLE.getText());
        gbc.insets.right = 0;
        gbc.gridx = 0;
        gbc.gridy = 9;
        add(lbl, gbc);
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        add(_txtLogoutTitle, gbc);

        // logout screen text
        lbl = new WslLabel(LABEL_LOGOUTSCREENTEXT.getText());
        gbc.insets.right = 0;
        gbc.gridx = 0;
        gbc.gridy = 10;
        add(lbl, gbc);
        gbc.gridx = 1;
        gbc.weighty = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        add(_txtLogoutText, gbc);
    }

    //--------------------------------------------------------------------------
    /**
     * Transfer data between the Category DataObject and panel controls.
     * @param toDataObject, determines the direction of the transfer.
     */
    public void transferData(boolean toDataObject)
    {
        // must have a DataObject
        LoginSettings dobj = (LoginSettings) getDataObject();
        Util.argCheckNull(dobj);

        if (toDataObject)
        {
            // to the LoginSettings DataObject
            // build and set the flags
            int flags = 0;
            if (_checkUseSplashScreen.getModel().isSelected())
                flags |= LoginSettings.FLAG_USE_SPLASHSCREEN;
            if (_checkRequirePassword.getModel().isSelected())
                flags |= LoginSettings.FLAG_REQUIRE_PASSWORD;
            if (_checkNumericPassword.getModel().isSelected())
                flags |= LoginSettings.FLAG_NUMERIC_PASSWORD;
            dobj.setFlags(flags);

            // set the rest of the text
            dobj.setSplashTitle(_txtSplashTitle.getText());
            dobj.setSplashText(_txtSplashText.getText());
            dobj.setLoginTitle(_txtLoginTitle.getText());
            dobj.setLoginText(_txtLoginText.getText());
            dobj.setUsernamePrompt(_txtUsernamePrompt.getText());
            dobj.setPasswordPrompt(_txtPasswordPrompt.getText());
            dobj.setLogoutTitle(_txtLogoutTitle.getText());
            dobj.setLogoutText(_txtLogoutText.getText());
        }
        else
        {
            // to the controls
            // checkbox state from flags
            _checkUseSplashScreen.getModel().setSelected(dobj.getFlags(LoginSettings.FLAG_USE_SPLASHSCREEN));
            _checkRequirePassword.getModel().setSelected(dobj.getFlags(LoginSettings.FLAG_REQUIRE_PASSWORD));
            _checkNumericPassword.getModel().setSelected(dobj.getFlags(LoginSettings.FLAG_NUMERIC_PASSWORD));

            // init text comtrols
            _txtSplashTitle.setText(dobj.getSplashTitle());
            _txtSplashText.setText(dobj.getSplashText());
            _txtLoginTitle.setText(dobj.getLoginTitle());
            _txtLoginText.setText(dobj.getLoginText());
            _txtUsernamePrompt.setText(dobj.getUsernamePrompt());
            _txtPasswordPrompt.setText(dobj.getPasswordPrompt());
            _txtLogoutTitle.setText(dobj.getLogoutTitle());
            _txtLogoutText.setText(dobj.getLogoutText());
        }
    }

    //--------------------------------------------------------------------------
    /**
     * @return the preferred size.
     */
    public Dimension getPreferredSize()
    {
        return new Dimension(450, 360);
    }
    //--------------------------------------------------------------------------
    /**
     * @return the GroupPropertiesPanel help id.
     */
    public HelpId getHelpId()
    {
        return HID_LOGINSETTINGS;
    }

    //--------------------------------------------------------------------------
    /**
     * Action handler.
     */
    public void actionPerformed(ActionEvent ev)
    {
        try
        {
            // switch on source, update state when the checkboxes change
            if (ev.getSource().equals(_checkUseSplashScreen))
                updateButtons();
            else if (ev.getSource().equals(_checkRequirePassword))
                updateButtons();
        }
        catch(Exception e)
        {
            wsl.fw.gui.GuiManager.showErrorDialog(this,
                wsl.mdn.common.MdnAdminConst.ERR_UNHANDLED.getText(), e);
            Log.error(wsl.mdn.common.MdnAdminConst.ERR_UNHANDLED.getText(), e);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Update the state of the controls.
     */
    public void updateButtons()
    {
        // call superclass
        super.updateButtons();

        // disable splash controls if splash button not checked
        boolean bSplash = _checkUseSplashScreen.getModel().isSelected();
        _lblSplashTitle.setEnabled(bSplash);
        _txtSplashTitle.setEnabled(bSplash);
        _lblSplashText.setEnabled(bSplash);
        _txtSplashText.setEnabled(bSplash);

        // disable password prompt if password not required
        boolean bPassword = _checkRequirePassword.getModel().isSelected();
        _lblPasswordPrompt.setEnabled(bPassword);
        _txtPasswordPrompt.setEnabled(bPassword);
		if (bPassword==false)
			_checkNumericPassword.getModel().setSelected(bPassword);
		_checkNumericPassword.setEnabled(bPassword);
    }
}

//==============================================================================
// end of file LoginSettingsPropertiesPaneltiesPanel.java
//==============================================================================
