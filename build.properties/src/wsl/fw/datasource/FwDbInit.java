//==============================================================================
// FwDbInit.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.datasource;

import wsl.fw.resource.ResourceManager;
import wsl.fw.security.FwFeatures;
import wsl.fw.security.Feature;
import wsl.fw.security.Group;
import wsl.fw.security.Privilege;
import wsl.fw.security.GroupMembership;
import wsl.fw.security.User;
import wsl.fw.security.SecurityId;
import wsl.fw.presentation.PresentationItem;
import wsl.fw.message.Message;
import wsl.fw.util.Log;
import wsl.fw.util.CKfw;
import wsl.fw.util.Config;
import wsl.fw.util.Util;
import java.util.Vector;

//------------------------------------------------------------------------------
/**
 * Class to create the database tables and seed the db with values.
 */
public class FwDbInit
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/datasource/FwDbInit.java $ ";

    // data members to hold the intermediate data objects that will later be
    // used by getDependentSeedDataObjects()
    private User  _adminUser  = null;
    private Group _adminGroup = null;

    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    protected FwDbInit()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Create the tables.
     */
    private void create()
    {
        // create the tables
        System.out.println("\nCreating tables:");
        int nCreated = 0;
        int nFailed  = 0;
        Vector v = getCreateEntities();

        // get a framework data source
        DataSource ds = DataManager.getDataSource(Feature.ENT_FEATURE);

        // iterate over the create entities creating tables for them
        for (int i = 0; i < v.size(); i++)
        {
            Entity ent = (Entity) v.get(i);
            try
            {
                ds.createEntityTable(ent, false);
                nCreated++;
            }
            catch (DataSourceException e)
            {
                System.out.println("Failed to create table for entity [" + ent.getName() + "], "
                    + e.toString());
                nFailed++;
            }
        }

        // display results
        System.out.println("Total Tables: " + v.size());
        System.out.println("Created: " + nCreated);
        if (nFailed > 0)
            System.out.println(FwFeatures.LOG_FAILED.getText() + " " + nFailed);
    }

    //--------------------------------------------------------------------------
    /**
     * Seed the database with its initial values.
     * Note that the current mechanism is limited in that it cannot handle
     * relational data objects that that need a generated key from another
     * data object. Could change this so that getSeedDataObjects does the actual
     * save and could therefore reference the ids of DataObjectsit has
     * previously saved.
     */
    private void seed()
    {
        int    i;
        int    nSaved;
        int    nExistant;
        int    nFailed;
        Vector v;

        // seed the features
        System.out.println("\nSeeding Features:");
        getFeatures().saveFeatureSet();

        // seed from raw sql
        System.out.println("\nSeeding from SQL:");
        nSaved  = 0;
        nFailed = 0;
        v = getSeedSqlCommands();

        // get a framework data source
        DataSource ds = DataManager.getDataSource(Feature.ENT_FEATURE);

        // iterate over the seed sql commands saving them
        for (i = 0; i < v.size(); i++)
        {
            String sql = (String) v.get(i);
            try
            {
                ds.rawExecuteUpdate(sql);
                nSaved++;
            }
            catch (DataSourceException e)
            {
                System.out.println("Failed to execute ["
                    + sql + "], "+ e.toString());
                nFailed++;
            }
        }

        // display results
        System.out.println("Total SQL seed commands " + v.size());
        System.out.println("Executed: " + nSaved);
        if (nFailed > 0)
            System.out.println(FwFeatures.LOG_FAILED.getText() + " " + nFailed);

        // seed the data objects
        System.out.println("\nSeeding DataObjects:");
        nSaved    = 0;
        nExistant = 0;
        nFailed   = 0;
        v = getSeedDataObjects();

        // iterate over data objects
        for (i = 0; i < v.size(); i++)
        {
            DataObject dobj = (DataObject) v.get(i);
            try
            {
                // save and inc count
                dobj.save();
                nSaved++;
            }
            catch (KeyConstraintException e)
            {
                System.out.println(dobj.toString() + " " + FwFeatures.LOG_ALREADY_IN_DB.getText());
                nExistant++;

                // load the dobj to ensure the key is valid for use by
                // getDependentSeedDataObjects
                try
                {
                    dobj.load();
                }
                catch (DataSourceException e2)
                {
                    System.out.println("FwDbInit.seed, error loading DataObject after "
                        + "a KeyConstraintException: " + e2.toString());
                }

            }
            catch (DataSourceException e)
            {
                System.out.println(FwFeatures.LOG_FAILED_TO_SAVE.getText() + " "
                    + dobj.toString() + ": "+ e.toString());
                nFailed++;
            }
        }

        // display results
        System.out.println("Total DataObjects: " + v.size());
        System.out.println(FwFeatures.LOG_SAVED.getText() + ": " + nSaved);
        if (nExistant > 0)
            System.out.println(FwFeatures.LOG_ALREADY_IN_DB.getText() + ": " + nExistant);
        if (nFailed > 0)
            System.out.println(FwFeatures.LOG_FAILED.getText() + " " + nFailed);

        // seed the data objects
        System.out.println("\nSeeding dependent DataObjects:");
        nSaved    = 0;
        nExistant = 0;
        nFailed   = 0;
        v = getDependentSeedDataObjects();

        // iterate over data objects
        for (i = 0; i < v.size(); i++)
        {
            DataObject dobj = (DataObject) v.get(i);
            try
            {
                // save and inc count
                dobj.save();
                nSaved++;
            }
            catch (KeyConstraintException e)
            {
                System.out.println(dobj.toString() + " " + FwFeatures.LOG_ALREADY_IN_DB.getText());
                nExistant++;
            }
            catch (DataSourceException e)
            {
                System.out.println(FwFeatures.LOG_FAILED_TO_SAVE.getText() + " "
                    + dobj.toString() + ": "+ e.toString());
                nFailed++;
            }
        }

        // display results
        System.out.println("Total dependent DataObjects: " + v.size());
        System.out.println(FwFeatures.LOG_SAVED.getText() + ": " + nSaved);
        if (nExistant > 0)
            System.out.println(FwFeatures.LOG_ALREADY_IN_DB.getText() + ": " + nExistant);
        if (nFailed > 0)
            System.out.println(FwFeatures.LOG_FAILED.getText() + " " + nFailed);
    }

    //--------------------------------------------------------------------------
    /**
     * Execute the command as specified by the command line.
     */
    protected void runCommand(String args[])
    {
        String cmd = Util.getArg(args, "-cmd", "");

        // check the command, create seed or if invalid display help
        if (cmd.equals("create"))
            create();
        else if (cmd.equals("seed"))
            seed();
        else
            displayHelp();

        // finished saving data, close the data manager
        try
        {
            DataManager.closeAll();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    //--------------------------------------------------------------------------
    /**
     * Display the help.
     */
    protected void displayHelp()
    {
        System.out.println();
        System.out.println("Syntax is: " + getClass().getName() + " -cmd "
            + "{ create | seed }");
        System.out.println("Used to configure and seed the database.");
        System.out.println("create: create the tables, errors wil be generated if");
        System.out.println("tables with the same name already exist.");
        System.out.println("seed: seed the database with initial values.");
        System.out.println(Config.getHelp());
        System.out.println();
    }

    //--------------------------------------------------------------------------
    /**
     * Main entrypoint. Init resource and data managers and config then run
     * the create or seed command.
     */
    public static void main(String args[])
    {
        // set up Resource and Data managers
        ResourceManager.set(new ResourceManager());
        Config.getSingleton().addContext(args, CKfw.RMISERVER_CONTEXT);
        // does not need to call LocalServerFactory.setArgs as this should be
        // run from the server and does not notify or register RmiServers
        DataManager.setDataManager(new DataManager());

        // execute the comand in args
        FwDbInit dbi = new FwDbInit();
        dbi.runCommand(args);
    }

    //--------------------------------------------------------------------------
    // Beginning of overridable functions that define the create and seed
    // operations for DbInit.
    //--------------------------------------------------------------------------
    /**
     * Get the entities that will be used to create the tables.
     * Subclasses should call the base class then add their own entries to the
     * Vector.
     * @return a Vector of Entities that will bre created.
     */
    protected Vector getCreateEntities()
    {
        Vector v = new Vector();

        // add the key table, which is not defined as a DataObject, so manually
        // make an entity for it
        Entity nextKeyEntity = new EntityImpl("TBL_NEXTKEY", null);
        nextKeyEntity.addField(new FieldImpl("FLD_MAPTABLE", Field.FT_STRING,
            Field.FF_SYSTEM_KEY | Field.FF_UNIQUE_KEY, 50));
        nextKeyEntity.addField(new FieldImpl("FLD_NEXTKEY", Field.FT_INTEGER));
        v.add(nextKeyEntity);

        // add the rest as defined by their DataObjects
        v.add(Feature.createEntity());
        v.add(Group.createEntity());
        v.add(GroupMembership.createEntity());
        v.add(Privilege.createEntity());
        v.add(User.createEntity());
//        v.add(PresentationItem.createEntity());
        v.add(Message.createEntity());

        return v;
    }

    //--------------------------------------------------------------------------
    /**
     * @return the FwFeatures subclass that defines the available set of
     *   features for this application.
     */
    protected FwFeatures getFeatures()
    {
        // return the framework feature set
        return new FwFeatures();
    }

    //--------------------------------------------------------------------------
    /**
     * Get the SQL to perform any non-DataObject seeding of the database.
     * Subclasses should call the base class then add their own entries to the
     * Vector.
     * @return a Vector of Strings containing seed SQL.
     */
    protected Vector getSeedSqlCommands()
    {
        Vector v = new Vector();

        // add SQL strings to the Vector here
        // v.add("some SQL");

        return v;
    }

    //--------------------------------------------------------------------------
    /**
     * Get the DataObjects to be seeded into the database.
     * Subclasses should call the base class then add their own entries to the
     * Vector.
     * The DataObjects returned by this function will be saved into the DB by
     * the seed() function, if a duplicate DataObject exists then the DataObject
     * will beloaded so its key will be valid for use by
     * getDependentSeedDataObjects.
     * @return a Vector of DataObjects to be seeded.
     */
    protected Vector getSeedDataObjects()
    {
        Vector v = new Vector();

        // note that we save objects to members for later use by
        // getDependentSeedDataObjects which assumes that getSeedDataObjects
        // has done the init correctly

        // create admin group and user
        _adminUser = new User(new SecurityId("admin", "admin"), "Administrator");
        v.add(_adminUser);
        _adminGroup = new Group(1, "Administrator", "Administrator group");
        v.add(_adminGroup);

        return v;
    }

    //--------------------------------------------------------------------------
    /**
     * Get the data objects to be seeded into the database that require the
     * generated key of a DataObject saved or loaded from getSeedDataObjects.
     * Subclasses should call the base class then add their own entries to the
     * Vector.
     * The DataObjects returned by this function will be saved into the DB by
     * the seed() function.
     * @return a Vector of DataObjects to be seeded.
     */
    protected Vector getDependentSeedDataObjects()
    {
        Vector v = new Vector();

        // create admin privileges and group membership objects
        v.add(new Privilege(_adminGroup, FwFeatures.USERGROUP_ADMIN));
        v.add(new GroupMembership(_adminGroup, _adminUser));

        return v;
    }
}

//==============================================================================
// end of file FwDbInit.java
//==============================================================================
