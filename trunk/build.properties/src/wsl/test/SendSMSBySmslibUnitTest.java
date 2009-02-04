package wsl.test;

import javax.comm.CommDriver;

import org.smslib.CMessage;
import org.smslib.COutgoingMessage;
import org.smslib.CService;

public class SendSMSBySmslibUnitTest {
	public static void main(String args[]) {
		// Model strings define which of the available AT Handlers will be used.
		CService srv = new CService("COM1", 9600, "JANUS", "GSM864Q");
	
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
			
		System.out.println();
		System.out.println("SendMessage(): Send a message.");
		System.out.println("  Using " + CService._name + " " + CService._version);
		System.out.println();

		try
		{
			// connect to service
			srv.connect();

			// info about the GSM device...
			System.out.println("Mobile Device Information: ");
			System.out.println("	Manufacturer  : " + srv.getDeviceInfo().getManufacturer());
			System.out.println("	Model         : " + srv.getDeviceInfo().getModel());
			System.out.println("	Serial No     : " + srv.getDeviceInfo().getSerialNo());
			System.out.println("	IMSI          : " + srv.getDeviceInfo().getImsi());
			System.out.println("	S/W Version   : " + srv.getDeviceInfo().getSwVersion());
			System.out.println("	Battery Level : " + srv.getDeviceInfo().getBatteryLevel() + "%");
			System.out.println("	Signal Level  : " + srv.getDeviceInfo().getSignalLevel() + "%");

			// A test message to send
			COutgoingMessage msg = new COutgoingMessage("+64212627443", "Test SMS from Unit Test SMS server");
			msg.setMessageEncoding(CMessage.MessageEncoding.Enc7Bit);
			msg.getText();

			msg.setStatusReport(true);
			msg.setValidityPeriod(8);

			// now send it!
			srv.sendMessage(msg);

			// Disconnect after sending
			srv.disconnect();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		System.exit(0);		
	    }
}
