/**	$Header: /wapsolutions/cvsroot/mdn/fw/src/wsl/fw/msgserver/ItemDobj.java,v 1.1.1.1 2002/06/11 23:11:42 jonc Exp $
 *
 * Super class for all message elements (as opposed to aggregates) They have fields and
 * can be displayed by property pages
 *
 */
package wsl.fw.msgserver;

import java.util.Vector;
import java.util.Hashtable;

public abstract class ItemDobj extends MessageDobj
{
    //--------------------------------------------------------------------------
    // attributes

    private Vector _fields = new Vector();
    private Hashtable _values = new Hashtable(89);
    private Object _userId = null;


    //--------------------------------------------------------------------------
    // construction

    /**
     * Blank ctor
     */
    public ItemDobj()
    {
        // create fields
        createFields();
    }

    //--------------------------------------------------------------------------
    // accessors

    /**
     * Get the user id
     * @return the user id
     */
    public Object getUserId()
    {
        return _userId;
    }

    /**
     * Set the user id
     * @param userId the user id
     */
    public void setUserId(Object userId)
    {
        _userId = userId;
    }

    //--------------------------------------------------------------------------
    // fields

    /**
     * Abstract method for subs to create / add fields
     */
    protected abstract void createFields();


    /**
     * Add a field to the fields table
     * @param label a label for the field
     * @param value the field value
     */
    protected void addField(String label, String value)
    {
        _fields.add(label);
        _values.put(label, value);
    }

    /**
     * Add a field to the fields table
     * @param label a label for the field
     */
    protected void addField(String label)
    {
        _fields.add(label);
    }

    /**
     * Set the value of a field
     * @param label the identifying label
     * @param value
     */
    public void setFieldValue(String label, String value)
    {
        _values.put(label, value);
    }

    /**
     * @return the field label vector
     */
    public Vector getFieldLabels()
    {
        return _fields;
    }

    /**
     * Get a field value from a label
     * @param label
     * @return field value
     */
    public String getFieldValue(String label)
    {
        return (String)_values.get(label);
    }

	/**
	 *	Does the field support click & dial?
	 */
	public boolean
	isPhonedialField (
	 String label)
	{
		return false;
	}

	/**
	 *	Is the field and email address?
	 */
	public boolean
	isEmailField (
	 String label)
	{
		return false;
	}
}