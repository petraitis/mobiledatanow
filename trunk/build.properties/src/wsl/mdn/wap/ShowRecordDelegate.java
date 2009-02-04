/**	$Id: ShowRecordDelegate.java,v 1.3 2002/09/25 02:46:41 jonc Exp $
 *
 *	Display a d/b query record
 *
 */
package wsl.mdn.wap;

// imports
import java.util.Vector;
import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.ecs.wml.*;
import wsl.fw.servlet.*;
import wsl.fw.util.Util;
import wsl.fw.resource.ResId;
import wsl.fw.wml.WEUtil;
import wsl.fw.datasource.*;
import wsl.mdn.dataview.*;
import wsl.mdn.guiconfig.LoginSettings;

public class ShowRecordDelegate
	extends MdnWmlServletDelegate
{
	//--------------------------------------------------------------------------
	// resources

	public static final ResId
		TEXT_TITLE = new ResId ("ShowRecordDelegate.text.title"),
		TEXT_EDIT_REC = new ResId ("ShowRecordDelegate.text.EditRec"),
		TEXT_DELETE_REC = new ResId ("ShowRecordDelegate.text.DeleteRec"),
		ERR_FIELD_EXCLUDED = new ResId ("ShowRecordDelegate.error.fieldExcluded");


	//--------------------------------------------------------------------------
	// attributes

	private Record _rec;
	private String _strIndex;

	//--------------------------------------------------------------------------
	// construction

	/**
	 * Default ctor
	 */
	public
	ShowRecordDelegate ()
	{
	}

	/**
	 * Ctor taking a record and an index
	 * @param rec the record to show
	 */
	public
	ShowRecordDelegate (
	 Record rec,
	 String strIndex)
	{
		_rec = rec;
		_strIndex = strIndex;
	}

	//--------------------------------------------------------------------------
	// wml

	/**
	 * Output wml
	 */
	public void
	run ()
		throws ServletException, IOException
	{
		try
		{
			// validate
			Util.argCheckNull (_request);
			Util.argCheckNull (_response);

			// make the card
			Card card = new Card ();
			card.setTitle (WEUtil.esc (TEXT_TITLE.getText ()));

			// get the group id from the current menu
			Object ogid = getUserState ().getCurrentMenu ().getGroupId ();
			int groupId = Integer.parseInt (ogid.toString ());

			// get the record
			P p = new P ();
			card.addElement (p);
			if (_rec == null)
			{
				_strIndex = _request.getParameter (MdnWmlServlet.PV_RECORD_INDEX);
				if (_strIndex != null && _strIndex.length () > 0)
				{
					int index = Integer.parseInt (_strIndex);

					// get the record
					PagedSelectDelegate psd = (PagedSelectDelegate)getUserState ().getCurrentPagedQuery ();
					_rec = psd.getRecord (index);

				} else
					p.addElement (WEUtil.esc (MdnWmlServlet.ERR_REC_NOT_FOUND.getText ()));
			}

			if (_rec != null)
			{
				// get the view
				DataView dv = (DataView)_rec.getEntity ();

				try
				{
					// iterate fields
					String label, val;
					DataViewField f;
					Vector fields = dv.getFields ();
					Vector excl   = getUserState ().getCache ().getFieldExclusions (groupId, dv.getId ());
					for (int i = 0; fields != null && i < fields.size (); i++)
					{
						// get the field, musnt be excluded
						f = (DataViewField)fields.elementAt (i);
						if (f != null && !MdnWapDataCache.isFieldExcluded (f, excl))
						{
							// get the display name
							label = f.getDisplayName ();
							if (label != null && label.length () > 0)
							{
								p.addElement (WEUtil.esc (label + ": "));

								val = WEUtil.esc (
										Util.noNullStr (_rec.getStringValue (f.getName ())));

								/*
								 *	If the field is a non-empty phonelink,
								 *	add the tag to enable dial on click
								 */
								if (val.trim ().length () > 0 &&
									(f.getFlags () & Field.FF_PHONELINK) != 0)
								{
									p.addElement (WEUtil.makePhoneLink (val, val));
								} else
								{
									p.addElement (WEUtil.esc (val));
								}

								p.addElement (new BR ());
							}
						}
					}

				} catch (Exception e)
				{
					getServlet ().onError (_request, _response,
						new ServletError (ERR_FIELD_EXCLUDED.getText (), e));
				}

				try
				{
					// get the group data view
					GroupDataView gdv = dv.getGroupDataView (groupId);

					// edit
					if (gdv != null /*&& gdv.getCanEdit () != 0 */)
					{
						String href = makeHref (MdnWmlServlet.ACT_EDITRECORD);
						href = addParam (href, MdnWmlServlet.PV_RECORD_INDEX, String.valueOf (_strIndex));
						card.addElement (WEUtil.makeHrefP (href,
							WEUtil.esc (TEXT_EDIT_REC.getText ())));
					}
					// delete
					if (gdv != null /*&& gdv.getCanDelete () != 0 */)
					{
						String href = makeHref (MdnWmlServlet.ACT_CONFIRMDELETE);
						href = addParam (href, MdnWmlServlet.PV_RECORD_INDEX, String.valueOf (_strIndex));
						card.addElement (WEUtil.makeHrefP (href,
							WEUtil.esc (TEXT_DELETE_REC.getText ())));
					}

				} catch (Exception e)
				{
					getServlet ().onError (_request, _response,
						new ServletError (ERR_FIELD_EXCLUDED.getText (), e));
				}
			} else
			{
				p.addElement (WEUtil.esc (MdnWmlServlet.ERR_REC_NOT_FOUND.getText ()));
			}

			// Main
			Do doMain = new Do (DoType.OPTIONS, MdnWmlServlet.TEXT_MAIN.getText ());
			Go goMain = new Go (makeHref (MdnWmlServlet.ACT_MAINMENU), Method.GET);
			doMain.addElement (goMain);
			card.addElement (doMain);

			// Back
			Do doOp = new Do (DoType.PREV, MdnWmlServlet.TEXT_BACK.getText ());
			doOp.addElement (new Prev ());
			card.addElement (doOp);

			// send output
			wmlOutput (card, true);

		} catch (Exception e)
		{
			onError (e);
		}
	}
}
