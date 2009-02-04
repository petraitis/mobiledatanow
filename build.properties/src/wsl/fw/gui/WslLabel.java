
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
import javax.swing.JLabel;

/**
 * Wsl framework subclass of JLabel. Adds sizing constructors.
 */
public class WslLabel extends JLabel
{
    /**
     * Preferred label width
     */
    public static final int DEFAULT_WIDTH = 100;

    /**
     * Preferred label height
     */
    public static final int DEFAULT_HEIGHT = 20;

    /**
     * Dimension of the label
     */
    private Dimension _dimension = null;

    /**
     * String constructor
     * @param text the text of the label
     */
    public WslLabel(String text)
    {
        super(text);
    }

    /**
     * Constructor taking text and a width for the label
     * @param text the text of the label
     * @param width the width of the label
     */
    public WslLabel(String text, int width)
    {
        super(text);
        _dimension = new Dimension(width, DEFAULT_HEIGHT);
        updateSize();
    }

    /**
     * Constructor taking text and a Dimension for the label
     * @param dimension the Dimension of the label
     */
    public WslLabel(String text, Dimension dimension)
    {
        super(text);
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
     * return the preferred size for the text box
     */
    public Dimension getPreferredSize()
    {
        return (_dimension == null)? super.getPreferredSize(): _dimension;
    }
}