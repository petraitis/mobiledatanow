package wsl.installerCustom;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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

import wsl.JLinkLabelLibrary.JLinkAction;
import wsl.JLinkLabelLibrary.JLinkLabel;
import wsl.JLinkLabelLibrary.JLinkLabelActionListener;
import wsl.fw.util.Validate;
import wsl.installerCustom.dependencies.InstallerMdnStore;
import wsl.licence.ActivationKey;
import wsl.licence.LicenceKey;
import wsl.licence.Store.InvalidStoreException;
import wsl.mdn.dataview.ResultWrapper;
import wsl.mdn.licence.LicenseRemoteCallManager;
import wsl.mdn.licence.MdnLicenceManager;

import com.zerog.ia.api.pub.CustomCodePanel;
import com.zerog.ia.api.pub.CustomCodePanelProxy;

import edu.stanford.cs.ejalbert.BrowserLauncher;

public class MdnLicencePanel extends CustomCodePanel implements ActionListener{
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean inited = false;
	private static InstallerMdnStore _store = null;
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
        "as your account information will be sent to it."
    }; 

    public static final String _headerLines2[] =
    {
        "Remember to check your inbox after installation."
    };	    
    
   /* public static final String _headerLines3[] =
    {
        "If this is reinstall, please enter the installation reference."
    };*/  
    
    private JRadioButton _chkNewInstall = new JRadioButton("This is installed on a new computer");
    private JRadioButton _chkReInstall = new JRadioButton("This is an upgrade or re-install of an existing installation");

    private JLinkLabel _findRefNumber = null;

    private JButton _btnGetInstallations;
    private JComboBox _lstRefs = null;    
    
    private URL url = null;
    private static String instDir = null;
	    
	public MdnLicencePanel() {
		
	}

	@Override
	public boolean setupUI(CustomCodePanelProxy proxy) {
		  // Use a boolean flag here to prevent duplicate GUI elements.
		  if (inited == true)
			  return true;
		  inited = true;
		  
	        _radFirst = new JRadioButton("First time installation?");
	        _radFirst.setBackground(this.getBackground());
	        _radFirst.addActionListener(new ActionListener()
	        {
	            public void actionPerformed(ActionEvent e) { onRadioButtonChanged(e); }
	        });   
	        
	        _radExisting = new JRadioButton("Use existing MDN account");	
	        _radExisting.setBackground(this.getBackground());
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
	        _chkReInstall.setBackground(this.getBackground());
	        
	        _userID = new JTextField();
	        _userID.setEditable(false);
	        _existingUserID = new JTextField();
	        _existingUserID.setEditable(false);

//	        _reinstallReference = new JTextField();
//	        _reinstallReference.setEditable(false);		  

			_findRefNumber = new JLinkLabel();
			JLinkAction action = new JLinkAction();
			action.putValue(Action.NAME, "Get more reference number details?");
			
			_findRefNumber.setAction(action);	
			_findRefNumber.addActionListener( new JLinkLabelActionListener()
				{
					public void actionPerformed(ActionEvent e) { 
						goToSecureLoginLink();
					}
				}
			);
			
			_lstRefs = new JComboBox();		
			
			_btnGetInstallations = new JButton("Get Installation References");
			_btnGetInstallations.addActionListener(new ActionListener()
	        {
	            public void actionPerformed(ActionEvent e) { getInstallations(); }
	        });  
			_btnGetInstallations.setEnabled(false);
			
			
	        initControls();
	        
	        /*String instDir = proxy.substitute("$USER_INSTALL_DIR$$/$tomcat-5.5.25$/$webapps$/$dashwell.war$/$WEB-INF$/$classes$/$wsl$/$config$/$mdn$/$MdnRmiServer.conf");
	        //String SERVER_CONFIG = "wsl$/$config$/$mdn$/$MdnRmiServer.conf";
	        url = ClassLoader.getSystemResource(instDir);
	        if (url == null){//throw new RuntimeException("Unable to locate MdnRmiServer.conf");
	        	//url = ClassLoader.getSystemResource(instDir + MdnStore.SERVER_CONFIG);
	        	//if (url == null){
	        		JOptionPane.showMessageDialog((Component)null, "Unable to locate MdnRmiServer.conf [" + instDir  + "]");
	        	//	return false;
	        	//}else{
	        	//	JOptionPane.showMessageDialog((Component)null, "Able to locate MdnRmiServer.conf [" + instDir + MdnStore.SERVER_CONFIG + "]");
	        	//}
	        }else{
	        	JOptionPane.showMessageDialog((Component)null, "Able to locate MdnRmiServer.conf [" + instDir + "]");
		        
	        }*/
	        
	        instDir = proxy.substitute("$USER_INSTALL_DIR$$/$tomcat-5.5.25$/$webapps$/$dashwell.war$/$WEB-INF$/$classes$/$wsl$/$config$/$mdn");
	        /*try {
				_store = getStore();
			} catch (Exception e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog((Component)null, "set store install path [" + instDir2 + "]" + e1.toString());
				
			}*/
	        
	            	        
		  return true;
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
	
	        /*gbc.gridwidth = GridBagConstraints.REMAINDER;
	        gbc.weightx = 0.0;
	        gbc.fill = GridBagConstraints.NONE;
	        gbc.insets.right = 10;
	        add(new JLabel(" "), gbc);       
	*/
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
	        add(new JLabel("Registered E-mail Address:"), gbc);
	
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
	        gbc.insets.top = 8;
	        gbc.insets.left = 30;
	        gbc.gridx = 0;
	        gbc.gridwidth = GridBagConstraints.NONE;
	        add(_chkReInstall, gbc);        
	        
	        // constraints for new JLabel("Reinstall:")
/*	        gbc.anchor = GridBagConstraints.NORTHEAST;
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
	        gbc.insets.top = 8;
	        add(_btnGetInstallations, gbc);

	        // constraints for _userId
	        gbc.weightx = 1.0;
	        gbc.anchor = GridBagConstraints.NORTHWEST;
	        gbc.insets.left = 0;
	        gbc.fill = GridBagConstraints.HORIZONTAL;
	        gbc.insets.left = 10;
	        add(_lstRefs, gbc);   	        
	        
/*	        gbc.gridwidth = GridBagConstraints.REMAINDER;
	        gbc.weightx = 0.0;
	        gbc.fill = GridBagConstraints.NONE;
	        gbc.insets.right = 10;
	        add(new JLabel(" "), gbc);        
	        
	        gbc.anchor = GridBagConstraints.NORTHEAST;
	        gbc.gridwidth = 1;
	        gbc.insets.left = 30;
	        gbc.gridx = GridBagConstraints.RELATIVE;
	        gbc.insets.right = 10;
	        gbc.insets.top = 10;
	        add(_findRefNumber, gbc);       
*/	
	    }


	public void actionPerformed(ActionEvent e) {
		
	}

	@Override
	public boolean okToContinue() {
	  // Set an IA variable based upon the text field's value, then continue.
	  //customCodePanelProxy.setVariable("$CHOSEN_URL$", tf.getText());
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
        	return false;
        }else{
        	if (!Validate.validateEmailAddress(userID)){
            	JOptionPane.showMessageDialog(this, "Email address is not valid.");
            	return false;        		
        	}
        }
        
        LicenceKey lKey = getLicenseKey();
        if (lKey == null){
        	return false;
        }
        
        ResultWrapper result = LicenseRemoteCallManager.validateUser(userID, lKey.toString(), installRef);
        ActivationKey aKey = (ActivationKey)result.getObject();
        Boolean publicGroup = (Boolean)result.getPublicGroupBoolean();
        String msg = result.getErrorMsg();
        String registeredEmailAddress = (String)result.getRegisteredEmailAddress();
        if (msg != null){
      	
        	if (msg.equalsIgnoreCase("The user does not exist")){
                if (!existingAccount){
                	/*String optionButtons[] =
                    {
                        "OK",
                        "Cancel"
                    }; 
            		int rv = JOptionPane.showOptionDialog(null, "You have not registered yet. Your email address will be registered automatically.",
                            "Create an account", JOptionPane.YES_NO_OPTION,
                            JOptionPane.PLAIN_MESSAGE, null,
                            optionButtons, optionButtons[0]);  
            		*/
            		//if (rv == 0){
            			ResultWrapper createResult = LicenseRemoteCallManager.createAnAccount(userID, lKey.toString(), this);
            			aKey = (ActivationKey)createResult.getObject();
            			publicGroup = (Boolean)createResult.getPublicGroupBoolean();
                        if (aKey != null){
                        	registeredEmailAddress = userID;
                        }            			
            			msg = createResult.getErrorMsg();
            			if (msg != null){
            				JOptionPane.showMessageDialog(this, msg);
            			}
            		//}                	
                }
                else{
                	JOptionPane.showMessageDialog(this, msg);
                }
        	}else{
        		JOptionPane.showMessageDialog(this, msg);
        	}           	
        }
        
        if (aKey != null){
            //JOptionPane.showMessageDialog((Component)null, "Successed!");
            try
            {
                _store = getStore();
                _store.setActivationKey(lKey, aKey);
                _store.setRegisteredEmailAddress(registeredEmailAddress);
                _store.setPublicGroup(publicGroup);
                _store.setAvailablePublicMessages(result.getAvailablePublicMessages());
                _store.setInstallationReferenceNumber(result.getInstallationReferenceNumber());
                _store.store();
            }
            catch (Exception e)
            {
                System.out.println("Error activating application.");
                System.out.println(e.toString());
                JOptionPane.showMessageDialog(this, "Error saving activation information. " + e.toString());
                /*StackTraceElement[] elements = e.getStackTrace();
                for (int j = 0; (j < elements.length && j < 2); j++){
					JOptionPane.showMessageDialog((Component)null, "Error saving activation information. [" + lKey + "] (" + elements[j] + ")");
				}*/
                return false;
            } 
            JOptionPane.showMessageDialog((Component)null, "Activation Successful!");
    		return true;
        }else{
        	return false;
        }
	}
	
	private LicenceKey getLicenseKey(){
		LicenceKey lKey = null;
		try {
			lKey = getLicenceKey(MdnLicenceManager.PROD_CODE_MDN_SERVER);
			if (lKey == null){
			    lKey = makeLicenceKey(MdnLicenceManager.PROD_CODE_MDN_SERVER); // new installation
				//JOptionPane.showMessageDialog((Component)null, "valid registration key1: [" + lKey + "]" );
			}else //if (!MdnLicenceManager.isValidLicenceKey(lKey))
			{
				LicenceKey lTmp = makeLicenceKey(lKey.getProductCode());
				if (!lKey.equals(lTmp)){
					JOptionPane.showMessageDialog((Component)null, "Fatal Error: Invalid registration key: [" + lKey + "][" + lTmp + "]");
				    return null;					
				}
			}
		} catch (HeadlessException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog((Component)null, "Fatal Error: Invalid registration key (HeadlessException): [" + lKey + "]");
			return null;
		} catch (Exception e) {
			//If this is store problem, then it is because new installation
			if (e instanceof InvalidStoreException){
			    try {
					lKey = makeLicenceKey(MdnLicenceManager.PROD_CODE_MDN_SERVER);
					if (lKey == null){
						JOptionPane.showMessageDialog((Component)null, "Fatal Error: can not create registration key: ");
					    return null;						
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog((Component)null, "Fatal Error after creating license key: Invalid registration key [" + lKey + "] (" + e1.toString() + ")");
					return null;					
				} // new installation
				//JOptionPane.showMessageDialog((Component)null, "valid registration key2: [" + lKey + "]" );				
			}else{
				e.printStackTrace();
				JOptionPane.showMessageDialog((Component)null, "Fatal Error: Invalid registration key [" + lKey + "] (" + e.toString() + ")");
				/*StackTraceElement[] elements = e.getStackTrace();
				for (int j = 0; (j < elements.length && j < 2); j++){
					JOptionPane.showMessageDialog((Component)null, "Fatal Error: Invalid registration key [" + lKey + "] (" + elements[j] + ")");
				}*/				
				return null;
			}
		}
		return lKey;
	}
	
	protected void goToSecureLoginLink() {
		String userID = null;
		if (_radFirst.isSelected()){
			userID = _userID.getText().trim();
		}else{
			userID = _existingUserID.getText().trim();
		}
        
        if (userID == null || userID.isEmpty()){
        	JOptionPane.showMessageDialog((Component)null, "Please enter the email address.");
        	return;
        }else{
        	if (!Validate.validateEmailAddress(userID)){
            	JOptionPane.showMessageDialog(this, "Email address is not valid.");
            	return;        		
        	}
        }
        
		LicenceKey lKey = getLicenseKey();
        if (lKey == null){
        	//Display the error message already inside the above method
        	//JOptionPane.showMessageDialog(this, "License Key is not valid.");
        	return;
        }
		
        ResultWrapper ret = LicenseRemoteCallManager.getSecureLoginLink(userID, lKey.toString());
        if (ret.getErrorMsg() != null){
        	JOptionPane.showMessageDialog((Component)null, ret.getErrorMsg());
        	return;
        }
        
        String loginLink = ret.getSecureLoginLink();
		try {
			if (loginLink != null && !loginLink.isEmpty())
				BrowserLauncher.openURL(loginLink);
			else
				JOptionPane.showMessageDialog((Component)null, "Sorry, Can not find link.");
		} catch (IOException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog((Component)null, "IOException Error: (" + e1.toString() + ")");			
		}		
	}
	
	//--------------------------------------------------------------------------
    /**
     *
     */
    public static LicenceKey getLicenceKey(int productID) throws Exception
    {
    	_store = getStore();
        if (_store != null){
        	//JOptionPane.showMessageDialog((Component)null, "existing license key: [" + _store.getLicenceKey(productID) + "]");
        	return _store.getLicenceKey(productID);         
        }           
        return null;
    }
	//--------------------------------------------------------------------------
    /**
     * Create a non-nodelocked LicenceKey
     */
    public static LicenceKey makeLicenceKey(int productID) throws Exception
    {
        // return new LicenceKey(productID);
    	_store = getStore();
        String path = _store.getPath();
        String sep = System.getProperty("file.separator", "/");
        //JOptionPane.showMessageDialog((Component)null, "Path for making license key: [" + path + "]");
		if (System.getProperty ("os.name").startsWith ("Windows"))
		{
	        //Make path same as later on
	        if (!path.startsWith(sep)){
	        	path = sep + path;
	        }
	        //JOptionPane.showMessageDialog((Component)null, "Path for making license key: [" + path + "]");
	        if (sep.equals("\\")){
	        	path = path.replace("\\", "/");
	        	//JOptionPane.showMessageDialog((Component)null, "Path for making license key: [" + path + "]");
	        }
		}
//      URL's could contain %20 as spaces, these have to be removed
        path = path.replaceAll("%20", " ");    
        //JOptionPane.showMessageDialog((Component)null, "Path for making license key: [" + path + "]");
        
        InetAddress addr = InetAddress.getLocalHost();

        String host = addr.getHostName();
        //JOptionPane.showMessageDialog((Component)null, "Path for making license key: [" + path + "]["+ addr + "]["+ host + "]");
        
        return MdnLicenceManager.makeLicenceKey(productID, path, host);
    }

    //--------------------------------------------------------------------------
	//--------------------------------------------------------------------------
    /**
     * @return the MdnStore singleton.
     */
    public static InstallerMdnStore getStore() throws Exception
    {
        if (_store == null)
        {
            _store = new InstallerMdnStore();
			_store.setPath(instDir);
			_store.setInstallPath(instDir);
            _store.load();
            
        }

        return _store;
    }

    //--------------------------------------------------------------------------
    
	@Override
	public boolean okToGoPrevious() {
		return super.okToGoPrevious();
	}

	@Override
	public void panelIsDisplayed() {
		super.panelIsDisplayed();
	}

}
