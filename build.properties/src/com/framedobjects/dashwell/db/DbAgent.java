package com.framedobjects.dashwell.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.object.MappingSqlQuery;

import wsl.mdn.mdnim.IMConnection;

import com.framedobjects.dashwell.db.meta.MetaQuery;
import com.framedobjects.dashwell.db.meta.MetaView;
import com.framedobjects.dashwell.db.meta.MetaViewField;
import com.framedobjects.dashwell.db.DbConnection;
import com.framedobjects.dashwell.db.DbSchema;
import com.framedobjects.dashwell.db.meta.MetaField;
import com.framedobjects.dashwell.db.meta.MetaRelation;
import com.framedobjects.dashwell.db.meta.MetaTable;
import com.framedobjects.dashwell.utils.DataUtils;
import com.framedobjects.dashwell.biz.ConnectionWizard;
import com.framedobjects.dashwell.db.meta.MetaDbConnection;
import com.framedobjects.dashwell.biz.Group;
import com.framedobjects.dashwell.biz.RecycleBinItem;
import com.framedobjects.dashwell.biz.User;
import com.framedobjects.dashwell.utils.Constants;
import com.framedobjects.db.ConnectionPool;
import com.framedobjects.db.SQLMultipleRequest;
import com.framedobjects.db.SQLParameter;
import com.framedobjects.db.SQLQuery;
import com.framedobjects.db.SQLUpdate;

//FIXME DbAgent DOES NOT implement IDataAgent... most of the methods aren't implemented!
public class DbAgent  {
  
  static Logger logger = Logger.getLogger(DbAgent.class.getName());

  public static boolean isInitialised = false;
  
  public DbAgent() {
    try {
      if (!isInitialised) {
        ConnectionPool.init();
        isInitialised = true;
      } else {
      }
    } catch (Exception e) {
      logger.warn("Exception initialising connection pool. " + e.getMessage());
    }
  }

  public User getLoginUser(String username, String password){
    User user = null;
    UserQuery query = new UserQuery();
    query.setSQL("select * from users where name = ? and password = ?");
    query.declareParameter(new SQLParameter(Types.VARCHAR));
    query.declareParameter(new SQLParameter(Types.VARCHAR));
    user = (User)query.findObject(username, password);
    return user;
  }
  
  public Vector<String> getNavProjects(){
    Vector<String> projects = new Vector<String>();
    projects.add("ACT!");
    projects.add("Sample 1");
    projects.add("Sample 2");
    projects.add("Sample 3");
    projects.add("Weather");
    return projects;
  }
  
  public List<MetaDbConnection> getAllDbConnections() {
    DbConnectionQuery query = new DbConnectionQuery();
    query.setSQL("select * from db_connection order by name");
    return query.execute();
  }
  
  public List<MetaTable> getAllMetaTables(int dbConnectionID, boolean includeFields) {
  	SQLMultipleRequest multiRequest = new SQLMultipleRequest();
  	multiRequest.start();
  	
  	MetaTableQuery tableQuery = new MetaTableQuery();
    tableQuery.setSQL("select * from meta_table where connection_id = ? order by table_name");
    tableQuery.declareParameter(new SQLParameter(Types.INTEGER));
    
    List<MetaTable> tables = multiRequest.execute(tableQuery, dbConnectionID);
    
    if (includeFields){
    	for (MetaTable table : tables) {
	    	MetaFieldQuery fieldQuery = new MetaFieldQuery();
	    	fieldQuery.setSQL("select * from meta_field where table_id = ? order by field_name");
	    	fieldQuery.declareParameter(new SQLParameter(Types.INTEGER));
	    	List<MetaField> fields = multiRequest.execute(fieldQuery, table.getTableID());
	    	table.setAllFields(fields);
    	}
    }
    
    multiRequest.finish();
    
    return tables;
  }
  /**
   * get all the views based on connection ID
   * if need to include fields then load view fields also
   * @param dbConnectionID
   * @param includeFields
   * @return
   */
  public List<MetaView> getAllMetaViews(int dbConnectionID, boolean includeFields) {
	  	SQLMultipleRequest multiRequest = new SQLMultipleRequest();
	  	multiRequest.start();
	  	
	  	MetaViewQuery viewQuery = new MetaViewQuery();
	    viewQuery.setSQL("select * from meta_view where connection_id = ? order by view_name");
	    viewQuery.declareParameter(new SQLParameter(Types.INTEGER));
	    
	    List<MetaView> views = multiRequest.execute(viewQuery, dbConnectionID);
	    
	    if (includeFields){
	    	for (MetaView view : views) {
	    		MetaViewFieldQuery viewFieldQuery = new MetaViewFieldQuery();
		    	viewFieldQuery.setSQL("select * from view_field where view_id = ? order by field_name");
		    	viewFieldQuery.declareParameter(new SQLParameter(Types.INTEGER));
		    	List<MetaViewField> fields = multiRequest.execute(viewFieldQuery, view.getViewID());
		    	view.setAllFields(fields);
	    	}
	    }
	    
	    multiRequest.finish();
	    
	    return views;
  }

  public MetaView getMetaViewByID(int viewID, boolean includeFields){
	  	MetaView view = null;
	  	
	  	SQLMultipleRequest multiRequest = new SQLMultipleRequest();
	  	multiRequest.start();
	  	
	  	MetaViewQuery viewQuery = new MetaViewQuery();
	    viewQuery.setSQL("select * from meta_view where view_id = ?");
	    viewQuery.declareParameter(new SQLParameter(Types.INTEGER));
	    
	    Object obj = multiRequest.findObject(viewQuery, viewID);
	    if (obj != null){
	    	view = (MetaView)obj;
	    }
	    
	    if (view != null && includeFields){
	    	MetaViewFieldQuery fieldQuery = new MetaViewFieldQuery();
	    	fieldQuery.setSQL("select * from view_field where view_id = ? order by field_name");
	    	fieldQuery.declareParameter(new SQLParameter(Types.INTEGER));
	    	List<MetaViewField> fields = multiRequest.execute(fieldQuery, view.getViewID());
	    	view.setAllFields(fields);
	    }
	    
	    multiRequest.finish();
	  	
	  	return view;
  }  
  
  public MetaTable getMetaTableByID(int tableID, boolean includeFields){
  	MetaTable table = null;
  	
  	SQLMultipleRequest multiRequest = new SQLMultipleRequest();
  	multiRequest.start();
  	
  	MetaTableQuery tableQuery = new MetaTableQuery();
    tableQuery.setSQL("select * from meta_table where table_id = ?");
    tableQuery.declareParameter(new SQLParameter(Types.INTEGER));
    
    Object obj = multiRequest.findObject(tableQuery, tableID);
    if (obj != null){
    	table = (MetaTable)obj;
    }
    
    if (table != null && includeFields){
    	MetaFieldQuery fieldQuery = new MetaFieldQuery();
    	fieldQuery.setSQL("select * from meta_field where table_id = ? order by field_name");
    	fieldQuery.declareParameter(new SQLParameter(Types.INTEGER));
    	List<MetaField> fields = multiRequest.execute(fieldQuery, table.getTableID());
    	table.setAllFields(fields);
    }
    
    multiRequest.finish();
  	
  	return table;
  }
  
  public MetaTable getMetaTableByName(String tableName, int connID, boolean includeFields){
  	MetaTable table = null;
  	
  	SQLMultipleRequest multiRequest = new SQLMultipleRequest();
  	multiRequest.start();
  	
  	MetaTableQuery tableQuery = new MetaTableQuery();
    tableQuery.setSQL("select * from meta_table where table_name = ? and connection_id = ?");
    tableQuery.declareParameter(new SQLParameter(Types.VARCHAR));
    tableQuery.declareParameter(new SQLParameter(Types.INTEGER));
    
    Object obj = multiRequest.findObject(tableQuery, tableName, connID);
    if (obj != null){
    	table = (MetaTable)obj;
    }
    
    if (table != null && includeFields){
    	MetaFieldQuery fieldQuery = new MetaFieldQuery();
    	fieldQuery.setSQL("select * from meta_field where table_id = ? order by field_name");
    	fieldQuery.declareParameter(new SQLParameter(Types.INTEGER));
    	List<MetaField> fields = multiRequest.execute(fieldQuery, table.getTableID());
    	table.setAllFields(fields);
    }
    
    multiRequest.finish();
  	
  	return table;
  }
  
  public int editMetaTable(MetaTable table){
  	SQLUpdate update = new SQLUpdate();
  	update.setSQL("update meta_table set table_name = ?, description = ? where table_id = ?");
  	update.declareParameter(new SQLParameter(Types.VARCHAR));
  	update.declareParameter(new SQLParameter(Types.VARCHAR));
  	update.declareParameter(new SQLParameter(Types.INTEGER));
  	return update.update(new Object[]{table.getName(), table.getDescription(),
  			Integer.valueOf(table.getTableID())});
  }
  
  
  public MetaField getMetaField(int fieldID){
  	MetaField field = null;
  	MetaFieldQuery fieldQuery = new MetaFieldQuery();
  	fieldQuery.setSQL("select * from meta_field where field_id = ?");
  	fieldQuery.declareParameter(new SQLParameter(Types.INTEGER));
  	Object obj = fieldQuery.findObject(fieldID);
  	if (obj != null){
  		field = (MetaField)obj;
  	}
  	return field;
  }

  public MetaDbConnection getDbConnectionByName(String dbConnName) {
    DbConnectionQuery query = new DbConnectionQuery();
    query.setSQL("select * from db_connection where name = ?");
    query.declareParameter(new SQLParameter(Types.VARCHAR));
    MetaDbConnection metaConn = (MetaDbConnection) query.findObject(dbConnName);
    return metaConn;
  }

  public MetaDbConnection getDbConnectionByID(int connectionID) {
    DbConnectionQuery query = new DbConnectionQuery();
    query.setSQL("select * from db_connection where connection_id = ?");
    query.declareParameter(new SQLParameter(Types.INTEGER));
    MetaDbConnection metaConn = (MetaDbConnection) query.findObject(connectionID);
    return metaConn;
  }

  public MetaDbConnection getDbConnectionByFullUrl(String url, String schema) {
    DbConnectionQuery query = new DbConnectionQuery();
    query.setSQL("select * from db_connection where url = ? and schema = ?");
    query.declareParameter(new SQLParameter(Types.VARCHAR));
    query.declareParameter(new SQLParameter(Types.VARCHAR));
    MetaDbConnection metaConn = (MetaDbConnection) query.findObject(url, schema);
    return metaConn;
  }
  
  public int createNewDbConnection(ConnectionWizard wizard) {
  	int result = 0;
  	SQLMultipleRequest multiRequest = new SQLMultipleRequest();
  	multiRequest.start();
  	multiRequest.autoCommitOff();
  	
  	DbConnection dbConn = wizard.getDbConn();
    DbSchema dbSchema = wizard.getDbSchema();
  	//logger.debug("Inserting schema ... " + dbConn.getSchema());
  	SQLUpdate insertSchema = new SQLUpdate();
  	insertSchema.setSQL("insert into db_connection " +
          "(name, driver, url, schema, username, password, mirrorred) " +
          "values (?, ?, ?, ?, ?, ?, ?)");
  	insertSchema.declareParameter(new SQLParameter(Types.VARCHAR));	// name
  	insertSchema.declareParameter(new SQLParameter(Types.VARCHAR));	// driver
  	insertSchema.declareParameter(new SQLParameter(Types.VARCHAR));	// url
  	insertSchema.declareParameter(new SQLParameter(Types.VARCHAR));	// schema
  	insertSchema.declareParameter(new SQLParameter(Types.VARCHAR));	// username
  	insertSchema.declareParameter(new SQLParameter(Types.VARCHAR));	// password
  	insertSchema.declareParameter(new SQLParameter(Types.INTEGER));	// mirrorred
  	if (multiRequest.update(insertSchema, new Object[]{dbConn.getName(),
  			dbConn.getDriver(), dbConn.getUrl(), //dbConn.getSchema(), 
  			dbConn.getUsername(), dbConn.getPassword(), 
  			Integer.valueOf(dbConn.getMirrorred())}) > 0){
  		int connectionID = multiRequest.getLastID();
  		logger.debug("... schema inserted with connection_id " + connectionID);
  		int tableID = 0;
    	
      // Get all selected tables.
      Set<MetaTable> metaTables = wizard.getMetaTables();
      for (MetaTable metaTable : metaTables) {
      	logger.debug("Inserting table ... " + metaTable.getName());
      	SQLUpdate insertTable = new SQLUpdate();
    		insertTable.setSQL("insert into meta_table " +
            "(table_name, description, connection_id) values (?, ?, ?)");
    		insertTable.declareParameter(new SQLParameter(Types.VARCHAR));	// table_name
    		insertTable.declareParameter(new SQLParameter(Types.VARCHAR));	// description
    		insertTable.declareParameter(new SQLParameter(Types.INTEGER));	// connection_id
				multiRequest.update(insertTable, new Object[]{metaTable.getName(),
						metaTable.getDescription(), Integer.valueOf(connectionID)});
				tableID = multiRequest.getLastID();
				logger.debug("... table inserted with table_id " + tableID);
        // Get all fields for this table.
				List<MetaField> fields = dbSchema.getFieldsForTable(metaTable.getName());
				for (MetaField field : fields) {
					logger.debug("Inserting field ... " + field.getName());
					SQLUpdate insertField = new SQLUpdate();
		    	insertField.setSQL("insert into meta_field " +
		          "(field_name, field_type, native_type, description, column_size, " +
		          "decimal_digits, table_id, connection_id) " +
		        	"values (?, ?, ?, ?, ?, ?, ?, ?)");
		    	insertField.declareParameter(new SQLParameter(Types.VARCHAR));	// field_name
		    	insertField.declareParameter(new SQLParameter(Types.INTEGER));	// field_type
		    	insertField.declareParameter(new SQLParameter(Types.VARCHAR));	// native_type
		    	insertField.declareParameter(new SQLParameter(Types.VARCHAR));	// description.
		    	insertField.declareParameter(new SQLParameter(Types.INTEGER));	// column_size
		    	insertField.declareParameter(new SQLParameter(Types.INTEGER));	// decimal_digits
		    	insertField.declareParameter(new SQLParameter(Types.INTEGER));	// table_id
		    	insertField.declareParameter(new SQLParameter(Types.INTEGER));	// connectoin_id
					multiRequest.update(insertField, new Object[]{field.getName(),
							Integer.valueOf(DataUtils.getFieldTypeInt(field.getType())),
							field.getNativeType(), field.getDescription(),
							Integer.valueOf(field.getSize()), 
							Integer.valueOf(field.getDecimalDigits()),
							Integer.valueOf(tableID), Integer.valueOf(connectionID)});
					logger.debug("... field inserted.");
				}
			}
      // Insert the relationships.
      List<MetaRelation> relations = dbSchema.getRelations();
      for (MetaRelation relation : relations) {
      	logger.debug("Inserting relation ... " + relation.getQualifiedName());
      	SQLUpdate insertRelation = new SQLUpdate();
        insertRelation.setSQL("insert into meta_relation " +
               "(relation_name, left_table, left_field, right_table, right_field, connection_id) " +
               "values (?, ?, ?, ?, ?, ?)");
        insertRelation.declareParameter(new SQLParameter(Types.VARCHAR));	// relation_name
        insertRelation.declareParameter(new SQLParameter(Types.VARCHAR));	// left_table
        insertRelation.declareParameter(new SQLParameter(Types.VARCHAR));	// left_field
        insertRelation.declareParameter(new SQLParameter(Types.VARCHAR));	// right_table
        insertRelation.declareParameter(new SQLParameter(Types.VARCHAR));	// right_field
        insertRelation.declareParameter(new SQLParameter(Types.INTEGER));	// connection_id
				multiRequest.update(insertRelation, new Object[]{
						relation.getQualifiedName(), relation.getLeftTable(),
						relation.getLeftField(), relation.getRightTable(),
						relation.getRightField(), Integer.valueOf(connectionID)});
				logger.debug("... relation inserted");
			}
      result = 1;
  	}
  	multiRequest.commit();
  	multiRequest.finish();
  	return result;
  }
  
  public int deleteConnection(int connectionID){
  	SQLUpdate deleteConn = new SQLUpdate();
  	deleteConn.setSQL("delete from db_connection where connection_id = ?");
  	deleteConn.declareParameter(new SQLParameter(Types.INTEGER));
  	return deleteConn.update(connectionID);
  }
  
  /* (non-Javadoc)
   * @see com.framedobjects.dashwell.db.IDataAgent#getUsers()
   */
  public Vector<User> getUsers(boolean includeGroups){
  	SQLMultipleRequest multiRequest = new SQLMultipleRequest();
  	multiRequest.start();
  	
  	UserQuery query = new UserQuery();
    query.setSQL("select * from users where del_status = ? order by name");
    query.declareParameter(new SQLParameter(Types.INTEGER));
    List<User> list = multiRequest.execute(query, new Integer(0));
    
    GroupQuery groupQuery = null;
    if (includeGroups){
	    // Prepare the groups query.
	    groupQuery = new GroupQuery();
	    groupQuery.setSQL("select groups.* from groups, user_groups "
	        + "where user_groups.user_id = ? "
	        + "and user_groups.group_id = groups.group_id");
	    groupQuery.declareParameter(new SQLParameter(Types.INTEGER));
    }
    Vector<User> users = new Vector<User>();
    for (User user: list) {
    	if (includeGroups){
    		List groups = multiRequest.execute(groupQuery, user.getUserID());
    		user.setAllGroups(groups);
    	}
    	users.add(user);
		}
    
    multiRequest.finish();
    return users;
  }
  
  /* (non-Javadoc)
   * @see com.framedobjects.dashwell.db.IDataAgent#existUsername(java.lang.String)
   */
  public User getUserByName(String username){
  	User user = null;
  	UserQuery query = new UserQuery();
  	query.setSQL("select * from users where name = ?");
  	query.declareParameter(new SQLParameter(Types.VARCHAR));
  	Object obj = query.findObject(username);
  	if (obj != null){
  		user = (User)obj;
  	}
  	return user;
  }
  
  /* (non-Javadoc)
   * @see com.framedobjects.dashwell.db.IDataAgent#getUser(int)
   */
  public User getUser(int userID, boolean includeGroups){
  	User user = null;
  	
  	SQLMultipleRequest multiRequest = new SQLMultipleRequest();
  	multiRequest.start();
  	
  	UserQuery query = new UserQuery();
    query.setSQL("select * from users where user_id = ?");
    query.declareParameter(new SQLParameter(Types.INTEGER));
    
    Object obj = multiRequest.findObject(query, userID);
    if (obj != null){
    	user = (User)obj;
    	if (includeGroups){
    		GroupQuery groupQuery = new GroupQuery();
  	    groupQuery.setSQL("select groups.* from groups, user_groups "
  	        + "where user_groups.user_id = ? "
  	        + "and user_groups.group_id = groups.group_id");
  	    groupQuery.declareParameter(new SQLParameter(Types.INTEGER));
  	    List groups = multiRequest.execute(groupQuery, user.getUserID());
  	    user.setAllGroups(groups);
    	}
    }
    multiRequest.finish();
    return user;
  }
  
  public int newUser(User user){
  	SQLUpdate update = new SQLUpdate();
  	update.setSQL("insert into users (name, password, email, mobile, details, " +
  			"query_user_id) values (?, ?, ?, ?, ?, ?)");
  	update.declareParameter(new SQLParameter(Types.VARCHAR));
  	update.declareParameter(new SQLParameter(Types.VARCHAR));
  	update.declareParameter(new SQLParameter(Types.VARCHAR));
  	update.declareParameter(new SQLParameter(Types.VARCHAR));
  	update.declareParameter(new SQLParameter(Types.VARCHAR));
  	update.declareParameter(new SQLParameter(Types.VARCHAR));
  	return update.update(new Object[]{user.getUsername(), user.getPassword(),
  												user.getEmail(), user.getMobile(), user.getDetails(), 
  												user.getQueryUserID()});
  }
  
  public int editUser(User user){
  	SQLUpdate update = new SQLUpdate();
  	update.setSQL("update users set name = ?, password = ?, email = ?, " +
  			"mobile = ?, details = ?, query_user_id = ? where user_id = ?");
  	update.declareParameter(new SQLParameter(Types.VARCHAR));
  	update.declareParameter(new SQLParameter(Types.VARCHAR));
  	update.declareParameter(new SQLParameter(Types.VARCHAR));
  	update.declareParameter(new SQLParameter(Types.VARCHAR));
  	update.declareParameter(new SQLParameter(Types.VARCHAR));
  	update.declareParameter(new SQLParameter(Types.VARCHAR));
  	update.declareParameter(new SQLParameter(Types.INTEGER));
  	return update.update(new Object[]{user.getUsername(), user.getPassword(),
  												user.getEmail(), user.getMobile(), user.getDetails(), 
  												user.getQueryUserID(), new Integer(user.getUserID())});
  }
  
  public int markUserAsRecycled(int userID){
  	return markUserDeletedState(userID, Constants.MARKED_AS_RECYCLED_INT);
  }
  
  public int markUserAsCleared(int userID){
  	return markUserDeletedState(userID, Constants.MARKED_AS_CLEARED_INT);
  }
  
  public int markUserAsDeleted(int userID){
		return markUserDeletedState(userID, Constants.MARKED_AS_DELETED_INT);
	}
  
  private int markUserDeletedState(int userID, int deletedState){
  	SQLUpdate update = new SQLUpdate();
  	update.setSQL("update users set del_status = ? where user_id = ?");
  	update.declareParameter(new SQLParameter(Types.INTEGER));
  	update.declareParameter(new SQLParameter(Types.INTEGER));
  	return update.update(deletedState, userID);
  }
  
  /* (non-Javadoc)
   * @see com.framedobjects.dashwell.db.IDataAgent#getGroups()
   */
  public Vector<Group> getGroups(){
    Vector<Group> groups = new Vector<Group>();
    
    GroupQuery query = new GroupQuery();
    query.setSQL("select * from groups where del_status = ?");
    query.declareParameter(new SQLParameter(Types.INTEGER));
    List<Group> list = query.execute(Constants.MARKED_AS_CLEARED_INT);
    
    for (Group group: list) {
    	groups.add(group);
		}    
    return groups;
  }
  
  public int newGroup(Group group){
  	SQLUpdate update = new SQLUpdate();
  	update.setSQL("insert into groups (name) values (?)");
  	update.declareParameter(new SQLParameter(Types.VARCHAR));
  	return update.update(group.getName());
	}
  
  /* (non-Javadoc)
   * @see com.framedobjects.dashwell.db.IDataAgent#getGroup(int)
   */
  public Group getGroup(int groupID){
  	Group group = null;
  	GroupQuery query = new GroupQuery();
    query.setSQL("select * from groups where group_id = ?");
    query.declareParameter(new SQLParameter(Types.INTEGER));
    Object obj = query.findObject(groupID);
    if (obj != null){
    	group = (Group)obj;
    }
    return group;
  }
  
  public Group getGroupByName(String groupName){
  	Group group = null;
  	GroupQuery query = new GroupQuery();
    query.setSQL("select * from groups where name = ?");
    query.declareParameter(new SQLParameter(Types.VARCHAR));
    Object obj = query.findObject(groupName);
    if (obj != null){
    	group = (Group)obj;
    }
    return group;
	}
  
  public int markGroupAsCleared(int groupID){
		return markGroupDeletedState(groupID, Constants.MARKED_AS_CLEARED_INT);
	}
  
  public int markGroupAsDeleted(int groupID){
		return markGroupDeletedState(groupID, Constants.MARKED_AS_DELETED_INT);
	}
  
  public int markGroupAsRecycled(int groupID){
		return markGroupDeletedState(groupID, Constants.MARKED_AS_RECYCLED_INT);
	}
  
  private int markGroupDeletedState(int groupID, int deletedState){
  	SQLUpdate update = new SQLUpdate();
  	update.setSQL("update groups set del_status = ? where group_id = ?");
  	update.declareParameter(new SQLParameter(Types.INTEGER));
  	update.declareParameter(new SQLParameter(Types.INTEGER));
  	return update.update(deletedState, groupID);
  }
  
  /* (non-Javadoc)
   * @see com.framedobjects.dashwell.db.IDataAgent#getRecycleBinContent()
   */
  public Vector<RecycleBinItem> getRecycleBinContent(){
  	Vector<RecycleBinItem> bin = new Vector<RecycleBinItem>();
  	RecycleBinItem binItem = null;
  	SQLMultipleRequest multiRequest = new SQLMultipleRequest();
  	multiRequest.start();
  	// Add all users that are marked as being recycled.
  	UserQuery userQuery = new UserQuery();
  	userQuery.setSQL("select * from users where del_status = ?");
  	userQuery.declareParameter(new SQLParameter(Types.INTEGER)); 
    List users = multiRequest.execute(userQuery, Constants.MARKED_AS_RECYCLED);
    User user = null;
    for (Object object : users) {
			user = (User)object;
			//FIXME need to set an actual projectID
			binItem = new RecycleBinItem(user.getUserID(), "user", user.getUsername(),0);
			bin.add(binItem);
		}
    // Add all groups that are marked as being recycled.
    GroupQuery groupQuery = new GroupQuery();
    groupQuery.setSQL("select * from groups where del_status = ?");
    groupQuery.declareParameter(new SQLParameter(Types.INTEGER)); 
    List groups = multiRequest.execute(groupQuery, Constants.MARKED_AS_RECYCLED);
    Group group = null;
    for (Object object : groups) {
			group = (Group)object;
			//FIXME need to set an actual projectID
			binItem = new RecycleBinItem(group.getGroupID(), "group", group.getName(),0);
			bin.add(binItem);
		}
  	multiRequest.finish();
		return bin;
	}
  
  class UserQuery extends SQLQuery{
    
    public User mapRow(ResultSet rs) throws SQLException{
      User user = null;
      int userID = rs.getInt("user_id");
      String username = rs.getString("name");
      String password = rs.getString("password");
      String email = rs.getString("email");
      String mobile = rs.getString("mobile");
      String details = rs.getString("details");
      String queryUserID = rs.getString("query_user_id");
      user = new User(userID, username, password, email, mobile, details, queryUserID);
      return user;
    }
  }
  
  class GroupQuery extends SQLQuery{
    
    public Group mapRow(ResultSet rs) throws SQLException{
      Group group = new Group();
      group.setGroupID(rs.getInt("group_id"));
      group.setName(rs.getString("name"));
      group.setDescription(rs.getString("description"));
      logger.debug(group);
      return group;
    }
  }
  
  class DbConnectionQuery extends SQLQuery {

    public MetaDbConnection mapRow(ResultSet rs) throws SQLException {
      MetaDbConnection metaConn = null;
      int connectionID = rs.getInt("connection_id");
      String name = rs.getString("name");
      String driver = rs.getString("driver");
      String url = rs.getString("url");
      String schema = rs.getString("schema");
      String username = rs.getString("username");
      String password = rs.getString("password");
      int mirrorred = rs.getInt("mirrorred");
      metaConn = new MetaDbConnection(connectionID, driver, name, username, 
      		password, url, schema, mirrorred);
      return metaConn;
    }
  }
  
  class MetaTableQuery extends SQLQuery {
    
    public MetaTable mapRow(ResultSet rs) throws SQLException {
      int tableID = rs.getInt("table_id");
      String tableName = rs.getString("table_name");
      String description = rs.getString("description");
      int connectionID = rs.getInt("connection_id");
      MetaTable metaTable = new MetaTable(tableID, connectionID, "", tableName, description);
      return metaTable;
    }
  }
  
  class MetaViewQuery extends SQLQuery {
    
    public Object mapRow(ResultSet rs) throws SQLException {
      MetaView metaView = new MetaView();
      metaView.setViewID(rs.getInt("view_id"));
      metaView.setConnectionID(rs.getInt("connection_id"));
      metaView.setViewName(rs.getString("view_name"));
      metaView.setDescription(rs.getString("description"));
      return metaView;
    }
  }
  
  class MetaQueryQuery extends SQLQuery {
    
    public Object mapRow(ResultSet rs) throws SQLException {
      MetaQuery metaQuery = new MetaQuery();
      metaQuery.setQueryID(rs.getInt("query_id"));
      metaQuery.setViewID(rs.getInt("view_id"));
      metaQuery.setSchemaID(rs.getInt("connection_id"));
      metaQuery.setQueryName(rs.getString("queryName"));
      metaQuery.setDescription(rs.getString("description"));
      metaQuery.setCriteria(rs.getString("criteria"));
      metaQuery.setSorts(rs.getString("sorts"));
      metaQuery.setGroupFieldID(rs.getInt("group_field_id"));
      metaQuery.setRawQuery(rs.getString("raw_query"));
      return metaQuery;
    }
  }
  
  class MetaViewFieldQuery extends SQLQuery{
    
    public Object mapRow(ResultSet rs) throws SQLException {
      MetaViewField metaViewField = new MetaViewField();
      metaViewField.setFieldID(rs.getInt("field_id"));
      metaViewField.setViewID(rs.getInt("view_id"));
      metaViewField.setSchemaID(rs.getInt("connection_id"));
      metaViewField.setFieldName(rs.getString("field_name"));
      metaViewField.setDescription(rs.getString("description"));
      metaViewField.setSourceTable(rs.getString("source_table"));
      metaViewField.setSourceField(rs.getString("source_field"));
      metaViewField.setDisplayName(rs.getString("display_name"));
      metaViewField.setFlags(rs.getInt("flags"));
      metaViewField.setOptionList(rs.getString("option_list"));
      return metaViewField;
    }
  }
  
  class MetaFieldQuery extends SQLQuery {
    
    public Object mapRow(ResultSet rs) throws SQLException {
      MetaField metaField = new MetaField();
      metaField.setFieldID(rs.getInt("field_id"));
      metaField.setTableID(rs.getInt("table_id"));
      metaField.setConnectionID(rs.getInt("connection_id"));
      metaField.setName(rs.getString("field_name"));
      metaField.setFieldType(rs.getInt("field_type"));
      metaField.setType(DataUtils.getFieldType(metaField.getFieldType()));
      metaField.setNativeType(rs.getString("native_type"));
      metaField.setSize(String.valueOf(rs.getInt("column_size")));
      metaField.setDecimalDigits(rs.getInt("decimal_digits"));
      metaField.setDescription(rs.getString("description"));
      return metaField;
    }
  }
  
  class MetaRelationQuery extends SQLQuery {
    
    public Object mapRow(ResultSet rs) throws SQLException {
      int relationID = rs.getInt("relation_id");
      int connectionID = rs.getInt("connection_id");
      String relationName = rs.getString("relation_name");
      String leftTable = rs.getString("left_table");
      String leftField = rs.getString("left_field");
      String rightTable = rs.getString("right_table");
      String rightField = rs.getString("right_field");
      MetaRelation metaRelation = new MetaRelation(relationID, connectionID, 
      		relationName, leftTable, leftField, rightTable, rightField);
      return metaRelation;
    }
  }

}
