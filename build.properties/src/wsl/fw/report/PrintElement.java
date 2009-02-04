
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
import java.awt.Point;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;

/**
 * Superclass for all elements that may be printed in a WslReport
 */
public abstract class PrintElement
{
    //--------------------------------------------------------------------------
    // attributes

    private WslPos _pos = null;

    //--------------------------------------------------------------------------
    // accessors

    /**
     * Set the position
     * @param pos
     */
    public void setPosition(WslPos pos)
    {
        _pos = pos;
    }

    /**
     * @return WslPos the position
     */
    public WslPos getPosition()
    {
        return _pos;
    }


    //--------------------------------------------------------------------------
    // drawing

    /**
     * Draw the PrintElement
     * @param pd PrintData for printing
     */
    public abstract void drawElement(PrintData pd);
}