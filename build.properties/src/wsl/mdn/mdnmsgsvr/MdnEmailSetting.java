package wsl.mdn.mdnmsgsvr;

import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.DefaultKeyGeneratorData;
import wsl.fw.datasource.Entity;
import wsl.fw.datasource.EntityImpl;
import wsl.fw.datasource.Field;
import wsl.fw.datasource.FieldImpl;
import wsl.fw.util.Util;

public class MdnEmailSetting extends DataObject
{
    // version tag
    private final static String _ident = "$Date: 2006/12/9 12:26:09 $  $Revision: 1.2 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/mdnmsgsvr/MdnEmailSetting.java $ ";

    //--------------------------------------------------------------------------
    // constants
	
    // the entity name
    public final static String ENT_EMAIL = "TBL_EMAIL_SETTING";

    // field names
    public final static String FLD_ID       		     = "FLD_ID";
    
    public final static String FLD_EMAIL_ADDRESS      	 = "FLD_EMAIL_ADDRESS";
    public final static String FLD_IMAP_HOST     	     = "FLD_IMAP_HOST";
    public final static String FLD_IMAP_USER_NAME 		 = "FLD_IMAP_USERNAME";
    public final static String FLD_IMAP_PASSWORD      	 = "FLD_IMAP_PASSWORD";
    public final static String FLD_IMAP_PORT       	 	 = "FLD_IMAP_PORT";
    public final static String FLD_IMAP_ENCRYPTED_TYPE   = "FLD_IMAP_ENCRYPTED_TYPE";    
    
    public final static String FLD_SMTP_HOST     		 = "FLD_SMTP_HOST";
    public final static String FLD_SMTP_USERNAME     	 = "FLD_SMTP_USERNAME";    
    public final static String FLD_SMTP_PASSWORD     	 = "FLD_SMTP_PASSWORD";
    public final static String FLD_SMTP_PORT     	     = "FLD_SMTP_PORT";
    public final static String FLD_SMTP_ENCRYPTED_TYPE   = "FLD_SMTP_ENCRYPTED_TYPE";
    
    public final static String FLD_PROJECT_ID       	 = "FLD_PROJECT_ID";
    public final static String FLD_DEL_STATUS   		 = "FLD_DEL_STATUS";

    
    //--------------------------------------------------------------------------
    /**
     * Default constructor. Since an MdnEmailSetting is invalid if it is not correctly initialized
     * ensure that setter methods are called when using this constructor/
     */
    public MdnEmailSetting()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Static factory method to create the entity to be used by this dataobject
     * and any subclasses. This is called by the DataManager's factory when
     * creating a ENT_EMAIL entity.
     * @return the created entity.
     */
    public static Entity createEntity()
    {
        // create the Mdn Email entity
        Entity mdnEmailSettingEntity = new EntityImpl(ENT_EMAIL, MdnEmailSetting.class);

        // add the key generator for the system id
        mdnEmailSettingEntity.addKeyGeneratorData(new DefaultKeyGeneratorData(ENT_EMAIL, FLD_ID));

        // create the fields and add them to the entity
        mdnEmailSettingEntity.addField(new FieldImpl(FLD_ID, Field.FT_INTEGER, Field.FF_UNIQUE_KEY | Field.FF_SYSTEM_KEY));
        
        mdnEmailSettingEntity.addField(new FieldImpl(FLD_EMAIL_ADDRESS, Field.FT_STRING, Field.FF_NONE));
        mdnEmailSettingEntity.addField(new FieldImpl(FLD_IMAP_HOST, Field.FT_STRING, Field.FF_NONE));        
        mdnEmailSettingEntity.addField(new FieldImpl(FLD_IMAP_USER_NAME, Field.FT_STRING, Field.FF_NONE));        
        mdnEmailSettingEntity.addField(new FieldImpl(FLD_IMAP_PASSWORD, Field.FT_STRING, Field.FF_NONE));
        mdnEmailSettingEntity.addField(new FieldImpl(FLD_IMAP_PORT, Field.FT_STRING, Field.FF_NONE));
        mdnEmailSettingEntity.addField(new FieldImpl(FLD_IMAP_ENCRYPTED_TYPE, Field.FT_INTEGER));
        
        mdnEmailSettingEntity.addField(new FieldImpl(FLD_SMTP_HOST, Field.FT_STRING, Field.FF_NONE));        
        mdnEmailSettingEntity.addField(new FieldImpl(FLD_SMTP_USERNAME, Field.FT_STRING, Field.FF_NONE));        
        mdnEmailSettingEntity.addField(new FieldImpl(FLD_SMTP_PASSWORD, Field.FT_STRING, Field.FF_NONE));
        mdnEmailSettingEntity.addField(new FieldImpl(FLD_SMTP_PORT, Field.FT_STRING, Field.FF_NONE));
        mdnEmailSettingEntity.addField(new FieldImpl(FLD_SMTP_ENCRYPTED_TYPE, Field.FT_INTEGER));
        
        mdnEmailSettingEntity.addField(new FieldImpl(FLD_PROJECT_ID, Field.FT_INTEGER));
        mdnEmailSettingEntity.addField(new FieldImpl(FLD_DEL_STATUS, Field.FT_INTEGER, Field.FF_NONE));        
        // return the entity
        return mdnEmailSettingEntity;
    }

    //--------------------------------------------------------------------------
    /**
     * Return the Entity name.
     * @return the entity name.
     */
    public String getEntityName()
    {
        return ENT_EMAIL;
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
    //  --------------------------------------------------------------------------
	/**
	 * @return int the id of this project
	 */
	public int
	getProjectId ()
	{
		return getIntValue (FLD_PROJECT_ID);
	}
	//	--------------------------------------------------------------------------
	/**
	 * Set the id of this project
	 * @param id
	 */
	public void
	setProjectId (
	 int id)
	{
		setValue (FLD_PROJECT_ID, id);
	}    
    
    //--------------------------------------------------------------------------
    /**
     * Get the SMTP host.
     * @return the SmtpHost.
     */
    public String getSmtpHost()
    {
        return getStringValue(FLD_SMTP_HOST);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the SmtpHost.
     * @param SmtpHost
     */
    public void setSmtpHost(String smtpHost)
    {
        setValue(FLD_SMTP_HOST, smtpHost);
    }

    //--------------------------------------------------------------------------
    /**
     * Get the IMAP host.
     * @return the ImapHost.
     */
    public String getImapHost()
    {
        return getStringValue(FLD_IMAP_HOST);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the ImapHost.
     * @param ImapHost
     */
    public void setImapHost(String imapHost)
    {
        setValue(FLD_IMAP_HOST, imapHost);
    }

    //--------------------------------------------------------------------------
    /**
     * Get the IMAP username.
     * @return the imapUserName.
     */
    public String getImapUserName()
    {
        return getStringValue(FLD_IMAP_USER_NAME);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the ImapUserName.
     * @param ImapUserName
     */
    public void setImapUserName(String imapUserName)
    {
        setValue(FLD_IMAP_USER_NAME, imapUserName);
    }

    //--------------------------------------------------------------------------
    /**
     * Get the IMAP password.
     * @return the imapPassword.
     */
    public String getImapPassword()
    {
        return getStringValue(FLD_IMAP_PASSWORD);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the ImapPassword.
     * @param imapPassword
     */
    public void setImapPassword(String imapPassword)
    {
        setValue(FLD_IMAP_PASSWORD, imapPassword);
    }

    //--------------------------------------------------------------------------
    /**
     * Get the EmailAddress
     * @return the emailAddress
     */
    public String getEmailAddress()
    {
        return getStringValue(FLD_EMAIL_ADDRESS);
    }

    //--------------------------------------------------------------------------
    /**
     * Set the emailAddress.
     * @param emailAddress
     */
    public void setEmailAddress(String emailAddress)
    {
        setValue(FLD_EMAIL_ADDRESS, emailAddress);
    }
    
    //----------------------------------------------------------------------------
    /**
     * @return int the Delete Status
     */
    public int getDelStatus()
    {
        return getIntValue(FLD_DEL_STATUS);
    }
    
    //--------------------------------------------------------------------------
    /**
     * Set the DeleteStatus
     * @param delStatus
     */
    public void setDelStatus(int delStatus)
    {
        setValue(FLD_DEL_STATUS, delStatus);
    }
    
    //--------------------------------------------------------------------------
    /**
     * Get the imapPort
     * @return the imapPort
     */
    public String getImapPort()
    {
        return getStringValue(FLD_IMAP_PORT);
    }
    //--------------------------------------------------------------------------
    /**
     * Set the imapPort.
     * @param imapPort
     */
    public void setImapPort(String imapPort)
    {
        setValue(FLD_IMAP_PORT, imapPort);
    }
    //----------------------------------------------------------------------------
    /**
     * @return int the ImapEncryptedType
     */
    public int getImapEncryptedType()
    {
        return getIntValue(FLD_IMAP_ENCRYPTED_TYPE);
    }
    //--------------------------------------------------------------------------
    /**
     * Set the ImapSSL
     * @param imapSSL
     */
    public void setImapEncryptedType(int imapEncryptedType)
    {
        setValue(FLD_IMAP_ENCRYPTED_TYPE, imapEncryptedType);
    }
    //--------------------------------------------------------------------------
    /**
     * Get the smtpUsername
     * @return the smtpUsername
     */
    public String getSmtpUsername()
    {
        return getStringValue(FLD_SMTP_USERNAME);
    }
    //--------------------------------------------------------------------------
    /**
     * Set the SmtpUsername.
     * @param SmtpUsername
     */
    public void setSmtpUsername(String smtpUsername)
    {
        setValue(FLD_SMTP_USERNAME, smtpUsername);
    }
    //----------------------------------------------------------------------------    
    /**
     * Get the smtpPassword
     * @return the smtpPassword
     */
    public String getSmtpPassword()
    {
        return getStringValue(FLD_SMTP_PASSWORD);
    }
    //--------------------------------------------------------------------------
    /**
     * Set the SmtpPassword.
     * @param SmtpPassword
     */
    public void setSmtpPassword(String smtpPassword)
    {
        setValue(FLD_SMTP_PASSWORD, smtpPassword);
    }
    //----------------------------------------------------------------------------    
    /**
     * Get the smtpPort
     * @return the smtpPort
     */
    public String getSmtpPort()
    {
        return getStringValue(FLD_SMTP_PORT);
    }
    //--------------------------------------------------------------------------
    /**
     * Set the smtpPort.
     * @param smtpPort
     */
    public void setSmtpPort(String smtpPort)
    {
        setValue(FLD_SMTP_PORT, smtpPort);
    }
    //  --------------------------------------------------------------------------
    /**
     * @return int the smtpEncryptedType
     */
    public int getSmtpEncryptedType()
    {
        return getIntValue(FLD_SMTP_ENCRYPTED_TYPE);
    }
    //--------------------------------------------------------------------------
    /**
     * Set the SmtpEncryptedType
     * @param smtpEncryptedType
     */
    public void setSmtpEncryptedType(int smtpEncryptedType)
    {
        setValue(FLD_SMTP_ENCRYPTED_TYPE, smtpEncryptedType);
    }    
}


