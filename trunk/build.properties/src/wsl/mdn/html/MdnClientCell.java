package wsl.mdn.html;

import org.apache.ecs.html.TD;
import org.apache.ecs.Element;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:
 * @author
 * @version 1.0
 */

public class MdnClientCell extends TD
{
    //--------------------------------------------------------------------------
    // attributes

    private String _title = "";
    private String _helpUrl = "/help/mdnhelp.html";


    //--------------------------------------------------------------------------
    // construction

    /**
     * Blank ctor
     */
    public MdnClientCell()
    {
    }

    /**
     * String ctor
     */
    public MdnClientCell(String str)
    {
        super(str);
    }

    /**
     * boolean ctor
     */
    public MdnClientCell(boolean b)
    {
        super(b);
    }

    /**
     * Element ctor
     */
    public MdnClientCell(Element e)
    {
        super(e);
    }

    /**
     * Element, title ctor
     */
    public MdnClientCell(Element e, String title)
    {
        super(e);
        setClientTitle(title);
    }


    //--------------------------------------------------------------------------
    // accessors

    /**
     * Set title
     * @param title
     */
    public void setClientTitle(String title)
    {
        _title = title;
    }

    /**
     * @return String title
     */
    public String getClientTitle()
    {
        return _title;
    }

    /**
     * Set htlp url
     * @param url
     */
    public void setHelpUrl(String url)
    {
        _helpUrl = url;
    }

    /**
     * @return String the help url
     */
    public String getHelpUrl()
    {
        return _helpUrl;
    }
}