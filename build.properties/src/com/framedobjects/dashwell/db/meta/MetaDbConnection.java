/**
 * 
 */
package com.framedobjects.dashwell.db.meta;

import com.framedobjects.dashwell.db.AbstractDbConnection;

/**
 * This class describes a database connection. However, it is not used to connect
 * to the physical database.
 * 
 * @author Jens Richnow
 *
 */
public class MetaDbConnection extends AbstractDbConnection {

  private MetaSchema metaSchema = null;
  
  public MetaDbConnection(int connectionID, String driver, String name,
  		String username, String password, String url, String schema, int mirrored){
  	super(connectionID, driver, name, username, password, url, mirrored);//schema, 
  }

  /**
   * @return Returns the metaSchema.
   */
  public MetaSchema getMetaSchema() {
    return metaSchema;
  }

  /**
   * @param metaSchema The metaSchema to set.
   */
  public void setMetaSchema(MetaSchema metaSchema) {
    this.metaSchema = metaSchema;
  }
}
