/*	$Header: /wapsolutions/cvsroot/mdn/mdn/src/wsl/mdn/html/ShowRecordDelegate.java,v 1.1.1.1 2002/06/11 23:35:35 jonc Exp $
 *
 */
package wsl.mdn.html;

// imports
import java.util.Vector;
import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.ecs.html.*;
import org.apache.ecs.*;
import wsl.fw.servlet.*;
import wsl.fw.util.Util;
import wsl.fw.resource.ResId;
import wsl.fw.html.*;
import wsl.fw.datasource.*;
import wsl.mdn.dataview.*;
import wsl.mdn.guiconfig.MenuAction;
import wsl.mdn.guiconfig.LoginSettings;
import wsl.mdn.wap.MdnWapDataCache;

public class ShowRecordDelegate extends MdnHtmlServletDelegate
{
    //--------------------------------------------------------------------------
    // resources

    public static final ResId TEXT_EDIT_REC =
        new ResId("HtmlShowRecordDelegate.text.EditRec");
    public static final ResId TEXT_DELETE_REC =
        new ResId("HtmlShowRecordDelegate.text.DeleteRec");
    public static final ResId ERR_FIELD_EXCLUDED =
        new ResId("HtmlShowRecordDelegate.error.fieldExcluded");


    //--------------------------------------------------------------------------
    // attributes

    private Record _rec;
    private String _strIndex;

    //--------------------------------------------------------------------------
    // construction

    /**
     * Default ctor
     */
    public ShowRecordDelegate()
    {
    }

    /**
     * Ctor taking a record and an index
     * @param rec the record to show
     */
    public ShowRecordDelegate(Record rec, String strIndex)
    {
        _rec = rec;
        _strIndex = strIndex;
    }


    //--------------------------------------------------------------------------
    // wml

    /**
     * Output wml
     */
    public void run() throws ServletException, IOException
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

            // get the group id from the current menu
            Object ogid = getUserState().getCurrentMenu().getGroupId();
            int groupId = Integer.parseInt(ogid.toString());

            // get the record
            if(_rec == null)
            {
                _strIndex = _request.getParameter(MdnHtmlServlet.PV_RECORD_INDEX);
                if(_strIndex != null && _strIndex.length() > 0)
                {
                    int index = Integer.parseInt(_strIndex);

                    // get the record
                    PagedSelectDelegate psd = (PagedSelectDelegate)getUserState().getCurrentPagedQuery();
                    _rec = psd.getRecord(index);
                }
                else
                {
                    cell = new TD(WslHtmlUtil.esc(MdnHtmlServlet.ERR_REC_NOT_FOUND.getText()));
                    row = new TR(cell);
                    table.addElement(row);
                }
            }

            // build the record
            if(_rec != null)
            {
                // title
                String title = _rec.toString();
                client.setClientTitle(title);

                // heading
                cell = new TD(MdnHtmlServlet.getTitleElement(title));
                cell.setColSpan(2);
                row = new TR(cell);
                table.addElement(row);

                // get the view
                DataView dv = (DataView)_rec.getEntity();

                try
                {
                    // iterate fields
                    Vector fields = dv.getFields();
                    Vector excl   = getUserState().getCache().getFieldExclusions(groupId, dv.getId());
                    for(int i = 0; fields != null && i < fields.size(); i++)
                    {
                        // get the field, musnt be excluded
                    	DataViewField f = (DataViewField) fields.elementAt (i);
                        if (f == null || MdnWapDataCache.isFieldExcluded (f, excl))
							continue;

						// get the display name
						String label = f.getDisplayName ();
						if (label == null || label.length () == 0)
							continue;

						// HTML output for label
						B b = new B (WslHtmlUtil.esc (label));
						cell = new TD (b);
						row = new TR (cell);

						String val = Util.noNullStr (_rec.getStringValue (f.getName ()));
						val = WslHtmlUtil.esc (val);
						if ((f.getFlags () & Field.FF_LARGE) != 0)
						{
							/*
							 *	Field is a "Large" field
							 *	Display using a TextArea
							 */
							TextArea text = new TextArea (
												10,			// area rows
												40);		// area cols
							text.addElement (val);
							text.setReadOnly (true);
							row.addElement (new TD (text));

						} else
						{
							row.addElement (new TD (val));
						}
						table.addElement(row);
                    }
                }
                catch(Exception e)
                {
                    getServlet().onError(_request, _response,
                        new ServletError(ERR_FIELD_EXCLUDED.getText(), e));
                }

                // spacing row
                cell = new TD();
                cell.setHeight(10);
                row = new TR(cell);
                table.addElement(row);

                try
                {
                    // get the group data view
                    GroupDataView gdv = dv.getGroupDataView(groupId);

                    // edit
                    if(gdv != null /*&& gdv.getCanEdit() != 0 */)
                    {
                        String href = makeHref(MdnHtmlServlet.ACT_EDITRECORD);
                        href = addParam(href, MdnHtmlServlet.PV_RECORD_INDEX, String.valueOf(_strIndex));
                        A a = new A(href, WslHtmlUtil.esc(TEXT_EDIT_REC.getText()));
                        cell = new TD(a);
                        row = new TR(cell);
                        table.addElement(row);
                    }
                    // delete
                    if(gdv != null /* && gdv.getCanDelete() != 0 */)
                    {
                        String href = makeHref(MdnHtmlServlet.ACT_CONFIRMDELETE);
                        href = addParam(href, MdnHtmlServlet.PV_RECORD_INDEX, String.valueOf(_strIndex));
                        A a = new A(href, WslHtmlUtil.esc(TEXT_DELETE_REC.getText()));
                        cell = new TD(a);
                        row = new TR(cell);
                        table.addElement(row);
                    }
                }
                catch(Exception e)
                {
                    getServlet().onError(_request, _response,
                        new ServletError(ERR_FIELD_EXCLUDED.getText(), e));
                }
            }
            else
            {
                cell = new TD(WslHtmlUtil.esc(MdnHtmlServlet.ERR_REC_NOT_FOUND.getText()));
                row = new TR(cell);
                table.addElement(row);
            }

            // send output
            outputClientCell(client);
        }
        catch(Exception e)
        {
            onError(e);
        }
    }
}

//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
