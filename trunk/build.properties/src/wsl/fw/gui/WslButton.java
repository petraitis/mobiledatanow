
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
import javax.swing.JButton;
import javax.swing.Icon;
import javax.swing.SwingConstants;
import java.awt.Dimension;
import java.awt.event.ActionListener;

/**
 * JButton with a default size
 */
public class WslButton extends JButton
{
    /**
     * Constructor taking a caption and an ActionListener
     * @param caption
     * @param al ActionListener
     */
    public WslButton(String caption)
    {
        // delegate
        this(caption, null);
    }

    /**
     * Constructor taking a caption and an ActionListener
     * @param caption
     * @param al the ActionListener for this button
     */
    public WslButton(String caption, ActionListener al)
    {
        // delegate
        this(caption, new Dimension(GuiConst.BTN_WIDTH, GuiConst.BTN_HEIGHT), al);
    }

    /**
     * Constructor taking a caption, width and an ActionListener
     * @param caption
     * @param width the width of this button
     * @param al the ActionListener for this button
     */
    public WslButton(String caption, int width, ActionListener al)
    {
        // delegate
        this(caption, new Dimension(width, GuiConst.BTN_HEIGHT), al);
    }

    /**
     * Constructor taking a caption, size and an ActionListener
     * @param caption
     * @param dimension the size of this button
     * @param al the ActionListener for this button
     */
    public WslButton(String caption, Dimension dimension, ActionListener al)
    {
        // super
        super(caption);

        // set size
        setDimension(dimension);

        // set alignment
        //setHorizontalAlignment(SwingConstants.LEFT);

        // add ActionListener
        if(al != null)
            addActionListener(al);
    }

    /**
     * Constructor taking an icon, dimension and action listener. Ideal constructor for toolbar buttons.
     * @param icon an icon to show on the button
     * @param d the Dimension of the button
     */
    public WslButton(Icon icon, Dimension d, ActionListener al)
    {
        // super
        super(icon);

        // set size
        if(d != null)
            setDimension(d);

        // add ActionListener
        if(al != null)
            addActionListener(al);
    }

    /**
     * Set the dimension of the button
     * @param d the new Dimension for the button
     */
    public void setDimension(Dimension d)
    {
        setSize(d);
        setMinimumSize(d);
        setMaximumSize(d);
        setPreferredSize(d);
    }
}