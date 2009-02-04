//==============================================================================
// EntitySchemaName.java
// Copyright (c) 2001 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.datasource;

import java.io.Serializable;

//------------------------------------------------------------------------------
/**
 * Class to hold table schema and name information when importing data store
 * definitions.
 */
public class EntitySchemaName implements Serializable
{
    // attributes
    private String _fullTableName;
    private String _schemaName;
    private String _tableName;

    //--------------------------------------------------------------------------
    /**
     * Constructor.
     * @param fullTableName the fully qualified table name, includes the schema.
     *   prefix if required.
     * @param schemaName, the schema name, may be null.
     * @param tableName, the table name, without schema prefix.
     */
    public EntitySchemaName(String fullTableName, String schemaName,
        String tableName)
    {
        _fullTableName = fullTableName;
        _schemaName    = schemaName;
        _tableName     = tableName;
    }

    //--------------------------------------------------------------------------
    /**
     * @return the fully qualified table name, includes the schema prefix if
     *   required.
     */
    public String getFullTableName()
    {
        return _fullTableName;
    }

    //--------------------------------------------------------------------------
    /**
     * @return the schema name, may be null.
     */
    public String getSchemaName()
    {
        return _schemaName;
    }

    //--------------------------------------------------------------------------
    /**
     * @return the table name, without schema prefix.
     */
    public String getTableName()
    {
        return _tableName;
    }
}

//==============================================================================
// end of file EntitySchemaName.java
//==============================================================================
