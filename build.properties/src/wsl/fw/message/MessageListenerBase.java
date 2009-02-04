//==============================================================================
// MessageListenerBase.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.message;

import wsl.fw.datasource.DataManager;
import wsl.fw.datasource.DataSource;
import wsl.fw.datasource.DataSourceException;
import wsl.fw.datasource.Query;
import wsl.fw.datasource.QueryCriterium;
import wsl.fw.datasource.RecordSet;
import wsl.fw.remote.LocalServerFactory;
import wsl.fw.notification.NRNotificationListener;
import wsl.fw.notification.NotificationListenerServer;
import wsl.fw.util.Config;
import wsl.fw.util.Log;
import wsl.fw.util.Util;
import wsl.fw.util.CKfw;
import java.io.Serializable;
import java.util.HashSet;
import wsl.fw.resource.ResId;

//------------------------------------------------------------------------------
/**
 * Base class for Message Listeners, which are handlers for specific message
 * types.
 */
public abstract class MessageListenerBase implements NRNotificationListener
{
    // version tag
    private final static String _ident = "$Date: 2004/01/06 01:49:11 $  $Revision: 1.2 $ "
        + "$Archive: /Framework/Source/wsl/fw/message/MessageListenerBase.java $ ";

    // resources
    public static final ResId ERR_LOAD_MESSAGES  = new ResId("MessageListenerBase.error.LoadMessages");
    public static final ResId ERR_NON_MESSAGE  = new ResId("MessageListenerBase.error.NonMessage");
    public static final ResId ERR_NOT_MESSAGE  = new ResId("MessageListenerBase.error.NotMessage");
    public static final ResId ERR_LOAD_MESSAGE  = new ResId("MessageListenerBase.error.LoadMessage");
    public static final ResId ERR_MESSAGE  = new ResId("MessageListenerBase.error.Message");

    // OM_ constants used for onMessage return values
    public final static int OM_NOT_PROCESSED = 1;
    public final static int OM_FAILED        = 2;
    public final static int OM_PROCESSED     = 3;

    // members
    private NotificationListenerServer _notificationListenerServer = null;
    private HashSet                    _lockSet = new HashSet();
    private int _msLoadRetry;

    //--------------------------------------------------------------------------
    /**
     * Constructor.
     * @param args, command line arguments which may be used to override config
     *   defaults for context and LocalServerFactory.
     */
    public MessageListenerBase(String args[])
    {
        // load Config context
        Config.getSingleton().addContext(args, CKfw.MESSAGELISTENER_CONTEXT);

        // init LocalServerFactory
        LocalServerFactory.setArgs(args);

        // get load period
        _msLoadRetry = Config.getProp(CKfw.MESSAGELISTENER_MS_LOAD_RETRY, 1000);

        // set DataManager if it does not exist, else chech the class is correct
        DataManager dm      = DataManager.getDataManager();
        Class       dmClass = getDataManagerClass();
        if (dm == null)
        {
            try
            {
                DataManager.setDataManager((DataManager) dmClass.newInstance());
            }
            catch (Exception e)
            {
                assert false: e.toString();
            }
        }
        else
            assert dm.getClass().equals(dmClass);

    }

    //--------------------------------------------------------------------------
    /**
     * Virtual to be overridden by concrete subclasses to get the type of
     * DataManager to create.
     * @return the class of the desired data manager, a Class object that
     *   specifies DataManager or a subclass.
     */
    protected abstract Class getDataManagerClass();

    //--------------------------------------------------------------------------
    /**
     * Virtual to be overridden by concrete subclasses to get the message types
     * that this Message Listener is to handle.
     * @return an array of Strings defining the desired message types.
     */
    protected abstract String[] getMessageTypes();

    //--------------------------------------------------------------------------
    /**
     * Virtual to be overridden by concrete subclasses to handle the processing
     * of a message. Handler implementation should use lock and unlock to avoid
     * possible thread collission.
     * @param msgType, the type of message.
     * @param msg, the Message.
     * @return one of the OM_ constants that indicate whether the message has
     *   been processed and how it should be updated in the DB.
     */
    protected abstract int onMessage(String msgType, Message msg);

    //--------------------------------------------------------------------------
    /**
     * Generate an onMessage call for the first (max) Messages of desired types
     * that are still in the DB (i.e. pending processing, STATUS_NEW).
     * @param max, the maximum number of pings to generate for each message type
     *   handled by this listener, if 0 then all.
     */
    public synchronized void pingDeferred(int max)
    {
        String     msgTypes[] = getMessageTypes();
        DataSource ds         = DataManager.getDataSource(Message.ENT_MESSAGE);

        // iterate over the message types
        for (int i = 0; i < msgTypes.length; i++)
        {
            // query to get all NEW messages of the specified message types
            Query query = new Query();
            query.addQueryCriterium(new QueryCriterium(Message.ENT_MESSAGE,
                Message.FLD_TYPE, QueryCriterium.OP_EQUALS, msgTypes[i]));
            query.addQueryCriterium(new QueryCriterium(Message.ENT_MESSAGE,
                Message.FLD_STATUS, QueryCriterium.OP_EQUALS,
                new Integer(Message.STATUS_NEW)));

            try
            {
                // perform the query and iterate up to max records
                int count = 0;
                RecordSet rs = ds.select(query);
                while (rs.next() && (max == 0 || count < max))
                {
                    // forward the message for processing
                    Message msg = (Message) rs.getCurrentObject();
                    forwardMessage(msgTypes[i], msg);
                    count++;
                }
            }
            catch (DataSourceException e)
            {
                Log.error(ERR_LOAD_MESSAGES.getText()
                    + e.toString());
            }
        }
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
    public synchronized boolean startListening()
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
     * Stop the message listener server. Should be called before
     * system exit if you have started listening.
     * @return true if the server was stopped, false if it was not running.
     */
    public synchronized boolean stopListener()
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
     * Called by the NotificationListenerServer to get the set of notification
     * types to listen to. Returns a NT_MESSAGE with a subtype matching each
     * message type.
     * @return a 2 dimensional array of Strings. For each sub array the first
     *   elements is the notification type and the second is the sub type.
     */
    public String[][] getNotificationTypes()
    {
        // our notification types are NT_MESSAGE with a subtype of message type
        String msgTypes[]   = getMessageTypes();
        String notTypes[][] = new String[msgTypes.length][2];

        for (int i = 0; i < msgTypes.length; i++)
        {
            notTypes[i][0] = MessageSender.NT_MESSAGE;
            notTypes[i][1] = msgTypes[i];
        }

        return notTypes;
    }

    //--------------------------------------------------------------------------
    /**
     * This function is called by the Notifier (via NotificationListenerServant
     * and NotificationListenerServer which handles the remoting) for the
     * message types we have registered for.
     * @param notificationType, NT_MESSAGE.
     * @param notificationSubtype, the message type.
     * @param notificationData, the Message.
     */
    public void onNotification(String notificationType,
        String notificationSubtype, Serializable notificationData)
    {
        if (!notificationType.equals(MessageSender.NT_MESSAGE))
            //Log.error("MessageListenerBase.onNotification, received a "
            //    + "non-message notification: " + notificationType);
            Log.error(ERR_NON_MESSAGE.getText() + " " + notificationType);
        else if (!(notificationData instanceof Message))
            Log.error(ERR_NOT_MESSAGE.getText()
                + notificationData.getClass());
        else
            forwardMessage(notificationSubtype, (Message) notificationData);
    }

    //--------------------------------------------------------------------------
    /**
     * Trivial non-remote lock handler to avoid thread colision.
     * Logically locks the Message to assume ownership of it. The forwardMessage
     *   function uses this to should acquire a lock before forwarding the
     *   message to the onMessage handler.
     * @param msg, the message to lock and assume ownership of. Uses DB systemId
     *   to identify Messages.
     * @return true if the lock was acquired, in which case unlock must be
     *   called. If false someone else has the lock and is processing it.
     */
    private synchronized boolean lock(Message msg)
    {
        Util.argCheckNull(msg);

        Object key = msg.getId();

        if (_lockSet.contains(key))
            return false;
        else
            return _lockSet.add(key);
    }

    //--------------------------------------------------------------------------
    /**
     * Release a lock acquired wit lock. May only be called by the lock owner.
     * @param msg, the Message to unlock.
     * @return true if the lock existed and was released.
     */
    private synchronized boolean unlock(Message msg)
    {
        return _lockSet.remove(msg.getId());
    }

    //--------------------------------------------------------------------------
    /**
     * Forward the message to the onMessage handler. Takes care of locking and
     * deletes the Message if onMessage returns true.
     * possible thread collission.
     * @param msgType, the type of message.
     * @param msg, the Message.
     */
    private void forwardMessage(String msgType, Message msg)
    {
        // try to lock if no lock the someone else is already processing it so
        // skip this message
        if (lock(msg))
        try
        {
            try
            {
                // having got the local lock we load from db, ensure it is not being
                // processed and mark as processing
                Message dbMessage = new Message();
                dbMessage.setId(msg.getId());

                boolean bLoaded = false;

                // retry loop trying to load Message from BD
                final int MAX_ATTEMPTS = 4;
                for (int i = 0; i < MAX_ATTEMPTS; i++)
                {
                    // try to load
                    bLoaded = dbMessage.load();

                    // exit loop if successful
                    if (bLoaded)
                        break;

                    // failed, sleep before retrying
                    try
                    {
                        Thread.sleep(_msLoadRetry);
                    }
                    catch (InterruptedException e)
                    {
                    }
                }

                if (bLoaded)
                {
                    if (dbMessage.getStatus() == Message.STATUS_NEW)
                    {
                        dbMessage.setStatus(Message.STATUS_PROCESSING);
                        dbMessage.save();

                        // having marked as processing we pass on for processing
                        int rv = onMessage(msgType, dbMessage);

                        // post processing depends on return from onMessage
                        switch (rv)
                        {
                            case OM_PROCESSED :
                                // processed, delete from DB
                                dbMessage.delete();
                                break;

                            case OM_NOT_PROCESSED :
                                // not processed, return status to new
                                dbMessage.setStatus(Message.STATUS_NEW);
                                dbMessage.save();
                                break;

                            case OM_FAILED :
                            default :
                                // processing failed, set status to FAILED
                                dbMessage.setStatus(Message.STATUS_FAILED);
                                dbMessage.save();
                                break;
                        }
                    }
                }
                else
                    //Log.warning("MessageListenerBase.forwardMessage: timed out "
                    //    + "trying to load Message from DB");
                    Log.warning(ERR_LOAD_MESSAGE.getText());
            }
            catch (DataSourceException e)
            {
                Log.error(ERR_MESSAGE.getText() + " "
                    + e.toString());
            }
        }
        finally
        {
            // finally ensures we always unlock anything we locked
            unlock(msg);
        }
    }
}

//==============================================================================
// end of file MessageListenerBase.java
//==============================================================================
