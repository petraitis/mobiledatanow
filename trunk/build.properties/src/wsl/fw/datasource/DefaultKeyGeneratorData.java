//==============================================================================
// DefaultKeyGeneratorData.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.datasource;

//--------------------------------------------------------------------------
/**
 * Default key generator data that uses the default key table and columns.
 */
public class DefaultKeyGeneratorData extends KeyGeneratorData
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/datasource/DefaultKeyGeneratorData.java $ ";

    // constants for the defaulr key table and field names
    public final static String TBL_NEXTKEY  = "TBL_NEXTKEY";
    public final static String FLD_NEXTKEY  = "FLD_NEXTKEY";
    public final static String FLD_MAPTABLE = "FLD_MAPTABLE";

    //--------------------------------------------------------------------------
    /**
     * Constructor to set the defaults.
     * @param mapValue, the value used to look up the correct row in the key
     *   table, usualy the entity name of the data object.
     * @param targetField, the name of the DataObject field that is set using
     *   the generated key.
     */
    public DefaultKeyGeneratorData(String mapValue, String targetField)
    {
        super(TBL_NEXTKEY, FLD_NEXTKEY, FLD_MAPTABLE, mapValue, targetField);
    }
}

//==============================================================================
// end of file DefaultKeyGeneratorData.java
//==============================================================================
