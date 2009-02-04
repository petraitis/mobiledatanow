//Source file: D:\\dev\\wsl\\mdn\\dataview\\JdbcDataSourceDobj.java

package wsl.mdn.dataview;

// imports
import wsl.fw.datasource.*;
import java.util.Vector;

//------------------------------------------------------------------------------
/**
 * DataObject used to persist JdbcDataSource objects
 */
public class JdbcDataSourceDobj extends DataSourceDobj
{
    // attributes
    private transient DataSource _impl = null;
    private transient JdbcDriver _driver = null;


    //--------------------------------------------------------------------------
    // construction

    /**
     * Blank ctor
     */
    public JdbcDataSourceDobj()
    {
    }


    //--------------------------------------------------------------------------
    // accessors
    //--------------------------------------------------------------------------
    /**
     * Returns the jdbc driver
     * @return String
     */
    public String getJdbcDriver()
    {
        return getDriverDobj() == null ? "" : getDriverDobj().getDriver();
    }
    //--------------------------------------------------------------------------
    /**
     * Returns the jdbc url
     * @return String
     */
    public String getJdbcUrl()
    {
        return getStringValue(DataSourceDobj.FLD_JDBC_URL);
    }

    //--------------------------------------------------------------------------
    /**
     * Sets the jdbc url
     * @param val
     * @return void
     */
    public void setJdbcUrl(String val)
    {
        setValue(DataSourceDobj.FLD_JDBC_URL, val);
    }

    //--------------------------------------------------------------------------
    /**
     * Returns the jdbc catalog
     * @return String
     */
    public String getJdbcCatalog()
    {
        return getStringValue(DataSourceDobj.FLD_JDBC_CATALOG);
    }

    //--------------------------------------------------------------------------
    /**
     * Sets the jdbc catalog
     * @param val
     * @return void
     */
    public void setJdbcCatalog(String val)
    {
        setValue(DataSourceDobj.FLD_JDBC_CATALOG, val);
    }

    //--------------------------------------------------------------------------
    /**
     * Returns the jdbc user
     * @return String
     */
    public String getJdbcUser()
    {
        return getStringValue(DataSourceDobj.FLD_JDBC_USER);
    }

    //--------------------------------------------------------------------------
    /**
     * Sets the jdbc user
     * @param val
     * @return void
     */
    public void setJdbcUser(String val)
    {
        setValue(DataSourceDobj.FLD_JDBC_USER, val);
    }

    //--------------------------------------------------------------------------
    /**
     * Returns the jdbc password
     * @return String
     */
    public String getJdbcPassword()
    {
        return getStringValue(DataSourceDobj.FLD_JDBC_PASSWORD);
    }

    //--------------------------------------------------------------------------
    /**
     * Sets the jdbc password
     * @param val
     * @return void
     */
    public void setJdbcPassword(String val)
    {
        setValue(DataSourceDobj.FLD_JDBC_PASSWORD, val);
    }

    //--------------------------------------------------------------------------
    /**
     * @return int the jdbcd driver id
     */
    public int getJdbcDriverId()
    {
        return getIntValue(FLD_JDBC_DRIVERID);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the jdbc driver id
     * @param id
     */
    public void setJdbcDriverId(int id)
    {
        setValue(FLD_JDBC_DRIVERID, id);
    }


    //--------------------------------------------------------------------------
    /**
     * driver
     */
    public JdbcDriver getDriverDobj()
    {
        try
        {
            // if null, load
            if(true)//_driver == null)
            {
                int did = getJdbcDriverId();
                if(did > 0)
                {
                    // load it
                    _driver = new JdbcDriver();
                    _driver.setId(did);

                    // if it doesnt load, null it
                    if(!_driver.load())
                        _driver = null;
                }
            }
        }
        catch(Exception e)
        {
            throw new RuntimeException(e.toString());
        }

        // return
        return _driver;
    }

    /**
     * Set the driver dobj
     */
    public void setDriverDobj(JdbcDriver driver)
    {
        _driver = driver;
    }


    //--------------------------------------------------------------------------
    // impl

    /**
     * Create and return the DataSource object.
     * @return DataSource
     */
    public DataSource createImpl()
    {
        if (_impl == null)
        {
            // create a param defining the datasource
            
        	JdbcDataSourceParam dsParam = new JdbcDataSourceParam(getName(),
                getJdbcDriver(), getJdbcUrl() , //getJdbcCatalog(),
                getJdbcUser(), getJdbcPassword());//+ ":" + getJdbcCatalog()

            // get/create the datasource
            _impl = DataManager.getDataSource(dsParam);

            // get the entities, create and add them
            Vector v = getEntities();
            for (int i = 0; i < v.size(); i++)
            {
                EntityDobj ed = (EntityDobj) v.get(i);
                _impl.addEntity(ed.createImpl());
            }

            // get the joins, create and add them
            v = getJoins();
            for (int i = 0; i < v.size(); i++)
            {
                JoinDobj jd = (JoinDobj) v.get(i);
                _impl.addJoin(jd.createImpl());
            }
        }

        return _impl;
    }
}
