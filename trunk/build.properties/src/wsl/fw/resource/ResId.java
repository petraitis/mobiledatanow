//==============================================================================
// ResId.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.resource;

//------------------------------------------------------------------------------
/**
 * Objects of this class keep Resource Id.
 * There is method to get resource for default locale.
 */
public class ResId
{
    /**
     * Version tag.
     */
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/resource/ResId.java $";

    /**
     * Resource Id.
     */
    private String _id;

    /** cache of loaded string */
    private String _text = null;

    /**
     * Constractor.
     * @param id Resource Id to be kept.
     */
    public ResId(String id)
    {
        _id = id;
    }

    // methods
    /**
     * This method returns Resource Id.
     * @return Resource Id.
     */
    public String getId()
    {
        return _id;
    }

    /**
     * This method returns resource for default locale.
     * @return Resource for default locale.
     */
    public String getText()
    {

        // cache the loaded string so we only have to go to the ResourceManager
        // the first time
        if (_text == null)
            _text = ResourceManager.getText(this);

        return _text;
    }

    /**
     * Equality, test if the id strings are equal.
     * @return true if ResIds have the same id.
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof ResId)
            return _id.equals(((ResId)obj)._id);
        return false;
    }

    /**
     * Hashcode, use hashcode of the id string.
     * @return the hashcode of the id String.
     */
    public int hashCode()
    {
        return _id.hashCode();
    }
}