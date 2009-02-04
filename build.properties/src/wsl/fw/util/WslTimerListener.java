package wsl.fw.util;

/**
 * Title:        Framework utility classes
 * Description:
 * Copyright:    Copyright (c) Jason Nigro
 * Company:      WAP Solutions Ltd
 * @author Jason Nigro
 * @version 1.0
 */

public interface WslTimerListener
{
    /**
     * Timer has gone off. This is the notification claa to listeners
     */
    public void onTimer(WslTimer timer);
}