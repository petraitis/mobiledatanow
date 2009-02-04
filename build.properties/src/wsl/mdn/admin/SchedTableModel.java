package wsl.mdn.admin;

// imports
import java.util.Vector;
import pv.jfcx.JPVDate;
import pv.jfcx.JPVTime;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.resource.ResId;
import wsl.fw.gui.WslPvTableModel;
import wsl.fw.datasource.RecordSet;
import wsl.fw.datasource.DataObject;
import wsl.mdn.dataview.Scheduling;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c)
 * Company:
 * @author
 * @version 1.0
 */

public class SchedTableModel extends WslPvTableModel
{
    //--------------------------------------------------------------------------
    // resources

    public static final ResId COL_TRANSFER  = new ResId("SchedTableModel.col.Transfer");
    public static final ResId COL_STARTDATE  = new ResId("SchedTableModel.col.StartDate");
    public static final ResId COL_STARTTIME  = new ResId("SchedTableModel.col.StartTime");
    public static final ResId COL_ENDDATE  = new ResId("SchedTableModel.col.EndDate");
    public static final ResId COL_ENDTIME  = new ResId("SchedTableModel.col.EndTime");
    public static final ResId COL_REPEAT  = new ResId("SchedTableModel.col.Repeat");


    //--------------------------------------------------------------------------
    // constants

    /**
     * Static string array of column titles
     */
    private static final ResId[] _colTitles = { COL_TRANSFER, COL_STARTDATE,
            COL_STARTTIME, COL_REPEAT, COL_ENDDATE, COL_ENDTIME };


    //--------------------------------------------------------------------------
    // attributes

    private JPVDate _date = new JPVDate();
    private JPVTime _time = new JPVTime();



    //--------------------------------------------------------------------------
    // construction

    /**
     * Blank constructor
     */
    public SchedTableModel()
    {
        this(null);
    }

    /**
     * Constructor taking a Vector of schedulings
     * @param v the Vector of schedulings
     */
    public SchedTableModel(Vector v)
    {
        super(v, _colTitles);
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
            Scheduling s = (Scheduling)dobj;
            ResId colName = _colTitles[col];
            if(colName.equals(COL_TRANSFER))
                return s.getDataTransfer().getName();
            else if(colName.equals(COL_STARTDATE))
            {
                _date.setDate(s.getStartDate());
                return _date.getText();
            }
            else if(colName.equals(COL_STARTTIME))
            {
                _time.setTime(s.getStartDate());
                return _time.getText();
            }
            else if(colName.equals(COL_ENDDATE))
            {
                _date.setDate(s.getEndDate());
                return _date.getText();
            }
            else if(colName.equals(COL_ENDTIME))
            {
                _time.setTime(s.getEndDate());
                return _time.getText();
            }
            else if(colName.equals(COL_REPEAT))
                return s.getRepeatString();
        }
        catch(Exception e)
        {
            Log.error("SchedTableModel.getValueAt:", e);
        }

        // unknown column
        return "######";
    }

    /**
     * @return the width of the param column
     */
    protected int getColumnWidth(int col)
    {
        // get the value based on column
        ResId colName = _colTitles[col];
        if(colName.equals(COL_TRANSFER))
            return 150;
        else if(colName.equals(COL_STARTDATE))
            return 80;
        else if(colName.equals(COL_STARTTIME))
            return 80;
        else if(colName.equals(COL_ENDDATE))
            return 80;
        else if(colName.equals(COL_ENDTIME))
            return 80;
        else if(colName.equals(COL_REPEAT))
            return 100;
        else
            return 0;
    }
}