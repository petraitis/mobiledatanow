package wsl.mdn.licence;

import java.awt.Component;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import wsl.licence.ActivationKey;
import wsl.licence.LicenceKey;
import wsl.mdn.dataview.ResultWrapper;

public class LicenseRemoteCallManager {

	public LicenseRemoteCallManager() {
		// TODO Auto-generated constructor stub
	}
    public static ResultWrapper validateUser(String userID, String licenceKey, String installRef)
    {
        // make sure all compulsory fields are valid
        String msg = "";
        //create random license key for testing
        //int PROD_CODE = 121001;
        //licenceKey = new LicenceKey(PROD_CODE).toString();

		if (userID == null || userID.isEmpty()){
			return new ResultWrapper(null, "Please enter the email address.");
		}        
        
        String activationKey = null;
        String strPublicGroup = null;
        String strPublicMessages = null;
        String strInstallationName = null;
        
        try {        
        	String urlStr = null;
        	String data = null;
        	if (installRef != null && !installRef.isEmpty()){
	        	//This is reintall
	        	urlStr = "https://api.mobiledatanow.com/reInstall?id=" + userID + "&license_key=" + licenceKey + "&reference=" + installRef;
	        	data = "id=" + userID + "&license_key=" + licenceKey + "&reference=" + installRef;
	        }else{       
				urlStr = "https://api.mobiledatanow.com/getActivationKey?id=" + userID + "&license_key=" + licenceKey;
	        	//String urlStr = "http://api.mdn.encode.net.nz/tests/getActivationKey?id=" + userID + "&license_key=" + licenceKey;
				
				data = "id=" + userID + "&license_key=" + licenceKey;
	        }
        	
			URL u = new URL(urlStr);
        	System.out.println(u.toString());
			
			HttpURLConnection uc = (HttpURLConnection) u.openConnection();
			//uc.setRequestMethod("POST");
			//uc.setRequestProperty("Content-Type", "text/xml");
			uc.setDoOutput(true);
			//uc.setDoInput(true);
			uc.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
			//uc.setUseCaches (false);
			
			PrintWriter pw = new PrintWriter(uc.getOutputStream());
            pw.write(data);
			pw.flush();
            pw.close();		
			
//            BufferedReader in = new BufferedReader(
//            		new InputStreamReader(uc.getInputStream()));
//            		String res = in.readLine();
//            		in.close();
//            System.out.println(res);
            
        	DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document        doc        = docBuilder.parse(uc.getInputStream());
			Element element = doc.getDocumentElement();
			String status = element.getAttribute("status");
			if (status.equalsIgnoreCase("ok")){
				NodeList list = element.getChildNodes();
								 
				for (int i = 0; i < list.getLength(); i++){
					Node node = list.item(i);
					String nodeName = node.getNodeName();
					String nodeValue = node.getTextContent();
					System.out.println( nodeName + " " + nodeValue);
					if (nodeName.equalsIgnoreCase("activation_key")){
						activationKey = nodeValue;						
					}else if (nodeName.equalsIgnoreCase("public_group")){
						strPublicGroup = nodeValue;
					}else if (nodeName.equalsIgnoreCase("available_public_messages")){
						strPublicMessages = nodeValue;
					}else if (nodeName.equalsIgnoreCase("id")){
						userID = nodeValue;
					}else if (nodeName.equalsIgnoreCase("name")){
						strInstallationName = nodeValue;
					}
				}
				System.out.println("activationKey: " + activationKey);
			}
			else {//if (status.equalsIgnoreCase("failed")){
				msg = element.getFirstChild().getTextContent();
				System.out.println("failed: " + msg);		         
		         
		        return new ResultWrapper(null, msg);
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResultWrapper(null, e.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResultWrapper(null, e.toString());
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResultWrapper(null, e.toString());
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResultWrapper(null, e.toString());
		}

        if (activationKey == null || activationKey.length() < ActivationKey.ENCODED_LENGTH)
        {
            msg = "Incomplete activation key.  Cannot continue.";
            //ret = false;
            return new ResultWrapper(null, msg);
        }
        else
        {
            try
            {
                ActivationKey aKey = new ActivationKey(activationKey, licenceKey);
                if (!aKey.verifyChecksum())
                {
                    msg = "Invalid activation key checksum.";
                    //ret = false;
                    return new ResultWrapper(null, msg);
                }
                Boolean publicGroup = null;
                if (strPublicGroup.equalsIgnoreCase("true")){
                	publicGroup = new Boolean("true");
                }else{
                	publicGroup = new Boolean("false");
                }
                int availablePublicMessages = 0;
                if (!strPublicMessages.isEmpty()){
                	try {
						availablePublicMessages = Integer.parseInt(strPublicMessages);
					} catch (NumberFormatException e) {
						availablePublicMessages = 0;
					}
                }
                int installationRef = 0;
                if (installRef != null && !installRef.isEmpty()){
                	try {
                		installationRef = Integer.parseInt(installRef);
					} catch (NumberFormatException e) {
						installationRef = 0;
					}
                }
                return new ResultWrapper(aKey, userID, publicGroup, availablePublicMessages, installationRef, null);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            	msg = "Corrupt activation key.";// + e.toString();
                //ret = false;
                return new ResultWrapper(null, msg);
            }

        }
    }
    /**
     * If user does not exist, create one user account right now
     * @param userID
     * @param licenceKey
     * @return
     */
    public static ResultWrapper createAnAccount(String userID, String licenceKey, Component panel) {
        String msg = "";
        
        try {
			if (userID == null || userID.isEmpty()){
				return new ResultWrapper(null, "Please enter the email address.");
			}
        	String urlStr = "https://api.mobiledatanow.com/createId?id=" + userID;
			
			String data = "id=" + userID;
			
			URL u = new URL(urlStr);
        	System.out.println(u.toString());
			
			HttpURLConnection uc = (HttpURLConnection) u.openConnection();
			uc.setDoOutput(true);
			//uc.setDoInput(true);
			uc.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
			
			PrintWriter pw = new PrintWriter(uc.getOutputStream());
            pw.write(data);
			pw.flush();
            pw.close();		
			
        	DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document        doc        = docBuilder.parse(uc.getInputStream());
			Element element = doc.getDocumentElement();
			String status = element.getAttribute("status");
			if (status.equalsIgnoreCase("ok")){
				if (panel != null)
					JOptionPane.showMessageDialog(panel, "Your email address has been registered and an account created. Please check your email inbox for further details.");
				return LicenseRemoteCallManager.validateUser(userID, licenceKey, null);
			}
			else {//if (status.equalsIgnoreCase("failed")){
				msg = element.getFirstChild().getTextContent();
				System.out.println("failed: " + msg);		         
		         
		        return new ResultWrapper(null, msg);
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResultWrapper(null, e.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResultWrapper(null, e.toString());
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResultWrapper(null, e.toString());
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResultWrapper(null, e.toString());
		}
		
	}    
    
    public static ResultWrapper getSecureLoginLink(String userID, String lKey){
		ResultWrapper ret = new ResultWrapper();
    	
		
		
    	String link = "https://api.mobiledatanow.com/secureLoginLink";
		String urlStr = link + "?id=" + userID + "&license_key=" + lKey;
		
		String data = "id=" + userID + "&license_key=" + lKey;
		String loginLink = null;
		
		try {
			URL u = new URL(urlStr);
			System.out.println(u.toString());
			
			HttpURLConnection uc = (HttpURLConnection) u.openConnection();
			uc.setDoOutput(true);
			//uc.setDoInput(true);
			uc.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
			
			PrintWriter pw = new PrintWriter(uc.getOutputStream());
			pw.write(data);
			pw.flush();
			pw.close();		
			
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document        doc        = docBuilder.parse(uc.getInputStream());
			Element element = doc.getDocumentElement();
			String status = element.getAttribute("status");
			System.out.println("get Secure login link status: " + status);
			String msg = "";
			if (status.equalsIgnoreCase("ok")){
				NodeList list = element.getChildNodes();
				 
				for (int i = 0; i < list.getLength(); i++){
					Node node = list.item(i);
					String nodeName = node.getNodeName();
					String nodeValue = node.getTextContent();
					System.out.println( nodeName + " " + nodeValue);
					if (nodeName.equalsIgnoreCase("login_link")){
						loginLink = nodeValue;	
						ret.setSecureLoginLink(loginLink);
					}
				}
				System.out.println("Secure login link: [" + loginLink+"]");
			}
			else {//if (status.equalsIgnoreCase("failed")){
				msg = element.getFirstChild().getTextContent();
				System.out.println("failed: " + msg);		         
				//JOptionPane.showMessageDialog((Component)null, msg); 
				ret.setErrorMsg(msg);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			//JOptionPane.showMessageDialog((Component)null, "MalformedURLException Error: (" + e.toString() + ")");
			ret.setErrorMsg("MalformedURLException: (" + e.toString() + ")");
		} catch (DOMException e) {
			e.printStackTrace();
			//JOptionPane.showMessageDialog((Component)null, "DOMException Error: (" + e.toString() + ")");
			ret.setErrorMsg("DOMException: (" + e.toString() + ")");
		} catch (IOException e) {
			e.printStackTrace();
			//JOptionPane.showMessageDialog((Component)null, "IOException Error: (" + e.toString() + ")");
			ret.setErrorMsg("IOException: (" + e.toString() + ")");
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			//JOptionPane.showMessageDialog((Component)null, "ParserConfigurationException Error: (" + e.toString() + ")");
			ret.setErrorMsg("ParserConfigurationException: (" + e.toString() + ")");
		} catch (SAXException e) {
			e.printStackTrace();
			//JOptionPane.showMessageDialog((Component)null, "SAXException Error: (" + e.toString() + ")");
			ret.setErrorMsg("SAXException: (" + e.toString() + ")");
		}
		return ret;
    }
    public static ResultWrapper getInstallations(String userID){
		ResultWrapper ret = new ResultWrapper();
    	
		
		
    	String link = "https://api.mobiledatanow.com/getInstallations";
		String urlStr = link + "?id=" + userID ;
		
		String data = "id=" + userID ;
		
		try {
			URL u = new URL(urlStr);
			System.out.println(u.toString());
			
			HttpURLConnection uc = (HttpURLConnection) u.openConnection();
			uc.setDoOutput(true);
			//uc.setDoInput(true);
			uc.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
			
			PrintWriter pw = new PrintWriter(uc.getOutputStream());
			pw.write(data);
			pw.flush();
			pw.close();		
			
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document        doc        = docBuilder.parse(uc.getInputStream());
			Element element = doc.getDocumentElement();
			String status = element.getAttribute("status");
			System.out.println("get Installations status: " + status);
			String msg = "";
			HashMap<String, String> installations = new HashMap<String, String>();
			if (status.equalsIgnoreCase("ok")){
				NodeList list = element.getChildNodes();

				for (int i = 0; i < list.getLength(); i++){
					Node node = list.item(i);
					String nodeName = node.getNodeName();
					String nodeValue = node.getTextContent();
					System.out.println( "nodeName: [" + nodeName + "], nodeValue:[" + nodeValue + "]");
					if (nodeName.equalsIgnoreCase("installation")){
						NodeList detailList = node.getChildNodes();
						String installationName = null;
						String installationNumber = null;
						for (int j = 0; j < detailList.getLength(); j++){
							Node childNode = detailList.item(j);
							String childNodeName = childNode.getNodeName();
							String childNodeValue = childNode.getTextContent();
							System.out.println( "childNodeName: [" + childNodeName + "], childNodeValue:[" + childNodeValue + "]");
							if (childNodeName.equalsIgnoreCase("name")){
								installationName = childNodeValue;
							}else if (childNodeName.equalsIgnoreCase("reference_number")){
								installationNumber = childNodeValue;
							}
						}
						installations.put(installationNumber, installationName);
					}
				}
				System.out.println("installations: [" + installations+"]");
				ret.setInstallations(installations);
			}
			else {//if (status.equalsIgnoreCase("failed")){
				msg = element.getFirstChild().getTextContent();
				System.out.println("failed: " + msg);		         
				//JOptionPane.showMessageDialog((Component)null, msg); 
				ret.setErrorMsg(msg);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			//JOptionPane.showMessageDialog((Component)null, "MalformedURLException Error: (" + e.toString() + ")");
			ret.setErrorMsg("MalformedURLException: (" + e.toString() + ")");
		} catch (DOMException e) {
			e.printStackTrace();
			//JOptionPane.showMessageDialog((Component)null, "DOMException Error: (" + e.toString() + ")");
			ret.setErrorMsg("DOMException: (" + e.toString() + ")");
		} catch (IOException e) {
			e.printStackTrace();
			//JOptionPane.showMessageDialog((Component)null, "IOException Error: (" + e.toString() + ")");
			ret.setErrorMsg("IOException: (" + e.toString() + ")");
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			//JOptionPane.showMessageDialog((Component)null, "ParserConfigurationException Error: (" + e.toString() + ")");
			ret.setErrorMsg("ParserConfigurationException: (" + e.toString() + ")");
		} catch (SAXException e) {
			e.printStackTrace();
			//JOptionPane.showMessageDialog((Component)null, "SAXException Error: (" + e.toString() + ")");
			ret.setErrorMsg("SAXException: (" + e.toString() + ")");
		}
		return ret;
    }    
}
