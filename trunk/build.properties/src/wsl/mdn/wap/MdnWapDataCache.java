package wsl.mdn.wap;

// imports
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import wsl.fw.datasource.*;
import wsl.fw.security.*;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.mdn.dataview.*;
import wsl.mdn.guiconfig.*;
import wsl.fw.msgserver.*;

/**
 * Title:
 * Description: Cache for DataViews, QueryDobjs, DataSourceDobjs
 * Copyright:    Copyright (c)
 * Company:
 * @author
 * @version 1.0
 */
public class MdnWapDataCache extends MdnDataCache
{
    //--------------------------------------------------------------------------
    // attributes

    private Vector _nullVector = new Vector();
    private LoginSettings _ls = null;


    //--------------------------------------------------------------------------
    // construction

    /**
     * Blank ctor
     */
    public MdnWapDataCache()
    {
        super(true);
    }


    //--------------------------------------------------------------------------
    // accessors

    public static MdnWapDataCache getWapCache()
    {
        return (MdnWapDataCache)getCache();
    }


    //--------------------------------------------------------------------------
    // field exclusions

    /**
     * @return boolean true if the DataViewField is excluded for the param group
     * @param field the DataViewField
     * @param groupId
     */
    public synchronized boolean isFieldExcluded(DataViewField field, int groupId)
        throws DataSourceException
    {
        // validate
        Util.argCheckNull(field);

        // get the exclusions
        Vector ex = getFieldExclusions(groupId, field.getDataViewId());

        return isFieldExcluded(field, ex);
    }

    /**
     * @return boolean true if the DataViewField is excluded in the vector.
     * @param field the DataViewField.
     * @param ex, the exclusion vector.
     */
    public static boolean isFieldExcluded(DataViewField field, Vector ex)
    {
        // iterate exclusions and return true if found
        FieldExclusion fe;
        for(int i = 0; ex != null && i < ex.size(); i++)
        {
            // get the exclusion
            fe = (FieldExclusion)ex.elementAt(i);

            // compare, return true if found
            if(fe != null && fe.getDvFieldId() == field.getId())
                return true;
        }

        // not found, not excluded
        return false;
    }

    /**
     * Get a vector of FieldExclusions based on Group Id and DataView Id
     * @param groupId
     * @param dataViewId
     * @return Vector a Vector of FieldExclusions
     */
    public synchronized Vector getFieldExclusions(int groupId, int dataViewId) throws DataSourceException
    {
        // create the string key
        String strKey = String.valueOf(groupId) + ":" + String.valueOf(dataViewId);

        // if not in cache, select
        Vector ret = (Vector)getObject(FieldExclusion.ENT_FIELDEXCLUSION, strKey);
        if(isObjectExpired(ret, FieldExclusion.ENT_FIELDEXCLUSION, new Integer(groupId)))
        {
            // build query
            Query q = new Query(FieldExclusion.ENT_FIELDEXCLUSION);
            q.addQueryEntity(DataViewField.ENT_DVFIELD);
            q.addQueryCriterium(new QueryCriterium(FieldExclusion.ENT_FIELDEXCLUSION,
                FieldExclusion.FLD_GROUPID, QueryCriterium.OP_EQUALS,
                new Integer(groupId)));
            q.addQueryCriterium(new QueryCriterium(DataViewField.ENT_DVFIELD,
                DataViewField.FLD_DATAVIEWID, QueryCriterium.OP_EQUALS,
                new Integer(dataViewId)));

            // execute query and build Vector
            RecordSet rs = DataManager.getSystemDS().select(q);
            ret = new Vector();
            while(rs != null && rs.next())
            {
                // add the FieldExclusion to the vector
                ret.add(rs.getCurrentObject(FieldExclusion.ENT_FIELDEXCLUSION));
            }

            // put vector into hashtable
            if(doUseCache())
                setObject(FieldExclusion.ENT_FIELDEXCLUSION, ret, strKey);
        }

        // return vector
        return ret;
    }


    //--------------------------------------------------------------------------
    // User

    /**
     * @return User a cached user
     * @param userId the id of the user to get
     */
    public synchronized User getUserFromId(Object userId) throws DataSourceException
    {
        // if not in cache, select
        User ret = (User)getObject(User.ENT_USER, userId);
        if(isObjectExpired(ret, User.ENT_USER, userId))
        {
            // build query
            ret = new User();
            ret.setId(Integer.parseInt(userId.toString()));
            if(ret.load())
            {
                if(doUseCache())
                    setObject(User.ENT_USER, ret, userId);
            }
            else
                ret = null;
        }
        return ret;
    }

    /**
     * @return User a cached user
     * @param userName the name of the user to get
     */
    public synchronized User getUser(String userName) throws DataSourceException
    {
        // get the user enum
        User ret = null;
        User temp;
        Enumeration userEnum = getEntityTable(User.ENT_USER);
        while(ret == null && userEnum != null && userEnum.hasMoreElements())
        {
             temp = (User)userEnum.nextElement();
             if(temp != null && temp.getName().equals(userName))
                ret = temp;
        }

        // if user not found, load
        if(ret == null)
        {
            ret = new User();
            ret.setName(userName);
            if(ret.load())
            {
                // update cache and local version
                if(doUseCache())
                {
                    updateLocalVersion(User.ENT_USER, ret.getId());
                    setObject(User.ENT_USER, ret, ret.getId());
                }
            }
            else
            {
                ret = null;
            }
        }
        else
        {
            // delegate to get with userId
            Object userId = ret.getId();
            ret = getUserFromId(userId);
        }
        return ret;
    }


    //--------------------------------------------------------------------------
    // Group Membership

   /**
     * Get a vector of FieldExclusions based on Group Id and DataView Id
     * @param groupId
     * @param dataViewId
     * @return Vector a Vector of FieldExclusions
     */
    public synchronized Vector getGroupMemberships(Object userId) throws DataSourceException
    {
        // get the user
        User user = getUserFromId(userId);
        return (user != null)? user.getGroupMemberships(): _nullVector;
    }


    //--------------------------------------------------------------------------
    // LoginSettings

    /**
     * @return LoginSettings
     */
    public synchronized LoginSettings getLoginSettings() throws DataSourceException
    {
        // if not in cache, select
        LoginSettings ret = (LoginSettings)getFirstObject(LoginSettings.ENT_LOGINSETTINGS);
        if(ret == null)
            ret = LoginSettings.getLoginSettings();

        // get the key
        Object key = ret.getId();

        // check expiry
        if(isObjectExpired(ret, LoginSettings.ENT_LOGINSETTINGS, key))
        {
            // get the login settings
            ret = LoginSettings.getLoginSettings();

            // set into cache
            if(doUseCache())
                setObject(LoginSettings.ENT_LOGINSETTINGS, ret, key);
        }
        return ret;
    }


    //--------------------------------------------------------------------------
    // Group Menus

    public synchronized Vector getGroupMenus(Object groupId) throws DataSourceException
    {
        Util.argCheckNull(groupId);

        // look in cache
        Vector ret = (Vector)getObject(MenuAction.ENT_MENUACTION, groupId);

        // if expired, load
        if(isObjectExpired(ret, MenuAction.ENT_MENUACTION, groupId))
        {
            // build query
            DataSource ds = DataManager.getSystemDS();
            Query q = new Query(MenuAction.ENT_MENUACTION);

            // add criteria for null parent
            q.addQueryCriterium(new QueryCriterium(MenuAction.ENT_MENUACTION,
                MenuAction.FLD_PARENTMENUID, QueryCriterium.OP_IS_NULL, null));

            // add criteria for groupId
            q.addQueryCriterium(new QueryCriterium(MenuAction.ENT_MENUACTION,
                MenuAction.FLD_GROUPID, QueryCriterium.OP_EQUALS, groupId));

            // sort by sequence
            q.addSort(new Sort(MenuAction.ENT_MENUACTION, MenuAction.FLD_SEQUENCE));
            RecordSet rs = ds.select(q);
            if(rs != null && rs.next())
            {
                ret = rs.getRows();

                // set into cache
                if(doUseCache())
                    setObject(MenuAction.ENT_MENUACTION, ret, groupId);
            }
        }
        return ret;
    }
}