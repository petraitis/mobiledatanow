
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
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JScrollPane;
import wsl.fw.util.Util;
import wsl.fw.datasource.RecordSet;
import wsl.fw.datasource.DataManager;
import wsl.fw.datasource.DataSource;
import wsl.fw.datasource.DataSourceException;
import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.Query;

/**
 * Wsl framework subclass of JList. Adds constructors to build combo using the DataSource framework
 */
public class WslList extends JList
{
    /**
     * Preferred width
     */
    public static final int DEFAULT_WIDTH = 100;

    /**
     * Scrollpane containing the list
     */
    private JScrollPane _sp;

    /**
     * Dimension of the combo box
     */
    private Dimension _dimension = null;

    /**
     * Blank ctor
     */
    public WslList()
    {
        super(new DefaultListModel());
        // create the scrollpane
        _sp = new JScrollPane(this);
    }

    /**
     * Constructor taking a height for the combo
     * @param height the height of the combo
     */
    public WslList(int height)
    {
        super(new DefaultListModel());
        // create the scrollpane
        _sp = new JScrollPane(this);
        _dimension = new Dimension(DEFAULT_WIDTH, height);
        updateSize();
    }

    /**
     * Constructor taking a dimension for the combo
     * @param dimension the size of the combo
     */
    public WslList(Dimension dimension)
    {
        super(new DefaultListModel());
        // create the scrollpane
        _sp = new JScrollPane(this);
        _dimension = dimension;
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
    public WslList(String entityName)
    {
        // create the scrollpane
        _sp = new JScrollPane(this);

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
    public WslList(RecordSet rs)
    {
        // create the scrollpane
        _sp = new JScrollPane(this);

        // delegate
        buildFromRecordSet(rs);
    }

    /**
     * Builds a combo from a Vector
     * @param v the Vector used to build the combo
     */
    public WslList(Vector v)
    {
        // create the scrollpane
        _sp = new JScrollPane(this);

        // delegate
        buildFromVector(v);
    }

    /**
     * @return DefaultListModel the default list model
     */
    public DefaultListModel getDefaultModel()
    {
        return (DefaultListModel)getModel();
    }

    /**
     * @return JScrollPane the scrollpane
     */
    public JScrollPane getScrollPane()
    {
        return _sp;
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
            getDefaultModel().addElement(array[i]);
    }

    /**
     * Add an item to the list
     */
    public void addItem(Object obj)
    {
        // validate
        Util.argCheckNull(obj);

        // add to the list
        getDefaultModel().addElement(obj);
    }

    /**
     * Remove an item from the list
     */
    public void removeItem(Object obj)
    {
        // validate
        Util.argCheckNull(obj);

        // remove
        getDefaultModel().removeElement(obj);
    }

    /**
     * Clear the list
     */
    public void clear()
    {
        getDefaultModel().removeAllElements();
    }

    /**
     * Select the item with the specified text
     * @param text the text to look for
     * @return int the index if the row found to match the param string, -1 if not found
     */
    public int selectItem(String text)
    {
        // iterate the combo
        for(int i = 0; i < getModel().getSize(); i++)
        {
            // compare the item text
            if(getModel().getElementAt(i).toString().equalsIgnoreCase(text))
            {
                setSelectedIndex(i);
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
        return (_dimension == null)? super.getPreferredSize(): _dimension;
    }
}
