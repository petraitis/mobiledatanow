package wsl.fw.exception;

/**
 * @author julie
 *
 */
public class MdnExceptionItem implements java.io.Serializable
{
    private static final String Copyright = "Copyright (C) 2006 Firetrust Limited";
    private String errorMessage;
    private int errorCode;
    private String errorFieldName;
    private int errorType;

    /**
     * MdnExceptionItem error code only constructor.
     * 
     * @param inErrorCode int error code.
     */
    public MdnExceptionItem ( int inErrorCode )
    {
        errorMessage = "MDN Exception Item";
        errorCode = inErrorCode;
        errorType = MdnException.UnknownType;
    }

    /**
     * MdnExceptionItem error message only constructor.
     * 
     * @param inErrorMessage String error message.
     */
    public MdnExceptionItem ( String inErrorMessage )
    {
        errorMessage = inErrorMessage;
        errorCode = -1;
        errorType = MdnException.UnknownType;
    }

    /**
     * MdnExceptionItem generic error type constructor.
     * 
     * @param inErrorMessage String error message.
     * @param inErrorCode int error code.
     */
    public MdnExceptionItem ( String inErrorMessage, int inErrorCode )
    {
        errorMessage = inErrorMessage;
        errorCode = inErrorCode;
        errorType = MdnException.UnknownType;
    }

    /**
     * MdnExceptionItem no filed name constructor.
     * 
     * @param inErrorMessage String error message.
     * @param inErrorCode int error code.
     * @param inErrorType int error type.
     */
    public MdnExceptionItem ( String inErrorMessage, int inErrorCode, 
                                    int inErrorType )
    {
        errorMessage = inErrorMessage;
        errorCode = inErrorCode;
        errorType = inErrorType;
    }

    /**
     * MdnExceptionItem full constructor.
     * 
     * @param inErrorMessage String error message.
     * @param inErrorCode int error code.
     * @param inErrorFieldName String error field name.
     * @param inErrorType int error type.
     */
    public MdnExceptionItem ( String inErrorMessage, int inErrorCode, 
                                    String inErrorFieldName, int inErrorType )
    {
        errorMessage = inErrorMessage;
        errorCode = inErrorCode;
        errorFieldName = inErrorFieldName;
        errorType = inErrorType;
    }

    /**
     * Gets the error code
     * 
     * @return int.
     */
    public int getErrorCode ()
    {
        return errorCode;
    }

    /**
     * Gets the error message.
     * 
     * @return java.lang.String
     */
    public String getErrorDescription ()
    {
        return errorMessage;
    }

    /**
     * Gets the error type.
     * 
     * @return int
     */
    public int getErrorType ()
    {
        return errorType;
    }

    /**
     * Gets the field name.
     * 
     * @return java.lang.String
     */
    public String getFieldName ()
    {
        return errorFieldName;
    }

    /**
     * Sets the error code.
     * 
     * @param inErrorCode int error code.
     */
    public void setErrorCode ( int inErrorCode )
    {
        this.errorCode = inErrorCode;
    }

    /**
     * Sets the error message.
     * 
     * @param inErrorMessage String error code.
     */
    public void setErrorMessage ( String inErrorMessage )
    {
        this.errorMessage = inErrorMessage;
    }

    /**
     * Sets the error type.
     * 
     * @param inErrorType int error type.
     */
    public void setErrorType ( int inErrorType )
    {
        this.errorType = inErrorType;
    }

    /**
     * Sets the field name.
     * 
     * @param inErrorFieldName String error field name.
     */
    public void setFieldName ( String inErrorFieldName )
    {
        this.errorFieldName = inErrorFieldName;
    }

    /**
     * Outputs the class contents as a string.
     * @return Required value
     */
    public String toString ()
    {
        String output = "    Item Error Code : " + errorCode + "\r\n";

        if ( errorFieldName != null )
        {
            output = output + "    Item Error Field Name : " + 
                     errorFieldName + "\r\n";
        }

        output = output + "    Item Error Type : " + errorType + "\r\n";
        output = output + "    Item Error Message : " + errorMessage + 
                 "\r\n";

        return output;
    }
}
