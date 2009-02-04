package wsl.mdn.dataview;

// import
import java.util.Hashtable;
import wsl.fw.datasource.*;
import wsl.mdn.common.MdnDataManager;


/**
 * Pool of DataSource objects
 * Singleton
 */
public class DataSourcePool
{
    //--------------------------------------------------------------------------
    // static attributes

    private static DataSourcePool _pool;


    //--------------------------------------------------------------------------
    // instance attribs

    private Hashtable _map = new Hashtable();


    //--------------------------------------------------------------------------
    // construction

    /**
     * Default ctor
     */
    public DataSourcePool()
    {
    }


    //--------------------------------------------------------------------------
    // static methods

    /**
     * @return DataSourcePool the singleton
     */
    public static DataSourcePool getPool()
    {
        // if null, create
        if(_pool == null)
            _pool = new DataSourcePool();

        // return
        return _pool;
    }

    /**
     * Get a DataSource from a DataSource id
     * @param dsid the id of the datasource
     * @return DataSource the datasource
     */
    public static DataSource getDataSource(int dsid) throws DataSourceException
    {
        // delegate to singleton
        return getPool().iGetDataSource(dsid, false);
    }

    /**
     * Get a DataSource from a DataSource id
     * @param dsid the id of the datasource
     * @return DataSource the datasource
     */
    public static DataSource getDataSource(int dsid, boolean doRefresh)
        throws DataSourceException
    {
        // delegate to singleton
        return getPool().iGetDataSource(dsid, doRefresh);
    }

    /**
     * Get a DataSource from a DataSource id
     * @param dsid the id of the datasource
     * @return DataSOurce the datasource
     */
    private DataSource iGetDataSource(int dsid, boolean doRefresh) throws DataSourceException
    {
        // validate
        if(dsid < 0)
            throw new RuntimeException("Bad DataSource ID");

        // get from map
        DataSource ds = (DataSource)_map.get(new Integer(dsid));

        // if not found, select
        if(ds == null || doRefresh)
        {
            // create query
            DataSource sysDs = DataManager.getSystemDS();
            Query q = new Query(new QueryCriterium(DataSourceDobj.ENT_DATASOURCE,
                DataSourceDobj.FLD_ID, QueryCriterium.OP_EQUALS, new Integer(dsid)));
            RecordSet rs = sysDs.select(q);

            // get the ds
            if(rs != null && rs.next())
            {
                DataSourceDobj dsDobj = (DataSourceDobj)rs.getCurrentObject();
                ds = dsDobj.createImpl();

                // if it is mirrored set the mirror ds
                if(ds != null && dsDobj.isMirrored())
                {
                    // get the mirror ds
                    int mirrorId = dsDobj.getMirrorId();
                    DataSource mirrorDs = DataSourcePool.getDataSource(mirrorId);
                    //DataSource mirrorDs = MdnDataManager.getMirrorDS();

                    // set new entities
                    for(int i = 0; ds.getEntities() != null && i < ds.getEntities().size(); i++)
                        mirrorDs.addEntity((Entity)ds.getEntities().elementAt(i));

                    // set joins
                    for(int i = 0; ds.getJoins() != null && i < ds.getJoins().size(); i++)
                        mirrorDs.addJoin((Join)ds.getJoins().elementAt(i));

                    // set the mirror as the ds
                    ds = mirrorDs;
                }

                // add to the map
                //_map.put(new Integer(dsid), ds);
            }
        }

        // return
        return ds;
    }
}