//==============================================================================
// TextAction.java
// Copyright (c) 2001 WAP Solutions Ltd.
//==============================================================================

package wsl.mdn.guiconfig;

import wsl.fw.util.Util;

//------------------------------------------------------------------------------
/**
 * MenuAction that displays text that is optionally a link to a url or a phone
 * number.
 */
public class TextAction extends MenuAction
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:35:35 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/guiconfig/TextAction.java $ ";

    protected final static char C_URL   = 'U';
    protected final static char C_PHONE = 'P';

    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public TextAction()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Partial constructor (links must be set manually).
     * @param name, name of the action.
     * @param description, description of the action.
     * @param groupId, id of the group that owns this menu
     * @param parentMenuId, id of the parent MenuAction that contains this.
     */
    public TextAction(String name, String description, Object groupId,
        Object parentMenuId)
    {
        super(name, description, groupId, parentMenuId);
    }

    //--------------------------------------------------------------------------
    /**
     * Get the phoneNumber link.
     * Note, may have a url link OR a phone link, not both.
     * @return the phone number, will be empty if there is no phone link.
     */
    public String getPhoneLink()
    {
        return getLink(true);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the phone number.
     * Note, may have a url link OR a phone link, not both.
     * @param phoneNumber, the phone number, may be null or empty.
     */
    public void setPhoneLink(String phoneNumber)
    {
        setLink(true, phoneNumber);
    }

    //--------------------------------------------------------------------------
    /**
     * @return true if this TextAction has a non-empty phone number link.
     */
    public boolean hasPhoneLink()
    {
        return !Util.isEmpty(getPhoneLink());
    }

    //--------------------------------------------------------------------------
    /**
     * Get the url link.
     * Note, may have a url link OR a phone link, not both.
     * @return the url, will be empty if there is no url.
     */
    public String getUrlLink()
    {
        return getLink(false);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the url link.
     * Note, may have a url link OR a phone link, not both.
     * @param url, the url link, may be null or empty.
     */
    public void setUrlLink(String url)
    {
        setLink(false, url);
    }

    //--------------------------------------------------------------------------
    /**
     * @return true if this TextAction has a non-empty phone number link.
     */
    public boolean hasUrlLink()
    {
        return !Util.isEmpty(getUrlLink());
    }

    //--------------------------------------------------------------------------
    /**
     * @param bPhone, if true gets a phone link, else a url link.
     * @return the link value for a url or phone link, or empty if not present.
     */
    protected String getLink(boolean bPhone)
    {
        // get the encoded link value
        String linkValue = getStringValue(FLD_LINK);
        String link = "";
        char   typeCode;

        // must have at least 2 chars, one for code and one or more for the
        // link itself
        if (linkValue != null && linkValue.length() > 1)
        {
            typeCode = linkValue.charAt(0);
            if ((bPhone && typeCode == C_PHONE)
                || (!bPhone && typeCode == C_URL))
                link = linkValue.substring(1);
        }

        return link;
    }
    //--------------------------------------------------------------------------
    /**
     * Set the link.
     * @param bPhone, if true sets a phone link, else a url link.
     * @param link, the link value to set, if empty or null clears the link.
     */
    protected void setLink(boolean bPhone, String link)
    {
        if (Util.isEmpty(link))
            setValue(FLD_LINK, null);
        else
            setValue(FLD_LINK, (bPhone ? C_PHONE : C_URL) + link);
    }
}

//==============================================================================
// end of file TextAction.java
//==============================================================================
