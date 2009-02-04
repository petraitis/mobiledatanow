//==============================================================================
// DayCalendarPanel.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.mdn.admin;

// imports
import java.util.Date;
import java.util.Vector;
import java.util.Calendar;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.Dimension;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.resource.ResId;
import wsl.fw.help.HelpId;
import wsl.fw.datasource.DataSourceException;
import wsl.fw.gui.GuiManager;
import wsl.fw.gui.OkCancelPanel;
import wsl.fw.gui.WslButtonPanel;
import wsl.fw.gui.WslButton;
import wsl.fw.gui.GuiConst;
import wsl.mdn.dataview.CalendarEvent;
import wsl.mdn.dataview.DayEventNode;
import wsl.mdn.dataview.Scheduling;
import pv.jfcx.JPVDay;
import pv.util.Appointment;


//------------------------------------------------------------------------------
/**
 * Shows CalendarEvents for a day
 */
public class DayCalendarPanel extends WslButtonPanel
    implements ActionListener
{
    //--------------------------------------------------------------------------
    // resources

    public static final ResId PANEL_TITLE  =
        new ResId("DayCalendarPanel.panel.Title");
    public static final ResId BUTTON_CLOSE  =
        new ResId("DayCalendarPanel.btn.Close");
    public static final ResId BUTTON_SCHEDULE  =
        new ResId("DayCalendarPanel.btn.Schedule");
    public static final ResId BUTTON_HELP =
        new ResId("OkPanel.button.Help");
    public static final ResId ERR_LOAD_EVENTS =
        new ResId("DayCalendarPanel.error.loadCalendarEvents");

    public final static HelpId HID_DAYCALENDAR = new HelpId("mdn.admin.DayCalendarPanel");

    //--------------------------------------------------------------------------
    // constants

    private static final int APP_LENGTH = 30;
    private static final int ROW_HEIGHT = 16;
    private static final int START_TIME = 0;
    private static final int END_TIME = 1440;


    //--------------------------------------------------------------------------
    // attributes

    private JPVDay _jpvDay = new JPVDay();
    private boolean _isBuilding = false;


    //--------------------------------------------------------------------------
    // controls

    private WslButton _btnSchedule = new WslButton(BUTTON_SCHEDULE.getText(), this);
    private WslButton _btnClose = new WslButton(BUTTON_CLOSE.getText(), this);


    //--------------------------------------------------------------------------
    // construction

    /**
     * Ctor taking a DayEventNode
     * @param date the day to show events for
     */
    public DayCalendarPanel(Date date)
    {
        // super
        super(WslButtonPanel.VERTICAL);

        this.setPanelTitle(PANEL_TITLE.getText());

        // init panel
        initDayCalendarPanelControls();

        // set the initial date of the day control
        _jpvDay.setDate(date);

        // build calendar events
        buildCalendarEvents();
    }

    /**
     * Init panel
     */
    private void initDayCalendarPanelControls()
    {
        // buttons
        addButton(_btnSchedule);

        // custom buttons
        addHelpButton(BUTTON_HELP.getText(), HID_DAYCALENDAR, -1, true);
        _btnClose.setIcon(Util.resourceIcon(GuiConst.FW_IMAGE_PATH + "close.gif"));
        addCustomButton(_btnClose);

        // main panel
        getMainPanel().setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // day control
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        getMainPanel().add(_jpvDay, gbc);
        _jpvDay.setStartTime(START_TIME);
        _jpvDay.setEndTime(END_TIME);
        _jpvDay.setRowHeight(ROW_HEIGHT);
        _jpvDay.addActionListener(this);
    }


    //--------------------------------------------------------------------------
    // Calendar Events

    /**
     * Build the CalendarEvents into the day control
     */
    private void buildCalendarEvents()
    {
        // set is building flag
        _isBuilding = true;

        // clear the day control
        Vector apps = _jpvDay.getAppointments();
        int size = (apps == null)? 0: apps.size();
        for(int i = 0; i < size; i++)
            _jpvDay.delete(0);

        // get the DayEventNode
        try
        {
            DayEventNode den = CalendarEvent.getDayEvents(_jpvDay.getDate());
            if(den != null)
            {
                // iterate the CalendarEvents
                Appointment app;
                CalendarEvent ce;
                Vector ces = den.getEvents();
                for(int i = 0; ces != null && i < ces.size(); i++)
                {
                    // get the calendar event
                    ce = (CalendarEvent)ces.elementAt(i);
                    if(ce != null)
                    {
                        // create a new appointment
                        app = new Appointment();
                        app.m_length = APP_LENGTH;
                        app.m_color = ce.getColor();
                        app.m_time = ce.getDateTime().getTime();
                        app.m_text = ce.getDataTransfer().getName();
                        _jpvDay.add(app, false);
                    }
                }
            }
        }
        catch(Exception e)
        {
            wsl.fw.gui.GuiManager.showErrorDialog(this, ERR_LOAD_EVENTS.getText(), e);
        }

        // clear building flag
        _isBuilding = false;
    }


    //--------------------------------------------------------------------------
    // actions

    /**
     * Action performed
     */
    public void actionPerformed(ActionEvent ev)
    {
        try
        {
            // switch on source
            if(ev.getSource() == _btnClose)
                closePanel();
            else if(ev.getSource() == _btnSchedule)
                onSchedule();
            else if(ev.getSource() == _jpvDay && !_isBuilding)
                buildCalendarEvents();

            // update buttons
            updateButtons();
        }
        catch(Exception e)
        {
            wsl.fw.gui.GuiManager.showErrorDialog(this,
                wsl.mdn.common.MdnAdminConst.ERR_UNHANDLED.getText(), e);
            Log.error(wsl.mdn.common.MdnAdminConst.ERR_UNHANDLED.getText(), e);
        }
    }

    /**
     * Schedule button clicked
     */
    private void onSchedule() throws Exception
    {
        // create a new scheduling
        Scheduling sched = new Scheduling();
        int index = _jpvDay.m_focus;
        if(index >= 0)
        {
            // set the time
            Date time = new Date(_jpvDay.getTimeAt(index));
            sched.setStartDate(time);

            // open scheduling prop panel

            OkCancelPanel panel = GuiManager.openOkCancelPanel(
                this.getFrameParent(), sched, true, true);

            // rebuild the day calendar
            if(panel.isOk())
                buildCalendarEvents();
        }
    }


    //--------------------------------------------------------------------------
    // misc

    /**
     * Enable disable controls
     */
    public void updateButtons()
    {
    }

    /**
     * Preferred size
     */
    public Dimension getPreferredSize()
    {
        return new Dimension(500, 500);
    }
}
