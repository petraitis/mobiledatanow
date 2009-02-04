//==============================================================================
// LoginPanel.java
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
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JDialog;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.security.SecurityManager;
import wsl.fw.security.SecurityException;
import wsl.fw.gui.GuiConst;
import wsl.fw.gui.WslButtonPanel;
import wsl.fw.gui.WslButton;
import wsl.fw.gui.WslTextField;
import wsl.fw.gui.WslPasswordField;
import wsl.fw.gui.WslSwingApplication;
import wsl.fw.gui.GuiManager;
import wsl.fw.help.HelpId;
import wsl.fw.resource.ResId;

//------------------------------------------------------------------------------
/**
 * Panel used to login to the application
 */
public class LoginPanel extends WslButtonPanel implements ActionListener
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/security/gui/LoginPanel.java $ ";

    // resources
    public static final ResId BUTTON_LOGIN  = new ResId("LoginPanel.button.Login");
    public static final ResId BUTTON_EXIT  = new ResId("LoginPanel.button.Exit");
    public static final ResId BUTTON_HELP  = new ResId("LoginPanel.button.Help");
    public static final ResId LABEL_USER_NAME  = new ResId("LoginPanel.label.UserName");
    public static final ResId LABEL_PASSWORD  = new ResId("LoginPanel.label.Password");
    public static final ResId ERR_LOGIN  = new ResId("LoginPanel.error.Login");
    public static final ResId MSG_USER_NAME  = new ResId("LoginPanel.msg.UserName");
    public static final ResId TITLE_MSG_LOGIN_FAILED  = new ResId("LoginPanel.title.MsgLoginFailed");
    public static final ResId MSG1  = new ResId("LoginPanel.msg1");
    public static final ResId MSG2  = new ResId("LoginPanel.msg2");
    public static final ResId TITLE_LOGIN_FAILED  = new ResId("LoginPanel.title.LoginFailed");
    public static final ResId LOG_LOGIN_FAILED  = new ResId("LoginPanel.log.LoginFailed");
    public static final ResId MSG_FAILED_TO_CONNECT  = new ResId("LoginPanel.msg.FailedToConnect");
    public static final ResId TITLE_FAILED_TO_CONNECT  = new ResId("LoginPanel.title.FailedToConnect");
    public static final ResId ERR_FAILED_TO_CONNECT  = new ResId("LoginPanel.error.FailedToConnect");

    // help id
    public final static HelpId HID_LOGIN = new HelpId("fw.security.gui.LoginPanel");

    // attributes
    private WslTextField     _txtUserName = new WslTextField(140);
    private WslPasswordField _txtPassword = new WslPasswordField(140);
    private WslButton        _btnLogin    = new WslButton(BUTTON_LOGIN.getText(), this);
    private WslButton        _btnExit     = new WslButton(BUTTON_EXIT.getText(), this);

    //--------------------------------------------------------------------------
    /**
     * Blank constructor
     */
    public LoginPanel()
    {
        // super
        super(WslButtonPanel.HORIZONTAL);

        // init controls
        initLoginPanelControls();
    }

    //--------------------------------------------------------------------------
    /**
     * Create and initialise panel controls
     */
    private void initLoginPanelControls()
    {
        // buttons
        _btnLogin.setIcon(Util.resourceIcon(GuiConst.FW_IMAGE_PATH + "loginOk.gif"));
        _btnExit.setIcon(Util.resourceIcon(GuiConst.FW_IMAGE_PATH + "loginCancel.gif"));
        addButton(_btnLogin);
        addButton(_btnExit);
        addHelpButton(BUTTON_HELP.getText(), HID_LOGIN);

        // main panel
        getMainPanel().setLayout(new GridBagLayout());
        getMainPanel().setBorder(BorderFactory.createLoweredBevelBorder());
        GridBagConstraints gbc = new GridBagConstraints();

        // user name
        JLabel lbl = new JLabel(LABEL_USER_NAME.getText());
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets.left = GuiConst.DEFAULT_INSET;
        gbc.insets.top = GuiConst.DEFAULT_INSET;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        getMainPanel().add(lbl, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        //gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        getMainPanel().add(_txtUserName, gbc);

        // password
        lbl = new JLabel(LABEL_PASSWORD.getText());
        gbc.insets.right = 0;
        gbc.weightx = 0;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        getMainPanel().add(lbl, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        getMainPanel().add(_txtPassword, gbc);
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
            if(ev.getSource().equals(_btnLogin))
                onLogin();
            else if(ev.getSource().equals(_btnExit))
                onExit();
        }
        catch(Exception e)
        {
            GuiManager.showErrorDialog(this, ERR_LOGIN.getText(), e);
            Log.error(ERR_LOGIN.getText(), e);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Login button is clicked
     */
    public void onLogin()
    {
        // get data
        String userName = _txtUserName.getText();
        String password = new String(_txtPassword.getPassword());

        // verify data
        if(userName == null || userName.length() == 0)
            JOptionPane.showMessageDialog(this, MSG_USER_NAME.getText(),
            TITLE_MSG_LOGIN_FAILED.getText(), JOptionPane.ERROR_MESSAGE);

        else
        {
            try
            {
                // login
                SecurityManager.login(userName, password);

                // close the panel
                closePanel();

            }
            catch(SecurityException e)
            {
                String msg[] = { MSG1.getText(),
                    " " + MSG2.getText()};
                JOptionPane.showMessageDialog(this, msg, TITLE_LOGIN_FAILED.getText(),
                    JOptionPane.ERROR_MESSAGE);
                Log.log(LOG_LOGIN_FAILED.getText() + " [" + userName
                    + "] : " + e.toString());
            }
            catch(Exception e2)
            {
                JOptionPane.showMessageDialog(this, MSG_FAILED_TO_CONNECT.getText() + " "
                    + e2.toString(), TITLE_FAILED_TO_CONNECT.getText(), JOptionPane.ERROR_MESSAGE);
                Log.error(ERR_FAILED_TO_CONNECT.getText(), e2);
                WslSwingApplication.exitApplication();
            }
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Exit button is clicked
     */
    public void onExit()
    {
        WslSwingApplication.exitApplication();
    }

    //--------------------------------------------------------------------------
    /**
     * @return the preferred size of the LoginPanel
     */
    public Dimension getPreferredSize()
    {
        return new Dimension(346, 145);
    }

    //--------------------------------------------------------------------------
    /**
     * @return WslButton the default button
     */
    public WslButton getDefaultButton()
    {
        return _btnLogin;
    }

    //--------------------------------------------------------------------------
    /**
     * Open the login panel and do a login
     */
    public static LoginPanel openLoginPanel(Window parent, String title)
    {
        // create the LoginPanel
        LoginPanel p = new LoginPanel();

        // create the framing dialog
        JDialog dlg = GuiManager.getFramingDialog(parent, title, true, p);

        // centre the window and show it
        GuiManager.centerWindow(dlg);
        dlg.show();

        // return
        return p;
    }
}

//==============================================================================
// end of file LoginPanel.java
//==============================================================================
