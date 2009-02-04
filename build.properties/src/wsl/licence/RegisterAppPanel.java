/*	$Id: RegisterAppPanel.java,v 1.2 2004/02/12 22:28:38 jonc Exp $
 */
package wsl.licence;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.stanford.cs.ejalbert.BrowserLauncher;

import wsl.JLinkLabelLibrary.JLinkAction;
import wsl.JLinkLabelLibrary.JLinkLabel;
import wsl.JLinkLabelLibrary.JLinkLabelActionListener;
import wsl.fw.util.Validate;
import wsl.licence.Store.InvalidStoreException;
import wsl.mdn.dataview.ResultWrapper;
import wsl.mdn.licence.LicenseRemoteCallManager;
import wsl.mdn.licence.MdnLicenceManager;

/**
 * Title:        Mobile Data Now
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      WAP Solutions Ltd
 * @version $Revision: 1.2 $
 */

public class RegisterAppPanel extends JPanel
{

	//private JTextField _licenceKey;
    //private JTextField _activationKey;
    //private JTextField _productID;
    //TODO
	private JTextField _expiry;
    private JTextField _numUsers;

    //private JButton _btnRegister;
    private JButton _btnSetKey;
    private JButton _btnCancel;
    private Dimension _dim = new Dimension(400, 450);
    private JDialog _parent;
    private Dialog _dlgParent;
    private ActivationKey _aKey = null;
    private Boolean _publicGroup = null;
    private int _availablePublicMessages = 0;
    private int _installationReferenceNumber = 0;
    private LicenceKey _lKey = null;
    private String _registerdEmailAddress = null;
    private JLinkLabel _findRefNumber = null;
    private JButton _btnGetInstallations;
    private JComboBox _lstRefs = null;

    private static final String TXT_PERMANENT = "Permanent";
    private static final String TXT_EXPIRES_PREFIX = "Expires ";

    private JRadioButton _radFirst;
    private JRadioButton _radExisting;
	private JTextField _userID;
	private JTextField _existingUserID;
	//private JTextField _reinstallReference;
	
	
    public static final String _headerLines0[] =
    {
        "Important! Please enter a valid e-mail address "
    };

    public static final String _headerLines1[] =
    {
        "as your MDN account information will be sent to it."
    }; 

    public static final String _headerLines2[] =
    {
        "Remember to check your inbox after installation."
    };	    
    
    /*public static final String _headerLines3[] =
    {
        "If this is reinstall, please enter the installation reference."
    };*/  	
    private JRadioButton _chkNewInstall = new JRadioButton("This is installed on a new computer");
    private JRadioButton _chkReInstall = new JRadioButton("This is an upgrade or re-install of an existing installation");
    /*public static final String _seperator[] =
    {
        "________________________________________________"
    };  
    
    public static final String _descLines[] =
    {
        "Please enter your Registered Email Address."
    };*/

    public RegisterAppPanel(JDialog parent, LicenceKey lKey)
    {
        this(parent, lKey, null);
    }
    public RegisterAppPanel(Dialog parent, LicenceKey lKey, ActivationKey aKey)
    {
        _lKey = lKey;
        _aKey = aKey;

        _dlgParent = parent;
        
        _radFirst = new JRadioButton("First time installation?");
        _radExisting = new JRadioButton("Use existing MDN account");	        
        ButtonGroup bg = new ButtonGroup();
        bg.add(_radFirst);
        bg.add(_radExisting);
        
        _userID = new JTextField();
        _userID.setEditable(true);
        _existingUserID = new JTextField();
        _existingUserID.setEditable(true);
        
        //_reinstallReference = new JTextField();
        //_reinstallReference.setEditable(true);
        
        //_licenceKey = new JTextField();
		//_licenceKey.setEditable(false);

        //_activationKey = new JTextField();
       // _activationKey.setEditable(true);

        //_productID = new JTextField();
		//_productID.setEditable (false);
        _expiry = new JTextField();
		_expiry.setEditable (false);
        _numUsers = new JTextField();
		_numUsers.setEditable (false);

		//_btnRegister = new JButton("Register");
		/*_btnRegister.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) { onRegister(); }
        });*/
		
		_btnSetKey = new JButton("Activate");
        _btnSetKey.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) { onSetKey(); }
        });

        _btnCancel = new JButton("Close");
        _btnCancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) { onCancel(); }
        });

		_btnGetInstallations = new JButton("Get Installation References");
		_btnGetInstallations.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) { getInstallations(); }
        });  
		_btnGetInstallations.setEnabled(false);
        
        initControls();
        transferData(true);

    }
    public RegisterAppPanel(JDialog parent, LicenceKey lKey, ActivationKey aKey)
    {
        _lKey = lKey;
        _aKey = aKey;

        _parent = parent;
        
        _radFirst = new JRadioButton("First time installation?");
        _radFirst.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) { onRadioButtonChanged(e); }
        });   
        
        _radExisting = new JRadioButton("Use existing MDN accout");	        
        _radExisting.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) { onRadioButtonChanged(e); }
        });
        ButtonGroup bg = new ButtonGroup();
        bg.add(_radFirst);
        bg.add(_radExisting);

        _chkNewInstall.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) { onRadioButtonChanged(e); }
        });        
        
        _chkReInstall.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) { onRadioButtonChanged(e); }
        });

        ButtonGroup bg2 = new ButtonGroup();
        bg2.add(_chkNewInstall);
        bg2.add(_chkReInstall);     
        _chkNewInstall.setEnabled(false);
        _chkReInstall.setEnabled(false);
        
        _userID = new JTextField();
        _userID.setEditable(false);
        _existingUserID = new JTextField();
        _existingUserID.setEditable(false);
        
        //_reinstallReference = new JTextField();
        //_reinstallReference.setEditable(false);
        
        //_licenceKey = new JTextField();
		//_licenceKey.setEditable(false);

        //_activationKey = new JTextField();
       // _activationKey.setEditable(true);

        //_productID = new JTextField();
		//_productID.setEditable (false);
        _expiry = new JTextField();
		_expiry.setEditable (false);
        _numUsers = new JTextField();
		_numUsers.setEditable (false);
		
		_findRefNumber = new JLinkLabel();
		JLinkAction action = new JLinkAction();
		action.putValue(Action.NAME, "Get more reference number details?");
		
		_lstRefs = new JComboBox();
		
		_findRefNumber.setAction(action);	
		_findRefNumber.addActionListener( new JLinkLabelActionListener()
			{
				public void actionPerformed(ActionEvent e) { 
					goToSecureLoginLink();

				}
			}
		);

		//_btnRegister = new JButton("Register");
		/*_btnRegister.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) { onRegister(); }
        });*/
		
		_btnSetKey = new JButton("Activate");
        _btnSetKey.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) { onSetKey(); }
        });

        _btnCancel = new JButton("Close");
        _btnCancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) { onCancel(); }
        });
        
		_btnGetInstallations = new JButton("Get Installation References");
		_btnGetInstallations.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) { getInstallations(); }
        }); 
		_btnGetInstallations.setEnabled(false);
		
        initControls();
        transferData(true);

    }
	protected void goToSecureLoginLink() {
		String userID = null;
		if (_radFirst.isSelected()){
			userID = _userID.getText().trim();
		}else{
			userID = _existingUserID.getText().trim();
		}
        
        if (userID == null || userID.isEmpty()){
        	JOptionPane.showMessageDialog(this, "Please enter the email address.");
        	return;
        }else{
        	if (!Validate.validateEmailAddress(userID)){
            	JOptionPane.showMessageDialog(this, "Email address is not valid.");
            	return;        		
        	}
        }
        
		LicenceKey lKey = null;
		try {
			lKey = MdnLicenceManager.getLicenceKey(MdnLicenceManager.PROD_CODE_MDN_SERVER);
			if (lKey == null){
			    lKey = MdnLicenceManager.makeLicenceKey(MdnLicenceManager.PROD_CODE_MDN_SERVER); // new installation
				//JOptionPane.showMessageDialog((Component)null, "valid registration key1: [" + lKey + "]" );
			}else //if (!MdnLicenceManager.isValidLicenceKey(lKey))
			{
				LicenceKey lTmp = MdnLicenceManager.makeLicenceKey(lKey.getProductCode());
				if (!lKey.equals(lTmp)){
					JOptionPane.showMessageDialog((Component)null, "Fatal Error: Invalid registration key: [" + lKey + "][" + lTmp + "]");
				    return;					
				}
			}
		} catch (HeadlessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog((Component)null, "Fatal Error: Invalid registration key (HeadlessException): [" + lKey + "]");
			return;
		} catch (Exception e) {
			//If this is store problem, then it is because new installation
			if (e instanceof InvalidStoreException){
			    try {
					lKey = MdnLicenceManager.makeLicenceKey(MdnLicenceManager.PROD_CODE_MDN_SERVER);
					if (lKey == null){
						JOptionPane.showMessageDialog((Component)null, "Fatal Error: can not create registration key: ");
					    return;						
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					JOptionPane.showMessageDialog((Component)null, "Fatal Error after creating license key: Invalid registration key [" + lKey + "] (" + e1.toString() + ")");
					return;					
				} // new installation
				//JOptionPane.showMessageDialog((Component)null, "valid registration key2: [" + lKey + "]" );				
			}else{
				e.printStackTrace();
				JOptionPane.showMessageDialog((Component)null, "Fatal Error: Invalid registration key [" + lKey + "] (" + e.toString() + ")");
				StackTraceElement[] elements = e.getStackTrace();
				for (int j = 0; (j < elements.length && j < 2); j++){
					JOptionPane.showMessageDialog((Component)null, "Fatal Error: Invalid registration key [" + lKey + "] (" + elements[j] + ")");
				}
				
				return;
			}
		}        
		ResultWrapper ret = LicenseRemoteCallManager.getSecureLoginLink(userID, lKey.toString());
        if (ret.getErrorMsg() != null){
        	JOptionPane.showMessageDialog((Component)null, ret.getErrorMsg());
        	return;
        }
        
        String loginLink = ret.getSecureLoginLink();		
		try {
			if (loginLink != null)
				BrowserLauncher.openURL(loginLink);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
	}
	private void onRadioButtonChanged(ActionEvent e) {
		if(_radFirst.isSelected()){
			_userID.setEditable(true);
			_existingUserID.setEditable(false);
			//_reinstallReference.setEditable(false);
			_chkReInstall.setSelected(false);
			_chkNewInstall.setSelected(false);
			_chkNewInstall.setEnabled(false);
			_chkReInstall.setEnabled(false);
			_btnGetInstallations.setEnabled(false);			
		}else if (_radExisting.isSelected()){
			_userID.setEditable(false);
			_existingUserID.setEditable(true);
			_chkNewInstall.setEnabled(true);
			_chkReInstall.setEnabled(true);
			//_reinstallReference.setEditable(false);	
			_btnGetInstallations.setEnabled(false);
		}
		
		//if (e.getSource() == _chkReInstall){
			if (_chkReInstall.isSelected()){
				//_reinstallReference.setEditable(true);
				_btnGetInstallations.setEnabled(true);
			}else if (_chkNewInstall.isSelected()){
				//_reinstallReference.setEditable(false);
				_btnGetInstallations.setEnabled(false);
			}
		//}
		
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
               JOptionPane.showMessageDialog(null, "User licence key has not been generated.  Cannot continue.");
               System.exit(1);
            }

            if (!lKey.verifyChecksum())
            {
                JOptionPane.showMessageDialog(null, "Invalid licence key checksum");
                System.exit(1);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(2);
        }

        info._licenceKey = lKey.toString();

        Frame frame = new Frame();
        frame.setVisible(false);
        JDialog dlg = new JDialog(frame, "Register Application");
        RegisterAppPanel panel = new RegisterAppPanel(dlg, info);
        dlg.getContentPane().add(panel);
        dlg.pack();
        dlg.setModal(true);
        dlg.show();
    }
*/

    private void initControls()
    {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Display the radio button
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets.right = 10;
        gbc.insets.top = 10;
        gbc.insets.left = 10;
        gbc.gridx = 0;
        gbc.gridwidth = GridBagConstraints.NONE;
        add(_radFirst, gbc);    
        
        /*gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets.right = 10;
        add(new JLabel(" "), gbc);         
        */
        // Display the description info
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets.right = 10;
        gbc.insets.top = 10;
        gbc.insets.left = 30;
        gbc.gridx = 0;
        gbc.gridwidth = GridBagConstraints.NONE;
        for (int i = 0; i < _headerLines0.length; i++)
            add(new JLabel( _headerLines0[i]), gbc);
        
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets.right = 10;
        gbc.insets.top = 0;
        gbc.insets.left = 30;
        gbc.gridx = 0;
        gbc.gridwidth = GridBagConstraints.NONE;
        for (int i = 0; i < _headerLines1.length; i++)
            add(new JLabel( _headerLines1[i]), gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets.right = 10;
        gbc.insets.top = 0;
        gbc.insets.left = 30;
        gbc.gridx = 0;
        gbc.gridwidth = GridBagConstraints.NONE;
        for (int i = 0; i < _headerLines2.length; i++)
            add(new JLabel( _headerLines2[i]), gbc);   
        
        // constraints for new JLabel("User Id:")
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets.right = 10;
        gbc.insets.top = 5;
        gbc.insets.left = 30;
        gbc.gridx = 0;
        gbc.gridwidth = GridBagConstraints.NONE;
        add(new JLabel("Valid E-mail Address:"), gbc);

        // constraints for _userId
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets.right = 10;
        gbc.insets.top = 0;
        gbc.insets.left = 30;
        gbc.gridx = 0;
        gbc.gridwidth = 200;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(_userID, gbc);

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets.right = 10;
        add(new JLabel(" "), gbc);       

        // Display the radio button
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets.right = 10;
        gbc.insets.top = 10;
        gbc.insets.left = 10;
        gbc.gridx = 0;
        gbc.gridwidth = GridBagConstraints.NONE;
        add(_radExisting, gbc);            
        

        // constraints for new JLabel("Reinstall:")
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets.right = 10;
        gbc.insets.top = 5;
        gbc.insets.left = 30;
        gbc.gridx = 0;
        gbc.gridwidth = GridBagConstraints.NONE;
        add(new JLabel("Registered E-mail:"), gbc);

        // constraints for _userId
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets.right = 10;
        gbc.insets.top = 0;
        gbc.insets.left = 30;
        gbc.gridx = 0;
        gbc.gridwidth = 200;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(_existingUserID, gbc);          

        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets.right = 10;
        gbc.insets.top = 10;
        gbc.insets.left = 30;
        gbc.gridx = 0;
        gbc.gridwidth = GridBagConstraints.NONE;
        add(_chkNewInstall, gbc);          
        
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets.right = 10;
        gbc.insets.top = 10;
        gbc.insets.left = 30;
        gbc.gridx = 0;
        gbc.gridwidth = GridBagConstraints.NONE;
        add(_chkReInstall, gbc);        
        
        // constraints for new JLabel("Reinstall:")
/*        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.gridwidth = 1;
        gbc.insets.left = 30;
        gbc.gridx = GridBagConstraints.RELATIVE;
        gbc.insets.right = 10;
        gbc.insets.top = 10;
        add(new JLabel("Installation Reference Number:"), gbc);

        // constraints for _userId
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets.left = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.left = 10;
        add(_reinstallReference, gbc);      

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets.right = 10;
        add(new JLabel(" "), gbc);   
*/        
        // constraints for new JLabel("Reinstall:")
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.gridwidth = 1;
        gbc.insets.left = 30;
        gbc.gridx = GridBagConstraints.RELATIVE;
        gbc.insets.right = 10;
        gbc.insets.top = 10;
        add(_btnGetInstallations, gbc);

        // constraints for _userId
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets.left = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.left = 10;
        add(_lstRefs, gbc);         

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets.right = 10;
        add(new JLabel(" "), gbc);        
        
/*        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.gridwidth = 1;
        gbc.insets.left = 30;
        gbc.gridx = GridBagConstraints.RELATIVE;
        gbc.insets.right = 10;
        gbc.insets.top = 10;
        add(_findRefNumber, gbc);       

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets.right = 10;
        add(new JLabel(" "), gbc);
*/
        // create button panel
        JPanel panel = new JPanel(new FlowLayout());
        //panel.add(_btnRegister);
        panel.add(_btnSetKey);
        panel.add(_btnCancel);

        // constraints for button panel
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.top = 10;
        gbc.insets.left = 0;
        gbc.insets.right = 0;
        add(panel, gbc);

    }
    
    private void getInstallations(){
		String userID = null;
		if (_radFirst.isSelected()){
			userID = _userID.getText().trim();
		}else{
			userID = _existingUserID.getText().trim();
		}
        
        if (userID == null || userID.isEmpty()){
        	JOptionPane.showMessageDialog(this, "Please enter the email address.");
        	return;
        }else{
        	if (!Validate.validateEmailAddress(userID)){
            	JOptionPane.showMessageDialog(this, "Email address is not valid.");
            	return;        		
        	}
        }
        
        
		ResultWrapper ret = LicenseRemoteCallManager.getInstallations(userID);
        if (ret.getErrorMsg() != null){
        	JOptionPane.showMessageDialog((Component)null, ret.getErrorMsg());
        	return;
        }else{
        	HashMap<String, String> installations = ret.getInstallations();
        	Iterator ite = installations.entrySet().iterator();
        	while (ite.hasNext()){
        		Map.Entry o = (Map.Entry)ite.next();
        		_lstRefs.addItem(o);
        	}
        	
        }
    	
    }

    private void transferData(boolean toControls)
    {
        if (toControls)
        {
            //_licenceKey.setText(_lKey != null ? _lKey.toString() : "INVALID");

            updateFields();
        }
        else
        {
//            String a = _activationKey.getText().trim();
//            validateKey(_lKey.toString(), a);
            
    		String userID = null;
    		String installRef = null;
    		boolean existingAccount = false;
    		if (_radFirst.isSelected()){
    			userID = _userID.getText().trim();
    			existingAccount = false;
    		}else{
    			userID = _existingUserID.getText().trim();
    			if (_chkReInstall.isSelected()){
    				//installRef = _reinstallReference.getText().trim();
    				//if (installRef.isEmpty()){
        				Map.Entry o = (Map.Entry)_lstRefs.getSelectedItem();
        				installRef = (String)o.getKey();    					
    				//}
    			}
    			existingAccount = true;
    		}
            
            if (userID == null || userID.isEmpty()){
            	JOptionPane.showMessageDialog(this, "Please enter the email address.");
            	return;
            }else{
            	if (!Validate.validateEmailAddress(userID)){
                	JOptionPane.showMessageDialog(this, "Email address is not valid.");
                	return;        		
            	}
            }           
        	
            ResultWrapper result = LicenseRemoteCallManager.validateUser(userID, _lKey.toString(), installRef);
            ActivationKey key = (ActivationKey)result.getObject();
            Boolean publicGroup = (Boolean)result.getPublicGroupBoolean();
            String msg = result.getErrorMsg();
            if (msg != null){
          	
            	if (msg.equalsIgnoreCase("The user does not exist")){
            		if (!existingAccount){
	            		String optionButtons[] =
	                    {
	                        "OK",
	                        "Cancel"
	                    }; 
	            		int rv = JOptionPane.showOptionDialog(null, "You have not registered at www.mobiledatanow.com yet. Your email address will be registered automatically.",
	                            "Create an account", JOptionPane.YES_NO_OPTION,
	                            JOptionPane.PLAIN_MESSAGE, null,
	                            optionButtons, optionButtons[0]);  
	            		
	            		if (rv == 0){
	            			ResultWrapper createResult = LicenseRemoteCallManager.createAnAccount(userID, _lKey.toString(), this);
	            			key = (ActivationKey)createResult.getObject();
	            			publicGroup = (Boolean)createResult.getPublicGroupBoolean();
	                        if (key != null){
	                        	_aKey = key;
	                        	_publicGroup = publicGroup;
	                        	_registerdEmailAddress = userID;
	                        }            			
	            			msg = createResult.getErrorMsg();
	            			if (msg != null){
	            				JOptionPane.showMessageDialog(this, msg);
	            			}
	            		}
            		}
            		else{
                    	JOptionPane.showMessageDialog(this, msg);
                    }
            	}else{
            		JOptionPane.showMessageDialog(this, msg);
            	}           	
            }
            
            if (key != null){
            	_aKey = key;
            	_publicGroup = publicGroup;
            	_registerdEmailAddress = userID;
            	_availablePublicMessages = result.getAvailablePublicMessages();
            	_installationReferenceNumber = result.getInstallationReferenceNumber();
            }            
        }
    }   


	private boolean validateKey(String licenceKey, String activationKey)
    {
        // make sure all compulsory fields are valid
        boolean ret = true;
        String msg = "";

        if (activationKey == null || activationKey.length() < ActivationKey.ENCODED_LENGTH)
        {
            msg = "Incomplete activation key.  Cannot continue.";
            ret = false;
        }
        else
        {
            try
            {
                ActivationKey aKey = new ActivationKey(activationKey, licenceKey);
                if (!aKey.verifyChecksum())
                {
                    msg = "Invalid activation key checksum.";
                    ret = false;
                }

                _aKey = aKey;
            }
            catch (Exception e)
            {
                msg = "Corrupt activation key." + e.getMessage();
                ret = false;
            }

        }


        if (ret == false)
           JOptionPane.showMessageDialog(this, msg);

        return ret;
    }

    private void onRegister()
    {
//    	JEditorPane editorPane = new JEditorPane();
//    	editorPane.setEditable(false);
//    	URL url = null;
//		try {
//			url = new URL("http://www.mobiledatanow.com/mdn-user-installations");
////			JTextField.class.getResource("http://www.mobiledatanow.com/mdn-user-installations");
//		} catch (MalformedURLException e2) {
//			// TODO Auto-generated catch block
//			e2.printStackTrace();
//		}
//    		
//    	try {
//			editorPane.setPage(url);
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			System.err.println("Couldn't find file: " + url);
//		}
//		
////		Put the editor pane in a scroll pane.
//		JScrollPane editorScrollPane = new JScrollPane(editorPane);
//		editorScrollPane.setVerticalScrollBarPolicy(
//		                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//		editorScrollPane.setPreferredSize(new Dimension(250, 145));
//		editorScrollPane.setMinimumSize(new Dimension(10, 10));
    	
    	String osName = System.getProperty("os.name");
        String url = "http://www.mobiledatanow.com/user/register";
        try {
           if (osName.startsWith("Mac OS")) {
              Class fileMgr = Class.forName("com.apple.eio.FileManager");
              Method openURL = fileMgr.getDeclaredMethod("openURL",
                 new Class[] {String.class});
              openURL.invoke(null, new Object[] {url});
              }
           else if (osName.startsWith("Windows"))
              Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
           else { //assume Unix or Linux
              String[] browsers = {
                 "gnome-open", "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape" };
              String browser = null;
              for (int count = 0; count < browsers.length && browser == null; count++)
                 if (Runtime.getRuntime().exec(
                       new String[] {"which", browsers[count]}).waitFor() == 0)
                    browser = browsers[count];
              if (browser == null)
                 throw new Exception("Could not find web browser");
              else
                 Runtime.getRuntime().exec(new String[] {browser, url});
              }
        }
        catch (Exception e) {
           JOptionPane.showMessageDialog(null, e.getLocalizedMessage());
        }    	
    }    
    
    private void onSetKey()
    {
        transferData(false);
        updateFields();
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

    private void updateFields()
    {
        if (_aKey == null)
            return;

        if (!_aKey.verifyChecksum())
        {
            System.out.println("Invalid activation key");
            return;
        }

        //_activationKey.setText(_aKey.toString());
        //_productID.setText(Integer.toString(_aKey.getProductCode()));
        int expiry = _aKey.getExpiry();
        _expiry.setText(expiry == 0 ? TXT_PERMANENT : TXT_EXPIRES_PREFIX+Integer.toString(expiry));
        _numUsers.setText(Integer.toString(_aKey.getNumUsers()));
    }

    public ActivationKey getActivationKey()
    {
        return _aKey;
    }
    public String getRegisterdEmailAddress()
    {
        return _registerdEmailAddress;
    }    
/*
    public class ResultWrapper {

    	private Object object = null;
    	private Boolean publicGroupBoolean = null;
    	private String errorMsg = null;
    	private String errorMsg2 = null;*/
    	/**
    	 * Constructor with only one error message
    	 * @param object
    	 * @param errorMsg
    	 */
    	/*public ResultWrapper(Object object, String errorMsg) {
    		setObject(object);
    		setErrorMsg(errorMsg);
    	}*/
    	/**
    	 * Constructor with two error messages
    	 * @param object
    	 * @param errorMsg
    	 * @param errorMsg2
    	 */
    	/*public ResultWrapper(Object object, String errorMsg, String errorMsg2) {
    		setObject(object);
    		setErrorMsg(errorMsg);
    		setErrorMsg2(errorMsg2);
    	}*/
    	/**
    	 * 
    	 * @param object
    	 * @param publicGroupObject
    	 * @param errorMsg
    	 */
    	/*public ResultWrapper(Object object, Boolean publicGroupObject, String errorMsg) {
    		setObject(object);
    		setPublicGroupBoolean(publicGroupObject);
    		setErrorMsg(errorMsg);
    	}*/
    	/**
    	 * 
    	 * @param object
    	 * @param publicGroupObject
    	 * @param errorMsg
    	 * @param errorMsg2
    	 */
    	/*public ResultWrapper(Object object, Boolean publicGroupObject, String errorMsg, String errorMsg2) {
    		setObject(object);
    		setPublicGroupBoolean(publicGroupObject);
    		setErrorMsg(errorMsg);
    		setErrorMsg2(errorMsg2);
    	}
    	public String getErrorMsg() {
    		return errorMsg;
    	}
    	public void setErrorMsg(String errorMsg) {
    		this.errorMsg = errorMsg;
    	}

    	public String getErrorMsg2() {
    		return errorMsg2;
    	}
    	public void setErrorMsg2(String errorMsg2) {
    		this.errorMsg2 = errorMsg2;
    	}
    	public Object getObject() {
    		return object;
    	}
    	public void setObject(Object object) {
    		this.object = object;
    	}
		public Boolean getPublicGroupBoolean() {
			return publicGroupBoolean;
		}
		public void setPublicGroupBoolean(Boolean publicGroupBoolean) {
			this.publicGroupBoolean = publicGroupBoolean;
		}

    }*/
	public Boolean getPublicGroup() {
		return _publicGroup;
	}
	public int getAvailablePublicMessages() {
		return _availablePublicMessages;
	}
	public void setAvailablePublicMessages(int publicMessages) {
		_availablePublicMessages = publicMessages;
	}
	public int getInstallationReferenceNumber() {
		return _installationReferenceNumber;
	}
	public void setInstallationReferenceNumber(int referenceNumber) {
		_installationReferenceNumber = referenceNumber;
	}
}
