//==============================================================================
// KeyGeneratorData.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.datasource;

import java.io.Serializable;

/**
 * Structure class to encapsulate information required by a key generator
 */
public class KeyGeneratorData implements Serializable
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/datasource/KeyGeneratorData.java $ ";

    /**
     * key generator types
     */
    public static final int KG_KEY_TABLE = 1;

    /**
     * attributes
     */
    public String _keyTable;
    public String _keyColumn;
    public String _mapColumn;
    public String _mapValue;
    public String _targetField;

    /**
     * Constructor
     * @param keyTable the name of the key table
     * @param keyColumn the name of the column where the next key is
     * @param mapColumn the name of the column with the map value in it
     * @param mapValue the map value used to find the next key
     * @param targetField the DataObject field for which the key is destined
     */
    public KeyGeneratorData(String keyTable, String keyColumn, String mapColumn, String mapValue, String targetField)
    {
        // set attribs
        _keyTable = keyTable;
        _keyColumn = keyColumn;
        _mapColumn = mapColumn;
        _mapValue = mapValue;
        _targetField = targetField;
    }
}

//==============================================================================
// end of file KeyGeneratorData.java
//==============================================================================
