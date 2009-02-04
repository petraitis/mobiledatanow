//==============================================================================
// LogStreamFactory.java
// $Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.util;

import java.io.PrintStream;

//------------------------------------------------------------------------------
/**
 * LogStreamFactory is an interface that must be implemented by factories that
 * create logging streams for use by wsl.fw.util.Log.
 */
public interface LogStreamFactory
{
    //--------------------------------------------------------------------------
    /**
     * Gets a PrintStream for the Log.
     * @return the PrinStream that log messages are written to.
     */
    public PrintStream getLogStream();

    //--------------------------------------------------------------------------
    /**
     * Check if the Log is permitted to close the PrintStream supplied by
     * getLogStream. Some streams used for logging, notably System.err and
     * System.out are shared system resources that the logger should not close.
     * @return true if the Log is permitted to close the stream.
     */
    public boolean canClose();
}

//==============================================================================
// end of file LogStreamFactory.java
//==============================================================================
