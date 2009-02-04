//==============================================================================
// SecurityException.java
// $Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.security;

import wsl.fw.exception.MdnException;

//------------------------------------------------------------------------------
/**
 * Exception for security failures, such as invalid login or permission denied.
 */
public class SecurityException extends MdnException
{

    //--------------------------------------------------------------------------
    /**
     * Exception text constructor.
     * @param text, exception text.
     */
    public SecurityException(String text)
    {
        super(text);
    }
}

//==============================================================================
// end of file SecurityException.java
//==============================================================================
