//==============================================================================
// SelectPanel.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.gui;

// imports
import java.util.Vector;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.DefaultListModel;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.DataManager;
import wsl.fw.datasource.DataSource;
import wsl.fw.datasource.Query;
import wsl.fw.datasource.QueryCriterium;
import wsl.fw.datasource.Entity;
import wsl.fw.datasource.Field;
import wsl.fw.datasource.RecordSet;
import wsl.fw.datasource.Sort;
import wsl.fw.util.Log;
import wsl.fw.util.Util;
import wsl.fw.help.HelpId;

import wsl.fw.resource.ResId;

//------------------------------------------------------------------------------
/**
 * Panel to allow the user to search for and select DataObjects from the
 * Database. User may type criteria into the name field and a like search will
 * be performed on the specified class, entity, or supplied data vector.
 */
public class SelectPanel extends WslButtonPanel
    implements ActionListener, TreeSelectionListener
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/gui/SelectPanel.java $ ";

    // resources
    public static final ResId BUTTON_SEARCH  = new ResId("SelectPanel.button.Search");
    public static final ResId BUTTON_SELECT  = new ResId("SelectPanel.button.Select");
    public static final ResId BUTTON_CANCEL  = new ResId("SelectPanel.button.Cancel");
    public static final ResId LABEL_RESULTS  = new ResId("SelectPanel.label.Results");
    public static final ResId LABEL_NAME  = new ResId("SelectPanel.label.Name");
    public static final ResId BUTTON_HELP  = new ResId("SelectPanel.button.Help");
    public static final ResId ERR_ERROR  = new ResId("SelectPanel.error.Error");
    public static final ResId ERR_DATABASE  = new ResId("SelectPanel.error.Database");

    // help ids
    public  final static HelpId HID_SELECT = new HelpId("fw.gui.SelectPanel");

    // attributes
    private String         _searchEntity = "";
    private Class          _searchClass  = null;
    private Vector         _dataObjects  = null;
    private DataObject     _selObject    = null;
    private DataObjectTree _treeResults  = null;
    private JTextField     _txtCriteria  = new JTextField();
    private WslButton      _btnSearch    = new WslButton(BUTTON_SEARCH.getText(), this);
    private WslButton      _btnSelect    = new WslButton(BUTTON_SELECT.getText(), this);
    private WslButton      _btnCancel    = new WslButton(BUTTON_CANCEL.getText(), this);

    //--------------------------------------------------------------------------
    /**
     * Constructor taking a search entity name
     * @param searchEntity the name of the entities to search for
     * @param autoSearch if true will automatically select all data objects on opening
     */
    public SelectPanel(String searchEntity, boolean doAutoSearch)
    {
        // super
        super(WslButtonPanel.HORIZONTAL);

        // set attribs
        _searchEntity = searchEntity;

        // init controls
        initSelectPanelControls();

        // autosearch
        if(doAutoSearch)
            onSearch();
        updateButtons();
    }

    //--------------------------------------------------------------------------
    /**
     * Constructor taking a search entity name
     * @param searchEntity the name of the entities to search for
     * @param autoSearch if true will automatically select all data objects on opening
     */
    public SelectPanel(Class searchClass, boolean doAutoSearch)
    {
        // super
        super(WslButtonPanel.HORIZONTAL);

        // set attribs
        _searchClass = searchClass;

        // init controls
        initSelectPanelControls();

        // autosearch
        if(doAutoSearch)
            onSearch();
        updateButtons();
    }

    //--------------------------------------------------------------------------
    /**
     * Constructor taking a vector of data objects.
     * @param searchEntity the name of the entities to search for
     * @param autoSearch if true will automatically select all data objects on opening
     */
    public SelectPanel(Vector dobjs)
    {
        // super
        super(WslButtonPanel.HORIZONTAL);

        Util.argCheckNull(dobjs);

        // set attribs
        _dataObjects = dobjs;

        // init controls
        initSelectPanelControls();

        // autosearch
        onSearch();
        updateButtons();
    }

    //--------------------------------------------------------------------------
    /**
     * Create and init controls
     */
    private void initSelectPanelControls()
    {
        // results panel
        getMainPanel().setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        JPanel pnlResults = new JPanel();
        pnlResults.setLayout(new GridBagLayout());
        pnlResults.setBorder(BorderFactory.createLoweredBevelBorder());
        getMainPanel().add(pnlResults, gbc);

        // results controls
        JLabel lbl = new JLabel(LABEL_RESULTS.getText());
        gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets.top = GuiConst.DEFAULT_INSET;
        gbc.insets.left = GuiConst.DEFAULT_INSET;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        pnlResults.add(lbl, gbc);

        // tree
        _treeResults = new DataObjectTree();
        JScrollPane sp = new JScrollPane(_treeResults);
        initTreeListeners();
        sp.setBorder(BorderFactory.createLoweredBevelBorder());
        gbc.weighty = 1;
        gbc.gridy = 1;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        pnlResults.add(sp, gbc);

        // criteria panel
        JPanel pnlCriteria = new JPanel();
        pnlCriteria.setLayout(new GridBagLayout());
        pnlCriteria.setBorder(BorderFactory.createLoweredBevelBorder());
        gbc = new GridBagConstraints();
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        getMainPanel().add(pnlCriteria, gbc);

        // criteria controls
        lbl = new JLabel(LABEL_NAME.getText());
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets.top = GuiConst.DEFAULT_INSET;
        gbc.insets.left = GuiConst.DEFAULT_INSET;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        pnlCriteria.add(lbl, gbc);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        gbc.gridx = 1;
        gbc.weightx = 1;
        pnlCriteria.add(_txtCriteria, gbc);

        // add buttons to buttons panel
        addButton(_btnSearch);
        addButton(_btnSelect);
        addHelpButton(BUTTON_HELP.getText(), HID_SELECT);
        addButton(_btnCancel);
    }

    //--------------------------------------------------------------------------
    /**
     * Initialise tree listeners
     */
    private void initTreeListeners()
    {
        // selection listener
        _treeResults.addTreeSelectionListener(this);

        // mouse adapter for double clicks
        MouseListener ml = new MouseAdapter()
        {
            public void mousePressed(MouseEvent e)
            {
                int selRow = _treeResults.getRowForLocation(e.getX(), e.getY());
                if(e.getClickCount() == 2 && selRow >= 0)
                    onSelect();
            }
         };
        _treeResults.addMouseListener(ml);
     }

    //--------------------------------------------------------------------------
    /**
     * Action performed handler.
     */
    public void actionPerformed(ActionEvent ev)
    {
        try
        {
            // switch on source
            if(ev.getSource().equals(_btnSearch))
                onSearch();
            else if(ev.getSource().equals(_btnSelect))
                onSelect();
            else if(ev.getSource().equals(_btnCancel))
                onCancel();
        }
        catch(Exception e)
        {
            GuiManager.showErrorDialog(this, ERR_ERROR.getText(), e);
            Log.error(ERR_ERROR.getText(), e);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Search button clicked
     * @return void
     * @exception
     * @roseuid 3990C63703CB
     */
    private void onSearch()
    {
        if (_dataObjects != null)
        {
            // if we have a data object vector use that instead of doing a query
            String criteria = _txtCriteria.getText();
            buildResults(criteria);
        }
        else
        {
            try
            {
                // if class is set use it
                String entityName = _searchEntity;
                if(_searchClass != null)
                {
                    DataObject dobj = (DataObject)_searchClass.newInstance();
                    entityName = dobj.getEntityName();
                }

                // get the appropriate DataSource and Entity
                DataSource ds = DataManager.getDataSource(entityName);
                Entity ent = DataManager.getEntity(entityName);
                if(ds != null && ent != null)
                {
                    // get the naming field
                    Vector v = ent.getNamingFields();
                    if(v != null && v.size() > 0)
                    {
                        Field f = (Field)v.elementAt(0);
                        if(f != null)
                        {
                            // build query
                            Query q = new Query();
                            q.addQueryEntity(entityName);
                            String criteria = _txtCriteria.getText();
                            if(criteria.length() > 0)
                                q.addQueryCriterium(new QueryCriterium(entityName, f.getName(), QueryCriterium.OP_LIKE, criteria));
                            q.addSort(new Sort(entityName, f.getName(), Sort.DIR_ASC));

                            // execute query
                            RecordSet rs = ds.select(q);

                            // build results
                            buildResults(rs);
                        }
                    }
                }
            }
            catch(Exception e)
            {
                GuiManager.showErrorDialog(this, ERR_DATABASE.getText(), e);
                Log.error(ERR_DATABASE.getText(), e);
            }
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Build the results list from a the data object vector, filtering by
     * criteria.
     * @param criteria, the filter (search) criteria
     */
    private void buildResults(String criteria)
    {
        // clear list
        _treeResults.clear();

        // build the list
        DoTreeNode root = _treeResults.getRoot();

        for (int i = 0; i < _dataObjects.size(); i++)
        {
            // get the DataObject and add to list if it matches criteria
            DataObject dobj = (DataObject) _dataObjects.get(i);
            if (dobj != null)
                if (dobj.toString().startsWith(criteria))
                    _treeResults.addNode(root, dobj, false);
        }

        // refresh the tree
        _treeResults.refreshModel();
    }

    //--------------------------------------------------------------------------
    /**
     * Build the results list from a RecordSet
     * @param rs RecordSet to build the results list from
     */
    private void buildResults(RecordSet rs) throws Exception
    {
        // validate
        Util.argCheckNull(rs);

        // clear list
        _treeResults.clear();

        // build the list
        DoTreeNode root = _treeResults.getRoot();
        DataObject dobj;
        while(rs.next())
        {
            // get the DataObject and add to list
            dobj = rs.getCurrentObject();
            if(dobj != null)
                _treeResults.addNode(root, dobj, false);
        }

        // refresh the tree
        _treeResults.refreshModel();
    }

    //--------------------------------------------------------------------------
    /**
     * Select button clicked
     * @return void
     * @exception
     * @roseuid 3990C7720342
     */
    private void onSelect()
    {
        // set the selected object
        _selObject = (DataObject)_treeResults.getSelectedDataObject();

        // if we have a selection, close the panel
        if(_selObject != null)
            closePanel();
    }

    //--------------------------------------------------------------------------
    /**
     * Cancel button clicked
     */
    private void onCancel()
    {
        // clear the selected object
        _selObject = null;
        closePanel();
    }

    //--------------------------------------------------------------------------
    /**
     * @return the DataObject selected in the SelectPanel
     */
    public DataObject getSelectedDataObject()
    {
        return _selObject;
    }

    //--------------------------------------------------------------------------
    /**
     * @return the preferred dimensions of SelectPanel
     */
    public Dimension getPreferredSize()
    {
        return new Dimension(500, 400);
    }

    //--------------------------------------------------------------------------
    /**
     * Update controls based on state
     */
    public void updateButtons()
    {
        // set flags
        boolean isSelected = _treeResults.getSelectedDataObject() != null;

        // enable disable controls
        _btnSelect.setEnabled(isSelected);
    }

    //--------------------------------------------------------------------------
    /**
     * List has been clicked
     */
    public void valueChanged(TreeSelectionEvent e)
    {
        updateButtons();
    }
}

//==============================================================================
// end of file SelectPanel.java
//==============================================================================
