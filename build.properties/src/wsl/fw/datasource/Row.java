//==============================================================================
// Row.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.datasource;

/**
 * imports
 */
import java.util.Vector;
import wsl.fw.resource.ResId;

//------------------------------------------------------------------------------
/**
 * Represents a row in a recordset
 * Composite of DataObjects
 */
public class Row extends DataObject
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/datasource/Row.java $ ";

    // resources
    public static final ResId TO_STRING1  = new ResId("Row.toString1");
    public static final ResId TO_STRING2  = new ResId("Row.toString2");

    /**
     * attributes
     */
    private Vector _components = null;

    //--------------------------------------------------------------------------
    /**
     * Blank constructor.
     */
    public Row()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Special overload of init to ensure that a Row cannot do a normal init
     * which wil set an entity and datasource, neither of which is valid as Row
     * does not have an entity name and should not be put in the DB.
     */
    protected void init()
    {
        // do nothing except set _initDone to true
        _initDone = true;
    }

    //--------------------------------------------------------------------------
    /**
     * Return the name of the entity that this DataObject is defined by
     * @return String name of the Entity
     */
    public String getEntityName()
    {
        return "";
    }

    //--------------------------------------------------------------------------
    /**
     * Add a DataObject as a component
     * @param dobj DataObject component
     * @return void
     */
    public void addComponent(DataObject component)
    {
        // if null vector, create
        if(_components == null)
            _components = new Vector();

        // add to the component vector
        if(component != null)
            _components.add(component);
    }

    //--------------------------------------------------------------------------
    /**
     * Returns a Vector of component DataObjects
     * @return Vector the component DataObjects
     * @roseuid 3973D93A022E
     */
    public Vector getComponents()
    {
        return _components;
    }

    //--------------------------------------------------------------------------
    /**
     * Create and return the Entity object that defines this class of DataObject
     * @return Entity the entity that defines this class of DataObject
     */
    public Entity createEntity()
    {
        return null;
    }

    //--------------------------------------------------------------------------
    /**
     * Creates and returns the Field objects that defines this class of DataObject
     * @return Vector a vector of Field objects
     */
    public Vector createFields()
    {
        return null;
    }

    //--------------------------------------------------------------------------
    /**
     * Creates and returns the Join objects belonging to this class of DataObject
     * @return Vector a vector of Join objects
     */
    public Vector createJoins()
    {
        return null;
    }

    //--------------------------------------------------------------------------
    /**
     * Returns a string indicating that this is a row rather than a normal
     * data object.
     * @returns the short description of the row object.
     */
    public String toString()
    {
        return TO_STRING1.getText() + " " + _components.size() + " " + TO_STRING2.getText();
    }

    //--------------------------------------------------------------------------
    /**
     * Returns a long description string that concatenates all the components
     * in the row.
     * @return String
     */
    public String getLongDesc()
    {
        StringBuffer buff = new StringBuffer();

        // iterate over components adding their long descriptions
        for (int i = 0; i < _components.size(); i++)
        {
            // except for the first time add a separator
            if (i > 0)
                buff.append(" | ");
            DataObject component = (DataObject) _components.get(i);
            buff.append(component.getLongDesc());
        }

        // return the concatenated descriptions
        return buff.toString();
    }
}

//==============================================================================
// end of file Row.java
//==============================================================================
