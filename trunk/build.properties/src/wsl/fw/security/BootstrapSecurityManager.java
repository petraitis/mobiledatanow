//==============================================================================
// BootstrapSecurityManager.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.security;

import wsl.fw.util.Config;
import wsl.fw.util.Util;
import wsl.fw.util.CKfw;
import wsl.fw.resource.ResId;

//------------------------------------------------------------------------------
/**
 * Config based security manager, used to check system to system access,
 * especially for booting clients and servers to grant them initial access
 * to remote resources.
 */
public class BootstrapSecurityManager
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/security/BootstrapSecurityManager.java $ ";

    // resources
    public static final ResId EXCEPTION_NULL_SECURITY_ID  = new ResId("BootstrapSecurityManager.exception.NullSecurityId");
    public static final ResId EXCEPTION_AUTHENTICATION_FAILURE  = new ResId("BootstrapSecurityManager.exception.AuthenticationFailure");
    public static final ResId EXCEPTION_PERMISSION  = new ResId("BootstrapSecurityManager.exception.Permission");

    // constants
    // constant for the standard password subkey
    private final static String PASSWORD = "password";

    // member variables
    // the Config object that holds the security data
    private Config _config;

    //--------------------------------------------------------------------------
    /**
     * Default constructor, uses the default (current singleton) Config.
     */
    public BootstrapSecurityManager()
    {
        _config = Config.getSingleton();
    }

    //--------------------------------------------------------------------------
    /**
     * Constructor that uses an explicit custom Config.
     */
    public BootstrapSecurityManager(Config config)
    {
        // check parameter
        Util.argCheckNull(config, "Config");

        _config = config;
    }

    //--------------------------------------------------------------------------
    /**
     * Get the value of the named property for the entity specified
     * by the SecurityId.
     * @param id, a SecurityId representing the entity.
     * @param propertyName, the name of the property to check. May be null, in
     *   which case the identity check is performed and null is returned.
     * @return a string containing the property value or null if the entity does
     *   no have the property.
     * @throws SecurityException if the entity's id is not valid.
     */
    public String get(SecurityId id, String propertyName)
      throws SecurityException
    {
        // fail if null id
        if (id == null)
            throw new wsl.fw.security.SecurityException(EXCEPTION_NULL_SECURITY_ID.getText());

        // validate the securityId
        String name = id.getName();
        String password = getBootsecProperty(name, PASSWORD);
        if (password == null)
        {
            // no entry found
            throw new wsl.fw.security.SecurityException(EXCEPTION_AUTHENTICATION_FAILURE.getText());
        }
        else
        {
            // password invalid
            SecurityId validId = new SecurityId(name, password);
            if (!id.equals(validId))
                throw new wsl.fw.security.SecurityException(EXCEPTION_AUTHENTICATION_FAILURE.getText());
        }

        // if a property is specified then load it
        String propertyValue = null;
        if (propertyName != null && propertyName.length() > 0)
            propertyValue = getBootsecProperty(name, propertyName);

        return propertyValue;
    }

    //--------------------------------------------------------------------------
    /**
     * Check if the named bootstrap security property contains a certain value.
     * If the check is false than a security exception is thrown.
     * @param id, a SecurityId representing the entity.
     * @param propertyName, the name of the property to check. May be null, in
     *   which case the identity check is performed and null is returned.
     * @param propertyValue, the value to check against.
     * @throws SecurityException if the entity's id is not valid or the value
     *   does not match.
     */
    public void check(SecurityId id, String propertyName, String propertyValue)
        throws SecurityException
    {
        // get the vaule of the property
        String value = get(id, propertyName);

        // fail if null or if the property does not match
        if (value == null || propertyValue == null
            || value.indexOf(propertyValue) == -1)
            throw new wsl.fw.security.SecurityException(id
                + " " + EXCEPTION_PERMISSION.getText() + " " + propertyName + "=" + propertyValue);
    }

    //--------------------------------------------------------------------------
    /**
     * Get the named bootstrap security property for the specified entity.
     * @param enitity, the entity (user) to get the proprtyu for.
     * @param propertyName, the name of the property to get.
     * @return the property, or null if the entity or property does not exist.
     * @throws IllegalArgumentException if either paramtere is null or empty.
     */
    private String getBootsecProperty(String entity, String propertyName)
        throws IllegalArgumentException
    {
        // check the params
        Util.argCheckNull(entity, "entity");
        Util.argCheckNull(propertyName, "propertyName");

        // build the composite key from prefix, entity and property
        String key = CKfw.BOOTSEC_PREFIX + CKfw.DOT + entity + CKfw.DOT
            + propertyName;

        // return the property
        return _config.getProperty(key);
    }
}

//==============================================================================
// end of file BootstrapSecurityManager.java
//==============================================================================
