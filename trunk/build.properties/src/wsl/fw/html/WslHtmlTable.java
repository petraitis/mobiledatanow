
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
import java.util.Enumeration;
import org.apache.ecs.Element;
import org.apache.ecs.html.Table;

/**
 * org.apach.ecs.html.Table subclass to default some attributes
 */
public class WslHtmlTable extends Table
{
    /**
     * Blank ctor
     */
    public WslHtmlTable()
    {
    }

    public boolean getPrettyPrint()
    {
        return true;
    }

    /**
     * Override add to set pretty print for child
     */
    public Table addElement(Element element)
    {
        // set attribs
        if(element != null)
            super.addElement(Integer.toString(element.hashCode()), element);
        return this;
    }

    /**
     * Remove all the elements
     */
    public void removeAllElements()
    {
        // iterate the keys
        Enumeration keys = this.keys();
        while(keys != null && keys.hasMoreElements())
        {
            // remove
            removeElement((String)keys.nextElement());
            keys = keys();
        }
    }
}