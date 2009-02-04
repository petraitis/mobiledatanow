package wsl.mdn.mdnmsgsvr;

import wsl.fw.datasource.DataManager;
import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.DefaultKeyGeneratorData;
import wsl.fw.datasource.Entity;
import wsl.fw.datasource.EntityImpl;
import wsl.fw.datasource.Field;
import wsl.fw.datasource.FieldImpl;
import wsl.fw.datasource.Query;
import wsl.fw.datasource.QueryCriterium;
import wsl.fw.datasource.RecordSet;
import wsl.mdn.dataview.DataView;
import wsl.mdn.dataview.EntityDobj;

public class UserReply  extends DataObject
{
    // version tag
    private final static String _ident = "$Date: 2006/12/9 12:26:09 $  $Revision: 1.2 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/mdnmsgsvr/UserReply2.java $ ";

    //--------------------------------------------------------------------------
    // constants
	
    // the entity name
    public final static String ENT_USER_REPLY            = "TBL_USER_REPLY";
    
    public final static String FLD_ID               	 = "FLD_ID";
    public final static String FLD_TYPE                  = "FLD_TYPE";
    public final static String FLD_VIEW_TABLE_ID       	 = "FLD_VIEW_TABLE_ID";
    public final static String FLD_DB_ID 		         = "FLD_DB_ID";
    public final static String FLD_CRITERIA              = "FLD_CRITERIA";
    public final static String FLD_SORTS                 = "FLD_SORTS";
    public final static String FLD_GROUPFIELDID          = "FLD_GROUPFIELDID";
    //  ---------------- INSERTED THESE FIELDS FOR MESSAGING INFO ------------------ //
    public final static String FLD_PARENT_ID             = "FLD_PARENT_ID";
    public final static String FLD_CHILDREN              = "FLD_CHILDREN";
//    public final static String FLD_QUERY_ID              = "FLD_QUERY_ID";
    public final static String FLD_MSG_TEXT              = "FLD_MSG_TEXT";
    public final static String FLD_TIMEOUT				 = "FLD_TIMEOUT";
    public final static String FLD_RESPONSE              = "FLD_RESPONSE";    
    public final static String FLD_PROJECT_ID        	 = "FLD_PROJECT_ID";
    public final static String FLD_DESCIPTION        	 = "FLD_DESCIPTION";
    public final static String FLD_QUERY_PARENT_ID       = "FLD_QUERY_PARENT_ID";
    public final static String FLD_DISPLAY_RESULT        = "FLD_DISPLAY_RESULT";
    public final static String FLD_DS_STATUS		     = "FLD_DS_STATUS";
    public final static String FLD_WS_ID		 		 = "FLD_WS_ID";

    public final static String FLD_DEL_STATUS   		 = "FLD_DEL_STATUS";
    
    // ----------------------------------------------------------------------------- //
    
    /**
     * Default constructor. Since an UserReply is invalid if it is not correctly initialized
     * ensure that setter methods are called when using this constructor/
     */
    public UserReply()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Static factory method to create the entity to be used by this dataobject
     * and any subclasses. This is called by the DataManager's factory when
     * creating a ENT_EMAIL entity.
     * @return the created entity.
     */
    public static Entity createEntity()
    {
        // create the Mdn Email entity
        Entity ur = new EntityImpl(ENT_USER_REPLY, UserReply.class);

        // add the key generator for the system id
        ur.addKeyGeneratorData(new DefaultKeyGeneratorData(ENT_USER_REPLY, FLD_ID));

        // create the fields and add them to the entity
        ur.addField(new FieldImpl(FLD_ID, Field.FT_INTEGER, Field.FF_UNIQUE_KEY | Field.FF_SYSTEM_KEY));
        ur.addField(new FieldImpl(FLD_TYPE, Field.FT_STRING, Field.FF_NONE));
        ur.addField(new FieldImpl(FLD_VIEW_TABLE_ID, Field.FT_INTEGER, Field.FF_NONE));        
        ur.addField(new FieldImpl(FLD_DB_ID, Field.FT_INTEGER, Field.FF_NONE));        
        ur.addField(new FieldImpl(FLD_CRITERIA, Field.FT_STRING, Field.FF_NONE));
        ur.addField(new FieldImpl(FLD_SORTS, Field.FT_STRING, Field.FF_NONE));
        ur.addField(new FieldImpl(FLD_GROUPFIELDID, Field.FT_INTEGER, Field.FF_NONE));
        ur.addField(new FieldImpl(FLD_PARENT_ID, Field.FT_INTEGER, Field.FF_NONE));
        ur.addField(new FieldImpl(FLD_CHILDREN, Field.FT_STRING, Field.FF_NONE));
        ur.addField(new FieldImpl(FLD_MSG_TEXT, Field.FT_STRING, Field.FF_NONE));
        ur.addField(new FieldImpl(FLD_TIMEOUT, Field.FT_STRING, Field.FF_NONE));
        ur.addField(new FieldImpl(FLD_RESPONSE, Field.FT_STRING, Field.FF_NONE, 1024));
        ur.addField(new FieldImpl(FLD_DESCIPTION, Field.FT_STRING, Field.FF_NONE, 1024));        
        ur.addField(new FieldImpl(FLD_PROJECT_ID, Field.FT_INTEGER));
        ur.addField(new FieldImpl(FLD_QUERY_PARENT_ID, Field.FT_INTEGER, Field.FF_NONE));
        ur.addField(new FieldImpl(FLD_DISPLAY_RESULT, Field.FT_INTEGER));
        ur.addField(new FieldImpl(FLD_DS_STATUS, Field.FT_INTEGER));        
        ur.addField(new FieldImpl(FLD_WS_ID, Field.FT_INTEGER));      
        
        ur.addField(new FieldImpl(FLD_DEL_STATUS, Field.FT_INTEGER, Field.FF_NONE));
        // return the entity
        return ur;
    }

    //--------------------------------------------------------------------------
    /**
     * Return the Entity name.
     * @return the entity name.
     */
    public String getEntityName()
    {
        return ENT_USER_REPLY;
    }
    //--------------------------------------------------------------------------
    /**
     * @return the ID
     */
    public int getId()
    {
        return getIntValue(FLD_ID);
    }
    //  --------------------------------------------------------------------------
 	/**
 	 * @return int the id of this project
 	 */
 	public int
 	getProjectId ()
 	{
 		return getIntValue (FLD_PROJECT_ID);
 	}
 	//	--------------------------------------------------------------------------
 	/**
 	 * Set the id of this project
 	 * @param id
 	 */
 	public void
 	setProjectId (
 	 int id)
 	{
 		setValue (FLD_PROJECT_ID, id);
 	}  
 	
	public String getType() {
		return getStringValue(FLD_TYPE);
	}


	public void setType(String fld_type) {
		setValue(FLD_TYPE, fld_type);
	}    

    public int getViewOrTableId()
    {
        return getIntValue(FLD_VIEW_TABLE_ID);
    }

    /**
     * Set the id of the parent dataview of this query
     * @param id the parent DataView id
     */
    public void setViewOrTableId(int id)
    {
        setValue(FLD_VIEW_TABLE_ID, id);
    }
	
    //--------------------------------------------------------------------------
    /**
     * Set the ID.
     */
    public void setId(int id)
    {
        setValue(FLD_ID, id);
    }

    //--------------------------------------------------------------------------    
    /**
     * @return int the databaseId
     */
    public int getDatabaseId()
    {
        return getIntValue(FLD_DB_ID);
    }
    
    //--------------------------------------------------------------------------
    /**
     * Set the databaseId
     * @param databaseId
     */
    public void setDatabaseId(int database)
    {
        setValue(FLD_DB_ID, database);
    }
    //--------------------------------------------------------------------------    
    public String getCriteriaString()
    {
        return getStringValue(FLD_CRITERIA);
    }

    /**
     * Sets the criteria into the query
     * @param criteria
     * @return void
     */
    public void setCriteriaString(String criteria)
    {
        setValue(FLD_CRITERIA, criteria);
    }
    /**
     * Returns the sort of the query
     * @return String
     */
    public String getSortString()
    {
        return getStringValue(FLD_SORTS);
    }

    /**
     * Sets the sort string into the query
     * @param sorts
     * @return void
     */
    public void setSortString(String sorts)
    {
        setValue(FLD_SORTS, sorts);
    }
    /**
     * @return int the id of the grouping field
     */
    public int getGroupFieldId()
    {
        return getIntValue(FLD_GROUPFIELDID);
    }

    /**
     * Set the id of the grouping field
     * @param id
     */
    public void setGroupFieldId(Integer id)
    {
        setValue(FLD_GROUPFIELDID, id);
    }
    //--------------------------------------------------------------------------    
    /**
     * @return int the parentId
     */
    public int getParentId()
    {
        return getIntValue(FLD_PARENT_ID);
    }
    
    //--------------------------------------------------------------------------
    /**
     * Set the parentId
     * @param parentId
     */
    public void setParentId(int parentId)
    {
        setValue(FLD_PARENT_ID, parentId);
    }
    //--------------------------------------------------------------------------    
    /**
     * @return int the children
     */
    public String getChildren()
    {
        return getStringValue(FLD_CHILDREN);
    }
    
    //--------------------------------------------------------------------------
    /**
     * Set the children
     * @param children
     */
    public void setChildren(String children)
    {
    	setValue(FLD_CHILDREN, children);
    }
    
    //-----------------------------------------------------------------------
    /**
     * Get the response
     * @return the response.
     */
     public String getMsgText()
     {
          return getStringValue(FLD_MSG_TEXT);
     }
     //--------------------------------------------------------------------------
     /**
     * Set the response.
     * @param response
     */
     public void setMsgText(String msgText)
     {
        setValue(FLD_MSG_TEXT, msgText);
     }
    
    //---------------------------------------------------------------------------
    /**
     * Get the timeout
     * @return the timeout.
     */
     public String getTimeout()
     {
          return getStringValue(FLD_TIMEOUT);
     }
     //--------------------------------------------------------------------------
     /**
     * Set the timeout.
     * @param timeout
     */
     public void setTimeout(String timeout)
     {
        setValue(FLD_TIMEOUT, timeout);
     }
     //-----------------------------------------------------------------------
     /**
      * Get the response
      * @return the response.
      */
      public String getResponse()
      {
           return getStringValue(FLD_RESPONSE);
      }
      //--------------------------------------------------------------------------
      /**
      * Set the response.
      * @param response
      */
      public void setResponse(String response)
      {
         setValue(FLD_RESPONSE, response);
      }

      //-----------------------------------------------------------------------
      /**
       * Get the description
       * @return the description.
       */
       public String getDescription()
       {
            return getStringValue(FLD_DESCIPTION);
       }
       
       //--------------------------------------------------------------------------    
       /**
        * @return int the QueryId
        */
       public int getQueryId()
       {
           return getIntValue(FLD_QUERY_PARENT_ID);
       }
       
       //--------------------------------------------------------------------------
       /**
        * Set the queryId
        * @param queryId
        */
       public void setQueryId(int queryId)
       {
           setValue(FLD_QUERY_PARENT_ID, queryId);
       }
       
       //--------------------------------------------------------------------------
       /**
       * Set the description.
       * @param description
       */
       public void setDescription(String description)
       {
          setValue(FLD_DESCIPTION, description);
       }
       //---------------------------------------------------------------------------
       /**
        * Get the displayResult
        * @return the DisplayResult.
        */
        public int getDisplayResult()
        {
             return getIntValue(FLD_DISPLAY_RESULT);
        }
        //--------------------------------------------------------------------------    
        /**
         * @return int the datasourceStatus
         */
        public int getDatasourceStatus()
        {
            return getIntValue(FLD_DS_STATUS);
        }
        
        //--------------------------------------------------------------------------
        /**
         * Set the datasourceStatus
         * @param datasourceStatus
         */
        public void setDatasourceStatus(int datasourceStatus)
        {
            setValue(FLD_DS_STATUS, datasourceStatus);
        }
        
        //--------------------------------------------------------------------------
        /**
        * Set the DisplayResult.
        * @param DisplayResult
        */
        public void setDisplayResult(int displayResult)
        {
           setValue(FLD_DISPLAY_RESULT, displayResult);
        }

        //--------------------------------------------------------------------------    
        /**
         * @return int the webServiceId
         */
        public int getWebServiceId()
        {
            return getIntValue(FLD_WS_ID);
        }
        
        //--------------------------------------------------------------------------
        /**
         * Set the webServiceId
         * @param webServiceId
         */
        public void setWebServiceId(int webServiceId)
        {
            setValue(FLD_WS_ID, webServiceId);
        }
        
        //----------------------------------------------------------------------------
        /**
         * @return int the Delete Status
         */
        public int getDelStatus()
        {
            return getIntValue(FLD_DEL_STATUS);
        }
        
        //--------------------------------------------------------------------------
        /**
         * Set the DeleteStatus
         * @param delStatus
         */
        public void setDelStatus(int delStatus)
        {
            setValue(FLD_DEL_STATUS, delStatus);
        }
        
      private transient DataView _dv = null;
      private transient EntityDobj _table = null;      
      /**
       * Returns the transient DataView of the query
       * @param doLoad if true loads dv from db
       * @return DataView
       */
      public DataView getDataView(boolean doLoad)
      {
    	  // if null, load
          if(_dv == null && doLoad && this.getViewOrTableId() >= 0)
          {
              try
              {
                  Query q = new Query(DataView.ENT_DATAVIEW);
                  q.addQueryCriterium(new QueryCriterium(DataView.ENT_DATAVIEW,
                      DataView.FLD_ID, QueryCriterium.OP_EQUALS,
                      new Integer(this.getViewOrTableId())));
                  RecordSet rs = DataManager.getSystemDS().select(q);
                  if(rs != null && rs.next())
                      _dv = (DataView)rs.getCurrentObject();
              }
              catch(Exception e)
              {
                  throw new RuntimeException(e.toString());
              }
          }
          // return
          return _dv;
      }
      
      public EntityDobj getTable(boolean doLoad)
      {
          // if null, load
          if(_table == null && doLoad && this.getViewOrTableId() >= 0)
          {
              try
              {
                  Query q = new Query(EntityDobj.ENT_ENTITY);
                  q.addQueryCriterium(new QueryCriterium(EntityDobj.ENT_ENTITY,
                      DataView.FLD_ID, QueryCriterium.OP_EQUALS,
                      new Integer(this.getViewOrTableId())));
                  RecordSet rs = DataManager.getSystemDS().select(q);
                  if(rs != null && rs.next())
                  	_table = (EntityDobj)rs.getCurrentObject();
              }
              catch(Exception e)
              {
                  throw new RuntimeException(e.toString());
              }
          }

          // return
          return _table;
      }   
}


