
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


/**
 * TextELement subclass that adds a String field name. The field name is used to
 * identify the element to dynamically set values into the element.
 */
public class TextFieldElement extends TextElement
{
    //--------------------------------------------------------------------------
    // attributes

    private String _fieldName = "";


    //--------------------------------------------------------------------------
    // constructors

    /**
     * Argument ctor
     * @param fieldName the name of the field
     * @param pos the position to draw the text at
     */
    public TextFieldElement(String fieldName, WslPos pos)
    {
        setFieldName(fieldName);
        setPosition(pos);
    }


    //--------------------------------------------------------------------------
    // accessors

    /**
     * Set the field name
     * @param fieldName
     */
    public void setFieldName(String fieldName)
    {
        _fieldName = fieldName;
    }

    /**
     * @return String the field name
     */
    public String getFieldName()
    {
        return _fieldName;
    }
}