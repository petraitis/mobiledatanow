package wsl.mdn.dataview;

// imports
import java.util.Vector;
import java.util.Hashtable;
import wsl.fw.util.Util;
import wsl.fw.resource.ResId;
import wsl.fw.gui.WslProgressClient;
import wsl.fw.gui.WslProgressPanel;
import wsl.fw.gui.GuiManager;
import wsl.fw.datasource.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c)
 * Company:
 * @author
 * @version 1.0
 */

public class NoJoinTransferStrategy
    implements DataTransferStrategy, WslProgressClient
{
    //--------------------------------------------------------------------------
    // attributes

    private DataTransfer _dt;
    private int _recCount = 0;
    private boolean _isFinished = false;
    private String _strError = "";


    //--------------------------------------------------------------------------
    // constructor

    /**
     * Default ctor
     */
    public NoJoinTransferStrategy()
    {
    }

    /**
     * Ctor taking a DataTransfer. Used in multi-threading
     */
    public NoJoinTransferStrategy(DataTransfer dt)
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
        DataSource targetDs = MdnDataCache.getCache().getDataSource(dsDobj.getId(), true);

        // iterate the transfer tables
        TransferEntity te;
        Vector tes = dt.getTransferEntities();
        for(int i = 0; tes != null && i < tes.size(); i++)
        {
            // get the te
            te = (TransferEntity)tes.elementAt(i);
            if(te != null)
            {
                // transfer the table
                transferTable(te, sourceDs, targetDs);
            }
        }

        // transfer is finished
        _isFinished = true;
    }

    /**
     * Transfer data for a table
     * @param te the TransferEntity
     * @param sourceDs the source DataSource
     * @param targetDs the target DataSource
     */
    private void transferTable(TransferEntity te, DataSource sourceDs,
        DataSource targetDs) throws DataSourceException
    {
        // validate
        Util.argCheckNull(te);
        Util.argCheckNull(sourceDs);
        Util.argCheckNull(targetDs);

        // delete all records from the target
        targetDs.rawExecuteUpdate("DELETE FROM " +
            JdbcDataSource.escapeRawTableName(te.getSourceEntityName()));

        // build and execute the source query
        Query q = new Query(te.getSourceEntityName());
        RecordSet rs = sourceDs.select(q);

        // insert new records
        DataObject row;
        Record r;
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
                        transferRecord(r, targetDs);
                }
            }

            // else straight transfer
            else
                transferRecord((Record)row, targetDs);
        }
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

            // insert
            r.setState(DataObject.NEW);
            r.save(false);
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
            _strError = e.toString();
            _isFinished = true;
        }
    }

    /**
     * @return the panel title
     */
    public String getProgressTitle()
    {
        return DefaultTransferStrategy.TITLE_TRANSFER.getText();
    }

    /**
     * @return String a message to be displayed in the progress panel
     */
    public String getProgressMessage()
    {
        return DefaultTransferStrategy.LABEL_PROGRESS.getText() + " : " +
            String.valueOf(_recCount);
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
        return _strError;
    }
}