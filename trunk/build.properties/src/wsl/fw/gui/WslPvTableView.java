
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
import javax.swing.ListSelectionModel;
import pv.jfcx.JPVTableView;

/**
 * JPVTableView subclass that forces single line, non-editing selection on the table
 * Note: New constructor allows editing of cells
 */
public class WslPvTableView extends JPVTableView
{
    /**
     * the model
     */
    private WslPvTableModel _model;
    private boolean _editable = false;
    /**
     * Constructor
     */
    public WslPvTableView(WslPvTableModel model)
    {
        // super
        super(model.getPvTableModel());
        _model = model;

        // set self into model
        model.setTableView(this);

        // set editable to false
        setEditable(_editable);

        // set selection mode
        getTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        getTable().setColumnSelectionAllowed(false);
    }

    public WslPvTableView(WslPvTableModel model, boolean editable)
    {
        // super
        super(model.getPvTableModel());
        _editable = editable;
        _model = model;

        // set self into model
        model.setTableView(this);

        // set editable to false
        setEditable(_editable);

        // set selection mode
        getTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        getTable().setColumnSelectionAllowed(false);
    }

}