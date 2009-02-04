package wsl.fw.msgserver;

// imports
import java.util.Date;
import java.util.Calendar;
import java.util.Locale;
import java.text.DateFormat;
import wsl.fw.util.Type;
import wsl.fw.datasource.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c)
 * Company:
 * @author
 * @version 1.0
 */

public class AppointmentDobj extends ItemDobj
{
    //--------------------------------------------------------------------------
    // constants

    public static final String FLD_SUBJECT = "Subject";
    public static final String FLD_START = "Start";
    public static final String FLD_END = "End";
    public static final String FLD_LOCATION = "Location";
    public static final String FLD_NOTES = "Notes";


    //--------------------------------------------------------------------------
    // attributes

    private Date _startTime;


    //--------------------------------------------------------------------------
    // construction

    /**
     * Param ctor
     * @param name the name of the contact
     */
    public AppointmentDobj(Appointment a)
    {
        // recreate the start time
        Date start = null;
        try
        {
            start = DateFormat.getDateTimeInstance().parse(a._startTime);
        }
        catch(Exception e)
        {
            try
            {
                start = DateFormat.getDateTimeInstance(DateFormat.DEFAULT,
                    DateFormat.DEFAULT, Locale.US).parse(a._startTime);
            }
            catch(Exception e2)
            {}
        }
        if(start != null)
            setStartTime(start);

        // fields
        setFieldValue(FLD_SUBJECT, a._subject);
        setFieldValue(FLD_START, a._startTime);
        setFieldValue(FLD_END, a._endTime);
        setFieldValue(FLD_LOCATION, a._location);
        setFieldValue(FLD_NOTES, a._text);
    }

    /**
     * Create fields
     */
    protected void createFields()
    {
        // fields
        addField(FLD_SUBJECT);
        addField(FLD_START);
        addField(FLD_END);
        addField(FLD_LOCATION);
        addField(FLD_NOTES);
    }


    //--------------------------------------------------------------------------
    // accessors

    /**
     * Returns the app start time
     * @return Date
     */
    public Date getStartTime()
    {
        return _startTime;
    }

    /**
     * Sets the app start time
     * @param startTime
     * @return void
     */
    public void setStartTime(Date startTime)
    {
        _startTime = startTime;
    }


    //--------------------------------------------------------------------------
    // to string

    public String toString()
    {
        // get the time out
        DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT);
        String time = null;
        if(getStartTime() != null)
        {
            try
            {
                time = df.format(getStartTime());
            }
            catch(Exception e)
            {
                time = null;
            }
        }
        if(time == null)
        {
            time = getFieldValue("Start");
            if(time == null)
                time = ":";
        }

        // build return string
        String ret = time + " " + getFieldValue(FLD_SUBJECT);
        return ret;
    }
}