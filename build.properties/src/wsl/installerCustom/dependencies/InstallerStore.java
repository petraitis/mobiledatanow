package wsl.installerCustom.dependencies;

import java.awt.Component;

import javax.swing.JOptionPane;

/**
 * Title:        Mobile Data Now
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      WAP Solutions Ltd
 * @author Paul Lupton
 * @version 1.0
 */


public class InstallerStore extends wsl.licence.Store
{
	private String installPath = null;
    public InstallerStore() throws Exception
    {
    }



    /**
     * Returns the filename and path for the store
     */
    protected  String getFileNamePath()
    {
        String sep = System.getProperty("file.separator", "/");
        //String path = System.getProperty("user.home", ".");

        String filename = installPath + sep + "sec.dat";
        
        //JOptionPane.showMessageDialog((Component)null, "this file name is being used: [" + filename + "]");
        
        return filename;
    }



	public String getInstallPath() {
		return installPath;
	}



	public void setInstallPath(String installPath) {
		this.installPath = installPath;
	}

}
