package com.framedobjects.dashwell.mdninstantmsg.bean;

/**
 * Implementation class for Instant Message <code>Group</code> Interface.  
 * @author Adele
 */

import com.framedobjects.dashwell.utils.Constants;
import com.itbs.aimcer.bean.Group;
import com.itbs.aimcer.bean.Nameable;
import com.itbs.aimcer.commune.Connection;

public class GroupImpl implements Group {

	Nameable contact;
	public int size() { return 0; }
    public void clear(Connection connection) { }
    public Nameable get(int index) { return null; }
    public Nameable add(Nameable contact) { return contact; }
    public boolean remove(Nameable contact) { return true; }
    public String getName() { return Constants.IM_MDN_GROUP_NAME; }
	public Nameable[] toArray() {
		return null;
	}
}
