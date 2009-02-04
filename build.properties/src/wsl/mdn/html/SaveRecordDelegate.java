/**	$Header: /wapsolutions/cvsroot/mdn/mdn/src/wsl/mdn/html/SaveRecordDelegate.java,v 1.1.1.1 2002/06/11 23:35:35 jonc Exp $
 *
 *  Save a record to d/b
 *
 */
package wsl.mdn.html;

// imports
import wsl.fw.servlet.ServletBase;
import wsl.fw.servlet.ServletError;
import wsl.fw.util.Util;
import wsl.fw.resource.ResId;
import wsl.fw.wml.*;
import wsl.mdn.dataview.*;
import wsl.mdn.wap.MdnWapDataCache;
import org.apache.ecs.wml.*;
import java.util.Vector;
import java.io.IOException;
import javax.servlet.ServletException;

public class SaveRecordDelegate extends MdnHtmlServletDelegate
{
    public final static ResId ERR_SAVE = new ResId("HtmlSaveRecordDelegate.error.save");

    public void
	run ()
		throws ServletException, IOException
    {
        try
        {
            // get the current record
            UserState us = getUserState();
            Record rec = us.getRecord(_request);
            Util.argCheckNull(us);
            Util.argCheckNull(rec);

            // get the group id from the current menu
            Object ogid = getUserState().getCurrentMenu().getGroupId();
            int groupId = Integer.parseInt(ogid.toString());

            // get the params
            // get the view
            DataView dv = (DataView)rec.getEntity();

            // iterate fields and set values, excluding thise fields that have
            // a field exclusion or are read-only
            Vector fields = dv.getFields();
            Vector rof    = dv.getReadOnlyFields();
            Vector excl   = getUserState().getCache().getFieldExclusions(groupId, dv.getId());
            for(int i = 0; fields != null && i < fields.size(); i++)
            {
                // get the field, musnt be excluded
            	DataViewField f = (DataViewField) fields.elementAt (i);
                if (f != null &&
					!MdnWapDataCache.isFieldExcluded (f, excl) &&
					!DataView.isFieldReadOnly (f, rof))
                {
                    // get the value
                    String value = _request.getParameter(MdnHtmlServlet.RP_FIELD_PREFIX
                        + String.valueOf(i));

                    // set the value into the rec
                    rec.setValue (f.getName (), value);
                }
            }

            // save the record
            rec.save();

            // get the record index
            String strIndex = _request.getParameter(MdnHtmlServlet.PV_RECORD_INDEX);

            // delegate back to show record
            delegate(new ShowRecordDelegate(rec, strIndex));
        }
        catch(Exception e)
        {
            onError(ERR_SAVE.getText(), e);
        }
    }
}