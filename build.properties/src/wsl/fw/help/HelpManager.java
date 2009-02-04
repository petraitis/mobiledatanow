//==============================================================================
// HelpManager.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.help;

import wsl.fw.util.Util;
import java.net.URL;
import javax.help.HelpSet;
import javax.help.HelpBroker;
import javax.help.HelpSetException;
import wsl.fw.resource.ResId;

//------------------------------------------------------------------------------
/**
 * Singleton manager for help.
 * May be subclassed to overload addHelpSets which allows the subclass to
 * specify which helpsets are loaded.
 *
 * Usage:
 * If desired subclass HelpManager to add project sepcific HelpSets.
 * In the main or constructor of the application call HelpManager.set() to set
 * the HelpManager singleton.
 * In various application classes that require help define HelpId constants and
 * use them (generally in response to a menu item or button press) to display
 * help topics.
 */
public class HelpManager
{
    // version tag
    private final static String _ident = "$Date: 2004/01/06 01:47:06 $  $Revision: 1.2 $ "
        + "$Archive: /Framework/Source/wsl/fw/help/HelpManager.java $ ";

    // resources
    public static final ResId ERR_NOT_SET  = new ResId("HelpManager.error.NotSet");
    public static final ResId ERR_HELP_SET  = new ResId("HelpManager.error.HelpSet");

    // help set base path/name for framework help
    public final static String FW_HELPSET = "help/wsl/fw/Framework";

    // singleton
    private static HelpManager s_helpManagerSingleton = null;

    // member data
    private HelpSet    _helpSet    = null;
    private HelpBroker _helpBroker = null;

    //--------------------------------------------------------------------------
    /**
     * Set the HelpManager singleton.
     * @param hm, the HelpManager singleton
     */
    public static void set(HelpManager hm)
    {
        s_helpManagerSingleton = hm;
    }

    //--------------------------------------------------------------------------
    /**
     * @return the HelpManager singleton.
     */
    public static HelpManager get()
    {
        return s_helpManagerSingleton;
    }

    //--------------------------------------------------------------------------
    /**
     * Static accessor to get the help set from the singleton.
     * @return the HelpSet.
     */
    public static HelpSet getHelpSet()
    {
        assert s_helpManagerSingleton != null: ERR_NOT_SET.getText();
        return s_helpManagerSingleton._helpSet;
    }

    //--------------------------------------------------------------------------
    /**
     * Static accessor to get the help broker from the singleton.
     * @return the HelpBroker.
     */
    public static HelpBroker getHelpBroker()
    {
        assert s_helpManagerSingleton != null: ERR_NOT_SET.getText();
        return s_helpManagerSingleton._helpBroker;
    }

    //--------------------------------------------------------------------------
    /**
     * Constructor.
     * Loads all the help sets specified by addHelpSets().
     * @throws HelpSetException if a helpset cannot be loaded.
     */
    public HelpManager() throws HelpSetException
    {
        // add the help sets
        addHelpSets();

        // create the help broker
        _helpBroker = _helpSet.createHelpBroker();

        // init the helpbroker UI atthe time the help set is inited.
        // this allows us to choose to (ini theapp init order) init the
        // HelpManager before GuiManager so that it gets the default
        // (usually java) look and feel. The womdows look and feel does not
        // look allthat good with JHelp

        // use whatever UI the app sets
        // _helpBroker.initPresentation();
    }

    //--------------------------------------------------------------------------
    /**
     * Add all the desired help sets.
     * Override in subclasses to define which help sets to load. Genenerally
     * should call superclass to merge in the framework HelpSets. Alternatively
     * the subclass could add a specific subset of framework topics directly
     * by defining them in its helpset file.
     * Subclasses call addHelpSet() to add the individual HelpSets.
     * @throws HelpSetException if the helpset cannot be loaded.
     */
    protected void addHelpSets() throws HelpSetException
    {
        // add the framework helpset
        addHelpSet(FW_HELPSET);
    }

    //--------------------------------------------------------------------------
    /**
     * Add (merge) a HelpSet.
     * @param helpSetName, the base path/name of the helpset.
     * @throws HelpSetException if the HelpSet cannot be loaded.
     */
    protected void addHelpSet(String helpSetName) throws HelpSetException
    {
        // find the helpset file
        URL hsURL = HelpSet.findHelpSet(null, helpSetName);
        assert hsURL != null: ERR_HELP_SET.getText() + " " + helpSetName;

        // create/load  the helpset (uses the default class loader)
        HelpSet hs    = new HelpSet(null, hsURL);

        // add it, if we do not already have a help set then set this as our
        // help set, else merge
        if (_helpSet == null)
            _helpSet = hs;
        else
            _helpSet.add(hs);
    }
}

//==============================================================================
// end of file HelpManager.java
//==============================================================================
