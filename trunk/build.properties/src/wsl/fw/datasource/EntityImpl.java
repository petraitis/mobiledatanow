//==============================================================================
// EntityImpl.java
// Copyright (c) 2001 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.datasource;

// imports
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import wsl.fw.util.Util;
import wsl.fw.resource.ResId;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

//------------------------------------------------------------------------------
/**
 * Implementation subclass of the Entity interface
 */
public class EntityImpl implements Entity, Serializable
{
    //--------------------------------------------------------------------------
    // resources

    public static final ResId EXCEPTION_NO_FIELDS  = new ResId("Entity.exception.NoFields");
    public static final ResId EXCEPTION_NO_SYSTEM_KEY  = new ResId("Entity.exception.NoSystemKey");
    public static final ResId EXCEPTION_MORE_THAN_ONE  = new ResId("Entity.exception.MoreThanOne");
    public static final ResId EXCEPTION_UNIQUE  = new ResId("Entity.exception.Unique");
    public static final ResId EXCEPTION_MORE_THAN_ONE_UNIQUE1  = new ResId("Entity.exception.MoreThanOneUnique1");
    public static final ResId EXCEPTION_MORE_THAN_ONE_UNIQUE2  = new ResId("Entity.exception.MoreThanOneUnique2");


    //--------------------------------------------------------------------------
    // attributes
    private String               _name = "";
    private Class                _defaultClass = null;
    private int                  _flags = EF_NONE;
    private Vector               _fields = new Vector();
    private Vector               _parentJoins = new Vector();
    private Vector               _kgd = new Vector();
    private transient DataSource _parentDs;
    private boolean              _ignoreVerification = false;


    //--------------------------------------------------------------------------
    // constructors

    /**
     * Blank constructor
     * @roseuid 3973C564039F
     */
    public EntityImpl()
    {
    }

    /**
     * Constructor taking the entity name
     */
    public EntityImpl(String name)
    {
        setName(name);
    }

    /**
     * Constructor taking the entity name and the default class
     */
    public EntityImpl(String name, Class defaultClass)
    {
        setName(name);
        setDefaultClass(defaultClass);
    }

    /**
     * Constructor taking the entity name and the default class and ignore verification flag
     */
    public EntityImpl(String name, Class defaultClass, boolean ignoreVerification)
    {
        setName(name);
        setDefaultClass(defaultClass);
        setIgnoreVerification(ignoreVerification);
    }


    //--------------------------------------------------------------------------
    // accessors

    /**
     * Returns the name of the entity
     * @return String
     * @roseuid 3973D5BF0006
     */
    public String getName()
    {
        return _name;
    }

    /**
     * Sets the entity name into the Entity
     * @param name
     * @return void
     * @roseuid 3973C5CE0347
     */
    public void setName(String name)
    {
        _name = name;
    }

    /**
     * Return the default class
     * @return Class the default class
     */
    public Class getDefaultClass()
    {
        return _defaultClass;
    }

    /**
     * Set the default class name
     * @param defaultClass the default class
     * @return void
     */
    public void setDefaultClass(Class defaultClass)
    {
        _defaultClass = defaultClass;
    }

    /**
     * Returns the entity flags
     * @return int
     * @roseuid 3973D5CA01C5
     */
    public int getFlags()
    {
        return _flags;
    }

    /**
     * Set the entity flags
     * @param flags the value to set
     */
    public void setFlags(int val)
    {
        _flags = val;
    }

    /**
     * @return DataSource the parent DataSource of this Entity
     */
    public DataSource getParentDataSource()
    {
        return _parentDs;
    }

    /**
     * Set the parent DataSource of this Entity
     * @param parentDs the parent DataSource
     */
    public void setParentDataSource(DataSource parentDs)
    {
        _parentDs = parentDs;
    }

    /**
     * @return true if datasource should ignore entity verification
     */
    public boolean ignoreVerification()
    {
        return _ignoreVerification;
    }

    /**
     * Set the ignoreVerification flag
     * @param ignore true if datasource should ignore entity verification
     */
    public void setIgnoreVerification(boolean ignore)
    {
        _ignoreVerification = ignore;
    }


    //--------------------------------------------------------------------------
    // fields

    /**
     * Add a single Field to the Entity
     * @param f the field to add
     */
    public void addField(Field f)
    {
        // validate
        Util.argCheckNull(f);

        // add to table
        _fields.add(f);
    }

    /**
     * Sets fields into the Entity
     * @param fields Vector of fields to set
     * @return void
     * @roseuid 3973C70E0185
     */
    public void setFields(Vector fields)
    {
        // build the fields hashtable
        Field f;
        for(int i = 0; fields != null && i < fields.size(); i++)
        {
            // get the field and add to hashtable
            f = (Field)fields.elementAt(i);
            if(f != null)
                addField(f);
        }
    }

    /**
     * Returns the fields of the entity
     * @return Hashtable
     */
    public Vector getFields()
    {
        return _fields;
    }

    /**
     * Returns a the field mapped to the param name
     * @param fieldName the name of the field
     * @return Field
     */
    public Field getField(String fieldName)
    {
        // validate
        Util.argCheckEmpty(fieldName);

        // iterate fields
        Field f;
        for(int i = 0; _fields != null && i < _fields.size(); i++)
        {
            // get the field and compare
            f = (Field)_fields.elementAt(i);
            if(f != null && f.getName().equalsIgnoreCase(fieldName))
                return f;
        }

        // not found
        return null;
    }

    /**
     * returns a vector of fields that have the FF_UNIQUE_KEY flag set
     * @return Vector the vector of key Fields
     */
    public Vector getUniqueKeyFields()
    {
        // iterate the fields
        Vector v = new Vector();
        if(_fields != null)
        {
            Field f;
            Enumeration enums = _fields.elements();
            while(enums.hasMoreElements())
            {
                // get the field
                f = (Field)enums.nextElement();

                // if flag set, add to vector
                if(f != null && f.hasFlag(Field.FF_UNIQUE_KEY))
                    v.add(f);
            }
        }

        // return the vector
        return v;
    }

    /**
     * @return Field the first field with FF_SYSTEM_KEY flag set
     */
    public Field getSystemKeyField()
    {
        // iterate the fields
        Field ret = null;
        if(_fields != null)
        {
            Field f;
            Enumeration enums = _fields.elements();
            while(ret == null && enums.hasMoreElements())
            {
                // get the field
                f = (Field)enums.nextElement();

                // if flag set, set return
                if(f != null && f.hasFlag(Field.FF_SYSTEM_KEY))
                    ret = f;
            }
        }

        // return the field
        return ret;
    }

    /**
     * @param f field to verify is generated or not
     * @return boolean the if the param field is generated
     */
    public boolean isFieldGenerated(Field f)
    {
        // must have a field
        Util.argCheckNull(f);

        // iterate the generator data
        KeyGeneratorData kgd;
        for(int i = 0; i < _kgd.size(); i++)
        {
            // compare the field names
            kgd = (KeyGeneratorData)_kgd.elementAt(i);
            if(kgd != null && kgd._targetField.equalsIgnoreCase(f.getName()))
                return true;
        }

        // no key generator set for this field
        return false;
    }

    /**
     * returns a vector of fields that have the FF_NAMING flag set
     * @return Vector the vector of naming Fields
     */
    public Vector getNamingFields()
    {
        // iterate the fields
        Vector v = new Vector();
        if(_fields != null)
        {
            Field f;
            Enumeration enums = _fields.elements();
            while(enums.hasMoreElements())
            {
                // get the field
                f = (Field)enums.nextElement();

                // if flag set, add to vector
                if(f != null && f.hasFlag(Field.FF_NAMING))
                    v.add(f);
            }
        }

        // return the vector
        return v;
    }

    /**
     * Get the numeric index of a field from its name
     * @param name the name of the field
     * @return int the index of the field (zero-based), -1 if not found
     */
    public int getFieldIndex(String name)
    {
        // iterate the fields
        if(_fields != null)
        {
            Field f;
            Enumeration enums = _fields.elements();
            int index = 0;
            while(enums.hasMoreElements())
            {
                // get the field
                f = (Field)enums.nextElement();

                // compare name. if found return index
                if(f != null && f.getName().equalsIgnoreCase(name))
                    return index;

                // increment index
                index++;
            }
        }

        // not found
        return -1;
    }


    //--------------------------------------------------------------------------
    // joins

    /**
     * Add a single Join to the Entity
     * @param j the Join to add
     */
    public void addJoin(Join j)
    {
        // validate
        Util.argCheckNull(j);

        // add to table
        _parentJoins.add(j);
    }

    /**
     * Sets joins into the Entity
     * @param joins Vector of joins to set
     * @return void
     */
    public void setJoins(Vector joins)
    {
        // add joins to join vector
        Join j;
        for(int i = 0; joins != null && i < joins.size(); i++)
        {
            // get the param join and add to attrib
            j = (Join)joins.elementAt(i);
            if(j != null)
                addJoin(j);
        }
    }

    /**
     * Return the vector of parent joins
     * @return Vector the vector of Join objects
     */
    public Vector getJoins()
    {
        return _parentJoins;
    }


    //--------------------------------------------------------------------------
    // other methods

    /**
     * Return true if the param flag is set
     * @param flag the flag to check for
     * @return boolean true if the param flag is set
     */
    public boolean hasFlag(int flag)
    {
        return (getFlags() & flag) > 0;
    }

    /**
     * Verifies the structure and flags on the fields and entity
     * @throws RuntimeException if invalid setup of fields or entity
     */
    public void verifyEntityDefinition()
    {
        // if ignoreing verification return
        if(ignoreVerification())
            return;

        // iterate the fields
        int sysKeyCount = 0;
        boolean sysKeyIsUnique = false;
        int uniqueKeyCount = 0;
        int fieldCount = 0;
        if(_fields != null)
        {
            Field f;
            Enumeration enums = _fields.elements();
            while(enums.hasMoreElements())
            {
                // get the field
                f = (Field)enums.nextElement();
                fieldCount++;

                // set flags
                if(f != null)
                {
                    if(f.hasFlag(Field.FF_SYSTEM_KEY))
                    {
                        sysKeyCount++;
                        if(f.hasFlag(Field.FF_UNIQUE_KEY))
                            sysKeyIsUnique = true;
                    }
                    if(f.hasFlag(Field.FF_UNIQUE_KEY))
                        uniqueKeyCount++;
                }
            }
        }

        // no fields
        if(fieldCount == 0)
            //throw new RuntimeException("You must have fields defined for " + _name);
            throw new RuntimeException(EXCEPTION_NO_FIELDS.getText() + " " + _name);

        // no system key
        if(sysKeyCount == 0)
            //throw new RuntimeException("You must have exactly 1 (one) FF_SYSTEM_KEY field defined for " + _name);
            throw new RuntimeException(EXCEPTION_NO_SYSTEM_KEY.getText() + " " + _name);

        // more than one system key
        if(sysKeyCount > 1)
            //throw new RuntimeException("You must have exactly 1 (one) FF_SYSTEM_KEY field defined for " + _name);
            throw new RuntimeException(EXCEPTION_MORE_THAN_ONE.getText() + " " + _name);

        // must have a unique key defined
        if(uniqueKeyCount == 0)
            //throw new RuntimeException("You must have at least 1 (one) FF_UNIQUE_KEY field defined for " + _name);
            throw new RuntimeException(EXCEPTION_UNIQUE.getText() + " " + _name);

        // if more than 1 unique key, system key cannot be a unique key
        if(uniqueKeyCount > 1 && sysKeyIsUnique)
            //throw new RuntimeException("In entity " + _name + ", If you have more than 1 FF_UNIQUE_KEY defined, then the FF_SYSTEM_KEY may not be one of them.");
            throw new RuntimeException(EXCEPTION_MORE_THAN_ONE_UNIQUE1.getText() + " " + _name +
                EXCEPTION_MORE_THAN_ONE_UNIQUE1.getText());
    }

    /**
     * Adds a KeyGeneratorData object to the Entity
     * @param kgd the KeyGeneratorData object to add
     * @return void
     */
    public void addKeyGeneratorData(KeyGeneratorData kgd)
    {
        _kgd.add(kgd);
    }

    /**
     * Returns the Vector of KeyGeneratorData objects
     * @return Vector the KeyGeneratorData objects
     */
    public Vector getKeyGeneratorData()
    {
        return _kgd;
    }


    //--------------------------------------------------------------------------
    /**
     * toString()
     */
    public String toString()
    {
        return (getName() != null)? getName(): super.toString();
    }

    //--------------------------------------------------------------------------
    /**
     * Serialization support (write).
     * Required because we do not serialize DataSources.
     */
    private void writeObject(ObjectOutputStream outStream)
        throws IOException
    {
        // get info defining the data source for use in reconnection after
        // RMI/serialization
        Object dsId = null;
        if (_parentDs != null)
            dsId = _parentDs.getDsId();

        // write to stream
        outStream.writeObject(dsId);

        // now save the rest of the object normally
        outStream.defaultWriteObject();
    }

    //--------------------------------------------------------------------------
    /**
     * Serialization support (read).
     */
    private void readObject(ObjectInputStream inStream)
        throws IOException, ClassNotFoundException
    {
        // get the info to be used to reconnect to the local data source after
        // RMI/serialization
        Object dsId = inStream.readObject();

        // use the info to locate the correct data source
        if (dsId != null && dsId instanceof DataSourceParam)
            _parentDs = DataManager.getDataSource((DataSourceParam) dsId);
        else if (dsId != null && dsId instanceof String)
            _parentDs = DataManager.getDataSource((String) dsId);
        else
           _parentDs = null;

        // now load the rest of the data normally
        inStream.defaultReadObject();
    }
 }

//==============================================================================
// end of file EntityImpl.java
//==============================================================================


