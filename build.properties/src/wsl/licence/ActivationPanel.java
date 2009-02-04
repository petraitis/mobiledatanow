/*	$Id: ActivationPanel.java,v 1.1.1.1 2002/02/22 02:51:56 jonc Exp $
 *
 */
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

import java.sql.Connection;

/**
 * Title:        Mobile Data Now
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      WAP Solutions Ltd
 * @author Paul Lupton
 * @version 1.0
 */

public class ActivationPanel extends JPanel
{
	/*
	 *
	 */
	Connection _db;

	/*
	 *	Swing components
	 */
    private JTextField _licenceKeyField;
    private JTextField _origProdCode;
    private JTextField _newProdCode;
    private JTextField _coName;
    private JTextField _userName;
    private JTextField _email;
    private JTextField _phone;
    private JTextField _fax;
    private JTextArea _address;
    private JTextField _users;
    private JRadioButton _radPerm;
    private JRadioButton _radTemp;
    private JTextField _expiry;
    private JTextField _activationKeyField;
    private JButton _btnGenKey;
    private JButton _btnLoad;
    private JButton _btnSave;
    private JButton _btnClear;
    private JButton _btnCancel;
    private Dimension _dim = new Dimension(600, 500);
    private UserInfo _info;
    private JDialog _parent;

    private int _iProdCode = 0;
    private int _iExpiry = 0;

    private LicenceKey _lKey;
    private ActivationKey _aKey;

    public static final String _descLines[] =
    {
        "Please enter your activation code."
    };


    /**
     * Constructor.
     */
    public
	ActivationPanel (
	 Connection db,
	 JDialog parent)
    {
        this (db, parent, null);
    }

    /**
     * Constructor.  UserInfo parameter can be null.
     */
    public
	ActivationPanel (
	 Connection db,
	 JDialog parent,
	  UserInfo info)
    {
		_db = db;

        if (info == null)
            _info = new UserInfo();
        else
            _info = info;

        if (_info != null && _info._licenceKey != null && _info._licenceKey.length() == LicenceKey.ENCODED_LENGTH)
        {
            try
            {
                _lKey = new LicenceKey(info._licenceKey);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Invalid registration key.  Unable to decrypt.");
            }
            if (!_lKey.verifyChecksum())
            {
                JOptionPane.showMessageDialog(null, "Invalid registration key checksum.");
            }
        }
        else
        {
            _lKey = null;
        }

        _parent = parent;

        _origProdCode = new JTextField();
        _newProdCode = new JTextField();
        _licenceKeyField = new JTextField();
        _licenceKeyField.setEditable(false);

        _coName = new JTextField();
        _userName = new JTextField();
        _email = new JTextField();
        _phone = new JTextField();
        _fax = new JTextField();
        _address = new JTextArea();
        _address.setSize(new Dimension(200, 100));
        _address.setPreferredSize(new Dimension(200, 100));
        _users = new JTextField();
        _expiry = new JTextField();
        _activationKeyField = new JTextField();
        _activationKeyField.setEditable(false);

        _radPerm = new JRadioButton("Permanent");
        _radTemp = new JRadioButton("Temporary");
        ButtonGroup bg = new ButtonGroup();
        bg.add(_radPerm);
        bg.add(_radTemp);

        _btnLoad = new JButton("Load");
        _btnLoad.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) { onLoad(); }
        });

        _btnGenKey = new JButton("Generate");
        _btnGenKey.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) { onGenerate(); }
        });

        _btnSave = new JButton("Save");
        _btnSave.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) { onSave(); }
        });

        _btnClear = new JButton("Clear");
        _btnClear.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) { onClear(); }
        });

        _btnCancel = new JButton("Close");
        _btnCancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) { onCancel(); }
        });

        initControls();
        transferData(true);

    }

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

        // constraints for _licenceKeyField
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.left = 10;
        add(_licenceKeyField, gbc);

        // Constraints for Opt/Mand
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets.right = 10;
        add(new JLabel(" "), gbc);

        // Constraints for Original Product code
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.insets.left = 0;
        add(new JLabel("Original Prod Code:"), gbc);

        // Constraints for Original Product code
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.left = 10;
        add(_origProdCode, gbc);

        // Constraints for New Product code
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets.right = 10;
        add(new JLabel(" "), gbc);

        // Constraints for New Product code
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.insets.left = 0;
        add(new JLabel("New Prod Code:"), gbc);

        // Constraints for Original Product code
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.left = 10;
        add(_newProdCode, gbc);

        // Constraints for New Product code
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
        add(new JLabel(" "), gbc);

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
        add(new JLabel(" "), gbc);

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
        add(new JLabel(" "), gbc);

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

        // constraints for expiry
        gbc.gridwidth = 1;
        gbc.gridx = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        add(new JLabel("Expires:"), gbc);

        // constraints for expiry
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.left = 10;
        add(_expiry, gbc);

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets.right = 10;
        add(new JLabel("eg. 20010923"), gbc);


        // constraints for activation key
        gbc.gridwidth = 1;
        gbc.gridx = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        add(new JLabel("Activation Key:"), gbc);

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.left = 10;
        add(_activationKeyField, gbc);

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets.right = 10;
        add(new JLabel(""), gbc);

        // create button panel
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(_btnLoad);
        panel.add(_btnGenKey);
        panel.add(_btnSave);
        panel.add(_btnClear);
        panel.add(_btnCancel);

        // constraints for button panel
        gbc.gridwidth = 3;
        gbc.gridx = 0;
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
            _origProdCode.setText(_lKey != null ? String.valueOf(_lKey.getProductCode()) : "");
            _newProdCode.setText(_aKey != null ? String.valueOf(_aKey.getProductCode()) : "");

            _licenceKeyField.setText(_info._licenceKey);
            _coName.setText(_info._coName);
            _userName.setText(_info._userName);
            _email.setText(_info._email);
            _phone.setText(_info._phone);
            _fax.setText(_info._fax);
            _address.setText(_info._address);
            _users.setText (Short.toString (_info._users));
            _radPerm.setSelected(_info._expiry == 0);
            _radTemp.setSelected(!(_info._expiry != 0));
            _activationKeyField.setText(_info._activationKey);
            _expiry.setText (Integer.toString (_info._expiry));
        }
        else
        {
            _info._coName = _coName.getText();
            _info._userName = _userName.getText();
            _info._email = _email.getText();
            _info._phone = _phone.getText();
            _info._fax = _fax.getText();
            _info._address = _address.getText();
            _info._activationKey = _activationKeyField.getText();
			_info._expiry = Integer.parseInt (_expiry.getText ());

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

        String oldProdCode = _origProdCode.getText();
        String newProdCode = _newProdCode.getText();
        String expiry = _expiry.getText();

        if (newProdCode == null || newProdCode.length() <= 0)
           newProdCode = oldProdCode;

        try
        {
            _iProdCode = Integer.parseInt(newProdCode);
        }
        catch (Exception e)
        {
            msg.append("- Invalid Product Code\n");
            ret = false;
        }

        try
        {
            if (_info._expiry == 0)
               _iExpiry = ActivationKey.PERM_KEY;
            else
               _iExpiry = Integer.parseInt(expiry);

        }
        catch (Exception e)
        {
            msg.append("- Invalid Expiry\n");
            ret = false;
        }

        if (ret == false)
           JOptionPane.showMessageDialog(this, msg.toString());

        return ret;
    }

    /**
     * Generate an activation key
     */
    private void onGenerate()
    {
        transferData(false);
        if (!validateData())
           return;

        // now generate the activation key
        try
        {
            _aKey = new ActivationKey (_iProdCode, _info._users, _iExpiry, _lKey);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error while generating the activation key");
            return;
        }

        if (_aKey.verifyChecksum() == true)
        {
            _info._activationKey = _aKey.toString();
            _activationKeyField.setText(_aKey.toString());
        }
    }

    /**
     * Load an activation key
     */
    private void onLoad()
    {
        JDialog dlg = new JDialog(_parent, "Enter licence key");

        LicenceKeyPanel panel = new LicenceKeyPanel(dlg);
        dlg.getContentPane().add(panel);
        dlg.pack();
        dlg.setModal(true);
        dlg.show();

        LicenceKey lKey = panel.getLicenceKey();
        if (lKey == null)
            return;

        onClear();

        _lKey = lKey;
        _info._licenceKey = lKey.toString();
        _licenceKeyField.setText (_info._licenceKey);

        // now see if there is a user info file for this licence key
        if (_info.load (_info._licenceKey))
        {
            if (_info._activationKey != null && _info._activationKey.length() == ActivationKey.ENCODED_LENGTH)
            {
                try
                {
                    _aKey = new ActivationKey(_info._activationKey, lKey.toString());
                }
                catch (Exception e)
                {
                    _aKey = null;
                    _info._activationKey = "";
                }
            }
        }
        else
            _aKey = null;

        transferData(true);
    }

    /**
     * Save an activation key
     */
    private void onSave()
    {
        // check we have a licence key
        if (_lKey == null || !_lKey.verifyChecksum())
        {
            JOptionPane.showMessageDialog(this, "Error savings keys.  Invalid licence key.");
            return;
        }

        try
        {
            _info.saveDatabase (_db);
        }
        catch (Exception e)
        {
            StringBuffer buf = new StringBuffer("An error occured while saving the registration information.\n");
            buf.append(e.getMessage());
            JOptionPane.showMessageDialog(this, buf.toString());
        }
    }

    private void onCancel()
    {
        _parent.dispose();
    }

    private void onClear()
    {
        _aKey = null;
        _lKey = null;
        _info = new UserInfo();
        transferData(true);
    }

    public Dimension getPreferredSize()
    {
        return _dim;
    }
}
