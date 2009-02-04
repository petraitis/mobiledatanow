package wsl.fw.msgserver;

// imports
import wsl.fw.datasource.*;
import wsl.fw.security.User;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c)
 * Company:
 * @author
 * @version 1.0
 */

public class MessageServerProfile extends DataObject
{
    //--------------------------------------------------------------------------
    // constants

    public final static String ENT_MSGSVR_PROFILE   = "TBL_MSGSVR_PROFILE";
    public final static String FLD_ID               = "FLD_ID";
    public final static String FLD_USERID           = "FLD_USERID";
    public final static String FLD_MSG_SERVERID     = "FLD_MSG_SERVERID";
    public final static String FLD_PROFILE_NAME     = "FLD_PROFILE_NAME";
    public final static String FLD_PASSWORD         = "FLD_PASSWORD";
    public final static String FLD_DESCRIPTION      = "FLD_DESCRIPTION";
    public final static String FLD_CLASS            = JdbcDataSource.CLASS_COLUMN_NAME;


    //--------------------------------------------------------------------------
    // attributes

    private transient User _user = null;

    //--------------------------------------------------------------------------
    // construction

    /**
     * Blank ctor
     */
    public MessageServerProfile()
    {
    }


    //--------------------------------------------------------------------------
    // persistence

    /**
     * Static factory method to create the entity to be used by this dataobject
     * and any subclasses. This is called by the DataManager's factory when
     * creating a MSGSVR_PROFILE entity.
     * @return the created entity.
     */
    public static Entity createEntity()
    {
        // create the entity
        Entity ent = new EntityImpl(ENT_MSGSVR_PROFILE, null);

        // add the key generator for the system id
        ent.addKeyGeneratorData(new DefaultKeyGeneratorData(ENT_MSGSVR_PROFILE, FLD_ID));

        // create the fields and add them to the entity
        ent.addField(new FieldImpl(FLD_ID, Field.FT_INTEGER, Field.FF_UNIQUE_KEY | Field.FF_SYSTEM_KEY));
        ent.addField(new FieldImpl(FLD_USERID, Field.FT_INTEGER));
        ent.addField(new FieldImpl(FLD_MSG_SERVERID, Field.FT_INTEGER));
        ent.addField(new FieldImpl(FLD_PROFILE_NAME, Field.FT_STRING, Field.FF_NAMING));
        ent.addField(new FieldImpl(FLD_DESCRIPTION, Field.FT_STRING));
        ent.addField(new FieldImpl(FLD_PASSWORD, Field.FT_STRING));
        ent.addField(new FieldImpl(FLD_CLASS, Field.FT_STRING)); // polymorph support

        // create the joins and add them to the entity
        ent.addJoin(new JoinImpl(ENT_MSGSVR_PROFILE, FLD_MSG_SERVERID,
            MessageServer.ENT_MSGSERVER, MessageServer.FLD_ID));
        ent.addJoin(new JoinImpl(ENT_MSGSVR_PROFILE, FLD_USERID,
            User.ENT_USER, User.FLD_ID));

        // return the entity
        return ent;
    }

    //--------------------------------------------------------------------------
    /**
     * @return String the name of the entity that defines this DataObject
     */
    public String getEntityName()
    {
        return ENT_MSGSVR_PROFILE;
    }


    //--------------------------------------------------------------------------
    // accessors

    /**
     * @return int the id
     */
    public int getId()
    {
        return getIntValue(FLD_ID);
    }

    /**
     * Set the id
     * @param id, the id to set
     */
    public void setId(Object id)
    {
        setValue(FLD_ID, id);
    }

    /**
     * @return int the user id
     */
    public int getUserId()
    {
        return getIntValue(FLD_USERID);
    }

    /**
     * Set the user id
     * @param id, the id to set
     */
    public void setUserId(Object id)
    {
        setValue(FLD_USERID, id);
    }

    /**
     * @return int the msg server id
     */
    public int getMsgServerId()
    {
        return getIntValue(FLD_MSG_SERVERID);
    }

    /**
     * Set the msg server id
     * @param id, the id to set
     */
    public void setMsgServerId(Object id)
    {
        setValue(FLD_MSG_SERVERID, id);
    }

    /**
     * Returns the description
     * @return String
     */
    public String getDescription()
    {
        return getStringValue(FLD_DESCRIPTION);
    }

    /**
     * Sets the entity description
     * @param name
     * @return void
     */
    public void setDescription(String name)
    {
        setValue(FLD_DESCRIPTION, name);
    }

    /**
     * Returns the profile name
     * @return String
     */
    public String getProfileName()
    {
        return getStringValue(FLD_PROFILE_NAME);
    }

    /**
     * Sets the profile name
     * @param name
     * @return void
     */
    public void setProfileName(String name)
    {
        setValue(FLD_PROFILE_NAME, name);
    }

    /**
     * Returns the password
     * @return String
     */
    public String getPassword()
    {
        return getStringValue(FLD_PASSWORD);
    }

    /**
     * Sets the password
     * @param pw
     * @return void
     */
    public void setPassword(String pw)
    {
        setValue(FLD_PASSWORD, pw);
    }

    /**
     * Returns the user
     * @return String
     */
    public User getUser()
    {
        return _user;
    }

    /**
     * Sets the user
     * @param user
     * @return void
     */
    public void setUser(User user)
    {
        _user = user;
    }


    //--------------------------------------------------------------------------
    // toString()

    public String toString()
    {
        String str = getProfileName();
        return (_user != null)? str + ": " + _user.toString(): str;
    }
}