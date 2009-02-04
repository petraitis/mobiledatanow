//==============================================================================
// MessageSender.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.message;

import wsl.fw.notification.NotificationSender;
import wsl.fw.datasource.DataSourceException;
import wsl.fw.util.Config;
import wsl.fw.util.CKfw;
import wsl.fw.security.SecurityId;
import wsl.fw.resource.ResId;

//--------------------------------------------------------------------------
/**
 * Singleton class for sending messages.
 * Users applications must call MessageSender.set() to set the singleton before
 * using the MessageSender.
 * Message handlers (applications that listen for messages and process them)
 * should listen for and send DataChangeNotifications for Message.
 * Clients need not do this as MessageSender sends notifications that the
 * Message handlers can respond to. For a given message type there should be no
 * more than one active handler.
 */
public class MessageSender
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/message/MessageSender.java $ ";

    // resources
    public static final ResId STRING_INVALID_MESSAGE_TYPE  = new ResId("MessageSender.string.InvalidMessageType");

    // Main Message notification type, this is the major type used for
    // sending the notifications. The actual message type (as per
    // getMessageTypes() and Message.getType()) is passed as the notification
    // sub-type.
    public final static String NT_MESSAGE = "wsl.fw.message.type._new_message_";

    /** the singleton */
    private static MessageSender s_singleton = null;

    /** the available message types */
    private String _messageTypes[] = null;

    private SecurityId _systemId = null;

    //--------------------------------------------------------------------------
    /**
     * Constructor, loads the set of message types from the Config.
     */
    public MessageSender()
    {
        // get a system id for authentication
        _systemId = SecurityId.getSystemId();

        // set the message types
        // get the set of message type keys based off the message type prefix
        String typeKeys[] = Config.getSingleton().getSubkeys(CKfw.MESSAGE_TYPE_PREFIX, false, true);

        // init the message tye array ot he correct size
        _messageTypes = new String[typeKeys.length];

        // populate the array with the message types
        for (int i = 0; i < typeKeys.length; i++)
            _messageTypes[i] = Config.getProp(typeKeys[i], STRING_INVALID_MESSAGE_TYPE.getText());
    }

    //--------------------------------------------------------------------------
    /**
     * Set the singleton.
     * @param ms, the MessageSender singleton.
     */
    public static void set(MessageSender ms)
    {
        s_singleton = ms;
    }

    //--------------------------------------------------------------------------
    /**
     * Get the singleton.
     * @return the MessageSender singleton.
     */
    public static MessageSender get()
    {
        return s_singleton;
    }

    //--------------------------------------------------------------------------
    /**
     * @return the available message types.
     */
    public String[] getMessageTypes()
    {
        return _messageTypes;
    }

    //--------------------------------------------------------------------------
    /**
     * Send a message. This will create a Message DataObject, save it and then
     * send a notification so the message can be processed by a handler.
     * @param type, the message type, hsould be one of the types returned by
     *   getMessageTypes().
     * @param destination, the destination that is to receive the message.
     *   i.e. a phone number or email address.
     * @param text, the content of the message.
     * @return true if the notification was sent, false if the notification
     *   failed but the Messageobject was still saved.
     * @throws DataSourceException if the save fails.
     */
    public boolean sendMessage(String type, String destination, String subject,
        String text)
        throws DataSourceException
    {
        // create a message and save it.
        Message message = new Message(type, destination, subject, text);
        message.save();

        // send the notificaton
        return NotificationSender.sendNotification(_systemId, NT_MESSAGE, type,
            message);
    }
}

//==============================================================================
// end of file MessageSender.java
//==============================================================================
