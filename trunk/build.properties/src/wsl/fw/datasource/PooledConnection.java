//==============================================================================
// PooledConnection.java
// Copyright (c) 2001 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.datasource;

import wsl.fw.util.Util;
import wsl.fw.util.Log;
import java.util.Map;
import java.util.Properties;
import java.sql.*;

//------------------------------------------------------------------------------
/**
 * A wrapper class for a Connection that is intended to be used and then
 * returned to the ConnectionPool.
 * Usage:
 * Call ConnectionPool.getConnection() to obtain a Connection.
 * When finished call close() on the connection to return it to the pool.
 */
public class PooledConnection implements Connection
{
    private Connection     _delegate;
    private ConnectionPool _pool;
    private long           _msCreateTime;

    //--------------------------------------------------------------------------
    /**
     * Constructor.
     * @param delegate, the actual connection that we delegate to do the work.
     * @param pool, the pool that owns this connection, to which the connection
     *   is returned when close() is called.
     */
    public PooledConnection(Connection delegate, ConnectionPool pool)
    {
        Util.argCheckNull(delegate);
        Util.argCheckNull(pool);

        _delegate      = delegate;
        _pool          = pool;

        // store the creation time for later use by the pool.
        _msCreateTime = System.currentTimeMillis();
    }

    //--------------------------------------------------------------------------
    /**
     * Shallow copy constructor. Initializes the PooledConnection to use the
     * same data and connection as the source PooledConnection
     */
    private PooledConnection(PooledConnection src)
    {
    // shallow copy of attributes
    _delegate      = src._delegate;
    _pool          = src._pool;
    _msCreateTime  = src._msCreateTime;
    }

    //--------------------------------------------------------------------------
    /**
     * @return the time this PooledConnection was created.
     */
    long getCreateTime()
    {
        return _msCreateTime;
    }

    //--------------------------------------------------------------------------
    /**
     * Close the JDBC connection delegate. For use by the ConnectionPool.
     */
    void closeDelegate()
    {
        if (_delegate != null)
        {
            try
            {
                synchronized (_delegate)
                {
                    if (!_delegate.isClosed())
                        _delegate.close();
                }
                Log.debug("PooledConnection.closeDelegate");
            }
            catch (SQLException e)
            {
                Log.warning("PooledConnection.closeDelegate: " + e.toString());
            }

            _delegate = null;
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Overload of close to return the connection to the pool.
     */
    public void close()
    {
        // delegate, return rather than destroy
        close(false);
    }

    //--------------------------------------------------------------------------
    /**
     * Overload of close to return the connection to the pool.
     * @param shouldDestroy, if true informs the ConnectionPool that this
     *   connection is bad and should be destroyed.
     */
    public void close(boolean shouldDestroy)
    {
        // make a copy of the PooledConnection then clear our pointers so we
        // fail when trying to use after close
        PooledConnection newCopy = new PooledConnection(this);
        _delegate = null;
        _pool = null;

        // return the connection to the pool
        newCopy._pool.freeConnection(this, newCopy, shouldDestroy);
    }

    //--------------------------------------------------------------------------
    /**
     * Delegating functions, all of these are just wrappers.
     */
    public Statement createStatement() throws SQLException
    {
        return _delegate.createStatement();
    }

    public PreparedStatement prepareStatement(String sql)
        throws SQLException
    {
        return _delegate.prepareStatement(sql);
    }

    public CallableStatement prepareCall(String sql) throws SQLException
    {
        return _delegate.prepareCall(sql);
    }

    public String nativeSQL(String sql) throws SQLException
    {
        return _delegate.nativeSQL(sql);
    }


    public void setAutoCommit(boolean autoCommit) throws SQLException
    {
        _delegate.setAutoCommit(autoCommit);
    }


    public boolean getAutoCommit() throws SQLException
    {
        return _delegate.getAutoCommit();
    }


    public void commit() throws SQLException
    {
        _delegate.commit();
    }


    public void rollback() throws SQLException
    {
        _delegate.rollback();
    }


    public boolean isClosed() throws SQLException
    {
        return _delegate.isClosed();
    }


    public DatabaseMetaData getMetaData() throws SQLException
    {
        return _delegate.getMetaData();
    }


    public void setReadOnly(boolean readOnly) throws SQLException
    {
        _delegate.setReadOnly(readOnly);
    }


    public boolean isReadOnly() throws SQLException
    {
        return _delegate.isReadOnly();
    }


    public void setCatalog(String catalog) throws SQLException
    {
        _delegate.setCatalog(catalog);
    }


    public String getCatalog() throws SQLException
    {
        return _delegate.getCatalog();
    }


    public void setTransactionIsolation(int level) throws SQLException
    {
        _delegate.setTransactionIsolation(level);
    }


    public int getTransactionIsolation() throws SQLException
    {
        return _delegate.getTransactionIsolation();
    }


    public SQLWarning getWarnings() throws SQLException
    {
        return _delegate.getWarnings();
    }


    public void clearWarnings() throws SQLException
    {
        _delegate.clearWarnings();
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency)
        throws SQLException
    {
        return _delegate.createStatement(resultSetType, resultSetConcurrency);
    }


    public PreparedStatement prepareStatement(String sql, int resultSetType,
        int resultSetConcurrency)
        throws SQLException
    {
        return _delegate.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }


    public CallableStatement prepareCall(String sql, int resultSetType,
        int resultSetConcurrency) throws SQLException
    {
        return _delegate.prepareCall(sql, resultSetType, resultSetConcurrency);
    }


    public Map getTypeMap() throws SQLException
    {
        return _delegate.getTypeMap();
    }


    public void setTypeMap(Map map) throws SQLException
    {
        _delegate.setTypeMap(map);
    }

		/**
		 Empty methods, added to port MDN to jdk1.4
		 */
    public Statement 
		createStatement (
		 int a, int b, int c) 
		 	throws SQLException
    {
			throw new SQLException ();
    }

    public CallableStatement 
		prepareCall (
		 String sql, 
		 int a, int b, int c) 
		 	throws SQLException
    {
			throw new SQLException ();
    }

    public PreparedStatement 
		prepareStatement (
		 String sql, 
		 String[] columnNames) 
		 	throws SQLException
    {
			throw new SQLException ();
    }

    public PreparedStatement 
		prepareStatement (
		 String sql, 
		 int a) 
		 	throws SQLException
    {
			throw new SQLException ();
    }

    public PreparedStatement 
		prepareStatement (
		 String sql, 
		 int a, int b, int c) 
		 	throws SQLException
    {
			throw new SQLException ();
    }

    public PreparedStatement 
		prepareStatement (
		 String sql, 
		 int[] columnNames) 
		 	throws SQLException
    {
			throw new SQLException ();
    }

    public void 
		releaseSavepoint (
		 Savepoint s)
		 	throws SQLException
    {
    }

    public int 
		getHoldability ()
		 	throws SQLException
    {
			throw new SQLException ();
    }

    public void 
		setHoldability ()
		 	throws SQLException
    {
    }

    public void 
		setHoldability (
		 int i)
		 	throws SQLException
    {
    }

    public void 
		rollback (
		 Savepoint s) 
		 	throws SQLException
    {
    }

    public Savepoint 
		setSavepoint (
		 String s)
		 	throws SQLException
    {
			throw new SQLException ();
    }

    public Savepoint 
		setSavepoint ()
		 	throws SQLException
    {
			throw new SQLException ();
    }

	public Array createArrayOf(String arg0, Object[] arg1) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public Blob createBlob() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public Clob createClob() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public NClob createNClob() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public SQLXML createSQLXML() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public Struct createStruct(String arg0, Object[] arg1) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public Properties getClientInfo() throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public String getClientInfo(String arg0) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public boolean isValid(int arg0) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public void setClientInfo(Properties arg0) throws SQLClientInfoException {
		throw new RuntimeException("Not implemented");
	}

	public void setClientInfo(String arg0, String arg1) throws SQLClientInfoException {
		throw new RuntimeException("Not implemented");
	}

	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		throw new RuntimeException("Not implemented");
	}

	public <T> T unwrap(Class<T> arg0) throws SQLException {
		throw new RuntimeException("Not implemented");
	}
    
}

//==============================================================================
// end of file PooledConnection.java
//==============================================================================
