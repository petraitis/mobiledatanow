package wsl.mdn.admin;

// imports
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import javax.swing.JLabel;
import pv.jfcx.JPVPassword;
import javax.swing.JCheckBox;
import wsl.fw.util.Type;
import wsl.fw.datasource.*;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.gui.GuiConst;
import wsl.fw.gui.WslWizardChild;
import wsl.fw.gui.PropertiesPanel;
import wsl.fw.gui.WslTextField;
import wsl.fw.gui.WslTextArea;
import wsl.fw.gui.WslComboBox;
import wsl.fw.resource.ResId;
import wsl.fw.help.HelpId;
import wsl.fw.datasource.Field;
import wsl.mdn.dataview.DataView;
import wsl.mdn.dataview.DataViewField;
import wsl.mdn.dataview.QueryDobj;

//------------------------------------------------------------------------------
/**
 *
 */
public class GroupByPanel extends WslWizardChild implements ActionListener
{
    //--------------------------------------------------------------------------
    // resources

    public static final ResId LABEL_GROUP_BY =
        new ResId("GroupByPanel.label.GroupBy");
    public static final ResId LABEL_NONE =
        new ResId("GroupByPanel.label.None");

    public final static HelpId HID_GROUPBY = new HelpId("mdn.admin.GroupByPanel");

    //--------------------------------------------------------------------------
    // controls

    private WslComboBox _cmbFields = new WslComboBox(200);
    private boolean _isBuilding = false;


    //--------------------------------------------------------------------------
    // attributes

    private QueryDobj _query;


    //--------------------------------------------------------------------------
    // construction

    /**
     * Default constructor.
     */
    public GroupByPanel(QueryDobj query)
    {
        // init controls
        initGroupByPanelControls();

        // set query
        _query = query;

        // build combo
        buildFieldCombo();
    }


    //--------------------------------------------------------------------------
    /**
     * Init the panel's controls.
     */
    private void initGroupByPanelControls()
    {
        // set layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Group by combo
        JLabel lbl = new JLabel(LABEL_GROUP_BY.getText());
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets.left = GuiConst.DEFAULT_INSET;
        gbc.insets.top = GuiConst.DEFAULT_INSET;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(lbl, gbc);
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        _cmbFields.addActionListener(this);
        add(_cmbFields, gbc);
    }

    /**
     * Build DataViewField combo
     */
    private void buildFieldCombo()
    {
        // must have a query
        Util.argCheckNull(_query);

        // set building flag
        _isBuilding = true;

        // add None
        _cmbFields.addItem(new String(LABEL_NONE.getText()));

        // build combo
        _cmbFields.buildFromVector(_query.getDataView().getFields());

        // if we have a group by field, set it
        int gfid = _query.getGroupFieldId();
        if(gfid >= 0)
        {
            // iterate combo
            DataViewField dvf;
            for(int i = 1; i < _cmbFields.getModel().getSize(); i++)
            {
                // get the field
                dvf = (DataViewField)_cmbFields.getItemAt(i);
                if(dvf != null && dvf.getId() == gfid)
                {
                    _cmbFields.setSelectedIndex(i);
                    break;
                }
            }
        }
        else
            _cmbFields.setSelectedIndex(0);

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
            if(ev.getSource() == _cmbFields && !_isBuilding)
                onComboSelectionChanged();
        }
        catch(Exception e)
        {
            wsl.fw.gui.GuiManager.showErrorDialog(this,
                wsl.mdn.common.MdnAdminConst.ERR_UNHANDLED.getText(), e);
            Log.error(wsl.mdn.common.MdnAdminConst.ERR_UNHANDLED.getText(), e);
        }
    }

    /**
     * Combo selection changed
     */
    private void onComboSelectionChanged()
    {
        // get the selected item
        Integer id = null;
        int index = _cmbFields.getSelectedIndex();
        if(index > 0)
        {
            // get the dvf and the id
            DataViewField dvf = (DataViewField)_cmbFields.getSelectedItem();
            if(dvf != null)
                id = new Integer(dvf.getId());
        }

        // set group field id
        _query.setGroupFieldId(id);
    }


    //--------------------------------------------------------------------------
    /**
     * @return true if the finish button is to be enabled.
     */
    public boolean canFinish()
    {
        return true;
    }

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
        return HID_GROUPBY;
    }
}