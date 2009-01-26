package com.framedobjects.dashwell.tests.smpp;

import ie.omk.smpp.Connection;
import ie.omk.smpp.event.ReceiverExitEvent;
import ie.omk.smpp.event.SMPPEventAdapter;
import ie.omk.smpp.message.BindResp;
import ie.omk.smpp.message.DeliverSM;
import ie.omk.smpp.message.Unbind;
import ie.omk.smpp.message.UnbindResp;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Example SMPP receiver using asynchronous communications. This example
 * demonstrates asynchronous communications using the SMPPEventAdapter. The
 * SMPPEventAdapter is a utility class which implements the ConnectionObserver
 * interface for you and delivers received events to appropriate methods in the
 * adapter implementation.
 * 
 * @see ie.omk.smpp.examples.ParseArgs ParseArgs for details on running this
 *      class.
 */
public class AsyncReceiver2{// extends SMPPAPIExample 

    private Log logger = LogFactory.getLog(AsyncReceiver2.class);

    // time example started at
    public static long start = 0;

    // time example ended at
    public static long end = 0;

    // Number of deliver_sm packets received
    public static int msgCount = 0;
    
    public static Connection myConnection; 

    public AsyncReceiver2() {
    }

    // Print out a report
    private void endReport() {
        logger.info("deliver_sm's received: " + msgCount);
        logger.info("Start time: " + new Date(start).toString());
        logger.info("End time: " + new Date(end).toString());
        logger.info("Elapsed: " + (end - start) + " milliseconds.");
    }

    public static void main(String args[]) {
    	
        try {
            //myConnection = new Connection("194.247.82.149", 8011, true);//HSL
            myConnection = new Connection("208.106.182.84", 9500, true);//Mexican


            // bind to the SMSC as a receiver

            //synchronized (this) {
    		boolean retry = false;
    		
    		while (!retry) {
    			//myConnection.bind(Connection.RECEIVER, "8888888501", "ddadee3d", "SMPP", 1, 1, null);//hsl
    			myConnection.bind(Connection.TRANSCEIVER, "tester2139", "PXTMDKYO", "SMPP", 1, 1, null); //mexican
    			
                // Create the observer
                AsyncExampleObserver observer = new AsyncExampleObserver();

                // set the receiver to automatically acknowledge deliver_sm and
                // enquire_link requests from the SMSC.
                myConnection.autoAckLink(true);
                myConnection.autoAckMessages(true);

                // add this example to the list of observers on the receiver
                // connection
                myConnection.addObserver(observer);
    			
                retry = true;
    		}

                //wait();
            //}

            end = System.currentTimeMillis();
            
            // Close down the network connection.
            myConnection.closeLink();
        } catch (Exception x) {
            //throw new BuildException("Exception running example: " + x.getMessage(), x);
        } 
//        finally {
//            endReport();
//        }
    }

    public static class AsyncExampleObserver extends SMPPEventAdapter {

        public AsyncExampleObserver() {
        }

        // Handle message delivery. This method does not need to acknowledge the
        // deliver_sm message as we set the Connection object to
        // automatically acknowledge them.
        public void deliverSM(Connection source, DeliverSM dm) {
            int st = dm.getCommandStatus();

            if (st != 0) {
                System.out.println("DeliverSM: !Error! status = " + st);
            } else {
                ++msgCount;
                System.out.println("@@@@@@@@@ DeliverSM: \"" + dm.getMessageText() + "\"");
            }
        }

        // Called when a bind response packet is received.
        public void bindResponse(Connection source, BindResp br) {
//            synchronized (AsyncReceiver2.this) {
                // on exiting this block, we're sure that
                // the main thread is now sitting in the wait
                // call, awaiting the unbind request.
            	System.out.println("Bind response received.");
//           }
            if (br.getCommandStatus() == 0) {
            	System.out.println("Successfully bound. Awaiting messages..");
            } else {
            	System.out.println("Bind did not succeed!");
                try {
                    myConnection.closeLink();
               } catch (IOException x) {
            	   System.out.println("IOException closing link:\n" + x.toString());
               }
            }
        }

        // This method is called when the SMSC sends an unbind request to our
        // receiver. We must acknowledge it and terminate gracefully..
        public void unbind(Connection source, Unbind ubd) {
        	System.out.println("SMSC requested unbind. Acknowledging..");

            try {
                // SMSC requests unbind..
                UnbindResp ubr = new UnbindResp(ubd);
                myConnection.sendResponse(ubr);
            } catch (IOException x) {
            	System.out.println("IOException while acking unbind: " + x.toString());
            }
        }

        // This method is called when the SMSC responds to an unbind request we
        // sent
        // to it..it signals that we can shut down the network connection and
        // terminate our application..
        public void unbindResponse(Connection source, UnbindResp ubr) {
            int st = ubr.getCommandStatus();

            if (st != 0) {
            	System.out.println("Unbind response: !Error! status = " + st);
            } else {
            	System.out.println("Successfully unbound.");
            }
        }

        // this method is called when the receiver thread is exiting normally.
        public void receiverExit(Connection source, ReceiverExitEvent ev) {
            if (ev.getReason() == ReceiverExitEvent.BIND_TIMEOUT) {
            	System.out.println("Bind timed out waiting for response.");
            }

            System.out.println("Receiver thread has exited.");
            //synchronized (AsyncReceiver2.this) {
                //AsyncReceiver2.this.notify();
            //}
        }

        // this method is called when the receiver thread exits due to an
        // exception
        // in the thread...
        public void receiverExitException(Connection source,
                ReceiverExitEvent ev) {
        	System.out.println("Receiver thread exited abnormally. The following"
                            + " exception was thrown:\n"
                            + ev.getException().toString());
            //synchronized (AsyncReceiver2.this) {
              //  AsyncReceiver2.this.notify();
            //}
        }

    }
}