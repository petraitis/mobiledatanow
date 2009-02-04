/**	$Id: MainDocument.java,v 1.4 2002/07/17 23:01:59 jonc Exp $
 *
 */
package wsl.mdn.html;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ecs.*;
import org.apache.ecs.html.*;

import wsl.fw.html.WslHtmlTable;

public class MainDocument
	extends Document
{
	//--------------------------------------------------------------------------
	// attributes
	private TR _clientRow = new TR ();
	private MdnClientCell _clientCell;
	private TitleCell _titleCell;

	/**
	 * Ctor
	 */
	public
	MainDocument (
	 HttpServletRequest request,
	 HttpServletResponse response)
	{
		/*
		 *	Set language tags
		 */
		getHtml ().setLang (MdnHtmlServlet.getLanguage ());
		String contentType = MdnHtmlServlet.getContentType ();
		if (!contentType.equals (MdnHtmlServlet.HTML_MIME_TYPE))
		{
			Meta metaLang = new Meta ();
			metaLang.setContent (contentType);
			metaLang.setHttpEquiv ("Content-Type");

			getHead ().addElement (metaLang);
		}

		build (request, response);
	}

	/**
	 * Build the client table of the document
	 */
	private void
	build (
	 HttpServletRequest request,
	 HttpServletResponse response)
	{
		// table
		WslHtmlTable table = new WslHtmlTable ();
		table.setWidth ("100%");
		table.setHeight ("100%");
		BaseFont bf = new BaseFont ("sans-serif");
		getBody ().addElement (bf);
		appendBody (table);

		// title
		_titleCell = new TitleCell (request, response);
		TR titleRow = new TR (_titleCell);
		table.addElement (titleRow);

		// client row
		_clientRow = new TR ();
		table.addElement (_clientRow);
	}

	/**
	 * Set a client builder into the document
	 * @param builder
	 */
	public void
	setClientAreaCell (
	 HttpServletRequest request,
	 MdnClientCell clientCell)
		throws IOException, ServletException
	{
		// remove existing client area
		if (_clientCell != null)
		{
			_clientRow.removeElement (
				Integer.toString (_clientCell.hashCode ()));
		}

		// build and add new client area
		_clientCell = clientCell;
		_clientRow.addElement (Integer.toString (_clientCell.hashCode ()),
							   _clientCell);

		// set the help url into the title cell
		String helpUrl = _clientCell.getHelpUrl ();
		if (helpUrl != null && helpUrl.length () > 0)
		{
			helpUrl = request.getContextPath () + helpUrl;
			_titleCell.setHelpUrl (helpUrl);
		}
	}

	/**
	 * @return TD the current client cell
	 */
	public TD
	getClientAreaCell ()
	{
		return _clientCell;
	}
}
