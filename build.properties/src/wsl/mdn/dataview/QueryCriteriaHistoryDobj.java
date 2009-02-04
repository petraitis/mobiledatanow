//==============================================================================
// QueryDobj.java
// Copyright (c) 2001 WAP Solutions Ltd.
//==============================================================================

package wsl.mdn.dataview;

// imports
import java.util.Date;

import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.DefaultKeyGeneratorData;
import wsl.fw.datasource.Entity;
import wsl.fw.datasource.EntityImpl;
import wsl.fw.datasource.Field;
import wsl.fw.datasource.FieldImpl;

//------------------------------------------------------------------------------
/**
 * DataObject that persists Query Criteria
 */
public class QueryCriteriaHistoryDobj extends DataObject
{
    // version tag
    private final static String _ident = "$Date: 2003/06/12 23:26:09 $  $Revision: 1.2 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/dataview/QueryCriteriaDobj.java $ ";

    //--------------------------------------------------------------------------
    // constants

    public final static String ENT_QUERY_CRITERIA_HISTORY   = "TBL_QUERY_CRITERIA_HISTORY";
    public final static String FLD_ID               = "FLD_ID";
    public final static String FLD_MSG_ID           = "FLD_MSG_ID";
    public final static String FLD_ORIGINAL_ID      = "FLD_ORIGINAL_ID";
    public final static String FLD_QUERY_ID       	= "FLD_QUERY_ID";
    public final static String FLD_VALUEORCOND      = "FLD_VALUEORCOND";
    public final static String FLD_ROW_NO         	= "FLD_ROW_NO";
    public final static String FLD_TYPE             = "FLD_TYPE";
    public final static String FLD_USED             = "FLD_USED";
    public final static String FLD_INDENT           = "FLD_INDENT";
    public final static String FLD_PARENT           = "FLD_PARENT";
    public final static String FLD_NUMBER           = "FLD_NUMBER";
    public final static String FLD_NAME             = "FLD_NAME";
    public final static String FLD_COMP_ID      	= "FLD_COMP_ID";
    public final static String FLD_COMPARISON       = "FLD_COMPARISON";
    public final static String FLD_VALUE     		= "FLD_VALUE";
    public final static String FLD_CONNECTION      	= "FLD_CONNECTION";
    public final static String FLD_VALUE2           = "FLD_VALUE2";
    public final static String FLD_GROUPING         = "FLD_GROUPING";
    public final static String FLD_DATETIME         = "FLD_DATETIME";

    public final static String DELIM_AND            = "\f";
    public final static String DELIM_OPS            = "\t";

    //--------------------------------------------------------------------------
    // attributes



    //--------------------------------------------------------------------------
    // construction

    /**
     * Blank ctor
     */
    public QueryCriteriaHistoryDobj()
    {
    }


    //--------------------------------------------------------------------------
    // persistence

    /**
     * Static factory method to create the entity to be used by this dataobject
     * and any subclasses. This is called by the DataManager's factory when
     * creating a ENTITY entity.
     * @return the created entity.
     */
    public static Entity createEntity()
    {
        // create the entity
        Entity ent = new EntityImpl(ENT_QUERY_CRITERIA_HISTORY, QueryCriteriaHistoryDobj.class);

        // add the key generator for the system id
        ent.addKeyGeneratorData(new DefaultKeyGeneratorData(ENT_QUERY_CRITERIA_HISTORY, FLD_ID));

        // create the fields and add them to the entity
        ent.addField(new FieldImpl(FLD_ID, Field.FT_INTEGER, Field.FF_SYSTEM_KEY | Field.FF_UNIQUE_KEY));
        ent.addField(new FieldImpl(FLD_ORIGINAL_ID, Field.FT_INTEGER));
        ent.addField(new FieldImpl(FLD_MSG_ID, Field.FT_INTEGER));
        ent.addField(new FieldImpl(FLD_QUERY_ID, Field.FT_INTEGER));
        ent.addField(new FieldImpl(FLD_VALUEORCOND, Field.FT_STRING)); 
        ent.addField(new FieldImpl(FLD_ROW_NO, Field.FT_INTEGER));
        ent.addField(new FieldImpl(FLD_TYPE, Field.FT_STRING));
        ent.addField(new FieldImpl(FLD_USED, Field.FT_INTEGER));
        ent.addField(new FieldImpl(FLD_INDENT, Field.FT_INTEGER));
        ent.addField(new FieldImpl(FLD_PARENT, Field.FT_STRING));
        ent.addField(new FieldImpl(FLD_NUMBER, Field.FT_INTEGER));
        ent.addField(new FieldImpl(FLD_NAME, Field.FT_STRING, Field.FF_NAMING));
        ent.addField(new FieldImpl(FLD_COMP_ID, Field.FT_INTEGER));
        ent.addField(new FieldImpl(FLD_COMPARISON, Field.FT_STRING, Field.FF_NONE, 1024));
        ent.addField(new FieldImpl(FLD_VALUE, Field.FT_STRING, Field.FF_NONE, 1024));
        ent.addField(new FieldImpl(FLD_CONNECTION, Field.FT_STRING, Field.FF_NONE, 1024));
        ent.addField(new FieldImpl(FLD_VALUE2, Field.FT_STRING, Field.FF_NONE, 1024)); 
        ent.addField(new FieldImpl(FLD_GROUPING, Field.FT_STRING)); 
        ent.addField(new FieldImpl(FLD_DATETIME, Field.FT_DATETIME)); 
        // return the entity
        return ent;
    }

    /**
     * @return String the name of the entity that defines this DataObject
     */
    public String getEntityName()
    {
        return ENT_QUERY_CRITERIA_HISTORY;
    }


    //--------------------------------------------------------------------------
    // accessors


    /**
     * @return int the id of this query
     */
    public int getId()
    {
        return getIntValue(FLD_ID);
    }

    /**
     * set the id.
     */
    public void setId(Object id)
    {
        setValue(FLD_ID, id);
    }

    /**
     * @return int the id of this query
     */
    public int getMsgId()
    {
        return getIntValue(FLD_MSG_ID);
    }

    /**
     * set the id.
     */
    public void setMsgId(Object id)
    {
        setValue(FLD_MSG_ID, id);
    }     
    
    /**
     * @return int the id of this query
     */
    public int getOriginalId()
    {
        return getIntValue(FLD_ORIGINAL_ID);
    }

    /**
     * set the id.
     */
    public void setOriginalId(Object id)
    {
        setValue(FLD_ORIGINAL_ID, id);
    }    
    
    /**
     * Returns the name of the query
     * @return String
     */
    public String getName()
    {
        return getStringValue(FLD_NAME);
    }

    /**
     * Sets the query name into the query
     * @param name
     * @return void
     */
    public void setName(String name)
    {
        setValue(FLD_NAME, name);
    }

	public String getType() {
		return getStringValue(FLD_TYPE);
	}


	public void setType(String fld_type) {
		setValue(FLD_TYPE, fld_type);
	}      
    
    /**
     * @return int the id of the parent DataView of this query
     */
    public int getQueryId()
    {
        return getIntValue(FLD_QUERY_ID);
    }

    /**
     * Set the id of the parent dataview of this query
     * @param id the parent DataView id
     */
    public void setQueryId(int id)
    {
        setValue(FLD_QUERY_ID, id);
    }

    /**
     * Returns the criteria of the query
     * @return String
     */
    public int getRowNo()
    {
        return getIntValue(FLD_ROW_NO);
    }

    /**
     * Sets the criteria into the query
     * @param criteria
     * @return void
     */
    public void setRowNo(int rowNo)
    {
        setValue(FLD_ROW_NO, rowNo);
    }

    /**
     * Returns the sort of the query
     * @return String
     */
    public int getCompId()
    {
        return getIntValue(FLD_COMP_ID);
    }

    /**
     * Sets the sort string into the query
     * @param sorts
     * @return void
     */
    public void setCompId(int compId)
    {
        setValue(FLD_COMP_ID, compId);
    }

    /**
     * Returns the description of the query
     * @return String
     */
    public String getComparison()
    {
        return getStringValue(FLD_COMPARISON);
    }

    /**
     * Sets the query description into the query
     * @param name
     * @return void
     */
    public void setComparison(String comparison)
    {
        setValue(FLD_COMPARISON, comparison);
    }

    /**
     * @return int the id of the grouping field
     */
    public String getValue()
    {
        return getStringValue(FLD_VALUE);
    }

    /**
     * Set the id of the grouping field
     * @param id
     */
    public void setValue(String value)
    {
        setValue(FLD_VALUE, value);
    }

	public String getConnection() {
		return getStringValue(FLD_CONNECTION);
	}


	public void setConnection(String connection) {
		setValue(FLD_CONNECTION, connection);
	}


	public String getGrouping() {
		return getStringValue(FLD_GROUPING);
	}


	public void setGrouping(String grouping) {
		setValue(FLD_GROUPING, grouping);
	}


	public String getValue2() {
		return getStringValue(FLD_VALUE2);
	}


	public void setValue2(String value2) {
		setValue(FLD_VALUE2, value2);
	}


	public String getValueOrCondition() {
		return getStringValue(FLD_VALUEORCOND);
	}


	public void setValueOrCondition(String fld_valueorcond) {
		setValue(FLD_VALUEORCOND, fld_valueorcond);
	}


	public int getUsed() {
		return getIntValue(FLD_USED);
	}


	public void setUsed(int fld_used) {
		setValue(FLD_USED, fld_used);
	}


	public int getIndent() {
		return getIntValue(FLD_INDENT);
	}


	public void setIndent(int fld_indent) {
		setValue(FLD_INDENT, fld_indent);
	}


	public String getParent() {
		return getStringValue(FLD_PARENT);
	}


	public void setParent(String fld_parent) {
		setValue(FLD_PARENT, fld_parent);
	}


	public int getNumber() {
		return getIntValue(FLD_NUMBER);
	}


	public void setNumber(int fld_number) {
		setValue(FLD_NUMBER, fld_number);
	}
	
    /**
     * @return int the id of this query
     */
    public Date getDateTime()
    {
        return getDateValue(FLD_DATETIME);
    }

    /**
     * set the id.
     */
    public void setDateTime(Object date)
    {
        setValue(FLD_DATETIME, date);
    }	
}

//==============================================================================
// end of file QueryDobj.java
//==============================================================================
