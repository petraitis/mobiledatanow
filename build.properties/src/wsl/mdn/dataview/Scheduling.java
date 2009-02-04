package wsl.mdn.dataview;

// imports
import java.sql.Timestamp;
import java.util.Date;
import wsl.fw.datasource.*;
import wsl.fw.resource.ResId;


/**
 * Title:
 * Description:
 * Copyright:    Copyright (c)
 * Company:
 * @author
 * @version 1.0
 */

public class Scheduling extends DataObject
{
    //--------------------------------------------------------------------------
    // constants

    public final static String ENT_SCHEDULING       = "TBL_SCHEDULING";
    public final static String FLD_ID               = "FLD_ID";
    public final static String FLD_DTID             = "FLD_DTID";
    public final static String FLD_STARTDATE        = "FLD_STARTDATE";
    public final static String FLD_ENDDATE          = "FLD_ENDDATE";
    public final static String FLD_REPEATTYPE       = "FLD_REPEATTYPE";
    public final static String FLD_REPEATCOUNT      = "FLD_REPEATCOUNT";


    // constants
    public static final int REPEATTYPE_NONE = 0;
    public static final int REPEATTYPE_HOUR = 1;
    public static final int REPEATTYPE_DAY = 2;
    public static final int REPEATTYPE_WEEK = 3;
    public static final int REPEATTYPE_MONTH = 4;

    // resources
    public static final ResId LABEL_NONE  = new ResId("Scheduling.none");
    public static final ResId LABEL_MONTHS  = new ResId("Scheduling.months");
    public static final ResId LABEL_DAYS  = new ResId("Scheduling.days");
    public static final ResId LABEL_HOURS  = new ResId("Scheduling.hours");
    public static final ResId LABEL_WEEKS  = new ResId("Scheduling.weeks");
    public static final ResId LABEL_EVERY  = new ResId("Scheduling.every");


    //--------------------------------------------------------------------------
    // attributes

    private transient DataTransfer _dt = null;


    //--------------------------------------------------------------------------
    // construction

    /**
     * Default ctor
     */
    public Scheduling()
    {
    }

    /**
     * @return String the name of the entity that defines this DataObject
     */
    public String getEntityName()
    {
        return ENT_SCHEDULING;
    }


    //--------------------------------------------------------------------------
    // persistence

    /**
     * Static factory method to create the entity to be used by this dataobject
     * and any subclasses. This is called by the DataManager's factory when
     * creating a TRANSFERENTITY entity.
     * @return the created entity.
     */
    public static Entity createEntity()
    {
        // create the entity
        Entity ent = new EntityImpl(ENT_SCHEDULING, Scheduling.class);

        // add the key generator for the system id
        ent.addKeyGeneratorData(new DefaultKeyGeneratorData(ENT_SCHEDULING, FLD_ID));

        // create the fields and add them to the entity
        ent.addField(new FieldImpl(FLD_ID, Field.FT_INTEGER, Field.FF_SYSTEM_KEY | Field.FF_UNIQUE_KEY));
        ent.addField(new FieldImpl(FLD_DTID, Field.FT_INTEGER));
        ent.addField(new FieldImpl(FLD_STARTDATE, Field.FT_DATETIME));
        ent.addField(new FieldImpl(FLD_ENDDATE, Field.FT_DATETIME));
        ent.addField(new FieldImpl(FLD_REPEATTYPE, Field.FT_INTEGER));
        ent.addField(new FieldImpl(FLD_REPEATCOUNT, Field.FT_INTEGER));

        // create the joins and add them to the entity
        ent.addJoin(new JoinImpl(ENT_SCHEDULING, FLD_DTID,
            DataTransfer.ENT_DATATRANSFER, DataTransfer.FLD_ID));

        // return the entity
        return ent;
    }


    //--------------------------------------------------------------------------
    // accessors


    /**
     * @return int the id of this Entity
     */
    public int getId()
    {
        return getIntValue(FLD_ID);
    }

    /**
     * Set the id of this Entity
     * @param id
     */
    public void setId(int id)
    {
        setValue(FLD_ID, id);
    }

    /**
     * Returns the dt id
     * @return int
     */
    public int getDataTransferId()
    {
        return getIntValue(FLD_DTID);
    }

    /**
     * Set the dt id
     * @param  val the value to set
     */
    public void setDataTransferId(int val)
    {
        setValue(FLD_DTID, val);
    }

    /**
     * Returns the start date
     * @return Date
     */
    public Date getStartDate()
    {
        return getDateValue(FLD_STARTDATE);
    }

    /**
     * Set the start date
     * @param val the value to set
     */
    public void setStartDate(Date val)
    {
        setValue(FLD_STARTDATE, val);
    }

    /**
     * Returns the end date
     * @return Date
     */
    public Date getEndDate()
    {
        return getDateValue(FLD_ENDDATE);
    }

    /**
     * Set the end date
     * @param val the value to set
     */
    public void setEndDate(Date val)
    {
        setValue(FLD_ENDDATE, val);
    }

    public int getRepeatType()
    {
        return getIntValue(FLD_REPEATTYPE);
    }

    /**
     * Set the Repeat Type
     * @param val the value to set
     */
    public void setRepeatType(int val)
    {
        setValue(FLD_REPEATTYPE, val);
    }

    /**
     * Returns the Repeat Count
     * @return int
     */
    public int getRepeatCount()
    {
        return getIntValue(FLD_REPEATCOUNT);
    }

    /**
     * Set the Repeat Count
     * @param val the value to set
     */
    public void setRepeatCount(int val)
    {
        setValue(FLD_REPEATCOUNT, val);
    }

    /**
     * Returns the string value of the Scheduling.
     * @return String the string value of the Scheduling.
     */
    public String toString()
    {
        String str = "";

        // get string representation of DataTransfer object
        DataSource ds = DataManager.getSystemDS();
        Query q = new Query(DataTransfer.ENT_DATATRANSFER);
        QueryCriterium qc = new QueryCriterium(DataTransfer.ENT_DATATRANSFER, FLD_ID,
            QueryCriterium.OP_EQUALS, new Integer(getDataTransferId()));
        q.addQueryCriterium(qc);
        RecordSet rs;
        try
        {
            rs = ds.select(q);
            if (rs.getRows().size() == 1)
            {
                rs.next();
                str += rs.getCurrentObject().toString();
            }
        }
        catch (Exception e)
        {
        }

        // add start date
        Timestamp startDate = new Timestamp(getStartDate().getTime());
        String sqlDateString = startDate.toString();
        sqlDateString = sqlDateString.substring(0, sqlDateString.indexOf("."));
        str += " " + sqlDateString;

        // add end date
        Timestamp endDate = new Timestamp(getEndDate().getTime());
        if(endDate != null)
        {
            sqlDateString = endDate.toString();
            sqlDateString = sqlDateString.substring(0, sqlDateString.indexOf("."));
            str += "-" + sqlDateString;
        }

        // add Repeat Count
        if(getRepeatType() != REPEATTYPE_NONE)
        {
            str += " " + LABEL_EVERY.getText() + " " + getRepeatCount() + " ";

            // add Repeat Type
            switch (getRepeatType())
            {
                case REPEATTYPE_MONTH:
                    str += LABEL_MONTHS.getText();
                    break;
                case REPEATTYPE_DAY:
                    str += LABEL_DAYS.getText();
                    break;
                case REPEATTYPE_HOUR:
                    str += LABEL_HOURS.getText();
                    break;
                case REPEATTYPE_WEEK:
                    str += LABEL_WEEKS.getText();
                    break;
            }
        }

        // return
        return (str.length() == 0)? super.toString(): str;
    }

    /**
     * Format and return a string describing the repeats
     */
    public String getRepeatString()
    {
        // add Repeat Count
        String ret = "";
        if(getRepeatType() != REPEATTYPE_NONE)
        {
            ret = LABEL_EVERY.getText() + " " + getRepeatCount() + " ";

            // add Repeat Type
            switch (getRepeatType())
            {
                case REPEATTYPE_MONTH:
                    ret += LABEL_MONTHS.getText();
                    break;
                case REPEATTYPE_DAY:
                    ret += LABEL_DAYS.getText();
                    break;
                case REPEATTYPE_HOUR:
                    ret += LABEL_HOURS.getText();
                    break;
                case REPEATTYPE_WEEK:
                    ret += LABEL_WEEKS.getText();
                    break;
            }
        }
        return ret;
    }


    //--------------------------------------------------------------------------
    // DataTransfer

    /**
     * Set the DataTransfer for this Scheduling
     * @param dt the DataTransfer to set
     */
    public void setDataTransfer(DataTransfer dt)
    {
        _dt = dt;
    }

    /**
     * @return the data transfer that this scheduling is for, will load if not loaded
     */
    public DataTransfer getDataTransfer() throws DataSourceException
    {
        // if null, load
        if(_dt == null)
        {
            _dt = new DataTransfer();
            _dt.setId(this.getDataTransferId());
            _dt = (DataTransfer)_dt.loadPolymorphic();
        }

        // return
        return _dt;
    }
}