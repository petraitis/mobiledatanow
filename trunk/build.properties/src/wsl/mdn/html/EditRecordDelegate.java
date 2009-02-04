/**	$Header: /wapsolutions/cvsroot/mdn/mdn/src/wsl/mdn/html/EditRecordDelegate.java,v 1.1.1.1 2002/06/11 23:35:35 jonc Exp $
 *
 * Delegate used by MdnHtmlServlet to edit records.
 *
 */
package wsl.mdn.html;

import wsl.fw.datasource.Field;
import wsl.fw.servlet.ServletBase;
import wsl.fw.servlet.ServletError;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.resource.ResId;
import wsl.fw.html.WslHtmlTable;
import wsl.fw.html.WslHtmlUtil;
import wsl.mdn.dataview.*;
import wsl.mdn.wap.MdnWapDataCache;
import org.apache.ecs.html.*;
import org.apache.ecs.*;
import java.util.Vector;
import java.util.StringTokenizer;
import java.io.IOException;
import javax.servlet.ServletException;

public class EditRecordDelegate extends MdnHtmlServletDelegate
{
    //--------------------------------------------------------------------------
    // resources

    public static final ResId TEXT_SAVE_REC =
        new ResId("HtmlEditRecordDelegate.text.SaveRec");
    public static final ResId TEXT_EDIT_TITLE =
        new ResId("HtmlEditRecordDelegate.text.editTitle");
    public static final ResId TEXT_CANCEL =
        new ResId("HtmlEditRecordDelegate.text.Cancel");
    public static final ResId ERR_EDIT =
        new ResId("HtmlEditRecordDelegate.error.edit");

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
    public void run() throws IOException, ServletException
    {
        try
        {
            // validate
            Util.argCheckNull(_request);
            Util.argCheckNull(_response);

            // make the client
            MdnClientCell client = new MdnClientCell();
            client.setAlign(AlignType.CENTER);
            client.setClientTitle(TEXT_EDIT_TITLE.getText());
            client.setHelpUrl("/help/mdnhelp.html#bEditRecord");

            // form
            Form form = new Form();
            form.setMethod(Form.POST);
            form.setAction(makeHref());
            client.addElement(form);

            // table and heading
            WslHtmlTable table = new WslHtmlTable();
            form.addElement(table);
            TD cell = new TD(MdnHtmlServlet.getTitleElement(TEXT_EDIT_TITLE.getText()));
            cell.setColSpan(2);
            TR row = new TR(cell);
            table.addElement(row);

            // get the record
            if(_rec == null)
                _rec = getUserState().getRecord(_request);
            if(_strIndex == null)
                _strIndex = _request.getParameter(MdnHtmlServlet.PV_RECORD_INDEX);

            // build the save action element and record index
            form.addElement(new Input(Input.HIDDEN, MdnHtmlServlet.RP_ACTION,
                MdnHtmlServlet.ACT_SAVERECORD));
            form.addElement(new Input(Input.HIDDEN,
                MdnHtmlServlet.PV_RECORD_INDEX, _strIndex));

            // get the group id from the current menu
            Object ogid = getUserState().getCurrentMenu().getGroupId();
            int groupId = Integer.parseInt(ogid.toString());

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
                    // get the field, musnt be excluded
                    f = (DataViewField)fields.elementAt(i);
                    if(f != null && !(MdnWapDataCache.isFieldExcluded(f, excl)))
                    {
                        // get the display name
                        label = f.getDisplayName();
                        if(label != null && label.length() > 0)
                        {
                            val = _rec.getStringValue(f.getName());
                            val = (val == null) ? "" : WslHtmlUtil.esc(val);

                            // add input element to table
                            row = new TR();
                            table.addElement(row);

                            row.addElement(new TD(new B(WslHtmlUtil.esc(label))
                                + MdnHtmlServlet.TEXT_PROMPTCOLON.getText()));

							/*
							 *	Examine the field attributes to determine
							 *	the editable area.
							 */
                            if (DataView.isFieldReadOnly(f, rof))
							{
                            	/*
								 *	Field is readonly text
								 */
                                row.addElement (new TD (val));

							} else if (f.getOptionList () != null &&
									   f.getOptionList ().trim ().length () > 0)
							{
								/*
								 *	Field has an Option list associated
								 *	with it.
								 */
								String options = f.getOptionList ().trim ();
								StringTokenizer tok = new StringTokenizer (options, "\n");
								int count = tok.countTokens ();
								if (count > 0)
								{
									count++;
									String str[] = new String[count];
									str[0] = val;
									for(int j = 1; tok.hasMoreTokens(); j++)
										str[j] = tok.nextToken();
									Select sel = new Select(MdnHtmlServlet.RP_FIELD_PREFIX
										+ String.valueOf(i), str);
									row.addElement(new TD(sel));
								}

							} else if ((f.getFlags () & Field.FF_LARGE) != 0)
							{
								/*
								 *	Field is a "Large" field
								 *	Allow an edit using TextArea element
								 */
								TextArea edit = new TextArea (
													MdnHtmlServlet.RP_FIELD_PREFIX
														+ String.valueOf (i),
													10,			// area rows
													40);		// area cols
								edit.addElement (val);
								row.addElement (new TD(edit));

							} else
							{
								/*
								 *	Standard default Input field
								 */
								row.addElement (
									new TD (
										new Input (
											Input.TEXT,
											MdnHtmlServlet.RP_FIELD_PREFIX
												+ String.valueOf (i),
											val)));
                            }
                        }
                    }
                }

                // spacing
                cell = new TD();
                cell.setHeight(20);
                row = new TR(cell);
                table.addElement(row);

                // save button
                table.addElement(new TR(new TD(new Input(Input.SUBMIT,
                    "saveButton", WslHtmlUtil.esc(TEXT_SAVE_REC.getText())))));
            }
            else
            {
                table.addElement(new TR(new TD(WslHtmlUtil.esc(
                    MdnHtmlServlet.ERR_REC_NOT_FOUND.getText()))));
            }

            // send output
            outputClientCell(client);
        }
        catch(Exception e)
        {
            onError(ERR_EDIT.getText(), e);
        }
    }
}

//==============================================================================
// end of file EditRecordDelegate.java
//==============================================================================
