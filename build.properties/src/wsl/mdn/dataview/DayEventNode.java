package wsl.mdn.dataview;

// imports
import java.util.Vector;
import java.util.Date;
import wsl.fw.util.Util;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c)
 * Company:
 * @author
 * @version 1.0
 */

public class DayEventNode
{
    //--------------------------------------------------------------------------
    // attributes

    private Vector _events = new Vector();
    private Date _date;


    //--------------------------------------------------------------------------
    // construction

    /**
     * Date ctor
     * @param date the dmy of this node
     */
    public DayEventNode(Date date)
    {
        _date = date;
    }


    //--------------------------------------------------------------------------
    // accessors

    /**
     * @return Date the dmy of this node
     */
    public Date getDate()
    {
        return _date;
    }


    //--------------------------------------------------------------------------
    // events

    /**
     * @return Vector CalendarEvents for the day sorted by time
     */
    public Vector getEvents()
    {
        return _events;
    }

    /**
     * Add an event to the events vector sorted by dmyhm
     * @param event the CalendarEvent to add
     */
    public void addEvent(CalendarEvent event)
    {
        // validate
        Util.argCheckNull(event);

        // iterate events
        CalendarEvent temp;
        boolean isAdded = false;
        for(int i = 0; !isAdded && i < _events.size(); i++)
        {
            // get the event and compare
            temp = (CalendarEvent)_events.elementAt(i);
            if(temp != null && event.getDateTime().after(event.getDateTime()))
            {
                _events.insertElementAt(event, i);
                isAdded = true;
            }
        }

        // if not added, add to end
        if(!isAdded)
            _events.add(event);
    }


    //--------------------------------------------------------------------------
    // misc

    /**
     * toString()
     */
    public String toString()
    {
        return _date.toString();
    }
}