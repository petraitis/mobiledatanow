
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
 * @author
 * @version 1.0
 */
package wsl.fw.report;

// imports
import pv.jfcx.JPVDate;

/**
 * Displays the current date for a report
 */
public class CurrentDate extends TextElement
{
    //--------------------------------------------------------------------------
    // attributes
    private JPVDate _date;


    //--------------------------------------------------------------------------
    // init

    /**
     * Argument ctor
     * @param pos the position of the element
     */
    public CurrentDate(WslPos pos)
    {
        // init the date
        _date = new JPVDate();
        _date.setUseLocale(true);
        _date.setCurrentDate(true);

        // set text and position
        setText(_date.getText());
        setPosition(pos);
    }
}