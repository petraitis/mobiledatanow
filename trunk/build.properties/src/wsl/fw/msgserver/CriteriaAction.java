/*	$Header: /wapsolutions/cvsroot/mdn/fw/src/wsl/fw/msgserver/CriteriaAction.java,v 1.1.1.1 2002/06/11 23:11:42 jonc Exp $
 *
 * Add Criteria to actions
 *
 */
package wsl.fw.msgserver;

import java.util.Iterator;
import java.util.Vector;
import wsl.fw.util.Log;
import wsl.fw.datasource.RecordSet;
import wsl.fw.datasource.RecordItrClient;
import wsl.fw.datasource.RecordItrDSClient;
import wsl.fw.datasource.DataSource;
import wsl.fw.datasource.DataSourceException;

/**
 * Adds criteria to actions
 */
public class CriteriaAction extends ActionDobj
{
    //--------------------------------------------------------------------------
    // attributes
    private String _label;
    private String _anchorText;
	private Class _clazz;				// filter class
    private Object _value;

    //--------------------------------------------------------------------------
    // construction

    /**
     * Default ctor
     */
    public
	CriteriaAction()
    {
    }

    /**
     * Param ctor
     * @param actionType the action type
     */
    public
	CriteriaAction (
	 int actionType,
	 String name,
	 String id,
	 Class clazz,
	 String label,
	 String anchorText)
    {
        // super
        super(actionType, name, id);

        // attribs
		_clazz = clazz;
        _label = label;
        _anchorText = anchorText;
    }


    //--------------------------------------------------------------------------
    // accessors

    /**
     * Get the criterium label
     */
    public String
	getCriteriumLabel ()
    {
        return _label;
    }

	/**
	 *	Get the filtering class
	 */
	public Class
	getCriteriumClass ()
	{
		return _clazz;
	}

    /**
     * Set the criterium value
     * @param value
     */
    public void
	setCriteriumValue (
	 Object value)
    {
        _value = value;
    }

    /**
     * @return the criterium value
     */
    public Object
	getCriteriumValue ()
    {
        return _value;
    }

    /**
     * @return the anchor text
     */
    public String
	getAnchorText()
    {
        return _anchorText;
    }
}