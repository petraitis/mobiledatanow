//==============================================================================
// EmailSmsMessageListener.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.message;

import wsl.fw.datasource.DataManager;
import wsl.fw.util.CKfw;
import wsl.fw.util.Config;
import wsl.fw.util.Log;
import wsl.fw.util.Util;
import java.util.Vector;
import wsl.fw.resource.ResId;
import wsl.fw.resource.ResourceManager;

//------------------------------------------------------------------------------
/**
 * Listener (handler) for email and SMS messages. Receives the message, sends it
 * to the destination using email or sms, then removes the Message from the DB.
 */
public class EmailSmsMessageListener extends MessageListenerBase
{
    // version tag
    private final static String _ident = "$Date: 2004/01/06 01:49:11 $  $Revision: 1.2 $ "
        + "$Archive: /Framework/Source/wsl/fw/message/EmailSmsMessageListener.java $ ";

    // resources
    public static final ResId ERR_NO_EMAIL  = new ResId("EmailSmsMessageListener.error.NoEmail");
    public static final ResId ERR_EMAIL_SENDER  = new ResId("EmailSmsMessageListener.error.EmailSender");
    public static final ResId ERR_SMS_SENDER  = new ResId("EmailSmsMessageListener.error.SMSSender");
    public static final ResId WARNING_UNEXPECTED_TYPE  = new ResId("EmailSmsMessageListener.warning.UnexpectedType");
    public static final ResId WARNING_SEND_SMS  = new ResId("EmailSmsMessageListener.warning.SendSMS");
    public static final ResId WARNING_SEND_EMAIL  = new ResId("EmailSmsMessageListener.warning.SendEmail");

    // members
    private String          _mtEmail;
    private String          _mtSMS;
    private String          _messageTypes[];

    private Sender          _smsSender    = null;
    private Sender          _emailSender  = null;

    //--------------------------------------------------------------------------
    /**
     * Constructor.
     * @param args, command line args to be passed to base class.
     */
    public EmailSmsMessageListener(String args[])
    {
        // call superclass
        super(args);

        // load the message types from config
        _mtEmail = Config.getProp(CKfw.MESSAGE_TYPE_EMAIL);
        _mtSMS   = Config.getProp(CKfw.MESSAGE_TYPE_SMS);

        // put in a suitable sized array
        Vector v = new Vector();
        if (_mtEmail != null)
            v.add(_mtEmail);
        if (_mtSMS != null)
            v.add(_mtSMS);
        _messageTypes = (String[]) v.toArray(new String[0]);

        // if no message types to listen to then fail
        assert _messageTypes.length > 0:
            ERR_NO_EMAIL.getText();

        // if email listening is active then load the Email Sender
        if (_mtEmail != null)
        {
            String emailSenderClass = Config.getProp(CKfw.EMAILSMSLISTENER_EMAIL_SENDERCLASS);

            try
            {
                _emailSender = (Sender) Class.forName(emailSenderClass).newInstance();
            }
            catch (Exception e)
            {
                assert false: ERR_EMAIL_SENDER.getText() + " " + e.toString();
            }
        }

        // if sms listening is active then load SMS Sender
        if (_mtSMS != null)
        {
            String smsSenderClass = Config.getProp(CKfw.EMAILSMSLISTENER_SMS_SENDERCLASS);

            try
            {
                _smsSender = (Sender) Class.forName(smsSenderClass).newInstance();
            }
            catch (Exception e)
            {
                assert false: ERR_SMS_SENDER.getText() + " " + e.toString();
            }
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Override to get the type of DataManager to create.
     * @return the class of the desired data manager, a Class object that
     *   specifies DataManager or a subclass.
     */
    protected Class getDataManagerClass()
    {
        return DataManager.class;
    }

    //--------------------------------------------------------------------------
    /**
     * Virtual to be overridden by concrete subclasses to get the message types
     * that this Message Listener is to handle.
     * @return an array of Strings defining the desired message types.
     */
    protected String[] getMessageTypes()
    {
        return _messageTypes;
    }

    //--------------------------------------------------------------------------
    /**
     * Virtual to be overridden by concrete subclasses to handle the processing
     * of a message. Uses lock and unlock to avoid
     * possible thread collission.
     * @param msgType, the type of message.
     * @param msg, the Message.
     * @return one of the OM_ constants that indicate whether the message has
     *   been processed and how it should be updated in the DB.
     */
    protected int onMessage(String msgType, wsl.fw.message.Message msg)
    {
        int rv = OM_NOT_PROCESSED;

        // try to process the message
        if (_mtEmail != null && _mtEmail.equals(msgType))
            rv = processEmail(msg);
        else if (_mtSMS != null && _mtSMS.equals(msgType))
            rv = processSMS(msg);
        else
            //Log.warning("EmailSMSMessageListener.onMessage, recieved an "
            //    + "unexpected message type: " + msgType);
            Log.warning(WARNING_UNEXPECTED_TYPE.getText() + " " + msgType);

        return rv;
    }

    //--------------------------------------------------------------------------
    /**
     * Process a SMS message.
     * @param msg, the message to process (send).
     * @return one of the OM_ constants that indicate whether the message has
     *   been processed and how it should be updated in the DB.
     */
    protected int processSMS(wsl.fw.message.Message msg)
    {
        Util.argCheckNull(msg);

        String rv = _smsSender.send(msg);

        if (rv == null)
        {
            // success
            return OM_PROCESSED;
        }
        else
        {
            // failure
            //Log.warning("EmailSMSMessageListener.processSMS, failed to send "
            //    + "SMS message: " + rv);
            Log.warning(WARNING_SEND_SMS.getText() + " " + rv);
            return OM_FAILED;
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Process an Email message.
     * @param msg, the message to process (send).
     * @return one of the OM_ constants that indicate whether the message has
     *   been processed and how it should be updated in the DB.
     */
    protected int processEmail(wsl.fw.message.Message msg)
    {
        Util.argCheckNull(msg);

        String rv = _emailSender.send(msg);

        if (rv == null)
        {
            // success
            return OM_PROCESSED;
        }
        else
        {
            // failure
            //Log.warning("EmailSMSMessageListener.processEmail, failed to send "
            //    + "E-mail message: " + rv);
            Log.warning(WARNING_SEND_EMAIL.getText() + " " + rv);
            return OM_FAILED;
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Close the Senders.
     */
    protected void closeSenders()
    {
        if (_emailSender != null)
            _emailSender.close();
        if (_smsSender != null)
            _smsSender.close();
    }

    //--------------------------------------------------------------------------
    /**
     * Main entrypoint.
     * @param args, command line arguments.
     */
    public static void main(String args[])
    {
        // set the resource manager
        ResourceManager.set(new ResourceManager());

        // create the listener, this also loads config and data manager
        EmailSmsMessageListener listener = new EmailSmsMessageListener(args);

        // get sweep period from config, default is 10 minutes
        int msSweepPeriod = Config.getProp(CKfw.EMAILSMSLISTENER_MS_SWEEP , 600000);

        // start the listener
        listener.startListening();

        // loop checking for deferred messages, exit if interrupted
        try
        {
            while (true)
            {
                Thread.sleep(msSweepPeriod);
                listener.pingDeferred(0);
            }
        }
        catch (InterruptedException e)
        {
        }

        // stop the listener
        listener.stopListener();

        // close the senders
        listener.closeSenders();
    }
}

//==============================================================================
// end of file EmailSmsMessageListener.java
//==============================================================================
