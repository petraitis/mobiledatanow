/**	$Header: /wapsolutions/cvsroot/mdn/fw/src/wsl/fw/msgserver/DominoTaskFolderItr.java,v 1.1.1.1 2002/06/11 23:11:42 jonc Exp $
 *
 * 	Converts documents in the folder to TaskDobj
 *
 */
package wsl.fw.msgserver;

import lotus.domino.*;

public class DominoTaskFolderItr
	extends DominoFolderItr
{
	private static final String
		TASK_SUBJECT		= "Subject",
		TASK_BODY			= "Body",
		TASK_TYPE			= "TaskType",
		TASK_STATE			= "DueState",
		TASK_BEGDATE		= "StartDate",
		TASK_DUEDATE		= "DueDate",
		TASK_PRIORITY		= "Importance";

	public
	DominoTaskFolderItr (
	 Database db,
	 View v)
	{
		super (db, v);
	}

	public ItemDobj
	docToItemDobj (
	 Document doc)
	 	throws NotesException
	{
		Task task = new Task (
						getDocValue (doc, TASK_SUBJECT),
						getDocValue (doc, TASK_BODY),
						getDocValue (doc, TASK_TYPE),
						"",					// isComplete...
						getDocValue (doc, TASK_STATE),
						getDocValue (doc, TASK_BEGDATE),
						getDocValue (doc, TASK_DUEDATE),
						getDocValue (doc, TASK_PRIORITY));

		return new DominoTaskDobj (task);
	}
}
