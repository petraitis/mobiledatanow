//Source file: D:\dev\wsl\fw\datasource\Join.java

package wsl.fw.datasource;


/**
 * Class to represent the join information between 2 Entities
 */
public interface Join
{
    //--------------------------------------------------------------------------
    // constants

    public static final String JT_INNER = "INNER JOIN";
    public static final String JT_LEFT_OUTER = "LEFT OUTER JOIN";
    public static final String JT_RIGHT_OUTER = "RIGHT OUTER JOIN";


    //--------------------------------------------------------------------------
    // accessors

    /**
     * Get the left entity
     * @return String the left entity
     */
    public String getLeftEntity();

    /**
     * Set the left entity
     * @param val the left entity
     */
    public void setLeftEntity(String val);

    /**
     * Get the left key
     * @return String the left key
     */
    public String getLeftKey();

    /**
     * Set the left key
     * @param val the left key
     */
    public void setLeftKey(String val);

    /**
     * Get the right entity
     * @return String the right entity
     */
    public String getRightEntity();

    /**
     * Set the right entity
     * @param val the right entity
     */
    public void setRightEntity(String val);

    /**
     * Get the right key
     * @return String the right key
     */
    public String getRightKey();

    /**
     * Set the right key
     * @param val the right key
     */
    public void setRightKey(String val);

    /**
     * Get the join type
     * @return String the join type
     */
    public String getJoinType();

    /**
     * Set the join type
     * @param val the join type
     */
    public void setJoinType(String val);
}
