/**	$Id: TitleCell.java,v 1.3 2002/07/10 23:56:24 jonc Exp $
 *
 */
package wsl.mdn.html;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ecs.*;
import org.apache.ecs.html.*;

import wsl.fw.html.WslHtmlTable;
import wsl.fw.html.WslHtmlUtil;
import wsl.fw.resource.ResId;
import wsl.mdn.server.MdnServer;

public class TitleCell extends MdnClientCell
{
	//--------------------------------------------------------------------------
	// constants

	public final static String DEFAULT_HELP_URL = "/help/mdnhelp.html";

	//--------------------------------------------------------------------------
	// resources

	public static final ResId
		TEXT_TITLE		= new ResId ("TitleCell.text.Title"),
		TEXT_MENU		= new ResId ("TitleCell.text.Menu"),
		TEXT_LOGOUT		= new ResId ("TitleCell.text.Logout"),
		TEXT_HELP		= new ResId ("TitleCell.text.Help");


	//--------------------------------------------------------------------------
	// attributes

	private A _helpLink;

	//--------------------------------------------------------------------------
	// construction

	/**
	 * Blank ctor
	 */
	public
	TitleCell (
	 HttpServletRequest request,
	 HttpServletResponse response)
	{
		// build the cell
		buildCell (request, response);
	}

	//--------------------------------------------------------------------------
	// cell building

	/**
	 * Build the cell
	 */
	private void
	buildCell (
	 HttpServletRequest request,
	 HttpServletResponse response)
	{
		// constants
		final int ROW_HEIGHT = 25;

		// cell properties
		this.setAlign (AlignType.CENTER);
		this.setHeight (50);

		// table
		WslHtmlTable table = new WslHtmlTable ();
		table.setWidth ("100%");
		table.setHeight ("100%");
		table.setCellPadding (5);
		table.setCellSpacing (0);
		//table.setBorder (1);
		this.addElement (table);

		// top row
		TD topCell = new TD ();
		topCell.setHeight (ROW_HEIGHT);
		topCell.setAlign (AlignType.LEFT);
		topCell.setColSpan (3);
		TR topRow = new TR (topCell);
		topRow.setBgColor (org.apache.ecs.HtmlColor.NAVY);
		table.addElement (topRow);

		Font font = new Font ("sans-serif", HtmlColor.WHITE, 4);
		String title = TEXT_TITLE.getText ();
		title += " - " + MdnHtmlServlet.TEXT_VERSION.getText () + " "
			+ MdnServer.VERSION_NUMBER;
		font.addElement (title);
		topCell.addElement (font);

		// bottom row
		TD bottomCell = new TD ();
		bottomCell.setHeight (ROW_HEIGHT);
		bottomCell.setAlign (AlignType.LEFT);
		TR bottomRow = new TR (bottomCell);
		bottomRow.setBgColor (org.apache.ecs.HtmlColor.silver);
		table.addElement (bottomRow);

		// menu link
		font = new Font ("sans-serif", HtmlColor.NAVY, 2);
		bottomCell.addElement (font);
		A link = new A (MdnHtmlServlet.makeHref (response,
			MdnHtmlServlet.ACT_SHOWMENU), TEXT_MENU.getText ());
		font.addElement (link);

		// logout link
		bottomCell = new TD ();
		bottomCell.setHeight (30);
		bottomCell.setAlign (AlignType.CENTER);
		bottomRow.addElement (bottomCell);
		font = new Font ("sans-serif", HtmlColor.NAVY, 2);
		bottomCell.addElement (font);
		link = new A (MdnHtmlServlet.makeHref (response,
			MdnHtmlServlet.ACT_LOGOUT), TEXT_LOGOUT.getText ());
		font.addElement (link);

		// help
		bottomCell = new TD ();
		bottomCell.setHeight (30);
		bottomCell.setAlign (AlignType.RIGHT);
		bottomRow.addElement (bottomCell);
		font = new Font ("sans-serif", HtmlColor.NAVY, 2);
		bottomCell.addElement (font);
		String url = request.getContextPath () + DEFAULT_HELP_URL;
		_helpLink = new A (WslHtmlUtil.esc (url), TEXT_HELP.getText ());
		font.addElement (_helpLink);
	}

	//--------------------------------------------------------------------------
	// accessors

	/**
	 * Set the help url
	 * @param url
	 */
	public void
	setHelpUrl (
	 String url)
	{
		_helpLink.setHref (url);
	}
}
