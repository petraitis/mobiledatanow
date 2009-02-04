package wsl.mdn.admin;

// imports
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;
import wsl.fw.util.Util;
import wsl.fw.gui.GuiConst;
import wsl.fw.gui.GuiManager;
import wsl.fw.gui.PropertiesPanel;
import wsl.fw.gui.WslTextField;
import wsl.fw.gui.WslComboBox;
import wsl.fw.resource.ResId;
import wsl.fw.datasource.DataManager;
import wsl.fw.datasource.DataSource;
import wsl.fw.datasource.Query;
import wsl.fw.datasource.QueryCriterium;
import wsl.fw.datasource.RecordSet;
import wsl.fw.security.Group;
import wsl.fw.resource.ResId;
import wsl.fw.help.HelpId;
import wsl.fw.util.Log;
import wsl.fw.util.Type;
import wsl.mdn.dataview.DataView;
import wsl.mdn.dataview.DataTransfer;
import wsl.mdn.dataview.Scheduling;
import pv.jfcx.JPVDatePlus;
import pv.jfcx.JPVTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;


//------------------------------------------------------------------------------
/**
 *
 */
public class SchedulingPropPanel extends PropertiesPanel implements ActionListener
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:35:35 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/admin/SchedulingPropPanel.java $ ";

    // resources
    public static final ResId RADIO_NONE  = new ResId("SchedulingPropPanel.radio.None");
    public static final ResId RADIO_MONTH  = new ResId("SchedulingPropPanel.radio.Month");
    public static final ResId RADIO_DAY  = new ResId("SchedulingPropPanel.radio.Day");
    public static final ResId RADIO_HOUR  = new ResId("SchedulingPropPanel.radio.Hour");
    public static final ResId RADIO_WEEK  = new ResId("SchedulingPropPanel.radio.Week");
    public static final ResId LABEL_DATATRANSFER  = new ResId("SchedulingPropPanel.label.DataTransfer");
    public static final ResId LABEL_STARTDATE  = new ResId("SchedulingPropPanel.label.StartDate");
    public static final ResId LABEL_STARTTIME  = new ResId("SchedulingPropPanel.label.StartTime");
    public static final ResId LABEL_REPEAT  = new ResId("SchedulingPropPanel.label.Repeat");
    public static final ResId LABEL_EVERY  = new ResId("SchedulingPropPanel.label.Every");
    public static final ResId LABEL_ENDDATE  = new ResId("SchedulingPropPanel.label.EndDate");
    public static final ResId LABEL_ENDTIME  = new ResId("SchedulingPropPanel.label.EndTime");
    public static final ResId VALIDATION_DATETIME  = new ResId("SchedulingPropPanel.validation.DateTime");
    public static final ResId VALIDATION_START_DATETIME  = new ResId("SchedulingPropPanel.validation.StartDateTime");
    public static final ResId ERR_DATATRANSFER_COMBO = new ResId("SchedulingPropPanel.error.dataTransferCombo");
    public final static HelpId HID_SCHEDULING = new HelpId("mdn.admin.SchedulingPropPanel");

    // controls
    private WslComboBox _cmbDataTransfer = new WslComboBox(200);
    private JPanel _panelRepeat = new JPanel();
    private JRadioButton _radioNone = new JRadioButton(RADIO_NONE.getText());
    private JRadioButton _radioMonth = new JRadioButton(RADIO_MONTH.getText());
    private JRadioButton _radioDay = new JRadioButton(RADIO_DAY.getText());
    private JRadioButton _radioHour = new JRadioButton(RADIO_HOUR.getText());
    private JRadioButton _radioWeek = new JRadioButton(RADIO_WEEK.getText());
    private WslTextField _txtRepeatCount        = new WslTextField(100);
    private JLabel _repeatType = new JLabel("");
    private JPVDatePlus _jpvStartDate = new JPVDatePlus();
    private JPVTime _jpvStartTime = new JPVTime();
    private JPVDatePlus _jpvEndDate = new JPVDatePlus();
    private JPVTime _jpvEndTime = new JPVTime();

    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public SchedulingPropPanel()
    {
        // init controls
        initControls();

        // build the Data Transfer combo
        buildDataTransferCombo();

        // update buttons
    }

    //--------------------------------------------------------------------------
    /**
     * Init the panel's controls.
     */
    private void initControls()
    {
        // set layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // data transfer
        JLabel lblGroup = new JLabel(LABEL_DATATRANSFER.getText());
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets.left = GuiConst.DEFAULT_INSET;
        gbc.insets.top = GuiConst.DEFAULT_INSET;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 0.2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(lblGroup, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        add(_cmbDataTransfer, gbc);

        // start date
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel(LABEL_STARTDATE.getText()), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        _jpvStartDate.setUseLocale(true);
        _jpvStartDate.setShowCentury(true);
        _jpvStartDate.setDialog(true);
        add(_jpvStartDate, gbc);

        // start time
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel(LABEL_STARTTIME.getText()), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        _jpvStartTime.setShowSeconds(false);
        _jpvStartTime.setTwelveHours(false);
        _jpvStartTime.setEnableArrows(true);
        add(_jpvStartTime, gbc);

        // repeat panel
        _panelRepeat.setBorder(BorderFactory.createTitledBorder(LABEL_REPEAT.getText()));
        _panelRepeat.setLayout(new GridBagLayout());
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weighty = 1;
        add(_panelRepeat, gbc);

        // repeat type
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets.left = GuiConst.DEFAULT_INSET;
        gbc2.insets.top = GuiConst.DEFAULT_INSET;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.weightx = 0.2;
        gbc2.gridx = 0;
        gbc2.gridy = 0;
        _panelRepeat.add(_radioNone, gbc2);
        gbc2.gridx = 1;
        _panelRepeat.add(_radioMonth, gbc2);
        gbc2.gridx = 2;
        _panelRepeat.add(_radioWeek, gbc2);
        gbc2.gridx = 3;
        _panelRepeat.add(_radioDay, gbc2);
        gbc2.gridx = 4;
        gbc2.gridwidth = GridBagConstraints.REMAINDER;
        _panelRepeat.add(_radioHour, gbc2);

        ButtonGroup bg = new ButtonGroup();
        bg.add(_radioNone);
        bg.add(_radioMonth);
        bg.add(_radioWeek);
        bg.add(_radioDay);
        bg.add(_radioHour);
        _radioNone.addActionListener(this);
        _radioMonth.addActionListener(this);
        _radioDay.addActionListener(this);
        _radioHour.addActionListener(this);
        _radioWeek.addActionListener(this);
        _radioNone.setSelected(true);

        // repeat count
        gbc2.gridy = 1;
        gbc2.gridx = 0;
        _panelRepeat.add(new JLabel(LABEL_EVERY.getText()), gbc2);
        gbc2.gridx = 1;
        gbc2.gridwidth = 2;
        _panelRepeat.add(_txtRepeatCount, gbc2);
        gbc2.gridx = 3;
        gbc2.gridwidth = GridBagConstraints.REMAINDER;
        _panelRepeat.add(_repeatType, gbc2);

        // end date
        gbc2.gridx = 0;
        gbc2.gridy = 2;
        _panelRepeat.add(new JLabel(LABEL_ENDDATE.getText()), gbc2);
        gbc2.gridx = 1;
        gbc2.gridwidth = GridBagConstraints.REMAINDER;
        _jpvEndDate.setUseLocale(true);
        _jpvEndDate.setShowCentury(true);
        _jpvEndDate.setDialog(true);
        _panelRepeat.add(_jpvEndDate, gbc2);

        // add end time control
        gbc2.gridx = 0;
        gbc2.gridy = 3;
        _panelRepeat.add(new JLabel(LABEL_ENDTIME.getText()), gbc2);
        gbc2.gridx = 1;
        gbc2.gridwidth = GridBagConstraints.REMAINDER;
        _jpvEndTime.setShowSeconds(false);
        _jpvEndTime.setTwelveHours(false);
        _jpvEndTime.setEnableArrows(true);
        _jpvEndTime.setAllowNull(true);
        _jpvEndTime.setTime(null);
        _panelRepeat.add(_jpvEndTime, gbc2);

        // add mandatories
        addMandatory(VALIDATION_START_DATETIME.getText(), _jpvStartDate);
        addMandatory(VALIDATION_START_DATETIME.getText(), _jpvStartTime);
        addMandatory(VALIDATION_DATETIME.getText(), _jpvEndDate);
        addMandatory(VALIDATION_DATETIME.getText(), _jpvEndTime);
    }

    //--------------------------------------------------------------------------
    /**
     * Build Data Transfer Combo
     */
    private void buildDataTransferCombo()
    {
        // select the datasources
        DataSource sysDs = DataManager.getSystemDS();
        Query q = new Query(DataTransfer.ENT_DATATRANSFER);
        try
        {
            RecordSet rs = sysDs.select(q);

            // iterate and build combo
            DataTransfer dt;
            while(rs != null && rs.next())
            {
                // get the dt
                dt = (DataTransfer)rs.getCurrentObject();
                if(dt != null)
                    _cmbDataTransfer.addItem(dt);
            }
        }
        catch(Exception e)
        {
            wsl.fw.gui.GuiManager.showErrorDialog(this, ERR_DATATRANSFER_COMBO.getText(), e);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Selects item in Data Transfer Combo if Data Transfer Id is known.
     * @param dataTransferId Data Transfer Id.
     */
    private void selectDataTransferComboItem(int dataTransferId)
    {
        // iterate the combo
        for(int i = 0; i < _cmbDataTransfer.getItemCount(); i++)
        {
            // compare the item text
            if(((DataTransfer)_cmbDataTransfer.getItemAt(i)).getId() == dataTransferId)
            {
                _cmbDataTransfer.setSelectedIndex(i);
                break;
            }
        }
    }



    //--------------------------------------------------------------------------
    /**
     * Transfer data between the DataObject and panel controls.
     * @param toDataObject, determines the direction of the transfer.
     */
    public void transferData(boolean toDataObject)
    {
        // must have a DataObject
        Scheduling dobj = (Scheduling) getDataObject();
        Util.argCheckNull(dobj);

        if (toDataObject)
        {
            // to the DataObject
            DataTransfer dtSelected = (DataTransfer)_cmbDataTransfer.getSelectedItem();

            // set Data Transfer Id
            dobj.setDataTransferId(dtSelected.getId());

            // set Start Date and End Date
            Calendar cStartDate = mergeDateAndTime(_jpvStartDate.getCalendar(),
                                                _jpvStartTime.getCalendar());
            if(cStartDate != null)
                dobj.setStartDate(cStartDate.getTime());
            Calendar cEndDate = mergeDateAndTime(_jpvEndDate.getCalendar(),
                                                    _jpvEndTime.getCalendar());
            if(cEndDate != null)
                dobj.setEndDate(cEndDate.getTime());

            // set Repeat Count
            String strCount = _txtRepeatCount.getText();
            if(strCount != null && strCount.length() > 0)
                dobj.setRepeatCount(Integer.parseInt(strCount));

            // setRepeatType
            if (_radioMonth.isSelected())
                dobj.setRepeatType(Scheduling.REPEATTYPE_MONTH);
            else if (_radioWeek.isSelected())
                dobj.setRepeatType(Scheduling.REPEATTYPE_WEEK);
            else if (_radioDay.isSelected())
                dobj.setRepeatType(Scheduling.REPEATTYPE_DAY);
            else if (_radioHour.isSelected())
                dobj.setRepeatType(Scheduling.REPEATTYPE_HOUR);
            else if (_radioNone.isSelected())
                dobj.setRepeatType(Scheduling.REPEATTYPE_NONE);
        }
        else
        {
            // to the panel from DataObject

            // select item in combo for dobj
            selectDataTransferComboItem(dobj.getDataTransferId());

            // set start date and time
            Date date;

            if ((date = dobj.getStartDate()) != null)
            {
                _jpvStartDate.setDate(date);
                _jpvStartTime.setTime(date);
            }

            // set end date and time
            if (dobj.getRepeatType() != Scheduling.REPEATTYPE_NONE &&
                (date = dobj.getEndDate()) != null)
            {
                _jpvEndDate.setDate(date);
                _jpvEndTime.setTime(date);
            }

            // set radio buttons
            switch (dobj.getRepeatType())
            {
                case Scheduling.REPEATTYPE_MONTH:
                    _radioMonth.setSelected(true);
                    _repeatType.setText(Scheduling.LABEL_MONTHS.getText());
                    break;
                case Scheduling.REPEATTYPE_WEEK:
                    _radioWeek.setSelected(true);
                    _repeatType.setText(Scheduling.LABEL_WEEKS.getText());
                    break;
                case Scheduling.REPEATTYPE_DAY:
                    _radioDay.setSelected(true);
                    _repeatType.setText(Scheduling.LABEL_DAYS.getText());
                    break;
                case Scheduling.REPEATTYPE_HOUR:
                    _radioHour.setSelected(true);
                    _repeatType.setText(Scheduling.LABEL_HOURS.getText());
                    break;
                default:
                case Scheduling.REPEATTYPE_NONE:
                    _radioNone.setSelected(true);
                    _repeatType.setText("");
                    break;
            }

            // set RepeatCount
            int repeatCount = dobj.getRepeatCount();
            if (repeatCount != Type.NULL_INTEGER)
                _txtRepeatCount.setText((new Integer(repeatCount)).toString());
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Merges Date from first argument and Time from second.
     * @param cDate Date.
     * @param cTime Time.
     * @return Calendar Calendar with Date from first arg and Time from second.
     */
    protected Calendar mergeDateAndTime(Calendar cDate, Calendar cTime)
    {
        if(cDate == null)
            return null;

        Calendar cMergedDate = (Calendar)cDate.clone();
        if(cTime != null)
        {
            cMergedDate.set(Calendar.HOUR_OF_DAY, cTime.get(Calendar.HOUR_OF_DAY));
            cMergedDate.set(Calendar.MINUTE, cTime.get(Calendar.MINUTE));
            cMergedDate.set(Calendar.SECOND, cTime.get(Calendar.SECOND));
        }

        return cMergedDate;
    }

    //--------------------------------------------------------------------------
    /**
     * Validates Data in control.
     * @param comp Component to validate data.
     * @return boolean true if the component contains data
     */
    protected boolean hasData(JComponent comp)
    {
        // validate
        Util.argCheckNull(comp);

        // start date and time
        if (comp == _jpvStartDate || comp == _jpvStartTime)
        {
            if (_jpvStartDate.getDate() == null ||  _jpvStartTime.getTime() == null)
                return false;
        }
        else

        // switch on control
        if(!_radioNone.isSelected())
        {
            // repeat count
            if(comp == _txtRepeatCount)
            {
                // Repeat Count must be a number and >= 0
                String text = _txtRepeatCount.getText();
                if (text == null || text.length() == 0)
                    return false;
                try
                {
                    Integer num = new Integer(text);
                    if (num.intValue() < 0)
                        return false;
                }
                catch (NumberFormatException e)
                {
                    return false;
                }
            }

            // end date and time
/*
            else if (comp == _jpvEndDate || comp == _jpvEndTime)
            {
                if (_jpvEndDate.getDate() == null || _jpvEndTime.getTime() == null)
                    return false;
                try
                {
                    Calendar cStartDate = mergeDateAndTime(_jpvStartDate.getCalendar(),
                                                        _jpvStartTime.getCalendar());

                    Calendar cEndDate = mergeDateAndTime(_jpvEndDate.getCalendar(),
                                                        _jpvEndTime.getCalendar());

                    if (cStartDate.after(cEndDate))
                        return false;
                }
                catch (Exception e)
                {
                    return false;
                }
            }
*/

            //else if (comp == _jpvEndDate)
            //    return _jpvEndDate.getDate() != null;
            //else if (comp == _jpvEndTime)
            //    return _jpvEndTime.getTime() != null;
        }

        // has data
        return true;
    }

    //--------------------------------------------------------------------------
    /**
     * Handle actions from buttons.
     */
    public void actionPerformed(ActionEvent event)
    {
        try
        {
            if(event.getSource().equals(_radioNone))
            {
                _repeatType.setText("");
                _txtRepeatCount.setText("");
                _jpvEndDate.setDate(null);
                _jpvEndTime.setTime(null);
            }
            else if(event.getSource().equals(_radioMonth))
            {
                _repeatType.setText(Scheduling.LABEL_MONTHS.getText());
                _txtRepeatCount.setText("1");
            }
            else if(event.getSource().equals(_radioDay))
            {
                _repeatType.setText(Scheduling.LABEL_DAYS.getText());
                _txtRepeatCount.setText("1");
            }
            else if(event.getSource().equals(_radioHour))
            {
                _repeatType.setText(Scheduling.LABEL_HOURS.getText());
                _txtRepeatCount.setText("1");
            }
            else if(event.getSource().equals(_radioWeek))
            {
                _repeatType.setText(Scheduling.LABEL_WEEKS.getText());
                _txtRepeatCount.setText("1");
            }

            updateButtons();
        }
        catch(Exception e)
        {
            GuiManager.showErrorDialog(this,
                wsl.mdn.common.MdnAdminConst.ERR_UNHANDLED.getText(), e);
            Log.error(wsl.mdn.common.MdnAdminConst.ERR_UNHANDLED.getText(), e);
        }
    }


    //--------------------------------------------------------------------------
    // misc

    /**
     * enable / disable controls
     */
    public void updateButtons()
    {
        // flags
        boolean isRepeat = !_radioNone.isSelected();

        // enable
        _txtRepeatCount.setEnabled(isRepeat);
        _jpvEndDate.setEnabled(isRepeat);
        _jpvEndTime.setEnabled(isRepeat);
    }

    /**
     * Return the preferred size
     */
    public Dimension getPreferredSize()
    {
        return new Dimension(500, 300);
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
        return HID_SCHEDULING;
    }
}