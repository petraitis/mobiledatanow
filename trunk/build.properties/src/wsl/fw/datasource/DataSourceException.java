//==============================================================================
// DataSourceException.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.datasource;

//------------------------------------------------------------------------------
/**
 * Exception for DataSource errors
 */
public class DataSourceException extends Exception
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/datasource/DataSourceException.java $ ";

    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public DataSourceException()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * String Constructor.
     * @param msg, message tex passed to superclass constructor.
     */
    public DataSourceException(String msg)
    {
        super(msg);
    }
}

//==============================================================================
// end of file DataSourceException.java
//==============================================================================
