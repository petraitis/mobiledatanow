package wsl.fw.msgserver;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c)
 * Company:
 * @author
 * @version 1.0
 */

public class Folder extends Message
{
    //--------------------------------------------------------------------------
    // constants

    // folder content types
    public static final String FCT_CALENDAR = "IPF.Appointment";
    public static final String FCT_CONTACT = "IPF.Contact";
    public static final String FCT_MAIL = "IPF.Note";
    public static final String FCT_JOURNAL = "IPF.Journal";
    public static final String FCT_NOTE = "IPF.StickyNote";
    public static final String FCT_TASK = "IPF.Task";
    public static final String FCT_MIXED = "IPF.Mixed";

    // default folder names
    public static final String DFN_INBOX = "Inbox";
    public static final String DFN_CONTACTS = "Contacts";
    public static final String DFN_TASKS = "Tasks";
    public static final String DFN_CALENDAR = "Calendar";
    public static final String DFN_DELETED_ITEMS = "Deleted Items";
    public static final String DFN_JOURNAL = "Journal";
    public static final String DFN_NOTES = "Notes";
    public static final String DFN_OUTBOX = "Outbox";
    public static final String DFN_SENT_ITEMS = "Sent Items";


    //--------------------------------------------------------------------------
    // attributes

    public String _name = "";
    public String _id;
    public String _contentType = "";


    //--------------------------------------------------------------------------
    // construction

    /**
     * Name ctor
     * @param name the name of the folder
     */
    public Folder(String name, String id)
    {
        this(name, id, FCT_MIXED);
    }

    /**
     * Name ctor
     * @param name the name of the folder
     */
    public Folder(String name, String id, String contentType)
    {
        super(name, id, MT_FOLDER);
        _name = name;
        _id = id;
        _contentType = contentType;
    }
}