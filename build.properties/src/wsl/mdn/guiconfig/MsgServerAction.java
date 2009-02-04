package wsl.mdn.guiconfig;

// imports
import java.util.Vector;
import wsl.fw.datasource.*;
import wsl.fw.msgserver.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:
 * @author
 * @version 1.0
 */

public class MsgServerAction extends MenuAction
{
    //--------------------------------------------------------------------------
    // action type

    public final static int AT_MSGSERVER = 0;
    public final static int AT_INBOX = 1;
    public final static int AT_INBOX_ALL = 2;
    public final static int AT_INBOX_UNREAD = 3;
    public final static int AT_CONTACTS = 4;
    public final static int AT_ADDRESS_BOOK = 5;
    public final static int AT_CALENDAR = 6;
    public final static int AT_CALENDAR_TODAY = 7;
    public final static int AT_CALENDAR_TOMMORROW = 8;
    public final static int AT_CALENDAR_THIS_WEEK = 9;
    public final static int AT_CALENDAR_NEXT_WEEK = 10;
    public final static int AT_FOLDERS = 11;
    public final static int AT_INBOX_DELIVER_NOW = 12;


    //--------------------------------------------------------------------------
    // message server flags

    public static final int MSF_NONE = 0;
    public static final int MSF_INBOX = 1;
    public static final int MSF_FOLDERS = 2;
    public static final int MSF_CONTACTS = 4;
    public static final int MSF_CALENDAR = 8;


    //--------------------------------------------------------------------------
    // attributes

    private int _actionType = AT_MSGSERVER;


    //--------------------------------------------------------------------------
    // construction

    public MsgServerAction()
    {
    }

    /**
     * Param ctor
     * @param action type
     */
    public MsgServerAction(int actionType)
    {
        _actionType = actionType;
    }


    //--------------------------------------------------------------------------
    // accessors

    /**
     * @return the action type
     */
    public int getActionType()
    {
        return _actionType;
    }


    //--------------------------------------------------------------------------
    /**
     * @return the ID of the message server.
     */
    public int getMsgServerId()
    {
        return getIntValue(FLD_MSGSERVERID);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the ID of the message server.
     */
    public void setMsgServerId(int id)
    {
        setValue(FLD_MSGSERVERID, id);
    }

    //--------------------------------------------------------------------------
    /**
     * @return the flags of the message server
     */
    public int getMsgServerFlags()
    {
        return getIntValue(FLD_MSGSERVER_FLAGS);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the flags of the message server
     */
    public void setMsgServerFlags(int flags)
    {
        setValue(FLD_MSGSERVER_FLAGS, flags);
    }

    //--------------------------------------------------------------------------
    /**
     * @return true if the flag is set
     */
    public boolean hasMsgServerFlag(int flag)
    {
        return (getIntValue(FLD_MSGSERVER_FLAGS) & flag) > 0;
    }
}