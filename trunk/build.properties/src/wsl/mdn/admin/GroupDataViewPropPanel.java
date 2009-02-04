package wsl.mdn.admin;

// imports
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import wsl.fw.util.Util;
import wsl.fw.gui.GuiConst;
import wsl.fw.gui.PropertiesPanel;
import wsl.fw.gui.WslTextField;
import wsl.fw.gui.WslComboBox;
import wsl.fw.resource.ResId;
import wsl.fw.datasource.DataManager;
import wsl.fw.datasource.DataSource;
import wsl.fw.datasource.Query;
import wsl.fw.datasource.QueryCriterium;
import wsl.fw.datasource.RecordSet;
import wsl.mdn.dataview.DataView;
import wsl.fw.security.Group;
import wsl.mdn.dataview.GroupDataView;
import wsl.fw.resource.ResId;
import wsl.fw.help.HelpId;

//------------------------------------------------------------------------------
/**
 *
 */
public class GroupDataViewPropPanel extends PropertiesPanel
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:35:35 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/admin/GroupDataViewPropPanel.java $ ";

    // resources
    public static final ResId LABEL_GROUP  = new ResId("GroupDataViewPropPanel.label.Group");
    public static final ResId LABEL_VIEW  = new ResId("GroupDataViewPropPanel.label.View");
    public static final ResId LABEL_ALLOW_ADD  = new ResId("GroupDataViewPropPanel.label.AllowAdd");
    public static final ResId LABEL_ALLOW_EDIT  = new ResId("GroupDataViewPropPanel.label.AllowEdit");
    public static final ResId LABEL_ALLOW_DELETE  = new ResId("GroupDataViewPropPanel.label.AllowDelete");
    public static final ResId ERR_GET_DV_GROUP = new ResId("GroupDataViewPropPanel.error.getDvGroup");

    public final static HelpId HID_GROUP_DATAVIEW = new HelpId("mdn.admin.GroupDataViewPropPanel");

    // controls
    private WslTextField _txtGroup        = new WslTextField(200);
    private WslTextField _txtView = new WslTextField(200);
    private JCheckBox _chkAdd = new JCheckBox();
    private JCheckBox _chkEdit = new JCheckBox();
    private JCheckBox _chkDelete = new JCheckBox();

    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public GroupDataViewPropPanel()
    {
        // init controls
        initGroupDataViewPropPanelControls();

    }

    //--------------------------------------------------------------------------
    /**
     * Init the panel's controls.
     */
    private void initGroupDataViewPropPanelControls()
    {
        // set layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // add "Group" label
        JLabel lblGroup = new JLabel(LABEL_GROUP.getText());
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets.left = GuiConst.DEFAULT_INSET;
        gbc.insets.top = GuiConst.DEFAULT_INSET;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 0.2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(lblGroup, gbc);
        //add "Group" text field
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        _txtGroup.setEditable(false);
        add(_txtGroup, gbc);

        // add "View" label
        JLabel lblView = new JLabel(LABEL_VIEW.getText());
        gbc.insets.right = 0;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(lblView, gbc);
        //add "View" text field
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        gbc.weighty = 1;
        _txtView.setEditable(false);
        add(_txtView, gbc);

        // add "Allow Add" label
        JLabel lblAdd = new JLabel(LABEL_ALLOW_ADD.getText());
        gbc.insets.right = 0;
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(lblAdd, gbc);
        // add "Allow Add" check box
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        gbc.weighty = 1;
        add(_chkAdd, gbc);

        // add "Allow Edit" label
        JLabel lblEdit = new JLabel(LABEL_ALLOW_EDIT.getText());
        gbc.insets.right = 0;
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(lblEdit, gbc);
        // add "Allow Edit" check box
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        gbc.weighty = 1;
        add(_chkEdit, gbc);

        // add "Allow Delete" label
        JLabel lblDelete = new JLabel(LABEL_ALLOW_DELETE.getText());
        gbc.insets.right = 0;
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(lblDelete, gbc);
        // add "Allow Delete" check box
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        gbc.weighty = 1;
        add(_chkDelete, gbc);

    }

    //--------------------------------------------------------------------------
    /**
     * Transfer data between the DataObject and panel controls.
     * @param toDataObject, determines the direction of the transfer.
     */
    public void transferData(boolean toDataObject)
    {
        // must have a DataObject
        GroupDataView dobj = (GroupDataView) getDataObject();
        Util.argCheckNull(dobj);

        if (toDataObject)
        {
            // to the DataObject
//            if (_chkAdd.isSelected())
//                dobj.setCanAdd(1);
//            else
//                dobj.setCanAdd(0);
//
//            if (_chkEdit.isSelected())
//                dobj.setCanEdit(1);
//            else
//                dobj.setCanEdit(0);
//
//            if (_chkDelete.isSelected())
//                dobj.setCanDelete(1);
//            else
//                dobj.setCanDelete(0);
        }
        else
        {
            // to the controls
//            _chkAdd.setSelected(dobj.getCanAdd() == 1);
//            _chkEdit.setSelected(dobj.getCanEdit() == 1);
//            _chkDelete.setSelected(dobj.getCanDelete() == 1);

            DataSource ds = DataManager.getSystemDS();

            Query q1 = new Query(Group.ENT_GROUP);
            QueryCriterium qc1 = new QueryCriterium(Group.ENT_GROUP, Group.FLD_ID,
                QueryCriterium.OP_EQUALS, new Integer(dobj.getGroupId()) );
            q1.addQueryCriterium(qc1);

            Query q2 = new Query(DataView.ENT_DATAVIEW);
            QueryCriterium qc2 = new QueryCriterium(DataView.ENT_DATAVIEW, DataView.FLD_ID,
                QueryCriterium.OP_EQUALS, new Integer(dobj.getDataViewId()) );
            q2.addQueryCriterium(qc2);

            try
            {
                RecordSet rsGroup = ds.select(q1);
                if (rsGroup.next())
                {
                    Group group = (Group)rsGroup.getCurrentObject();
                    _txtGroup.setText(group.getName());
                }

                RecordSet rsDataView = ds.select(q2);
                if (rsDataView.next())
                {
                    DataView dataView = (DataView)rsDataView.getCurrentObject();
                    _txtView.setText(dataView.getName());
                }
            }
            catch (Exception e)
            {
            wsl.fw.gui.GuiManager.showErrorDialog(this, ERR_GET_DV_GROUP.getText(), e);
            }
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Return the preferred size
     */
    public Dimension getPreferredSize()
    {
        return new Dimension(400, 200);
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
        return HID_GROUP_DATAVIEW;
    }
}