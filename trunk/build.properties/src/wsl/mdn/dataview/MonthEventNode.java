package wsl.mdn.dataview;

// imports
import java.util.Date;
import java.util.Vector;
import wsl.fw.util.Util;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c)
 * Company:
 * @author
 * @version 1.0
 */

public class MonthEventNode
{
    //--------------------------------------------------------------------------
    // attributes

    private Vector _dens = new Vector();
    private Date _date;


    //--------------------------------------------------------------------------
    // construction

    /**
     * Date ctor
     * @param date the my of this node
     */
    public MonthEventNode(Date date)
    {
        _date = date;
    }


    //--------------------------------------------------------------------------
    // accessors

    /**
     * @return Date the my of this node
     */
    public Date getDate()
    {
        return _date;
    }


    //--------------------------------------------------------------------------
    // events

    /**
     * @return Vector DayEventNodes for the day sorted by time
     */
    public Vector getDayEventNodes()
    {
        return _dens;
    }


    //--------------------------------------------------------------------------
    // misc

    /**
     * toString()
     */
    public String toString()
    {
        return "MonthEventNode: " + _date.toString();
    }
}