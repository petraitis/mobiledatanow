//==============================================================================
// MdnResourceManager.java
// Copyright (c) 2001 WAP Solutions Ltd.
//==============================================================================

package wsl.mdn.common;

import wsl.fw.resource.ResourceManager;

//------------------------------------------------------------------------------
/**
 * Resource Manager for the MDN application.
 */
public class MdnResourceManager extends ResourceManager
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:35:35 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/common/MdnResourceManager.java $ ";

    //--------------------------------------------------------------------------
    /**
     * Creates entry for Admin in hashtable of registed resource bundles.
     */
    public MdnResourceManager()
    {
        iAddBundle("wsl.mdn.resource.strings.admin");
        iAddBundle("wsl.mdn.resource.strings.wml");
        iAddBundle("wsl.mdn.resource.strings.html");
    }
}

//==============================================================================
// end of file MdnResourceManager.java
//==============================================================================
