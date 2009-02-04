//==============================================================================
// DataSourceParam.java
// Copyright (c) 2001 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.datasource;

import java.io.Serializable;
import wsl.fw.util.Util;

//--------------------------------------------------------------------------
/**
 * Class to hold params used to identify or connect to a datasource.
 * Currently only supports JdbcDataSource
 *
 */
public abstract class DataSourceParam implements Serializable
{
    protected String _name;

    //--------------------------------------------------------------------------
    /**
     * Create a DataSourceParam defining a JdbcDataSource.
     */
    public DataSourceParam(String name)
    {
        _name = name;
    }

    //--------------------------------------------------------------------------
    /**
     * @return the fully qualified name identifying the datasource
     */
    public abstract String getFullName();

    //--------------------------------------------------------------------------
    /**
     * @return true if the two DataSourceParams are equal
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof DataSourceParam)
            return getFullName().equals(((DataSourceParam)obj).getFullName());
        return false;
    }

    //--------------------------------------------------------------------------
    /**
     * @return the hashCode.
     */
    public int hashCode()
    {
        return getFullName().hashCode();
    }
}

//==============================================================================
// end of file DataSourceParam.java
//==============================================================================
