package wsl.mdn.admin;

// imports
import java.util.Vector;
import java.util.Hashtable;
import pv.jfcx.JPVDate;
import pv.jfcx.JPVTime;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.resource.ResId;
import wsl.fw.gui.WslPvTableModel;
import wsl.fw.gui.WslPvTableView;
import wsl.fw.datasource.RecordSet;
import wsl.fw.datasource.DataObject;
import wsl.mdn.dataview.Record;
import wsl.mdn.dataview.DataViewField;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c)
 * Company:
 * @author
 * @version 1.0
 */

public class ExecDataTableModel extends WslPvTableModel
{
    //--------------------------------------------------------------------------
    // resources


    //--------------------------------------------------------------------------
    // constants

    //--------------------------------------------------------------------------
    // attributes

    private JPVDate _date = new JPVDate();
    private JPVTime _time = new JPVTime();
    private Vector _fields;

    //--------------------------------------------------------------------------
    // construction

    /**
     * Constructor taking a Recordset
     * @param rs
     */
    public ExecDataTableModel(Vector dobjs, String[] colTitles, Vector fields)
    {
        super(dobjs, colTitles);
        _fields = fields;
        _date.setUseLocale(true);
        _date.setShowCentury(true);
        _date.setAllowNull(true);
        _time.setShowSeconds(false);
        _time.setTwelveHours(false);
        _time.setAllowNull(true);
    }

    /**
     * @return the value at the param row and column
     */
    protected Object getValueAt(DataObject dobj, int col)
    {
        // validate
        Util.argCheckNull(dobj);

        // get the value based on column
        try
        {
            Record r = (Record)dobj;
            if (r != null)
            {
                String colName = getColumnName(col);
                Vector fields = r.getEntity().getFields();
                String fldName = ((DataViewField)fields.elementAt(col)).getName();
                Object val = r.getObjectValue(fldName);
                return (val == null)? new String(""): val;
            }
        }
        catch(Exception e)
        {
            Log.error("ExecDataTableModel.getValueAt: ", e);
        }

        // unknown column
        return new String("######");
    }

    /**
     * @return the width of the param column
     */
    protected int getColumnWidth(int col)
    {
        return 120;
    }
}