//==============================================================================
// FwFeatures.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.security;

import java.util.Vector;
import wsl.fw.datasource.DataManager;
import wsl.fw.datasource.KeyConstraintException;
import wsl.fw.datasource.DataSourceException;
import wsl.fw.util.CKfw;
import wsl.fw.util.Config;
import wsl.fw.util.Log;
import wsl.fw.resource.ResId;
import wsl.fw.resource.ResourceManager;

//------------------------------------------------------------------------------
/**
 * Class to hold constants defining the Features used by security for wsl.fw
 * and sub-packages.
 * Also contains a main() which can be executed to ensure the correct features
 * are written in the database.
 * Code which needs to check an individual feature should use the constants
 * defined here and in any domain specific subclasses.
 * Code which needs to browse or query the set of features should query the
 * database.
 * Subclasses should:
 * Define constants for any new Features. Note constants shouold be sure to use
 * the no-init constructor (doInit == false).
 * Override getFeatureSet to add the new features.
 * Implement a main that instantiates the subclass and calls saveFeatureSet.
 */
public class FwFeatures
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/security/FwFeatures.java $ ";

    // resources
    public static final ResId RES_USERGROUP_ADMIN  = new ResId("FwFeatures.UserGroupAdmin");
    public static final ResId LOG_SAVED  = new ResId("FwFeatures.log.Saved");
    public static final ResId LOG_ALREADY_IN_DB  = new ResId("FwFeatures.log.AlreadyInDB");
    public static final ResId LOG_FAILED_TO_SAVE  = new ResId("FwFeatures.log.FailedToSave");
    public static final ResId LOG_TOTAL_FEATURES  = new ResId("FwFeatures.log.TotalFeatures");
    public static final ResId LOG_FAILED  = new ResId("FwFeatures.log.Failed");

    private static final String DUMMY = "__not_available__";

    // the available features
    /** Administer users, groups, privileges and group membership. */
    public final static Feature USERGROUP_ADMIN = new Feature("UserGroupAdmin",
        DUMMY, false);

    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public FwFeatures()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Overridable function to get the set of features for use by saveFeatureSet.
     * @return a vector of all the features that are to be saved.
     */
    protected Vector getFeatureSet()
    {
        // create the vector
        Vector v = new Vector();

        // add the features
        //v.add(USERGROUP_ADMIN);
        v.add(new Feature(USERGROUP_ADMIN.getName(), RES_USERGROUP_ADMIN.getText()));
        // return the vector
        return v;
    }

    //--------------------------------------------------------------------------
    /**
     * Save the features returned by getFeatureSet to the DB.
     */
    public final void saveFeatureSet()
    {
        // get the set of features to save
        Vector v = getFeatureSet();

        // iterate saving the features
        int     nSaved    = 0;
        int     nExistant = 0;
        int     nFailed   = 0;
        Feature fConst    = null;

        for (int i = 0; i < v.size(); i++)
            try
            {
                // get the feature
                fConst = (Feature) v.get(i);

                // make a non const inited copy
                Feature f = new Feature(fConst.getName(), fConst.getDescription());

                // save it
                f.saveInternal();

                Log.log(LOG_SAVED.getText() + " " + fConst.getName());
                nSaved++;
            }
            catch (KeyConstraintException e)
            {
                Log.log(fConst.getName() + " " + LOG_ALREADY_IN_DB.getText());
                nExistant++;
            }
            catch (DataSourceException e)
            {
                Log.log(LOG_FAILED_TO_SAVE.getText() + " " + fConst.getName()
                    + ": "+ e.toString());
                nFailed++;
            }


        // display results
        Log.log(LOG_TOTAL_FEATURES.getText() + " " + v.size());
        Log.log(LOG_SAVED.getText() + ": " + nSaved);
        if (nExistant > 0)
            Log.log(LOG_ALREADY_IN_DB.getText() + ": " + nExistant);
        if (nFailed > 0)
            Log.log(LOG_FAILED.getText() + " " + nFailed);
    }
}

//==============================================================================
// end of file FwFeatures.java
//==============================================================================
