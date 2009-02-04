
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
 * Struct containing data for printing of print elements
 */
public class PrintData
{
    //--------------------------------------------------------------------------
    // public attributes

    public Graphics2D g;
    public PageFormat pf;
    public Point pos;
    public int page;
}