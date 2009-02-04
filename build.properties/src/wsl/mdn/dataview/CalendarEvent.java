package wsl.mdn.dataview;

// imports
import java.util.Date;
import java.util.Vector;
import java.util.Calendar;
import java.awt.Color;
import wsl.fw.util.Type;
import wsl.fw.util.Util;
import wsl.fw.datasource.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c)
 * Company:
 * @author
 * @version 1.0
 */

public class CalendarEvent
{
    //--------------------------------------------------------------------------
    // attributes

    private Date _dateTime;
    private DataTransfer _dt;
    private Color _color = null;
    private static Calendar _cal = Calendar.getInstance();
    private static Calendar _cal2 = Calendar.getInstance();


    //--------------------------------------------------------------------------
    // construction

    /**
     * Param ctor
     * @param dt the DataTransfer of this event
     * @param dateTime the date and time of this event
     */
    public CalendarEvent(DataTransfer dt, Date dateTime)
    {
        // set attribs
        _dt = dt;
        _dateTime = dateTime;
    }


    //--------------------------------------------------------------------------
    // accessors

    /**
     * @return DataTransfer the DataTransfer of this event
     */
    public DataTransfer getDataTransfer()
    {
        return _dt;
    }

    /**
     * @return Date the date and time of this event
     */
    public Date getDateTime()
    {
        return _dateTime;
    }

    /**
     * Set the color
     * @param color the Color to set
     */
    public void setColor(Color color)
    {
        _color = color;
    }

    /**
     * @return Color the color for the CalendarEvent
     */
    public Color getColor()
    {
        return _color;
    }


    //--------------------------------------------------------------------------
    // timespan statics

    /**
     * Get the calendar events for a month
     * @param month the month of the year (1 - 12)
     * @param year the year (4 digit)
     * @return Vector a Vector of DayEventNodes sorted by date
     */
    public static MonthEventNode getMonthEvents(int month, int year)
        throws DataSourceException
    {
        return getMonthEvents(month, year, Type.NULL_INTEGER);
    }

    /**
     * Get the calendar events for a month
     * @param month the month of the year (1 - 12)
     * @param year the year (4 digit)
     * @parm dataTransferId the id of the data transfer to build events for
     * @return Vector a Vector of DayEventNodes sorted by date
     */
    public static MonthEventNode getMonthEvents(int month, int year, int dataTransferId)
        throws DataSourceException
    {
        // set the parameter dates
        month -= 1;
        _cal.set(year, month, 1, 0, 0, 0);
        _cal.add(Calendar.SECOND, -1);
        Date lastMonth = _cal.getTime();
        _cal.set(year, month, 1, 0, 0, 0);
        _cal.add(Calendar.MONTH, 1);
        Date nextMonth = _cal.getTime();

        // build the query
        Query q = new Query(Scheduling.ENT_SCHEDULING);
        q.addQueryEntity(DataTransfer.ENT_DATATRANSFER);
        QueryCriterium qc = new QueryCriterium(Scheduling.ENT_SCHEDULING,
            Scheduling.FLD_ENDDATE, QueryCriterium.OP_GREATER_THAN, lastMonth);
        qc.setOrIsNull(true);
        q.addQueryCriterium(qc);
        q.addQueryCriterium(new QueryCriterium(Scheduling.ENT_SCHEDULING,
            Scheduling.FLD_STARTDATE, QueryCriterium.OP_LESS_THAN, nextMonth));
        if(dataTransferId != Type.NULL_INTEGER)
        {
            q.addQueryCriterium(new QueryCriterium(Scheduling.ENT_SCHEDULING,
                Scheduling.FLD_DTID, QueryCriterium.OP_EQUALS,
                new Integer(dataTransferId)));
        }

        // select potential schedulings
        _cal.clear();
        _cal.set(year, month, 0);
        MonthEventNode men = new MonthEventNode(_cal.getTime());
        Color color = null;
        Scheduling sched;
        DataTransfer dt;
        RecordSet rs = DataManager.getSystemDS().select(q);
        while(rs != null && rs.next())
        {
            sched = (Scheduling)rs.getCurrentObject(Scheduling.ENT_SCHEDULING);
            dt = (DataTransfer)rs.getCurrentObject(DataTransfer.ENT_DATATRANSFER);
            sched.setDataTransfer(dt);
            color = getNextColor(color);
            buildCalendarEvents(men, sched, lastMonth, nextMonth, color);
        }

        // return
        return men;
    }

    /**
     * Build the calendar events for a particular scheduling into
     * a Vector of DayEventNodes
     * @param dayNodes the Vector of DayEventNodes
     * @param sched the Scheduling to find build CalendarEvents for
     * @param lowDate the date that calendar events must be > to
     * @param highDate the date that calendar events must be <
     */
    private static void buildCalendarEvents(MonthEventNode men, Scheduling sched,
        Date lowDate, Date highDate, Color color) throws DataSourceException
    {
        // get day nodes
        //Vector dayNodes = men.getDayEventNodes();

        // set start
        Date startDate = sched.getStartDate();
        Date endDate = sched.getEndDate();
        //int repeatType = sched.getRepeatType();
        int repeatCount = sched.getRepeatCount();
        DataTransfer dt = sched.getDataTransfer();
        if(startDate == null)
            throw new RuntimeException("Scheduling has no start date");
        _cal.setTime(startDate);

        // iterate rolling date
        int rt = sched.getRepeatType();
        DayEventNode den;
        CalendarEvent ce;
        while((rt == Scheduling.REPEATTYPE_NONE || endDate == null ||
            _cal.getTime().before(endDate)) && _cal.getTime().before(highDate))
        {
            // if in the range
            if(_cal.getTime().after(lowDate))
            {
                // get the day node
                den = getDayEventNode(men, _cal.getTime());
                if(den != null)
                {
                    // add to the day node
                    ce = new CalendarEvent(dt, _cal.getTime());
                    ce.setColor(color);
                    den.addEvent(ce);
                }
            }

            // roll date
            if(rt == Scheduling.REPEATTYPE_MONTH)
                _cal.add(Calendar.MONTH, repeatCount);
            else if(rt == Scheduling.REPEATTYPE_WEEK)
                _cal.add(Calendar.WEEK_OF_MONTH, repeatCount);
            else if(rt == Scheduling.REPEATTYPE_DAY)
                _cal.add(Calendar.DATE, repeatCount);
            else if(rt == Scheduling.REPEATTYPE_HOUR)
                _cal.add(Calendar.HOUR_OF_DAY, repeatCount);
            else if(rt == Scheduling.REPEATTYPE_NONE)
                break;
        }
    }

    /**
     * Get the DayEventNode for a date
     * @param dayNodes the Vector of DayaEventNodes
     * @param date the Date to get a node for
     * @return DayEventNode
     */
    private static DayEventNode getDayEventNode(MonthEventNode men, Date date)
    {
        // validate
        Util.argCheckNull(men);

        // get day nodes
        Vector dayNodes = men.getDayEventNodes();

        // get dmy
        _cal2.setTime(date);
        int d = _cal2.get(Calendar.DATE);

        // iterate the vector
        DayEventNode ret = null;
        DayEventNode temp;
        for(int i = 0; i < dayNodes.size(); i++)
        {
            // get the den
            temp = (DayEventNode)dayNodes.elementAt(i);
            if(temp != null)
            {
                // compare days
                _cal2.setTime(temp.getDate());
                int d2 = _cal2.get(Calendar.DATE);
                if(d == d2)
                {
                    ret = temp;
                    break;
                }
                else if(d2 > d)
                {
                    ret = new DayEventNode(date);
                    dayNodes.insertElementAt(ret, i);
                    break;
                }
            }
        }

        // if null add to end
        if(ret == null)
        {
            ret = new DayEventNode(date);
            dayNodes.add(ret);
        }

        // return
        return ret;
    }

    /**
     * Get the calendar events for a day
     * @param date the date
     * @return DayEventNode
     */
    public static DayEventNode getDayEvents(Date date)
        throws DataSourceException
    {
        _cal.setTime(date);
        return getDayEvents(_cal.get(Calendar.DATE),
            _cal.get(Calendar.MONTH) + 1, _cal.get(Calendar.YEAR), Type.NULL_INTEGER);
    }

    /**
     * Get the calendar events for a day
     * @param day the day
     * @param month the month of the year (1 - 12)
     * @param year the year (4 digit)
     * @return DayEventNode
     */
    public static DayEventNode getDayEvents(int day, int month, int year)
        throws DataSourceException
    {
        return getDayEvents(day, month, year, Type.NULL_INTEGER);
    }

    /**
     * Get the calendar events for a day
     * @param day the day
     * @param month the month of the year (1 - 12)
     * @param year the year (4 digit)
     * @parm dataTransferId the id of the data transfer to build events for
     * @return DayEventNode
     */
    public static DayEventNode getDayEvents(int day, int month, int year, int dataTransferId)
        throws DataSourceException
    {
        // set the parameter dates
        month -= 1;
        _cal.set(year, month, day, 0, 0, 0);
        _cal.add(Calendar.SECOND, -1);
        Date yesterday = _cal.getTime();
        _cal.set(year, month, day, 0, 0, 0);
        _cal.add(Calendar.DATE, 1);
        Date tomorrow = _cal.getTime();

        // build the query
        Query q = new Query(Scheduling.ENT_SCHEDULING);
        q.addQueryEntity(DataTransfer.ENT_DATATRANSFER);
        QueryCriterium qc = new QueryCriterium(Scheduling.ENT_SCHEDULING,
            Scheduling.FLD_ENDDATE, QueryCriterium.OP_GREATER_THAN, yesterday);
        qc.setOrIsNull(true);
        q.addQueryCriterium(qc);
        q.addQueryCriterium(new QueryCriterium(Scheduling.ENT_SCHEDULING,
            Scheduling.FLD_STARTDATE, QueryCriterium.OP_LESS_THAN, tomorrow));
        if(dataTransferId != Type.NULL_INTEGER)
        {
            q.addQueryCriterium(new QueryCriterium(Scheduling.ENT_SCHEDULING,
                Scheduling.FLD_DTID, QueryCriterium.OP_EQUALS,
                new Integer(dataTransferId)));
        }

        // select potential schedulings
        _cal.clear();
        _cal.set(year, month, 0);
        MonthEventNode men = new MonthEventNode(_cal.getTime());
        Scheduling sched;
        DataTransfer dt;
        RecordSet rs = DataManager.getSystemDS().select(q);
        Color color = null;
        while(rs != null && rs.next())
        {
            sched = (Scheduling)rs.getCurrentObject(Scheduling.ENT_SCHEDULING);
            dt = (DataTransfer)rs.getCurrentObject(DataTransfer.ENT_DATATRANSFER);
            sched.setDataTransfer(dt);
            color = getNextColor(color);
            buildCalendarEvents(men, sched, yesterday, tomorrow, color);
        }

        // return
        Vector dens = men.getDayEventNodes();
        return (dens.size() > 0)? (DayEventNode)dens.elementAt(0): null;
    }


    /**
     * returns the next color
     */
    public static Color getNextColor(Color lastColor)
    {
        //switch on last color
        if(lastColor == null)
            return Color.magenta;
        else if(lastColor == Color.magenta)
            return Color.red;
        else if(lastColor == Color.red)
            return Color.green;
        else if(lastColor == Color.green)
            return Color.yellow;
        else if(lastColor == Color.yellow)
            return Color.orange;
        else if(lastColor == Color.orange)
            return Color.pink;
        else if(lastColor == Color.pink)
            return Color.lightGray;
        else if(lastColor == Color.lightGray)
            return Color.cyan;
        else
            return Color.magenta;
    }


    //--------------------------------------------------------------------------
    // toString

    /**
     * toString()
     */
    public String toString()
    {
        return _dateTime.toString() + ";  " + _dt;
    }
}