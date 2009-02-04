//==============================================================================
// MonthCalendarTabPane.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.mdn.admin;

import java.util.Date;
import java.util.Vector;
import wsl.fw.util.Log;
import wsl.fw.gui.WslTabChildPanel;
import wsl.fw.gui.WslButton;
import wsl.fw.gui.GuiManager;
import wsl.fw.gui.GuiConst;
import wsl.fw.gui.OkCancelPanel;
import wsl.fw.datasource.DataSourceException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Color;
import wsl.fw.resource.ResId;
import wsl.fw.help.HelpId;
import wsl.mdn.common.MdnAdminConst;
import wsl.mdn.dataview.Scheduling;
import wsl.mdn.dataview.CalendarEvent;
import wsl.mdn.dataview.MonthEventNode;
import wsl.mdn.dataview.DayEventNode;

import pv.jfcx.JPVCalendar;
import pv.util.CalDay;

//--------------------------------------------------------------------------
/**
 * It is to show and maintain Schedulings.
 * There are elements:
 * - Big Calendar Control. Each cell shows list of scheduled Data Transfers
 * - View Day button to show Day View Panel for selected day
 * - Schedule button to set up new Transfer Scheduling
 */
public class MonthCalendarTabPane extends WslTabChildPanel
    implements ActionListener
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:35:35 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/admin/MonthCalendarTabPane.java $ ";

    // resources
    public static final ResId TITLE_MONTH_CALENDAR  = new ResId("MonthCalendarTabPane.title.MonthCalendar");
    public static final ResId BUTTON_SCHEDULE  = new ResId("MonthCalendarTabPane.button.Schedule");
    public static final ResId BUTTON_VIEW_DAY  = new ResId("MonthCalendarTabPane.button.ViewDay");

    // help id
    public final static HelpId HID_MONTH_CALENDAR = new HelpId("mdn.admin.MonthCalendarTabPane");

    // attributes
    int _dataTransferId;
    MonthEventNode _monthEventNode;
    MaintainTransferSchedulePanel _mtspParent;

    // controls
    private WslButton _btnSchedule      = new WslButton(BUTTON_SCHEDULE.getText(), this);
    private WslButton _btnViewDay       = new WslButton(BUTTON_VIEW_DAY.getText(), this);
    private WslButton _buttons[] = { _btnSchedule, _btnViewDay };

    private JPVCalendar _monthCalendar = new JPVCalendar();

    //--------------------------------------------------------------------------
    /**
     * This constructor creates MonthCalendarTabPane for dataTransferId.
     */
    public MonthCalendarTabPane(MaintainTransferSchedulePanel mtspParent, int dataTransferId)
    {
        // call base class constructor to set the title
        super(TITLE_MONTH_CALENDAR.getText());

        _mtspParent = mtspParent;

        // store ref to parent panel
        _dataTransferId = dataTransferId;

        // add controls
        createControls();

        // built Calendar Events
        buildCalendarEvents();
    }

    // accessors


    //--------------------------------------------------------------------------
    // accessors
    public int getDataTransferId()
    {
        return _dataTransferId;
    }
    //--------------------------------------------------------------------------
    /**
     * Create the JPVCalendar control.
     */
    private void createControls()
    {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets.top = GuiConst.DEFAULT_INSET;
        gbc.insets.left = GuiConst.DEFAULT_INSET;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        gbc.insets.right = GuiConst.DEFAULT_INSET;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        _monthCalendar.setShadow(10);
        _monthCalendar.setCalendarColor(Color.white);
        _monthCalendar.setCellBorderWidth(7);
        _monthCalendar.setBackground(Color.white);
        _monthCalendar.setCustomForeground(Color.white);

        add(_monthCalendar, gbc);
        _monthCalendar.addActionListener(this);
    }

    //--------------------------------------------------------------------------
    /**
     * Get the buttons that this tab wants to display in the parent's button
     * panel. This is called each time the tab child panel is selected.
     * @return an array of buttons, may be empty, not null.
     */
    public WslButton[] getButtons()
    {
        return _buttons;
    }

    //--------------------------------------------------------------------------
    public void buildCalendarEvents(int dataTransferId)
    {
        // clear Custom Dates in _monthCalendar
        Vector v = _monthCalendar.getCustomDates();
        int size = (v == null)? 0:v.size();
        for (int i = 0; i < size; i++)
            _monthCalendar.removeCustomDateAt(0);

        int month = _monthCalendar.getMonth();
        int year = _monthCalendar.getYear();
        int previosMonth = (month == 0)?11:month-1;
        int previosYear = (month == 0)?year-1:year;
        int nextMonth = (month == 11)?0:month+1;
        int nextYear = (month == 11)?year+1:year;
        addCalendarEventsOneMonth(dataTransferId, previosMonth, previosYear);
        addCalendarEventsOneMonth(dataTransferId, month, year);
        addCalendarEventsOneMonth(dataTransferId, nextMonth, nextYear);
    }

    //--------------------------------------------------------------------------
    public void buildCalendarEvents()
    {
        buildCalendarEvents(_dataTransferId);
        _mtspParent.selectDataTransfer(_dataTransferId);
    }

    //--------------------------------------------------------------------------
    public void addCalendarEventsOneMonth(int dataTransferId, int month, int year)
    {

        // get month events
        //int month = _monthCalendar.getMonth();
        //int year = _monthCalendar.getYear();
        try
        {
            _monthEventNode = CalendarEvent.getMonthEvents(month+1, year, dataTransferId);
        }
        catch (Exception e)
        {
        }

        // add events to calendar cells
        Vector days = _monthEventNode.getDayEventNodes();
        for (int i = 0; i < days.size(); i++)
        {
            DayEventNode day = (DayEventNode)days.get(i);
            Date date = day.getDate();
            Vector events = day.getEvents();
            for (int j = 0; j < events.size(); j++)
            {
                CalendarEvent event = (CalendarEvent)events.get(j);
                CalDay calDay = _monthCalendar.calDay(_monthCalendar.pack(date));
                if (calDay == null)
                    _monthCalendar.addCustomDate(date, event.getColor(), event.getDataTransfer().getName());
                else
                {
                    String str = (String)calDay.m_object;
                    str += "\n" + event.getDataTransfer().getName();
                    calDay.m_object = str;
                }
            }
        }

        _monthCalendar.repaint();
    }

    //--------------------------------------------------------------------------
    /**
     * Handle actions from buttons.
     */
    public void actionPerformed(ActionEvent event)
    {
        try
        {
            // switch on source button
            if(event.getSource().equals(_btnSchedule))
                onSchedule();
            else if(event.getSource().equals(_btnViewDay))
                onViewDay();
            else if(event.getSource().equals(_monthCalendar))
                onMonthCalendar(event);
        }
        catch(Exception e)
        {
            GuiManager.showErrorDialog(this, MdnAdminConst.ERR_UNHANDLED.getText(), e);
            Log.error(MdnAdminConst.ERR_UNHANDLED.getText(), e);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Notification function called each time the tab is selected or deselected.
     * @param selected, true if being selected, false if being deselected.
     */
    public void onSelected(boolean selected)
    {
        Log.debug("MonthCalendarTabPane.onSelected");
        synchronized (this)
        {
            // update all buttons
            updateButtons();
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Called by the framework to cause subclass panels to update the state
     * of controls.
     */
    public void updateButtons()
    {
        // flags

        // enable
    }

    //--------------------------------------------------------------------------
    /**
     * onSchedule.
     */
    private void onSchedule() throws DataSourceException
    {
        Scheduling scheduling = new Scheduling();
        scheduling.setStartDate(_monthCalendar.getDate());
        OkCancelPanel panel = GuiManager.openOkCancelPanel(_mtspParent.getFrameParent(), scheduling, true);
        if(panel.isOk())
        {
            _dataTransferId = scheduling.getDataTransferId();
            buildCalendarEvents(_dataTransferId);
            _mtspParent.selectDataTransfer(_dataTransferId);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Panel closing
     */
    public void onClosePanel()
    {
        Log.debug("MonthCalendarTabPane.onClosePanel");

        super.onClosePanel();
    }

    //--------------------------------------------------------------------------
    /**
     * Creates DayCalendarPanel for selected day.
     */
    private void onViewDay()
    {
        Date d = _monthCalendar.getDate();
        if(d != null)
        {
            DayCalendarPanel dcp = new DayCalendarPanel(d);
            GuiManager.openWslPanel(_mtspParent.getFrameParent(),
                dcp, true);
            buildCalendarEvents();
        }
    }

    //--------------------------------------------------------------------------
    private void onMonthCalendar(ActionEvent e)
    {
        int iMod = e.getModifiers();
        if(iMod == pv.jfcx.JPVCalendar.MONTH_YEAR)
        {
            buildCalendarEvents();
            _mtspParent.selectDataTransfer(_dataTransferId);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * If the subclass has help override this to specify the HelpId.
     * This help is displayed using the parent wizards's help button.
     * @return the HelpId of the help to display, if null the help button is not
     *   displayed.
     */
    public HelpId getHelpId()
    {
        return HID_MONTH_CALENDAR;
    }
}

//==============================================================================
// end of file MonthCalendarTabPane.java
//==============================================================================
