package com.framedobjects.dashwell.biz;

import java.util.Date;
import java.util.List;

public class GuestMessagesInfo {

	private int count;
	private String contact;
	private Date firstMsgReceivedDate;
	private String guestUserId;
	private long timeToBlock;
	private long cancelTime;
	private boolean isBlock;
	
    public int getCount(){
        return count;
    }
    
	public void setCount(int count) {
		this.count = count;
	}    	
	
    public String getGuestUserId(){
        return guestUserId;
    }
    
	public void setGuestUserId(String guestUserId) {
		this.guestUserId = guestUserId;
	}    		
	
    public String getContact(){
        return contact;
    }
    
	public void setContact(String contact) {
		this.contact = contact;
	}    		
	
    public Date getFirstMsgReceivedDate()
    {
        return firstMsgReceivedDate;
    }	
    
	public void setFirstMsgReceivedDate(Date firstMsgReceivedDate) {
		this.firstMsgReceivedDate = firstMsgReceivedDate;
	}

    public long getTimeToBlock()
    {
        return timeToBlock;
    }	
    
	public void setTimeToBlock(long timeToBlock) {
		this.timeToBlock = timeToBlock;
	}
	
    public long getCancelTime()
    {
        return cancelTime;
    }	
    
	public void setCancelTime(long cancelTime) {
		this.cancelTime = cancelTime;
	}
	
    public boolean isBlock()
    {
        return isBlock;
    }	
    
	public void setIsBlock(boolean isBlock) {
		this.isBlock = isBlock;
	}	
}
