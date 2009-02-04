//==============================================================================
// CKfw.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.util;

//------------------------------------------------------------------------------
/**
 * Class to hold constants defining key names used to access framework
 * properties in Config. The value of these constants may be used in a .conf
 * file or on the command line (with -D) to define the porperty, or with
 * Config.getProp() to retrieve the property.
 * All constants should be public final static String.
 */
public class CKfw
{
    // Incomplete config keys (prefixes, sufixes and parts)

    /** dot character usedfor building structured key names */
    public final static char DOT = '.';

    /** prefix for automatically loaded config URLs */
    public final static String CONFIG_NAME_PREFIX  = "wsl.configfile.";

    /** prefix for automatically loaded config URLs */
    public final static String DOLAST_CONFIG_NAME_PREFIX  = "wsl.doLastConfigfile.";

    /** prefix for optionally loaded contexts */
    public final static String CONTEXT_PREFIX = "wsl.configcontext.";

    /** prefix for BootstrapSecurityManager entries */
    public final static String BOOTSEC_PREFIX = "BootstrapSecurityManager.entity";

    /** prefix for RmiServer servant port ranges */
    public final static String RMISERVANT_PORTRANGE_PREFIX = "RmiServer.ServantPortRange.";

    /** prefix for servlet command line args, suffix is numeric 1..n */
    public final static String SERVLET_COMMANDLINE_PREFIX = "wsl.fw.servlet.commandline.";

    /** prefix for message types, used by MessageSender, note: no dot */
    public final static String MESSAGE_TYPE_PREFIX = "wsl.fw.message.type";

    /** suffix for unicode conversion types, semi-colon delimited */
    public final static String UNICODE_CONVERT_TYPE_SUFFIX = ".unicodeConvertTypes";

    //--------------------------------------------------------------------------
    /** log stream factory, a package.class name */
    public final static String LOGSTREAM_FACTORY = "Log.logStreamFactory";

    /** logging level, integer, one of the values from Log */
    public final static String LOG_LEVEL = "Log.logLevel";

    /** the host machine running the secure registry, a machine name or IP */
    public final static String SECREG_HOST = "SecureRegistry.host";

    /** the port for the bootstrap registry used by the secure registry */
    public final static String SECREG_BOOTSTRAP_PORT = "SecureRegistry.bootstrapPort";

    /** the port the secure registry listens on */
    public final static String SECREG_PORT = "SecureRegistry.port";

    /** system ID username, used programatically to access privileged systems */
    public final static String SYSTEMID_NAME     = "SecurityId.systemId.name";

    /** system ID password */
    public final static String SYSTEMID_PASSWORD = "SecurityId.systemId.password";

    /** RemoteDataManager to be used by DataManager */
    public final static String REMOTE_DATAMANAGER = "DataManager.RemoteDataManager";

    /** CustomPresentation classes, delimited by ";" */
    public final static String CUSTOM_PRESENTATIONS = "CustomPresentation.presentationClasses";

    // config key constants for JdbcDataSource (used by DataManager for system datasource)
    /** jdbc driver class */
    public final static String JDBC_DS_DRIVER = "JdbcDataSource.driver";
    /** url of jdbc database */
    public final static String JDBC_DS_URL = "JdbcDataSource.url";
    /** catalog of the jdbc database */
    public final static String JDBC_DS_CATALOG = "JdbcDataSource.catalog";
    /** user to log into the jdbc database */
    public final static String JDBC_DS_USER = "JdbcDataSource.user";
    /** password to log into the jdbc database */
    public final static String JDBC_DS_PASSWORD = "JdbcDataSource.password";

    // config key constants for ConectionPoolManager (used by Datamanager for default pools)
    /** Max size of pool, applies for each pool separately */
    public final static String POOL_MANAGER_POOLSIZE = "ConnectionPoolManager.poolSize";
    /** liftime (in MS) of a connection before it is closed */
    public final static String POOL_MANAGER_MSLIFETIME = "ConnectionPoolManager.msLifetime";
    /** Max time (in MS) to wait for a free connection before timing out */
    public final static String POOL_MANAGER_MSGETTIMEOUT = "ConnectionPoolManager.msGetTimeout";

    // config key constants for system datasource pool settings
    /** Max size of pool */
    public final static String SYSTEM_DATASOURCE_POOLSIZE = "DataManager.SystemDataSource.poolSize";
    /** liftime (in MS) of a connection before it is closed */
    public final static String SYSTEM_DATASOURCE_MSLIFETIME = "DataManager.SystemDataSource.msLifetime";
    /** Max time (in MS) to wait for a free connection before timing out */
    public final static String SYSTEM_DATASOURCE_MSGETTIMEOUT = "DataManager.SystemDataSource.msGetTimeout";


    /** millisecond rebind period for RMI servers */
    public final static String RMISERVER_MS_REBIND = "wsl.fw.remote.RMIServer.msRebind";

    /** period between retries (max 3) when trying to load a Message for forwarding */
    public final static String MESSAGELISTENER_MS_LOAD_RETRY = "wsl.fw.message.MessageListenerBase.msLoadRetry";

    /** millisecond period between old msg sweeps by EmailSMSMessageListener */
    public final static String EMAILSMSLISTENER_MS_SWEEP  = "wsl.fw.message.EmailSmsMessageListener.msSweep";

    /** the class to instantiate for the SMS Sender */
    public final static String EMAILSMSLISTENER_SMS_SENDERCLASS = "wsl.fw.message.EmailSmsMessageListener.sms.senderClass";
    /** the class to instantiate for the E-mail Sender */
    public final static String EMAILSMSLISTENER_EMAIL_SENDERCLASS = "wsl.fw.message.EmailSmsMessageListener.email.senderClass";

    /** SMTP host for sending email */
    public final static String SMTP_EMAILSENDER_HOST = "wsl.fw.message.SmtpSender.host";
    /** SMTP sender address for email */
    public final static String SMTP_EMAILSENDER_FROM = "wsl.fw.message.SmtpSender.from";

    /** the IP of the host SMSC for sending SMS messages */
    public final static String SMSC_SMSSENDER_HOST       = "wsl.fw.message.SmscSender.host";
    /** the port of the host SMSC for sending SMS messages */
    public final static String SMSC_SMSSENDER_PORT       = "wsl.fw.message.SmscSender.port";
    /** SMSC SMS system id */
    public final static String SMSC_SMSSENDER_SYSTEMID   = "wsl.fw.message.SmscSender.systemId";
    /** SMSC SMS system type */
    public final static String SMSC_SMSSENDER_SYSTEMTYPE = "wsl.fw.message.SmscSender.systemType";
    /** SMSC SMS login password */
    public final static String SMSC_SMSSENDER_PASSWORD   = "wsl.fw.message.SmscSender.password";
    /** Sender (from) address for SMSC SMS messages */
    public final static String SMSC_SMSSENDER_FROM       = "wsl.fw.message.SmscSender.from";

    // message type key constants
    public final static String MESSAGE_TYPE_HOME_PHONE   = MESSAGE_TYPE_PREFIX + DOT + "homePhone";
    public final static String MESSAGE_TYPE_MOBILE_PHONE = MESSAGE_TYPE_PREFIX + DOT + "mobilePhone";
    public final static String MESSAGE_TYPE_EMAIL        = MESSAGE_TYPE_PREFIX + DOT + "email";
    public final static String MESSAGE_TYPE_SMS          = MESSAGE_TYPE_PREFIX + DOT + "sms";
    public final static String MESSAGE_TYPE_DISABLED     = MESSAGE_TYPE_PREFIX + DOT + "disabled";

    //--------------------------------------------------------------------------
    // context names

    /** context for SecureRegistry permission data */
    public final static String SECREG_CONTEXT = "wsl.fw.remote.SecureRegistry";

    /** context for RmiServers */
    public final static String RMISERVER_CONTEXT = "wsl.fw.remote.RmiServer";

    /** context for RMI clients */
    public final static String RMICLIENT_CONTEXT = "wsl.fw.remote.RmiClient";

    /** context for Message Listeners */
    public final static String MESSAGELISTENER_CONTEXT = "wsl.fw.Message.MessageListenerBase";

    //--------------------------------------------------------------------------
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/util/CKfw.java $ ";

    //--------------------------------------------------------------------------
    /**
     * Private constructor to stop instantiation.
     */
    private CKfw()
    {
    }
}

//==============================================================================
// end of file CKfw.java
//==============================================================================
