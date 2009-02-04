/**	$Header: /wapsolutions/cvsroot/mdn/fw/src/wsl/fw/msgserver/ContactDobj.java,v 1.1.1.1 2002/06/11 23:11:42 jonc Exp $
 *
 *	Contact information
 *
 */
package wsl.fw.msgserver;

// imports
import wsl.fw.datasource.*;

public class ContactDobj extends ItemDobj
{
    //--------------------------------------------------------------------------
    // constants

    public static final String FLD_SURNAME = "Surname";
    public static final String FLD_FIRST_NAME = "First Name";
    public static final String FLD_COMPANY = "Company";
    public static final String FLD_EMAIL = "Email";
    public static final String FLD_BUS_PHONE = "Bus Ph";
    public static final String FLD_HOME_PHONE = "Hm Ph";
    public static final String FLD_MOB_PHONE = "Mob Ph";
    public static final String FLD_BUS_FAX = "Bus Fax";
    public static final String FLD_BUS_ADD = "Bus Add";
    public static final String FLD_BUS_CITY = "Bus City";
    public static final String FLD_BUS_CTRY = "Bus Ctry";


    //--------------------------------------------------------------------------
    // construction

    /**
     * Param ctor
     * @param name the name of the contact
     */
    public ContactDobj(Contact c)
    {
        // fields
        setFieldValue(FLD_SURNAME, c._surname);
        setFieldValue(FLD_FIRST_NAME, c._firstName);
        setFieldValue(FLD_COMPANY, c._company);
        setFieldValue(FLD_EMAIL, c._email);
        setFieldValue(FLD_BUS_PHONE, c._busTel);
        setFieldValue(FLD_HOME_PHONE, c._homeTel);
        setFieldValue(FLD_MOB_PHONE, c._mobTel);
        setFieldValue(FLD_BUS_FAX, c._busFax);
        setFieldValue(FLD_BUS_ADD, c._busStreet);
        setFieldValue(FLD_BUS_CITY, c._busCity);
        setFieldValue(FLD_BUS_CTRY, c._busCountry);

        // if no surname or first name, use name
        if(c._firstName.length() == 0 && c._surname.length() == 0)
            setFieldValue(FLD_SURNAME, c._name);
    }

    /**
     * method for subs to create / add fields
     */
    protected void createFields()
    {
        addField(FLD_SURNAME);
        addField(FLD_FIRST_NAME);
        addField(FLD_COMPANY);
        addField(FLD_EMAIL);
        addField(FLD_BUS_PHONE);
        addField(FLD_HOME_PHONE);
        addField(FLD_MOB_PHONE);
        addField(FLD_BUS_FAX);
        addField(FLD_BUS_ADD);
        addField(FLD_BUS_CITY);
        addField(FLD_BUS_CTRY);
    }

	/**
	 *	Query fields click&dial capability (override superclass)
	 *  @param label field name
	 */
	public boolean
	isPhonedialField (
	 String label)
	{
		return
			label.equals (FLD_BUS_PHONE) ||
			label.equals (FLD_HOME_PHONE) ||
			label.equals (FLD_MOB_PHONE);
	}

	/*
	 *	Query fields email capability (override superclass)
	 *  @param label field name
	 */
	public boolean
	isEmailField (
	 String label)
	{
		return label.equals (FLD_EMAIL);
	}

    //--------------------------------------------------------------------------
    // to string

    public String toString()
    {
        String ret = "";
        String surname = getFieldValue(FLD_SURNAME);
        String firstName = getFieldValue(FLD_FIRST_NAME);

        if(surname.length() > 0)
            ret = surname;
        if(firstName.length() > 0)
        {
            if(ret.length() > 0)
                ret += ", ";
            ret += firstName;
        }
        return ret;
    }
}