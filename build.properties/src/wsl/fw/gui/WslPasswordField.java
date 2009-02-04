//==============================================================================
// WslPasswordField.java
// Copyright (c) 2001 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.gui;

// imports
import java.awt.Dimension;
import javax.swing.JPasswordField;

//------------------------------------------------------------------------------
/**
 * JPasswordField subclass that sets a default preferred size and provides
 * size-based constructors.
 */
public class WslPasswordField extends JPasswordField
{
    /**
     * Preferred text field width
     */
    public static final int DEFAULT_WIDTH = 100;

    /**
     * Preferred text field height
     */
    public static final int DEFAULT_HEIGHT = 20;

    /**
     * Dimension of the text field
     */
    private Dimension _dimension = new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);

    //--------------------------------------------------------------------------
    /**
     * Blank constructor
     */
    public WslPasswordField()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Constructor taking a width for the text field
     * @param width the width of the text field
     */
    public WslPasswordField(int width)
    {
        _dimension.width = width;
        updateSize();
    }

    //--------------------------------------------------------------------------
    /**
     * Constructor taking a Dimension for the text field
     * @param dimension the Dimension of the text field
     */
    public WslPasswordField(Dimension dimension)
    {
        _dimension = dimension;
        updateSize();
    }

    //--------------------------------------------------------------------------
    /**
     * Updates the size properties of the control based on preferred size
     */
    private void updateSize()
    {
        setSize(getPreferredSize());
        setMinimumSize(getPreferredSize());
        setMaximumSize(getPreferredSize());
    }

    //--------------------------------------------------------------------------
    /**
     * return the preferred size for the text box
     */
    public Dimension getPreferredSize()
    {
        return _dimension;
    }
}

//==============================================================================
// end of file WslPasswordField.java
//==============================================================================
