package wsl.mdn.wap;

// imports
import wsl.fw.servlet.ServletBase;
import wsl.fw.servlet.ServletError;
import wsl.fw.util.Util;
import wsl.fw.resource.ResId;
import wsl.fw.wml.*;
import wsl.mdn.dataview.*;
import org.apache.ecs.wml.*;
import java.util.Vector;
import java.io.IOException;
import javax.servlet.ServletException;

//------------------------------------------------------------------------------
/**
 *
 */
public class SaveRecordDelegate extends MdnWmlServletDelegate
{
    public final static ResId ERR_SAVE = new ResId("SaveRecordDelegate.error.save");
    /**
     * Output WML
     */
    public void run() throws ServletException, IOException
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
            DataViewField f;
            Vector fields = dv.getFields();
            Vector rof    = dv.getReadOnlyFields();
            Vector excl   = getUserState().getCache().getFieldExclusions(groupId, dv.getId());
            for(int i = 0; fields != null && i < fields.size(); i++)
            {
                // get the field, musnt be excluded
                f = (DataViewField)fields.elementAt(i);
                if(f != null && !MdnWapDataCache.isFieldExcluded(f, excl)
                    && !DataView.isFieldReadOnly(f, rof))
                {
                    // get the value
                    String value = _request.getParameter(MdnWmlServlet.RP_FIELD_PREFIX
                        + String.valueOf(i));

                    // set eth value into the rec
                    rec.setValue(f.getName(), value);
                }
            }

            // save the record
            rec.save();

            // get the record index
            String strIndex = _request.getParameter(MdnWmlServlet.PV_RECORD_INDEX);

            // delegate back to show record
            delegate(new ShowRecordDelegate(rec, strIndex));
        }
        catch(Exception e)
        {
            onError(ERR_SAVE.getText(), e);
        }
    }
}