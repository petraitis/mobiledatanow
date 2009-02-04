
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
 * @author
 * @version 1.0
 */
package wsl.fw.gui;

// imports
import java.util.Vector;
import java.awt.Color;
import javax.swing.table.*;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.datasource.DataChangeListener;
import wsl.fw.datasource.DataChangeNotification;
import wsl.fw.datasource.DataListenerData;
import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.DataManager;
import pv.jfcx.PVTableModel;
import pv.jfcx.JPVTableView;

import wsl.fw.resource.ResId;

/**
 * TableModel implementation to contain data for the JPVTable
 * Simply construct the model with a Vector of DataObject (getRows() from a RecordSet will give you this Vector).
 */
public abstract class WslPvTableModel implements DataChangeListener
{
    // resources
    public static final ResId TITLE_HIDDEN  = new ResId("WslPvTableModel.title.Hidden");

    /**
     * Default column width
     */
    public static final int DEFAULT_COLUMN_WIDTH = 100;

    /**
     * ProtoView Table model to hold the data for the JPVTable
     */
    private PVTableModel _pvTableModel;

    /**
     * WslPvTableView using this model
     */
    private WslPvTableView _tblView;

    /**
     * DataListenerData
     */
    private DataListenerData _dld;

    /**
     * String array of column titles
     */
    private String[] _colTitles;

    /**
     * Constructor
     * @param columnTitles a String array of column titles
     */
    public WslPvTableModel(ResId[] columnTitles)
    {
        this(null, columnTitles);
    }

    /**
     * Constructor
     * @param dobjs the Vector of DataObjects
     * @param columnTitles a ResId array of column titles
     */
    public WslPvTableModel(Vector dobjs, ResId[] columnTitles)
    {
        // set the titles
        //_colTitles = columnTitles;
        _colTitles = new String[columnTitles.length];
        for (int i = 0; i < columnTitles.length; i++)
            _colTitles[i] = columnTitles[i].getText();

        // init the table model
        initTableModel();

        // set the DataObjects
        if(dobjs != null)
            setDataObjects(dobjs);
    }

    /**
     * Constructor
     * @param dobjs the Vector of DataObjects
     * @param columnTitles a String array of column titles
     */
    public WslPvTableModel(Vector dobjs, String[] columnTitles)
    {
        // set the titles
        _colTitles = columnTitles;

        // init the table model
        initTableModel();

        // set the DataObjects
        if(dobjs != null)
            setDataObjects(dobjs);
    }

    /**
     * Set the tabel view into the model
     * @param tblView the WslPvTableView that uses this model
     */
    public void setTableView(WslPvTableView tblView)
    {
        _tblView = tblView;
    }

    /**
     * Set a vector of DataObjects into the model
     * @param dobjs the Vector of DataObjects
     */
    public void setDataObjects(Vector dobjs)
    {
        // build the model data
        buildModelFromData(dobjs);

        // setup the listener
        if(dobjs != null && dobjs.size() > 0)
        {
            // get the entity name of the first object
            DataObject dobj = (DataObject)dobjs.elementAt(0);
            String entityName = dobj.getEntityName();

            // set listener
            setDataChangeListener(entityName);
        }
    }

    /**
     * Sets a DataChangeListener
     */
    public void setDataChangeListener(String entityName)
    {
        // remove current listener
        if(_dld != null)
            DataManager.removeAllDataChangeListeners(this);

        // set the new listener
        _dld = new DataListenerData(this, entityName, null);
        DataManager.addDataChangeListener(_dld);
    }

    /**
     * Initialise the PVTableModel
     */
    private void initTableModel()
    {
        // create the column vector
        Vector titles = new Vector();
        for(int col = 0; col < getColumnCount(); col++)
            titles.add(getColumnName(col));

        // add the hidden object column
        titles.add(TITLE_HIDDEN.getText());

        // create the model
        _pvTableModel = new PVTableModel(titles, 0);
        _pvTableModel.setColumnCount(getColumnCount());
    }

    /**
     * Build the PVTabelModel from a Vector of DataObjects
     */
    private void buildModelFromData(Vector dobjs)
    {
        // clear the table model
        clear();

        // build the table model
        DataObject dobj;
        for(int i = 0, j = 0; dobjs != null && i < dobjs.size(); i++)
        {
            // get the data object
            dobj = (DataObject)dobjs.elementAt(i);

            // if it valid, add it
            if(isValidObject(dobj))
            {
                setRowData(dobj, j);
                j++;
            }
        }
    }

    /**
     * @return PVTableModel the table model
     */
    public PVTableModel getPvTableModel()
    {
        return _pvTableModel;
    }

    /**
     * Clear the pv model
     */
    public void clear()
    {
        while(_pvTableModel.getRowCount() > 0)
            _pvTableModel.removeRow(0);
    }

    /**
     * @return the name of the column at the param index
     * @param col the index of the column
     */
    public String getColumnName(int col)
    {
        return (col > _colTitles.length)? "": _colTitles[col];
    }

    /**
     * @return int the number of columns in the table
     */
    public int getColumnCount()
    {
        return _colTitles.length;
    }

    /**
     * @return int the index of the column with the param name, -1 if not found
     */
    public int getColumnIndex(String name)
    {
        // iterate columns
        for(int col = 0; col < getColumnCount(); col++)
        {
            // compare names
            if(getColumnName(col).equalsIgnoreCase(name))
                return col;
        }

        // not found
        return -1;
    }

    /**
     * @return the value at the param row and column
     */
    protected abstract Object getValueAt(DataObject dobj, int col);

    /**
     * @return the width of the param column
     */
    protected int getColumnWidth(int col)
    {
        return DEFAULT_COLUMN_WIDTH;
    }

    /**
     * Set visual properties into the table
     */
    public void configureTable(JPVTableView table)
    {
        // validate
        Util.argCheckNull(table);

        // iterate the column indices
        TableColumn tc;
        for(int col = 0; col < table.getTable().getColumnCount(); col++)
        {
            // get the TableColumn
            tc = table.getColumn(col);

            // set the column properties
            tc.setPreferredWidth(getColumnWidth(col));
        }
    }

    /**
     * Return the name of the column at the param index
     * @param col the index of the column
     */
    private int getRowCount()
    {
        return (_pvTableModel == null)? 0: _pvTableModel.getRowCount();
    }

    /**
     * @return DataObject the DataObject at the specified row
     */
    public DataObject getDataObjectAt(int row)
    {
        return (_pvTableModel == null && _pvTableModel.getRowCount() <= row)? null:
            (DataObject)_pvTableModel.getCell(row, _pvTableModel.getColumnCount());
    }

    /**
     * @return int the row containing the param DataObject, if not found returns -1
     * @param dobj the DataObject to find the row of
     */
    public int getRowAt(DataObject dobj)
    {
        // iterate the rows
        DataObject dobjTemp;
        for(int i = 0; i < getRowCount(); i++)
        {
            // get the data object and compare
            dobjTemp = getDataObjectAt(i);
            if(dobjTemp.equals(dobj))
                return i;
        }
        return -1;
    }

    /**
     * Directs model to remove all its DataChangeListener subscriptions from the DataManager
     */
    public void removeAllDataChangeListeners()
    {
        DataManager.removeAllDataChangeListeners(this);
    }

    /**
     * Notification of DataObject change event
     * @param DataChangeNotification contains the data regarding data change
     */
    public synchronized void onDataChanged(DataChangeNotification not)
    {
        // switch on change type
        DataObject dobj = not.getDataObject();
        switch(not.getChangeType())
        {
            case DataChangeNotification.INSERT: addDataObject(dobj); break;
            case DataChangeNotification.UPDATE: updateDataObject(dobj, not.getDataObjectKey()); break;
            case DataChangeNotification.DELETE: removeDataObject(dobj); break;
        }

        // reselect the object int the table
        if(not.getChangeType() == DataChangeNotification.UPDATE)
        {
            int row = getRowAt(dobj);
            _tblView.getTable().getSelectionModel().setSelectionInterval(row, row);
        }
    }

    /**
     * @return boolean false if object is not valid for this table, used to ignore invalid new objects
     * and remove updated objects that are no longer valid
     * @param dobj DataObject to check
     */
    protected boolean isValidObject(DataObject dobj)
    {
        return true;
    }

    /**
     * Add a DataObject to the model
     * @param dobj the DataObject to add
     */
    private void addDataObject(DataObject dobj)
    {
        // validate
        Util.argCheckNull(dobj);

        // check validity
        if(isValidObject(dobj))
        {
            // update the model
            setRowData(dobj, _pvTableModel.getRowCount());
            configureTable(_tblView);
            _pvTableModel.fireTableDataChanged();
        }
    }

    /**
     * Update the model from a changed DataObject
     * @param newDobj the DataObject that has changed
     */
    private void updateDataObject(DataObject newDobj, String oldKey)
    {
        // validate
        Util.argCheckNull(newDobj);

        // find the old data object
        boolean found = false;
        DataObject oldDobj;
        for(int row = 0; !found && row < _pvTableModel.getRowCount(); row++)
        {
            // get the data object and compare
            oldDobj = (DataObject)_pvTableModel.getCell(row, _pvTableModel.getColumnCount());
            if(oldDobj != null && oldDobj.equals(newDobj))
            {
                // set the row data into the model
                if(isValidObject(newDobj))
                    setRowData(newDobj, row);

                // if object is no longer valid remove it
                else
                    _pvTableModel.removeRow(row);
                found = true;
            }
        }
        // fire changed event
        if(found)
            _pvTableModel.fireTableDataChanged();
    }

    /**
     * Remove a DataObject from the model
     * @param remDobj the DataObject to remove
     */
    public void removeDataObject(DataObject remDobj)
    {
        // validate
        Util.argCheckNull(remDobj);

        // find the data object
        DataObject dobj;
        Object rowKey;
        for(int row = 0; row < _pvTableModel.getRowCount(); row++)
        {
            // get the data object and compare
            dobj = (DataObject)_pvTableModel.getCell(row, _pvTableModel.getColumnCount());
            if(dobj != null)
            {
                // if the object is deleted remove it
                if(dobj.getState() == DataObject.DELETED || dobj.equals(remDobj))
                    _pvTableModel.removeRow(row);
            }
        }
        _pvTableModel.fireTableDataChanged();
    }

    /**
     * Set model row data for a DataObject
     * @param dobj the DataObject to set row data for
     * @param row the row to set data into
     */
    private void setRowData(DataObject dobj, int row)
    {
        // iterate the columns
        Vector pvRow = new Vector();
        for(int col = 0; col < getColumnCount(); col++)
        {
            // get the value and add to the row
            Object value = getValueAt(dobj, col);
            pvRow.addElement(value);
        }

        // add the data object
        pvRow.addElement(dobj);

        // set the new row and break
        _pvTableModel.setRowData(pvRow, row);
    }
}