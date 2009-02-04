package wsl.installerCustom.dependencies;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class InstallerMdnStore extends InstallerStore
{
    //public static final String SERVER_CONFIG = "wsl/config/mdn/MdnRmiServer.conf";
    private String _path = null;

    public InstallerMdnStore() throws Exception
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

        String filename = getPath() + sep + "sec.dat";

        return filename;
    }

    public String getPath()
    {
        //if (_path != null)
        //    return  _path;

        //URL url = ClassLoader.getSystemResource(SERVER_CONFIG);
        //if (url == null)
        //    throw new RuntimeException("Unable to locate MdnRmiServer.conf");

        //String path = url.getPath();


        //_path = _path.substring(0, _path.lastIndexOf("/"));;

        return _path;
    }

	public void setPath(String _path) {
		this._path = _path;
	}

}