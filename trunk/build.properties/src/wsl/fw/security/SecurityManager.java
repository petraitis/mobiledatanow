//==============================================================================
// SecurityManager.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.security;

// imports
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.awt.Window;
import wsl.fw.security.gui.ChangePasswordPanel;
import wsl.fw.datasource.DataSourceException;
import wsl.fw.exception.MdnException;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.resource.ResId;

//------------------------------------------------------------------------------
/**
 * Singleton security management class. Contains static methods for easy use of
 * security features.
 */
public class SecurityManager
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/security/SecurityManager.java $ ";

    // resources
    public static final ResId ERR_MUST_ENTER_PASSWORD  = new ResId("SecurityManager.error.MustEnterPassword");
    public static final ResId ERR_PASSWORD_NOT_SAME  = new ResId("SecurityManager.error.PasswordNotSame");
    public static final ResId EXCEPTION_NOT_LOGGED_IN  = new ResId("SecurityManager.exception.NotLoggedIn");
    public static final ResId ERR_FAILED_TO_SAVE  = new ResId("SecurityManager.error.FailedToSave");

    //--------------------------------------------------------------------------
    /**
     * Singleton SecurityManager object
     */
    private static SecurityManager _sm = new SecurityManager();

    //--------------------------------------------------------------------------
    /**
     * The single logged in User for this SecurityManager
     */
    private static User _user = null;

    //--------------------------------------------------------------------------
    /**
     * Blank ctor
     */
    public SecurityManager()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * @return SecurityManager the singleton SecurityManager
     */
    public static SecurityManager getSecurityManager()
    {
        return _sm;
    }

    //--------------------------------------------------------------------------
    /**
     * Verifies a user name and password, and sets the logged in User if valid
     * @param userName
     * @param password
     * @return User the logged in User object, or null if not valid
     * @throws MdnException 
     */
    public static UserWrapper login(String userName, String password) throws MdnException        
    {
        // validate
        UserWrapper userWrapper = null;
		try {
			userWrapper = validateUser(userName, password);
	        if(userWrapper.getLoginUser() != null)
	            setLoggedInUser(userWrapper.getLoginUser());

	        // return the user
	        return userWrapper;
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new MdnException("SecurityException in SecurityManager", e);
		} catch (RemoteException e) {
			e.printStackTrace();
			throw new MdnException("RemoteException in SecurityManager", e);
		} catch (NotBoundException e) {
			e.printStackTrace();
			throw new MdnException("NotBoundException in SecurityManager", e);
		}
    }

    public static UserWrapper getLoginUserByName(String userName) throws MdnException        
    {
        // validate
        UserWrapper userWrapper = null;
		try {
			userWrapper = getUserByName(userName);
	        
	        // return the user
	        return userWrapper;
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new MdnException("SecurityException in SecurityManager", e);
		} catch (RemoteException e) {
			e.printStackTrace();
			throw new MdnException("RemoteException in SecurityManager", e);
		} catch (NotBoundException e) {
			e.printStackTrace();
			throw new MdnException("NotBoundException in SecurityManager", e);
		}
    }    
    //--------------------------------------------------------------------------
    /**
     * Verifies a user name and password
     * @param userName
     * @param password
     * @return User the validated user object, or null if not valid
     */
    public static UserWrapper validateUser(String userName, String password)
        throws SecurityException, RemoteException, NotBoundException
    {
        // validate
        UserWrapper userWrapper = User.login(new SecurityId(userName, password));

        // return the user
        return userWrapper;
    }

    public static UserWrapper getUserByName(String userName)
	    throws SecurityException, RemoteException, NotBoundException
	{
	    // validate
	    UserWrapper userWrapper = User.getLoginUserByName(userName);
	
	    // return the user
	    return userWrapper;
	}    
    
    //--------------------------------------------------------------------------
    /**
     * Sets the logged in user for the SecurityManager
     * @param user the user to set
     */
    public static void setLoggedInUser(User user)
    {
        _user = user;
    }

    //--------------------------------------------------------------------------
    /**
     * @return the currently logged in User for this SecurityManager
     */
    public static User getLoggedInUser()
    {
        return _user;
    }

    //--------------------------------------------------------------------------
    /**
     * Encrypt a password
     */
    public static String encryptPassword(String password)
    {
        SecurityId secId = new SecurityId("x", password);
        return secId.getPassword();
    }

    //--------------------------------------------------------------------------
    /**
     * verifies new and confirm passwords
     * @param newPw the new password (unencrypted)
     * @param confirmPw the confirm password (unencrypted)
     * @param String error string, zero length if no error
     */
    public static String validateNewPassword(String newPw, String confirmPw)
    {
        // validate params
        Util.argCheckNull(newPw);
        Util.argCheckNull(confirmPw);

        // must have a password
        String error = "";
        if(newPw.length() == 0)
            error = ERR_MUST_ENTER_PASSWORD.getText();

        // check that confirm password == password
        else if(!newPw.equals(confirmPw))
            error = ERR_PASSWORD_NOT_SAME.getText();

        return error;
    }

    //--------------------------------------------------------------------------
    /**
     * Check if the currently logged in user has privilege to use the specified
     * feature.
     * @param feature, the feature to check access for.
     * @return true if the user is allowed access.
     */
    public static boolean hasPrivilege(Feature feature)
    {
        if (_user == null)
            return false;
        else
            return _user.hasPrivilege(feature);
    }

    //--------------------------------------------------------------------------
    /**
     * Check if the currently logged in user has privilege to use the specified
     * feature.
     * @param feature, the feature to check access for.
     * @throws SecurityException if the user does not have privilege.
     */
    public static void checkPrivilege(Feature feature)
        throws SecurityException
    {
        if (_user == null)
            throw new wsl.fw.security.SecurityException(EXCEPTION_NOT_LOGGED_IN.getText());
        else
            _user.checkPrivilege(feature);
    }

    //--------------------------------------------------------------------------
    /**
     * Change the password for the logged in user by displaying a chnage
     * password dialog.
     * @param parent, the parent window for the modal change password dialog.
     * @return true if thepassword was changed, false if no logged in user
     *   or the change dialog was cancelled.
     */
    public static boolean changeLoggedInUserPassword(Window parent)
    {
        boolean rv = false;

        // get the user
        User user = SecurityManager.getLoggedInUser();

        if (user != null)
        {
            // get the old password
            String oldPw = user.getPassword();

            // open the change password panel requiring validation of previous
            // password as this is not an admin screen
            ChangePasswordPanel p = ChangePasswordPanel.openChangePasswordPanel(
                parent, oldPw);

            // get the new password and set it
            String newPw = p.getNewPassword();
            if(newPw != null)
            {
                user.setPassword(newPw);
                try
                {
                    user.save();
                    rv = true;
                }
                catch (DataSourceException e)
                {
                    Log.error(ERR_FAILED_TO_SAVE.getText()
                        + " " + e.toString());
                }
            }
        }

        return rv;
    }
}

//==============================================================================
// end of file SecurityManager.java
//==============================================================================
