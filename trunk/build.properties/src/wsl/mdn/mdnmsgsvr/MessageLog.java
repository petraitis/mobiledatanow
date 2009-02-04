package wsl.mdn.mdnmsgsvr;

import java.util.Date;

import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.DefaultKeyGeneratorData;
import wsl.fw.datasource.Entity;
import wsl.fw.datasource.EntityImpl;
import wsl.fw.datasource.Field;
import wsl.fw.datasource.FieldImpl;
import wsl.mdn.mdnim.IMMessage;

public class MessageLog extends DataObject{
    // version tag
    private final static String _ident = "$Date: 2006/01/24 15:27:00 $  $Revision: 1.2 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/mdnmsgsvr/MessageLog.java $ ";

    //--------------------------------------------------------------------------
    // constants
	
    // the entity name
    public final static String ENT_MESSAGE_LOG = "TBL_MESSAGE_LOG";

    // field names
    public final static String FLD_ID       		       = "FLD_ID";
    public final static String FLD_TEXT     		       = "FLD_TEXT";
    public final static String FLD_RECEIVED_DATE           = "FLD_RECEIVED_DATE";
    public final static String FLD_SENDER_USER_ID 	       = "FLD_SENDER_USER_ID";
    public final static String FLD_RECEIVER_CONNECTION_ID  = "FLD_RECEIVER_CONNECTION_ID";
    public final static String FLD_MSG_TYPE        	   	   = "FLD_MSG_TYPE";//SMS, IM, Email

    //--------------------------------------------------------------------------
    /**
     * Default constructor. Since an MessageLog is invalid if it is not correctly initialized
     * ensure that setter methods are called when using this constructor/
     */
    public MessageLog()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Static factory method to create the entity to be used by this dataobject
     * and any subclasses. This is called by the DataManager's factory when
     * creating a MESSAGE_LOG entity.
     * @return the created entity.
     */
    public static Entity createEntity()
    {
        // create the IMMessage entity
        Entity imMessageEntity = new EntityImpl(ENT_MESSAGE_LOG, MessageLog.class);

        // add the key generator for the system id
        imMessageEntity.addKeyGeneratorData(new DefaultKeyGeneratorData(ENT_MESSAGE_LOG, FLD_ID));

        // create the fields and add them to the entity
        imMessageEntity.addField(new FieldImpl(FLD_ID, Field.FT_INTEGER, Field.FF_UNIQUE_KEY | Field.FF_SYSTEM_KEY));
        imMessageEntity.addField(new FieldImpl(FLD_TEXT, Field.FT_STRING,  Field.FF_NONE));
        imMessageEntity.addField(new FieldImpl(FLD_SENDER_USER_ID, Field.FT_INTEGER, Field.FF_NONE));
        imMessageEntity.addField(new FieldImpl(FLD_RECEIVER_CONNECTION_ID, Field.FT_INTEGER, Field.FF_NONE));
        imMessageEntity.addField(new FieldImpl(FLD_RECEIVED_DATE, Field.FT_DATETIME, Field.FF_NONE, 1024));
        imMessageEntity.addField(new FieldImpl(FLD_MSG_TYPE, Field.FT_STRING, Field.FF_NONE));
        // return the entity
        return imMessageEntity;
    }

    //--------------------------------------------------------------------------
    /**
     * Return the Entity name.
     * @return the entity name.
     */
    public String getEntityName()
    {
        return ENT_MESSAGE_LOG;
    }
    //--------------------------------------------------------------------------
    /**
     * @return the ID
     */
    public int getId()
    {
        return getIntValue(FLD_ID);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the ID.
     */
    public void setId(int id)
    {
        setValue(FLD_ID, id);
    }
    //  --------------------------------------------------------------------------
    /**
     * Get the text of the message.
     * @return the text of the message.
     */
    public String getText()
    {
        return getStringValue(FLD_TEXT);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the Text Message.
     * @param text
     */
    public void setText(String text)
    {
        // check parameter, null or empty user names are not permitted
//        Util.argCheckEmpty(name);
        setValue(FLD_TEXT, text);
    }
    //--------------------------------------------------------------------------    
    /**
     * @return int the sender userId
     */
    public int getUserId()
    {
        return getIntValue(FLD_SENDER_USER_ID);
    }
    
    //--------------------------------------------------------------------------
    /**
     * Set the User Id
     * @param userId
     */
    public void setUserId(int userId)
    {
        setValue(FLD_SENDER_USER_ID, userId);
    }

    //--------------------------------------------------------------------------    
    /**
     * @return int the reciever connection id
     */
    public int getConnectionId()
    {
        return getIntValue(FLD_RECEIVER_CONNECTION_ID);
    }
    
    //--------------------------------------------------------------------------
    /**
     * Set the ConnectionId
     * @param id
     */
    public void setConnectionId(int connectionId)
    {
        setValue(FLD_RECEIVER_CONNECTION_ID, connectionId);
    }

    //--------------------------------------------------------------------------
    /**
     * Get the recieved date of the message
     * @return the userName.
     */
    public Date getDate()
    {
        return getDateValue(FLD_RECEIVED_DATE);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the recieved date of the message
     * @param date.
     */
    public void setDate(Date date)
    {
        setValue(FLD_RECEIVED_DATE, date);
    }
    
    //  --------------------------------------------------------------------------
    /**
     * Get the text of the messageType.
     * @return the type of the message.
     */
    public String getMessageType()
    {
        return getStringValue(FLD_MSG_TYPE);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the Message type.
     * @param Message type
     */
    public void setMessageType(String messageType)
    {
        setValue(FLD_MSG_TYPE, messageType);
    }
    
}

