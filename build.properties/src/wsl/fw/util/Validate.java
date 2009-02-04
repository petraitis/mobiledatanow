package wsl.fw.util;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Useful utility methods for validating strings and numbers
 * 
 * @author Julie Bie
 */
public class Validate
{

    /**
     * Test to see if a String is null or empty, ie "" or all spaces
     * @param str String
     * @return boolean true if the String is empty
     */
    public static boolean isEmpty (String str)
    {
        return ((str == null) || (str.trim().equals("")));
    }

    /**
     * Test to see if a Collection is null or empty
     * @param list Collection
     * @return boolean true if the String is empty
     */
    public static boolean isEmpty (Collection list)
    {
        return ((list == null) || (list.isEmpty()));
    }

    /**
     * Validation BigDecimal number
     * eg. Decimal(12,3)
     * it should be bigger than 999999999
     */
    public static boolean validateDecimalNumber (BigDecimal decimalValue, int totalNumber, int scale)
    {
        
        //Check number is not bigger than required value
        int numberOfIntegerValue = totalNumber - scale ; //eg. decimal (12, 3) then it is 9
        String strMaxInt = new String ();
        for (int i = 0; i < numberOfIntegerValue; i++)
        {
            strMaxInt += "9";
        }
        if ( decimalValue.intValue() > Integer.parseInt(strMaxInt) ) // eg. decimal (12, 3), then int value is less than 9 digits
        {
            return false;            
        }
        return true;
    }
    
    public static boolean validateEmailAddress(String email){
        boolean valid = true;
    	//Input the string for validation
        //String email = "xyz@hotmail.com";

        //Set the email pattern string
        Pattern p = Pattern.compile(".+@.+\\.[a-z]+");

        //Match the given string with the pattern
        Matcher m = p.matcher(email);

        //check whether match is found 
        boolean matchFound = m.matches();

        if (matchFound){
          //System.out.println("Valid Email Id.");
        }else{
          System.out.println("Invalid Email Id.");  
          valid = false;
        }

        //String input = "@sun.com";
        //Checks for email addresses starting with
        //inappropriate symbols like dots or @ signs.
        Pattern p2 = Pattern.compile("^\\.|^\\@");
        Matcher m2 = p2.matcher(email);
        if (m2.find()){
           System.err.println("Email addresses don't start" +
                              " with dots or @ signs.");
           valid = false;
        }
        //Checks for email addresses that start with
        //www. and prints a message if it does.
        p2 = Pattern.compile("^www\\.");
        m2 = p2.matcher(email);
        if (m2.find()) {
          System.out.println("Email addresses don't start" +
                  " with \"www.\", only web pages do.");
          valid = false;
        }
        p2 = Pattern.compile("[^A-Za-z0-9\\.\\@_\\-~#]+");
        m2 = p2.matcher(email);
        StringBuffer sb = new StringBuffer();
        boolean result = m2.find();
        boolean deletedIllegalChars = false;

        while(result) {
           deletedIllegalChars = true;
           m2.appendReplacement(sb, "");
           result = m2.find();
        }

        // Add the last segment of input to the new String
        m2.appendTail(sb);

        //email = sb.toString();

        if (deletedIllegalChars) {
           System.out.println("It contained incorrect characters" +
                             " , such as spaces or commas.");
           valid = false;
        }        
        
        //EmailValidator emailValidator = EmailValidator.getInstance();
        //valid = emailValidator.isValid(email);
        
        return valid;
    }
        
}
