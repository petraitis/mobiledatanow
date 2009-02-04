//==============================================================================
// DataManager.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.datasource;

// imports
import java.util.Hashtable;
import java.util.HashSet;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Iterator;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import wsl.fw.util.Config;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.util.CKfw;
import wsl.fw.security.Feature;
import wsl.fw.security.Group;
import wsl.fw.security.Privilege;
import wsl.fw.security.GroupMembership;
import wsl.fw.security.User;
import wsl.fw.security.SecurityId;
import wsl.fw.msgserver.MessageServer;
import wsl.fw.msgserver.MsExchangeMsgServer;
import wsl.fw.msgserver.MessageServerParam;
import wsl.fw.msgserver.MessageServerProfile;
import wsl.fw.msgserver.MessageDobj;
import wsl.fw.remote.LocalServerFactory;
import wsl.fw.presentation.PresentationItem;
import wsl.fw.message.Message;
import wsl.fw.notification.NRNotificationListener;
import wsl.fw.notification.NotificationListenerServer;
import wsl.fw.notification.Notifier;
import wsl.fw.resource.ResId;

//------------------------------------------------------------------------------
/**
 * Singleton class providing static data access methods.
 * Contains flyweights of Entities and DataSources.
 * Subclasses should override and extend iCreateEntity.
 * Subclasses may override and extend iCreateDataSource if they wish
 * to support other data sources.
 * This class can be configured (with CKfw.REMOTE_DATAMANAGER) to use a
 * remote data manager and remote data sources. In this case everything
 * the data manager does is local EXCEPT that any data sources created
 * are remote. This means than any listeners only apply to the local VM.
 * The subclass of the DataManager in both client and server processes
 * must be the same.
 *
 * By default DataChangeNotifications are local to the DataManageSingleton
 * (ie within the java VM only). To send and/or recieve data change
 * notifications remotely (from other data managers in other server/VMs) then
 * you must register the entities for which remote notifications are required
 * using addListenerEntity/addSenderEntity and then start the remote
 * notification listener server using startRemoteNotifications. Remote
 * notifications are received by the onNotification function and distributed to
 * all data change listerners in the normal fashion.
 * Before app exit you should call stopRemoteNotifications to free the
 * remote listener. You may not add more listener or sender entities after the
 * listener has been started.
 * Remote notifications sent by this DataManager are ignored to avoid double
 * ups on notifications.
 */
public class DataManager implements NRNotificationListener
{
    // version tag
    private final static String _ident = "$Date: 2004/01/06 01:41:03 $  $Revision: 1.2 $ "
        + "$Archive: /Framework/Source/wsl/fw/datasource/DataManager.java $ ";

    // resources
    public static final ResId LOG_USING  = new ResId("DataManager.log.Using");
    public static final ResId ERR_SEND_NOTIFICATION  = new ResId("DataManager.error.SendNotification");
    public static final ResId ERR_NOT_SET  = new ResId("DataManager.error.NotSet");
    public static final ResId ERR_FATAL  = new ResId("DataManager.error.Fatal");
    public static final ResId ERR_RETURNED_NULL  = new ResId("DataManager.error.ReturnedNull");
    public static final ResId ERR_CREATE_REMOTE_DATA_SOURCE  = new ResId("DataManager.error.CreateRemoteDataSource");
    public static final ResId ERR_FORWARD  = new ResId("DataManager.error.Forward");

    /** constant defining a notification type for sending or receiving data
     * change notifications from the data framework. The subtype specifies the
     * entity. Used wsl.fw.notification.Notifier */
    public final static String NT_DATA_CHANGE = "wsl.fw.notification.type._data_change_";

    // the data manager singleton.
    private static DataManager s_dm = null;

    // attributes for data managememt and local notifications
    private Hashtable         _datasources = new Hashtable();
    private Vector            _dataListeners = new Vector();
    private RemoteDataManager _remoteDataManager = null;
    private String            _rdmBindName = null;
    private DataObjectVersionCache _dovCache = null;

    // attributes for remote notification
    private Notifier                   _notifier = null;
    private SecurityId                 _systemId = null;
    private NotificationListenerServer _notificationListenerServer = null;
    private HashSet                    _sendEntities = new HashSet();
    private HashSet                    _listenEntities = new HashSet();

    // the JDBC connection pool used by this DataManager
    ConnectionPoolManager              _connectionPoolMgr = null;

    //--------------------------------------------------------------------------
    /**
     * Default constructor
     */
    public DataManager()
    {
        // get the config data to see if we are to use a RemoteDataManager.
        String rdmBindName = Config.getProp(CKfw.REMOTE_DATAMANAGER);
        if (rdmBindName != null && rdmBindName.length() > 0)
            {
                _rdmBindName = rdmBindName;
                Log.log("DataManager using RemoteDataManager " + rdmBindName);
                Log.log(LOG_USING.getText() + " " + rdmBindName);
            }
    }

    //--------------------------------------------------------------------------
    /**
     * Add a DataChangeListener.
     * @param data DataListenerData object containing the DataChangeListener and
     *   other subscriber information.
     * @see DataListenerData constructors for subscriber details.
     * @param userTag this object will be passed back to the subscriber in all
     *   notifications
     */
    public static void addDataChangeListener(DataListenerData data)
    {
        // add the node
        getDataManager()._dataListeners.add(data);
    }

    //--------------------------------------------------------------------------
    /**
     * Remove a DataChangeListener based on DataListenerData. The listener
     * mapped to the param DataListenerData will be removed.
     * This allows one to add the same DataChangeListener mapped to multiple
     * DataListenerData, and then selectively remove a
     * single listener. The match is done on the contents of the data as opposed
     * to its reference.
     * @param data the DataListenerData that will identify the
     *   DataChangeListener to remove.
     * @return boolean true if a matching listener was found and removed.
     */
    public static boolean removeDataChangeListener(DataListenerData data)
    {
        return getDataManager()._dataListeners.remove(data);
    }

    //--------------------------------------------------------------------------
    /**
     * Remove all notification binding that reply to the specified listener.
     * @param listener, the listener to remove all bindings for.
     */
    public static void removeAllDataChangeListeners(DataChangeListener listener)
    {
        // iterate the DataListenerData vector (starting at end to avoid delete
        // problems)
        DataListenerData data;
        for(int i = (getDataManager()._dataListeners.size() - 1); i >= 0 ; i--)
        {
            // get the dld, if the listeners match remove it
            data = (DataListenerData) getDataManager()._dataListeners.elementAt(i);
            if (data != null && data._listener == listener)
                getDataManager()._dataListeners.remove(i);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Notify data listeners of a DataObject event.
     * @param DataChangeNotification contains the data regarding data change
     */
    public static void notifyListeners(DataChangeNotification notification)
    {
        // get singleton and delegate to non-static version
        getDataManager().internalNotifyListeners(notification);
    }

    //--------------------------------------------------------------------------
    /**
     * Notify data listeners of a DataObject event.
     * @param DataChangeNotification contains the data regarding data change
     */
    protected void internalNotifyListeners(DataChangeNotification notification)
    {
        boolean sendLocally = true;
        String  entityName  = notification.getEntityName();

        // if the notification is from local then see if we should forward it by
        // checking if this entity (or NST_ALL) is in the sender set
        if (!notification.isFromRemoteNotifier()
            && isListenerRunning()
            && (_sendEntities.contains(entityName)
            || _sendEntities.contains(Notifier.NST_ALL)))
        {
            // a sendable internal notification, send to remote notifier
            sendRemoteNotification(notification);
        }

        // send to local listeners
        // iterate the DataListener vector
        DataListenerData data;
        for(int i = 0; i < _dataListeners.size(); i++)
        {
            // get the data
            data = (DataListenerData) _dataListeners.elementAt(i);
            if(data != null)
            {
                // notify the listener (in own thread)
                data.notifyListener(notification);
            }
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Get the system id for authenticating with the secure registry, used when
     * sending remote notifications.
     * @return a system id loaded from Config data.
     */
    protected synchronized SecurityId getSystemId()
    {
        if (_systemId == null)
            _systemId = SecurityId.getSystemId();
        return _systemId;
    }

    //--------------------------------------------------------------------------
    /**
     * Get the Notifier to send remote notifications to.
     * @param forceNew, if true a New notifier reference is always obtained.
     * @return the Notifier.
     */
    protected synchronized Notifier getNotifier(boolean forceNew)
        throws RemoteException, NotBoundException,
        wsl.fw.security.SecurityException
    {
        if (forceNew || _notifier == null)
            _notifier = (Notifier) LocalServerFactory.get(getSystemId(),
                Notifier.class.getName());

        return _notifier;
    }

    //--------------------------------------------------------------------------
    /**
     * Send a data change notification to the remote Notifier.
     * @param notification, the notification to send.
     */
    protected void sendRemoteNotification(DataChangeNotification notification)
    {

        // inner thread class to send the notification without blocking
        class NotThread extends Thread
        {
            private DataChangeNotification _notification = null;

            public NotThread(DataChangeNotification notification)
            {
                _notification = notification;
            }

            // thread run method
            public void run()
            {
                // build a remote data change notification data object which
                // holds the data change notification and an id unique to
                // this sender
                RemoteDCNData rdcnData = new RemoteDCNData(
                    _notificationListenerServer.getListenerId(), _notification);

                try
                {
                    // send the remote data change notification to the notifier
                    getNotifier(false).sendNotification(NT_DATA_CHANGE,
                        _notification.getEntityName(), rdcnData);
                }
                catch (Exception e)
                {
                    // failed, try again forcing a new Notifier reference
                    try
                    {
                        getNotifier(true).sendNotification(NT_DATA_CHANGE,
                            _notification.getEntityName(), rdcnData);
                    }
                    catch (Exception e2)
                    {
                        // failed a second time, log the error
                        //Log.error("NotificationSender.sendNotification: failed to send notification", e2);
                        Log.error(ERR_SEND_NOTIFICATION.getText(), e2);
                    }
                }
            }
        }

        // use NotThread inner class to send the remote notification
        NotThread nt = new NotThread(notification);
        nt.start();
    }

    //--------------------------------------------------------------------------
    /**
     * Sets a subclass DataManager as the singleton
     * @param dm The subclass DataManager object
     */
    public static void setDataManager(DataManager dm)
    {
        // set attrib
        s_dm = dm;
    }

    //--------------------------------------------------------------------------
    /**
     * Returns the DataManager singleton
     * Creates a DataManager if no subclass singleton has been set
     * @return The DataManager singleton
     */
    public static DataManager getDataManager()
    {
        // return the singleton
        return s_dm;
    }

    //--------------------------------------------------------------------------
    /**
     * Close all the DataSources held by the DataManager
     */
    public static synchronized void closeAll() throws DataSourceException
    {
        // iterate the datasource table
        DataSource ds;
        Enumeration enums = getDataManager()._datasources.elements();
        while(enums != null && enums.hasMoreElements())
        {
            // get the ds and close it
            ds = (DataSource)enums.nextElement();
            if(ds != null)
                ds.close();
        }

        // close the connection pools
        if (getDataManager()._connectionPoolMgr != null)
            getDataManager()._connectionPoolMgr.terminate();
    }

    //--------------------------------------------------------------------------
    /**
     * Return the system DataSource
     * @return DataSource the system datasource
     */
    public static DataSource getSystemDS()
    {
        // delegate with system DataSOurce name
        return getDataSource(DataSource.SYS_DS_NAME);
    }

    //--------------------------------------------------------------------------
    /**
     * Flyweight factory for DataSource objects.
     * DataSources are mapped to entity names
     * @param entityName entity name
     * @return DataSource
     */
    public final static synchronized DataSource getDataSource(String entityName)
    {
        // must have a param
        Util.argCheckNull(entityName);

        // Must have a data manager
        assert getDataManager() != null: ERR_NOT_SET.getText();

        // get the ds name for this entity
        String dsName = getDataManager().getEntityDSName(entityName);//DataSource.SYS_DS_NAME "System JDBC Data Source"

        // find it in the datasources table
        DataSource ds = (DataSource) getDataManager()._datasources.get(dsName);
        if(ds == null)
        {
            // create the data source
            ds = getDataManager().createDataSource(dsName);
            if(ds != null)
            {
                // set its name
                ds.setName(dsName);

                // put into tables
                getDataManager()._datasources.put(dsName, ds);
            }
        }

        // return the ds
        return ds;
    }

    //--------------------------------------------------------------------------
    /**
     * Get a datasource using params. Stores in a map.
     * @param param, the DataSourceParam that defines the desired datasource.
     *   If null returns the system DS.
     */
    public final static synchronized DataSource getDataSource(DataSourceParam param)
    {
        // Must have a data manager
        assert getDataManager() != null: ERR_NOT_SET.getText();

        DataSource ds = null;

        // if null get system ds
        if (param == null)
            ds = getSystemDS();
        else
        {
            // else find it in the datasources table
            ds = (DataSource) getDataManager()._datasources.get(param);
            if (ds == null)
            {
                // create the data source
                ds = getDataManager().createDataSource(param);
                if(ds != null)
                {
                    // put into tables
                    getDataManager()._datasources.put(param, ds);
                }
            }
        }

        // return the ds
        return ds;

    }

    //--------------------------------------------------------------------------
    /**
     * Get the remote data manager, create if necessary. Will attempt to
     * reconnect on subsequent calls if there is a RMI failure.
     * @returns the RemoteDataManager.
     */
    public synchronized RemoteDataManager getRemoteDataManager()
    {
        if (_remoteDataManager == null)
            try
            {
                // have a name for the remote data manager, get it
                _remoteDataManager = (RemoteDataManager)
                    LocalServerFactory.get(getSystemId(), _rdmBindName);
            }
            catch (Exception e)
            {
                //Log.fatal("Cound not get RemoteDataManager", e);
                Log.fatal(ERR_FATAL.getText(), e);
            }

        return _remoteDataManager;
    }

    //--------------------------------------------------------------------------
    /**
     * Package scope method to go to a RemoteDataManager and create a new
     * RemoteDataSource. Used to assist with reconnection logic.
     * @param dsName the name of the RemoteDataSource to create.
     * @return the RemoteDataSource.
     */
    RemoteDataSource createRemoteDataSource(String dsName)
    {
            RemoteDataSource rds = null;

            // get the remote data manager
            RemoteDataManager rdm = getRemoteDataManager();

            // if successful create and return the RemoteDataSource
            if (rdm != null)
                try
                {
                    rds = rdm.createDataSource(dsName);

                    if (rds == null)
                        //throw new NullPointerException("RemoteDataManager.createDataSource returned null");
                        throw new NullPointerException(ERR_RETURNED_NULL.getText());
                }
                catch (Exception e)
                {
                    // on remote failure clear the RemoteDataManager so it is
                    // recreated the next time

                    //Log.fatal("could not create RemoteDataSource " + dsName, e);
                    Log.fatal(ERR_CREATE_REMOTE_DATA_SOURCE.getText() + " " + dsName, e);
                }

        return rds;
    }

    //--------------------------------------------------------------------------
    /**
     * Package scope method to go to a RemoteDataManager and create a new
     * RemoteDataSource. Used to assist with reconnection logic.
     * @param param, the param defining the RemoteDataSource to create.
     * @return the RemoteDataSource.
     */
    RemoteDataSource createRemoteDataSource(DataSourceParam param)
    {
            RemoteDataSource rds = null;

            // get the remote data manager
            RemoteDataManager rdm = getRemoteDataManager();

            // if successful create and return the RemoteDataSource
            if (rdm != null)
                try
                {
                    rds = rdm.createDataSource(param);

                    if (rds == null)
                        //throw new NullPointerException("RemoteDataManager.createDataSource returned null");
                        throw new NullPointerException(ERR_RETURNED_NULL.getText());
                }
                catch (Exception e)
                {
                    // on remote failure clear the RemoteDataManager so it is
                    // recreated the next time

                    //Log.fatal("could not create RemoteDataSource " + dsName, e);
                    Log.fatal(ERR_CREATE_REMOTE_DATA_SOURCE.getText() + " "
                        + param.getFullName(), e);
                }

        return rds;
    }

    //--------------------------------------------------------------------------
    /**
     * Return the name of the DataSource mapped to this entity name
     * @param the name of the entity to be mapped to a datasource
     * @return String the name of the datasource to be used for this entity
     */
    protected String getEntityDSName(String entityName)
    {
        // return the system name by default
        return DataSource.SYS_DS_NAME;
    }

    //--------------------------------------------------------------------------
    /**
     * @return boolean true if RemoteDataManager is running
     */
    public boolean isDataManagerRemote()
    {
        return _rdmBindName != null;
    }

    //--------------------------------------------------------------------------
    /**
     * Set the DataObjectVersionCache
     * @param dovCache the cache to set
     */
    public void setVersionCache(DataObjectVersionCache dovCache)
    {
        _dovCache = dovCache;
    }

    /**
     * @return DataObjectVersionCache
     */
    public DataObjectVersionCache getVersionCache()
    {
        // if null, create default
        if(_dovCache == null)
            _dovCache = new DataObjectVersionCache();
        return _dovCache;
    }


    //--------------------------------------------------------------------------
    /**
     * Private final method so it cannot be overridden. This method handles
     * the local/remote selection so the same data manager can be configured to
     * use local or remote data sources.
     */
    private final DataSource createDataSource(String dsName)
    {
        DataSource ds = null;

        // if _rdmBindName is set then we are using remote data source/managers,
        // so create a proxy which will later be wired up to the
        // RemoteDataSource, otherwise create locally.
        if (_rdmBindName == null)
            return iCreateDataSource(dsName);
        else
            return new RemoteDataSourceProxy(dsName);
    }

    //--------------------------------------------------------------------------
    /**
     * Private final method so it cannot be overridden. This method handles
     * the local/remote selection so the same data manager can be configured to
     * use local or remote data sources.
     */
    private final DataSource createDataSource(DataSourceParam param)
    {
        DataSource ds = null;

        // if _rdmBindName is set then we are using remote data source/managers,
        // so create a proxy which will later be wired up to the
        // RemoteDataSource, otherwise create locally.
        if (_rdmBindName == null)
            return iCreateDataSource(param);
        else
            return new RemoteDataSourceProxy(param);
    }

    //--------------------------------------------------------------------------
    /**
     * Creates a DataSource based on a DataSource name
     * @param dsName DataSource name
     * @return DataSource
     * @roseuid 3973C823038C
     */
    protected DataSource iCreateDataSource(String dsName)
    {
        // create the system JDBC DataSource by default
        JdbcDataSource ds = new JdbcDataSource();

        // set the name
        ds.setName(dsName);

        // in the current DS creation mechanism this will always be the system
        // data source, so set up the connect and pool info for that DS

        // set connect info using system DS values in config
        String driver = Config.getProp(CKfw.JDBC_DS_DRIVER, "sun.jdbc.odbc.JdbcOdbcDriver");
        String url = Config.getProp(CKfw.JDBC_DS_URL, "");
        String catalog = Config.getProp(CKfw.JDBC_DS_CATALOG, "");
        String user = Config.getProp(CKfw.JDBC_DS_USER);
        String pw = Config.getProp(CKfw.JDBC_DS_PASSWORD);
        ds.setConnectData(driver, url, user, pw);//catalog, 

        // set pool info using system DS values in config
        int poolSize     = Config.getProp(CKfw.SYSTEM_DATASOURCE_POOLSIZE, -1);
        int msLifetime   = Config.getProp(CKfw.SYSTEM_DATASOURCE_MSLIFETIME, -1);
        int msGetTimeout = Config.getProp(CKfw.SYSTEM_DATASOURCE_MSGETTIMEOUT, -5000);

        ds.setPoolData(poolSize, msLifetime, msGetTimeout);

        // return ds
        return ds;
    }

    //--------------------------------------------------------------------------
    /**
     * Creates a DataSource based on a DataSourceParam.
     * May need to be overridden in subclass datamanagers if they add new
     * types of DataSourceParam/DataSource
     * @param param, the DataSourceParam
     * @return DataSource
     */
    protected DataSource
	iCreateDataSource (
	 DataSourceParam param)
    {
        DataSource ds = null;

        if (param instanceof JdbcDataSourceParam)
        {
            // jdbc datasource, create the DataSource and set its param
            JdbcDataSource jdbcDS = new JdbcDataSource();
            ds = jdbcDS;
            jdbcDS.setParam((JdbcDataSourceParam) param);
    		System.out.println ("DataManager iCreateDataSource: driver=" + ((JdbcDataSourceParam) param)._driver + ", url=" + ((JdbcDataSourceParam) param)._url
    				+ ", user=" + ((JdbcDataSourceParam) param)._user);
            
        } else if (param instanceof MessageServerParam)
        {
            /*
			 *	We create the MessageServer datasource from
			 *	the classname in MessageServerParam
			 */
			try
			{
				MessageServerParam msp = (MessageServerParam) param;
				MessageServer ms = (MessageServer) Class.forName (
										msp.getClassName ()).newInstance ();
				ms.setParam (msp);

				ds = ms;

			} catch (Exception e)
			{
            	assert false;				// crash and burn
			}

        } else
        {
            // unknown type
            assert false;
        }

        // return ds
        return ds;
    }

    //--------------------------------------------------------------------------
    /**
     * Flyweight factory for Entity definitions.
     * Entities are mapped to entity names
     * @param entityName entity name
     * @return Entity
     */
    public static synchronized Entity getEntity(String entityName)
    {
        // must have a data object
        Util.argCheckEmpty(entityName);

        // get the datasource for this entity
        DataSource ds = getDataSource(entityName);

        // get the entity from the DataSource
        Entity ent = ds.getEntity(entityName);

        // return
        return ent;
    }

    //--------------------------------------------------------------------------
    /**
     * Flyweight factory for Entity definitions.
     * Entities are mapped to entity names
     * @param dobj DataObject
     * @return Entity
     */
    public static Entity getEntity(DataObject dobj)
    {
        // must have a data object
        if(dobj == null)
            return null;

        // delegate
        return getEntity(dobj.getEntityName());
    }

    //--------------------------------------------------------------------------
    /**
     * Creates an entity based on an entity name
     * @param entityName entity name
     * @return Entity
     */
    public static Entity createEntity(String entityName)
    {
        // Must have a data manager
        assert getDataManager() != null: ERR_NOT_SET.getText();

        // delegate to singleton
        return getDataManager().iCreateEntity(entityName);
    }

    //--------------------------------------------------------------------------
    /**
     * Entity factory. Creates and returns entity definitions based on the
     * supplied entity name. Subclasses should create the entities they are
     * aware of and delegate unknown entity names to this superclass.
     * @param entityName the name of the entity to create.
     * @return Entity the created Entity
     */
    protected Entity iCreateEntity(String entityName)
    {
        // switch on name
        Entity ent = null;

        if (entityName.equals(Feature.ENT_FEATURE))
            ent = Feature.createEntity();
        else if (entityName.equals(Group.ENT_GROUP))
            ent = Group.createEntity();
        else if (entityName.equals(Privilege.ENT_PRIVILEGE))
            ent = Privilege.createEntity();
        else if (entityName.equals(GroupMembership.ENT_GROUPMEMBERSHIP))
            ent = GroupMembership.createEntity();
        else if (entityName.equals(User.ENT_USER))
            ent = User.createEntity();
        else if (entityName.equals(PresentationItem.ENT_PRESENTATION_ITEM))
            ent = PresentationItem.createEntity();
        else if (entityName.equals(Message.ENT_MESSAGE))
            ent = Message.createEntity();
        else if (entityName.equals(MessageDobj.ENT_MS_MESSAGE))
            ent = MessageDobj.createEntity();
        else if (entityName.equals(MessageServer.ENT_MSGSERVER))
            ent = MessageServer.createEntity();
        else if (entityName.equals(MessageServerProfile.ENT_MSGSVR_PROFILE))
            ent = MessageServerProfile.createEntity();

        // return
        return ent;
    }

    //--------------------------------------------------------------------------
    /**
     * Called by the NotificationListenerServer to get the set of notification
     * types to listen to. Called when the listener server starts, changes after
     * that time will have no effect unless the server is stopped and restarted.
     * @return a 2 dimensional array of Strings. For each sub array the first
     *   elements is the notification type and the second is the sub type.
     */
    public String[][] getNotificationTypes()
    {
        // the data manager listens to data change notifications for all entities
        // in the _listenEntities set
        synchronized (_listenEntities)
        {
            // build the notification type array for our listen set
            String notTypes[][] = new String[_listenEntities.size()][2];

            // iterate over the set placing the elements in the array
            Iterator iter = _listenEntities.iterator();
            int index = 0;
            while(iter.hasNext())
            {
                String entityName = (String) iter.next();
                // notification type is always NT_DATA_CHANGE
                notTypes[index][0] = NT_DATA_CHANGE;
                // notificatio subtype is the entity name
                notTypes[index][1] = entityName;
                index++;
            }

            return notTypes;
        }
    }

    //--------------------------------------------------------------------------
    /**
     * This function is called by the Notifier (via NotificationListenerServant
     * and NotificationListenerServer which handles the remoting) for any
     * notifications of a type that this DataManager has been registered as a
     * listener for by calling addListenerEntity.
     * @param notificationType, a string specifying the type of notification.
     * @param notificationSubtype, a string specifying the notification subtype.
     * @param notificationData, notification information specific to this
     *   notificationType. Listeners are expected to know how to interpret this
     *   data and perform appropriate processing. May be null. Must be
     *   Serializable.
     */
    public void onNotification(String notificationType,
        String notificationSubtype, Serializable notificationData)
    {
        // recieving a notification from the remote Notifier
        // we should only be recieving types that we have registered for
        try
        {
            // get the remote DataChangeNotification
            RemoteDCNData rdcn = (RemoteDCNData) notificationData;

            // set the data change notification flag to remote
            rdcn._dcn.setFromRemoteNotifier(true);

            // ignore if this DataManager originated this notification
            if (!_notificationListenerServer.getListenerId().equals(rdcn._sendingDMListener))
            {
                // notification is from someone else, forward to local listeners
                notifyListeners(rdcn._dcn);
            }
        }
        catch (Exception e)
        {
            //Log.error("DataManager.onNotification: failed to forward remote notification", e);
            Log.error(ERR_FORWARD.getText(), e);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Adds the specified entity to the set of entities that we will listen for
     * remote data change notifications from. This operation is disallowed while
     * the listener is operational.
     * @param entityName, the name of the entity to listen for. Not null or
     *   empty. May be the special tag Notifier.NST_ALL to recieve notifications
     *   for all entities.
     * @return true if the listener was added, false if the operation was
     *   disallowed.
     */
    public synchronized boolean addListenerEntity(String entityName)
    {
        Util.argCheckEmpty(entityName);

        // cannot change after listener is running
        if (isListenerRunning())
            return false;

        // add the listener entity
        _listenEntities.add(entityName);

        return true;
    }

    //--------------------------------------------------------------------------
    /**
     * Adds the specified entity to the set of entities that we will send
     * remote data change notifications for. This operation is disallowed while
     * the listener is running.
     * @param entityName, the name of the entity to send for. Not null or empty.
     *   May be the special tag Notifier.NST_ALL to send notifications for
     *   all entities.
     * @return true if the listener was added, false if the operation was
     *   disallowed.
     */
    public synchronized boolean addSenderEntity(String entityName)
    {
        Util.argCheckEmpty(entityName);

        // cannot change after listener is running
        if (isListenerRunning())
            return false;

        // add the sender entity
        _sendEntities.add(entityName);

        return true;
    }

    //--------------------------------------------------------------------------
    /**
     * Remove all remote notification listeners and senders. This operation is
     * disallowed while the listener is running.
     * @return true if the listener was added, false if the operation was
     *   disallowed.
     */
    public synchronized boolean removeSendersAndListeners()
    {
        // cannot change after listener is running
        if (isListenerRunning())
            return false;

        // remove senders and listners
        _sendEntities.clear();
        _listenEntities.clear();

        return true;
    }

    //--------------------------------------------------------------------------
    /**
     * @return true if the listener is running.
     */
    public boolean isListenerRunning()
    {
        return (_notificationListenerServer != null);
    }

    //--------------------------------------------------------------------------
    /**
     * Start the remote notification listener server. Before calling this
     * function use addSenderEntity and addListenerEntity to register the
     * entities that this data manager will send/recieve remote data change
     * notifications for.
     * @return true if the server was started, false if it was already running.
     */
    public synchronized boolean startRemoteNotifications()
    {
        // check if already running
        if (isListenerRunning())
            return false;

        // create the listener server, which will pass notifications back to us
        // through onNotification
        _notificationListenerServer = new NotificationListenerServer(this,
            new String[0]);

        // start the listener server
        _notificationListenerServer.start();

        return true;
    }

    //--------------------------------------------------------------------------
    /**
     * Stop the remote notification listener server. Should be called before
     * system exit if you have started notifications.
     * @return true if the server was stopped, false if it was not running.
     */
    public synchronized boolean stopRemoteNotifications()
    {
        // cannot stop if it is not running
        if (!isListenerRunning())
            return false;

        // stop the listener server
        _notificationListenerServer.stop();
        _notificationListenerServer = null;

        return true;
    }

    //--------------------------------------------------------------------------
    /**
     * Static accessor to get the ConnectionPoolManager from the DataManager
     * singleton, this can then be used to get pooled JDBC connections.
     */
    public static synchronized ConnectionPoolManager getConnectionPoolMgr()
    {
        // get the DataManager
        DataManager dm = DataManager.getDataManager();

        if (dm._connectionPoolMgr == null)
        {
            // if it doesn't exist then create a new one using defaults from
            // the config
            int poolSize     = Config.getProp(CKfw.POOL_MANAGER_POOLSIZE, -1);
            int msLifeime    = Config.getProp(CKfw.POOL_MANAGER_MSLIFETIME, -1);
            int msGetTimeout = Config.getProp(CKfw.POOL_MANAGER_POOLSIZE, 5000);

            dm._connectionPoolMgr = new ConnectionPoolManager(poolSize, msLifeime,
                msGetTimeout);
        }

        // return the ConnectionPoolManager
        return dm._connectionPoolMgr;
    }
}

//==============================================================================
// end of file DataManager.java
//==============================================================================
