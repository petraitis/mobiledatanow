package wsl.mdn.admin;

// imports
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.resource.ResId;
import wsl.fw.help.HelpId;
import wsl.fw.datasource.*;
import wsl.fw.gui.*;
import wsl.mdn.dataview.*;

//------------------------------------------------------------------------------
/**
 *
 */
public class MaintainDataTransfersPanel extends WslButtonPanel
    implements ActionListener, TreeExpansionListener, TreeSelectionListener,
    WizardClosedListener
{
    // resources
    public static final ResId BUTTON_ADD_TRANSFER  =
        new ResId("MaintainDataTransfersPanel.button.AddTransfer");
    public static final ResId BUTTON_SCHEDULE  =
        new ResId("MaintainDataTransfersPanel.button.Schedule");
    public static final ResId BUTTON_REMOVE  =
        new ResId("MaintainDataTransfersPanel.button.Remove");
    public static final ResId BUTTON_PROPERTIES  =
        new ResId("MaintainDataTransfersPanel.button.Properties");
    public static final ResId BUTTON_EXECUTE  =
        new ResId("MaintainDataTransfersPanel.button.Execute");
    public static final ResId BUTTON_CLOSE  =
        new ResId("MaintainDataTransfersPanel.button.Close");
    public static final ResId TREE_NAME  =
        new ResId("MaintainDataTransfersPanel.tree");
    public static final ResId PANEL_TITLE  =
        new ResId("MaintainDataTransfersPanel.title");
    public static final ResId BUTTON_HELP =
        new ResId("OkPanel.button.Help");
    public static final ResId ERR_BUILD_TREE =
        new ResId("MaintainDataTransfersPanel.error.buildTree");
    public static final ResId ERR_SAVE =
        new ResId("MaintainDataTransfersPanel.error.save");

    public final static HelpId HID_MAINT_DATATRANSFER = new HelpId("mdn.admin.MaintainDataTransfersPanel");

    // constants
    private static final int BTN_WIDTH = 132;

    // attributes
    private WslButton _btnAddTransfer = new WslButton(BUTTON_ADD_TRANSFER.getText(), BTN_WIDTH, this);
    private WslButton _btnSchedule = new WslButton(BUTTON_SCHEDULE.getText(), BTN_WIDTH, this);
    private WslButton _btnRemove = new WslButton(BUTTON_REMOVE.getText(), BTN_WIDTH, this);
    private WslButton _btnProperties = new WslButton(BUTTON_PROPERTIES.getText(), BTN_WIDTH, this);
    private WslButton _btnExecute = new WslButton(BUTTON_EXECUTE.getText(), BTN_WIDTH, this);
    private WslButton _btnClose = new WslButton(BUTTON_CLOSE.getText(), BTN_WIDTH, this);
    private WslButton _btnTest = new WslButton("Test", BTN_WIDTH, this);
    private DataObjectTree _tree;
    private boolean _isBuildingTree = false;
    private DataTransferWizard _wiz = null;

    //--------------------------------------------------------------------------
    /**
     * Blank constructor.
     */
    public MaintainDataTransfersPanel()
    {
        // super
        super(WslButtonPanel.VERTICAL);

        // set title
        setPanelTitle(PANEL_TITLE.getText());

        // init controls
        initMaintainDataTransfersPanelControls();

        // build the tree
        buildTree();

        // update buttons
        updateButtons();
    }

    //--------------------------------------------------------------------------
    /**
     * Initialise controls.
     */
    private void initMaintainDataTransfersPanelControls()
    {
        // add buttons
        addButton(_btnAddTransfer);
        addButton(_btnSchedule);
        addButton(_btnRemove);
        addButton(_btnProperties);
        addButton(_btnExecute);
        _btnClose.setIcon(Util.resourceIcon(GuiConst.FW_IMAGE_PATH + "close.gif"));
        addHelpButton(BUTTON_HELP.getText(), HID_MAINT_DATATRANSFER, BTN_WIDTH, false);
        addButton(_btnClose);
        //addCustomButton(_btnTest);

        // create the tree
        _tree = new DataObjectTree(TREE_NAME.getText());
        _tree.addTreeExpansionListener(this);
        _tree.addTreeSelectionListener(this);
        _tree.addListener(new DataListenerData(null, DataTransfer.ENT_DATATRANSFER, null));
        _tree.addListener(new DataListenerData(null, TransferEntity.ENT_TRANSFERENTITY, null));
        JScrollPane sp = new JScrollPane(_tree);
        sp.setPreferredSize(new Dimension(450, 450));

        // add the tree to the main panel
        getMainPanel().setBorder(BorderFactory.createLoweredBevelBorder());
        getMainPanel().setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets.top = GuiConst.DEFAULT_INSET;
        gbc.insets.left = GuiConst.DEFAULT_INSET;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        gbc.weightx = 1;
        gbc.weighty = 1;
        getMainPanel().add(sp, gbc);
    }

    //--------------------------------------------------------------------------
    /**
     * Build the Category and Product tree.
     */
    private void buildTree()
    {
        // set is building flag
        _isBuildingTree = true;

        // clear tree
        _tree.clear();

        // build the tree staring at the root
        try
        {
            buildNode(_tree.getRoot());
        }
        catch(Exception e)
        {
            GuiManager.showErrorDialog(this, ERR_BUILD_TREE.getText(), e);
        }

        // refresh the tree to ensure proper display
        _tree.refreshModel();

        // set is building flag
        _isBuildingTree = false;
    }

    //--------------------------------------------------------------------------
    /**
     * Build a node of the tree.
     */
    private void buildNode(DoTreeNode parentNode) throws DataSourceException
    {
        // set is building flag
        _isBuildingTree = true;

        // clear the node
        try
        {
            Util.argCheckNull(parentNode);
            _tree.clearNode(parentNode);

            // get parent data object and key
            DataObject doParent = parentNode.getDataObject();

            // if the doParent is null, then it is the root
            if(doParent == null)
            {
                // select all datatransfers
                DataSource ds = DataManager.getSystemDS();
                Query q = new Query(DataTransfer.ENT_DATATRANSFER);
                RecordSet rs = ds.select(q);

                // build the root node
                _tree.buildFromRecordSet(rs, parentNode);
            }

            // else, building a datatransfer
            else if(doParent instanceof DataTransfer)
            {
                // build transfer entities
                _tree.buildFromVector(((DataTransfer)doParent).getTransferEntities(),
                    parentNode, false);
            }
        }
        finally
        {
            _isBuildingTree = false;
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Tree selection changed.
     */
    public void valueChanged(TreeSelectionEvent ev)
    {
        updateButtons();
    }

    //--------------------------------------------------------------------------
    /**
     * Tree node expanded.
     */
    public void treeExpanded(TreeExpansionEvent ev)
    {
        // if building, out
        if(_isBuildingTree)
            return;

        // get the expanded node
        DoTreeNode node = (DoTreeNode)ev.getPath().getLastPathComponent();
        if(node != null)
        {
            try
            {
                // build the node
                buildNode(node);
            }
            catch(Exception e)
            {
                GuiManager.showErrorDialog(this, ERR_BUILD_TREE.getText(), e);
            }
        }

        // update buttons
        updateButtons();
    }

    //--------------------------------------------------------------------------
    /**
     * Tree node collapsed.
     */
    public void treeCollapsed(TreeExpansionEvent event)
    {
        // if building, out
        if(_isBuildingTree)
            return;
    }

    //--------------------------------------------------------------------------
    /**
     * Button clicked.
     */
    public void actionPerformed(ActionEvent ev)
    {
        try
        {
            // switch on source
            if(ev.getSource().equals(_btnClose))
                closePanel();
            else if(ev.getSource().equals(_btnAddTransfer))
                onAddTransfer();
            else if(ev.getSource().equals(_btnSchedule))
                onSchedule();
            else if(ev.getSource().equals(_btnRemove))
                onRemove();
            else if(ev.getSource().equals(_btnProperties))
                onProperties();
            else if(ev.getSource().equals(_btnExecute))
                onExecute();
            else if(ev.getSource().equals(_btnTest))
                onTestDay();

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
     * Panel closing.
     */
    public void onClosePanel()
    {
        // remove data listeners
        _tree.removeAllListeners();

        // super
        super.onClosePanel();
    }

    /**
     * Add DataSource button clicked.
     */

    private void onAddTransfer() throws DataSourceException
    {
        // create the wizard controller
        _wiz = new DataTransferWizard();

        // set the selected ds
        DataObject dobj = _tree.getSelectedDataObject();
        if(dobj != null && dobj instanceof DataTransfer)
            _wiz.setExistingDataTransfer((DataTransfer)dobj);

        // add close listener
        _wiz.addCloseListener(this);

        // create framing dialog and show
        GuiManager.openWslPanel(this.getFrameParent(), _wiz, true);
    }

    /**
     * DataStore import wizard closed
     */
    public void wizardClosed(boolean bFinished)
    {
        // validate
        Util.argCheckNull(_wiz);

        // musnt be closing
        if(isClosing())
            return;

        // must be finished
        if(bFinished)
        {
            // get the dt from the wizard
            DataTransfer dt = (DataTransfer)_wiz.getDataTransfer();
            if(dt != null)
            {
                try
                {
                    // save the dt
                    dt.save();

                    // if a new dt add it to the tree
                    DataObject dobj = _tree.getSelectedDataObject();
                    boolean isExisting = dobj != null && dobj instanceof DataTransfer &&
                        ((DataTransfer)dobj) == dt;
                    if(!isExisting)
                        _tree.addNode(_tree.getRoot(), dt, true);

                    // else rebuild the node
                    else
                        buildNode(_tree.getSelectedNode());
                }
                catch(Exception e)
                {
                    GuiManager.showErrorDialog(this, ERR_SAVE.getText(), e);
                }
            }
        }
    }

    /**
     * Schedule button clicked.
     */
    private void onSchedule() throws DataSourceException
    {
        // get the selected datasource
        DataTransfer dataTransfer = (DataTransfer)_tree.getSelectedDataObject();
        if(dataTransfer != null)
        {
            Scheduling scheduling = new Scheduling();
            scheduling.setDataTransferId(dataTransfer.getId());
            GuiManager.openOkCancelPanel(this.getFrameParent(), scheduling, true);
        }
    }


    /**
     * Remove button clicked.
     */

    private void onRemove() throws DataSourceException
    {
        // get the selected object
        DataObject dobj = _tree.getSelectedDataObject();

        // if it is IN_DB delete it
        if(dobj != null && dobj.getState() == DataObject.IN_DB)
        {
            // if it is a datatransfer, straight delete
            if(dobj instanceof DataTransfer)
                dobj.delete();

            // else remove from parent
            else
            {
                // get parent
                DoTreeNode selNode = _tree.getSelectedNode();
                DoTreeNode parentNode = (DoTreeNode)selNode.getParent();
                DataObject parentDobj = _tree.getDataObject(parentNode);

                // if parent not found just delete
                if(parentDobj == null)
                    dobj.delete();

                // else if te remove from data source
                else if(dobj instanceof TransferEntity)
                    ((DataTransfer)parentDobj).removeTransferEntity((TransferEntity)dobj, true);
            }
        }
    }

    /**
     * Properties button clicked.
     */
    private void onProperties() throws DataSourceException
    {
        // get the selected datasource
        DataObject dobj = _tree.getSelectedDataObject();
        if(dobj != null)
        {
            if(dobj instanceof DataTransfer)
                onAddTransfer();
            else
                GuiManager.openOkCancelPanel(this.getFrameParent(), dobj, true);
        }
    }

    /**
     * Execute button clicked
     */
    private void onExecute() throws Exception
    {
        // get the transfer
        DataTransfer dt = (DataTransfer)_tree.getSelectedDataObject();
        if(dt != null)
        {
            // create the transfer strategy
            NoJoinTransferStrategy ts = new NoJoinTransferStrategy(dt);

            // execute the strategy in a progress panel
            GuiManager.runProgressPanel(this.getFrameParent(), ts);
        }
    }

    /**
     * Update controls
     */
    public void updateButtons()
    {
        // flags
        DataObject selDobj = _tree.getSelectedDataObject();
        boolean hasDobj = selDobj != null;
        boolean isDt = hasDobj && selDobj instanceof DataTransfer;
        boolean isEnt = hasDobj && selDobj instanceof TransferEntity;
        boolean isSched = hasDobj && selDobj instanceof Scheduling;

        // enable
        _btnAddTransfer.setEnabled(true);
        _btnSchedule.setEnabled(isDt);
        _btnRemove.setEnabled(hasDobj);
        _btnExecute.setEnabled(isDt);
        _btnProperties.setEnabled(isDt || isSched);
    }


    //--------------------------------------------------------------------------
    // test

    private void onTestMonth() throws Exception
    {
        // get day events
        MonthEventNode men = CalendarEvent.getMonthEvents(3, 2001);
        Vector dens = men.getDayEventNodes();

        // output dens
        DayEventNode den;
        Vector events;
        System.out.println("\n*** Calendar Events ***\n");
        for(int i = 0; dens != null && i < dens.size(); i++)
        {
            // output day event
            den = (DayEventNode)dens.elementAt(i);
            outputDayEvents(den);
        }
    }

    private void onTestDay() throws Exception
    {
        // get day event
        DayEventNode den = CalendarEvent.getDayEvents(2, 3, 2001);
        if(den != null)
            outputDayEvents(den);
    }

    private void outputDayEvents(DayEventNode den)
    {
        System.out.println(" " + den);

        // iterate calendar events
        CalendarEvent ce;
        Vector events = den.getEvents();
        for(int j = 0; events != null && j < events.size(); j++)
        {
            // output calendar event
            ce = (CalendarEvent)events.elementAt(j);
            System.out.println("  " + ce);
        }
    }
}