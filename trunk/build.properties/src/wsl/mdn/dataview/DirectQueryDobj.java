//==============================================================================
// DirectQueryDobj.java
// Copyright (c) 2001 WAP Solutions Ltd.
//==============================================================================

package wsl.mdn.dataview;

// imports
import java.util.Vector;
import java.util.StringTokenizer;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.datasource.*;
import wsl.mdn.guiconfig.MenuAction;
import java.net.URLDecoder;
import java.net.URLEncoder;

//------------------------------------------------------------------------------
/**
 * DataObject that persists Queries
 */
public class DirectQueryDobj extends QueryDobj
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:35:35 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/dataview/DirectQueryDobj.java $ ";

    //--------------------------------------------------------------------------
    // attributes

    private transient DataView _dv = null;
    private String _imageQuery = null;

    //--------------------------------------------------------------------------
    // construction

    /**
     * Blank ctor
     */
    public DirectQueryDobj()
    {
    }

    //--------------------------------------------------------------------------
    // accessors

    /**
     * Returns the raw SQL query
     * @return String
     */
    public String getRawSQL()
    {
        return URLDecoder.decode(getStringValue(FLD_RAWQUERY));
    }

    /**
     * Sets the raw SQL query
     * @param query
     * @return void
     */
    public void setRawSQL(String query)
    {
        setValue(FLD_RAWQUERY, URLEncoder.encode(query));
    }


    /**
     * Create and return the DirectQuery object
     * @return Query
     */
    public Query createNewImpl()
    {
        // create
        DirectQuery impl = new DirectQuery(getRawSQL());
        return impl;
    }

    //--------------------------------------------------------------------------
    /**
     * Create and return the DirectQuery object
     * @return Query
     */
    public Query createImpl()
    {
        // create
        DirectQuery impl = new DirectQuery(getRawSQL());
        return impl;
    }


    //--------------------------------------------------------------------------
    // criteria

    /**
     * Add a criterium
     * @param qc the criterium
     */
    public void addCriterium(QueryCriterium qc)
    {
    }

    /**
     * Remove a criterium
     */
    public QueryCriterium removeCriterium(QueryCriterium qc, boolean doDelete)
    {
        return null;
    }

    /**
     * Get the Vector of QueryCriteriums
     * @return Vector the Query Criteria
     */
    public Vector getCriteria()
    {
        return null;
    }

    /**
     * Set criteria vector
     * @param criteria the new criteria Vector
     */
    public void setCriteria(Vector criteria)
    {
    }

    /**
     * @return String a string containing all criteria
     */
    public String createCriteriaString()
    {
        return "";
    }

    //--------------------------------------------------------------------------
    // sorts

    /**
     * Add a sort
     * @param sort the Sort
     */
    public void addSort(Sort sort)
    {
    }

    /**
     * Remove a sort
     */
    public Sort removeSort(Sort sort, boolean doDelete)
    {
        return null;
    }

    /**
     * Get the Vector of Sorts
     * @return Vector the Sorts
     */
    public Vector getSorts()
    {
        return null;
    }

    /**
     * Parse a sort
     * @param str the string sort
     * @return Sort
     */
    private Sort parseSort(String str)
    {
        return null;
    }

    /**
     * Set sorts vector
     * @param sorts the new sorts Vector
     */
    public void setSorts(Vector sorts)
    {
    }

    /**
     * @return String a string containing all sorts
     */
    public String createSortString()
    {
        return "";
    }

    //--------------------------------------------------------------------------
    // imaging

    /**
     * Start an imaging session
     */
    public void imageQuery()
    {
        // clone
        String str = getStringValue(FLD_RAWQUERY);
        if(str != null)
            _imageQuery = new String(str);
        _isImaging = true;
    }

    /**
     * Reverts to the image
     */
    public void revertToImage()
    {
        // must be imaging
        if(isImaging())
        {
            // set the strings back to the image
            setValue(FLD_RAWQUERY, _imageQuery);

            // clear the image
            clearImage();
        }
    }

    /**
     * Process and finish the imaging
     */
    public void processImaging()
    {
        clearImage();
    }

    /**
     * Clear the image
     */
    public void clearImage()
    {
        // clear the image strings
        _imageQuery = null;
        _isImaging = false;
    }

    /**
     * @return boolean true if imaging
     */
    public boolean isImaging()
    {
        return _isImaging;
    }


    //--------------------------------------------------------------------------
    // pre save

    /**
     * Called before a save
     */
    public void preSave() throws DataSourceException
    {
    }
}

//==============================================================================
// end of file DirectQueryDobj.java
//==============================================================================
