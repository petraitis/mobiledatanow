/*	$Id: Licence.java,v 1.1.1.1 2002/02/22 02:51:56 jonc Exp $
 *
 */
package wsl.licence;


import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.BorderFactory;
import javax.swing.border.*;
import javax.swing.plaf.BorderUIResource;
import javax.swing.JOptionPane;
import javax.swing.JDialog;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.event.ActionEvent;

import java.sql.Connection;
import java.sql.DriverManager;

public class Licence
{
    public static final String ACTIVATION_FILENAME = "active.txt";
    public static final String REGISTRATION_FILENAME = "register.txt";

    private static final int PROD_CODE = 121001;

    public Licence()
    {
    }

    public static void
	main (
	 String args [])
    {
        Licence licence = new Licence();

        if (args.length > 0)
        {
            if (args[0].equalsIgnoreCase("-create"))
                licence.createStore();
            else if (args[0].equalsIgnoreCase("-register"))
                licence.registerUser();
            else if (args[0].equalsIgnoreCase("-generate"))
                licence.generateActivation();
            else if (args[0].equalsIgnoreCase("-activate"))
            	licence.activateUser();

        } else
        {
			System.out.println ("Program parameters are:");
            System.out.println ("\t-create     Create a new uninitialised key store.");
            System.out.println ("\t-register   Create the initial licence key and complete user registration file.");
            System.out.println ("\t-generate   Generate the activation key from a licence key.");
            System.out.println ("\t-activate   Activate the application by entering a valid activation key and licence key.");
            //licence.createStore();
            licence.generateActivation();
        }

		System.exit (0);
    }

    public void createStore()
    {
        try
        {
            Store store = new Store();
            store.create();
            store.setLicenceKey(new LicenceKey(PROD_CODE));
            store.store();
            System.out.println("\n\nNew store generated and saved.");
        }
        catch (Exception e)
        {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void registerUser()
    {
        UserInfo info = new UserInfo();
        Store store = null;
        LicenceKey lKey = null;

        // Get the licence key
        try
        {
            store = new Store();
            store.load();
            lKey = store.getLicenceKey(PROD_CODE);
            if (lKey == null)
            {
               System.out.println("No existing licence, so create one.");

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

        if (info.load (filename) == false) // no reg file, or no licence key in it
           info._licenceKey = lKey.toString();

        Frame frame = new Frame();
        frame.setVisible(false);
        JDialog dlg = new JDialog(frame, "Register User Information");

        RegisterUserPanel panel = new RegisterUserPanel(dlg, info);
        dlg.getContentPane().add(panel);
        dlg.pack();
        dlg.setModal(true);
        dlg.show();
    }

    public void
	generateActivation()
    {
		/*
		 *
		 */
		Config config = null;
		Connection db = null;

		try
		{
			config = new Config ("config.properties");

			/*
			 *	Attempt a database connection
			 */
			Class.forName (config.getDbDriver ());

			db = DriverManager.getConnection (
					config.getDbURL (),
					config.getDbUsername (),
					config.getDbPassword ());

		} catch (Exception e)
		{
			e.printStackTrace ();
			System.out.println ("ERR: " + e.getMessage ());
			//System.exit (1);
		}

		/*
		 *
		 */
        Frame frame = new Frame();
        frame.setVisible(false);
        JDialog dlg = new JDialog(frame, "Generate Activation Key");

        ActivationPanel panel = new ActivationPanel (db, dlg);
        dlg.getContentPane().add(panel);
        dlg.pack();
        dlg.setModal(true);
        dlg.show();
    }

    public void activateUser()
    {
        Store store = null;
        LicenceKey lKey = null;
        ActivationKey aKey = null;

        // Get the licence key
        try
        {
            store = new Store();
            store.load();

            lKey = store.getLicenceKey(PROD_CODE);
            if (lKey == null)
            {
               JOptionPane.showMessageDialog(null, "User licence key has not been generated.  Cannot continue.");
               System.exit(1);
            }

            aKey = store.getActivationKey(lKey);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(2);
        }

        Frame frame = new Frame();
        frame.setVisible(false);
        JDialog dlg = new JDialog(frame, "Register Application");

        RegisterAppPanel panel = new RegisterAppPanel(dlg, lKey, aKey);
        dlg.getContentPane().add(panel);
        dlg.pack();
        dlg.setModal(true);
        dlg.show();

        aKey = panel.getActivationKey();
        if (aKey != null)
        {
            store.setActivationKey(lKey, aKey);
            try
            {
                store.store();
            }
            catch (Exception e)
            {
                System.out.println("Error while storing keys.");
            }
        }
    }
}
