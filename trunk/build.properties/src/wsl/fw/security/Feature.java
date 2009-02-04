//==============================================================================
// Feature.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.security;

import wsl.fw.util.Util;
import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.Entity;
import wsl.fw.datasource.Field;
import wsl.fw.datasource.EntityImpl;
import wsl.fw.datasource.FieldImpl;
import wsl.fw.datasource.JoinImpl;
import wsl.fw.datasource.DataSourceException;
import java.util.Vector;
import wsl.fw.resource.ResId;

//------------------------------------------------------------------------------
/**
 * Class to represent a feature that may be secured. Features are represented by
 * a name string, using structured names.
 */
public class Feature extends DataObject
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/security/Feature.java $ ";

    // resources
    public static final ResId EXCEPTION_SAVE  = new ResId("Feature.exception.Save");
    public static final ResId EXCEPTION_DELETE  = new ResId("Feature.exception.Dalete");

    // the entity name
    public final static String ENT_FEATURE = "FW_FEATURE";

    // field names
    public final static String FLD_NAME       	= "FLD_NAME";
    public final static String FLD_DESCRIPTION	= "FLD_DESCRIPTION";
    public final static String FLD_TYPE 		= "FLD_TYPE";

    //--------------------------------------------------------------------------
    /**
     * Default constructor. Intended for use by the datasource when creating
     * instances. Since a Feature is invalid if it is not correctly initialized
     * ensure that setName is called when using this constructor/
     */
    public Feature()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Constructor, set the name and description of the feature.
     * @param featureName, the name of this feature.
     * @param featureDescription, the description of this feature
     * @throws IllegalArgumentException if the parameters are ivalid
     */
    public Feature(String featureName, String featureDescription)
        throws IllegalArgumentException
    {
        this(featureName, featureDescription, true);
    }

    //--------------------------------------------------------------------------
    /**
     * Constructor, set the name and description of the feature.
     * Calling init is optional so that we can use Feature constants which are
     * initialized at static init time, which is generally before a data manager
     * would be set.
     * @param featureName, the name of this feature.
     * @param featureDescription, the description of this feature.
     * @param doInit, if true init is called during construction as normal. If
     *   false init is skipped, this allows construction even if no data manager
     *   is set, as is the case with Feature constants.
     * @throws IllegalArgumentException if the parameters are ivalid
     */
    public Feature(String featureName, String featureDescription, boolean doInit)
        throws IllegalArgumentException
    {
        // optionally call init.
        super(doInit);

        // save feature name and description
        setName(featureName);
        setDescription(featureDescription);
    }

    //--------------------------------------------------------------------------
    /**
     * Static factory method to create the entity to be used by this dataobject
     * and any subclasses. This is called by the DataManager's factory when
     * creating a FEATURE entity.
     * @return the created entity.
     */
    public static Entity createEntity()
    {
        // create the feature entity
        Entity featureEntity = new EntityImpl(ENT_FEATURE, Feature.class);

        // create the fields and add them to the entity
        featureEntity.addField(new FieldImpl(FLD_NAME, Field.FT_STRING,
            Field.FF_SYSTEM_KEY | Field.FF_UNIQUE_KEY, 100));
        featureEntity.addField(new FieldImpl(FLD_DESCRIPTION, Field.FT_STRING, Field.FF_NAMING, 400));
        featureEntity.addField(new FieldImpl(FLD_TYPE, Field.FT_STRING, 100));

        // return the entity
        return featureEntity;
    }

    //--------------------------------------------------------------------------
    /**
     * Return the Entity name.
     * @return the entity name.
     */
    public String getEntityName()
    {
        return ENT_FEATURE;
    }

    //--------------------------------------------------------------------------
    /**
     * Get the name of the feature.
     * @return the name of the feature.
     */
    public String getName()
    {
        return getStringValue(FLD_NAME);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the feature name.
     * @param featureName, the name of the feature.
     * @throws IllegalArgumentException if the feature name is null or empty.
     */
    public void setName(String featureName) throws IllegalArgumentException
    {
        // check parameter, null or empty feature names are not permitted
        Util.argCheckEmpty(featureName);
        setValue(FLD_NAME, featureName);
    }

    //--------------------------------------------------------------------------
    /**
     * Get the description of the feature.
     * @return the description of the feature.
     */
    public String getDescription()
    {
        return getStringValue(FLD_DESCRIPTION);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the description.
     * @param featureDescription, the description.
     * @throws IllegalArgumentException if the feature description is null.
     */
    public void setDescription(String featureDescription)
        throws IllegalArgumentException
    {
        // check parameter, null descriptions are not permitted
        Util.argCheckNull(featureDescription);
        setValue(FLD_DESCRIPTION, featureDescription);
    }
    //--------------------------------------------------------------------------
    /**
     * Get the type of the feature.
     * @return the type of the feature.
     */
    public String getType()
    {
        return getStringValue(FLD_TYPE);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the type.
     * @param type, the type.
     * @throws IllegalArgumentException if the feature type is null.
     */
    public void setType(String type)
        throws IllegalArgumentException
    {
        // check parameter, null type are not permitted
        Util.argCheckNull(type);
        setValue(FLD_TYPE, type);
    }
    //--------------------------------------------------------------------------
    /**
     * Override to stop save on Features.
     */
    public void save() throws DataSourceException
    {
        throw new UnsupportedOperationException(EXCEPTION_SAVE.getText());
    }

    //--------------------------------------------------------------------------
    /**
     * Override to stop delete on Features.
     */
    public void delete() throws DataSourceException
    {
        throw new UnsupportedOperationException(EXCEPTION_DELETE.getText());
    }

    //--------------------------------------------------------------------------
    /**
     * Package only reimplementation of save so FwFeatures and subclasses
     * can save the feature set.
     */
    void saveInternal() throws DataSourceException
    {
        super.save();
    }
}


//==============================================================================
// end of file Feature.java
//==============================================================================
