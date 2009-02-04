/**	$Id: EditRecordDelegate.java,v 1.2 2002/06/24 02:30:15 jonc Exp $
 *
 * Delegate used by MdnWmlServlet to edit records.
 *
 */
package wsl.mdn.wap;

import java.util.Vector;
import java.util.StringTokenizer;

import org.apache.ecs.wml.*;

import wsl.fw.resource.ResId;
import wsl.fw.servlet.ServletBase;
import wsl.fw.util.Util;
import wsl.fw.wml.WslInput;
import wsl.fw.wml.WEUtil;
import wsl.mdn.dataview.Record;
import wsl.mdn.dataview.DataView;
import wsl.mdn.dataview.DataViewField;

import java.io.IOException;
import javax.servlet.ServletException;

public class EditRecordDelegate
	extends MdnWmlServletDelegate
{
    //--------------------------------------------------------------------------
    // resources

    public static final ResId
		TEXT_SAVE_REC	= new ResId ("EditRecordDelegate.text.SaveRec"),
    	TEXT_CANCEL		= new ResId ("EditRecordDelegate.text.Cancel"),
    	ERR_EDIT		= new ResId ("EditRecordDelegate.error.edit"),
    	TEXT_TITLE		= new ResId ("EditRecordDelegate.text.title");

    //--------------------------------------------------------------------------
    // attributes
    private Record _rec = null;
    private String _strIndex = null;

    //--------------------------------------------------------------------------
    // construction
    /**
     * Default ctor
     */
    public EditRecordDelegate()
    {
    }

    /**
     * Ctor taking a record and an index
     * @param rec the record to show
     */
    public EditRecordDelegate(Record rec, String strIndex)
    {
        _rec = rec;
        _strIndex = strIndex;
    }

    //--------------------------------------------------------------------------
    /**
     * Handle actions for the EditRecordDelegate.
     * @throws IOException, standard exception thrown by servlet methods.
     * @throws ServletException, standard exception thrown by servlet methods.
     */
    public void
	run ()
		throws IOException, ServletException
    {
        try
        {
            // validate
            Util.argCheckNull(_request);
            Util.argCheckNull(_response);

            // make the card
            Card card = new Card();
            card.setTitle(WEUtil.esc(TEXT_TITLE.getText()));

            // build the save go element
            Go goSave = new Go(makeHref(), Method.GET);
            goSave.addElement(new Postfield(ServletBase.RP_ACTION, MdnWmlServlet.ACT_SAVERECORD));

            // get the group id from the current menu
            Object ogid = getUserState().getCurrentMenu().getGroupId();
            int groupId = Integer.parseInt(ogid.toString());

            // get the record
            P p = new P();
            if(_rec == null)
                _rec = getUserState().getRecord(_request);
            if(_strIndex == null)
                _strIndex = _request.getParameter(MdnWmlServlet.PV_RECORD_INDEX);
            if(_rec != null)
            {
                // get the view
                DataView dv = (DataView)_rec.getEntity();

                // iterate fields
                String label, val;
                DataViewField f;
                Vector fields = dv.getFields();
                Vector rof    = dv.getReadOnlyFields();
                Vector excl   = getUserState().getCache().getFieldExclusions(groupId, dv.getId());
                for(int i = 0; fields != null && i < fields.size(); i++)
                {
                    // get the field, musnt be excluded or read-only
                    f = (DataViewField)fields.elementAt(i);
                    if(f != null
                        && !MdnWapDataCache.isFieldExcluded(f, excl)
                        && !DataView.isFieldReadOnly(f, rof))
                    {
                        // get the display name
                        label = f.getDisplayName();
                        if (label != null && label.length() > 0)
                        {
                            val = _rec.getStringValue(f.getName());
                            String varName = MdnWmlServlet.getRandomVarName();
							if (val != null)
                            	val = WEUtil.esc(val);

                            // add input element to card
                            card.addElement(p = new P());
                            p.addElement(WEUtil.esc(label));
                            p.addElement(MdnWmlServlet.TEXT_PROMPTCOLON.getText());

                            // option list
                            if(f.getOptionList() != null &&
                                f.getOptionList().trim().length() > 0)
                            {
                                String options = f.getOptionList().trim();
                                StringTokenizer tok = new StringTokenizer(options, "\n");
                                int count = tok.countTokens();
                                if(count > 0)
                                {
                                    Select sel = new Select ();
									sel.setName (varName);
									if (val != null)
										sel.setValue (val);
                                    p.addElement(sel);
                                    Option opt;
                                    for(int j = 1; tok.hasMoreTokens(); j++)
                                    {
                                        String str = tok.nextToken();
                                        opt = new Option(str, str);
                                        sel.addElement(opt);
                                    }
                                }
                            }

                            // input
                            else
                            {
                                p.addElement(new WslInput(varName, "", val, null, false));
                            }

                            // add postfield element to go
                            goSave.addElement(new Postfield(MdnWmlServlet.RP_FIELD_PREFIX
                                + String.valueOf(i), WEUtil.makeVar(varName)));
                        }
                    }
                }

                // add rec index to go save
                goSave.addElement(new Postfield(MdnWmlServlet.PV_RECORD_INDEX, _strIndex));

                // save anchor
				String saveText = WEUtil.esc (TEXT_SAVE_REC.getText ());
                Anchor a = new Anchor (saveText, goSave);
                a.addElement (saveText);
                p = new P ();
                p.addElement (a);
                card.addElement (p);

                // save do
                Do doSave = new Do(DoType.ACCEPT, TEXT_SAVE_REC.getText());
                doSave.addElement(goSave);
                card.addElement(doSave);

                // Main
                Do doMain = new Do(DoType.OPTIONS, MdnWmlServlet.TEXT_MAIN.getText());
                Go goMain = new Go(makeHref(MdnWmlServlet.ACT_MAINMENU), Method.GET);
                doMain.addElement(goMain);
                card.addElement(doMain);
            }
            else
            {
                p.addElement(WEUtil.esc(MdnWmlServlet.ERR_REC_NOT_FOUND.getText()));
                card.addElement(p);
            }

            // Back
            Do doOp = new Do(DoType.PREV, MdnWmlServlet.TEXT_BACK.getText());
            doOp.addElement(new Prev());
            card.addElement(doOp);

            // send output
            wmlOutput(card, true);
        }
        catch(Exception e)
        {
            onError(ERR_EDIT.getText(), e);
        }
    }
}
