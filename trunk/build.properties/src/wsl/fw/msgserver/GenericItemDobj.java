package wsl.fw.msgserver;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c)
 * Company:
 * @author
 * @version 1.0
 */

public class GenericItemDobj extends ItemDobj
{
    //--------------------------------------------------------------------------
    // constants

    public static final String FLD_SUBJECT = "Subject";
    public static final String FLD_TEXT = "Text";


    //--------------------------------------------------------------------------
    // construction

    /**
     * Blank ctor
     */
    public GenericItemDobj()
    {
    }

    /**
     * Message ctor
     */
    public GenericItemDobj(Message m)
    {
        // set field values
        setFieldValue(FLD_SUBJECT, m._subject);
        setFieldValue(FLD_TEXT, m._text);
    }

    /**
     * Create fields
     */
    protected void createFields()
    {
        addField(FLD_SUBJECT);
        addField(FLD_TEXT);
    }


    //--------------------------------------------------------------------------
    // toString

    public String toString()
    {
        return getFieldValue(FLD_SUBJECT);
    }
}