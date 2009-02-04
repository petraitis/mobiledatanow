/*	$Header: /wapsolutions/cvsroot/mdn/fw/src/wsl/fw/msgserver/MessageServerParam.java,v 1.1.1.1 2002/06/11 23:11:42 jonc Exp $
 *
 */
package wsl.fw.msgserver;

import wsl.fw.datasource.DataSourceParam;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c)
 * Company:
 * @author
 * @version 1.0
 */

public class MessageServerParam extends DataSourceParam
{
    //--------------------------------------------------------------------------
    // attributes

    private int _msId = -1;
    private String _msType;
    private String _propTags = "";
	private String _host = "";
	private String _className;			// MessageServer class to load

    //--------------------------------------------------------------------------
    // construction

    //--------------------------------------------------------------------------
    /**
     * Param ctor
     * @param ms the message server
     */
    public
	MessageServerParam (
	 MessageServer ms)
    {
        super (ms.getServerName ());
        _msId		= ms.getId();
        _msType		= ms.getType();
		_host		= ms.getHost();
		_className	= ms.getClass ().getName ();
        _propTags	= ms.getPropTags();
    }


    //--------------------------------------------------------------------------
    // accessors

    //--------------------------------------------------------------------------
    /**
     * @return the server name
     */
    public String getServerName()
    {
        return _name;
    }

    //--------------------------------------------------------------------------
    /**
     * @return the server id
     */
    public int getMsId()
    {
        return _msId;
    }

    //--------------------------------------------------------------------------
    /**
     * @return server type
     */
    public String getMsType()
    {
        return _msType;
    }

    //--------------------------------------------------------------------------
    /**
     * @return prop tags
     */
    public String getPropTags()
    {
        return _propTags;
    }

    //--------------------------------------------------------------------------
    /**
     * @return host
     */
    public String getHost()
    {
        return _host;
    }

	/**
	 * @return MessageServer load class
	 */
	public String
	getClassName ()
	{
		return _className;
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
        fullName.append(String.valueOf(_msId));
        return fullName.toString();
    }
}