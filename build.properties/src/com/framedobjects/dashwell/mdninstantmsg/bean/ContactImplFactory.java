package com.framedobjects.dashwell.mdninstantmsg.bean;

/**
 * Implementation class for Instant Message <code>ContactFactory</code> Interface.
 * @author Adele
 * 
 */

import com.itbs.aimcer.bean.Contact;
import com.itbs.aimcer.bean.ContactFactory;
import com.itbs.aimcer.bean.Nameable;
import com.itbs.aimcer.commune.Connection;

public class ContactImplFactory implements ContactFactory {

    public Contact create(Nameable buddy, Connection connection) {
        return create(buddy.getName(), connection);
    }


    public Contact create(String name, Connection connection) {
        return new ContactImpl(connection, name);
    }


    public Contact get(String name, Connection connection) {
        return new ContactImpl(connection, name);
    }

}
