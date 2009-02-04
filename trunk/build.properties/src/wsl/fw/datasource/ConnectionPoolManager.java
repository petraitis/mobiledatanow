//==============================================================================
// ConnectionPoolManager.java
// Copyright (c) 2001 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.datasource;

import java.util.HashMap;
import java.util.Iterator;
import wsl.fw.util.Log;

//------------------------------------------------------------------------------
/**
 * Manager for ConnectionPools.
 * Usage:
 * Create a ConnectionPoolManager. The manager should be stored in a singleton.
 * Get the ConnectionPoolManager and call getPool().
 * Call getConnection() on the returned pool.
 * Whne finished with the conection call close();
 */
public class ConnectionPoolManager
{
    // attributes
    private int            _poolSize;
    private int            _msLifetime;
    private int            _msGetTimeout;
    private ConnectionPool _cpTerminated    = null;
    private HashMap        _connectionPools = new HashMap();

    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     * Sets the default pool values, which are used when creating new pools
     * that have not specified these values.
     */
    public ConnectionPoolManager()
    {
        this(/* no pool size limit */ -1, /* no expiry */ -1,
            /* immediate timeout */ 0);
    }

    //--------------------------------------------------------------------------
    /**
     * Full constructor.
     * Sets the default pool values, whic are used when creating new pools
     * that have not specified these values.
     * @param poolSize, the max size of the pool, infinite if =< 0.
     * @param msLifetime, time in MS that a connection will be kept alive and
     *   reused. After this time it is closed. Infinite if < 0.
     * @param msGetTimeout, time in MS that getConnection will wait for a
     *   connection to become available before throwing a PoolTimeoutException.
     *   Infinite if < 0.
     */
    public ConnectionPoolManager(int poolSize, int msLifetime, int msGetTimeout)
    {
        _poolSize     = poolSize;
        _msLifetime   = msLifetime;
        _msGetTimeout = msGetTimeout;

        Log.debug("ConnectionPoolManager ctor: size=" + _poolSize + ", life="
            + _msLifetime + ", timeout=" + _msGetTimeout);
    }

    //--------------------------------------------------------------------------
    /**
     * Gets a Connection pool that matches the driver-password param. The pools
     * are shared and a new one will only be created if a match cannot be found.
     *
     * @param poolSize, the max size of the pool, infinite if =< 0.
     *   Only used if the pool is being created.
     * @param msLifetime, time in MS that a connection will be kept alive and
     *   reused. After this time it is closed. Infinite if < 0.
     *   Only used if the pool is being created.
     * @param msGetTimeout, time in MS that getConnection will wait for a
     *   connection to become available before throwing a PoolTimeoutException.
     *   Infinite if < 0.
     *   Only used if the pool is being created.
     * @param driver, JDBC connection information.
     * @param url, JDBC connection information.
     * @param catalog, JDBC connection information.
     * @param user, JDBC connection information.
     * @param password, JDBC connection information.
     * @return a Connection pool whose driver-password params match those
     *   specified.
     */
    public ConnectionPool getPool(int poolSize, int msLifetime,
        int msGetTimeout, String driver, String url, String catalog,
        String user, String password)
    {
        // if terminated return the inoperable terminated pool
        if (_cpTerminated != null)
            return _cpTerminated;

        ConnectionPool pool = null;

        // not terminated
        synchronized (_connectionPools)
        {
            // try to get an existing pool
            PoolId pid = new PoolId(driver, url, catalog, user, password);
            pool = (ConnectionPool) _connectionPools.get(pid);

            // if not in pool then create a new one
            if (pool == null)
            {
                pool = new ConnectionPool(poolSize, msLifetime, msGetTimeout,
                    driver, url, catalog, user, password);
                // add it to the map of pools
                _connectionPools.put(pid, pool);
            }
        }

        return pool;
    }

    //--------------------------------------------------------------------------
    /**
     * Simpler version of getPool. The values for poolSize, msLifetime and
     *   msGetTimeout are defaulted to those supplied to the
     *   ConnectionPoolManager constructor.
     * @see iGetPool.
     */
    public ConnectionPool getPool(String driver, String url,
        String catalog, String user, String password)
    {
    	/*System.out.println ("ConnectionPoolManager getPool: driver=" + driver + ", url=" + url
				+ ", user=" + user);*/	
    	return getPool(_poolSize, _msLifetime, _msGetTimeout,
            driver, url, catalog, user, password);
    }

    //--------------------------------------------------------------------------
    /**
     * Terminate all connections of all pools and do not permit any new
     * connections. Used when shutting down the system.
     */
    public void terminate()
    {
        Log.debug("ConnectionPoolManager.terminate()");

        // set terminated to stop any new pools being created
        ConnectionPool term = new ConnectionPool(1, -1, -1, "x", "x", "", "", "");
        term.terminate();
        _cpTerminated = term;

        // terminate all existing pools
        synchronized (_connectionPools)
        {
            Iterator keyIter = _connectionPools.keySet().iterator();

            while (keyIter.hasNext())
            {
                PoolId pid = (PoolId) keyIter.next();
                ConnectionPool pool = (ConnectionPool) _connectionPools.get(pid);

                if (pool != null)
                    pool.terminate();
            }

            // empty pool as we have terminated them all
            _connectionPools.clear();
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Inner class to hold the unique info that keys a ConnectionPool.
     */
    public static class PoolId
    {
        // attributes
        private String _driver;
        private String _url;
        private String _catalog;
        private String _user;
        private String _password;

        public PoolId(String driver, String url,
            String catalog, String user, String password)
        {
            _driver   = driver;
            _url      = url;
            _catalog  = catalog;
            _user     = user;
            _password = password;
        }

        public boolean equals(Object obj)
        {
            if (obj == null || !(obj instanceof PoolId))
                return false;

            return getCombinedText().equals(((PoolId) obj).getCombinedText());
        }

        public int hashCode()
        {
            return getCombinedText().hashCode();
        }

        private String getCombinedText()
        {
            final char SEP = ':';
            StringBuffer buf = new StringBuffer();
            buf.append(_driver);
            buf.append(SEP);
            buf.append(_url);
            buf.append(SEP);
            buf.append(_catalog);
            buf.append(SEP);
            buf.append(_user);
            buf.append(SEP);
            buf.append(_password);

            return buf.toString();
        }
    }
}

//==============================================================================
// end of file ConnectionPoolManager.java
//==============================================================================
