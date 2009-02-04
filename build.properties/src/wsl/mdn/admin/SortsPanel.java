package wsl.mdn.admin;

// imports
import java.util.Vector;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.Sort;
import wsl.fw.gui.GuiConst;
import wsl.fw.gui.WslWizardChild;
import wsl.fw.gui.WslTextField;
import wsl.fw.gui.WslTextArea;
import wsl.fw.gui.WslComboBox;
import wsl.fw.gui.WslList;
import wsl.fw.gui.WslButton;
import wsl.fw.resource.ResId;
import wsl.fw.help.HelpId;
import wsl.mdn.dataview.QueryDobj;
import wsl.mdn.dataview.DataViewField;

//------------------------------------------------------------------------------
/**
 *
 */
public class SortsPanel extends WslWizardChild
    implements ActionListener, ListSelectionListener
{
    //--------------------------------------------------------------------------
    // resources

    public static final ResId LABEL_FIELD  =
        new ResId("SortsPanel.label.Field");
    public static final ResId LABEL_DIRECTION  =
        new ResId("SortsPanel.label.Direction");
    public static final ResId LABEL_DIR_ASC  =
        new ResId("SortsPanel.label.DirAsc");
    public static final ResId LABEL_DIR_DESC  =
        new ResId("SortsPanel.label.DirDesc");
    public static final ResId LABEL_SORTS_LIST  =
        new ResId("SortsPanel.label.SortsList");
    public static final ResId BUTTON_ADD  =
        new ResId("SortsPanel.btn.Add");
    public static final ResId BUTTON_REMOVE  =
        new ResId("SortsPanel.btn.Remove");
    public static final ResId BUTTON_MOVE_UP  =
        new ResId("SortsPanel.btn.MoveUp");
    public static final ResId BUTTON_MOVE_DOWN  =
        new ResId("SortsPanel.btn.MoveDown");

    public final static HelpId HID_SORTS = new HelpId("mdn.admin.SortsPanel");

    //--------------------------------------------------------------------------
    // controls

    private WslComboBox _cmbField = new WslComboBox(150);
    private JRadioButton _rdoAsc = new JRadioButton(LABEL_DIR_ASC.getText());
    private JRadioButton _rdoDesc = new JRadioButton(LABEL_DIR_DESC.getText());
    private WslList _lstSorts = new WslList();
    private WslButton _btnAdd = new WslButton(BUTTON_ADD.getText(), this);
    private WslButton _btnRemove = new WslButton(BUTTON_REMOVE.getText(), this);
    private WslButton _btnMoveUp = new WslButton(BUTTON_MOVE_UP.getText(), this);
    private WslButton _btnMoveDown = new WslButton(BUTTON_MOVE_DOWN.getText(), this);


    //--------------------------------------------------------------------------
    // attributes

    private QueryDobj _query;


    //--------------------------------------------------------------------------
    // construction

    /**
     * Default constructor.
     */
    public SortsPanel(QueryDobj query)
    {
        // set attrib
        _query = query;
        if(!_query.isImaging())
            _query.imageQuery();

        // init controls
        initSortsPanelControls();

        // build combos
        buildCombos();

        // build sorts list
        buildSortsList();

        // update buttons
        updateButtons();
    }

    /**
     * Init the panel's controls.
     */
    private void initSortsPanelControls()
    {
        // set layout
        setBorder(BorderFactory.createLoweredBevelBorder());
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // field
        JLabel lbl = new JLabel(LABEL_FIELD.getText());
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets.left = GuiConst.DEFAULT_INSET;
        gbc.insets.top = GuiConst.DEFAULT_INSET;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.7;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        add(lbl, gbc);
        gbc.gridy = 1;
        add(_cmbField, gbc);

        // direction
        ButtonGroup bg = new ButtonGroup();
        bg.add(_rdoAsc);
        bg.add(_rdoDesc);
        _rdoAsc.setSelected(true);
        gbc.gridwidth = 1;
        gbc.gridy = 2;
        add(_rdoAsc, gbc);
        gbc.gridx = 1;
        add(_rdoDesc, gbc);

        // sorts list
        lbl = new JLabel(LABEL_SORTS_LIST.getText());
        gbc.gridwidth = 4;
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(lbl, gbc);
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        _lstSorts.addListSelectionListener(this);
        _lstSorts.getScrollPane().setBorder(BorderFactory.createLoweredBevelBorder());
        add(_lstSorts.getScrollPane(), gbc);

        // buttons
        gbc.weighty = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        add(_btnAdd, gbc);
        gbc.gridx = 1;
        add(_btnRemove, gbc);
        gbc.gridx = 2;
        add(_btnMoveUp, gbc);
        gbc.gridx = 3;
        add(_btnMoveDown, gbc);
    }

    /**
     * Build sorts list
     */
    private void buildSortsList()
    {
        // validate
        Util.argCheckNull(_query);

        // get criteria from the query
        Vector sorts = _query.getSorts();

        // build list
        _lstSorts.buildFromVector(sorts);
    }

    /**
     * Build combos
     */
    private void buildCombos()
    {
        // validate
        Util.argCheckNull(_query);

        // build fields
        _cmbField.buildFromVector(_query.getDataView().getFields());
        _cmbField.setSelectedIndex(0);
    }


    /**
     * List clicked
     */
    public void valueChanged(ListSelectionEvent ev)
    {
        updateButtons();
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
            else if(ev.getSource() == _btnMoveUp)
                onMoveUp();
            else if(ev.getSource() == _btnMoveDown)
                onMoveDown();
        }
        catch(Exception e)
        {
            wsl.fw.gui.GuiManager.showErrorDialog(this,
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
        Util.argCheckNull(_query);

        // create a Sort and set vals
        Sort s = new Sort(_query.getDataView().getName(),
            ((DataViewField)_cmbField.getSelectedItem()).getName(),
            _rdoAsc.isSelected()? Sort.DIR_ASC: Sort.DIR_DESC);
        try
        {
            // add to list
            _lstSorts.getDefaultModel().addElement(s);

            // add to query
            _query.addSort(s);
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
        if(_lstSorts.getSelectedIndex() >= 0)
        {
            // remove from list
            Sort s = (Sort)_lstSorts.getDefaultModel().remove(
                _lstSorts.getSelectedIndex());

            // remove from query
            _query.removeSort(s, true);
        }
    }

    /**
     * Move Up button clicked
     */
    public void onMoveUp()
    {
        // must be selected
        int selIndex = _lstSorts.getSelectedIndex();
        if(selIndex > 0)
        {
            // remove from list
            Sort s = (Sort)_query.getSorts().remove(selIndex);

            // insert before current index
            int newIndex = selIndex - 1;
            _query.getSorts().insertElementAt(s, newIndex);
            _lstSorts.clear();
            _lstSorts.buildFromVector(_query.getSorts());
            _lstSorts.setSelectedIndex(newIndex);
        }
    }

    /**
     * Move Down button clicked
     */
    public void onMoveDown()
    {
        // must be selected
        int selIndex = _lstSorts.getSelectedIndex();
        if(selIndex >= 0)
        {
            // remove from list
            Sort s = (Sort)_query.getSorts().remove(selIndex);

            // insert before current index
            int newIndex = selIndex + 1;
            _query.getSorts().insertElementAt(s, newIndex);
            _lstSorts.clear();
            _lstSorts.buildFromVector(_query.getSorts());
            _lstSorts.setSelectedIndex(newIndex);
        }
    }

    /**
     * update controls
     */
    public void updateButtons()
    {
        // flags
        boolean isSel = _lstSorts.getSelectedIndex() >= 0;
        int selIndex = _lstSorts.getSelectedIndex();
        boolean isFirst = selIndex == 0;
        boolean isLast = selIndex == (_lstSorts.getDefaultModel().getSize() - 1);

        // enable
        _btnRemove.setEnabled(isSel);
        _btnMoveUp.setEnabled(isSel && !isFirst);
        _btnMoveDown.setEnabled(isSel && !isLast);
    }

    /**
     * Return the preferred size
     */
    public Dimension getPreferredSize()
    {
        return new Dimension(550, 350);
    }

    /**
     * @return true if the finish button is to be enabled.
     */
    public boolean canFinish()
    {
        return true;
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
        return HID_SORTS;
    }
}