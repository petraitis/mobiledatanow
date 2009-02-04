//==============================================================================
// MenuAction.java
// Copyright (c) 2001 WAP Solutions Ltd.
//==============================================================================

package wsl.mdn.guiconfig;

import wsl.fw.datasource.*;
import wsl.fw.security.Group;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.util.Type;
import wsl.fw.util.TypeConversionException;
import wsl.mdn.dataview.DataView;
import wsl.mdn.dataview.QueryDobj;
import java.util.Vector;
import java.util.Collections;
import java.util.Comparator;
import java.io.ObjectInputStream;
import java.io.IOException;

//------------------------------------------------------------------------------
/**
 * Base class for configurable actions that can be placed in a menu using the
 * menu builder.
 *
 * Note that while the aggregation of other menu items is in this base class
 * it is intended that for the initial version of the presentation and builder
 * only the Submenu will have child MenuActions.
 */
public class MenuAction extends DataObject implements IMenuAction
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:35:35 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/guiconfig/MenuAction.java $ ";

    // the entity name
    public final static String ENT_MENUACTION = "ENT_MENUACTION";

    // field names
    // Note FLD_CLASS for polymorph support
    public final static String FLD_ID           = "FLD_ID";
    public final static String FLD_CLASS        = JdbcDataSource.CLASS_COLUMN_NAME;
    public final static String FLD_NAME         = "FLD_NAME";
    public final static String FLD_DESCRIPTION  = "FLD_DESCRIPTION";
    public final static String FLD_SEQUENCE     = "FLD_SEQUENCE";
    public final static String FLD_GROUPID      = "FLD_GROUPID";
    public final static String FLD_PARENTMENUID = "FLD_PARENTMENUID";
    public final static String FLD_DATAVIEWID   = "FLD_DATAVIEWID";
    public final static String FLD_QUERYDOBJID  = "FLD_QUERYDOBJID";
    public final static String FLD_LINK         = "FLD_LINK";
    public final static String FLD_MSGSERVERID  = "FLD_MSGSERVERID";
    public final static String FLD_MSGSERVER_FLAGS = "FLD_MSGSERVER_FLAGS";

    // attributes
    protected transient Vector  _children       = new Vector();
    protected transient boolean _childrenLoaded = false;

    //--------------------------------------------------------------------------
    /**
     * Inner class Comparator to compare MenuActions by Sequence.
     * Note, this comparator is inconsistent with equals.
     */
    public class SequenceComparator implements Comparator
    {
        public int compare(Object o1, Object o2)
        {
            MenuAction ma1 = (MenuAction) o1;
            MenuAction ma2 = (MenuAction) o2;

            return ma1.getSequence() - ma2.getSequence();
        }

        public boolean equals(Object obj)
        {
            return (obj instanceof SequenceComparator);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    protected MenuAction()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Full constructor.
     * @param name, name of the action.
     * @param description, description of the action.
     * @param groupId, id of the group that owns this menu
     * @param parentMenuId, id of the paren MenuAction that contains this.
     */
    protected MenuAction(String name, String description, Object groupId,
        Object parentMenuId)
    {
        setName(name);
        setDescription(description);
        setGroupId(groupId);
        setParentMenuId(parentMenuId);
    }

    //--------------------------------------------------------------------------
    /**
     * Static factory method to create the entity to be used by this dataobject
     * and any subclasses. This is called by the DataManager's factory when
     * creating a MENUACTION entity.
     * @return the created entity.
     */
    public static Entity createEntity()
    {
        // create the MenuAction entity
        Entity maEntity = new EntityImpl(ENT_MENUACTION, null);

        // add the key generator for the system id
        maEntity.addKeyGeneratorData(new DefaultKeyGeneratorData(ENT_MENUACTION, FLD_ID));

        // create the fields and add them to the entity
        maEntity.addField(new FieldImpl(FLD_ID, Field.FT_INTEGER, Field.FF_SYSTEM_KEY | Field.FF_UNIQUE_KEY));
        maEntity.addField(new FieldImpl(FLD_CLASS, Field.FT_STRING)); // polymorph support
        maEntity.addField(new FieldImpl(FLD_NAME, Field.FT_STRING, Field.FF_NAMING, 255));
        maEntity.addField(new FieldImpl(FLD_DESCRIPTION, Field.FT_STRING, Field.FF_NONE, 255));
        maEntity.addField(new FieldImpl(FLD_SEQUENCE, Field.FT_INTEGER));
        maEntity.addField(new FieldImpl(FLD_GROUPID, Field.FT_INTEGER));
        maEntity.addField(new FieldImpl(FLD_PARENTMENUID, Field.FT_INTEGER));
        maEntity.addField(new FieldImpl(FLD_DATAVIEWID, Field.FT_INTEGER));
        maEntity.addField(new FieldImpl(FLD_QUERYDOBJID, Field.FT_INTEGER));
        maEntity.addField(new FieldImpl(FLD_LINK, Field.FT_STRING, Field.FF_NONE, 255));
        maEntity.addField(new FieldImpl(FLD_MSGSERVERID, Field.FT_INTEGER));
        maEntity.addField(new FieldImpl(FLD_MSGSERVER_FLAGS, Field.FT_INTEGER));

        // add the join to group.
        // note  that there is a logical self join which are not expressed here
        maEntity.addJoin(new JoinImpl(ENT_MENUACTION, FLD_GROUPID, Group.ENT_GROUP, Group.FLD_ID));

        maEntity.addJoin(new JoinImpl(ENT_MENUACTION, FLD_DATAVIEWID, DataView.ENT_DATAVIEW, DataView.FLD_ID));
        maEntity.addJoin(new JoinImpl(ENT_MENUACTION, FLD_QUERYDOBJID, QueryDobj.ENT_QUERY, QueryDobj.FLD_ID));

        // return the entity
        return maEntity;
    }

    //--------------------------------------------------------------------------
    /**
     * Return the Entity name.
     * @return the entity name.
     * @roseuid 3A821D67039E
     */
    public String getEntityName()
    {
        return ENT_MENUACTION;
    }

    //--------------------------------------------------------------------------
    /**
     * @return the ID
     */
    public Object getId()
    {
        return getObjectValue(FLD_ID);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the ID.
     */
    public void setId(Object id)
    {
        setValue(FLD_ID, id);
    }

    //--------------------------------------------------------------------------
    /**
     * Get the name of the group.
     * @return the name of the menu action.
     */
    public String getName()
    {
        return getStringValue(FLD_NAME);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the menu action name.
     * @param name, the name of the menu action.
     */
    public void setName(String name)
    {
        setValue(FLD_NAME, name);
    }


    //--------------------------------------------------------------------------
    /**
     * @return the ID of the dataview.
     */
    public Object getDataViewId()
    {
        return getObjectValue(FLD_DATAVIEWID);
    }

    //--------------------------------------------------------------------------
    /**
     * @return the ID of the dataview.
     */
    public int getIntDataViewId()
    {
        return getIntValue(FLD_DATAVIEWID);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the ID of the dataview.
     */
    public void setDataViewId(Object id)
    {
        setValue(FLD_DATAVIEWID, id);
    }

    //--------------------------------------------------------------------------
    /**
     * Get the description of the menu action.
     * @return the description of the menu action.
     */
    public String getDescription()
    {
        return getStringValue(FLD_DESCRIPTION);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the menu action description.
     * @param name, the name of the menu action.
     */
    public void setDescription(String description)
    {
        setValue(FLD_DESCRIPTION, description);
    }

    //--------------------------------------------------------------------------
    /**
     * @return the sequence number for this menu item. This allows the order in
     *   which the items are displayed to be altered.
     */
    public int getSequence()
    {
        return getIntValue(FLD_SEQUENCE);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the sequence number for this menu item.
     */
    public void setSequence(int seqNum)
    {
        setValue(FLD_SEQUENCE, seqNum);
    }

    //--------------------------------------------------------------------------
    /**
     * @return the ID of the owning Group.
     */
    public Object getGroupId()
    {
        return getObjectValue(FLD_GROUPID);
    }

    //--------------------------------------------------------------------------
    /**
     * @return the ID of the owning Group as an int.
     */
    public int getIntGroupId()
    {
        return getIntValue(FLD_GROUPID);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the ID of the owning Group.
     */
    public void setGroupId(Object id)
    {
        setValue(FLD_GROUPID, id);
    }

    //--------------------------------------------------------------------------
    // set groupid of this and all children recursively

    public void setGroupIdDeep(Object id)
    {
        // this
        this.setGroupId(id);

        // iterate children
        MenuAction child;
        for(int i = 0; i < getChildren().size(); i++)
        {
            // get child and set group id deep
            child = (MenuAction)getChildren().elementAt(i);
            if(child != null)
                child.setGroupIdDeep(id);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * @return the ID of the parent menu, or null if part of the top level menu
     *   for a group.
     */
    public Object getParentMenuId()
    {
        return getObjectValue(FLD_PARENTMENUID);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the ID of the parent menu.
     */
    public void setParentMenuId(Object id)
    {
        setValue(FLD_PARENTMENUID, id);
    }

    //--------------------------------------------------------------------------
    /**
     * Called pre delete call on DataSource.
     * Perform a cascading delete on all children.
     */
    protected void preDelete() throws DataSourceException
    {
        // perform query to get the children
        DataSource ds = getDataSource();
        Query query = new Query(new QueryCriterium(getEntityName(),
            FLD_PARENTMENUID, QueryCriterium.OP_EQUALS, getId()));
        RecordSet rs = ds.select(query);

        // iterate over the children removing them
        while (rs.next())
            rs.getCurrentObject().delete();
    }

    //--------------------------------------------------------------------------
    /**
     * Called post insert / update call on DataSource.
     * Cascading save.
     */
    protected void postSave() throws DataSourceException
    {
        // save all children
        MenuAction child;
        for(int i = 0; _childrenLoaded && i < getChildren().size(); i++)
        {
            child = (MenuAction) getChildren().elementAt(i);
            if(child != null)
            {
                // if new set keys
                if(child.getState() == DataObject.NEW)
                    child.setParentMenuId(getId());

                // save
                child.save();
            }
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Add a single child to the menu.
     * @param child, the child to add.
     */
    public void addChild(MenuAction child)
    {
        // validate
        Util.argCheckNull(child);

        // add the child
        _children.add(child);


        // ensure the list remains sorted
        sortChildren();

        // set the loaded flag
        _childrenLoaded = true;
    }

    //--------------------------------------------------------------------------
    /**
     * Add a single child to the menu, the sequence number will be changed and
     * the child added as the last item.
     * @param child, the child to add.
     */
    public void addChildAtEnd(MenuAction child)
    {
        // validate
        Util.argCheckNull(child);

        // determine sequence number
        int sequence = 1;
        if (_children.size() > 0)
            sequence = ((MenuAction) _children.lastElement()).getSequence() + 1;
        child.setSequence(sequence);

        // add child
        _children.add(child);

        // set the loaded flag
        _childrenLoaded = true;
    }

    //--------------------------------------------------------------------------
    /**
     * Remove a child.
     * @param child, the child to remove.
     * @param doDelete, if true will also delete from DB.
     */
    public MenuAction removeChild(MenuAction child, boolean doDelete)
    {
        // if it is indb, remove by key
        MenuAction rem = null;

        if (child.getState() == DataObject.IN_DB)
        {
            // iterate
            MenuAction temp;
            for (int i = 0; i < getChildren().size(); i++)
            {
                temp = (MenuAction) getChildren().elementAt(i);
                if (temp != null && temp.getState() == DataObject.IN_DB &&
                    temp.getId() == child.getId())
                    rem = (MenuAction) getChildren().remove(i);
            }
        }

        // else, try to remove by ref
        else
        {
            if (getChildren().remove(child))
                rem = child;
        }

        try
        {
            // delete
            if (doDelete && rem.getState() == DataObject.IN_DB)
                rem.delete();
        }
        catch(Exception e)
        {
            throw new RuntimeException(e.toString());
        }

        // return
        return rem;
    }

    //--------------------------------------------------------------------------
    /**
     * Query DB to get the top level MenuActions (those with no parent menu) for
     * the specified Group.
     * @param groupId, the group to get menus for, if null gets the template
     *   menus (which are defined as those without a parent group).
     * @return a vector containing the top level MenuActions
     */
    public static Vector getGroupMenus(Object groupId) throws DataSourceException
    {
        Vector v = new Vector();

        // build query
        DataSource ds = DataManager.getSystemDS();
        Query q = new Query(MenuAction.ENT_MENUACTION);

        // add criteria for null parent
        q.addQueryCriterium(new QueryCriterium(MenuAction.ENT_MENUACTION,
            MenuAction.FLD_PARENTMENUID, QueryCriterium.OP_IS_NULL, null));

        // add criteria for groupId
        if (groupId == null)
            q.addQueryCriterium(new QueryCriterium(MenuAction.ENT_MENUACTION,
                MenuAction.FLD_GROUPID, QueryCriterium.OP_IS_NULL, null));
        else
            q.addQueryCriterium(new QueryCriterium(MenuAction.ENT_MENUACTION,
                MenuAction.FLD_GROUPID, QueryCriterium.OP_EQUALS, groupId));

        // sort by sequence
        q.addSort(new Sort(MenuAction.ENT_MENUACTION, MenuAction.FLD_SEQUENCE));

        MenuAction child;
        RecordSet rs = ds.select(q);

        while (rs != null && rs.next())
        {
            // get the child
            child = (MenuAction) rs.getCurrentObject();
            v.add(child);
        }

        return v;
    }

    //--------------------------------------------------------------------------
    /**
     * Sort the list of children by sequence number. Call this if you directly
     * alter the child Vector (except with add/remove child) or alter the
     * sequence number of any child in the vector.
     */
    public void sortChildren()
    {
        Collections.sort(_children, new SequenceComparator());
    }

    //--------------------------------------------------------------------------
    /**
     * @return the a vector of the child MenuActions, loaded from DB if
     *   necessary. Guaranteed to be sorted by sequence.
     *   Note, if you modify the returned Vector or the sequence numbers of the
     *   children you must call sortChildren to ensure their order is correct.
     */
    public Vector getChildren()
    {
        // if not loaded, load
        if(!_childrenLoaded)
        {
            // must be indb
            if(getState() == DataObject.IN_DB)
            {
                // build query
                DataSource ds = DataManager.getSystemDS();
                Query q = new Query(new QueryCriterium(MenuAction.ENT_MENUACTION,
                    MenuAction.FLD_PARENTMENUID, QueryCriterium.OP_EQUALS, getId()));
                // sort by sequence
                q.addSort(new Sort(MenuAction.ENT_MENUACTION, MenuAction.FLD_SEQUENCE));

                try
                {
                    // execute query and build vector
                    MenuAction child;
                    RecordSet rs = ds.select(q);

                    while (rs != null && rs.next())
                    {
                        // get the child
                        child = (MenuAction) rs.getCurrentObject();
                        if(child != null)
                            _children.add(child);
                    }
                }
                catch(Exception e)
                {
                    throw new RuntimeException(e.toString());
                }
            }

            // set flag
            _childrenLoaded = true;
        }

        // return
        return _children;
    }

    //--------------------------------------------------------------------------
    /**
     * Recursively scan through menus to find the MenuAction with menuId.
     * @param menuId, the menu id to search for.
     */
    public MenuAction findMenu(Object menuId)
    {
        // validate
        Util.argCheckNull(menuId);

        // check this
        String strMenuId = menuId.toString();
        String thisId = this.getStringValue(MenuAction.FLD_ID);
        if(strMenuId.equals(thisId))
            return this;

        // get the children
        Vector children = getChildren();

        // search through the children
        for (int i = 0; i < children.size(); i++)
        {
            MenuAction child = (MenuAction) children.get(i);
            String childId = child.getStringValue(MenuAction.FLD_ID);
            if(childId != null && childId.equals(strMenuId))
                return child;
        }

        // not found, search recursively
        for (int i = 0; i < children.size(); i++)
        {
            MenuAction child = (MenuAction) children.get(i);
            // only search submenus and queries as currently nothing else has children
            if (child instanceof MsgServerAction || child instanceof Submenu || child instanceof QueryRecords)
            {
                MenuAction found = child.findMenu(menuId);
                if (found != null)
                    return found;
            }
        }

        // not found
        return null;
    }

    //--------------------------------------------------------------------------
    /**
     * Create an exact copy of this MenuAction and all its children, except that
     * they explicitly are not in the DB, do not have the system key set and
     * do not have their parent key or group set. The clone may then have its
     * parent set and be saved creating a new set of DB records for the cloned
     * branch.
     * Note that this will cause a recursive getChildren to ensure this
     * MenuAction is fully populated.
     * @return the clone of this branch, including clones of all children.
     */
    public MenuAction cloneBranch()
    {
        // create a copy of this MenuAction, state will be new
        MenuAction cloneMA = null;
        try
        {
            cloneMA = (MenuAction) getClass().newInstance();
        }
        catch (Exception e)
        {
            Log.error("MenuAction.CloneBranch: " + e.toString());
            return null;
        }

        // copy the values
        cloneMA.importValues(this);

        // clear the keys
        cloneMA.setId(null);
        cloneMA.setGroupId(null);
        cloneMA.setParentMenuId(null);

        // clone the children
        Vector v = getChildren();
        for (int i = 0; i < v.size(); i++)
            cloneMA.addChild(((MenuAction) v.get(i)).cloneBranch());

        // since we have explicitly set all children (even if there are zero)
        // then set the _childrenLoaded flag to avoif redundant loads
        _childrenLoaded = true;

        // return the clone
        return cloneMA;
    }

    //--------------------------------------------------------------------------
    /**
     * Serialization support.
     */
    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException
    {
        _childrenLoaded = false;
        _children = new Vector();

        // do default loading
        in.defaultReadObject();
    }
}

//==============================================================================
// end of file MenuAction.java
//==============================================================================
