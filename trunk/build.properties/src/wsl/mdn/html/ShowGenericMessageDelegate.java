/**	$Id: ShowGenericMessageDelegate.java,v 1.7 2002/12/23 20:53:08 tecris Exp $
 *
 *	Base code showing a Message
 *
 */
package wsl.mdn.html;

import java.util.Vector;

import org.apache.ecs.html.*;
import org.apache.ecs.*;

import wsl.fw.datasource.DataSource;
import wsl.fw.html.WslHtmlTable;
import wsl.fw.html.WslHtmlUtil;
import wsl.fw.msgserver.ItemDobj;
import wsl.fw.util.Util;

import java.io.IOException;
import javax.servlet.ServletException;
import wsl.fw.datasource.DataSourceException;

public class ShowGenericMessageDelegate
	extends MdnHtmlServletDelegate
{
	//--------------------------------------------------------------------------
	private static final String 
		SENDER			= "Sender",
		EMPTY_STRING 	= "";
	private boolean showReplyLink = true;
	// construction

	/**
	 * Default ctor
	 */
	public ShowGenericMessageDelegate()
	{
	}

	/**
	 * Called by servlet
	 */
	public void
	run ()
		throws ServletException, IOException
	{
		try
		{
			// validate
			Util.argCheckNull(_request);
			Util.argCheckNull(_response);

			// cell
			MdnClientCell client = new MdnClientCell();
			client.setAlign(AlignType.CENTER);
			client.setHelpUrl("/help/mdnhelp.html#bShowRecord");

			// table
			WslHtmlTable table = new WslHtmlTable();
			client.addElement(table);
			TD cell;
			TR row;

			// get the record and index
			String strIndex = _request.getParameter(MdnHtmlServlet.PV_RECORD_INDEX);
			if(strIndex != null && strIndex.length() > 0)
			{
				int index = Integer.parseInt(strIndex);

				// get the userState
				UserState userState = getUserState ();

				// get the message
				PagedItrMsgDelegate pmd = (PagedItrMsgDelegate)
										userState.getCurrentPagedItDelegate ();
				ItemDobj md = (ItemDobj) pmd.getMessage (index);
				Util.argCheckNull(md);

				// title
				String title = md.toString();
				client.setClientTitle(title);

				// heading
				cell = new TD(MdnHtmlServlet.getTitleElement(title));
				cell.setColSpan(2);
				row = new TR(cell);
				table.addElement(row);

				// iterate the contact fields
				Vector labels = md.getFieldLabels();
				for (int i = 0; i < labels.size (); i++)
				{
					// get the label and value
					String label = WslHtmlUtil.esc (
									Util.noNullStr (
										(String) labels.elementAt (i)));
					String value = WslHtmlUtil.esc (
									Util.noNullStr (
										md.getFieldValue (label)));

					if (label.equals (SENDER) && value.equals (EMPTY_STRING))
						showReplyLink = false;

					if (!value.equals (EMPTY_STRING))
					{
						// add new line before the label
						cell = new TD ();
						row = new TR (cell);
						table.addElement (row);

						// add label element
						B b = new B (label);
						cell = new TD (b);
						row = new TR (cell);
						table.addElement (row);

						// Reformat value to provide newline breaks
						cell = new TD (Util.strReplace (value, "\n", "<BR>"));
						row = new TR (cell);
						table.addElement (row);
					}
				}

				markMessageRead (md);
			}

			// add end links
			addEndLinks(table, strIndex,showReplyLink);

			// send output
			outputClientCell(client);

		} catch (Exception e)
		{
			onError (e);
		}
	}

	/**
	 * Add links to the end of the client cell
	 * @param table the outer table
	 * @param recIndex the record index
	 */
	protected void
	addEndLinks (
		WslHtmlTable table,
		String recIndex,
		boolean showReplyLink)
	{
	}

	/**
	 *	Allow subclasses to mark specific message types as read
	 */
	protected void
	markMessageRead (
	 ItemDobj item)
		throws DataSourceException
	{
	}
}
