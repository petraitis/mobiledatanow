package wsl.fw.msgserver;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c)
 * Company:
 * @author
 * @version 1.0
 */

public class Contact extends Message
{
    //--------------------------------------------------------------------------
    // attributes

    public String _name = "";
    public String _firstName = "";
    public String _surname = "";
    public String _company = "";
    public String _email = "";
    public String _busTel = "";
    public String _homeTel = "";
    public String _mobTel = "";
    public String _busFax = "";
    public String _busStreet = "";
    public String _busCity = "";
    public String _busProvince = "";
    public String _busCountry = "";


    //--------------------------------------------------------------------------
    // construction

    /**
     * Param ctor
     * @param name the name of the contact
     */
    public
	Contact (
	 String type,
	 String name,
	 String firstName,
	 String surname,
	 String company,
	 String email,
	 String busTel,
	 String homeTel,
	 String mobTel,
	 String busFax,
	 String busStreet,
	 String busCity,
	 String busProvince,
	 String busCountry)
    {
        super(name, email, Message.MT_CONTACT);
        _name = name;
        _firstName = firstName;
        _surname = surname;
        _company = company;
        _email = email;
        _busTel = busTel;
        _homeTel = homeTel;
        _mobTel = mobTel;
        _busFax = busFax;
        _busStreet = busStreet;
        _busCity = busCity;
        _busProvince = busProvince;
        _busCountry = busCountry;
    }
}