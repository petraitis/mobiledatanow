package wsl.fw.msgserver;

// imports
import java.util.Date;
import java.util.Calendar;
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

public class TaskDobj extends ItemDobj
{
    //--------------------------------------------------------------------------
    // constants

    public static final String FLD_TASK = "Task";
    public static final String FLD_PRIORITY = "Priority";
    public static final String FLD_STATUS = "Status";
    public static final String FLD_START = "Start";
    public static final String FLD_DUE = "Due";

    // task status
    public static final String[] _statii = {"Open", "In Progress", "Completed",
        "Waiting for Someone Else", "Deferred"};

    // task priority
    public static final String[] _priorities = {"Low", "Normal", "High"};


    //--------------------------------------------------------------------------
    // attributes
    protected Date _dueDate = null;


    //--------------------------------------------------------------------------
    // construction

    /**
     * Param ctor
     * @param a the Task object
     */
    public
	TaskDobj (
	 Task t)
    {
        // due date
        try
        {
            _dueDate = DateFormat.getDateTimeInstance().parse(t._dueDate);
        }
        catch(Exception e)
        {
		}

		setFieldValues (t);
    }


    /**
     * Create fields
     */
    protected void createFields()
    {
        // fields
        addField(FLD_TASK);
        addField(FLD_PRIORITY);
        addField(FLD_STATUS);
        addField(FLD_START);
        addField(FLD_DUE);
    }

	protected void
	setFieldValues (
	 Task t)
	{
        // status
        int status = 0;
        try
        {
            status = Integer.parseInt(t._status);
        }
        catch(Exception e2)
        {
		}

        // priority
        int priority = 0;
        try
        {
            priority = Integer.parseInt(t._priority);
        }
        catch(Exception e3)
        {
		}

        // fields
        setFieldValue(FLD_TASK, t._subject);
        setFieldValue(FLD_PRIORITY, _priorities[priority]);
        setFieldValue(FLD_STATUS, _statii[status]);
        setFieldValue(FLD_START, t._startDate);
        setFieldValue(FLD_DUE, t._dueDate);
	}

    //--------------------------------------------------------------------------
    // accessors
    /**
     * Returns the due date
     * @return Date
     */
    public Date getDueDate()
    {
        return _dueDate;
    }


    //--------------------------------------------------------------------------
    // to string

    public String toString()
    {
        return getFieldValue(FLD_TASK) + "; " + getFieldValue(FLD_STATUS);
    }
}