/*	$Id: PagedIteratorDelegate.java,v 1.4 2002/07/24 03:38:55 jonc Exp $
 *
 *	Display pages of results from an Iterator
 *
 */
package wsl.mdn.html;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Vector;
import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.ecs.html.*;
import org.apache.ecs.*;
import wsl.fw.servlet.*;
import wsl.fw.util.Util;
import wsl.fw.resource.ResId;
import wsl.fw.html.*;
import wsl.fw.datasource.RecordSet;
import wsl.mdn.guiconfig.MenuAction;
import wsl.mdn.dataview.Record;
import wsl.mdn.guiconfig.LoginSettings;

public abstract class PagedIteratorDelegate
	extends MdnHtmlServletDelegate
{
	/*
	 *	Resources
	 */
	public static final ResId
		TEXT_NO_RESULTS	= new ResId ("HtmlPagedSelectDelegate.text.NoResults"),
		TEXT_TITLE		= new ResId ("HtmlPagedSelectDelegate.text.Title");

	/*
	 *	Constants
	 */
	public static final int DEFAULT_RECS_PER_PAGE = 20;
	public static final String PV_NEXTINDEX = "nextindex";

	/*
	 *	Unique number generator..
	 */
	private static DecimalFormat fmt = new DecimalFormat ("PIt0000000");
	private static long nextItrId = 0;

	//--------------------------------------------------------------------------
	// attributes
	private String _iterId;
	private Iterator _iter;
	private int _recsPerPage;

	private Vector _store;		// local cache for enumerated objects

	/**
	 *	Generate a String to uniquely identify an instance
	 */
	private synchronized String
	getNextId ()
	{
		return fmt.format (nextItrId++);
	}

	/**
	 * Ctor taking an Iterator
	 * @param e the Enuermation
	 */
	public
	PagedIteratorDelegate (
	 Iterator it)
	{
		this (it, DEFAULT_RECS_PER_PAGE);
	}

	/**
	 * Ctor taking an Iterator
	 * @param e the Enuermation
	 */
	public
	PagedIteratorDelegate(
	 Iterator it,
	 int recsPerPage)
	{
		_iterId = getNextId ();
		_iter = it;
		_recsPerPage = recsPerPage;

		_store = new Vector ();
	}

	/**
	 *	Return the Unique identifier for the Delegate
	 */
	public String
	getId ()
	{
		return _iterId;
	}

	/**
	 *	Advance the iterator and store the object locally
	 */
	private synchronized void
	storeNextObj ()
	{
		Object obj = _iter.next ();
		_store.add (obj);				// cache for possible reread
	}

	/**
	 *  Get an internally cached object
	 */
	public Object
	getObject (
	 int index)
	{
		return _store.elementAt (index);
	}

	/**
	 *	Remove an internally cached object.
	 */
	public Object
	removeObject (
	 int index)
	{
		return _store.remove (index);
	}

	/**
	 *	Return an iterator to inspect internal objects
	 */
	public Iterator
	iterator ()
	{
		return new DelegateIterator (this);
	}

	/**
	 * build and send HTML
	 */
	public void
	run ()
		throws ServletException, IOException
	{
		try
		{
			// cell
			MdnClientCell client = new MdnClientCell ();
			client.setAlign (AlignType.CENTER);
			client.setHelpUrl ("/help/mdnhelp.html#bQueryResults");

			// title
			String title = TEXT_TITLE.getText ();
			MenuAction ma = getUserState ().getCurrentMenu ();
			if (ma != null)
				title = ma.getName ();
			client.setClientTitle (title);

			// table
			WslHtmlTable table = new WslHtmlTable ();
			client.addElement (table);
			TD cell = new TD (MdnHtmlServlet.getTitleElement (title));
			TR row = new TR (cell);
			table.addElement (row);

			// get the next index
			int nextIndex = 0;
			String strNextIndex = _request.getParameter (PV_NEXTINDEX);
			if (strNextIndex != null)
				nextIndex = Integer.parseInt (strNextIndex);

			int recsShown = 0;

			if (nextIndex < _store.size ())
			{
				/*
				 *	Request for cached results
				 */
				int i;
				for (i = 0;
					 nextIndex + i < _store.size () && i < _recsPerPage;
					 i++)
				{
					cell = new TD (getObjAnchor (nextIndex + i));
					row = new TR (cell);
					table.addElement (row);
				}
				nextIndex += i;
				recsShown = i;

			}

			if (recsShown < _recsPerPage)
			{
				if (_iter == null ||
					(	!_iter.hasNext () &&
						recsShown == 0))
				{
					/*
					 *	Nothing to look at
					 */
					row = new TR (
							new TD (
								PagedSelectDelegate.TEXT_NO_RESULTS.getText()));
					table.addElement (row);

				} else
				{
					/*
					 *	Let's suck up a  page's worth of stuff
					 */
					int c;
					for (c = 0;
						 c + recsShown < _recsPerPage && _iter.hasNext ();
						 c++)
					{
						storeNextObj ();	// get & store next obj off iterator

						cell = new TD (getObjAnchor (c + nextIndex));
						row = new TR (cell);
						table.addElement (row);
					}
					nextIndex += c;
				}
			}

			if (_iter.hasNext () || nextIndex < _store.size ())
			{
				/*
				 *	There's still some more stuff to see.
				 */
				String href = makeHref (getMoreAction ());
				href = addParam (href, PV_NEXTINDEX, String.valueOf (nextIndex));
				href = addMoreParams (href);
				cell = new TD (
						new A (href, WslHtmlUtil.esc (MdnHtmlServlet.TEXT_MORE.getText())));
				row = new TR (cell);
				table.addElement (row);
			}

			// send output
			outputClientCell(client);

		} catch (Exception e)
		{
			onError (e);
		}
	}

	/**
	 * Get an Anchor link from an Object after subclasses have had a
	 * chance to massage it
	 */
	public abstract A
	getObjAnchor (int index);

	/**
	 * Get the <More> action constant
	 */
	public abstract String
	getMoreAction ();

	/**
	 * Add params to the <More> href
	 * @param the existing href
	 * @return the new href
	 */
	public String
	addMoreParams (
	 String href)
	{
		return addParam (href, MdnHtmlServlet.PV_PAGEDITERATORID, getId ());
	}

	/**
	 *	Define internal class to iterate over internal contents
	 */
	public class DelegateIterator
		implements Iterator
	{
		PagedIteratorDelegate _pid;
		int _curPoint;

		public
		DelegateIterator (
		 PagedIteratorDelegate pid)
		{
			_pid = pid;
			_curPoint = 0;
		}

		public boolean
		hasNext ()
		{
			return _curPoint < _pid._store.size () || _pid._iter.hasNext ();
		}

		public Object
		next ()
		{
			if (_curPoint < _pid._store.size ())
				return _pid.getObject (_curPoint++);

			if (_pid._iter.hasNext())
			{
				_pid.storeNextObj ();
				return _pid.getObject (_curPoint++);
			}

			return null;
		}

		public void
		remove ()
		{
			// do nothing
		}
	}
}
