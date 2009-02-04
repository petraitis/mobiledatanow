
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
import java.awt.Dimension;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

/**
 * Wsl framework subclass of JTextArea. Adds size constructors and integrated JScrollPane
 */
public class WslTextArea extends JTextArea
{
    /**
     * Default number of columns
     */
    public static final int DEFAULT_COLUMNS = 30;

    /**
     * Default number of rows
     */
    public static final int DEFAULT_ROWS = 4;

    /**
     * Scrollpane containing the text area
     */
    private JScrollPane _sp;

    /**
     * Blank ctor
     */
    public WslTextArea()
    {
        // init
        init(DEFAULT_COLUMNS, DEFAULT_ROWS);
    }

    /**
     * Constructor taking a number of columns
     * @param cols the preferred number of columns of the control
     */
    public WslTextArea(int cols)
    {
        // init
        init(cols, DEFAULT_ROWS);
    }

    /**
     * Constructor taking a columns and rows
     * @param cols the preferred number of columns of the control
     * @param rows the preferred number of rows of the control
     */
    public WslTextArea(int cols, int rows)
    {
        // init
        init(cols, rows);
    }

    /**
     * Initialise the control
     * @param cols the preferred number of columns of the control
     * @param rows the preferred number of rows of the control
     */
    private void init(int cols, int rows)
    {
        // set text area properties
        this.setLineWrap(true);
        this.setWrapStyleWord(true);
        this.setColumns(cols);
        this.setRows(rows);

        Dimension dim = getPreferredSize();
        setSize(dim);
        setMinimumSize(dim);

        // create the scrollpane
        _sp = new JScrollPane(this);
    }

    /**
     * @return JScrollPane the scrollpane
     */
    public JScrollPane getScrollPane()
    {
        return _sp;
    }

}