//==============================================================================
// TypeConversionException.java
// $Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $
// Copyright (c) 2001 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.util;

//------------------------------------------------------------------------------
/**
 * Exception thrown when a type conversion fails.
 */
public class TypeConversionException extends Exception
{
    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public TypeConversionException()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Exception text constructor.
     * @param text, exception text.
     */
    public TypeConversionException(String text)
    {
        super(text);
    }
}

//==============================================================================
// end of file TypeConversionException.java
//==============================================================================
