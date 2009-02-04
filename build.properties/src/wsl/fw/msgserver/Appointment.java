package wsl.fw.msgserver;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c)
 * Company:
 * @author
 * @version 1.0
 */

public class Appointment extends Message
{
    //--------------------------------------------------------------------------
    // attributes

    public String _startTime = "";
    public String _endTime = "";
    public String _location = "";


    //--------------------------------------------------------------------------
    // construction

    /**
     * Param ctor
     * @param name the name of the contact
     */
    public Appointment(String type, String subject, String text,
        String startTime, String endTime, String location)
    {
        super(subject, text, type);
        _startTime = startTime;
        _endTime = endTime;
        _location = location;
    }
}