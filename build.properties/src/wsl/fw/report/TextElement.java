
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
 * PrintElement that draws text onto graphics contexts
 */
public class TextElement extends PrintElement
{
    //--------------------------------------------------------------------------
    // attributes

    private String _text = "";


    //--------------------------------------------------------------------------
    // constructors

    /**
     * Blank ctor
     */
    public TextElement()
    {
    }

    /**
     * Argument ctor
     * @param text the String to draw
     * @param pos the position of the element
     */
    public TextElement(String text, WslPos pos)
    {
        setText(text);
        setPosition(pos);
    }


    //--------------------------------------------------------------------------
    // accessors

    /**
     * Set text into the TextElement
     * @param text
     */
    public void setText(String text)
    {
        _text = text;
    }

    /**
     * @return String the text of the TextElement
     */
    public String getText()
    {
        return _text;
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
        pd.g.drawString(_text, pd.pos.x, pd.pos.y);
    }
}