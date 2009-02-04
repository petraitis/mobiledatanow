package wsl.mdn.dataview;

import wsl.fw.datasource.DataSourceException;
import wsl.fw.datasource.Entity;
import wsl.fw.datasource.Field;
import wsl.fw.datasource.DataSource;
import wsl.fw.datasource.DataObject;
import wsl.fw.resource.ResId;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

//------------------------------------------------------------------------------
/**
 *
 */
public class DirectQueryDataView extends DataView
{
    public final static ResId ERR_QUERY = new ResId("mdn.DirectQueryDataView.queryFailure");


    //--------------------------------------------------------------------------
    /**
     *
     */
    public DirectQueryDataView()
    {
    }

    public void setQuery(String sql) throws DataSourceException
    {
        Entity ent = null;

        DataSourceDobj dsDobj = getSourceDataSource();

        int dsID = dsDobj.getId();
        if (dsDobj.isMirrored())
            dsID = dsDobj.getMirrorId();

        DataSource ds = MdnDataCache.getCache().getDataSource(dsID, true);
        if (ds == null)
            throw new DataSourceException("Unable to retrieve mirror datasource.");

        ent = ds.importRawSelectDefinition(sql);

        if (ent == null)
            throw new DataSourceException(ERR_QUERY.getText());

        DirectQueryDobj qdobj = getQueryDobj();
        if (qdobj == null)
        {
            // add a new DirectQueryDobj
            qdobj = new DirectQueryDobj();
            qdobj.setRawSQL(sql);
            qdobj.setName(getName());
            qdobj.setDataView(this);
            qdobj.setViewOrTableId(getId());
            addQuery(qdobj);
        }

        if (getState() == DataObject.IN_DB) // update record
        {
            if (qdobj != null)
                qdobj.setRawSQL(sql);

            Field fld;
            // this is an update.  create a copy of the existing fields
            Vector oldFlds = (Vector)getFields().clone();

            // now run thru the new fields, and see if they match the old ones
            Vector fields = ent.getFields();
            Field oldFld;
            for (int i = 0; i < fields.size(); i++)
            {
                fld = (Field)fields.elementAt(i);
                if (i < oldFlds.size())
                    oldFld = (Field)oldFlds.elementAt(i);
                else
                    oldFld = null;

                if (oldFld != null && oldFld.getName().equals(fld.getName()))
                {
                    // this is an existing field, take it out of the hashtable so it is not removed later
                    oldFlds.setElementAt(null, i);
                }
                else
                {
                    // this is a new field, so add it
                    DataViewField dvf = new DataViewField();
                    dvf.setDisplayName(fld.getName());
                    dvf.setName(fld.getName());
                    dvf.setColumnSize(fld.getColumnSize());
                    dvf.setFlags(fld.getFlags());
                    dvf.setType(fld.getType());
                    dvf.setSourceDataSource(getSourceDataSource());
                    addField(dvf);
                }
            }

            // now remove all remaning fields in the hashtable
            for (int i = 0; i < oldFlds.size(); i++)
            {
                Field f = (Field)oldFlds.elementAt(i);
                if (f != null)
                    removeField((DataViewField)f, true);
            }
        }
        else // insert record
        {
            Vector fields = ent.getFields();
            Field fld;
            for (int i = 0; i < fields.size(); i++)
            {
                fld = (Field)fields.elementAt(i);
                DataViewField dvf = new DataViewField();
                dvf.setDisplayName(fld.getName());
                dvf.setName(fld.getName());
                dvf.setColumnSize(fld.getColumnSize());
                dvf.setFlags(fld.getFlags());
                dvf.setType(fld.getType());
                dvf.setSourceDataSource(getSourceDataSource());
                addField(dvf);
            }
        }
    }

    public DirectQueryDobj getQueryDobj() throws DataSourceException
    {
        Vector queries = getQueries();
        // should be 0-1 queries
        if (queries.size() == 0)
            return null;

        return (DirectQueryDobj)queries.elementAt(0);
    }

    public String getQuery() throws DataSourceException
    {
        DirectQueryDobj dobj = getQueryDobj();
        if (dobj != null)
            return dobj.getRawSQL();

        return "";
    }

    //--------------------------------------------------------------------------
    /**
     * Save the DataObject into its DataSource. Delegates to insert() or
     * update() based on state.
     * @throws DataSourceException if there is an error from the DataSource.
     */
    public void save() throws DataSourceException
    {
        boolean updating = getState() == DataObject.IN_DB;

        String query = getQuery();

        super.save();

        DirectQueryDobj qDobj = getQueryDobj();

        if (!updating)
        {
            // Create the DirectQueryDobj.
            if (qDobj == null)
            {
                qDobj = new DirectQueryDobj();
                qDobj.setRawSQL(query);
                qDobj.setName(getName());
                qDobj.setDataView(this);
                qDobj.setViewOrTableId(getId());
                addQuery(qDobj);
            }
        }
        else
        {
            qDobj = getQueryDobj();
            qDobj.setRawSQL(query);
            qDobj.setName(getName());
        }

        if (qDobj != null)
            qDobj.save();
    }
}