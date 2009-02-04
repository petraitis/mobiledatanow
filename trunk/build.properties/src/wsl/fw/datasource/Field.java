/**	$Header: /wapsolutions/cvsroot/mdn/fw/src/wsl/fw/datasource/Field.java,v 1.1.1.1 2002/06/11 23:11:42 jonc Exp $
 *
 * Interface representing a field in a DataObject
 * Has name type and flags
 *
 */
package wsl.fw.datasource;


/**
 */
public interface Field extends Comparable
{
    //--------------------------------------------------------------------------
    // constants

    // field type constants
    public static final int FT_STRING = 0;
    public static final int FT_INTEGER = 1;
    public static final int FT_DECIMAL = 2;
    public static final int FT_DATETIME = 3;
    public static final int FT_CURRENCY = 4;
    public static final int FT_BOOLEAN = 5;
    public static final int FT_UNKNOWN = 6;
    // field flags
    public static final int
		FF_NONE			= 0,
    	FF_UNIQUE_KEY	= 1,
    	FF_SYSTEM_KEY	= 2,
    	FF_NAMING		= 4,
    	FF_READ_ONLY	= 8,
		FF_PHONELINK	= 16,			// content can be called
		FF_LARGE		= 32;			// present TEXTAREA under HTML


    //--------------------------------------------------------------------------
    // operations

    /**
     * Returns the field name
     * @return String the field name
     * @roseuid 3973D61801DB
     */
    public String getName();

    /**
     * Sets the field name
     * @param val the value to set
     */
    public void setName(String val);

    /**
     * Returns the field type
     * @return int the field type
     * @roseuid 3973D6210288
     */
    public int getType();

    /**
     * Sets the field type
     * @param val the value to set
     */
    public void setType(int val);

    /**
     * Returns the field flags
     * @return int the field flags
     * @roseuid 3973D62E00A6
     */
    public int getFlags();

    /**
     * Sets the field flags
     * @param val the value to set
     */
    public void setFlags(int val);

    /**
     * @return the column size, the number of characters for string fields.
     */
    public int getColumnSize();

    /**
     * @param columnSize, the number of characters for string fields.
     */
    public void setColumnSize(int columnSize);

    /**
     * @return the number of decimal digits
     */
    public int getDecimalDigits();

    /**
     * @param n, the number of decimal digits in a number
     */
    public void setDecimalDigits(int n);

    /**
     * @return the native type of the field
     */
    public int getNativeType();

    /**
     * @param n, the native type of the field
     */
    public void setNativeType(int n);

    /**
     * returns true if the param flags are set
     * @param flag the flag to check
     * @return true if the param flags are set
     */
    public boolean hasFlag(int flag);
}
