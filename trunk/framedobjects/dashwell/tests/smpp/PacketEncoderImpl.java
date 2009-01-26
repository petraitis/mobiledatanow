package com.framedobjects.dashwell.tests.smpp;

import ie.omk.smpp.ErrorAddress;
import ie.omk.smpp.util.SMPPDate;

import java.io.IOException;
import java.io.OutputStream;

public class PacketEncoderImpl {
	private OutputStream out;
	    
	   public PacketEncoderImpl() {
	   }
	   
	   public PacketEncoderImpl(OutputStream stream) {
		   this.out = stream;
	   }
	   
	   public PacketEncoderImpl setStream(OutputStream out) {
	            this.out = out;
	            return this;
	   }
	   
	   public OutputStream getStream() {
	            return out;
	   }
	   
	   public PacketEncoderImpl writeCString(String value) throws IOException {
	            if (value != null) {
	                out.write(value.getBytes("US-ASCII"));
	            }
	            out.write(0);
	            return this;
	   }
	   
	   public PacketEncoderImpl writeString(String value, int length) throws IOException {
	            if (length > value.length()) {
	                throw new IndexOutOfBoundsException(String.valueOf(length));
	            }
	            byte[] array = value.getBytes("US-ASCII");
	            out.write(array, 0, length);
	            return this;
	   }
	    
	   public PacketEncoderImpl writeUInt1(int value) throws IOException {
	            out.write(value);
	            return this;
	   }
	   
	   public PacketEncoderImpl writeUInt2(int value) throws IOException {
	            out.write(value >>> 8);
	            out.write(value);
	            return this;
	   }
	    
	   public PacketEncoderImpl writeUInt4(long value) throws IOException {
	            out.write((int) (value >>> 24));
	            out.write((int) (value >>> 16));
	            out.write((int) (value >>> 8));
	            out.write((int) value);
	            return this;
	   }
	    
	   public PacketEncoderImpl writeInt4(int value) throws IOException {
	            out.write(value >>> 24);
	            out.write(value >>> 16);
	            out.write(value >>> 8);
	            out.write(value);
	            return this;
	   }
	    
	   public PacketEncoderImpl writeInt8(long value) throws IOException {
	            out.write((int) (value >>> 56));
	            out.write((int) (value >>> 48));
	            out.write((int) (value >>> 40));
	            out.write((int) (value >>> 32));
	            out.write((int) (value >>> 24));
	            out.write((int) (value >>> 16));
	            out.write((int) (value >>> 8));
	            out.write((int) value);
	            return this;
	   }
	   
//	   public PacketEncoderImpl writeAddress(Address address) throws IOException {
//	            if (address != null) {
//	                address.writeTo(this);
//	            } else {
//	                new Address().writeTo(this);
//	            }
//	            return this;
//	   }
	   
//	  public PacketEncoderImpl writeErrorAddress(ErrorAddress errorAddress) throws IOException {
//	           if (errorAddress != null) {
//	               errorAddress.writeTo(this);
//	           } else {
//	               new ErrorAddress().writeTo(this);
//	           }
//	           return this;
//	  }
//	  
//	  public PacketEncoderImpl writeDate(SMPPDate date) throws IOException {
//	           String str = DATE_FORMAT.format(date);
//	           writeCString(str);
//	           return this;
//	  }
	   
	  public PacketEncoderImpl writeBytes(byte[] bytes) throws IOException {
	           return writeBytes(bytes, 0, bytes.length);
	  }
	  
	  public PacketEncoderImpl writeBytes(byte[] bytes, int offset, int length) throws IOException {
	           if (bytes != null) {
	               out.write(bytes, offset, length);
	           } else {
	               if (length != 0) {
	                   throw new IndexOutOfBoundsException(Integer.toString(offset));
	               }
	          }
	           return this;
	  	}
	  }