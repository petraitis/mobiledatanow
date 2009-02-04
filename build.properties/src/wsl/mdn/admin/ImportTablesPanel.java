//==============================================================================
// ImportTablesPanel.java
// Copyright (c) 2001 WAP Solutions Ltd.
//==============================================================================

package wsl.mdn.admin;

// imports
import java.sql.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.util.Vector;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Cursor;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BorderFactory;

import wsl.fw.resource.ResId;
import wsl.fw.help.HelpId;
import wsl.fw.util.Log;
import wsl.fw.util.Util;
import wsl.fw.datasource.Field;
import wsl.fw.datasource.Entity;
import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.DataSourceException;
import wsl.fw.datasource.DataSource;
import wsl.fw.datasource.DataManager;
import wsl.fw.datasource.JdbcDataSourceParam;
import wsl.fw.datasource.EntitySchemaName;
import wsl.fw.gui.WslList;
import wsl.fw.gui.WslButton;
import wsl.fw.gui.WslWizardChild;
import wsl.fw.gui.GuiConst;
import wsl.fw.gui.GuiManager;
import wsl.mdn.dataview.*;


//------------------------------------------------------------------------------
/**
 * ImportTablesPanel.
 */
public class ImportTablesPanel
    extends WslWizardChild
    implements ActionListener, ListSelectionListener
{
    //--------------------------------------------------------------------------
    // resources

    public static final ResId LABEL_SOURCE_LIST  =
        new ResId("ImportTablesPanel.label.SourceList");
    public static final ResId LABEL_TARGET_LIST  =
        new ResId("ImportTablesPanel.label.TargetList");
    public static final ResId BUTTON_ADD  =
        new ResId("ImportTablesPanel.btn.Add");
    public static final ResId BUTTON_REMOVE  =
        new ResId("ImportTablesPanel.btn.Remove");
    public static final ResId WARNING_TABLE_OVERWRITE  =
        new ResId("ImportTablesPanel.warn.TableOverwrite");

    public final static HelpId HID_IMPORT_TABLES = new HelpId("mdn.admin.ImportTablesPanel");

    //--------------------------------------------------------------------------
    // attributes

    private JdbcDataSourceDobj _ds;
    private static final int MAX_COL_SIZE = 1024;

    //--------------------------------------------------------------------------
    // controls

    private WslList _lstSource = new WslList();
    private WslList _lstTarget = new WslList();
    private WslButton _btnAddTables = new WslButton(BUTTON_ADD.getText(), this);
    private WslButton _btnRemoveTables = new WslButton(BUTTON_REMOVE.getText(), this);


    //--------------------------------------------------------------------------
    // construction

    /**
     * DataSourceDobj ctor
     * @param ds the DatSourceDobj to import tables for
     */
    public ImportTablesPanel(DataSourceDobj ds)
    {
        // set attribs
        _ds = (JdbcDataSourceDobj)ds;
        _ds.imageEntities();

        // init controls
        initImportTablesPanelControls();

        try
        {
            // build source table
            buildSourceTable();
        }
        catch (DataSourceException e)
        {
            GuiManager.showErrorDialog(this, "ImportTablesPanel ctor:", e);
        }

        // build target table
        buildTargetTable();

        // update buttons
        updateButtons();
    }

    /**
     * Init controls
     */
    private void initImportTablesPanelControls()
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
        JLabel lbl = new JLabel(LABEL_SOURCE_LIST.getText());
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
        pnlSource.add(lbl, gbc);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.gridy = 1;
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
        pnlButtons.add(_btnAddTables, gbc);
        gbc.gridy = 1;
        pnlButtons.add(_btnRemoveTables, gbc);

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
        _lstTarget.getScrollPane().setBorder(BorderFactory.createLoweredBevelBorder());
        _lstTarget.addListSelectionListener(this);
        pnlTarget.add(_lstTarget.getScrollPane(), gbc);
    }


    //--------------------------------------------------------------------------
    // importing

    private void buildSourceTable() throws DataSourceException
    {
        // validate
        Util.argCheckNull(_ds);

        // make a param describing the data source
        JdbcDataSourceParam dsParam = new JdbcDataSourceParam(_ds.getName(),
            _ds.getJdbcDriver(), _ds.getJdbcUrl(), //_ds.getJdbcCatalog(),
            _ds.getJdbcUser(), _ds.getJdbcPassword());

        // get/create the data source from Datamanager
        DataSource impl = DataManager.getDataSource(dsParam);

        // import definition
        EntitySchemaName esn;
        EntityDobj ed;
        Field f;
        FieldDobj fd;
        Vector tableNames = impl.importTableNames();
        Vector eds = new Vector();

        // iterate table names
        for(int i = 0; tableNames != null && i < tableNames.size(); i++)
        {
            // create the EntityDobj and partially init it with the esn
            esn = (EntitySchemaName) tableNames.elementAt(i);
            ed = new EntityDobj();
            ed.setEsn(esn);
            eds.add(ed);
        }

        // build the list
        _lstSource.buildFromVector(eds);
    }

    /**
     * Build target table
     */
    private void buildTargetTable()
    {
        // validate
        Util.argCheckNull(_ds);

        // get the entities
        Vector ents = _ds.getEntities();

        // build target
        _lstTarget.buildFromVector(ents);
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
            Cursor oldCursor = null;
            try
            {
                oldCursor = getCursor();
                setCursor(new Cursor(Cursor.WAIT_CURSOR));

                // switch on source
                if(ev.getSource() == _btnAddTables)
                    onAddTables();
                else if(ev.getSource() == _btnRemoveTables)
                    onRemoveTables();

                // update buttons
                updateButtons();
            }
            finally
            {
                if (oldCursor != null)
                    setCursor(oldCursor);
            }
        }
        catch(Exception e)
        {
            wsl.fw.gui.GuiManager.showErrorDialog(this,
                wsl.mdn.common.MdnAdminConst.ERR_UNHANDLED.getText(), e);
            Log.error(wsl.mdn.common.MdnAdminConst.ERR_UNHANDLED.getText(), e);
        }
    }

    /**
     * Add tables button clicked
     */
    private void onAddTables()
    {
        // get the source list selection
        Object selection[] = _lstSource.getSelectedValues();

        // iterate the selection
        EntityDobj table;
        for(int i = 0; selection != null && i < selection.length; i++)
        {
            // get the table
            table = (EntityDobj)selection[i];
            if(table != null)
            {
                // have the table, ensure it is fully imported
                if (table.getEsn() != null)
                    finishEntityImport(table);

                // add the table
                if(addTableToTarget(table))
                {
                    // if added to target, remove from source
                    _lstSource.removeItem(table);
                }
            }
        }
    }

    //--------------------------------------------------------------------------
    /**
     *
     */
    private void finishEntityImport(EntityDobj entDobj)
    {
        // get the esn
        EntitySchemaName esn = entDobj.getEsn();

        // null esn so we don't do this again
        entDobj.setEsn(null);

        // make a param describing the data source
        JdbcDataSourceParam dsParam = new JdbcDataSourceParam(_ds.getName(),
            _ds.getJdbcDriver(), _ds.getJdbcUrl(), //_ds.getJdbcCatalog(),
            _ds.getJdbcUser(), _ds.getJdbcPassword());

        // get/create the data source from Datamanager
        DataSource impl = DataManager.getDataSource(dsParam);

        Entity importedEnt = null;

        try
        {
            importedEnt = impl.importEntityDefinition(esn);
        }
        catch (DataSourceException e)
        {
            Log.error("ImportTablesPanel.finishEntityImport: ", e);
        }

        if (importedEnt != null)
        {
            entDobj.setName(importedEnt.getName());
            entDobj.setFlags(importedEnt.getFlags());

            // add the FieldDobjs
            for(int j = 0; importedEnt.getFields() != null && j < importedEnt.getFields().size(); j++)
            {
                // create the FieldDobj
                Field f = (Field) importedEnt.getFields().elementAt(j);
                FieldDobj fd = new FieldDobj(f);
                entDobj.addField(fd);
            }
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Add a table to the target list and datasource
     * @param table the table to add
     * @return boolean true if table added
     */
    private boolean addTableToTarget(EntityDobj table)
    {
        // validate
        Util.argCheckNull(table);

        // does the table already exist
        boolean doAdd = true;
        EntityDobj temp;
        for(int i = 0; i < _lstTarget.getDefaultModel().getSize(); i++)
        {
            // get the table and compare
            temp = (EntityDobj)_lstTarget.getDefaultModel().getElementAt(i);
            if(temp != null && temp.getName().equals(table.getName()))
            {
                // warn
                String msg = WARNING_TABLE_OVERWRITE.getText() + " " + table.getName();
                if(!GuiManager.showConfirmDialog(this.getFrameParent(), msg))
                    doAdd = false;

                // remove the found item from the target
                if(doAdd)
                {
                    _ds.removeEntity(temp, true);
                    _lstTarget.removeItem(temp);
                }
                break;
            }
        }

        // add to the target list and ds
        if(doAdd)
        {
            _lstTarget.addItem(table);
            _ds.addEntity(table);
        }

        // return
        return doAdd;
    }

    /**
     * Remove tables button clicked
     */
    private void onRemoveTables()
    {
        // get the target list selection
        Object selection[] = _lstTarget.getSelectedValues();

        // iterate the selection
        EntityDobj table;
        for(int i = 0; selection != null && i < selection.length; i++)
        {
            // get the table
            table = (EntityDobj)selection[i];
            if(table != null)
            {
                // if new, add to the source list
                if(table.getState() == DataObject.NEW)
                    _lstSource.addItem(table);

                // remove from ds and target list
                _ds.removeEntity(table, true);
                _lstTarget.removeItem(table);
            }
        }
    }


    //--------------------------------------------------------------------------
    // misc

    /**
     * Update controls
     */
    public void updateButtons()
    {
        // flags
        boolean sourceSelected = _lstSource.getSelectedIndex() >= 0;
        boolean targetSelected = _lstTarget.getSelectedIndex() >= 0;

        // enable
        _btnAddTables.setEnabled(sourceSelected);
        _btnRemoveTables.setEnabled(targetSelected);
    }

    /**
     * @return true if can finish
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
        return HID_IMPORT_TABLES;
    }
}

//==============================================================================
// end of file ImportTablesPanel.java
//==============================================================================
