/**
 * 
 */
package com.framedobjects.dashwell.db;

import com.framedobjects.dashwell.utils.Constants;

/**
 * Factory returning the correct data interface based on setting in properties file.
 * @author Jens Richnow
 *
 */
public class DataAgentFactory {
	
	/**
	 * Creates a new object of the interface type <code>IDataAgent</code>. The
	 * desired type is to be set in the <code>config.properties</code> file. If
	 * it is not defined in the properties file <code>null</code> is returned, i.e.,
	 * this method does not return a default data interface.
	 * @return
	 */
	public static IDataAgent getDataInterface(){
		if (Constants.DATA_AGENT.equalsIgnoreCase(Constants.DATA_AGENT_DB)){
			throw new RuntimeException("DbAgent doesn't fully implement IDataAgent, can't use.");
//			return new DbAgent();
		} else if (Constants.DATA_AGENT.equalsIgnoreCase(Constants.DATA_AGENT_RMI)){
			return new RmiAgent();
		}
		return null;
	}
	
	

}
