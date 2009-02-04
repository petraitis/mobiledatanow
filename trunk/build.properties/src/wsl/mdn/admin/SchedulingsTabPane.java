package wsl.mdn.admin;

// imports
import java.util.Vector;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.table.TableColumn;
import wsl.fw.util.Log;
import wsl.fw.util.Util;
import wsl.fw.util.Type;
import wsl.fw.datasource.*;
import wsl.fw.gui.WslTabChildPanel;
import wsl.fw.gui.WslButton;
import wsl.fw.gui.GuiManager;
import wsl.fw.gui.GuiConst;
import wsl.fw.gui.WslPvTableModel;
import wsl.fw.gui.WslPvTableView;
import wsl.fw.resource.ResId;
import wsl.fw.help.HelpId;
import wsl.mdn.dataview.*;

//--------------------------------------------------------------------------
/**
 * Tab child panel schowing table of Schedulings
 */
public class SchedulingsTabPane extends WslTabChildPanel
    implements ActionListener, ListSelectionListener
{
    //--------------------------------------------------------------------------
    // resources

    public static final ResId LABEL_TITLE =
        new ResId("SchedulingsTabPane.label.Title");
    public static final ResId BUTTON_PROPERTIES =
        new ResId("SchedulingsTabPane.btn.Properties");
    public static final ResId BUTTON_DELETE =
        new ResId("SchedulingsTabPane.btn.Delete");
    public static final ResId ERR_LOAD_SCHEDULINGS =
        new ResId("SchedulingsTabPane.error.loadSchedulings");

    public final static HelpId HID_SCHEDULINGS = new HelpId("mdn.admin.SchedulingsTabPane");


    //--------------------------------------------------------------------------
    // attributes

    private WslPvTableView  _table;
    private SchedTableModel _model;
    private MaintainTransferSchedulePanel _parent;


    //--------------------------------------------------------------------------
    // controls

    private WslButton _btnProperties   = new WslButton(BUTTON_PROPERTIES.getText(), this);
    private WslButton _btnDelete       = new WslButton(BUTTON_DELETE.getText() , this);
    private WslButton _buttons[] = { _btnProperties, _btnDelete };


    //--------------------------------------------------------------------------
    // construction

    /**
     * Constructor
     */
    public SchedulingsTabPane(MaintainTransferSchedulePanel parent)
    {
        // title
        super(LABEL_TITLE.getText());

        // set parent
        _parent = parent;

        // table
        initTable();

        // controls
        createControls();

        // build table
        buildTable(Type.NULL_INTEGER);

        // update buttons
        updateButtons();
    }

    //--------------------------------------------------------------------------
    /**
     * controls
     */
    private void createControls()
    {
        // layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // table
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets.top = GuiConst.DEFAULT_INSET;
        gbc.insets.left = GuiConst.DEFAULT_INSET;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        gbc.weightx = 1;
        gbc.weighty = 1;
        add(_table, gbc);
    }


    //--------------------------------------------------------------------------
    // table

    /**
     * Init the schedulings table
     */
    private void initTable()
    {
        // create the table
        _model = new SchedTableModel();
        _table = new WslPvTableView(_model);
        initTableListeners();
    }

    /**
     * Init table listeners.
     */
    private void initTableListeners()
    {
        // mouse adapter for double clicks
        MouseListener ml = new MouseAdapter()
        {
            public void mousePressed(MouseEvent e)
            {
                int selRow = _table.getTable().getSelectedRow();
                if(e.getClickCount() == 2 && selRow >= 0)
                    onProperties();
            }
         };
        _table.getTable().addMouseListener(ml);
        _table.addRowSelectionListener(this);
    }

    /**
     * Post creation call by framework. Allows non-constructor-safe
     * initialisation of some controls.
     */
    public void postCreate()
    {
        // configure the table model
        _model.configureTable(_table);
    }

    /**
     * Panel closing.
     */
    public void onClosePanel()
    {
        // remove DataChangeListeners
        _model.removeAllDataChangeListeners();

        // super
        super.onClosePanel();
    }

    /**
     * List selected
     */
    public void valueChanged(ListSelectionEvent ev)
    {
        updateButtons();
    }

    /**
     * Build the table
     * @param dtid the id of a DataTransfer to build schedulings for
     * Type.NULL_INTEGER if all transfers
     */
    public void buildTable(int dtid)
    {
        try
        {
            // build query
            Query q = new Query(Scheduling.ENT_SCHEDULING);
            q.addQueryEntity(DataTransfer.ENT_DATATRANSFER);
            if(dtid != Type.NULL_INTEGER)
            {
                q.addQueryCriterium(new QueryCriterium(DataTransfer.ENT_DATATRANSFER,
                    DataTransfer.FLD_ID, QueryCriterium.OP_EQUALS,
                    new Integer(dtid)));
            }
            q.addSort(new Sort(DataTransfer.ENT_DATATRANSFER, DataTransfer.FLD_NAME));

            // execute query
            RecordSet rs = DataManager.getSystemDS().select(q);

            // build result vector
            Scheduling sched;
            DataTransfer dt;
            Vector v = new Vector();
            while(rs != null && rs.next())
            {
                // get the sched and dt
                sched = (Scheduling)rs.getCurrentObject(Scheduling.ENT_SCHEDULING);
                dt = (DataTransfer)rs.getCurrentObject(DataTransfer.ENT_DATATRANSFER);
                if(sched != null && dt != null)
                {
                    sched.setDataTransfer(dt);
                    v.add(sched);
                }
            }

            // set vector into model
            _model.setDataObjects(v);

            // configure the table model
            _model.configureTable(_table);
        }
        catch(Exception e)
        {
            GuiManager.showErrorDialog(this, ERR_LOAD_SCHEDULINGS.getText(), e);
        }
    }

    /**
     * @return Scheduling the selected Scheduling in the table.
     */
    private Scheduling getSelectedScheduling()
    {
        // get the selected index
        Scheduling s = null;
        int row = _table.getTable().getSelectedRow();
        if(row >= 0)
            s = (Scheduling)_model.getDataObjectAt(row);
        return s;
    }


    //--------------------------------------------------------------------------
    // overrides

    /**
     * Get the buttons that this tab wants to display in the parent's button
     * panel. This is called each time the tab child panel is selected.
     * @return an array of buttons, may be empty, not null.
     */
    public WslButton[] getButtons()
    {
        return _buttons;
    }

    /**
     * Tab has been selected
     * @param selected true if being selected, false if being deselected
     */
    public void onSelected(boolean selected)
    {
    }


    //--------------------------------------------------------------------------
    // actions

    /**
     * Handle actions from buttons.
     */
    public void actionPerformed(ActionEvent event)
    {
        try
        {
            // switch on source button
            if(event.getSource().equals(_btnProperties))
                onProperties();
            else if(event.getSource().equals(_btnDelete))
                onDelete();

            // update buttons
            updateButtons();
        }
        catch(Exception e)
        {
            GuiManager.showErrorDialog(this,
                wsl.mdn.common.MdnAdminConst.ERR_UNHANDLED.getText(), e);
            Log.error(wsl.mdn.common.MdnAdminConst.ERR_UNHANDLED.getText(), e);
        }
    }


    /**
     * Display a Properties panel for the selected scheduling
     */
    private void onProperties()
    {
        // get the scheduling
        Scheduling s = getSelectedScheduling();
        if(s != null)
        {
            // open prop panel
            GuiManager.openOkCancelPanel(_parent.getFrameParent(), s, true);
        }
    }

    /**
     * Delete the selected scheduling.
     * The exact operation depends on the type of the selected node.
     */
    private void onDelete() throws DataSourceException
    {
        // get the scheduling
        Scheduling s = getSelectedScheduling();
        if(s != null)
        {
            // delete the scheduling
            s.delete();
            _parent.updateUI();
            _parent.refreshMonthCalendar();
        }
    }


    //--------------------------------------------------------------------------
    // misc

    /**
     * Enable disable controls
     */
    public void updateButtons()
    {
        // flags
        Scheduling s = getSelectedScheduling();
        boolean isSelected = s != null;

        // enable
        _btnProperties.setEnabled(isSelected);
        _btnDelete.setEnabled(isSelected);
    }

    /**
     * Return the preferred size
     */
    public Dimension getPreferredSize()
    {
        return new Dimension(500, 500);
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
        return HID_SCHEDULINGS;
    }
}