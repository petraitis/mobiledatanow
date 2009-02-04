package com.framedobjects.dashwell.biz;

import java.util.HashMap;
import java.util.Map;

public class MsgSessionManager {
	
	private Map attributeMap; 
	
	public MsgSessionManager(){
		attributeMap = new HashMap();
	}
	
	public Object getAttribute(String name){
		Object obj = attributeMap.get(name);
		return obj;
	}
	
	public void setAttribute(String name, Object value){
		if(attributeMap.containsKey(name))
			attributeMap.remove(name);
		attributeMap.put(name, value);
	}
}
