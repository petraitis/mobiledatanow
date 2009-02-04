//==============================================================================
// MdnAdminHelpManager.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.mdn.admin;

import wsl.fw.help.HelpManager;
import javax.help.HelpSetException;

//------------------------------------------------------------------------------
/**
 * Help manager for MDN administrator.
 */
public class MdnAdminHelpManager extends HelpManager
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:35:35 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/admin/MdnAdminHelpManager.java $ ";

    // help set base path/name
    public final static String MDN_HELPSET = "help/wsl/mdn/mdn";

    //--------------------------------------------------------------------------
    /**
     * Constructor.
     * @throws HelpSetException if the helpset cannot be loaded.
     */
    public MdnAdminHelpManager() throws HelpSetException
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Load the help sets.
     * @throws HelpSetException if the helpset cannot be loaded.
     */
    protected void addHelpSets() throws HelpSetException
    {
        // do not call base class to include its helpsets (framework)
        // as we can customise this in the mdn helpset itself
        // super.addHelpSets();

        // add the MDN admin helpset
        addHelpSet(MDN_HELPSET);
    }
}

//==============================================================================
// end of file MdnAdminHelpManager.java
//==============================================================================
