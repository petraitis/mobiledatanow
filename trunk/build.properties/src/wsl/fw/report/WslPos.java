
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
import wsl.fw.util.Type;


/**
 * Encapsulates positional information for reporting
 */
public class WslPos
{
    //--------------------------------------------------------------------------
    // attributes

    public int _row = Type.NULL_INTEGER;
    public int _col = Type.NULL_INTEGER;
    public double _x = Type.NULL_DOUBLE;
    public double _y = Type.NULL_DOUBLE;
    public Point _pos = null;

    //--------------------------------------------------------------------------
    // constructors

    /**
     * Blank ctor
     */
    public WslPos()
    {
        this((int)0, (int)0);
    }

    /**
     * Constructor
     * @param pos Point position
     */
    public WslPos(Point pos)
    {
        this(Type.NULL_INTEGER, Type.NULL_INTEGER, Type.NULL_DOUBLE, Type.NULL_DOUBLE, pos);
    }

    /**
     * Constructor
     * @param col Column, row defaults to 0
     */
    public WslPos(int col)
    {
        this(col, 0, Type.NULL_DOUBLE, Type.NULL_DOUBLE, null);
    }

    /**
     * Constructor
     * @param col Column
     * @param row Row
     */
    public WslPos(int col, int row)
    {
        this(col, row, Type.NULL_DOUBLE, Type.NULL_DOUBLE, null);
    }

    /**
     * Constructor
     * @param x x position
     */
    public WslPos(double x)
    {
        this(Type.NULL_INTEGER, Type.NULL_INTEGER, x, Type.NULL_DOUBLE, null);
    }

    /**
     * Constructor
     * @param x x position
     * @param row Row
     */
    public WslPos(double x, int row)
    {
        this(Type.NULL_INTEGER, row, x, Type.NULL_DOUBLE, null);
    }

    /**
     * Constructor
     * @param x x position
     * @param y y position
     */
    public WslPos(double x, double y)
    {
        this(Type.NULL_INTEGER, Type.NULL_INTEGER, x, y, null);
    }

    /**
     * The big one
     */
    public WslPos(int col, int row, double x, double y, Point pos)
    {
        _row = row;
        _col = col;
        _x = x;
        _y = y;
        _pos = pos;
    }
}