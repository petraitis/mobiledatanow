//==============================================================================
// AssertFailedException.java
// $Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.util;

//------------------------------------------------------------------------------
/**
 * Exception thrown by assert failures
 */
public class AssertFailedException extends RuntimeException
{
    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public AssertFailedException()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Exception text constructor.
     * @param text, exception text.
     */
    public AssertFailedException(String text)
    {
        super(text);
    }
}

//==============================================================================
// end of file AssertFailedException.java
//==============================================================================
