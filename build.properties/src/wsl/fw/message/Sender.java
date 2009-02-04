//==============================================================================
// Sender.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.message;

//------------------------------------------------------------------------------
/**
 * Interface for objects that can send messages to the customer.
 * Concrete implementation classes use a specific protocol to send the actual
 * message. The implementation should implement a default constructor that loads
 * any required info from Config. The send function can be called multiple
 * times. The close function should be called before discarding the Sender or
 * exiting the app.
 */
public interface Sender
{
    //--------------------------------------------------------------------------
    /**
     * Send a message. Implementation must reestablish a connection if the
     * previous one has been closed.
     * @param msg, the message to send.
     * @return null on success, on fail a string describing the failure.
     */
    public String send(Message msg);

    //--------------------------------------------------------------------------
    /**
     * Close any open connections. Should be called before discarding the
     * Sender or exiting the app. May be called at other times to dispose
     * of the current connection. A new connection will be established by send
     * as required.
     */
    public void close();
}

//==============================================================================
// end of file Sender.java
//==============================================================================
