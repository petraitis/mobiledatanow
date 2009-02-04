/*	$Id: PagedIteratorDelegate.java,v 1.5 2003/02/10 22:57:27 tecris Exp $
 *
 *	Display pages of results from an Iterator
 *
 */
package wsl.mdn.wap;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Vector;
import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.ecs.wml.*;
import org.apache.ecs.*;
import wsl.fw.servlet.*;
import wsl.fw.util.Util;
import wsl.fw.resource.ResId;
import wsl.fw.wml.WEUtil;

public abstract class PagedIteratorDelegate
	extends MdnWmlServletDelegate
{
	/*
	 *	Resources
	 */
	public static final ResId
		TEXT_NO_RESULTS	= new ResId ("PagedSelectDelegate.text.NoResults"),
		TEXT_TITLE		= new ResId ("PagedSelectDelegate.text.title");

	/*
	 *	Constants
	 */
	public static final int DEFAULT_RECS_PER_PAGE = 5;
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
	protected String _title = TEXT_TITLE.getText ();

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
	 *
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
	 *	Removed an internally cached object
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
	 * build and send WML
	 */
	public void
	run ()
		throws ServletException, IOException
	{
		try
		{
			// make the card
			Card card = new Card ();
			card.setTitle (WEUtil.esc (_title));

			// add the start links
			addStartLinks (card);

			// get the next index
			int nextIndex = 0;
			String strNextIndex = _request.getParameter (PV_NEXTINDEX);
			if (strNextIndex != null)
				nextIndex = Integer.parseInt (strNextIndex);

			int recsShown = 0;

			// THE MAIN LINK
			String href = makeHref (MdnWmlServlet.ACT_MAINMENU);
			card.addElement (
				WEUtil.makeHrefP(
					href,
					WEUtil.esc (MdnWmlServlet.TEXT_MAIN.getText ())));

			if (nextIndex < _store.size ())
			{
				/*
				 *	Request for cached results
				 */
				int c;
				for (c = 0;
					 nextIndex + c < _store.size () && c < _recsPerPage;
					 c++)
				{
					card.addElement (getListElement (nextIndex + c));
				}
				nextIndex += c;
				recsShown = c;
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
					P p = new P (Alignment.LEFT, Mode.WRAP);
					p.addElement (
						WEUtil.esc (
							PagedSelectDelegate.TEXT_NO_RESULTS.getText ()));
					card.addElement (p);

				} else
				{
					/*
					 *	Let's suck up a page's worth
					 */
					int c;
					for (c = 0;
						 c + recsShown < _recsPerPage && _iter.hasNext ();
						 c++)
					{
						storeNextObj ();	// get & store next obj off iterator
						card.addElement (getListElement (nextIndex + c));
					}
					nextIndex += c;
				}
			}

			if (_iter.hasNext () || nextIndex < _store.size ())
			{
				/*
				 *	There's still some more stuff to see.
				 */
				href = makeHref (getMoreAction ());
				href = addParam (href,
								 PV_NEXTINDEX,
								 String.valueOf (nextIndex));
				href = addMoreParams (href);
				card.addElement (
					WEUtil.makeHrefP(
						href,
						WEUtil.esc (MdnWmlServlet.TEXT_MORE.getText ())));
			}

			// add links to the end of the card
			addEndLinks (card);

			/*
			 *	Add 3 bottom action buttons
			 *		- More/End
			 *		- Main
			 *		- Back
			 */
			// More do action
			String label = (_iter.hasNext () || nextIndex < _store.size ()) ?
							MdnWmlServlet.TEXT_MORE.getText ():
							MdnWmlServlet.TEXT_END.getText ();
			Do doOp = new Do (DoType.ACCEPT, WEUtil.esc (label));
			href = makeHref (getMoreAction ());
			href = addParam (href, PV_NEXTINDEX, String.valueOf(nextIndex));
			href = addParam (
					href, MdnWmlServlet.PV_MENUACTIONID,
					getUserState ().getCurrentMenu ().getId ().toString ());
			href = addMoreParams (href);
			Go go = new Go (href);
			doOp.addElement (go);
			card.addElement (doOp);

			// Main
			Do doMain = new Do (DoType.OPTIONS,
								MdnWmlServlet.TEXT_MAIN.getText ());
			Go goMain = new Go (makeHref (MdnWmlServlet.ACT_MAINMENU),
								Method.GET);
			doMain.addElement(goMain);
			card.addElement(doMain);

			// Back
			doOp = new Do (DoType.PREV, MdnWmlServlet.TEXT_BACK.getText ());
			doOp.addElement (new Prev ());
			card.addElement (doOp);

			// send output
			wmlOutput (card, true);

		} catch (Exception e)
		{
			onError (e);
		}
	}

	/**
	 * Get the card element for an entry in the Vector
	 */
	public abstract P
	getListElement  (
	 int index);

	/**
	 * Get the more action constant
	 */
	public abstract String getMoreAction();

	/**
	 * Add params to the more href
	 * @param the existing href
	 * @return the new href
	 */
	public String
	addMoreParams (
	 String href)
	{
		return addParam (href, MdnWmlServlet.PV_PAGEDITERATORID, getId ());
	}

	/**
	 * Add links to the start of the card
	 */
	public void addStartLinks(Card card)
	{
	}

	/**
	 * Add links to the end of the card
	 */
	public void addEndLinks(Card card)
	{
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
