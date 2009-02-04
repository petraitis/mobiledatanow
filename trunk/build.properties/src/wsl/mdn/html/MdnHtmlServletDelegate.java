/**	$Id: MdnHtmlServletDelegate.java,v 1.2 2002/06/25 03:58:57 jonc Exp $
 *
 * Base class for delegates used by WMLServlet.
 *
 */
package wsl.mdn.html;
import org.apache.ecs.html.*;
import wsl.fw.servlet.ServletDelegate;
import wsl.fw.servlet.ServletError;
import javax.servlet.ServletException;
import java.io.IOException;

public abstract class MdnHtmlServletDelegate
	extends ServletDelegate
{
	//--------------------------------------------------------------------------
	/**
	 * Default constructor.
	 */
	public MdnHtmlServletDelegate()
	{
	}

	//--------------------------------------------------------------------------
	/**
	 * This delegate is designed to be called by WMLServlet, this accessor
	 * casts _servlet to the expected type.
	 * @return the calling servlet cast to WMLServlet.
	 */
	protected MdnHtmlServlet getServlet()
	{
		return (MdnHtmlServlet) _servlet;
	}

	//--------------------------------------------------------------------------
	/**
	 * Get the WmlUserState object for this user form the session var.
	 * @return the WmlUserState from the session var or a new one
	 */
	protected UserState getUserState()
	{
		return getServlet().getUserState(_request);
	}

	//--------------------------------------------------------------------------
	/**
	 * Output wml to the servlet http output stream.
	 * @param cards, an ECS element (usualy a wml card) or an array or List
	 *   of ECS Elements. These will be added (in order) to a deck and output.
	 * @param disableCache, if true appropriate http anf wml headers will be
	 *   included to disable caching of the page.
	 */
	protected void wmlOutput(Object cards, boolean disableCache)
		throws ServletException
	{
		getServlet().wmlOutput(_response, cards, disableCache);
	}


	//--------------------------------------------------------------------------
	// Output HTML client

	/**
	 * @param client the client cell
	 */
	protected void outputClientCell(MdnClientCell client)
		throws ServletException, IOException
	{
		// delegate
		getServlet().outputClientCell(_request, _response, client);
	}

	//--------------------------------------------------------------------------
	/**
	 * Wrappers for the .makeHref functions that delegate to the servlet.
	 */
	public String makeHref()
	{
		return getServlet().makeHref(_response);
	}

	public String makeHref(String action)
	{
		return getServlet().makeHref(_response, action);
	}

	public String makeHref(String action, String subaction)
	{
		return getServlet().makeHref(_response, action, subaction);
	}

	public String addParam(String href, String varName, String value)
	{
		return getServlet().addParam(href, varName, value);
	}

	//--------------------------------------------------------------------------
	// build a generic exception page

	/**
	 * Build an error page from an Exception
	 * @param e the Exception
	 */
	public void onError(Exception e)
		throws IOException, ServletException
	{
		onError("", e);
	}

	/**
	 * Build an error page from an Exception
	 * @param error a String error msg
	 * @param e the Exception
	 */
	public void onError(String error, Exception e)
		throws IOException, ServletException
	{
		getServlet().onError(_request, _response,
			new ServletError(error, e));
	}

	/**
	 * Build an error page from a String error
	 * @param error a String error msg
	 */
	public void onError(String error)
		throws IOException, ServletException
	{
		getServlet().onError(_request, _response,
			new ServletError(error, null));
	}

	/**
	 *	Forward the current request to the current Iterator display.
	 *	Useful for mail directed actions, since this takes
	 *	us back to the current Mailbox
	 */
	protected void
	fwdToMailbox ()
		throws ServletException, IOException
	{
		/*
		 *	Set up the action:
		 *
		 *	We have to do in POST as well as GET to get around
		 *	possible confusion
		 */
		_request.setAttribute (
			MdnHtmlServlet.RP_ACTION,
			MdnHtmlServlet.ACT_NEXTPVPAGE);

		String fwdTo = "/servlet/"
						+ MdnHtmlServlet.HREF
						+ "?"
						+ MdnHtmlServlet.RP_ACTION
						+ "="
						+ MdnHtmlServlet.ACT_NEXTPVPAGE;

		/*
		 *	Forward
		 */
		_servlet.
			getServletContext ().
			getRequestDispatcher (fwdTo).
			forward (_request, _response);
	}
}
