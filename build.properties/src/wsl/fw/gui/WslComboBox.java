
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
import java.awt.Dimension;
import javax.swing.JComboBox;
import wsl.fw.util.Type;
import wsl.fw.util.Util;
import wsl.fw.datasource.RecordSet;
import wsl.fw.datasource.DataManager;
import wsl.fw.datasource.DataSource;
import wsl.fw.datasource.DataSourceException;
import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.Query;

/**
 * Wsl framework subclass of JComboBox. Adds constructors to build combo using the DataSource framework
 */
public class WslComboBox extends JComboBox
{
    /**
     * Preferred width
     */
    public static final int DEFAULT_WIDTH = 100;

    /**
     * Preferred height
     */
    public static final int DEFAULT_HEIGHT = 20;

    /**
     * Dimension of the combo box
     */
    private Dimension _dimension = new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);

    /**
     * Blank ctor
     */
    public WslComboBox()
    {
    }

    /**
     * Constructor taking a width for the combo
     * @param width the width of the combo
     */
    public WslComboBox(int width)
    {
        _dimension.width = width;
        updateSize();
    }

    /**
     * Updates the size properties of the control based on preferred size
     */
    private void updateSize()
    {
        setSize(getPreferredSize());
        setMinimumSize(getPreferredSize());
        setMaximumSize(getPreferredSize());
    }

    /**
     * Builds a combo from an entity name
     * @param entityName the entity used to build the combo
     */
    public WslComboBox(String entityName)
    {
        // select from data source
        DataSource ds = DataManager.getDataSource(entityName);
        Util.argCheckNull(ds);
        try
        {
            RecordSet rs = ds.select(new Query(entityName));
            // delegate to recordset ctor
            if(rs != null)
                buildFromRecordSet(rs);
        }
        catch(DataSourceException e)
        {
            throw new RuntimeException(e.toString());
        }
    }

    /**
     * Builds a combo from a RecordSet
     * @param rs the RecordSet used to build the combo
     */
    public WslComboBox(RecordSet rs)
    {
        // delegate
        buildFromRecordSet(rs);
    }

    /**
     * Builds a combo from a Vector
     * @param v the Vector used to build the combo
     */
    public WslComboBox(Vector v)
    {
        // delegate
        buildFromVector(v);
    }

    /**
     * Builds a combo from a RecordSet
     * @param rs the RecordSet used to build the combo
     */
    public void buildFromRecordSet(RecordSet rs)
    {
        // delegate
        buildFromVector(rs.getRows());
    }

    /**
     * Builds a combo from a Vector
     * @param v the Vector used to build the combo
     */
    public void buildFromVector(Vector v)
    {
        // delegate
        buildFromArray(v.toArray());
    }

    /**
     * Builds a combo from an Object array
     * @param array the Object array used to build the combo
     */
    public void buildFromArray(Object[] array)
    {
        // verify params
        Util.argCheckNull(array);

        // build from vector
        for(int i = 0; i < array.length; i++)
            this.addItem(array[i]);
    }

    /**
     * Select the item with the specified text
     * @param text the text to look for
     * @return int the index if the row found to match the param string, -1 if not found
     */
    public int selectItem(String text)
    {
        // iterate the combo
        for(int i = 0; i < this.getItemCount(); i++)
        {
            // compare the item text
            if(getItemAt(i).toString().equalsIgnoreCase(text))
            {
                this.setSelectedIndex(i);
                return i;
            }
        }

        // not found
        return -1;
    }

    /**
     * Select the combo by finding a DataObject by a field and value
     * @param field the field to compare the value with
     * @param value the value to compare
     * @return int the selected index, -1 if not found
     */
    public int selectDataObject(String field, Object value)
    {
        // get the selected index
        int index = findDataObject(field, value);
        if(index >= 0)
            this.setSelectedIndex(index);
        return index;
    }

    /**
     * Find the index of a data object from a field value
     * @param field the name of a field
     * @param value the value of the field
     * @return int the index of the DataObject, -1 if not found
     */
    public int findDataObject(String field, Object value)
    {
        // iterate the combo
        DataObject dobj;
        String tempVal;
        for(int i = 0; i < this.getItemCount(); i++)
        {
            // get the DataObject
            dobj = (DataObject)getItemAt(i);

            // compare the field values
            if(dobj != null)
            {
                tempVal = dobj.getStringValue(field);
                if((tempVal == null && value == null) ||
                    (tempVal != null && tempVal.equals(Type.objectToString(value))))
                return i;
            }
        }

        // not found
        return -1;
    }

    /**
     * return the preferred size for the combo
     */
    public Dimension getPreferredSize()
    {
        return _dimension;
    }


    //--------------------------------------------------------------------------
    // clear

    /**
     * Clear the combo
     */
    public void clear()
    {
        // delegate to removeAllItems()
        this.removeAllItems();
    }
}