//==============================================================================
// JdbcDataSourceParam.java
// Copyright (c) 2001 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.datasource;

import wsl.fw.util.Util;

//--------------------------------------------------------------------------
/**
 * Class to hold params used to identify or connect to a datasource.
 */
public class JdbcDataSourceParam extends DataSourceParam
{
    // JdbcDataSource params
    protected String  _driver = "";
    protected String  _url = "";
    //protected String  _catalog = "";
    protected String  _user = "";
    protected String  _pw = "";

    // pool info, these values are not used for the name or hashcode
    protected boolean _hasPoolInfo;
    protected int     _poolSize;
    protected int     _msLifetime;
    protected int     _msGetTimeout;

    //--------------------------------------------------------------------------
    /**
     * Create a JdbcDataSourceParam defining a JdbcDataSource. No pool info is
     * defined so it will use the ConnectionPoolManager defaults.
     */
    public JdbcDataSourceParam(String name, String driver, String url,
        String user, String pw)//String catalog, 
    {
        super(name);

        _driver      = driver;
        _url         = url;
        //_catalog     = catalog;
        _user        = user;
        _pw          = pw;
        _hasPoolInfo = false;
    }

    //--------------------------------------------------------------------------
    /**
     * Create a JdbcDataSourceParam defining a JdbcDataSource (with pool info).
     */
    public JdbcDataSourceParam(int poolSize, int msLifetime, int msGetTimeout,
        String name, String driver, String url, String user,
        String pw)//String catalog, 
    {
        super(name);

        _driver       = driver;
        _url          = url;
        //_catalog      = catalog;
        _user         = user;
        _pw           = pw;

        _hasPoolInfo  = true;
        _poolSize     = poolSize;
        _msLifetime   = msLifetime;
        _msGetTimeout = msGetTimeout;
    }

    //--------------------------------------------------------------------------
    /**
     * @return the fully qualified name identifying the datasource
     */
    public String getFullName()
    {
        final char SEP = ':';
        StringBuffer fullName = new StringBuffer(getClass().getName());
        fullName.append(SEP);
        fullName.append(_name);
        fullName.append(SEP);

        fullName.append(_driver);
        fullName.append(SEP);
        fullName.append(_url);
        fullName.append(SEP);
        //fullName.append(_catalog);
        //fullName.append(SEP);
        fullName.append(_user);
        fullName.append(SEP);
        fullName.append(_pw);

        return fullName.toString();
    }
}

//==============================================================================
// end of file JdbcDataSourceParam.java
//==============================================================================
