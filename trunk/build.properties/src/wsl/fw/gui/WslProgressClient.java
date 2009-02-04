package wsl.fw.gui;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c)
 * Company:
 * @author
 * @version 1.0
 */

public interface WslProgressClient extends Runnable
{
    //--------------------------------------------------------------------------
    // operations

    /**
     * @return the panel title
     */
    public String getProgressTitle();

    /**
     * @return String a message to be displayed in the progress panel
     */
    public String getProgressMessage();

    /**
     * @return int the progress of the client as a percentage. ie 100% = 100
     */
    public int getProgressPercentage();

    /**
     * @return boolean true if the process is finished
     */
    public boolean isFinished();

    /**
     * @return String error string
     */
    public String getError();
}