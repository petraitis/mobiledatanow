/*	$Header: /wapsolutions/cvsroot/mdn/mdn/src/wsl/mdn/common/MdnDbInit.java,v 1.1.1.1 2002/06/11 23:35:35 jonc Exp $
 *
 *	Database Initialization for MDN
 */
package wsl.mdn.common;

import wsl.fw.datasource.DataManager;
import wsl.fw.datasource.FwDbInit;
import wsl.fw.security.FwFeatures;
import wsl.fw.resource.ResourceManager;
import wsl.fw.msgserver.MessageServer;
import wsl.fw.msgserver.MessageServerProfile;
import wsl.fw.util.CKfw;
import wsl.fw.util.Config;
import wsl.mdn.common.MdnFeatures;
import wsl.mdn.common.MdnResourceManager;
import wsl.mdn.dataview.*;
import wsl.mdn.guiconfig.MenuAction;
import wsl.mdn.guiconfig.LoginSettings;
import wsl.mdn.mdnim.IMConnection;
import wsl.mdn.mdnim.IMContact;
import wsl.mdn.mdnim.IMMessage;
import wsl.mdn.mdnmsgsvr.CustomField;
import wsl.mdn.mdnmsgsvr.MdnSmpp;
import wsl.mdn.mdnmsgsvr.MessagingSettingDetails;
import wsl.mdn.mdnmsgsvr.TemporaryBlockContacInfo;
import wsl.mdn.mdnmsgsvr.BlockContacts;
import wsl.mdn.mdnmsgsvr.MdnEmailSetting;
import wsl.mdn.mdnmsgsvr.MdnMessageSeparator;
import wsl.mdn.mdnmsgsvr.MdnSmsSetting;
import wsl.mdn.mdnmsgsvr.MessageLog;
import wsl.mdn.mdnmsgsvr.UserReply;
import wsl.mdn.mdnmsgsvr.UserCustomField;

import java.util.Vector;

//------------------------------------------------------------------------------
/**
 * Class to create the database tables and seed the db with values.
 */
public class MdnDbInit extends FwDbInit
{
    // version tag
    private final static String _ident = "$Header: /wapsolutions/cvsroot/mdn/mdn/src/wsl/mdn/common/MdnDbInit.java,v 1.1.1.1 2002/06/11 23:35:35 jonc Exp $";

    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    protected MdnDbInit()
    {
    }


    //--------------------------------------------------------------------------
    /**
     * Get the SQL to create the tables.
     * Subclasses should call the base class then add their own entries to the
     * Vector.
     * @return a Vector of Strings containing SQL.
     */
    protected Vector getCreateEntities()
    {
        // call base class
        Vector v = super.getCreateEntities();

        // add the entities to create
        v.add(DataSourceDobj.createEntity());
        v.add(EntityDobj.createEntity());
        v.add(FieldDobj.createEntity());
        v.add(JoinDobj.createEntity());
        v.add(DataView.createEntity());
        v.add(DataViewField.createEntity());
        v.add(FieldExclusion.createEntity());
        v.add(GroupDataView.createEntity());
        v.add(GroupTablePermission.createEntity());        
        v.add(MenuAction.createEntity());
        v.add(LoginSettings.createEntity());

        v.add(DataTransfer.createEntity());
        v.add(QueryDobj.createEntity());
        v.add(QueryCriteriaDobj.createEntity());
        v.add(Scheduling.createEntity());
        v.add(TransferEntity.createEntity());
        v.add(JdbcDriver.createEntity());

		v.add (MessageServer.createEntity ());
		v.add (MessageServerProfile.createEntity ());
		v.add (IMConnection.createEntity ());
		v.add (IMMessage.createEntity());		
		v.add (IMContact.createEntity());		
		v.add (MdnEmailSetting.createEntity());
		//v.add (AddressBook.createEntity());		
		v.add (MdnSmsSetting.createEntity());
		v.add (MdnMessageSeparator.createEntity());
		v.add (UserReply.createEntity());
		v.add (ProjectDobj.createEntity());
		v.add (WebServiceOperationDobj.createEntity());
		v.add (CustomField.createEntity());
		v.add (MessagingSettingDetails.createEntity());
		v.add (TemporaryBlockContacInfo.createEntity());
		v.add (BlockContacts.createEntity());
		v.add (UserCustomField.createEntity());
		v.add (MessageLog.createEntity());
		v.add (MdnSmpp.createEntity());
		return v;
    }

    //--------------------------------------------------------------------------
    /**
     * @return the FwFeatures subclass that defines the available set of
     *   features for this application.
     */
    protected FwFeatures getFeatures()
    {
        // return the MDN  server features
        return new MdnFeatures();
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
        Vector v = super.getSeedSqlCommands();

        // add SQL strings to the Vector here
        // v.add("some SQL");

        return v;
    }

    //--------------------------------------------------------------------------
    /**
     * Get the data objects to be seeded into the database.
     * Subclasses should call the base class then add their own entries to the
     * Vector.
     * @return a Vector of DataObjects to be seeded.
     */
    protected Vector getSeedDataObjects()
    {
        Vector v = super.getSeedDataObjects();

		/*
		 *	Add JDBCDataStore entries.
		 *		- Sun's JDBC/ODBC bridge
		 *		- Oracle's driver
		 *		- Postgresql
		 */
		v.add (new JdbcDriver ("JDBC/ODBC Bridge",
							   "sun.jdbc.odbc.JdbcOdbcDriver",
							   "For Microsoft ODBC data sources", 
							   null));
		v.add (new JdbcDriver ("Oracle Driver",
							   "oracle.jdbc.driver.OracleDriver",
							   "Works with Oracle 8+", 
							   null));
		v.add (new JdbcDriver ("Postgresql Driver",
							   "org.postgresql.Driver",
							   "Postgresql 7.x series", 
							   null));
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
        Vector v = super.getDependentSeedDataObjects();

        // fixme
        // dependent seed??

        return v;
    }

    //--------------------------------------------------------------------------
    /**
     * Main entrypoint. Init resource and data managers and config then run
     * the create or seed command.
     */
    public static void main(String args[])
    {
        // set up Resource, config and Data managers
        ResourceManager.set(new MdnResourceManager());

        Config.setSingleton(MdnAdminConst.MDN_CONFIG_FILE, true);
        Config.getSingleton().addContext(args, CKfw.RMISERVER_CONTEXT);
        // does not need to call LocalServerFactory.setArgs as this should be
        // run from the server and does not notify or register RmiServers
        DataManager.setDataManager(new MdnDataManager());

        // execute the comand in args
        MdnDbInit dbi = new MdnDbInit();
        dbi.runCommand(args);
    }
}