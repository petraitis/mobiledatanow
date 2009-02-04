
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
 * @author
 * @version 1.0
 */
package wsl.fw.html;

// imports
import java.util.Vector;
import wsl.fw.datasource.*;
import org.apache.ecs.ConcreteElement;


/**
 * Node for holding hierarchical objects
 */
public class WslHtmlTreeNode
{
    //--------------------------------------------------------------------------
    // constants

    public static final int OPEN = 0;
    public static final int CLOSED = 1;

    //--------------------------------------------------------------------------
    // attributes

    public Object data;
    public ConcreteElement element;
    public WslHtmlTreeNode parent;
    public Vector children = new Vector();
    public int level = 0;
    public int state = CLOSED;

    /**
     * Ctor taking an Object
     * @param obj the Object for this node
     */
    public WslHtmlTreeNode(Object obj)
    {
        data = obj;
    }

    /**
     * Add a child node to this
     * @param child the node to add
     */
    public void addChildNode(WslHtmlTreeNode child)
    {
        // add child
        children.add(child);
        child.parent = this;
        child.level = this.level + 1;
    }

    /**
     * Add a child node to this
     * @param obj the Object to add
     */
    public void addChildObject(Object obj)
    {
        // delegate
        addChildNode(new WslHtmlTreeNode(obj));
    }

    /**
     * @return String representation of the object
     */
    public String toString()
    {
        return (data == null)? super.toString(): data.toString();
    }
}