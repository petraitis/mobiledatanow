//==============================================================================
// ServletError.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.servlet;

//--------------------------------------------------------------------------
/**
 * Holds information on an error for later display on a generic error page.
 */
public class ServletError
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/servlet/ServletError.java $ ";

    // attributes
    public String    _message;
    public Throwable _exception;

    //--------------------------------------------------------------------------
    /**
     * Constructor.
     * @param message, text describing the error.
     * @param exception, the exceptionwhich caused the error, may be null.
     */
    public ServletError(String message, Throwable exception)
    {
        _message = message;
        _exception = exception;
    }
}

//==============================================================================
// end of file ServletError.java
//==============================================================================
