//==============================================================================
// SmtpSender.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.message;

import wsl.fw.util.CKfw;
import wsl.fw.util.Config;
import wsl.fw.util.Util;
import wsl.fw.util.Log;

import java.util.Date;
import java.util.Properties;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.AddressException;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import wsl.fw.resource.ResId;

//------------------------------------------------------------------------------
/**
 * Sender to send E-mail messages  using the SMTP protocol.
 */
public class SmtpSender implements Sender
{
    // version tag
    private final static String _ident = "$Date: 2004/01/06 01:49:11 $  $Revision: 1.2 $ "
        + "$Archive: /Framework/Source/wsl/fw/message/SmtpSender.java $ ";

    // resources
    public static final ResId ERR_PARAMETER_MISSING  = new ResId("SmtpSender.error.ParameterMissing");
    public static final ResId LOG_STARTING1  = new ResId("SmtpSender.log.Starting1");
    public static final ResId LOG_STARTING2  = new ResId("SmtpSender.log.Starting2");
    public static final ResId ERR_PARSE  = new ResId("SmtpSender.error.Parse");
    public static final ResId EXCEPTION_DESTINATION1  = new ResId("SmtpSender.exception.Destination1");
    public static final ResId EXCEPTION_DESTINATION2  = new ResId("SmtpSender.exception.Destination2");
    public static final ResId DEBUG_EMAIL_SENT  = new ResId("SmtpSender.debug.EmailSent");
    public static final ResId EXCEPTION_MESSAGE  = new ResId("SmtpSender.exception.Message");

    // data members
    private Session         _emailSession = null;
    private InternetAddress _emailFrom    = null;

    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public SmtpSender()
    {
        try
        {
            // get SMTP email Config entries
            String smtpHost = Config.getProp(CKfw.SMTP_EMAILSENDER_HOST);
            String senderAddress = Config.getProp(CKfw.SMTP_EMAILSENDER_FROM);

            assert !Util.isEmpty(smtpHost) && !Util.isEmpty(senderAddress):
                ERR_PARAMETER_MISSING.getText();

            _emailFrom = new InternetAddress(senderAddress);

            // make the email session
            Properties props = new Properties();
            props.put("mail.smtp.host", smtpHost);
            _emailSession = Session.getDefaultInstance(props, null);

            //Log.log(getClass().getName() + "starting.\nSMTP host="
            //    + smtpHost + "\nSender=" + senderAddress);
            Log.log(getClass().getName() + LOG_STARTING1.getText()
                + smtpHost + LOG_STARTING2.getText() + senderAddress);
        }
        catch (AddressException e)
        {
            String message = ERR_PARSE.getText()
                + " " + CKfw.SMTP_EMAILSENDER_FROM
                + ", " + e.toString();
            assert false: message;
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Send a message. Implementation must reestablish a connection if the
     * previous one has been closed.
     * @param msg, the message to send.
     * @return null on success, on fail a string describing the failure.
     */
    public String send(Message msg)
    {
        // construct an email MimeMessage and set the fields
        MimeMessage emailMsg = new MimeMessage(_emailSession);

        // parse the destination address
        try
        {
            InternetAddress[] toAddress = InternetAddress.parse(msg.getDestination());
            emailMsg.setRecipients(MimeMessage.RecipientType.TO, toAddress);
        }
        catch (Exception e)
        {
            //return "Message=" + msg.getId().toString() + ", could not parse "
            //    + "the destination [" + msg.getDestination() + "] : "
            return EXCEPTION_DESTINATION1.getText() + msg.getId().toString() + EXCEPTION_DESTINATION2.getText()
                + " [" + msg.getDestination() + "] : "
                + e.toString();
        }

        try
        {
            // set the rest of the message params
            emailMsg.setFrom(_emailFrom);
            emailMsg.setSubject(msg.getSubject());
            emailMsg.setSentDate(new Date());
            emailMsg.setText(msg.getText());

            // send the message, the smtp transport details are already configured
            // in the session that was passed to the message constructor
            Transport.send(emailMsg);

            // success
            Log.debug(DEBUG_EMAIL_SENT.getText() + " " + msg.getDestination());
            return null;
        }
        catch (Exception e)
        {
            // failure
            return EXCEPTION_MESSAGE.getText() + msg.getId().toString() + ": " + e.toString();
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Close any open connections. Should be called before discarding the
     * Sender or exiting the app. May be called at other times to dispose
     * of the current connection. A new connection will be established by send
     * as required.
     */
    public void close()
    {
        // nothing to do, not holding onto the connection
    }
}

//==============================================================================
// end of file SmtpSender.java
//==============================================================================
