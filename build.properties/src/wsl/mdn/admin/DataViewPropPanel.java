package wsl.mdn.admin;

// imports
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import javax.swing.JLabel;
import pv.jfcx.JPVPassword;
import wsl.fw.util.Util;
import wsl.fw.datasource.*;
import wsl.fw.gui.GuiConst;
import wsl.fw.gui.PropertiesPanel;
import wsl.fw.gui.WslTextField;
import wsl.fw.gui.WslComboBox;
import wsl.fw.gui.GuiManager;
import wsl.fw.resource.ResId;
import wsl.fw.help.HelpId;
import wsl.mdn.dataview.DataSourceDobj;
import wsl.mdn.dataview.DataView;

//------------------------------------------------------------------------------
/**
 *
 */
public class DataViewPropPanel extends PropertiesPanel
{
    // resources
    public static final ResId LABEL_NAME  = new ResId("DataViewPropPanel.label.Name");
    public static final ResId LABEL_DATASOURCE  = new ResId("DataViewPropPanel.label.DataSource");
    public static final ResId ERR_DS_COMBO = new ResId("DataViewPropPanel.error.loadDsCombo");
    public static final ResId ERR_SOURCE_DS = new ResId("DataViewPropPanel.error.sourceDs");

    public final static HelpId HID_DATAVIEW = new HelpId("mdn.admin.DataViewPropPanel");

    // controls
    private WslTextField _txtName        = new WslTextField(200);
    private WslComboBox _cmbDataSource = new WslComboBox(200);


    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public DataViewPropPanel()
    {
        // init controls
        initDataViewPropPanelControls();

        // build the type combo
        buildDataSourceCombo();
    }

    //--------------------------------------------------------------------------
    /**
     * Init the panel's controls.
     */
    private void initDataViewPropPanelControls()
    {
        // set layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Name control
        JLabel lbl = new JLabel(LABEL_NAME.getText());
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets.left = GuiConst.DEFAULT_INSET;
        gbc.insets.top = GuiConst.DEFAULT_INSET;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 0.2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(lbl, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        add(_txtName, gbc);
        addMandatory(LABEL_NAME.getText(), _txtName);

        // datasource combo
        lbl = new JLabel(LABEL_DATASOURCE.getText());
        gbc.insets.right = 0;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(lbl, gbc);
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        gbc.weighty = 1;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        add(_cmbDataSource, gbc);
    }

    /**
     * Build type combo
     */
    private void buildDataSourceCombo()
    {
        // select the datasources
        DataSource sysDs = DataManager.getSystemDS();
        Query q = new Query(DataSourceDobj.ENT_DATASOURCE);
        QueryCriterium qc = new QueryCriterium(DataSourceDobj.ENT_DATASOURCE,
            DataSourceDobj.FLD_IS_MIRROR_DB, QueryCriterium.OP_EQUALS,
            new Boolean(false));
        qc.setOrIsNull(true);
        q.addQueryCriterium(qc);
        try
        {
            RecordSet rs = sysDs.select(q);

            // iterate and build combo
            DataSourceDobj ds;
            while(rs != null && rs.next())
            {
                // get the ds
                ds = (DataSourceDobj)rs.getCurrentObject();
                if(ds != null)
                    _cmbDataSource.addItem(ds);
            }
        }
        catch(Exception e)
        {
            GuiManager.showErrorDialog(this, ERR_DS_COMBO.getText(), e);
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
        DataView dobj = (DataView) getDataObject();
        Util.argCheckNull(dobj);

        if (toDataObject)
        {
            // to the DataObject
            dobj.setName(_txtName.getText());

            // datasource
            DataSourceDobj ds = (DataSourceDobj)_cmbDataSource.getSelectedItem();
            if(ds != null)
            {
                dobj.setSourceDsId(ds.getId());
                dobj.setSourceDataSource(ds);
            }
        }
        else
        {
            // to the controls
            _txtName.setText(dobj.getName());

            // datasource
            try
            {
                DataSourceDobj ds = dobj.getSourceDataSource();
                if (ds != null)
                    _cmbDataSource.selectItem(ds.getName());

                if (dobj.getState() != DataObject.NEW)
                    _cmbDataSource.disable();
            }
            catch (Exception e)
            {
                GuiManager.showErrorDialog(this, ERR_SOURCE_DS.getText(), e);
            }
        }
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
        return HID_DATAVIEW;
    }
}