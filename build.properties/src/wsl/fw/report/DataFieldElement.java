
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
 * Represents a field in a DataObject. Adds entityName to TextFieldElement
 */
public class DataFieldElement extends TextFieldElement
{
    //--------------------------------------------------------------------------
    // attributes

    private String _entityName = "";


    //--------------------------------------------------------------------------
    // constructors

    /**
     * Argument ctor
     * @param entityName the name of the entity
     * @param fieldName the name of the field
     * @param pos the position to draw the text at
     */
    public DataFieldElement(String entityName, String fieldName, WslPos pos)
    {
        super(fieldName, pos);
        setEntityName(entityName);
    }


    //--------------------------------------------------------------------------
    // accessors

    /**
     * Set the entity name
     * @param entityName
     */
    public void setEntityName(String entityName)
    {
        _entityName = entityName;
    }

    /**
     * @return String the entity name
     */
    public String getEntityName()
    {
        return _entityName;
    }
}