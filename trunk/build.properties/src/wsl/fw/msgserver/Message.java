package wsl.fw.msgserver;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c)
 * Company:
 * @author
 * @version 1.0
 */

public class Message
{
    //--------------------------------------------------------------------------
    // type constants

    public static final String MT_TASK = "IPM.Task";
    public static final String MT_NOTE = "IPM.StickyNote";
    public static final String MT_JOURNAL = "IPM.Activity";
    public static final String MT_MAIL = "IPM.Note";
    public static final String MT_CONTACT = "IPM.Contact";
    public static final String MT_APPOINTMENT = "IPM.Appointment";
    public static final String MT_FOLDER = "IPM.Folder";
    public static final String MT_APP_DAY = "AppDay";


    //--------------------------------------------------------------------------
    // attributes

    public String _subject = "";
    public String _text = "";
    public String _type = "";

    //--------------------------------------------------------------------------
    // construction

    /**
     * 3 arg ctor
     * @param subject the subject of the message
     * @param text the body of the message
     */
    public Message(String subject, String text, String type)
    {
        _subject = subject;
        _text = text;
        _type = type;
    }
}