//==============================================================================
// ServantSet.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.remote;

import java.util.List;
import java.util.LinkedList;
import wsl.fw.util.Log;
import wsl.fw.resource.ResId;

//------------------------------------------------------------------------------
/**
 * Package-level class to contain the multi-entry list of bindings for
 * SecureRegistryServer.  Acts as a set, only one of each object permitted.
 * The ServantSet's get... methods check any servants before returning and
 * removes invalid entries.
 */
public class ServantSet
{
    // version tag
    private final static String _ident = "$Archive: /Framework/Source/wsl/fw/remote/ServantSet.java $  $Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $";

    // resources
    public static final ResId EXCEPTON_PING  = new ResId("ServantSet.exception.Ping");
    public static final ResId EXCEPTON_PING2  = new ResId("ServantSet.exception.Ping2");

    // member variables
    private List   _servants = null;
    private String _bindName = null;

    //--------------------------------------------------------------------------
    /**
     * Constructor.
     * @param bindName, store the name this set is bound to.
     */
    public ServantSet(String bindName)
    {
        _bindName = bindName;

        // create the list that holds servants
        _servants = new LinkedList();
    }

    //--------------------------------------------------------------------------
    /**
     * Add a servant to the set.
     * @param servant, the servant object to add.
     * @return true if added successfully, false if failed or a duplicate
     *   entry already existed.
     */
    public boolean add(RmiServant servant)
    {
        // check if it is already in the set
        if (_servants.contains(servant))
            return false;

        return _servants.add(servant);
    }

    //--------------------------------------------------------------------------
    /**
     * Remove the servant from the set.
     * @param servant, the servant to remove.
     * @return true if the servant was in the set.
     */
    public boolean remove(RmiServant servant)
    {
        return _servants.remove(servant);
    }

    //--------------------------------------------------------------------------
    /**
     * Get the next available servant, then move the servant to the end
     * of the list.
     * @return the next servant, or null if there are none.
     */
    public RmiServant getNext()
    {
        RmiServant servant = null;

        // repeat until we find a servant or the list is empty
        while (_servants.size() > 0 && servant == null)
        {
            // get the servant and remove it
            servant = (RmiServant) _servants.get(0);
            _servants.remove(0);
            // check the servant for validity
            try
            {   // if ping succeeds then add the servant back to the end
                // of the list (so the other servants will get used)
                servant.ping();
                add(servant);
                // success, servant != null so the loop will terminate
            }
            catch (Exception e)
            {
                // bad servant, set ref to null so the loop continues. the
                // bad servant is not added to the list
                servant = null;
                Log.debug(EXCEPTON_PING.getText() + " "
                    + _bindName);
            }
        }

        return servant;
    }

    //--------------------------------------------------------------------------
    /**
     * Get all servants in the list.
     * @return an array of all the servants, may be empty.
     */
    public RmiServant[] getAll()
    {
        // iterate over all servants removing any that are invalid
        RmiServant allServants[] = (RmiServant[]) _servants.toArray(new RmiServant[0]);
        for (int i = 0; i < allServants.length; i++)
        {
            try
            {
                // test servant
                allServants[i].ping();
            }
            catch (Exception e)
            {
                // bad servant, remove it
                _servants.remove(allServants[i]);
                Log.debug(EXCEPTON_PING2.getText() + " "
                    + _bindName);
            }
        }

        // return the set of servants
        return (RmiServant[]) _servants.toArray(new RmiServant[0]);
    }

    //--------------------------------------------------------------------------
    /**
     * @return the size of the servant set.
     */
    public int size()
    {
        return _servants.size();
    }
}

//==============================================================================
// end of file ServantSet.java
//==============================================================================
