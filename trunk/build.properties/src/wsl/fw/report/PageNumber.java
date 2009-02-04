
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
 * @author
 * @version 1.0
 */
package wsl.fw.report;

import wsl.fw.resource.ResId;

/**
 * Draws a page number
 */
public class PageNumber extends TextElement
{
    //--------------------------------------------------------------------------
    // init

    // resources
    public static final ResId STRING_PAGE  = new ResId("PageNumber.string.Page");

    /**
     * Argument ctor
     * @param pos the position of the element
     */
    public PageNumber(WslPos pos)
    {
        setPosition(pos);
    }

    //--------------------------------------------------------------------------
    // drawing

    /**
     * Draw the PrintElement
     * @param pd PrintData for printing
     */
    public void drawElement(PrintData pd)
    {
        // draw onto the graphics context
        pd.g.drawString(STRING_PAGE.getText() + " " + String.valueOf(pd.page + 1), pd.pos.x, pd.pos.y);
    }
}