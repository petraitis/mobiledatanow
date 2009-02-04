package wsl.mdn.dataview;

// imports
import java.util.Vector;
import java.util.Hashtable;
import wsl.fw.util.Util;
import wsl.fw.resource.ResId;
import wsl.fw.gui.WslProgressClient;
import wsl.fw.gui.WslProgressPanel;
import wsl.fw.datasource.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c)
 * Company:
 * @author
 * @version 1.0
 */

public class DefaultTransferStrategy
    implements DataTransferStrategy, WslProgressClient
{
    //--------------------------------------------------------------------------
    // resources

    public static final ResId TITLE_TRANSFER  =
        new ResId("DefaultTransferStrategy.Title");
    public static final ResId LABEL_PROGRESS  =
        new ResId("DefaultTransferStrategy.label.Progress");


    //--------------------------------------------------------------------------
    // attributes

    private Hashtable _lastRecs = new Hashtable(89);
    private DataTransfer _dt = null;
    private int _recCount = 0;
    private boolean _isFinished = false;


    //--------------------------------------------------------------------------
    // constructor

    /**
     * Default ctor
     */
    public DefaultTransferStrategy()
    {
    }

    /**
     * Ctor taking a DataTransfer. Used in multi-threading
     */
    public DefaultTransferStrategy(DataTransfer dt)
    {
        _dt = dt;
    }


    //--------------------------------------------------------------------------
    // execution

    /**
     * Execute the data transfer
     * @param dt the transfer to execute
     */
    public void executeTransfer(DataTransfer dt) throws DataSourceException
    {
        // get the source ds dobj
        DataSourceDobj dsDobj = dt.getSourceDataSource();

        // must be mirrored
        if(!dsDobj.isMirrored())
            throw new RuntimeException("Data Source must be mirrored to transfer");

        // get the source ds
        DataSource sourceDs = dsDobj.createImpl();

        // get the mirror target
        // getting it from the pool should load it with the source entities and joins
        DataSource mirrorDs = MdnDataCache.getCache().getDataSource(dsDobj.getId());

        // build the query
        Query q = new Query();
        TransferEntity te;
        Vector tes = dt.getTransferEntities();
        for(int i = 0; tes != null && i < tes.size(); i++)
        {
            // get the te
            te = (TransferEntity)tes.elementAt(i);
            if(te != null)
            {
                // add to the query
                q.addQueryEntity(te.getSourceEntityName());
            }
        }

        // add the transfer filter
        QueryDobj filter = dt.getFilterQuery();
        Vector criteria = filter.getCriteria(null);
        QueryCriterium qc;
        for(int i = 0; criteria != null && i < criteria.size(); i++)
        {
            // get the qc and add
            qc = (QueryCriterium)criteria.elementAt(i);
            if(qc != null)
                q.addQueryCriterium(qc);
        }

        // select records from the source
        DataObject row;
        Record r;
        RecordSet rs = sourceDs.select(q);
        _recCount = 0;
        while(rs != null && rs.next())
        {
            // update the record count
            _recCount++;

            // get the row
            row = rs.getCurrentObject();

            // if a row, iterate
            if(row instanceof Row)
            {
                Vector comps = ((Row)row).getComponents();
                for(int i = 0; comps != null && i < comps.size(); i++)
                {
                    // get the record and transfer
                    r = (Record)comps.elementAt(i);
                    if(r != null)
                        transferRecord(r, mirrorDs);
                }
            }

            // else straight transfer
            else
                transferRecord((Record)row, mirrorDs);
        }

        // clear the hash
        _lastRecs.clear();

        // set finished
        _isFinished = true;
    }

    /**
     * Transfer a record to the source
     */
    private void transferRecord(Record r, DataSource targetDs) throws DataSourceException
    {
        // get the target entity
        String entityName = r.getEntity().getName();
        Entity entTarget = targetDs.getEntity(entityName);
        if(entTarget != null)
        {
            // set the new entity into the record
            r.setEntity(entTarget);

            // get the last rec for this entity
            Record lastRec = (Record)_lastRecs.get(entityName);
            String lastKey = (lastRec != null)? lastRec.getUniqueKey(false): null;

            // get the unique key for the current rec
            String thisKey = r.getUniqueKey(false);

            // compare keys
            if(lastKey != null && thisKey != null && lastKey.equalsIgnoreCase(thisKey))
            {
                // we have the same record
                // if values have change update
                lastRec.copyValues(r);
                lastRec.save(false);
            }

            // not the same record, load from db
            else
            {
                // clone the object
                Record rClone = (Record)r.clone();

                // load the clone
                if(rClone.load())
                {
                    // found in db
                    // merge and update
                    rClone.copyValues(r);
                    rClone.save(false);
                }

                // not found, insert original
                else
                {
                    // insert
                    r.setState(DataObject.NEW);
                    r.save(false);
                }
            }

            // set last rec
            _lastRecs.put(entityName, r);
        }
    }


    //--------------------------------------------------------------------------
    // Progress client

    /**
     * Runnable interface
     */
    public void run()
    {
        // must have a dt
        Util.argCheckNull(_dt);

        // execute transfer
        try
        {
            executeTransfer(_dt);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e.toString());
        }
    }

    /**
     * @return the panel title
     */
    public String getProgressTitle()
    {
        return TITLE_TRANSFER.getText();
    }

    /**
     * @return String a message to be displayed in the progress panel
     */
    public String getProgressMessage()
    {
        return LABEL_PROGRESS.getText() + " : " + String.valueOf(_recCount);
    }

    /**
     * @return int the progress of the client as a percentage. ie 100% = 100
     */
    public int getProgressPercentage()
    {
        return WslProgressPanel.UNKNOWN_PROGRESS;
    }

    /**
     * @return boolean true if the process is finished
     */
    public boolean isFinished()
    {
        return _isFinished;
    }

    /**
     * @return String error string
     */
    public String getError()
    {
        return "";
    }
}