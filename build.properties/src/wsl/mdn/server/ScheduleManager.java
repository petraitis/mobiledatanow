//==============================================================================
// ScheduleManager.java
// Copyright (c) 2001 WAP Solutions Ltd.
//==============================================================================

package wsl.mdn.server;

import java.util.Date;
import java.util.Calendar;
import java.util.Vector;

import com.framedobjects.dashwell.utils.XmlFormatter;

import wsl.fw.datasource.DataSourceException;
import wsl.fw.util.Log;
import wsl.mdn.dataview.CalendarEvent;
import wsl.mdn.dataview.DayEventNode;
import wsl.mdn.dataview.DataTransfer;
import wsl.mdn.dataview.NoJoinTransferStrategy;
import wsl.mdn.dataview.ResultWrapper;

//------------------------------------------------------------------------------
/**
 * Manager process to execute scheduled tasks.
 */
public class ScheduleManager implements Runnable
{
    private int      _sleepTime;
    private Calendar _lastCheck = Calendar.getInstance();

    //--------------------------------------------------------------------------
    /**
     * Constructor.
     * @param sleepTime, the time in MS between checks for scheduled operations.
     */
    public ScheduleManager(int sleepTime)
    {
        _sleepTime = sleepTime;
    }

    //--------------------------------------------------------------------------
    /**
     * Run function for schedule manager.
     * Responds to interrupt by exiting.
     */
    public void run()
    {
        try
        {
            // perform initialisation
            init();

            // enter loop polling schedules, checking and executing
            while (true)
            {
                // poll
                poll();

                // sleep util next check
                Thread.sleep(_sleepTime);
            }
        }
        catch (InterruptedException e)
        {
        }
        finally
        {
            // perform cleanup
            cleanup();
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Called on startup to proeform any initialisation.
     */
    private void init()
    {
        Log.log("ScheduleManager.init");
        // nothing to do
    }

    //--------------------------------------------------------------------------
    /**
     * Called every shedule check period to check for scheduled operations.
     */
    private void poll()
    {
        Log.debug("ScheduleManager.poll");

        // get the current time.
        Calendar now = Calendar.getInstance();

        // check for a period that extends past one day, and clip if reqd
        if (!isSameDay(_lastCheck, now))
        {
            // day rollover, clip to end of previous day
            now = (Calendar) _lastCheck.clone();
            advanceToLastMilliOfDay(now);
            
            checkLicense();
        }
        
        try
        {
            // process for the period
            processPeriod(_lastCheck, now);
        }
        catch (Exception e)
        {
            Log.error("ScheduleManager.processPeriod", e);
        }

        // advance and set the last check time
        advanceOneMilli(now);
        _lastCheck = now;
    }

    //--------------------------------------------------------------------------
    /**
     * Called on shutdown toperform any cleanup.
     */
    private void cleanup()
    {
        Log.log("ScheduleManager.cleanup");
        // nothing to do
    }

    //--------------------------------------------------------------------------
    /**
     * Compare two Calendars to see if they are the same year and day
     */
    public static boolean isSameDay(Calendar dt1, Calendar dt2)
    {
        return (dt1.get(Calendar.YEAR) == dt2.get(Calendar.YEAR))
            && (dt1.get(Calendar.DAY_OF_YEAR) == dt2.get(Calendar.DAY_OF_YEAR));
    }

    //--------------------------------------------------------------------------
    /**
     * Increment the calendar to the last milli of the day. This means that
     * advanceOneMilli will incremnt to the first milli of the next day.
     */
    public static void advanceToLastMilliOfDay(Calendar cal)
    {
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
    }

    //--------------------------------------------------------------------------
    /**
     * Increment the  calendar by one milli.
     */
    public static void advanceOneMilli(Calendar cal)
    {
        cal.add(Calendar.MILLISECOND, 1);
    }

    //--------------------------------------------------------------------------
    /**
     * Execute all transfers in the specified period.
     */
    private void processPeriod(Calendar startPeriod, Calendar endPeriod)
        throws DataSourceException
    {
        // get the calendar events for the day
        DayEventNode den = CalendarEvent.getDayEvents(startPeriod.getTime());
        if (den != null)
        {
            Vector dayEvents = den.getEvents();

            // iterate selecting those within the range
            for (int i = 0; i < dayEvents.size(); i++)
            {
                CalendarEvent ce = (CalendarEvent) dayEvents.get(i);

                if ((ce.getDateTime().compareTo(startPeriod.getTime()) >= 0)
                    && (ce.getDateTime().compareTo(endPeriod.getTime()) <= 0))
                {
                    // in thtype filter texte range, get data transfer
                    DataTransfer dt = ce.getDataTransfer();

                    Log.log("Executing DataTransfer " + dt.getName());

                    // create the transfer strategy
                    NoJoinTransferStrategy ts = new NoJoinTransferStrategy();

                    // execute
                    ts.executeTransfer(dt);
                    

                }
            }
        }
    
    }
    private void checkLicense(){
        //check license key validation
        //int daysLeft = LicenseManager.isLicenseValid ();
    	//if (daysLeft < 0)
    	//{
    		//update license
        	//String registeredEmailAddress = LicenseManager.getRegisteredEmailAddress();	
        	ResultWrapper resultWrapper = LicenseManager.updateUserLicense();
    		String errorMsg = resultWrapper.getErrorMsg();
    		if (errorMsg != null){
        		// invalid license, display error and exit
        		//String errorMsg = LicenseManager.getErrorDescription (daysLeft);
        		Log.log("License expired: " + errorMsg);
        		System.exit (1);    			
    		}
    		int daysLeft = LicenseManager.isLicenseValid();
    		if (daysLeft < 0){
    			errorMsg = LicenseManager.getErrorDescription (daysLeft);
        		Log.log("License expired: " + errorMsg);
        		System.exit (1);    			
    		}
    	//}        	
    }
}



//==============================================================================
// end of file ScheduleManager.java
//==============================================================================
