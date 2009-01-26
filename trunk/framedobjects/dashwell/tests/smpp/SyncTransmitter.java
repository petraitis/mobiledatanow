package com.framedobjects.dashwell.tests.smpp;

import java.io.IOException;
import java.net.SocketTimeoutException;

import ie.omk.smpp.Address;
import ie.omk.smpp.AlreadyBoundException;
import ie.omk.smpp.BadCommandIDException;
import ie.omk.smpp.Connection;
import ie.omk.smpp.UnsupportedOperationException;
import ie.omk.smpp.message.BindResp;
import ie.omk.smpp.message.InvalidParameterValueException;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.message.SMPPProtocolException;
import ie.omk.smpp.message.SubmitSM;
import ie.omk.smpp.util.EncodingFactory;
import ie.omk.smpp.util.UTF16Encoding;
import ie.omk.smpp.version.VersionException;

public class SyncTransmitter{

		    public SyncTransmitter() {
		    }


	/**
	 * @param args
	 */
	public static void main(String[] args) {
//	        try {
//	        	System.out.println("Binding to the SMSC");
//	
//	        	Connection myConnection = new Connection("194.247.82.149", 8011);
//	            myConnection.autoAckLink(true);
//	            myConnection.autoAckMessages(true);
//	
///*	            BindResp resp = myConnection.bind(
//	                    Connection.TRANSMITTER,
//	                    systemID,
//	                    password,
//	                    systemType,
//	                    sourceTON,
//	                    sourceNPI,
//	                    sourceAddress);*/
//	            
////	            BindResp resp = myConnection.bind(
////	                    Connection.TRANSMITTER,
////	                    "mdn-666490",
////	                    "eb732314",
////	                    "SMPP",
////	                    1,
////	                    1,
////	                    null);
//	            BindResp resp = myConnection.bind(Connection.TRANSMITTER, "mdn-666490", "eb732314", "SMPP");	            
//	            if (resp.getCommandStatus() != 0) {
//	            	System.out.println("SMSC bind failed.");
//	                System.exit(1);
//	            }
//	
//	            System.out.println("Bind successful...submitting a message.");
//	
//	            // Submit a simple message
//	            SubmitSM sm = (SubmitSM) myConnection.newInstance(SMPPPacket.SUBMIT_SM);
//	            //sm.setDestination(new Address(0, 0, "3188332314"));
//	            sm.setDestination(new Address(0, 0, "64212627443"));
//
//	            sm.setMessageText("from MDN Gateway!");
//	            SubmitSMResp smr = (SubmitSMResp) myConnection.sendRequest(sm);
//	
//	            System.out.println("Submitted message ID: " + smr.getMessageId());
//	
//	            // Unbind.
//	            UnbindResp ubr = myConnection.unbind();
//	
//	            if (ubr.getCommandStatus() == 0) {
//	            	System.out.println("Successfully unbound from the SMSC");
//	            } else {
//	            	System.out.println("There was an error unbinding.");
//	            }
//	        } catch (Exception x) {
//	        	System.out.println("An exception occurred.");
//	            x.printStackTrace(System.err);
//	        }
		try {	
			Connection myConnection = new Connection("194.247.82.149", 8011);//new Connection("sms1.cardboardfish.com", 9000, true);
			//Connection myConnection = new Connection("mobilesms.sytes.net", 9502, true);//+524776769212
	        //myConnection.autoAckLink(true);
	        //myConnection.autoAckMessages(true);
	        
	        System.out.println("Binding to the SMSC");
	
//	        BindResp resp = myConnection.bind(Connection.TRANSCEIVER,
//						"nickbol1",
//				        "Xsw98p",
//				        "SMPP",
//				        1,
//				        1,
//				        null);
	
	        BindResp resp = myConnection.bind(Connection.TRANSCEIVER,
					"8888888501",
			        "ddadee3d",
			        "SMPP",
			        1,
			        1,
			        "6421303707");
	        
/*	        myConnection.bind(Connection.TRANSCEIVER,
					"tester2139",
                    "PXTMDKYO",
                    "SMPP",
                    1,
                    1,
                    null);					        */
//	        if (resp.getCommandStatus() != 0) {
//	        	System.out.println("SMSC bind failed.");
//	            System.exit(1);
//	        }
//	
	        System.out.println("Bind successful...submitting a message.");
	        
	        // Submit a simple message
	        SubmitSM sm = (SubmitSM) myConnection.newInstance(SMPPPacket.SUBMIT_SM);

	        sm.setDestination(new Address(1, 1, "64212627443"));
	        //String smsStr = "Replyyyyy SMS ..";
	        
			//sm.setDataCoding((byte)0x80);
				String replyMsg2 = "testtt123\u00c0\u00c3\u20ac";

//				UTF16Encoding encoding = new UTF16Encoding(true);
//				byte[] replyEncoded = encoding.encodeString(replyMsg2);
//				sm.setDataCoding(encoding.getDataCoding());
//				sm.setMessage(replyEncoded);
				
				//sm.setMessageText(replyMsg2);
				//sm.setMessage(replyMsg2.getBytes(), EncodingFactory.getInstance().getDefaultAlphabet());
				
	        
	        
	        //String smsHexStr = SyncTransmitter.hexDump("teeeeest", smsStr.getBytes(), smsStr.length());
//	        sm.setEsmClass(0x40);
//	        sm.setMessage(smsStr.getBytes());
//	        sm.getMessageId();
	        
	        //sm.readBodyFrom(arg0, arg1);
	        //sm.setMessageText(smsStr);
	        myConnection.sendRequest(sm);
	        //System.out.println("Submitted message ID: " + smr.getMessageId());
	        
	        

/*			String smsTxt = "111111111111111111111111111111111111111 \n" +
			"2222222222222222222222222222222222222222222222222222 \n" +
			"333333333333333333333333333333333333333333333333333 \n" +
			"444444444444444444444444444444444444444444444444444444 \n" +
			"555555555555555555555555555555555555555555555555555555 \n" +
			"6666666666666666666666666666666666666666666666666666666666666\n" +
			"77777777777777777777777777777777777777777777777777777777777777\n";
			
			int start=0;
			int end = 160;
			int seqNum = 1;
			List list = new ArrayList<SubmitSM>(); 
//			if(smsTxt.length()>end)
//			{
				while(end <= smsTxt.length()){
					SubmitSM sm = (SubmitSM)myConnection.newInstance(SMPPPacket.SUBMIT_SM);
					sm.setMessageStatus(sm.SMC_MULTI);
					sm.setEsmClass(0x40);//64
					//sm.ESME_ROK;//0
					sm.setDestination((new Address(0, 0, "64212627443")));
					String msgId = "testId1";
					
					String smsPart1 = smsTxt.substring(start, end);
					System.out.println("smsPart1: " + smsPart1);
					
					sm.setMessageText(smsPart1);
					sm.setSequenceNum(seqNum);
					sm.setMessageId(msgId);
					
					list.add(sm);								
					
					smsTxt = smsTxt.substring(smsPart1.length()+1);
					seqNum++;
				}
				SubmitSM sm = (SubmitSM)myConnection.newInstance(SMPPPacket.SUBMIT_SM);
				sm.setMessageStatus(sm.SMC_MULTI);
				sm.setDestination((new Address(0, 0, "64212627443")));
				String msgId = "testId1";
				
				String smsPart1 = smsTxt.substring(start);
				System.out.println("smsPart1: " + smsPart1);
				sm.setMessageText(smsPart1);
				sm.setSequenceNum(seqNum);
				sm.setMessageId(msgId);
				list.add(sm);								
				
			//}	        
	        
			for (int i = 0; i < list.size(); i++) {
				SubmitSM resp1 = (SubmitSM)list.get(i);
				System.out.println("reply part >> " + resp1.getMessageText() + ", seq num : " +resp1.getSequenceNum());				
				myConnection.sendRequest(resp1);
			}*/

	            // Wait a while, see if the SMSC delivers anything to us...
	            //SMPPPacket p = myConnection.readNextPacket();
	            //System.out.println("Received a packet!");
	            //System.out.println(p.toString());
	
	            // API should be automatically acking deliver_sm and
	            // enquire_link packets...
	        } catch (java.net.SocketTimeoutException x) {
	            // ah well...
			} catch (InvalidParameterValueException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AlreadyBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (VersionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SMPPProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadCommandIDException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        // Unbind.
//	        UnbindResp ubr = myConnection.unbind();
//	
//	        if (ubr.getCommandStatus() == 0) {
//	        	System.out.println("Successfully unbound from the SMSC");
//	        } else {
//	        	System.out.println("There was an error unbinding.");
//	        }
	}
	public static String hexDump(String title, byte[] m, int l) {
		int p = 0;
		StringBuffer line = new StringBuffer();
		//System.out.println(title);
		System.out.println("Hex dump (" + l + ") bytes:");
		for (int i = 0; i < l; i++) {
			if ((m[i] >= 0) & (m[i] < 16))
				line.append("0");
			line.append(Integer.toString(m[i] & 0xff, 16).toUpperCase());
			if ((++p % 4) == 0) {
				//line.append(":");
			}
			if (p == 16) {
				p = 0;
				System.out.println(line.toString());
				line = new StringBuffer();
			}
		}
		if (p != 16) {
			System.out.println(line.toString());
		}
		return line.toString();
		//System.out.println("====================================");
	}
	

  }

