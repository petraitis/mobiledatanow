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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import wsl.fw.resource.ResId;
import wsl.fw.help.HelpId;
import wsl.fw.util.Log;
import wsl.fw.util.Util;
import wsl.fw.datasource.Field;
import wsl.fw.gui.WslList;
import wsl.fw.gui.WslButton;
import wsl.fw.gui.WslWizardChild;
import wsl.fw.gui.GuiConst;
import wsl.mdn.dataview.*;

//------------------------------------------------------------------------------
/**
 *
 */
public class TransferEntitiesPanel extends WslWizardChild
    implements ActionListener, ListSelectionListener
{
    //--------------------------------------------------------------------------
    // resources

    public static final ResId LABEL_SOURCE_LIST  =
        new ResId("TransferEntitiesPanel.label.SourceList");
    public static final ResId LABEL_TARGET_LIST  =
        new ResId("TransferEntitiesPanel.label.TargetList");
    public static final ResId BUTTON_ADD  =
        new ResId("TransferEntitiesPanel.btn.Add");
    public static final ResId BUTTON_REMOVE  =
        new ResId("TransferEntitiesPanel.btn.Remove");

    public final static HelpId HID_TRANSFER_ENTITIES = new HelpId("mdn.admin.TransferEntitiesPanel");

    //--------------------------------------------------------------------------
    // attributes

    private DataTransfer _dt;


    //--------------------------------------------------------------------------
    // controls

    private WslList _lstSource = new WslList();
    private WslList _lstTarget = new WslList();
    private WslButton _btnAdd = new WslButton(BUTTON_ADD.getText(), this);
    private WslButton _btnRemove = new WslButton(BUTTON_REMOVE.getText(), this);


    //--------------------------------------------------------------------------
    // construction

    /**
     * DataTransfer ctor
     * @param dt the DataTransfer
     */
    public TransferEntitiesPanel(DataTransfer dt)
    {
        // set attribs
        _dt = (DataTransfer)dt;
        _dt.imageDataTransfer();

        // init controls
        initTransferEntitiesPanelControls();

        // build tables
        buildTargetTable();
        buildSourceTable();

        // update buttons
        updateButtons();
    }

    /**
     * Init controls
     */
    private void initTransferEntitiesPanelControls()
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
        pnlButtons.add(_btnAdd, gbc);
        gbc.gridy = 1;
        pnlButtons.add(_btnRemove, gbc);

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
    // Entities

    /**
     * Add datasource entities
     */
    private void buildSourceTable()
    {
        // validate
        Util.argCheckNull(_dt);

        // get entities
        try
        {
            DataSourceDobj ds = _dt.getSourceDataSource();
            Util.argCheckNull(ds);
            Vector entities = ds.getEntities();

            // iterate entities
            EntityDobj ent;
            for(int i = 0; entities != null && i < entities.size(); i++)
            {
                // get the entity
                ent = (EntityDobj)entities.elementAt(i);

                // add to source table if not in target table
                if(ent != null)
                {
                    // search target list
                    boolean inTarget = false;
                    TransferEntity te;
                    for(int j = 0; !inTarget && j < _lstTarget.getDefaultModel().getSize(); j++)
                    {
                        te = (TransferEntity)_lstTarget.getDefaultModel().getElementAt(j);
                        if(te != null && te.getSourceEntityName().equalsIgnoreCase(ent.getName()))
                            inTarget = true;
                    }

                    // if !inTarget add to source
                    if(!inTarget)
                        _lstSource.addItem(ent);
                }
            }
        }
        catch(Exception e)
        {
            throw new RuntimeException("TransferEntitiesPanel.buildSourceTable: " + e.toString());
        }
    }

    /**
     * Add transfer entities
     */
    private void buildTargetTable()
    {
        // validate
        Util.argCheckNull(_dt);

        // get entities
        Vector entities = _dt.getTransferEntities();

        // build table
        _lstTarget.buildFromVector(entities);
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
            if(ev.getSource() == _btnAdd)
                onAdd();
            else if(ev.getSource() == _btnRemove)
                onRemove();

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
     * Add tables button clicked
     */
    private void onAdd()
    {
        // get the source list selection
        Object selection[] = _lstSource.getSelectedValues();

        // iterate the selection
        EntityDobj table;
        TransferEntity te;
        for(int i = 0; selection != null && i < selection.length; i++)
        {
            // get the table
            table = (EntityDobj)selection[i];
            if(table != null)
            {
                // create new te
                te = new TransferEntity();
                te.setSourceEntityName(table.getName());

                // add to the target list and the datasource
                _lstTarget.addItem(te);
                _dt.addTransferEntity(te);
            }
        }

        // remove the selection from the source list
        for(int i = 0; selection != null && i < selection.length; i++)
        {
            // get the table
            table = (EntityDobj)selection[i];
            if(table != null)
                _lstSource.removeItem(table);
        }
    }

    /**
     * Remove tables button clicked
     */
    private void onRemove() throws Exception
    {
        // get the target list selection
        Object selection[] = _lstTarget.getSelectedValues();

        // iterate the selection
        TransferEntity te;
        EntityDobj table;
        for(int i = 0; selection != null && i < selection.length; i++)
        {
            // get the te
            te = (TransferEntity)selection[i];
            if(te != null)
            {
                // get the associate entitydobj
                table = _dt.getSourceDataSource().getEntity(te.getSourceEntityName());
                if(table != null)
                {
                    // add to the source list
                    _lstSource.addItem(table);

                    // remove from ds
                    _dt.removeTransferEntity(te, true);
                }
            }
        }

        // remove the selection from the target list
        for(int i = 0; selection != null && i < selection.length; i++)
        {
            // get the table
            te = (TransferEntity)selection[i];
            if(te != null)
                _lstTarget.removeItem(te);
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
        _btnAdd.setEnabled(sourceSelected);
        _btnRemove.setEnabled(targetSelected);
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
        return HID_TRANSFER_ENTITIES;
    }
}