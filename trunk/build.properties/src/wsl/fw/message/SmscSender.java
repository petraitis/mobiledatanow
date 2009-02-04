//==============================================================================
// SmscSender.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.message;

import wsl.fw.util.CKfw;
import wsl.fw.util.Config;
import wsl.fw.util.Util;
import wsl.fw.util.Log;

import sms.Address;
import sms.Message;
import sms.Smpp;
import sms.Constants;
import sms.Binding;
import sms.SMSException;
import utils.socksException;

import java.io.IOException;

import wsl.fw.resource.ResId;

//------------------------------------------------------------------------------
/**
 * Sender to send SMS messages to a SMSC using the SMPP protocol.
 */
public class SmscSender implements Sender
{
    // version tag
    private final static String _ident = "$Date: 2004/01/06 01:49:11 $  $Revision: 1.2 $ "
        + "$Archive: /Framework/Source/wsl/fw/message/SmscSender.java $ ";

    // resources
    public static final ResId ERR_MISSING_PARAMETERS  = new ResId("SmscSender.error.MissingParameters");
    public static final ResId LOG_SMS_HOST1  = new ResId("SmscSender.log.SMSHost1");
    public static final ResId LOG_SMS_HOST2  = new ResId("SmscSender.log.SMSHost2");
    public static final ResId LOG_SMS_HOST3  = new ResId("SmscSender.log.SMSHost3");
    public static final ResId DEBUG_SMS_SENT  = new ResId("SmscSender.debug.SMSSent");

    // data members
    private String _smsHost;
    private int    _smsPort;
    private String _smsSystemId;
    private String _smsSystemType;
    private String _smsPassword;
    private String _smsFromAddress;
    private Smpp   _smsTx = null;

    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public SmscSender()
    {
        // load the SMSC/SMPP params
        _smsHost        = Config.getProp(CKfw.SMSC_SMSSENDER_HOST);
        _smsPort        = Config.getProp(CKfw.SMSC_SMSSENDER_PORT, -1);
        _smsSystemId    = Config.getProp(CKfw.SMSC_SMSSENDER_SYSTEMID);
        _smsSystemType  = Config.getProp(CKfw.SMSC_SMSSENDER_SYSTEMTYPE);
        _smsPassword    = Config.getProp(CKfw.SMSC_SMSSENDER_PASSWORD);
        _smsFromAddress = Config.getProp(CKfw.SMSC_SMSSENDER_HOST);

        // check params
        if (Util.isEmpty(_smsHost) || _smsPort == -1 || Util.isEmpty(_smsSystemId)
            || _smsSystemType == null || _smsPassword == null)
        {
            //String message = "SmscSender, missing SMS "
            //    + "parameters, check the config.";
            String message = ERR_MISSING_PARAMETERS.getText();
            assert false: message;
        }

        //Log.log(getClass().getName() + "starting.\nSMSC host="
        //    + _smsHost + ":" + String.valueOf(_smsPort)
        //    + "\nSystem Id/type=" + _smsSystemId + "/" + _smsSystemType
        //    + "\nSender=" + _smsFromAddress);
        Log.log(getClass().getName() + LOG_SMS_HOST1.getText()
            + _smsHost + ":" + String.valueOf(_smsPort)
            + LOG_SMS_HOST2.getText() + _smsSystemId + "/" + _smsSystemType
            + LOG_SMS_HOST3.getText() + _smsFromAddress);
    }

    //--------------------------------------------------------------------------
    /**
     * @return the Smpp opject to be used to send messages. If necessary a new
     *   one is created and bound.
     */
    protected synchronized Smpp getTx()
        throws IOException, socksException, SMSException
    {
        // if no Tx object create and bind one
        if (_smsTx == null)
        {

            // connect and bind a sms connection for transmission
            _smsTx = new Smpp(_smsHost, _smsPort);
            Binding binding = new Binding(_smsSystemId, _smsSystemType,
                _smsPassword);
            _smsTx.bind(binding);
        }

        return _smsTx;
    }

    //--------------------------------------------------------------------------
    /**
     * If a Smpp Tx object exists it is closed and discarded.
     */
    protected synchronized void freeTx()
    {
        if (_smsTx != null)
        {
            _smsTx.close();
            _smsTx = null;
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Send an SMS message. Implementation must reestablish a connection if the
     * previous one has been closed.
     * @param msg, the message to send.
     * @return null on success, on fail a string describing the failure.
     */
    public String send(wsl.fw.message.Message msg)
    {
        try
        {
            // format the message text
            String messageText = msg.getSubject();
            if (!Util.isEmpty(messageText))
                messageText += ". ";
            messageText += msg.getText();

            // limit message length
            if (messageText.length() > Constants.GSM_ASCII_MESSAGE_LENGTH)
                ;// messageText = messageText.

            // create the to and from addresses
            Address toAddress = new Address(msg.getDestination(),
                Constants.GSM_TON_INTERNATIONAL, Constants.GSM_NPI_E164);
            Address fromAddress = new Address(_smsFromAddress,
                Constants.GSM_TON_INTERNATIONAL, Constants.GSM_NPI_E164);

            // create the message and set the addresses
            sms.Message smsMsg = new Message(toAddress, messageText);
            smsMsg.setFrom(fromAddress);

            synchronized (this)
            {
                // send the message
                getTx().send(smsMsg);
            }

            // success
            Log.debug(DEBUG_SMS_SENT.getText() + " " + msg.getDestination());
            return null;
        }
        catch (Exception e)
        {
            // failure, free the connection as it may be invalid and return
            // the error description
            freeTx();
            return e.toString();
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Close any open connections. Should be called before discarding the
     * SmsSender or exiting the app. May be called at other times to dispose
     * of the current connection. A new connection will be established by send
     * as required.
     */
    public void close()
    {
        // free the Smpp Tx connection
        freeTx();
    }
}

//==============================================================================
// end of file SmscSender.java
//==============================================================================
