//==============================================================================
// QueryCriterium.java
// Copyright (c) 2000-2001 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.datasource;

// imports
import java.util.Calendar;
import java.util.Date;
import java.io.Serializable;
import wsl.fw.util.Type;

//------------------------------------------------------------------------------
/**
 * Class to encapsulate information about a single criterium in a Query
 */
public class QueryCriterium extends Query implements Serializable
{
    // version tag
    private final static String _ident = "$Date: 2003/06/12 23:27:28 $  $Revision: 1.2 $ "
        + "$Archive: /Framework/Source/wsl/fw/datasource/QueryCriterium.java $ ";

    // constants
    public static final String OP_EQUALS = "=";
    public static final String OP_NOT_EQUALS = "<>";
    public static final String OP_LIKE = "LIKE";
    public static final String OP_NOT_LIKE = "NOT LIKE";
    public static final String OP_GREATER_THAN = ">";
    public static final String OP_GREATER_THAN_EQUALS = ">=";
    public static final String OP_LESS_THAN = "<";
    public static final String OP_LESS_THAN_EQUALS = "<=";
    public static final String OP_IS_NULL = "IS NULL";
    public static final String OP_IS_NOT_NULL = "IS NOT NULL";

    public static final String INCOMPLETE_VALUE = "<?>";
    public static final String USERID_VALUE = "<UID>";
    
    // functions
    public static final String FN_DATE_NOW = "<now>";
    public static final String FN_DATE_TODAY = "<today>";
    public static final String[] FUNCTIONS = {FN_DATE_NOW, FN_DATE_TODAY};

    // attributes
    public String _fieldName = "";
    public String _entityName = "";
    public String _op = "";
    public Object _value = null;
    public boolean _orIsNull = false;


    //--------------------------------------------------------------------------
    /**
     * Default ctor
     */
    public QueryCriterium()
    {
    }

    /**
     * Copy ctor
     * @param qc the QueryCriterium to copy
     */
    public QueryCriterium(QueryCriterium qc)
    {
        this(qc._entityName, qc._fieldName, qc._op, qc._value);
    }

    //--------------------------------------------------------------------------
    /**
     * Sets the attributes of the QueryCriterium object
     * @param entityName
     * @param fieldName
     * @param op
     * @param value
     * @roseuid 3973DEA60180
     */
    public QueryCriterium(String entityName, String fieldName, String op, Object value)
    {
        // set attribs
        _entityName = entityName;
        _fieldName = fieldName;
        _op = op;
        _value = value;

        // add self to query
        addQueryCriterium(this);
    }

    //--------------------------------------------------------------------------
    /**
     * toString()
     */
    public String toString()
    {
        return _entityName + "." + _fieldName + " " + _op
            + ((isBinary()) ? (" " + _value) : "");
    }

    //--------------------------------------------------------------------------
    /**
     * @return true if the criterium is complete (i.e. has an operation and,
     *   if binary, a value.
     */
    public boolean isComplete()
    {
        return (_op != null && (!isBinary() || !(_value == null || _value.equals(INCOMPLETE_VALUE))));
    }

    //--------------------------------------------------------------------------
    /**
     * @return true if the operator is binary, false if unary, null or unknown.
     */
    public boolean isBinary()
    {
        return isBinary(_op);
    }

    //--------------------------------------------------------------------------
    /**
     * @param operation the operation to test.
     * @return true if the operator is binary, null or unknown, false if unary.
     */
    public static boolean isBinary(String operation)
    {
        if (operation != null)
        {
            // test for unary operators
            if (operation.equals(OP_IS_NULL))
                return false;
            if (operation.equals(OP_IS_NOT_NULL))
                return false;
        }

        return true;
    }

    //--------------------------------------------------------------------------
    // unary

    /**
     * @return boolean true if operator is unary
     */
    public boolean isUnary()
    {
        return !isBinary();
    }

    /**
     * @param operation the operation to check
     * @return boolean true if operator is unary
     */
    public static boolean isUnary(String operation)
    {
        return !isBinary(operation);
    }


    //--------------------------------------------------------------------------
    // OR IS NULL

    /**
     * Set the or is null flag
     * @param b
     */
    public void setOrIsNull(boolean b)
    {
        _orIsNull = b;
    }

    /**
     * @return boolean the or is null flag
     */
    public boolean orIsNull()
    {
        return _orIsNull;
    }


    //--------------------------------------------------------------------------
    // Empty?

    /**
     * @return boolean true if the criterium is unary || binary and the value is Empty
     * as defined by Type.isEmpty()
     */
    public boolean isValueEmpty()
    {
        return isValueEmpty(_value);
    }

    /**
     * @param the value to check for emptiness
     * @return boolean true if the criterium is unary || binary and the param value is Empty
     * as defined by Type.isEmpty()
     */
    public boolean isValueEmpty(Object value)
    {
        return Type.isValueEmpty(value);
    }
    
    
    //--------------------------------------------------------------------------
    // functions
    
    /**
     * @return true if this query criterium has a function for a value
     */
    public boolean isFunction()
    {
        // no value no function
        String val = (_value == null)? null: _value.toString().trim();
        if(val == null || val.length() == 0)
            return false;
        
        // iterate function array
        for(int i = 0; i < FUNCTIONS.length; i++)
        {
            // compare
            if(val.equalsIgnoreCase(FUNCTIONS[i]))
                return true;
        }
        
        // not a function
        return false;
    }
    
    /**
     * Evaluate the function and set a value inot the _value attributes
     */
    public void evaluateFunction()
    {
        // no function, no evaluate
        if(!this.isFunction())
            return;
        
        // switch on function
        String val = (_value == null)? null: _value.toString().trim();
        if(val.equalsIgnoreCase(FN_DATE_NOW))
            _value = evaluateDateNow();
        else if(val.equalsIgnoreCase(FN_DATE_TODAY))
            _value = evaluateDateToday();
    }
    
    
    //--------------------------------------------------------------------------
    // date functions
    
    /**
     * @return the date for now
     */
    private Date evaluateDateNow()
    {
        return new java.util.Date();
    }
    
    /**
     * @return the date for 0:00:00 today
     */
    private Date evaluateDateToday()
    {
        // set time to now
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        
        // set hour, min, sec, millis to 0
        // this gives us 0;00;00 this morning ie the beginning of today
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}

//==============================================================================
// end of file QueryCriterium.java
//==============================================================================
