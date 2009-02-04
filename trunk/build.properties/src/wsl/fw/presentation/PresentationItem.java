//==============================================================================
// PresentationItem.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.presentation;

import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.Entity;
import wsl.fw.datasource.Field;
import wsl.fw.datasource.EntityImpl;
import wsl.fw.datasource.FieldImpl;
import wsl.fw.datasource.DefaultKeyGeneratorData;

//--------------------------------------------------------------------------
/**
 * DataObject to hold custom presentation information. Used by
 * CustomPresentation and subclasses to define custom headers and footers for
 * a names set of presentation pages.
 */
public class PresentationItem  extends DataObject
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/presentation/PresentationItem.java $ ";

    // position constants
    public final static String HEADER = "Header";
    public final static String FOOTER = "Footer";

    // content type constants
    public final static String CT_TEXT = "Text";
    public final static String CT_URL  = "URL";

    // the entity name
    public final static String ENT_PRESENTATION_ITEM  = "FW_PRES_ITEM";

    // field names
    public final static String FLD_ID                 = "FLD_ID";
    public final static String FLD_PRESENTATION_CLASS = "FLD_PRES_CLASS";
    public final static String FLD_POSITION           = "FLD_POSITION";
    public final static String FLD_PAGE               = "FLD_PAGE";
    public final static String FLD_SEQUENCE           = "FLD_SEQUENCE";
    public final static String FLD_DESCRIPTION        = "FLD_DESCRIPTION";
    public final static String FLD_CONTENT_TYPE       = "FLD_CONTENT_TYPE";
    public final static String FLD_CONTENT            = "FLD_CONTENT";

    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public PresentationItem()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Static factory method to create the entity to be used by this dataobject
     * and any subclasses. This is called by the DataManager's factory when
     * creating a presentation item entity.
     * @return the created entity.
     */
    public static Entity createEntity()
    {
        // create the user entity
        Entity piEntity = new EntityImpl(ENT_PRESENTATION_ITEM, PresentationItem.class);

        // add the key generator for the system id
        piEntity.addKeyGeneratorData(new DefaultKeyGeneratorData(ENT_PRESENTATION_ITEM, FLD_ID));

        // create the fields and add them to the entity
        piEntity.addField(new FieldImpl(FLD_ID, Field.FT_INTEGER, Field.FF_SYSTEM_KEY));
        piEntity.addField(new FieldImpl(FLD_PRESENTATION_CLASS, Field.FT_STRING, Field.FF_UNIQUE_KEY, 100));
        piEntity.addField(new FieldImpl(FLD_PAGE, Field.FT_STRING, Field.FF_UNIQUE_KEY, 50));
        piEntity.addField(new FieldImpl(FLD_POSITION, Field.FT_STRING, Field.FF_UNIQUE_KEY, 50));
        piEntity.addField(new FieldImpl(FLD_SEQUENCE, Field.FT_INTEGER, Field.FF_UNIQUE_KEY));
        piEntity.addField(new FieldImpl(FLD_DESCRIPTION, Field.FT_STRING, Field.FF_NAMING, 100));
        piEntity.addField(new FieldImpl(FLD_CONTENT_TYPE, Field.FT_STRING, Field.FF_NONE, 50));
        piEntity.addField(new FieldImpl(FLD_CONTENT, Field.FT_STRING, Field.FF_NONE, 200));

        // return the entity
        return piEntity;
    }

    //--------------------------------------------------------------------------
    /**
     * Return the Entity name.
     * @return the entity name.
     */
    public String getEntityName()
    {
        return ENT_PRESENTATION_ITEM;
    }

    //--------------------------------------------------------------------------
    /**
     * SystemId accessor.
     */
    public int getSystemId()
    {
        return getIntValue(FLD_ID);
    }

    //--------------------------------------------------------------------------
    /**
     * SystemId accessor.
     */
    public void setSystemId(int value)
    {
        setValue(FLD_ID, value);
    }

    //--------------------------------------------------------------------------
    /**
     * PresentationClass accessor.
     * This should contain the name of a CustomPresentation subclass.
     */
    public String getPresentationClass()
    {
        return getStringValue(FLD_PRESENTATION_CLASS);
    }

    //--------------------------------------------------------------------------
    /**
     * PresentationClass accessor.
     * This should contain the name of a CustomPresentation subclass.
     */
    public void setPresentationClass(String value)
    {
        setValue(FLD_PRESENTATION_CLASS, value);
    }

    //--------------------------------------------------------------------------
    /**
     * Position accessor.
     * Should be either HEADER or FOOTER.
     */
    public String getPosition()
    {
        return getStringValue(FLD_POSITION);
    }

    //--------------------------------------------------------------------------
    /**
     * Position accessor.
     * Should be either HEADER or FOOTER.
     */
    public void setPosition(String value)
    {
        setValue(FLD_POSITION, value);
    }

    //--------------------------------------------------------------------------
    /**
     * Page accessor.
     * The name of a defined page as listed by a CustomPresentation subclass
     * implementation of getCustomizablePages().
     */
    public String getPage()
    {
        return getStringValue(FLD_PAGE);
    }

    //--------------------------------------------------------------------------
    /**
     * Page accessor.
     * The name of a defined page as listed by a CustomPresentation subclass
     * implementation of getCustomizablePages().
     */
    public void setPage(String value)
    {
        setValue(FLD_PAGE, value);
    }

    //--------------------------------------------------------------------------
    /**
     * Sequence accessor.
     * A numneric sequence number that specifies the order in which the
     * PresentationItems are displayed.
     */
    public int getSequence()
    {
        return getIntValue(FLD_SEQUENCE);
    }

    //--------------------------------------------------------------------------
    /**
     * Sequence accessor.
     * A numneric sequence number that specifies the order in which the
     * PresentationItems are displayed.
     */
    public void setSequence(int value)
    {
        setValue(FLD_SEQUENCE, value);
    }

    //--------------------------------------------------------------------------
    /**
     * Description accessor.
     * A freeform textual description of this PresentationItem.
     */
    public String getDescription()
    {
        return getStringValue(FLD_DESCRIPTION);
    }

    //--------------------------------------------------------------------------
    /**
     * Description accessor.
     * A freeform textual description of this PresentationItem.
     */
    public void setDescription(String value)
    {
        setValue(FLD_DESCRIPTION, value);
    }

    //--------------------------------------------------------------------------
    /**
     * ContentType accessor.
     * Should be either CT_TEXT or CT_URL.
     */
    public String getContentType()
    {
        return getStringValue(FLD_CONTENT_TYPE);
    }

    //--------------------------------------------------------------------------
    /**
     * ContentType accessor.
     * Should be either CT_TEXT or CT_URL.
     */
    public void setContentType(String value)
    {
        setValue(FLD_CONTENT_TYPE, value);
    }

    //--------------------------------------------------------------------------
    /**
     * Content accessor.
     * The custom content of this PresentationItem. Either text or a URL
     * depending on the content type.
     */
    public String getContent()
    {
        return getStringValue(FLD_CONTENT);
    }

    //--------------------------------------------------------------------------
    /**
     * Content accessor.
     * The custom content of this PresentationItem. Either text or a URL
     * depending on the content type.
     */
    public void setContent(String value)
    {
        setValue(FLD_CONTENT, value);
    }
}

//==============================================================================
// end of file PresentationItem.java
//==============================================================================
