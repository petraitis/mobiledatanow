//Source file: D:\dev\wsl\fw\datasource\RecordSet.java

package wsl.fw.datasource;

// imports
import java.util.Vector;
import java.io.Serializable;
import wsl.fw.util.Log;
import wsl.fw.util.Util;

/**
 * A recordset of multiple Rows of multiple DataObjects
 * Selected from a DataSource object as a snapshot
 * May be scrolled to yield Rows
 */
public class RecordSet implements Serializable
{
    /**
     * attributes
     */
    private Vector _rows = new Vector();
    private int _cursor = -1;
    private String _filter = "";

    /**
     * Scrolls the cursor to the next row in the RecordSet
     * @return boolean true if there is a valid row
     * @roseuid 3973D44F0224
     */
    public boolean next()
    {
        // iterate the cursor
        _cursor++;

        // filter
        if(_filter != null && _filter.length() > 0)
        {
            while(_cursor < _rows.size() &&
                !getCurrentObject().toString().toLowerCase().startsWith(_filter.toLowerCase()))
            {
                _cursor++;
            }
        }

        // return true if a valid row
        return (_cursor < _rows.size());
    }

    /**
     * Set the current row.
     * @param row, the 0 based index of the row which is to be made current.
     *   should be in the range 0-size().
     */
    public void setRow(int row)
    {
        _cursor = row;
    }

    /**
     * Reset the RecordSet.
     */
    public void reset()
    {
        _cursor = -1;
    }

    /**
     * Get the size (number of rows) of this RecordSet.
     * @return the number of rows.
     */
    public int size()
    {
        return _rows.size();
    }

    /**
     * Add a row to the row vector
     * @param row the row to add
     * @return void
     * @roseuid 3973D4E6009A
     */
    public void addRow(DataObject row)
    {
        // add the row
        _rows.add(row);
    }

    /**
     * @return DataObject
     * @roseuid 3973D52503A8
     */
    public DataObject getCurrentObject()
    {
        // return the row at the cursor position
        return (_cursor < _rows.size())? (DataObject)_rows.elementAt(_cursor): null;
    }

    /**
     * Return the DataObject of the param class in the current row
     * @param className the class name of the DataObject being sought
     * @return DataObject the requested DataObject or null
     * @roseuid 3973D54401E0
     */
    public DataObject getCurrentObject(String entityName)
    {
        // validate param
        if(entityName == null || entityName.length() == 0)
            return null;

        // get the current row
        DataObject ret = null;
        DataObject row = getCurrentObject();

        // find the DataObject of the correct class
        if(row != null)
        {
            // is it a Row
            if(row instanceof wsl.fw.datasource.Row)
            {
                // search the components
                Vector components = ((Row)row).getComponents();
                DataObject comp;
                for(int i = 0; ret == null && components != null && i < components.size(); i++)
                {
                    // if data object has correct class, set ret
                    comp = (DataObject)components.elementAt(i);
                    if(comp != null && comp.getEntityName().equalsIgnoreCase(entityName))
                        ret = comp;
                }
            }

            // if not a Row verify class
            else
            {
                if(row.getEntityName().equalsIgnoreCase(entityName))
                    ret = row;
            }
        }

        // return
        return ret;
    }

    /**
     * Return the rows Vector
     * @return Vector
     */
    public Vector getRows()
    {
        return _rows;
    }

    /**
     * Externally set the rows vector
     * @param newRows the new Vector to set
     */
    public void setRows(Vector newRows)
    {
        _rows = newRows;
    }


    /**
     * Sort the recordset ascending based on object to string
     */
    public void sortAlpha()
    {
        // sort the recordset
        Object obj;
        Vector newRows = new Vector();
        reset();
        while(next())
        {
            // get the obj
            obj = (Object)getCurrentObject();

            // add to the new vector
            Object temp;
            boolean isAdded = false;
            for(int i = 0; i < newRows.size(); i++)
            {
                // if the obj < the temp obj add it before the temp
                temp = (Object)newRows.elementAt(i);
                if(obj.toString().compareTo(temp.toString()) <= 0)
                {
                    newRows.insertElementAt(obj, i);
                    isAdded = true;
                    break;
                }
            }

            // if not added, add to end
            if(!isAdded)
                newRows.add(obj);
        }

        // set the new rows into the record set
        setRows(newRows);
        reset();
    }

    /**
     * Append a recordset to the end of this
     * @param rs the RecordSet to append
     */
    public void append(RecordSet rs)
    {
        // validate
        Util.argCheckNull(rs);

        // iterate and add
        DataObject dobj;
        rs.reset();
        while(rs.next())
            this.addRow(rs.getCurrentObject());
    }


    //--------------------------------------------------------------------------
    // filtering

    /**
     * Set a filter
     * @param filter
     */
    public void setFilter(String filter)
    {
        _filter = filter;
    }

    /**
     * Creates and returns a transient vector of filtered rows
     * @return filtered rows
     */
    public Vector getFilteredRows()
    {
        Vector ret = getRows();
        if(_filter != null && _filter.length() > 0)
        {
            ret = new Vector();
            reset();
            while(next())
                ret.add(getCurrentObject());
        }
        return ret;
    }
}
