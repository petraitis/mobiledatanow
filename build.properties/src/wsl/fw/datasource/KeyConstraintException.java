//==============================================================================
// KeyConstraintException.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.datasource;

import wsl.fw.resource.ResId;

//------------------------------------------------------------------------------
/**
 * An exception to be thrown by the DataObject/DataSource when an insert or
 * update operation results ina key collission.
 */
public class KeyConstraintException extends DataSourceException
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/datasource/KeyConstraintException.java $ ";

    // resources
    public static final ResId KEY_CONSTRAINT_MESSAGE  = new ResId("KeyConstraintException.msg.KeyConstraint");

    //public final static String KEY_CONSTRAINT_MESSAGE = "Could not save record,"
    //    + " another record with the same key already exists";

    //--------------------------------------------------------------------------
    /**
     * Default constructor, the message is set to indicate a key constraint
     * error.
     */
    public KeyConstraintException()
    {
        //super(KEY_CONSTRAINT_MESSAGE);
        super(KEY_CONSTRAINT_MESSAGE.getText());
    }

    //--------------------------------------------------------------------------
    /**
     * Constructor taking additional message text.
     * @param additionalText, additional message text that is appended to the
     *   default message.
     */
    public KeyConstraintException(String additionalText)
    {
        //super(KEY_CONSTRAINT_MESSAGE + " " + additionalText);
        super(KEY_CONSTRAINT_MESSAGE.getText() + " " + additionalText);
    }
}

//==============================================================================
// end of file KeyConstraintException.java
//==============================================================================
