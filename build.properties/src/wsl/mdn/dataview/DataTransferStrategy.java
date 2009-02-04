package wsl.mdn.dataview;

// imports
import wsl.fw.datasource.DataSourceException;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c)
 * Company:
 * @author
 * @version 1.0
 */

public interface DataTransferStrategy
{
    /**
     * Execute the data transfer
     * @param dt the transfer to execute
     */
    public void executeTransfer(DataTransfer dt) throws DataSourceException;
}