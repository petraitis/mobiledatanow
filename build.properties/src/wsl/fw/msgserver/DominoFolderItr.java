/**	$Header: /wapsolutions/cvsroot/mdn/fw/src/wsl/fw/msgserver/DominoFolderItr.java,v 1.1.1.1 2002/06/11 23:11:42 jonc Exp $
 *
 * 	RecordIterator for Domino folders for Generic documents
 *
 */
package wsl.fw.msgserver;

import lotus.domino.*;
import wsl.fw.datasource.RecordItr;

public class DominoFolderItr
	extends RecordItr
{
	private static final String
		GEN_SUBJECT		= "Subject",
		GEN_BODY		= "Body";

	/*
	 *	Local state variables
	 */
	private Database _db;
	private ViewNavigator _nav;
	private boolean _skipped;
	private ViewEntry _curEntry;

	public
	DominoFolderItr ()
	{
		_db = null;
		_nav = null;
		_skipped = true;
		_curEntry = null;
	}

	public
	DominoFolderItr (
	 Database db,
	 View view)
	{
		_db = db;
		_skipped = false;
		try
		{
			_nav = view.createViewNav ();

		} catch (NotesException e)
		{
			_skipped = true;
			_curEntry = null;

			System.err.println ("Notes: " + e.getMessage ());
			e.printStackTrace ();
		}
	}

	/**
	 *	<P>Skip to the first Document</P>
	 *
	 * The reason we have it out here is 'cos we invoke isValidDocument().
	 * If we have this in the constructor, any checks that the subclasses
	 * want to make against their internal states with isValidDocument()
	 * will not be possible as they will not have been fully initialised yet.
	 */
	private void
	skipThru ()
	{
		if (_skipped)
			return;

		try
		{
			/*
			 * Skip to the first valid document
			 */
			_curEntry = _nav.getFirst ();
			while (_curEntry != null)
			{
				if (_curEntry.isDocument () && isValidDocument (currentDoc ()))
					break;
				_curEntry = _nav.getNext ();
			}

		} catch (NotesException e)
		{
			_curEntry = null;
		}
		_skipped = true;
	}

	public boolean
	hasNext ()
	{
		skipThru ();
		return _curEntry != null;
	}

	public Object
	next ()
	{
		skipThru ();
		if (_curEntry == null)
			return null;

		try
		{
			ItemDobj dobj = docToItemDobj (currentDoc ());

			/*
			 *	Skip to next valid document
			 */
			do
			{
				_curEntry = _nav.getNext ();
				if (_curEntry == null ||
					(	_curEntry.isDocument () &&
						isValidDocument (currentDoc ())))
				{
					break;
				}

			}	while (true);

			return dobj;

		} catch (NotesException e)
		{
			System.err.println ("DominoFolderItr: " + e.getMessage ());
			e.printStackTrace ();
		}
		return null;
	}

	public ItemDobj
	docToItemDobj (
	 Document doc)
	 	throws NotesException
	{
		return new GenericItemDobj (
				new Message (
					getDocValue (doc, GEN_SUBJECT),
					getDocValue (doc, GEN_BODY),
					"generic"));
	}

	public boolean
	isValidDocument (
	 Document doc)
	 	throws NotesException
	{
		return true;
	}

	/*
	 *	Helper function
	 */
	protected static String
	getDocValue (
	 Document doc,
	 String key)
	 	throws NotesException
	{
		Item item = doc.getFirstItem (key);
		if (item == null)
			return "";
		return item.getText ();
	}

	protected Document
	currentDoc ()
		throws NotesException
	{
		return _db.getDocumentByUNID (_curEntry.getUniversalID ());
	}
}