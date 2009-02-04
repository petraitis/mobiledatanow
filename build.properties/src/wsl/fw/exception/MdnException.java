package wsl.fw.exception;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;

import wsl.fw.util.Validate;

public class MdnException extends Exception implements Serializable {
	
    protected Throwable initiatingException = null;
    private Vector<MdnExceptionItem> errorItems;
    
    /** An exception which includes the field/property which was involved */
    public static final int PropertyType = 1;

    /** An exception with just a description, and no field/property mentioned */
    public static final int LogicType = 2;

    /** Variable */
    public static final int ApplicationType = 3;

    /** Variable */
    public static final int UnknownType = 4;

	public MdnException(String msg) {
		super(msg);
        this.errorItems = new Vector();
	}

	public MdnException(String msg, Throwable ex) {
		super(msg);
        initiatingException = ex;
        this.errorItems = new Vector();
	}
    /**
     * Prints this Exception and its backtrace to the standard error stream.
     */
    public void printStackTrace ()
    {
        super.printStackTrace ();

        if (initiatingException != null)
        {
            System.err.println ("\nInitiating exception:");
            initiatingException.printStackTrace ();
        }
    }    
    /**
     * Prints this Exception and its backtrace to the input stream.
     *
     * @param stream PrintStream to dump the information to.
     */
    public void printStackTrace (java.io.PrintStream stream)
    {
        super.printStackTrace (stream);

        if (initiatingException != null)
        {
            stream.println ("\nInitiating exception:");
            initiatingException.printStackTrace (stream);
        }
    }    
    /**
     * Prints this Exception and its backtrace to the input print writer.
     *
     * @param writer PrintStream to dump the information to.
     */
    public void printStackTrace (java.io.PrintWriter writer)
    {
        super.printStackTrace (writer);

        if (initiatingException != null)
        {
            writer.println ("\nInitiating exception:");
            initiatingException.printStackTrace (writer);
        }
    }    
    /**
     * Adds a Logic exception to the Vector of items.
     * 
     * @param itemDescription String exception item description.
     */
    public void addLogicException ( String itemDescription )
    {
        MdnExceptionItem newExceptionItem = new MdnExceptionItem( 
                                                          itemDescription, -1, 
                                                          LogicType );
        this.errorItems.addElement ( newExceptionItem );
    }    
    /**
     * Gets the number of exception items.
     * @return Required value
     */
    public int getExceptionItemCount ()
    {
        return errorItems.size ();
    }
    
    /**
     * Gets the number of exception items.
     * @return Required value
     */
    public Vector getExceptionItems ()
    {
        return errorItems;
    }    
    /**
     * Outputs the class contents as a string.
     * @return String representation of the error
     */
    public String toString ()
    {
        String output = super.toString () + "\r\n";
        
        //output = output + "  Exception Error Code : " + errorCode + "\r\n";

        if (!errorItems.isEmpty ())
        {
            output = output + "  Exception Error Item(s) : \r\n";

            for ( int itemCount = 0;
                  itemCount < errorItems.size ();
                  itemCount++ )
            {
                output = output + 
                         ( (MdnExceptionItem) errorItems.elementAt ( 
                                   itemCount ) ).toString () + "\r\n";
            }
        }

        return output;
    }    
    /**
     * Outputs the class contents as an HTML string (containing <br /> etc)
     * @return String representation of the error
     */
    public String toHtmlString ()
    {
        // Extract the strings from the validation exception
        StringBuffer buffer = new StringBuffer();
        
        Vector errorItems = this.getExceptionItems ();
        int count = errorItems.size ();

        String msg = this.getMessage ();
        if (!Validate.isEmpty (msg))
        {
            buffer.append (msg.trim ());
            buffer.append ("<br />");
        }

        if (count > 0)
        {
            Iterator<MdnExceptionItem> errorIter = errorItems.iterator();
            while (errorIter.hasNext())
            {
                MdnExceptionItem item = (MdnExceptionItem)errorIter.next ();

                // See if the exception includes a field where the error occurred
                if (item.getErrorType () == MdnException.PropertyType)
                {
                    buffer.append ((item.getErrorDescription () + " - " + 
                                    item.getFieldName ()).trim ());
                }
                else
                {
                    buffer.append (item.getErrorDescription ().trim ());
                }
                buffer.append ("<br />");
            }
        }
        return buffer.toString();
    }

    /**
     * Returns the initiatingException.
     * @return Throwable
     */
    public Throwable getInitiatingException()
    {
        return initiatingException;
    }    
}
