package wsl.mdn.admin;

// imports
import java.util.Vector;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.DataSourceException;
import wsl.fw.datasource.QueryCriterium;
import wsl.fw.gui.GuiConst;
import wsl.fw.gui.WslWizardChild;
import wsl.fw.gui.WslTextField;
import wsl.fw.gui.WslTextArea;
import wsl.fw.gui.WslComboBox;
import wsl.fw.gui.WslList;
import wsl.fw.gui.WslButton;
import wsl.fw.gui.WslButtonPanel;
import wsl.fw.gui.GuiManager;
import wsl.fw.resource.ResId;
import wsl.fw.help.HelpId;
import wsl.mdn.dataview.QueryDobj;
import wsl.mdn.dataview.DataTransfer;
import wsl.mdn.dataview.EntityDobj;
import wsl.mdn.dataview.FieldDobj;

//------------------------------------------------------------------------------
/**
 *
 */
public class TransferFilterPanel extends WslWizardChild
    implements ActionListener, ListSelectionListener, ItemListener
{
    //--------------------------------------------------------------------------
    // resources

    public static final ResId LABEL_ENTITY  =
        new ResId("TransferFilterPanel.label.Entity");
    public static final ResId LABEL_FIELD  =
        new ResId("TransferFilterPanel.label.Field");
    public static final ResId LABEL_OPERATOR  =
        new ResId("TransferFilterPanel.label.Operator");
    public static final ResId LABEL_VALUE  =
        new ResId("TransferFilterPanel.label.Value");
    public static final ResId LABEL_CRITERIA_LIST  =
        new ResId("TransferFilterPanel.label.CriteriaList");
    public static final ResId BUTTON_ADD  =
        new ResId("TransferFilterPanel.btn.Add");
    public static final ResId BUTTON_REMOVE  =
        new ResId("TransferFilterPanel.btn.Remove");
    public static final ResId BUTTON_OK  =
        new ResId("TransferFilterPanel.btn.Ok");
    public static final ResId BUTTON_CANCEL  =
        new ResId("TransferFilterPanel.btn.Cancel");
    public static final ResId ENTER_VALUE_ERROR  =
        new ResId("TransferFilterPanel.error.EnterValue");
    public static final ResId ERR_GET_ENTITIES =
        new ResId("TransferFilterPanel.error.getEntities");

    public final static HelpId HID_TRANSFER_FILTER = new HelpId("mdn.admin.TransferFilterPanel");

    //--------------------------------------------------------------------------
    // controls

    private WslComboBox _cmbEntity = new WslComboBox(150);
    private WslComboBox _cmbField = new WslComboBox(150);
    private WslComboBox _cmbOperator = new WslComboBox(150);
    private WslTextField _txtValue = new WslTextField(150);
    private WslList _lstCriteria = new WslList();
    private WslButton _btnAdd = new WslButton(BUTTON_ADD.getText(), this);
    private WslButton _btnRemove = new WslButton(BUTTON_REMOVE.getText(), this);


    //--------------------------------------------------------------------------
    // attributes

    private DataTransfer _dt;
    private QueryDobj _q;


    //--------------------------------------------------------------------------
    // construction

    /**
     * Default constructor.
     */
    public TransferFilterPanel(DataTransfer dt)
    {
        // set attrib
        _dt = dt;
        _dt.imageDataTransfer();
        _q = _dt.getFilterQuery();

        // init controls
        initTransferFilterPanelControls();

        // build combos
        buildCombos();

        // build criteria list
        buildCriteriaList();

        // update buttons
        updateButtons();
    }

    /**
     * Init the panel's controls.
     */
    private void initTransferFilterPanelControls()
    {
        // set layout
        setBorder(BorderFactory.createLoweredBevelBorder());
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // entity
        JLabel lbl = new JLabel(LABEL_ENTITY.getText());
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets.left = GuiConst.DEFAULT_INSET;
        gbc.insets.top = GuiConst.DEFAULT_INSET;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(lbl, gbc);
        gbc.gridy = 1;
        _cmbEntity.addItemListener(this);
        add(_cmbEntity, gbc);

        // field
        gbc.gridx = 1;
        gbc.gridy = 0;
        lbl = new JLabel(LABEL_FIELD.getText());
        add(lbl, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(_cmbField, gbc);

        // operator
        lbl = new JLabel(LABEL_OPERATOR.getText());
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(lbl, gbc);
        gbc.gridy = 3;
        add(_cmbOperator, gbc);

        // value
        lbl = new JLabel(LABEL_VALUE.getText());
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(lbl, gbc);
        gbc.gridy = 3;
        add(_txtValue, gbc);

        // criteria list
        lbl = new JLabel(LABEL_CRITERIA_LIST.getText());
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(lbl, gbc);
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        _lstCriteria.addListSelectionListener(this);
        _lstCriteria.getScrollPane().setBorder(BorderFactory.createLoweredBevelBorder());
        add(_lstCriteria.getScrollPane(), gbc);

        // buttons
        gbc.weighty = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        add(_btnAdd, gbc);
        gbc.gridx = 1;
        add(_btnRemove, gbc);
    }

    /**
     * Build join list
     */
    private void buildCriteriaList()
    {
        // validate
        Util.argCheckNull(_q);

        // get criteria from the q
        Vector criteria = _q.getCriteria(null);

        // build list
        _lstCriteria.buildFromVector(criteria);
    }

    /**
     * Build combos
     */
    private void buildCombos()
    {
        // validate
        Util.argCheckNull(_dt);

        // build entities
         try
        {
            _cmbEntity.buildFromVector(_dt.getSourceDataSource().getEntities());
            _cmbEntity.setSelectedIndex(0);

        }
        catch(Exception e)
        {
            GuiManager.showErrorDialog(this, ERR_GET_ENTITIES.getText(), e);
        }

        // build fields
        //_cmbField.buildFromVector(_dt.getSourceDataSource().getEntities());
        //_cmbField.setSelectedIndex(0);

        // build operators
        _cmbOperator.addItem(QueryCriterium.OP_EQUALS);
        _cmbOperator.addItem(QueryCriterium.OP_GREATER_THAN);
        _cmbOperator.addItem(QueryCriterium.OP_GREATER_THAN_EQUALS);
        _cmbOperator.addItem(QueryCriterium.OP_IS_NOT_NULL);
        _cmbOperator.addItem(QueryCriterium.OP_IS_NULL);
        _cmbOperator.addItem(QueryCriterium.OP_LESS_THAN);
        _cmbOperator.addItem(QueryCriterium.OP_LESS_THAN_EQUALS);
        _cmbOperator.addItem(QueryCriterium.OP_LIKE);
        _cmbOperator.addItem(QueryCriterium.OP_NOT_EQUALS);
        _cmbOperator.addItem(QueryCriterium.OP_NOT_LIKE);
        _cmbOperator.setSelectedIndex(0);
    }


    /**
     * List clicked
     */
    public void valueChanged(ListSelectionEvent ev)
    {
        updateButtons();
    }

    /**
     * Combo selection changed
     */
    public void itemStateChanged(ItemEvent ev)
    {
        // only build on the selected event
        if(ev.getStateChange() == ItemEvent.SELECTED)
        {
            // left entity
            if(ev.getSource() == _cmbEntity)
            {
                // get the selected item
                EntityDobj ent = (EntityDobj)ev.getItem();
                if(ent != null)
                {
                    // get the fields
                    Vector fields = ent.getFields();

                    // build the field combo
                    _cmbField.removeAllItems();
                    _cmbField.buildFromVector(fields);
                }
            }
        }
    }

    /**
     * button clicked
     */
    public void actionPerformed(ActionEvent ev)
    {
        try
        {
            // switch on source
            if(ev.getSource() == _btnAdd)
                onAdd();
            else if(ev.getSource() == _btnRemove)
                onRemove();
        }
        catch(Exception e)
        {
            GuiManager.showErrorDialog(this,
                wsl.mdn.common.MdnAdminConst.ERR_UNHANDLED.getText(), e);
            Log.error(wsl.mdn.common.MdnAdminConst.ERR_UNHANDLED.getText(), e);
        }
    }

    /**
     * Add button clicked
     */
    private void onAdd()
    {
        // validate
        Util.argCheckNull(_dt);

        // must have a value if a binary operator
        String op = _cmbOperator.getSelectedItem().toString();
        if(!(op.equalsIgnoreCase(QueryCriterium.OP_IS_NULL) ||
            op.equalsIgnoreCase(QueryCriterium.OP_IS_NOT_NULL)) &&
            _txtValue.getText() == null || _txtValue.getText().length() == 0)
        {
            JOptionPane.showMessageDialog(this.getFrameParent(), ENTER_VALUE_ERROR.getText());
            return;
        }

        // create a QueryCriterium and set vals
        QueryCriterium qc = new QueryCriterium();
        qc._entityName = ((EntityDobj)_cmbEntity.getSelectedItem()).getName();
        qc._fieldName = ((FieldDobj)_cmbField.getSelectedItem()).getName();
        qc._op = _cmbOperator.getSelectedItem().toString();
        qc._value = _txtValue.getText();

        try
        {
            // add to list
            _lstCriteria.getDefaultModel().addElement(qc);

            // add to query
            _q.addCriterium(qc);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e.toString());
        }
    }

    /**
     * Remove button clicked
     */
    private void onRemove()
    {
        // must be selected
        if(_lstCriteria.getSelectedIndex() >= 0)
        {
            // remove from list
            QueryCriterium qc = (QueryCriterium)_lstCriteria.getDefaultModel().remove(
                _lstCriteria.getSelectedIndex());

            // remove from query
            _q.removeCriterium(qc, false);
        }
    }

    /**
     * @return true if the finish button is to be enabled.
     */
    public boolean canFinish()
    {
        return true;
    }

    /**
     * update controls
     */
    public void updateButtons()
    {
        // flags
        boolean isSel = _lstCriteria.getSelectedIndex() >= 0;

        // enable
        _btnRemove.setEnabled(isSel);
    }

    /**
     * Return the preferred size
     */
    public Dimension getPreferredSize()
    {
        return new Dimension(550, 350);
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
        return HID_TRANSFER_FILTER;
    }
}