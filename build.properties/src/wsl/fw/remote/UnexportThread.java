//==============================================================================
// UnexportThread.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.remote;

import java.rmi.Remote;
import java.rmi.server.UnicastRemoteObject;

//------------------------------------------------------------------------------
/**
 * Thread that will keep trying a soft unexport until it succeeds.
 */
public class UnexportThread extends Thread
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/remote/UnexportThread.java $ ";

    // the remote object to unexport
    Remote _remoteObj;

    //--------------------------------------------------------------------------
    /**
     * Constructor.
     * @param r, the remote object to unexport
     */
    public UnexportThread(Remote r)
    {
        _remoteObj = r;
    }

    //--------------------------------------------------------------------------
    /**
     * Thread run method. Will exit when it has unexported the remote object.
     */
    public void run()
    {
        try
        {
            // try to unexport, stop looping on success
            while (_remoteObj != null
                && !UnicastRemoteObject.unexportObject(_remoteObj, false))
            {
                // failed, sleep for 1 second then try again
                sleep(1000);
            }
        }
        catch (Exception e)
        {
            // catch errors and interrupts
        }
    }
}

//==============================================================================
// end of file UnexportThread.java
//==============================================================================
