package wsl.mdn.dataview;

// imports
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Vector;

import wsl.fw.datasource.DataManager;
import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.DataSource;
import wsl.fw.datasource.DataSourceException;
import wsl.fw.datasource.DefaultKeyGeneratorData;
import wsl.fw.datasource.Entity;
import wsl.fw.datasource.EntityImpl;
import wsl.fw.datasource.Field;
import wsl.fw.datasource.FieldImpl;
import wsl.fw.datasource.Query;
import wsl.fw.datasource.QueryCriterium;
import wsl.fw.datasource.RecordSet;
import wsl.fw.util.Util;

//------------------------------------------------------------------------------
/**
 *
 */
public class DataTransfer extends DataObject
{
    //--------------------------------------------------------------------------
    // constants

    public final static String ENT_DATATRANSFER     = "TBL_DATATRANSFER";
    public final static String FLD_ID               = "FLD_ID";
    public final static String FLD_NAME             = "FLD_NAME";
    public final static String FLD_DSID             = "FLD_DSID";
    public final static String FLD_DESCRIPTION      = "FLD_DESCRIPTION";
    public final static String FLD_FILTER           = "FLD_FILTER";


    //--------------------------------------------------------------------------
    // attributes
    private transient Vector         _transferEntities = new Vector();
    private transient Vector         _imageEntities    = null;
    private transient Vector         _deleteEntities   = new Vector();
    private transient boolean        _tesLoaded        = false;
    private transient DataSourceDobj _sourceDs         = null;
    private transient QueryDobj      _filter           = null;

    //--------------------------------------------------------------------------
    // construction

    /**
     * Blank ctor
     */
    public DataTransfer()
    {
    }


    //--------------------------------------------------------------------------
    // persistence

    /**
     * Static factory method to create the entity to be used by this dataobject
     * and any subclasses. This is called by the DataManager's factory when
     * creating a FIELD entity.
     * @return the created entity.
     */
    public static Entity createEntity()
    {
        // create the entity
        Entity ent = new EntityImpl(ENT_DATATRANSFER, DataTransfer.class);

        // add the key generator for the system id
        ent.addKeyGeneratorData(new DefaultKeyGeneratorData(ENT_DATATRANSFER, FLD_ID));

        // create the fields and add them to the entity
        ent.addField(new FieldImpl(FLD_ID, Field.FT_INTEGER, Field.FF_SYSTEM_KEY | Field.FF_UNIQUE_KEY));
        ent.addField(new FieldImpl(FLD_NAME, Field.FT_STRING, Field.FF_NAMING));
        ent.addField(new FieldImpl(FLD_DESCRIPTION, Field.FT_STRING));
        ent.addField(new FieldImpl(FLD_DSID, Field.FT_INTEGER));
        ent.addField(new FieldImpl(FLD_FILTER, Field.FT_STRING));

        // return the entity
        return ent;
    }

    /**
     * @return String the name of the entity that defines this DataObject
     */
    public String getEntityName()
    {
        return ENT_DATATRANSFER;
    }


    //--------------------------------------------------------------------------
    // accessors Note the get/setName() are in entity interface section

    /**
     * @return int the id
     */
    public int getId()
    {
        return getIntValue(FLD_ID);
    }

    /**
     * Sets the id
     * @param id the id to set
     */
    public void setId(int id)
    {
        setValue(FLD_ID, id);
    }

    /**
     * Returns the name
     * @return String the name
     */
    public String getName()
    {
        return getStringValue(FLD_NAME);
    }

    /**
     * Sets the name
     * @param val the value to set
     */
    public void setName(String val)
    {
        setValue(FLD_NAME, val);
    }

    /**
     * @return int the datasource id
     */
    public int getDataSourceId()
    {
        return getIntValue(FLD_DSID);
    }

    /**
     * Set the datasource id
     * @param id
     */
    public void setDataSourceId(int id)
    {
        // if diff from the old one, null the source ds
        if(this.getDataSourceId() != id)
        {
            _sourceDs = null;

            // set the value
            setValue(FLD_DSID, id);
        }
    }


    /**
     * @return String the filter
     */
    public String getFilter()
    {
        return getStringValue(FLD_FILTER);
    }

    /**
     * Set the filter
     * @param filter
     */
    public void setFilter(String filter)
    {
        setValue(FLD_FILTER, filter);
    }

    /**
     * Returns the description of the datatransfer
     * @return String
     */
    public String getDescription()
    {
        return getStringValue(FLD_DESCRIPTION);
    }

    /**
     * Sets the entity description into the datatransfer
     * @param name
     * @return void
     */
    public void setDescription(String name)
    {
        setValue(FLD_DESCRIPTION, name);
    }


    //--------------------------------------------------------------------------
    // source datasource

    public DataSourceDobj getSourceDataSource() throws DataSourceException
    {
        // if null, load
        if(_sourceDs == null)
        {
            // get the dsid
            int dsid = this.getDataSourceId();

            // build query
            DataSource sysDs = DataManager.getSystemDS();
            Query q = new Query(new QueryCriterium(DataSourceDobj.ENT_DATASOURCE,
                DataSourceDobj.FLD_ID, QueryCriterium.OP_EQUALS, new Integer(dsid)));
            RecordSet rs = sysDs.select(q);
            _sourceDs = (rs != null && rs.next())? (DataSourceDobj)rs.getCurrentObject(): null;
        }

        // return
        return _sourceDs;
    }


    //--------------------------------------------------------------------------
    // Transfer Entities

    /**
     * Add a single TransferEntity
     * @param te the te to add
     */
    public void addTransferEntity(TransferEntity te)
    {
        // validate
        Util.argCheckNull(te);

        // add the te
        _transferEntities.add(te);

        // set the loaded flag
        _tesLoaded = true;
    }

    /**
     * Returns the Transfer Entities of the DataTransfer
     * @return Vector
     */
    public Vector getTransferEntities()
    {
        // if not loaded, load
        if(!_tesLoaded)
        {
            // must be indb
            if(this.getState() == DataObject.IN_DB)
            {
                // build query
                DataSource ds = DataManager.getSystemDS();
                Query q = new Query(TransferEntity.ENT_TRANSFERENTITY);
                q.addQueryCriterium(new QueryCriterium(TransferEntity.ENT_TRANSFERENTITY,
                    TransferEntity.FLD_DTID, QueryCriterium.OP_EQUALS,
                    new Integer(this.getId())));

                try
                {
                    // execute query and build vector
                    TransferEntity te;
                    RecordSet rs = ds.select(q);
                    while(rs != null && rs.next())
                    {
                        // get the te and entity
                        te = (TransferEntity)rs.getCurrentObject();
                        if(te != null)
                            addTransferEntity(te);
                    }
                }
                catch(Exception e)
                {
                    throw new RuntimeException(e.toString());
                }
            }

            // set flag
            _tesLoaded = true;
        }

        // return
        return _transferEntities;
    }

    /**
     * Remove a TransferEntity
     */
    public TransferEntity removeTransferEntity(TransferEntity te, boolean doDelete)
    {
        // if it is indb, remove by key
        TransferEntity rem = null;
        if(te.getState() == DataObject.IN_DB)
        {
            // iterate
            TransferEntity temp;
            for(int i = 0; i < getTransferEntities().size(); i++)
            {
                temp = (TransferEntity)getTransferEntities().elementAt(i);
                if(temp != null && temp.getState() == DataObject.IN_DB &&
                    temp.getId() == te.getId())
                    rem = (TransferEntity)getTransferEntities().remove(i);
            }
        }

        // else, try to remove by ref
        else
        {
            if(getTransferEntities().remove(te))
                rem = te;
        }

        try
        {
            // delete
            if(doDelete && rem.getState() == DataObject.IN_DB)
            {
                // imaging?
                if(isImaging())
                    _deleteEntities.add(rem);
                else
                    rem.delete();
            }
        }
        catch(Exception e)
        {
            throw new RuntimeException(e.toString());
        }

        // return
        return rem;
    }


    //--------------------------------------------------------------------------
    // cascading save

    /**
     * Called pre-save
     */
    protected void preSave() throws DataSourceException
    {
        // get the filter string
        if(_filter != null)
        {
            // set the filter
            setFilter(_filter.createCriteriaString());
        }
    }

    /**
     * Called post insert / update call
     */
    protected void postSave() throws DataSourceException
    {
        // process imaging, delete the delete fields
        if(isImaging())
            processImage();

        // save all tes
        TransferEntity te;
        for(int i = 0; _tesLoaded && i < getTransferEntities().size(); i++)
        {
            te = (TransferEntity)getTransferEntities().elementAt(i);
            if(te != null)
            {
                // if new set keys
                if(te.getState() == DataObject.NEW)
                    te.setDataTransferId(this.getId());

                // save
                te.save();
            }
        }
    }


    //--------------------------------------------------------------------------
    // cascading delete

    /**
     * Called pre delete call
     */
    protected void preDelete() throws DataSourceException
    {
        // delete all fields
        TransferEntity te;
        for(int i = 0; i < getTransferEntities().size(); i++)
        {
            te = (TransferEntity)getTransferEntities().elementAt(i);
            if(te != null && te.getState() == DataObject.IN_DB)
                te.delete();
        }
        getTransferEntities().clear();
    }


    //--------------------------------------------------------------------------
    // field imaging

    /**
     * Start an imaging session
     */
    public void imageDataTransfer()
    {
        // clone vectors
        _imageEntities = (Vector)getTransferEntities().clone();
    }

    /**
     * Reverts to the image
     */
    public void revertToImage()
    {
        // must be imaging
        if(isImaging())
        {
            // set fields vector back to image
            _transferEntities = _imageEntities;

            // clear the image
            clearImage();
        }
    }

    /**
     * End the imaging session and delete image deletes
     */
    public void processImage() throws DataSourceException
    {
        // iterate deletes
        TransferEntity te;
        for(int i = 0; i < _deleteEntities.size(); i++)
        {
            te = (TransferEntity)_deleteEntities.elementAt(i);
            if(te != null && te.getState() == DataObject.IN_DB)
                te.delete();
        }

        // clear image
        clearImage();
    }

    /**
     * Clear the image
     */
    public void clearImage()
    {
        // clear the image and delete vector
        _imageEntities = null;
        _deleteEntities.clear();
        _filter = null;
    }

    /**
     * @return boolean true if imaging
     */
    public boolean isImaging()
    {
        return _imageEntities != null;
    }


    //--------------------------------------------------------------------------
    // filter

    public QueryDobj getFilterQuery()
    {
        // if null, create
        if(_filter == null)
        {
            // new
            _filter = new QueryDobj();

            // set criteria
            _filter.setCriteriaString(this.getFilter());
        }

        // return
        return _filter;
    }

    //--------------------------------------------------------------------------
    /**
     * Serialization support.
     */
    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException
    {
        _transferEntities = new Vector();
        _imageEntities    = null;
        _deleteEntities   = new Vector();
        _tesLoaded        = false;
        _sourceDs         = null;
        _filter           = null;

        // do default loading
        in.defaultReadObject();
    }
}