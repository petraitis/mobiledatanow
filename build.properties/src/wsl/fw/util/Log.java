//==============================================================================
// Log.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.util;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import wsl.fw.util.CKfw;
import wsl.fw.resource.ResId;

//------------------------------------------------------------------------------
/**
 * Log class to provide basic logging functions.
 * Log entries are prefixed with a timestamp and severity and written to the
 * log stream created by a LogStreamFactory. The default log stream factory is
 * StderrLogStreamFactory. The default factory may be changed by setting the
 * Config property wsl.fw.util.Log.LSF to the class name of the desired
 * factory. The default logging level can be set with the Config property
 * wsl.fw.util.Log.logLevel.
 *
 * Since it is possible that the Log will be initialized with its defaults
 * before the Config has finished reading the correct settings it may be
 * necessary for some clients of Log to call init() after they have finished
 * setting up Config to ensure the Log uses the desired stream and loglevel.
 */
public class Log
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $"
        + "$Archive: /Framework/Source/wsl/fw/util/Log.java $";

    // resources
    public static final ResId ERR_LOAD_CLASS1  = new ResId("Log.error.LoadClass1");
    public static final ResId ERR_LOAD_CLASS2  = new ResId("Log.error.LoadClass2");

    // constants
    // logging severity levels from lowest to highest
    public final static int DEBUG   = 0;        // debug and trace messages
    public final static int WARNING = 1;        // warnings
    public final static int LOG     = 2;        // Logging of important activity
    public final static int ERROR   = 3;        // errors
    public final static int FATAL   = 4;        // fatal errors
    public final static int NONE    = 5;        // this log level disables log

    private final static int DEFAULT_LOGLEVEL = WARNING;

    // member variables
    private static int         s_logLevel  = DEFAULT_LOGLEVEL;
    private static PrintStream s_logStream = null;
    private static boolean     s_canClose  = false;

    //--------------------------------------------------------------------------
    // Static initializer block.
    // This uses the default factory or one specified in the system properties
    // to set the log stream.
    static
    {
        init();
    }

    //--------------------------------------------------------------------------
    // Private constructor to stop instantiation.
    private Log()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Init function, sets the LogStreamFactory and logLevel from settings
     * in Config. usually called by the static initializer it may be necessary
     * for some clients to call directly if the log is not being inited
     * correctly due to a race between Log and Config, both of which are needed
     * early in the bootstrap sequence.
     */
     public static synchronized void init()
     {
        LogStreamFactory lsf = null;
        Exception factoryLoadException = null;

        // check if the system properties set another lsf factory
        String lsfClassName = Config.getProp(CKfw.LOGSTREAM_FACTORY);
        if (lsfClassName != null)
        {
            try
            {
                // use the class defined in the property
                lsf = (LogStreamFactory) Class.forName(lsfClassName).newInstance();
            }
            catch (Exception e)
            {
                // on failure save the exception for later reporting
                factoryLoadException = e;
            }
        }

        // if no factory use the default StderrLogStreamFactory
        if (lsf == null)
            lsf = new StderrLogStreamFactory();

        // set the log stream factory
        setLogStreamfactory(lsf);

        // now that the log is initialised report any pending exceptions
        if (factoryLoadException != null)
            error(ERR_LOAD_CLASS1.getText() + " [" + lsfClassName
                + "]" + ERR_LOAD_CLASS2.getText(), factoryLoadException);

        // get the logging level
        String logLevel = Config.getProp(CKfw.LOG_LEVEL);
        if (logLevel != null)
            try
            {
                s_logLevel = Integer.parseInt(logLevel);
            }
            catch (NumberFormatException e)
            {
            }
    }

    //--------------------------------------------------------------------------
    /**
     * Set a new LogStreamFactory, the previous logging stream will be flushed,
     * closed if appropriate and replaced with a new one from the supplied
     * factory.
     * @param lsf, the new LogStreamFactory.
     */
    public static synchronized void setLogStreamfactory(LogStreamFactory lsf)
    {
        if (lsf != null)
        {
            // free the old stream if it exists
            if (s_logStream != null)
            {
                s_logStream.flush();
                if (s_canClose)
                    s_logStream.close();
            }

            // set the new stream
            s_logStream = lsf.getLogStream();
            s_canClose = lsf.canClose();
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Set the logging level. Only log messages of the specified level or higher
     * will be output.
     * @param logLevel, the new logging level, DEBUG <= logLevel <= NONE.
     */
    public static synchronized void setLogLevel(int logLevel)
    {
        // set the new log level
        s_logLevel = logLevel;

        // ensure the log level is valid
        if (s_logLevel < DEBUG)
            s_logLevel = DEBUG;
        if (s_logLevel > NONE)
            s_logLevel = NONE;
    }

    //--------------------------------------------------------------------------
    /**
     * @return the current logging level
     */
    static int getLogLevel()
    {
        return s_logLevel;
    }

    //--------------------------------------------------------------------------
    /**
     * Add a log entry with the DEBUG severity.
     * The text is prefixed with a time/date stamp and the severity.
     * @param text the text to add
     */
    public static void debug(String text)
    {
        appendToLog(DEBUG, text, null);
    }

    //--------------------------------------------------------------------------
    /**
     * Add a log entry with the DEBUG severity.
     * The text is prefixed with a time/date stamp and the severity.
     * The text is followed by a stack trace from the supplied Throwable.
     * @param text the text to add
     * @param exceptionForStackTrace the Throwable (or Exception or Error) to use
     *   to produce the stack trace.
     */
    public static void debug(String text, Throwable exceptionForStackTrace)
    {
        appendToLog(DEBUG, text, exceptionForStackTrace);
    }

    //--------------------------------------------------------------------------
    /**
     * As above, but severity is WARNING.
     */
    public static void warning(String text)
    {
        appendToLog(WARNING, text, null);
    }

    //--------------------------------------------------------------------------
    /**
     * As above, but severity is WARNING.
     */
    public static void warning(String text, Throwable exceptionForStackTrace)
    {
        appendToLog(WARNING, text, exceptionForStackTrace);
    }

    //--------------------------------------------------------------------------
    /**
     * As above, but severity is LOG, this is for logging, not errors.
     */
    public static void log(String text)
    {
        appendToLog(LOG, text, null);
    }

    //--------------------------------------------------------------------------
    /**
     * as above, but severity is LOG, this is for logging, not errors
     */
    public static void log(String text, Throwable exceptionForStackTrace)
    {
        appendToLog(LOG, text, exceptionForStackTrace);
    }

    //--------------------------------------------------------------------------
    /**
     * As above, but severity is ERROR.
     */
    public static void error(String text)
    {
        appendToLog(ERROR, text, null);
    }

    //--------------------------------------------------------------------------
    /**
     * As above, but severity is ERROR.
     */
    public static void error(String text, Throwable exceptionForStackTrace)
    {
        appendToLog(ERROR, text, exceptionForStackTrace);
    }

    //--------------------------------------------------------------------------
    /**
     * As above, but severity is FATAL.
     */
    public static void fatal(String text)
    {
        appendToLog(FATAL, text, null);
    }

    //--------------------------------------------------------------------------
    /**
     * As above, but severity is FATAL.
     */
    public static void fatal(String text, Throwable exceptionForStackTrace)
    {
        appendToLog(FATAL, text, exceptionForStackTrace);
    }

    //--------------------------------------------------------------------------
    /**
     * Add an entry to the log. The entry is prefixed with a timestamp and the
     * severity. A stacktrace is appended if a Throwable is supplied.
     * @param severity, the log message severity.
     * @param text, the log message text.
     * @param exceptionForStackTrace, may be null, used to add a stack trace.
     */
    private static synchronized void appendToLog(int severity, String text,
        Throwable exceptionForStackTrace)
    {
        // only output if the severity is high enough for  the logging level
        if (severity >= s_logLevel)
        {
            // the logger should never throw exceptions, just fail quietly
            try
            {
                // log timestamp, severity and text
                s_logStream.println(getTimestamp() + " "
                    + getSeverityText(severity) + " " + text);
                // log the stacktrace
                if (exceptionForStackTrace != null)
                    exceptionForStackTrace.printStackTrace(s_logStream);

                // flush to ensure data is written to stream
                s_logStream.flush();
            }
            catch (Exception e)
            {
                // discard exceptions
            }
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Get a string describing the severity.
     * @param severity, the severity (one of DEBUG, WARNING, ERROR or FATAL).
     * @return the severity as a string.
     */
    private static String getSeverityText(int severity)
    {
        switch (severity)
        {
            case DEBUG   : return "DEBUG  ";
            case WARNING : return "WARNING";
            case LOG     : return "LOG";
            case ERROR   : return "ERROR  ";
            case FATAL   : return "FATAL  ";
        }

        // default case, unknown severity
        return "UNKNOWN SEVERITY";
    }

    //--------------------------------------------------------------------------
    /**
     * Create a date/time stamp in YYYY/MM/DD HH:MM:SS format.
     */
    private static String getTimestamp()
    {
        SimpleDateFormat df = new SimpleDateFormat ("yyyy/MM/dd hh:mm:ss");
        Date currentTime = new Date();
        return df.format(currentTime);
    }
}

//==============================================================================
// end of file Log.java
//==============================================================================
