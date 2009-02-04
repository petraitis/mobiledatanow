package com.framedobjects.dashwell.mdninstantmsg.bean;

/**
 * Implementation class for Instant Message <code>Contact</code> Interface.  
 * @author Adele
 */

import javax.swing.Icon;

import com.itbs.aimcer.bean.Contact;
import com.itbs.aimcer.bean.Status;
import com.itbs.aimcer.bean.StatusImpl;
import com.itbs.aimcer.commune.Connection;

public class ContactImpl implements Contact {

    private final Status status = new StatusImpl(this);
    private final Connection connection;
    private final String name;


    public ContactImpl(Connection connection, String name) {
        this.connection = connection;
        this.name = name;
    }
    public void statusChanged() {}
    public Icon getIcon() { return null; }
    public void setIcon(Icon icon) {}
    public Icon getPicture() { return null; }
    public void setPicture(Icon icon) { }
    public String getDisplayName() { return name; }
    public void setDisplayName(String name) {}
    public Status getStatus() { return status; }
    public Connection getConnection() { return connection; }
    public String getName() { return name; }

}
