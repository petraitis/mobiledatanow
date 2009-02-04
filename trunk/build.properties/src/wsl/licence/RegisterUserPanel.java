package wsl.licence;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.BorderFactory;
import javax.swing.border.*;
import javax.swing.plaf.BorderUIResource;
import javax.swing.JOptionPane;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.event.ActionEvent;

/**
 * Title:        Mobile Data Now
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      WAP Solutions Ltd
 * @author Paul Lupton
 * @version 1.0
 */

public class RegisterUserPanel extends JPanel
{
    private JTextField _licenceKey;
    private JTextField _coName;
    private JTextField _userName;
    private JTextField _email;
    private JTextField _phone;
    private JTextField _fax;
    private JTextArea _address;
    private JTextField _users;
    private JRadioButton _radPerm;
    private JRadioButton _radTemp;
    private JButton _btnRegister;
    private JButton _btnCancel;
    private Dimension _dim = new Dimension(600, 500);
    private UserInfo _info;
    private JDialog _parent;
    private String _filename;

    private static final String WAP_EMAIL_ADDRESS = "register@wap.com";
    private static final String WAP_FAX_NUM = "(+64 9) 123-4567";
    public static final String REGISTRATION_FILENAME = "register.txt";
    private static final int PROD_CODE = 121001;

    public static final String _descLines[] =
    {
        "Please enter you registration information.",
        "Fields marked with '*' are compulsory."
    };

    public RegisterUserPanel(JDialog parent, UserInfo info)
    {
        String sep = System.getProperty("file.separator", "/");
        String path = System.getProperty("user.dir", ".");
        _filename = path + sep + REGISTRATION_FILENAME;

        _info = info;
        _parent = parent;

        _licenceKey = new JTextField(); _licenceKey.setEditable(false);
        _coName = new JTextField();
        _userName = new JTextField();
        _email = new JTextField();
        _phone = new JTextField();
        _fax = new JTextField();
        _address = new JTextArea();
        _address.setSize(new Dimension(200, 100));
        _address.setPreferredSize(new Dimension(200, 100));
        _users = new JTextField();

        _radPerm = new JRadioButton("Permanent");
        _radTemp = new JRadioButton("Temporary");
        ButtonGroup bg = new ButtonGroup();
        bg.add(_radPerm);
        bg.add(_radTemp);

        _btnRegister = new JButton("Register");
        _btnRegister.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) { onRegister(); }
        });

        _btnCancel = new JButton("Cancel");
        _btnCancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) { onCancel(); }
        });

        initControls();
        transferData(true);

    }

/*
    public static void main(String[] args)
    {
        UserInfo info = new UserInfo();
        Store store = null;
        LicenceKey lKey = null;

        // Get the licence key
        try
        {
            store = new Store();
            lKey = store.getLicenceKey();
            if (lKey.isDefault())
            {
               System.out.println("We have the default licence");
               // create a new key
               lKey = new LicenceKey(PROD_CODE);
               store.setLicenceKey(lKey);
               store.store(); // save the user code for the first time
            }

            if (!lKey.verifyChecksum())
            {
                System.out.println("Invalid licence key checksum");
                System.exit(1);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(2);
        }

        String sep = System.getProperty("file.separator", "/");
        String path = System.getProperty("user.dir", ".");
        String filename = path + sep + REGISTRATION_FILENAME;


        if (info.load(filename) == false) // no reg file, or no licence key in it
           info._licenceKey = lKey.toString();

        JFrame frame = new JFrame("Register User Information");
        RegisterUserPanel regUserPanel = new RegisterUserPanel(frame, info);
        frame.getContentPane().add(regUserPanel);
        frame.pack();
        frame.show();
    }
*/
    private void initControls()
    {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Display the description info
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets.right = 10;
        gbc.insets.top = 10;
        gbc.gridx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        for (int i = 0; i < _descLines.length; i++)
            add(new JLabel( _descLines[i]), gbc);

        // constraints for new JLabel("Customer Licence Key:")
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.gridwidth = 1;
        gbc.gridx = GridBagConstraints.RELATIVE;
        gbc.insets.right = 10;
        gbc.insets.top = 10;
        add(new JLabel("Customer Licence Key:"), gbc);

        // constraints for _licenceKey
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.left = 10;
        add(_licenceKey, gbc);

        // Constraints for Opt/Mand
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets.right = 10;
        add(new JLabel(" "), gbc);

        // constraints for new JLabel("Company Name:")
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.insets.left = 0;
        add(new JLabel("Company Name:"), gbc);

        // constraints for _coName
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.left = 10;
        add(_coName, gbc);

        // Constraints for Opt/Mand
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets.right = 10;
        add(new JLabel("*"), gbc);

        // constraints for new JLabel("User Name:")
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        add(new JLabel("User Name:"), gbc);

        // constraints for _userName
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.left = 10;
        add(_userName, gbc);

        // Constraints for Opt/Mand
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets.right = 10;
        add(new JLabel("*"), gbc);

        // constraints for new JLabel("Contact EMail Address:")
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        add(new JLabel("Contact EMail Address:"), gbc);

        // constraints for _email
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.left = 10;
        add(_email, gbc);

        // Constraints for Opt/Mand
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets.right = 10;
        add(new JLabel("*"), gbc);

        // constraints for new JLabel("Company Phone:")
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        add(new JLabel("Company Phone:"), gbc);

        // constraints for _phone
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.left = 10;
        add(_phone, gbc);

        // Constraints for Opt/Mand
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets.right = 10;
        add(new JLabel(" "), gbc);

        // constraints for new JLabel("Company Fax:")
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        add(new JLabel("Company Fax:"), gbc);

        // constraints for _fax
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.left = 10;
        add(_fax, gbc);

        // Constraints for Opt/Mand
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets.right = 10;
        add(new JLabel(" "), gbc);

        // constraints for new JLabel("Company Address:")
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        add(new JLabel("Company Address:"), gbc);

        // constraints for _address
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets.left = 10;
        add(_address, gbc);
//        _address.setBorder(BorderFactory.createEtchedBorder(Color.lightGray, Color.white));
        _address.setBorder(_fax.getBorder());

        // Constraints for Opt/Mand
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets.right = 10;
        add(new JLabel(" "), gbc);

        // constraints for new JLabel("Number of Users:")
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        add(new JLabel("Number of Users:"), gbc);

        // constraints for _users
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.left = 10;
        add(_users, gbc);

        // Constraints for Opt/Mand
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets.right = 10;
        add(new JLabel(" "), gbc);

        // constraints for new JLabel("Required Key Type:")
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        add(new JLabel("Required Key Type:"), gbc);

        // constraints for _chkPerm
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.left = 10;
        add(_radPerm, gbc);

        // constraints for _chkTemp
        gbc.gridx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets.top = 0;
        add(_radTemp, gbc);

        // create button panel
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(_btnRegister);
        panel.add(_btnCancel);

        // constraints for button panel
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.top = 10;
        gbc.insets.left = 0;
        gbc.insets.right = 0;
        add(panel, gbc);

    }

    private void transferData(boolean toControls)
    {
        if (toControls)
        {
            _licenceKey.setText(_info._licenceKey);
            _coName.setText(_info._coName);
            _userName.setText(_info._userName);
            _email.setText(_info._email);
            _phone.setText(_info._phone);
            _fax.setText(_info._fax);
            _address.setText(_info._address);
            _users.setText(Short.toString (_info._users));
            _radPerm.setSelected(_info._expiry == 0);
            _radTemp.setSelected(!(_info._expiry == 0));
        }
        else
        {
            _info._coName = _coName.getText();
            _info._userName = _userName.getText();
            _info._email = _email.getText();
            _info._phone = _phone.getText();
            _info._fax = _fax.getText();
            _info._address = _address.getText();

            try
            {
                _info._users = Short.parseShort (_users.getText());
            }
            catch (NumberFormatException e)
            {
                _info._users = 1;
            }
        }
    }

    private boolean validateData()
    {
        // make sure all compulsory fields are valid
        boolean ret = true;
        StringBuffer msg = new StringBuffer("Not all mandatory fields have been completed.  Please complete:\n");

        if (_info._coName == null || _info._coName.trim().length() <= 0)
        {
            msg.append("- Company Name\n");
            ret = false;
        }

        if (_info._userName == null || _info._userName.trim().length() <= 0)
        {
            msg.append("- User Name\n");
            ret = false;
        }

        if (_info._email == null || _info._email.trim().length() <= 0)
        {
            msg.append("- EMail Address\n");
            ret = false;
        }

        if (ret == false)
           JOptionPane.showMessageDialog(this, msg.toString());

        return ret;
    }

    private void onRegister()
    {
        transferData(false);
        if (!validateData())
           return;

        try
        {
            _info.saveFile (_filename);
        }
        catch (Exception e)
        {
            StringBuffer buf = new StringBuffer("An error occured while saving the registration information.\n");
            buf.append("Please contact WAP Solutions Ltd. and quote the following message:\n");
            buf.append(e.getMessage());
            JOptionPane.showMessageDialog(this, buf.toString());
            System.exit(0);
        }

        StringBuffer buf = new StringBuffer("The file " + _filename + " has been created for you, containing your\n");
        buf.append("registration information.  Please attach, or copy the contents of this file to an email addressed to\n");
        buf.append(WAP_EMAIL_ADDRESS + ",  or alternatively print the file and fax it to " + WAP_FAX_NUM);
        JOptionPane.showMessageDialog(this, buf.toString());
        System.exit(0);
    }

    private void onCancel()
    {
        int ret = JOptionPane.showConfirmDialog(this, "Are you sure you want to quit without generating the user information?");
        if (ret == JOptionPane.YES_OPTION)
           System.exit(0);
    }

    public Dimension getPreferredSize()
    {
        return _dim;
    }
}
