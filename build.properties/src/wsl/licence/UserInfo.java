/*	$Id: UserInfo.java,v 1.3 2004/10/20 00:20:46 tecris Exp $
 */
package wsl.licence;

import java.io.*;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.sql.*;

public class UserInfo
{
    public String 
        _coName     = "",
        _userName   = "",
        _email      = "",
        _phone      = "",
        _fax        = "",
        _address    = "",
        _suburb     = "",
        _city       = "",
        _state      = "",
        _country    = "";
    public short _users;
    public int _expiry;
	public String _licenceKey;
    public String _activationKey;

    private static final String LINEFEED = "\n";
	private static final String ENC = "utf-8";

    public UserInfo()
    {
    }

	public void
	saveFile (
	 String filename)
		throws IOException
	{
        File f = new File(filename);
        FileWriter fw = new FileWriter(f);

        if (_licenceKey != null)
           fw.write("licenceKey: " + _licenceKey + LINEFEED);

        if (_coName != null)
           fw.write("coName: " + _coName + LINEFEED);

        if (_userName != null)
           fw.write("userName: " + _userName + LINEFEED);

        if (_email != null)
           fw.write("email: " + _email + LINEFEED);

        if (_phone != null)
           fw.write("phone: " + _phone + LINEFEED);

        if (_fax != null)
           fw.write("fax: " + _fax + LINEFEED);

        if (_address != null)
           fw.write ("address: " + URLEncoder.encode (_address, ENC) + LINEFEED);

		fw.write("users: " + _users + LINEFEED);

        if (_activationKey != null)
           fw.write("activationKey: " + _activationKey + LINEFEED);

        fw.close();
    }

    /**
     * Loads a registration file.
	 * Returns true if file is found and it contains at least a licence key.
     * Return is false if file does not exist.
     */
	public boolean
	load (
	 String filename)
    {
        File f = new File(filename);
        if (!f.exists())
           return false;

        _licenceKey = null;

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(f));

            String line;
            while (null != (line = br.readLine()))
            {
                if (line.length() > 0)
                {
                    int idx = line.indexOf(':');
                    if (idx > 0)
                    {
                        String title = line.substring(0, idx);
                        String val = line.length() > idx+2 ?  line.substring(idx+2) : "";
                        if (title.equals("licenceKey"))
                           _licenceKey = val;
                        else if (title.equals("coName"))
                           _coName = val;
                        else if (title.equals("userName"))
                           _userName = val;
                        else if (title.equals("email"))
                           _email = val;
                        else if (title.equals("phone"))
                           _phone = val;
                        else if (title.equals("fax"))
                           _fax = val;
                        else if (title.equals("address"))
                           _address = URLDecoder.decode (val, ENC);
                        else if (title.equals("users"))
                           _users = Short.parseShort (val);
                        else if (title.equals("activationKey"))
                           _activationKey = val;
                    }
                }
            }

            br.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }

        return _licenceKey != null;
    }

    /**
     * Puts user information to a database.
     */
    public void
	saveDatabase (
	 Connection db)
		throws SQLException
    {
		Statement stmt = db.createStatement ();
		String expiry = (_expiry == 0) ? "null" : "'" + _expiry + "'";
		String sql =
			"insert into tbl_customer_licence ("
				+ "fld_licence_key, "
				+ "fld_activation_key, "
				+ "fld_co_name, "
				+ "fld_address, "
				+ "fld_suburb, "
				+ "fld_city, "
				+ "fld_state, "
				+ "fld_country, "
				+ "fld_user_name, "
				+ "fld_email, "
				+ "fld_phone, "
				+ "fld_fax, "
				+ "fld_usercount, "
				+ "fld_expiry) "
			+ "values ("
				+ "'" + _licenceKey + "', "
				+ "'" + _activationKey + "', "
				+ "'" + _coName + "', "
				+ "'" + _address + "', "
				+ "'" + _suburb + "', "
				+ "'" + _city + "', "
				+ "'" + _state + "', "
				+ "'" + _country + "', "
				+ "'" + _userName + "', "
				+ "'" + _email + "', "
				+ "'" + _phone + "', "
				+ "'" + _fax + "', "
				+ _users + ", "
				+ expiry
				+ ")";
		stmt.execute (sql);
    }
}
