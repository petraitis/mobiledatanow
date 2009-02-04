package com.framedobjects.dashwell.mdninstantmsg.bean;

/**
 * Implementation class for Instant Message <code>GroupFactory</code> Interface. 
 * @author Adele
 */

import com.itbs.aimcer.bean.Group;
import com.itbs.aimcer.bean.GroupFactory;
import com.itbs.aimcer.bean.GroupList;

public class GroupImplFactory implements GroupFactory {
    public Group create(String group) {
        return new GroupImpl();
    }
    public Group create(Group group) {
        return new GroupImpl();
    }
	public GroupList getGroupList() {
		// TODO Auto-generated method stub
		return null;
	}
}
