package com.framedobjects.dashwell.utils.webservice;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides a structure 
 * to hole web services result as a row of Data Object
 */
		
public class WebServiceResultRow {

	private int rowIndex;
	private Object parentObj;
	private String parentName;
	private List<ParamListItem> paramItemList = new ArrayList<ParamListItem>();
	
	public void setRowIndex(int rowIndex){
		this.rowIndex = rowIndex;
	}
	public  int getRowIndex(){
		return rowIndex;
	}
	public void setPrentObj(Object parentObj){
		this.parentObj = parentObj;
	}
	public  Object getParentObj(){
		return parentObj;
	}	
	public void setParamItemList(List<ParamListItem> paramItemList){
		this.paramItemList = paramItemList;
	}
	public  List<ParamListItem> getParamItemList(){
		return paramItemList;
	}		
	
	public void addParamItem(ParamListItem paramItem){
		paramItemList.add(paramItem);
	}
}
