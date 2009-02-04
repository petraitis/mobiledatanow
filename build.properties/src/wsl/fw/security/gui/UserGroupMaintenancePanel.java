//==============================================================================
// UserGroupMaintenancePanel.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.security.gui;

import wsl.fw.security.User;
import wsl.fw.security.Group;
import wsl.fw.security.Feature;
import wsl.fw.gui.GuiManager;
import wsl.fw.gui.WslTabbedPanel;
import wsl.fw.datasource.DataListenerData;
import wsl.fw.datasource.DataChangeListener;
import wsl.fw.datasource.DataManager;
import wsl.fw.datasource.DataSource;
import wsl.fw.datasource.DataSourceException;
import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.Query;
import wsl.fw.datasource.RecordSet;
import wsl.fw.datasource.DataChangeNotification;
import wsl.fw.util.Log;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import wsl.fw.resource.ResId;

//------------------------------------------------------------------------------
/**
 * Maintenance screen for user/group security.
 * Manages creating and editing users and groups, the relationship between them
 * and the permissions available to the groups.
 * Note that applications that use this screen should require that the user have
 * The privilege FwFeatures.USERGROUP_ADMIN.
 */
public class UserGroupMaintenancePanel
    extends WslTabbedPanel
    implements DataChangeListener
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/security/gui/UserGroupMaintenancePanel.java $ ";

    // resources
    public static final ResId ERR_LOADING  = new ResId("UserGroupMaintenancePanel.error.Loading");
    public static final ResId ERR_UNEXPECTED_TYPE  = new ResId("UserGroupMaintenancePanel.error.UnexpectedType");
    public static final ResId TITLE_SELECT_USER  = new ResId("UserGroupMaintenancePanel.title.SelectUser");
    public static final ResId TITLE_SELECT_PRIVILEGE  = new ResId("UserGroupMaintenancePanel.title.SelectPrivilege");
    public static final ResId TITLE_SELECT_GROUP  = new ResId("UserGroupMaintenancePanel.title.SelectGroup");

    private Hashtable _users    = new Hashtable();
    private Hashtable _groups   = new Hashtable();
    private Hashtable _features = new Hashtable();

    //--------------------------------------------------------------------------
    /**
     * Constructor.
     */
    public UserGroupMaintenancePanel()
    {
        super(VERTICAL);

        // add the data change listeners for user and group
        DataListenerData dldUser = new DataListenerData(this, User.ENT_USER, null);
        DataListenerData dldGroup = new DataListenerData(this, Group.ENT_GROUP, null);
        DataManager.addDataChangeListener(dldGroup);
        DataManager.addDataChangeListener(dldUser);

        // load the set of users, groups and features
        loadData();

        // create the tab panels for user and group
        UserTabPane  userPanel  = new UserTabPane(this);
        GroupTabPane groupPanel = new GroupTabPane(this);

        // add the panels
        addTabPanel(userPanel);
        addTabPanel(groupPanel);
    }

    //--------------------------------------------------------------------------
    /**
     * Load the data, this data will be shared by
     */
    private void loadData()
    {
        DataSource ds;
        Query      query;
        RecordSet  rs;

        try
        {
            // load the Users.
            ds    = DataManager.getDataSource(User.ENT_USER);
            query = new Query(User.ENT_USER);
            rs    = ds.select(query);
            while (rs.next())
            {
                User user = (User) rs.getCurrentObject();
                _users.put(user.getId(), user);
            }

            // load the Groups.
            ds    = DataManager.getDataSource(Group.ENT_GROUP);
            query = new Query(Group.ENT_GROUP);
            rs    = ds.select(query);
            while (rs.next())
            {
                Group group = (Group) rs.getCurrentObject();
                _groups.put(group.getId(), group);
            }

            // load the Features.
            ds    = DataManager.getDataSource(Feature.ENT_FEATURE);
            query = new Query(Feature.ENT_FEATURE);
            rs    = ds.select(query);
            while (rs.next())
            {
                Feature feature = (Feature) rs.getCurrentObject();
                _features.put(feature.getName(), feature);
            }
        }
        catch (DataSourceException e)
        {
            GuiManager.showErrorDialog(this, ERR_LOADING.getText(), e);
            Log.error(ERR_LOADING.getText(), e);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Get the set of users, does a clone to ensure integrity, do not modify
     * the hashtable. Can modify a contained object. Use when performing an
     * iteration.
     * @return the set of users.
     */
    public Hashtable getUsers()
    {
        return (Hashtable) _users.clone();
    }

    //--------------------------------------------------------------------------
    /**
     * Get a specific User.
     * @param key, an object identifying the user.
     * @retunr the User or null if not found.
     */
    public User getUser(Object key)
    {
        return (User) _users.get(key);
    }

    //--------------------------------------------------------------------------
    /**
     * Get the set of groups, does a clone to ensure integrity, do not modify
     * the hashtable. Can modify a contained object. Use when performing an
     * iteration.
     * @return the set of groups.
     */
    public Hashtable getGroups()
    {
        return (Hashtable) _groups.clone();
    }

    //--------------------------------------------------------------------------
    /**
     * Get a specific Group.
     * @param key, an object identifying the group.
     * @retunr the User or null if not found.
     */
    public Group getGroup(Object key)
    {
        return (Group) _groups.get(key);
    }

    //--------------------------------------------------------------------------
    /**
     * Get the set of features do not modify the hashtable or contained objects.
     * Use when performing an iteration.
     * @return the set of features.
     */
    public Hashtable getFeatures()
    {
        return _features;
    }

    //--------------------------------------------------------------------------
    /**
     * Get a specific Feature.
     * @param key, an object identifying the feature.
     * @retunr the User or null if not found.
     */
    public Feature getFeature(Object key)
    {
        return (Feature) _features.get(key);
    }

    //--------------------------------------------------------------------------
    /**
     * Notification of DataObject change event
     * @param DataChangeNotification contains the data regarding data change
     */
    public void onDataChanged(DataChangeNotification notification)
    {
        // data has changed update the user or group table as required.
        // get the data object
        DataObject dobj = notification.getDataObject();
        Object     key;
        Hashtable  ht;

        // choose the appropriate hashtable and key
        if (dobj instanceof User)
        {
            ht = _users;
            key = ((User) dobj).getId();
        }
        else if (dobj instanceof Group)
        {
            ht = _groups;
            key = ((Group) dobj).getId();
        }
        else
        {
            // should not get here
            Log.error(ERR_UNEXPECTED_TYPE.getText()
            + " " + dobj.getClass().getName());
            return;
        }

        // choose action depending on type of data change
        switch (notification.getChangeType())
        {
            case DataChangeNotification.DELETE :
                // deleted, remove from set
                ht.remove(key);
                break;

            case DataChangeNotification.INSERT :
            case DataChangeNotification.UPDATE :
                // added or updated, put in set
                ht.put(key, dobj);
                break;
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Notification function called when we are closed.
     */
    public void onClosePanel()
    {
        // remove the data change listeners
        DataManager.removeAllDataChangeListeners(this);
        Log.debug("UserGroupMaintenancePanel.onClosePanel");

        super.onClosePanel();
    }

    //--------------------------------------------------------------------------
    /**
     * Select a user from the set of users.
     * @param exclude, a vector of DataObjects to exclude from the select list.
     *   maye be null or empty.
     * @return the selected user or null if no selection.
     */
    public User selectUser(Vector exclude)
    {
        return (User) selectInternal(_users, exclude, TITLE_SELECT_USER.getText());
    }

    //--------------------------------------------------------------------------
    /**
     * Select a Feature from the set of features.
     * @param exclude, a vector of DataObjects to exclude from the select list.
     *   maye be null or empty.
     * @return the selected Feature or null if no selection.
     */
    public Feature selectFeature(Vector exclude)
    {
        return (Feature) selectInternal(_features, exclude, TITLE_SELECT_PRIVILEGE.getText());
    }

    //--------------------------------------------------------------------------
    /**
     * Select a Group from the set of groups.
     * @param exclude, a vector of DataObjects to exclude from the select list.
     *   maye be null or empty.
     * @return the selected Group or null if no selection.
     */
    public Group selectGroup(Vector exclude)
    {
        return (Group) selectInternal(_groups, exclude, TITLE_SELECT_GROUP.getText());
    }

    //--------------------------------------------------------------------------
    /**
     * Internal implementation used by the above select... functions to
     * get a vector or data objects, remove the exclude set and then select.
     * @param src, the soutce set of data objects from which to select.
     * @param exclude, the set of data objects (which may be in src) to be
     *   excluded from the selection.
     * @param title, the title to be displayed inthe select window.
     * @return the selected data object or null if no selection.
     */
    protected DataObject selectInternal(Hashtable src, Vector exclude,
        String title)
    {
        // make a vector from src
        Enumeration srcEnum = src.elements();
        Vector vSrc      = new Vector();
        while (srcEnum.hasMoreElements())
            vSrc.add(srcEnum.nextElement());

        // remove the excluded objects
        if (exclude != null)
            for (int i = 0; i < exclude.size(); i++)
                vSrc.remove(exclude.get(i));

        // select the object
        return GuiManager.selectDataObject(getFrameParent(), title, vSrc);
    }
}

//==============================================================================
// end of file UserGroupMaintenancePanel.java
//==============================================================================
