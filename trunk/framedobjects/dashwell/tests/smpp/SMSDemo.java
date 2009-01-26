package com.framedobjects.dashwell.tests.smpp;

import ie.omk.smpp.Address;
import ie.omk.smpp.AlreadyBoundException;
import ie.omk.smpp.BadCommandIDException;
import ie.omk.smpp.Connection;
import ie.omk.smpp.UnsupportedOperationException;
import ie.omk.smpp.event.ConnectionObserver;
import ie.omk.smpp.event.ReceiverExitEvent;
import ie.omk.smpp.event.SMPPEvent;
import ie.omk.smpp.message.EnquireLink;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.message.SMPPProtocolException;
import ie.omk.smpp.message.SubmitSM;
import ie.omk.smpp.message.tlv.TLVTable;
import ie.omk.smpp.message.tlv.Tag;
import ie.omk.smpp.util.EncodingFactory;
import ie.omk.smpp.version.VersionException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;

public class SMSDemo extends Thread implements ConnectionObserver {
	
	private Connection conn;
	private boolean exit = false;
	
	private static SMSDemo instance;
	
	private SMSDemo() {
	}
	
	public static SMSDemo getInstance() {
		if (instance == null)
			instance = new SMSDemo();
		return instance;
	}
	
	private void connect() {
		try {
			//conn = new Connection("localhost", 2775, true);
			//conn = new Connection("194.247.82.149", 8011, true);//asynchronous
			//conn = new Connection("203.97.61.135", 2775, true);//asynchronous
			//conn = new Connection("server3.msgtoolbox.com", 2775, true);//asynchronous
			//conn = new Connection("78.110.226.163", 2775, true);//asynchronous
			conn = new Connection("mobilesms.sytes.net", 9502, true);//newer = 55 19 15 85 16, new= 55 19 15 85 15, old=Mexican +5214776769212   
			 
//			conn = new Connection("sms1.cardboardfish.com", 9000, true);//asynchronous
//			APIConfig conf = new APIConfig();
//			conf.setProperty(APIConfig.BIND_TIMEOUT, "30");			
			conn.addObserver(this);
		} catch (UnknownHostException uhe) {
			System.exit(0);
		}
		
		boolean retry = false;
		
		while (!retry) {
			try {
				//conn.bind(Connection.TRANSCEIVER, "sysId", "secret", null);
/*				conn.bind(Connection.TRANSCEIVER,
						"mdn-666490",
	                    "eb732314",
	                    "SMPP",
	                    1,
	                    1,
	                    null);*/							
/*				conn.bind(Connection.TRANSCEIVER,
						"8888888501",
	                    "ddadee3d",
	                    "SMPP",
	                    1,
	                    1,
	                    null);*/
	                    
/*				conn.bind(Connection.TRANSCEIVER,
						"nickbol1",
	                    "Xsw98p",
	                    "TRX",
	                    0,
	                    0,
	                    null);*/
				
/*				conn.bind(Connection.TRANSCEIVER,
						"DataNow1",
	                    "97560526",
	                    "SMPP",
	                    1,
	                    1,
	                    null);				
*/
				
/*				conn.bind(Connection.TRANSCEIVER,
						"datanow",
	                    "jX01JlMT",
	                    "SMPP",
	                    1,
	                    1,
	                    null);				*/
				
				conn.bind(Connection.TRANSCEIVER,
						"tester2139",
	                    "PXTMDKYO",
	                    "SMPP",
	                    1,
	                    1,
	                    null);				
				
		        conn.autoAckLink(true);
		        conn.autoAckMessages(true);
		        
				retry = true;
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		
	}
	
	public void run() {
		while (!exit) {
			connect();
			synchronized(this) {
				try {
					wait(100000);
					if(conn.isBound()){
						//System.out.println("bound!!!");
						//conn.sendRequest((EnquireLink)conn.newInstance(SMPPPacket.ENQUIRE_LINK));
					}
				} catch (InterruptedException ie) {
				} catch (VersionException e) {
					e.printStackTrace();
//				} catch (BadCommandIDException e) {
//					e.printStackTrace();
//				} catch (SocketTimeoutException e) {
//					e.printStackTrace();
				} catch (AlreadyBoundException e) {
					e.printStackTrace();
				} catch (SMPPProtocolException e) {
					e.printStackTrace();
				} catch (UnsupportedOperationException e) {
					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
				}
				
			}
		}
	}
	
	public void update(Connection conn, SMPPEvent ev) {
		//System.out.println("update methoddddd---->> " + ev.getType());
		if(ev instanceof ReceiverExitEvent) {
			//System.out.println("::::::::::::::::::" + ((ReceiverExitEvent)ev).getException());
		}
		if (ev.getType() == SMPPEvent.RECEIVER_EXIT && ((ReceiverExitEvent)ev).isException()) {
			synchronized(this) {
				notify();
			}
		}
		//System.out.println("update event = " + ev.getType());
		if (ev.getType() == SMPPEvent.RECEIVER_EXCEPTION ) {
			//System.out.println("event :: " + ev.toString());
			//System.out.println("::::::::::::::::::" + ((ReceiverExitEvent)ev).getException());
			connect();
		}				
	}

	public void packetReceived(Connection conn, SMPPPacket pack) {
		switch(pack.getCommandId()) {
			case SMPPPacket.DELIVER_SM : 
				System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ recived.. " );
				try {
					if(conn.isBound()){
						//logPacket(pack, "IN");
						SubmitSM sm = (SubmitSM)conn.newInstance(SMPPPacket.SUBMIT_SM);
						Address dest = new Address( 0, 0, "+64212627443");
						sm.setDestination(dest);//sm.setDestination(pack.getSource());
						String msgStr = pack.getMessageText();
						System.out.println("++++++++++++++++++++++++++++++++++++++ " + msgStr);
						sm.setMessageText("Replyyy ====>>>>>>" + msgStr);
						conn.sendRequest(sm);

						//String longMsg = "part1 qwerrtyuioplkjhgfdsazxcvbnmpoiuytrewqasdfghjklmnbvcxz1234567890poiuytrewqasdfghjklmnbvcxzydsazxcvbnmpoiuytrewqasdfghjklmnbvcxz1234567890poiuytr   part2 ygdyaksufhwegfdsazxcvbnmpoiuytrewqasdfghjklmnbvcxz1234567890poiuytrewqasdfghjklmnbvcxkawhegdfahwegfyawgeyfgwyegfd part 3 endddddd...";
/*						String longMsg = "aaaaaaaaaa1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890inja shode150-bbbb-gheamate dovom-1234567890-1234567890-1234567890 end";
						
						List msgList= spliLongMsg(longMsg);
						Integer refNum = new Integer(333);
						Integer seqNum = new Integer(1);
						TLVTable tvl;
						int totalSeqment =  msgList.size();
						for(int i = 0; i< totalSeqment; i++){
							String smsPart1 = (String)msgList.get(i);
							
							sm.setMessage(smsPart1.getBytes(),EncodingFactory.getInstance().getDefaultAlphabet());							
							tvl = new TLVTable(); 
							tvl.set(Tag.SAR_MSG_REF_NUM, refNum);
							tvl.set(Tag.SAR_SEGMENT_SEQNUM, i+1);					
							tvl.set(Tag.SAR_TOTAL_SEGMENTS, new Integer(totalSeqment));
							sm.setTLVTable(tvl);
							
							conn.sendRequest(sm);
							
							System.out.println("smsPart: " + smsPart1);
							System.out.println("Ref num : " + sm.getOptionalParameter(Tag.SAR_MSG_REF_NUM));
							System.out.println("Sequence : " + sm.getOptionalParameter(Tag.SAR_SEGMENT_SEQNUM));
							System.out.println("Total Segment : " +sm.getOptionalParameter(Tag.SAR_TOTAL_SEGMENTS));		
							System.out.println("---------------------------------------------------------------");
							
						}
*/						///end<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<						
					}
					//SubmitSM response = processRequest(pack);
					//SubmitSMResp smr = (SubmitSMResp)conn.sendRequest(response);

/*					//PART1>>>>>>>>>>>>>>>>
					SubmitSM sm1 = (SubmitSM)conn.newInstance(SMPPPacket.SUBMIT_SM);
					sm1.setDestination(pack.getSource());
					//sm1.setEsmClass(0x40);
					String p1 = "part1 ";
					sm1.setMessage(p1.getBytes(),EncodingFactory.getInstance().getDefaultAlphabet());//

					Integer refNum = new Integer(258);//same
					Integer seqNum = new Integer(1);//next should be 2
					Integer segmentNum = new Integer(2);//It is 2 part Same
					
					TLVTable table = new TLVTable(); 
					table.set(Tag.SAR_MSG_REF_NUM, refNum);
					table.set(Tag.SAR_SEGMENT_SEQNUM, seqNum);					
					table.set(Tag.SAR_TOTAL_SEGMENTS, segmentNum);
					sm1.setTLVTable(table);
//					System.out.println("part 1 Ref num : " + sm1.getOptionalParameter(Tag.SAR_MSG_REF_NUM));
//					System.out.println("part 1 Sequence : " + sm1.getOptionalParameter(Tag.SAR_SEGMENT_SEQNUM));
//					System.out.println("part 1 Total Segment : " +sm1.getOptionalParameter(Tag.SAR_TOTAL_SEGMENTS));		
					//PART2<<<<<<<<<<<<<<<<<<<<<<
					SubmitSM sm2 = (SubmitSM)conn.newInstance(SMPPPacket.SUBMIT_SM);
					sm2.setDestination(pack.getSource());
					String p2 = "part2 ";
					sm2.setMessage(p2.getBytes(), EncodingFactory.getInstance().getDefaultAlphabet());
					conn.sendRequest(sm1);

					Integer seqNum2 = new Integer(2);//next should be 2
					TLVTable table2 = new TLVTable(); 
					table2.set(Tag.SAR_MSG_REF_NUM, refNum);
					table2.set(Tag.SAR_SEGMENT_SEQNUM, seqNum2);					
					table2.set(Tag.SAR_TOTAL_SEGMENTS, segmentNum);
					sm2.setTLVTable(table2);
					
					conn.sendRequest(sm2);*/
/*					for{ .....
						TLVTable table = new TLVTable(); 
						table.set(Tag.SAR_TOTAL_SEGMENTS, new Integer(totalSegment+1));
						table.set(Tag.SAR_MSG_REF_NUM, refNumber);
						table.set(Tag.SAR_SEGMENT_SEQNUM, new Integer(i+1));

						sm.setTLVTable(table);

//						send
						...
						..
						}*/					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case SMPPPacket.BIND_TRANSCEIVER_RESP :
				if (pack.getCommandStatus() != 0) {
					System.out.println("Error binding: " + pack.getCommandStatus());
					exit = true;
					synchronized(this) {
						notify();
					}
				} else {
					System.out.println("Bounded");
					
/*					SubmitSM sm;
					try {
						sm = (SubmitSM)conn.newInstance(SMPPPacket.SUBMIT_SM);
						Address dest = new Address( 1, 1, "+64212627443");
							
						sm.setDestination(dest);						
						sm.setMessageText("from Mexicooooo");
						conn.sendRequest(sm);

					} catch (VersionException e) {
						e.printStackTrace();
					} catch (BadCommandIDException e) {
						e.printStackTrace();
					} catch (SocketTimeoutException e) {
						e.printStackTrace();
					} catch (AlreadyBoundException e) {
						e.printStackTrace();
					} catch (SMPPProtocolException e) {
						e.printStackTrace();
					} catch (UnsupportedOperationException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}					*/
				}
			case SMPPPacket.ENQUIRE_LINK:
//				System.out.println("ENQUIRE_LINK" + pack.getCommandStatus());
//				try {
//					SubmitSM activeConnRequest = (SubmitSM)conn.newInstance(SMPPPacket.SUBMIT_SM);
//					conn.sendRequest(activeConnRequest);
//				} catch (BadCommandIDException e) {
//					e.printStackTrace();
//				} catch (SMPPProtocolException e) {
//					e.printStackTrace();
//				} catch (UnsupportedOperationException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}				
//				EnquireLink l = (EnquireLink)conn.newInstance(SMPPPacket.ENQUIRE_LINK);
//				conn.sendRequest(l);
				break;
			case SMPPPacket.ENQUIRE_LINK_RESP:
				System.out.println("ENQUIRE_LINK_RESP");
				break;				
		}
	}
	
	private List spliLongMsg(String longMsg){
		int start=0;
		int limitedLenght = 150;
		List msgItemList = new ArrayList<String>();
		String smsPart;
		while(limitedLenght <= longMsg.length()){
			smsPart = longMsg.substring(start, limitedLenght);
			msgItemList.add(smsPart);
			
			System.out.println("smsPart: " + smsPart);

			longMsg = longMsg.substring(smsPart.length()+1);
		}
		smsPart = longMsg.substring(start);
		System.out.println("last smsPart: " + smsPart);
		msgItemList.add(smsPart);		


//		msgItemList.add("  part1 qwerrtyuioplkjhgfdsazxcvbnmpoiuytrewqasdfghjklmnbvcxz1234567890poiuytrewqasdfghjklmnbvcxzydsazxcvbnmpoiuytrewqasdfghjklmnbvcxz1234567890poiuytr");
//		msgItemList.add("  part2 ygdyaksufhwegfdsazxcvbnmpoiuytrewqasdfghjklmnbvcxz1234567890poiuytrewqasdfghjklmnbvcxkawhegdfahwegfyawgeyfgwyegfd");
//		msgItemList.add("  part3 egfhegfhergfhf");
		
		return msgItemList;
		
	}
	private SubmitSM processRequest(SMPPPacket request) throws BadCommandIDException {
		SubmitSM sm = (SubmitSM)conn.newInstance(SMPPPacket.SUBMIT_SM);
		sm.setDestination(request.getSource());
		String[] parts = request.getMessageText().split(" ");
		//logPacket(request, "IN");
//		if (parts[0].equalsIgnoreCase("balance")) {
//			User user = User.findByPhone(request.getSource().getAddress());
//			if (user == null)
//				sm.setMessageText("Your phone number is not registered in our database! Please contact one of our offices");
//			else if (!user.getAccountNumber().equalsIgnoreCase(parts[1]))
//				sm.setMessageText("Account number that you have entered is not correct! Please try again");
//			else
//				sm.setMessageText("Balance on your account is " + user.getBalance() + "$");
//		} else {
//			sm.setMessageText("Wrong message format! Please send BALANCE <ACCOUNT_NUMBER>");
//		}
		//sm.setMessageText("Autho reply to this Received SMS ==>> " + request.getMessageText() );
		
		
		///start>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		String msgStr = request.getMessageText();
		String smsTxt = "aaaaaaaaaa1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890inja shode150-bbbb-gheamate dovom-1234567890-1234567890-1234567890";

		int start=0;
		int end = 160;
		
		Integer refNum = new Integer(987);
		Integer seqNum = new Integer(1);
		TLVTable tvl;
		
		int totalSeqment =  smsTxt.length()/160;
		
		while(end <= smsTxt.length()){
			String smsPart1 = smsTxt.substring(start, end);
			tvl = new TLVTable(); 
			tvl.set(Tag.SAR_MSG_REF_NUM, refNum);
			tvl.set(Tag.SAR_SEGMENT_SEQNUM, seqNum);					
			tvl.set(Tag.SAR_TOTAL_SEGMENTS, new Integer(totalSeqment));
			sm.setTLVTable(tvl);
			
			try {
				conn.sendRequest(sm);
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
			} catch (AlreadyBoundException e) {
				e.printStackTrace();
			} catch (VersionException e) {
				e.printStackTrace();
			} catch (SMPPProtocolException e) {
				e.printStackTrace();
			} catch (UnsupportedOperationException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			System.out.println("smsPart: " + smsPart1);
			System.out.println("Ref num : " + sm.getOptionalParameter(Tag.SAR_MSG_REF_NUM));
			System.out.println("Sequence : " + sm.getOptionalParameter(Tag.SAR_SEGMENT_SEQNUM));
			System.out.println("Total Segment : " +sm.getOptionalParameter(Tag.SAR_TOTAL_SEGMENTS));		
			System.out.println("---------------------------------------------------------------");
			
			smsTxt = smsTxt.substring(smsPart1.length()+1);
			seqNum++;			
		}
		///end<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

		//logPacket(sm, "OUT");
		return sm;
	}

	private void logPacket(SMPPPacket packet, String direction) {
		String phone;
		if (direction.equals("OUT"))
			phone = packet.getDestination().getAddress();
		else
			phone = packet.getSource().getAddress();
		System.out.println(direction + ": " + phone +  " - " + packet.getMessageText());
	}
	
	public Connection getConnection() {
		return conn;
	}	
	
	public static void main(String args[]) {
		//Runtime.getRuntime().addShutdownHook(new Hook());
		System.out.println("Unbinding");
		SMSDemo demo = SMSDemo.getInstance();
		demo.run();
	}	

}