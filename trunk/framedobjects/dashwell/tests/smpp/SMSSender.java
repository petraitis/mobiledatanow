package com.framedobjects.dashwell.tests.smpp;

import javax.comm.CommDriver;

import org.smslib.CIncomingCall;
import org.smslib.CIncomingMessage;
import org.smslib.CMessage;
import org.smslib.COutgoingMessage;
import org.smslib.CService;
import org.smslib.ICallListener;
import org.smslib.ISmsMessageListener;

public class SMSSender {
	public static void main(String args[]) {
		CService service = new CService("COM1", 9600, "JANUS", "GSM864Q");
		
		String driverName = "com.sun.comm.Win32Driver"; // or get as a JNLP property
		CommDriver commDriver;
		try {
			commDriver = (CommDriver)Class.forName(driverName).newInstance();
			commDriver.initialize();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try
		{
			
			service.connect();
			CCallListener2 callListener = new CCallListener2();
			service.setCallHandler(callListener);

			CMessageListener2 smsMessageListener = new CMessageListener2();
			service.setMessageHandler(smsMessageListener);

			// Set the polling interval in seconds.
			service.setAsyncPollInterval(10);

			// Set the class of the messages to be read.
			service.setAsyncRecvClass(CIncomingMessage.MessageClass.All);

			// Switch to asynchronous POLL mode.
			try {
				service.setReceiveMode(CService.ReceiveMode.AsyncPoll);
			} catch (Exception e) {
				e.printStackTrace();
			}			

			System.out.println("Connected!" );
			
			for(;;){
				COutgoingMessage msg1 = new COutgoingMessage("447624804279", "a 7");
				msg1.setMessageEncoding(CMessage.MessageEncoding.Enc7Bit);
				msg1.setStatusReport(true);
				msg1.setValidityPeriod(8);
				service.sendMessage(msg1);
				System.out.println("Send SMS!");
				
				COutgoingMessage msg2 = new COutgoingMessage("447624804279", "ch");
				msg2.setMessageEncoding(CMessage.MessageEncoding.Enc7Bit);
				msg2.setStatusReport(true);
				msg2.setValidityPeriod(8);
				service.sendMessage(msg2);				
			}
			
			//srv.disconnect();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		System.exit(0);		
	}
	private static class CMessageListener2 implements ISmsMessageListener
	{
		public boolean received(CService service, CIncomingMessage message)
		{
			String mobileNumber = message.getOriginator();
			System.out.println(">>>>>> This Message received ( " + message.getText() + ") from this number (" + mobileNumber + ")");
			return true;//Return true to delete it
		}
	}
	private static class CCallListener2 implements ICallListener
	{
		public void received(CService service, CIncomingCall call)
		{
			System.out.println("<< ---------------------------- INCOMING CALL ---------------------------- >>");
			System.out.println(" From: " + call.getPhoneNumber() + " @ time of call ===>> " + call.getTimeOfCall());
			System.out.println("<< ---------------------------- END ---------------------------- >>");
		}
	}	
}
