/*	$Id: MdnLicenceHtmlServlet.java,v 1.3 2004/10/26 23:14:38 tecris Exp $
 *
 *	Small license servlet to generate license keys
 *
 */
package wsl.licence;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Date;
import java.util.Calendar;
import java.text.MessageFormat;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import javax.mail.*;
import javax.mail.internet.*;

import java.sql.SQLException;

public class MdnLicenceHtmlServlet extends HttpServlet
{
    /*
     *	Configuration keys
     */
    private static final String
	ConfigDbDriver      = "dbDriver",
	ConfigDbUri         = "dbURI",
	ConfigDbUsername    = "dbUsername",
	ConfigDbPassword    = "dbPassword",

        ConfigSMTPHost      = "smtphost",
	ConfigMailFrom      = "mailfrom",
	ConfigMailSubject   = "mailsubject",
	ConfigMailBody      = "mailbody",

        ConfigLicExpiry     = "licenceexpires",
	ConfigLicUsers      = "licenceusers",

	ConfigHtml          = "htmltemplate",

	ReqClientKey        = "clientkey",
	ReqCompany          = "company",
	ReqAddress          = "address",
	ReqSuburb           = "suburb",
	ReqCity             = "city",
	ReqState            = "state",
	ReqCountry          = "country",
	ReqContact          = "contact",
	ReqEmail            = "email",
	ReqPhone            = "phone",
	ReqFax              = "fax";

    /*
    *	Configuration
    */
    private String _dbDriver, _dbUri, _dbUsername, _dbPassword;
    private String _smtpHost, _mailFrom, _mailSubject, _mailBody;
    private short _licUsers;
    private int _licExpireDays;
    private String _htmlPath;

    public void
    init (
     ServletConfig config)
 	throws ServletException
    {
	/*
 *	Grab setup from Context
	 */
	ServletContext context = config.getServletContext ();

        _dbDriver	= context.getInitParameter (ConfigDbDriver).trim ();
	_dbUri		= context.getInitParameter (ConfigDbUri).trim ();
	_dbUsername	= context.getInitParameter (ConfigDbUsername).trim ();
	_dbPassword	= context.getInitParameter (ConfigDbPassword).trim ();

        _smtpHost	= context.getInitParameter (ConfigSMTPHost).trim ();
	_mailFrom	= context.getInitParameter (ConfigMailFrom).trim ();
	_mailSubject	= context.getInitParameter (ConfigMailSubject).trim ();
	_mailBody	= context.getInitParameter (ConfigMailBody).trim ();

	_licUsers       = Short.parseShort (
                            context.getInitParameter (ConfigLicUsers).trim ());
	_licExpireDays	= Integer.parseInt (
                            context.getInitParameter (ConfigLicExpiry).trim ());
	_htmlPath	= context.getInitParameter (ConfigHtml).trim ();

	/*
	 *	Attempt load of supplied JDBC driver
	 */
        try
        {
            Class.forName (_dbDriver);
        }
        catch (ClassNotFoundException e)
        {
            throw new ServletException ("Cannot load db driver: " + _dbDriver);
        }
    }

    protected void
    doGet (
     HttpServletRequest req,
     HttpServletResponse resp)
	throws ServletException, IOException
    {
	doPost (req, resp);
}

    protected void
    doPost (
     HttpServletRequest req,
     HttpServletResponse resp)
	throws ServletException, IOException
    {
        /*
	 *	Get request parameters
	 */
        String
            clientKey	= req.getParameter (ReqClientKey).trim (),
            company     = req.getParameter (ReqCompany).trim (),
            address	= req.getParameter (ReqAddress).trim (),
            suburb	= req.getParameter (ReqSuburb).trim (),
            city	= req.getParameter (ReqCity).trim (),
            state	= req.getParameter (ReqState).trim (),
            country	= req.getParameter (ReqCountry).trim (),
            contact	= req.getParameter (ReqContact).trim (),
            email	= req.getParameter (ReqEmail).trim (),
            phone	= req.getParameter (ReqPhone).trim (),
            fax		= req.getParameter (ReqFax).trim ();

	/*
	 *	Create a licenceKey from the clientKey
	 */
        LicenceKey cKey = createLicenceKey (clientKey);
	if (cKey == null)
	{
            sendReportBack (resp, "ERR: Invalid Licence Key");
            return;
	}

	/*
	 *	Work out expiry date
	 */
        Calendar today = Calendar.getInstance ();
        today.add (Calendar.DAY_OF_MONTH, _licExpireDays);
        int expiryDate = 10000 * today.get (Calendar.YEAR)
                        + 100 * (today.get (Calendar.MONTH) + 1)
                        + today.get (Calendar.DAY_OF_MONTH);
        

	/*
	 *	generate an evaluation key
	 */
	ActivationKey aKey = generateEvaluationKey (cKey, expiryDate);
	if (aKey == null)
	{
            sendReportBack (resp, "Failed to generate activation key");
            return;
	}

	/*
	 *	create a new record in the db for the generation
	 */
	UserInfo info = new UserInfo ();
	info._coName	= company;
        info._userName	= contact;
        info._email	= email;
        info._phone	= phone;
        info._fax	= fax;
        info._address	= address;
        info._suburb	= suburb;
        info._city	= city;
        info._state	= state;
        info._country	= country;
        info._users	= _licUsers;
        info._expiry	= expiryDate;
	info._licenceKey    = cKey.toString ();
        info._activationKey = aKey.toString ();
     
        String activationKey = searchActivationKey (info._licenceKey);
        if (activationKey == null)
        {
            sendReportBack (resp, "Licence expired");
            return;            
        }
        else if (activationKey != null && !activationKey.equals(""))
        {
            sendJavaMail (activationKey, contact, email);
        }
        else if (activationKey != null && activationKey.equals("") && createDbRecord (info))
	{
            sendJavaMail (aKey, contact, email);
	} else
	{
            sendReportBack (resp, "Failed to record details");
            return;
	}

        sendReportBack (resp, "Success. Key has been sent to " + email);
    }

    /**
     * Creates and Validates Licence Key.
     */
    private LicenceKey
    createLicenceKey (
     String licenceKey)
    {
        if (licenceKey != null && licenceKey.length () > 0)
        {
            try
            {
                LicenceKey key = new LicenceKey (licenceKey);
                if (key.verifyChecksum ())
                    return key;
            } catch (Exception e)
            {
            }
        }
        return null;
    }

    /**
     * Generates an evaluation key
     */
    private ActivationKey
    generateEvaluationKey (
     LicenceKey lKey,
     int expiryDate)
    {
        ActivationKey aKey = new ActivationKey(
				lKey.getProductCode (),
                               	_licUsers,
				expiryDate, lKey);
        if ((aKey == null) || (aKey.toString().length() == 0))
           	return null;
        return aKey;
    }

    /**
     * Puts user information to a database.
     */
    private boolean
    createDbRecord (
     UserInfo user)
    {
        try
        {
            Connection dbConx = getConnection ();
            
            user.saveDatabase (dbConx);
            dbConx.close();

        } catch (SQLException e)
        {
            System.out.println ("ERR: " + e.getMessage ());
            e.printStackTrace ();
            return false;
        }
            return true;
    }
    
    /**
     * Checks if the specified key exists in the database and returns:
     * an empty string if no key found in the database,
     * the activation key if the key is present in the database
     * null if the key is present and the expiry date is in the past
     *
     */
    private String 
    searchActivationKey (
     String customerKey)
    {
        try
        {
        Statement stmt = getConnection ().createStatement();
        String query = "SELECT fld_licence_key, fld_expiry, fld_activation_key FROM tbl_customer_licence " +
            "where fld_licence_key='" + customerKey + "'";
        ResultSet rs = stmt.executeQuery(query);
        Date expiryDate = null;
        String activationKey = null;
        java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
        if (rs.next())
        {
            expiryDate = rs.getDate("fld_expiry");
            activationKey = rs.getString("fld_activation_key");
        }
        if (expiryDate == null)
            return "";
        else if (expiryDate != null && expiryDate.compareTo(today) > 0)
            return activationKey;
        else if (expiryDate != null && expiryDate.compareTo(today) < 0)
            return null;
        } catch (SQLException sqlex)
        {
            sqlex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Returns a connection to the database
     */
    private Connection
    getConnection ()
        throws SQLException
    {
        /*
         *	We always make a new connection so that if the
         *	db goes down and comes back up, we don't end up
         *	with a bad cached connection
         */
        return DriverManager.getConnection (
		_dbUri,
		_dbUsername,
		_dbPassword);
    }
    
    private void 
    sendJavaMail (
     String keyStr,
     String contact,
     String sendTo)
    {
        // read letter template file
        String template = readTemplate (_mailBody);
        if (template == null)
            return;

        /*
	 *	Substitue Contact, Evaluation Key, Expiry and Concurent Users
	 *	to the template
	 */
        Object[] arguments =
            {
		contact,
                keyStr,
		"" + _licExpireDays,
		"" + _licUsers
		};
        MessageFormat mf = new MessageFormat(template);
        String message = mf.format(arguments);

        try
        {
            String text = "";
            String name = "";

            Properties props = System.getProperties();
			props.put ("mail.smtp.host", _smtpHost);

            // Get a Session object
            Session session = Session.getDefaultInstance (props, null);

            // construct the message
            Message msg = new MimeMessage(session);
			msg.setFrom (new InternetAddress (_mailFrom));

            msg.setRecipients(
				Message.RecipientType.TO,
				InternetAddress.parse (sendTo, false));

            msg.setSubject (_mailSubject);
            msg.setText (message);

            msg.setSentDate (new Date());

            // send the thing off
            Transport.send(msg);

        } catch (AddressException e)
        {
            e.printStackTrace();

        } catch (MessagingException e)
        {
            e.printStackTrace();
        }
        
    }

    /**
     * Send email using JavaMail.
     */
    private void
    sendJavaMail (
     ActivationKey aKey,
     String contact,
     String sendTo)
    {
        sendJavaMail (aKey.toString (), contact, sendTo);
    }

    /**
     * Reads template file.
     */
    private String
    readTemplate (
     String path)
     {
        try
        {
            BufferedReader reader = new BufferedReader (new FileReader (path));
            String line = null;
            String template = "";

            while ((line = reader.readLine()) != null)
                template += line + "\n";

            reader.close();

            return template;
        } catch (IOException e)
        {
            System.err.println ("ERR: " + e.getMessage ());
            e.printStackTrace ();
        }
	return null;
     }

    private void
    sendReportBack (
     HttpServletResponse response,
     String message)
        throws IOException
     {
        String title = "Evaluation Key Report";

        // set content type and other response header fields first
            response.setContentType("text/html");

        // then write the data of the response
        PrintWriter out = response.getWriter ();

        out.println("<HTML><HEAD><TITLE>" + title + "</TITLE></HEAD><BODY>");
        out.println("<img src='images/mdnlogo.jpg'/>");
        out.println("<H1>Mobile Data Now: " + title + "</H1>");
        out.println("<P>" + message + "</P>");
        out.println("</P></BODY></HTML>");
        out.close();
     }
}
