//==============================================================================
// ChangePasswordPanel.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.security.gui;

// imports
import java.awt.Window;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import wsl.fw.help.HelpId;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.security.Security;
import wsl.fw.security.SecurityId;
import wsl.fw.security.SecurityException;
import wsl.fw.security.SecurityManager;
import wsl.fw.gui.GuiConst;
import wsl.fw.gui.WslButtonPanel;
import wsl.fw.gui.WslButton;
import wsl.fw.gui.WslSwingApplication;
import wsl.fw.gui.WslPasswordField;
import wsl.fw.gui.WslLabel;
import wsl.fw.gui.GuiManager;
import wsl.fw.resource.ResId;

//------------------------------------------------------------------------------
/**
 * Panel used to change passwords.
 */
public class ChangePasswordPanel extends WslButtonPanel implements ActionListener
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/security/gui/ChangePasswordPanel.java $ ";

    // resources
    public static final ResId BUTTON_OK  = new ResId("ChangePasswordPanel.button.Ok");
    public static final ResId BUTTON_CANCEL  = new ResId("ChangePasswordPanel.button.Cancel");
    public static final ResId BUTTON_HELP  = new ResId("ChangePasswordPanel.button.Help");
    public static final ResId LABEL_OLD_PASSWORD  = new ResId("ChangePasswordPanel.label.OldPassword");
    public static final ResId LABEL_NEW_PASSWORD  = new ResId("ChangePasswordPanel.label.NewPassword");
    public static final ResId LABEL_CONFIRM_PASSWORD  = new ResId("ChangePasswordPanel.label.ConfirmPassword");
    public static final ResId ERR_CHANGE_PASSWORD  = new ResId("ChangePasswordPanel.error.ChangePassword");
    public static final ResId EXCEPTION_INVALID_OLD_PASSWORD  = new ResId("ChangePasswordPanel.exception.InvalidOldPassword");
    public static final ResId TITLE_J_OPTION_PANE  = new ResId("ChangePasswordPanel.title.JOptionPane");
    public static final ResId TITLE_CHANGE_PASSWORD  = new ResId("ChangePasswordPanel.title.ChangePassword");

    // help id
    public final static HelpId HID_CHANGE_PASSWORD = new HelpId("fw.security.gui.ChangePasswordPanel");

    // attributes
    private WslPasswordField _txtCheckOldPassword = new WslPasswordField(140);
    private WslPasswordField _txtNewPassword      = new WslPasswordField(140);
    private WslPasswordField _txtConfirmPassword  = new WslPasswordField(140);
    private WslButton        _btnOk               = new WslButton(BUTTON_OK.getText(), this);
    private WslButton        _btnCancel           = new WslButton(BUTTON_CANCEL.getText(), this);
    private String           _newPassword         = null;
    private String           _oldPassword;

    //--------------------------------------------------------------------------
    /**
     * @param oldPassword, the encrypted existing password, if null then
     * the old password field is disabled and old passwords are not checked.
     */
    public ChangePasswordPanel(String oldPassword)
    {
        // super
        super(WslButtonPanel.HORIZONTAL);

        // set attribs
        _oldPassword = oldPassword;

        // init controls
        initChangePasswordPanelControls();
    }

    //--------------------------------------------------------------------------
    /**
     * Create and initialise panel controls
     */
    private void initChangePasswordPanelControls()
    {
        // buttons
        _btnOk.setIcon(Util.resourceIcon(GuiConst.FW_IMAGE_PATH + "save.gif"));
        _btnCancel.setIcon(Util.resourceIcon(GuiConst.FW_IMAGE_PATH + "close.gif"));
        addButton(_btnOk);
        addButton(_btnCancel);
        addHelpButton(BUTTON_HELP.getText(), HID_CHANGE_PASSWORD);

        // main panel
        getMainPanel().setLayout(new GridBagLayout());
        getMainPanel().setBorder(BorderFactory.createLoweredBevelBorder());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets.left = GuiConst.DEFAULT_INSET;
        gbc.insets.top = GuiConst.DEFAULT_INSET;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        WslLabel lbl;
        int    yPos = 0;

        // old password, only display if we have an old password
        if (_oldPassword != null)
        {
            gbc.gridy = yPos++;
            lbl = new WslLabel(LABEL_OLD_PASSWORD.getText());
            gbc.gridx = 0;
            getMainPanel().add(lbl, gbc);
            gbc.gridx = 1;
            gbc.weightx = 1;
            gbc.insets.right = GuiConst.DEFAULT_INSET;
            getMainPanel().add(_txtCheckOldPassword, gbc);
        }

        // new password
        lbl = new WslLabel(LABEL_NEW_PASSWORD.getText());
        gbc.insets.right = 0;
        gbc.weightx = 0;
        gbc.gridx = 0;
        gbc.gridy = yPos++;
        getMainPanel().add(lbl, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        getMainPanel().add(_txtNewPassword, gbc);

        // confirm password
        lbl = new WslLabel(LABEL_CONFIRM_PASSWORD.getText());
        gbc.insets.right = 0;
        gbc.weightx = 0;
        gbc.gridx = 0;
        gbc.gridy = yPos++;
        getMainPanel().add(lbl, gbc);
        gbc.gridx = 1;
        gbc.weighty = 1;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        getMainPanel().add(_txtConfirmPassword, gbc);
    }

    //--------------------------------------------------------------------------
    /**
     * Action event has occurred
     */
    public void actionPerformed(ActionEvent ev)
    {
        try
        {
            // switch on source
            if(ev.getSource().equals(_btnOk))
                onOk();
            else if(ev.getSource().equals(_btnCancel))
                closePanel();
        }
        catch(Exception e)
        {
            GuiManager.showErrorDialog(this, ERR_CHANGE_PASSWORD.getText(), e);
            Log.error(ERR_CHANGE_PASSWORD.getText(), e);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Ok button is clicked
     */
    public void onOk()
    {
        // get strings
        String checkPw = new String(_txtCheckOldPassword.getPassword());
        String newPw = new String(_txtNewPassword.getPassword());
        String confirmPw = new String(_txtConfirmPassword.getPassword());

        try
        {
            // verify old password, skipp of no old password
            if (_oldPassword != null)
            {
                String comparePw = SecurityManager.encryptPassword(checkPw);
                if(!comparePw.equals(_oldPassword))
                    throw new RuntimeException(EXCEPTION_INVALID_OLD_PASSWORD.getText());
            }

            // verify new and confirm passwords
            String err = SecurityManager.validateNewPassword(newPw, confirmPw);
            if(err.length() > 0)
                throw new RuntimeException(err);

            // valid login, set password and close
            _newPassword = SecurityManager.encryptPassword(newPw);
            closePanel();
        }
        catch(Exception e)
        {
            // show error
            JOptionPane.showMessageDialog(this, e.getMessage(),
                TITLE_J_OPTION_PANE.getText(), JOptionPane.ERROR_MESSAGE);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * @return the preferred size of the LoginPanel
     */
    public Dimension getPreferredSize()
    {
        return new Dimension(346, 200);
    }

    //--------------------------------------------------------------------------
    /**
     * @return WslButton the default button
     */
    public WslButton getDefaultButton()
    {
        return _btnOk;
    }

    //--------------------------------------------------------------------------
    /**
     * @return String the new password, null if cancelled
     */
    public String getNewPassword()
    {
        return _newPassword;
    }


    //--------------------------------------------------------------------------
    /**
     * Open the Change password panel
     */
    public static ChangePasswordPanel openChangePasswordPanel(Window parent, String oldPassword)
    {
        // create the panel
        ChangePasswordPanel p = new ChangePasswordPanel(oldPassword);

        // create the framing dialog
        JDialog dlg = GuiManager.getFramingDialog(parent, TITLE_CHANGE_PASSWORD.getText(), true, p);

        // centre the window and show it
        GuiManager.centerWindow(dlg);
        dlg.show();

        // return
        return p;
    }
}

//==============================================================================
// end of file ChangePasswordPanel.java
//==============================================================================
