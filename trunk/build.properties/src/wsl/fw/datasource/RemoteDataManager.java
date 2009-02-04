//==============================================================================
// RemoteDataManager.java
// Copyright (c) 2000 WAP Solutions Ltd.
// $Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $
// $Archive: /Framework/Source/wsl/fw/datasource/RemoteDataManager.java $
//==============================================================================

package wsl.fw.datasource;

import wsl.fw.remote.RmiServant;
import java.rmi.RemoteException;

//------------------------------------------------------------------------------
/**
 * Interface for remote DataManager, used in conjunction with RemoteDataSource.
 */
public interface RemoteDataManager extends RmiServant
{
    //--------------------------------------------------------------------------
    /**
     * Create a remote data source.
     * @param dsName the name of the data source to create
     */
    public RemoteDataSource createDataSource(String dsName) throws RemoteException;

    //--------------------------------------------------------------------------
    /**
     * Create a remote data source.
     * @param param, the param defining the data source to create
     */
    public RemoteDataSource createDataSource(DataSourceParam param) throws RemoteException;


    //--------------------------------------------------------------------------
    // DataObject Versioning

    /**
     * Returns a version for an object
     * @param entityName the name of the entity
     * @param key the unique key of the object
     * @return Integer the version of the object
     */
    public Integer getObjectVersion(String entityName, Object key)
        throws RemoteException;
}

//==============================================================================
// end of file RemoteDataManager.java
//==============================================================================
