//==============================================================================
// RmiServantBase.java
// $Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.remote;

import wsl.fw.util.Util;
import java.rmi.RemoteException;
import java.rmi.Remote;
import wsl.fw.resource.ResId;

//------------------------------------------------------------------------------
/**
 * Base class for RMIServant implementations, the servant knows the name
 * it is to be bound to (in the SecureRegistry) and has functions to get and
 * set that binding name. The default binding name is that fully qualified name
 * of the first (implementing multile remote interfaces in the one servant could
 * be problematic) remote interface implemented by the class. Servants which do
 * not want this default naming should call setBindName()in their constructor
 * and provide a static in the implementation or interface so clients can access
 * the name without having to create an instance.
 */
public class RmiServantBase implements RmiServant
{
    // resources
    public static final ResId EXCEPTON_REMOTE_INTERFACE  = new ResId("RmiServantBase.exception.RemoteIinterface");

    // member variables
    protected String _bindName = null;

    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public RmiServantBase()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Get the registry binding name for this servant, if one has not been
     * explicitly set then the class name is used as a default.
     * @return the name to bind to.
     * @throws
     */
    public String getBindName()
    {
        if (_bindName == null)
        {
            // no special binding name, use the name of the rmi interface
            // enumerate interfaces, if interface is remote then return its name
            Class interfaces[] = getClass().getInterfaces();
            for (int i = 0; i < interfaces.length; i++)
                if (Remote.class.isAssignableFrom(interfaces[i]))
                    return interfaces[i].getName();

            // error, no suitable interface found
            throw new IllegalArgumentException(EXCEPTON_REMOTE_INTERFACE.getText());
        }
        else
            return _bindName;
    }

    //--------------------------------------------------------------------------
    /**
     * Set the bind name for this servant.
     * @param bindName, may not be null or empty.
     */
    protected void setBindName(String bindName)
    {
        // check param and set it
        Util.argCheckEmpty(bindName);
        _bindName = bindName;
    }

    //--------------------------------------------------------------------------
    /**
     * Do nothing implementation of RmiServant.ping().
     */
    public void ping() throws RemoteException
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Called by server to tell us this servant is about to be unexported.
     * Override to perform any required cleanup.
     */
    public void terminate()
    {
        // do nothing
    }

}

//==============================================================================
// end of file RmiServantBase.java
//==============================================================================
