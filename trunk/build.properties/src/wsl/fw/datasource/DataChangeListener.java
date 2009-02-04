
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
 * @author
 * @version 1.0
 */
package wsl.fw.datasource;

/**
 * Interface for all classes that wish to subscribe to DataObject change events.
 * Implementors should call addDataChangeListener(..) on DataManager to subscribe to this notification system.
 * Implementors will receive notification of insert, update, delete of DataObjects
 */
public interface DataChangeListener
{
    /**
     * Notification of DataObject change event
     * @param DataChangeNotification contains the data regarding data change
     */
    public void onDataChanged(DataChangeNotification notification);
}