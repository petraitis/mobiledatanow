package wsl.mdn.admin;

// imports
import java.sql.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.util.Vector;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import wsl.fw.resource.ResId;
import wsl.fw.help.HelpId;
import wsl.fw.util.Log;
import wsl.fw.util.Util;
import wsl.fw.datasource.Field;
import wsl.fw.datasource.DataSourceException;
import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.DataListenerData;
import wsl.fw.gui.WslList;
import wsl.fw.gui.DataObjectTree;
import wsl.fw.gui.WslButton;
import wsl.fw.gui.WslWizardChild;
import wsl.fw.gui.WslComboBox;
import wsl.fw.gui.GuiConst;
import wsl.fw.gui.GuiManager;
import wsl.fw.gui.DoTreeNode;
import wsl.mdn.dataview.*;

//------------------------------------------------------------------------------
/**
 *
 */
public class MapViewFieldsPanel extends WslWizardChild
    implements ActionListener, ListSelectionListener, ItemListener, TreeSelectionListener
{
    //--------------------------------------------------------------------------
    // resources

    public static final ResId LABEL_SOURCE_LIST  =
        new ResId("MapViewFieldsPanel.label.SourceList");
    public static final ResId LABEL_TARGET_LIST  =
        new ResId("MapViewFieldsPanel.label.TargetList");
    public static final ResId BUTTON_ADD  =
        new ResId("MapViewFieldsPanel.btn.Add");
    public static final ResId BUTTON_REMOVE  =
        new ResId("MapViewFieldsPanel.btn.Remove");
    public static final ResId BUTTON_SET_NAMING  =
        new ResId("MapViewFieldsPanel.btn.SetNaming");
    public static final ResId LABEL_ENTITIES  =
        new ResId("MapViewFieldsPanel.label.Entities");
    public static final ResId ERR_GET_ENTITY =
        new ResId("MapViewFieldsPanel.error.getEntity");
    public static final ResId ERR_BUILDTREE =
        new ResId("MapViewFieldsPanel.error.buildTree");

    public final static HelpId HID_MAP_VIEWFIELDS = new HelpId("mdn.admin.MapViewFieldsPanel");

    //--------------------------------------------------------------------------
    // attributes

    private DataView _dv;
    private transient boolean _isLoading = false;
    private boolean _isBuildingTree = false;

    //--------------------------------------------------------------------------
    // controls

    private WslList _lstSource = new WslList();
    private DataObjectTree _treeTarget = new DataObjectTree();
    private WslButton _btnAddFields = new WslButton(BUTTON_ADD.getText(), this);
    private WslButton _btnRemoveFields = new WslButton(BUTTON_REMOVE.getText(), this);
    private WslButton _btnSetNaming = new WslButton(BUTTON_SET_NAMING.getText(), this);
    private WslComboBox _cmbEntity = new WslComboBox(50);


    //--------------------------------------------------------------------------
    // construction

    /**
     * DataView ctor
     * @param dv the DataView to import fields for
     */
    public MapViewFieldsPanel(DataView dv)
    {
        // set attribs
        _dv = (DataView)dv;
        _dv.imageFields();

        // init controls
        initMapViewFieldsPanelControls();

        // build source combo
        buildEntityCombo();

        // build target tree
        buildTargetTree();

        // update buttons
        updateButtons();
    }

    /**
     * Init controls
     */
    private void initMapViewFieldsPanelControls()
    {
        // set layout
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // source panel
        JPanel pnlSource = new JPanel();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 1;
        add(pnlSource, gbc);

        // buttons panel
        JPanel pnlButtons = new JPanel();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0;
        add(pnlButtons, gbc);

        // terget panel
        JPanel pnlTarget = new JPanel();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        add(pnlTarget, gbc);

        // source panel
        pnlSource.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        JLabel lbl = new JLabel(LABEL_ENTITIES.getText());
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets.left = GuiConst.DEFAULT_INSET;
        gbc.insets.top = GuiConst.DEFAULT_INSET;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        pnlSource.add(lbl, gbc);
        gbc.gridy = 1;
        _cmbEntity.addItemListener(this);
        pnlSource.add(_cmbEntity, gbc);
        lbl = new JLabel(LABEL_SOURCE_LIST.getText());
        gbc.gridy = 2;
        pnlSource.add(lbl, gbc);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.gridy = 3;
        _lstSource.getScrollPane().setBorder(BorderFactory.createLoweredBevelBorder());
        _lstSource.addListSelectionListener(this);
        pnlSource.add(_lstSource.getScrollPane(), gbc);

        // buttons
        pnlButtons.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets.left = GuiConst.DEFAULT_INSET;
        gbc.insets.top = GuiConst.DEFAULT_INSET;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        gbc.gridx = 0;
        gbc.gridy = 0;
        pnlButtons.add(_btnAddFields, gbc);
        gbc.gridy = 1;
        pnlButtons.add(_btnRemoveFields, gbc);
        gbc.gridy = 2;
        pnlButtons.add(_btnSetNaming, gbc);

        // target panel
        pnlTarget.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        lbl = new JLabel(LABEL_TARGET_LIST.getText());
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets.left = GuiConst.DEFAULT_INSET;
        gbc.insets.top = GuiConst.DEFAULT_INSET;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        pnlTarget.add(lbl, gbc);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.gridy = 1;
        _treeTarget.addTreeSelectionListener(this);
        _treeTarget.addListener(new DataListenerData(null, DataViewField.ENT_DVFIELD, null));
        JScrollPane sp = new JScrollPane(_treeTarget);
        sp.setBorder(BorderFactory.createLoweredBevelBorder());
        pnlTarget.add(sp, gbc);
    }

    /**
     * Build combos
     */
    private void buildEntityCombo()
    {
        try
        {
            // get the source ds
            Util.argCheckNull(_dv);
            DataSourceDobj ds = _dv.getSourceDataSource();
            Util.argCheckNull(ds);

            // build entities
            _isLoading = true;
            EntityDobj ent;
            Vector ents = ds.getEntities();
            for(int i = 0; i < ents.size(); i++)
            {
                // add the entity to the combos
                ent = (EntityDobj)ents.elementAt(i);
                if(ent != null)
                    _cmbEntity.addItem(ent);
            }
            _isLoading = false;
        }
        catch(Exception e)
        {
            wsl.fw.gui.GuiManager.showErrorDialog(this, ERR_GET_ENTITY.getText(), e);
        }
    }

    /**
     * Combo selection changed
     */
    public void itemStateChanged(ItemEvent ev)
    {
        // only build on the selected event
        if(ev.getStateChange() == ItemEvent.SELECTED)
            buildSourceList();
    }

    /**
     * Map fields from the DataSource to the DataView
     */
    private void buildSourceList()
    {
        // must have a dv
        Util.argCheckNull(_dv);

        // get the selected entity
        EntityDobj ent = (EntityDobj)_cmbEntity.getSelectedItem();
        if(ent != null)
        {
            // get the fields
            Vector fields = ent.getFields();

            // clear the list
            _lstSource.clear();

            // build the list
            _lstSource.buildFromVector(fields);
        }
    }

    /**
     * build the target tree
     */
    private void buildTargetTree()
    {
        // set is building flag
        _isBuildingTree = true;

        // clear tree
        _treeTarget.clear();

        // build the tree staring at the root
        try
        {
            buildNode(_treeTarget.getRoot());
        }
        catch(Exception e)
        {
            GuiManager.showErrorDialog(this, ERR_BUILDTREE.getText(), e);
        }

        // refresh the tree to ensure proper display
        _treeTarget.refreshModel();

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

        // get the fields from the dv
        Vector fields = _dv.getFields();

        // build tree
        try
        {
            _treeTarget.buildFromVector(fields, parentNode, false);
        }
        catch (DataSourceException e)
        {
        }

        _isBuildingTree = false;
    }


    /**
     * Tree selection changed.
     */
    public void valueChanged(TreeSelectionEvent ev)
    {
        updateButtons();
    }

    //--------------------------------------------------------------------------
    // actions

    /**
     * List selected
     */
    public void valueChanged(ListSelectionEvent ev)
    {
        updateButtons();
    }

    /**
     * Action performed
     */
    public void actionPerformed(ActionEvent ev)
    {
        try
        {
            // switch on source
            if(ev.getSource() == _btnAddFields)
                onAddFields();
            else if(ev.getSource() == _btnRemoveFields)
                onRemoveFields();
            else if(ev.getSource() == _btnSetNaming)
                onSetNaming();

            // update buttons
            updateButtons();
        }
        catch(Exception e)
        {
            wsl.fw.gui.GuiManager.showErrorDialog(this,
                wsl.mdn.common.MdnAdminConst.ERR_UNHANDLED.getText(), e);
            Log.error(wsl.mdn.common.MdnAdminConst.ERR_UNHANDLED.getText(), e);
        }
    }

    /**
     * Add fields button clicked
     */
    private void onAddFields()
    {
        // get the entity
        EntityDobj ent = (EntityDobj)_cmbEntity.getSelectedItem();

        // get the source list selection
        Object selection[] = _lstSource.getSelectedValues();

        // iterate the selection
        DataViewField dvf;
        FieldDobj f;
        for(int i = 0; selection != null && i < selection.length; i++)
        {
            // get the table
            f = (FieldDobj)selection[i];
            if(f != null)
            {
                // create a DataViewField
                dvf = new DataViewField();
                String name = ent.getName() + "." + f.getName();
                dvf.setName(name);
                dvf.setDisplayName(f.getName());
                dvf.setSourceField(f.getName());
                dvf.setSourceEntity(ent.getName());

                // add dvf to the view
                _dv.addField(dvf);

                // add to the target list
                _treeTarget.addNode(_treeTarget.getRoot(), dvf, false);
                _treeTarget.refreshModel();
            }
        }
    }

    /**
     * Remove fields button clicked
     */
    private void onRemoveFields()
    {
        TreePath[] selectedPaths = _treeTarget.getSelectionPaths();
        if (selectedPaths != null)
        {
            int n = selectedPaths.length;
            for (int i = 0; i < n; i++)
            {
                DefaultMutableTreeNode lastNode = (DefaultMutableTreeNode)
                    ((TreePath)selectedPaths[i]).getLastPathComponent();
                if (lastNode != null && !lastNode.isRoot())
                {
                    // get the selected datasource
                    //DataObject dobj = _tree.getSelectedDataObject();
                    DataObject dobj = (DataObject)lastNode.getUserObject();
                    if(dobj != null)
                    {
                        // if it is a DataViewField then set the ds
                        if(dobj instanceof DataViewField)
                        {
                            DataViewField dvf = (DataViewField)dobj;
                            _treeTarget.removeNode((DoTreeNode)lastNode);
                            //_treeTarget.removeSelectionPath(selectedPaths[i]);

                            // remove from dv
                            _dv.removeField(dvf, true);
                        } //if(dobj instanceof DataViewField)
                    } //if(dobj != null)
                } //if (lastNode != null)
            } //for (int i = 0; i < n; i++)
        } //if (selectedPaths != null)
    }

    /**
     * SetNaming button clicked.
     */
    private void onSetNaming() throws DataSourceException
    {
        TreePath[] selectedPaths = _treeTarget.getSelectionPaths();
        if (selectedPaths != null)
        {
            int n = selectedPaths.length;
            for (int i = 0; i < n; i++)
            {
                DefaultMutableTreeNode lastNode = (DefaultMutableTreeNode)
                    ((TreePath)selectedPaths[i]).getLastPathComponent();
                if (lastNode != null && !lastNode.isRoot())
                {
                    // get the selected datasource
                    //DataObject dobj = _tree.getSelectedDataObject();
                    DataObject dobj = (DataObject)lastNode.getUserObject();
                    if(dobj != null)
                    {
                        // if it is a DataViewField then set the ds
                        if(dobj instanceof DataViewField)
                        {
                            DataViewField dvf = (DataViewField)dobj;
                            if (dvf.hasFlag(Field.FF_NAMING))
                                dvf.setFlags(dvf.getFlags() & ~Field.FF_NAMING);
                            else
                                dvf.setFlags(dvf.getFlags() | Field.FF_NAMING);
                            _treeTarget.refreshModel();
                        } //if(dobj instanceof DataViewField)
                    } //if(dobj != null)
                } //if (lastNode != null)
            } //for (int i = 0; i < n; i++)
        } //if (selectedPaths != null)
    }

    //--------------------------------------------------------------------------
    // misc

    protected boolean isTargetListEmpty()
    {
        return (_treeTarget.getRoot().getChildCount() == 0);
    }

    /**
     * Update controls
     */
    public void updateButtons()
    {
        // flags
        boolean sourceSelected = _lstSource.getSelectedIndex() >= 0;
        //boolean targetSelected = _lstTarget.getSelectedIndex() >= 0;
        TreePath[] selectedPaths = _treeTarget.getSelectionPaths();
        boolean targetSelected = selectedPaths != null && selectedPaths.length > 0;

        // enable
        _btnAddFields.setEnabled(sourceSelected);
        _btnRemoveFields.setEnabled(targetSelected);
        _btnSetNaming.setEnabled(targetSelected);
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
        return HID_MAP_VIEWFIELDS;
    }
}