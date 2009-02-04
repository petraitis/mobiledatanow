/**	$Id: ConnectionPool.java,v 1.3 2002/07/16 02:29:21 jonc Exp $
 *
 * A pool of connections (all to the same DB with the same connect parameters)
 * to a JDBC database.
 * Usage:
 * Construct a pool wit all the requisite conection and timeout params and
 * save for later use (i.e. as ConnectionPoolManager does).
 * Call getConnection ().
 * Use the returned connection.
 * When finished call close () on the connection to return it to the pool.
 *
 */
package wsl.fw.datasource;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Iterator;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

import com.framedobjects.dashwell.db.DataAgentFactory;
import com.framedobjects.dashwell.db.IDataAgent;

import wsl.fw.exception.MdnException;
import wsl.fw.util.Config;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.mdn.common.MdnAdminConst;
import wsl.mdn.dataview.JdbcDriver;
import wsl.mdn.server.LicenseManager;

public class ConnectionPool
	implements Runnable
{
	private final static long MS_SLEEP_TIME        = 50;
	private final static long MS_PRELOAD_PERIOD    = 1000;

	// attributes
	private int        _poolSize;
	private int        _msLifetime;
	private int        _msGetTimeout;
	private String     _driver;
	private String     _url;
	private String     _catalog;
	private String     _user;
	private String     _password;
	private LinkedList _availablePool       = new LinkedList ();
	private HashSet    _inUsePool           = new HashSet ();
	private boolean    _terminated          = false;

	private int        _connectionCount     = 0;
	private Object     _connectionCountLock = new Object ();

	private Thread     _preloadThread       = null;

	//--------------------------------------------------------------------------
	/**
	 * Constructor.
	 * @param poolSize, the max size of the pool, infinite if =< 0
	 * @param msLifetime, time in MS that a connection will be kept alive and
	 *   reused. After this time it is closed. Infinite if < 0.
	 * @param msGetTimeout, time in MS that getConnection will wait for a
	 *   connection to become available before throwing a PoolTimeoutException.
	 *    Infinite if < 0.
	 * @param driver, JDBC connection information.
	 * @param url, JDBC connection information.
	 * @param catalog, JDBC connection information.
	 * @param user, JDBC connection information.
	 * @param password, JDBC connection information.
	 */
	public
	ConnectionPool (
	 int poolSize,
	 int msLifetime,
	 int msGetTimeout,
	 String driver,
	 String url,
	 String catalog,
	 String user,
	 String password)
	{
		// check args
		Util.argCheckEmpty (driver);
		Util.argCheckEmpty (url);

		// store the connection management params
		_poolSize     = poolSize;
		_msLifetime   = msLifetime;
		_msGetTimeout = msGetTimeout;

		// store the params needed to make the connections
		_driver       = driver;
		_url          = url;
		_catalog      = catalog;
		_user         = user;
		_password     = password;

		// create a thread to preload with pooled connections
		_preloadThread  = new Thread (this);
		_preloadThread.setDaemon (true);

		// fixme, test code, do not use yet as a pool with invalid connection
		// info will continually try to reconnect.
		// may want to change pooling to disallow creation of a pool
		// with invalid connect info.

		// fixme, test, do not actually start the preload thread
		// _preloadThread.start ();

		// log creation of pool
		Log.debug ("ConnectionPool ctor: driver=" + _driver + ", url=" + _url
			+ ", user=" + _user);
		System.out.println ("ConnectionPool ctor: driver=" + _driver + ", url=" + _url
				+ ", user=" + _user);
	}

	//--------------------------------------------------------------------------
	/**
	 * Terminate all connections and deny any attempts to make new connections.
	 * Used when shutting down the system.
	 */
	public void
	terminate ()
	{
		// set terminated flag
		_terminated = true;

		// interrupt preload thread
		_preloadThread.interrupt ();

		// iterate all available connections and close them
		synchronized (_availablePool)
		{
			while (_availablePool.size () > 0)
			{
				// remove from list
				PooledConnection con = (PooledConnection) _availablePool.removeFirst ();

				// dec count, this isn't really important as we are terminating
				// so don't bother synchronizing
				_connectionCount--;

				// close the connection
				con.closeDelegate ();
			}
		}

		// iterate all in-use connections and close them
		synchronized (_inUsePool)
		{
			// get iterator
			Iterator iter = _inUsePool.iterator ();

			while (iter.hasNext ())
			{
				// remove from list
				PooledConnection con = (PooledConnection) iter.next ();

				// dec count, this isn't really important as we are terminating
				// so don't bother synchronizing
				_connectionCount--;

				// close the connection
				con.closeDelegate ();
			}

			// remove all entries
			_inUsePool.clear ();
		}
	}

	/**
	 * Create a new pooledConnection referencing the real JDBC connection
	 * delegate that uses the JDBC params.
	 * @return the newly created PooledConnection.
	 * @throws ClassNotFoundException 
	 */
	private PooledConnection
	createPooledConnection ()
		throws SQLException, ClassNotFoundException
	{
		// if terminating then ret null, this will later cause a
		// PoolTimeoutException to be thrown
		if (_terminated)
			return null;

		try
		{
			try
			{
				// load driver
				Class.forName (_driver);
			}
			catch (ClassNotFoundException e)
			{
				try {
					JdbcDriver jdbcDriver = getJdbcDriverByDriver(_driver);
					if (jdbcDriver != null){
						String fileName = jdbcDriver.getFileName();
						String path = Config.getProp(MdnAdminConst.DATABASE_DRIVER_UPLOAD_PATH);
						String filePath = path + fileName;
						System.out.println("dynamically set the classpath: " + filePath);
						ClassPathHacker.addFile(filePath);
						Class.forName(_driver);
					}
				} catch (MdnException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					Log.error ("ConnectionPool.createPooledConnection: " + e1.toString ());
					throw e;
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.error ("ConnectionPool.createPooledConnection: " + e2.toString ());
					throw e;
				}
				/*e.printStackTrace();
				Log.error ("ConnectionPool.createPooledConnection: " + e.toString ());
				throw e;*/
			}

			// get connection
			Connection con = DriverManager.getConnection (_url, _user, _password);

			Log.debug ("ConnectionPool.createPooledConnection: driver=" + _driver
				+ ", url=" + _url + ", user=" + _user);
			/*System.out.println ("ConnectionPool.createPooledConnection: driver=" + _driver
					+ ", url=" + _url + ", user=" + _user);
			*/
			// create the wrapping PooledConnection
			PooledConnection pc = new PooledConnection (con, this);

			return pc;

		} catch (SQLException e)
		{
			// if we are failing due to an SQLException then be sure to
			// decrement the count which has already be incremented in
			// anticipation of this function succeeding

			synchronized (_connectionCountLock)
			{
				_connectionCount--;
			}

			Log.warning (e.getMessage ());
			e.printStackTrace();
			throw e;
		}
	}
	public JdbcDriver getJdbcDriverByDriver(String driver) throws MdnException{
		//Find JdbcDriver
		DataSource ds = DataManager.getSystemDS();
		JdbcDriver jdbcDriver = null;
		Query q = new Query(JdbcDriver.ENT_JDBCDRIVER);
		QueryCriterium qc = new QueryCriterium(JdbcDriver.ENT_JDBCDRIVER, JdbcDriver.FLD_DRIVER, QueryCriterium.OP_EQUALS, driver);
		qc.setOrIsNull(true);
		q.addQueryCriterium(qc);
		try {
			RecordSet rs = ds.select(q);
			if (rs.next()){
				jdbcDriver = (JdbcDriver)rs.getCurrentObject();
			}
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new MdnException("DataSourceException when getJDBCDriverByName()", e);
		}		
		return jdbcDriver;
	}
	/**
	 * Get a connection from the pool.
	 * @return the PooledConnection, which MUST be close ()ed to return it to the
	 *   pool when no longer needed.
	 * @throws SQLException if the JDBC connection fails.
	 * @throws PoolTimeoutException if a connection cannot be obtained within
	 *   the specified time.
	 * @throws ClassNotFoundException 
	 */
	public PooledConnection
	getConnection ()
		throws SQLException, PoolTimeoutException, ClassNotFoundException
	{
		PooledConnection con     = null;
		boolean          bCreate = false;
		long             msStart = System.currentTimeMillis ();

		synchronized (_availablePool)
		{
			// try to get a connection from the pool, keep trying until
			// successful or timeout
			while (true)
			{
				// is there a connection in the available pool
				if (_availablePool.size () >= 1)
				{
					// free pool entry, get it and break
					con = (PooledConnection) _availablePool.removeFirst ();

					break;
				}

				// none in available pool, can we create a new one?
				synchronized (_connectionCountLock)
				{
					if (_poolSize <= 0 || _connectionCount < _poolSize)
					{
						// inc count and flag to create new connection
						_connectionCount++;
						bCreate = true;
						break;
					}
				}

				// none free and pool is full, must wait
				// check if out timeout is expired
				if (_msGetTimeout >= 0
					&& (System.currentTimeMillis () > (msStart + _msGetTimeout)))
					break;

				// none free and no timed out yet, do a non-blocking wait on the
				// monitor before startring the next iteration
				try
				{
					_availablePool.wait (MS_SLEEP_TIME);
				}
				catch (InterruptedException e)
				{
				}
			}
		}

		// if we have been instructed to create a new connection do so
		if (bCreate && con == null)
			con = createPooledConnection ();

		// add the connection to the in-use pool
		if (con != null)
			synchronized (_inUsePool)
			{
				_inUsePool.add (con);
			}

		// check if we have a connection, if not it is because of a timeout
		if (con == null)
			throw new PoolTimeoutException ();

		return con;
	}

	/**
	 * Return a PooledConnection to the pool. Expired connections will be
	 * destroyed rather than returned.
	 * @param oldConnection, the old (now disabled) connection originally
	 *   obtained with getConnection (), this will be removed from the in-use
	 *   pool.
	 * @param newConnection, a copy of the oldConnnection that will be destroyed
	 *   or returned to the availabel pool.
	 * @param shouldDestroy, if true the pool should destroy the connection
	 *   rather than return it to the pool.
	 */
	void
	freeConnection (
	 PooledConnection oldConnection,
	 PooledConnection newConnection,
	 boolean shouldDestroy)
	{
		synchronized (_inUsePool)
		{
			// remove the old connection from the in-use pool
			_inUsePool.remove (oldConnection);
		}

		// determine if we should destroy the returned connection
		if (shouldDestroy || isExpired (newConnection))
		{
			// expired
			newConnection.closeDelegate ();

			synchronized (_connectionCountLock)
			{
				_connectionCount--;
			}
		}
		else
			synchronized (_availablePool)
			{
				// return it to the available pool
				_availablePool.addLast (newConnection);

				// notify anyone waiting that there is a new free connection
				_availablePool.notifyAll ();
			}
	}

	/**
	 * @return true if the connection's lifetime has expired.
	 */
	private boolean
	isExpired (
	 PooledConnection pc)
	{
		if (_msLifetime < 0)
			return false;
		else
			return System.currentTimeMillis () > (pc.getCreateTime () + _msLifetime);
	}

	//--------------------------------------------------------------------------
	/**
	 * Run method used by preload thread.
	 */
	public void
	run ()
	{
		try
		{
			// iterate until interrupted or terminated, calling preload to make
			// new connections
			while (!_terminated)
			{
				preload ();
				Thread.sleep (MS_PRELOAD_PERIOD);
			}
		}
		catch (InterruptedException e)
		{
		}
	}

	//--------------------------------------------------------------------------
	/**
	 * Where possible preload connections to avoid delays.
	 */
	private void
	preload ()
	{
		boolean bCreate = false;

		// determine if we should create a connection
		if (_availablePool.size () < getDesiredFree ()
			&& (_poolSize <= 0 || _connectionCount < _poolSize))
		{
			// want to create a new connection
			synchronized (_connectionCountLock)
			{
				// inc count and flag to create new connection
				_connectionCount++;
				bCreate = true;
			}
		}

		// if flagged to create
		if (bCreate)
		{
			// create connection
			try
			{
				Connection con = createPooledConnection ();

				if (con != null)
				{
					synchronized (_availablePool)
					{
						// add to available pool
						_availablePool.addLast (con);

						// notify anyone waiting that there is a new free connection
						_availablePool.notifyAll ();
					}
				}
			}
			catch (SQLException e)
			{
				// don't care, createPooledConnection has already logged and
				// decremented the count
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	//--------------------------------------------------------------------------
	/**
	 * @return the max desired number of available threads, the pool will
	 *   attempt to preload up to this number of connections depending on
	 *   the pool size and in-use connections.
	 */
	private int
	getDesiredFree ()
	{
		// fixme, this should be an attribute of the pool
		final int FREE_FOR_UNLIMITED = 5;
		int       free;

		// if pool is unlimited use fixed number
		if (_poolSize <= 0)
			free = FREE_FOR_UNLIMITED;
		else
		{
			// else want half the empty space connected
			free = (_poolSize - _connectionCount) / 2;

			// ensure num is between 1 and FREE_FOR_UNLIMITED
			if (free > FREE_FOR_UNLIMITED)
				free = FREE_FOR_UNLIMITED;
			if (free < 1)
				free = 1;
		}

		return free;
	}
}
