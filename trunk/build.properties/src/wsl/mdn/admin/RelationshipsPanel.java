package wsl.mdn.admin;

// imports
import java.util.Vector;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import javax.swing.JLabel;
import pv.jfcx.JPVPassword;
import javax.swing.JCheckBox;
import javax.swing.BorderFactory;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.datasource.Join;
import wsl.fw.datasource.DataObject;
import wsl.fw.gui.GuiConst;
import wsl.fw.gui.PropertiesPanel;
import wsl.fw.gui.WslTextField;
import wsl.fw.gui.WslTextArea;
import wsl.fw.gui.WslComboBox;
import wsl.fw.gui.WslList;
import wsl.fw.gui.WslButton;
import wsl.fw.gui.WslButtonPanel;
import wsl.fw.gui.GuiManager;
import wsl.fw.resource.ResId;
import wsl.fw.help.HelpId;
import wsl.mdn.dataview.DataSourceDobj;
import wsl.mdn.dataview.EntityDobj;
import wsl.mdn.dataview.JoinDobj;
import wsl.mdn.common.MdnAdminConst;

//------------------------------------------------------------------------------
/**
 * Description: Shows the relationships belonging to a DataSource
 * DataSourceDobj is the DataObject belonging to the PropertiesPanel
 */
public class RelationshipsPanel extends WslButtonPanel
    implements ActionListener, ItemListener
{
    //--------------------------------------------------------------------------
    // resources

    public static final ResId LABEL_TITLE  =
        new ResId("RelationshipsPanel.label.Title");
    public static final ResId LABEL_LEFT_ENTITY  =
        new ResId("RelationshipsPanel.label.LeftEntity");
    public static final ResId LABEL_LEFT_FIELD  =
        new ResId("RelationshipsPanel.label.LeftField");
    public static final ResId LABEL_RIGHT_ENTITY  =
        new ResId("RelationshipsPanel.label.RightEntity");
    public static final ResId LABEL_RIGHT_FIELD  =
        new ResId("RelationshipsPanel.label.RightField");
    public static final ResId LABEL_JOIN_TYPE  =
        new ResId("RelationshipsPanel.label.JoinType");
    public static final ResId LABEL_JOIN_LIST  =
        new ResId("RelationshipsPanel.label.JoinList");
    public static final ResId BUTTON_ADD  =
        new ResId("RelationshipsPanel.btn.Add");
    public static final ResId BUTTON_REMOVE  =
        new ResId("RelationshipsPanel.btn.Remove");
    public static final ResId BUTTON_OK  =
        new ResId("RelationshipsPanel.btn.Ok");
    public static final ResId BUTTON_CANCEL  =
        new ResId("RelationshipsPanel.btn.Cancel");
    public static final ResId BUTTON_HELP =
        new ResId("OkPanel.button.Help");

    public final static HelpId HID_RELATIONSHIPS = new HelpId("mdn.admin.RelationshipsPanel");


    //--------------------------------------------------------------------------
    // controls

    private WslComboBox _cmbLeftEntity = new WslComboBox(150);
    private WslComboBox _cmbLeftField = new WslComboBox(150);
    private WslComboBox _cmbRightEntity = new WslComboBox(150);
    private WslComboBox _cmbRightField = new WslComboBox(150);
    private WslComboBox _cmbJoinType = new WslComboBox(150);
    private WslList _lstJoins = new WslList();
    private WslButton _btnAddRel = new WslButton(BUTTON_ADD.getText(), this);
    private WslButton _btnRemoveRel = new WslButton(BUTTON_REMOVE.getText(), this);
    private WslButton _btnOk = new WslButton(BUTTON_OK.getText(), this);
    private WslButton _btnCancel = new WslButton(BUTTON_CANCEL.getText(), this);


    //--------------------------------------------------------------------------
    // attributes

    private DataSourceDobj _ds;


    //--------------------------------------------------------------------------
    // construction

    /**
     * Default constructor.
     */
    public RelationshipsPanel(DataSourceDobj ds)
    {
        // super
        super(WslButtonPanel.VERTICAL);

        // set title
        this.setPanelTitle(LABEL_TITLE.getText() + " " + ds.getName());

        // set attrib
        _ds = ds;
        _ds.imageJoins();

        // init controls
        initRelationshipsPanelControls();

        // build combos
        buildCombos();

        // build join list
        buildJoinList();

        // update buttons
        updateButtons();
    }

    /**
     * Init the panel's controls.
     */
    private void initRelationshipsPanelControls()
    {
        // buttons
        _btnOk.setIcon(Util.resourceIcon(GuiConst.FW_IMAGE_PATH + "save.gif"));
        _btnCancel.setIcon(Util.resourceIcon(GuiConst.FW_IMAGE_PATH + "close.gif"));
        addButton(_btnOk);
        addHelpButton(BUTTON_HELP.getText(), HID_RELATIONSHIPS);
        addButton(_btnCancel);

        addCustomButton(_btnAddRel);
        addCustomButton(_btnRemoveRel);

        // set layout
        getMainPanel().setBorder(BorderFactory.createLoweredBevelBorder());
        getMainPanel().setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // entity labels
        JLabel lbl = new JLabel(LABEL_LEFT_ENTITY.getText());
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets.left = GuiConst.DEFAULT_INSET;
        gbc.insets.top = GuiConst.DEFAULT_INSET;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 0.5;
        gbc.gridx = 0;
        gbc.gridy = 0;
        getMainPanel().add(lbl, gbc);
        lbl = new JLabel(LABEL_RIGHT_ENTITY.getText());
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        getMainPanel().add(lbl, gbc);

        // entity combos
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets.right = 0;
        //gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        _cmbLeftEntity.addItemListener(this);
        getMainPanel().add(_cmbLeftEntity, gbc);
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        _cmbRightEntity.addItemListener(this);
        getMainPanel().add(_cmbRightEntity, gbc);

        // field labels
        lbl = new JLabel(LABEL_LEFT_FIELD.getText());
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets.bottom = 0;
        getMainPanel().add(lbl, gbc);
        lbl = new JLabel(LABEL_RIGHT_FIELD.getText());
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        getMainPanel().add(lbl, gbc);

        // field combos
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.insets.right = 0;
        getMainPanel().add(_cmbLeftField, gbc);
        gbc.gridx = 1;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        getMainPanel().add(_cmbRightField, gbc);

        // join type
        lbl = new JLabel(LABEL_JOIN_TYPE.getText());
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.insets.right = 0;
        gbc.insets.bottom = 0;
        //getMainPanel().add(lbl, gbc);
        gbc.gridy = 5;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        //getMainPanel().add(_cmbJoinType, gbc);

        // join list
        lbl = new JLabel(LABEL_JOIN_LIST.getText());
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.insets.right = 0;
        gbc.insets.bottom = 0;
        getMainPanel().add(lbl, gbc);
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        gbc.weighty = 1;
        _lstJoins.getScrollPane().setBorder(BorderFactory.createLoweredBevelBorder());
        getMainPanel().add(_lstJoins.getScrollPane(), gbc);
    }

    /**
     * Build join list
     */
    private void buildJoinList()
    {
        // validate
        Util.argCheckNull(_ds);

        // get joins from the ds
        Vector joins = _ds.getJoins();

        // build list
        _lstJoins.buildFromVector(joins);
    }

    /**
     * Build combos
     */
    private void buildCombos()
    {
        // validate
        Util.argCheckNull(_ds);

        // build entities
        EntityDobj ent;
        Vector ents = _ds.getEntities();
        for(int i = 0; i < ents.size(); i++)
        {
            // add the entity to the combos
            ent = (EntityDobj)ents.elementAt(i);
            if(ent != null)
            {
                _cmbLeftEntity.addItem(ent);
                _cmbRightEntity.addItem(ent);
            }
        }

        // build join type
        //_cmbJoinType.addItem(Join.JT_INNER);
        //_cmbJoinType.addItem(Join.JT_LEFT_OUTER);
        //_cmbJoinType.addItem(Join.JT_RIGHT_OUTER);
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
            if(ev.getSource() == _cmbLeftEntity)
            {
                // get the selected item
                EntityDobj ent = (EntityDobj)ev.getItem();
                if(ent != null)
                {
                    // get the fields
                    Vector fields = ent.getFields();

                    // build the field combo
                    _cmbLeftField.removeAllItems();
                    _cmbLeftField.buildFromVector(fields);
                }
            }
            else if(ev.getSource() == _cmbRightEntity)
            {
                // get the selected item
                EntityDobj ent = (EntityDobj)ev.getItem();
                if(ent != null)
                {
                    // get the fields
                    Vector fields = ent.getFields();

                    // build the field combo
                    _cmbRightField.removeAllItems();
                    _cmbRightField.buildFromVector(fields);
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
            if(ev.getSource() == _btnCancel)
                onCancel();
            else if(ev.getSource() == _btnOk)
                onOk();
            else if(ev.getSource() == _btnAddRel)
                onAddRelation();
            else if(ev.getSource() == _btnRemoveRel)
                onRemoveRelation();
        }
        catch(Exception e)
        {
            GuiManager.showErrorDialog(this, MdnAdminConst.ERR_UNHANDLED.getText(), e);
            Log.error(wsl.mdn.common.MdnAdminConst.ERR_UNHANDLED.getText(), e);
        }
    }

    /**
     * Add button clicked
     */
    private void onAddRelation()
    {
        // validate
        Util.argCheckNull(_ds);

        // create a JoinDobj and set vals
        JoinDobj j = new JoinDobj();
        j.setDataSourceId(_ds.getId());
        //j.setJoinType(_cmbJoinType.getSelectedItem().toString());
        j.setJoinType(Join.JT_INNER);
        j.setLeftEntity(_cmbLeftEntity.getSelectedItem().toString());
        j.setLeftField(_cmbLeftField.getSelectedItem().toString());
        j.setRightEntity(_cmbRightEntity.getSelectedItem().toString());
        j.setRightField(_cmbRightField.getSelectedItem().toString());

        try
        {
            // add to list
            _lstJoins.getDefaultModel().addElement(j);

            // add to ds
            _ds.addJoin(j);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e.toString());
        }
    }

    /**
     * Remove button clicked
     */
    private void onRemoveRelation()
    {
        // must be selected
        if(_lstJoins.getSelectedIndex() >= 0)
        {
            // remove from list
            JoinDobj j = (JoinDobj)_lstJoins.getDefaultModel().remove(_lstJoins.getSelectedIndex());

            // remove from ds
            _ds.removeJoin(j, true);
        }
    }

    /**
     * Ok button clicked
     */
    public void onOk() throws Exception
    {
        // validate
        Util.argCheckNull(_ds);

        // save the ds
        _ds.save();

        // close
        closePanel();
    }

    /**
     * Cancel button clicked
     */
    public void onCancel() throws Exception
    {
        // validate
        Util.argCheckNull(_ds);

        // set the old joins vector back
        _ds.revertJoinsToImage();

        // close
        closePanel();
    }

    /**
     * update controls
     */
    public void updateButtons()
    {
        // enable
        _btnOk.setEnabled(!isInWizard());
        _btnCancel.setEnabled(!isInWizard());
    }

    /**
     * Return the preferred size
     */
    public Dimension getPreferredSize()
    {
        return new Dimension(550, 350);
    }

    /**
     * @return true if can finish
     */
    public boolean canFinish()
    {
        return true;
    }

}