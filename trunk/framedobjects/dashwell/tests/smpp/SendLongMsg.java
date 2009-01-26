package com.framedobjects.dashwell.tests.smpp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ie.omk.smpp.Address;
import ie.omk.smpp.AlreadyBoundException;
import ie.omk.smpp.BadCommandIDException;
import ie.omk.smpp.Connection;
import ie.omk.smpp.UnsupportedOperationException;
import ie.omk.smpp.message.InvalidParameterValueException;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.message.SMPPProtocolException;
import ie.omk.smpp.message.SubmitSM;
import ie.omk.smpp.util.EncodingFactory;
import ie.omk.smpp.version.VersionException;

public class SendLongMsg {

	public static void main(String args[]) {
		Connection myConnection = null;
		try {
       	//myConnection = new Connection("sms1.cardboardfish.com", 9000, true);//asynchronous
//            myConnection.autoAckLink(true);
//            myConnection.autoAckMessages(true);
			//Connection myConnection = new Connection("194.247.82.149", 8011, true);
			myConnection = new Connection("server2.msgtoolbox.com", 2775, true);//client ip : 203.97.61.135
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}  

            while (!myConnection.isBound()) {
            	try {
	                //myConnection.bind(Connection.TRANSCEIVER,"nickbol1","Xsw98p","SMPP",0,0,null);
	            	//myConnection.bind(Connection.TRANSCEIVER,"8888888501","ddadee3d","SMPP",1,1,null);//server is closing you socket in the server side.It all depends on your server protocol.
					myConnection.bind(Connection.TRANSCEIVER,"DataNow1","97560526","SMPP",1,1,null);
				} catch (InvalidParameterValueException e) {
					e.printStackTrace();
				} catch (AlreadyBoundException e) {
					e.printStackTrace();
				} catch (VersionException e) {
					e.printStackTrace();
				} catch (SMPPProtocolException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

            System.out.println("Bind successful...submitting a message.");

    		try {            
	            // Submit a simple message
	            SubmitSM request = (SubmitSM) myConnection.newInstance(SMPPPacket.SUBMIT_SM);
	            request.setDestination(new Address(0, 0, "64212627443"));//  64212627443 - 6421622070
	            //request.setSource(new Address(0, 0, "+123"));//  6421622070
	            //request.setMessageText("jjjj");
	            //myConnection.sendRequest(request);
	            
	
	            /////////////////////////
	            // Set up a byte array which contains 2 known optional parameters
	            // followed by 2 unknowns.
	//            ByteArrayOutputStream out = new ByteArrayOutputStream();
	//			out.write(new byte[] {
	//		            (byte) 0x05,
	//		            (byte) 0x00,
	//		            (byte) 0x03,
	//		            (byte) 0xEF,
	//		            (byte) 0x02,
	//		            (byte) 0x01});//(byte[] bytes, int offset, int length)
	//            byte[] header = out.toByteArray();
	//            request.setMessage(header);
	//            myConnection.sendRequest(request);
	//            
	//            ByteArrayOutputStream out2 = new ByteArrayOutputStream();
	//			out2.write(new byte[] {
	//		            (byte) 0x05,
	//		            (byte) 0x00,
	//		            (byte) 0x03,
	//		            (byte) 0xEF,
	//		            (byte) 0x02,
	//		            (byte) 0x02});//(byte[] bytes, int offset, int length)
	//            byte[] header2 = out.toByteArray();
	//            request.setMessage(header2);
	//            myConnection.sendRequest(request);
	            
	            //byte[] temp = new byte[]{(byte)0x05, (byte)0x00, (byte)0x03, (byte)0xEF, (byte)0x02, (byte)0x01, (byte) 0x02, (byte) 0x02,(byte) 0x02,(byte) 0x02,(byte) 0x02 };
	//            request.setMessageText("test");
	//            SubmitSMResp smr = (SubmitSMResp)myConnection.sendRequest(request);
	            
	//            byte[] temp2 = new byte[]{(byte)0x05, (byte)0x00, (byte)0x03, (byte)0xEF, (byte)0x02,
	//            (byte)0x02,(byte) 0x02, (byte) 0x02,(byte) 0x02,(byte) 0x02,(byte) 0x02 };
	//            request.setMessage(temp2);
	//            myConnection.sendRequest(request);
	
	            String longMsg = "part1-----1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890 inja shode 150-bbbb-gheamate dovom-1234567890-1234567890-1234567890-12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890END";
	            byte[] msgBytes = longMsg.getBytes();
	            ByteArrayInputStream ms = new ByteArrayInputStream(msgBytes);
	            int len = msgBytes.length;//ms.length
	            
	            int TotalAmount = ((int)len / 153) + 1;
	            int i = 0;
	            byte[] temp;
	            //Random x = new Random(); // This random number is because every Concatenated message 
	            int refernce = 87;//x.nextInt(255); //need a reference number so that they are not mixed.
	            
	            for(i=1;i<=TotalAmount ;i++)
	            {
	               int arraycount = 159;
	               if(i * 153 > len)
	                  arraycount = ((int)len - (153 * (i -1))) + 6;
	               temp = new byte[arraycount];
	               temp[0] = 0x05;
	               temp[1] = 0x00;
	               temp[2] = 0x03;
	               temp[3] = Byte.parseByte(String.valueOf(refernce));  //refrence number
	               temp[4] = Byte.parseByte(String.valueOf(TotalAmount)); // Total number of messages
	               temp[5] = Byte.parseByte(String.valueOf(i)); //current message number
	               
	               ms.read(temp,6,arraycount - 6);
	               //ms.write(temp, 6, arraycount-6);//msRead(temp,6,arraycount - 6);
	               //temp = pack8Bits(temp); //this is uncommented if you need to pack the data.
	               
	               request.setEsmClass((byte)0x40);
	               request.setMessage(temp, EncodingFactory.getInstance().getDefaultAlphabet());
	               try {
	            	   myConnection.sendRequest(request);
					} catch (SocketTimeoutException e) {
						e.printStackTrace();
					} catch (UnsupportedOperationException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
	            }
    		}
            /////////////////////////
/*			String longMsg = "part1-----1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890 inja shode 150-bbbb-gheamate dovom-1234567890-1234567890-1234567890-12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890END";
            int msgLenght = longMsg.length();
			System.out.println("size: " + msgLenght);
			
			if(msgLenght > 150){
				List msgList= spliLongMsg(longMsg);
				Integer refNum = new Integer(989);
				TLVTable tvl;
				int totalSeqment =  msgList.size();
				for(int i = 0; i< totalSeqment; i++){
					String smsPart1 = (String)msgList.get(i);
					
					request.setMessage(smsPart1.getBytes(),EncodingFactory.getInstance().getDefaultAlphabet());
					//request.setMessageText("part" + i);
					tvl = new TLVTable(); 
					tvl.set(Tag.SAR_MSG_REF_NUM, 0xEF);
					tvl.set(Tag.SAR_SEGMENT_SEQNUM, i+1);					
					tvl.set(Tag.SAR_TOTAL_SEGMENTS, new Integer(totalSeqment));
					request.setTLVTable(tvl);
					
					myConnection.sendRequest(request);
					
					System.out.println("smsPart: " + smsPart1);
					System.out.println("Ref num : " + request.getOptionalParameter(Tag.SAR_MSG_REF_NUM));
					System.out.println("Sequence : " + request.getOptionalParameter(Tag.SAR_SEGMENT_SEQNUM));
					System.out.println("Total Segment : " + request.getOptionalParameter(Tag.SAR_TOTAL_SEGMENTS));		
					System.out.println("EsmClass = " + request.getEsmClass()+ "---------------------------------------------------------------");
					
				}
			}else{
				request.setMessage(longMsg.getBytes(),EncodingFactory.getInstance().getDefaultAlphabet());
				myConnection.sendRequest(request);
			}
*/            // Unbind.
//            UnbindResp ubr = myConnection.unbind();
//            if (ubr != null && ubr.getCommandStatus() == 0) {
//            	System.out.println("Successfully unbound from the SMSC");
//            } else {
//            	System.out.println("There was an error unbinding.");
//            }			
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		} 
    	catch (InvalidParameterValueException e) {
			e.printStackTrace();
		} catch (AlreadyBoundException e) {
			e.printStackTrace();
		} catch (VersionException e) {
			e.printStackTrace();
		} catch (SMPPProtocolException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (BadCommandIDException e) {
			e.printStackTrace();
		}
	}
	
	public static byte[] pack8Bits(byte[] NormalData)
	{
	  int i = 0; //Pointer for the NormalData
	  int j = 0; //Pointer for the PackedData
	  int DLWOHeader = NormalData.length - 7; //Data Length With Out Header :)
	  int PackedDataLength = ((DLWOHeader) - (DLWOHeader/8)) + 7; //Length of packed data will be equalt to Normal length - (Normal length / 8) + Header Length
	  byte[] PackedData = new byte[PackedDataLength]; 
	  for(j=0;j<6;j++)
	     PackedData[j] = NormalData[j]; //copy the header because it should not be packed
	  PackedData[j] = (byte)(NormalData[j] << 1); //This is just for long message because the header is 6 bytes 0500ff030201 then the first byte of the data will be the end of the octan and it must be shifted by 1 bit always.
	  j++;			
			
	  for(i=7;i<NormalData.length ;i++)
	  {
	    if(i != NormalData.length -1)
	       PackedData[j] = (byte)((NormalData[i] >> ((i+1) % 8))|(NormalData[i+1] << (7 - ((i+1) % 8)))); //It is hard to explain but trust me it is working :-)
	    else
	       PackedData[j] = (byte)((NormalData[i] >> ((i+1) % 8))|(byte)0x00); //If the number of bytes is not a multiple of 7 then we must pack with zero at the end GSM specification :-)
	    j++;
	    if((j % 7) == 0 && j != 7) //this is because every 8 bytes is packed into 7 bytes :-) so we skip any multiple of 8 and j!= 8 because the header is not packed so no byte to skip
	    i++;
	  }
	  return PackedData;
	}
	private static List spliLongMsg(String longMsg){
		int start=0;
		int limitedLenght = 150;
		List msgItemList = new ArrayList<String>();
		String smsPart;
		while(limitedLenght < longMsg.length()){
			smsPart = longMsg.substring(start, limitedLenght);
			msgItemList.add(smsPart);
			
			System.out.println("smsPart: " + smsPart);

			longMsg = longMsg.substring(smsPart.length());
		}
		smsPart = longMsg.substring(start);
		System.out.println("last smsPart: " + smsPart);
		msgItemList.add(smsPart);		

//		msgItemList.add("  part1 qwerrtyuioplkjhgfdsazxcvbnmpoiuytrewqasdfghjklmnbvcxz1234567890poiuytrewqasdfghjklmnbvcxzydsazxcvbnmpoiuytrewqasdfghjklmnbvcxz1234567890poiuytr");
//		msgItemList.add("  part2 ygdyaksufhwegfdsazxcvbnmpoiuytrewqasdfghjklmnbvcxz1234567890poiuytrewqasdfghjklmnbvcxkawhegdfahwegfyawgeyfgwyegfd");
//		msgItemList.add("  part3 egfhegfhergfhf");
		
		return msgItemList;
		
	}	
	public static void converter(){
        String x = "1f4b";//40, 
        int dd = Integer.parseInt(x,16);
        System.out.println("hex to str >>" + dd);	  
        String hex = Integer.toHexString(8011); //1f4b
        System.out.println("str to hex >>" + hex);
	}
//	 for your convenience here is the code for loading
	  // of the data from a file
//	  public ByteBuffer loadByteBuffer(String fileName) throws FileNotFoundException,IOException 
//	  { 
//	      FileInputStream is = new FileInputStream(fileName); 
//	      byte[] data = new byte[is.available()]; 
//	      is.read(data); 
//	      is.close(); 
//	      return new ByteBuffer(data); 
//	  } 
	  	
}
