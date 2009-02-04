package wsl.fw.msgserver;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c)
 * Company:
 * @author
 * @version 1.0
 */

public class Task extends Message
{
    //--------------------------------------------------------------------------
    // attributes

    public String _isComplete = "";
    public String _status = "";
    public String _startDate = "";
    public String _dueDate = "";
    public String _priority = "";


    //--------------------------------------------------------------------------
    // construction

    /**
     * Ctor
     */
    public
	Task (
	 String subject,
	 String text,
	 String type,
	 String isComplete,
	 String status,
	 String startDate,
	 String dueDate,
	 String priority)
    {
        // super
        super(subject, text, type);

        // attribs
        _isComplete = isComplete;
        _status = status;
        _startDate = startDate;
        _dueDate = dueDate;
        _priority = priority;
    }
}