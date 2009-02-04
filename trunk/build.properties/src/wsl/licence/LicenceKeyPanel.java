package wsl.licence;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JDialog;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Title:        Mobile Data Now
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      WAP Solutions Ltd
 * @author Paul Lupton
 * @version 1.0
 */

public class LicenceKeyPanel extends JPanel
{
    private JTextField _licenceKey;
    private LicenceKey _lKey = null;
    private JButton _btnOK;
    private JButton _btnCancel;
    private Dimension _dim = new Dimension(400, 150);
    private JDialog _parent;

    public LicenceKeyPanel(JDialog parent)
    {
        _parent = parent;

        _licenceKey = new JTextField();

        _btnOK = new JButton("OK");
        _btnOK.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) { onOK(); }
        });

        _btnCancel = new JButton("Close");
        _btnCancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) { onCancel(); }
        });

        initControls();
    }

    private void initControls()
    {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // constraints for new JLabel("Customer Licence Key:")
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.gridwidth = 1;
        gbc.insets.left = 10;
        gbc.gridx = GridBagConstraints.RELATIVE;
        gbc.insets.right = 10;
        gbc.insets.top = 10;
        add(new JLabel("Customer Licence Key:"), gbc);

        // constraints for _licenceKey
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets.left = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
       gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets.left = 10;
        add(_licenceKey, gbc);

        // create button panel
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(_btnOK);
        panel.add(_btnCancel);

        // constraints for button panel
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.top = 10;
        gbc.insets.left = 0;
        gbc.insets.right = 0;
        add(panel, gbc);
    }

    private boolean validateKey(String licenceKey)
    {
        boolean ret = true;
        String msg = "";

        if (licenceKey == null || licenceKey.length() < 0)
        {
            msg = "Incomplete licence key.";
            ret = false;
        }
        else
        {
            try
            {
                LicenceKey lKey = new LicenceKey(licenceKey);
                if (!lKey.verifyChecksum())
                {
                    msg = "Invalid licence key checksum.";
                    ret = false;
                }
                else
                    _lKey = lKey;
            }
            catch (Exception e)
            {
                msg = "Corrupt licence key." + e.getMessage();
                ret = false;
            }

        }


        if (ret == false)
           JOptionPane.showMessageDialog(this, msg);

        return ret;
    }

    private void onOK()
    {
        if (validateKey(_licenceKey.getText()) == true)
        {
            if (_parent == null)
                System.exit(0);

            _parent.dispose();
        }
    }

    private void onCancel()
    {
        if (_parent == null)
            System.exit(0);

        _parent.dispose();
    }

    public Dimension getPreferredSize()
    {
        return _dim;
    }

    /**
     * Returns the licence key that was generated from the text key entered in the text field.
     * If the key is invalid, or cancel was pressed, then the return value is NULL.
     */
    public LicenceKey getLicenceKey()
    {
        return _lKey;
    }
}