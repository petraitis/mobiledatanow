package com.framedobjects.dashwell.biz;

import wsl.mdn.dataview.QueryDobj;
import wsl.mdn.mdnmsgsvr.UserReply;

public class MessageObject {
	private int id;
	private String type;
	private int viewOrTableId;
	private int dbId;
	private String criteria;
	private String sort;
	private int groupFieldId;
	private int parentId = 0;
	private String keyword;
	private String timeout;
	private String response;
	private int displayResult;
	private String objectType;
	private int dsId;
	private int webServiceId;
	private int projectId;
	
	public static MessageObject getMessageObject(QueryDobj queryMessaging) {
		MessageObject msgObj = new MessageObject();
		msgObj.setId(queryMessaging.getId());
		msgObj.setType(queryMessaging.getType());
		msgObj.setViewOrTableId(queryMessaging.getViewOrTableId());
		msgObj.setDatabaseId(queryMessaging.getDatabaseId());
		msgObj.setCriteriaString(queryMessaging.getCriteriaString());
		msgObj.setSortString(queryMessaging.getSortString());
		msgObj.setGroupFieldId(queryMessaging.getGroupFieldId());
		msgObj.setProjectId(queryMessaging.getProjectId());
		msgObj.setDatasourceStatus(queryMessaging.getDatasourceStatus());

		//queryMessaging.setparentId = parentId;
		//queryMessaging.settimeout = timeout;

//		msgObj.setImKeyword(queryMessaging);
//		msgObj.setSmsKeyword(keyword);
//		msgObj.setEmailKeyword(keyword);
//		msgObj.setImDisplayResult(displayResult);
//		msgObj.setMobileDisplayResult(displayResult);
//		msgObj.setEmailDisplayResult(displayResult);
		
		msgObj.setResponse(queryMessaging.getResponse());
		return msgObj;
	}

	public static MessageObject getMessageObject(UserReply userReply) {
		MessageObject msgObj = new MessageObject();
		msgObj.setId(userReply.getId());
		msgObj.setType(userReply.getType());
		msgObj.setViewOrTableId(userReply.getViewOrTableId());
		msgObj.setDatabaseId(userReply.getDatabaseId());
		msgObj.setCriteriaString(userReply.getCriteriaString());
		msgObj.setSortString(userReply.getSortString());
		msgObj.setGroupFieldId(userReply.getGroupFieldId());
		msgObj.setParentId(userReply.getParentId());
		msgObj.setTimeout(userReply.getTimeout());
		msgObj.setKeyword(userReply.getMsgText());
		msgObj.setDisplayResult(userReply.getDisplayResult());
		msgObj.setResponse(userReply.getResponse());
		msgObj.setProjectId(userReply.getProjectId());
		msgObj.setDatasourceStatus(userReply.getDatasourceStatus());
		msgObj.setObjectType("UR");
		return msgObj;
	}
	
    public int getId(){
        return id;
    }
    
	public void setId(int id) {
		this.id = id;
	}    

    public int getDatasourceStatus(){
        return dsId;
    }
    
	public void setDatasourceStatus(int dsId) {
		this.dsId = dsId;
	}    
 	
    public int getWebServiceId(){
        return webServiceId;
    }
    
	public void setWebServiceId(int webServiceId) {
		this.webServiceId = webServiceId;
	}    
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}    

    public int getViewOrTableId() {
        return viewOrTableId;
    }

    public void setViewOrTableId(int id)
    {
        this.viewOrTableId = id;
    }
	
    public int getDatabaseId()
    {
        return dbId;
    }
    
    public void setDatabaseId(int dbId)
    {
       this.dbId = dbId;
    }

    public String getCriteriaString()
    {
        return criteria;
    }

    public void setCriteriaString(String criteria)
    {
        this.criteria = criteria;
    }

    public String getSortString()
    {
        return sort;
    }

    public void setSortString(String sorts)
    {
        this.sort = sorts;
    }

    public int getGroupFieldId()
    {
        return groupFieldId;
    }

    public void setGroupFieldId(Integer id)
    {
        this.groupFieldId = id;
    }

    public int getParentId()
    {
        return parentId;
    }

    public void setParentId(int parentId)
    {
        this.parentId = parentId;
    }

    public String getKeyword()
    {
        return keyword;
    }
    
    public void setKeyword(String keyword)
    {
    	this.keyword = keyword;
    }

     public String getTimeout()
     {
        return timeout;
     }

     public void setTimeout(String timeout)
     {
        this.timeout = timeout;
     }

     public String getResponse()
      {
         return response;
      }

     public void setResponse(String response)
      {
         this.response = response;
      }

    public int getDisplayResult()
    {
         return displayResult;
    }

    public void setDisplayResult(int displayResult)
    {
       this.displayResult = displayResult;
    }

    public String getObjectType()
    {
       return objectType;
    }

   public void setObjectType(String objectType)
    {
       this.objectType= objectType;
    }

   public int getProjectId()
   {
       return projectId;
   }

   public void setProjectId(int projectId)
   {
       this.projectId = projectId;
   }
	
}
