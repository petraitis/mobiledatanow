//==============================================================================
// StderrLogStreamFactory.java
// $Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.util;

import java.io.PrintStream;

//------------------------------------------------------------------------------
/**
 * A LogStreamFactory that returns System.err as the log stream.
 */
public class StderrLogStreamFactory implements LogStreamFactory
{
    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public StderrLogStreamFactory()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Gets System.err as the PrintStream for the Log.
     */
    public PrintStream getLogStream()
    {
        return System.err;
    }

    //--------------------------------------------------------------------------
    /**
     * Since System.err is shared tell the Log not to close it
     */
    public boolean canClose()
    {
        return false;
    }
}

//==============================================================================
// end of file StderrLogStreamFactory.java
//==============================================================================
