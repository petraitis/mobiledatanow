//Source file: D:\\dev\\wsl\\mdn\\dataview\\GroupDataView.java

package wsl.mdn.dataview;

import java.util.Vector;
import java.lang.Integer;

import wsl.fw.datasource.*;
import wsl.fw.security.Group;
import wsl.fw.util.Log;

//------------------------------------------------------------------------------
/**
 *
 */
public class GroupDataView extends DataObject
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:35:35 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/dataview/GroupDataView.java $ ";

    //--------------------------------------------------------------------------
    // constants

    public final static String ENT_GROUPDATAVIEW    = "TBL_GROUPDATAVIEW";

    public final static String FLD_ID               = "FLD_ID";
    public final static String FLD_GROUPID          = "FLD_GROUPID";
    public final static String FLD_DATAVIEWID       = "FLD_DATAVIEWID";


    public GroupDataView()
    {
    }

    /**
     * @return String the name of the entity that defines this DataObject
     */
    public String getEntityName()
    {
        return ENT_GROUPDATAVIEW;
    }

    //--------------------------------------------------------------------------
    // persistence

    /**
     * Static factory method to create the entity to be used by this dataobject
     * and any subclasses. This is called by the DataManager's factory when
     * creating a GROUPDATAVIEW entity.
     * @return the created entity.
     */
    public static Entity createEntity()
    {
        // create the entity
        Entity ent = new EntityImpl(ENT_GROUPDATAVIEW, GroupDataView.class);

        // add the key generator for the system id
        ent.addKeyGeneratorData(new DefaultKeyGeneratorData(ENT_GROUPDATAVIEW, FLD_ID));

        // create the fields and add them to the entity
        ent.addField(new FieldImpl(FLD_ID, Field.FT_INTEGER, Field.FF_SYSTEM_KEY));
        ent.addField(new FieldImpl(FLD_GROUPID, Field.FT_INTEGER, Field.FF_UNIQUE_KEY | Field.FF_NAMING));
        ent.addField(new FieldImpl(FLD_DATAVIEWID, Field.FT_INTEGER, Field.FF_UNIQUE_KEY | Field.FF_NAMING));

//        ent.addField(new FieldImpl(FLD_CANEDIT, Field.FT_INTEGER));
//        ent.addField(new FieldImpl(FLD_CANDELETE, Field.FT_INTEGER));

        // create the joins and add them to the entity
        Vector joins = new Vector();
        joins.add(new JoinImpl(ENT_GROUPDATAVIEW, FLD_GROUPID, Group.ENT_GROUP, Group.FLD_ID));
        joins.add(new JoinImpl(ENT_GROUPDATAVIEW, FLD_DATAVIEWID, DataViewField.ENT_DVFIELD, DataView.FLD_ID));
        ent.setJoins(joins);

        // return the entity
        return ent;
    }
    //--------------------------------------------------------------------------
    // accessors


    /**
     * @return int the id of this Entity
     */
    public int getId()
    {
        return getIntValue(FLD_ID);
    }

    /**
     * Set the id of this Entity
     * @param id
     */
    public void setId(int id)
    {
        setValue(FLD_ID, id);
    }

    /**
     * Returns the group id
     * @return int
     */
    public int getGroupId()
    {
        return getIntValue(FLD_GROUPID);
    }

    /**
     * Set the group id
     * @param  groupId the value to set
     */
    public void setGroupId(int groupId)
    {
        setValue(FLD_GROUPID, groupId);
    }

    /**
     * Returns the dataview id
     * @return int
     */
    public int getDataViewId()
    {
        return getIntValue(FLD_DATAVIEWID);
    }

    /**
     * Set the dataview id
     * @param dataviewId the value to set
     */
    public void setDataViewId(int dataviewId)
    {
        setValue(FLD_DATAVIEWID, dataviewId);
    }

    /**
     *  This method return DataView.FLD_NAME field of DataView object for this GroupDataView object
     */
    public String toString()
    {
        DataSource ds = DataManager.getSystemDS();
        Query q = new Query(DataView.ENT_DATAVIEW);
        QueryCriterium qc = new QueryCriterium(DataView.ENT_DATAVIEW, FLD_ID,
            QueryCriterium.OP_EQUALS, new Integer(getDataViewId()));
        q.addQueryCriterium(qc);
        RecordSet rs;
        try
        {
            rs = ds.select(q);
            if (rs.getRows().size() == 1)
            {
                rs.next();
                return ((DataView)rs.getCurrentObject()).getStringValue(DataView.FLD_NAME);
            }
        }
        catch (Exception e)
        {
            Log.error("GroupDataView.toString: ", e);
        }
        return null;
    }
}
