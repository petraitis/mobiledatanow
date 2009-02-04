//==============================================================================
// MdnFeatures.java
// Copyright (c) 2001 WAP Solutions Ltd.
//==============================================================================

package wsl.mdn.common;

import wsl.fw.security.Feature;
import wsl.fw.security.FwFeatures;
import java.util.Vector;
import wsl.fw.resource.ResId;
import wsl.fw.resource.ResourceManager;

//------------------------------------------------------------------------------
/**
 * Class to hold constants defining the Features used by security for
 * wsl.mdn and sub-packages.
 */
public class MdnFeatures extends FwFeatures
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:35:35 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/common/MdnFeatures.java $ ";

    // resources

    // the available features

    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public MdnFeatures()
    {
    }

   //--------------------------------------------------------------------------
    /**
     * Override to get the set of features for use by saveFeatureSet.
     * @return a vector of all the features that are to be saved.
     */
    protected Vector getFeatureSet()
    {
        // get the superclass' vector
        Vector v = super.getFeatureSet();

        // add the features

        // return the vector
        return v;
    }
}

//==============================================================================
// end of file MdnFeatures.java
//==============================================================================
