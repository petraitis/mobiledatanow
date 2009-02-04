
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
 * @author
 * @version 1.0
 */
package wsl.fw.report;

// imports
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.print.PageFormat;

/**
 * draws a line
 */
public class LineElement extends PrintElement
{
    //--------------------------------------------------------------------------
    // constants

    private final int Y_OFFSET = 10;


    //--------------------------------------------------------------------------
    // attributes

    private int _length = 100;


    //--------------------------------------------------------------------------
    // init

    /**
     * Ctor
     * @param length the length of the line
     * @param pos the top, left position of the line
     */
    public LineElement(int length, WslPos pos)
    {
        setPosition(pos);
        _length = length;
    }

    /**
     * Draw the element
     * @param pd PrintData for printing
     */
    public void drawElement(PrintData pd)
    {
        // draw a line
        Point pos = pd.pos;
        pd.g.drawLine(pos.x, pos.y - Y_OFFSET, pos.x + _length, pos.y - Y_OFFSET);
    }
}