//==============================================================================
// User.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.security;

import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.datasource.*;

import java.util.Vector;
import java.util.Set;
import java.util.HashSet;

import wsl.fw.resource.ResId;
import wsl.fw.msgserver.MessageServerProfile;

//------------------------------------------------------------------------------
/**
 * Class to represent a users. Users are members of groups.
 */
public class User extends DataObject
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/security/User.java $ ";

    // resources
    public static final ResId ERR_QUERY  = new ResId("User.error.Query");
    public static final ResId ERR_INCORRECT_PASSWORD  = new ResId("User.error.IncorrectPassword");
    public static final ResId ERR_USER_NOT_FOUND1  = new ResId("User.error.UserNotFound1");
    public static final ResId ERR_USER_NOT_FOUND2  = new ResId("User.error.UserNotFound2");
    public static final ResId LOG_SELECT_ERROR1  = new ResId("User.log.SelectError1");
    public static final ResId LOG_SELECT_ERROR2  = new ResId("User.log.SelectError2");
    public static final ResId EXCEPTION_AUTHENTICATION_FAILURE  = new ResId("User.exception.AuthenticationFailure");
    public static final ResId EXCEPTION_PRIVILEGE1  = new ResId("User.exception.Privilege1");
    public static final ResId EXCEPTION_PRIVILEGE2  = new ResId("User.exception.Privilege2");

    // the entity name
    public final static String ENT_USER = "FW_USER";

    // field names
    //User
    public final static String FLD_ID       		= "FLD_ID";
    public final static String FLD_NAME     		= "FLD_NAME";
    public final static String FLD_PASSWORD 		= "FLD_PASSWORD";
    public final static String FLD_FIRST_NAME     	= "FLD_FIRST_NAME";
    public final static String FLD_LAST_NAME     	= "FLD_LAST_NAME";
    public final static String FLD_GROUP_ID  		= "FLD_GROUP_ID";
    public final static String FLD_NOTES  			= "FLD_NOTES";

    //Msg Detailes
//    public final static String FLD_CUSTOM_FLD_ID  	= "FLD_CUSTOM_FLD_ID";
    public final static String FLD_EMAIL  			= "FLD_EMAIL";
    public final static String FLD_MOBILE  			= "FLD_MOBILE";
    
    public final static String FLD_DEL_STATUS  		= "FLD_DEL_STATUS";
    public final static String FLD_PRIVILEGE  		= "FLD_PRIVILEGE";
    public final static String FLD_LICENSE_TYPE  	= "FLD_LICENSE_TYPE";
    
    public final static String PRIVILEGE_ADMIN	  	= "ADMIN";
    public final static String PRIVILEGE_USER  		= "USER";

    public final static String LICENSE_TYPE_PERPETUAL	  	= "PERPETUAL";
    public final static String LICENSE_TYPE_ANNUAL  		= "ANNUAL";    
    
    // transient cache of users feature set
    private transient Set _featureSet = null;

    // transient vector of group memberships
    private transient Vector _groupMems = null;
    private transient Vector _groups = null;


    //--------------------------------------------------------------------------
    /**
     * Default constructor. Intended for use by the datasource when creating
     * instances. Since a User is invalid if it is not correctly initialized
     * ensure that setName is called when using this constructor/
     */
    public User()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Constructor, set the name, password and details of the user.
     * @param userName, the name of this user, may not be null or empty.
     * @param password, the password of the user.
     * @param details, extra user details.
     * @throws IllegalArgumentException if the parameters are ivalid.
     */
    public User(String userName, String password, String details)
        throws IllegalArgumentException
    {
        // save user name, password and details
        setName(userName);
        setPassword(password);
        //setDetails(details);
    }

    //--------------------------------------------------------------------------
    /**
     * Constructor that takes a SecurityId. This facilitates creating Users
     * that use the encoded passwords provided by SecurityId.
     * @param securityId, a SecurityId that specifies username and password,
     *   may not be null.
     * @param details, extra user details.
     * @throws IllegalArgumentException if the parameters are ivalid.
     */
    public User(SecurityId securityId, String details)
    {
        setSecurityId(securityId);
        //setDetails(details);
    }

    //--------------------------------------------------------------------------
    /**
     * Static factory method to create the entity to be used by this dataobject
     * and any subclasses. This is called by the DataManager's factory when
     * creating a USER entity.
     * @return the created entity.
     */
    public static Entity createEntity()
    {
        // create the user entity
        Entity userEntity = new EntityImpl(ENT_USER, User.class);

        // add the key generator for the system id
        userEntity.addKeyGeneratorData(new DefaultKeyGeneratorData(ENT_USER, FLD_ID));

        // create the fields and add them to the entity
        //userEntity.addField(new FieldImpl(FLD_ID, Field.FT_INTEGER, Field.FF_SYSTEM_KEY));
        userEntity.addField(new FieldImpl(FLD_ID, Field.FT_INTEGER, Field.FF_UNIQUE_KEY | Field.FF_SYSTEM_KEY));
        //userEntity.addField(new FieldImpl(FLD_NAME, Field.FT_STRING, Field.FF_UNIQUE_KEY | Field.FF_NAMING, 50));
        userEntity.addField(new FieldImpl(FLD_NAME, Field.FT_STRING, Field.FF_NONE, 50));
        userEntity.addField(new FieldImpl(FLD_PASSWORD, Field.FT_STRING, Field.FF_NONE, 50));
        userEntity.addField(new FieldImpl(FLD_FIRST_NAME, Field.FT_STRING, Field.FF_NONE, 200));
        userEntity.addField(new FieldImpl(FLD_LAST_NAME, Field.FT_STRING, Field.FF_NONE, 200));
        userEntity.addField(new FieldImpl(FLD_NOTES, Field.FT_STRING, Field.FF_NONE, 200));
        userEntity.addField(new FieldImpl(FLD_GROUP_ID, Field.FT_INTEGER, Field.FF_NONE));
        
        userEntity.addField(new FieldImpl(FLD_EMAIL, Field.FT_STRING, Field.FF_NONE, 200));
        userEntity.addField(new FieldImpl(FLD_MOBILE, Field.FT_STRING, Field.FF_NONE, 200));
        
        userEntity.addField(new FieldImpl(FLD_DEL_STATUS, Field.FT_STRING, Field.FF_NONE, 200));
        userEntity.addField(new FieldImpl(FLD_PRIVILEGE, Field.FT_STRING, Field.FF_NONE, 200));
        userEntity.addField(new FieldImpl(FLD_LICENSE_TYPE, Field.FT_STRING, Field.FF_NONE, 200));
        // return the entity
        return userEntity;
    }

    //--------------------------------------------------------------------------
    /**
     * Return the Entity name.
     * @return the entity name.
     */
    public String getEntityName()
    {
        return ENT_USER;
    }

    //--------------------------------------------------------------------------
    /**
     * @return the ID
     */
    public int getId()
    {
        return getIntValue(FLD_ID);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the ID.
     */
    public void setId(int id)
    {
        setValue(FLD_ID, id);
    }
    //--------------------------------------------------------------------------
    /**
     * Get the name of the user.
     * @return the name of the user.
     */
    public String getName()
    {
        return getStringValue(FLD_NAME);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the user name.
     * @param userName, the name of the user.
     * @throws IllegalArgumentException if the user name is null or empty.
     */
    public void setName(String userName)
    {
        // check parameter, null or empty user names are not permitted
        //Util.argCheckEmpty(userName);
        setValue(FLD_NAME, userName);
    }

    //--------------------------------------------------------------------------
    /**
     * Get the user's password.
     * @return the password.
     */
    public String getPassword()
    {
        return getStringValue(FLD_PASSWORD);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the user's password.
     * @param password, the password.
     */
    public void setPassword(String password)
    {
        setValue(FLD_PASSWORD, password);
    }

    //--------------------------------------------------------------------------
    /**
     * Get the user's Notes.
     * @return the Notes.
     */
    public String getNotes()
    {
        return getStringValue(FLD_NOTES);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the user's Notes.
     * @param details, the Notes.
     */
    public void setNotes(String notes)
    {
        setValue(FLD_NOTES, notes);
    }

    //--------------------------------------------------------------------------
    /**
     * Get the user's FirstName.
     * @return the firstName.
     */
    public String getFirstName()
    {
        return getStringValue(FLD_FIRST_NAME);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the user's FirstName.
     * @param settings, the FirstName.
     */
    public void setFirstName(String firstName)
    {
        setValue(FLD_FIRST_NAME, firstName);
    }
    //--------------------------------------------------------------------------
    /**
     * Get the user's lastName.
     * @return the lastName.
     */
    public String getLastName()
    {
        return getStringValue(FLD_LAST_NAME);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the user's LastName.
     * @param settings, the LastName.
     */
    public void setLastName(String lastName)
    {
        setValue(FLD_LAST_NAME, lastName);
    }

    //--------------------------------------------------------------------------
    /**
     * Get a SecurityId constructed from the username and password.
     * @return the SecurityId
     */
    public SecurityId getSecurityId()
    {
        // construct and return a SecurityId, does not re-encode the
        // password as this object shoulf already be storing it in encodes form
        return new SecurityId(getName(), getPassword(), false);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the username and password from a SecurityId. This facilitates
     * creating Users that use the encoded passwords provided by SecurityId.
     * @param securityId, contains the username and password.
     * @throws IllegalArgumentException if the SecurityId is null.
     */
    public void setSecurityId(SecurityId securityId)
    {
        // check the parameter
        Util.argCheckNull(securityId);

        setName(securityId.getName());
        setPassword(securityId.getPassword());
    }

    //--------------------------------------------------------------------------
    /**
     * Check if the user is validated by the SecurityId, i.e. the usernames and
     * passwords match.
     * @param securityId, the SecurityId to test against.
     * @return true if the security id matches this user.
     */
    public boolean identityCheck(SecurityId securityId)
    {
        return getName().equals(securityId.getName())
            && getPassword().equals(securityId.getPassword());
    }

    //--------------------------------------------------------------------------
    /**
     * Get the set of Features this user has privilege to use.
     * @return a Set containing the allowed Features. May be empty if the user
     *   has no Feature privileges or if there is a DataSource error.
     */
    public Set getFeatureSet()
    {
        // delegate to static version
        return getFeatureSet(getName());
    }

    //--------------------------------------------------------------------------
    /**
     * Get the set of Features the named user has privilege to use.
     * @param username, the user name, not null or empty.
     * @return a Set containing the allowed Features. May be empty if the user
     *   has no Feature privileges or if there is a DataSource error.
     * @throws IllegalArgumentException if username is null or empty.
     */
    public static Set getFeatureSet(String username)
    {
        // check param
        Util.argCheckEmpty(username);

        // create the empty set
        Set featureSet = new HashSet();

        try
        {
            // create a query to return all the features for this user
            Query query = new Query();
            query.addQueryEntity(User.ENT_USER);
            query.addQueryEntity(GroupMembership.ENT_GROUPMEMBERSHIP);
            query.addQueryEntity(Group.ENT_GROUP);
            query.addQueryEntity(Privilege.ENT_PRIVILEGE);
            query.addQueryEntity(Feature.ENT_FEATURE);
            query.addQueryCriterium(new QueryCriterium(User.ENT_USER,
                User.FLD_NAME, QueryCriterium.OP_EQUALS, username));

            // perform the query
            DataSource ds = DataManager.getDataSource(User.ENT_USER);
            RecordSet rs = ds.select(query);

            // get the Features out of the result set and iterate adding the query
            // results to the set
            while (rs.next())
            {
                Feature f = (Feature) rs.getCurrentObject(Feature.ENT_FEATURE);
                featureSet.add(f);
            }
        }
        catch (Exception e)
        {
            // exception from select, log it, an empty set will be returned
            Log.error(ERR_QUERY.getText() + " "
                + e.toString());
        }

        // return the set
        return featureSet;
    }

    //--------------------------------------------------------------------------
    /**
     * Perform a user login. This looks the user up in the datasource and then
     * validates it against the SecurityId.
     * @param securityId, the SecurityId to test against.
     * @return the user data object.
     * @throws SecurityException if the user is not found or the password is
     *   invalid.
     */
    public static UserWrapper login(SecurityId securityId) throws SecurityException
    {
        // check param
        Util.argCheckNull(securityId);

        // perform a query to find the user
        // get the data source for querying
        DataSource ds = DataManager.getDataSource(User.ENT_USER);

        // create the query for user on username
        Query query = new Query(new QueryCriterium(User.ENT_USER, User.FLD_NAME,
            QueryCriterium.OP_EQUALS, securityId.getName()));

        try
        {
            // get the results
            RecordSet rs = ds.select(query);

            // get the user out of the result set
            if (rs.next())
            {
                User user = (User) rs.getCurrentObject();

                // test the user against the security id
                if (user.identityCheck(securityId)){
                	if (user.getPrivilege().equalsIgnoreCase(PRIVILEGE_ADMIN))
                		return new UserWrapper(user, null, null);
                	else{
                		String errorMsg = EXCEPTION_PRIVILEGE1.getText() + " [" + securityId.getName() + "]" + " " ;
                		String errorMsg2 = EXCEPTION_PRIVILEGE2.getText() + " " + PRIVILEGE_ADMIN;
                		Log.log(errorMsg + errorMsg2);
                		return new UserWrapper(null, errorMsg, errorMsg2);
                	}	
                		
                }else{
                	String errorMsg = ERR_INCORRECT_PASSWORD.getText();
                	String errorMsg2 = " ["	+ securityId.getName() + "]";
                	Log.log(errorMsg + errorMsg2);
                	return new UserWrapper(null, errorMsg, errorMsg2);
                }
            }
            else{
                String errorMsg = ERR_USER_NOT_FOUND1.getText() + " ["
                	+ securityId.getName() + "] " + ERR_USER_NOT_FOUND2.getText();
            	Log.log(errorMsg);
            	return new UserWrapper(null, errorMsg, null);
            }
        }
        catch (Exception e)
        {
            // got a query exception, rethrow as a SecurityException
        	String errorMsg = LOG_SELECT_ERROR1.getText() + " [" + e.toString() + "] ";
        	String errorMsg2 = LOG_SELECT_ERROR2.getText() + " [" + securityId.getName() + "]";
        	Log.log(errorMsg + errorMsg2);
        	return new UserWrapper(null, errorMsg, errorMsg2);
            //throw new wsl.fw.security.SecurityException(e.toString());
        }

        // failure, no user or incorrect password, throw a security exception
        //throw new wsl.fw.security.SecurityException(EXCEPTION_AUTHENTICATION_FAILURE.getText());
    }

    public static UserWrapper getLoginUserByName(String userName) throws SecurityException
    {
        // check param
        Util.argCheckNull(userName);

        // perform a query to find the user
        // get the data source for querying
        DataSource ds = DataManager.getDataSource(User.ENT_USER);

        // create the query for user on username
        Query query = new Query(new QueryCriterium(User.ENT_USER, User.FLD_NAME,
            QueryCriterium.OP_EQUALS, userName));

        try
        {
            // get the results
            RecordSet rs = ds.select(query);

            // get the user out of the result set
            if (rs.next())
            {
                User user = (User) rs.getCurrentObject();

                // test the user against the security id
                //if (user.identityCheck(securityId)){
                	if (user.getPrivilege().equalsIgnoreCase(PRIVILEGE_ADMIN))
                		return new UserWrapper(user, null, null);
                	else{
                		String errorMsg = EXCEPTION_PRIVILEGE1.getText() + " [" + userName + "]" + " " ;
                		String errorMsg2 = EXCEPTION_PRIVILEGE2.getText() + " " + PRIVILEGE_ADMIN;
                		Log.log(errorMsg + errorMsg2);
                		return new UserWrapper(null, errorMsg, errorMsg2);
                	}	
                /*		
                }else{
                	String errorMsg = ERR_INCORRECT_PASSWORD.getText();
                	String errorMsg2 = " ["	+ securityId.getName() + "]";
                	Log.log(errorMsg + errorMsg2);
                	return new UserWrapper(null, errorMsg, errorMsg2);
                }*/
            }
            else{
                String errorMsg = ERR_USER_NOT_FOUND1.getText() + " ["
                	+ userName + "] " + ERR_USER_NOT_FOUND2.getText();
            	Log.log(errorMsg);
            	return new UserWrapper(null, errorMsg, null);
            }
        }
        catch (Exception e)
        {
            // got a query exception, rethrow as a SecurityException
        	String errorMsg = LOG_SELECT_ERROR1.getText() + " [" + e.toString() + "] ";
        	String errorMsg2 = LOG_SELECT_ERROR2.getText() + " [" + userName + "]";
        	Log.log(errorMsg + errorMsg2);
        	return new UserWrapper(null, errorMsg, errorMsg2);
            //throw new wsl.fw.security.SecurityException(e.toString());
        }

        // failure, no user or incorrect password, throw a security exception
        //throw new wsl.fw.security.SecurityException(EXCEPTION_AUTHENTICATION_FAILURE.getText());
    }    
    
    //--------------------------------------------------------------------------
    /**
     * Delete this user from the DataSource. Overidden to remove all the
     * dependant group memberships.
     * @throws DataSourceException if there is an error from the DataSource.
     */
    protected void preDelete() throws DataSourceException
    {
        // perform query to get the memberships
        DataSource sysDs = DataManager.getSystemDS();
        Query q = new Query(new QueryCriterium(GroupMembership.ENT_GROUPMEMBERSHIP,
            GroupMembership.FLD_USERID, QueryCriterium.OP_EQUALS, getId()));
        RecordSet rs = sysDs.select(q);

        // iterate over the memberships removing them
        while (rs.next())
            rs.getCurrentObject().delete();

        // select all profiles
        q = new Query(MessageServerProfile.ENT_MSGSVR_PROFILE);
        q.addQueryCriterium(new QueryCriterium(MessageServerProfile.ENT_MSGSVR_PROFILE,
            MessageServerProfile.FLD_USERID, QueryCriterium.OP_EQUALS,
            getId()));
        rs = sysDs.select(q);

        // delete all profiles
        while (rs.next())
            rs.getCurrentObject().delete();
    }


    //--------------------------------------------------------------------------
    /**
     * Check if this user has privilege to access the specified feature.
     * @param feature, the feature to check access for.
     * @return true if the user is allowed access.
     */
    public boolean hasPrivilege(Feature feature)
    {
        Util.argCheckNull(feature);

        // if no feature set loaded then load it now
        if (_featureSet == null)
            _featureSet = getFeatureSet(getName());

        // if still no feature set then fail, else return true if the set
        // contains the specified feature
        if (_featureSet == null)
            return false;
        else
            return _featureSet.contains(feature);
    }

    //--------------------------------------------------------------------------
    /**
     * Check if this user has privilege to access the specified feature.
     * @param feature, the feature to check access for.
     * @throws SecurityException if the user does not have privilege.
     */
    public void checkPrivilege(Feature feature) throws SecurityException
    {
        if (!hasPrivilege(feature))
            throw new wsl.fw.security.SecurityException(EXCEPTION_PRIVILEGE1.getText() + getName()
                + " " + EXCEPTION_PRIVILEGE2.getText() + " " + feature.getName());
    }


    //--------------------------------------------------------------------------
    // GroupMembership

    public Vector getGroupMemberships() throws DataSourceException
    {
        // if null create
        if(_groupMems == null)
        {
            // create vector
            _groupMems = new Vector();

            // build query
            Query q = new Query(new QueryCriterium(GroupMembership.ENT_GROUPMEMBERSHIP,
                GroupMembership.FLD_USERID, QueryCriterium.OP_EQUALS,
                this.getId()));

            // execute query and build Vector
            RecordSet rs = DataManager.getSystemDS().select(q);
            if(rs != null && rs.next())
                _groupMems = rs.getRows();
        }
        return _groupMems;
    }
    
    @SuppressWarnings("unchecked")
	public Vector getGroups() throws DataSourceException
    {
        // if null create
        if(_groups == null)
        {
            // create vector
            _groups = new Vector();
            DataSource ds = DataManager.getSystemDS();

            _groupMems = getGroupMemberships();
            if (_groupMems != null){
    	    	
    	    	for (GroupMembership groupMem : (Vector<GroupMembership>)(_groupMems)) {  
    	            Query q1 = new Query(Group.ENT_GROUP);
    	            QueryCriterium qc1 = new QueryCriterium(Group.ENT_GROUP, Group.FLD_ID,
    	                QueryCriterium.OP_EQUALS, new Integer(groupMem.getGroupId()) );
    	            q1.addQueryCriterium(qc1);
    	            RecordSet rsGroup;
	    			rsGroup = ds.select(q1);
	    	        if (rsGroup.next())
	    	        {
	    	            Group group = (Group)rsGroup.getCurrentObject();
	    	            _groups.add(group);
	    	        }     	    		
    	    	}
            }
        }
        return _groups;   	
    }

	public String getDelStatus() {
		return getStringValue(FLD_DEL_STATUS);
	}

	public void setDelStatus(String delStatus) {
		setValue(FLD_DEL_STATUS, delStatus);
	}

	public String getEmail() {
		return getStringValue(FLD_EMAIL);
	}

	public void setEmail(String email) {
		setValue(FLD_EMAIL, email);
	}

	public String getMobile() {
		return getStringValue(FLD_MOBILE);
	}

	public void setMobile(String mobile) {
		setValue(FLD_MOBILE, mobile);
	}

	public int getGroupId() {
		return getIntValue(FLD_GROUP_ID);
	}

	public void setGroupId(int groupId) {
		setValue(FLD_GROUP_ID, groupId);
	}
	
	public String getPrivilege() {
		return getStringValue(FLD_PRIVILEGE);
	}

	public void setPrivilege(String privilege) {
		setValue(FLD_PRIVILEGE, privilege);
	}

	public String getLicenseType() {
		return getStringValue(FLD_LICENSE_TYPE);
	}

	public void setLicenseType(String licenseType) {
		setValue(FLD_LICENSE_TYPE, licenseType);
	}	
	
	public String getConfigSettings() {
		throw new RuntimeException("Not implemented!");
	}
}

//==============================================================================
// end of file User.java
//==============================================================================
