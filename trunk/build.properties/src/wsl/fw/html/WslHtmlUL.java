
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
import org.apache.ecs.html.UL;


/**
 * Adds functionality to org.apache.ecs.html.UL
 */
public class WslHtmlUL extends UL
{
    /**
     * Blank ctor
     */
    public WslHtmlUL()
    {
        // set attribs
        setPrettyPrint(true);
    }

    /**
     * Override add to set pretty print for child
     */
    public UL addElement(Element element)
    {
        // set attribs
        if(element != null)
        {
            element.setPrettyPrint(true);
            super.addElement(String.valueOf(element.toString().hashCode()), element);
        }
        return this;
    }

    /**
     * Remove all the elements
     */
    public void removeAllElements()
    {
        // iterate the keys
        Enumeration keys = this.keys();
        while(keys.hasMoreElements())
        {
            // remove
            removeElement((String)keys.nextElement());
        }
    }
}