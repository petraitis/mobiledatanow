package wsl.mdn.licence;

import wsl.licence.Store;
import java.net.URL;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class MdnStore extends Store
{
    public static final String SERVER_CONFIG = "wsl/config/mdn/MdnRmiServer.conf";
    private String _path = null;

    public MdnStore() throws Exception
    {
        _path = getPath();
    }

    /**
     * Returns the filename and path for the store
     */
    protected  String getFileNamePath()
    {
        String sep = System.getProperty("file.separator", "/");
//        String path = System.getProperty("user.dir", ".");

        String filename = _path + sep + "sec.dat";

        return filename;
    }

    public String getPath()  throws Exception
    {
        if (_path != null)
            return  _path;

        URL url = ClassLoader.getSystemResource(SERVER_CONFIG);
        if (url == null)
            throw new RuntimeException("Unable to locate MdnRmiServer.conf");

        String path = url.getPath();
        // URL's could contain %20 as spaces, these have to be removed
        path = path.replaceAll("%20", " ");
        _path = path.substring(0, path.lastIndexOf("/"));

        return _path;
    }

}