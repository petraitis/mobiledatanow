//Source file: D:\dev\wsl\fw\datasource\Entity.java

package wsl.fw.datasource;

// imports
import java.util.Vector;

/**
 * Entity represents an entity in a datasource
 * ie a table in a database or a row in a file
 * Entities contain fields and joins
 * DataSource objects use Entity definitions to determine how to retrieve and update data
 * into themselves
 * DataObjects each reference an Entity, that contains their definition
 */
public interface Entity
{
    //--------------------------------------------------------------------------
    // constants

    public static final int EF_NONE = 0;


    //--------------------------------------------------------------------------
    // accessors

    /**
     * Returns the name of the entity
     * @return String
     * @roseuid 3973D5BF0006
     */
    public String getName();

    /**
     * Sets the entity name into the Entity
     * @param name
     * @return void
     * @roseuid 3973C5CE0347
     */
    public void setName(String name);

    /**
     * Return the default class
     * @return Class the default class
     */
    public Class getDefaultClass();

    /**
     * Set the default class name
     * @param defaultClass the default class
     * @return void
     */
    public void setDefaultClass(Class defaultClass);

    /**
     * Returns the entity flags
     * @return int
     * @roseuid 3973D5CA01C5
     */
    public int getFlags();

    /**
     * Set the entity flags
     * @param flags the value to set
     */
    public void setFlags(int val);

    /**
     * @return DataSource the parent DataSource of this Entity
     */
    public DataSource getParentDataSource();

    /**
     * Set the parent DataSource of this Entity
     * @param parentDs the parent DataSource
     */
    public void setParentDataSource(DataSource parentDs);


    //--------------------------------------------------------------------------
    // fields

    /**
     * Add a single Field to the Entity
     * @param f the field to add
     */
    public void addField(Field f);

    /**
     * Sets fields into the Entity
     * @param fields Vector of fields to set
     * @return void
     * @roseuid 3973C70E0185
     */
    public void setFields(Vector fields);

    /**
     * Returns the fields of the entity
     * @return Hashtable
     */
    public Vector getFields();

    /**
     * Returns a the field mapped to the param name
     * @param fieldName the name of the field
     * @return Field
     */
    public Field getField(String fieldName);

    /**
     * returns a vector of fields that have the FF_UNIQUE_KEY flag set
     * @return Vector the vector of key Fields
     */
    public Vector getUniqueKeyFields();

    /**
     * @return Field the first field with FF_SYSTEM_KEY flag set
     */
    public Field getSystemKeyField();

    /**
     * @param f field to verify is generated or not
     * @return boolean the if the param field is generated
     */
    public boolean isFieldGenerated(Field f);

    /**
     * returns a vector of fields that have the FF_NAMING flag set
     * @return Vector the vector of naming Fields
     */
    public Vector getNamingFields();

    /**
     * Get the numeric index of a field from its name
     * @param name the name of the field
     * @return int the index of the field (zero-based)
     */
    public int getFieldIndex(String name);


    //--------------------------------------------------------------------------
    // joins

    /**
     * Add a single Join to the Entity
     * @param j the Join to add
     */
    public void addJoin(Join j);

    /**
     * Sets joins into the Entity
     * @param joins Vector of joins to set
     * @return void
     */
    public void setJoins(Vector joins);

    /**
     * Return the vector of parent joins
     * @return Vector the vector of Join objects
     */
    public Vector getJoins();


    //--------------------------------------------------------------------------
    // other operations

    /**
     * Return true if the param flag is set
     * @param flag the flag to check for
     * @return boolean true if the param flag is set
     */
    public boolean hasFlag(int flag);

    /**
     * Verifies the structure and flags on the fields and entity
     * @throws RuntimeException if invalid setup of fields or entity
     */
    public void verifyEntityDefinition();

    /**
     * Adds a KeyGeneratorData object to the Entity
     * @param kgd the KeyGeneratorData object to add
     * @return void
     */
    public void addKeyGeneratorData(KeyGeneratorData kgd);

    /**
     * Returns the Vector of KeyGeneratorData objects
     * @return Vector the KeyGeneratorData objects
     */
    public Vector getKeyGeneratorData();
}
