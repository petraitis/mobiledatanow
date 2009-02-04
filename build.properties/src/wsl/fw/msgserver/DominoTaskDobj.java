/*	$Header: /wapsolutions/cvsroot/mdn/fw/src/wsl/fw/msgserver/DominoTaskDobj.java,v 1.1.1.1 2002/06/11 23:11:42 jonc Exp $
 *
 *	Extensions for Domino
 */
package wsl.fw.msgserver;

public class DominoTaskDobj
	extends TaskDobj
{
    /*
	 *	Task status
	 */
    public static final String
		STATUS_UNKNOWN		= "Unknown",
		STATUS_OVERDUE		= "Overdue",
		STATUS_CURRENT		= "Current",
		STATUS_FUTURE		= "Future",
		STATUS_DONE			= "Done";

    /*
	 *	Task priorities
	 */
    public static final String
		PRIORITY_UNKNOWN	= "Unknown",
		PRIORITY_HIGH		= "High",
		PRIORITY_MEDIUM		= "Medium",
		PRIORITY_LOW		= "Low",
		PRIORITY_NONE		= "None";

    //--------------------------------------------------------------------------
    // construction

    /**
     * Param ctor
     * @param a the Task object
     */
    public
	DominoTaskDobj (
	 Task t)
    {
		super (t);
	}

	/**
	 *	Override superclasses priority/status strings
	 */
	protected void
	setFieldValues (
	 Task t)
	{
        setFieldValue (FLD_TASK, t._subject);
        setFieldValue (FLD_PRIORITY, getPriorityString (t._priority));
        setFieldValue (FLD_STATUS, getStatusString (t._status));
        setFieldValue (FLD_START, t._startDate);
        setFieldValue (FLD_DUE, t._dueDate);
    }

	private static String
	getPriorityString (
	 String raw)
	{
		if (raw.equals ("1"))
			return PRIORITY_HIGH;
		if (raw.equals ("2"))
			return PRIORITY_MEDIUM;
		if (raw.equals ("3"))
			return PRIORITY_LOW;
		if (raw.equals ("99"))
			return PRIORITY_NONE;
		return PRIORITY_UNKNOWN;
	}

	private static String
	getStatusString (
	 String raw)
	{
		if (raw.equals ("0"))
			return STATUS_OVERDUE;
		if (raw.equals ("1"))
			return STATUS_CURRENT;
		if (raw.equals ("2"))
			return STATUS_FUTURE;
		if (raw.equals ("9"))
			return STATUS_DONE;
		return STATUS_UNKNOWN;
	}
}